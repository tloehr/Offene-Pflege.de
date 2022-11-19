-- Ab Version 1.15.3
UPDATE `sysprops`
SET V = '17'
WHERE K = 'dbstructure';
--
-- Bestell Tabelle für Medikamente
--
alter table medorder add confirmed_by char(10) null after created_by;
-- defaults to monday
INSERT INTO sysprops (K, V, UKennung) VALUES ('calc.medi.start.order.week', '1', null);