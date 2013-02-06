package entity.prescription;

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
public class MedPackageTools {
    public static final String GROESSE[] = {"N1", "N2", "N3", "AP", "OP"};

    public static ListCellRenderer getMedPackungRenderer() {
        return new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean b, boolean b1) {
                String text;
                if (o == null) {
                    text = SYSTools.toHTML("<i>Keine Auswahl</i>");
                } else if (o instanceof MedPackage) {
                    MedPackage aPackage = (MedPackage) o;

                    text = toPrettyString(aPackage);
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, b, b1);
            }
        };
    }

    public static String toPrettyString(MedPackage aPackage) {
        String text = aPackage.getContent().toString() + " " + TradeFormTools.getPackUnit(aPackage.getTradeForm()) + ", " + GROESSE[aPackage.getSize()] + ", ";
        text += "PZN: " + aPackage.getPzn();
        return text;
    }

    /**
     * Checks if a PZN is valid and id not already in use.
     * Testet ob eine neue PZN gültig ist. Also ob sie 7 oder 8 Zeichen lang ist. Führende 'ß' Zeichen (kommt bei den Barcodes vor)
     * werden abgeschnitten. Und es wird anhand der Datenbank geprüft, ob die PZN noch frei ist oder nicht.
     *
     * @param pzn      die geprüfte und bereinigte PZN. <code>null</code> bei falscher oder belegter PZN.
     * @param ignoreMe lässt die betreffende Packung bei der Suche ausser acht. Null, wenn nicht gewünscht.
     * @return gibt die PZN zurück, wenn sie gültig ist. NULL sonst.
     */
    public static String checkNewPZN(String pzn, MedPackage ignoreMe) {
        pzn = parsePZN(pzn);

        if (pzn != null) {
            // PZN's darfs nur einmal geben. Gibts die hier schon ?
            // Dann ist die Packung falsch.
            EntityManager em = OPDE.createEM();
            String jpql = "SELECT m FROM MedPackage m WHERE m.pzn = :pzn " + (ignoreMe != null ? " AND m <> :medPackage " : "");
            Query query = em.createQuery(jpql);
            query.setParameter("pzn", pzn);
            if (ignoreMe != null) {
                query.setParameter("medPackage", ignoreMe);
            }
            if (!query.getResultList().isEmpty()) {
                pzn = null;
            }
            em.close();
        }

        return pzn;
    }

    /**
     * This method checks if a given string represents a valid german PZN, which may (as of 2013) have a length
     * of 7 or 8 chars. It must also be conform to the checksum algorithm defined by SecurPharm.
     * All the barcode scanners that came across added a "ß" at the front of the scanned number. So this
     * has to be removed if present.
     *
     * @param pzn the string to be checked
     * @return the cleaned and checked string. PZN7's are always added up to PZN8's (by puttin a zero to the head). If the PZN was invalid you will only get NULL.
     */
    public static String parsePZN(String pzn) {
        pzn = pzn.trim();
        if (pzn.matches("^ß?\\d{7,8}")) {
            pzn = (pzn.startsWith("ß") ? pzn.substring(1) : pzn);

            // this is only temporarily until the PZN7's are gone completely. it may take some years.
            if (pzn.length() == 7) {
                pzn = "0" + pzn;
            }

            if (!isPZNValid(pzn)) {
                pzn = null;
            }

        } else {
            pzn = null;
        }
        return pzn;
    }


    /**
     * checks the validity of a given PZN according to the algorithm defined by SecurPharm
     * <br/>
     * <br/>
     * <img src="http://www.offene-pflege.de/images/javadoc/pzn-checksum-calculation.png">
     * <br/>
     * <br/>
     * <i>This picture has been taken from the german PZN8 document. It is copyrighted to
     * Informationsstelle für Arzneispezialitäten - IFA GmbH</i>
     *
     * @return guess
     */
    private static boolean isPZNValid(String pzn) {
        int[] digits = new int[pzn.length() - 1];

        for (int c = 0; c < pzn.length() - 1; c++) {
            digits[c] = Integer.parseInt(String.valueOf(pzn.charAt(c)));
        }
        int givenChecksum = Integer.parseInt(String.valueOf(pzn.charAt(pzn.length() - 1)));

        int weightedSum = 0;
        int w = 7;
        for (int c = digits.length - 1; c >= 0; c--) {
            weightedSum += digits[c] * w;
            w--;
        }
        int calculatedChecksum = weightedSum % 11;

        if (calculatedChecksum != givenChecksum) {
            OPDE.debug("PZN is NOT valid");
        }

        return calculatedChecksum == givenChecksum;
    }
}