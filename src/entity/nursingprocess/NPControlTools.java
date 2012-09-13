package entity.nursingprocess;

import java.text.DateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 19.07.12
 * Time: 16:23
 * To change this template use File | Settings | File Templates.
 */
public class NPControlTools {

    public static String getAsHTML(NPControl kontrolle) {
        String result = "";
        DateFormat df = DateFormat.getDateInstance();
        result += "<b>" + df.format(kontrolle.getDatum()) + "</b>; <u>"+kontrolle.getUser().getFullname()+"</u>; "+kontrolle.getBemerkung() ;
//        result += "<p><b>Durchgeführt von:</b> " + kontrolle.getUser().getFullname() + "</p>";
//        result += "<p><b>Ergebnis:</b> " + kontrolle.getText() + "</p>";
        if (kontrolle.isAbschluss()) {
            result += "<u>Die Pflegeplanung wurde mit dieser Kontrolle geändert bzw. abgeschlossen</u>";
        }

        return result;
    }
}
