/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.reports;

import entity.system.Users;
import op.OPDE;
import op.care.supervisor.PnlHandover;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author tloehr
 */
public class Handover2UserTools {
//    public static boolean containsUser(Collection<Handover2User> list, Users user) {
//        boolean found = false;
//        for (Handover2User conn : list) {
//            found = conn.getUser().equals(user);
//            if (found) break;
//        }
//        return found;
//    }

    public static boolean containsUser(EntityManager em, Handovers handover, Users user) {

          Query query = em.createQuery(" " +
                  " SELECT count(u) FROM Handovers h JOIN h.usersAcknowledged u " +
                  " WHERE h = :handover AND u.user = :user ");

          query.setParameter("handover", handover);
          query.setParameter("user", user);

          Long count = (Long) query.getSingleResult();

          return count.longValue() > 0;

      }


    public static String getAsHTML(Handovers handover) {
        String result = "";
        result += "<h2 id=\"fonth2\" >" + SYSTools.xx(PnlHandover.internalClassID) + "</h2>";
        if (!handover.getUsersAcknowledged().isEmpty()) {
            result += "<h3 id=\"fonth3\" >" + SYSTools.xx(PnlHandover.internalClassID + ".ListOfUsersAcknowledged") + "</h3>";
            result += HandoversTools.getAsHTML(handover);

            result += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\"><tr>" +
                    "<th>" + SYSTools.xx("misc.msg.DateAndTime") + "</th><th>" + SYSTools.xx("misc.msg.Users") + "</th></tr>";

            Collections.sort(handover.getUsersAcknowledged());

            for (Handover2User h2u : handover.getUsersAcknowledged()) {
                result += "<tr>";
                result += "<td valign=\"top\">" + DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(h2u.getPit()) + "</td>";
                result += "<td valign=\"top\">" + h2u.getUser().getFullname() + "</td>";
                result += "</tr>";
            }

            result += "</table>";
        } else {
            result += "<i>" + SYSTools.xx("misc.msg.currentlynoentry") + "</i>";
        }
        return result;
    }
}
