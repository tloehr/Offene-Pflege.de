package entity.staff;

import entity.system.Users;
import op.tools.SYSConst;

import javax.swing.*;
import java.text.DateFormat;
import java.util.ArrayList;
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


    public static Training2Users get4User(Collection<Training2Users> collection, Users user) {
        Training2Users result = null;

        for (Training2Users training2Users : collection) {
            if (training2Users.getAttendee().equals(user)) {
                result = training2Users;
                break;
            }
        }

        return result;
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

    public static String getHTMLIcon(Training2Users training2Users) {
        if (training2Users.getState() == STATE_DONE) {
            return SYSConst.html_16x16_apply;
        }
        if (training2Users.getState() == STATE_OPEN) {
            return SYSConst.html_16x16_empty;
        }
        if (training2Users.getState() == STATE_REFUSED) {
            return SYSConst.html_16x16_cancel;
        }
        return null;
    }

    public static String getTooltip(Training2Users training2Users) {
        if (training2Users.getState() == STATE_DONE) {
            return "opde.t2u.state.done";
        }
        if (training2Users.getState() == STATE_OPEN) {
            return "opde.t2u.state.open";
        }
        if (training2Users.getState() == STATE_REFUSED) {
            return "opde.t2u.state.refused";
        }
        return null;
    }


    public static String getAsHTML(ArrayList<Training2Users> listT2U) {
        String html = "";

        if (listT2U.isEmpty()) {
            return "";
        }

        for (Training2Users training2Users : listT2U) {
            html += SYSConst.html_li(
                    training2Users.getAttendee().getFullname() + "; " + DateFormat.getDateInstance(DateFormat.SHORT).format(training2Users.getPit())
            );
        }

        return SYSConst.html_ul(html);
    }

}
