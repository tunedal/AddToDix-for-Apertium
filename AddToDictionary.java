//AddToDictionary.java

// ADD TO DICTIONARY: 

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

// v. 0.1 Comments etc mainly in Swedish.
// v. 0.2 Lots of bugs fixed.
// Some new features like e.g.
// 			Removed direction (not used any more for lexical selection)
//			Added some hints to the user.
//			Stem suggested from the lemma.
// 			File names displayed after saving for easy retrieval.
//			Stop list with existing words of the actual type, e.g. nouns.
// v. 0.3 User has to admit GPL license for the dix.
// v. 0.4 More helpful questions.
// v. 0.5 Better algo for finding lemma.
// v. 0.6 Confirmation before quitting input-loop.
//			Don't ask for paradigm when adding interjections and abbreviations
//			Encourgement: number of added words displayed
//			Nicer output.
// v. 0.7
// v. 0.8
// v. 0.9 
// v. 0.95
// v. 0.96 Lists the most frequent paradigms
//			Checks input for paradigms
// v. 0.97
// v. 0.98 Quicker and more intuitive to change stem.
//			Fixed bugs:
//			Algo for finding lemmas now resistant to extra spaces in monodix.
//			Doesn't try to create a list of paradigms for abbr and ij.
// -----------------------------------------------------------------------

// Skapa enspråkig ordlista för Apertium
// Maskinöversättning
// www.apertium.org
// ====================================== 

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

class AddToDictionary

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
	
	allowed = "ij|abbr|n|vblex";
	String klass = Inmatning.rad("Vilken ordklass ska du mata in? tex interjektion (ij), förkortning (abbr), substantiv (n), verb(vblex)", allowed);
	
	Utskrift.skrivText ("Inmatad ordklass: " + tmKlass.get(klass) + " ("+ klass + ")\n");
	
	// Lista på ord som redan finns
	Utskrift.skrivText("Vänta - göra en lista på ord som redan finns");
	String[] stop = OrdSomFinns (pair, lang, klass);
	
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
			
	// Samla in ordbeskrivningarna
	// ===========================
		
	// Sparas i en ArrayList
	// så att det är lätt att lägga till rader.
		
	ArrayList<String> alDix = new ArrayList<String>();
		
		
	// Inmatning av ord
	// ================
	String rad = "";
	int added = 0;
		
	// Lägg till en rad (sträng) i taget
	// avsluta med ENTER
	Utskrift.rubrik ("Nytt ord (klass: " + klass + " språk: " + lang + ")");
	while (true)
		{
		// mall: paradigm-stam, klass: ordklass
		// stop: ord som finns
		rad = defineWord(author, mall, klass, stop, topParadigms);
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
		}
		else Utskrift.skrivText("Mata in ordet på nytt!");
		}
		
	// Skriv ut hela listan med radnr
	// ==============================
	String raden = "";
	
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
			r--; // r ändras till korrekt index
			andraRad(alDix, r, author, mall, klass, stop, topParadigms); // ändras på plats
		}
	}
	
	// Formatera listan dvs. lägg till HTML-taggar etc
	// ===============================================
	
	String monodix = "";
	String line = "====================================================" + "\n";
	monodix = monodix + "Monolingual dictionnary for " + pair + ", language: " + lang + "\n";
	//monodix = monodix + "Part of speach: " + klass + "\n" + line + "\n";
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
		
		//
		
		return p;	
	}
		
		
	// Inmatning av ett ord:
	// lemma, stam, paradigm etc
		
	public static String defineWord (String author, String mall, String klass, String[] stop, String topParadigms) throws Exception
	{
	// <e lm="dagis" c="domain:family style:fam" a="PT">
	
	String ord = "";
	String lemma = "";
	String paradigm = "";
	String sluta = "";
	String allowed = "";
				
	// uppgifterna skiljs åt med tecknet |
	
	Utskrift.skrivText ("(Tecken att kopiera: å æ ø  - å ä ö )\n");
	
	while (true)
		{
			lemma = Inmatning.rad("Lemma (grundform):", stop, "Ordet finns redan i ordlistan. ***\nå æ ø - å ä ö ").trim();
			
			if (lemma.length() > 0) break;
			
			else
				{			
					allowed = "j|J|ja|JA|Ja|n|N|nej|NEJ|Nej"; // Tillåtna svar (ej tomt)
					sluta = Inmatning.rad("Sluta (j/n)?", allowed);	
					if (sluta.charAt(0)=='j') break;
				}				
		}
		
	if (lemma.length() == 0) 
		{
			return ord = "";
		}

	else
		{
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
			/**************************************
			String change = Inmatning.rad("Ändra (j/N) ?");
			if (change.equals("") || !(change.charAt(0)=='j'))
			{
			ord = ord + "|" + stam;
			}
			else
			{
			ord = ord + "|" +  Inmatning.rad("stam:").trim();
			}
			**************************************************/
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
	public static void andraRad (ArrayList<String> alDix, int index, String author, String mall, String klass, String[] stop, String topParadigms) throws Exception
	{
		// Skriv ut den gamla raden
		String rad = (String) alDix.get(index);
		Utskrift.rubrik ("Ordet du vill ändra");
		Utskrift.skrivText(rad);
		// Mata in den nya raden
		// =====================
		while (true)
		{
			// mall = paradigm-stam, klass = ordklass
			rad = defineWord(author, mall, klass, stop, topParadigms);
					
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
			else Utskrift.skrivText("Mata in ordet på nytt!");
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
				/***********************************************
				// början av ordet
				i = rad.indexOf('=');
				
				rad = rad.substring(i+2);
				
				// slutet av ordet
				j = -1 + rad.indexOf('>'); // end of e-tag
				j1 = -3 + rad.indexOf('='); // comment or author
				
				if (j1<j) j = j1;  // comment or author
									
				//Utskrift.skrivText ("ord som finns: " + rad.substring(0, j));
				p = p + rad.substring(0, j) + "|";
				****************************************************/
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
	// StringTokenizer lämnar inte extra blanka
	// mellan ord.
		
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