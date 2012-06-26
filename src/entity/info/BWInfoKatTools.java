package entity.info;

import op.OPDE;
import op.care.info.PnlInfo;
import op.tools.InternalClassACL;
import org.eclipse.persistence.internal.xr.Result;

import javax.persistence.Query;
import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 22.06.12
 * Time: 14:21
 * To change this template use File | Settings | File Templates.
 */
public class BWInfoKatTools {

    public static List<BWInfoKat> getKategorien() {

        String katart = "0 "; // 0 können alle

        katart += OPDE.getAppInfo().userHasAccessLevelForThisClass(PnlInfo.internalClassID, InternalClassACL.USER1) ? ",2 " : ""; // Stammdaten
        katart += OPDE.getAppInfo().userHasAccessLevelForThisClass(PnlInfo.internalClassID, InternalClassACL.USER2) ? ",1 " : ""; // Verwaltung

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM BWInfoKat b WHERE b.katArt IN (" + katart + " ) ORDER BY b.bezeichnung ");
        List<BWInfoKat> result = query.getResultList();
        em.close();
        OPDE.debug("ping");
        return result;
    }


}
