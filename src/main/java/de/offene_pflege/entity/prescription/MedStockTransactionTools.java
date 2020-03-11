package de.offene_pflege.entity.prescription;

import de.offene_pflege.op.OPDE;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by shortelliJ IDEA.
 * User: tloehr
 * Date: 15.12.11
 * Time: 16:30
 * To change this template use File | Settings | File Templates.
 */
public class MedStockTransactionTools {
    public static final short STATE_DEBIT = 0;
    public static final short STATE_CREDIT = 1;
    public static final short STATE_EDIT_MANUAL = 2;
    public static final short STATE_EDIT_EMPTY_NOW = 3;
    public static final short STATE_EDIT_EMPTY_SOON = 4;
    public static final short STATE_EDIT_EMPTY_PAST_EXPIRY = 5;
    public static final short STATE_EDIT_EMPTY_BROKEN_OR_LOST = 6;
    public static final short STATE_EDIT_STOCK_CLOSED = 7;
    public static final short STATE_EDIT_INVENTORY_CLOSED = 8;
    public static final short STATE_CANCELLED = 9;
    public static final short STATE_CANCEL_REC = 10;


    public static List<MedStockTransaction> getAll(BHP bhp) {
           EntityManager em = OPDE.createEM();
           String jpql = " " +
                   " SELECT tx FROM MedStockTransaction tx " +
                   " WHERE tx.bhp = :bhp " +
                   " ORDER BY tx.pit DESC ";

           Query query = em.createQuery(jpql);
           query.setParameter("bhp", bhp);
           List<MedStockTransaction> list = query.getResultList();
           em.close();
           return list;
       }

    public static List<MedStockTransaction> getAll(MedStock stock) {
        EntityManager em = OPDE.createEM();
        String jpql = " " +
                " SELECT tx FROM MedStockTransaction tx " +
                " WHERE tx.stock = :stock " +
                " ORDER BY tx.pit DESC ";

        Query query = em.createQuery(jpql);
        query.setParameter("stock", stock);
        List<MedStockTransaction> list = query.getResultList();
        em.close();
        return list;
    }

}
