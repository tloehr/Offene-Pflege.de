/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offene_pflege.entity.system;

import de.offene_pflege.entity.EntityTools;
import de.offene_pflege.op.OPDE;

import javax.persistence.EntityManager;
import java.util.Date;

/**
 *
 * @author tloehr
 */
public class SYSLoginTools {

    public static SYSLogin login(String username, String password) {
        SYSLogin login = null;
        OPUsers user = UsersTools.checkPassword(username, password);

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
        try {
            em.getTransaction().begin();
            login = em.merge(login);
            em.getTransaction().commit();
            OPDE.setLogin(login);
        } catch (Exception e) {
            OPDE.fatal(e);
        } finally {
            em.close();
            OPDE.closeEMF();
        }
    }

}
