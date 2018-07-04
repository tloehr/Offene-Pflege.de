UPDATE `opde`.`sysprops`
SET `V` = '11'
WHERE `K` = 'dbstructure';
--
-- Wenn eine Einrichtung stillgelegt wird.
ALTER TABLE `opde`.`homes`
  ADD active TINYINT DEFAULT 1 NOT NULL;
--
-- Neue Felder bei den Infektionen
UPDATE `opde`.`resinfotype`
SET `type` = '-1'
WHERE `BWINFTYP` = 'INFECT1';
INSERT INTO `opde`.`resinfotype` (`BWINFTYP`, `XML`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`)
VALUES ('INFECT2', '<imagelabel image="/artwork/48x48/biohazard.png"/>
<tx tooltip="Diese Eintragungen werden in den Überleitbogen übernommen. Seite 2, Abschnitt 10.[br/]Ausserdem führt eine [b]multiresistente Infektion[/b] dazu, dass die Anlage ''MRE'' erstellt und beigefügt wird."/>
<checkbox label="MRSA" tooltip="Methicillin-resistenter Staphylococcus aureus" name="mrsa"/>
<checkbox label="VRE" tooltip="Vancomycin-resistente Enterokokken" name="vre" layout="left"/>
<checkbox label="ESBL" tooltip="Extended Spectrum Beta-Lactamasen" name="esbl" layout="left"/>
<checkbox label="3-MRGN" tooltip="Multiresistente, gramnegative Bakterien (resistent gegen 3 Antibiotikaklassen)" name="3mrgn" layout="left"/>
<checkbox label="4-MRGN" tooltip="Multiresistente, gramnegative Bakterien (resistent gegen 4 Antibiotikaklassen)" name="4mrgn" layout="left"/>
<textfield name="other" label="Andere Infektion" length="40" hfill="false" innerlayout="tab"/>
<checkbox label="multi-resistent" tooltip="Diese Markierung ist nur nötig, wenn es sich nicht um MRSA, VRE oder ESBL handelt." name="mre" layout="left"/>
<optiongroup name="lab" label="Untersuchung">
<option label="gesichert" name="confirmed"/>
<option label="nicht gesichert (Befund steht noch aus)" name="waiting"/>
<option label="nicht untersucht" name="notchecked" default="true"/>
</optiongroup>

<tabgroup size="16" label="Lokalisation" name="localize">
<checkbox label="Nase" name="nose"/>
<checkbox label="Rachen" name="pharynx" layout="left"/>
<checkbox label="Urin"  name="urine"  layout="left"/>
<checkbox label="Respirationstrakt" name="respiration"  layout="left"/>
</tabgroup>
<textfield name="woundtext" label="Wunde" length="30" hfill="false" layout="br" innerlayout="tab"/>
<textfield name="otherplace" label="Sonstiges" layout="br" length="30" hfill="false" innerlayout="tab"/>
<textfield name="date" label="Datum Erstbefund" layout="br" type="date" optional="true" length="20" hfill="false" innerlayout="tab"/>

<bodyscheme name="bs1"/>

<label size="16" fontstyle="bold" label="Sanierung (bei Besiedlung)" name="l1"/>
<textfield name="cleaningfrom" label="von" length="10" type="date" optional="true" hfill="false"/>
<textfield name="cleaningto" label="bis" length="10" hfill="false" type="date" optional="true" layout="left"  innerlayout="left"/>
<textfield name="cleaningwith" label="mit" length="30" hfill="false" layout="br"/>

<label size="16" fontstyle="bold" label="Therapie (bei Infektion)" name="l2"/>
<textfield name="therapylocal" label="lokal" length="30" hfill="false" innerlayout="tab"/>
<textfield name="therapysystem" label="systemisch" length="30" hfill="false" layout="br" innerlayout="tab"/>
<textfield name="therapyfrom" label="Beginn" length="10" hfill="false" type="date" optional="true"  layout="br" innerlayout="tab"/>
<textfield name="therapyto" label="bis (vorraussichtlich)" length="10" type="date" optional="true"  hfill="false" layout="left" innerlayout="tab"/>

<label size="16" fontstyle="bold" label="Verlauf" name="l3"/>
<textfield name="med1" label="Wirkstoff" length="30" hfill="false" innerlayout="tab"/>
<textfield name="dose1" label="Dosierung" length="30" hfill="false" innerlayout="tab"/>
<textfield name="from1" label="Von" length="10" hfill="false" type="date" optional="true" innerlayout="tab"/>
<textfield name="to1" label="Bis" length="10" type="date" optional="true" layout="left" innerlayout="left"/>

<textfield name="med2" label="Wirkstoff" length="30" hfill="false" layout="p" innerlayout="tab"/>
<textfield name="dose2" label="Dosierung" length="30" hfill="false" innerlayout="tab"/>
<textfield name="from2" label="Von" length="10" hfill="false" type="date" optional="true" innerlayout="tab"/>
<textfield name="to2" label="Bis" length="10" type="date" optional="true" layout="left" innerlayout="left" />

<textfield name="med3" label="Wirkstoff" length="30" hfill="false" layout="p" innerlayout="tab"/>
<textfield name="dose3" label="Dosierung" length="30" hfill="false" innerlayout="tab"/>
<textfield name="from3" label="Von" length="10" hfill="false" type="date" optional="true" innerlayout="tab"/>
<textfield name="to3" label="Bis" length="10" type="date" optional="true" layout="left" innerlayout="left" />

<textfield name="med4" label="Wirkstoff" length="30" hfill="false" layout="p" innerlayout="tab"/>
<textfield name="dose4" label="Dosierung" length="30" hfill="false" innerlayout="tab"/>
<textfield name="from4" label="Von" length="10" hfill="false" type="date" optional="true" innerlayout="tab"/>
<textfield name="to4" label="Bis" length="10" type="date" optional="true" layout="left" innerlayout="left"/>',
        'Ansteckende Infektionen', NULL, '15', '99', '0', '12');
--
-- Alte "INFECT1" anpassen
SET @now = now();
INSERT INTO `opde`.`resinfo` (AnUKennung, AbUKennung, BWKennung, BWINFTYP, Von, Bis, Bemerkung, Properties, HTML)
  SELECT
    AnUKennung,
    AbUKennung,
    BWKennung,
    "INFECT2",
    @now,
    '9999-12-31 23:59:59',
    CONCAT(Bemerkung, "\n","Automatisch erstellt während des Software-Updates auf 1.14.3. Original BWINFOID: ",BWINFOID),
    Properties,
    HTML
  FROM resinfo
  WHERE BWINFTYP = "INFECT1" AND Bis = "9999-12-31 23:59:59";
UPDATE `opde`.`resinfo`
SET AbUKennung = AnUKennung, Bis = DATE_ADD(@now, INTERVAL -1 SECOND)
WHERE BWINFTYP = "INFECT1" AND Bis = "9999-12-31 23:59:59";
