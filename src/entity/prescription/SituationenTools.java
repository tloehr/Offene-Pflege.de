package entity.prescription;

import op.OPDE;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.util.List;

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

                String text;
                if (o == null) {
                    text = SYSTools.toHTML("<i>Keine Auswahl</i>");
                } else if (o instanceof Situationen) {
                    text = ((Situationen) o).getText();
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, b, b1);
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
