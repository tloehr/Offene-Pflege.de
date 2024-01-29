-- Ab Version 1.16.3 >b44
UPDATE `sysprops`
SET V = '20'
WHERE K = 'dbstructure';
--
alter table prescription
    add never_remind bool default false
        not null
        comment 'this prescription will remind, even when the application is due today'
        after Stellplan;
--
UPDATE resinfotype t
SET t.XML = '
 <qdvs optional="false"/>

    <optiongroup size="18" name="BEWUSSTSEINSZUSTAND"
                 label="Bewusstseinszustand"
                 tx="Diese Eintragungen werden in den Überleitbogen übFernommen. Seite 2, Abschnitt 11.">
        <option label="wach" name="1" default="true"
                tooltip="Die Person ist ansprechbar und kann an Aktivitäten teilnehmen." tx="setzt ''wach''"/>
        <option label="somnolent" name="2"
                tooltip="Die Person ist ansprechbar und gut erweckbar, wirkt jedoch müde und ist verlangsamt in seinen Handlungen." tx="setzt ''soporös''"/>
        <option label="soporös" name="3"
                tooltip="Die Person ist sehr schläfrig und kann nur durch starke äußere Reize geweckt werden (z. B. kräftiges Rütteln an der Schulter oder mehrfaches, sehr lautes Ansprechen)."
                layout="br left"  tx="setzt ''somnolent''"/>
        <option label="komatös" name="4" tooltip="Die Person kann durch äußere Reize nicht mehr geweckt werden."
                tx="setzt ''komatös''"/>
        <option label="wachkoma" name="5" tooltip="Dies trifft nur dann zu, wenn eine ärztliche Diagnose vorliegt."
                tx="setzt ''komatös''" />
    </optiongroup> '
WHERE t.BWINFTYP = 'bewusst01';

