//Header
abcheader ::= fieldnumber comment* fieldtitle otherfields* fieldkey music;
fieldnumber ::= "X:"digit endofline;
fieldtitle ::= "T:" text endofline;
otherfields ::= fieldcomposer | fielddefaultlength | fieldmeter | fieldtempo | fieldvoice | comment;
fieldcomposer ::= "C:" text endofline;
fielddefaultlength ::= "L:" notelengthstrict endofline;
fieldmeter ::= "M:" meter endofline;
fieldtempo ::= "Q:" tempo endofline;
fieldvoice ::= "V:" text endofline;
fieldkey ::= "K:" key endofline;
notelengthstrict ::= digit ("/" digit)?;

key ::= keynote modeminor?;
keynote ::= basenote keyaccidental?;
keyaccidental ::= "#" | "b";
modeminor ::= "m";

meter ::= common | half | meterfraction;
meterfraction ::= digit "/" digit;
tempo ::= meterfraction "=" digit;

//General
comment ::= spaceortab* "%" commenttext newline;
commenttext ::= text;
//commenttext should be defined appropriately
endofline ::= spaceortab? (comment)? newline;
music ::= [^$]+;
common ::= 'C';
half ::= "C|";
digit ::= [0-9]+;
newline ::= "\n" | "\r" "\n"?;
spaceortab ::= " " | "\t";
basenote ::= [A-G];
text ::= [a-zA-z 0-9'.,]*;