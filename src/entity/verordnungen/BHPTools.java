package entity.verordnungen;

import entity.Bewohner;
import entity.system.SYSPropsTools;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
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

    public static Date getMinDatum(Bewohner bewohner) {
        Date date;
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM BHP b WHERE b.bewohner = :bewohner ORDER BY b.soll");
        query.setParameter("bewohner", bewohner);
        query.setMaxResults(1);
        try {
            date = ((BHP) query.getSingleResult()).getSoll();
        } catch (Exception e) {
            date = new Date();
        }
        em.close();
        return date;
    }

    /**
     * Diese Methode erzeugt den Tagesplan für die Behandlungspflegen. Dabei werden alle aktiven Verordnungen geprüft, ermittelt ob sie am betreffenden Stichtag auch "dran" sind und dann
     * werden daraus Einträge in der BHP Tabelle erzeugt. Sie teilt sich die Arbeit mit der <code>erzeugen(EntityManager em, List<VerordnungPlanung> list, Date stichtag, Date zeit)</code> Methode
     *
     * @param em,    EntityManager Kontext
     * @param datum, das Datum für den die BHPs erzeugt werden sollen. <code>null</code>, wenn das heutige Datum gewünscht ist.
     * @return Anzahl der erzeugten BHPs
     */
    public static int erzeugen(EntityManager em, Date datum) throws Exception {

        String internalClassID = "nursingrecords.bhpimport";
        int numbhp = 0;


        if (datum == null) {
            datum = new Date();
        }
        Date stichtag = new Date(SYSCalendar.startOfDay(datum));
        GregorianCalendar gcStichtag = SYSCalendar.toGC(stichtag);

        // Datum, an dem der letzte BHP Gesamtimport erfolgte. Darf nur einmal am Tag gemacht werden.
        GregorianCalendar lastbhp = (GregorianCalendar) gcStichtag.clone();

        if (OPDE.getProps().containsKey("LASTBHPIMPORT")) {
            lastbhp = SYSCalendar.erkenneDatum(OPDE.getProps().getProperty("LASTBHPIMPORT"));
        } else {
            lastbhp.add(GregorianCalendar.DATE, -1); // einen Tag vorher, falls es noch nichts gibt
        }

        if (SYSCalendar.getDaysBetween(lastbhp, gcStichtag) != 1) {
//                SYSRunningClassesTools.endModule(me);
            throw new IndexOutOfBoundsException("Es kann kein BHPImport für dieses Datum durchgeführt werden.");
        }

        Query select = em.createQuery(" " +
                " SELECT vp FROM VerordnungPlanung vp " +
                " JOIN vp.verordnung v " +
                // nur die Verordnungen, die überhaupt gültig sind
                // das sind die mit Gültigkeit BAW oder Gültigkeit endet irgendwann in der Zukunft.
                // Das heisst, wenn eine Verordnung heute endet, dann wird sie dennoch eingetragen.
                // Also alle, die bis EINSCHLIEßLICH heute gültig sind.
                " WHERE v.situation IS NULL AND v.anDatum <= :andatum AND v.abDatum >= :abdatum " +
                // und nur diejenigen, deren Referenzdatum nicht in der Zukunft liegt.
                " AND vp.lDatum <= :ldatum AND v.bewohner.adminonly <> 2 " +
                " ORDER BY vp.bhppid ");

        // Diese Aufstellung ergibt mindestens die heute gültigen Einträge.
        // Wahrscheinlich jedoch mehr als diese. Anhand des LDatums müssen
        // die wirklichen Treffer nachher genauer ermittelt werden.

//        OPDE.info(SYSTools.getWindowTitle("BHPImport"));

        OPDE.debug("[BHPImport] Schreibe nach: " + OPDE.getUrl());

        select.setParameter("andatum", new Date(SYSCalendar.startOfDay(stichtag)));
        select.setParameter("abdatum", new Date(SYSCalendar.endOfDay(stichtag)));
        select.setParameter("ldatum", new Date(SYSCalendar.endOfDay(stichtag)));

        List<VerordnungPlanung> list = select.getResultList();

        numbhp = erzeugen(em, list, stichtag, true);

        OPDE.info("[BHPImport] Durchgeführt. Stichtag: " + DateFormat.getDateInstance().format(stichtag) + " Anzaghl erzeugter BHPs: "+numbhp);

//            SYSRunningClassesTools.endModule(me);

        SYSPropsTools.storeProp(em, "LASTBHPIMPORT", DateFormat.getDateInstance().format(stichtag));

//        } else {
//            OPDE.warn("BHPImport nicht abgeschlossen. Zugriffskonflikt.");
//        }
        return numbhp;
    }


    /**
     * Löscht alle <b>heutigen</b> nicht <b>abgehakten</b> BHPs für eine bestimmte Verordnung <b>ab</b> ab dem aktuellen Zeitpunkt.
     * Es wird die aktuelle Schicht (bzw. Zeit) ermittelt. Bei BHPs,
     * die sich auf eine bestimmte Uhrzeit beziehen, werden nur diejenigen gelöscht, die <b>größer gleich</b> der aktuellen Uhrzeit sind sind.
     *
     * @param em         EntityManager, in dessen Kontext das hier ablaufen soll.
     * @param verordnung um die es geht.
     */
    public static void aufräumen(EntityManager em, Verordnung verordnung) throws Exception {
        Date now = new Date();

        int sollZeit = SYSCalendar.ermittleZeit(now.getTime());

//        String sql = "DELETE b.* FROM BHP b INNER JOIN BHPPlanung bhp ON b.BHPPID = bhp.BHPPID " +
//                " WHERE bhp.VerID = ? AND Status = 0 AND Date(Soll)=Date(now()) AND " +
//                " (" +
//                "   ( " +
//                "       ( SZeit > ? )" +
//                "   ) " +
//                "   OR " +
//                "   (" +
//                "       ( SZeit = 0 AND Time(Soll) >= Time(?) )" +
//                "   )" +
//                " )";

        Query query = em.createQuery("SELECT b FROM BHP b WHERE b.verordnung = :verordnung AND b.soll >= :bofday AND b.soll <= :eofday");

        query.setParameter("verordnung", verordnung);
        query.setParameter("bofday", new Date(SYSCalendar.startOfDay()));
        query.setParameter("eofday", new Date(SYSCalendar.endOfDay()));

        List<BHP> bhps = query.getResultList();

        for (BHP bhp : bhps) {
            if (bhp.getSollZeit() > sollZeit || (bhp.getSollZeit() == 0 && SYSCalendar.compareTime(bhp.getSoll(), now) >= 0)) {
                em.remove(bhp);
            }
        }

    }

    /**
     * Hiermit werden alle BHP Einträge erzeugt, die sich aus den Verordnungen in der zugehörigen Liste ergeben. Die Liste wird aber vorher
     * noch darauf geprüft, ob sie auch wirklich an dem besagten Stichtag passt. Dabei gilt:
     * <ol>
     * <li>Alles was taeglich angeordnet ist (jeden Tag oder jeden soundsovielten Tag)</li>
     * <li>Alles was woechentlich ist und die Spalte (Attribut) mit dem aktuellen Wochentagsnamen größer null ist.</li>
     * <li>Monatliche Einträge. Aber nur dann, wenn
     * <ol>
     * <li>es der <i>n</i>.te Tag im Monat ist <br/><b>oder</b></li>
     * <li>oder der <i>n</i>.te Wochentag (z.B. Freitag) im Monat ist</li>
     * </ol>
     * </li>
     * </ol>
     * <p/>
     * Diese Methode kann von verschiednenen Seiten aufgerufen werden. Zum einen von der "anderen" erzeugen Methode, die einen vollständigen Tagesplan für
     * alle BWs erzeugt oder von dem Verordnungs Editor, der seinerseits nur eine einzige Verordnung nachtragen möchte. Auf jeden Fall kann die Liste <code>list</code>
     * auch Einträge enthalten, die unpassend sind. Sie dient nur der Vorauswahl und wird innerhalb dieser Methode dann genau geprüft. Sie "pickt" sich also
     * nur die passenden Elemente aus dieser Liste heraus.
     *
     * @param em        EntityManager Kontext
     * @param list      Liste der VerordnungPlanungen, die ggf. einzutragen sind.
     * @param stichtag  gibt an, für welches Datum die Einträge erzeugt werden. In der Regel ist das immer der aktuelle Tag.
     * @param ganzerTag true, dann wird für den ganzen Tag erzeugt. false, dann ab der aktuellen Zeit.
     * @return die Anzahl der erzeugten BHPs.
     */
    public static int erzeugen(EntityManager em, List<VerordnungPlanung> list, Date stichtag, boolean ganzerTag) {
        GregorianCalendar gcStichtag = SYSCalendar.toGC(stichtag);
        int maxrows = list.size();
        int numbhp = 0;

        long now = System.currentTimeMillis();
        byte aktuelleZeit = SYSCalendar.ermittleZeit(now);

//        Iterator<VerordnungPlanung> planungen = list.iterator();
        int row = 0;

        OPDE.debug("MaxRows: " + maxrows);

        for (VerordnungPlanung planung : list) {

            planung = em.merge(planung);
            em.lock(planung, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            em.lock(em.merge(planung.getVerordnung()), LockModeType.OPTIMISTIC_FORCE_INCREMENT);

            if (!SYSCalendar.isInFuture(planung.getLDatum()) && (planung.isTaeglich() || planung.isPassenderWochentag(stichtag) || planung.isPassenderTagImMonat(stichtag))) {

                row++;

                OPDE.debug("Fortschritt Vorgang: " + ((float) row / maxrows) * 100 + "%");
                OPDE.debug("==========================================");
                System.out.println("Planung: " + planung.getBhppid());
                OPDE.debug("BWKennung: " + planung.getVerordnung().getBewohner().getBWKennung());
                OPDE.debug("VerID: " + planung.getVerordnung().getVerid());


                boolean treffer = false;
                GregorianCalendar ldatum = SYSCalendar.toGC(planung.getLDatum());

                OPDE.debug("LDatum: " + DateFormat.getDateTimeInstance().format(planung.getLDatum()));
                OPDE.debug("Stichtag: " + DateFormat.getDateTimeInstance().format(stichtag));

                // Genaue Ermittlung der Treffer
                // =============================
                if (planung.isTaeglich()) {
                    OPDE.debug("Eine tägliche Planung");
                    // Dann wird das LDatum solange um die gewünschte Tagesanzahl erhöht, bis
                    // der stichtag getroffen wurde oder überschritten ist.
                    while (SYSCalendar.sameDay(ldatum, gcStichtag) < 0) {
                        OPDE.debug("ldatum liegt vor dem stichtag. Addiere tage: " + planung.getTaeglich());
                        ldatum.add(GregorianCalendar.DATE, planung.getTaeglich());
                    }
                    // Mich interssiert nur der Treffer, also die Punktlandung auf dem Stichtag
                    treffer = SYSCalendar.sameDay(ldatum, gcStichtag) == 0;
                } else if (planung.isWoechentlich()) {
                    OPDE.debug("Eine wöchentliche Planung");
                    while (SYSCalendar.sameWeek(ldatum, gcStichtag) < 0) {
                        OPDE.debug("ldatum liegt vor dem stichtag. Addiere Wochen: " + planung.getWoechentlich());
                        ldatum.add(GregorianCalendar.WEEK_OF_YEAR, planung.getWoechentlich());
                    }
                    // Ein Treffer ist es dann, wenn das Referenzdatum gleich dem Stichtag ist ODER es zumindest in der selben Kalenderwoche liegt.
                    // Da bei der Vorauswahl durch die Datenbank nur passende Wochentage überhaupt zugelassen wurden, muss das somit der richtige sein.
                    treffer = SYSCalendar.sameWeek(ldatum, gcStichtag) == 0;
                } else if (planung.isMonatlich()) {
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
                // Bei ganzerTag=true werden all diese booleans zu true und damit neutralisiert.
                boolean erstAbFM = ganzerTag || aktuelleZeit == SYSConst.FM;
                boolean erstAbMO = ganzerTag || erstAbFM || aktuelleZeit == SYSConst.MO;
                boolean erstAbMI = ganzerTag || erstAbMO || aktuelleZeit == SYSConst.MI;
                boolean erstAbNM = ganzerTag || erstAbMI || aktuelleZeit == SYSConst.NM;
                boolean erstAbAB = ganzerTag || erstAbNM || aktuelleZeit == SYSConst.AB;
                boolean erstAbNA = ganzerTag || erstAbAB || aktuelleZeit == SYSConst.NA;
                boolean uhrzeitOK = ganzerTag || (planung.getUhrzeit() != null && SYSCalendar.compareTime(planung.getUhrzeit(), new Date(now)) > 0);

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

                }
            } else {
                OPDE.debug("///////////////////////////////////////////////////////////");
                OPDE.debug("Folgende Planung wurde nicht angenommen: " + planung);
            }
        }
        OPDE.debug("Erzeugte BHPs: " + numbhp);
        return numbhp;
    }

}
