/*
 * OffenePflege
 * Copyright (C) 2006-2012 Torsten Löhr
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
import entity.nursingprocess.DFNTools;
import entity.prescription.BHPTools;
import op.OPDE;
import op.threads.DisplayMessage;
import org.apache.commons.collections.Closure;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class SYSCalendar {


    public static GregorianCalendar heute() {
        return new GregorianCalendar();
    }


    public static boolean isInFuture(long time) {
        return isInFuture(new Date(time));
    }

    public static boolean isInFuture(Date date) {
        return date.after(new Date());
    }


    /**
     * Generiert ein Array aus Uhrzeiten in der PrinterForm {"17:00","17:15"...}
     * Der verwendete Datentyp ist GregorianCalendar
     */
    public static ArrayList<Date> getTimeList() {
        ArrayList list = new ArrayList();
        GregorianCalendar gc = today();
        for (int i = 1; i <= 96; i++) {
            list.add(new Date(gc.getTimeInMillis()));
            gc.add(GregorianCalendar.MINUTE, 15);
        }
        return list;
    }


    public static ListCellRenderer getTimeRenderer() {
        return new ListCellRenderer() {
            DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);

            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                String text;
                if (o == null) {
                    text = SYSTools.toHTML("<i>Keine Auswahl</i>");
                } else if (o instanceof Date) {
                    Date date = (Date) o;
                    text = timeFormat.format(date) + " Uhr";
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
            }
        };
    }


    public static GregorianCalendar toGC(Date d) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(d.getTime());
        return gc;
    }


    public static String toGermanTime(GregorianCalendar gc) {
        Date date = new Date(gc.getTimeInMillis());
        Format formatter;

        formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(date);
    }


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
    }


    /**
     * A "sane" birthday is not older than 120 years and not younger than 15 years.
     *
     * @param date
     * @return
     */
    public static boolean isBirthdaySane(Date date) {
        if (date == null) return false;
        //TODO: those min and max values must not be hardcoded in future
        int maxage = 120;
        int minage = 15;
        LocalDate min = new LocalDate().minusYears(minage);
        LocalDate max = new LocalDate().minusYears(maxage);
        LocalDate d = new LocalDate(date);

        return d.isAfter(max) && d.isBefore(min);
    }

    public static void handleDateFocusLost(FocusEvent evt, LocalDate min, LocalDate max) {
        LocalDate dt;
        if (max == null) {
            max = new LocalDate();
        }
        try {
            dt = new LocalDate(parseDate(((JTextField) evt.getSource()).getText()));
        } catch (NumberFormatException ex) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wrongdate")));
            dt = new LocalDate();
        }
        if (dt.isAfter(max)) {
            dt = new LocalDate();
            DisplayMessage dm = new DisplayMessage(dt.isAfter(new LocalDate()) ? OPDE.lang.getString("misc.msg.futuredate") : OPDE.lang.getString("misc.msg.wrongdate"));
            OPDE.getDisplayManager().addSubMessage(dm);
        }
        if (dt.isBefore(min)) {
            dt = new LocalDate();
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.DateTooOld")));
        }

        ((JTextField) evt.getSource()).setText(DateFormat.getDateInstance().format(dt.toDate()));
    }

    /**
     * Expiry dates usually have a form like "12-10" oder "12/10" to indicate that the product in question is
     * best before December 31st, 2010. This method parses dates like this.
     * If it fails it hands over the parsing efforts to <code>public static Date parseDate(String input)</code>.
     *
     * @param input a string to be parsed. It can handle the following formats "mm/yy", "mm/yyyy" (it also recognizes these kinds of
     *              dates if the slash is replaced with one of the following chars: "-,.".
     * @return the parsed date which is always the last day and the last second of that month.
     * @throws NumberFormatException
     */
    public static DateTime parseExpiryDate(String input) throws NumberFormatException {
        if (input == null || input.isEmpty()) {
            throw new NumberFormatException("empty");
        }
        input = input.trim();
        if (input.indexOf(".") + input.indexOf(",") + input.indexOf("-") + input.indexOf("/") == -4) {
            input += ".";
        }

        StringTokenizer st = new StringTokenizer(input, "/,.-");
        if (st.countTokens() == 1) { // only one number, then this must be the month. we add the current year.
            input = "1." + input + SYSCalendar.today().get(GregorianCalendar.YEAR);
        }
        if (st.countTokens() == 2) { // complete expiry date. we fill up some dummy day.
            input = "1." + input;
            //st = new StringTokenizer(input, "/,.-"); // split again...
        }
        DateTime expiry = new DateTime(parseDate(input));

        // when the user has entered a complete date, then we return that date
        // if he has omitted some parts of it, we consider it always the last day of that month.
        return st.countTokens() == 3 ? expiry : expiry.dayOfMonth().withMaximumValue().secondOfDay().withMaximumValue();

    }

    public static Date parseDate(String input) throws NumberFormatException {
        if (input == null || input.equals("")) {
            throw new NumberFormatException("empty");
        }
        if (input.indexOf(".") + input.indexOf(",") + input.indexOf("-") + input.indexOf("/") == -4) {
            input += "."; // er war zu faul auch nur einen punkt anzuhängen.
        }
        StringTokenizer st = new StringTokenizer(input, "/,.-");
        if (st.countTokens() == 1) { // Vielleicht fehlen ja nur die Monats- und Jahresangaben. Dann hängen wir sie einach an.
            input += (SYSCalendar.today().get(GregorianCalendar.MONTH) + 1) + "." + SYSCalendar.today().get(GregorianCalendar.YEAR);
            st = new StringTokenizer(input, "/,.-"); // dann nochmal aufteilen...
        }
        if (st.countTokens() == 2) { // Vielleicht fehlt ja nur die Jahresangabe. Dann hängen wir es einfach an.

            if (!input.trim().substring(input.length() - 1).equals(".") && !input.trim().substring(input.length() - 1).equals(",")) {
                input += "."; // er war zu faul den letzten Punkt anzuhängen.
            }
            input += SYSCalendar.today().get(GregorianCalendar.YEAR);
            st = new StringTokenizer(input, "/,.-"); // dann nochmal aufteilen...
        }
        if (st.countTokens() != 3) {
            throw new NumberFormatException("wrong format");
        }
        String sTag = st.nextToken();
        String sMonat = st.nextToken();
        String sJahr = st.nextToken();
        int tag, monat, jahr;

        // Year 2010 Problem
        GregorianCalendar now = new GregorianCalendar();
        int decade = (now.get(GregorianCalendar.YEAR) / 10) * 10;
        int century = (now.get(GregorianCalendar.YEAR) / 100) * 100;

        try {
            tag = Integer.parseInt(sTag);
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException("day");
        }
        try {
            monat = Integer.parseInt(sMonat);
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException("month");
        }
        try {
            jahr = Integer.parseInt(sJahr);
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException("year");
        }

        if (jahr < 0) {
            throw new NumberFormatException("year");
        }
        if (jahr > 9999) {
            throw new NumberFormatException("year");
        }
        if (jahr < 10) {
            jahr += decade;
        }
        if (jahr < 100) {
            jahr += century;
        }
        if (monat < 1 || monat > 12) {
            throw new NumberFormatException("month");
        }

        if (tag < 1 || tag > eom(new LocalDate(jahr, monat, tag)).getDayOfMonth()) {
            throw new NumberFormatException("month");
        }

        return new LocalDate(jahr, monat, tag).toDate();
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
     * erkennt Uhrzeitn im Format HH:MM[:SS]
     */
    public static GregorianCalendar parseTime(String input) throws NumberFormatException {
        return parseTime(input, new GregorianCalendar());
    }

    /**
     * erkennt Uhrzeiten im Format HH:MM und erstellt einen GregorianCalendar basierend auf ref
     */
    public static GregorianCalendar parseTime(String input, GregorianCalendar gc)
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
    public static byte ermittleZeit(long ts) {
        byte zeit;
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(ts);
        long fm = parseTime(OPDE.getProps().getProperty("FM"), gc).getTimeInMillis();
        long mo = parseTime(OPDE.getProps().getProperty("MO"), gc).getTimeInMillis();
        long mi = parseTime(OPDE.getProps().getProperty("MI"), gc).getTimeInMillis();
        long nm = parseTime(OPDE.getProps().getProperty("NM"), gc).getTimeInMillis();
        long ab = parseTime(OPDE.getProps().getProperty("AB"), gc).getTimeInMillis();
        long na = parseTime(OPDE.getProps().getProperty("NA"), gc).getTimeInMillis();
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

    public static byte ermittleZeit() {
        return ermittleZeit(new Date().getTime());
    }

    public static int ermittleSchicht() {
        return ermittleSchicht(new Date().getTime());
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

    public static String getHTMLColor4Schicht(Byte schicht) {

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
     * @return Liste mit 4 Elementen. Die ersten beiden sind die byte Kennungen der beteiligten Schichten innerhalb der gewünschten
     * Anzeige. Kann sein SYSConst.FM oder SYSConst.MO etc. Die dritte Stelle enthält die Start-Uhrzeit als Zeichenkette (gemäß des
     * Eintrags in der SYSProps Tabelle. In der vierten Stelle steht die End-Uhrzeit, ebenfalls als Zeichenkette (minus 1 Minute). Damit
     * liegt diese Zeit in dem betreffenden Intervall.
     */
    public static ArrayList getZeiten4Schicht(byte schicht) {
        ArrayList result = new ArrayList(4);
        String z1, z2;
        byte s1, s2;
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
                s2 = Byte.MAX_VALUE; // kleiner Trick :-$
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

        result.add(z1.isEmpty() ? "" : OPDE.getProps().getProperty(z1));

        if (!z2.isEmpty()) {
            GregorianCalendar gc = SYSCalendar.parseTime(OPDE.getProps().getProperty(z2));
            gc.add(GregorianCalendar.MINUTE, -1);
            result.add(SYSCalendar.toGermanTime(gc));
        } else {
            result.add("");
        }
        return result;
    }

    public static Pair<Byte, Byte> getTimeIDs4Shift(byte shiftid) {
        byte id1 = -1, id2 = -1;
        switch (shiftid) {
            case DFNTools.SHIFT_VERY_EARLY: {
                id1 = DFNTools.BYTE_EARLY_IN_THE_MORNING;
                id2 = DFNTools.BYTE_EARLY_IN_THE_MORNING;
                break;
            }
            case DFNTools.SHIFT_EARLY: {
                id1 = DFNTools.BYTE_MORNING;
                id2 = DFNTools.BYTE_NOON;
                break;
            }
            case DFNTools.SHIFT_LATE: {
                id1 = DFNTools.BYTE_AFTERNOON;
                id2 = DFNTools.BYTE_LATE_AT_NIGHT;
                break;
            }
            case DFNTools.SHIFT_VERY_LATE: {
                id1 = DFNTools.BYTE_LATE_AT_NIGHT;
                id2 = Byte.MAX_VALUE;
                break;
            }
        }

        return new Pair<Byte, Byte>(id1, id2);
    }

    public static Pair<Date, Date> getTimeOfDay4Shift(byte shiftid) {
        String id1 = "", id2 = "";
        switch (shiftid) {
            case DFNTools.SHIFT_VERY_EARLY: {
                id1 = DFNTools.STRING_EARLY_IN_THE_MORNING;
                id2 = DFNTools.STRING_MORNING;
                break;
            }
            case DFNTools.SHIFT_EARLY: {
                id1 = DFNTools.STRING_MORNING;
                id2 = DFNTools.STRING_AFTERNOON;
                break;
            }
            case DFNTools.SHIFT_LATE: {
                id1 = DFNTools.STRING_AFTERNOON;
                id2 = DFNTools.STRING_LATE_AT_NIGHT;
                break;
            }
            case DFNTools.SHIFT_VERY_LATE: {
                id1 = DFNTools.STRING_LATE_AT_NIGHT;
                id2 = DFNTools.STRING_EARLY_IN_THE_MORNING;
                break;
            }
        }

        DateTimeFormatter parser = DateTimeFormat.forPattern("HH:mm");
        DateTime time1 = parser.parseDateTime(OPDE.getProps().getProperty(id1));
        DateTime time2 = parser.parseDateTime(OPDE.getProps().getProperty(id2)).minusSeconds(1);
        Period period1 = new Period(time1.getHourOfDay(), time1.getMinuteOfHour(), time1.getSecondOfMinute(), time1.getMillisOfSecond());
        Period period2 = new Period(time2.getHourOfDay(), time2.getMinuteOfHour(), time2.getSecondOfMinute(), time2.getMillisOfSecond());

        return new Pair<Date, Date>(new DateMidnight().toDateTime().plus(period1).toDate(), new DateMidnight().toDateTime().plus(period2).toDate());
    }


    public static byte whatShiftIs(byte timeID) {
        byte shift;
        if (DFNTools.BYTE_EARLY_IN_THE_MORNING <= timeID && timeID < DFNTools.BYTE_MORNING) {
            shift = DFNTools.SHIFT_VERY_EARLY;
        } else if (DFNTools.BYTE_MORNING <= timeID && timeID < DFNTools.BYTE_AFTERNOON) {
            shift = DFNTools.SHIFT_EARLY;
        } else if (DFNTools.BYTE_AFTERNOON <= timeID && timeID < DFNTools.BYTE_LATE_AT_NIGHT) {
            shift = DFNTools.SHIFT_LATE;
        } else {
            shift = DFNTools.SHIFT_VERY_LATE;
        }
        return shift;
    }

    public static byte whatShiftIs(Date date) {
        // TODO: not clean enough. there should be one method for DFNs and one method for BHPs. even though they may do the same.
        return whatShiftIs(whatTimeIDIs(date));
    }


    /**
     * determines to which timeofday code a given date object belongs. The settings in SYSProps are taken into account.
     * or in short: it answers a question like "is 0800h early, noon or early in the morning ?"
     *
     * @param date
     * @return timecode
     */
    public static byte whatTimeIDIs(Date date) {
        byte timeid;

        DateTimeFormatter parser = DateTimeFormat.forPattern("HH:mm");

        DateTime early_in_the_morning = parser.parseDateTime(OPDE.getProps().getProperty(DFNTools.STRING_EARLY_IN_THE_MORNING));
        DateTime morning = parser.parseDateTime(OPDE.getProps().getProperty(DFNTools.STRING_MORNING));
        DateTime noon = parser.parseDateTime(OPDE.getProps().getProperty(DFNTools.STRING_NOON));
        DateTime afternoon = parser.parseDateTime(OPDE.getProps().getProperty(DFNTools.STRING_AFTERNOON));
        DateTime evening = parser.parseDateTime(OPDE.getProps().getProperty(DFNTools.STRING_EVENING));
        DateTime late_at_night = parser.parseDateTime(OPDE.getProps().getProperty(DFNTools.STRING_LATE_AT_NIGHT));

        Period period_early_in_the_morning = new Period(early_in_the_morning.getHourOfDay(), early_in_the_morning.getMinuteOfHour(), early_in_the_morning.getSecondOfMinute(), early_in_the_morning.getMillisOfSecond());
        Period period_morning = new Period(morning.getHourOfDay(), morning.getMinuteOfHour(), morning.getSecondOfMinute(), morning.getMillisOfSecond());
        Period period_noon = new Period(noon.getHourOfDay(), noon.getMinuteOfHour(), noon.getSecondOfMinute(), noon.getMillisOfSecond());
        Period period_afternoon = new Period(afternoon.getHourOfDay(), afternoon.getMinuteOfHour(), afternoon.getSecondOfMinute(), afternoon.getMillisOfSecond());
        Period period_evening = new Period(evening.getHourOfDay(), evening.getMinuteOfHour(), evening.getSecondOfMinute(), evening.getMillisOfSecond());
        Period period_late_at_night = new Period(late_at_night.getHourOfDay(), late_at_night.getMinuteOfHour(), late_at_night.getSecondOfMinute(), late_at_night.getMillisOfSecond());

        DateTime ref = new DateTime(date);
        DateTime eitm = new DateMidnight(date).toDateTime().plus(period_early_in_the_morning);
        DateTime m = new DateMidnight(date).toDateTime().plus(period_morning);
        DateTime n = new DateMidnight(date).toDateTime().plus(period_noon);
        DateTime a = new DateMidnight(date).toDateTime().plus(period_afternoon);
        DateTime e = new DateMidnight(date).toDateTime().plus(period_evening);
        DateTime lan = new DateMidnight(date).toDateTime().plus(period_late_at_night);

        if (eitm.compareTo(ref) <= 0 && ref.compareTo(m) < 0) {
            timeid = DFNTools.BYTE_EARLY_IN_THE_MORNING;
        } else if (m.compareTo(ref) <= 0 && ref.compareTo(n) < 0) {
            timeid = DFNTools.BYTE_MORNING;
        } else if (n.compareTo(ref) <= 0 && ref.compareTo(a) < 0) {
            timeid = DFNTools.BYTE_NOON;
        } else if (a.compareTo(ref) <= 0 && ref.compareTo(e) < 0) {
            timeid = DFNTools.BYTE_AFTERNOON;
        } else if (e.compareTo(ref) <= 0 && ref.compareTo(lan) < 0) {
            timeid = DFNTools.BYTE_EVENING;
        } else {
            timeid = DFNTools.BYTE_LATE_AT_NIGHT;
        }
        return timeid;
    }


    /**
     * bottom of month
     *
     * @param d
     * @return
     */
    public static DateTime bom(DateTime d) {
        return d.dayOfMonth().withMinimumValue().hourOfDay().withMinimumValue().minuteOfHour().withMinimumValue().secondOfMinute().withMinimumValue();
    }

    /**
     * end of month
     *
     * @param d
     * @return
     */
    public static DateTime eom(DateTime d) {
        return d.dayOfMonth().withMaximumValue().hourOfDay().withMaximumValue().minuteOfHour().withMaximumValue().secondOfMinute().withMaximumValue();
    }

    /**
     * bottom of week
     *
     * @param d
     * @return
     */
    public static LocalDate bow(LocalDate d) {
        return d.dayOfWeek().withMinimumValue();
    }

    /**
     * end of week
     *
     * @param d
     * @return
     */
    public static LocalDate eow(LocalDate d) {
        return d.dayOfWeek().withMaximumValue();
    }

    /**
     * bottom of month
     *
     * @param d
     * @return
     */
    public static LocalDate bom(LocalDate d) {
        return d.dayOfMonth().withMinimumValue();
    }

    /**
     * end of month
     *
     * @param d
     * @return
     */
    public static LocalDate eom(LocalDate d) {
        return d.dayOfMonth().withMaximumValue();
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

    public static void checkJDC(JDateChooser jdc) {
        if (jdc.getDate() == null) {
            jdc.setDate(new Date());
        } // ungültiges Datum
        if (jdc.getMaxSelectableDate().before(jdc.getDate())) {
            jdc.setDate(new Date());
        }
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
     * Erstellt ein ComboBox Modell, dass eine Liste mit Dates zwischen den Begrenzungs-Zeitpunkten enthält.
     *
     * @param start Beginn der Liste
     * @param end Ende der Liste
     */
    public static DefaultComboBoxModel createMonthList(LocalDate start, LocalDate end) {
        DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
        start = SYSCalendar.bom(start);
        end = SYSCalendar.bom(end);
        for (LocalDate month = start; month.compareTo(end) <= 0; month = month.plusMonths(1)) {
            dcbm.addElement(month);
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


    public static boolean betweenDisjunctive(Date from, Date to, Date date) {
        return date.getTime() > from.getTime() && date.getTime() < to.getTime();
    }

    public static boolean betweenOverlap(Date from, Date to, Date date) {
        return betweenDisjunctive(from, to, date) || date.getTime() == from.getTime() || date.getTime() == to.getTime();
    }


    public static Date addTime2Date(Date date, Date time) {
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

    /**
     * Diese Routine vergleicht Uhrzeiten, die in zwei longs hinterlegt sind.
     * Das Besondere dabei ist, dass das Datum ausser acht gelassen wird.
     *
     * @return int < 0, wenn time1 < time2; int == 0, wenn time1 = time2; int > 0, wenn time1 > time2
     * @time1
     * @time2
     */
    public static int compareTime(Date date1, Date date2) {
        return compareTime(date1.getTime(), date2.getTime());
    }


    /**
     * nimmt das übergebene Datum und setzt die Uhrzeitkomponente auf 23:59:59
     *
     * @param d
     * @return das Ergebnis als TimeInMillis
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
     * nimmt das übergebene Datum und setzt die Uhrzeitkomponente auf 00:00:00.
     *
     * @param d
     * @return das Ergebnis als TimeInMillis
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


    /**
     * Sucht alle Feiertage in einem Jahr zusammen.
     *
     * @return Eine Hashmap, die je das Datum als Zeichenkette der PrinterForm "jjjj-mm-tt" enthält und dazu die Bezeichnung des Feiertags.
     */
    public static HashMap<LocalDate, String> getHolidays(int from, int to) {

        HashMap<LocalDate, String> hm = new HashMap<LocalDate, String>();

        // TODO: i18n
        for (int year = from; year <= to; year++) {
            // Feste Feiertage
            hm.put(new LocalDate(year, 1, 1), "Neujahrstag");
            hm.put(new LocalDate(year, 5, 1), "Maifeiertag");
            hm.put(new LocalDate(year, 10, 3), "Tag der Einheit");
//            hm.put(new LocalDate(year, 06, 05), "Torsten Tag 1");
//            hm.put(new LocalDate(year, 06, 27), "Torsten Tag 2");
            hm.put(new LocalDate(year, 11, 1), "Allerheiligen");
            hm.put(new LocalDate(year, 12, 25), "1. Weihnachtstag");
            hm.put(new LocalDate(year, 12, 26), "2. Weihnachtstag");

            // Bewegliche Feiertage
            hm.put(new LocalDate(Karfreitag(year)), "Karfreitag");
            hm.put(new LocalDate(Ostersonntag(year)), "Ostersonntag");
            hm.put(new LocalDate(Ostermontag(year)), "Ostermontag");
            hm.put(new LocalDate(ChristiHimmelfahrt(year)), "Christi Himmelfahrt");
            hm.put(new LocalDate(Pfingstsonntag(year)), "Pfingstsonntag");
            hm.put(new LocalDate(Pfingstmontag(year)), "Pfingstmontag");
            hm.put(new LocalDate(Fronleichnam(year)), "Fronleichnam");
        }

        return hm;
    }


    public static JPopupMenu getMinutesMenu(int[] mins, final Closure action) {
        JPopupMenu timemenu = new JPopupMenu(OPDE.lang.getString("misc.commands.changeeffort"));

        for (int min : mins) {
            String title = "";
            if (min % 60 == 0) {
                title = min / 60 + " " + OPDE.lang.getString("misc.msg.Hour(s)");
            } else {
                title = min + " " + OPDE.lang.getString("misc.msg.Minute(s)");
            }

            JMenuItem item = new JMenuItem(title);
            final int minutes = min;
            item.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    action.execute(minutes);
                }
            });
            timemenu.add(item);
        }

        return timemenu;
    }

    public static Color getFGSHIFT(Byte shift) {
        if (shift == BHPTools.SHIFT_ON_DEMAND) {
            return GUITools.getColor(OPDE.getProps().getProperty("ON_DEMAND_FGSHIFT"));
        }
        return GUITools.getColor(OPDE.getProps().getProperty(BHPTools.SHIFT_KEY_TEXT[shift] + "_FGSHIFT"));
    }

    public static Color getBGSHIFT(Byte shift) {
        if (shift == BHPTools.SHIFT_ON_DEMAND) {
            return GUITools.getColor(OPDE.getProps().getProperty("ON_DEMAND_BGSHIFT"));
        }
        return GUITools.getColor(OPDE.getProps().getProperty(BHPTools.SHIFT_KEY_TEXT[shift] + "_BGSHIFT"));
    }


    public static BigDecimal getHoursAsDecimal(DateTime from, DateTime to) {
        if (from == null || to == null) return null;
        Period period = new Period(from, to);
        return BigDecimal.valueOf(period.toStandardDuration().getMillis()).divide(BigDecimal.valueOf(DateTimeConstants.MILLIS_PER_HOUR), 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal getHoursAsDecimal(Interval interval) {
        if (interval == null) {
            return BigDecimal.ZERO;
        }
        DateTime from = interval.getStart();
        DateTime to = interval.getEnd();
        return getHoursAsDecimal(from, to);
    }

    /**
     * end of day
     *
     * @param date
     * @return
     */
    public static DateTime eod(DateTime date) {
        return date.hourOfDay().withMaximumValue().minuteOfHour().withMaximumValue().secondOfMinute().withMaximumValue();
    }

    public static DateTime eod(LocalDate date) {
        return eod(date.toDateTimeAtStartOfDay());
    }


    /**
     * calculates the intervals of int1 NOT IN int2
     *
     * @param int1 first interval in the notion in1 NOT IN int2
     * @param int2 second interval in the notion in1 NOT IN int2
     * @return a list of complementary intervals (maximum 2)
     */
    public static Interval[] notin(Interval int1, Interval int2) {
        ArrayList<Interval> listComplement = new ArrayList<Interval>();

        if (int1.gap(int2) != null) {
            listComplement.add(int1);
        } else if (int1.abuts(int2)) {
            listComplement.add(int1);
        } else if (int1.contains(int2)) {
            if (!int1.getStart().equals(int2.getStart())) {
                listComplement.add(new Interval(int1.getStart(), int2.getStart()));
            }
            if (!int1.getEnd().equals(int2.getEnd())) {
                listComplement.add(new Interval(int2.getEnd(), int1.getEnd()));
            }
        } else if (int1.overlaps(int2)) {

            Interval overlap = int1.overlap(int2);

            if (int1.getStart().isBefore(overlap.getStart()))
                listComplement.add(new Interval(int1.getStart(), overlap.getStart()));

            if (overlap.getEnd().isBefore(int1.getEnd()))
                listComplement.add(new Interval(overlap.getEnd(), int1.getEnd()));

        }

        Collections.sort(listComplement, new Comparator<Interval>() {
            @Override
            public int compare(Interval o1, Interval o2) {
                return o1.getStart().compareTo(o2.getStart());
            }
        });

        return listComplement.toArray(new Interval[0]);

    }


    /**
     * parses a standard german time string and returns a LocalTime object with the appropriate contents.
     *
     * @param input
     * @return
     * @throws NumberFormatException - when something is wrong with the string
     */
    public static LocalTime parseLocalTime(String input) throws NumberFormatException {
        if (input == null || input.equals("")) {
            throw new NumberFormatException("leere Eingabe");
        }

        if (input.matches("\\d*")){
            input += ":00";
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
        //           GregorianCalendar now = (GregorianCalendar) gc.clone();

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

        return new LocalTime(stunde, minute, sekunde);
    }


    public static LocalDate min(LocalDate a, LocalDate b) {
        return new LocalDate(Math.min(a.toDateTimeAtStartOfDay().getMillis(), b.toDateTimeAtStartOfDay().getMillis()));
    }

    public static LocalDate max(LocalDate a, LocalDate b) {
        return new LocalDate(Math.max(a.toDateTimeAtStartOfDay().getMillis(), b.toDateTimeAtStartOfDay().getMillis()));
    }


}
