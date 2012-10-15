package entity.prescription;

import entity.EntityTools;
import entity.info.Resident;
import op.OPDE;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
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
public class TradeFormTools {
    public static final int SHORT = 0;
    public static final int MEDIUM = 1;
    public static final int LONG = 2;

    public static ListCellRenderer getDarreichungRenderer(int verbosity) {
        final int v = verbosity;
        return new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                String text;
                if (o == null) {
                    text = SYSTools.toHTML("<i>Keine Auswahl</i>");
                } else if (o instanceof TradeForm) {
                    TradeForm darreichung = (TradeForm) o;
                    if (v == SHORT) {
                        text = darreichung.getSubtext();
                    } else if (v == MEDIUM) {
                        text = toPrettyStringMedium(darreichung);
                    } else {
                        text = toPrettyString(darreichung);
                    }
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
            }
        };
    }


    public static String toPrettyString(TradeForm darreichung) {
        String zubereitung = SYSTools.catchNull(darreichung.getDosageForm().getPreparation());
        String anwtext = SYSTools.catchNull(darreichung.getDosageForm().getUsageTex());
        String zusatz = SYSTools.catchNull(darreichung.getSubtext());

        String text = darreichung.getMedProduct().getBezeichnung();
        text += zusatz.isEmpty() ? "" : ", " + zusatz;
        text += zubereitung.isEmpty() ? " " : " " + zubereitung + ", ";
        text += anwtext.isEmpty() ? DosageFormTools.EINHEIT[darreichung.getDosageForm().getUsageUnit()] : anwtext;
        return text;
    }

    public static String toPrettyStringMedium(TradeForm darreichung) {
        String zubereitung = SYSTools.catchNull(darreichung.getDosageForm().getPreparation());
        String anwtext = SYSTools.catchNull(darreichung.getDosageForm().getUsageTex());
        String zusatz = SYSTools.catchNull(darreichung.getSubtext());

        String text = zusatz;
        text += zubereitung.isEmpty() ? " " : " " + zubereitung + ", ";
        text += anwtext.isEmpty() ? DosageFormTools.EINHEIT[darreichung.getDosageForm().getUsageUnit()] : anwtext;
        return text;
    }

    public static String getPackUnit(TradeForm darreichung) {
        return DosageFormTools.EINHEIT[darreichung.getDosageForm().getPackUnit()];
    }


    public static List<TradeForm> findTradeFormByMedProductText(EntityManager em, String suche) {
        suche = "%" + suche.trim() + "%";

        Query query = em.createQuery(" " +
                " SELECT d FROM TradeForm d " +
                " WHERE d.medProduct.bezeichnung like :suche" +
                " ORDER BY d.medProduct.bezeichnung, d.subtext, d.dosageForm.preparation ");

        query.setParameter("suche", suche);

        List<TradeForm> list = query.getResultList();

        return list;
    }

    public static List<TradeForm> findDarreichungByMedProduktText(String suche) {
        suche = "%" + suche.trim() + "%";

        EntityManager em = OPDE.createEM();

        Query query = em.createQuery(" " +
                " SELECT d FROM TradeForm d " +
                " WHERE d.medProduct.bezeichnung like :suche" +
                " ORDER BY d.medProduct.bezeichnung, d.subtext, d.dosageForm.preparation ");

        query.setParameter("suche", suche);

        List<TradeForm> list = query.getResultList();

        em.close();

        return list;
    }

    /**
     * Die genaue Erläuterung zu dieser Methode befindet sich in der Methode <code>getSuitableInventoriesForThisTradeForm</code>.
     * Sie implementiert Punkt 1 der dort beschriebenen 2 Antworten.
     *
     * @param bewohner
     * @param tradeform
     * @return Wenn die Darreichung zu einem früheren Zeitpunkt schonmal zugeordnet war, dann wird dieser Vorrat zurück gegeben. Ansonsten <code>null</code>.
     * @see #getSuitableInventoriesForThisTradeForm(entity.info.Resident, TradeForm)
     */
    public static MedInventory getInventory4TradeForm(Resident bewohner, TradeForm tradeform) {
        MedInventory result = null;
        EntityManager em = OPDE.createEM();
        try {
            result = getInventory4TradeForm(em, bewohner, tradeform);
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


    public static MedInventory getInventory4TradeForm(EntityManager em, Resident resident, TradeForm tradeform) throws Exception {
        Query query = em.createQuery(" SELECT DISTINCT inv FROM MedInventory inv " +
                " JOIN inv.medStocks stock " +
                " WHERE inv.resident = :resident AND stock.tradeform = :tradeform " +
                " AND inv.to = :to");
        query.setParameter("resident", resident);
        query.setParameter("tradeform", tradeform);
        query.setParameter("to", SYSConst.DATE_BIS_AUF_WEITERES);
        return (MedInventory) query.getSingleResult();
    }


    /**
     * Um diese Methode zu verstehen muss man sich einige Fakten und Konzepte klar machen,
     * die wir bei der Entwicklung von OPDE bzgl. der Medikamente angewendet haben. Bitte beachten Sie
     * dass mit dieser Methode der Punkt 2 der beschrieben Antworten implementiert wird. Punkt 1 finden
     * sie in der Methode <code>getInventory4TradeForm</code> umgesetzt.
     * <p/>
     * Eine <b>Darreichung</b> ist eine genaue Definition eines Medikaments, bei einer bestimmten Stärke, ein
     * Hersteller, eine Darreichungsform (bitte vom Begriff Darreichung unterscheiden), eine Anwendungseinheit,
     * eine Packungseinheit. usw. Das heisst, bei einer Verordnung wird immer eine Darreichung verordnet.
     * Diese ist ausreichend, ja viel genauer als eine Medikamentenangabe.
     * <p/>
     * Über die Zeit kann es immer wieder vorkommen, dass eine Verordnung zwar bestehen bleibt, aber
     * das Medikament sich verändert. Allerdings nicht beliebig sondern <b>wirkstoffgleich</b>. Das passiert
     * z.B. wenn zu Beginn, sagen wir Aspirin von Bayer verordnet wurde. Nach einigen Wochen schreibt der
     * Doc dann aber statt dessen ASS 100 von ratiopharm auf. Diese Medikament kommt dann an und passt aber
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
     * @param resident
     * @param tradeform
     * @return
     * @see #getInventory4TradeForm(entity.info.Resident, TradeForm)
     */
    public static List<MedInventory> getSuitableInventoriesForThisTradeForm(Resident resident, TradeForm tradeform) {
        // TODO: das muss noch getestet werden
        EntityManager em = OPDE.createEM();
        List<MedInventory> liste;

        // 1. Form der gesuchten darreichung bestimmen.
        DosageForm meineForm = tradeform.getDosageForm();

        // 2. Alle äquivalenten Formen dazu finden
        List<DosageForm> aehnlicheFormen;
        if (meineForm.getSameAs() != 0) {
            Query query = em.createQuery("SELECT m FROM DosageForm m WHERE m.sameAs = :equiv");
            query.setParameter("equiv", meineForm.getSameAs());
            aehnlicheFormen = query.getResultList();
        } else {
            aehnlicheFormen = new ArrayList<DosageForm>();
            aehnlicheFormen.add(meineForm);
        }

        // 3. Anhand der Bestände die passenden Vorräte ermitteln
        Query queryVorraete = em.createQuery(" " +
                " SELECT DISTINCT b.inventory FROM MedStock b " +
                " WHERE b.inventory.resident = :resident " +
                " AND b.inventory.to = :to " +
                " AND b.tradeform.dosageForm.id  IN " +
                " ( " + EntityTools.getIDList(aehnlicheFormen) + " ) "
        );
        queryVorraete.setParameter("resident", resident);
        queryVorraete.setParameter("to", SYSConst.DATE_BIS_AUF_WEITERES);

        liste = queryVorraete.getResultList();
        em.close();
        return liste;
    }


}
