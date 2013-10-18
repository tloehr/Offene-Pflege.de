package entity.prescription;

import entity.info.Resident;
import op.OPDE;
import op.care.med.inventory.DlgCloseStock;
import op.care.med.inventory.PnlInventory;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 18.11.11
 * Time: 16:25
 * To change this template use File | Settings | File Templates.
 */
public class MedInventoryTools {

    public static ListCellRenderer getInventoryRenderer() {
        return new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                String text;
                if (o == null) {
                    text = SYSTools.toHTML("<i>Keine Auswahl</i>");
                } else if (o instanceof MedInventory) {
                    MedInventory inventory = (MedInventory) o;
                    text = inventory.getText();
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
            }
        };
    }

    public static String getInventoryAsHTML(MedInventory inventory) {
        String result = "";

        String htmlcolor = inventory.isClosed() ? "gray" : "blue";

        result += "<font face =\"" + SYSConst.ARIAL14.getFamily() + "\">";
        result += "<font color=\"" + htmlcolor + "\"><b><u>" + inventory.getID() + "</u></b></font>&nbsp; ";
        result += inventory.getText();


        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

        result += "<br/><font color=\"blue\">Eingang: " + df.format(inventory.getFrom()) + "</font>";
        if (inventory.isClosed()) {
            result += "<br/><font color=\"green\">Abschluss: " + df.format(inventory.getTo()) + "</font>";
        }

        result += "</font>";
        return result;

    }

//    public static BigDecimal getSum(MedInventory inventory) {
////        long timeStart = System.currentTimeMillis();
//        BigDecimal result = BigDecimal.ZERO;
//        for (MedStock bestand : inventory.getMedStocks()) {
//            BigDecimal summe = MedStockTools.getSum(bestand);
//            result = result.add(summe);
//        }
////        long time2 = System.currentTimeMillis();
////        OPDE.debug("MedInventoryTools.getSumme(): " + (time2 - timeStart) + " millis");
//        return result;
//    }

    public static BigDecimal getSum(MedInventory inventory) throws Exception {
        BigDecimal result = BigDecimal.ZERO;
        for (MedStock stock : inventory.getMedStocks()) {
            if (!stock.isClosed()) {
                BigDecimal summe = MedStockTools.getSum(stock);
                result = result.add(summe);
            }
        }
        return result;
    }

    /**
     * Bucht eine Menge aus einem Vorrat aus, ggf. zugehörig zu einer BHP. Übersteigt die Entnahme-Menge den
     * Restbestband, dann wird entweder
     * <ul>
     * <li>wenn der nächste Bestand <b>nicht</b> bekannt ist (<code>bestand.hasNext2Open() = <b>false</b></code>) &rarr; der Bestand trotzdem weiter gebucht. Bis ins negative.</li>
     * <li>wenn der nächste Bestand <b>doch schon</b> bekannt ist (<code>bestand.hasNext2Open() = <b>true</b></code>)  &rarr; ein neuer Bestand wird angebrochen.</li>
     * </ul>
     * Ist <b>keine</b> Packung im Anbruch, dann wird eine Exception geworfen. Das kann aber eingentlich nicht passieren. Es sei denn jemand hat von Hand
     * an den Datenbank rumgespielt.
     *
     * @param em       der EntityManager der verwendet wird
     * @param quantity die gewünschte Entnahmemenge
     * @param bhp      BHP aufgrund dere dieser Buchungsvorgang erfolgt.
     */
    public static void withdraw(EntityManager em, MedInventory inventory, BigDecimal quantity, BHP bhp) throws Exception {
        OPDE.debug("withdraw/5: inventory: " + inventory);
        if (inventory == null) {
            throw new Exception("No MedStock is currently in use");
        }
        MedStock stock = MedStockTools.getStockInUse(inventory);
        if (stock.getTradeForm().getDosageForm().isDontCALC()){
            OPDE.debug("withdraw/5: no calculation necessary. is ointment or something like that");
            return;
        }

        if (stock.getTradeForm().getDosageForm().isUPRn()) {
            BigDecimal upr = stock.getTradeForm().getUPR() != null ? stock.getTradeForm().getUPR() : stock.getUPR();
            quantity = quantity.divide(upr, 4, BigDecimal.ROUND_HALF_UP);
        }

        OPDE.debug("withdraw/5: amount: " + quantity);
        withdraw(em, stock, quantity, bhp);
    }


    /**
     * Bricht von allen geschlossenen das nächste (im Sinne des Einbuchdatums) an. Funktioniert nur bei Vorräten, die z.Zt. keine
     * angebrochenen Bestände haben.
     *
     * @param inventory
     * @return der neu angebrochene Bestand. null, wenns nicht geklappt hat.
     */
    public static MedStock openNext(MedInventory inventory) {
        MedStock result = null;

        java.util.List<MedStock> list = new ArrayList(inventory.getMedStocks());
        Collections.sort(list);

        for (MedStock medStock : list) {
            if (medStock.getOut().equals(SYSConst.DATE_UNTIL_FURTHER_NOTICE) && medStock.getOpened().equals(SYSConst.DATE_UNTIL_FURTHER_NOTICE)) {
                medStock.setOpened(new Date());
                result = medStock;
                break;
            }
        }

        return result;
    }


    public static BigDecimal getSum(EntityManager em, MedInventory inventory) throws Exception {
        BigDecimal result = BigDecimal.ZERO;
        for (MedStock stock : MedStockTools.getAll(inventory)) {
            if (!stock.isClosed()) {
                BigDecimal summe = MedStockTools.getSum(em, stock);
                result = result.add(summe);
            }
        }
        return result;
    }

    /**
     * <ul>
     * <li><i>If there is more than we need in this package,</i> then we simply withdraw whats needed.</li>
     * <li><i>We need more than we have in this package,</i>
     * <ol>
     * <li><i>but we already know that there is another one to be used.</i> Then we use up the current package, open the next one and take the rest from there.</li>
     * <li><i>but we dont know about a new one yet.</i> Then we take from the current package, even if we risk a negative balance.</li>
     * <li><i>and we know, that this package should be the last one.</i> Then we take from the current package, even if we risk a negative balance. The package is closed automatically afterwards.</li>
     * </ol>
     * </li>
     * <li><i>The package contains the exact amount of whats needed,</i>
     * <ol>
     * <li><i>and we already know that there is another one to be used.</i> Then we use up the current package and open the next one.</li>
     * <li><i>but we dont know about a new one yet.</i> Nothing to be done now.</li>
     * <li><i>and we know, that this package should be the last one.</i> Then we take from the current package, using it up. The package is closed automatically afterwards.</li>
     * </ol>
     * </li>
     * </ul>
     *
     * @param em
     * @param activeStock
     * @param quantity
     * @param bhp
     * @throws Exception
     */
    private static void withdraw(EntityManager em, MedStock activeStock, BigDecimal quantity, BHP bhp) throws Exception {
        if (quantity.compareTo(BigDecimal.ZERO) < 0) {
            OPDE.fatal(new NumberFormatException("withdraw/4: negative quantity"));
        }
        MedStock stock = em.merge(activeStock);
        em.lock(stock, LockModeType.OPTIMISTIC);

        OPDE.debug("withdraw/4: MedStock: " + stock);

        BigDecimal stockSum = MedStockTools.getSum(stock); // wieviel der angebrochene Bestand noch hergibt.


        BigDecimal withdrawal = quantity;
        if (stockSum.compareTo(quantity) < 0 && stock.hasNext2Open()) {
            withdrawal = stockSum;
        }

        // The TX for this turn
        MedStockTransaction tx = em.merge(new MedStockTransaction(stock, withdrawal.negate(), bhp));
        stock.getStockTransaction().add(tx);
        bhp.getStockTransaction().add(tx);
        OPDE.debug("withdraw/4: tx: " + tx);

        if (stockSum.compareTo(quantity) == 0) {
            if (stock.isToBeClosedSoon()) {
                MedStockTools.close(em, stock, OPDE.lang.getString(DlgCloseStock.internalClassID + ".TX.AUTOCLOSED_EMPTY_PACKAGE"), MedStockTransactionTools.STATE_EDIT_EMPTY_NOW);
            }
        } else if (stockSum.compareTo(quantity) < 0) {
            if (!stock.hasNext2Open() && stock.isToBeClosedSoon()) {
                MedStockTools.close(em, stock, OPDE.lang.getString(DlgCloseStock.internalClassID + ".TX.AUTOCLOSED_EMPTY_PACKAGE"), MedStockTransactionTools.STATE_EDIT_EMPTY_NOW);
            } else if (stock.hasNext2Open()) {
                MedStock nextStock = MedStockTools.close(em, stock, OPDE.lang.getString(DlgCloseStock.internalClassID + ".TX.AUTOCLOSED_EMPTY_PACKAGE"), MedStockTransactionTools.STATE_EDIT_EMPTY_NOW);
                withdraw(em, nextStock, quantity.subtract(stockSum), bhp);
            }
        }
    }

//    /**
//     * Diese Methode bucht ein Medikament in einen Vorrat ein.
//     * Dabei wird ein passendes APV ermittelt, eine Buchung angelegt und der neue MedBestand zurück gegeben.
//     * Der muss dann nur noch persistiert werden.
//     *
//     * @param inventory
//     * @param aPackage
//     * @param tradeForm
//     * @param text
//     * @param menge
//     * @return
//     * @throws Exception
//     */
//    public static MedStock addTo(MedInventory inventory, MedPackage aPackage, TradeForm tradeForm, String text, BigDecimal menge) {
//        MedStock stock = null;
//        if (menge.compareTo(BigDecimal.ZERO) > 0) {
//            BigDecimal estimatedUPR = MedStockTools.getEstimatedUPR(tradeForm, inventory.getResident());
//            stock = new MedStock(inventory, tradeForm, aPackage, text, estimatedUPR);
//            MedStockTransaction buchung = new MedStockTransaction(stock, menge);
//            stock.getStockTransaction().add(buchung);
//        }
//        return stock;
//    }


    public static MedStock getNextToOpen(MedInventory inventory) {
        MedStock bestand = null;
        if (inventory == null) return null;
        java.util.List<MedStock> listStocks = inventory.getMedStocks();
        if (inventory != null && !listStocks.isEmpty()) {
            for (MedStock myBestand : listStocks) {
                if (myBestand.isNew()) {
                    bestand = myBestand;
                    break;
                }
            }
        }
        return bestand;
    }

    /**
     * @param inventory
     * @return returns the currently opened stock of an inventory. <b>null</b>, if there are no open stocks.
     */
    public static MedStock getCurrentOpened(MedInventory inventory) {
        MedStock stock = null;
        java.util.List<MedStock> listStocks = inventory.getMedStocks();
        if (!listStocks.isEmpty()) {
            for (MedStock mystock : listStocks) {
                if (mystock.isOpened()) {
                    stock = mystock;
                    break;
                }
            }
        }
        return stock;
    }

    public static DosageForm getForm(MedInventory inventory) {
        DosageForm form = null;
        try {

            EntityManager em = OPDE.createEM();

//            OPDE.debug("MedInventoryTools.getPrinterForm: inventory: " + inventory.getID());

            Query query = em.createQuery(" " +
                    " SELECT tf.dosageForm FROM MedInventory i " +
                    " JOIN i.medStocks s " +
                    " JOIN s.tradeform tf " +
                    " WHERE i = :inventory");
            query.setParameter("inventory", inventory);
            query.setMaxResults(1);

            form = (DosageForm) query.getSingleResult();
            em.close();
        } catch (NoResultException nre) {
            form = null;
        } catch (Exception e) {
            OPDE.fatal(e);
        }
        return form;
    }

    public static ArrayList<MedInventory> getAllActive(Resident resident) {
        ArrayList<MedInventory> result;

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT inv FROM MedInventory inv WHERE inv.resident = :resident AND inv.to = :to AND inv.resident.adminonly <> 2 ORDER BY inv.text");
        query.setParameter("resident", resident);
        query.setParameter("to", SYSConst.DATE_UNTIL_FURTHER_NOTICE);

        result = new ArrayList<MedInventory>(query.getResultList());
        em.close();

        return result;
    }

    public static ArrayList<MedInventory> getAll(Resident resident) {
        ArrayList<MedInventory> result;

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT inv FROM MedInventory inv WHERE inv.resident = :resident ORDER BY inv.to DESC, inv.text");
        query.setParameter("resident", resident);

        result = new ArrayList<MedInventory>(query.getResultList());
        em.close();

        return result;
    }

    public static void closeAll(EntityManager em, Resident resident, Date enddate) throws Exception {
        Query query = em.createQuery("SELECT i FROM MedInventory i WHERE i.resident = :resident AND i.to >= :now");
        query.setParameter("resident", resident);
        query.setParameter("now", enddate);
        java.util.List<MedInventory> inventories = query.getResultList();

        for (MedInventory inventory : inventories) {
            MedInventory myInventory = em.merge(inventory);
            em.lock(myInventory, LockModeType.OPTIMISTIC);

            // close all stocks
            for (MedStock stock : inventory.getMedStocks()) {
                if (!stock.isClosed()) {
                    MedStock mystock = em.merge(stock);
                    em.lock(mystock, LockModeType.OPTIMISTIC);
                    mystock.setNextStock(null);
                    MedStockTools.close(em, mystock, OPDE.lang.getString(PnlInventory.internalClassID + ".stock.msg.allinvetories.closed"), MedStockTransactionTools.STATE_EDIT_INVENTORY_CLOSED);
                }
            }
            // close inventory
            myInventory.setTo(enddate);
        }
    }
}
