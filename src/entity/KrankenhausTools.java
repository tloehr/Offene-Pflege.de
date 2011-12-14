package entity;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 14.12.11
 * Time: 13:30
 * To change this template use File | Settings | File Templates.
 */
public class KrankenhausTools {

    public static ListCellRenderer getKHRenderer() {
        return new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean b, boolean b1) {
                JLabel l = new JLabel();
                if (o == null){
                    l.setText("<i>Keine Auswahl</i>");
                } else if (o instanceof Krankenhaus) {
                    l.setText(((Krankenhaus) o).getName() + ", " + ((Krankenhaus) o).getOrt());
                }
                return l;
            }
        };
    }
}
