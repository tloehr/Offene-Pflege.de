package de.offene_pflege.entity.prescription;

import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.op.OPDE;
import lombok.extern.log4j.Log4j2;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.Optional;

@Log4j2
public class MedOrderTools {
    public static Optional<MedOrder> toggle(EntityManager em, MedOrders medOrders, Resident resident, TradeForm tradeForm) {
        Optional<MedOrder> optionalMedOrder = find(em, medOrders, resident, tradeForm);
        if (optionalMedOrder.isPresent()) {
            em.remove(optionalMedOrder.get());
            return Optional.empty();
        }
        MedOrder medOrder = new MedOrder();
        medOrder.setMedOrders(medOrders);
        medOrder.setOpened_by(OPDE.getLogin().getUser());
        medOrder.setOpened_on(LocalDateTime.now());
        medOrder.setResident(resident);
        medOrder.setTradeForm(tradeForm);
        em.merge(medOrder);
        return Optional.of(medOrder);
    }

    public static Optional<MedOrder> find(EntityManager em, MedOrders medOrders, Resident resident, TradeForm tradeForm) {
        try {
            String jpql = " SELECT p " +
                    " FROM MedOrder p" +
                    " WHERE p.medOrders = :medOrders AND p.resident  = :resident AND p.tradeForm = :tradeForm ";
            Query query = em.createQuery(jpql);
            query.setParameter("medOrders", medOrders);
            query.setParameter("resident", resident);
            query.setParameter("tradeForm", tradeForm);
            return Optional.of((MedOrder) query.getSingleResult());
        } catch (Exception e) {
            log.trace(e);
            return Optional.empty();
        }
    }


    public static Optional<MedOrder> find(Prescription prescription) {
        try {
            EntityManager em = OPDE.createEM();
            String jpql = " SELECT p " +
                    " FROM MedOrder p" +
                    " WHERE p.resident  = :resident AND p.tradeForm = :tradeForm AND p.closed_by = null ";
            Query query = em.createQuery(jpql);
            query.setParameter("resident", prescription.getResident());
            query.setParameter("tradeForm", prescription.getTradeForm());
            return Optional.of((MedOrder) query.getSingleResult());
        } catch (Exception e) {
            log.trace(e);
            return Optional.empty();
        }
    }
}
