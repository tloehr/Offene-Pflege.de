package entity.qms;

import io.lamma.LammaConversion;
import io.lamma.Recurrence;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.LocalDate;
import org.joda.time.MutableDateTime;

import java.text.DateFormat;

/**
 * Created by tloehr on 17.06.14.
 */
public class QmsschedTools {


    public static final byte STATE_ACTIVE = 0;
    public static final byte STATE_INACTIVE = 1;
    public static final byte STATE_ARCHIVE = 2;

    public static String getAsHTML(Qmssched qmssched) {
        String result = SYSConst.html_paragraph(SYSConst.html_bold(qmssched.getMeasure()));

        result += getRepeatPattern(qmssched);
//        result += SYSConst.html_paragraph(qmssched.hasTime() ? DateFormat.getTimeInstance(DateFormat.SHORT).format(qmssched.getTime()) + " " + SYSTools.xx("misc.msg.Time.short") + ", " + wdh : wdh);

        if (qmssched.getStation() != null) {
            result += SYSConst.html_paragraph(SYSTools.xx("misc.msg.station") + ": " + qmssched.getStation().getName() + ", " + qmssched.getStation().getHome().getName());
        } else if (qmssched.getHome() != null) {
            result += SYSConst.html_paragraph(SYSTools.xx("misc.msg.home") + ": " + qmssched.getHome().getName());
        }
        result += SYSTools.catchNull(qmssched.getText(), "<p><i>", "</i></p>");


        return result;
    }

    public static String getRepeatPattern(Qmssched qmssched) {
        String result = "";

        if (qmssched.isDaily()) {
            if (qmssched.getDaily() > 1) {
                result += SYSTools.xx("misc.msg.every") + " " + qmssched.getDaily() + " " + SYSTools.xx("misc.msg.Days2");
            } else {
                result += SYSTools.xx("misc.msg.everyDay");
            }
        } else if (qmssched.isWeekly()) {
            if (qmssched.getWeekly() == 1) {
                result += result += SYSTools.xx("misc.msg.everyWeek");
            } else {
                result += SYSTools.xx("misc.msg.every") + " " + qmssched.getWeekly() + " " + SYSTools.xx("misc.msg.weeks");
            }

            MutableDateTime mdt = new MutableDateTime();
            mdt.setDayOfWeek(qmssched.getWeekday());

            result += ", " + SYSTools.xx("misc.msg.each") + " " + SYSTools.xx("misc.msg.atchrono") + " " + mdt.dayOfWeek().getAsText();

        } else if (qmssched.isMonthly()) {


            if (qmssched.getMonthly() == 1) {
                result += SYSTools.xx("misc.msg.everyMonth") + ", ";
            } else {
                result += SYSTools.xx("misc.msg.every") + " " + qmssched.getMonthly() + " " + SYSTools.xx("misc.msg.months") + ", ";
            }

            if (qmssched.getWeekday() > 0) { // with a nth weekday in that month
                MutableDateTime mdt = new MutableDateTime();
                mdt.setDayOfWeek(qmssched.getWeekday());

                result += SYSTools.xx("misc.msg.each") + " " + SYSTools.xx("misc.msg.atchrono") + " " + qmssched.getDayinmonth() + ". " + mdt.dayOfWeek().getAsText();
            } else {
                result += SYSTools.xx("misc.msg.each") + " " + SYSTools.xx("misc.msg.atchrono") + " " + qmssched.getDayinmonth() + ". " + SYSTools.xx("misc.msg.day");
            }


        } else if (qmssched.isYearly()) {

            if (qmssched.getYearly() == 1) {
                result += SYSTools.xx("misc.msg.everyYear") + ", ";
            } else {
                result += SYSTools.xx("misc.msg.every") + " " + qmssched.getYearly() + " " + SYSTools.xx("misc.msg.Years") + ", ";
            }

            if (qmssched.getMonthinyear() > 0) {
                MutableDateTime mdt = new MutableDateTime();
                mdt.setDayOfWeek(qmssched.getWeekday());

                result += SYSTools.xx("misc.msg.every") + " " + qmssched.getDayinmonth() + ". " + mdt.dayOfWeek().getAsText() + " " + SYSTools.xx("in");
            } else {
                result += SYSTools.xx("misc.msg.every") + " " + qmssched.getDayinmonth() + ". " + SYSTools.xx("misc.msg.day");
            }


        } else {
            result = "";
        }

        LocalDate ldatum = new LocalDate(qmssched.getStartingOn());
        LocalDate today = new LocalDate();

        if (ldatum.compareTo(today) > 0) { // Die erste Ausführung liegt in der Zukunft
            result += "<br/>" + SYSTools.xx("opde.controlling.qms.dlgqmsplan.pnlschedule.startingon") + ": " + DateFormat.getDateInstance().format(qmssched.getStartingOn());
        }

        return result;
    }


    /**
     * takes the recurrence pattern inside a qmssched and creates a list of recurrences for a lamma sequence generator.
     *
     * @param qmssched
     * @return
     */
    public static Recurrence getRecurrence(Qmssched qmssched) {

        Recurrence recurrence = null;

        if (qmssched.isDaily()) {
            recurrence = LammaConversion.days(qmssched.getDaily());
        } else if (qmssched.isWeekly()) {
            recurrence = LammaConversion.weeks(
                    qmssched.getWeekly(),
                    SYSCalendar.weeksdays[qmssched.getWeekday()]);
        } else if (qmssched.isMonthly()) {
            if (qmssched.getWeekday() > 0) { // with a nth weekday in that month
                recurrence = LammaConversion.months(
                        qmssched.getMonthly(),
                        LammaConversion.nthWeekdayOfMonth(qmssched.getDayinmonth(), SYSCalendar.weeksdays[qmssched.getWeekday()]));
            } else { // with a specific day in that month
                recurrence = LammaConversion.months(
                        qmssched.getMonthly(),
                        LammaConversion.nthDayOfMonth(qmssched.getDayinmonth()));
            }
        } else if (qmssched.isYearly()) {

            if (qmssched.getWeekday() > 0) { // month with a nth weekday in that month
                recurrence = LammaConversion.years(
                        qmssched.getYearly(),
                        LammaConversion.nthMonthOfYear(SYSCalendar.months[qmssched.getMonthinyear()],
                                LammaConversion.nthWeekdayOfMonth(qmssched.getDayinmonth(), SYSCalendar.weeksdays[qmssched.getWeekday()])));
            } else { // month with a specific day in that month
                recurrence = LammaConversion.years(
                        qmssched.getYearly(),
                        LammaConversion.nthMonthOfYear(SYSCalendar.months[qmssched.getMonthinyear()],
                                LammaConversion.nthDayOfMonth(qmssched.getDayinmonth())));
            }

        }
        return recurrence;
    }

}
