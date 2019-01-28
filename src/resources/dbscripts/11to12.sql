-- Ab Version 1.14.4.x
UPDATE `sysprops` SET `V` = '12' WHERE `K` = 'dbstructure';
--
-- Neue Felder bei den Infektionen
UPDATE `resinfotype` SET `type` = '157' WHERE `BWINFTYP` = 'FALLRISK1';
