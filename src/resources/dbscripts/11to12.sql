-- Ab Version 1.14.4.x
UPDATE `sysprops` SET `V` = '12' WHERE `K` = 'dbstructure';
--
-- Neue Felder bei den Infektionen
UPDATE `resinfotype` SET `type` = '157' WHERE `BWINFTYP` = 'FALLRISK1';
UPDATE `resinfotype` SET `XML` = '<optiongroup name="sturzrisiko" label="Wie hoch wird das Sturzrisiko eingeschätzt ?">
    	<option label="stark" name="ja"/>
    	<option label="mittel" name="mittel"/>
    	<option label="leicht" name="leicht"/>
    	<option label="kein" name="nein" default="true"/>
    </optiongroup>
    <imagelabel image="/artwork/32x32/hillslope.png" text="Dieses Symbol erscheint bei einem Risiko *stark* oder *mittel*"/>

    <label name="hinweis" label="Nicht vergessen: schriftliche Auswertung in das Bemerkungsfeld schreiben" color="red"/>

    <label size="12" fontstyle="bold" label="Sturzanamnese"/>
    <checkbox name="past" label="fallrisk.past"/>
    <checkbox name="fear" label="fallrisk.fear" layout="left"/>

    <label size="12" fontstyle="bold" label="Funktionseinbußen und Funktionsbeeinträchtigungen"/>
    <checkbox name="balance" label="Probleme mit der Körperbalance"/>
    <checkbox name="gang" label="Gangveränderungen / eingeschränkte Bewegungsfreiheit" layout="left"/>

    <label size="12" fontstyle="bold" label="Sehstörungen"/>
    <checkbox name="kontrast" label="gestörte Kontrastwahrnehmung"/>
    <checkbox name="schaerfe" label="gestörte Sehschärfe" layout="left"/>
    <checkbox name="fov" label="Gesichtsfeldeinschränkung" layout="left"/>
    <checkbox name="badglasses" label="Ungeeignete Brille" layout="br"/>
    <checkbox name="newglasses" label="neue oder angepasste Brille" tooltip="Neue, angepasste Brillen mit der passenden Stärke können das Sturzrisiko zu Beginn erhöhen (!). Das liegt daran, dass die betroffene Person sich erst an die neue Brille gewöhnen muss. In dieser Zeit steigt das Sturzrisiko." layout="left"/>

    <label size="12" fontstyle="bold" label="Psychische Störungen"/>
    <checkbox name="demenz" label="Demenz"/>
    <checkbox name="depression" label="Depression" layout="left"/>
    <checkbox name="delir" label="Delir" layout="left"/>

    <label size="12" fontstyle="bold" label="Erkrankungen" tooltip="erho?hte Belastung durch Erkrankungen oder Gesundheitssto?rungen"/>
    <label size="12" fontstyle="italic" label="Durch Ohnmachtsgefahr"/>
    <checkbox name="hypoglykämie" label="Unterzuckerung (Hypoglykämie)"  layout="br"/>
    <checkbox name="blutdruckabfall" label="Blutdruckabfall beim Aufstehen" layout="left"/>
    <checkbox name="herzrhythmusstörungen" label="Herzrhythmusstörungen" layout="left"/>
    <checkbox name="tia" label="TIA (Transitorisch ischämische Attacke)"  layout="br"/>
    <checkbox name="epilepsie" label="Epilepsie"  layout="left"/>

    <label size="12" fontstyle="italic" label="Durch veränderte Mobilität, Motorik und Epfindung"/>
    <checkbox name="ms" label="Multiple Sklerose" layout="br"/>
    <checkbox name="parkinson" label="Parkinson"  layout="left"/>
    <checkbox name="apoplexie" label="Apoplexie" layout="left"/>
    <checkbox name="polyneuropathie" label="Polyneuropathie"  layout="left"/>
    <checkbox name="krebserkrankungen" label="Krebserkrankungen" layout="left"/>
    <checkbox name="osteoathritis" label="Osteoathritis"  layout="br"/>

    <checkbox name="az" fontstyle="italic" label="andere chronische Erkrankungen / schlechter Allgemeinzustand" layout="br"/>

    <label size="12" fontstyle="bold" label="Ausscheidungsverhalten"/>
    <checkbox name="nykturie" label="Dranginkontinenz, Nykturie"/>
    <checkbox name="toilette" label="Probleme bei den Toilettengängen" layout="left"/>

    <checkbox name="hilfsm" label="Unangemessener Umgang mit Hilfsmitteln" layout="br"/>
    <checkbox name="kleidung" label="Unangemessene Kleidung und Schuhe"  layout="br"/>

    <label size="12" fontstyle="bold" label="Medikamente"/>
    <checkbox name="psychopharmaka" label="Psychopharmaka"/>
    <checkbox name="sedativa" label="Sedativa / Hypnotika" layout="left"/>
    <checkbox name="antiarrhythmika" label="Antiarrhythmika" tooltip="z.B. Metoprolol, Bisoprolol und Nebivolol" layout="left"/>
    <checkbox name="morethan4" label="mehrere Medikamente" tooltip="generell Einnahme von mehreren Medikamenten (bei mehr als 4 verschiedenen besteht ein erhöhtes Risiko)" layout="br"/>

    <label size="12" fontstyle="italic" label="Gefahren in der Umgebung werden hier nicht aufgeführt. Davon ist in einer Pflegeeinrichtung nicht auszugehen."/>' WHERE `BWINFTYP` = 'FALLRISK1';