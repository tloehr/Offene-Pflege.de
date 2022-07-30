package de.offene_pflege.entity.prescription;

import de.offene_pflege.op.OPDE;
import lombok.extern.log4j.Log4j2;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDateTime;
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
        } catch (Exception e) {
            log.debug(e);
            return Optional.empty();
        }
    }

    public static MedOrders get_or_create_active_med_orders(EntityManager em) {
        MedOrders result;
        Optional<MedOrders> optionalMedOrder = get_active_med_orders(em);
        if (optionalMedOrder.isPresent()) {
            result = optionalMedOrder.get();
        } else {
            result = new MedOrders();
            result.setOpened_by(OPDE.getLogin().getUser());
            result.setOpened_on(LocalDateTime.now());
        }
        return result;
    }
}
