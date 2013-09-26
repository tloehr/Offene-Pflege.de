package entity.roster;

import java.math.BigDecimal;
import java.util.ArrayList;

public class UserStats {

    private final BigDecimal hours_carry;
    private final BigDecimal sick_carry;
    private final BigDecimal holiday_carry;
    private final RosterParameters rosterParameters;
    private BigDecimal hours_sum;
    private BigDecimal sick_sum;
    private BigDecimal holiday_sum;

    public UserStats(BigDecimal hours_carry, BigDecimal sick_carry, BigDecimal holiday_carry, RosterParameters rosterParameters) {
        this.hours_carry = hours_carry;
        this.sick_carry = sick_carry;
        this.holiday_carry = holiday_carry;
        this.rosterParameters = rosterParameters;
        this.hours_sum = hours_carry;
        this.sick_sum = sick_carry;
        this.holiday_sum = holiday_carry;
    }

    public void update(ArrayList<Rplan> data) {
        BigDecimal sumHours = hours_carry;
        BigDecimal sumSick = sick_carry;
        BigDecimal sumHol = holiday_carry;

        for (Rplan rplan : data) {
            if (rplan != null) {
                sumHours = sumHours.add(rplan.getNetValue());
                if (rosterParameters.getSymbol(rplan.getEffectiveP()).getSymbolType() == Symbol.ONLEAVE) {
                    sumHol = sumHol.subtract(BigDecimal.ONE);
                }
                if (rosterParameters.getSymbol(rplan.getEffectiveP()).getSymbolType() == Symbol.SICK) {
                    sumSick = sumSick.add(BigDecimal.ONE);
                }
            }
        }

        hours_sum = sumHours;
        sick_sum = sumSick;
        holiday_sum = sumHol;
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
}