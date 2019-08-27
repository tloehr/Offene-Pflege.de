-- Ab Version 1.14.4.x
UPDATE `sysprops`
SET `V` = '13'
WHERE `K` = 'dbstructure';
--
-- Beatmung
UPDATE `resinfotype`
SET `type` = '-1'
WHERE `BWINFTYP` = 'respirat1';
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('respirat2', 'Atmung/Beatmung', '', '15', '133', '0', '22',
        '
 <tx tooltip="Diese Eintragungen werden in den Überleitbogen übernommen. Seite 2, Abschnitt 12."/>
    <label size="16" fontstyle="bold" label="Atmung"/>
    <checkbox label="unauffällig" name="normal"/>
    <checkbox label="kardialer Stau" name="cardcongest" layout="left"/>
    <checkbox label="Schmerzen" name="pain" layout="left"/>
    <checkbox label="Husten" name="cough" layout="left"/>
    <checkbox label="Verschleimung" name="mucous" layout="br"/>
    <checkbox label="Auswurf" name="sputum" layout="left"/>
    <checkbox label="Rauchen" name="smoking" layout="left"/>
    <checkbox label="Asthma" name="asthma" layout="left"/>
    <textfield name="other" label="Sonstiges" length="20"/>

    <label size="16" fontstyle="bold" label="Besonderheiten"/>
    <checkbox label="Tracheostoma" name="stoma"/>
    <checkbox label="Silberkanüle" name="silver" layout="left"/>
    <checkbox label="Silikonkanüle" name="silicon" layout="left"/>
    <checkbox label="Absaugen" name="aspirate" layout="left"/>

    <textfield name="tubetype" label="Kanülenart" length="10"/>
    <textfield name="tubesize" label="Kanülengröße" length="10" layout="left"/>

    <imagelabel image="/artwork/48x48/evaluation-score.png"
                tooltip="QDVS-Indikator &quot;Beatmung&quot;"/>
    <!-- Maßstäbe und Grundsätze für die Qualität, die Q-Sicherung und Q-Darstellung sowie für die Entwicklung eines einrichtungsinternen Q-Managements nach §113 SGB XI in der vollstationären Pflege.
         Anlage3 - Seite 10ff - Zeile 23-->
    <label size="16" fontstyle="bold" label="Beatmung"/>
    <optiongroup name="beatmung">
        <option label="ja, invasive Beatmung" name="1"/>
        <option label="ja, nicht invasiv" name="2"/>
        <option label="nein" default="true" name="3"/>
    </optiongroup>
');
--
-- Bewusstseinszustand
UPDATE `resinfotype`
SET `type` = '-1'
WHERE `BWINFTYP` = 'concious';
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('bewusst01', 'Bewusstseinszustand', '', '5', '131', '0', '21',
        '
 <tx tooltip="Diese Eintragungen werden in den Überleitbogen übernommen. Seite 2, Abschnitt 11."/>
    <imagelabel image="/artwork/48x48/evaluation-score.png"
                tooltip="QDVS-Indikator &quot;Bewusstseinszustand&quot;"/>
    <!-- Maßstäbe und Grundsätze für die Qualität, die Q-Sicherung und Q-Darstellung sowie für die Entwicklung eines einrichtungsinternen Q-Managements nach §113 SGB XI in der vollstationären Pflege.
         Anlage3 - Seite 10ff - Zeile 24-->
    <checkbox label="wach/ansprechbar" name="awake" tooltip="Der Bewohner bzw. die Bewohnerin ist ansprechbar und kann an Aktivitäten teilnehmen."/>
    <checkbox label="schläfrig" name="sleepy"
              tooltip="Der Bewohner bzw. die Bewohnerin ist ansprechbar und gut erweckbar, wirkt jedoch müde und ist verlangsamt in seinen Handlungen."/>
    <checkbox label="soporös" name="sopor"
              tooltip="Als Sopor (lat. tiefer Schlaf) bezeichnet man eine Form der quantitativen Bewusstseinsstörung, bei der der Patient sich in einem schlafähnlichen Zustand befindet, in dem nur durch starke Stimuli (z. B. Schmerzreize) Reaktionen (z. B. Abwehrbewegungen) ausgelöst werden können. Ein volles Erwecken des Patienten ist dabei meist nicht mehr möglich. Die Reflexe sind erhalten, der Muskeltonus ist herabgesetzt."/>
    <checkbox label="somnolent" name="somnolent"
              tooltip="Der Bewohner bzw. die Bewohnerin ist sehr schläfrig und kann nur durch starke äußere Reize geweckt werden (z. B. kräftiges Rütteln an der Schulter oder mehrfaches, sehr lautes Ansprechen)."/>
    <checkbox label="komatös" name="coma"
              tooltip="Der Bewohner bzw. die Bewohnerin kann durch äußere Reize nicht mehr geweckt werden."/>
    <checkbox label="wachkoma" name="vegetative"
                  tooltip="Dies trifft nur dann zu, wenn eine ärztliche Diagnose vorliegt."/>
    <label label="Erläuterungstexte übernommen aus &quot;Maßstäbe und Grundsätze für die Qualität, die Q-Sicherung"/>
    <label label="Q-Darstellung sowie für die Entwicklung eines einrichtungsinternen Q-Managements nach §113 SGB XI"/>
    <label label="in der vollstationären Pflege. Anlage3&quot;"/>
');
--
-- Neue Kategorie für die BI Instrumente
UPDATE `resinfocategory` t SET t.`Bezeichnung` = 'BI Begutachtungsinstrumente', t.`Sortierung` = 101 WHERE t.`BWIKID` = 20
--
UPDATE `resinfotype` t SET t.`equiv` = 129 WHERE t.`BWINFTYP` = 'ARTNUTRIT';
