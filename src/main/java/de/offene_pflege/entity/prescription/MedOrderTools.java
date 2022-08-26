package de.offene_pflege.entity.prescription;

import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.entity.info.ResidentTools;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.SYSTools;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.lang3.mutable.MutableInt;
import org.javatuples.Quintet;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class MedOrderTools {
    public static Optional<MedOrder> toggle(EntityManager em, MedOrders medOrders, Prescription prescription) {
        Optional<MedOrder> optionalMedOrder = find(em, medOrders, prescription.getResident(), prescription.getTradeForm());
        if (optionalMedOrder.isPresent()) {
            em.remove(optionalMedOrder.get());
            return Optional.empty();
        }
        MedOrder medOrder = new MedOrder();
        medOrder.setMedOrders(medOrders);
        medOrder.setOpened_by(OPDE.getLogin().getUser());
        medOrder.setOpened_on(LocalDateTime.now());
        medOrder.setResident(em.merge(prescription.getResident()));
        medOrder.setTradeForm(em.merge(prescription.getTradeForm()));

        if (prescription.getDocON() != null)
            medOrder.setGp(em.merge(prescription.getDocON()));
        else
            medOrder.setGp(null);

        if (prescription.getHospitalON() != null)
            medOrder.setHospital(em.merge(prescription.getHospitalON()));
        else
            medOrder.setHospital(null);

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
        } catch (NoResultException e) {
            log.trace(e);
            return Optional.empty();
        } catch (Exception e) {
            OPDE.fatal(e);
        }
        return Optional.empty();
    }

    public static List<MedOrder> get_open_orders(EntityManager em, Resident resident) {
        ArrayList<MedOrder> list = new ArrayList<>();
        try {
            String jpql = " SELECT p " +
                    " FROM MedOrder p" +
                    " WHERE p.resident  = :resident AND p.closed_by = NULL";
            Query query = em.createQuery(jpql);
            query.setParameter("resident", resident);
            list.addAll(query.getResultList());
        } catch (Exception e) {
            OPDE.fatal(e);
        }
        return list;
    }

    public static Optional<MedOrder> find(Prescription prescription) {
        EntityManager em = OPDE.createEM();
        try {
            String jpql = " SELECT p " +
                    " FROM MedOrder p" +
                    " WHERE p.resident  = :resident AND p.tradeForm = :tradeForm AND p.closed_by = null ";
            Query query = em.createQuery(jpql);
            query.setParameter("resident", prescription.getResident());
            query.setParameter("tradeForm", prescription.getTradeForm());
            return Optional.of((MedOrder) query.getSingleResult());
        } catch (NoResultException e) {
            log.trace(e);
            return Optional.empty();
        } catch (Exception e) {
            OPDE.fatal(e);
        } finally {
            em.close();
        }
        return Optional.empty();
    }

    public static List<MedOrder> generate_orders(MedOrders medOrders, final double cover_days) {
        List<MedOrder> result = new ArrayList<>();
        final MultiKeyMap<Object, Quintet<BigDecimal, BigDecimal, BigDecimal, GP, Hospital>> map = new MultiKeyMap();
        List<Resident> residents = ResidentTools.getAllActive();
        MutableInt i = new MutableInt(0);

        residents.forEach(resident -> {
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(ResidentTools.getNameAndFirstname(resident), i.intValue(), residents.size()));
                    i.increment();
                    // suche alle medikamente die im moment verordnet sind und schreibe den aktuellen Bedarf und den aktuellen bestand in eine Map
                    PrescriptionTools.getAllActive(resident)
                            .stream().filter(prescription -> prescription.hasMed() && !prescription.getTradeForm().getDosageForm().isDontCALC())
                            .forEach(prescription -> {
                                BigDecimal consumption_per_day = PrescriptionTools.get_consumption_per_day(prescription);
                                BigDecimal sum_for_inventory = MedInventoryTools.getSum(TradeFormTools.getInventory4TradeForm(resident, prescription.getTradeForm()));
                                BigDecimal range = sum_for_inventory.divide(consumption_per_day, RoundingMode.HALF_UP);
                                if (range.compareTo(BigDecimal.valueOf(cover_days)) <= 0) {
                                    map.put(resident, prescription.getTradeForm(), new Quintet<>(consumption_per_day, sum_for_inventory, range, prescription.getDocON(), prescription.getHospitalON()));
                                    log.debug("{}, {} per day {} remaining {} range of days {}", resident.getName(), TradeFormTools.toPrettyString(prescription.getTradeForm()), consumption_per_day, sum_for_inventory, range);
                                }
                            });
                    // wie weit kommen wir mit unserm vorrat bei dieser Verordnung ?
                }
        );
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), 0, 0));
        map.entrySet().forEach(multiKeyTupleEntry -> {
            final Resident resident = (Resident) multiKeyTupleEntry.getKey().getKey(0);
            final TradeForm tradeForm = (TradeForm) multiKeyTupleEntry.getKey().getKey(1);

            // wir haben so eine bestellung schon
            if (medOrders.getOrderList().stream().anyMatch(medOrder -> medOrder.getResident().equals(resident) && medOrder.getTradeForm().equals(tradeForm)))
                return;

            DecimalFormat df = new DecimalFormat("#0.##");

            MedOrder medOrder = new MedOrder();
            medOrder.setMedOrders(medOrders);
            medOrder.setTradeForm(tradeForm);
            medOrder.setResident(resident);
            medOrder.setOpened_on(LocalDateTime.now());
            medOrder.setOpened_by(OPDE.getLogin().getUser());
            medOrder.setGp(multiKeyTupleEntry.getValue().getValue3());
            medOrder.setHospital(multiKeyTupleEntry.getValue().getValue4());
            medOrder.setNote(String.format("Bedarf %s pro Tag, %s sind noch da, hält noch %s Tage",
                            df.format(multiKeyTupleEntry.getValue().getValue0()),
                            df.format(multiKeyTupleEntry.getValue().getValue1()),
                            df.format(multiKeyTupleEntry.getValue().getValue2())
                    )
            );
            result.add(medOrder);
        });
        return result;
    }

    public static boolean is_closed(MedOrder medOrder) {
        return medOrder.getClosed_by() != null;
    }
}
