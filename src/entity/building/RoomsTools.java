package entity.building;

import op.OPDE;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 03.11.12
 * Time: 14:05
 * To change this template use File | Settings | File Templates.
 */
public class RoomsTools {


    public static ListCellRenderer getRenderer() {
        return (jList, o, i, isSelected, cellHasFocus) -> {
            String text;
            if (o == null) {
                text = SYSTools.xx("misc.commands.>>noselection<<");
//            } else if (o instanceof Rooms) {
//                text = o.toString();
            } else {
                text = o.toString();
            }
            return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
        };
    }


    public static ArrayList<Rooms> getAllActive() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery(" SELECT r FROM Rooms r WHERE r.active = TRUE ORDER BY r.floor.home.eid, r.floor.level, r.text ");
        //SELECT b FROM LCustodian b WHERE b.status >= 0 ORDER BY b.name, b.vorname");
        ArrayList<Rooms> list = new ArrayList<Rooms>(query.getResultList());
        em.close();

        return list;
    }

    public static short getMaxLevel(Homes home) {

        short total = 0;
        //        int inUse = 0;

        try {
            EntityManager em = OPDE.createEM();
            Query query = em.createQuery("SELECT MAX(b.floor.level) FROM Rooms b WHERE b.floor.home = :home ");
            query.setParameter("home", home);
            total = (short) query.getSingleResult();
            em.close();
        } catch (NoResultException nre){
            total = 0;
        } catch (Exception e){
            OPDE.fatal(e);
        }
        return total;
    }


    public static int getBedsTotal(Homes home, short level) {

        int total = 0;
//        int inUse = 0;

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM Rooms b WHERE b.floor.home = :home AND b.level = :level AND b.active = TRUE");
        query.setParameter("level", level);
        query.setParameter("home", home);
        ArrayList<Rooms> listRooms = new ArrayList(query.getResultList());
        em.close();

        for (Rooms room : listRooms) {
            total++;
            if (!room.isSingle()) total++;
        }

//        ResInfoType rooms = ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_ROOM);
//        for (Resident resident : ResidentTools.getAllActive(date, date)) {
//            for (ResInfo resInfo : ResInfoTools.getAll(resident, rooms, SYSCalendar.midOfDay(date), SYSCalendar.midOfDay(date))) { // this is only one, sometimes none
//                Properties props = SYSTools.load(resInfo.getProperties());
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

//    /**
//     * @param resident
//     * @param day
//     * @return
//     * @relates #9
//     */
//    public static ArrayList<Resident2Rooms> getRoomsFor(Resident resident, LocalDate day) {
//        EntityManager em = OPDE.createEM();
//        Query query = em.createQuery("" +
//                " SELECT r FROM Resident2Rooms r WHERE r.resident = :resident" +
//                "      AND ((r.from <= :from AND r.to >= :from) OR " +
//                "      (r.from <= :to AND r.to >= :to) OR " +
//                "      (r.from > :from AND r.to < :to)) " +
//                " ORDER BY r.from ");
//        query.setParameter("resident", resident);
//        query.setParameter("from", day.toDateTimeAtStartOfDay().toDate());
//        query.setParameter("to", SYSCalendar.eod(day));
//        ArrayList<Resident2Rooms> list = new ArrayList(query.getResultList());
//        em.close();
//
//        return list;
//    }


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

    public static DefaultComboBoxModel getCombobox4Levels() {

        ArrayList<Station> listStat = new ArrayList();

        DefaultComboBoxModel result = new DefaultComboBoxModel(listStat.toArray());

        return result;
    }

}
