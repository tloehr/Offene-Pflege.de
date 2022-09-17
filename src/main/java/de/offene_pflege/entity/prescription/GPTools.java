package de.offene_pflege.entity.prescription;

import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA. User: tloehr Date: 14.12.11 Time: 13:27 To change this template use File | Settings | File
 * Templates.
 */
public class GPTools {

    public static ListCellRenderer getRenderer() {
        return (jList, o, i, isSelected, cellHasFocus) -> {
            String text;
            if (o == null) {
                text = SYSTools.xx("misc.commands.>>noselection<<");
            } else if (o instanceof GP) {
//                    text = ((GP) o).getName() + ", " + ((GP) o).getFirstname() + ", " + ((GP) o).getCity();
                text = getFullName((GP) o);
            } else {
                text = o.toString();
            }
            return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
        };
    }

    public static String getFullName(GP doc) {
        if (doc != null) {
            if (OPDE.isAnonym()) {
                return "[" + SYSTools.xx("misc.msg.anon") + "]";
            }
            return doc.getAnrede() + " " + SYSTools.catchNull(doc.getTitle(), "", " ") + doc.getName() + " " + doc.getFirstname() + ", " + doc.getCity();
        } else {
            return SYSTools.xx("misc.msg.noentryyet");
        }
    }

    public static String get_for_order_list(GP doc) {
        return doc.getAnrede() + " " + SYSTools.catchNull(doc.getTitle(), "", " ") + SYSTools.anonymizeName(doc.getName(), SYSTools.INDEX_LASTNAME) + "<br/>" + SYSTools.anonymizeString(doc.getFax());
    }

    public static String getCompleteAddress(GP doc) {
        if (doc != null) {
            if (OPDE.isAnonym()) {
                return "[" + SYSTools.xx("misc.msg.anon") + "]";
            }
            return doc.getAnrede() + " " + SYSTools.catchNull(doc.getTitle(), "", " ") + doc.getFirstname() + " " + doc.getName() + ", " + doc.getStreet() + ", " + doc.getZIP() + " " + doc.getCity() + ", Tel: " + doc.getTel();
        } else {
            return SYSTools.xx("misc.msg.noentryyet");
        }
    }


    public static ArrayList<GP> getAllActive() {
        EntityManager em = OPDE.createEM();
        Query queryArzt = em.createQuery("SELECT a FROM GP a WHERE a.status >= 0 ORDER BY a.name, a.vorname");
        ArrayList<GP> listAerzte = new ArrayList<GP>(queryArzt.getResultList());
        em.close();

        return listAerzte;

    }

    public static ArrayList<GP> getAllActiveNeurologist() {
        EntityManager em = OPDE.createEM();
        Query queryGP = em.createQuery("SELECT a FROM GP a WHERE a.status >= 0 AND a.neurologist = TRUE ORDER BY a.name, a.vorname");
        ArrayList<GP> listGPs = new ArrayList<GP>(queryGP.getResultList());

        em.close();

        return listGPs;

    }
}
