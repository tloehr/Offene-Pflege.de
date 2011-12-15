package entity.verordnungen;

import op.OPDE;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 18.11.11
 * Time: 14:28
 * To change this template use File | Settings | File Templates.
 */
public class MedBestandTools {

    public static MedBestand findByVerordnungImAnbruch(Verordnung verordnung) {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("MedBestand.findByDarreichungAndBewohnerImAnbruch");
        query.setParameter("bewohner", verordnung.getBewohner());
        query.setParameter("darreichung", verordnung.getDarreichung());

        MedBestand result = null;

        try {
            result = (MedBestand) query.getSingleResult();
        } catch (NoResultException nre) {
            result = null;
        } catch (Exception e) {
            OPDE.fatal(e);
            System.exit(1);
        } finally {
            em.close();
        }

        return result;
    }

    public static boolean anbrechen(MedBestand bestand) {
        boolean result = false;
        BigDecimal apv;

        if (bestand.getDarreichung().getMedForm().getStatus() == MedFormenTools.APV_PER_BW) {
            apv = MPAPVTools.getAPV(bestand.getVorrat().getBewohner(), bestand.getDarreichung());
        } else if (bestand.getDarreichung().getMedForm().getStatus() == MedFormenTools.APV_PER_DAF) {
            apv = MPAPVTools.getAPV(bestand.getDarreichung());
        } else { //APV1
            apv = BigDecimal.ONE;
        }
        // TODO: HIER GEHTS WEITER
        if (!hasAnbruch(vorid)) {
            HashMap hm = new HashMap();
            hm.put("Anbruch", "!NOW!");
            hm.put("APV", apv);
            result = op.tools.DBHandling.updateRecord("MPBestand", hm, "BestID", bestid);
            hm.clear();

        } else {
            result = false;
        }
        return result;
    }

    public static MedBestand getVorratImAnbruch(MedVorrat vorrat) {
        EntityManager em = OPDE.createEM();
        MedBestand bestand = null;
        try {
            Query query = em.createNamedQuery("MedBestand.findByVorratImAnbruch");
            query.setParameter("vorrat", vorrat);
            bestand = (MedBestand) query.getSingleResult();
        } catch (Exception e) {
            OPDE.fatal(e);
        } finally {
            em.close();
        }
        return bestand;
    }

//    public static boolean anbrechenNaechste(long vorid) {
//        boolean result = false;
//        if (!hasAnbruch(vorid)) {
//            try {
//                String sql1 = " SELECT BestID FROM MPBestand " +
//                        " WHERE VorID = ? AND Aus = '9999-12-31 23:59:59' AND Anbruch = '9999-12-31 23:59:59' " +
//                        " ORDER BY Ein, BestID " +
//                        " LIMIT 0,1";
//                PreparedStatement stmt1 = OPDE.getDb().db.prepareStatement(sql1);
//                stmt1.setLong(1, vorid);
//                ResultSet rs1 = stmt1.executeQuery();
//                if (rs1.first()) {
//                    long bestid = rs1.getLong("BestID");
//                    result = anbrechen(bestid);
//                }
//                rs1.close();
//                stmt1.close();
//            } catch (SQLException ex) {
//                new DlgException(ex);
//                result = false;
//            }
//        } else {
//            result = false;
//        }
//        return result;
//    }
}
