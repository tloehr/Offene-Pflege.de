package de.offene_pflege.entity.building;

import de.offene_pflege.entity.NotRemovableEvaluation;
import de.offene_pflege.op.OPDE;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;

/**
 * Created by tloehr on 22.06.15.
 * <p>
 * Class still contains JDBC
 */
public class RoomsNotRemovableEvaluation implements NotRemovableEvaluation<Rooms> {
    @Override
    public boolean isRemovable(Rooms checkme) {

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT e FROM ResInfo e WHERE e.properties LIKE :pattern");
        query.setParameter("pattern", "%room.id=" + checkme.getRoomID()+"%");
        query.setMaxResults(1);
        ArrayList<Rooms> list = new ArrayList<Rooms>(query.getResultList());
        em.close();


        return list.isEmpty();
    }
}
