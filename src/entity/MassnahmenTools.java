package entity;

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
public class MassnahmenTools {
    //    public static final int ART_PROBLEM = 0;
//    public static final int ART_RESSOURCE = 1;
//    public static final int ART_ZIEL = 2;
//    public static final int ART_INFO = 3;
    public static final int ART_MASSNAHME = 4;
    public static final int ART_KONTROLLEN = 5;
    public static final String[] ARTEN = new String[]{"", "", "", "", "Massnahme", "Kontrolle"}; // die ersten vier leeren stammen noch aus alten Zeiten.
    public static final int MODE_ALLE = 0;
    public static final int MODE_NUR_BHP = 1;
    public static final int MODE_NUR_PFLEGE = 2;

    public static ListCellRenderer getMassnahmenRenderer() {
        return new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean b, boolean b1) {

                String text;
                if (o == null) {
                    text = SYSTools.toHTML("<i>Keine Auswahl</i>");
                } else if (o instanceof Massnahmen) {
                    text = ((Massnahmen) o).getBezeichnung();
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, b, b1);
            }
        };
    }

    public static List<Massnahmen> findMassnahmenBy(int mode) {
        return findMassnahmenBy(mode, "");
    }

    public static List<Massnahmen> findMassnahmenBy(int mode, String suche) {

        EntityManager em = OPDE.createEM();

        Query query = em.createQuery(" " +
                " SELECT m FROM Massnahmen m WHERE m.aktiv = TRUE AND m.massArt = :art " +
                (SYSTools.catchNull(suche).isEmpty() ? "" : " AND m.bezeichnung like :suche ") +
                " ORDER BY m.bezeichnung "
        );

        int art = 0;
        if (mode == MODE_NUR_BHP) {
            art = 2;
        } else if (mode == MODE_NUR_PFLEGE) {
            art = 1;
        }

        query.setParameter("art", art);
        if (!SYSTools.catchNull(suche).isEmpty()) {
            suche = "%" + suche + "%";
            query.setParameter("suche", suche);
        }

        List<Massnahmen> list = query.getResultList();

        em.close();

        return list;
    }


}
