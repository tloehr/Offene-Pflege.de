package entity.verordnungen;

import entity.Arzt;
import op.tools.SYSTools;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 05.01.12
 * Time: 15:43
 * To change this template use File | Settings | File Templates.
 */
public class MedHerstellerTools {
     public static ListCellRenderer getHerstellerRenderer() {
        return new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                String text;
                if (o == null) {
                    text = SYSTools.toHTML("<i>Keine Auswahl</i>");
                } else if (o instanceof MedHersteller) {
                    text = ((MedHersteller) o).getFirma() + SYSTools.catchNull(((MedHersteller) o).getOrt(), ", ", "");
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
            }
        };
    }
}
