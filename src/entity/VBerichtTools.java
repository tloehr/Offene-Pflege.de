/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import op.OPDE;

/**
 *
 * @author tloehr
 */
public class VBerichtTools {

    public static final short VBERICHT_ART_USER = 0;
    public static final short VBERICHT_ART_ASSIGN_ELEMENT = 1;
    public static final short VBERICHT_ART_REMOVE_ELEMENT = 2;
    public static final short VBERICHT_ART_SET_OWNERSHIP = 3;
    public static final short VBERICHT_ART_CREATE = 4;
    public static final short VBERICHT_ART_CLOSE = 5;
    public static final short VBERICHT_ART_REOPEN = 6;
    public static final short VBERICHT_ART_EDIT = 7;
    public static final short VBERICHT_ART_WV = 8;
    public static final String[] VBERICHT_ARTEN = {"Benutzerbericht", "SYS Zuordnung Element", "SYS Entfernung Element", "SYS Eigentümer geändert", "SYS Vorgang erstellt", "SYS Vorgang geschlossen", "SYS Vorgang wieder geöffnet", "SYS Vorgang bearbeitet", "SYS Wiedervorlage gesetzt"};

    public static String getBerichtAsHTML(VBericht bericht) {
        String html = "";
        html += "<b>Vorgangsbericht</b>";
        if (bericht.getArt() > 0) {
            html += " <font color=\"blue\"><i>" + VBERICHT_ARTEN[bericht.getArt()] + "</i></font>";
        }

        html += "<p>" + bericht.getText() + "</p>";
        return html;
    }

    public static String getPITAsHTML(VBericht bericht) {
        DateFormat df = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");
        String html = "";
        if (bericht.getArt() != VBERICHT_ART_USER) {
            html += "<font color=\"blue\">";
        }
        html += df.format(bericht.getPit()) + "; " + bericht.getUser().getNameUndVorname();
        if (bericht.getArt() != VBERICHT_ART_USER) {
            html += "</font>";
        }
        return html;
    }

    public static void newBericht(Vorgaenge vorgang, String text, short art){
        OPDE.getEM().getTransaction().begin();
        VBericht vbericht = new VBericht(text, art, vorgang);
        OPDE.getEM().persist(vbericht);
        OPDE.getEM().getTransaction().commit();
    }
}
