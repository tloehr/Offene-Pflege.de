package entity;


import op.OPDE;

import javax.persistence.Query;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 28.10.11
 * Time: 16:41
 * To change this template use File | Settings | File Templates.
 */
public class BWerteTools {

    public static BWerte findByID(long id){
        BWerte wert = null;

        Query query = OPDE.getEM().createNamedQuery("BWerte.findByBwid");
        query.setParameter("bwid", id);

        wert = (BWerte) query.getSingleResult();

        return wert;
    }
}
