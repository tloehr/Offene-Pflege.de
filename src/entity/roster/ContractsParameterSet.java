package entity.roster;


import op.tools.Pair;
import org.joda.time.DateMidnight;
import org.joda.time.LocalTime;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 22.07.13
 * Time: 15:08
 * To change this template use File | Settings | File Templates.
 */
public class ContractsParameterSet implements Cloneable, Comparable<ContractsParameterSet> {

    DateMidnight from, to;
    Pair<LocalTime, LocalTime> night;
    BigDecimal vacationDaysPerYear, wagePerHour, workingDaysPerWeek, targetHoursPerMonth, hollidayPremiumPercentage, nightPremiumPercentage;
    String section;
    boolean trainee;

    public ContractsParameterSet(DateMidnight from, DateMidnight to) {
        this.from = from;
        this.to = to;
        trainee = false;
        exam = false;
    }

    private ContractsParameterSet(DateMidnight from, DateMidnight to, Pair<LocalTime, LocalTime> night, BigDecimal vacationDaysPerYear, BigDecimal wagePerHour, BigDecimal workingDaysPerWeek, BigDecimal targetHoursPerMonth, BigDecimal hollidayPremiumPercentage, BigDecimal nightPremiumPercentage, String section, boolean trainee, boolean exam) {
        this.from = from;
        this.to = to;
        this.night = night;
        this.vacationDaysPerYear = vacationDaysPerYear;
        this.wagePerHour = wagePerHour;
        this.workingDaysPerWeek = workingDaysPerWeek;
        this.targetHoursPerMonth = targetHoursPerMonth;
        this.hollidayPremiumPercentage = hollidayPremiumPercentage;
        this.nightPremiumPercentage = nightPremiumPercentage;
        this.section = section;
        this.trainee = trainee;
        this.exam = exam;
    }

    @Override
    protected Object clone()  {
        return new ContractsParameterSet(from, to, night, vacationDaysPerYear, wagePerHour, workingDaysPerWeek, targetHoursPerMonth, hollidayPremiumPercentage, nightPremiumPercentage, section, trainee, exam);
    }

    public boolean isExam() {
        return exam;
    }

    public void setExam(boolean exam) {
        this.exam = exam;
    }

    boolean exam;

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public boolean isTrainee() {
        return trainee;
    }

    public void setTrainee(boolean trainee) {
        this.trainee = trainee;
    }


    public DateMidnight getFrom() {
        return from;
    }

    public void setFrom(DateMidnight from) {
        this.from = from;
    }

    public DateMidnight getTo() {
        return to;
    }

    public void setTo(DateMidnight to) {
        this.to = to;
    }

    public Pair<LocalTime, LocalTime> getNight() {
        return night;
    }

    public void setNight(Pair<LocalTime, LocalTime> night) {
        this.night = night;
    }

    public BigDecimal getVacationDaysPerYear() {
        return vacationDaysPerYear;
    }

    public void setVacationDaysPerYear(BigDecimal vacationDaysPerYear) {
        this.vacationDaysPerYear = vacationDaysPerYear;
    }

    public BigDecimal getWagePerHour() {
        return wagePerHour;
    }

    public void setWagePerHour(BigDecimal wagePerHour) {
        this.wagePerHour = wagePerHour;
    }

    public BigDecimal getWorkingDaysPerWeek() {
        return workingDaysPerWeek;
    }

    public void setWorkingDaysPerWeek(BigDecimal workingDaysPerWeek) {
        this.workingDaysPerWeek = workingDaysPerWeek;
    }

    public BigDecimal getTargetHoursPerMonth() {
        return targetHoursPerMonth;
    }

    public void setTargetHoursPerMonth(BigDecimal targetHoursPerMonth) {
        this.targetHoursPerMonth = targetHoursPerMonth;
    }

    public BigDecimal getHollidayPremiumPercentage() {
        return hollidayPremiumPercentage;
    }

    public void setHollidayPremiumPercentage(BigDecimal hollidayPremiumPercentage) {
        this.hollidayPremiumPercentage = hollidayPremiumPercentage;
    }

    public BigDecimal getNightPremiumPercentage() {
        return nightPremiumPercentage;
    }

    public void setNightPremiumPercentage(BigDecimal nightPremiumPercentage) {
        this.nightPremiumPercentage = nightPremiumPercentage;
    }

    public int compareTo(ContractsParameterSet o) {
        return from.compareTo(o.getFrom());
    }

    public BigDecimal getTargetHoursPerWeek() {
        return targetHoursPerMonth.multiply(new BigDecimal(12)).divide(new BigDecimal(52),4, RoundingMode.HALF_UP);
    }

    public BigDecimal getDayValue(){
        return getTargetHoursPerWeek().divide(getWorkingDaysPerWeek(), 4, RoundingMode.HALF_UP);
    }

}
