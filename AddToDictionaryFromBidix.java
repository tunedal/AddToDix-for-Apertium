//AddToDictionaryFromBidix.java

// ADD TO DICTIONARY FROM BIDIX: 

// Skapa enspråkig ordlista för Apertium
// Maskinöversättning
// www.apertium.org
// ====================================== 

// Version: 0.98

//      Copyright (c) 2012-2013 Per Tunedal, Stockholm, Sweden
//       Author: Per Tunedal <info@tunedal.nu>

//       This program is free software: you can redistribute it and/or modify
//       it under the terms of the GNU General Public License as published by
//       the Free Software Foundation, either version 3 of the License, or
//       (at your option) any later version.

//       This program is distributed in the hope that it will be useful,
//       but WITHOUT ANY WARRANTY; without even the implied warranty of
//       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//       GNU General Public License for more details.

//       You should have received a copy of the GNU General Public License
//       along with this program.  If not, see <http://www.gnu.org/licenses/>.


// Notes
// =====

// v. 0.2 Comments etc mainly in Swedish.
//			Removed direction (not used any more for lexical selection)
//			Added some hints to the user.
//			Stem suggested from the lemma.
// 			File names displayed after saving for easy retrieval.
//			Stop list with existing words of the actual type, e.g. nouns.
// v. 0.3 User has to admit GPL license for the dix.
// v. 0.4 More helpful questions.
// v. 0.5 Better algo for finding lemma.
//			Bugfix: correction of a word

// v. 0.6 Don't ask for paradigm when adding interjections and abbreviations
//			Encourgement: number of added words displayed.
//			Nicer output.
//
// v. 0.7
// v. 0.8 Doublet words in input are discarded.
// v. 0.9 
// v. 0.95
// v. 0.96 Lists the most frequent paradigms
//			Checks input for paradigms
//			Possibility to quit after each 5 added words.
//			Fixed bugs:
//			List of words to translate from the correct language.
// v. 0.97 List of words to translate now only contains new words.
//			The number of remaining words is displayed after each 5 treated.
//			Unused large variables are nulled.
//			Nicer output. Fixed bug: now stops nicely after last entry.
// v. 0.98 Quicker and more intuitive to change stem.
//			Fixed bugs:
//			Stops nicely if all words in bidix already are present in monodix.
//			Algo for finding lemmas now resistant to extra spaces in monodix.
//			Doesn't try to create a list of paradigms for abbr and ij.

// -----------------------------------------------------------------------

// Skapa enspråkig ordlista för Apertium
// Maskinöversättning
// www.apertium.org
// ====================================== 

// Author: Per Tunedal 2012

// Demonstrerar:
// Flera metoder i ett program
// Använder metoder från flera standardklasser
// 
// Använder metoder från egna klasser för inmatning
// och utskrift med korrekta svenska tecken.

// Inmatningskontroll

// Läsning från en textfil
// Skrivning till en textfil

import java.util.*; // ArrayList, Scanner
import java.io.*; // för att skapa/öppna en fil (File)
import per.edu.*;

class AddToDictionaryFromBidix

{
	
	public static void main (String[] args) throws Exception
	
	{
	// Inmatning av namn
	// =================

	String namn = "";
		
	while (true)
		{
		namn = Inmatning.rad("Ditt namn:");	
		if (namn.length() != 0) break;
		}

	// Inmatning av signatur
	// =====================

	String author = "";
		
	while (true)
		{
		author = Inmatning.rad("Din signatur:");	
		if (author.length() != 0) break;
		}
		
	// Inmatning av e-post
	// ===================

	String epost = "";
		
	while (true)
		{
		epost = Inmatning.rad("Din e-postadress:");	
		if (epost.length() != 0) break;
		}		
			
	// Tillåta att dix licensieras under GPL	
	String gpl = "";
	String allowed = "ja|JA|Ja|nej|NEJ|Nej"; // Tillåtna svar

	Utskrift.rubrik ("Apertium inkl. ordlistorna är licensierat enl. GPL v.2");
	Utskrift.skrivText("Du måste tillåta att den ordlista du bidrar med licensieras enl. GPL version 2 eller senare,\n" 
	+ "för att ditt bidrag ska komma till nytta. https://www.gnu.org/licenses/old-licenses/gpl-2.0.html\n");
	while (true)
		{
		gpl = Inmatning.rad("Ja, jag tillåter att ordlistan licensieras enl. GPL version 2 eller senare (JA/NEJ):", allowed);	
		if (gpl.length() != 0) break;
		}
	if (gpl.matches("nej|NEJ|Nej")) System.exit(0); // Quit the program					
			
	// Inmatning av språkpar
	// =====================
		
	// tillåten input
	allowed = "sv-da|sv-nb";
	String pair = "";
		
	while (true)
		{
		pair = Inmatning.rad("Språkpar (tex sv-da):", allowed);		
		if (pair.length() != 0) break;
		}
		
	// Val av språk (monodix)
	// ======================
	// tillåten input
	allowed = pair;
	allowed = allowed.replaceAll ("-", "|");

	String lang = "";
	
	Utskrift.rubrik("Ordlistan du ska skapa");
	
	while (true)
		{
		lang = Inmatning.rad("Ordlistans språk (tex sv):", allowed);		
		if (lang.length() != 0) break;
		}
		
	// Typ av ord
	// ==========
	// Lingvistisk kategori
	// dvs ordklass
	
	// Skapar lista på ordklasser
	TreeMap<String, String> tmKlass = partOfSpeach ();
	
	String klass = lasKlass (pair);
	Utskrift.skrivText("Orden hämtade från den tvåspråkiga ordlistan tillhör ordklass: " + tmKlass.get(klass) + " ("+ klass + ")\n");	
	
	// Lista på ord som redan finns
	Utskrift.skrivText("Vänta - göra en lista på ord som redan finns");
	String[] stop = OrdSomFinns (pair, lang, klass);
	
	//Utskrift.skrivVektor (stop);
	
	String mall = "";
	String topParadigms = "";
	
	// Interjektioner och förkortningar oböjliga i sv-da
	if (klass.equals("ij") || klass.equals("abbr"))
	{}
	else
	{
		// Gör en paradigm-lista för vald ordklass
		// =======================================
		
		Utskrift.skrivText("Vänta - göra en lista på paradigm (böjningsmönster)");
		//ArrayList<String> paradigmer = new ArrayList<String>(); // används ej??
			
		// Lista ord som karakteriserar resp. paradigm
		// dvs mönster/mall för böjningen

		mall = paradigmOrd(pair, lang, klass);
		
		// Läser statisk frekvenslista på paradigm
		//String topParadigms = Textfil.laesText ("paradigms-da-nouns.txt", "utf-8", 6);
		topParadigms = Textfil.laesText ("paradigms-" + lang + "-" + tmKlass.get(klass) + ".txt", "utf-8", 10);
		topParadigms = topParadigms.replaceAll("\n"," |");
		//Utskrift.skrivText("TopParadigms: " + topParadigms);
	}
	
	// Beräkna språkets position
	// =========================
	// dvs. är språket till vänster (L)
	// eller till höger (R)
	// Behövs för att läsa från bidix
		
	String pos = languagePosition (lang, pair);
	
	// Läser in lemma från bidix
	// ---------------------------
	Utskrift.skrivText("Vänta - göra en lista på ord att beskriva");
	// Listan rensad från ord som redan finns i monodix.
	ArrayList<String> WordsToDefine = lasBidix (pair, lang, pos, klass, stop);
	
	//Utskrift.rubrik ("Antal ord att beskriva: " + WordsToDefine.size());
	int ant = WordsToDefine.size();
	Utskrift.rubrik ("Antal ord att beskriva: " + ant);
	if (ant == 0) System.exit(0);
		
	
			
	// Samla in ordbeskrivningarna
	// ===========================
		
	// Sparas i en ArrayList
	// så att det är lätt att lägga till rader.
		
	ArrayList<String> alDix = new ArrayList<String>();
		
		
	// Inmatning av ord
	// ================
	String rad = "";
	int added = 0; // för uppmuntran mm
	
	// Jfr AddToBidixFromMonodix.java : elegantare lösning!
	// "added" används ist.f. check-vektor!
	// Går ej här pga koll mot befintliga ord :-(
	// Jo, det går om listan kollas först:
	// innehåller då bara ord som inte finns!
	
	//int[] check = {0}; // antalet genomgångna ord
	// Lägg till en rad (sträng) i taget
	// avsluta med ENTER
	Utskrift.rubrik ("Nytt ord (klass: " + klass + " språk: " + lang + ")");
	while (true && added < WordsToDefine.size())
		{
		// mall: paradigm-stam, klass: ordklass
		// stop: ord som finns: redan kollat!
		//rad = defineWord(WordsToDefine, author, mall, klass, stop, check, topParadigms);
		//rad = defineWord(added, WordsToDefine, author, mall, klass, check, topParadigms);
		rad = defineWord(added, WordsToDefine, author, mall, klass, topParadigms);
		if (rad == "") break; // avsluta med ENTER
				
		// Skriv ut raden och ge möjlighet att ändra
		Utskrift.rubrik ("Nya ordet");
		Utskrift.skrivText (rad);
		String change = Inmatning.rad("Ändra (j/N) ?");

		if (change.equals("") || !(change.charAt(0)=='j'))
			{
				alDix.add (rad);
				added++; //räknar antalet tillagda ord
				Utskrift.skrivText("Antal ord: " + added); // Uppmuntran!
				Utskrift.rubrik("Nästa ord");
				
				// gör backup för var 10:e rad
				//if (0 == Math.round(alDix.size()%10))
				if (0 == Math.round(added%10))
				{
					String filNamn = pair + "." + lang + "." + "dix.txt.back";
					dixBackup (alDix, filNamn); // Backup till fil
				}
				
				// Möjlighet att avsluta efter var 5:e ord.
				if (0 == Math.round(added%5))
				{	
					Utskrift.skrivText("Du har matat in " + added + " ord. Återstår: " + (WordsToDefine.size() - added));
					allowed = "j|J|ja|JA|Ja|n|N|nej|NEJ|Nej|^$"; // Tillåtna svar (^$ = tomt)
					String sluta = Inmatning.rad("Sluta (j/N)?", allowed);	
					if (!sluta.equals("") && sluta.charAt(0)=='j') break;
				}
			}
		else 
			{
				Utskrift.skrivText("Mata in ordet på nytt!");
				//check[0] = check[0] - 1; // ordets index
				//added dvs. ordets index i WordsToDefine oförändrat.
			}
		}
		
	// Skriv ut hela listan med radnr
	// ==============================
	String raden = "";
	
	//Utskrift.skrivText ("Antal inmatade ord: " + alDix.size());
	
	for (int i=0; i<alDix.size(); i++)
	
	{
		// Första raden har nr 1, men index 0!
		// ===================================
		raden = Integer.toString(i+1) + " " + (String) alDix.get(i);
		Utskrift.skrivText (raden);
	}
	// Möjlighet att ändra viss rad.
	// Avsluta med 0
	int r = 0;
	
	while (true)
	{
		Utskrift.rubrik ("Vill du ändra någon rad (1, 2, 3 ...) ?");
		r = Inmatning.heltal ("Ange radnummer (avsluta med 0):", 1 + alDix.size()); //maxvärde!
		
		if (r == 0) break;
		else
		{			
			r--; // r ändras till korrekt index för raden
			andraRad(added, WordsToDefine, alDix, r, author, mall, klass, topParadigms); // ändras på plats
		}
	}
	
	// Formatera listan dvs. lägg till HTML-taggar
	// ===========================================
	
	String monodix = "";
	String line = "====================================================" + "\n";
	monodix = monodix + "Monolingual dictionnary for " + pair + ", language: " + lang + "\n";
	monodix = monodix + "Part of speach: " + tmKlass.get(klass) + " (" + klass + ")\n" + line + "\n";
	
	monodix = monodix + makeXMLmonodix (alDix);
	
	String lic = "Jag tillåter att ordlistan licensieras enl. GPL version 2 eller senare.";
	monodix = monodix + "\n" + lic + "\n" + namn + "\n" + epost; //avsändare	


	if (monodix.length() != 0) Utskrift.skrivText ("\n" + monodix + "\n");
	else Utskrift.skrivText ("Inget inmatat");
	
	
	// Fråga om spara till fil
	// =======================
	
	// regex ^$ betyder tom sträng
	allowed = "j|J|ja|JA|Ja|n|N|nej|NEJ|Nej|^$"; // Tillåtna svar
	String save = Inmatning.rad("Spara (J/n)?", allowed);	
	
	if (save.equals("") || save.matches("j|J|ja|JA|Ja"))
		{
		Utskrift.skrivText ("Vänta! Sparar ordlistan.");
		// Texten redan klar!
		// Här anrop av metod för att skriva till fil.
		String filnamn = pair + "." + lang + "." + "dix.txt";
		//String lic = "Jag tillåter att ordlistan licensieras enl. GPL version 2 eller senare.";
		//monodix = monodix + "\n" + lic + "\n" + namn + "\n" + epost; //avsändare
		Textfil.skrivText (filnamn, monodix, "UTF-8");
		Utskrift.rubrik ("Sparat ordlistan " +  filnamn);
		
		}
	else Utskrift.rubrik ("Sparar ingenting");	
	}
	
	// Konstruerar en lista över tillgängliga
	// paradigmer (böjningsmönster) för vald ordklass.		
	// Listar orden som är mönster/mall för böjningen

	public static String paradigmOrd (String pair, String lang, String klass) throws Exception
	
	{
	
		String p = "";
	
		// Konstruerar en lista över tillgängliga
		// paradigmer utifrån språkets monodix!
		// =======================================
		
		String fil = "apertium-" + pair + "." + lang + ".dix";
		
		FileInputStream fs = new FileInputStream(fil); // byte till character
		Scanner fin = new Scanner (fs, "UTF-8"); // rätt tecken!

		String rad= "";
		int i = 0;
		int j = 0;
	
		while (true)
	
			{	

			rad = fin.nextLine();

			if (rad.contains("<e lm=")) // Word definitions: too far down
				break;
			if (!rad.contains("<pardef") || !rad.contains("__" + klass + "\""))
				continue;
			else
				{
				// Modellordet i paradigmet/paradigm-namnet, inkl. ev tillägg
				i = rad.indexOf('\"'); // första "
				j = rad.indexOf("__" + klass); //sista delen av paradigmet

				p = p + rad.substring(i+1, j) + " ";
				}		
			}
			
		fin.close();
		
		//}
		
		return p;	
	}
		
		
	// Inmatning av ett ord:
	// lemma, stam, paradigm etc
		
	public static String defineWord (int added, ArrayList<String> WordsToDefine, String author, String mall, String klass, String topParadigms) throws Exception
	{
	// <e lm="dagis" c="domain:family style:fam" a="PT">
	
	String ord = "";
	String paradigm = "";
				
	// uppgifterna skiljs åt med tecknet |
	
	// Obs! Lemma redan inläst från bidix!
	// Fortsätta till listan på ord är slut.

	//Utskrift.rubrik ("Nytt ord");
	Utskrift.skrivText ("(danska/norska tecken: å æ ø svenska tecken: å ä ö)");
	// String rad (String ask, String[] stop, String meddelande)

	//String lemma = lasWordsToDefine(WordsToDefine, stop, check); // läser från ordlista skapad från bidix
	//String lemma = lasWordsToDefine(WordsToDefine, check); // läser från ordlista skapad från bidix
	String lemma = WordsToDefine.get(added); // antalet ord = index för nästa!
	
	if (lemma.length() == 0) return ord = "";

	else
		{
		//Utskrift.skrivText("Lemma: " + lemma);
		Utskrift.rubrik("Lemma (grundform): " + lemma);
		ord = ord + lemma;
		
		// <e lm="dagis" c="domain:family style:fam" a="PT">		
		Utskrift.skrivText ("I vilket sammanhang används ordet?");
		// Inga blanka! De är gräns mot nästa "attribute":
		// 'space' as a separator of pairs and ':' to separate key:value pairs
		ord = ord + "|" +  Inmatning.rad("Domän:").replaceAll("\\s","");
				
		Utskrift.skrivText ("genre: sol neu fam pej vulg old dial");
		ord = ord + "|" +  Inmatning.rad("Genre:", "sol|neu|fam|pej|vulg|old|dial|^$").trim();
					
		ord = ord + "|" + author;
		
		Utskrift.skrivText ("flerordsuttryck: <b/> ist.f. mellanslag");

		// ij|abbr|n|vblex tillåtna
		if (klass.equals("n")||klass.equals("ij") ||klass.equals("abbr"))
		{
			String stam = lemma;
			stam = stam.replaceAll(" ","<b/>");
			Utskrift.skrivText("stam: " + stam);
			//String change = Inmatning.rad("Ändra (j/N) ?");
			//if (change.equals("") || !(change.charAt(0)=='j'))
			String change = Inmatning.rad("Ändra (j/N) ?");
			if (change.equals("") || change.equals("n") || change.equals("N"))
			{
			ord = ord + "|" + stam;
			}
			else
			{
				if (!(change.charAt(0)=='j'))
				{
				ord = ord + "|" + change; // antar att man skrivit nya stammen
				}
				else
				{
				ord = ord + "|" +  Inmatning.rad("stam:").trim();
				}
			}
		}
		
		else
		{
			ord = ord + "|" +  Inmatning.rad("stam:").trim();
		}
					
		Utskrift.rubrik ("Paradigmer");

		// interjektioner oböjliga i sv-da-no
		if(klass.equals("ij"))
		{
			Utskrift.skrivText("(för interjektioner väljs paradigm automatiskt)");
			paradigm = mall.trim();
		}

		// förkortningar normalt oböjliga i sv-da-no
		// annars bör de behandlas som tex egennamn (np) eller substantiv (n)
		else if(klass.equals("abbr"))
		{
			Utskrift.skrivText("(för förkortningar väljs paradigm automatiskt.");
			Utskrift.skrivText("De är normalt oböjliga, annars bör de behandlas som tex egennamn (np).)");
			paradigm = mall.trim();
		}
		else
		{
			// Skriver ut de vanligaste paradigmen
			Utskrift.skrivText("De vanligaste: " + topParadigms);
			// Skriver ut ord som karakteriserar resp. paradigm
			Utskrift.skrivText(mall);
			
			while (true)
			{
				paradigm = Inmatning.rad("paradigm:", mall.replaceAll(" ","|")).trim();	
				if (paradigm.length() != 0) break;
			}
		}
		
		ord = ord + "|" +  paradigm;
		//ord = ord + "|" +  Inmatning.rad("paradigm:").trim();
		ord = ord + "__" + klass;
				
		return ord;
		}
	}
		
	// XML-taggar läggs till en monodix
		
	public static String makeXMLmonodix (ArrayList alDix) throws Exception
	{
	// <e lm="dagis" c="domain:family style:fam" a="PT">
	// c="domain:sjö och hav style:neu" FEL: inga mellanslag!!
	String monodix = "";
	String lemma = "";

	String domain = "";
	String genre = "";
	
	String author = "";
	String stam = "";
	String paradigm = "";
		
	String rad = "";
		
	for (int r = 0; r < alDix.size(); r++)
		{		
		rad = (String) alDix.get(r);
				
		Scanner	sc = new Scanner (rad);
		// Obs! Delimiter måste anges som regex! [] är teckenklass
		sc.useDelimiter ("[|]"); // två i följd ger TOM sträng
								
		lemma = sc.next();
		monodix = monodix + "<e lm=\"" + lemma;
				
		// Comment: domän och genre
		// c="domain:family style:fam"
		domain = sc.next(); // domän
		
		if (domain.length() == 0)
			{}
		else
			monodix = monodix + "\" c=\"domain:" + domain;
		
		genre = sc.next(); // genre dvs. style
		
		if (genre.length() == 0)
			{}
		else if (domain.length() == 0)
			{
			monodix = monodix + "\" c=\"style:" + genre;
			}
		else // dvs. !(domain.length() == 0)
			{
			monodix = monodix + " style:" + genre;
			}		
				
		author = sc.next();
		if (author.length() == 0)
			{}
		else
			monodix = monodix + "\" a=\"" + author + "\"";	
				
		stam = sc.next();
				
		paradigm = sc.next();
				
		monodix = monodix + ">              <i>" + stam + "</i><par n=\"" + paradigm + "\"/></e>\n";

		}
			
	return monodix; // XML-taggad
		
	}

	// Backup av ordlistan
	public static void dixBackup (ArrayList alDix, String fileName) throws Exception
	{
		Utskrift.skrivText ("Vänta! Sparar backkup av ordlistan.");
		// formaterar ordlistan
		String back = makeXMLmonodix (alDix);
		// skriver till fil
		Textfil.skrivText (fileName, back, "UTF-8");
		Utskrift.skrivText ("Backup sparad: " + fileName);
		System.out.println();
	}
	
	// Ändra rad dvs. ett ord (före taggning)
	public static void andraRad (int added, ArrayList<String> WordsToDefine, ArrayList<String> alDix, int index, String author, String mall, String klass, String topParadigms) throws Exception
	{
		// Skriv ut den gamla raden
		String rad = (String) alDix.get(index);
		/****************************************************
		Scanner	sc = new Scanner (rad);
		// Obs! Delimiter måste anges som regex! [] är teckenklass
		sc.useDelimiter ("[|]"); // två i följd ger TOM sträng
		String lemma = "";
		lemma = sc.next();
		WordsToDefine.add(lemma); // lägg till lemmat sist
		check[0] = WordsToDefine.size() - 1; // peka på den sista (nya!) raden!
		******************************************************/
		Utskrift.rubrik ("Ordet du vill ändra");
		Utskrift.skrivText(rad);
		// Mata in den nya raden
		// =====================
		while (true)
		{
			// mall = paradigm-stam, klass = ordklass
			//rad = defineWord(WordsToDefine, author, mall, klass, stop, check, topParadigms);
			//rad = defineWord(WordsToDefine, author, mall, klass, check, topParadigms);
			//rad = defineWord(index, WordsToDefine, author, mall, klass, check, topParadigms);
			rad = defineWord(index, WordsToDefine, author, mall, klass, topParadigms);
					
			// Skriv ut raden och ge möjlighet att ändra
			Utskrift.rubrik ("Nya ordet");
			Utskrift.skrivText (rad);
			String change = Inmatning.rad("Ändra (j/N) ?");

			if (change.equals("") || !(change.charAt(0)=='j'))
			{
				// Skriv den nya raden
				alDix.set (index, rad);
				break;
			}
			else
			{
				Utskrift.skrivText("Mata in ordet på nytt!");
				//check[0] = check[0] - 1; // ordets index
			}
		}	
	}
	
	// Sträng-vektor med ord som redan finns i monodix
	// (för att kunna utesluta dem)
		
	public static String[] OrdSomFinns (String pair, String lang, String klass) throws Exception
	{			
		String p = "";
		
		String fil = "apertium-" + pair + "." + lang + ".dix";
		
		FileInputStream fs = new FileInputStream(fil); // byte till character
		Scanner fin = new Scanner (fs, "UTF-8"); // rätt tecken!

		String rad= "";
		int i = 0;
		int j = 0;
		int j1 = 0;
	
		while (fin.hasNextLine())	
		{	
			rad = fin.nextLine();
			
			// Definition av ord och rätt ordklass
			if (rad.contains("<e lm=") && rad.contains("__" + klass))
			
			{
				// Ord som finns i ordlistan
				// Exempel:
				// <e lm="ack" a="PT">              <i>ack</i><par n="ack__ij"/></e>
				// <e lm="aha" c="style:neu" a="PT">              <i>aha</i><par n="ack__ij"/></e>
				// <e lm="t.ex."><i>t.ex.</i><par n="t.ex.__abbr"/></e>
/********************************************
				// början av ordet
				i = rad.indexOf('=');
				
				rad = rad.substring(i+2);
				
				// slutet av ordet
				j = -1 + rad.indexOf('>'); // end of e-tag
				j1 = -3 + rad.indexOf('='); // comment or author
				
				if (j1<j) j = j1;  // comment or author
				
				Utskrift.skrivText ("ord som finns: " + rad.substring(0, j));
				p = p + rad.substring(0, j) + "|";
*********************************************************/
				// början av ordet
				i = rad.indexOf('=');
				
				rad = rad.substring(i+2);
				
				// slutet av ordet
				//j = -1 + rad.indexOf('>'); // end of e-tag
				//j1 = -3 + rad.indexOf('='); // comment or author
				j = -1 + rad.indexOf('>'); // end of e-tag
				j1 = -3 + rad.indexOf('='); // comment or author
				
				if (j1<j) j = j1;  // comment or author
				
				rad = rad.substring(0, j);
				//rad = rad.replaceAll("", "") etc! även "
				rad = rad.replaceAll("\"", "");
				rad = rad.trim();
				
				//Utskrift.skrivText ("ord som finns: " + rad);
				p = p + rad + "|";
			}		
		}
		
		fin.close();
		
	// gör om strängen till vektor
	String[] stop = stringToVektor (p);
	Arrays.sort(stop); // sorteras på plats
	return stop;
	}
	
	//Ord kopieras  från sträng till vektor
	public static String[] stringToVektor (String text) throws Exception
	{
	// pga flerordsuttryck med blanka mellan orden
	StringTokenizer st = new StringTokenizer(text,"|");
  
	// Orden sparas i en sträng-vektor
	// med rätt längd
	String [] ord = new String[st.countTokens()];
	
	int i = 0;
	
	while (st.hasMoreTokens())
		{
			ord[i++] = st.nextToken();
		}
		
	return ord;	
		
	}

	// Läser in en bidix-fil till en vektor som sorteras,
	// så att dubletter kan rensas bort. Ut: ArrayList
	// Medger att man bara lägger till översättningen
	// till varje unikt nytt ord.
	public static ArrayList<String> lasBidix (String pair, String lang, String languagePosition, String klass, String[] stop) throws Exception
	{
		String fil = pair + "." + pair + "." + "dix.txt";
		FileInputStream fs = new FileInputStream(fil); // byte till character
		Scanner fin = new Scanner (fs, "UTF-8"); // rätt tecken!
	
		ArrayList<String> ordlista = new ArrayList<String>();
		String text = "";		
		String rad= "";

		int i = 0;
		int j = 0;
		//String ordet = " ";
		String nyttOrd = "";	
		while (fin.hasNextLine())	
		{	
			rad = fin.nextLine();
			
			// <e>       <p><l>jodå<s n="ij"/></l>                    <r>jodå<s n="ij"/></r></p></e>
			// <e>       <p><l>jävlar<s n="ij"/></l>                  <r>for<b/>fanden<s n="ij"/></r></p></e>
			// <e a="PT">       <p><l>lycka<b/>till<s n="ij"/></l>                      <r>tillykke<s n="ij"/></r></p></e>
			if (rad.contains("<p><l>")) // Definition av ord
						
			{
				if (languagePosition.equals("L")) // språk att översätta från dvs givna ord
				{
					// Ord som finns i ordlistan
					// <e>       <p><l>jävlar<s n="ij"/></l>
					// <e a="PT">       <p><l>lycka<b/>till<s n="ij"/></l>

					// början av ordet
					i = rad.indexOf("<p><l>");
					
					// slutet av ordet
					j = rad.indexOf("<s");
					
					nyttOrd = rad.substring(i+6, j);

					nyttOrd = nyttOrd.replaceAll("<b/>", " ");
					
					if (Arrays.binarySearch (stop, nyttOrd) < 0) // ordet finns inte
					{
						nyttOrd = nyttOrd + "|";
						text = text + nyttOrd;
					}
					else  //ordet finns redan
					{}
				}
				
				if (languagePosition.equals("R")) // språk att översätta från dvs givna ord
				{
					// Ord som finns i ordlistan
					// <r>jodå<s n="ij"/></r></p></e>
					// <r>for<b/>fanden<s n="ij"/></r></p></e>
					
					// början av ordet
					i = rad.indexOf("<r>");
					
					// slutet av ordet
					rad = rad.substring(i+3);
					j = rad.indexOf("<s n=");
					
					nyttOrd = rad.substring(0, j);
					nyttOrd = nyttOrd.replaceAll("<b/>", " ");

					if (Arrays.binarySearch (stop, nyttOrd) < 0) // ordet finns inte
					{
						nyttOrd = nyttOrd + "|";
						text = text + nyttOrd;
					}
					else  //ordet finns redan
					{}
				}
			}		
		}
		
		fin.close();
		
	if (!(text.length() == 0))
	{		
			
		// StringTokenizer lämnar inte extra blanka
		// mellan ord.
			
		StringTokenizer st = new StringTokenizer(text,"|");
	  
		// Orden sparas i en sträng-vektor
		// med rätt längd
		String [] vOrd = new String[st.countTokens()];
		text = null; // Används inte mer.
		
		i = 0;
		
		while (st.hasMoreTokens())
			{
				vOrd[i++] = st.nextToken();
			}
		
		Arrays.sort(vOrd); // sorteras på plats

		String ordet = vOrd[0];
						
		for (i=1;i<vOrd.length;i++)
			{		
				nyttOrd = vOrd[i];
				if (!ordet.equals(nyttOrd)) // ej dubletter
				{
					ordlista.add (ordet);
					ordet = nyttOrd;
				}			
			}
		ordlista.add(ordet);
	}
	return ordlista;
	}
	
	// Beräkna språkets position i paret
	// dvs. om det är till vänster eller höger					
	public static String languagePosition (String lang, String pair) throws Exception
	{
	String pos = "R";					
	if (pair.startsWith (lang) ) pos = "L";	
	return pos;
	}
	
	/*********************************************************
	// Läser från Ordlista skapad från bidix
	public static String lasWordsToDefine(ArrayList<String> WordsToDefine, int[] check) throws Exception
	{
	String lemma = "";
	String ord = "";
	// Läs orden från listan
	for (int i=check[0];i<WordsToDefine.size();i++)
	{
		ord = WordsToDefine.get(i);

		// Om ej träff dvs ej finns i stop-listan, returnera det lästa ordet.
		if (Arrays.binarySearch (stop, ord) < 0)
			{
			lemma = ord;
			check[0] = i+1; // eller ++i (ej i++ !!)
			break;
			}
		lemma = ord;
		check[0] = i+1; // eller ++i (ej i++ !!)
	}
	
	return lemma;
	}
	****************************************************************/
	
	// Läser in ordklass från en monodix-fil
	// så man slipper få en fråga
	public static String lasKlass (String pair) throws Exception
	{
		String fil = pair + "." + pair + "." + "dix.txt";
		FileInputStream fs = new FileInputStream(fil); // byte till character
		Scanner fin = new Scanner (fs, "UTF-8"); // rätt tecken!

		// Skulle kunna läsa från rubriken instället,
		// men det är lätt hänt att den ändras!
		
		String rad= "";
		String klass = "";
		
		int i = 0;
		int j = 0;
			
		while (fin.hasNextLine())	
		{	
			rad = fin.nextLine();

			if (rad.contains("<e")) // Översättning av ord	
			{
				// Ord som finns i ordlistan
				// Exempel från bidix:
				// <e a="PT">       <p><l>nov.<s n="abbr"/></l>                <r>nov.<s n="abbr"/></r></p></e>
				//Utskrift.skrivText("&" + rad + "&");
				
				// början av ordklass
				i = rad.indexOf("n=\"");
				rad = rad.substring(i+3);
				
				//Utskrift.skrivText("&" + rad + "&");
				
				// slutet av ordklassen
				j = rad.indexOf("\"/></l>");

				klass = rad.substring(0, j);
				break; // räcker med att hitta första ordet!
			}		
		}
		
		fin.close();
	return klass;
	}
	
		
	// Skapar en lista som översätter klasskod till ordklass
	public static TreeMap<String, String> partOfSpeach ()
	{
		// ger sorterad lista
		// med kod (nyckel) och klartext (översättning/värde)
		TreeMap<String, String> tm = new TreeMap<String, String>();
		
		tm.put("ij", "interjections"); // Interjektion
		tm.put("abbr", "abbreviations"); // Förkortning
		tm.put("n", "nouns"); // Substantiv/Navneord
		tm.put("vblex", "verbs"); // Verb
		tm.put("adj", "adjectives"); // Adjektiv/Tillægsord
		tm.put("adv", "adverbs"); // Adverb/Biord
		
		return tm;
	}
}