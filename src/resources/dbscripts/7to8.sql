-- Ab Version 1.13.1.x
-- Library Updates
-- Downloads/jgoodies-binding-2_14_0-20150402.zip
-- Downloads/jgoodies-looks-2_8_0-20150402.zip
-- Downloads/jgoodies-common-1_9_0-20150402.zip
-- Downloads/jgoodies-forms-1_10_0-20150402.zip
-- Downloads/jgoodies-validation-2_6_0-20150402.zip
-- beanutuls 1.9.2
-- commons logging
-- quartz 2.1.7
UPDATE `sysprops` SET `V` = '8' WHERE `K` = 'dbstructure'; 
--
UPDATE `resinfotype` SET `type` = '-1' WHERE `BWINFTYP` = 'INKOAID';
--
ALTER TABLE `resinfo` ADD COLUMN `prescriptionid` BIGINT(20) UNSIGNED NULL AFTER `resvalueid`; 
--
INSERT INTO `resinfotype` (`BWINFTYP`, `XML`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`) VALUES ('INKOAID2', '<checkbox name="needshelp" label="Bewohner[in] benötigt Hilfe beim Wechsel der Inko-Materialien" tx="Seite 1, Abschnitt 5"/>
<separator/>
<checkbox name="windel" label="Windelhosen" tx="Seite 1, Abschnitt 5"/>
<textfield name="windel.size" label="Größe" layout="tab" length="3" hfill="false"/>
<textfield name="hwf" label="Anzahl Windel, früh" layout="tab"  innerlayout="left" length="3" hfill="false"/>
<textfield name="hws" label="Anzahl Windel, spät" layout="tab"  innerlayout="left" length="3" hfill="false"/>
<textfield name="hwn" label="Anzahl Windel, nacht" layout="tab"  innerlayout="left" length="3" hfill="false"/>
<checkbox name="vorlagen1" label="Vorlagen, Typ 1" tx="Seite 1, Abschnitt 5"/>
<textfield name="vorlagen1.size" label="Größe" layout="tab"  innerlayout="left" length="3" hfill="false"/>
<textfield name="v1f" label="Anzahl, früh" layout="tab"  innerlayout="left" length="3" hfill="false"/>
<textfield name="v1s" label="Anzahl, spät" layout="tab"  innerlayout="left" length="3" hfill="false"/>
<textfield name="v1n" label="Anzahl, nacht" layout="tab"  innerlayout="left" length="3" hfill="false"/>
<checkbox name="vorlagen2" label="Vorlagen, Typ 2" tx="Seite 1, Abschnitt 5"/>
<textfield name="vorlagen2.size" label="Größe" layout="tab"  innerlayout="left" length="3" hfill="false"/>
<textfield name="v2f" label="Anzahl, früh" layout="tab" innerlayout="left"  length="3" hfill="false"/>
<textfield name="v2s" label="Anzahl, spät" layout="tab"  innerlayout="left" length="3" hfill="false"/>
<textfield name="v2n" label="Anzahl, nacht" layout="tab"  innerlayout="left" length="3" hfill="false"/>
<checkbox name="vorlagen3" label="Vorlagen, Typ 3" tx="Seite 1, Abschnitt 5"/>
<textfield name="vorlagen3.size" label="Größe" layout="tab"  innerlayout="left" length="3" hfill="false"/>
<textfield name="v3f" label="Anzahl, früh" layout="tab"  innerlayout="left" length="3" hfill="false"/>
<textfield name="v3s" label="Anzahl, spät" layout="tab"  innerlayout="left" length="3" hfill="false"/>
<textfield name="v3n" label="Anzahl, nacht" layout="tab"  innerlayout="left" length="3" hfill="false"/>
<checkbox name="dbinden" label="Damenbinden" tx="Seite 1, Abschnitt 5"/>
<textfield name="dbinden.size" label="Größe" layout="tab"  innerlayout="left" length="3" hfill="false"/>
<textfield name="dbf" label="Damenbinden, früh" layout="tab"  innerlayout="left" length="3" hfill="false"/>
<textfield name="dbs" label="Damenbinden, spät" layout="tab"  innerlayout="left" length="3" hfill="false"/>
<textfield name="dbn" label="Damenbinden, nacht" layout="tab" innerlayout="left"  length="3" hfill="false"/>
<checkbox name="krunterlagen" label="Krankenunterlagen" tx="Seite 1, Abschnitt 5"/>
<textfield name="krunterlagen.size" label="Größe" layout="tab"  innerlayout="left" length="3" hfill="false"/>
<textfield name="kuf" label="Krankenunterlagen, früh" layout="tab"  innerlayout="left" length="3" hfill="false"/>
<textfield name="kus" label="Krankenunterlagen, spät" layout="tab"   innerlayout="left" length="3" hfill="false"/>
<textfield name="kun" label="Krankenunterlagen, nacht" layout="tab"  innerlayout="left" length="3" hfill="false"/>
<separator/>
<checkbox name="commode.aid" label="Toilettenstuhl" tx="Seite 1, Abschnitt 4 und 5"/>
<checkbox name="urinal.aid" label="Urinflasche"  layout="left" tx="Seite 1, Abschnitt 5"/>
<checkbox name="bedpan.aid" label="Steckbecken" layout="left" tx="Seite 1, Abschnitt 5"/>
<checkbox name="seat.aid" label="Toilettensitzerhöhung" layout="left"/>
<separator/>
<checkbox name="sup.aid" label="Suprapubischer Katheter" layout="br" tx="Seite 1, Abschnitt 5"/>
<checkbox name="trans.aid" label="Transurethraler Blasenkatheter" layout="left" tx="Seite 1, Abschnitt 5"/>
<textfield label="CH" name="tubesize" length="5" hfill="false" innerlayout="left" layout="left" tx="Seite 1, Abschnitt 5"/>
<gpselect name="prescribed.by" label="misc.msg.prescribed.by"/>
<textfield label="med.indication" name="med.indication" hfill="false" layout="br"/>
<label label="update.every.six.months" color="blue"/>', 'Inkontinenz-Hilfsmittel und Katheter', '', '2', '112', '0', '16');
--
INSERT INTO `resinfotype` (`BWINFTYP`, `XML`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`) VALUES ('ANTIBIO1', '
<optiongroup name="application" label="Anwendung" tooltip="wird verwendet zur Prävalenzmessung im Rahmen des MRE-Siegels">
<option label="lokal" name="local" />
<option label="systemisch" name="systemic" default="true"/>
</optiongroup>

<optiongroup name="treatment" label="Art der Behandlung">
<option label="prophylaktisch" name="prophylactic" />
<option label="therapeutisch" name="therapeutic" default="true"/>
</optiongroup>

<tabgroup label="Antibiotikagabe wegen Infektion" name="reason">
<checkbox name="inf.urethra" label="Harnwege" layout="br"/>
<checkbox name="inf.skin.wound" label="Haut- oder Wunden"/>
<checkbox name="inf.respiratoric" label="Atemwege" />
<checkbox name="inf.digestive" label="Magen-Darmtrakt" layout="br"/>
<checkbox name="inf.eyes" label="Augen"/>
<checkbox name="inf.ear.nose.mouth" label="Ohren/Nase/Mund"/>
<checkbox name="inf.systemic" label="systemische Infektion" layout="br"/>
<checkbox name="inf.fever" label="unerklärbares Fieber"/>
</tabgroup>
<textfield name="inf.other" label="andere Gründe" length="10" optional="true" hfill="false"/>

<combobox label="Wo wurde die Therapie begonnen" name="therapy.start">
<item label="Einrichtung" name="here"/>
<item label="Krankenhaus" name="hospital"/>
<item label="anderer Ort" name="other"/>
</combobox>

<combobox label="Wer hat das Mittel verschrieben" name="prescription.by">
<item label="Hausarzt" name="gp"/>
<item label="Facharzt" name="specialist"/>
<item label="Notarzt" name="emergency"/>
</combobox>

<label fontstyle="bold" label="Bitte angeben, falls vorhanden"/>
<checkbox name="diag.urinetest" label="Urintest vor Therapie durchgeführt" layout="br"/>
<checkbox name="diag.microbiology" label="Mikrobiologische Diagnostik vor der Therapie"/>
<textfield name="diag.result" label="isolierter Erreger" length="30" optional="true" hfill="false"/>
<textfield name="diag.resistent" label="Antibiotikaresistenz" length="30" optional="true" hfill="false"/>

<url label="Nach: ''Bewohner B - Bewohner Fragebogen'' (mre-netz regio rhein-ahr, siehe OPDE Quellen EXNER01)" link="https://www.offene-pflege.de/de/sources-de"/>', 'Angaben zur Antiobitikum Verordnung', '', '15', '143', '3');
--
INSERT INTO `commontags` (`text`,`type`) VALUES ('antibiotikum','14');
--
INSERT INTO `resinfotype` (`BWINFTYP`, `XML`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`) VALUES ('VACCIN1', '<combobox label="word.vaccine" name="vaccinetype" >
<item label="Diphterie" name="0"/>
<item label="HPV (Humane Papillomviren)" name="1"/>
<item label="Hepatitis B" name="2"/>
<item label="Hib (H. influenzae Typ b)" name="3" tooltip="Die invasive Haemophilus-influenzae-b-Infektion ist eine der schwersten bakteriellen Infektionen in den ersten fünf Lebensjahren. Der Erreger kommt nur beim Menschen vor und findet sich vor allem auf den Schleimhäuten der oberen Atemwege."/>
<item label="Influenza" name="4"/>
<item label="Masern" name="5"/>
<item label="Meningokokken C" name="6"/>
<item label="Mumps, Röteln" name="7"/>
<item label="Pertussis" name="8"/>
<item label="Pneumokokken" name="9"/>
<item label="Poliomyelitis" name="10"/>
<item label="Rotaviren" name="11"/>
<item label="Tetanus" name="12"/>
<item label="Varizellen" name="13"/>
</combobox>

<optiongroup name="type" label="Art der Impfung">
<option label="Grundimmunisierung 1" name="g1" default="true"/>
<option label="Grundimmunisierung 2" name="g2"/>
<option label="Grundimmunisierung 3" name="g3"/>
<option label="Grundimmunisierung 4" name="g4"/>
<option label="Auffrischimpfung" name="a" layout="br tab"/>
<option label="Standardimpfung" name="s"/>
<option label="Nachholimpfung" name="n"/>
</optiongroup>', 'Impfung', '', '15', '144', '0');
--
INSERT INTO `resinfotype` (`BWINFTYP`, `XML`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`) VALUES ('SURGERY1', '<textfield name="surgerytext" label="Art der OP" hfill="true"/>
<gpselect name="gp1" label="Ambulant durchgeführt"/>
<hospitalselect name="hospital1" label="im KH durchgeführt"/>', 'Chirurgischer Eingriff', 'Diese Angaben werden für die MRE Prävalenzmessungen verwendet', '15', '145', '0');
--
INSERT INTO `resinfotype` (`BWINFTYP`, `XML`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`) VALUES ('VCATH1', '
<checkbox name="vessel.catheter" label="Gefäßkatheter" layout="br" enables="bs1" default="true"/>
<label label="Lage des Katheters" size="16" fontstyle="bold"/>
<bodyscheme name="bs1"/>
', 'Gefäßkatheter', '', '15', '146', '0');
--
INSERT INTO `resinfotype` (`BWINFTYP`, `XML`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`) VALUES ('ROOM1', '
<roomselect name="room" label="Zimmer"/>
', 'BewohnerInnen-Zimmer', '', '13', '12', '0');
--
ALTER TABLE `homes` ADD COLUMN `color` CHAR(6) DEFAULT 'EEEEFF' NOT NULL AFTER `Fax`; 
ALTER TABLE `rooms` CHANGE `StatID` `floorid` BIGINT(20) UNSIGNED NOT NULL, ADD COLUMN `active` TINYINT(1) UNSIGNED DEFAULT 1 NOT NULL AFTER `bath`, DROP COLUMN `Level`,  ADD COLUMN `version` BIGINT(20) UNSIGNED DEFAULT 0 NOT NULL AFTER `active`;
UPDATE rooms SET active = 1;
--
DROP TABLE IF EXISTS `floors`;
--
CREATE TABLE `floors`
(
	`floorid`	 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	`homeid`  VARCHAR(15) NOT NULL,
	`NAME`  VARCHAR(30),
	`LEVEL` SMALLINT COMMENT '0 means ground floor. negative levels are below ground. positives above', 
	`lift` SMALLINT COMMENT 'number of lifts connecting to this floor',
	`version` BIGINT(20) UNSIGNED NOT NULL,
	PRIMARY KEY (floorid)
)
ENGINE = INNODB;
--
ALTER TABLE `commontags`  ADD COLUMN `version` BIGINT(20) UNSIGNED DEFAULT 0 NOT NULL AFTER `type`;
--
INSERT INTO `floors` (`homeid`, `name`, `level`, `lift`, `version`) VALUES ('home1', 'Erdgeschoss', '0', '1', '1'); 
INSERT INTO `rooms` (`floorid`, `Text`, `Single`, `Bath`, `active`, `version`) VALUES('1','1','1','1','1','0');
--
INSERT INTO `sysprops` (`K`, `V`, `UKennung`) VALUES('bhp_max_minutes_to_withdraw','30',NULL);
INSERT INTO `sysprops` (`K`, `V`, `UKennung`) VALUES('cash_pagebreak_after_element_no','30',NULL);
INSERT INTO `sysprops` (`K`, `V`, `UKennung`) VALUES('dfn_max_minutes_to_withdraw','30',NULL);
INSERT INTO `sysprops` (`K`, `V`, `UKennung`) VALUES('EARLY_BGITEM','D0E6FF',NULL);
INSERT INTO `sysprops` (`K`, `V`, `UKennung`) VALUES('EARLY_BGSHIFT','62A9FF',NULL);
INSERT INTO `sysprops` (`K`, `V`, `UKennung`) VALUES('EARLY_FGITEM','62A9FF',NULL);
INSERT INTO `sysprops` (`K`, `V`, `UKennung`) VALUES('EARLY_FGSHIFT','ECF4FF',NULL);
INSERT INTO `sysprops` (`K`, `V`, `UKennung`) VALUES('LATE_BGITEM','DBEADC',NULL);
INSERT INTO `sysprops` (`K`, `V`, `UKennung`) VALUES('LATE_BGSHIFT','59955C',NULL);
INSERT INTO `sysprops` (`K`, `V`, `UKennung`) VALUES('LATE_FGITEM','59955C',NULL);
INSERT INTO `sysprops` (`K`, `V`, `UKennung`) VALUES('LATE_FGSHIFT','F3F8F4',NULL);
INSERT INTO `sysprops` (`K`, `V`, `UKennung`) VALUES('ONDEMAND_BGITEM','EEEECE',NULL);
INSERT INTO `sysprops` (`K`, `V`, `UKennung`) VALUES('ONDEMAND_BGSHIFT','D1D17A',NULL);
INSERT INTO `sysprops` (`K`, `V`, `UKennung`) VALUES('ONDEMAND_FGITEM','D1D17A',NULL);
INSERT INTO `sysprops` (`K`, `V`, `UKennung`) VALUES('ONDEMAND_FGSHIFT','F5F5E2',NULL);
INSERT INTO `sysprops` (`K`, `V`, `UKennung`) VALUES('OUTCOME_BGSHIFT','404040',NULL);
INSERT INTO `sysprops` (`K`, `V`, `UKennung`) VALUES('OUTCOME_FGSHIFT','c0c0c0',NULL);
INSERT INTO `sysprops` (`K`, `V`, `UKennung`) VALUES('VERY_EARLY_BGITEM','FFC8E3',NULL);
INSERT INTO `sysprops` (`K`, `V`, `UKennung`) VALUES('VERY_EARLY_BGSHIFT','FF62B0',NULL);
INSERT INTO `sysprops` (`K`, `V`, `UKennung`) VALUES('VERY_EARLY_FGITEM','FF62B0',NULL);
INSERT INTO `sysprops` (`K`, `V`, `UKennung`) VALUES('VERY_EARLY_FGSHIFT','FFECF5',NULL);
INSERT INTO `sysprops` (`K`, `V`, `UKennung`) VALUES('VERY_LATE_BGITEM','FFA8FF',NULL);
INSERT INTO `sysprops` (`K`, `V`, `UKennung`) VALUES('VERY_LATE_BGSHIFT','990099',NULL);
INSERT INTO `sysprops` (`K`, `V`, `UKennung`) VALUES('VERY_LATE_FGITEM','990099',NULL);
INSERT INTO `sysprops` (`K`, `V`, `UKennung`) VALUES('VERY_LATE_FGSHIFT','FFE3FF',NULL);