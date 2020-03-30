package de.offene_pflege.entity.system;

import de.offene_pflege.op.OPDE;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 29.08.12
 * Time: 11:34
 * To change this template use File | Settings | File Templates.
 */
public class GroupsTools {

    public static ArrayList<OPGroups> getGroups() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT g FROM OPGroups g ORDER BY g.gid ");
        ArrayList<OPGroups> list = new ArrayList<OPGroups>(query.getResultList());
        em.close();
        return list;
    }


}
