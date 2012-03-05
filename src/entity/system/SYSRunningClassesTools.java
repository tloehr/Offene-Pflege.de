/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.system;

import entity.EntityTools;
import op.OPDE;
import op.tools.Pair;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author tloehr
 */
public class SYSRunningClassesTools {


    public static final short STATUS_DONT_CARE = -1;
    public static final short STATUS_RO = 0;
    public static final short STATUS_RW = 1;

    /**
     * Diese Methode versucht eine signierte Klasse zu starten. Das geht nur, wenn nicht eine andere Klasse mit derselben Signatur läuft oder eine
     * unsignierte Klasse aus derselben Konfliktgruppe. Es wird immer versucht, den Zustand RW zu erhalten. Wenn ein Konflikt vorliegt,
     * wird nur ein RO Zugriff gewährt.
     *
     * @param internalClassID eigene Klass ID des zu startenden Moduls
     * @param signature um die es geht
     * @param conflictGroup Liste der Module, die mit diesem hier kollidieren würden
     * @return Ein Paar : [neu erzeugtes Modul, blockierendes Modul (wenns eins gibt, null sonst)]
     */
    public static Pair<SYSRunningClasses, SYSRunningClasses> startModule(String internalClassID, Object signature, String[] conflictGroup) {
        SYSRunningClasses myClass, blocking = null;

        // 1. Ist eine unsignierte Klasse in meiner Gruppe aktiv ? Dann bin ich immer RO
        List<SYSRunningClasses> list = getRunning("", STATUS_DONT_CARE, conflictGroup);
        OPDE.debug("SYSRunningClassesTools.startModule/3: " + internalClassID + " will starten.");
        OPDE.debug("SYSRunningClassesTools.startModule/3: Signatur: " + signature);

        short status;

        if (list.isEmpty()) {
            OPDE.debug("SYSRunningClassesTools.startModule/3: eine usignierte blockiert sie schonmal nicht");
            // 2. sind signierte (mit gleicher Signatur) aus meiner Gruppe mit Status RW aktiv ?
            // dann bin ich RO
            list = getRunning(signature, STATUS_RW, conflictGroup);
            if (!list.isEmpty()) {
                blocking = list.get(0);
                OPDE.debug("SYSRunningClassesTools.startModule/3: STATUS_RO");
                status = STATUS_RO;
            } else {
                // 3. Ansonsten darf ich RW sein.
                status = STATUS_RW;
                OPDE.debug("SYSRunningClassesTools.startModule/3: STATUS_RW");
            }
        } else {
            OPDE.debug("SYSRunningClassesTools.startModule/3: durch unsignierte geblockt.");
            OPDE.debug("SYSRunningClassesTools.startModule/3: STATUS_RO");
            status = STATUS_RO;
            blocking = list.get(0);
        }

        myClass = new SYSRunningClasses(internalClassID, signature.toString(), status);
        EntityTools.persist(myClass);
        OPDE.debug("SYSRunningClassesTools.startModule/3: RunningClass wird angelegt RCID: " + myClass.getRcid());

        return new Pair<SYSRunningClasses, SYSRunningClasses>(myClass, blocking);
    }

    /**
     * Methode, die für den Start eines Moduls aufegrufen wird, dass unsigniert ist. Alle anderen laufenden Module innerhalb der Konfliktgruppe müssen
     * beendet werden. Diese Methode macht 5 Versuche Nachrichten an die "anderen" zu schicken. Danach scheitert die Ausführung.
     *
     * @param internalClassID eigene Klass ID des zu startenden Moduls
     * @param conflictGroup Liste der Module, die mit diesem hier kollidieren würden
     * @param maxtries die Anzahl der Versuche, die die Methode unternimmt, die anderen Module zu kontaktieren, bevor sie aufgibt.
     * @param message die Textnachricht, die an die anderen Module geschickt wird.
     * @return Bei Erfolg wird das neu erzeugte running Classes Modul zurück gegeben. NULL bei Misserfolg.
     */
    public static SYSRunningClasses startModule(String internalClassID, String[] conflictGroup, int maxtries, String message) {
        SYSRunningClasses result = null;
        int tries = 0;
        OPDE.debug("SYSRunningClassesTools.startModule/5: " + internalClassID + " will starten.");

        // 1. ist ein unsignierter aus der conflictGroup da ? Dann mache ich direkt Schluss. Und der return ist false.
        boolean block = !getRunning("", STATUS_DONT_CARE, conflictGroup).isEmpty();
        OPDE.debug("SYSRunningClassesTools.startModule/5: unsigned blockade gefunden: " + block);

        // 2. sind signierte da, dann ist der return true. ich versuche dann solange bis ich erfolg habe und rufe dann success auf.
        // wenn ich keinen erfolg habe (nach tries versuchen), dann rufe ich fail auf.
        List<SYSRunningClasses> listSignedBlocks = new ArrayList<SYSRunningClasses>();
        if (!block) { // bisher keine Blockade gefunden ? Dann weiter.
            // Zuerst trage ich mich als status RW ein. Das wird erstmal alle weiteren davon abhalten mir
            // in die Quere zu kommen.
            result = new SYSRunningClasses(internalClassID, "", STATUS_RW);
            EntityTools.persist(result);
            OPDE.debug("SYSRunningClassesTools.startModule/5: RunningClass wird vorab angelegt RCID: " + result.getRcid());

            boolean done = false;
            while (!done) {
                tries++;
                listSignedBlocks = getRunning(null, STATUS_RW, conflictGroup);
                if (tries == 1) {
                    notifyClasses(listSignedBlocks, SYSMessagesTools.CMD_DO_LOGOUT, message);
                }
                done = listSignedBlocks.isEmpty() || tries > maxtries;
                if (done) {
                    break;
                }
                try {
                    OPDE.debug("SYSRunningClassesTools.startModule/5: Bisher keinen Erfolg. Warte 1 Minute. " + tries + "/" + maxtries + " Versuche.");
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    OPDE.fatal(e);
                }
            }
            OPDE.debug("SYSRunningClassesTools.startModule/5: Abschluss");

            // hat's geklappt ?
            if (!listSignedBlocks.isEmpty()) {
                OPDE.debug("SYSRunningClassesTools.startModule/5: OHNE ERFOLG");
                // nein
                // dann wieder weg mit dem Eintrag in den Classes.
                EntityTools.delete(result);
                result = null;

            } else {
                OPDE.debug("SYSRunningClassesTools.startModule/5: MIT ERFOLG");

            }

        }

        return result;
    }

    protected static void notifyClasses(List<SYSRunningClasses> list, int message, String textmessage) {
        Iterator<SYSRunningClasses> it = list.iterator();
        while (it.hasNext()) {
//            SYSRunningClasses thisClass = it.next();
//            SYSMessages sysMessage = new SYSMessages(OPDE.getHost(), thisClass.getLogin().getHost(), message, textmessage);
//            EntityTools.persist(sysMessage);
        }
    }

    /**
     * Erstellt eine Liste aller laufenden Klassen gemäß der Signatur und des Status.
     *
     * @param signature - welche Signatur interessiert mich ? "", wenn unsigniert, null wenn egal.
     * @param status    - welcher Status ? STATUS_DONT_CARE wenn egal.
     * @param classes   - welche Klassen (internalClassID) sollen gesucht werden ?
     * @return
     */
    public static List<SYSRunningClasses> getRunning(Object signature, int status, String[] classes) {
        EntityManager em = OPDE.createEM();
        String strClasses = "";
        for (String c : classes) {
            strClasses += "'" + c + "'" + ",";
        }
        strClasses = strClasses.substring(0, strClasses.length() - 1);

        String strquery = ""
                + " SELECT s FROM SYSRunningClasses s "
                + " WHERE s.classname IN (" + strClasses + ") "
                + " AND s.login.host <> :host "
                + (signature != null ? " AND s.signature = :signature " : "")
                + (status != STATUS_DONT_CARE ? " AND s.status = :status " : "");
        Query query = em.createQuery(strquery);
        query.setParameter("host", OPDE.getHost());

        if (signature != null) {
            query.setParameter("signature", signature.toString());
        }

        if (status != STATUS_DONT_CARE) {
            query.setParameter("status", status);
        }

        List<SYSRunningClasses> list = query.getResultList();

        em.close();

        return list;

    }

    public static void endModule(SYSRunningClasses runningClass) {
        EntityTools.delete(runningClass);
    }

    public static void endAllModules(SYSLogin login) throws Exception {
        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            Query query = em.createQuery("DELETE FROM SYSRunningClasses r WHERE r.login = :login");
            query.setParameter("login", login);
            query.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }


}
