package karaoke.sound;

import static org.junit.Assert.assertEquals;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import org.junit.Test;

import edu.mit.eecs.parserlib.ParseTree;
import edu.mit.eecs.parserlib.UnableToParseException;
import karaoke.Header;
import karaoke.Music;
import karaoke.Player;
import karaoke.parser.KaraokeParser;

/**
 * @category no_didit
 * Tests verifiable by ear, using the Parser and the Music Grammar.
 */
public class PlayTest {
    
    /*
     * Testing strategy for Playing Music:
     * Partition the input as follows:
     * header with/without L,M,Q,V 
     * L,M,Q,V in different order
     * Meter having C|, C, rational number
     * voice/composer name has special characters/spaces
     * has comments/doesn't have comments
     * music has 0, 1, >1 measures
     * music has 0, 1, >1 repeats
     * music has/doesn't have accidentals
     * music has 1, 2, >2 voices
     * music has/doesn't have lyrics
     * number of notes in chord: 1,2,3+
     * note lengths in chord: same, all different, only first note different
     * number of sharps in chord: 0, 1, >1
     * number of flats in chord: 0, 1, >1
     * Testing strategy for Header
     * Index: 1, other
     * Title length: 0,1,2+
     * Title special chars: none, "." , "," , ":", "'", """, "!", "?"
     * Meter: c, c|, 2/2, 2/4, 3/4, 4/4, 3/8, 5/8, 6/8, other
     * Key: A,B,C,D,E,F,G
     * Key accidental: none, #, b
     * Key: major, minor
     * Tempo: <60, 60-100, 100-140, 140-200, 200+
     * Lyric length: 0,1,2+
     * Special characters: none, "." , "," , ":", "'", """, "!", "?"
     * Meaningful Symbols: none, "-", "_", "*", "~", "\-", "|", combination of the above
     * Number of consecutive hyphens: 0,1,2,3+
     * length only has /
     * length has / followed by a number
     * length has both numerator and denominator
     * length only has numerator
     * 
     */
    
    /*
     * Represents a basic header in C major
     */
    final String headerBasic = "X:1\n" + 
            "T:Piece No.1\n" + 
            "M:4/4\n" + 
            "L:1/4\n" + 
            "Q:1/4=140\n" + 
            "K:C\n";
    
    /*
     * Represents a basic header in C major, but key is alterable by the tester
     */
    final String headerBasicNoKey = "X:1\n" + 
            "T:Piece No.1\n" + 
            "M:4/4\n" + 
            "L:1/4\n" + 
            "Q:1/4=140\n";
    
    final String headerThreeVoices = "X:1\n" + 
            "T:Three Voices\n" + 
            "M:4/4\n" + 
            "L:1/4\n" + 
            "Q:1/4=140\n" + 
            "V:1\n" +
            "V:2\n" +
            "V:3\n" +
            "K:C\n";
    
    public static void main(String[] args) throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
     
    }
    
    /**
     * The backbone of all the music tests; takes an input string, parses it, and then plays it on the computer
     * 
     * @param input The input abc file to play music from
     * @throws UnableToParseException
     * @throws MidiUnavailableException
     * @throws InvalidMidiDataException
     */
    private static void playTest(String input) throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {

            Music m = KaraokeParser.parse(input);
            Header header = m.header();
            
            Instrument piano = Instrument.PIANO;

            final int beatsPerMinute = (int) (header.tempoLength()/header.noteLength()*header.tempo()); // a beat is a quarter note, so this is 120 quarter notes per minute
            final int ticksPerBeat = 360; // allows up to 1/64-beat notes to be played with fidelity
            SequencePlayer player = new MidiSequencePlayer(beatsPerMinute, ticksPerBeat);
                        
            m.play(player, 0, new Player());
            System.out.println("PlayTest Initiated");
            Object lock = new Object();
            player.addEvent(m.duration(), (Double beat) -> {
                synchronized (lock) {
                    lock.notify();
                }
            });
            
            player.play();
            
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    return;
                }
            }
    }
    
    @Test
    public void playOneNote() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasic + "C\n";
        playTest(input);  
    }
    
    @Test
    public void playScaleCompact() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasic + "CDEF|GABc\n";
        playTest(input);  
    }
    
    @Test
    public void playScale() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasic + "C D E F G A B c\n";
        playTest(input);  
    }
    
    @Test
    public void playScaleWhitespace() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasic + "C   D     E F |G    A   B   c\n";
        playTest(input);  
    }
    
    @Test
    public void playScaleDoubleTempo() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = "X:1\n" + 
                "T:Piece No.1\n" + 
                "M:4/4\n" + 
                "L:1/4\n" + 
                "Q:1/4=280\n" + 
                "K:C\n" + 
                "C D E F G A B c\n";
        playTest(input);  
    }
    
    @Test
    public void playScaleDifferentQ1() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = "X:1\n" + 
                "T:Piece No.1\n" + 
                "M:4/4\n" + 
                "L:1/4\n" + 
                "Q:1/2=140\n" + 
                "K:C\n" + 
                "C D E F G A B c\n";
        playTest(input);  
    }
    
    @Test
    public void playScaleDifferentQ2() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = "X:1\n" + 
                "T:Piece No.1\n" + 
                "M:4/4\n" + 
                "L:1/4\n" + 
                "Q:1/8=140\n" + 
                "K:C\n" + 
                "C D E F G A B c\n";
        playTest(input);  
    }
    
    @Test
    public void playScaleD() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasicNoKey + "K:D\n" + "DEFGABcd\n";
        playTest(input);  
    }
    
    @Test
    public void playScaleE() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasicNoKey + "K:E\n" + "EFGABcde\n";
        playTest(input);  
    }
    
    @Test
    public void playScaleF() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasicNoKey + "K:F\n" + "FGABcdef\n";
        playTest(input);  
    }
    
    @Test
    public void playScaleG() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasicNoKey + "K:G\n" + "GABcdefg\n";
        playTest(input);  
    }
    
    @Test
    public void playScaleA() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasicNoKey + "K:A\n" + "ABcdefga\n";
        playTest(input);  
    }
    
    @Test
    public void playScaleB() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasicNoKey + "K:B\n" + "Bcdefgab\n";
        playTest(input);  
    }
    
    @Test
    public void playScaleCm() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasicNoKey + "K:Cm\n" + "CDEFGABc\n";
        playTest(input);  
    }
    
    @Test
    public void playScaleDm() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasicNoKey + "K:Dm\n" + "DEFGABcd\n";
        playTest(input);  
    }
    
    @Test
    public void playScaleEm() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasicNoKey + "K:Em\n" + "EFGABcde\n";
        playTest(input);  
    }
    
    //TODO sounds ugly
    @Test
    public void playScaleFm() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasicNoKey + "K:Fm\n" + "FGABcdef\n";
        playTest(input);  
    }
    
    @Test
    public void playScatleGm() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasicNoKey + "K:Gm\n" + "GABcdefg\n";
        playTest(input);  
    }
    
    @Test
    public void playScaleAm() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasicNoKey + "K:Am\n" + "ABcdefga\n";
        playTest(input);  
    }
    
    @Test
    public void playScaleBm() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasicNoKey + "K:Bm\n" + "Bcdefgab\n";
        playTest(input);  
    }
    
    @Test
    public void playChromaticScale() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasic + "C ^C D ^D E F _G =G _A =A _B =B c\n";
        playTest(input);  
    }
    
    //TODO sounds ugly
    @Test
    public void playDoubleAccidentals() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasic + "__C _C =C ^C ^^C\n";
        playTest(input);  
    }
    
    @Test
    public void playAccidentalReset() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasic + "CCC ^CCC | CCC _CCC | CCC\n";
        playTest(input);  
    }
    
    @Test
    public void playAllNoteLengths() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasic + "A1/4 A/4 A/ A A2 A3 A4| A,1/4 A,/4 A,/ A, A,2 A,3 A,4|]\n";
        playTest(input);  
    }
    
    @Test
    public void notesAndRests() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasic + "A z A z/2 A z1/ A z/ A z2 A\n";
        playTest(input);  
    }
    
    @Test
    public void beautifulChord() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasic + "[CEG]\n";
        playTest(input);  
    }
    
    @Test
    public void beautifulChords() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasic + "[CEG][CFA][CEG][B,DG][CEG]\n";
        playTest(input);  
    }
    
    @Test
    public void playChordVaryingLength() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasic + "[C3E2G1]\n";
        playTest(input);  
    }
    
    @Test
    public void playChordConcatVaryingLength() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasic + "[C3E2G1][c1e2g3][c'3e'2g'1]\n";
        playTest(input);  
    }
    
    @Test
    public void playDupletBasic() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasic + "(2GA\n";
        playTest(input);  
    }
    
    @Test
    public void playTripletBasic() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasic + "(3GAB\n";
        playTest(input);  
    }
    
    @Test
    public void playTripletWithNormal() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasic + "(3GAB GAB (3GAB GAB\n";
        playTest(input);  
    }
    
    @Test
    public void playQuadrupletBasic() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerBasic + "(4GABC\n";
        playTest(input);  
    }
    
    @Test
    public void playOneVoice() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerThreeVoices + 
                "V:1\n"+
                "C D E F G A B c\n";
        playTest(input);  
    }
    
    @Test
    public void playOneVoiceMultiLine() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerThreeVoices + 
                "V:1\n"+
                "C D E F G A B c\n"+
                "V:1\n"+
                "C D E F G A B c\n";
        playTest(input);  
    }
    
    @Test
    public void playTwoVoices() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerThreeVoices + 
                "V:1\n"+
                "C D E F G A B c\n"+
                "V:2\n"+
                "c B A G F E D C\n";
        playTest(input);  
    }
    
    @Test
    public void playTwoVoicesMultiLine() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerThreeVoices + 
                "V:1\n"+
                "C D E F G A B c\n"+
                "V:2\n"+
                "c B A G F E D C\n"+
                "V:1\n"+
                "C D E F G A B c\n"+
                "V:2\n"+
                "c B A G F E D C\n";
        playTest(input);  
    }
    
    @Test
    public void playTwoVoicesMultiLineDifferentLengths() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerThreeVoices + 
                "V:1\n"+
                "C D E F G A B c d e f g a\n"+
                "V:2\n"+
                "c B A G F E D C\n"+
                "V:1\n"+
                "C D E F G A B c d e f g a\n"+
                "V:2\n"+
                "c B A G F E D C\n";
        playTest(input);  
    }
    
    @Test
    public void playThreeVoices() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerThreeVoices + 
                "V:1\n"+
                "C D E F G A B c z1/2\n"+
                "V:2\n"+
                "c B A G F E D C z1/2\n"+
                "V:3\n"+
                "z1/2 c d e f g a b c'\n";
        playTest(input);  
    }
    
    @Test
    public void playThreeVoicesMultiLine() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerThreeVoices + 
                "V:1\n"+
                "C D E F G A B c z1/2\n"+
                "V:2\n"+
                "c B A G F E D C z1/2\n"+
                "V:3\n"+
                "z1/2 c d e f g a b c'\n"+
                "V:1\n"+
                "C D E F G A B c z1/2\n"+
                "V:2\n"+
                "c B A G F E D C z1/2\n"+
                "V:3\n"+
                "z1/2 c d e f g a b c'\n";
        playTest(input);  
    }
    
    @Test
    public void playRepeatBasic() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerThreeVoices + "|: C D E :|\n";
        playTest(input);  
    }
    
    @Test
    public void playRepeatConcatted() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerThreeVoices + "A, B, |: C D E :| F G |: A B c :| \n";
        playTest(input);  
    }
    
    @Test
    public void playSecondEndingBasic() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = headerThreeVoices + "|: C D E F |[1 G A B c | G A B B :|[2 F E D C |\n";
        playTest(input);  
    }
    
    /***************** SONG EXAMPLES ******************/
    
    @Test
    public void playPiece1() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = "X:1\n" + 
                "T:Piece No.1\n" + 
                "M:4/4\n" + 
                "L:1/4\n" + 
                "Q:1/4=140\n" + 
                "K:C\n" + 
                "C C C3/4 D/4 E | E3/4 D/4 E3/4 F/4 G2 | (3C'/2C'/2C'/2 (3G/2G/2G/2 (3E/2E/2E/2 (3C/2C/2C/2 G3/4 F/4 E3/4 D/4 C2 |]\n";
        playTest(input);  
    }
    
    @Test
    public void playPiece2() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = "X:1 \n" + 
                "T:Piece No.2\n" + 
                "M:4/4\n" + 
                "L:1/4\n" + 
                "Q:1/4=200\n" + 
                "K:C\n" + 
                "[^F/2 e/2] [F/2 e/2] z/2 [F/2 e/2] z/2 [F/2 c/2] [F e] | [G b g] z G z | c3/2 G/2 z E | E/2 a b _b/2 a | (3Geg a' f/2 g/2 | z/2 e c/2 d/2 b3/4 ||\n";
        playTest(input);  
    }
    
    @Test
    public void playPiece3() throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = "X:1\n" + 
                "T:Piece No. 3\n" + 
                "M:3/4\n" + 
                "L:1/8\n" + 
                "Q:1/8=100\n" + 
                "K:C\n" + 
                "z2 D | G2 B/2 G/2 | B2 A | G2 E | D2 D | G2 B/2 G/2 | B2 A | d3\n" + 
                "w: * A-ma-zing_ grace! How sweet the sound That saved a_ wretch like me.\n";
        playTest(input);  
    }
}
