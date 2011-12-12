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
        // MainHost Angabe aus der local.properties
        boolean mainhost = SYSTools.catchNull(OPDE.getLocalProps().getProperty("mainhost")).equalsIgnoreCase("true");

        try {
            // doppelte Hostkeys können nicht auftreten. Die sind unique.
            host = (SYSHosts) query.getSingleResult();

            // Das bedeutet, dass der Host sich entweder korrekt abgemeldet hat oder sich mindestens 2 Minuten lang nicht gemeldet hat.
            // Dann gehen wir davon aus, dass der Host abgestürzt ist.
            // Der Host scheint noch zu leben. Dann können wir nich nochmal starten. Gäb sonst Durcheinander.
            if (SYSCalendar.earlyEnough(host.getLpol().getTime(), 2)) {
                OPDE.fatal("Es gibt bereits einen aktiven Host mit demselben Hostkey.");
                host = null;
            } else {
                // ===================== REPARATUR DEFEKTER HOST EINTRÄGE ======================
                // Ein frühere Sitzung ist zusammengebrochen und nicht sauber beendet worden.
                // Da müssen wir erst aufräumen.

                OPDE.warn("Host wurde beim letzten mal nicht korrekt beendet. Wird jetzt behoben.");

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
                    queryDeleteLogins.setParameter("logout", host.getLpol());
                    queryDeleteLogins.executeUpdate();

                    if (mainhost) {
                        Query query2 = em.createNamedQuery("SYSHosts.findOtherMainHosts");
                        query2.setParameter("host", host);

                        if (!query2.getResultList().isEmpty()) {
                            SYSHosts alreadyRunningHost = (SYSHosts) query2.getResultList().get(0);
                            OPDE.warn("Es gibt bereits einen MainHost mit der Adresse: " + alreadyRunningHost.getIp());
                            OPDE.warn("Unsere Maschine läuft entgegen des Wunsches nun als normaler Host.");
                            mainhost = false;
                        }
                    }

                    host.setMainHost(mainhost); // Die lokale Einstellung überschreibt immer die Datenbank Einstellung des Hosts.
                    host.setLpol(new Date());
                    host.setUp(new Date());
                    em.merge(host);
                    em.getTransaction().commit();

                    OPDE.getLocalProps().setProperty("mainhost", Boolean.toString(mainhost));

                } catch (Exception e) {
                    OPDE.fatal(e);
                    em.getTransaction().rollback();
                }


            }
        } catch (Exception e) { // Neuer Host, der bisher noch nicht existierte. Dann legen wir den neu an.
            OPDE.debug(e);
            host = new SYSHosts(hostkey, localMachine.getHostName(), localMachine.getHostAddress(), mainhost);
            EntityTools.persist(host);
        } finally {
            em.close();
        }
        return host;
    }


    /**
     * Meldet den aktuellen Host ab, indem das Last Proof of Life auf NULL gesetzt wird.
     */
    public static void shutdown() {
        SYSMessagesTools.setAllMesages2Processed();
        OPDE.getHost().setLpol(SYSConst.DATE_VON_ANFANG_AN);
        OPDE.getHost().setUp(SYSConst.DATE_VON_ANFANG_AN);
        EntityTools.merge(OPDE.getHost());
        // OPDE.getBM().interrupt();
    }
}
