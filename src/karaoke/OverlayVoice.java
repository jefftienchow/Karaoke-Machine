package karaoke;

import java.io.PrintWriter;

import karaoke.sound.SequencePlayer;

/**
 * 
 */
public class OverlayVoice implements Music{
    
    // Abstraction Function
    //  AF(m1, m2, header); represents music that overlays m1 and m2, such that it plays both music objects 
    //  simultaneously. OverlyVoice is also associated with ABC header. 
    // Rep Invariant: 
    //  true
    // Safety from rep exposure
    //  all fields are private, final, and immutable
    // Thread Safety argument
    //  This class is threadsafe because it's immutable
    //  m1, m2, are private and final 

    private final Music m1;
    private final Music m2;
    private final Header header;
    
    /**
     * Constructor for voice
     * @param m1 first voice
     * @param m2 second voice
     */
    public OverlayVoice(Music m1, Music m2, Header header) {
        this.m1 = m1;
        this.m2 = m2;
        this.header = header;

    }
    
    @Override
    public void play(SequencePlayer player, double atBeat,Player mainPlayer) {
        m1.play(player, atBeat,mainPlayer);
        m2.play(player, atBeat,mainPlayer);
    }

    @Override
    public double duration() {
        return Double.max(m1.duration(), m2.duration());
    }

    @Override
    public int beatsPerMinute() {
        System.out.println("what's wrong: ");
        System.out.println("what's wrong: "+ header.tempo());
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
        return 0; 
    }
    
    @Override
    public Music addSyllableToNote(Syllable inputSyllable) {
        return this; 
    }
    
    @Override
    public String toString() {
        return "Voice: " + m1.toString() + "\nVoice:" + m2.toString();
    }
    
    @Override
    public boolean equals(Object that) {
        if (that instanceof OverlayVoice) {
            OverlayVoice other = (OverlayVoice) that;
            return m1.equals(other.m1) && m2.equals(other.m2);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return m1.hashCode() + m2.hashCode();
    }
}
