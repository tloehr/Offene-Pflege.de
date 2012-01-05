package entity.verordnungen;

import op.OPDE;
import op.tools.SYSConst;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

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
     * Ist <b>keine</b> Packung im Anbruch, dann wird eine Exception geworfen. Das kann aber eingentlich nicht passieren. Es sei denn jemand hat von Hand
     * an den Datenbank rumgespielt.
     *
     * @param em         der EntityManager der verwendet wird
     * @param menge      die gewünschte Entnahmemenge
     * @param anweinheit true, dann wird in der Anwedungseinheit ausgebucht. false, in der Packungseinheit
     * @param bhp        BHP aufgrund dere dieser Buchungsvorgang erfolgt.
     */
    public static void entnahmeVorrat(EntityManager em, MedVorrat vorrat, BigDecimal menge, boolean anweinheit, BHP bhp) throws Exception {

        OPDE.debug("entnahmeVorrat/6: vorrat: " + vorrat);

        if (vorrat == null) {
            throw new Exception("Keine Packung im Anbruch");
        }

        if (anweinheit) { // Umrechnung der Anwendungs Menge in die Packungs-Menge.
            MedBestand bestand = MedVorratTools.getImAnbruch(vorrat);
            BigDecimal apv = bestand.getApv();

            if (apv.equals(BigDecimal.ZERO)) {
                apv = BigDecimal.ONE;
            }
            menge = menge.divide(apv, 4, BigDecimal.ROUND_UP);
        }

        OPDE.debug("entnahmeVorrat/6: menge: " + menge);

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


    protected static void entnahmeVorrat(EntityManager em, MedVorrat vorrat, BigDecimal wunschmenge, BHP bhp) throws Exception {
        MedBestand bestand = MedVorratTools.getImAnbruch(vorrat);

        OPDE.debug("entnahmeVorrat/4: bestand: " + bestand);

        if (bestand != null && wunschmenge.compareTo(BigDecimal.ZERO) > 0) {

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
            OPDE.debug("entnahmeVorrat/4: buchung1: " + buchung);

            if (bestand.hasNextBestand()) { // Jetzt gibt es direkt noch den Wunsch das nächste Päckchen anzubrechen.
                if (restsumme.compareTo(wunschmenge) <= 0) {
                    // Es war mehr gewünscht, als die angebrochene Packung hergegeben hat.
                    // Dann müssen wird erstmal den alten Bestand abschließen.

                    APV apv = MedBestandTools.abschliessen(em, bestand, "Automatischer Abschluss bei leerer Packung", MedBestandTools.apvNeuberechnung, MedBuchungenTools.STATUS_KORREKTUR_AUTO_VORAB);
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


    /**
     * Sucht aus den den Beständen des Vorrats den angebrochenen heraus.
     *
     * @param vorrat
     * @return der angebrochene Bestand. null, wenn es keinen gab.
     */
    public static MedBestand getImAnbruch(MedVorrat vorrat) {
        MedBestand bestand = null;
        if (vorrat.getBestaende() != null) {
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


    /**
     * Schliesst einen Vorrat und alle zugehörigen, noch vorhandenen Bestände ab. Inklusive der notwendigen Abschlussbuchungen.
     * @param vorrat
     */
    public static void abschliessen(MedVorrat vorrat) {
        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();

            // Alle Bestände abschliessen.
            for (MedBestand bestand : vorrat.getBestaende()) {
                if (!bestand.isAbgeschlossen()) {
                    MedBestandTools.abschliessen(em, bestand, "Abschluss des Bestandes bei Vorratsabschluss.", !MedBestandTools.apvNeuberechnung, MedBuchungenTools.STATUS_KORREKTUR_AUTO_ABSCHLUSS_BEI_VORRATSABSCHLUSS);
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
