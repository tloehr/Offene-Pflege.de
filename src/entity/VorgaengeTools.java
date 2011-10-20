/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import op.OPDE;
import op.tools.DlgException;
import op.tools.SYSConst;

import javax.persistence.Query;
import java.text.DateFormat;
import java.util.*;

/**
 * @author tloehr
 */
public class VorgaengeTools {

    public static final int PDCA_OFF = 0;
    public static final int PDCA_PLAN = 1;
    public static final int PDCA_DO = 2;
    public static final int PDCA_CHECK = 3;
    public static final int PDCA_ACT = 4;
    public static final String[] PDCA = new String[]{"ABGESCHALTET", "Plan", "Do", "Check", "Act"};

    /**
     * Sucht alle Elemente für einen bestimmten Bewohner raus und gibt diesen als Set von SYSFiles zurück.
     *
     * @param vorgang
     * @return
     */
    public static List findElementeByVorgang(Vorgaenge vorgang, boolean mitSystem) {

        Comparator<Object> elementsComparator = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {

                long l1;
                if (o1 instanceof Object[]){
                    l1 = ((VorgangElement) ((Object[]) o1)[0]).getPITInMillis();
                } else {
                    l1 = ((VorgangElement) o1).getPITInMillis();
                }


                long l2;
                if (o2 instanceof Object[]){
                    l2 = ((VorgangElement) ((Object[]) o2)[0]).getPITInMillis();
                } else {
                    l2 = ((VorgangElement) o2).getPITInMillis();
                }

                return new Long(l1).compareTo(l2);
            }
        };

        List elements = new ArrayList();
        Query query;

        query = OPDE.getEM().createNamedQuery(mitSystem ? "VBericht.findByVorgang" : "VBericht.findByVorgangOhneSystem");
        query.setParameter("vorgang", vorgang);
        elements.addAll(query.getResultList());

        query = OPDE.getEM().createNamedQuery("Pflegeberichte.findByVorgang");
        query.setParameter("vorgang", vorgang);
        elements.addAll(query.getResultList());

        Collections.sort(elements, elementsComparator);

        return elements;
    }

    public static void deleteVorgang(Vorgaenge vorgang) {
        vorgang.setBis(new Date());
        OPDE.getEM().getTransaction().begin();
        try {
            OPDE.getEM().remove(vorgang);
            OPDE.getEM().getTransaction().commit();
        } catch (Exception ex) {
            OPDE.getEM().getTransaction().rollback();
            new DlgException(ex);
        }
    }

    public static void endVorgang(Vorgaenge vorgang) {
        VBericht systemBericht = new VBericht("Vorgang abgeschlossen", VBerichtTools.VBERICHT_ART_CLOSE, vorgang);
        vorgang.setBis(new Date());
        OPDE.getEM().getTransaction().begin();
        try {
            //OPDE.getEM().persist(systemBericht);
            OPDE.getEM().merge(vorgang);
            OPDE.getEM().getTransaction().commit();
        } catch (Exception ex) {
            OPDE.getEM().getTransaction().rollback();
            new DlgException(ex);
        }
    }

    public static void reopenVorgang(Vorgaenge vorgang) {
        VBericht systemBericht = new VBericht("Vorgang wieder geöffnet", VBerichtTools.VBERICHT_ART_REOPEN, vorgang);
        vorgang.setBis(SYSConst.DATE_BIS_AUF_WEITERES);
        OPDE.getEM().getTransaction().begin();
        try {
            //OPDE.getEM().persist(systemBericht);
            OPDE.getEM().merge(vorgang);
            OPDE.getEM().getTransaction().commit();
        } catch (Exception ex) {
            OPDE.getEM().getTransaction().rollback();
            new DlgException(ex);
        }
    }

    public static void setWVVorgang(Vorgaenge vorgang, Date wv) {
        VBericht systemBericht = new VBericht("Wiedervorlage gesetzt auf: " + DateFormat.getDateInstance().format(wv), VBerichtTools.VBERICHT_ART_WV, vorgang);
        vorgang.setWv(wv);
        OPDE.getEM().getTransaction().begin();
        try {
            //OPDE.getEM().persist(systemBericht);
            OPDE.getEM().merge(vorgang);
            OPDE.getEM().getTransaction().commit();
        } catch (Exception ex) {
            OPDE.getEM().getTransaction().rollback();
            new DlgException(ex);
        }
    }

    /**
     * dreht den Kreislauf eine Stufe nach vorne.
     *
     * @param pdca
     * @return
     */
    public static short incPDCA(short pdca) {
        pdca++;
        if (pdca > PDCA_ACT) {
            pdca = PDCA_PLAN;
        }
        return pdca;
    }

    /**
     * dreht den Kreislauf eine Stufe zurück. Jedoch nicht weiter als PLAN.
     *
     * @param pdca
     * @return
     */
    public static short decPDCA(short pdca) {
        pdca--;
        if (pdca < PDCA_PLAN) {
            pdca = PDCA_PLAN;
        }
        return pdca;
    }


}
