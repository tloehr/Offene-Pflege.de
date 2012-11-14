/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.info;

import entity.EntityTools;
import entity.Homes;
import entity.nursingprocess.NursingProcessTools;
import entity.prescription.MedInventoryTools;
import entity.prescription.PrescriptionTools;
import entity.process.QProcessTools;
import op.OPDE;
import op.tools.SYSTools;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Years;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
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

    public static final int MALE = 1;
    public static final int FEMALE = 2;
    public static final String GESCHLECHT[] = {"", OPDE.lang.getString("misc.msg.male"), OPDE.lang.getString("misc.msg.female")};
    public static final String ANREDE[] = {"", OPDE.lang.getString("misc.msg.termofaddress.mr"), OPDE.lang.getString("misc.msg.termofaddress.mrs")};

    public static final String KEY_STOOLDAYS = "stooldays";
    public static final String KEY_BALANCE = "liquidbalance";
    public static final String KEY_LOWIN = "lowin";
    public static final String KEY_TARGETIN = "targetin";
    public static final String KEY_HIGHIN = "highin";
    public static final String KEY_DAYSDRINK = "daysdrink";

    public static final short ADMINONLY = 2;
    public static final short NORMAL = 0;

    public static Resident findByBWKennung(String bwkennung) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM Resident b WHERE b.rid = :bWKennung");
        query.setParameter("bWKennung", bwkennung);
        Resident bewohner = (Resident) query.getSingleResult();
        em.close();
        return bewohner;
    }

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
                    text = SYSTools.toHTMLForScreen(OPDE.lang.getString("misc.commands.>>noselection<<"));
                } else if (o instanceof Resident) {
                    text = o.toString();
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
            }
        };
    }

    public static String getNameAndFirstname(Resident bewohner) {
        return bewohner.getName() + ", " + bewohner.getFirstname();
    }

    public static String getBWLabelWithBDay(Resident bewohner) {
        return "(*" + DateFormat.getDateInstance().format(bewohner.getDOB()) + ") [" + bewohner.getRIDAnonymous() + "]";
    }

    public static String getTextCompact(Resident bewohner) {
        return bewohner.getName() + ", " + bewohner.getFirstname() + " [" + bewohner.getRIDAnonymous() + "]";
    }

    public static void setBWLabel(JLabel lblBW, Resident bewohner) {
        lblBW.setFont(new java.awt.Font("Dialog", 1, 18));
        lblBW.setHorizontalAlignment(SwingConstants.LEADING);
        lblBW.setForeground(new java.awt.Color(255, 51, 0));
        lblBW.setText(getLabelText(bewohner));
    }

    public static String getFullName(Resident bewohner) {
        return ANREDE[bewohner.getGender()] + " " + bewohner.getFirstname() + " " + bewohner.getName();
    }

//    public static boolean isWeiblich(Bewohner bewohner) {
//        return bewohner.getGender() == FEMALE;
//    }

    public static String getLabelText(Resident bewohner) {
        boolean verstorben = ResInfoTools.isDead(bewohner);
        boolean ausgezogen = ResInfoTools.isDead(bewohner);
        ResInfo hauf = ResInfoTools.getLastResinfo(bewohner, ResInfoTypeTools.getByID("hauf"));

        DateFormat df = DateFormat.getDateInstance();
        String result = bewohner.getName() + ", " + bewohner.getFirstname() + " (*" + df.format(bewohner.getDOB()) + "), ";

        DateMidnight birthdate = new DateTime(bewohner.getDOB()).toDateMidnight();
        DateTime refdate = verstorben ? new DateTime(hauf.getTo()) : new DateTime();
        Years age = Years.yearsBetween(birthdate, refdate);
        result += age.getYears() + " " + OPDE.lang.getString("misc.msg.Years") + " [" + bewohner.getRIDAnonymous() + "]";

        if (verstorben || ausgezogen) {
            result += "  " + (verstorben ? OPDE.lang.getString("misc.msg.late") : OPDE.lang.getString("misc.msg.movedout")) + ": " + df.format(hauf.getTo()) + ", ";
        }

        return result;
    }

    /**
     * creates a list of fitting residents by the given pattern
     *
     * @param pattern
     * @return
     */
    public static ArrayList<Resident> getBy(String pattern) {
        ArrayList<Resident> lstResult = null;
        Resident resident = EntityTools.find(Resident.class, pattern);

        if (resident == null) { // the pattern is not a valid RID
            pattern += "%"; // MySQL Wildcard
            EntityManager em = OPDE.createEM();

            Query query = em.createQuery("SELECT b FROM Resident b WHERE b.name like :nachname ORDER BY b.name, b.firstname");
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
    public static void endOfStay(EntityManager em, Resident resident, Date enddate) throws Exception {
        em.lock(em.merge(resident), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        PrescriptionTools.closeAll(em, resident, enddate);
        NursingProcessTools.closeAll(em, resident, enddate);
        ResInfoTools.closeAll(em, resident, enddate);
        MedInventoryTools.closeAll(em, resident, enddate);
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

    /**
     * retrieves a list of all residents who were staying in the home during that particular
     * month. They are also included if they have left or arrived in that time period.
     * @param month
     * @return
     */
    public static ArrayList<Resident> getAllActive(DateMidnight month) {
        DateTime from = month.dayOfMonth().withMinimumValue().toDateTime();
        DateTime to = month.dayOfMonth().withMaximumValue().plusDays(1).toDateTime().minusSeconds(1);
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery(" " +
                " SELECT b FROM Resident b " +
                " JOIN b.resInfoCollection rinfo" +
                " WHERE rinfo.bwinfotyp.bwinftyp = :type " +
                " AND ((rinfo.from <= :from AND rinfo.to >= :from) OR " +
                " (rinfo.from <= :to AND rinfo.to >= :to) OR " +
                " (rinfo.from > :from AND rinfo.to < :to)) " +
                " ORDER BY b.name, b.firstname");
        query.setParameter("type", ResInfoTypeTools.TYPE_STAY);
        query.setParameter("from", from.toDate());
        query.setParameter("to", to.toDate());
        ArrayList<Resident> list = new ArrayList<Resident>(query.getResultList());
        em.close();
        return list;
    }

    public static ArrayList<Resident> getAllInactive() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM Resident b WHERE b.station IS NULL ORDER BY b.name, b.firstname");
        ArrayList<Resident> list = new ArrayList<Resident>(query.getResultList());
        em.close();
        return list;
    }

    public static ArrayList<Resident> getAllWithBirthdayIn(int days) {
        ArrayList<Resident> list = getAllActive();
        ArrayList<Resident> result = new ArrayList<Resident>();

        for (Resident resident : list) {
            DateMidnight birthday = new DateMidnight(resident.getDOB());
            DateMidnight now = new DateMidnight();
            if (
                    now.getDayOfYear() <= birthday.getDayOfYear() && now.getDayOfYear() + days >= birthday.getDayOfYear()
                            ||
                            now.getDayOfYear() <= birthday.getDayOfYear() + 365 && now.getDayOfYear() + days >= birthday.getDayOfYear() + 365
                    ) {
                result.add(resident);
            }
        }
        list.clear();

        return result;
    }


}
