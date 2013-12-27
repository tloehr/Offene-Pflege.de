package entity.roster;

import op.tools.Pair;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class StatsPerUser {

    private final BigDecimal hours_carry, targethours;
    private final BigDecimal sick_carry;

//    private BigDecimal extrahours_sum;

    private BigDecimal extra_hours_for_holidays;
    private BigDecimal night_hours;

    private final RosterParameters rosterParameters;
    private BigDecimal hours_sum;

    private BigDecimal sick_sum;

    private final BigDecimal holiday_thisyear_carry;
    private final BigDecimal holiday_lastyear_carry;
    //    private BigDecimal holiday_lastyear_sum;
    private BigDecimal holiday_sum;


    private final ContractsParameterSet contractsParameterSet;
//    private Rosters roster;


    public StatsPerUser(BigDecimal hours_carry, BigDecimal targethours, BigDecimal sick_carry, BigDecimal holiday_thisyear_carry, BigDecimal holiday_lastyear_carry, RosterParameters rosterParameters, ContractsParameterSet contractsParameterSet, Date month) {
        this.targethours = targethours;

        this.contractsParameterSet = contractsParameterSet;
//        this.roster = roster;

        this.night_hours = BigDecimal.ZERO;
        this.hours_carry = hours_carry;
        this.sick_carry = sick_carry;
        this.holiday_thisyear_carry = holiday_thisyear_carry;
        this.holiday_lastyear_carry = holiday_lastyear_carry;

        this.extra_hours_for_holidays = contractsParameterSet.getHolidayHours(new LocalDate(month));

        this.rosterParameters = rosterParameters;
        this.hours_sum = BigDecimal.ZERO;
        this.sick_sum = BigDecimal.ZERO;
        this.holiday_sum = BigDecimal.ZERO;

    }

    public void update(ArrayList<Rplan> data) {

        BigDecimal sumExtra = BigDecimal.ZERO;
        hours_sum = BigDecimal.ZERO;
        sick_sum = BigDecimal.ZERO;
//        extra_hours = BigDecimal.ZERO;
        night_hours = BigDecimal.ZERO;
        holiday_sum = BigDecimal.ZERO;

        for (Rplan rplan : data) {
            if (rplan != null) {
                HashMap<String, BigDecimal> map = rosterParameters.getSymbol(rplan.getEffectiveSymbol()).getHourStats(new LocalDate(rplan.getStart()), contractsParameterSet);
                if (map != null) {
                    hours_sum = hours_sum.add(map.get(Symbol.BASEHOURS)).subtract(map.get(Symbol.BREAKTIME));
//                    extra_hours = extra_hours.add(map.get(Symbol.HOLIHOURS1)).add(extra_hours.add(map.get(Symbol.HOLIHOURS2)));
                    night_hours = night_hours.add(map.get(Symbol.NIGHTHOURS1)).add(map.get(Symbol.NIGHTHOURS2));
                }
                if (rosterParameters.getSymbol(rplan.getEffectiveSymbol()).getSymbolType() == Symbol.ONLEAVE) {
                    holiday_sum = holiday_sum.add(BigDecimal.ONE);
                }
                if (rosterParameters.getSymbol(rplan.getEffectiveSymbol()).getSymbolType() == Symbol.SICK) {
                    sick_sum = sick_sum.add(BigDecimal.ONE);
                }
            }
        }

    }

    public String getStatsAsHTML() {

        String html = "";

        String content = "";

        content = SYSConst.html_table_tr(
                SYSConst.html_table_th("--") +
                        SYSConst.html_table_th("Übertr.") +
                        SYSConst.html_table_th("Soll") +
                        SYSConst.html_table_th("Arbeit") +
                        SYSConst.html_table_th("Feier") +
                        SYSConst.html_table_th("Urlaub") +
                        SYSConst.html_table_th("Krank") +
                        SYSConst.html_table_th("Summe")
        );

        BigDecimal holiddayhours = contractsParameterSet.getDayValue().multiply(holiday_sum);
        BigDecimal sickhours = contractsParameterSet.getDayValue().multiply(sick_sum);
//        BigDecimal targethours = contractsParameterSet.getTargetHoursPerMonth();

        content += SYSConst.html_table_tr(
                SYSConst.html_table_th("Std.") +
                        // Carry
                        SYSConst.html_table_td(hours_carry.setScale(2, RoundingMode.HALF_UP).toString()) +
                        // Soll
                        SYSConst.html_table_td(targethours.setScale(2, RoundingMode.HALF_UP).toString()) +
                        // Arbeit
                        SYSConst.html_table_td(hours_sum.setScale(2, RoundingMode.HALF_UP).toString()) +
                        // Feiertage
                        SYSConst.html_table_td(extra_hours_for_holidays.setScale(2, RoundingMode.HALF_UP).toString()) +
                        // Urlaub
                        SYSConst.html_table_td(holiddayhours.setScale(2, RoundingMode.HALF_UP).toString()) +
                        // Krank
                        SYSConst.html_table_td(sickhours.setScale(2, RoundingMode.HALF_UP).toString()) +
                        // Summe
                        SYSConst.html_table_td(hours_carry.add(targethours).add(hours_sum).add(extra_hours_for_holidays).add(holiddayhours).add(sickhours).setScale(2, RoundingMode.HALF_UP).toString())
        );

        content += SYSConst.html_table_tr(
                SYSConst.html_table_th("--") +
                        SYSConst.html_table_th("Url.Plan") +
                        SYSConst.html_table_th("Url.alt") +
                        SYSConst.html_table_th("Url.neu") +
                        SYSConst.html_table_th("Url.Rest") +
                        SYSConst.html_table_th("Kr.vorh.") +
                        SYSConst.html_table_th("Krank") +
                        SYSConst.html_table_th("Kr.Summe")

        );

        Pair<BigDecimal, BigDecimal> holidays = getRemainingHoliday();

        content += SYSConst.html_table_tr(
                SYSConst.html_table_th("Tage") +

                        SYSConst.html_table_td(holiday_sum.setScale(0, RoundingMode.HALF_UP).toString()) +
//                        SYSConst.html_table_td(holiday_lastyear_carry.setScale(0, RoundingMode.HALF_UP).toString()) +
//                        SYSConst.html_table_td(holiday_thisyear_carry.setScale(0, RoundingMode.HALF_UP).toString()) +//
                        SYSConst.html_table_td(holidays.getFirst().setScale(0, RoundingMode.HALF_UP).toString()) +
                        SYSConst.html_table_td(holidays.getSecond().setScale(0, RoundingMode.HALF_UP).toString()) +
                        SYSConst.html_table_td(holidays.getFirst().add(holidays.getSecond()).setScale(0, RoundingMode.HALF_UP).toString()) +
                        SYSConst.html_table_td(sick_carry.setScale(0, RoundingMode.HALF_UP).toString()) +
                        SYSConst.html_table_td(sick_sum.setScale(0, RoundingMode.HALF_UP).toString()) +
                        SYSConst.html_table_td(sick_carry.add(sick_sum).setScale(0, RoundingMode.HALF_UP).toString())
        );

        return "<table style=\"font-family:arial;font-size:9px;\" border=\"1\">" + SYSTools.xx(content) + "</table>\n";

    }

    public BigDecimal getVacationSum() {
            return holiday_lastyear_carry.add(holiday_thisyear_carry).subtract(holiday_sum);
        }

    public Pair<BigDecimal, BigDecimal> getRemainingHoliday() {
        BigDecimal thisYearRemain = BigDecimal.ZERO;
        BigDecimal lastYearRemain = BigDecimal.ZERO;

        // Die Zahlen stimmen nicht


        if (holiday_lastyear_carry.equals(BigDecimal.ZERO)) {
            thisYearRemain = holiday_thisyear_carry.subtract(holiday_sum);
            lastYearRemain = BigDecimal.ZERO;
        } else if (holiday_lastyear_carry.compareTo(holiday_sum) >= 0) {
            thisYearRemain = holiday_thisyear_carry;
            lastYearRemain = holiday_lastyear_carry.subtract(holiday_sum);
        } else if (holiday_lastyear_carry.compareTo(holiday_sum) < 0) {
            lastYearRemain = BigDecimal.ZERO;
            thisYearRemain = holiday_thisyear_carry.add(holiday_lastyear_carry).subtract(holiday_sum);
        }


        return new Pair<BigDecimal, BigDecimal>(lastYearRemain, thisYearRemain);
    }
}