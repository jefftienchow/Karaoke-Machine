package karaoke.sound;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.mit.eecs.parserlib.ParseTree;
import edu.mit.eecs.parserlib.UnableToParseException;
import karaoke.Music;
import karaoke.parser.KaraokeParser;
import karaoke.parser.KaraokeParser.MusicGrammar;

/**
 * Tests for parsing AST for abc files
 */
public class ParserTest {
    /*
     * Testing strategy for grammar 
     * Partition the input as follows:
     * header with/without L,M,Q,V 
     * L,M,Q,V in different order
     * Meter having C|, C, rational number
     * voice/composer name has special characters/spaces
     * has comments/doesn't have comments
     * music has 0, 1, >1 measures
     * music has 0, 1, >1 repeats
     * music has/doesn't have accidentals
     * music has 1, 2, >2 voices
     * music has/doesn't have lyrics
     * 
     * =========================================================
     * 
     * Testing strategy for parse
     * Partition the input as follows:
     * header with/without L,M,Q,V 
     * L,M,Q,V in different order
     * Meter having C|, C, rational number
     * voice/composer name has special characters/spaces
     * has comments/doesn't have comments
     * voices do not come in order
     * some voices have lyrics/some don't
     * music has 0, 1, >1 measures
     * music has 0, 1, >1 repeats
     * music has repeat on different lines
     * music has/doesn't have accidentals
     * music has 1, 2, >2 voices
     * music has/doesn't have lyrics
     * 
     * 
     */
    
    @Test
    public void testBasicHeader() throws UnableToParseException {
        final String input = "X:1\n" + 
                "T:Alphabet Song\n" + 
                "C:Kid\n" +  
                "K:D\n" + 
                "A\n";
        Music m = KaraokeParser.parse(input);
        assertEquals(m.duration(), 1.0, 0.01);
        //assertEquals(100,m.beatsPerMinute());
    }

    @Test
    public void testHeaderWithRandomSpaces() throws UnableToParseException {
        final String input = "X:1\n" + 
                "T:Alphabet Song\n" + 
                "C:Kid\n" +  
                "K:D\n" + 
                "A\n";
        Music m = KaraokeParser.parse(input);                              
        assertEquals(m.duration(), 1.0, 0.01);
        //assertEquals(100,m.beatsPerMinute());
    }

    @Test
    public void testHeaderWithL() throws UnableToParseException {
        final String input = "X:1\n" + 
                "T:Alphabet Song\n" + 
                "C:Kid\n" +  
                "L:1/4\n" + 
                "K:D\n" + 
                "A\n";
        Music m = KaraokeParser.parse(input);
        assertEquals(m.duration(), 1.0, 0.01);
        //assertEquals(100,m.beatsPerMinute());
    }
    @Test
    public void testHeaderWithM() throws UnableToParseException {
        final String input = "X:1\n" + 
                "T:Alphabet Song\n" + 
                "C:Kid\n" +  
                "M:4/4\n" + 
                "K:D\n" + 
                "A\n";
        Music m = KaraokeParser.parse(input);
        assertEquals(m.duration(), 1.0, 0.01);
        //assertEquals(100,m.beatsPerMinute());
    }
    @Test
    public void testHeaderWithQ() throws UnableToParseException {
        final String input = "X:1\n" + 
                "T:Alphabet Song\n" + 
                "C:Kid\n" +  
                "Q:1/4=200\n" + 
                "K:D\n" + 
                "A\n";
        Music m = KaraokeParser.parse(input);
        assertEquals(m.duration(), 1.0, 0.01);
        //assertEquals(200,m.beatsPerMinute());
    }
    
    @Test
    public void testHeaderWithV() throws UnableToParseException {
        final String input = "X:1\n" + 
                "T:Alphabet Song\n" + 
                "C:Kid\n" +  
                "V:1\n" + 
                "K:D\n" + 
                "V:1\n" +
                "A\n";
        Music m = KaraokeParser.parse(input);
        assertEquals(m.duration(), 1.0, 0.01);
        assertEquals(100,m.beatsPerMinute());
    }
    
    @Test
    public void testHeaderWithVMultiple() throws UnableToParseException {
        final String input = "X:1\n" + 
                "T:Alphabet Song\n" + 
                "C:Kid\n" +  
                "V:1\n" + 
                "V:2\n" + 
                "V:3\n" + 
                "K:D\n" + 
                "V:1\n" +
                "A\n" +
                "V:2\n" +
                "A\n" +
                "V:3\n" +
                "A\n";
        Music m = KaraokeParser.parse(input);
        assertEquals(m.duration(), 1.0, 0.01);
        System.out.println("???"+m.beatsPerMinute());
        //assertEquals(100,m.beatsPerMinute());
    }
    
    @Test
    public void testHeaderQDoubleTempo() throws UnableToParseException {
        final String input = "X:1\n" + 
                "T:Alphabet Song\n" + 
                "C:Traditional Kid's Song\n" + 
                "M:4/4\n" + 
                "L:1/4\n" + 
                "Q:1/2=90\n" + 
                "K:D\n" + 
                "A\n";
        Music m = KaraokeParser.parse(input);
        System.out.println("music: " + m.toString());
        assertEquals(90, m.beatsPerMinute());
    }
    
    @Test
    public void testHeaderQHalfTempo() throws UnableToParseException {
        final String input = "X:1\n" + 
                "T:Alphabet Song\n" + 
                "C:Traditional Kid's Song\n" + 
                "M:4/4\n" + 
                "L:1/4\n" + 
                "Q:1/8=90\n" + 
                "K:D\n" + 
                "A\n";
        Music m = KaraokeParser.parse(input);
        assertEquals(90,m.beatsPerMinute());
    }
    
    @Test
    public void testHeaderCommonTime() throws UnableToParseException {
        final String input = "X:1\n" + 
                "T:Alphabet Song\n" + 
                "C:Traditional Kid's Song\n" + 
                "M:C\n" + 
                "K:D\n" + 
                "A\n";
        Music m = KaraokeParser.parse(input);
    }
    
    @Test
    public void testHeaderCutCommonTime() throws UnableToParseException {
        final String input = "X:1\n" + 
                "T:Alphabet Song\n" + 
                "C:Traditional Kid's Song\n" + 
                "M:C|\n" + 
                "K:D\n" + 
                "A\n";
        Music m = KaraokeParser.parse(input);
    }
    
    /****************************
     * TEST MUSIC PORTION
     ***************************/
    
    @Test
    public void testMusicSingleNote() throws UnableToParseException {
        final String input = "C\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testMusicSingleNoteAccidentalFlat() throws UnableToParseException {
        final String input = "_C\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testMusicSingleNoteAccidentalSharp() throws UnableToParseException {
        final String input = "^C\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testMusicSingleNoteAccidentalNatural() throws UnableToParseException {
        final String input = "=C\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testMusicSingleNoteAccidentalDoubleFlat() throws UnableToParseException {
        final String input = "__C\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testMusicSingleNoteAccidentalDoubleSharp() throws UnableToParseException {
        final String input = "^^C\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testMusicTwoNotes() throws UnableToParseException {
        final String input = "C D\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testMusicTwoNotesWhitespace()throws UnableToParseException {
        final String input = "C    D     \n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testMusicSingleNoteLengthBasic() throws UnableToParseException {
        final String input = "C2\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testMusicSingleNoteLengthLong() throws UnableToParseException {
        final String input = "C40\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testMusicSingleNoteLengthFraction() throws UnableToParseException {
        final String input = "C1/2\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testMusicSingleNoteLengthFractionNoNumerator() throws UnableToParseException {
        final String input = "C/2\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testMusicSingleNoteLengthFractionNoDenominator() throws UnableToParseException {
        final String input = "C3/\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testMusicSingleNoteLengthFractionSlashOnly() throws UnableToParseException {
        final String input = "C/\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testMusicRestBasic() throws UnableToParseException {
        final String input = "z/\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testMusicRestMultiple() throws UnableToParseException {
        final String input = "zzzz/\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testMusicRestLengthBasic() throws UnableToParseException {
        final String input = "z2\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testMusicRestLengthLong() throws UnableToParseException {
        final String input = "z40\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testMusicRestLengthFraction() throws UnableToParseException {
        final String input = "z1/2\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testMusicRestLengthFractionNoNumerator() throws UnableToParseException {
        final String input = "z/2\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testMusicRestLengthFractionNoDenominator() throws UnableToParseException {
        final String input = "z3/\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testMusicRestLengthFractionSlashOnly() throws UnableToParseException {
        final String input = "z/\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testChordBasic() throws UnableToParseException {
        final String input = "[ABC]\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testNotesTogether() throws UnableToParseException {
        final String input = "ABC\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testChordLengths() throws UnableToParseException {
        final String input = "[A1B2C3]\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testChordSomeLengths() throws UnableToParseException {
        final String input = "[AB2C3]\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testChordMoreLengths() throws UnableToParseException {
        final String input = "[A/B/2C3/]\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testSingleNoteChord() throws UnableToParseException {
        final String input = "[C]\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testAccidentalChord() throws UnableToParseException {
        final String input = "[_CC^C]\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testTwoChordsBasic() throws UnableToParseException {
        final String input = "[CD] [EF]\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testOctave1() throws UnableToParseException {
        final String input = "c\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testOctave2() throws UnableToParseException {
        final String input = "c'\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testOctave3() throws UnableToParseException {
        final String input = "C,\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testOctavesTogether() throws UnableToParseException {
        final String input = "C,Ccc'\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testAccidentalOctive() throws UnableToParseException {
        final String input = "_C,\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testAccidentalOctive2() throws UnableToParseException {
        final String input = "^c'\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testDupletBasic() throws UnableToParseException {
        final String input = "(2GA\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testTripletBasic() throws UnableToParseException {
        final String input = "(3GAB\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }

    @Test
    public void testQuadrupletBasic() throws UnableToParseException {
        final String input = "(4GABC\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testTripletDifferentLengths() throws UnableToParseException {
        final String input = "(3GA2B3\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testTripletChordBasic() throws UnableToParseException {
        final String input = "(3[ABC][BCD][CDE]\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testTripletChordDifferentLengths() throws UnableToParseException {
        final String input = "(3[A1B1C1][B2C2D2][C3D3E3]\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testDupletChordBasic() throws UnableToParseException {
        final String input = "(2[ABC][CDE]\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testDupletChordDifferentLengths() throws UnableToParseException {
        final String input = "(2[A1B1C1][B2C2D2]\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testQuadrupletChordBasic() throws UnableToParseException {
        final String input = "(4[ABC][BCD][CDE][EFG]\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testQuadrupletChordDifferentLengths() throws UnableToParseException {
        final String input = "(4[A1B1C1][B2C2D2][C3D3E3][B2C2D2]\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testBarsBasic() throws UnableToParseException {
        final String input = "ABCD|ABCD\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testBarsSpaced() throws UnableToParseException {
        final String input = "A B C D | A B C D\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testBarsSpaaaaced() throws UnableToParseException {
        final String input = "A   B   CD   | AB C    D\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testRepeatBasic() throws UnableToParseException {
        final String input = "|: C :|\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testRepeatWithBar() throws UnableToParseException {
        final String input = "|: C D E F | G A B c :|\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testAlternateEnding() throws UnableToParseException {
        final String input = "|: C D E F |[1 G A B c | G A B B :|[2 F E D C |\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testSingleVoiceBasic() throws UnableToParseException {
        final String input = "V:one\n"+
                             "C\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testMultiVoiceBasic() throws UnableToParseException {
        final String input = "V:one\n"+
                             "A\n"+
                             "V:two\n"+
                             "B\n"+
                             "V:three\n"+
                             "C\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testSingleVoiceRecurring() throws UnableToParseException {
        final String input = "V:one\n"+
                             "A\n"+
                             "V:one\n"+
                             "B\n"+
                             "V:one\n"+
                             "C\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testMultiVoiceRecurring() throws UnableToParseException {
        final String input = "V:one\n"+
                             "A\n"+
                             "V:two\n"+
                             "B\n"+
                             "V:three\n"+
                             "C\n"+
                             "V:one\n"+
                             "a\n"+
                             "V:two\n"+
                             "b\n"+
                             "V:three\n"+
                             "c\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    //TEST COMMENTS
    public void testCommentBasic() throws UnableToParseException {
        final String input = "C D E %comment";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    public void testCommentEmpty() throws UnableToParseException {
        final String input = "C D E %";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    @Test
    public void testCommentSingleLine() throws UnableToParseException {
        final String input = 
                "V:one\n"+
                "A\n"+
                "%\n"+
                "V:one\n"+
                "B\n";
        final ParseTree<MusicGrammar> parseTree = KaraokeParser.getMusicParser().parse(input);
    }
    
    //EXAMPLES FROM FILES
    
    @Test
    public void testABCSong() throws UnableToParseException {
        final String input = "X:1\n" + 
                "T:Alphabet Song\n" + 
                "C:Traditional Kid's Song\n" + 
                "M:4/4\n" + 
                "L:1/4\n" + 
                "Q:1/4=100\n" + 
                "K:D\n" + 
                "| D D A A|B B A2|G G F F|E/2E/2E/2E/2 D2|\n" + 
                "w:A B C D E F G  H I J K L  M  N  O   P\n" + 
                "   D   D    A    A |B B A2 | G    G  F    F | E     E  D2|\n" + 
                "w: Now I've said my A B C's. Tell me what you think of me.\n";
        Music m = KaraokeParser.parse(input);
    }
    
    @Test
    public void testFurElise() throws UnableToParseException {
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
        Music m = KaraokeParser.parse(input);
    }
    
    @Test
    public void testPaddy() throws UnableToParseException {
        final String input = "X:1\n" + 
                "T:Paddy O'Rafferty\n" + 
                "C:Trad.\n" + 
                "M:6/8\n" + 
                "Q:1/8=200\n" + 
                "K:D\n" + 
                "dff cee|def gfe|dff cee|dfe dBA|\n" + 
                "dff cee|def gfe|faf gfe|[1 dfe dBA:|[2 dfe dcB|]\n" + 
                "A3 B3|gfe fdB|AFA B2c|dfe dcB|\n" + 
                "A3 B3|efe efg|faf gfe|[1 dfe dcB:|[2 dfe dBA|]\n" + 
                "fAA eAA| def gfe|fAA eAA|dfe dBA|\n" + 
                "fAA eAA| def gfe|faf gfe|dfe dBA:|\n";
        Music m = KaraokeParser.parse(input);
    }
    
    @Test
    public void testPiece1() throws UnableToParseException {
        final String input = "X:1\n" + 
                "T:Piece No.1\n" + 
                "M:4/4\n" + 
                "L:1/4\n" + 
                "Q:1/4=140\n" + 
                "K:C\n" + 
                "C C C3/4 D/4 E | E3/4 D/4 E3/4 F/4 G2 | (3C'C'C' (3GGG (3EEE (3CCC G3/4 F/4 E3/4 D/4 C2 |]\n";
        Music m = KaraokeParser.parse(input);
    }
    
    @Test
    public void testPiece2() throws UnableToParseException {
        final String input = "X:1\n" + 
                "T:Piece No.2\n" + 
                "M:4/4\n" + 
                "L:1/4\n" + 
                "Q:1/4=200\n" + 
                "K:C\n" + 
                "[^F/2e/2] [F/2e/2] z/2 [F/2e/2] z/2 [F/2c/2] [Fe] | [Gbg] z G z | "+
                "c3/2 G/2 z E | E/2 a b _b/2 a | (3Geg a' f/2 g/2 | z/2 e c/2 d/2 b3/4 ||\n";
        Music m = KaraokeParser.parse(input);
    }
    
    public void testPiece3() throws UnableToParseException {
        final String input = "X:1\n" + 
                "T:Piece No. 3\n" + 
                "M:3/4\n" + 
                "L:1/8\n" + 
                "Q:1/8=100\n" + 
                "K:C\n" + 
                "z2 D | G2 B/2 G/2 | B2 A | G2 E | D2 D | G2 B/2 G/2 | B2 A | d3\n" + 
                "w: * A-ma-zing_ grace! How sweet the sound That saved a_ wretch like me.\n";
        Music m = KaraokeParser.parse(input);
    }
    
    @Test
    public void testSimpleScale() throws UnableToParseException {
        final String input = "X:1\n" + 
                "T:Simple scale\n" + 
                "C:Unknown\n" + 
                "M:4/4\n" + 
                "L:1/4\n" + 
                "Q:1/4=120\n" + 
                "K:C\n" + 
                "C D E F | G A B c | c B A G | F E D C |\n";
        Music m = KaraokeParser.parse(input);
    }
    
    
}
