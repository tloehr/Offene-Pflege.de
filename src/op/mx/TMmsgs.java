/*
 * OffenePflege
 * Copyright (C) 2011 Torsten Löhr
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
package op.mx;

import entity.mx.MXmsg;
import entity.mx.MXrecipientTools;
import op.OPDE;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.swing.table.AbstractTableModel;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author tloehr
 */
public class TMmsgs extends AbstractTableModel {
    public static final int COL_USER = 0;
    public static final int COL_SUBJECT = 1;
    public static final int COL_PIT = 2;
    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

    ArrayList<MXmsg> mymodel;

    public TMmsgs(ArrayList<MXmsg> modelData) {
        mymodel = modelData;
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Class getColumnClass(int column) {

        Class thisclass;
        switch (column) {
            case COL_PIT: {
                thisclass = Date.class;
                break;
            }
//                   case COL_USER: {
//                       thisclass = String.class;
//                       break;
//                   }
//                   case COL_TEXT: {
//                       thisclass = String.class;
//                       break;
//                   }
            default: {
                thisclass = String.class;
            }
        }


        return thisclass;
    }

    public void addMsg(MXmsg mXmsg) {
        mymodel.add(0, mXmsg);
        fireTableRowsUpdated(0, 1);
    }

    public void updateMsg(MXmsg mXmsg) {
        int row = mymodel.indexOf(mXmsg);
        if (row == -1) {
            addMsg(mXmsg);
        } else {
            mymodel.set(row, mXmsg);
            fireTableRowsUpdated(row, row);
        }
    }

    public void cleanup() {
        mymodel.clear();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public MXmsg getRow(int row) {
        return mymodel.get(row);
    }

    @Override
    public int getRowCount() {
        int rowcount = 0;

        if (mymodel != null) {
            rowcount = mymodel.size();
        }
        return rowcount;
    }

    String boldIfUnread(MXmsg msg, String in) {
        return MXrecipientTools.findMXrecipient(msg, OPDE.getLogin().getUser()).isUnread() ? SYSConst.html_bold(in) : in;
    }


    @Override
    public Object getValueAt(int row, int column) {
        Object value;

        MXmsg mymsg = mymodel.get(row);
        switch (column) {
            case COL_PIT: {
                value = mymodel.get(row).getPit();
                break;
            }
            case COL_USER: {
                value = SYSTools.anonymizeUser(mymodel.get(row).getSender().getUID());
                break;
            }

            case COL_SUBJECT: {
                value = SYSTools.catchNull(mymodel.get(row).getSubject(), "mx.no.subject");
                break;
            }
            default: {
                value = null;
            }
        }

        if (value != null) {
            String html = SYSConst.html_fontface;
            html += "<p>" + boldIfUnread(mymodel.get(row), value.toString()) + "</p>";
            html += "</font>";
            value = html;
        }

        return value;
    }
}
