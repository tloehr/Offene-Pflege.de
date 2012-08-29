/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.process;

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
    public static final String PREPORT_TEXT_SET_OWNERSHIP = PnlProcess.internalClassID+".preport.text.setowner";
    public static final String PREPORT_TEXT_REOPEN = PnlProcess.internalClassID+".preport.text.reopen";
    public static final String PREPORT_TEXT_CLOSE = PnlProcess.internalClassID+".preport.text.close";
    public static final String PREPORT_TEXT_WV = PnlProcess.internalClassID+".preport.text.revision";
    public static final short PREPORT_TYPE_REOPEN = 6;
    public static final short PREPORT_TYPE_EDIT = 7;
    public static final short PREPORT_TYPE_WV = 8;
    public static final String[] PREPORT_TYPES = {"Benutzerbericht", "SYS Zuordnung Element", "SYS Entfernung Element", "SYS Eigentümer geändert", "SYS Vorgang erstellt", "SYS Vorgang geschlossen", "SYS Vorgang wieder geöffnet", "SYS Vorgang bearbeitet", "SYS Wiedervorlage gesetzt"};
    public static final String[] PREPORT_TEXTS = new String[]{"misc.msg.Time.long", "misc.msg.earlyinthemorning.long", "misc.msg.morning.long", "misc.msg.noon.long", "misc.msg.afternoon.long", "misc.msg.evening.long", "misc.msg.lateatnight.long"};


    public static String getBerichtAsHTML(PReport bericht) {
        String html = "";
        html += "<b>Vorgangsbericht</b>";
        html += "<p>" + bericht.getText() + "</p>";
        return html;
    }

    public static String getPITAsHTML(PReport bericht) {
        DateFormat df = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");
        String html = "";
        html += df.format(bericht.getPit()) + "; " + bericht.getUser().getFullname();
        return html;
    }

}
