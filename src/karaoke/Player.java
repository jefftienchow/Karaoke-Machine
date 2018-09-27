package karaoke;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import karaoke.sound.MidiSequencePlayer;
import karaoke.sound.SequencePlayer;

/**
 * keeps track of the outputs to stream to
 */
public class Player {
    
    /*
     * Abstraction function
     *  AF(writers) = a collection of clients that we want to stream lyrics to, where each element 
     *      is a printwriter that gives outputs the lyrics
     *  Rep invariant
     *      true
     *  Safety from rep exposure
     *      nothing is returned
     *      inputs are all immutable
     *  Thread safety argument
     *      all fields are private and final
     *      elements are only added to list, so there won't be any errors with accessing indices
     */

    
    private final Map<String,List<PrintWriter>> writers;

    /**
     * constructor for player
     */
    public Player(){
        writers = new HashMap<String,List<PrintWriter>>();
    }
    
        
    /**
     * adds a client's printwriter
     * @param out printwriter that we want to stream to
     * @param voice voice that the client wants to view 
     */
    public void addWriter(PrintWriter out, String voice) {

        if (writers.containsKey(voice)) {
            writers.get(voice).add(out);
        }
        else {
            writers.put(voice, new ArrayList<PrintWriter>());
            writers.get(voice).add(out);
        }
    }
    
    /**
     * streams to all clients
     * @param lyrics lyrics that we want to stream
     * @param voice the voice we want to stream
     */
    public void streamToAll(String lyrics, String voice) {
        for (PrintWriter writer:writers.get(voice)) {
            writer.println(lyrics);
        }
    }
    
}
