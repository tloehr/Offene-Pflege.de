/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.process;

import op.OPDE;
import op.tools.SYSTools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author tloehr
 */
public class PReportTools {

    public static final short PREPORT_TYPE_USER = 0;
    public static final short PREPORT_TYPE_ASSIGN_ELEMENT = 1;
    public static final String PREPORT_TEXT_ASSIGN_ELEMENT = "nursingrecords.qprocesses.preport.text.assign";
    public static final short PREPORT_TYPE_REMOVE_ELEMENT = 2;
    public static final String PREPORT_TEXT_REMOVE_ELEMENT = "nursingrecords.qprocesses.preport.text.remove";
    public static final short PREPORT_TYPE_SET_OWNERSHIP = 3;
    public static final short PREPORT_TYPE_CREATE = 4;
    public static final String PREPORT_TEXT_CREATE = "nursingrecords.qprocesses.preport.text.create";
    public static final short PREPORT_TYPE_CLOSE = 5;
    public static final String PREPORT_TEXT_SET_OWNERSHIP = "nursingrecords.qprocesses.preport.text.setowner";
    public static final String PREPORT_TEXT_TAKE_OWNERSHIP = "nursingrecords.qprocesses.preport.text.takeowner";
    public static final String PREPORT_TEXT_REOPEN = "nursingrecords.qprocesses.preport.text.reopen";
    public static final String PREPORT_TEXT_CLOSE = "nursingrecords.qprocesses.preport.text.close";
    public static final String PREPORT_TEXT_WV = "nursingrecords.qprocesses.preport.text.revision";
    public static final short PREPORT_TYPE_REOPEN = 6;
    public static final short PREPORT_TYPE_EDIT = 7;
    public static final short PREPORT_TYPE_WV = 8;
    public static final short PREPORT_TYPE_TAKE_OWNERSHIP = 9;
    public static final short PREPORT_TYPE_SET_PDCA_PLAN = 10;
    public static final short PREPORT_TYPE_SET_PDCA_DO = 11;
    public static final short PREPORT_TYPE_SET_PDCA_CHECK = 12;
    public static final short PREPORT_TYPE_SET_PDCA_ACT = 13;
    public static final String PREPORT_TEXT_PDCA_PLAN = "nursingrecords.qprocesses.preport.pdca.plan";
    public static final String PREPORT_TEXT_PDCA_DO = "nursingrecords.qprocesses.preport.pdca.do";
    public static final String PREPORT_TEXT_PDCA_CHECK = "nursingrecords.qprocesses.preport.pdca.check";
    public static final String PREPORT_TEXT_PDCA_ACT = "nursingrecords.qprocesses.preport.pdca.act";

//    public static final String[] PREPORT_TYPES = {"Benutzerbericht", "SYS Zuordnung Element", "SYS Entfernung Element", "SYS Eigentümer geändert", "SYS Vorgang erstellt", "SYS Vorgang geschlossen", "SYS Vorgang wieder geöffnet", "SYS Vorgang bearbeitet", "SYS Wiedervorlage gesetzt"};
//    public static final String[] PREPORT_TEXTS = new String[]{"misc.msg.Time.long", "misc.msg.earlyinthemorning.long", "misc.msg.morning.long", "misc.msg.noon.long", "misc.msg.afternoon.long", "misc.msg.evening.long", "misc.msg.lateatnight.long"};


    public static String getBerichtAsHTML(PReport preport) {

        if (preport.isPDCA()) return "<b>" + getPDCA(preport) + "</b>";

        String html = "";
        html += "<b>" + OPDE.lang.getString("nursingrecords.qprocesses.preport") + "</b>";
        html += "<p>" + SYSTools.replace(preport.getText(), "\n", "<br/>", true) + "</p>";
        return html;
    }

    public static String getPITAsHTML(PReport bericht) {
        DateFormat df = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");
        String html = "";
        html += df.format(bericht.getPit()) + "; " + bericht.getUser().getFullname();
        return html;
    }

    public static String getPDCA(PReport preport) {

        if (preport.getArt() == PREPORT_TYPE_SET_PDCA_PLAN) {
            return OPDE.lang.getString(PReportTools.PREPORT_TEXT_PDCA_PLAN);
        }
        if (preport.getArt() == PREPORT_TYPE_SET_PDCA_DO) {
            return OPDE.lang.getString(PReportTools.PREPORT_TEXT_PDCA_DO);
        }
        if (preport.getArt() == PREPORT_TYPE_SET_PDCA_CHECK) {
            return OPDE.lang.getString(PReportTools.PREPORT_TEXT_PDCA_CHECK);
        }
        if (preport.getArt() == PREPORT_TYPE_SET_PDCA_ACT) {
            return OPDE.lang.getString(PReportTools.PREPORT_TEXT_PDCA_ACT);
        }
        return "";
    }

}
