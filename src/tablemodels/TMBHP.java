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
package tablemodels;

import entity.info.Resident;
import entity.prescription.BHP;
import entity.prescription.BHPTools;
import entity.prescription.PrescriptionsTools;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.table.AbstractTableModel;
import java.math.BigInteger;
import java.text.DateFormat;
import java.util.*;
import java.util.List;

/**
 * @author tloehr
 */
public class TMBHP extends AbstractTableModel {

    public static final int COL_BEZEICHNUNG = 0;
    public static final int COL_DOSIS = 1;
    public static final int COL_ZEIT = 2;
    public static final int COL_STATUS = 3;
    public static final int COL_UKENNUNG = 4;
    public static final int COL_BEMPLAN = 5;
    public static final int COL_BEMBHP = 6;


    public List<Object[]> getListeBHP() {
        return listeBHP;
    }

    protected List<Object[]> listeBHP;
    protected Comparator comparator;

    /**
     * @param schicht entsprechen OC_Const.ZEIT
     */
    public TMBHP(Resident bewohner, Date datum, int schicht) {
        super();

        comparator = new Comparator<Object[]>() {
            @Override
            public int compare(Object[] us, Object[] them) {
                BHP usBHP = (BHP) us[0];
                BHP themBHP = (BHP) them[0];

                int result = usBHP.getSoll().compareTo(themBHP.getSoll());

                if (result == 0) {
                    result = usBHP.getSollZeit().compareTo(themBHP.getSollZeit());
                }
                return result;
            }
        };

        listeBHP = new ArrayList<Object[]>();

        ArrayList al = SYSCalendar.getZeiten4Schicht((byte) schicht);
        String zeit1 = al.get(2).toString();
        String zeit2 = al.get(3).toString();
        byte schicht1 = ((Byte) al.get(0)).byteValue();
        byte schicht2 = ((Byte) al.get(1)).byteValue();

        EntityManager em = OPDE.createEM();
        try {

            Query queryOHNEmedi = em.createNativeQuery(" SELECT bhp.bhpid " +
                    " FROM BHP bhp " +
                    " WHERE Date(Soll)=Date(?) AND BWKennung=? AND DafID IS NULL" +
                    // Durch den Kniff mit ? = 1 kann man den ganzen Ausdruck anhängen oder abhängen.
                    " AND ( ? = TRUE OR (SZeit >= ? AND SZeit <= ?) OR (SZeit = 0 AND TIME(Soll) >= ? AND TIME(Soll) <= ?)) ");
            queryOHNEmedi.setParameter(1, datum);
            queryOHNEmedi.setParameter(2, bewohner.getRID());

            queryOHNEmedi.setParameter(3, schicht == SYSConst.ZEIT_ALLES);

            queryOHNEmedi.setParameter(4, schicht1);
            queryOHNEmedi.setParameter(5, schicht2);
            queryOHNEmedi.setParameter(6, zeit1);
            queryOHNEmedi.setParameter(7, zeit2);

            for (BigInteger bhpid : (List<BigInteger>) queryOHNEmedi.getResultList()) {
                listeBHP.add(new Object[]{em.find(BHP.class, bhpid.longValue()), null, null});
            }

            Query queryMITmedi = em.createNativeQuery(" SELECT bhp.bhpid, bestand.BestID, bestand.NextBest" +
                    " FROM BHP bhp " +
                    // Das hier gibt eine Liste aller Vorräte eines Bewohners. Jedem Vorrat
                    // wird mindestens eine DafID zugeordnet. Das können auch mehr sein, die stehen
                    // dann in verschiedenen Zeilen. Das bedeutet ganz einfach, dass einem Vorrat
                    // ja unterschiedliche DAFs mal zugeordnet worden sind. Und hier stehen jetzt einfach
                    // alle gültigen Kombinationen aus DAF und VOR inkl. der Salden, die jemals vorgekommen sind.
                    // Für den entsprechenden Bewohner natürlich. Wenn man das nun über die DAF mit der Verordnung joined,
                    // dann erhält man zwingend den passenden Vorrat, wenn es denn einen gibt.
                    " LEFT OUTER JOIN " +
                    " (" +
                    "     SELECT DISTINCT a.VorID, b.DafID FROM (" +
                    "       SELECT best.VorID, best.DafID FROM MPBestand best" +
                    "       INNER JOIN MPVorrat vor1 ON best.VorID = vor1.VorID" +
                    "       WHERE vor1.BWKennung=? AND vor1.Bis = '9999-12-31 23:59:59'" +
                    "       GROUP BY VorID" +
                    "     ) a " +
                    "     INNER JOIN (" +
                    "       SELECT best.VorID, best.DafID FROM MPBestand best " +
                    "     ) b ON a.VorID = b.VorID " +
                    " ) vor ON vor.DafID = bhp.DafID " +
                    // Das hier sucht passende Bestände im Anbruch raus
                    " LEFT OUTER JOIN( " +
                    "       SELECT best1.NextBest, best1.VorID, best1.BestID, best1.DafID, best1.APV " +
                    "       FROM MPBestand best1" +
                    "       WHERE best1.Aus = '9999-12-31 23:59:59' AND best1.Anbruch < now() " +
                    "       GROUP BY best1.BestID" +
                    " ) bestand ON bestand.VorID = vor.VorID " +
                    " WHERE bhp.DafID IS NOT NULL AND Date(bhp.Soll)=Date(?) AND bhp.BWKennung=?" +
                    // Durch den Kniff mit ? = 1 kann man den ganzen Ausdruck anhängen oder abhängen.
                    " AND (? = TRUE OR (SZeit >= ? AND SZeit <= ?) OR (SZeit = 0 AND TIME(Soll) >= ? AND TIME(Soll) <= ?))");
            queryMITmedi.setParameter(1, bewohner.getRID());
            queryMITmedi.setParameter(2, datum);
            queryMITmedi.setParameter(3, bewohner.getRID());

            queryMITmedi.setParameter(4, schicht == SYSConst.ZEIT_ALLES);

            queryMITmedi.setParameter(5, schicht1);
            queryMITmedi.setParameter(6, schicht2);
            queryMITmedi.setParameter(7, zeit1);
            queryMITmedi.setParameter(8, zeit2);

            for (Object[] objs : (List<Object[]>) queryMITmedi.getResultList()) {
                listeBHP.add(new Object[]{em.find(BHP.class, ((BigInteger) objs[0]).longValue()), objs[1], objs[2]});
            }

            Collections.sort(listeBHP, comparator);

        } catch (Exception e) {
            OPDE.fatal(e);
        } finally {
            em.close();
        }
//        OPDE.debug(listeBHP);

    }

    @Override
    public int getRowCount() {
        return listeBHP.size();
    }

    public int getColumnCount() {
        return 6;
    }

    public Class getColumnClass(int c) {
        return String.class;
    }

    public void setBHP(int row, BHP bhp) {
        listeBHP.get(row)[0] = bhp;
    }

    public BHP getBHP(int row) {
        return (BHP) listeBHP.get(row)[0];
    }

    @Override
    public Object getValueAt(int row, int col) {
        Object result = null;
        Object[] objects = (Object[]) listeBHP.get(row);
        BHP bhp = (BHP) objects[0];
        BigInteger bestid = (BigInteger) objects[1];
        BigInteger nextbest = objects[2] == null ? null : BigInteger.valueOf((Long) objects[2]); // Komisch einmal ist es Long einmal ist es BigInteger.

        switch (col) {
            case COL_BEZEICHNUNG: {
                result = PrescriptionsTools.getPrescriptionAsText(bhp.getPrescriptionSchedule().getPrescription());
                break;
            }
            case COL_DOSIS: {
                result = bhp.getDosis().toPlainString();
                break;
            }
            case COL_ZEIT: {
                if (bhp.getSollZeit() == 0) { // Uhrzeit
                    result = DateFormat.getTimeInstance(DateFormat.SHORT).format(bhp.getSoll());
                } else {
                    result = BHPTools.SOLLZEITTEXT[bhp.getSollZeit()];
                }
                break;
            }
            case COL_STATUS: {
                result = bhp.getStatus();
                break;
            }
            case COL_UKENNUNG: {
                result = bhp.getStatus() != BHPTools.STATE_OPEN ? bhp.getUser().getUID() : "";
                break;
            }
            case COL_BEMPLAN: {
                result = "";

                if (bestid != null && !bestid.equals(BigInteger.ZERO)) {
                    result = "<i>Bestand im Anbruch Nr.: " + bestid + "</i><br/>";
                    if (nextbest != null && !nextbest.equals(BigInteger.ZERO)) {
                        result = result.toString() + "<i>nächster anzubrechender Bestand Nr.: " + nextbest + "<i><br/>";
                    }
                }
                if (!SYSTools.catchNull(bhp.getPrescriptionSchedule().getPrescription().getBemerkung()).isEmpty()) {
                    result = result.toString() + "<b>Bemerkung:</b> " + bhp.getPrescriptionSchedule().getPrescription().getBemerkung();
                }
                break;
            }
            case COL_BEMBHP: {
                result = SYSTools.catchNull(bhp.getBemerkung());
                break;
            }

            default: {
                result = "";
            }
        }
        return result;
    }


}