/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.verordnungen;

import entity.Bewohner;
import entity.Einrichtungen;
import op.OPDE;
import op.tools.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author tloehr
 */
public class VerordnungTools {

    /**
     * Diese Methode ermittelt eine Liste aller Verordnungen gemäß der unten genannten Einschränkungen.
     *
     * @param bewohner    dessen Verordnungen wir suchen
     * @param nurAktuelle true, wenn wir nur die aktuellen Verordnungen wünschen, false sonst.
     * @return Eine Liste aus Objekt Arrays, die folgenden Aufbau hat:
     *         <center><code><b>{Verordnung, Vorrat, Saldo des Vorrats,
     *         Bestand (im Anbruch), Saldo des Bestandes, Bezeichnung des Medikamentes, Bezeichnung der Massnahme}</b></code></center>
     *         Es gibt Verordnungen, die keine Medikamente besitzen, bei denen steht dann <code>null</code> an den entsprechenden
     *         Stellen.
     */
    public static List<Object[]> getVerordnungenUndVorraeteUndBestaende(Bewohner bewohner, boolean nurAktuelle) {
        EntityManager em = OPDE.createEM();
        Query queryVorrat = em.createNamedQuery("Verordnung.findByBewohnerMitVorraeten");
        queryVorrat.setParameter(1, bewohner.getBWKennung());
        queryVorrat.setParameter(2, bewohner.getBWKennung());
        queryVorrat.setParameter(3, !nurAktuelle);
        List listeVorrat = queryVorrat.getResultList();

        // Aus technischen Gründe, kann ich diese native SQL nicht direkt mit der Liste aller zugehörigen Objekte
        // füllen lassen, daher muss ich das hier von Hand machen. Die Verordnungsobjekte sind zwar schon da, aber
        // die Vorrats und Bestandsobjekte nicht. Da bei diesem Ausdruck NULLs auftreten können, geht das nicht automatisch.

        Iterator it = listeVorrat.iterator();
        while (it.hasNext()) {
            Object[] line = (Object[]) it.next();


            if (line[1] != null) {
                BigInteger vorID = (BigInteger) line[1];
                MedVorrat vorrat = em.find(MedVorrat.class, vorID.longValue());
                line[1] = vorrat;
            }

            if (line[3] != null) {
                BigInteger bestID = (BigInteger) line[3];
                MedBestand bestand = em.find(MedBestand.class, bestID.longValue());
                line[3] = bestand;
            }
        }

        em.close();

        return listeVorrat;
    }

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
    public static String getStellplan(ResultSet rs) {

        int STELLPLAN_PAGEBREAK_AFTER_ELEMENT_NO = Integer.parseInt(OPDE.getProps().getProperty("stellplan_pagebreak_after_element_no"));

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

    public static String getEinheit(ResultSet rs) throws SQLException {
        return SYSTools.catchNull(rs.getString("Zubereitung"), "", ", ")
                + SYSTools.catchNull(rs.getString("AnwText").equals("") ? SYSConst.EINHEIT[rs.getInt("AnwEinheit")] : rs.getString("AnwText"));
    }


    public static String getMassnahme(ResultSet rs) throws SQLException {
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

    public static String getHinweis(ResultSet rs) throws SQLException {
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

    public static String getWiederholung(ResultSet rs) throws SQLException {
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

    public static String getMassnahme(Verordnung verordnung) {
        String result = "";

        if (verordnung.isAbgesetzt()) {
            result += "<s>"; // Abgesetzte
        }
        if (!verordnung.hasMedi()) {
            result += verordnung.getMassnahme().getBezeichnung();
        } else {
            // Prüfen, was wirklich im Anbruch gegeben wird. (Wenn das Medikament über die Zeit gegen Generica getauscht wurde.)

            MedBestand aktuellerAnbruch = MedBestandTools.findByVerordnungImAnbruch(verordnung);
            if (aktuellerAnbruch != null && !aktuellerAnbruch.getDarreichung().equals(verordnung.getDarreichung())) { // Nur bei Abweichung.
                result += "<font face=\"Sans Serif\"><b>" + aktuellerAnbruch.getDarreichung().getMedProdukt().getBezeichnung().replaceAll("-", "- ") +
                        aktuellerAnbruch.getDarreichung().getZusatz() + "</b></font>" +
                        aktuellerAnbruch.getDarreichung().getMedForm().getZubereitung() + " " +
                        (aktuellerAnbruch.getDarreichung().getMedForm().getAnwText().isEmpty() ? SYSConst.EINHEIT[aktuellerAnbruch.getDarreichung().getMedForm().getAnwEinheit()] : aktuellerAnbruch.getDarreichung().getMedForm().getAnwText());
                result += " <i>(ursprünglich verordnet: " + verordnung.getDarreichung().getMedProdukt().getBezeichnung().replaceAll("-", "- ");
                result += (result.endsWith(" ") ? "" : " ") +verordnung.getDarreichung().getZusatz() + "</i>";
            } else {
                result += "<font face=\"Sans Serif\"><b>" + verordnung.getDarreichung().getMedProdukt().getBezeichnung().replaceAll("-", "- ") +
                        " " + verordnung.getDarreichung().getZusatz() + "</b></font>" +
                        verordnung.getDarreichung().getMedForm().getZubereitung() + " " +
                        (verordnung.getDarreichung().getMedForm().getAnwText().isEmpty() ? SYSConst.EINHEIT[verordnung.getDarreichung().getMedForm().getAnwEinheit()] : verordnung.getDarreichung().getMedForm().getAnwText());
            }


        }
        if (verordnung.isAbgesetzt()) {//if (rs.getDate("AbDatum") != null && rs.getTimestamp("AbDatum").getTime() <=  SYSCalendar.nowDB() ){
            result += "</s>"; // Abgesetzte
            //OPDE.debug(this.toString() + ": " + result);
        }

        return result;
    }

    public static String getHinweis(Verordnung verordnung) {
        String result = "";

        if (verordnung.isBedarf()) {
            result += "<b><u>Nur bei Bedarf:</u> <font color=\"blue\">" + verordnung.getSituation().getText() + "</font></b><br/>";
        }
        if (!verordnung.getBemerkung().isEmpty()) {
            result += "<b><u>Bemerkung:</u> </b>" + verordnung.getBemerkung();
        }
        return (result.equals("") ? "" : "<html><body>" + result + "</body></html>");
    }

    public static String getAN(Verordnung verordnung) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
        String datum = sdf.format(verordnung.getAnDatum());

        result += "<html><body>";
        result += "<font color=\"green\">" + datum + "; ";
        if (verordnung.getAnKH() != null) {
            result += verordnung.getAnKH().getName();
        }
        if (verordnung.getAnArzt() != null) {
            if (verordnung.getAnKH() != null) {
                result += " <i>bestätigt durch:</i> ";
            }
            result += verordnung.getAnArzt().getAnrede() + " " + SYSTools.anonymizeName(verordnung.getAnArzt().getName(), SYSTools.INDEX_NACHNAME);
        }
        result += "; " + verordnung.getAngesetztDurch().getNameUndVorname() + "</font>";
        result += "</body></html>";

        return result;
    }

    public static String getAB(Verordnung verordnung) {
        String result = "";

        if (verordnung.isAbgesetzt()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
            String datum = sdf.format(verordnung.getAbDatum());

            result += "<html><body>";
            result += "<font color=\"red\">" + datum + "; ";

            result += verordnung.getAbKH() != null ? verordnung.getAbKH().getName() : "";

            if (verordnung.getAbArzt() != null) {
                if (verordnung.getAbKH() != null) {
                    result += " <i>bestätigt durch:</i> ";
                }
                result += verordnung.getAbArzt().getAnrede() + " " + SYSTools.anonymizeName(verordnung.getAbArzt().getName(), SYSTools.INDEX_NACHNAME);
            }
            result += "; " + verordnung.getAbgesetztDurch().getNameUndVorname() + "</font>";
            result += "</body></html>";
        }
        return result;
    }

    public static String getDosis(Verordnung verordnung, MedBestand bestandImAnbruch, MedVorrat vorrat, BigDecimal bestandSumme, BigDecimal vorratSumme, boolean mitBestand) {
        String result = "";
        //OPDE.debug("VerID: "+verid);

        // ======================================================================================================
        // Erster Teil
        // ======================================================================================================
        Iterator<VerordnungPlanung> planungen = verordnung.getPlanungen().iterator();

        if (planungen.hasNext()) {
            VerordnungPlanung vorherigePlanung = null;
            while (planungen.hasNext()) {
                VerordnungPlanung planung = planungen.next();

                result += VerordnungPlanungTools.getDosisAsHTML(planung, vorherigePlanung, false);

            }
            if (vorherigePlanung != null && VerordnungPlanungTools.getTerminStatus(vorherigePlanung) != VerordnungPlanungTools.MAXDOSIS) {
                // noch den Footer vom letzten Durchgang dabei. Aber nur, wenn das hier nicht
                // der erste Durchlauf ist ODER ein Wechsel stattgefunden hat und der
                // vorherige Zustand nicht MAXDOSIS war, das braucht nämlich keinen Footer.
                result += "</table>";
            }
        } else {
            result += "<i>Noch keine Dosierung / Anwendungsinformationen verfügbar</i><br/>";
        }

        if (verordnung.hasMedi()) {


            if (verordnung.isBisPackEnde()) {
                result += "nur bis Packungs Ende<br/>";
            }
            if (mitBestand && !verordnung.isAbgesetzt()) {
                if (vorratSumme.compareTo(BigDecimal.ZERO) > 0) {
                    result += "<b><u>Vorrat:</u> <font color=\"green\">" + SYSTools.roundScale2(vorratSumme) + " " +
                            SYSConst.EINHEIT[bestandImAnbruch.getDarreichung().getMedForm().getPackEinheit()] +
                            "</font></b>";
                    if (!bestandImAnbruch.getDarreichung().getMedForm().anwUndPackEinheitenGleich()) {

                        BigDecimal anwmenge = vorratSumme.multiply(bestandImAnbruch.getApv());


                        //double anwmenge = SYSTools.roundScale2(rs.getDouble("saldo") * rs.getDouble("APV"));
                        result += " <i>entspricht " + SYSTools.roundScale2(anwmenge) + " " +//SYSConst.EINHEIT[rs.getInt("f.AnwEinheit")]+"</i>";
                                MedFormenTools.getAnwText(bestandImAnbruch.getDarreichung().getMedForm());
                        result += " (bei einem APV von "+  SYSTools.roundScale2(bestandImAnbruch.getApv()) +" zu 1)";
                        result += "</i>";
                    }
                    if (bestandImAnbruch != null) {
                        result += "<br/>Bestand im Anbruch Nr.: <b><font color=\"green\">" + bestandImAnbruch.getBestID() + "</font></b>";

                        if (vorratSumme.compareTo(bestandSumme) != 0) {
                            result += "<br/>Restmenge im Anbruch: <b><font color=\"green\">" + bestandSumme + " " +
                                    SYSConst.EINHEIT[bestandImAnbruch.getDarreichung().getMedForm().getPackEinheit()] + "</font></b>";
                            if (!bestandImAnbruch.getDarreichung().getMedForm().anwUndPackEinheitenGleich()) {
                                //double anwmenge = SYSTools.roundScale2(rs.getDouble("bestsumme") * rs.getDouble("APV"));
                                BigDecimal anwmenge = bestandSumme.multiply(bestandImAnbruch.getApv());

                                result += " <i>entspricht " + SYSTools.roundScale2(anwmenge) + " " +//SYSConst.EINHEIT[rs.getInt("f.AnwEinheit")]+"</i>";
                                        MedFormenTools.getAnwText(bestandImAnbruch.getDarreichung().getMedForm()) + "</i>";
                            }
                        }
                    } else {
                        result += "<br/><b><font color=\"red\">Kein Bestand im Anbruch. Vergabe nicht möglich.</font></b>";
                    }
//                                if (rs.getLong("BestellID") > 0){
//                                    tmp += "<br/>Produkt / Medikament wurde nachbestellt";
//                                }
                } else {
                    result += "<b><font color=\"red\">Der Vorrat an diesem Medikament ist <u>leer</u>.</font></b>";
                }
            }


        }


        // ======================================================================================================
        // Zweiter Teil
        // ======================================================================================================

//        if (verordnung.hasMedi()) { // Gilt nur für Medikamente, sonst passt das nicht
//            if (verordnung.isBisPackEnde()) {
//                result += "nur bis Packungs Ende<br/>";
//            }
//            if (!verordnung.isAbgesetzt() && mitBestand) {
//
//            }
//        }
        return result;
    }

    public static String getSummen(Verordnung verordnung) {
        String tmp = "";

//        BigDecimal vorratSumme = MedVorratTools.getSumme(VerordnungTools.)
//
//        if (rs.getDouble("saldo") > 0) {
//            tmp += "<b><u>Vorrat:</u> <font color=\"green\">" + SYSTools.roundScale2(rs.getDouble("saldo")) + " " +
//                    SYSConst.EINHEIT[rs.getInt("PackEinheit")] +
//                    "</font></b>";
//            if (rs.getInt("f.PackEinheit") != rs.getInt("f.AnwEinheit")) {
//                double anwmenge = SYSTools.roundScale2(rs.getDouble("saldo") * rs.getDouble("APV"));
//                tmp += " <i>entspricht " + SYSTools.roundScale2(anwmenge) + " " +//SYSConst.EINHEIT[rs.getInt("f.AnwEinheit")]+"</i>";
//                        (rs.getString("AnwText") == null || rs.getString("AnwText").equals("") ? SYSConst.EINHEIT[rs.getInt("AnwEinheit")] : rs.getString("AnwText")) + "</i>";
//            }
//            if (rs.getLong("BestID") > 0) {
//                tmp += "<br/>Bestand im Anbruch Nr.: <b><font color=\"green\">" + rs.getLong("BestID") + "</font></b>";
//
//                if (rs.getDouble("bestsumme") != rs.getDouble("saldo")) {
//                    tmp += "<br/>Restmenge im Anbruch: <b><font color=\"green\">" + SYSTools.roundScale2(rs.getDouble("bestsumme")) + " " +
//                            SYSConst.EINHEIT[rs.getInt("PackEinheit")] + "</font></b>";
//                    if (rs.getInt("f.PackEinheit") != rs.getInt("f.AnwEinheit")) {
//                        double anwmenge = SYSTools.roundScale2(rs.getDouble("bestsumme") * rs.getDouble("APV"));
//                        tmp += " <i>entspricht " + SYSTools.roundScale2(anwmenge) + " " +//SYSConst.EINHEIT[rs.getInt("f.AnwEinheit")]+"</i>";
//                                (rs.getString("AnwText") == null || rs.getString("AnwText").equals("") ? SYSConst.EINHEIT[rs.getInt("AnwEinheit")] : rs.getString("AnwText")) + "</i>";
//                    }
//                }
//            } else {
//                tmp += "<br/><b><font color=\"red\">Kein Bestand im Anbruch. Vergabe nicht möglich.</font></b>";
//            }
////                                if (rs.getLong("BestellID") > 0){
////                                    tmp += "<br/>Produkt / Medikament wurde nachbestellt";
////                                }
//        } else {
//            tmp += "<b><font color=\"red\">Der Vorrat an diesem Medikament ist <u>leer</u>.</font></b>";
//        }
        return tmp;
    }



}
