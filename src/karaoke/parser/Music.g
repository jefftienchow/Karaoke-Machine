// Music
abcBody ::= abcLine+;
abcLine ::= element+ endOfLine (lyric endOfLine)?  | middleOfBodyField endOfLine | comment;
element ::= noteElement | restElement | tupletElement | barline | nthRepeat | spaceOrTab; 

// notes
noteElement ::= note | chord;

note ::= pitch noteLength?;
pitch ::= accidental? basenote octave?;
octave ::= "'"+ | ","+;
noteLength ::= (digit)? ("/" (digit)?)?;
noteLengthStrict ::= digit "/" digit;

// "^" is sharp, "_" is flat, and "=" is neutral
accidental ::= "^" | "^^" | "_" | "__" | "=";

basenote ::= "C" | "D" | "E" | "F" | "G" | "A" | "B"
        | "c" | "d" | "e" | "f" | "g" | "a" | "b";

// rests
restElement ::= "z" noteLength?;

// tuplets
tupletElement ::= tupletSpec noteElement+;
tupletSpec ::= "(" digit;

// chords
chord ::= "[" note (" "* note)* "]";

barline ::= "|" | "||" | "[|" | "|]" | ":|" | "|:";
nthRepeat ::= "[1" | "[2";

// A voice field might reappear in the middle of a piece 
// to indicate the change of a voice
middleOfBodyField ::= fieldVoice;
fieldVoice ::= "V:" text; 

lyric ::= "w:" lyricalElement*;
lyricalElement ::= " "+ | "-" | "_" | "*" | "~" | backslashHyphen | "|" | lyricText;
lyricText ::= text;

backslashHyphen ::= "\\" "-";
// backslash immediately followed by hyphen


// General
comment ::= spaceOrTab* "%" commentText newline;
commentText ::= text;

endOfLine ::= comment | newline;

text ::= [A-Za-z0-9'?.!,]*;
digit ::= [0-9]+;
newline ::= "\n" | "\r" "\n"?;
spaceOrTab ::= " " | "\t";