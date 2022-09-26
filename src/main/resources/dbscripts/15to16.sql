-- Ab Version 1.15.3
UPDATE `sysprops`
SET V = '16'
WHERE K = 'dbstructure';
--
-- Bestell Tabelle für Medikamente
--
drop table if exists medorder;
create table medorder
(
    id              bigint unsigned auto_increment primary key,
    version         bigint unsigned default 0 not null,
    resid           CHAR(10)                  not null,
    dafid           bigint unsigned,
    arztid          bigint unsigned,
    khid            bigint unsigned,
    note            varchar(200),
    created_on      datetime                  not null,
    created_by      char(10)                  not null,
    auto_created    boolean                   not null,
    closed_on       datetime,
    closed_by       char(10),
    closing_stockid bigint unsigned,
    KEY `idx1` (`closed_by`),
    KEY `idx2` (`resid`),
    KEY `idx3` (`dafid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;