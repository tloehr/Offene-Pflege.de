package entity.roster;

import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    public static final int TYPE_AUTO_NIGHT1 = 1;
    public static final int TYPE_AUTO_NIGHT2 = 2;
    public static final int TYPE_AUTO_DAY2 = 3;
    public static final int TYPE_AUTO_BREAK = 4; // negative value
    public static final int TYPE_ADDITIONAL = 5;
    public static final int TYPE_MANUAL = 6;
    public static final int TYPE_TIMECLOCK = 7; // Stechuhr
    public static final String[] TYPES = new String[]{"Tag1", "Nacht1", "Nacht2", "Tag2", "Pause", "Zusätzlich", "Manuell", "Zeiterfassung"};


    public static final int STATE_UNUSED = 0;
    public static final int STATE_REJECTED = 1;
    public static final int STATE_ACCEPTED = 2;

    public static String toPrettyString(WLog WLog) {
        String text = "";

        text = WLog.getHours().setScale(2, RoundingMode.HALF_UP).toString();

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
    public static WLog[] createWorkingLogs(Rplan myRplan, Symbol symbol, ContractsParameterSet parameterSet) {
        LocalDate day = new LocalDate(myRplan.getStart());
        HashMap<String, BigDecimal> map = symbol.getHourStats(day, parameterSet);
        ArrayList<WLog> listLogs = new ArrayList<WLog>();

        if (map != null) {
            if (map.get(Symbol.DAYHOURS1).compareTo(BigDecimal.ZERO) > 0) {
                listLogs.add(new WLog(map.get(Symbol.DAYHOURS1), BigDecimal.ZERO, symbol.getKey(), myRplan.getEffectiveHome(), myRplan, WorkinglogTools.TYPE_AUTO_DAY1));
            }
            if (map.get(Symbol.DAYHOURS2).compareTo(BigDecimal.ZERO) > 0) {
                listLogs.add(new WLog(map.get(Symbol.DAYHOURS2), BigDecimal.ZERO, symbol.getKey(), myRplan.getEffectiveHome(), myRplan, WorkinglogTools.TYPE_AUTO_DAY2));
            }
            if (map.get(Symbol.NIGHTHOURS1).compareTo(BigDecimal.ZERO) > 0) {
                listLogs.add(new WLog(map.get(Symbol.NIGHTHOURS1), parameterSet.nightPremiumPercentage, symbol.getKey(), myRplan.getEffectiveHome(), myRplan, WorkinglogTools.TYPE_AUTO_NIGHT1));
            }
            if (map.get(Symbol.NIGHTHOURS2).compareTo(BigDecimal.ZERO) > 0) {
                listLogs.add(new WLog(map.get(Symbol.NIGHTHOURS2), parameterSet.getNightPremiumPercentage(), symbol.getKey(), myRplan.getEffectiveHome(), myRplan, WorkinglogTools.TYPE_AUTO_NIGHT2));
            }
            if (map.get(Symbol.BREAKTIME).compareTo(BigDecimal.ZERO) > 0) {
                listLogs.add(new WLog(map.get(Symbol.BREAKTIME).negate(), BigDecimal.ZERO, symbol.getKey(), myRplan.getEffectiveHome(), myRplan, WorkinglogTools.TYPE_AUTO_BREAK));
            }
        }

        return listLogs.toArray(new WLog[]{});
    }


//    public static WLog getAdditional1(Rplan rplan) {
//        WLog workinglog = null;
//        for (WLog wlog : rplan.getWLogs()) {
//            if (wlog.getType() == TYPE_ADDITIONAL1) {
//                workinglog = wlog;
//                break;
//            }
//        }
//        return workinglog;
//    }
//
//    public static WLog getAdditional2(Rplan rplan) {
//        WLog workinglog = null;
//        for (WLog wlog : rplan.getWLogs()) {
//            if (wlog.getType() == TYPE_ADDITIONAL2) {
//                workinglog = wlog;
//                break;
//            }
//        }
//        return workinglog;
//    }


}
