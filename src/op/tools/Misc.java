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
 */
package op.tools;

import op.OPDE;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author tloehr
 */
public class Misc {

//    public static void doAPVs() {
//        try {
//            ResultSet rs = DBRetrieve.getResultSet("MPAnbruch");
//            rs.beforeFirst();
//            while (rs.next()) {
//                long bestid = rs.getLong("BestID");
//                Date von = rs.getDate("Anbruch");
//                Date bis = rs.getDate("Abschluss");
//                berechneBuchungsWert(bestid, von, bis);
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(Misc.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//
//    }

//    public static double berechneBuchungsWert(long bestid, Date von, Date bis) {
//
//        long dafid = ((BigInteger) op.tools.DBHandling.getSingleValue("MPBestand", "DafID", "BestID", bestid)).longValue();
//        long vorid = ((BigInteger) op.tools.DBHandling.getSingleValue("MPBestand", "VorID", "BestID", bestid)).longValue();
//        long formid = ((BigInteger) op.tools.DBHandling.getSingleValue("MPDarreichung", "FormID", "DafID", dafid)).longValue();
//        long formstatus = ((Integer) op.tools.DBHandling.getSingleValue("MPFormen", "Status", "FormID", formid)).intValue();
//        String bwkennung = op.tools.DBHandling.getSingleValue("MPVorrat", "BWKennung", "VorID", vorid).toString();
//
//        double apvNeu = 1d;
//
//        if (formstatus != op.care.med.DBHandling.FORMSTATUS_APV1) {
//            HashMap filter = new HashMap();
//            filter.put("BestID", new Object[]{bestid, "="});
//            filter.put("Status", new Object[]{op.care.med.DBHandling.STATUS_EINBUCHEN_ANFANGSBESTAND, "="});
//            double inhaltReal = ((BigDecimal) op.tools.DBHandling.getSingleValue("MPBuchung", "Menge", filter)).doubleValue();
//            filter.clear();
//            double inhaltRechnerisch = 0d;
//            double apvAlt = ((BigDecimal) op.tools.DBHandling.getSingleValue("MPBestand", "APV", "BestID", bestid)).doubleValue();
//
//            String sqlGetSumme = "" +
//                    " SELECT SUM(Dosis) FROM BHP bhp " +
//                    " WHERE Ist >= ? AND Ist <= ? ";
//            try {
//                PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sqlGetSumme);
//                stmt.setDate(1, new java.sql.Date(von.getTime()));
//                stmt.setDate(2, new java.sql.Date(bis.getTime()));
//                ResultSet rs = stmt.executeQuery();
//                rs.first();
//                double bhpsumme = rs.getDouble(1);
//                inhaltRechnerisch = bhpsumme / apvAlt;
//            } catch (SQLException ex) {
//                new DlgException(ex);
//            }
//
//            if (inhaltRechnerisch > 0) {
//                apvNeu = inhaltReal / inhaltRechnerisch * apvAlt;
//
//                if (formstatus == op.care.med.DBHandling.FORMSTATUS_APV_PER_BW) {
//                    op.care.med.DBHandling.addAPV(dafid, bwkennung, apvNeu);
//                    OPDE.info("FormStatus APV_PER_BW. APVneu: " + apvNeu);
//                } else {
//                    HashMap hm2 = new HashMap();
//                    hm2.put("BWKennung", new Object[]{"", "="});
//                    hm2.put("DafID", new Object[]{dafid, "="});
//                    //boolean tauschen = (Boolean) DBRetrieve.getSingleValue("MPAPV", "Tauschen", hm2);
//                    hm2.clear();
//                    op.care.med.DBHandling.setAPV(dafid, (apvAlt + apvNeu) / 2, false); // der DafID APV wird durch den Mittelwert aus altem und neuem APV ersetzt.
//                    OPDE.info("FormStatus APV_PER_DAF. APValt: " + apvAlt + "  APVneu: " + (apvAlt + apvNeu) / 2);
//                }
//            } else {
//                OPDE.debug("inhaltRechnersich = 0, BestID: " + bestid + " DafID: " + dafid);
//            }
//        }
//        return apvNeu;
//    }
}
