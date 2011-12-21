package entity.verordnungen;

import entity.Bewohner;
import entity.EntityTools;
import op.OPDE;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 18.11.11
 * Time: 16:29
 * To change this template use File | Settings | File Templates.
 */
public class DarreichungTools {


    public static ListCellRenderer getDarreichungRenderer() {
        return new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                String text;
                if (o == null) {
                    text = SYSTools.toHTML("<i>Keine Auswahl</i>");
                } else if (o instanceof Darreichung) {
                    Darreichung darreichung = (Darreichung) o;
                    text = toPrettyString(darreichung);
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
            }
        };
    }


    public static String toPrettyString(Darreichung darreichung) {
        String zubereitung = SYSTools.catchNull(darreichung.getMedForm().getZubereitung());
        String anwtext = SYSTools.catchNull(darreichung.getMedForm().getAnwText());

        String text = darreichung.getMedProdukt().getBezeichnung() + ", " + darreichung.getZusatz();
        text += zubereitung.isEmpty() ? "" : zubereitung + " ";
        text += anwtext.isEmpty() ? MedFormenTools.EINHEIT[darreichung.getMedForm().getAnwEinheit()] : anwtext;
        return text;
    }


    public static List<Darreichung> findDarreichungByMedProduktText(String suche) {
        suche = "%" + suche + "%";

        EntityManager em = OPDE.createEM();

        Query query = em.createQuery(" " +
                " SELECT d FROM Darreichung d " +
                " WHERE d.medProdukt.bezeichnung like :suche" +
                " ORDER BY d.medProdukt.bezeichnung, d.zusatz, d.medForm.zubereitung ");

        query.setParameter("suche", suche);

        List<Darreichung> list = query.getResultList();

        em.close();

        return list;
    }

    /**
     * Dieses Methode wird vorwiegend bei den Verordnungen eingesetzt.
     * Der Gedanke ist wie folgt: Eine neue Verordnung eines Medikamentes wird immer
     * einem aktiven Vorrat zugeordnet, wenn es bereits früher mal eine Zuordnung zu einer
     * bestimmten DAF gab.
     * Gibt es keine frühere Zuweisung, dann werden nur Vorräte angezeigt, die zu der FormID der
     * neuen DAF passen. Notfalls muss man einen Vorrat anlegen.
     * Es werden Zuordnungen erlaubt, die aufgrund der Äquivalenzen zwischen
     * Formen bestehen. z.B. Tabletten zu Dragees zu Filmtabletten etc.
     */
    public static MedVorrat getVorratZurDarreichung(Bewohner bewohner, Darreichung darreichung) {
        MedVorrat result = null;
        EntityManager em = OPDE.createEM();
        try {
            Query query = em.createNamedQuery("MedVorrat.findByBewohnerAndDarreichung");
            query.setParameter("bewohner", bewohner);
            query.setParameter("darreichung", darreichung);

            result = (MedVorrat) query.getSingleResult();
        } catch (NoResultException nre) {
            result = null;
        } catch (Exception e) {
            OPDE.fatal(e);
        } finally {
            em.close();
        }


        return result;
    }

    public static List<MedVorrat> getPassendeVorraeteZurDarreichung(Bewohner bewohner, Darreichung darreichung) {
        // TODO: das muss noch getestet werden
        EntityManager em = OPDE.createEM();
        List<MedVorrat> liste = new ArrayList();

        // 1. Form der gesuchten darreichung bestimmen.
        MedFormen meineForm = darreichung.getMedForm();

        // 2. Alle äquivalenten Formen dazu finden
        List<MedFormen> aehnlicheFormen = new ArrayList<MedFormen>();
        if (meineForm.getEquiv() != 0) {
            Query query = em.createNamedQuery("MedFormen.findByEquiv");
            query.setParameter("equiv", meineForm.getEquiv());
            aehnlicheFormen = query.getResultList();
        } else {
            aehnlicheFormen.add(meineForm);
        }

        // 3. Anhand der Bestände die passenden Vorräte ermitteln
        Query queryVorraete = em.createQuery(" " +
                " SELECT DISTINCT b.vorrat FROM MedBestand b " +
                " WHERE b.vorrat.bewohner = :bewohner " +
                " AND b.vorrat.bis = :bis " +
                " AND b.darreichung.medForm.formID  IN " +
                " ( " + EntityTools.getIDList(aehnlicheFormen) + " ) "
        );
        queryVorraete.setParameter("bewohner", bewohner);
        queryVorraete.setParameter("bis", SYSConst.DATE_BIS_AUF_WEITERES);

        liste = queryVorraete.getResultList();
        em.close();
        return liste;
    }

//    public static ResultSet getVorrat2DAF(String bwkennung, long dafid, Bool foundMatch) {
//        ResultSet result = null;
//        foundMatch.setBool(true);
//        String sql = " SELECT v.VorID, v.Text " +
//                " FROM MPVorrat v" +
//                " INNER JOIN MPBestand b ON v.VorID = b.VorID" +
//                " WHERE v.BWKennung=? AND b.DafID = ?  " +
//                " AND v.Bis = " + SYSConst.MYSQL_DATETIME_BIS_AUF_WEITERES;
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            stmt.setString(1, bwkennung);
//            stmt.setLong(2, dafid);
//            result = stmt.executeQuery();
//            if (!result.first()) {
//                // Gibts nicht. Dann zeigen wir alle Vorr?te an, die zumindest dieselbe FormID haben, wie
//                // die gesuchte DAF.
//                foundMatch.setBool(false);
//
//                String sql1 = " SELECT DISTINCT v.VorID, v.Text " +
//                        " FROM MPVorrat v" +
//                        " INNER JOIN MPBestand b ON v.VorID = b.VorID " +
//                        " INNER JOIN MPDarreichung d ON b.DafID = d.DafID" +
//                        " WHERE d.FormID IN (" +
//                        "       SELECT FormID " +
//                        "       FROM MPFormen " +
//                        "       WHERE (Equiv IN ( " + // Alle Formen, die gleichwertig sind
//                        "               SELECT Equiv " +
//                        "               FROM MPDarreichung d " +
//                        "               INNER JOIN MPFormen f ON f.FormID = d.FormID " +
//                        "               WHERE d.DafID = ? " +
//                        "               ) AND Equiv <> 0 " +
//                        "           OR " +
//                        "           ( FormID IN (" + // Falls diese Form keine Gleichwertigen besitzt (Equiv = 0), dann nur die Form selbst.
//                        "               SELECT FormID FROM MPDarreichung WHERE DafID = ? )" +
//                        "           )" +
//                        "       )" +
//                        " ) " +
//                        " AND v.BWKennung=? " +
//                        " AND v.Bis = " + SYSConst.MYSQL_DATETIME_BIS_AUF_WEITERES +
//                        " ORDER BY v.Text ";
//                PreparedStatement stmt1 = OPDE.getDb().db.prepareStatement(sql1);
//                stmt1.setLong(1, dafid);
//                stmt1.setLong(2, dafid);
//                stmt1.setString(3, bwkennung);
//                result = stmt1.executeQuery();
//            }
//        } catch (SQLException ex) {
//            foundMatch.setBool(false);
//            new DlgException(ex);
//        }
//
//        return result;
//    }


}
