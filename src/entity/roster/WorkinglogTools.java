package entity.roster;

import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 16.10.13
 * Time: 12:07
 * To change this template use File | Settings | File Templates.
 */
public class WorkinglogTools {

    // all AUTO types meaning an entry which has been created while applying the planned shift
    public static final int TYPE_AUTO_DAY = 0;
    public static final int TYPE_AUTO_NIGHT = 1;
    public static final int TYPE_AUTO_HOLIDAY = 2;
    public static final int TYPE_AUTO_BREAK = 3; // negative value
    public static final int TYPE_MANUAL_HOURS = 4;
    public static final int TYPE_AUTO_EXTRA = 5; // additional hours for holiday shifts
    public static final String[] TYPES = new String[]{"Tag", "Nacht", "Urlaub", "Pausen", "Zuschlag"};

    public static String toPrettyString(Workinglog workinglog) {
        String text = "";

        text = TYPES[workinglog.getType()] + ": " + workinglog.getHours();

        return text;
    }


    /**
     * when entering an actual work log, the could be separate log entries to represent the hours and percentages for the specific
     * time periods during that shift
     *
     * @param myRplan
     * @param symbol
     * @param parameterSet
     * @return
     */
    public static Workinglog[] createWorkingLogs(Rplan myRplan, Symbol symbol, ContractsParameterSet parameterSet) {
        LocalDate day = new LocalDate(myRplan.getStart());
        HashMap<String, BigDecimal> map = symbol.getHourStats(day, parameterSet);
        ArrayList<Workinglog> listLogs = new ArrayList<Workinglog>();

        grmpf;
        // U und X so gut ?

        if (map != null) {
            if (map.get(Symbol.DAYHOURS1).compareTo(BigDecimal.ZERO) > 0) {
                listLogs.add(new Workinglog(map.get(Symbol.DAYHOURS1), BigDecimal.ZERO, BigDecimal.ZERO, myRplan, WorkinglogTools.TYPE_AUTO_DAY));
            }
            if (map.get(Symbol.DAYHOURS2).compareTo(BigDecimal.ZERO) > 0) {
                listLogs.add(new Workinglog(map.get(Symbol.DAYHOURS2), BigDecimal.ZERO, BigDecimal.ZERO, myRplan, WorkinglogTools.TYPE_AUTO_DAY));
            }
            if (map.get(Symbol.NIGHTHOURS1).compareTo(BigDecimal.ZERO) > 0) {
                listLogs.add(new Workinglog(map.get(Symbol.NIGHTHOURS1), BigDecimal.ZERO, parameterSet.nightPremiumPercentage, myRplan, WorkinglogTools.TYPE_AUTO_NIGHT));
            }
            if (map.get(Symbol.NIGHTHOURS2).compareTo(BigDecimal.ZERO) > 0) {
                listLogs.add(new Workinglog(map.get(Symbol.NIGHTHOURS2), BigDecimal.ZERO, parameterSet.getNightPremiumPercentage(), myRplan, WorkinglogTools.TYPE_AUTO_NIGHT));
            }
            if (map.get(Symbol.HOLIHOURS1).compareTo(BigDecimal.ZERO) > 0) {
                listLogs.add(new Workinglog(map.get(Symbol.HOLIHOURS1), BigDecimal.ZERO, parameterSet.getHollidayPremiumPercentage(), myRplan, WorkinglogTools.TYPE_AUTO_HOLIDAY));
            }
            if (map.get(Symbol.HOLIHOURS2).compareTo(BigDecimal.ZERO) > 0) {
                listLogs.add(new Workinglog(map.get(Symbol.HOLIHOURS2), BigDecimal.ZERO, parameterSet.getHollidayPremiumPercentage(), myRplan, WorkinglogTools.TYPE_AUTO_HOLIDAY));
            }
            if (map.get(Symbol.EXTRA).compareTo(BigDecimal.ZERO) > 0) {
                listLogs.add(new Workinglog(BigDecimal.ZERO, map.get(Symbol.EXTRA), BigDecimal.ZERO, myRplan, WorkinglogTools.TYPE_AUTO_EXTRA));
            }
            if (map.get(Symbol.BREAKTIME).compareTo(BigDecimal.ZERO) > 0) {
                listLogs.add(new Workinglog(map.get(Symbol.BREAKTIME).negate(), BigDecimal.ZERO, BigDecimal.ZERO, myRplan, WorkinglogTools.TYPE_AUTO_BREAK));
            }
        }

//        symbol.getBaseHoursAsDecimalDay1(day);

        return listLogs.toArray(new Workinglog[]{});
        //myRplan, rosterParameters.getSymbol(myRplan.getEffectiveSymbol(), userContracts)
    }


}
