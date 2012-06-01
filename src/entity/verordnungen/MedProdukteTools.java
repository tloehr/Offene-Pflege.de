package entity.verordnungen;

import entity.Arzt;
import op.OPDE;
import op.tools.DlgException;
import op.tools.SYSTools;

import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 14.12.11
 * Time: 11:27
 * To change this template use File | Settings | File Templates.
 */
public class MedProdukteTools {

    public static ListCellRenderer getMedProdukteRenderer() {
        return new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                String text;
                if (o == null) {
                    text = SYSTools.toHTML("<i>Keine Auswahl</i>");
                } else if (o instanceof MedProdukte) {
                    MedProdukte produkt = (MedProdukte) o;
                    text = produkt.getBezeichnung();
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
            }
        };
    }
}
