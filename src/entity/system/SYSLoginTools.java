/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.system;

import java.util.Date;

import entity.Users;
import entity.UsersTools;
import op.OPDE;
import op.tools.SYSConst;

import javax.persistence.EntityManager;

/**
 *
 * @author tloehr
 */
public class SYSLoginTools {

    public static SYSLogin login(String username, String password) {
        SYSLogin login = null;
        Users user = UsersTools.checkPassword(username, password);
        EntityManager em = OPDE.createEM();
        if (user != null) {
            em.getTransaction().begin();
            login = new SYSLogin(user);
            em.persist(login);
            em.getTransaction().commit();
        }

        return login;
    }

    public static void logout() {
        logout(OPDE.getLogin());
    }

    protected static void logout(SYSLogin login) {
        EntityManager em = OPDE.createEM();
        if (login == null) {
            return;
        }

        login.setLogout(new Date());
        em.getTransaction().begin();
        try {
            SYSRunningClassesTools.endAllModules(login);
            em.merge(login);
            em.getTransaction().commit();
        } catch (Exception e) {
            login.setLogout(SYSConst.DATE_BIS_AUF_WEITERES);
            em.getTransaction().rollback();
            OPDE.getLogger().debug(e);
        }
    }

    public static SYSLogin getPreviousLogin(SYSLogin login) {
        return login;
    }
}
