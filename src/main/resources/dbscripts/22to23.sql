-- Ab Version 1.16.5
UPDATE `sysprops`
SET V = '23'
WHERE K = 'dbstructure';
-- Um unliebsame Formen loszuwerden (Stomaplatte)
alter table `dosageform`
    modify Stellplan tinyint(3) default 0 not null;
