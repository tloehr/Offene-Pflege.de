-- Ab Version 1.14.4.x
UPDATE `sysprops`
SET V = '13'
WHERE K = 'dbstructure';
-- Verbindung zwischen ResInfos herstellen (QDVS)
alter table `resinfo`
    comment '';
alter table `resinfo`
    add connectionid bigint default 0 not null;
-- Felder für die QDVS
alter table `homes`
    add maxcap              int default 0 null comment 'Belegungskapazitaet',
    add erhebungszeitraum   int default 1 null comment 'ERHEBUNGSZEITRAUM für die QDVS. Eindeutig durchnummertiert. Wird mit jeder Meldung um 1 erhöht.',
    add auswertungszeitraum int default 1 null comment 'Gilt innerhalb einer Erhebung. Kann 1 oder 2 sein. 0, wenn gerade keine Erhebung läuft. Für die QDVS',
    add careproviderid      int default 1 null comment 'Von DAS-PFLEGE eindeutig der Einrichtung zugewiesene 6 Stellige Kennung';
--
-- Diese Kennung ist neu für die QDVS.
alter table `resident`
    add idbewohner int default 1 not null comment 'QDVS: Bewohnerbezogene Nummer gemäß Erhebungsbogen';
-- Die bestehenden BWs werden einmal durchnummeriert und erhalten dadurch eine eindeutige Kennung.
SET @counter := 0;
UPDATE `resident` r
SET r.idbewohner = (SELECT @counter := @counter + 1)
WHERE 1 = 1; -- um die "unsafe query" Warnung zu umgehen
CREATE UNIQUE INDEX resident_idbewohner_uindex ON resident (idbewohner);
-- damit auch zukünftige BWs eine eindeutige id erhalten.
ALTER TABLE `uniqueid`
    MODIFY PREFIX CHAR(20) DEFAULT '' NOT NULL;
INSERT INTO `uniqueid` (`UID`, `PREFIX`, `version`)
VALUES ((SELECT MAX(idbewohner) FROM resident) + 1, '__idbewohner', 0);
--
alter table `resinfotype`
    add `deprecated` tinyint default 0 null;
update `resinfotype`
SET `deprecated` = 1
WHERE type < 0;
UPDATE `resinfotype` t
SET t.`type` = 128
WHERE t.`BWINFTYP` LIKE 'ISSTGERN';
UPDATE `resinfotype` t
SET t.`type` = 119
WHERE t.`BWINFTYP` LIKE 'WUNDE3';
UPDATE `resinfotype` t
SET t.`type` = 30
WHERE t.`BWINFTYP` LIKE 'STURZPROT2';
UPDATE `resinfotype` t
SET t.`type` = 110
WHERE t.`BWINFTYP` LIKE 'HAENDIG';
UPDATE `resinfotype` t
SET t.`type` = 138
WHERE t.`BWINFTYP` LIKE 'FIXIERUNG';
UPDATE `resinfotype` t
SET t.`type` = 105
WHERE t.`BWINFTYP` LIKE 'PSTF';
UPDATE `resinfotype` t
SET t.`type` = 127
WHERE t.`BWINFTYP` LIKE 'SCHLAFGEW';
UPDATE `resinfotype` t
SET t.`type` = 128
WHERE t.`BWINFTYP` LIKE 'KAUEN';
UPDATE `resinfotype` t
SET t.`type` = 97
WHERE t.`BWINFTYP` LIKE 'ALLERGIE';
UPDATE `resinfotype` t
SET t.`type` = 132
WHERE t.`BWINFTYP` LIKE 'ORIENTIERU';
UPDATE `resinfotype` t
SET t.`type` = 102
WHERE t.`BWINFTYP` LIKE 'ANGEH';
UPDATE `resinfotype` t
SET t.`type` = 126
WHERE t.`BWINFTYP` LIKE 'WUNDANAM5a';
UPDATE `resinfotype` t
SET t.`type` = 125
WHERE t.`BWINFTYP` LIKE 'WUNDANAM4a';
UPDATE `resinfotype` t
SET t.`type` = 112
WHERE t.`BWINFTYP` LIKE 'INKOAID';
UPDATE `resinfotype` t
SET t.`type` = 128
WHERE t.`BWINFTYP` LIKE 'ZUSATZKOST';
UPDATE `resinfotype` t
SET t.`type` = 120
WHERE t.`BWINFTYP` LIKE 'WUNDE4';
UPDATE `resinfotype` t
SET t.`type` = 108
WHERE t.`BWINFTYP` LIKE 'KPFLEGE';
UPDATE `resinfotype` t
SET t.`type` = 132
WHERE t.`BWINFTYP` LIKE 'WEGLAUF';
UPDATE `resinfotype` t
SET t.`type` = 99
WHERE t.`BWINFTYP` LIKE 'INFECT1';
UPDATE `resinfotype` t
SET t.`type` = 131
WHERE t.`BWINFTYP` LIKE 'BEWUSST';
UPDATE `resinfotype` t
SET t.`type` = 137
WHERE t.`BWINFTYP` LIKE 'SCHMERZE';
UPDATE `resinfotype` t
SET t.`type` = 99
WHERE t.`BWINFTYP` LIKE 'ANSTECK';
UPDATE `resinfotype` t
SET t.`type` = 101
WHERE t.`BWINFTYP` LIKE 'SPRACHVER';
UPDATE `resinfotype` t
SET t.`type` = 127
WHERE t.`BWINFTYP` LIKE 'TAGNACHT';
UPDATE `resinfotype` t
SET t.`type` = 104
WHERE t.`BWINFTYP` LIKE 'FSTAND';
UPDATE `resinfotype` t
SET t.`type` = 133
WHERE t.`BWINFTYP` LIKE 'TRACHEOST';
UPDATE `resinfotype` t
SET t.`type` = 119
WHERE t.`BWINFTYP` LIKE 'WUNDE3b';
UPDATE `resinfotype` t
SET t.`type` = 128
WHERE t.`BWINFTYP` LIKE 'TRINKTGERN';
UPDATE `resinfotype` t
SET t.`type` = 30
WHERE t.`BWINFTYP` LIKE 'STURZPROT3';
UPDATE `resinfotype` t
SET t.`type` = 110
WHERE t.`BWINFTYP` LIKE 'BEWEGUNG';
UPDATE `resinfotype` t
SET t.`type` = 101
WHERE t.`BWINFTYP` LIKE 'HOEREN';
UPDATE `resinfotype` t
SET t.`type` = 120
WHERE t.`BWINFTYP` LIKE 'WUNDE4a';
UPDATE `resinfotype` t
SET t.`type` = 121
WHERE t.`BWINFTYP` LIKE 'WUNDE5a';
UPDATE `resinfotype` t
SET t.`type` = 121
WHERE t.`BWINFTYP` LIKE 'WUNDE5b';
UPDATE `resinfotype` t
SET t.`type` = 110
WHERE t.`BWINFTYP` LIKE 'BETTLAE';
UPDATE `resinfotype` t
SET t.`type` = 120
WHERE t.`BWINFTYP` LIKE 'WUNDE4b';
UPDATE `resinfotype` t
SET t.`type` = 123
WHERE t.`BWINFTYP` LIKE 'WUNDANAM2a';
UPDATE `resinfotype` t
SET t.`type` = 132
WHERE t.`BWINFTYP` LIKE 'MERK';
UPDATE `resinfotype` t
SET t.`type` = 117
WHERE t.`BWINFTYP` LIKE 'WUNDE';
UPDATE `resinfotype` t
SET t.`type` = 128
WHERE t.`BWINFTYP` LIKE 'PARENAEHR';
UPDATE `resinfotype` t
SET t.`type` = 10
WHERE t.`BWINFTYP` LIKE 'ABWE';
UPDATE `resinfotype` t
SET t.`type` = 30
WHERE t.`BWINFTYP` LIKE 'STURZPROT';
UPDATE `resinfotype` t
SET t.`type` = 124
WHERE t.`BWINFTYP` LIKE 'WUNDANAM3a';
UPDATE `resinfotype` t
SET t.`type` = 133
WHERE t.`BWINFTYP` LIKE 'ABSAUG';
UPDATE `resinfotype` t
SET t.`type` = 118
WHERE t.`BWINFTYP` LIKE 'WUNDE2b';
UPDATE `resinfotype` t
SET t.`type` = 117
WHERE t.`BWINFTYP` LIKE 'WUNDE1';
UPDATE `resinfotype` t
SET t.`type` = 118
WHERE t.`BWINFTYP` LIKE 'WUNDE2a';
UPDATE `resinfotype` t
SET t.`type` = 105
WHERE t.`BWINFTYP` LIKE 'NINSURANCE';
UPDATE `resinfotype` t
SET t.`type` = 117
WHERE t.`BWINFTYP` LIKE 'WUNDE1a';
UPDATE `resinfotype` t
SET t.`type` = 117
WHERE t.`BWINFTYP` LIKE 'WUNDE1b';
UPDATE `resinfotype` t
SET t.`type` = 157
WHERE t.`BWINFTYP` LIKE 'STURZRIS';
UPDATE `resinfotype` t
SET t.`type` = 121
WHERE t.`BWINFTYP` LIKE 'WUNDE5';
UPDATE `resinfotype` t
SET t.`type` = 119
WHERE t.`BWINFTYP` LIKE 'WUNDE3a';
UPDATE `resinfotype` t
SET t.`type` = 101
WHERE t.`BWINFTYP` LIKE 'SPRACHE';
UPDATE `resinfotype` t
SET t.`type` = 130
WHERE t.`BWINFTYP` LIKE 'HERZSCHRTT';
UPDATE `resinfotype` t
SET t.`type` = 101
WHERE t.`BWINFTYP` LIKE 'RIECHEN';
UPDATE `resinfotype` t
SET t.`type` = 101
WHERE t.`BWINFTYP` LIKE 'SEHEN';
UPDATE `resinfotype` t
SET t.`type` = 128
WHERE t.`BWINFTYP` LIKE 'DIAET';
UPDATE `resinfotype` t
SET t.`type` = 122
WHERE t.`BWINFTYP` LIKE 'WUNDANAM1a';
UPDATE `resinfotype` t
SET t.`type` = 101
WHERE t.`BWINFTYP` LIKE 'SCHMECK';
UPDATE `resinfotype` t
SET t.`type` = 128
WHERE t.`BWINFTYP` LIKE 'ESSEN';
UPDATE `resinfotype` t
SET t.`type` = 118
WHERE t.`BWINFTYP` LIKE 'WUNDE2';
UPDATE `resinfotype` t
SET t.`type` = 101
WHERE t.`BWINFTYP` LIKE 'HOERGERAET';
UPDATE `resinfotype` t
SET t.`type` = 128
WHERE t.`BWINFTYP` LIKE 'SCHLUCKST';
UPDATE `resinfotype` t
SET t.`type` = 128
WHERE t.`BWINFTYP` LIKE 'TRINKEN';
UPDATE `resinfotype` t
SET t.`type` = 127
WHERE t.`BWINFTYP` LIKE 'SCHLAF';
UPDATE `resinfotype` t
SET t.`type` = 128
WHERE t.`BWINFTYP` LIKE 'ZUBEREIT';
UPDATE `resinfotype` t
SET t.`type` = 99
WHERE t.`BWINFTYP` LIKE 'ANSTECK1';
UPDATE `resinfotype` t
SET t.`type` = 132
WHERE t.`BWINFTYP` LIKE 'GEFAHR';
UPDATE `resinfotype` t
SET t.`type` = 132
WHERE t.`BWINFTYP` LIKE 'LESEN';
UPDATE `resinfotype` t
SET t.`type` = 110
WHERE t.`BWINFTYP` LIKE 'BEWHM';
UPDATE `resinfotype` t
SET t.`type` = 107
WHERE t.`BWINFTYP` LIKE 'MUNDPF';
UPDATE `resinfotype` t
SET t.`type` = 112
WHERE t.`BWINFTYP` LIKE 'INKO2';
UPDATE `resinfotype` t
SET t.`type` = 109
WHERE t.`BWINFTYP` LIKE 'HAUT';
UPDATE `resinfotype` t
SET t.`type` = 101
WHERE t.`BWINFTYP` LIKE 'TASTEMPF';
-- neue Zuordnungen für types die bisher 0 waren. nur damit das konsistent ist und es keine 0 types mehr gibt
UPDATE `resinfotype` t
SET t.`type` = 37
WHERE t.`BWINFTYP` LIKE 'KH';
UPDATE `resinfotype` t
SET t.`type` = 47
WHERE t.`BWINFTYP` LIKE 'SEIZURE';
UPDATE `resinfotype` t
SET t.`type` = 36
WHERE t.`BWINFTYP` LIKE 'FIXPROT1';
UPDATE `resinfotype` t
SET t.`type` = 43
WHERE t.`BWINFTYP` LIKE 'PEMUK1';
UPDATE `resinfotype` t
SET t.`type` = 41
WHERE t.`BWINFTYP` LIKE 'PEG2';
UPDATE `resinfotype` t
SET t.`type` = 41
WHERE t.`BWINFTYP` LIKE 'PEG';
UPDATE `resinfotype` t
SET t.`type` = 35
WHERE t.`BWINFTYP` LIKE 'EIGENTUM';
UPDATE `resinfotype` t
SET t.`type`   = 34,
    t.`BWIKID` = 15
WHERE t.`BWINFTYP` LIKE 'DOLOPLUS';
UPDATE `resinfotype` t
SET t.`type` = 38
WHERE t.`BWINFTYP` LIKE 'KLEIDEN';
UPDATE `resinfotype` t
SET t.`type` = 39
WHERE t.`BWINFTYP` LIKE 'KONRISK';
UPDATE `resinfotype` t
SET t.`type` = 31
WHERE t.`BWINFTYP` LIKE 'AMBULANT';
UPDATE `resinfotype` t
SET t.`type` = 46
WHERE t.`BWINFTYP` LIKE 'PNEURISK';
UPDATE `resinfotype` t
SET t.`type` = 40
WHERE t.`BWINFTYP` LIKE 'KONTRAKT';
UPDATE `resinfotype` t
SET t.`type`   = 167,
    t.`BWIKID` = 15
WHERE t.`BWINFTYP` LIKE 'BESD1';
UPDATE `resinfotype` t
SET t.`BWIKID` = 15
WHERE t.`BWINFTYP` LIKE 'schmerze1';
UPDATE `resinfotype` t
SET t.`BWIKID` = 15
WHERE t.`BWINFTYP` LIKE 'schmerze';
UPDATE `resinfotype` t
SET t.`type` = 45
WHERE t.`BWINFTYP` LIKE 'PEMULT1';
UPDATE `resinfotype` t
SET t.`type` = 48
WHERE t.`BWINFTYP` LIKE 'ZUZAHL';
UPDATE `resinfotype` t
SET t.`type` = 33
WHERE t.`BWINFTYP` LIKE 'BRILLE';
UPDATE `resinfotype` t
SET t.`type` = 44
WHERE t.`BWINFTYP` LIKE 'PEMULE1';
--
-- Wechsel der Kategorie von Wahrnehmung nach Alltag
UPDATE `resinfotype` t
SET t.`BWIKID` = 12
WHERE t.`BWINFTYP` LIKE 'RIECHEN';
UPDATE `resinfotype` t
SET t.`BWIKID` = 12
WHERE t.`BWINFTYP` LIKE 'SPRACHVER';
UPDATE `resinfotype` t
SET t.`BWIKID` = 12
WHERE t.`BWINFTYP` LIKE 'BRILLE';
UPDATE `resinfotype` t
SET t.`BWIKID` = 12
WHERE t.`BWINFTYP` LIKE 'SEHEN';
UPDATE `resinfotype` t
SET t.`BWIKID` = 12
WHERE t.`BWINFTYP` LIKE 'HOERGERAET';
UPDATE `resinfotype` t
SET t.`BWIKID` = 12
WHERE t.`BWINFTYP` LIKE 'TASTEMPF';
UPDATE `resinfotype` t
SET t.`BWIKID` = 12
WHERE t.`BWINFTYP` LIKE 'HOEREN';
UPDATE `resinfotype` t
SET t.`BWIKID` = 12
WHERE t.`BWINFTYP` LIKE 'COMMS';
UPDATE `resinfotype` t
SET t.`BWIKID` = 12
WHERE t.`BWINFTYP` LIKE 'SCHMECK';
UPDATE `resinfotype` t
SET t.`BWIKID` = 12
WHERE t.`BWINFTYP` LIKE 'SPRACHE';
--
-- Ersetzte InfoTypes
UPDATE `resinfotype`
SET `deprecated` = '1'
WHERE `BWINFTYP` IN
      ('confidants', 'respirat1', 'mobility', 'orient1', 'artnutrit', 'hinko', 'hinkon', 'excrem1', 'finco1', 'care',
       'mouthcare', 'food', 'sleep1', 'ninsur02', 'amputation', 'wound1', 'wound2', 'wound3', 'wound4', 'wound5',
       'wound6', 'wound7', 'wound8', 'wound9', 'wound10', 'fixprot1', 'woundh1', 'woundh2', 'woundh3', 'woundh4',
       'woundh5', 'woundh6', 'woundh7', 'woundh8', 'woundh9', 'woundh10', 'schmerze1', 'doloplus', 'besd1',
       'conscious');
-- Kategorien
UPDATE `resinfocategory` t
SET t.Bezeichnung = 'Alltagsleben, Soziales'
WHERE t.BWIKID = 12;
UPDATE `resinfocategory` t
SET t.Bezeichnung = 'Selbstversorgung'
WHERE t.BWIKID = 2;
--
UPDATE `resinfotype` t
SET t.BWIKID = 2
WHERE t.BWIKID = 1;
UPDATE `resinfotype` t
SET t.BWIKID = 2
WHERE t.BWIKID = 4;
UPDATE `resinfotype` t
SET t.BWIKID = 2
WHERE t.BWIKID = 18;
UPDATE `resinfotype` t
SET t.BWIKID = 2
WHERE t.BWIKID = 10;
UPDATE `resinfotype` t
SET t.BWIKID = 2
WHERE t.BWIKID = 16;
UPDATE `resinfotype` t
SET t.BWIKID = 2
WHERE t.BWIKID = 19;
UPDATE `resinfotype` t
SET t.BWIKID = 12
WHERE t.BWIKID = 7;
--
UPDATE `nursingprocess` n
SET n.BWIKID = 2
WHERE n.BWIKID = 1;
UPDATE `nursingprocess` n
SET n.BWIKID = 2
WHERE n.BWIKID = 4;
UPDATE `nursingprocess` n
SET n.BWIKID = 2
WHERE n.BWIKID = 18;
UPDATE `nursingprocess` n
SET n.BWIKID = 2
WHERE n.BWIKID = 10;
UPDATE `nursingprocess` n
SET n.BWIKID = 2
WHERE n.BWIKID = 16;
UPDATE `nursingprocess` n
SET n.BWIKID = 2
WHERE n.BWIKID = 19;
UPDATE `nursingprocess` n
SET n.BWIKID = 12
WHERE n.BWIKID = 7;
--
DELETE
FROM `resinfocategory`
WHERE BWIKID = 20;
DELETE
FROM `resinfocategory`
WHERE BWIKID = 1;
DELETE
FROM `resinfocategory`
WHERE BWIKID = 4;
DELETE
FROM `resinfocategory`
WHERE BWIKID = 18;
DELETE
FROM `resinfocategory`
WHERE BWIKID = 7;
DELETE
FROM `resinfocategory`
WHERE BWIKID = 10;
DELETE
FROM `resinfocategory`
WHERE BWIKID = 16;
DELETE
FROM `resinfocategory`
WHERE BWIKID = 19;
--
-- Wird mit kern01 in einer neuen equiv zusammengefasst.
UPDATE `resinfotype` t
SET t.`equiv` = 129
WHERE t.`BWINFTYP` = 'ARTNUTRIT';
UPDATE `resinfotype` t
SET t.`equiv` = 137
WHERE t.`BWINFTYP` IN ('hinko', 'hinkon', 'excrem1', 'finco1');
UPDATE `resinfotype` t
SET t.`equiv` = 1
WHERE t.`BWINFTYP` IN ('MOUTHCARE', 'MUNDPF');
--
-- Tabelle 1 Variablen zur Erfassung von Versorgungsergebnissen (fortlaufende Nummerierung)
-- QDVS#23
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('respirat2', 'Atmung/Beatmung', '', '8', '133', '0', '22',
        '
         <tx tooltip="Diese Eintragungen werden in den Überleitbogen übernommen. Seite 2, Abschnitt 12."/>

    <tabgroup size="18" fontstyle="bold" label="Atmung" name="atmung">
        <checkbox label="unauffällig" name="normal"/>
        <checkbox label="kardialer Stau" name="cardcongest" layout="left"/>
        <checkbox label="Schmerzen" name="pain" layout="left"/>
        <checkbox label="Husten" name="cough" layout="left"/>
        <checkbox label="Verschleimung" name="mucous" layout="br"/>
        <checkbox label="Auswurf" name="sputum" layout="left"/>
        <checkbox label="Rauchen" name="smoking" layout="left"/>
        <checkbox label="Asthma" name="asthma" layout="left"/>
        <textfield name="other" label="Sonstiges" length="20"/>
    </tabgroup>

    <tabgroup size="18" fontstyle="bold" label="Besonderheiten" name="besonderheiten">
        <checkbox label="Tracheostoma" name="stoma"/>
        <checkbox label="Silberkanüle" name="silver" layout="left"/>
        <checkbox label="Silikonkanüle" name="silicon" layout="left"/>
        <checkbox label="Absaugen" name="aspirate" layout="left"/>
        <textfield name="tubetype" label="Kanülenart" length="10"/>
        <textfield name="tubesize" label="Kanülengröße" length="10" layout="left"/>
    </tabgroup>

    <optiongroup size="18" name="BEATMUNG" label="Wird der:die Bewohner:in beatmet ?"
                 qdvs="Zeile(n) 22 im DAS Dokumentationsbogen."
                 tooltip="Die Antwortmöglichkeit „invasive Beatmung“ trifft zu, wenn die Beatmung durch eine Trachealkanüle erfolgt. Ansonsten ist „nicht invasiv“ anzukreuzen.">
        <option label="nein" default="true" name="0"/>
        <option label="ja, invasive Beatmung" name="1"/>
        <option label="ja, aber nicht invasiv" name="2"/>
    </optiongroup>
');
--
-- QDVS#24
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('bewusst01', 'Bewusstseinszustand', '', '5', '131', '0', '21',
        '

    <optiongroup size="18" name="BEWUSSTSEINSZUSTAND"
                 label="Bewusstseinszustand"
                 qdvs="Zeile(n) 23 im DAS Dokumentationsbogen." tooltip="bi6.beschaeftigen.erklaerung"
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

');
--
-- QDVS#42, 43, 44
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('kern01', 'Künstliche Ernährung', '', '2', '129', '0', '129',
        '
      <qdvs tooltip="Sobald diese Bewohner-Information eingetragen wurde, geht das System von einer künstlichen Ernährung aus. "/>
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
          <optiongroup name="SVERNAEHRUNGUMFANG" label="In welchem Umfang erfolgt eine künstliche Ernährung?"
                      qdvs="Zeile(n) 43 im DAS Dokumentationsbogen." >
              <option label="nicht täglich oder nicht dauerhaft" name="0" default="true"/>
              <option label="täglich, aber zusätzlich zur oralen Ernährung" name="6"/>
              <option label="ausschließlich oder nahezu ausschließlich künstliche Ernährung" name="3"/>
          </optiongroup>

          <optiongroup name="SVFREMDHILFE" label="Erfolgt die Bedienung selbständig oder mit Fremdhilfe?" qdvs="Zeile(n) 42 im DAS Dokumentationsbogen.">
              <option label="selbständig" name="0" default="true"/>
              <option label="mit Fremdhilfe" name="1"/>
          </optiongroup>

');
--
-- QDVS#25, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41
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

    <optiongroup name="KKFERKENNEN" size="18" label="Erkennen von Personen aus dem näheren Umfeld"
                 tooltip="bi2.personen.erklaerung" qdvs="Zeile(n) 30 im DAS Dokumentationsbogen.">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.personen.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.personen.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.personen.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.personen.selbst3"/>
    </optiongroup>

    <optiongroup name="KKFORIENTOERTLICH" size="18" label="Örtliche Orientierung" tooltip="bi2.orte.erklaerung"
                 qdvs="Zeile(n) 31 im DAS Dokumentationsbogen.">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.orte.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.orte.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.orte.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.orte.selbst3"/>
    </optiongroup>

    <optiongroup name="KKFORIENTZEITLICH" size="18" label="Zeitliche Orientierung" tooltip="bi2.zeitlich.erklaerung"
                 qdvs="Zeile(n) 32 im DAS Dokumentationsbogen.">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.zeitlich.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.zeitlich.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.zeitlich.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.zeitlich.selbst3"/>
    </optiongroup>

    <optiongroup name="KKFERINNERN" size="18" label="Sich Erinnern" tooltip="bi2.erinnern.erklaerung"
                 qdvs="Zeile(n) 33 im DAS Dokumentationsbogen.">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.erinnern.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.erinnern.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.erinnern.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.erinnern.selbst3"/>
    </optiongroup>

    <optiongroup name="KKFHANDLUNGEN" size="18" label="Steuern von mehrschrittigen Alltagshandlungen"
                 tooltip="bi2.handlungen.erklaerung" qdvs="Zeile(n) 34 im DAS Dokumentationsbogen.">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.handlungen.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.handlungen.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.handlungen.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.handlungen.selbst3"/>
    </optiongroup>

    <optiongroup name="KKFENTSCHEIDUNGEN" size="18" label="Treffen von Entscheidungen im Alltagsleben"
                 tooltip="bi2.entscheidungen.erklaerung" qdvs="Zeile(n) 35 im DAS Dokumentationsbogen.">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.entscheidungen.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.entscheidungen.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.entscheidungen.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.entscheidungen.selbst3"/>
    </optiongroup>

    <optiongroup name="KKFVERSTEHENINFO" size="18" label="Verstehen von Sachverhalten und Informationen"
                 tooltip="bi2.verstehen.erklaerung" qdvs="Zeile(n) 36 im DAS Dokumentationsbogen.">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.verstehen.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.verstehen.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.verstehen.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.verstehen.selbst3"/>
    </optiongroup>

    <optiongroup name="KKFGEFAHRERKENNEN" size="18" label="Erkennen von Risiken und Gefahren"
                 tooltip="bi2.risiken.erklaerung"
                 qdvs="Zeile(n) 37 im DAS Dokumentationsbogen.">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.risiken.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.risiken.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.risiken.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.risiken.selbst3"/>
    </optiongroup>

    <optiongroup name="KKFMITTEILEN" size="18" label="Mitteilen von elementaren Bedürfnissen"
                 tooltip="bi2.beduerfnissen.erklaerung" qi="Zeile 39">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.beduerfnissen.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.beduerfnissen.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.beduerfnissen.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.beduerfnissen.selbst3"/>
    </optiongroup>

    <optiongroup name="KKFVERSTEHENAUF" size="18" label="Verstehen von Aufforderungen"
                 tooltip="bi2.aufforderungen.erklaerung" qdvs="Zeile(n) 39 im DAS Dokumentationsbogen.">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.aufforderungen.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.aufforderungen.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.aufforderungen.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.aufforderungen.selbst3"/>
    </optiongroup>

    <optiongroup name="KKFBETEILIGUNG" size="14" label="Beteiligung an einem Gespräch"
                 tooltip="bi2.gespraech.erklaerung"
                 qdvs="Zeile(n) 40 im DAS Dokumentationsbogen.">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.gespraech.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.gespraech.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.gespraech.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.gespraech.selbst3"/>
    </optiongroup>
');
--
-- QDVS#26, 27, 28, 29, 30, 31, 32
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('mobil02', 'Mobilität', '', '3', '110', '0',
        '14', '
 <tx tooltip="[b]Seite 1, Abschnitt 4.[/b][br/]Alles was Sie hier als Bemerkung eintragen, steht hinterher in der Bemerkungs-Zeile dieses Abschnitts im Überleitbogen.[br/][b]Lagerungsarten[/b] werden anhand der Pflegeplanungen bestimmt."/>
    <checkbox name="bedridden" label="bettlägerig"/>
    <optiongroup size="18" name="MOBILPOSWECHSEL" label="Positionswechsel im Bett" qdvs="Zeile(n) 25 im DAS Dokumentationsbogen."
                 tooltip="bi1.bett.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi1.bett.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi1.bett.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi1.bett.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi1.bett.selbst3"/>
    </optiongroup>
    <textfield label="Hilfsmittel" name="MOBILPOSWECHSEL.hilfsmittel" innerlayout="left"/>

    <optiongroup size="18" name="MOBILSITZPOSITION" label="Halten einer stabilen Sitzposition" qdvs="Zeile(n) 26 im DAS Dokumentationsbogen."
                 tooltip="bi1.sitz.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi1.sitz.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi1.sitz.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi1.sitz.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi1.sitz.selbst3"/>
    </optiongroup>
    <textfield label="Hilfsmittel" name="MOBILSITZPOSITION.hilfsmittel" innerlayout="left"/>

    <optiongroup size="18" name="MOBILUMSETZEN" label="Umsetzen" qdvs="Zeile(n) 27 im DAS Dokumentationsbogen." tooltip="bi1.umsetzen.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi1.umsetzen.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi1.umsetzen.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi1.umsetzen.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi1.umsetzen.selbst3"/>
    </optiongroup>
    <textfield label="Hilfsmittel" name="MOBILSITZPOSITION.hilfsmittel" innerlayout="left"/>

    <optiongroup size="18" name="MOBILFORTBEWEGUNG" label="Fortbewegen innerhalb des Wohnbereichs" qdvs="Zeile(n) 28 im DAS Dokumentationsbogen."
                 tooltip="bi1.wohnbereich.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi1.wohnbereich.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi1.wohnbereich.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi1.wohnbereich.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi1.wohnbereich.selbst3"/>
    </optiongroup>
    <tabgroup label="Hilfsmittel zur Bewegung" name="hilfsmittel">
        <checkbox name="rollstuhl" label="Rollstuhl"/>
        <checkbox name="kruecke" label="Unterarmgehstütze"/>
        <checkbox name="rollator" label="Rollator"/>
        <checkbox name="gehstock" label="Gehstock"/>
    </tabgroup>

    <optiongroup size="18" name="MOBILTREPPENSTEIGEN" label="Treppensteigen" tooltip="bi1.treppen.erklaerung"
                qdvs="Zeile(n) 29 im DAS Dokumentationsbogen.">
        <option label="selbstständig" name="0" default="true" tooltip="bi1.treppen.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi1.treppen.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi1.treppen.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi1.treppen.selbst3"/>
    </optiongroup>

    <checkbox name="unfaegig-arme-beine" label="Gebrauchsunfähigkeit beider Arme und beider Beine"
              tooltip="bi1.unfaehig.arme.beine"
              bi="4.1.6"/>

');
-- QDVS#45, 46, 57, 56, 58
--   todo: hier txessen prüfen
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('aussch01', 'Ausscheidungen', '', '2', '159', '0', '137', '
    <tx tooltip="Sobald das Inkontinenzprofil nicht mehr auf &quot;Kontinenz&quot; steht, wird im Überleitbogen die Markierung für &quot;Harninkontinenz&quot; gesetzt."/>

    <optiongroup size="18" name="SVTOILETTE"
                 label="Benutzen einer Toilette oder eines Toilettenstuhls"
                 qdvs="Zeile(n) 55 im DAS Dokumentationsbogen." tooltip="bi4.toilette.erklaerung"
                 tx="Seite 1, Abschnitt 3">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.toilette.selbst0"/>
        <option label="überwiegend selbständig" name="2" tooltip="bi4.toilette.selbst2"/>
        <option label="überwiegend unselbständig" name="4" tooltip="bi4.toilette.selbst4" layout="br left"/>
        <option label="unselbständig" name="6" tooltip="bi4.toilette.selbst6"/>
    </optiongroup>

    <separator/>

    <optiongroup size="18" name="SVSTUHLKONTINENZ" label="Darmkontrolle, Stuhlkontinenz"
                 qdvs="Zeile(n) 45 im DAS Dokumentationsbogen."
                 tooltip="bi4.stuhlangabe.erklaerung">

        <option label="ständig kontinent" name="0" default="true" tooltip="bi4.stuhlangabe.stufe0" layout="br left"/>
        <option label="überwiegend kontinent" name="1" tooltip="bi4.stuhlangabe.stufe1"/>
        <option label="überwiegend IN_kontinent" name="2" tooltip="bi4.stuhlangabe.stufe2" layout="br left"/>
        <option label="komplett IN_kontinent" name="3" tooltip="bi4.stuhlangabe.stufe3"/>
        <option label="Person hat ein Colo- oder Ileostoma" name="4" tooltip="bi4.stuhlangabe.stufe4"/>
    </optiongroup>

    <optiongroup size="18" name="SVSTUHLKONTINENZBEW"
                 label="Bewältigen der Folgen einer Stuhlinkontinenz und Umgang mit Stoma"
                 qdvs="Zeile(n) 57 im DAS Dokumentationsbogen." tooltip="bi4.stuhlbewaeltigung.erklaerung"
                 tx="Seite 1, Abschnitt 3">
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
    <checkbox name="ap.aid" label="Anus Praeter" tx="Seite 1, Abschnitt 5"/>
    <separator/>

    <optiongroup size="18" name="SVHARNKONTINENZ" label="Blasenkontrolle/Harnkontinenz"
                 qdvs="Zeile(n) 44 im DAS Dokumentationsbogen."
                 tooltip="bi4.harnangabe.erklaerung">
        <option label="ständig kontinent" name="0" default="true" tooltip="bi4.harnangabe.stufe0" layout="br left"/>
        <option label="überwiegend kontinent" name="1" tooltip="bi4.harnangabe.stufe1"/>
        <option label="überwiegend IN_kontinent" name="2" tooltip="bi4.harnangabe.stufe2" layout="br left"/>
        <option label="komplett IN_kontinent" name="3" tooltip="bi4.harnangabe.stufe3"/>
        <option label="Person hat einen Dauerkatheter oder ein Urostoma" name="4" tooltip="bi4.harnangabe.stufe4"/>
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

    <optiongroup size="18" name="SVHARNKONTINENZBEW"
                 label="Bewältigen der Folgen einer Harninkontinenz und Umgang mit Dauerkatheter und Urostoma"
                 qdvs="Zeile(n) 56 im DAS Dokumentationsbogen."
                 tooltip="bi4.harnbewaeltigung.erklaerung" tx="Seite 1, Abschnitt 3">
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
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('kpflege02', 'Körperpflege', '', '2', '108', '0',
        '1', '
            <tx
        tooltip="[b]Seite 1, Abschnitt 3. &quot;Grundpflege&quot;[/b]
            [br/]Die Markierungen im Abschnitt &quot;Grundpflege&quot; werden entsprechenden Ihren Einträgen in diesem Formular gesetzt.
            [br/]Der Bemerkungs-Text wird in die Bemerkungs-Zeile dieses Abschnitts im Überleitbogen übernommen."/>

    <optiongroup size="18" name="SVOBERKOERPER" label="Waschen des vorderen Oberkörpers" qdvs="Zeile 47" bi="4.4.1"
                 tooltip="bi4.oberkoerper.erklaerung" >
        <option label="selbstständig" name="0" default="true" tooltip="bi4.oberkoerper.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.oberkoerper.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.oberkoerper.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi4.oberkoerper.selbst3"/>
    </optiongroup>

    <optiongroup size="18" name="SVKOPF" label="Körperpflege im Bereich des Kopfes" qdvs="Zeile 48" bi="4.4.2"
                 tooltip="bi4.kopf.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.kopf.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.kopf.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.kopf.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi4.kopf.selbst3"/>
    </optiongroup>

    <label size="14" fontstyle="bold" label="Pflege des Kopfes umfasst auch:"/>
    <checkbox name="SVKOPF.kaemmen" label="Kämmen" layout="left"/>
    <checkbox name="SVKOPF.mundpflege" label="Mundpflege" layout="left"/>
    <checkbox name="SVKOPF.rasur" label="Rasieren" layout="left"/>
    <checkbox name="SVKOPF.zahnprothese" label="Zahnprothese" layout="left"/>

    <optiongroup size="18" name="SVINTIMBEREICH" label="Waschen des Intimbereichs" qdvs="Zeile 49" bi="4.4.3"
                 tooltip="bi4.intim.erklaerung" >
        <option label="selbstständig" name="0" default="true" tooltip="bi4.intim.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.intim.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.intim.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi4.intim.selbst3"/>
    </optiongroup>

    <optiongroup size="18" name="SVDUSCHENBADEN" label="Duschen und Baden einschließlich Waschen der Haare"
                 qdvs="Zeile 50"
                 bi="4.4.4"
                 tooltip="bi4.baden.erklaerung" >
        <option label="selbstständig" name="0" default="true" tooltip="bi4.baden.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.baden.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.baden.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi4.baden.selbst3"/>
    </optiongroup>

    <optiongroup size="18" name="SVANAUSOBERKOERPER" label="An- und Auskleiden des Oberkörpers" qdvs="Zeile 51"
                 bi="4.4.5" tooltip="bi4.okankleiden.erklaerung" >
        <option label="selbstständig" name="0" default="true" tooltip="bi4.okankleiden.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.okankleiden.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.okankleiden.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi4.okankleiden.selbst3"/>
    </optiongroup>

    <optiongroup size="18" name="SVANAUSUNTERKOERPER" label="An- und Auskleiden des Unterkörpers" qdvs="Zeile 52"
                 bi="4.4.6" tooltip="bi4.ukankleiden.erklaerung" >
        <option label="selbstständig" name="0" default="true" tooltip="bi4.ukankleiden.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.ukankleiden.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.ukankleiden.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi4.ukankleiden.selbst3"/>
    </optiongroup>

    <label size="14" fontstyle="bold" label="Hilfen erfolgen im/am/in:"/>
    <checkbox name="personal.care.bed" label="Bett" layout="left"/>
    <checkbox name="personal.care.shower" label="Bad, Dusche" layout="left"/>
    <checkbox name="personal.care.basin" label="Waschbecken" layout="left"/>

    <textfield label="Bevorzugte Pflegemittel" name="preferred.careproducts" innerlayout="left"/>

    <!-- ==================Spezielle Mundpflege================== -->
    <label size="18" fontstyle="bold" label="Gründe für eine spezielle Mundpflege"/>

    <checkbox name="zahnlosigkeit" label="Zahnlosigkeit" layout="br"/>
    <checkbox name="mundtrockenheit" label="extreme Mundtrockenheit (durch Mundatmung, durch Medikamente)"
              layout="left"/>
    <checkbox name="trockene.lippen" label="Trockene Lippen (Rhagade)" layout="left"/>
    <checkbox name="zungenbelag" label="Schleimhautbelägen der Zunge" layout="br"/>
    <checkbox name="speichel.dickf" label="dickflüssiger Speichel" layout="left"/>
    <checkbox name="lockere.zaehne" label="lockere Zähne" layout="left"/>
    <checkbox name="laesion.mund" label="Schädigungen des Mundes" layout="left"/>
    <checkbox name="soor" label="Soor (Pilzbefall)" layout="left"/>
    <checkbox name="stomatitis" label="Stomatitis (Mundentzündung)" layout="br"/>
    <checkbox name="gingivitis" label="Gingivitis (Zahnfleischentzündung)" layout="left"/>
    <checkbox name="aphten" label="Erosionen mit entzündlichem Randsaum (Aphten)" layout="left"/>
    <checkbox name="herpes" label="Lippenherpes" layout="br"/>
    <checkbox name="blutung" label="erhöhter Blutungsneigung" layout="left"/>
    <checkbox name="mundflora" label="Zerstörung der physiologischen Mundflora"
              tooltip="durch Medikamente, Kortison, Zytostatika, Antibiotika" layout="left"/>
    <checkbox name="sauerstoff" label="längerfristiger Sauerstofftherapie (Mund trocknet aus)" layout="br"/>
    <checkbox name="absaugen" label="wiederholte Nasale oder Orale Absaugvorgänge" layout="left"/>
    <checkbox name="bewusstlos" label="Bewußtlosigkeit" layout="left"/>
    <checkbox name="verletzung" label="Bei Verletzungen am Kiefer und in der Mundhöhle" layout="br"/>
    <checkbox name="nahrungskarenz" label="Bei Nahrungskarenz (PEG, Parenterale Ernährung (s.c.))" layout="left"/>
    <checkbox name="schluckstoerung" label="Schluckstörungen" layout="br"/>
    <checkbox name="az" label="reduzierter Allgemeinzustand" layout="left"/>
    <checkbox name="praefinal" label="während des Sterbeprozesses" layout="left"/>
    <checkbox name="immunschwaeche" label="Immunschwäche" layout="left"/>
');
-- QDVS#53,54,55
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('ern01', 'Essen und Trinken', '', '2', '128', '0', '18', '
       <optiongroup name="SVESSEN" size="18" label="Essen" tooltip="bi4.essen.erklaerung"
                 qdvs="Zeile(n) 53 im DAS Dokumentationsbogen.">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.essen.selbst0"/>
        <option label="überwiegend selbständig" name="3" tooltip="bi4.essen.selbst3"/>
        <option label="überwiegend unselbständig" name="6" tooltip="bi4.essen.selbst6"/>
        <option label="unselbständig" name="9" tooltip="bi4.essen.selbst9"/>
    </optiongroup>

    <optiongroup name="SVTRINKEN" size="18" label="Trinken" tooltip="bi4.trinken.erklaerung"
                 qdvs="Zeile(n) 54 im DAS Dokumentationsbogen.">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.trinken.selbst0"/>
        <option label="überwiegend selbständig" name="2" tooltip="bi4.trinken.selbst2"/>
        <option label="überwiegend unselbständig" name="4" tooltip="bi4.trinken.selbst4"/>
        <option label="unselbständig" name="6" tooltip="bi4.trinken.selbst6"/>
    </optiongroup>

    <optiongroup name="SVNAHRUNGZUBEREITEN" size="18" tooltip="bi4.mundgerecht.erklaerung" qdvs="Zeile(n) 52 im DAS Dokumentationsbogen."
                 label="Mundgerechtes Zubereiten der Nahrung, Eingießen von Getränken">

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
-- QDVS#59,61,62
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('alltag01', 'Alltagsleben', '', '12', '160', '0', '135', '

    <optiongroup size="18" name="GATAGESABLAUF"
                 label="Tagesablauf gestalten und an Veränderungen anpassen"
                 qdvs="Zeile(n) 58 im DAS Dokumentationsbogen." tooltip="bi6.alltag.erklaerung" tx="Seite 1, Abschnitt 8">
        <option label="selbstständig" name="0" default="true" tooltip="bi6.alltag.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi6.alltag.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi6.alltag.selbst2" layout="br left"/>
        <option label="unselbständig" name="3" tooltip="bi6.alltag.selbst3"/>
    </optiongroup>

    <optiongroup size="18" name="GABESCHAEFTIGEN"
                 label="Sich beschäftigen"
                 qdvs="Zeile(n) 60 im DAS Dokumentationsbogen." tooltip="bi6.beschaeftigen.erklaerung" tx="Seite 1, Abschnitt 8">
        <option label="selbstständig" name="0" default="true" tooltip="bi6.beschaeftigen.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi6.beschaeftigen.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi6.beschaeftigen.selbst2" layout="br left"/>
        <option label="unselbständig" name="3" tooltip="bi6.beschaeftigen.selbst3"/>
    </optiongroup>

    <optiongroup size="18" name="GAPLANUNGEN"
                 label="Vornehmen von in die Zukunft gerichteten Planungen"
                 qdvs="Zeile(n) 61 im DAS Dokumentationsbogen." tooltip="bi6.zukunft.erklaerung" tx="Seite 1, Abschnitt 8">
        <option label="selbstständig" name="0" default="true" tooltip="bi6.zukunft.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi6.zukunft.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi6.zukunft.selbst2" layout="br left"/>
        <option label="unselbständig" name="3" tooltip="bi6.zukunft.selbst3"/>
    </optiongroup>

');
-- QDVS#60
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('schlaf02', 'Ruhen und Schlafen', '', '12', '127', '0', '17', '
       <optiongroup size="18" name="GARUHENSCHLAFEN"
                 label="Ruhen und Schlafen"
                 qdvs="Zeile(n) 59 im DAS Dokumentationsbogen." tooltip="bi6.schlafen.erklaerung" tx="Seite 1, Abschnitt 8">
        <option label="selbstständig" name="0" default="true" tooltip="bi6.schlafen.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi6.schlafen.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi6.schlafen.selbst2" layout="br left"/>
        <option label="unselbständig" name="3" tooltip="bi6.schlafen.selbst3"/>
    </optiongroup>


    <label size="18" fontstyle="bold" label="Schlafeigenschaften"/>
    <checkbox name="normal" label="normal"/>
    <checkbox name="einschlaf" label="Einschlafstörungen" layout="left"/>
    <checkbox name="durchschlaf" label="Durchschlafstörungen" layout="left"/>
    <checkbox name="unruhe" label="nächtliche Unruhe"/>
    <checkbox name="daynight" label="Tag-/Nachtrhythmus gestört" layout="left"/>

    <label size="18" fontstyle="bold" label="Schlaflage"/>
    <checkbox name="left" label="links"/>
    <checkbox name="right" label="rechts" layout="left"/>
    <checkbox name="front" label="Bauchlage" layout="left"/>
    <checkbox name="back" label="Rückenlage" layout="left"/>

    <textfield name="schlafhilfen" innerlayout="br" label="Welche Schlafhilfen oder Gewohnheiten sind bekannt"/>
');
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('sozial01', 'Soziales', '', '12', '161', '0', '23', '
     <tabgroup size="18" fontstyle="bold" label="Hauptbezugsperson" name="hbp">
        <tx tooltip="Diese Angaben werden auf Seite 1, Abschnitt 1 eingetragen."/>
        <textfield label="Anrede" name="c1title" length="25" hfill="false" innerlayout="tab"/>
        <textfield label="Name" name="c1name" length="25" hfill="false" innerlayout="tab"/>
        <textfield label="Vorname" name="c1firstname" length="25" hfill="false" innerlayout="tab"/>
        <textfield label="Strasse" name="c1street" length="25" hfill="false" innerlayout="tab"/>
        <textfield label="PLZ" name="c1zip" length="25" hfill="false" innerlayout="tab"/>
        <textfield label="Ort" name="c1city" length="25" hfill="false" innerlayout="tab"/>
        <textfield label="Tel" name="c1tel" length="25" hfill="false" innerlayout="tab"/>
        <textfield label="Mobil" name="c1mobile" length="25" hfill="false" innerlayout="tab"/>
        <textfield label="E-Mail" name="c1email" length="25" hfill="false" innerlayout="tab"/>
        <checkbox name="c1ready2nurse" label="Pflegebereitschaft der Bezugsperson"
                  tx="Setzt ''Pflegebereitschaft der Bezugsperson'' auf ''ja'', falls angeklickt."/>
    </tabgroup>


    <tabgroup size="18" fontstyle="bold" label="Angehörige 1" name="a1">
        <textfield name="name1" label="Name" length="25" hfill="false" innerlayout="tab"/>
        <textfield name="ans1" label="Anschrift" length="25" hfill="false" innerlayout="tab"/>
        <textfield name="tel1" label="Telefon" length="25" hfill="false" innerlayout="tab"/>
    </tabgroup>

    <tabgroup size="18" fontstyle="bold" label="Angehörige 2" name="a2">
        <textfield name="name2" label="Name" length="25" hfill="false" innerlayout="tab"/>
        <textfield name="ans2" label="Anschrift" length="25" hfill="false" innerlayout="tab"/>
        <textfield name="tel2" label="Telefon" length="25" hfill="false" innerlayout="tab"/>
    </tabgroup>

    <optiongroup size="18" name="GAINTERAKTION"
                 label="Interaktion mit Personen im direkten Kontakt"
                 qi="63" bi="4.6.5" tooltip="bi6.interaktion.erklaerung" tx="Seite 1, Abschnitt 8">
        <option label="selbstständig" name="0" default="true" tooltip="bi6.interaktion.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi6.interaktion.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi6.interaktion.selbst2" layout="br left"/>
        <option label="unselbständig" name="3" tooltip="bi6.interaktion.selbst3"/>
    </optiongroup>

    <optiongroup size="18" name="GAKONTAKTPFLEGE"
                 label="Kontaktpflege zu Personen außerhalb des direkten Umfelds"
                 qi="64" bi="4.6.6" tooltip="bi6.kontaktpflege.erklaerung" tx="Seite 1, Abschnitt 8">
        <option label="selbstständig" name="0" default="true" tooltip="bi6.kontaktpflege.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi6.beschaeftigen.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi6.kontaktpflege.selbst2" layout="br left"/>
        <option label="unselbständig" name="3" tooltip="bi6.kontaktpflege.selbst3"/>
    </optiongroup>

');
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('ninsur03', 'Pflegekasse', '', '13', '105', '0', '15',
        '
       <textfield label="Name" name="niname" innerlayout="tab"/>
    <textfield label="Strasse" name="nistreet" innerlayout="tab"/>
    <textfield label="PLZ" name="nizip" innerlayout="tab"/>
    <textfield label="Ort" name="nicity" innerlayout="tab"/>
    <textfield label="Tel" name="nitel" innerlayout="tab"/>
    <textfield label="Fax" name="nifax" innerlayout="tab"/>
    <textfield label="Zuständig" name="nicontact" innerlayout="tab"/>
    <separator/>
    <textfield label="Versicherten Nr." name="personno" innerlayout="tab"/>
    <textfield label="Krankenkassen Nr." name="insuranceno" innerlayout="tab"/>
    <separator/>
    <optiongroup name="grade" label="Einstufung" tx="Wird auf Seite 1 eingetragen."
                 qdvs="Zeile(n) 8 im DAS Dokumentationsbogen.">
        <option label="Kein Pflegegrad" name="0" layout="br"/>
        <option label="Pflegegrad 1" name="1"/>
        <option label="Pflegegrad 2" name="2"/>
        <option label="Pflegegrad 3" name="3" layout="br"/>
        <option label="Pflegegrad 4" name="4"/>
        <option label="Pflegegrad 5" name="5"/>
        <option label="andere (bitte in Beschreibung)" name="other" layout="br"/>
    </optiongroup>

    <checkbox name="requested" label="Einstufung beantrag" layout="left"/>
    <textfield label="Beantragt am" name="requestdate" length="12" type="date" innerlayout="tab"
               tx="Wird auf Seite 1 eingetragen."/>
');
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('fraktur01', 'Knochenbruch (Fraktur)', '', '15', '162', '3', '0',
        '<textfield label="Datum" length="12" name="datum" type="date" preset="currentdate"
                            qdvs="Zeile(n) 11, 12 im DAS Dokumentationsbogen."/>');
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('herzinf01', 'Herzinfarkt', '', '15', '163', '3', '0',
        '<textfield label="Datum" length="12" name="datum" type="date" preset="currentdate"
                            qdvs="Zeile(n) 13, 14 im DAS Dokumentationsbogen."/>');
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('amput01', 'Amputation', '', '15', '140', '0', '138',
        '
          <label size="16" fontstyle="bold"
           label="Amputationsangaben (werden zur Berechnung der angepassten Köpergewichte herangezogen)"/>
    <tx tooltip="Diese Eintragungen werden in den Überleitbogen übernommen. Seite 2, Abschnitt 18."/>
    <qdvs
            tooltip="Zeile(n) 15, 16 im DAS Dokumentationsbogen. Bitte besonders auf das Datum der Amputation achten."/>

    <optiongroup name="upperleft" label="linkes, oberes Viertel">
        <option label="keine Amputation" name="none" default="true"/>
        <option label="linke Hand" name="hand"/>
        <option label="unterhalb des linken Ellenbogens" name="belowellbow"/>
        <option label="oberhalb des linken Ellenbogens" name="aboveellbow" layout="br tab"/>
        <option label="vollständige linke, obere Extremität" name="complete"/>
    </optiongroup>

    <datefield label="Datum der Operation" name="dateupperleft" depends-on="upperleft"
               visible-when-dependency-neq="none" default-value-when-shown="now"/>

    <separator/>

    <optiongroup name="upperright" label="rechtes, oberes Viertel">
        <option label="keine Amputation" name="none" default="true"/>
        <option label="rechte Hand" name="hand"/>
        <option label="unterhalb des rechten Ellenbogens" name="belowellbow"/>
        <option label="oberhalb des rechten Ellenbogens" name="aboveellbow" layout="br tab"/>
        <option label="vollständige rechte, obere Extremität" name="complete"/>
    </optiongroup>

    <datefield label="Datum der Operation" name="dateupperright" depends-on="upperright"
               visible-when-dependency-neq="none" default-value-when-shown="now"/>
    <separator/>

    <optiongroup name="lowerleft" label="linkes, unteres Viertel">
        <option label="keine Amputation" name="none" default="true"/>
        <option label="linker Fuß" name="foot"/>
        <option label="unterhalb des linken Knies" name="belowknee"/>
        <option label="oberhalb des linken Knies" name="aboveknee" layout="br tab"/>
        <option label="vollständige linke, untere Extremität" name="complete"/>
    </optiongroup>

    <datefield label="Datum der Operation" name="datelowerleft" depends-on="lowerleft"
               visible-when-dependency-neq="none" default-value-when-shown="now"/>
    <separator/>

    <optiongroup name="lowerright" label="rechtes, unteres Viertel">
        <option label="keine Amputation" name="none" default="true"/>
        <option label="rechter Fuß" name="foot"/>
        <option label="unterhalb des rechten Knies" name="belowknee"/>
        <option label="oberhalb des rechten Knies" name="aboveknee" layout="br tab"/>
        <option label="vollständige rechte, untere Extremität" name="complete"/>
    </optiongroup>

    <datefield label="Datum der Operation" name="datelowerright" depends-on="lowerright"
               visible-when-dependency-neq="none" default-value-when-shown="now"/>
');
--
-- Reparaturen
UPDATE `resinfotype` t
SET t.`equiv` = 136
WHERE t.`BWINFTYP` LIKE 'HINSURANCE' ESCAPE '#';
UPDATE `resinfotype` t
SET t.`type`  = 106,
    t.`equiv` = 136
WHERE t.`BWINFTYP` LIKE 'ZUZAHL' ESCAPE '#';
UPDATE `resinfotype` t
SET t.`equiv` = 11
WHERE t.`BWINFTYP` LIKE 'KH' ESCAPE '#';
UPDATE `resinfotype` t
SET t.`equiv` = 11
WHERE t.`BWINFTYP` LIKE 'AMBULANT' ESCAPE '#';
UPDATE `resinfotype` t
SET t.`type`  = 108,
    t.`equiv` = 1
WHERE t.`BWINFTYP` LIKE 'KLEIDEN' ESCAPE '#';
--
UPDATE `resinfotype` t
SET t.`XML` = '<textfield label="Datum" length="12" name="datum" type="date" preset="currentdate" qdvs="Diese Information ist ein Indikator für die Qualitätsprüfung."/>'
WHERE t.`BWINFTYP` LIKE 'apoplex01';
UPDATE `resinfotype` t
SET t.`equiv` = 138
WHERE t.`BWINFTYP` LIKE 'AMPUTATION' ESCAPE '#';
--
-- Wunden
--
UPDATE `resinfotype` t
SET t.`equiv` = 139
WHERE t.`BWINFTYP` = 'WOUND6';
UPDATE `resinfotype` t
SET t.`equiv` = 140
WHERE t.`BWINFTYP` = 'WOUND7';
UPDATE `resinfotype` t
SET t.`equiv` = 141
WHERE t.`BWINFTYP` = 'WOUND8';
UPDATE `resinfotype` t
SET t.`equiv` = 142
WHERE t.`BWINFTYP` = 'WOUND9';
UPDATE `resinfotype` t
SET t.`equiv` = 143
WHERE t.`BWINFTYP` = 'WOUND10';
UPDATE `resinfotype` t
SET t.`equiv` = 144
WHERE t.`BWINFTYP` = 'WOUNDH6';
UPDATE `resinfotype` t
SET t.`equiv` = 145
WHERE t.`BWINFTYP` = 'WOUNDH7';
UPDATE `resinfotype` t
SET t.`equiv` = 146
WHERE t.`BWINFTYP` = 'WOUNDH8';
UPDATE `resinfotype` t
SET t.`equiv` = 147
WHERE t.`BWINFTYP` = 'WOUNDH9';
UPDATE `resinfotype` t
SET t.`equiv` = 148
WHERE t.`BWINFTYP` = 'WOUNDH10';
--
-- Wunden
--
SET @wundxml = '<tx tooltip="Wunden werden auf dem Überleitbogen auf Seite 2 Abschnitt 19 eingetragen. Ebenso wirken sich die Eintragunegn auf den Abschnitt 10 aus (Wundschmerz, Wunden)"/>
    <qdvs
            tooltip="Zeile(n) 64 - 71 im DAS Dokumentationsbogen (Abschnitt &quot;Dekubitus&quot;)"/>
    <label
            label="Sie können mehr als eine Stelle markieren, aber beschreiben Sie unbedingt nur *eine* Wunde pro Formular."
            size="16" fontstyle="bold"/>
    <bodyscheme name="bs1"/>

    <checkbox name="dekubitus" label="Diese Wunde ist ein Druckgeschwür (Dekubitus)"
              tx="Erscheint auf dem Überleitbogen auf Seite 1 Abschnitt 7."
              lockedforchanges="true" default="true"/>

    <combobox label="Kategorien nach EPUAP" name="epuap"
              tooltip="Denken Sie bitte daran, dass auch im weiteren Heilungs-Verlauf die anfängliche Dekubitus-Kategorie [b]nicht mehr geändert[/b] wird. Sie bleibt bis zur Epithalisierung bestehen. Ein Dekubitus entwickelt sich also nicht rückwärts."
              lockedforchanges="true" depends-on="dekubitus" visible-when-dependency-eq="true"
              default-value-when-shown="1">
        <item label="Kategorie I: Nicht wegdrückbare Rötung" name="1"
              tooltip="Nicht wegdrückbare, umschriebene Rötung bei intakter Haut, gewöhnlich über einem knöchernen Vorsprung. Bei dunkel pigmentierter Haut ist ein Verblassen möglicherweise nicht sichtbar, die Farbe kann sich aber von der umgebenden Haut unterscheiden. Der Bereich kann schmerzempfindlich, verhärtet, weich, wärmer oder kälter sein als das umgebende Gewebe. Diese Symptome können auf eine (Dekubitus-) Gefährdung hinweisen."/>
        <item label="Kategorie II: Teilverlust der Haut" name="2"
              tooltip="Teilzerstörung der Haut (bis zur Dermis), die als flaches, offenes Ulcus mit einem rot bis rosafarbenen Wundbett ohne Beläge in Erscheinung tritt. Kann sich auch als intakte oder offene/rupturierte, serumgefüllte Blase darstellen. Manifestiert sich als glänzendes oder trockenes, flaches Ulcus ohne nekrotisches Gewebe oder Bluterguss10. Diese Kategorie sollte nicht benutzt werden um Skin Tears (Gewebezerreißungen), Verbands- oder pflasterbedingte Hautschädigungen, feuchtigkeitsbedingte Läsionen, Mazerationen oder Abschürfungen zu beschreiben."/>
        <item label="Kategorie III: Verlust der Haut" name="3"
              tooltip="Zerstörung aller Hautschichten. Subkutanes Fett kann sichtbar sein, jedoch keine Knochen, Muskeln oder Sehnen. Es kann ein Belag vorliegen, der jedoch nicht die Tiefe der Gewebsschädigung verschleiert. Es können Tunnel oder Unterminierungen vorliegen. Die Tiefe des Dekubitus der Kategorie III variiert je nach anatomischer Lokalisation. Der Nasenrücken, das Ohr, der Hinterkopf und das Gehörknöchelchen haben kein subkutanes Gewebe, daher können Kategorie III Wunden dort auch sehr oberflächlich sein. Im Gegensatz dazu können an besonders adipösen Körperstellen extrem tiefe Kategorie III Wunden auftreten. Knochen und Sehnen sind nicht sichtbar oder tastbar."/>
        <item label="Kategorie IV: vollständiger Haut oder Gewebeverlust" name="4"
              tooltip="Totaler Gewebsverlust mit freiliegenden Knochen, Sehnen oder Muskeln. Belag und Schorf können vorliegen. Tunnel oder Unterminierungen liegen oft vor. Die Tiefe des Kategorie IV Dekubitus hängt von der anatomischen Lokalisation ab. Der Nasenrücken, das Ohr, der Hinterkopf und der Knochenvorsprung am Fußknöchel haben kein subkutanes Gewebe, daher können Wunden dort auch sehr oberflächlich sein. Kategorie IV Wunden können sich in Muskeln oder unterstützende Strukturen ausbreiten (Fascien, Sehnen oder Gelenkkapseln) und können dabei leicht Osteomyelitis oder Ostitis verursachen. Knochen und Sehnen sind sichtbar oder tastbar."/>
    </combobox>

    <combobox label="Wo ist der Dekubitus entstanden ?" name="dekubituslok"
              tooltip="Solange diese Wunde nicht abgeheilt ist, bleibt der Entstehungsort gleich."
              lockedforchanges="true" depends-on="dekubitus" visible-when-dependency-eq="true"
              default-value-when-shown="1" qdvs="Verwendung im Abschnitt &quot;Dekubitus&quot;">
        <item label="bei uns" name="1"/>
        <item label="Im Krankenhaus" name="2"/>
        <item label="zu Hause (vor dem Einzug)" name="3"/>
        <item label="woanders" name="4"/>
    </combobox>

    <combobox label="Wund-Stadium nach Daniel" name="daniel" depends-on="dekubitus"
              visible-when-dependency-neq="true" default-value-when-shown="1">
        <item label="1. Grad Hautrötung" name="1"
              tooltip="Erythem, scharf begrenzt, schmerzlos, reversibel umschriebene Hautrötung bei intakter Epidermis (Oberhaut)"/>
        <item label="2. Grad Blasenbildung" name="2"
              tooltip="Blasenbildung der Haut (Cutis), oberflächliche Ulcerationen der Epidermis (Oberhaut) bis zur Dermis (Lederhaut)"/>
        <item label="3. Grad offener Hautdefekt" name="3" tooltip="Ulcerationen bis in die Subcutis (Unterhaut)"/>
        <item label="4. Grad Muskulatur sichtbar" name="4"
              tooltip="Ulcerationen bis auf die Faszie auch Mitbefall der Muskulatur möglich"/>
        <item label="5. Grad Knochen befallen" name="5"
              tooltip="Mitbefall von Knochen, Gelenken oder Beckenorganen (Rectum, Vagina usw.)"/>
    </combobox>

    <label label="Die Schmerzintensität tragen Sie gesondert unter &quot;Schmerzeinschätzung&quot; ein." size="16"
           fontstyle="bold" layout="p"/>

    <textfield name="laenge" label="Länge (cm)" layout="br tab" length="6" hfill="false"/>
    <textfield name="breite" label="Breite (cm)" layout="tab" length="6" hfill="false"/>
    <textfield name="tiefe" label="Tiefe (cm)" layout="tab" length="6" hfill="false"/>

    <tabgroup size="16" fontstyle="bold" label="Wundfläche" name="tg1">
        <checkbox name="epi" label="Epithelisierung" layout="left"/>
        <checkbox name="ggran" label="gute Granulation" layout="left"/>
        <checkbox name="sgran" label="schlechte Granulation" layout="left"/>
        <checkbox name="fibrin" label="Fibrinbelag" layout="left"/>
        <checkbox name="fnekr" label="Feuchte Nekrose" layout="left"/>
        <checkbox name="tnekr" label="Trockene Nekrose" layout="br"/>
        <checkbox name="inseln" label="Wundheilungsinseln" layout="left"/>
        <checkbox name="hyper" label="Hypergranulation" layout="left"/>
    </tabgroup>

    <tabgroup size="16" fontstyle="bold" label="Wundumgebung" name="tg2">
        <checkbox name="rosig1" label="rosig" layout="left"/>
        <checkbox name="rot1" label="rot" layout="left"/>
        <checkbox name="blau1" label="bläulich verfärbt" layout="left"/>
        <checkbox name="marz1" label="marzeriert" layout="left"/>
        <checkbox name="verh1" label="verhärtet" layout="left"/>
        <checkbox name="oedem1" label="Ödembildung" layout="left"/>
    </tabgroup>

    <tabgroup size="16" fontstyle="bold" label="Wundrand" name="tg3">
        <checkbox name="rosig2" label="rosig" layout="left"/>
        <checkbox name="rot2" label="rot" layout="left"/>
        <checkbox name="glatt2" label="glatt" layout="left"/>
        <checkbox name="geschw2" label="geschwollen" layout="left"/>
        <checkbox name="marz2" label="marzeriert" layout="left"/>
        <checkbox name="nekr2" label="Nekrose" layout="left"/>
        <checkbox name="taschen2" label="Taschenbildung" layout="br left" default="false"/>
        <textfield name="ttasche" default="0" label="Tiefe der Tasche (cm)" length="6" hfill="false" size="16"
                   fontstyle="bold" layout="left" depends-on="taschen2" visible-when-dependency-eq="true"
                   default-value-when-shown="1"/>
    </tabgroup>

    <tabgroup size="16" fontstyle="bold" label="Wundexsudat" name="tg4">
        <checkbox name="kein3" label="kein" layout="left"/>
        <checkbox name="wenig3" label="wenig" layout="left"/>
        <checkbox name="viel3" label="viel" layout="left"/>
        <checkbox name="klar3" label="klar" layout="left"/>
        <checkbox name="blut3" label="blutig" layout="left"/>
        <checkbox name="eitr3" label="eitrig" layout="left"/>
        <checkbox name="gruen3" label="grün" layout="left"/>
        <checkbox name="braun3" label="braun" layout="left"/>
    </tabgroup>

    <tabgroup size="16" fontstyle="bold" label="Wundgeruch (vor VW)" name="tg5">
        <checkbox name="unauf4" label="unauffällig" layout="left"/>
        <checkbox name="leicht4" label="leicht" layout="left"/>
        <checkbox name="uebel4" label="übelriechend" layout="left"/>
        <checkbox name="suebel4" label="stark übelriechend" layout="left"/>
    </tabgroup>

    <tabgroup size="16" fontstyle="bold" label="Wundgeruch (während VW)" name="tg6">
        <checkbox name="unauf5" label="unauffällig" layout="left"/>
        <checkbox name="leicht5" label="leicht" layout="left"/>
        <checkbox name="uebel5" label="übelriechend" layout="left"/>
        <checkbox name="suebel5" label="stark übelriechend" layout="left"/>
    </tabgroup>

    <tabgroup size="16" fontstyle="bold" label="Wundgeruch (nach VW)" name="tg7">
        <checkbox name="unauf6" label="unauffällig" layout="left"/>
        <checkbox name="leicht6" label="leicht" layout="left"/>
        <checkbox name="uebel6" label="übelriechend" layout="left"/>
        <checkbox name="suebel6" label="stark übelriechend" layout="left"/>
    </tabgroup>';
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('wunde1c', 'Wunde Nr.01', '', '9', '117', '0', '6', @wundxml);
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('wunde2c', 'Wunde Nr.02', '', '9', '118', '0', '7', @wundxml);
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('wunde3c', 'Wunde Nr.03', '', '9', '119', '0', '8', @wundxml);
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('wunde4c', 'Wunde Nr.04', '', '9', '120', '0', '9', @wundxml);
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('wunde5c', 'Wunde Nr.05', '', '9', '121', '0', '10', @wundxml);
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('wunde6c', 'Wunde Nr.06', '', '9', '147', '0', '139', @wundxml);
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('wunde7c', 'Wunde Nr.07', '', '9', '148', '0', '140', @wundxml);
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('wunde8c', 'Wunde Nr.08', '', '9', '149', '0', '141', @wundxml);
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('wunde9c', 'Wunde Nr.09', '', '9', '150', '0', '142', @wundxml);
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('wunde10c', 'Wunde Nr.10', '', '9', '151', '0', '143', @wundxml);
--
-- Gewichts Kommentierung
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('gewdoku1', 'Kommentierung Gewicht', '', '2', '164', '0', '149',
        ' <qdvs
            tooltip="Zeile(n) 72 - 75 im DAS Dokumentationsbogen (Abschnitt &quot;Körpergewicht und Größe&quot;)"/>
            <label size="18" fontstyle="bold" parwidth="800"
                   label="Welche der aufgeführten Punkte trafen laut Pflegedokumentation für den Bewohner bzw. die Bewohnerin seit der letzten Ergebniserfassung zu?"/>
            <checkbox label="Gewichtsverlust durch medikamentöse Ausschwemmung" name="1"/>
            <checkbox label="Gewichtsverlust aufgrund ärztlich angeordneter oder ärztlich genehmigter Diät" name="2"
                      layout="br left"/>
            <checkbox label="Mindestens 10% Gewichtsverlust während eines Krankenhausaufenthalts" name="3" layout="br left"/>
            <checkbox
                    label="Aktuelles Gewicht liegt nicht vor. BW wird aufgrund einer Entscheidung des Arztes oder der Angehörigen oder eines Betreuers nicht mehr gewogen"
                    name="4" layout="br left"/>
            <checkbox
                    label="Aktuelles Gewicht liegt nicht vor. BW möchte nicht gewogen werden" name="5" layout="br left"/>
        ');
-- Auswirkungen Sturz
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('strzfolg01', 'Auswirkung Sturz', '', '3', '165', '0', '0',
        ' <qdvs tooltip="Zeile(n) 76, 77 im DAS Dokumentationsbogen (Abschnitt &quot;Sturzfolgen&quot;)"/>
          <checkbox label="erhöhter Unterstützungsbedarf bei Alltagsverrichtungen" name="erhoehter_bedarf_alltag"/>
          <checkbox label="erhöhter Unterstützungsbedarf bei der Mobilität" name="erhoehter_bedarf_mobilitaet"/>
        ');
-- Kleine Anpassung für Sturzprotokoll
UPDATE `resinfotype` t
SET t.`XML` = '  <label layout="br left hfill" size="20" fontstyle="bold" color="yellow" bgcolor="blue"
                 label="1. Grundlegende Angaben"/>
          <label fontstyle="bold" label="Datum und Uhrzeit des Sturzes"/>
          <qdvs tooltip="Zeile(n) 76, 77 im DAS Dokumentationsbogen (Abschnitt &quot;Sturzfolgen&quot;)"/>
          <textfield label="Datum" length="12" name="falldate" type="date" preset="currentdate"/>
          <textfield label="Uhrzeit" length="12" name="falltime" type="time" layout="left" preset="currenttime"/>

          <label fontstyle="bold" label="letzter Zeitpunkt, an welchem [der|die] Bewohner[in] vor dem Sturz gesehen wurde."/>
          <textfield label="Datum" length="12" name="b4falldate" type="date" preset="currentdate"/>
          <textfield label="Uhrzeit" length="12" name="b4falltime" type="time" layout="left" preset="currenttime"/>
          <textfield name="textb4fall" label="Aktivitäten des Bewohners unmittelbar vor dem Sturz" innerlayout="br"
                     hfill="false"/>

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
              <checkbox label="Fraktur" name="fracture" layout="left" qdvs="Zeile(n) 77 im DAS Dokumentationsbogen (Abschnitt &quot;Sturzfolgen&quot;)"/>
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
              <checkbox label="Arztkontakt" name="gp" qdvs="Zeile(n) 77 im DAS Dokumentationsbogen (Abschnitt &quot;Sturzfolgen&quot;)"/>
              <checkbox label="Krankenhauseinweisung" name="hospital" qdvs="Zeile(n) 77 im DAS Dokumentationsbogen (Abschnitt &quot;Sturzfolgen&quot;)"/>
          </tabgroup>
          <textfield name="othertext6" label="Anmerkungen zu den Massnahmen" hfill="false"/>

          <url label="Das neue Formular  &quot;Sturzprotokoll &quot; (Uni Bonn, siehe OPDE Quellen UNIBONN2008-01)"
               link="https://www.offene-pflege.de/de/sources-de"/>'
WHERE t.`BWINFTYP` = 'FALLPROT01';
--
-- Fixierungprotokoll
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('fixprot02', 'Fixierungsprotokoll', '', '17', '166', '3', '0',
        '     <tabgroup size="18" label="Grund der Fixierung" name="tab1">
                <checkbox name="eigen" label="Eigengefährung" />
                <checkbox name="unfall" label="Sturz-/Unfallgefahr" layout="left"/>
                <checkbox name="eigen" label="Auf eigenen Wunsch" layout="left"/>
                <checkbox name="fremd" label="Fremdgefährdung"/>
                <checkbox name="agg" label="Aggression" layout="left"/>
                <checkbox name="path" label="pathologische Unruhe" layout="left"/>
                <checkbox name="ngf" label="Nicht steh- und gehfähig"/>
            </tabgroup>
            <tabgroup size="18" label="Art der Fixierung" name="tab2">
                <checkbox name="leibgurt" label="Leibgurt" qdvs="Verwendung im Abschschnitt &quot;Anwendung von Gurten&quot;"
                          tooltip="Bitte beachten Sie, dass alle Gurtanwendungen zu erfassen sind, gleichgültig, ob eine richterliche Genehmigung oder das Einverständnis des Bewohners bzw. der Bewohnerin vorliegt. Auch Gurte, die der Bewohner bzw. die Bewohnerin theoresch selbst öffnen könnte, sind einzutragen. Auch wenn nur aufgrund der Befürchtung eines Sturzes fixiert wird, ist dies einzutragen."/>
                <checkbox name="bettgitter" label="Bettseitenteile"
                          qdvs="Verwendung im Abschschnitt &quot;Anwendung von Bettseitenteile&quot;"
                          tooltip="Außer Betracht bleiben unterbrochene Bettseitenteile, die das Verlassen des Bettes nicht behindern."
                          layout="left"/>
                <checkbox name="sitzgurt" label="Sitzgurt"
                          qdvs="Verwendung im Abschschnitt &quot;Anwendung von Gurten&quot;" layout="br tab"/>
                <checkbox name="geristuhl" label="Geriatriestuhl mit Vorsatztisch" layout="left"/>
                <checkbox name="sonst" label="Sonstiges" tooltip="Bitte in der Bemerkung beschreiben."/>
            </tabgroup>

            <optiongroup size="18" name="dauer">
                <option label="tagsüber" default="true" name="tag"/>
                <option label="nachts" name="nacht"/>
                <option label="halbe Stunde" name="05"/>
                <option label="1 Stunde" name="1"  layout="br tab"/>
                <option label="2 Stunden" name="2"/>
                <option label="sonst (bitte aufschreiben)" name="sonst"/>
            </optiongroup>

            <textfield name="dauersonst" label="Sonstige Dauer" depends-on="dauer" visible-when-dependency-eq="sonst"/>
');

--
-- Schmerzeinschätzung
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('schmerze2', 'Schmerzeinschätzung', '', '15', '137', '3', '0',
        '<label size="16" label="Allgemeine Einschätzung" color="blue"/>
          <qdvs tooltip="Zeile(n) 82 - 86 im DAS Dokumentationsbogen (Abschnitt &quot;Schmerz&quot;)"/>
          <combobox label="Schmerzintensität (Numerische Rating Skala)" name="schmerzint">
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

          <checkbox name="schmerzfrei" label="Schmerzfrei durch Medikamente" layout="br left" depends-on="schmerzint"
                    visible-when-dependency-leq="4" default-value-when-shown="false" size="14" qdvs="Zeile(n) 83 im DAS Dokumentationsbogen."/>

          <label
                  label="Akutschmerz i.d.R. weniger als 3 Monate, Chronischer Schmerz zwischen 6 Wochen und 3 Monaten, oder länger"
                  depends-on="schmerzint"
                  visible-when-dependency-neq="0" size="14" fontstyle="bold"/>

          <combobox label="Schmerztyp" name="schmerztyp" tooltip="[h1]Akuter Schmerz, „sinnvoller Schmerz“[/h1]
      [p]
      Der akute Schmerz gilt als Alarmzeichen des Körpers. Schon die alten Griechen nannten den Schmerz den „bellenden Wächter der Gesundheit“ (Hypokrates).
      Der akute Schmerz macht uns aufmerksam, dass etwas nicht stimmt und ist zeitlich begrenzt. Ist die Ursache behoben verschwindet der Schmerz meistens wieder. Wenn wir wissen warum wir Schmerzen haben (z.B. den Fuss verstaucht), können wir den Schmerz auch eher akzeptieren. Hier spielt die individuelle Wahrnehmung und das Erlernte „umgehen mit dem Schmerz“ eine wichtige Rolle.
      [/p]
      [p]
      Akuter Schmerz ist ein plötzlich auftretender und nur kurze Zeit andauern der Schmerz. Er wird als existentielle Erfahrung wahrgenommen, die eine lebenserhaltende Alarm- und Schutzfunktion einnimmt. Akuter Schmerz steht in einem offensichtlichen und direkten Zusammenhang mit einer Gewebe oder Organschädigung, also einer körperlichen Ursache. Nonverbale und verbale Signale, die wir im akuten Schmerz aussenden, verursachen unwillku?rlich Empathie und das Bedu?ürfnis fu?r Abhilfe zu sorgen. Akuter Schmerz geht mit physiologischen Begleiterscheinungen einher, wie einem Anstieg des Blutdrucks, des Pulses, Schweißausbru?chen und Anstieg der Atemfrequenz. Insbesondere diese Begleiterscheinungen, die in der akuten Versorgungssituation unmittelbar erkennbar sind, zeigt der Mensch mit ausschließlich chronischen Schmerzen nicht.
      [/p]

      [h1]Chronischer Schmerz, „sinnloser Schmerz“[/h1]
      [p]
      Der chronische Schmerz hat an sich keine Warnfunktion mehr. Seine Ursache ist nicht (mehr) ausschaltbar, er nimmt dem Menschen sinnlos die Kraft weg und zehrt allmählich seinen Lebensmut auf. Wenn die Tage zur Qual werden, erschöpft sich die Tragfähigkeit, der Leidende wünscht nur mehr ein Ende herbei, unter Umständen sogar um den Preis seines Lebens, denn es genügt nicht nur am Leben zu sein, man muss auch sein Leben haben.
      Der chronische Schmerz kann zur eigenständigen Schmerzkrankheit werden, der alle Ebenen des Menschseins beeinflusst und beeinträchtigt. Man spricht dann von „total pain“. Dieser Schmerz ist oft losgelöst von der ursprünglichen Krankheit. Gerade wenn die Ursache unbekannt ist, kann die Chronifizierung schnell eintreten.
      [/p]
      [p]
      Der Übergang zwischen akutem und chronischem Schmerz verläuft kontinuierlich. Gleichwohl werden verschiedene Zeiträume angenommen, ab wann ein Schmerz als chronischer, oder anhaltender Schmerz zu betrachten ist. Je nach Lokalisation des Schmerzes wird hierbei von mehr als 6 Wochen bis hin zu 3 Monaten ausgegangen. In erster Linie wird die Entstehung des chronischen Schmerzes durch drei grundlegende Elemente beschrieben:
      [/p]
      [ul]
      [li]Es handelt sich um einen Entstehungsprozess, der durch ein Zusammenwirken von krankheitsbedingten und psychosozialen Prozessen gekennzeichnet ist.[/li]
      [li]Chronischer Schmerz ist Schmerz, der u?ber einen Punkt, an dem die Heilung abgeschlossen sein sollte hinaus, anhält oder weiter auftritt. Chronischer Schmerz kann häufig nicht (mehr) mit einem Gewebeschaden oder einer Verletzung in Verbindung gebracht werden.[/li]
      [li]Der Chronifizierung akuter Schmerzen kann durch angemessene Therapie des akuten Schmerzes entgegengewirkt werden. Eine fru?hzeitige Linderung von akutem Schmerz kann eine Entwicklung von chronischen Schmerzen verhindern. Bestimmte operative Verfahren, z. B. Amputationen, Mastektomien oder Thorakotomien bewirken häufig chronische Schmerzen.[/li]
      [/ul]" depends-on="schmerzint" visible-when-dependency-neq="0" default-value-when-shown="0">
              <item label="akute Schmerzen" name="0"/>
              <item label="chronische Schmerzen" name="1"/>
          </combobox>

          <textfield name="schmerzort" label="Wo tritt der Schmerz auf ?" depends-on="schmerzint"
                     visible-when-dependency-neq="0"/>

          <textfield name="schmerzart" label="Beschreibung der Schmerzart"
                     tooltip="Beispiele für Schmerzarten: dumpf, pulsierend, nagelnd, schießend, brennen, steched, bohrend, ausstrahlend"
                     depends-on="schmerzint"
                     visible-when-dependency-neq="0"/>

          <textfield name="schmerzort" label="Wo tritt der Schmerz auf ?" depends-on="schmerzint"
                               visible-when-dependency-neq="0"/>

 <textfield name="lindernd" label="Lindernde Faktoren"  depends-on="schmerzint"
                   visible-when-dependency-neq="0"/>
    <textfield name="verstaerkend" label="Verstärkende Faktoren"  depends-on="schmerzint"
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
         ');
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('besd2', 'BESD', 'BEurteilung von Schmerzen bei Demenz', '15', '167', '3', '0',
        '  <label size="24" label="Einschätzung durch Pflegekraft" color="blue"/>
            <qdvs tooltip="Zeile(n) 82 - 86 im DAS Dokumentationsbogen (Abschnitt &quot;Schmerz&quot;)"/>

            <scale name="besd" label="BESD - BEurteilung von Schmerzen bei Demenz" resvaltype="14">
                <scalegroup name="besd1" label="Atmung (unabhängig von Lautäußerungen)">
                    <option label="normal" name="s0-1" score="0" layout="left" default="true"/>
                    <option label="gelegentlich angestrengt atmen" name="s1-1" score="1" layout="left"/>
                    <option label="kurze Phasen von Hyperventilation" tooltip="schnelle und tiefe Atemzu?ge" name="s1-2"
                            score="1" layout="br"/>
                    <option label="lautstark angestrengt atmen" name="s2-1" score="2" layout="left"/>
                    <option label="lange Phasen von Hyperventilation" tooltip="schnelle und tiefe Atemzu?ge" name="s2-2"
                            score="2" layout="br"/>
                    <option label="Cheyne Stoke Atmung"
                            tooltip="tiefer werdende und wieder abflachende Atemzu?ge mit Atempausen" name="s2-3" score="2"
                            layout="left"/>
                </scalegroup>
                <scalegroup name="besd2" label="Negative Lautäußerungen">
                    <option label="keine" name="s0-1" score="0" layout="left" default="true"/>
                    <option label="gelegentlich stöhnen oder ächzen" name="s1-1" score="1" layout="left"/>
                    <option label="sich leise negativ oder missbilligend äußern" name="s1-2" score="1" layout="br"/>
                    <option label="wiederholt beunruhigt rufen" name="s2-1" score="2" layout="left"/>
                    <option label="laut stöhnen oder ächzen" name="s2-2" score="2" layout="br"/>
                    <option label="weinen" name="s2-3" score="2" layout="left"/>
                </scalegroup>
                <scalegroup name="besd3" label="Gesichtsausdruck">
                    <option label="lächelnd oder nichts sagend" name="s0-1" score="0" layout="left" default="true"/>
                    <option label="trauriger Gesichtsausdruck" name="s1-1" score="1" layout="left"/>
                    <option label="ängstlicher Gesichtsausdruck" name="s1-2" score="1" layout="br"/>
                    <option label="sorgenvoller Blick" name="s1-3" score="1" layout="left"/>
                    <option label="grimassieren" name="s2-1" score="2" layout="left"/>
                </scalegroup>
                <scalegroup name="besd4" label="Körpersprache">
                    <option label="entspannt" name="s0-1" score="0" layout="left" default="true"/>
                    <option label="angespannte Körperhaltung" name="s1-1" score="1" layout="left"/>
                    <option label="nervös hin und her gehen" name="s1-2" score="1" layout="br"/>
                    <option label="nesteln" name="s1-3" score="1" layout="left"/>
                    <option label="Körpersprache starr" name="s2-1" score="2" layout="left"/>
                    <option label="geballte Fäuste" name="s2-2" score="2" layout="left"/>
                    <option label="angezogene Knie" name="s2-3" score="2" layout="br"/>
                    <option label="sich entziehen oder wegstoßen" name="s2-4" score="2" layout="left"/>
                    <option label="schlagen" name="s2-5" score="2" layout="left"/>
                </scalegroup>
                <scalegroup name="besd5" label="Trost">
                    <option label="trösten nicht notwendig" name="s0-1" score="0" layout="left" default="true"/>
                    <option label="Ablenken durch Stimme oder Berührung ist möglich" name="s1-1" score="1" layout="left"/>
                    <option label="Ablenken durch Stimme oder Berührung ist nicht möglich" name="s2-1" score="2" layout="br"/>
                </scalegroup>
                <risk from="0" to="1" label="Wahrscheinlich keine Schmerzen" color="dark_green" rating="0"/>
                <risk from="2" to="10" label="BW hat wahrscheinlich Schmerzen" color="dark_red" rating="1"/>
            </scale>

            <label size="24" label="Situationsbeurteilung" color="blue"/>

            <checkbox name="schmerzfrei" label="Schmerzfrei durch Medikamente" layout="br left" depends-on="schmerzint"
                      size="14"/>
            <label
                    label="Akutschmerz i.d.R. weniger als 3 Monate, Chronischer Schmerz zwischen 6 Wochen und 3 Monaten, oder länger"
                    size="14" fontstyle="bold"/>

            <combobox label="Schmerztyp" name="schmerztyp" tooltip="[h1]Akuter Schmerz, „sinnvoller Schmerz“[/h1]
              [p]
              Der akute Schmerz gilt als Alarmzeichen des Körpers. Schon die alten Griechen nannten den Schmerz den „bellenden Wächter der Gesundheit“ (Hypokrates).
              Der akute Schmerz macht uns aufmerksam, dass etwas nicht stimmt und ist zeitlich begrenzt. Ist die Ursache behoben verschwindet der Schmerz meistens wieder. Wenn wir wissen warum wir Schmerzen haben (z.B. den Fuss verstaucht), können wir den Schmerz auch eher akzeptieren. Hier spielt die individuelle Wahrnehmung und das Erlernte „umgehen mit dem Schmerz“ eine wichtige Rolle.
              [/p]
              [p]
              Akuter Schmerz ist ein plötzlich auftretender und nur kurze Zeit andauern der Schmerz. Er wird als existentielle Erfahrung wahrgenommen, die eine lebenserhaltende Alarm- und Schutzfunktion einnimmt. Akuter Schmerz steht in einem offensichtlichen und direkten Zusammenhang mit einer Gewebe oder Organschädigung, also einer körperlichen Ursache. Nonverbale und verbale Signale, die wir im akuten Schmerz aussenden, verursachen unwillku?rlich Empathie und das Bedu?ürfnis fu?r Abhilfe zu sorgen. Akuter Schmerz geht mit physiologischen Begleiterscheinungen einher, wie einem Anstieg des Blutdrucks, des Pulses, Schweißausbru?chen und Anstieg der Atemfrequenz. Insbesondere diese Begleiterscheinungen, die in der akuten Versorgungssituation unmittelbar erkennbar sind, zeigt der Mensch mit ausschließlich chronischen Schmerzen nicht.
              [/p]

              [h1]Chronischer Schmerz, „sinnloser Schmerz“[/h1]
              [p]
              Der chronische Schmerz hat an sich keine Warnfunktion mehr. Seine Ursache ist nicht (mehr) ausschaltbar, er nimmt dem Menschen sinnlos die Kraft weg und zehrt allmählich seinen Lebensmut auf. Wenn die Tage zur Qual werden, erschöpft sich die Tragfähigkeit, der Leidende wünscht nur mehr ein Ende herbei, unter Umständen sogar um den Preis seines Lebens, denn es genügt nicht nur am Leben zu sein, man muss auch sein Leben haben.
              Der chronische Schmerz kann zur eigenständigen Schmerzkrankheit werden, der alle Ebenen des Menschseins beeinflusst und beeinträchtigt. Man spricht dann von „total pain“. Dieser Schmerz ist oft losgelöst von der ursprünglichen Krankheit. Gerade wenn die Ursache unbekannt ist, kann die Chronifizierung schnell eintreten.
              [/p]
              [p]
              Der Übergang zwischen akutem und chronischem Schmerz verläuft kontinuierlich. Gleichwohl werden verschiedene Zeiträume angenommen, ab wann ein Schmerz als chronischer, oder anhaltender Schmerz zu betrachten ist. Je nach Lokalisation des Schmerzes wird hierbei von mehr als 6 Wochen bis hin zu 3 Monaten ausgegangen. In erster Linie wird die Entstehung des chronischen Schmerzes durch drei grundlegende Elemente beschrieben:
              [/p]
              [ul]
              [li]Es handelt sich um einen Entstehungsprozess, der durch ein Zusammenwirken von krankheitsbedingten und psychosozialen Prozessen gekennzeichnet ist.[/li]
              [li]Chronischer Schmerz ist Schmerz, der u?ber einen Punkt, an dem die Heilung abgeschlossen sein sollte hinaus, anhält oder weiter auftritt. Chronischer Schmerz kann häufig nicht (mehr) mit einem Gewebeschaden oder einer Verletzung in Verbindung gebracht werden.[/li]
              [li]Der Chronifizierung akuter Schmerzen kann durch angemessene Therapie des akuten Schmerzes entgegengewirkt werden. Eine fru?hzeitige Linderung von akutem Schmerz kann eine Entwicklung von chronischen Schmerzen verhindern. Bestimmte operative Verfahren, z. B. Amputationen, Mastektomien oder Thorakotomien bewirken häufig chronische Schmerzen.[/li]
              [/ul]" depends-on="schmerzint" visible-when-dependency-neq="0" default-value-when-shown="0">
                <item label="akute Schmerzen" name="0"/>
                <item label="chronische Schmerzen" name="1"/>
            </combobox>');
--
-- Integrationsgespräch
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('intgesp01', 'Integrationsgespräch', '', '13', '168', '0', '150',
        '
 <optiongroup size="18" name="EINZUGGESPR"
                 label="Integrationsgespräch"
                 qdvs="Zeile 93"
                 tooltip="Ist in den Wochen nach dem Einzug mit dem Bewohner bzw. der Bewohnerin und/oder einer seiner bzw. ihrer Angehörigen oder sonstigen Vertrauenspersonen ein Gespräch über sein bzw. ihr Einleben und die zukünftige Versorgung geführt worden?">
        <option label="ja" name="1" default="true"/>
        <option label="nicht möglich aufgrund fehlender Vertrauenspersonen des Bewohners bzw. der Bewohnerin"
                name="2"  layout="br left"/>
        <option label="nein, aus anderen Gründen" name="3" layout="br left"/>
    </optiongroup>

    <datefield label="Datum des Integrationsgesprächs" name="EINZUGGESPRDATUM" depends-on="EINZUGGESPR"
               visible-when-dependency-eq="1" default-value-when-shown="now" qdvs="Zeile 94"/>

    <label size="18" fontstyle="bold" label="Wer hat an dem Integrationsgespräch teilgenommen?" qdvs="Zeile 95"/>
    <checkbox name="0" label="Keine der angegebenen"/>
    <checkbox name="1" label="Bewohner/Bewohnerin" layout="left"/>
    <checkbox name="2" label="Angehörige" layout="left"/>
    <checkbox name="3" label="Betreuer/Betreuerin"/>
    <checkbox name="4" label="andere Vertrauenspersonen, die nicht in der Einrichtung beschäftigt sind" layout="left"/>

    <optiongroup size="18" name="EINZUGGESPRDOKU"
                 label="Protokoll wurde erstellt und angehangen"
                 qdvs="Zeile 96">
        <option label="nein" name="0"/>
        <option label="ja" name="1" default="true"/>
    </optiongroup>
');
--
UPDATE opde.resinfotype t SET t.XML = '
 <tx tooltip="Die jeweils aktuelle Risikoermittlung wird, sofern ein Risiko besteht, auf dem Überleitbogen auf Seite 1 Abschnitt 7 vermerkt."/>
    <scale name="braden" label="Dekubitus Risiko-Wert">
        <scalegroup name="braden1" label="Sensorisches Empfindungsvermögen"
                    tooltip="Das ist die Fähigkeit des Bewohners  auf druckbedingte Beschwerden lindernd  zu reagieren.">
            <option label="fehlt" name="fehlt"
                    tooltip="[ul][li]keine Reaktion auf schmerzhafte Reize. Mögliche Gründe: Bewusstlosigkeit, Sedierung[/li][li]Störung der Schmerzempfindung durch Lähmung, die den größten Teil des Körpers betreffen (z.B. hoher Querschnitt)[/li][/ul]"
                    score="1" layout="tab"/>
            <option label="stark eingeschränkt" name="se" tooltip="[ul]
[li]eine Reaktion erfolgt nur auf starke Schmerzreize[/li]
[li]Beschwerden können kaum geäußert werden (z.B. nur durch Stöhnen oder Unruhe)[/li]
[li]Störung der Schmerzempfindung durch Lähmung, wovon die Hälfte des Körpers betroffen ist[/li][/ul]" score="2"/>
            <option label="leicht eingeschränkt" name="le"
                    tooltip="[ul][li]Reaktion auf Ansprache oder Kommandos[/li][li]Beschwerden können aber nicht immer ausgedrückt werden (z.B. dass die Position geändert werden soll)[/li][li]Störung der Schmerzempfindung durch Lähmung, wovon eine oder zwei Extremitäten betroffen sind[/li][/ul]"
                    score="3" layout="tab"/>
            <option label="vorhanden" name="vorhanden" score="4"
                    tooltip="[ul][li]Reaktion auf Ansprache, Beschwerden können geäußert werden[/li][li]keine Störung der Schmerzempfindung[/li][/ul]"
                    default="true" layout="tab"/>
        </scalegroup>
        <scalegroup name="braden2" label="Feuchtigkeit" tooltip="Ausmaß, in dem die Haut Feuchtigkeit ausgesetzt ist.">
            <option label="ständig feucht" name="staendig"
                    tooltip="[ul][li]die Haut ist ständig feucht durch Urin, Schweiß oder Kot[/li][li]immer, wenn der Patient gedreht wird, liegt er im Nassen[/li][/ul]"
                    score="1" layout="tab"/>
            <option label="oft feucht" name="oft"
                    tooltip="[ul][li]die Haut ist oft feucht, aber nicht immer[/li][li]Bettzeug oder Wäsche muss mindestens einmal pro Schicht gewechselt werden[/li][/ul]"
                    score="2" layout="tab"/>
            <option label="manchmal feucht" name="manchmal"
                    tooltip="[ul][li]die Haut ist manchmal feucht, und etwa einmal pro Tag wird neue Wäsche benötigt[/li][/ul]"
                    score="3" layout="tab"/>
            <option label="selten feucht" name="selten"
                    tooltip="[ul][li]die Haut ist meist trocken[/li][li]neue Wäsche wird selten benötigt[/li][/ul]"
                    score="4" default="true" layout="tab"/>
        </scalegroup>
        <scalegroup name="braden3" label="Aktivitäten" tooltip="Ausmaß der physischen Aktivität.">
            <option label="bettlägerig" name="bettlaegerig" tooltip="ans Bett gebunden" score="1" layout="tab"/>
            <option label="sitzt auf" name="sitzt"
                    tooltip="[ul][li]kann mit Hilfe etwas laufen[/li][li]kann das eigene Gewicht nicht allein tragen[/li][li]braucht Hilfe, um aufzusitzen (Bett, Stuhl, Rollstuhl)[/li][/ul]"
                    score="2" layout="tab"/>
            <option label="geht wenig" name="wenig"
                    tooltip="[ul][li]geht am Tag allein, aber selten und nur kurze Distanzen[/li][li]braucht für längere Strecken Hilfe[/li][li]verbringt die meiste Zeit im Bett oder im Stuhl[/li][/ul]"
                    score="3" layout="tab"/>
            <option label="geht regelmäßig" name="regelm"
                    tooltip="[ul][li]geht regelmäßig 2- bis 3-mal pro Schicht[/li][li]bewegt sich regelmäßig[/li][/ul]"
                    score="4" default="true" layout="tab"/>
        </scalegroup>
        <scalegroup name="braden4" label="Mobilität"
                    tooltip="Fähigkeit des Bewohners, die Position alleine zu wechseln und zu halten.">
            <option label="komplett immobil" name="komplett"
                    tooltip="kann auch keinen geringfügigen Positionswechsel ohne Hilfe ausführen" score="1"
                    layout="tab"/>
            <option label="Mobilität stark eingeschränkt" name="se"
                    tooltip="[ul][li]bewegt sich manchmal geringfügig (Körper oder Extremitäten)[/li][li]kann sich aber nicht regelmäßig allein ausreichend umlagern[/li][/ul]"
                    score="2" layout="tab"/>
            <option label="Mobilität gering eingeschränkt" name="ge"
                    tooltip="macht regelmäßig kleine Positionswechsel des Körpers und der Extremitäten" score="3"
                    layout="tab"/>
            <option label="mobil" name="mobil" tooltip="kann allein seine Position umfassend verändern" score="4"
                    default="true" layout="tab"/>
        </scalegroup>
        <scalegroup name="braden5" label="Ernährung" tooltip="Ernährungsgewohnheiten">
            <option label="sehr schlechte Ernährung" name="schlecht"
                    tooltip="[ul][li] isst kleine Portionen nie auf, sondern etwa nur 2/3[/li][li]isst nur 2 oder weniger Eiweißportionen (Milchprodukte, Fisch, Fleisch)[/li][li]trinkt zu wenig[/li][li]nimmt keine Ergänzungskost zu sich[/li][li]darf oral keine Kost zu sich nehmen[/li][li]nur klare Flüssigkeiten[/li][li]erhält Infusionen länger als 5 Tage[/li][/ul]"
                    score="1" layout="tab"/>
            <option label="mäßige Ernährung" name="maessig"
                    tooltip="[ul][li]isst selten eine normale Essensportion auf, isst aber im Allgemeinen etwa die Hälfte der angebotenen Nahrung[/li][li]isst etwa 3 Eiweißportionen (Milchprodukte, Fisch, Fleisch)[/li][li]nimmt unregelmäßig Ergänzungskost zu sich[/li][li]erhält zu wenig Nährstoffe über Sondenkost oder Infusionen[/li][/ul]"
                    score="2" layout="tab"/>
            <option label="adäquate Ernährung" name="adaequat"
                    tooltip="[ul][li]isst mehr als die Hälfte der normalen Essensportionen[/li][li]nimmt 4 Eiweißportionen (Milchprodukte, Fisch, Fleisch) zu sich[/li][li]verweigert gelegentlich eine Mahlzeit, nimmt aber Ergänzungskost zu sich[/li][li]kann über Sonde oder Infusion die meisten Nährstoffe zu sich nehmen[/li][/ul]"
                    score="3" layout="tab"/>
            <option label="gute Ernährung" name="gut"
                    tooltip="[ul][li]isst immer die gebotenen Mahlzeiten auf[/li][li]nimmt 4 oder mehr Eiweißportionen (Milchprodukte, Fisch, Fleisch) zu sich[/li][li]isst auch manchmal zwischen den Mahlzeiten[/li][li]braucht keine Ergänzungskost[/li][/ul]"
                    score="4" default="true" layout="tab"/>
        </scalegroup>
        <scalegroup name="braden6" label="Reibung und Scherkräfte">
            <option label="Problem" name="problem"
                    tooltip="[ul][li]braucht viel bis massive Unterstützung bei Lagewechsel[/li][li]Anheben ist ohne Schleifen über die Laken nicht möglich[/li][li]rutscht ständig im Bett oder im (Roll-)Stuhl herunter, muss immer wieder hochgezogen werden[/li][li]hat spastische Kontrakturen[/li][li]ist sehr unruhig (scheuert auf dem Laken)[/li][/ul]"
                    score="1" layout="tab"/>
            <option label="potenzielles Problem" name="potenziell"
                    tooltip="[ul][li]bewegt sich etwas allein oder braucht wenig Hilfe[/li][li]beim Hochziehen schleift die Haut nur wenig über die Laken (kann sich etwas anheben)[/li][li]kann sich über längere Zeit in einer Lage halten (Stuhl, Rollstuhl)[/li][li]rutscht nur selten herunter[/li][/ul]"
                    score="2" layout="tab"/>
            <option label="kein Problem zurzeit" name="kein"
                    tooltip="[ul][li]bewegt sich in Bett und Stuhl allein[/li][li]hat genügend Kraft, sich anzuheben[/li][li]kann eine Position über lange Zeit halten, ohne herunterzurutschen[/li][/ul]"
                    score="3" layout="tab" default="true"/>
        </scalegroup>
        <risk from="20" to="23" label="niedrig" color="dark_green" rating="0"/>
        <risk from="16" to="19" label="mittel" color="blue" rating="1"/>
        <risk from="11" to="15" label="hoch" color="dark_orange" rating="2"/>
        <risk from="6" to="10" label="sehr hoch" color="dark_red" rating="3"/>
    </scale>

    <separator/>
    <tabgroup size="14" fontstyle="bold" label="extrinsische Faktoren" name="extrinsic">
        <checkbox name="meds" label="Medikamente" layout="left"/>
        <checkbox name="hygiene" label="Körperhygiene" layout="left"/>
        <checkbox name="moisture" label="Feuchtigkeit" layout="br"/>
        <checkbox name="friction" label="Reibung und Scherkräfte" layout="left"/>
    </tabgroup>
    <tabgroup size="14" fontstyle="bold" label="intrinsische Faktoren" name="intrinsic">
        <checkbox name="age" label="Alter" layout="left"/>
        <checkbox name="dehydration" label="Exsikose" layout="left"/>
        <checkbox name="mobility" label="Reduzierte Mobilität" layout="left"/>
        <checkbox name="weight" label="Gewicht" layout="br"/>
        <checkbox name="metabolic" label="Stoffwechselerkrankung" layout="left"/>
        <checkbox name="neuro" label="neurologische Erkrankung" layout="left"/>
        <checkbox name="sensibility" label="Sensibilitätsstörungen" layout="left"/>
        <checkbox name="malnutrition" label="Mangelernährung" layout="br"/>
        <checkbox name="incontinence" label="Inkontinenz" layout="left"/>
        <checkbox name="infection" label="Infektion" layout="left"/>
    </tabgroup>
' WHERE t.BWINFTYP='braden';

