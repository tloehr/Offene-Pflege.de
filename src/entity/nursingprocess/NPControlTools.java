package entity.nursingprocess;

import op.OPDE;
import op.care.nursingprocess.PnlNursingProcess;
import op.tools.SYSTools;

import java.text.DateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 19.07.12
 * Time: 16:23
 * To change this template use File | Settings | File Templates.
 */
public class NPControlTools {

    public static String getAsHTML(NPControl npcontrol) {
        String result = "";
        DateFormat df = DateFormat.getDateInstance();
        result += "<b>" + df.format(npcontrol.getDatum()) + "</b>; <u>"+npcontrol.getUser().getFullname()+"</u>; "+npcontrol.getBemerkung() ;
//        result += "<p><b>Durchgeführt von:</b> " + kontrolle.getUser().getFullname() + "</p>";
//        result += "<p><b>Ergebnis:</b> " + kontrolle.getText() + "</p>";
        if (npcontrol.isLastValidation()) {
            result += "<br/><b>"+ SYSTools.xx(PnlNursingProcess.internalClassID + ".isClosedAfterThisNPControl")+"</b>";
        }

        return result;
    }
}
