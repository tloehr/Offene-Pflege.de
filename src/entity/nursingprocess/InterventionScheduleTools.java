package entity.nursingprocess;

import op.OPDE;
import op.care.nursingprocess.PnlNursingProcess;
import op.tools.SYSTools;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import java.text.DateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 19.07.12
 * Time: 16:23
 * To change this template use File | Settings | File Templates.
 */
public class InterventionScheduleTools {

    public static String getTerminAsHTML(InterventionSchedule termin) {
        String result = "";


        final int ZEIT = 0;
        final int UHRZEIT = 1;
        int previousState = -1;

        int currentState;
        // Zeit verwendet ?
        if (termin.verwendetUhrzeit()) {
            currentState = UHRZEIT;
        } else {
            currentState = ZEIT;
        }
        boolean headerNeeded = previousState == -1 || currentState != previousState;

        if (previousState > -1 && headerNeeded) {
            // noch den Footer vom letzten Durchgang dabei. Aber nur, wenn nicht
            // der erste Durchlauf, ein Wechsel stattgefunden hat und der
            // vorherige Zustand nicht MAXDOSIS war, das braucht nämlich keinen Footer.
            result += "</table>";
        }
        previousState = currentState;
        if (currentState == ZEIT) {
            if (headerNeeded) {
                result += "<table id=\"fonttext\" border=\"1\">" +
                        "   <tr>" +
                        "      <th align=\"center\">" + OPDE.lang.getString("misc.msg.earlyinthemorning.short") + "</th>" +
                        "      <th align=\"center\">" + OPDE.lang.getString("misc.msg.morning.short") + "</th>" +
                        "      <th align=\"center\">" + OPDE.lang.getString("misc.msg.noon.short") + "</th>" +
                        "      <th align=\"center\">" + OPDE.lang.getString("misc.msg.afternoon.short") + "</th>" +
                        "      <th align=\"center\">" + OPDE.lang.getString("misc.msg.evening.short") + "</th>" +
                        "      <th align=\"center\">" + OPDE.lang.getString("misc.msg.lateatnight.short") + "</th>" +
                        "      <th align=\"center\">" + OPDE.lang.getString("misc.msg.repeat.short") + "</th>" +
                        "   </tr>";
            }
            String wdh = getWiederholung(termin);

            result += "    <tr>" +
                    "      <td align=\"center\">" + (termin.getNachtMo() > 0 ? termin.getNachtMo() : "--") + "</td>" +
                    "      <td align=\"center\">" + (termin.getMorgens() > 0 ? termin.getMorgens() : "--") + "</td>" +
                    "      <td align=\"center\">" + (termin.getMittags() > 0 ? termin.getMittags() : "--") + "</td>" +
                    "      <td align=\"center\">" + (termin.getNachmittags() > 0 ? termin.getNachmittags() : "--") + "</td>" +
                    "      <td align=\"center\">" + (termin.getAbends() > 0 ? termin.getAbends() : "--") + "</td>" +
                    "      <td align=\"center\">" + (termin.getNachtAb() > 0 ? termin.getNachtAb() : "--") + "</td>" +
                    "      <td>" + wdh + "</td>" +
                    "    </tr>";
        } else if (currentState == UHRZEIT) {
            if (headerNeeded) {
                result += "<table id=\"fonttext\" border=\"1\" >" +
                        "   <tr>" +
                        "      <th align=\"center\">" + OPDE.lang.getString("misc.msg.Time.long") + "</th>" +
                        "      <th align=\"center\">" + OPDE.lang.getString("misc.msg.Number") + "</th>" +
                        "      <th align=\"center\">" + OPDE.lang.getString("misc.msg.repeat.short") + "</th>" +
                        "   </tr>";
            }

            String wdh = getWiederholung(termin);
            result += "    <tr>" +
                    "      <td align=\"center\">" + DateFormat.getTimeInstance(DateFormat.SHORT).format(termin.getUhrzeit()) + " Uhr</td>" +
                    "      <td align=\"center\">" + termin.getUhrzeitAnzahl() + "</td>" +
                    "      <td>" + wdh + "</td>" +
                    "    </tr>";
        } else {
            result = "!!FEHLER!!";
        }

        result += "</table>";

        result += SYSTools.catchNull(termin.getBemerkung(), "<div id=\"fonttext\"><b>" + OPDE.lang.getString("misc.msg.comment") + ": </b>", "</div><br/>&nbsp;");

        if (termin.isFloating()) {
            result += "<div id=\"fonttext\"><font color=\"blue\">" + OPDE.lang.getString(PnlNursingProcess.internalClassID + ".floatinginterventions") + "</font></div>";
        }

        return result;
    }

    public static String getWiederholung(InterventionSchedule termin) {
        String result = "";

        if (termin.isTaeglich()) {
            if (termin.getTaeglich() > 1) {
                result += "alle " + termin.getTaeglich() + " Tage";
            } else {
                result += "jeden Tag";
            }
        } else if (termin.isWoechentlich()) {
            if (termin.getWoechentlich() == 1) {
                result += "jede Woche ";
            } else {
                result += "alle " + termin.getWoechentlich() + " Wochen ";
            }

            if (termin.getMon() > 0) {
                result += "Mon ";
            }
            if (termin.getDie() > 0) {
                result += "Die ";
            }
            if (termin.getMit() > 0) {
                result += "Mit ";
            }
            if (termin.getDon() > 0) {
                result += "Don ";
            }
            if (termin.getFre() > 0) {
                result += "Fre ";
            }
            if (termin.getSam() > 0) {
                result += "Sam ";
            }
            if (termin.getSon() > 0) {
                result += "Son ";
            }

        } else if (termin.isMonatlich()) {
            if (termin.getMonatlich() == 1) {
                result += "jeden Monat ";
            } else {
                result += "alle " + termin.getMonatlich() + " Monate ";
            }

            if (termin.getTagNum() > 0) {
                result += "jeweils am " + termin.getTagNum() + ". des Monats";
            } else {

                int wtag = 0;
                String tag = "";
                if (termin.getMon() > 0) {
                    tag += "Montag ";
                    wtag = termin.getMon();
                }
                if (termin.getDie() > 0) {
                    tag += "Dienstag ";
                    wtag = termin.getDie();
                }
                if (termin.getMit() > 0) {
                    tag += "Mittwoch ";
                    wtag = termin.getMit();
                }
                if (termin.getDon() > 0) {
                    tag += "Donnerstag ";
                    wtag = termin.getDon();
                }
                if (termin.getFre() > 0) {
                    tag += "Freitag ";
                    wtag = termin.getFre();
                }
                if (termin.getSam() > 0) {
                    tag += "Samstag ";
                    wtag = termin.getSam();
                }
                if (termin.getSon() > 0) {
                    tag += "Sonntag ";
                    wtag = termin.getSon();
                }
                result += "jeweils am " + wtag + ". " + tag + " des Monats";
            }
        } else {
            result = "";
        }

        DateMidnight ldatum = new DateTime(termin.getLDatum()).toDateMidnight();
        DateMidnight today = new DateMidnight();

        if (ldatum.compareTo(today) > 0) { // Die erste Ausführung liegt in der Zukunft
            result += "<br/>erst ab: " + DateFormat.getDateInstance().format(termin.getLDatum());
        }

        return result;
    }






}
