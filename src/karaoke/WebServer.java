package karaoke;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import examples.StreamingExample;
import karaoke.sound.Instrument;
import karaoke.sound.Pitch;
import karaoke.sound.SequencePlayer;
import karaoke.Player;
/**
 * Web server that plays music. 
 */
public class WebServer {
    // Abstraction function:
    //   A server representing a karaoke machine that multiple clients can connect to in order to
    //   listen to and view lyrics of music scheduled by player. 
    //
    // Representation invariant:
    //   True
    //
    // Safety from rep exposure:
    //   All fields are private, final, and never accessed by clients
    //
    // Thread safety argument:
    //   Web servers have automatic synchronization
    //   All fields are private and final and never accessed by clients
    
    private final HttpServer server;
    private static Object lock;
    private static Player mainPlayer;

    /**
     * Creates a new WebServer that listens for connections who want to join in on Karaoke.
     * @param port the port number to connect to
     * @param mainPlayer the player that contains all of the outputstreams to stream to
     * @param lock lock that is shared among all threads
     * @param m music that we are streaming the lyrics to
     * @throws IOException if network problem
     */
    public WebServer(int port,  Player mainPlayer, Object lock, Music m) throws IOException{
        WebServer.mainPlayer = mainPlayer;
        WebServer.lock = lock;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
     // handle concurrent requests with multiple threads
        server.setExecutor(Executors.newCachedThreadPool());
        // register handlers
        server.createContext("/textStream", WebServer::textStream);
        for (String voice:m.header().voices()) {
            server.createContext("/" + voice, WebServer::voiceStream);
        }
    }
    
    /**
     * start the webserver
     */
    public void start() {
        server.start();
    }
    
    /**
     * This handler sends a plain text stream of lines of lyrics to a song,
     * highlighting the word/syllable that the song is currently on
     * client must exit using Ctrl-C
     * 
     * @param exchange request/reply object
     * @throws IOException if network problem
     */
    private static void textStream(HttpExchange exchange) throws IOException {
        final String path = exchange.getRequestURI().getPath();
        System.err.println("received request " + path);

        // plain text response
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");

        // must call sendResponseHeaders() before calling getResponseBody()
        final int successCode = 200;
        final int lengthNotKnownYet = 0;
        exchange.sendResponseHeaders(successCode, lengthNotKnownYet);
        
        // get output stream to write to web browser
        final boolean autoflushOnPrintln = true;
        PrintWriter out = new PrintWriter(
                              new OutputStreamWriter(
                                  exchange.getResponseBody(), 
                                  StandardCharsets.UTF_8), 
                              autoflushOnPrintln);
        
        try {
            // IMPORTANT: some web browsers don't start displaying a page until at least 2K bytes
            // have been received.  So we'll send a line containing 2K spaces first.
            final int enoughBytesToStartStreaming = 2048;
            for (int i = 0; i < enoughBytesToStartStreaming; ++i) {
                out.print(' ');
            }
            out.println(); // also flushes
            mainPlayer.addWriter(out,"unknown");
            synchronized (lock) {
                while (true) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            
            
        } finally {
            exchange.close();
        }
    }
    
    /**
     * This handler sends a plain text stream of lines of lyrics for
     * a specific voice in a song, highlighting the word/syllable that 
     * the song is currently on
     * client must exit using Ctrl-C
     * 
     * @param exchange request/reply object
     * @throws IOException if network problem
     */
    private static void voiceStream(HttpExchange exchange) throws IOException {
        final String path = exchange.getRequestURI().getPath();
        System.err.println("received request " + path);
        String voice = path.substring(1, path.length());
        // plain text response
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");

        // must call sendResponseHeaders() before calling getResponseBody()
        final int successCode = 200;
        final int lengthNotKnownYet = 0;
        exchange.sendResponseHeaders(successCode, lengthNotKnownYet);
        
        // get output stream to write to web browser
        final boolean autoflushOnPrintln = true;
        PrintWriter out = new PrintWriter(
                              new OutputStreamWriter(
                                  exchange.getResponseBody(), 
                                  StandardCharsets.UTF_8), 
                              autoflushOnPrintln);
        
        try {
            // IMPORTANT: some web browsers don't start displaying a page until at least 2K bytes
            // have been received.  So we'll send a line containing 2K spaces first.
            final int enoughBytesToStartStreaming = 2048;
            for (int i = 0; i < enoughBytesToStartStreaming; ++i) {
                out.print(' ');
            }

            out.println(); // also flushes

            mainPlayer.addWriter(out,voice);
            synchronized (lock) {
                while (true) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            
        } finally {
            exchange.close();
        }
    }
    
}
