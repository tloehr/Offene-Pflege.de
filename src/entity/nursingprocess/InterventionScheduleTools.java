package entity.nursingprocess;

import entity.info.Resident;
import op.OPDE;
import op.care.nursingprocess.PnlNursingProcess;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.DateFormat;
import java.util.ArrayList;

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
            // vorherige Zustand nicht MAXDOSE war, das braucht nämlich keinen Footer.
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
            String wdh = getRepeatPattern(termin);

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

            String wdh = getRepeatPattern(termin);
            result += "    <tr>" +
                    "      <td align=\"center\">" + DateFormat.getTimeInstance(DateFormat.SHORT).format(termin.getUhrzeit()) + " Uhr</td>" +
                    "      <td align=\"center\">" + termin.getUhrzeitAnzahl() + "</td>" +
                    "      <td>" + wdh + "</td>" +
                    "    </tr>";
        } else {
            result = "!!ERROR!!";
        }

        result += "</table>";

        result += SYSTools.catchNull(termin.getBemerkung(), "<div id=\"fonttext\"><b>" + OPDE.lang.getString("misc.msg.comment") + ": </b>", "</div><br/>&nbsp;");

        if (termin.isFloating()) {
            result += "<div id=\"fonttext\"><font color=\"blue\">" + OPDE.lang.getString(PnlNursingProcess.internalClassID + ".floatinginterventions") + "</font></div>";
        }

        return result;
    }

    public static String getRepeatPattern(InterventionSchedule schedule) {
        String result = "";

        if (schedule.isTaeglich()) {
            if (schedule.getTaeglich() > 1) {
                result += "alle " + schedule.getTaeglich() + " Tage";
            } else {
                result += "jeden Tag";
            }
        } else if (schedule.isWoechentlich()) {
            if (schedule.getWoechentlich() == 1) {
                result += "jede Woche ";
            } else {
                result += "alle " + schedule.getWoechentlich() + " Wochen ";
            }

            String daylist = "";

            daylist += (schedule.getMon() > 0 ? OPDE.lang.getString("misc.msg.monday").substring(0, 3) + ", " : "");
            daylist += (schedule.getDie() > 0 ? OPDE.lang.getString("misc.msg.tuesday").substring(0, 3) + ", " : "");
            daylist += (schedule.getMit() > 0 ? OPDE.lang.getString("misc.msg.wednesday").substring(0, 3) + ", " : "");
            daylist += (schedule.getDon() > 0 ? OPDE.lang.getString("misc.msg.thursday").substring(0, 3) + ", " : "");
            daylist += (schedule.getFre() > 0 ? OPDE.lang.getString("misc.msg.friday").substring(0, 3) + ", " : "");
            daylist += (schedule.getSam() > 0 ? OPDE.lang.getString("misc.msg.saturday").substring(0, 3) + ", " : "");
            daylist += (schedule.getSon() > 0 ? OPDE.lang.getString("misc.msg.sunday").substring(0, 3) + ", " : "");

            if (!daylist.isEmpty()) {
                result += "{" + daylist.substring(0, daylist.length() - 2) + "}";
            }

        } else if (schedule.isMonatlich()) {
            if (schedule.getMonatlich() == 1) {
                result += OPDE.lang.getString("misc.msg.everyMonth") + " ";
            } else {
                result += OPDE.lang.getString("misc.msg.every") + " " + schedule.getMonatlich() + " " + OPDE.lang.getString("misc.msg.months") + " ";
//                result += "alle " + schedule.getMonatlich() + " Monate ";
            }

            if (schedule.getTagNum() > 0) {
                result += OPDE.lang.getString("misc.msg.atchrono") + " " + schedule.getTagNum() + ". " + OPDE.lang.getString("misc.msg.ofTheMonth");
//                result += "jeweils am " + schedule.getTagNum() + ". des Monats";
            } else {
                int wtag = 0;
                String tag = "";
                tag += (schedule.getMon() > 0 ? OPDE.lang.getString("misc.msg.monday") : "");
                tag += (schedule.getDie() > 0 ? OPDE.lang.getString("misc.msg.tuesday") : "");
                tag += (schedule.getMit() > 0 ? OPDE.lang.getString("misc.msg.wednesday") : "");
                tag += (schedule.getDon() > 0 ? OPDE.lang.getString("misc.msg.thursday") : "");
                tag += (schedule.getFre() > 0 ? OPDE.lang.getString("misc.msg.friday") : "");
                tag += (schedule.getSam() > 0 ? OPDE.lang.getString("misc.msg.saturday") : "");
                tag += (schedule.getSon() > 0 ? OPDE.lang.getString("misc.msg.sunday") : "");

                // In this case, only one of the below can be >0. So this will work.
                wtag += schedule.getMon();
                wtag += schedule.getDie();
                wtag += schedule.getMit();
                wtag += schedule.getDon();
                wtag += schedule.getFre();
                wtag += schedule.getSam();
                wtag += schedule.getSon();

                result += OPDE.lang.getString("misc.msg.atchrono") + " " + wtag + ". " + tag + " " + OPDE.lang.getString("misc.msg.ofTheMonth");
            }
        } else {
            result = "";
        }

        DateMidnight ldatum = new DateTime(schedule.getLDatum()).toDateMidnight();
        DateMidnight today = new DateMidnight();

        if (ldatum.compareTo(today) > 0) { // Die erste Ausführung liegt in der Zukunft
            result += OPDE.lang.getString("nursingrecords.prescription.firstApplication") + ": " + DateFormat.getDateInstance().format(schedule.getLDatum());
        }

        return result;
    }

    public static ArrayList<InterventionSchedule> getAllActiveByFlag(Resident resident, int flag) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery(" " +
                " SELECT i FROM InterventionSchedule i " +
                " WHERE i.nursingProcess.resident = :resident AND i.nursingProcess.to = :ufn AND i.intervention.flag = :flag ORDER BY i.intervention.bezeichnung ");
        query.setParameter("ufn", SYSConst.DATE_UNTIL_FURTHER_NOTICE);
        query.setParameter("resident", resident);
        query.setParameter("flag", flag);
        ArrayList<InterventionSchedule> listIS = new ArrayList<InterventionSchedule>(query.getResultList());
        em.close();
        return listIS;
    }

    public static String getTerminAsCompactText(InterventionSchedule schedule) {
        String result = "";

        final int ZEIT = 0;
        final int UHRZEIT = 1;
//            int previousState = -1;

        int currentState;
        // Zeit verwendet ?
        if (schedule.verwendetUhrzeit()) {
            currentState = UHRZEIT;
        } else {
            currentState = ZEIT;
        }

        if (currentState == ZEIT) {
            result += (schedule.getNachtMo() > 0 ? OPDE.lang.getString("misc.msg.earlyinthemorning.long") : "") +
                    (schedule.getMorgens() > 0 ? OPDE.lang.getString("misc.msg.morning.long") : "") +
                    (schedule.getMittags() > 0 ? OPDE.lang.getString("misc.msg.noon.long") : "") +
                    (schedule.getNachmittags() > 0 ? OPDE.lang.getString("misc.msg.afternoon.long") : "") +
                    (schedule.getAbends() > 0 ? OPDE.lang.getString("misc.msg.evening.long") : "") +
                    (schedule.getNachtAb() > 0 ? OPDE.lang.getString("misc.msg.lateatnight.long") : "");

            result += (schedule.getTaeglich() != 1 ? getRepeatPattern(schedule) : "");
        } else if (currentState == UHRZEIT) {

            result += DateFormat.getTimeInstance(DateFormat.SHORT).format(schedule.getUhrzeit()) + " " + OPDE.lang.getString("misc.msg.Time.short");

        } else {
            result = "!!ERROR!!";
        }

        return result;
    }

}
