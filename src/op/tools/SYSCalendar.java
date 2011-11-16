/*
 * OffenePflege
 * Copyright (C) 2008 Torsten Löhr
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License V2 as published by the Free Software Foundation
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even 
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to 
 * the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
 * www.offene-pflege.de
 * ------------------------ 
 * Auf deutsch (freie Übersetzung. Rechtlich gilt die englische Version)
 * Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License, 
 * wie von der Free Software Foundation veröffentlicht, weitergeben und/oder modifizieren, gemäß Version 2 der Lizenz.
 *
 * Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, daß es Ihnen von Nutzen sein wird, aber 
 * OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN 
 * BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, 
 * schreiben Sie an die Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA.
 * 
 */
package op.tools;

import com.toedter.calendar.JDateChooser;
import op.OPDE;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class SYSCalendar {

    public static String printGC(GregorianCalendar gc) {
        return (gc.get(GregorianCalendar.YEAR) + "-" + (gc.get(GregorianCalendar.MONTH) + 1) + "-" + gc.get(GregorianCalendar.DAY_OF_MONTH));
    }

    public static GregorianCalendar heute() {
        return new GregorianCalendar();
    }

    public static Date today_date() {
        return new Date();
    }

    /**
     * Dasselbe wie die gleichnamige Methode, jedoch wird hier als Referenzzeitpunkt immer
     * die aktuelle Zeit genommen.
     *
     * @param time   Zeitpunkt in der Vergangenheit, der geprüft werden soll.
     * @param offset Anzahl der Minuten, welche die zwei Zeipunkte maximal auseinader liegen dürfen.
     * @return true, wenn offset nicht überschritten wird. false, sonst.
     */
    public static boolean earlyEnough(long time, int offset) {
        return earlyEnough(now(), time, offset);
    }

    public static boolean isInFuture(long time) {
        return time > nowDB();
    }

    /**
     * Generiert ein Array aus Uhrzeiten in der Form {"17:00","17:15"...}
     * Der verwendete Datentyp ist Time.
     */
    public static ArrayList fillUhrzeiten() {
        ArrayList list = new ArrayList();
        GregorianCalendar gc = today();
        for (int i = 1; i <= 96; i++) {
            list.add(new ListElement(toGermanTime(gc), gc.clone()));
            gc.add(GregorianCalendar.MINUTE, 15);
        }
        return list;
    }

    /**
     * Ermittelt ob zwei Zeitpunkte nur höchstens eine bestimmte Anzahl von Minuten auseinanderliegen.
     *
     * @param reftime Ausgangszeitpunkt. Wird meistens die aktuelle Zeit sein.
     * @param time    Zeitpunkt in der Vergangenheit, der geprüft werden soll.
     * @param offset  Anzahl der Minuten, welche die zwei Zeipunkte maximal auseinader liegen dürfen.
     * @return true, wenn offset nicht überschritten wird. false, sonst.
     */
    public static boolean earlyEnough(long reftime, long time, int offset) {
        GregorianCalendar gcreftime = new GregorianCalendar();
        gcreftime.setTimeInMillis(reftime);
        GregorianCalendar gctime = new GregorianCalendar();
        gctime.setTimeInMillis(time);
        gctime.add(GregorianCalendar.MINUTE, offset);
        return gctime.after(gcreftime);
    }

    public static String printGCGermanStyle(GregorianCalendar gc) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        String result;
        Date d = new Date(gc.getTimeInMillis());
        //result =  gc.get(GregorianCalendar.DAY_OF_MONTH) + "." + (gc.get(GregorianCalendar.MONTH)+1) + "." + gc.get(GregorianCalendar.YEAR);
        result = formatter.format(d);
        if (gc.equals(SYSConst.BIS_AUF_WEITERES)) {
            result = "bis auf weiteres";
        }
        if (gc.equals(SYSConst.VON_ANFANG_AN)) {
            result = "von anfang an";
        }
        return (result);
    }

    public static String GC_MMMYY(GregorianCalendar gc) {
        return (MonatName(gc) + " " + gc.get(GregorianCalendar.YEAR));
    }

    public static String printGermanStyle(Date d) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        String result;
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(d);
        result = formatter.format(d);
        if (gc.equals(SYSConst.BIS_AUF_WEITERES)) {
            result = "bis auf weiteres";
        }
        if (gc.equals(SYSConst.VON_ANFANG_AN)) {
            result = "von anfang an";
        }
        return (result);
    }

    public static String printGermanStyleShort(Date d) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy");
        String result;
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(d);
        result = formatter.format(d);
        if (gc.equals(SYSConst.BIS_AUF_WEITERES_WO_TIME)) {
            result = "==>";
        }
        if (gc.equals(SYSConst.VON_ANFANG_AN)) {
            result = "|<=";
        }
        if (sameDay(gc, new GregorianCalendar()) == 0) {
            result = "heute";
        }
        GregorianCalendar heute = new GregorianCalendar();
        GregorianCalendar morgen = (GregorianCalendar) heute.clone();
        morgen.add(GregorianCalendar.DATE, +1);
        GregorianCalendar gestern = (GregorianCalendar) heute.clone();
        gestern.add(GregorianCalendar.DATE, -1);

        if (sameDay(gc, gestern) == 0) {
            result = "gestern";
        }
        if (sameDay(gc, morgen) == 0) {
            result = "morgen";
        }
        return (result);
    }

    public static GregorianCalendar toGC(Date d) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(d.getTime());
        return gc;
    }

    public static GregorianCalendar toGC(long l) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(l);
        return gc;
    }


    public static String toGermanTime(GregorianCalendar gc) {
        Date date = new Date(gc.getTimeInMillis());
        Format formatter;

        formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(date);
    }

    public static String toGermanTime(Time t) {
        if (t == null) {
            return "";
        }
        Date d = new Date(t.getTime());
        Format formatter;

        formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(d);
    }


    public static String MonatName(GregorianCalendar gc) {
        switch (gc.get(GregorianCalendar.MONTH)) {
            case GregorianCalendar.JANUARY: {
                return "Januar";
            }
            case GregorianCalendar.FEBRUARY: {
                return "Februar";
            }
            case GregorianCalendar.MARCH: {
                return "März";
            }
            case GregorianCalendar.APRIL: {
                return "April";
            }
            case GregorianCalendar.MAY: {
                return "Mai";
            }
            case GregorianCalendar.JUNE: {
                return "Juni";
            }
            case GregorianCalendar.JULY: {
                return "Juli";
            }
            case GregorianCalendar.AUGUST: {
                return "August";
            }
            case GregorianCalendar.SEPTEMBER: {
                return "September";
            }
            case GregorianCalendar.OCTOBER: {
                return "Oktober";
            }
            case GregorianCalendar.NOVEMBER: {
                return "November";
            }
            case GregorianCalendar.DECEMBER: {
                return "Dezember";
            }

            default: {
                return "";
            }
        }
    } // MonatName(GregorianCalendar gc)

    public static String WochentagName(GregorianCalendar gc) {
        switch (gc.get(GregorianCalendar.DAY_OF_WEEK)) {
            case GregorianCalendar.SUNDAY: {
                return "Sonntag";
            }
            case GregorianCalendar.MONDAY: {
                return "Montag";
            }
            case GregorianCalendar.TUESDAY: {
                return "Dienstag";
            }
            case GregorianCalendar.WEDNESDAY: {
                return "Mittwoch";
            }
            case GregorianCalendar.THURSDAY: {
                return "Donnerstag";
            }
            case GregorianCalendar.FRIDAY: {
                return "Freitag";
            }
            case GregorianCalendar.SATURDAY: {
                return "Samstag";
            }
            default: {
                return "";
            }
        }
    } //public static String WochentagName(GregorianCalendar gc){

    /**
     * Calculates the number of days between two calendar days in a manner
     * which is independent of the Calendar type used.
     *
     * @param d1 The first date.
     * @param d2 The second date.
     * @return The number of days between the two dates.  Zero is
     *         returned if the dates are the same, one if the dates are
     *         adjacent, etc.  The order of the dates
     *         does not matter, the value returned is always >= 0.
     *         If Calendar types of d1 and d2
     *         are different, the result may not be accurate.
     */
    public static int getDaysBetween(java.util.Calendar d1, java.util.Calendar d2) {
        if (d1.after(d2)) {  // swap dates so that d1 is start and d2 is end
            java.util.Calendar swap = d1;
            d1 = d2;
            d2 = swap;
        }
        int days = d2.get(java.util.Calendar.DAY_OF_YEAR) -
                d1.get(java.util.Calendar.DAY_OF_YEAR);
        int y2 = d2.get(java.util.Calendar.YEAR);
        if (d1.get(java.util.Calendar.YEAR) != y2) {
            d1 = (java.util.Calendar) d1.clone();
            do {
                days += d1.getActualMaximum(java.util.Calendar.DAY_OF_YEAR);
                d1.add(java.util.Calendar.YEAR, 1);
            } while (d1.get(java.util.Calendar.YEAR) != y2);
        }
        return days;
    } // getDaysBetween()

    public static GregorianCalendar erkenneDatum(String input) throws NumberFormatException {
        if (input == null || input.equals("")) {
            throw new NumberFormatException("leere Eingabe");
        }
        if (input.indexOf(".") + input.indexOf(",") + input.indexOf("-") == -3) {
            input += "."; // er war zu faul auch nur einen punkt anzuhängen.
        }
        StringTokenizer st = new StringTokenizer(input, ",.-");
        if (st.countTokens() == 1) { // Vielleicht fehlen ja nur die Monats- und Jahresangaben. Dann hängen wir sie einach an.
            input += (SYSCalendar.today().get(GregorianCalendar.MONTH) + 1) + "." + SYSCalendar.today().get(GregorianCalendar.YEAR);
            st = new StringTokenizer(input, ",.-"); // dann nochmal aufteilen...
        }
        if (st.countTokens() == 2) { // Vielleicht fehlt ja nur die Jahresangabe. Dann hängen wir es einfach an.

            if (!input.trim().substring(input.length() - 1).equals(".") && !input.trim().substring(input.length() - 1).equals(",")) {
                input += "."; // er war zu faul den letzten Punkt anzuhängen.
            }
            input += SYSCalendar.today().get(GregorianCalendar.YEAR);
            st = new StringTokenizer(input, ",.-"); // dann nochmal aufteilen...
        }
        if (st.countTokens() != 3) {
            throw new NumberFormatException("falsches Format");
        }
        String sTag = st.nextToken();
        String sMonat = st.nextToken();
        String sJahr = st.nextToken();
        int tag, monat, jahr;
        // Hier ist das Jahr 2010 Problem
        GregorianCalendar now = new GregorianCalendar();
        int decade = (now.get(GregorianCalendar.YEAR) / 10) * 10;
        int century = (now.get(GregorianCalendar.YEAR) / 100) * 100;

        try {
            tag = Integer.parseInt(sTag);
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException("tag");
        }
        try {
            monat = Integer.parseInt(sMonat);
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException("monat");
        }
        try {
            jahr = Integer.parseInt(sJahr);
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException("jahr");
        }

        if (jahr < 0) {
            throw new NumberFormatException("jahr");
        }
        if (jahr > 9999) {
            throw new NumberFormatException("jahr");
        }
        if (jahr < 10) {
            jahr += decade;
        }
        if (jahr < 100) {
            jahr += century;
        }
        if (monat < 1 || monat > 12) {
            throw new NumberFormatException("monat");
        }

        if (tag < 1 || tag > eom(new GregorianCalendar(jahr, monat - 1, 1))) {
            throw new NumberFormatException("monat");
        }

        return new GregorianCalendar(jahr, monat - 1, tag, 0, 0, 0);
    }

    /**
     * gibt das heutige Datum zurück, allerdings um die Uhrzeitanteile bereinigt.
     *
     * @return das ein um die Uhrzeit bereinigtes Datum.
     */
    public static GregorianCalendar today() {
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(GregorianCalendar.HOUR, 0);
        gc.set(GregorianCalendar.HOUR_OF_DAY, 0);
        gc.set(GregorianCalendar.MINUTE, 0);
        gc.set(GregorianCalendar.SECOND, 0);
        gc.set(GregorianCalendar.MILLISECOND, 0);
        return gc;
    }

    public static long now() {
        return java.lang.System.currentTimeMillis();
        //return heute().getTimeInMillis();
    }

    /**
     * Gibt den aktuellen Zeitstempel der Datenbank zurück, damit man von evtl. Uhr-Abweichungen
     * des lokalen Maschine unabhängig ist.
     *
     * @return Zeitstempel
     */
    public static long nowDB() {
        String sql = "SELECT now()";
        long now = 0;
        try {
            Statement stmt = OPDE.getDb().db.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            rs.first();
            now = rs.getTimestamp(1).getTime();
        } catch (SQLException ex) {
            new DlgException(ex);
        }
        return now;
    }

    /**
     * Gibt die aktuelle Zeit als Date zurück. Es wird die Zeit der Datenbank verwendet.
     *
     * @return Zeitstempel
     */
    public static Date nowDBDate() {
        return new Date(nowDB());
    }

    /**
     * erkennt Uhrzeitn im Format HH:MM[:SS]
     */
    public static GregorianCalendar erkenneUhrzeit(String input) throws NumberFormatException {
        return erkenneUhrzeit(input, new GregorianCalendar());
    }

    /**
     * erkennt Uhrzeiten im Format HH:MM und erstellt einen GregorianCalendar basierend auf ref
     */
    public static GregorianCalendar erkenneUhrzeit(String input, GregorianCalendar gc)
            throws NumberFormatException {
        if (input == null || input.equals("")) {
            throw new NumberFormatException("leere Eingabe");
        }
        StringTokenizer st = new StringTokenizer(input, ":,.");
        if (st.countTokens() < 2 || st.countTokens() > 3) {
            throw new NumberFormatException("falsches Format");
        }
        String sStunde = st.nextToken();
        String sMinute = st.nextToken();
        String sSekunde = "00";

        if (st.countTokens() == 1) { // Noch genau einer übrig, kann nur Sekunde sein.
            sSekunde = st.nextToken();
        }

        int stunde, minute, sekunde;
        GregorianCalendar now = (GregorianCalendar) gc.clone();

        try {
            stunde = Integer.parseInt(sStunde);
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException("stunde");
        }
        try {
            minute = Integer.parseInt(sMinute);
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException("minute");
        }
        try {
            sekunde = Integer.parseInt(sSekunde);
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException("Sekunde");
        }

        if (stunde < 0) {
            throw new NumberFormatException("stunde");
        }
        if (stunde > 23) {
            throw new NumberFormatException("stunde");
        }
        if (minute < 0 || minute > 59) {
            throw new NumberFormatException("minute");
        }
        if (sekunde < 0 || sekunde > 59) {
            throw new NumberFormatException("Sekunde");
        }

        now.set(GregorianCalendar.HOUR_OF_DAY, stunde);
        now.set(GregorianCalendar.MINUTE, minute);
        now.set(GregorianCalendar.SECOND, sekunde);
        return now;
    }

    /**
     * @param ts Ist ein TS mit der Uhrzeit, für die wir wissen möchten in welchem Bereich der Medikamenten Vergabe
     *           sie liegt. Früh morgens, morgens... etc. Das Datum in diesem TS ist egal. Es geht NUR um die Uhrzeit.
     * @return Zeit-Konstante gemäß den Angaben in SYSConst.FM ... SYSConst.NA.
     */
    public static int ermittleZeit(long ts) {
        int zeit;
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(ts);
        long fm = erkenneUhrzeit(OPDE.getProps().getProperty("FM"), gc).getTimeInMillis();
        long mo = erkenneUhrzeit(OPDE.getProps().getProperty("MO"), gc).getTimeInMillis();
        long mi = erkenneUhrzeit(OPDE.getProps().getProperty("MI"), gc).getTimeInMillis();
        long nm = erkenneUhrzeit(OPDE.getProps().getProperty("NM"), gc).getTimeInMillis();
        long ab = erkenneUhrzeit(OPDE.getProps().getProperty("AB"), gc).getTimeInMillis();
        long na = erkenneUhrzeit(OPDE.getProps().getProperty("NA"), gc).getTimeInMillis();
        if (fm <= ts && ts < mo) {
            zeit = SYSConst.FM;
        } else if (mo <= ts && ts < mi) {
            zeit = SYSConst.MO;
        } else if (mi <= ts && ts < nm) {
            zeit = SYSConst.MI;
        } else if (nm <= ts && ts < ab) {
            zeit = SYSConst.NM;
        } else if (ab <= ts && ts < na) {
            zeit = SYSConst.AB;
        } else {
            zeit = SYSConst.NA;
        }
        return zeit;
    }

    public static int ermittleZeit() {
        return ermittleZeit(nowDB());
    }

    public static int ermittleSchicht() {
        return ermittleSchicht(nowDB());
    }

    /**
     * Ermittelt zu einer gegebenen Zeit die entsprechende Schicht.
     *
     * @param ts - Die Uhrzeit in Millis.
     * @return - Antwort als int entsprechend der SYSConst Konstanten zu den Schichten.
     */
    public static int ermittleSchicht(long ts) {
        int zeit = ermittleZeit(ts);
        int schicht;
        if (SYSConst.FM <= zeit && zeit < SYSConst.MO) {
            schicht = SYSConst.ZEIT_NACHT_MO;
        } else if (SYSConst.MO <= zeit && zeit < SYSConst.NM) {
            schicht = SYSConst.ZEIT_FRUEH;
        } else if (SYSConst.NM <= zeit && zeit < SYSConst.NA) {
            schicht = SYSConst.ZEIT_SPAET;
        } else {
            schicht = SYSConst.ZEIT_NACHT_AB;
        }
        return schicht;
    }

    public static int ermittleSchicht(Date date) {
        return ermittleSchicht(date.getTime());
    }

    public static String getHTMLColor4Schicht(int schicht) {
        String color = "";
        switch (schicht) {
            case SYSConst.ZEIT_FRUEH: {
                color = "color=\"blue\"";
                break;
            }
            case SYSConst.ZEIT_SPAET: {
                color = SYSConst.html_darkgreen;
                break;
            }
            case SYSConst.ZEIT_NACHT_AB: {
                color = SYSConst.html_darkred;
                break;
            }
            case SYSConst.ZEIT_NACHT_MO: {
                color = SYSConst.html_darkred;
                break;
            }
            default: {
                color = "color=\"black\"";
                break;
            }
        }
        return color;
    }

    /**
     *
     *
     **/
    public static ArrayList getZeiten4Schicht(int schicht) {
        ArrayList result = new ArrayList(4);
        String z1, z2;
        int s1, s2;
        switch (schicht) {
            case SYSConst.ZEIT_NACHT_MO: {
                z1 = "FM";
                z2 = "MO";
                s1 = SYSConst.FM;
                s2 = SYSConst.FM;
                break;
            }
            case SYSConst.ZEIT_FRUEH: {
                z1 = "MO";
                z2 = "NM";
                s1 = SYSConst.MO;
                s2 = SYSConst.MI;
                break;
            }
            case SYSConst.ZEIT_SPAET: {
                z1 = "NM";
                z2 = "NA";
                s1 = SYSConst.NM;
                s2 = SYSConst.AB;
                break;
            }
            case SYSConst.ZEIT_NACHT_AB: {
                z1 = "NA";
                z2 = "FM";
                s1 = SYSConst.NA;
                s2 = Integer.MAX_VALUE; // kleiner Trick :-$
                break;
            }
            default: {
                z1 = "";
                z2 = "";
                s1 = -1;
                s2 = -1;
            }
        }

        result.add(s1);
        result.add(s2);
        result.add(OPDE.getProps().getProperty(z1));
        GregorianCalendar gc = SYSCalendar.erkenneUhrzeit(OPDE.getProps().getProperty(z2));
        gc.add(GregorianCalendar.MINUTE, -1);
        result.add(SYSCalendar.toGermanTime(gc));
        return result;
    }

    /**
     * EndOfMonth
     * Berechnet das Enddatum eines Monats, passend zum Parameter
     *
     * @param d Datum innerhalb des entsprechenden Monats.
     * @return Enddatum des Monats
     */
    public static Date eom(Date d) {
        GregorianCalendar gc = toGC(d);
        int ieom = eom(gc);
        gc.set(GregorianCalendar.DATE, ieom);
        return new Date(endOfDay(new Date(gc.getTimeInMillis())));
    }

    public static Date bom(Date d) {
        GregorianCalendar gc = toGC(d);
        gc.set(GregorianCalendar.DATE, 1);
        return new Date(startOfDay(new Date(gc.getTimeInMillis())));
    }

    public static int eom(GregorianCalendar d) {
        return d.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
//        switch (d.get(GregorianCalendar.MONTH)) {
//            case GregorianCalendar.JANUARY: {
//                return 31;
//            }
//            case GregorianCalendar.FEBRUARY: {
//                if (d.isLeapYear(d.get(GregorianCalendar.YEAR))) {
//                    return 29;
//                } else {
//                    return 28;
//                }
//            }
//            case GregorianCalendar.MARCH: {
//                return 31;
//            }
//            case GregorianCalendar.APRIL: {
//                return 30;
//            }
//            case GregorianCalendar.MAY: {
//                return 31;
//            }
//            case GregorianCalendar.JUNE: {
//                return 30;
//            }
//            case GregorianCalendar.JULY: {
//                return 31;
//            }
//            case GregorianCalendar.AUGUST: {
//                return 31;
//            }
//            case GregorianCalendar.SEPTEMBER: {
//                return 30;
//            }
//            case GregorianCalendar.OCTOBER: {
//                return 31;
//            }
//            case GregorianCalendar.NOVEMBER: {
//                return 30;
//            }
//            case GregorianCalendar.DECEMBER: {
//                return 31;
//            }
//
//            default: {
//                return 0;
//            }
//        }
    } // eom

    /**
     * Entscheidet ob zwei Daten in der selben Woche liegen. Berücksichtigt dabei die Jahreszahlen, so dass KW 34/2006 != KW 34/2005.
     */
    public static int sameWeek(GregorianCalendar a, GregorianCalendar b) {
        NumberFormat form = new java.text.DecimalFormat("00");

        int aYear = a.get(GregorianCalendar.YEAR);
        int bYear = b.get(GregorianCalendar.YEAR);

        // Die KW der letzten Tage eines Jahres gehören manchmal schon zum neuen Jahr. So gehörte der 29.12.2008 schon zur KW1 von 2009.
        // Diese Zeilen berücksichtigen das. Sie erhöhen die Jahreszahl wenn der Monat Dezember ist und die KW trotzdem 1.
        // Bug 0000016: Fehlende Einträge in der Behandlungspflege
        if (a.get(GregorianCalendar.WEEK_OF_YEAR) == 1 && a.get(GregorianCalendar.MONTH) == GregorianCalendar.DECEMBER) {
            aYear++;
        }
        if (b.get(GregorianCalendar.WEEK_OF_YEAR) == 1 && b.get(GregorianCalendar.MONTH) == GregorianCalendar.DECEMBER) {
            bYear++;
        }

        String sa = Integer.toString(aYear) + form.format(a.get(GregorianCalendar.WEEK_OF_YEAR));
        String sb = Integer.toString(bYear) + form.format(b.get(GregorianCalendar.WEEK_OF_YEAR));
        int ia = Integer.parseInt(sa);
        int ib = Integer.parseInt(sb);

        if (ia > ib) {
            return 1;
        }
        if (ia < ib) {
            return -1;
        }
        return 0;
    }

    /**
     * Entscheidet ob zwei Daten im selben Monat liegen. Berücksichtigt dabei die Jahreszahlen, so dass Monat 12/2006 != Monat 12/2005.
     */
    public static int sameMonth(GregorianCalendar a, GregorianCalendar b) {
        NumberFormat form = new java.text.DecimalFormat("00");
        String sa = Integer.toString(a.get(GregorianCalendar.YEAR)) + form.format(a.get(GregorianCalendar.MONTH));
        String sb = Integer.toString(b.get(GregorianCalendar.YEAR)) + form.format(b.get(GregorianCalendar.MONTH));
        int ia = Integer.parseInt(sa);
        int ib = Integer.parseInt(sb);

        if (ia > ib) {
            return 1;
        }
        if (ia < ib) {
            return -1;
        }
        return 0;
    }

    /**
     * Entscheidet ob zwei Daten am selben Tag liegen.
     *
     * @param a Datum a
     * @param b Datum b
     * @return 0, wenn a und b am selben Tag liegen. -1, wenn a <b>vor</b> b ist. und +1 wenn a <b>nach</b> b ist.
     */
    public static int sameDay(GregorianCalendar a, GregorianCalendar b) {
        NumberFormat form = new java.text.DecimalFormat("00");
        String sa = Integer.toString(a.get(GregorianCalendar.YEAR)) + form.format(a.get(GregorianCalendar.MONTH)) + form.format(a.get(GregorianCalendar.DATE));
        String sb = Integer.toString(b.get(GregorianCalendar.YEAR)) + form.format(b.get(GregorianCalendar.MONTH)) + form.format(b.get(GregorianCalendar.DATE));
        int ia = Integer.parseInt(sa);
        int ib = Integer.parseInt(sb);

        if (ia > ib) {
            return 1;
        }
        if (ia < ib) {
            return -1;
        }
        return 0;
    }

    /**
     * Entscheidet ob zwei Daten am selben Tag liegen.
     *
     * @param a Datum a
     * @param b Datum b
     * @return 0, wenn a und b am selben Tag liegen. -1, wenn a <b>vor</b> b ist. und +1 wenn a <b>nach</b> b ist.
     */
    public static int sameDay(Date a, Date b) {
        return sameDay(toGC(a), toGC(b));
    }

    public static int sameDay(long a, long b) {
        return sameDay(toGC(a), toGC(b));
    }

    public static int calculateAge(GregorianCalendar a, GregorianCalendar b) {
        return getDaysBetween(a, b) / 365;
    }

    public static int calculateAge(GregorianCalendar a) {
        return getDaysBetween(a, new GregorianCalendar()) / 365;
    }

    public static void checkJDC(JDateChooser jdc) {
        if (jdc.getDate() == null) {
            jdc.setDate(new Date());
        } // ungültiges Datum
        if (jdc.getMaxSelectableDate().before(jdc.getDate())) {
            jdc.setDate(new Date());
        }
    }

    public static boolean isJDCValid(JDateChooser jdc) {
        boolean valid = false;
        boolean dateTrouble = (jdc.getDate() == null);
        return !dateTrouble && isInRange(jdc);
    }

    /**
     * Addiert (bzw. Subtrahiert) eine angebenene Anzahl von Tagen auf das (bzw. von dem)
     * übergebenen Datum und gibt es dann zurück.
     *
     * @param date    - Ausgangsdatum
     * @param numDays - Anzahl der Tage die addiert bzw. subtrahiert werdenn sollen.
     * @return neues Datum
     */
    public static Date addDate(Date date, int numDays) {
        GregorianCalendar gc = toGC(date);
        gc.add(GregorianCalendar.DATE, numDays);
        return new Date(gc.getTimeInMillis());
    }

    // Das hier ist noch nicht ganz richtig. Vertut sich an den Rändern schon mal.
    public static Date addDate(Date date, int amount, Date min, Date max) {
        GregorianCalendar gc = toGC(date);
        gc.add(GregorianCalendar.DATE, amount);
        Date result = new Date(gc.getTimeInMillis());
        if (!(trimTime(min).before(trimTime(result)) && trimTime(max).after(trimTime(result)))) {
            result = date;
        }
        return result;
    }

    public static Date addField(Date date, int amount, int field) {
        GregorianCalendar gc = toGC(date);
        gc.add(field, amount);
        return new Date(gc.getTimeInMillis());
    }

    public static Date min(Date d1, Date d2) {
        Date result;
        if (d1.before(d2)) {
            result = d1;
        } else {
            result = d2;
        }
        return result;
    }

    public static Date max(Date d1, Date d2) {
        Date result;
        if (d1.before(d2)) {
            result = d2;
        } else {
            result = d1;
        }
        return result;
    }

    /*
     * Erstellt ein ComboBox Modell, das eine Liste von Monaten mit Jahreszahlen enthält.
     * "Januar 2007, Februar 2007, ...."
     *
     * @param start Beginn der Liste
     * @param end Ende der Liste
     */
    public static DefaultComboBoxModel createMonthList(Date start, Date end) {
        start = bom(start);
        end = bom(end);
        DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
        if (start.compareTo(end) > 0) {
            dcbm = null;
        } else {
            Date runner = start;
            do {
                Format formatter;
                formatter = new SimpleDateFormat("MMMM yyyy");
                dcbm.addElement(new ListElement(formatter.format(runner), runner.clone()));
                //System.out.println(printGermanStyle(runner) + ", " + runner.compareTo(end) + ", " + formatter.format(runner));
                runner = addField(runner, 1, GregorianCalendar.MONTH);
            } while (runner.compareTo(end) <= 0);

        }
        return dcbm;
    }

    /**
     * gibt das heutige Datum zurück, allerdings um die Uhrzeitanteile bereinigt.
     *
     * @return das ein um die Uhrzeit bereinigtes Datum.
     */
    public static GregorianCalendar trimTime(GregorianCalendar gc) {
        gc.set(GregorianCalendar.HOUR, 0);
        gc.set(GregorianCalendar.MINUTE, 0);
        gc.set(GregorianCalendar.SECOND, 0);
        gc.set(GregorianCalendar.MILLISECOND, 0);
        return gc;
    }

    /**
     * Bereinigt ein Datum um die Uhrzeitanteile, damit die Vergleichsoperatoren richtig funktionieren, wenn man sich nur auf das
     * Datum beschränken will.
     *
     * @param Das zu bereinigende Datum
     * @return das bereinigte Datum
     */
    public static Date trimTime(Date date) {
        return new Date(trimTime(toGC(date)).getTimeInMillis());
    }

    public static boolean betweenDisjunctive(Date from, Date to, Date date) {
        return date.getTime() > from.getTime() && date.getTime() < to.getTime();
    }

    public static boolean betweenOverlap(Date from, Date to, Date date) {
        return betweenDisjunctive(from, to, date) || date.getTime() == from.getTime() || date.getTime() == to.getTime();
    }

    public static boolean betweenDisjunctive(Date fromInt, Date toInt, Date from, Date to) {
        return betweenDisjunctive(fromInt, toInt, from) && betweenDisjunctive(fromInt, toInt, to);
    }

    public static boolean betweenOverlap(Date fromInt, Date toInt, Date from, Date to) {
        return betweenOverlap(fromInt, toInt, from) && betweenOverlap(fromInt, toInt, to);
    }

    public static boolean isInRange(JDateChooser jdc) {
        if (jdc.getDate() != null) {
            return betweenOverlap(jdc.getMinSelectableDate(), jdc.getMaxSelectableDate(), jdc.getDate());
        }
        return false;
    }

    public static ArrayList getUhrzeitListe(GregorianCalendar start, GregorianCalendar stop, int minutes) {
        ArrayList result = new ArrayList();
        while (start.before(stop)) {
            result.add(toGermanTime(start));
            start.add(GregorianCalendar.MINUTE, minutes);
        }
        return result;
    }


    public static Date addTime2Date(Date date, Time time) {
        return new Date(addTime2Date(toGC(date), toGC(time)).getTimeInMillis());
    }

    /**
     * Setzt die Zeitkomponente eines GregorianCalendars. Das heisst ganz einfach,
     * wenn man ein Datum hat (in einem GC) und in einem anderen GC steht die Uhrzeit, dann
     * fügt diese Methode die beiden Komponenten zu einem Datum und Uhrzeit Wert zusammen
     * und gibt diesen zurück.
     *
     * @param date - Datumsanteil
     * @param time - Uhrzeitanzeil
     * @return Datum und Uhrzeit kombiniert.
     */
    public static GregorianCalendar addTime2Date(GregorianCalendar date, GregorianCalendar time) {
        date.set(GregorianCalendar.HOUR_OF_DAY, time.get(GregorianCalendar.HOUR_OF_DAY));
        date.set(GregorianCalendar.MINUTE, time.get(GregorianCalendar.MINUTE));
        date.set(GregorianCalendar.SECOND, time.get(GregorianCalendar.SECOND));
        date.set(GregorianCalendar.MILLISECOND, time.get(GregorianCalendar.MILLISECOND));
        return (GregorianCalendar) date.clone();
    }

    public static GregorianCalendar setDate2Time(GregorianCalendar source, GregorianCalendar target) {
        target.set(GregorianCalendar.DATE, source.get(GregorianCalendar.DATE));
        target.set(GregorianCalendar.MONTH, source.get(GregorianCalendar.MONTH));
        target.set(GregorianCalendar.YEAR, source.get(GregorianCalendar.YEAR));
        return (GregorianCalendar) target.clone();
    }

    /**
     * Diese Routine vergleicht Uhrzeiten, die in zwei longs hinterlegt sind.
     * Das Besondere dabei ist, dass das Datum ausser acht gelassen wird.
     *
     * @return int < 0, wenn time1 < time2; int == 0, wenn time1 = time2; int > 0, wenn time1 > time2
     * @time1
     * @time2
     */
    public static int compareTime(long time1, long time2) {
        // normalisierung des timestamps
        GregorianCalendar gc1 = new GregorianCalendar();
        gc1.setTimeInMillis(time1);
        GregorianCalendar gc2 = new GregorianCalendar();
        gc2.setTimeInMillis(time2);
        gc2 = setDate2Time(gc1, gc2); // Hier werden die Daten gleichgesetzt.
        return gc1.compareTo(gc2);
    }

    public static long startOfDay() {
        return startOfDay(new Date());
    }

    public static long midOfDay() {
        return midOfDay(new Date());
    }

    public static long endOfDay() {
        return endOfDay(new Date());
    }

    /**
     * nimmt das übergebene Datum und Zeit die Uhrzeitkomponente auf 00:00:00
     *
     * @param d
     * @return
     */
    public static long startOfDay(Date d) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(d.getTime());
        gc.set(GregorianCalendar.HOUR_OF_DAY, 0);
        gc.set(GregorianCalendar.MINUTE, 0);
        gc.set(GregorianCalendar.SECOND, 0);
        gc.set(GregorianCalendar.MILLISECOND, 0);
        return gc.getTimeInMillis();
    }

    /**
     * nimmt das übergebene Datum und Zeit die Uhrzeitkomponente auf 12:00:00
     *
     * @param d
     * @return
     */
    public static long midOfDay(Date d) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(d.getTime());
        gc.set(GregorianCalendar.HOUR_OF_DAY, 12);
        gc.set(GregorianCalendar.MINUTE, 0);
        gc.set(GregorianCalendar.SECOND, 0);
        gc.set(GregorianCalendar.MILLISECOND, 0);
        return gc.getTimeInMillis();
    }

    /**
     * nimmt das übergebene Datum und Zeit die Uhrzeitkomponente auf 23:59:59
     *
     * @param d
     * @return
     */
    public static long endOfDay(Date d) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(d.getTime());
        gc.set(GregorianCalendar.HOUR_OF_DAY, 23);
        gc.set(GregorianCalendar.MINUTE, 59);
        gc.set(GregorianCalendar.SECOND, 59);
        gc.set(GregorianCalendar.MILLISECOND, 0);
        return gc.getTimeInMillis();
    }

    /**
     * Berechnet zu einem gegebenen Jahr den Ostersonntag. Dieser wird als GregorianCalendar zurückgegeben.
     * Der Algorhythmus wurde von der Internet-Seite www.th-o.de/kalender.htm entnommen.
     * Dort wurde er von Walter Irion beschrieben. Danke, Walter und Thomas.
     * <p/>
     * Ich habe leider nicht die geringste Ahnung, was hier passiert. ;-)
     *
     * @param year
     * @return Das Datum des Ostersonntags in dem angegebene Jahr.
     * @THO99
     */
    public static GregorianCalendar Ostersonntag(int year) {
        int a, b, c, d, e, f, g, h, i, k, l, m, n, p;

        a = year % 19;

        b = year / 100;
        c = year % 100;

        d = b / 4;
        e = b % 4;

        f = (b + 8) / 25;

        g = (b - f + 1) / 3;

        h = (19 * a + b - d - g + 15) % 30;

        i = c / 4;
        k = c % 4;

        l = (32 + 2 * e + 2 * i - h - k) % 7;

        m = (a + 11 * h + 22 * l) / 451;

        n = (h + l - 7 * m + 114) / 31;
        p = (h + l - 7 * m + 114) % 31;

        return new GregorianCalendar(year, n - 1, p + 1);
    }

    public static GregorianCalendar Aschermittwoch(int year) {
        GregorianCalendar gc = Ostersonntag(year);
        gc.add(GregorianCalendar.DAY_OF_MONTH, -46);
        return gc;
    }

    public static GregorianCalendar Rosenmontag(int year) {
        GregorianCalendar gc = Ostersonntag(year);
        gc.add(GregorianCalendar.DAY_OF_MONTH, -48);
        return gc;
    }

    public static GregorianCalendar Weiberfastnacht(int year) {
        GregorianCalendar gc = Ostersonntag(year);
        gc.add(GregorianCalendar.DAY_OF_MONTH, -52);
        return gc;
    }

    public static GregorianCalendar Ostermontag(int year) {
        GregorianCalendar gc = Ostersonntag(year);
        gc.add(GregorianCalendar.DAY_OF_MONTH, 1);
        return gc;
    }

    public static GregorianCalendar Karfreitag(int year) {
        GregorianCalendar gc = Ostersonntag(year);
        gc.add(GregorianCalendar.DAY_OF_MONTH, -2);
        return gc;
    }

    public static GregorianCalendar Pfingstsonntag(int year) {
        GregorianCalendar gc = Ostersonntag(year);
        gc.add(GregorianCalendar.DAY_OF_MONTH, 49);
        return gc;
    }

    public static GregorianCalendar Pfingstmontag(int year) {
        GregorianCalendar gc = Ostersonntag(year);
        gc.add(GregorianCalendar.DAY_OF_MONTH, 50);
        return gc;
    }

    public static GregorianCalendar ChristiHimmelfahrt(int year) {
        GregorianCalendar gc = Ostersonntag(year);
        gc.add(GregorianCalendar.DAY_OF_MONTH, 39);
        return gc;
    }

    public static GregorianCalendar Fronleichnam(int year) {
        GregorianCalendar gc = Ostersonntag(year);
        gc.add(GregorianCalendar.DAY_OF_MONTH, 60);
        return gc;
    }

    public static String toAnsi(GregorianCalendar gc) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date(gc.getTimeInMillis());
        return sdf.format(d);
    }

    /**
     * Sucht alle Feiertage in einem Jahr zusammen.
     *
     * @param year
     * @return Eine Hashmap, die je das Datum als Zeichenkette der Form "jjjj-mm-tt" enthält und dazu die Bezeichnung des Feiertags.
     */
    public static HashMap getFeiertage(int year) {

        HashMap hm = new HashMap();

        // Feste Feiertage
        hm.put(year + "-01-01", "Neujahrstag");
        hm.put(year + "-05-01", "Maifeiertag");
        hm.put(year + "-10-03", "Tag der Einheit");
        hm.put(year + "-11-01", "Allerheiligen");
        hm.put(year + "-12-25", "1. Weihnachtstag");
        hm.put(year + "-12-26", "2. Weihnachtstag");

        // Bewegliche Feiertage        
        hm.put(toAnsi(Karfreitag(year)), "Karfreitag");
        hm.put(toAnsi(Ostersonntag(year)), "Ostersonntag");
        hm.put(toAnsi(Ostermontag(year)), "Ostermontag");
        hm.put(toAnsi(ChristiHimmelfahrt(year)), "Christi Himmelfahrt");
        hm.put(toAnsi(Pfingstsonntag(year)), "Pfingstsonntag");
        hm.put(toAnsi(Pfingstmontag(year)), "Pfingstmontag");
        hm.put(toAnsi(Fronleichnam(year)), "Fronleichnam");

        return hm;
    }
}
