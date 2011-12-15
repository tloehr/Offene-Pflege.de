package entity.verordnungen;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 15.12.11
 * Time: 15:24
 * To change this template use File | Settings | File Templates.
 */
public class MedPackungTools {
    public static final String GROESSE[] = {"N1", "N2", "N3", "AP", "OP"};

    public static ListCellRenderer getMedPackungRenderer() {
        return new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean b, boolean b1) {
                JLabel l = new JLabel();
                if (o == null) {
                    l.setText("<i>Keine Auswahl</i>");
                } else if (o instanceof MedPackung) {
                    MedPackung packung = (MedPackung) o;

                    String text = packung.getInhalt().toString() + " " + MedFormenTools.EINHEIT[packung.getDarreichung().getMedForm().getPackEinheit()] + " " + GROESSE[packung.getGroesse()] + " ";
                    text += "PZN: " + packung.getPzn();
                    l.setText(text);
                } else {
                    l.setText(o.toString());
                }
                return l;
            }
        };
    }
}
