-- Ab Version 1.16.3
UPDATE `sysprops`
SET V = '18'
WHERE K = 'dbstructure';
--
UPDATE `resinfotype`
SET `deprecated` = '1'
WHERE `BWINFTYP` = 'kpflege02';
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('kpflege03', 'Körperpflege', '', '2', '108', '0',
        '1', '
            <tx
        tooltip="[b]Seite 1, Abschnitt 3. &quot;Grundpflege&quot;[/b]
            [br/]Die Markierungen im Abschnitt &quot;Grundpflege&quot; werden entsprechenden Ihren Einträgen in diesem Formular gesetzt.
            [br/]Der Bemerkungs-Text wird in die Bemerkungs-Zeile dieses Abschnitts im Überleitbogen übernommen."/>

    <optiongroup size="18" name="SVOBERKOERPER" label="Waschen des vorderen Oberkörpers" qdvs="Zeile 47" bi="4.4.1"
                 tooltip="bi4.oberkoerper.erklaerung" >
        <option label="selbstständig" name="0" default="true" tooltip="bi4.oberkoerper.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.oberkoerper.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.oberkoerper.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi4.oberkoerper.selbst3"/>
    </optiongroup>

    <optiongroup size="18" name="SVKOPF" label="Körperpflege im Bereich des Kopfes" qdvs="Zeile 48" bi="4.4.2"
                 tooltip="bi4.kopf.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.kopf.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.kopf.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.kopf.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi4.kopf.selbst3"/>
    </optiongroup>

    <label size="14" fontstyle="bold" label="Pflege des Kopfes umfasst auch:"/>
    <checkbox name="SVKOPF.kaemmen" label="Kämmen" layout="left"/>
    <checkbox name="SVKOPF.mundpflege" label="Mundpflege" layout="left"/>
    <checkbox name="SVKOPF.rasur" label="Rasieren" layout="left"/>
    <checkbox name="SVKOPF.zahnprothese" label="Zahnprothese" layout="left"/>

    <optiongroup size="18" name="SVINTIMBEREICH" label="Waschen des Intimbereichs" qdvs="Zeile 49" bi="4.4.3"
                 tooltip="bi4.intim.erklaerung" >
        <option label="selbstständig" name="0" default="true" tooltip="bi4.intim.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.intim.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.intim.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi4.intim.selbst3"/>
    </optiongroup>

    <optiongroup size="18" name="SVDUSCHENBADEN" label="Duschen und Baden einschließlich Waschen der Haare"
                 qdvs="Zeile 50"
                 bi="4.4.4"
                 tooltip="bi4.baden.erklaerung" >
        <option label="selbstständig" name="0" default="true" tooltip="bi4.baden.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.baden.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.baden.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi4.baden.selbst3"/>
    </optiongroup>

    <optiongroup size="18" name="SVANAUSOBERKOERPER" label="An- und Auskleiden des Oberkörpers" qdvs="Zeile 51"
                 bi="4.4.5" tooltip="bi4.okankleiden.erklaerung" >
        <option label="selbstständig" name="0" default="true" tooltip="bi4.okankleiden.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.okankleiden.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.okankleiden.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi4.okankleiden.selbst3"/>
    </optiongroup>

    <optiongroup size="18" name="SVANAUSUNTERKOERPER" label="An- und Auskleiden des Unterkörpers" qdvs="Zeile 52"
                 bi="4.4.6" tooltip="bi4.ukankleiden.erklaerung" >
        <option label="selbstständig" name="0" default="true" tooltip="bi4.ukankleiden.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.ukankleiden.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.ukankleiden.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi4.ukankleiden.selbst3"/>
    </optiongroup>

    <label size="14" fontstyle="bold" label="Hilfen erfolgen im/am/in:"/>
    <checkbox name="personal.care.bed" label="Bett" layout="left"/>
    <checkbox name="personal.care.shower" label="Bad, Dusche" layout="left"/>
    <checkbox name="personal.care.basin" label="Waschbecken" layout="left"/>

    <textfield label="Bevorzugte Pflegemittel" name="preferred.careproducts" innerlayout="left"/>
');
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('ohat01', 'Mund Assessment OHAT', 'Oral Health Assessment Tool nach Murray und Schölten 2018', '2', '169', '3',
        '0', '
 <scale name="ohat" label="OHAT Wert">
        <scalegroup name="lippen" label="Lippen">
            <option label="gesund" tooltip="weich, rosa, befeuchtet" name="0" score="0" layout="left" default="true" />
            <option label="verändert" tooltip="trocken, rissig, an den Mundwinkeln gerötet" name="1" score="1"
                    layout="left"/>
            <option label="ungesund/erkrankt"
                    tooltip="Schwellung oder Knoten, weißer/roter/ulzerierter Fleck, Blutung/Ulzeartion am Mundwinkel"
                    name="2" score="2" layout="left"/>
        </scalegroup>

        <scalegroup name="zunge" label="Zunge">
            <option label="gesund" tooltip="unauffällig, durchfeuchtet, rosa" name="0" score="0" layout="left" default="true" />
            <option label="verändert" tooltip="fleckig, gefurcht, rot, belegt" name="1" score="1" layout="left"/>
            <option label="ungesund/erkrankt"
                    tooltip="rote und/oder weiße Flecken ulzeriert, geschwollen"
                    name="2" score="2" layout="left"/>
        </scalegroup>

        <scalegroup name="zahnfleisch" label="Zahnfleisch und Schleimhaut">
            <option label="gesund" tooltip="rosa, befeuchtet, glatt, keine Blutungen" name="0" score="0" layout="left" default="true" />
            <option label="verändert"
                    tooltip="trocken, glänzend, rau, gerötet, geschwollen, Geschwür/wunde Stelle unter Zahnprothese"
                    name="1" score="1" layout="left"/>
            <option label="ungesund/erkrankt"
                    tooltip="geschwollen, blutend, Geschwüre, weiße/rote Flecken, generalisierte Rötung unter Zahnprothese"
                    name="2" score="2" layout="left"/>
        </scalegroup>

        <scalegroup name="speichel" label="Speichel">
            <option label="gesund" tooltip="feuchte Schleimhäute, wässrig flüssiger Speichel" name="0" score="0"
                    layout="left" default="true" />
            <option label="verändert"
                    tooltip="trockene, klebrige Schleimhäute, wenig Speichel vorhanden, Bewohner klagt über trockenen Mund"
                    name="1" score="1" layout="left"/>
            <option label="ungesund/erkrankt"
                    tooltip="ausgetrocknete, gerötete Schleimhäute, sehr wenig/kein Speichel vorhanden, zäher Speichel, Bewohner klagt über trockenen Mund"
                    name="2" score="2" layout="left"/>
        </scalegroup>

        <scalegroup name="zaehne" label="Natürliche Zähne">
            <option label="gesund" tooltip="keine kariösen oder zerstörten Zähne/Wurzeln" name="0" score="0"
                    layout="left" default="true" />
            <option label="verändert"
                    tooltip="ein bis drei kariöse oder zerstörte Zähne/Wurzeln oder extrem abgenutzte Zähne"
                    name="1" score="1" layout="left"/>
            <option label="ungesund/erkrankt"
                    tooltip="vier oder mehr kariöse oder zerstörte Zähne/Wurzeln oder extrem abgenutzte Zähne oder weniger als vier Zähnevorhanden"
                    name="2" score="2" layout="left"/>
        </scalegroup>

        <scalegroup name="prothesen" label="Prothesen">
            <option label="gesund"
                    tooltip="keine beschädigten Prothesenflächen oder Zähne, Prothese wird regelmäßig getragen" name="0"
                    score="0" layout="left" default="true" />
            <option
                    label="verändert"
                    tooltip="eine beschädigte Prothesenfläche oder Zahn oder Prothese wird nur ein bis zwei Stunden täglich getragen oder Prothese sitzt locker"
                    name="1" score="1" layout="left"/>
            <option label="ungesund/erkrankt"
                    tooltip="mehrere beschädigte Prothesenflächen oder Zahn oder Prothese fehlt bzw. wird nicht getragen, sitzt locker und benötigt Haftmittel"
                    name="2" score="2" layout="left"/>
        </scalegroup>

        <scalegroup name="hygiene" label="Mund-/Prothesenhygiene">
            <option label="gesund" tooltip="sauber, keine Speisereste/Beläge/Zahnstein mi Mund oder an Prothese"
                    name="0" score="0"
                    layout="left" default="true" />
            <option label="verändert"
                    tooltip="Speisereste/Zahnstein/ Belag an ein bis zwei Stellen des Mundes bzw. an kleinem Bereich der Prothese oder Halitosis (Mundgeruch)"
                    name="1" score="1" layout="left"/>
            <option label="ungesund/erkrankt"
                    tooltip="Speisereste/Zahnstein/Belag an den meisten Stellen des Mundes/der Prothese und starke Halitosis (Mundgeruch)"
                    name="2" score="2" layout="left"/>
        </scalegroup>

        <scalegroup name="schmerzen" label="Zahnschmerzen">
            <option label="gesund"
                    tooltip="keine verbalen, körperlichen Zeichen oder Verhaltensindikatoren deuten auf Zahnschmerz hin"
                    name="0" score="0" layout="left" default="true" />
            <option
                    label="verändert"
                    tooltip="Verbale Zeichen und/oder Verhaltensindikatoren für Schmerz, z.B. das Gesicht verziehen, Lippen kauen, Essen verweigern, aggresives Verhalten."
                    name="1" score="1" layout="left"/>
            <option label="ungesund/erkrankt"
                    tooltip="körperliche Zeichen von Schmerz (Schwellung von Wange oder Zahnfleisch, abgebrochene Zähne, Geschwüre) sowie verbale Zeichen und/oder Verhaltensindikatoren (das Gesicht verziehen, Essen verweigern, aggressives Verhalten)"
                    name="2" score="2" layout="left"/>
        </scalegroup>

        <risk from="0" to="2" label="sehr gut" color="dark_green" rating="0"/>
        <risk from="3" to="6" label="gut" color="blue" rating="1"/>
        <risk from="7" to="9" label="mittelmäßig" color="dark_orange" rating="2"/>
        <risk from="10" to="16" label="schlecht" color="dark_red" rating="3"/>
    </scale>
    <label name="hinweis" color="blue" size="14"
           label="Oral Health Assessment Tool nach Murray und Schölten 2018"/>
');
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('mouthscrn', 'Mund Kurzscreening', 'Screening zur Beurteilung, ob ein volles OHAT Assessment nötig ist',
        '2', '169', '3', '0', '
    <label name="hinweis1" color="red" size="14"
           label="Wenn jeglicher Verdacht auf Probleme bei der Mundgesundheit ausgeschlossen werden kann, benötigen wir kein Assessment. Ansonsten bitte den OHAT ausfüllen."/>

    <label name="hinweis2" color="black" size="12"
           label="Ein Screening soll ohne Inspektion des Mundes erfolgen."/>

    <checkbox name="schmerz" label="Schmerzen, Schwellungen oder Verletzunge" layout="br left"/>
    <checkbox name="kauen" label="Probleme beim Essen/Kauen (auch Nahrungskarenzen)" layout="br left"/>
    <checkbox name="ersatz" label="Probleme mit herausnehmbarem Zahnersatz" layout="br left"/>
    <checkbox name="mundpflege" label="Probleme bei der Mundpflege" layout="br left"/>
    <checkbox name="lippen" label="Trockene/rissige Lippen, Rhagaden" layout="br left"/>
    <checkbox name="trocken" label="Mundtrockenheit" layout="br left"/>
    <checkbox name="geruch" label="Mundgeruch" layout="br left"/>

    <label name="hinweis3" color="blue" size="14"
           label="Erläuterungen übernommen aus Expertenstandaerd ''Förderung der Mundgesund, 2021''"/>
');
