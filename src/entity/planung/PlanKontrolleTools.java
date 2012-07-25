package entity.planung;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 19.07.12
 * Time: 16:23
 * To change this template use File | Settings | File Templates.
 */
public class PlanKontrolleTools {

    public static String getAsHTML(PlanKontrolle kontrolle) {
        String result = "";
        DateFormat df = DateFormat.getDateInstance();
        result += "<b>" + df.format(kontrolle.getDatum()) + "</b>; <u>"+kontrolle.getUser().getNameUndVorname()+"</u>; "+kontrolle.getBemerkung() ;
//        result += "<p><b>Durchgeführt von:</b> " + kontrolle.getUser().getNameUndVorname() + "</p>";
//        result += "<p><b>Ergebnis:</b> " + kontrolle.getBemerkung() + "</p>";
        if (kontrolle.isAbschluss()) {
            result += "<u>Die Pflegeplanung wurde mit dieser Kontrolle geändert bzw. abgeschlossen</u>";
        }

        return result;
    }
}
