package entity.prescription;

import op.OPDE;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 05.12.12
 * Time: 11:05
 * To change this template use File | Settings | File Templates.
 */
public class UPRTools {

    /**
     * calculates a starting UPR for a newly opened stock. If there is no UPR yet, it creates a new one and marks it as dummy,
     * so it will be replaced by the first calculated result, when this package is closed.
     * For DosageForms with type UPR1, there is no calculation at all. Those are always 1 constantly.
     *
     * @param stock the stock to be tested
     */
    public static BigDecimal getEstimatedUPR(MedStock stock) {
        OPDE.debug("<--- getEstimatedUPR");
        OPDE.debug("MedStock ID: " + stock.getID());
//        BigDecimal bdUPR = BigDecimal.ONE;

        UPR upr = null;
        BigDecimal bdUPR = null;

        if (!stock.getTradeForm().getDosageForm().isUPR1()) {
            EntityManager em = OPDE.createEM();
            Query query = null;
            if (stock.getTradeForm().getDosageForm().getState() == DosageFormTools.UPR_BY_RESIDENT) {
                OPDE.debug("UPR_BY_RESIDENT");
                String jpql = "SELECT AVG(upr.upr) FROM UPR upr WHERE upr.tradeform = :tradeform AND upr.resident = :resident ";
                query = em.createQuery(jpql);
                query.setParameter("tradeform", stock.getTradeForm());
                query.setParameter("resident", stock.getInventory().getResident());
            } else {
                OPDE.debug("UPR_BY_TRADEFORM");
                String jpql = "SELECT AVG(upr.upr) FROM UPR upr WHERE upr.tradeform = :tradeform ";
                query = em.createQuery(jpql);
                query.setParameter("tradeform", stock.getTradeForm());
            }

            try {
                bdUPR = (BigDecimal) query.getSingleResult();
                if (bdUPR == null) {
                    bdUPR = BigDecimal.ONE;
                    upr = new UPR(bdUPR, stock);
                    upr.setDummy(true);
                    upr.setUpr(BigDecimal.ONE);
                }
            } catch (Exception exc) {
                OPDE.fatal(exc);
            }

            em.close();
        } else {
            OPDE.debug("UPR1");
            bdUPR = BigDecimal.ONE;
        }
//        OPDE.debug("upr: " + bdUPR);
        OPDE.debug("getEstimatedUPR --->");
        return bdUPR;
    }


    /**
     * This method calculates the effective UPR as it transpired during the lifetime of that particular medstock.
     * It is vital, that this calculation is only done, when a package is empty. Otherwise the estimation
     * of the UPR is wrong.
     *
     * @param medstock, für den das Verhältnis neu berechnet werden soll.
     */
    public static UPR recalculateUPR(EntityManager em, MedStock medstock) throws Exception {
        if (medstock.getTradeForm().getDosageForm().isUPR1()) {
            return null;
        }

        MedStock myStock = em.merge(medstock);


        OPDE.debug("<--- recalculateUPR ");
        OPDE.debug("MedStock ID: " + myStock.getID());

        // this is the amount of content, which was in that package before it was opened
        // package unit
        BigDecimal startContent = MedStockTools.getStartTX(myStock).getAmount();

        // usage unit
        BigDecimal sumOfAllAplications = MedStockTools.getSumOfDosesInBHP(myStock);

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
        //                          the startContent in the package unit
        //    effective UPR   =     --------------------------------------------
        //                          the sum of all applications in the usage unit
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
        UPR estimatedUPR = em.merge(myStock.getUPR());

        estimatedUPR.setUprEFF(effectiveUPR);
        if (estimatedUPR.isDummy()) {
            estimatedUPR.setUpr(effectiveUPR);
            estimatedUPR.setDummy(false);
            OPDE.debug("the dummy UPR is replaced now");
        } else {
            // if the deviation was too high (usually more than 20%), then the new UPR is discarded
            BigDecimal maxDeviation = new BigDecimal(Double.parseDouble(OPDE.getProps().getProperty("apv_korridor"))).divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_UP);
            BigDecimal deviation = estimatedUPR.getUpr().divide(effectiveUPR).subtract(new BigDecimal(100)).abs();

            OPDE.debug("the deviation was: " + deviation + "%");

            // Is the deviation too high, then it will be discarded
            if (deviation.compareTo(maxDeviation) <= 0 && effectiveUPR.compareTo(BigDecimal.ZERO) > 0) {
                estimatedUPR.setUpr(effectiveUPR);
            }

        }

//

//        OPDE.debug("old UPR: " + medstock.getUPR());
        OPDE.debug("effective UPR: " + effectiveUPR);
        OPDE.debug("new UPR: " + estimatedUPR.getUpr());
        OPDE.debug("calcEffectiveUPR --->");
        return estimatedUPR;
    }

    public static BigDecimal getUPR(UPR upr) {
        return upr == null ? BigDecimal.ONE : upr.getUpr();
    }

    public static boolean isDummyUPR(MedStock stock) {

        return stock.getUPR() != null && stock.getUPR().isDummy();
    }

    public static UPR getUPR(MedStock stock) {
        UPR upr = null;
        EntityManager em = OPDE.createEM();
        try {
            Query query = em.createQuery("SELECT u FROM UPR u WHERE u.medStock = :stock");
            query.setParameter("stock", stock);
            upr = (UPR) query.getSingleResult();
        } catch (NoResultException nre) {
            upr = null;
        } catch (Exception e) {
            OPDE.fatal(e);
        } finally {
            em.close();
        }
        return upr;
    }

}
