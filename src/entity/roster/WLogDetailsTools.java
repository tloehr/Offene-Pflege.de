package entity.roster;

import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tloehr on 13.01.14.
 */
public class WLogDetailsTools {

    public static final int DAY1 = 0;
    public static final int NIGHT1 = 1;
    public static final int NIGHT2 = 2;
    public static final int DAY2 = 3;
    public static final int BREAK = 4; // negative value
    public static final int ADDITIONAL = 5;

    public static final String[] TYPES = new String[]{"Tag1", "Nacht1", "Nacht2", "Tag2", "Pause", "Zusätzlich"};


    public static String toPrettyString(WLogDetails wLogDetails) {
        String text = "";

        text = wLogDetails.getHours().setScale(2, RoundingMode.HALF_UP).toString();

        return text;
    }


    /**
     * when entering an actual work log, there could be separate necessary details to represent the hours and percentages for the specific
     * time periods during that shift
     *
     * @param myRplan must be merged to the EM prior to this method
     * @param symbol
     * @param parameterSet
     * @return
     */
    public static void setDetails(EntityManager em, Rplan myRplan, Symbol symbol, ContractsParameterSet parameterSet) throws Exception{
//        Rplan myRplan = em.merge(rplan);
        LocalDate day = new LocalDate(myRplan.getStart());
        HashMap<String, BigDecimal> map = symbol.getHourStats(day, parameterSet);
        myRplan.setActual(symbol.getKey());

        // first clean all existing automatic WLOGDETAILS
        if (!myRplan.getWLogDetails().isEmpty()) {
            ArrayList<WLogDetails> details2remove = new ArrayList();
            for (WLogDetails wLogDetails : myRplan.getWLogDetails()) {
                if (wLogDetails.getType() != ADDITIONAL) {
                    details2remove.add(wLogDetails);
                    em.remove(wLogDetails);
                }
            }

            myRplan.getWLogDetails().removeAll(details2remove);
        }

        // now add new ones
        if (map != null) {
            if (map.get(Symbol.DAYHOURS1).compareTo(BigDecimal.ZERO) > 0) {
                myRplan.getWLogDetails().add(new WLogDetails(map.get(Symbol.DAYHOURS1), BigDecimal.ZERO, DAY1, myRplan));
            }
            if (map.get(Symbol.DAYHOURS2).compareTo(BigDecimal.ZERO) > 0) {
                myRplan.getWLogDetails().add(new WLogDetails(map.get(Symbol.DAYHOURS2), BigDecimal.ZERO, DAY2, myRplan));
            }
            if (map.get(Symbol.NIGHTHOURS1).compareTo(BigDecimal.ZERO) > 0) {
                myRplan.getWLogDetails().add(new WLogDetails(map.get(Symbol.NIGHTHOURS1), parameterSet.getNightPremiumPercentage(), NIGHT1, myRplan));
            }
            if (map.get(Symbol.NIGHTHOURS2).compareTo(BigDecimal.ZERO) > 0) {
                myRplan.getWLogDetails().add(new WLogDetails(map.get(Symbol.NIGHTHOURS2), parameterSet.getNightPremiumPercentage(), NIGHT2, myRplan));
            }
            if (map.get(Symbol.BREAKTIME).compareTo(BigDecimal.ZERO) > 0) {
                myRplan.getWLogDetails().add(new WLogDetails(map.get(Symbol.BREAKTIME).negate(), BigDecimal.ZERO, BREAK, myRplan));
            }
        }


    }

}

