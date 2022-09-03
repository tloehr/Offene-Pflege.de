package de.offene_pflege.entity.prescription;

import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA. User: tloehr Date: 14.12.11 Time: 13:30 To change this template use File | Settings | File
 * Templates.
 */
public class HospitalTools {

    public static ListCellRenderer getKHRenderer() {
        return (jList, o, i, isSelected, cellHasFocus) -> {
            String text;
            if (o == null) {
                text = SYSTools.xx("misc.commands.>>noselection<<");
            } else if (o instanceof Hospital) {
                text = ((Hospital) o).getName() + ", " + ((Hospital) o).getCity();
            } else {
                text = o.toString();
            }
            return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
        };
    }

    public static ArrayList<Hospital> getAll() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT a FROM Hospital a  ORDER BY a.name");
        ArrayList<Hospital> liste = new ArrayList<Hospital>(query.getResultList());
        em.close();

        return liste;

    }


    public static String getCompleteAddress(Hospital kh) {
        if (kh != null) {
            if (OPDE.isAnonym()) {
                return "[" + SYSTools.xx("misc.msg.anon") + "]";
            }
            return kh.getName() + ", " + kh.getStreet() + ", " + kh.getCity() + ", Tel: " + kh.getTel();
        } else {
            return SYSTools.xx("misc.msg.noentryyet");
        }
    }

    public static String get_for_order_list(Hospital h) {
        if (OPDE.isAnonym()) {
            return "[" + SYSTools.xx("misc.msg.anon") + "]";
        }
        return h.getName() + "<br/>" + h.getFax();
    }

    public static String getFullName(Hospital kh) {
        if (kh == null)
            return "--";


        if (OPDE.isAnonym()) {
            return "[" + SYSTools.xx("misc.msg.anon") + "]";
        }

        String string = kh.getName() + SYSTools.catchNull(kh.getCity(), ", ", "");
//        string += SYSTools.catchNull(kh.getTel(), SYSTools.xx("misc.msg.phone") + ": ", " ") + SYSTools.catchNull(kh.getFax(), SYSTools.xx("misc.msg.fax") + ": ", " ");
//        String string = kh.getName() + ", " + SYSTools.catchNull(kh.getStreet(), "", ", ") + SYSTools.catchNull(kh.getZIP(), "", " ") + SYSTools.catchNull(kh.getCity(), "", ", ");
//        string += SYSTools.catchNull(kh.getTel(), SYSTools.xx("misc.msg.phone") + ": ", " ") + SYSTools.catchNull(kh.getFax(), SYSTools.xx("misc.msg.fax") + ": ", " ");
        return string;
    }
}
