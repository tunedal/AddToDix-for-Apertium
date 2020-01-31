Read me på Svenska

Instruktioner
=============

Dessa små enkla Java-program är tänkta
att underlätta för dig som vill lägga till
nya ord till ett Apertium-språkpar,
tex Svenska - Danska (sv-da).

Den största fördelen är att du slipper skriva xml-kod
och göra triviala misstag, som inte alls har med språk
och översättning att göra. Dessutom underlättar programmen
genom att automatiskt kontrollera olika saker och presentera
alternativ när du t.ex. ska ange paradigm (böjningsmönster).

Använd dessa program för att göra tilläggsförslag till
ordlistorna. Sedan skickar du dem till resp. språkutvecklare,
som lägger till orden till den officiella ordlistan. 
Eller gör det själv om du blivit utvecklare för språket.

Utgångspunkt:
Det är svårt och kräver 10 000-tals nya ord om man ska
täcka in alla tänkbara sorters texter. När man totalt har kanske
50 - 60 000 ord i ordlistorna kanske man når c:a 80 % täckning
när man översätter.

Det är mycket lättare att få en hög täckning, ja t.o.m. över
90 % för en viss typ av texter, låt oss kalla det en domän.
Om du är intresserad av något visst område, 
det må vara daggmaskar, näverslöjd, filosofi, knyppling, 
förskolepedagogik eller något annat, 
kan du lägga till ord inom det området och snabbt få
en fungerande översättningshjälp.

Principen är att lägga in de vanligaste orden först. Det ger
snabbt en förbättring av översättningen. Därför börjar 
man med att göra en frekvenslista utifrån en korpus dvs. en samling 
texter. Sedan lägger man till orden i frekvensordning.

Gör så här:

1. Installera programmen genom att packa upp dem i en katalog 
där du har skrivrättigheter, t.ex. någonstans under 
"Mina dokument" eller under ditt användarnamn.
Du kör programmen genom att öppna kommandotolken ("DOS-fönstret"),
bläddra till programkatalogen och skriva "java" + programmets namn, tex:
java OrdFrekvens

2. Med programmen följer de officiella ordlistorna:
sv-da.da.dix
sv-da.sv-da.dix
sv-da.sv.dix

De behöver sannolikt uppdateras. Ladda därför ned de senast från Apertium.
Se Apertiums hemsida: www.apertium.org
Utgivna språkpar finns under apertium-trunk.
Kopiera sedan den senaste versionen till mappen där du packat upp
AddToDix, så att de gamla versionerna skrivs över.

3. Översätt FRÅN det språk du kan sämst TILL det språk du kan bäst 
(t.ex. ditt modersmål). Jag kallar nedan språket du översätter 
från för källspråk och språket du översätter till för målspråk. 

4. Samla först så många texter som möjligt (skrivna på källspråket) 
inom den domän du vill arbeta med. Lägg dem sedan i en textfil, 
och spara den i samma mapp som programmen. (Använd t.ex. programmet 
Anteckningar: Start - Alla program - Tillbehör - Anteckningar)
Har du filer med lämpliga texter? Då kan du kopiera textfiler, 
rtf-filer och gamla wordfiler (.doc) till en mapp och sedan köra programmet 
CollectText för att samla texterna i en textfil. Filen får samma namn 
som mappen och ändelsen ".txt".

5. Kör programmet OrdFrekvens.java för att få en lista på alla ord
 i textfilen, listade i frekvensordning dvs. de vanligaste orden först.
Ord som redan finns i ordlistorna ska i stort sett vara bortrensade, 
liksom skräptecken. Listan heter "Frekvens. + DinTextfil"
(t.ex. Frekvens.DanishTowns.txt) och finns i samma katalog som programmen.

6. Du ska nu lägga in orden i frekvensordning. Genom att du lägger 
in de vanligaste orden först, får du snabbt nytta av ditt arbete.

7. Börja med att lägga in ord i den enspråkiga ordlistan för källspråket 
med programmet AddToDictionnary.java
Programmet kollar att orden inte redan finns i ordlistan, så du inte 
gör något onödigt arbete. (Du behöver alltså inte heller söka igenom 
ordlistorna för se om ordet finns - programmet gör jobbet!) 
Spara filen när du är klar.

8. Lägg sedan in översättningen av ordet i den tvåspråkiga ordlistan 
med programmet AddToBidixFromMonodix.java Programmet läser in orden från den 
enspråkiga ordlistan du skapat tidigare, så att du bara behöver
lägga till information om översättningen mm. Spara filen när du är klar.

9. Lägg slutligen till ord i den enspråkiga ordlistan för målspråket 
med programmet AddToDictionaryFromBidix.java Programmet läser in orden från 
den tvåspråkiga ordlistan du nyss skapat, så att du bara behöver lägga 
till information om t.ex. paradigm (böjningsmönster).
Spara filen.

10. När du är klar skickar du de tre filerna till resp. språkutvecklare, 
gärna komprimerade i en zip-fil. Utvecklaren kontrollerar dina filer, 
ändrar eventuellt lite grand, och lägger sedan till de nya orden till 
Apertium-parets officiella ordlistor.

11. Filerna heter:

Ordfrekvens
-----------
Frekvens.DinTextfil.txt (t.ex. Frekvens.DanishTowns.txt) Skicka inte den!

Ordlistor (Skicka dessa!)
-------------------------

språkpar.språk1.dix.txt (t.ex. sv-da.da.dix.txt)

språkpar.språkpar.dix.txt (t.ex. sv-da.sv-da.dix.txt)

språkpar.språk2.dix.txt(t.ex. sv-da.sv.dix.txt)