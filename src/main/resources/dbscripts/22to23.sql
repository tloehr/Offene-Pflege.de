-- Ab Version 1.16.6
UPDATE `sysprops`
SET V = '23'
WHERE K = 'dbstructure';
#
alter table `nreports`
    change PBID id bigint unsigned auto_increment;
alter table `acl`
    change ACLID id bigint unsigned auto_increment,
    add version bigint(20) not null;
alter table `acme`
    change MPHID id bigint unsigned auto_increment,
    add version bigint(20) not null;
alter table `bhp`
    change BHPID id bigint unsigned auto_increment;
#alter table `dfn`
#    change DFNID id bigint unsigned auto_increment;
