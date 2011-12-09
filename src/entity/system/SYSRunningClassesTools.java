/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.system;

import entity.EntityTools;
import op.OPDE;
import op.tools.Pair;

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

    public static Pair<SYSRunningClasses, SYSRunningClasses> startModule(String internalClassID, Object signature, String[] conflictGroup) {
        SYSRunningClasses myClass = null, blocking = null;
        // 1. Ist eine unsignierte Klasse in meiner Gruppe aktiv ? Dann bin ich immer RO
        boolean block = !getRunning("", STATUS_DONT_CARE, conflictGroup).isEmpty();
        OPDE.debug("SYSRunningTools.startModule/3: " + internalClassID + " will starten.");
        OPDE.debug("SYSRunningTools.startModule/3: Signatur: " + signature);

        // Im Zweifel immer RO
        short status = STATUS_RO;

        if (!block) {
            OPDE.debug("SYSRunningTools.startModule/3: nicht durch unsignierte geblockt.");
            // 2. sind signierte (mit gleicher Signatur) aus meiner Gruppe mit Status RW aktiv ?
            // dann bin ich RO
            List<SYSRunningClasses> list = getRunning(signature, STATUS_RW, conflictGroup);
            if (!list.isEmpty()) {
                blocking = list.get(0);
                OPDE.debug("SYSRunningTools.startModule/3: STATUS_RO");
            } else {
                // 3. Ansonsten darf ich RW sein.
                status = STATUS_RW;
                OPDE.debug("SYSRunningTools.startModule/3: STATUS_RW");
            }
        } else {
            OPDE.debug("SYSRunningTools.startModule/3: durch unsignierte geblockt.");
            OPDE.debug("SYSRunningTools.startModule/3: STATUS_RO");
        }

        myClass = new SYSRunningClasses(internalClassID, signature.toString(), status);
        EntityTools.persist(myClass);
        OPDE.debug("SYSRunningTools.startModule/3: RunningClass wird angelegt RCID: " + myClass.getRcid());

        return new Pair<SYSRunningClasses, SYSRunningClasses>(myClass, blocking);
    }


    public static SYSRunningClasses startModule(String internalClassID, String[] conflictGroup, int maxtries, String message) {
        SYSRunningClasses result = null;
        int tries = 0;
        OPDE.debug("SYSRunningTools.startModule/5: " + internalClassID + " will starten.");

        // 1. ist ein unsignierter aus der conflictGroup da ? Dann mache ich direkt Schluss. Und der return ist false.
        boolean block = !getRunning(null, STATUS_DONT_CARE, conflictGroup).isEmpty();
        OPDE.debug("SYSRunningTools.startModule/5: unsigned blockade gefunden: " + block);

        // 2. sind signierte da, dann ist der return true. ich versuche dann solange bis ich erfolg habe und rufe dann success auf.
        // wenn ich keinen erfolg habe (nach tries versuchen), dann rufe ich fail auf.
        List<SYSRunningClasses> listSignedBlocks = new ArrayList<SYSRunningClasses>();
        if (!block) { // bisher keine Blockade gefunden ? Dann weiter.
            // Zuerst trage ich mich als status RW ein. Das wird erstmal alle weiteren davon abhalten mir
            // in die Quere zu kommen.
            result = new SYSRunningClasses(internalClassID, "", STATUS_RW);
            EntityTools.persist(result);
            OPDE.debug("SYSRunningTools.startModule/5: RunningClass wird vorab angelegt RCID: " + result.getRcid());

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
                    OPDE.debug("SYSRunningTools.startModule/5: Bisher keinen Erfolg. Warte 1 Minute. " + tries + "/" + maxtries + " Versuche.");
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    OPDE.fatal(e);
                }
            }
            OPDE.debug("SYSRunningTools.startModule/5: Abschluss");

            // hat's geklappt ?
            if (!listSignedBlocks.isEmpty()) {
                OPDE.debug("SYSRunningTools.startModule/5: OHNE ERFOLG");
                // nein
                // dann wieder weg mit dem Eintrag in den Classes.
                EntityTools.delete(result);
                result = null;

            } else {
                OPDE.debug("SYSRunningTools.startModule/5: MIT ERFOLG");

            }

        }

        return result;
    }

    protected static void notifyClasses(List<SYSRunningClasses> list, int message, String textmessage) {
        Iterator<SYSRunningClasses> it = list.iterator();
        while (it.hasNext()) {
            SYSRunningClasses thisClass = it.next();
            SYSMessages sysMessage = new SYSMessages(OPDE.getHost(), thisClass.getLogin().getHost(), message, textmessage);
            EntityTools.persist(sysMessage);
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
        Query query = OPDE.getEM().createQuery(strquery);
        query.setParameter("host", OPDE.getHost());
        if (signature != null) {
            query.setParameter("signature", signature.toString());
        }

        if (status != STATUS_DONT_CARE) {
            query.setParameter("status", status);
        }

        return query.getResultList();

    }

    public static void endModule(SYSRunningClasses runningClass) {
        EntityTools.delete(runningClass);
    }

    public static void endAllModules(SYSLogin login) throws Exception {
        Query query = OPDE.getEM().createQuery("DELETE FROM SYSRunningClasses r WHERE r.login = :login");
        query.setParameter("login", login);
        query.executeUpdate();
    }


}
