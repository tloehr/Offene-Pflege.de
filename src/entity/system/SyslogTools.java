/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.system;

import entity.EntityTools;
import op.OPDE;

import javax.persistence.EntityManager;
import java.net.InetAddress;
import java.util.Date;

/**
 * @author tloehr
 */
public class SyslogTools {
    public static final short INFO = 0;
    public static final short WARN = 1;
    public static final short ERROR = 2;
    public static final short FATAL = 3;

    public static void info(String message) {
        addLog(message, INFO);
    }

    public static void error(String message) {
        addLog(message, ERROR);
    }

    public static void warn(String message) {
        addLog(message, WARN);
    }

    public static void fatal(String message) {
        addLog(message, FATAL);
    }

    public static void addLog(String message, short level) {

        InetAddress localMachine = null;


        SYSLogin login = OPDE.getLogin();

        message = String.format("[%s] [%s] %s", Integer.toString(OPDE.getAppInfo().getBuildnum()), login != null ? login.getUser().getUID() : "nobody" , message);

        try {
            localMachine = InetAddress.getLocalHost();
        } catch (java.net.UnknownHostException uhe) {
            OPDE.fatal(uhe);
        }

        EntityTools.persist(new Syslog(localMachine.getHostName(), localMachine.getHostAddress(), OPDE.getLocalProps().getProperty("hostkey"), new Date(), message, level, OPDE.getLogin()));

    }

    public static void addLog(EntityManager em, String message, short level) throws Exception {

        InetAddress localMachine = null;
        SYSLogin login = OPDE.getLogin();
        message = String.format("[%s] [%s] %s", Integer.toString(OPDE.getAppInfo().getBuildnum()), login != null ? login.getUser().getUID() : "nobody", message);

        try {
            localMachine = InetAddress.getLocalHost();
        } catch (java.net.UnknownHostException uhe) {
            System.exit(1);
        }

        em.merge(new Syslog(localMachine.getHostName(), localMachine.getHostAddress(), OPDE.getLocalProps().getProperty("hostkey"), new Date(), message, level, OPDE.getLogin()));

    }

}
