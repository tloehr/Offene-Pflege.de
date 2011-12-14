package entity.verordnungen;

import entity.Arzt;
import entity.Bewohner;
import entity.EntityTools;
import op.OPDE;
import op.tools.DlgException;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean b, boolean b1) {
                JLabel l = new JLabel();
                if (o == null) {
                    l.setText("<i>Keine Auswahl</i>");
                } else if (o instanceof Darreichung) {
                    Darreichung darreichung = (Darreichung) o;
                    String zubereitung = SYSTools.catchNull(darreichung.getMedForm().getZubereitung());
                    String anwtext = SYSTools.catchNull(darreichung.getMedForm().getAnwText());

                    String text = darreichung.getMedProdukt().getBezeichnung() + ", " + darreichung.getZusatz();
                    text += zubereitung.isEmpty() ? "" : zubereitung + " ";
                    text += anwtext.isEmpty() ? MedFormenTools.EINHEIT[darreichung.getMedForm().getAnwEinheit()] : anwtext;

                    l.setText(text);
                }
                return l;
            }
        };
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
            OPDE.fatal(e.getMessage());
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
        Query queryVorraete = em.createQuery(
                " " +
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


}
