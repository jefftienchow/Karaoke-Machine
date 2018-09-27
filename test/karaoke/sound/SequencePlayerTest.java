package karaoke.sound;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import org.junit.Test;
/**
 * Tests for sequenceplayer
 * @category no_didit
 */
public class SequencePlayerTest {
    
    @Test
    public void testPlayPiece1() throws MidiUnavailableException, InvalidMidiDataException{ 
        Instrument piano = Instrument.PIANO;

        // create a new player
        final int beatsPerMinute = 140; // a beat is a quarter note, so this is 120 quarter notes per minute
        final int ticksPerBeat = 96; // allows up to 1/64-beat notes to be played with fidelity
        SequencePlayer player = new MidiSequencePlayer(beatsPerMinute, ticksPerBeat);
         
        // addNote(instr, pitch, startBeat, numBeats) schedules a note with pitch value 'pitch'
        // played by 'instr' starting at 'startBeat' to be played for 'numBeats' beats.
        
        double startBeat = 0;
        double numBeats = 1;
        
        /** MEASURE 1 **/
        
        //first two notes
        for (int i = 0; i < 2; i++) {
            player.addNote(piano, new Pitch('C'), startBeat, numBeats); 
            startBeat = startBeat + 1; 
        }
        
        // dotted eighth note
        player.addNote(piano, new Pitch('C'), startBeat, numBeats*.75); 
        startBeat = startBeat + .75; 
        
        // sixteenth note
        player.addNote(piano, new Pitch('D'), startBeat, numBeats*.25); 
        startBeat = startBeat + .25; 
        
        player.addNote(piano, new Pitch('E'), startBeat, numBeats); 
        startBeat = startBeat + 1; 
        
        /** MEASURE 2*/
        // dotted eighth note
        player.addNote(piano, new Pitch('E'), startBeat, numBeats*.75); 
        startBeat = startBeat + .75; 
        
        // sixteenth note
        player.addNote(piano, new Pitch('D'), startBeat, numBeats*.25); 
        startBeat = startBeat + .25; 
        
        // dotted eighth note
        player.addNote(piano, new Pitch('E'), startBeat, numBeats*.75); 
        startBeat = startBeat + .75; 
        
        // sixteenth note
        player.addNote(piano, new Pitch('F'), startBeat, numBeats*.25); 
        startBeat = startBeat + .25; 
        
        // half note
        player.addNote(piano, new Pitch('G'), startBeat, numBeats*2); 
        startBeat = startBeat + 2; 
        
        /** MEASURE 3*/
        
        //first three triplets
        for (int i = 0; i < 3; i++) {
            player.addNote(piano, new Pitch('C').transpose(Pitch.OCTAVE), startBeat, numBeats/3); 
            startBeat = startBeat + 1.0/3; 
        }   
        
        //first three triplets
        for (int i = 0; i < 3; i++) {
            player.addNote(piano, new Pitch('G'), startBeat, numBeats/3); 
            startBeat = startBeat + 1.0/3; 
        }
        
        //first three triplets
        for (int i = 0; i < 3; i++) {
            player.addNote(piano, new Pitch('E'), startBeat, numBeats/3); 
            startBeat = startBeat + 1.0/3; 
        }
        
        //first three triplets
        for (int i = 0; i < 3; i++) {
            player.addNote(piano, new Pitch('C'), startBeat, numBeats/3); 
            startBeat = startBeat + 1.0/3; 
        }
        
        /** MEASURE 4*/
        
        // dotted eighth note
        player.addNote(piano, new Pitch('G'), startBeat, numBeats*.75); 
        startBeat = startBeat + .75; 
        
        // sixteenth note
        player.addNote(piano, new Pitch('F'), startBeat, numBeats*.25); 
        startBeat = startBeat + .25; 
        
        // dotted eighth note
        player.addNote(piano, new Pitch('E'), startBeat, numBeats*.75); 
        startBeat = startBeat + .75; 
        
        // sixteenth note
        player.addNote(piano, new Pitch('D'), startBeat, numBeats*.25); 
        startBeat = startBeat + .25; 
        
        // half note
        player.addNote(piano, new Pitch('C'), startBeat, numBeats*2); 
        startBeat = startBeat + 2; 
        
        // add a listener at the end of the piece to tell main thread when it's done
        Object lock = new Object();
        player.addEvent(startBeat, (Double beat) -> {
            synchronized (lock) {
                lock.notify();
            }
        });
        
        // print the configured player
        System.out.println(player);

        // play!
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
    public void testPlayPiece2() throws MidiUnavailableException, InvalidMidiDataException{ 
        Instrument piano = Instrument.PIANO;

        // create a new player
        final int beatsPerMinute = 200; // a beat is a quarter note, so this is 120 quarter notes per minute
        final int ticksPerBeat = 64; // allows up to 1/64-beat notes to be played with fidelity
        SequencePlayer player = new MidiSequencePlayer(beatsPerMinute, ticksPerBeat);
         
        // addNote(instr, pitch, startBeat, numBeats) schedules a note with pitch value 'pitch'
        // played by 'instr' starting at 'startBeat' to be played for 'numBeats' beats.
        
        double startBeat = 0;
        double numBeats = 1;
        
        /** MEASURE 1 **/
        
        //first two chords
        for (int i = 0; i < 2; i++) {
            player.addNote(piano, new Pitch('F').transpose(1), startBeat, numBeats/2); 
            player.addNote(piano, new Pitch('E').transpose(Pitch.OCTAVE), startBeat, numBeats/2); 
            startBeat = startBeat + .5; 
        }
        
        // eight rest
        startBeat = startBeat + .5;
        
        //chord
        player.addNote(piano, new Pitch('F').transpose(1), startBeat, numBeats/2);
        player.addNote(piano, new Pitch('E').transpose(Pitch.OCTAVE), startBeat, numBeats/2); 
        startBeat = startBeat + .5; 
        
        // eight rest
        startBeat = startBeat + .5; 
        
        //chord
        player.addNote(piano, new Pitch('F').transpose(1), startBeat, numBeats/2);
        player.addNote(piano, new Pitch('C').transpose(Pitch.OCTAVE), startBeat, numBeats/2);
        startBeat = startBeat + .5; 
        
        //chord
        player.addNote(piano, new Pitch('F').transpose(1), startBeat, numBeats); 
        player.addNote(piano, new Pitch('E').transpose(Pitch.OCTAVE), startBeat, numBeats); 
        startBeat = startBeat + 1; 
        
        

        /** MEASURE 2 **/
        // chord
        player.addNote(piano, new Pitch('G'), startBeat, numBeats); 
        player.addNote(piano, new Pitch('B').transpose(Pitch.OCTAVE), startBeat, numBeats); 
        player.addNote(piano, new Pitch('G').transpose(Pitch.OCTAVE), startBeat, numBeats); 
        startBeat = startBeat + 1; 
        
        // quarter rest
        startBeat = startBeat + 1;
        
        // quarter note
        player.addNote(piano, new Pitch('G'), startBeat, numBeats); 
        startBeat = startBeat + 1; 
        
        // quarter rest
        startBeat = startBeat + 1; 
        
        /** MEASURE 3 **/ 
        // dotted quarter note
        player.addNote(piano, new Pitch('C').transpose(Pitch.OCTAVE), startBeat, numBeats*3/2); 
        startBeat = startBeat + 1.5; 
        
        // eight note
        player.addNote(piano, new Pitch('G'), startBeat, numBeats/2); 
        startBeat = startBeat + .5;
        
        // quarter rest
        startBeat = startBeat + 1;
        
        // quarter note
        player.addNote(piano, new Pitch('E'), startBeat, numBeats); 
        startBeat = startBeat + 1;
        
        /** MEASURE 4 **/ 
        // eight note
        player.addNote(piano, new Pitch('E'), startBeat, numBeats/2); 
        startBeat = startBeat + .5; 
        
        // quarter note
        player.addNote(piano, new Pitch('A'), startBeat, numBeats); 
        startBeat = startBeat + 1; 
        
        // quarter note 
        player.addNote(piano, new Pitch('B'), startBeat, numBeats); 
        startBeat = startBeat + 1;
        
        // eight note
        player.addNote(piano, new Pitch('B').transpose(-1), startBeat, numBeats/2); 
        startBeat = startBeat + .5; 
        
        // quarter note
        player.addNote(piano, new Pitch('A'), startBeat, numBeats); 
        startBeat = startBeat + 1; 
        
        /** MEASURE 5 **/ 
        // quarter note
        player.addNote(piano, new Pitch('G'), startBeat, numBeats*2/3); 
        startBeat = startBeat + 2.0/3; 
        
        // quarter note
        player.addNote(piano, new Pitch('E').transpose(Pitch.OCTAVE), startBeat, numBeats*2/3); 
        startBeat = startBeat + 2.0/3; 
        
        // quarter note
        player.addNote(piano, new Pitch('G').transpose(Pitch.OCTAVE), startBeat, numBeats*2/3); 
        startBeat = startBeat + 2.0/3; 
        
        // quarter note
        player.addNote(piano, new Pitch('A').transpose(Pitch.OCTAVE), startBeat, numBeats); 
        startBeat = startBeat + 1; 
        
        // eight note
        player.addNote(piano, new Pitch('F').transpose(Pitch.OCTAVE), startBeat, numBeats/2); 
        startBeat = startBeat + .5; 
        
        player.addNote(piano, new Pitch('G').transpose(Pitch.OCTAVE), startBeat, numBeats/2); 
        startBeat = startBeat + .5; 
        
        /** MEASURE 6 **/ 
        // eight rest 
        startBeat = startBeat + .5; 
        
        // quarter note
        player.addNote(piano, new Pitch('E').transpose(Pitch.OCTAVE), startBeat, numBeats); 
        startBeat = startBeat + 1; 
        
        // eight note
        player.addNote(piano, new Pitch('C').transpose(Pitch.OCTAVE), startBeat, numBeats/2); 
        startBeat = startBeat + .5; 
        
        // eight note
        player.addNote(piano, new Pitch('D').transpose(Pitch.OCTAVE), startBeat, numBeats/2); 
        startBeat = startBeat + .5; 
        
        // dotted eight note
        player.addNote(piano, new Pitch('B'), startBeat, numBeats*3/4); 
        startBeat = startBeat + .75; 
        
        
        // add a listener at the end of the piece to tell main thread when it's done
        Object lock = new Object();
        player.addEvent(startBeat, (Double beat) -> {
            synchronized (lock) {
                lock.notify();
            }
        });
        
        // print the configured player
        System.out.println(player);

        // play!
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
    public void testPlayPiece3() throws MidiUnavailableException, InvalidMidiDataException{ 
        Instrument piano = Instrument.PIANO;
        
        // create a new player
        final int beatsPerMinute = 100; // a beat is a quarter note, so this is 120 quarter notes per minute
        final int ticksPerBeat = 64; // allows up to 1/64-beat notes to be played with fidelity
        SequencePlayer player = new MidiSequencePlayer(beatsPerMinute, ticksPerBeat);
         
        // addNote(instr, pitch, startBeat, numBeats) schedules a note with pitch value 'pitch'
        // played by 'instr' starting at 'startBeat' to be played for 'numBeats' beats.
        
        double startBeat = 0;
        double numBeats = 1;
        
        /** MEASURE 1 **/ 
        // half rest
        startBeat = startBeat + 2;
        
        // quarter note
        player.addNote(piano, new Pitch('D'), startBeat, numBeats); 
        player.addEvent(startBeat, (x) -> System.out.print("A"));
        startBeat = startBeat + 1; 
        
        /** MEASURE 2 **/ 
        // half note
        player.addNote(piano, new Pitch('G'), startBeat, numBeats*2); 
        player.addEvent(startBeat, (x) -> System.out.print("-ma"));
        startBeat = startBeat + 2; 
        
        // eighth note
        player.addNote(piano, new Pitch('B'), startBeat, numBeats/2); 
        player.addEvent(startBeat, (x) -> System.out.print("-zing"));
        startBeat = startBeat + 0.5; 
        
        // eighth note
        player.addNote(piano, new Pitch('G'), startBeat, numBeats/2); 
        startBeat = startBeat + 0.5; 
        
        /** MEASURE 3 **/ 
        // half note
        player.addNote(piano, new Pitch('B'), startBeat, numBeats*2); 
        player.addEvent(startBeat, (x) -> System.out.print(" grace!"));
        startBeat = startBeat + 2; 
        
        // quarter note
        player.addNote(piano, new Pitch('A'), startBeat, numBeats); 
        player.addEvent(startBeat, (x) -> System.out.print(" how"));
        startBeat = startBeat + 1; 
        
        /** MEASURE 4 **/ 
        // half note
        player.addNote(piano, new Pitch('G'), startBeat, numBeats*2); 
        player.addEvent(startBeat, (x) -> System.out.print(" sweet"));
        startBeat = startBeat + 2; 
        
        // quarter note
        player.addNote(piano, new Pitch('E'), startBeat, numBeats); 
        player.addEvent(startBeat, (x) -> System.out.print(" the"));
        startBeat = startBeat + 1; 
        
        /** MEASURE 5 **/ 
        // half note
        player.addNote(piano, new Pitch('D'), startBeat, numBeats*2); 
        player.addEvent(startBeat, (x) -> System.out.print(" sound"));
        startBeat = startBeat + 2; 
        
        // quarter note
        player.addNote(piano, new Pitch('D'), startBeat, numBeats); 
        player.addEvent(startBeat, (x) -> System.out.print(" That"));
        startBeat = startBeat + 1; 
        
        /** MEASURE 6 **/ 
        // half note
        player.addNote(piano, new Pitch('G'), startBeat, numBeats*2); 
        player.addEvent(startBeat, (x) -> System.out.print(" saved"));
        startBeat = startBeat + 2; 
        
        // eighth note
        player.addNote(piano, new Pitch('B'), startBeat, numBeats/2); 
        player.addEvent(startBeat, (x) -> System.out.print(" a"));
        startBeat = startBeat + 0.5; 
        
        // eighth note
        player.addNote(piano, new Pitch('G'), startBeat, numBeats/2); 
        startBeat = startBeat + 0.5; 
        
        /** MEASURE 7 **/ 
        // half note
        player.addNote(piano, new Pitch('B'), startBeat, numBeats*2); 
        player.addEvent(startBeat, (x) -> System.out.print(" wretch"));
        startBeat = startBeat + 2; 
        
        // quarter note
        player.addNote(piano, new Pitch('A'), startBeat, numBeats); 
        player.addEvent(startBeat, (x) -> System.out.print(" like"));
        startBeat = startBeat + 1; 
        
        /** MEASURE 8 **/ 
        // dotted half note
        player.addNote(piano, new Pitch('D').transpose(Pitch.OCTAVE), startBeat, numBeats*3); 
        player.addEvent(startBeat, (x) -> System.out.print(" me."));
        startBeat = startBeat + 3; 
        
        // add a listener at the end of the piece to tell main thread when it's done
        Object lock = new Object();
        player.addEvent(startBeat, (Double beat) -> {
            synchronized (lock) {
                lock.notify();
            }
        });
        
        // print the configured player
        System.out.println(player);

        // play!
        player.play();
        
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                return;
            }
        }
    }
    
    
    
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
}
