package entity;

import op.OPDE;
import op.tools.DlgException;
import op.tools.SYSTools;
import op.tools.Zeitraum;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 24.10.11
 * Time: 16:10
 * To change this template use File | Settings | File Templates.
 */
public class BWInfoTools {
    public static BWInfo getLastBWInfo(Bewohner bewohner, BWInfoTyp bwinfotyp) {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("BWInfo.findByBewohnerByBWINFOTYP_DESC");
        query.setParameter("bewohner", bewohner);
        query.setParameter("bwinfotyp", bwinfotyp);
        query.setFirstResult(0);
        query.setMaxResults(1);
        List<BWInfo> bwinfos = query.getResultList();
        em.close();
        return bwinfos.isEmpty() ? null : bwinfos.get(0);
    }

    public static Zeitraum getZeitraum(BWInfo bwinfo) {
        Zeitraum zeitraum = null;
        try {
            zeitraum = new Zeitraum(bwinfo.getVon(), bwinfo.getBis());
        } catch (Exception ex) {
            new DlgException(ex);
        }
        return zeitraum;
    }


    public static boolean isAusgezogen(BWInfo bwinfo) {
        return !(bwinfo == null || bwinfo.getXml().indexOf("ausgezogen") == -1);
    }

    public static boolean isVerstorben(BWInfo bwinfo) {
        return !(bwinfo == null || bwinfo.getXml().indexOf("verstorben") == -1);
    }


    /**
     * Ermittelt, seit wann ein Bewohner abwesend war.
     *
     * @return Datum des Beginns der Abwesenheitsperiode. =NULL wenn ANwesend.
     */
    public static Date getAbwesendSeit(Bewohner bewohner) {

        Date d = null;
        EntityManager em = OPDE.createEM();
        try {

            String jpql = "" +
                    " SELECT b FROM BWInfo b WHERE b.bwinfotyp.bwinftyp = 'abwe' AND b.bewohner = :bewohner AND b.von <= :von AND b.bis >= :bis";
            Query query = em.createQuery(jpql);
            query.setParameter("bewohner", bewohner);
            query.setParameter("von", new Date());
            query.setParameter("bis", new Date());
            d = (Date) query.getSingleResult();
        } catch (NoResultException nre) {
            d = null;
        } catch (Exception e) {
            OPDE.fatal(e);
        } finally {
            em.close();
        }
        return d;
    }

    /**
     * @return Eine ArrayList aus Date[0..1] Arrays mit jeweils Von, Bis, die alle Heimaufenthalte des BW enthalten.
     */
    public static List<BWInfo> getHeimaufenthalte(Bewohner bewohner) {
        List<BWInfo> result = new Vector<BWInfo>();
        EntityManager em = OPDE.createEM();
        try {
            String jpql = "" +
                    " SELECT b FROM BWInfo b" +
                    " WHERE b.bwinfotyp.bwinftyp = 'hauf' AND b.bewohner = :bewohner " +
                    " ORDER BY b.von ";
            Query query = em.createQuery(jpql);
            query.setParameter("bewohner", bewohner);
            result = query.getResultList();
        } catch (Exception e) {
            OPDE.fatal(e);
        } finally {
            em.close();
        }
        return result;
    }
}
