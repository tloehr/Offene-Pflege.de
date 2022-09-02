package de.offene_pflege.entity.prescription;

import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.entity.info.ResidentTools;
import de.offene_pflege.entity.system.OPUsers;
import de.offene_pflege.entity.system.UsersTools;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.HTMLTools;
import de.offene_pflege.op.tools.JavaTimeConverter;
import de.offene_pflege.op.tools.SYSConst;
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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class MedOrderTools {
    public static Optional<MedOrder> toggle_order_status(EntityManager em, Prescription prescription) {
        Optional<MedOrder> optionalMedOrder = find(em, prescription.getResident(), prescription.getTradeForm());
        if (optionalMedOrder.isPresent()) {
            em.remove(optionalMedOrder.get());
            return Optional.empty();
        }
        MedOrder medOrder = new MedOrder();
        medOrder.setCreated_by(OPDE.getLogin().getUser());
        medOrder.setCreated_on(LocalDateTime.now());
        medOrder.setAuto_created(false);
        medOrder.setResident(em.merge(prescription.getResident()));
        medOrder.setTradeForm(em.merge(prescription.getTradeForm()));
        medOrder.setClosing_med_stock(null);

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

    public static Optional<MedOrder> find(EntityManager em, Resident resident, TradeForm tradeForm) {
        try {
            String jpql = " SELECT p " +
                    " FROM MedOrder p" +
                    " WHERE  p.resident  = :resident AND p.tradeForm = :tradeForm AND p.closed_by = null ";
            Query query = em.createQuery(jpql);

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


    public static Optional<MedOrder> find(Prescription prescription) {
        EntityManager em = OPDE.createEM();
        try {
            return find(em, prescription.getResident(), prescription.getTradeForm());
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

//    public static List<MedOrder> get_open_orders(EntityManager em, Resident resident) {
//        ArrayList<MedOrder> list = new ArrayList<>();
//        try {
//            String jpql = " SELECT p " +
//                    " FROM MedOrder p" +
//                    " WHERE p.resident  = :resident AND p.closed_by = NULL";
//            Query query = em.createQuery(jpql);
//            query.setParameter("resident", resident);
//            list.addAll(query.getResultList());
//        } catch (Exception e) {
//            OPDE.fatal(e);
//        }
//        return list;
//    }

    public static List<MedOrder> get_open_orders(EntityManager em) {
        ArrayList<MedOrder> list = new ArrayList<>();
        try {
            String jpql = " SELECT p " +
                    " FROM MedOrder p" +
                    " WHERE p.closed_by = NULL";
            Query query = em.createQuery(jpql);
            list.addAll(query.getResultList());
        } catch (Exception e) {
            OPDE.fatal(e);
        }
        return list;
    }

    public static List<MedOrder> get_closed_medorders(EntityManager em, int within_last_days) {
        ArrayList<MedOrder> list = new ArrayList<>();
        try {
            String jpql = " SELECT p " +
                    " FROM MedOrder p" +
                    " WHERE p.closed_by IS NOT NULL" +
                    " AND p.created_on >= :target_date";
            Query query = em.createQuery(jpql);
            query.setParameter("target_date", LocalDateTime.now().minusDays(within_last_days));
            list.addAll(query.getResultList());
        } catch (Exception e) {
            OPDE.fatal(e);
        }
        return list;
    }

    public static List<MedOrder> get_medorders(int within_last_days) {
        List<MedOrder> list = new ArrayList<>();
        EntityManager em = OPDE.createEM();
        try {
            list = get_open_orders(em);
            if (within_last_days > 0) {
                list.addAll(get_closed_medorders(em, within_last_days));
            }
        } catch (Exception e) {
            OPDE.fatal(e);
        } finally {
            em.close();
        }
        return list;
    }


    public static String toPrettyHTML(MedOrder medOrder) {
        String text = TradeFormTools.toPrettyHTML(medOrder.getTradeForm());
        text += "<br/>";
        text += DateFormat.getDateInstance(DateFormat.SHORT).format(JavaTimeConverter.toDate(medOrder.getCreated_on()));
        text += "&nbsp;" + medOrder.getCreated_by().getFullname();
        return text;
    }


    public static List<MedOrder> generate_orders(final int cover_days, List<MedOrder> open_orders) {
        List<MedOrder> result = new ArrayList<>();
        final MultiKeyMap<Object, Quintet<BigDecimal, BigDecimal, BigDecimal, GP, Hospital>> map = new MultiKeyMap();
        List<Resident> residents = ResidentTools.getAllActive();

        MutableInt running_integer = new MutableInt(0);
        residents.forEach(resident -> {
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(ResidentTools.getNameAndFirstname(resident), running_integer.intValue(), residents.size()));
                    running_integer.increment();
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
            if (open_orders.stream().anyMatch(medOrder -> medOrder.getResident().equals(resident) && medOrder.getTradeForm().equals(tradeForm)))
                return;

            DecimalFormat df = new DecimalFormat("#0.##");

            //todo: order week loswerden

            MedOrder medOrder = new MedOrder();
            medOrder.setTradeForm(tradeForm);
            medOrder.setResident(resident);
            medOrder.setCreated_on(LocalDateTime.now());
            medOrder.setCreated_by(OPDE.getLogin().getUser());
            medOrder.setAuto_created(true);
            medOrder.setGp(multiKeyTupleEntry.getValue().getValue3());
            medOrder.setHospital(multiKeyTupleEntry.getValue().getValue4());
            medOrder.setClosing_med_stock(null);
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
