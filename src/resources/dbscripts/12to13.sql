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
UPDATE `resinfotype` t SET t.`equiv` = 129 WHERE t.`BWINFTYP` = 'ARTNUTRIT';
--
-- Mobilität
UPDATE `resinfotype` SET `type` = '-1' WHERE `BWINFTYP` = 'MOBILITY';
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('bi101', 'Mobilität', 'bi1.intro', '3', '110', '0', '14',
        '
<tx tooltip="[b]Seite 1, Abschnitt 4.[/b][br/]Alles was Sie hier als Bemerkung eintragen, steht hinterher in der Bemerkungs-Zeile dieses Abschnitts im Überleitbogen.[br/][b]Lagerungsarten[/b] werden anhand der Pflegeplanungen bestimmt."/>

    <label parwidth="600px" label="bi1.intro"/>
    <separator/>
    <!--  QI26 F4.1.1 -->
    <optiongroup size="18" label="Positionswechsel im Bett" name="poswechselbett" qi="Zeile 26"
                 bi="Formulargutachten 4.1.1" tooltip="bi1.bett.erklaerung">
        <option label="selbstständig" name="selbst0" default="true" tooltip="bi1.bett.selbst0"  />
        <option label="überwiegend selbständig" name="selbst1" tooltip="bi1.bett.selbst1"/>
        <option label="überwiegend unselbständig" name="selbst2" tooltip="bi1.bett.selbst2"/>
        <option label="unselbständig" name="selbst3" tooltip="bi1.bett.selbst3"/>
    </optiongroup>

    <!--    QI27 F4.1.2 -->
    <optiongroup size="18" name="stabilersitz" label="Halten einer stabilen Sitzposition" tooltip="bi1.sitz.erklaerung"
                 qi="Zeile 27" bi="Formulargutachten 4.1.2">
        <option label="selbstständig" name="selbst0" default="true" tooltip="bi1.sitz.selbst0"/>
        <option label="überwiegend selbständig" name="selbst1" tooltip="bi1.sitz.selbst1"/>
        <option label="überwiegend unselbständig" name="selbst2" tooltip="bi1.sitz.selbst2"/>
        <option label="unselbständig" name="selbst3" tooltip="bi1.sitz.selbst3"/>
    </optiongroup>

    <!--    QI28 F4.1.3 -->
    <optiongroup size="18" name="umsetzen" label="Umsetzen" tooltip="bi1.umsetzen.erklaerung" qi="Zeile 28"
                 bi="Formulargutachten 4.1.3">
        <option label="selbstständig" name="selbst0" default="true" tooltip="bi1.umsetzen.selbst0"/>
        <option label="überwiegend selbständig" name="selbst1" tooltip="bi1.umsetzen.selbst1"/>
        <option label="überwiegend unselbständig" name="selbst2" tooltip="bi1.umsetzen.selbst2"/>
        <option label="unselbständig" name="selbst3" tooltip="bi1.umsetzen.selbst3"/>
    </optiongroup>

    <!--    QI29 F4.1.4 -->
    <optiongroup size="18" name="wohnbereich" label="Fortbewegen innerhalb des Wohnbereichs"
                 tooltip="bi1.wohnbereich.erklaerung" qi="Zeile 29" bi="Formulargutachten 4.1.4">
        <option label="selbstständig" name="selbst0" default="true" tooltip="bi1.wohnbereich.selbst0"/>
        <option label="überwiegend selbständig" name="selbst1" tooltip="bi1.wohnbereich.selbst1"/>
        <option label="überwiegend unselbständig" name="selbst2" tooltip="bi1.wohnbereich.selbst2"/>
        <option label="unselbständig" name="selbst3" tooltip="bi.selbst3"/>
    </optiongroup>

    <!--    QI30 F4.1.5 -->
    <optiongroup size="18" name="treppe" label="Treppensteigen" tooltip="bi1.treppen.erklaerung" qi="Zeile 30"
                 bi="Formulargutachten 4.1.5">
        <option label="selbstständig" name="selbst0" default="true" tooltip="bi1.treppen.selbst0"/>
        <option label="überwiegend selbständig" name="selbst1" tooltip="bi1.treppen.selbst1"/>
        <option label="überwiegend unselbständig" name="selbst2" tooltip="bi1.treppen.selbst2"/>
        <option label="unselbständig" name="selbst3" tooltip="bi1.treppen.selbst3"/>
    </optiongroup>

    <label fontstyle="bold" size="14" label="Hilfsmittel"/>
    <textfield name="hilfsmittel" innerlayout="left"/>

    <separator/>
    <label parwidth="600px" label="bi.quellen"/>
');