/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offene_pflege.entity.system;

import de.offene_pflege.entity.EntityTools;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.SYSTools;
import lombok.extern.log4j.Log4j2;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tloehr
 */
@Log4j2
public class UsersTools {
    public static final short STATUS_INACTIVE = 0;
    public static final short STATUS_ACTIVE = 1;
    public static final short STATUS_ROOT = 2;

    public static final int MAIL_UNCONFIRMED = 0;
    public static final int MAIL_CONFIRMED = 1;
    public static final int MAIL_NOTIFICATIONS_ENABLED = 2;

    public static ArrayList<OPUsers> getUsers(boolean inactiveToo) {
        EntityManager em = OPDE.createEM();

        Query query;
        if (inactiveToo) {
            query = em.createQuery("SELECT u FROM OPUsers u WHERE u.userstatus <> :status ORDER BY u.nachname, u.vorname ");
            query.setParameter("status", STATUS_ROOT);
        } else {
            query = em.createQuery("SELECT u FROM OPUsers u WHERE u.userstatus = :status ORDER BY u.nachname, u.vorname ");
            query.setParameter("status", STATUS_ACTIVE);
        }

        ArrayList<OPUsers> list = new ArrayList<OPUsers>(query.getResultList());

        em.close();

        return list;

    }

    public static ArrayList<OPUsers> getUsers(String searchPattern, boolean inactiveToo) {
        EntityManager em = OPDE.createEM();

        Query query;
        if (inactiveToo) {
            query = em.createQuery("SELECT u FROM OPUsers u WHERE u.userstatus <> :status ORDER BY u.nachname, u.vorname ");
            query.setParameter("status", STATUS_ROOT);
        } else {
            query = em.createQuery("SELECT u FROM OPUsers u WHERE (u.uid LIKE :pattern OR u.nachname LIKE :pattern OR u.vorname LIKE :pattern) AND u.userstatus = :status ORDER BY u.nachname, u.vorname ");
            query.setParameter("status", STATUS_ACTIVE);
            query.setParameter("pattern", EntityTools.getMySQLsearchPattern(searchPattern));
        }

        ArrayList<OPUsers> list = new ArrayList<OPUsers>(query.getResultList());

        em.close();

        return list;

    }

    public static ListCellRenderer getRenderer() {
        return (jList, o, i, isSelected, cellHasFocus) -> {
            String text;
            if (o == null) {
                text = SYSTools.xx("misc.commands.>>noselection<<");
            } else if (o instanceof OPUsers) {
                text = o.toString();
            } else {
                text = o.toString();
            }
            return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
        };
    }

    public static boolean isAdmin(OPUsers user) {

        EntityManager em = OPDE.createEM();


        Query query = em.createQuery("SELECT g FROM OPGroups g WHERE g.gid = 'admin' AND :user MEMBER OF g.members");
        query.setParameter("user", user);
        boolean admin = query.getResultList().size() > 0;
        em.close();
        return admin;
    }

    public static Color getBG1(OPUsers user) {
        Color active = GUITools.getColor("CEF0FF");
        Color closed = GUITools.getColor("C0C0C0");
        if (user.isActive()) {
            return active;
        }

        return closed;
    }

    public static boolean isQualified(OPUsers user) {
        boolean qualified = false;
        for (OPGroups group : user.getGroups()) {
            if (group.isQualified()) {
                qualified = true;
                break;
            }
        }
        return qualified;
    }

    public static OPUsers checkPassword(String username, String password) {
        EntityManager em = OPDE.createEM();
        OPUsers user = null;
        try {
            Query query = em.createQuery("SELECT o FROM OPUsers o WHERE o.uid = :uKennung AND o.hashed_pw = :hashed_pw");
            query.setParameter("uKennung", username);

            // zwei versuche. Wenn das PW bereits in SHA256 vorliegt sind wir fertig
            query.setParameter("hashed_pw", SYSTools.hashword(password, "SHA-256"));
            List results = query.getResultList();

            if (results.isEmpty()){ // PW falsch oder noch in MD5
                log.debug("password seems to be wrong or wrong encoding. trying md5");
                query.setParameter("hashed_pw", SYSTools.hashword(password, "MD5"));
                results = query.getResultList();
                if (!results.isEmpty()) {
                    log.debug("old md5 encoding found. correcting");
                    // passwort war noch in md5 gespeichert. aktualisieren
                    user = (OPUsers) results.get(0);
                    user.setHashed_pw(SYSTools.hashword(password, "SHA-256"));
                    EntityTools.merge(user);
                }
            } else {
                user = (OPUsers) results.get(0);
            }
        } catch (Exception e) {
            log.info(e);
        } finally {
            em.close();
        }

        return user;
    }

    public static String getFullname(OPUsers opUsers) {
        String fullname = "";
        if (OPDE.isUserCipher()) {
            fullname = "#" + opUsers.getCipherid();
        } else {
            fullname = opUsers.getName() + ", " + opUsers.getVorname();
        }
        return fullname;
    }

}
