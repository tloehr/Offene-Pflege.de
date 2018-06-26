UPDATE `sysprops` SET `V` = '11' WHERE `K` = 'dbstructure';
--
-- Wenn eine Einrichtung stillgelegt wird.
ALTER TABLE `homes` ADD active TINYINT DEFAULT 1 NOT NULL;
