package entity.mx;

import entity.system.Users;
import op.OPDE;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;

/**
 * Created by tloehr on 02.08.16.
 */
public class MXmsgTools {

    public static ArrayList<MXmsg> getAllFor(Users recipient) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT rcp.msg FROM MXrecipient rcp WHERE rcp.recipient = :recipient ORDER BY rcp.msg.pit DESC");
        query.setParameter("recipient", recipient);
        ArrayList<MXmsg> result = null;
        try {
            result = new ArrayList<MXmsg>(query.getResultList());
        } catch (Exception e) {
            OPDE.fatal(e);
        }
        if (result == null) {
            result = new ArrayList<MXmsg>();
        }
        return result;
    }

    public static boolean hasUnread(Users recipient) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT rcp.msg FROM MXrecipient rcp WHERE rcp.recipient = :recipient and rcp.unread = TRUE and rcp.msg.draft = FALSE ");
        query.setMaxResults(1);
        query.setParameter("recipient", recipient);
        ArrayList<MXmsg> result = null;
        try {
            result = new ArrayList<>(query.getResultList());
        } catch (Exception e) {
            OPDE.fatal(e);
        }

        return result.size() > 0;
    }

//     public static boolean isUnread(MXmsg mXmsg, Users user){
//         MXrecipient mXrecipient = MXrecipientTools.findMXrecipient(mXmsg, user);
//         return mXrecipient.getReceived().compareTo(SYSConst.DATE_UNTIL_FURTHER_NOTICE) == 0;
//     }

}
