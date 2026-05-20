-- Ab Version 1.16.6
UPDATE `sysprops`
SET V = '23'
WHERE K = 'dbstructure';
#
alter table `nreports`
    change PBID id bigint unsigned auto_increment;
