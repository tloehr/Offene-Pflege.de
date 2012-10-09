package entity.info;

import op.OPDE;
import op.care.info.PnlInfo;
import op.system.InternalClassACL;
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
public class ResInfoCategoryTools {

    public static final int GRUNDPFLEGE = 100;
    public static final int HAUT = 110;
    public static final int VITAL = 120;
    public static final int VERWALTUNG = 1000;
    public static final int STAMMDATEN = 2000;


//    public static ListCellRenderer getRenderer() {
//        return new ListCellRenderer() {
//            @Override
//            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
//                String text;
//                if (o == null) {
//                    text = SYSTools.toHTML("<i>Keine Auswahl</i>");
//                } else if (o instanceof ResInfoCategory) {
//                    text = SYSTools.toHTML("<div id=\"fonttext\"><font color=\"#"+((ResInfoCategory) o).getFgheader()+"\">"+((ResInfoCategory) o).getText()+"</font></div>");
//                } else {
//                    text = o.toString();
//                }
//                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
//            }
//        };
//    }

    /**
     * @return
     */
    public static List<ResInfoCategory> getAll4NP() {
        // katart below 1000 is accessible for everyone
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfoCategory b WHERE b.catType < 1000 AND b.sort >= 0 ORDER BY b.text");
        List<ResInfoCategory> result = query.getResultList();
        em.close();
        return result;
    }

    /**
     * @return
     */
    public static List<ResInfoCategory> getAll4ResInfo() {
        long begin = System.currentTimeMillis();
        String katart = "0";   // a little trick. 0 is always viable

        katart += OPDE.getAppInfo().userHasAccessLevelForThisClass(PnlInfo.internalClassID, InternalClassACL.USER1) ? "," + STAMMDATEN : ""; // Stammdaten
        katart += OPDE.getAppInfo().userHasAccessLevelForThisClass(PnlInfo.internalClassID, InternalClassACL.USER2) ? "," + VERWALTUNG : ""; // Verwaltung

        // katart below 1000 is accessible for everyone
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT DISTINCT b FROM ResInfoCategory b JOIN b.resInfoTypes t WHERE (b.catType < 1000 OR b.catType IN (" + katart + " )) AND b.sort >= 0 ORDER BY b.text ");
        List<ResInfoCategory> result = query.getResultList();
        em.close();
        SYSTools.showTimeDifference(begin);
//        Collections.sort(result);
        return result;
    }


}
