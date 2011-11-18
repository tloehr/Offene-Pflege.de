package entity.medis;

import entity.Verordnung;
import op.OPDE;

import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 18.11.11
 * Time: 14:28
 * To change this template use File | Settings | File Templates.
 */
public class MedBestandTools {

    public static MedBestand findByVerordnungImAnbruch(Verordnung verordnung) {

        Query query = OPDE.getEM().createNamedQuery("MedBestand.findByDarreichungAndBewohnerImAnbruch");
        query.setParameter("bewohner", verordnung.getBewohner());
        query.setParameter("darreichung", verordnung.getDarreichung());

        MedBestand result = null;

        try {
            result = (MedBestand) query.getSingleResult();
        } catch (NoResultException nre){
            result = null;
        } catch (Exception e) {
            OPDE.fatal(e);
            System.exit(1);
        }

        return result;
    }
}
