package entity.qms;

import op.OPDE;
import op.tools.SYSTools;
import org.joda.time.LocalDate;

import java.text.DateFormat;

/**
 * Created by tloehr on 17.06.14.
 */
public class QmsschedTools {

    public static String getScheduleAsHTML(Qmssched qmssched) {
        String result = "";

        result += SYSTools.catchNull(qmssched.getText(), "<div id=\"fonttext\"><b>" + OPDE.lang.getString("misc.msg.comment") + ": </b>", "</div><br/>&nbsp;");

        result += "<table id=\"fonttext\" border=\"1\" >" +
                "   <tr>" +
                "      <th align=\"center\">" + OPDE.lang.getString("misc.msg.Time.long") + "</th>" +
                "      <th align=\"center\">" + OPDE.lang.getString("misc.msg.repeat.short") + "</th>" +
                "   </tr>";


        String wdh = getRepeatPattern(qmssched);
        result += "    <tr>" +
                "      <td align=\"center\">" + DateFormat.getTimeInstance(DateFormat.SHORT).format(qmssched.getTime()) + " " + OPDE.lang.getString("misc.msg.Time.short") + "</td>" +
                "      <td>" + wdh + "</td>" +
                "    </tr>";


        result += "</table>";



        return result;
    }

    public static String getRepeatPattern(Qmssched qmssched) {
        String result = "";

        if (qmssched.isDaily()) {
            if (qmssched.getDaily() > 1) {
                result += OPDE.lang.getString("misc.msg.every") + " " + qmssched.getDaily() + " " + OPDE.lang.getString("misc.msg.Days2");
            } else {
                result += OPDE.lang.getString("misc.msg.everyDay");
            }
        } else if (qmssched.isWeekly()) {
            if (qmssched.getWeekly() == 1) {
                result += result += OPDE.lang.getString("misc.msg.everyWeek");
            } else {
                result += OPDE.lang.getString("misc.msg.every") + " " + qmssched.getWeekly() + " " + OPDE.lang.getString("misc.msg.weeks");
            }

            String daylist = "";

            daylist += (qmssched.getMon() > 0 ? OPDE.lang.getString("misc.msg.monday").substring(0, 3) + ", " : "");
            daylist += (qmssched.getTue() > 0 ? OPDE.lang.getString("misc.msg.tuesday").substring(0, 3) + ", " : "");
            daylist += (qmssched.getWed() > 0 ? OPDE.lang.getString("misc.msg.wednesday").substring(0, 3) + ", " : "");
            daylist += (qmssched.getThu() > 0 ? OPDE.lang.getString("misc.msg.thursday").substring(0, 3) + ", " : "");
            daylist += (qmssched.getFri() > 0 ? OPDE.lang.getString("misc.msg.friday").substring(0, 3) + ", " : "");
            daylist += (qmssched.getSat() > 0 ? OPDE.lang.getString("misc.msg.saturday").substring(0, 3) + ", " : "");
            daylist += (qmssched.getSun() > 0 ? OPDE.lang.getString("misc.msg.sunday").substring(0, 3) + ", " : "");

            if (!daylist.isEmpty()) {
                result += "{" + daylist.substring(0, daylist.length() - 2) + "}";
            }

        } else if (qmssched.isMonthly()) {
            if (qmssched.getMonthly() == 1) {
                result += OPDE.lang.getString("misc.msg.everyMonth") + " ";
            } else {
                result += OPDE.lang.getString("misc.msg.every") + " " + qmssched.getMonthly() + " " + OPDE.lang.getString("misc.msg.months") + " ";
            }

            if (qmssched.getDaynum() > 0) {
                result += OPDE.lang.getString("misc.msg.atchrono") + " " + qmssched.getDaynum() + ". " + OPDE.lang.getString("misc.msg.ofTheMonth");
                //                result += "jeweils am " + schedule.getTagNum() + ". des Monats";
            } else {
                int wtag = 0;
                String tag = "";
                tag += (qmssched.getMon() > 0 ? OPDE.lang.getString("misc.msg.monday") : "");
                tag += (qmssched.getTue() > 0 ? OPDE.lang.getString("misc.msg.tuesday") : "");
                tag += (qmssched.getWed() > 0 ? OPDE.lang.getString("misc.msg.wednesday") : "");
                tag += (qmssched.getThu() > 0 ? OPDE.lang.getString("misc.msg.thursday") : "");
                tag += (qmssched.getFri() > 0 ? OPDE.lang.getString("misc.msg.friday") : "");
                tag += (qmssched.getSat() > 0 ? OPDE.lang.getString("misc.msg.saturday") : "");
                tag += (qmssched.getSun() > 0 ? OPDE.lang.getString("misc.msg.sunday") : "");

                // In this case, only one of the below can be >0. So this will work.
                wtag += qmssched.getMon();
                wtag += qmssched.getTue();
                wtag += qmssched.getWed();
                wtag += qmssched.getThu();
                wtag += qmssched.getFri();
                wtag += qmssched.getSat();
                wtag += qmssched.getSun();

                result += OPDE.lang.getString("misc.msg.atchrono") + " " + wtag + ". " + tag + " " + OPDE.lang.getString("misc.msg.ofTheMonth");
            }
        } else {
            result = "";
        }

        LocalDate ldatum = new LocalDate(qmssched.getlDate());
        LocalDate today = new LocalDate();

        if (ldatum.compareTo(today) > 0) { // Die erste Ausführung liegt in der Zukunft
            result += OPDE.lang.getString("opde.controlling.qms.dlgqmsplan.pnlschedule.ldate") + ": " + DateFormat.getDateInstance().format(qmssched.getlDate());
        }

        return result;
    }

}
