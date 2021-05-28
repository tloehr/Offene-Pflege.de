-- Ab Version 1.15.2.x mit x > 600
UPDATE `sysprops`
SET V = '14'
WHERE K = 'dbstructure';
--
alter table resident
    change BWKENNUNG id CHAR(10) NOT NULL;
alter table homes
    change eid id VARCHAR(36) NOT NULL;
alter table station
    change StatID id bigint(20) unsigned auto_increment NOT NULL;
alter table station
    modify eid VARCHAR(36) NOT NULL;
alter table floors
    modify homeid VARCHAR(36) NOT NULL;
alter table resident
	add sterbephase tinyint(1) default 0 not null;
--
UPDATE resinfotype
SET deprecated = '1'
WHERE BWINFTYP IN ('fallprot01');
--
UPDATE resinfotype t
SET t.XML = '
 <tx tooltip="Die jeweils aktuelle Risikoermittlung wird, sofern ein Risiko besteht, auf dem Überleitbogen auf Seite 1 Abschnitt 7 vermerkt."/>
    <scale name="braden" label="Dekubitus Risiko-Wert">
        <scalegroup name="braden1" label="Sensorisches Empfindungsvermögen"
                    tooltip="Das ist die Fähigkeit des Bewohners  auf druckbedingte Beschwerden lindernd  zu reagieren.">
            <option label="fehlt" name="fehlt"
                    tooltip="[ul][li]keine Reaktion auf schmerzhafte Reize. Mögliche Gründe: Bewusstlosigkeit, Sedierung[/li][li]Störung der Schmerzempfindung durch Lähmung, die den größten Teil des Körpers betreffen (z.B. hoher Querschnitt)[/li][/ul]"
                    score="1" layout="tab"/>
            <option label="stark eingeschränkt" name="se" tooltip="[ul]
[li]eine Reaktion erfolgt nur auf starke Schmerzreize[/li]
[li]Beschwerden können kaum geäußert werden (z.B. nur durch Stöhnen oder Unruhe)[/li]
[li]Störung der Schmerzempfindung durch Lähmung, wovon die Hälfte des Körpers betroffen ist[/li][/ul]" score="2"/>
            <option label="leicht eingeschränkt" name="le"
                    tooltip="[ul][li]Reaktion auf Ansprache oder Kommandos[/li][li]Beschwerden können aber nicht immer ausgedrückt werden (z.B. dass die Position geändert werden soll)[/li][li]Störung der Schmerzempfindung durch Lähmung, wovon eine oder zwei Extremitäten betroffen sind[/li][/ul]"
                    score="3" layout="tab"/>
            <option label="vorhanden" name="vorhanden" score="4"
                    tooltip="[ul][li]Reaktion auf Ansprache, Beschwerden können geäußert werden[/li][li]keine Störung der Schmerzempfindung[/li][/ul]"
                    default="true" layout="tab"/>
        </scalegroup>
        <scalegroup name="braden2" label="Feuchtigkeit" tooltip="Ausmaß, in dem die Haut Feuchtigkeit ausgesetzt ist.">
            <option label="ständig feucht" name="staendig"
                    tooltip="[ul][li]die Haut ist ständig feucht durch Urin, Schweiß oder Kot[/li][li]immer, wenn der Patient gedreht wird, liegt er im Nassen[/li][/ul]"
                    score="1" layout="tab"/>
            <option label="oft feucht" name="oft"
                    tooltip="[ul][li]die Haut ist oft feucht, aber nicht immer[/li][li]Bettzeug oder Wäsche muss mindestens einmal pro Schicht gewechselt werden[/li][/ul]"
                    score="2" layout="tab"/>
            <option label="manchmal feucht" name="manchmal"
                    tooltip="[ul][li]die Haut ist manchmal feucht, und etwa einmal pro Tag wird neue Wäsche benötigt[/li][/ul]"
                    score="3" layout="tab"/>
            <option label="selten feucht" name="selten"
                    tooltip="[ul][li]die Haut ist meist trocken[/li][li]neue Wäsche wird selten benötigt[/li][/ul]"
                    score="4" default="true" layout="tab"/>
        </scalegroup>
        <scalegroup name="braden3" label="Aktivitäten" tooltip="Ausmaß der physischen Aktivität.">
            <option label="bettlägerig" name="bettlaegerig" tooltip="ans Bett gebunden" score="1" layout="tab"/>
            <option label="sitzt auf" name="sitzt"
                    tooltip="[ul][li]kann mit Hilfe etwas laufen[/li][li]kann das eigene Gewicht nicht allein tragen[/li][li]braucht Hilfe, um aufzusitzen (Bett, Stuhl, Rollstuhl)[/li][/ul]"
                    score="2" layout="tab"/>
            <option label="geht wenig" name="wenig"
                    tooltip="[ul][li]geht am Tag allein, aber selten und nur kurze Distanzen[/li][li]braucht für längere Strecken Hilfe[/li][li]verbringt die meiste Zeit im Bett oder im Stuhl[/li][/ul]"
                    score="3" layout="tab"/>
            <option label="geht regelmäßig" name="regelm"
                    tooltip="[ul][li]geht regelmäßig 2- bis 3-mal pro Schicht[/li][li]bewegt sich regelmäßig[/li][/ul]"
                    score="4" default="true" layout="tab"/>
        </scalegroup>
        <scalegroup name="braden4" label="Mobilität"
                    tooltip="Fähigkeit des Bewohners, die Position alleine zu wechseln und zu halten.">
            <option label="komplett immobil" name="komplett"
                    tooltip="kann auch keinen geringfügigen Positionswechsel ohne Hilfe ausführen" score="1"
                    layout="tab"/>
            <option label="Mobilität stark eingeschränkt" name="se"
                    tooltip="[ul][li]bewegt sich manchmal geringfügig (Körper oder Extremitäten)[/li][li]kann sich aber nicht regelmäßig allein ausreichend umlagern[/li][/ul]"
                    score="2" layout="tab"/>
            <option label="Mobilität gering eingeschränkt" name="ge"
                    tooltip="macht regelmäßig kleine Positionswechsel des Körpers und der Extremitäten" score="3"
                    layout="tab"/>
            <option label="mobil" name="mobil" tooltip="kann allein seine Position umfassend verändern" score="4"
                    default="true" layout="tab"/>
        </scalegroup>
        <scalegroup name="braden5" label="Ernährung" tooltip="Ernährungsgewohnheiten">
            <option label="sehr schlechte Ernährung" name="schlecht"
                    tooltip="[ul][li] isst kleine Portionen nie auf, sondern etwa nur 2/3[/li][li]isst nur 2 oder weniger Eiweißportionen (Milchprodukte, Fisch, Fleisch)[/li][li]trinkt zu wenig[/li][li]nimmt keine Ergänzungskost zu sich[/li][li]darf oral keine Kost zu sich nehmen[/li][li]nur klare Flüssigkeiten[/li][li]erhält Infusionen länger als 5 Tage[/li][/ul]"
                    score="1" layout="tab"/>
            <option label="mäßige Ernährung" name="maessig"
                    tooltip="[ul][li]isst selten eine normale Essensportion auf, isst aber im Allgemeinen etwa die Hälfte der angebotenen Nahrung[/li][li]isst etwa 3 Eiweißportionen (Milchprodukte, Fisch, Fleisch)[/li][li]nimmt unregelmäßig Ergänzungskost zu sich[/li][li]erhält zu wenig Nährstoffe über Sondenkost oder Infusionen[/li][/ul]"
                    score="2" layout="tab"/>
            <option label="adäquate Ernährung" name="adaequat"
                    tooltip="[ul][li]isst mehr als die Hälfte der normalen Essensportionen[/li][li]nimmt 4 Eiweißportionen (Milchprodukte, Fisch, Fleisch) zu sich[/li][li]verweigert gelegentlich eine Mahlzeit, nimmt aber Ergänzungskost zu sich[/li][li]kann über Sonde oder Infusion die meisten Nährstoffe zu sich nehmen[/li][/ul]"
                    score="3" layout="tab"/>
            <option label="gute Ernährung" name="gut"
                    tooltip="[ul][li]isst immer die gebotenen Mahlzeiten auf[/li][li]nimmt 4 oder mehr Eiweißportionen (Milchprodukte, Fisch, Fleisch) zu sich[/li][li]isst auch manchmal zwischen den Mahlzeiten[/li][li]braucht keine Ergänzungskost[/li][/ul]"
                    score="4" default="true" layout="tab"/>
        </scalegroup>
        <scalegroup name="braden6" label="Reibung und Scherkräfte">
            <option label="Problem" name="problem"
                    tooltip="[ul][li]braucht viel bis massive Unterstützung bei Lagewechsel[/li][li]Anheben ist ohne Schleifen über die Laken nicht möglich[/li][li]rutscht ständig im Bett oder im (Roll-)Stuhl herunter, muss immer wieder hochgezogen werden[/li][li]hat spastische Kontrakturen[/li][li]ist sehr unruhig (scheuert auf dem Laken)[/li][/ul]"
                    score="1" layout="tab"/>
            <option label="potenzielles Problem" name="potenziell"
                    tooltip="[ul][li]bewegt sich etwas allein oder braucht wenig Hilfe[/li][li]beim Hochziehen schleift die Haut nur wenig über die Laken (kann sich etwas anheben)[/li][li]kann sich über längere Zeit in einer Lage halten (Stuhl, Rollstuhl)[/li][li]rutscht nur selten herunter[/li][/ul]"
                    score="2" layout="tab"/>
            <option label="kein Problem zurzeit" name="kein"
                    tooltip="[ul][li]bewegt sich in Bett und Stuhl allein[/li][li]hat genügend Kraft, sich anzuheben[/li][li]kann eine Position über lange Zeit halten, ohne herunterzurutschen[/li][/ul]"
                    score="3" layout="tab" default="true"/>
        </scalegroup>
        <risk from="20" to="23" label="niedrig" color="dark_green" rating="0"/>
        <risk from="16" to="19" label="mittel" color="blue" rating="1"/>
        <risk from="11" to="15" label="hoch" color="dark_orange" rating="2"/>
        <risk from="6" to="10" label="sehr hoch" color="dark_red" rating="3"/>
    </scale>

    <separator/>
    <tabgroup size="14" fontstyle="bold" label="extrinsische Faktoren" name="extrinsic">
        <checkbox name="meds" label="Medikamente" layout="left"/>
        <checkbox name="hygiene" label="Körperhygiene" layout="left"/>
        <checkbox name="moisture" label="Feuchtigkeit" layout="br"/>
        <checkbox name="friction" label="Reibung und Scherkräfte" layout="left"/>
    </tabgroup>
    <tabgroup size="14" fontstyle="bold" label="intrinsische Faktoren" name="intrinsic">
        <checkbox name="age" label="Alter" layout="left"/>
        <checkbox name="dehydration" label="Exsikose" layout="left"/>
        <checkbox name="mobility" label="Reduzierte Mobilität" layout="left"/>
        <checkbox name="weight" label="Gewicht" layout="br"/>
        <checkbox name="metabolic" label="Stoffwechselerkrankung" layout="left"/>
        <checkbox name="neuro" label="neurologische Erkrankung" layout="left"/>
        <checkbox name="sensibility" label="Sensibilitätsstörungen" layout="left"/>
        <checkbox name="malnutrition" label="Mangelernährung" layout="br"/>
        <checkbox name="incontinence" label="Inkontinenz" layout="left"/>
        <checkbox name="infection" label="Infektion" layout="left"/>
    </tabgroup>
'
WHERE t.BWINFTYP = 'braden';
--
UPDATE resinfotype t
SET t.XML = ' <qdvs optional="false"/>

    <tabgroup size="18" fontstyle="bold" label="Hauptbezugsperson" name="hbp">
        <tx tooltip="Diese Angaben werden auf Seite 1, Abschnitt 1 eingetragen."/>
        <textfield label="Anrede" name="c1title" length="25" hfill="false" innerlayout="tab"/>
        <textfield label="Name" name="c1name" length="25" hfill="false" innerlayout="tab"/>
        <textfield label="Vorname" name="c1firstname" length="25" hfill="false" innerlayout="tab"/>
        <textfield label="Strasse" name="c1street" length="25" hfill="false" innerlayout="tab"/>
        <textfield label="PLZ" name="c1zip" length="25" hfill="false" innerlayout="tab"/>
        <textfield label="Ort" name="c1city" length="25" hfill="false" innerlayout="tab"/>
        <textfield label="Tel" name="c1tel" length="25" hfill="false" innerlayout="tab"/>
        <textfield label="Mobil" name="c1mobile" length="25" hfill="false" innerlayout="tab"/>
        <textfield label="E-Mail" name="c1email" length="25" hfill="false" innerlayout="tab"/>
        <checkbox name="c1ready2nurse" label="Pflegebereitschaft der Bezugsperson"
                  tx="Setzt ''Pflegebereitschaft der Bezugsperson'' auf ''ja'', falls angeklickt."/>
    </tabgroup>

    <tabgroup size="18" fontstyle="bold" label="Angehörige 1" name="a1">
        <textfield name="name1" label="Name" length="25" hfill="false" innerlayout="tab"/>
        <textfield name="ans1" label="Anschrift" length="25" hfill="false" innerlayout="tab"/>
        <textfield name="tel1" label="Telefon" length="25" hfill="false" innerlayout="tab"/>
    </tabgroup>

    <tabgroup size="18" fontstyle="bold" label="Angehörige 2" name="a2">
        <textfield name="name2" label="Name" length="25" hfill="false" innerlayout="tab"/>
        <textfield name="ans2" label="Anschrift" length="25" hfill="false" innerlayout="tab"/>
        <textfield name="tel2" label="Telefon" length="25" hfill="false" innerlayout="tab"/>
    </tabgroup>

    <optiongroup size="18" name="GAINTERAKTION"
                 label="Interaktion mit Personen im direkten Kontakt"
                 bi="4.6.5" tooltip="bi6.interaktion.erklaerung" tx="Seite 1, Abschnitt 8">
        <option label="selbstständig" name="0" default="true" tooltip="bi6.interaktion.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi6.interaktion.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi6.interaktion.selbst2" layout="br left"/>
        <option label="unselbständig" name="3" tooltip="bi6.interaktion.selbst3"/>
    </optiongroup>

    <optiongroup size="18" name="GAKONTAKTPFLEGE"
                 label="Kontaktpflege zu Personen außerhalb des direkten Umfelds"
                 bi="4.6.6" tooltip="bi6.kontaktpflege.erklaerung" tx="Seite 1, Abschnitt 8">
        <option label="selbstständig" name="0" default="true" tooltip="bi6.kontaktpflege.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi6.beschaeftigen.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi6.kontaktpflege.selbst2" layout="br left"/>
        <option label="unselbständig" name="3" tooltip="bi6.kontaktpflege.selbst3"/>
    </optiongroup>
      '
WHERE t.BWINFTYP = 'sozial01';
--
UPDATE resinfotype t
SET t.XML = ' <qdvs optional="false"/>

                <optiongroup size="18" name="GATAGESABLAUF"
                             label="Tagesablauf gestalten und an Veränderungen anpassen"
                             bi="4.6.1" tooltip="bi6.alltag.erklaerung">
                    <option label="selbstständig" name="0" default="true" tooltip="bi6.alltag.selbst0"/>
                    <option label="überwiegend selbständig" name="1" tooltip="bi6.alltag.selbst1"/>
                    <option label="überwiegend unselbständig" name="2" tooltip="bi6.alltag.selbst2" layout="br left"/>
                    <option label="unselbständig" name="3" tooltip="bi6.alltag.selbst3"/>
                </optiongroup>

                <optiongroup size="18" name="GABESCHAEFTIGEN"
                             label="Sich beschäftigen"
                             bi="4.6.3" tooltip="bi6.beschaeftigen.erklaerung">
                    <option label="selbstständig" name="0" default="true" tooltip="bi6.beschaeftigen.selbst0"/>
                    <option label="überwiegend selbständig" name="1" tooltip="bi6.beschaeftigen.selbst1"/>
                    <option label="überwiegend unselbständig" name="2" tooltip="bi6.beschaeftigen.selbst2"
                            layout="br left"/>
                    <option label="unselbständig" name="3" tooltip="bi6.beschaeftigen.selbst3"/>
                </optiongroup>

                <optiongroup size="18" name="GAPLANUNGEN"
                             label="Vornehmen von in die Zukunft gerichteten Planungen"
                             bi="4.6.4" tooltip="bi6.zukunft.erklaerung">
                    <option label="selbstständig" name="0" default="true" tooltip="bi6.zukunft.selbst0"/>
                    <option label="überwiegend selbständig" name="1" tooltip="bi6.zukunft.selbst1"/>
                    <option label="überwiegend unselbständig" name="2" tooltip="bi6.zukunft.selbst2" layout="br left"/>
                    <option label="unselbständig" name="3" tooltip="bi6.zukunft.selbst3"/>
                </optiongroup>
      '
WHERE t.BWINFTYP = 'alltag01';
--
UPDATE resinfotype t
SET t.XML = '     <qdvs optional="false"/>

    <tabgroup size="18" fontstyle="bold" label="Anschrift" name="a2">
        <textfield label="Name" name="niname" innerlayout="tab"/>
        <textfield label="Strasse" name="nistreet" innerlayout="tab"/>
        <textfield label="PLZ" name="nizip" innerlayout="tab"/>
        <textfield label="Ort" name="nicity" innerlayout="tab"/>
        <textfield label="Tel" name="nitel" innerlayout="tab"/>
        <textfield label="Fax" name="nifax" innerlayout="tab"/>
        <textfield label="Zuständig" name="nicontact" innerlayout="tab"/>
        <separator/>
        <textfield label="Versicherten Nr." name="personno" innerlayout="tab"/>
        <textfield label="Krankenkassen Nr." name="insuranceno" innerlayout="tab"/>
    </tabgroup>

    <optiongroup name="grade" label="Einstufung" tx="Wird auf Seite 1 eingetragen.">
        <option label="Kein Pflegegrad" name="0" layout="br"/>
        <option label="Pflegegrad 1" name="1"/>
        <option label="Pflegegrad 2" name="2"/>
        <option label="Pflegegrad 3" name="3" layout="br"/>
        <option label="Pflegegrad 4" name="4"/>
        <option label="Pflegegrad 5" name="5"/>
        <option label="andere (bitte in Beschreibung)" name="other" layout="br"/>
    </optiongroup>

    <tabgroup size="18" fontstyle="bold" label="Beantragung" name="a1">
        <checkbox name="requested" label="Einstufung beantrag" layout="left"/>
        <textfield label="Beantragt am" name="requestdate" length="12" type="date" innerlayout="tab"
                   tx="Wird auf Seite 1 eingetragen."/>
    </tabgroup> '
WHERE t.BWINFTYP = 'ninsur03';
--
UPDATE resinfotype t
SET t.XML = ' <copyfromtemplate/>
          <tx tooltip="Diese Angaben werden auf Seite 1, Abschnitt 1 eingetragen. Sobald dieser Eintrag vorhanden ist, wird auch ''Gesetzlicher Betreuer'' gesetzt."/>

          <tabgroup size="18" fontstyle="bold" label="Anschrift" name="a2">
              <textfield label="Anrede" name="title" innerlayout="tab"/>
              <textfield label="Name" name="name" innerlayout="tab"/>
              <textfield label="Vorname" name="firstname" innerlayout="tab"/>
              <textfield label="Organisation" name="orga" innerlayout="tab"/>
              <textfield label="Strasse" name="street" innerlayout="tab"/>
              <textfield label="PLZ" name="zip" innerlayout="tab"/>
              <textfield label="Ort" name="city" innerlayout="tab"/>
              <textfield label="Tel" name="tel" innerlayout="tab"/>
              <textfield label="Mobil" name="mobile" innerlayout="tab"/>
              <textfield label="Fax" name="fax" innerlayout="tab"/>
              <textfield label="E-Mail" name="email" innerlayout="tab"/>
          </tabgroup>

          <tabgroup size="18" fontstyle="bold" label="Aufgabenkreis" name="a1">
              <checkbox name="finance" label="Vermögenssorge" tooltip="check" tx="Setzt ''Vermögensverwaltung''"/>
              <checkbox name="health" label="Gesundheitssorge" layout="left" tx="Setzt ''Gesundheitsvorsorge''"/>
              <checkbox name="confinement" label="Aufenthaltsbestimmung" layout="left" tx="Setzt ''Aufenthaltsbestimmung''"/>
              <checkbox name="residence" label="Wohnungsangelegenheiten" layout="br"/>
              <checkbox name="legal" label="Vertretung des Betroffenen in gerichtlichen Verfahren" layout="left"/>
              <checkbox name="official" label="Vertretung gegenüber Behörden" layout="br"/>
              <checkbox name="postal" label="Post (siehe Erläuterung)"
                        tooltip="Entscheidung über den Fernmeldeverkehr des Betroffenen und über die Entgegennahme, das Öffnen und Anhalten seiner Post"
                        layout="left"/>
              <checkbox name="welfare" label="Sozialhilfeangelegenheiten" layout="br"/>
              <checkbox name="pension" label="Rentenangelegenheiten" layout="left"/>
              <textfield label="Sonstiges" innerlayout="tab" name="other"/>
              <textfield label="Einwilligungsvorbehalt"
                         tooltip="Schreibe hier die Bereiche auf, in denen die Willenserklärung des Betroffenen eingeschränkt wurde."
                         innerlayout="tab" name="veto" layout="p"/>
          </tabgroup>'
WHERE t.BWINFTYP = 'LCUST';
UPDATE resinfotype t
SET t.XML = '    <optiongroup name="application" label="Anwendung"
                       tooltip="wird verwendet zur Prävalenzmessung im Rahmen des MRE-Siegels">
              <option label="lokal" name="local"/>
              <option label="systemisch" name="systemic" default="true"/>
          </optiongroup>

          <optiongroup name="treatment" label="Art der Behandlung">
              <option label="prophylaktisch" name="prophylactic"/>
              <option label="therapeutisch" name="therapeutic" default="true"/>
          </optiongroup>

          <tabgroup label="Antibiotikagabe wegen Infektion" name="reason">
              <checkbox name="inf.urethra" label="Harnwege" layout="br"/>
              <checkbox name="inf.skin.wound" label="Haut- oder Wunden"/>
              <checkbox name="inf.respiratoric" label="Atemwege"/>
              <checkbox name="inf.digestive" label="Magen-Darmtrakt" layout="br"/>
              <checkbox name="inf.eyes" label="Augen"/>
              <checkbox name="inf.ear.nose.mouth" label="Ohren/Nase/Mund"/>
              <checkbox name="inf.systemic" label="systemische Infektion" layout="br"/>
              <checkbox name="inf.fever" label="unerklärbares Fieber"/>
          </tabgroup>
          <textfield name="inf.other" label="andere Gründe" length="10" optional="true" hfill="false"/>

          <combobox label="Wo wurde die Therapie begonnen" name="therapy.start">
              <item label="Einrichtung" name="here"/>
              <item label="Krankenhaus" name="hospital"/>
              <item label="anderer Ort" name="other"/>
          </combobox>

          <combobox label="Wer hat das Mittel verschrieben" name="prescription.by">
              <item label="Hausarzt" name="gp"/>
              <item label="Facharzt" name="specialist"/>
              <item label="Notarzt" name="emergency"/>
          </combobox>

          <tabgroup label="Sonstige Angaben" name="sonst">
              <checkbox name="diag.urinetest" label="Urintest vor Therapie durchgeführt" layout="br"/>
              <checkbox name="diag.microbiology" label="Mikrobiologische Diagnostik vor der Therapie"/>
              <textfield name="diag.result" label="isolierter Erreger" length="30" optional="true" hfill="false"/>
              <textfield name="diag.resistent" label="Antibiotikaresistenz" length="30" optional="true" hfill="false"/>
          </tabgroup>
          <url label="Nach: ''Bewohner B - Bewohner Fragebogen'' (mre-netz regio rhein-ahr, siehe OPDE Quellen EXNER01)"
               link="https://www.offene-pflege.de/de/sources-de"/>'
WHERE t.BWINFTYP = 'ANTIBIO1';
--
UPDATE resinfotype t
SET t.XML = '  <qdvs optional="false"/>
    <tx tooltip="Sobald das Inkontinenzprofil nicht mehr auf &quot;Kontinenz&quot; steht, wird im Überleitbogen die Markierung für &quot;Harninkontinenz&quot; gesetzt."/>

    <optiongroup size="18" name="SVTOILETTE"
                 label="Benutzen einer Toilette oder eines Toilettenstuhls" tooltip="bi4.toilette.erklaerung"
                 tx="Seite 1, Abschnitt 3">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.toilette.selbst0"/>
        <option label="überwiegend selbständig" name="2" tooltip="bi4.toilette.selbst2"/>
        <option label="überwiegend unselbständig" name="4" tooltip="bi4.toilette.selbst4" layout="br left"/>
        <option label="unselbständig" name="6" tooltip="bi4.toilette.selbst6"/>
    </optiongroup>
    <separator/>

    <optiongroup size="18" name="SVSTUHLKONTINENZ" label="Darmkontrolle, Stuhlkontinenz"
                 tooltip="bi4.stuhlangabe.erklaerung">

        <option label="ständig kontinent" name="0" default="true" tooltip="bi4.stuhlangabe.stufe0" layout="br left"/>
        <option label="überwiegend kontinent" name="1" tooltip="bi4.stuhlangabe.stufe1"/>
        <option label="überwiegend IN_kontinent" name="2" tooltip="bi4.stuhlangabe.stufe2" layout="br left"/>
        <option label="komplett IN_kontinent" name="3" tooltip="bi4.stuhlangabe.stufe3"/>
        <option label="Person hat ein Colo- oder Ileostoma" name="4" tooltip="bi4.stuhlangabe.stufe4"/>
    </optiongroup>

    <optiongroup size="18" name="SVSTUHLKONTINENZBEW"
                 label="Bewältigen der Folgen einer Stuhlinkontinenz und Umgang mit Stoma" tooltip="bi4.stuhlbewaeltigung.erklaerung"
                 tx="Seite 1, Abschnitt 3">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.stuhlbewaeltigung.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.stuhlbewaeltigung.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.stuhlbewaeltigung.selbst2" layout="br left"/>
        <option label="unselbständig" name="3" tooltip="bi4.stuhlbewaeltigung.selbst3"/>
    </optiongroup>

    <tabgroup label="Sonstige Angaben" name="sonsta">
        <checkbox name="diarrhoe" label="Neigt zu Durchfällen" tx="Seite 1, Abschnitt 5" layout="br left"/>
        <checkbox name="obstipation" label="Neigt zu Verstopfung" tx="Seite 1, Abschnitt 5" layout="left"/>
        <checkbox name="digital" label="Digitales Ausräumen"
                  tooltip="Das digitale Ausräumen beschreibt eine Maßnahme zur manuellen Entfernung von hartem Stuhl aus dem Enddarm.[br/]Diese Behandlung wird vorallem bei Koprostase, Stuhlimpaktion oder einer Darmlähmung durchgeführt."
                  tx="Seite 1, Abschnitt 5" layout="left"/>
        <checkbox name="ap.aid" label="Anus Praeter" tx="Seite 1, Abschnitt 5"/>
    </tabgroup>

    <optiongroup size="18" name="SVHARNKONTINENZ" label="Blasenkontrolle/Harnkontinenz"
                 tooltip="bi4.harnangabe.erklaerung">
        <option label="ständig kontinent" name="0" default="true" tooltip="bi4.harnangabe.stufe0" layout="br left"/>
        <option label="überwiegend kontinent" name="1" tooltip="bi4.harnangabe.stufe1"/>
        <option label="überwiegend IN_kontinent" name="2" tooltip="bi4.harnangabe.stufe2" layout="br left"/>
        <option label="komplett IN_kontinent" name="3" tooltip="bi4.harnangabe.stufe3"/>
        <option label="Person hat einen Dauerkatheter oder ein Urostoma" name="4" tooltip="bi4.harnangabe.stufe4"/>
    </optiongroup>

    <optiongroup size="18" name="hinkotag" label="Harn-Inkontinenzprofil (Tag)"
                 tooltip="exp.kontinenzprof.erklaerung">
        <option label="Kontinenz" name="kontinenz" default="true" tooltip="exp.harn.kontinenz"/>
        <option label="Unabhängig erreichte Kontinenz" name="uek" tooltip="exp.harn.uek"/>
        <option label="Abhängig erreichte Kontinenz" name="aek" tooltip="exp.harn.aek"/>
        <option label="Unabhängig kompensierte Inkontinenz" name="uki" tooltip="exp.harn.uki" layout="br left"/>
        <option label="Abhängig kompensierte Inkontinenz" name="aki" tooltip="exp.harn.aki"/>
        <option label="Nicht kompensierte Inkontinenz" name="nki" tooltip="exp.harn.nki" layout="br left"/>
    </optiongroup>

    <optiongroup size="18" name="hinkonacht" label="Harn-Inkontinenzprofil (Nacht)"
                 tooltip="exp.kontinenzprof.erklaerung">
        <option label="Kontinenz" name="kontinenz" default="true" tooltip="exp.harn.kontinenz"/>
        <option label="Unabhängig erreichte Kontinenz" name="uek" tooltip="exp.harn.uek"/>
        <option label="Abhängig erreichte Kontinenz" name="aek" tooltip="exp.harn.aek"/>
        <option label="Unabhängig kompensierte Inkontinenz" name="uki" tooltip="exp.harn.uki" layout="br left"/>
        <option label="Abhängig kompensierte Inkontinenz" name="aki" tooltip="exp.harn.aki"/>
        <option label="Nicht kompensierte Inkontinenz" name="nki" tooltip="exp.harn.nki" layout="br left"/>
    </optiongroup>

    <optiongroup size="18" name="SVHARNKONTINENZBEW"
                 label="Bewältigen der Folgen einer Harninkontinenz und Umgang mit Dauerkatheter und Urostoma"
                 tooltip="bi4.harnbewaeltigung.erklaerung" tx="Seite 1, Abschnitt 3">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.harnbewaeltigung.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.harnbewaeltigung.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.harnbewaeltigung.selbst2" layout="br left"/>
        <option label="unselbständig" name="3" tooltip="bi4.harnbewaeltigung.selbst3"/>
    </optiongroup>

    <label size="18" label="Ursachenanalyse für eine Harninkontinenz (falls vorhanden)"/>
    <tabgroup size="12" label="Kognitive Einschränkung" name="kogn1">
        <checkbox name="kogn1" label="Abnahme geistiger Leistungsfähigkeit"/>
        <checkbox name="kogn2" label="BewohnerIn bemerkt den Harndrang nicht" layout="left"/>
        <checkbox name="kogn3" label="BewohnerIn findet die Toilette nicht" layout="br"/>
        <checkbox name="kogn4" label="BewohnerIn hat Angst oder schämt sich bzgl. der Inkontinenz" layout="left"/>
    </tabgroup>
    <tabgroup size="12" label="Körperliche Einschränkung" name="krp1">
        <checkbox name="koerp1" label="BW schafft es nicht alleine auf die Toilette"/>
        <checkbox name="koerp2" label="BW ist gangunsicher" layout="left"/>
        <checkbox name="koerp3" label="hat Schwierigkeiten das Gleichgewicht zu halten" layout="left"/>
        <checkbox name="koerp4" label="Muskelkraft fehlt" layout="br"/>
        <checkbox name="koerp5" label="Flexibilität des Körpers fehlt" layout="left"/>
        <checkbox name="koerp6" label="Fingerfertigkeit fehlt"
                  tooltip="z.B. fehlt dem BW die Fähigkeit die Hose oder den Rock aufzuknöpfen." layout="left"/>
        <checkbox name="koerp7" label="BW sieht schlecht" layout="left"/>
        <checkbox name="koerp8" label="BW ist immobil" layout="br"/>
    </tabgroup>
    <tabgroup size="12" label="Erkrankungen" name="erkr1a">
        <checkbox name="erk1" label="Schlaganfall"/>
        <checkbox name="erk2" label="MS" layout="left"/>
        <checkbox name="erk3" label="Parkinson" layout="left"/>
        <checkbox name="erk4" label="Demenz" layout="left"/>
        <checkbox name="erk5" label="Diabetes Mellitus" layout="left"/>
        <checkbox name="erk6" label="Herzinsuffizienz" layout="left"/>
    </tabgroup>

    <tabgroup size="12" label="Medikamente"
              tooltip="Erhält der BW Medikamente, welche die Harn-Inkontinenz fördern ?" name="medik1">

        <checkbox name="med1" label="Diuretika"/>
        <checkbox name="med2" label="Anticholinergika" layout="left"/>
        <checkbox name="med3" label="Antihistaminika" layout="left"/>
        <checkbox name="med4" label="Antidepressiva" layout="left"/>
        <checkbox name="med5" label="Neuroleptika" layout="left"/>
        <checkbox name="med6" label="Kalziumantagonisten" layout="br"/>
        <checkbox name="med7" label="Opiate" layout="left"/>
    </tabgroup>
    <tabgroup size="12" label="Besonderheiten" name="besnd1">
        <checkbox name="bes1" label="Harnwegsinfekt" tooltip="Bitte Ausschluss mittels Urinanalyse wenn nötig"/>
        <checkbox name="bes2" label="Obstipation (bei Frauen)" layout="left"/>
        <checkbox name="bes3" label="Beckbodenschwäche (bei Frauen) z.B. bei Adipositas" layout="left"/>
        <checkbox name="bes4" label="Östrogenmangel (bei Frauen)" layout="br"/>
        <checkbox name="bes5" label="Veränderungen der Prostata (bei Männern)" layout="left"/>
    </tabgroup>'
WHERE t.BWINFTYP = 'aussch01';
--
UPDATE resinfotype t
SET t.XML = ' <tx tooltip="Diese Eintragungen werden in den Überleitbogen übernommen. Seite 2, Abschnitt 11."/>
          <textfield name="mothertongue" label="Muttersprache"
                     tx="Übernahme in ''Muttersprache'' auf Seite 1 des Überleitbogen."/>
          <tabgroup size="12" label="Hilfsmittel" name="hm1">
              <checkbox name="hearaid" label="Hörgerät"/>
              <checkbox name="glassesnear" label="Lesebrille" layout="left"/>
              <checkbox name="glassesfar" label="Weitsichtbrille" layout="left"/>
          </tabgroup>

          <optiongroup name="ability1" label="Sprache">
              <option label="ohne Einschränkung" name="oE1" default="true"/>
              <option label="mit Einschränkungen" name="mE1"/>
              <option label="zeitweise eingeschränkt" name="zE1"/>
          </optiongroup>
          <optiongroup name="ability2" label="Sprachverständnis">
              <option label="ohne Einschränkung" name="oE2" default="true"/>
              <option label="mit Einschränkungen" name="mE2"/>
              <option label="zeitweise eingeschränkt" name="zE2"/>
          </optiongroup>
          <optiongroup name="ability3" label="Gehör">
              <option label="ohne Einschränkung" name="oE3" default="true"/>
              <option label="mit Einschränkungen" name="mE3"/>
              <option label="zeitweise eingeschränkt" name="zE3"/>
          </optiongroup>
          <optiongroup name="ability4" label="Sehen">
              <option label="ohne Einschränkung" name="oE4" default="true"/>
              <option label="mit Einschränkungen" name="mE4"/>
              <option label="zeitweise eingeschränkt" name="zE4"/>
          </optiongroup>
          <optiongroup name="ability5" label="Schrift">
              <option label="ohne Einschränkung" name="oE5" default="true"/>
              <option label="mit Einschränkungen" name="mE5"/>
              <option label="zeitweise eingeschränkt" name="zE5"/>
          </optiongroup>
          <optiongroup name="ability6" label="Geruch">
              <option label="ohne Einschränkung" name="oE6" default="true"/>
              <option label="mit Einschränkungen" name="mE6"/>
              <option label="zeitweise eingeschränkt" name="zE6"/>
          </optiongroup>
          <optiongroup name="ability7" label="Geschmack">
              <option label="ohne Einschränkung" name="oE7" default="true"/>
              <option label="mit Einschränkungen" name="mE7"/>
              <option label="zeitweise eingeschränkt" name="zE7"/>
          </optiongroup>
          <optiongroup name="ability8" label="Tastempfinden">
              <option label="ohne Einschränkung" name="oE8" default="true"/>
              <option label="mit Einschränkungen" name="mE8"/>
              <option label="zeitweise eingeschränkt" name="zE8"/>
          </optiongroup>'
WHERE t.BWINFTYP = 'COMMS';
--
UPDATE resinfotype t
SET t.XML = '  <qdvs optional="false"/>

    <optiongroup name="SVESSEN" size="18" label="Essen" tooltip="bi4.essen.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.essen.selbst0"/>
        <option label="überwiegend selbständig" name="3" tooltip="bi4.essen.selbst3"/>
        <option label="überwiegend unselbständig" name="6" tooltip="bi4.essen.selbst6"/>
        <option label="unselbständig" name="9" tooltip="bi4.essen.selbst9"/>
    </optiongroup>

    <optiongroup name="SVTRINKEN" size="18" label="Trinken" tooltip="bi4.trinken.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.trinken.selbst0"/>
        <option label="überwiegend selbständig" name="2" tooltip="bi4.trinken.selbst2"/>
        <option label="überwiegend unselbständig" name="4" tooltip="bi4.trinken.selbst4"/>
        <option label="unselbständig" name="6" tooltip="bi4.trinken.selbst6"/>
    </optiongroup>

    <optiongroup name="SVNAHRUNGZUBEREITEN" size="18" tooltip="bi4.mundgerecht.erklaerung"
                 label="Mundgerechtes Zubereiten der Nahrung, Eingießen von Getränken">

        <option label="selbstständig" name="0" default="true" tooltip="bi4.mundgerecht.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.mundgerecht.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.mundgerecht.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi4.mundgerecht.selbst3"/>
    </optiongroup>

    <tabgroup size="18" label="Sonstige Angaben" name="sonst">
        <checkbox label="Nahrungskarenz" name="abrosia" layout="br left"/>
        <checkbox label="Diätkost" name="diet" layout="left"/>
        <checkbox label="Schluckstörungen" name="dysphagia" layout="left"/>
        <textfield name="likes" label="Vorlieben, Essen und Trinken" hfill="false" length="40"/>
        <textfield name="hates" label="Abneigungen, Essen und Trinken" hfill="false" length="40"/>

        <textfield name="zieltrinkmenge" label="Zieltrinkmenge (ml in 24h)"
                   tooltip="Hinterlegen Sie die zugehörige ärztliche Verordnung." layout="br" hfill="false"
                   type="double"
                   length="20"/>

        <textfield name="ubw" label="Übliches Körpergewicht"
                   tooltip="Was [der|die] Bewohner[in] als [ihr|sein] übliches Gewicht angibt. Einheit: kg"
                   type="double"
                   layout="br" hfill="false" length="20"/>
    </tabgroup>'
WHERE t.BWINFTYP = 'ern01';
--
UPDATE resinfotype t
SET t.XML = ' <optiongroup name="sturzrisiko" label="Wie hoch wird das Sturzrisiko eingeschätzt ?">
              <option label="stark" name="ja"/>
              <option label="mittel" name="mittel"/>
              <option label="leicht" name="leicht"/>
              <option label="kein" name="nein" default="true"/>
          </optiongroup>
          <imagelabel image="/artwork/32x32/hillslope.png"
                      text="Dieses Symbol erscheint bei einem Risiko *stark* oder *mittel*"/>

          <label name="hinweis" label="Nicht vergessen: schriftliche Auswertung in das Bemerkungsfeld schreiben" color="red"/>

          <tabgroup size="12" fontstyle="bold" label="Sturzanamnese" name="sanam">
              <checkbox name="past" label="fallrisk.past"/>
              <checkbox name="fear" label="fallrisk.fear" layout="left"/>
          </tabgroup>

          <tabgroup size="12" fontstyle="bold" label="Funktionseinbußen und Funktionsbeeinträchtigungen" name="tab2">
              <checkbox name="balance" label="Probleme mit der Körperbalance"/>
              <checkbox name="gang" label="Gangveränderungen / eingeschränkte Bewegungsfreiheit" layout="left"/>
          </tabgroup>

          <tabgroup size="12" fontstyle="bold" label="Sehstörungen" name="tab3">
              <checkbox name="kontrast" label="gestörte Kontrastwahrnehmung"/>
              <checkbox name="schaerfe" label="gestörte Sehschärfe" layout="left"/>
              <checkbox name="fov" label="Gesichtsfeldeinschränkung" layout="left"/>
              <checkbox name="badglasses" label="Ungeeignete Brille" layout="br"/>
              <checkbox name="newglasses" label="neue oder angepasste Brille"
                        tooltip="Neue, angepasste Brillen mit der passenden Stärke können das Sturzrisiko zu Beginn erhöhen (!). Das liegt daran, dass die betroffene Person sich erst an die neue Brille gewöhnen muss. In dieser Zeit steigt das Sturzrisiko."
                        layout="left"/>
          </tabgroup>

          <tabgroup size="12" fontstyle="bold" label="Psychische Störungen" name="tab4">
              <checkbox name="demenz" label="Demenz"/>
              <checkbox name="depression" label="Depression" layout="left"/>
              <checkbox name="delir" label="Delir" layout="left"/>
          </tabgroup>

          <label size="18" fontstyle="bold" label="Erkrankungen"
                 tooltip="erhöhte Belastung durch Erkrankungen oder Gesundheitsstörungen"/>
          <tabgroup size="12" fontstyle="bold" label="Durch Ohnmachtsgefahr" name="tab5">
              <checkbox name="hypoglykämie" label="Unterzuckerung (Hypoglykämie)" layout="br"/>
              <checkbox name="blutdruckabfall" label="Blutdruckabfall beim Aufstehen" layout="left"/>
              <checkbox name="herzrhythmusstörungen" label="Herzrhythmusstörungen" layout="left"/>
              <checkbox name="tia" label="TIA (Transitorisch ischämische Attacke)" layout="br"/>
              <checkbox name="epilepsie" label="Epilepsie" layout="left"/>
          </tabgroup>

          <tabgroup size="12" fontstyle="bold" label="Durch veränderte Mobilität, Motorik und Epfindung" name="tab6">
              <checkbox name="ms" label="Multiple Sklerose" layout="br"/>
              <checkbox name="parkinson" label="Parkinson" layout="left"/>
              <checkbox name="apoplexie" label="Apoplexie" layout="left"/>
              <checkbox name="polyneuropathie" label="Polyneuropathie" layout="left"/>
              <checkbox name="krebserkrankungen" label="Krebserkrankungen" layout="left"/>
              <checkbox name="osteoathritis" label="Osteoathritis" layout="br"/>
              <checkbox name="az" fontstyle="italic" label="andere chronische Erkrankungen / schlechter Allgemeinzustand"
                        layout="br"/>
          </tabgroup>


          <tabgroup size="12" fontstyle="bold" label="Sonstiges" name="tab7">
              <checkbox name="nykturie" label="Dranginkontinenz, Nykturie"/>
              <checkbox name="toilette" label="Probleme bei den Toilettengängen" layout="left"/>
              <checkbox name="hilfsm" label="Unangemessener Umgang mit Hilfsmitteln" layout="br"/>
              <checkbox name="kleidung" label="Unangemessene Kleidung und Schuhe" layout="br"/>
          </tabgroup>

          <tabgroup size="12" fontstyle="bold" label="Medikamente" name="tab8">
              <checkbox name="psychopharmaka" label="Psychopharmaka"/>
              <checkbox name="sedativa" label="Sedativa / Hypnotika" layout="left"/>
              <checkbox name="antiarrhythmika" label="Antiarrhythmika" tooltip="z.B. Metoprolol, Bisoprolol und Nebivolol"
                        layout="left"/>
              <checkbox name="morethan4" label="mehrere Medikamente"
                        tooltip="generell Einnahme von mehreren Medikamenten (bei mehr als 4 verschiedenen besteht ein erhöhtes Risiko)"
                        layout="br"/>
          </tabgroup>

          <label size="12" fontstyle="italic"
                 label="Gefahren in der Umgebung werden hier nicht aufgeführt. Davon ist in einer Pflegeeinrichtung nicht auszugehen."/>'
WHERE t.BWINFTYP = 'FALLRISK1';
--
UPDATE resinfotype t
SET t.XML = '<textfield name="gericht" label="Amtsgericht"/>
          <textfield label="Beschluss läuft ab" length="12" name="datum" type="date"/>

          <tabgroup size="12" fontstyle="bold" label="Art der Fixierung" name="tab1">
              <checkbox name="isolation" label="Zimmerisolation"/>
              <checkbox name="gurte" label="Fixierung durch Gurte" layout="left"/>
              <checkbox name="geristuhl" label="Fixierung durch Tisch des Geristuhl" layout="left"/>
              <checkbox name="bettgitter" label="Bettgitter"/>
              <checkbox name="sedierung" label="Sedierung" layout="left"/>
          </tabgroup>

          <label size="16" fontstyle="bold"
                 label="Denken Sie daran, dass Sie den Gerichtsbeschluss scannen und hier anhängen."/>'
WHERE t.BWINFTYP = 'FIXATION1';
--
UPDATE resinfotype t
SET t.XML = '<imagelabel image="/artwork/48x48/diabetes.png"/>
    <tx tooltip="Diese Eintragungen werden in den Überleitbogen übernommen. Seite 2, Abschnitt 16."/>
    <qdvs/>
    <textfield name="description" label="Diabetes" length="30" hfill="false"/>
    <optiongroup name="application" label="Verabreichung per">
        <option label="nicht insulinpflichtig" name="none" default="true"/>
        <option label="Pen" name="pen"/>
        <option label="Spritze" name="syringe"/>
        <option label="Pumpe" name="pump"/>
    </optiongroup>'
WHERE t.BWINFTYP = 'DIABETES1';
--
UPDATE resinfotype t
SET t.XML = ' <qdvs optional="true"/>
    <tabgroup size="18" fontstyle="bold"
              label="Welche der aufgeführten Punkte trafen laut Pflegedokumentation für den Bewohner bzw. die Bewohnerin seit der letzten Ergebniserfassung zu?">
        <checkbox label="Gewichtsverlust durch medikamentöse Ausschwemmung" name="1"/>
        <checkbox label="Gewichtsverlust aufgrund ärztlich angeordneter oder ärztlich genehmigter Diät" name="2"
                  layout="br left"/>
        <checkbox label="Mindestens 10% Gewichtsverlust während eines Krankenhausaufenthalts" name="3"
                  layout="br left"/>
        <checkbox
                label="Aktuelles Gewicht bzw. Körpergröße liegt nicht vor. BW wird aufgrund einer Entscheidung des Arztes oder der Angehörigen oder eines Betreuers nicht mehr gewogen"
                name="4" layout="br left"/>
        <checkbox
                label="Aktuelles Gewicht bzw. Körpergröße liegt nicht vor. BW möchte nicht gewogen werden" name="5"
                layout="br left"/>
    </tabgroup> '
WHERE t.BWINFTYP = 'gewdoku1';
--
UPDATE resinfotype t
SET t.XML = ' <imagelabel image="/artwork/48x48/biohazard.png"/>
          <tx tooltip="Diese Eintragungen werden in den Überleitbogen übernommen. Seite 2, Abschnitt 10.[br/]Ausserdem führt eine [b]multiresistente Infektion[/b] dazu, dass die Anlage ''MRE'' erstellt und beigefügt wird."/>
          <tabgroup size="16" label="Art der Infektion" name="tab1">
              <checkbox label="MRSA" tooltip="Methicillin-resistenter Staphylococcus aureus" name="mrsa"/>
              <checkbox label="VRE" tooltip="Vancomycin-resistente Enterokokken" name="vre" layout="left"/>
              <checkbox label="2-MRGN"
                        tooltip="Multiresistente, gramnegative Bakterien (resistent gegen 2 Antibiotikaklassen)"
                        name="2mrgn" layout="left"/>
              <checkbox label="3-MRGN"
                        tooltip="Multiresistente, gramnegative Bakterien (resistent gegen 3 Antibiotikaklassen)"
                        name="3mrgn" layout="left"/>
              <checkbox label="4-MRGN"
                        tooltip="Multiresistente, gramnegative Bakterien (resistent gegen 4 Antibiotikaklassen)"
                        name="4mrgn" layout="left"/>
              <textfield name="other" label="Andere Infektion" length="40" hfill="false" innerlayout="tab"/>
              <checkbox label="multi-resistent"
                        tooltip="Diese Markierung ist nur nötig, wenn es sich nicht um MRSA, VRE oder ESBL handelt."
                        name="mre"
                        layout="left"/>
          </tabgroup>
          <optiongroup name="lab" label="Untersuchung">
              <option label="gesichert" name="confirmed"/>
              <option label="nicht gesichert (Befund steht noch aus)" name="waiting"/>
              <option label="nicht untersucht" name="notchecked" default="true"/>
          </optiongroup>
          <tabgroup size="16" label="Lokalisation" name="localize">
              <checkbox label="Nase" name="nose"/>
              <checkbox label="Rachen" name="pharynx" layout="left"/>
              <checkbox label="Urin" name="urine" layout="left"/>
              <checkbox label="Respirationstrakt" name="respiration" layout="left"/>
          </tabgroup>
          <textfield name="woundtext" label="Wunde" length="30" hfill="false" layout="br" innerlayout="tab"/>
          <textfield name="otherplace" label="Sonstiges" layout="br" length="30" hfill="false" innerlayout="tab"/>
          <textfield name="date" label="Datum Erstbefund" layout="br" type="date" optional="true" length="20" hfill="false"
                     innerlayout="tab"/>
          <bodyscheme name="bs1"/>
          <label size="16" fontstyle="bold" label="Sanierung (bei Besiedlung)" name="l1"/>
          <textfield name="cleaningfrom" label="von" length="10" type="date" optional="true" hfill="false"/>
          <textfield name="cleaningto" label="bis" length="10" hfill="false" type="date" optional="true" layout="left"
                     innerlayout="left"/>
          <textfield name="cleaningwith" label="mit" length="30" hfill="false" layout="br"/>
          <label size="16" fontstyle="bold" label="Therapie (bei Infektion)" name="l2"/>
          <textfield name="therapylocal" label="lokal" length="30" hfill="false" innerlayout="tab"/>
          <textfield name="therapysystem" label="systemisch" length="30" hfill="false" layout="br" innerlayout="tab"/>
          <textfield name="therapyfrom" label="Beginn" length="10" hfill="false" type="date" optional="true" layout="br"
                     innerlayout="tab"/>
          <textfield name="therapyto" label="bis (vorraussichtlich)" length="10" type="date" optional="true" hfill="false"
                     layout="left" innerlayout="tab"/>
          <label size="16" fontstyle="bold" label="Verlauf" name="l3"/>
          <textfield name="med1" label="Wirkstoff" length="30" hfill="false" innerlayout="tab"/>
          <textfield name="dose1" label="Dosierung" length="30" hfill="false" innerlayout="tab"/>
          <textfield name="from1" label="Von" length="10" hfill="false" type="date" optional="true" innerlayout="tab"/>
          <textfield name="to1" label="Bis" length="10" type="date" optional="true" layout="left" innerlayout="left"/>
          <textfield name="med2" label="Wirkstoff" length="30" hfill="false" layout="p" innerlayout="tab"/>
          <textfield name="dose2" label="Dosierung" length="30" hfill="false" innerlayout="tab"/>
          <textfield name="from2" label="Von" length="10" hfill="false" type="date" optional="true" innerlayout="tab"/>
          <textfield name="to2" label="Bis" length="10" type="date" optional="true" layout="left" innerlayout="left"/>
          <textfield name="med3" label="Wirkstoff" length="30" hfill="false" layout="p" innerlayout="tab"/>
          <textfield name="dose3" label="Dosierung" length="30" hfill="false" innerlayout="tab"/>
          <textfield name="from3" label="Von" length="10" hfill="false" type="date" optional="true" innerlayout="tab"/>
          <textfield name="to3" label="Bis" length="10" type="date" optional="true" layout="left" innerlayout="left"/>
          <textfield name="med4" label="Wirkstoff" length="30" hfill="false" layout="p" innerlayout="tab"/>
          <textfield name="dose4" label="Dosierung" length="30" hfill="false" innerlayout="tab"/>
          <textfield name="from4" label="Von" length="10" hfill="false" type="date" optional="true" innerlayout="tab"/>
          <textfield name="to4" label="Bis" length="10" type="date" optional="true" layout="left" innerlayout="left"/>'
WHERE t.BWINFTYP = 'INFECT2';
--
UPDATE resinfotype t
SET t.XML = '  <qdvs optional="true"/>

    <optiongroup size="18" name="EINZUGGESPR"
                 label="Integrationsgespräch"
                 tooltip="Ist in den Wochen nach dem Einzug mit dem Bewohner bzw. der Bewohnerin und/oder einer seiner bzw. ihrer Angehörigen oder sonstigen Vertrauenspersonen ein Gespräch über sein bzw. ihr Einleben und die zukünftige Versorgung geführt worden?">
        <option label="ja" name="1" default="true"/>
        <option label="nicht möglich aufgrund fehlender Vertrauenspersonen des Bewohners bzw. der Bewohnerin"
                name="2" layout="br left"/>
        <option label="nein, aus anderen Gründen" name="3" layout="br left"/>
    </optiongroup>

    <tabgroup size="18" fontstyle="bold" label="Wer hat an dem Integrationsgespräch teilgenommen?"
              name="tab1">
        <checkbox name="1" label="Bewohner/Bewohnerin" default="true" layout="br left"/>
        <checkbox name="2" label="Angehörige"/>
        <checkbox name="3" label="Betreuer/Betreuerin"/>
        <checkbox name="4" label="andere Vertrauenspersonen, die nicht in der Einrichtung beschäftigt sind"
                  layout="left"/>
    </tabgroup>

    <optiongroup size="18" name="EINZUGGESPRDOKU"
                 label="Protokoll wurde erstellt und angehangen">
        <option label="nein" name="0"/>
        <option label="ja" name="1" default="true"/>
    </optiongroup>',
t.IntervalMode = 3,
t.BWIKID = 12
WHERE t.BWINFTYP = 'intgesp01';
UPDATE resinfo r set r.Bis = r.Von WHERE r.BWINFTYP = 'intgesp01';
--
UPDATE resinfotype t
SET t.XML = '    <qdvs optional="false"/>

    <optiongroup size="18" name="BEWUSSTSEINSZUSTAND"
                 label="Bewusstseinszustand"
                 tx="Diese Eintragungen werden in den Überleitbogen übFernommen. Seite 2, Abschnitt 11.">
        <option label="wach" name="1" default="true"
                tooltip="Die Person ist ansprechbar und kann an Aktivitäten teilnehmen." tx="setzt ''wach''"/>
        <option label="schläfrig" name="2"
                tooltip="Die Person ist ansprechbar und gut erweckbar, wirkt jedoch müde und ist verlangsamt in seinen Handlungen." tx="setzt ''soporös''"/>
        <option label="somnolent" name="3"
                tooltip="Die Person ist sehr schläfrig und kann nur durch starke äußere Reize geweckt werden (z. B. kräftiges Rütteln an der Schulter oder mehrfaches, sehr lautes Ansprechen)."
                layout="br left"  tx="setzt ''somnolent''"/>
        <option label="komatös" name="4" tooltip="Die Person kann durch äußere Reize nicht mehr geweckt werden."
                tx="setzt ''komatös''"/>
        <option label="wachkoma" name="5" tooltip="Dies trifft nur dann zu, wenn eine ärztliche Diagnose vorliegt."
                tx="setzt ''komatös''"/>
    </optiongroup>'
WHERE t.BWINFTYP = 'bewusst01';
--
UPDATE resinfotype t
SET t.XML = '  <qdvs optional="true"/>
    <bi tooltip="Sobald diese Information eingetragen wurde, geht das System von einer künstlichen Ernährung aus. Formular: 4.4.13"/>
    <tx tooltip="Seite 2, Abschnitt 9."/>

    <combobox label="Sondentyp" name="tubetype">
        <item label="PEG (Perkutane endoskopische Gastrostomie)" name="peg"/>
        <item label="PEG/J (PEG mit duodenalem Schenkel)" name="pej"/>
        <item label="Transnasale Ernährungssonde" name="nose"/>
    </combobox>
    <textfield name="tubesince" label="Sonde gelegt am" length="12" type="date"/>
    <textfield name="tubereason" label="Warum wurde die PEG gelegt ?" hfill="false" length="40" innerlayout="br"/>

    <tabgroup size="12" label="Verabreichung" name="tab1">
        <checkbox label="Ernährungspumpe" name="pump"/>
        <checkbox label="Schwerkraft" name="gravity" layout="left"/>
        <checkbox label="Spritze" name="syringe" layout="left"/>
    </tabgroup>

    <tabgroup size="12" label="Sonstiges" name="tab2">
        <checkbox label="Orale Ernährung zusätzlich" name="oralnutrition"/>
        <checkbox label="Parenterale Ernährung" name="parenteral" layout="left"/>
    </tabgroup>

    <textfield name="calories" label="Kalorien (in 24h)" hfill="false" length="12"/>

    <separator/>
    <optiongroup name="SVERNAEHRUNGUMFANG" label="In welchem Umfang erfolgt eine künstliche Ernährung?" bi="4.4.13">
        <option label="nicht täglich oder nicht dauerhaft" name="0" default="true"/>
        <option label="täglich, aber zusätzlich zur oralen Ernährung" name="6"/>
        <option label="ausschließlich oder nahezu ausschließlich künstliche Ernährung" name="3"/>
    </optiongroup>

    <optiongroup name="SVFREMDHILFE" label="Erfolgt die Bedienung selbständig oder mit Fremdhilfe?" bi="4.4.13">
        <option label="selbständig" name="0" default="true"/>
        <option label="mit Fremdhilfe" name="1"/>
    </optiongroup>'
WHERE t.BWINFTYP = 'kern01';
--
UPDATE resinfotype t
SET t.XML = '<tabgroup size="12" label="Kontrakturen an den oberen Extremitäten" name="tab1">
              <checkbox name="finger.links" label="Finger der linken Hand"/>
              <checkbox name="finger.rechts" label="Finger der rechten Hand" layout="left"/>
              <checkbox name="hand.links" label="linkes Handgelenk" layout="left"/>
              <checkbox name="hand.rechts" label="rechtes Handgelenk" layout="left"/>
              <checkbox name="ebogen.links" label="linkes Ellenbogengelenk" layout="br"/>
              <checkbox name="ebogen.rechts" label="rechtes Ellenbogengelenk" layout="left"/>
              <checkbox name="schulter.links" label="linkes Schultergelenk" layout="left"/>
              <checkbox name="schulter.rechts" label="rechtes Schultergelenk" layout="left"/>
          </tabgroup>

          <tabgroup size="12" label="Kontrakturen an den unteren Extremitäten" name="tab2">
              <checkbox name="zehen.links" label="Zehen des linken Fußes"/>
              <checkbox name="zehen.rechts" label="Zehen des rechten Fußes" layout="left"/>
              <checkbox name="fuss.links" label="linkes Fussgelenk" layout="left"/>
              <checkbox name="fuss.rechts" label="rechtes Fussgelenk" layout="br"/>
              <checkbox name="knie.links" label="linkes Knie" layout="left"/>
              <checkbox name="knie.rechts" label="rechtes Knie" layout="left"/>
              <checkbox name="huefte.links" label="linkes Hüftgelenk" layout="br"/>
              <checkbox name="huefte.rechts" label="rechtes Hüftgelenk" layout="left"/>
              <checkbox name="spitzfuss" label="Spitzfuß" layout="left"/>
          </tabgroup>'
WHERE t.BWINFTYP = 'KONTRAKT';
--
UPDATE resinfotype t
SET t.XML = '  <qdvs optional="false"/>
    <tx tooltip="[b]Seite 1, Abschnitt 3. &quot;Grundpflege&quot;[/b]
            [br/]Die Markierungen im Abschnitt &quot;Grundpflege&quot; werden entsprechenden Ihren Einträgen in diesem Formular gesetzt.
            [br/]Der Bemerkungs-Text wird in die Bemerkungs-Zeile dieses Abschnitts im Überleitbogen übernommen."/>

    <optiongroup size="18" name="SVOBERKOERPER" label="Waschen des vorderen Oberkörpers"
                 tooltip="bi4.oberkoerper.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.oberkoerper.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.oberkoerper.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.oberkoerper.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi4.oberkoerper.selbst3"/>
    </optiongroup>

    <!-- Fehlt im Prüfkonzept und auch in der Begutachtungsrichtlinie - Haben wir wieder dabei geschrieben -->
    <optiongroup size="18" name="SVUNTERKOERPER" label="Waschen des Unterkörpers">
           <option label="selbstständig" name="0" default="true"/>
           <option label="überwiegend selbständig" name="1"/>
           <option label="überwiegend unselbständig" name="2"/>
           <option label="unselbständig" name="3"/>
    </optiongroup>

    <optiongroup size="18" name="SVKOPF" label="Körperpflege im Bereich des Kopfes"
                 tooltip="bi4.kopf.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.kopf.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.kopf.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.kopf.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi4.kopf.selbst3"/>
    </optiongroup>

    <tabgroup size="14" label="Pflege des Kopfes umfasst auch" name="tab1">
        <checkbox name="SVKOPF.kaemmen" label="Kämmen" layout="left"/>
        <checkbox name="SVKOPF.mundpflege" label="Mundpflege" layout="left"/>
        <checkbox name="SVKOPF.rasur" label="Rasieren" layout="left"/>
        <checkbox name="SVKOPF.zahnprothese" label="Zahnprothese" layout="left"/>
    </tabgroup>

    <optiongroup size="18" name="SVINTIMBEREICH" label="Waschen des Intimbereichs"
                 tooltip="bi4.intim.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.intim.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.intim.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.intim.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi4.intim.selbst3"/>
    </optiongroup>

    <optiongroup size="18" name="SVDUSCHENBADEN" label="Duschen und Baden einschließlich Waschen der Haare"
                 tooltip="bi4.baden.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.baden.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.baden.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.baden.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi4.baden.selbst3"/>
    </optiongroup>

    <optiongroup size="18" name="SVANAUSOBERKOERPER" label="An- und Auskleiden des Oberkörpers"
                 tooltip="bi4.okankleiden.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.okankleiden.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.okankleiden.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.okankleiden.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi4.okankleiden.selbst3"/>
    </optiongroup>

    <optiongroup size="18" name="SVANAUSUNTERKOERPER" label="An- und Auskleiden des Unterkörpers"
                tooltip="bi4.ukankleiden.erklaerung">
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

    <!-- ==================Spezielle Mundpflege================== -->
    <tabgroup size="18" label="Gründe für eine spezielle Mundpflege" name="tab2">
        <checkbox name="zahnlosigkeit" label="Zahnlosigkeit" layout="br"/>
        <checkbox name="mundtrockenheit" label="extreme Mundtrockenheit (durch Mundatmung, durch Medikamente)"
                  layout="left"/>
        <checkbox name="trockene.lippen" label="Trockene Lippen (Rhagade)" layout="left"/>
        <checkbox name="zungenbelag" label="Schleimhautbelägen der Zunge" layout="br"/>
        <checkbox name="speichel.dickf" label="dickflüssiger Speichel" layout="left"/>
        <checkbox name="lockere.zaehne" label="lockere Zähne" layout="left"/>
        <checkbox name="laesion.mund" label="Schädigungen des Mundes" layout="left"/>
        <checkbox name="soor" label="Soor (Pilzbefall)" layout="left"/>
        <checkbox name="stomatitis" label="Stomatitis (Mundentzündung)" layout="br"/>
        <checkbox name="gingivitis" label="Gingivitis (Zahnfleischentzündung)" layout="left"/>
        <checkbox name="aphten" label="Erosionen mit entzündlichem Randsaum (Aphten)" layout="left"/>
        <checkbox name="herpes" label="Lippenherpes" layout="br"/>
        <checkbox name="blutung" label="erhöhter Blutungsneigung" layout="left"/>
        <checkbox name="mundflora" label="Zerstörung der physiologischen Mundflora"
                  tooltip="durch Medikamente, Kortison, Zytostatika, Antibiotika" layout="left"/>
        <checkbox name="sauerstoff" label="längerfristiger Sauerstofftherapie (Mund trocknet aus)" layout="br"/>
        <checkbox name="absaugen" label="wiederholte Nasale oder Orale Absaugvorgänge" layout="left"/>
        <checkbox name="bewusstlos" label="Bewußtlosigkeit" layout="left"/>
        <checkbox name="verletzung" label="Bei Verletzungen am Kiefer und in der Mundhöhle" layout="br"/>
        <checkbox name="nahrungskarenz" label="Bei Nahrungskarenz (PEG, Parenterale Ernährung (s.c.))" layout="left"/>
        <checkbox name="schluckstoerung" label="Schluckstörungen" layout="br"/>
        <checkbox name="az" label="reduzierter Allgemeinzustand" layout="left"/>
        <checkbox name="praefinal" label="während des Sterbeprozesses" layout="left"/>
        <checkbox name="immunschwaeche" label="Immunschwäche" layout="left"/>
    </tabgroup>'
WHERE t.BWINFTYP = 'kpflege02';
--
UPDATE resinfotype t
SET t.XML = '<tx tooltip="Diese Eintragungen werden in den Überleitbogen übernommen. Seite 2, Abschnitt 16."/>

          <tabgroup size="12" label="Medikamenteneinnahme" name="tab1">
              <checkbox label="Einnahme selbstständig" name="self"/>
              <checkbox label="Bereitstellen der Tagesration" name="dailyration" layout="left"/>
              <checkbox label="Überwachung der Einnahme" name="control"/>
              <checkbox label="Marcumar-Pass" name="marcumarpass" layout="left"/>
          </tabgroup>

          <optiongroup name="injection" label="Injektion">
              <option label="trifft nicht zu" name="na" default="true"/>
              <option label="selbstständig" name="none"/>
              <option label="mit Anleitung" name="lvl1"/>
              <option label="vollständige Übernahme" name="lvl3"/>
          </optiongroup>'
WHERE t.BWINFTYP = 'MEDS1';
--
UPDATE resinfotype t
SET t.XML = '  <tx tooltip="Diese Eintragungen werden in den Überleitbogen übernommen. Seite 2, Abschnitt 11."/>
    <qdvs optional="false"/>
    <optiongroup name="type_of_dementia" label="Form der Demenz" size="18">
        <option label="Nicht dement" name="none" default="true"/>
        <option label="Primäre Demenz" name="primary"
                tooltip="Bei einer primären Demenz liegen neurodegenerative oder vaskuläre Veränderungen vor. Es wird unterschieden, ob die Nervenzellen des Gehirns degenerieren, also ohne äußerlich erkennbare Ursache untergehen (wie bei der Alzheimer-Krankheit), oder ob sie z.B. wegen Durchblutungsstörungen schwere Schäden erlitten haben (diese Form wird als vaskulärer Demenztyp bezeichnet)."/>
        <option label="Sekundäre Demenz"
                tooltip="Hier ist der geistige Verfall die Folge einer anderen organischen Erkrankung wie einer Hirnverletzung, einer Hirngeschwulst oder einer Herz-Kreislauf-Krankheit; auch Arzneistoffe und Gifte wie Alkohol (Korsakow- Syndrom) oder andere Drogen können dazu führen. Wenn die Grunderkrankung wirksam behandelt wird, Giftstoffe das Gehirn nicht mehr belasten oder Verletzungen geheilt sind, normalisiert sich meist die geistige Leistungsfähigkeit. Oder es ist ein Stillstand des Leidens zu erreichen."
                name="secondary"/>
    </optiongroup>

    <tabgroup size="12" label="Welche Tests wurden durchgeführt ?" name="tab1">
        <checkbox name="cct" label="Uhrentest (Clock Completion Test)"/>
        <checkbox name="demtect" label="DemTect" layout="left"/>
        <checkbox name="tfdd" label="TFDD" tooltip="TFDD - Test zu Früherkennung von Demenzen mit Depressionsabgrenzung"
                  layout="left"/>
        <label fontstyle="bold" label="Bitte ausgefüllte Tests an diese Info anhängen."/>
    </tabgroup>

    <optiongroup name="runaway" label="Weglauftendenz" tx="Verwendung bei Orientierung/Psyche: Weglauftendenz">
        <option label="ja" name="1"/>
        <option label="nein" name="2"  default="true"/>
        <option label="zeitweise" name="3"/>
    </optiongroup>

    <separator/>

    <optiongroup name="KKFERKENNEN" size="18" label="Erkennen von Personen aus dem näheren Umfeld"
                 tooltip="bi2.personen.erklaerung"
                 tx="Verwendung bei Orientierung/Psyche: persönlich, Abbildung wie folgt: vorhanden ==> ja, nicht vorhanden ==> nein, ansonsten zeitweise">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.personen.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.personen.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.personen.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.personen.selbst3"/>
    </optiongroup>

    <optiongroup name="KKFORIENTOERTLICH" size="18" label="Örtliche Orientierung" tooltip="bi2.orte.erklaerung"
                 tx="Verwendung bei Orientierung/Psyche: örtlich, Abbildung wie folgt: vorhanden ==> ja, nicht vorhanden ==> nein, ansonsten zeitweise">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.orte.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.orte.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.orte.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.orte.selbst3"/>
    </optiongroup>

    <optiongroup name="KKFORIENTZEITLICH" size="18" label="Zeitliche Orientierung" tooltip="bi2.zeitlich.erklaerung"
                 tx="Verwendung bei Orientierung/Psyche: zeitlich, Abbildung wie folgt: vorhanden ==> ja, nicht vorhanden ==> nein, ansonsten zeitweise">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.zeitlich.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.zeitlich.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.zeitlich.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.zeitlich.selbst3"/>
    </optiongroup>

    <optiongroup name="KKFERINNERN" size="18" label="Sich Erinnern" tooltip="bi2.erinnern.erklaerung">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.erinnern.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.erinnern.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.erinnern.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.erinnern.selbst3"/>
    </optiongroup>

    <optiongroup name="KKFHANDLUNGEN" size="18" label="Steuern von mehrschrittigen Alltagshandlungen"
                 tooltip="bi2.handlungen.erklaerung">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.handlungen.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.handlungen.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.handlungen.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.handlungen.selbst3"/>
    </optiongroup>

    <optiongroup name="KKFENTSCHEIDUNGEN" size="18" label="Treffen von Entscheidungen im Alltagsleben"
                 tooltip="bi2.entscheidungen.erklaerung">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.entscheidungen.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.entscheidungen.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.entscheidungen.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.entscheidungen.selbst3"/>
    </optiongroup>

    <optiongroup name="KKFVERSTEHENINFO" size="18" label="Verstehen von Sachverhalten und Informationen"
                 tooltip="bi2.verstehen.erklaerung"
                 tx="Verwendung bei Orientierung/Psyche: situativ, Abbildung wie folgt: vorhanden ==> ja, nicht vorhanden ==> nein, ansonsten zeitweise">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.verstehen.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.verstehen.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.verstehen.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.verstehen.selbst3"/>
    </optiongroup>

    <optiongroup name="KKFGEFAHRERKENNEN" size="18" label="Erkennen von Risiken und Gefahren"
                 tooltip="bi2.risiken.erklaerung">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.risiken.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.risiken.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.risiken.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.risiken.selbst3"/>
    </optiongroup>

    <optiongroup name="KKFMITTEILEN" size="18" label="Mitteilen von elementaren Bedürfnissen"
                   tooltip="bi2.beduerfnissen.erklaerung">
          <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.beduerfnissen.selbst0"/>
          <option label="größtenteils vorhanden" name="1" tooltip="bi2.beduerfnissen.selbst1"/>
          <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.beduerfnissen.selbst2"/>
          <option label="nicht vorhanden" name="3" tooltip="bi2.beduerfnissen.selbst3"/>
      </optiongroup>

      <optiongroup name="KKFVERSTEHENAUF" size="18" label="Verstehen von Aufforderungen"
                   tooltip="bi2.aufforderungen.erklaerung">
          <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.aufforderungen.selbst0"/>
          <option label="größtenteils vorhanden" name="1" tooltip="bi2.aufforderungen.selbst1"/>
          <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.aufforderungen.selbst2"/>
          <option label="nicht vorhanden" name="3" tooltip="bi2.aufforderungen.selbst3"/>
      </optiongroup>

      <optiongroup name="KKFBETEILIGUNG" size="18" label="Beteiligung an einem Gespräch"
                   tooltip="bi2.gespraech.erklaerung">
          <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.gespraech.selbst0"/>
          <option label="größtenteils vorhanden" name="1" tooltip="bi2.gespraech.selbst1"/>
          <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.gespraech.selbst2"/>
          <option label="nicht vorhanden" name="3" tooltip="bi2.gespraech.selbst3"/>
      </optiongroup>'
WHERE t.BWINFTYP = 'orient02';
--
UPDATE resinfotype t
SET t.XML = ' <tabgroup size="16" label="Zeichen von Nahrungsmangel" name="tab1">

              <checkbox name="außen" label="Äußerer Eindruck" tooltip="unterernährt / untergewichtig"/>
              <checkbox name="bmi" label="BMI unter 20" tooltip="nur wenn ermittelbar" layout="left"/>
              <checkbox name="unbgew" label="Unbeabsichtigter Gewichtsverlust"
                        tooltip="über 5% in einem Monat, über 10% in 6 Monaten oder weit gewordene Kleidung" layout="left"/>
              <checkbox layout="br" name="geringe" label="Auffällig geringe Essmenge"
                        tooltip="mehr als ein Viertel Essensreste bei zwei Drittel der Mahlzeiten"/>
              <checkbox layout="left" name="bedarfe" label="Erhöhter Energie-/Nährstoffbedarf und Verluste"
                        tooltip="Hyperaktivität, Stresssituation, akute Krankheit, Fieber, offene Wunden wie Dekubitus, Ulcus Cruris, Diarrhö, Erbrechen, Blutverlust"/>
          </tabgroup>

          <tabgroup size="16" label="Risiko für Flüssigkeitsmangel" name="tab2">
              <checkbox layout="br" name="mangelf" label="Zeichen von Flüssigkeitsmangel"
                        tooltip="Plötzlich unerwartete Verwirrtheit, trockene Schleimhäute, konzentrierte Urin"/>
              <checkbox layout="left" name="geringf" label="Auffällig geringe Trinkmenge"
                        tooltip="weniger als 1000 ml/Tag über mehrere Tage"/>
              <checkbox layout="left" name="bedarff" label="Erhöhter Flüssigkeitsbedarf"
                        tooltip="Fieber, stark geheizte Räume, Sommerhitze"/>
          </tabgroup>

          <tabgroup size="16" label="Einschätzungshilfe" name="tab3">
              <textfield name="groesse" type="double" preset="heightlast" label="Körpergröße in m" length="10" layout="br"/>
              <textfield name="gewichta" type="double" preset="weightlast" label="Körpergewicht (aktuell) in kg" length="10"
                         layout="left"/>
              <textfield name="gewicht1" type="double" preset="weight-1m" label="Körpergewicht (vor 1 Monat) in kg"
                         length="10"
                         layout="br"/>
              <textfield name="gewicht6" type="double" preset="weight-6m" label="Körpergewicht (vor 6 Monaten) in kg"
                         length="10"
                         layout="left"/>
              <textfield name="gewicht12" type="double" preset="weight-1y" label="Körpergewicht (vor 1 Jahr) in kg"
                         length="10"
                         layout="br"/>
              <checkbox layout="br" name="oedem" label="Ödeme sichtbar"/>
              <checkbox layout="br" name="zuweit" label="Kleidung (Rock, Hose) zu weit geworden"/>
          </tabgroup>

          <optiongroup fontstyle="bold" name="grobein" label="Grobe äußere Einschätzung">
              <option label="unterernährt" name="unter"/>
              <option layout="left" label="normal ernährt" name="normal" default="true"/>
              <option layout="left" label="überernährt" name="ueber"/>
          </optiongroup>

          <label size="10" color="blue"
                 label="Instrument des Projektverbundes Institut für Pflegewissenschaft, Universität Witten/Herdecke &amp; Institut für Ernährungs- und Lebensmittelwissenschaften, Universität Bonn, 2008"/>'
WHERE t.BWINFTYP = 'PEMUK1';
--
UPDATE resinfotype t
SET t.XML = ' <label size="16" label="Gründe für eine geringe Nahrungsaufnahme" tooltip="warum isst der BW so wenig ?"
                 fontstyle="bold"/>

          <tabgroup size="12" label="Körperlich oder geistig bedingte Beeinträchtigungen" name="tab1">
              <checkbox layout="br" name="c1a" label="Kognitive Überforderung"
                        tooltip="z.B. durch Demenzerkrankung; weiß nichts mit Essen anzufangen, vergisst zu schlucken"/>
              <checkbox layout="left" name="c1b" label="Funktionseinschränkungen der Arme und Hände"
                        tooltip="z.B. Erreichbarkeit von Speisen, kann Besteck nicht greifen, kann nicht schneiden"/>
              <checkbox layout="br" name="c1c" label="Schlechter Zustand des Mundes"
                        tooltip="z.B. Mundtrockenheit, Schleimhautdefekte"/>
              <checkbox layout="left" name="c1d" label="Beeinträchtigung der Kaufunktion/Zahnprobleme"/>
              <checkbox layout="br" name="c1e" label="Schluckstörungen"
                        tooltip="z.B. verschluckt sich leicht, hustet oft beim Essen, vermeidet bestimmte Konsistenz"/>
              <checkbox layout="left" name="c1f" label="Müdigkeit beim Essen"
                        tooltip="z.B. Verdacht auf Medikamentennebenwirkung, veränderter Schlaf-Wachrhythmus"/>
              <checkbox layout="br" name="c1g" label="Beeinträchtigung der Seh- oder Hörfähigkeit"/>
              <checkbox layout="left" name="c1h" label="Andere Gründe/Ursachen"/>
          </tabgroup>

          <tabgroup size="12" label="Fehlende Lust zum Essen, kein Appetit, Ablehnen des Essens" name="tab2">
              <checkbox layout="br" name="c2a" label="Besondere psychische Belastung"
                        tooltip="z.B. Einsamkeit, Depressivität"/>
              <checkbox layout="left" name="c2b" label="Akute Krankheit"/>
              <checkbox layout="left" name="c2c" label="Schmerzen"/>
              <checkbox layout="br" name="c2d" label="Bewegungsmangel"/>
              <checkbox layout="left" name="c2e" label="Verdacht auf Medikamentennebenwirkungen"
                        tooltip="z.B. Art, Anzahl der verschiedenen Präparate"/>
              <checkbox layout="br" name="c2f" label="Auffallend reduzierter Geschmacks- und Geruchssinn"/>
              <checkbox layout="left" name="c2h" label="Kulturelle, religiöse Gründe"/>
              <checkbox layout="br" name="c2g"
                        label="Keine ausreichenden Informationen über Speisen und ihre Zusammensetzung"/>
              <checkbox layout="br" name="c2j" label="Angst vor Unverträglichkeiten oder Allergien"/>
              <checkbox layout="left" name="c2i" label="Individuelle Abneigungen, Vorlieben, Gewohnheiten"/>
              <checkbox layout="br" name="c2k" label="Andere Gründe/Ursachen"/>
          </tabgroup>

          <tabgroup size="12" label="Umgebungsfaktoren" name="tab3">
              <checkbox layout="br" name="c3a" label="Esssituation wird als unangenehm empfunden"
                        tooltip="z.B. Geräusche, Gerüche, Tischnachbarn"/>
              <checkbox layout="left" name="c3b" label="inadäquate Essenszeiten"
                        tooltip="z.B. Zeitpunkt, Dauer, Anpassungsmöglichkeit"/>
              <checkbox layout="br" name="c3c" label="Hilfsmittelangebot"/>
              <checkbox layout="left" name="c3d" label="Beziehung zu den Versorgungspersonen"/>
              <checkbox layout="br" name="c3e" label="Andere Gründe/Ursachen"/>
          </tabgroup>

          <tabgroup size="12" label="Essensangebot" name="tab4">
              <checkbox layout="br" name="c4a" label="Unzufriedenheit mit dem üblichen Angebot"
                        tooltip="z.B. Gewohnheiten, soziale, kulturelle, religiöse Bedürfnisse hinsichtlich Lebensmittelauswahl, Menge, Geschmack, Temperatur, Aussehen"/>
              <checkbox layout="left" name="c4b" label="Unangemessene Konsistenz" tooltip="z.B. hart, weich"/>
              <checkbox layout="br" name="c4c" label="Nicht akzeptierte verordnete Diät (welche ?)"/>
              <checkbox layout="left" name="c4d" label="Verdacht auf inadäquate Diät"/>
              <checkbox layout="br" name="c4e" label="Einschätzung des Angebots"
                        tooltip="Speisenplanung hinsichtlich Abwechslung, Menüzusammenstellung, Angemessenheit etc."/>
              <checkbox layout="left" name="c4f" label="Andere Gründe/Ursachen"/>
          </tabgroup>
          <tabgroup size="12" label="Gründe für einen erhöhten Energie- und Nährstoffbedarf bzw. Verluste" name="tab5">

              <checkbox layout="br" name="c5a" label="Krankheit"
                        tooltip="z.B. Fieber, Infektion, Tumor, offene Wunden, Dekubitus, psychischer Stress, Blutverlust, starkes Erbrechen, anhaltende Durchfälle"/>
              <checkbox layout="left" name="c5b" label="Hyperaktivität"
                        tooltip="z.B. ständiges Umherlaufen, evtl. in Verbindung mit kognitiven Erkrankungen"/>
              <checkbox layout="br" name="c5c" label="Andere Gründe/Ursachen"/>
          </tabgroup>

          <label size="10" color="blue"
                 label="Instrument des Projektverbundes Institut für Pflegewissenschaft, Universität Witten/Herdecke"/>
          <label size="10" color="blue" label="und"/>
          <label size="10" color="blue"
                 label="Institut für Ernährungs- und Lebensmittelwissenschaften, Universität Bonn, 2008"/>'
WHERE t.BWINFTYP = 'PEMULE1';
--
UPDATE resinfotype t
SET t.XML = ' <label size="16" label="Gründe für eine geringe Flüssigkeitsmenge" tooltip="warum trinkt der BW so wenig ?"
                 fontstyle="bold"/>
          <tabgroup size="12" label="Körperlich oder geistig bedingte Beeinträchtigungen" name="tab1">
              <checkbox layout="br" name="c1a" label="Kognitive Überforderung"
                        tooltip="z.B. durch Demenzerkrankung; weiß nichts mit Getränk anzufangen, vergisst zu schlucken"/>
              <checkbox layout="left" name="c1c" label="Schluckstörungen"
                        tooltip="z.B. verschluckt sich leicht, hustet oft beim Trinken, vermeidet bestimmte Konsistenz"/>
              <checkbox layout="br" name="c1b" label="Funktionseinschränkungen der Arme und Hände"
                        tooltip="z.B. Erreichbarkeit von Getränken, kann Tasse/Becher nicht greifen"/>
              <checkbox layout="left" name="c1d" label="Andere Gründe/Ursachen"/>
          </tabgroup>

          <tabgroup size="12" label="Fehlende Lust zum Trinken" name="tab2">
              <checkbox layout="br" name="c2a" label="Schmerzen"/>
              <checkbox layout="left" name="c2d"
                        label="Keine ausreichenden Informationen über Getränke und Ihre Zusammensetzung"/>
              <checkbox layout="br" name="c2b" label="Resuziertes Durstgefühl"/>
              <checkbox layout="left" name="c2c" label="Wunsch nach geringer Urinausscheidung"
                        tooltip="z.B. Angst vor Inkontinenz, häufige Toiiettengänge)"/>
              <checkbox layout="br" name="c2e" label="Kulturelle, religiöse Gründe, Gewohnheiten"/>
              <checkbox layout="left" name="c2f" label="Angst vor Unverträglichkeiten oder Allergien"/>
              <checkbox layout="br" name="c2g" label="Andere Gründe/Ursachen"/>
          </tabgroup>

          <tabgroup size="12" label="Umgebungsfaktoren" name="tab3">
              <checkbox layout="br" name="c3a" label="Hilfsmittelangebot"/>
              <checkbox layout="left" name="c3b" label="Beziehung zu den Versorgungspersonen"/>
              <checkbox layout="left" name="c3c" label="Andere Gründe/Ursachen"/>
          </tabgroup>

          <tabgroup size="12" label="Trinkangebot" name="tab4">
              <checkbox layout="br" name="c4a" label="Allgemeine Unzufriedenheit"
                        tooltip="z.B. nicht beachtete Gewohnheiten, kulturelle Bedürfnisse, Art der Getränke, Menge, Geschmack, Temperatur, Aussehen"/>
              <checkbox layout="left" name="c4b" label="Andere Gründe/Ursachen"/>
          </tabgroup>

          <tabgroup size="12" label="Starkes Schwitzen" name="tab5">
              <checkbox layout="br" name="c5a" label="Hitze" tooltip="z.B. stark geheizte Räume, Sommerhitze"/>
              <checkbox layout="left" name="c5b" label="Unzweckmäßige Kleidung"/>
              <checkbox layout="left" name="c5c" label="Andere Gründe/Ursachen"/>
          </tabgroup>

          <tabgroup size="12" label="Krankheitsbedingter Flüssigkeitsverlust" name="tab6">
              <checkbox layout="br" name="c6a" label="Fieber" tooltip="z.B. stark geheizte Räume, Sommerhitze"/>
              <checkbox layout="left" name="c6b" label="Starkes Erbrechen"/>
              <checkbox layout="left" name="c6c" label="Blutverlust"/>
              <checkbox layout="left" name="c6d" label="Anhaltende Durchfälle (Häufigkeit)"/>
              <checkbox layout="br" name="c6e" label="Medikamente zur Entwässerung oder zum Abführen"/>
              <checkbox layout="left" name="c6f" label="Andere Gründe/Ursachen"/>
          </tabgroup>

          <label size="10" color="blue"
                 label="Instrument des Projektverbundes Institut für Pflegewissenschaft, Universität Witten/Herdecke"/>
          <label size="10" color="blue" label="und"/>
          <label size="10" color="blue"
                 label="Institut für Ernährungs- und Lebensmittelwissenschaften, Universität Bonn, 2008"/>'
WHERE t.BWINFTYP = 'PEMULT1';
--
UPDATE resinfotype t
SET t.XML = '<label size="16" fontstyle="bold" label="Allgemeines"/>
          <gpselect name="gp1" label="Facharzt"/>
          <tx tooltip="Diese Eintragungen werden in den Überleitbogen übernommen. Dadurch wird die Anlage ''PSYCH'' erstellt und beigefügt.[br/]Der Bemerkungstext zu dieser Information wird unter ''Empfehlung/Status'' eingetragen."/>
          <optiongroup name="accomodation" label="Wohnsituation">
              <option label="eigene Wohnung" name="apartment"/>
              <option label="betreutes Wohnen" name="assistedliving" default="true"/>
              <option label="ohne festen Wohnsitz" name="homeless"/>
          </optiongroup>
          <optiongroup name="socialcontacts" label="Sozialkontakte">
              <option label="unterstützend" name="supportive" default="true"/>
              <option label="problematisch" name="problem"/>
              <option label="fehlend" name="missing"/>
          </optiongroup>

          <tabgroup size="12" label="Tätigkeiten" name="tab1">
              <checkbox name="job" label="berufstätig"/>
              <checkbox name="volunteer" label="ehrenamtlich tätig" layout="left"/>
          </tabgroup>

          <separator/>
          <label size="16" fontstyle="bold" label="Psychosoziale Aspekte"/>
          <optiongroup name="aggressive" label="Aggressives Verhalten">
              <option label="ja" name="yes1"/>
              <option label="nein" name="no1" default="true"/>
              <option label="zeitweise" name="intermittent1"/>
          </optiongroup>
          <optiongroup name="selfdestructive" label="Selbstgefährdung">
              <option label="ja" name="yes2"/>
              <option label="nein" default="true" name="no2"/>
              <option label="zeitweise" name="intermittent2"/>
          </optiongroup>
          <optiongroup name="manicdepressive" label="Depression / Manisches Verhalten">
              <option label="ja" name="yes3"/>
              <option label="nein" default="true" name="no3"/>
              <option label="zeitweise" name="intermittent3"/>
          </optiongroup>
          <optiongroup name="delusion" label="Wahn">
              <option label="ja" name="yes4"/>
              <option label="nein" default="true" name="no4"/>
              <option label="zeitweise" name="intermittent4"/>
          </optiongroup>
          <optiongroup name="hallucination" label="Halluzinationen / Wahrnehmungsstörungen">
              <option label="ja" name="yes5"/>
              <option label="nein" default="true" name="no5"/>
              <option label="zeitweise" name="intermittent5"/>
          </optiongroup>
          <optiongroup name="passive" label="Antriebsminderung">
              <option label="ja" name="yes6"/>
              <option label="nein" default="true" name="no6"/>
              <option label="zeitweise" name="intermittent6"/>
          </optiongroup>
          <optiongroup name="restless" label="Umtriebigkeit (Psychomotorische Unruhe)">
              <option label="ja" name="yes7"/>
              <option label="nein" default="true" name="no7"/>
              <option label="zeitweise" name="intermittent7"/>
          </optiongroup>
          <optiongroup name="regressive" label="Regressives Verhalten" tooltip="[h2]Symptome regressiven Verhaltens[/h2]
      [ol]
      [li][b]Resignation:[/b] PatientInnen zeigen keinen eigenen Willen, sind völlig unkritisch, übernehmen keine Verantwortung für sich selbst sondern übertragen sie auf die Pflegekräfte, lassen sich hängen, sind extrem anhänglich und völlig angepasst. Im Allgemeinen sind dies für den Krankenhausalltag „gute“ PatientInnen.[/li]
      [li][b]oberflächliche Anpassung:[/b] scheinbar lassen sich die PatientInnen auf alles ein, heimlich jedoch rauchen sie oder werfen Medikamente weg. Sie nässen/koten ein.[/li]
      [li][b]Protest:[/b] PatientInnen beschweren sich, sind rebellisch, weisen auf Fehler hin und wissen alles besser. Diese PatientInnen ziehen oftmals den Ärger des Pflegepersonals auf sich.[/li]
      [/ol]">
              <option label="ja" name="yes8"/>
              <option label="nein" default="true" name="no8"/>
              <option label="zeitweise" name="intermittent8"/>
          </optiongroup>
          <optiongroup name="faecal" label="Kotschmieren / Kotessen">
              <option label="ja" name="yes9"/>
              <option label="nein" default="true" name="no9"/>
              <option label="zeitweise" name="intermittent9"/>
          </optiongroup>
          <optiongroup name="apraxia" label="Apraxie"
                       tooltip="Als Apraxie (gr. „Untätigkeit“) bezeichnet man eine Störung der Ausführung willkürlicher zielgerichteter und geordneter Bewegungen bei intakter motorischer Funktion.">
              <option label="ja" name="yes10"/>
              <option label="nein" default="true" name="no10"/>
              <option label="zeitweise" name="intermittent10"/>
          </optiongroup>
          <optiongroup name="agnosia" label="Agnosie"
                       tooltip="Die Agnosie ist ein relativ seltenes neuropsychologisches Symptom, das nach bi- oder unilateralen (sub)kortikalen Läsionen auftritt. Es wird definiert als eine Störung des Erkennens, ohne dass elementare sensorische Defizite, kognitive Ausfälle, Aufmerksamkeitsstörungen, aphasische Benennstörungen oder die Unkenntnis des zu erkennenden Stimulus vorliegt.">
              <option label="ja" name="yes11"/>
              <option label="nein" default="true" name="no11"/>
              <option label="zeitweise" name="intermittent11"/>
          </optiongroup>
          <optiongroup name="fear" label="Angst">
              <option label="ja" name="yes12"/>
              <option label="nein" default="true" name="no12"/>
              <option label="zeitweise" name="intermittent12"/>
          </optiongroup>
          <textfield length="30" name="feartext" label="Angst wovor"/>

          <separator/>
          <label size="16" fontstyle="bold" label="Sucht"/>
          <checkbox label="aktueller Konsum" name="consuming"/>
          <optiongroup name="treatmentsymptoms" label="Entzugssymptome">
              <option label="ja" name="yes13"/>
              <option label="nein" default="true" name="no13"/>
              <option label="zeitweise" name="intermittent13"/>
          </optiongroup>

          <tabgroup size="12" label="Suchtart" name="tab2">
              <checkbox name="alcohol" label="Alkohol"/>
              <checkbox name="drugs" label="Drogen" layout="left"/>
              <checkbox name="meds" label="Medikamente" layout="left"/>
              <checkbox name="nicotin" label="Nikotin" layout="left"/>
              <checkbox name="gambling" label="Spielsucht" layout="left"/>
              <textfield name="otheraddiction" length="30" label="sonstiges"/>
          </tabgroup>

          <textfield length="30" name="substitution" label="Substitution"/>
          <textfield length="30" name="substlocation" label="Vergabestelle"/>
          <textfield length="30" name="substcontact" label="Ansprechpartner"/>'
WHERE t.BWINFTYP = 'PSYCH1';
--
UPDATE resinfotype t
SET t.XML = '  <qdvs optional="false"/>

    <optiongroup size="18" name="GARUHENSCHLAFEN"
                 label="Ruhen und Schlafen" bi="4.6.2" tooltip="bi6.schlafen.erklaerung" tx="Seite 1, Abschnitt 8">
        <option label="selbstständig" name="0" default="true" tooltip="bi6.schlafen.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi6.schlafen.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi6.schlafen.selbst2" layout="br left"/>
        <option label="unselbständig" name="3" tooltip="bi6.schlafen.selbst3"/>
    </optiongroup>

    <tabgroup size="18" label="Schlafeigenschaften" name="tab1">
        <checkbox name="normal" label="normal"/>
        <checkbox name="einschlaf" label="Einschlafstörungen" layout="left"/>
        <checkbox name="durchschlaf" label="Durchschlafstörungen" layout="left"/>
        <checkbox name="unruhe" label="nächtliche Unruhe"/>
        <checkbox name="daynight" label="Tag-/Nachtrhythmus gestört" layout="left"/>
    </tabgroup>

    <tabgroup size="18" label="Schlaflage" name="tab2">
        <checkbox name="left" label="links"/>
        <checkbox name="right" label="rechts" layout="left"/>
        <checkbox name="front" label="Bauchlage" layout="left"/>
        <checkbox name="back" label="Rückenlage" layout="left"/>
    </tabgroup>
    <textfield name="schlafhilfen" innerlayout="br" label="Welche Schlafhilfen oder Gewohnheiten sind bekannt"/>'
WHERE t.BWINFTYP = 'schlaf02';
--
alter table homes
    drop column erhebungszeitraum,
    drop column auswertungszeitraum;
--
UPDATE resinfotype t
SET t.XML = ' <qdvs optional="false"/>

    <tabgroup size="18" fontstyle="bold" label="Hauptbezugsperson" name="hbp">
        <tx tooltip="Diese Angaben werden auf Seite 1, Abschnitt 1 eingetragen."/>
        <textfield label="Anrede" name="c1title" length="25" hfill="false" innerlayout="tab"/>
        <textfield label="Name" name="c1name" length="25" hfill="false" innerlayout="tab"/>
        <textfield label="Vorname" name="c1firstname" length="25" hfill="false" innerlayout="tab"/>
        <textfield label="Strasse" name="c1street" length="25" hfill="false" innerlayout="tab"/>
        <textfield label="PLZ" name="c1zip" length="25" hfill="false" innerlayout="tab"/>
        <textfield label="Ort" name="c1city" length="25" hfill="false" innerlayout="tab"/>
        <textfield label="Tel" name="c1tel" length="25" hfill="false" innerlayout="tab"/>
        <textfield label="Mobil" name="c1mobile" length="25" hfill="false" innerlayout="tab"/>
        <textfield label="E-Mail" name="c1email" length="25" hfill="false" innerlayout="tab"/>
        <checkbox name="c1ready2nurse" label="Pflegebereitschaft der Bezugsperson"
                  tx="Setzt ''Pflegebereitschaft der Bezugsperson'' auf ''ja'', falls angeklickt."/>
    </tabgroup>

    <tabgroup size="18" fontstyle="bold" label="Angehörige 1" name="a1">
        <textfield name="name1" label="Name" length="25" hfill="false" innerlayout="tab"/>
        <textfield name="ans1" label="Anschrift" length="25" hfill="false" innerlayout="tab"/>
        <textfield name="tel1" label="Telefon" length="25" hfill="false" innerlayout="tab"/>
    </tabgroup>

    <tabgroup size="18" fontstyle="bold" label="Angehörige 2" name="a2">
        <textfield name="name2" label="Name" length="25" hfill="false" innerlayout="tab"/>
        <textfield name="ans2" label="Anschrift" length="25" hfill="false" innerlayout="tab"/>
        <textfield name="tel2" label="Telefon" length="25" hfill="false" innerlayout="tab"/>
    </tabgroup>

    <optiongroup size="18" name="GAINTERAKTION"
                 label="Interaktion mit Personen im direkten Kontakt"
                 bi="4.6.5" tooltip="bi6.interaktion.erklaerung" tx="Seite 1, Abschnitt 8">
        <option label="selbstständig" name="0" default="true" tooltip="bi6.interaktion.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi6.interaktion.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi6.interaktion.selbst2" layout="br left"/>
        <option label="unselbständig" name="3" tooltip="bi6.interaktion.selbst3"/>
    </optiongroup>

    <optiongroup size="18" name="GAKONTAKTPFLEGE"
                 label="Kontaktpflege zu Personen außerhalb des direkten Umfelds"
                 bi="4.6.6" tooltip="bi6.kontaktpflege.erklaerung" tx="Seite 1, Abschnitt 8">
        <option label="selbstständig" name="0" default="true" tooltip="bi6.kontaktpflege.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi6.beschaeftigen.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi6.kontaktpflege.selbst2" layout="br left"/>
        <option label="unselbständig" name="3" tooltip="bi6.kontaktpflege.selbst3"/>
    </optiongroup>'
WHERE t.BWINFTYP = 'sozial01';
UPDATE resinfotype t
SET t.XML = '  <tx tooltip="[b]Seite 1, Abschnitt 4.[/b][br/]Alles was Sie hier als Bemerkung eintragen, steht hinterher in der Bemerkungs-Zeile dieses Abschnitts im Überleitbogen.[br/][b]Lagerungsarten[/b] werden anhand der Pflegeplanungen bestimmt."/>
    <qdvs optional="false"/>
    <checkbox name="bedridden" label="bettlägerig"/>
    <optiongroup size="18" name="MOBILPOSWECHSEL" label="Positionswechsel im Bett"
                 tooltip="bi1.bett.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi1.bett.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi1.bett.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi1.bett.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi1.bett.selbst3"/>
    </optiongroup>
    <textfield label="Hilfsmittel" name="MOBILPOSWECHSEL.hilfsmittel" innerlayout="left"/>

    <optiongroup size="18" name="MOBILSITZPOSITION" label="Halten einer stabilen Sitzposition"
                 tooltip="bi1.sitz.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi1.sitz.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi1.sitz.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi1.sitz.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi1.sitz.selbst3"/>
    </optiongroup>
    <textfield label="Hilfsmittel" name="MOBILSITZPOSITION.hilfsmittel" innerlayout="left"/>

    <optiongroup size="18" name="MOBILUMSETZEN" label="Umsetzen" tooltip="bi1.umsetzen.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi1.umsetzen.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi1.umsetzen.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi1.umsetzen.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi1.umsetzen.selbst3"/>
    </optiongroup>
    <textfield label="Hilfsmittel" name="MOBILUMSETZEN.hilfsmittel" innerlayout="left"/>

    <optiongroup size="18" name="MOBILFORTBEWEGUNG" label="Fortbewegen innerhalb des Wohnbereichs"
                 tooltip="bi1.wohnbereich.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi1.wohnbereich.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi1.wohnbereich.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi1.wohnbereich.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi1.wohnbereich.selbst3"/>
    </optiongroup>
    <tabgroup label="Hilfsmittel zur Bewegung" name="hilfsmittel">
        <checkbox name="rollstuhl" label="Rollstuhl"/>
        <checkbox name="kruecke" label="Unterarmgehstütze"/>
        <checkbox name="rollator" label="Rollator"/>
        <checkbox name="gehstock" label="Gehstock"/>
    </tabgroup>

    <optiongroup size="18" name="MOBILTREPPENSTEIGEN" label="Treppensteigen" tooltip="bi1.treppen.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi1.treppen.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi1.treppen.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi1.treppen.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi1.treppen.selbst3"/>
    </optiongroup>

    <checkbox name="unfaegig-arme-beine" label="Gebrauchsunfähigkeit beider Arme und beider Beine"
              tooltip="bi1.unfaehig.arme.beine"/>'
WHERE t.BWINFTYP LIKE 'mobil02';
--
UPDATE resinfotype t
SET t.XML = ' <qdvs optional="false"/>
    <roomselect name="room" label="Zimmer"/>'
WHERE t.BWINFTYP LIKE 'room1';
--
UPDATE resinfotype t
SET t.XML = '    <qdvs optional="false"/>
    <tx tooltip="Diese Eintragungen werden in den Überleitbogen übernommen. Seite 2, Abschnitt 12."/>

    <tabgroup size="18" fontstyle="bold" label="Atmung" name="atmung">
        <checkbox label="unauffällig" name="normal"/>
        <checkbox label="kardialer Stau" name="cardcongest" layout="left"/>
        <checkbox label="Schmerzen" name="pain" layout="left"/>
        <checkbox label="Husten" name="cough" layout="left"/>
        <checkbox label="Verschleimung" name="mucous" layout="br"/>
        <checkbox label="Auswurf" name="sputum" layout="left"/>
        <checkbox label="Rauchen" name="smoking" layout="left"/>
        <checkbox label="Asthma" name="asthma" layout="left"/>
        <textfield name="other" label="Sonstiges" length="20"/>
    </tabgroup>

    <tabgroup size="18" fontstyle="bold" label="Besonderheiten" name="besonderheiten">
        <checkbox label="Tracheostoma" name="stoma"/>
        <checkbox label="Silberkanüle" name="silver" layout="left"/>
        <checkbox label="Silikonkanüle" name="silicon" layout="left"/>
        <checkbox label="Absaugen" name="aspirate" layout="left"/>
        <textfield name="tubetype" label="Kanülenart" length="10"/>
        <textfield name="tubesize" label="Kanülengröße" length="10" layout="left"/>
    </tabgroup>

    <optiongroup size="18" name="BEATMUNG" label="Wird der:die Bewohner:in beatmet ?"
                 tooltip="Die Antwortmöglichkeit „invasive Beatmung“ trifft zu, wenn die Beatmung durch eine Trachealkanüle erfolgt. Ansonsten ist „nicht invasiv“ anzukreuzen.">
        <option label="nein" default="true" name="0"/>
        <option label="ja, invasive Beatmung" name="1"/>
        <option label="ja, aber nicht invasiv" name="2"/>
    </optiongroup>'
WHERE t.BWINFTYP LIKE 'respirat2';
--
INSERT INTO `resinfotype` (`BWINFTYP`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`, `IntervalMode`, `equiv`, `XML`)
VALUES ('fallprot02', 'Sturzprotokoll', '', '3', '30', '3',
        '2',
        ' <qdvs optional="true"/>
    <label layout="br left hfill" size="20" fontstyle="bold" color="yellow" bgcolor="blue"
           label="1. Grundlegende Angaben"/>

    <tabgroup size="16" label="Datum und Uhrzeit des Sturzes" name="date1">
        <textfield label="Datum" length="12" name="falldate" type="date" preset="currentdate"/>
        <textfield label="Uhrzeit" length="12" name="falltime" type="time" layout="left" preset="currenttime"/>
    </tabgroup>
    <tabgroup size="16"
              label="letzter Zeitpunkt, wann die Person vor dem Sturz gesehen wurde."
              name="date2">
        <textfield label="Datum" length="12" name="b4falldate" type="date" preset="currentdate"/>
        <textfield label="Uhrzeit" length="12" name="b4falltime" type="time" layout="left" preset="currenttime"/>
        <textfield name="textb4fall" label="Aktivitäten des Bewohners unmittelbar vor dem Sturz" innerlayout="br"
                   hfill="false"/>
    </tabgroup>

    <label layout="br left hfill" size="20" fontstyle="bold" color="yellow" bgcolor="blue"
           label="2. Beschreibung des Sturzereignisses"/>
    <optiongroup label="Sturzort" name="sturzort">
        <option label="BW-Zimmer" name="room" default="true"/>
        <option label="BW Badezimmer" name="bathroom"/>
        <option label="Gemeinschafts-Badezimmer" name="commbathroom"/>
        <option label="Flur" name="hallway"/>
        <option label="Gemeinschaftsraum" name="livingroom" layout="br"/>
        <option label="Außerhalb des Hauses" name="outside"/>
        <option label="Sonstiges (siehe unten)" name="other1"/>
    </optiongroup>
    <textfield name="othertext1" label="Sonstiges oder Erläuterung" hfill="false"/>

    <optiongroup label="Wie aufgefunden" name="aufgefunden">
        <option label="auf dem Bauch liegend" name="bauch" default="true"/>
        <option label="auf dem Rücken liegend" name="ruecken"/>
        <option label="sitzend" name="sitzend"/>
        <option label="kniend/kriechend" name="knie"/>
        <option label="Sonstiges" name="sonst" layout="br"/>
    </optiongroup>
    <textfield name="aufgefundentext" default="" label="Beschreiben Sie, wie der BW aufgefunden wurde." length="30"
               hfill="true"
               layout="br left" depends-on="aufgefunden" visible-when-dependency-eq="sonst"
                      default-value-when-shown=""/>

    <optiongroup label="Körperstellung unmittelbar vor dem Sturz" name="b4fall">
        <option label="Liegen" name="lying"/>
        <option label="Sitzen" name="sitting"/>
        <option label="Aufstehen/Hinsetzen vom/aufs Bett" name="getupsitdown1"/>
        <option label="Aufstehen/Hinsetzen vom/auf Sessel/Stuhl" name="getupsitdown2" layout="br"/>
        <option label="Aufstehen/Hinsetzen vom/auf Rollstuhl" name="getupsitdown3"/>
        <option label="Aufstehen/Hinsetzen vom/auf Toilettenstuhl" name="getupsitdown4" layout="br"/>
        <option label="Aufstehen/Hinsetzen (andere, bitte unten beschreiben)" name="getupsitdown5"/>
        <option label="Stehen" name="standing" layout="br"/>
        <option label="Gehen" name="walking" default="true"/>
    </optiongroup>
    <textfield name="othertext2" label="Andere" hfill="false"/>

    <optiongroup label="Hilfsmittel" name="aux">
        <option label="keine Hilfsmittel" name="none" default="true"/>
        <option label="einseitige Gehhilfe" name="onesided"/>
        <option label="beidseitige Gehhilfe" name="twosided"/>
        <option label="Handstock" name="cane" layout="br"/>
        <option label="Rollator" name="rollator"/>
        <option label="Gehwagen (KG)" name="walker"/>
        <option label="Andere, bitte unten beschreiben" name="other3"/>
    </optiongroup>
    <textfield name="othertext3" label="Andere" hfill="false"/>

    <optiongroup label="Hindernisse/Umgebungsfaktoren" name="evironment"
                 tooltip="Bedingungen, die durch die Umgebung vorgegeben sind, beeinflussen in ganz erheblichem Maße die Sturzgefährdung. Gerade im Begründungszusammenhang, warum es zu einem Sturzereignis gekommen ist, sollten Aspekte, die im Sturzumfeld liegen unbedingt beschrieben werden. Dies kann z.B. eine mangelhafte Beleuchtung, eine im Weg stehende Reisetasche, ein unebener oder besonders glatter Boden oder die Verwendung von nicht geeignetem Schuhwerk sein.">
        <option label="keine" name="none" default="true"/>
        <option label="Boden rutschig" name="slippery"/>
        <option label="Hindernis im Weg" name="obstacle"/>
        <option label="Beleuchtung unzureichend" name="badlight" layout="br"/>
        <option label="Andere, bitte unten beschreiben" name="other4"/>
    </optiongroup>
    <textfield name="othertext4" label="Andere" hfill="false"/>

    <label layout="br left hfill" size="20" fontstyle="bold" color="yellow" bgcolor="blue"
           label="3. Beschreibung der Sturzfolgen"/>
    <tabgroup label="Verletzungfolgen" name="consequences" hfill="false">
        <checkbox label="Platzwunde" name="laceration" layout="br"/>
        <checkbox label="Schürfwunde" name="graze" layout="left"/>
        <checkbox label="Hämatome" name="bruise" layout="left"/>
        <checkbox label="Fraktur" name="fracture" layout="left"/>
        <checkbox label="Schmerzen" name="pain" layout="left"/>
    </tabgroup>
    <textfield name="othertext5" label="Andere" hfill="false"/>
    <textfield name="fear" label="Sturzangst" hfill="false"
               tooltip="zukünftige präventive Maßnahmen hängen von der Sturzangst des Patienten ab, da das Sturzereignis im Gedächtnis des Patienten haften bleibt und wissenschaftlich nachgewiesen ist, dass es einen negativen Einfluss auf ein mögliches weiteres Sturzgeschehen hat"/>

    <label layout="br left hfill" size="20" fontstyle="bold" color="yellow" bgcolor="blue"
           label="4. Massnahmen nach dem Sturz"/>
    <tabgroup name="massnahmen" label="Eingeleitete Maßnahmen" hfill="false">
        <checkbox label="Wundverband" name="wound"/>
        <checkbox label="Lagerung / Kühlung" name="wound"/>
        <checkbox label="pflegerische Unterstützung" name="pflegerisch"/>
        <checkbox label="Arztkontakt" name="gp"/>
        <checkbox label="Krankenhauseinweisung" name="hospital"/>
    </tabgroup>
    <textfield name="othertext6" label="Anmerkungen zu den Massnahmen" hfill="false"/>

    <url label="Das neue Formular ''Sturzprotokoll'' (Uni Bonn, siehe OPDE Quellen UNIBONN2008-01)"
         link="https://www.offene-pflege.de/de/sources-de"/>
');
--
-- Interpretationsfehler bei der Schmerzauswertung. Schmerzfrei durch Medikamente erst bei einer NRS größer 0 nicht gleich 0.
UPDATE resinfotype t
SET t.XML = '<qdvs optional="true"/>
    <label size="16" label="Allgemeine Einschätzung" color="blue"/>
    <combobox label="Schmerzintensität (Numerische Rating Skala)" name="schmerzint">
        <item label="0 - kein Schmerz" name="0"/>
        <item label="1 - kaum Schmerzen" name="1"/>
        <item label="2 - kaum Schmerzen" name="2"/>
        <item label="3 - erträgliche Schmerzen" name="3"/>
        <item label="4 - erträgliche Schmerzen" name="4"/>
        <item label="5 - stärkere Schmerzen" name="5"/>
        <item label="6 - starke Schmerzen" name="6"/>
        <item label="7 - starke Schmerzen" name="7"/>
        <item label="8 - sehr starke Schmerzen" name="8"/>
        <item label="9 - fast unerträgliche Schmerzen" name="9"/>
        <item label="10 - unerträgliche Schmerzen" name="10"/>
    </combobox>
    <checkbox name="schmerzfrei" label="Schmerzfrei durch Medikamente" layout="br left" depends-on="schmerzint"
              visible-when-dependency-neq="0" default-value-when-shown="false" size="14"/>
    <label label="Akutschmerz i.d.R. weniger als 3 Monate, Chronischer Schmerz zwischen 6 Wochen und 3 Monaten, oder länger"
           depends-on="schmerzint" visible-when-dependency-neq="0" size="14" fontstyle="bold"/>
    <combobox label="Schmerztyp" name="schmerztyp"
              tooltip="[h1]Akuter Schmerz, „sinnvoller Schmerz“[/h1] [p] Der akute Schmerz gilt als Alarmzeichen des Körpers. Schon die alten Griechen nannten den Schmerz den „bellenden Wächter der Gesundheit“ (Hypokrates). Der akute Schmerz macht uns aufmerksam, dass etwas nicht stimmt und ist zeitlich begrenzt. Ist die Ursache behoben verschwindet der Schmerz meistens wieder. Wenn wir wissen warum wir Schmerzen haben (z.B. den Fuss verstaucht), können wir den Schmerz auch eher akzeptieren. Hier spielt die individuelle Wahrnehmung und das Erlernte „umgehen mit dem Schmerz“ eine wichtige Rolle. [/p] [p] Akuter Schmerz ist ein plötzlich auftretender und nur kurze Zeit andauern der Schmerz. Er wird als existentielle Erfahrung wahrgenommen, die eine lebenserhaltende Alarm- und Schutzfunktion einnimmt. Akuter Schmerz steht in einem offensichtlichen und direkten Zusammenhang mit einer Gewebe oder Organschädigung, also einer körperlichen Ursache. Nonverbale und verbale Signale, die wir im akuten Schmerz aussenden, verursachen unwillkürlich Empathie und das Beduürfnis für Abhilfe zu sorgen. Akuter Schmerz geht mit physiologischen Begleiterscheinungen einher, wie einem Anstieg des Blutdrucks, des Pulses, Schweißausbrüchen und Anstieg der Atemfrequenz. Insbesondere diese Begleiterscheinungen, die in der akuten Versorgungssituation unmittelbar erkennbar sind, zeigt der Mensch mit ausschließlich chronischen Schmerzen nicht. [/p]  [h1]Chronischer Schmerz, „sinnloser Schmerz“[/h1] [p] Der chronische Schmerz hat an sich keine Warnfunktion mehr. Seine Ursache ist nicht (mehr) ausschaltbar, er nimmt dem Menschen sinnlos die Kraft weg und zehrt allmählich seinen Lebensmut auf. Wenn die Tage zur Qual werden, erschöpft sich die Tragfähigkeit, der Leidende wünscht nur mehr ein Ende herbei, unter Umständen sogar um den Preis seines Lebens, denn es genügt nicht nur am Leben zu sein, man muss auch sein Leben haben. Der chronische Schmerz kann zur eigenständigen Schmerzkrankheit werden, der alle Ebenen des Menschseins beeinflusst und beeinträchtigt. Man spricht dann von „total pain“. Dieser Schmerz ist oft losgelöst von der ursprünglichen Krankheit. Gerade wenn die Ursache unbekannt ist, kann die Chronifizierung schnell eintreten. [/p] [p] Der Übergang zwischen akutem und chronischem Schmerz verläuft kontinuierlich. Gleichwohl werden verschiedene Zeiträume angenommen, ab wann ein Schmerz als chronischer, oder anhaltender Schmerz zu betrachten ist. Je nach Lokalisation des Schmerzes wird hierbei von mehr als 6 Wochen bis hin zu 3 Monaten ausgegangen. In erster Linie wird die Entstehung des chronischen Schmerzes durch drei grundlegende Elemente beschrieben: [/p] [ul] [li]Es handelt sich um einen Entstehungsprozess, der durch ein Zusammenwirken von krankheitsbedingten und psychosozialen Prozessen gekennzeichnet ist.[/li] [li]Chronischer Schmerz ist Schmerz, der über einen Punkt, an dem die Heilung abgeschlossen sein sollte hinaus, anhält oder weiter auftritt. Chronischer Schmerz kann häufig nicht (mehr) mit einem Gewebeschaden oder einer Verletzung in Verbindung gebracht werden.[/li] [li]Der Chronifizierung akuter Schmerzen kann durch angemessene Therapie des akuten Schmerzes entgegengewirkt werden. Eine frühzeitige Linderung von akutem Schmerz kann eine Entwicklung von chronischen Schmerzen verhindern. Bestimmte operative Verfahren, z. B. Amputationen, Mastektomien oder Thorakotomien bewirken häufig chronische Schmerzen.[/li] [/ul]"
              depends-on="schmerzint" visible-when-dependency-neq="0" default-value-when-shown="0">
        <item label="akute Schmerzen" name="0"/>
        <item label="chronische Schmerzen" name="1"/>
    </combobox>
    <textfield name="schmerzort" label="Wo tritt der Schmerz auf ?" depends-on="schmerzint"
               visible-when-dependency-neq="0"/>
    <textfield name="schmerzart" label="Beschreibung der Schmerzart"
               tooltip="Beispiele für Schmerzarten: dumpf, pulsierend, nagelnd, schießend, brennen, steched, bohrend, ausstrahlend"
               depends-on="schmerzint" visible-when-dependency-neq="0"/>
    <textfield name="lindernd" label="Lindernde Faktoren" depends-on="schmerzint" visible-when-dependency-neq="0"/>
    <textfield name="verstaerkend" label="Verstärkende Faktoren" depends-on="schmerzint"
               visible-when-dependency-neq="0"/>
    <label size="16" label="Folgen für Lebensalltag" color="blue"/>
    <combobox label="Stuhlgang" name="stuhl">
        <item label="unabhängig vom Schmerz" name="0"/>
        <item label="normal" name="1"/>
        <item label="schlechter" name="2"/>
    </combobox>
    <combobox label="Schlaf" name="schlaf">
        <item label="unabhängig vom Schmerz" name="0"/>
        <item label="normal" name="1"/>
        <item label="schlechter" name="2"/>
    </combobox>
    <combobox label="Wohlbefinden" name="wohlb">
        <item label="unabhängig vom Schmerz" name="0"/>
        <item label="normal" name="1"/>
        <item label="schlechter" name="2"/>
    </combobox>
    <combobox label="Beeinträchtigung der Tagesaktivität durch Schmerzen" name="tagaktiv">
        <item label="unabhängig vom Schmerz" name="0"/>
        <item label="leicht" name="1"/>
        <item label="mittel" name="2"/>
        <item label="stark" name="3"/>
    </combobox>'
WHERE t.BWINFTYP LIKE 'schmerze2';
--
SET @wundxml = '  <tx tooltip="Wunden werden auf dem Überleitbogen auf Seite 2 Abschnitt 19 eingetragen. Ebenso wirken sich die Eintragunegn auf den Abschnitt 10 aus (Wundschmerz, Wunden)"/>
    <qdvs optional="true"/>
    <label
            label="Sie können mehr als eine Stelle markieren, aber beschreiben Sie unbedingt nur *eine* Wunde pro Formular."
            size="16" fontstyle="bold"/>
    <bodyscheme name="bs1"/>

    <checkbox name="dekubitus" label="Diese Wunde ist ein Druckgeschwür (Dekubitus)"
              tx="Erscheint auf dem Überleitbogen auf Seite 1 Abschnitt 7."
              lockedforchanges="true" default="true"/>

    <combobox label="Kategorien nach EPUAP" name="epuap"
              tooltip="Denken Sie bitte daran, dass auch im weiteren Heilungs-Verlauf die anfängliche Dekubitus-Kategorie [b]nicht mehr geändert[/b] wird. Sie bleibt bis zur Epithalisierung bestehen. Ein Dekubitus entwickelt sich also nicht rückwärts."
              lockedforchanges="false" depends-on="dekubitus" visible-when-dependency-eq="true"
              default-value-when-shown="1">
        <item label="Kategorie I: Nicht wegdrückbare Rötung" name="1"
              tooltip="Nicht wegdrückbare, umschriebene Rötung bei intakter Haut, gewöhnlich über einem knöchernen Vorsprung. Bei dunkel pigmentierter Haut ist ein Verblassen möglicherweise nicht sichtbar, die Farbe kann sich aber von der umgebenden Haut unterscheiden. Der Bereich kann schmerzempfindlich, verhärtet, weich, wärmer oder kälter sein als das umgebende Gewebe. Diese Symptome können auf eine (Dekubitus-) Gefährdung hinweisen."/>
        <item label="Kategorie II: Teilverlust der Haut" name="2"
              tooltip="Teilzerstörung der Haut (bis zur Dermis), die als flaches, offenes Ulcus mit einem rot bis rosafarbenen Wundbett ohne Beläge in Erscheinung tritt. Kann sich auch als intakte oder offene/rupturierte, serumgefüllte Blase darstellen. Manifestiert sich als glänzendes oder trockenes, flaches Ulcus ohne nekrotisches Gewebe oder Bluterguss10. Diese Kategorie sollte nicht benutzt werden um Skin Tears (Gewebezerreißungen), Verbands- oder pflasterbedingte Hautschädigungen, feuchtigkeitsbedingte Läsionen, Mazerationen oder Abschürfungen zu beschreiben."/>
        <item label="Kategorie III: Verlust der Haut" name="3"
              tooltip="Zerstörung aller Hautschichten. Subkutanes Fett kann sichtbar sein, jedoch keine Knochen, Muskeln oder Sehnen. Es kann ein Belag vorliegen, der jedoch nicht die Tiefe der Gewebsschädigung verschleiert. Es können Tunnel oder Unterminierungen vorliegen. Die Tiefe des Dekubitus der Kategorie III variiert je nach anatomischer Lokalisation. Der Nasenrücken, das Ohr, der Hinterkopf und das Gehörknöchelchen haben kein subkutanes Gewebe, daher können Kategorie III Wunden dort auch sehr oberflächlich sein. Im Gegensatz dazu können an besonders adipösen Körperstellen extrem tiefe Kategorie III Wunden auftreten. Knochen und Sehnen sind nicht sichtbar oder tastbar."/>
        <item label="Kategorie IV: vollständiger Haut oder Gewebeverlust" name="4"
              tooltip="Totaler Gewebsverlust mit freiliegenden Knochen, Sehnen oder Muskeln. Belag und Schorf können vorliegen. Tunnel oder Unterminierungen liegen oft vor. Die Tiefe des Kategorie IV Dekubitus hängt von der anatomischen Lokalisation ab. Der Nasenrücken, das Ohr, der Hinterkopf und der Knochenvorsprung am Fußknöchel haben kein subkutanes Gewebe, daher können Wunden dort auch sehr oberflächlich sein. Kategorie IV Wunden können sich in Muskeln oder unterstützende Strukturen ausbreiten (Fascien, Sehnen oder Gelenkkapseln) und können dabei leicht Osteomyelitis oder Ostitis verursachen. Knochen und Sehnen sind sichtbar oder tastbar."/>
    </combobox>

    <combobox label="Wo ist der Dekubitus entstanden ?" name="dekubituslok"
              tooltip="Solange diese Wunde nicht abgeheilt ist, bleibt der Entstehungsort gleich."
              lockedforchanges="true" depends-on="dekubitus" visible-when-dependency-eq="true"
              default-value-when-shown="1">
        <item label="bei uns" name="1"/>
        <item label="Im Krankenhaus" name="2"/>
        <item label="zu Hause (vor dem Einzug)" name="3"/>
        <item label="woanders" name="4"/>
    </combobox>

    <combobox label="Wund-Stadium nach Daniel" name="daniel" depends-on="dekubitus"
              visible-when-dependency-neq="true" default-value-when-shown="1">
        <item label="1. Grad Hautrötung" name="1"
              tooltip="Erythem, scharf begrenzt, schmerzlos, reversibel umschriebene Hautrötung bei intakter Epidermis (Oberhaut)"/>
        <item label="2. Grad Blasenbildung" name="2"
              tooltip="Blasenbildung der Haut (Cutis), oberflächliche Ulcerationen der Epidermis (Oberhaut) bis zur Dermis (Lederhaut)"/>
        <item label="3. Grad offener Hautdefekt" name="3" tooltip="Ulcerationen bis in die Subcutis (Unterhaut)"/>
        <item label="4. Grad Muskulatur sichtbar" name="4"
              tooltip="Ulcerationen bis auf die Faszie auch Mitbefall der Muskulatur möglich"/>
        <item label="5. Grad Knochen befallen" name="5"
              tooltip="Mitbefall von Knochen, Gelenken oder Beckenorganen (Rectum, Vagina usw.)"/>
    </combobox>

    <label label="Die Schmerzintensität tragen Sie gesondert unter &quot;Schmerzeinschätzung&quot; ein." size="16"
           fontstyle="bold" layout="p"/>

    <textfield name="laenge" label="Länge (cm)" layout="br tab" length="6" hfill="false"/>
    <textfield name="breite" label="Breite (cm)" layout="tab" length="6" hfill="false"/>
    <textfield name="tiefe" label="Tiefe (cm)" layout="tab" length="6" hfill="false"/>

    <tabgroup size="16" fontstyle="bold" label="Wundfläche" name="tg1">
        <checkbox name="epi" label="Epithelisierung" layout="left"/>
        <checkbox name="ggran" label="gute Granulation" layout="left"/>
        <checkbox name="sgran" label="schlechte Granulation" layout="left"/>
        <checkbox name="fibrin" label="Fibrinbelag" layout="left"/>
        <checkbox name="fnekr" label="Feuchte Nekrose" layout="left"/>
        <checkbox name="tnekr" label="Trockene Nekrose" layout="br"/>
        <checkbox name="inseln" label="Wundheilungsinseln" layout="left"/>
        <checkbox name="hyper" label="Hypergranulation" layout="left"/>
    </tabgroup>

    <tabgroup size="16" fontstyle="bold" label="Wundumgebung" name="tg2">
        <checkbox name="rosig1" label="rosig" layout="left"/>
        <checkbox name="rot1" label="rot" layout="left"/>
        <checkbox name="blau1" label="bläulich verfärbt" layout="left"/>
        <checkbox name="marz1" label="marzeriert" layout="left"/>
        <checkbox name="verh1" label="verhärtet" layout="left"/>
        <checkbox name="oedem1" label="Ödembildung" layout="left"/>
    </tabgroup>

    <tabgroup size="16" fontstyle="bold" label="Wundrand" name="tg3">
        <checkbox name="rosig2" label="rosig" layout="left"/>
        <checkbox name="rot2" label="rot" layout="left"/>
        <checkbox name="glatt2" label="glatt" layout="left"/>
        <checkbox name="geschw2" label="geschwollen" layout="left"/>
        <checkbox name="marz2" label="marzeriert" layout="left"/>
        <checkbox name="nekr2" label="Nekrose" layout="left"/>
        <checkbox name="taschen2" label="Taschenbildung" layout="br left" default="false"/>
        <textfield name="ttasche" default="0" label="Tiefe der Tasche (cm)" length="6" hfill="false" size="16"
                   fontstyle="bold" layout="left" depends-on="taschen2" visible-when-dependency-eq="true"
                   default-value-when-shown="1"/>
    </tabgroup>

    <tabgroup size="16" fontstyle="bold" label="Wundexsudat" name="tg4">
        <checkbox name="kein3" label="kein" layout="left"/>
        <checkbox name="wenig3" label="wenig" layout="left"/>
        <checkbox name="viel3" label="viel" layout="left"/>
        <checkbox name="klar3" label="klar" layout="left"/>
        <checkbox name="blut3" label="blutig" layout="left"/>
        <checkbox name="eitr3" label="eitrig" layout="left"/>
        <checkbox name="gruen3" label="grün" layout="left"/>
        <checkbox name="braun3" label="braun" layout="left"/>
    </tabgroup>

    <tabgroup size="16" fontstyle="bold" label="Wundgeruch (vor VW)" name="tg5">
        <checkbox name="unauf4" label="unauffällig" layout="left"/>
        <checkbox name="leicht4" label="leicht" layout="left"/>
        <checkbox name="uebel4" label="übelriechend" layout="left"/>
        <checkbox name="suebel4" label="stark übelriechend" layout="left"/>
    </tabgroup>

    <tabgroup size="16" fontstyle="bold" label="Wundgeruch (während VW)" name="tg6">
        <checkbox name="unauf5" label="unauffällig" layout="left"/>
        <checkbox name="leicht5" label="leicht" layout="left"/>
        <checkbox name="uebel5" label="übelriechend" layout="left"/>
        <checkbox name="suebel5" label="stark übelriechend" layout="left"/>
    </tabgroup>

    <tabgroup size="16" fontstyle="bold" label="Wundgeruch (nach VW)" name="tg7">
        <checkbox name="unauf6" label="unauffällig" layout="left"/>
        <checkbox name="leicht6" label="leicht" layout="left"/>
        <checkbox name="uebel6" label="übelriechend" layout="left"/>
        <checkbox name="suebel6" label="stark übelriechend" layout="left"/>
    </tabgroup>';
UPDATE `resinfotype` SET `XML` = @wundxml WHERE resinfotype.BWINFTYP LIKE 'WUNDE%c';
--
-- Impfungen geändert
UPDATE resinfotype t
SET t.XML = '  <combobox label="word.vaccine" name="vaccinetype">
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
    </optiongroup>',
    t.IntervalMode = 3
WHERE t.BWINFTYP = 'vaccin1';
-- bestehende Impfungen korrigieren
UPDATE resinfo r SET r.Bis = r.Von
WHERE r.BWINFTYP = 'vaccin1';
--
INSERT INTO sysprops (K, V, UKennung) VALUES ('qdvs.tage.erfassungsperiode', '183', null);
--
alter table `groups`
    change `System` sysflag tinyint(1) default 0 not null;
rename table `groups` to opgroups;
alter table users
    change Status userstatus tinyint null;
rename table users to opusers;
--
alter table nreports
    drop column Dauer;
alter table intervention
    drop column Dauer;
alter table ischedule
    drop column Dauer;
alter table bhp
    drop column Dauer;
alter table dfn
    drop column Dauer;
--
alter table resvaluetypes
    add active bool default true not null,
    add version  bigint(20) not null,
    add min1 DECIMAL(9,2) default null comment 'minimum value for value1',
    add min2 DECIMAL(9,2) default null comment 'minimum value for value2',
    add min3 DECIMAL(9,2) default null comment 'minimum value for value3',
    add max1 DECIMAL(9,2) default null comment 'minimum value for value1',
    add max2 DECIMAL(9,2) default null comment 'minimum value for value2',
    add max3 DECIMAL(9,2) default null comment 'minimum value for value3';
UPDATE resvaluetypes t
SET t.active = 0
WHERE t.ID = 14;
-- Blutdruck / Puls
UPDATE resvaluetypes t SET t.min1 = 10, t.min2 = 10, t.min3 = 1, t.max1 = 500, t.max2 = 500, t.max3 = 500 WHERE t.ID = 1;
-- Puls
UPDATE resvaluetypes t SET t.min1 = 1, t.max1 = 500 WHERE t.ID = 2;
-- Temperatur
UPDATE resvaluetypes t SET t.min1 = 15, t.max1 = 44 WHERE t.ID = 3;
-- BZ
UPDATE resvaluetypes t SET t.min1 = 5, t.max1 = 1000 WHERE t.ID = 4;
-- Gewicht
UPDATE resvaluetypes t SET t.min1 = 1.00, t.max1 = 500.0 WHERE t.ID = 5;
-- Größe
UPDATE resvaluetypes t SET t.min1 = 0.1, t.max1 = 3.00 WHERE t.ID = 6;
-- Atemfrequenz
UPDATE resvaluetypes t SET t.min1 = 2, t.max1 = 100 WHERE t.ID = 7;
-- Quickwert
UPDATE resvaluetypes t SET t.min1 = 0.1, t.max1 = 100 WHERE t.ID = 8;
-- Ein-Ausfuhr
UPDATE resvaluetypes t SET t.min1 = 0.1, t.max1 = 5000 WHERE t.ID = 11;
-- Sauerstoffsättigung
UPDATE resvaluetypes t SET t.min1 = 0.1, t.max1 = 100 WHERE t.ID = 12;
--
UPDATE resinfotype t SET t.XML = '<label layout="br left hfill" size="14" fontstyle="bold" label="Setzen Sie den Zeitpunkt des Ereignis nach dem Speichern."/>
<qdvs optional="true"/>' WHERE t.BWINFTYP LIKE 'apoplex01';
UPDATE resinfotype t SET t.XML = '<label layout="br left hfill" size="14" fontstyle="bold" label="Setzen Sie den Zeitpunkt des Ereignis nach dem Speichern."/>
<qdvs optional="true"/>' WHERE t.BWINFTYP LIKE 'herzinf01';
UPDATE resinfotype t SET t.XML = '<label layout="br left hfill" size="14" fontstyle="bold" label="Setzen Sie den Zeitpunkt des Ereignis nach dem Speichern."/>
<qdvs optional="true"/>' WHERE t.BWINFTYP LIKE 'fraktur01';
UPDATE resinfotype t SET t.XML = '<qdvs optional="true"/>
<checkbox label="erhöhter Unterstützungsbedarf bei Alltagsverrichtungen" name="erhoehter_bedarf_alltag"/>
<checkbox label="erhöhter Unterstützungsbedarf bei der Mobilität" name="erhoehter_bedarf_mobilitaet"/>' WHERE t.BWINFTYP LIKE 'strzfolg01';
UPDATE resinfotype t SET t.XML = '<label size="16" fontstyle="bold"
           label="Amputationsangaben (werden zur Berechnung der angepassten Köpergewichte herangezogen)"/>
    <tx tooltip="Diese Eintragungen werden in den Überleitbogen übernommen. Seite 2, Abschnitt 18."/>
    <qdvs optional="true"/>

    <optiongroup name="upperleft" label="linkes, oberes Viertel">
        <option label="keine Amputation" name="none" default="true"/>
        <option label="linke Hand" name="hand"/>
        <option label="unterhalb des linken Ellenbogens" name="belowellbow"/>
        <option label="oberhalb des linken Ellenbogens" name="aboveellbow" layout="br tab"/>
        <option label="vollständige linke, obere Extremität" name="complete"/>
    </optiongroup>

    <datefield label="Datum der Operation" name="dateupperleft" depends-on="upperleft"
               visible-when-dependency-neq="none" default-value-when-shown="now"/>
    <separator/>

    <optiongroup name="upperright" label="rechtes, oberes Viertel">
        <option label="keine Amputation" name="none" default="true"/>
        <option label="rechte Hand" name="hand"/>
        <option label="unterhalb des rechten Ellenbogens" name="belowellbow"/>
        <option label="oberhalb des rechten Ellenbogens" name="aboveellbow" layout="br tab"/>
        <option label="vollständige rechte, obere Extremität" name="complete"/>
    </optiongroup>

    <datefield label="Datum der Operation" name="dateupperright" depends-on="upperright"
               visible-when-dependency-neq="none" default-value-when-shown="now"/>
    <separator/>

    <optiongroup name="lowerleft" label="linkes, unteres Viertel">
        <option label="keine Amputation" name="none" default="true"/>
        <option label="linker Fuß" name="foot"/>
        <option label="unterhalb des linken Knies" name="belowknee"/>
        <option label="oberhalb des linken Knies" name="aboveknee" layout="br tab"/>
        <option label="vollständige linke, untere Extremität" name="complete"/>
    </optiongroup>

    <datefield label="Datum der Operation" name="datelowerleft" depends-on="lowerleft"
               visible-when-dependency-neq="none" default-value-when-shown="now"/>
    <separator/>

    <optiongroup name="lowerright" label="rechtes, unteres Viertel">
        <option label="keine Amputation" name="none" default="true"/>
        <option label="rechter Fuß" name="foot"/>
        <option label="unterhalb des rechten Knies" name="belowknee"/>
        <option label="oberhalb des rechten Knies" name="aboveknee" layout="br tab"/>
        <option label="vollständige rechte, untere Extremität" name="complete"/>
    </optiongroup>

    <datefield label="Datum der Operation" name="datelowerright" depends-on="lowerright"
               visible-when-dependency-neq="none" default-value-when-shown="now"/>' WHERE t.BWINFTYP LIKE 'amput01';
UPDATE resinfotype t SET t.XML = '<label size="24" label="Einschätzung durch Pflegekraft" color="blue"/>
    <qdvs optional="true"/>

    <scale name="besd" label="BESD - BEurteilung von Schmerzen bei Demenz" resvaltype="14">
        <scalegroup name="besd1" label="Atmung (unabhängig von Lautäußerungen)">
            <option label="normal" name="s0-1" score="0" layout="left" default="true"/>
            <option label="gelegentlich angestrengt atmen" name="s1-1" score="1" layout="left"/>
            <option label="kurze Phasen von Hyperventilation" tooltip="schnelle und tiefe Atemzu?ge" name="s1-2"
                    score="1" layout="br"/>
            <option label="lautstark angestrengt atmen" name="s2-1" score="2" layout="left"/>
            <option label="lange Phasen von Hyperventilation" tooltip="schnelle und tiefe Atemzu?ge" name="s2-2"
                    score="2" layout="br"/>
            <option label="Cheyne Stoke Atmung"
                    tooltip="tiefer werdende und wieder abflachende Atemzu?ge mit Atempausen" name="s2-3" score="2"
                    layout="left"/>
        </scalegroup>
        <scalegroup name="besd2" label="Negative Lautäußerungen">
            <option label="keine" name="s0-1" score="0" layout="left" default="true"/>
            <option label="gelegentlich stöhnen oder ächzen" name="s1-1" score="1" layout="left"/>
            <option label="sich leise negativ oder missbilligend äußern" name="s1-2" score="1" layout="br"/>
            <option label="wiederholt beunruhigt rufen" name="s2-1" score="2" layout="left"/>
            <option label="laut stöhnen oder ächzen" name="s2-2" score="2" layout="br"/>
            <option label="weinen" name="s2-3" score="2" layout="left"/>
        </scalegroup>
        <scalegroup name="besd3" label="Gesichtsausdruck">
            <option label="lächelnd oder nichts sagend" name="s0-1" score="0" layout="left" default="true"/>
            <option label="trauriger Gesichtsausdruck" name="s1-1" score="1" layout="left"/>
            <option label="ängstlicher Gesichtsausdruck" name="s1-2" score="1" layout="br"/>
            <option label="sorgenvoller Blick" name="s1-3" score="1" layout="left"/>
            <option label="grimassieren" name="s2-1" score="2" layout="left"/>
        </scalegroup>
        <scalegroup name="besd4" label="Körpersprache">
            <option label="entspannt" name="s0-1" score="0" layout="left" default="true"/>
            <option label="angespannte Körperhaltung" name="s1-1" score="1" layout="left"/>
            <option label="nervös hin und her gehen" name="s1-2" score="1" layout="br"/>
            <option label="nesteln" name="s1-3" score="1" layout="left"/>
            <option label="Körpersprache starr" name="s2-1" score="2" layout="left"/>
            <option label="geballte Fäuste" name="s2-2" score="2" layout="left"/>
            <option label="angezogene Knie" name="s2-3" score="2" layout="br"/>
            <option label="sich entziehen oder wegstoßen" name="s2-4" score="2" layout="left"/>
            <option label="schlagen" name="s2-5" score="2" layout="left"/>
        </scalegroup>
        <scalegroup name="besd5" label="Trost">
            <option label="trösten nicht notwendig" name="s0-1" score="0" layout="left" default="true"/>
            <option label="Ablenken durch Stimme oder Berührung ist möglich" name="s1-1" score="1" layout="left"/>
            <option label="Ablenken durch Stimme oder Berührung ist nicht möglich" name="s2-1" score="2" layout="br"/>
        </scalegroup>
        <risk from="0" to="1" label="Wahrscheinlich keine Schmerzen" color="dark_green" rating="0"/>
        <risk from="2" to="10" label="BW hat wahrscheinlich Schmerzen" color="dark_red" rating="1"/>
    </scale>

    <label size="24" label="Situationsbeurteilung" color="blue"/>

    <checkbox name="schmerzfrei" label="Schmerzfrei durch Medikamente" layout="br left" depends-on="schmerzint"
              size="14"/>
    <label
            label="Akutschmerz i.d.R. weniger als 3 Monate, Chronischer Schmerz zwischen 6 Wochen und 3 Monaten, oder länger"
            size="14" fontstyle="bold"/>

    <combobox label="Schmerztyp" name="schmerztyp" tooltip="[h1]Akuter Schmerz, „sinnvoller Schmerz“[/h1]
              [p]
              Der akute Schmerz gilt als Alarmzeichen des Körpers. Schon die alten Griechen nannten den Schmerz den „bellenden Wächter der Gesundheit“ (Hypokrates).
              Der akute Schmerz macht uns aufmerksam, dass etwas nicht stimmt und ist zeitlich begrenzt. Ist die Ursache behoben verschwindet der Schmerz meistens wieder. Wenn wir wissen warum wir Schmerzen haben (z.B. den Fuss verstaucht), können wir den Schmerz auch eher akzeptieren. Hier spielt die individuelle Wahrnehmung und das Erlernte „umgehen mit dem Schmerz“ eine wichtige Rolle.
              [/p]
              [p]
              Akuter Schmerz ist ein plötzlich auftretender und nur kurze Zeit andauern der Schmerz. Er wird als existentielle Erfahrung wahrgenommen, die eine lebenserhaltende Alarm- und Schutzfunktion einnimmt. Akuter Schmerz steht in einem offensichtlichen und direkten Zusammenhang mit einer Gewebe oder Organschädigung, also einer körperlichen Ursache. Nonverbale und verbale Signale, die wir im akuten Schmerz aussenden, verursachen unwillku?rlich Empathie und das Bedu?ürfnis fu?r Abhilfe zu sorgen. Akuter Schmerz geht mit physiologischen Begleiterscheinungen einher, wie einem Anstieg des Blutdrucks, des Pulses, Schweißausbru?chen und Anstieg der Atemfrequenz. Insbesondere diese Begleiterscheinungen, die in der akuten Versorgungssituation unmittelbar erkennbar sind, zeigt der Mensch mit ausschließlich chronischen Schmerzen nicht.
              [/p]

              [h1]Chronischer Schmerz, „sinnloser Schmerz“[/h1]
              [p]
              Der chronische Schmerz hat an sich keine Warnfunktion mehr. Seine Ursache ist nicht (mehr) ausschaltbar, er nimmt dem Menschen sinnlos die Kraft weg und zehrt allmählich seinen Lebensmut auf. Wenn die Tage zur Qual werden, erschöpft sich die Tragfähigkeit, der Leidende wünscht nur mehr ein Ende herbei, unter Umständen sogar um den Preis seines Lebens, denn es genügt nicht nur am Leben zu sein, man muss auch sein Leben haben.
              Der chronische Schmerz kann zur eigenständigen Schmerzkrankheit werden, der alle Ebenen des Menschseins beeinflusst und beeinträchtigt. Man spricht dann von „total pain“. Dieser Schmerz ist oft losgelöst von der ursprünglichen Krankheit. Gerade wenn die Ursache unbekannt ist, kann die Chronifizierung schnell eintreten.
              [/p]
              [p]
              Der Übergang zwischen akutem und chronischem Schmerz verläuft kontinuierlich. Gleichwohl werden verschiedene Zeiträume angenommen, ab wann ein Schmerz als chronischer, oder anhaltender Schmerz zu betrachten ist. Je nach Lokalisation des Schmerzes wird hierbei von mehr als 6 Wochen bis hin zu 3 Monaten ausgegangen. In erster Linie wird die Entstehung des chronischen Schmerzes durch drei grundlegende Elemente beschrieben:
              [/p]
              [ul]
              [li]Es handelt sich um einen Entstehungsprozess, der durch ein Zusammenwirken von krankheitsbedingten und psychosozialen Prozessen gekennzeichnet ist.[/li]
              [li]Chronischer Schmerz ist Schmerz, der u?ber einen Punkt, an dem die Heilung abgeschlossen sein sollte hinaus, anhält oder weiter auftritt. Chronischer Schmerz kann häufig nicht (mehr) mit einem Gewebeschaden oder einer Verletzung in Verbindung gebracht werden.[/li]
              [li]Der Chronifizierung akuter Schmerzen kann durch angemessene Therapie des akuten Schmerzes entgegengewirkt werden. Eine fru?hzeitige Linderung von akutem Schmerz kann eine Entwicklung von chronischen Schmerzen verhindern. Bestimmte operative Verfahren, z. B. Amputationen, Mastektomien oder Thorakotomien bewirken häufig chronische Schmerzen.[/li]
              [/ul]" depends-on="schmerzint" visible-when-dependency-neq="0" default-value-when-shown="0">
        <item label="akute Schmerzen" name="0"/>
        <item label="chronische Schmerzen" name="1"/>
    </combobox>' WHERE t.BWINFTYP LIKE 'besd2';
UPDATE resinfotype t SET t.XML = ' <qdvs optional="true"/>
    <tabgroup size="18" label="Grund der Fixierung" name="tab1">
        <checkbox name="eigen" label="Eigengefährung" />
        <checkbox name="unfall" label="Sturz-/Unfallgefahr" layout="left"/>
        <checkbox name="eigen" label="Auf eigenen Wunsch" layout="left"/>
        <checkbox name="fremd" label="Fremdgefährdung"/>
        <checkbox name="agg" label="Aggression" layout="left"/>
        <checkbox name="path" label="pathologische Unruhe" layout="left"/>
        <checkbox name="ngf" label="Nicht steh- und gehfähig"/>
    </tabgroup>
    <tabgroup size="18" label="Art der Fixierung" name="tab2">
        <checkbox name="leibgurt" label="Leibgurt"
                  tooltip="Bitte beachten Sie, dass alle Gurtanwendungen zu erfassen sind, gleichgültig, ob eine richterliche Genehmigung oder das Einverständnis des Bewohners bzw. der Bewohnerin vorliegt. Auch Gurte, die der Bewohner bzw. die Bewohnerin theoretisch selbst öffnen könnte, sind einzutragen. Auch wenn nur aufgrund der Befürchtung eines Sturzes fixiert wird, ist dies einzutragen."/>
        <checkbox name="bettgitter" label="Bettseitenteile"
                  tooltip="Außer Betracht bleiben unterbrochene Bettseitenteile, die das Verlassen des Bettes nicht behindern."
                  layout="left"/>
        <checkbox name="sitzgurt" label="Sitzgurt"/>
        <checkbox name="geristuhl" label="Geriatriestuhl mit Vorsatztisch" layout="left"/>
        <checkbox name="sonst" label="Sonstiges" tooltip="Bitte in der Bemerkung beschreiben."/>
    </tabgroup>

    <optiongroup size="18" name="dauer">
        <option label="tagsüber" default="true" name="tag"/>
        <option label="nachts" name="nacht"/>
        <option label="halbe Stunde" name="05"/>
        <option label="1 Stunde" name="1"  layout="br tab"/>
        <option label="2 Stunden" name="2"/>
        <option label="sonst (bitte aufschreiben)" name="sonst"/>
    </optiongroup>

    <textfield name="dauersonst" label="Sonstige Dauer" depends-on="dauer" visible-when-dependency-eq="sonst"/>' WHERE t.BWINFTYP LIKE 'fixprot2';
--
alter table floors
  change floorid id bigint unsigned auto_increment;
alter table rooms
  change RID id bigint unsigned auto_increment;
-- für die Zukunft
ALTER TABLE nreports ADD FULLTEXT(Text);
--
drop table training;
drop table training2file;
drop table training2tags;
drop table training2users;
drop table trainatt2file;
