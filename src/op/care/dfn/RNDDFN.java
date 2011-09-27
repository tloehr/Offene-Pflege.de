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

package op.care.dfn;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.math.BigInteger;
import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.TableModel;
import op.tools.DBRetrieve;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;
import tablerenderer.RNDHTML;

/**
 *
 * @author tloehr
 */
public class RNDDFN
        extends RNDHTML {

    Color color;
    Font font;

    public RNDDFN() {
        super();
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component result = null;

//        Color bermuda_sand = new Color(246, 201, 204);
//        Color melonrindgreen = new Color(223, 255, 165);
        TableModel tm = table.getModel();
        int status = ((Integer) tm.getValueAt(row, TMDFN.COL_STATUS)).intValue();
        long dfnid = ((Long) tm.getValueAt(row, TMDFN.COL_DFNID)).longValue();
        boolean erforderlich = ((Boolean) tm.getValueAt(row, TMDFN.COL_ERFORDERLICH)).booleanValue();
        //boolean zebra = (Boolean) tm.getValueAt(row, TMDFN.COL_ZEBRA);
        Date stdatum = (Date) tm.getValueAt(row, TMDFN.COL_STDATUM);
        if (status == TMDFN.STATUS_OFFEN) {
            if (isSelected) {
                this.color = SYSConst.grey80;
            } else {
                this.color = Color.white;
            }
        } else if (status == TMDFN.STATUS_ERLEDIGT) {
            if (isSelected) {
                color = SYSConst.darkolivegreen3;
            } else {
                color = SYSConst.darkolivegreen1;
            }
        } else { // (status == TMDFN.STATUS_VERWEIGERT
            if (isSelected) {
                color = SYSConst.salmon3;
            } else {
                color = SYSConst.salmon1;
            }
        }


        if (column == TMDFN.COL_STATUS) {
            JPanel p = new JPanel();
            JLabel j = new JLabel();
//            if (dafid > 0){
//                double saldo = ((Double) tm.getValueAt(row, TMDFN.COL_SALDO)).doubleValue();
//                int packeinheit = ((Integer) tm.getValueAt(row, TMDFN.COL_PACKEINHEIT)).intValue();
//                j.setToolTipText("Vorrat: "+saldo+" "+SYSConst.EINHEIT[packeinheit]);
//            }
            ImageIcon icon;
            ImageIcon icon2 = null;
            ImageIcon icon4 = null;
            if (status == TMDFN.STATUS_OFFEN) {
                icon = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/infoyellow.png"));
                if (erforderlich) {
                    int daysBetween = SYSCalendar.getDaysBetween(SYSCalendar.toGC(stdatum), new GregorianCalendar());
                    if (daysBetween < 4) {
                        icon2 = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/reload-green.png"));
                    } else if (daysBetween < 7) {
                        icon2 = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/reload-yellow.png"));
                    } else {
                        icon2 = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/reload-red.png"));
                    }
                }
            } else if (status == TMDFN.STATUS_ERLEDIGT) {
                if (erforderlich) {
                    icon2 = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/infogreen.png"));
                }
                icon = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/apply.png"));
            } else {
                if (erforderlich) {
                    icon2 = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/infogreen.png"));
                }
                icon = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/cancel.png"));
            }
            j.setIcon(icon);
            j.setOpaque(true);
            j.setBackground(color);
            p.add(j);
            p.setOpaque(true);
            p.setBackground(color);
            if (icon4 != null) {
                JLabel j2 = new JLabel();
                j2.setIcon(icon4);
                j2.setOpaque(true);
                j2.setBackground(color);
                p.add(j2);
            }
            if (icon2 != null) {
                JLabel j2 = new JLabel();
                j2.setIcon(icon2);
                j2.setOpaque(true);
                j2.setBackground(color);
                String tiptext = "Diese Massnahme muss bearbeitet werden.";
                if (SYSCalendar.sameDay(stdatum, SYSCalendar.today_date()) != 0) {
                    DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT);
                    tiptext += " Sie wurde ursprünglich für den <b>" + df.format(stdatum) + "</b> eingeplant.";
                }
                p.setToolTipText(SYSTools.toHTML(tiptext));
                p.add(j2);
            }
            result = p;
        } else if (column == TMDFN.COL_BEMPLAN) {
            long termid = (Long) tm.getValueAt(row, TMDFN.COL_TERMID);
            result = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String text = "";
            if (termid == 0) {
                text = SYSTools.toHTML("<i>spontane Einzelmassnahme</i>");
            } else {
                //result = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String stichwort = tm.getValueAt(row, TMDFN.COL_STICHWORT).toString();
                String situation = SYSTools.catchNull(tm.getValueAt(row, TMDFN.COL_SITUATION));
                text = "<html><body><b><font color=\"green\">" +
                        stichwort + "</font></b><table border=\"0\">";
                text += "    <tr><td align=\"left\" width=\"600\">" + situation + "</td></tr>";
                text += "   </table></body></html>";
            }
            ((JTextPane) result.getComponentAt(row, column)).setToolTipText(text);
        } else {
            result = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
        return result;
    }

    private String getMassnahmenText(
            long dfnid) {
        long massid = ((BigInteger) DBRetrieve.getSingleValue("DFN", "MassID", "DFNID", dfnid)).longValue();
        return (String) DBRetrieve.getSingleValue("Massnahmen", "Bezeichnung", "MassID", massid);
    }

    private String getMassnahmenErsatzText(long dfnid) {
        long massid = ((BigInteger) DBRetrieve.getSingleValue("DFN", "MassID", "AltDFNID", dfnid)).longValue();
        return (String) DBRetrieve.getSingleValue("Massnahmen", "Bezeichnung", "MassID", massid);
    }

    public Color getBackground() {
        return color;
    }
} // RNDDFN

