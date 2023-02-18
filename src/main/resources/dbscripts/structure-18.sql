/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `acl` (
  `ACLID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `ICID` bigint(20) unsigned NOT NULL,
  `acl` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY (`ACLID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `acme` (
  `MPHID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `Firma` varchar(100) NOT NULL,
  `Strasse` varchar(100) DEFAULT NULL,
  `PLZ` char(10) DEFAULT NULL,
  `Ort` varchar(100) DEFAULT NULL,
  `Tel` varchar(100) DEFAULT NULL,
  `Fax` varchar(100) DEFAULT NULL,
  `WWW` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`MPHID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `allowance` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `resid` char(10) NOT NULL,
  `pit` datetime NOT NULL,
  `text` varchar(100) NOT NULL,
  `amount` decimal(9,2) NOT NULL,
  `uid` char(10) NOT NULL,
  `editpit` datetime DEFAULT NULL,
  `editby` char(10) DEFAULT NULL,
  `replacedby` bigint(20) unsigned DEFAULT NULL,
  `replacementfor` bigint(20) unsigned DEFAULT NULL,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `tgindex` (`resid`,`pit`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bhp` (
  `BHPID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `BHPPID` bigint(20) unsigned NOT NULL,
  `UKennung` char(10) CHARACTER SET utf8 DEFAULT NULL,
  `Soll` datetime NOT NULL,
  `Ist` datetime DEFAULT NULL,
  `SZeit` smallint(6) DEFAULT NULL,
  `IZeit` smallint(6) DEFAULT NULL,
  `Dosis` decimal(9,2) DEFAULT NULL,
  `Status` smallint(6) DEFAULT NULL,
  `Text` mediumtext,
  `MDate` datetime NOT NULL,
  `BWKennung` char(10) CHARACTER SET utf8 NOT NULL,
  `DafID` bigint(20) unsigned DEFAULT NULL,
  `VerID` bigint(20) unsigned NOT NULL,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  `nanotime` bigint(20) unsigned NOT NULL DEFAULT '0',
  `needsText` tinyint(1) NOT NULL DEFAULT '0',
  `outcome4` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`BHPID`),
  KEY `BHPPID_IDX` (`BHPPID`),
  KEY `idx1` (`Soll`),
  KEY `idx2` (`BWKennung`),
  KEY `idx3` (`DafID`),
  KEY `idx4` (`VerID`),
  KEY `idx5` (`SZeit`),
  KEY `idx6` (`outcome4`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `commontags` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `text` varchar(15) NOT NULL,
  `color` char(6) NOT NULL DEFAULT '000000',
  `type` int(11) NOT NULL DEFAULT '0',
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniquetext` (`text`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dfn` (
  `DFNID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `BWKennung` char(10) NOT NULL,
  `TermID` bigint(20) unsigned DEFAULT NULL,
  `UKennung` char(10) DEFAULT NULL,
  `MassID` bigint(20) unsigned NOT NULL,
  `Soll` datetime NOT NULL,
  `Ist` datetime DEFAULT NULL,
  `StDatum` datetime NOT NULL,
  `SZeit` smallint(6) DEFAULT NULL,
  `IZeit` smallint(6) DEFAULT NULL,
  `Status` smallint(6) DEFAULT NULL,
  `Erforderlich` tinyint(1) NOT NULL DEFAULT '0',
  `MDate` datetime NOT NULL,
  `PlanID` bigint(20) DEFAULT NULL,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`DFNID`),
  KEY `idx1` (`Soll`),
  KEY `idx2` (`BWKennung`),
  KEY `idx3` (`PlanID`),
  KEY `idx4` (`TermID`),
  KEY `idx5` (`SZeit`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dosageform` (
  `FormID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `Zubereitung` varchar(100) CHARACTER SET utf8 NOT NULL,
  `AnwText` varchar(100) CHARACTER SET utf8 NOT NULL,
  `AnwEinheit` tinyint(3) unsigned NOT NULL,
  `PackEinheit` tinyint(3) unsigned NOT NULL,
  `MassID` bigint(20) unsigned NOT NULL,
  `Stellplan` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `Status` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `Equiv` int(10) unsigned NOT NULL DEFAULT '0',
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`FormID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `floors` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `homeid` varchar(36) NOT NULL,
  `NAME` varchar(30) DEFAULT NULL,
  `LEVEL` smallint(6) DEFAULT NULL COMMENT '0 means ground floor. negative levels are below ground. positives above',
  `lift` smallint(6) DEFAULT NULL COMMENT 'number of lifts connecting to this floor',
  `version` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gp` (
  `ArztID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `Anrede` varchar(20) NOT NULL,
  `Titel` varchar(20) NOT NULL,
  `Name` varchar(100) NOT NULL,
  `Vorname` varchar(100) NOT NULL,
  `Strasse` varchar(100) NOT NULL,
  `PLZ` char(10) NOT NULL,
  `Ort` varchar(100) NOT NULL,
  `Tel` varchar(100) NOT NULL,
  `Fax` varchar(100) NOT NULL,
  `Mobil` varchar(100) DEFAULT NULL,
  `EMail` varchar(100) DEFAULT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  `neurologist` bit(1) NOT NULL DEFAULT b'0',
  `skin` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`ArztID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `handover2user` (
  `PKID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `HID` bigint(20) unsigned NOT NULL,
  `UID` char(10) NOT NULL,
  `PIT` datetime NOT NULL,
  PRIMARY KEY (`PKID`),
  KEY `idx_hid` (`HID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `handovers` (
  `HID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `PIT` datetime NOT NULL,
  `Text` mediumtext,
  `UID` char(10) NOT NULL,
  `EID` char(10) NOT NULL,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`HID`),
  KEY `idx_pit` (`PIT`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hinsurance` (
  `KassID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `Name` varchar(100) NOT NULL,
  `Strasse` varchar(100) NOT NULL,
  `PLZ` char(10) NOT NULL,
  `Ort` varchar(100) NOT NULL,
  `Tel` varchar(100) NOT NULL,
  `Fax` varchar(100) NOT NULL,
  `KNr` varchar(100) NOT NULL,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`KassID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `homes` (
  `id` varchar(36) NOT NULL,
  `Name` varchar(30) DEFAULT NULL,
  `Str` varchar(30) DEFAULT NULL,
  `ZIP` char(5) DEFAULT NULL,
  `City` varchar(30) DEFAULT NULL,
  `Tel` varchar(30) DEFAULT NULL,
  `Fax` varchar(30) DEFAULT NULL,
  `color` char(6) NOT NULL DEFAULT 'EEEEFF',
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  `active` tinyint(4) NOT NULL DEFAULT '1',
  `maxcap` int(11) DEFAULT '0' COMMENT 'Belegungskapazitaet',
  `careproviderid` int(11) DEFAULT '1' COMMENT 'Von DAS-PFLEGE eindeutig der Einrichtung zugewiesene 6 Stellige Kennung',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hospital` (
  `KHID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `Name` varchar(100) NOT NULL,
  `Strasse` varchar(100) DEFAULT NULL,
  `PLZ` char(10) DEFAULT NULL,
  `Ort` varchar(100) DEFAULT NULL,
  `Tel` varchar(100) DEFAULT NULL,
  `Fax` varchar(100) DEFAULT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`KHID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `icd` (
  `icdid` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `icd10` varchar(20) NOT NULL,
  `Text` varchar(500) NOT NULL,
  PRIMARY KEY (`icdid`),
  KEY `idx1` (`icd10`),
  KEY `idx2` (`Text`(200))
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `intervention` (
  `MassID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `Bezeichnung` varchar(500) NOT NULL,
  `MassArt` int(11) NOT NULL,
  `BWIKID` bigint(20) unsigned NOT NULL,
  `Aktiv` tinyint(1) DEFAULT '1',
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  `Flag` int(10) unsigned NOT NULL,
  PRIMARY KEY (`MassID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ischedule` (
  `TermID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `MassID` bigint(20) unsigned NOT NULL,
  `PlanID` bigint(20) unsigned NOT NULL DEFAULT '0',
  `NachtMo` tinyint(4) DEFAULT '0',
  `Morgens` tinyint(4) DEFAULT '0',
  `Mittags` tinyint(4) DEFAULT '0',
  `Nachmittags` tinyint(4) DEFAULT '0',
  `Abends` tinyint(4) DEFAULT '0',
  `NachtAb` tinyint(4) DEFAULT '0',
  `UhrzeitAnzahl` tinyint(4) DEFAULT '0',
  `Uhrzeit` time DEFAULT NULL,
  `Taeglich` tinyint(4) DEFAULT '0',
  `Woechentlich` tinyint(4) DEFAULT '0',
  `Monatlich` tinyint(4) DEFAULT '0',
  `TagNum` tinyint(4) DEFAULT '0',
  `Mon` tinyint(4) DEFAULT '0',
  `Die` tinyint(4) DEFAULT '0',
  `Mit` tinyint(4) DEFAULT '0',
  `Don` tinyint(4) DEFAULT '0',
  `Fre` tinyint(4) DEFAULT '0',
  `Sam` tinyint(4) DEFAULT '0',
  `Son` tinyint(4) DEFAULT '0',
  `Erforderlich` tinyint(1) DEFAULT '0',
  `LDatum` datetime NOT NULL,
  `Bemerkung` mediumtext,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`TermID`),
  KEY `IDX1` (`PlanID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lcustodian` (
  `BetrID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `Anrede` varchar(20) NOT NULL,
  `Name` varchar(100) NOT NULL,
  `Vorname` varchar(100) NOT NULL,
  `Strasse` varchar(100) NOT NULL,
  `PLZ` char(10) NOT NULL,
  `Ort` varchar(100) NOT NULL,
  `Tel` varchar(100) NOT NULL,
  `Privat` varchar(100) NOT NULL,
  `Fax` varchar(100) NOT NULL,
  `Mobil` varchar(100) DEFAULT NULL,
  `EMail` varchar(100) DEFAULT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`BetrID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `medinventory` (
  `VorID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `Text` varchar(500) NOT NULL,
  `BWKennung` char(10) NOT NULL,
  `UKennung` char(10) NOT NULL,
  `Von` datetime NOT NULL,
  `Bis` datetime NOT NULL,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`VorID`),
  KEY `idx1` (`BWKennung`),
  KEY `idx2` (`Bis`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `medorder` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  `resid` char(10) NOT NULL,
  `dafid` bigint(20) unsigned DEFAULT NULL,
  `arztid` bigint(20) unsigned DEFAULT NULL,
  `khid` bigint(20) unsigned DEFAULT NULL,
  `note` varchar(200) DEFAULT NULL,
  `created_on` datetime NOT NULL,
  `created_by` char(10) NOT NULL,
  `confirmed_by` char(10) DEFAULT NULL,
  `auto_created` tinyint(1) NOT NULL,
  `closed_on` datetime DEFAULT NULL,
  `closed_by` char(10) DEFAULT NULL,
  `closing_stockid` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx1` (`closed_by`),
  KEY `idx2` (`resid`),
  KEY `idx3` (`dafid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `medpackage` (
  `MPID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `DafID` bigint(20) unsigned NOT NULL,
  `PZN` char(8) NOT NULL,
  `Groesse` tinyint(3) unsigned DEFAULT NULL,
  `Inhalt` decimal(9,2) NOT NULL,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`MPID`),
  UNIQUE KEY `PZN` (`PZN`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `medproducts` (
  `medpid` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `acmeid` bigint(20) unsigned NOT NULL,
  `text` varchar(200) NOT NULL,
  `sideeffects` mediumtext,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`medpid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `medstock` (
  `BestID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `DafID` bigint(20) unsigned NOT NULL,
  `MPID` bigint(20) unsigned DEFAULT NULL,
  `VorID` bigint(20) unsigned NOT NULL,
  `UKennung` char(10) NOT NULL,
  `Ein` datetime NOT NULL,
  `Anbruch` datetime NOT NULL,
  `NextBest` bigint(20) unsigned DEFAULT NULL,
  `Aus` datetime NOT NULL,
  `Text` varchar(100) DEFAULT NULL,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  `state` smallint(6) NOT NULL DEFAULT '0',
  `UPReff` decimal(9,4) NOT NULL DEFAULT '1.0000',
  `DummyUPR` tinyint(1) NOT NULL DEFAULT '0',
  `UPR` decimal(9,4) NOT NULL DEFAULT '1.0000',
  `expire` datetime DEFAULT NULL,
  PRIMARY KEY (`BestID`),
  KEY `VORID_IDX` (`VorID`),
  KEY `idx2` (`DafID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `medstocktx` (
  `BuchID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `BestID` bigint(20) unsigned NOT NULL,
  `BHPID` bigint(20) unsigned DEFAULT NULL,
  `Menge` decimal(11,4) NOT NULL DEFAULT '0.0000',
  `weight` decimal(11,4) unsigned NOT NULL DEFAULT '0.0000',
  `Text` varchar(100) DEFAULT NULL,
  `UKennung` char(10) NOT NULL,
  `Status` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `PIT` datetime NOT NULL,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`BuchID`),
  KEY `BestID_IDX` (`BestID`),
  KEY `BHPID_IDX` (`BHPID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `member` (
  `OCMID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `UKennung` char(10) NOT NULL,
  `GKennung` char(20) NOT NULL,
  PRIMARY KEY (`OCMID`),
  UNIQUE KEY `UKennung` (`UKennung`,`GKennung`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='Diese Tabelle enthaelt die Liste aller Gruppenzugehörigkeite';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `np2tags` (
  `npid` bigint(20) unsigned NOT NULL,
  `ctagid` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`npid`,`ctagid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `npcontrol` (
  `PKonID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `PlanID` bigint(20) unsigned NOT NULL,
  `UKennung` char(10) NOT NULL,
  `Bemerkung` mediumtext,
  `Datum` datetime NOT NULL,
  `Abschluss` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`PKonID`),
  KEY `IDX1` (`PlanID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nr2user` (
  `PKID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `PBID` bigint(20) unsigned NOT NULL,
  `UID` char(10) NOT NULL,
  `PIT` datetime NOT NULL,
  PRIMARY KEY (`PKID`),
  KEY `idx_pbid` (`PBID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nreports` (
  `PBID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `PIT` datetime NOT NULL,
  `NewPIT` datetime NOT NULL,
  `Text` mediumtext,
  `NewBy` char(10) NOT NULL,
  `BWKennung` char(10) NOT NULL,
  `EditedBy` char(10) DEFAULT NULL,
  `ReplacedBy` bigint(20) unsigned DEFAULT NULL,
  `ReplacementFor` bigint(20) unsigned DEFAULT NULL,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  `DelPIT` datetime DEFAULT NULL,
  `EditedPIT` datetime DEFAULT NULL,
  `DeletedBy` char(10) DEFAULT NULL,
  PRIMARY KEY (`PBID`),
  KEY `Resident` (`BWKennung`),
  KEY `PIT` (`PIT`),
  FULLTEXT KEY `Text` (`Text`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nreports2tags` (
  `pbid` bigint(20) unsigned NOT NULL,
  `ctagid` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`pbid`,`ctagid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nursingprocess` (
  `PlanID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `BWKennung` char(10) NOT NULL,
  `Stichwort` varchar(200) NOT NULL,
  `Situation` mediumtext,
  `Ziel` mediumtext,
  `BWIKID` bigint(20) unsigned NOT NULL,
  `Von` datetime NOT NULL,
  `Bis` datetime NOT NULL,
  `AnUKennung` char(10) NOT NULL,
  `AbUKennung` char(10) DEFAULT NULL,
  `PlanKennung` bigint(20) unsigned NOT NULL,
  `NKontrolle` date NOT NULL,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`PlanID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `opgroups` (
  `GKENNUNG` char(20) NOT NULL,
  `Beschreibung` mediumtext,
  `sysflag` tinyint(1) NOT NULL DEFAULT '0',
  `Examen` tinyint(1) NOT NULL DEFAULT '0',
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`GKENNUNG`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Diese Tabelle enthaelt die Liste aller OC Benutzergruppen.';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `opusers` (
  `UKennung` varchar(10) NOT NULL DEFAULT '',
  `Vorname` varchar(100) NOT NULL DEFAULT '',
  `Nachname` varchar(100) NOT NULL DEFAULT '',
  `userstatus` tinyint(4) DEFAULT NULL,
  `MD5PW` varchar(32) NOT NULL COMMENT 'Diese Spalte ist für den FTP Server notwendig.',
  `EMail` varchar(100) DEFAULT NULL,
  `mailconfirmed` smallint(6) NOT NULL DEFAULT '0',
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  `active_since` datetime DEFAULT NULL,
  `cipherid` int(11) NOT NULL,
  PRIMARY KEY (`UKennung`),
  UNIQUE KEY `users_cipherid_uindex` (`cipherid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pcat` (
  `VKatID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `Text` varchar(100) NOT NULL,
  `Art` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`VKatID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `preport` (
  `VBID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `VorgangID` bigint(20) unsigned NOT NULL,
  `Text` mediumtext,
  `PIT` datetime NOT NULL,
  `UKennung` char(10) NOT NULL,
  `Art` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`VBID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `prescription` (
  `VerID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `BWKennung` char(10) NOT NULL,
  `AnDatum` datetime NOT NULL,
  `AbDatum` datetime NOT NULL,
  `AnKHID` bigint(20) unsigned DEFAULT NULL,
  `AbKHID` bigint(20) unsigned DEFAULT NULL,
  `AnArztID` bigint(20) unsigned DEFAULT NULL,
  `AbArztID` bigint(20) unsigned DEFAULT NULL,
  `AnUKennung` char(10) NOT NULL,
  `AbUKennung` char(10) DEFAULT NULL,
  `BisPackEnde` tinyint(1) DEFAULT '0',
  `VerKennung` bigint(20) unsigned NOT NULL,
  `Bemerkung` mediumtext,
  `MassID` bigint(20) NOT NULL DEFAULT '0',
  `DafID` bigint(20) unsigned DEFAULT NULL,
  `SitID` bigint(20) unsigned DEFAULT NULL,
  `Stellplan` tinyint(1) NOT NULL DEFAULT '0',
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`VerID`),
  KEY `idx1` (`BWKennung`),
  KEY `idx2` (`SitID`),
  KEY `idx3` (`AnDatum`),
  KEY `idx4` (`AbDatum`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `prescription2tags` (
  `prescid` bigint(20) unsigned NOT NULL,
  `ctagid` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`prescid`,`ctagid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pschedule` (
  `BHPPID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `VerID` bigint(20) unsigned DEFAULT NULL,
  `NachtMo` decimal(9,2) DEFAULT NULL,
  `Morgens` decimal(9,2) DEFAULT NULL,
  `Mittags` decimal(9,2) DEFAULT NULL,
  `Nachmittags` decimal(9,2) DEFAULT NULL,
  `Abends` decimal(9,2) DEFAULT NULL,
  `NachtAb` decimal(9,2) DEFAULT NULL,
  `UhrzeitDosis` decimal(9,2) DEFAULT NULL,
  `Uhrzeit` time DEFAULT NULL,
  `MaxAnzahl` int(11) DEFAULT NULL,
  `MaxEDosis` decimal(9,2) DEFAULT NULL,
  `Taeglich` tinyint(4) DEFAULT NULL,
  `Woechentlich` tinyint(4) DEFAULT NULL,
  `Monatlich` tinyint(4) DEFAULT NULL,
  `TagNum` tinyint(4) DEFAULT NULL,
  `Mon` tinyint(4) DEFAULT NULL,
  `Die` tinyint(4) DEFAULT NULL,
  `Mit` tinyint(4) DEFAULT NULL,
  `Don` tinyint(4) DEFAULT NULL,
  `Fre` tinyint(4) DEFAULT NULL,
  `Sam` tinyint(4) DEFAULT NULL,
  `Son` tinyint(4) DEFAULT NULL,
  `LDatum` datetime NOT NULL,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  `checkAfterHrs` decimal(9,2) DEFAULT NULL,
  PRIMARY KEY (`BHPPID`),
  KEY `new_index1` (`VerID`,`version`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qms` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `qmssid` bigint(20) unsigned DEFAULT NULL,
  `qmspid` bigint(20) unsigned DEFAULT NULL,
  `uid` char(10) DEFAULT NULL,
  `text` mediumtext,
  `target` datetime NOT NULL,
  `actual` datetime DEFAULT NULL,
  `state` smallint(6) DEFAULT NULL,
  `sequence` int(10) unsigned NOT NULL DEFAULT '0',
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qms2file` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `qmsid` bigint(20) unsigned NOT NULL,
  `fid` bigint(20) unsigned NOT NULL,
  `pit` datetime NOT NULL,
  `editor` char(10) NOT NULL,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx1` (`qmsid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qmsp2tags` (
  `qmspid` bigint(20) unsigned NOT NULL,
  `ctagid` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`qmspid`,`ctagid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qmsplan` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL,
  `description` mediumtext,
  `state` tinyint(3) unsigned DEFAULT '0',
  `uid` char(10) NOT NULL,
  `color` char(6) NOT NULL DEFAULT '000000',
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qmsplan2file` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `qmsplanid` bigint(20) unsigned NOT NULL,
  `fid` bigint(20) unsigned NOT NULL,
  `pit` datetime NOT NULL,
  `editor` char(10) NOT NULL,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx1` (`qmsplanid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qmssched` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `qmspid` bigint(20) unsigned DEFAULT NULL,
  `measure` varchar(400) NOT NULL,
  `startingon` date NOT NULL,
  `daily` tinyint(3) unsigned DEFAULT NULL,
  `weekly` tinyint(3) unsigned DEFAULT NULL,
  `monthly` tinyint(3) unsigned DEFAULT NULL,
  `yearly` tinyint(3) unsigned DEFAULT NULL,
  `monthinyear` tinyint(3) unsigned DEFAULT NULL,
  `dayinmonth` tinyint(3) unsigned DEFAULT NULL,
  `weekday` tinyint(3) unsigned DEFAULT NULL,
  `workingday` tinyint(3) unsigned DEFAULT NULL,
  `text` mediumtext NOT NULL,
  `home` varchar(15) DEFAULT 'null',
  `station` bigint(20) unsigned DEFAULT '0',
  `uid` char(10) NOT NULL,
  `state` tinyint(3) unsigned DEFAULT NULL,
  `dueDays` smallint(5) unsigned NOT NULL DEFAULT '3',
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qprocess` (
  `VorgangID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `Titel` varchar(100) NOT NULL,
  `BWKennung` char(10) DEFAULT NULL,
  `Von` datetime NOT NULL,
  `WV` datetime NOT NULL,
  `Bis` datetime NOT NULL,
  `Ersteller` char(10) NOT NULL,
  `Besitzer` char(10) NOT NULL,
  `VKatID` bigint(20) unsigned NOT NULL,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  `pdca` smallint(6) DEFAULT NULL,
  PRIMARY KEY (`VorgangID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resident` (
  `id` char(10) NOT NULL,
  `Nachname` varchar(100) NOT NULL DEFAULT '',
  `Vorname` varchar(100) NOT NULL DEFAULT '',
  `Geschlecht` tinyint(1) NOT NULL DEFAULT '0',
  `GebDatum` date NOT NULL,
  `Editor` char(10) DEFAULT NULL,
  `adminonly` tinyint(1) NOT NULL DEFAULT '0',
  `RaumID` bigint(20) unsigned DEFAULT NULL,
  `StatID` bigint(20) unsigned DEFAULT NULL,
  `BV1UKennung` char(10) DEFAULT NULL,
  `BV2UKennung` char(10) DEFAULT NULL,
  `ArztID` bigint(20) unsigned DEFAULT NULL,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  `controlling` mediumtext,
  `calcmedi` tinyint(1) DEFAULT '0',
  `idbewohner` int(11) NOT NULL DEFAULT '1' COMMENT 'QDVS: Bewohnerbezogene Nummer gema?ß Erhebungsbogen',
  `sterbephase` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resident2file` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `rid` char(10) NOT NULL,
  `fid` bigint(20) unsigned NOT NULL,
  `pit` datetime NOT NULL,
  `editor` char(10) NOT NULL,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx1` (`rid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resinfo` (
  `BWINFOID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `AnUKennung` char(10) NOT NULL,
  `AbUKennung` char(10) DEFAULT NULL,
  `BWKennung` char(10) NOT NULL,
  `BWINFTYP` char(10) NOT NULL,
  `Von` datetime NOT NULL,
  `Bis` datetime NOT NULL,
  `Bemerkung` mediumtext,
  `Properties` mediumtext,
  `HTML` mediumtext,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  `resvalueid` bigint(20) unsigned DEFAULT NULL,
  `prescriptionid` bigint(20) unsigned DEFAULT NULL,
  `connectionid` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`BWINFOID`),
  KEY `idx1` (`resvalueid`),
  KEY `idx2` (`BWKennung`),
  KEY `idx3` (`BWINFTYP`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resinfo2tags` (
  `resinfoid` bigint(20) unsigned NOT NULL,
  `ctagid` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`resinfoid`,`ctagid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resinfocategory` (
  `BWIKID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `Bezeichnung` varchar(100) DEFAULT NULL,
  `KatArt` int(11) DEFAULT '0',
  `Sortierung` int(11) DEFAULT '0',
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  `color` char(6) NOT NULL DEFAULT 'EEEEFF',
  PRIMARY KEY (`BWIKID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resinfotype` (
  `BWINFTYP` char(10) NOT NULL,
  `XML` mediumtext NOT NULL,
  `BWInfoKurz` varchar(200) NOT NULL,
  `BWInfoLang` mediumtext,
  `BWIKID` bigint(20) unsigned NOT NULL,
  `type` int(11) NOT NULL DEFAULT '0',
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  `IntervalMode` tinyint(4) DEFAULT '0',
  `equiv` int(10) unsigned NOT NULL DEFAULT '0',
  `deprecated` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`BWINFTYP`),
  KEY `idx1` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resvalue` (
  `BWID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `UKennung` varchar(10) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `BWKennung` varchar(10) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `PIT` datetime NOT NULL,
  `Wert` decimal(9,2) DEFAULT NULL,
  `Wert2` decimal(9,2) DEFAULT NULL,
  `Wert3` decimal(9,2) DEFAULT NULL,
  `Bemerkung` mediumtext CHARACTER SET utf8,
  `ReplacedBy` bigint(20) unsigned DEFAULT NULL,
  `ReplacementFor` bigint(20) unsigned DEFAULT NULL,
  `EditBy` char(10) CHARACTER SET utf8 DEFAULT NULL,
  `_cdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `_mdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  `Type` smallint(5) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`BWID`),
  KEY `idx1` (`BWKennung`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resvaluetypes` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `Text` varchar(100) NOT NULL,
  `Label1` varchar(100) DEFAULT NULL,
  `Label2` varchar(100) DEFAULT NULL,
  `Label3` varchar(100) DEFAULT NULL,
  `Unit1` varchar(100) DEFAULT NULL,
  `Unit2` varchar(100) DEFAULT NULL,
  `Unit3` varchar(100) DEFAULT NULL,
  `Default1` decimal(9,2) DEFAULT NULL,
  `Default2` decimal(9,2) DEFAULT NULL,
  `Default3` decimal(9,2) DEFAULT NULL,
  `ValType` smallint(6) NOT NULL,
  `format1` varchar(100) DEFAULT NULL,
  `format2` varchar(100) DEFAULT NULL,
  `format3` varchar(100) DEFAULT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `version` bigint(20) NOT NULL,
  `min1` decimal(9,2) DEFAULT NULL COMMENT 'minimum value for value1',
  `min2` decimal(9,2) DEFAULT NULL COMMENT 'minimum value for value2',
  `min3` decimal(9,2) DEFAULT NULL COMMENT 'minimum value for value3',
  `max1` decimal(9,2) DEFAULT NULL COMMENT 'minimum value for value1',
  `max2` decimal(9,2) DEFAULT NULL COMMENT 'minimum value for value2',
  `max3` decimal(9,2) DEFAULT NULL COMMENT 'minimum value for value3',
  PRIMARY KEY (`ID`),
  KEY `idx1` (`ValType`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rooms` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `floorid` bigint(20) unsigned NOT NULL,
  `Text` varchar(30) DEFAULT NULL,
  `Single` tinyint(1) DEFAULT NULL,
  `Bath` tinyint(1) DEFAULT NULL,
  `active` tinyint(1) unsigned NOT NULL DEFAULT '1',
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `situations` (
  `SitID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `Kategorie` tinyint(4) DEFAULT NULL,
  `UKategorie` tinyint(4) DEFAULT NULL,
  `Text` mediumtext,
  PRIMARY KEY (`SitID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `station` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `eid` varchar(36) NOT NULL,
  `Name` varchar(30) DEFAULT NULL,
  `version` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sysfiles` (
  `OCFID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `Type` smallint(6) DEFAULT NULL,
  `Filename` varchar(500) NOT NULL,
  `MD5` varchar(32) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `Beschreibung` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `Filedate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Filesize` bigint(20) NOT NULL,
  `UID` varchar(10) NOT NULL,
  `PIT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`OCFID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sysgroups2acl` (
  `ICID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `GID` char(20) NOT NULL,
  `internalClassesID` varchar(500) NOT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ICID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sysinf2file` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `BWInfoID` bigint(20) unsigned NOT NULL,
  `FID` bigint(20) unsigned NOT NULL,
  `PIT` datetime NOT NULL,
  `UKennung` char(10) NOT NULL,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `idx1` (`BWInfoID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sysinf2process` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `BWInfoID` bigint(20) unsigned NOT NULL,
  `VorgangID` bigint(20) unsigned NOT NULL,
  `VERSION` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `idx1` (`BWInfoID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `syslog` (
  `LOGID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `LoginID` bigint(20) unsigned DEFAULT NULL,
  `Host` varchar(50) DEFAULT NULL,
  `IP` varchar(50) DEFAULT NULL,
  `Hostkey` char(36) DEFAULT NULL,
  `PIT` datetime NOT NULL,
  `MESSAGE` mediumtext,
  `Loglevel` smallint(6) DEFAULT '0',
  PRIMARY KEY (`LOGID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `syslogin` (
  `LoginID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `UKennung` char(10) NOT NULL,
  `Login` datetime NOT NULL,
  `Logout` datetime NOT NULL,
  PRIMARY KEY (`LoginID`),
  KEY `idx1` (`UKennung`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sysnp2file` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `ForeignID` bigint(20) unsigned NOT NULL,
  `FID` bigint(20) unsigned NOT NULL,
  `PIT` datetime NOT NULL,
  `UKennung` char(10) NOT NULL,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `idx1` (`ForeignID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sysnp2process` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `PlanID` bigint(20) unsigned NOT NULL,
  `VorgangID` bigint(20) unsigned NOT NULL,
  `VERSION` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `idx` (`PlanID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sysnr2file` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `PBID` bigint(20) unsigned NOT NULL,
  `FID` bigint(20) unsigned NOT NULL,
  `PIT` datetime NOT NULL,
  `UKennung` char(10) NOT NULL,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `idx1` (`PBID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sysnr2process` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `PBID` bigint(20) unsigned NOT NULL,
  `VorgangID` bigint(20) unsigned NOT NULL,
  `VERSION` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `idx1` (`PBID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `syspre2file` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `VERID` bigint(20) unsigned NOT NULL,
  `FID` bigint(20) unsigned NOT NULL,
  `PIT` datetime NOT NULL,
  `UKennung` char(10) NOT NULL,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `idx1` (`VERID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `syspre2process` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `VerID` bigint(20) unsigned NOT NULL,
  `VorgangID` bigint(20) unsigned NOT NULL,
  `VERSION` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `idx1` (`VerID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sysprops` (
  `SYSPID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `K` varchar(200) NOT NULL,
  `V` varchar(200) NOT NULL,
  `UKennung` char(10) DEFAULT NULL,
  PRIMARY KEY (`SYSPID`),
  KEY `idx1` (`UKennung`),
  KEY `idx2` (`K`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sysval2file` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `ForeignID` bigint(20) unsigned NOT NULL,
  `FID` bigint(20) unsigned NOT NULL,
  `PIT` datetime NOT NULL,
  `UKennung` char(10) NOT NULL,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `idx` (`ForeignID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sysval2process` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `BWID` bigint(20) unsigned NOT NULL,
  `VorgangID` bigint(20) unsigned NOT NULL,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `idx1` (`BWID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tradeform` (
  `DafID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `Zusatz` varchar(100) DEFAULT NULL,
  `MedPID` bigint(20) unsigned NOT NULL,
  `FormID` bigint(20) unsigned NOT NULL,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  `UPR` decimal(9,4) DEFAULT NULL,
  `expDaysWhenOpen` int(10) unsigned DEFAULT NULL,
  `weightcontrol` tinyint(1) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`DafID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `uniqueid` (
  `UNIQID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `UID` bigint(20) unsigned NOT NULL DEFAULT '0',
  `PREFIX` char(20) NOT NULL DEFAULT '',
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`UNIQID`),
  UNIQUE KEY `UNIQUE` (`UID`,`PREFIX`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='Dies ist eine Hilfstabelle zur Generierung von eindeutigen S';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user2file` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `uid` char(10) NOT NULL,
  `fid` bigint(20) unsigned NOT NULL,
  `pit` datetime NOT NULL,
  `editor` char(10) NOT NULL,
  `version` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vorgangassign` (
  `VAID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `VorgangID` bigint(20) unsigned NOT NULL,
  `TableName` varchar(100) NOT NULL,
  `ForeignKey` bigint(20) unsigned NOT NULL,
  `UKennung` char(10) NOT NULL,
  PRIMARY KEY (`VAID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
