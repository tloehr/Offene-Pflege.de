UPDATE `sysprops` SET `V` = '3' WHERE `K` = 'dbstructure'; 
ALTER TABLE qprocess ADD COLUMN `pdca` SMALLINT NULL AFTER `version`;