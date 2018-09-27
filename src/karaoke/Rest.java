package karaoke;

import java.io.PrintWriter;

import karaoke.sound.SequencePlayer;

/**
 * A rest in which no pitch is played
 */
public class Rest implements Music{
    
    // Abstraction Function
    // AF(restLength, header): 
    //   represents a rest element with a length of restLength. It is also associated with an ABC header  
    // Rep Invariant: 
    //   restLength >= 0
    // Safety from rep exposure
    //   all fields are private, final and immutable
    // Thread Safety argument
    //   This class is threadsafe because it's immutable: 
    //   header and restLength are private & final 
    //   header and restLength are immutable
    
    private final Header header;
    private final double restLength;
    
    /**
     * Constructor for Rest
     * @param header header for the music
     * @param length length of rest
     */
    public Rest(Header header, String length) {
        this.header = header;
        if (length.contains("/")) {
            if (length.charAt(0)=='/') {
                if (length.length()>1) {
                    restLength = 1.0/Double.parseDouble(length.substring(1, length.length()));
                }
                else {
                    restLength = 1.0/2.0;
                }
            }
            else {
                double numerator = Double.parseDouble(length.substring(0,length.indexOf("/")));
                double denominator = 0.0;
                if(length.charAt(length.length()-1)=='/') {
                    denominator = 2.0;
                }
                else {
                    denominator = Double.parseDouble(length.substring(length.indexOf("/")+1,length.length()));
                }    
                restLength = numerator/denominator;
            }
        }
        else {
            restLength = Double.parseDouble(length);
        }
    }
    
    public Rest(Header header) {
        this(header, "1");
    }

    @Override
    public void play(SequencePlayer player, double atBeat, Player mainPlayer) {
        //don't play anything
    }
    
    @Override
    public double duration() {
        return restLength;
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
    public Music addSyllableToNote(Syllable inputsSyllable) {
        return this; 
    }
    
    @Override
    public int numNotesFree() {
        return 0; 
    }
    
    @Override
    public String toString() {
        return "Rest(" + restLength + ")";
    }
    
    @Override
    public boolean equals(Object that) {
        if (that instanceof Rest) {
            Rest other = (Rest) that;
            return restLength == other.restLength;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return (int) Math.round(restLength);
    }
}
