package entity.planung;

import entity.Bewohner;
import entity.EntityTools;
import entity.info.BWInfoKat;
import op.OPDE;
import op.care.planung.PnlPlanung;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 19.07.12
 * Time: 15:50
 * To change this template use File | Settings | File Templates.
 */
public class NursingProcessTools {
    public static final String UNIQUEID = "__plankenn";

    public static List<NursingProcess> findByKategorieAndBewohner(Bewohner bewohner, BWInfoKat kat) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT p FROM NursingProcess p WHERE p.bewohner = :bewohner AND p.kategorie = :kat ORDER BY p.stichwort, p.von");
        query.setParameter("kat", kat);
        query.setParameter("bewohner", bewohner);
        List<NursingProcess> planungen = query.getResultList();
        em.close();
        return planungen;
    }

    public static List<NursingProcess> getTemplates(String topic, boolean includeInactives) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT p FROM NursingProcess p WHERE p.stichwort like :topic " + (includeInactives ? "" : " AND p.bis > :now ") + " ORDER BY p.stichwort, p.bewohner.bWKennung, p.von");
        query.setParameter("topic", EntityTools.getMySQLsearchPattern(topic));
        if (!includeInactives) {
            query.setParameter("now", new Date());
        }
        List<NursingProcess> planungen = query.getResultList();
        em.close();
        return planungen;
    }


    /**
     * Gibt einen String zurück, der eine HTML Darstellung einer Pflegeplanung enthält.
     *
     * @param planung
     * @return
     */
    public static String getAsHTML(NursingProcess planung, boolean withHeader) {

        String html = "";
        html += "<h2 id=\"fonth2\" >";
        html += (withHeader ? OPDE.lang.getString(PnlPlanung.internalClassID) : "") + "&raquo;" + planung.getStichwort() + "&laquo;";
        html += "</h2>";

        html += "<div id=\"fonttext\">";

        html += withHeader ? "<b>Kategorie:</b> " + planung.getKategorie().getBezeichnung() + "<br/>" : "";

        DateFormat df = DateFormat.getDateInstance();
        html += "<b>Prüfungstermin:</b> " + df.format(planung.getNKontrolle()) + "<br/>";
        html += "<b>Erstellt von:</b> " + planung.getAngesetztDurch().getNameUndVorname() + "  ";
        html += "<b>Am:</b> " + df.format(planung.getVon()) + "<br/>";
        if (planung.isAbgesetzt()) {
            html += "<b>Abgesetzt von:</b> " + planung.getAbgesetztDurch().getNameUndVorname() + "  ";
            html += "<b>Am:</b> " + df.format(planung.getBis()) + "<br/>";
        }

        html += "<h3 id=\"fonth3\">Situation</h3>" + SYSTools.replace(planung.getSituation(), "\n", "<br/>");
        html += "<h3 id=\"fonth3\">Ziel(e):</h3>" + SYSTools.replace(planung.getZiel(), "\n", "<br/>");

        html += "<h3 id=\"fonth3\">" + OPDE.lang.getString(PnlPlanung.internalClassID + ".interventions") + "</h3>";

        if (planung.getInterventionSchedule().isEmpty()) {
            html += "<ul><li><b>Massnahmen fehlen !!!</b></li></ul>";
        } else {
            html += "<ul>";
//            html += "<li><b>" + OPDE.lang.getString(PnlPlanung.internalClassID + ".interventions") + "</b></li><ul>";
            for (InterventionSchedule interventionSchedule : planung.getInterventionSchedule()) {
                html += "<li>";
                html += "<div id=\"fonttext\"><b>" + interventionSchedule.getIntervention().getBezeichnung() + "</b> (" + interventionSchedule.getDauer().toPlainString() + " " + OPDE.lang.getString("misc.msg.Minutes") + ")</div>";
                html += InterventionScheduleTools.getTerminAsHTML(interventionSchedule);
                html += "</li>";
            }
            html += "</ul>";
        }


        if (!planung.getKontrollen().isEmpty()) {
            html += "<h3 id=\"fonth3\">Kontrolltermine</h3>";
            html += "<ul>";
            for (NPControl kontrolle : planung.getKontrollen()) {
                html += "<li><div id=\"fonttext\">" + NPControlTools.getAsHTML(kontrolle) + "</div></li>";
            }
            html += "</ul>";
        }

        html += "</div>";
        return html;
    }

//    public static void close(EntityManager em, NursingProcess np, String closingText) {
//
//
//
//    }
}
