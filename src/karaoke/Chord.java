package karaoke;

import java.io.PrintWriter;

import karaoke.sound.SequencePlayer;

/**
 *  Puts two musics into the same time frame
 */
public class Chord implements Music{
    
    // Abstraction Function
    // AF(m1, m2, chordSyllable, header): 
    //   represents a chord in which m1 is played together with m2, with a syllable chordSyllable linked to the chord.
    //   It is also associated with an ABC header. 
    // Rep Invariant
    //   true;
    // Safety from rep exposure: 
    //   all fields are private and immutable
    //   m1,m2, header are final 
    //   chordSyllable is never provided to a client
    // Thread Safety argument
    //   This class is threadsafe because it's immutable
    //   m1, m2, are final 
    //   chordSyllable is never exposed to the client
    //   

    private final Music m1;
    private final Music m2;
    private final Header header;
    private Syllable chordSyllable; 
    
    /**
     * Creates a chord, a union of two or note notes.
     * @param m1
     * @param m2
     */
    public Chord(Music m1, Music m2, Header header) {
        this(m1, m2, header, new Syllable("unknown","", 0,0));
    }
    
    
    public Chord(Music m1, Music m2, Header header, Syllable chordSyllable) {
        this.m1 = m1;
        this.m2 = m2;
        this.header = header;
        this.chordSyllable = chordSyllable; 

    }
    
    @Override
    public void play(SequencePlayer player, double atBeat, Player mainPlayer) {
        m1.play(player,atBeat, mainPlayer);
        m2.play(player, atBeat, mainPlayer);
        if (!chordSyllable.isSkipped()) {
            player.addEvent(atBeat, (x)-> mainPlayer.streamToAll(chordSyllable.getLine(),chordSyllable.getVoice())); 
        }
    }

    @Override
    public double duration() {
        return m1.duration();
    }

    @Override
    public int beatsPerMinute() {
        return (int) header.tempo();
    }

    @Override
    public int ticksPerBeat() {
        return 360;
    }
    
    @Override
    public Header header() {
        return this.header;
    }
    
    @Override
    public int numNotesFree() { 
        if (chordSyllable.getLyricLine().isEmpty()) {
            return 1;
        }
        return 0; 
    }
    
    @Override
    public Music addSyllableToNote(Syllable inputSyllable) {
        if (this.numNotesFree() == 1) {
            this.chordSyllable = inputSyllable; 
        }
        
       return new Chord(this.m1, this.m2, this.header, this.chordSyllable); 
    }
    
    @Override
    public String toString() {
        return "Chord(" + this.m1.toString() + ", " + this.m2.toString() + ")";
    }
    
    @Override
    public boolean equals(Object that) {
        if (that instanceof Chord){
            Chord other = (Chord) that;
            return m1.equals(other.m1) && m2.equals(other.m2);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return m1.hashCode() + m2.hashCode();
    }
}
