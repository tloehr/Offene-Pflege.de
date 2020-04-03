package de.offene_pflege.backend.entity.prescription;

import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 14.12.11
 * Time: 15:36
 * To change this template use File | Settings | File Templates.
 */
public class SituationsTools {
    public static ListCellRenderer getSituationenRenderer() {
        return (jList, o, i, b, b1) -> {

            String text;
            if (o == null) {
                text = SYSTools.toHTML("<i>Keine Auswahl</i>");
            } else if (o instanceof Situations) {
                text = ((Situations) o).getText();
            } else {
                text = o.toString();
            }
            return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, b, b1);
        };
    }

    public static List<Situations> findSituationByText(String suche) {
        suche = "%" + suche + "%";
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT s FROM Situations s WHERE s.text like :suche");
        query.setParameter("suche", suche);
        List<Situations> list = query.getResultList();
        em.close();
        return list;
    }
}
