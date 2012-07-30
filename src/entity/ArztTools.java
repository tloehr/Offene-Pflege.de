package entity;

import op.OPDE;
import op.tools.SYSTools;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 14.12.11
 * Time: 13:27
 * To change this template use File | Settings | File Templates.
 */
public class ArztTools {

    public static ListCellRenderer getArztRenderer() {
        return new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                String text;
                if (o == null) {
                    text = OPDE.lang.getString("misc.commands.>>noselection<<");
                } else if (o instanceof Arzt) {
//                    text = ((Arzt) o).getName() + ", " + ((Arzt) o).getVorname() + ", " + ((Arzt) o).getOrt();
                    text = getFullName((Arzt) o);
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
            }
        };
    }

    public static String getFullName(Arzt arzt) {
        return arzt.getAnrede() + " " + SYSTools.catchNull(arzt.getTitel(), "", " ") + arzt.getVorname() + " " + arzt.getName();
    }

}
