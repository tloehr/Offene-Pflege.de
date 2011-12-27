/*
 * OffenePflege
 * Copyright (C) 2008 Torsten Löhr
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License V2 as published by the Free Software Foundation
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even 
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to 
 * the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
 * www.offene-pflege.de
 * ------------------------ 
 * Auf deutsch (freie Übersetzung. Rechtlich gilt die englische Version)
 * Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License, 
 * wie von der Free Software Foundation veröffentlicht, weitergeben und/oder modifizieren, gemäß Version 2 der Lizenz.
 *
 * Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, daß es Ihnen von Nutzen sein wird, aber 
 * OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN 
 * BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, 
 * schreiben Sie an die Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA.
 * 
 */
package op.care.verordnung;

import op.OPDE;
import op.tools.DlgException;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author tloehr
 */
@Deprecated
public class DBRetrieve {

//    public static long numAffectedBHPs(long verid) {
//        long result = 0;
//        String sql = " SELECT count(*) " +
//                " FROM BHP bhp INNER JOIN BHPPlanung bhpp ON bhp.BHPPID = bhpp.BHPPID" +
//                " WHERE bhpp.VerID = ? AND bhp.Status > 0";
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            stmt.setLong(1, verid);
//            ResultSet rs = stmt.executeQuery();
//            if (rs.first()) {
//                result = rs.getLong(1);
//            }
//        } catch (SQLException ex) {
//            new DlgException(ex);
//        }
//        return result;
//    }

    /*

    public static String getDosis(long verid) {
        String result = "";
        //OPDE.debug("VerID: "+verid);
        try {
            HashMap where = new HashMap();
            where.put("VerID", new Object[]{verid, "="});
            where.put("tmp", new Object[]{0l, "="});

//            String sql = "SELECT BHPPID,VerID,NachtMo,Morgens,Mittags,Nachmittags,Abends,NachtAb,UhrzeitDosis,Uhrzeit,MaxAnzahl,MaxEDosis,Taeglich,Woechentlich,Monatlich,TagNum,Mon,Die,Mit,Don,Fre,Sam,Son,LDatum,UKennung,tmp FROM BHPPlanung WHERE VerID = ? AND tmp = 0 ORDER BY Uhrzeit, NachtMo, Morgens, Mittags, Nachmittags, Abends, NachtAb";
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            stmt.setLong(1, verid);
            ResultSet rsDosis = op.tools.DBRetrieve.getResultSet("BHPPlanung", where, new String[]{"Uhrzeit", "NachtMo", "Morgens",
                    "Mittags", "Nachmittags", "Abends", "NachtAb"});
            //ResultSet rsDosis = stmt.executeQuery();
            if (rsDosis.first()) {
                rsDosis.beforeFirst();
                final int ZEIT = 0;
                final int UHRZEIT = 1;
                final int MAXDOSIS = 2;
                int previousState = -1;
                while (rsDosis.next()) {
                    //OPDE.debug("ResultSet: "+rsDosis.getRow());
                    int currentState;
                    // Zeit verwendet ?
                    if ((rsDosis.getDouble("NachtMo") + rsDosis.getDouble("NachtAb") + rsDosis.getDouble("Morgens") +
                            rsDosis.getDouble("Mittags") + rsDosis.getDouble("Nachmittags") + rsDosis.getDouble("Abends")) > 0) {
                        currentState = ZEIT;
                    } else if (rsDosis.getInt("MaxAnzahl") > 0) {
                        currentState = MAXDOSIS;
                    } else {
                        currentState = UHRZEIT;
                    }
                    boolean headerNeeded = previousState == -1 || currentState != previousState;

                    if (previousState > -1 && headerNeeded && previousState != MAXDOSIS) {
                        // noch den Footer vom letzten Durchgang dabei. Aber nur, wenn nicht
                        // der erste Durchlauf, ein Wechsel stattgefunden hat und der
                        // vorherige Zustand nicht MAXDOSIS war, das braucht nämlich keinen Footer.
                        result += "</table>";
                    }
                    previousState = currentState;
                    if (currentState == ZEIT) {
                        if (headerNeeded) {
                            result += "<table border=\"1\" cellspacing=\"0\">" +
                                    "   <tr>" +
                                    "      <th align=\"center\">fm</th>" +
                                    "      <th align=\"center\">mo</th>" +
                                    "      <th align=\"center\">mi</th>" +
                                    "      <th align=\"center\">nm</th>" +
                                    "      <th align=\"center\">ab</th>" +
                                    "      <th align=\"center\">sa</th>" +
                                    "      <th align=\"center\">Wdh.</th>" +
                                    "   </tr>";
                        }
                        result += "    <tr>" +
                                "      <td align=\"center\">" + (rsDosis.getDouble("NachtMo") > 0 ? SYSTools.printDouble(rsDosis.getDouble("NachtMo")) : "--") + "</td>" +
                                "      <td align=\"center\">" + (rsDosis.getDouble("Morgens") > 0 ? SYSTools.printDouble(rsDosis.getDouble("Morgens")) : "--") + "</td>" +
                                "      <td align=\"center\">" + (rsDosis.getDouble("Mittags") > 0 ? SYSTools.printDouble(rsDosis.getDouble("Mittags")) : "--") + "</td>" +
                                "      <td align=\"center\">" + (rsDosis.getDouble("Nachmittags") > 0 ? SYSTools.printDouble(rsDosis.getDouble("Nachmittags")) : "--") + "</td>" +
                                "      <td align=\"center\">" + (rsDosis.getDouble("Abends") > 0 ? SYSTools.printDouble(rsDosis.getDouble("Abends")) : "--") + "</td>" +
                                "      <td align=\"center\">" + (rsDosis.getDouble("NachtAb") > 0 ? SYSTools.printDouble(rsDosis.getDouble("NachtAb")) : "--") + "</td>" +
                                "      <td>" + getWiederholung(rsDosis) + "</td>" +
                                "    </tr>";
                    } else if (currentState == MAXDOSIS) {
//                        if (rsDosis.getLong("DafID") > 0){
//                            result += "Maximale Tagesdosis: ";
//                        } else {
//                            result += "Maximale Häufigkeit: ";
//                        }
                        result += "<b>Maximale Tagesdosis: ";
                        result += rsDosis.getInt("MaxAnzahl") + "x " + SYSTools.printDouble(rsDosis.getDouble("MaxEDosis"));
                        result += "</b><br/>";
                    } else if (currentState == UHRZEIT) {
                        if (headerNeeded) {
                            result += "<table border=\"1\" >" +
                                    "   <tr>" +
                                    "      <th align=\"center\">Uhrzeit</th>" +
                                    "      <th align=\"center\">Anzahl</th>" +
                                    "      <th align=\"center\">Wdh.</th>" +
                                    "   </tr>";
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        result += "    <tr>" +
                                "      <td align=\"center\">" + sdf.format(rsDosis.getTime("Uhrzeit")) + " Uhr</td>" +
                                "      <td align=\"center\">" + SYSTools.printDouble(rsDosis.getDouble("UhrzeitDosis")) + "</td>" +
                                "      <td>" + getWiederholung(rsDosis) + "</td>" +
                                "    </tr>";
                    } else {
                        result = "!!FEHLER!!";
                    }
                }
                if (previousState != MAXDOSIS) {
                    // noch den Footer vom letzten Durchgang dabei. Aber nur, wenn nicht
                    // der erste Durchlauf, ein Wechsel stattgefunden hat und der
                    // vorherige Zustand nicht MAXDOSIS war, das braucht nämlich keinen Footer.
                    result += "</table>";
                }
            } else {
                result += "<i>Noch keine Dosierung / Anwendungsinformationen verfügbar</i><br/>";
            }

        } catch (SQLException ex) {
            new DlgException(ex);
        }


        return result;
    }

    public static String getDosis4Druck(long verid, String einheit) {
        String result = "";
        try {
            HashMap where = new HashMap();
            where.put("VerID", new Object[]{verid, "="});
            where.put("tmp", new Object[]{0l, "="});

            ResultSet rsDosis = op.tools.DBRetrieve.getResultSet("BHPPlanung", where, new String[]{"Uhrzeit", "NachtMo", "Morgens",
                    "Mittags", "Nachmittags", "Abends", "NachtAb", "LDatum"});
            if (rsDosis.first()) {
                rsDosis.last();
                int num = rsDosis.getRow();
                rsDosis.beforeFirst();
                final int ZEIT = 0;
                final int UHRZEIT = 1;
                final int MAXDOSIS = 2;
                int previousState = -1;
                while (rsDosis.next()) {
                    if (num > 1) {
                        result += "[" + rsDosis.getRow() + ".) ";
                    }
                    int currentState;
                    // Zeit verwendet ?
                    if ((rsDosis.getDouble("NachtMo") + rsDosis.getDouble("NachtAb") + rsDosis.getDouble("Morgens") +
                            rsDosis.getDouble("Mittags") + rsDosis.getDouble("Nachmittags") + rsDosis.getDouble("Abends")) > 0) {
                        currentState = ZEIT;
                    } else if (rsDosis.getInt("MaxAnzahl") > 0) {
                        currentState = MAXDOSIS;
                    } else {
                        currentState = UHRZEIT;
                    }

                    if (currentState == ZEIT) {
                        result += (rsDosis.getDouble("NachtMo") > 0 ? "früh morgens " + rsDosis.getDouble("NachtMo") + " " + einheit + ", " : "") +
                                (rsDosis.getDouble("Morgens") > 0 ? "morgens " + rsDosis.getDouble("Morgens") + " " + einheit + ", " : "") +
                                (rsDosis.getDouble("Mittags") > 0 ? "mittags " + rsDosis.getDouble("Mittags") + " " + einheit + ", " : "") +
                                (rsDosis.getDouble("Nachmittags") > 0 ? "nachmittags " + rsDosis.getDouble("Nachmittags") + " " + einheit + ", " : "") +
                                (rsDosis.getDouble("Abends") > 0 ? "abends " + rsDosis.getDouble("Abends") + " " + einheit + ", " : "") +
                                (rsDosis.getDouble("NachtAb") > 0 ? "nachts " + rsDosis.getDouble("NachtAb") + " " + einheit + ", " : "");
                        result += getWiederholung(rsDosis);
//                        if (wdh.equals("")){
//                            // letztes Komma wieder weg.
//                            result = result.substring(0, result.length() - 2);
//                        } else {
//                            result += wdh;
//                        }

                    } else if (currentState == MAXDOSIS) {
                        result += "Maximale Tagesdosis: ";
                        result += rsDosis.getInt("MaxAnzahl") + "x " + rsDosis.getDouble("MaxEDosis") + " " + einheit;
                    } else if (currentState == UHRZEIT) {
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        result += sdf.format(rsDosis.getTime("Uhrzeit")) + " Uhr " +
                                rsDosis.getDouble("UhrzeitDosis") + " " + einheit +
                                " " + getWiederholung(rsDosis);
                    } else {
                        result = "!!FEHLER!!";
                    }
                    //result += ( rsDosis.isLast() ? "" : " / " );
                    if (num > 1) {
                        result += "] ";
                    }
                }
            } else {
                result += "<i>Noch keine Dosierung / Anwendungsinformationen verfügbar</i><br/>";
            }
        } catch (SQLException ex) {
            new DlgException(ex);
        }


        return result;
    }

    public static String getWiederholung(ResultSet rswdh) {
        String result = "";
        try {
            //ResultSet rsDosis = DBRetrieve.getResultSet("BHPPlanung","VerID",verid,"=");
            if (rswdh.getInt("Taeglich") > 0) {
                if (rswdh.getInt("Taeglich") > 1) {
                    result += "alle " + rswdh.getInt("Taeglich") + " Tage";
                } else {
                    result += "jeden Tag";
                }
            } else if (rswdh.getInt("Woechentlich") > 0) {
                if (rswdh.getInt("Woechentlich") == 1) {
                    result += "jede Woche ";
                } else {
                    result += "alle " + rswdh.getInt("Woechentlich") + " Wochen ";
                }

                if (rswdh.getInt("Mon") > 0) {
                    result += "Mon ";
                }
                if (rswdh.getInt("Die") > 0) {
                    result += "Die ";
                }
                if (rswdh.getInt("Mit") > 0) {
                    result += "Mit ";
                }
                if (rswdh.getInt("Don") > 0) {
                    result += "Don ";
                }
                if (rswdh.getInt("Fre") > 0) {
                    result += "Fre ";
                }
                if (rswdh.getInt("Sam") > 0) {
                    result += "Sam ";
                }
                if (rswdh.getInt("Son") > 0) {
                    result += "Son ";
                }

            } else if (rswdh.getInt("Monatlich") > 0) {
                if (rswdh.getInt("Monatlich") == 1) {
                    result += "jeden Monat ";
                } else {
                    result += "alle " + rswdh.getInt("Monatlich") + " Monate ";
                }

                if (rswdh.getInt("TagNum") > 0) {
                    result += "jeweils am " + rswdh.getInt("TagNum") + ". des Monats";
                } else {

                    int wtag = 0;
                    String tag = "";
                    if (rswdh.getInt("Mon") > 0) {
                        tag += "Montag ";
                        wtag = rswdh.getInt("Mon");
                    }
                    if (rswdh.getInt("Die") > 0) {
                        tag += "Dienstag ";
                        wtag = rswdh.getInt("Die");
                    }
                    if (rswdh.getInt("Mit") > 0) {
                        tag += "Mittwoch ";
                        wtag = rswdh.getInt("Mit");
                    }
                    if (rswdh.getInt("Don") > 0) {
                        tag += "Donnerstag ";
                        wtag = rswdh.getInt("Don");
                    }
                    if (rswdh.getInt("Fre") > 0) {
                        tag += "Freitag ";
                        wtag = rswdh.getInt("Fre");
                    }
                    if (rswdh.getInt("Sam") > 0) {
                        tag += "Samstag ";
                        wtag = rswdh.getInt("Sam");
                    }
                    if (rswdh.getInt("Son") > 0) {
                        tag += "Sonntag ";
                        wtag = rswdh.getInt("Son");
                    }
                    result += "jeweils am " + wtag + ". " + tag + " des Monats";
                }
            } else {
                result = "";
            }
            Date ldatum = new Date(rswdh.getTimestamp("LDatum").getTime());
            if (SYSCalendar.sameDay(ldatum, new Date()) > 0) { // Die erste Ausführung liegt in der Zukunft
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
                result += "<br/>erst ab: " + sdf.format(ldatum);
            }
        } catch (SQLException ex) {
            new DlgException(ex);
        }
        return result;
    }

//    public static String getBestaetigung(String bwkennung, long arztid) {
//        String s = "";
//        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
//        HashMap arzt = op.tools.DBRetrieve.getSingleRecord("Arzt", "ArztID", arztid);
//        String anrede = "";
//        if (arzt.get("Anrede").toString().equalsIgnoreCase("Frau")) {
//            anrede = "Sehr geehrte ";
//        } else {
//            anrede = "Sehr geehrter ";
//        }
//        anrede += arzt.get("Anrede").toString() + " ";
//        anrede += arzt.get("Titel").toString();
//        anrede += (arzt.get("Titel").toString().equals("") ? "" : " ");
//        anrede += arzt.get("Name").toString();
//        anrede += ",";
//
//        HashMap einrichtung = op.tools.DBRetrieve.getEinrichtung(bwkennung);
//
//        s += anrede + "<br/>" +
//                "<br/>" +
//                "damit wir bei der Übernahme der ärztlichen Anordnungen keinen Fehler machen, haben wir Ihnen die Gesamtverordnung aufgelistet.<br/>" +
//                "Es wäre sehr schön, wenn Sie uns diese Verordnungen bei nächster Gelegenheit kurz gegenzeichnen könnten.<br/>" +
//                "<br/>" +
//                "<b>" + sdf.format(SYSCalendar.today_date()) + "</b><br/>" +
//                "Fax.: " + arzt.get("Anrede").toString() + " " + arzt.get("Titel").toString() + " " + arzt.get("Name").toString() + ", " + arzt.get("Fax").toString() + "<br/>";
//
//        return s;
//    }

//    public static String getBestaetigung2(String bwkennung, long arztid) {
//        String s = "";
//        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
//        HashMap arzt = op.tools.DBRetrieve.getSingleRecord("Arzt", "ArztID", arztid);
//        String anrede = "";
//        if (arzt.get("Anrede").toString().equalsIgnoreCase("Frau")) {
//            anrede = "Sehr geehrte ";
//        } else {
//            anrede = "Sehr geehrter ";
//        }
//        anrede += arzt.get("Anrede").toString() + " ";
//        anrede += arzt.get("Titel").toString();
//        anrede += (arzt.get("Titel").toString().equals("") ? "" : " ");
//        anrede += arzt.get("Name").toString();
//        anrede += ",";
//
//        HashMap einrichtung = op.tools.DBRetrieve.getEinrichtung(bwkennung);
//
//        String sql = " SELECT v.VerID, fia.anzahl, v.AnArztID, an.Anrede, an.Titel, an.Name, an.Ort, khan.Name khname, khan.Ort khort, " +
//                " v.MassID, Ms.Bezeichnung mssntext, v.DafID, " +
//                " v.SitID, S.Text, v.Bemerkung, v.BisPackEnde, M.Bezeichnung mptext, D.Zusatz, F.Zubereitung, " +
//                " F.AnwText, F.PackEinheit, F.AnwEinheit " +
//                " FROM BHPVerordnung v " +
//                " INNER JOIN Massnahmen Ms ON Ms.MassID = v.MassID " +
//                " LEFT OUTER JOIN MPDarreichung D ON v.DafID = D.DafID " +
//                " LEFT OUTER JOIN Arzt an ON an.ArztID = v.AnArztID " +
//                " LEFT OUTER JOIN KH khan ON khan.KHID = v.AnKHID " +
//                " LEFT OUTER JOIN MProdukte M ON M.MedPID = D.MedPID " +
//                " LEFT OUTER JOIN MPFormen F ON D.FormID = F.FormID " +
//                " LEFT OUTER JOIN Situationen S ON v.SitID = S.SitID " +
//                " LEFT OUTER JOIN " +
//                " (" +
//                " 	SELECT DISTINCT f1.VerID, anzahl " +
//                "	FROM BHPVerordnung f1 " +
//                "	LEFT OUTER JOIN ( " +
//                "           SELECT VerID, count(*) anzahl FROM SYSVER2FILE " +
//                "           GROUP BY VerID " +
//                "       ) fa ON fa.VerID = f1.VerID " +
//                "       WHERE f1.BWKennung=? " +
//                " ) fia ON fia.VerID = v.VerID ";
//
//        try {
//            String sql1 = sql +
//                    " WHERE BWKennung=? AND (anzahl IS NULL OR anzahl = 0) AND AnArztID = ? AND v.AbDatum='9999-12-31 23:59:59' " +
//                    " ORDER BY v.SitID = 0, v.DafID <> 0, v.AnDatum ";
//            PreparedStatement stmt1 = OPDE.getDb().db.prepareStatement(sql1);
//            stmt1.setString(1, bwkennung);
//            stmt1.setString(2, bwkennung);
//            stmt1.setLong(3, arztid);
//            ResultSet rs1 = stmt1.executeQuery();
//            rs1.beforeFirst();
//
//            String sql2 = sql +
//                    " WHERE BWKennung=? AND v.AbDatum='9999-12-31 23:59:59' " +
//                    " ORDER BY v.SitID = 0, v.DafID <> 0, v.AnDatum ";
//            PreparedStatement stmt2 = OPDE.getDb().db.prepareStatement(sql2);
//            stmt2.setString(1, bwkennung);
//            stmt2.setString(2, bwkennung);
//            ResultSet rs2 = stmt2.executeQuery();
//            rs2.beforeFirst();
//
//            s += "<font size=\"18\"><b>Verordnungsänderung</b></font><br/>";
//            s += "<b>" + SYSTools.getBWLabel(bwkennung) + "</b><br/>" +
//                    "<br/>" +
//                    "<u>Absender:</u><br/>" +
//                    einrichtung.get("Bezeichnung").toString() + "<br/>" +
//                    einrichtung.get("Strasse").toString() + "<br/>" +
//                    einrichtung.get("PLZ").toString() + " " + einrichtung.get("Ort").toString() + "<br/>" +
//                    "Tel.:" + einrichtung.get("Tel").toString() + "<br/>" +
//                    "Fax.:" + einrichtung.get("Fax").toString() + "<br/>" +
//                    "<b>" + sdf.format(SYSCalendar.today_date()) + "</b><br/>" +
//                    "<br/>" +
//                    "<br/>" +
//                    anrede + "<br/>" +
//                    "<br/>" +
//                    "damit wir bei der Übernahme der ärztlichen Anordnungen keinen Fehler machen, haben wir Ihnen die Gesamtverordnung aufgelistet.<br/>" +
//                    "<br/>";
//            if (rs1.first()) {
//                s += "Sie haben für die/den obige(n) BewohnerIn folgendes angeordnet:<br/>";
//                s += makeItemList4Bestaetigung(rs1, -1); // Hier soll nichts fett gedruckt werden. Daher ArztID = -1;
//                s += "<br/><br/>Somit ergibt sich die folgende Gesamt-Verordnung:<br/><br/>";
//            } else {
//                s += "<br/><br/>Es liegt folgende Gesamt-Verordnung vor:<br/><br/>";
//            }
//
//            s += makeItemList4Bestaetigung(rs2, arztid);
//            s += "<br/>" +
//                    "Die <b>hervorgehobenen</b> Verordnungen sind neu. Es wäre sehr schön, wenn Sie uns diese Verordnungen bei nächster Gelegenheit kurz gegenzeichnen könnten.<br/>" +
//                    "<br/>" +
//                    "<br/>" +
//                    "<br/>" +
//                    "<br/>" +
//                    "Fax.: " + arzt.get("Anrede").toString() + " " + arzt.get("Titel").toString() + " " + arzt.get("Name").toString() + ", " + arzt.get("Fax").toString() + "<br/>";
//        } catch (SQLException ex) {
//            new DlgException(ex);
//        }
//
//        return s;
//    }
//
//    private static String makeItemList4Bestaetigung(ResultSet rs1, long arztid)
//            throws SQLException {
//        String s = "";
//        rs1.beforeFirst();
//        while (rs1.next()) {
//            s += "<li>";
//            s += (rs1.getInt("anzahl") == 0 && rs1.getLong("AnArztID") == arztid ? "<b>" : "");
//            s += "<u>" + (rs1.getLong("DafID") > 0 ? rs1.getString("mptext") +
//                    (SYSTools.catchNull(rs1.getString("Zusatz")).equals("") ? "" : rs1.getString("Zusatz")) + ", " +
//                    SYSTools.catchNull(rs1.getString("F.Zubereitung"), "", ", ") +
//                    SYSTools.catchNull(rs1.getString("AnwText").equals("") ? SYSConst.EINHEIT[rs1.getInt("AnwEinheit")] : rs1.getString("AnwText")) : rs1.getString("mssntext")) + "</u>; ";
//            if (rs1.getLong("SitID") > 0) {
//                s += "<i>Bei Bedarf: " + rs1.getString("S.Text") + "</i>; ";
//            }
//
//            s += getDosis4Druck(rs1.getLong("VerID"), SYSTools.catchNull(rs1.getString("AnwText")).equals("") ? SYSConst.EINHEIT[rs1.getInt("AnwEinheit")] : rs1.getString("AnwText"));
//            s += "; (";
//            if (!SYSTools.catchNull(rs1.getString("khname")).equals("")) {
//                s += rs1.getString("khname") + ", " + rs1.getString("khort");
//                s += (rs1.getLong("AnArztID") > 0 ? ", bestätigt durch " : "");
//            }
//            s += rs1.getString("Titel") + " " + rs1.getString("Name") + ", " + rs1.getString("Ort") + ")";
//            s += (rs1.getInt("anzahl") == 0 && rs1.getLong("AnArztID") == arztid ? "</b>" : "");
//            s += "</li>";
//        }
//        return s;
//    }

    public static String getVerordnungAsHTML(long verid) {
        String result = "";
        String sql = " SELECT v.VerID, v.AnDatum, v.AbDatum, an.Anrede, an.Titel, an.Name, ab.Anrede, khan.Name, ab.Titel, ab.Name, " +
                " khab.Name, v.AnUKennung, v.AbUKennung, v.MassID, Ms.Bezeichnung mssntext, v.DafID," +
                " v.SitID, S.Text sittext, v.Bemerkung, v.BisPackEnde, M.Bezeichnung mptext, D.Zusatz, " +
                " F.Zubereitung, F.AnwText, F.PackEinheit, " +
                " F.AnwEinheit, v.AnArztID, v.AbArztID, v.AnKHID, v.AbKHID " +
                " FROM BHPVerordnung v" +
                " INNER JOIN Massnahmen Ms ON Ms.MassID = v.MassID" +
                " LEFT OUTER JOIN MPDarreichung D ON v.DafID = D.DafID" +
                " LEFT OUTER JOIN Arzt an ON an.ArztID = v.AnArztID" +
                " LEFT OUTER JOIN KH khan ON khan.KHID = v.AnKHID" +
                " LEFT OUTER JOIN Arzt ab ON ab.ArztID = v.AbArztID" +
                " LEFT OUTER JOIN KH khab ON khab.KHID = v.AbKHID" +
                " LEFT OUTER JOIN MProdukte M ON M.MedPID = D.MedPID" +
                " LEFT OUTER JOIN MPFormen F ON D.FormID = F.FormID" +
                " LEFT OUTER JOIN Situationen S ON v.SitID = S.SitID" +
                " WHERE VerID=? ";
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            //OPDE.debug(sql);
            stmt.setLong(1, verid);
            ResultSet rs = stmt.executeQuery();
            rs.first();

            result = "<b>Ärztliche Verordnung</b><br/>";

            if (rs.getLong("DafID") == 0) {
                result += rs.getString("mssntext");
            } else {
                result += "<font face=\"Sans Serif\"><b>" + rs.getString("mptext").replaceAll("-", "- ") +
                        SYSTools.catchNull(rs.getString("D.Zusatz"), " ", "") + "</b></font>" +
                        SYSTools.catchNull(rs.getString("F.Zubereitung"), ", ", ", ") + " " +
                        SYSTools.catchNull(rs.getString("AnwText").equals("") ? SYSConst.EINHEIT[rs.getInt("AnwEinheit")] : rs.getString("AnwText"));
            }

            result += "<br/>" + getDosis(verid);
            result += "<br/>" + getHinweis(rs);
            result += "<br/>" + getAN(rs);
            result += "<br/>" + getAB(rs);
        } catch (SQLException ex) {
            Logger.getLogger(DBRetrieve.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    private static String getAN(ResultSet rs) {
        String result = "";
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
            String datum = sdf.format(rs.getDate("AnDatum"));

            result += "<font color=\"green\">" + datum + "; ";
            if (rs.getLong("v.AnKHID") > 0) {
                result += rs.getString("khan.Name");
            }
            if (rs.getLong("v.AnArztID") > 0) {
                if (rs.getLong("v.AnKHID") > 0) {
                    result += " <i>bestätigt durch:</i> ";
                }
                result += rs.getString("an.Titel") + " ";
                if (OPDE.isAnonym()) {
                    result += rs.getString("an.Name").substring(0, 1) + "***";
                } else {
                    result += rs.getString("an.Name");
                }

            }
            result += "; " + op.tools.DBRetrieve.getUsername(rs.getString("AnUKennung")) + "</font>";

        } catch (SQLException ex) {
            new DlgException(ex);
        }
        return result;
    }

    private static String getAB(ResultSet rs) {
        String result = "";
        try {
            if (rs.getDate("AbDatum") != null && rs.getTimestamp("AbDatum").getTime() < SYSConst.BIS_AUF_WEITERES.getTimeInMillis()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
                String datum = sdf.format(rs.getDate("AbDatum"));

                result += "<font color=\"red\">" + datum + "; ";
                if (rs.getLong("v.AbKHID") > 0) {
                    result += rs.getString("khab.Name");
                }
                if (rs.getLong("v.AbArztID") > 0) {
                    if (rs.getLong("v.AbKHID") > 0) {
                        result += " <i>bestätigt durch:</i> ";
                    }
                    result += rs.getString("ab.Titel");
                    if (OPDE.isAnonym()) {
                        result += rs.getString("ab.Name").substring(0, 1) + "***";
                    } else {
                        result += rs.getString("ab.Name");
                    }

                }
                result += "; " + op.tools.DBRetrieve.getUsername(rs.getString("AbUKennung")) + "</font>";
            }
        } catch (SQLException ex) {
            new DlgException(ex);
        }
        return result;
    }

    private static String getHinweis(ResultSet rs) {
        String result = "";
        try {
            if (rs.getLong("SitID") > 0) {
                result += "<b><u>Nur bei Bedarf:</u> <font color=\"blue\">" + rs.getString("sittext") + "</font></b><br/>";
            }
            if (rs.getString("Bemerkung") != null && !rs.getString("Bemerkung").equals("")) {
                result += "<b><u>Bemerkung:</u> </b>" + rs.getString("Bemerkung");
            }
        } catch (SQLException ex) {
            new DlgException(ex);
        }
        return result;
    }
    */
}
