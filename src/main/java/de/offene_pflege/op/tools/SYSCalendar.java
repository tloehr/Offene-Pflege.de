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
package de.offene_pflege.op.tools;

import com.toedter.calendar.JDateChooser;
import de.offene_pflege.backend.entity.system.SYSPropsTools;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.threads.DisplayMessage;
import org.apache.commons.collections.Closure;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

public class SYSCalendar {

    // Both for DFN and BHP
    public static final byte SHIFT_OUTCOMES = -2;
    public static final byte SHIFT_ON_DEMAND = -1;
    public static final byte SHIFT_VERY_EARLY = 0;
    public static final byte SHIFT_EARLY = 1;
    public static final byte SHIFT_LATE = 2;
    public static final byte SHIFT_VERY_LATE = 3;

    public static final Byte[] SHIFTS = new Byte[]{SHIFT_VERY_EARLY, SHIFT_EARLY, SHIFT_LATE, SHIFT_VERY_LATE};

    public static final byte BYTE_TIMEOFDAY = 0;
    public static final byte BYTE_EARLY_IN_THE_MORNING = 1;
    public static final byte BYTE_MORNING = 2;
    public static final byte BYTE_NOON = 3;
    public static final byte BYTE_AFTERNOON = 4;
    public static final byte BYTE_EVENING = 5;
    public static final byte BYTE_LATE_AT_NIGHT = 6;

    public static final String STRING_TIMEOFDAY = "UZ";
    public static final String STRING_EARLY_IN_THE_MORNING = "FM";
    public static final String STRING_MORNING = "MO";
    public static final String STRING_NOON = "MI";
    public static final String STRING_AFTERNOON = "NM";
    public static final String STRING_EVENING = "AB";
    public static final String STRING_LATE_AT_NIGHT = "NA";

    public static final String[] SHIFT_KEY_TEXT = new String[]{"VERY_EARLY", "EARLY", "LATE", "VERY_LATE"};
    public static final String[] SHIFT_TEXT = new String[]{"msg.shift.veryearly", "msg.shift.early", "msg.shift.late", "msg.shift.verylate"};
    public static final String[] TIMEIDTEXTLONG = new String[]{"misc.msg.Time.long", "misc.msg.earlyinthemorning.long", "misc.msg.morning.long", "misc.msg.noon.long", "misc.msg.afternoon.long", "misc.msg.evening.long", "misc.msg.lateatnight.long"};
    public static final String[] TIMEIDTEXTSHORT = new String[]{"misc.msg.Time.short", "misc.msg.earlyinthemorning.short", "misc.msg.morning.short", "misc.msg.noon.short", "misc.msg.afternoon.short", "misc.msg.evening.short", "misc.msg.lateatnight.short"};


//    // the beginning null makes it compatible with the joda int constants.
//    public final static Month[] months = new Month[]{null, LammaConst.JANUARY, LammaConst.FEBRUARY, LammaConst.MARCH,
//            LammaConst.APRIL, LammaConst.MAY, LammaConst.JUNE, LammaConst.JULY,
//            LammaConst.AUGUST, LammaConst.SEPTEMBER, LammaConst.OCTOBER, LammaConst.NOVEMBER, LammaConst.DECEMBER};
//
//    public final static Weekday[] weeksdays = new Weekday[]{null, LammaConst.MONDAY, LammaConst.TUESDAY, LammaConst.WEDNESDAY, LammaConst.THURSDAY,
//            LammaConst.FRIDAY, LammaConst.SATURDAY, LammaConst.SUNDAY};


    public static boolean isInFuture(long time) {
        return isInFuture(new Date(time));
    }

    public static boolean isInFuture(Date date) {
        return date.after(new Date());
    }


    /**
     * Generates an ArrayList of Dates. Stepping through the day in 15 minutes steps like {"17:00","17:15"...} Der
     * verwendete Datentyp ist GregorianCalendar
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
                    text = SYSTools.xx("misc.commands.>>noselection<<");
                } else if (o instanceof Date) {
                    Date date = (Date) o;
                    text = timeFormat.format(date) + " " + SYSTools.xx("misc.msg.Time.short");
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
        DateMidnight min = new DateMidnight().minusYears(minage);
        DateMidnight max = new DateMidnight().minusYears(maxage);
        DateMidnight d = new DateMidnight(date);

        return d.isAfter(max) && d.isBefore(min);
    }

    public static void handleDateFocusLost(FocusEvent evt, DateMidnight min, DateMidnight max) {
        DateTime dt;
        if (max == null) {
            max = new DateMidnight();
        }
        try {
            dt = new DateTime(parseDate(((JTextField) evt.getSource()).getText()));
        } catch (NumberFormatException ex) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("misc.msg.wrongdate")));
            dt = new DateTime();
        }
        if (dt.isAfter(max)) {
            dt = new DateTime();
            DisplayMessage dm = new DisplayMessage(dt.isAfterNow() ? SYSTools.xx("misc.msg.futuredate") : SYSTools.xx("misc.msg.wrongdate"));
            OPDE.getDisplayManager().addSubMessage(dm);
        }
        if (dt.isBefore(min)) {
            dt = new DateTime();
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("misc.msg.DateTooOld")));
        }

        ((JTextField) evt.getSource()).setText(DateFormat.getDateInstance().format(dt.toDate()));
    }


    public static void handleDateFocusLost(FocusEvent evt, LocalDate min, LocalDate max) {
        LocalDate dt;
        if (max == null) {
            max = new LocalDate();
        }
        try {
            dt = new LocalDate(parseDate(((JTextField) evt.getSource()).getText()));
        } catch (NumberFormatException ex) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("misc.msg.wrongdate")));
            dt = new LocalDate();
        }
        if (dt.isAfter(max)) {
            dt = new LocalDate();
            DisplayMessage dm = new DisplayMessage(dt.isAfter(max) ? SYSTools.xx("misc.msg.futuredate") : SYSTools.xx("misc.msg.wrongdate"));
            OPDE.getDisplayManager().addSubMessage(dm);
        }
        if (dt.isBefore(min)) {
            dt = new LocalDate();
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("misc.msg.DateTooOld")));
        }

        ((JTextField) evt.getSource()).setText(DateFormat.getDateInstance().format(dt.toDate()));
    }

    /**
     * Expiry dates usually have a form like "12-10" oder "12/10" to indicate that the product in question is best
     * before December 31st, 2010. This method parses dates like this. If it fails it hands over the parsing efforts to
     * <code>public static Date parseDate(String input)</code>.
     *
     * @param input a string to be parsed. It can handle the following formats "mm/yy", "mm/yyyy" (it also recognizes
     *              these kinds of dates if the slash is replaced with one of the following chars: "-,.".
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

        if (tag < 1 || tag > eom(new GregorianCalendar(jahr, monat - 1, 1))) {
            throw new NumberFormatException("month");
        }

        return new DateMidnight(jahr, monat, tag).toDate();
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
        return System.currentTimeMillis();
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
        if (st.countTokens() > 3) {
            throw new NumberFormatException("falsches Format");
        }

        String sStunde = "00";
        String sMinute = "00";
        String sSekunde = "00";

        if (st.countTokens() == 1) { // maybe a simple hour
            sStunde = st.nextToken();
        } else if (st.countTokens() == 2) { // maybe a simple hour
            sStunde = st.nextToken();
            sMinute = st.nextToken();
        } else {
            sStunde = st.nextToken();
            sMinute = st.nextToken();
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
     * @param ts Ist ein TS mit der Uhrzeit, für die wir wissen möchten in welchem Bereich der Medikamenten Vergabe sie
     *           liegt. Früh morgens, morgens... etc. Das Datum in diesem TS ist egal. Es geht NUR um die Uhrzeit.
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


    public static byte whatShiftIs(byte timeID) {
        byte shift;
        if (BYTE_EARLY_IN_THE_MORNING <= timeID && timeID < BYTE_MORNING) {
            shift = SHIFT_VERY_EARLY;
        } else if (BYTE_MORNING <= timeID && timeID < BYTE_AFTERNOON) {
            shift = SHIFT_EARLY;
        } else if (BYTE_AFTERNOON <= timeID && timeID < BYTE_LATE_AT_NIGHT) {
            shift = SHIFT_LATE;
        } else {
            shift = SHIFT_VERY_LATE;
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

        DateTime early_in_the_morning = parser.parseDateTime(OPDE.getProps().getProperty(STRING_EARLY_IN_THE_MORNING));
        DateTime morning = parser.parseDateTime(OPDE.getProps().getProperty(STRING_MORNING));
        DateTime noon = parser.parseDateTime(OPDE.getProps().getProperty(STRING_NOON));
        DateTime afternoon = parser.parseDateTime(OPDE.getProps().getProperty(STRING_AFTERNOON));
        DateTime evening = parser.parseDateTime(OPDE.getProps().getProperty(STRING_EVENING));
        DateTime late_at_night = parser.parseDateTime(OPDE.getProps().getProperty(STRING_LATE_AT_NIGHT));

        Period period_early_in_the_morning = new Period(early_in_the_morning.getHourOfDay(), early_in_the_morning.getMinuteOfHour(), early_in_the_morning.getSecondOfMinute(), early_in_the_morning.getMillisOfSecond());
        Period period_morning = new Period(morning.getHourOfDay(), morning.getMinuteOfHour(), morning.getSecondOfMinute(), morning.getMillisOfSecond());
        Period period_noon = new Period(noon.getHourOfDay(), noon.getMinuteOfHour(), noon.getSecondOfMinute(), noon.getMillisOfSecond());
        Period period_afternoon = new Period(afternoon.getHourOfDay(), afternoon.getMinuteOfHour(), afternoon.getSecondOfMinute(), afternoon.getMillisOfSecond());
        Period period_evening = new Period(evening.getHourOfDay(), evening.getMinuteOfHour(), evening.getSecondOfMinute(), evening.getMillisOfSecond());
        Period period_late_at_night = new Period(late_at_night.getHourOfDay(), late_at_night.getMinuteOfHour(), late_at_night.getSecondOfMinute(), late_at_night.getMillisOfSecond());

        DateTime ref = new DateTime(date);
        DateTime eitm = new LocalDate(date).toDateTimeAtStartOfDay().plus(period_early_in_the_morning);
        DateTime m = new LocalDate(date).toDateTimeAtStartOfDay().plus(period_morning);
        DateTime n = new LocalDate(date).toDateTimeAtStartOfDay().plus(period_noon);
        DateTime a = new LocalDate(date).toDateTimeAtStartOfDay().plus(period_afternoon);
        DateTime e = new LocalDate(date).toDateTimeAtStartOfDay().plus(period_evening);
        DateTime lan = new LocalDate(date).toDateTimeAtStartOfDay().plus(period_late_at_night);

        if (eitm.compareTo(ref) <= 0 && ref.compareTo(m) < 0) {
            timeid = BYTE_EARLY_IN_THE_MORNING;
        } else if (m.compareTo(ref) <= 0 && ref.compareTo(n) < 0) {
            timeid = BYTE_MORNING;
        } else if (n.compareTo(ref) <= 0 && ref.compareTo(a) < 0) {
            timeid = BYTE_NOON;
        } else if (a.compareTo(ref) <= 0 && ref.compareTo(e) < 0) {
            timeid = BYTE_AFTERNOON;
        } else if (e.compareTo(ref) <= 0 && ref.compareTo(lan) < 0) {
            timeid = BYTE_EVENING;
        } else {
            timeid = BYTE_LATE_AT_NIGHT;
        }
        return timeid;
    }


    /**
     * EndOfMonth Berechnet das Enddatum eines Monats, passend zum Parameter
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
//   min
//            default: {
//                return 0;
//            }
//        }
    } // eom

//    /**
//     * Entscheidet ob zwei Daten in der selben Woche liegen. Berücksichtigt dabei die Jahreszahlen, so dass KW 34/2006 != KW 34/2005.
//     */
//    public static int sameWeek(GregorianCalendar a, GregorianCalendar b) {
//        NumberFormat form = new java.text.DecimalFormat("00");
//
//        int aYear = a.get(GregorianCalendar.YEAR);
//        int bYear = b.get(GregorianCalendar.YEAR);
//
//        // Die KW der letzten Tage eines Jahres gehören manchmal schon zum neuen Jahr. So gehörte der 29.12.2008 schon zur KW1 von 2009.
//        // Diese Zeilen berücksichtigen das. Sie erhöhen die Jahreszahl wenn der Monat Dezember ist und die KW trotzdem 1.
//        // Bug 0000016: Fehlende Einträge in der Behandlungspflege
//        if (a.get(GregorianCalendar.WEEK_OF_YEAR) == 1 && a.get(GregorianCalendar.MONTH) == GregorianCalendar.DECEMBER) {
//            aYear++;
//        }
//        if (b.get(GregorianCalendar.WEEK_OF_YEAR) == 1 && b.get(GregorianCalendar.MONTH) == GregorianCalendar.DECEMBER) {
//            bYear++;
//        }
//
//        String sa = Integer.toString(aYear) + form.format(a.get(GregorianCalendar.WEEK_OF_YEAR));
//        String sb = Integer.toString(bYear) + form.format(b.get(GregorianCalendar.WEEK_OF_YEAR));
//        int ia = Integer.parseInt(sa);
//        int ib = Integer.parseInt(sb);
//
//        if (ia > ib) {
//            return 1;
//        }
//        if (ia < ib) {
//            return -1;
//        }
//        return 0;
//    }
//
//    /**
//     * Entscheidet ob zwei Daten im selben Monat liegen. Berücksichtigt dabei die Jahreszahlen, so dass Monat 12/2006 != Monat 12/2005.
//     */
//    public static int sameMonth(GregorianCalendar a, GregorianCalendar b) {
//        NumberFormat form = new java.text.DecimalFormat("00");
//        String sa = Integer.toString(a.get(GregorianCalendar.YEAR)) + form.format(a.get(GregorianCalendar.MONTH));
//        String sb = Integer.toString(b.get(GregorianCalendar.YEAR)) + form.format(b.get(GregorianCalendar.MONTH));
//        int ia = Integer.parseInt(sa);
//        int ib = Integer.parseInt(sb);
//
//        if (ia > ib) {
//            return 1;
//        }
//        if (ia < ib) {
//            return -1;
//        }
//        return 0;
//    }

//    /**
//     * Entscheidet ob zwei Daten am selben Tag liegen.
//     *
//     * @param a Datum a
//     * @param b Datum b
//     * @return 0, wenn a und b am selben Tag liegen. -1, wenn a <b>vor</b> b ist. und +1 wenn a <b>nach</b> b ist.
//     */
//    public static int sameDay(GregorianCalendar a, GregorianCalendar b) {
//        NumberFormat form = new java.text.DecimalFormat("00");
//        String sa = Integer.toString(a.get(GregorianCalendar.YEAR)) + form.format(a.get(GregorianCalendar.MONTH)) + form.format(a.get(GregorianCalendar.DATE));
//        String sb = Integer.toString(b.get(GregorianCalendar.YEAR)) + form.format(b.get(GregorianCalendar.MONTH)) + form.format(b.get(GregorianCalendar.DATE));
//        int ia = Integer.parseInt(sa);
//        int ib = Integer.parseInt(sb);
//
//        if (ia > ib) {
//            return 1;
//        }
//        if (ia < ib) {
//            return -1;
//        }
//        return 0;
//    }

//    /**
//     * Entscheidet ob zwei Daten am selben Tag liegen.
//     *
//     * @param a Datum a
//     * @param b Datum b
//     * @return 0, wenn a und b am selben Tag liegen. -1, wenn a <b>vor</b> b ist. und +1 wenn a <b>nach</b> b ist.
//     */
//    public static int sameDay(Date a, Date b) {
//        return sameDay(toGC(a), toGC(b));
//    }
//
//    public static int sameDay(long a, long b) {
//        return sameDay(toGC(a), toGC(b));
//    }

//    public static int calculateAge(GregorianCalendar a, GregorianCalendar b) {
//        return getDaysBetween(a, b) / 365;
//    }
//
//    public static int calculateAge(GregorianCalendar a) {
//        return getDaysBetween(a, new GregorianCalendar()) / 365;
//    }

    public static void checkJDC(JDateChooser jdc) {
        if (jdc.getDate() == null) {
            jdc.setDate(new Date());
        } // ungültiges Datum
        if (jdc.getMaxSelectableDate().before(jdc.getDate())) {
            jdc.setDate(new Date());
        }
    }

//    public static boolean isJDCValid(JDateChooser jdc) {
//        boolean valid = false;
//        boolean dateTrouble = (jdc.getDate() == null);
//        return !dateTrouble && isInRange(jdc);
//    }

    /**
     * Addiert (bzw. Subtrahiert) eine angebenene Anzahl von Tagen auf das (bzw. von dem) übergebenen Datum und gibt es
     * dann zurück.
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

//    // Das hier ist noch nicht ganz richtig. Vertut sich an den Rändern schon mal.
//    public static Date addDate(Date date, int amount, Date min, Date max) {
//        GregorianCalendar gc = toGC(date);
//        gc.add(GregorianCalendar.DATE, amount);
//        Date result = new Date(gc.getTimeInMillis());
//        if (!(trimTime(min).before(trimTime(result)) && trimTime(max).after(trimTime(result)))) {
//            result = date;
//        }
//        return result;
//    }

//    public static Date addField(Date date, int amount, int field) {
//        GregorianCalendar gc = toGC(date);
//        gc.add(field, amount);
//        return new Date(gc.getTimeInMillis());
//    }

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
        for (LocalDate month = start; month.compareTo(end) <= 0; month = month.plusMonths(1)) {
            dcbm.addElement(month);
        }
        return dcbm;
    }

//    /**
//     * gibt das heutige Datum zurück, allerdings um die Uhrzeitanteile bereinigt.
//     *
//     * @return das ein um die Uhrzeit bereinigtes Datum.
//     */
//    public static GregorianCalendar trimTime(GregorianCalendar gc) {
//        gc.set(GregorianCalendar.HOUR, 0);
//        gc.set(GregorianCalendar.MINUTE, 0);
//        gc.set(GregorianCalendar.SECOND, 0);
//        gc.set(GregorianCalendar.MILLISECOND, 0);
//        return gc;
//    }

    /**
     * Bereinigt ein Datum um die Uhrzeitanteile, damit die Vergleichsoperatoren richtig funktionieren, wenn man sich nur auf das
     * Datum beschränken will.
     *
     * @param date das zu bereinigende Datum
     * @return das bereinigte Datum
     */
//    public static Date trimTime(Date date) {
//        return new Date(trimTime(toGC(date)).getTimeInMillis());
//    }
//
//    public static boolean betweenDisjunctive(Date from, Date to, Date date) {
//        return date.getTime() > from.getTime() && date.getTime() < to.getTime();
//    }
//
//    public static boolean betweenOverlap(Date from, Date to, Date date) {
//        return betweenDisjunctive(from, to, date) || date.getTime() == from.getTime() || date.getTime() == to.getTime();
//    }
//
//    public static boolean betweenDisjunctive(Date fromInt, Date toInt, Date from, Date to) {
//        return betweenDisjunctive(fromInt, toInt, from) && betweenDisjunctive(fromInt, toInt, to);
//    }
//
//    public static boolean betweenOverlap(Date fromInt, Date toInt, Date from, Date to) {
//        return betweenOverlap(fromInt, toInt, from) && betweenOverlap(fromInt, toInt, to);
//    }
//
//    public static boolean isInRange(JDateChooser jdc) {
//        if (jdc.getDate() != null) {
//            return betweenOverlap(jdc.getMinSelectableDate(), jdc.getMaxSelectableDate(), jdc.getDate());
//        }
//        return false;
//    }
//
//    public static ArrayList getUhrzeitListe(GregorianCalendar start, GregorianCalendar stop, int minutes) {
//        ArrayList result = new ArrayList();
//        while (start.before(stop)) {
//            result.add(toGermanTime(start));
//            start.add(GregorianCalendar.MINUTE, minutes);
//        }
//        return result;
//    }
//
//
//    public static Date addTime2Date(Date date, Time time) {
//        return new Date(addTime2Date(toGC(date), toGC(time)).getTimeInMillis());
//    }
//
//    public static DateTime addCurrentTime(DateMidnight date) {
//        DateTime time = new DateTime();
//
//        return date.toDateTime().withTime(time.getHourOfDay(), time.getMinuteOfHour(), time.getSecondOfMinute(), time.getMillisOfSecond());
//    }
//
//    public static Date addTime2Date(Date date, Date time) {
//        return new Date(addTime2Date(toGC(date), toGC(time)).getTimeInMillis());
//    }

    /**
     * Setzt die Zeitkomponente eines GregorianCalendars. Das heisst ganz einfach, wenn man ein Datum hat (in einem GC)
     * und in einem anderen GC steht die Uhrzeit, dann fügt diese Methode die beiden Komponenten zu einem Datum und
     * Uhrzeit Wert zusammen und gibt diesen zurück.
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
     * Diese Routine vergleicht Uhrzeiten, die in zwei longs hinterlegt sind. Das Besondere dabei ist, dass das Datum
     * ausser acht gelassen wird.
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
     * Diese Routine vergleicht Uhrzeiten, die in zwei longs hinterlegt sind. Das Besondere dabei ist, dass das Datum
     * ausser acht gelassen wird.
     *
     * @return int < 0, wenn time1 < time2; int == 0, wenn time1 = time2; int > 0, wenn time1 > time2
     * @time1
     * @time2
     */
    public static int compareTime(Date date1, Date date2) {
        return compareTime(date1.getTime(), date2.getTime());
    }

    public static long startOfDay() {
        return startOfDay(new Date());
    }


    /**
     * Nimmt den aktuellen Zeitpunkt, setzt die Zeit auf 23:59:59 und gibt das Ergebnis zurück.
     *
     * @return
     */
    public static long endOfDay() {
        return endOfDay(new Date());
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


//    /**
//     * nimmt das übergebene Datum und setzt die Uhrzeitkomponente auf 12:00:00
//     *
//     * @param d
//     * @return das Ergebnis als TimeInMillis
//     */
//    public static long midOfDay(Date d) {
//        GregorianCalendar gc = new GregorianCalendar();
//        gc.setTimeInMillis(d.getTime());
//        gc.set(GregorianCalendar.HOUR_OF_DAY, 12);
//        gc.set(GregorianCalendar.MINUTE, 0);
//        gc.set(GregorianCalendar.SECOND, 0);
//        gc.set(GregorianCalendar.MILLISECOND, 0);
//        return gc.getTimeInMillis();
//    }


    public static DateTime midOfDay() {
        return midOfDay(new LocalDate());
    }

    public static DateTime midOfDay(LocalDate d) {
        return midOfDay(d.toDateTimeAtCurrentTime());
    }

    public static DateTime midOfDay(DateTime dt) {
        return dt.withHourOfDay(12).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
    }



    /**
     * Berechnet zu einem gegebenen Jahr den Ostersonntag. Dieser wird als GregorianCalendar zurückgegeben. Der
     * Algorhythmus wurde von der Internet-Seite www.th-o.de/kalender.htm entnommen. Dort wurde er von Walter Irion
     * beschrieben. Danke, Walter und Thomas.
     * <p>
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
     * @return Eine Hashmap, die je das Datum als Zeichenkette der PrinterForm "jjjj-mm-tt" enthält und dazu die
     * Bezeichnung des Feiertags.
     */
    public static HashMap<LocalDate, String> getHolidays(int from, int to) {

        HashMap<LocalDate, String> hm = new HashMap<LocalDate, String>();

        // TODO: i18n
        for (int year = from; year <= to; year++) {
            // Feste Feiertage
            hm.put(new LocalDate(year, 1, 1), "Neujahrstag");
            hm.put(new LocalDate(year, 5, 1), "Maifeiertag");
            hm.put(new LocalDate(year, 10, 3), "Tag der Einheit");
            hm.put(new LocalDate(year, 11, 1), "Allerheiligen");
            hm.put(new LocalDate(year, 12, 25), "1. Weihnachtstag");
            hm.put(new LocalDate(year, 12, 26), "2. Weihnachtstag");

            // Ausnahmsweise Feiertage (Was es alles gibt).
            // https://github.com/tloehr/Offene-Pflege.de/issues/70
            hm.put(new LocalDate(2017, 10, 31), "Reformationstag"); // aus Anlass des 500-jährigen Jubiläums

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
        JPopupMenu timemenu = new JPopupMenu(SYSTools.xx("misc.commands.changeeffort"));

        for (int min : mins) {
            String title = "";
            if (min % 60 == 0) {
                title = min / 60 + " " + SYSTools.xx("misc.msg.Hour(s)");
            } else {
                title = min + " " + SYSTools.xx("misc.msg.Minute(s)");
            }

            JMenuItem item = new JMenuItem(title);
            final int minutes = min;
            item.addActionListener(evt -> action.execute(minutes));
            timemenu.add(item);
        }

        return timemenu;
    }


    public static Color getFGItem(Byte shift) {
        if (shift == SHIFT_ON_DEMAND) {
            return GUITools.getColor(OPDE.getProps().getProperty(SYSPropsTools.KEY_ONDEMAND_FGITEM));
        } else if (shift == SHIFT_VERY_EARLY) {
            return GUITools.getColor(OPDE.getProps().getProperty(SYSPropsTools.KEY_VERY_EARLY_FGITEM));
        } else if (shift == SHIFT_EARLY) {
            return GUITools.getColor(OPDE.getProps().getProperty(SYSPropsTools.KEY_EARLY_FGITEM));
        } else if (shift == SHIFT_LATE) {
            return GUITools.getColor(OPDE.getProps().getProperty(SYSPropsTools.KEY_LATE_FGITEM));
        } else if (shift == SHIFT_VERY_LATE) {
            return GUITools.getColor(OPDE.getProps().getProperty(SYSPropsTools.KEY_VERY_LATE_FGITEM));
        }
        return null;
    }

    public static Color getBGItem(Byte shift) {
        if (shift == SHIFT_ON_DEMAND) {
            return GUITools.getColor(OPDE.getProps().getProperty(SYSPropsTools.KEY_ONDEMAND_BGITEM));
        } else if (shift == SHIFT_VERY_EARLY) {
            return GUITools.getColor(OPDE.getProps().getProperty(SYSPropsTools.KEY_VERY_EARLY_BGITEM));
        } else if (shift == SHIFT_EARLY) {
            return GUITools.getColor(OPDE.getProps().getProperty(SYSPropsTools.KEY_EARLY_BGITEM));
        } else if (shift == SHIFT_LATE) {
            return GUITools.getColor(OPDE.getProps().getProperty(SYSPropsTools.KEY_LATE_BGITEM));
        } else if (shift == SHIFT_VERY_LATE) {
            return GUITools.getColor(OPDE.getProps().getProperty(SYSPropsTools.KEY_VERY_LATE_BGITEM));
        }

        return null;
    }

    public static Color getFGSHIFT(Byte shift) {
        if (shift == SHIFT_ON_DEMAND) {
            return GUITools.getColor(OPDE.getProps().getProperty(SYSPropsTools.KEY_ONDEMAND_FGSHIFT));
        } else if (shift == SHIFT_OUTCOMES) {
            return GUITools.getColor(OPDE.getProps().getProperty(SYSPropsTools.KEY_OUTCOME_FGSHIFT));
        } else if (shift == SHIFT_VERY_EARLY) {
            return GUITools.getColor(OPDE.getProps().getProperty(SYSPropsTools.KEY_VERY_EARLY_FGSHIFT));
        } else if (shift == SHIFT_EARLY) {
            return GUITools.getColor(OPDE.getProps().getProperty(SYSPropsTools.KEY_EARLY_FGSHIFT));
        } else if (shift == SHIFT_LATE) {
            return GUITools.getColor(OPDE.getProps().getProperty(SYSPropsTools.KEY_LATE_FGSHIFT));
        } else if (shift == SHIFT_VERY_LATE) {
            return GUITools.getColor(OPDE.getProps().getProperty(SYSPropsTools.KEY_VERY_LATE_FGSHIFT));
        }
        return null;
    }

    public static Color getBGSHIFT(Byte shift) {
        if (shift == SHIFT_ON_DEMAND) {
            return GUITools.getColor(OPDE.getProps().getProperty(SYSPropsTools.KEY_ONDEMAND_BGSHIFT));
        } else if (shift == SHIFT_OUTCOMES) {
            return GUITools.getColor(OPDE.getProps().getProperty(SYSPropsTools.KEY_OUTCOME_BGSHIFT));
        } else if (shift == SHIFT_VERY_EARLY) {
            return GUITools.getColor(OPDE.getProps().getProperty(SYSPropsTools.KEY_VERY_EARLY_BGSHIFT));
        } else if (shift == SHIFT_EARLY) {
            return GUITools.getColor(OPDE.getProps().getProperty(SYSPropsTools.KEY_EARLY_BGSHIFT));
        } else if (shift == SHIFT_LATE) {
            return GUITools.getColor(OPDE.getProps().getProperty(SYSPropsTools.KEY_LATE_BGSHIFT));
        } else if (shift == SHIFT_VERY_LATE) {
            return GUITools.getColor(OPDE.getProps().getProperty(SYSPropsTools.KEY_VERY_LATE_BGSHIFT));
        }

        return null;
    }

    public static DateTime eod(DateTime date) {
        return date.hourOfDay().withMaximumValue().minuteOfHour().withMaximumValue().secondOfMinute().withMaximumValue().millisOfSecond().withMaximumValue();
    }

    public static DateTime eod(LocalDate date) {
        return eod(date.toDateTimeAtStartOfDay());
    }

    public static DateTime bom(DateTime d) {
        return d.dayOfMonth().withMinimumValue().hourOfDay().withMinimumValue().minuteOfHour().withMinimumValue().secondOfMinute().withMinimumValue().millisOfSecond().withMinimumValue();
    }

    public static DateTime eom(DateTime d) {
        return d.dayOfMonth().withMaximumValue().hourOfDay().withMaximumValue().minuteOfHour().withMaximumValue().secondOfMinute().withMaximumValue().millisOfSecond().withMaximumValue();
    }


    public static LocalDate bow(LocalDate d) {
        return d.dayOfWeek().withMinimumValue();
    }

    public static LocalDate eow(LocalDate d) {
        return d.dayOfWeek().withMaximumValue();
    }

    public static DateTime boy(int year) {
        return new DateTime(year, 1, 1, 0, 0, 0);
    }

    public static LocalDate boy(LocalDate year) {
        return new LocalDate(year.getYear(), 1, 1);
    }

    public static DateTime eoy(int year) {
        return new DateTime(year, 12, 31, 23, 59, 59);
    }


    public static LocalDate eoy(LocalDate day) {
        return new LocalDate(day.getYear(), 12, 31);
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

    public static LocalDate eom(LocalDate d) {
        return d.dayOfMonth().withMaximumValue();
    }

    public static LocalDate min(LocalDate a, LocalDate b) {
        return new LocalDate(Math.min(a.toDateTimeAtStartOfDay().getMillis(), b.toDateTimeAtStartOfDay().getMillis()));
    }

    public static DateTime max(DateTime a, DateTime b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.compareTo(b) > 0 ? a : b;
    }

    public static DateTime min(DateTime a, DateTime b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.compareTo(b) < 0 ? a : b;
    }

    public static LocalDate max(LocalDate a, LocalDate b) {
        return new LocalDate(Math.max(a.toDateTimeAtStartOfDay().getMillis(), b.toDateTimeAtStartOfDay().getMillis()));
    }



}
