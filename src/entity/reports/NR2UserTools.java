/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.reports;

import entity.building.Homes;
import entity.system.Users;
import op.tools.SYSTools;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.DateFormat;
import java.util.Collections;

/**
 * @author tloehr
 */
public class NR2UserTools {

    public static boolean containsUser(EntityManager em, NReport nReport, Users user) {

        Query query = em.createQuery(" " +
                " SELECT count(u) FROM NReport n JOIN n.usersAcknowledged u " +
                " WHERE n = :nreport AND u.user = :user ");

        query.setParameter("nreport", nReport);
        query.setParameter("user", user);

        Long count = (Long) query.getSingleResult();

        return count.longValue() > 0;

    }

    public static boolean containsUser(NReport nReport, Users user) {
        boolean contains = false;
        for (NR2User nr2User : nReport.getUsersAcknowledged()) {
            contains = nr2User.getUser().equals(user);
            if (contains) break;
        }
        return contains;
    }

    public static String getAsHTML(NReport nReport) {
        String result = "";
        result += "<h2 id=\"fonth2\" >" + SYSTools.xx("nursingrecords.handover") + "</h2>";
        if (!nReport.getUsersAcknowledged().isEmpty()) {
            result += "<h3 id=\"fonth3\" >" + SYSTools.xx("nursingrecords.handover.ListOfUsersAcknowledged") + "</h3>";
            result += NReportTools.getAsHTML(nReport, null);

            result += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\"><tr>" +
                    "<th>" + SYSTools.xx("misc.msg.DateAndTime") + "</th><th>" + SYSTools.xx("misc.msg.Users") + "</th></tr>";

            Collections.sort(nReport.getUsersAcknowledged());

            for (NR2User n2u : nReport.getUsersAcknowledged()) {
                result += "<tr>";
                result += "<td valign=\"top\">" + DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(n2u.getPit()) + "</td>";
                result += "<td valign=\"top\">" + n2u.getUser().getFullname() + "</td>";
                result += "</tr>";
            }

            result += "</table>";
        } else {
            result += "<i>" + SYSTools.xx("misc.msg.currentlynoentry") + "</i>";
        }
        return result;
    }


    /**
     * Tells, if there are open reports for a specific day...
     * https://github.com/tloehr/Offene-Pflege.de/issues/43
     *
     * @param day
     * @param user
     * @param home
     * @return
     */
    public static boolean hasOpenReports(LocalDate day, Users user, Homes home) {
        boolean openReports = false;
        for (NReport nReport : NReportTools.getNReports4Handover(day, home)) {
            openReports = !containsUser(nReport, user);
            if (openReports) break;
        }
        return openReports;
    }
}
