package entity.verordnungen;

import entity.Bewohner;
import op.OPDE;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 15.12.11
 * Time: 16:00
 * To change this template use File | Settings | File Templates.
 */
public class APVTools {

    /**
     * Für die Kombination mit einem Bewohner kann es mehrere APVs für dasselbe Präparat geben. Daher wird hier der Mittelwert gebildet.
     * @param bewohner
     * @param darreichung
     * @return Mittelwert aller bisherigen APVs für diese bewohner / darreichung Kombination
     */
    public static BigDecimal getAPVMittelwert(Bewohner bewohner, Darreichung darreichung) {
        EntityManager em = OPDE.createEM();
        BigDecimal result = null;

        try {
            Query query = em.createQuery("SELECT AVG(apv.apv) FROM APV apv WHERE apv.bewohner = :bewohner AND apv.darreichung = :darreichung");
            query.setParameter("bewohner", bewohner);
            query.setParameter("darreichung", darreichung);
            result = (BigDecimal) query.getSingleResult();
        } catch (Exception e) {
            OPDE.fatal(e);
        } finally {
            em.close();
        }
        return result;
    }

    /**
     * Für eine Darreichung (unabhängig vom Bewohner), gibt es immer genau ein APV Objekt.
     * @param darreichung
     * @return
     */
    public static APV getAPV(Darreichung darreichung) {
        EntityManager em = OPDE.createEM();
        APV result = null;

        try {
            Query query = em.createQuery("SELECT apv FROM APV apv WHERE apv.bewohner IS NULL AND apv.darreichung = :darreichung");
            query.setParameter("darreichung", darreichung);
            result = (APV) query.getSingleResult();
        } catch (Exception e) {
            OPDE.fatal(e);
        } finally {
            em.close();
        }
        return result;
    }

     //double apv = 1d;
//        String sqlAverage = "" +
//                " SELECT IFNULL(AVG(APV),0) FROM MPAPV WHERE DafID = ? AND BWKennung = ?";
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sqlAverage);
//            stmt.setLong(1, dafid);
//            stmt.setString(2, bwkennung);
//            ResultSet rs = stmt.executeQuery();
//            rs.first();
//            apv = rs.getBigDecimal(1).doubleValue();
//            if (!bwkennung.equals("") && apv == 0d) {
//                // Es war ein bewohnerspezifischer APV gew¸nscht und den gab es nicht.
//                // Dann suchen wir den DAF spezifischen und geben den zur¸ck.
//                rs.close();
//                stmt.setString(2, "");
//                rs = stmt.executeQuery();
//                rs.first();
//                apv = rs.getBigDecimal(1).doubleValue();
//                if (apv == 0d) { // Immer noch nicht. Dann ist er jetzt eben 1.
//                    apv = 1d;
//                }
//                rs.close();
//                stmt.close();

}
