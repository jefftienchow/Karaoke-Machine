package karaoke;

import java.io.PrintWriter;

import karaoke.sound.SequencePlayer;

/**
 * Immutable Music DataType
 *
 */
public interface Music {
    
    /*
     * Datatype Definition
     * Music = Note(noteInput: String, header: Header, length: String)
        + Rest(header: Header, length: String)
        + Concat(m1:Music, m2:Music)
        + Chord(m1:Music, m2:Music)
        + OverlayVoice(m1:Music, m2:Music)
        + Repeat(m1:Music, m2:Music)
        + Lyric(duration:double, caption:String)

     */
    
    /**
     * Adds the notes of the music to player 
     * @param player player to play on
     * @param atBeat when to play
     */
    void play(SequencePlayer player, double atBeat, Player mainPlayer);
    
    /**
     * @return the total duration of the music in beats
     */
    double duration();
    
    /**
     * @return beats per minute of the music
     */
    int beatsPerMinute();
    
    /**
     * @return ticks per beat of the music
     */
    int ticksPerBeat();
    
    /**
     * @return the header info of the music
     */
    Header header();
    
    /**
     * @return the number of notes free for lyrics
     */
    int numNotesFree(); 
    
    /**
     * produces a new Music object with the included syllable
     * @param syllable
     * @return Music object with syllable
     */
    Music addSyllableToNote(Syllable syllable); 
    
}
