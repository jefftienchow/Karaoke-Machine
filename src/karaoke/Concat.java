package karaoke;

import java.io.PrintWriter;

import karaoke.sound.SequencePlayer;

/**
 * Concatenates two musics together
 */
public class Concat implements Music{
    
    // Abstraction Function
    //  AF(m1, m2, header): represents music that concatenates m1 and m2 together such that m1 
    //  plays first and then m2 plays second. Concat is associated with an abc Header 
    // Rep Invariant
    //  true
    // Safety from rep exposure
    //  all fields are private and final and immutable 
    // Thread Safety argument
    //  This class is threadsafe because it's immutable:
    //  m1, m2 are private and final

    private final Music m1;
    private final Music m2;
    private final Header header;
    
    /**
     * Creates a new Concat object.
     * 
     * @param m1
     * @param m2
     */
    public Concat(Music m1, Music m2, Header header) {
        this.m1 = m1;
        this.m2 = m2;
        this.header = header;

    }
    
    @Override
    public void play(SequencePlayer player, double atBeat,Player mainPlayer) {
        m1.play(player, atBeat,mainPlayer);
        m2.play(player, atBeat + m1.duration(),mainPlayer);
    }

    @Override
    public double duration() {
        return m1.duration()+m2.duration();
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
        return m1.numNotesFree() + m2.numNotesFree(); 
    }
    
    @Override
    public Music addSyllableToNote(Syllable inputSyllable) {
  
        if (m1.numNotesFree() > 0) { 
            Music m1Modified = m1.addSyllableToNote(inputSyllable);
            return new Concat(m1Modified, m2, header);
        } else if (m2.numNotesFree() > 0) { 
            Music m2Modified = m2.addSyllableToNote(inputSyllable);
            return new Concat(m1, m2Modified, header); 
        } else {
            return this;
        } 
    }
    
    @Override
    public String toString() {
        return "Concat(" + m1.toString() + ", " + m2.toString() + ")";
    }
    
    @Override
    public boolean equals(Object that) {
        if (that instanceof Concat) {
            Concat other = (Concat) that;
            return m1.equals(other.m1) && m2.equals(other.m2);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return m1.hashCode() + m2.hashCode();
    }
}
