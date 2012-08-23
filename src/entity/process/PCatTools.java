package entity.process;

import entity.prescription.DosageForm;
import op.OPDE;
import op.process.PnlProcess;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 16.06.11
 * Time: 14:07
 * To change this template use File | Settings | File Templates.
 */
public class PCatTools {

    public static final int PCAT_ART_MISC = 0;
    public static final int PCAT_ART_CARE = 1;
    public static final int PCAT_ART_BHP = 2;
    public static final int PCAT_ART_SOCIAL = 3;
    public static final int PCAT_ART_ADMIN = 4;
    public static final int PCAT_ART_COMPLAINT = 5;



    public static ArrayList<PCat> getPCats() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT pc FROM PCat pc ORDER BY pc.text");
        ArrayList<PCat> list = new ArrayList<PCat>(query.getResultList());
        em.close();
        return list;
    }
}
