package entity.verordnungen;

import entity.system.SYSRunningClasses;
import entity.system.SYSRunningClassesTools;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 01.12.11
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
public class BHPTools {

    public static final byte STATUS_OFFEN = 0;
    public static final byte STATUS_ERLEDIGT = 1;
    public static final byte STATUS_VERWEIGERT = 2;
    public static final byte STATUS_VERWEIGERT_VERWORFEN = 3;

    public static final String[] SOLLZEITTEXT = new String[]{"Uhrzeit", "NachtMo", "Morgens", "Mittags", "Nachmittags", "Abends", "NachtAb"};

    public static long getNumBHPs(Verordnung verordnung) {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("BHP.numByNOTStatusAndVerordnung");
        query.setParameter("verordnung", verordnung);
        query.setParameter("status", STATUS_OFFEN);
        long num = (Long) query.getSingleResult();
        em.close();
        return num;
    }


//    public static int erzeugen(EntityManager em) throws Exception {
//        return erzeugen(em, (Verordnung) null, null);
//    }

    /**
     * verordnung Die Verordnung, auf den sich der Import Vorgang beschränken soll. Steht hier null, dann wird die BHP für alle erstellt.
     *                   Wird eine Zeit angegeben, dann wird der Plan nur ab diesem Zeitpunkt (innerhalb des Tages) erstellt.
     *                   Es wird noch geprüft, ob es abgehakte BHPs in dieser Schicht gibt. Wenn ja, wird alles erst ab der nächsten
     *                   Schicht eingetragen.
     * zeit       ist ein Date, dessen Uhrzeit-Anteil benutzt wird, um nur die BHPs für diesen Tag <b>ab</b> dieser Uhrzeit zu importieren. <code>null</code>, wenn alle importiert werden sollen.
     * @return Anzahl der erzeugten BHPs
     */
    public static int erzeugen(EntityManager em) throws Exception {

        String internalClassID = "nursingrecords.bhpimport";
        int numbhp = 0;

        String wtag; // kurze Darstellung des Wochentags des Stichtags
        int tagnum;
        int wtagnum;
        SYSRunningClasses me = null;


        me = SYSRunningClassesTools.startModule(internalClassID, new String[]{"nursingrecords.prescription", "nursingrecords.bhp", "nursingrecords.bhpimport"}, 5, "BHP Tagesplan muss erstellt werden.");


        if (me != null) {


            Date stichtag = new Date();
            GregorianCalendar gcStichtag = SYSCalendar.toGC(stichtag);

            // Mache aus "Montag" -> "mon"
            wtag = SYSCalendar.WochentagName(gcStichtag);
            wtag = wtag.substring(0, 3).toLowerCase();

            tagnum = gcStichtag.get(GregorianCalendar.DAY_OF_MONTH);
            wtagnum = gcStichtag.get(GregorianCalendar.DAY_OF_WEEK_IN_MONTH); // der erste Mitwwoch im Monat hat 1, der zweite 2 usw...

            Query select = em.createQuery(" " +
                    " SELECT vp FROM VerordnungPlanung vp " +
                    " JOIN vp.verordnung v " +
                    // nur die Verordnungen, die überhaupt gültig sind
                    // das sind die mit Gültigkeit BAW oder Gültigkeit endet irgendwann in der Zukunft.
                    // Das heisst, wenn eine Verordnung heute endet, dann wird sie dennoch eingetragen.
                    // Also alle, die bis EINSCHLIEßLICH heute gültig sind.
                    " WHERE v.situation IS NULL AND v.anDatum <= :andatum AND v.abDatum >= :abdatum" +
//                    // Einschränkung auf bestimmte Verordnung0en, wenn gewünscht
//                    (verordnung != null ? " AND v = :verordnung " : " ") +
                    // und nur die Planungen, die überhaupt auf den Stichtag passen könnten.
                    // Hier werden bereits die falschen Wochentage rausgefiltert. Brauchen
                    // wir uns nachher nicht mehr drum zu kümmern.
                    " AND (" +
                    // 1. Alles was taeglich (jeden Tag oder jeden soundsovielten Tag)
                    "       (vp.taeglich > 0) " +
                    "            OR" +
                    // 2.   Alles was woechentlich ist und die Spalte mit dem aktuellen Wochentagsnamen größer null ist.
                    //      Hier wird der Ausdruck dynamisch erzeugt. wtag enthält nämlich das Wochentagskürzel
                    "       (vp.woechentlich > 0 AND vp." + wtag + " > 0) " +
                    "           OR " +
                    // 3.   Monatliche Einträge. Aber nur dann, wenn
                    "       (vp.monatlich > 0 AND " +
                    // 3a.  es der n.te Tag im Monat ist
                    "           (vp.tagNum = :tagnum " +
                    "               OR " +
                    // 3b.  oder der n.te Wochentag (z.B. Freitag) im Monat ist.
                    //      :wtag enthält hier den sog. DAY_OF_WEEK_IN_MONTH
                    "           vp." + wtag + " = :wtag)" +
                    "       )" +
                    "    ) " +
                    // und nur diejenigen, deren Referenzdatum nicht in der Zukunft liegt.
                    "AND vp.lDatum <= :ldatum AND v.bewohner.adminonly <> 2");

            // Diese Aufstellung ergibt mindestens die heute gültigen Einträge.
            // Wahrscheinlich jedoch mehr als diese. Anhand des LDatums müssen
            // die wirklichen Treffer nachher genauer ermittelt werden.

            OPDE.info(SYSTools.getWindowTitle("BHPImport"));

            OPDE.info("Schreibe nach: " + OPDE.getUrl());

            select.setParameter("andatum", new Date(SYSCalendar.endOfDay(stichtag)));
            select.setParameter("abdatum", new Date(SYSCalendar.startOfDay(stichtag)));
            select.setParameter("tagnum", tagnum);
            select.setParameter("wtag", wtagnum);
            select.setParameter("ldatum", new Date(SYSCalendar.endOfDay(stichtag)));

//            if (verordnung != null) {
//                select.setParameter("verordnung", verordnung);
//            }

            List<VerordnungPlanung> list = select.getResultList();


            numbhp = erzeugen(em, list, stichtag, null);


            OPDE.info("BHPImport abgeschlossen");
            if (me != null) {
                SYSRunningClassesTools.endModule(me);
            }
        } else {
            OPDE.warn("BHPImport nicht abgeschlossen. Zugriffskonflikt.");
        }
        return numbhp;
    }


    /**
     * Löscht alle <b>heutigen</b> nicht <b>abgehakten</b> BHPs für eine bestimmte Verordnung <b>ab</b> einer bestimmten Tages-Zeit.
     *
     * @param em         EntityManager, in dessen Kontext das hier ablaufen soll.
     * @param abUhrzeit  ist ein bestimmter Zeitpunkt. Das gilt natürlich nur für den aktuellen Tag. Somit ist
     *                   bei <code>abUhrzeit</code> nur der Uhrzeit anteil relevant. Über diesen wird die Schicht (bzw. Zeit) ermittelt. Bei BHPs,
     *                   die sich auf eine bestimmte Uhrzeit beziehen, werden nur diejenigen gelöscht, die <b>größer gleich</b> <code>abUhrzeit</code> sind.
     * @param verordnung um die es geht.
     */
    public static void aufräumen(EntityManager em, Verordnung verordnung, Date abUhrzeit) throws Exception {

        int zeit = SYSCalendar.ermittleZeit(abUhrzeit.getTime());

        String sql = "DELETE b.* FROM BHP b INNER JOIN BHPPlanung bhp ON b.BHPPID = bhp.BHPPID " +
                " WHERE bhp.VerID = ? AND Status = 0 AND Date(Soll)=Date(now()) AND " +
                " (" +
                "   ( " +
                "       ( SZeit > ? )" +
                "   ) " +
                "   OR " +
                "   (" +
                "       ( SZeit = 0 AND Time(Soll) >= Time(?) )" +
                "   )" +
                " )";

        Query query = em.createNativeQuery(sql);

        query.setParameter(1, verordnung.getVerid());
        query.setParameter(2, zeit);
        query.setParameter(3, abUhrzeit);

        query.executeUpdate();

    }


    public static int erzeugen(EntityManager em, List<VerordnungPlanung> list, Date stichtag, Date zeit) {
        GregorianCalendar gcStichtag = SYSCalendar.toGC(stichtag);
        int maxrows = list.size();
        int numbhp = 0;

        Iterator<VerordnungPlanung> planungen = list.iterator();
        int row = 0;

        OPDE.debug("MaxRows: " + maxrows);
        // Erstmal alle Einträge, die täglich oder wöchentlich nötig sind.
        while (planungen.hasNext()) {
            row++;
            VerordnungPlanung planung = planungen.next();
            OPDE.info("Fortschritt Vorgang: " + ((float) row / maxrows) * 100 + "%");
            OPDE.debug("==========================================");
            //OPDE.debug(planung);
            System.out.println("Planung: " + planung.getBhppid());
            OPDE.debug("BWKennung: " + planung.getVerordnung().getBewohner().getBWKennung());
            OPDE.debug("VerID: " + planung.getVerordnung().getVerid());


            boolean treffer = false;
            GregorianCalendar ldatum = SYSCalendar.toGC(planung.getLDatum());

            OPDE.debug("LDatum: " + DateFormat.getDateTimeInstance().format(planung.getLDatum()));
            OPDE.debug("Stichtag: " + DateFormat.getDateTimeInstance().format(stichtag));

            // Genaue Ermittlung der Treffer
            // =============================
            if (planung.getTaeglich() > 0) { // Taeglich -------------------------------------------------------------
                OPDE.debug("Eine tägliche Planung");
                // Dann wird das LDatum solange um die gewünschte Tagesanzahl erhöht, bis
                // der stichtag getroffen wurde oder überschritten ist.
                while (SYSCalendar.sameDay(ldatum, gcStichtag) < 0) {
                    OPDE.debug("ldatum liegt vor dem stichtag. Addiere tage: " + planung.getTaeglich());
                    ldatum.add(GregorianCalendar.DATE, planung.getTaeglich());
                }
                // Mich interssiert nur der Treffer, also die Punktlandung auf dem Stichtag
                treffer = SYSCalendar.sameDay(ldatum, gcStichtag) == 0;
            } else if (planung.getWoechentlich() > 0) { // Woechentlich -------------------------------------------------------------
                OPDE.debug("Eine wöchentliche Planung");
                while (SYSCalendar.sameWeek(ldatum, gcStichtag) < 0) {
                    OPDE.debug("ldatum liegt vor dem stichtag. Addiere Wochen: " + planung.getWoechentlich());
                    ldatum.add(GregorianCalendar.WEEK_OF_YEAR, planung.getWoechentlich());
                }
                // Ein Treffer ist es dann, wenn das Referenzdatum gleich dem Stichtag ist ODER es zumindest in der selben Kalenderwoche liegt.
                // Da bei der Vorauswahl durch die Datenbank nur passende Wochentage überhaupt zugelassen wurden, muss das somit der richtige sein.
                treffer = SYSCalendar.sameWeek(ldatum, gcStichtag) == 0;
            } else if (planung.getMonatlich() > 0) { // Monatlich -------------------------------------------------------------
                OPDE.debug("Eine monatliche Planung");
                while (SYSCalendar.sameMonth(ldatum, gcStichtag) < 0) {
                    OPDE.debug("ldatum liegt vor dem stichtag. Addiere Monate: " + planung.getMonatlich());
                    ldatum.add(GregorianCalendar.MONTH, planung.getMonatlich());
                }
                // Ein Treffer ist es dann, wenn das Referenzdatum gleich dem Stichtag ist ODER es zumindest im selben Monat desselben Jahres liegt.
                // Da bei der Vorauswahl durch die Datenbank nur passende Wochentage oder Tage im Monat überhaupt zugelassen wurden, muss das somit der richtige sein.
                treffer = SYSCalendar.sameMonth(ldatum, gcStichtag) == 0;
            }

            OPDE.debug("LDatum jetzt: " + DateFormat.getDateTimeInstance().format(new Date(ldatum.getTimeInMillis())));
            OPDE.debug("Treffer ? : " + Boolean.toString(treffer));

            // Es wird immer erst eine Schicht später eingetragen. Damit man nicht mit bereits
            // abgelaufenen Zeitpunkten arbeitet.
            // Bei zeit == null werden all diese Booleans zu true und damit neutralisiert.
            boolean erstAbFM = (zeit == null) || SYSCalendar.ermittleZeit(zeit.getTime()) == SYSConst.FM;
            boolean erstAbMO = (zeit == null) || erstAbFM || SYSCalendar.ermittleZeit(zeit.getTime()) == SYSConst.MO;
            boolean erstAbMI = (zeit == null) || erstAbMO || SYSCalendar.ermittleZeit(zeit.getTime()) == SYSConst.MI;
            boolean erstAbNM = (zeit == null) || erstAbMI || SYSCalendar.ermittleZeit(zeit.getTime()) == SYSConst.NM;
            boolean erstAbAB = (zeit == null) || erstAbNM || SYSCalendar.ermittleZeit(zeit.getTime()) == SYSConst.AB;
            boolean erstAbNA = (zeit == null) || erstAbAB || SYSCalendar.ermittleZeit(zeit.getTime()) == SYSConst.NA;
            boolean uhrzeitOK = (zeit == null) || (planung.getUhrzeit() != null && SYSCalendar.compareTime(planung.getUhrzeit(), zeit) > 0);

            if (treffer) {
                if (erstAbFM && planung.getNachtMo().compareTo(BigDecimal.ZERO) > 0) {
                    //OPDE.debug(bhp);
                    OPDE.debug("SYSConst.FM, " + planung.getNachtMo());
                    em.persist(new BHP(planung, stichtag, SYSConst.FM, planung.getNachtMo()));
                    numbhp++;
                }
                if (erstAbMO && planung.getMorgens().compareTo(BigDecimal.ZERO) > 0) {
                    OPDE.debug("SYSConst.MO, " + planung.getMorgens());
                    em.persist(new BHP(planung, stichtag, SYSConst.MO, planung.getMorgens()));
                    numbhp++;
                }
                if (erstAbMI && planung.getMittags().compareTo(BigDecimal.ZERO) > 0) {
                    OPDE.debug("SYSConst.MI, " + planung.getMittags());
                    em.persist(new BHP(planung, stichtag, SYSConst.MI, planung.getMittags()));
                    numbhp++;
                }
                if (erstAbNM && planung.getNachmittags().compareTo(BigDecimal.ZERO) > 0) {
                    OPDE.debug("SYSConst.NM, " + planung.getNachmittags());
                    em.persist(new BHP(planung, stichtag, SYSConst.NM, planung.getNachmittags()));
                    numbhp++;
                }
                if (erstAbAB && planung.getAbends().compareTo(BigDecimal.ZERO) > 0) {
                    OPDE.debug("SYSConst.AB, " + planung.getAbends());
                    em.persist(new BHP(planung, stichtag, SYSConst.AB, planung.getAbends()));
                    numbhp++;
                }
                if (erstAbNA && planung.getNachtAb().compareTo(BigDecimal.ZERO) > 0) {
                    OPDE.debug("SYSConst.NA, " + planung.getNachtAb());
                    em.persist(new BHP(planung, stichtag, SYSConst.NA, planung.getNachtAb()));
                    numbhp++;
                }
                if (uhrzeitOK && planung.getUhrzeit() != null) {
                    Date neuerStichtag = SYSCalendar.addTime2Date(stichtag, planung.getUhrzeit());
                    OPDE.debug("SYSConst.UZ, " + planung.getUhrzeitDosis() + ", " + DateFormat.getDateTimeInstance().format(neuerStichtag));
                    em.persist(new BHP(planung, neuerStichtag, SYSConst.UZ, planung.getUhrzeitDosis()));
                    numbhp++;
                }

                // Nun noch das LDatum in der Tabelle DFNPlanung neu setzen.
                planung.setLDatum(stichtag);
                em.merge(planung);
            }
        }
        OPDE.debug("Erzeugte BHPs: " + numbhp);
        return numbhp;
    }

}
