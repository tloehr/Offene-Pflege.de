package entity.qms;

import entity.nursingprocess.NPControl;
import entity.nursingprocess.NPControlTools;
import op.OPDE;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by tloehr on 17.06.14.
 */
public class QmsplanTools {

    public static final byte STATE_ACTIVE = 0;
    public static final byte STATE_INACTIVE = 1;
    public static final byte STATE_ARCHIVE = 2;

    /**
     * returns a list with all active Qmsplan
     *
     * @return
     */
    public static ArrayList<Qmsplan> getAllActive() {
        EntityManager em = OPDE.createEM();
        ArrayList<Qmsplan> list = null;

        try {

            String jpql = " SELECT q " +
                    " FROM Qmsplan q" +
                    " WHERE q.state = :state " +
                    " ORDER BY q.title DESC ";

            Query query = em.createQuery(jpql);
            query.setParameter("state", QmsplanTools.STATE_ACTIVE);
            list = new ArrayList<Qmsplan>(query.getResultList());

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list;
    }


    /**
     * returns a HTML representation of the given Qmsplan.
     *
     * @param qmsplan
     * @return
     */
    public static String getAsHTML(Qmsplan qmsplan) {
        String html = "";


        DateFormat df = DateFormat.getDateInstance();


//        html += SYSConst.html_bold("misc.msg.createdby") + ": " + qmsplan.getUserON().getFullname() + " ";
//        html += SYSConst.html_bold("misc.msg.atchrono") + ": " + df.format(qmsplan.getFrom());
//        if (qmsplan.isClosed()) {
//            html += "<br/>";
//            html += SYSConst.html_bold("misc.msg.closedBy") + ": " + qmsplan.getUserOFF().getFullname() + " ";
//            html += SYSConst.html_bold("misc.msg.atchrono") + ": " + df.format(qmsplan.getTo());
//        }

        html += SYSConst.html_h3("misc.msg.description") +
                SYSTools.replace(qmsplan.getDescription(), "\n", "<br/>", false);

        html += SYSConst.html_h3("misc.msg.measures");

        if (qmsplan.getQmsschedules().isEmpty()) {
            html += "<ul><li><b>" + SYSTools.xx("misc.msg.MissingInterventions") + " !!!</b></li></ul>";
        }
//         else {
//            html += "<ul>";
//            for (Qmssched qmssched : qmsplan.getQmsschedules()) {
//                html += "<li>";
//                html += SYSConst.html_div(SYSConst.html_bold(qmssched.getMeasure()));
//                html += QmsschedTools.getAsHTML(qmssched);
//                html += "</li>";
//            }
//            html += "</ul>";
//        }

        //        if (np.getFlag() > 0) {
        //            html += "<br/><b>" + SYSTools.xx("nursingrecords.nursingprocess.dlgplanung.lblFlag") + ":</b> " + FLAGS[np.getFlag()];
        //        }



        html += "</div>";
        return html;
    }

}
