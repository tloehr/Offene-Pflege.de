package entity.verordnungen;

import entity.Bewohner;
import op.OPDE;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 15.12.11
 * Time: 16:00
 * To change this template use File | Settings | File Templates.
 */
public class MPAPVTools {

    public static BigDecimal getAPV(Bewohner bewohner, Darreichung darreichung) {
        EntityManager em = OPDE.createEM();
        BigDecimal result = null;

        try {
            Query query = em.createNamedQuery("APV.findByBewohnerAndDarreichung");
            query.setParameter("bewohner", bewohner);
            query.setParameter("darreichung", darreichung);
            result = (BigDecimal) query.getSingleResult();
        } catch (Exception e) {
            OPDE.fatal(e);
        } finally {
            em.close();
        }
        return result;
    }

    public static BigDecimal getAPV(Darreichung darreichung) {
        EntityManager em = OPDE.createEM();
        BigDecimal result = null;

        try {
            Query query = em.createNamedQuery("APV.findByDarreichungOnly");
            query.setParameter("darreichung", darreichung);
            result = (BigDecimal) query.getSingleResult();
        } catch (Exception e) {
            OPDE.fatal(e);
        } finally {
            em.close();
        }
        return result;
    }

}
