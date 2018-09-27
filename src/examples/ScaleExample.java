package examples;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import karaoke.sound.Instrument;
import karaoke.sound.MidiSequencePlayer;
import karaoke.sound.Pitch;
import karaoke.sound.SequencePlayer;

public class ScaleExample {
    
    /**
     * Play an octave up and back down starting from middle C.
     * 
     * @param args not used
     * @throws MidiUnavailableException if MIDI device unavailable
     * @throws InvalidMidiDataException if MIDI play fails
    */
    public static void main(String[] args) throws MidiUnavailableException, InvalidMidiDataException {
        /*
        Instrument piano = Instrument.PIANO;

        // create a new player
        final int beatsPerMinute = 120; // a beat is a quarter note, so this is 120 quarter notes per minute
        final int ticksPerBeat = 64; // allows up to 1/64-beat notes to be played with fidelity
        SequencePlayer player = new MidiSequencePlayer(beatsPerMinute, ticksPerBeat);
        
        // addNote(instr, pitch, startBeat, numBeats) schedules a note with pitch value 'pitch'
        // played by 'instr' starting at 'startBeat' to be played for 'numBeats' beats.
        
        int startBeat = 0;
        int numBeats = 1;
        player.addNote(piano, new Pitch('C'), startBeat++, numBeats);
        player.addNote(piano, new Pitch('D'), startBeat++, numBeats);
        player.addNote(piano, new Pitch('E'), startBeat++, numBeats);
        player.addNote(piano, new Pitch('F'), startBeat++, numBeats);
        player.addNote(piano, new Pitch('G'), startBeat++, numBeats);
        player.addNote(piano, new Pitch('A'), startBeat++, numBeats);
        player.addNote(piano, new Pitch('B'), startBeat++, numBeats);
        player.addNote(piano, new Pitch('C').transpose(Pitch.OCTAVE), startBeat++, numBeats);
        player.addNote(piano, new Pitch('B'), startBeat++, numBeats);
        player.addNote(piano, new Pitch('A'), startBeat++, numBeats);
        player.addNote(piano, new Pitch('G'), startBeat++, numBeats);
        player.addNote(piano, new Pitch('F'), startBeat++, numBeats);
        player.addNote(piano, new Pitch('E'), startBeat++, numBeats);
        player.addNote(piano, new Pitch('D'), startBeat++, numBeats);
        player.addNote(piano, new Pitch('C'), startBeat++, numBeats);
        
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
        
        // wait until player is done
        // (not strictly needed here, but useful for JUnit tests)
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                return;
            }
        }
        System.out.println("done playing");
        */
        Instrument piano = Instrument.PIANO;

        // create a new player
        final int beatsPerMinute = 140; // a beat is a quarter note, so this is 120 quarter notes per minute
        final int ticksPerBeat = 96; // allows up to 1/64-beat notes to be played with fidelity
        SequencePlayer player = new MidiSequencePlayer(beatsPerMinute, ticksPerBeat);
         
        // addNote(instr, pitch, startBeat, numBeats) schedules a note with pitch value 'pitch'
        // played by 'instr' starting at 'startBeat' to be played for 'numBeats' beats.
        
        double startBeat = 0;
        int numBeats = 1;
        
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
    } 
    
}
