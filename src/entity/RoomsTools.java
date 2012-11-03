package entity;

import op.OPDE;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 03.11.12
 * Time: 14:05
 * To change this template use File | Settings | File Templates.
 */
public class RoomsTools {
    public static ArrayList<Rooms> getAllActive() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT r FROM Rooms r ORDER BY r.station.name, r.text");
        //SELECT b FROM LCustodian b WHERE b.status >= 0 ORDER BY b.name, b.vorname");
        ArrayList<Rooms> list = new ArrayList<Rooms>(query.getResultList());
        em.close();

        return list;
    }
}
