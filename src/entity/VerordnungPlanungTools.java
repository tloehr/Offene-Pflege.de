package entity;

import op.tools.SYSTools;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 18.11.11
 * Time: 15:35
 * To change this template use File | Settings | File Templates.
 */
public class VerordnungPlanungTools {
    public static final int ZEIT = 0;
    public static final int UHRZEIT = 1;
    public static final int MAXDOSIS = 2;


    public static int getTerminStatus(VerordnungPlanung planung){
        int status = 0;
        if (planung.verwendetZeiten()){
            status = ZEIT;
        } else if (planung.verwendetMaximalDosis()){
            status = MAXDOSIS;
        } else {
            status = UHRZEIT;
        }
        return status;
    }

    public static String getValueAsString(BigDecimal bd){
        return (bd.compareTo(BigDecimal.ZERO) > 0 ? SYSTools.printDouble(bd.doubleValue()) : "--");
    }

}
