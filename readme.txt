Read me

How-to use the programs
=======================

The purpose of this is collection of simple java programs is to make it easier for anyone wanting to add new words to an Apertium language pair, e.g. Svenska - Danska [Swedish - Danish]  (sv-da)

The main advantage is that you don't have to write xml-code and making trivial misstakes, not at all related to languages or translation. In additiion, the programs facilitates by automatic control of lots of things and by presenting alternativs e.g. when you are about to choose a paradigm.

Use these programs to propose additions to the official wordlists. Send them to the appropriate language developer, hwo will add the words to the official wordlist. 
Or do it yourself, if you have become developer for the language.

Presumption:
It is very difficult and requires adding of  tens of thousands of new words to cover all kinds of texts. When you have a total of about
50 - 60 000 words in the word lists, you might reach a couverage of about 80 % when translating .

It's much easier to get a higher couverage, even over
90 %, for a subset of texts, let's call it a domain.
If you are interested in some particular subject, 
it might be earthworms, birch-bark work, philosophy, lace-maiking, nursery school pedagogy, or something else, 
you can add words in that field and rapidly get a working translation help.

The guiding principle is to first add the most common words.  This will improve the translation rapidly. Hency, you start by making a frequency list out of a corpus, that is a collection of texts. Then you add the words in order of frequency: the most frequent first.

Do like this:

1. Install the programs by unpacking the to a folder you have write permissions for. For instance somewhere in "My documents" or under your username.
You run the programs by opening the command interpreter ("the DOS-window"). move to the folder with the program and write "java" followed by the name of the program, e.g. :
java OrdFrekvens

2. Included with the programs are the official wordlists:
sv-da.da.dix
sv-da.sv-da.dix
sv-da.sv.dix

They would most likely need to be updated. Download the latest from Apertium.
See the homepage of Apertium: www.apertium.org
You will  find released language pairs under apertium-trunk.
Then copy the latest version to the folder where you have anpacked AddToDix, overwriting the old versions.

3. Translate FROM the language you know least TO the language you know best (e.g. your native language. Below, I will call the language you translate from "the source language" and the language you translate to "the target language". 

4. Begin by collecting as many texts as possible (written in the source language) in the domain you would like to work with. Copy them to a text file and save it in the same folder as the programs. (Use for instance the program Notepad: Start - All programs - Windows Accessories - Notepad
Do you have files with suitable texts? In that case you may copy text files, rtf-files and old Word files (.doc) to a folder and then run the program CollectText to assemble them in a textfile. The file will get the same name as the folder and the file extension ".txt".

5. Run the program OrdFrekvens.java to get a list of all words
 in the text file, listed in the order of frequency i.e. the most common words first.
Words already present in the wordlists should be largely removed, the same applies to garbage characters. The list will be named "Frekvens. + YourTextFile"
and will be found in the same folder as the programs.

6. You should now add the words in the order of frequency. By adding the most common words first, you will soon benefit from your work.

7. Begin by adding words to the monolingual wordlist for the source language with the program AddToDictionnary.java
The program will check if the words are already present in the dictionnary, to make shure you don't do any unnecessary work. (You won't need to manually seek in the wordlists for the word you plan to add - the program does it for you!)
Save the file when you have finished.

8. Then add the translation of the word in the bilingual wordlist
with the program AddToBidixFromMonodix.java The program will read the words from the monolingual dictionary you just created, thus you only have to add information on the translation etc. Save the file when your are finished.

9. Finally, add words to the monolingual dictionary for the target language with the progran AddToDictionaryFromBidix.java The program will read the words from the bilingual dictionary you just created, thus you just have to add information about e.g. the paradigm.
Save the file.

10. When you have finshed, please send the three files to the developer for each language, preferably  kompressed into a zip-file. The developer will check your files and maybe makes a few changes before he adds the new words to the dictionaries of the Apertium language pair.

11. The files are named:

Word frequency
-----------
Frekvens.YourTextFile.txt (t.ex. Frekvens.DanishTowns.txt) Please, do NOT send this file!

Dictionaries (Please, send these!)
-------------------------

languagePair.language1.dix.txt (t.ex. sv-da.da.dix.txt)

languagePair.languagePair.dix.txt (t.ex. sv-da.sv-da.dix.txt)

languagePair.language2.dix.txt(t.ex. sv-da.sv.dix.txt)