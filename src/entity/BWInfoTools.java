package entity;

import op.OPDE;
import op.tools.DlgException;
import op.tools.Zeitraum;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

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
        return bwinfo.getXml().indexOf("ausgezogen") > -1;
    }

    public static boolean isVerstorben(BWInfo bwinfo) {
        return bwinfo.getXml().indexOf("verstorben") > -1;
    }


}
