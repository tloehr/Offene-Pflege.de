package entity.roster;

import op.OPDE;
import op.tools.SYSCalendar;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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

        grpmf;
        // die pause nicht mit einrechnen.

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
        //todo: holidays
        return allowedDays.isEmpty() || allowedDays.contains(dm.getDayOfWeek());
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
        return SYSCalendar.getDecimalHours(now, now.plusMinutes(minutesBreak));

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
        if (start == null)
            return day.toDateTime(new LocalTime().hourOfDay().withMinimumValue().minuteOfHour().withMinimumValue().secondOfMinute().withMinimumValue());
        return day.toDateTime(start);
    }

    public DateTime getEnd(LocalDate day) {
        if (end == null) return null;
        DateTime endTime = day.toDateTime(end);
        return start.isAfter(end) ? endTime.plusDays(1) : endTime;
    }

    public BigDecimal getBaseHoursAsDecimal() {
        LocalDate today = new LocalDate();
        return SYSCalendar.getDecimalHours(getStart(today), getEnd(today));
    }

    private BigDecimal getExtraHoursA(LocalDate day, ContractsParameterSet contractsParameterSet) {
        if (day.getDayOfWeek() == DateTimeConstants.SUNDAY && !OPDE.isHoliday(day)) {
            return contractsParameterSet.getDayValue();
        } else {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal getExtraHoursX(LocalDate day, ContractsParameterSet contractsParameterSet) {
        if (day.getDayOfWeek() == DateTimeConstants.SUNDAY && !OPDE.isHoliday(day)) {
            return contractsParameterSet.getDayValue();
        } else {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal getExtraHoursK(ContractsParameterSet contractsParameterSet) {
        return contractsParameterSet.getDayValue();
    }

    private BigDecimal getExtraHoursU(ContractsParameterSet contractsParameterSet) {
        return contractsParameterSet.getDayValue();
    }

    private BigDecimal getBaseHoursA() {
        return getBaseHoursAsDecimal();
    }

    private BigDecimal getBaseHoursX() {
        return BigDecimal.ZERO;
    }

    private BigDecimal getBaseHoursK() {
        return BigDecimal.ZERO;
    }

    private BigDecimal getBaseHoursU() {
        return BigDecimal.ZERO;
    }

    public BigDecimal getBaseHours() {
        if (start == null) {
            return BigDecimal.ZERO;
        } else if (calc == AWERT) {
            return getBaseHoursA();
        } else if (calc == KWERT) {
            return getBaseHoursK();
        } else if (calc == XWERT) {
            return getBaseHoursX();
        } else if (calc == UWERT) {
            return getBaseHoursU();
        } else if (calc == PVALUE) {
            return BigDecimal.ZERO;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getExtraHours(LocalDate day, ContractsParameterSet contractsParameterSet) {
        if (calc == AWERT) {
            return getExtraHoursA(day, contractsParameterSet);
        } else if (calc == KWERT) {
            return getExtraHoursK(contractsParameterSet);
        } else if (calc == XWERT) {
            return getExtraHoursX(day, contractsParameterSet);
        } else if (calc == UWERT) {
            return getExtraHoursU(contractsParameterSet);
        } else if (calc == PVALUE) {
            return BigDecimal.ZERO;
        } else {
            return BigDecimal.ZERO;
        }
    }

    /**
     * returns the working hours for this symbol. including all extras without the deduction of the break.
     *
     * @param day
     * @param contractsParameterSet
     * @return
     */
    public BigDecimal getHours(LocalDate day, ContractsParameterSet contractsParameterSet) {
        if (start == null) {
            return null;
        }
        return getBaseHours().add(getExtraHours(day, contractsParameterSet));
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }
}
