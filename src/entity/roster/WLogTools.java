package entity.roster;

import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 16.10.13
 * Time: 12:07
 * To change this template use File | Settings | File Templates.
 */
public class WLogTools {


//    public static String toPrettyString(WLog WLog) {
//        String text = "";
//
//        //text = WLog.getHours().setScale(2, RoundingMode.HALF_UP).toString();
//
//        return text;
//    }
//

    /**
     * when entering an actual work log, there could be separate necessary details to represent the hours and percentages for the specific
     * time periods during that shift
     *
     * @param myRplan
     * @param symbol
     * @param parameterSet
     * @return
     */
//    public static WLog createWorkingLog(Rplan myRplan, Symbol symbol, ContractsParameterSet parameterSet) {
//        LocalDate day = new LocalDate(myRplan.getStart());
//        HashMap<String, BigDecimal> map = symbol.getHourStats(day, parameterSet);
//        WLog wlog = new WLog(myRplan, symbol.getKey(), myRplan.getEffectiveHome());
//
//        if (map != null) {
//            if (map.get(Symbol.DAYHOURS1).compareTo(BigDecimal.ZERO) > 0) {
//                wlog.getWLogDetails().add(new WLogDetails(map.get(Symbol.DAYHOURS1), BigDecimal.ZERO, WLogDetailsTools.DAY1, wlog));
//            }
//            if (map.get(Symbol.DAYHOURS2).compareTo(BigDecimal.ZERO) > 0) {
//                wlog.getWLogDetails().add(new WLogDetails(map.get(Symbol.DAYHOURS2), BigDecimal.ZERO, WLogDetailsTools.DAY2, wlog));
//            }
//            if (map.get(Symbol.NIGHTHOURS1).compareTo(BigDecimal.ZERO) > 0) {
//                wlog.getWLogDetails().add(new WLogDetails(map.get(Symbol.NIGHTHOURS1), parameterSet.getNightPremiumPercentage(), WLogDetailsTools.NIGHT1, wlog));
//            }
//            if (map.get(Symbol.NIGHTHOURS2).compareTo(BigDecimal.ZERO) > 0) {
//                wlog.getWLogDetails().add(new WLogDetails(map.get(Symbol.NIGHTHOURS2), parameterSet.getNightPremiumPercentage(), WLogDetailsTools.NIGHT2, wlog));
//            }
//            if (map.get(Symbol.BREAKTIME).compareTo(BigDecimal.ZERO) > 0) {
//                wlog.getWLogDetails().add(new WLogDetails(map.get(Symbol.BREAKTIME).negate(), BigDecimal.ZERO, WLogDetailsTools.BREAK, wlog));
//            }
//        }
//
//        return wlog;
//    }




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
