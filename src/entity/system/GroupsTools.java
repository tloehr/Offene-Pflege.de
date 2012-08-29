package entity.system;

import op.OPDE;

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

    public static ArrayList<Groups> getGroups() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT g FROM Groups g ORDER BY g.description ");
        ArrayList<Groups> list = new ArrayList<Groups>(query.getResultList());
        em.close();
        return list;
    }
}
