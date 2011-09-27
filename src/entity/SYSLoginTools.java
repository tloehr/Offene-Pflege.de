/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.util.Date;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSConst;

/**
 *
 * @author tloehr
 */
public class SYSLoginTools {

    public static SYSLogin login(String username, String password) {
        SYSLogin login = null;
        Users user = UsersTools.checkPassword(username, password);

        if (user != null) {
            OPDE.getEM().getTransaction().begin();
            login = new SYSLogin(user);
            OPDE.getEM().persist(login);
            OPDE.getEM().getTransaction().commit();
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

        login.setLogout(new Date());
        OPDE.getEM().getTransaction().begin();
        try {
            SYSRunningClassesTools.endAllModules(login);
            OPDE.getEM().merge(login);
            OPDE.getEM().getTransaction().commit();
        } catch (Exception e) {
            login.setLogout(SYSConst.DATE_BIS_AUF_WEITERES);
            OPDE.getEM().getTransaction().rollback();
            OPDE.getLogger().debug(e);
        }
    }

    public static SYSLogin getPreviousLogin(SYSLogin login) {
        return login;
    }
}
