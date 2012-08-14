package entity.prescription;

import op.tools.HTMLTools;
import op.tools.SYSCalendar;
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
        if (planung.verwendetZeiten()) {
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
     *
     * @param planung um die es geht
     * @param writeTaeglich hiermit kann man festlegen, ob bei den Dosierungen, die jeden Tag gegeben werden sollen, das Wort <i>täglich</i> in die Wiederholungsspalte geschrieben wird oder nicht.
     * @return
     */
    public static String getWiederholung(PrescriptionSchedule planung, boolean writeTaeglich) {
        String result = "<div id=\"fonttext\">";

        if (planung.isTaeglich()) {
            if (planung.getTaeglich() > 1) {
                result += "<b>alle " + planung.getTaeglich() + " Tage</b>";
            } else if (writeTaeglich) {
                result += "<b>täglich</b>";
            }
        } else if (planung.isWoechentlich()) {
            result += "<b>";
            if (planung.getWoechentlich() == 1) {
                result += "jede Woche ";
            } else {
                result += "alle " + planung.getWoechentlich() + " Wochen ";
            }

            result += (planung.getMon() > 0 ? "Mon " : "");
            result += (planung.getDie() > 0 ? "Die " : "");
            result += (planung.getMit() > 0 ? "Mit " : "");
            result += (planung.getDon() > 0 ? "Don " : "");
            result += (planung.getFre() > 0 ? "Fre " : "");
            result += (planung.getSam() > 0 ? "Sam " : "");
            result += (planung.getSon() > 0 ? "Son " : "");

            result += "</b>";
        } else if (planung.isMonatlich()) {
            result += "<b>";
            if (planung.getMonatlich() == 1) {
                result += "jeden Monat ";
            } else {
                result += "alle " + planung.getMonatlich() + " Monate ";
            }

            if (planung.getTagNum() > 0) {
                result += "jeweils am " + planung.getTagNum() + ". des Monats";
            } else {
                int wtag = 0;
                String tag = "";

                // In diesem fall kann immer nur ein Wochentag >0 sein. Daher klappt das so.
                tag += (planung.getMon() > 0 ? "Montag" : "");
                tag += (planung.getDie() > 0 ? "Dienstag" : "");
                tag += (planung.getMit() > 0 ? "Mittwoch" : "");
                tag += (planung.getDon() > 0 ? "Donnerstag" : "");
                tag += (planung.getFre() > 0 ? "Freitag" : "");
                tag += (planung.getSam() > 0 ? "Samstag" : "");
                tag += (planung.getSon() > 0 ? "Sonntag" : "");

                wtag += planung.getMon();
                wtag += planung.getDie();
                wtag += planung.getMit();
                wtag += planung.getDon();
                wtag += planung.getFre();
                wtag += planung.getSam();
                wtag += planung.getSon();

                result += "jeweils am " + wtag + ". " + tag + " des Monats";
            }
            result += "</b>";
        } else {
            result = "";
        }

        if (planung.getTaeglich() != 1) { // Wenn nicht jeden Tag, dann das letzte mal anzeigen.
            DateFormat df = DateFormat.getDateInstance();
            if (SYSCalendar.isInFuture(planung.getLDatum().getTime())) {
                result += "<br/>erste Anwendung am: ";
            } else {
                result += "<br/>Zuletzt eingeplant: ";
            }
            result += df.format(planung.getLDatum());
        }

        return result + "</div>";
    }

    public static String getDosisAsHTML(PrescriptionSchedule planung, PrescriptionSchedule vorherigePlanung, boolean singleUsageOnly) {
        String result = "<div id=\"fonttext\">";

        // Wenn die vorherige Planung null ist, dann muss das hier der De erste durchlauf sein
        // gleichzeitig brauchen wir einen Header dann, wenn der Status sich unterscheidet.
        // Sagen wir, dass vorher eine Verordnungn nach früh, spät, nacht usw. war
        // und jetzt eine Uhrzeit kommt. Dann ändert sich der Aufbau der Tabellen.
        boolean headerNeeded = vorherigePlanung == null || getTerminStatus(vorherigePlanung) != getTerminStatus(planung);
        boolean footerNeeded = vorherigePlanung != null && getTerminStatus(vorherigePlanung) != getTerminStatus(planung) && getTerminStatus(vorherigePlanung) != MAXDOSIS;

        if (footerNeeded) {
            // noch den Footer vom letzten Durchgang dabei. Aber nur, wenn nicht
            // der erste Durchlauf, ein Wechsel stattgefunden hat und der
            // vorherige Zustand nicht MAXDOSIS war, das braucht nämlich keinen Footer.
            result += "</table>";
        }

        if (getTerminStatus(planung) == ZEIT) {
            if (headerNeeded) {
                result += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\">" +
                        "   <tr>" +
                        "      <th align=\"center\">fm</th>" +
                        "      <th align=\"center\">mo</th>" +
                        "      <th align=\"center\">mi</th>" +
                        "      <th align=\"center\">nm</th>" +
                        "      <th align=\"center\">ab</th>" +
                        "      <th align=\"center\">sa</th>" +
                        "      <th align=\"center\">Wdh.</th>" +
                        "   </tr>";
            }
            result += "    <tr>" +
                    "      <td align=\"center\">" + getValueAsString(planung.getNachtMo()) + "</td>" +
                    "      <td align=\"center\">" + getValueAsString(planung.getMorgens()) + "</td>" +
                    "      <td align=\"center\">" + getValueAsString(planung.getMittags()) + "</td>" +
                    "      <td align=\"center\">" + getValueAsString(planung.getNachmittags()) + "</td>" +
                    "      <td align=\"center\">" + getValueAsString(planung.getAbends()) + "</td>" +
                    "      <td align=\"center\">" + getValueAsString(planung.getNachtAb()) + "</td>" +
                    "      <td>" + getWiederholung(planung, true) + "</td>" +
                    "    </tr>";
            if (singleUsageOnly) {
                result += "</table>";
            }
        } else if (getTerminStatus(planung) == MAXDOSIS) {
//                        if (rsDosis.getLong("DafID") > 0){
//                            result += "Maximale Tagesdosis: ";
//                        } else {
//                            result += "Maximale Häufigkeit: ";
//                        }
            result += "<b>Maximale Tagesdosis: ";
            result += planung.getMaxAnzahl() + "x " + SYSTools.printDouble(planung.getMaxEDosis().doubleValue());
            result += "</b><br/>";
        } else if (getTerminStatus(planung) == UHRZEIT) {
            if (headerNeeded) {
                result += "<table border=\"1\" cellspacing=\"0\" >" +
                        "   <tr>" +
                        "      <th align=\"center\">Uhrzeit</th>" +
                        "      <th align=\"center\">Anzahl</th>" +
                        "      <th align=\"center\">Wdh.</th>" +
                        "   </tr>";
            }
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            result += "    <tr>" +
                    "      <td align=\"center\">" + sdf.format(planung.getUhrzeit()) + " Uhr</td>" +
                    "      <td align=\"center\">" + planung.getUhrzeitDosis().toPlainString() + "</td>" +
                    "      <td>" + getWiederholung(planung, true) + "</td>" +
                    "    </tr>";
            if (singleUsageOnly) {
                result += "</table>";
            }
        } else {
            result = "!!FEHLER!!";
        }
        return result + "</div>";
    }


    public static String getHinweis(PrescriptionSchedule planung, boolean writeTaeglich) {
        String result = "";

        // Handelt es sich hierbei vielleicht um Uhrzeit oder Bedarf ?
        if (planung.verwendetMaximalDosis()) {
            result += "Maximale Tagesdosis: ";
            result += planung.getMaxAnzahl() + "x " + HTMLTools.printDouble(planung.getMaxEDosis()) + " " + DosageFormTools.EINHEIT[planung.getPrescription().getDarreichung().getMedForm().getAnwEinheit()];
            result += "<br/>";
        } else if (planung.verwendetUhrzeit()) {

            result += "<b><u>" + DateFormat.getTimeInstance(DateFormat.SHORT).format(planung.getUhrzeit()) + " Uhr</u></b> ";
            result += HTMLTools.printDouble(planung.getUhrzeitDosis());
            result += planung.getPrescription().hasMedi() ? " " + DosageFormTools.EINHEIT[planung.getPrescription().getDarreichung().getMedForm().getAnwEinheit()] : "x";
            result += "<br/>";
        }

        String wiederholung = getWiederholung(planung, writeTaeglich);
        result += wiederholung;

        if (!SYSTools.catchNull(planung.getPrescription().getBemerkung()).isEmpty()) {
            if (!wiederholung.isEmpty()) {
                result += "<br/>";
            }
            result += "<b><u>Bemerkung:</u></b> " + planung.getPrescription().getBemerkung();
        }

        return result.equals("") ? "&nbsp;" : result;
    }



}
