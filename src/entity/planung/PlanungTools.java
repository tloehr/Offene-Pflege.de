package entity.planung;

import entity.Bewohner;
import entity.info.BWInfoKat;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 19.07.12
 * Time: 15:50
 * To change this template use File | Settings | File Templates.
 */
public class PlanungTools {


    public static List<Planung> findByKategorieAndBewohner(Bewohner bewohner, BWInfoKat kat) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT p FROM Planung p WHERE p.bewohner = :bewohner AND p.kategorie = :kat ORDER BY p.stichwort, p.von");
        query.setParameter("kat", kat);
        query.setParameter("bewohner", bewohner);
        List<Planung> planungen = query.getResultList();
        em.close();
        return planungen;
    }


    /**
     * Gibt einen String zurück, der eine HTML Darstellung einer Pflegeplanung enthält.
     *
     * @param planung
     * @return
     */
    public static String getAsHTML(Planung planung) {

        String html = "<h2><font color=\"green\">";
        html += "Pflegeplanung &raquo;" + planung.getStichwort() + "&laquo;";
        html += "</font></h2>";
        html += "<b>Kategorie:</b> " + planung.getKategorie().getBezeichnung() + "<br/>";

        DateFormat df = DateFormat.getDateInstance();
        html += "<b>Prüfungstermin:</b> " + df.format(planung.getNKontrolle()) + "<br/>";
        html += "<b>Erstellt von:</b> " + planung.getAngesetztDurch().getNameUndVorname() + "  ";
        html += "<b>Am:</b> " + df.format(planung.getVon()) + "<br/>";
        if (planung.isAbgesetzt()) {
            html += "<b>Abgesetzt von:</b> " + planung.getAbgesetztDurch().getNameUndVorname() + "  ";
            html += "<b>Am:</b> " + df.format(planung.getBis()) + "<br/>";
        }

        html += "<h3>Situation</h3>" + SYSTools.replace(planung.getSituation(), "\n", "<br/>");
        html += "<h3>Ziel(e):</h3>" + SYSTools.replace(planung.getZiel(), "\n", "<br/>");

        html += "<h3>Informationen und Massnahmen</h3>";

        if (planung.getMassnahmen().isEmpty()) {
            html += "<ul><li><i>bisher nichts zugeordnet</i></li></ul>";
        } else {
            html += "<ul>";
            html += "<li><b>Einzelmassnahmen</b></li><ul>";
            for (MassTermin massTermin : planung.getMassnahmen()) {
                html += "<li>" + MassTerminTools.getTerminAsHTML(massTermin) + "</li>";

//                        case ART_KONTROLLEN: {
//                            html += "<li><b>Kontrolltermine</b></li><ul>";
//                            break;
//                        }
//                        default: {
//                        }
//                    } // switch
                //html += "<ul>";
                // Gruppenwechsel

            }
            html += "</ul>";
            if (!planung.getKontrollen().isEmpty()) {
                html += "<li><b>Kontrolltermine</b></li><ul>";
                for (PlanKontrolle kontrolle : planung.getKontrollen()) {
                    html += "<li>" + DateFormat.getDateInstance().format(kontrolle.getDatum()) + "</li>";
                }
                html += "</ul>";
            }
        }
        html += "</ul></ul>";
        return html;
    }

    public static String getWiederholung(int Mon, int Die, int Mit, int Don, int Fre,
                                         int Sam, int Son, int Taeglich, int Woechentlich, int Monatlich, int TagNum, Date LDatum) {
        String result = "";

        if (Taeglich > 0) {
            if (Taeglich > 1) {
                result += "alle " + Taeglich + " Tage";
            } else {
                result += "jeden Tag";
            }
        } else if (Woechentlich > 0) {
            if (Woechentlich == 1) {
                result += "jede Woche ";
            } else {
                result += "alle " + Woechentlich + " Wochen ";
            }

            if (Mon > 0) {
                result += "Mon ";
            }
            if (Die > 0) {
                result += "Die ";
            }
            if (Mit > 0) {
                result += "Mit ";
            }
            if (Don > 0) {
                result += "Don ";
            }
            if (Fre > 0) {
                result += "Fre ";
            }
            if (Sam > 0) {
                result += "Sam ";
            }
            if (Son > 0) {
                result += "Son ";
            }

        } else if (Monatlich > 0) {
            if (Monatlich == 1) {
                result += "jeden Monat ";
            } else {
                result += "alle " + Monatlich + " Monate ";
            }

            if (TagNum > 0) {
                result += "jeweils am " + TagNum + ". des Monats";
            } else {

                int wtag = 0;
                String tag = "";
                if (Mon > 0) {
                    tag += "Montag ";
                    wtag = Mon;
                }
                if (Die > 0) {
                    tag += "Dienstag ";
                    wtag = Die;
                }
                if (Mit > 0) {
                    tag += "Mittwoch ";
                    wtag = Mit;
                }
                if (Don > 0) {
                    tag += "Donnerstag ";
                    wtag = Don;
                }
                if (Fre > 0) {
                    tag += "Freitag ";
                    wtag = Fre;
                }
                if (Sam > 0) {
                    tag += "Samstag ";
                    wtag = Sam;
                }
                if (Son > 0) {
                    tag += "Sonntag ";
                    wtag = Son;
                }
                result += "jeweils am " + wtag + ". " + tag + " des Monats";
            }
        } else {
            result = "";
        }

        if (SYSCalendar.sameDay(LDatum, new Date()) > 0) { // Die erste Ausführung liegt in der Zukunft
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
            result += "<br/>erst ab: " + sdf.format(LDatum);
        }

        return result;
    }


}
