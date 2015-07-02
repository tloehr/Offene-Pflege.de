package entity.nursingprocess;

import entity.info.ResInfoTools;
import entity.info.Resident;
import entity.system.SYSPropsTools;
import gui.GUITools;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.swing.*;
import java.math.BigDecimal;
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

    public static final byte SHIFT_ON_DEMAND = -1;
    public static final byte SHIFT_VERY_EARLY = 0;
    public static final byte SHIFT_EARLY = 1;
    public static final byte SHIFT_LATE = 2;
    public static final byte SHIFT_VERY_LATE = 3;

    public static final String[] SHIFT_KEY_TEXT = new String[]{"VERY_EARLY", "EARLY", "LATE", "VERY_LATE"};
    public static final String[] SHIFT_TEXT = new String[]{"nursingrecords.dfn.shift.veryearly", "nursingrecords.dfn.shift.early", "nursingrecords.dfn.shift.late", "nursingrecords.dfn.shift.verylate"};
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
//        String internalClassID = "nursingrecords.dfnimport";
        int numdfn = 0;

        LocalDate lastdfn = new LocalDate().minusDays(1);
        if (OPDE.getProps().containsKey("LASTDFNIMPORT")) {
            lastdfn = new LocalDate(DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(OPDE.getProps().getProperty("LASTDFNIMPORT")));
        }

        if (lastdfn.isAfter(new LocalDate())) {
            throw new IndexOutOfBoundsException("The date of the last import is somewhere in the future. Can't be true.");
        }

        if (lastdfn.equals(new LocalDate())) {
            OPDE.info("Today's DFNImport is already done. Stopping.");
            System.exit(0);
        }

        LocalDate targetdate = null;

        // If (for technical reasons) the lastdfn lies in the past (more than the usual 1 day),
        // then the generation is interated to the current day.
        for (int days = 1; days <= Days.daysBetween(lastdfn.plusDays(1), new LocalDate()).getDays() + 1; days++) {

            targetdate = lastdfn.plusDays(days);

            Query select = em.createQuery(" " +
                    " SELECT mt FROM InterventionSchedule mt " +
                    " JOIN mt.nursingProcess p " +
                    // nur die Planungen, die überhaupt gültig sind
                    // das sind die mit Gültigkeit BAW oder Gültigkeit endet irgendwann in der Zukunft.
                    // Das heisst, wenn eine Planungen heute endet, dann wird sie dennoch eingetragen.
                    // Also alle, die bis EINSCHLIESSLICH heute gültig sind.
                    " WHERE p.from <= :von AND p.to >= :bis " +
                    // und nur diejenigen, deren Referenzdatum nicht in der Zukunft liegt.
                    " AND mt.lDatum <= :ldatum AND p.resident.adminonly <> 2 " +
                    " ORDER BY mt.termID ");

            // Diese Aufstellung ergibt mindestens die heute gültigen Einträge.
            // Wahrscheinlich jedoch mehr als diese. Anhand des LDatums müssen
            // die wirklichen Treffer nachher genauer ermittelt werden.

            OPDE.info("[DFNImport] " + SYSTools.xx("misc.msg.writingto") + ": " + OPDE.getUrl());
            select.setParameter("von", targetdate.toDateTimeAtStartOfDay().toDate());
            select.setParameter("bis", SYSCalendar.eod(targetdate).toDate());
            select.setParameter("ldatum", targetdate.toDate());

            List<InterventionSchedule> list = select.getResultList();
            numdfn += generate(em, list, targetdate, true);
            OPDE.important(em, SYSTools.xx("nursingrecords.dfnimport") + " " + SYSTools.xx("nursingrecords.dfnimport.completed") + ": " + DateFormat.getDateInstance().format(targetdate.toDate()) + " " + SYSTools.xx("nursingrecords.dfnimport.numCreatedEntities") + ": " + numdfn);
        }

        SYSPropsTools.storeProp(em, "LASTDFNIMPORT", DateTimeFormat.forPattern("yyyy-MM-dd").print(targetdate));

        return numdfn;
    }

    /**
     * Those DFNs which habe to be processed but weren't yet, have to be transferred to the current day.
     *
     * @param em
     * @throws Exception
     */
    public static void moveFloating(EntityManager em) throws Exception {

        Date now = new Date();

        Query forceQuery = em.createQuery(" SELECT d FROM DFN d "
                + " WHERE d.floating = TRUE AND d.state = :state AND d.soll < :now "
                + " AND d.nursingProcess.from < :now AND d.nursingProcess.to > :now ");


        forceQuery.setParameter("now", now);
        forceQuery.setParameter("state", DFNTools.STATE_OPEN);

        int affectedOldDFNs = 0;
        for (DFN dfn : new ArrayList<DFN>(forceQuery.getResultList())) {
            dfn.setSoll(now);
            affectedOldDFNs++;
        }

        OPDE.important(em, affectedOldDFNs + " " + SYSTools.xx("nursingrecords.dfnimport.floatingMoved"));
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
     * <p>
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
    public static int generate(EntityManager em, List<InterventionSchedule> list, LocalDate targetdate, boolean wholeday) {
        DateTimeZone dtz = DateTimeZone.getDefault();
        String internalClassID = "nursingrecords.dfnimport";
        BigDecimal maxrows = new BigDecimal(list.size());
        int numdfn = 0;

        long now = System.currentTimeMillis();
        byte aktuelleZeit = SYSCalendar.ermittleZeit(now);

        BigDecimal row = BigDecimal.ZERO;

        System.out.println(SYSTools.xx(internalClassID) + " " + SYSTools.xx(internalClassID + ".generationForDate") + ": " + DateFormat.getDateInstance(DateFormat.SHORT).format(targetdate.toDate()));
        System.out.println(SYSTools.xx(internalClassID + ".progress"));

        for (InterventionSchedule termin : list) {

            row = row.add(BigDecimal.ONE);
            SYSTools.printProgBar(row.divide(maxrows, 2, BigDecimal.ROUND_UP).multiply(new BigDecimal(100)).intValue());

            termin = em.merge(termin);
            em.lock(termin, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            em.lock(em.merge(termin.getNursingProcess()), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            em.lock(termin.getNursingProcess().getResident(), LockModeType.OPTIMISTIC);

            if (!SYSCalendar.isInFuture(termin.getLDatum()) && (termin.isTaeglich() || termin.isPassenderWochentag(targetdate.toDate()) || termin.isPassenderTagImMonat(targetdate.toDate()))) {

                boolean treffer = false;
                LocalDate ldatum = new LocalDate(termin.getLDatum());

                // Genaue Ermittlung der Treffer
                // =============================
                if (termin.isTaeglich()) {
//                    OPDE.debug("Eine tägliche Planung");
                    // Dann wird das LDatum solange um die gewünschte Tagesanzahl erhöht, bis
                    // der stichtag getroffen wurde oder überschritten ist.
                    while (Days.daysBetween(ldatum, targetdate).getDays() > 0) {
//                        OPDE.debug("ldatum liegt vor dem stichtag. Addiere tage: " + termin.getTaeglich());
                        ldatum = ldatum.plusDays(termin.getTaeglich());
                    }
                    // Mich interssiert nur der Treffer, also die Punktlandung auf dem Stichtag
                    treffer = Days.daysBetween(ldatum, targetdate).getDays() == 0;
                } else if (termin.isWoechentlich()) {
//                    OPDE.debug("Eine wöchentliche Planung");
                    while (Weeks.weeksBetween(ldatum, targetdate).getWeeks() > 0) {
//                        OPDE.debug("ldatum liegt vor dem stichtag. Addiere Wochen: " + termin.getWoechentlich());
                        ldatum = ldatum.plusWeeks(termin.getWoechentlich());
                    }
                    // Ein Treffer ist es dann, wenn das Referenzdatum gleich dem Stichtag ist ODER es zumindest in der selben Kalenderwoche liegt.
                    // Da bei der Vorauswahl durch die Datenbank nur passende Wochentage überhaupt zugelassen wurden, muss das somit der richtige sein.
                    treffer = Weeks.weeksBetween(ldatum, targetdate).getWeeks() == 0;
                } else if (termin.isMonatlich()) {
//                    OPDE.debug("Eine monatliche Planung");
                    while (Months.monthsBetween(ldatum, targetdate).getMonths() > 0) {
//                        OPDE.debug("ldatum liegt vor dem stichtag. Addiere Monate: " + termin.getMonatlich());
                        ldatum = ldatum.plusMonths(termin.getMonatlich());
                    }
                    // Ein Treffer ist es dann, wenn das Referenzdatum gleich dem Stichtag ist ODER es zumindest im selben Monat desselben Jahres liegt.
                    // Da bei der Vorauswahl durch die Datenbank nur passende Wochentage oder Tage im Monat überhaupt zugelassen wurden, muss das somit der richtige sein.
                    treffer = Months.monthsBetween(ldatum, targetdate).getMonths() == 0;
                }

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
//                        OPDE.debug("SYSConst.FM, " + termin.getNachtMo());
                        for (int dfncount = 1; dfncount <= termin.getNachtMo(); dfncount++) {
                            em.merge(new DFN(termin, targetdate.toDate(), BYTE_EARLY_IN_THE_MORNING));
                            numdfn++;
                        }
                    }
                    if (erstAbMO && termin.getMorgens() > 0) {
//                        OPDE.debug("SYSConst.MO, " + termin.getMorgens());
                        for (int dfncount = 1; dfncount <= termin.getMorgens(); dfncount++) {
                            em.merge(new DFN(termin, targetdate.toDate(), BYTE_MORNING));
                            numdfn++;
                        }
                    }
                    if (erstAbMI && termin.getMittags() > 0) {
//                        OPDE.debug("SYSConst.MI, " + termin.getMittags());
                        for (int dfncount = 1; dfncount <= termin.getMittags(); dfncount++) {
                            em.merge(new DFN(termin, targetdate.toDate(), BYTE_NOON));
                            numdfn++;
                        }
                    }
                    if (erstAbNM && termin.getNachmittags() > 0) {
//                        OPDE.debug("SYSConst.NM, " + termin.getNachmittags());
                        for (int dfncount = 1; dfncount <= termin.getNachmittags(); dfncount++) {
                            em.merge(new DFN(termin, targetdate.toDate(), BYTE_AFTERNOON));
                            numdfn++;
                        }
                    }
                    if (erstAbAB && termin.getAbends() > 0) {
//                        OPDE.debug("SYSConst.AB, " + termin.getAbends());
                        for (int dfncount = 1; dfncount <= termin.getAbends(); dfncount++) {
                            em.merge(new DFN(termin, targetdate.toDate(), BYTE_EVENING));
                            numdfn++;
                        }
                    }
                    if (erstAbNA && termin.getNachtAb() > 0) {
//                        OPDE.debug("SYSConst.NA, " + termin.getNachtAb());
                        for (int dfncount = 1; dfncount <= termin.getNachtAb(); dfncount++) {
                            em.merge(new DFN(termin, targetdate.toDate(), BYTE_LATE_AT_NIGHT));
                            numdfn++;
                        }
                    }
                    if (uhrzeitOK && termin.getUhrzeit() != null) {

                        // This adds a Time Value to a given Date

                        // Correction for Daylight Savings
                        LocalTime timeofday = new LocalTime(termin.getUhrzeit());
                        LocalDateTime localTargetDateTime = targetdate.toLocalDateTime(timeofday);

                        if (dtz.isLocalDateTimeGap(localTargetDateTime)) {
                            //todo: find a better way to calculate this (getOffsetFromLocal)
                            localTargetDateTime = localTargetDateTime.plusHours(1);
                            OPDE.info(SYSTools.xx("Correcting for DST. [TermID=" + termin.getTermID() + "] " + localTargetDateTime.toString()));
                        }

//                        Date newTargetdate = localTargetDateTime.toDate();
                        for (int dfncount = 1; dfncount <= termin.getUhrzeitAnzahl(); dfncount++) {
                            em.merge(new DFN(termin, localTargetDateTime.toDate(), SYSConst.UZ));
                            numdfn++;
                        }
                    }

                    // Nun noch das LDatum in der Tabelle MassTermin neu setzen.
                    termin.setLDatum(targetdate.toDate());
                }
            } else {
//                OPDE.debug("///////////////////////////////////////////////////////////");
//                OPDE.debug("Folgender MassTermin wurde nicht angenommen: " + termin);
            }
        }

        System.out.println();
        System.out.println(SYSTools.xx(internalClassID + ".numCreatedEntities") + " [" + DateFormat.getDateInstance(DateFormat.SHORT).format(targetdate.toDate()) + "]: " + numdfn);
//        System.out.println("------------------------------------------");
        return numdfn;
    }


    public static long getNumDFNs(NursingProcess np) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT COUNT(dfn) FROM DFN dfn WHERE dfn.nursingProcess = :np AND dfn.state <> :state");
        query.setParameter("np", np);
        query.setParameter("state", STATE_OPEN);
        long num = (Long) query.getSingleResult();
        em.close();
        return num;
    }


    /**
     * retrieves a list of BHPs for a given resident for a given day. Only regular prescriptions are used (not OnDemand)
     *
     * @param resident
     * @param date
     * @return
     */
    public static ArrayList<DFN> getDFNs(Resident resident, Date date) {
        EntityManager em = OPDE.createEM();
        ArrayList<DFN> listDFN = null;

        try {

            String jpql = " SELECT dfn " +
                    " FROM DFN dfn " +
                    " WHERE dfn.resident = :resident " +
                    " AND dfn.soll >= :von AND dfn.soll <= :bis ";
//                    " ORDER BY dfn.intervention.bezeichnung ";

//                    " ORDER BY dfn.nursingProcess.id, dfn.sZeit, dfn.soll, dfn.intervention.bezeichnung, dfn.dfnid ";

            Query query = em.createQuery(jpql);

            LocalDate lDate = new LocalDate(date);
            query.setParameter("resident", resident);
            query.setParameter("von", lDate.toDateTimeAtStartOfDay().toDate());
            query.setParameter("bis", SYSCalendar.eod(lDate).toDate());

            listDFN = new ArrayList<DFN>(query.getResultList());
            Collections.sort(listDFN);

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return listDFN;
    }

//    public static String getInterventionAsHTML(DFN dfn) {
//        String result = SYSConst.html_div_open;
//
//        if (dfn.getNursingProcess() != null && dfn.getNursingProcess().isClosed()) {
//            result += "<s>";
//        }
//        result += "<b>" + dfn.getIntervention().getBezeichnung() + "</b>";
//        result += "<font size=\"-1\">";
//        result += "<br/>Dauer: <b>" + dfn.getMinutes() + "</b> " + SYSTools.xx("misc.msg.Minutes");
//
//        if (dfn.getNursingProcess() == null) { // on demand
//            result += " " + SYSTools.xx(PnlDFN.internalClassID + ".ondemand");
//        } else {
//            result += " <font color=\"blue\">(" + dfn.getNursingProcess().getCategory().getText() + "</font>)";
//        }
//
//        result += "</font>";
//
//        if (dfn.getNursingProcess() != null && dfn.getNursingProcess().isClosed()) {
//            result += "</s>";
//        }
//
//        result += SYSConst.html_div_close;
//        return result;
//    }

    public static Icon getIcon(DFN dfn) {
        if (dfn.getState() == STATE_DONE) {
            return SYSConst.icon22apply;
        }
        if (dfn.getState() == STATE_OPEN) {
            return null;
        }
        if (dfn.getState() == STATE_REFUSED) {
            return SYSConst.icon22cancel;
        }
        return null;
    }

    public static Icon getFloatingIcon(DFN dfn) {
        if (!dfn.isFloating()) return null;
        LocalDate start = new LocalDate(dfn.getStDatum());
        LocalDate stop = dfn.getIst() != null ? new LocalDate(dfn.getIst()) : new LocalDate();

        Icon icon;

        if (Days.daysBetween(start, stop).getDays() > 14) {
            if (dfn.isOpen()) {
                icon = SYSConst.icon22ledPurpleOn;
            } else {
                icon = SYSConst.icon22ledPurpleOff;
            }
        } else if (Days.daysBetween(start, stop).getDays() > 7) {
            if (dfn.isOpen()) {
                icon = SYSConst.icon22ledOrangeOn;
            } else {
                icon = SYSConst.icon22ledOrangeOff;
            }
        } else {
            if (dfn.isOpen()) {
                icon = SYSConst.icon22ledBlueOn;
            } else {
                icon = SYSConst.icon22ledBlueOff;
            }
        }

        return icon;
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
            text += DateFormat.getTimeInstance(DateFormat.SHORT).format(dfn.getSoll()) + " " + SYSTools.xx("misc.msg.Time.short");
        }

        return prefix + text + postfix;
    }

    /**
     * Tells if a certain DFN is changeable (can be clicked in the daily list).
     * <p>
     * The following criteria have to be met:
     * <ul>
     * <li>The resident must be active</li>
     * <li>The resident must not be absent</li>
     * <li>the dfn is either <b>on demand</b> or the connected nursingprocess must be active</li>
     * <li>there is no owning user assigned or its the same user that is currently logged in and the time period of the last change does not exceed DFN_MAX_MINUTES_TO_WITHDRAW</li>
     * </ul>
     * </p>
     * <p/>
     * <p>Note: DFN_MAX_MINUTES_TO_WITHDRAW is a system parameter which can be changed in the system settings and is stored as a system property with the key &quot;dfn_max_minutes_to_withdraw&quot;</p>
     *
     * @param dfn
     * @return
     */
    public static boolean isChangeable(DFN dfn) {
        int DFN_MAX_MINUTES_TO_WITHDRAW = Integer.parseInt(OPDE.getProps().getProperty("dfn_max_minutes_to_withdraw"));
        boolean residentAbsent = dfn.getResident().isActive() && ResInfoTools.absentSince(dfn.getResident()) != null;

        return !residentAbsent && dfn.getResident().isActive() &&
                (dfn.isOnDemand() || dfn.getNursingProcess().getTo().after(new Date())) && // prescription is active or it is unassigned
                (dfn.getUser() == null ||
                        (dfn.getUser().equals(OPDE.getLogin().getUser()) &&
                                Minutes.minutesBetween(new DateTime(dfn.getMdate()), new DateTime()).getMinutes() < DFN_MAX_MINUTES_TO_WITHDRAW));
    }


    public static Date getMinDatum(Resident bewohner) {
        Date date;
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT d FROM DFN d WHERE d.resident = :bewohner ORDER BY d.dfnid");
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


    public static DFN getLastDFN(Resident resident, int flag) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT d FROM DFN d WHERE d.resident = :resident AND d.intervention.flag = :flag AND d.state = :state AND d.nursingProcess.to > :now ORDER BY d.ist DESC");
        query.setParameter("resident", resident);
        query.setParameter("flag", flag);
        query.setParameter("now", new Date());
        query.setParameter("state", STATE_DONE);
        query.setFirstResult(0);
        query.setMaxResults(1);
        List<DFN> dfn = query.getResultList();
        em.close();
        return dfn.isEmpty() ? null : dfn.get(0);
    }

    public static ArrayList<Object[]> getAVGTimesPerDay(LocalDate month) {

        String mysql = "" +
                "SELECT i1 j1, (i4 / ?) j3, i3 j4  FROM " +
                " (" +
                "SELECT dfn.BWKennung i1, DATE(dfn.Soll) i2, intv.BWIKID i3, SUM(dfn.Dauer) i4 FROM DFN dfn " +
                "INNER JOIN intervention intv ON dfn.MassID = intv.MassID " +
                " INNER JOIN resident res ON res.BWKennung = dfn.BWKennung " +
                " WHERE dfn.Soll >= ? AND dfn.Soll <= ? AND res.StatID IS NOT NULL " +
                " GROUP BY dfn.BWKennung, intv.BWIKID " +
                " ) tbl1 " +
                " INNER JOIN resinfocategory cat ON cat.BWIKID = i3 " +
                " GROUP BY j1, j3 " +
                " ORDER BY j1, j4 ";

        EntityManager em = OPDE.createEM();
        Query query = em.createNativeQuery(mysql);

        DateTime f = month.toDateTimeAtStartOfDay().dayOfMonth().withMinimumValue();
        DateTime t = SYSCalendar.eod(month.toDateTimeAtStartOfDay().dayOfMonth().withMaximumValue()).toDateTime();//.secondOfDay().withMaximumValue();


//        OPDE.debug("period " + Days.daysBetween(f, t).getDays() + " days");

        query.setParameter(1, Days.daysBetween(f, t).getDays() + 1);
        query.setParameter(2, f.toDate());
        query.setParameter(3, t.toDate());

        ArrayList<Object[]> list = new ArrayList(query.getResultList());

        em.close();

        return list;
    }

    public static String getDFNsAsHTMLtable(List<DFN> list) {
        String result = "";

        if (!list.isEmpty()) {

            DFN d1 = list.get(0);
            result += SYSConst.html_h2(d1.isOnDemand() ? "nursingrecords.dfn.ondemand" : SHIFT_TEXT[d1.getShift()]);


            result += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\"><tr>" +
                    "<th>" + SYSTools.xx("nursingrecords.nursingprocess.interventions") + "</th><th>Zeit / Status</th><th>Benutzer / Zeit</th></tr>";

            for (DFN dfn : list) {


                result += "<tr>";
                result += "<td valign=\"top\">" + dfn.getIntervention().getBezeichnung() +
                        (dfn.isOnDemand() || SYSTools.catchNull(dfn.getInterventionSchedule().getBemerkung()).isEmpty() ? "" : " <i>(" + dfn.getInterventionSchedule().getBemerkung() + ")</i>")
                        + "</td>";
                result += "<td valign=\"top\">" + getScheduleText(dfn, " [", "]") + getStateAsHTML(dfn) + "<br/>";
                result += "<td valign=\"top\">" + (dfn.isOpen() ? "" : dfn.getUser().getUID() + "; " + DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(dfn.getIst())) + "</td>";
//                result += "<td valign=\"top\">" + myprescription.getPITAsHTML();

                result += "</td>";
                result += "</tr>";

            }

            result += "</table>";
        }

        return result;
    }

    public static String getStateAsHTML(DFN dfn) {
        String html = "";
        if (dfn.getState() == STATE_DONE) {
            html = "&#x2713;";
        }
        if (dfn.getState() == STATE_REFUSED) {
            html = "&#x2717;";
        }
        return html;
    }

}
