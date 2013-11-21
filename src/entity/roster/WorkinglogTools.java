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
    public static final int TYPE_AUTO_DAY1 = 0;
    public static final int TYPE_AUTO_DAY2 = 1;
    public static final int TYPE_AUTO_NIGHT1 = 2;
    public static final int TYPE_AUTO_NIGHT2 = 3;
    //    public static final int TYPE_AUTO_HOLIDAY = 2;
    public static final int TYPE_AUTO_BREAK = 4; // negative value
    //    public static final int TYPE_MANUAL_HOURS = 4;
//    public static final int TYPE_AUTO_EXTRA = 5; // additional hours for holiday shifts
    public static final String[] TYPES = new String[]{"Tag1", "Tag2", "Nacht1", "Nacht2", "Pause"};

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
    public static Workinglog[] createWorkingLogs(Rplan myRplan, Symbol symbol, ContractsParameterSet parameterSet, long actual) {
        LocalDate day = new LocalDate(myRplan.getStart());
        HashMap<String, BigDecimal> map = symbol.getHourStats(day, parameterSet);
        ArrayList<Workinglog> listLogs = new ArrayList<Workinglog>();

        if (map != null) {
            if (map.get(Symbol.DAYHOURS1).compareTo(BigDecimal.ZERO) > 0) {
                listLogs.add(new Workinglog(map.get(Symbol.DAYHOURS1), BigDecimal.ZERO, myRplan, WorkinglogTools.TYPE_AUTO_DAY1, actual));
            }
            if (map.get(Symbol.DAYHOURS2).compareTo(BigDecimal.ZERO) > 0) {
                listLogs.add(new Workinglog(map.get(Symbol.DAYHOURS2), BigDecimal.ZERO, myRplan, WorkinglogTools.TYPE_AUTO_DAY1, actual));
            }
            if (map.get(Symbol.NIGHTHOURS1).compareTo(BigDecimal.ZERO) > 0) {
                listLogs.add(new Workinglog(map.get(Symbol.NIGHTHOURS1), parameterSet.nightPremiumPercentage, myRplan, WorkinglogTools.TYPE_AUTO_NIGHT1, actual));
            }
            if (map.get(Symbol.NIGHTHOURS2).compareTo(BigDecimal.ZERO) > 0) {
                listLogs.add(new Workinglog(map.get(Symbol.NIGHTHOURS2), parameterSet.getNightPremiumPercentage(), myRplan, WorkinglogTools.TYPE_AUTO_NIGHT2, actual));
            }
            if (map.get(Symbol.BREAKTIME).compareTo(BigDecimal.ZERO) > 0) {
                listLogs.add(new Workinglog(map.get(Symbol.BREAKTIME).negate(), BigDecimal.ZERO, myRplan, WorkinglogTools.TYPE_AUTO_BREAK, actual));
            }
        }

        return listLogs.toArray(new Workinglog[]{});
    }


}
