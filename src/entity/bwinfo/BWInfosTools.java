package entity.bwinfo;

import entity.Bewohner;
import op.OPDE;
import op.tools.SYSConst;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 09.02.12
 * Time: 15:38
 * To change this template use File | Settings | File Templates.
 */
public class BWInfosTools {
    public static String TypeID_KH="KH";


    public static List<BWInfos> getKHAufenthalte(Bewohner bewohner){
        Date von = SYSConst.DATE_VON_ANFANG_AN;
        Date bis = SYSConst.DATE_BIS_AUF_WEITERES;

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT bwi FROM BWInfos bwi WHERE bwi.typeID = :typeID AND bwi.bewohner = :bewohner ORDER BY bwi.von DESC ");
        query.setParameter("typeID", TypeID_KH);
        query.setParameter("bewohner", bewohner);
        return  null;
    }

}
