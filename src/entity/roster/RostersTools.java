package entity.roster;


import entity.reports.NReport;

import op.OPDE;
import op.tools.Pair;
import op.tools.SYSTools;
import org.eclipse.persistence.platform.xml.DefaultErrorHandler;
import org.joda.time.DateMidnight;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.swing.*;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.awt.*;
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

    public static final int FLAG_ACTIVE = 0;
    public static final int FLAG_CLOSED = 1;
    public static final int FLAG_LOCKED = 2;

    public static final int SECTION_CARE = 0;


    public static final String DEFAULT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<roster section=\"care\">\n" +
            "    <config>\n" +
            "        <hourstoworkperyear value=\"2002\"/>\n" +
            "    </config>\n" +
            "    <symbols>\n" +
            "        <symbol key=\"SC\" calc=\"kwert\" type=\"school\" description=\"Schule\" />\n" +
            "        <symbol key=\"F\" starttime=\"06:30\" endtime=\"13:15\" break=\"0\" calc=\"awert\" type=\"work\" description=\"Frühdienst\"  shift1=\"early\" statvalue1=\"1.00\"  shift2=\"\" statvalue2=\"0.00\" />\n" +
            "        <symbol key=\"B\" calc=\"pvalue\" type=\"work\" description=\"Ergo\" />\n" +
            "        <symbol key=\"FB\" calc=\"awert\" type=\"work\" description=\"Fortbildung\" />\n" +
            "        <symbol key=\"SL\" starttime=\"13:00\" endtime=\"21:00\" break=\"0\" calc=\"awert\" type=\"work\" description=\"Spätdienst, lang\" />\n" +
            "        <symbol key=\"M\" calc=\"kwert\" type=\"sick\" description=\"Mutterschutz\" />\n" +
            "        <symbol key=\"S1\" starttime=\"16:45\" endtime=\"19:45\" break=\"0\" calc=\"awert\" type=\"work\" description=\"Spätdienst, kurz\"  shift1=\"late\" statvalue1=\"1.00\"  shift2=\"\" statvalue2=\"0.00\" />\n" +
            "        <symbol key=\"N\" starttime=\"19:45\" endtime=\"06:45\" break=\"0\" calc=\"awert\" type=\"work\" description=\"Nachtdienst\"  shift1=\"night\" statvalue1=\"1.00\"  shift2=\"\" statvalue2=\"0.00\" />\n" +
            "        <symbol key=\"K\" calc=\"kwert\" type=\"sick\" description=\"Krank\" />\n" +
            "        <symbol key=\"U\" calc=\"uwert\" type=\"onleave\" description=\"Urlaub\" >\n" +
            "            <monday/>\n" +
            "            <tuesday/>\n" +
            "            <wednesday/>\n" +
            "            <thursday/>\n" +
            "            <holiday/>\n" +
            "            <friday/>\n" +
            "            <saturday/>\n" +
            "        </symbol>\n" +
            "        <symbol key=\"F1\" starttime=\"06:45\" endtime=\"09:45\" break=\"0\" calc=\"awert\" type=\"work\" description=\"Frühdienst, kurz\"  shift1=\"early\" statvalue1=\"0.50\"  shift2=\"\" statvalue2=\"0.00\" />\n" +
            "        <symbol key=\"T\" starttime=\"09:00\" endtime=\"16:00\" break=\"0\" calc=\"awert\" type=\"work\" description=\"Tagdienst\"  shift1=\"early\" statvalue1=\"0.50\"  shift2=\"late\" statvalue2=\"0.50\" />\n" +
            "        <symbol key=\"S\" starttime=\"13:00\" endtime=\"20:00\" break=\"0\" calc=\"awert\" type=\"work\" description=\"Spätdienst\"  shift1=\"late\" statvalue1=\"1.00\"  shift2=\"\" statvalue2=\"0.00\" />\n" +
            "        <symbol key=\"X\" calc=\"xwert\" type=\"offduty\" description=\"Frei\" />\n" +
            "    </symbols>\n" +
            "    <users>\n" +
            "    </users>\n" +
            "</roster>\n";

    public static ListCellRenderer getRenderer() {
           return new ListCellRenderer() {
               @Override
               public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                   String text;
                   if (o == null) {
                       text = SYSTools.toHTML("<i>Keine Auswahl</i>");
                   } else if (o instanceof Rosters) {
                       Rosters rosters = (Rosters) o;
                       text = new DateMidnight(rosters.getMonth()).toString("MMMM yyyy");
                   } else {
                       text = o.toString();
                   }
                   return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
               }
           };
       }

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
