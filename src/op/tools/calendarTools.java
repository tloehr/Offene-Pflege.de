package op.tools;

import op.OPDE;

import java.text.NumberFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 12.12.11
 * Time: 16:50
 * To change this template use File | Settings | File Templates.
 */
public class CalendarTools {


    /**
     * Nimmt den aktuellen Zeitpunkt, setzt die Zeit auf 23:59:59 und gibt das Ergebnis zurück.
     *
     * @return
     */
    public static Date endOfDay() {
        return endOfDay(new Date());
    }

    /**
     * nimmt das übergebene Datum und Zeit die Uhrzeitkomponente auf 23:59:59
     *
     * @param d
     * @return
     */
    public static Date endOfDay(Date d) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(d.getTime());
        gc.set(GregorianCalendar.HOUR_OF_DAY, 23);
        gc.set(GregorianCalendar.MINUTE, 59);
        gc.set(GregorianCalendar.SECOND, 59);
        gc.set(GregorianCalendar.MILLISECOND, 0);
        return new Date(gc.getTimeInMillis());
    }

    /**
     * Nimmt den aktuellen Zeitpunkt, setzt die Zeit auf 00:00:00 und gibt das Ergebnis zurück.
     *
     * @return
     */
    public static Date startOfDay() {
        return startOfDay(new Date());
    }

    /**
     * nimmt das übergebene Datum und Zeit die Uhrzeitkomponente auf 00:00:00
     *
     * @param d
     * @return
     */
    public static Date startOfDay(Date d) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(d.getTime());
        gc.set(GregorianCalendar.HOUR_OF_DAY, 0);
        gc.set(GregorianCalendar.MINUTE, 0);
        gc.set(GregorianCalendar.SECOND, 0);
        gc.set(GregorianCalendar.MILLISECOND, 0);
        return new Date(gc.getTimeInMillis());
    }

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

    public static GregorianCalendar toGC(Date d) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(d.getTime());
        return gc;
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

    public static int compareTime(Date date1, Date date2) {
        return compareTime(date1.getTime(), date2.getTime());
    }

    /**
     * Kopiert den Datumsanteil von Source nach Target.
     * @param source
     * @param target
     * @return
     */
    public static GregorianCalendar setDate2Time(GregorianCalendar source, GregorianCalendar target) {
        target.set(GregorianCalendar.DATE, source.get(GregorianCalendar.DATE));
        target.set(GregorianCalendar.MONTH, source.get(GregorianCalendar.MONTH));
        target.set(GregorianCalendar.YEAR, source.get(GregorianCalendar.YEAR));
        return (GregorianCalendar) target.clone();
    }

}
