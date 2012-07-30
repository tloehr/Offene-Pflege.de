package entity;

import entity.Betreuer;
import op.OPDE;
import op.tools.SYSTools;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 11.07.12
 * Time: 13:53
 * To change this template use File | Settings | File Templates.
 */
public class BetreuerTools {

    public static ListCellRenderer getBetreuerRenderer() {
        return new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                String text;
                if (o == null) {
                    text = OPDE.lang.getString("misc.commands.>>noselection<<");
                } else if (o instanceof Betreuer) {
//                    text = ((Betreuer) o).getName() + ", " + ((Betreuer) o).getVorname() + ", " + ((Betreuer) o).getOrt();
                    text = getFullName((Betreuer) o);
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
            }
        };
    }

    public static String getFullName(Betreuer betreuer) {
        return SYSTools.anonymizeString(betreuer.getAnrede() + " " + betreuer.getVorname() + " " + betreuer.getName());
    }

}
