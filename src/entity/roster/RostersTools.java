package entity.roster;

import entity.reports.NReport;
import entity.system.Users;
import op.OPDE;
import op.tools.Pair;
import org.eclipse.persistence.platform.xml.DefaultErrorHandler;
import org.joda.time.DateMidnight;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 16.08.13
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class RostersTools {


    public static Pair<DateMidnight, DateMidnight> getMinMax() {
        Pair<DateMidnight, DateMidnight> result = null;

        EntityManager em = OPDE.createEM();
        Query queryMin = em.createQuery("SELECT r FROM Rosters r ORDER BY r.month ASC ");
        queryMin.setMaxResults(1);

        Query queryMax = em.createQuery("SELECT r FROM Rosters r ORDER BY r.month DESC ");
        queryMax.setMaxResults(1);

        try {
            ArrayList<NReport> min = new ArrayList<NReport>(queryMin.getResultList());
            ArrayList<NReport> max = new ArrayList<NReport>(queryMax.getResultList());
            if (min.isEmpty()) {
                result = null;
            } else {
                result = new Pair<DateMidnight, DateMidnight>(new DateMidnight(min.get(0).getPit()), new DateMidnight(max.get(0).getPit()));
            }

        } catch (Exception e) {
            OPDE.fatal(e);
        }

        em.close();
        return result;
    }


    public static Rosters get4Month(DateMidnight month) {
        EntityManager em = OPDE.createEM();
        Rosters roster = null;

        try {
            String jpql = " SELECT r " +
                    " FROM Rosters r" +
                    " WHERE r.month  = :month ";

            Query query = em.createQuery(jpql);
            query.setParameter("month", month.dayOfMonth().withMinimumValue().toDate());

            roster = (Rosters) query.getSingleResult();
        } catch (NonUniqueResultException nue) {
            // thats ok
        } catch (NoResultException nre) {
            // thats ok
        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return roster;
    }

    public static RosterParameters getParameters(Rosters roster) {

           RosterXML rosterXML = new RosterXML();

           SAXParserFactory spf = SAXParserFactory.newInstance();
           //        spf.setValidating(true);
           //        spf.setNamespaceAware(true);
           try {
               SAXParser saxParser = spf.newSAXParser();

               XMLReader reader = saxParser.getXMLReader();
               reader.setErrorHandler(new DefaultErrorHandler());
               reader.setContentHandler(rosterXML);
               reader.parse(new InputSource(new StringReader(roster.getXml())));
           } catch (Exception e) {
               OPDE.fatal(e);
           }
           return rosterXML.getRosterParameters();
       }
}
