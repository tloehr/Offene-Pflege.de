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
import entity.mx.MXrecipient;
import entity.mx.MXrecipientTools;
import entity.system.UsersTools;
import op.OPDE;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.swing.table.AbstractTableModel;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author tloehr
 */
public class TMmsgs extends AbstractTableModel {
    public static final int COL_USER = 0;
    public static final int COL_SUBJECT = 1;
    public static final int COL_PIT = 2;
    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    String currentModel = "";
    HashMap<String, MXDataModelProvider> mymodels;

    /**
     * This TableModel allows several models to be switched when needed.
     * I wanted to try this concept in order to get rid of all this new model assignments to tables with
     * all the event problems turning up.
     *
     * @param mxDataModelProviders list of data providers to be used.
     */
    public TMmsgs(MXDataModelProvider... mxDataModelProviders) {
        mymodels = new HashMap<>();
        for (MXDataModelProvider provider : mxDataModelProviders){
            mymodels.put(provider.getKey(), provider);
        }
        // set to the first model in the list
        setCurrentMpdel(mxDataModelProviders[0].getKey());
    }

    /**
     * switch the current model in use.
     * @param key
     */
    public void setCurrentMpdel(String key){
        currentModel = key;
        mymodels.get(currentModel).loadModel();
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int column) {
        String name = "";
        if (column ==  COL_USER) name = SYSTools.xx("mx.col_user");
        if (column ==  COL_SUBJECT) name = SYSTools.xx("mx.col_subject");
        if (column ==  COL_PIT) name = SYSTools.xx("mx.col_pit");

        return name;
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Class getColumnClass(int column) {

        return String.class;
//        Class thisclass;
//        switch (column) {
//            case COL_PIT: {
//                thisclass = Date.class;
//                break;
//            }
////                   case COL_USER: {
////                       thisclass = String.class;
////                       break;
////                   }
////                   case COL_TEXT: {
////                       thisclass = String.class;
////                       break;
////                   }
//            default: {
//                thisclass = String.class;
//            }
//        }
//
//
//        return thisclass;
    }

//    public void addMsg(MXmsg mXmsg) {
//        mymodel.add(0, mXmsg);
//        fireTableRowsUpdated(0, 1);
//    }

    /**
     * adds or updates a message without rereading the whole model.
     * @param mXmsg
     */
    public void updateMsg(MXmsg mXmsg) {
        int row = mymodels.get(currentModel).getDataModel().indexOf(mXmsg);
        if (row == -1) {
            mymodels.get(currentModel).getDataModel().add(0, mXmsg);
            fireTableRowsUpdated(0, 1);
        } else {
            mymodels.get(currentModel).getDataModel().set(row, mXmsg);
            fireTableRowsUpdated(row, row);
        }
    }

    public void reload(){
        mymodels.get(currentModel).loadModel();
        fireTableDataChanged();
    }

    /**
     * to help the GC
     */
    public void cleanup() {
        for (String key : mymodels.keySet()){
            mymodels.get(key).getDataModel().clear();
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public MXmsg getRow(int row) {
        return mymodels.get(currentModel).getDataModel().get(row);
    }

    @Override
    public int getRowCount() {
        return mymodels.get(currentModel).getDataModel().size();
    }

    String boldIfUnread(MXmsg msg, String in) {
        if (msg.getRecipients().isEmpty()) return in;
        if (msg.isDraft()) return in;
        if (msg.getSender().equals(OPDE.getMe())) return in;
        return MXrecipientTools.findMXrecipient(msg, OPDE.getMe()).isUnread() ? SYSConst.html_bold(in) : in;
    }


    @Override
    public Object getValueAt(int row, int column) {
        Object value;

        MXmsg mymsg = mymodels.get(currentModel).getDataModel().get(row);
        switch (column) {
            case COL_PIT: {
                value = df.format(mymodels.get(currentModel).getDataModel().get(row).getPit());
                break;
            }
            case COL_USER: {

                if (mymsg.getSender().equals(OPDE.getMe())) {

                    String listOfRecipients = "";
                    for (MXrecipient mxr : mymsg.getRecipients()) {
                        listOfRecipients += UsersTools.getFullnameWithID(mxr.getRecipient()) + " ";
                    }
                    if (listOfRecipients.isEmpty()) listOfRecipients = "mx.norecipients.yet";

                    value = SYSTools.xx("mx.from.me") + " ==> " + listOfRecipients;
                } else {
                    value = UsersTools.getFullnameWithID(mymsg.getSender()) + " ==> " + SYSTools.xx("mx.to.me");
                }

                break;
            }

            case COL_SUBJECT: {
                value = SYSTools.catchNull(mymodels.get(currentModel).getDataModel().get(row).getSubject(), SYSConst.html_italic("mx.no.subject"));
                break;
            }
            default: {
                value = null;
            }
        }

        if (value != null) {
            String html = SYSConst.html_fontface;
            html += "<p>" + boldIfUnread(mymodels.get(currentModel).getDataModel().get(row), value.toString()) + "</p>";
            html += "</font>";
            value = SYSTools.toHTMLForScreen(html);
        } else {
            value = "Fehler";
        }

        return value;
    }
}
