package entity.mx;

import entity.system.Users;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by tloehr on 03.08.16.
 */
public class MXrecipientTools {

    public static Set<Users> getAllUsersFor(Set<MXrecipient> recipients) {
        HashSet<Users> users = new HashSet<>();
        for (MXrecipient mxr : recipients) {
            users.add(mxr.getRecipient());
        }

        return users;
    }


    /**
     * searches for a specific user in the recipient list of a message.
     * @param msg
     * @param user
     * @return the wanted MXrecipient or null if the user wasnt found
     */
    public static MXrecipient findMXrecipient(MXmsg msg, Users user) {
        MXrecipient mXrecipient = null;

        for (MXrecipient mxr : msg.getRecipients()) {
            if (mxr.getRecipient().equals(user)) {
                mXrecipient = mxr;
                break;
            }
        }

        return mXrecipient;
    }

    public static boolean isUnread(MXmsg msg, Users user) {
        return findMXrecipient(msg, user).isUnread();
    }

}
