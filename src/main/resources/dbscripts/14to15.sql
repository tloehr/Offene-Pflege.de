-- Ab Version 1.15.2.703
UPDATE `sysprops`
SET V = '15'
WHERE K = 'dbstructure';
--
-- Genesen hinzugefügt
UPDATE resinfotype t
SET t.XML = ' <resinfotype intervalmode="3">
    <combobox label="word.vaccine" name="vaccinetype">
        <item label="COVID-19" name="14"/>
        <item label="Diphterie" name="0"/>
        <item label="HPV (Humane Papillomviren)" name="1"/>
        <item label="Hepatitis B" name="2"/>
        <item label="Hib (H. influenzae Typ b)" name="3"
              tooltip="Die invasive Haemophilus-influenzae-b-Infektion ist eine der schwersten bakteriellen Infektionen in den ersten fünf Lebensjahren. Der Erreger kommt nur beim Menschen vor und findet sich vor allem auf den Schleimhäuten der oberen Atemwege."/>
        <item label="Influenza" name="4"/>
        <item label="Masern" name="5"/>
        <item label="Meningokokken C" name="6"/>
        <item label="Mumps, Röteln" name="7"/>
        <item label="Pertussis" name="8"/>
        <item label="Pneumokokken" name="9"/>
        <item label="Poliomyelitis" name="10"/>
        <item label="Rotaviren" name="11"/>
        <item label="Tetanus" name="12"/>
        <item label="Varizellen" name="13"/>
    </combobox>

    <datefield label="Datum der Impfung" name="vdate" default="now"/>

    <optiongroup name="type" label="Art der Impfung">
        <option label="Grundimmunisierung 1" name="g1"/>
        <option label="Grundimmunisierung 2" name="g2"/>
        <option label="Grundimmunisierung 3" name="g3"/>
        <option label="Grundimmunisierung 4" name="g4"/>
        <option label="Auffrischimpfung" name="a" layout="br tab"/>
        <option label="Standardimpfung" name="s" default="true"/>
        <option label="Nachholimpfung" name="n"/>
        <option label="Genesen" tooltip="natürliche Immunisierung" name="g"/>
    </optiongroup>
</resinfotype>'
WHERE t.BWINFTYP = 'vaccin1';
