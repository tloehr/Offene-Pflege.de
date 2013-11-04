package entity.roster;

import op.OPDE;
import op.tools.SYSCalendar;
import org.joda.time.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 29.07.13
 * Time: 14:52
 * To change this template use File | Settings | File Templates.
 */


public class Symbol {

    public static final int AWERT = 0;
    public static final int KWERT = 1;
    public static final int UWERT = 2;
    public static final int XWERT = 3;
    public static final int PVALUE = 4; // only for planning reasons. No predicted work time. Comes only into effect, when the real worklog for this day is present.

    public static final String[] CALC = new String[]{"awert", "kwert", "uwert", "xwert", "pvalue"};

    public static final int WORK = 0;
    public static final int SICK = 1;
    public static final int OFFDUTY = 2;
    public static final int ONLEAVE = 3;
    public static final int SCHOOL = 3;

    public static final String[] TYPE = new String[]{"work", "sick", "offduty", "onleave", "school"};

    public static final byte SHIFT_NONE = 0;
    public static final byte SHIFT_EARLY = 1;
    public static final byte SHIFT_LATE = 2;
    public static final byte SHIFT_NIGHT = 3;

    public static final String[] SHIFT = new String[]{"", "early", "late", "night"};

    public static final int HOLIDAY = 99;

    String key, description;
    int calc, symboltype, minutesBreak, section;
    LocalTime start, end;

    int shift1, shift2;
    BigDecimal statval1, statval2;

    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    HashSet<Integer> allowedDays;

    public Symbol() {
        allowedDays = new HashSet<Integer>();
        shift1 = SHIFT_NONE;
        shift2 = SHIFT_NONE;
        statval1 = BigDecimal.ZERO;
        statval2 = BigDecimal.ZERO;
    }

    public Symbol(String key, String description, String start, String end, int minutesBreak, String calc, String symboltype) throws Exception {
        this();
        this.key = key.toLowerCase();
        this.description = description;
        this.minutesBreak = minutesBreak;
        this.calc = Arrays.asList(CALC).indexOf(calc.toLowerCase());
        this.symboltype = Arrays.asList(TYPE).indexOf(symboltype.toLowerCase());
        this.section = RosterXML.CARE;

        if (start == null) {
            this.start = null;
        } else {
            this.start = new LocalTime(timeFormat.parse(start).getTime());
        }
        if (end == null) {
            this.end = null;
        } else {
            this.end = new LocalTime(timeFormat.parse(end).getTime());
        }


//        if (start != null && end != null) {
//            grossWorkingTime = new Period(this.start, this.end).toStandardDuration().;
//            System.out.println(key + ": " + grossWorkingTime.getHours() + ":" + grossWorkingTime.getMinutes());
//        }

    }


    public int getShift1() {
        return shift1;
    }

    public int getShift2() {
        return shift2;
    }

    public BigDecimal getStatval1() {
        return statval1;
    }

    public BigDecimal getStatval2() {
        return statval2;
    }

    public void setShift1(String s1, String sv1) {
        this.shift1 = Arrays.asList(SHIFT).indexOf(s1);
        this.statval1 = new BigDecimal(sv1);
    }

    public void setShift2(String s2, String sv2) {
        this.shift2 = Arrays.asList(SHIFT).indexOf(s2);
        this.statval2 = new BigDecimal(sv2);
    }

    public HashSet<Integer> getAllowedDays() {
        return allowedDays;
    }

    public void addDay(int day) {
        allowedDays.add(day);
    }

    public boolean isAllowed(LocalDate dm) {

        boolean weekdayAllowed = allowedDays.isEmpty() || allowedDays.contains(dm.getDayOfWeek());
        boolean holidayAllowed = allowedDays.isEmpty() || allowedDays.contains(HOLIDAY) || !OPDE.isHoliday(dm);

        return weekdayAllowed && holidayAllowed;
    }

    public int getSymbolType() {
        return symboltype;
    }

    public int getCalc() {
        return calc;
    }

    public BigDecimal getBreak() {
        if (start == null) {
            return BigDecimal.ZERO;
        }
        DateTime now = new DateTime();
        return SYSCalendar.getHoursAsDecimal(now, now.plusMinutes(minutesBreak));

    }

    public String getDescription() {
        return description;
    }

    public String getKey() {
        return key;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public DateTime getStart(LocalDate day) {
        if (start == null) return null;

//            return day.toDateTime(new LocalTime().hourOfDay().withMinimumValue().minuteOfHour().withMinimumValue().secondOfMinute().withMinimumValue());
        return day.toDateTime(start);
    }

    public DateTime getEnd(LocalDate day) {
        if (end == null || start == null) return null;
        DateTime endTime = day.toDateTime(end);
        return start.isAfter(end) ? endTime.plusDays(1) : endTime;
    }

//    public DateTime getStart1(LocalDate day) {
//        return getStart(day);
//    }
//
//    public DateTime getStart2(LocalDate day) {
//        if (isOvernight()) {
//            return day.plusDays(1).toDateTimeAtStartOfDay();
//        } else {
//            return null;
//        }
//    }
//
//    public DateTime getEndDay1(LocalDate day) {
//        if (isOvernight()) {
//            return SYSCalendar.eod(day.toDateTimeAtCurrentTime());
//        } else {
//            return getEnd(day);
//        }
//    }
//
//    public DateTime getEndDay2(LocalDate day) {
//        if (isOvernight()) {
//            return getEnd(day);
//        } else {
//            return null;
//        }
//    }

    public boolean isOvernight() {
        if (end == null) return false;
        return start.isAfter(end);
    }


    /**
     * the sum of hours between the beginning of a shift and the end.
     *
     * @return
     */
    public BigDecimal getBaseHoursAsDecimal() {
        LocalDate today = new LocalDate();
        return SYSCalendar.getHoursAsDecimal(getStart(today), getEnd(today));
    }


//    private BigDecimal getExtraHoursA(LocalDate day, ContractsParameterSet contractsParameterSet) {
//        if (day.getDayOfWeek() != DateTimeConstants.SUNDAY && OPDE.isHoliday(day)) {
//            return contractsParameterSet.getDayValue();
//        } else {
//            return BigDecimal.ZERO;
//        }
//    }
//
//    private BigDecimal getExtraHoursX(LocalDate day, ContractsParameterSet contractsParameterSet) {
//        if (OPDE.isHoliday(day)) {
//            return contractsParameterSet.getDayValue();
//        } else {
//            return BigDecimal.ZERO;
//        }
//    }
//
//    private BigDecimal getExtraHoursK(ContractsParameterSet contractsParameterSet) {
//        return contractsParameterSet.getDayValue();
//    }
//
//    private BigDecimal getExtraHoursU(ContractsParameterSet contractsParameterSet) {
//        return contractsParameterSet.getDayValue();
//    }
//
//    private BigDecimal getBaseHoursA() {
//        return getBaseHoursAsDecimal();
//    }
//
//    private BigDecimal getBaseHoursX() {
//        return BigDecimal.ZERO;
//    }
//
//    private BigDecimal getBaseHoursK() {
//        return BigDecimal.ZERO;
//    }
//
//    private BigDecimal getBaseHoursU() {
//        return BigDecimal.ZERO;
//    }


    /**
     * this is the amount of hours displayed on the roster during the planning phase.
     * these values will be replaced by the Workinglog entities, as soon as they exist.
     *
     * @return
     */
    public BigDecimal getDisplayHours() {
        if (start == null || end == null) {
            return BigDecimal.ZERO;
        } else if (calc == AWERT) {
            return getBaseHoursAsDecimal();
        } else {
            return BigDecimal.ZERO;
        }
    }

//    public BigDecimal getBaseHours() {
//        if (start == null) {
//            return BigDecimal.ZERO;
//        } else if (calc == AWERT) {
//            return getBaseHoursA();
//        } else if (calc == KWERT) {
//            return getBaseHoursK();
//        } else if (calc == XWERT) {
//            return getBaseHoursX();
//        } else if (calc == UWERT) {
//            return getBaseHoursU();
//        } else if (calc == PVALUE) {
//            return BigDecimal.ZERO;
//        } else {
//            return BigDecimal.ZERO;
//        }
//    }

    public BigDecimal getExtraHours(LocalDate day, ContractsParameterSet contractsParameterSet) {
        if (calc == AWERT) {
            if (day.getDayOfWeek() != DateTimeConstants.SUNDAY && OPDE.isHoliday(day)) {
                return contractsParameterSet.getDayValue();
            } else {
                return BigDecimal.ZERO;
            }
        } else if (calc == KWERT) {
            if (OPDE.isHoliday(day)) {
                return contractsParameterSet.getDayValue();
            } else {
                return BigDecimal.ZERO;
            }
        } else if (calc == XWERT) {
            return BigDecimal.ZERO;
        } else if (calc == UWERT) {
            return contractsParameterSet.getDayValue();
        } else if (calc == PVALUE) {
            return BigDecimal.ZERO;
        } else {
            return BigDecimal.ZERO;
        }
    }

//    /**
//     * returns the working hours for this symbol. including all extras without the deduction of the break.
//     *
//     * @param day
//     * @param contractsParameterSet
//     * @return
//     */
//    public BigDecimal getHours(LocalDate day, ContractsParameterSet contractsParameterSet) {
//        if (start == null) {
//            return null;
//        }
//        return getBaseHours().add(getExtraHours(day, contractsParameterSet));
//    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

//    /**
//     * returns the extra hours for working on a sunday or a holliday.
//     *
//     * @param day
//     * @return
//     */
//    public BigDecimal getSunHolidayHours(LocalDate day) {
//        if (end == null) return BigDecimal.ZERO;
//
//    }
//
//    public BigDecimal getHolidayHours(LocalDate day) {
//        if (end == null) return BigDecimal.ZERO;
//
//        DateTime nightStart = day.toDateTime(contractsParameterSet.getNight().getFirst());
//        DateTime nightEnd = day.toDateTime(contractsParameterSet.getNight().getSecond());
//        if (nightEnd.isBefore(nightStart)) nightEnd = nightEnd.plusDays(1);
//
//        Interval nightInterval = new Interval(nightStart, nightEnd);
//        Interval shiftInterval = new Interval(getStart(day), getEnd(day));
//
//        return SYSCalendar.getHoursAsDecimal(nightInterval.overlap(shiftInterval).getStart(), nightInterval.overlap(shiftInterval).getEnd());
//        return BigDecimal.ZERO;
//    }


    /**
     * the sum of night hours between the beginning of a shift and the end. if that end is on the next day only the
     * sum until midnight is calculated, hence splitting the hours into two days. and this is <b>DAY 1</b>.
     * night hours a special hours between to specific times during a night (usually between 23h and 6h), but
     * contracts can have different notions of night shift.
     * <p/>
     * anyways, this method calcualtes the <b>OVERLAP</b> between the interval of the shift and the interval of the night hours for day 1.
     *
     * @return 0 a map with the hours for every part of the shift. the following keys are used for the map: "dayhours1", "dayhours2", "nighthours1", "nighthours2", "holihours1", "holihours2", "extra1", "extra2".
     *         <code>NULL</code> if calculation is not possible. e.g. when using pure PLANNING symbols.
     */
    public HashMap<String, BigDecimal> getHourStats(LocalDate day, ContractsParameterSet contractsParameterSet) {

        if (start == null || end == null) return null;
        if (symboltype == PVALUE) return null;

        HashMap<String, BigDecimal> mapHours = new HashMap<String, BigDecimal>();
        mapHours.put("dayhours1", BigDecimal.ZERO);
        mapHours.put("dayhours2", BigDecimal.ZERO);
        mapHours.put("nighthours1", BigDecimal.ZERO);
        mapHours.put("nighthours2", BigDecimal.ZERO);
        mapHours.put("holihours1", BigDecimal.ZERO);
        mapHours.put("holihours2", BigDecimal.ZERO);
        mapHours.put("extra", BigDecimal.ZERO);
//        mapHours.put("extra2", BigDecimal.ZERO);

        // determine the night hours according to the user's contract
        DateTime contractNightStart = day.toDateTime(contractsParameterSet.getNight().getFirst());
        DateTime contractNightEnd = day.toDateTime(contractsParameterSet.getNight().getSecond());
        if (contractNightEnd.isBefore(contractNightStart)) contractNightEnd = contractNightEnd.plusDays(1);

        Interval nightInterval = new Interval(contractNightStart, contractNightEnd);

        DateTime startDay1 = day.toDateTime(start);
        DateTime startDay2 = isOvernight() ? day.plusDays(1).toDateTimeAtStartOfDay() : null;
        DateTime endDay1 = isOvernight() ? SYSCalendar.eod(day) : day.toDateTime(end);
        DateTime endDay2 = isOvernight() ? day.toDateTime(end).plusDays(1) : null;

        Interval shiftInterval1 = new Interval(startDay1, endDay1);
        Interval[] dayhours1 = SYSCalendar.notin(shiftInterval1, nightInterval);
        Interval nighthours1 = nightInterval.overlap(shiftInterval1);

        if (isOvernight()) {
            Interval shiftInterval2 = new Interval(startDay2, endDay2);
            Interval[] dayhours2 = SYSCalendar.notin(shiftInterval2, nightInterval);
            Interval nighthours2 = nightInterval.overlap(shiftInterval2);
        }



        return mapHours;
    }

    /**
     * the sum of night hours between the beginning of a shift and the end. if that end is on the next day only the
     * sum until midnight is calculated, hence splitting the hours into two days. and this is <b>DAY 2</b>.
     * night hours a special hours between to specific times during a night (usually between 23h and 6h), but
     * contracts can have different notions of night shift.
     * <p/>
     * anyways, this method calcualtes the <b>OVERLAP</b> between the interval of the shift and the interval of the night hours for day 2.
     *
     * @return 0 if not overlapping or shift has no end time.
     */
    public BigDecimal getNightHoursDay2(LocalDate day, ContractsParameterSet contractsParameterSet) {

        if (end == null) return BigDecimal.ZERO;

        // determine the night hours according to the user's contract
        DateTime nightStart = day.toDateTime(contractsParameterSet.getNight().getFirst());
        DateTime nightEnd = day.toDateTime(contractsParameterSet.getNight().getSecond());
        if (nightEnd.isBefore(nightStart)) nightEnd = nightEnd.plusDays(1);

        Interval nightInterval = new Interval(nightStart, nightEnd);

        // determine the shift hours according to the symbol settings
        DateTime start = isOvernight() ? day.toDateTimeAtStartOfDay() : getStart(day);
        Interval shiftInterval = new Interval(start, getEnd(day));

        return SYSCalendar.getHoursAsDecimal(nightInterval.overlap(shiftInterval).getStart(), nightInterval.overlap(shiftInterval).getEnd());
    }

    /**
     * the sum of hours between the beginning of a shift and the end. if that end is on the next day only the
     * sum until midnight is calculated, hence splitting the hours into two days. and this is <b>DAY 1</b>.
     *
     * @return
     */
    public BigDecimal getBaseHoursAsDecimalDay1(LocalDate day) {
        DateTime end = isOvernight() ? SYSCalendar.eod(day) : getEnd(day);

        return SYSCalendar.getHoursAsDecimal(getStart(day), end);
    }

    /**
     * the sum of hours between the beginning of a shift and the end. if that end is on the next day only the
     * sum between midnight and the <b>end</b> is calculated, hence splitting the hours into two days. and this is <b>DAY 2</b>.
     *
     * @return
     */
    public BigDecimal getBaseHoursAsDecimalDay2(LocalDate day) {
        if (!isOvernight()) return BigDecimal.ZERO;
        DateTime start = day.plusDays(1).toDateTimeAtStartOfDay();

        return SYSCalendar.getHoursAsDecimal(start, getEnd(day));
    }

}
