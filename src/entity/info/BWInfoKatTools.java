package entity.info;

import entity.verordnungen.Darreichung;
import op.OPDE;
import op.care.info.PnlInfo;
import op.tools.InternalClassACL;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 22.06.12
 * Time: 14:21
 * To change this template use File | Settings | File Templates.
 */
public class BWInfoKatTools {

    public static final int GRUNDPFLEGE = 100;
    public static final int HAUT = 110;
    public static final int VITAL = 120;
    public static final int VERWALTUNG = 1000;
    public static final int STAMMDATEN = 2000;


    public static ListCellRenderer getRenderer() {
        return new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                String text;
                if (o == null) {
                    text = SYSTools.toHTML("<i>Keine Auswahl</i>");
                } else if (o instanceof BWInfoKat) {
                    text = SYSTools.toHTML("<div id=\"fonttext\"><font color=\"#"+((BWInfoKat) o).getFgheader()+"\">"+((BWInfoKat) o).getBezeichnung()+"</font></div>");
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
            }
        };
    }

    /**
     * @return
     */
    public static List<BWInfoKat> getKategorien() {

        String katart = "0";   // a little trick. 0 is always viable

        katart += OPDE.getAppInfo().userHasAccessLevelForThisClass(PnlInfo.internalClassID, InternalClassACL.USER1) ? "," + STAMMDATEN : ""; // Stammdaten
        katart += OPDE.getAppInfo().userHasAccessLevelForThisClass(PnlInfo.internalClassID, InternalClassACL.USER2) ? "," + VERWALTUNG : ""; // Verwaltung

        // katart below 1000 is accessible for everyone
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM BWInfoKat b WHERE (b.katArt < 1000 OR b.katArt IN (" + katart + " )) AND b.sortierung >= 0");
        List<BWInfoKat> result = query.getResultList();
        em.close();
        Collections.sort(result);
        return result;
    }


}
