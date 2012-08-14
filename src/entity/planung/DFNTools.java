package entity.planung;

import entity.info.Resident;
import entity.info.BWInfoTools;
import entity.system.SYSPropsTools;
import op.OPDE;
import op.care.dfn.PnlDFN;
import op.tools.*;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.swing.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
    public static final byte STATE_OPEN = 0;
    public static final byte STATE_DONE = 1;
    public static final byte STATE_REFUSED = 2;

    public static final byte SHIFT_ALL = -1;
    public static final byte SHIFT_VERY_EARLY = 0;
    public static final byte SHIFT_EARLY = 1;
    public static final byte SHIFT_LATE = 2;
    public static final byte SHIFT_VERY_LATE = 3;

    public static final String[] TIMEIDTEXTLONG = new String[]{"misc.msg.Time.long", "misc.msg.earlyinthemorning.long", "misc.msg.morning.long", "misc.msg.noon.long", "misc.msg.afternoon.long", "misc.msg.evening.long", "misc.msg.lateatnight.long"};
    public static final String[] TIMEIDTEXTSHORT = new String[]{"misc.msg.Time.short", "misc.msg.earlyinthemorning.short", "misc.msg.morning.short", "misc.msg.noon.short", "misc.msg.afternoon.short", "misc.msg.evening.short", "misc.msg.lateatnight.short"};

    public static final byte BYTE_TIMEOFDAY = 0;
    public static final byte BYTE_EARLY_IN_THE_MORNING = 1;
    public static final byte BYTE_MORNING = 2;
    public static final byte BYTE_NOON = 3;
    public static final byte BYTE_AFTERNOON = 4;
    public static final byte BYTE_EVENING = 5;
    public static final byte BYTE_LATE_AT_NIGHT = 6;

    public static final String STRING_TIMEOFDAY = "UZ";
    public static final String STRING_EARLY_IN_THE_MORNING = "FM";
    public static final String STRING_MORNING = "MO";
    public static final String STRING_NOON = "MI";
    public static final String STRING_AFTERNOON = "NM";
    public static final String STRING_EVENING = "AB";
    public static final String STRING_LATE_AT_NIGHT = "NA";

    /**
     * Diese Methode erzeugt den Tagesplan für die Behandlungspflegen. Dabei werden alle aktiven Verordnungen geprüft, ermittelt ob sie am betreffenden Stichtag auch "dran" sind und dann
     * werden daraus Einträge in der BHP Tabelle erzeugt. Sie teilt sich die Arbeit mit der <code>erzeugen(EntityManager em, List<VerordnungPlanung> list, Date stichtag, Date zeit)</code> Methode
     *
     * @param em, EntityManager Kontext
     * @return Anzahl der erzeugten BHPs
     */
    public static int generate(EntityManager em) throws Exception {
        String internalClassID = "nursingrecords.dfnimport";
        int numdfn = 0;

        DateMidnight lastdfn = new DateMidnight().minusDays(1);
        if (OPDE.getProps().containsKey("LASTDFNIMPORT")) {
            lastdfn = new DateMidnight(DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(OPDE.getProps().getProperty("LASTDFNIMPORT")));
        }

        if (lastdfn.isAfterNow()) {
            throw new IndexOutOfBoundsException("The date of the last import is somewhere in the future. Can't be true.");
        }

        DateMidnight targetdate = null;

        // If (for technical reasons) the lastdfn lies in the past (more than the usual 1 day),
        // then the generation is interated until the current day.
        for (int days = 1; days <= Days.daysBetween(lastdfn.plusDays(1), new DateMidnight()).getDays() + 1; days++) {

            targetdate = lastdfn.plusDays(days);

            Query select = em.createQuery(" " +
                    " SELECT mt FROM InterventionSchedule mt " +
                    " JOIN mt.nursingProcess p " +
                    // nur die Planungen, die überhaupt gültig sind
                    // das sind die mit Gültigkeit BAW oder Gültigkeit endet irgendwann in der Zukunft.
                    // Das heisst, wenn eine Planungen heute endet, dann wird sie dennoch eingetragen.
                    // Also alle, die bis EINSCHLIESSLICH heute gültig sind.
                    " WHERE p.von <= :von AND p.bis >= :bis " +
                    // und nur diejenigen, deren Referenzdatum nicht in der Zukunft liegt.
                    " AND mt.lDatum <= :ldatum AND p.bewohner.adminonly <> 2 " +
                    " ORDER BY mt.termID ");

            // Diese Aufstellung ergibt mindestens die heute gültigen Einträge.
            // Wahrscheinlich jedoch mehr als diese. Anhand des LDatums müssen
            // die wirklichen Treffer nachher genauer ermittelt werden.

            OPDE.important(em, "[DFNImport] " + OPDE.lang.getString("misc.msg.writingto") + ": " + OPDE.getUrl());

            select.setParameter("von", targetdate.toDate());
            select.setParameter("bis", targetdate.plusDays(1).toDateTime().minusMinutes(1).toDate());
            select.setParameter("ldatum", targetdate.toDate());

            List<InterventionSchedule> list = select.getResultList();

            numdfn += generate(em, list, targetdate, true);

            Query forceQuery = em.createQuery(" UPDATE DFN d "
                    + " SET d.soll = :now "
                    + " WHERE d.floating = TRUE AND d.status = :status AND d.soll < :now1 "
                    + " AND d.nursingProcess.von < :now2 AND d.nursingProcess.bis > :now3 ");
            forceQuery.setParameter("now", targetdate.toDate());
            forceQuery.setParameter("now1", targetdate.toDate());
            forceQuery.setParameter("now2", targetdate.toDate());
            forceQuery.setParameter("now3", targetdate.toDate());
            forceQuery.setParameter("status", DFNTools.STATE_OPEN);

            // Die nicht beachteten zu erzwingenden müssen noch auf das heutige Datum umgetragen werden.
            // Das erfolgt unabhängig von dem eingegebenen Stichtag.
            // Nur bei uneingeschränkten Imports.
            int affectedOldDFNs = forceQuery.executeUpdate();
            OPDE.debug("Notwendige Massnahmen werden übertragen...");


            OPDE.important(em, "[DFNImport] Durchgeführt. Stichtag: " + DateFormat.getDateInstance().format(targetdate.toDate()) + " Anzaghl erzeugter DFNs: " + numdfn);
            OPDE.important(em, affectedOldDFNs + " notwendige Massnahmen wurden übertragen.");
        }

        SYSPropsTools.storeProp(em, "LASTDFNIMPORT", DateTimeFormat.forPattern("yyyy-MM-dd").print(targetdate));

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
     * @param em         EntityManager Kontext
     * @param list       Liste der VerordnungPlanungen, die ggf. einzutragen sind.
     * @param targetdate gibt an, für welches Datum die Einträge erzeugt werden. In der Regel ist das immer der aktuelle Tag.
     * @param wholeday   true, dann wird für den ganzen Tag erzeugt. false, dann ab der aktuellen Zeit.
     * @return die Anzahl der erzeugten BHPs.
     */
    public static int generate(EntityManager em, List<InterventionSchedule> list, DateMidnight targetdate, boolean wholeday) {
//        GregorianCalendar gcStichtag = SYSCalendar.toGC(stichtag);
        int maxrows = list.size();
        int numdfn = 0;

        long now = System.currentTimeMillis();
        byte aktuelleZeit = SYSCalendar.ermittleZeit(now);

        int row = 0;

        OPDE.debug("MaxRows: " + maxrows);

        for (InterventionSchedule termin : list) {

            termin = em.merge(termin);
            em.lock(termin, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            em.lock(em.merge(termin.getNursingProcess()), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            em.lock(termin.getNursingProcess().getBewohner(), LockModeType.OPTIMISTIC);

            if (!SYSCalendar.isInFuture(termin.getLDatum()) && (termin.isTaeglich() || termin.isPassenderWochentag(targetdate.toDate()) || termin.isPassenderTagImMonat(targetdate.toDate()))) {

                row++;

                SYSTools.printProgBar(row / maxrows * 100);

                OPDE.debug("Fortschritt Vorgang: " + ((float) row / maxrows) * 100 + "%");
                OPDE.debug("==========================================");
                OPDE.debug("MassTermin: " + termin.getTermID());
                OPDE.debug("BWKennung: " + termin.getNursingProcess().getBewohner().getBWKennung());
                OPDE.debug("PlanID: " + termin.getNursingProcess().getPlanID());

                boolean treffer = false;
                DateMidnight ldatum = new DateMidnight(termin.getLDatum());

                OPDE.debug("LDatum: " + DateFormat.getDateTimeInstance().format(ldatum.toDate()));
                OPDE.debug("Stichtag: " + DateFormat.getDateTimeInstance().format(targetdate.toDate()));

                // Genaue Ermittlung der Treffer
                // =============================
                if (termin.isTaeglich()) {
                    OPDE.debug("Eine tägliche Planung");
                    // Dann wird das LDatum solange um die gewünschte Tagesanzahl erhöht, bis
                    // der stichtag getroffen wurde oder überschritten ist.
                    while (Days.daysBetween(ldatum, targetdate).getDays() > 0) {
                        OPDE.debug("ldatum liegt vor dem stichtag. Addiere tage: " + termin.getTaeglich());
                        ldatum = ldatum.plusDays(termin.getTaeglich());
                    }
                    // Mich interssiert nur der Treffer, also die Punktlandung auf dem Stichtag
                    treffer = Days.daysBetween(ldatum, targetdate).getDays() == 0;
                } else if (termin.isWoechentlich()) {
                    OPDE.debug("Eine wöchentliche Planung");
                    while (Weeks.weeksBetween(ldatum, targetdate).getWeeks() > 0) {
                        OPDE.debug("ldatum liegt vor dem stichtag. Addiere Wochen: " + termin.getWoechentlich());
                        ldatum = ldatum.plusWeeks(termin.getWoechentlich());
                    }
                    // Ein Treffer ist es dann, wenn das Referenzdatum gleich dem Stichtag ist ODER es zumindest in der selben Kalenderwoche liegt.
                    // Da bei der Vorauswahl durch die Datenbank nur passende Wochentage überhaupt zugelassen wurden, muss das somit der richtige sein.
                    treffer = Weeks.weeksBetween(ldatum, targetdate).getWeeks() == 0;
                } else if (termin.isMonatlich()) {
                    OPDE.debug("Eine monatliche Planung");
                    while (Months.monthsBetween(ldatum, targetdate).getMonths() > 0) {
                        OPDE.debug("ldatum liegt vor dem stichtag. Addiere Monate: " + termin.getMonatlich());
                        ldatum = ldatum.plusMonths(termin.getMonatlich());
                    }
                    // Ein Treffer ist es dann, wenn das Referenzdatum gleich dem Stichtag ist ODER es zumindest im selben Monat desselben Jahres liegt.
                    // Da bei der Vorauswahl durch die Datenbank nur passende Wochentage oder Tage im Monat überhaupt zugelassen wurden, muss das somit der richtige sein.
                    treffer = Months.monthsBetween(ldatum, targetdate).getMonths() == 0;
                }

                OPDE.debug("LDatum jetzt: " + DateFormat.getDateTimeInstance().format(ldatum.toDate()));
                OPDE.debug("Treffer ? : " + Boolean.toString(treffer));

                // Es wird immer erst eine Schicht später eingetragen. Damit man nicht mit bereits
                // abgelaufenen Zeitpunkten arbeitet.
                // Bei ganzerTag=true werden all diese booleans zu true und damit neutralisiert.
                boolean erstAbFM = wholeday || aktuelleZeit == BYTE_EARLY_IN_THE_MORNING;
                boolean erstAbMO = wholeday || erstAbFM || aktuelleZeit == BYTE_MORNING;
                boolean erstAbMI = wholeday || erstAbMO || aktuelleZeit == BYTE_NOON;
                boolean erstAbNM = wholeday || erstAbMI || aktuelleZeit == BYTE_AFTERNOON;
                boolean erstAbAB = wholeday || erstAbNM || aktuelleZeit == BYTE_EVENING;
                boolean erstAbNA = wholeday || erstAbAB || aktuelleZeit == BYTE_LATE_AT_NIGHT;
                boolean uhrzeitOK = wholeday || (termin.getUhrzeit() != null && DateTimeComparator.getTimeOnlyInstance().compare(termin.getUhrzeit(), new DateTime(now)) > 0);


                if (treffer) {
                    if (erstAbFM && termin.getNachtMo() > 0) {
                        OPDE.debug("SYSConst.FM, " + termin.getNachtMo());
                        for (int dfncount = 1; dfncount <= termin.getNachtMo(); dfncount++) {
                            em.merge(new DFN(termin, targetdate.toDate(), BYTE_EARLY_IN_THE_MORNING));
                            numdfn++;
                        }
                    }
                    if (erstAbMO && termin.getMorgens() > 0) {
                        OPDE.debug("SYSConst.MO, " + termin.getMorgens());
                        for (int dfncount = 1; dfncount <= termin.getMorgens(); dfncount++) {
                            em.merge(new DFN(termin, targetdate.toDate(), BYTE_MORNING));
                            numdfn++;
                        }
                    }
                    if (erstAbMI && termin.getMittags() > 0) {
                        OPDE.debug("SYSConst.MI, " + termin.getMittags());
                        for (int dfncount = 1; dfncount <= termin.getMittags(); dfncount++) {
                            em.merge(new DFN(termin, targetdate.toDate(), BYTE_NOON));
                            numdfn++;
                        }
                    }
                    if (erstAbNM && termin.getNachmittags() > 0) {
                        OPDE.debug("SYSConst.NM, " + termin.getNachmittags());
                        for (int dfncount = 1; dfncount <= termin.getNachmittags(); dfncount++) {
                            em.merge(new DFN(termin, targetdate.toDate(), BYTE_AFTERNOON));
                            numdfn++;
                        }
                    }
                    if (erstAbAB && termin.getAbends() > 0) {
                        OPDE.debug("SYSConst.AB, " + termin.getAbends());
                        for (int dfncount = 1; dfncount <= termin.getAbends(); dfncount++) {
                            em.merge(new DFN(termin, targetdate.toDate(), BYTE_EVENING));
                            numdfn++;
                        }
                    }
                    if (erstAbNA && termin.getNachtAb() > 0) {
                        OPDE.debug("SYSConst.NA, " + termin.getNachtAb());
                        for (int dfncount = 1; dfncount <= termin.getNachtAb(); dfncount++) {
                            em.merge(new DFN(termin, targetdate.toDate(), BYTE_LATE_AT_NIGHT));
                            numdfn++;
                        }
                    }
                    if (uhrzeitOK && termin.getUhrzeit() != null) {

                        // This adds a Time Value to a given Date
                        DateTime timeofday = new DateTime(termin.getUhrzeit());
                        Period period = new Period(timeofday.getHourOfDay(), timeofday.getMinuteOfHour(), timeofday.getSecondOfMinute(), timeofday.getMillisOfSecond());
                        Date newTargetdate = targetdate.toDateTime().plus(period).toDate();
//                        Date neuerStichtag = SYSCalendar.addTime2Date(stichtag.toDate(), termin.getUhrzeit());
                        OPDE.debug("SYSConst.UZ, " + termin.getUhrzeit() + ", " + DateFormat.getDateTimeInstance().format(newTargetdate));
                        for (int dfncount = 1; dfncount <= termin.getUhrzeitAnzahl(); dfncount++) {
                            em.merge(new DFN(termin, newTargetdate, SYSConst.UZ));
                            numdfn++;
                        }
                    }

                    // Nun noch das LDatum in der Tabelle MassTermin neu setzen.
                    termin.setLDatum(targetdate.toDate());
                }
            } else {
                OPDE.debug("///////////////////////////////////////////////////////////");
                OPDE.debug("Folgender MassTermin wurde nicht angenommen: " + termin);
            }
        }
        OPDE.debug("Erzeugte DFNs: " + numdfn);
        return numdfn;
    }


    public static long getNumDFNs(NursingProcess planung) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT COUNT(dfn) FROM DFN dfn WHERE dfn.nursingProcess = :planung AND dfn.status <> :status");
        query.setParameter("planung", planung);
        query.setParameter("status", STATE_OPEN);
        long num = (Long) query.getSingleResult();
        em.close();
        return num;
    }


    public static ArrayList<NursingProcess> getInvolvedNPs(byte shift, Resident resident, Date date) {
        EntityManager em = OPDE.createEM();
        ArrayList<NursingProcess> listNP = null;

        try {

            String jpql = " SELECT DISTINCT dfn.nursingProcess " +
                    " FROM DFN dfn " +
                    " WHERE dfn.bewohner = :bewohner AND dfn.nursingProcess IS NOT NULL AND dfn.soll >= :von AND dfn.soll <= :bis ";

            if (shift != SHIFT_ALL) {
                jpql = jpql + " AND ((dfn.sZeit >= :timeid1 AND dfn.sZeit <= :timeid2) OR " +
                        " (dfn.sZeit = 0 AND dfn.soll >= :time1 AND dfn.soll <= :time2)) ";

            }

            jpql = jpql + " ORDER BY dfn.nursingProcess.kategorie.bezeichnung, dfn.nursingProcess.stichwort, dfn.nursingProcess.planID ";

            Query query = em.createQuery(jpql);

            query.setParameter("bewohner", resident);
            query.setParameter("von", new DateTime(date).toDateMidnight().toDate());
            query.setParameter("bis", new DateTime(date).toDateMidnight().plusDays(1).toDateTime().minusSeconds(1).toDate());

            if (shift != SHIFT_ALL) {
                Pair<Date, Date> times = SYSCalendar.getTimeOfDay4Shift(shift);
                Pair<Byte, Byte> timeids = SYSCalendar.getTimeIDs4Shift(shift);
                query.setParameter("timeid1", timeids.getFirst());
                query.setParameter("timeid2", timeids.getSecond());
                query.setParameter("time1", times.getFirst());
                query.setParameter("time2", times.getSecond());
            }

            listNP = new ArrayList<NursingProcess>(query.getResultList());

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return listNP;
    }

    public static ArrayList<DFN> getDFNs(byte shift, NursingProcess np, Date date) {
        EntityManager em = OPDE.createEM();
        ArrayList<DFN> listDFN = null;

        try {

            String jpql = " SELECT dfn " +
                    " FROM DFN dfn " +
                    " WHERE dfn.nursingProcess = :np " +
                    " AND dfn.soll >= :von AND dfn.soll <= :bis ";

            if (shift != SHIFT_ALL) {
                jpql = jpql + " AND ((dfn.sZeit >= :timeid1 AND dfn.sZeit <= :timeid2) OR " +
                        " (dfn.sZeit = 0 AND dfn.soll >= :time1 AND dfn.soll <= :time2)) ";

            }

//            jpql = jpql + " ORDER BY dfn.intervention.bezeichnung ";

            Query query = em.createQuery(jpql);

            query.setParameter("np", np);
            query.setParameter("von", new DateTime(date).toDateMidnight().toDate());
            query.setParameter("bis", new DateTime(date).toDateMidnight().plusDays(1).toDateTime().minusSeconds(1).toDate());

            if (shift != SHIFT_ALL) {
                Pair<Date, Date> times = SYSCalendar.getTimeOfDay4Shift(shift);
                Pair<Byte, Byte> timeids = SYSCalendar.getTimeIDs4Shift(shift);
                query.setParameter("timeid1", timeids.getFirst());
                query.setParameter("timeid2", timeids.getSecond());
                query.setParameter("time1", times.getFirst());
                query.setParameter("time2", times.getSecond());
            }

            listDFN = new ArrayList<DFN>(query.getResultList());
            Collections.sort(listDFN);
        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return listDFN;
    }

    public static ArrayList<DFN> getDFNs(byte shift, Resident resident, Date date) {
        EntityManager em = OPDE.createEM();
        ArrayList<DFN> listDFN = null;

        try {

            String jpql = " SELECT dfn " +
                    " FROM DFN dfn " +
                    " WHERE dfn.bewohner = :resident AND dfn.nursingProcess IS NULL " +
                    " AND dfn.soll >= :von AND dfn.soll <= :bis ";

            if (shift != SHIFT_ALL) {
                jpql = jpql + " AND ((dfn.sZeit >= :timeid1 AND dfn.sZeit <= :timeid2) OR " +
                        " (dfn.sZeit = 0 AND dfn.soll >= :time1 AND dfn.soll <= :time2)) ";

            }

            Query query = em.createQuery(jpql);

            query.setParameter("resident", resident);
            query.setParameter("von", new DateTime(date).toDateMidnight().toDate());
            query.setParameter("bis", new DateTime(date).toDateMidnight().plusDays(1).toDateTime().minusSeconds(1).toDate());

            if (shift != SHIFT_ALL) {
                Pair<Date, Date> times = SYSCalendar.getTimeOfDay4Shift(shift);
                Pair<Byte, Byte> timeids = SYSCalendar.getTimeIDs4Shift(shift);
                query.setParameter("timeid1", timeids.getFirst());
                query.setParameter("timeid2", timeids.getSecond());
                query.setParameter("time1", times.getFirst());
                query.setParameter("time2", times.getSecond());
            }

            listDFN = new ArrayList<DFN>(query.getResultList());
            Collections.sort(listDFN);

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return listDFN;
    }

    public static String getInterventionAsHTML(DFN dfn) {
        String result = SYSConst.html_div_open;

        if (dfn.getNursingProcess() != null && dfn.getNursingProcess().isAbgesetzt()) {
            result += "<s>";
        }
        result += "<b>" + dfn.getIntervention().getBezeichnung() + "</b>";
        result += "<font size=\"-1\">";
        result += "<br/>Dauer: <b>" + dfn.getMinutes() + "</b> " + OPDE.lang.getString("misc.msg.Minutes");

        if (dfn.getNursingProcess() == null) { // on demand
            result += " " + OPDE.lang.getString(PnlDFN.internalClassID + ".ondemand");
        } else {
            result += " <font color=\"blue\">(" + dfn.getNursingProcess().getKategorie().getBezeichnung() + "</font>)";
        }

        result += "</font>";

        if (dfn.getNursingProcess() != null && dfn.getNursingProcess().isAbgesetzt()) {
            result += "</s>";
        }

        result += SYSConst.html_div_close;
        return result;
    }

    public static Icon getIcon(DFN dfn) {
        if (dfn.getStatus() == STATE_DONE) {
            return SYSConst.icon22apply;
        }
        if (dfn.getStatus() == STATE_OPEN) {
            return SYSConst.icon22empty;
        }
        if (dfn.getStatus() == STATE_REFUSED) {
            return SYSConst.icon22cancel;
        }
        return null;
    }

    public static String getScheduleText(DFN dfn, String prefix, String postfix) {
        String text = "";
        if (!dfn.isOnDemand()) {
            if (dfn.getSollZeit() == BYTE_TIMEOFDAY) {
                text += DateFormat.getTimeInstance(DateFormat.SHORT).format(dfn.getSoll());
            } else {
                String[] msg = GUITools.getLocalizedMessages(TIMEIDTEXTLONG);
                text += msg[dfn.getsZeit()];
            }
        } else {
            text += DateFormat.getTimeInstance(DateFormat.SHORT).format(dfn.getSoll());
        }

        return prefix + text + postfix;
    }

    public static boolean isChangeable(DFN dfn) {
        int DFN_MAX_MINUTES_TO_WITHDRAW = Integer.parseInt(OPDE.getProps().getProperty("dfn_max_minutes_to_withdraw"));
        boolean residentAbsent = dfn.getBewohner().isActive() && BWInfoTools.absentSince(dfn.getBewohner()) != null;

        return !residentAbsent && dfn.getBewohner().isActive() &&
                (dfn.isOnDemand() || dfn.getNursingProcess().getBis().after(new Date())) && // prescription is active or it is unassigned
                (dfn.getUser() == null ||
                        (dfn.getUser().equals(OPDE.getLogin().getUser()) &&
                                Minutes.minutesBetween(new DateTime(dfn.getMdate()), new DateTime()).getMinutes() < DFN_MAX_MINUTES_TO_WITHDRAW));
    }


    public static Date getMinDatum(Resident bewohner) {
        Date date;
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT d FROM DFN d WHERE d.bewohner = :bewohner ORDER BY d.dfnid");
        query.setParameter("bewohner", bewohner);
        query.setMaxResults(1);
        try {
            date = ((DFN) query.getSingleResult()).getSoll();
        } catch (Exception e) {
            date = new Date();
        }
        em.close();
        return date;
    }

}
