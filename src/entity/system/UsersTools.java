/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.system;

import entity.EntityTools;
import gui.GUITools;
import op.OPDE;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * @author tloehr
 */
public class UsersTools {
    public static final short STATUS_INACTIVE = 0;
    public static final short STATUS_ACTIVE = 1;
    public static final short STATUS_ROOT = 2;

    public static final int MAIL_UNCONFIRMED = 0;
    public static final int MAIL_CONFIRMED = 1;
    public static final int MAIL_NOTIFICATIONS_ENABLED = 2;

    public static ArrayList<Users> getUsers(boolean inactiveToo) {
        EntityManager em = OPDE.createEM();

        Query query;
        if (inactiveToo) {
            query = em.createQuery("SELECT u FROM Users u WHERE u.status <> :status ORDER BY u.nachname, u.vorname ");
            query.setParameter("status", STATUS_ROOT);
        } else {
            query = em.createQuery("SELECT u FROM Users u WHERE u.status = :status ORDER BY u.nachname, u.vorname ");
            query.setParameter("status", STATUS_ACTIVE);
        }

        ArrayList<Users> list = new ArrayList<Users>(query.getResultList());

        em.close();

        return list;

    }

//    public static String getFullnameWithID(Users user) {
//        return user.getName() + ", " + user.getVorname() + " [" + user.getUIDCiphered() + "]";
//    }


    public static ArrayList<Users> getUsers(String searchPattern, boolean inactiveToo) {
        EntityManager em = OPDE.createEM();

        Query query;
        if (inactiveToo) {
            query = em.createQuery("SELECT u FROM Users u WHERE u.status <> :status ORDER BY u.nachname, u.vorname ");
            query.setParameter("status", STATUS_ROOT);
        } else {
            query = em.createQuery("SELECT u FROM Users u WHERE (u.uid LIKE :pattern OR u.nachname LIKE :pattern OR u.vorname LIKE :pattern) AND u.status = :status ORDER BY u.nachname, u.vorname ");
            query.setParameter("status", STATUS_ACTIVE);
            query.setParameter("pattern", EntityTools.getMySQLsearchPattern(searchPattern));
        }

        ArrayList<Users> list = new ArrayList<Users>(query.getResultList());

        em.close();

        return list;

    }

    public static ListCellRenderer getRenderer() {
        return (jList, o, i, isSelected, cellHasFocus) -> {
            String text;
            if (o == null) {
                text = SYSTools.xx("misc.commands.>>noselection<<");
            } else if (o instanceof Users) {
                text = o.toString();
            } else {
                text = o.toString();
            }
            return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
        };
    }

    public static boolean isAdmin(Users user) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT g FROM Groups g WHERE g.gid = 'admin' AND :user MEMBER OF g.members");
        query.setParameter("user", user);
        boolean admin = query.getResultList().size() > 0;
//        OPDE.debug("Benutzer ist " + (admin ? "" : "kein") + " Admin");
        em.close();
        return admin;
    }

    public static Color getBG1(Users user) {
        Color active = GUITools.getColor("CEF0FF");
        Color closed = GUITools.getColor("C0C0C0");
        if (user.isActive()) {
            return active;
        }

        return closed;
    }

    public static boolean isQualified(Users user) {
        boolean qualified = false;
        for (Groups group : user.getGroups()) {
            if (group.isQualified()) {
                qualified = true;
                break;
            }
        }
        return qualified;
    }

    public static Users checkPassword(String username, String password) {
        EntityManager em = OPDE.createEM();
        Users user = null;
        try {
            Query query = em.createQuery("SELECT o FROM Users o WHERE o.uid = :uKennung AND o.md5pw = :md5pw");
            query.setParameter("uKennung", username);
            query.setParameter("md5pw", SYSTools.hashword(password));
            user = (Users) query.getSingleResult();
        } catch (Exception e) {
            OPDE.info(e);
        } finally {
            em.close();
        }

        return user;
    }

}
