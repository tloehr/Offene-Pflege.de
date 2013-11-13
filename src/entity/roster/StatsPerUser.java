package entity.roster;

import op.tools.Pair;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;

public class StatsPerUser {

    private final BigDecimal hours_carry;
    private final BigDecimal sick_carry;

    private BigDecimal extrahours_sum;

    private BigDecimal extra_hours;
    private BigDecimal night_hours;

    private final RosterParameters rosterParameters;
    private BigDecimal hours_sum;

    private BigDecimal sick_sum;

    private final BigDecimal holiday_thisyear_carry;
    private final BigDecimal holiday_lastyear_carry;
    //    private BigDecimal holiday_lastyear_sum;
    private BigDecimal holiday_sum;


    private final ContractsParameterSet contractsParameterSet;
    private short rosterStage;

    public StatsPerUser(BigDecimal hours_carry, BigDecimal sick_carry, BigDecimal holiday_thisyear_carry, BigDecimal holiday_lastyear_carry, RosterParameters rosterParameters, ContractsParameterSet contractsParameterSet, short rosterStage) {
//        this.hours_per_month = hours_per_month;
        this.contractsParameterSet = contractsParameterSet;
        this.rosterStage = rosterStage;
        this.night_hours = BigDecimal.ZERO;
        this.hours_carry = hours_carry;
        this.sick_carry = sick_carry;
        this.holiday_thisyear_carry = holiday_thisyear_carry;
        this.holiday_lastyear_carry = holiday_lastyear_carry;

        this.extrahours_sum = BigDecimal.ZERO;
        this.extra_hours = BigDecimal.ZERO;
        this.rosterParameters = rosterParameters;
        this.hours_sum = BigDecimal.ZERO;
        this.sick_sum = BigDecimal.ZERO;
//        this.holiday_lastyear_sum = BigDecimal.ZERO;
        this.holiday_sum = BigDecimal.ZERO;
    }

    public void update(ArrayList<Rplan> data) {

        BigDecimal sumExtra = BigDecimal.ZERO;
        hours_sum = BigDecimal.ZERO;
        sick_sum = BigDecimal.ZERO;
        extra_hours = BigDecimal.ZERO;
        night_hours = BigDecimal.ZERO;

        for (Rplan rplan : data) {
            if (rplan != null) {
                HashMap<String, BigDecimal> map = rosterParameters.getSymbol(rplan.getEffectiveSymbol()).getHourStats(new LocalDate(rplan.getStart()), contractsParameterSet);
                if (map != null) {
                    hours_sum = hours_sum.add(map.get(Symbol.BASEHOURS)).subtract(map.get(Symbol.BREAKTIME));
                    extra_hours = extra_hours.add(map.get(Symbol.HOLIHOURS1)).add(extra_hours.add(map.get(Symbol.HOLIHOURS2)));
                    night_hours = night_hours.add(map.get(Symbol.NIGHTHOURS1)).add(extra_hours.add(map.get(Symbol.NIGHTHOURS2)));
                }
                if (rosterParameters.getSymbol(rplan.getEffectiveSymbol()).getSymbolType() == Symbol.ONLEAVE) {
                    holiday_sum = holiday_sum.add(BigDecimal.ONE);
                }
                if (rosterParameters.getSymbol(rplan.getEffectiveSymbol()).getSymbolType() == Symbol.SICK) {
                    sick_sum = sick_sum.add(BigDecimal.ONE);
                }
            }
        }


        extrahours_sum = sumExtra;
    }

    public BigDecimal getHoursSum() {
        return hours_sum;
    }

    public BigDecimal getSickSum() {
        return sick_sum;
    }

    public BigDecimal getHolidaySum() {
        return holiday_sum;
    }

    public BigDecimal getHoursCarry() {
        return hours_carry;
    }

    public BigDecimal getSickCarry() {
        return sick_carry;
    }


    public BigDecimal getExtraHoursSum() {
        return extrahours_sum;
    }

    public String getStatsAsHTML() {

        String html = "";

        String content = "";

        content = SYSConst.html_table_tr(
                SYSConst.html_table_th("--") +
                        SYSConst.html_table_th("Soll") +
                        SYSConst.html_table_th("Plan") +
                        SYSConst.html_table_th("l.Monat") +
                        SYSConst.html_table_th("d.Monat") +
                        SYSConst.html_table_th("Feier.Std.") +
                        SYSConst.html_table_th("Nacht.Std.")
        );

        content += SYSConst.html_table_tr(
                SYSConst.html_table_th("Stunden") +
                        SYSConst.html_table_td(contractsParameterSet.getTargetHoursPerMonth().setScale(2, RoundingMode.HALF_UP).toString()) +
                        SYSConst.html_table_td(hours_sum.setScale(2, RoundingMode.HALF_UP).toString()) +
                        SYSConst.html_table_td(hours_carry.setScale(2, RoundingMode.HALF_UP).toString()) +
                        SYSConst.html_table_td(hours_carry.add(hours_sum).setScale(2, RoundingMode.HALF_UP).toString()) +
                        SYSConst.html_table_td(extra_hours.setScale(2, RoundingMode.HALF_UP).toString()) +
                        SYSConst.html_table_td(night_hours.setScale(2, RoundingMode.HALF_UP).toString())
        );

        content += SYSConst.html_table_tr(
                SYSConst.html_table_th("--") +
                        SYSConst.html_table_th("l.Jahr") +
                        SYSConst.html_table_th("d.Jahr") +
                        SYSConst.html_table_th("Plan") +
                        SYSConst.html_table_th("R.l.Jahr") +
                        SYSConst.html_table_th("R.d.Jahr") +
                        SYSConst.html_table_th("")
        );

        Pair<BigDecimal, BigDecimal> holidays = getRemainingHoliday();

        content += SYSConst.html_table_tr(
                SYSConst.html_table_th("Url.Tage") +
                        SYSConst.html_table_td(holiday_lastyear_carry.setScale(0, RoundingMode.HALF_UP).toString()) +
                        SYSConst.html_table_td(holiday_thisyear_carry.setScale(0, RoundingMode.HALF_UP).toString()) +
                        SYSConst.html_table_td(holiday_sum.setScale(0, RoundingMode.HALF_UP).toString()) +
                        SYSConst.html_table_td(holidays.getFirst().setScale(0, RoundingMode.HALF_UP).toString()) +
                        SYSConst.html_table_td(holidays.getSecond().setScale(0, RoundingMode.HALF_UP).toString()) +
                        SYSConst.html_table_td("")
        );

        return "<table style=\"font-family:arial;font-size:9px;\" border=\"1\">" + SYSTools.xx(content) + "</table>\n";

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