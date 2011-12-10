/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.system;

import java.util.Date;

import entity.EntityTools;
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

        if (user != null) {
            login = new SYSLogin(user);
            EntityTools.persist(login);
        }

        return login;
    }

    public static void logout() {
        logout(OPDE.getLogin());
    }

    protected static void logout(SYSLogin login) {

        if (login == null) {
            return;
        }

        EntityManager em = OPDE.createEM();
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
        } finally {
            em.close();
        }
    }

//    public static SYSLogin getPreviousLogin(SYSLogin login) {
//        return login;
//    }
}
