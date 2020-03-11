/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offene_pflege.entity.info;

import de.offene_pflege.entity.EntityTools;
import de.offene_pflege.entity.building.Homes;
import de.offene_pflege.entity.building.Station;
import de.offene_pflege.entity.nursingprocess.NursingProcessTools;
import de.offene_pflege.entity.prescription.MedInventoryTools;
import de.offene_pflege.entity.prescription.PrescriptionTools;
import de.offene_pflege.entity.process.QProcessTools;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.JavaTimeConverter;
import de.offene_pflege.op.tools.SYSCalendar;
import de.offene_pflege.op.tools.SYSTools;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.Years;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author tloehr
 */
public class ResidentTools {

    public static final int AGE_MINOR = 18;
    public static final int MALE = 1;
    public static final int FEMALE = 2;
    public static final int DIVERSE = 3;
    // Das einfügen des 3. Geschlechts ist nicht einfach so möglich, weil teilweise Berechnungen des Gewichtes und des Grundumsatzes darauf basieren und da gibt es keine 3. Geschlecht.

    public static final String GENDER[] = {"", SYSTools.xx("misc.msg.male"), SYSTools.xx("misc.msg.female"), SYSTools.xx("misc.msg.diverse")};
    public static final String ADDRESS[] = {"", SYSTools.xx("misc.msg.termofaddress.mr"), SYSTools.xx("misc.msg.termofaddress.mrs")};
    public static final String KEY_STOOLDAYS = "stooldays";
    public static final String KEY_DATE1 = "date1";
    public static final String KEY_DATE2 = "date2";
    public static final String KEY_DATE3 = "date3";
    public static final String KEY_BALANCE = "liquidbalance";
    public static final String KEY_LOWIN = "lowin";
    public static final String KEY_TARGETIN = "targetin";
    public static final String KEY_HIGHIN = "highin";
    public static final String KEY_DAYSDRINK = "daysdrink";
    public static final short ADMINONLY = 2;
    public static final short NORMAL = 0;

    public static Resident createResident(EntityManager em, String nachname, String vorname, int geschlecht, Date gebdatum) {
        Resident resident = new Resident();
        resident.setName(nachname);
        resident.setFirstname(vorname);
        resident.setGender(geschlecht);
        resident.setDob(gebdatum);
        resident.setEditor(OPDE.getLogin().getUser());
        resident.setAdminonly((short) 0);
        setControlling(resident, null);
        resident.setCalcMediUPR1(OPDE.isCalcMediUPR1());
        return resident;
    }

    public static void setControlling(Resident resident, Properties props) {
        if (props == null) {
            resident.setControlling(null);
            return;
        }
        try {
            StringWriter writer = new StringWriter();
            props.store(writer, null);
            resident.setControlling(writer.toString());
            writer.close();
        } catch (IOException ex) {
            OPDE.fatal(ex);
        }
    }

    public static Properties getControlling(Resident resident) {
        Properties props = new Properties();
        if (resident.getControlling() != null) {
            try {
                StringReader reader = new StringReader(resident.getControlling());
                props.load(reader);
                reader.close();
            } catch (IOException ex) {
                OPDE.fatal(ex);
            }
        }
        return props;
    }

    public static ListCellRenderer getRenderer() {
        return (jList, o, i, isSelected, cellHasFocus) -> {
            String text;
            if (o == null) {
                text = SYSTools.toHTMLForScreen(SYSTools.xx("misc.commands.>>noselection<<"));
            } else if (o instanceof Resident) {
                text = o.toString();
            } else {
                text = o.toString();
            }
            return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
        };
    }

    public static boolean isMinor(Resident resident) {
        return getAge(resident).getYears() < AGE_MINOR;
    }

    public static String getNameAndFirstname(Resident resident) {
        return ResidentTools.getName(resident) + ", " + ResidentTools.getFirstname(resident);
    }

//    public static String getResidentLabelWithBDay(Resident bewohner) {
//        return "(*" + DateFormat.getDateInstance().format(bewohner.getDob()) + ") [" + bewohner.getRIDAnonymous() + "]";
//    }

    public static String getTextCompact(Resident resident) {
        return ResidentTools.getName(resident) + ", " + ResidentTools.getFirstname(resident) + " [" + SYSTools.anonymizeRID(resident.getId()) + "]";
    }

    public static Date getDob(Resident resident) {
        return SYSTools.anonymizeDate(resident.getDob());
    }

    public static String getName(Resident resident) {
        return SYSTools.anonymizeName(resident.getName(), SYSTools.INDEX_LASTNAME);
    }

    public static String getFirstname(Resident resident) {
        int index = (resident.getGender() == ResidentTools.MALE ? SYSTools.INDEX_FIRSTNAME_MALE : SYSTools.INDEX_FIRSTNAME_FEMALE);
        return SYSTools.anonymizeName(resident.getFirstname(), index);
    }

//    public static void setBWLabel(JLabel lblBW, Resident bewohner) {
//        lblBW.setFont(new java.awt.Font("Dialog", 1, 18));
//        lblBW.setHorizontalAlignment(SwingConstants.LEADING);
//        lblBW.setForeground(new java.awt.Color(255, 51, 0));
//        lblBW.setText(getLabelText(bewohner));
//    }

    public static String getFullName(Resident resident) {
        return ADDRESS[resident.getGender()] + " " + ResidentTools.getFirstname(resident) + " " + ResidentTools.getName(resident);
    }

//    public static boolean isWeiblich(Bewohner bewohner) {
//        return bewohner.getGender() == FEMALE;
//    }


    public static Years getAge(Resident resident) {
        boolean dead = ResInfoTools.isDead(resident);
        ResInfo stay = ResInfoTools.getLastResinfo(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_STAY));
        LocalDate birthdate = new DateTime(getDob(resident)).toLocalDate();
        DateTime refdate = dead ? new DateTime(stay.getTo()) : new DateTime();
        return Years.yearsBetween(birthdate.toDateTimeAtStartOfDay(), refdate);

    }

    public static String getLabelText(Resident resident) {
        boolean dead = ResInfoTools.isDead(resident);
        boolean gone = ResInfoTools.isGone(resident);
        ResInfo stay2 = ResInfoTools.getLastResinfo(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_STAY));
        ResInfo stay1 = ResInfoTools.getFirstResinfo(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_STAY));

        DateFormat df = DateFormat.getDateInstance();
        String result = ResidentTools.getName(resident) + ", " + ResidentTools.getFirstname(resident) + " (*" + df.format(getDob(resident)) + "), ";


        result += getAge(resident).getYears() + " " + SYSTools.xx("misc.msg.Years") + " [" + SYSTools.anonymizeRID(resident.getId()) + "]";

        if (dead || gone) {
            // https://github.com/tloehr/Offene-Pflege.de/issues/81
            if (stay1 != null && stay2 != null) {
                result += "  " + SYSTools.xx("misc.msg.movein") + ": " + df.format(stay1.getFrom()) + ", ";
                result += (dead ? SYSTools.xx("misc.msg.late") : SYSTools.xx("misc.msg.movedout")) + ": " + df.format(stay2.getTo());
            }
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
     * @param em       as it is quite a complex operation, it runs within a surrounding EM to trigger rollbacks if
     *                 necessary
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

    public static Optional<Resident> findByPK(String rid) {
        Optional<Resident> resident;
        EntityManager em = OPDE.createEM();
        try {

            Query query = em.createQuery(" " +
                    " SELECT b FROM Resident b " +
                    " WHERE b.id = :rid ");

            query.setParameter("rid", rid);
            resident = Optional.of((Resident) query.getSingleResult());
        } catch (Exception e) {
            resident = Optional.empty();
        } finally {
            em.close();
        }
        return resident;
    }

    public static ArrayList<Resident> findByPK(String... rids) {
        ArrayList<Resident> list = new ArrayList<>();
        for (String rid : rids) {
            findByPK(rid).ifPresent(resident -> list.add(resident));
        }
        return list;
    }


    public static List<Resident> getAll(LocalDate start, LocalDate end) {
        return getAll(start.toDateTimeAtStartOfDay(), SYSCalendar.eod(end));
    }


    public static List<Resident> getAll(java.time.LocalDateTime target_date) {
        LocalDateTime target = JavaTimeConverter.toJodaLocalDateTime(target_date);
        return getAll(target.toDateTime(), target.toDateTime());
    }

    public static List<Resident> getAll(Homes home, java.time.LocalDateTime target_date) {
        LocalDateTime target = JavaTimeConverter.toJodaLocalDateTime(target_date);
        return getAll(target.toDateTime(), target.toDateTime()).stream().filter(resident -> resident.getStation().getHome().equals(home)).collect(Collectors.toList());
    }


    /**
     * Alle BW die in der Einrichtung wohnen, innerhalb eines bestimmten Intervals.
     *
     * @param start
     * @param end
     * @return
     */
    public static ArrayList<Resident> getAll(DateTime start, DateTime end) {
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
            query.setParameter("from", start.toDate());
            query.setParameter("to", end.toDate());
            list = new ArrayList<Resident>(query.getResultList());
        } catch (Exception e) {
            OPDE.fatal(e);
        } finally {
            em.close();
        }
        return list;
    }

//    /**
//     * bestimmt die Liste aller BWs, die zum Zeitpunkt target in der Einrichtung home gewohnt haben.
//     *
//     * @param target
//     * @param home
//     * @return
//     */
//    public static ArrayList<Resident> getAll(DateTime target, Homes home) {
//        ArrayList<Resident> result = new ArrayList<>();
//        getAll(target, target).forEach(resident -> {
//            RoomsTools.getRoom(resident, target).ifPresent(rooms -> {
//                if (rooms.getFloor().getHome().equals(home)) {
//                    result.add(resident);
//                }
//            });
//        });
//        return result;
//    }

//    public static ArrayList<Resident> getAllActiveAndPresent(LocalDate day) {
//        ArrayList<Resident> list = getAllActive(day, day);
//        ArrayList<Resident> listOnlyPresent = new ArrayList<>();
//        for (Resident resident : list) {
//            if (!ResInfoTools.wasAway(resident, day)) {
//                listOnlyPresent.add(resident);
//            }
//        }
//        list.clear();
//        return listOnlyPresent;
//    }

    /**
     * retrieves a list of all residents who were staying in the home during that particular month. They are also
     * included if they have left or arrived in that time period.
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
     * Ermittelt eine Liste von baldigen Geburtstagen (Resident, Neues Alter, in wieviel Tagen)
     *
     * @param days - maximaler Betrachtungszeitraum
     * @return
     */
    public static ArrayList<Triple<Resident, Long, Long>> getAllWithBirthdayIn(int days) {

//        String mysql = "" +
//                "SELECT id, DATE_FORMAT(NOW(), '%Y') - DATE_FORMAT(GebDatum, '%Y') + IF(DATE_FORMAT(GebDatum, '%m%d') < DATE_FORMAT(NOW(), '%m%d'), 1, 0) AS new_age, " +
//                "DATEDIFF(GebDatum + INTERVAL YEAR(NOW()) - YEAR(GebDatum) + IF(DATE_FORMAT(NOW(), '%m%d') > DATE_FORMAT(GebDatum, '%m%d'), 1, 0) YEAR, NOW()) AS days_to_birthday " +
//                "FROM resident res " +
//                "WHERE res.StatID IS NOT NULL " +
//                "HAVING days_to_birthday < ? " +
//                "ORDER BY days_to_birthday ASC ";
//

        ArrayList<Resident> listResident = getAllActive();
        ArrayList<Triple<Resident, Long, Long>> baldigeGeburtstage = new ArrayList<>();

        listResident.forEach(resident -> {
            java.time.LocalDate dob = JavaTimeConverter.toJavaLocalDateTime(resident.getDob()).toLocalDate();
            java.time.LocalDate now = java.time.LocalDate.now();
            java.time.LocalDate dob1 = dob.withYear(now.getYear()); // auf dieses Jahr anpassen
            if (dob1.isBefore(now))
                dob1 = dob1.withYear(now.getYear() + 1); // Geburstag war schon, also liegt der nächste im nächsten Jahr

            long daysbetween = ChronoUnit.DAYS.between(now, dob1);
            if (daysbetween <= days) {
                // ursprünglicher Geburtstag
                baldigeGeburtstage.add(new ImmutableTriple<>(resident, ChronoUnit.YEARS.between(dob, now) + 1, daysbetween));
            }
        });
        listResident.clear();

        return baldigeGeburtstage;
    }

    public static boolean isActive(Resident resident) {
        return resident.getStation() != null;
    }


}
