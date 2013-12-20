/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.system;

import java.util.Date;

import entity.EntityTools;
import op.OPDE;

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
        OPDE.setLogin(null);
    }

    protected static void logout(SYSLogin login) {

        if (login == null) {
            return;
        }

        EntityManager em = OPDE.createEM();
        login.setLogout(new Date());
        try {
            em.getTransaction().begin();
            em.merge(login);
            em.getTransaction().commit();
        } catch (Exception e) {
            OPDE.fatal(e);
        } finally {
            em.close();
        }
    }

}
