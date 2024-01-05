-- Ab Version 1.16.3 >b44
UPDATE `sysprops`
SET V = '20'
WHERE K = 'dbstructure';
--
alter table prescription
    add never_remind bool default false
        not null
        comment 'this object will never turn up as a reminder after the user''s login'
        after Stellplan;

