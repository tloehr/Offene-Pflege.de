package de.offene_pflege.entity.values;

import de.offene_pflege.op.OPDE;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 27.10.12
 * Time: 13:55
 * To change this template use File | Settings | File Templates.
 */
public class ResValueTypesTools {
    public static final short RR = 1;
    public static final short PULSE = 2;
    public static final short TEMP = 3;
    public static final short GLUCOSE = 4;
    public static final short WEIGHT = 5;
    public static final short HEIGHT = 6;
    public static final short BREATHING = 7;
    public static final short QUICK = 8;
    public static final short STOOL = 9;
    public static final short VOMIT = 10;
    public static final short LIQUIDBALANCE = 11;
    public static final short O2SATURATION = 12;
    public static final short ASPIRATION = 13;
    public static final short PAIN = 14;

    public static ResValueTypes getType(short type) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT v FROM ResValueTypes v WHERE v.valType = :type");

        query.setParameter("type", type);
        ResValueTypes result = (ResValueTypes) query.getSingleResult();
        return result;
    }


}
