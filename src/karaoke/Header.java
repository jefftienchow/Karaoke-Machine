package karaoke;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a header, which stores all the beginning information of the music (everything except notes and sections)
 * 
 * Contains the following information:
 *      Index: the index of the piece
 *      Title: the title of the piece
 *      MeterNumberatior: the first part of the time signature
 *      MeterDenominator: the second part of the time signature
 *      Key: The determiner of "accidentals" and transposes of various notes in the scale
 *      Tempo: the beats per minute of the piece
 *      NoteLength: When we say a note is of length "1", it actually means a note that is x beats long
 *      Voices: Represents the set of voices heard throughout the piece, in the form of various instruments.
 *      Music: The musical note part, aka the non-header part of the piece. 
 *
 */
public class Header {
    
    // Abstraction Function
    //  AF(index, title, meterNumerator, meterDenominator, key, 
    //     tempo, noteLength, voices, music, tempoLenght, composer): 
    //  represents a header object that lists the fields:
    //      Index: the index of the piece
    //      Title: the title of the piece
    //      MeterNumberatior: the first part of the time signature
    //      MeterDenominator: the second part of the time signature
    //      Key: The determiner of "accidentals" and transposes of various notes in the scale
    //      Tempo: the beats per minute of the piece
    //      NoteLength: When we say a note is of length "1", it actually means a note that is x beats long
    //      Voices: Represents the set of voices heard throughout the piece, in the form of various instruments.
    //      Music: The musical note part, aka the non-header part of the piece. 
    //
    // Rep Invariant
    //   true
    // Safety from rep exposure
    //   all the fields are private and final
    //   all the fields except voices are immutable 
    //   voices is defensively copied when observed by client
    // Thread Safety argument
    //   The class is immutable
    //   all fields are private and final 
    //   voices can't be mutated by other clients because 
    //   it's defensively copied when trying to be observed
    
    private final int index ; //X:
    private final String title; //T:
    private final int meterNumerator; //M:
    private final int meterDenominator; //M:
    private final String key; //K:
    private final double tempo; //Q:
    private final double noteLength; //L:
    private final Set<String> voices; //V:
    private final String music;
    private final double tempoLength;
    private final String composer;

    /**
     * Constructs a default header
     * @param header
     */
    public Header(int index, String title, int num, int den, String key, double tempo, double noteLength, Set<String> voices, String music, double tempoLength, String composer) {
        this.index = index;
        this.title = title;
        this.meterNumerator = num;
        this.meterDenominator = den;
        this.key = key;
        this.tempo = tempo;
        this.noteLength = noteLength;
        this.voices = voices;
        this.music = music;
        this.tempoLength = tempoLength;
        this.composer = composer;
    }
    
    /**
     * @return index of the piece
     */
    public int index() {
        return index;
    }
    
    /**
     * @return the title of the piece
     */
    public String title() {
        return title;
    }
    
    /**
     * @return the first part of the time signature
     */
    public int meterN() {
        return meterNumerator;
    }
    /**
     * @return the second part of the time signature
     */
    public int meterD() {
        return meterDenominator;
    }
    
    /**
     * @return The determiner of "accidentals" and transposes of various notes in the scale
     */
    public String key() {
        return key;
    }
    
    /**
     * @return the beats per minute of the piece
     */
    public double tempo() {
        return tempo;
    }
    
    /**
     * @return When we say a note is of length "1", it actually means a note that is x beats long
     */
    public double noteLength() {
        return noteLength;
    }
    
    /**
     * @return the set of voices heard throughout the piece, in the form of various instruments.
     */
    public Set<String> voices(){
        return new HashSet<>(this.voices);
    }
    
    /**
     * @return The musical note part, aka the non-header part of the piece. 
     */
    public String getMusic() {
        return music; 
    }
    
    /**
     * @return the length of a tempo
     */
    public double tempoLength() {
        return tempoLength;
    }
    
    /**
     * @return name of composer; unknown if composer is not given
     */
    public String composer() {
        return composer;
    }
    
}
