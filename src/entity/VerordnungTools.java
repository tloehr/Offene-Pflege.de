/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import op.OPDE;
import op.tools.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author tloehr
 */
public class VerordnungTools {

    public static String getStellplanAsHTML(Einrichtungen einrichtung) {
        PreparedStatement stmt;
        ResultSet rs;
        String sql;
        String result = "";

        try {
            sql = " SELECT aa.BWName, ifnull(bb.BestID, 0) BestID, aa.mptext, aa.mssntext, aa.SitID, aa.Bemerkung, aa.BisPackEnde, "
                    + " aa.Zusatz, aa.Zubereitung, aa.AnwText, aa.PackEinheit, aa.AnwEinheit, aa.Uhrzeit, aa.NachtMo, "
                    + " aa.Morgens, aa.Mittags, aa.Nachmittags, aa.Abends, aa.NachtAb, aa.UhrzeitDosis, aa.Mon, aa.Die, aa.Mit, "
                    + " aa.Don, aa.Fre, aa.Sam, aa.Son, aa.Taeglich, aa.Woechentlich, aa.Monatlich, aa.TagNum, aa.MaxAnzahl, aa.MaxEDosis, "
                    + " aa.VerID, aa.BWKennung, aa.Bemerkung, aa.Stellplan, aa.FStellplan, aa.DafID, aa.VorID, aa.tmp, aa.LDatum, aa.StatID,"
                    + " aa.StatBezeichnung "
                    + " FROM ( "
                    + "       SELECT CONCAT(bw.nachname,', ',bw.vorname) BWName, v.bwkennung, v.VerID, v.AnDatum, v.AbDatum, "
                    + "       Ms.Bezeichnung mssntext, v.DafID, F.Stellplan FStellplan, v.Stellplan, "
                    + "       v.SitID, v.Bemerkung, v.BisPackEnde, M.Bezeichnung mptext, D.Zusatz, F.Zubereitung, "
                    + "       F.AnwText, F.PackEinheit, F.AnwEinheit, bhp.Uhrzeit, bhp.NachtMo, "
                    + "       bhp.Morgens, bhp.Mittags, bhp.Nachmittags, bhp.Abends, bhp.NachtAb, bhp.UhrzeitDosis, bhp.Mon, bhp.Die, bhp.Mit, "
                    + "       bhp.Don, bhp.Fre, bhp.Sam, bhp.Son, bhp.Taeglich, bhp.Woechentlich, bhp.Monatlich, bhp.TagNum, bhp.MaxAnzahl, bhp.MaxEDosis, "
                    + "       vorr.VorID, bhp.tmp, bhp.LDatum, st.StatID, st.Bezeichnung StatBezeichnung "
                    + "       FROM BHPVerordnung v "
                    + "       INNER JOIN OCUsers anoc ON anoc.UKennung = v.AnUKennung "
                    + "       INNER JOIN Bewohner bw ON v.BWKennung = bw.BWKennung  "
                    + "       INNER JOIN Massnahmen Ms ON Ms.MassID = v.MassID "
                    + "       INNER JOIN Stationen st ON bw.StatID = st.StatID "
                    + "       LEFT OUTER JOIN OCUsers aboc ON aboc.UKennung = v.AbUKennung "
                    + "       LEFT OUTER JOIN BHPPlanung bhp ON bhp.VerID = v.VerID "
                    + "       LEFT OUTER JOIN MPDarreichung D ON v.DafID = D.DafID "
                    + "       LEFT OUTER JOIN MProdukte M ON M.MedPID = D.MedPID "
                    + "       LEFT OUTER JOIN MPFormen F ON D.FormID = F.FormID "
                    + "       LEFT OUTER JOIN ( "
                    + "           SELECT DISTINCT M.VorID, M.BWKennung, B.DafID FROM MPVorrat M  "
                    + "           INNER JOIN MPBestand B ON M.VorID = B.VorID "
                    + "           WHERE M.Bis = '9999-12-31 23:59:59' "
                    + "       ) vorr ON vorr.DafID = v.DafID AND vorr.BWKennung = v.BWKennung "
                    + "       WHERE v.AnDatum < now() AND v.AbDatum > now() AND v.SitID = 0 AND (v.DafID <> 0 OR v.Stellplan > 0) "
                    + "       AND st.EKennung = ? "
                    + " ) aa "
                    + " LEFT OUTER JOIN "
                    + " ( "
                    + "       SELECT DISTINCT best.DafID, vor.VorID, vor.BWKennung, best.BestID, best.Aus, best.Anbruch "
                    + "       FROM MPVorrat vor "
                    + "       INNER JOIN MPBestand best ON vor.VorID = best.VorID "
                    + "       WHERE vor.Bis = '9999-12-31 23:59:59' AND best.Aus = '9999-12-31 23:59:59' AND best.Anbruch < '9999-12-31 23:59:59' "
                    + " ) bb ON aa.VorID = bb.VorID "
                    + " WHERE aa.tmp = 0 " + // Falls noch alte Trümmer existieren, dann die nicht anzeigen.
                    " ORDER BY aa.StatID, aa.BWName, aa.DafID <> 0, aa.FStellplan, CONCAT(aa.mptext, aa.mssntext)";
            stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setString(1, einrichtung.getEKennung());
            rs = stmt.executeQuery();

            result = getStellplan(rs);


        } catch (SQLException se) {
            new DlgException(se);
        }
        return result;
    }

    /**
     * Diese Methode erzeugt einen Stellplan für den aktuellen Tag im HTML Format.
     * Eine Besonderheit bei der Implementierung muss ich hier erläutern.
     * Aufgrund der ungleichen HTML Standards (insbesonders der Druckdarstellung im CSS2.0 und später auch CSS2.1)
     * muss ich hier einen Trick anwenden, damit das auf verschiedenen Browsern halbwegs gleich aussieht.
     * <p/>
     * Daher addiere ich jedes größere Element auf einer Seite (also Header, Tabellen Zeilen) mit dem Wert 1.
     * Nach einer bestimmten Anzahl von Elementen erzwinge ich einen Pagebreak.
     * <p/>
     * Nach einem Pagebreak wird der Name des aktuellen Bewohner nocheinmal wiederholt.
     * <p/>
     * Ein Mac OS Safari druckt mit diesen Werten sehr gut.
     * Beim Firefox sollten die Ränder wie folgt eingestellt werden:
     * <ul>
     * <li>print.print_margin_bottom = 0.3</li>
     * <li>print.print_margin_left = 0.1</li>
     * <li>print.print_margin_right = 0.1</li>
     * <li>print.print_margin_top = 0.3</li>
     * <li>print.print_unwriteable_margin_bottom = 57</li>
     * <li>print.print_unwriteable_margin_left = 25</li>
     * <li>print.print_unwriteable_margin_right = 25</li>
     * <li>print.print_unwriteable_margin_top = 25</li>
     * <li>Drucken des Hintergrundes einschalten</li>
     * <ul>
     *
     * @param rs
     * @return
     */
    private static String getStellplan(ResultSet rs) {

        // TODO: Kandidat für SYSProps
        int STELLPLAN_PAGEBREAK_AFTER_ELEMENT_NO = 23;

        int elementNumber = 1;
        boolean pagebreak = false;

        String header = "Stellplan für den " + DateFormat.getDateInstance().format(new Date());

        String html = "<html>\n"
                + "<head>\n"
                + "<title>" + header + "</title>\n"
                + "<style type=\"text/css\" media=\"all\">\n"
                + "body { padding:10px; }\n"
                + "#fontsmall { font-size:10px; font-weight:bold; font-family:Arial,sans-serif;}\n"
                + "#fonth1 { font-size:24px; font-family:Arial,sans-serif;}\n"
                + "#fonth2 { font-size:16px; font-weight:bold; font-family:Arial,sans-serif;}\n"
                + "#fonttext { font-size:12px; font-family:Arial,sans-serif;}\n"
                + "#fonttextgrau { font-size:12px; background-color:#CCCCCC; font-family:Arial,sans-serif;}\n"
                + "</style>\n"
                + HTMLTools.JSCRIPT_PRINT
                + "</head>\n"
                + "<body>\n";

        String bwkennung = "";
        long statid = 0;

        try {

            rs.beforeFirst();

            while (rs.next()) {

                boolean stationsWechsel = statid != rs.getLong("aa.StatID");

                // Wenn der Plan für eine ganze Einrichtung gedruckt wird, dann beginnt eine
                // neue Station immer auf einer neuen Seite.
                if (stationsWechsel) {
                    elementNumber = 1;
                    // Beim ersten Mal nur ein H1 Header. Sonst mit Seitenwechsel.
                    if (statid == 0) {
                        html += "<h1 align=\"center\" id=\"fonth1\">";
                    } else {
                        html += "</table>\n";
                        html += "<h1 align=\"center\" id=\"fonth1\" style=\"page-break-before:always\">";
                    }
                    html += header + " (" + rs.getString("aa.StatBezeichnung") + ")" + "</h1>\n";
                    html += "<div align=\"center\" id=\"fontsmall\">Stellpläne <u>nur einen Tag</u> lang benutzen! Danach <u>müssen sie vernichtet</u> werden.</div>";
                    statid = rs.getLong("aa.StatID");
                }

                // Alle Formen, die nicht abzählbar sind, werden grau hinterlegt. Also Tropfen, Spritzen etc.
                boolean grau = rs.getInt("FStellplan") > 0;

                // Wenn der Bewohnername sich in der Liste ändert, muss
                // einmal die Überschrift drüber gesetzt werden.
                boolean bewohnerWechsel = !bwkennung.equals(rs.getString("bwkennung"));

                if (pagebreak || stationsWechsel || bewohnerWechsel) {
                    // Falls zufällig ein weiterer Header (der 2 Elemente hoch ist) einen Pagebreak auslösen WÜRDE
                    // müssen wir hier schonmal vorsorglich den Seitenumbruch machen.
                    // 2 Zeilen rechne ich nochdrauf, damit die Tabelle mindestens 2 Zeilen hat, bevor der Seitenumbruch kommt.
                    // Das kann dann passieren, wenn dieser if Konstrukt aufgrund eines BW Wechsels durchlaufen wird.
                    pagebreak = (elementNumber + 2 + 2) > STELLPLAN_PAGEBREAK_AFTER_ELEMENT_NO;

                    // Außer beim ersten mal und beim Pagebreak, muss dabei die vorherige Tabelle abgeschlossen werden.
                    if (pagebreak || !bwkennung.equals("")) {
                        html += "</table>\n";
                    }

                    bwkennung = rs.getString("bwkennung");
                    html += "<h2 id=\"fonth2\" " + (pagebreak ? "style=\"page-break-before:always\">" : ">") + ((pagebreak && !bewohnerWechsel) ? "<i>(fortgesetzt)</i> " : "") + rs.getString("bwname") + " [" + rs.getString("bwkennung") + "]</h2>\n";
                    html += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\">\n<tr>"
                            + "<th>Präparat / Massnahme</th><th>FM</th><th>MO</th><th>MI</th><th>NM</th><th>AB</th><th>NA</th><th>Bemerkungen</th></tr>\n";
                    elementNumber += 2;

                    if (pagebreak) {
                        elementNumber = 1;
                        pagebreak = false;
                    }
                }

                html += "<tr " + (grau ? "id=\"fonttextgrau\">" : ">");
                html += "<td width=\"300\" >" + getMassnahme(rs) + "</td>";
                html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(rs.getDouble("NachtMo")) + "</td>";
                html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(rs.getDouble("Morgens")) + "</td>";
                html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(rs.getDouble("Mittags")) + "</td>";
                html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(rs.getDouble("Nachmittags")) + "</td>";
                html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(rs.getDouble("Abends")) + "</td>";
                html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(rs.getDouble("NachtAb")) + "</td>";
                html += "<td width=\"300\" >" + getHinweis(rs) + "</td>";
                html += "</tr>\n";
                elementNumber += 1;

                pagebreak = elementNumber > STELLPLAN_PAGEBREAK_AFTER_ELEMENT_NO;
            }

            html += "</table>\n"
                    + "</body>";
        } catch (SQLException e) {
            new DlgException(e);
        }

        return html;
    }

    private static String getEinheit(ResultSet rs) throws SQLException {
        return SYSTools.catchNull(rs.getString("Zubereitung"), "", ", ")
                + SYSTools.catchNull(rs.getString("AnwText").equals("") ? SYSConst.EINHEIT[rs.getInt("AnwEinheit")] : rs.getString("AnwText"));
    }

    /**
     * Erstellt den Text für die Massnahmenspalte beim Ausdruck mit dem Styled Text XML Tags
     * von JasperReports.
     *
     * @param rs ResultSet der zugrundeliegenden Abfrage. Die Position des Sets wird durch
     *           die aufrufende JRDataSource bestimmt.
     * @return XML Code für den Druck.
     */
    private static String getMassnahme(ResultSet rs) throws SQLException {
        String result = "";

        if (rs.getLong("DafID") == 0) {
            result += rs.getString("mssntext");
        } else {
            result += "<b>" + rs.getString("mptext")
                    + SYSTools.catchNull(rs.getString("Zusatz"), ", ", "") + ", "
                    + getEinheit(rs) + "</b>";

        }
        if (rs.getLong("BestID") > 0) {
            result += "<br/><i>Bestand im Anbruch Nr.: " + rs.getLong("BestID") + "</i>";
        }
        return result;
    }

    private static String getHinweis(ResultSet rs) throws SQLException {
        String result = "";

        // Handelt es sich hierbei vielleicht um Uhrzeit oder Bedarf ?
        if (rs.getInt("MaxAnzahl") > 0) {
            result += "Maximale Tagesdosis: ";
            result += rs.getInt("MaxAnzahl") + "x " + SYSTools.printDouble4Jasper(rs.getDouble("MaxEDosis")) + " " + SYSConst.EINHEIT[rs.getInt("AnwEinheit")];
            result += "<br/>";
        } else if ((rs.getDouble("NachtMo") + rs.getDouble("NachtAb") + rs.getDouble("Morgens")
                + rs.getDouble("Mittags") + rs.getDouble("Nachmittags") + rs.getDouble("Abends")) == 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            result += sdf.format(rs.getTime("Uhrzeit")) + " Uhr "
                    + SYSTools.printDouble4Jasper(rs.getDouble("UhrzeitDosis")) + " " + SYSConst.EINHEIT[rs.getInt("AnwEinheit")];
            result += "<br/>";
        }

        String wiederholung = getWiederholung(rs);
        result += wiederholung;

//        if (rs.getLong("SitID") > 0) {
//            result += "<style isBold=\"true\" isUnderline=\"true\">Nur bei Bedarf:</style> <style isItalic=\"true\">" + rs.getString("sittext") + "</style>";
//        }
        if (rs.getString("Bemerkung") != null && !rs.getString("Bemerkung").equals("")) {
            if (!wiederholung.equals("")) {
                result += "<br/>";
            }
            result += "<b><u>Bemerkung:</u></b> " + rs.getString("Bemerkung");
        }

        return result.equals("") ? "&nbsp;" : result;
    }

    private static String getWiederholung(ResultSet rs) throws SQLException {
        String result = "";

        //ResultSet rs = DBRetrieve.getResultSet("BHPPlanung","VerID",verid,"=");
        if (rs.getInt("Taeglich") > 0) {
            if (rs.getInt("Taeglich") > 1) {
                result += "<b>alle " + rs.getInt("Taeglich") + " Tage</b>";
            }
        } else if (rs.getInt("Woechentlich") > 0) {
            result += "<b>";
            if (rs.getInt("Woechentlich") == 1) {
                result += "jede Woche ";
            } else {
                result += "alle " + rs.getInt("Woechentlich") + " Wochen ";
            }

            if (rs.getInt("Mon") > 0) {
                result += "Mon ";
            }
            if (rs.getInt("Die") > 0) {
                result += "Die ";
            }
            if (rs.getInt("Mit") > 0) {
                result += "Mit ";
            }
            if (rs.getInt("Don") > 0) {
                result += "Don ";
            }
            if (rs.getInt("Fre") > 0) {
                result += "Fre ";
            }
            if (rs.getInt("Sam") > 0) {
                result += "Sam ";
            }
            if (rs.getInt("Son") > 0) {
                result += "Son ";
            }
            result += "</b>";
        } else if (rs.getInt("Monatlich") > 0) {
            result += "<b>";
            if (rs.getInt("Monatlich") == 1) {
                result += "jeden Monat ";
            } else {
                result += "alle " + rs.getInt("Monatlich") + " Monate ";
            }

            if (rs.getInt("TagNum") > 0) {
                result += "jeweils am " + rs.getInt("TagNum") + ". des Monats";
            } else {
                int wtag = 0;
                String tag = "";
                if (rs.getInt("Mon") > 0) {
                    tag += "Montag ";
                    wtag = rs.getInt("Mon");
                }
                if (rs.getInt("Die") > 0) {
                    tag += "Dienstag ";
                    wtag = rs.getInt("Die");
                }
                if (rs.getInt("Mit") > 0) {
                    tag += "Mittwoch ";
                    wtag = rs.getInt("Mit");
                }
                if (rs.getInt("Don") > 0) {
                    tag += "Donnerstag ";
                    wtag = rs.getInt("Don");
                }
                if (rs.getInt("Fre") > 0) {
                    tag += "Freitag ";
                    wtag = rs.getInt("Fre");
                }
                if (rs.getInt("Sam") > 0) {
                    tag += "Samstag ";
                    wtag = rs.getInt("Sam");
                }
                if (rs.getInt("Son") > 0) {
                    tag += "Sonntag ";
                    wtag = rs.getInt("Son");
                }
                result += "jeweils am " + wtag + ". " + tag + " des Monats";
            }
            result += "</b>";
        } else {
            result = "";
        }

        if (rs.getInt("Taeglich") != 1) { // Wenn nicht jeden Tag, dann das letzte mal anzeigen.
            DateFormat df = DateFormat.getDateInstance();
            if (SYSCalendar.isInFuture(rs.getDate("LDatum").getTime())) {
                result += "<br/>erste Anwendung am: ";
            } else {
                result += "<br/>Zuletzt eingeplant: ";
            }
            result += df.format(rs.getDate("LDatum"));
        }

        return result;
    }
}