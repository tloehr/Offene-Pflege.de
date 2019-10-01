-- Ab Version 1.14.4.x
UPDATE `sysprops`
SET `V` = '13'
WHERE `K` = 'dbstructure';
--
UPDATE `resinfocategory` t
SET t.`Bezeichnung` = 'Alltagsleben, Soziales'
WHERE t.`BWIKID` = 12;
-- TODO: Die Nursingprocesses greifen auch auf die Kategorien zu. Muss ich noch anpassen.
-- TODO: Schlaf noch löschen.
--
# Template
#        UPDATE `resinfotype` SET `type` = '-1' WHERE `BWINFTYP` = 'respirat1';
#        INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
#                           VALUES ('respirat2', 'Atmung/Beatmung', '',     '15',     '133',       '0',         '22',
#                    '');
-- Tabelle 1 Variablen zur Erfassung von Versorgungsergebnissen (fortlaufende Nummerierung)
-- QDVS#23
UPDATE `resinfotype`
SET `type` = '-1'
WHERE `BWINFTYP` = 'respirat1';
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('respirat2', 'Atmung/Beatmung', '', '15', '133', '0', '22',
        '
   <tx tooltip="Diese Eintragungen werden in den Überleitbogen übernommen. Seite 2, Abschnitt 12."/>
    <label size="18" fontstyle="bold" label="Atmung"/>
    <checkbox label="unauffällig" name="normal"/>
    <checkbox label="kardialer Stau" name="cardcongest" layout="left"/>
    <checkbox label="Schmerzen" name="pain" layout="left"/>
    <checkbox label="Husten" name="cough" layout="left"/>
    <checkbox label="Verschleimung" name="mucous" layout="br"/>
    <checkbox label="Auswurf" name="sputum" layout="left"/>
    <checkbox label="Rauchen" name="smoking" layout="left"/>
    <checkbox label="Asthma" name="asthma" layout="left"/>
    <textfield name="other" label="Sonstiges" length="20"/>

    <label size="18" fontstyle="bold" label="Besonderheiten"/>
    <checkbox label="Tracheostoma" name="stoma"/>
    <checkbox label="Silberkanüle" name="silver" layout="left"/>
    <checkbox label="Silikonkanüle" name="silicon" layout="left"/>
    <checkbox label="Absaugen" name="aspirate" layout="left"/>

    <textfield name="tubetype" label="Kanülenart" length="10"/>
    <textfield name="tubesize" label="Kanülengröße" length="10" layout="left"/>

    <optiongroup size="18" name="beatmung"
                 label="Beatmung"
                 qi="23"
                 tooltip="Die Antwortmöglichkeit „invasive Beatmung“ trifft zu, wenn die Beatmung durch eine Trachealkanüle erfolgt. Ansonsten ist „nicht invasiv“ anzukreuzen.">
        <option label="ja, invasive Beatmung" name="1"/>
        <option label="ja, aber nicht invasiv" name="2"/>
        <option label="nein" default="true" name="3"/>
    </optiongroup>
');
--
-- QDVS#24
UPDATE `resinfotype`
SET `type` = '-1'
WHERE `BWINFTYP` = 'concious';
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('bewusst01', 'Bewusstseinszustand', '', '5', '131', '0', '21',
        '
<resinfotype bwinftyp="bewusst01" sinceversion="13" abstract_type="131" equivalent="21" intervalmode="0"
             infokurz="Bewusstseinszustand" infolang="" category="5" tx="true" qpr="true">


    <optiongroup size="18" name="bewusst"
                 label="Bewusstseinszustand"
                 qi="24"
                 tx="Diese Eintragungen werden in den Überleitbogen übernommen. Seite 2, Abschnitt 11.">
        <option label="wach" name="1" default="true"
                tooltip="Die Person ist ansprechbar und kann an Aktivitäten teilnehmen."/>
        <option label="schläfrig" name="2"
                tooltip="Die Person ist ansprechbar und gut erweckbar, wirkt jedoch müde und ist verlangsamt in seinen Handlungen."/>
        <option label="somnolent" name="3"
                tooltip="Die Person ist sehr schläfrig und kann nur durch starke äußere Reize geweckt werden (z. B. kräftiges Rütteln an der Schulter oder mehrfaches, sehr lautes Ansprechen)."
                layout="br left"/>
        <option label="komatös" name="4" tooltip="Die Person kann durch äußere Reize nicht mehr geweckt werden."/>
        <option label="wachkoma" name="5" tooltip="Dies trifft nur dann zu, wenn eine ärztliche Diagnose vorliegt."/>
    </optiongroup>

</resinfotype>

');
--
-- QDVS#42, 43, 44
UPDATE `resinfotype` t
SET t.`equiv` = 129
WHERE t.`BWINFTYP` = 'ARTNUTRIT';
UPDATE `resinfotype`
SET `type` = '-1'
WHERE `BWINFTYP` = 'ARTNUTRIT';
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('kern01', 'Künstliche Ernährung', '', '4', '129', '0', '129',
        '
       <qpr
            tooltip="Sobald diese Information eingetragen wurde, geht das System von einer künstlichen Ernährung aus. Zeile: 42"/>
    <bi tooltip="Sobald diese Information eingetragen wurde, geht das System von einer künstlichen Ernährung aus. Formular: 4.4.13"/>
    <tx tooltip="Seite 2, Abschnitt 9."/>

    <combobox label="Sondentyp" name="tubetype">
        <item label="PEG (Perkutane endoskopische Gastrostomie)" name="peg"/>
        <item label="PEG/J (PEG mit duodenalem Schenkel)" name="pej"/>
        <item label="Transnasale Ernährungssonde" name="nose"/>
    </combobox>
    <textfield name="tubesince" label="Sonde gelegt am" length="12" type="date"/>
    <textfield name="tubereason" label="Warum wurde die PEG gelegt ?" hfill="false" length="40" innerlayout="br"/>

    <label size="12" fontstyle="bold" label="Verabreichung"/>
    <checkbox label="Ernährungspumpe" name="pump"/>
    <checkbox label="Schwerkraft" name="gravity" layout="left"/>
    <checkbox label="Spritze" name="syringe" layout="left"/>

    <label size="12" fontstyle="bold" label="Sonstiges"/>
    <checkbox label="Orale Ernährung zusätzlich" name="oralnutrition"/>
    <checkbox label="Parenterale Ernährung" name="parenteral" layout="left"/>

    <textfield name="calories" label="Kalorien (in 24h)" hfill="false" length="12"/>

    <separator/>
    <optiongroup name="umfang" label="In welchem Umfang erfolgt eine künstliche Ernährung?"
                 qi="43" bi="4.4.13">
        <option label="nicht täglich oder nicht dauerhaft" name="1" default="true"/>
        <option label="täglich, aber zusätzlich zur oralen Ernährung" name="2"/>
        <option label="ausschließlich oder nahezu ausschließlich künstliche Ernährung" name="3"/>
    </optiongroup>

    <optiongroup name="selbst" label="Erfolgt die Bedienung selbständig oder mit Fremdhilfe?" qi="44" bi="4.4.13">
        <option label="selbständig" name="1" default="true"/>
        <option label="mit Fremdhilfe" name="2"/>
    </optiongroup>

');
--
-- QDVS#25, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41
UPDATE `resinfotype`
SET `type` = '-1'
WHERE `BWINFTYP` = 'ORIENT1';
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('orient02', 'Demenz und Orientierung', '', '5', '132', '0',
        '20',
        '
    <tx tooltip="Diese Eintragungen werden in den Überleitbogen übernommen. Seite 2, Abschnitt 11."/>
    <optiongroup name="type_of_dementia" label="Form der Demenz" size="18">
        <option label="Nicht dement" name="none" default="true"/>
        <option label="Primäre Demenz" name="primary"
                tooltip="Bei einer primären Demenz liegen neurodegenerative oder vaskuläre Veränderungen vor. Es wird unterschieden, ob die Nervenzellen des Gehirns degenerieren, also ohne äußerlich erkennbare Ursache untergehen (wie bei der Alzheimer-Krankheit), oder ob sie z.B. wegen Durchblutungsstörungen schwere Schäden erlitten haben (diese Form wird als vaskulärer Demenztyp bezeichnet)."/>
        <option label="Sekundäre Demenz"
                tooltip="Hier ist der geistige Verfall die Folge einer anderen organischen Erkrankung wie einer Hirnverletzung, einer Hirngeschwulst oder einer Herz-Kreislauf-Krankheit; auch Arzneistoffe und Gifte wie Alkohol (Korsakow- Syndrom) oder andere Drogen können dazu führen. Wenn die Grunderkrankung wirksam behandelt wird, Giftstoffe das Gehirn nicht mehr belasten oder Verletzungen geheilt sind, normalisiert sich meist die geistige Leistungsfähigkeit. Oder es ist ein Stillstand des Leidens zu erreichen."
                name="secondary"/>
    </optiongroup>

    <label fontstyle="bold" label="Welche Tests wurden durchgeführt ?"/>
    <checkbox name="cct" label="Uhrentest (Clock Completion Test)"/>
    <checkbox name="demtect" label="DemTect" layout="left"/>
    <checkbox name="tfdd" label="TFDD" tooltip="TFDD - Test zu Früherkennung von Demenzen mit Depressionsabgrenzung"
              layout="left"/>
    <label fontstyle="bold" label="Bitte ausgefüllte Tests an diese Info anhängen."/>

    <separator/>
    <label fontstyle="bold" size="18" label="Fragen aus dem Begutachtungsinstrument. Modul 2"/>
    <label parwidth="600px" label="bi2.intro"/>

    <optiongroup name="personen" size="18" label="Erkennen von Personen aus dem näheren Umfeld"
                 tooltip="bi2.personen.erklaerung" qi="Zeile 31">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.personen.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.personen.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.personen.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.personen.selbst3"/>
    </optiongroup>

    <optiongroup name="orte" size="18" label="Örtliche Orientierung" tooltip="bi2.orte.erklaerung" qi="Zeile 32">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.orte.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.orte.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.orte.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.orte.selbst3"/>
    </optiongroup>

    <optiongroup name="zeitlich" size="18" label="Zeitliche Orientierung" tooltip="bi2.zeitlich.erklaerung"
                 qi="Zeile 33">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.zeitlich.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.zeitlich.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.zeitlich.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.zeitlich.selbst3"/>
    </optiongroup>

    <optiongroup name="erinnern" size="18" label="Sich Erinnern" tooltip="bi2.erinnern.erklaerung" qi="Zeile 34">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.erinnern.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.erinnern.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.erinnern.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.erinnern.selbst3"/>
    </optiongroup>

    <optiongroup name="handlungen" size="18" label="Steuern von mehrschrittigen Alltagshandlungen"
                 tooltip="bi2.handlungen.erklaerung" qi="Zeile 35">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.handlungen.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.handlungen.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.handlungen.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.handlungen.selbst3"/>
    </optiongroup>

    <optiongroup name="entscheidungen" size="18" label="Treffen von Entscheidungen im Alltagsleben"
                 tooltip="bi2.entscheidungen.erklaerung" qi="Zeile 36">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.entscheidungen.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.entscheidungen.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.entscheidungen.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.entscheidungen.selbst3"/>
    </optiongroup>

    <optiongroup name="verstehen" size="18" label="Verstehen von Sachverhalten und Informationen"
                 tooltip="bi2.verstehen.erklaerung" qi="Zeile 37">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.verstehen.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.verstehen.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.verstehen.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.verstehen.selbst3"/>
    </optiongroup>

    <optiongroup name="risiken" size="18" label="Erkennen von Risiken und Gefahren" tooltip="bi2.risiken.erklaerung"
                 qi="Zeile 38">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.personen.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.personen.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.personen.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.personen.selbst3"/>
    </optiongroup>

    <optiongroup name="beduerfnissen" size="18" label="Mitteilen von elementaren Bedürfnissen"
                 tooltip="bi2.beduerfnissen.erklaerung" qi="Zeile 39">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.beduerfnissen.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.beduerfnissen.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.beduerfnissen.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.beduerfnissen.selbst3"/>
    </optiongroup>

    <optiongroup name="aufforderungen" size="18" label="Verstehen von Aufforderungen"
                 tooltip="bi2.aufforderungen.erklaerung" qi="Zeile 40">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.aufforderungen.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.aufforderungen.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.aufforderungen.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.aufforderungen.selbst3"/>
    </optiongroup>

    <optiongroup name="gespraech" size="14" label="Beteiligung an einem Gespräch" tooltip="bi2.gespraech.erklaerung"
                 qi="Zeile 41">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.gespraech.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.gespraech.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.gespraech.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.gespraech.selbst3"/>
    </optiongroup>
');
--
-- QDVS#26, 27, 28, 29, 30, 31, 32
UPDATE `resinfotype`
SET `type` = '-1'
WHERE `BWINFTYP` = 'MOBILITY';
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('mobil02', 'Mobilität', '', '3', '110', '0',
        '14', '
 <tx tooltip="[b]Seite 1, Abschnitt 4.[/b][br/]Alles was Sie hier als Bemerkung eintragen, steht hinterher in der Bemerkungs-Zeile dieses Abschnitts im Überleitbogen.[br/][b]Lagerungsarten[/b] werden anhand der Pflegeplanungen bestimmt."/>
    <checkbox name="bedridden" label="bettlägerig"/>
    <!-- name war vorher "bedmovement" -->
    <optiongroup size="18" name="bett" label="Positionswechsel im Bett" qi="26" bi="4.1.1"
                 tooltip="bi1.bett.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi1.bett.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi1.bett.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi1.bett.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi1.bett.selbst3"/>
    </optiongroup>
    <!-- name war vorher "sitting" wurde ab mobil02 umbenannt. Ebenso, wie bei allen Auswahlfelder gibts
        kein "triff nicht" zu mehr und es gibt die Formulierung "übernahme" nicht mehr.
        Das Hilfmittel Textfeld für jeden Abschnitt ist nicht mehr da. -->
    <optiongroup size="18" name="sitz" label="Halten einer stabilen Sitzposition" qi="26" bi="4.1.2"
                 tooltip="bi1.sitz.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi1.sitz.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi1.sitz.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi1.sitz.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi1.sitz.selbst3"/>
    </optiongroup>
    <!-- name war vorher "stand" und "transfer"-->
    <optiongroup size="18" name="umsetzen" label="Umsetzen" qi="26" bi="4.1.3" tooltip="bi1.umsetzen.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi1.umsetzen.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi1.umsetzen.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi1.umsetzen.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi1.umsetzen.selbst3"/>
    </optiongroup>
    <!-- name war vorher "walk" -->
    <optiongroup size="18" name="wohnbereich" label="Fortbewegen innerhalb des Wohnbereichs" qi="26" bi="4.1.4"
                 tooltip="bi1.wohnbereich.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi1.wohnbereich.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi1.wohnbereich.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi1.wohnbereich.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi1.wohnbereich.selbst3"/>
    </optiongroup>

    <!--  gabs vorher nicht -->
    <optiongroup size="18" name="treppe" label="Treppensteigen" tooltip="bi1.treppen.erklaerung" qi="Zeile 30"
                 bi="4.1.5">
        <option label="selbstständig" name="0" default="true" tooltip="bi1.treppen.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi1.treppen.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi1.treppen.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi1.treppen.selbst3"/>
    </optiongroup>

    <checkbox name="unfaegig-arme-beine" label="Gebrauchsunfähigkeit beider Arme und beider Beine"
              tooltip="bi1.unfaehig.arme.beine"
              bi="4.1.6"/>

    <tabgroup label="Hilfsmittel zur Bewegung" name="hilfsmittel">
        <checkbox name="rollstuhl" label="Rollstuhl"/>
        <checkbox name="kruecke" label="Unterarmgehstütze"/>
        <checkbox name="rollator" label="Rollator"/>
        <checkbox name="gehstock" label="Gehstock"/>
    </tabgroup>
    <textfield label="Sonstige" name="anderehm" innerlayout="left"/>
');
-- Wechsel der Kategorie von Wahrnehmung nach Alltag
UPDATE `resinfotype` t
SET t.`BWIKID` = 12
WHERE t.`BWINFTYP` LIKE 'RIECHEN' ESCAPE '#';
UPDATE `resinfotype` t
SET t.`BWIKID` = 12
WHERE t.`BWINFTYP` LIKE 'SPRACHVER' ESCAPE '#';
UPDATE `resinfotype` t
SET t.`BWIKID` = 12
WHERE t.`BWINFTYP` LIKE 'BRILLE' ESCAPE '#';
UPDATE `resinfotype` t
SET t.`BWIKID` = 12
WHERE t.`BWINFTYP` LIKE 'SEHEN' ESCAPE '#';
UPDATE `resinfotype` t
SET t.`BWIKID` = 12
WHERE t.`BWINFTYP` LIKE 'HOERGERAET' ESCAPE '#';
UPDATE `resinfotype` t
SET t.`BWIKID` = 12
WHERE t.`BWINFTYP` LIKE 'TASTEMPF' ESCAPE '#';
UPDATE `resinfotype` t
SET t.`BWIKID` = 12
WHERE t.`BWINFTYP` LIKE 'HOEREN' ESCAPE '#';
UPDATE `resinfotype` t
SET t.`BWIKID` = 12
WHERE t.`BWINFTYP` LIKE 'COMMS' ESCAPE '#';
UPDATE `resinfotype` t
SET t.`BWIKID` = 12
WHERE t.`BWINFTYP` LIKE 'SCHMECK' ESCAPE '#';
UPDATE `resinfotype` t
SET t.`BWIKID` = 12
WHERE t.`BWINFTYP` LIKE 'SPRACHE' ESCAPE '#';
