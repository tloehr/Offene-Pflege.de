package entity.prescription;

import entity.building.Homes;
import entity.info.ResInfoTools;
import entity.info.Resident;
import entity.system.SYSPropsTools;
import gui.GUITools;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.swing.*;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 01.12.11
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
public class BHPTools {

    public static final byte STATE_OPEN = 0;
    public static final byte STATE_DONE = 1;
    public static final byte STATE_REFUSED = 2;
    public static final byte STATE_REFUSED_DISCARDED = 3;
    private static Logger logger = Logger.getLogger(BHPTools.class);

//    public static final String[] SHIFT_KEY_TEXT = new String[]{"VERY_EARLY", "EARLY", "LATE", "VERY_LATE"};
//    public static final String[] SHIFT_TEXT = new String[]{"nursingrecords.bhp.shift.veryearly", "nursingrecords.bhp.shift.early", "nursingrecords.bhp.shift.late", "nursingrecords.bhp.shift.verylate"};
//    public static final String[] TIMEIDTEXTLONG = new String[]{"misc.msg.Time.long", "misc.msg.earlyinthemorning.long", "misc.msg.morning.long", "misc.msg.noon.long", "misc.msg.afternoon.long", "misc.msg.evening.long", "misc.msg.lateatnight.long"};
//    public static final String[] TIMEIDTEXTSHORT = new String[]{"misc.msg.Time.short", "misc.msg.earlyinthemorning.short", "misc.msg.morning.short", "misc.msg.noon.short", "misc.msg.afternoon.short", "misc.msg.evening.short", "misc.msg.lateatnight.short"};
//
//    public static final byte BYTE_TIMEOFDAY = 0;
//    public static final byte BYTE_EARLY_IN_THE_MORNING = 1;
//    public static final byte BYTE_MORNING = 2;
//    public static final byte BYTE_NOON = 3;
//    public static final byte BYTE_AFTERNOON = 4;
//    public static final byte BYTE_EVENING = 5;
//    public static final byte BYTE_LATE_AT_NIGHT = 6;


    public static final String UIDPREFIX = "__bhp";

    public static BHP getLastBHP(Prescription prescription) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM BHP b WHERE b.prescription = :prescription AND b.state = :state ORDER BY b.ist DESC");
        query.setParameter("prescription", prescription);
        query.setParameter("state", STATE_DONE);
        query.setFirstResult(0);
        query.setMaxResults(1);
        List<BHP> bhp = query.getResultList();
        em.close();
        return bhp.isEmpty() ? null : bhp.get(0);
    }

    public static BHP getLastBHP(Resident resident, int flag) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM BHP b WHERE b.resident = :resident AND b.prescription.intervention.flag = :flag AND b.state = :state AND b.prescription.to > :now ORDER BY b.ist DESC");
        query.setParameter("resident", resident);
        query.setParameter("flag", flag);
        query.setParameter("now", new Date());
        query.setParameter("state", STATE_DONE);
        query.setFirstResult(0);
        query.setMaxResults(1);
        List<BHP> bhp = query.getResultList();
        em.close();
        return bhp.isEmpty() ? null : bhp.get(0);
    }

    public static long getConfirmedBHPs(Prescription prescription) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT COUNT(bhp) FROM BHP bhp WHERE bhp.prescription = :prescription AND bhp.state <> :status");
        query.setParameter("prescription", prescription);
        query.setParameter("status", STATE_OPEN);
        long num = (Long) query.getSingleResult();
        em.close();
        return num;
    }

    public static boolean hasBeenUsedAlready(Prescription prescription) {
        long begin = System.currentTimeMillis();
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT bhp FROM BHP bhp WHERE bhp.prescription = :prescription AND bhp.state <> :status");
        query.setParameter("prescription", prescription);
        query.setParameter("status", STATE_OPEN);
        query.setMaxResults(1);
        boolean used = query.getResultList().size() > 0;
        em.close();
        SYSTools.showTimeDifference(begin);
        return used;
    }

    public static Comparator<BHP> getOnDemandComparator() {
        return new Comparator<BHP>() {
            @Override
            public int compare(BHP o1, BHP o2) {
                int result = o1.getPrescription().getSituation().getText().toUpperCase().compareTo(o2.getPrescription().getSituation().getText().toUpperCase());
                if (result == 0) {
                    result = o1.getPrescription().compareTo(o2.getPrescription());
                }
//                if (result == 0) {
//                    Long l1 = o1.getOutcome4();
//                    Long l2 = o2.getOutcome4();
//                    if (l1 != null && l2 != null) {
//                        result = l1.compareTo(l2);
//                    } else {
//                        result = SYSTools.nullCompare(l1, l2);
//                    }
//                }

                return result;
            }
        };
    }

    public static Date getMinDatum(Resident bewohner) {
        Date date;
        long begin = System.currentTimeMillis();
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM BHP b WHERE b.resident = :resident ORDER BY b.bhpid");
        query.setParameter("resident", bewohner);
        query.setMaxResults(1);
        try {
            date = ((BHP) query.getSingleResult()).getSoll();
        } catch (Exception e) {
            date = new Date();
        }
        em.close();
        SYSTools.showTimeDifference(begin);
        return date;
    }

    /**
     * Diese Methode erzeugt den Tagesplan für die Behandlungspflegen. Dabei werden alle aktiven Verordnungen geprüft, ermittelt ob sie am betreffenden targetdate auch "dran" sind und dann
     * werden daraus Einträge in der BHP Tabelle erzeugt. Sie teilt sich die Arbeit mit der <code>erzeugen(EntityManager em, List<VerordnungpSchedule> list, Date targetdate, Date zeit)</code> Methode
     *
     * @param em, EntityManager Kontext
     * @return Anzahl der erzeugten BHPs
     */
    public static int generate(EntityManager em) throws Exception {

//        String internalClassID = "nursingrecords.bhpimport";
        int numbhp = 0;

        LocalDate lastbhp = new LocalDate().minusDays(1);
        if (OPDE.getProps().containsKey("LASTBHPIMPORT")) {
            lastbhp = new LocalDate(DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(OPDE.getProps().getProperty("LASTBHPIMPORT")));
        }

        if (lastbhp.isAfter(new LocalDate())) {
            throw new IndexOutOfBoundsException("The date of the last import is somewhere in the future. Can't be true.");
        }

        if (lastbhp.equals(new LocalDate())) {
            OPDE.info("Today's BHPImport is already done. Stopping.");
            System.exit(0);
        }

//        if (lastbhp.isAfterNow()) {
//            throw new IndexOutOfBoundsException("The date of the last import is somewhere in the future. Can't be true.");
//        }

        LocalDate targetdate = null;

        // If (for technical reasons) the lastdfn lies in the past (more than the usual 1 day),
        // then the generation is interated until the current day.
        for (int days = 1; days <= Days.daysBetween(lastbhp.plusDays(1), new LocalDate()).getDays() + 1; days++) {

            targetdate = lastbhp.plusDays(days);

            Query select = em.createQuery(" " +
                    " SELECT vp FROM PrescriptionSchedule vp " +
                    " JOIN vp.prescription v " +
                    // nur die Verordnungen, die überhaupt gültig sind
                    // das sind die mit Gültigkeit BAW oder Gültigkeit endet irgendwann in der Zukunft.
                    // Das heisst, wenn eine Verordnung heute endet, dann wird sie dennoch eingetragen.
                    // Also alle, die bis EINSCHLIEßLICH heute gültig sind.
                    " WHERE v.situation IS NULL AND v.from <= :andatum AND v.to >= :abdatum " +
                    // und nur diejenigen, deren Referenzdatum nicht in der Zukunft liegt.
                    " AND vp.lDatum <= :ldatum AND v.resident.adminonly <> 2 " +
                    " ORDER BY vp.bhppid ");

            // Diese Aufstellung ergibt mindestens die heute gültigen Einträge.
            // Wahrscheinlich jedoch mehr als diese. Anhand des LDatums müssen
            // die wirklichen Treffer nachher genauer ermittelt werden.

            OPDE.info(SYSTools.xx("\"nursingrecords.bhpimport\"") + " " + SYSTools.xx("misc.msg.writingto") + ": " + OPDE.getUrl());

            select.setParameter("andatum", new Date(SYSCalendar.startOfDay(targetdate.toDate())));
            select.setParameter("abdatum", new Date(SYSCalendar.endOfDay(targetdate.toDate())));
            select.setParameter("ldatum", new Date(SYSCalendar.endOfDay(targetdate.toDate())));

            List<PrescriptionSchedule> list = select.getResultList();

            numbhp += generate(em, list, targetdate, true);

            OPDE.important(em, SYSTools.xx("\"nursingrecords.bhpimport\"") + " " + SYSTools.xx("nursingrecords.bhpimport.completed") + ": " + DateFormat.getDateInstance().format(targetdate.toDate()) + " " + SYSTools.xx("nursingrecords.bhpimport.numCreatedEntities") + ": " + numbhp);
        }

        SYSPropsTools.storeProp(em, "LASTBHPIMPORT", DateTimeFormat.forPattern("yyyy-MM-dd").print(targetdate));

        return numbhp;
    }

    /**
     * Hiermit werden alle BHP Einträge erzeugt, die sich aus den Verordnungen in der zugehörigen Liste ergeben. Die Liste wird aber vorher
     * noch darauf geprüft, ob sie auch wirklich an dem besagten targetdate passt. Dabei gilt:
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
     * @param list       Liste der VerordnungpScheduleen, die ggf. einzutragen sind.
     * @param targetdate gibt an, für welches Datum die Einträge erzeugt werden. In der Regel ist das immer der aktuelle Tag.
     * @param wholeday   true, dann wird für den ganzen Tag erzeugt. false, dann ab der aktuellen Zeit.
     * @return die Anzahl der erzeugten BHPs.
     */
    public static int generate(EntityManager em, List<PrescriptionSchedule> list, LocalDate targetdate, boolean wholeday) {
        DateTimeZone dtz = DateTimeZone.getDefault();
//        String internalClassID = "nursingrecords.bhpimport";
        BigDecimal maxrows = new BigDecimal(list.size());
        int numbhp = 0;

        long now = System.currentTimeMillis();
        byte aktuelleZeit = SYSCalendar.ermittleZeit(now);

        BigDecimal row = BigDecimal.ZERO;

        System.out.println("------------------------------------------");
        System.out.println(SYSTools.xx("nursingrecords.bhpimport") + " " + SYSTools.xx("nursingrecords.bhpimport.generationForDate") + ": " + DateFormat.getDateInstance(DateFormat.SHORT).format(targetdate.toDate()));
        System.out.println(SYSTools.xx("nursingrecords.bhpimport.progress"));

        for (PrescriptionSchedule pSchedule : list) {
            int numbhpbefore = numbhp;
            OPDE.debug("generation for schedule: " + pSchedule.toString());
            OPDE.debug("targetdate: " + DateFormat.getDateInstance(DateFormat.SHORT).format(targetdate.toDate()));
            row = row.add(BigDecimal.ONE);
            SYSTools.printProgBar(row.divide(maxrows, 2, BigDecimal.ROUND_UP).multiply(new BigDecimal(100)).intValue());

            pSchedule = em.merge(pSchedule);
            em.lock(pSchedule, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            em.lock(em.merge(pSchedule.getPrescription()), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            em.lock(pSchedule.getPrescription().getResident(), LockModeType.OPTIMISTIC);

            if (!SYSCalendar.isInFuture(pSchedule.getLDatum()) && (pSchedule.isDaily() || pSchedule.isPassenderWochentag(targetdate.toDate()) || pSchedule.isPassenderTagImMonat(targetdate.toDate()))) {

                boolean treffer = false;
                LocalDate ldatum = new LocalDate(pSchedule.getLDatum());

                // Genaue Ermittlung der Treffer
                // =============================
                if (pSchedule.isDaily()) {
//                    OPDE.debug("Eine tägliche pSchedule");
                    // Dann wird das LDatum solange um die gewünschte Tagesanzahl erhöht, bis
                    // der targetdate getroffen wurde oder überschritten ist.
                    while (Days.daysBetween(ldatum, targetdate).getDays() > 0) {
                        ldatum = ldatum.plusDays(pSchedule.getTaeglich());
                    }
                    // Mich interssiert nur der Treffer, also die Punktlandung auf dem targetdate
                    treffer = Days.daysBetween(ldatum, targetdate).getDays() == 0;
                } else if (pSchedule.isWeekly()) {
//                    OPDE.debug("Eine wöchentliche pSchedule");
                    while (Weeks.weeksBetween(ldatum, targetdate).getWeeks() > 0) {
                        ldatum = ldatum.plusWeeks(pSchedule.getWoechentlich());
                    }
                    // Ein Treffer ist es dann, wenn das Referenzdatum gleich dem targetdate ist ODER es zumindest in der selben Kalenderwoche liegt.
                    // Da bei der Vorauswahl durch die Datenbank nur passende Wochentage überhaupt zugelassen wurden, muss das somit der richtige sein.
                    treffer = Weeks.weeksBetween(ldatum, targetdate).getWeeks() == 0;
                } else if (pSchedule.isMonthly()) {
//                    OPDE.debug("Eine monatliche pSchedule");
                    while (Months.monthsBetween(ldatum, targetdate).getMonths() > 0) {
                        ldatum = ldatum.plusMonths(pSchedule.getMonatlich());
                    }
                    // Ein Treffer ist es dann, wenn das Referenzdatum gleich dem targetdate ist ODER es zumindest im selben Monat desselben Jahres liegt.
                    // Da bei der Vorauswahl durch die Datenbank nur passende Wochentage oder Tage im Monat überhaupt zugelassen wurden, muss das somit der richtige sein.
                    treffer = Months.monthsBetween(ldatum, targetdate).getMonths() == 0;
                }

                // Es wird immer erst eine Schicht später eingetragen. Damit man nicht mit bereits
                // abgelaufenen Zeitpunkten arbeitet.
                // Bei ganzerTag=true werden all diese booleans zu true und damit neutralisiert.
                boolean erstAbFM = wholeday || aktuelleZeit == SYSCalendar.BYTE_EARLY_IN_THE_MORNING;
                boolean erstAbMO = wholeday || erstAbFM || aktuelleZeit == SYSCalendar.BYTE_MORNING;
                boolean erstAbMI = wholeday || erstAbMO || aktuelleZeit == SYSCalendar.BYTE_NOON;
                boolean erstAbNM = wholeday || erstAbMI || aktuelleZeit == SYSCalendar.BYTE_AFTERNOON;
                boolean erstAbAB = wholeday || erstAbNM || aktuelleZeit == SYSCalendar.BYTE_EVENING;
                boolean erstAbNA = wholeday || erstAbAB || aktuelleZeit == SYSCalendar.BYTE_LATE_AT_NIGHT;
                boolean uhrzeitOK = wholeday || (pSchedule.getUhrzeit() != null && DateTimeComparator.getTimeOnlyInstance().compare(pSchedule.getUhrzeit(), new DateTime(now)) > 0);

                if (treffer) {
                    if (erstAbFM && pSchedule.getNachtMo().compareTo(BigDecimal.ZERO) > 0) {
                        em.merge(new BHP(pSchedule, targetdate.toDate(), SYSCalendar.BYTE_EARLY_IN_THE_MORNING, pSchedule.getNachtMo()));
                        numbhp++;
                    }
                    if (erstAbMO && pSchedule.getMorgens().compareTo(BigDecimal.ZERO) > 0) {
                        em.merge(new BHP(pSchedule, targetdate.toDate(), SYSCalendar.BYTE_MORNING, pSchedule.getMorgens()));
                        numbhp++;
                    }
                    if (erstAbMI && pSchedule.getMittags().compareTo(BigDecimal.ZERO) > 0) {
                        em.merge(new BHP(pSchedule, targetdate.toDate(), SYSCalendar.BYTE_NOON, pSchedule.getMittags()));
                        numbhp++;
                    }
                    if (erstAbNM && pSchedule.getNachmittags().compareTo(BigDecimal.ZERO) > 0) {
                        em.merge(new BHP(pSchedule, targetdate.toDate(), SYSCalendar.BYTE_AFTERNOON, pSchedule.getNachmittags()));
                        numbhp++;
                    }
                    if (erstAbAB && pSchedule.getAbends().compareTo(BigDecimal.ZERO) > 0) {
                        em.merge(new BHP(pSchedule, targetdate.toDate(), SYSCalendar.BYTE_EVENING, pSchedule.getAbends()));
                        numbhp++;
                    }
                    if (erstAbNA && pSchedule.getNachtAb().compareTo(BigDecimal.ZERO) > 0) {
                        em.merge(new BHP(pSchedule, targetdate.toDate(), SYSCalendar.BYTE_LATE_AT_NIGHT, pSchedule.getNachtAb()));
                        numbhp++;
                    }
                    if (uhrzeitOK && pSchedule.getUhrzeit() != null) {
                        // Correction for Daylight Savings
                        LocalTime timeofday = new LocalTime(pSchedule.getUhrzeit());
                        LocalDateTime localTargetDateTime = targetdate.toLocalDateTime(timeofday);

                        if (dtz.isLocalDateTimeGap(localTargetDateTime)) {
                            //todo: find a better way to calculate this (getOffsetFromLocal)
                            localTargetDateTime = localTargetDateTime.plusHours(1);
                            OPDE.info(SYSTools.xx("Correcting for DST. [BHPPID=" + pSchedule.getBhppid() + "] " + localTargetDateTime.toString()));
                        }

                        em.merge(new BHP(pSchedule, localTargetDateTime.toDate(), SYSConst.UZ, pSchedule.getUhrzeitDosis()));
                        numbhp++;
                    }

                    // Nun noch das LDatum in der Tabelle DFNpSchedule neu setzen.
                    pSchedule.setLDatum(targetdate.toDate());

                }
            }
            OPDE.debug("number of bhps for this run: " + Integer.toString(numbhp - numbhpbefore));
        }

        System.out.println();
        System.out.println(SYSTools.xx("nursingrecords.bhpimport.numCreatedEntities") + " [" + DateFormat.getDateInstance(DateFormat.SHORT).format(targetdate.toDate()) + "]: " + numbhp);
        System.out.println("------------------------------------------");

        OPDE.debug("number of bhps overall: " + Integer.toString(numbhp));
        OPDE.debug("------------------------------------------");

        return numbhp;
    }


    /**
     * retrieves a list of BHPs for a given resident for a given day. Only OnDemand prescriptions are used (not regular ones)
     * This method creates a list of existing BHPs, as well as possible appliable BHPs which may be clicked by the user.
     *
     * @param resident
     * @param date
     * @return
     */
    public static ArrayList<BHP> getBHPsOnDemand(Resident resident, Date date) {

        List<Prescription> listPrescriptions = PrescriptionTools.getOnDemandPrescriptions(resident, date);
        LocalDate lDate = new LocalDate(date);
        long begin = System.currentTimeMillis();
        EntityManager em = OPDE.createEM();
        ArrayList<BHP> listBHP = new ArrayList<BHP>();

        try {
            Date now = new Date();

            String jpql = " SELECT bhp " +
                    " FROM BHP bhp " +
                    " WHERE bhp.prescription = :prescription " +
                    " AND bhp.soll >= :from AND bhp.soll <= :to AND bhp.dosis > 0 ";
            Query queryOnDemand = em.createQuery(jpql);

            for (Prescription prescription : listPrescriptions) {
                queryOnDemand.setParameter("prescription", prescription);
                queryOnDemand.setParameter("from", lDate.toDateTimeAtStartOfDay().toDate());
                queryOnDemand.setParameter("to", SYSCalendar.eod(lDate).toDate());

                ArrayList<BHP> listBHP4ThisPrescription = new ArrayList<BHP>(queryOnDemand.getResultList());

                PrescriptionSchedule schedule = prescription.getPrescriptionSchedule().get(0);
                // On Demand prescriptions have exactly one schedule, hence the .get(0).
                // There may not be more than MaxAnzahl BHPs resulting from this prescription.
                if (listBHP4ThisPrescription.size() < schedule.getMaxAnzahl()) {
                    // Still some BHPs to go ?
                    for (int i = listBHP4ThisPrescription.size(); i < schedule.getMaxAnzahl(); i++) {
                        BHP bhp = new BHP(schedule);
                        bhp.setIst(now);
                        bhp.setSoll(date);
                        bhp.setSollZeit(SYSCalendar.BYTE_TIMEOFDAY);
                        bhp.setDosis(schedule.getMaxEDosis());
                        bhp.setState(BHPTools.STATE_OPEN);
                        listBHP4ThisPrescription.add(bhp);
                    }
                }
                listBHP.addAll(listBHP4ThisPrescription);
                // outcome BHPs
//                listBHP.addAll(new ArrayList<BHP>(queryOutcome.getResultList()));
            }

            Collections.sort(listBHP, getOnDemandComparator());
        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        SYSTools.showTimeDifference(begin);
        return listBHP;
    }

    /**
     * retrieves BHPs for a prescription <b>WITHOUT</b> outcomes
     *
     * @param prescription
     * @param from
     * @param to
     * @return
     */
    public static ArrayList<BHP> getBHPs(Prescription prescription, LocalDate from, LocalDate to) {
        long begin = System.currentTimeMillis();
        EntityManager em = OPDE.createEM();
        ArrayList<BHP> listBHP = null;

        try {
            Date now = new Date();

            String jpql = " SELECT bhp " +
                    " FROM BHP bhp " +
                    " WHERE bhp.prescription = :prescription " +
                    " AND bhp.outcome4 IS NULL" +
                    " AND bhp.soll >= :from AND bhp.soll <= :to " +
                    " ORDER BY bhp.soll ";
            Query queryOnDemand = em.createQuery(jpql);

            queryOnDemand.setParameter("prescription", prescription);
            queryOnDemand.setParameter("from", from.toDateTimeAtStartOfDay().toDate());
            queryOnDemand.setParameter("to", SYSCalendar.eod(to).toDate());

            listBHP = new ArrayList<BHP>(queryOnDemand.getResultList());

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }

        SYSTools.showTimeDifference(begin);
        return listBHP;
    }

    /**
     * retrieves a list of BHPs for a given resident for a given day. Only regular prescriptions are used (not OnDemand).
     * Outcome BHPs included, even if they originate from onDemand Prescriptions.
     *
     * @param resident
     * @param date
     * @return
     */
    public static ArrayList<BHP> getBHPs(Resident resident, Date date) {
//        long begin = System.currentTimeMillis();
        EntityManager em = OPDE.createEM();
        ArrayList<BHP> listBHP = null;

        try {

            String jpql = " SELECT bhp " +
                    " FROM BHP bhp " +
                    " WHERE bhp.resident = :resident AND bhp.prescription.situation IS NULL" +
                    " AND bhp.soll >= :von AND bhp.soll <= :bis ";

            Query query = em.createQuery(jpql);

            LocalDate lDate = new LocalDate(date);
            query.setParameter("resident", resident);
            query.setParameter("von", lDate.toDateTimeAtStartOfDay().toDate());
            query.setParameter("bis", SYSCalendar.eod(lDate).toDate());

            listBHP = new ArrayList<BHP>(query.getResultList());
            Collections.sort(listBHP);

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
//        SYSTools.showTimeDifference(begin);
        return listBHP;
    }


    /**
     * tells us, if the BHP is commented
     *
     * @param bhp
     * @return
     */
    public static BHP getComment(BHP bhp) {
        if (bhp.getPrescriptionSchedule().getCheckAfterHours() == null) {
            return null;
        }

        if (bhp.isOutcomeText()) {
            return null;
        }

        EntityManager em = OPDE.createEM();
        ArrayList<BHP> listBHP = null;

        try {

            String jpql = " SELECT bhp " +
                    " FROM BHP bhp " +
                    " WHERE bhp.outcome4 = :outcome4  ";

            Query query = em.createQuery(jpql);

            query.setParameter("outcome4", bhp);

            listBHP = new ArrayList<BHP>(query.getResultList());

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return listBHP.isEmpty() ? null : listBHP.get(0);
    }

    /**
     * retrieves a list of BHPs for a given resident for a given day. Only regular prescriptions are used (not OnDemand).
     * Outcome BHPs included, even if they originate from onDemand Prescriptions.
     *
     * @param resident
     * @param date
     * @return
     */
    public static ArrayList<BHP> getOutcomeBHPs(Resident resident, LocalDate date) {
//            long begin = System.currentTimeMillis();
        EntityManager em = OPDE.createEM();
        ArrayList<BHP> listBHP = null;

        try {

            String jpql = " SELECT bhp " +
                    " FROM BHP bhp " +
                    " WHERE bhp.resident = :resident AND bhp.outcome4 IS NOT NULL " +
                    " AND bhp.soll >= :von AND bhp.soll <= :bis ";

            Query query = em.createQuery(jpql);

            query.setParameter("resident", resident);
            query.setParameter("von", date.toDateTimeAtStartOfDay().toDate());
            query.setParameter("bis", SYSCalendar.eod(date).toDate());

            listBHP = new ArrayList<BHP>(query.getResultList());
            Collections.sort(listBHP);

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
//            SYSTools.showTimeDifference(begin);
        return listBHP;
    }


//    public static BHP getOutcome4(BHP bhp) {
//        long begin = System.currentTimeMillis();
//        EntityManager em = OPDE.createEM();
//        ArrayList<BHP> listBHP = null;
//
//        try {
//
//            String jpql = " SELECT bhp " +
//                    " FROM BHP bhp " +
//                    " WHERE bhp.outcome4 = :bhp ";
//
//            Query query = em.createQuery(jpql);
//
//            query.setParameter("bhp", bhp);
//
//            listBHP = new ArrayList<>(query.getResultList());
//
//        } catch (Exception se) {
//            OPDE.fatal(se);
//        } finally {
//            em.close();
//        }
//        SYSTools.showTimeDifference(begin);
//        return listBHP.isEmpty() ? null : listBHP.get(0);
//    }


    public static boolean isOnDemandBHPs(Resident resident, LocalDate date) {
        EntityManager em = OPDE.createEM();
        boolean result = false;
        try {
            String jpql = " " +
                    " SELECT bhp " +
                    " FROM BHP bhp " +
                    " WHERE bhp.prescription.situation IS NOT NULL " +
                    " AND bhp.resident = :resident " +
                    " AND bhp.outcome4 IS NULL " +
                    " AND bhp.soll >= :from AND bhp.soll <= :to ";

            Query query = em.createQuery(jpql);

            query.setParameter("resident", resident);
            query.setParameter("from", date.toDateTimeAtStartOfDay().toDate());
            query.setParameter("to", SYSCalendar.eod(date).toDate());

            result = !new ArrayList<BHP>(query.getResultList()).isEmpty();

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return result;
    }


    /**
     * @param date
     * @return
     */
    public static ArrayList<BHP> getOpenBHPs(LocalDate date, Homes home) {
//        long begin = System.currentTimeMillis();
        EntityManager em = OPDE.createEM();
        ArrayList<BHP> listBHP = null;

        try {
            String jpql = " " +
                    " SELECT bhp " +
                    " FROM BHP bhp " +
                    " WHERE bhp.prescription.situation IS NULL AND bhp.state = :state " +
                    " AND bhp.resident.station.home = :home " +
                    " AND bhp.soll >= :from AND bhp.soll <= :to ";

            Query query = em.createQuery(jpql);
            query.setParameter("state", STATE_OPEN);
            query.setParameter("home", home);
            query.setParameter("from", date.toDateTimeAtStartOfDay().toDate());
            query.setParameter("to", SYSCalendar.eod(date).toDate());

            listBHP = new ArrayList<BHP>(query.getResultList());
            Collections.sort(listBHP);

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
//        SYSTools.showTimeDifference(begin);
        return listBHP;
    }

    public static String getScheduleText(BHP bhp, String prefix, String postfix) {
        String text = "";

        // https://github.com/tloehr/Offene-Pflege.de/issues/63
        if (bhp.isOutcomeText()) {
            text += DateFormat.getTimeInstance(DateFormat.SHORT).format(bhp.getSoll()) + " " + SYSTools.xx("misc.msg.Time.short");
        } else if (!bhp.isOnDemand() && !bhp.isOutcomeText()) {
            if (bhp.getSollZeit() == SYSCalendar.BYTE_TIMEOFDAY) {
                text += "<font color=\"blue\">" + DateFormat.getTimeInstance(DateFormat.SHORT).format(bhp.getSoll()) + " " + SYSTools.xx("misc.msg.Time.short") + "</font>";
            } else {
                String[] msg = GUITools.getLocalizedMessages(SYSCalendar.TIMEIDTEXTLONG);
                text += msg[bhp.getSollZeit()];
            }
        } else {
            if (bhp.getState() == STATE_DONE) {
                text += DateFormat.getTimeInstance(DateFormat.SHORT).format(bhp.getIst()) + " " + SYSTools.xx("misc.msg.Time.short");
            } else {
                text += "--";
            }

        }

        return prefix + text + postfix;
    }

    public static Icon getIcon(BHP bhp) {
        if (bhp.getState() == STATE_DONE) {
            return SYSConst.icon22apply;
        }
        if (bhp.getState() == STATE_OPEN) {
            return null;
        }
        if (bhp.getState() == STATE_REFUSED) {
            return SYSConst.icon22cancel;
        }
        if (bhp.getState() == STATE_REFUSED_DISCARDED) {
            return SYSConst.icon22deleteall;
        }
        return null;
    }


    public static Icon getWarningIcon(BHP bhp, MedStock stock) {
        if (!bhp.shouldBeCalculated() || bhp.getPrescription().isClosed()) return null;

        Icon icon = null;
        BigDecimal sum = stock == null ? BigDecimal.ZERO : MedStockTools.getSum(stock);

        if (stock == null) {
            icon = SYSConst.icon22ledRedOn;
        } else if (stock.isExpired()) {
            icon = SYSConst.icon22ledOrangeOn;
        } else if (!stock.getTradeForm().getDosageForm().isDontCALC() && sum.compareTo(BigDecimal.ZERO) <= 0) {
            icon = SYSConst.icon22ledYellowOn;
        }

        return icon;
    }

    /**
     * see https://offene-pflege.de/doku.php?id=en:dev:pnlbhp#ischangeable
     *
     * @param bhp
     * @return
     */
    public static boolean isChangeable(BHP bhp) {
        int BHP_MAX_MINUTES_TO_WITHDRAW = Integer.parseInt(OPDE.getProps().getProperty(SYSPropsTools.BHP_MAX_MINUTES_TO_WITHDRAW));
        boolean residentAbsent = bhp.getResident().isActive() && ResInfoTools.absentSince(bhp.getResident()) != null;
        MedInventory inventoryInUse = bhp.hasMed() ? TradeFormTools.getInventory4TradeForm(bhp.getResident(), bhp.getTradeForm()) : null;
        boolean medTrouble = bhp.shouldBeCalculated() && (inventoryInUse == null || MedStockTools.getStockInUse(inventoryInUse) == null);

        return !residentAbsent && bhp.getResident().isActive() &&
                !bhp.getPrescription().isClosed() &&
                !medTrouble &&
                (bhp.getUser() == null ||
                        (bhp.getUser().equals(OPDE.getMe()) &&
                                Minutes.minutesBetween(new DateTime(bhp.getMDate()), new DateTime()).getMinutes() < BHP_MAX_MINUTES_TO_WITHDRAW)) &&
                !bhp.isClosedStockInvolved();
    }


    public static ArrayList<Object[]> getAVGTimesPerDay(LocalDate month) {

        String mysql = " " +
                " SELECT bhp.BWKennung i1, (SUM(intv.Dauer) / ?) i4 FROM bhp bhp " +
                " INNER JOIN prescription ver ON ver.VERID = bhp.VERID " +
                " INNER JOIN Intervention intv ON ver.MassID = intv.MassID " +
                " INNER JOIN resident res ON res.BWKennung = bhp.BWKennung " +
                " WHERE DATE(bhp.Soll) >= ? AND  DATE(bhp.Soll) <= ? AND res.StatID IS NOT NULL " +
                " GROUP BY bhp.BWKennung ";

        EntityManager em = OPDE.createEM();
        Query query = em.createNativeQuery(mysql);

        DateTime f = month.toDateTimeAtStartOfDay().dayOfMonth().withMinimumValue();
        DateTime t = month.toDateTimeAtStartOfDay().dayOfMonth().withMaximumValue().secondOfDay().withMaximumValue();


        //        OPDE.debug("period " + Days.daysBetween(f, t).getDays() + " days");

        query.setParameter(1, Days.daysBetween(f, t).getDays() + 1);
        query.setParameter(2, f.toDate());
        query.setParameter(3, t.toDate());

        ArrayList<Object[]> list = new ArrayList(query.getResultList());

        em.close();

        return list;
    }


    public static String getBHPsAsHTMLtable(List<BHP> list, boolean withHeader) {
        String result = "";

        if (!list.isEmpty()) {

            BHP b1 = list.get(0);

            if (withHeader) {
                if (b1.isOnDemand()) {
                    result += SYSConst.html_h2("nursingrecords.bhp.ondemand");
                } else if (b1.isOutcomeText()) {
                    result += SYSConst.html_h2("nursingrecords.bhp.outcome");
                } else {
                    result += SYSConst.html_h2(SYSCalendar.SHIFT_TEXT[b1.getShift()]);
                }
            }


            String table = "";


            if (b1.isOnDemand()) {
                table += SYSConst.html_table_tr(
                        SYSConst.html_table_th("nursingrecords.nursingprocess.interventions"),
                        SYSConst.html_table_th("misc.msg.state", "center"),
                        SYSConst.html_table_th("misc.msg.outcome", "center")
                );
            } else {
                table += SYSConst.html_table_tr(
                        SYSConst.html_table_th("nursingrecords.nursingprocess.interventions"),
                        SYSConst.html_table_th("misc.msg.state", "center")
                );
            }

            for (BHP bhp : list) {
                String text =
                        PrescriptionTools.getShortDescriptionAsCompactText(bhp.getPrescriptionSchedule().getPrescription()) +
                                (bhp.hasMed() ? ", <b>" + SYSTools.formatBigDecimal(bhp.getDose()) +
                                        " " + DosageFormTools.getUsageText(bhp.getPrescription().getTradeForm().getDosageForm()) + "</b>" : "") +
                                (bhp.isOnDemand() || bhp.isOutcomeText() ? "" : getScheduleText(bhp, ", ", ""));

                if (bhp.isOutcomeText() && bhp.getState() == BHPTools.STATE_DONE) {
                    text += "\n" + SYSConst.html_paragraph(bhp.getText());
                }


                if (b1.isOnDemand()) {
                    String outcomeText = "/";
                    BHP outcome = getComment(bhp);
                    if (outcome != null && !outcome.isOpen()) {
                        outcomeText = getStateAsHTML(outcome) + " ";
                        outcomeText += (bhp.isOpen() ? "" : outcome.getUser().getUID() + "; " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(outcome.getIst()));
                        outcomeText += (bhp.isOpen() ? "" : "<br/>" + SYSConst.html_paragraph(bhp.getText()));
                    }

                    table += SYSConst.html_table_tr(
                            SYSConst.html_table_td(text, "top"),
                            SYSConst.html_table_td(getStateAsHTML(bhp) + " " + (bhp.isOpen() ? "" : bhp.getUser().getUID() + "; " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(bhp.getIst())), "center"),
                            SYSConst.html_table_td(outcomeText, "center")
                    );

                } else {
                    table += SYSConst.html_table_tr(
                            SYSConst.html_table_td(text, "top"),
                            SYSConst.html_table_td(getStateAsHTML(bhp) + " " + (bhp.isOpen() ? "" : bhp.getUser().getUID() + "; " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(bhp.getIst())), "center")
                    );
                }


            }

            result = SYSConst.html_table(table, "1");
        }

        return result;
    }

    private static String getStateAsHTML(BHP bhp) {
        String html = "";
        if (bhp.getState() == STATE_DONE) {
            html = "&#x2713;";
        }
        if (bhp.getState() == STATE_REFUSED) {
            html = "&#x2717;";
        }
        if (bhp.getState() == STATE_OPEN) {
            html = "&#x274D;";
        }
        return html;
    }


    /**
     * a BHP should be confirmed on the same day. unless its during the night shift. Then you can click the BHPs from the nightshift before, until the night shift is over.
     *
     * https://github.com/tloehr/Offene-Pflege.de/issues/64
     *
     * @param bhp
     * @return true, if its too late. false, if we can still access the bhp
     */
    public static boolean bhp2Old(BHP bhp) {

        DateTime now = new DateTime();
        LocalDate day = new LocalDate(bhp.getSoll());

        if (now.toLocalDate().equals(day)) return false;
        if (!now.toLocalDate().minusDays(1).equals(day)) return true; // if its more than 1 day, then we are out.

        // this can only happen, when the morning part of the night shift is still active, and the BHP in question
        // belongs to the same shift on the day before. (in the late evening)

        logger.debug(bhp.getShift());

        if (bhp.getShift() == SYSCalendar.SHIFT_VERY_LATE && SYSCalendar.whatShiftIs(now.toDate()) == SYSCalendar.SHIFT_VERY_EARLY)
            return false;

        return true;
    }
}
