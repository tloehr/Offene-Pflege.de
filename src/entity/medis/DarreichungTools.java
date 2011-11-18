package entity.medis;

import entity.Bewohner;
import op.OPDE;
import op.tools.DlgException;
import op.tools.SYSConst;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 18.11.11
 * Time: 16:29
 * To change this template use File | Settings | File Templates.
 */
public class DarreichungTools {

    /**
     * Dieses Methode wird vorwiegend bei den Verordnungen eingesetzt.
     * Der Gedanke ist wie folgt: Eine neue Verordnung eines Medikamentes wird immer
     * einem aktiven Vorrat zugeordnet, wenn es bereits früher mal eine Zuordnung zu einer
     * bestimmten DAF gab.
     * Gibt es keine frühere Zuweisung, dann werden nur Vorräte angezeigt, die zu der FormID der
     * neuen DAF passen. Notfalls muss man einen Vorrat anlegen.
     * Es werden Zuordnungen erlaubt, die aufgrund der Äquivalenzen zwischen
     * Formen bestehen. z.B. Tabletten zu Dragees zu Filmtabletten etc.
     *
     * @param dafid PK der Darreichung
     * @return ResultSet mit der gew¸nschten Liste.
     */
    public static MedVorrat getVorratZurDarreichung(Bewohner bewohner, Darreichung darreichung) {
        MedVorrat result = null;

        try {
            Query query = OPDE.getEM().createNamedQuery("MedVorrat.findByBewohnerAndDarreichung");
            query.setParameter("bewohner", bewohner);
            query.setParameter("darreichung", darreichung);

            result = (MedVorrat) query.getSingleResult();

        } catch (NoResultException nre) {
            result = null;
        } catch (Exception e) {
            OPDE.fatal(e.getMessage());
        }

        return result;
    }

    public static List<MedVorrat> getPassendeVorraeteZurDarreichung(Bewohner bewohner, Darreichung darreichung) {
        List<MedVorrat> liste = new ArrayList();

        // TODO: Hier gehts weiter
                String sql1 = " SELECT DISTINCT v.VorID, v.Text " +
                        " FROM MPVorrat v" +
                        " INNER JOIN MPBestand b ON v.VorID = b.VorID " +
                        " INNER JOIN MPDarreichung d ON b.DafID = d.DafID" +
                        " WHERE d.FormID IN (" +
                        "       SELECT FormID " +
                        "       FROM MPFormen " +
                        "       WHERE (Equiv IN ( " + // Alle Formen, die gleichwertig sind
                        "               SELECT Equiv " +
                        "               FROM MPDarreichung d " +
                        "               INNER JOIN MPFormen f ON f.FormID = d.FormID " +
                        "               WHERE d.DafID = ? " +
                        "               ) AND Equiv <> 0 " +
                        "           OR " +
                        "           ( FormID IN (" + // Falls diese Form keine Gleichwertigen besitzt (Equiv = 0), dann nur die Form selbst.
                        "               SELECT FormID FROM MPDarreichung WHERE DafID = ? )" +
                        "           )" +
                        "       )" +
                        " ) " +
                        " AND v.BWKennung=? " +
                        " AND v.Bis = " + SYSConst.MYSQL_DATETIME_BIS_AUF_WEITERES +
                        " ORDER BY v.Text ";
                PreparedStatement stmt1 = OPDE.getDb().db.prepareStatement(sql1);
                stmt1.setLong(1, dafid);
                stmt1.setLong(2, dafid);
                stmt1.setString(3, bwkennung);
                result = stmt1.executeQuery();
            }

        return result;
    }
}
