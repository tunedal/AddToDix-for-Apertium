//CollectText.java

// COLLECT TEXT: 

// Läser textfiler från en katalog
// och sparar texterna i en textfil
// 
// för Apertium
// Maskinöversättning
// www.apertium.org
// ====================================== 

// Version: 0.98

//      Copyright (c) 2013 Per Tunedal, Stockholm, Sweden
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
// v. 0.96 Added cleaning of html and xml (Wordpress) files.
// v. 0.97 Added cleaning of php, and some cleaning of doc and rtf
//			Unused large variables are nulled.
// v. 0.98
// 

// -----------------------------------------------------------------------


// TODO:
// Välja Windows- eller utf-8 fil!
// Rensa även doc- och rtf-filer.
// ev. senare gå över till att istället välja ut texten.

import java.util.*; // ArrayList, Scanner, Comparator
import java.io.*; // för att skapa/öppna en fil (File)
import per.edu.*;

class CollectText

{	
	public static void main (String[] args) throws Exception
	{
	// Inmatning katalognamn
	// C:\Users\Per\tmp\corpustest
	
	String katalog = "";
		
	while (true)
		{
		katalog = Inmatning.rad("Katalog: ");
		if (katalog.length() != 0) break;
		}
	
	Utskrift.rubrik("Angiven katalog: " + katalog);
	Utskrift.skrivText("Obs! Om något dokument innehåller bilder hänger sig programmet!");
	
	// Skapar filobjekt av katalogen
	File kat = new File (katalog);

	// Skapar filobjekt från katalogen
	
	String[] katalogerFiler = kat.list();
	
	File[] fv = new File[katalogerFiler.length];
	
	int html = 0; // räknar html-filer
	int xml = 0; // räknar xml-filer
	int php = 0; // räknar php-filer
	int doc = 0; // räknar doc-filer
	int rtf = 0; // räknar rtf-filer
	
	for (int i = 0;i < fv.length; i++)
	{
		fv[i] = new File (kat, katalogerFiler[i]);
		if (FileExtension(fv[i]).matches("html")) html++;
		else if (FileExtension(fv[i]).matches("htm")) html++;
		else if (FileExtension(fv[i]).matches("xml")) xml++;
		else if (FileExtension(fv[i]).matches("php")) php++;
		else if (FileExtension(fv[i]).matches("doc")) doc++;
		else if (FileExtension(fv[i]).matches("rtf")) rtf++;
	}
	
	// Bygger doc-stop-lista om det finns rtf-filer
	String[] docStop = {""};
	if (doc > 0)
	{
	String stopDoc = makeStopList ("stop.doc.txt");
	docStop = stringToVektor (stopDoc);
	stopDoc = null; // Städar
	Arrays.sort(docStop); // sorteras på plats
	}
	
	// Bygger rtf-stop-lista om det finns rtf-filer
	String[] rtfStop = {""};
	if (rtf > 0)
	{
	String stopRtf = makeStopList ("stop.rtf.txt");
	rtfStop = stringToVektor (stopRtf);
	stopRtf = null; // Städar
	Arrays.sort(rtfStop); // sorteras på plats
	}
	
	//Utskrift.skrivText("html: " + (html > 0)  );

	// Bygger htlm-stop-lista om det finns html-filer
	// eller php-filer
	String[] htmlStop = {""};
	if (html > 0 || php > 0)
	{
	String stopHTML = makeStopList ("stop.html.txt");
	htmlStop = stringToVektor (stopHTML);
	stopHTML = null; // Städar
	Arrays.sort(htmlStop); // sorteras på plats
	}

	// Bygger xml-stop-lista om det finns xml-filer
	// (för Wordpress-filer)
	String[] xmlStop = {""};
	if (xml > 0 )
	{
	String stopXML = makeStopList ("stop.xml.txt");
	xmlStop = stringToVektor (stopXML);
	stopXML = null; // Städar
	Arrays.sort(xmlStop); // sorteras på plats
	}
	
	// Bygger php-stop-lista om det finns php-filer
	String[] phpStop = {""};
	if (php > 0)
	{
	String stopPHP = makeStopList ("stop.php.txt");
	phpStop = stringToVektor (stopPHP);
	stopPHP = null; // Städar
	Arrays.sort(phpStop); // sorteras på plats
	}
	
	// Öppna en ny fil för skrivning
	// namn = katalognamn + .txt
	
	// Måste rensa bort sökvägen!
	katalog = removePath(katalog);
	String utfil = katalog + ".txt";
	
	Utskrift.skrivText("Utfil: " + utfil);

	
	// Läs en fil i taget och
	// lägg till texten i den nya filen.
	// Stäng den nya filen.
	
	String text = "";
	// Tillåtna filändelser
	// bara textfiler (även Word etc!)
	// för html och xml måste taggar rensas bort!
	// sxw, odt och docx är komprimerade!
	String allowed = "txt|rtf|doc|htm|html|php|xml";
	//String allowed = "txt|rtf|doc";
	
	// Rensa utfilen, ifall man prövat flera gånger
	Textfil.skrivText(utfil,"");
	
	for (int i = 0;i < fv.length; i++)
	{
		if(fv[i].isFile() && FileExtension(fv[i]).matches(allowed))
		{
		Utskrift.skrivText(fv[i].getName());
		text = laesText(fv[i]);
		text = text.replaceAll("-\n", "").replaceAll("-\r\n", ""); // sätter ihop avdelade ord
	//Utskrift.skrivText("före: " + text);
		if (FileExtension(fv[i]).matches("htm") || FileExtension(fv[i]).matches("html"))
			{
				Utskrift.skrivText("Rensar html-fil");
				text = text.replaceAll("&ndash;", " - ").replaceAll("&mdash;", " - ").replaceAll("&nbsp;", " ").replaceAll("&copy;", "").replaceAll("&reg;", "");
				// lösgör ord från taggar
				//text = text.replaceAll(">", "> ").replaceAll("<", " <").replaceAll("&lt;", " ").replaceAll("&gt;", " ");
				text = text.replaceAll(">", "> ").replaceAll("<", " <").replaceAll("&lt;", " < ").replaceAll("&gt;", " > ");
				text = text.replaceAll("&lsquo;", "'").replaceAll("&rsquo;", "'").replaceAll("&quot;", "\"").replaceAll("&raquo;", "").replaceAll("&laquo;", "");
				text = text.replaceAll("&auml;", "ä").replaceAll("&aring;", "å").replaceAll("&ouml;", "ö");
				text = text.replaceAll("&aelig;", "æ").replaceAll("&oslash;", "ø");
				text = text.replaceAll("&agrave;", "à").replaceAll("&aacute;", "á").replaceAll("&acirc;", "â");
				text = text.replaceAll("&egrave;", "è").replaceAll("&eacute;", "é").replaceAll("&ecirc;", "ê").replaceAll("&euml;", "ë");
				text = text.replaceAll("&igrave;", "ì").replaceAll("&iacute;", "í").replaceAll("&icirc;", "î").replaceAll("&iuml;", "î");
				text = text.replaceAll("&ntilde;", "ñ");
				text = text.replaceAll("&ograve;", "ò").replaceAll("&oacute;", "ó").replaceAll("&ocirc;", "ô");
				text = text.replaceAll("&ugrave;", "ù").replaceAll("&uacute;", "ú").replaceAll("&ucirc;", "û").replaceAll("&uuml;", "ü");
				text = text.replaceAll("&Auml;", "Ä").replaceAll("&Aring;", "Å").replaceAll("&Ouml;", "Ö");
				text = text.replaceAll("&AElig;", "Æ").replaceAll("&Oslash;", "Ø");
				text = text.replaceAll("&Agrave;", "À").replaceAll("&Aacute;", "Á").replaceAll("&Acirc;", "Â");
				text = text.replaceAll("&Egrave;", "È").replaceAll("&Eacute;", "É").replaceAll("&Ecirc;", "Ê").replaceAll("&Euml;", "Ë");
				text = text.replaceAll("&Igrave;", "Ì").replaceAll("&Iacute;", "Í").replaceAll("&Icirc;", "Î").replaceAll("&Iuml;", "Ï");
				text = text.replaceAll("&Ograve;", "Ò").replaceAll("&Oacute;", "Ó").replaceAll("&Ocirc;", "Ô");
				text = text.replaceAll("&Ugrave;", "Ù").replaceAll("&Uacute;", "Ú").replaceAll("&Ucirc;", "Û").replaceAll("&uuml;", "Ü");
				text = text.replaceAll("&ccedil;", "ç").replaceAll("&Ccedil;", "Ç");
				
				text = text.replaceAll("&#8211;", "-").replaceAll("&#8212;", "-").replaceAll("&#160;", " ").replaceAll("&#169", "").replaceAll("&#174", "");
				text = text.replaceAll("&#039;", "'").replaceAll("&#39;", "'").replaceAll("&#60;", " ").replaceAll("&#62", " ");
				text = text.replaceAll("&#8216;", "'").replaceAll("&#8217;", "'").replaceAll("&#34", "").replaceAll("&#187", "").replaceAll("&#171", "");
				text = text.replaceAll("&#228;", "ä").replaceAll("&#229;", "å").replaceAll("&#246;", "ö");
				text = text.replaceAll("&#230;", "æ").replaceAll("&#248;", "ø");
				text = text.replaceAll("&#224;", "à").replaceAll("&#225;", "á").replaceAll("&#226;", "â");
				text = text.replaceAll("&#232;", "è").replaceAll("&#233;", "é").replaceAll("&#234;", "ê").replaceAll("&#235;", "ë");
				text = text.replaceAll("&#236;", "ì").replaceAll("&#237;", "í").replaceAll("&#238;", "î").replaceAll("&#239;", "î");
				text = text.replaceAll("&Ntilde;", "Ñ");
				text = text.replaceAll("&#242;", "ò").replaceAll("&#243;", "ó").replaceAll("&#244;", "ô");
				text = text.replaceAll("&#249;", "ù").replaceAll("&#250;", "ú").replaceAll("&#251;", "û").replaceAll("&uuml;", "ü");
				text = text.replaceAll("&#196;", "Ä").replaceAll("&#197;", "Å").replaceAll("&#214;", "Ö");
				text = text.replaceAll("&#198;", "Æ").replaceAll("&#216;", "Ø");
				text = text.replaceAll("&#192;", "À").replaceAll("&#193;", "Á").replaceAll("&#194;", "Â");
				text = text.replaceAll("&#200", "È").replaceAll("&#201;", "É").replaceAll("&#202;", "Ê").replaceAll("&#203;", "Ë");
				text = text.replaceAll("&#204;", "Ì").replaceAll("&#205;", "Í").replaceAll("&#206;", "Î").replaceAll("&#207;", "Ï");
				text = text.replaceAll("&#210;", "Ò").replaceAll("&#211;", "Ó").replaceAll("&#212;", "Ô");
				text = text.replaceAll("&#217;", "Ù").replaceAll("&#250;", "Ú").replaceAll("&#251;", "Û").replaceAll("&#252;", "Ü");
							
				text = text.replaceAll("alt=\"", " "); // tar vara på alt-texter, men ej content.
				//text = text.replaceAll("-\n", "").replaceAll("-\r\n", "").replaceAll("&amp;", "").replaceAll("\";", "\"");
				//text = text.replaceAll("-\n", "").replaceAll("-\r\n", ""); // slå ihop avdelade ord
				text = text.replaceAll("&amp;", "&").replaceAll("&#38", "&").replaceAll("&#038", "&");
				
				text = rensaStringHTML (text, htmlStop); // slutrensning mha stopp-lista
				text = text.replaceAll("- ", ""); // sätter ihop avdelade ord
				
				//Utskrift.skrivText("rensad HTML: " + text);
			}
			
					if (FileExtension(fv[i]).matches("php"))
			{
				Utskrift.skrivText("Rensar php-fil");
				text = text.replaceAll("&ndash;", " - ").replaceAll("&mdash;", " - ").replaceAll("&nbsp;", " ").replaceAll("&copy;", "").replaceAll("&reg;", "");
				// lösgör ord från taggar
				//text = text.replaceAll(">", "> ").replaceAll("<", " <").replaceAll("&lt;", " ").replaceAll("&gt;", " ");
				text = text.replaceAll(">", "> ").replaceAll("<", " <").replaceAll("&lt;", " < ").replaceAll("&gt;", " > ");
				text = text.replaceAll("&lsquo;", "'").replaceAll("&rsquo;", "'").replaceAll("&quot;", "\"").replaceAll("&raquo;", "").replaceAll("&laquo;", "");
				text = text.replaceAll("&auml;", "ä").replaceAll("&aring;", "å").replaceAll("&ouml;", "ö");
				text = text.replaceAll("&aelig;", "æ").replaceAll("&oslash;", "ø");
				text = text.replaceAll("&agrave;", "à").replaceAll("&aacute;", "á").replaceAll("&acirc;", "â");
				text = text.replaceAll("&egrave;", "è").replaceAll("&eacute;", "é").replaceAll("&ecirc;", "ê").replaceAll("&euml;", "ë");
				text = text.replaceAll("&igrave;", "ì").replaceAll("&iacute;", "í").replaceAll("&icirc;", "î").replaceAll("&iuml;", "î");
				text = text.replaceAll("&ntilde;", "ñ");
				text = text.replaceAll("&ograve;", "ò").replaceAll("&oacute;", "ó").replaceAll("&ocirc;", "ô");
				text = text.replaceAll("&ugrave;", "ù").replaceAll("&uacute;", "ú").replaceAll("&ucirc;", "û").replaceAll("&uuml;", "ü");
				text = text.replaceAll("&Auml;", "Ä").replaceAll("&Aring;", "Å").replaceAll("&Ouml;", "Ö");
				text = text.replaceAll("&AElig;", "Æ").replaceAll("&Oslash;", "Ø");
				text = text.replaceAll("&Agrave;", "À").replaceAll("&Aacute;", "Á").replaceAll("&Acirc;", "Â");
				text = text.replaceAll("&Egrave;", "È").replaceAll("&Eacute;", "É").replaceAll("&Ecirc;", "Ê").replaceAll("&Euml;", "Ë");
				text = text.replaceAll("&Igrave;", "Ì").replaceAll("&Iacute;", "Í").replaceAll("&Icirc;", "Î").replaceAll("&Iuml;", "Ï");
				text = text.replaceAll("&Ograve;", "Ò").replaceAll("&Oacute;", "Ó").replaceAll("&Ocirc;", "Ô");
				text = text.replaceAll("&Ugrave;", "Ù").replaceAll("&Uacute;", "Ú").replaceAll("&Ucirc;", "Û").replaceAll("&uuml;", "Ü");
				text = text.replaceAll("&ccedil;", "ç").replaceAll("&Ccedil;", "Ç");
				
				text = text.replaceAll("&#8211;", "-").replaceAll("&#8212;", "-").replaceAll("&#160;", " ").replaceAll("&#169", "").replaceAll("&#174", "");
				text = text.replaceAll("&#039;", "'").replaceAll("&#39;", "'").replaceAll("&#60;", " ").replaceAll("&#62", " ");
				text = text.replaceAll("&#8216;", "'").replaceAll("&#8217;", "'").replaceAll("&#34", "").replaceAll("&#187", "").replaceAll("&#171", "");
				text = text.replaceAll("&#228;", "ä").replaceAll("&#229;", "å").replaceAll("&#246;", "ö");
				text = text.replaceAll("&#230;", "æ").replaceAll("&#248;", "ø");
				text = text.replaceAll("&#224;", "à").replaceAll("&#225;", "á").replaceAll("&#226;", "â");
				text = text.replaceAll("&#232;", "è").replaceAll("&#233;", "é").replaceAll("&#234;", "ê").replaceAll("&#235;", "ë");
				text = text.replaceAll("&#236;", "ì").replaceAll("&#237;", "í").replaceAll("&#238;", "î").replaceAll("&#239;", "î");
				text = text.replaceAll("&Ntilde;", "Ñ");
				text = text.replaceAll("&#242;", "ò").replaceAll("&#243;", "ó").replaceAll("&#244;", "ô");
				text = text.replaceAll("&#249;", "ù").replaceAll("&#250;", "ú").replaceAll("&#251;", "û").replaceAll("&uuml;", "ü");
				text = text.replaceAll("&#196;", "Ä").replaceAll("&#197;", "Å").replaceAll("&#214;", "Ö");
				text = text.replaceAll("&#198;", "Æ").replaceAll("&#216;", "Ø");
				text = text.replaceAll("&#192;", "À").replaceAll("&#193;", "Á").replaceAll("&#194;", "Â");
				text = text.replaceAll("&#200", "È").replaceAll("&#201;", "É").replaceAll("&#202;", "Ê").replaceAll("&#203;", "Ë");
				text = text.replaceAll("&#204;", "Ì").replaceAll("&#205;", "Í").replaceAll("&#206;", "Î").replaceAll("&#207;", "Ï");
				text = text.replaceAll("&#210;", "Ò").replaceAll("&#211;", "Ó").replaceAll("&#212;", "Ô");
				text = text.replaceAll("&#217;", "Ù").replaceAll("&#250;", "Ú").replaceAll("&#251;", "Û").replaceAll("&#252;", "Ü");
					
				text = text.replaceAll("'", " ' "); // frigör taggar
				text = text.replaceAll(",", " , "); // frigör taggar
				//text = text.replaceAll(".", ".  "); // frigör meningar FEL ger bara punkter!
				
				text = text.replaceAll("alt=\"", " "); // tar vara på alt-texter, men ej content.
				//text = text.replaceAll("-\n", "").replaceAll("-\r\n", "").replaceAll("&amp;", "").replaceAll("\";", "\"");
				//text = text.replaceAll("-\n", "").replaceAll("-\r\n", ""); // slå ihop avdelade ord
				text = text.replaceAll("&amp;", "&").replaceAll("&#38", "&").replaceAll("&#038", "&");
				
				text = rensaStringPHP (text, phpStop); // PHP-specific rensning
				
				text = rensaStringHTML (text, htmlStop); // slutrensning mha stopp-lista
				text = text.replaceAll("- ", ""); // sätter ihop avdelade ord
				
				text = text.replaceAll(" ,", ","); // placerar komman rätt
				
				//Utskrift.skrivText("rensad PHP: " + text);
			}
			
		if (FileExtension(fv[i]).matches("xml"))
			{
				Utskrift.skrivText("Rensar xml-fil");
				text = text.replaceAll("&ndash;", " - ").replaceAll("&mdash;", " - ").replaceAll("&nbsp;", " ").replaceAll("&copy;", "").replaceAll("&reg;", "");
				// lösgör ord från taggar
				//text = text.replaceAll(">", "> ").replaceAll("<", " <").replaceAll("&lt;", " ").replaceAll("&gt;", " ");
				text = text.replaceAll(">", "> ").replaceAll("<", " <").replaceAll("&lt;", " < ").replaceAll("&gt;", " > ");

				text = text.replaceAll("alt=\"", " "); // tar vara på alt-texter, men ej content.
				//text = text.replaceAll("-\n", "").replaceAll("-\r\n", "").replaceAll("&amp;", "").replaceAll("\";", "\"");
				//text = text.replaceAll("-\n", "").replaceAll("-\r\n", ""); // slå ihop avdelade ord
				text = text.replaceAll("&amp;", "&").replaceAll("&#38", "&").replaceAll("&#038", "&");
				
				text = rensaStringXML (text, xmlStop); // slutrensning mha stopp-lista
				text = text.replaceAll("- ", ""); // sätter ihop avdelade ord
				
				//Utskrift.skrivText("rensad XML: " + text);
			}
			
			if (FileExtension(fv[i]).matches("doc"))
			{
				// OBS! Denna rensning funkar INTE av någon anledning!
				// Stopplistan funkar inte.
				// ===================================================
				Utskrift.skrivText("Rensar doc-fil");
				//text = text.replaceAll("&ndash;", " - ").replaceAll("&mdash;", " - ").replaceAll("&nbsp;", " ").replaceAll("&copy;", "").replaceAll("&reg;", "");
				// lösgör ord från taggar
				//text = text.replaceAll(">", "> ").replaceAll("<", " <").replaceAll("&lt;", " ").replaceAll("&gt;", " ");
				//text = text.replaceAll(">", "> ").replaceAll("<", " <").replaceAll("&lt;", " < ").replaceAll("&gt;", " > ");

				//text = text.replaceAll("alt=\"", " "); // tar vara på alt-texter, men ej content.
				//text = text.replaceAll("-\n", "").replaceAll("-\r\n", "").replaceAll("&amp;", "").replaceAll("\";", "\"");
				//text = text.replaceAll("-\n", "").replaceAll("-\r\n", ""); // slå ihop avdelade ord
				//text = text.replaceAll("&amp;", "&").replaceAll("&#38", "&").replaceAll("&#038", "&");
				
				//text = rensaStringXML (text, docStop); // slutrensning mha stopp-lista
				text = text.replace("\\'f6","ö").replace("\\'e4","ä").replace("\\'e5","å"); // å, ä, ö
				text = text.replace("\\'c4","ä").replace("\\'c5","å"); // å, ä, ö
				text = text.replace("\\'e9","é"); // mosk\'e9n
				text = text.replace("}"," }"); // frigör ord
				//text = text.replace("\-}","\- }"); // frigör ord
				
				text = text.replace("\\-", ""); // sätter ihop avdelade ord \-
				text = rensaStringXML (text, docStop); // slutrensning mha stopp-lista
				//Utskrift.skrivText("rensad: " + text);
			}
			
			if (FileExtension(fv[i]).matches("rtf"))
			{
				// OBS! Denna rensning funkar NÄSTAN perfekt!
				// Stopplistan funkar med några få undantag.
				// ==========================================
				Utskrift.skrivText("Rensar rtf-fil");
				//text = text.replaceAll("&ndash;", " - ").replaceAll("&mdash;", " - ").replaceAll("&nbsp;", " ").replaceAll("&copy;", "").replaceAll("&reg;", "");
				// lösgör ord från taggar
				//text = text.replaceAll(">", "> ").replaceAll("<", " <").replaceAll("&lt;", " ").replaceAll("&gt;", " ");
				//text = text.replaceAll(">", "> ").replaceAll("<", " <").replaceAll("&lt;", " < ").replaceAll("&gt;", " > ");

				//text = text.replaceAll("alt=\"", " "); // tar vara på alt-texter, men ej content.
				//text = text.replaceAll("-\n", "").replaceAll("-\r\n", "").replaceAll("&amp;", "").replaceAll("\";", "\"");
				//text = text.replaceAll("-\n", "").replaceAll("-\r\n", ""); // slå ihop avdelade ord
				//text = text.replaceAll("&amp;", "&").replaceAll("&#38", "&").replaceAll("&#038", "&");

				text = text.replace("\\endash","–"); // 1590\endash 1600 dvs. 1590–1600
				text = text.replace("\\'f6","ö").replace("\\'e4","ä").replace("\\'e5","å"); // å, ä, ö
				text = text.replace("\\'c4","ä").replace("\\'c5","å"); // å, ä, ö
				text = text.replace("\\'e9","é"); // mosk\'e9n
				text = text.replace("\\'94"," "); // frigör formattering från ord.
				text = text.replace("\\"," \\"); // frigör formattering från ord.
				text = text.replace("}"," }").replace("{","{ "); // frigör ord
				

				//text = rensaStringXML (text, rtfStop); // slutrensning mha stopp-lista
				text = text.replace("\\-", ""); // sätter ihop avdelade ord \-
				text = rensaStringXML (text, rtfStop); // slutrensning mha stopp-lista
				
				//Utskrift.skrivText("rensad RTF: " + text);
			}
			
		//Utskrift.skrivText("efter: " + text);
		Textfil.addText(utfil, text);
		}
	}
	
	// Texterna nu samlade i den nya filen
	Utskrift.skrivText("KLART\nAll text har skrivits till filen: " + utfil);

	}
	
	// Rensar bort sökvägen
	//String katalog = removePath(katalog)
	
	public static String removePath (String katalogEllerFil) throws Exception
	{
	// StringTokenizer lämnar inte extra blanka
	// mellan ord.
		
	StringTokenizer st = new StringTokenizer(katalogEllerFil,"\\");
  
	// Söker sista delen av strängen
	
	int len = st.countTokens();
	
  String [] parts = new String[len];
	
	int i = 0;
	
	while (st.hasMoreTokens())
		{
			parts[i++] = st.nextToken();
		}
		
	String s = parts[len - 1];
	
	return s;	
		
	}
	
	// Läs från en textfil (filobjekt)	
	public static String laesText (File fil) throws Exception
	
	{
	
	//File fil = new File (filnamn); // Öppnar en fil
	
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
	
	// Ta fram filändelsen för ett filobject	
	public static String FileExtension(File fil) throws Exception
	{
		String filnamn = fil.getName();
		
		// StringTokenizer lämnar inte extra blanka
		// mellan ord.
			
		StringTokenizer st = new StringTokenizer(filnamn,".");
	  
		String extension = "";
		
		while (st.hasMoreTokens())
			{
				extension = st.nextToken();
			}
			
		return extension;
	}
	// Läs en stoppliste-fil till en sträng
	public static String makeStopList (String filename) throws Exception
	{
		Utskrift.skrivText("Vänta - gör lista på stopp-ord");
		
		String rad = "";
		int antal = 0;
				
		FileInputStream fs = new FileInputStream(filename); // byte till character
		//Windowsfil
		Scanner fin = new Scanner (fs, "Cp1252"); // rätt tecken!
		
		while (fin.hasNextLine())
		{
			rad = rad + fin.nextLine() + " ";
			//System.out.println(".");
			antal++;
			//System.out.println(antal);
			if (0 == Math.round(antal%1000)) Utskrift.skrivText ("Läst "+ antal);
		}
	
		fin.close(); // Stänger filen
				
		return rad;
	}
	
		//Ord kopieras  från sträng till vektor
		
	public static String[] stringToVektor (String text) throws Exception
	{
	// StringTokenizer lämnar inte extra blanka
	// mellan ord.
		
	StringTokenizer st = new StringTokenizer(text," ");
  
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
	
		//Sträng rensas från ord i stopp-lista (för HTML)
	public static String rensaStringHTML (String text, String[] stop) throws Exception
	{
	// StringTokenizer lämnar inte extra blanka
	// mellan ord.
	
	//text.replaceAll("\n", " ");
		
	//StringTokenizer st = new StringTokenizer(text," ");
	StringTokenizer st = new StringTokenizer(text);
  
			
	String rensad = "";
	String ord = "";
	String forbid2 = "a=|b=|c=|d=|e="; // regexp
	String forbid3 = "MM_|li.|<!-|<w:|<o:|<m:"; // regexp
	String forbid4 = "rel=|src=|id=.|for=|mso-|top:|url=|div.|DefQ|DefP|DefL|</w:|</o:|</m:|dir="; // regexp
	String forbid5 = "href=|type=|face=|size=|http:|rows=|lang=|name=|Name=|text=|cols=|span.|p.Mso|Math.|goon="; // regexp
	String forbid6 = "title=|align=|value=|style=|scope=|color=|\"http:|class=|width=|vlink=|m:val=|media=|color:|v:ext=|xmlns=|clear=|alink=|xmlns:|float:|width:|shape=|wmode="; // regexp
	String forbid7 = "valign=|target=|border=|height=|locked=|Locked=|method=|margin:|panose-|format:|DefSemi|params.|action=|'splash|dateEnd|scheme=|onblur=|filter:"; // regexp
	String forbid8 = "colspan=|summary=|bgcolor=|onclick=|qformat=|QFormat=|charset=|padding:|version=|content=|Strict//|classid=|onfocus=|enctype=|rowspan="; // regexp
	String forbid9 = "itemtype=|itemprop=|priority=|Priority=|onsubmit=|language=|encoding=|this.href|onSubmit=|document.|DefUnhide|overflow:|position:|accesskey="; // regexp
	String forbid10 = "font-size:|trackview=|@font-face|navigator.|swfobject.|newwindow=|accesskey=|resizable="; // regexp
	String forbid11 = "semihidden=|SemiHidden=|http-equiv=|pageTracker|scrollbars="; // regexp
	String forbid12 = "cellpadding=|cellspacing=|font-weight:|font-family:|ispermalink=|ispermalink|isPermaLink=|margin-right|margin-left:|margin-botto|marginwidth=|bordercolor=|marginheight"; // regexp
	String forbid14 = "maximum-scale=|initial-scale=|onselectstart=|marginheight=\""; // regexp
	String forbid15 = "unhidewhenused=|UnhideWhenUsed=|text-underline:|text-decoration|splashScreen.lo|navigator.userA|text-transform:|ChangeDetection|background-imag"; // regexp
	
	// Bättre : startsWith och endsWith!!
	
	while (st.hasMoreTokens())
		{
			ord = st.nextToken();
			int len = ord.length();
			//Utskrift.skrivText("Ord: " + ord);
			
			if (len>= 2 && ord.substring(0,2).matches(forbid2))
			{}
			if (len>= 3 && ord.substring(0,3).matches(forbid3))
			{}
			else if (len>= 4 && ord.substring(0,4).matches(forbid4))
			{}
			else if (len>= 5 && ord.substring(0,5).matches(forbid5))
			{}
			else if (len >= 6 && ord.substring(0,6).matches(forbid6))
			{}
			else if (len >= 7 && ord.substring(0,7).matches(forbid7))
			{}
			else if (len >= 8 && ord.substring(0,8).matches(forbid8))
			{}
			else if (len >= 9 && ord.substring(0,9).matches(forbid9))
			{}
			else if (len >= 10 && ord.substring(0,10).matches(forbid10))
			{}
			else if (len >= 11 && ord.substring(0,11).matches(forbid11))
			{}
			else if (len >= 12 && ord.substring(0,12).matches(forbid12))
			{}
			else if (len >= 14 && ord.substring(0,14).matches(forbid14))
			{}
			else if (len >= 15 && ord.substring(0,15).matches(forbid15))
			{}
			else if (len>= 3 && ord.contains("twttr.")) // twttr.
			{}
			else if (len>= 3 && ord.contains("]]>"))
			{}
			else if (len>= 3 && ord.contains("px;"))
			{}
			else if (len>= 3 && ord.contains("\"/>"))
			{}
			else if (len>= 3 && ord.contains("\"'>"))
			{}
			else if (len>= 2 && ord.contains("<!"))
			{}
			else if (len>= 2 && ord.contains("+="))
			{}
			else if (len>= 2 && ord.contains("\"\"")) // ""
			{}
			else if (len>= 2 && ord.contains("&&")) // &&
			{}
			else if (len>= 2 && ord.contains("}}")) // }}
			{}
			else if (len>= 2 && ord.contains("()")) // ()
			{}
			else if (len>= 2 && ord.contains("._")) // ._
			{}
			else if (len>= 2 && ord.startsWith("<"))
			{}
			else if (ord.startsWith("#"))
			{}
			else if (ord.startsWith("\"#"))
			{}
			else if (ord.startsWith("{behavior:"))
			{}
			else if (ord.startsWith("{mso-style"))
			{}
			else if (ord.startsWith("{size:"))
			{}
			else if (ord.startsWith("{page:"))
			{}
			else if (ord.startsWith("{font-family:"))
			{}
			else if (ord.startsWith("flashFile["))
			{}
			else if (ord.startsWith("Flash\"]"))
			{}
			else if (ord.startsWith("(navigator."))
			{}
			else if (ord.startsWith("'splash"))
			{}
			else if (ord.startsWith("'http:"))
			{}
			else if (ord.startsWith("(url."))
			{}
			else if (ord.startsWith("((navigator."))
			{}
			else if (ord.startsWith("url("))
			{}
			else if (ord.startsWith("categ_"))
			{}
			else if (ord.startsWith("data["))
			{}
			else if (ord.startsWith("$(")) // $('
			{}
			else if (ord.startsWith("RegExp(")) // RegExp(
			{}
			else if (ord.startsWith("rgb(")) // rgb(
			{}
			else if (ord.startsWith("rgba(")) // rgba(
			{}
			else if (ord.startsWith("alpha(")) // alpha(
			{}
			else if (ord.startsWith("s.parent")) // s.parent
			{}
			else if (ord.startsWith("twttr.")) // twttr.
			{}
			else if (ord.startsWith("str.")) // str.
			{}
			else if (ord.startsWith("parseInt(")) // parseInt(
			{}
			else if (ord.startsWith("//")) // //
			{}
			else if (ord.endsWith("]>")) // ">
			{}
			else if (ord.endsWith("\">")) // ">
			{}
			else if (ord.endsWith("\";")) // ";
			{}
			else if (ord.endsWith("'};")) // '};
			{}
			else if (ord.endsWith("});")) // });
			{}
			else if (ord.endsWith(" );")) //  ); (blank först)
			{}
			else if (ord.endsWith("');")) //  ');
			{}
			else if (ord.endsWith(");\"")) //  );"
			{}
			else if (ord.endsWith(");\"")) //  );"
			{}
			else if (ord.endsWith("\");")) //  ");
			{}
			else if (ord.endsWith("();")) //  ();;
			{}
			else if (ord.endsWith("url);")) //  url);
			{}
			else if (ord.endsWith("));")) //  ));
			{}
			else if (ord.endsWith(");var")) //  );var
			{}
			else if (ord.endsWith(");return")) //  );return
			{}
			//else if (ord.endsWith(");") && !ord.contains(".")) // );
			//{}
			else if (ord.endsWith(";}")) // ;}
			{}
			else if (ord.endsWith(".jpg\"")) // .jpg"
			{}
			else if (ord.endsWith(".gif\"")) // .gif"
			{}
			else if (ord.endsWith("//EN\"")) // //EN"
			{}
			else if (Arrays.binarySearch (stop, ord) >= 0)
			{}
			else
			rensad = rensad + ord + " ";
		}
		
	return rensad;	
		
	}

	//Sträng rensas från taggar (för PHP)
	public static String rensaStringPHP (String text, String[] stop) throws Exception
	{
	// Mesta rensningen sker med rensningen för HTML!
	// StringTokenizer lämnar inte extra blanka
	// mellan ord.
	
	//text.replaceAll("\n", " ");
		
	//StringTokenizer st = new StringTokenizer(text," ");
	StringTokenizer st = new StringTokenizer(text);
  
			
	String rensad = "";
	String ord = "";
	
	String forbid5 = "TYPE=|NAME=|SIZE=|FACE="; // regexp
	String forbid6 = "VALUE="; // regexp
	String forbid7 = "METHOD=|ACTION="; // regexp
	
	/***********************************************
	String forbid2 = "b="; // regexp
	String forbid3 = "MM_|li.|<!-|<w:|<o:|<m:"; // regexp
	String forbid4 = "rel=|src=|id=.|for=|mso-|top:|url=|div.|DefQ|DefP|DefL|</w:|</o:|</m:|dir="; // regexp
	String forbid5 = "href=|type=|face=|size=|http:|rows=|lang=|name=|Name=|text=|cols=|span.|p.Mso|Math.|goon="; // regexp
	String forbid6 = "title=|align=|value=|style=|scope=|color=|\"http:|class=|width=|vlink=|m:val=|media=|color:|v:ext=|xmlns=|clear=|alink=|xmlns:|float:|width:|shape=|wmode="; // regexp
	String forbid7 = "valign=|target=|border=|height=|locked=|Locked=|method=|margin:|panose-|format:|DefSemi|params.|action=|'splash|dateEnd|scheme=|onblur="; // regexp
	String forbid8 = "colspan=|summary=|bgcolor=|onclick=|qformat=|QFormat=|charset=|padding:|version=|content=|Strict//|classid=|onfocus=|enctype=|rowspan="; // regexp
	String forbid9 = "itemtype=|itemprop=|priority=|Priority=|onsubmit=|language=|encoding=|this.href|onSubmit=|document.|DefUnhide|overflow:|position:|accesskey="; // regexp
	String forbid10 = "font-size:|trackview=|@font-face|navigator.|swfobject.|newwindow=|accesskey="; // regexp
	String forbid11 = "semihidden=|SemiHidden=|http-equiv=|pageTracker"; // regexp
	String forbid12 = "cellpadding=|cellspacing=|font-weight:|font-family:|ispermalink=|ispermalink|isPermaLink=|margin-right|margin-left:|margin-botto|marginwidth=|bordercolor=|marginheight"; // regexp
	String forbid14 = "maximum-scale=|initial-scale=|onselectstart=|marginheight=\""; // regexp
	String forbid15 = "unhidewhenused=|UnhideWhenUsed=|text-underline:|text-decoration|splashScreen.lo|navigator.userA|text-transform:"; // regexp
	**************************************************/
	
	// Bättre : startsWith och endsWith!!
	
	while (st.hasMoreTokens())
		{
			ord = st.nextToken();
			int len = ord.length();
			
			if (len >= 5 && ord.substring(0,5).matches(forbid5))
			{}
			else if (len >= 6 && ord.substring(0,6).matches(forbid6))
			{}
			else if (len >= 7 && ord.substring(0,7).matches(forbid7))
			{}

			/****************************************************
			if (len >= 2 && ord.substring(0,2).matches(forbid2))
			{}
			if (len >= 3 && ord.substring(0,3).matches(forbid3))
			{}
			else if (len >= 4 && ord.substring(0,4).matches(forbid4))
			{}
			else if (len >= 5 && ord.substring(0,5).matches(forbid5))
			{}
			else if (len >= 6 && ord.substring(0,6).matches(forbid6))
			{}
			else if (len >= 7 && ord.substring(0,7).matches(forbid7))
			{}
			else if (len >= 8 && ord.substring(0,8).matches(forbid8))
			{}
			else if (len >= 9 && ord.substring(0,9).matches(forbid9))
			{}
			else if (len >= 10 && ord.substring(0,10).matches(forbid10))
			{}
			else if (len >= 11 && ord.substring(0,11).matches(forbid11))
			{}
			else if (len >= 12 && ord.substring(0,12).matches(forbid12))
			{}
			else if (len >= 14 && ord.substring(0,14).matches(forbid14))
			{}
			else if (len >= 15 && ord.substring(0,15).matches(forbid15))
			{}
			else if (len>= 3 && ord.contains("]]>"))
			{}
			else if (len>= 3 && ord.contains("px;"))
			{}
			******************************************************/
			
			else if (ord.startsWith("<?"))
			{}
			else if (ord.startsWith("include"))
			{}
			else if (ord.startsWith("$meta"))
			{}
			else if (ord.startsWith(");"))
			{}
			else if (ord.startsWith("$meny"))
			{}
			else if (ord.startsWith("$validated"))
			{}
			else if (ord.startsWith("start(\""))
			{}
			else if (ord.startsWith("?>"))
			{}
			else if (ord.startsWith("method="))
			{}
			else if (ord.startsWith("onClick="))
			{}
			else if (ord.startsWith(");return"))
			{}
			else if (ord.startsWith("slut();"))
			{}
			else if (ord.startsWith("array("))
			{}
			else if (ord.startsWith("ruta("))
			{}

/***************************************************************
			else if (ord.endsWith("]>")) // ">
			{}
			else if (ord.endsWith("\">")) // ">
			{}
			else if (ord.endsWith("\";")) // ";
			{}
			else if (ord.endsWith(");")) // );
			{}
			else if (ord.endsWith(";}")) // ;}
			{}
			else if (ord.endsWith(".jpg\"")) // .jpg"
			{}
			else if (ord.endsWith(".gif\"")) // .gif"
			{}
			else if (ord.endsWith("//EN\"")) // //EN"
			{}
*******************************************************************/
			else if (Arrays.binarySearch (stop, ord) >= 0)
			{}

			else
			rensad = rensad + ord + " ";
		}
		
	return rensad;	
		
	}
		
	//Sträng rensas från ord i stopp-lista (för XML)
	public static String rensaStringXML (String text, String[] stop) throws Exception
	{
	// StringTokenizer lämnar inte extra blanka
	// mellan ord.
	
	//text.replaceAll("\n", " ");
		
	//StringTokenizer st = new StringTokenizer(text," ");
	StringTokenizer st = new StringTokenizer(text);
  
			
	String rensad = "";
	String ord = "";
	//String forbid2 = "b="; // regexp
	String forbid3 = "_wp|id="; // regexp
	String forbid4 = "<wp:|</wp|rel="; // regexp
	String forbid5 = "sort=|href=|role=|alpha=|list="; // regexp
	String forbid6 = "title=|xmlns:|width="; // regexp
	String forbid7 = "search=|target=|domain="; // regexp
	String forbid8 = "created=|version=|message="; // regexp
	String forbid9 = "nicename=|encoding="; // regexp
	String forbid10 = "_MailPress|generator="; // regexp
	String forbid11 = "pagination="; // regexp
	String forbid12 = "isPermaLink=|pagination2="; // regexp
	/********************************************
	String forbid14 = "maximum-scale=|initial-scale=|onselectstart=|marginheight=\""; // regexp
	String forbid15 = "unhidewhenused=|UnhideWhenUsed=|text-underline:|text-decoration|splashScreen.lo|navigator.userA|text-transform:"; // regexp
	*****************************************/
	
	// Bättre : startsWith och endsWith!!
	
	while (st.hasMoreTokens())
		{
			ord = st.nextToken();
			int len = ord.length();
			
			//if (len>= 2 && ord.substring(0,2).matches(forbid2))
			{}
			if (len>= 3 && ord.substring(0,3).matches(forbid3))
			{}
			else if (len>= 4 && ord.substring(0,4).matches(forbid4))
			{}
			else if (len>= 5 && ord.substring(0,5).matches(forbid5))
			{}
			else if (len >= 6 && ord.substring(0,6).matches(forbid6))
			{}
			else if (len >= 7 && ord.substring(0,7).matches(forbid7))
			{}
			else if (len >= 8 && ord.substring(0,8).matches(forbid8))
			{}
			else if (len >= 9 && ord.substring(0,9).matches(forbid9))
			{}
			else if (len >= 10 && ord.substring(0,10).matches(forbid10))
			{}
			else if (len >= 11 && ord.substring(0,11).matches(forbid11))
			{}
			else if (len >= 12 && ord.substring(0,12).matches(forbid12))
			{}
			/****************************************		
			else if (len >= 14 && ord.substring(0,14).matches(forbid14))
			{}
			else if (len >= 15 && ord.substring(0,15).matches(forbid15))
			{}
			else if (len>= 3 && ord.contains("px;"))
			{}
			else if (len>= 3 && ord.contains("\"/>"))
			{}
			else if (len>= 3 && ord.contains("\"'>"))
			{}
			else if (len>= 3 && ord.contains("<!"))
			{}
			else if (ord.startsWith("{behavior:"))
			{}
			else if (ord.startsWith("{mso-style"))
			{}
			else if (ord.startsWith("{size:"))
			{}
			else if (ord.startsWith("{page:"))
			{}
			else if (ord.startsWith("{font-family:"))
			{}
			else if (ord.startsWith("flashFile["))
			{}
			else if (ord.startsWith("Flash\"]"))
			{}
			else if (ord.startsWith("(navigator."))
			{}
			else if (ord.startsWith("'splash"))
			{}
			else if (ord.startsWith("'http:"))
			{}
			else if (ord.startsWith("(url."))
			{}
			else if (ord.startsWith("((navigator."))
			{}
			else if (ord.startsWith("url("))
			{}
			else if (ord.startsWith("categ_"))
			{}
			else if (ord.startsWith("data["))
			{}
			else if (ord.endsWith("]>")) // ">
			{}
			else if (ord.endsWith("\">")) // ">
			{}
			else if (ord.endsWith("\";")) // ";
			{}
			else if (ord.endsWith(");")) // );
			{}
			else if (ord.endsWith(";}")) // ;}
			{}
			else if (ord.endsWith(".jpg\"")) // .jpg"
			{}
			else if (ord.endsWith(".gif\"")) // .gif"
			{}
			else if (ord.endsWith("//EN\"")) // //EN"
			{}
			************************************/
			else if (len>= 3 && ord.contains("]]>"))
			{}
			else if (len>= 3 && ord.contains("<!"))
			{}
			else if (ord.endsWith(".gif")) // .gif"
			{}
			else if (ord.endsWith(".jpg")) // .jpg"
			{}
			else if (Arrays.binarySearch (stop, ord) >= 0)
			{}
			else
			rensad = rensad + ord + " ";
		}
		
	return rensad;	
		
	}
}