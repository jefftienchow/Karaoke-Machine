package karaoke;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import com.sun.net.httpserver.HttpServer;

import edu.mit.eecs.parserlib.UnableToParseException;
import karaoke.parser.KaraokeParser;
import karaoke.sound.Instrument;
import karaoke.sound.MidiSequencePlayer;
import karaoke.sound.SequencePlayer;


/**
 * Main entry point of your application.
 */
public class Main {

    /**
     * main method for karoke machine
     * @param args command line argument in which ABC file is passed in args[0]
     * @throws IOException
     */
    public static void main(String[] args) throws MidiUnavailableException, InvalidMidiDataException, IOException{
        try {
            final File file = new File(args[0]);
            FileReader in;
            BufferedReader br;
            try {
                in = new FileReader(file);
                br = new BufferedReader(in);
            }
            catch (IOException e) {
                throw new IOException("file not found");
            }
            String line;
            String abcString = "";
            while ((line = br.readLine()) != null) {
                abcString += line + "\n";
            }
            in.close();
            String ip = "unknown";
            for (NetworkInterface iface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (InetAddress address: Collections.list(iface.getInetAddresses())) {
                    if (address instanceof Inet4Address) {
                        if (!address.getHostName().equals("localhost")) {
                            ip = address.getHostName();
                        }
                        
                    }
                }
            }
            
            Music m = KaraokeParser.parse(abcString);
            Header header = m.header();
            System.out.println(header.title());
            System.out.println(header.composer());
            Instrument piano = Instrument.PIANO;


            final int beatsPerMinute = (int) (header.tempoLength()/header.noteLength()*header.tempo()); // a beat is a quarter note, so this is 120 quarter notes per minute
            final int ticksPerBeat = 96; // allows up to 1/64-beat notes to be played with fidelity
            SequencePlayer player = new MidiSequencePlayer(beatsPerMinute, ticksPerBeat);
                        
            final int serverPort = 4567;
            Player mainPlayer = new Player();
            Object lock = new Object();
            WebServer webserver = new WebServer(serverPort, mainPlayer,lock,m);
            webserver.start();
            
            m.play(player, 0, mainPlayer);
            
            final BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));
            if (m.header().voices().size()>0) {
                for (String voice:m.header().voices()) {
                    System.out.println("Go to \"http://" + ip + ":4567/"

                            + voice + "\" to view lyrics for " + voice + " voice");
                }
            }
            else {
                System.out.println("Go to \"" + ip + "\" to view the lyrics");
            }
            System.out.println("Or, substitute \"" + ip + "\" with the IP address of the "
                    + "machine running the program");

            System.out.println("press \"play\" to begin");
            System.out.println("press \"Ctrl-C\" to exit");
            
            while (true) {
                
                final String input = systemIn.readLine();
                
                if (input.isEmpty()) {
                    System.exit(0); // exits the program
                }

                if (input.equals("play")) {
                    break;
                }
                
            }
            
            player.play();
            
            
        } catch (UnableToParseException e) {
            e.printStackTrace();
        }
    }
}
