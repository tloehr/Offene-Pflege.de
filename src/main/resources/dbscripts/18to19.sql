-- Ab Version 1.16.3 >b44
UPDATE `sysprops`
SET V = '19'
WHERE K = 'dbstructure';
--
alter table `opusers`
    change MD5PW hashed_pw varchar(256) not null;
