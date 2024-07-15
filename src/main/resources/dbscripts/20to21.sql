-- Ab Version 1.16.3
UPDATE `sysprops`
SET V = '21'
WHERE K = 'dbstructure';
--
UPDATE `resinfotype`
SET `deprecated` = '1'
WHERE `BWINFTYP` = 'FALLRISK1';
--
-- `BWINFTYP`, `XML`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `version`, `IntervalMode`, `equiv`
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES('FALLRISK2','Sturzrisiko (lang)','Vertiefte Einschätzung, falls das Sturz Screening Auffälligkeiten ergibt.','3','157','0','35', '
<resinfotype>
    <optiongroup name="sturzrisiko" size="20" fontstyle="bold" label="Wie hoch wird das Sturzrisiko eingeschätzt ?">
        <option label="stark" name="ja"/>
        <option label="mittel" name="mittel"/>
        <option label="leicht" name="leicht"/>
        <option label="kein" name="nein" default="true"/>
    </optiongroup>
    <imagelabel image="/artwork/32x32/hillslope.png"
                text="Dieses Symbol erscheint bei einem Risiko *stark* oder *mittel*"/>
    <separator/>
    <tabgroup size="20" fontstyle="bold" label="Personenbezogene Sturzrisikofaktoren" name="tb001">
        <tabgroup size="14" fontstyle="bold" label="Sturz- und Frakturvorgeschichte" name="tb01">
            <checkbox name="past" label="Stürze in den letzten 12 Monaten" layout="br"/>
            <textfield name="fall1" label="Wie oft" length="25" hfill="false" innerlayout="tab"/>
            <checkbox name="injuries" label="Verletzungen ?" layout="br"/>
            <checkbox name="frac" label="Frakturen in den letzten 12 Monaten" layout="left"/>
        </tabgroup>
        <tabgroup size="14" fontstyle="bold" label="Sturzangst"
                  tooltip="Bei Hinweisen auf Angst zu stürzen (Screeningergebnis): Ausmaß, Situationen und Ursachen der Sturzangst ermitteln"
                  name="tb02">
            <checkbox name="fear" label="Hat der BW Angst zu stürzen ?" layout="br"/>
        </tabgroup>
        <tabgroup size="14" fontstyle="bold" label="Mobilitätsbeeinträchtigung"
                  tooltip="Bei Hinweisen auf Mobilitätseinschränkungen (Screeningergebnis): Einschätzung von Schweregrad, Art, Ursachen und Konsequenzen der Beeinträchtigungen,"
                  name="tb03">
            <checkbox name="insecure" label="BW fühlt sich unsicher beim Gehen oder Stehen" layout="left"/>
            <checkbox name="immobile" label="Mobilitätsbeeinträchtigung"
                      tooltip="Kraft, Ausdauer, Beweglichkeit, Balance" layout="left"/>
            <checkbox name="balance" label="Probleme mit der Körperbalance"/>
            <checkbox name="gang" label="Gangveränderungen / eingeschränkte Bewegungsfreiheit" layout="left"/>
            <checkbox name="aid" label="BW verwendet Mobilitätshilfen" layout="left"/>
        </tabgroup>
        <tabgroup size="14" fontstyle="bold" label="kognitive Beeinträchtigungen" name="tb04"
                  tooltip="Prüfen der Orientierung zu Zeit, Ort, Person und Situation">
            <checkbox name="demenz" label="Demenz"/>
            <checkbox name="depression" label="Depression" layout="left"/>
            <checkbox name="delir" label="Delir" layout="left"/>
        </tabgroup>
        <tabgroup size="14" fontstyle="bold" label="Probleme mit der Urinausscheidung"
                  tooltip="Bei Harninkontinenz: Einschätzung von Beeinträchtigungen der Harnkontinenz sowie möglicher Auswirkungen auf das Sturzrisiko "
                  name="tb05">
            <checkbox name="nykturie" label="Dranginkontinenz, Nykturie"/>
            <checkbox name="toilette" label="Probleme bei den Toilettengängen" layout="left"/>
        </tabgroup>
        <tabgroup size="14" fontstyle="bold" label="Schmerzen" name="tb06">
            <checkbox name="pain" label="Schmerzen bei der Bewegung" layout="br"/>
        </tabgroup>
        <tabgroup size="14" fontstyle="bold" label="Ernährung"
                  tooltip="Risiko einer Mangelernährung, kalziumarme Diät, extrem hoher/niedriger BMI" name="tb07">
            <checkbox name="malnutrition" label="Mangelernährung" layout="br"/>
            <checkbox name="last3month" label="Unbeabsichtigter Gewichtsverlust in den letzten 3 Monaten"
                      layout="left"/>
        </tabgroup>
        <tabgroup size="14" fontstyle="bold" label="Sehstörungen" name="tab08">
            <checkbox name="kontrast" label="gestörte Kontrastwahrnehmung"/>
            <checkbox name="schaerfe" label="gestörte Sehschärfe" layout="left"/>
            <checkbox name="fov" label="Gesichtsfeldeinschränkung" layout="left"/>
            <checkbox name="badglasses" label="Ungeeignete Brille" layout="left"/>
            <checkbox name="newglasses" label="neue oder angepasste Brille"
                      tooltip="Neue, angepasste Brillen mit der passenden Stärke können das Sturzrisiko zu Beginn erhöhen (!). Das liegt daran, dass die betroffene Person sich erst an die neue Brille gewöhnen muss. In dieser Zeit steigt das Sturzrisiko."
                      layout="br"/>
        </tabgroup>
        <tabgroup size="14" fontstyle="bold" label="Schwindel" name="tab09">
            <checkbox name="diabetes" label="Diabetes" layout="br"/>
            <checkbox name="blutdruckabfall" label="Blutdruckabfall beim Aufstehen" layout="left"/>
            <checkbox name="herzrhythmusstörungen" label="Herzrhythmusstörungen" layout="left"/>
            <checkbox name="tia" label="TIA (Transitorisch ischämische Attacke)" layout="left"/>
            <checkbox name="epilepsie" label="Epilepsie" layout="br"/>
            <checkbox name="ms" label="Multiple Sklerose" layout="left"/>
            <checkbox name="parkinson" label="Parkinson" layout="left"/>
            <checkbox name="apoplexie" label="Apoplexie" layout="left"/>
            <checkbox name="polyneuropathie" label="Polyneuropathie" layout="left"/>
            <checkbox name="krebserkrankungen" label="Krebserkrankungen" layout="br"/>
            <checkbox name="osteoathritis" label="Osteoathritis" layout="left"/>
            <checkbox name="frailty" label="Gebrechlichkeit" layout="left"/>
            <checkbox name="az" fontstyle="italic"
                      label="andere chronische Erkrankungen / schlechter Allgemeinzustand / Multimorbidität"
                      layout="left"/>

        </tabgroup>
    </tabgroup>
    <separator/>
    <tabgroup size="20" fontstyle="bold" label="Medikationsbezogene Risikofaktoren" name="tb002">
        <tabgroup size="14" fontstyle="bold" label="Psychotrope Medikamente" name="tab10">
            <checkbox name="psychopharmaka" label="Psychopharmaka"/>
            <checkbox name="sedativa" label="Sedativa / Hypnotika" layout="left"/>
        </tabgroup>
        <tabgroup size="14" fontstyle="bold" label="Androgenrezeptor-Inhibitoren" name="tab11">
            <checkbox name="androgen" label="Androgenrezeptor-Inhibitoren " tooltip="Bei Prostatakrebs"
                      layout="br"/>
        </tabgroup>
        <tabgroup size="14" fontstyle="bold" label="Polypharmazie" name="tab12">
            <checkbox name="antiarrhythmika" label="Antiarrhythmika" tooltip="z.B. Metoprolol, Bisoprolol und Nebivolol"
                      layout="br"/>
            <checkbox name="az"
                      label="andere chronische Erkrankungen / schlechter Allgemeinzustand / Multimorbidität"
                      layout="br"/>
        </tabgroup>
    </tabgroup>
    <separator/>
    <tabgroup size="20" fontstyle="bold" label="Umweltbezogene Risikofaktoren" name="tb003">
        <checkbox name="fem" label="Freiheitsentziehende Maßnahmen"/>
        <checkbox name="hilfsm" label="Unangemessener Umgang mit Hilfsmitteln" layout="left"/>
        <checkbox name="kleidung" label="Unangemessene Kleidung und Schuhe" layout="left"/>
    </tabgroup>
</resinfotype>


');
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES('fallscrn1','Sturzrisiko (kurz)','Schnellscreening zur Bewertung, ob eine vertiefte Einschätzung nötig ist.','3','0','0','0','

<resinfotype>
    <label size="20" fontstyle="bold" name="hinweis"
           label="Bei zwei oder mehr Auffälligkeiten, können Sie hier abbrechen."
           color="blue"/>
    <label size="20" fontstyle="bold" name="hinweis"
           label="Füllen Sie dann stattdessen die ausführliche Risikoeinschätzung aus."
           color="blue"/>
    <separator/>
    <tabgroup size="20" fontstyle="bold" label="Sturz- und Frakturvorgeschichte" name="tb01">
        <checkbox name="past" label="Stürze in den letzten 12 Monaten" layout="br"/>
        <textfield name="fall1" label="Wie oft" length="25" hfill="false" innerlayout="tab"/>
        <checkbox name="injuries" label="Verletzungen ?" layout="br"/>
        <checkbox name="frac" label="Frakturen in den letzten 12 Monaten" layout="left"/>
    </tabgroup>
    <tabgroup size="20" fontstyle="bold" label="Sturzangst" name="tb02">
        <checkbox name="fear" label="Hat der BW Angst zu stürzen ?" layout="br"/>
    </tabgroup>
    <tabgroup size="20" fontstyle="bold" label="Mobilitätsbeeinträchtigung" name="tb03">
        <checkbox name="insecure" label="BW fühlt sich unsicher beim Gehen oder Stehen" layout="left"/>
        <checkbox name="immobile" label="Mobilitätsbeeinträchtigung"
                  tooltip="Kraft, Ausdauer, Beweglichkeit, Balance" layout="left"/>
        <checkbox name="balance" label="Probleme mit der Körperbalance"/>
        <checkbox name="gang" label="Gangveränderungen / eingeschränkte Bewegungsfreiheit" layout="left"/>
        <checkbox name="aid" label="BW verwendet Mobilitätshilfen" layout="left"/>
    </tabgroup>
    <tabgroup size="20" fontstyle="bold" label="kognitive Beeinträchtigungen" name="tb04"
              tooltip="Prüfen der Orientierung zu Zeit, Ort, Person und Situation">
        <checkbox name="demenz" label="Demenz"/>
        <checkbox name="depression" label="Depression" layout="left"/>
        <checkbox name="delir" label="Delir" layout="left"/>
    </tabgroup>
</resinfotype>

');
