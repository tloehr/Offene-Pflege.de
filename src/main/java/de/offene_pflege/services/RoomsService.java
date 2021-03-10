package de.offene_pflege.services;

import de.offene_pflege.entity.EntityTools;
import de.offene_pflege.entity.building.Floors;
import de.offene_pflege.entity.building.Homes;
import de.offene_pflege.entity.building.Rooms;
import de.offene_pflege.entity.info.ResInfo;
import de.offene_pflege.entity.info.ResInfoTools;
import de.offene_pflege.entity.info.ResInfoTypeTools;
import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.JavaTimeConverter;
import de.offene_pflege.op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;
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
                text = toPrettyString((Rooms) o);
            }
            return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
        };
    }


    public static ArrayList<Rooms> getAllActive() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery(" SELECT r FROM Rooms r WHERE r.active = TRUE AND r.floor.home.active = TRUE ORDER BY r.floor.home.id, r.floor.level, r.text ");
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


    /**
     * Ermittelt den Raum, in dem der BW am Stichtag gewohnt hat. Wenn es denn einen gab.
     *
     * @param resident
     * @param ldt
     * @return
     */
    public static Optional<Rooms> getRoom(Resident resident, java.time.LocalDateTime ldt) {
        Optional<Rooms> room1 = Optional.empty();
        Date noon = JavaTimeConverter.toDate(ldt.withHour(12).withMinute(0).withSecond(0));
        for (ResInfo resInfo : ResInfoTools.getAll(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_ROOM), noon, noon)) {
            Properties p1 = ResInfoTools.getContent(resInfo);
            long rid1 = Long.parseLong(SYSTools.catchNull(p1.getProperty("room.id"), "-1"));
            room1 = Optional.ofNullable(EntityTools.find(Rooms.class, rid1));
        }
        return room1;
    }

    public static String toPrettyString(Rooms rooms) {
        return SYSTools.xx("misc.msg.room") + " " + rooms.getText() + ", " + rooms.getFloor().getName() + ", " + rooms.getFloor().getHome().getName();
    }

}
