-- Ab Version 1.14.4.x
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
--
create table qdvs_laufend
(
    id       bigint auto_increment,
    lfd      smallint   not null,
    version  bigint(20) not null,
    stichtag date       not null,
    homeid   bigint     not null,
    status   smallint   not null,
    constraint qdvs_laufend_pk
        primary key (id)
)
    comment 'Liste aller QDVS Erhebungen';
--
create unique index qdvs_laufend_lfd_uindex
    on qdvs_laufend (lfd);
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
SET t.XML = ' <tabgroup size="18" fontstyle="bold" label="Hauptbezugsperson" name="hbp">
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
                       qi="63" bi="4.6.5" tooltip="bi6.interaktion.erklaerung" tx="Seite 1, Abschnitt 8">
              <option label="selbstständig" name="0" default="true" tooltip="bi6.interaktion.selbst0"/>
              <option label="überwiegend selbständig" name="1" tooltip="bi6.interaktion.selbst1"/>
              <option label="überwiegend unselbständig" name="2" tooltip="bi6.interaktion.selbst2" layout="br left"/>
              <option label="unselbständig" name="3" tooltip="bi6.interaktion.selbst3"/>
          </optiongroup>

          <optiongroup size="18" name="GAKONTAKTPFLEGE"
                       label="Kontaktpflege zu Personen außerhalb des direkten Umfelds"
                       qi="64" bi="4.6.6" tooltip="bi6.kontaktpflege.erklaerung" tx="Seite 1, Abschnitt 8">
              <option label="selbstständig" name="0" default="true" tooltip="bi6.kontaktpflege.selbst0"/>
              <option label="überwiegend selbständig" name="1" tooltip="bi6.beschaeftigen.selbst1"/>
              <option label="überwiegend unselbständig" name="2" tooltip="bi6.kontaktpflege.selbst2" layout="br left"/>
              <option label="unselbständig" name="3" tooltip="bi6.kontaktpflege.selbst3"/>
          </optiongroup>
      '
WHERE t.BWINFTYP = 'sozial01';
--
UPDATE resinfotype t
SET t.XML = '  <tabgroup size="18" fontstyle="bold" label="Anschrift" name="a2">
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

           <optiongroup name="grade" label="Einstufung" tx="Wird auf Seite 1 eingetragen."
                        qdvs="Zeile(n) 8 im DAS Dokumentationsbogen.">
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
SET t.XML = '  <tx tooltip="Sobald das Inkontinenzprofil nicht mehr auf &quot;Kontinenz&quot; steht, wird im Überleitbogen die Markierung für &quot;Harninkontinenz&quot; gesetzt."/>

    <optiongroup size="18" name="SVTOILETTE"
                 label="Benutzen einer Toilette oder eines Toilettenstuhls"
                 qdvs="Zeile(n) 55 im DAS Dokumentationsbogen." tooltip="bi4.toilette.erklaerung"
                 tx="Seite 1, Abschnitt 3">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.toilette.selbst0"/>
        <option label="überwiegend selbständig" name="2" tooltip="bi4.toilette.selbst2"/>
        <option label="überwiegend unselbständig" name="4" tooltip="bi4.toilette.selbst4" layout="br left"/>
        <option label="unselbständig" name="6" tooltip="bi4.toilette.selbst6"/>
    </optiongroup>

    <separator/>

    <optiongroup size="18" name="SVSTUHLKONTINENZ" label="Darmkontrolle, Stuhlkontinenz"
                 qdvs="Zeile(n) 45 im DAS Dokumentationsbogen."
                 tooltip="bi4.stuhlangabe.erklaerung">

        <option label="ständig kontinent" name="0" default="true" tooltip="bi4.stuhlangabe.stufe0" layout="br left"/>
        <option label="überwiegend kontinent" name="1" tooltip="bi4.stuhlangabe.stufe1"/>
        <option label="überwiegend IN_kontinent" name="2" tooltip="bi4.stuhlangabe.stufe2" layout="br left"/>
        <option label="komplett IN_kontinent" name="3" tooltip="bi4.stuhlangabe.stufe3"/>
        <option label="Person hat ein Colo- oder Ileostoma" name="4" tooltip="bi4.stuhlangabe.stufe4"/>
    </optiongroup>

    <optiongroup size="18" name="SVSTUHLKONTINENZBEW"
                 label="Bewältigen der Folgen einer Stuhlinkontinenz und Umgang mit Stoma"
                 qdvs="Zeile(n) 57 im DAS Dokumentationsbogen." tooltip="bi4.stuhlbewaeltigung.erklaerung"
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
                 qdvs="Zeile(n) 44 im DAS Dokumentationsbogen."
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
                 label="Bewältigen der Folgen einer Harninkontinenz und Umgang mit Dauerkatheter und Urostoma"
                 qdvs="Zeile(n) 56 im DAS Dokumentationsbogen."
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
SET t.XML = '<optiongroup name="SVESSEN" size="18" label="Essen" tooltip="bi4.essen.erklaerung"
                       qdvs="Zeile(n) 53 im DAS Dokumentationsbogen.">
              <option label="selbstständig" name="0" default="true" tooltip="bi4.essen.selbst0"/>
              <option label="überwiegend selbständig" name="3" tooltip="bi4.essen.selbst3"/>
              <option label="überwiegend unselbständig" name="6" tooltip="bi4.essen.selbst6"/>
              <option label="unselbständig" name="9" tooltip="bi4.essen.selbst9"/>
          </optiongroup>

          <optiongroup name="SVTRINKEN" size="18" label="Trinken" tooltip="bi4.trinken.erklaerung"
                       qdvs="Zeile(n) 54 im DAS Dokumentationsbogen.">
              <option label="selbstständig" name="0" default="true" tooltip="bi4.trinken.selbst0"/>
              <option label="überwiegend selbständig" name="2" tooltip="bi4.trinken.selbst2"/>
              <option label="überwiegend unselbständig" name="4" tooltip="bi4.trinken.selbst4"/>
              <option label="unselbständig" name="6" tooltip="bi4.trinken.selbst6"/>
          </optiongroup>

          <optiongroup name="SVNAHRUNGZUBEREITEN" size="18" tooltip="bi4.mundgerecht.erklaerung"
                       qdvs="Zeile(n) 52 im DAS Dokumentationsbogen."
                       label="Mundgerechtes Zubereiten der Nahrung, Eingießen von Getränken">

              <option label="selbstständig" name="0" default="true" tooltip="bi4.mundgerecht.selbst0"/>
              <option label="überwiegend selbständig" name="1" tooltip="bi4.mundgerecht.selbst1"/>
              <option label="überwiegend unselbständig" name="2" tooltip="bi4.mundgerecht.selbst2"/>
              <option label="unselbständig" name="3" tooltip="bi4.mundgerecht.selbst3"/>
          </optiongroup>

          <tabgroup size="18" label="Sonstige Angaben" name="sonst">
              <checkbox label="Nahrungskarenz" name="abrosia" layout="br left"/>
              <checkbox label="Diätkost" name="diet" layout="left"/>
              <checkbox label="Schluckstörungen" name="dysphagia" layout="left"/>
              <textfield name="=s" label="Vorlieben, Essen und Trinken" hfill="false" length="40"/>
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
SET t.XML = ' <label layout="br left hfill" size="20" fontstyle="bold" color="yellow" bgcolor="blue"
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
              <checkbox label="Fraktur" name="fracture" layout="left"
                        qdvs="Verwendung im Abschschnitt &quot;Sturzfolgen&quot;"/>
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
              <checkbox label="Arztkontakt" name="gp" qdvs="Verwendung im Abschschnitt &quot;Sturzfolgen&quot;"/>
              <checkbox label="Krankenhauseinweisung" name="hospital"
                        qdvs="Verwendung im Abschschnitt &quot;Sturzfolgen&quot;"/>
          </tabgroup>
          <textfield name="othertext6" label="Anmerkungen zu den Massnahmen" hfill="false"/>

          <url label="Das neue Formular ''Sturzprotokoll'' (Uni Bonn, siehe OPDE Quellen UNIBONN2008-01)"
               link="https://www.offene-pflege.de/de/sources-de"/>'
WHERE t.BWINFTYP = 'FALLPROT01';
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
SET t.XML = ' <qdvs
                  tooltip="Zeile(n) 72 - 75 im DAS Dokumentationsbogen (Abschnitt &quot;Körpergewicht und Größe&quot;)"/>
          <tabgroup size="18" fontstyle="bold"
                    label="Welche der aufgeführten Punkte trafen laut Pflegedokumentation für den Bewohner bzw. die Bewohnerin seit der letzten Ergebniserfassung zu?">
              <checkbox label="Gewichtsverlust durch medikamentöse Ausschwemmung" name="1"/>
              <checkbox label="Gewichtsverlust aufgrund ärztlich angeordneter oder ärztlich genehmigter Diät" name="2"
                        layout="br left"/>
              <checkbox label="Mindestens 10% Gewichtsverlust während eines Krankenhausaufenthalts" name="3"
                        layout="br left"/>
              <checkbox
                      label="Aktuelles Gewicht liegt nicht vor. BW wird aufgrund einer Entscheidung des Arztes oder der Angehörigen oder eines Betreuers nicht mehr gewogen"
                      name="4" layout="br left"/>
              <checkbox
                      label="Aktuelles Gewicht liegt nicht vor. BW möchte nicht gewogen werden" name="5" layout="br left"/>
          </tabgroup>
          '
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
SET t.XML = ' <optiongroup size="18" name="EINZUGGESPR"
                       label="Integrationsgespräch"
                       qdvs="Zeile 93"
                       tooltip="Ist in den Wochen nach dem Einzug mit dem Bewohner bzw. der Bewohnerin und/oder einer seiner bzw. ihrer Angehörigen oder sonstigen Vertrauenspersonen ein Gespräch über sein bzw. ihr Einleben und die zukünftige Versorgung geführt worden?">
              <option label="ja" name="1" default="true"/>
              <option label="nicht möglich aufgrund fehlender Vertrauenspersonen des Bewohners bzw. der Bewohnerin"
                      name="2" layout="br left"/>
              <option label="nein, aus anderen Gründen" name="3" layout="br left"/>
          </optiongroup>

          <datefield label="Datum des Integrationsgesprächs" name="EINZUGGESPRDATUM" depends-on="EINZUGGESPR"
                     visible-when-dependency-eq="1" default-value-when-shown="now" qdvs="Zeile 94"/>

          <tabgroup size="18" fontstyle="bold" label="Wer hat an dem Integrationsgespräch teilgenommen?" qdvs="Zeile 95"
                    name="tab1">
              <checkbox name="1" label="Bewohner/Bewohnerin" default="true" layout="br left"/>
              <checkbox name="2" label="Angehörige"/>
              <checkbox name="3" label="Betreuer/Betreuerin"/>
              <checkbox name="4" label="andere Vertrauenspersonen, die nicht in der Einrichtung beschäftigt sind"
                        layout="left"/>
          </tabgroup>

          <optiongroup size="18" name="EINZUGGESPRDOKU"
                       label="Protokoll wurde erstellt und angehangen"
                       qdvs="Zeile 96">
              <option label="nein" name="0"/>
              <option label="ja" name="1" default="true"/>
          </optiongroup>'
WHERE t.BWINFTYP = 'intgesp01';
--
UPDATE resinfotype t
SET t.XML = '<optiongroup size="18" name="BEWUSSTSEINSZUSTAND"
                       label="Bewusstseinszustand"
                       qdvs="Zeile(n) 23 im DAS Dokumentationsbogen." tooltip="bi6.beschaeftigen.erklaerung"
                       tx="Diese Eintragungen werden in den Überleitbogen übernommen. Seite 2, Abschnitt 11.">
              <option label="wach" name="1" default="true"
                      tooltip="Die Person ist ansprechbar und kann an Aktivitäten teilnehmen."/>
              <option label="schläfrig" name="2"
                      tooltip="Die Person ist ansprechbar und gut erweckbar, wirkt jedoch müde und ist verlangsamt in seinen Handlungen."/>
              <option label="somnolent" name="3"
                      tooltip="Die Person ist sehr schläfrig und kann nur durch starke äußere Reize geweckt werden (z. B. kräftiges Rütteln an der Schulter oder mehrfaches, sehr lautes Ansprechen)."
                      layout="br left"/>
              <option label="komatös" name="4" tooltip="Die Person kann durch äußere Reize nicht mehr geweckt werden."/>
              <option label="wachkoma" name="5" tooltip="Dies trifft nur dann zu, wenn eine ärztliche Diagnose vorliegt."/>
          </optiongroup>'
WHERE t.BWINFTYP = 'bewusst01';
--
UPDATE resinfotype t
SET t.XML = ' <qpr tooltip="Sobald diese Information eingetragen wurde, geht das System von einer künstlichen Ernährung aus. Zeile: 42"/>
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
          <optiongroup name="SVERNAEHRUNGUMFANG" label="In welchem Umfang erfolgt eine künstliche Ernährung?"
                       qi="43" bi="4.4.13">
              <option label="nicht täglich oder nicht dauerhaft" name="0" default="true"/>
              <option label="täglich, aber zusätzlich zur oralen Ernährung" name="6"/>
              <option label="ausschließlich oder nahezu ausschließlich künstliche Ernährung" name="3"/>
          </optiongroup>

          <optiongroup name="SVFREMDHILFE" label="Erfolgt die Bedienung selbständig oder mit Fremdhilfe?" qi="44"
                       bi="4.4.13">
              <option label="selbständig" name="0" default="true"/>
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
SET t.XML = '   <tx
            tooltip="[b]Seite 1, Abschnitt 3. &quot;Grundpflege&quot;[/b]
            [br/]Die Markierungen im Abschnitt &quot;Grundpflege&quot; werden entsprechenden Ihren Einträgen in diesem Formular gesetzt.
            [br/]Der Bemerkungs-Text wird in die Bemerkungs-Zeile dieses Abschnitts im Überleitbogen übernommen."/>

    <optiongroup size="18" name="SVOBERKOERPER" label="Waschen des vorderen Oberkörpers" qdvs="Zeile 47"
                 tooltip="bi4.oberkoerper.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.oberkoerper.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.oberkoerper.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.oberkoerper.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi4.oberkoerper.selbst3"/>
    </optiongroup>

    <optiongroup size="18" name="SVKOPF" label="Körperpflege im Bereich des Kopfes" qdvs="Zeile 48"
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

    <optiongroup size="18" name="SVINTIMBEREICH" label="Waschen des Intimbereichs" qdvs="Zeile 49"
                 tooltip="bi4.intim.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.intim.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.intim.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.intim.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi4.intim.selbst3"/>
    </optiongroup>

    <optiongroup size="18" name="SVDUSCHENBADEN" label="Duschen und Baden einschließlich Waschen der Haare"
                 qdvs="Zeile 50"
                 tooltip="bi4.baden.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.baden.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.baden.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.baden.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi4.baden.selbst3"/>
    </optiongroup>

    <optiongroup size="18" name="SVANAUSOBERKOERPER" label="An- und Auskleiden des Oberkörpers" qdvs="Zeile 51"
                 tooltip="bi4.okankleiden.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi4.okankleiden.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi4.okankleiden.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi4.okankleiden.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi4.okankleiden.selbst3"/>
    </optiongroup>

    <optiongroup size="18" name="SVANAUSUNTERKOERPER" label="An- und Auskleiden des Unterkörpers" qdvs="Zeile 52"
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

    <optiongroup name="runaway" label="Weglauftendenz"><option label="ja" name="1" default="true"/> <option
            label="nein" name="2"/><option label="zeitweise" name="3"/></optiongroup>

    <separator/>

    <optiongroup name="KKFERKENNEN" size="18" label="Erkennen von Personen aus dem näheren Umfeld"
                 tooltip="bi2.personen.erklaerung" qdvs="Zeile(n) 30 im DAS Dokumentationsbogen.">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.personen.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.personen.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.personen.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.personen.selbst3"/>
    </optiongroup>

    <optiongroup name="KKFORIENTOERTLICH" size="18" label="Örtliche Orientierung" tooltip="bi2.orte.erklaerung"
                 qdvs="Zeile(n) 31 im DAS Dokumentationsbogen.">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.orte.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.orte.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.orte.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.orte.selbst3"/>
    </optiongroup>

    <optiongroup name="KKFORIENTZEITLICH" size="18" label="Zeitliche Orientierung" tooltip="bi2.zeitlich.erklaerung"
                 qdvs="Zeile(n) 32 im DAS Dokumentationsbogen.">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.zeitlich.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.zeitlich.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.zeitlich.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.zeitlich.selbst3"/>
    </optiongroup>

    <optiongroup name="KKFERINNERN" size="18" label="Sich Erinnern" tooltip="bi2.erinnern.erklaerung"
                 qdvs="Zeile(n) 33 im DAS Dokumentationsbogen.">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.erinnern.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.erinnern.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.erinnern.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.erinnern.selbst3"/>
    </optiongroup>

    <optiongroup name="KKFHANDLUNGEN" size="18" label="Steuern von mehrschrittigen Alltagshandlungen"
                 tooltip="bi2.handlungen.erklaerung" qdvs="Zeile(n) 34 im DAS Dokumentationsbogen.">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.handlungen.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.handlungen.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.handlungen.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.handlungen.selbst3"/>
    </optiongroup>

    <optiongroup name="KKFENTSCHEIDUNGEN" size="18" label="Treffen von Entscheidungen im Alltagsleben"
                 tooltip="bi2.entscheidungen.erklaerung" qdvs="Zeile(n) 35 im DAS Dokumentationsbogen.">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.entscheidungen.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.entscheidungen.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.entscheidungen.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.entscheidungen.selbst3"/>
    </optiongroup>

    <optiongroup name="KKFVERSTEHENINFO" size="18" label="Verstehen von Sachverhalten und Informationen"
                 tooltip="bi2.verstehen.erklaerung" qdvs="Zeile(n) 36 im DAS Dokumentationsbogen.">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.verstehen.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.verstehen.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.verstehen.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.verstehen.selbst3"/>
    </optiongroup>

    <optiongroup name="KKFGEFAHRERKENNEN" size="18" label="Erkennen von Risiken und Gefahren"
                 tooltip="bi2.risiken.erklaerung"
                 qdvs="Zeile(n) 37 im DAS Dokumentationsbogen.">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.risiken.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.risiken.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.risiken.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.risiken.selbst3"/>
    </optiongroup>

    <optiongroup name="KKFMITTEILEN" size="18" label="Mitteilen von elementaren Bedürfnissen"
                 tooltip="bi2.beduerfnissen.erklaerung" qi="Zeile 39">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.beduerfnissen.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.beduerfnissen.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.beduerfnissen.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.beduerfnissen.selbst3"/>
    </optiongroup>

    <optiongroup name="KKFVERSTEHENAUF" size="18" label="Verstehen von Aufforderungen"
                 tooltip="bi2.aufforderungen.erklaerung" qdvs="Zeile(n) 39 im DAS Dokumentationsbogen.">
        <option label="vorhanden/unbeeinträchtigt" name="0" default="true" tooltip="bi2.aufforderungen.selbst0"/>
        <option label="größtenteils vorhanden" name="1" tooltip="bi2.aufforderungen.selbst1"/>
        <option label="in geringem Maße vorhanden" name="2" tooltip="bi2.aufforderungen.selbst2"/>
        <option label="nicht vorhanden" name="3" tooltip="bi2.aufforderungen.selbst3"/>
    </optiongroup>

    <optiongroup name="KKFBETEILIGUNG" size="18" label="Beteiligung an einem Gespräch"
                 tooltip="bi2.gespraech.erklaerung"
                 qdvs="Zeile(n) 40 im DAS Dokumentationsbogen.">
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
SET t.XML = '  <optiongroup size="18" name="GARUHENSCHLAFEN"
                       label="Ruhen und Schlafen"
                       qi="60" bi="4.6.2" tooltip="bi6.schlafen.erklaerung" tx="Seite 1, Abschnitt 8">
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
SET t.XML = ' <tabgroup size="18" fontstyle="bold" label="Hauptbezugsperson" name="hbp">
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
                       qdvs="63" bi="4.6.5" tooltip="bi6.interaktion.erklaerung" tx="Seite 1, Abschnitt 8">
              <option label="selbstständig" name="0" default="true" tooltip="bi6.interaktion.selbst0"/>
              <option label="überwiegend selbständig" name="1" tooltip="bi6.interaktion.selbst1"/>
              <option label="überwiegend unselbständig" name="2" tooltip="bi6.interaktion.selbst2" layout="br left"/>
              <option label="unselbständig" name="3" tooltip="bi6.interaktion.selbst3"/>
          </optiongroup>

          <optiongroup size="18" name="GAKONTAKTPFLEGE"
                       label="Kontaktpflege zu Personen außerhalb des direkten Umfelds"
                       qdvs="64" bi="4.6.6" tooltip="bi6.kontaktpflege.erklaerung" tx="Seite 1, Abschnitt 8">
              <option label="selbstständig" name="0" default="true" tooltip="bi6.kontaktpflege.selbst0"/>
              <option label="überwiegend selbständig" name="1" tooltip="bi6.beschaeftigen.selbst1"/>
              <option label="überwiegend unselbständig" name="2" tooltip="bi6.kontaktpflege.selbst2" layout="br left"/>
              <option label="unselbständig" name="3" tooltip="bi6.kontaktpflege.selbst3"/>
          </optiongroup>'
WHERE t.BWINFTYP = 'sozial01';
UPDATE resinfotype t
SET t.XML = '  <tx tooltip="[b]Seite 1, Abschnitt 4.[/b][br/]Alles was Sie hier als Bemerkung eintragen, steht hinterher in der Bemerkungs-Zeile dieses Abschnitts im Überleitbogen.[br/][b]Lagerungsarten[/b] werden anhand der Pflegeplanungen bestimmt."/>
    <checkbox name="bedridden" label="bettlägerig"/>
    <optiongroup size="18" name="MOBILPOSWECHSEL" label="Positionswechsel im Bett" qdvs="26"
                 tooltip="bi1.bett.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi1.bett.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi1.bett.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi1.bett.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi1.bett.selbst3"/>
    </optiongroup>
    <textfield label="Hilfsmittel" name="MOBILPOSWECHSEL.hilfsmittel" innerlayout="left"/>

    <optiongroup size="18" name="MOBILSITZPOSITION" label="Halten einer stabilen Sitzposition" qdvs="26"
                 tooltip="bi1.sitz.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi1.sitz.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi1.sitz.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi1.sitz.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi1.sitz.selbst3"/>
    </optiongroup>
    <textfield label="Hilfsmittel" name="MOBILSITZPOSITION.hilfsmittel" innerlayout="left"/>

    <optiongroup size="18" name="MOBILUMSETZEN" label="Umsetzen" qdvs="26" tooltip="bi1.umsetzen.erklaerung">
        <option label="selbstständig" name="0" default="true" tooltip="bi1.umsetzen.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi1.umsetzen.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi1.umsetzen.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi1.umsetzen.selbst3"/>
    </optiongroup>
    <textfield label="Hilfsmittel" name="MOBILUMSETZEN.hilfsmittel" innerlayout="left"/>

    <optiongroup size="18" name="MOBILFORTBEWEGUNG" label="Fortbewegen innerhalb des Wohnbereichs" qdvs="26"
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

    <optiongroup size="18" name="MOBILTREPPENSTEIGEN" label="Treppensteigen" tooltip="bi1.treppen.erklaerung"
                 qdvs="Zeile 30">
        <option label="selbstständig" name="0" default="true" tooltip="bi1.treppen.selbst0"/>
        <option label="überwiegend selbständig" name="1" tooltip="bi1.treppen.selbst1"/>
        <option label="überwiegend unselbständig" name="2" tooltip="bi1.treppen.selbst2"/>
        <option label="unselbständig" name="3" tooltip="bi1.treppen.selbst3"/>
    </optiongroup>

    <checkbox name="unfaegig-arme-beine" label="Gebrauchsunfähigkeit beider Arme und beider Beine"
              tooltip="bi1.unfaehig.arme.beine"/>'
WHERE t.BWINFTYP LIKE 'mobil02';
UPDATE resinfotype t
SET t.XML = ' <qpr tooltip="Sobald diese Information eingetragen wurde, geht das System von einer künstlichen Ernährung aus. Zeile: 42"/>
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
          <optiongroup name="SVERNAEHRUNGUMFANG" label="In welchem Umfang erfolgt eine künstliche Ernährung?"
                       qdvs="43" bi="4.4.13">
              <option label="nicht täglich oder nicht dauerhaft" name="0" default="true"/>
              <option label="täglich, aber zusätzlich zur oralen Ernährung" name="6"/>
              <option label="ausschließlich oder nahezu ausschließlich künstliche Ernährung" name="3"/>
          </optiongroup>

          <optiongroup name="SVFREMDHILFE" label="Erfolgt die Bedienung selbständig oder mit Fremdhilfe?" qdvs="44"
                       bi="4.4.13">
              <option label="selbständig" name="0" default="true"/>
              <option label="mit Fremdhilfe" name="1"/>
          </optiongroup>'
WHERE t.BWINFTYP LIKE 'kern01';
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
    add version  bigint(20) not null;
UPDATE resvaluetypes t
SET t.active = 0
WHERE t.ID = 14;
--
alter table floors
  change floorid id bigint unsigned auto_increment;
alter table rooms
  change RID id bigint unsigned auto_increment;
--
drop table training;
drop table training2file;
drop table training2tags;
drop table training2users;
drop table trainatt2file;
