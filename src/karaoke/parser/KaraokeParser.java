package karaoke.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import edu.mit.eecs.parserlib.ParseTree;
import edu.mit.eecs.parserlib.Parser;
import edu.mit.eecs.parserlib.UnableToParseException;
import edu.mit.eecs.parserlib.Visualizer;
import karaoke.Chord;
import karaoke.Concat;
import karaoke.Header;
import karaoke.Music;
import karaoke.Note;
import karaoke.OverlayVoice;
import karaoke.Player;
import karaoke.Rest;
import karaoke.Syllable;
import karaoke.sound.Instrument;
import karaoke.sound.MidiSequencePlayer;
import karaoke.sound.SequencePlayer;

/**
 * 
 * The main Karaoke parser. Has tools to convert abc files into our Music datatype and its variants.
 * 
 *
 */
public class KaraokeParser {
    
    //TODO Abstraction Function
    //TODO Rep Invariant
    //TODO Safety from rep exposure
    //TODO Thread Safety argument
    
    /**
     * Main method. Parses and then reprints an example abc file.
     * The main method first subdivides a abc file string into two main sections: the header, and the music.
     * The header consists of aspects of music as specified in the Header class, and represents that Header class.
     * The input and music string argument represents the respective portions of the abc file parsed as a string, complete with newlines and all.
     * 
     * @param args command line arguments, not used
     * @throws UnableToParseException if example expression can't be parsed
     * @throws InvalidMidiDataException 
     * @throws MidiUnavailableException 
     */
    public static void main(final String[] args) throws UnableToParseException, MidiUnavailableException, InvalidMidiDataException {
        final String input = "X:1\n" + 
                "T:Bagatelle No.25 in A, WoO.59\n" + 
                "C:Ludwig van Beethoven\n" + 
                "V:1\n" + 
                "V:2\n" + 
                "M:3/8\n" + 
                "L:1/16\n" + 
                "Q:1/8=140\n" + 
                "K:Am\n" + 
                "V:1\n" + 
                "e^d|e^deB=dc|A2 z CEA|B2 z E^GB|c2 z Ee^d|\n" + 
                "V:2\n" + 
                "z2|z6|A,,E,A, z z2|E,,E,^G, z z2|A,,E,A, z z2|\n" + 
                "%\n" + 
                "V:1\n" + 
                "e^deB=dc|A2 z CEA|B2 z EcB|[1A2 z2:|[2A2z Bcd|\n" + 
                "V:2\n" + 
                "z6|A,,E,A, z z2|E,,E,^G, z z2|[1A,,E,A, z :|[2A,,E,A, z z2|\n" + 
                "%\n" + 
                "V:1\n" + 
                "|:e3 Gfe|d3 Fed|c3 Edc|B2 z Ee z|z ee' z z ^d|\n" + 
                "V:2\n" + 
                "|:C,E,C z z2|G,,G,B, z z2|A,,E,A, z z2|E,,E,E z z E|e z z ^de z|\n" + 
                "%\n";
        
        final ParseTree<HeaderGrammar> headerParseTree = headerParser.parse(input); 
        Header musicHeader = makeHeader(headerParseTree);
        // will pass in the header values to the header object 
        
        final ParseTree<MusicGrammar> parseTree = musicParser.parse(musicHeader.getMusic());
        Music musicTest = makeAbstractSyntaxTree(parseTree, musicHeader);
        //Visualizer.showInBrowser(parseTree);
        
        Instrument piano = Instrument.PIANO;
        System.out.println(musicTest);

        final int beatsPerMinute = (int) (musicHeader.tempoLength()/musicHeader.noteLength()*musicHeader.tempo()); // a beat is a quarter note, so this is 120 quarter notes per minute
        final int ticksPerBeat = 96; // allows up to 1/64-beat notes to be played with fidelity
        SequencePlayer player = new MidiSequencePlayer(beatsPerMinute, ticksPerBeat);
        musicTest.play(player, 0, new Player());
        player.play();
    }
    
    public static enum HeaderGrammar{
        ABCHEADER, FIELDNUMBER, FIELDTITLE, OTHERFIELDS, FIELDCOMPOSER,
        FIELDDEFAULTLENGTH, FIELDMETER, FIELDTEMPO, FIELDVOICE,
        FIELDKEY, NOTELENGTHSTRICT, KEY, KEYNOTE, KEYACCIDENTAL,
        MODEMINOR, METER, METERFRACTION, TEMPO, COMMENT, COMMENTTEXT,
        ENDOFLINE, MUSIC, COMMON, HALF, DIGIT, NEWLINE, SPACEORTAB, BASENOTE,
        TEXT
    }
    
    public static enum MusicGrammar{ 
        ABCBODY, ABCLINE, ELEMENT, NOTEELEMENT, NOTE, PITCH,
        OCTAVE, NOTELENGTH, NOTELENGTHSTRICT, ACCIDENTAL, BASENOTE,
        RESTELEMENT, TUPLETELEMENT, TUPLETSPEC, CHORD, BARLINE, NTHREPEAT,
        MIDDLEOFBODYFIELD, FIELDVOICE, TEXT, LYRIC, LYRICALELEMENT, BACKSLASHHYPHEN,
        COMMENT, ENDOFLINE, DIGIT, NEWLINE, SPACEORTAB, LYRICTEXT, COMMENTTEXT
    }

    private static Parser<MusicGrammar> musicParser = makeMusicParser();
    private static Parser<HeaderGrammar> headerParser = makeHeaderParser();
    
    /**
     * Compile the header grammar into a parser. Designates ABCHEADER as the root of the abstract syntax tree.
     * 
     * @return parser for the grammar
     * @throws RuntimeException if grammar file can't be read or has syntax errors
     */
    private static Parser<HeaderGrammar> makeHeaderParser() { 
        try {
            // read the grammar as a file, relative to the project root.
            final File grammarFile = new File("src/karaoke/parser/Header.g");
            
            //abcheader ::= fieldnumber comment* fieldtitle otherfields* fieldkey music;
            return Parser.compile(grammarFile, HeaderGrammar.ABCHEADER);

        } catch (IOException e) {
            throw new RuntimeException("can't read the grammar file", e);
        } catch (UnableToParseException e) {
            throw new RuntimeException("the grammar has a syntax error", e);
        }
    }
    
    /**
     * Compile the music grammar into a parser. Designates ABCBODY as the root of the abstract syntax tree.
     * 
     * @return parser for the grammar
     * @throws RuntimeException if grammar file can't be read or has syntax errors
     */
    private static Parser<MusicGrammar> makeMusicParser() {
        try {
            // read the grammar as a file, relative to the project root.
            final File grammarFile = new File("src/karaoke/parser/Music.g");
            
            //abcBody ::= abcLine+;
            //abcLine ::= element+ endOfLine (lyric endOfLine)?  | middleOfBodyField endOfLine | comment;
            return Parser.compile(grammarFile, MusicGrammar.ABCBODY);
        } catch (IOException e) {
            throw new RuntimeException("can't read the grammar file", e);
        } catch (UnableToParseException e) {
            throw new RuntimeException("the grammar has a syntax error", e);
        }
    }

    /**
     * Parse an ABC file into a Music Datatype
     * @param string string to parse
     * @return Expression parsed from the string
     * @throws UnableToParseException if the string doesn't match the Expression grammar
     */
    public static Music parse(final String string) throws UnableToParseException {
        final ParseTree<HeaderGrammar> headerParseTree = headerParser.parse(string); 
        Header musicHeader = makeHeader(headerParseTree);
        
        // will pass in the header values to the header object 
        final ParseTree<MusicGrammar> musicParseTree = musicParser.parse(musicHeader.getMusic());
        return makeAbstractSyntaxTree(musicParseTree, musicHeader);
    }
    
    /**
     * Convert a parse tree into an abstract syntax tree.
     * 
     * @param parseTree constructed according to the grammar in Exression.g
     * @return abstract syntax tree corresponding to parseTree
     */
    
    public static Header makeHeader(final ParseTree<HeaderGrammar> parseTree) {
        // default values
        boolean lengthInit = false;
        boolean tempoInit = false;
        int index = 1; //X:
        String title = "Unknown"; //T:
        int meterNumerator = 4; //M:
        int meterDenominator = 4; //M:
        String key = "C"; //K:
        double tempo = 100; //Q:
        double noteLength = .75; //L:
        Set<String> voices = new HashSet<>(); //V:
        String music= ""; 
        double tempoLength = 1;
        String composer = "Unknown";
        
        final List<ParseTree<HeaderGrammar>> headerChildren = parseTree.children();
        for (int i=0; i<headerChildren.size(); i++) {
            switch (headerChildren.get(i).name()) {
                case FIELDNUMBER: //fieldnumber ::= "X:"digit endofline;
                    break;
                case FIELDTITLE: //fieldtitle ::= "T:" text endofline;
                    final List<ParseTree<HeaderGrammar>> titleChildren = headerChildren.get(i).children();
                    title = titleChildren.get(0).text();
                    break;
                case COMMENT: //comment ::= spaceortab* "%" commenttext newline;
                    break;
                case OTHERFIELDS: //otherfields ::= fieldcomposer | fielddefaultlength | fieldmeter | fieldtempo | fieldvoice | comment;
                    final List<ParseTree<HeaderGrammar>> otherfieldsChildren = headerChildren.get(i).children();
                    //assuming one child
                    ParseTree<HeaderGrammar> otherField = otherfieldsChildren.get(0);
                    switch (otherField.name()) {
                        case FIELDCOMPOSER: //fieldcomposer ::= "C:" text endofline;
                            final List<ParseTree<HeaderGrammar>> composerChildren = otherField.children();
                            composer = composerChildren.get(0).text();
                            break;
                        case FIELDDEFAULTLENGTH: //fielddefaultlength ::= "L:" notelengthstrict endofline;
                            final List<ParseTree<HeaderGrammar>> lengthChildren = otherField.children();
                            ParseTree<HeaderGrammar> length = lengthChildren.get(0);
                            final List<ParseTree<HeaderGrammar>> lengthNums = length.children();
                            double lengthNum = Double.parseDouble(lengthNums.get(0).text());
                            if (lengthNums.size()>1) {
                                lengthNum = lengthNum/Double.parseDouble(lengthNums.get(1).text());
                            }
                            noteLength = lengthNum; //L
                            lengthInit = true;
                            break;
                        case FIELDMETER: //fieldmeter ::= "M:" meter endofline;
                            final List<ParseTree<HeaderGrammar>> fieldmeterChildren = otherField.children();
                            ParseTree<HeaderGrammar> meter = fieldmeterChildren.get(0);
                            final List<ParseTree<HeaderGrammar>> meterPossibilities = meter.children();
                            switch (meterPossibilities.get(0).name()) {
                                case COMMON: //common ::= 'C';
                                    meterNumerator = 4;
                                    meterDenominator = 4;
                                    break;
                                case HALF: //half ::= "C|";
                                    meterNumerator = 2; 
                                    meterDenominator = 2;
                                    break;
                                case METERFRACTION: //meterfraction ::= digit "/" digit;
                                    final List<ParseTree<HeaderGrammar>> meterFraction = meterPossibilities.get(0).children();
                                    meterNumerator = Integer.parseInt(meterFraction.get(0).text());
                                    meterDenominator = Integer.parseInt(meterFraction.get(1).text());
                                    break;
                                default:
                                    throw new AssertionError("should never get here");
                            }
                            break;
                        case FIELDTEMPO: //fieldtempo ::= "Q:" tempo endofline;
                            final List<ParseTree<HeaderGrammar>> fieldtempoChildren = otherField.children();
                            ParseTree<HeaderGrammar> headerTempo = fieldtempoChildren.get(0);
                            final List<ParseTree<HeaderGrammar>> tempoChildren = headerTempo.children();
                            tempo = Double.parseDouble(tempoChildren.get(1).text());
                            final List<ParseTree<HeaderGrammar>> tempoLengthChildren = tempoChildren.get(0).children();

                            tempoLength = Double.parseDouble(tempoLengthChildren.get(0).text())/Double.parseDouble(tempoLengthChildren.get(1).text());
                            tempoInit = true;
                            break;
                        case FIELDVOICE: //fieldvoice ::= "V:" text endofline;
                            final List<ParseTree<HeaderGrammar>> fieldvoiceChildren = otherField.children();
                            voices.add(fieldvoiceChildren.get(0).text());
                            break;
                        default:
                            throw new AssertionError("should never get here");
                    }
                    break;

                case FIELDKEY: //fieldkey ::= "K:" key endofline;
                    final List<ParseTree<HeaderGrammar>> fieldkeyChildren = headerChildren.get(i).children();
                    ParseTree<HeaderGrammar> headerKey = fieldkeyChildren.get(0);
                    final List<ParseTree<HeaderGrammar>> keyChildren = headerKey.children();
                    String keyName = keyChildren.get(0).text();
                    if (keyChildren.size()>1) {
                        keyName += "m";
                    }
                    key = keyName;
                    break;
                case MUSIC: //music ::= [^$]+;
                    // pass in string of music to the music parser, basically accepts everything else
                    music = headerChildren.get(i).text();
                    break;
     
                default:
                    throw new AssertionError("should never get here");
                    
            }
        }
        
        //Default note lengths for L=? in the header when L is absent
        if (!lengthInit) {
            if ((double) meterNumerator/meterDenominator < .75) {
                noteLength = 1.0/16;
            }
            else {
                noteLength = 1.0/8;
            }
        }
        //Default tempo designated as 100
        if (!tempoInit) {
            tempo = 100;
            tempoLength = noteLength;

        }

        return new Header(index, title, meterNumerator, meterDenominator, key , tempo, noteLength, voices, music, tempoLength,composer);

    }
    
    /**
     * Creates a Music Object from a parsetree representing a single note.
     * 
     * @param noteValue The parsed to-be Note class object
     * @param header Header info about the entire piece
     * @param measureAccidentals Keeps track of the accidentals throughout the measure
     * @return a Music Object representing that note
     */
    public static Music makeNote(
            final ParseTree<MusicGrammar> noteValue,
            Header header,
            Map<String, String> measureAccidentals) {
        return KaraokeParser.makeNote(noteValue, header, measureAccidentals, "1");
    }
        
    /**
     * Creates a Music Object from a parsetree representing a single note.
     * 
     * @param noteValue The parsed to-be Note class object
     * @param header Header info about the entire piece
     * @param measureAccidentals Keeps track of the accidentals throughout the measure
     * @param tupletLengthStr a (possibly) fractional string denoting the multiplicative factor if the note is part of a tuple
     * @return a Music Object representing that note
     */
    public static Music makeNote(
            final ParseTree<MusicGrammar> noteValue,
            Header header,
            Map<String, String> measureAccidentals,
            String tupletLengthStr) {
        ParseTree<MusicGrammar> pitchElement = noteValue.children().get(0);
        List<ParseTree<MusicGrammar>> pitchValueList = pitchElement.children();
        String noteInput = ""; 
        String accidental= ""; 
        String note = "";
        for (ParseTree<MusicGrammar> pitchValue : pitchValueList) {
            switch (pitchValue.name()) {
                case ACCIDENTAL: //accidental ::= "^" | "^^" | "_" | "__" | "=";
                    accidental += pitchValue.text();
                    break;
                case BASENOTE: //basenote ::= "C" | "D" | "E" | "F" | "G" | "A" | "B" | "c" | "d" | "e" | "f" | "g" | "a" | "b";
                    note += pitchValue.text();
                    break;
                case OCTAVE: //octave ::= "'"+ | ","+;
                    note += pitchValue.text();
                    break;
            }
        }
        
        // takes care of the case for measure accidentals
        if (measureAccidentals.containsKey(note)) {
            String currentAccidental = measureAccidentals.get(note);
            note = measureAccidentals.get(note) + note; 
            if (!accidental.isEmpty()) {
                currentAccidental = accidental + currentAccidental; 
                measureAccidentals.put(note, currentAccidental); 
            }
        } else { 
            // adds new accidental to the measure
            if (!accidental.isEmpty()) {
                measureAccidentals.put(note, accidental);
            }
        }
        
        noteInput = accidental + note;
        
        
        
        if (noteValue.children().size() == 2) {
            ParseTree<MusicGrammar> noteLength = noteValue.children().get(1);
            String length = noteLength.text();
            
            // if length specified
            if (!length.isEmpty()) {
                return new Note(noteInput, header, length, tupletLengthStr);           
            }
        }
        
        return new Note(noteInput, header, tupletLengthStr); 
    }
    
    /**
     * Creates a Music Object from a parsetree representing a single rest.
     * 
     * @param restValue The parsed to-be Rest class object
     * @param header Header info about the entire piece
     * @return a Music Object representing that rest
     */
    public static Music makeRest(final ParseTree<MusicGrammar> restValue, Header header) {
        if (restValue.children().size() == 0) {
            return new Rest(header);
        }
        
        String restLength = restValue.children().get(0).text();
        if (restLength.isEmpty()) {
            return new Rest(header);
        }
        
        return new Rest(header, restLength);
    }
    
    public static String getLyricLine(List<ParseTree<MusicGrammar>> lyricElementValues) {
        String lyricLine = ""; 
        for (ParseTree<MusicGrammar> lyricElement: lyricElementValues) {
            switch (lyricElement.text()) {
            case " ":
                lyricLine += ' ';
                break; 
            case "-": 
                break; 
            case "_": 
                break; 
            case "*": 
                break;
            case "~": 
                lyricLine += ' ';
                break; 
            case "\\-": 
                lyricLine += '-';
                break; 
            case "|": 
                break; 
            default: 
                // lyric text
                lyricLine += lyricElement.text();
               
            }
        }
        return lyricLine; 
    }
    
    /**
     * Parses the music portion of the abc file.
     * 
     * @param abcBodyType The recursive call for the specific portion of the abc file
     * @param header Header info about the entire piece
     * @return A recursive Music Object represnting the elements within the entire abc body type
     */
    public static Music makeAbstractSyntaxTree(final ParseTree<MusicGrammar> abcBodyType, Header header) {
        List<ParseTree<MusicGrammar>> abcLineList = abcBodyType.children();
        Map<String, List<Music>> voiceToMusic = new HashMap<>();
        
        //initialize the first if not found, the music in general, repeat, and barline accidentals
        String voice = "unknown"; 
        Music music = new Rest(header, "0");
        
        //initialize maps
        Map<String, Music> voiceToMusicRepeat = new HashMap<>(); 
        Map<String, Boolean> voiceToFirstRepeat = new HashMap<>();
        
        
        voiceToMusicRepeat.put(voice, new Rest(header, "0"));
        voiceToFirstRepeat.put(voice, false);
        
        // format: (string) note -> (string) accidental 
        Map<String, String> measureAccidentals = new HashMap<>();

        // loop through every abcLine
        for (ParseTree<MusicGrammar> abcLineType : abcLineList) {
            // parse line of music and append it to voice to Music map
            Music musicLine = new Rest(header, "0");
            List<ParseTree<MusicGrammar>> abcLineValueList = abcLineType.children();
            
            for (ParseTree<MusicGrammar> abcLineValue : abcLineValueList) {
                switch (abcLineValue.name()) {
                    case ELEMENT: //element ::= noteElement | restElement | tupletElement | barline | nthRepeat | spaceOrTab;
                        ParseTree<MusicGrammar> elementValue = abcLineValue.children().get(0);
                        switch (elementValue.name()) {
                            case NOTEELEMENT: //noteElement ::= note | chord;
                                ParseTree<MusicGrammar> noteElementValue = elementValue.children().get(0);
                                switch (noteElementValue.name()) {
                                    case NOTE: //note ::= pitch noteLength?;
                                        musicLine = new Concat(musicLine, makeNote(noteElementValue, header, measureAccidentals), header);
                                        if (!voiceToFirstRepeat.get(voice)) {
                                            Music musicRepeat = voiceToMusicRepeat.get(voice);
                                            musicRepeat = new Concat(musicRepeat, makeNote(noteElementValue, header, measureAccidentals),header);
                                            voiceToMusicRepeat.put(voice, musicRepeat);
                                        }
                                        break;
                                    case CHORD: //chord ::= "[" note (" "* note)* "]";
                                        List<ParseTree<MusicGrammar>> noteValueList = noteElementValue.children();
                                        
                                        //initialize chord with the first note
                                        Music chord = makeNote(noteValueList.get(0), header, measureAccidentals);
                                        
                                        for (int i = 1; i < noteValueList.size(); i++) {
                                            ParseTree<MusicGrammar> noteValue = noteValueList.get(i);
                                            chord = new Chord(chord, makeNote(noteValue, header, measureAccidentals),header);
                                        }
                                        
                                        musicLine = new Concat(musicLine, chord, header);
                                        if (!voiceToFirstRepeat.get(voice)) {
                                            Music musicRepeat = voiceToMusicRepeat.get(voice);
                                            musicRepeat = new Concat(musicRepeat, chord, header);
                                            voiceToMusicRepeat.put(voice, musicRepeat);
                                        }
                                        
                                        break; 
                                    }
                                break;
                            case RESTELEMENT: //restElement ::= "z" noteLength?;
                                musicLine = new Concat(musicLine, makeRest(elementValue, header),header);
                                if (!voiceToFirstRepeat.get(voice)) {
                                    Music musicRepeat = voiceToMusicRepeat.get(voice);
                                    musicRepeat = new Concat(musicRepeat, makeRest(elementValue, header), header);
                                    voiceToMusicRepeat.put(voice, musicRepeat);
                                }
                                break;
                            case TUPLETELEMENT: //tupletElement ::= tupletSpec noteElement+;
                                // get the specs 
                                ParseTree<MusicGrammar> tupletSpecValue = elementValue.children().get(0);      
                                
                                // tupletNum can only be 2,3,4
                                int tupletNum = Integer.parseInt(tupletSpecValue.children().get(0).text());
                                String tupletString = "1";
                                switch (tupletNum) {
                                    case 2:
                                        tupletString = "3/2";
                                        break;
                                    case 3:
                                        tupletString = "2/3";
                                        break;
                                    case 4:
                                        tupletString = "3/4";
                                        break;
                                    default:
                                        System.out.println("tuplet number is not 2,3,4");
                                }
                                
                                // noteElement+
                                for (int i = 1; i < elementValue.children().size(); i++) {
                                    // noteElement
                                    ParseTree<MusicGrammar> elementTupletValue = elementValue.children().get(i);
                                    
                                    // note | chord
                                    ParseTree<MusicGrammar> noteElementTupletValue = elementTupletValue.children().get(0);
                                    switch (noteElementTupletValue.name()) {
                                        case NOTE: //note ::= pitch noteLength?;
                                            musicLine = new Concat(musicLine, makeNote(noteElementTupletValue, header, measureAccidentals, tupletString), header);
                                            if (!voiceToFirstRepeat.get(voice)) {
                                                Music musicRepeat = voiceToMusicRepeat.get(voice);
                                                musicRepeat = new Concat(musicRepeat, makeNote(noteElementTupletValue, header, measureAccidentals),header);
                                                voiceToMusicRepeat.put(voice, musicRepeat);
                                            }
                                            break;
                                        case CHORD: //chord ::= "[" note (" "* note)* "]";
                                            List<ParseTree<MusicGrammar>> noteValueList = noteElementTupletValue.children();
                                            
                                            //initialize chord with the first note
                                            Music chord = makeNote(noteValueList.get(0), header, measureAccidentals, tupletString);
                                            
                                            for (int j = 1; j < noteValueList.size(); j++) {
                                                ParseTree<MusicGrammar> noteValue = noteValueList.get(j);
                                                chord = new Chord(chord, makeNote(noteValue, header, measureAccidentals, tupletString),header);
                                            }
                                            
                                            musicLine = new Concat(musicLine, chord, header);
                                            if (!voiceToFirstRepeat.get(voice)) {
                                                Music musicRepeat = voiceToMusicRepeat.get(voice);
                                                musicRepeat = new Concat(musicRepeat, chord, header);
                                                voiceToMusicRepeat.put(voice, musicRepeat);
                                            }
                                            break; 
                                        default: 
                                            System.out.println("something wrong with noteElementTupletValue");
                                    }
                                }
                                
                                break; 
                            case BARLINE: //barline ::= "|" | "||" | "[|" | "|]" | ":|" | "|:";
                                String barlineType = elementValue.text();
                                switch (barlineType) {
                                    case "|":
                                        // cause accidentals to go away
                                        measureAccidentals.clear();
                                        break;
                                    case ":|":
                                        // repeat everything from entry point
                                        Music musicRepeat = voiceToMusicRepeat.get(voice);
                                        musicLine = new Concat(musicLine, musicRepeat, header);
                                        musicRepeat = new Rest(header, "0");
                                        voiceToMusicRepeat.put(voice, musicRepeat);
                                        break;
                                    case "|:":
                                        // start repeating from when this starts
                                        musicRepeat = new Rest(header, "0");
                                        break;
                                    default: 
                                        System.out.println("something wrong with barlineTYpe");
                                }
                                // end measure
                                break;
                            case NTHREPEAT: //nthRepeat ::= "[1" | "[2";
                                // will entail storing music in an additional data structure
                                String repeatType = elementValue.text();
                                switch (repeatType) {
                                    case "[1": 
                                        voiceToFirstRepeat.put(voice, true);
                                        break;
                                    case "[2":
                                        voiceToFirstRepeat.put(voice, true);
                                        break;
                                    default: 
                                        System.out.println("something wrong with repeatType");
                                }
                                break;
                            case SPACEORTAB: //spaceOrTab ::= " " | "\t";
                                // ignore
                                break;
                            default: 
                                System.out.println("something wrong with elementValue");
                        }
                        break;
                    case LYRIC: //lyric ::= "w:" lyricalElement*;
                        List<ParseTree<MusicGrammar>> lyricElementValues = abcLineValue.children();
                        
                        // string that will be passed into each syllable
                        String lyricLineString = getLyricLine(lyricElementValues);
                        
                        // keep track of syllables in lyric line 
                        LinkedList<Syllable> syllableNoteList = new LinkedList<>();
                        Boolean joinSyllable = false;
                        String previousElement = ""; 
                        int currentIndex = 0;
                        
                        
                        for (ParseTree<MusicGrammar> lyricElementValue : lyricElementValues) {
                            if (lyricElementValue.children().size() == 0) {
                                // has no nonterminal 
                                switch (lyricElementValue.text()) {
                                    case " ":
                                        currentIndex += 1;
                                        previousElement = " ";
                                        break;
                                    case "-": 
                                        if (previousElement == "-"
                                        || previousElement == " ") { 
                                            // hyphen is regarded as a separate syllable
                                            Syllable latestSyllable = syllableNoteList.removeLast();
                                            int latestSyllableBegin = latestSyllable.getBeginIndex();
                                            int latestSyllableEnd = latestSyllable.getEndIndex();
                                            
                                            syllableNoteList.addLast(new Syllable(
                                                    voice,
                                                    lyricLineString,
                                                    latestSyllableBegin,
                                                    latestSyllableEnd));
                                        }
                                        previousElement = "-";
                                        break; 
                                    case "_": 
                                        previousElement = "_";
                                        break;
                                    case "*":  
                                        Syllable latestSyllable = syllableNoteList.removeLast();
                                        int latestSyllableBegin = latestSyllable.getBeginIndex();
                                        int latestSyllableEnd = latestSyllable.getEndIndex();
                                        
                                        syllableNoteList.addLast(new Syllable(
                                                voice,
                                                lyricLineString,
                                                latestSyllableBegin,
                                                latestSyllableEnd));
                                        
                                        previousElement = "*";
                                        break; 
                                    case "~": 
                                        currentIndex += 1;
                                        previousElement = "~";
                                        joinSyllable = true;         
                                        break; 
                                    case "|": 
                                        previousElement = "|";
                                        break; 
                                    default: 
                                        System.out.println("something is wrong with lyricElement");
                                }
                            } else { 
                                ParseTree<MusicGrammar> lyricElementValueChild = lyricElementValue.children().get(0);
                                switch (lyricElementValueChild.name()) {
                                    case BACKSLASHHYPHEN: 
                                        // combines multiple syllables under one note
                                        currentIndex +=1; 
                                        previousElement = "\\-";
                                        joinSyllable = true; 
                                        break; 
                                    case LYRICTEXT:
                                        // append lyricText to syllable
                                        String lyricString = lyricElementValueChild.text();
                                        int beginIndex = currentIndex; 
                                        
                                        if (joinSyllable) {
                                            // add new syllable object and use same begin and end index from previous 
                                            // syllable
                                            
                                            Syllable latestSyllable = syllableNoteList.removeLast();
                                            int latestSyllableBegin = latestSyllable.getBeginIndex();
                                            int latestSyllableEnd = latestSyllable.getEndIndex();
                                            
                                            syllableNoteList.addLast(new Syllable(
                                                    voice,
                                                    lyricLineString,
                                                    latestSyllableBegin,
                                                    latestSyllableEnd));
                         
                                            // turn joinSyllable off 
                                            joinSyllable = false;
                          
                                        } else {       
                                            // regular syllable 
                                            syllableNoteList.add(
                                                    new Syllable(
                                                            voice,
                                                            lyricLineString,
                                                            beginIndex,
                                                            beginIndex + lyricString.length()));
                                        }
                                        
                                        currentIndex += lyricString.length();
                                        previousElement = lyricString; 
                                        break;
                                    default: 
                                        System.out.println("something iw wrong with lyricElementChild");
                                }
                            }
                        }
                        
                        // given the list of lyrics, add them to the music data type 
                        // from left to right
    
                        for (Syllable syllable: syllableNoteList) {
                            musicLine = musicLine.addSyllableToNote(syllable);
                        }
                        
                        break; 
                    case MIDDLEOFBODYFIELD: //middleOfBodyField ::= fieldVoice;
                        ParseTree<MusicGrammar> fieldVoice = abcLineValue.children().get(0);
                        ParseTree<MusicGrammar> text = fieldVoice.children().get(0);
                        voice = text.text();
                        
                        // add repeat for voices
                        voiceToMusicRepeat.put(voice, new Rest(header, "0"));
                        voiceToFirstRepeat.put(voice, false);
                        break;
                    case COMMENT: //comment ::= spaceOrTab* "%" commentText newline;
                        break; 
                    case ENDOFLINE:
                        break;
                    default: 
                        System.out.println("something wrong with abcLine");
                }
            }
            
            // add abcLine to appropriate voice
            if (voiceToMusic.containsKey(voice)) {
                List<Music> voiceMusicLines = voiceToMusic.get(voice);
                voiceMusicLines.add(musicLine);
            } else { 
                List<Music> voiceMusicLines = new ArrayList<>(); 
                voiceMusicLines.add(musicLine);
                voiceToMusic.put(voice, voiceMusicLines);
            }
        }
        
        // give voiceToMusic, concat all the music lines
        Map<String, Music> voiceToSingleMusic = new HashMap<>();
        for (String singleVoice : voiceToMusic.keySet()) {
            Music voiceMusic = new Rest(header, "0");
            for (Music musicLine : voiceToMusic.get(singleVoice)) {
                voiceMusic = new Concat(voiceMusic, musicLine, header);
            }
            voiceToSingleMusic.put(singleVoice, voiceMusic);
        }
        
        // overlay all of the voice
        for (Music musicVoice : voiceToSingleMusic.values()) {
            music = new OverlayVoice(music, musicVoice, header);
        }
                
        return music;    
    }
    
    
    ///////////////////////////////////////
    //Accessors for tests
    ///////////////////////////////////////
    
    /**
     * @return the parser grammar for the music portion of the abc file
     */
    public static Parser<MusicGrammar> getMusicParser(){
        return musicParser;
    }
    
    /**
     * @return the parser grammar for the header portion of the abc file
     */
    public static Parser<HeaderGrammar> getHeaderParser(){
        return headerParser;
    }

}
