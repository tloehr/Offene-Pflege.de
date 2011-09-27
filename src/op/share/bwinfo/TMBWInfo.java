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
package op.share.bwinfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.swing.table.AbstractTableModel;
import op.OPDE;
import op.tools.DBRetrieve;
import op.tools.SYSConst;
import op.tools.SYSTools;

/**
 *
 * @author tloehr
 */
public class TMBWInfo extends AbstractTableModel {

    private ArrayList content;
    private SimpleDateFormat sdf;
    public static final int COL_ATTRIBNAME = 0;
    public static final int COL_HTML = 1;
    public static final int COL_VON = 2;
    public static final int COL_BIS = 3;
    public static final int COL_ATTRIB_HASHMAP = 4;
    public static final int COL_KATBEZ = 5;
    public static final int COL_KATINDEX = 6;
    public static final int COL_HTMLRAW = 7;
    public static final int COL_PRINT = 8;
    public static final int COL_KATBEZ_ATTRIB = 9;
    public static final int COL_ANUSER = 10;
    public static final int COL_ABUSER = 11;
    private boolean detailview;
    private boolean tooltip;

    public TMBWInfo(ArrayList c, boolean detailview, boolean tooltip, boolean mitZeitraum) {
        super();
        this.detailview = detailview;
        this.tooltip = tooltip;
        sdf = new SimpleDateFormat("dd.MM.yyyy");
        this.content = c;
    }

    public int getRowCount() {
        return content.size();
    }

    public int getColumnCount() {
        return 4;
    }

    public Class getColumnClass(int c) {
        return String.class;
    }

    public boolean isDetailview() {
        return detailview;
    }

    private boolean isAbgesetzt(HashMap attrib) {
        int interval = ((Integer) attrib.get("intervalmode")).intValue();
        return interval != BWInfo.MODE_INTERVAL_SINGLE_INCIDENTS && ((Date) attrib.get("bis")).getTime() < System.currentTimeMillis();
    }

    public Object getValueAt(int r, int c) {
        String result = "";
        HashMap attrib = (HashMap) content.get(r);
        String katbez = "";
        if (r == 0 || !getValueAt(r - 1, COL_KATBEZ_ATTRIB).toString().equalsIgnoreCase(attrib.get("katbez").toString())) {
            katbez = attrib.get("katbez").toString();
        }

        switch (c) {
            case COL_ATTRIBNAME: {
                if (!katbez.equals("")) {
                    result += "<font color=\"blue\"><b>" + katbez + "</b></font>";
                }
                break;
            }
            case COL_HTML: {
                String color = "green";
                if (attrib.containsKey("unbeantwortet")) {
                    color = "red";
                }

                int fianzahl = ((Integer) attrib.get("dokanzahl")).intValue();
                int vrganzahl = ((Integer) attrib.get("vrganzahl")).intValue();
                if (fianzahl > 0) {
                    result += "<font color=\"green\">&#9679;</font>";
                }
                if (vrganzahl > 0) {
                    result += "<font color=\"red\">&#9679;</font>";
                }

                result += "<font color=\"" + color + "\"><b>" + (isAbgesetzt(attrib) ? "<s>" : "") + attrib.get("bwinfokurz").toString() + (isAbgesetzt(attrib) ? "</s>" : "") + "</b></font><br/>";
                if (detailview) {
                    if (isAbgesetzt(attrib)) {
                        result += "<font color=\"" + SYSConst.html_lightslategrey + "\">";
                    }
                    result += attrib.get("html").toString();
                } else {
                    String html = attrib.get("html").toString();
                    String htmlshort = html.replaceAll("\\<.*?\\>", " "); // Alle HTML Tags entfernen.
                    int max = Math.min(60, htmlshort.length() - 1); // Nur die ersten 40 Zeichen zeigen...
                    result += "<i>" + htmlshort.substring(0, max) + "...</i>";
                }

                break;
            }
            case COL_VON: {
                if (((Date) attrib.get("von")).getTime() == SYSConst.DATE_VON_ANFANG_AN.getTime()) {
                    result = "|<==";
                } else {
                    int interval = ((Integer) attrib.get("intervalmode")).intValue();
                    if (interval == BWInfo.MODE_INTERVAL_SINGLE_INCIDENTS) {
                        SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MM.yyyy' 'HH':'mm' Uhr'");
                        result = sdf2.format((Date) attrib.get("von"));
                    } else {
                        result = sdf.format((Date) attrib.get("von"));
                    }
                }
                break;
            }
            case COL_BIS: {
                if (((Date) attrib.get("von")).getTime() == ((Date) attrib.get("bis")).getTime()) { // Einzelereignis
                    result = "--";
                } else { // Normaler Zeitraumeintrag
                    if (((Date) attrib.get("bis")).getTime() == SYSConst.DATE_BIS_AUF_WEITERES.getTime()) {
                        result += "==>|";
                    } else {
                        result += sdf.format((Date) attrib.get("bis"));
                    }
                }
                break;
            }


            case COL_ATTRIB_HASHMAP: {
                return attrib;
            }
            case COL_HTMLRAW: {
                result = attrib.get("html").toString();
                OPDE.getLogger().debug(result);
                break;
            }
            case COL_KATBEZ_ATTRIB: {
                result = attrib.get("katbez").toString();
                break;
            }
            case COL_KATBEZ: {
                result = katbez;
                break;
            }
            case COL_KATINDEX: {
                return attrib.get("katindex");
            }
            case COL_PRINT: {
                result += "<b><u>" + attrib.get("bwinfokurz").toString() + "</u></b><br/>";
                result += attrib.get("html").toString();

                if (isAbgesetzt(attrib)) {
                    result += "<br/>Vom: ";
                    if (((Date) attrib.get("von")).getTime() == SYSConst.DATE_VON_ANFANG_AN.getTime()) {
                        result += " <b>Anfang an</b>";
                    } else {
                        result += " <b>" + sdf.format((Date) attrib.get("von")) + "</b>";
                    }
                    result += " <b>(" + DBRetrieve.getUsername(attrib.get("anukennung").toString()) + ")</b>";
                    result += "<br/>";
                    result += "Bis: <b>" + sdf.format((Date) attrib.get("bis"));
                    result += " (" + DBRetrieve.getUsername(attrib.get("abukennung").toString()) + ")</b>";
                }
                //result = SYSTools.unHTML(result);
                break;
            }
            case COL_ANUSER: {
                result += " <b>" + DBRetrieve.getUsername(attrib.get("anukennung").toString()) + "</b>";
                result = SYSTools.unHTML(result);
                break;
            }
            case COL_ABUSER: {
                if (isAbgesetzt(attrib)) {
                    result += " <b>" + DBRetrieve.getUsername(attrib.get("abukennung").toString()) + "</b>";
                    result = SYSTools.unHTML(result);
                } else {
                    result = null;
                }
                break;
            }



            default: {
                result = "";
                break;
            }
        }




        return result;
    }

    public boolean isTooltip() {
        return tooltip;
    }
}
