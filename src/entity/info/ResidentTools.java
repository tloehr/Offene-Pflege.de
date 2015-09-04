/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.info;

import entity.EntityTools;
import entity.building.Homes;
import entity.building.Station;
import entity.nursingprocess.NursingProcessTools;
import entity.prescription.MedInventoryTools;
import entity.prescription.PrescriptionTools;
import entity.process.QProcessTools;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSTools;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Years;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author tloehr
 */
public class ResidentTools {

    public static final int AGE_MINOR = 18;
    public static final int MALE = 1;
    public static final int FEMALE = 2;
    public static final String GENDER[] = {"", SYSTools.xx("misc.msg.male"), SYSTools.xx("misc.msg.female")};
    public static final String ADDRESS[] = {"", SYSTools.xx("misc.msg.termofaddress.mr"), SYSTools.xx("misc.msg.termofaddress.mrs")};
    public static final String KEY_STOOLDAYS = "stooldays";
    public static final String KEY_BALANCE = "liquidbalance";
    public static final String KEY_LOWIN = "lowin";
    public static final String KEY_TARGETIN = "targetin";
    public static final String KEY_HIGHIN = "highin";
    public static final String KEY_DAYSDRINK = "daysdrink";
    public static final short ADMINONLY = 2;
    public static final short NORMAL = 0;

//    public static Resident findByBWKennung(String bwkennung) {
//        EntityManager em = OPDE.createEM();
//        Query query = em.createQuery("SELECT b FROM Resident b WHERE b.rid = :bWKennung");
//        query.setParameter("bWKennung", bwkennung);
//        Resident bewohner = (Resident) query.getSingleResult();
//        em.close();
//        return bewohner;
//    }

//    public static JLabel getBWLabel(Resident bewohner) {
//        JLabel lblBW = new JLabel();
//        setBWLabel(lblBW, bewohner);
//        return lblBW;
//    }

    public static ListCellRenderer getRenderer() {
        return new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                String text;
                if (o == null) {
                    text = SYSTools.toHTMLForScreen(SYSTools.xx("misc.commands.>>noselection<<"));
                } else if (o instanceof Resident) {
                    text = o.toString();
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
            }
        };
    }

    public static boolean isMinor(Resident resident) {
        return getAge(resident).getYears() < AGE_MINOR;
    }

    public static String getNameAndFirstname(Resident bewohner) {
        return bewohner.getName() + ", " + bewohner.getFirstname();
    }

//    public static String getResidentLabelWithBDay(Resident bewohner) {
//        return "(*" + DateFormat.getDateInstance().format(bewohner.getDOB()) + ") [" + bewohner.getRIDAnonymous() + "]";
//    }

    public static String getTextCompact(Resident bewohner) {
        return bewohner.getName() + ", " + bewohner.getFirstname() + " [" + bewohner.getRIDAnonymous() + "]";
    }

//    public static void setBWLabel(JLabel lblBW, Resident bewohner) {
//        lblBW.setFont(new java.awt.Font("Dialog", 1, 18));
//        lblBW.setHorizontalAlignment(SwingConstants.LEADING);
//        lblBW.setForeground(new java.awt.Color(255, 51, 0));
//        lblBW.setText(getLabelText(bewohner));
//    }

    public static String getFullName(Resident bewohner) {
        return ADDRESS[bewohner.getGender()] + " " + bewohner.getFirstname() + " " + bewohner.getName();
    }

//    public static boolean isWeiblich(Bewohner bewohner) {
//        return bewohner.getGender() == FEMALE;
//    }


    public static Years getAge(Resident resident) {
        boolean dead = ResInfoTools.isDead(resident);
        ResInfo stay = ResInfoTools.getLastResinfo(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_STAY));
        LocalDate birthdate = new DateTime(resident.getDOB()).toLocalDate();
        DateTime refdate = dead ? new DateTime(stay.getTo()) : new DateTime();
        return Years.yearsBetween(birthdate.toDateTimeAtStartOfDay(), refdate);

    }

    public static String getLabelText(Resident resident) {
        boolean dead = ResInfoTools.isDead(resident);
        boolean gone = ResInfoTools.isGone(resident);
        ResInfo stay2 = ResInfoTools.getLastResinfo(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_STAY));
        ResInfo stay1 = ResInfoTools.getFirstResinfo(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_STAY));

        DateFormat df = DateFormat.getDateInstance();
        String result = resident.getName() + ", " + resident.getFirstname() + " (*" + df.format(resident.getDOB()) + "), ";


        result += getAge(resident).getYears() + " " + SYSTools.xx("misc.msg.Years") + " [" + resident.getRIDAnonymous() + "]";

        if (dead || gone) {
            result += "  " + SYSTools.xx("") + ": " + df.format(stay1.getFrom()) + ", ";
            result += (dead ? SYSTools.xx("misc.msg.late") : SYSTools.xx("misc.msg.movedout")) + ": " + df.format(stay2.getTo());
        }

        return result;
    }

    /**
     * creates a list of residents by the given pattern
     *
     * @param pattern
     * @return
     */
    public static ArrayList<Resident> getBy(String pattern, boolean archiveToo) {
        ArrayList<Resident> lstResult = null;
        Resident resident = EntityTools.find(Resident.class, pattern);

        if (resident == null) { // the pattern is not a valid RID
            pattern += "%"; // MySQL Wildcard
            EntityManager em = OPDE.createEM();

            Query query = em.createQuery("SELECT b FROM Resident b WHERE b.name like :nachname " +
                    (archiveToo ? "" : "AND b.station IS NOT NULL ")
                    + " ORDER BY b.name, b.firstname");
            query.setParameter("nachname", pattern);
            lstResult = new ArrayList<Resident>(query.getResultList());
        } else {
            lstResult = new ArrayList<Resident>();
            lstResult.add(resident);
        }

        return lstResult;
//        DefaultListModel dlm = SYSTools.list2dlm(listBW);
//
//            if (dlm.getSize() > 1) {
//                new DlgListSelector("Bitte wählen Sie eine(n) Bewohner(in) aus.", "Ihre Suche ergab mehrere Möglichkeiten. Welche(n) Bewohner(in) meinten Sie ?", dlm, applyClosure).setVisible(true);
//            } else if (dlm.getSize() == 1) {
//                resident = listBW.get(0);
//                applyClosure.execute(resident);
//            } else {
//                applyClosure.execute(null);
//            }

    }

    /**
     * This method must be called if a resident finally leaves the home. It will then seize all running processes and
     * end all open periods of any kind. Plans, Medication etc.
     *
     * @param em       as it is quite a complex operation, it runs within a surrounding EM to trigger rollbacks if necessary
     * @param resident the resident in question
     */
    public static void endOfStay(EntityManager em, Resident resident, Date enddate, String reason) throws Exception {
        NursingProcessTools.closeAll(em, resident, enddate);
        ResInfoTools.closeAll(em, resident, enddate, reason);
        MedInventoryTools.closeAll(em, resident, enddate);
        // The prescriptions must be closed after the MedInventories. Ohterwise there may be a locking exception.
        PrescriptionTools.closeAll(em, resident, enddate);
        QProcessTools.closeAll(em, resident, enddate);
    }

    public static ArrayList<Resident> getAllActive(Homes homes) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM Resident b WHERE b.station IS NOT NULL AND b.station.home = :home ORDER BY b.name, b.firstname");
        query.setParameter("home", homes);
        ArrayList<Resident> list = new ArrayList<Resident>(query.getResultList());
        em.close();
        return list;
    }

    public static ArrayList<Resident> getAllActive() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM Resident b WHERE b.station IS NOT NULL ORDER BY b.name, b.firstname");
        ArrayList<Resident> list = new ArrayList<Resident>(query.getResultList());
        em.close();
        return list;
    }

    public static ArrayList<Resident> getAllActive(Station station) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM Resident b WHERE b.station = :station ORDER BY b.name, b.firstname");
        query.setParameter("station", station);
        ArrayList<Resident> list = new ArrayList<Resident>(query.getResultList());
        em.close();
        return list;
    }


    /**
     * retrieves a list of all residents who were staying in the home during that specified
     * interval.
     *
     * @param start
     * @param end
     * @return
     */
    public static ArrayList<Resident> getAllActive(LocalDate start, LocalDate end) {
        DateTime from = start.toDateTimeAtStartOfDay();
        DateTime to = SYSCalendar.eod(end);
        ArrayList<Resident> list = null;
        EntityManager em = OPDE.createEM();
        try {

            Query query = em.createQuery(" " +
                    " SELECT b FROM Resident b " +
                    " JOIN b.resInfoCollection rinfo " +
                    " WHERE rinfo.bwinfotyp.type = :type " +
                    " AND b.adminonly <> 2 " +
                    " AND b.station IS NOT NULL " +
                    " AND ((rinfo.from <= :from AND rinfo.to >= :from) OR " +
                    " (rinfo.from <= :to AND rinfo.to >= :to) OR " +
                    " (rinfo.from > :from AND rinfo.to < :to)) " +
                    " ORDER BY b.name, b.firstname");
            query.setParameter("type", ResInfoTypeTools.TYPE_STAY);
            query.setParameter("from", from.toDate());
            query.setParameter("to", to.toDate());
            list = new ArrayList<Resident>(query.getResultList());
        } catch (Exception e) {
            OPDE.fatal(e);
        } finally {
            em.close();
        }
        return list;
    }

    /**
     * retrieves a list of all residents who were staying in the home during that particular
     * month. They are also included if they have left or arrived in that time period.
     *
     * @param month
     * @return
     */
    public static ArrayList<Resident> getAllActive(LocalDate month) {
        DateTime from = SYSCalendar.bom(month).toDateTimeAtStartOfDay();
        DateTime to = SYSCalendar.eod(SYSCalendar.eom(month));
        ArrayList<Resident> list = null;
        EntityManager em = OPDE.createEM();
        try {

            Query query = em.createQuery(" " +
                    " SELECT b FROM Resident b " +
                    " JOIN b.resInfoCollection rinfo " +
                    " WHERE rinfo.bwinfotyp.type = :type " +
                    " AND b.adminonly <> 2 " +
                    " AND ((rinfo.from <= :from AND rinfo.to >= :from) OR " +
                    " (rinfo.from <= :to AND rinfo.to >= :to) OR " +
                    " (rinfo.from > :from AND rinfo.to < :to)) " +
                    " ORDER BY b.name, b.firstname");
            query.setParameter("type", ResInfoTypeTools.TYPE_STAY);
            query.setParameter("from", from.toDate());
            query.setParameter("to", to.toDate());
            list = new ArrayList<Resident>(query.getResultList());
        } catch (Exception e) {
            OPDE.fatal(e);
        } finally {
            em.close();
        }
        return list;
    }

    public static ArrayList<Resident> getAllInactive() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM Resident b WHERE b.station IS NULL ORDER BY b.name, b.firstname");
        ArrayList<Resident> list = new ArrayList<Resident>(query.getResultList());
        em.close();
        return list;
    }

    /**
     * http://www.tsc-web.net/archive/2007/12/mysql-query-howto-select-upcoming-birthdays/comment-page-1/
     *
     * @param days
     * @return
     */
    public static ArrayList<Object[]> getAllWithBirthdayIn(int days) {

        String mysql = "" +
                "SELECT BWKennung, DATE_FORMAT(NOW(), '%Y') - DATE_FORMAT(GebDatum, '%Y') + IF(DATE_FORMAT(GebDatum, '%m%d') < DATE_FORMAT(NOW(), '%m%d'), 1, 0) AS new_age, " +
                "DATEDIFF(GebDatum + INTERVAL YEAR(NOW()) - YEAR(GebDatum) + IF(DATE_FORMAT(NOW(), '%m%d') > DATE_FORMAT(GebDatum, '%m%d'), 1, 0) YEAR, NOW()) AS days_to_birthday " +
                "FROM resident res " +
                "WHERE res.StatID IS NOT NULL " +
                "HAVING days_to_birthday < ? " +
                "ORDER BY days_to_birthday ASC ";

        EntityManager em = OPDE.createEM();
        Query query = em.createNativeQuery(mysql);
        query.setParameter(1, days);

        ArrayList<Object[]> list = new ArrayList(query.getResultList());

        em.close();

        return list;
    }


}
