package entity.roster;

import op.OPDE;
import op.tools.SYSCalendar;
import org.joda.time.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public static final String DAYHOURS1 = "dayhours1";
    public static final String DAYHOURS2 = "dayhours2";
    public static final String NIGHTHOURS1 = "nighthours1";
    public static final String NIGHTHOURS2 = "nighthours2";
    public static final String BREAKTIME = "breaktime";
    public static final String BASEHOURS = "basehours";

    // CalcTypes
    public static final int AWERT = 0;
    public static final int KWERT = 1;
    public static final int UWERT = 2;
    public static final int XWERT = 3;
    public static final int PVALUE = 4; // only for planning reasons. No predicted work time. Comes only into effect, when the real worklog for this day is present. Mainly for social workers.
    public static final String[] CALC = new String[]{"awert", "kwert", "uwert", "xwert", "pvalue"};

    // SymbolTypes
    public static final int WORK = 0;
    public static final int SICK = 1;
    public static final int OFFDUTY = 2;
    public static final int ONLEAVE = 3; // For symbols denoting vacation days
    public static final int SCHOOL = 4;
    public static final String[] TYPE = new String[]{"work", "sick", "offduty", "onleave", "school"};

    public static final byte SHIFT_NONE = 0;
    public static final byte SHIFT_EARLY = 1;
    public static final byte SHIFT_LATE = 2;
    public static final byte SHIFT_NIGHT = 3;
    public static final String[] SHIFT = new String[]{"", "early", "late", "night"};

    public static final int HOLIDAY = 99;

    String key, description;
    int calc, symboltype, minutesBreakDay, minutesBreakNight, section;
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
        this.minutesBreakDay = minutesBreak;
        this.minutesBreakNight = 0;
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
        if (minutesBreakDay == 0) return BigDecimal.ZERO;
        return new BigDecimal(minutesBreakDay).divide(new BigDecimal(DateTimeConstants.MINUTES_PER_HOUR)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getNBreak() {
        if (minutesBreakDay == 0) return BigDecimal.ZERO;
        return new BigDecimal(minutesBreakDay).divide(new BigDecimal(DateTimeConstants.MINUTES_PER_HOUR)).setScale(2, RoundingMode.HALF_UP);
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
        if (start == null) return day.toDateTimeAtStartOfDay();

        return day.toDateTime(start);
    }

    public DateTime getEnd(LocalDate day) {
        if (end == null || start == null) return null;
        DateTime endTime = day.toDateTime(end);
        return start.isAfter(end) ? endTime.plusDays(1) : endTime;
    }

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


    /**
     * this is the amount of hours displayed on the roster during the planning phase.
     * these values will be replaced by the WLog entities, as soon as they exist.
     *
     * @return
     */
    public BigDecimal getDisplayHours() {
        if (start == null || end == null) {
            return null;
        } else if (calc == AWERT) {
            return getBaseHoursAsDecimal().subtract(getBreak());
        } else {
            return BigDecimal.ZERO;
        }
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    /**
     * the sum of night hours between the beginning of a shift and the end. if that end is on the next day only the
     * sum until midnight is calculated, hence splitting the hours into two days. and this is <b>DAY 1</b>.
     * night hours a special hours between to specific times during a night (usually between 23h and 6h), but
     * contracts can have different notions of night shift.
     * <p/>
     * anyways, this method calcualtes the <b>OVERLAP</b> between the interval of the shift and the interval of the night hours for day 1.
     *
     * @return 0 a map with the hours for every part of the shift. the following keys are used for the map: "dayhours1", "dayhours2", "nighthours1", "nighthours2", "holihours1", "holihours2", "extra1", "extra2".
     * <code>NULL</code> if calculation is not possible. e.g. when using pure PLANNING symbols.
     */
    public HashMap<String, BigDecimal> getHourStats(LocalDate day, ContractsParameterSet contractsParameterSet) {

        if (start == null || end == null) return null;
        if (calc == PVALUE) return null;

        HashMap<String, BigDecimal> mapHours = new HashMap<String, BigDecimal>();
        mapHours.put(DAYHOURS1, BigDecimal.ZERO);
        mapHours.put(DAYHOURS2, BigDecimal.ZERO);
        mapHours.put(NIGHTHOURS1, BigDecimal.ZERO);
        mapHours.put(NIGHTHOURS2, BigDecimal.ZERO);
        mapHours.put(BREAKTIME, getBreak());

            // determine the night hours according to the user's contract
            DateTime contractNightStart = day.toDateTime(contractsParameterSet.getNight().getFirst());
            DateTime contractNightEnd = day.toDateTime(contractsParameterSet.getNight().getSecond());
            if (contractNightEnd.isBefore(contractNightStart)) contractNightEnd = contractNightEnd.plusDays(1);

            Interval nightInterval = new Interval(contractNightStart, contractNightEnd);

            DateTime startDay1 = day.toDateTime(start);
            DateTime startDay2 = isOvernight() ? day.plusDays(1).toDateTimeAtStartOfDay() : null;
            DateTime endDay1 = isOvernight() ? day.plusDays(1).toDateTimeAtStartOfDay() : day.toDateTime(end);
            DateTime endDay2 = isOvernight() ? day.toDateTime(end).plusDays(1) : null;

            // probier die Nacht aus. Das stimmt was nicht mit START und END
            Interval shiftInterval1 = new Interval(startDay1, endDay1);
            Interval[] dayhours1 = SYSCalendar.notin(shiftInterval1, nightInterval);
            BigDecimal dayh1 = BigDecimal.ZERO;
            for (Interval interval : dayhours1) {
                dayh1 = dayh1.add(SYSCalendar.getHoursAsDecimal(interval));
            }
            mapHours.put(DAYHOURS1, dayh1);

            Interval nighthours1 = nightInterval.overlap(shiftInterval1);
            mapHours.put(NIGHTHOURS1, SYSCalendar.getHoursAsDecimal(nighthours1));

            if (isOvernight()) {
                Interval shiftInterval2 = new Interval(startDay2, endDay2);
                Interval[] dayhours2 = SYSCalendar.notin(shiftInterval2, nightInterval);
                BigDecimal dayh2 = BigDecimal.ZERO;
                for (Interval interval : dayhours2) {
                    dayh2 = dayh2.add(SYSCalendar.getHoursAsDecimal(interval));
                }
                mapHours.put(DAYHOURS2, dayh2);


                Interval nighthours2 = nightInterval.overlap(shiftInterval2);
                mapHours.put(NIGHTHOURS2, SYSCalendar.getHoursAsDecimal(nighthours2));
            }

            mapHours.put(BASEHOURS, SYSCalendar.getHoursAsDecimal(new Interval(getStart(day), getEnd(day))));
//        }
        return mapHours;
    }

    public int getMinutesBreakDay() {
        return minutesBreakDay;
    }

    public int getMinutesBreakNight() {
        return minutesBreakNight;
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "key='" + key + '\'' +
                ", symboltype=" + symboltype +
                ", minutesBreak=" + minutesBreakDay +
                ", start=" + start +
                ", end=" + end +
                ", shift1=" + shift1 +
                ", shift2=" + shift2 +
                ", statval1=" + statval1 +
                ", statval2=" + statval2 +
                "} " + super.toString();
    }
}
