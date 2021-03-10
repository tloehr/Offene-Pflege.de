package de.offene_pflege.services.qdvs;


import de.offene_pflege.entity.building.Homes;
import de.offene_pflege.entity.building.Rooms;
import de.offene_pflege.entity.info.*;
import de.offene_pflege.entity.values.ResValue;
import de.offene_pflege.entity.values.ResValueTools;
import de.offene_pflege.gui.events.AddTextListener;
import de.offene_pflege.op.tools.*;
import de.offene_pflege.services.ResvaluetypesService;
import de.offene_pflege.services.RoomsService;
import de.offene_pflege.services.qdvs.schema.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.javatuples.Quintet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Bis 2019 wurden unter Begleitung des Qualitätsausschuss Pflege vom Institut für Pflegewissenschaft an der Universität
 * Bielefeld (IPW) und vom aQua-Institut unter Federführung von Dr. Wingenfeld indikatorengestützte Instrumente und
 * Verfahren für die Qualitätsprüfungen gem. § 113b SGB XI entwickelt. Sie lösen die bisherigen
 * Qualitäts-Prüfungsrichtlinien und die Pflege-Transparenzvereinbarungen gem. § 113 SGB XI ab. Der gesetzlich
 * vorgeschriebene  Erprobungsbetrieb startet am 01.Oktober 2019 und endet am 30. Juni 2020. Ab dem 01. Juli 2020
 * beginnt dann die stichtagsbezogene regelhafte Erhebung der Ergebnisindikatoren.
 */
public class QdvsService implements HasLogger {
    private static final String HAUF = "hauf";
    private static final String ABWESENHEIT = "abwe";
    private static final String BEWUSST = "bewusst01";
    private static final String ORIENT = "orient02";
    private static final String MOBIL = "mobil02";
    private static final String AUSSCHEID = "aussch01";
    private static final String KPFLEGE = "kpflege02";
    private static final String ERN = "ern01";
    private static final String ALLTAG = "alltag01";
    private static final String SCHLAF = "schlaf02";
    private static final String SOZIAL = "sozial01";
    private static final String NINSUR = "ninsur03";
    private static final String ROOM = "room1";
    private static final String RESPIRAT = "respirat2";
    private static final String KERN = "kern01";
    private static final String INTEGRATION = "intgesp01";
    private static final String KOERPERGEWICHTDOKU = "gewdoku1";
    private static final String BESD = "besd2";
    private static final String SCHMERZ = "schmerze2";

    private final ResInfoType FALLTYPE;
    private final ResInfoType FALLAUSWIRKUNG;
    private final ResInfoType FIXIERUNGSPROTOKOLLE;
    private final AddTextListener textListener;

    ObjectFactory of;

    private LocalDateTime STICHTAG; // ist das vorher festgelegte Zieldatum (2x im Jahr). Das bleibt auch bei den Nachkorrekturen gleich
    private LocalDateTime LETZTE_ERGEBNISERFASSUNG; // ist das vorher festgelegte Zieldatum (2x im Jahr)
    private LocalDateTime ERHEBUNGSDATUM; // Gibt an, zu welchem Datum die ResInfos ausgewertet werden sollen. Das kann durchaus nach dem Stichtag sein, wenn Fehler korrigiert werden mussten.

    static final String SPECIFICATION = "V01"; // die Version der jeweilig eingereichten Datenstruktur, die zum Zeitpunkt der Verwendung gültig war.
    public static DecimalFormat NF_IDBEWOHNER = new DecimalFormat("000000");
    private File target;
    int runningNumber = 0;
    int numResidents;
    List<Resident> listeBWFehlerfrei;

    public Map<Resident, QdvsResidentInfoObject> getResidentInfoObjectMap() {
        return residentInfoObjectMap;
    }

    Map<Resident, QdvsResidentInfoObject> residentInfoObjectMap;

    RootType rootType;

    HashSet<ResInfoType> mandantoryTypes; // Diese Typen müssen unbedingt vorhanden sein. "kern01" ist optional.
    HashSet<ResInfoType> forbiddenTypes; // Diese Typen stammen noch aus der VOR QDVS Zeit und müssen ersetzt werden.

    // eine Liste aller gültigen Resinfos zum Thema Diagnosen
    ResInfoType typeDiagnosen = ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_DIAGNOSIS);
    ResInfoType typeDiabetes = ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_DIABETES);
    ResInfoType typeDemenz = ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_ORIENTATION);
    private Homes home;


    /**
     * Diese Service Klasse wertet die Pflegedaten aus und erzeugt eine passende XML Datei, die an DAS-PFLEGE
     * hochgeladen werden kann.
     *
     * @param textListener meldet alle Protokoll Einträge an den Textlistener. Der kann das dann hinterher anzeigen.
     */
    public QdvsService(AddTextListener textListener) {
        this.textListener = textListener;
        // ein paar Hilfs-Variablen.

        FALLTYPE = ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_FALL);
        FALLAUSWIRKUNG = ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_FALL_AUSWIRKUNG);
        FIXIERUNGSPROTOKOLLE = ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_FIXIERUNGPROTOKOLL);
        of = new ObjectFactory();
        rootType = of.createRootType();

        listeBWFehlerfrei = new ArrayList<>();
        residentInfoObjectMap = new HashMap<>();

        /**
         * das sind alte resinfotypes die unbedingt durch die neuen ersetzt werden müssen, damit die Auswertung funktioniert.
         * Sobald einmal die alte Version aktualisiert wurde und die Daten geändert sind, kommt das Problem nicht mehr vor.
         *
         * Wenn es noch einen von diesen Types gibt, dann ablehnen
         *
         */
        forbiddenTypes = new HashSet<>();
        for (String pk : new String[]{"confidants", "respirat1", "mobility", "orient1", "artnutrit", "hinko", "hinkon", "excrem1", "finco1", "care", "mouthcare", "food", "sleep1", "ninsur02", "amputation"}) {
            forbiddenTypes.add(ResInfoTypeTools.getByID(pk));
        }

        /**
         * Die muss es auf jeden fall geben, sonst ablehnen
         */
        mandantoryTypes = new HashSet<>();
        for (String pk : new String[]{BEWUSST, ORIENT, MOBIL, AUSSCHEID, KPFLEGE, ERN, ALLTAG, SCHLAF, SOZIAL, NINSUR, ROOM, RESPIRAT}) {
            mandantoryTypes.add(ResInfoTypeTools.getByID(pk));
        }
    }


//    public Map<Resident, QdvsResidentInfoObject> getResidentInfoObjectMap() {
//        return residentInfoObjectMap;
//    }

    /**
     * diese Methode wird von der GUI aus aufgerufen und setzt die Parameter für die Auswertung
     *
     * @param stichtag
     * @param erhebungsdatum
     * @param letzte_ergebniserfassung
     * @param home
     * @param listeAlleBewohnerAmStichtag
     * @param target
     */
    public void setParameters(LocalDate stichtag, LocalDate erhebungsdatum, LocalDate letzte_ergebniserfassung, Homes home, List<Resident> listeAlleBewohnerAmStichtag, File target) {
        LETZTE_ERGEBNISERFASSUNG = letzte_ergebniserfassung.atStartOfDay();
        ERHEBUNGSDATUM = erhebungsdatum.atTime(23, 59, 59);
        STICHTAG = stichtag.atStartOfDay();
        this.home = home;
        this.target = target;
        residentInfoObjectMap.clear();
        listeAlleBewohnerAmStichtag.forEach(resident -> residentInfoObjectMap.put(resident, new QdvsResidentInfoObject(resident)));
        numResidents = listeAlleBewohnerAmStichtag.size();
        runningNumber = 0;
    }

    private void createQDVS() {
        try {
            rootType.setHeader(createHeaderType());
            rootType.setBody(createBodyType());
        } catch (Exception e) {
            getLogger().error(e);

        }
    }

    /**
     * Erstellt den Kopf der Testdatei. Enthält die nötigen Angaben zur Prüfung, wie Datum, ID der Einrichtung usw.
     *
     * @return
     */
    private HeaderType createHeaderType() {
        HeaderType header = of.createHeaderType();
        // Document
        header.setDocument(of.createDocumentType());
        header.getDocument().setGuid(of.createGuidType()); //Factory erzeugt die Zufalls UUID automatisch
        header.getDocument().setCreationDate(of.createDateTimeType()); // Factory erzeugt das aktuelle Datum automatisch
        header.getDocument().setSpecification(of.createSpecificationType()); // Factory erzeugt das aktuelle Datum automatisch
        header.getDocument().getSpecification().setValue(SPECIFICATION);
        // Careprovider
        header.setCareProvider(of.createCareProviderType());
        header.getCareProvider().setRegistration(of.createRegistrationType());
        header.getCareProvider().getRegistration().setValue(home.getCareproviderid());
        header.getCareProvider().setTargetDate(of.createDateType(STICHTAG));
        // Software
        header.setSoftware(of.createSoftwareType());
        return header;
    }


    private BodyType createBodyType() throws Exception {

        BodyType body = of.createBodyType();
        body.setDataContainer(of.createCareDataType());

        // Facility
        body.getDataContainer().setFacility(of.createFacilityDataType());
        body.getDataContainer().getFacility().setQsData(of.createDasQsDataFacilityType());
        body.getDataContainer().getFacility().getQsData().setBELEGUNGSKAPAZITAET(of.createDasQsDataFacilityTypeBELEGUNGSKAPAZITAET());
        body.getDataContainer().getFacility().getQsData().getBELEGUNGSKAPAZITAET().setValue(home.getMaxcap());
        body.getDataContainer().getFacility().getQsData().setBELEGUNGAMSTICHTAG(of.createDasQsDataFacilityTypeBELEGUNGAMSTICHTAG());

        body.getDataContainer().getFacility().getQsData().getBELEGUNGAMSTICHTAG().setValue(ResidentTools.getAll(STICHTAG).size());

        // Residents
        body.getDataContainer().setResidents(of.createResidentsType());

        // Hier ist die Schleife, die das ganze XML Dokument erzeugt.
        residentInfoObjectMap.values().stream().sorted(Comparator.comparing(QdvsResidentInfoObject::getResident)).forEach(infoObject -> {
            runningNumber++;
            textListener.setProgress(runningNumber, numResidents * 3, ResidentTools.getTextCompact(infoObject.getResident()));
            // Unterscheidung, ob Ausschluss oder nicht
            ResidentType residentType = infoObject.getAusschluss_grund() == QdvsResidentInfoObject.MDS_GRUND_KEIN_AUSSCHLUSS ? createResidentType(infoObject.getResident()) : createResidentAusschlussType(infoObject.getResident());
            body.getDataContainer().getResidents().getResident().add(residentType);
        });

        return body;
    }


    public boolean ergebniserfassung() throws JAXBException, IOException {

        listeBWFehlerfrei.clear();
        if (STICHTAG == null) return false;

        vorpruefung();

        // fehlerfrei? wenn nur einer nicht fehlerfrei ist, gehts hier nicht weiter
        residentInfoObjectMap.values().stream().filter(qdvsResidentInfoObject -> !qdvsResidentInfoObject.isFehlerfrei()).forEach(qdvsResidentInfoObject ->
                textListener.addLog(SYSConst.html_li(SYSConst.html_bold("qdvs.vorpruefung.fehler.gefunden") + ": " + qdvsResidentInfoObject.getResident() + " " + qdvsResidentInfoObject.getFehler()))
        );

        // Nur wenn Fehlerfrei gehts hier weiter.
        boolean fehlerfrei = residentInfoObjectMap.values().stream().allMatch(qdvsResidentInfoObject -> qdvsResidentInfoObject.isFehlerfrei());
        if (fehlerfrei) {
            textListener.addLog(SYSConst.html_h2("qdvs.vorpruefung.abgeschlossen"));
            hauptpruefung();
        } else {
            textListener.addLog(SYSConst.html_h2("qdvs.vorpruefung.gescheitert"));
        }


        return fehlerfrei;
    }

    private void hauptpruefung() throws JAXBException, IOException {
        textListener.addLog(SYSConst.html_h2("qdvs.hauptprüfung.laeuft"));
        try {
            createQDVS();
            marshal(of.createRoot(rootType));
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().fatal(e);
            textListener.addLog(e.getMessage());
        }
        textListener.addLog(SYSConst.html_h2("qdvs.hauptprüfung.abgeschlossen"));
    }


    /**
     * Führt eine Vor Überprüfung durch. Wenn das Fehlerprotokoll danach nicht leer ist, gibts Gründe warum der
     * Auswertungslauf nicht vollendet werden kann.
     * <h2>Fehlergründe</h2>
     * <li><b>Fehlen</b> der ResInfos der folgenden Typen: {@code respirat2, bewusst01, orient02, mobil02, aussch01,
     * kpflege02, ern01, alltag01, schlaf02, sozial01, ninsur03, room1}. Wobei {@code kern01} optional ist.</li>
     * <li><b>Verwendung</b> von alten, nun ungeeigneten ResInfoTypen wie: {@code confidants, respirat1, mobility,
     * orient1, artnutrit, hinko, hinkon, excrem1, finco1, care, mouthcare, food, sleep1, ninsur02}</li> <br/> Während
     * des Laufs werden auch direkt alle notwendigen Daten gesammelt, so dass jede nachträgliche Ermittlung im weiteren
     * Programm Ablauf unnötig ist. Die Prüfung bricht nicht vorzeitig ab, damit wir eine vollständige Prüfung aller BW
     * erhalten.
     * <p>
     * Ein erfolgreicher Abschluss dieser Methode ist dann erreicht, wenn das Fehlerprotokoll {@code fehler_protokoll}
     * anschließend leer ist.
     */
    private void vorpruefung() {
        textListener.addLog(SYSConst.html_h2("qdvs.vorpruefung.laeuft.step1"));
        /**
         * Ausschlussgründe ( dann minimaldatensatz MDS erzeugen)
         * (1) Einzugsdatum liegt weniger als 14 Tage vor dem Stichtag.
         * (2) Bewohner bzw. Bewohnerin ist Kurzzeitpflegegast.
         * (3) Bewohner bzw. Bewohnerin befindet sich in der Sterbephase.
         * (4) Bewohner bzw. Bewohnerin hält sich seit mindestens 21 Tagen vor dem Stichtag nicht mehr in der Einrichtung auf (z. B. wegen einer Krankenhausbehandlung oder eines längeren Urlaubs mit Angehörigen).
         * Die obigen Ausschlussgründe müssen wir berücksichtigen
         *
         * Es gibt noch spezifische Ausschlussgründe, die sich aber erst bei der Berechnung der Qualität ergeben. Das macht aber die DAS. Diese Gründe beziehen sich teilweise auf Vorherige Ergebnisse.
         * Siehe "Massstaebe-und-Grundsaetze-Anlage-3-23.11.2018.pdf" Seite 29ff
         */

        // 1. Durchlauf
        // ermitteln, welche BW ausgeschlossen werden. Und warum.
        residentInfoObjectMap.keySet().forEach(resident -> {
                    // gehen wir mal davon aus, das HAUF existiert
                    getLogger().debug("Vorprüfung step1 - " + resident.toString());

                    Optional<ResInfo> hauf = ResInfoTools.getValidOnThatDayIfAny(resident, HAUF, STICHTAG);
                    runningNumber++;
                    textListener.setProgress(runningNumber, numResidents * 3, "");

                    if (!hauf.isPresent()) {
                        residentInfoObjectMap.get(resident).addLog("Kein Heimaufenthalt HAUF.");
                        getLogger().debug("Bewohner " + resident + " kein HAUF ");
                    } else {
                        long aufenthaltszeitraumInTagen = ChronoUnit.DAYS.between(JavaTimeConverter.toJavaLocalDateTime(hauf.get().getFrom()).toLocalDate(), STICHTAG.toLocalDate());
                        Date abwesendSeit = ResInfoTools.absentSince(resident);
                        long abwesenheitsZeitraumInTagen = abwesendSeit == null ? 0l : ChronoUnit.DAYS.between(JavaTimeConverter.toJavaLocalDateTime(abwesendSeit).toLocalDate(), STICHTAG.toLocalDate());

                        // Ausschlussgrund (4)
                        if (abwesenheitsZeitraumInTagen >= 21) {
                            residentInfoObjectMap.get(resident).setAusschluss_grund(QdvsResidentInfoObject.MDS_GRUND_MEHR_ALS_21_TAGE_WEG);
                            getLogger().debug("Bewohner " + resident.getId() + " andauernde Abwesenheit länger als 21 Tage :" + abwesendSeit + " // " + abwesenheitsZeitraumInTagen);
                            textListener.addLog(SYSConst.html_bold("AUSSCHLUSS Bewohner " + ResidentTools.getLabelText(resident) + ": andauernde Abwesenheit länger als 21 Tage :" + abwesendSeit + " // " + abwesenheitsZeitraumInTagen + " Tage"));

                        } else if (aufenthaltszeitraumInTagen < 14) { // Ausschlussgrund (1)
                            residentInfoObjectMap.get(resident).setAusschluss_grund(QdvsResidentInfoObject.MDS_GRUND_WENIGER_14_TAGE_DA);
                            getLogger().debug("Bewohner " + resident.getId() + " Heimaufnahme weniger als 14 Tage :" + hauf.get().getFrom() + " // " + aufenthaltszeitraumInTagen);
                            textListener.addLog(SYSConst.html_bold(("AUSSCHLUSS Bewohner " + ResidentTools.getLabelText(resident) + ": Heimaufnahme weniger als 14 Tage :" + hauf.get().getFrom() + " // " + aufenthaltszeitraumInTagen + " Tage")));
                        } else { // kein Ausschluss
                            residentInfoObjectMap.get(resident).setAusschluss_grund(QdvsResidentInfoObject.MDS_GRUND_KEIN_AUSSCHLUSS);
                        }
                        // todo: sterbephase einbauen (3) Wie definiert man eine Sterbephase ?
                        // todo: KZP einbauen (2)
                    }
                }
        );
        textListener.addLog(SYSConst.html_h2("qdvs.vorpruefung.laeuft.step2"));

        // 2. Durchlauf
        // Prüfen, welche BW noch fehlerhafte Einträge haben
        residentInfoObjectMap.keySet().forEach(resident -> {

            // BW, die bereits von der Auswertung ausgeschlossen wurden, brauchen auch keine weitere Vorprüfung.
            if (residentInfoObjectMap.get(resident).getAusschluss_grund() == QdvsResidentInfoObject.MDS_GRUND_KEIN_AUSSCHLUSS) {
                runningNumber++;
                textListener.setProgress(runningNumber, numResidents * 3, "");

                Set<ResInfoType> vorhandeneTypen = ResInfoTools.getUsedActiveTypesBetween(resident, LETZTE_ERGEBNISERFASSUNG, ERHEBUNGSDATUM);
                boolean schmerze2 = vorhandeneTypen.stream().anyMatch(resInfoType -> resInfoType.getID().equals("schmerze2"));
                boolean besd2 = vorhandeneTypen.stream().anyMatch(resInfoType -> resInfoType.getID().equals("besd2"));

                listeBWFehlerfrei.add(resident);

                if (!(schmerze2 || besd2)) {
                    residentInfoObjectMap.get(resident).addLog("Schmerzeinschätzung fehlt (SCHMERZE2 oder BESD2)");
                    textListener.addLog(SYSConst.html_critical("KRITISCHER FEHLER Bewohner " + ResidentTools.getLabelText(resident) + ": Schmerzeinschätzung fehlt (SCHMERZE2 oder BESD2)"));
                    listeBWFehlerfrei.remove(resident);
                }

                Set<ResInfoType> notwendigeTypen = new HashSet<>(vorhandeneTypen);
                Set<ResInfoType> verboteneTypen = new HashSet<>(vorhandeneTypen);

                notwendigeTypen.retainAll(mandantoryTypes); // alles hier rein, was notwendig und bereits vorhanden ist
                verboteneTypen.retainAll(forbiddenTypes); // alles hier rein, was verboten und immer noch vorhanden ist

                if (notwendigeTypen.size() != mandantoryTypes.size()) { // zu wenig ? dann müssen wir hier die fehlenen reinschreiben
                    HashSet<ResInfoType> copyMandantoryTypes = new HashSet<>(mandantoryTypes);
                    copyMandantoryTypes.removeAll(notwendigeTypen);
                    copyMandantoryTypes.forEach(resInfoType -> {
                        residentInfoObjectMap.get(resident).addLog(SYSTools.xx("qdvs.error.new.type.missing.please.add") + " " + resInfoType);
                        textListener.addLog(SYSConst.html_critical("KRITISCHER FEHLER Bewohner " + ResidentTools.getLabelText(resident) + ": " + SYSTools.xx("qdvs.error.new.type.missing.please.add") + " " + resInfoType));
                    });

                    listeBWFehlerfrei.remove(resident);
                }

                if (verboteneTypen.size() != 0) { // immer noch verbotene ? dann bitte auflisten
                    verboteneTypen.forEach(resInfoType -> residentInfoObjectMap.get(resident).addLog(SYSTools.xx("qdvs.error.old.type.found.please.replace") + " " + resInfoType));
                    listeBWFehlerfrei.remove(resident);
                }

                if (!ResValueTools.getMostRecentBefore(resident, ResvaluetypesService.WEIGHT, ERHEBUNGSDATUM).isPresent()) {
                    residentInfoObjectMap.get(resident).addLog("qdvs.error.weight.missing");
                    textListener.addLog(SYSConst.html_critical("KRITISCHER FEHLER Bewohner " + ResidentTools.getLabelText(resident) + ": " + SYSTools.xx("qdvs.error.weight.missing")));
                    listeBWFehlerfrei.remove(resident);
                }
                if (!ResValueTools.getMostRecentBefore(resident, ResvaluetypesService.HEIGHT, ERHEBUNGSDATUM).isPresent()) {
                    textListener.addLog(SYSConst.html_critical("KRITISCHER FEHLER Bewohner " + ResidentTools.getLabelText(resident) + ": " + SYSTools.xx("qdvs.error.height.missing")));
                    listeBWFehlerfrei.remove(resident);
                }
            }
        });

    }

    /**
     * In diesem Abschnitt wird, jeweils für einen BW, der gesamte Datenabschnitt zusammengestellt.
     *
     * @param resident
     * @return
     */
    private ResidentType createResidentType(Resident resident) {
        getLogger().debug("====================================================================");
        getLogger().debug("====------------------------------------------------------------====");
        getLogger().debug("====                                                            ====");
        getLogger().debug("Indikatoren Ermittlung für Bewohner:in");
        getLogger().debug(ResidentTools.getLabelText(resident) + " (" + NF_IDBEWOHNER.format(resident.getIdbewohner()) + ")");
        ResidentType residentType = of.createResidentType();

        try {

            residentType.setQsData(of.createDasQsDataType());
            getLogger().debug(String.format("===---%s---===", StringUtils.center("Allgemeine Angaben", 60)));
            allgemeine_angaben(residentType.getQsData(), resident);
            getLogger().debug(String.format("===---%s---===", StringUtils.center("Krankenhaus", 60)));
            krankenhaus(residentType.getQsData(), resident);
            getLogger().debug(String.format("===---%s---===", StringUtils.center("Mobilität", 60)));
            mobilitaet(residentType.getQsData(), resident);
            getLogger().debug(String.format("===---%s---===", StringUtils.center("Kognitiv / Kommunikativ", 60)));
            kognitiv_kommunikativ(residentType.getQsData(), resident);
            getLogger().debug(String.format("===---%s---===", StringUtils.center("Selbstversorgung", 60)));
            selbstversorgung(residentType.getQsData(), resident);
            getLogger().debug(String.format("===---%s---===", StringUtils.center("Alltag, Soziales", 60)));
            alltag_soziales(residentType.getQsData(), resident);
            getLogger().debug(String.format("===---%s---===", StringUtils.center("Dekubitus", 60)));

            dekubitus(residentType.getQsData(), resident);

            getLogger().debug(String.format("===---%s---===", StringUtils.center("Größe, Gewicht", 60)));
            groesse_gewicht(residentType.getQsData(), resident);
            getLogger().debug(String.format("===---%s---===", StringUtils.center("Sturz", 60)));
            sturz(residentType.getQsData(), resident);
            getLogger().debug(String.format("===---%s---===", StringUtils.center("Fixierung", 60)));
            fixierung(residentType.getQsData(), resident);
            getLogger().debug(String.format("===---%s---===", StringUtils.center("Schmerzen", 60)));
            schmerzen(residentType.getQsData(), resident);
            getLogger().debug(String.format("===---%s---===", StringUtils.center("Informationen zum Einzug", 60)));
            einzug(residentType.getQsData(), resident);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        getLogger().debug("====                                                            ====");
        getLogger().debug("====------------------------------------------------------------====");
        getLogger().debug("====================================================================");
        getLogger().debug("");
        getLogger().debug("");
        getLogger().debug("");

        return residentType;
    }


    private ResidentType createResidentAusschlussType(Resident resident) {
        getLogger().debug("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        getLogger().debug("XXXX------------------------------------------------------------XXXX");
        getLogger().debug("XXXX                                                            XXXX");
        getLogger().debug("AUSSCHLUSS: " + ResidentTools.getLabelText(resident) + " (" + NF_IDBEWOHNER.format(resident.getIdbewohner()) + ")");
        ResidentType residentType = of.createResidentType();
        DasQsDataMdsType qsMdsData = of.createDasQsDataMdsType();
        residentType.setQsDataMds(qsMdsData);

        /** 1 */qsMdsData.setIDBEWOHNER(of.createDasQsDataMdsTypeIDBEWOHNER());
        qsMdsData.getIDBEWOHNER().setValue(NF_IDBEWOHNER.format(resident.getIdbewohner()));

        Optional<Rooms> room = RoomsService.getRoom(resident, STICHTAG);
        /** 2 */qsMdsData.setWOHNBEREICH(of.createDasQsDataMdsTypeWOHNBEREICH());
        qsMdsData.getWOHNBEREICH().setValue(room.get().getFloor().getName());
        getLogger().debug(room.get().toString());

        // Datum der Erhebung
        /** 3 */qsMdsData.setERHEBUNGSDATUM(of.createDasQsDataMdsTypeERHEBUNGSDATUM());
        qsMdsData.getERHEBUNGSDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(ERHEBUNGSDATUM.toLocalDate()));

        // Datum des Einzugs
        /** 4 */qsMdsData.setEINZUGSDATUM(of.createDasQsDataMdsTypeEINZUGSDATUM());
        qsMdsData.getEINZUGSDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(ResInfoTools.getValidOnThatDayIfAny(resident, HAUF, ERHEBUNGSDATUM).get().getFrom()));

        /** 5 */qsMdsData.setGEBURTSMONAT(of.createDasQsDataMdsTypeGEBURTSMONAT());
        qsMdsData.getGEBURTSMONAT().setValue(JavaTimeConverter.toJavaLocalDateTime(resident.getDob()).getMonthValue());
        /** 6 */qsMdsData.setGEBURTSJAHR(of.createDasQsDataMdsTypeGEBURTSJAHR());
        qsMdsData.getGEBURTSJAHR().setValue(JavaTimeConverter.toJavaLocalDateTime(resident.getDob()).getYear());
        /** 7 */qsMdsData.setGESCHLECHT(of.createDasQsDataMdsTypeGESCHLECHT());
        qsMdsData.getGESCHLECHT().setValue(resident.getGender());

        /** 8 */qsMdsData.setAUSSCHLUSSGRUND(of.createDasQsDataMdsTypeAUSSCHLUSSGRUND());
        qsMdsData.getAUSSCHLUSSGRUND().setValue(residentInfoObjectMap.get(resident).getAusschluss_grund()); // in der map stehen die ausschlussgründe drin

        getLogger().debug("XXXX------------------------------------------------------------XXXX");
        getLogger().debug("XXXX                                                            XXXX");
        getLogger().debug("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");


        return residentType;
    }


    /**
     * Qualitätssicherungsdaten zu Bewohnern und Bewohnerinnen 01 - Allgemeine Angaben
     *
     * @param qsData
     * @param resident
     */
    private void allgemeine_angaben(DasQsDataType qsData, Resident resident) {
        // Bewohnerbezogene Nummer
        qsData.setIDBEWOHNER(of.createDasQsDataTypeIDBEWOHNER());
        qsData.getIDBEWOHNER().setValue(NF_IDBEWOHNER.format(resident.getIdbewohner()));

        Optional<Rooms> room = RoomsService.getRoom(resident, STICHTAG);
        qsData.setWOHNBEREICH(of.createDasQsDataTypeWOHNBEREICH());
        qsData.getWOHNBEREICH().setValue(room.get().getFloor().getName());

        // Datum der Erhebung
        qsData.setERHEBUNGSDATUM(of.createDasQsDataTypeERHEBUNGSDATUM());
        qsData.getERHEBUNGSDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(ERHEBUNGSDATUM.toLocalDate())); // Das Erhebungsdatum ist immer das Datum an dem die Eingabe erfolgte.


        qsData.setGEBURTSMONAT(of.createDasQsDataTypeGEBURTSMONAT());
        qsData.getGEBURTSMONAT().setValue(JavaTimeConverter.toJavaLocalDateTime(resident.getDob()).getMonthValue());
        qsData.setGEBURTSJAHR(of.createDasQsDataTypeGEBURTSJAHR());
        qsData.getGEBURTSJAHR().setValue(JavaTimeConverter.toJavaLocalDateTime(resident.getDob()).getYear());
        qsData.setGESCHLECHT(of.createDasQsDataTypeGESCHLECHT());
        qsData.getGESCHLECHT().setValue(resident.getGender());
        // weiter bei Pflegegrad
        qsData.setPFLEGEGRAD(of.createDasQsDataTypePFLEGEGRAD());
        qsData.getPFLEGEGRAD().setValue(Integer.valueOf(ResInfoTools.getContent(ResInfoTools.getValidOnThatDayIfAny(resident, NINSUR, ERHEBUNGSDATUM).get()).getProperty("grade")));

    }

    private void krankenhaus(DasQsDataType qsData, Resident resident) {

        //____ ___  ____ ___  _    ____ _  _
        //|__| |__] |  | |__] |    |___  \/
        //|  | |    |__| |    |___ |___ _/\_
        /** 9 */qsData.setAPOPLEX(of.createDasQsDataTypeAPOPLEX());
        List<ResInfo> list_apoplex = ResInfoTools.getAll(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_APOPLEX), LETZTE_ERGEBNISERFASSUNG, STICHTAG);
        /** 10 */qsData.setAPOPLEXDATUM(of.createDasQsDataTypeAPOPLEXDATUM());
        qsData.getAPOPLEX().setValue(0);
        if (!list_apoplex.isEmpty()) {
            ResInfo last = list_apoplex.get(list_apoplex.size() - 1);
            LocalDate ereignis = ZonedDateTime.parse(ResInfoTools.getContent(last).getProperty("datum")).toLocalDate();
            if (ereignis.isAfter(LETZTE_ERGEBNISERFASSUNG.toLocalDate())) {
                qsData.getAPOPLEX().setValue(1);
                qsData.getAPOPLEXDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(ereignis));
            }
        }

        //____ ____ ____ _  _ ___ _  _ ____
        //|___ |__/ |__| |_/   |  |  | |__/
        //|    |  \ |  | | \_  |  |__| |  \
        /** 11 */qsData.setFRAKTUR(of.createDasQsDataTypeFRAKTUR());
        // Fraktur
        List<ResInfo> list_fraktur = ResInfoTools.getAll(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_FRAKTUR), LETZTE_ERGEBNISERFASSUNG, STICHTAG);
        /** 12 */qsData.setFRAKTURDATUM(of.createDasQsDataTypeFRAKTURDATUM());
        qsData.getFRAKTUR().setValue(0);
        if (!list_fraktur.isEmpty()) {
            ResInfo last = list_fraktur.get(list_fraktur.size() - 1);
            LocalDate ereignis = ZonedDateTime.parse(ResInfoTools.getContent(last).getProperty("datum")).toLocalDate();
            if (ereignis.isAfter(LETZTE_ERGEBNISERFASSUNG.toLocalDate())) {
                qsData.getFRAKTUR().setValue(1);
                qsData.getFRAKTURDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(ereignis));
            }
        }

        //_  _ ____ ____ ___  _ _  _ ____ ____ ____ _  _ ___
        //|__| |___ |__/   /  | |\ | |___ |__| |__/ |_/   |
        //|  | |___ |  \  /__ | | \| |    |  | |  \ | \_  |
        /** 13 */qsData.setHERZINFARKT(of.createDasQsDataTypeHERZINFARKT());
        List<ResInfo> list_herzinfarkt = ResInfoTools.getAll(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_HERZINFARKT), LETZTE_ERGEBNISERFASSUNG, STICHTAG);
        /** 14 */qsData.setHERZINFARKTDATUM(of.createDasQsDataTypeHERZINFARKTDATUM());
        qsData.getHERZINFARKT().setValue(0);
        if (!list_herzinfarkt.isEmpty()) {
            ResInfo last = list_herzinfarkt.get(list_herzinfarkt.size() - 1);
            LocalDate ereignis = ZonedDateTime.parse(ResInfoTools.getContent(last).getProperty("datum")).toLocalDate();
            if (ereignis.isAfter(LETZTE_ERGEBNISERFASSUNG.toLocalDate())) {
                qsData.getHERZINFARKT().setValue(1);
                qsData.getHERZINFARKTDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(ereignis));
            }
        }

//        if (resident.getId().equalsIgnoreCase("SG3")) {
//            getLogger().debug("hier ist sie");
//        }

        //____ _  _ ___  _  _ ___ ____ ___ _ ____ _  _
        //|__| |\/| |__] |  |  |  |__|  |  | |  | |\ |
        //|  | |  | |    |__|  |  |  |  |  | |__| | \|
        /** 15 */qsData.setAMPUTATION(of.createDasQsDataTypeAMPUTATION());
        List<ResInfo> list_amputation = ResInfoTools.getAll(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_AMPUTATION), LETZTE_ERGEBNISERFASSUNG, STICHTAG); // das kann höchstens einer sein wegen des intervall_mode
        /** 16 */qsData.setAMPUTATIONDATUM(of.createDasQsDataTypeAMPUTATIONDATUM());
        Optional<LocalDate> letzteAmputation = ResInfoTools.getLetzteAmputation(list_amputation.isEmpty() ? null : list_amputation.get(0));
        letzteAmputation.ifPresent(letzte_amp -> {
            qsData.getAMPUTATION().setValue(0);
            if (letzte_amp.isAfter(LETZTE_ERGEBNISERFASSUNG.toLocalDate())) {
                qsData.getAMPUTATION().setValue(1);
                qsData.getAMPUTATIONDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(letzte_amp));
            }
        });

        // Krankenhaus Aufenthalte werden über Abwesenheiten ermittelt.
        List<ResInfo> list_away = ResInfoTools.getAll(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_ABSENCE), LETZTE_ERGEBNISERFASSUNG, STICHTAG);

        int khbehandlung = 0; // 0 = nein  1 = ja, einmal  2 = ja, mehrmals
        int khbanzahlaufenhalte = 0; // min 2, max 10
        int khbanzahltage = 0; // min 2, max 200

        long bisher_laengste_periode = 0;
        Optional<ResInfo> laengsterAufenthalt = Optional.empty();
        for (ResInfo away : list_away) {
            Properties props = ResInfoTools.getContent(away);
            if (props.getProperty("type").equalsIgnoreCase(ResInfoTypeTools.TYPE_ABSENCE_HOSPITAL)) {

                long diese_periode_in_tagen = ChronoUnit.DAYS.between(JavaTimeConverter.toJavaLocalDateTime(away.getFrom()).toLocalDate(), JavaTimeConverter.toJavaLocalDateTime(away.getTo()).toLocalDate()) + 1;
                if (diese_periode_in_tagen > 0) {
                    khbanzahltage += diese_periode_in_tagen;
                    khbanzahlaufenhalte++;
                }

                if (bisher_laengste_periode < diese_periode_in_tagen) {
                    bisher_laengste_periode = diese_periode_in_tagen;
                    laengsterAufenthalt = Optional.of(away);
                }

                getLogger().debug("KH Aufenthalt " + DateFormat.getDateInstance().format(away.getFrom()) + " " + DateFormat.getDateInstance().format(away.getTo()) + " Period " + diese_periode_in_tagen);
                getLogger().debug("Länge der Abwesenheitsperiode: " + diese_periode_in_tagen);
            }
        }

        khbehandlung = (khbanzahlaufenhalte == 0 ? 0 : (khbanzahlaufenhalte > 1 ? 2 : 1));
        /** 17 */qsData.setKHBEHANDLUNG(of.createDasQsDataTypeKHBEHANDLUNG());
        qsData.getKHBEHANDLUNG().setValue(khbehandlung);

        /** 18 */qsData.setKHBEGINNDATUM(of.createDasQsDataTypeKHBEGINNDATUM());
        /** 19 */qsData.setKHENDEDATUM(of.createDasQsDataTypeKHENDEDATUM());
        /** 20 */qsData.setKHBANZAHLAUFENTHALTE(of.createDasQsDataTypeKHBANZAHLAUFENTHALTE());
        /** 21 */qsData.setKHBANZAHLTAGE(of.createDasQsDataTypeKHBANZAHLTAGE());

        if (laengsterAufenthalt.isPresent()) { // <==> khbehandlung > 0
            qsData.getKHBEGINNDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(laengsterAufenthalt.get().getFrom()));
            Date to = SYSCalendar.min(laengsterAufenthalt.get().getTo(), new Date()); // nicht weiter als heute
            qsData.getKHENDEDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(to));
        }

        if (khbehandlung > 0) getLogger().debug("KHBEHANDLUNG " + khbehandlung + "  KHBANZAHLTAGE " + khbanzahltage);
        if (khbehandlung == 2) {
            qsData.getKHBANZAHLAUFENTHALTE().setValue(Math.min(khbanzahlaufenhalte, 10)); // mehrere
            qsData.getKHBANZAHLTAGE().setValue(Math.min(khbanzahltage, 200));
        }


        /** 22 */qsData.setBEATMUNG(of.createDasQsDataTypeBEATMUNG());
        qsData.getBEATMUNG().setValue(0); // default ist NEIN
//        if (resinfos.containsKey(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_RESPIRATION))) {

        qsData.getBEATMUNG().setValue(Integer.valueOf(ResInfoTools.getContent(ResInfoTools.getValidOnThatDayIfAny(resident, RESPIRAT, ERHEBUNGSDATUM).get()).getProperty("BEATMUNG")));
//        }

        /** 23 */qsData.setBEWUSSTSEINSZUSTAND(of.createDasQsDataTypeBEWUSSTSEINSZUSTAND());
        ResInfo bewusst = ResInfoTools.getValidOnThatDayIfAny(resident, BEWUSST, ERHEBUNGSDATUM).get();
        qsData.getBEWUSSTSEINSZUSTAND().setValue(Integer.valueOf(ResInfoTools.getContent(bewusst).getProperty("BEWUSSTSEINSZUSTAND")));

        /** 24 */qsData.getDIAGNOSEN().addAll(getAktuelleDiagnosenFuerQDVS(resident));

    }

    private void mobilitaet(DasQsDataType qsData, Resident resident) {
        // das nennen wir dann letzter_abfragezeitpunkt
        ResInfo mobil = ResInfoTools.getValidOnThatDayIfAny(resident, MOBIL, ERHEBUNGSDATUM).get();

        // 25. Positionswechsel im Bett
        qsData.setMOBILPOSWECHSEL(of.createDasQsDataTypeMOBILPOSWECHSEL());
        qsData.getMOBILPOSWECHSEL().setValue(Integer.valueOf(ResInfoTools.getContent(mobil).getProperty("MOBILPOSWECHSEL")));

        // 26. Halten einer stabilen Sitzposition
        qsData.setMOBILSITZPOSITION(of.createDasQsDataTypeMOBILSITZPOSITION());
        qsData.getMOBILSITZPOSITION().setValue(Integer.valueOf(ResInfoTools.getContent(mobil).getProperty("MOBILSITZPOSITION")));

        // 27. Sich Umsetzen
        qsData.setMOBILUMSETZEN(of.createDasQsDataTypeMOBILUMSETZEN());
        qsData.getMOBILUMSETZEN().setValue(Integer.valueOf(ResInfoTools.getContent(mobil).getProperty("MOBILUMSETZEN")));

        // 28. Fortbewegen innerhalb des Wohnbereichs
        qsData.setMOBILFORTBEWEGUNG(of.createDasQsDataTypeMOBILFORTBEWEGUNG());
        qsData.getMOBILFORTBEWEGUNG().setValue(Integer.valueOf(ResInfoTools.getContent(mobil).getProperty("MOBILFORTBEWEGUNG")));

        // 29. Treppensteigen
        qsData.setMOBILTREPPENSTEIGEN(of.createDasQsDataTypeMOBILTREPPENSTEIGEN());
        qsData.getMOBILTREPPENSTEIGEN().setValue(Integer.valueOf(ResInfoTools.getContent(mobil).getProperty("MOBILTREPPENSTEIGEN")));

    }

    private void kognitiv_kommunikativ(DasQsDataType qsData, Resident resident) {
        ResInfo orient = ResInfoTools.getValidOnThatDayIfAny(resident, ORIENT, ERHEBUNGSDATUM).get();

        // 30. Erkennen von Personen aus dem näheren Umfeld
        qsData.setKKFERKENNEN(of.createDasQsDataTypeKKFERKENNEN());
        qsData.getKKFERKENNEN().setValue(Integer.valueOf(ResInfoTools.getContent(orient).getProperty("KKFERKENNEN")));

        // 31. Örtliche Orientierung
        qsData.setKKFORIENTOERTLICH(of.createDasQsDataTypeKKFORIENTOERTLICH());
        qsData.getKKFORIENTOERTLICH().setValue(Integer.valueOf(ResInfoTools.getContent(orient).getProperty("KKFORIENTOERTLICH")));

        // 32. Zeitliche Orientierung
        qsData.setKKFORIENTZEITLICH(of.createDasQsDataTypeKKFORIENTZEITLICH());
        qsData.getKKFORIENTZEITLICH().setValue(Integer.valueOf(ResInfoTools.getContent(orient).getProperty("KKFORIENTZEITLICH")));

        // 33. Sich Erinnern
        qsData.setKKFERINNERN(of.createDasQsDataTypeKKFERINNERN());
        qsData.getKKFERINNERN().setValue(Integer.valueOf(ResInfoTools.getContent(orient).getProperty("KKFERINNERN")));

        // 34. Steuern von mehrschrittigen Alltagshandlungen
        qsData.setKKFHANDLUNGEN(of.createDasQsDataTypeKKFHANDLUNGEN());
        qsData.getKKFHANDLUNGEN().setValue(Integer.valueOf(ResInfoTools.getContent(orient).getProperty("KKFHANDLUNGEN")));

        // 35. Treffen von Entscheidungen im Alltagsleben
        qsData.setKKFENTSCHEIDUNGEN(of.createDasQsDataTypeKKFENTSCHEIDUNGEN());
        qsData.getKKFENTSCHEIDUNGEN().setValue(Integer.valueOf(ResInfoTools.getContent(orient).getProperty("KKFENTSCHEIDUNGEN")));

        // 36. Verstehen von Sachverhalten und Informationen
        qsData.setKKFVERSTEHENINFO(of.createDasQsDataTypeKKFVERSTEHENINFO());
        qsData.getKKFVERSTEHENINFO().setValue(Integer.valueOf(ResInfoTools.getContent(orient).getProperty("KKFVERSTEHENINFO")));

        // 37. Erkennen von Risiken und Gefahren
        qsData.setKKFGEFAHRERKENNEN(of.createDasQsDataTypeKKFGEFAHRERKENNEN());
        qsData.getKKFGEFAHRERKENNEN().setValue(Integer.valueOf(ResInfoTools.getContent(orient).getProperty("KKFGEFAHRERKENNEN")));

        // 38. Mitteilen von elementaren Bedürfnissen
        qsData.setKKFMITTEILEN(of.createDasQsDataTypeKKFMITTEILEN());
        qsData.getKKFMITTEILEN().setValue(Integer.valueOf(ResInfoTools.getContent(orient).getProperty("KKFMITTEILEN")));

        // 39. Verstehen von Aufforderungen
        qsData.setKKFVERSTEHENAUF(of.createDasQsDataTypeKKFVERSTEHENAUF());
        qsData.getKKFVERSTEHENAUF().setValue(Integer.valueOf(ResInfoTools.getContent(orient).getProperty("KKFVERSTEHENAUF")));

        // 40. Beteiligung an einem Gespräch
        qsData.setKKFBETEILIGUNG(of.createDasQsDataTypeKKFBETEILIGUNG());
        qsData.getKKFBETEILIGUNG().setValue(Integer.valueOf(ResInfoTools.getContent(orient).getProperty("KKFBETEILIGUNG")));

    }

    private void selbstversorgung(DasQsDataType qsData, Resident resident) {
        //╻┏ ╻ ╻┏━╸┏┓╻┏━┓╺┳╸╻  ╻┏━╸╻ ╻┏━╸   ┏━╸┏━┓┏┓╻┏━┓┏━╸╻ ╻┏━┓╻ ╻┏┓╻┏━╸
        //┣┻┓┃ ┃┣╸ ┃┗┫┗━┓ ┃ ┃  ┃┃  ┣━┫┣╸    ┣╸ ┣┳┛┃┗┫┣━┫┣╸ ┣━┫┣┳┛┃ ┃┃┗┫┃╺┓
        //╹ ╹┗━┛┗━╸╹ ╹┗━┛ ╹ ┗━╸╹┗━╸╹ ╹┗━╸   ┗━╸╹┗╸╹ ╹╹ ╹┗━╸╹ ╹╹┗╸┗━┛╹ ╹┗━┛
        /** 41 */qsData.setSVERNAEHRUNG(of.createDasQsDataTypeSVERNAEHRUNG());
        /** 42 */qsData.setSVFREMDHILFE(of.createDasQsDataTypeSVFREMDHILFE());
        /** 43 */qsData.setSVERNAEHRUNGUMFANG(of.createDasQsDataTypeSVERNAEHRUNGUMFANG());

        // "kern01" künstliche Ernährung ist eine optionale resinfo. Falls sie nicht existiert, wird Frage 41. einfach auf 0 gesetzt.
        Optional<ResInfo> kern = ResInfoTools.getValidOnThatDayIfAny(resident, KERN, ERHEBUNGSDATUM);
        if (kern.isPresent()) { // Sobald ein kern01 Eintrag Vorliergt ist die SVERNAEHRUNG gesetzt.
            qsData.getSVERNAEHRUNG().setValue(1);
            int svfremdhilfe = Integer.valueOf(ResInfoTools.getContent(kern.get()).getProperty("SVFREMDHILFE"));
            qsData.getSVFREMDHILFE().setValue(svfremdhilfe);
            if (svfremdhilfe == 1) {
                int svernaehrungumfang = Integer.valueOf(ResInfoTools.getContent(kern.get()).getProperty("SVERNAEHRUNGUMFANG"));
                qsData.getSVERNAEHRUNGUMFANG().setValue(svernaehrungumfang);
            }
        } else {
            qsData.getSVERNAEHRUNG().setValue(0);
        }

        //┏━┓╻ ╻┏━┓┏━┓┏━╸╻ ╻┏━╸╻╺┳┓╻ ╻┏┓╻┏━╸┏━╸┏┓╻
        //┣━┫┃ ┃┗━┓┗━┓┃  ┣━┫┣╸ ┃ ┃┃┃ ┃┃┗┫┃╺┓┣╸ ┃┗┫
        //╹ ╹┗━┛┗━┛┗━┛┗━╸╹ ╹┗━╸╹╺┻┛┗━┛╹ ╹┗━┛┗━╸╹ ╹
        ResInfo aus = ResInfoTools.getValidOnThatDayIfAny(resident, AUSSCHEID, ERHEBUNGSDATUM).get();
        /** 44 */qsData.setSVHARNKONTINENZ(of.createDasQsDataTypeSVHARNKONTINENZ());
        qsData.getSVHARNKONTINENZ().setValue(Integer.valueOf(ResInfoTools.getContent(aus).getProperty("SVHARNKONTINENZ")));
        /** 45 */qsData.setSVSTUHLKONTINENZ(of.createDasQsDataTypeSVSTUHLKONTINENZ());
        qsData.getSVSTUHLKONTINENZ().setValue(Integer.valueOf(ResInfoTools.getContent(aus).getProperty("SVSTUHLKONTINENZ")));
        /** 55 */qsData.setSVTOILETTE(of.createDasQsDataTypeSVTOILETTE());
        qsData.getSVTOILETTE().setValue(Integer.valueOf(ResInfoTools.getContent(aus).getProperty("SVTOILETTE")));
        /** 56 */qsData.setSVHARNKONTINENZBEW(of.createDasQsDataTypeSVHARNKONTINENZBEW());
        if (qsData.getSVHARNKONTINENZ().getValue() >= 2) { // wenn Feld 44 = 2, 3 oder 4
            qsData.getSVHARNKONTINENZBEW().setValue(Integer.valueOf(ResInfoTools.getContent(aus).getProperty("SVHARNKONTINENZBEW")));
        }
        /** 57 */qsData.setSVSTUHLKONTINENZBEW(of.createDasQsDataTypeSVSTUHLKONTINENZBEW());
        if (qsData.getSVSTUHLKONTINENZ().getValue() >= 2) { // wenn Feld 45 = 2, 3 oder 4
            qsData.getSVSTUHLKONTINENZBEW().setValue(Integer.valueOf(ResInfoTools.getContent(aus).getProperty("SVSTUHLKONTINENZBEW")));
        }

        //┏┓ ┏━╸╻ ╻┏━╸┏━┓╺┳╸╻ ╻┏┓╻┏━╸
        //┣┻┓┣╸ ┃╻┃┣╸ ┣┳┛ ┃ ┃ ┃┃┗┫┃╺┓
        //┗━┛┗━╸┗┻┛┗━╸╹┗╸ ╹ ┗━┛╹ ╹┗━┛
        ResInfo kpflege = ResInfoTools.getValidOnThatDayIfAny(resident, KPFLEGE, ERHEBUNGSDATUM).get();
        /** 46 */qsData.setSVOBERKOERPER(of.createDasQsDataTypeSVOBERKOERPER());
        qsData.getSVOBERKOERPER().setValue(Integer.valueOf(ResInfoTools.getContent(kpflege).getProperty("SVOBERKOERPER")));
        /** 47 */qsData.setSVKOPF(of.createDasQsDataTypeSVKOPF());
        qsData.getSVKOPF().setValue(Integer.valueOf(ResInfoTools.getContent(kpflege).getProperty("SVKOPF")));
        /** 48 */qsData.setSVINTIMBEREICH(of.createDasQsDataTypeSVINTIMBEREICH());
        qsData.getSVINTIMBEREICH().setValue(Integer.valueOf(ResInfoTools.getContent(kpflege).getProperty("SVINTIMBEREICH")));
        /** 49 */qsData.setSVDUSCHENBADEN(of.createDasQsDataTypeSVDUSCHENBADEN());
        qsData.getSVDUSCHENBADEN().setValue(Integer.valueOf(ResInfoTools.getContent(kpflege).getProperty("SVDUSCHENBADEN")));
        /** 50 */qsData.setSVANAUSOBERKOERPER(of.createDasQsDataTypeSVANAUSOBERKOERPER());
        qsData.getSVANAUSOBERKOERPER().setValue(Integer.valueOf(ResInfoTools.getContent(kpflege).getProperty("SVANAUSOBERKOERPER")));
        /** 51 */qsData.setSVANAUSUNTERKOERPER(of.createDasQsDataTypeSVANAUSUNTERKOERPER());
        qsData.getSVANAUSUNTERKOERPER().setValue(Integer.valueOf(ResInfoTools.getContent(kpflege).getProperty("SVANAUSUNTERKOERPER")));

        ResInfo ern = ResInfoTools.getValidOnThatDayIfAny(resident, ERN, ERHEBUNGSDATUM).get();
        /** 52 */qsData.setSVNAHRUNGZUBEREITEN(of.createDasQsDataTypeSVNAHRUNGZUBEREITEN());
        qsData.getSVNAHRUNGZUBEREITEN().setValue(Integer.valueOf(ResInfoTools.getContent(ern).getProperty("SVNAHRUNGZUBEREITEN")));
        /** 53 */qsData.setSVESSEN(of.createDasQsDataTypeSVESSEN());
        qsData.getSVESSEN().setValue(Integer.valueOf(ResInfoTools.getContent(ern).getProperty("SVESSEN")));
        /** 54 */qsData.setSVTRINKEN(of.createDasQsDataTypeSVTRINKEN());
        qsData.getSVTRINKEN().setValue(Integer.valueOf(ResInfoTools.getContent(ern).getProperty("SVTRINKEN")));
    }

    private void alltag_soziales(DasQsDataType qsData, Resident resident) {
        ResInfo alltag = ResInfoTools.getValidOnThatDayIfAny(resident, ALLTAG, ERHEBUNGSDATUM).get();
        ResInfo schlaf = ResInfoTools.getValidOnThatDayIfAny(resident, SCHLAF, ERHEBUNGSDATUM).get();

        /** 58 */qsData.setGATAGESABLAUF(of.createDasQsDataTypeGATAGESABLAUF());
        qsData.getGATAGESABLAUF().setValue(Integer.valueOf(ResInfoTools.getContent(alltag).getProperty("GATAGESABLAUF")));
        /** 59 */qsData.setGARUHENSCHLAFEN(of.createDasQsDataTypeGARUHENSCHLAFEN());
        qsData.getGARUHENSCHLAFEN().setValue(Integer.valueOf(ResInfoTools.getContent(schlaf).getProperty("GARUHENSCHLAFEN")));
        /** 60 */qsData.setGABESCHAEFTIGEN(of.createDasQsDataTypeGABESCHAEFTIGEN());
        qsData.getGABESCHAEFTIGEN().setValue(Integer.valueOf(ResInfoTools.getContent(alltag).getProperty("GABESCHAEFTIGEN")));
        /** 61 */qsData.setGAPLANUNGEN(of.createDasQsDataTypeGAPLANUNGEN());
        qsData.getGAPLANUNGEN().setValue(Integer.valueOf(ResInfoTools.getContent(alltag).getProperty("GAPLANUNGEN")));

        ResInfo sozial = ResInfoTools.getValidOnThatDayIfAny(resident, SOZIAL, ERHEBUNGSDATUM).get();
        /** 62 */qsData.setGAINTERAKTION(of.createDasQsDataTypeGAINTERAKTION());
        qsData.getGAINTERAKTION().setValue(Integer.valueOf(ResInfoTools.getContent(sozial).getProperty("GAINTERAKTION")));
        /** 63 */qsData.setGAKONTAKTPFLEGE(of.createDasQsDataTypeGAKONTAKTPFLEGE());
        qsData.getGAKONTAKTPFLEGE().setValue(Integer.valueOf(ResInfoTools.getContent(sozial).getProperty("GAKONTAKTPFLEGE")));
    }

    /**
     * Hatte der Bewohner bzw. die Bewohnerin in der Zeit seit der letzten Ergebniserfassung einen Dekubitus?
     * <p>
     * Gemeint sind alle Dekubitalulcera, die in den vergangenen 6 Monaten beim Bewohner bzw. bei der Bewohnerin
     * bestanden oder bis heute bestehen. Auch wenn der Zeitpunkt der Entstehung länger als 6 Monate zurückliegt, der
     * Dekubitus aber noch nicht abgeheilt war, ist die Frage mit „ja“ zu beantworten und das Entstehungsdatum
     * anzugeben.
     * <p>
     * Wenn es mehr als einen Dekubitus gab, dann werden davon maximal 2 übermittelt. Gab es mehr als zwei
     * Dekubitusepisoden in den letzten 6 Monaten, sind die beiden zeitlich letzten zu berücksichtigen.
     *
     * @param qsData
     * @param resident
     */
    private void dekubitus(DasQsDataType qsData, Resident resident) {
        // zuerst hole ich alle Wunden, die in dem besagten Zeitraum bestanden haben.
        ArrayList<ResInfo> liste_wunden = ResInfoTools.getAll(resident, ResInfoTypeTools.TYPE_ALL_WOUNDS, LETZTE_ERGEBNISERFASSUNG, STICHTAG);

        // Dann ermittele ich alle ConnectionIDs, die es in diesem Zeitraum gab.
        // Denn der Wundbeginn könnte ja auch VOR dem Betrachtungszeitraum liegen
        HashSet<Long> connectionIDs = new HashSet<>();

        // Die statistischen Informationen sammele ich pro ConnectionID in einem Quartet (connectionid, entstehungsort, max_grade, beginn (der wunde), ende (der wunde) - BAW wenn noch aktiv)
        // Das sammele ich dann in dieser Map, der key ist die ConnectionID und der Value die zugehörige statistik
        // (ConnectionID, entehungsort, grad, beginn, ende)
        HashMap<Long, Quintet<Long, Integer, Integer, LocalDateTime, LocalDateTime>> auswertung_dekubitus = new HashMap<>();
        liste_wunden.forEach(resInfo -> {
            connectionIDs.add(resInfo.getConnectionid());
            auswertung_dekubitus.put(resInfo.getConnectionid(), Quintet.with(resInfo.getConnectionid(), 1, 0, SYSConst.LD_UNTIL_FURTHER_NOTICE, SYSConst.LD_VERY_BEGINNING)); // Basisfall, 1 ist bei uns. Wird aber sowieso überschrieben.
        });
        getLogger().debug(String.format("%d Wunden gefunden", liste_wunden.size()));

        // Jetzt nehme ich mir jeden Wundverlauf einzeln vor und möchte wissen:
        // entstehungsort
        // maximaler Grad
        // Beginn der Wunde.
        // Ende der Wunde (oder noch nicht beendet)
        // mich interessieren nur dekubitus einträge
        connectionIDs.forEach(aConnectionID -> {
            ArrayList<ResInfo> wundverlauf = ResInfoTools.getAll(aConnectionID);

            wundverlauf.forEach(resInfo -> {
                Properties props = ResInfoTools.getContent(resInfo);
                // uns interessieren hier nur die Dekubitalgeschwüre
                if (props.getProperty("dekubitus").equalsIgnoreCase("true")) {
                    int dekubituslok = Integer.parseInt(props.getProperty("dekubituslok"));
                    int epuap = Integer.parseInt(props.getProperty("epuap"));
                    LocalDateTime beginn = JavaTimeConverter.toJavaLocalDateTime(resInfo.getFrom());
                    LocalDateTime ende = JavaTimeConverter.toJavaLocalDateTime(resInfo.getTo());

                    // was stand vorher drin ?
                    Quintet<Long, Integer, Integer, LocalDateTime, LocalDateTime> auswertung = auswertung_dekubitus.get(aConnectionID);
                    // höchster wert, frühesten beginn, spätestes ende suchen

                    int max_epuap = Math.max(epuap, auswertung.getValue2());
                    LocalDateTime _beginn = JavaTimeConverter.min(beginn, auswertung.getValue3());
                    LocalDateTime _ende = JavaTimeConverter.max(ende, auswertung.getValue4());

                    auswertung_dekubitus.put(aConnectionID, Quintet.with(aConnectionID, dekubituslok, max_epuap, _beginn, _ende));
                } else {
                    auswertung_dekubitus.remove(aConnectionID); // kein dekubitus -> kein interesse
                }
            });
        });

        // sortiere die Liste nach startdatum (zeitlich letzten) - (ConnectionID, entehungsort, grad, beginn - Value3, ende)
        ArrayList<Quintet<Long, Integer, Integer, LocalDateTime, LocalDateTime>> listDekubitus = new ArrayList<>(auswertung_dekubitus.values().stream().sorted(Comparator.comparing(Quintet::getValue3)).collect(Collectors.toList()));
        // danach drehe ich die Liste um und nehme mir die ersten beiden, falls vorhanden.
        Collections.reverse(listDekubitus);
        // Aus dieser Liste nehme ich nun die ersten beiden Einträge, wenn es denn welche gibt.
        Optional<Quintet<Long, Integer, Integer, LocalDateTime, LocalDateTime>> optWunde1 = Optional.ofNullable(listDekubitus.size() >= 1 ? listDekubitus.get(0) : null);
        Optional<Quintet<Long, Integer, Integer, LocalDateTime, LocalDateTime>> optWunde2 = Optional.ofNullable(listDekubitus.size() > 1 ? listDekubitus.get(1) : null);

        // Formalitäten
        /** 64 */qsData.setDEKUBITUS(of.createDasQsDataTypeDEKUBITUS());
        /** 65 */qsData.setDEKUBITUSSTADIUM(of.createDasQsDataTypeDEKUBITUSSTADIUM());
        /** 66 */qsData.setDEKUBITUS1BEGINNDATUM(of.createDasQsDataTypeDEKUBITUS1BEGINNDATUM());
        /** 67 */qsData.setDEKUBITUS1ENDEDATUM(of.createDasQsDataTypeDEKUBITUS1ENDEDATUM());
        /** 68 */qsData.setDEKUBITUS1LOK(of.createDasQsDataTypeDEKUBITUS1LOK());
        /** 69 */qsData.setDEKUBITUS2BEGINNDATUM(of.createDasQsDataTypeDEKUBITUS2BEGINNDATUM());
        /** 70 */qsData.setDEKUBITUS2ENDEDATUM(of.createDasQsDataTypeDEKUBITUS2ENDEDATUM());
        /** 71 */qsData.setDEKUBITUS2LOK(of.createDasQsDataTypeDEKUBITUS2LOK());

        // Die Analyse der XML Auswertungen hat ergeben, dass wenn der maximale Wert für DEKUBITUS >1, dann werden alle Beginn- und Enddaten erwartet. Auch wenn eine von den beiden Wunden nicht über 1 hinausgekommen ist.
        int maximales_dekubitus_stadium = optWunde1.isPresent() ? optWunde1.get().getValue2() : 0;
        maximales_dekubitus_stadium = Math.max(maximales_dekubitus_stadium, optWunde2.isPresent() ? optWunde2.get().getValue2() : 0);

        int dekubitus_schluessel = 0; // wenn kein dekubitus vorhanden
        if (listDekubitus.size() == 1) dekubitus_schluessel = 1; // genau einer
        if (listDekubitus.size() > 1) dekubitus_schluessel = 2; // mehr als einer

        if (maximales_dekubitus_stadium > 1) { // Zu Dekubitus in Kategorie/Stadium 1 sollen keine Datumsangaben gemacht werden.
            optWunde1.ifPresent(wunde1 -> {
                qsData.getDEKUBITUS1BEGINNDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(wunde1.getValue3()));
                // Falls der Dekubitus zum Stichtag noch besteht, bitte den Stichtag angeben.
                qsData.getDEKUBITUS1ENDEDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(JavaTimeConverter.min(wunde1.getValue4().toLocalDate(), STICHTAG.toLocalDate())));
                qsData.getDEKUBITUS1LOK().setValue(wunde1.getValue1());
            });

            optWunde2.ifPresent(wunde2 -> {
                qsData.getDEKUBITUS2BEGINNDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(wunde2.getValue3()));
                // Falls der Dekubitus zum Stichtag noch besteht, bitte den Stichtag angeben.
                qsData.getDEKUBITUS2ENDEDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(JavaTimeConverter.min(wunde2.getValue4().toLocalDate(), STICHTAG.toLocalDate())));
                qsData.getDEKUBITUS2LOK().setValue(wunde2.getValue1());
            });
        }

        if (dekubitus_schluessel > 0) {
            qsData.getDEKUBITUSSTADIUM().setValue(maximales_dekubitus_stadium);
        }
        qsData.getDEKUBITUS().setValue(dekubitus_schluessel);

    }

    private void groesse_gewicht(DasQsDataType qsData, Resident resident) {
        ResValue weight = ResValueTools.getMostRecentBefore(resident, ResvaluetypesService.WEIGHT, ERHEBUNGSDATUM).get();
        ResValue height = ResValueTools.getMostRecentBefore(resident, ResvaluetypesService.HEIGHT, ERHEBUNGSDATUM).get();

        getLogger().debug("Gewicht: " + weight.toString());
        getLogger().debug("Größe: " + height.toString());

        /** 72 */qsData.setKOERPERGROESSE(of.createDasQsDataTypeKOERPERGROESSE());
        qsData.getKOERPERGROESSE().setValue(height.getVal1().multiply(BigDecimal.valueOf(100)).intValue()); // umrechnung von meter in centimeter

        /** 73 */qsData.setKOERPERGEWICHT(of.createDasQsDataTypeKOERPERGEWICHT());
        qsData.getKOERPERGEWICHT().setValue(weight.getVal1());

        /** 74 */qsData.setKOERPERGEWICHTDATUM(of.createDasQsDataTypeKOERPERGEWICHTDATUM());
        qsData.getKOERPERGEWICHTDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(weight.getPit()));


        /** 75 */
        // Ich suche den letzten ResInfo mit einem Gewichts Kommentar. Wenn es einen gibt
        // dann werte ich den aus. Wenn nicht ist das Ergebnis 0, wenn er älter als das letzte Datum
        // ist setze ich den ebenfalls auf 0.
        Optional<ResInfo> gewichtdoku = ResInfoTools.getValidOnThatDayIfAny(resident, KOERPERGEWICHTDOKU, ERHEBUNGSDATUM);
        // wenn sie fehlt oder zu alt ist, dann setzen wir das auf 0
        if (!gewichtdoku.isPresent() || JavaTimeConverter.toJavaLocalDateTime(gewichtdoku.get().getFrom()).isBefore(LETZTE_ERGEBNISERFASSUNG)) {
            DasQsDataType.KOERPERGEWICHTDOKU kd = of.createDasQsDataTypeKOERPERGEWICHTDOKU();
            kd.setValue(0);
            qsData.getKOERPERGEWICHTDOKU().add(kd);
            getLogger().debug("KEINE GewichtsDoku (oder zu alt)");
        } else {
            Properties props = ResInfoTools.getContent(gewichtdoku.get());
            props.forEach((key, value) -> {
                if (value.toString().equalsIgnoreCase("true")) { // für jede checkbox selected steht eine der 5 Zahlen (1-5)
                    DasQsDataType.KOERPERGEWICHTDOKU kd = of.createDasQsDataTypeKOERPERGEWICHTDOKU();
                    kd.setValue(Integer.valueOf(key.toString()));
                    qsData.getKOERPERGEWICHTDOKU().add(kd);
                }
            });
            // Falls der Eintrag vorhanden, aber leer ist, dann wird auch das hier berücksichtigt.
            if (qsData.getKOERPERGEWICHTDOKU().isEmpty()) {
                DasQsDataType.KOERPERGEWICHTDOKU kd = of.createDasQsDataTypeKOERPERGEWICHTDOKU();
                kd.setValue(0);
                qsData.getKOERPERGEWICHTDOKU().add(kd);
                getLogger().debug("Eintrag vorhanden, aber nicht angekreuzt. Gilt als 0");
            }
        }
    }

    private void sturz(DasQsDataType qsData, Resident resident) {
        // die sind nach FROM sortiert
        // die stürze werden teilweise aus fallprot01 und aus strzfolg01 ermittelt
        ArrayList<ResInfo> stuerze = ResInfoTools.getAll(resident, FALLTYPE, LETZTE_ERGEBNISERFASSUNG, STICHTAG);

        /** 76 */qsData.setSTURZ(of.createDasQsDataTypeSTURZ());
        int sturz = 0;
        if (stuerze.size() == 1) sturz = 1;
        if (stuerze.size() > 1) sturz = 2;
        qsData.getSTURZ().setValue(sturz);

        /** 77 */
        if (sturz > 0) { // es gab also mindestens einen Sturz
            // erhöter Unterstützungsbedarf
            ArrayList<ResInfo> auswirkungen = ResInfoTools.getAll(resident, FALLAUSWIRKUNG, LETZTE_ERGEBNISERFASSUNG, ERHEBUNGSDATUM);
            if (!auswirkungen.isEmpty()) {
                Properties auswirkung_props = ResInfoTools.getContent(auswirkungen.get(auswirkungen.size() - 1));
                if (auswirkung_props.getProperty("erhoehter_bedarf_alltag").equalsIgnoreCase("true")) {
                    DasQsDataType.STURZFOLGEN alltag = of.createDasQsDataTypeSTURZFOLGEN();
                    alltag.setValue(3);
                    qsData.getSTURZFOLGEN().add(alltag);
                }
                if (auswirkung_props.getProperty("erhoehter_bedarf_mobilitaet").equalsIgnoreCase("true")) {
                    DasQsDataType.STURZFOLGEN mobilitaet = of.createDasQsDataTypeSTURZFOLGEN();
                    mobilitaet.setValue(4);
                    qsData.getSTURZFOLGEN().add(mobilitaet);
                }
            }
            // Auswertung für Frakturen oder Arzt Kontakt
            boolean frakturen = false;
            boolean arzt = false;

            // schlimmste auswirkung über Sturzprotokolle muss ermittelt werden
            for (ResInfo s : stuerze) { // alle stürze durchsuchen und das schlimmste einsammlen (daher auch die OR verknüpfung)
                Properties props = ResInfoTools.getContent(s);
                frakturen |= props.getProperty("fracture", "false").equalsIgnoreCase("true");
                // ich gehe davon aus, dass ARZT Kontakt notwendig war, wenn KH oder Arzt angeklickt wurden.
                arzt |= props.getProperty("gp", "false").equalsIgnoreCase("true");
                arzt |= props.getProperty("hospital", "false").equalsIgnoreCase("true");
            }
            if (frakturen) {
                DasQsDataType.STURZFOLGEN sf = of.createDasQsDataTypeSTURZFOLGEN();
                sf.setValue(1);
                qsData.getSTURZFOLGEN().add(sf);
            }
            if (arzt) {
                DasQsDataType.STURZFOLGEN sf = of.createDasQsDataTypeSTURZFOLGEN();
                sf.setValue(2);
                qsData.getSTURZFOLGEN().add(sf);
            }
            // Falls bisher nichts zutreffend, setzen wir eine 0 ein. Verlangt das Schema so.
            if (qsData.getSTURZFOLGEN().isEmpty()) {
                DasQsDataType.STURZFOLGEN sf = of.createDasQsDataTypeSTURZFOLGEN();
                sf.setValue(0);
                qsData.getSTURZFOLGEN().add(sf);
            }
        } else { // leer wenn es keinen Sturz gab
            DasQsDataType.STURZFOLGEN sf = of.createDasQsDataTypeSTURZFOLGEN();
            qsData.getSTURZFOLGEN().add(sf);
        }


    }

    private void fixierung(DasQsDataType qsData, Resident resident) {
        // Frage 78 bzw. 80 beziehe ich auf die letzten 4 Wochen.
        // Frage 79 und 81 dann nur auf die letzte Woche
        // vom Support und VDAB kamen keine brauchabaren Antworten. Ist im Gesetz nicht genau dargelegt.
        int gurt_haeufigkeit = 10;
        int gitter_haeufigkeit = 10;
        boolean woche_ohne_gurte = false;
        boolean woche_ohne_gitter = false;

        for (int weekminus = 1; weekminus <= 4; weekminus++) {
            // wie oft wurde in der genannten Woche mit Gurt oder Bettseitenteil fixiert ?
            Pair<Integer, Integer> result = analyse_fixierung_innerhalb_einer_woche(resident, STICHTAG.minusWeeks(weekminus).toLocalDate());  // (gurt_häufigkeit, bettgitter_häufigkeit)
            gurt_haeufigkeit = Math.min(gurt_haeufigkeit, result.getLeft());
            woche_ohne_gurte |= result.getLeft() == 10; // 10 steht für keine verwendung von fixierungen. Hat technische gründe, weil kleinere Zahlen im DAS Bogen für Häufiger stehen.

            gitter_haeufigkeit = Math.min(gitter_haeufigkeit, result.getRight());
            woche_ohne_gitter |= result.getRight() == 10; // 10 steht für keine verwendung von fixierungen.
        }


        /** 78 */qsData.setGURT(of.createDasQsDataTypeGURT());
        qsData.getGURT().setValue(gurt_haeufigkeit == 10 ? 0 : 1); // umwandlung der Häufigkeit auf eine JA/NEIN Antwort
        /** 79 */qsData.setGURTHAUFIGKEIT(of.createDasQsDataTypeGURTHAUFIGKEIT());
        if (gurt_haeufigkeit < 10) { // es gab also gurt fixierungen
            // das ist der kleineste Wert, der über die Wochen ermittelt wurde. Je kleiner, je häufiger.
            // gibt es aber nur eine Woche in der nicht fixiert wurde, dann nehmen wir 4 = seltener als 1x wöchentlich.
            // analyse_fixierung() ermittelt nur die häufigkeit INNERHALB einer Woche. Die übergeordnete
            // Häufigkeit wird hier berechnet.
            qsData.getGURTHAUFIGKEIT().setValue(woche_ohne_gurte ? 4 : gurt_haeufigkeit);
        }

        /** 80 */qsData.setSEITENTEILE(of.createDasQsDataTypeSEITENTEILE());
        qsData.getSEITENTEILE().setValue(gitter_haeufigkeit == 10 ? 0 : 1);
        /** 81 */qsData.setSEITENTEILEHAUFIGKEIT(of.createDasQsDataTypeSEITENTEILEHAUFIGKEIT());
        if (gitter_haeufigkeit < 10) {
            qsData.getSEITENTEILEHAUFIGKEIT().setValue(woche_ohne_gitter ? 4 : gitter_haeufigkeit);
        }
    }

    /**
     * Ermittelt wie oft innerhalb einer Woche fixiert wurde (Bettseitenteil oder Gurt). Wird bestimmt anhand
     * ResInfoTypeTools.TYPE_FIXIERUNGPROTOKOLL.
     *
     * @param resident
     * @param tag_in_der_woche - ein Tag in dieser Woche.
     * @return ein Pair bestehend aus (gurt_häufigkeit, bettgitter_häufigkeit). Häufigkeit ist dann: 10 = gar nicht, 2 =
     * mehrmals wöchentlich // 3 = 1x wöchentlich
     */
    private Pair<Integer, Integer> analyse_fixierung_innerhalb_einer_woche(Resident resident, LocalDate tag_in_der_woche) {
        LocalDateTime start = tag_in_der_woche.with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime end = tag_in_der_woche.with(DayOfWeek.SUNDAY).atTime(23, 59, 59);

        final Set<LocalDate> gurtList = new HashSet();
        final Set bettgitterList = new HashSet();
        for (ResInfo resInfo : ResInfoTools.getAll(resident, FIXIERUNGSPROTOKOLLE, start, end)) {
            LocalDateTime pit = JavaTimeConverter.toJavaLocalDateTime(resInfo.getFrom());
            Properties props = ResInfoTools.getContent(resInfo);
            if (props.getProperty("leibgurt", "false").equalsIgnoreCase("true") || props.getProperty("sitzgurt", "false").equalsIgnoreCase("true")) {
                gurtList.add(pit.toLocalDate());
            }
            if (props.getProperty("bettgitter", "false").equalsIgnoreCase("true")) {
                bettgitterList.add(pit.toLocalDate());
            }
        }

        int gurt = 0;
        int seitengitter = 0;

        if (gurtList.isEmpty()) gurt = 10; // gar nicht. Nehme die 10, weil hier gilt, je kleiner desto häufiger.
        if (gurtList.size() == 1) gurt = 3; // 1x wöchentlich
        if (gurtList.size() > 1) gurt = 2; // mehrmals wöchentlich
        if (gurtList.size() == 7) gurt = 1; // täglich

        if (bettgitterList.isEmpty()) seitengitter = 10;
        if (bettgitterList.size() == 1) seitengitter = 3;
        if (bettgitterList.size() > 1) seitengitter = 2;
        if (bettgitterList.size() == 7) seitengitter = 1;

        return new ImmutablePair<>(gurt, seitengitter);
    }

    /**
     * diese methode ermittelt die Schmerzsituation. Dabei wird die Frage nach "Schmerzfrei durch Medikamente" NUR
     * berücksichtigt bei einem NRS > 3. Ansonsten stellt sich die Frage nicht und würde auch bei der Auswertung zu
     * einem Validationsfehler #60047 führen.
     *
     * @param qsData
     * @param resident
     */
    private void schmerzen(DasQsDataType qsData, Resident resident) {
        int SCHMERZEN = 0;
        int SCHMERZFREI = 0; // durch medikamente

        // das macht keinen sinn. nur der letzte Eintrag zählt
        int max = 0;
        LocalDate pit = null;

        // Letzten Eintrag suchen

        List<ResInfo> list_schmerz = ResInfoTools.getAll(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_PAIN), LETZTE_ERGEBNISERFASSUNG, STICHTAG); // DESC sortiert
        List<ResInfo> list_besd = ResInfoTools.getAll(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_BESD), LETZTE_ERGEBNISERFASSUNG, STICHTAG); // DESC sortiert

        Optional<ResInfo> letzter_schmerz = list_schmerz.stream().findFirst();
        Optional<ResInfo> letzter_besd = list_besd.stream().findFirst();

        // falls es mehrere Einträge gibt, dann verwende ich immer den aktuelleren (also falls ein BESD und ein SCHMERZE2 vorliegt.
        // einer von beiden muss mindestens da sein
        boolean verwende_schmerze2_zur_auswertung;
        if (letzter_besd.isPresent() && letzter_schmerz.isPresent()) { // sehr seltener Fall
            verwende_schmerze2_zur_auswertung = letzter_besd.get().getFrom().compareTo(letzter_schmerz.get().getFrom()) < 0;
        } else {
            verwende_schmerze2_zur_auswertung = letzter_schmerz.isPresent();
        }

        if (verwende_schmerze2_zur_auswertung) { // bei einer NRS Angabe durch den BW selbst
            Properties props = ResInfoTools.getContent(letzter_schmerz.get());
            int nrs = Integer.valueOf(props.getProperty("schmerzint"));
            boolean chronisch = props.getProperty("schmerztyp", "0").equalsIgnoreCase("1");
            // Schmerzen hat er dann, wenn sie chronisch und NRS >3 sind.
            /**
             * Es geht in dieser Frage um die Feststellung, ob überhaupt eine Schmerzproblematik besteht (und somit ein Bedarf,
             * den Bewohner bzw. die Bewohnerin im Umgang mit seinen bzw. ihren Schmerzen ärztlich und/oder pflegerisch zu unterstützen).
             * Beantworten Sie die Frage mit „ja“, wenn aus den Äußerungen des Bewohners
             * bzw. der Bewohnerin oder der Dokumentation hervorgeht, dass Schmerzen über mehrere Wochen oder Monate bestehen oder eine
             * Schmerzproblematik zwar mit Unterbrechungen, aber wiederholt äußert. Auch die regelmäßige Einnahme von Schmerzmedikamenten oder die
             * regelmäßige Anwendung anderer schmerzlindernder Maßnahmen lassen auf eine bestehende Schmerzproblematik schließen.
             * Einmalig auftretende Schmerzen, z.B. Kopfschmerzen am Tag der Erhebung, sind nicht zu berücksichtigen.
             */
            SCHMERZEN = (nrs >= 4 && chronisch ? 1 : 0);
            SCHMERZFREI = props.getProperty("schmerzfrei", "false").equalsIgnoreCase("true") ? 1 : 0;
            pit = JavaTimeConverter.toJavaLocalDateTime(letzter_schmerz.get().getFrom()).toLocalDate();
        } else { // Bei fremdbestimmter Ermittlung
            Properties props = ResInfoTools.getContent(letzter_besd.get());
            // Schmerzen hat er dann, wenn sie chronisch und die Rating ==1 sind.
            int rating = Integer.valueOf(props.getProperty("rating"));
            boolean chronisch = props.getProperty("schmerztyp", "0").equalsIgnoreCase("1");
            SCHMERZEN = (rating >= 1 && chronisch ? 1 : 0);
            SCHMERZFREI = props.getProperty("schmerzfrei", "false").equalsIgnoreCase("true") ? 1 : 0;
            pit = JavaTimeConverter.toJavaLocalDateTime(letzter_besd.get().getFrom()).toLocalDate();
        }

        /** 82 */qsData.setSCHMERZEN(of.createDasQsDataTypeSCHMERZEN());
        qsData.getSCHMERZEN().setValue(SCHMERZEN);
        /** 83 */qsData.setSCHMERZFREI(of.createDasQsDataTypeSCHMERZFREI());
        /** 84 */qsData.setSCHMERZEINSCH(of.createDasQsDataTypeSCHMERZEINSCH());
        /** 85 */qsData.setSCHMERZEINSCHDATUM(of.createDasQsDataTypeSCHMERZEINSCHDATUM());

        if (SCHMERZEN == 1) {
            // Bei den verwendeten Formularen gehe ich immer von einer differenzierten Einschätzung aus
            qsData.getSCHMERZEINSCH().setValue(1); // JA
            qsData.getSCHMERZEINSCHDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(pit)); // JA

            // SCHMERZE2 hat alle Infos
            /** 86 */
            DasQsDataType.SCHMERZEINSCHINFO info1 = of.createDasQsDataTypeSCHMERZEINSCHINFO();
            info1.setValue(1);
            DasQsDataType.SCHMERZEINSCHINFO info2 = of.createDasQsDataTypeSCHMERZEINSCHINFO();
            info2.setValue(2);
            DasQsDataType.SCHMERZEINSCHINFO info3 = of.createDasQsDataTypeSCHMERZEINSCHINFO();
            info3.setValue(3);
            DasQsDataType.SCHMERZEINSCHINFO info4 = of.createDasQsDataTypeSCHMERZEINSCHINFO();
            info4.setValue(4);
            qsData.getSCHMERZEINSCHINFO().add(info1);
            qsData.getSCHMERZEINSCHINFO().add(info2);
            qsData.getSCHMERZEINSCHINFO().add(info3);
            qsData.getSCHMERZEINSCHINFO().add(info4);

            // erhält nur einen Value, wenn es Schmerzen gibt. Sonst nicht.
            qsData.getSCHMERZFREI().setValue(SCHMERZFREI);

        } else { // hat er keine Schmerzen ist gar keine differenzierte Einschätzung nötig.
            // ein leerer Eintrag ist nötig, sonst schimpft die XSD
            DasQsDataType.SCHMERZEINSCHINFO infoleer = of.createDasQsDataTypeSCHMERZEINSCHINFO();
            qsData.getSCHMERZEINSCHINFO().add(infoleer);
        }
    }

    private void einzug(DasQsDataType qsData, Resident resident) {
        // Datum des Einzugs
        /** 4 */qsData.setEINZUGSDATUM(of.createDasQsDataTypeEINZUGSDATUM());
        Optional<ResInfo> aufenthalt = ResInfoTools.getValidOnThatDayIfAny(resident, HAUF, STICHTAG);
        LocalDate beginn_aktueller_aufenthalt = JavaTimeConverter.toJavaLocalDateTime(aufenthalt.get().getFrom()).toLocalDate();
        qsData.getEINZUGSDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(beginn_aktueller_aufenthalt));

        /** 87 */qsData.setNEUEINZUG(of.createDasQsDataTypeNEUEINZUG());
        int NEUEINZUG = beginn_aktueller_aufenthalt.isAfter(LETZTE_ERGEBNISERFASSUNG.toLocalDate()) ? 1 : 0;
        qsData.getNEUEINZUG().setValue(NEUEINZUG);

        /** 88 */qsData.setEINZUGNACHKZ(of.createDasQsDataTypeEINZUGNACHKZ());
        /** 89 */qsData.setEINZUGNACHKZDATUM(of.createDasQsDataTypeEINZUGNACHKZDATUM());


        /** 90 */qsData.setEINZUGKHBEHANDLUNG(of.createDasQsDataTypeEINZUGKHBEHANDLUNG());
        /** 91 */qsData.setEINZUGKHBEGINNDATUM(of.createDasQsDataTypeEINZUGKHBEGINNDATUM());
        /** 92 */qsData.setEINZUGKHENDEDATUM(of.createDasQsDataTypeEINZUGKHENDEDATUM());
        /** 93 */qsData.setEINZUGGESPR(of.createDasQsDataTypeEINZUGGESPR());
        /** 94 */qsData.setEINZUGGESPRDATUM(of.createDasQsDataTypeEINZUGGESPRDATUM());
        /** 96 */qsData.setEINZUGGESPRDOKU(of.createDasQsDataTypeEINZUGGESPRDOKU());

        if (NEUEINZUG == 1) {
            qsData.getEINZUGNACHKZ().setValue(0); // todo: mach mich richtig. nicht einfach 0

            qsData.getEINZUGKHBEHANDLUNG().setValue(0);
            for (ResInfo resInfo : ResInfoTools.getAll(resident, ABWESENHEIT, beginn_aktueller_aufenthalt.atStartOfDay(), beginn_aktueller_aufenthalt.plusWeeks(8).atTime(23, 59, 59))) {
                LocalDate start = JavaTimeConverter.toJavaLocalDateTime(resInfo.getFrom()).toLocalDate();
                Date from = resInfo.getTo().compareTo(SYSConst.DATE_UNTIL_FURTHER_NOTICE) == 0 ? new Date() : resInfo.getTo();
                LocalDate ende = JavaTimeConverter.toJavaLocalDateTime(from).toLocalDate();

                if (ChronoUnit.DAYS.between(start, ende) > 3) {
                    qsData.getEINZUGKHBEHANDLUNG().setValue(1);
                    qsData.getEINZUGKHBEGINNDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(start));
                    qsData.getEINZUGKHENDEDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(ende));
                    break;
                }
            }


            // _____       _                       _   _
            //|_   _|     | |                     | | (_)
            //  | |  _ __ | |_ ___  __ _ _ __ __ _| |_ _  ___  _ __  ___  __ _ ___ _ __
            //  | | | '_ \| __/ _ \/ _` | '__/ _` | __| |/ _ \| '_ \/ __|/ _` / __| '_ \
            // _| |_| | | | ||  __/ (_| | | | (_| | |_| | (_) | | | \__ \ (_| \__ \ |_) |
            //|_____|_| |_|\__\___|\__, |_|  \__,_|\__|_|\___/|_| |_|___/\__, |___/ .__(_)
            //                      __/ |                                 __/ |   | |
            //                     |___/                                 |___/    |_|

            ResInfo integration = ResInfoTools.getLastResinfo(resident, INTEGRATION);
            if (integration == null) {
                qsData.getEINZUGGESPR().setValue(3);
                DasQsDataType.EINZUGGESPRTEILNEHMER et = of.createDasQsDataTypeEINZUGGESPRTEILNEHMER();
                /** 95 */qsData.getEINZUGGESPRTEILNEHMER().add(et);
            } else {
                Properties content = ResInfoTools.getContent(integration);
                qsData.getEINZUGGESPR().setValue(Integer.valueOf(content.getProperty("EINZUGGESPR")));
                if (qsData.getEINZUGGESPR().getValue() == 1) {
                    qsData.getEINZUGGESPRDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(JavaTimeConverter.toLocalDateTime(content.getProperty("EINZUGGESPRDATUM")).toLocalDate()));

                    /** 95 */DasQsDataType.EINZUGGESPRTEILNEHMER et0 = of.createDasQsDataTypeEINZUGGESPRTEILNEHMER();
                    DasQsDataType.EINZUGGESPRTEILNEHMER et1 = of.createDasQsDataTypeEINZUGGESPRTEILNEHMER();
                    DasQsDataType.EINZUGGESPRTEILNEHMER et2 = of.createDasQsDataTypeEINZUGGESPRTEILNEHMER();
                    DasQsDataType.EINZUGGESPRTEILNEHMER et3 = of.createDasQsDataTypeEINZUGGESPRTEILNEHMER();
                    DasQsDataType.EINZUGGESPRTEILNEHMER et4 = of.createDasQsDataTypeEINZUGGESPRTEILNEHMER();

                    et0.setValue(0);
                    et1.setValue(1);
                    et2.setValue(2);
                    et3.setValue(3);
                    et4.setValue(4);

                    if (content.getProperty("1").equalsIgnoreCase("true")) qsData.getEINZUGGESPRTEILNEHMER().add(et1);
                    if (content.getProperty("2").equalsIgnoreCase("true")) qsData.getEINZUGGESPRTEILNEHMER().add(et2);
                    if (content.getProperty("3").equalsIgnoreCase("true")) qsData.getEINZUGGESPRTEILNEHMER().add(et3);
                    if (content.getProperty("4").equalsIgnoreCase("true")) qsData.getEINZUGGESPRTEILNEHMER().add(et4);
                    if (qsData.getEINZUGGESPRTEILNEHMER().isEmpty()) qsData.getEINZUGGESPRTEILNEHMER().add(et0);

                    qsData.getEINZUGGESPRDOKU().setValue(Integer.valueOf(content.getProperty("EINZUGGESPRDOKU")));
                }
            }
        } else {
            DasQsDataType.EINZUGGESPRTEILNEHMER et = of.createDasQsDataTypeEINZUGGESPRTEILNEHMER();
            /** 95 */qsData.getEINZUGGESPRTEILNEHMER().add(et);
        }
    }

    private void marshal(JAXBElement<RootType> root) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(RootType.class);
        Marshaller mar = context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        mar.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "https://www.das-pflege.de ../das_interface.xsd");
        target.toPath().getParent().toFile().mkdirs();
        textListener.addLog(SYSConst.html_h3(SYSTools.xx("qdvs.erzeuge.xml") + ": " + target.toString()));
//        textListener.addLog(target.toString());
        mar.marshal(root, target);
    }

    /**
     * ermittelt die zur Zeit gültigen Hauptdiagnosen für den Bewohner. Diese setzen sich zusammen aus allen gültigen
     * ICD10 (bzw. deren Markierungen) und den ResInfoTypes Demenz und Diabetes. Die Menge der Properties sind entweder
     * true oder false. Wobei das über alle Diagnosen per logischem ODER zusammengefasst wird.
     *
     * @param resident
     * @return Properties mit den folgenden Schlüsseln ("tumor","tetra","chorea","apallisch","parkinson","osteo","ms","demenz","diabetes").
     * Die Values sind jeweils true oder false.
     */
    private List<DasQsDataType.DIAGNOSEN> getAktuelleDiagnosenFuerQDVS(Resident resident) {
        // die reihenfolge entspricht den Ordnungsnummern die vom DAS erwartet werden. Tumor ist die 1 usw...
        List<String> diagnosen_schluessel = Arrays.asList("tumor", "tetra", "chorea", "apallisch", "diabetes", "demenz", "parkinson", "osteo", "ms");
        List<DasQsDataType.DIAGNOSEN> result = new ArrayList<>();
        Properties myprops = new Properties();


        // Defaults setzen
        diagnosen_schluessel.forEach(s -> myprops.put(s, "false"));

        // Alle in eine Liste zusammenfassen
        ArrayList<ResInfo> listInfos = ResInfoTools.getAll(resident, typeDiagnosen.getID(), ERHEBUNGSDATUM);

        ResInfoTools.getValidOnThatDayIfAny(resident, typeDiabetes.getID(), ERHEBUNGSDATUM).ifPresent(resInfo -> listInfos.add(resInfo));
        ResInfoTools.getValidOnThatDayIfAny(resident, typeDemenz.getID(), ERHEBUNGSDATUM).ifPresent(resInfo -> listInfos.add(resInfo));

        // Properties auswerten
        listInfos.forEach(info -> {
            Properties myProperties = ResInfoTools.getContent(info);
            if (info.getResInfoType().getType() == ResInfoTypeTools.TYPE_DIAGNOSIS) {
                // Jede Diagnose Resinfo hat jeden der 9 diagnosen_schluessel gesetzt. Entweder auf TRUE oder FALSE
                // Daher bilden wir hier die Schnittmenge zwischen den Properties aus der Entity und der Liste der diagnosen_schlüssel.
                CollectionUtils.intersection(myProperties.stringPropertyNames(), diagnosen_schluessel).forEach(o -> {
                    // die booleans parsen true oder false
                    boolean wert_vorher = Boolean.valueOf(myprops.getProperty(o));
                    boolean neuer_wert = wert_vorher || Boolean.valueOf(myProperties.getProperty(o)); // logisches OR, wenn irgendeine INFO das auf TRUE setzt bleibt das so.
                    myProperties.setProperty(o, Boolean.toString(neuer_wert));
                });
            } else if (info.getResInfoType().getType() == ResInfoTypeTools.TYPE_DIABETES) {
                // Hier reicht schon die Tatsache, dass es einen Diabetes Eintrag gibt, der z.Zt. gültig ist
                myprops.setProperty("diabetes", "true");
            } else if (info.getResInfoType().getType() == ResInfoTypeTools.TYPE_ORIENTATION) {
                // Sobald bei "type_of_dementia" nicht "none" steht setzen wir hier die Demenz auf true
                myprops.setProperty("demenz", Boolean.toString(!myProperties.getProperty("type_of_dementia").equalsIgnoreCase("none")));
            }
        });

        // hier füge ich für jedes true eine DIAGNOSE (jeweils typ INT) hinzu.
        myprops.entrySet().stream().filter(entry -> entry.getValue().toString().equalsIgnoreCase("true")).forEach(entry -> {
            DasQsDataType.DIAGNOSEN diag = of.createDasQsDataTypeDIAGNOSEN();
            // hier ist die reihenfolge wichtig, wo der schlüssel stand. Das passt dann zu dem DAS Schema
            diag.setValue(diagnosen_schluessel.indexOf(entry.getKey().toString()) + 1);
            result.add(diag);
        });

        if (result.isEmpty()) { // immer noch leer ?? dann gab es keine. Dann füge ich eine 0 für "KEINE DIAGNOSE" hinzu.
            DasQsDataType.DIAGNOSEN keine = of.createDasQsDataTypeDIAGNOSEN();
            keine.setValue(0);
            result.add(keine);
        }
        return result;
    }

    public static String toString(Resident resident) {
        DateFormat df = DateFormat.getDateInstance();
        String result = ResidentTools.getName(resident) + ", " + ResidentTools.getFirstname(resident) + " (*" + df.format(resident.getDob()) + "), ";


        result += " [" + resident.getId() + "]";
        return result + " (" + NF_IDBEWOHNER.format(resident.getIdbewohner()) + ")";
    }

}
