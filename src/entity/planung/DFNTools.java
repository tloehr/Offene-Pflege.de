package entity.planung;

import entity.system.SYSPropsTools;
import entity.verordnungen.Verordnung;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import org.joda.time.*;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 19.07.12
 * Time: 16:23
 * To change this template use File | Settings | File Templates.
 */
public class DFNTools {
    public static final byte STATUS_OFFEN = 0;
    public static final byte STATUS_ERLEDIGT = 1;
    public static final byte STATUS_VERWEIGERT = 2;

    /**
     * Diese Methode erzeugt den Tagesplan für die Behandlungspflegen. Dabei werden alle aktiven Verordnungen geprüft, ermittelt ob sie am betreffenden Stichtag auch "dran" sind und dann
     * werden daraus Einträge in der BHP Tabelle erzeugt. Sie teilt sich die Arbeit mit der <code>erzeugen(EntityManager em, List<VerordnungPlanung> list, Date stichtag, Date zeit)</code> Methode
     *
     * @param em,    EntityManager Kontext
     * @param datum, das Datum für den die DFN erzeugt werden sollen. <code>null</code>, wenn das heutige Datum gewünscht ist.
     * @return Anzahl der erzeugten BHPs
     */
    public static int generate(EntityManager em, Date datum) throws Exception {

        String internalClassID = "nursingrecords.dfnimport";
        int numdfn = 0;


        DateMidnight stichtag = new DateMidnight();
        if (datum != null) {
            stichtag = new DateMidnight(datum);
        }


//        GregorianCalendar gcStichtag = SYSCalendar.toGC(stichtag);

        // Datum, an dem der letzte BHP Gesamtimport erfolgte. Darf nur einmal am Tag gemacht werden.
        DateMidnight lastdfn = stichtag;
        if (OPDE.getProps().containsKey("LASTDFNIMPORT")) {
            lastdfn = new DateMidnight(SYSCalendar.parseDate(OPDE.getProps().getProperty("LASTDFNIMPORT")));
        } else {
            lastdfn = lastdfn.minusDays(1);
        }

        if (Days.daysBetween(lastdfn, stichtag).getDays() != 1) {
            throw new IndexOutOfBoundsException(OPDE.lang.getString(internalClassID + ".exception.import"));
        }

        Query select = em.createQuery(" " +
                " SELECT mt FROM MassTermin mt " +
                " JOIN mt.planung p " +
                // nur die Planungen, die überhaupt gültig sind
                // das sind die mit Gültigkeit BAW oder Gültigkeit endet irgendwann in der Zukunft.
                // Das heisst, wenn eine Planungen heute endet, dann wird sie dennoch eingetragen.
                // Also alle, die bis EINSCHLIESSLICH heute gültig sind.
                " WHERE p.von <= :von AND p.bis >= :bis " +
                // und nur diejenigen, deren Referenzdatum nicht in der Zukunft liegt.
                " AND mt.lDatum <= :ldatum AND p.bewohner.adminonly <> 2 " +
                " ORDER BY mt.termID ");


//        SELECT * FROM MassTermin mt
//INNER JOIN Planung plan ON plan.PlanID = mt.PlanID
//INNER JOIN Bewohner bw ON bw.BWKennung = plan.BWKennung
//WHERE plan.von <= now() AND plan.bis >= now()
//AND mt.ldatum <= now() AND bw.adminonly <> 2
//ORDER BY mt.termid

        // Diese Aufstellung ergibt mindestens die heute gültigen Einträge.
        // Wahrscheinlich jedoch mehr als diese. Anhand des LDatums müssen
        // die wirklichen Treffer nachher genauer ermittelt werden.

        OPDE.important(em, "[DFNImport] " + OPDE.lang.getString("misc.msg.writingto") + ": " + OPDE.getUrl());

        select.setParameter("von", stichtag.toDate());
        select.setParameter("bis", stichtag.plusDays(1).toDateTime().minusMinutes(1).toDate());
        select.setParameter("ldatum", stichtag.toDate());

        List<MassTermin> list = select.getResultList();

        numdfn = generate(em, list, stichtag, true);

        Query forceQuery = em.createQuery(" UPDATE DFN d "
                + " SET d.soll = :now "
                + " WHERE d.erforderlich = TRUE AND d.status = :status AND d.soll < :now1 "
                + " AND d.planung.von < :now2 AND d.planung.bis > :now3 ");
        forceQuery.setParameter("now", stichtag.toDate());
        forceQuery.setParameter("now1", stichtag.toDate());
        forceQuery.setParameter("now2", stichtag.toDate());
        forceQuery.setParameter("now3", stichtag.toDate());
        forceQuery.setParameter("status", DFNTools.STATUS_OFFEN);

//        String forcedSQL = " UPDATE DFN d "
//                + " INNER JOIN MassTermin t ON d.TermID = t.TermID "
//                + " INNER JOIN Planung p ON p.PlanID = t.PlanID "
//                + " SET d.Soll=now() "
//                + " WHERE d.Erforderlich > 0 AND d.Status = 0 AND DATE(d.Soll) < Date(now()) "
//                + " AND p.von < now() AND p.bis > now()";
        // Die nicht beachteten zu erzwingenden müssen noch auf das heutige Datum umgetragen werden.
        // Das erfolgt unabhängig von dem eingegebenen Stichtag.
        // Nur bei uneingeschränkten Imports.
        int affectedOldDFNs = forceQuery.executeUpdate();
        OPDE.debug("Notwendige Massnahmen werden übertragen...");


        OPDE.important(em, "[DFNImport] Durchgeführt. Stichtag: " + DateFormat.getDateInstance().format(stichtag.toDate()) + " Anzaghl erzeugter DFNs: " + numdfn);
        OPDE.important(em, affectedOldDFNs + " notwendige Massnahmen wurden übertragen.");

        SYSPropsTools.storeProp(em, "LASTDFNIMPORT", DateFormat.getDateInstance().format(stichtag.toDate()));

        return numdfn;
    }

    /**
     * Hiermit werden alle BHP Einträge erzeugt, die sich aus den Verordnungen in der zugehörigen Liste ergeben. Die Liste wird aber vorher
     * noch darauf geprüft, ob sie auch wirklich an dem besagten Stichtag passt. Dabei gilt:
     * <ol>
     * <li>Alles was taeglich angeordnet ist (jeden Tag oder jeden soundsovielten Tag)</li>
     * <li>Alles was woechentlich ist und die Spalte (Attribut) mit dem aktuellen Wochentagsnamen größer null ist.</li>
     * <li>Monatliche Einträge. Aber nur dann, wenn
     * <ol>
     * <li>es der <i>n</i>.te Tag im Monat ist <br/><b>oder</b></li>
     * <li>oder der <i>n</i>.te Wochentag (z.B. Freitag) im Monat ist</li>
     * </ol>
     * </li>
     * </ol>
     * <p/>
     * Diese Methode kann von verschiednenen Seiten aufgerufen werden. Zum einen von der "anderen" erzeugen Methode, die einen vollständigen Tagesplan für
     * alle BWs erzeugt oder von dem Verordnungs Editor, der seinerseits nur eine einzige Verordnung nachtragen möchte. Auf jeden Fall kann die Liste <code>list</code>
     * auch Einträge enthalten, die unpassend sind. Sie dient nur der Vorauswahl und wird innerhalb dieser Methode dann genau geprüft. Sie "pickt" sich also
     * nur die passenden Elemente aus dieser Liste heraus.
     *
     * @param em       EntityManager Kontext
     * @param list     Liste der VerordnungPlanungen, die ggf. einzutragen sind.
     * @param stichtag gibt an, für welches Datum die Einträge erzeugt werden. In der Regel ist das immer der aktuelle Tag.
     * @param wholeday true, dann wird für den ganzen Tag erzeugt. false, dann ab der aktuellen Zeit.
     * @return die Anzahl der erzeugten BHPs.
     */
    public static int generate(EntityManager em, List<MassTermin> list, DateMidnight stichtag, boolean wholeday) {
//        GregorianCalendar gcStichtag = SYSCalendar.toGC(stichtag);
        int maxrows = list.size();
        int numdfn = 0;

        long now = System.currentTimeMillis();
        byte aktuelleZeit = SYSCalendar.ermittleZeit(now);

        int row = 0;

        OPDE.debug("MaxRows: " + maxrows);

        for (MassTermin termin : list) {

            termin = em.merge(termin);
            em.lock(termin, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            em.lock(em.merge(termin.getPlanung()), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            em.lock(termin.getPlanung().getBewohner(), LockModeType.OPTIMISTIC);

            if (!SYSCalendar.isInFuture(termin.getLDatum()) && (termin.isTaeglich() || termin.isPassenderWochentag(stichtag.toDate()) || termin.isPassenderTagImMonat(stichtag.toDate()))) {

                row++;

                OPDE.debug("Fortschritt Vorgang: " + ((float) row / maxrows) * 100 + "%");
                OPDE.debug("==========================================");
                OPDE.debug("MassTermin: " + termin.getTermID());
                OPDE.debug("BWKennung: " + termin.getPlanung().getBewohner().getBWKennung());
                OPDE.debug("PlanID: " + termin.getPlanung().getPlanID());


                boolean treffer = false;
                DateMidnight ldatum = new DateMidnight(termin.getLDatum());

                OPDE.debug("LDatum: " + DateFormat.getDateTimeInstance().format(ldatum.toDate()));
                OPDE.debug("Stichtag: " + DateFormat.getDateTimeInstance().format(stichtag.toDate()));

                // Genaue Ermittlung der Treffer
                // =============================
                if (termin.isTaeglich()) {
                    OPDE.debug("Eine tägliche Planung");
                    // Dann wird das LDatum solange um die gewünschte Tagesanzahl erhöht, bis
                    // der stichtag getroffen wurde oder überschritten ist.
                    while (Days.daysBetween(ldatum, stichtag).getDays() > 0) {
                        OPDE.debug("ldatum liegt vor dem stichtag. Addiere tage: " + termin.getTaeglich());
                        ldatum = ldatum.plusDays(termin.getTaeglich());
                    }
                    // Mich interssiert nur der Treffer, also die Punktlandung auf dem Stichtag
                    treffer = Days.daysBetween(ldatum, stichtag).getDays() == 0;
                } else if (termin.isWoechentlich()) {
                    OPDE.debug("Eine wöchentliche Planung");
                    while (Weeks.weeksBetween(ldatum, stichtag).getWeeks() > 0) {
                        OPDE.debug("ldatum liegt vor dem stichtag. Addiere Wochen: " + termin.getWoechentlich());
                        ldatum = ldatum.plusWeeks(termin.getWoechentlich());
                    }
                    // Ein Treffer ist es dann, wenn das Referenzdatum gleich dem Stichtag ist ODER es zumindest in der selben Kalenderwoche liegt.
                    // Da bei der Vorauswahl durch die Datenbank nur passende Wochentage überhaupt zugelassen wurden, muss das somit der richtige sein.
                    treffer = Weeks.weeksBetween(ldatum, stichtag).getWeeks() == 0;
                } else if (termin.isMonatlich()) {
                    OPDE.debug("Eine monatliche Planung");
                    while (Months.monthsBetween(ldatum, stichtag).getMonths() > 0) {
                        OPDE.debug("ldatum liegt vor dem stichtag. Addiere Monate: " + termin.getMonatlich());
                        ldatum = ldatum.plusMonths(termin.getMonatlich());
                    }
                    // Ein Treffer ist es dann, wenn das Referenzdatum gleich dem Stichtag ist ODER es zumindest im selben Monat desselben Jahres liegt.
                    // Da bei der Vorauswahl durch die Datenbank nur passende Wochentage oder Tage im Monat überhaupt zugelassen wurden, muss das somit der richtige sein.
                    treffer = Months.monthsBetween(ldatum, stichtag).getMonths() == 0;
                }

                OPDE.debug("LDatum jetzt: " + DateFormat.getDateTimeInstance().format(ldatum.toDate()));
                OPDE.debug("Treffer ? : " + Boolean.toString(treffer));

                // Es wird immer erst eine Schicht später eingetragen. Damit man nicht mit bereits
                // abgelaufenen Zeitpunkten arbeitet.
                // Bei ganzerTag=true werden all diese booleans zu true und damit neutralisiert.
                boolean erstAbFM = wholeday || aktuelleZeit == SYSConst.FM;
                boolean erstAbMO = wholeday || erstAbFM || aktuelleZeit == SYSConst.MO;
                boolean erstAbMI = wholeday || erstAbMO || aktuelleZeit == SYSConst.MI;
                boolean erstAbNM = wholeday || erstAbMI || aktuelleZeit == SYSConst.NM;
                boolean erstAbAB = wholeday || erstAbNM || aktuelleZeit == SYSConst.AB;
                boolean erstAbNA = wholeday || erstAbAB || aktuelleZeit == SYSConst.NA;
                boolean uhrzeitOK = wholeday || (termin.getUhrzeit() != null && DateTimeComparator.getTimeOnlyInstance().compare(termin.getUhrzeit(), new DateTime(now)) > 0);


                if (treffer) {
                    if (erstAbFM && termin.getNachtMo() > 0) {
                        OPDE.debug("SYSConst.FM, " + termin.getNachtMo());
                        for (int dfncount = 1; dfncount <= termin.getNachtMo(); dfncount++) {
                            em.merge(new DFN(termin, stichtag.toDate(), SYSConst.FM));
                            numdfn++;
                        }
                    }
                    if (erstAbMO && termin.getMorgens() > 0) {
                        OPDE.debug("SYSConst.MO, " + termin.getMorgens());
                        for (int dfncount = 1; dfncount <= termin.getMorgens(); dfncount++) {
                            em.merge(new DFN(termin, stichtag.toDate(), SYSConst.MO));
                            numdfn++;
                        }
                    }
                    if (erstAbMI && termin.getMittags() > 0) {
                        OPDE.debug("SYSConst.MI, " + termin.getMittags());
                        for (int dfncount = 1; dfncount <= termin.getMittags(); dfncount++) {
                            em.merge(new DFN(termin, stichtag.toDate(), SYSConst.MI));
                            numdfn++;
                        }
                    }
                    if (erstAbNM && termin.getNachmittags() > 0) {
                        OPDE.debug("SYSConst.NM, " + termin.getNachmittags());
                        for (int dfncount = 1; dfncount <= termin.getNachmittags(); dfncount++) {
                            em.merge(new DFN(termin, stichtag.toDate(), SYSConst.NM));
                            numdfn++;
                        }
                    }
                    if (erstAbAB && termin.getAbends() > 0) {
                        OPDE.debug("SYSConst.AB, " + termin.getAbends());
                        for (int dfncount = 1; dfncount <= termin.getAbends(); dfncount++) {
                            em.merge(new DFN(termin, stichtag.toDate(), SYSConst.AB));
                            numdfn++;
                        }
                    }
                    if (erstAbNA && termin.getNachtAb() > 0) {
                        OPDE.debug("SYSConst.NA, " + termin.getNachtAb());
                        for (int dfncount = 1; dfncount <= termin.getNachtAb(); dfncount++) {
                            em.merge(new DFN(termin, stichtag.toDate(), SYSConst.NA));
                            numdfn++;
                        }
                    }
                    if (uhrzeitOK && termin.getUhrzeit() != null) {
                        Date neuerStichtag = SYSCalendar.addTime2Date(stichtag.toDate(), termin.getUhrzeit());
                        OPDE.debug("SYSConst.UZ, " + termin.getUhrzeit() + ", " + DateFormat.getDateTimeInstance().format(neuerStichtag));
                        for (int dfncount = 1; dfncount <= termin.getUhrzeitAnzahl(); dfncount++) {
                            em.merge(new DFN(termin, neuerStichtag, SYSConst.UZ));
                            numdfn++;
                        }
                    }

                    // Nun noch das LDatum in der Tabelle MassTermin neu setzen.
                    termin.setLDatum(stichtag.toDate());
                }
            } else {
                OPDE.debug("///////////////////////////////////////////////////////////");
                OPDE.debug("Folgender MassTermin wurde nicht angenommen: " + termin);
            }
        }
        OPDE.debug("Erzeugte DFNs: " + numdfn);
        return numdfn;
    }


    public static long getNumDFNs(Planung planung) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT COUNT(dfn) FROM DFN dfn WHERE dfn.planung = :planung AND dfn.status <> :status");
        query.setParameter("planung", planung);
        query.setParameter("status", STATUS_OFFEN);
        long num = (Long) query.getSingleResult();
        em.close();
        return num;
    }
}
