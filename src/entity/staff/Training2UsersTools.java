package entity.staff;

import entity.system.Users;
import op.tools.SYSConst;

import javax.swing.*;
import java.util.Collection;

/**
 * Created by tloehr on 19.07.14.
 */
public class Training2UsersTools {

    public static final byte STATE_OPEN = 0;
    public static final byte STATE_DONE = 1;
    public static final byte STATE_REFUSED = 2;

    public static boolean contains(Collection<Training2Users> collection, Users user) {
        boolean yes = false;

        for (Training2Users training2Users : collection) {
            if (training2Users.getAttendee().equals(user)) {
                yes = true;
                break;
            }
        }

        return yes;
    }


    public static Icon getIcon(Training2Users training2Users) {
           if (training2Users.getState() == STATE_DONE) {
               return SYSConst.icon16apply;
           }
           if (training2Users.getState() == STATE_OPEN) {
               return SYSConst.icon16empty;
           }
           if (training2Users.getState() == STATE_REFUSED) {
               return SYSConst.icon16cancel;
           }
           return null;
       }

}
