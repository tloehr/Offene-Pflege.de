package entity.verordnungen;

import op.OPDE;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 18.11.11
 * Time: 16:25
 * To change this template use File | Settings | File Templates.
 */
public class MedVorratTools {

    public static ListCellRenderer getMedVorratRenderer() {
        return new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                String text;
                if (o == null) {
                    text = SYSTools.toHTML("<i>Keine Auswahl</i>");
                } else if (o instanceof MedVorrat) {
                    MedVorrat vorrat = (MedVorrat) o;
                    text = vorrat.getText();
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
            }
        };
    }

    public static BigDecimal getSumme(MedVorrat vorrat) {
//        long timeStart = System.currentTimeMillis();
        BigDecimal result = BigDecimal.ZERO;
        for (MedBestand bestand : vorrat.getBestaende()) {
            BigDecimal summe = MedBestandTools.getBestandSumme(bestand);
            result = result.add(summe);
        }
//        long time2 = System.currentTimeMillis();
//        OPDE.debug("MedVorratTools.getSumme(): " + (time2 - timeStart) + " millis");
        return result;
    }

    public static BigDecimal getSumme(EntityManager em, MedVorrat vorrat) throws Exception {
//        long timeStart = System.currentTimeMillis();
        BigDecimal result = BigDecimal.ZERO;
        for (MedBestand bestand : vorrat.getBestaende()) {
            BigDecimal summe = MedBestandTools.getBestandSumme(em, bestand);
            result = result.add(summe);
        }
//        long time2 = System.currentTimeMillis();
//        OPDE.debug("MedVorratTools.getSumme(): " + (time2 - timeStart) + " millis");
        return result;
    }

    /**
     * Bucht eine Menge aus einem Vorrat aus, ggf. zugehörig zu einer BHP. Übersteigt die Entnahme-Menge den
     * Restbestband, dann wird entweder
     * <ul>
     * <li>wenn der nächste Bestand <b>nicht</b> bekannt ist (<code>bestand.hasNextBestand() = <b>false</b></code>) &rarr; der Bestand trotzdem weiter gebucht. Bis ins negative.</li>
     * <li>wenn der nächste Bestand <b>doch schon</b> bekannt ist (<code>bestand.hasNextBestand() = <b>true</b></code>)  &rarr; ein neuer Bestand wird angebrochen.</li>
     * </ul>
     * Ist <b>keine</b> Packung im Anbruch, dann wird eine Exception geworfen. Das kann aber eingentlich nicht passieren. Es sei denn jemand hat von Hand
     * an den Datenbank rumgespielt.
     *
     * @param em         der EntityManager der verwendet wird
     * @param menge      die gewünschte Entnahmemenge
     * @param anweinheit true, dann wird in der Anwedungseinheit ausgebucht. false, in der Packungseinheit
     * @param bhp        BHP aufgrund dere dieser Buchungsvorgang erfolgt.
     */
    public static void entnahmeVorrat(EntityManager em, MedVorrat vorrat, BigDecimal menge, boolean anweinheit, BHP bhp) throws Exception {

        vorrat = em.merge(vorrat);
        bhp = em.merge(bhp);

        OPDE.debug("entnahmeVorrat/5: vorrat: " + vorrat);

        if (vorrat == null) {
            throw new Exception("Keine Packung im Anbruch");
        }

        if (anweinheit) { // Umrechnung der Anwendungs Menge in die Packungs-Menge.
            MedBestand bestand = MedBestandTools.getBestandImAnbruch(vorrat);
            BigDecimal apv = bestand.getApv();

            if (apv.equals(BigDecimal.ZERO)) {
                apv = BigDecimal.ONE;
            }
            menge = menge.divide(apv, 4, BigDecimal.ROUND_UP);
        }

        OPDE.debug("entnahmeVorrat/5: menge: " + menge);

        entnahmeVorrat(em, vorrat, menge, bhp);
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
     * @param em  der EntityManager der verwendet wird
     * @param bhp die BHP, die zurück genommen wird.
     * @result true bei Erfolg, false sonst.
     */
    public static void rueckgabeVorrat(EntityManager em, BHP bhp) throws Exception {

        Query delQuery = em.createQuery("DELETE FROM MedBuchungen b WHERE b.bhp = :bhp");
        delQuery.setParameter("bhp", bhp);

        // Das hier passiert, wenn bei der Entnahme mehr als ein Bestand betroffen ist.
        // Dann kann man das nicht mehr rückgängig machen. Und es wird eine Exception geworfen.
        if (delQuery.executeUpdate() > 1) { // es gibt genau eine Buchung.
            throw new Exception("Rueckgabe Vorrat");
        }

    }


    public static void entnahmeVorrat(EntityManager em, MedVorrat vorrat, BigDecimal wunschmenge, BHP bhp) throws Exception {
        MedBestand bestand = MedBestandTools.getBestandImAnbruch(vorrat);

        OPDE.debug("entnahmeVorrat/4: bestand: " + bestand);

        if (bestand != null && wunschmenge.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal restsumme = MedBestandTools.getBestandSumme(em, bestand); // wieviel der angebrochene Bestand noch hergibt.

            // normalerweise wird immer das hergegeben, was auch gewünscht ist. Notfalls bis ins minus.
            BigDecimal entnahme = wunschmenge; // wieviel in diesem Durchgang tatsächlich entnommen wird.

            //TODO: hier gibts noch ein Problem. Wenn eine Packung leer wird und NEXTBEST != null, dann sollte mit dem leer werden die Packung auch abgeschlossen werden und nicht erst mit der nächsten Buchung

            if (bestand.hasNextBestand()) { // Jetzt gibt es direkt noch den Wunsch das nächste Päckchen anzubrechen.
                if (restsumme.compareTo(wunschmenge) <= 0) { // ist nicht mehr genug in der Packung, bzw. die Packung wird jetzt leer.
                    MedBestand naechsterBestand = bestand.getNaechsterBestand();

                    // Es war mehr gewünscht, als die angebrochene Packung hergegeben hat.
                    // Bzw. die Packung wurde mit dieser Gabe geleert.
                    // Dann müssen wird erstmal den alten Bestand abschließen.
                    MedBestandTools.abschliessen(em, bestand, "Automatischer Abschluss bei leerer Packung", MedBuchungenTools.STATUS_KORREKTUR_AUTO_VORAB);

                    // dann den neuen (NextBest) Bestand anbrechen.
                    // Das noch nichts commited wurde, übergeben wir hier den neuen APV direkt als BigDecimal mit.
                    naechsterBestand = MedBestandTools.anbrechen(em, naechsterBestand, MedBestandTools.berechneAPV(em, bestand));
                } else {
                    MedBuchungen buchung = em.merge(new MedBuchungen(bestand, entnahme.negate(), bhp));
//                    em.persist(buchung);
                    OPDE.debug("entnahmeVorrat/4: buchung: " + buchung);
                }

                if (wunschmenge.compareTo(entnahme) > 0) { // Sind wir hier fertig, oder müssen wir noch mehr ausbuchen.
                    entnahmeVorrat(em, vorrat, wunschmenge.subtract(entnahme), bhp);
                }
            } else {
                MedBuchungen buchung = new MedBuchungen(bestand, entnahme.negate(), bhp);
                em.persist(buchung);
                OPDE.debug("entnahmeVorrat/4: buchung: " + buchung);
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
            bestand = em.merge(new MedBestand(vorrat, darreichung, packung, text));
            bestand.setApv(MedBestandTools.getPassendesAPV(bestand));
            MedBuchungen buchung = em.merge(new MedBuchungen(bestand, menge));
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

    /**
     * Bricht von allen geschlossenen das nächste (im Sinne des Einbuchdatums) an. Funktioniert nur bei Vorräten, die z.Zt. keine
     * angebrochenen Bestände haben.
     *
     * @param vorrat
     * @return der neu angebrochene Bestand. null, wenns nicht geklappt hat.
     */
    public static MedBestand anbrechenNaechste(MedVorrat vorrat) throws Exception {
        MedBestand result = null;


        java.util.List<MedBestand> list = new ArrayList(vorrat.getBestaende());
        Collections.sort(list);

        for (MedBestand bestand : list) {
            if (bestand.getAus().equals(SYSConst.DATE_BIS_AUF_WEITERES) && bestand.getAnbruch().equals(SYSConst.DATE_BIS_AUF_WEITERES)) {
                BigDecimal apv = MedBestandTools.getPassendesAPV(bestand);
                bestand.setAnbruch(new Date());
                bestand.setApv(apv);
                result = bestand;
                break;
            }
        }

//
//        EntityManager em = OPDE.createEM();
//        try {
//            Query query = em.createQuery(" " +
//                    " SELECT b FROM MedBestand b " +
//                    " WHERE b.vorrat = :vorrat AND b.aus = :aus AND b.anbruch = :anbruch " +
//                    " ORDER BY b.ein, b.bestID "); // Geht davon aus, dass die PKs immer fortlaufend, automatisch vergeben werden.
//            query.setParameter("vorrat", vorrat);
//            query.setParameter("aus", SYSConst.DATE_BIS_AUF_WEITERES);
//            query.setParameter("anbruch", SYSConst.DATE_BIS_AUF_WEITERES);
//            query.setMaxResults(1);
//            result = (MedBestand) query.getSingleResult();
//            MedBestandTools.anbrechen(result);
//        } catch (NoResultException nre) {
//            OPDE.debug(nre);
//        } catch (Exception ex) {
//            OPDE.fatal(ex);
//        } finally {
//            em.close();
//        }

        return result;
    }


    /**
     * Schliesst einen Vorrat und alle zugehörigen, noch vorhandenen Bestände ab. Inklusive der notwendigen Abschlussbuchungen.
     *
     * @param vorrat
     */
    public static void abschliessen(MedVorrat vorrat) {
        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();

            // Alle Bestände abschliessen.
            for (MedBestand bestand : vorrat.getBestaende()) {
                if (!bestand.isAbgeschlossen()) {
                    MedBestandTools.abschliessen(em, bestand, "Abschluss des Bestandes bei Vorratsabschluss.", MedBuchungenTools.STATUS_KORREKTUR_AUTO_ABSCHLUSS_BEI_VORRATSABSCHLUSS);
                }
            }

            // Vorrat abschliessen
            vorrat.setBis(new Date());

            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            OPDE.fatal(ex);
        } finally {
            em.close();
        }
    }


}
