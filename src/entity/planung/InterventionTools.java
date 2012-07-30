package entity.planung;

import entity.EntityTools;
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
 * Time: 14:26
 * To change this template use File | Settings | File Templates.
 */
public class InterventionTools {

    public static final int MASSART_PFLEGE = 1;
    public static final int MASSART_BHP = 2;

    public static ListCellRenderer getMassnahmenRenderer() {
        return new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean b, boolean b1) {

                String text;
                if (o == null) {
                    text = SYSTools.toHTML("<i>Keine Auswahl</i>");
                } else if (o instanceof Intervention) {
                    text = ((Intervention) o).getBezeichnung();
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, b, b1);
            }
        };
    }

    public static List<Intervention> findMassnahmenBy(int mode) {
        return findMassnahmenBy(mode, "");
    }

    public static List<Intervention> findMassnahmenBy(int massArt, String suche) {

        EntityManager em = OPDE.createEM();

        Query query = em.createQuery(" " +
                " SELECT m FROM Intervention m WHERE m.aktiv = TRUE AND m.massArt = :art " +
                (SYSTools.catchNull(suche).isEmpty() ? "" : " AND m.bezeichnung like :suche ") +
                " ORDER BY m.bezeichnung "
        );

        query.setParameter("art", massArt);
        if (!SYSTools.catchNull(suche).isEmpty()) {
            query.setParameter("suche", EntityTools.getMySQLsearchPattern(suche));
        }

        List<Intervention> list = query.getResultList();

        em.close();

        return list;
    }


}
