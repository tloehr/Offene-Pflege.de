-- Ab Version 1.15.3
UPDATE `sysprops`
SET V = '16'
WHERE K = 'dbstructure';
--
-- Bestell Tabelle für Medikamente
--
drop table if exists medorders;
create table medorders
(
    id        bigint unsigned auto_increment primary key,
    version   bigint unsigned default 0 not null,
    opened_on datetime                  not null,
    opened_by char(10)                  not null,
    closed_on datetime,
    closed_by char(10)
);
drop table if exists medorder;
create table medorder
(
    id        bigint unsigned auto_increment primary key,
    version   bigint unsigned default 0 not null,
    mosid     bigint unsigned           not null,
    resid     CHAR(10)                  not null,
    dafid     bigint unsigned           not null,
    arztid    bigint unsigned,
    khid      bigint unsigned,
    opened_on datetime                  not null,
    opened_by char(10)                  not null,
    closed_on datetime,
    closed_by char(10)
);