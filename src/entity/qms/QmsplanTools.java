package entity.qms;

import entity.system.Commontags;
import op.OPDE;
import op.tools.SYSConst;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;

/**
 * Created by tloehr on 17.06.14.
 */
public class QmsplanTools {


    public static ArrayList<Qmsplan> getAllActive() {
           EntityManager em = OPDE.createEM();
           ArrayList<Qmsplan> list = null;

           try {

               String jpql = " SELECT q " +
                       " FROM Qmsplan q" +
                       " WHERE q.to = :to " +
                       " ORDER BY q.title DESC ";

               Query query = em.createQuery(jpql);
               query.setParameter("to", SYSConst.DATE_UNTIL_FURTHER_NOTICE);
               list = new ArrayList<Qmsplan>(query.getResultList());

           } catch (Exception se) {
               OPDE.fatal(se);
           } finally {
               em.close();
           }
           return list;
       }

}
