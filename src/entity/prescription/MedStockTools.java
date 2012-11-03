package entity.prescription;

import entity.Station;
import entity.info.Resident;
import entity.info.ResidentTools;
import op.OPDE;
import op.care.med.inventory.PnlInventory;
import op.controlling.PnlControlling;
import op.tools.Pair;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.*;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 18.11.11
 * Time: 14:28
 * To change this template use File | Settings | File Templates.
 */
public class MedStockTools {
//    public static boolean apvNeuberechnung = true;

    public static ListCellRenderer getBestandOnlyIDRenderer() {
        return new ListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                String text;
                if (o == null) {
                    text = "<i>Keine Auswahl</i>";//SYSTools.toHTML("<i>Keine Auswahl</i>");
                } else if (o instanceof MedStock) {
                    text = ((MedStock) o).getID().toString();
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
            }
        };
    }

//    public static MedBestand findByVerordnungImAnbruch(Verordnung prescription) {
//        EntityManager em = OPDE.createEM();
//        Query query = em.createNamedQuery("MedBestand.findByDarreichungAndBewohnerImAnbruch");
//        query.setParameter("bewohner", prescription.getResident());
//        query.setParameter("darreichung", prescription.getTradeForm());
//
//        MedBestand result = null;
//
//        try {
//            result = (MedBestand) query.getSingleResult();
//        } catch (NoResultException nre) {
//            result = null;
//        } catch (Exception e) {
//            OPDE.fatal(e);
//            System.exit(1);
//        } finally {
//            em.close();
//        }
//
//        return result;
//    }


    /**
     * Sucht aus den den Beständen des Vorrats den angebrochenen heraus.
     *
     * @param inventory
     * @return der angebrochene Bestand. null, wenn es keinen gab.
     */
    public static MedStock getStockInUse(MedInventory inventory) {
        MedStock bestand = null;
        if (inventory != null && inventory.getMedStocks() != null) {
            Iterator<MedStock> itBestand = inventory.getMedStocks().iterator();
            while (itBestand.hasNext()) {
                MedStock b = itBestand.next();
                if (b.isOpened() && !b.isClosed()) {
                    bestand = b;
                    break;
                }
            }
        }
        return bestand;
    }

//
//    /**
//     * Bricht einen Bestand an. Berechnet das neue APV selbstständig.
//     *
//     * @param bestand
//     * @return der geänderte Bestand. Direkt persistiert und committed.
//     */
//    public static MedBestand open(EntityManager em, MedBestand bestand) throws Exception {
//        BigDecimal apv;
//        MedBestand result = null;
//
//        apv = getAPV4(bestand);
//
//        result = open(em, bestand, apv);
//
//        return result;
//    }

    /**
     * Bricht einen Bestand an.
     *
     * @param stock
     * @return
     */
    public static void open(MedStock stock, BigDecimal apv) throws Exception {
        if (apv == null) {
            throw new NullPointerException("apv must not be null");
        }

        stock.setOpened(new Date());
        if (apv.equals(BigDecimal.ZERO)) {
            // Das hier verhindert Division by Zero Exceptions.
            apv = BigDecimal.ONE;
        }
        stock.setAPV(apv);
    }

    public static HashMap getStock4Printing(MedStock bestand) {
        OPDE.debug("BestandID: " + bestand.getID());

        HashMap hm = new HashMap();
        hm.put("bestand.darreichung", TradeFormTools.toPrettyString(bestand.getTradeForm()));

        String pzn = bestand.getPackage().getPzn() == null ? "??" : bestand.getPackage().getPzn();
        hm.put("bestand.packung.pzn", pzn);
        hm.put("bestand.bestid", bestand.getID());
        hm.put("bestand.eingang", bestand.getEin());
        hm.put("bestand.userkurz", bestand.getUser().getUID());
        hm.put("bestand.userlang", bestand.getUser().getFullname());
        hm.put("bestand.inventory.bewohnername", ResidentTools.getNameAndFirstname(bestand.getInventory().getResident()));
        hm.put("bestand.inventory.bewohnergebdatum", bestand.getInventory().getResident().getDOB());
        hm.put("bestand.inventory.bewohnerkennung", bestand.getInventory().getResident().getRID());

        return hm;
    }


//    public static String getBestandText4Print(MedBestand bestand) {
//        String result = "";
//
//        result = SYSPrint.EPL2_CLEAR_IMAGE_BUFFER;
//        result += SYSPrint.EPL2_labelformat(57, 19, 3);
//        result += SYSPrint.EPL2_print_ascii(5, 5, 0, SYSPrint.EPL2_FONT_7pt, 1, 1, false, DarreichungTools.toPrettyString(bestand.getTradeForm())); // bestand.getTradeForm().getMedProduct().getText() + " " + bestand.getTradeForm().getSubtext())
//        if (!SYSTools.catchNull(bestand.getPackage().getPzn()).equals("")) {
//            result += SYSPrint.EPL2_print_ascii(5, 30, 0, SYSPrint.EPL2_FONT_6pt, 1, 1, false, "PZN:" + bestand.getPackage().getPzn() + "  Datum:" + DateFormat.getDateInstance().format(bestand.getEin()) + " (" + bestand.getUser().getUID() + ")");
//        }
//
//        result += SYSPrint.EPL2_print_ascii(5, 55, 0, SYSPrint.EPL2_FONT_12pt, 2, 2, true, Long.toString(bestand.getID()));
//        result += SYSPrint.EPL2_print_ascii(5, 107, 0, SYSPrint.EPL2_FONT_6pt, 1, 1, false, BewohnerTools.getNameAndFirstname(bestand.getInventory().getResident()));
//        result += SYSPrint.EPL2_print_ascii(5, 122, 0, SYSPrint.EPL2_FONT_6pt, 1, 1, false, BewohnerTools.getBWLabel2(bestand.getInventory().getResident()));
//
//        result += SYSPrint.EPL2_PRINT;
//
//
//        // Konvertierung auf PC850 weg. Umlauten.
//        try {
//            byte[] conv = result.getBytes("Cp850");
//            result = new String(conv);
//        } catch (UnsupportedEncodingException ex) {
//            //ex.printStackTrace();
//            new DlgException(ex);
//        }
//        return result;
//    }


    public static BigDecimal getSum(MedStock bestand) {
        BigDecimal result = BigDecimal.ZERO;

        for (MedStockTransaction buchung : bestand.getStockTransaction()) {
            result = result.add(buchung.getAmount());
        }
        return result;
    }

    /**
     * Ermittelt die Menge, die in einer Packung noch enthalten ist.
     *
     * @param stock die entsprechende Packung
     * @return die Summe in der Packungs Einheit.
     */
    public static BigDecimal getSum(EntityManager em, MedStock stock) throws Exception {
        BigDecimal result;

        Query query = em.createQuery(" " +
                " SELECT SUM(tx.amount) " +
                " FROM MedStock st " +
                " JOIN st.stockTransaction tx " +
                " WHERE st = :stock ");

        try {
            query.setParameter("stock", stock);
            result = (BigDecimal) query.getSingleResult();
        } catch (NoResultException nre) {
            result = BigDecimal.ZERO;
        }

        return result;
    }

    /**
     * Schliesst einen Bestand ab. Erzeugt dazu direkt eine passende Abschlussbuchung, die den Bestand auf null bringt.
     * <p/>
     *
     * @param stock, der abzuschliessen ist.
     * @param text,  evtl. gewünschter Text für die Abschlussbuchung
     * @param state, für die Abschlussbuchung
     * @return Falls die Neuberechung gewünscht war, steht hier das geänderte, bzw. neu erstelle APV Objekt. null andernfalls.
     * @throws Exception
     */
    public static void close(EntityManager em, MedStock stock, String text, short state) throws Exception {
        BigDecimal stocksum = getSum(stock);
        MedStockTransaction finalTX = new MedStockTransaction(stock, stocksum.negate(), state);
        finalTX.setText(text);
        stock.getStockTransaction().add(finalTX);
        stock.setOut(new Date());
//        stock.setNextStock(null);

        if (stock.hasNext2Open()) {
            stock.setOpened(new Date());
            BigDecimal apv = calcAPV(stock);
            if (apv.equals(BigDecimal.ZERO)) {
                apv = BigDecimal.ONE;
            }
            stock.setAPV(apv);

//            MedStockTools.open(stock.getNextStock(), calcAPV(stock));
            OPDE.debug("NextStock: " + stock.getNextStock().getID() + " will be opened now");
        } else {

            // Nothing to open next ?
            // Are there still stocks in this inventory ?
            if (MedInventoryTools.getNextToOpen(stock.getInventory()) == null) {
                // No ??
                // Are there any prescriptions that needs to be closed now, because of the empty package ?
                for (Prescription verordnung : PrescriptionTools.getPrescriptionsByInventory(stock.getInventory())) {
                    if (verordnung.isTillEndOfPackage()) {
                        verordnung = em.merge(verordnung);
                        em.lock(verordnung, LockModeType.OPTIMISTIC);
                        verordnung.setTo(new Date());
                        verordnung.setUserOFF(em.merge(OPDE.getLogin().getUser()));
                        verordnung.setDocOFF(verordnung.getDocON());
                        verordnung.setHospitalOFF(verordnung.getHospitalON());
                        BHPTools.cleanup(em, verordnung);
                    }
                }
            }
        }
    }

    private static MedStockTransaction getAnfangsBuchung(MedStock bestand) {
        MedStockTransaction result = null;
        for (MedStockTransaction buchung : bestand.getStockTransaction()) {
            if (buchung.getState() == MedStockTransactionTools.STATE_CREDIT) {
                result = buchung;
                break;
            }
        }
        return result;
    }

    /**
     * @param stock, für den das Verhältnis neu berechnet werden soll.
     */
    public static BigDecimal calcAPV(MedStock stock) throws Exception {

        BigDecimal apvNeu = BigDecimal.ONE;

        if (stock.getTradeForm().getDosageForm().getState() != DosageFormTools.APV1) {

            // Menge in der Packung (in der Packungseinheit). Also das, was wirklich in der Packung am Anfang
            // drin war. Meist das, was auf der Packung steht.
            BigDecimal inhaltZuBeginn = getAnfangsBuchung(stock).getAmount();

            // Das APV, das bei diesem Bestand angenommen wurde.
            BigDecimal apvAlt = stock.getAPV();

            // Zur Verhinderung von Division durch 0
            if (apvAlt.equals(BigDecimal.ZERO)) {
                apvAlt = BigDecimal.ONE;
            }

            BigDecimal summeBHPDosis = getSum(stock);

            // Die Gaben aus der BHP sind immer in der Anwendungseinheit. Teilt man diese durch das
            // verwendete APV, erhält man das was rechnerisch in der Packung drin gewesen
            // sein soll. Das kann natürlich von dem realen Inhalt abweichen. Klebt noch was an
            // der Flaschenwand oder wurde was verworfen. Das APV steht ja für Anzahl der Anwendung im
            // Verhaltnis zur Packungseinheit 1. Wurden 100 Tropfen gegeben, bei einem APV von 20(:1)
            // Dann ergibt das einen rechnerischen Flascheninhalt von 5 ml.
            BigDecimal inhaltRechnerisch = summeBHPDosis.divide(apvAlt, 4, BigDecimal.ROUND_UP);
            // Zur Verhinderung von Division durch 0
            if (inhaltRechnerisch.equals(BigDecimal.ZERO)) {
                inhaltRechnerisch = inhaltZuBeginn;
            }

            // Nimmt man den realen Inhalt und teil ihn durch den rechnerischen, dann gibt es drei Möglichkeiten
            // 1. Es wurde mehr gegeben als in der Packung drin war. Dann muss das ursprüngliche APV zu gross gewesen
            // sein. Die Division von realem Inhalt durch rechnerischem Inhalt ist kleiner 1 und somit wird auch
            // das apvNeu kleiner als das apvAlt.
            // 2. Es wurde genau so viel gegeben wie drin war. Dann war das apvAlt genau richtig. Der Divisor ist
            // dann 1 und apvNeu ist gleich apvAlt.
            // 3. Es wurde weniger gegeben als drin war. Dann war apvAlt zu klein und der Divisor (real durch rechnerisch) wird größer 0 und
            // der apvNeu wird größer als der apvAlt.
            apvNeu = inhaltZuBeginn.divide(inhaltRechnerisch, 4, BigDecimal.ROUND_UP).multiply(apvAlt);


            // Zu große APV Abweichungen verhindern. Alle außerhalb eines 20% Korridors wird ignoriert.
            BigDecimal apvkorridor = new BigDecimal(Double.parseDouble(OPDE.getProps().getProperty("apv_korridor"))).divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_UP);
            BigDecimal halbeBreite = apvAlt.multiply(apvkorridor);
            BigDecimal korridorUnten = apvAlt.subtract(halbeBreite);
            BigDecimal korridorOben = apvAlt.add(halbeBreite);

            // Liegt der neue apv AUSSERHALB des maximalen Korridors, so wird er verworfen
            if (apvAlt.compareTo(korridorUnten) < 0 || korridorOben.compareTo(apvNeu) < 0) {
                apvNeu = apvAlt;
            }

        }
        return apvNeu;
    }

    /**
     * Diese Methode bucht auf einen Bestand immer genau soviel drauf oder runter, damit er auf
     * dem gewünschten soll landet.
     *
     * @param bestand um die es geht.
     * @param soll    gewünschter Endbestand
     * @param em      EntityManager in dessen Kontext das ganze abläuft
     * @param text    für die Buchung
     * @param status  für die Buchung
     * @return die Korrektur Buchung
     * @throws Exception
     */
    public static MedStockTransaction setStockTo(EntityManager em, MedStock bestand, BigDecimal soll, String text, short status) throws Exception {
        MedStockTransaction result = null;
        bestand = em.merge(bestand);

        BigDecimal bestandSumme = getSum(em, bestand);

        if (!bestandSumme.equals(soll)) {
            BigDecimal korrektur;
            if (bestandSumme.compareTo(BigDecimal.ZERO) <= 0) {
                korrektur = bestandSumme.abs().add(soll);
            } else {
                korrektur = bestandSumme.negate().add(soll);
            }

            // passende Buchung anlegen.
            result = em.merge(new MedStockTransaction(bestand, korrektur, status));
            result.setText(text);
        }
        return result;
    }

    public static String getTextASHTML(MedStock bestand) {
        String result = "";
        result += "<font color=\"blue\"><b>" + bestand.getTradeForm().getMedProduct().getBezeichnung() + " " + bestand.getTradeForm().getSubtext() + ", ";

        if (!SYSTools.catchNull(bestand.getPackage().getPzn()).equals("")) {
            result += "PZN: " + bestand.getPackage().getPzn() + ", ";
            result += MedPackageTools.GROESSE[bestand.getPackage().getSize()] + ", " + bestand.getPackage().getContent() + " " + DosageFormTools.EINHEIT[bestand.getTradeForm().getDosageForm().getPackUnit()] + " ";
            String zubereitung = SYSTools.catchNull(bestand.getTradeForm().getDosageForm().getPreparation());
            String anwtext = SYSTools.catchNull(bestand.getTradeForm().getDosageForm().getUsageTex());
            result += zubereitung.equals("") ? anwtext : (anwtext.equals("") ? zubereitung : zubereitung + ", " + anwtext);
            result += "</b></font>";
        }

        return result;
    }

    public static String getAsHTML(MedStock stock) {
        String result = "";

        String htmlcolor = stock.isClosed() ? "gray" : "red";

        result += "<font face =\"" + SYSConst.ARIAL14.getFamily() + "\">";
        result += "<font color=\"" + htmlcolor + "\"><b><u>" + stock.getID() + "</u></b></font>&nbsp; ";
        result += TradeFormTools.toPrettyString(stock.getTradeForm());

        if (stock.hasPackage()) {
            result += ", " + MedPackageTools.toPrettyString(stock.getPackage());
        }

        result += ", APV: " + NumberFormat.getNumberInstance().format(stock.getAPV());

        if (stock.hasNext2Open()) {
            result += ", <b>" + OPDE.lang.getString(PnlInventory.internalClassID + ".nextstock") + ": " + stock.getNextStock().getID() + "</b>";
        }

        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

        result += "&nbsp;<font color=\"blue\">Eingang: " + df.format(stock.getEin()) + "</font>";
        if (stock.isOpened()) {
            result += "&nbsp;<font color=\"green\">Anbruch: " + df.format(stock.getOpened()) + "</font>";
            if (stock.isClosed()) {
                result += "&nbsp;<font color=\"black\">Ausgebucht: " + df.format(stock.getOut()) + "</font>";
            }
        }
        result += "</font>";

        return result;

    }


    public static String getCompactHTML(MedStock stock) {
        String result = "";
        result += "<b>" + stock.getID() + "</b>&nbsp;";
        result += TradeFormTools.toPrettyString(stock.getTradeForm());

        if (stock.hasPackage()) {
            result += ", " + MedPackageTools.toPrettyString(stock.getPackage());
        }

        result += ", APV: " + NumberFormat.getNumberInstance().format(stock.getAPV());
        return result;
    }


//    /**
//     * Setzt für einen Bestand <b>alle</b> Buchungen zurück, bis auf die Anfangsbuchung.
//     *
//     * @param bestand
//     */
//    public static void zuruecksetzen(MedBestand bestand) {
//        EntityManager em = OPDE.createEM();
//        try {
//            em.getTransaction().begin();
//            bestand = em.merge(bestand);
//            em.lock(bestand, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
//            Query query = em.createQuery("DELETE FROM MedStockTransaction b WHERE b.bestand = :bestand AND b.status <> :notStatus ");
//            query.setParameter("bestand", bestand);
//            query.setParameter("notStatus", MedStockTransactionTools.STATE_CREDIT);
//            query.executeUpdate();
//            em.getTransaction().commit();
//        } catch (OptimisticLockException ole) {
//            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Wurde zwischenzeitlich von jemand anderem geändert.", DisplayMessage.IMMEDIATELY, OPDE.getErrorMessageTime()));
//            em.getTransaction().rollback();
//        } catch (Exception ex) {
//            if (em.getTransaction().isActive()) {
//                em.getTransaction().rollback();
//            }
//            OPDE.fatal(ex);
//        } finally {
//            em.close();
//        }
//    }

//    public static boolean hasAbgesetzteBestaende(BHP bhp) {
//        boolean result = false;
//
//        EntityManager em = OPDE.createEM();
//
//        try {
//            Query query = em.createQuery(" " +
//                    " SELECT b FROM MedBestand b " +
//                    " JOIN b.buchungen bu " +
//                    " WHERE bu.bhp = :bhp " +
//                    " AND b.aus < '9999-12-31 23:59:59'");
//            query.setParameter("bhp", bhp);
//            result = !query.getResultList().isEmpty();
//        } catch (Exception ex) {
//            OPDE.fatal(ex);
//        } finally {
//            em.close();
//        }
//        return result;
//    }

    /**
     * Ermittelt für einen bestimmten Bestand ein passendes APV.
     */
    public static BigDecimal getAPV4(MedStock bestand) {

        BigDecimal apv = BigDecimal.ONE;

        if (bestand.getTradeForm().getDosageForm().getState() == DosageFormTools.APV_PER_BW) {
            apv = getAPV4(bestand.getInventory());
        } else if (bestand.getTradeForm().getDosageForm().getState() == DosageFormTools.APV_PER_DAF) {
            apv = getAPV4(bestand.getTradeForm());
        }

        return apv;
    }

    public static BigDecimal getAPV4(MedInventory inventory) {
        BigDecimal apv;

        if (!inventory.getMedStocks().isEmpty()) {
            apv = BigDecimal.ZERO;
            for (MedStock bestand : inventory.getMedStocks()) {
                apv.add(bestand.getAPV());
            }
            // Arithmetisches Mittel
            apv = apv.divide(new BigDecimal(inventory.getMedStocks().size()), BigDecimal.ROUND_UP);
        } else {
            apv = BigDecimal.ONE;
        }

//
//        EntityManager em = OPDE.createEM();
//        BigDecimal result = null;
//
//        try {
//            Query query = em.createQuery("SELECT AVG(b.apv) FROM MedBestand b WHERE b.inventory = :inventory");
//            query.setParameter("inventory", inventory);
//            result = (BigDecimal) query.getSingleResult();
//        } catch (NoResultException nre) {
//            result = BigDecimal.ONE; // Im Zweifel ist das 1
//        } catch (Exception e) {
//            OPDE.fatal(e);
//        } finally {
//            em.close();
//        }
        return apv;
    }

    public static BigDecimal getAPV4(TradeForm darreichung) {

        BigDecimal apv = null;

        if (!darreichung.getMedStocks().isEmpty()) {
            apv = BigDecimal.ZERO;
            for (MedStock bestand : darreichung.getMedStocks()) {
                apv.add(bestand.getAPV());
            }
            // Arithmetisches Mittel
            apv = apv.divide(new BigDecimal(darreichung.getMedStocks().size()), BigDecimal.ROUND_UP);
        } else {
            // Im Zweifel ist das 1
            apv = BigDecimal.ONE;
        }

//        EntityManager em = OPDE.createEM();
//        BigDecimal result = null;
//
//
//
//        try {
//            Query query = em.createQuery("SELECT AVG(b.apv) FROM MedBestand b WHERE b.darreichung = :darreichung");
//            query.setParameter("darreichung", darreichung);
//            result = (BigDecimal) query.getSingleResult();
//        } catch (NoResultException nre) {
//            result = BigDecimal.ONE; // Im Zweifel ist das 1
//        } catch (Exception e) {
//            OPDE.fatal(e);
//        } finally {
//            em.close();
//        }
        return apv;
    }

    public static String getListForMedControl(Station station, Closure progress) {
        StringBuilder html = new StringBuilder(1000);
        int p = -1;
        progress.execute(new Pair<Integer, Integer>(p, 100));
        Resident prevResident = null;

        EntityManager em = OPDE.createEM();
        String jpql = " " +
                " SELECT b FROM MedStock b " +
                " WHERE b.inventory.resident.station = :station AND b.inventory.resident.adminonly <> 2 " +
                " AND b.opened < :opened AND b.out = :tfn " +
                " ORDER BY b.inventory.resident.name, b.inventory.resident.firstname, b.inventory.text, b.opened ";

        Query query = em.createQuery(jpql);
        query.setParameter("opened", new Date());
        query.setParameter("tfn", SYSConst.DATE_UNTIL_FURTHER_NOTICE);
        query.setParameter("station", station);
        List<MedStock> list = query.getResultList();
        em.close();

        StringBuilder table = new StringBuilder(1000);
        DateFormat df = DateFormat.getDateInstance();

        html.append(SYSConst.html_h1(PnlControlling.internalClassID + ".drugs.controllist"));
        html.append(SYSConst.html_h2(OPDE.lang.getString("misc.msg.subdivision") + ": " + station.getName()));
        html.append(SYSConst.html_h3(PnlControlling.internalClassID + ".drugs.controllist.key"));
        html.append(SYSConst.html_ul(
                SYSConst.html_li(PnlControlling.internalClassID + ".drugs.controllist.key.1") +
                        SYSConst.html_li(PnlControlling.internalClassID + ".drugs.controllist.key.2") +
                        SYSConst.html_li(PnlControlling.internalClassID + ".drugs.controllist.key.3") +
                        SYSConst.html_li(PnlControlling.internalClassID + ".drugs.controllist.key.4") +
                        SYSConst.html_li(PnlControlling.internalClassID + ".drugs.controllist.key.5") +
                        SYSConst.html_li(PnlControlling.internalClassID + ".drugs.controllist.key.6") +
                        SYSConst.html_li(PnlControlling.internalClassID + ".drugs.controllist.key.7") +
                        SYSConst.html_li(PnlControlling.internalClassID + ".drugs.controllist.key.8")
        ));

        table.append(SYSConst.html_table_tr(
                SYSConst.html_table_th("misc.msg.resident") +
                        SYSConst.html_table_th(PnlInventory.internalClassID + ".search.stockid") +
                        SYSConst.html_table_th("misc.msg.medication") +
                        SYSConst.html_table_th("misc.msg.opened") +
                        SYSConst.html_table_th("#1") +
                        SYSConst.html_table_th("#2") +
                        SYSConst.html_table_th("#3") +
                        SYSConst.html_table_th("#4") +
                        SYSConst.html_table_th("#5") +
                        SYSConst.html_table_th("#6") +
                        SYSConst.html_table_th("#7") +
                        SYSConst.html_table_th("#8")
        ));

        p = 0;
        int zebra = 0;
        for (MedStock bestand : list) {
            progress.execute(new Pair<Integer, Integer>(p, list.size()));
            p++;

            if (prevResident != bestand.getInventory().getResident()) {
                zebra++;
                prevResident = bestand.getInventory().getResident();
            }

            table.append(SYSConst.html_table_tr(
                    SYSConst.html_table_td(ResidentTools.getTextCompact(bestand.getInventory().getResident())) +
                            SYSConst.html_table_td(bestand.getID().toString()) +
                            SYSConst.html_table_td(TradeFormTools.toPrettyString(bestand.getTradeForm())) +
                            SYSConst.html_table_td(df.format(bestand.getOpened())) +
                            SYSConst.html_table_td("&nbsp;") +
                            SYSConst.html_table_td("&nbsp;") +
                            SYSConst.html_table_td("&nbsp;") +
                            SYSConst.html_table_td("&nbsp;") +
                            SYSConst.html_table_td("&nbsp;") +
                            SYSConst.html_table_td("&nbsp;") +
                            SYSConst.html_table_td("&nbsp;") +
                            SYSConst.html_table_td("&nbsp;")
                    , zebra % 2 == 0));
        }
        html.append(SYSConst.html_table(table.toString(), "1"));
        return html.toString();
    }
}
