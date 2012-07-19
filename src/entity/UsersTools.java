/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;

import op.OPDE;
import op.tools.SYSTools;

import java.awt.*;
import java.util.List;

/**
 *
 * @author tloehr
 */
public class UsersTools {
    public static final short STATUS_INACTIVE = 0;
    public static final short STATUS_ACTIVE = 1;

    public static ListCellRenderer
    getUserRenderer() {
        return new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                String text;
                if (o == null) {
                    text = OPDE.lang.getString("misc.commands.>>noselection<<");
                } else if (o instanceof Users) {
                    text = o.toString();
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
            }
        };
    }

    public static boolean isAdmin(Users user) {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("Groups.findByUserAndAdmin");
        query.setParameter("user", user);
        boolean admin = query.getResultList().size() > 0;
//        OPDE.debug("Benutzer ist " + (admin ? "" : "kein") + " Admin");
        em.close();
        return admin;
    }

    public static boolean isExamen(Users user) {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("Groups.findByUserAndExamen");
        query.setParameter("user", user);
        boolean examen = query.getResultList().size() > 0;
//        OPDE.debug("Benutzer ist " + (examen ? "" : "kein") + " Admin");
        em.close();
        return examen;
    }

    public static Users checkPassword(String username, String password) {
        EntityManager em = OPDE.createEM();
        Users user = null;
        try {
            Query query = em.createNamedQuery("Users.findForLogin");
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
