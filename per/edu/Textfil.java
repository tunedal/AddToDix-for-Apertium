//Textfil.java

// TEXTFIL:

// Metoder för att skriva och läsa textfiler bekvämt
// och med rätt tecken (å, ä, ö).
// ====================================== 

// Version: 0.98

//      Copyright (c) 2012-2013 Per Tunedal, Stockholm, Sweden
//       Author: Per Tunedal <info@tunedal.nu>

	// Optimized code for line counting heavily inspired by:
	// http://stackoverflow.com/questions/453018/number-of-lines-in-a-file-in-java
	// with the kind consent of Martin Ankerl martin.ankerl@gmail.com
	// "It's not completely my code, it has been modified by one or more of the stackoverflow community. But you are of course free to use it anywhere, as far as I can say. Just don't sue me or anyone because of it ;)"
	// Martin

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
// v. 0.98 Added line counting
// --------------------------------------

package per.edu;

import java.io.*; // för att skapa/öppna en fil (File)
import java.util.*; // för att skriva/läsa en fil (PrintWriter resp. Scanner)

public class Textfil

// Öppna och skriv till en textfil
// Skriver över om filen finns

{

	public static void skrivText (String filnamn, String text) throws Exception
	{
	// Binder en textfil till en variabel
	// Filen skapas om den inte finns.
	// Använder paketet java.io
	
	File   fil = new File (filnamn); // skapas i aktuell katalog
	
	// Skapar ett FileWriter-objekt för skrivning till filen
	// och binder objektet till filen
	// Filen öppnas samtidigt för skrivning.
	// Använder paketet java.io
	
	// Skriver över filen om den finns
	// (fil, true) lägger till på slutet
	
	FileWriter fw = new FileWriter (fil); // Java 1.4?
	
	// Skapar ett PrintWriter-objekt för 
	// bekväm skrivning till filen.
	// (Automatisk omvandling till tkn-sträng.)
	// och binder objektet till filen
	// Använder paketet java.util	
	
	PrintWriter fout = new PrintWriter (fw); // Java 1.4?
		
	// Skriver till filen
	
	fout.print (text);
	
	// Stänger filen
	// framtvingar skrivning av de sista buffrade tecken,
	// görs annars med flush().
	
	fout.close();  // Stänger även fw!

	}

	// Öppna och skriv till en textfil
	// Lägger till om filen finns

	public static void addText (String filnamn, String text) throws Exception
	{

	// Binder en textfil till en variabel
	// Filen skapas om den inte finns.
	// Använder paketet java.io
	
	File   fil = new File (filnamn); // skapas i aktuell katalog
	
	// Skapar ett FileWriter-objekt för skrivning till filen
	// och binder objektet till filen
	// Filen öppnas samtidigt för skrivning.
	// Använder paketet java.io
	
	// Lägger till i slutet på filen
	// om den finns
	// (argumentet "true")
	
	FileWriter fw = new FileWriter (fil, true);
		
	// Buffrad utskrift
	// skapar ett BufferedWriter-objekt för skrivning
	// och binder det till FileWriter-objektet.
	
	BufferedWriter fout = new BufferedWriter (fw);
		
	// Lägger till radbrytning.
	fout.write (text + "\n"); // BufferedWriter
	
	// Stänger filen
	// framtvingar skrivning av de sista buffrade tecken,
	// görs annars med flush().
	
	fout.close();  // Stänger även fw!
	
	}
	
	
// Öppna och skriv till en textfil med angiven kodning
// Skriver över om filen finns

	public static void skrivText (String filnamn, String text, String encoding) throws Exception
	{
	// Binder en textfil till en variabel
	// Filen skapas om den inte finns.
	// Använder paketet java.io
	
	File   fil = new File (filnamn); // skapas i aktuell katalog
	
	// Skapar ett FileWriter-objekt för skrivning till filen
	// och binder objektet till filen
	// Filen öppnas samtidigt för skrivning.
	// Använder paketet java.io
	
	// Skriver över filen om den finns
	// (fil, true) lägger till på slutet
	
	 // Skriver byte-ström till fil
	FileOutputStream fos = new FileOutputStream (fil);
		
	// Character till byte
	OutputStreamWriter fout = new OutputStreamWriter (fos, encoding); // Java 1.6
	
	// Skriver till filen
	
	//Obs! Lägger till!
	fout.append (text); // Java 1.6
	
	// Stänger filen
	// framtvingar skrivning av de sista buffrade tecken,
	// görs annars med flush().
	
	fout.close(); // Stänger även fw!

	}
	
// Öppna och skriv till en textfil med angiven kodning
// Lägger till om filen finns och append = true
public static void addText (String filnamn, String text, String encoding) throws Exception
	{
	// Binder en textfil till en variabel
	// Filen skapas om den inte finns.
	// Använder paketet java.io
	
	File   fil = new File (filnamn); // skapas i aktuell katalog
	
	// Skapar ett FileWriter-objekt för skrivning till filen
	// och binder objektet till filen
	// Filen öppnas samtidigt för skrivning.
	// Använder paketet java.io
	
	// Skriver över filen om den finns
	// (fil, true) lägger till på slutet
	
	 // Skriver byte-ström till fil
	 // boolean true anger append! Annars: skriv över.
	 // --------------------------
	FileOutputStream fos = new FileOutputStream (fil, true); // true ger append
		
	// Character till byte
	OutputStreamWriter fout = new OutputStreamWriter (fos, encoding); // Java 1.6
	
	// Skriver till filen
	
	//Obs! Lägger till!
	fout.append (text); // Java 1.6
	
	// Stänger filen
	// framtvingar skrivning av de sista buffrade tecken,
	// görs annars med flush().
	
	fout.close(); // Stänger även fw!

	}
	
	// Läs från en textfil
	
	public static String laesText (String filnamn) throws Exception
	
	{
	
	File fil = new File (filnamn); // Öppnar en fil
	
	Scanner fin = new Scanner (fil); // Binder till läsare
	
	String text = "";
	
	String rad = "";
	
	while (fin.hasNextLine())
		{
		rad = fin.nextLine() + "\n";
		text = text + rad;		
		}
	
	fin.close(); // Stänger filen
		
	return text;
	}
	
	// Läs från en texfil med angiven kodning
	public static String laesText (String filnamn, String encoding) throws Exception
	
	{
	
	File fil = new File (filnamn); // Öppnar en fil
	
	// läser byte från fil
	FileInputStream fis = new FileInputStream (fil);
	
	// Läsare för rader etc
	// avkodar från angiven kodning
	
	Scanner fin = new Scanner (fis, encoding);
	
	String text = "";
	
	String rad = "";
	
	while (fin.hasNextLine())
		{
		rad = fin.nextLine() + "\n";
		text = text + rad;
		}
	
	fin.close(); // Stänger filen
	
	return text;	
	}
	
	// Läs ett visst antal rader från en texfil med angiven kodning
	public static String laesText (String filnamn, String encoding, int lines) throws Exception
	
	{
	
	File fil = new File (filnamn); // Öppnar en fil
	
	// läser byte från fil
	FileInputStream fis = new FileInputStream (fil);
	
	// Läsare för rader etc
	// avkodar från angiven kodning
	
	Scanner fin = new Scanner (fis, encoding);
	
	String text = "";
	
	String rad = "";
	
	int antal = 0;
	
	while (fin.hasNextLine())
		{
		rad = fin.nextLine() + "\n";
		text = text + rad;
		antal++;
		if (antal == lines) break;
		}
	
	fin.close(); // Stänger filen
	
	return text;
	}
	
	// Läs angivna rader från en texfil med angiven kodning
	public static String laesText (String filnamn, String encoding, int[] lines) throws Exception
	
	{
	// Kräver att vektorn med radnummer är sorterad.
	// BufferedReader är c:a 25 % snabbare.
	BufferedReader br = new BufferedReader(new InputStreamReader(new
	FileInputStream(filnamn), encoding));

	String text = "";
	String rad = "";
	int antal = 0;
	int hittade = 0;

	while ((hittade < lines.length) && ((rad = br.readLine()) != null))
	{
		//System.out.println(rad);
		antal++;
		if (0 == Math.round(antal%100000)) System.out.println ("Tusental rader hittills: "+ antal/1000);
		if (Arrays.binarySearch(lines, antal) >= 0)
		{
			text = text + rad + "\n";
			hittade++;
		}
	}
	
	br.close();
	return text;
	}
	
	// Läs rader från en texfil, sätt angiven avgränsare
	public static String laesText (String filnamn, String encoding, String delimiter) throws Exception
	
	{
	// BufferedReader lär vara snabbare. Ja, c:a 25 %!
	BufferedReader br = new BufferedReader(new InputStreamReader(new
	FileInputStream(filnamn), encoding));

	String text = "";
	String rad = "";
	int antal = 0;

	while ((rad = br.readLine()) != null)
	{
		//System.out.println(rad);
		antal++;
		if (0 == Math.round(antal%100000)) System.out.println ("Tusental rader hittills: "+ antal/1000);
		//if (0 == Math.round(antal%1000)) System.out.println ("Tusental rader hittills: "+ antal/1000);
		{
			text = text + rad + delimiter;
		}
	}
	
	br.close();
	return text;
	}
	
	// Läs visst antal rader från en textfil till en strängvektor
	public static String[] laesText (int lines, String filnamn, String encoding) throws Exception
	
	{
	// BufferedReader lär vara snabbare. Ja, c:a 25 %!
	BufferedReader br = new BufferedReader(new InputStreamReader(new
	FileInputStream(filnamn), encoding));

	String[] text = new String[lines];
	String rad = "";
	//int antal = 0;

	//while ((rad = br.readLine()) != null)
	for (int i = 0;i < lines; i++)
	{
		rad = br.readLine();

		if (0 == Math.round(i%100000)) System.out.println ("Tusental rader hittills: "+ i/1000);
		{
			text[i] = rad;
		}
	}
	
	br.close();
	return text;
	}
	
	// Läs en textfil till en 2-dim strängvektor med varje ord i en cell
	public static String[][] laesText (int lines, int words, String filnamn, String encoding) throws Exception
	
	{
	// BufferedReader lär vara snabbare. Ja, c:a 25 %!
	BufferedReader br = new BufferedReader(new InputStreamReader(new
	FileInputStream(filnamn), encoding));

	String[][] text = new String[lines][words];
	String rad = "";
	//int antal = 0;

	//while ((rad = br.readLine()) != null)
	for (int i = 0;i < lines; i++)
	{
		rad = br.readLine();

		if (0 == Math.round(i%100000)) System.out.println ("Tusental rader hittills: "+ i/1000);
		{
			StringTokenizer st = new StringTokenizer(rad);
			
			int j = 0;
			
			while (st.hasMoreTokens())
				{
					text[i][j++] = st.nextToken();
				}
		}
	}
	
	br.close();
	return text;
	}
	
	// Läser från en textfil och visar att något händer
	public static String laesText (String filnamn, String encoding, boolean showActivity) throws Exception
	
	{
	
	File fil = new File (filnamn); // Öppnar en fil
	
	// läser byte från fil
	FileInputStream fis = new FileInputStream (fil);
	
	// Läsare för rader etc
	// avkodar från angiven kodning
	
	Scanner fin = new Scanner (fis, encoding);
	
	String text = "";	
	String rad = "";
	
	if (showActivity)
	{
		int antal = 0;
		
		while (fin.hasNextLine())
			{
			antal++;
			rad = fin.nextLine() + "\n";
			text = text + rad;
			if (0 == Math.round(antal%1000)) System.out.println ("Rader hittills: "+ antal);
			}
	}
	else
	{
		while (fin.hasNextLine())
			{
			rad = fin.nextLine() + "\n";
			text = text + rad;
			}
	}
	
	fin.close(); // Stänger filen
	
	return text;	
	}
	
	// Räknar rader i en textfil
	public static int rader (String filnamn) throws Exception
	{
	
	/**********************************************
	
	// Denna algoritm ger en på tok för låg siffra!!
	// =============================================
	File fil = new File (filnamn); // Öppnar en fil
	
	Scanner fin = new Scanner (fil); // Binder till läsare
	
	int antal = 0;
	
	while (fin.hasNextLine())
		{
		antal++;
		fin.nextLine();
		}
	
	fin.close(); // Stänger filen
	
	*************************************************/
	
	/************************************************************
	// Nedanstående kod är långsam, men ger korrekt resultat
	// =====================================================
	BufferedReader reader = new BufferedReader(new FileReader(filnamn));
	int antal = 0;
	while (reader.readLine() != null) antal++;
	reader.close();
	*********************************************************/
	
	/*******************************************
	// Denna kod är lite snabbare
	FileReader fr = new FileReader(filnamn);
	LineNumberReader lnr = new LineNumberReader(fr);
 
	int antal = 0;
 
	while (lnr.readLine() != null)
	{
		antal++;
	}
  
	lnr.close();
	
	return antal;
	**********************************************/
	
	// Nedanstående kod är blixtsnabb!!
	// Heavily inspired by:
	// http://stackoverflow.com/questions/453018/number-of-lines-in-a-file-in-java
	// with the kind consent of Martin Ankerl martin.ankerl@gmail.com
	// "It's not completely my code, it has been modified by one or more of 
	// the stackoverflow community. But you are of course free to use it anywhere, 
	// as far as I can say. Just don't sue me or anyone because of it ;)"
	// Martin
	InputStream is = new BufferedInputStream(new FileInputStream(filnamn));
	
	//int antal = 0;
    try {
        byte[] c = new byte[1024];
        int antal = 0;
        int readChars = 0;
        boolean empty = true;
        while ((readChars = is.read(c)) != -1) {
            empty = false;
            for (int i = 0; i < readChars; ++i) {
                if (c[i] == '\n')
                    ++antal;
            }
        }
        return (antal == 0 && !empty) ? 1 : antal;
    } finally {
        is.close();
    }
	//return antal;
	}
}