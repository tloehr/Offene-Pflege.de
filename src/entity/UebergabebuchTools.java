/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import entity.nursingprocess.DFNTools;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSTools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author tloehr
 */
public class UebergabebuchTools {

    public static String getDatumUndUser(Uebergabebuch bericht, boolean showIDs) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");
        result = sdf.format(bericht.getPit()) + "; " + bericht.getUser().getFullname();
        if (showIDs) {
            result += "<br/><i>(" + bericht.getUebid() + ")</i>";
        }
        return "<font " + getHTMLColor(bericht) + ">" + result + "</font>";
    }

    private static String getHTMLColor(Uebergabebuch bericht) {
        return OPDE.getProps().getProperty(DFNTools.SHIFT_KEY_TEXT[SYSCalendar.whatShiftIs(bericht.getPit())] + "_FGBHP");
    }

    /**
     * gibt eine HTML Darstellung des Berichtes zurück.
     *
     * @return
     */
    public static String getAsHTML(Uebergabebuch bericht) {
        String result = "";

        String fonthead = "<font " + getHTMLColor(bericht) + ">";

        DateFormat df = DateFormat.getDateTimeInstance();
        //result += (flags.equals("") ? "" : "<b>" + flags + "</b><br/>");

        result += SYSTools.replace(bericht.getText(), "\n", "<br/>");
        result = fonthead + result + "</font>";
        return result;
    }

    /**
     * gibt eine HTML Darstellung des Einrichtungsnamen zurück.
     *
     * @return
     */
    public static String getEinrichtungAsHTML(Uebergabebuch bericht) {
        String result = "";

        String fonthead = "<font " + getHTMLColor(bericht) + ">";
        result += bericht.getEinrichtung().getBezeichnung();
        result = fonthead + result + "</font>";
        return result;
    }
}
