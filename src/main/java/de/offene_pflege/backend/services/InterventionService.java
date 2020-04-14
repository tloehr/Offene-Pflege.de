package de.offene_pflege.backend.services;


import de.offene_pflege.backend.entity.EntityTools;
import de.offene_pflege.backend.entity.done.ResInfoCategory;
import de.offene_pflege.backend.entity.done.Intervention;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 14.12.11
 * Time: 14:26
 * To change this template use File | Settings | File Templates.
 */
public class InterventionService {

    public static final int TYPE_CARE = 1;
    public static final int TYPE_PRESCRIPTION = 2;
    public static final int TYPE_SOCIAL = 3;

    public static final int FLAG_NONE = 0;
    public static final int FLAG_MOBILITY = 1;
    public static final int FLAG_WEIGHT_MONITORING = 2;
    public static final int FLAG_CATHETER_CHANGE = 3;
    public static final int FLAG_SUP_CATHETER_CHANGE = 4;
    public static final int FLAG_CONTROL_PACEMAKER = 5;
    public static final int FLAG_GAVAGE_FOOD_500ML = 6;
    public static final int FLAG_GAVAGE_LIQUID_500ML = 7;
    public static final int FLAG_GLUCOSE_MONITORING = 8;
    public static final int FLAG_BP_MONITORING = 9;
    public static final int FLAG_PORT_MONITORING = 10;
    public static final int FLAG_PULSE_MONITORING = 11;
    //    public static final int FLAG_WEIGHT_MONITORING = 12;
    public static final int FLAG_PAIN_MONITORING = 13;
    public static final int FLAG_TEMP_MONITORING = 14;
    public static final int FLAG_FOOD_CONSUMPTION = 15;
    public static final int FLAG_PROPH_CONTRACTURE = 16;
    public static final int FLAG_PROPH_BEDSORE = 17;
    public static final int FLAG_PROPH_SOOR = 18;
    public static final int FLAG_PROPH_THROMBOSIS = 19;
    public static final int FLAG_PROPH_PNEUMONIA = 20;
    public static final int FLAG_PROPH_INTERTRIGO = 21;
    public static final int FLAG_PROPH_FALL = 22;
    public static final int FLAG_PROPH_OBSTIPATION = 23;
    public static final int FLAG_ADDITIONAL_NUTRITION = 24;
    public static final int FLAG_THERAPY_PHYSIO = 25;
    public static final int FLAG_THERAPY_ERGO = 26;
    public static final int FLAG_THERAPY_LOGOPEDICS = 27;
    public static final int FLAG_BREATH_MONITORING = 28;
    public static final int FLAG_MEDS_APPLICATION = 29;
    public static final int FLAG_GAVAGE_FOOD_1000ML = 30;
    public static final int FLAG_GAVAGE_LIQUID_1000ML = 31;
    public static final int FLAG_GAVAGE_FOOD_250ML = 32;
    public static final int FLAG_GAVAGE_LIQUID_250ML = 33;
    public static final int FLAG_GAVAGE_FOOD_750ML = 34;
    public static final int FLAG_GAVAGE_LIQUID_750ML = 35;
    public static final int FLAG_GAVAGE_FOOD_200ML = 36;
    public static final int FLAG_GAVAGE_LIQUID_200ML = 37;
    public static final int FLAG_GAVAGE_FOOD_100ML = 38;
    public static final int FLAG_GAVAGE_LIQUID_100ML = 39;
    public static final int FLAG_GAVAGE_FOOD_1500ML = 40;
    public static final int FLAG_GAVAGE_LIQUID_1500ML = 41;

    // special flag for interventions that will trigger a mandantory remark, when checking them
    // there must be exactly ONE of this system records
    public static final int FLAG_SYSTEM_BHP_OUTCOME_TEXT = 10000;

//    public static final String[] FLAGS = new String[]{"nursingrecords.nursingprocess.flag.none",
//            "nursingrecords.nursingprocess.flag.contracture",
//            "nursingrecords.nursingprocess.flag.bedsore",
//            "nursingrecords.nursingprocess.flag.soor",
//            "nursingrecords.nursingprocess.flag.thrombosis",
//            "nursingrecords.nursingprocess.flag.pneumonia",
//            "nursingrecords.nursingprocess.flag.intertrigo",
//            "nursingrecords.nursingprocess.flag.fall",
//            "nursingrecords.nursingprocess.flag.obstipation",
//            "nursingrecords.nursingprocess.flag.extranutrition",
//            "nursingrecords.nursingprocess.flag.physio",
//            "nursingrecords.nursingprocess.flag.ergo",
//            "nursingrecords.nursingprocess.flag.logo"};

    public static Intervention create(String bezeichnung, int interventionType, ResInfoCategory category) {
        Intervention i = new Intervention();
        i.setBezeichnung(SYSTools.tidy(bezeichnung));
        i.setInterventionType(interventionType);
        i.setCategory(category);
        i.setActive(true);
        return i;
      }

    public static ListCellRenderer getRenderer() {
        return (jList, o, i, b, b1) -> {

            String text;
            if (o == null) {
                text = SYSTools.toHTML("<i>Keine Auswahl</i>");
            } else if (o instanceof Intervention) {
                text = ((Intervention) o).getBezeichnung();
            } else {
                text = o.toString();
            }
            return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, b, b1);
        };
    }

    public static List<Intervention> findBy(int mode) {
        return findBy(mode, "");
    }

    public static List<Intervention> findBy(String suche) {

        EntityManager em = OPDE.createEM();


        Query query = em.createQuery(" " +
                " SELECT m FROM Intervention m WHERE m.bezeichnung like :search " +
                " ORDER BY m.bezeichnung "
        );

        if (!SYSTools.catchNull(suche).isEmpty()) {
            query.setParameter("search", EntityTools.getMySQLsearchPattern(suche));
        }

        // https://github.com/tloehr/Offene-Pflege.de/issues/82
        List<Intervention> list = query.getResultList();

        em.close();

        return list;
    }

    public static List<Intervention> findBy(int massArt, String suche) {

        EntityManager em = OPDE.createEM();

        Query query = em.createQuery(" " +
                " SELECT m FROM Intervention m WHERE m.active = TRUE AND m.interventionType = :art " +
                (SYSTools.catchNull(suche).isEmpty() ? "" : " AND m.bezeichnung like :suche ") +
                " ORDER BY m.bezeichnung "
        );

        query.setParameter("art", massArt);
        if (!SYSTools.catchNull(suche).isEmpty()) {
            query.setParameter("suche", EntityTools.getMySQLsearchPattern(suche));
        }

        List<Intervention> list = query.getResultList();

        em.close();

        return list;
    }

    public static List<Intervention> findBy(ResInfoCategory category) {

        EntityManager em = OPDE.createEM();

        Query query = em.createQuery(" " +
                " SELECT m FROM Intervention m WHERE m.active = TRUE AND m.category = :cat " +
                " ORDER BY m.bezeichnung "
        );

        query.setParameter("cat", category);

        List<Intervention> list = query.getResultList();

        em.close();

        return list;
    }

    public static Intervention getBHPOutcomeIntervention() {
        Intervention outcomeIntervention = null;

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery(" SELECT m FROM Intervention m WHERE m.active = TRUE AND m.flag = :flag");
        query.setParameter("flag", FLAG_SYSTEM_BHP_OUTCOME_TEXT);
        outcomeIntervention = (Intervention) query.getSingleResult();
        em.close();

        return outcomeIntervention;
    }


}
