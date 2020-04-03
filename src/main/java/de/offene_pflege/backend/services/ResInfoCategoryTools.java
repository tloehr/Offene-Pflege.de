package de.offene_pflege.backend.services;

import de.offene_pflege.backend.entity.info.ResInfoCategory;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 22.06.12
 * Time: 14:21
 * To change this template use File | Settings | File Templates.
 */
public class ResInfoCategoryTools {

    public static final int BASICS = 0;
    public static final int NURSING = 100;
    public static final int SKIN = 110;
    public static final int VITAL = 120;
    public static final int ADMINISTRATIVE = 1000;

    public static final String[] TYPESS = new String[]{SYSTools.xx("opde.settings.model.type.BASICS"), SYSTools.xx("opde.settings.model.type.NURSING"), SYSTools.xx("opde.settings.model.type.SKIN"), SYSTools.xx("opde.settings.model.type.VITAL"), SYSTools.xx("opde.settings.model.type.ADMINISTRATIVE")};
    public static final Integer[] TYPES = new Integer[]{BASICS, NURSING, SKIN, VITAL, ADMINISTRATIVE};


    /**
     * @return
     */
    public static List<ResInfoCategory> getAll4NP() {
        // katart below 1000 is accessible for everyone
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfoCategory b WHERE b.catType < 1000 AND b.sort >= 0 ORDER BY b.text");
        List<ResInfoCategory> result = query.getResultList();
        em.close();
        return result;
    }



//    /**
//     * @return
//     */
//    public static List<ResInfoCategory> getAll4ResInfo() {
//        long begin = System.currentTimeMillis();
//        String katart = "0";   // a little trick. 0 is always viable
//
//        //        katart += OPDE.getAppInfo().isAllowedTo(InternalClassACL.USER1, PnlInfo.internalClassID) ? "," + STAMMDATEN : ""; // Stammdaten
//        katart += OPDE.getAppInfo().isAllowedTo(InternalClassACL.USER2, PnlInformation.internalClassID) ? "," + ADMINISTRATIVE : ""; // Verwaltung
//
//        // katart below 1000 is accessible for everyone
//        EntityManager em = OPDE.createEM();
//        Query query = em.createQuery("SELECT DISTINCT b FROM ResInfoCategory b JOIN b.resInfoTypes t WHERE (b.catType < 1000 OR b.catType IN (" + katart + " )) AND b.sort >= 0 ORDER BY b.text ");
//        List<ResInfoCategory> result = query.getResultList();
//        em.close();
//        SYSTools.showTimeDifference(begin);
//        //        Collections.sort(result);
//        return result;
//    }

    public static List<ResInfoCategory> getAll() {

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT DISTINCT b FROM ResInfoCategory b ORDER BY b.text ");
        List<ResInfoCategory> result = query.getResultList();
        em.close();
        return result;
    }

}
