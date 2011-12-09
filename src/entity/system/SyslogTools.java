/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.system;

import java.net.InetAddress;
import java.util.Date;
import op.OPDE;

import javax.persistence.EntityManager;

/**
 *
 * @author tloehr
 */
public class SyslogTools {
    public static final short INFO = 0;
    public static final short ERROR = 1;
    public static final short WARN = 2;
    public static final short FATAL = 3;

    public static void info(String message){
        addLog(message, INFO);
    }

    public static void error(String message){
        addLog(message, ERROR);
    }

    public static void warn(String message){
        addLog(message, WARN);
    }

    public static void fatal(String message){
        addLog(message, FATAL);
    }

    public static void addLog(String message, short level){
        EntityManager em = OPDE.createEM();
        InetAddress localMachine = null;

        try {
            localMachine = InetAddress.getLocalHost();
        } catch (java.net.UnknownHostException uhe) {
            System.exit(1);
        }

        em.getTransaction().begin();
        em.persist(new Syslog(localMachine.getHostName(), localMachine.getHostAddress(), OPDE.getLocalProps().getProperty("hostkey"), new Date(), message, level, OPDE.getLogin()));
        em.getTransaction().commit();
    }

}
