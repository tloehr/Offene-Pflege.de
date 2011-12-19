package entity;

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


        class arztListRenderer extends DefaultListCellRenderer {
            arztListRenderer() {
                super();
            }

            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                String text;
                if (o == null) {
                    text = SYSTools.toHTML("<i>Keine Auswahl</i>");
                } else if (o instanceof Arzt) {
                    text = ((Arzt) o).getName() + ", " + ((Arzt) o).getVorname() + ", " + ((Arzt) o).getOrt();
                } else {
                    text = o.toString();
                }
                return super.getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);

            }
        }
        return new arztListRenderer();
    }
}
