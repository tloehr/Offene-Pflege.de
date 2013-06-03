package entity.nursingprocess;

import entity.EntityTools;
import entity.info.ResInfoCategory;
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

    public static final int TYPE_CARE = 1;
    public static final int TYPE_PRESCRIPTION = 2;
    public static final int TYPE_SOCIAL = 3;

    public static final int FLAG_NONE = 0;
    public static final int FLAG_MOBILIZATION = 1;
    public static final int FLAG_WEIGHT_CONTROL = 2;
    public static final int FLAG_CATHETER_CHANGE = 3;
    public static final int FLAG_SUP_CATHETER_CHANGE = 4;
    public static final int FLAG_CONTROL_PACEMAKER = 5;
    public static final int FLAG_GAVAGE_FOOD = 6;
    public static final int FLAG_GAVAGE_LIQUID = 7;
    public static final int FLAG_GLUCOSE_MONITORING  = 8;
    public static final int FLAG_BP_MONITORING  = 9;
    public static final int FLAG_PORT_MONITORING  = 10;
    public static final int FLAG_PULSE_MONITORING  = 11;
    public static final int FLAG_WEIGHT_MONITORING  = 12;
    public static final int FLAG_PAIN_MONITORING  = 13;
    public static final int FLAG_TEMP_MONITORING  = 14;
    public static final int FLAG_FOOD_CONSUMPTION  = 15;


    public static ListCellRenderer getRenderer() {
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

    public static List<Intervention> findBy(int mode) {
        return findBy(mode, "");
    }

    public static List<Intervention> findBy(String suche) {

        EntityManager em = OPDE.createEM();


        Query query = em.createQuery(" " +
                " SELECT m FROM Intervention m WHERE m.bezeichnung like :search " +
                " ORDER BY m.bezeichnung "
        );

        if (!SYSTools.catchNull(suche).isEmpty()) {
            query.setParameter("search", EntityTools.getMySQLsearchPattern(suche));
        }

        List<Intervention> list = query.getResultList();

        em.close();

        return list;
    }

    public static List<Intervention> findBy(int massArt, String suche) {

            EntityManager em = OPDE.createEM();

            Query query = em.createQuery(" " +
                    " SELECT m FROM Intervention m WHERE m.active = TRUE AND m.interventionType = :art " +
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

    public static List<Intervention> findBy(ResInfoCategory category) {

            EntityManager em = OPDE.createEM();

            Query query = em.createQuery(" " +
                    " SELECT m FROM Intervention m WHERE m.active = TRUE AND m.category = :cat " +
                    " ORDER BY m.bezeichnung "
            );

            query.setParameter("cat", category);

            List<Intervention> list = query.getResultList();

            em.close();

            return list;
        }


}
