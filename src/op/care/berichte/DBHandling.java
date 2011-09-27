/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package op.care.berichte;

import entity.Bewohner;
import entity.Pflegeberichte;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import javax.persistence.Query;
import op.OPDE;
import op.tools.DBRetrieve;
import op.tools.DlgException;
import op.tools.SYSCalendar;
import op.tools.SYSTools;

/**
 *
 * @author tloehr
 */
public class DBHandling {

    

 

    public static String getBerichtAsHTML(long tbid) {
        HashMap hm = DBRetrieve.getSingleRecord("Tagesberichte", "TBID", tbid);
        return getBerichtAsHTML(hm.get("Text").toString(), hm.get("UKennung").toString(), hm.get("BWKennung").toString(), hm.get("EKennung").toString(), false, (Date) hm.get("PIT"));
    }

    public static String getBerichtAsHTML(String text, String ukennung, String bwkennung, String ekennung, boolean mitBWKennung, Date pit) {
        String html = "";
        text = SYSTools.replace(text, "\n", "<br/>");
        if (ekennung.equals("")) {
            if (mitBWKennung) {
                html += "<b>Pflegebericht für " + SYSTools.getBWLabel(bwkennung) + "</b>";
            } else {
                html += "<b>Pflegebericht</b>";
            }
        } else {
            html += "<b>Stationsbucheintrag für die Einrichtung '" + ekennung + "'</b>";
        }
        String name = DBRetrieve.getUsername(ukennung);
        html += "<p>" + text + "</p>";

        return html;
    }

//    public static String getBerichteAsHTML(TMPflegeberichte tm, Bewohner bewohner, int[] sel) {
//        String html = "";
//
//        html += "<h1>Pflegeberichte für " + SYSTools.getBWLabel(bewohner) + "</h1>";
//
//        int num = tm.getRowCount();
//        if (num > 0) {
//            html += "<table border=\"1\" cellspacing=\"0\"><tr>"
//                    + "<th>Info</th><th>Text</th></tr>";
//            for (int v = 0; v < num; v++) {
//                if (sel == null || Arrays.binarySearch(sel, v) > -1) {
//                    Pflegeberichte bericht = (Pflegeberichte) tm.getValueAt(v, TMPflegeberichte.COL_BERICHT);
//                    html += "<tr>";
//                    html += "<td>" + bericht.getDatumUndUser(false) + "</td>";
//                    html += "<td>" + bericht.getAsHTML() + "</td>";
//                    html += "</tr>";
//                }
//            }
//            html += "</table>";
//        } else {
//            html += "<i>keine Berichte in der Auswahl vorhanden</i>";
//        }
//
//        html = "<html><head>"
//                + "<title>" + SYSTools.getWindowTitle("") + "</title>"
//                + "<script type=\"text/javascript\">"
//                + "window.onload = function() {"
//                + "window.print();"
//                + "}</script></head><body>" + html + "</body></html>";
//        return html;
//    }

//    public static String getBerichteAsHTML(TMPflegeberichte tm, String bwkennung, int[] sel) {
//
//        String html = "";
//
//        html += "<h1>Pflegeberichte für " + SYSTools.getBWLabel(bwkennung) + "</h1>";
//
//        int num = tm.getRowCount();
//        if (num > 0) {
//            html += "<table border=\"1\" cellspacing=\"0\"><tr>"
//                    + "<th>Info</th><th>Text</th></tr>";
//            for (int v = 0; v < num; v++) {
//                if (sel == null || Arrays.binarySearch(sel, v) > -1) {
//                    html += "<tr>";
//                    html += "<td>" + tm.getValueAt(v, TMBerichte.COL_PIT).toString() + "</td>";
//                    html += "<td>" + tm.getValueAt(v, TMBerichte.COL_INFO).toString() + "</td>";
//                    html += "</tr>";
//                }
//            }
//            html += "</table>";
//        } else {
//            html += "<i>keine Berichte in der Auswahl vorhanden</i>";
//        }
//
//        html = "<html><head>"
//                + "<title>" + SYSTools.getWindowTitle("") + "</title>"
//                + "<script type=\"text/javascript\">"
//                + "window.onload = function() {"
//                + "window.print();"
//                + "}</script></head><body>" + html + "</body></html>";
//        return html;
//    }


}
