package entity.verordnungen;

import op.OPDE;
import op.tools.DBRetrieve;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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
     * Bucht eine Menge aus einem Vorrat aus, ggf. zugeh?rig zu einer BHP. ?bersteigt die Entnahme Menge den
     * Restbestband, dann wird entweder
     * <ul>
     * <li>wenn NextBest==0 &rarr; der Bestand trotzdem weiter gebucht. Bis ins Negative.</li>
     * <li>wenn NextBest > 0 &rarr; ein neuer Bestand wird angebrochen.</li>
     * </ul>
     * Ist keine Packung im Anbruch, dann passiert gar nichts. Der R¸ckgabewert ist dann false.
     *
     * @param dafid      pk der Darreichungsform
     * @param menge      gew¸nschte Entnahmemenge
     * @param bhpid      pk der BHP aufgrund dere dieser Buchungsvorgang erfolgt.
     * @param anweinheit true, dann wird in der anweinheit ausgebucht. false, in der packeinheit.
     * @return true, bei Erfolg; false, sonst
     */
    public static boolean entnahmeVorrat(long dafid, String bwkennung, double menge, boolean anweinheit, long bhpid) {
        if (dafid <= 0) {
            return true;
        }
        boolean result = true;
        long vorid = getVorrat2DAF(bwkennung, dafid);

        if (vorid > 0) {
            if (anweinheit) { // Umrechnung der Anwendungs Menge in die PackMenge.
                long bestid = getBestandImAnbruch(vorid);
                // Das ist der APV aus MPBestand.
                double apv = ((BigDecimal) op.tools.DBHandling.getSingleValue("MPBestand", "APV", "BestID", bestid)).doubleValue();
                menge = menge / apv;
            }
            result &= menge > 0 && entnahmeVorrat(vorid, menge, bhpid);
        }
        OPDE.debug(result ? "" : "entnahmeVorrat/5 fehlgeschlagen");

        return result;
    }

    private static boolean entnahmeVorrat(long vorid, double wunschmenge, long bhpid) {
        boolean result = true;
        long bestid = getBestandImAnbruch(vorid);

        if (bestid > 0 && wunschmenge > 0) {
            // ist schon eine Packung festgelegt, die angebrochen werden soll, sobald diese leer ist ?
            long nextBest = ((Long) DBRetrieve.getSingleValue("MPBestand", "NextBest", "BestID", bestid)).longValue();
            double entnahme; // wieviel in diesem Durchgang tats?chlich entnommen wird.
            double restsumme = getBestandSumme(bestid); // wieviel der angebrochene Bestand noch hergibt.
            entnahme = wunschmenge; // normalerweise wird immer das hergegeben, was auch gew¸nscht ist. Notfalls bis ins minus.
            if (nextBest > 0 && restsumme <= wunschmenge) { // sollte eine Packung aber schon als nachfolger bestimmt sein,
                entnahme = restsumme; // dann wird erst diese hier leergebraucht
            } // und dann der Rest aus der n?chsten Packung genommen.

            // Erstmal die Buchung f¸r diesen Durchgang
            HashMap hm = new HashMap();
            hm.put("BestID", bestid);
            hm.put("BHPID", bhpid);
            hm.put("Menge", entnahme * -1);
            hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());
            hm.put("PIT", "!NOW!");
            hm.put("Status", MedBuchungenTools.STATUS_AUSBUCHEN_NORMAL);
            result &= op.tools.DBHandling.insertRecord("MPBuchung", hm) > 0;

            if (nextBest > 0) { // Jetzt gibt es direkt noch den Wunsch das n?chste P?ckchen anzubrechen.

                if (restsumme <= wunschmenge) { // Es war mehr gew¸nscht, als die angebrochene Packung hergegeben hat.
                    // Dann m¸ssen wird erstmal den alten Bestand abschlie?en.
                    try {
                        // Es war mehr gew¸nscht, als die angebrochene Packung hergegeben hat.
                        // Dann m¸ssen wird erstmal den alten Bestand abschlie?en.
                        closeBestand(bestid, "Automatischer Abschluss bei leerer Packung", true, MedBuchungenTools.STATUS_KORREKTUR_AUTO_VORAB);

                        // dann den neuen Bestand anbrechen.
                        result &= anbrechen(nextBest);
                        Thread.sleep(1000); // Sonst ist die Anzeige im PnlBHP falsch.
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DBHandling.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                if (wunschmenge > entnahme) { // Sind wir hier fertig, oder m¸ssen wir noch mehr ausbuchen.
//                    try {
                    // Sind wir hier fertig, oder m¸ssen wir noch mehr ausbuchen.
                    //Thread.sleep(1000); // Ohne diese Warteschleife verschlucken sich die Datenbankanfragen.
                    // und es kommt zur Exception. :-( Schrecklich !
                    result &= entnahmeVorrat(vorid, wunschmenge - entnahme, bhpid);
//                    } catch (InterruptedException ex) {
//                        Logger.getLogger(DBHandling.class.getName()).log(Level.SEVERE, null, ex);
//                    }
                }
            }
            hm.clear();
        } else {
            result = false;
        }
        OPDE.debug(result ? "" : "entnahmeVorrat/3 fehlgeschlagen");
        return result;
    }

    /**
     * bucht ein Medikament in einen Vorrat ein. Aber nur dann, wenn es keinen anderen Vorrat gibt,
     * der mit seiner DafID schon passt.
     * <p/>
     * Falls diese DafID noch den Dummy "Startbestand" hat, wird dieser zuerst gelöscht.
     *
     * @return true, bei Erfolg. false, sonst.
     */
    public static MedBestand einbuchenVorrat(MedVorrat vorrat, MedPackung packung, Darreichung darreichung, String text, BigDecimal menge) throws Exception {
        MedBestand bestand = null;
        if (menge.compareTo(BigDecimal.ZERO) > 0) {

            BigDecimal apv;
            if (darreichung.getMedForm().getStatus() == MedFormenTools.APV_PER_BW) {
                apv = MPAPVTools.getAPV(vorrat.getBewohner(), darreichung);
            } else if (darreichung.getMedForm().getStatus() == MedFormenTools.APV_PER_DAF) {
                apv = MPAPVTools.getAPV(darreichung);
            } else { //APV1
                apv = BigDecimal.ONE;
            }

            bestand = new MedBestand(apv, vorrat, darreichung, packung, text);
            MedBuchungen buchung = new MedBuchungen(bestand, menge);
            bestand.getBuchungen().add(buchung);
        }
        return bestand;
    }


}
