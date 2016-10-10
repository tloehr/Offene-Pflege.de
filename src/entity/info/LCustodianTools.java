package entity.info;

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
 * Date: 11.07.12
 * Time: 13:53
 * To change this template use File | Settings | File Templates.
 */
public class LCustodianTools {

    public static ListCellRenderer getRenderer() {
        return (jList, o, i, isSelected, cellHasFocus) -> {
            String text;
            if (o == null) {
                text = SYSTools.xx("misc.commands.>>noselection<<");
            } else if (o instanceof LCustodian) {
//                    text = ((LCustodian) o).getName() + ", " + ((LCustodian) o).getFirstname() + ", " + ((LCustodian) o).getCity();
                text = getFullName((LCustodian) o);
            } else {
                text = o.toString();
            }
            return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
        };
    }

    public static String getFullName(LCustodian lcustodian) {
        if (lcustodian != null) {
            return SYSTools.anonymizeString(lcustodian.getAnrede() + " " + lcustodian.getVorname() + " " + lcustodian.getName());
        } else {
            return SYSTools.xx("misc.msg.noentryyet");
        }
    }

    public static ArrayList<LCustodian> getAllActive() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM LCustodian b WHERE b.status >= 0 ORDER BY b.name, b.vorname");
        ArrayList<LCustodian> listLC = new ArrayList<LCustodian>(query.getResultList());
        em.close();

        return listLC;
    }

}
