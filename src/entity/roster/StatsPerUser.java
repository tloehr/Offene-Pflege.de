package entity.roster;

import java.math.BigDecimal;
import java.util.ArrayList;

public class StatsPerUser {

    private final BigDecimal hours_carry;
    private final BigDecimal sick_carry;
    private final BigDecimal holiday_carry;
    private final BigDecimal holiday_lastyear_carry;

    private BigDecimal extrahours_sum;

    private final BigDecimal holiday_hours;
    private final BigDecimal night_hours;

    private final RosterParameters rosterParameters;
    private BigDecimal hours_sum;
    private BigDecimal sick_sum;
    private BigDecimal holiday_sum;


    public StatsPerUser(BigDecimal night_hours, BigDecimal hours_carry, BigDecimal sick_carry, BigDecimal holiday_carry, BigDecimal holiday_lastyear_carry, BigDecimal extrahours_sum, BigDecimal holiday_hours, RosterParameters rosterParameters, BigDecimal hours_sum, BigDecimal sick_sum, BigDecimal holiday_sum) {
        this.night_hours = night_hours;
        this.hours_carry = hours_carry;
        this.sick_carry = sick_carry;
        this.holiday_carry = holiday_carry;
        this.holiday_lastyear_carry = holiday_lastyear_carry;
        this.extrahours_sum = extrahours_sum;
        this.holiday_hours = holiday_hours;
        this.rosterParameters = rosterParameters;
        this.hours_sum = hours_sum;
        this.sick_sum = sick_sum;
        this.holiday_sum = holiday_sum;
    }

//    public StatsPerUser(BigDecimal hours_carry, BigDecimal sick_carry, BigDecimal holiday_carry, RosterParameters rosterParameters) {
//        this.hours_carry = hours_carry;
//        this.sick_carry = sick_carry;
//        this.holiday_carry = holiday_carry;
//        this.rosterParameters = rosterParameters;
//        this.hours_sum = hours_carry;
//        this.sick_sum = sick_carry;
//        this.holiday_sum = holiday_carry;
//        this.extrahours_sum = BigDecimal.ZERO;
//    }


    public void update(ArrayList<Rplan> data) {
        BigDecimal sumHours = hours_carry;
        BigDecimal sumSick = sick_carry;
        BigDecimal sumHol = holiday_carry;
        BigDecimal sumExtra = BigDecimal.ZERO;

        for (Rplan rplan : data) {
            if (rplan != null) {
//                sumHours = sumHours.add(rplan.getNetValue());
//                sumExtra = sumExtra.add(rplan.getExtrahours());
                if (rosterParameters.getSymbol(rplan.getEffectiveSymbol()).getSymbolType() == Symbol.ONLEAVE) {
                    sumHol = sumHol.subtract(BigDecimal.ONE);
                }
                if (rosterParameters.getSymbol(rplan.getEffectiveSymbol()).getSymbolType() == Symbol.SICK) {
                    sumSick = sumSick.add(BigDecimal.ONE);
                }
            }
        }

        hours_sum = sumHours;
        sick_sum = sumSick;
        holiday_sum = sumHol;
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

    public BigDecimal getHolidayCarry() {
        return holiday_carry;
    }

    public BigDecimal getExtraHoursSum() {
        return extrahours_sum;
    }

    public String getStatsAsHTML(){




    }
}