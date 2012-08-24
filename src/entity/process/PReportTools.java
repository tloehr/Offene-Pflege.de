/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.process;

import entity.EntityTools;
import op.process.PnlProcess;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author tloehr
 */
public class PReportTools {

    public static final short PREPORT_TYPE_USER = 0;
    public static final short PREPORT_TYPE_ASSIGN_ELEMENT = 1;
    public static final short PREPORT_TYPE_REMOVE_ELEMENT = 2;
    public static final String PREPORT_TEXT_REMOVE_ELEMENT = PnlProcess.internalClassID+".preport.text.remove";
    public static final short PREPORT_TYPE_SET_OWNERSHIP = 3;
    public static final short PREPORT_TYPE_CREATE = 4;
    public static final String PREPORT_TEXT_CREATE = PnlProcess.internalClassID+".preport.text.create";
    public static final short PREPORT_TYPE_CLOSE = 5;
    public static final String PREPORT_TEXT_CLOSE = PnlProcess.internalClassID+".preport.text.close";
    public static final short PREPORT_TYPE_REOPEN = 6;
    public static final short PREPORT_TYPE_EDIT = 7;
    public static final short PREPORT_TYPE_WV = 8;
    public static final String[] PREPORT_TYPES = {"Benutzerbericht", "SYS Zuordnung Element", "SYS Entfernung Element", "SYS Eigentümer geändert", "SYS Vorgang erstellt", "SYS Vorgang geschlossen", "SYS Vorgang wieder geöffnet", "SYS Vorgang bearbeitet", "SYS Wiedervorlage gesetzt"};
    public static final String[] PREPORT_TEXTS = new String[]{"misc.msg.Time.long", "misc.msg.earlyinthemorning.long", "misc.msg.morning.long", "misc.msg.noon.long", "misc.msg.afternoon.long", "misc.msg.evening.long", "misc.msg.lateatnight.long"};


    public static String getBerichtAsHTML(PReport bericht) {
        String html = "";
        html += "<b>Vorgangsbericht</b>";
        if (bericht.getArt() > 0) {
            html += " <font color=\"blue\"><i>" + PREPORT_TYPES[bericht.getArt()] + "</i></font>";
        }

        html += "<p>" + bericht.getText() + "</p>";
        return html;
    }

    public static String getPITAsHTML(PReport bericht) {
        DateFormat df = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");
        String html = "";
        if (bericht.getArt() != PREPORT_TYPE_USER) {
            html += "<font color=\"blue\">";
        }
        html += df.format(bericht.getPit()) + "; " + bericht.getUser().getNameUndVorname();
        if (bericht.getArt() != PREPORT_TYPE_USER) {
            html += "</font>";
        }
        return html;
    }

    public static void newBericht(QProcess vorgang, String text, short art) {
        PReport vbericht = new PReport(text, art, vorgang);
        EntityTools.persist(vbericht);
    }
}
