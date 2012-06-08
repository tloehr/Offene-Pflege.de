package entity;


import op.care.vital.DlgVital;
import op.tools.SYSCalendar;
import op.tools.SYSConst;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 28.10.11
 * Time: 16:41
 * To change this template use File | Settings | File Templates.
 */
public class BWerteTools {
    public static final int UNKNOWN = 0;
    public static final int RR = 1;
    public static final int PULS = 2;
    public static final int TEMP = 3;
    public static final int BZ = 4;
    public static final int GEWICHT = 5;
    public static final int GROESSE = 6;
    public static final int ATEM = 7;
    public static final int QUICK = 8;
    public static final int STUHLGANG = 9;
    public static final int ERBRECHEN = 10;
    public static final int BILANZ = 11;


    public static String getPITasHTML(BWerte bwert, boolean showids, boolean colorize) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");
        String color = "";
        if (colorize) {
            if (bwert.isReplaced()) {
                color = SYSConst.html_lightslategrey;
            } else {
                color = SYSCalendar.getHTMLColor4Schicht(SYSCalendar.ermittleSchicht(bwert.getPit()));
            }
        }
        String result = sdf.format(bwert.getPit()) + "; " + bwert.getUser().getNameUndVorname();
        if (showids) {
            result += "<br/><i>(" + bwert.getBwid() + ")</i>";
        }
        return colorize ? "<font " + color + ">" + result + "</font>" : result;
    }

    public static String getAsHTML(BWerte bwert, boolean colorize) {
        String result = "";
        String color = "";
        if (colorize) {
            if (bwert.isReplaced()) {
                color = SYSConst.html_lightslategrey;
            } else {
                color = SYSCalendar.getHTMLColor4Schicht(SYSCalendar.ermittleSchicht(bwert.getPit()));
            }
        }
        if (!bwert.getAttachedFiles().isEmpty()) {
            result += "<font color=\"green\">&#9679;</font>";
        }
        if (!bwert.getAttachedVorgaenge().isEmpty()) {
            result += "<font color=\"red\">&#9679;</font>";
        }
        DateFormat df = DateFormat.getDateTimeInstance();
        if (bwert.isDeleted()) {
            result += "<i>Gelöschter Eintrag. Gelöscht am/um: " + df.format(bwert.getMdate()) + " von " + bwert.getEditedBy().getNameUndVorname() + "</i><br/>";
        }
        if (bwert.isReplacement()) {
            result += "<i>Dies ist ein Eintrag, der nachbearbeitet wurde.<br/>Am/um: " + df.format(bwert.getCdate()) + "<br/>Der Originaleintrag hatte die Nummer: " + bwert.getReplacementFor().getBwid() + "</i><br/>";
        }
        if (bwert.isReplaced()) {
            result += "<i>Dies ist ein Eintrag, der durch eine Nachbearbeitung ungültig wurde. Bitte ignorieren.<br/>Änderung wurde am/um: " + df.format(bwert.getCdate()) + " von " + bwert.getEditedBy().getNameUndVorname() + " vorgenommen.";
            result += "<br/>Der Korrektureintrag hat die Nummer: " + bwert.getReplacedBy().getBwid() + "</i><br/>";
        }
        result += "<b>" + bwert.getWert() + " " + getEinheit(bwert.getXml()) + "</b> (" + getArt(bwert.getXml()) + ")";

        if (bwert.getBemerkung() != null && !bwert.getBemerkung().isEmpty()) {
            result += "<br/><b>Bemerkung:</b> " + bwert.getBemerkung();
        }


//                if (fianzahl > 0) {
//                    //URL url = this.getClass().getResource("/artwork/16x16/attach.png");
//                    //System.out.println(url.getPath());
//                    result += "<font color=\"green\">&#9679;</font>";
//                }
//                if (vrganzahl > 0) {
//                    //URL url = this.getClass().getResource("/artwork/16x16/mail-tagged.png");
//                    //System.out.println(url.getPath());
//                    result += "<font color=\"red\">&#9679;</font>";
//                }
        return colorize ? "<font " + color + ">" + result + "</font>" : result;
    }


//    public static String getArt(String xml) {
//        String result;
//        if (xml.indexOf("<RRSYS/>") >= 0) {
//            result = "Blutdruck Systole";
//        } else if (xml.indexOf("<RRDIA/>") >= 0) {
//            result = "Blutdruck Diastole";
//        } else if (xml.indexOf("<TEMP/>") >= 0) {
//            result = "Temperatur";
//        } else if (xml.indexOf("<PULS/>") >= 0) {
//            result = "Puls";
//        } else if (xml.indexOf("<BZ/>") >= 0) {
//            result = "Blutzucker";
//        } else if (xml.indexOf("<GEWICHT/>") >= 0) {
//            result = "Gewicht";
//        } else if (xml.indexOf("<GROESSE/>") >= 0) {
//            result = "Groesse";
//        } else if (xml.indexOf("<ATEM/>") >= 0) {
//            result = "Atemfrequenz";
//        } else if (xml.indexOf("<braden") >= 0) {
//            result = "Bradenskala";
//        } else if (xml.indexOf("<QUICK/>") >= 0) {
//            result = "Quickwert";
//        } else if (xml.indexOf("<STUHLGANG/>") >= 0) {
//            result = "Stuhlgang";
//        } else if (xml.indexOf("<ERBRECHEN/>") >= 0) {
//            result = "Erbrochen";
//        } else if (xml.indexOf("<BILANZ/>") >= 0) {
//            result = "Ein-/Ausfuhrbilanz";
//        } else {
//            result = "?? unbekannt ??";
//        }
//        return result;
//    }
//
//    public static String getEinheit(String xml) {
//        String result;
//        if (xml.indexOf("<RRSYS/>") >= 0) {
//            result = "mmHg";
//        } else if (xml.indexOf("<RRDIA/>") >= 0) {
//            result = "mmHg";
//        } else if (xml.indexOf("<TEMP/>") >= 0) {
//            result = "°C";
//        } else if (xml.indexOf("<PULS/>") >= 0) {
//            result = "s/m";
//        } else if (xml.indexOf("<BZ/>") >= 0) {
//            result = "mg/dl";
//        } else if (xml.indexOf("<GEWICHT/>") >= 0) {
//            result = "kg";
//        } else if (xml.indexOf("<GROESSE/>") >= 0) {
//            result = "m";
//        } else if (xml.indexOf("<ATEM/>") >= 0) {
//            result = "A/m";
//        } else if (xml.indexOf("<braden") >= 0) {
//            result = "Bradenskala";
//        } else if (xml.indexOf("<QUICK/>") >= 0) {
//            result = "%";
//        } else if (xml.indexOf("<STUHLGANG/>") >= 0) {
//            result = "";
//        } else if (xml.indexOf("<ERBRECHEN/>") >= 0) {
//            result = "";
//        } else if (xml.indexOf("<BILANZ/>") >= 0) {
//            result = "ml";
//        } else {
//            result = "?? unbekannt ??";
//        }
//        return result;
//    }
//
//    public static int getMode(String xml) {
//        int result;
//        if (xml.indexOf("<RRSYS/>") >= 0) {
//            result = DlgVital.MODE_RRSYS;
//        } else if (xml.indexOf("<RRDIA/>") >= 0) {
//            result = DlgVital.MODE_RRDIA;
//        } else if (xml.indexOf("<TEMP/>") >= 0) {
//            result = DlgVital.MODE_TEMP;
//        } else if (xml.indexOf("<RR/>") >= 0) {
//            result = DlgVital.MODE_RR;
//        } else if (xml.indexOf("<PULS/>") >= 0) {
//            result = DlgVital.MODE_PULS;
//        } else if (xml.indexOf("<BZ/>") >= 0) {
//            result = DlgVital.MODE_BZ;
//        } else if (xml.indexOf("<GEWICHT/>") >= 0) {
//            result = DlgVital.MODE_GEWICHT;
//        } else if (xml.indexOf("<GROESSE/>") >= 0) {
//            result = DlgVital.MODE_GROESSE;
//        } else if (xml.indexOf("<ATEM/>") >= 0) {
//            result = DlgVital.MODE_ATEM;
//        } else if (xml.indexOf("<QUICK/>") >= 0) {
//            result = DlgVital.MODE_QUICK;
//        } else if (xml.indexOf("<STUHLGANG/>") >= 0) {
//            result = DlgVital.MODE_STUHLGANG;
//        } else if (xml.indexOf("<ERBRECHEN/>") >= 0) {
//            result = DlgVital.MODE_ERBRECHEN;
//        } else if (xml.indexOf("<BILANZ/>") >= 0) {
//            result = DlgVital.MODE_BILANZ;
//        } else {
//            result = DlgVital.MODE_UNKNOWN;
//        }
//        return result;
//    }
//
//    public static String getXML(int mode) {
//
//        String result;
//
//        switch (mode) {
//            case DlgVital.MODE_RRSYS: {
//                result = "<RRSYS/>";
//                break;
//            }
//            case DlgVital.MODE_RRDIA: {
//                result = "<RRDIA/>";
//                break;
//            }
//            case DlgVital.MODE_TEMP: {
//                result = "<TEMP/>";
//                break;
//            }
//            case DlgVital.MODE_RR: {
//                result = "<RR/>";
//                break;
//            }
//            case DlgVital.MODE_PULS: {
//                result = "<PULS/>";
//                break;
//            }
//            case DlgVital.MODE_BZ: {
//                result = "<BZ/>";
//                break;
//            }
//            case DlgVital.MODE_GEWICHT: {
//                result = "<GEWICHT/>";
//                break;
//            }
//            case DlgVital.MODE_GROESSE: {
//                result = "<GROESSE/>";
//                break;
//            }
//            case DlgVital.MODE_ATEM: {
//                result = "<ATEM/>";
//                break;
//            }
//            case DlgVital.MODE_QUICK: {
//                result = "<QUICK/>";
//                break;
//            }
//            case DlgVital.MODE_ERBRECHEN: {
//                result = "<ERBRECHEN/>";
//                break;
//            }
//            case DlgVital.MODE_STUHLGANG: {
//                result = "<STUHLGANG/>";
//                break;
//            }
//            case DlgVital.MODE_BILANZ: {
//                result = "<BILANZ/>";
//                break;
//            }
//            default: {
//                result = "<UNKNOWN/>";
//                break;
//            }
//        }
//        return result;
//    }
}
