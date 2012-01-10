package entity.verordnungen;

import entity.BewohnerTools;
import op.OPDE;
import op.tools.DlgException;
import op.tools.SYSPrint;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 18.11.11
 * Time: 14:28
 * To change this template use File | Settings | File Templates.
 */
public class MedBestandTools {
    public static boolean apvNeuberechnung = true;

    public static ListCellRenderer getBestandOnlyIDRenderer() {
        return new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                String text;
                if (o == null) {
                    text = SYSTools.toHTML("<i>Keine Auswahl</i>");
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
        MedBestand result = null;
        if (apv == null) {
            throw new NullPointerException("apv darf nicht null sein");
        }

        bestand.setAnbruch(new Date());
        if (apv.equals(BigDecimal.ZERO)) {
            // Das hier verhindert Division by Zero Exceptions.
            apv = BigDecimal.ONE;
        }
        bestand.setApv(apv);
        bestand = em.merge(bestand);

        return result;
    }


    public static String getBestandText4Print(MedBestand bestand) {
        String result = "";

        result = SYSPrint.EPL2_CLEAR_IMAGE_BUFFER;
        result += SYSPrint.EPL2_labelformat(57, 19, 3);
        result += SYSPrint.EPL2_print_ascii(5, 5, 0, SYSPrint.EPL2_FONT_7pt, 1, 1, false, bestand.getDarreichung().getMedProdukt().getBezeichnung() + " " + bestand.getDarreichung().getZusatz());
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

    /**
     * Ermittelt die Menge, die in einer Packung noch enthalten ist.
     *
     * @param bestand die entsprechende Packung
     * @return die Summe in der Packungs Einheit.
     */
    public static BigDecimal getBestandSumme(MedBestand bestand) {
        BigDecimal result = BigDecimal.ZERO;

        EntityManager em = OPDE.createEM();
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
        } catch (Exception ex) {
            OPDE.fatal(ex);
        } finally {
            em.close();
        }
        return result;
    }

    /**
     * @param em
     * @param bestand
     * @param text
     * @param mitNeuberechnung
     * @param status
     * @return Falls die Neuberechung gewünscht war, steht hier das geänderte, bzw. neu erstelle APV Objekt. null andernfalls.
     * @throws Exception
     */
    public static BigDecimal abschliessen(EntityManager em, MedBestand bestand, String text, boolean mitNeuberechnung, short status) throws Exception {
        BigDecimal bestandsumme = getBestandSumme(bestand);
        BigDecimal apvNeu = bestand.getApv();

        MedBuchungen buchung = new MedBuchungen(bestand, bestandsumme.negate(), null, status);
        buchung.setText(text);
        em.persist(buchung);

        bestand.setAus(new Date());
        bestand.setNaechsterBestand(null);
        bestand = em.merge(bestand);

        if (mitNeuberechnung) { // Wenn gewünscht wird bei Abschluss der Packung der APV neu berechnet.
            apvNeu = berechneBuchungsWert(bestand);

            OPDE.info("Neuberechnung von DafID:" + bestand.getDarreichung().getDafID() + ", " + bestand.getDarreichung().getMedProdukt().getBezeichnung());

//            if (bestand.getDarreichung().getMedForm().getStatus() != MedFormenTools.APV1) {
//                apvNeu = berechneBuchungsWert(bestand);
//
//                if (bestand.getDarreichung().getMedForm().getStatus() == MedFormenTools.APV_PER_BW) {
//                    apv = new APV(apvNeu, false, bestand.getVorrat().getBewohner(), bestand.getDarreichung());
//                    em.persist(apv);
//                    OPDE.info("FormStatus APV_PER_BW. APVneu: " + apvNeu.toPlainString());
//                } else {
//                    // APV_PER_DAF hier gibt es immer nur ein APV Objekt. Das wird entweder ausgetauscht (nur bei Beginn, wenn man
//                    // noch nicht weiss, wie der Bedarf ist) oder durch das arithmetische Mittel des alten apv und des neuen erstetzt.
//
//                    BigDecimal apvAlt = bestand.getApv();
//                    // Zugehöriges APV Objekt ermitteln.
//
//                    apv = APVTools.getAPV(bestand.getDarreichung());
//
//                    if (apv.isTauschen()) {
//                        apv.setApv(apvNeu);
//                        apv.setTauschen(false);
//                        OPDE.info("FormStatus APV_PER_DAF. APValt: " + apvAlt + "  APVneu: " + apvNeu + "  !Wert wurde ausgetauscht!");
//                    } else {
//                        // der DafID APV wird durch den arithmetischen Mittelwert aus altem und neuem APV ersetzt.
//                        apv.setApv(apvAlt.add(apvNeu).divide(BigDecimal.valueOf(2), 4, BigDecimal.ROUND_UP));
//                        OPDE.info("FormStatus APV_PER_DAF. APValt: " + apvAlt.toPlainString() + "  APVneu: " + apv.getApv().toPlainString());
//                    }
//                    apv = em.merge(apv);
//                }
//            }
        }
        return apvNeu;
    }

    /**
     * @param bestand, für den das Verhältnis neu berechnet werden soll.
     */
    public static BigDecimal berechneBuchungsWert(MedBestand bestand) {

        BigDecimal apvNeu = BigDecimal.ONE;

        if (bestand.getDarreichung().getMedForm().getStatus() != MedFormenTools.APV1) {

            EntityManager em = OPDE.createEM();
            try {
                Query queryAnfangsBuchung = em.createQuery("SELECT m FROM MedBuchungen m WHERE m.bestand = :bestand AND m.status = :status");
                queryAnfangsBuchung.setParameter("bestand", bestand);
                queryAnfangsBuchung.setParameter("status", MedBuchungenTools.STATUS_EINBUCHEN_ANFANGSBESTAND);
                MedBuchungen anfangsBuchung = (MedBuchungen) queryAnfangsBuchung.getSingleResult();

                // Menge in der Packung (in der Packungseinheit). Also das, was wirklich in der Packung am Anfang
                // drin war. Meist das, was auf der Packung steht.
                BigDecimal inhaltZuBeginn = anfangsBuchung.getMenge();

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

        BigDecimal bestandSumme = getBestandSumme(bestand);
        if (!bestandSumme.equals(soll)) {
            BigDecimal korrektur;
            if (bestandSumme.compareTo(BigDecimal.ZERO) <= 0) {
                korrektur = bestandSumme.abs().add(soll);
            } else {
                korrektur = bestandSumme.negate().add(soll);
            }

            // passende Buchung anlegen.
            result = new MedBuchungen(bestand, korrektur, null, status);
            result.setText(text);
            em.persist(result);
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

    public static boolean hasAbgesetzteBestaende(BHP bhp) {
        boolean result = false;

        EntityManager em = OPDE.createEM();

        try {
            Query query = em.createQuery(" " +
                    " SELECT b FROM MedBestand b " +
                    " JOIN b.buchungen bu " +
                    " WHERE bu.bhp = :bhp " +
                    " AND b.aus < '9999-12-31 23:59:59'");
            query.setParameter("bhp", bhp);
            result = !query.getResultList().isEmpty();
        } catch (Exception ex) {
            OPDE.fatal(ex);
        } finally {
            em.close();
        }
        return result;
    }

    /**
     * Ermittelt für einen bestimmten Bestand ein passendes APV.
     */
    public static BigDecimal getPassendesAPV(MedBestand bestand) {

        BigDecimal apv = BigDecimal.ONE;

        if (bestand.getDarreichung().getMedForm().getStatus() == MedFormenTools.APV_PER_BW){
            apv = getAPVperBW(bestand.getVorrat());
        } else if (bestand.getDarreichung().getMedForm().getStatus() == MedFormenTools.APV_PER_DAF){
            apv = getAPVperDAF(bestand.getDarreichung());
        }

        return apv;
    }

    public static BigDecimal getAPVperBW(MedVorrat vorrat) {
        EntityManager em = OPDE.createEM();
        BigDecimal result = null;

        try {
            Query query = em.createQuery("SELECT AVG(b.apv) FROM MedBestand b WHERE b.vorrat = :vorrat");
            query.setParameter("vorrat", vorrat);
            result = (BigDecimal) query.getSingleResult();
        } catch (NoResultException nre) {
            result = BigDecimal.ONE; // Im Zweifel ist das 1
        } catch (Exception e) {
            OPDE.fatal(e);
        } finally {
            em.close();
        }
        return result;
    }

    public static BigDecimal getAPVperDAF(Darreichung darreichung) {
        EntityManager em = OPDE.createEM();
        BigDecimal result = null;

        try {
            Query query = em.createQuery("SELECT AVG(b.apv) FROM MedBestand b WHERE b.darreichung = :darreichung");
            query.setParameter("darreichung", darreichung);
            result = (BigDecimal) query.getSingleResult();
        } catch (NoResultException nre) {
            result = BigDecimal.ONE; // Im Zweifel ist das 1
        } catch (Exception e) {
            OPDE.fatal(e);
        } finally {
            em.close();
        }
        return result;
    }


}
