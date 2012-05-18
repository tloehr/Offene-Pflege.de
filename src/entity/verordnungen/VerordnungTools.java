/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.verordnungen;

import entity.*;
import op.OPDE;
import op.tools.HTMLTools;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author tloehr
 */
public class VerordnungTools {

    /**
     * Diese Methode ermittelt eine Liste aller Verordnungen gemäß der unten genannten Einschränkungen.
     *
     * @param bewohner dessen Verordnungen wir suchen
     * @return Eine Liste aus Objekt Arrays, die folgenden Aufbau hat:
     *         <center><code><b>{Verordnung, Vorrat, Saldo des Vorrats,
     *         Bestand (im Anbruch), Saldo des Bestandes, Bezeichnung des Medikamentes, Bezeichnung der Massnahme}</b></code></center>
     *         Es gibt Verordnungen, die keine Medikamente besitzen, bei denen steht dann <code>null</code> an den entsprechenden
     *         Stellen.
     */
    public static List<Object[]> getVerordnungenUndVorraeteUndBestaende(Bewohner bewohner, boolean archiv) {
        List<Object[]> listResult = new ArrayList<Object[]>();

        EntityManager em = OPDE.createEM();
        Query queryVerordnung = em.createQuery("SELECT v FROM Verordnung v WHERE " + (archiv ? "" : " v.abDatum >= :now AND ") + " v.bewohner = :bewohner ");
        if (!archiv) {
            queryVerordnung.setParameter("now", new Date());
        }
        queryVerordnung.setParameter("bewohner", bewohner);
        List<Verordnung> listeVerordnung = queryVerordnung.getResultList();

        for (Verordnung verordnung : listeVerordnung) {

            MedVorrat vorrat = verordnung.getDarreichung() == null ? null : DarreichungTools.getVorratZurDarreichung(bewohner, verordnung.getDarreichung());
            MedBestand aktiverBestand = MedBestandTools.getBestandImAnbruch(vorrat);
            listResult.add(new Object[]{verordnung, vorrat, aktiverBestand});
        }

        em.close();

        return listResult;
    }


    /**
     * Diese Methode erzeugt einen Stellplan für den aktuellen Tag im HTML Format.
     * Eine Besonderheit bei der Implementierung muss ich hier erläutern.
     * Aufgrund der ungleichen HTML Standards (insbesonders der Druckdarstellung im CSS2.0 und später auch CSS2.1)
     * muss ich hier einen Trick anwenden, damit das auf verschiedenen Browsern halbwegs gleich aussieht.
     * <p/>
     * Daher addiere ich jedes größere Element auf einer Seite (also Header, Tabellen Zeilen) mit dem Wert 1.
     * Nach einer bestimmten Anzahl von Elementen erzwinge ich einen Pagebreak.
     * <p/>
     * Nach einem Pagebreak wird der Name des aktuellen Bewohner nocheinmal wiederholt.
     * <p/>
     * Ein Mac OS Safari druckt mit diesen Werten sehr gut.
     * Beim Firefox sollten die Ränder wie folgt eingestellt werden:
     * <ul>
     * <li>print.print_margin_bottom = 0.3</li>
     * <li>print.print_margin_left = 0.1</li>
     * <li>print.print_margin_right = 0.1</li>
     * <li>print.print_margin_top = 0.3</li>
     * <li>print.print_unwriteable_margin_bottom = 57</li>
     * <li>print.print_unwriteable_margin_left = 25</li>
     * <li>print.print_unwriteable_margin_right = 25</li>
     * <li>print.print_unwriteable_margin_top = 25</li>
     * <li>Drucken des Hintergrundes einschalten</li>
     * <ul>
     *
     * @param einrichtungen Die Einrichtung, für die der Stellplan erstellt werden soll. Sortiert nach den Stationen.
     */
    public static String getStellplanAsHTML(Einrichtungen einrichtungen) {
        EntityManager em = OPDE.createEM();
        String html = "";

        try {
            Query query = em.createNamedQuery("Verordnung.findAllForStellplan");
            query.setParameter(1, einrichtungen.getEKennung());
            html = getStellplan(query.getResultList());

        } catch (Exception e) {
            OPDE.fatal(e);
        }
        return html;
    }


    private static String getStellplan(List data) {

        int STELLPLAN_PAGEBREAK_AFTER_ELEMENT_NO = Integer.parseInt(OPDE.getProps().getProperty("stellplan_pagebreak_after_element_no"));

        int elementNumber = 1;
        boolean pagebreak = false;

        String header = "Stellplan für den " + DateFormat.getDateInstance().format(new Date());

        String html = "<html>"
                + "<head>"
                + "<title>" + header + "</title>"
                + OPDE.getCSS()
                + HTMLTools.JSCRIPT_PRINT
                + "</head>"
                + "<body>";

        String bwkennung = "";
        long statid = 0;

        Iterator it = data.iterator();

        while (it.hasNext()) {

            Object[] objects = (Object[]) it.next();

            Verordnung verordnung = (Verordnung) objects[0];
            Stationen station = (Stationen) objects[1];
            VerordnungPlanung planung = (VerordnungPlanung) objects[2];

            BigInteger bestid = (BigInteger) objects[3];
            //Vorrat wäre objects[4]
            BigInteger formid = (BigInteger) objects[5];

            OPDE.debug(verordnung);


            boolean stationsWechsel = statid != station.getStatID();

            // Wenn der Plan für eine ganze Einrichtung gedruckt wird, dann beginnt eine
            // neue Station immer auf einer neuen Seite.
            if (stationsWechsel) {
                elementNumber = 1;
                // Beim ersten Mal nur ein H1 Header. Sonst mit Seitenwechsel.
                if (statid == 0) {
                    html += "<h1 align=\"center\" id=\"fonth1\">";
                } else {
                    html += "</table>";
                    html += "<h1 align=\"center\" id=\"fonth1\" style=\"page-break-before:always\">";
                }
                html += header + " (" + station.getBezeichnung() + ")" + "</h1>";
                html += "<div align=\"center\" id=\"fontsmall\">Stellpläne <u>nur einen Tag</u> lang benutzen! Danach <u>müssen sie vernichtet</u> werden.</div>";
                statid = station.getStatID();
            }


            // Alle Formen, die nicht abzählbar sind, werden grau hinterlegt. Also Tropfen, Spritzen etc.
            boolean grau = false;
            if (formid != null) {
                MedFormen form = OPDE.createEM().find(MedFormen.class, formid.longValue());
                grau = form.getStellplan() > 0;
            }

            // Wenn der Bewohnername sich in der Liste ändert, muss
            // einmal die Überschrift drüber gesetzt werden.
            boolean bewohnerWechsel = !bwkennung.equalsIgnoreCase(verordnung.getBewohner().getBWKennung());

            if (pagebreak || stationsWechsel || bewohnerWechsel) {
                // Falls zufällig ein weiterer Header (der 2 Elemente hoch ist) einen Pagebreak auslösen WÜRDE
                // müssen wir hier schonmal vorsorglich den Seitenumbruch machen.
                // 2 Zeilen rechne ich nochdrauf, damit die Tabelle mindestens 2 Zeilen hat, bevor der Seitenumbruch kommt.
                // Das kann dann passieren, wenn dieser if Konstrukt aufgrund eines BW Wechsels durchlaufen wird.
                pagebreak = (elementNumber + 2 + 2) > STELLPLAN_PAGEBREAK_AFTER_ELEMENT_NO;

                // Außer beim ersten mal und beim Pagebreak, muss dabei die vorherige Tabelle abgeschlossen werden.
                if (pagebreak || !bwkennung.equals("")) {
                    html += "</table>";
                }

                bwkennung = verordnung.getBewohner().getBWKennung();

                html += "<h2 id=\"fonth2\" " +
                        (pagebreak ? "style=\"page-break-before:always\">" : ">") +
                        ((pagebreak && !bewohnerWechsel) ? "<i>(fortgesetzt)</i> " : "")
                        + BewohnerTools.getBWLabelText(verordnung.getBewohner())
                        + "</h2>";
                html += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\"><tr>"
                        + "<th>Präparat / Massnahme</th><th>FM</th><th>MO</th><th>MI</th><th>NM</th><th>AB</th><th>NA</th><th>Bemerkungen</th></tr>";
                elementNumber += 2;

                if (pagebreak) {
                    elementNumber = 1;
                    pagebreak = false;
                }
            }


            html += "<tr " + (grau ? "id=\"fonttextgrau\">" : ">");
            html += "<td width=\"300\" >" + (verordnung.hasMedi() ? "<b>" + DarreichungTools.toPrettyString(verordnung.getDarreichung()) + "</b>" : verordnung.getMassnahme().getBezeichnung());
            html += (bestid != null ? "<br/><i>Bestand im Anbruch Nr.: " + bestid + "</i>" : "") + "</td>";
            html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(planung.getNachtMo()) + "</td>";
            html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(planung.getMorgens()) + "</td>";
            html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(planung.getMittags()) + "</td>";
            html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(planung.getNachmittags()) + "</td>";
            html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(planung.getAbends()) + "</td>";
            html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(planung.getNachtAb()) + "</td>";
            html += "<td width=\"300\" >" + VerordnungPlanungTools.getHinweis(planung, false) + "</td>";
            html += "</tr>";
            elementNumber += 1;

            pagebreak = elementNumber > STELLPLAN_PAGEBREAK_AFTER_ELEMENT_NO;
        }

        html += "</table>"
                + "</body>";


        return html;
    }

    public static String getMassnahme(Verordnung verordnung) {
        String result = "";

        if (verordnung.isAbgesetzt()) {
            result += "<s>"; // Abgesetzte
        }
        if (!verordnung.hasMedi()) {
            result += verordnung.getMassnahme().getBezeichnung();
        } else {
            // Prüfen, was wirklich im Anbruch gegeben wird. (Wenn das Medikament über die Zeit gegen Generica getauscht wurde.)

            MedVorrat vorrat = DarreichungTools.getVorratZurDarreichung(verordnung.getBewohner(), verordnung.getDarreichung());
            MedBestand aktuellerAnbruch = MedBestandTools.getBestandImAnbruch(vorrat);

            if (aktuellerAnbruch != null) {
                if (!aktuellerAnbruch.getDarreichung().equals(verordnung.getDarreichung())) { // Nur bei Abweichung.
                    result += "<b>" + aktuellerAnbruch.getDarreichung().getMedProdukt().getBezeichnung().replaceAll("-", "- ") +
                            (aktuellerAnbruch.getDarreichung().getZusatz().isEmpty() ? "" : " " + aktuellerAnbruch.getDarreichung().getZusatz()) + "</b>" +
                            (aktuellerAnbruch.getDarreichung().getMedForm().getZubereitung().isEmpty() ? "" : " " + aktuellerAnbruch.getDarreichung().getMedForm().getZubereitung()) + " " +
                            (aktuellerAnbruch.getDarreichung().getMedForm().getAnwText().isEmpty() ? SYSConst.EINHEIT[aktuellerAnbruch.getDarreichung().getMedForm().getAnwEinheit()] : aktuellerAnbruch.getDarreichung().getMedForm().getAnwText());
                    result += " <i>(ursprünglich verordnet: " + verordnung.getDarreichung().getMedProdukt().getBezeichnung().replaceAll("-", "- ");
                    result += (aktuellerAnbruch.getDarreichung().getZusatz().isEmpty() ? "" : " " + aktuellerAnbruch.getDarreichung().getZusatz()) + ")</i>";
                } else {
                    result += "<b>" + verordnung.getDarreichung().getMedProdukt().getBezeichnung().replaceAll("-", "- ")
                            + (aktuellerAnbruch.getDarreichung().getZusatz().isEmpty() ? "" : " " + aktuellerAnbruch.getDarreichung().getZusatz()) + "</b>" +
                            (aktuellerAnbruch.getDarreichung().getMedForm().getZubereitung().isEmpty() ? "" : " " + aktuellerAnbruch.getDarreichung().getMedForm().getZubereitung()) + " " +
                            (verordnung.getDarreichung().getMedForm().getAnwText().isEmpty() ? SYSConst.EINHEIT[verordnung.getDarreichung().getMedForm().getAnwEinheit()] : verordnung.getDarreichung().getMedForm().getAnwText());
                }
            } else {
                result += "<b>" + verordnung.getDarreichung().getMedProdukt().getBezeichnung().replaceAll("-", "- ")
                        + (verordnung.getDarreichung().getZusatz().isEmpty() ? "" : " " + verordnung.getDarreichung().getZusatz()) + "</b>" +
                        (verordnung.getDarreichung().getMedForm().getZubereitung().isEmpty() ? "" : " " + verordnung.getDarreichung().getMedForm().getZubereitung()) + " " +
                        (verordnung.getDarreichung().getMedForm().getAnwText().isEmpty() ? SYSConst.EINHEIT[verordnung.getDarreichung().getMedForm().getAnwEinheit()] : verordnung.getDarreichung().getMedForm().getAnwText());
            }


        }
        if (verordnung.isAbgesetzt()) {
            result += "</s>"; // Abgesetzte
        }

        return result;
    }

    public static String getHinweis(Verordnung verordnung) {
        String result = "";

        if (verordnung.isBedarf()) {
            result += "<b><u>Nur bei Bedarf:</u> <font color=\"blue\">" + verordnung.getSituation().getText() + "</font></b>";
        }
        if (!verordnung.getBemerkung().isEmpty()) {
            result += result.isEmpty() ? "" : "<br/>";
            result += "<b><u>Bemerkung:</u> </b>" + verordnung.getBemerkung();
        }
        return result;
    }

    public static String getAN(Verordnung verordnung) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
        String datum = sdf.format(verordnung.getAnDatum());


        result += "<font color=\"green\">" + datum + "; ";
        if (verordnung.getAnKH() != null) {
            result += verordnung.getAnKH().getName();
        }
        if (verordnung.getAnArzt() != null) {
            if (verordnung.getAnKH() != null) {
                result += " <i>bestätigt durch:</i> ";
            }
            result += verordnung.getAnArzt().getAnrede() + " " + SYSTools.anonymizeName(verordnung.getAnArzt().getName(), SYSTools.INDEX_NACHNAME);
        }
        result += "; " + verordnung.getAngesetztDurch().getNameUndVorname() + "</font>";


        return result;
    }

    public static String getAB(Verordnung verordnung) {
        String result = "";

        if (verordnung.isAbgesetzt()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
            String datum = sdf.format(verordnung.getAbDatum());


            result += "<font color=\"red\">" + datum + "; ";

            result += verordnung.getAbKH() != null ? verordnung.getAbKH().getName() : "";

            if (verordnung.getAbArzt() != null) {
                if (verordnung.getAbKH() != null) {
                    result += " <i>bestätigt durch:</i> ";
                }
                result += verordnung.getAbArzt().getAnrede() + " " + SYSTools.anonymizeName(verordnung.getAbArzt().getName(), SYSTools.INDEX_NACHNAME);
            }
            result += "; " + verordnung.getAbgesetztDurch().getNameUndVorname() + "</font>";

        }
        return result;
    }

    public static String getDosis(Verordnung verordnung) {
        return getDosis(verordnung, false, null, null);
    }

    public static String getDosis(Verordnung verordnung, boolean mitBestandsAnzeige, MedVorrat vorrat, MedBestand bestandImAnbruch) {
        long timestart = System.currentTimeMillis();
        String result = "";
        if (verordnung.getPlanungen().size() > 1) {
            Collections.sort(verordnung.getPlanungen());
        }
        Iterator<VerordnungPlanung> planungen = verordnung.getPlanungen().iterator();

        if (planungen.hasNext()) {
            VerordnungPlanung vorherigePlanung = null;
            VerordnungPlanung planung = null;
            while (planungen.hasNext()) {
                planung = planungen.next();
                result += VerordnungPlanungTools.getDosisAsHTML(planung, vorherigePlanung, false);
                vorherigePlanung = planung;
            }
            if (VerordnungPlanungTools.getTerminStatus(planung) != VerordnungPlanungTools.MAXDOSIS) {
                // Wenn die letzte Planung eine Tabelle benötigte (das tut sie dann, wenn
                // es keine Bedarfsverordnung war), dann müssen wir die Tabelle hier noch
                // schließen.
                result += "</table>";
            }
        } else {
            result += "<i>Noch keine Dosierung / Anwendungsinformationen verfügbar</i><br/>";
        }

        if (mitBestandsAnzeige && verordnung.hasMedi()) {
            if (verordnung.isBisPackEnde()) {
                result += "nur bis Packungs Ende<br/>";
            }
            if (!verordnung.isAbgesetzt()) {
                if (bestandImAnbruch != null) {
                    EntityManager em = OPDE.createEM();

                    BigDecimal vorratSumme = null;
                    BigDecimal bestandSumme = null;
                    try {
                        vorratSumme = MedVorratTools.getVorratSumme(em, bestandImAnbruch.getVorrat());
                        bestandSumme = MedBestandTools.getBestandSumme(em, bestandImAnbruch);
                    } catch (Exception e) {
                        OPDE.fatal(e);
                    } finally {
                        em.close();
                    }


                    if (vorratSumme != null && vorratSumme.compareTo(BigDecimal.ZERO) > 0) {
                        result += "<b><u>Vorrat:</u> <font color=\"green\">" + SYSTools.roundScale2(vorratSumme) + " " +
                                SYSConst.EINHEIT[bestandImAnbruch.getDarreichung().getMedForm().getPackEinheit()] +
                                "</font></b>";
                        if (!bestandImAnbruch.getDarreichung().getMedForm().anwUndPackEinheitenGleich()) {

                            BigDecimal anwmenge = vorratSumme.multiply(bestandImAnbruch.getApv());


                            //double anwmenge = SYSTools.roundScale2(rs.getDouble("saldo") * rs.getDouble("APV"));
                            result += " <i>entspricht " + SYSTools.roundScale2(anwmenge) + " " +//SYSConst.EINHEIT[rs.getInt("f.AnwEinheit")]+"</i>";
                                    MedFormenTools.getAnwText(bestandImAnbruch.getDarreichung().getMedForm());
                            result += " (bei einem APV von " + SYSTools.roundScale2(bestandImAnbruch.getApv()) + " zu 1)";
                            result += "</i>";
                        }

                        result += "<br/>Bestand im Anbruch Nr.: <b><font color=\"green\">" + bestandImAnbruch.getBestID() + "</font></b>";

                        if (vorratSumme.compareTo(bestandSumme) != 0) {
                            result += "<br/>Restmenge im Anbruch: <b><font color=\"green\">" + bestandSumme.setScale(2, BigDecimal.ROUND_UP) + " " +
                                    SYSConst.EINHEIT[bestandImAnbruch.getDarreichung().getMedForm().getPackEinheit()] + "</font></b>";
                            if (!bestandImAnbruch.getDarreichung().getMedForm().anwUndPackEinheitenGleich()) {
                                //double anwmenge = SYSTools.roundScale2(rs.getDouble("bestsumme") * rs.getDouble("APV"));
                                BigDecimal anwmenge = bestandSumme.multiply(bestandImAnbruch.getApv());

                                result += " <i>entspricht " + anwmenge.setScale(2, BigDecimal.ROUND_UP) + " " +//SYSConst.EINHEIT[rs.getInt("f.AnwEinheit")]+"</i>";
                                        MedFormenTools.getAnwText(bestandImAnbruch.getDarreichung().getMedForm()) + "</i>";
                            }
                        }

                    } else {
                        result += "<b><font color=\"red\">Der Vorrat an diesem Medikament ist <u>leer</u>.</font></b>";
                    }
                } else {
                    if (vorrat == null) {
                        result += "<b><font color=\"red\">Es gibt bisher keinen Vorrat für dieses Medikament.</font></b>";
                    } else {
                        if (MedVorratTools.getNaechsteNochUngeoeffnete(vorrat) != null) {
                            result += "<br/><b><font color=\"red\">Kein Bestand im Anbruch. Vergabe nicht möglich.</font></b>";
                        } else {
                            result += "<br/><b><font color=\"red\">Keine Bestände mehr im Vorrat vorhanden. Vergabe nicht möglich.</font></b>";
                        }

                    }
                }
            }

        }


        long timeend = System.currentTimeMillis();

        OPDE.debug("time end: " + (timeend - timestart) + " millis");

        return result;
    }

    /**
     * Dieser Query ordnet Verordnungen den Vorräten zu. Dazu ist ein kleiner Trick nötig. Denn über die Zeit können verschiedene Vorräte mit verschiedenen
     * Darreichungen für dieselbe Verordnung verwendet werden. Der Trick ist der Join über zwei Spalten in der Zeile mit "MPBestand"
     */
    public static List<Verordnung> getVerordnungenByVorrat(EntityManager em, MedVorrat vorrat) throws Exception {
        List<BigInteger> list = null;
        List<Verordnung> result = null;
        Query query = em.createNativeQuery(" SELECT DISTINCT ver.VerID FROM BHPVerordnung ver " +
                " INNER JOIN MPVorrat v ON v.BWKennung = ver.BWKennung " + // Verbindung über Bewohner
                " INNER JOIN MPBestand b ON ver.DafID = b.DafID AND v.VorID = b.VorID " + // Verbindung über Bestand zur Darreichung UND dem Vorrat
                " WHERE b.VorID=? AND ver.AbDatum > now() ");
        query.setParameter(1, vorrat.getVorID());
//        query.setParameter(2, bisPackEnde);
        list = query.getResultList();

        if (!list.isEmpty()) {
            result = new ArrayList<Verordnung>(list.size());
            for (BigInteger verid : list) {
                result.add(em.find(Verordnung.class, verid.longValue()));
            }
        }
        return result;
    }


//    public static boolean absetzen(Verordnung verordnung, Arzt arzt, Krankenhaus krankenhaus) {
//        EntityManager em = OPDE.createEM();
//        boolean result = false;
//        try {
//            em.getTransaction().begin();
//            verordnung = absetzen(em, verordnung, arzt, krankenhaus);
//            em.getTransaction().commit();
//            result = true;
//        } catch (Exception e) {
//            em.getTransaction().rollback();
//        } finally {
//            em.close();
//        }
//        return result;
//    }

//    /**
//     * Setzt eine Verordnung ab. Die zugehörigen BHPs werden ab JETZT entfernt.
//     *
//     * @param verordnung  welche Verordnung soll abgesetzt werden.
//     * @param arzt        welcher Arzt hat sie abgesetzt.
//     * @param krankenhaus welches KH hat sie abgesetzt
//     * @return erfolg
//     */
//    public static Verordnung absetzen(EntityManager em, Verordnung verordnung) throws Exception {
////        if (arzt == null && krankenhaus == null) {
////            throw new NullPointerException("Arzt und Krankenhaus dürfen nicht beide NULL sein.");
////        }
//        verordnung = em.merge(verordnung);
//        em.lock(verordnung, LockModeType.OPTIMISTIC);
//
//        verordnung.setAbDatum(new Date());
////        verordnung.setAbArzt(em.merge(arzt));
////        verordnung.setAbKH(em.merge(krankenhaus));
//        verordnung.setAbgesetztDurch(OPDE.getLogin().getUser());
//
//        BHPTools.aufräumen(em, verordnung);
//
//        return verordnung;
//    }

    /**
     * Gibt eine HTML Darstellung der Verordungen zurück, die in dem übergebenen TableModel enthalten sind.
     */
    public static String getVerordnungenAsHTML(List<Verordnung> list) {
        String result = "";

        if (!list.isEmpty()) {

            Verordnung verordnung = list.get(0);
//                if (SYSTools.catchNull(bwkennung).equals("")) {
//                    result += "<h2>Ärztliche Verordnungen</h2>";
//                } else {
            result += "<h1  id=\"fonth1\" >Ärztliche Verordnungen für " + BewohnerTools.getBWLabelText(verordnung.getBewohner()) + "</h1>";
            if (verordnung.getBewohner().getStation() != null) {
                result += EinrichtungenTools.getAsText(verordnung.getBewohner().getStation().getEinrichtung());
            }

            result += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\"><tr>" +
                    "<th >Medikament/Massnahme</th><th >Dosierung / Hinweise</th><th >Angesetzt</th></tr>";

            Iterator<Verordnung> itVerordnung = list.iterator();
            while (itVerordnung.hasNext()) {
                verordnung = itVerordnung.next();

                result += "<tr>";
                result += "<td valign=\"top\">" + getMassnahme(verordnung) + "</td>";
                result += "<td valign=\"top\">" + getDosis(verordnung) + "<br/>";
                result += getHinweis(verordnung) + "</td>";
                result += "<td valign=\"top\">" + getAN(verordnung) + "</td>";
                //result += "<td>" + SYSTools.unHTML2(tmv.getValueAt(v, TMVerordnung.COL_AB).toString()) + "</td>";
                result += "</tr>";
            }

            result += "</table>";
        } else {
            result += "<h2  id=\"fonth2\" >Ärztliche Verordnungen</h2><i>zur Zeit gibt es keine Verordnungen</i>";
        }
        return result;
    }

//    /**
//     * Löscht eine Verordnung und die zugehörigen BHPs und deren Planungen.
//     */
//    public static void loeschen(Verordnung verordnung) {
//        OPDE.getDisplayManager().setDBActionMessage(true);
//        EntityManager em = OPDE.createEM();
//        try {
//            em.getTransaction().begin();
////            verordnung = em.merge(verordnung);
////            Query queryBHP = em.createQuery(" " +
////                    " DELETE FROM BHP bhp " +
////                    " WHERE bhp.verordnungPlanung.verordnung = :verordnung ");
////            queryBHP.setParameter("verordnung", verordnung);
////            queryBHP.executeUpdate();
////
////            Query queryPlanung = em.createQuery(" " +
////                    " DELETE FROM VerordnungPlanung vp" +
////                    " WHERE vp.verordnung = :verordnung ");
////            queryPlanung.setParameter("verordnung", verordnung);
////            queryPlanung.executeUpdate();
//
//            em.remove(em.merge(verordnung));
//            em.getTransaction().commit();
//
//        } catch (Exception ex) {
//            OPDE.debug(ex.getMessage());
//            em.getTransaction().rollback();
//            OPDE.fatal(ex);
//        } finally {
//            em.close();
//            OPDE.getDisplayManager().setDBActionMessage(false);
//        }
//    }

    /**
     * Ermittelt die Anzahl der Verordnungen, die zu dieser Verordnung gemäß der VerordnungKennung gehören.
     * Verordnung, die über die Zeit mehrfach geändert werden, hängen über die VerordnungsKennung aneinander.
     *
     * @param verordnung
     * @return Anzahl der Verordnungen, die zu dieser gehören.e
     */
    public static int getNumVerodnungenMitGleicherKennung(Verordnung verordnung) {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("Verordnung.findByVerKennung");
        query.setParameter("verKennung", verordnung.getVerKennung());
        int num = query.getResultList().size();
        em.close();
        return num;
    }

    public static String toPrettyString(Verordnung verordnung) {
        String myPretty = "";

        if (verordnung.hasMedi()) {
            myPretty = DarreichungTools.toPrettyString(verordnung.getDarreichung());
        } else {
            myPretty = verordnung.getMassnahme().getBezeichnung();
        }

        myPretty += verordnung.isBedarf() ? " (Nur bei Bedarf: " + verordnung.getSituation().getText() + ")" : "";

        return myPretty;
    }

}
