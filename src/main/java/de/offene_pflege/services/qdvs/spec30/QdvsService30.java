package de.offene_pflege.services.qdvs.spec30;


import com.google.common.io.Resources;
import de.offene_pflege.entity.building.Homes;
import de.offene_pflege.entity.building.Rooms;
import de.offene_pflege.entity.info.*;
import de.offene_pflege.entity.system.SYSPropsTools;
import de.offene_pflege.entity.values.ResValue;
import de.offene_pflege.entity.values.ResValueTools;
import de.offene_pflege.gui.events.AddTextListener;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.JavaTimeConverter;
import de.offene_pflege.op.tools.SYSCalendar;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import de.offene_pflege.services.ResvaluetypesService;
import de.offene_pflege.services.RoomsService;
import de.offene_pflege.services.qdvs.QdvsResidentInfoObject;
import de.offene_pflege.services.qdvs.QdvsService;
import de.offene_pflege.services.qdvs.spec30.schema.*;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Quintet;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Mit der Vesion 3.0 der Spezifikation wurden einige Änderungen vorgenommen.
 * <ul>
 *     <li>beim BEWUSSTSEINSZUSTAND fallen schläfrig und wachkoma raus</li>
 *     <li>bei den DIAGNOSEN fällt das appalische Syndrom weg</li>
 *     <li>das appalische Syndrom ist nun ein Ausschlussgrund</li>
 * </ul>
 */
@Log4j2
public class QdvsService30 implements QdvsService {
    private static final String HAUF = "hauf";
    private static final String ABWESENHEIT = "abwe1";
    private static final String BEWUSST = "bewusst01";
    private static final String ORIENT = "orient02";
    private static final String MOBIL = "mobil02";
    private static final String AUSSCHEID = "aussch01";
    private static final String KPFLEGE = "kpflege03";
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

    private JSONObject MANDANTORY_RESINFOTYPES;

    private final ResInfoType FALLTYPE;
    private final ResInfoType FALLAUSWIRKUNG;
    private final ResInfoType FIXIERUNGSPROTOKOLLE;
    private final AddTextListener textListener;
    private final StringBuilder report;
    private static final String NL = System.lineSeparator();
    private static final String NL2 = NL + NL;
    ObjectFactory of;

    private LocalDateTime STICHTAG; // ist das vorher festgelegte Zieldatum (2x im Jahr). Das bleibt auch bei den Nachkorrekturen gleich
    private LocalDateTime BEGINN_ERFASSUNGSZEITRAUM; // ist der letzt Stichtag.


    static final String SPECIFICATION = "V03"; // die Version der jeweilig eingereichten Datenstruktur, die zum Zeitpunkt der Verwendung gültig war.
    public static DecimalFormat NF_IDBEWOHNER = new DecimalFormat("000000");
    private File target;
    int runningNumber = 0;
    int numResidents;
    List<Resident> listeBWFehlerfrei;
    List<String> selbststaendig = List.of("selbst", "überw.selbst", "überw.UNselbst", "UNselbst");
    List<String> selbststaendig369 = List.of("selbst", "", "", "überw.selbst", "", "", "überw.UNselbst", "", "", "UNselbst");
    List<String> selbststaendig246 = List.of("selbst", "", "überw.selbst", "", "überw.UNselbst", "", "UNselbst");
    List<String> erkennen = List.of("vorhanden", "größt.vorh.", "gering vorh.", "nicht vorh.");
    List<String> kontinenz = List.of("ständig kontinent", "überwiegend kontinent.",
            "überwiegend INkontinent", "komplett inkontinent",
            "Hat DK / Stoma");
    List<String> dekub_lok = List.of("", "bei uns", "im KH", "vor dem Einzug", "woanders");
    List<String> sturzfolg = List.of("keine", "Frakturen", "arztlich behandlungsbedürftige Wunde",
            "erhöhter Unterstützungsbedarf bei Alltagsverrichtungen",
            "erhöhter Unterstützungsbedarf bei Mobilität");
    List<String> schmerzein = List.of("keine Informationen",
            "Schmerzintensität",
            "Schmerzqualität",
            "Schmerzlokalisation",
            "Folgen für Lebensalltag");

    @Override
    public Map<Resident, QdvsResidentInfoObject> getResidentInfoObjectMap() {
        return residentInfoObjectMap;
    }

    @Override
    public String get_DAS_SPEZIFIKATION() {
        return "DAS_Pflege_Spezifikation_V03.0/02_XSD/interface_qs_data/das_interface.xsd";
    }

    @Override
    public String get_DAS_REGELN_CSV() {
        return "DAS_Pflege_Spezifikation_V03.0/03_Dokumentationsbogen/DAS_Plausibilitaetsregeln.csv";
    }


    Map<Resident, QdvsResidentInfoObject> residentInfoObjectMap;

    RootType rootType;

    HashSet<ResInfoType> necessary_resinfotypes_for_included_residents; // Diese Typen müssen unbedingt vorhanden sein, wenn ein BW in der Stichprobe enthalten ist
    HashSet<ResInfoType> necessary_resinfotypes_for_excluded_residents; // Diese Typen müssen unbedingt vorhanden sein, wenn ein BW in der Stichprobe enthalten ist
    HashSet<ResInfoType> forbiddenTypes; // Diese Typen stammen noch aus der VOR QDVS Zeit und müssen ersetzt werden.

    // eine Liste aller gültigen Resinfos zum Thema Diagnosen
    ResInfoType typeDiagnosen = ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_DIAGNOSIS);
    //    ResInfoType typeDiabetes = ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_DIABETES);
//    ResInfoType typeDemenz = ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_ORIENTATION);
    private Homes home;


    /**
     * Diese Service Klasse wertet die Pflegedaten aus und erzeugt eine passende XML Datei, die an DAS-PFLEGE
     * hochgeladen werden kann.
     *
     * @param textListener meldet alle Protokoll Einträge an den Textlistener. Der kann das dann hinterher anzeigen.
     */
    @SneakyThrows
    public QdvsService30(AddTextListener textListener) {
        log.info("\n ____    _    ____     ____  _____ _     _____ ____ _____          ___ _____  ___\n" +
                "|  _ \\  / \\  / ___|   |  _ \\|  ___| |   | ____/ ___| ____| __   __/ _ \\___ / / _ \\\n" +
                "| | | |/ _ \\ \\___ \\   | |_) | |_  | |   |  _|| |  _|  _|   \\ \\ / / | | ||_ \\| | | |\n" +
                "| |_| / ___ \\ ___) |  |  __/|  _| | |___| |__| |_| | |___   \\ V /| |_| |__) | |_| |\n" +
                "|____/_/   \\_\\____/___|_|   |_|   |_____|_____\\____|_____|___\\_/  \\___/____(_)___/\n" +
                "                 |_____|                                |_____|");
        this.textListener = textListener;
        this.report = new StringBuilder();
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

        // todo: change this mechanism to JSON.
        MANDANTORY_RESINFOTYPES = new JSONObject(Resources.toString(Resources.getResource("mandantory_resinfotypes_regular_qdvs_v2.json"), StandardCharsets.UTF_8));

        /**
         * Diese Resinfotypes müssen gesetzt sein, wenn der BW mit in der Stichprobe drin ist. Sonst muss der Lauf abgebrochen werden.
         */
        necessary_resinfotypes_for_included_residents = new HashSet<>();
        for (String pk : new String[]{BEWUSST, ORIENT, MOBIL, AUSSCHEID, KPFLEGE, ERN, ALLTAG, SCHLAF, SOZIAL, NINSUR, ROOM, RESPIRAT}) {
            necessary_resinfotypes_for_included_residents.add(ResInfoTypeTools.getByID(pk));
        }

        /**
         * Diese Resinfotypes müssen gesetzt sein, wenn der BW nur einen Minimaldatensatz MDS erhält.
         */
        necessary_resinfotypes_for_excluded_residents = new HashSet<>();
        for (String pk : new String[]{ROOM}) {
            necessary_resinfotypes_for_excluded_residents.add(ResInfoTypeTools.getByID(pk));
        }
    }

    /**
     * diese Methode wird von der GUI aus aufgerufen und setzt die Parameter für die Auswertung
     *
     * @param stichtag
     * @param beginn_erfassungszeitraum
     * @param home
     * @param listeAlleBewohnerAmStichtag
     * @param target
     */
    public void setParameters(LocalDate stichtag, LocalDate beginn_erfassungszeitraum, Homes home, List<Resident> listeAlleBewohnerAmStichtag, File target) {
        BEGINN_ERFASSUNGSZEITRAUM = beginn_erfassungszeitraum.atStartOfDay(); // beginnt unmittelbar nach dem vorherigen STICHTAG
        STICHTAG = stichtag.atTime(23, 59, 59); // der Stichtag gehört mit zum Erhebungszeitraum
        this.home = home;
        this.target = target;
        residentInfoObjectMap.clear();
        listeAlleBewohnerAmStichtag.forEach(resident -> residentInfoObjectMap.put(resident, new QdvsResidentInfoObject(resident)));
        numResidents = listeAlleBewohnerAmStichtag.size();
        runningNumber = 0;
    }

    private void createQDVS(Optional<String> comment) {
        try {
            rootType.setHeader(createHeaderType());
            if (comment.isPresent()) rootType.setBody(createBodyType(comment.get()));
            else rootType.setBody(createBodyType());
        } catch (Exception e) {
            log.error(e);

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

        report.append("# Indikatoren Ermittlung" + NL);
        report.append("## Stichtag " + STICHTAG.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)) + NL2);
        report.append("Erstellt am  " + LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)) + NL2);

        // Hier ist die Schleife, die das ganze XML Dokument erzeugt.
        residentInfoObjectMap.values().stream().sorted(Comparator.comparing(QdvsResidentInfoObject::getResident)).forEach(infoObject -> {
            runningNumber++;
            textListener.setProgress(runningNumber, numResidents * 3, ResidentTools.getTextCompact(infoObject.getResident()));
            // Unterscheidung, ob Ausschluss oder nicht
            ResidentType residentType = infoObject.getAusschluss_grund() == QdvsResidentInfoObject.MDS_GRUND_KEIN_AUSSCHLUSS
                    ? createResidentType(infoObject.getResident())
                    : createResidentAusschlussType(infoObject.getResident());
            body.getDataContainer().getResidents().getResident().add(residentType);
        });

        return body;
    }

    /**
     * Erzeugt eine XML Datei zur Ergebniskommentierung.
     *
     * @param comment
     * @return
     * @throws Exception
     */
    private BodyType createBodyType(String comment) throws Exception {
        BodyType body = of.createBodyType();
        body.setDataContainer(null);
        body.setCommentationContainer(of.createDasCommentationType());
        DasCommentationType.KOMMENTAR kommentar = new DasCommentationType.KOMMENTAR();
        kommentar.setValue(comment);
        body.getCommentationContainer().setKOMMENTAR(kommentar);
        return body;
    }

    public void kommentierung(String kommentar) {
        try {
            createQDVS(Optional.of(kommentar));
            marshal(of.createRoot(rootType));
        } catch (Exception e) {
            e.printStackTrace();
            log.fatal(e);
            textListener.addLog(e.getMessage());
        }
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

        String base_name = FilenameUtils.getFullPathNoEndSeparator(target.getPath());
        File report_file = new File(base_name, "report.md");
        report.insert(0, String.format("<!-- " +
                "/opt/homebrew/lib/node_modules/markdown-styles/bin/generate-md --layout witex --input %s --output ~/Desktop " +
                "-->" + NL, report_file.getAbsoluteFile()));
        FileUtils.writeStringToFile(report_file, report.toString(), Charset.defaultCharset());

        return fehlerfrei;
    }

    @Override
    public String get_DESCRIPTION() {
        return "Spezifikation V03.0 - Stand 01.01.2023";
    }

    private void hauptpruefung() throws JAXBException, IOException {
        textListener.addLog(SYSConst.html_h2("qdvs.hauptprüfung.laeuft"));
        try {
            createQDVS(Optional.empty());
            marshal(of.createRoot(rootType));
        } catch (Exception e) {
            e.printStackTrace();
            log.fatal(e);
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
                    log.debug("Vorprüfung step1 - " + resident.toString());

                    Optional<ResInfo> hauf = ResInfoTools.getValidOnThatDayIfAny(resident, HAUF, STICHTAG);
                    runningNumber++;
                    textListener.setProgress(runningNumber, numResidents * 3, "");

                    if (!hauf.isPresent()) { // das sollte NIE vorkommen, weil da bereits vorher ausgeschlossen wird. Ich konnte es aber nicht lassen.
                        residentInfoObjectMap.get(resident).addLog("Kein Heimaufenthalt HAUF.");
                        log.error("Bewohner " + resident + " kein HAUF. Das darf nicht sein. Hier läuft etwas schief. ");
                    } else {
                        // +1, weil abreise und ankunftstag mitgerechnet werden als abwesenheit
                        long aufenthaltszeitraumInTagen = ChronoUnit.DAYS.between(JavaTimeConverter.toJavaLocalDateTime(hauf.get().getFrom()).toLocalDate(), STICHTAG.toLocalDate()) + 1;
                        Optional<ResInfo> absent = ResInfoTools.getValidOnThatDayIfAny(resident, ABWESENHEIT, STICHTAG.toLocalDate().atStartOfDay());

                        long abwesenheitsZeitraumInTagen = 0L;
                        if (absent.isPresent()) {
                            abwesenheitsZeitraumInTagen = ChronoUnit.DAYS.between(JavaTimeConverter.toJavaLocalDateTime(absent.get().getFrom()).toLocalDate(), STICHTAG.toLocalDate()) + 1;
                        }


                        ResInfo bewusst = ResInfoTools.getValidOnThatDayIfAny(resident, BEWUSST, STICHTAG).get();
                        int bewzustand = Integer.valueOf(ResInfoTools.getContent(bewusst).getProperty("BEWUSSTSEINSZUSTAND"));
                        boolean wachkoma = bewzustand == 5;

                        List<String> ausschlussDiagnosen = getAusschlussDiagnosen(resident);

                        String aus_grund = "";
                        // Ausschlussgrund (4)
                        if (abwesenheitsZeitraumInTagen >= 21) { // Ausschlussgrund (4)
                            residentInfoObjectMap.get(resident).setAusschluss_grund(QdvsResidentInfoObject.MDS_GRUND_MEHR_ALS_21_TAGE_WEG);
                            aus_grund = "andauernde Abwesenheit 21 Tage oder länger :" + absent.get().getFrom() + " // " + abwesenheitsZeitraumInTagen + " Tage";
                        } else if (aufenthaltszeitraumInTagen < 14) { // Ausschlussgrund (1)
                            residentInfoObjectMap.get(resident).setAusschluss_grund(QdvsResidentInfoObject.MDS_GRUND_WENIGER_14_TAGE_DA);
                            aus_grund = "Heimaufnahme weniger als 14 Tage :" + hauf.get().getFrom() + " // " + aufenthaltszeitraumInTagen + " Tage";
                        } else if (ResInfoTools.isKZP(hauf.get())) { // Ausschlussgrund (2)
                            residentInfoObjectMap.get(resident).setAusschluss_grund(QdvsResidentInfoObject.MDS_GRUND_KURZZEIT);
                            aus_grund = "Kurzzeitpflege";
                        } else if (resident.getSterbePhase()) { // Ausschlussgrund (3)
                            residentInfoObjectMap.get(resident).setAusschluss_grund(QdvsResidentInfoObject.MDS_GRUND_PALLIATIV);
                            aus_grund = " befindet sich in der Sterbephase";
                        } else if (wachkoma || !ausschlussDiagnosen.isEmpty()) { // Ausschlussgrund (5)
                            residentInfoObjectMap.get(resident).setAusschluss_grund(QdvsResidentInfoObject.MDS_GRUND_HIRNSCHADEN_WACHKOMA_APALLISCH);
                            aus_grund = " weil im wachkoma / apallisch";
                        } else { // kein Ausschluss
                            residentInfoObjectMap.get(resident).setAusschluss_grund(QdvsResidentInfoObject.MDS_GRUND_KEIN_AUSSCHLUSS);
                        }
                        if (!aus_grund.isEmpty()) {
                            String text = "AUSSCHLUSS: " + ResidentTools.getLabelText(resident) + " -> " + aus_grund;
                            log.debug(text);
                            textListener.addLog(SYSConst.html_paragraph(text));
                            report.append("### " + aus_grund + NL);
                        }
                    }
                }
        );
        textListener.addLog(SYSConst.html_h2("qdvs.vorpruefung.laeuft.step2"));

        // 2. Durchlauf
        // Prüfen, welche BW noch fehlerhafte Einträge haben
        residentInfoObjectMap.keySet().forEach(resident -> {
            Set<ResInfoType> used_resinfotypes_for_this_resident = ResInfoTools.getUsedActiveTypesBetween(resident, BEGINN_ERFASSUNGSZEITRAUM, STICHTAG);
            Set<ResInfoType> notwendigeTypen = new HashSet<>(used_resinfotypes_for_this_resident);
            listeBWFehlerfrei.add(resident);

            if (residentInfoObjectMap.get(resident).getAusschluss_grund() == QdvsResidentInfoObject.MDS_GRUND_KEIN_AUSSCHLUSS) {

                runningNumber++;
                textListener.setProgress(runningNumber, numResidents * 3, "");

                boolean schmerze2 = used_resinfotypes_for_this_resident.stream().anyMatch(resInfoType -> resInfoType.getID().equals(SCHMERZ));
                boolean besd2 = used_resinfotypes_for_this_resident.stream().anyMatch(resInfoType -> resInfoType.getID().equals(BESD));


                if (!(schmerze2 || besd2)) {
                    residentInfoObjectMap.get(resident).addLog("Schmerzeinschätzung fehlt (SCHMERZE2 oder BESD2)");
                    textListener.addLog(SYSConst.html_critical("KRITISCHER FEHLER Bewohner " + ResidentTools.getLabelText(resident) + ": Schmerzeinschätzung fehlt (SCHMERZE2 oder BESD2)"));
                    listeBWFehlerfrei.remove(resident);
                }

                notwendigeTypen.retainAll(necessary_resinfotypes_for_included_residents); // alles hier rein, was notwendig und bereits vorhanden ist
                if (notwendigeTypen.size() != necessary_resinfotypes_for_included_residents.size()) { // zu wenig ? dann müssen wir hier die fehlenen reinschreiben
                    HashSet<ResInfoType> copyMandantoryTypes = new HashSet<>(necessary_resinfotypes_for_included_residents);
                    copyMandantoryTypes.removeAll(notwendigeTypen);
                    copyMandantoryTypes.forEach(resInfoType -> {
                        residentInfoObjectMap.get(resident).addLog(SYSTools.xx("qdvs.error.new.type.missing.please.add") + " " + resInfoType);
                        textListener.addLog(SYSConst.html_critical("KRITISCHER FEHLER Bewohner " + ResidentTools.getLabelText(resident) + ": " + SYSTools.xx("qdvs.error.new.type.missing.please.add") + " " + resInfoType));
                    });
                    listeBWFehlerfrei.remove(resident);
                }

                Optional<ResInfo> gewichtdoku = ResInfoTools.getValidOnThatDayIfAny(resident, KOERPERGEWICHTDOKU, STICHTAG);
                if (gewichtdoku.isPresent()) {
                    Properties content = ResInfoTools.getContent(gewichtdoku.get());
                    // wenn eine gewichtsdoku Feld 70 vorhanden ist und die Punkte 4 und/oder 5 ausgefüllt wurden, dann ist es egal ob gewicht oder größe vorhanden sind
                    // also beides ist nötig, WENN 4 UND 5 nicht ausgefüllt sind.
                    boolean gewicht_noetig = content.getProperty("4", "false").equalsIgnoreCase("false") && content.getProperty("5", "false").equalsIgnoreCase("false");
                    if (gewicht_noetig) {
                        if (!ResValueTools.getMostRecentBefore(resident, ResvaluetypesService.WEIGHT, STICHTAG).isPresent()) {
                            residentInfoObjectMap.get(resident).addLog("qdvs.error.weight.missing");
                            textListener.addLog(SYSConst.html_critical("KRITISCHER FEHLER Bewohner " + ResidentTools.getLabelText(resident) + ": " + SYSTools.xx("qdvs.error.weight.missing")));
                            listeBWFehlerfrei.remove(resident);
                        }
                    }
                }

            } else { // Mindestprüfung ausgeschlossene BW (Minimaldatensatz)

                notwendigeTypen.retainAll(necessary_resinfotypes_for_excluded_residents); // alles hier rein, was notwendig und bereits vorhanden ist
                if (notwendigeTypen.size() != necessary_resinfotypes_for_excluded_residents.size()) { // zu wenig ? dann müssen wir hier die fehlenen reinschreiben
                    HashSet<ResInfoType> copyMandantoryTypes = new HashSet<>(necessary_resinfotypes_for_excluded_residents);
                    copyMandantoryTypes.removeAll(notwendigeTypen);
                    copyMandantoryTypes.forEach(resInfoType -> {
                        residentInfoObjectMap.get(resident).addLog(SYSTools.xx("qdvs.error.new.type.missing.please.add") + " " + resInfoType);
                        textListener.addLog(SYSConst.html_critical("KRITISCHER FEHLER Bewohner " + ResidentTools.getLabelText(resident) + ": " + SYSTools.xx("qdvs.error.new.type.missing.please.add") + " " + resInfoType));
                    });
                    listeBWFehlerfrei.remove(resident);
                }
            }

            Set<ResInfoType> verboteneTypen = new HashSet<>(used_resinfotypes_for_this_resident);
            verboteneTypen.retainAll(forbiddenTypes); // alles hier rein, was verboten und immer noch vorhanden ist
            if (verboteneTypen.size() != 0) { // immer noch verbotene typemn gesetzt ? dann bitte auflisten
                verboteneTypen.forEach(resInfoType -> residentInfoObjectMap.get(resident).addLog(SYSTools.xx("qdvs.error.old.type.found.please.replace") + " " + resInfoType));
                listeBWFehlerfrei.remove(resident);
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
        log.debug("====================================================================");
        log.debug("====------------------------------------------------------------====");
        log.debug("====                                                            ====");
        log.debug("Indikatoren Ermittlung für Bewohner:in");
        String title = ResidentTools.getLabelText(resident) + " (" + NF_IDBEWOHNER.format(resident.getIdbewohner()) + ")";
        log.debug(title);
        report.append("- - -" + NL);
        report.append("## " + title + NL);
        ResidentType residentType = of.createResidentType();

        try {

            final int WIDTH = 57;
            residentType.setQsData(of.createDasQsDataType());
            log.debug(String.format("===---%s---===", StringUtils.center("Allgemeine Angaben", WIDTH)));
            report.append("### Allgemeine Angaben" + NL);
            allgemeine_angaben(residentType.getQsData(), resident);
            log.debug(String.format("===---%s---===", StringUtils.center("Krankenhaus", WIDTH)));
            report.append("### Krankenhaus" + NL);
            krankenhaus(residentType.getQsData(), resident);
            log.debug(String.format("===---%s---===", StringUtils.center("Mobilität", WIDTH)));
            report.append("### Mobilität" + NL);
            mobilitaet(residentType.getQsData(), resident);
            log.debug(String.format("===---%s---===", StringUtils.center("Kognitiv / Kommunikativ", WIDTH)));
            report.append("### Kognitiv / Kommunikativ" + NL);
            kognitiv_kommunikativ(residentType.getQsData(), resident);
            log.debug(String.format("===---%s---===", StringUtils.center("Selbstversorgung", WIDTH)));
            report.append("### Selbstversorgung" + NL);
            selbstversorgung(residentType.getQsData(), resident);
            log.debug(String.format("===---%s---===", StringUtils.center("Alltag, Soziales", WIDTH)));
            report.append("### Alltag, Soziales" + NL);
            alltag_soziales(residentType.getQsData(), resident);
            log.debug(String.format("===---%s---===", StringUtils.center("Dekubitus", WIDTH)));
            report.append("### Dekubitus" + NL);
            dekubitus(residentType.getQsData(), resident);
            log.debug(String.format("===---%s---===", StringUtils.center("Größe, Gewicht", WIDTH)));
            report.append("### Größe, Gewicht" + NL);
            gewicht(residentType.getQsData(), resident);
            log.debug(String.format("===---%s---===", StringUtils.center("Sturz", WIDTH)));
            report.append("### Sturz" + NL);
            sturz(residentType.getQsData(), resident);
            log.debug(String.format("===---%s---===", StringUtils.center("Fixierung", WIDTH)));
            report.append("### Fixierung" + NL);
            fixierung(residentType.getQsData(), resident);
            log.debug(String.format("===---%s---===", StringUtils.center("Schmerzen", WIDTH)));
            report.append("### Schmerzen" + NL);
            schmerzen(residentType.getQsData(), resident);
            log.debug(String.format("===---%s---===", StringUtils.center("Informationen zum Einzug", WIDTH)));
            report.append("### Informationen zum Einzug" + NL);
            einzug(residentType.getQsData(), resident);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        log.debug("====                                                            ====");
        log.debug("====------------------------------------------------------------====");
        log.debug("====================================================================");
        log.debug("");
        log.debug("");
        log.debug("");

        return residentType;
    }


    private ResidentType createResidentAusschlussType(Resident resident) {
        log.debug("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        log.debug("XXXX------------------------------------------------------------XXXX");
        log.debug("XXXX                                                            XXXX");
        log.debug("AUSSCHLUSS: " + ResidentTools.getLabelText(resident) + " (" + NF_IDBEWOHNER.format(resident.getIdbewohner()) + ")");
        ResidentType residentType = of.createResidentType();
        DasQsDataMdsType qsMdsData = of.createDasQsDataMdsType();
        residentType.setQsDataMds(qsMdsData);

        /** 1 */qsMdsData.setIDBEWOHNER(of.createDasQsDataMdsTypeIDBEWOHNER());
        qsMdsData.getIDBEWOHNER().setValue(NF_IDBEWOHNER.format(resident.getIdbewohner()));

        Optional<Rooms> room = RoomsService.getRoom(resident, STICHTAG);
        /** 2 */qsMdsData.setWOHNBEREICH(of.createDasQsDataMdsTypeWOHNBEREICH());
        qsMdsData.getWOHNBEREICH().setValue(EnumWohnbereichType.fromValue(resident.getStation()));
        log.trace(room.get().toString());

        // Datum der Erhebung
        /** 3 */qsMdsData.setERHEBUNGSDATUM(of.createDasQsDataMdsTypeERHEBUNGSDATUM());
        qsMdsData.getERHEBUNGSDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(LocalDate.now())); // Ist immer das Datum an dem die Erhebung durchgeführt wurde. Also jetzt.

        // Datum des Einzugs
        /** 4 */qsMdsData.setEINZUGSDATUM(of.createDasQsDataMdsTypeEINZUGSDATUM());

        /** 5 */qsMdsData.setGEBURTSMONAT(of.createDasQsDataMdsTypeGEBURTSMONAT());
        qsMdsData.getGEBURTSMONAT().setValue(JavaTimeConverter.toJavaLocalDateTime(resident.getDob()).getMonthValue());
        /** 6 */qsMdsData.setGEBURTSJAHR(of.createDasQsDataMdsTypeGEBURTSJAHR());
        qsMdsData.getGEBURTSJAHR().setValue(JavaTimeConverter.toJavaLocalDateTime(resident.getDob()).getYear());
        /** Geschlecht entfällt ab der Scehma Version 2.0 */

        /** 8 */qsMdsData.setAUSSCHLUSSGRUND(of.createDasQsDataMdsTypeAUSSCHLUSSGRUND());
        qsMdsData.getAUSSCHLUSSGRUND().setValue(residentInfoObjectMap.get(resident).getAusschluss_grund()); // in der map stehen die ausschlussgründe drin
        if (residentInfoObjectMap.get(resident).getAusschluss_grund() != QdvsResidentInfoObject.MDS_GRUND_KURZZEIT) {
            // Bei KZP darf das Einzugsdatum nicht stehen. Denn das bezieht sich nur auf den DAUERHAFTEN Aufenthalt.
            qsMdsData.getEINZUGSDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(ResInfoTools.getValidOnThatDayIfAny(resident, HAUF, STICHTAG).get().getFrom()));
        }

        log.debug("XXXX------------------------------------------------------------XXXX");
        log.debug("XXXX                                                            XXXX");
        log.debug("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");


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

        //Optional<Rooms> room = RoomsService.getRoom(resident, STICHTAG);

        qsData.setWOHNBEREICH(of.createDasQsDataTypeWOHNBEREICH());
        qsData.getWOHNBEREICH().setValue(EnumWohnbereichType.fromValue(resident.getStation()));

        qsData.setERHEBUNGSDATUM(of.createDasQsDataTypeERHEBUNGSDATUM());
        qsData.getERHEBUNGSDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(STICHTAG.toLocalDate())); // Das Erhebungsdatum ist immer das Datum an dem die Eingabe erfolgte. Ich nehme hier STICHTAG.


        qsData.setGEBURTSMONAT(of.createDasQsDataTypeGEBURTSMONAT());
        qsData.getGEBURTSMONAT().setValue(JavaTimeConverter.toJavaLocalDateTime(resident.getDob()).getMonthValue());
        qsData.setGEBURTSJAHR(of.createDasQsDataTypeGEBURTSJAHR());
        qsData.getGEBURTSJAHR().setValue(JavaTimeConverter.toJavaLocalDateTime(resident.getDob()).getYear());

        // weiter bei Pflegegrad
        // seit der 2.0 keine Angabe mehr welcher Plfegegrad, nur ob ja 1 oder nein 0
        qsData.setPFLEGEGRAD(of.createDasQsDataTypePFLEGEGRAD());
        int pgrad = 0;
        // wenn es einen eintrag gibt und der auch noch größer 0 ist, dann ist pgrad ja
        if (ResInfoTools.getValidOnThatDayIfAny(resident, NINSUR, STICHTAG).isPresent()) {
            int pflegegrad = Integer.valueOf(ResInfoTools.getContent(ResInfoTools.getValidOnThatDayIfAny(resident, NINSUR, STICHTAG).get()).getProperty("grade"));
            report.append("Pflegegrad: " + pflegegrad + NL);
            if (pflegegrad > 0)
                pgrad = 1;
        }
        qsData.getPFLEGEGRAD().setValue(Integer.valueOf(pgrad));
    }

    private void krankenhaus(DasQsDataType qsData, Resident resident) {

        //____ ___  ____ ___  _    ____ _  _
        //|__| |__] |  | |__] |    |___  \/
        //|  | |    |__| |    |___ |___ _/\_
        /** 9 */qsData.setAPOPLEX(of.createDasQsDataTypeAPOPLEX());
        List<ResInfo> list_apoplex = ResInfoTools.getAll(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_APOPLEX), BEGINN_ERFASSUNGSZEITRAUM, STICHTAG);
        /** 10 */qsData.setAPOPLEXDATUM(of.createDasQsDataTypeAPOPLEXDATUM());
        qsData.getAPOPLEX().setValue(0);
        if (!list_apoplex.isEmpty()) {
            ResInfo last = list_apoplex.get(list_apoplex.size() - 1);
            qsData.getAPOPLEX().setValue(1);
            qsData.getAPOPLEXDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(last.getFrom()));
            report.append("- Anzahl Schlaganfälle: " + list_apoplex.size() + NL);
        }

        //____ ____ ____ _  _ ___ _  _ ____
        //|___ |__/ |__| |_/   |  |  | |__/
        //|    |  \ |  | | \_  |  |__| |  \
        /** 11 */qsData.setFRAKTUR(of.createDasQsDataTypeFRAKTUR());
        // Fraktur
        List<ResInfo> list_fraktur = ResInfoTools.getAll(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_FRAKTUR), BEGINN_ERFASSUNGSZEITRAUM, STICHTAG);
        /** 12 */qsData.setFRAKTURDATUM(of.createDasQsDataTypeFRAKTURDATUM());
        qsData.getFRAKTUR().setValue(0);
        if (!list_fraktur.isEmpty()) {
            ResInfo last = list_fraktur.get(list_fraktur.size() - 1);
            qsData.getFRAKTUR().setValue(1);
            qsData.getFRAKTURDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(last.getFrom()));
            report.append("- Anzahl Frakturen: " + list_fraktur.size() + NL);
        }

        //_  _ ____ ____ ___  _ _  _ ____ ____ ____ _  _ ___
        //|__| |___ |__/   /  | |\ | |___ |__| |__/ |_/   |
        //|  | |___ |  \  /__ | | \| |    |  | |  \ | \_  |
        /** 13 */qsData.setHERZINFARKT(of.createDasQsDataTypeHERZINFARKT());
        List<ResInfo> list_herzinfarkt = ResInfoTools.getAll(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_HERZINFARKT), BEGINN_ERFASSUNGSZEITRAUM, STICHTAG);
        /** 14 */qsData.setHERZINFARKTDATUM(of.createDasQsDataTypeHERZINFARKTDATUM());
        qsData.getHERZINFARKT().setValue(0);
        if (!list_herzinfarkt.isEmpty()) {
            ResInfo last = list_herzinfarkt.get(list_herzinfarkt.size() - 1);
            qsData.getHERZINFARKT().setValue(1);
            qsData.getHERZINFARKTDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(last.getFrom()));
            report.append("- Anzahl Herzinfarkte: " + list_herzinfarkt.size() + NL);
        }

        //____ _  _ ___  _  _ ___ ____ ___ _ ____ _  _
        //|__| |\/| |__] |  |  |  |__|  |  | |  | |\ |
        //|  | |  | |    |__|  |  |  |  |  | |__| | \|
        /** 15 */qsData.setAMPUTATION(of.createDasQsDataTypeAMPUTATION());
        List<ResInfo> list_amputation = ResInfoTools.getAll(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_AMPUTATION), BEGINN_ERFASSUNGSZEITRAUM, STICHTAG); // das kann höchstens einer sein wegen des intervall_mode
        /** 16 */qsData.setAMPUTATIONDATUM(of.createDasQsDataTypeAMPUTATIONDATUM());
        Optional<LocalDate> letzteAmputation = ResInfoTools.getLetzteAmputation(list_amputation.isEmpty() ? null : list_amputation.get(0));
        letzteAmputation.ifPresent(letzte_amp -> {
            qsData.getAMPUTATION().setValue(0);
            if (letzte_amp.compareTo(BEGINN_ERFASSUNGSZEITRAUM.toLocalDate()) >= 0) {
                qsData.getAMPUTATION().setValue(1);
                qsData.getAMPUTATIONDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(letzte_amp));
                report.append("- Letzte Amputation: " + letzte_amp.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)) + NL);
            }
        });

        // SPEC20 - Keine Anzahl und Anzahl Tage mehr
        // Krankenhaus Aufenthalte werden über Abwesenheiten ermittelt.
        List<ResInfo> list_away = ResInfoTools.getAll(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_ABSENCE), BEGINN_ERFASSUNGSZEITRAUM, STICHTAG);

        int khbehandlung = 0; // 0 = nein  1 = ja, einmal  2 = ja, mehrmals
        int khbanzahlaufenhalte = 0; // min 2, max 10
        int khbanzahltage = 0; // min 2, max 200

        long bisher_laengste_periode = 0;
        Optional<ResInfo> laengsterAufenthalt = Optional.empty();
        for (ResInfo away : list_away) {
            Properties props = ResInfoTools.getContent(away);
            if (props.getProperty("type").equalsIgnoreCase(ResInfoTypeTools.TYPE_ABSENCE_HOSPITAL)) {

                // +1, weil abreise und ankunftstag mitgerechnet werden als abwesenheit
                long diese_periode_in_tagen = ChronoUnit.DAYS.between(JavaTimeConverter.toJavaLocalDateTime(away.getFrom()).toLocalDate(), JavaTimeConverter.toJavaLocalDateTime(away.getTo()).toLocalDate()) + 1;
                if (diese_periode_in_tagen > 0) {
                    khbanzahltage += diese_periode_in_tagen;
                    khbanzahlaufenhalte++;
                }

                if (bisher_laengste_periode < diese_periode_in_tagen) {
                    bisher_laengste_periode = diese_periode_in_tagen;
                    laengsterAufenthalt = Optional.of(away);
                }

                String kh = "KH Aufenthalt **";
                if (away.isClosed()) {
                    kh += DateFormat.getDateInstance().format(away.getFrom()) + " - " + DateFormat.getDateInstance().format(away.getTo()) + "** Dauer **" + diese_periode_in_tagen + " Tage**";
                } else {
                    kh += " seit " + DateFormat.getDateInstance().format(away.getFrom());
                }

                log.debug(kh);
                report.append("- " + kh + NL);
            }
        }

        khbehandlung = (khbanzahlaufenhalte == 0 ? 0 : (khbanzahlaufenhalte > 1 ? 2 : 1));
        /** SPEC20-16 */qsData.setKHBEHANDLUNG(of.createDasQsDataTypeKHBEHANDLUNG());
        qsData.getKHBEHANDLUNG().setValue(khbehandlung);

        /** SPEC20-17 */qsData.setKHBEGINNDATUM(of.createDasQsDataTypeKHBEGINNDATUM());
        /** SPEC20-18 */qsData.setKHENDEDATUM(of.createDasQsDataTypeKHENDEDATUM());

        if (laengsterAufenthalt.isPresent()) { // <==> khbehandlung > 0
            qsData.getKHBEGINNDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(laengsterAufenthalt.get().getFrom()));
            Date to = SYSCalendar.min(laengsterAufenthalt.get().getTo(), JavaTimeConverter.toDate(STICHTAG)); // nicht weiter als heute
            qsData.getKHENDEDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(to));
        }

        if (khbehandlung > 0) log.debug("KHBEHANDLUNG " + khbehandlung + "  KHBANZAHLTAGE " + khbanzahltage);

        // seit SPEC20 keine Beatmung mehr

        /** SPEC30-19 */qsData.setBEWUSSTSEINSZUSTAND(of.createDasQsDataTypeBEWUSSTSEINSZUSTAND());
        ResInfo bewusst = ResInfoTools.getValidOnThatDayIfAny(resident, BEWUSST, STICHTAG).get();
        int bewzustand = Integer.valueOf(ResInfoTools.getContent(bewusst).getProperty("BEWUSSTSEINSZUSTAND"));
        // seit Version 3.0 kein Wachkoma mehr hier verwenden.
        // Bereinigung
        if (bewzustand == 5) bewzustand = 4; // wachkoma gilt hier als komatös
        report.append("- Bewusstsseinszustand: **" + List.of("wach", "somnolent", "soporös", "komatös").get(bewzustand - 1) + "**" + NL);

        if (bewzustand < 5)
            qsData.getBEWUSSTSEINSZUSTAND().setValue(bewzustand);

        /** SPEC20-18 */qsData.getDIAGNOSEN().addAll(getAktuelleDiagnosenFuerQDVS(resident));


    }

    private void mobilitaet(DasQsDataType qsData, Resident resident) {
        // das nennen wir dann letzter_abfragezeitpunkt
        ResInfo mobil = ResInfoTools.getValidOnThatDayIfAny(resident, MOBIL, STICHTAG).get();

        // SPEC30-21 Positionswechsel im Bett
        qsData.setMOBILPOSWECHSEL(of.createDasQsDataTypeMOBILPOSWECHSEL());
        qsData.getMOBILPOSWECHSEL().setValue(Integer.valueOf(ResInfoTools.getContent(mobil).getProperty("MOBILPOSWECHSEL")));
        report.append("- Positionswechsel im Bett: **" + selbststaendig.get(qsData.getMOBILPOSWECHSEL().getValue()) + "**" + NL);

        // SPEC30-22 Halten einer stabilen Sitzposition
        qsData.setMOBILSITZPOSITION(of.createDasQsDataTypeMOBILSITZPOSITION());
        qsData.getMOBILSITZPOSITION().setValue(Integer.valueOf(ResInfoTools.getContent(mobil).getProperty("MOBILSITZPOSITION")));
        report.append("- Halten einer stabilen Sitzposition: **" + selbststaendig.get(qsData.getMOBILSITZPOSITION().getValue()) + "**" + NL);

        // SPEC30-23 Sich Umsetzen
        qsData.setMOBILUMSETZEN(of.createDasQsDataTypeMOBILUMSETZEN());
        qsData.getMOBILUMSETZEN().setValue(Integer.valueOf(ResInfoTools.getContent(mobil).getProperty("MOBILUMSETZEN")));
        report.append("- Sich Umsetzen: **" + selbststaendig.get(qsData.getMOBILUMSETZEN().getValue()) + "**" + NL);

        // SPEC30-24 Fortbewegen innerhalb des Wohnbereichs
        qsData.setMOBILFORTBEWEGUNG(of.createDasQsDataTypeMOBILFORTBEWEGUNG());
        qsData.getMOBILFORTBEWEGUNG().setValue(Integer.valueOf(ResInfoTools.getContent(mobil).getProperty("MOBILFORTBEWEGUNG")));
        report.append("- Fortbewegen innerhalb des Wohnbereichs: **" + selbststaendig.get(qsData.getMOBILFORTBEWEGUNG().getValue()) + "**" + NL);

        // SPEC30-25 Treppensteigen
        qsData.setMOBILTREPPENSTEIGEN(of.createDasQsDataTypeMOBILTREPPENSTEIGEN());
        qsData.getMOBILTREPPENSTEIGEN().setValue(Integer.valueOf(ResInfoTools.getContent(mobil).getProperty("MOBILTREPPENSTEIGEN")));
        report.append("- Treppensteigen: **" + selbststaendig.get(qsData.getMOBILTREPPENSTEIGEN().getValue()) + "**" + NL);
    }

    private void kognitiv_kommunikativ(DasQsDataType qsData, Resident resident) {
        ResInfo orient = ResInfoTools.getValidOnThatDayIfAny(resident, ORIENT, STICHTAG).get();

        // SPEC30-26 Erkennen von Personen aus dem näheren Umfeld
        qsData.setKKFERKENNEN(of.createDasQsDataTypeKKFERKENNEN());
        qsData.getKKFERKENNEN().setValue(Integer.valueOf(ResInfoTools.getContent(orient).getProperty("KKFERKENNEN")));
        report.append("- Erkennen von Personen aus dem näheren Umfeld: **" + erkennen.get(qsData.getKKFERKENNEN().getValue()) + "**" + NL);

        // SPEC30-27 Örtliche Orientierung
        qsData.setKKFORIENTOERTLICH(of.createDasQsDataTypeKKFORIENTOERTLICH());
        qsData.getKKFORIENTOERTLICH().setValue(Integer.valueOf(ResInfoTools.getContent(orient).getProperty("KKFORIENTOERTLICH")));
        report.append("- Örtliche Orientierung: **" + erkennen.get(qsData.getKKFORIENTOERTLICH().getValue()) + "**" + NL);

        // SPEC30-28 Zeitliche Orientierung
        qsData.setKKFORIENTZEITLICH(of.createDasQsDataTypeKKFORIENTZEITLICH());
        qsData.getKKFORIENTZEITLICH().setValue(Integer.valueOf(ResInfoTools.getContent(orient).getProperty("KKFORIENTZEITLICH")));
        report.append("- Zeitliche Orientierung: **" + erkennen.get(qsData.getKKFORIENTZEITLICH().getValue()) + "**" + NL);

        // SPEC30-29 Sich Erinnern
        qsData.setKKFERINNERN(of.createDasQsDataTypeKKFERINNERN());
        qsData.getKKFERINNERN().setValue(Integer.valueOf(ResInfoTools.getContent(orient).getProperty("KKFERINNERN")));
        report.append("- Sich Erinnern: **" + erkennen.get(qsData.getKKFERINNERN().getValue()) + "**" + NL);

        // SPEC30-30 Steuern von mehrschrittigen Alltagshandlungen
        qsData.setKKFHANDLUNGEN(of.createDasQsDataTypeKKFHANDLUNGEN());
        qsData.getKKFHANDLUNGEN().setValue(Integer.valueOf(ResInfoTools.getContent(orient).getProperty("KKFHANDLUNGEN")));
        report.append("- Steuern von mehrschrittigen Alltagshandlungen: **" + erkennen.get(qsData.getKKFHANDLUNGEN().getValue()) + "**" + NL);

        // SPEC30-31 Treffen von Entscheidungen im Alltagsleben
        qsData.setKKFENTSCHEIDUNGEN(of.createDasQsDataTypeKKFENTSCHEIDUNGEN());
        qsData.getKKFENTSCHEIDUNGEN().setValue(Integer.valueOf(ResInfoTools.getContent(orient).getProperty("KKFENTSCHEIDUNGEN")));
        report.append("- Treffen von Entscheidungen im Alltagsleben: **" + erkennen.get(qsData.getKKFENTSCHEIDUNGEN().getValue()) + "**" + NL);

        // SPEC30-32 Verstehen von Sachverhalten und Informationen
        qsData.setKKFVERSTEHENINFO(of.createDasQsDataTypeKKFVERSTEHENINFO());
        qsData.getKKFVERSTEHENINFO().setValue(Integer.valueOf(ResInfoTools.getContent(orient).getProperty("KKFVERSTEHENINFO")));
        report.append("- Verstehen von Sachverhalten und Informationen: **" + erkennen.get(qsData.getKKFVERSTEHENINFO().getValue()) + "**" + NL);

        // SPEC30-33 Erkennen von Risiken und Gefahren
        qsData.setKKFGEFAHRERKENNEN(of.createDasQsDataTypeKKFGEFAHRERKENNEN());
        qsData.getKKFGEFAHRERKENNEN().setValue(Integer.valueOf(ResInfoTools.getContent(orient).getProperty("KKFGEFAHRERKENNEN")));
        report.append("- Erkennen von Risiken und Gefahren: **" + erkennen.get(qsData.getKKFGEFAHRERKENNEN().getValue()) + "**" + NL);

        // SPEC30-34 Mitteilen von elementaren Bedürfnissen
        qsData.setKKFMITTEILEN(of.createDasQsDataTypeKKFMITTEILEN());
        qsData.getKKFMITTEILEN().setValue(Integer.valueOf(ResInfoTools.getContent(orient).getProperty("KKFMITTEILEN")));
        report.append("- Mitteilen von elementaren Bedürfnissen: **" + erkennen.get(qsData.getKKFMITTEILEN().getValue()) + "**" + NL);

        // SPEC30-35 Verstehen von Aufforderungen
        qsData.setKKFVERSTEHENAUF(of.createDasQsDataTypeKKFVERSTEHENAUF());
        qsData.getKKFVERSTEHENAUF().setValue(Integer.valueOf(ResInfoTools.getContent(orient).getProperty("KKFVERSTEHENAUF")));
        report.append("- erstehen von Aufforderungen: **" + erkennen.get(qsData.getKKFVERSTEHENAUF().getValue()) + "**" + NL);

        // SPEC30-36 Beteiligung an einem Gespräch
        qsData.setKKFBETEILIGUNG(of.createDasQsDataTypeKKFBETEILIGUNG());
        qsData.getKKFBETEILIGUNG().setValue(Integer.valueOf(ResInfoTools.getContent(orient).getProperty("KKFBETEILIGUNG")));
        report.append("- Beteiligung an einem Gespräch: **" + erkennen.get(qsData.getKKFBETEILIGUNG().getValue()) + "**" + NL);

    }

    private void selbstversorgung(DasQsDataType qsData, Resident resident) {


        //┏━┓╻ ╻┏━┓┏━┓┏━╸╻ ╻┏━╸╻╺┳┓╻ ╻┏┓╻┏━╸┏━╸┏┓╻
        //┣━┫┃ ┃┗━┓┗━┓┃  ┣━┫┣╸ ┃ ┃┃┃ ┃┃┗┫┃╺┓┣╸ ┃┗┫
        //╹ ╹┗━┛┗━┛┗━┛┗━╸╹ ╹┗━╸╹╺┻┛┗━┛╹ ╹┗━┛┗━╸╹ ╹
        report.append("#### Ausscheidungen" + NL);
        ResInfo aus = ResInfoTools.getValidOnThatDayIfAny(resident, AUSSCHEID, STICHTAG).get();
        /** SPEC30-40 */qsData.setSVHARNKONTINENZ(of.createDasQsDataTypeSVHARNKONTINENZ());
        qsData.getSVHARNKONTINENZ().setValue(Integer.valueOf(ResInfoTools.getContent(aus).getProperty("SVHARNKONTINENZ")));
        report.append("- Blasenkontrolle/Harnkontinenz: **" + kontinenz.get(qsData.getSVHARNKONTINENZ().getValue()) + "**" + NL);
        /** SPEC30-52 */qsData.setSVHARNKONTINENZBEW(of.createDasQsDataTypeSVHARNKONTINENZBEW());
        if (qsData.getSVHARNKONTINENZ().getValue() >= 2) { // wenn Feld 44 = 2, 3 oder 4
            qsData.getSVHARNKONTINENZBEW().setValue(Integer.valueOf(ResInfoTools.getContent(aus).getProperty("SVHARNKONTINENZBEW")));
            report.append("  - Bewältigung der Folgen einer Harninkontinenz (auch Umgang mit Dauerkatheter/Urostoma): **" + selbststaendig.get(qsData.getSVHARNKONTINENZBEW().getValue()) + "**" + NL);
        }

        /** SPEC30-41 */qsData.setSVSTUHLKONTINENZ(of.createDasQsDataTypeSVSTUHLKONTINENZ());
        qsData.getSVSTUHLKONTINENZ().setValue(Integer.valueOf(ResInfoTools.getContent(aus).getProperty("SVSTUHLKONTINENZ")));
        report.append("- Darmkontrolle/Stuhlkontinenz: **" + kontinenz.get(qsData.getSVSTUHLKONTINENZ().getValue()) + "**" + NL);
        /** SPEC30-53 */qsData.setSVSTUHLKONTINENZBEW(of.createDasQsDataTypeSVSTUHLKONTINENZBEW());
        if (qsData.getSVSTUHLKONTINENZ().getValue() >= 2) { // wenn Feld 45 = 2, 3 oder 4
            qsData.getSVSTUHLKONTINENZBEW().setValue(Integer.valueOf(ResInfoTools.getContent(aus).getProperty("SVSTUHLKONTINENZBEW")));
            report.append("  - Bewältigung der Folgen einer Harninkontinenz (auch Umgang mit Dauerkatheter/Urostoma): **" + selbststaendig.get(qsData.getSVSTUHLKONTINENZBEW().getValue()) + "**" + NL);
        }

        /** SPEC30-51 */qsData.setSVTOILETTE(of.createDasQsDataTypeSVTOILETTE());
        qsData.getSVTOILETTE().setValue(Integer.valueOf(ResInfoTools.getContent(aus).getProperty("SVTOILETTE")));
        report.append("- Benutzen einer Toilette oder eines Toilettenstuhls: **" + selbststaendig246.get(qsData.getSVTOILETTE().getValue()) + "**" + NL);


        //┏┓ ┏━╸╻ ╻┏━╸┏━┓╺┳╸╻ ╻┏┓╻┏━╸
        //┣┻┓┣╸ ┃╻┃┣╸ ┣┳┛ ┃ ┃ ┃┃┗┫┃╺┓
        //┗━┛┗━╸┗┻┛┗━╸╹┗╸ ╹ ┗━┛╹ ╹┗━┛
        report.append("#### Körperpflege" + NL);
        ResInfo kpflege = ResInfoTools.getValidOnThatDayIfAny(resident, KPFLEGE, STICHTAG).get();
        /** SPEC30-42 */qsData.setSVOBERKOERPER(of.createDasQsDataTypeSVOBERKOERPER());
        qsData.getSVOBERKOERPER().setValue(Integer.valueOf(ResInfoTools.getContent(kpflege).getProperty("SVOBERKOERPER")));
        report.append("- Waschen des vorderen Oberkörpers: **" + selbststaendig.get(qsData.getSVOBERKOERPER().getValue()) + "**" + NL);
        /** SPEC30-43 */qsData.setSVKOPF(of.createDasQsDataTypeSVKOPF());
        qsData.getSVKOPF().setValue(Integer.valueOf(ResInfoTools.getContent(kpflege).getProperty("SVKOPF")));
        report.append("- Körperpflege im Bereich des Kopfes: **" + selbststaendig.get(qsData.getSVKOPF().getValue()) + "**" + NL);
        /** SPEC30-44 */qsData.setSVINTIMBEREICH(of.createDasQsDataTypeSVINTIMBEREICH());
        qsData.getSVINTIMBEREICH().setValue(Integer.valueOf(ResInfoTools.getContent(kpflege).getProperty("SVINTIMBEREICH")));
        report.append("- Waschen des Intimbereichs: **" + selbststaendig.get(qsData.getSVINTIMBEREICH().getValue()) + "**" + NL);
        /** SPEC30-45 */qsData.setSVDUSCHENBADEN(of.createDasQsDataTypeSVDUSCHENBADEN());
        qsData.getSVDUSCHENBADEN().setValue(Integer.valueOf(ResInfoTools.getContent(kpflege).getProperty("SVDUSCHENBADEN")));
        report.append("- Duschen oder Baden einschließlich Waschen der Haare: **" + selbststaendig.get(qsData.getSVDUSCHENBADEN().getValue()) + "**" + NL);
        /** SPEC30-46 */qsData.setSVANAUSOBERKOERPER(of.createDasQsDataTypeSVANAUSOBERKOERPER());
        qsData.getSVANAUSOBERKOERPER().setValue(Integer.valueOf(ResInfoTools.getContent(kpflege).getProperty("SVANAUSOBERKOERPER")));
        report.append("- An- und Auskleiden des Oberkörpers: **" + selbststaendig.get(qsData.getSVANAUSOBERKOERPER().getValue()) + "**" + NL);
        /** SPEC30-47 */qsData.setSVANAUSUNTERKOERPER(of.createDasQsDataTypeSVANAUSUNTERKOERPER());
        qsData.getSVANAUSUNTERKOERPER().setValue(Integer.valueOf(ResInfoTools.getContent(kpflege).getProperty("SVANAUSUNTERKOERPER")));
        report.append("- An- und Auskleiden des Unterkörpers: **" + selbststaendig.get(qsData.getSVANAUSUNTERKOERPER().getValue()) + "**" + NL);

        // ERNÄHRUNG
        report.append("#### Ernährung" + NL);
        ResInfo ern = ResInfoTools.getValidOnThatDayIfAny(resident, ERN, STICHTAG).get();
        /** SPEC30-48 */qsData.setSVNAHRUNGZUBEREITEN(of.createDasQsDataTypeSVNAHRUNGZUBEREITEN());
        qsData.getSVNAHRUNGZUBEREITEN().setValue(Integer.valueOf(ResInfoTools.getContent(ern).getProperty("SVNAHRUNGZUBEREITEN")));
        report.append("- Mundgerechtes Zubereiten der Nahrung, Eingießen von Getränken: **" + selbststaendig.get(qsData.getSVNAHRUNGZUBEREITEN().getValue()) + "**" + NL);
        /** SPEC30-49 */qsData.setSVESSEN(of.createDasQsDataTypeSVESSEN());
        qsData.getSVESSEN().setValue(Integer.valueOf(ResInfoTools.getContent(ern).getProperty("SVESSEN")));
        report.append("- Essen: **" + selbststaendig369.get(qsData.getSVESSEN().getValue()) + "**" + NL);
        /** SPEC30-50 */qsData.setSVTRINKEN(of.createDasQsDataTypeSVTRINKEN());
        qsData.getSVTRINKEN().setValue(Integer.valueOf(ResInfoTools.getContent(ern).getProperty("SVTRINKEN")));
        report.append("- Trinken: **" + selbststaendig246.get(qsData.getSVTRINKEN().getValue()) + "**" + NL);

        //╻┏ ╻ ╻┏━╸┏┓╻┏━┓╺┳╸╻  ╻┏━╸╻ ╻┏━╸   ┏━╸┏━┓┏┓╻┏━┓┏━╸╻ ╻┏━┓╻ ╻┏┓╻┏━╸
        //┣┻┓┃ ┃┣╸ ┃┗┫┗━┓ ┃ ┃  ┃┃  ┣━┫┣╸    ┣╸ ┣┳┛┃┗┫┣━┫┣╸ ┣━┫┣┳┛┃ ┃┃┗┫┃╺┓
        //╹ ╹┗━┛┗━╸╹ ╹┗━┛ ╹ ┗━╸╹┗━╸╹ ╹┗━╸   ┗━╸╹┗╸╹ ╹╹ ╹┗━╸╹ ╹╹┗╸┗━┛╹ ╹┗━┛
        /** SPEC30-37 */qsData.setSVERNAEHRUNG(of.createDasQsDataTypeSVERNAEHRUNG());
        /** SPEC30-38 */qsData.setSVFREMDHILFE(of.createDasQsDataTypeSVFREMDHILFE());
        /** SPEC30-39 */qsData.setSVERNAEHRUNGUMFANG(of.createDasQsDataTypeSVERNAEHRUNGUMFANG());

        // "kern01" künstliche Ernährung ist eine optionale resinfo. Falls sie nicht existiert, wird Frage 41. einfach auf 0 gesetzt.
        Optional<ResInfo> kern = ResInfoTools.getValidOnThatDayIfAny(resident, KERN, STICHTAG);
        if (kern.isPresent()) { // Sobald ein kern01 Eintrag Vorliergt ist die SVERNAEHRUNG gesetzt.
            report.append("- Bewohner wird künstlich ernährt" + NL);
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
    }

    private void alltag_soziales(DasQsDataType qsData, Resident resident) {
        ResInfo alltag = ResInfoTools.getValidOnThatDayIfAny(resident, ALLTAG, STICHTAG).get();
        ResInfo schlaf = ResInfoTools.getValidOnThatDayIfAny(resident, SCHLAF, STICHTAG).get();

        /** SPEC30-54 */qsData.setGATAGESABLAUF(of.createDasQsDataTypeGATAGESABLAUF());
        qsData.getGATAGESABLAUF().setValue(Integer.valueOf(ResInfoTools.getContent(alltag).getProperty("GATAGESABLAUF")));
        report.append("- Tagesablauf gestalten und an Veränderungen anpassen: **" + selbststaendig.get(qsData.getGATAGESABLAUF().getValue()) + "**" + NL);
        /** SPEC30-55 */qsData.setGARUHENSCHLAFEN(of.createDasQsDataTypeGARUHENSCHLAFEN());
        qsData.getGARUHENSCHLAFEN().setValue(Integer.valueOf(ResInfoTools.getContent(schlaf).getProperty("GARUHENSCHLAFEN")));
        report.append("- Ruhen und Schlafen: **" + selbststaendig.get(qsData.getGARUHENSCHLAFEN().getValue()) + "**" + NL);
        /** SPEC30-56 */qsData.setGABESCHAEFTIGEN(of.createDasQsDataTypeGABESCHAEFTIGEN());
        qsData.getGABESCHAEFTIGEN().setValue(Integer.valueOf(ResInfoTools.getContent(alltag).getProperty("GABESCHAEFTIGEN")));
        report.append("- Sich beschäftigen: **" + selbststaendig.get(qsData.getGABESCHAEFTIGEN().getValue()) + "**" + NL);
        /** SPEC30-57 */qsData.setGAPLANUNGEN(of.createDasQsDataTypeGAPLANUNGEN());
        qsData.getGAPLANUNGEN().setValue(Integer.valueOf(ResInfoTools.getContent(alltag).getProperty("GAPLANUNGEN")));
        report.append("- In die Zukunft gerichtete Planungen vornehmen: **" + selbststaendig.get(qsData.getGAPLANUNGEN().getValue()) + "**" + NL);

        ResInfo sozial = ResInfoTools.getValidOnThatDayIfAny(resident, SOZIAL, STICHTAG).get();
        /** SPEC30-58 */qsData.setGAINTERAKTION(of.createDasQsDataTypeGAINTERAKTION());
        qsData.getGAINTERAKTION().setValue(Integer.valueOf(ResInfoTools.getContent(sozial).getProperty("GAINTERAKTION")));
        report.append("- Interaktion mit Personen im direkten Kontakt: **" + selbststaendig.get(qsData.getGAINTERAKTION().getValue()) + "**" + NL);
        /** SPEC30-59 */qsData.setGAKONTAKTPFLEGE(of.createDasQsDataTypeGAKONTAKTPFLEGE());
        qsData.getGAKONTAKTPFLEGE().setValue(Integer.valueOf(ResInfoTools.getContent(sozial).getProperty("GAKONTAKTPFLEGE")));
        report.append("- Kontaktpflege zu Personen außerhalb des direkten Umfeldes: **" + selbststaendig.get(qsData.getGAKONTAKTPFLEGE().getValue()) + "**" + NL);
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
        ArrayList<ResInfo> liste_wunden = ResInfoTools.getAll(resident, ResInfoTypeTools.TYPE_ALL_WOUNDS, BEGINN_ERFASSUNGSZEITRAUM, STICHTAG);

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

        // Jetzt nehme ich mir jeden Wundverlauf einzeln vor und möchte wissen:
        // entstehungsort
        // maximaler Grad
        // Beginn der Wunde.
        // Ende der Wunde (oder noch nicht beendet)
        //
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

        if (auswertung_dekubitus.isEmpty())
            report.append("Kein Dekubitus" + NL);
        else {
            report.append("|Wundverlauf|wo entstanden|max grad|von|bis|" + NL);
            report.append("|---|---|---|---|---|" + NL);
        }

        auswertung_dekubitus.values().forEach(quintet ->
                report.append(String.format("|%s|%s|%s|%s|%s|" + NL,
                        quintet.getValue0(),
                        dekub_lok.get(quintet.getValue1()),
                        quintet.getValue2(),
                        quintet.getValue3().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)),
                        quintet.getValue4().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
                )));
        report.append(NL);

        // sortiere die Liste nach startdatum (zeitlich letzten) - (ConnectionID, entehungsort, grad, beginn - Value3, ende)
        ArrayList<Quintet<Long, Integer, Integer, LocalDateTime, LocalDateTime>> listDekubitus = new ArrayList<>(auswertung_dekubitus.values().stream().sorted(Comparator.comparing(Quintet::getValue3)).collect(Collectors.toList()));
        // danach drehe ich die Liste um und nehme mir die ersten beiden, falls vorhanden.
        Collections.reverse(listDekubitus);
        // Aus dieser Liste nehme ich nun die ersten beiden Einträge, wenn es denn welche gibt.
        Optional<Quintet<Long, Integer, Integer, LocalDateTime, LocalDateTime>> optWunde1 = Optional.ofNullable(listDekubitus.size() >= 1 ? listDekubitus.get(0) : null);
        Optional<Quintet<Long, Integer, Integer, LocalDateTime, LocalDateTime>> optWunde2 = Optional.ofNullable(listDekubitus.size() > 1 ? listDekubitus.get(1) : null);

        // Formalitäten
        /** SPEC30-60 */qsData.setDEKUBITUS(of.createDasQsDataTypeDEKUBITUS());
        /** SPEC30-61 */qsData.setDEKUBITUSSTADIUM(of.createDasQsDataTypeDEKUBITUSSTADIUM());
        /** SPEC30-62 */qsData.setDEKUBITUS1BEGINNDATUM(of.createDasQsDataTypeDEKUBITUS1BEGINNDATUM());
        /** SPEC30-63 */qsData.setDEKUBITUS1ENDEDATUM(of.createDasQsDataTypeDEKUBITUS1ENDEDATUM());
        /** SPEC30-64 */qsData.setDEKUBITUS1LOK(of.createDasQsDataTypeDEKUBITUS1LOK());
        /** SPEC30-65 */qsData.setDEKUBITUS2BEGINNDATUM(of.createDasQsDataTypeDEKUBITUS2BEGINNDATUM());
        /** SPEC30-66 */qsData.setDEKUBITUS2ENDEDATUM(of.createDasQsDataTypeDEKUBITUS2ENDEDATUM());
        /** SPEC30-67 */qsData.setDEKUBITUS2LOK(of.createDasQsDataTypeDEKUBITUS2LOK());

        // Die Analyse der XML Auswertungen hat ergeben, dass wenn der maximale Wert für DEKUBITUS >1, dann werden alle Beginn- und Enddaten erwartet.
        // Auch wenn eine von den beiden Wunden nicht über 1 hinausgekommen ist.
        int maximales_dekubitus_stadium = optWunde1.isPresent() ? optWunde1.get().getValue2() : 0;
        maximales_dekubitus_stadium = Math.max(maximales_dekubitus_stadium, optWunde2.isPresent() ? optWunde2.get().getValue2() : 0);

        /** SPEC20-60 */
        int dekubitus_schluessel = 0; // wenn kein dekubitus vorhanden
        if (listDekubitus.size() == 1) dekubitus_schluessel = 1; // genau einer
        if (listDekubitus.size() > 1) dekubitus_schluessel = 2; // mehr als einer
        qsData.getDEKUBITUS().setValue(dekubitus_schluessel);


        /** SPEC20-61 */
        if (dekubitus_schluessel > 0) {
            qsData.getDEKUBITUSSTADIUM().setValue(maximales_dekubitus_stadium);
        }

//        final int fin_dekubitus_schlussel = dekubitus_schluessel;
        optWunde1.ifPresent(wunde1 -> { // [Feld 60 = 1,2]
            // wenn [Feld 60 = 1,2] UND [Feld 61 = 2,3,4,9] UND [Feld 64 <> LEER] UND [Feld 64 = 1]
            if (qsData.getDEKUBITUSSTADIUM().getValue() > 1) { // Grad > 1  -> [Feld 61 = 2,3,4,9]

                /** SPEC20-64 */
                qsData.getDEKUBITUS1LOK().setValue(wunde1.getValue1());

                /** SPEC20-62 */
                if (qsData.getDEKUBITUS1LOK().getValue() == 1) // value1 enthält den entstehungs ort. 1 heisst "BEI UNS". [Feld 64 = 1]
                    qsData.getDEKUBITUS1BEGINNDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(wunde1.getValue3()));

                // wenn Bedingung 1: [Feld 60 = 1] UND [Feld 61 = 2,3,4,9] oder Bedingung 2: [Feld 60 = 2] UND ( [Feld 62 <> LEER] ODER [Feld 64 <> LEER] )
                /** SPEC20-63 */
                // Bedingung 1: [Feld 60 = 1] UND [Feld 61 = 2,3,4,9]
                boolean bedingung1 = qsData.getDEKUBITUS().getValue() == 1 && qsData.getDEKUBITUSSTADIUM().getValue() > 1;
                // Bedingung 2: [Feld 60 = 2] UND ( [Feld 62 <> LEER] ODER [Feld 64 <> LEER] )
                boolean bedingung2 = qsData.getDEKUBITUS().getValue() == 2 && (qsData.getDEKUBITUS1BEGINNDATUM().getValue() != null || qsData.getDEKUBITUS1LOK().getValue() != null);
                if (bedingung1 || bedingung2)
                    qsData.getDEKUBITUS1ENDEDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(JavaTimeConverter.min(wunde1.getValue4().toLocalDate(), STICHTAG.toLocalDate())));
            }
        });

        optWunde2.ifPresent(wunde2 -> { // Feld 60 = 2
            if (qsData.getDEKUBITUSSTADIUM().getValue() > 1) { // [Feld 61 = 2,3,4,9]
                /** SPEC20-67 */
                qsData.getDEKUBITUS2LOK().setValue(wunde2.getValue1()); // [Feld 67 <> LEER]
                /** SPEC20-65 */
                if (qsData.getDEKUBITUS2LOK().getValue() == 1) // [Feld 67 = 1]   Wunde bei uns entstanden.
                    qsData.getDEKUBITUS2BEGINNDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(wunde2.getValue3()));
                /** SPEC20-66 */
                // Feld 67 ist nie leer wenn wir bis hierher schon gekommen sind
                qsData.getDEKUBITUS2ENDEDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(JavaTimeConverter.min(wunde2.getValue4().toLocalDate(), STICHTAG.toLocalDate())));
            }
        });

        if (listDekubitus.size() > 1) {
            report.append("**Achtung, es gibt mindestens 2 Dekubitus Geschwüre. Ist das wirklich richtig ?**" + NL);
        }

    }

    private void gewicht(DasQsDataType qsData, Resident resident) {
        Optional<ResValue> weight = ResValueTools.getMostRecentBefore(resident, ResvaluetypesService.WEIGHT, STICHTAG);

        // seit SPEC20 keine Körpergröße mehr

        /** SPEC30-68 */qsData.setKOERPERGEWICHT(of.createDasQsDataTypeKOERPERGEWICHT());
        /** SPEC30-69 */qsData.setKOERPERGEWICHTDATUM(of.createDasQsDataTypeKOERPERGEWICHTDATUM());
        if (weight.isPresent()) {
            qsData.getKOERPERGEWICHT().setValue(weight.get().getVal1());
            qsData.getKOERPERGEWICHTDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(weight.get().getPit()));
            report.append(weight.get().getVal1() + " kg / " +
                    JavaTimeConverter.toJavaLocalDateTime(weight.get().getPit()).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)) + NL);
        } else {
            report.append("** ACHTUNG KEIN AKTUELLES GEWICHT VORHANDEN **" + NL);
        }

        /** SPEC30-70 */
        // Ich suche den letzten ResInfo mit einem Gewichts Kommentar. Wenn es einen gibt
        // dann werte ich den aus. Wenn nicht ist das Ergebnis 0, wenn er älter als das letzte Datum
        // ist setze ich den ebenfalls auf 0.
        Optional<ResInfo> gewichtdoku = ResInfoTools.getValidOnThatDayIfAny(resident, KOERPERGEWICHTDOKU, STICHTAG);
        // wenn sie fehlt oder zu alt ist, dann setzen wir das auf 0
        if (!gewichtdoku.isPresent() || JavaTimeConverter.toJavaLocalDateTime(gewichtdoku.get().getFrom()).isBefore(BEGINN_ERFASSUNGSZEITRAUM)) {
            DasQsDataType.KOERPERGEWICHTDOKU kd = of.createDasQsDataTypeKOERPERGEWICHTDOKU();
            kd.setValue(0);
            qsData.getKOERPERGEWICHTDOKU().add(kd);
            log.debug("KEINE GewichtsDoku (oder zu alt) - daher erzeuge ich eine und setze die Antwort auf 0");
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
                log.debug("Eintrag vorhanden, aber nicht angekreuzt. Gilt als 0");
            }
        }
    }

    private void sturz(DasQsDataType qsData, Resident resident) {
        // die sind nach FROM sortiert
        // die stürze werden teilweise aus fallprot01 und aus strzfolg01 ermittelt
        ArrayList<ResInfo> stuerze = ResInfoTools.getAll(resident, FALLTYPE, BEGINN_ERFASSUNGSZEITRAUM, STICHTAG);

        /** SPEC30-71 */qsData.setSTURZ(of.createDasQsDataTypeSTURZ());
        int sturz = 0;
        if (stuerze.size() == 1) sturz = 1;
        if (stuerze.size() > 1) sturz = 2;
        qsData.getSTURZ().setValue(sturz);

        report.append("Anzahl der Stürze: " + stuerze.size() + NL);

        /** SPEC30-72 */
        if (sturz > 0) { // es gab also mindestens einen Sturz

            // erhöter Unterstützungsbedarf
            ArrayList<ResInfo> auswirkungen = ResInfoTools.getAll(resident, FALLAUSWIRKUNG, BEGINN_ERFASSUNGSZEITRAUM, STICHTAG);
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

            report.append("#### Sturzfolgen" + NL);
            qsData.getSTURZFOLGEN().forEach(sturzfolgen -> report.append("- " + sturzfolg.get(sturzfolgen.getValue()) + NL));


        } else { // leer wenn es keinen Sturz gab
            DasQsDataType.STURZFOLGEN sf = of.createDasQsDataTypeSTURZFOLGEN();
            qsData.getSTURZFOLGEN().add(sf);
        }
    }

    private void fixierung(DasQsDataType qsData, Resident resident) {
        /** SPEC20-73 */
        // seit SPEC20 stark vereinfacht
        int gurt_haeufigkeit = 0;
        int gitter_haeufigkeit = 0;

        for (ResInfo resInfo : ResInfoTools.getAll(resident, FIXIERUNGSPROTOKOLLE, STICHTAG.minusWeeks(4), STICHTAG)) {
            LocalDateTime pit = JavaTimeConverter.toJavaLocalDateTime(resInfo.getFrom());
            Properties props = ResInfoTools.getContent(resInfo);
            if (props.getProperty("leibgurt", "false").equalsIgnoreCase("true") || props.getProperty("sitzgurt", "false").equalsIgnoreCase("true")) {
                gurt_haeufigkeit++;
            }
            if (props.getProperty("bettgitter", "false").equalsIgnoreCase("true")) {
                gitter_haeufigkeit++;
            }
        }

        if (gitter_haeufigkeit + gurt_haeufigkeit == 0) {
            report.append("keine Anwendung von Fixierungsmaßnahmen" + NL);
        } else {
            report.append("|Gurte|Bettgitter|" + NL);
            report.append("|---|---|" + NL);
            report.append(String.format("|%s|%s|" + NL2, gurt_haeufigkeit, gitter_haeufigkeit));
        }

        /** SPEC30-73 */
        qsData.setGURT(of.createDasQsDataTypeGURT());
        qsData.getGURT().setValue(Math.min(gurt_haeufigkeit, 1)); // umwandlung der Häufigkeit auf eine Nein / Ja Antwort

        /** SPEC30-74 */qsData.setSEITENTEILE(of.createDasQsDataTypeSEITENTEILE());
        qsData.getSEITENTEILE().setValue(Math.min(gitter_haeufigkeit, 1));
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

        List<ResInfo> list_schmerz = ResInfoTools.getAll(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_PAIN), BEGINN_ERFASSUNGSZEITRAUM, STICHTAG); // DESC sortiert
        List<ResInfo> list_besd = ResInfoTools.getAll(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_BESD), BEGINN_ERFASSUNGSZEITRAUM, STICHTAG); // DESC sortiert

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

        /** SPEC30-75 */qsData.setSCHMERZEN(of.createDasQsDataTypeSCHMERZEN());
        qsData.getSCHMERZEN().setValue(SCHMERZEN);
        /** SPEC30-76 */qsData.setSCHMERZFREI(of.createDasQsDataTypeSCHMERZFREI());
        /** SPEC30-77 */qsData.setSCHMERZEINSCH(of.createDasQsDataTypeSCHMERZEINSCH());
        /** SPEC30-78 */qsData.setSCHMERZEINSCHDATUM(of.createDasQsDataTypeSCHMERZEINSCHDATUM());

        if (SCHMERZEN == 1) {
            report.append("- Bewohner hat Schmerzen" + NL);
            // fixed 240710
            // wenn jemand schmerzfrei durch MEDIs ist, dann bleiben die Felder 76-79, MÜSSEN aber trotzdem in
            // der XML Datei drinstehen. Nur eben ohne Werte.
            // Bei den verwendeten Formularen gehe ich immer von einer differenzierten Einschätzung aus
            int schmerzeinsch_liegt_vor;
            if (SCHMERZFREI > 0) {
                report.append("  - ist aber schmerzfrei durch Medikamente" + NL);
                // allerdings nur, wenn die Schmerzfreiheit NICHT durch Medikamente erreicht wurde.
                schmerzeinsch_liegt_vor = 0;
                // ein leerer Eintrag ist nötig, sonst schimpft die XSD
                DasQsDataType.SCHMERZEINSCHINFO infoleer = of.createDasQsDataTypeSCHMERZEINSCHINFO();
                qsData.getSCHMERZEINSCHINFO().add(infoleer);
            } else {
                schmerzeinsch_liegt_vor = 1;
                qsData.getSCHMERZEINSCH().setValue(schmerzeinsch_liegt_vor); // Ja, aber nur wenn Feld 76 == 0
                if (schmerzeinsch_liegt_vor > 0) {
                    qsData.getSCHMERZEINSCHDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(pit)); // JA
                    report.append("- Datum der letzten Schmerzeinschätzung " + pit.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)) + NL);

                    // SCHMERZE2 hat alle Infos
                    /** SPEC30-79 */
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
                }
            }

            // erhält nur einen Value, wenn es Schmerzen gibt. Sonst nicht.
            qsData.getSCHMERZFREI().setValue(SCHMERZFREI);

        } else { // hat er keine Schmerzen ist gar keine differenzierte Einschätzung nötig.
            // ein leerer Eintrag ist nötig, sonst schimpft die XSD
            DasQsDataType.SCHMERZEINSCHINFO infoleer = of.createDasQsDataTypeSCHMERZEINSCHINFO();
            qsData.getSCHMERZEINSCHINFO().add(infoleer);
            report.append("Keine Schmerzen feststellbar" + NL);
        }


    }

    private void einzug(DasQsDataType qsData, Resident resident) {
        // Datum des Einzugs
        /** 4 */qsData.setEINZUGSDATUM(of.createDasQsDataTypeEINZUGSDATUM());
        Optional<ResInfo> aufenthalt = ResInfoTools.getValidOnThatDayIfAny(resident, HAUF, STICHTAG);
        LocalDate beginn_aktueller_aufenthalt = JavaTimeConverter.toJavaLocalDateTime(aufenthalt.get().getFrom()).toLocalDate(); // evtl. voheriger KZP zählt nicht
        qsData.getEINZUGSDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(beginn_aktueller_aufenthalt));

        report.append("- Einzugsdatum: **" + beginn_aktueller_aufenthalt.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)) + "**" + NL);

        // Ist der Bewohner bzw. die Bewohnerin nach der letzten Ergebniserfassung neu in die Einrichtung eingezogen?
        /** SPEC30-80 */qsData.setNEUEINZUG(of.createDasQsDataTypeNEUEINZUG());
        // Neu eingezogen gilt, wenn er seit der letzten Erfassung eingezogen ist.
        int NEUEINZUG = beginn_aktueller_aufenthalt.compareTo(BEGINN_ERFASSUNGSZEITRAUM.toLocalDate()) >= 0 ? 1 : 0; //
        qsData.getNEUEINZUG().setValue(NEUEINZUG);

        /** SPEC30-81 */qsData.setEINZUGNACHKZP(of.createDasQsDataTypeEINZUGNACHKZP());
        /** SPEC30-82 */qsData.setEINZUGNACHKZPDATUM(of.createDasQsDataTypeEINZUGNACHKZPDATUM());
        /** SPEC30-83 */qsData.setEINZUGKHBEHANDLUNG(of.createDasQsDataTypeEINZUGKHBEHANDLUNG());
        /** SPEC30-84 */qsData.setEINZUGKHBEGINNDATUM(of.createDasQsDataTypeEINZUGKHBEGINNDATUM());
        /** SPEC30-85 */qsData.setEINZUGKHENDEDATUM(of.createDasQsDataTypeEINZUGKHENDEDATUM());
        /** SPEC30-86 */qsData.setEINZUGGESPR(of.createDasQsDataTypeEINZUGGESPR());
        /** SPEC30-87 */qsData.setEINZUGGESPRDATUM(of.createDasQsDataTypeEINZUGGESPRDATUM());
        /** SPEC30-89 */qsData.setEINZUGGESPRDOKU(of.createDasQsDataTypeEINZUGGESPRDOKU());

        if (NEUEINZUG == 1) { /** 87 */
            report.append("  - Neu eingezogen" + NL);
            // Erfolgte der Einzug direkt im Anschluss an einen Kurzzeitpflegeaufenthalt in der Einrichtung (ohne zeitliche Lücke)?
            qsData.getEINZUGNACHKZP().setValue(0);
            if (ResInfoTools.getContent(aufenthalt.get()).getProperty("stay").equalsIgnoreCase(ResInfoTypeTools.STAY_VALUE_NOW_PERMANENT)) {
                /** 88 */qsData.getEINZUGNACHKZP().setValue(1);
                Optional<ResInfo> kzp = ResInfoTools.getKZPvorDauerhaft(aufenthalt.get());
                /** 89 */
                // Datum: Beginn des Kurzzeitpflegeaufenthalts
                kzp.ifPresent(resInfo -> qsData.getEINZUGNACHKZPDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(resInfo.getFrom())));
            }

            // Ist der Bewohner bzw. die Bewohnerin innerhalb der ersten 8 Wochen nach dem Einzug länger als drei Tage in einem Krankenhaus versorgt worden?
            /** 90 */qsData.getEINZUGKHBEHANDLUNG().setValue(0);
            List<ResInfo> listKH8WochenNachEinzug = ResInfoTools.getAll(resident, ABWESENHEIT, beginn_aktueller_aufenthalt.atStartOfDay(), beginn_aktueller_aufenthalt.plusWeeks(8).atTime(23, 59, 59)).stream().sorted(Comparator.comparing(ResInfo::getFrom)).collect(Collectors.toList());
            for (ResInfo resInfo : listKH8WochenNachEinzug) {
                LocalDate start = JavaTimeConverter.toJavaLocalDateTime(resInfo.getFrom()).toLocalDate();
                Date from = resInfo.getTo().after(JavaTimeConverter.toDate(STICHTAG)) ? JavaTimeConverter.toDate(STICHTAG) : resInfo.getTo();
                LocalDate ende = JavaTimeConverter.toJavaLocalDateTime(from).toLocalDate();

                // +1, weil abreise und ankunftstag mitgerechnet werden als abwesenheit
                if (ChronoUnit.DAYS.between(start, ende) + 1 > 3) { // der erste aufenthalt, der länger als 3 Tage ist.
                    /** 90 */qsData.getEINZUGKHBEHANDLUNG().setValue(1);
                    /** 91 */qsData.getEINZUGKHBEGINNDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(start));
                    /** 92 */qsData.getEINZUGKHENDEDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(ende));
                    break;
                }
            }
            // Ist in den Wochen nach dem Einzug mit dem Bewohner bzw. der Bewohnerin und/oder einer seiner bzw. ihrer Angehörigen
            // oder sonstigen Vertrauenspersonen ein Gespräch über sein bzw. ihr Einleben und die zuküntiige Versorgung geführt worden?
            ResInfo integration = ResInfoTools.getLastResinfo(resident, INTEGRATION);
            // kann sein, wenn der Einzug kurz vor dem Ende des EEZ (Ergebniserfassungszeitraum) war und das Integrationsgespräch noch nicht stattgefunden hat.
            if (integration == null || integration.getFrom().after(JavaTimeConverter.toDate(STICHTAG))) {
                /**  SPEC30-88 */
                qsData.getEINZUGGESPRTEILNEHMER().add(of.createDasQsDataTypeEINZUGGESPRTEILNEHMER()); // leer
                /**  SPEC30-86 */qsData.getEINZUGGESPR().setValue(3); // nein aus anderen Gründen
                report.append("- Bisher kein Integrationsgespräch" + NL);
            } else {
                Properties content = ResInfoTools.getContent(integration);
                qsData.getEINZUGGESPR().setValue(Integer.valueOf(content.getProperty("EINZUGGESPR")));
                if (qsData.getEINZUGGESPR().getValue() == 1) {
                    report.append("- Integrationsgespräch am " + JavaTimeConverter.toJavaLocalDateTime(integration.getFrom()).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)) + NL);
                    qsData.getEINZUGGESPRDATUM().setValue(JavaTimeConverter.toXMLGregorianCalendar(integration.getFrom()));

                    /** SPEC30-88 */
                    DasQsDataType.EINZUGGESPRTEILNEHMER et0 = of.createDasQsDataTypeEINZUGGESPRTEILNEHMER();
                    DasQsDataType.EINZUGGESPRTEILNEHMER et1 = of.createDasQsDataTypeEINZUGGESPRTEILNEHMER();
                    DasQsDataType.EINZUGGESPRTEILNEHMER et2 = of.createDasQsDataTypeEINZUGGESPRTEILNEHMER();
                    DasQsDataType.EINZUGGESPRTEILNEHMER et3 = of.createDasQsDataTypeEINZUGGESPRTEILNEHMER();
                    DasQsDataType.EINZUGGESPRTEILNEHMER et4 = of.createDasQsDataTypeEINZUGGESPRTEILNEHMER();

                    et0.setValue(0);
                    et1.setValue(1);
                    et2.setValue(2);
                    et3.setValue(3);
                    et4.setValue(4);

                    // wir so eingetragen wie im zugehörigen ResInfo Element
                    if (content.getProperty("1").equalsIgnoreCase("true")) qsData.getEINZUGGESPRTEILNEHMER().add(et1);
                    if (content.getProperty("2").equalsIgnoreCase("true")) qsData.getEINZUGGESPRTEILNEHMER().add(et2);
                    if (content.getProperty("3").equalsIgnoreCase("true")) qsData.getEINZUGGESPRTEILNEHMER().add(et3);
                    if (content.getProperty("4").equalsIgnoreCase("true")) qsData.getEINZUGGESPRTEILNEHMER().add(et4);
                    // wenn keiner dabei war, dann wird "0 = Keine der angegebenen" ausgewählt
                    if (qsData.getEINZUGGESPRTEILNEHMER().isEmpty()) qsData.getEINZUGGESPRTEILNEHMER().add(et0);

                    qsData.getEINZUGGESPRDOKU().setValue(Integer.valueOf(content.getProperty("EINZUGGESPRDOKU")));
                }
            }

            //todo: integration dokumentieren und testen.
        } else {
            /** SPEC30-88 */
            qsData.getEINZUGGESPRTEILNEHMER().add(of.createDasQsDataTypeEINZUGGESPRTEILNEHMER()); // leer
        }
    }

    private void marshal(JAXBElement<RootType> root) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(RootType.class);
        Marshaller mar = context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        mar.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "https://www.das-pflege.de ../das_interface.xsd");
        target.toPath().getParent().toFile().mkdirs();
        textListener.addLog(SYSConst.html_h3(SYSTools.xx("qdvs.erzeuge.xml") + ": " + target.toString()));
        mar.marshal(root, target);
    }

    /**
     * ermittelt die zur Zeit gültigen Hauptdiagnosen für den Bewohner. Diese setzen sich zusammen aus allen gültigen
     * ICD10 (bzw. deren Markierungen) und den ResInfoTypes Demenz und Diabetes. Die Menge der Properties sind entweder
     * true oder false. Wobei das über alle Diagnosen per logischem ODER zusammengefasst wird.
     * <p>
     * seit SPEC20 kein ("parkinson","osteo","ms","demenz","diabetes") mehr
     * seit SPEC30 kein ("apallisch") mehr
     *
     * @param resident
     * @return Properties mit den folgenden Schlüsseln ("tumor","tetra","chorea","apallisch"). Die Values sind jeweils
     * true oder false.
     */
    private List<DasQsDataType.DIAGNOSEN> getAktuelleDiagnosenFuerQDVS(Resident resident) {
        // die reihenfolge entspricht den Ordnungsnummern die vom DAS erwartet werden. Tumor ist die 1 usw...
        List<String> diagnosen_schluessel = Arrays.asList("tumor", "tetra", "chorea");
        List<DasQsDataType.DIAGNOSEN> result = new ArrayList<>();
        Properties myprops = new Properties();

        // Defaults setzen
        diagnosen_schluessel.forEach(s -> myprops.put(s, "false"));

        // Alle in eine Liste zusammenfassen
        ArrayList<ResInfo> listInfos = ResInfoTools.getAll(resident, typeDiagnosen.getID(), STICHTAG);

        // Properties auswerten
        listInfos.forEach(info -> {
            Properties myProperties = ResInfoTools.getContent(info);
            // Jede Diagnose Resinfo hat jeden der 3 diagnosen_schluessel gesetzt. Entweder auf TRUE oder FALSE
            // Daher bilden wir hier die Schnittmenge zwischen den Properties aus der Entity und der Liste der diagnosen_schlüssel.
            CollectionUtils.intersection(myProperties.stringPropertyNames(), diagnosen_schluessel).forEach(o -> {
                // die booleans parsen true oder false
                boolean wert_vorher = Boolean.valueOf(myprops.getProperty(o));
                boolean neuer_wert = wert_vorher || Boolean.valueOf(myProperties.getProperty(o)); // logisches OR, wenn irgendeine INFO das auf TRUE setzt bleibt das so.
                myprops.setProperty(o, Boolean.toString(neuer_wert));
            });
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


    private List<String> getAusschlussDiagnosen(Resident resident) {
        // die reihenfolge entspricht den Ordnungsnummern die vom DAS erwartet werden. Tumor ist die 1 usw...
        List<String> diagnosen_schluessel = Arrays.asList("apallisch");
        List<String> result = new ArrayList<>();
        Properties myprops = new Properties();

        // Defaults setzen
        diagnosen_schluessel.forEach(s -> myprops.put(s, "false"));

        // Alle in eine Liste zusammenfassen
        ArrayList<ResInfo> listInfos = ResInfoTools.getAll(resident, typeDiagnosen.getID(), STICHTAG);

        // Properties auswerten
        listInfos.forEach(info -> {
            Properties myProperties = ResInfoTools.getContent(info);
            CollectionUtils.intersection(myProperties.stringPropertyNames(), diagnosen_schluessel).forEach(o -> {
                // die booleans parsen true oder false
                boolean wert_vorher = Boolean.valueOf(myprops.getProperty(o));
                boolean neuer_wert = wert_vorher || Boolean.valueOf(myProperties.getProperty(o)); // logisches OR, wenn irgendeine INFO das auf TRUE setzt bleibt das so.
                myprops.setProperty(o, Boolean.toString(neuer_wert));
            });
        });

        myprops.entrySet().stream().filter(entry -> entry.getValue().toString().equalsIgnoreCase("true")).forEach(entry -> result.add(entry.getKey().toString()));

        return result;
    }

    public static String toString(Resident resident) {
        DateFormat df = DateFormat.getDateInstance();
        String result = ResidentTools.getName(resident) + ", " + ResidentTools.getFirstname(resident) + " (*" + df.format(resident.getDob()) + "), ";


        result += " [" + resident.getId() + "]";
        return result + " (" + NF_IDBEWOHNER.format(resident.getIdbewohner()) + ")";
    }

}
