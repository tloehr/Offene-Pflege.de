-- Ab Version 1.14.2.x
UPDATE `sysprops` SET `V` = '10' WHERE `K` = 'dbstructure';
--
-- Chiffre Werte für Mitarbeiter setzen einmalig.
-- nicht bei uns, da hab ich das schon mal gemacht.
-- https://github.com/tloehr/Offene-Pflege.de/issues/84
SET @r := 9999;
ALTER TABLE opusers ADD cipherid INT NOT NULL;
UPDATE  opusers
SET     cipherid = (@r := @r + FLOOR( 1 + RAND( ) *1000 ))
ORDER BY RAND();
CREATE UNIQUE INDEX users_cipherid_uindex ON opusers (cipherid);