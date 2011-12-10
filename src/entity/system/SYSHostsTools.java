/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.system;

import entity.EntityTools;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
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
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("SYSHosts.findByHostKey");
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

                try {
                    em.getTransaction().begin();
                    // Welche Running Classes hängen an diesem beschädigten Host ?
                    // Weg damit
                    String classesJPQL = " DELETE FROM SYSRunningClasses s WHERE s.login.host = :host ";
                    Query queryDeleteClasses = em.createQuery(classesJPQL);
                    queryDeleteClasses.setParameter("host", host);
                    queryDeleteClasses.executeUpdate();

                    // Welche unbeendeten Logins hängen an diesem Host ?
                    // Korrigieren
                    String loginsJPQL = " UPDATE SYSLogin l SET l.logout = l.host.lpol WHERE l.host = :host AND l.logout = :logout ";
                    Query queryDeleteLogins = em.createQuery(loginsJPQL);
                    queryDeleteLogins.setParameter("host", host);
                    queryDeleteLogins.setParameter("logout", SYSConst.DATE_BIS_AUF_WEITERES);
                    queryDeleteLogins.executeUpdate();

                    host.setLpol(null);
                    host.setUp(new Date());
                    em.merge(host);
                    em.getTransaction().commit();
                } catch (Exception e) {
                    em.getTransaction().rollback();
                }

                if (mainhost) {
                    Query query2 = em.createNamedQuery("SYSHosts.findOtherRunningMainHosts");
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
        } finally {
            em.close();
        }
        return host;
    }


    /**
     * Meldet den übergebenen Host ab, indem das Last Proof of Life auf NULL gesetzt wird.
     *
     * @param host
     */
    protected static void shutdown(SYSHosts host) {
        SYSMessagesTools.setAllMesages2Processed(host);
        host.setLpol(null);
        host.setUp(null);
        EntityTools.merge(host);
        // OPDE.getBM().interrupt();
    }

    /**
     * Meldet den aktuellen Host ab.
     */
    public static void shutdown() {
        shutdown(OPDE.getHost());
    }
}
