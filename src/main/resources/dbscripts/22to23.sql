-- Ab Version 1.16.7 b61
UPDATE `sysprops`
SET V = '23'
WHERE K = 'dbstructure';
--
alter table `nreports`
    change PBID id bigint unsigned auto_increment;
alter table `acl`
    change ACLID id bigint unsigned auto_increment,
    add version bigint(20) not null;
alter table `acme`
    change MPHID id bigint unsigned auto_increment,
    add version bigint(20) not null;
alter table `bhp`
    change BHPID id bigint unsigned auto_increment;
alter table gp
    change ArztID id bigint unsigned auto_increment,
    add medorder_period tinyint default 0
        not null comment 'the number of days for med order range. 0 means restore user default.';
--
UPDATE opde.resinfotype t
SET t.deprecated = 1
WHERE t.BWINFTYP = 'PERS';
UPDATE opde.resinfotype t
SET t.deprecated = 1
WHERE t.BWINFTYP = 'aussch01';
--
INSERT INTO opde.resinfotype (BWINFTYP, XML, BWInfoKurz, BWInfoLang, BWIKID, type, version, IntervalMode, equiv,
                              deprecated)
VALUES ('PERS2', '<textfield label="Geburtsname" name="gebname" innerlayout="tab"/>
<textfield label="Geburtsort" name="gebort" innerlayout="tab"/>
<textfield label="Konfession" name="konfession" innerlayout="tab" tx="\'Religion\' auf Seite 1, oben"/>
<textfield label="Bestattung" name="bestattung" innerlayout="tab"/>
<checkbox name="seelsorge" label="Seelsorge erwünscht"/>
<textfield label="Staatsang." name="staat" innerlayout="tab"/>
<optiongroup name="stand" label="Familienstand">
<option label="ledig" name="ledig" default="true"/>
<option label="verheiratet" name="verheiratet" />
<option label="getrennt lebend" name="getrennt"/>
<option label="geschieden" name="geschieden"/>
<option label="verwitwet" name="verwitwet"/>
</optiongroup>
<checkbox name="single" label="alleinstehend"  tx="\'alleinstehend\' auf Seite 1, Abschnitt 1"/>',
        'Persönliches', '', 13, 104, 26, 0, 24, 0);
INSERT INTO opde.resinfotype (BWINFTYP, XML, BWInfoKurz, BWInfoLang, BWIKID, type, version, IntervalMode, equiv,
                              deprecated)
VALUES ('aussch02', '
 <qdvs optional="false"/>
    <tx tooltip="Sobald das Inkontinenzprofil nicht mehr auf &quot;Kontinenz&quot; steht, wird im Überleitbogen die Markierung für &quot;Harninkontinenz&quot; bzw. &quot;Stuhlinkontinenz&quot; gesetzt."/>

    <optiongroup size="18" name="SVTOILETTE"
                 label="Benutzen einer Toilette oder eines Toilettenstuhls" tooltip="bi4.toilette.erklaerung"
                 tx="Seite 1, Abschnitt 3">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.toilette.selbst0"/>
        <option label="überwiegend selbständig" name="2" tooltip="bi4.toilette.selbst2"/>
        <option label="überwiegend unselbständig" name="4" tooltip="bi4.toilette.selbst4" layout="br left"/>
        <option label="unselbständig" name="6" tooltip="bi4.toilette.selbst6"/>
    </optiongroup>
    <separator/>

    <optiongroup size="18" name="SVSTUHLKONTINENZ" label="Darmkontrolle, Stuhlkontinenz"
                 tooltip="bi4.stuhlangabe.erklaerung">

        <option label="ständig kontinent" name="0" default="true" tooltip="bi4.stuhlangabe.stufe0" layout="br left"/>
        <option label="überwiegend kontinent" name="1" tooltip="bi4.stuhlangabe.stufe1"/>
        <option label="überwiegend IN_kontinent" name="2" tooltip="bi4.stuhlangabe.stufe2" layout="br left"/>
        <option label="komplett IN_kontinent" name="3" tooltip="bi4.stuhlangabe.stufe3"/>
        <option label="Person hat ein Colo- oder Ileostoma" name="4" tooltip="bi4.stuhlangabe.stufe4"/>
    </optiongroup>

    <optiongroup size="18" name="SVSTUHLKONTINENZBEW"
                 label="Bewältigen der Folgen einer Stuhlinkontinenz und Umgang mit Stoma"
                 tooltip="bi4.stuhlbewaeltigung.erklaerung"
                 tx="Seite 1, Abschnitt 3">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.stuhlbewaeltigung.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.stuhlbewaeltigung.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.stuhlbewaeltigung.selbst2" layout="br left"/>
        <option label="unselbständig" name="3" tooltip="bi4.stuhlbewaeltigung.selbst3"/>
    </optiongroup>

    <tabgroup label="Sonstige Angaben" name="sonsta">
        <checkbox name="diarrhoe" label="Neigt zu Durchfällen" tx="Seite 1, Abschnitt 5" layout="br left"/>
        <checkbox name="obstipation" label="Neigt zu Verstopfung" tx="Seite 1, Abschnitt 5" layout="left"/>
        <checkbox name="digital" label="Digitales Ausräumen"
                  tooltip="Das digitale Ausräumen beschreibt eine Maßnahme zur manuellen Entfernung von hartem Stuhl aus dem Enddarm.[br/]Diese Behandlung wird vorallem bei Koprostase, Stuhlimpaktion oder einer Darmlähmung durchgeführt."
                  tx="Seite 1, Abschnitt 5" layout="left"/>
        <checkbox name="ap.aid" label="Anus Praeter" tx="Seite 1, Abschnitt 5"/>
    </tabgroup>

    <optiongroup size="18" name="SVHARNKONTINENZ" label="Blasenkontrolle/Harnkontinenz"
                 tooltip="bi4.harnangabe.erklaerung">
        <option label="ständig kontinent" name="0" default="true" tooltip="bi4.harnangabe.stufe0" layout="br left"/>
        <option label="überwiegend kontinent" name="1" tooltip="bi4.harnangabe.stufe1"/>
        <option label="überwiegend IN_kontinent" name="2" tooltip="bi4.harnangabe.stufe2" layout="br left"/>
        <option label="komplett IN_kontinent" name="3" tooltip="bi4.harnangabe.stufe3"/>
        <option label="Person hat einen Dauerkatheter oder ein Urostoma" name="4" tooltip="bi4.harnangabe.stufe4"/>
    </optiongroup>

    <optiongroup size="18" name="sinkoprofil" label="Stuhl-Inkontinenzprofil"
                 tooltip="exp.kontinenzprof.erklaerung">
        <option label="Kontinenz" name="kontinenz" default="true" tooltip="exp.harn.kontinenz"/>
        <option label="Unabhängig erreichte Kontinenz" name="uek" tooltip="exp.harn.uek"/>
        <option label="Abhängig erreichte Kontinenz" name="aek" tooltip="exp.harn.aek"/>
        <option label="Unabhängig kompensierte Inkontinenz" name="uki" tooltip="exp.harn.uki" layout="br left"/>
        <option label="Abhängig kompensierte Inkontinenz" name="aki" tooltip="exp.harn.aki"/>
        <option label="Nicht kompensierte Inkontinenz" name="nki" tooltip="exp.harn.nki" layout="br left"/>
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
                 label="Bewältigen der Folgen einer Harninkontinenz und Umgang mit Dauerkatheter und Urostoma"
                 tooltip="bi4.harnbewaeltigung.erklaerung" tx="Seite 1, Abschnitt 3">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.harnbewaeltigung.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.harnbewaeltigung.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.harnbewaeltigung.selbst2" layout="br left"/>
        <option label="unselbständig" name="3" tooltip="bi4.harnbewaeltigung.selbst3"/>
    </optiongroup>

    <label size="18" label="Ursachenanalyse für eine Harninkontinenz (falls vorhanden)"/>
    <tabgroup size="12" label="Kognitive Einschränkung" name="kogn1">
        <checkbox name="kogn1" label="Abnahme geistiger Leistungsfähigkeit"/>
        <checkbox name="kogn2" label="BewohnerIn bemerkt den Harndrang nicht" layout="left"/>
        <checkbox name="kogn3" label="BewohnerIn findet die Toilette nicht" layout="br"/>
        <checkbox name="kogn4" label="BewohnerIn hat Angst oder schämt sich bzgl. der Inkontinenz" layout="left"/>
    </tabgroup>
    <tabgroup size="12" label="Körperliche Einschränkung" name="krp1">
        <checkbox name="koerp1" label="BW schafft es nicht alleine auf die Toilette"/>
        <checkbox name="koerp2" label="BW ist gangunsicher" layout="left"/>
        <checkbox name="koerp3" label="hat Schwierigkeiten das Gleichgewicht zu halten" layout="left"/>
        <checkbox name="koerp4" label="Muskelkraft fehlt" layout="br"/>
        <checkbox name="koerp5" label="Flexibilität des Körpers fehlt" layout="left"/>
        <checkbox name="koerp6" label="Fingerfertigkeit fehlt"
                  tooltip="z.B. fehlt dem BW die Fähigkeit die Hose oder den Rock aufzuknöpfen." layout="left"/>
        <checkbox name="koerp7" label="BW sieht schlecht" layout="left"/>
        <checkbox name="koerp8" label="BW ist immobil" layout="br"/>
    </tabgroup>
    <tabgroup size="12" label="Erkrankungen" name="erkr1a">
        <checkbox name="erk1" label="Schlaganfall"/>
        <checkbox name="erk2" label="MS" layout="left"/>
        <checkbox name="erk3" label="Parkinson" layout="left"/>
        <checkbox name="erk4" label="Demenz" layout="left"/>
        <checkbox name="erk5" label="Diabetes Mellitus" layout="left"/>
        <checkbox name="erk6" label="Herzinsuffizienz" layout="left"/>
    </tabgroup>

    <tabgroup size="12" label="Medikamente"
              tooltip="Erhält der BW Medikamente, welche die Harn-Inkontinenz fördern ?" name="medik1">

        <checkbox name="med1" label="Diuretika"/>
        <checkbox name="med2" label="Anticholinergika" layout="left"/>
        <checkbox name="med3" label="Antihistaminika" layout="left"/>
        <checkbox name="med4" label="Antidepressiva" layout="left"/>
        <checkbox name="med5" label="Neuroleptika" layout="left"/>
        <checkbox name="med6" label="Kalziumantagonisten" layout="br"/>
        <checkbox name="med7" label="Opiate" layout="left"/>
    </tabgroup>
    <tabgroup size="12" label="Besonderheiten" name="besnd1">
        <checkbox name="bes1" label="Harnwegsinfekt" tooltip="Bitte Ausschluss mittels Urinanalyse wenn nötig"/>
        <checkbox name="bes2" label="Obstipation (bei Frauen)" layout="left"/>
        <checkbox name="bes3" label="Beckbodenschwäche (bei Frauen) z.B. bei Adipositas" layout="left"/>
        <checkbox name="bes4" label="Östrogenmangel (bei Frauen)" layout="br"/>
        <checkbox name="bes5" label="Veränderungen der Prostata (bei Männern)" layout="left"/>
    </tabgroup>
',
        'Ausscheidungen', '', 2, 159, 0, 0, 137, 0);
-- UPDATE opde.gp t SET t.medorder_period = 28 WHERE t.id = 67;
-- UPDATE opde.gp t SET t.medorder_period = 14 WHERE t.id = 68;
