package de.offene_pflege.backend.services;

import de.offene_pflege.backend.entity.EntityTools;
import de.offene_pflege.backend.entity.done.Floors;
import de.offene_pflege.backend.entity.done.Homes;
import de.offene_pflege.backend.entity.done.Rooms;
import de.offene_pflege.backend.entity.info.ResInfo;
import de.offene_pflege.backend.entity.done.Resident;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.SYSCalendar;
import de.offene_pflege.op.tools.SYSTools;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA. User: tloehr Date: 03.11.12 Time: 14:05 To change this template use File | Settings | File
 * Templates.
 */
public class RoomsService {

    public static Rooms create(String text, Boolean single, Boolean bath, Floors floor) {
        Rooms rooms = new Rooms();
        rooms.setText(text);
        rooms.setSingle(single);
        rooms.setBath(bath);
        rooms.setActive(true);
        rooms.setFloor(floor);
        return rooms;

    }


    public static ListCellRenderer getRenderer() {
        return (jList, o, i, isSelected, cellHasFocus) -> {
            String text;
            if (o == null) {
                text = SYSTools.xx("misc.commands.>>noselection<<");
            } else {
                text = o.toString();
            }
            return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
        };
    }


    public static ArrayList<Rooms> getAllActive() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery(" SELECT r FROM Rooms r WHERE r.active = TRUE AND r.floor.home.active = TRUE ORDER BY r.floor.home.id, r.floor.level, r.text ");
        //SELECT b FROM LCustodian b WHERE b.status >= 0 ORDER BY b.name, b.vorname");
        ArrayList<Rooms> list = new ArrayList<Rooms>(query.getResultList());
        em.close();

        return list;
    }

    public static ArrayList<Rooms> getAllActive(Homes home) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery(" SELECT r FROM Rooms r WHERE r.active = TRUE AND r.floor.home = :home ");
        query.setParameter("home", home);
        ArrayList<Rooms> list = new ArrayList<Rooms>(query.getResultList());
        em.close();

        return list;
    }

    public static int getMaxLevel(Homes home) {

        Integer total = 0;
        //        int inUse = 0;

        try {
            EntityManager em = OPDE.createEM();
            Query query = em.createQuery("SELECT MAX(b.floor.level) FROM Rooms b WHERE b.active = TRUE AND b.floor.home = :home ");
            query.setParameter("home", home);
            total = (Integer) query.getSingleResult();
            em.close();
        } catch (NoResultException nre) {
            total = 0;
        } catch (Exception e) {
            OPDE.fatal(e);
        }
        return total;
    }


//    public static int countBeds(Homes home) {
//        int total = 0;
//        for (Rooms room : getAllActive(home)) {
//            total++;
//            if (!room.getSingle()) total++;
//        }
//        return total;
//    }

    public static int countBeds(Homes home, short level) {

        int total = 0;
//        int inUse = 0;

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM Rooms b WHERE b.floor.home = :home AND b.floor.level = :level AND b.active = TRUE");
        query.setParameter("level", level);
        query.setParameter("home", home);
        ArrayList<Rooms> listRooms = new ArrayList(query.getResultList());
        em.close();

        for (Rooms room : listRooms) {
            total++;
            if (!room.getSingle()) total++;
        }

//        ResInfoType rooms = ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_ROOM);
//        for (Resident resident : ResidentTools.getAllActive(date, date)) {
//            for (ResInfo resInfo : ResInfoTools.getAll(resident, rooms, SYSCalendar.midOfDay(date), SYSCalendar.midOfDay(date))) { // this is only one, sometimes none
//                Properties props = SYSTools.getProperties(resInfo.getProperties());
//                long rid = Long.parseLong(SYSTools.catchNull(props.getProperty("room.id"), "-1"));
//                if (rid > 0) {
//                    Rooms room = EntityTools.find(Rooms.class, rid);
//                    if (room.getStation().equals(station)){
//                        inUse++;
//                    }
//                }
//            }
//        }

        return total;
    }


    public static ArrayList<Rooms> getRooms(Homes home) {


        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM Rooms b WHERE b.floor.home = :home AND b.active = TRUE");
        query.setParameter("home", home);
        ArrayList<Rooms> listRooms = new ArrayList(query.getResultList());
        em.close();

        return listRooms;
    }


    public static Optional<Rooms> getRoom(Resident resident, DateTime datetime) {
        Optional<Rooms> room1 = Optional.empty();
        for (ResInfo resInfo : ResInfoService.getAll(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_ROOM), SYSCalendar.midOfDay(datetime).toDate(), SYSCalendar.midOfDay(datetime).toDate())) {
            Properties p1 = ResInfoService.getContent(resInfo);
            long rid1 = Long.parseLong(SYSTools.catchNull(p1.getProperty("room.id"), "-1"));
            room1 = Optional.ofNullable(EntityTools.find(Rooms.class, rid1));
        }
        return room1;
    }


//    /**
//     * @param resident
//     * @param day
//     * @return
//     * @relates #9
//     */
//    public static Rooms getRoomFor(Resident resident, LocalDate day) {
//        Rooms room = null;
//
//        ArrayList<Resident2Rooms> list = getRoomsFor(resident, day);
//        room = !list.isEmpty() ? list.get(list.size() - 1).getRoom() : null;
//
//        return room;
//    }

//    /**
//     * @param em
//     * @param resident
//     * @param enddate
//     * @throws Exception
//     * @relates #9
//     */
//    public static void closeAll(EntityManager em, Resident resident, Date enddate) throws Exception {
//        Query query = em.createQuery("" +
//                " SELECT r FROM Resident2Rooms r WHERE r.resident = :resident" +
//                "      AND r.to = :to ");
//
//        query.setParameter("resident", resident);
//        query.setParameter("to", SYSConst.DATE_UNTIL_FURTHER_NOTICE);
//
//        ArrayList<Resident2Rooms> list = new ArrayList(query.getResultList());
//
//        if (!list.isEmpty()) {
//            Resident2Rooms r2r = em.merge(list.get(0));
//            em.lock(r2r, LockModeType.OPTIMISTIC);
//            r2r.setTo(enddate);
//        }
//
//    }

//    public static DefaultComboBoxModel getCombobox4Levels() {
//
//        ArrayList<Station> listStat = new ArrayList();
//
//        DefaultComboBoxModel result = new DefaultComboBoxModel(listStat.toArray());
//
//        return result;
//    }

}
