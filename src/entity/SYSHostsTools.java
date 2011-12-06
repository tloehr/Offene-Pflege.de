/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSTools;

import javax.persistence.Query;
import java.net.InetAddress;
import java.util.Date;

/**
 * @author tloehr
 */
public class SYSHostsTools {

    public static SYSHosts getHost(String hostkey) {
        SYSHosts host = null;
        InetAddress localMachine = null;

        try {
            localMachine = InetAddress.getLocalHost();
        } catch (java.net.UnknownHostException uhe) {
            OPDE.fatal(uhe);
            System.exit(0);
        }

        Query query = OPDE.getEM().createNamedQuery("SYSHosts.findByHostKey");
        query.setParameter("hostKey", hostkey);
        boolean mainhost = SYSTools.catchNull(OPDE.getLocalProps().getProperty("mainhost")).equalsIgnoreCase("true");

        try {
            // doppelte Hostkeys können nicht auftreten. Die sind unique.
            host = (SYSHosts) query.getSingleResult();

            // Das bedeutet, dass der Host sich entweder korrekt abgemeldet hat oder sich mindestens 2 Minuten lang nicht gemeldet hat. Dann gehen wir davon aus, dass der Host abgestürzt ist.
            // Der Host scheint noch zu leben. Dann können wir nich nochmal starten. Gäb sonst Durcheinander.
            if (host.getLpol() != null && SYSCalendar.earlyEnough(host.getLpol().getTime(), 2)) {
                OPDE.fatal("Es gibt bereits einen aktiven Host mit demselben Hostkey.");
                host = null;
            } else {
                // ===================== REPARATUR DEFEKTER HOST EINTRÄGE ======================
                // Ein frühere Sitzung ist zusammengebrochen und nicht sauber beendet worden.
                // Da müssen wir erst aufräumen.
                if (host.getLpol() != null && !SYSCalendar.earlyEnough(host.getLpol().getTime(), 2)) {
                    OPDE.warn("Host wurde beim letzten mal nicht korrekt beendet. Wird jetzt behoben.");
                }

                OPDE.getEM().getTransaction().begin();
                try {
                    // Welche Running Classes hängen an diesem beschädigten Host ?
                    // Weg damit
                    String classesJPQL = " DELETE FROM SYSRunningClasses s WHERE s.login.host = :host ";
                    Query queryDeleteClasses = OPDE.getEM().createQuery(classesJPQL);
                    queryDeleteClasses.setParameter("host", host);
                    queryDeleteClasses.executeUpdate();

                    // Welche Logins (nach dem letzten Start) hängen an diesem beschädigten Host ?
                    // Weg damit
                    String loginsJPQL = " DELETE FROM SYSLogin l WHERE l.host = :host AND l.login >= l.host.up ";
                    Query queryDeleteLogins = OPDE.getEM().createQuery(loginsJPQL);
                    queryDeleteLogins.setParameter("host", host);
                    queryDeleteLogins.executeUpdate();

                    host.setLpol(null);
                    host.setUp(new Date());
                    OPDE.getEM().merge(host);
                    OPDE.getEM().getTransaction().commit();
                } catch (Exception e) {
                    OPDE.getEM().getTransaction().rollback();
                }

                if (mainhost) {
                    Query query2 = OPDE.getEM().createNamedQuery("SYSHosts.findOtherRunningMainHosts");
                    query2.setParameter("hostKey", hostkey);

                    if (!query2.getResultList().isEmpty()) {
                        SYSHosts alreadyRunningHost = (SYSHosts) query2.getResultList().get(0);
                        OPDE.warn("Es gibt bereits einen laufenden MainHost mit der Adresse: " + alreadyRunningHost.getIp());
                        OPDE.warn("Unsere Maschine läuft entgegen des Wunsches nun als normaler Host. Bitte local.properties reparieren.");
                        mainhost = false;
                    }
                }

                if (host.getMainHost() != mainhost) {
                    host.setMainHost(mainhost);
                    EntityTools.merge(host);
                }
            }
        } catch (Exception e) { // Neuer Host, der bisher noch nicht existierte. Dann legen wir den neu an.
            host = new SYSHosts(hostkey, localMachine.getHostName(), localMachine.getHostAddress(), mainhost);
            EntityTools.persist(host);
        }
        return host;
    }


    /**
     * Meldet den übergebenen Host ab, indem das Last Proof of Life auf NULL gesetzt wird.
     *
     * @param host
     */
    protected static void shutdown(SYSHosts host) {
        host.setLpol(null);
        EntityTools.merge(host);
    }

    /**
     * Meldet den aktuellen Host ab.
     */
    public static void shutdown() {
        shutdown(OPDE.getHost());
    }
}
