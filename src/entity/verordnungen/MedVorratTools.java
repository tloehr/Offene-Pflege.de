package entity.verordnungen;

import entity.Bewohner;
import op.OPDE;
import op.tools.SYSConst;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 18.11.11
 * Time: 16:25
 * To change this template use File | Settings | File Templates.
 */
public class MedVorratTools {


    public static BigDecimal getSumme(MedVorrat vorrat) {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("MedVorrat.getSumme");
        query.setParameter("vorrat", vorrat);
        BigDecimal result = (BigDecimal) query.getSingleResult();
        return result;
    }

    /**
     * Bucht eine Menge aus einem Vorrat aus, ggf. zugehörig zu einer BHP. Übersteigt die Entnahme-Menge den
     * Restbestband, dann wird entweder
     * <ul>
     * <li>wenn der nächste Bestand <b>nicht</b> bekannt ist (<code>bestand.hasNextBestand() = <b>false</b></code>) &rarr; der Bestand trotzdem weiter gebucht. Bis ins negative.</li>
     * <li>wenn der nächste Bestand <b>doch schon</b> bekannt ist (<code>bestand.hasNextBestand() = <b>true</b></code>)  &rarr; ein neuer Bestand wird angebrochen.</li>
     * </ul>
     * Ist <b>keine</b> Packung im Anbruch, dann wird eine Exception geworfen.
     *
     * @param em          der EntityManager der verwendet wird
     * @param darreichung das zu entnehmende Präparat
     * @param bewohner    der Bewohner, um den es geht
     * @param menge       die gewünschte Entnahmemenge
     * @param anweinheit  true, dann wird in der Anwedungseinheit ausgebucht. false, in der Packungseinheit
     * @param bhp         BHP aufgrund dere dieser Buchungsvorgang erfolgt.
     */
    public static void entnahmeVorrat(EntityManager em, Darreichung darreichung, Bewohner bewohner, BigDecimal menge, boolean anweinheit, BHP bhp) throws Exception {

        HIER GEHTS WEITER
        MedVorrat vorrat = DarreichungTools.getVorratZurDarreichung(bewohner, darreichung);

        foundExactMatch = vorraete != null;
        if (vorraete == null) {
            vorraete = DarreichungTools.getPassendeVorraeteZurDarreichung(bewohner, darreichung);
        }
        cmbVorrat.setModel(new DefaultComboBoxModel(vorraete.toArray()));


        if (vorrat != null) {
            if (anweinheit) { // Umrechnung der Anwendungs Menge in die PackMenge.
                MedBestand bestand = MedVorratTools.getImAnbruch(vorrat);
                BigDecimal apv = bestand.getApv();

                if (apv.equals(BigDecimal.ZERO)) {
                    apv = BigDecimal.ONE;
                }
                menge = menge.divide(apv);
            }
            entnahmeVorrat(em, vorrat, menge, bhp);
        }
    }

    /**
     * Die Rückgabe eines Vorrats bezieht sich auf eine BHP für die die Buchungen zurückgerechnet werden
     * sollen.
     * <ol>
     * <li>Zuerst werden alle Buchungen zu einer BHPID herausgesucht.</li>
     * <li>Gibt es mehr als eine, dann wurde f¸r die Buchung ein P?ckchen aufgebraucht und ein neues angefangen. In diesem Fall wird die Ausf¸hrung abgelehnt.</li>
     * <li>Es werden alle zugeh?rigen Buchungen zu dieser BHPID gel?scht.</li>
     * </ol>
     *
     * @result true bei Erfolg, false sonst.
     */
    public static void rueckgabeVorrat(EntityManager em, BHP bhp) throws Exception {

        Query delQuery = em.createQuery("DELETE FROM MedBuchungen b WHERE b.bhp = :bhp");
        delQuery.setParameter("bhp", bhp);


        if (delQuery.executeUpdate() > 1) { // es gibt genau eine Buchung.
            throw new Exception("Rueckgabe Vorrat");
        }

    }


    protected static void entnahmeVorrat(EntityManager em, MedVorrat vorrat, BigDecimal wunschmenge, BHP bhp) throws Exception {
        MedBestand bestand = MedVorratTools.getImAnbruch(vorrat);

        if (bestand != null && wunschmenge.compareTo(BigDecimal.ZERO) > 0) {
            // ist schon eine Packung festgelegt, die angebrochen werden soll, sobald diese leer ist ?
            //MedBestand nextBestand = bestand.getNaechsterBestand();


            BigDecimal restsumme = MedBestandTools.getBestandSumme(bestand); // wieviel der angebrochene Bestand noch hergibt.

            // normalerweise wird immer das hergegeben, was auch gew¸nscht ist. Notfalls bis ins minus.
            BigDecimal entnahme = wunschmenge; // wieviel in diesem Durchgang tatsächlich entnommen wird.

            // sollte eine Packung aber schon als nachfolger bestimmt sein,
            if (bestand.hasNextBestand() && restsumme.compareTo(wunschmenge) <= 0) {
                entnahme = restsumme; // dann wird erst diese hier leergebraucht
            } // und dann der Rest aus der nächsten Packung genommen.


            // Erstmal die Buchung für diesen Durchgang

            MedBuchungen buchung = new MedBuchungen(bestand, entnahme.negate(), bhp);
            em.persist(buchung);

            if (bestand.hasNextBestand()) { // Jetzt gibt es direkt noch den Wunsch das nächste Päckchen anzubrechen.
                if (restsumme.compareTo(wunschmenge) <= 0) {
                    // Es war mehr gewünscht, als die angebrochene Packung hergegeben hat.
                    // Dann müssen wird erstmal den alten Bestand abschließen.

                    APV apv = MedBestandTools.abschliessen(em, bestand, "Automatischer Abschluss bei leerer Packung", true, MedBuchungenTools.STATUS_KORREKTUR_AUTO_VORAB);
                    // dann den neuen (NextBest) Bestand anbrechen.
                    // Das noch nichts commited wurde, übergeben wir hier den neuen APV direkt als BigDecimal mit.
                    MedBestandTools.anbrechen(em, bestand.getNaechsterBestand(), apv.getApv());
                }

                if (wunschmenge.compareTo(entnahme) > 0) { // Sind wir hier fertig, oder müssen wir noch mehr ausbuchen.
                    entnahmeVorrat(em, vorrat, wunschmenge.subtract(entnahme), bhp);
                }
            }
        }
    }

    /**
     * bucht ein Medikament in einen Vorrat ein. Aber nur dann, wenn es keinen anderen Vorrat gibt,
     * der mit seiner DafID schon passt.
     * <p/>
     * Falls diese DafID noch den Dummy "Startbestand" hat, wird dieser zuerst gelöscht.
     *
     * @return true, bei Erfolg. false, sonst.
     */
    public static MedBestand einbuchenVorrat(EntityManager em, MedVorrat vorrat, MedPackung packung, Darreichung darreichung, String text, BigDecimal menge) throws Exception {
        MedBestand bestand = null;
        if (menge.compareTo(BigDecimal.ZERO) > 0) {

            BigDecimal apv;
            if (darreichung.getMedForm().getStatus() == MedFormenTools.APV_PER_BW) {
                apv = APVTools.getAPVMittelwert(vorrat.getBewohner(), darreichung);
            } else if (darreichung.getMedForm().getStatus() == MedFormenTools.APV_PER_DAF) {
                apv = APVTools.getAPV(darreichung).getApv();
            } else { //APV1
                apv = BigDecimal.ONE;
            }

            bestand = new MedBestand(apv, vorrat, darreichung, packung, text);
            MedBuchungen buchung = new MedBuchungen(bestand, menge);

            em.persist(bestand);
            em.persist(buchung);
        }
        return bestand;
    }


    public static MedBestand getNaechsteNochUngeoeffnete(MedVorrat vorrat) {
        MedBestand bestand = null;
        if (!vorrat.getBestaende().isEmpty()) {
            MedBestand[] bestaende = vorrat.getBestaende().toArray(new MedBestand[0]);
            Arrays.sort(bestaende); // nach Einbuchung
            for (MedBestand myBestand : bestaende) {
                if (!myBestand.isAngebrochen()) {
                    bestand = myBestand;
                    break;
                }
            }
        }
        return bestand;
    }

    public static MedBestand getNaechsteNichtAbgeschlossene(MedVorrat vorrat) {
        MedBestand bestand = null;
        if (!vorrat.getBestaende().isEmpty()) {
            MedBestand[] bestaende = vorrat.getBestaende().toArray(new MedBestand[0]);
            Arrays.sort(bestaende); // nach Einbuchung
            for (MedBestand myBestand : bestaende) {
                if (!myBestand.isAbgeschlossen()) {
                    bestand = myBestand;
                    break;
                }
            }
        }
        return bestand;
    }


    public static MedBestand getImAnbruch(MedVorrat vorrat) {
        MedBestand bestand = null;
        Iterator<MedBestand> itBestand = vorrat.getBestaende().iterator();
        while (itBestand.hasNext()) {
            bestand = itBestand.next();
            if (bestand.isAngebrochen()) {
                break;
            }
        }
//
//        MedBestand bestand = null;
//        EntityManager em = OPDE.createEM();
//        try {
//            Query query = em.createNamedQuery("MedBestand.findByVorratImAnbruch");
//            query.setParameter("vorrat", vorrat);
//            bestand = (MedBestand) query.getSingleResult();
//        } catch (NoResultException nre) {
//            OPDE.debug(nre);
//            // durchaus gewünschtes Ergebnis
//        } catch (Exception e) {
//            OPDE.fatal(e);
//        } finally {
//            em.close();
//        }
        return bestand;
    }

    /**
     * Bricht von allen geschlossenen das nächste (im Sinne des Einbuchdatums) an. Funktioniert nur bei Vorräten, die z.Zt. keine
     * angebrochenen Bestände haben.
     *
     * @param vorrat
     * @return der neu angebrochene Bestand. null, wenns nicht geklappt hat.
     */
    public static MedBestand anbrechenNaechste(MedVorrat vorrat) throws Exception {
        MedBestand result = null;
        EntityManager em = OPDE.createEM();
        try {
            Query query = em.createQuery(" " +
                    " SELECT b FROM MedBestand b " +
                    " WHERE b.vorrat = :vorrat AND b.aus = :aus AND b.anbruch = :anbruch " +
                    " ORDER BY b.ein, b.bestID "); // Geht davon aus, dass die PKs immer fortlaufend, automatisch vergeben werden.
            query.setParameter("vorrat", vorrat);
            query.setParameter("aus", SYSConst.DATE_BIS_AUF_WEITERES);
            query.setParameter("anbruch", SYSConst.DATE_BIS_AUF_WEITERES);
            query.setMaxResults(1);
            result = (MedBestand) query.getSingleResult();
            MedBestandTools.anbrechen(result);
        } catch (NoResultException nre) {
            OPDE.debug(nre);
        } catch (Exception ex) {
            OPDE.fatal(ex);
        } finally {
            em.close();
        }

        return result;
    }
//
//    public static boolean abschliessen(MedVorrat vorrat) {
//        Connection db = OPDE.getDb().db;
//        boolean result = false;
//        boolean doCommit = false;
//        try {
//            if (db.getAutoCommit()) {
//                db.setAutoCommit(false);
//                db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
//                db.commit();
//                doCommit = true;
//            }
//            // Alle Best?nde abschlie?en.
//            long bestid = getBestandImAnbruch(vorid);
//            if (bestid > 0) {
//                closeBestand(bestid, "Abschluss des Bestandes bei Vorratsabschlu?.", false, STATUS_KORREKTUR_AUTO_ABSCHLUSS_BEI_VORRATSABSCHLUSS);
//            }
//            DefaultComboBoxModel dcbm = getBestandGeschlossen(vorid);
//            if (dcbm.getSize() > 1) { // der erste ist immer "keine"
//                for (int i = 1; i < dcbm.getSize(); i++) {
//                    bestid = (Long) dcbm.getElementAt(i);
//                    closeBestand(bestid, "Abschluss des Bestandes bei Vorratsabschlu?.", false, STATUS_KORREKTUR_AUTO_ABSCHLUSS_BEI_VORRATSABSCHLUSS);
//                }
//            }
//
//            // Vorrat beenden.
//            HashMap data = new HashMap();
//            data.put("bis", "!NOW!");
//            if (!op.tools.DBHandling.updateRecord("MPVorrat", data, "VorID", vorid)) {
//                throw new SQLException("Fehler bei Update MPVorrat");
//            }
//            data.clear();
//            if (doCommit) {
//                db.commit();
//                db.setAutoCommit(true);
//            }
//            result = true;
//        } catch (SQLException ex) {
//            try {
//                if (doCommit) {
//                    db.rollback();
//                }
//                result = false;
//            } catch (SQLException ex1) {
//                new DlgException(ex1);
//                ex1.printStackTrace();
//                System.exit(1);
//            }
//            new DlgException(ex);
//        }
//        return result;
//    }
//
//    public static void deleteVorrat(long vorid) {
//        String sql1 = "DELETE buch.* " +
//                " FROM MPBuchung buch INNER JOIN MPBestand best ON buch.BestID = best.BestID " +
//                " WHERE best.VorID = ? ";
//        String sql2 = "DELETE best.* " +
//                " FROM MPBestand best " +
//                " WHERE best.VorID = ? ";
//        String sql3 = "DELETE " +
//                " FROM MPVorrat " +
//                " WHERE VorID = ? ";
//        Connection db = OPDE.getDb().db;
//        //boolean result = false;
//        boolean doCommit = false;
//        try {
//            // Hier beginnt eine Transaktion, wenn es nicht schon eine gibt.
//            if (db.getAutoCommit()) {
//                db.setAutoCommit(false);
//                db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
//                db.commit();
//                doCommit = true;
//            }
//
//            PreparedStatement stmt1 = OPDE.getDb().db.prepareStatement(sql1);
//            stmt1.setLong(1, vorid);
//            stmt1.executeUpdate();
//
//            PreparedStatement stmt2 = OPDE.getDb().db.prepareStatement(sql2);
//            stmt2.setLong(1, vorid);
//            stmt2.executeUpdate();
//
//            PreparedStatement stmt3 = OPDE.getDb().db.prepareStatement(sql3);
//            stmt3.setLong(1, vorid);
//            stmt3.executeUpdate();
//
//            if (doCommit) {
//                db.commit();
//                db.setAutoCommit(true);
//            }
//
//        } catch (SQLException ex) {
//            try {
//                if (doCommit) {
//                    db.rollback();
//                }
//                //result = false;
//            } catch (SQLException ex1) {
//                new DlgException(ex1);
//                ex1.printStackTrace();
//                System.exit(1);
//            }
//            new DlgException(ex);
//        }
//    }
}
