-- Ab Version 1.14.4.x
UPDATE 'sysprops'
SET 'V' = '13'
WHERE 'K' = 'dbstructure';
--
UPDATE 'resinfocategory' t
SET t.'Bezeichnung' = 'Alltagsleben, Soziales'
WHERE t.'BWIKID' = 12;
--
alter table resinfotype add deprecated tinyint default 0 null;
update resinfotype SET deprecated = 1 WHERE type < 0;
UPDATE `resinfotype` t SET t.`type` = 128 WHERE t.`BWINFTYP` LIKE 'ISSTGERN';
UPDATE `resinfotype` t SET t.`type` = 119 WHERE t.`BWINFTYP` LIKE 'WUNDE3';
UPDATE `resinfotype` t SET t.`type` = 30 WHERE t.`BWINFTYP` LIKE 'STURZPROT2';
UPDATE `resinfotype` t SET t.`type` = 110 WHERE t.`BWINFTYP` LIKE 'HAENDIG';
UPDATE `resinfotype` t SET t.`type` = 138 WHERE t.`BWINFTYP` LIKE 'FIXIERUNG';
UPDATE `resinfotype` t SET t.`type` = 105 WHERE t.`BWINFTYP` LIKE 'PSTF';
UPDATE `resinfotype` t SET t.`type` = 127 WHERE t.`BWINFTYP` LIKE 'SCHLAFGEW';
UPDATE `resinfotype` t SET t.`type` = 128 WHERE t.`BWINFTYP` LIKE 'KAUEN';
UPDATE `resinfotype` t SET t.`type` = 97 WHERE t.`BWINFTYP` LIKE 'ALLERGIE';
UPDATE `resinfotype` t SET t.`type` = 132 WHERE t.`BWINFTYP` LIKE 'ORIENTIERU';
UPDATE `resinfotype` t SET t.`type` = 102 WHERE t.`BWINFTYP` LIKE 'ANGEH';
UPDATE `resinfotype` t SET t.`type` = 126 WHERE t.`BWINFTYP` LIKE 'WUNDANAM5a';
UPDATE `resinfotype` t SET t.`type` = 125 WHERE t.`BWINFTYP` LIKE 'WUNDANAM4a';
UPDATE `resinfotype` t SET t.`type` = 112 WHERE t.`BWINFTYP` LIKE 'INKOAID';
UPDATE `resinfotype` t SET t.`type` = 128 WHERE t.`BWINFTYP` LIKE 'ZUSATZKOST';
UPDATE `resinfotype` t SET t.`type` = 120 WHERE t.`BWINFTYP` LIKE 'WUNDE4';
UPDATE `resinfotype` t SET t.`type` = 0 WHERE t.`BWINFTYP` LIKE 'PEG2';
UPDATE `resinfotype` t SET t.`type` = 108 WHERE t.`BWINFTYP` LIKE 'KPFLEGE';
UPDATE `resinfotype` t SET t.`type` = 132 WHERE t.`BWINFTYP` LIKE 'WEGLAUF';
UPDATE `resinfotype` t SET t.`type` = 99 WHERE t.`BWINFTYP` LIKE 'INFECT1';
UPDATE `resinfotype` t SET t.`type` = 131 WHERE t.`BWINFTYP` LIKE 'BEWUSST';
UPDATE `resinfotype` t SET t.`type` = 137 WHERE t.`BWINFTYP` LIKE 'SCHMERZE';
UPDATE `resinfotype` t SET t.`type` = 99 WHERE t.`BWINFTYP` LIKE 'ANSTECK';
UPDATE `resinfotype` t SET t.`type` = 0 WHERE t.`BWINFTYP` LIKE 'KH';
UPDATE `resinfotype` t SET t.`type` = 101 WHERE t.`BWINFTYP` LIKE 'SPRACHVER';
UPDATE `resinfotype` t SET t.`type` = 127 WHERE t.`BWINFTYP` LIKE 'TAGNACHT';
UPDATE `resinfotype` t SET t.`type` = 104 WHERE t.`BWINFTYP` LIKE 'FSTAND';
UPDATE `resinfotype` t SET t.`type` = 133 WHERE t.`BWINFTYP` LIKE 'TRACHEOST';
UPDATE `resinfotype` t SET t.`type` = 119 WHERE t.`BWINFTYP` LIKE 'WUNDE3b';
UPDATE `resinfotype` t SET t.`type` = 128 WHERE t.`BWINFTYP` LIKE 'TRINKTGERN';
UPDATE `resinfotype` t SET t.`type` = 30 WHERE t.`BWINFTYP` LIKE 'STURZPROT3';
UPDATE `resinfotype` t SET t.`type` = 110 WHERE t.`BWINFTYP` LIKE 'BEWEGUNG';
UPDATE `resinfotype` t SET t.`type` = 101 WHERE t.`BWINFTYP` LIKE 'HOEREN';
UPDATE `resinfotype` t SET t.`type` = 120 WHERE t.`BWINFTYP` LIKE 'WUNDE4a';
UPDATE `resinfotype` t SET t.`type` = 121 WHERE t.`BWINFTYP` LIKE 'WUNDE5a';
UPDATE `resinfotype` t SET t.`type` = 121 WHERE t.`BWINFTYP` LIKE 'WUNDE5b';
UPDATE `resinfotype` t SET t.`type` = 110 WHERE t.`BWINFTYP` LIKE 'BETTLAE';
UPDATE `resinfotype` t SET t.`type` = 120 WHERE t.`BWINFTYP` LIKE 'WUNDE4b';
UPDATE `resinfotype` t SET t.`type` = 0 WHERE t.`BWINFTYP` LIKE 'BRILLE';
UPDATE `resinfotype` t SET t.`type` = 123 WHERE t.`BWINFTYP` LIKE 'WUNDANAM2a';
UPDATE `resinfotype` t SET t.`type` = 132 WHERE t.`BWINFTYP` LIKE 'MERK';
UPDATE `resinfotype` t SET t.`type` = 117 WHERE t.`BWINFTYP` LIKE 'WUNDE';
UPDATE `resinfotype` t SET t.`type` = 128 WHERE t.`BWINFTYP` LIKE 'PARENAEHR';
UPDATE `resinfotype` t SET t.`type` = 10 WHERE t.`BWINFTYP` LIKE 'ABWE';
UPDATE `resinfotype` t SET t.`type` = 30 WHERE t.`BWINFTYP` LIKE 'STURZPROT';
UPDATE `resinfotype` t SET t.`type` = 0 WHERE t.`BWINFTYP` LIKE 'PEG';
UPDATE `resinfotype` t SET t.`type` = 124 WHERE t.`BWINFTYP` LIKE 'WUNDANAM3a';
UPDATE `resinfotype` t SET t.`type` = 133 WHERE t.`BWINFTYP` LIKE 'ABSAUG';
UPDATE `resinfotype` t SET t.`type` = 118 WHERE t.`BWINFTYP` LIKE 'WUNDE2b';
UPDATE `resinfotype` t SET t.`type` = 117 WHERE t.`BWINFTYP` LIKE 'WUNDE1';
UPDATE `resinfotype` t SET t.`type` = 118 WHERE t.`BWINFTYP` LIKE 'WUNDE2a';
UPDATE `resinfotype` t SET t.`type` = 105 WHERE t.`BWINFTYP` LIKE 'NINSURANCE';
UPDATE `resinfotype` t SET t.`type` = 117 WHERE t.`BWINFTYP` LIKE 'WUNDE1a';
UPDATE `resinfotype` t SET t.`type` = 117 WHERE t.`BWINFTYP` LIKE 'WUNDE1b';
UPDATE `resinfotype` t SET t.`type` = 157 WHERE t.`BWINFTYP` LIKE 'STURZRIS';
UPDATE `resinfotype` t SET t.`type` = 121 WHERE t.`BWINFTYP` LIKE 'WUNDE5';
UPDATE `resinfotype` t SET t.`type` = 0 WHERE t.`BWINFTYP` LIKE 'AMBULANT';
UPDATE `resinfotype` t SET t.`type` = 119 WHERE t.`BWINFTYP` LIKE 'WUNDE3a';
UPDATE `resinfotype` t SET t.`type` = 101 WHERE t.`BWINFTYP` LIKE 'SPRACHE';
UPDATE `resinfotype` t SET t.`type` = 130 WHERE t.`BWINFTYP` LIKE 'HERZSCHRTT';
UPDATE `resinfotype` t SET t.`type` = 101 WHERE t.`BWINFTYP` LIKE 'RIECHEN';
UPDATE `resinfotype` t SET t.`type` = 101 WHERE t.`BWINFTYP` LIKE 'SEHEN';
UPDATE `resinfotype` t SET t.`type` = 128 WHERE t.`BWINFTYP` LIKE 'DIAET';
UPDATE `resinfotype` t SET t.`type` = 122 WHERE t.`BWINFTYP` LIKE 'WUNDANAM1a';
UPDATE `resinfotype` t SET t.`type` = 101 WHERE t.`BWINFTYP` LIKE 'SCHMECK';
UPDATE `resinfotype` t SET t.`type` = 0 WHERE t.`BWINFTYP` LIKE 'ZUZAHL';
UPDATE `resinfotype` t SET t.`type` = 128 WHERE t.`BWINFTYP` LIKE 'ESSEN';
UPDATE `resinfotype` t SET t.`type` = 118 WHERE t.`BWINFTYP` LIKE 'WUNDE2';
UPDATE `resinfotype` t SET t.`type` = 101 WHERE t.`BWINFTYP` LIKE 'HOERGERAET';
UPDATE `resinfotype` t SET t.`type` = 128 WHERE t.`BWINFTYP` LIKE 'SCHLUCKST';
UPDATE `resinfotype` t SET t.`type` = 128 WHERE t.`BWINFTYP` LIKE 'TRINKEN';
UPDATE `resinfotype` t SET t.`type` = 127 WHERE t.`BWINFTYP` LIKE 'SCHLAF';
UPDATE `resinfotype` t SET t.`type` = 128 WHERE t.`BWINFTYP` LIKE 'ZUBEREIT';
UPDATE `resinfotype` t SET t.`type` = 99 WHERE t.`BWINFTYP` LIKE 'ANSTECK1';
UPDATE `resinfotype` t SET t.`type` = 132 WHERE t.`BWINFTYP` LIKE 'GEFAHR';
UPDATE `resinfotype` t SET t.`type` = 132 WHERE t.`BWINFTYP` LIKE 'LESEN';
UPDATE `resinfotype` t SET t.`type` = 110 WHERE t.`BWINFTYP` LIKE 'BEWHM';
UPDATE `resinfotype` t SET t.`type` = 107 WHERE t.`BWINFTYP` LIKE 'MUNDPF';
UPDATE `resinfotype` t SET t.`type` = 112 WHERE t.`BWINFTYP` LIKE 'INKO2';
UPDATE `resinfotype` t SET t.`type` = 109 WHERE t.`BWINFTYP` LIKE 'HAUT';
UPDATE `resinfotype` t SET t.`type` = 0 WHERE t.`BWINFTYP` LIKE 'KLEIDEN';
UPDATE `resinfotype` t SET t.`type` = 101 WHERE t.`BWINFTYP` LIKE 'TASTEMPF';
-- neue Zuordnungen für types die bisher 0 waren. nur damit das konsistent ist und es keine 0 types mehr gibt
UPDATE `resinfotype` t SET t.`type` = 37 WHERE t.`BWINFTYP` LIKE 'KH';
UPDATE `resinfotype` t SET t.`type` = 47 WHERE t.`BWINFTYP` LIKE 'SEIZURE';
UPDATE `resinfotype` t SET t.`type` = 36 WHERE t.`BWINFTYP` LIKE 'FIXPROT1';
UPDATE `resinfotype` t SET t.`type` = 43 WHERE t.`BWINFTYP` LIKE 'PEMUK1';
UPDATE `resinfotype` t SET t.`type` = 41 WHERE t.`BWINFTYP` LIKE 'PEG2';
UPDATE `resinfotype` t SET t.`type` = 41 WHERE t.`BWINFTYP` LIKE 'PEG';
UPDATE `resinfotype` t SET t.`type` = 35 WHERE t.`BWINFTYP` LIKE 'EIGENTUM';
UPDATE `resinfotype` t SET t.`type` = 34 WHERE t.`BWINFTYP` LIKE 'DOLOPLUS';
UPDATE `resinfotype` t SET t.`type` = 38 WHERE t.`BWINFTYP` LIKE 'KLEIDEN';
UPDATE `resinfotype` t SET t.`type` = 39 WHERE t.`BWINFTYP` LIKE 'KONRISK';
UPDATE `resinfotype` t SET t.`type` = 31 WHERE t.`BWINFTYP` LIKE 'AMBULANT';
UPDATE `resinfotype` t SET t.`type` = 46 WHERE t.`BWINFTYP` LIKE 'PNEURISK';
UPDATE `resinfotype` t SET t.`type` = 40 WHERE t.`BWINFTYP` LIKE 'KONTRAKT';
UPDATE `resinfotype` t SET t.`type` = 32 WHERE t.`BWINFTYP` LIKE 'BESD1';
UPDATE `resinfotype` t SET t.`type` = 45 WHERE t.`BWINFTYP` LIKE 'PEMULT1';
UPDATE `resinfotype` t SET t.`type` = 48 WHERE t.`BWINFTYP` LIKE 'ZUZAHL';
UPDATE `resinfotype` t SET t.`type` = 33 WHERE t.`BWINFTYP` LIKE 'BRILLE';
UPDATE `resinfotype` t SET t.`type` = 44 WHERE t.`BWINFTYP` LIKE 'PEMULE1';
--
-- Wechsel der Kategorie von Wahrnehmung nach Alltag
UPDATE 'resinfotype' t
SET t.'BWIKID' = 12
WHERE t.'BWINFTYP' LIKE 'RIECHEN';;
UPDATE 'resinfotype' t
SET t.'BWIKID' = 12
WHERE t.'BWINFTYP' LIKE 'SPRACHVER';;
UPDATE 'resinfotype' t
SET t.'BWIKID' = 12
WHERE t.'BWINFTYP' LIKE 'BRILLE';;
UPDATE 'resinfotype' t
SET t.'BWIKID' = 12
WHERE t.'BWINFTYP' LIKE 'SEHEN';;
UPDATE 'resinfotype' t
SET t.'BWIKID' = 12
WHERE t.'BWINFTYP' LIKE 'HOERGERAET';;
UPDATE 'resinfotype' t
SET t.'BWIKID' = 12
WHERE t.'BWINFTYP' LIKE 'TASTEMPF';;
UPDATE 'resinfotype' t
SET t.'BWIKID' = 12
WHERE t.'BWINFTYP' LIKE 'HOEREN';;
UPDATE 'resinfotype' t
SET t.'BWIKID' = 12
WHERE t.'BWINFTYP' LIKE 'COMMS';;
UPDATE 'resinfotype' t
SET t.'BWIKID' = 12
WHERE t.'BWINFTYP' LIKE 'SCHMECK';;
UPDATE 'resinfotype' t
SET t.'BWIKID' = 12
WHERE t.'BWINFTYP' LIKE 'SPRACHE';;
-- TODO: Die Nursingprocesses greifen auch auf die Kategorien zu. Muss ich noch anpassen.
-- TODO: Schlaf noch löschen.
--
# Template
#        UPDATE 'resinfotype' SET 'type' = '-1' WHERE 'BWINFTYP' = 'respirat1';
#        INSERT INTO 'resinfotype' ('BWINFTYP', 'BWInfoKurz', 'BWInfoLang', 'BWIKID', 'type', 'IntervalMode', 'equiv', 'XML')
#                           VALUES ('respirat2', 'Atmung/Beatmung', '',     '15',     '133',       '0',         '22',
#                    '');
-- Tabelle 1 Variablen zur Erfassung von Versorgungsergebnissen (fortlaufende Nummerierung)
-- QDVS#23
UPDATE 'resinfotype'
SET 'deprecated' = 'true'
WHERE 'BWINFTYP' = 'respirat1';
--
INSERT INTO 'resinfotype' ('BWINFTYP', 'BWInfoKurz', 'BWInfoLang', 'BWIKID', 'type', 'IntervalMode', 'equiv', 'XML')
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
UPDATE 'resinfotype'
SET 'deprecated' = 'true'
WHERE 'BWINFTYP' = 'concious';
--
INSERT INTO 'resinfotype' ('BWINFTYP', 'BWInfoKurz', 'BWInfoLang', 'BWIKID', 'type', 'IntervalMode', 'equiv', 'XML')
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
UPDATE 'resinfotype' t
SET t.'equiv' = 129
WHERE t.'BWINFTYP' = 'ARTNUTRIT';
UPDATE 'resinfotype'
SET 'deprecated' = 'true'
WHERE 'BWINFTYP' = 'ARTNUTRIT';
--
INSERT INTO 'resinfotype' ('BWINFTYP', 'BWInfoKurz', 'BWInfoLang', 'BWIKID', 'type', 'IntervalMode', 'equiv', 'XML')
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
UPDATE 'resinfotype'
SET 'deprecated' = 'true'
WHERE 'BWINFTYP' = 'ORIENT1';
--
INSERT INTO 'resinfotype' ('BWINFTYP', 'BWInfoKurz', 'BWInfoLang', 'BWIKID', 'type', 'IntervalMode', 'equiv', 'XML')
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
UPDATE 'resinfotype'
SET 'deprecated' = 'true'
WHERE 'BWINFTYP' = 'MOBILITY';
--
INSERT INTO 'resinfotype' ('BWINFTYP', 'BWInfoKurz', 'BWInfoLang', 'BWIKID', 'type', 'IntervalMode', 'equiv', 'XML')
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
-- QDVS#45, 46, 57, 56, 58
UPDATE 'resinfotype'
SET 'deprecated' = 'true'
WHERE 'BWINFTYP' IN ('hinko', 'hinkon', 'excrem1', 'finco1');
--
INSERT INTO 'resinfotype' ('BWINFTYP', 'BWInfoKurz', 'BWInfoLang', 'BWIKID', 'type', 'IntervalMode', 'equiv', 'XML')
VALUES ('aussch01', 'Auscheidungen', '', '2', '159', '0',
        '0', '
    <tx tooltip="Sobald das Inkontinenzprofil nicht mehr auf ''Kontinenz'' steht, wird im Überleitbogen die Markierung für ''Harninkontinenz'' gesetzt."/>

    <optiongroup size="18" name="toilette"
                 label="Benutzen einer Toilette oder eines Toilettenstuhls"
                 qi="56" bi="4.4.10" tooltip="bi4.toilette.erklaerung" tx="Seite 1, Abschnitt 3">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.toilette.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.toilette.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.toilette.selbst2" layout="br left"/>
        <option label="unselbständig" name="3" tooltip="bi4.toilette.selbst3"/>
    </optiongroup>

    <separator/>

    <optiongroup size="18" name="stuhlangabe" label="Darmkontrolle, Stuhlkontinenz" qi="46"
                 bi="4.4 Angaben zur Versorgung"
                 tooltip="bi4.stuhlangabe.erklaerung">
        <option label="Person hat ein Colo- oder Ileostoma" name="1" tooltip="bi4.stuhlangabe.stufe1"/>
        <option label="ständig kontinent" name="2" default="true" tooltip="bi4.stuhlangabe.stufe2" layout="br left"/>
        <option label="überwiegend kontinent" name="3" tooltip="bi4.stuhlangabe.stufe3"/>
        <option label="überwiegend IN_kontinent" name="4" tooltip="bi4.stuhlangabe.stufe4" layout="br left"/>
        <option label="komplett IN_kontinent" name="5" tooltip="bi4.stuhlangabe.stufe5"/>
    </optiongroup>

    <optiongroup size="18" name="stuhlbewaeltigung"
                 label="Bewältigen der Folgen einer Stuhlinkontinenz und Umgang mit Stoma"
                 qi="58" bi="4.4.12" tooltip="bi4.stuhlbewaeltigung.erklaerung" tx="Seite 1, Abschnitt 3">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.stuhlbewaeltigung.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.stuhlbewaeltigung.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.stuhlbewaeltigung.selbst2" layout="br left"/>
        <option label="unselbständig" name="3" tooltip="bi4.stuhlbewaeltigung.selbst3"/>
    </optiongroup>

    <checkbox name="diarrhoe" label="Neigt zu Durchfällen" tx="Seite 1, Abschnitt 5" layout="br left"/>
    <checkbox name="obstipation" label="Neigt zu Verstopfung" tx="Seite 1, Abschnitt 5" layout="left"/>
    <checkbox name="digital" label="Digitales Ausräumen"
              tooltip="Das digitale Ausräumen beschreibt eine Maßnahme zur manuellen Entfernung von hartem Stuhl aus dem Enddarm.[br/]Diese Behandlung wird vorallem bei Koprostase, Stuhlimpaktion oder einer Darmlähmung durchgeführt."
              tx="Seite 1, Abschnitt 5" layout="left"/>

    <separator/>

    <optiongroup size="18" name="harnangabe" label="Blasenkontrolle/Harnkontinenz" qi="45"
                 bi="4.4 Angaben zur Versorgung"
                 tooltip="bi4.harnangabe.erklaerung">
        <option label="Person hat einen Dauerkatheter oder ein Urostoma" name="1" tooltip="bi4.harnangabe.stufe1"/>
        <option label="ständig kontinent" name="2" default="true" tooltip="bi4.harnangabe.stufe2" layout="br left"/>
        <option label="überwiegend kontinent" name="3" tooltip="bi4.harnangabe.stufe3"/>
        <option label="überwiegend IN_kontinent" name="4" tooltip="bi4.harnangabe.stufe4" layout="br left"/>
        <option label="komplett IN_kontinent" name="5" tooltip="bi4.harnangabe.stufe5"/>
    </optiongroup>

    <optiongroup size="18" name="hinkotag" label="Harn-Inkontinenzprofil (Tag)"
                 tooltip="exp.kontinenzprof.erklaerung">
        <option label="Kontinenz" name="kontinenz" default="true" tooltip="exp.harn.kontinenz"/>
        <option label="Unabhängig erreichte Kontinenz" name="uek" tooltip="exp.harn.uek"/>
        <option label="Abhängig erreichte Kontinenz" name="aek" tooltip="exp.harn.aek"/>
        <option label="Unabhängig kompensierte Inkontinenz" name="uki" tooltip="exp.harn.uki" layout="br left"/>
        <option label="Abhängig kompensierte Inkontinenz" name="aki" tooltip="exp.harn.aki"/>
        <option label="Nicht kompensierte Inkontinenz" name="nki" tooltip="exp.harn.nki" layout="br left"/>
    </optiongroup>

    <optiongroup size="18" name="hinkonacht" label="Harn-Inkontinenzprofil (Nacht)"
                 tooltip="exp.kontinenzprof.erklaerung">
        <option label="Kontinenz" name="kontinenz" default="true" tooltip="exp.harn.kontinenz"/>
        <option label="Unabhängig erreichte Kontinenz" name="uek" tooltip="exp.harn.uek"/>
        <option label="Abhängig erreichte Kontinenz" name="aek" tooltip="exp.harn.aek"/>
        <option label="Unabhängig kompensierte Inkontinenz" name="uki" tooltip="exp.harn.uki" layout="br left"/>
        <option label="Abhängig kompensierte Inkontinenz" name="aki" tooltip="exp.harn.aki"/>
        <option label="Nicht kompensierte Inkontinenz" name="nki" tooltip="exp.harn.nki" layout="br left"/>
    </optiongroup>

    <optiongroup size="18" name="harnbewaeltigung"
                 label="Bewältigen der Folgen einer Harninkontinenz und Umgang mit Dauerkatheter und Urostoma" qi="57"
                 bi="4.4.11" tooltip="bi4.harnbewaeltigung.erklaerung" tx="Seite 1, Abschnitt 3">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.harnbewaeltigung.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.harnbewaeltigung.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.harnbewaeltigung.selbst2" layout="br left"/>
        <option label="unselbständig" name="3" tooltip="bi4.harnbewaeltigung.selbst3"/>
    </optiongroup>

    <label size="18" label="Ursachenanalyse für eine Harninkontinenz"/>
    <label size="12" fontstyle="bold" label="Kognitive Einschränkung"/>
    <checkbox name="kogn1" label="Abnahme geistiger Leistungsfähigkeit"/>
    <checkbox name="kogn2" label="BewohnerIn bemerkt den Harndrang nicht" layout="left"/>
    <checkbox name="kogn3" label="BewohnerIn findet die Toilette nicht" layout="br"/>
    <checkbox name="kogn4" label="BewohnerIn hat Angst oder schämt sich bzgl. der Inkontinenz" layout="left"/>

    <label size="12" fontstyle="bold" label="Körperliche Einschränkung"/>
    <checkbox name="koerp1" label="BW schafft es nicht alleine auf die Toilette"/>
    <checkbox name="koerp2" label="BW ist gangunsicher" layout="left"/>
    <checkbox name="koerp3" label="hat Schwierigkeiten das Gleichgewicht zu halten" layout="left"/>
    <checkbox name="koerp4" label="Muskelkraft fehlt" layout="br"/>
    <checkbox name="koerp5" label="Flexibilität des Körpers fehlt" layout="left"/>
    <checkbox name="koerp6" label="Fingerfertigkeit fehlt"
              tooltip="z.B. fehlt dem BW die Fähigkeit die Hose oder den Rock aufzuknöpfen." layout="left"/>
    <checkbox name="koerp7" label="BW sieht schlecht" layout="left"/>
    <checkbox name="koerp8" label="BW ist immobil" layout="br"/>

    <label size="12" fontstyle="bold" label="Erkrankungen"/>
    <checkbox name="erk1" label="Schlaganfall"/>
    <checkbox name="erk2" label="MS" layout="left"/>
    <checkbox name="erk3" label="Parkinson" layout="left"/>
    <checkbox name="erk4" label="Demenz" layout="left"/>
    <checkbox name="erk5" label="Diabetes Mellitus" layout="left"/>
    <checkbox name="erk6" label="Herzinsuffizienz" layout="left"/>

    <label size="12" fontstyle="bold" label="Medikamente"
           tooltip="Erhält der BW Medikamente, welche die Harn-Inkontinenz fördern ?"/>
    <checkbox name="med1" label="Diuretika"/>
    <checkbox name="med2" label="Anticholinergika" layout="left"/>
    <checkbox name="med3" label="Antihistaminika" layout="left"/>
    <checkbox name="med4" label="Antidepressiva" layout="left"/>
    <checkbox name="med5" label="Neuroleptika" layout="left"/>
    <checkbox name="med6" label="Kalziumantagonisten" layout="br"/>
    <checkbox name="med7" label="Opiate" layout="left"/>

    <label size="12" fontstyle="bold" label="Besonderheiten"/>
    <checkbox name="bes1" label="Harnwegsinfekt" tooltip="Bitte Ausschluss mittels Urinanalyse wenn nötig"/>
    <checkbox name="bes2" label="Obstipation (bei Frauen)" layout="left"/>
    <checkbox name="bes3" label="Beckbodenschwäche (bei Frauen) z.B. bei Adipositas" layout="left"/>
    <checkbox name="bes4" label="Östrogenmangel (bei Frauen)" layout="br"/>
    <checkbox name="bes5" label="Veränderungen der Prostata (bei Männern)" layout="left"/>
');
--
-- QDVS#47, 48, 49, 50, 51, 52
UPDATE 'resinfotype'
SET 'deprecated' = 'true'
WHERE 'BWINFTYP' IN ('care', 'mouthcare');
--
INSERT INTO 'resinfotype' ('BWINFTYP', 'BWInfoKurz', 'BWInfoLang', 'BWIKID', 'type', 'IntervalMode', 'equiv', 'XML')
VALUES ('kpflege02', 'Körperpflege', '', '2', '108', '0', '1', '
             <optiongroup size="18" name="oberkoerper" label="Waschen des vorderen Oberkörpers" qi="47" bi="4.4.1"
                          tooltip="bi4.oberkoerper.erklaerung" tx="Seite 1, Abschnitt 3">
                 <option label="selbstständig" name="0" default="true" tooltip="bi4.oberkoerper.selbst0"/>
                 <option label="überwiegend selbständig" name="1" tooltip="bi4.oberkoerper.selbst1"/>
                 <option label="überwiegend unselbständig" name="2" tooltip="bi4.oberkoerper.selbst2"/>
                 <option label="unselbständig" name="3" tooltip="bi4.oberkoerper.selbst3"/>
             </optiongroup>

             <optiongroup size="18" name="kopf" label="Körperpflege im Bereich des Kopfes" qi="48" bi="4.4.2"
                          tooltip="bi4.kopf.erklaerung" tx="Seite 1, Abschnitt 3">
                 <option label="selbstständig" name="0" default="true" tooltip="bi4.kopf.selbst0"/>
                 <option label="überwiegend selbständig" name="1" tooltip="bi4.kopf.selbst1"/>
                 <option label="überwiegend unselbständig" name="2" tooltip="bi4.kopf.selbst2"/>
                 <option label="unselbständig" name="3" tooltip="bi4.kopf.selbst3"/>
             </optiongroup>

             <optiongroup size="18" name="intim" label="Waschen des Intimbereichs" qi="49" bi="4.4.3"
                          tooltip="bi4.intim.erklaerung" tx="Seite 1, Abschnitt 3">
                 <option label="selbstständig" name="0" default="true" tooltip="bi4.intim.selbst0"/>
                 <option label="überwiegend selbständig" name="1" tooltip="bi4.intim.selbst1"/>
                 <option label="überwiegend unselbständig" name="2" tooltip="bi4.intim.selbst2"/>
                 <option label="unselbständig" name="3" tooltip="bi4.intim.selbst3"/>
             </optiongroup>

             <optiongroup size="18" name="baden" label="Duschen und Baden einschließlich Waschen der Haare" qi="50" bi="4.4.4"
                          tooltip="bi4.baden.erklaerung" tx="Seite 1, Abschnitt 3">
                 <option label="selbstständig" name="0" default="true" tooltip="bi4.baden.selbst0"/>
                 <option label="überwiegend selbständig" name="1" tooltip="bi4.baden.selbst1"/>
                 <option label="überwiegend unselbständig" name="2" tooltip="bi4.baden.selbst2"/>
                 <option label="unselbständig" name="3" tooltip="bi4.baden.selbst3"/>
             </optiongroup>

             <optiongroup size="18" name="okankleiden" label="An- und Auskleiden des Oberkörpers" qi="51"
                          bi="4.4.5" tooltip="bi4.okankleiden.erklaerung" tx="Seite 1, Abschnitt 3">
                 <option label="selbstständig" name="0" default="true" tooltip="bi4.okankleiden.selbst0"/>
                 <option label="überwiegend selbständig" name="1" tooltip="bi4.okankleiden.selbst1"/>
                 <option label="überwiegend unselbständig" name="2" tooltip="bi4.okankleiden.selbst2"/>
                 <option label="unselbständig" name="3" tooltip="bi4.okankleiden.selbst3"/>
             </optiongroup>

             <optiongroup size="18" name="ukankleiden" label="An- und Auskleiden des Unterkörpers" qi="52"
                          bi="4.4.6" tooltip="bi4.ukankleiden.erklaerung" tx="Seite 1, Abschnitt 3">
                 <option label="selbstständig" name="0" default="true" tooltip="bi4.ukankleiden.selbst0"/>
                 <option label="überwiegend selbständig" name="1" tooltip="bi4.ukankleiden.selbst1"/>
                 <option label="überwiegend unselbständig" name="2" tooltip="bi4.ukankleiden.selbst2"/>
                 <option label="unselbständig" name="3" tooltip="bi4.ukankleiden.selbst3"/>
             </optiongroup>

             <textfield label="Bevorzugte Pflegemittel" name="preferred.careproducts" innerlayout="left"
                           tx="Seite 1, Abschnitt 3"/>

             <separator/>
             <!-- ==================Spezielle Mundpflege================== -->
             <label size="18" fontstyle="bold" label="Gründe für eine spezielle Mundpflege"/>
             <checkbox name="zahnlosigkeit" label="Zahnlosigkeit" layout="br"/>
             <checkbox name="mundtrockenheit" label="extreme Mundtrockenheit (durch Mundatmung, durch Medikamente)" layout="left"/>
             <checkbox name="trockene.lippen" label="Trockene Lippen (Rhagade)" layout="left"/>
             <checkbox name="zungenbelag" label="Schleimhautbelägen der Zunge" layout="br"/>
             <checkbox name="speichel.dickf" label="dickflüssiger Speichel" layout="left"/>
             <checkbox name="lockere.zaehne" label="lockere Zähne"  layout="left"/>
             <checkbox name="laesion.mund" label="Schädigungen des Mundes" layout="left"/>
             <checkbox name="soor" label="Soor (Pilzbefall)"  layout="left"/>
             <checkbox name="stomatitis" label="Stomatitis (Mundentzündung)" layout="br"/>
             <checkbox name="gingivitis" label="Gingivitis (Zahnfleischentzündung)"  layout="left"/>
             <checkbox name="aphten" label="Erosionen mit entzündlichem Randsaum (Aphten)" layout="left"/>
             <checkbox name="herpes" label="Lippenherpes"  layout="br"/>
             <checkbox name="blutung" label="erhöhter Blutungsneigung" layout="left"/>
             <checkbox name="mundflora" label="Zerstörung der physiologischen Mundflora" tooltip="durch Medikamente, Kortison, Zytostatika, Antibiotika"  layout="left"/>
             <checkbox name="sauerstoff" label="längerfristiger Sauerstofftherapie (Mund trocknet aus)" layout="br"/>
             <checkbox name="absaugen" label="wiederholte Nasale oder Orale Absaugvorgänge" layout="left"/>
             <checkbox name="bewusstlos" label="Bewußtlosigkeit" layout="left"/>
             <checkbox name="verletzung" label="Bei Verletzungen am Kiefer und in der Mundhöhle"  layout="br"/>
             <checkbox name="nahrungskarenz" label="Bei Nahrungskarenz (PEG, Parenterale Ernährung (s.c.))" layout="left"/>
             <checkbox name="schluckstoerung" label="Schluckstörungen" layout="br"/>
             <checkbox name="az" label="reduzierter Allgemeinzustand" layout="left"/>
             <checkbox name="praefinal" label="während des Sterbeprozesses" layout="left"/>
             <checkbox name="immunschwaeche" label="Immunschwäche" layout="left"/>
');
-- QDVS#
UPDATE 'resinfotype'
SET 'deprecated' = 'true'
WHERE 'BWINFTYP' IN ('food');
--
--
INSERT INTO `resinfotype` ('BWINFTYP', 'BWInfoKurz', 'BWInfoLang', 'BWIKID', 'type', 'IntervalMode', 'equiv', 'XML')
VALUES ('ern01', 'Essen und Trinken', '', '4', '128', '0', '18', '
    <optiongroup name="essen" size="18" label="Essen" tooltip="bi4.essen.erklaerung" qi="54" bi="4.4.8">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.essen.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.essen.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.essen.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi4.essen.selbst3"/>
    </optiongroup>

    <optiongroup name="trinken" size="18" label="Trinken" tooltip="bi4.trinken.erklaerung" qi="55" bi="4.4.9">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.trinken.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.trinken.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.trinken.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi4.trinken.selbst3"/>
    </optiongroup>

    <optiongroup name="mundgerecht" size="18" label="Mundgerechtes Zubereiten der Nahrung, Eingießen von Getränken"
                 tooltip="bi4.mundgerecht.erklaerung" qi="53" bi="4.4.7">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.mundgerecht.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.mundgerecht.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.mundgerecht.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi4.mundgerecht.selbst3"/>
    </optiongroup>

    <label size="18" label="Sonstige Angaben"/>
    <checkbox label="Nahrungskarenz" name="abrosia" layout="br left"/>
    <checkbox label="Diätkost" name="diet" layout="left"/>
    <checkbox label="Schluckstörungen" name="dysphagia" layout="left"/>

    <separator/>
    <textfield name="likes" label="Vorlieben, Essen und Trinken" hfill="false" length="40"/>
    <textfield name="hates" label="Abneigungen, Essen und Trinken" hfill="false" length="40"/>
    <separator/>

    <textfield name="zieltrinkmenge" label="Zieltrinkmenge (ml in 24h)"
               tooltip="Hinterlegen Sie die zugehörige ärztliche Verordnung." layout="br" hfill="false" type="double"
               length="20"/>

    <textfield name="ubw" label="Übliches Körpergewicht"
               tooltip="Was [der|die] Bewohner[in] als [ihr|sein] übliches Gewicht angibt. Einheit: kg" type="double"
               layout="br" hfill="false" length="20"/>
');