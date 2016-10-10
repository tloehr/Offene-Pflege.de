package entity.process;

import op.OPDE;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 16.06.11
 * Time: 14:07
 * To change this template use File | Settings | File Templates.
 */
public class PCatTools {

    public static final int PCAT_TYPE_MISC = 0;
    public static final int PCAT_TYPE_CARE = 1;
    public static final int PCAT_TYPE_BHP = 2;
    public static final int PCAT_TYPE_SOCIAL = 3;
    public static final int PCAT_TYPE_ADMIN = 4;
    public static final int PCAT_TYPE_COMPLAINT = 5;

    public static ListCellRenderer getRenderer() {
        return (jList, o, i, isSelected, cellHasFocus) -> {
            String text;
            if (o == null) {
                text = SYSTools.toHTMLForScreen(SYSTools.xx("misc.commands.>>noselection<<"));
            } else if (o instanceof PCat) {
                text = o.toString();
            } else {
                text = o.toString();
            }
            return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
        };
    }

    public static ArrayList<PCat> getPCats() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT pc FROM PCat pc ORDER BY pc.text");
        ArrayList<PCat> list = new ArrayList<PCat>(query.getResultList());
        em.close();
        return list;
    }
}
