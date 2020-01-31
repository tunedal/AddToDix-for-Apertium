//AddToBidixFromMonodix.java

// ADD TO BIDIX FROM MONODIX:

// Skapa tvåspråkig ordlista för Apertium
// Utnyttjar egen enspråkig ordlista
// skapad med AddToDictionary.
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
// 			File names displayed after saving for easy retrieval.
// v. 0.3 User has to admit GPL license for the dix.
// v. 0.4 More helpful questions.
// v. 0.5 Better algo for finding lemma.
// v. 0.6 Encourgement: number of added words displayed
//			Nicer output.
// v. 0.7 Reads part of speach from monodix
//			Bugs fixed.
// v. 0.8 
// v. 0.9
// v. 0.95
// v. 0.96 Possibility to quit after each 5 added words.
// v. 0.97 List of words to translate now only contains new words.
// 			The number of remaining words is displayed after each 5 treated.
//			Nicer output.  Fixed bug: now stops nicely after last entry.
// v. 0.98
//			Fixed bugs:
//			Stops nicely if all words in monodix already are present in bidix.
//			Stop-words from correct language: left or right language in bidix.
//
// ----------------------------------------------------------------

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

class AddToBidixFromMonodix

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
	// ----------------------
	// tillåten input
	allowed = pair;
	allowed = allowed.replaceAll ("-", "|");

	String lang = "";
	
	Utskrift.rubrik("Källspråk");
	while (true)
		{
		lang = Inmatning.rad("Ordlistan du ska översätta FRÅN är på språket (tex sv):", allowed);		
		if (lang.length() != 0) break;
		}
		
	// Typ av ord
	// ==========
	// Lingvistisk kategori
	// dvs ordklass
	
	// Skapar lista på ordklasser
	TreeMap<String, String> tmKlass = partOfSpeach ();
	
	String klass = lasKlass (pair, lang);

	//Utskrift.skrivText("Orden i ordlistan tillhör ordklass: " + klass);
	Utskrift.skrivText("Orden i ordlistan tillhör ordklass: " + tmKlass.get(klass) + " ("+ klass + ")\n");

	// Beräkna språkets position
	// =========================
	// dvs. är språket till vänster (L)
	// eller till höger (R)
	// Behövs för att läsa från bidix
		
	String pos = languagePosition (lang, pair);
	
	// Lista på ord som redan finns
	// jfr AddToDictionaryFromBidix!
	// Listan måste rensas när den skapas.
	Utskrift.skrivText("Vänta - göra en lista på ord som redan finns");
	String[] stop = lasBidix (pair, klass, pos);

	Utskrift.skrivVektor(stop);
		
	// Utgå från egen ny monodix
	// =========================
	
	// Läser in lemma från monodix
	// ---------------------------
	// Rensas från ord som finns (stop) vid inläsningen!
	ArrayList<String> WordsToTranslate = lasMonodix (pair, lang, stop);
	
	int ant = WordsToTranslate.size();
	Utskrift.rubrik ("Antal ord att översätta: " + ant);
	if (ant == 0) System.exit(0);
		
	// Läser vänster och höger språk
	// från pair
	
	int len = pair.length();
	int ind = pair.indexOf("-");
	String left = pair.substring(0, ind);
	String right = pair.substring(ind+1);
	//Utskrift.skrivText("Vänster språk: " + left + " Höger språk: " + right);
	
	// Beräkna språkets position  (monodix)
	// =========================
	// dvs. är språket till vänster (L)
	// eller till höger (R)
	// Behövs för att utnyttja monodix
			
/**************************************
	// position på nytt sätt
	String pos = "R";
	if (left.equals(lang)) pos = "L";
*****************************************/
	
	// Skapar lista på språk
	TreeMap<String, String> tmLang = languages ();
	//Utskrift.skrivText (tmLang);
	
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
	Utskrift.rubrik ("Nytt ord (klass: " + klass + ")");
	while (true && added < WordsToTranslate.size())
		{
		// mall: paradigm-stam, klass: ordklass
		//pos: vänster eller höger språk , stop: ord som finns
		rad = defineWordFromMonodix (added, WordsToTranslate, pos, left, right, tmLang, author, klass);
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
				
				if (added == WordsToTranslate.size()) break;
				
				Utskrift.rubrik("Nästa ord");
				
				// gör backup för var 10:e rad			
				//if (0 == Math.round(alDix.size()%10))
				if (0 == Math.round(added%10))
				{
					String filNamn = pair + "." + pair + "." + "dix.txt.back";
					dixBackup (alDix, filNamn); // Backup till fil
				}
				
				// Möjlighet att avsluta efter var 5:e ord.
				if (0 == Math.round(added%5))
				{	
					//Utskrift.skrivText("Du har matat in " + added + " ord");
					Utskrift.skrivText("Du har matat in " + added + " ord. Återstår: " + (WordsToTranslate.size() - added));
					allowed = "j|J|ja|JA|Ja|n|N|nej|NEJ|Nej|^$"; // Tillåtna svar (^$ = tomt)
					String sluta = Inmatning.rad("Sluta (j/N)?", allowed);	
					if (!sluta.equals("") && sluta.charAt(0)=='j') break;
				}
			}
		else 
			{
				Utskrift.skrivText("Mata in ordet på nytt!");
				//added dvs. ordets index i WordsToTranslate oförändrat.
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
			r--; // r ändras till korrekt index
			andraRad(alDix, r, WordsToTranslate, pos, tmLang, left, right, author, klass); // ändras på plats
		}
	}
	
	// Formatera listan dvs. lägg till HTML-taggar
	// ===========================================

	String bidix = "";
	String line = "====================================================" + "\n";
	bidix = bidix + "Bilingual dictionnary for " + pair + "\n";
	//bidix = bidix + "Part of speach: " + klass + "\n" + line + "\n";
	bidix = bidix + "Part of speach: " + tmKlass.get(klass) + " (" + klass + ")\n" + line + "\n";
	
	bidix = bidix + makeXMLbidix (alDix);
	
	String lic = "Jag tillåter att ordlistan licensieras enl. GPL version 2 eller senare.";
	bidix = bidix + "\n" + lic + "\n" + namn + "\n" + epost; //avsändare

	if (bidix.length() != 0) Utskrift.skrivText ("\n" + bidix + "\n");
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
		String filnamn = pair + "." + pair + "." + "dix.txt";
		Textfil.skrivText (filnamn, bidix, "UTF-8");
		Utskrift.rubrik ("Sparat ordlistan: " + filnamn);
		
		}
	else Utskrift.rubrik ("Sparar ingenting");	
	}
				
	
	public static String defineWordFromMonodix (int rad, ArrayList<String> WordsToTranslate, String languagePosition, String left, String right, TreeMap<String, String> tmLang, String author, String klass) throws Exception

	{
		String ord = "";
		String toTranslate = "";
					
		// uppgifterna skiljs åt med tecknet |
		// <e a="PT">       <p><l>godkännande<s n="n"/></l>              <r>accept<s n="n"/></r></p><par n="_nt_ut"/></e>
		// bidix: author, L lemma, L paradigm, R lemma, R paradigm
		if (languagePosition.equals("L")) // Vänster lemma givet
		{
			//Utskrift.skrivText("lemma att översätta från " + tmLang.get(left) + ": " + WordsToTranslate.get(rad));
			toTranslate = WordsToTranslate.get(rad);
			Utskrift.rubrik("Lemma (grundform) att översätta från " + tmLang.get(left) + ": " + toTranslate);
			Utskrift.skrivText ("(danska/norska tecken: å æ ø svenska tecken: å ä ö)");
			String lemma = Inmatning.rad("lemma " + tmLang.get(right) + ":").trim();
			if (lemma.length() == 0) return ord = "";

			else
			{
				ord = ord + "|" + author;
				//ord = ord + "|" + lemma;
				//toTranslate
				//stam = stam.replaceAll(" ","<b/>");
				toTranslate = toTranslate.replaceAll(" ","<b/>");
				ord = ord + "|" + toTranslate;
				//ord = ord + "|" + WordsToTranslate.get(rad);
				ord = ord + "|" + klass;
				
				// bidix: riktning, author, L lemma, L paradigm, R lemma, R paradigm
				// Riktning ska EJ användas för att välja ord!
				lemma = lemma.replaceAll(" ","<b/>");
				ord = ord + "|" + lemma;
				
				ord = ord + "|" + klass;
				
				if (klass.equals("n"))
					{
					Utskrift.rubrik ("Paradigmer");
					Utskrift.skrivText ("Ange ordens genus");
					ord = ord + "|" + askParadigm (tmLang, left, right)  + "|";
					}
					
				else
				
					{
						ord = ord +"||";
					}
					
				Utskrift.skrivText(ord);
				return ord;
			}
		}
		
		else //languagePosition = R: Höger ord givet.
		{
			toTranslate = WordsToTranslate.get(rad);
			Utskrift.rubrik("Lemma (grundform) att översätta från " + tmLang.get(right) + ": " + toTranslate);
			Utskrift.skrivText ("(danska/norska tecken: å æ ø svenska tecken: å ä ö)");
			String lemma = Inmatning.rad("lemma " + tmLang.get(left) + ":").trim();
			if (lemma.length() == 0) return ord = "";

			else
			{
				ord = ord + "|" + author;
				lemma = lemma.replaceAll(" ","<b/>");
				ord = ord + "|" + lemma;
				ord = ord + "|" + klass;
				
				// bidix: riktning, author, L lemma, L paradigm, R lemma, R paradigm
				// Riktning ska EJ användas för att välja ord!
				
				toTranslate = toTranslate.replaceAll(" ","<b/>");
				ord = ord + "|" + toTranslate;
				
				ord = ord + "|" + klass;
								
				if (klass.equals("n"))
					{
					Utskrift.rubrik ("Paradigmer");
					Utskrift.skrivText ("Ange ordens genus");
					ord = ord + "|" + askParadigm (tmLang, left, right)  + "|";
					}
					
				else
				
					{
						ord = ord +"||";
					}
				
				Utskrift.skrivText(ord);
				return ord;
			}
		}
	}
		
	// XML-taggar läggs till en bidix
		
	public static String makeXMLbidix (ArrayList alDix) throws Exception
	{
	// <e a="PT">       <p><l>passa<s n="vblex"/></l>                <r>passe<s n="vblex"/></r></p></e>
	String bidix = "";
	String author = "";
	String vLemma = ""; // left lemma
	String vKlass = ""; // left part of speach
	String hLemma = ""; // right lemma
	String hKlass = ""; // right part of speach
	String paradigm = ""; //  paradigm
		
	String rad = "";
		
	for (int r = 0; r < alDix.size(); r++)
		{		
		rad = (String) alDix.get(r);
				
		Scanner	sc = new Scanner (rad);
		// Obs! Delimiter måste anges som regex! [] är teckenklass
		sc.useDelimiter ("[|]"); // två i följd ger TOM sträng

		author = sc.next();
		bidix = bidix + "<e a=\"" + author + "\">       <p><l>";
	
		vLemma = sc.next();
		bidix = bidix + vLemma + "<s n=\"";
		
		vKlass = sc.next();
		bidix = bidix + vKlass + "\"/></l>                <r>";

		hLemma = sc.next();
		bidix = bidix + hLemma + "<s n=\"";
		
		hKlass = sc.next();
		bidix = bidix + hKlass + "\"/></r></p>";		
		
		paradigm = sc.next();
		if (paradigm.length() == 0)
			{
				bidix = bidix + "</e>\n";
			}
		else
			bidix = bidix + "<par n=\"" + paradigm + "\"/></e>\n";		
		}
			
	return bidix; // XML-taggad
		
	}

	// Backup av ordlistan
	public static void dixBackup (ArrayList alDix, String fileName) throws Exception
	{
		Utskrift.skrivText ("Vänta! Sparar backkup av ordlistan.");
		// formaterar ordlistan
		String back = makeXMLbidix (alDix);
		// skriver till fil
		Textfil.skrivText (fileName, back, "UTF-8");
		Utskrift.skrivText ("Backup sparad: " + fileName);
		System.out.println();
	}
	
	// Ändra rad dvs. ett ord (före taggning)
	public static void andraRad (ArrayList<String> alDix, int index, ArrayList<String> WordsToTranslate, String languagePosition, TreeMap<String, String> tmLang, String left, String right, String author, String klass) throws Exception
	{
		// Hämta den gamla raden
		String rad = (String) alDix.get(index);

		// Skriv ut den gamla raden
		Utskrift.rubrik ("Ordet du vill ändra");
		Utskrift.skrivText(rad);
		// Mata in den nya raden
		// =====================
		while (true)
		{
			// klass = ordklass
			// languagePosition = vänster eller höger språk
			rad = defineWordFromMonodix (index, WordsToTranslate, languagePosition, left, right, tmLang, author, klass);
					
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
					//added dvs. ordets index i WordsToTranslate oförändrat
				}
		}	
	}
	
	// Skapar en lista som översätter språkkod till språk
	public static TreeMap<String, String> languages ()
	{
		// ger sorterad lista
		// med kod (nyckel) och klartext (översättning/värde)
		TreeMap<String, String> tm = new TreeMap<String, String>();
		
		// lägger till språk
		// blir en mycket lång lista!
		// läsa från fil i en loop? Onödigt långsamt?
		tm.put("sv", "svenska");
		tm.put("da", "danska");
		tm.put("nb", "norska bokmål");
		tm.put("nn", "norska nynorsk");
		
		return tm;
	}
	
	// Paradigm för substantiv
	public static String askParadigm (TreeMap<String, String> tmLang, String left, String right) throws Exception
	{
		// par n="_ut_ut"
		String p = "_";
		String ask = tmLang.get(left) + ": Är ordet ett \"den-ord\" (ut: utrium) eller ett \"det-ord\" (nt: neutrum)";
		p = p + Inmatning.rad(ask,"nt|ut").trim();
		ask = tmLang.get(right) + ": Är ordet ett \"den-ord\" (ut: utrium) eller ett \"det-ord\" (nt: neutrum)";
		p = p + "_" + Inmatning.rad(ask,"nt|ut").trim();
		return p;
	}
	
	// Läser in en monodix-fil
	// medger att man bara lägger till översättningen
	public static ArrayList<String> lasMonodix (String pair, String lang, String[] stop) throws Exception
	{
		String fil = pair + "." + lang + "." + "dix.txt";
		FileInputStream fs = new FileInputStream(fil); // byte till character
		Scanner fin = new Scanner (fs, "UTF-8"); // rätt tecken!

		ArrayList<String> ordlista = new ArrayList<String>();
		String rad= "";

		int i = 0;
		int j = 0;
		int j1 = 0;
		String nyttOrd = "";
			
		while (fin.hasNextLine())	
		{	
			rad = fin.nextLine();
			
			if (rad.contains("<e lm=")) // Definition av ord			
			{
				// Ord som finns i ordlistan
				// Exempel:
				// <e lm="ack" a="PT">              <i>ack</i><par n="ack__ij"/></e>
				// <e lm="aha" c="style:neu" a="PT">              <i>aha</i><par n="ack__ij"/></e>
				// <e lm="t.ex."><i>t.ex.</i><par n="t.ex.__abbr"/></e>
				// <e lm="AB"  c="Aktiebolag/AftonBladet" a="PT"><i>AB</i><par n="t.ex.__abbr"/></e>
				// <e lm="andelsboligforening" r="RL"  c="Danska!" a="PT"><i>andelsboligforeningak</i><par n="t.ex.__abbr"/></e>
				// <e lm="adr." a="PT"><i>adr.</i><par n="t.ex.__abbr"/></e>

				//Utskrift.skrivText("monodix-rad: " + rad);

				// början av ordet
				i = rad.indexOf('=');
				
				rad = rad.substring(i+2);
				
				// slutet av ordet
				j = -1 + rad.indexOf('>'); // end of e-tag
				j1 = -3 + rad.indexOf('='); // comment or author
				
				if (j1<j) j = j1;  // comment or author
				
				nyttOrd = rad.substring(0, j);
				
					if (Arrays.binarySearch (stop, nyttOrd) < 0) // ordet finns inte
					{
						ordlista.add (nyttOrd);
					}
					else  //ordet finns redan
					{}
				
				//Utskrift.skrivText ("ord: " + rad.substring(0, j));
				//ordlista.add (rad.substring(0, j));			
			}		
		}
		
		fin.close();
	//Utskrift.skrivText (ordlista);
	return ordlista;
	}
	
	// Läser in ordklass från en monodix-fil
	// så man slipper få en fråga
	public static String lasKlass (String pair, String lang) throws Exception
	{
		String fil = pair + "." + lang + "." + "dix.txt";
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
			
			if (rad.contains("<e lm=")) // Definition av ord			
			{
				// Ord som finns i ordlistan
				// Exempel:
				// <e lm="okt" a="PT"><i>okt</i><par n="t.ex.__abbr"/></e>
		
				// början av ordklass (slutet av paradigm)
				i = rad.indexOf("__");
				rad = rad.substring(i+2);
								
				// slutet av ordklassen
				j = rad.indexOf("\"/></e>");

				klass = rad.substring(0, j);
				break; // räcker med att hitta första ordet!
			}		
		}
		
		fin.close();
	return klass;
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
	
	// Läser in en bidix-fil till en vektor som sorteras,
	// så att dubletter kan rensas bort. Ut: ArrayList
	// Medger att man bara lägger till översättningen
	// till varje unikt nytt ord.
	// OBS! Utgår från VÄNSTER språk!
	public static String[] lasBidix (String pair, String klass, String pos) throws Exception
	{
		String fil = "apertium-" + pair + "." + pair + "." + "dix";
		FileInputStream fs = new FileInputStream(fil); // byte till character
		Scanner fin = new Scanner (fs, "UTF-8"); // rätt tecken!
	
		//ArrayList<String> ordlista = new ArrayList<String>();
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
			
			if (rad.contains("<l>") && rad.contains("n=\"" + klass)) // Definition av ord i rätt klass
						
			{
				if (pos.equals("L")) // språk att översätta från dvs givna ord
				{
					// Ord som finns i ordlistan
					// <e>       <p><l>jävlar<s n="ij"/></l>
					// <e a="PT">       <p><l>lycka<b/>till<s n="ij"/></l>

					// början av ordet
					i = rad.indexOf("<l");// Vänster språk!
					
					nyttOrd = rad.substring(i+3);
					
					// slutet av ordet
					j = nyttOrd.indexOf("<s");
					
					nyttOrd = nyttOrd.substring(0, j);

					nyttOrd = nyttOrd.replaceAll("<b/>", " ");
					
					Utskrift.skrivText("Bef. ord: " + nyttOrd);
					
					nyttOrd = nyttOrd + "|";
					text = text + nyttOrd;
				}
				if (pos.equals("R")) // språk att översätta från dvs givna ord
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

					nyttOrd = nyttOrd + "|";
					text = text + nyttOrd;
				}
				
			}
		}
		
		fin.close();
		
	// gör om strängen till vektor
	String[] vOrd = stringToVektor (text);
	text = null; // Används inte mer.

	Arrays.sort(vOrd); // sorteras på plats
	
	// tar bort dubletter
	// ------------------
	
	String unika = "";

	String ordet = vOrd[0];

	for (i=1;i<vOrd.length;i++)
	{		
		nyttOrd = vOrd[i];
		if (!ordet.equals(nyttOrd)) // ej dubletter
		{
			//ordlista.add (ordet);
			unika = unika + nyttOrd+ "|";
			ordet = nyttOrd;
		}			
	}
	unika = unika + nyttOrd+ "|";
	//ordlista.add(ordet);
	vOrd = stringToVektor (unika);
	
	return vOrd;
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
	
	// Beräkna språkets position i paret
	// dvs. om det är till vänster eller höger					
	public static String languagePosition (String lang, String pair) throws Exception
	{
	String pos = "R";					
	if (pair.startsWith (lang) ) pos = "L";	
	return pos;
	}
}