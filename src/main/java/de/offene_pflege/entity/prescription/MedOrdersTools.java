package de.offene_pflege.entity.prescription;

import de.offene_pflege.op.OPDE;
import lombok.extern.log4j.Log4j2;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class MedOrdersTools {

    public static Optional<MedOrders> get_active_med_orders(EntityManager em) {
        try {
            String jpql = " SELECT p " +
                    " FROM MedOrders p" +
                    " WHERE p.closed_on = null ";
            Query query = em.createQuery(jpql);
            return Optional.of((MedOrders) query.getSingleResult());
        } catch (NoResultException e) {
            log.trace(e);
            return Optional.empty();
        } catch (Exception e) {
            OPDE.fatal(e);
        }
        return Optional.empty();
    }

    public static Optional<MedOrders> next(MedOrders current, int skip_rows) {
        EntityManager em = OPDE.createEM();
        try {
            String operator = skip_rows >= 0 ? ">=" : "<";
            String asc = skip_rows >= 0 ? "DESC" : "ASC";
            String jpql = " SELECT o " +
                    " FROM MedOrders o" +
                    " WHERE o.opened_on " + operator + " :current " +
                    " ORDER BY o.opened_on " + asc;
            Query query = em.createQuery(jpql);
            query.setParameter("current", current.getOpened_on());
            query.setMaxResults(Math.abs(skip_rows));
            List<MedOrders> list = query.getResultList();
            return list.size() != Math.abs(skip_rows) ? Optional.empty() : Optional.of(list.get(0));
        } catch (NoResultException e) {
            log.trace(e);
            return Optional.empty();
        } catch (Exception e) {
            OPDE.fatal(e);
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public static MedOrders get_or_create_active_med_orders() {
        EntityManager em = OPDE.createEM();
        em.getTransaction().begin();
        MedOrders medOrders = get_or_create_active_med_orders(em);
        em.getTransaction().commit();
        em.close();
        return medOrders;
    }

    /**
     * returns the current active order list - creates one, if necessary
     *
     * @param em
     * @return
     */
    public static MedOrders get_or_create_active_med_orders(EntityManager em) {
        MedOrders result;
        Optional<MedOrders> optionalMedOrder = get_active_med_orders(em);
        if (optionalMedOrder.isPresent()) {
            result = optionalMedOrder.get();
        } else {
            result = em.merge(new MedOrders());
            result.setOpened_by(OPDE.getLogin().getUser());
            result.setOpened_on(LocalDateTime.now());
            result.setOrderList(new ArrayList<>());
        }
        return result;
    }
}
