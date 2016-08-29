package entity.mx;

import entity.system.Users;
import entity.system.UsersTools;
import op.OPDE;
import op.system.EMailSystem;
import op.system.Recipient;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.text.DateFormat;
import java.util.ArrayList;

/**
 * Created by tloehr on 02.08.16.
 */
public class MXmsgTools {

    public static ArrayList<MXmsg> getAllFor(Users recipient) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT rcp.msg FROM MXrecipient rcp WHERE rcp.recipient = :recipient AND rcp.msg.draft = FALSE AND rcp.trashed = FALSE ORDER BY rcp.msg.pit DESC");
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

    public static ArrayList<MXmsg> getAllUnreadFor(Users recipient) {
            EntityManager em = OPDE.createEM();
            Query query = em.createQuery("SELECT rcp.msg FROM MXrecipient rcp WHERE rcp.recipient = :recipient AND rcp.msg.draft = FALSE AND rcp.trashed = FALSE and rcp.unread = TRUE ORDER BY rcp.msg.pit DESC");
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

    public static ArrayList<MXmsg> getSentFor(Users sender) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT msg FROM MXmsg msg WHERE msg.sender = :sender AND msg.draft = FALSE ORDER BY msg.pit DESC");
        query.setParameter("sender", sender);
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

    public static ArrayList<MXmsg> getDrafts(Users sender) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT msg FROM MXmsg msg WHERE msg.sender = :sender AND msg.draft = TRUE ORDER BY msg.pit DESC");
        query.setParameter("sender", sender);
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

    public static ArrayList<MXmsg> getTrashed(Users recipient) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT rcp.msg FROM MXrecipient rcp WHERE rcp.recipient = :recipient AND rcp.trashed = TRUE ORDER BY rcp.msg.pit DESC");
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

    public static boolean isUnread(MXmsg msg) {
        boolean unread = true;
        for (MXrecipient mXrecipient : msg.getRecipients()) {
            if (!mXrecipient.isUnread()) {
                unread = false;
                break;
            }
        }
        return unread;
    }

    public static void sendNotificationsFor(MXmsg msg){
        if (!EMailSystem.isMailsystemActive()) return;

        for (MXrecipient mXrecipient : msg.getRecipients()){
            if (mXrecipient.getRecipient().getMailConfirmed() == UsersTools.MAIL_NOTIFICATIONS_ENABLED){
                SwingWorker w = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        EMailSystem.sendMail(SYSTools.xx("mx.mail.notification.subject"), SYSTools.xx("mx.mail.notification.body", mXrecipient.getRecipient().getVorname()) + getMsgAsText(msg), mXrecipient.getRecipient());
                        return null;
                    }
                };
                w.execute();
            }
        }
    }

    /**
     * compact string representation of the msg
     * @param msg
     * @return
     */
    public static String getMsgAsText(MXmsg msg){
        String text = SYSTools.xx("mx.sender") + ": "+ UsersTools.getFullnameWithID(msg.getSender()) + ", " + DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(msg.getPit()) + "<br/>";
        text += "<br/>";
        text += SYSTools.xx("mx.col_subject")+ ": " + SYSTools.catchNull(msg.getSubject(), "mx.no.subject");
        text += "<br/><br/>";
        text +=  msg.getText();
        return text;
    }

//     public static boolean isUnread(MXmsg mXmsg, Users user){
//         MXrecipient mXrecipient = MXrecipientTools.findMXrecipient(mXmsg, user);
//         return mXrecipient.getReceived().compareTo(SYSConst.DATE_UNTIL_FURTHER_NOTICE) == 0;
//     }

}
