package de.offene_pflege.entity.values;

import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.services.ResvaluetypesService;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.DateFormat;
import java.util.ArrayList;

public class ResValueTypeTools {

    public static ArrayList<Resvaluetypes> getAll() {
        EntityManager em = OPDE.createEM();

        String jpql = " " +
                " SELECT rvt " +
                " FROM Resvaluetypes rvt " +
                " ORDER BY rvt.text";

        Query query = em.createQuery(jpql);
        ArrayList<Resvaluetypes> listValtypes = new ArrayList<Resvaluetypes>(query.getResultList());
        em.close();
        return listValtypes;
    }
}
