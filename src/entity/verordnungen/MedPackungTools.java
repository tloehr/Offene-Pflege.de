package entity.verordnungen;

import op.OPDE;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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
        String text = packung.getInhalt().toString() + " " + DarreichungTools.getPackungsEinheit(packung.getDarreichung()) + ", " + GROESSE[packung.getGroesse()] + ", ";
        text += "PZN: " + packung.getPzn();
        return text;
    }

    /**
     * Testet ob eine neue PZN gültig ist. Also ob sie genau 7 Zeichen lang ist. Führende 'ß' Zeichen (kommt bei den Barcodes vor)
     * werden abgeschnitten. Und es wird anhand der Datenbank geprüft, ob die PZN noch frei ist oder nicht.
     *
     * @param pzn die geprüfte und bereinigte PZN. <code>null</code> bei falscher oder belegter PZN.
     * @return
     */
    public static String checkNewPZN(String pzn){
        if (pzn.matches("^ß?\\d{7}")) { // Hier sucht man nach einer PZN. Im Barcode ist das führende 'ß' enthalten.

            pzn = (pzn.startsWith("ß") ? pzn.substring(1) : pzn); // Führendes ß abschneiden

            // PZN's darfs nur einmal geben. Gibts die hier schon ?
            // Dann ist die Packung falsch.
            EntityManager em = OPDE.createEM();
            Query query = em.createNamedQuery("MedPackung.findByPzn");
            query.setParameter("pzn", pzn);
            if (!query.getResultList().isEmpty()){
                pzn = null;
            }
            em.close();
        } else {
            pzn = null;
        }
        return pzn;
    }
}
