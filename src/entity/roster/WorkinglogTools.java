package entity.roster;

import org.joda.time.LocalDate;

import java.util.ArrayList;

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
    public static final int TYPE_AUTO_BREAK = 3;
    public static final int TYPE_MANUAL_HOURS = 4;


    public static String toPrettyString(Workinglog workinglog) {
        String text = "";
//        if (workinglog.isActual()) {
//            text = workinglog.getActual() + ": " + workinglog.getHours();
//        } else {
//            text = workinglog.getText() + ": " + workinglog.getHours();
//        }

        return text;
    }


    /**
     * when entering an actual work log, the could be separate log entries to represent the hours and percentages for the specific
     * time periods during that shift
     *
     * @param myRplan
     * @param symbol
     * @param userContracts
     * @return
     */
    public static Workinglog[] createWorkingLogs(Rplan myRplan, Symbol symbol, UserContracts userContracts) {




        ArrayList<Workinglog> listLogs = new ArrayList<Workinglog>();
        LocalDate day = new LocalDate(myRplan.getStart());

//        symbol.getBaseHoursAsDecimalDay1(day);

        return listLogs.toArray(new Workinglog[]{});
        //myRplan, rosterParameters.getSymbol(myRplan.getEffectiveSymbol(), userContracts)
    }


}
