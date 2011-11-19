package entity.vorgang;

import entity.EntityTools;
import op.OPDE;

import javax.persistence.Query;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 16.06.11
 * Time: 14:07
 * To change this template use File | Settings | File Templates.
 */
public class VKatTools {

    public static final int VKAT_ART_ALLGEMEIN = 0;
    public static final int VKAT_ART_PFLEGE = 1;
    public static final int VKAT_ART_BHP = 2;
    public static final int VKAT_ART_SOZIAL = 3;
    public static final int VKAT_ART_VERWALTUNG = 4;
    public static final int VKAT_ART_BESCHWERDE = 5;

    public static VKat addKat(String kat) {
        VKat vkat = null;
        Query query = OPDE.getEM().createNamedQuery("VKat.findByText");
        query.setParameter("text", kat.trim());
        if (query.getResultList().isEmpty()){
            vkat = new VKat(kat.trim());
            EntityTools.persist(vkat);
        } else {
            vkat = (VKat) query.getResultList().get(0);
        }
        return vkat;
    }
}
