package entity.prescription;

import op.OPDE;
import op.care.prescription.PnlPrescription;
import op.tools.HTMLTools;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;

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
    public static final int ZEIT = 0;
    public static final int UHRZEIT = 1;
    public static final int MAXDOSIS = 2;


    public static int getTerminStatus(PrescriptionSchedule planung) {
        int status = 0;
        if (planung.usesTimesOfTheDay()) {
            status = ZEIT;
        } else if (planung.verwendetMaximalDosis()) {
            status = MAXDOSIS;
        } else {
            status = UHRZEIT;
        }
        return status;
    }

    public static String getValueAsString(BigDecimal bd) {
        return (bd.compareTo(BigDecimal.ZERO) > 0 ? SYSTools.printDouble(bd.doubleValue()) : "--");
    }

    /**
     * @param schedule      um die es geht
     * @param writeTaeglich hiermit kann man festlegen, ob bei den Dosierungen, die jeden Tag gegeben werden sollen, das Wort <i>täglich</i> in die Wiederholungsspalte geschrieben wird oder nicht.
     * @return
     */
    public static String getRepeatPattern(PrescriptionSchedule schedule, boolean writeTaeglich) {
        String result = "";

        if (schedule.isTaeglich()) {
            if (schedule.getTaeglich() > 1) {
                result += "<b>" + OPDE.lang.getString("misc.msg.every") + " " + schedule.getTaeglich() + " " + OPDE.lang.getString("misc.msg.Days2") + "</b>";
            } else if (writeTaeglich) {
                result += "<b>" + OPDE.lang.getString("misc.msg.daily") + "</b>";
            }
        } else if (schedule.isWoechentlich()) {
            result += "<b>";
            if (schedule.getWoechentlich() == 1) {
                result += OPDE.lang.getString("misc.msg.everyWeek") + " ";
            } else {
                result += OPDE.lang.getString("misc.msg.every") + " " + schedule.getWoechentlich() + " " + OPDE.lang.getString("misc.msg.weeks") + " ";
            }

            String daylist = "";

            daylist += (schedule.getMon() > 0 ? OPDE.lang.getString("misc.msg.monday").substring(0, 3) + ", " : "");
            daylist += (schedule.getTue() > 0 ? OPDE.lang.getString("misc.msg.tuesday").substring(0, 3) + ", " : "");
            daylist += (schedule.getWed() > 0 ? OPDE.lang.getString("misc.msg.wednesday").substring(0, 3) + ", " : "");
            daylist += (schedule.getThu() > 0 ? OPDE.lang.getString("misc.msg.thursday").substring(0, 3) + ", " : "");
            daylist += (schedule.getFri() > 0 ? OPDE.lang.getString("misc.msg.friday").substring(0, 3) + ", " : "");
            daylist += (schedule.getSat() > 0 ? OPDE.lang.getString("misc.msg.saturday").substring(0, 3) + ", " : "");
            daylist += (schedule.getSun() > 0 ? OPDE.lang.getString("misc.msg.sunday").substring(0, 3) + ", " : "");

            if (!daylist.isEmpty()) {
                result += "{" + daylist.substring(0, daylist.length() - 2) + "}";
            }

            result += "</b>";
        } else if (schedule.isMonatlich()) {
            result += "<b>";
            if (schedule.getMonatlich() == 1) {
                result += OPDE.lang.getString("misc.msg.everyMonth") + " ";
            } else {
                result += OPDE.lang.getString("misc.msg.every") + " " + schedule.getMonatlich() + " " + OPDE.lang.getString("misc.msg.months") + " ";
            }

            if (schedule.getTagNum() > 0) {
                result += OPDE.lang.getString("misc.msg.atchrono") + " " + schedule.getTagNum() + ". " + OPDE.lang.getString("misc.msg.ofTheMonth");
            } else {
                int wtag = 0;
                String tag = "";

                // In diesem fall kann immer nur ein Wochentag >0 sein. Daher klappt das so.
                tag += (schedule.getMon() > 0 ? OPDE.lang.getString("misc.msg.monday") : "");
                tag += (schedule.getTue() > 0 ? OPDE.lang.getString("misc.msg.tuesday") : "");
                tag += (schedule.getWed() > 0 ? OPDE.lang.getString("misc.msg.wednesday") : "");
                tag += (schedule.getThu() > 0 ? OPDE.lang.getString("misc.msg.thursday") : "");
                tag += (schedule.getFri() > 0 ? OPDE.lang.getString("misc.msg.friday") : "");
                tag += (schedule.getSat() > 0 ? OPDE.lang.getString("misc.msg.saturday") : "");
                tag += (schedule.getSun() > 0 ? OPDE.lang.getString("misc.msg.sunday") : "");

                wtag += schedule.getMon();
                wtag += schedule.getTue();
                wtag += schedule.getWed();
                wtag += schedule.getThu();
                wtag += schedule.getFri();
                wtag += schedule.getSat();
                wtag += schedule.getSun();

                result += OPDE.lang.getString("misc.msg.atchrono") + " " + wtag + ". " + tag + " " + OPDE.lang.getString("misc.msg.ofTheMonth");
            }
            result += "</b>";
        } else {
            result = "";
        }

        DateFormat df = DateFormat.getDateInstance();
        if (SYSCalendar.isInFuture(schedule.getLDatum().getTime())) {
            result += "<br/><font color=\"red\">" + OPDE.lang.getString(PnlPrescription.internalClassID + ".firstApplication") + ": " + df.format(schedule.getLDatum())+"</font>";
        } else {
            if (schedule.getTaeglich() != 1) { // Wenn nicht jeden Tag, dann das letzte mal anzeigen.
                result += "<br/>" + OPDE.lang.getString(PnlPrescription.internalClassID + ".mostRecentApplication") + ": ";
                result += df.format(schedule.getLDatum());
            }
        }

        return result.isEmpty() ? "" : "<div id=\"fonttext\">" + result + "</div>";
    }

    public static String getDoseAsHTML(PrescriptionSchedule schedule, PrescriptionSchedule vorherigePlanung, boolean singleUsageOnly) {
        String result = "<div id=\"fonttext\">";

        // Wenn die vorherige Planung null ist, dann muss das hier der De erste durchlauf sein
        // gleichzeitig brauchen wir einen Header dann, wenn der Status sich unterscheidet.
        // Sagen wir, dass vorher eine Verordnungn nach früh, spät, nacht usw. war
        // und jetzt eine Uhrzeit kommt. Dann ändert sich der Aufbau der Tabellen.
        boolean headerNeeded = vorherigePlanung == null || getTerminStatus(vorherigePlanung) != getTerminStatus(schedule);
        boolean footerNeeded = vorherigePlanung != null && getTerminStatus(vorherigePlanung) != getTerminStatus(schedule) && getTerminStatus(vorherigePlanung) != MAXDOSIS;

        if (footerNeeded) {
            // noch den Footer vom letzten Durchgang dabei. Aber nur, wenn nicht
            // der erste Durchlauf, ein Wechsel stattgefunden hat und der
            // vorherige Zustand nicht MAXDOSIS war, das braucht nämlich keinen Footer.
            result += "</table>";
        }

        if (getTerminStatus(schedule) == ZEIT) {
            if (headerNeeded) {
                result += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\">" +
                        "   <tr>" +
                        "      <th align=\"center\">" + OPDE.lang.getString("misc.msg.earlyinthemorning.short") + "</th>" +
                        "      <th align=\"center\">" + OPDE.lang.getString("misc.msg.morning.short") + "</th>" +
                        "      <th align=\"center\">" + OPDE.lang.getString("misc.msg.noon.short") + "</th>" +
                        "      <th align=\"center\">" + OPDE.lang.getString("misc.msg.afternoon.short") + "</th>" +
                        "      <th align=\"center\">" + OPDE.lang.getString("misc.msg.evening.short") + "</th>" +
                        "      <th align=\"center\">" + OPDE.lang.getString("misc.msg.lateatnight.short") + "</th>" +
                        "      <th align=\"center\">" + OPDE.lang.getString("misc.msg.repeat.short") + ".</th>" +
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
        } else if (getTerminStatus(schedule) == MAXDOSIS) {
            result += "<b>" + OPDE.lang.getString(PnlPrescription.internalClassID + ".maxDailyDose") + ": ";
            result += schedule.getMaxAnzahl() + "x " + SYSTools.printDouble(schedule.getMaxEDosis().doubleValue());
            result += "</b><br/>";
        } else if (getTerminStatus(schedule) == UHRZEIT) {
            if (headerNeeded) {
                result += "<table border=\"1\" cellspacing=\"0\" >" +
                        "   <tr>" +
                        "      <th align=\"center\">" + OPDE.lang.getString("misc.msg.Time") + "</th>" +
                        "      <th align=\"center\">" + OPDE.lang.getString("misc.msg.Number") + "</th>" +
                        "      <th align=\"center\">" + OPDE.lang.getString("misc.msg.repeat.short") + ".</th>" +
                        "   </tr>";
            }
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            result += "    <tr>" +
                    "      <td align=\"center\">" + sdf.format(schedule.getUhrzeit()) + " " + OPDE.lang.getString("misc.msg.Time.short") + "</td>" +
                    "      <td align=\"center\">" + schedule.getUhrzeitDosis().toPlainString() + "</td>" +
                    "      <td>" + getRepeatPattern(schedule, true) + "</td>" +
                    "    </tr>";
            if (singleUsageOnly) {
                result += "</table>";
            }
        } else {
            result = "!!" + OPDE.lang.getString("misc.msg.error") + "!!";
        }
        return result + "</div>";
    }


    public static String getRemark(PrescriptionSchedule schedule) {
        String result = "";

        if (schedule.getPrescription().isOnDemand()) {
            result += OPDE.lang.getString(PnlPrescription.internalClassID + ".maxDailyDose") + ": ";
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
            result += "<b><u>" + OPDE.lang.getString("misc.msg.comment") + ":</u></b> " + schedule.getPrescription().getText();

        }

        return result.equals("") ? "&nbsp;" : result;
    }


}
