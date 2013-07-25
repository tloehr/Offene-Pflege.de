/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.reports;

import entity.system.Users;
import op.OPDE;
import op.care.supervisor.PnlHandover;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Collections;

/**
 * @author tloehr
 */
public class NR2UserTools {
//    public static boolean containsUser(Collection<NR2User> list, Users user) {
//        boolean found = false;
//        for (NR2User conn : list) {
//            found = conn.getUser().equals(user);
//            if (found) break;
//        }
//        return found;
//    }

    public static boolean containsUser(EntityManager em, NReport nReport, Users user) {

        Query query = em.createQuery(" " +
                " SELECT count(u) FROM NReport n JOIN n.usersAcknowledged u " +
                " WHERE n = :nreport AND u.user = :user ");

        query.setParameter("nreport", nReport);
        query.setParameter("user", user);

        Long count = (Long) query.getSingleResult();

        return count.longValue() > 0;

    }

    public static String getAsHTML(NReport nReport) {
        String result = "";
        result += "<h2 id=\"fonth2\" >" + OPDE.lang.getString(PnlHandover.internalClassID) + "</h2>";
        if (!nReport.getUsersAcknowledged().isEmpty()) {
            result += "<h3 id=\"fonth3\" >" + OPDE.lang.getString(PnlHandover.internalClassID + ".ListOfUsersAcknowledged") + "</h3>";
            result += NReportTools.getAsHTML(nReport, null);

            result += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\"><tr>" +
                    "<th>" + OPDE.lang.getString("misc.msg.DateAndTime") + "</th><th>" + OPDE.lang.getString("misc.msg.Users") + "</th></tr>";

            Collections.sort(nReport.getUsersAcknowledged());

            for (NR2User n2u : nReport.getUsersAcknowledged()) {
                result += "<tr>";
                result += "<td valign=\"top\">" + DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(n2u.getPit()) + "</td>";
                result += "<td valign=\"top\">" + n2u.getUser().getFullname() + "</td>";
                result += "</tr>";
            }

            result += "</table>";
        } else {
            result += "<i>" + OPDE.lang.getString("misc.msg.currentlynoentry") + "</i>";
        }
        return result;
    }
}
