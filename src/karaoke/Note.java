package karaoke;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import karaoke.sound.Instrument;
import karaoke.sound.Pitch;
import karaoke.sound.SequencePlayer;

/**
 * A musical note 
 */
public class Note implements Music{
    
    // Abstraction Function
    //  AF(transpose, octave, pitch, noteLength, tupletLength, noteSyllable, header):
    //  represents a note with transposition of integer transpose semitones away from 
    //  specified pitch & octave. A note's length is determined by noteLength * tupletLength, 
    //  and has a corresponding syllable, noteSyllable. It is also associated with the Header header.  
    // Rep Invariant
    //  true
    // Safety from rep exposure
    //  all fields are private and immutable
    //  every observer method returns an immutable object
    // Thread Safety argument
    //  This class is threadsafe because it's immutable: 
    //  noteSyllable is final
    //  transpose, octave, pitch, noteLength, tupletLength point to immutable 
    //  objects, are never exposed to clients, and is confined 
    //  to one single thread in the class

    private static final Map<String, List<String>> SHARPS;
    private static final Map<String, List<String>> FLATS;

    static 
    {
        SHARPS = new HashMap<String, List<String>>();
        SHARPS.put("C", new ArrayList<>());
        SHARPS.put("G", Arrays.asList("F"));
        SHARPS.put("D", Arrays.asList("F","C"));
        SHARPS.put("A", Arrays.asList("F", "C", "G"));
        SHARPS.put("E", Arrays.asList("F","C", "G", "D"));
        SHARPS.put("B", Arrays.asList("F", "C", "G", "D", "A"));
        SHARPS.put("F#", Arrays.asList("F", "C", "G", "D", "A", "E"));
        SHARPS.put("C#", Arrays.asList("F", "C", "G", "D", "A", "E", "B"));
        SHARPS.put("Am", new ArrayList<>());
        SHARPS.put("Em", Arrays.asList("F"));
        SHARPS.put("Bm", Arrays.asList("F","C"));
        SHARPS.put("F#m", Arrays.asList("F", "C", "G"));
        SHARPS.put("C#m", Arrays.asList("F","C", "G", "D"));
        SHARPS.put("G#m", Arrays.asList("F", "C", "G", "D", "A"));
        SHARPS.put("D#m", Arrays.asList("F", "C", "G", "D", "A", "E"));
        SHARPS.put("A#m", Arrays.asList("F", "C", "G", "D", "A", "E", "B"));
        
        FLATS = new HashMap<String, List<String>>();
        FLATS.put("C", new ArrayList<>());
        FLATS.put("F", Arrays.asList("B"));
        FLATS.put("Bb", Arrays.asList("B","E"));
        FLATS.put("Eb", Arrays.asList("B", "E", "A"));
        FLATS.put("Ab", Arrays.asList("B","E", "A", "D"));
        FLATS.put("Db", Arrays.asList("B", "E", "A", "D", "G"));
        FLATS.put("Gb", Arrays.asList("B", "E", "A", "D", "G", "C"));
        FLATS.put("Cb", Arrays.asList("B", "E", "A", "D", "G", "C", "F"));
        FLATS.put("Am", new ArrayList<>());
        FLATS.put("Dm", Arrays.asList("B"));
        FLATS.put("Gm", Arrays.asList("B","E"));
        FLATS.put("Cm", Arrays.asList("B", "E", "A"));
        FLATS.put("Fm", Arrays.asList("B","E", "A", "D"));
        FLATS.put("Bbm", Arrays.asList("B", "E", "A", "D", "G"));
        FLATS.put("Ebm", Arrays.asList("B", "E", "A", "D", "G", "C"));
        FLATS.put("Abm", Arrays.asList("B", "E", "A", "D", "G", "C", "F"));
    }

    private int transpose;
    private int octave;
    private Pitch pitch;
    private double notelength;
    private double tupletlength;
    private Syllable noteSyllable; 
    private Header header;
    
    private String noteStringInput; 
    private String noteStringLength; 
    private String tupletStringLength; 
    private char notebase;
    
    /**
     * Creates a new Note from a given parseable string and header information.
     * 
     * @param noteInput the parsable Note from the AST
     * @param header Header info about the entire piece
     */
    public Note(String noteInput, Header header) {
        this(noteInput, header, "1");
    }
    
    /**
     * Creates a new Note from a given parseable string and header information, within a tuplet.
     * 
     * @param noteInput the parsable Note from the AST
     * @param header Header info about the entire piece
     * @param tupletStringLength the multiplier to multiply current note by due to tuplets
     */
    public Note(String noteInput, Header header, String tupletStringLength) {
        this(noteInput, header, "1", tupletStringLength, new Syllable("unknown","",0,0));
    }
    
    /**
     * Creates a new Note from a given parseable string and header information, 
     * with a custom length and/or within a tuplet.
     * 
     * @param noteInput the parsable Note from the AST
     * @param header Header info about the entire piece
     * @param length the duration multiplier for current note due to duration specifications in the AST
     * @param tupletStringLength tupletStringLength the duration multiplier for current note by due to tuplets
     */
    public Note(String noteInput, Header header, String length, String tupletStringLength) {
        this(noteInput, header, length, tupletStringLength, new Syllable("unknown","", 0,0));
    }
    
    /**
     * Creates a new Note from a given parseable string and header information, 
     * with a custom length and/or within a tuplet.
     * Also incorporates the corresponding syllable of Lyric within the Music.
     * 
     * @param noteStringInput the parsable Note from the AST
     * @param header Header info about the entire piece
     * @param noteStringLength the duration multiplier for current note due to duration specifications in the AST
     * @param tupletStringLength tupletStringLength the multiplier to multiply current note by due to tuplets
     * @param noteSyllable the syllable of Lyric that corresponds to this particular Note
     */
    public Note(String noteStringInput, Header header, String noteStringLength, String tupletStringLength, Syllable noteSyllable) {
        this.noteStringInput = noteStringInput; 
        this.header = header;
        this.noteStringLength = noteStringLength;
        this.tupletStringLength = tupletStringLength;
        this.noteSyllable = noteSyllable; 
        
        for (int i=0; i<noteStringInput.length(); i++) {
            if (noteStringInput.charAt(i)!='^' && noteStringInput.charAt(i)!='_' && noteStringInput.charAt(i)!='=') {
                notebase = noteStringInput.charAt(i);
                break;
            }
        }
        octave = 0;
        if (Character.isLowerCase(notebase)) {
            octave += 1;
            notebase = Character.toUpperCase(notebase);
        }
        boolean accidentals = false;
        for (int i=0; i<noteStringInput.length(); i++) {
            if (noteStringInput.charAt(i)==',') {
                octave -=1;
            }
            else if (noteStringInput.charAt(i)=='\'') {
                octave +=1;
            }
            else if (noteStringInput.charAt(i)=='^') {
                transpose +=1;
                accidentals = true;
            }
            else if (noteStringInput.charAt(i)=='_') {
                transpose -=1;
                accidentals = true;
            }
        }
        if (noteStringInput.contains("=")) {
            transpose = 0;
            accidentals = true;
        }
 
        if (!accidentals) {
            if (SHARPS.containsKey(header.key()) && SHARPS.get(header.key()).contains(String.valueOf(notebase))) {
                transpose +=1;
            }
            else if (FLATS.containsKey(header.key()) && FLATS.get(header.key()).contains(String.valueOf(notebase))) {
                transpose -=1;
            }
        }
        pitch = new Pitch(notebase);
        int finaltranspose = transpose + octave*12;
        pitch = pitch.transpose(finaltranspose);
        if (noteStringLength.contains("/")) {
            if (noteStringLength.charAt(0)=='/') {
                if (noteStringLength.length()>1) {
                    notelength = 1.0/Double.parseDouble(noteStringLength.substring(1, noteStringLength.length()));
                }
                else {
                    notelength = 1.0/2.0;
                }
            }
            else {
                double numerator = Double.parseDouble(noteStringLength.substring(0,noteStringLength.indexOf("/")));
                double denominator = 0.0;
                if(noteStringLength.charAt(noteStringLength.length()-1)=='/') {
                    denominator = 2.0;
                }
                else {
                    denominator = Double.parseDouble(noteStringLength.substring(noteStringLength.indexOf("/")+1,noteStringLength.length()));
                }    
                notelength = numerator/denominator;
            }
        }
        else {
            notelength = Double.parseDouble(noteStringLength);
        }
        //Very similar code to notelength
        if (tupletStringLength.contains("/")) {
            if (tupletStringLength.charAt(0)=='/') {
                if (tupletStringLength.length()>1) {
                    tupletlength = 1.0/Double.parseDouble(noteStringLength.substring(1, tupletStringLength.length()));
                }
                else {
                    tupletlength = 1.0/2.0;
                }
            }
            else {
                double numerator = Double.parseDouble(tupletStringLength.substring(0,tupletStringLength.indexOf("/")));
                double denominator = 0.0;
                if(tupletStringLength.charAt(tupletStringLength.length()-1)=='/') {
                    denominator = 2.0;
                }
                else {
                    denominator = Double.parseDouble(tupletStringLength.substring(tupletStringLength.indexOf("/")+1,tupletStringLength.length()));
                }    
                tupletlength = numerator/denominator;
            }
        }
        else {
            tupletlength = Double.parseDouble(tupletStringLength);
        }
    }
    
    @Override
    public void play(SequencePlayer player, double atBeat, Player mainPlayer) {
        player.addNote(Instrument.PIANO, pitch, atBeat, notelength);
        if (!noteSyllable.isSkipped()) {
            player.addEvent(atBeat, (x)-> mainPlayer.streamToAll(noteSyllable.getLine(),noteSyllable.getVoice())); 
        }
    }

    @Override
    public double duration() {
        return notelength*tupletlength;
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
    public String toString() {
        return "Note(" + noteStringInput + noteStringLength + ")";
    }
    
    @Override
    public Header header() {
        return this.header;
    }
    
    @Override
    public int numNotesFree() { 
        if (noteSyllable.getLyricLine().isEmpty()) {
            // the note is free and syllable can link to it
            return 1;
        }
        return 0; 
    }
    
    @Override
    public Music addSyllableToNote(Syllable inputSyllable) {
        if (this.numNotesFree() == 1) {
            this.noteSyllable = inputSyllable; 
        }
                
        return new Note(
                this.noteStringInput,
                this.header,
                this.noteStringLength,
                this.tupletStringLength,
                this.noteSyllable);
    }
    
    @Override
    public boolean equals(Object that) {
        if (that instanceof Note){
            Note other = (Note) that;
            return this.pitch.equals(other.pitch) && this.notelength==other.notelength;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.pitch.hashCode() + (int) Math.round(this.notelength);
    }
   
}
