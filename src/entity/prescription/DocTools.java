package entity.prescription;

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
public class DocTools {

    public static ListCellRenderer getArztRenderer() {
        return new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                String text;
                if (o == null) {
                    text = OPDE.lang.getString("misc.commands.>>noselection<<");
                } else if (o instanceof Doc) {
//                    text = ((Doc) o).getName() + ", " + ((Doc) o).getVorname() + ", " + ((Doc) o).getOrt();
                    text = getFullName((Doc) o);
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
            }
        };
    }

    public static String getFullName(Doc doc) {
        if (doc != null){
        return doc.getAnrede() + " " + SYSTools.catchNull(doc.getTitel(), "", " ") + doc.getVorname() + " " + doc.getName();
        } else {
            return "";
        }
    }

}
