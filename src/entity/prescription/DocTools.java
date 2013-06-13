package entity.prescription;

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
 * Date: 14.12.11
 * Time: 13:27
 * To change this template use File | Settings | File Templates.
 */
public class DocTools {

    public static ListCellRenderer getRenderer() {
        return new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                String text;
                if (o == null) {
                    text = OPDE.lang.getString("misc.commands.>>noselection<<");
                } else if (o instanceof Doc) {
//                    text = ((Doc) o).getName() + ", " + ((Doc) o).getFirstname() + ", " + ((Doc) o).getCity();
                    text = getFullName((Doc) o);
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
            }
        };
    }

    public static String getFullName(Doc doc) {
        if (doc != null) {
            if (OPDE.isAnonym()) {
                return "[" + OPDE.lang.getString("misc.msg.anon") + "]";
            }
            return doc.getAnrede() + " " + SYSTools.catchNull(doc.getTitle(), "", " ") + doc.getName() + " " + doc.getFirstname() + ", " + doc.getCity();
        } else {
            return OPDE.lang.getString("misc.msg.noentryyet");
        }
    }

    public static String getCompleteAddress(Doc doc) {
        if (doc != null) {
            if (OPDE.isAnonym()) {
                return "[" + OPDE.lang.getString("misc.msg.anon") + "]";
            }
            return doc.getAnrede() + " " + SYSTools.catchNull(doc.getTitle(), "", " ") + doc.getFirstname() + " " + doc.getName() + ", " + doc.getStreet() + ", " + doc.getZIP() + " " + doc.getCity() + ", Tel: " + doc.getTel();
        } else {
            return OPDE.lang.getString("misc.msg.noentryyet");
        }
    }


    public static ArrayList<Doc> getAllActive() {
        EntityManager em = OPDE.createEM();
        Query queryArzt = em.createQuery("SELECT a FROM Doc a WHERE a.status >= 0 ORDER BY a.name, a.vorname");
        ArrayList<Doc> listAerzte = new ArrayList<Doc>(queryArzt.getResultList());
        em.close();

        return listAerzte;

    }
}
