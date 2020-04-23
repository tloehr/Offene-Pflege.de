package de.offene_pflege.backend.services;

import de.offene_pflege.backend.entity.done.BHP;
import de.offene_pflege.backend.entity.done.MedStock;
import de.offene_pflege.backend.entity.done.MedStockTransaction;
import de.offene_pflege.op.OPDE;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by shortelliJ IDEA. User: tloehr Date: 15.12.11 Time: 16:30 To change this template use File | Settings |
 * File Templates.
 */
public class MedStockTransactionService {
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

    public static MedStockTransaction create(MedStock stock, BigDecimal amount) {
        MedStockTransaction tx = new MedStockTransaction();
        tx.setPit(new Date());
        tx.setStock(stock);
        tx.setAmount(amount);
        tx.setBhp(null);
        tx.setWeight(BigDecimal.ZERO);
        tx.setState(STATE_CREDIT);
        tx.setUser(OPDE.getLogin().getUser());
        return tx;
    }

    public static MedStockTransaction create(MedStock stock, BigDecimal amount, BigDecimal weight, BHP bhp) {
        MedStockTransaction tx = create(stock, amount, STATE_DEBIT);
        tx.setBhp(bhp);
        if (weight != null) tx.setWeight(weight);
        return tx;
    }

    public static MedStockTransaction create(MedStock stock, BigDecimal amount, short state) {
        MedStockTransaction tx = create(stock, amount);
        tx.setState(state);
        return tx;

    }

    public static  boolean isPartOfCancelPair(MedStockTransaction tx) {
        return tx.getState() == MedStockTransactionService.STATE_CANCEL_REC || tx.getState() == MedStockTransactionService.STATE_CANCELLED;
    }

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
