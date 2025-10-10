-- Ab Version 1.16.6
UPDATE `sysprops`
SET V = '22'
WHERE K = 'dbstructure';
-- Umwandlung von Intervall zu Zeitpunkt
UPDATE `resinfotype`
SET IntervalMode = 3
WHERE BWINFTYP LIKE 'strzfolg01';
--
-- Korrektur der bestehenden Einträge
UPDATE `resinfo`
SET Bis = Von
WHERE BWINFTYP LIKE 'strzfolg01';
--
-- tooltips nachgetragen bzgl. Frakturen und Arztkontakt
UPDATE `resinfotype`
SET XML = ' <qdvs optional="true"/>
          <label layout="br left hfill" size="20" fontstyle="bold" color="yellow" bgcolor="blue"
                 label="1. Grundlegende Angaben"/>

          <tabgroup size="16" label="Datum und Uhrzeit des Sturzes" name="date1">
              <textfield label="Datum" length="12" name="falldate" type="date" preset="currentdate"/>
              <textfield label="Uhrzeit" length="12" name="falltime" type="time" layout="left" preset="currenttime"/>
          </tabgroup>
          <tabgroup size="16"
                    label="letzter Zeitpunkt, wann die Person vor dem Sturz gesehen wurde."
                    name="date2">
              <textfield label="Datum" length="12" name="b4falldate" type="date" preset="currentdate"/>
              <textfield label="Uhrzeit" length="12" name="b4falltime" type="time" layout="left" preset="currenttime"/>
              <textfield name="textb4fall" label="Aktivitäten des Bewohners unmittelbar vor dem Sturz" innerlayout="br"
                         hfill="false"/>
          </tabgroup>

          <label layout="br left hfill" size="20" fontstyle="bold" color="yellow" bgcolor="blue"
                 label="2. Beschreibung des Sturzereignisses"/>
          <optiongroup label="Sturzort" name="sturzort">
              <option label="BW-Zimmer" name="room" default="true"/>
              <option label="BW Badezimmer" name="bathroom"/>
              <option label="Gemeinschafts-Badezimmer" name="commbathroom"/>
              <option label="Flur" name="hallway"/>
              <option label="Gemeinschaftsraum" name="livingroom" layout="br"/>
              <option label="Außerhalb des Hauses" name="outside"/>
              <option label="Sonstiges (siehe unten)" name="other1"/>
          </optiongroup>
          <textfield name="othertext1" label="Sonstiges oder Erläuterung" hfill="false"/>

          <optiongroup label="Wie aufgefunden" name="aufgefunden">
              <option label="auf dem Bauch liegend" name="bauch" default="true"/>
              <option label="auf dem Rücken liegend" name="ruecken"/>
              <option label="sitzend" name="sitzend"/>
              <option label="kniend/kriechend" name="knie"/>
              <option label="Sonstiges" name="sonst" layout="br"/>
          </optiongroup>
          <textfield name="aufgefundentext" default="" label="Beschreiben Sie, wie der BW aufgefunden wurde." length="30"
                     hfill="true"
                     layout="br left" depends-on="aufgefunden" visible-when-dependency-eq="sonst"
                            default-value-when-shown=""/>

          <optiongroup label="Körperstellung unmittelbar vor dem Sturz" name="b4fall">
              <option label="Liegen" name="lying"/>
              <option label="Sitzen" name="sitting"/>
              <option label="Aufstehen/Hinsetzen vom/aufs Bett" name="getupsitdown1"/>
              <option label="Aufstehen/Hinsetzen vom/auf Sessel/Stuhl" name="getupsitdown2" layout="br"/>
              <option label="Aufstehen/Hinsetzen vom/auf Rollstuhl" name="getupsitdown3"/>
              <option label="Aufstehen/Hinsetzen vom/auf Toilettenstuhl" name="getupsitdown4" layout="br"/>
              <option label="Aufstehen/Hinsetzen (andere, bitte unten beschreiben)" name="getupsitdown5"/>
              <option label="Stehen" name="standing" layout="br"/>
              <option label="Gehen" name="walking" default="true"/>
          </optiongroup>
          <textfield name="othertext2" label="Andere" hfill="false"/>

          <optiongroup label="Hilfsmittel" name="aux">
              <option label="keine Hilfsmittel" name="none" default="true"/>
              <option label="einseitige Gehhilfe" name="onesided"/>
              <option label="beidseitige Gehhilfe" name="twosided"/>
              <option label="Handstock" name="cane" layout="br"/>
              <option label="Rollator" name="rollator"/>
              <option label="Gehwagen (KG)" name="walker"/>
              <option label="Andere, bitte unten beschreiben" name="other3"/>
          </optiongroup>
          <textfield name="othertext3" label="Andere" hfill="false"/>

          <optiongroup label="Hindernisse/Umgebungsfaktoren" name="evironment"
                       tooltip="Bedingungen, die durch die Umgebung vorgegeben sind, beeinflussen in ganz erheblichem Maße die Sturzgefährdung. Gerade im Begründungszusammenhang, warum es zu einem Sturzereignis gekommen ist, sollten Aspekte, die im Sturzumfeld liegen unbedingt beschrieben werden. Dies kann z.B. eine mangelhafte Beleuchtung, eine im Weg stehende Reisetasche, ein unebener oder besonders glatter Boden oder die Verwendung von nicht geeignetem Schuhwerk sein.">
              <option label="keine" name="none" default="true"/>
              <option label="Boden rutschig" name="slippery"/>
              <option label="Hindernis im Weg" name="obstacle"/>
              <option label="Beleuchtung unzureichend" name="badlight" layout="br"/>
              <option label="Andere, bitte unten beschreiben" name="other4"/>
          </optiongroup>
          <textfield name="othertext4" label="Andere" hfill="false"/>

          <label layout="br left hfill" size="20" fontstyle="bold" color="yellow" bgcolor="blue"
                 label="3. Beschreibung der Sturzfolgen"/>
          <tabgroup label="Verletzungfolgen" name="consequences" hfill="false">
              <checkbox label="Platzwunde" name="laceration" layout="br"/>
              <checkbox label="Schürfwunde" name="graze" layout="left"/>
              <checkbox label="Hämatome" name="bruise" layout="left"/>
              <checkbox label="Fraktur" tooltip="Führt dazu, dass dieser Sturz mit in die MDK Erhebung aufgenommen wird." name="fracture" layout="left"/>
              <checkbox label="Schmerzen" name="pain" layout="left"/>
          </tabgroup>
          <textfield name="othertext5" label="Andere" hfill="false"/>
          <textfield name="fear" label="Sturzangst" hfill="false"
                     tooltip="zukünftige präventive Maßnahmen hängen von der Sturzangst des Patienten ab, da das Sturzereignis im Gedächtnis des Patienten haften bleibt und wissenschaftlich nachgewiesen ist, dass es einen negativen Einfluss auf ein mögliches weiteres Sturzgeschehen hat"/>

          <label layout="br left hfill" size="20" fontstyle="bold" color="yellow" bgcolor="blue"
                 label="4. Massnahmen nach dem Sturz"/>
          <tabgroup name="massnahmen" label="Eingeleitete Maßnahmen" hfill="false">
              <checkbox label="Wundverband" name="wound"/>
              <checkbox label="Lagerung / Kühlung" name="wound"/>
              <checkbox label="pflegerische Unterstützung" name="pflegerisch"/>
              <checkbox label="Arztkontakt" tooltip="Führt dazu, dass dieser Sturz mit in die MDK Erhebung aufgenommen wird." name="gp"/>
              <checkbox label="Krankenhauseinweisung" tooltip="Führt dazu, dass dieser Sturz mit in die MDK Erhebung aufgenommen wird." name="hospital"/>
          </tabgroup>
          <textfield name="othertext6" label="Anmerkungen zu den Massnahmen" hfill="false"/>

          <url label="Das neue Formular \'Sturzprotokoll\' (Uni Bonn, siehe OPDE Quellen UNIBONN2008-01)"
               link="https://www.offene-pflege.de/de/sources-de"/>'
WHERE BWINFTYP LIKE 'fallprot02';
#
UPDATE `resinfotype`
SET deprecated = 1,
    equiv      = 151
WHERE BWINFTYP LIKE 'fraktur01';
#
# INSERT INTO `resinfotype` (BWINFTYP, XML, BWInfoKurz, BWInfoLang, BWIKID, type, version, IntervalMode, equiv, deprecated)
# VALUES ('fraktur02', '<qdvs optional="true"/><label layout="br left hfill" size="14" fontstyle="bold" label="Setzen Sie den Zeitpunkt des Ereignis nach dem Speichern."/>
#    <checkbox label="Knochenbruch aufgrund eines Sturzes" name="fall" layout="br left"/>', 'Knochenbruch (Fraktur)', '',
#         15, 162, 0, 3, 151, 0);
#
UPDATE `resinfotype`
SET deprecated = 1,
    equiv      = 28
WHERE BWINFTYP LIKE 'schmerze2';
#
INSERT INTO `resinfotype` (BWINFTYP, XML, BWInfoKurz, BWInfoLang, BWIKID, type, version, IntervalMode, equiv, deprecated)
VALUES ('schmerze3', '
    <qdvs optional="true"/>
    <label size="16" label="Allgemeine Einschätzung" color="blue"/>
    <combobox label="Schmerzgrad in Ruhe" name="schmerzint_ruhe">
        <item label="0 - kein Schmerz" name="0"/>
        <item label="1 - kaum Schmerzen" name="1"/>
        <item label="2 - kaum Schmerzen" name="2"/>
        <item label="3 - erträgliche Schmerzen" name="3"/>
        <item label="4 - erträgliche Schmerzen" name="4"/>
        <item label="5 - stärkere Schmerzen" name="5"/>
        <item label="6 - starke Schmerzen" name="6"/>
        <item label="7 - starke Schmerzen" name="7"/>
        <item label="8 - sehr starke Schmerzen" name="8"/>
        <item label="9 - fast unerträgliche Schmerzen" name="9"/>
        <item label="10 - unerträgliche Schmerzen" name="10"/>
    </combobox>
    <combobox label="Schmerzgrad unter Belastung" name="schmerzint_last">
        <item label="0 - kein Schmerz" name="0"/>
        <item label="1 - kaum Schmerzen" name="1"/>
        <item label="2 - kaum Schmerzen" name="2"/>
        <item label="3 - erträgliche Schmerzen" name="3"/>
        <item label="4 - erträgliche Schmerzen" name="4"/>
        <item label="5 - stärkere Schmerzen" name="5"/>
        <item label="6 - starke Schmerzen" name="6"/>
        <item label="7 - starke Schmerzen" name="7"/>
        <item label="8 - sehr starke Schmerzen" name="8"/>
        <item label="9 - fast unerträgliche Schmerzen" name="9"/>
        <item label="10 - unerträgliche Schmerzen" name="10"/>
    </combobox>
    <checkbox name="schmerzfrei" label="Schmerzfrei durch Medikamente" layout="br left" size="14"/>
    <label label="Akutschmerz i.d.R. weniger als 3 Monate, Chronischer Schmerz zwischen 6 Wochen und 3 Monaten, oder länger" size="14" fontstyle="bold"/>
    <combobox label="Schmerztyp" name="schmerztyp"
              tooltip="[h1]Akuter Schmerz, „sinnvoller Schmerz“[/h1] [p] Der akute Schmerz gilt als Alarmzeichen des Körpers. Schon die alten Griechen nannten den Schmerz den „bellenden Wächter der Gesundheit“ (Hypokrates). Der akute Schmerz macht uns aufmerksam, dass etwas nicht stimmt und ist zeitlich begrenzt. Ist die Ursache behoben verschwindet der Schmerz meistens wieder. Wenn wir wissen warum wir Schmerzen haben (z.B. den Fuss verstaucht), können wir den Schmerz auch eher akzeptieren. Hier spielt die individuelle Wahrnehmung und das Erlernte „umgehen mit dem Schmerz“ eine wichtige Rolle. [/p] [p] Akuter Schmerz ist ein plötzlich auftretender und nur kurze Zeit andauern der Schmerz. Er wird als existentielle Erfahrung wahrgenommen, die eine lebenserhaltende Alarm- und Schutzfunktion einnimmt. Akuter Schmerz steht in einem offensichtlichen und direkten Zusammenhang mit einer Gewebe oder Organschädigung, also einer körperlichen Ursache. Nonverbale und verbale Signale, die wir im akuten Schmerz aussenden, verursachen unwillkürlich Empathie und das Beduürfnis für Abhilfe zu sorgen. Akuter Schmerz geht mit physiologischen Begleiterscheinungen einher, wie einem Anstieg des Blutdrucks, des Pulses, Schweißausbrüchen und Anstieg der Atemfrequenz. Insbesondere diese Begleiterscheinungen, die in der akuten Versorgungssituation unmittelbar erkennbar sind, zeigt der Mensch mit ausschließlich chronischen Schmerzen nicht. [/p]  [h1]Chronischer Schmerz, „sinnloser Schmerz“[/h1] [p] Der chronische Schmerz hat an sich keine Warnfunktion mehr. Seine Ursache ist nicht (mehr) ausschaltbar, er nimmt dem Menschen sinnlos die Kraft weg und zehrt allmählich seinen Lebensmut auf. Wenn die Tage zur Qual werden, erschöpft sich die Tragfähigkeit, der Leidende wünscht nur mehr ein Ende herbei, unter Umständen sogar um den Preis seines Lebens, denn es genügt nicht nur am Leben zu sein, man muss auch sein Leben haben. Der chronische Schmerz kann zur eigenständigen Schmerzkrankheit werden, der alle Ebenen des Menschseins beeinflusst und beeinträchtigt. Man spricht dann von „total pain“. Dieser Schmerz ist oft losgelöst von der ursprünglichen Krankheit. Gerade wenn die Ursache unbekannt ist, kann die Chronifizierung schnell eintreten. [/p] [p] Der Übergang zwischen akutem und chronischem Schmerz verläuft kontinuierlich. Gleichwohl werden verschiedene Zeiträume angenommen, ab wann ein Schmerz als chronischer, oder anhaltender Schmerz zu betrachten ist. Je nach Lokalisation des Schmerzes wird hierbei von mehr als 6 Wochen bis hin zu 3 Monaten ausgegangen. In erster Linie wird die Entstehung des chronischen Schmerzes durch drei grundlegende Elemente beschrieben: [/p] [ul] [li]Es handelt sich um einen Entstehungsprozess, der durch ein Zusammenwirken von krankheitsbedingten und psychosozialen Prozessen gekennzeichnet ist.[/li] [li]Chronischer Schmerz ist Schmerz, der über einen Punkt, an dem die Heilung abgeschlossen sein sollte hinaus, anhält oder weiter auftritt. Chronischer Schmerz kann häufig nicht (mehr) mit einem Gewebeschaden oder einer Verletzung in Verbindung gebracht werden.[/li] [li]Der Chronifizierung akuter Schmerzen kann durch angemessene Therapie des akuten Schmerzes entgegengewirkt werden. Eine frühzeitige Linderung von akutem Schmerz kann eine Entwicklung von chronischen Schmerzen verhindern. Bestimmte operative Verfahren, z. B. Amputationen, Mastektomien oder Thorakotomien bewirken häufig chronische Schmerzen.[/li] [/ul]">
        <item label="akute Schmerzen" name="0"/>
        <item label="chronische Schmerzen" name="1"/>
    </combobox>
    <textfield name="schmerzort" label="Wo tritt der Schmerz auf ?" depends-on="schmerzint"
               visible-when-dependency-neq="0"/>
    <textfield name="schmerzart" label="Beschreibung der Schmerzart"
               tooltip="Beispiele für Schmerzarten: dumpf, pulsierend, nagelnd, schießend, brennen, steched, bohrend, ausstrahlend"
               depends-on="schmerzint" visible-when-dependency-neq="0"/>
    <textfield name="lindernd" label="Lindernde Faktoren" depends-on="schmerzint" visible-when-dependency-neq="0"/>
    <textfield name="verstaerkend" label="Verstärkende Faktoren" depends-on="schmerzint"
               visible-when-dependency-neq="0"/>
    <label size="16" label="Folgen für Lebensalltag" color="blue"/>
    <combobox label="Stuhlgang" name="stuhl">
        <item label="unabhängig vom Schmerz" name="0"/>
        <item label="normal" name="1"/>
        <item label="schlechter" name="2"/>
    </combobox>
    <combobox label="Schlaf" name="schlaf">
        <item label="unabhängig vom Schmerz" name="0"/>
        <item label="normal" name="1"/>
        <item label="schlechter" name="2"/>
    </combobox>
    <combobox label="Wohlbefinden" name="wohlb">
        <item label="unabhängig vom Schmerz" name="0"/>
        <item label="normal" name="1"/>
        <item label="schlechter" name="2"/>
    </combobox>
    <combobox label="Beeinträchtigung der Tagesaktivität durch Schmerzen" name="tagaktiv">
        <item label="unabhängig vom Schmerz" name="0"/>
        <item label="leicht" name="1"/>
        <item label="mittel" name="2"/>
        <item label="stark" name="3"/>
    </combobox>
', 'Schmerzeinschätzung', 'Schmerzeinschätzung durch den Bewohner selbst',
        15, 137, 0, 3, 28, 0);
#
UPDATE `resinfotype`
SET deprecated = 1
WHERE BWINFTYP LIKE 'skin';
#
INSERT INTO `resinfotype` (BWINFTYP, XML, BWInfoKurz, BWInfoLang, BWIKID, type, version, IntervalMode, equiv, deprecated)
VALUES ('skin2', '
       <label label="Schnelleinschätzung" size="24" fontstyle="bold"/>
    <label label=" Sollte eine oder mehrere Fragen mit Ja beantwortet werden, muss eine vertiefte Einschätzung durchgeführt werden." fontstyle="bold"/>

    <tabgroup  size="16" label="Risikogruppe" name="riskgroup" >
        <checkbox name="risk.old" label="Ältere Menschen"/>
        <checkbox name="risk.inko" label="Inkontinenz" layout="left"/>
        <checkbox name="risk.diabetes" label="Diabetes mellitus" layout="left"/>
        <checkbox name="risk.chron.insuff" label="Chronisch venöse Insuffizienz" layout="br"/>
        <checkbox name="risk.adipositas" label="Adipositas" layout="left"/>
        <checkbox name="risk.mobility" label="Eingeschränkte Mobilität" layout="br"/>
        <checkbox name="risk.medication" label="Besondere Medikamente (z.B. Diuretika, Kortison)" layout="left"/>
    </tabgroup>

    <tabgroup  size="16" label="Hautbeschaffenheit" name="skintype" tx="Seite 1, Abschnitt 3">
        <checkbox name="skin.normal" label="intakt" layout="br"/>
        <checkbox name="skin.dry" label="trocken"  layout="left"/>
        <checkbox name="skin.greasy" label="fettig" layout="left"/>
        <checkbox name="skin.prob" label="Gegenwärtige Hautprobleme"  layout="left"/>
        <checkbox name="skin.changes" label="Aktuelle Veränderungen der Haut" layout="br"/>
        <checkbox name="skin.allergies" label="Kontaktallergien oder Unverträglichkeiten" layout="left"/>
        <checkbox name="skin.sweat" label="Starkes oder häufiges Schwitzen" layout="br"/>
        <checkbox name="skin.wound" label="Wunde Stellen" layout="left"/>
        <checkbox name="skin.tension" label="Spannungsgefühl" layout="left"/>
        <checkbox name="skin.bruises" label="Blutergüsse oder Einrisse" layout="br"/>
        <checkbox name="skin.itch" label="Jucken oder Schuppen" layout="left"/>
        <checkbox name="skin.burn" label="Brennen oder Schmerzen" layout="left"/>
    </tabgroup>

    <label label="Vertiefte Einschätzung" size="24" fontstyle="bold"/>

    <label label="[html][h2]Bitte prüfen Sie folgende Punkte und vermerken die Ergebnisse unten im Bemerkungsfeld[/h2]

                  [ul]
                  [li]Körperhygiene und Waschverhalten (wie oft und wie lange wird geduscht oder gebadet, welche Produkte werden zur Reinigung und Pflege der Haut verwendet und wie oft.[/li]
                  [li]Unterstützungsbedarf bei der Körperpflege gibt, zum Beispiel im Zusammenhang mit Ausscheidungen oder beim Waschen.[/li]
                  [li]Mangelernährung beziehungsweise Übergewicht[/li]
                  [li]Wie ist das Trinkverhalten ?[/li]
                  [li]Kontaktdermatitis oder Allergien[/li]
                  [li]Fähigkeit zur Selbstpflege[/li]
                  [/ul]

                  [h2]Achten Sie bei der Hautinspektion auf folgende Auffälligkeiten[/h2]

                  [ul]
                   [li]Farbe (z. B. rot, bräunlich, bläulich)[/li]
                   [li]Feuchtigkeit der Haut[/li]
                   [li]Erhabenheiten (z. B. Papeln, Bläschen)[/li]
                   [li]Schuppen[/li]
                   [li]Erosionen[/li]
                   [li]Schmerzen[/li]
                   [li]Juckreiz[/li]
                   [li]Brennen[/li]
                   [li]Spannungsgefühl[/li]
                  [/ul]
                  [/html]"/>

    <bodyscheme name="bs1"/>
', 'Hautintegrität', '',
        9, 109, 0, 0, 13, 0);
#
-- Um unliebsame Formen loszuwerden (Stomaplatte)
alter table `dosageform`
    modify Stellplan tinyint(3) default 0 not null;
#
delete from `bhp` where outcome4 is not null;
#
alter table `bhp`
    drop column needsText,
    change outcome4 outcome_nreport bigint unsigned null;
