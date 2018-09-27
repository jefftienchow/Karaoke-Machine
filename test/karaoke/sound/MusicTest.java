package karaoke.sound;

import java.util.HashSet;
import java.util.function.Consumer;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.VoiceStatus;

import org.junit.Test;
import karaoke.Note;
import karaoke.OverlayVoice;
import karaoke.Player;
import karaoke.Rest;
import karaoke.Chord;
import karaoke.Concat;
import karaoke.Header;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for Music concrete variants
 */
public class MusicTest {
    /*
     * Testing strategy for Note
     * octave is: <-1, -1, 0, 1, 2, >2
     * number of sharps: 0, 1, >1
     * number of flats: 0, 1, >1
     * length only has /
     * length has / followed by a number
     * length has both numerator and denominator
     * length only has numerator
     * has a flat/sharp in the key signature
     * 
     * =========================================================
     * 
     * Testing strategy for Chord
     * number of notes in chord: 1,2,3+
     * note lengths: same, all different, only first note different
     * number of sharps: 0, 1, >1
     * number of flats: 0, 1, >1
     * 
     * =========================================================
     * 
     * Testing strategy for Concat
     * Music types: same, different
     * Music elements: Chord, another concat, lyric, overlayvoice, rest, note, combination of the above
     * 
     * =========================================================
     * 
     * Testing strategy for Header
     * Index: 1, other
     * Title length: 0,1,2+
     * Title special chars: none, "." , "," , ":", "'", """, "!", "?"
     * Meter: c, c|, 2/2, 2/4, 3/4, 4/4, 3/8, 5/8, 6/8, other
     * Key: A,B,C,D,E,F,G
     * Key accidental: none, #, b
     * Key: major, minor
     * Tempo: <60, 60-100, 100-140, 140-200, 200+
     * 
     * =========================================================
     * 
     * Testing strategy for Lyric
     * Lyric length: 0,1,2+
     * Special characters: none, "." , "," , ":", "'", """, "!", "?"
     * Meaningful Symbols: none, "-", "_", "*", "~", "\-", "|", combination of the above
     * Number of consecutive hyphens: 0,1,2,3+
     * 
     * =========================================================
     * 
     * Testing strategy for OverlayVoice
     * Number of voices: 0,1,2,3+
     * Voice length, 0, 1beat, 2beats+
     * 
     * =========================================================
     * 
     * Testing strategy for Rest
     * length only has /
     * length has / followed by a number
     * length has both numerator and denominator
     * length only has numerator
     */
    private final Player mainPlayer = new Player();
    
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test 
    public void testNoteNoKey() {
        Header header = new Header(0,"", 0,0, "Am",0,0,new HashSet<String>(),"",0,"");
        Note note = new Note("_A", header, "3/4");
        assertEquals(0.75, note.duration(), 0.01);
    }
    
    @Test 
    public void testNoteKeySignature() {
        Header header = new Header(0,"", 0,0, "G",0,0,new HashSet<String>(),"",0,"");
        Note note = new Note("F", header, "3/4");
        assertEquals(0.75, note.duration(), 0.01);
    }   
    
    @Test 
    public void testNoteDoubleSharp() {
        Header header = new Header(0,"", 0,0, "G",0,0,new HashSet<String>(),"",0,"");
        Note note = new Note("^^F", header, "3/4");
        assertEquals(0.75, note.duration(), 0.01);
    }   
    
    @Test 
    public void testNoteDoubleFlat() {
        Header header = new Header(0,"", 0,0, "G",0,0,new HashSet<String>(),"",0,"");
        Note note = new Note("__F", header, "3/4");
        assertEquals(0.75, note.duration(), 0.01);
    }  
    
    @Test 
    public void testNoteOverrideSignature() {
        Header header = new Header(0,"", 0,0, "G",0,0,new HashSet<String>(),"",0,"");
        Note note = new Note("=F", header, "3/4");
        assertEquals(0.75, note.duration(), 0.01);
    }   
    
    @Test 
    public void testNoteSecondOctave() {
        Header header = new Header(0,"", 0,0, "G",0,0,new HashSet<String>(),"",0,"");
        Note note = new Note("_f", header, "/");
        assertEquals(0.5, note.duration(), 0.01);
    }  
    
    @Test 
    public void testNoteDurationWholeNumber() {
        Header header = new Header(0,"", 0,0, "Am",0,0,new HashSet<String>(),"",0,"");
        Note note = new Note("_A", header, "3");
        assertEquals(3.0, note.duration(), 0.01);
    }
    
    @Test 
    public void testNoDenominator() {
        Header header = new Header(0,"", 0,0, "Am",0,0,new HashSet<String>(),"",0,"");
        Note note = new Note("_A", header, "3/");
        assertEquals(1.5, note.duration(), 0.01);
    }
     
    public void testHighOctave() {
        Header header = new Header(0,"", 0,0, "A",0,0,new HashSet<String>(),"",0,"");
        Note note = new Note("f'", header, "/2");
        assertEquals(0.5, note.duration(), 0.01);
    }   
    
    @Test 
    public void testLowOctave() {
        Header header = new Header(0,"", 0,0, "A",0,0,new HashSet<String>(),"",0,"");
        Note note = new Note("G,,,", header, "3/4");
        assertEquals(0.75, note.duration(), 0.01);
    }
    
    @Test
    public void testRestBasic() {
        Header header = new Header(0,"", 0,0, "C",0,0,new HashSet<String>(),"",0,"");
        Rest rest = new Rest(header, "1");
        assertEquals(rest.duration(), 1.0, 0.01);
    }
    
    @Test
    public void testRestNoDenominator() {
        Header header = new Header(0,"", 0,0, "C",0,0,new HashSet<String>(),"",0,"");
        Rest rest = new Rest(header, "1/");
        assertEquals(rest.duration(), 0.5, 0.01);
    }
    
    @Test
    public void testRestNoNumerator() {
        Header header = new Header(0,"", 0,0, "C",0,0,new HashSet<String>(),"",0,"");
        Rest rest = new Rest(header, "/3");
        assertEquals(rest.duration(), 1.0/3.0, 0.01);
    }
    
    @Test
    public void testRestFraction() {
        Header header = new Header(0,"", 0,0, "C",0,0,new HashSet<String>(),"",0,"");
        Rest rest = new Rest(header, "5/4");
        assertEquals(rest.duration(), 1.25, 0.01);
    }
    
    public void testCMajor() {
        Note note;
        Header header = new Header(0,"", 0,0, "C",0,0,new HashSet<String>(),"",0,"");
        note = new Note("A", header, "1");
        assertEquals("Note: A duration: 1.0 transpose: 0 octave: 0", note.toString());
        note = new Note("B", header, "1");
        assertEquals("Note: B duration: 1.0 transpose: 0 octave: 0", note.toString());
        note = new Note("C", header, "1");
        assertEquals("Note: C duration: 1.0 transpose: 0 octave: 0", note.toString());
        note = new Note("D", header, "1");
        assertEquals("Note: D duration: 1.0 transpose: 0 octave: 0", note.toString());
        note = new Note("E", header, "1");
        assertEquals("Note: E duration: 1.0 transpose: 0 octave: 0", note.toString());
        note = new Note("F", header, "1");
        assertEquals("Note: F duration: 1.0 transpose: 0 octave: 0", note.toString());
        note = new Note("G", header, "1");
        assertEquals("Note: G duration: 1.0 transpose: 0 octave: 0", note.toString());   
    }
    
    public void testEMajor() {
        Note note;
        Header header = new Header(0,"", 0,0, "E",0,0,new HashSet<String>(),"",0,"");
        note = new Note("A", header, "1");
        assertEquals("Note: A duration: 1.0 transpose: 0 octave: 0", note.toString());
        note = new Note("B", header, "1");
        assertEquals("Note: B duration: 1.0 transpose: 0 octave: 0", note.toString());
        note = new Note("C", header, "1");
        assertEquals("Note: C duration: 1.0 transpose: 1 octave: 0", note.toString());
        note = new Note("D", header, "1");
        assertEquals("Note: D duration: 1.0 transpose: 1 octave: 0", note.toString());
        note = new Note("E", header, "1");
        assertEquals("Note: E duration: 1.0 transpose: 0 octave: 0", note.toString());
        note = new Note("F", header, "1");
        assertEquals("Note: F duration: 1.0 transpose: 1 octave: 0", note.toString());
        note = new Note("G", header, "1");
        assertEquals("Note: G duration: 1.0 transpose: 1 octave: 0", note.toString());   
    }
    
    public void testBFlatMinor() {
        Note note;
        Header header = new Header(0,"", 0,0, "Bbm",0,0,new HashSet<String>(),"",0,"");
        note = new Note("A", header, "1");
        assertEquals("Note: A duration: 1.0 transpose: -1 octave: 0", note.toString());
        note = new Note("B", header, "1");
        assertEquals("Note: B duration: 1.0 transpose: -1 octave: 0", note.toString());
        note = new Note("C", header, "1");
        assertEquals("Note: C duration: 1.0 transpose: 0 octave: 0", note.toString());
        note = new Note("D", header, "1");
        assertEquals("Note: D duration: 1.0 transpose: -1 octave: 0", note.toString());
        note = new Note("E", header, "1");
        assertEquals("Note: E duration: 1.0 transpose: -1 octave: 0", note.toString());
        note = new Note("F", header, "1");
        assertEquals("Note: F duration: 1.0 transpose: 0 octave: 0", note.toString());
        note = new Note("G", header, "1");
        assertEquals("Note: G duration: 1.0 transpose: -1 octave: 0", note.toString());   
    }
    
    @Test
    public void testHeaderInfo() {
        Header header = new Header(0,"", 0,0, "C",0,0,new HashSet<String>(),"",0,"");
        Note note = new Note("C", header, "1");
    }
    
    public void testPlayNoteBasic() throws MidiUnavailableException, InvalidMidiDataException {
        Header header = new Header(0,"", 0,0, "C",0,0,new HashSet<String>(),"",0,"");
        MidiSequencePlayer player = new MidiSequencePlayer();
        Note note = new Note("C", header, "1");
        note.play(player, 0,mainPlayer);
    }
    
    public void testPlayConcat() throws MidiUnavailableException, InvalidMidiDataException {
        Header header = new Header(0,"", 0,0, "C",0,0,new HashSet<String>(),"",0,"");
        MidiSequencePlayer player = new MidiSequencePlayer();
        Note note1 = new Note("C", header, "1");
        Note note2 = new Note("D", header, "1");
        Concat concat = new Concat(note1, note2,header);
        assertEquals(concat.duration(), 2, 0.01);
        concat.play(player, 0, mainPlayer);
    }
    
    public void testPlayConcatVariousLengths() throws MidiUnavailableException, InvalidMidiDataException {
        Header header = new Header(0,"", 0,0, "C",0,0,new HashSet<String>(),"",0,"");
        MidiSequencePlayer player = new MidiSequencePlayer();
        Note note1 = new Note("C", header, "1");
        Note note2 = new Note("D", header, "/");
        Note note3 = new Note("E", header, "2");
        Note note4 = new Note("F", header, "3/");
        Concat concat = new Concat(note1, new Concat(note2, new Concat(note3, note4,header),header),header);
        assertEquals(concat.duration(), 5, 0.01);
        concat.play(player, 0, mainPlayer);
    }
    
    public void testPlayChord() throws MidiUnavailableException, InvalidMidiDataException {
        Header header = new Header(0,"", 0,0, "C",0,0,new HashSet<String>(),"",0,"");
        MidiSequencePlayer player = new MidiSequencePlayer();
        Note note1 = new Note("C", header, "1");
        Note note2 = new Note("D", header, "1");
        Chord chord = new Chord(note1, note2,header);
        assertEquals(chord.duration(), 1, 0.01);
        chord.play(player, 0, mainPlayer);
    }
    
    public void testPlayChordDifferentLengths() throws MidiUnavailableException, InvalidMidiDataException {
        Header header = new Header(0,"", 0,0, "C",0,0,new HashSet<String>(),"",0,"");
        MidiSequencePlayer player = new MidiSequencePlayer();
        Note note1 = new Note("C", header, "1");
        Note note2 = new Note("D", header, "2");
        Chord chord = new Chord(note1, note2,header);
        assertEquals(chord.duration(), 1, 0.01);
        chord.play(player, 0, mainPlayer);
    }
    
    @Test
    public void testOverlayVoiceBasic() {
        Header header = new Header(0,"", 0,0, "C",0,0,new HashSet<String>(),"",0,"");
        Note note1 = new Note("C", header, "1");
        Note note2 = new Note("D", header, "1");
        OverlayVoice voices = new OverlayVoice(note1, note2,header);
        assertEquals(voices.duration(), 1, 0.01);
    }
    
    @Test
    public void testConcatChordsBasic() {
        Header header = new Header(0,"", 0,0, "C",0,0,new HashSet<String>(),"",0,"");
        Note note1 = new Note("C", header, "1");
        Note note2 = new Note("D", header, "1");
        Chord chord1 = new Chord(note1, note2, header);
        Note note3 = new Note("E", header, "1");
        Note note4 = new Note("F", header, "1");
        Chord chord2 = new Chord(note3, note4,header);
        Concat concat = new Concat(chord1,chord2,header);
        assertEquals(concat.duration(), 2, 0.01); 
    }
    
    @Test
    public void testConcatChordwithNote() {
        Header header = new Header(0,"", 0,0, "C",0,0,new HashSet<String>(),"",0,"");
        Note note1 = new Note("C", header, "1");
        Note note2 = new Note("D", header, "1");
        Chord chord1 = new Chord(note1, note2,header);
        Note note3 = new Note("E", header, "1");
        Concat concat = new Concat(chord1,note3,header);
        assertEquals(concat.duration(), 2, 0.01); 
    }
    
    //TEST EQUALITY
    @Test
    public void testEqualityNoteYes() {
        Header header = new Header(0,"", 0,0, "C",0,0,new HashSet<String>(),"",0,"");
        Note note1 = new Note("C", header, "1");
        Note note2 = new Note("C", header, "1");
        assertEquals(note1, note2);
    }
    
    @Test
    public void testEqualityNoteYes2() {
        Header header = new Header(0,"", 0,0, "C",0,0,new HashSet<String>(),"",0,"");
        Note note1 = new Note("C", header, "1/2");
        Note note2 = new Note("C", header, "/");
        assertEquals(note1, note2);
    }
    
    @Test
    public void testEqualityNoteNo() {
        Header header = new Header(0,"", 0,0, "C",0,0,new HashSet<String>(),"",0,"");
        Note note1 = new Note("C", header, "1");
        Note note2 = new Note("D", header, "1");
        assertFalse(note1.equals(note2));
    }
    
    public void testEqualityNoteNo2() {
        Header header = new Header(0,"", 0,0, "C",0,0,new HashSet<String>(),"",0,"");
        Note note1 = new Note("C", header, "1");
        Note note2 = new Note("C", header, "2");
        assertFalse(note1.equals(note2));
    }
    
}
