package de.offene_pflege.op.tools;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.GregorianCalendar;

public class JavaTimeConverter {

    public static final LocalDateTime THE_VERY_BEGINNING = LocalDateTime.of(1970, 1, 1, 0, 0);
    public static final LocalDateTime UNTIL_FURTHER_NOTICE = LocalDateTime.of(9999, 12, 31, 23, 59, 59);

    public static boolean isBefore(Date one, Date two) {
        return toJavaLocalDateTime(one).toLocalDate().isBefore(toJavaLocalDateTime(two).toLocalDate());
    }

    public static boolean isAfter(Date one, Date two) {
        return toJavaLocalDateTime(one).toLocalDate().isAfter(toJavaLocalDateTime(two).toLocalDate());
    }

    public static Date toDate(LocalDate ld) {
        return toDate(ld.atStartOfDay());
    }

    public static Date toDate(LocalDateTime ldt) {
        return new Date(ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    public static Date toDate(String iso8601){
        return toDate(from_iso8601(iso8601));
    }

    public static Period between(Date one, Date two) {
        return Period.between(toJavaLocalDateTime(one).toLocalDate(), toJavaLocalDateTime(two).toLocalDate());
    }

    public static java.time.LocalDateTime toJavaLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static java.time.LocalDate toJavaLocalDate(org.joda.time.LocalDate jodaLD) {
        return java.time.LocalDate.of(jodaLD.getYear(), jodaLD.getMonthOfYear(), jodaLD.getDayOfMonth());
    }

    public static java.time.LocalDateTime toJavaLocalDateTime(org.joda.time.LocalDateTime jodaLDT) {
        return java.time.LocalDateTime.of(jodaLDT.getYear(), jodaLDT.getMonthOfYear(), jodaLDT.getDayOfMonth(), jodaLDT.getMinuteOfHour(), jodaLDT.getHourOfDay(), jodaLDT.getSecondOfMinute());
    }

    public static org.joda.time.LocalDateTime toJodaLocalDateTime(LocalDateTime ldt) {
        ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());
        return new org.joda.time.LocalDateTime(zdt.toInstant().toEpochMilli());
    }

    public static LocalDateTime noon() {
        return noon(LocalDateTime.now());
    }

    public static LocalDateTime noon(LocalDateTime jt_ldt) {
        return jt_ldt.withHour(12).withMinute(0).withSecond(0).withNano(0);
    }


    public static XMLGregorianCalendar toXMLGregorianCalendar(LocalDateTime ldt) {
        XMLGregorianCalendar x = toXMLGregorianCalendar(GregorianCalendar.from(ldt.atZone(ZoneId.systemDefault())));
        x.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        return x;
    }

    public static XMLGregorianCalendar toXMLGregorianCalendar(LocalDate localDate) {
        return toXMLGregorianCalendar(localDate.atStartOfDay());
    }

    public static XMLGregorianCalendar toXMLGregorianCalendar(GregorianCalendar cal) {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static XMLGregorianCalendar toXMLGregorianCalendar(Date date) {
        return toXMLGregorianCalendar(toJavaLocalDateTime(date).toLocalDate());
    }

    public static LocalDateTime toLocalDateTime(String date) {
        return java.time.ZonedDateTime.parse(date).toLocalDateTime();
    }

    public static LocalDate min(LocalDate a, LocalDate b) {
        return min(a.atStartOfDay(), b.atStartOfDay()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDate max(LocalDate a, LocalDate b) {
        return max(a.atStartOfDay(), b.atStartOfDay()).atZone(ZoneId.systemDefault()).toLocalDate();
    }


    public static LocalDateTime min(LocalDateTime a, LocalDateTime b) {
        long la = a.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long lb = b.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        return Instant.ofEpochMilli(Math.min(la, lb)).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDateTime max(LocalDateTime a, LocalDateTime b) {
        long la = a.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long lb = b.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        return Instant.ofEpochMilli(Math.max(la, lb)).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static String parseDateIfPossible(String value, FormatStyle style) {
        try {
            // Ist value ein Datum ?
            // Dann stellen wir es schön dar und nicht in der ISO Schreibweise
            java.time.LocalDate jld = java.time.ZonedDateTime.parse(value).toLocalDate();
            return jld.format(DateTimeFormatter.ofLocalizedDate(style));
        } catch (java.time.format.DateTimeParseException dtpe) {
            // ansonsten lassen wir es einfach
            return value;
        }
    }

    public static String to_iso8601(Date date) {
        return to_iso8601(toJavaLocalDateTime(date));
    }

    public static String to_iso8601(GregorianCalendar gc) {
        return to_iso8601(toJavaLocalDateTime(gc.getTime()));
    }

    public static String to_iso8601(LocalDateTime now) {
        return now.atZone(ZoneId.systemDefault()).toString();
    }

    public static LocalDateTime from_iso8601(String iso8601) {
        return ZonedDateTime.parse(iso8601).toLocalDateTime();
    }



}
