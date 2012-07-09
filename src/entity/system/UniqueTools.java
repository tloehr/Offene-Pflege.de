package entity.system;

import op.tools.SYSTools;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 20.12.11
 * Time: 14:29
 * To change this template use File | Settings | File Templates.
 */
public class UniqueTools {

    /**
     * Gibt eine eindeutige Nummer an den Aufrufer zurück. Die Nummer wird anhand der Datenbanktabelle UNIQUEID bestimmt.
     * Da führt das System über die vergebenen IDs buch. Können für alles mögliche benutzt werden wo eben globale, eindeutige Schlüssel
     * benöigt werden. Man kann auch einen prefix angeben. Dann führt die Methode in der Tabelle auch mehrere, getrennte Zähler.
     * <p/>
     * Der Standardzähler ist leer, also "".
     *
     * @return long   UID
     */
    public static Unique getNewUID(EntityManager em, String prefix) throws Exception {

        long newID = 0L;
        Query query = em.createQuery("SELECT u FROM Unique u WHERE u.prefix = :prefix");
        query.setParameter("prefix", SYSTools.catchNull(prefix).trim());

        Unique unique;
        try {
            unique = (Unique) query.getSingleResult();
        } catch (NoResultException nre){
            unique = null;
        }

        if (unique == null) { // für diesen prefix gibt es noch keinen Zähler. Es wird einer angelegt.
            unique = em.merge(new Unique(prefix));
        } else {
            boolean done = false;
            while (!done) {
                try {
                    em.lock(unique, LockModeType.OPTIMISTIC);
                    unique.incUID();
                    done = true;
                } catch (OptimisticLockException ole){
                    done = false;
                    em.refresh(unique);
                }
            }
        }
        return unique;
    } // getUID()
}
