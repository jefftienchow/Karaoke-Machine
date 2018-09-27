package karaoke;

import java.util.List;

import karaoke.sound.SequencePlayer;

/**
 * Immutable representation of lyric syllables
 */
public class Syllable {

    // Abstraction Function:
    //  AF(voice, lyricLine, begin, end): represents a syllable linked to a voice, in which it's text 
    //  can be indexed through lyricLine.substring(begin, end-1)
    // Rep Invariant:
    //  begin <= end
    // Safety from rep exposure:
    //  all fields are final, private, and immutable
    // Thread Safety argument: 
    //  This class is threadsafe is because it's immutable: 
    //  lyricLine, begin, end, voice are private & final 
    
    private final String lyricLine;
    private final int begin;
    private final int end;
    private final String voice;

    
    public Syllable(String voice, String lyricLine, int begin, int end) { 
        this.voice = voice;  
        this.lyricLine = lyricLine; 
        this.begin = begin;
        this.end = end;
    }
    
    // this is the lyric line passed from the parser
    public String getLyricLine() { 
        checkRep();
        return lyricLine; 
    }
    
    public int getBeginIndex() { 
        checkRep();
        return begin;
    }
    
    public int getEndIndex() { 
        checkRep();
        return end; 
    }
    
    public boolean isSkipped() {
        checkRep();
        if (begin==0 && end==0) {
            return true;
        }
        return false;
    }
    
    public String getVoice() {
        checkRep();
        return voice;
    }
    
    public String getLine() {
        checkRep();
        String line = "";
        for (int i=0; i<lyricLine.length(); i++) {
            if (i==begin) {
                line += "*";
            }
            line += lyricLine.charAt(i);
            if (i==end-1) {
                line += "*";
            }
        }
        return line;        
    }
    
    private void checkRep() { 
        assert(this.begin <= this.end);
    }
    
    private boolean sameValue(Syllable that) {
        return that.getLyricLine() == this.getLyricLine()
                && that.getBeginIndex() == this.getBeginIndex()
                && that.getEndIndex() == this.getEndIndex()
                && that.getVoice() == this.getVoice();
    }
    
    @Override
    public boolean equals(Object that) { 
        checkRep();
        return that instanceof Syllable && sameValue((Syllable) that); 
    }
    
    @Override
    public int hashCode() { 
        checkRep();
        return this.toString().hashCode();
    }
    
    @Override
    public String toString() {
        checkRep();
        return "Syllable("
                + this.begin
                + ", "
                + this.end
                + this.lyricLine
                + this.voice;
    }
    
}
