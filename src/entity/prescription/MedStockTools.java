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
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.*;

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
//        apv = calcProspectiveUPR(bestand);
//
//        result = open(em, bestand, apv);
//
//        return result;
//    }

    public static HashMap getStock4Printing(MedStock bestand) {
        OPDE.debug("BestandID: " + bestand.getID());

        HashMap hm = new HashMap();
        hm.put("bestand.darreichung", TradeFormTools.toPrettyString(bestand.getTradeForm()));

        String pzn = bestand.getPackage().getPzn() == null ? "??" : bestand.getPackage().getPzn();
        hm.put("bestand.packung.pzn", pzn);
        hm.put("bestand.bestid", bestand.getID());
        hm.put("bestand.eingang", bestand.getIN());
        hm.put("bestand.userkurz", bestand.getUser().getUID());
        hm.put("bestand.userlang", bestand.getUser().getFullname());
        hm.put("bestand.inventory.bewohnername", ResidentTools.getNameAndFirstname(bestand.getInventory().getResident()));
        hm.put("bestand.inventory.bewohnergebdatum", bestand.getInventory().getResident().getDOB());
        hm.put("bestand.inventory.bewohnerkennung", bestand.getInventory().getResident().getRIDAnonymous());

        return hm;
    }


//    public static String getBestandText4Print(MedBestand bestand) {
//        String result = "";
//
//        result = SYSPrint.EPL2_CLEAR_IMAGE_BUFFER;
//        result += SYSPrint.EPL2_labelformat(57, 19, 3);
//        result += SYSPrint.EPL2_print_ascii(5, 5, 0, SYSPrint.EPL2_FONT_7pt, 1, 1, false, DarreichungTools.toPrettyString(bestand.getTradeForm())); // bestand.getTradeForm().getMedProduct().getText() + " " + bestand.getTradeForm().getSubtext())
//        if (!SYSTools.catchNull(bestand.getPackage().getPzn()).equals("")) {
//            result += SYSPrint.EPL2_print_ascii(5, 30, 0, SYSPrint.EPL2_FONT_6pt, 1, 1, false, "PZN:" + bestand.getPackage().getPzn() + "  Datum:" + DateFormat.getDateInstance().format(bestand.getIN()) + " (" + bestand.getUser().getUID() + ")");
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
     * gets the sum of doses in the BHP (note: this figure is in usage unit)
     *
     * @param medStock
     * @return
     */
    public static BigDecimal getSumOfDosesInBHP(MedStock medStock) {
        BigDecimal result = BigDecimal.ZERO;
        for (MedStockTransaction tx : medStock.getStockTransaction()) {
            if (!tx.isPartOfCancelPair() && tx.isBHP()) {
                result = result.add(tx.getBhp().getDose());
            }
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
     * This method closes the given MedStock. It forces the current balance to zero by adding a system MedStockTransaction
     * to the existing list of TXs.
     *
     * @param em       persistence context to be used
     * @param medStock to be closed
     * @param text     for the closing TX
     * @param state    of the closing TX
     * @return Falls die Neuberechung gewünscht war, steht hier das geänderte, bzw. neu erstelle APV Objekt. null andernfalls.
     * @throws Exception
     */
    public static void close(EntityManager em, MedStock medStock, String text, short state) throws Exception {
        BigDecimal stocksum = getSum(medStock);
        MedStockTransaction finalTX = new MedStockTransaction(medStock, stocksum.negate(), state);
        finalTX.setText(text);
        medStock.getStockTransaction().add(finalTX);
        DateTime now = new DateTime();
        medStock.setOut(now.toDate());
        medStock.setUPR(calcEffectiveUPR(medStock));
        if (medStock.hasNext2Open()) {
            MedStock nextStock = medStock.getNextStock();
            nextStock.setOpened(now.plusSeconds(1).toDate());
            // The new UPR is the arithmetic mean of the old UPRs and the effective UPR for this package.
            // The prospective UPR uses a SQL AVG function over all persisted entities, therefore the old
            // UPR for the current medStock is also included, even though we just calculated a new
            // effective UPR. As we are speaking of an arithmetic mean, this does not have any impact on the whole process.
            BigDecimal upr = calcEffectiveUPR(medStock).add(calcProspectiveUPR(medStock)).divide(new BigDecimal(2), RoundingMode.HALF_UP);
            nextStock.setUPR(upr);

            OPDE.debug("NextStock: " + medStock.getNextStock().getID() + " will be opened now");
        } else {
            // Nothing to open next ?
            // Are there still stocks in this inventory ?
            if (MedInventoryTools.getNextToOpen(medStock.getInventory()) == null) {
                // No ??
                // Are there any prescriptions that needs to be closed now, because of the empty package ?
                for (Prescription prescription : PrescriptionTools.getPrescriptionsByInventory(medStock.getInventory())) {
                    if (prescription.isUntilEndOfPackage()) {
                        prescription = em.merge(prescription);
                        em.lock(prescription, LockModeType.OPTIMISTIC);
                        prescription.setTo(new Date());
                        prescription.setUserOFF(em.merge(OPDE.getLogin().getUser()));
                        prescription.setDocOFF(prescription.getDocON());
                        prescription.setHospitalOFF(prescription.getHospitalON());
                    }
                }
            }
        }
    }

    private static MedStockTransaction getStartTX(MedStock bestand) {
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
     * This method calculates the effective UPR as it transpired during the lifetime of that particular medstock.
     * It is vital, that this calculation is only done, when a package is empty. Otherwise the estimation
     * of the UPR is wrong.
     *
     * @param medstock, für den das Verhältnis neu berechnet werden soll.
     */
    public static BigDecimal calcEffectiveUPR(MedStock medstock) throws Exception {
        if (medstock.getTradeForm().getDosageForm().isUPR1()) {
            return BigDecimal.ONE;
        }

        OPDE.debug("<--- calcEffectiveUPR ");
        OPDE.debug("MedStock ID: " + medstock.getID());

        // this is the amount of content, which was in that package before it was opened
        // package unit
        BigDecimal startContent = getStartTX(medstock).getAmount();

        // usage unit
        BigDecimal sumOfAllAplications = getSumOfDosesInBHP(medstock);

        // Die Gaben aus der BHP sind immer in der Anwendungseinheit. Teilt man diese durch das
        // verwendete APV, erhält man das was rechnerisch in der Packung drin gewesen
        // sein soll. Das kann natürlich von dem realen Inhalt abweichen. Klebt noch was an
        // der Flaschenwand oder wurde was verworfen. Das APV steht ja für Anzahl der Anwendung im
        // Verhaltnis zur Packungseinheit 1. Wurden 100 Tropfen gegeben, bei einem APV von 20(:1)
        // Dann ergibt das einen rechnerischen Flascheninhalt von 5 ml.

        // The doses of the applications which have been calculated by the BHPs are always in
        // the unit of the usage.
        // When a package is empty, we know two things for sure:
        // 1. The startContent has been completely used up
        // 2. the sum of all applications (theoreticalSum) is what we could really get out of the bottle
        //
        // hence the effective UPR must have been
        //
        //          the startContent in the package unit
        //          --------------------------------------------
        //          the sum of all applications in the usage unit
        //
        BigDecimal effectiveUPR = startContent.divide(sumOfAllAplications, 4, BigDecimal.ROUND_UP);

        // Nimmt man den realen Inhalt und teil ihn durch den rechnerischen, dann gibt es drei Möglichkeiten
        // 1. Es wurde mehr gegeben als in der Packung drin war. Dann muss das ursprüngliche APV zu gross gewesen
        // sein. Die Division von realem Inhalt durch rechnerischem Inhalt ist kleiner 1 und somit wird auch
        // das apvNeu kleiner als das apvAlt.
        // 2. Es wurde genau so viel gegeben wie drin war. Dann war das apvAlt genau richtig. Der Divisor ist
        // dann 1 und apvNeu ist gleich apvAlt.
        // 3. Es wurde weniger gegeben als drin war. Dann war apvAlt zu klein und der Divisor (real durch rechnerisch) wird größer 0 und
        // der apvNeu wird größer als der apvAlt.


        BigDecimal newUPR;
        if (medstock.isReplaceUPR()) {
            newUPR = effectiveUPR;
            OPDE.debug("the UPR shall be replaced");
        } else {
            // The UPR which was assumed when this stock was stored
            BigDecimal oldUPR = medstock.getUPR();

            // if the deviation was too high (usually more than 20%), then the new UPR is discarded
            BigDecimal maxDeviation = new BigDecimal(Double.parseDouble(OPDE.getProps().getProperty("apv_korridor"))).divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_UP);
            BigDecimal deviation = oldUPR.divide(effectiveUPR).subtract(new BigDecimal(100)).abs();

            OPDE.debug("the deviation was: " + deviation);

            // Liegt der neue apv AUSSERHALB des maximalen Korridors, so wird er verworfen
            if (deviation.compareTo(maxDeviation) > 0) {
                newUPR = oldUPR;
            } else {
                newUPR = effectiveUPR;
            }
        }

        OPDE.debug("old UPR: " + medstock.getUPR());
        OPDE.debug("effective UPR: " + effectiveUPR);
        OPDE.debug("new UPR: " + newUPR);
        OPDE.debug("calcEffectiveUPR --->");
        return newUPR.compareTo(BigDecimal.ZERO) > 0 ? newUPR : BigDecimal.ONE;
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
            result += OPDE.lang.getString("misc.msg.PZN") + ": " + bestand.getPackage().getPzn() + ", ";
            result += MedPackageTools.GROESSE[bestand.getPackage().getSize()] + ", " + bestand.getPackage().getContent() + " " + SYSConst.UNITS[bestand.getTradeForm().getDosageForm().getPackUnit()] + " ";
            String zubereitung = SYSTools.catchNull(bestand.getTradeForm().getDosageForm().getPreparation());
            String anwtext = SYSTools.catchNull(bestand.getTradeForm().getDosageForm().getUsageText());
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

        result += ", APV: " + NumberFormat.getNumberInstance().format(stock.getUPR()) + " " + (stock.isReplaceUPR() ? "wird bei abschluss ersetzt" : "");

        if (stock.hasNext2Open()) {
            result += ", <b>" + OPDE.lang.getString(PnlInventory.internalClassID + ".nextstock") + ": " + stock.getNextStock().getID() + "</b>";
        }

        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

        result += "&nbsp;<font color=\"blue\">Eingang: " + df.format(stock.getIN()) + "</font>";
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

        result += ", APV: " + NumberFormat.getNumberInstance().format(stock.getUPR()) + " " + (stock.isReplaceUPR() ? "wird bei abschluss ersetzt" : "");
        return result;
    }

    public static MedStock getPreviousStock(MedStock fromThisOne) {
        MedInventory inventory = fromThisOne.getInventory();
        Collections.sort(inventory.getMedStocks());
        int index = inventory.getMedStocks().indexOf(fromThisOne);
        MedStock previous = null;
        if (index > 0) {
            previous = inventory.getMedStocks().get(index - 1);
        }
        return previous;
    }

    /**
     * calculates a starting UPR for a newly opened stock
     */
    public static BigDecimal calcProspectiveUPR(MedStock stock) {
        OPDE.debug("<--- calcProspectiveUPR");
        OPDE.debug("MedStock ID: " + stock.getID());
        BigDecimal upr = BigDecimal.ONE;
        if (stock.getTradeForm().getDosageForm().getState() == DosageFormTools.UPR_BY_RESIDENT) {
            OPDE.debug("UPR_BY_RESIDENT");
            upr = calcProspectiveUPR(stock.getInventory());
        } else if (stock.getTradeForm().getDosageForm().getState() == DosageFormTools.UPR_BY_TRADEFORM) {
            OPDE.debug("UPR_BY_TRADEFORM");
            upr = calcProspectiveUPR(stock.getTradeForm());
        } else {
            OPDE.debug("UPR1");
        }
        OPDE.debug("upr: " + upr);
        OPDE.debug("calcProspectiveUPR --->");
        return upr;
    }

    private static BigDecimal calcProspectiveUPR(MedInventory inventory) {
        BigDecimal upr = null;
        EntityManager em = OPDE.createEM();
        try {
            Query query = em.createQuery("SELECT AVG(s.upr) FROM MedStock s WHERE s.inventory = :inventory");
            query.setParameter("inventory", inventory);
            upr = (BigDecimal) query.getSingleResult();
        } catch (NoResultException nre) {
            upr = null;
        } catch (Exception e) {
            OPDE.fatal(e);
        } finally {
            em.close();
        }
        return upr;
    }

    /**
     * calculates the average of all
     *
     * @param tradeForm
     * @return
     */
    private static BigDecimal calcProspectiveUPR(TradeForm tradeForm) {
        EntityManager em = OPDE.createEM();
        BigDecimal upr = null;
        try {
            Query query = em.createQuery("SELECT AVG(s.upr) FROM MedStock s WHERE s.tradeform = :tradeform");
            query.setParameter("tradeform", tradeForm);
            upr = (BigDecimal) query.getSingleResult();
        } catch (NoResultException nre) {
            upr = null;
        } catch (Exception e) {
            OPDE.fatal(e);
        } finally {
            em.close();
        }
        return upr;
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
        html.append(SYSConst.html_h3("misc.msg.key"));
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
