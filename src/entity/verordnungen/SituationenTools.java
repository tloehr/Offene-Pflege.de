package entity.verordnungen;

import op.OPDE;
import op.tools.DlgException;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 14.12.11
 * Time: 15:36
 * To change this template use File | Settings | File Templates.
 */
public class SituationenTools {
    public static ListCellRenderer getSituationenRenderer() {
        return new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean b, boolean b1) {
                JLabel l = new JLabel();
                if (o == null) {
                    l.setText("<i>Keine Auswahl</i>");
                } else if (o instanceof Situationen) {
                    l.setText(((Situationen) o).getText());
                }
                return l;
            }
        };
    }

    public static List<Situationen> findSituationByText(String suche) {
        suche = "%" + suche + "%";
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT s FROM Situationen s WHERE s.text like :suche");
        query.setParameter("suche", suche);
        List<Situationen> list = query.getResultList();
        em.close();
        return list;
    }
}
