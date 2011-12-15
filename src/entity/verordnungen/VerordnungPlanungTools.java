package entity.verordnungen;

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
public class VerordnungPlanungTools {
    public static final int ZEIT = 0;
    public static final int UHRZEIT = 1;
    public static final int MAXDOSIS = 2;


    public static int getTerminStatus(VerordnungPlanung planung) {
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

    public static String getWiederholung(VerordnungPlanung planung) {
        String result = "";

        if (planung.isTaeglich()) {
            if (planung.getTaeglich() > 1) {
                result += "<b>alle " + planung.getTaeglich() + " Tage</b>";
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

        return result;
    }

    public static String getDosisAsHTML(VerordnungPlanung planung, VerordnungPlanung vorherigePlanung, boolean singleUsageOnly) {
        String result = "";


        boolean headerNeeded = vorherigePlanung == null || getTerminStatus(vorherigePlanung) != getTerminStatus(planung);
        boolean footerNeeded = singleUsageOnly;

        if (vorherigePlanung != null && headerNeeded && getTerminStatus(vorherigePlanung) != MAXDOSIS) {
            // noch den Footer vom letzten Durchgang dabei. Aber nur, wenn nicht
            // der erste Durchlauf, ein Wechsel stattgefunden hat und der
            // vorherige Zustand nicht MAXDOSIS war, das braucht nämlich keinen Footer.
            result += "</table>";
        }

        vorherigePlanung = planung;
        if (getTerminStatus(planung) == ZEIT) {
            if (headerNeeded) {
                result += "<table border=\"1\" cellspacing=\"0\">" +
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
                    "      <td>" + getWiederholung(planung) + "</td>" +
                    "    </tr>";
            if (footerNeeded) {
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
                result += "<table border=\"1\" >" +
                        "   <tr>" +
                        "      <th align=\"center\">Uhrzeit</th>" +
                        "      <th align=\"center\">Anzahl</th>" +
                        "      <th align=\"center\">Wdh.</th>" +
                        "   </tr>";
            }
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            result += "    <tr>" +
                    "      <td align=\"center\">" + sdf.format(planung.getUhrzeit()) + " Uhr</td>" +
                    "      <td align=\"center\">" + SYSTools.printDouble(planung.getUhrzeitDosis().doubleValue()) + "</td>" +
                    "      <td>" + getWiederholung(planung) + "</td>" +
                    "    </tr>";
        } else {
            result = "!!FEHLER!!";
        }
        return result;
    }

}
