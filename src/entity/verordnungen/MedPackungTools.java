package entity.verordnungen;

import op.tools.SYSTools;

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
                String text;
                if (o == null) {
                    text = SYSTools.toHTML("<i>Keine Auswahl</i>");
                } else if (o instanceof MedPackung) {
                    MedPackung packung = (MedPackung) o;

                    text = toPrettyString(packung);
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, b, b1);
            }
        };
    }

    public static String toPrettyString(MedPackung packung) {
        String text = packung.getInhalt().toString() + " " + DarreichungTools.getPackungsEinheit(packung.getDarreichung()) + " " + GROESSE[packung.getGroesse()] + " ";
        text += "PZN: " + packung.getPzn();
        return text;
    }
}
