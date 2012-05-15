package entity.verordnungen;

import entity.BewohnerTools;
import entity.Stationen;
import op.OPDE;
import op.tools.DlgException;
import op.tools.SYSConst;
import op.tools.SYSPrint;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.*;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 18.11.11
 * Time: 14:28
 * To change this template use File | Settings | File Templates.
 */
public class MedBestandTools {
//    public static boolean apvNeuberechnung = true;

    public static ListCellRenderer getBestandOnlyIDRenderer() {
        return new ListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                String text;
                if (o == null) {
                    text = "<i>Keine Auswahl</i>";//SYSTools.toHTML("<i>Keine Auswahl</i>");
                } else if (o instanceof MedBestand) {
                    text = ((MedBestand) o).getBestID().toString();
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
            }
        };
    }

//    public static MedBestand findByVerordnungImAnbruch(Verordnung verordnung) {
//        EntityManager em = OPDE.createEM();
//        Query query = em.createNamedQuery("MedBestand.findByDarreichungAndBewohnerImAnbruch");
//        query.setParameter("bewohner", verordnung.getBewohner());
//        query.setParameter("darreichung", verordnung.getDarreichung());
//
//        MedBestand result = null;
//
//        try {
//            result = (MedBestand) query.getSingleResult();
//        } catch (NoResultException nre) {
//            result = null;
//        } catch (Exception e) {
//            OPDE.fatal(e);
//            System.exit(1);
//        } finally {
//            em.close();
//        }
//
//        return result;
//    }


    /**
     * Sucht aus den den Beständen des Vorrats den angebrochenen heraus.
     *
     * @param vorrat
     * @return der angebrochene Bestand. null, wenn es keinen gab.
     */
    public static MedBestand getBestandImAnbruch(MedVorrat vorrat) {
        MedBestand bestand = null;
        if (vorrat != null && vorrat.getBestaende() != null) {
            Iterator<MedBestand> itBestand = vorrat.getBestaende().iterator();
            while (itBestand.hasNext()) {
                bestand = itBestand.next();
                if (bestand.isAngebrochen() && !bestand.isAbgeschlossen()) {
                    break;
                }
            }
        }
        return bestand;
    }


    /**
     * Bricht einen Bestand an. Berechnet das neue APV selbstständig.
     *
     * @param bestand
     * @return der geänderte Bestand. Direkt persistiert und committed.
     */
    public static MedBestand anbrechen(MedBestand bestand) {
        BigDecimal apv;
        MedBestand result = null;

        apv = getPassendesAPV(bestand);

        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            result = anbrechen(em, bestand, apv);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            OPDE.fatal(e);
        } finally {
            em.close();
        }
        return result;
    }

    /**
     * Bricht einen Bestand an.
     *
     * @param bestand
     * @return
     */
    public static MedBestand anbrechen(EntityManager em, MedBestand bestand, BigDecimal apv) throws Exception {
        if (apv == null) {
            throw new NullPointerException("apv darf nicht null sein");
        }

        bestand.setAnbruch(new Date());
        if (apv.equals(BigDecimal.ZERO)) {
            // Das hier verhindert Division by Zero Exceptions.
            apv = BigDecimal.ONE;
        }
        bestand.setApv(apv);
        return em.merge(bestand);
    }

    public static HashMap getBestand4Printing(MedBestand bestand) {
        OPDE.debug("BestandID: " + bestand.getBestID());

        HashMap hm = new HashMap();
        hm.put("bestand.darreichung", DarreichungTools.toPrettyString(bestand.getDarreichung()));

        String pzn = bestand.getPackung().getPzn() == null ? "??" : bestand.getPackung().getPzn();
        hm.put("bestand.packung.pzn", pzn);
        hm.put("bestand.bestid", bestand.getBestID());
        hm.put("bestand.eingang", bestand.getEin());
        hm.put("bestand.userkurz", bestand.getUser().getUKennung());
        hm.put("bestand.userlang", bestand.getUser().getNameUndVorname());
        hm.put("bestand.vorrat.bewohnername", BewohnerTools.getBWLabel1(bestand.getVorrat().getBewohner()));
        hm.put("bestand.vorrat.bewohnergebdatum", bestand.getVorrat().getBewohner().getGebDatum());
        hm.put("bestand.vorrat.bewohnerkennung", bestand.getVorrat().getBewohner().getBWKennung());

        return hm;
    }


    public static String getBestandText4Print(MedBestand bestand) {
        String result = "";

        result = SYSPrint.EPL2_CLEAR_IMAGE_BUFFER;
        result += SYSPrint.EPL2_labelformat(57, 19, 3);
        result += SYSPrint.EPL2_print_ascii(5, 5, 0, SYSPrint.EPL2_FONT_7pt, 1, 1, false, DarreichungTools.toPrettyString(bestand.getDarreichung())); // bestand.getDarreichung().getMedProdukt().getBezeichnung() + " " + bestand.getDarreichung().getZusatz())
        if (!SYSTools.catchNull(bestand.getPackung().getPzn()).equals("")) {
            result += SYSPrint.EPL2_print_ascii(5, 30, 0, SYSPrint.EPL2_FONT_6pt, 1, 1, false, "PZN:" + bestand.getPackung().getPzn() + "  Datum:" + DateFormat.getDateInstance().format(bestand.getEin()) + " (" + bestand.getUser().getUKennung() + ")");
        }

        result += SYSPrint.EPL2_print_ascii(5, 55, 0, SYSPrint.EPL2_FONT_12pt, 2, 2, true, Long.toString(bestand.getBestID()));
        result += SYSPrint.EPL2_print_ascii(5, 107, 0, SYSPrint.EPL2_FONT_6pt, 1, 1, false, BewohnerTools.getBWLabel1(bestand.getVorrat().getBewohner()));
        result += SYSPrint.EPL2_print_ascii(5, 122, 0, SYSPrint.EPL2_FONT_6pt, 1, 1, false, BewohnerTools.getBWLabel2(bestand.getVorrat().getBewohner()));

        result += SYSPrint.EPL2_PRINT;


        // Konvertierung auf PC850 weg. Umlauten.
        try {
            byte[] conv = result.getBytes("Cp850");
            result = new String(conv);
        } catch (UnsupportedEncodingException ex) {
            //ex.printStackTrace();
            new DlgException(ex);
        }
        return result;
    }


    public static BigDecimal getBestandSumme(MedBestand bestand) {
        BigDecimal result = BigDecimal.ZERO;

        for (MedBuchungen buchung : bestand.getBuchungen()) {
            result = result.add(buchung.getMenge());
        }
        return result;
    }

    /**
     * Ermittelt die Menge, die in einer Packung noch enthalten ist.
     *
     * @param bestand die entsprechende Packung
     * @return die Summe in der Packungs Einheit.
     */
    public static BigDecimal getBestandSumme(EntityManager em, MedBestand bestand) throws Exception {
        BigDecimal result = BigDecimal.ZERO;

        Query query = em.createQuery(" " +
                " SELECT SUM(bu.menge) " +
                " FROM MedBestand b " +
                " JOIN b.buchungen bu " +
                " WHERE b = :bestand ");

        try {
            query.setParameter("bestand", bestand);
            result = (BigDecimal) query.getSingleResult();
        } catch (NoResultException nre) {
            result = BigDecimal.ZERO;
        }

        return result;
    }

    /**
     * Schliesst einen Bestand ab. Erzeugt dazu direkt eine passende Abschlussbuchung, die den Bestand auf null bringt.
     *
     * @param em,      EntityManager in dessen Transaktion das ganze abläuft.
     * @param bestand, der abzuschliessen ist.
     * @param text,    evtl. gewünschter Text für die Abschlussbuchung
     * @param status,  für die Abschlussbuchung
     * @return Falls die Neuberechung gewünscht war, steht hier das geänderte, bzw. neu erstelle APV Objekt. null andernfalls.
     * @throws Exception
     */
    public static MedBestand abschliessen(EntityManager em, MedBestand bestand, String text, short status) throws Exception {
        BigDecimal bestandsumme = getBestandSumme(em, bestand);

        bestand = em.merge(bestand);
        em.lock(bestand, LockModeType.OPTIMISTIC);

        MedBuchungen abschlussBuchung = em.merge(new MedBuchungen(bestand, bestandsumme.negate(), status));
        abschlussBuchung.setText(text);

        bestand.setAus(new Date());
        bestand.setNaechsterBestand(null);


//        if (mitNeuberechnung) { // Wenn gewünscht wird bei Abschluss der Packung der APV neu berechnet.
//            apvNeu = berechneBuchungsWert(bestand);
//
//            OPDE.info("Neuberechnung von DafID:" + bestand.getDarreichung().getDafID() + ", " + bestand.getDarreichung().getMedProdukt().getBezeichnung());
//        }
        return bestand;
    }

    private static MedBuchungen getAnfangsBuchung(MedBestand bestand) {
        MedBuchungen result = null;
        for (MedBuchungen buchung : bestand.getBuchungen()) {
            if (buchung.getStatus() == MedBuchungenTools.STATUS_EINBUCHEN_ANFANGSBESTAND) {
                result = buchung;
                break;
            }
        }
        return result;
    }

    /**
     * @param bestand, für den das Verhältnis neu berechnet werden soll.
     */
    public static BigDecimal berechneAPV(MedBestand bestand) {

        BigDecimal apvNeu = BigDecimal.ONE;

        if (bestand.getDarreichung().getMedForm().getStatus() != MedFormenTools.APV1) {

            EntityManager em = OPDE.createEM();
            try {
//                Query queryAnfangsBuchung = em.createQuery("SELECT m FROM MedBuchungen m WHERE m.bestand = :bestand AND m.status = :status");
//                queryAnfangsBuchung.setParameter("bestand", bestand);
//                queryAnfangsBuchung.setParameter("status", MedBuchungenTools.STATUS_EINBUCHEN_ANFANGSBESTAND);
//                MedBuchungen anfangsBuchung = ;

                // Menge in der Packung (in der Packungseinheit). Also das, was wirklich in der Packung am Anfang
                // drin war. Meist das, was auf der Packung steht.
                BigDecimal inhaltZuBeginn = getAnfangsBuchung(bestand).getMenge();

                // Das APV, das bei diesem Bestand angenommen wurde.
                BigDecimal apvAlt = bestand.getApv();

                // Zur Verhinderung von Division durch 0
                if (apvAlt.equals(BigDecimal.ZERO)) {
                    apvAlt = BigDecimal.ONE;
                }

                // Anzahl der per BHP verabreichten Einzeldosen. (in der Anwendungseinheit)
                Query querySummeBHPDosis = em.createQuery(" " +
                        " SELECT SUM(bhp.dosis) " +
                        " FROM MedBuchungen bu " +
                        " JOIN bu.bhp bhp" +
                        " WHERE bu.bestand = :bestand ");

                querySummeBHPDosis.setParameter("bestand", bestand);
                BigDecimal summeBHPDosis = (BigDecimal) querySummeBHPDosis.getSingleResult();

                // Die Gaben aus der BHP sind immer in der Anwendungseinheit. Teilt man diese durch das
                // verwendete APV, erhält man das was rechnerisch in der Packung drin gewesen
                // sein soll. Das kann natürlich von dem realen Inhalt abweichen. Klebt noch was an
                // der Flaschenwand oder wurde was verworfen. Das APV steht ja für Anzahl der Anwendung im
                // Verhaltnis zur Packungseinheit 1. Wurden 100 Tropfen gegeben, bei einem APV von 20(:1)
                // Dann ergibt das einen rechnerischen Flascheninhalt von 5 ml.
                BigDecimal inhaltRechnerisch = summeBHPDosis.divide(apvAlt, 4, BigDecimal.ROUND_UP);
                // Zur Verhinderung von Division durch 0
                if (inhaltRechnerisch.equals(BigDecimal.ZERO)) {
                    inhaltRechnerisch = inhaltZuBeginn;
                }

                // Nimmt man den realen Inhalt und teil ihn durch den rechnerischen, dann gibt es drei Möglichkeiten
                // 1. Es wurde mehr gegeben als in der Packung drin war. Dann muss das ursprüngliche APV zu gross gewesen
                // sein. Die Division von realem Inhalt durch rechnerischem Inhalt ist kleiner 1 und somit wird auch
                // das apvNeu kleiner als das apvAlt.
                // 2. Es wurde genau so viel gegeben wie drin war. Dann war das apvAlt genau richtig. Der Divisor ist
                // dann 1 und apvNeu ist gleich apvAlt.
                // 3. Es wurde weniger gegeben als drin war. Dann war apvAlt zu klein und der Divisor (real durch rechnerisch) wird größer 0 und
                // der apvNeu wird größer als der apvAlt.
                apvNeu = inhaltZuBeginn.divide(inhaltRechnerisch, 4, BigDecimal.ROUND_UP).multiply(apvAlt);


                // Zu große APV Abweichungen verhindern. Alle außerhalb eines 20% Korridors wird ignoriert.
                BigDecimal apvkorridor = new BigDecimal(Double.parseDouble(OPDE.getProps().getProperty("apv_korridor"))).divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_UP);
                BigDecimal halbeBreite = apvAlt.multiply(apvkorridor);
                BigDecimal korridorUnten = apvAlt.subtract(halbeBreite);
                BigDecimal korridorOben = apvAlt.add(halbeBreite);
                // Liegt der neue apv AUSSERHALB des maximalen Korridors, so wird er verworfen
                if (apvAlt.compareTo(korridorUnten) < 0 || korridorOben.compareTo(apvNeu) < 0) {
                    apvNeu = apvAlt;
                }
            } catch (Exception e) {
                OPDE.fatal(e);
            } finally {
                em.close();
            }
        }
        return apvNeu;
    }

    /**
     * Diese Methode bucht auf einen Bestand immer genau soviel drauf oder runter, damit er auf
     * dem gewünschten soll landet.
     *
     * @param bestand um die es geht.
     * @param soll    gewünschter Endbestand
     * @param em      EntityManager in dessen Kontext das ganze abläuft
     * @param text    für die Buchung
     * @param status  für die Buchung
     * @return die Korrektur Buchung
     * @throws Exception
     */
    public static MedBuchungen setzeBestandAuf(EntityManager em, MedBestand bestand, BigDecimal soll, String text, short status) throws Exception {
        MedBuchungen result = null;
        bestand = em.merge(bestand);

        BigDecimal bestandSumme = getBestandSumme(em, bestand);

        if (!bestandSumme.equals(soll)) {
            BigDecimal korrektur;
            if (bestandSumme.compareTo(BigDecimal.ZERO) <= 0) {
                korrektur = bestandSumme.abs().add(soll);
            } else {
                korrektur = bestandSumme.negate().add(soll);
            }

            // passende Buchung anlegen.
            result = em.merge(new MedBuchungen(bestand, korrektur, status));
            result.setText(text);
        }
        return result;
    }

    public static String getBestandTextAsHTML(MedBestand bestand) {
        String result = "";
        result += "<font color=\"blue\"><b>" + bestand.getDarreichung().getMedProdukt().getBezeichnung() + " " + bestand.getDarreichung().getZusatz() + ", ";

        if (!SYSTools.catchNull(bestand.getPackung().getPzn()).equals("")) {
            result += "PZN: " + bestand.getPackung().getPzn() + ", ";
            result += MedPackungTools.GROESSE[bestand.getPackung().getGroesse()] + ", " + bestand.getPackung().getInhalt() + " " + MedFormenTools.EINHEIT[bestand.getDarreichung().getMedForm().getPackEinheit()] + " ";
            String zubereitung = SYSTools.catchNull(bestand.getDarreichung().getMedForm().getZubereitung());
            String anwtext = SYSTools.catchNull(bestand.getDarreichung().getMedForm().getAnwText());
            result += zubereitung.equals("") ? anwtext : (anwtext.equals("") ? zubereitung : zubereitung + ", " + anwtext);
            result += "</b></font>";
        }

        return result;
    }

    public static String getBestandAsHTML(MedBestand bestand) {
        String result = "";

        String htmlcolor = bestand.isAbgeschlossen() ? "gray" : "red";

        result += "<font color=\"" + htmlcolor + "\"><b><u>" + bestand.getBestID() + "</u></b></font>&nbsp; ";
        result += DarreichungTools.toPrettyString(bestand.getDarreichung());

        if (!SYSTools.catchNull(bestand.getPackung().getPzn()).isEmpty()) {
            result += ", " + MedPackungTools.toPrettyString(bestand.getPackung());
        }

        result += ", APV: " + bestand.getApv().setScale(2, BigDecimal.ROUND_HALF_UP);

        if (bestand.hasNextBestand()) {
            result += ", <b>nächster Bestand: " + bestand.getNaechsterBestand().getBestID() + "</b>";
        }

        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

        result += "<br/><font color=\"blue\">Eingang: " + df.format(bestand.getEin()) + "</font>";
        if (bestand.isAngebrochen()) {
            result += "<br/><font color=\"green\">Anbruch: " + df.format(bestand.getAnbruch()) + "</font>";
            if (bestand.isAbgeschlossen()) {
                result += "<br/><font color=\"black\">Ausgebucht: " + df.format(bestand.getAus()) + "</font>";
            }
        }

        return result;

    }

    /**
     * Setzt für einen Bestand <b>alle</b> Buchungen zurück, bis auf die Anfangsbuchung.
     *
     * @param bestand
     */
    public static void zuruecksetzen(MedBestand bestand) {
        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            Query query = em.createQuery("DELETE FROM MedBuchungen b WHERE b.bestand = :bestand AND b.status <> :notStatus ");
            query.setParameter("bestand", bestand);
            query.setParameter("notStatus", MedBuchungenTools.STATUS_EINBUCHEN_ANFANGSBESTAND);
            query.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            OPDE.fatal(ex);
        } finally {
            em.close();
        }
    }

//    public static boolean hasAbgesetzteBestaende(BHP bhp) {
//        boolean result = false;
//
//        EntityManager em = OPDE.createEM();
//
//        try {
//            Query query = em.createQuery(" " +
//                    " SELECT b FROM MedBestand b " +
//                    " JOIN b.buchungen bu " +
//                    " WHERE bu.bhp = :bhp " +
//                    " AND b.aus < '9999-12-31 23:59:59'");
//            query.setParameter("bhp", bhp);
//            result = !query.getResultList().isEmpty();
//        } catch (Exception ex) {
//            OPDE.fatal(ex);
//        } finally {
//            em.close();
//        }
//        return result;
//    }

    /**
     * Ermittelt für einen bestimmten Bestand ein passendes APV.
     */
    public static BigDecimal getPassendesAPV(MedBestand bestand) {

        BigDecimal apv = BigDecimal.ONE;

        if (bestand.getDarreichung().getMedForm().getStatus() == MedFormenTools.APV_PER_BW) {
            apv = getAPVperBW(bestand.getVorrat());
        } else if (bestand.getDarreichung().getMedForm().getStatus() == MedFormenTools.APV_PER_DAF) {
            apv = getAPVperDAF(bestand.getDarreichung());
        }

        return apv;
    }

    public static BigDecimal getAPVperBW(MedVorrat vorrat) {
        BigDecimal apv = null;

        if (!vorrat.getBestaende().isEmpty()) {
            apv = BigDecimal.ZERO;
            for (MedBestand bestand : vorrat.getBestaende()) {
                apv.add(bestand.getApv());
            }
            // Arithmetisches Mittel
            apv = apv.divide(new BigDecimal(vorrat.getBestaende().size()), BigDecimal.ROUND_UP);
        } else {
            // Im Zweifel ist das 1
            apv = BigDecimal.ONE;
        }

//
//        EntityManager em = OPDE.createEM();
//        BigDecimal result = null;
//
//        try {
//            Query query = em.createQuery("SELECT AVG(b.apv) FROM MedBestand b WHERE b.vorrat = :vorrat");
//            query.setParameter("vorrat", vorrat);
//            result = (BigDecimal) query.getSingleResult();
//        } catch (NoResultException nre) {
//            result = BigDecimal.ONE; // Im Zweifel ist das 1
//        } catch (Exception e) {
//            OPDE.fatal(e);
//        } finally {
//            em.close();
//        }
        return apv;
    }

    public static BigDecimal getAPVperDAF(Darreichung darreichung) {

        BigDecimal apv = null;

        if (!darreichung.getBestaende().isEmpty()) {
            apv = BigDecimal.ZERO;
            for (MedBestand bestand : darreichung.getBestaende()) {
                apv.add(bestand.getApv());
            }
            // Arithmetisches Mittel
            apv = apv.divide(new BigDecimal(darreichung.getBestaende().size()), BigDecimal.ROUND_UP);
        } else {
            // Im Zweifel ist das 1
            apv = BigDecimal.ONE;
        }

//        EntityManager em = OPDE.createEM();
//        BigDecimal result = null;
//
//
//
//        try {
//            Query query = em.createQuery("SELECT AVG(b.apv) FROM MedBestand b WHERE b.darreichung = :darreichung");
//            query.setParameter("darreichung", darreichung);
//            result = (BigDecimal) query.getSingleResult();
//        } catch (NoResultException nre) {
//            result = BigDecimal.ONE; // Im Zweifel ist das 1
//        } catch (Exception e) {
//            OPDE.fatal(e);
//        } finally {
//            em.close();
//        }
        return apv;
    }

    public static String getMediKontrolle(EntityManager em, Stationen station, int headertiefe) {
        StringBuilder html = new StringBuilder(1000);

        String jpql = " " +
                " SELECT b FROM MedBestand b " +
                " WHERE b.vorrat.bewohner.station = :station AND b.vorrat.bewohner.adminonly <> 2 " +
                " AND b.anbruch < :anbruch AND b.aus = " + SYSConst.MYSQL_DATETIME_BIS_AUF_WEITERES +
                " ORDER BY b.vorrat.bewohner.nachname, b.vorrat.bewohner.vorname, b.vorrat.text, b.anbruch ";

        Query query = em.createQuery(jpql);
        query.setParameter("anbruch", new Date());
        query.setParameter("station", station);
        List<MedBestand> list = query.getResultList();

        DateFormat df = DateFormat.getDateInstance();
        if (!list.isEmpty())
            html.append("<h" + headertiefe + ">");
        html.append("Liste zur Medikamentenkontrolle");
        html.append("</h" + headertiefe + ">");
        html.append("<h" + (headertiefe + 1) + ">");
        html.append("Legende");
        html.append("</h" + (headertiefe + 1) + ">");
        html.append("#1 - Medikament abgelaufen<br/>");
        html.append("#2 - Packung nicht beschriftet<br/>");
        html.append("#3 - Packung beschädigt<br/>");
        html.append("#4 - Anbruchsdatum nicht vermerkt<br/>");
        html.append("#5 - Medikament ist nicht verordnet<br/>");
        html.append("#6 - Mehr als 1 Blister im Anbruch<br/>");
        html.append("#7 - Mehr als 1 Tablette geteilt<br/>");
        html.append("#8 - falsche Bestandsnummer im Anbruch<br/><br/>");

        html.append("<table border=\"1\"><tr>" +
                "<th>BewohnerIn</th><th>BestNr</th><th>Präparat</th><th>Anbruch</th><th>#1</th><th>#2</th><th>#3</th><th>#4</th><th>#5</th><th>#6</th><th>#7</th><th>#8</th></tr>");

        for (MedBestand bestand : list) {
            html.append("<tr>");
            html.append("<td>" + BewohnerTools.getBWLabelTextKompakt(bestand.getVorrat().getBewohner()) + "</td>");
            html.append("<td>" + bestand.getBestID() + "</td>");
            html.append("<td>" + DarreichungTools.toPrettyString(bestand.getDarreichung()) + "</td>");
            html.append("<td>" + df.format(bestand.getAnbruch()) + "</td>");
            html.append("<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        return html.toString();
    }
}
