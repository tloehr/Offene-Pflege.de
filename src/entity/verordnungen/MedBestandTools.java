package entity.verordnungen;

import entity.BewohnerTools;
import op.OPDE;
import op.tools.DlgException;
import op.tools.SYSConst;
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

     public static ListCellRenderer getBestandOnlyIDRenderer() {
        return new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean b, boolean b1) {
                JLabel l = new JLabel();
                if (o == null) {
                    l.setText("<i>Keine Auswahl</i>");
                } else if (o instanceof MedBestand) {
                    MedBestand bestand = (MedBestand) o;
                    l.setText(bestand.getBestID().toString());
                } else {
                    l.setText(o.toString());
                }
                return l;
            }
        };
    }

    public static MedBestand findByVerordnungImAnbruch(Verordnung verordnung) {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("MedBestand.findByDarreichungAndBewohnerImAnbruch");
        query.setParameter("bewohner", verordnung.getBewohner());
        query.setParameter("darreichung", verordnung.getDarreichung());

        MedBestand result = null;

        try {
            result = (MedBestand) query.getSingleResult();
        } catch (NoResultException nre) {
            result = null;
        } catch (Exception e) {
            OPDE.fatal(e);
            System.exit(1);
        } finally {
            em.close();
        }

        return result;
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

        if (bestand.getDarreichung().getMedForm().getStatus() == MedFormenTools.APV_PER_BW) {
            apv = APVTools.getAPVMittelwert(bestand.getVorrat().getBewohner(), bestand.getDarreichung());
        } else if (bestand.getDarreichung().getMedForm().getStatus() == MedFormenTools.APV_PER_DAF) {
            apv = APVTools.getAPV(bestand.getDarreichung()).getApv();
        } else { //APV1
            apv = BigDecimal.ONE;
        }

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
    public static APV abschliessen(EntityManager em, MedBestand bestand, String text, boolean mitNeuberechnung, short status) throws Exception {
        APV apv = null;
        BigDecimal bestandsumme = getBestandSumme(bestand);

        MedBuchungen buchung = new MedBuchungen(bestand, bestandsumme.negate(), null, status);
        em.persist(buchung);

        bestand.setAus(new Date());
        bestand.setNaechsterBestand(null);
        bestand = em.merge(bestand);

        if (mitNeuberechnung) { // Wenn gewünscht wird bei Abschluss der Packung der APV neu berechnet.

            OPDE.info("Neuberechnung von DafID:" + bestand.getDarreichung().getDafID() + ", " + bestand.getDarreichung().getMedProdukt().getBezeichnung());

            if (bestand.getDarreichung().getMedForm().getStatus() != MedFormenTools.APV1) {
                BigDecimal apvNeu = berechneBuchungsWert(bestand);
                if (bestand.getDarreichung().getMedForm().getStatus() == MedFormenTools.APV_PER_BW) {
                    // bei APV_PER_BW werden immer neue APV Objekte gespeichert
                    // sofern sie in einem bestimmten korridor liegen.
                    // Der Mittelwert wird dann nachher über alle APV Objekte gerechnet.
                    apv = new APV(apvNeu, false, bestand.getVorrat().getBewohner(), bestand.getDarreichung());
                    em.persist(apv);
                    OPDE.info("FormStatus APV_PER_BW. APVneu: " + apvNeu.toPlainString());
                } else {
                    // APV_PER_DAF hier gibt es immer nur ein APV Objekt. Das wird entweder ausgetauscht (nur bei Beginn, wenn man
                    // noch nicht weiss, wie der Bedarf ist) oder durch das arithmetische Mittel des alten apv und des neuen erstetzt.

                    BigDecimal apvAlt = bestand.getApv();
                    // Zugehöriges APV Objekt ermitteln.

                    apv = APVTools.getAPV(bestand.getDarreichung());

                    if (apv.isTauschen()) {
                        apv.setApv(apvNeu);
                        apv.setTauschen(false);
                        OPDE.info("FormStatus APV_PER_DAF. APValt: " + apvAlt + "  APVneu: " + apvNeu + "  !Wert wurde ausgetauscht!");
                    } else {
                        // der DafID APV wird durch den arithmetischen Mittelwert aus altem und neuem APV ersetzt.
                        apv.setApv(apvAlt.add(apvNeu).divide(BigDecimal.valueOf(2)));
                        OPDE.info("FormStatus APV_PER_DAF. APValt: " + apvAlt.toPlainString() + "  APVneu: " + apv.getApv().toPlainString());
                    }
                    apv = em.merge(apv);
                }
            }
        }
        return apv;
    }

    /**
     * Die Berechnungsmethode unterscheidet verschiedene Fälle:
     * <p/>
     * <ul>
     * <li>Die betroffene Packung hat die <code>FormStatus = APV1</code>. Das sind z.B. alle Tabletten oder Kapseln. Hier macht es
     * keinen Sinn, irgendwelche Verhältnismaße neu zu rechnen. Wenn es eine Diskrepanz zwischen gerechnetem und realem Bestandswert
     * gibt, dann ist irgendeine Tablette runtergefallen und nicht ausgebucht worden. Deshalb wird der Bestand einfach korrgiert und dann
     * ist das eben so.
     * <ul><li>Die Packung ist <b>jetzt</b> leer. Egal, wie der Buchungsbestand war. Er wird jetzt <i>gewaltsam</i> auf 0 gebracht.
     * Der Bestand abgeschlossen und (wenn gew¸nscht) der neue angebrochen.</li>
     * </ul></li>
     * <p/>
     * <li>Die Packung hat die Form Salben, Cremes. <code>FORMSTATUS = APV_PER_BW</code>. Hierbei passiert es häufig, dass die Menge, die bei einer Anwendung
     * verbraucht wird individuell vom Bewohner abhängt. Der eine braucht viel Salbe, der andere wenig. Hängt auch vom Krankheitsverlauf ab.
     * Sagen wir mal eine Salbe gegen Schuppenflechte. Das kann an einem Tag wenig und an einem anderen Tag viel sein. Somit macht es keinen Sinn,
     * hier einen Wert für die Salbe zu speichern. Höchstens einen Mittelwert aus allen Bewohner APVs.
     * <ul>
     * <li>Der Bestand wird abgeschlossen. Es wird ein neuer APV gerechnet. Dieser wird in der Entity Bean APV eingetragen und zwar für einen bestimmten
     * BW.</li>
     * </ul></li>
     * <p/>
     * <li>Die Packung hat die Form Tropfen, Sirup. <code>FORMSTATUS = APV_PER_DAF</code>. Hier macht es wiederum keinen Sinn einen APV pro BW zu speichern.
     * 5 Tropfen sind eben bei allen Bewohnern 5 Tropfen.
     * <ul>
     * <li>Der Bestand wird abgeschlossen. Es wird ein neuer APV gerechnet. Dieser wird in der Tabelle MPAPV eingetragen, hier allerdings mit
     * BWKennung = "".</li>
     * </ul></li>
     * </ul>
     * <p/>
     * Nach den Berechnungen wird immer (wenn gew¸nscht) ein neuer Bestand angebrochen. Falls ein APV berechnet wurde, wird jeweils der neue Mittelwert
     * ¸ber alle APVs einer Darreichung als neuer Anfangs APV dieser Darreichung hinterlegt. Also MPDarreicung.APV = AVG(MPAPV.APV).
     *
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

                // Menge in der Packung (in der Packungseinheit)
                BigDecimal inhaltZuBeginn = anfangsBuchung.getMenge();

                BigDecimal apvAlt = bestand.getApv();

                // Anzahl der per BHP verabreichten Einzeldosen. (in der Anwendungseinheit)
                Query querySummeBHPDosis = em.createQuery(" " +
                        " SELECT SUM(bhp.dosis) " +
                        " FROM MedBuchungen bu " +
                        " JOIN bu.bhp bhp" +
                        " WHERE bu.bestand = :bestand ");

                querySummeBHPDosis.setParameter("bestand", bestand);
                BigDecimal summeBHPDosis = (BigDecimal) querySummeBHPDosis.getSingleResult();

                BigDecimal inhaltRechnerisch = summeBHPDosis.divide(apvAlt);

                apvNeu = inhaltZuBeginn.divide(inhaltRechnerisch).multiply(apvAlt);

                // Zu große APV Abweichungen verhindern.
                BigDecimal apvkorridor = new BigDecimal(Double.parseDouble(OPDE.getProps().getProperty("apv_korridor"))).divide(BigDecimal.valueOf(100));
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
        String sql = " SELECT vor.BWKennung, best.Ein, best.UKennung, prod.Bezeichnung, daf.Zusatz, pack.PZN, " +
                " CASE pack.Groesse WHEN 0 THEN 'N1' WHEN 1 THEN 'N2' " +
                " WHEN 2 THEN 'N3' WHEN 3 THEN 'AP' WHEN 4 THEN 'OP' ELSE '' END Groesse, pack.Inhalt, best.Text," +
                " f.PackEinheit, f.Zubereitung, f.AnwText " +//, ifnull(b.saldo, 0.00) Bestandsmenge " +
                " FROM MPBestand best " +
                " INNER JOIN MPVorrat vor ON vor.VorID = best.VorID " +
                " INNER JOIN MPDarreichung daf ON daf.DafID = best.DafID " +
                " INNER JOIN MProdukte prod ON prod.MedPID = daf.MedPID " +
                " INNER JOIN MPFormen f ON f.FormID = daf.FormID " +
                " LEFT OUTER JOIN MPackung pack ON best.MPID = pack.MPID " +
                " WHERE best.BestID = ? ";


        result += "<br/><font color=\"blue\"><b>" + bestand.getDarreichung().getMedProdukt().getBezeichnung() + " " + bestand.getDarreichung().getZusatz() + ", ";

        if (!SYSTools.catchNull(bestand.getPackung().getPzn()).equals("")) {
            result += "PZN: " + bestand.getPackung().getPzn() + ", ";
            result += MedPackungTools.GROESSE[bestand.getPackung().getGroesse()] + ", " + bestand.getPackung().getInhalt() + " " + MedFormenTools.EINHEIT[bestand.getDarreichung().getMedForm().getPackEinheit()] + " ";
            String zubereitung = SYSTools.catchNull(bestand.getDarreichung().getMedForm().getZubereitung());
            String anwtext = SYSTools.catchNull(bestand.getDarreichung().getMedForm().getAnwText());
            result += zubereitung.equals("") ? anwtext : (anwtext.equals("") ? zubereitung : zubereitung + ", " + anwtext);
            result += "</b></font><br/>";
        }

        return result;
    }

}
