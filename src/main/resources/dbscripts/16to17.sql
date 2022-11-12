-- Ab Version 1.15.3
UPDATE `sysprops`
SET V = '17'
WHERE K = 'dbstructure';
--
-- Bestell Tabelle für Medikamente
--
alter table medorder add confirmed_by char(10) null after created_by;