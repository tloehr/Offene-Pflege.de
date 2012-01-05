package entity.verordnungen;

import entity.Bewohner;
import entity.EntityTools;
import op.OPDE;
import op.tools.DlgException;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        String zusatz = SYSTools.catchNull(darreichung.getZusatz());

        String text = darreichung.getMedProdukt().getBezeichnung();
        text += zusatz.isEmpty() ? "" : ", " + zusatz;
        text += zubereitung.isEmpty() ? " " : ", " + zubereitung + " ";
        text += anwtext.isEmpty() ? MedFormenTools.EINHEIT[darreichung.getMedForm().getAnwEinheit()] : anwtext;
        return text;
    }

    public static String getPackungsEinheit(Darreichung darreichung){
        return MedFormenTools.EINHEIT[darreichung.getMedForm().getPackEinheit()];
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
     * Die genaue Erläuterung zu dieser Methode befindet sich in der Methode <code>getPassendeVorraeteZurDarreichung</code>.
     * Sie implementiert Punkt 1 der dort beschriebenen 2 Antworten.
     *
     * @see #getPassendeVorraeteZurDarreichung(entity.Bewohner, Darreichung)
     * @param bewohner
     * @param darreichung
     * @return Wenn die Darreichung zu einem früheren Zeitpunkt schonmal zugeordnet war, dann wird dieser Vorrat zurück gegeben. Ansonsten <code>null</code>.
     *
     */
    public static MedVorrat getVorratZurDarreichung(Bewohner bewohner, Darreichung darreichung) {
        MedVorrat result = null;
        EntityManager em = OPDE.createEM();
        try {
            Query query = em.createNamedQuery("MedVorrat.findActiveByBewohnerAndDarreichung");
            query.setParameter("bewohner", bewohner);
            query.setParameter("darreichung", darreichung);
            result = (MedVorrat) query.getSingleResult();
        } catch (NoResultException nre) {
            result = null;
        } catch (NonUniqueResultException nure) {
            result = null;
        } catch (Exception e) {
            OPDE.fatal(e);
        } finally {
            em.close();
        }

        return result;
    }


    /**
     * Um diese Methode zu verstehen muss man sich einige Fakten und Konzepte klar machen,
     * die wir bei der Entwicklung von OPDE bzgl. der Medikamente angewendet haben. Bitte beachten Sie
     * dass mit dieser Methode der Punkt 2 der beschrieben Antworten implementiert wird. Punkt 1 finden
     * sie in der Methode <code>getVorratZurDarreichung</code> umgesetzt.
     * <p/>
     * Eine <b>Darreichung</b> ist eine genaue Definition eines Medikaments, bei einer bestimmten Stärke, ein
     * Hersteller, eine Darreichungsform (bitte vom Begriff Darreichung unterscheiden), eine Anwendungseinheit,
     * eine Packungseinheit. usw. Das heisst, bei einer Verordnung wird immer eine Darreichung verordnet.
     * Diese ist ausreichend, ja viel genauer als eine Medikamentenangabe.
     * <p/>
     * Über die Zeit kann es immer wieder vorkommen, dass eine Verordnung zwar bestehen bleibt, aber
     * das Medikament sich verändert. Allerdings nicht beliebig sondern <b>wirkstoffgleich</b>. Das passiert
     * z.B. wenn zu Beginn, sagen wir Aspirin von Bayer verordnet wurde. Nach einigen Wochen schreibt der
     * Arzt dann aber statt dessen ASS 100 von ratiopharm auf. Diese Medikament kommt dann an und passt aber
     * nicht zu der ursprünglichen Verordnung (aus Sicht von OPDE). Es ist ja eine andere <b>Darreichung</b>.
     * </p>
     * Trotzdem muss dieses neue Präparat ebenfalls mit auf den Vorrat, der für die Verordnung verwendet wird,
     * draufgebucht werden. Das einzige auf dem OPDE hier besteht ist, dass die beiden Präparate dieselbe bzw.
     * eine äquivalente Form haben.So können mit der Zeit die Vorräte Bestände mit ganz unterschiedlichen Darreichungen besessen haben.
     * <p/>
     * Bei der täglichen Arbeit, besonders bei dem Einbuchen von neuen Medikamenten stellt sich aber immer wieder die
     * Frage: <b>auf welchen Vorrat muss ich dieses Produkt buchen ? Wozu gehört es ?</b>
     * <p/>
     * Natürlich sind alle nachfolgenden Überlegungen <i>bewohnerbezogen</i>. Ein Vorrat gehört immer <b>genau einem</b> Bewohner.
     * <p/>
     * Die Antwort kann ganz unterschiedlich ausfallen:
     * <ol>
     * <li>Wenn eine Darreichung zu <b>einem früheren Zeitpunkt</b> schonmal zu einem Vorrat zugeordnet war, <b>dann wird diese jetzt wieder</b> zugeordnet. Fall erledigt.</li>
     * <li>Trifft Punkt 1 nicht zu, dann suchen wir alle Vorräte, die Darreichungen enthalten mit einer passenden Form. Passend heisst hier entweder dieselbe Form oder eine
     * äquivalente Form (z.B. Tabletten sind zu Dragees gleichwertig wie zu Filmtabletten etc.).</li>
     * </ol>
     *
     * @see #getVorratZurDarreichung(entity.Bewohner, Darreichung)
     * @param bewohner
     * @param darreichung
     * @return
     */
    public static List<MedVorrat> getPassendeVorraeteZurDarreichung(Bewohner bewohner, Darreichung darreichung) {
        // TODO: das muss noch getestet werden
        EntityManager em = OPDE.createEM();
        List<MedVorrat> liste;

        // 1. Form der gesuchten darreichung bestimmen.
        MedFormen meineForm = darreichung.getMedForm();

        // 2. Alle äquivalenten Formen dazu finden
        List<MedFormen> aehnlicheFormen;
        if (meineForm.getEquiv() != 0) {
            Query query = em.createNamedQuery("MedFormen.findByEquiv");
            query.setParameter("equiv", meineForm.getEquiv());
            aehnlicheFormen = query.getResultList();
        } else {
            aehnlicheFormen = new ArrayList<MedFormen>();
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



}
