package entity;

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
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean b, boolean b1) {
                JLabel l = new JLabel();
                if (o == null){
                    l.setText("<i>Keine Auswahl</i>");
                } else if (o instanceof Arzt) {
                    l.setText(((Arzt) o).getName() + ", " + ((Arzt) o).getVorname() + ", " + ((Arzt) o).getOrt());
                }
                return l;
            }
        };
    }
}
