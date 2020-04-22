package de.offene_pflege.backend.services;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import de.offene_pflege.backend.entity.prescription.PrescriptionSchedule;
import de.offene_pflege.op.system.PDF;
import de.offene_pflege.op.tools.HTMLTools;
import de.offene_pflege.op.tools.SYSCalendar;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 18.11.11
 * Time: 15:35
 * To change this template use File | Settings | File Templates.
 */
public class PrescriptionScheduleTools {
    public static final int ROUGHLY = 0;
    public static final int EXACTTIME = 1;
    public static final int MAXDOSE = 2;


    public static int getTerminStatus(PrescriptionSchedule planung) {
        int status = 0;
        if (planung.usesTimesOfTheDay()) {
            status = ROUGHLY;
        } else if (planung.isOnDemand()) {
            status = MAXDOSE;
        } else {
            status = EXACTTIME;
        }
        return status;
    }

    public static String getValueAsString(BigDecimal bd) {
        return (bd.compareTo(BigDecimal.ZERO) > 0 ? SYSTools.formatBigDecimal(bd) : "--");
    }


    /**
     * @param schedule      um die es geht
     * @param writeTaeglich hiermit kann man festlegen, ob bei den Dosierungen, die jeden Tag gegeben werden sollen, das Wort <i>täglich</i> in die Wiederholungsspalte geschrieben wird oder nicht.
     * @return
     */
    public static String getRepeatPattern(PrescriptionSchedule schedule, boolean writeTaeglich) {
        String result = "";

        if (schedule.isDaily()) {
            if (schedule.getTaeglich() > 1) {
                result += "<b>" + SYSTools.xx("misc.msg.every") + " " + schedule.getTaeglich() + " " + SYSTools.xx("misc.msg.Days2") + "</b>";
            } else if (writeTaeglich) {
                result += "<b>" + SYSTools.xx("misc.msg.daily") + "</b>";
            }
        } else if (schedule.isWeekly()) {
            result += "<b>";
            if (schedule.getWoechentlich() == 1) {
                result += SYSTools.xx("misc.msg.everyWeek") + " ";
            } else {
                result += SYSTools.xx("misc.msg.every") + " " + schedule.getWoechentlich() + " " + SYSTools.xx("misc.msg.weeks") + " ";
            }

            String daylist = "";

            daylist += (schedule.getMon() > 0 ? SYSTools.xx("misc.msg.monday").substring(0, 3) + ", " : "");
            daylist += (schedule.getTue() > 0 ? SYSTools.xx("misc.msg.tuesday").substring(0, 3) + ", " : "");
            daylist += (schedule.getWed() > 0 ? SYSTools.xx("misc.msg.wednesday").substring(0, 3) + ", " : "");
            daylist += (schedule.getThu() > 0 ? SYSTools.xx("misc.msg.thursday").substring(0, 3) + ", " : "");
            daylist += (schedule.getFri() > 0 ? SYSTools.xx("misc.msg.friday").substring(0, 3) + ", " : "");
            daylist += (schedule.getSat() > 0 ? SYSTools.xx("misc.msg.saturday").substring(0, 3) + ", " : "");
            daylist += (schedule.getSun() > 0 ? SYSTools.xx("misc.msg.sunday").substring(0, 3) + ", " : "");

            if (!daylist.isEmpty()) {
                result += "{" + daylist.substring(0, daylist.length() - 2) + "}";
            }

            result += "</b>";
        } else if (schedule.isMonthly()) {
            result += "<b>";
            if (schedule.getMonatlich() == 1) {
                result += SYSTools.xx("misc.msg.everyMonth") + " ";
            } else {
                result += SYSTools.xx("misc.msg.every") + " " + schedule.getMonatlich() + " " + SYSTools.xx("misc.msg.months") + " ";
            }

            if (schedule.getTagNum() > 0) {
                result += SYSTools.xx("misc.msg.atchrono") + " " + schedule.getTagNum() + ". " + SYSTools.xx("misc.msg.ofTheMonth");
            } else {
                int wtag = 0;
                String tag = "";

                // In diesem fall kann immer nur ein Wochentag >0 sein. Daher klappt das so.
                tag += (schedule.getMon() > 0 ? SYSTools.xx("misc.msg.monday") : "");
                tag += (schedule.getTue() > 0 ? SYSTools.xx("misc.msg.tuesday") : "");
                tag += (schedule.getWed() > 0 ? SYSTools.xx("misc.msg.wednesday") : "");
                tag += (schedule.getThu() > 0 ? SYSTools.xx("misc.msg.thursday") : "");
                tag += (schedule.getFri() > 0 ? SYSTools.xx("misc.msg.friday") : "");
                tag += (schedule.getSat() > 0 ? SYSTools.xx("misc.msg.saturday") : "");
                tag += (schedule.getSun() > 0 ? SYSTools.xx("misc.msg.sunday") : "");

                wtag += schedule.getMon();
                wtag += schedule.getTue();
                wtag += schedule.getWed();
                wtag += schedule.getThu();
                wtag += schedule.getFri();
                wtag += schedule.getSat();
                wtag += schedule.getSun();

                result += SYSTools.xx("misc.msg.atchrono") + " " + wtag + ". " + tag + " " + SYSTools.xx("misc.msg.ofTheMonth");
            }
            result += "</b>";
        } else {
            result = "";
        }

        DateFormat df = DateFormat.getDateInstance();
        if (SYSCalendar.isInFuture(schedule.getLDatum().getTime())) {
            result += "<br/><font color=\"red\">" + SYSTools.xx("nursingrecords.prescription.firstApplication") + ": " + df.format(schedule.getLDatum()) + "</font>";
        } else {
            if (schedule.getTaeglich() != 1) { // Wenn nicht jeden Tag, dann das letzte mal anzeigen.
                result += "<br/>" + SYSTools.xx("nursingrecords.prescription.mostRecentApplication") + ": ";
                result += df.format(schedule.getLDatum());
            }
        }

        return result.isEmpty() ? "" : "<div id=\"fonttext\">" + result + "</div>";
    }

    /**
     * every day's repeats are ignored
     *
     * @param schedule
     * @return
     */
    public static String getRepeatPatternAsCompactText(PrescriptionSchedule schedule) {
        String result = "";

        if (schedule.isDaily()) {
            if (schedule.getTaeglich() > 1) {
                result += SYSTools.xx("misc.msg.every") + " " + schedule.getTaeglich() + " " + SYSTools.xx("misc.msg.Days2");
            }
        } else if (schedule.isWeekly()) {
            String text = "";
            if (schedule.getWoechentlich() == 1) {

                text += SYSTools.xx("misc.msg.everyWeek") + " ";
            } else {
                text += SYSTools.xx("misc.msg.every") + " " + schedule.getWoechentlich() + " " + SYSTools.xx("misc.msg.weeks") + " ";
            }

            String daylist = "";

            daylist += (schedule.getMon() > 0 ? SYSTools.xx("misc.msg.monday").substring(0, 3) + ", " : "");
            daylist += (schedule.getTue() > 0 ? SYSTools.xx("misc.msg.tuesday").substring(0, 3) + ", " : "");
            daylist += (schedule.getWed() > 0 ? SYSTools.xx("misc.msg.wednesday").substring(0, 3) + ", " : "");
            daylist += (schedule.getThu() > 0 ? SYSTools.xx("misc.msg.thursday").substring(0, 3) + ", " : "");
            daylist += (schedule.getFri() > 0 ? SYSTools.xx("misc.msg.friday").substring(0, 3) + ", " : "");
            daylist += (schedule.getSat() > 0 ? SYSTools.xx("misc.msg.saturday").substring(0, 3) + ", " : "");
            daylist += (schedule.getSun() > 0 ? SYSTools.xx("misc.msg.sunday").substring(0, 3) + ", " : "");

            if (!daylist.isEmpty()) {
                text += "{" + daylist.substring(0, daylist.length() - 2) + "}";
            }

            result += text;

        } else if (schedule.isMonthly()) {
            String text = "";
            if (schedule.getMonatlich() == 1) {
                text += SYSTools.xx("misc.msg.everyMonth") + " ";
            } else {
                text += SYSTools.xx("misc.msg.every") + " " + schedule.getMonatlich() + " " + SYSTools.xx("misc.msg.months") + " ";
            }

            if (schedule.getTagNum() > 0) {
                text += SYSTools.xx("misc.msg.atchrono") + " " + schedule.getTagNum() + ". " + SYSTools.xx("misc.msg.ofTheMonth");
            } else {
                int wtag = 0;
                String tag = "";

                // In diesem fall kann immer nur ein Wochentag >0 sein. Daher klappt das so.
                tag += (schedule.getMon() > 0 ? SYSTools.xx("misc.msg.monday") : "");
                tag += (schedule.getTue() > 0 ? SYSTools.xx("misc.msg.tuesday") : "");
                tag += (schedule.getWed() > 0 ? SYSTools.xx("misc.msg.wednesday") : "");
                tag += (schedule.getThu() > 0 ? SYSTools.xx("misc.msg.thursday") : "");
                tag += (schedule.getFri() > 0 ? SYSTools.xx("misc.msg.friday") : "");
                tag += (schedule.getSat() > 0 ? SYSTools.xx("misc.msg.saturday") : "");
                tag += (schedule.getSun() > 0 ? SYSTools.xx("misc.msg.sunday") : "");

                wtag += schedule.getMon();
                wtag += schedule.getTue();
                wtag += schedule.getWed();
                wtag += schedule.getThu();
                wtag += schedule.getFri();
                wtag += schedule.getSat();
                wtag += schedule.getSun();

                text += SYSTools.xx("misc.msg.atchrono") + " " + wtag + ". " + tag + " " + SYSTools.xx("misc.msg.ofTheMonth");
            }
            result += text;
        }

        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        if (SYSCalendar.isInFuture(schedule.getLDatum().getTime())) {
            result += " !" + SYSTools.xx("nursingrecords.prescription.firstApplication") + ": " + df.format(schedule.getLDatum());
        } else {
            if (schedule.getTaeglich() != 1) { // Wenn nicht jeden Tag, dann das letzte mal anzeigen.
                result += " " + SYSTools.xx("misc.msg.mostRecent") + " " + df.format(schedule.getLDatum());
            }
        }

        return result;
    }

    public static Phrase getRepeatPatternAsPhrase(PrescriptionSchedule schedule, boolean writeDaily) {
        Phrase phrase = new Phrase();

        if (schedule.isDaily()) {
            if (schedule.getTaeglich() > 1) {
                phrase.add(new Chunk(SYSTools.xx("misc.msg.every") + " " + schedule.getTaeglich() + " " + SYSTools.xx("misc.msg.Days2"), PDF.bold()));
            } else if (writeDaily) {
                phrase.add(new Chunk("misc.msg.daily", PDF.bold()));
            }
        } else if (schedule.isWeekly()) {
            String text = "";
            if (schedule.getWoechentlich() == 1) {

                text += SYSTools.xx("misc.msg.everyWeek") + " ";
            } else {
                text += SYSTools.xx("misc.msg.every") + " " + schedule.getWoechentlich() + " " + SYSTools.xx("misc.msg.weeks") + " ";
            }

            String daylist = "";

            daylist += (schedule.getMon() > 0 ? SYSTools.xx("misc.msg.monday").substring(0, 3) + ", " : "");
            daylist += (schedule.getTue() > 0 ? SYSTools.xx("misc.msg.tuesday").substring(0, 3) + ", " : "");
            daylist += (schedule.getWed() > 0 ? SYSTools.xx("misc.msg.wednesday").substring(0, 3) + ", " : "");
            daylist += (schedule.getThu() > 0 ? SYSTools.xx("misc.msg.thursday").substring(0, 3) + ", " : "");
            daylist += (schedule.getFri() > 0 ? SYSTools.xx("misc.msg.friday").substring(0, 3) + ", " : "");
            daylist += (schedule.getSat() > 0 ? SYSTools.xx("misc.msg.saturday").substring(0, 3) + ", " : "");
            daylist += (schedule.getSun() > 0 ? SYSTools.xx("misc.msg.sunday").substring(0, 3) + ", " : "");

            if (!daylist.isEmpty()) {
                text += "{" + daylist.substring(0, daylist.length() - 2) + "}";
            }

            phrase.add(PDF.chunk(text, PDF.bold()));

        } else if (schedule.isMonthly()) {
            String text = "";
            if (schedule.getMonatlich() == 1) {
                text += SYSTools.xx("misc.msg.everyMonth") + " ";
            } else {
                text += SYSTools.xx("misc.msg.every") + " " + schedule.getMonatlich() + " " + SYSTools.xx("misc.msg.months") + " ";
            }

            if (schedule.getTagNum() > 0) {
                text += SYSTools.xx("misc.msg.atchrono") + " " + schedule.getTagNum() + ". " + SYSTools.xx("misc.msg.ofTheMonth");
            } else {
                int wtag = 0;
                String tag = "";

                // In diesem fall kann immer nur ein Wochentag >0 sein. Daher klappt das so.
                tag += (schedule.getMon() > 0 ? SYSTools.xx("misc.msg.monday") : "");
                tag += (schedule.getTue() > 0 ? SYSTools.xx("misc.msg.tuesday") : "");
                tag += (schedule.getWed() > 0 ? SYSTools.xx("misc.msg.wednesday") : "");
                tag += (schedule.getThu() > 0 ? SYSTools.xx("misc.msg.thursday") : "");
                tag += (schedule.getFri() > 0 ? SYSTools.xx("misc.msg.friday") : "");
                tag += (schedule.getSat() > 0 ? SYSTools.xx("misc.msg.saturday") : "");
                tag += (schedule.getSun() > 0 ? SYSTools.xx("misc.msg.sunday") : "");

                wtag += schedule.getMon();
                wtag += schedule.getTue();
                wtag += schedule.getWed();
                wtag += schedule.getThu();
                wtag += schedule.getFri();
                wtag += schedule.getSat();
                wtag += schedule.getSun();

                text += SYSTools.xx("misc.msg.atchrono") + " " + wtag + ". " + tag + " " + SYSTools.xx("misc.msg.ofTheMonth");
            }
            phrase.add(PDF.chunk(text, PDF.bold()));
        }

        DateFormat df = DateFormat.getDateInstance();
        if (SYSCalendar.isInFuture(schedule.getLDatum().getTime())) {
            phrase.add(Chunk.NEWLINE);
            Font red = PDF.plain();
            red.setColor(BaseColor.RED);
            phrase.add(PDF.chunk(SYSTools.xx("nursingrecords.prescription.firstApplication") + ": " + df.format(schedule.getLDatum()), red));
        } else {
            if (schedule.getTaeglich() != 1) { // Wenn nicht jeden Tag, dann das letzte mal anzeigen.
                phrase.add(Chunk.NEWLINE);
                phrase.add(PDF.chunk(SYSTools.xx("nursingrecords.prescription.mostRecentApplication") + ": " + df.format(schedule.getLDatum())));
            }
        }

        return phrase;
    }

    public static String getDoseAsHTML(PrescriptionSchedule schedule, PrescriptionSchedule vorherigePlanung, boolean singleUsageOnly) {
        String result = "<div id=\"fonttext\">";

        // Wenn die vorherige Planung null ist, dann muss das hier der De erste durchlauf sein
        // gleichzeitig brauchen wir einen Header dann, wenn der Status sich unterscheidet.
        // Sagen wir, dass vorher eine Verordnungn nach früh, spät, nacht usw. war
        // und jetzt eine Uhrzeit kommt. Dann ändert sich der Aufbau der Tabellen.
        boolean headerNeeded = vorherigePlanung == null || getTerminStatus(vorherigePlanung) != getTerminStatus(schedule);
        boolean footerNeeded = vorherigePlanung != null && getTerminStatus(vorherigePlanung) != getTerminStatus(schedule) && getTerminStatus(vorherigePlanung) != MAXDOSE;

        if (footerNeeded) {
            // noch den Footer vom letzten Durchgang dabei. Aber nur, wenn nicht
            // der erste Durchlauf, ein Wechsel stattgefunden hat und der
            // vorherige Zustand nicht MAXDOSE war, das braucht nämlich keinen Footer.
            result += "</table>";
        }

        if (getTerminStatus(schedule) == ROUGHLY) {
            if (headerNeeded) {
                result += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\">" +
                        "   <tr>" +
                        "      <th align=\"center\">" + SYSTools.xx("misc.msg.earlyinthemorning.short") + "</th>" +
                        "      <th align=\"center\">" + SYSTools.xx("misc.msg.morning.short") + "</th>" +
                        "      <th align=\"center\">" + SYSTools.xx("misc.msg.noon.short") + "</th>" +
                        "      <th align=\"center\">" + SYSTools.xx("misc.msg.afternoon.short") + "</th>" +
                        "      <th align=\"center\">" + SYSTools.xx("misc.msg.evening.short") + "</th>" +
                        "      <th align=\"center\">" + SYSTools.xx("misc.msg.lateatnight.short") + "</th>" +
                        "      <th align=\"center\">" + SYSTools.xx("misc.msg.repeat.short") + ".</th>" +
                        "   </tr>";
            }
            result += "    <tr>" +
                    "      <td align=\"center\">" + getValueAsString(schedule.getNachtMo()) + "</td>" +
                    "      <td align=\"center\">" + getValueAsString(schedule.getMorgens()) + "</td>" +
                    "      <td align=\"center\">" + getValueAsString(schedule.getMittags()) + "</td>" +
                    "      <td align=\"center\">" + getValueAsString(schedule.getNachmittags()) + "</td>" +
                    "      <td align=\"center\">" + getValueAsString(schedule.getAbends()) + "</td>" +
                    "      <td align=\"center\">" + getValueAsString(schedule.getNachtAb()) + "</td>" +
                    "      <td>" + getRepeatPattern(schedule, true) + "</td>" +
                    "    </tr>";
            if (singleUsageOnly) {
                result += "</table>";
            }
        } else if (getTerminStatus(schedule) == MAXDOSE) {
            result += "<b>" + SYSTools.xx("nursingrecords.prescription.maxDailyDose") + ": ";
            result += schedule.getMaxAnzahl() + "x " + SYSTools.formatBigDecimal(schedule.getMaxEDosis());
            result += "</b><br/>";
            if (schedule.getCheckAfterHours() != null) {
                result += SYSTools.xx("nursingrecords.prescription.dlgOnDemand.outcomeCheck") + ": ";
                if (schedule.getCheckAfterHours().equals(new BigDecimal("0.5"))) {
                    result += "&frac12; " + SYSTools.xx("misc.msg.Hour");
                } else if (schedule.getCheckAfterHours().equals(BigDecimal.ONE)) {
                    result += "1 " + SYSTools.xx("misc.msg.Hour");
                } else {
                    result += schedule.getCheckAfterHours() + " " + SYSTools.xx("misc.msg.Hours");
                }
                result += "<br/>";
            }
        } else if (getTerminStatus(schedule) == EXACTTIME) {
            if (headerNeeded) {
                result += "<table border=\"1\" cellspacing=\"0\" >" +
                        "   <tr>" +
                        "      <th align=\"center\">" + SYSTools.xx("misc.msg.Time") + "</th>" +
                        "      <th align=\"center\">" + SYSTools.xx("misc.msg.Number") + "</th>" +
                        "      <th align=\"center\">" + SYSTools.xx("misc.msg.repeat.short") + ".</th>" +
                        "   </tr>";
            }
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            result += "    <tr>" +
                    "      <td align=\"center\">" + sdf.format(schedule.getUhrzeit()) + " " + SYSTools.xx("misc.msg.Time.short") + "</td>" +
                    "      <td align=\"center\">" + SYSTools.formatBigDecimal(schedule.getUhrzeitDosis()) + "</td>" +
                    "      <td>" + getRepeatPattern(schedule, true) + "</td>" +
                    "    </tr>";
            if (singleUsageOnly) {
                result += "</table>";
            }
        } else {
            result = "!!" + SYSTools.xx("misc.msg.error") + "!!";
        }
        return result + "</div>";
    }


    public static String getDoseAsCompactText(PrescriptionSchedule schedule) {
        String result = "";
        if (getTerminStatus(schedule) == ROUGHLY) {

            result += schedule.getNachtMo().compareTo(BigDecimal.ZERO) > 0 ? SYSTools.xx("misc.msg.earlyinthemorning.medium") + ". " + SYSTools.formatBigDecimal(schedule.getNachtMo()) + "x; " : "";
            result += schedule.getMorgens().compareTo(BigDecimal.ZERO) > 0 ? SYSTools.xx("misc.msg.morning.medium") + ". " + SYSTools.formatBigDecimal(schedule.getMorgens()) + "x; " : "";
            result += schedule.getMittags().compareTo(BigDecimal.ZERO) > 0 ? SYSTools.xx("misc.msg.noon.medium") + ". " + SYSTools.formatBigDecimal(schedule.getMittags()) + "x; " : "";
            result += schedule.getNachmittags().compareTo(BigDecimal.ZERO) > 0 ? SYSTools.xx("misc.msg.afternoon.medium") + ". " + SYSTools.formatBigDecimal(schedule.getNachmittags()) + "x; " : "";
            result += schedule.getAbends().compareTo(BigDecimal.ZERO) > 0 ? SYSTools.xx("misc.msg.evening.medium") + ". " + SYSTools.formatBigDecimal(schedule.getAbends()) + "x; " : "";
            result += schedule.getNachtAb().compareTo(BigDecimal.ZERO) > 0 ? SYSTools.xx("misc.msg.lateatnight.medium") + ". " + SYSTools.formatBigDecimal(schedule.getNachtAb()) + "x; " : "";

            result = result.substring(0, result.length() - 2);

            String repeat = getRepeatPatternAsCompactText(schedule);
            if (!repeat.isEmpty()) {
                result = "(" + result + " => " + repeat + ")";
            }
        } else if (getTerminStatus(schedule) == MAXDOSE) {
            result += SYSTools.xx("nursingrecords.prescription.maxDailyDose") + ": ";
            result += schedule.getMaxAnzahl() + "x " + SYSTools.formatBigDecimal(schedule.getMaxEDosis());
            if (schedule.getCheckAfterHours() != null) {
                result += "\n";
                result += SYSTools.xx("nursingrecords.prescription.dlgOnDemand.outcomeCheck") + ": ";
                result += schedule.getCheckAfterHours() + " " + SYSTools.xx("misc.msg.Hour(s)");
            }
        } else if (getTerminStatus(schedule) == EXACTTIME) {

            DateTime dt = new DateTime(schedule.getUhrzeit());

            if (dt.getMinuteOfHour() == 0) {
                result += dt.getHourOfDay();
            } else {
                result += DateFormat.getTimeInstance(DateFormat.SHORT).format(schedule.getUhrzeit());
            }

            result += SYSTools.xx("misc.msg.Time.short") + " " + SYSTools.formatBigDecimal(schedule.getUhrzeitDosis()) + "x";
            String repeat = getRepeatPatternAsCompactText(schedule);
            if (!repeat.isEmpty()) {
                result = "(" + result + " => " + repeat + ")";
            }
        } else {
            result = "!!" + SYSTools.xx("misc.msg.error") + "!!";
        }
        return result;
    }

    public static Phrase getRemarkAsPhrase(PrescriptionSchedule schedule) {
        Phrase phrase = new Phrase();

        if (schedule.getPrescription().isOnDemand()) {
            phrase.add(SYSTools.xx("nursingrecords.prescription.maxDailyDose") + ": ");
            phrase.add(schedule.getMaxAnzahl() + "x " + HTMLTools.printDouble(schedule.getMaxEDosis()) + " " + SYSConst.UNITS[schedule.getPrescription().getTradeForm().getDosageForm().getUsageUnit()]);
            phrase.add(Chunk.NEWLINE);
        }

        phrase.add(PrescriptionTools.getOriginalPrescriptionAsPhrase(schedule.getPrescription()));

        Phrase repeat = getRepeatPatternAsPhrase(schedule, false);
        phrase.add(repeat);


        if (!SYSTools.catchNull(schedule.getPrescription().getText()).isEmpty()) {
            Chunk comment = PDF.chunk("misc.msg.comment", PDF.bold());
            comment.setUnderline(0.4f, -1f);
            phrase.add(comment);
            phrase.add(": ");
            phrase.add(PDF.chunk(schedule.getPrescription().getText()));
        }

        if (schedule.getPrescription().getTo().before(SYSConst.DATE_UNTIL_FURTHER_NOTICE)) {
            phrase.add(Chunk.NEWLINE);
            phrase.add(PDF.chunk(SYSTools.xx("nursingrecords.prescription.endsAtChrono") + ": " + DateFormat.getDateInstance().format(schedule.getPrescription().getTo()), PDF.bold()));
        }

        return phrase;
    }


    public static String getRemark(PrescriptionSchedule schedule) {
        String result = "";

        if (schedule.getPrescription().isOnDemand()) {
            result += SYSTools.xx("nursingrecords.prescription.maxDailyDose") + ": ";
            result += schedule.getMaxAnzahl() + "x " + HTMLTools.printDouble(schedule.getMaxEDosis()) + " " + SYSConst.UNITS[schedule.getPrescription().getTradeForm().getDosageForm().getUsageUnit()];
            result += "<br/>";
        }
//        else if (schedule.usesTime()) {
//            result += "<b><u>" + DateFormat.getTimeInstance(DateFormat.SHORT).format(schedule.getUhrzeit()) + " Uhr</u></b> ";
//            result += HTMLTools.printDouble(schedule.getUhrzeitDosis());
//            result += schedule.getPrescription().hasMed() ? " " + SYSConst.UNITS[schedule.getPrescription().getTradeForm().getDosageForm().getUsageUnit()] : "x";
//            result += "<br/>";
//        }

        String wiederholung = getRepeatPattern(schedule, false);
        result += wiederholung;

        String substitution = PrescriptionTools.getOriginalPrescription(schedule.getPrescription());
        if (!substitution.isEmpty()) {
            result += substitution;
        }

        if (!SYSTools.catchNull(schedule.getPrescription().getText()).isEmpty()) {
            if (!wiederholung.isEmpty()) {
                result += "<br/>";
            }
            result += "<b><u>" + SYSTools.xx("misc.msg.comment") + ":</u></b> " + schedule.getPrescription().getText();

        }

        return result.equals("") ? "&nbsp;" : result;
    }


}
