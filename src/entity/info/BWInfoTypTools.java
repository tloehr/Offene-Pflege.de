package entity.info;

import op.OPDE;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 24.10.11
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public class BWInfoTypTools {

    public static final int MODE_INTERVAL_BYSECOND = 0;
    public static final int MODE_INTERVAL_BYDAY = 1;
    public static final int MODE_INTERVAL_NOCONSTRAINTS = 2;
    public static final int MODE_INTERVAL_SINGLE_INCIDENTS = 3; // Das sind Ereignisse, bei denen von == bis gilt. Weitere Einschränkungen werden nicht gemacht.

    public static BWInfoTyp findByBWINFTYP(String bwinftyp) {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("BWInfoTyp.findByBwinftyp");
        query.setParameter("bwinftyp", bwinftyp);
        List<BWInfoTyp> bwInfoTyps = query.getResultList();
        em.close();
        return bwInfoTyps.isEmpty() ? null : bwInfoTyps.get(0);
    }

    public static List<BWInfoTyp> findByKategorie(BWInfoKat kat) {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("BWInfoTyp.findByKat");
        query.setParameter("kat", kat);
        List<BWInfoTyp> bwInfoTypen = query.getResultList();
        em.close();
        return bwInfoTypen;
    }

}
