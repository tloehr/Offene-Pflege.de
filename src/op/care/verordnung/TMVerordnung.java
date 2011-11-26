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

import entity.BewohnerTools;
import entity.verordnungen.MedBestand;
import entity.verordnungen.MedVorrat;
import entity.verordnungen.Verordnung;
import entity.verordnungen.VerordnungTools;
import op.OPDE;
import op.tools.DlgException;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

/**
 * @author tloehr
 */
public class TMVerordnung
        extends AbstractTableModel {

    public static final int COL_MSSN = 0;
    public static final int COL_Dosis = 1;
    public static final int COL_Hinweis = 2;
    public static final int COL_AN = 3;
    public static final int COL_AB = 4;

    public final String[] debug = {"COL_MSSN", "COL_Dosis", "COL_Hinweis", "COL_AN", "COL_AB", "COL_INFO", "COL_DOK", "COL_VERID", "COL_BESTELLID",
            "COL_ANARZTID", "COL_VORID", "COL_ABGESETZT", "COL_ABDATUM", "COL_SITID", "COL_ABARZTID", "COL_ANKHID", "COL_ABKHID"
    };
    //    ResultSet rs;
//    PreparedStatement stmt;
//    String sql;
    boolean mitBestand;

    HashMap cache;

    protected List<Object[]> listeVerordnungen;

    public TMVerordnung(String bwkennung, boolean abgesetzt, boolean bestand) {
        super();

        listeVerordnungen = VerordnungTools.getVerordnungenUndVorraeteUndBestaende(BewohnerTools.findByBWKennung(bwkennung), !abgesetzt);


        this.cache = new HashMap();
        this.mitBestand = bestand;
//        try {
//            sql = " SELECT v.VerID, v.AnDatum, v.AbDatum, an.Anrede, an.Titel, an.Name, ab.Anrede, khan.Name, ab.Titel, ab.Name, " +
//                    " khab.Name, v.AnUKennung, v.AbUKennung, v.MassID, Ms.Bezeichnung mssntext, v.DafID," +
//                    " v.SitID, S.Text sittext, v.Bemerkung, v.BisPackEnde, M.Bezeichnung mptext, D.Zusatz, " +
//                    " F.Zubereitung, F.AnwText, F.PackEinheit, ifnull(bestand.DafID, 0) bestandDafID, M1.Bezeichnung mptext1, D1.Zusatz, " +
//                    " F.AnwEinheit, bestand.APV, ifnull(vor.VorID, 0) vorid, vor.saldo, v.AnArztID, " +
//                    " v.AbArztID, v.AnKHID, v.AbKHID, bestand.Summe bestsumme, " +
//                    " ifnull(bestand.BestID, 0) BestID, ifnull(bestand.NextBest, 0) nextbest " +
//                    " FROM BHPVerordnung v" +
//                    " INNER JOIN Massnahmen Ms ON Ms.MassID = v.MassID" +
//                    " LEFT OUTER JOIN MPDarreichung D ON v.DafID = D.DafID" +
//                    " LEFT OUTER JOIN Arzt an ON an.ArztID = v.AnArztID" +
//                    " LEFT OUTER JOIN KH khan ON khan.KHID = v.AnKHID" +
//                    " LEFT OUTER JOIN Arzt ab ON ab.ArztID = v.AbArztID" +
//                    " LEFT OUTER JOIN KH khab ON khab.KHID = v.AbKHID" +
//                    " LEFT OUTER JOIN MProdukte M ON M.MedPID = D.MedPID" +
//                    " LEFT OUTER JOIN MPFormen F ON D.FormID = F.FormID" +
//                    " LEFT OUTER JOIN Situationen S ON v.SitID = S.SitID" +
//                    // Dieser Konstrukt bestimmt die Vorräte für einen Bewohner
//                    // Dabei wird berücksichtigt, dass ein Vorrat unterschiedliche Hersteller umfassen
//                    // kann. Dies wird durch den mehrfach join erreicht. Dadurch stehen die verschiedenen
//                    // DafIDs der unterschiedlichen Produkte im selben Vorrat jeweils in verschiedenen Zeilen.
//                    // Da sind dann für jeden Vorrat alle die DafIDs enthalten, die jemals auf den Vorrat
//                    // eingebucht wurden. Man kann z.B. sehen, dass VorID 435 bisher schon die DafIDs 50, 165 und 553
//                    // beinhaltet hatte.
//                    // Durch den LEFT OUTER JOIN pickt sich die Datenbank die richtigen Paare heraus.
//                    // Das braucht man, weil in der Verordnung ja nur die DafID steht, die am Anfang
//                    // verwendet wurde. Das kann ja mittlerweile eine ganz andere sein.
//                    //
//                    // Also nochmal
//                    // Das hier gibt eine Liste aller Vorräte eines Bewohners. Jedem Vorrat
//                    // wird mindestens eine DafID zugeordnet. Das können auch mehr sein, die stehen
//                    // dann in verschiedenen Zeilen. Das bedeutet ganz einfach, dass einem Vorrat
//                    // ja unterschiedliche DAFs mal zugeordnet worden sind. Und hier stehen jetzt einfach
//                    // alle gültigen Kombinationen aus DAF und VOR inkl. der Salden, die jemals vorgekommen sind.
//                    // Für den entsprechenden Bewohner natürlich. Wenn man das nun über die DAF mit der Verordnung joined,
//                    // dann erhält man zwingend den passenden Vorrat, wenn es denn einen gibt.
//                    " LEFT OUTER JOIN " +
//                    " ( " +
//                    "       SELECT DISTINCT a.VorID, b.DafID, a.saldo FROM ( " +
//                    "           SELECT best.VorID, best.DafID, sum(buch.Menge) saldo FROM MPBestand best " +
//                    "           INNER JOIN MPBuchung buch ON buch.BestID = best.BestID " +
//                    "           INNER JOIN MPVorrat vor1 ON best.VorID = vor1.VorID" +
//                    "           WHERE vor1.BWKennung=? AND vor1.Bis = '9999-12-31 23:59:59'" +
//                    "           GROUP BY VorID" +
//                    "       ) a  " +
//                    "       INNER JOIN (" +
//                    "           SELECT best.VorID, best.DafID FROM MPBestand best " +
//                    "       ) b ON a.VorID = b.VorID " +
//                    " ) vor ON vor.DafID = v.DafID " +
//                    // " INNER JOIN " +
//                    // Hier kommen die angehangen Dokumente hinzu
////                    " (" +
////                    " 	SELECT DISTINCT f1.VerID, ifnull(anzahl,0) anzahl" +
////                    " 	FROM BHPVerordnung f1" +
////                    " 	LEFT OUTER JOIN (" +
////                    " 		SELECT VerID, count(*) anzahl FROM SYSVER2FILE" +
////                    " 		GROUP BY VerID" +
////                    " 		) fa ON fa.VerID = f1.VerID" +
////                    " 	WHERE f1.BWKennung=?" +
////                    " ) fia ON fia.VerID = v.VerID " +
////                    // Hier die angehangenen Vorgänge
////                    " INNER JOIN " +
////                    " (" +
////                    " 	SELECT DISTINCT f2.VerID, ifnull(anzahl,0) anzahl" +
////                    " 	FROM BHPVerordnung f2" +
////                    " 	LEFT OUTER JOIN (" +
////                    " 		SELECT ForeignKey, count(*) anzahl FROM VorgangAssign" +
////                    " 		WHERE TableName='BHPVerordnung'" +
////                    " 		GROUP BY ForeignKey" +
////                    " 		) va ON va.ForeignKey = f2.VerID" +
////                    " 	WHERE f2.BWKennung=? " +
////                    " ) vrg ON vrg.VerID = v.VerID " +
//                    // Hier kommen jetzt die Bestände im Anbruch dabei. Die Namen der Medikamente könnten ja vom
//                    // ursprünglich verordneten abweichen.
//                    " LEFT OUTER JOIN( " +
//                    "       SELECT best1.NextBest, best1.VorID, best1.BestID, best1.DafID, best1.APV, SUM(buch1.Menge) summe " +
//                    "       FROM MPBestand best1 " +
//                    "       INNER JOIN MPBuchung buch1 ON buch1.BestID = best1.BestID " +
//                    "       WHERE best1.Aus = '9999-12-31 23:59:59' AND best1.Anbruch < now() " +
//                    "       GROUP BY best1.BestID" +
//                    " ) bestand ON bestand.VorID = vor.VorID " +
//                    " LEFT OUTER JOIN MPDarreichung D1 ON bestand.DafID = D1.DafID " +
//                    " LEFT OUTER JOIN MProdukte M1 ON M1.MedPID = D1.MedPID " +
//                    " WHERE BWKennung=? ";
//            if (!abgesetzt) {
//                // sql += " AND v.AbDatum = '9999-12-31 23:59:59' ";
//                sql += " " +
//                        " ";
//            }
//            if (!(medi && ohneMedi)) { // ungleich gesetzt
//                if (medi) {
//                    sql += " AND v.DafID > 0 ";
//                } else {
//                    sql += " AND v.DafID = 0 ";
//                }
//            }
//            if (!(bedarf && regel)) { // ungleich gesetzt
//                if (bedarf) {
//                    sql += " AND v.SitID > 0 ";
//                } else {
//                    sql += " AND v.SitID = 0 ";
//                }
//
//            }
//            sql += " ORDER BY v.SitID = 0, v.DafID <> 0, ifnull(mptext, mssntext)  ";
//            stmt = OPDE.getDb().db.prepareStatement(sql);
//            //OPDE.getLogger().debug(sql);
//            stmt.setString(1, bwkennung);
//            stmt.setString(2, bwkennung);
////            stmt.setString(3, bwkennung);
////            stmt.setString(4, bwkennung);
//            rs = stmt.executeQuery();
//            rs.first();

    }

    public Verordnung getVerordnung(int row){
         return (Verordnung) listeVerordnungen.get(row)[0];
    }

    public MedVorrat getVorrat(int row){
         return (MedVorrat) listeVerordnungen.get(row)[1];
    }

    public MedBestand getBestand(int row){
         return (MedBestand) listeVerordnungen.get(row)[3];
    }

    public BigDecimal getVorratSaldo(int row){
         return (BigDecimal) listeVerordnungen.get(row)[2];
    }

    public BigDecimal getBestandSaldo(int row){
         return (BigDecimal) listeVerordnungen.get(row)[4];
    }

    @Override
    public int getRowCount() {
       return listeVerordnungen.size();
    }

    @Override
    public int getColumnCount() {
        int result = 5;
        return result;
    }

    @Override
    public Class getColumnClass(int c) {
        return String.class;
    }

    /**
     * Dient nur zu Optimierungszwecken. Damit die Datenbankzugriffe minimiert werden.
     * Lokaler Cache.
     */
    protected String getDosis(Verordnung verordnung, MedBestand bestandImAnbruch, MedVorrat vorrat, BigDecimal bestandSumme, BigDecimal vorratSumme, boolean mitBestand) {
        String result = "";
        if (cache.containsKey(verordnung)) {
            result = cache.get(verordnung).toString();
        } else {
            result = VerordnungTools.getDosis(verordnung, bestandImAnbruch, vorrat, bestandSumme, vorratSumme, mitBestand);
            cache.put(verordnung, result);
        }
        return result;
    }

    @Override
    public Object getValueAt(int row, int col) {
        Object result = null;
        Verordnung verordnung = getVerordnung(row);

        switch (col) {
            case COL_MSSN: {
                String res = "";
                res = VerordnungTools.getMassnahme(verordnung);
                if (!verordnung.getAttachedFiles().isEmpty()) {
                    res += "<font color=\"green\">&#9679;</font>";
                }
                if (!verordnung.getAttachedVorgaenge().isEmpty()) {
                    res += "<font color=\"red\">&#9679;</font>";
                }
                result = res;
                break;
            }
            case COL_Dosis: {
                result = getDosis(verordnung, getBestand(row), getVorrat(row), getBestandSaldo(row), getVorratSaldo(row), mitBestand);
                break;
            }
            case COL_Hinweis: {
                result = VerordnungTools.getHinweis(verordnung);
                break;
            }
            case COL_AN: {
                result = VerordnungTools.getAN(verordnung);
                break;
            }
            case COL_AB: {
                result = VerordnungTools.getAB(verordnung);
                break;
            }

            default: {
                result = "!!FEHLER!!";
            }
        }

        return result;
    }
}
