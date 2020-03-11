package de.offene_pflege.entity.prescription;


import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 01.12.11
 * Time: 17:17
 * To change this template use File | Settings | File Templates.
 */
public class DosageFormTools {
    public static final short STATE_UPR1 = 0;
    public static final short STATE_UPRn = 1;
    public static final short STATE_DONT_CALC = 2;
    public static final String[] UPR_STATES = new String[]{SYSTools.xx("state_upr1"), SYSTools.xx("state_uprn"), SYSTools.xx("state_dont_calc")};


    public static ArrayList<DosageForm> getAll() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT a FROM DosageForm a ");
        ArrayList<DosageForm> listDF = new ArrayList<DosageForm>(query.getResultList());
        em.close();

        return listDF;

    }


    /**
     * @param maxlen maximale Zeichenlänge pro Zeile. maxlen < 1 heisst egal.
     * @return
     */
    public static ListCellRenderer getRenderer(int maxlen) {
        final int max = maxlen;
        return (jList, o, i, isSelected, cellHasFocus) -> {
            String text;
            if (o == null) {
                text = SYSTools.toHTML("<i>" + SYSTools.xx("misc.commands.noselection") + "</i>");
            } else if (o instanceof DosageForm) {
                DosageForm form = (DosageForm) o;

                text = toPrettyString(form);
                text += ", " + SYSTools.xx("misc.msg.upr") + " " + UPR_STATES[form.getUPRState()];

            } else {
                text = o.toString();
            }
            if (max > 0) {
                text = SYSTools.left(text, max);
            }
            return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
        };
    }

    public static String getUsageText(DosageForm form) {
        String result = "";
        if (form == null) {
            result = "?";
        } else if (!form.getUsageText().isEmpty()) {
            result = form.getUsageText();
        } else {
            result = SYSConst.UNITS[form.getUsageUnit()];
        }

        return result;
    }

    public static String toPrettyString(DosageForm form) {
        return (SYSTools.catchNull(form.getPreparation()).isEmpty() ? form.getUsageText() : form.getPreparation() + SYSTools.catchNull(form.getUsageText(), ", ", ""));
    }

    public static String getPackageText(DosageForm form) {
        String result = "<null>";
        if (form != null) {
            if (SYSTools.catchNull(form.getPreparation()).isEmpty()) {
                result = SYSConst.UNITS[form.getPackUnit()] + " " + form.getUsageText();
            } else {
                result = SYSConst.UNITS[form.getPackUnit()] + " " + form.getPreparation();
            }
        }

        return result;
    }

}
