package entity.roster;

import op.tools.SYSTools;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 16.10.13
 * Time: 12:07
 * To change this template use File | Settings | File Templates.
 */
public class WorkinglogTools {

    public static String toPrettyString(Workinglog workinglog) {
        String text = "";
        if (workinglog.isActual()) {
            text = workinglog.getActual() + ": " + workinglog.getHours();
        } else {
            text = workinglog.getText() + ": " + workinglog.getHours();
        }

        return text;
    }


}
