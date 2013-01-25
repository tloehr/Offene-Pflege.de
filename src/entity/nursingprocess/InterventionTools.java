package entity.nursingprocess;

import entity.EntityTools;
import entity.info.ResInfoCategory;
import entity.info.Resident;
import entity.prescription.MedInventory;
import entity.prescription.MedStock;
import entity.prescription.MedStockTools;
import entity.prescription.MedStockTransactionTools;
import op.OPDE;
import op.care.med.inventory.PnlInventory;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.util.Date;
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
    public static final int TYPE_SOCIAL = 4;

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
                " SELECT m FROM Intervention m WHERE m.aktiv = TRUE AND m.interventionType = :art " +
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

    public static List<Intervention> findMassnahmenBy(ResInfoCategory category) {

            EntityManager em = OPDE.createEM();

            Query query = em.createQuery(" " +
                    " SELECT m FROM Intervention m WHERE m.aktiv = TRUE AND m.category = :cat " +
                    " ORDER BY m.bezeichnung "
            );

            query.setParameter("cat", category);

            List<Intervention> list = query.getResultList();

            em.close();

            return list;
        }


}
