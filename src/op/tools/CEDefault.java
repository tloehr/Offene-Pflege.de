/*
 * OffenePflege
 * Copyright (C) 2006-2012 Torsten Löhr
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
/**
 * Inspiriert von "VolumeEditor.java"
 * aus O'Reilly Java Swing, 2.Auflage
 * ISBN: 0-596-00408-7
 * Verfeinert für Nimbus PLAF
 */
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.EventObject;
import java.util.GregorianCalendar;
import java.util.Vector;

public class CEDefault extends JTextField implements TableCellEditor {

    protected transient Vector listeners;
    protected transient Object originalValue;
    protected transient Object value;

    public CEDefault() {
        super(SwingConstants.HORIZONTAL);

        // Das hier ist wichtig für den Nimbus PLAF
        Border border = UIManager.getBorder("Table.cellNoFocusBorder");
        if (border != null) {
            setBorder(border);
        }

        listeners = new Vector();
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (value == null) {
            return this;
        }
        originalValue = value;
        this.value = value;
        table.setRowSelectionInterval(row, row);
        table.setColumnSelectionInterval(column, column);

        if (value instanceof Date) {
            setText(SYSCalendar.printGermanStyle((Date) value));
        } else if (value instanceof BigDecimal) {
            NumberFormat nf = DecimalFormat.getCurrencyInstance();
            setText(nf.format(value));
        } else {
            setText(value.toString());
        }
        this.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFocusGained(evt);
            }
        });
        SYSTools.markAllTxt(this);
        return this;
    }

    private void txtFocusGained(java.awt.event.FocusEvent evt) {
        ((JTextField) evt.getSource()).selectAll();
        //SYSTools.markAllTxt((JTextField)evt.getSource());
    }


    // CellEditor methods
    public void cancelCellEditing() {
        value = originalValue;
        fireEditingCanceled();
    }

    public Object getCellEditorValue() {
        return value;
//        GregorianCalendar gc;
//        try {
//            gc = SYSCalendar.erkenneDatum(getText());
//        } catch (NumberFormatException ex) {
//            gc = SYSCalendar.heute();
//        }
//        return new Date(gc.getTimeInMillis());
    }

    public boolean isCellEditable(EventObject eo) {
        return true;
    }

    public boolean shouldSelectCell(EventObject eo) {
        return true;
    }

    public boolean stopCellEditing() {
        boolean valid = false;
         if (value instanceof Date) {
            GregorianCalendar gc;
            try {
                gc = SYSCalendar.erkenneDatum(getText());
            } catch (NumberFormatException ex) {
                gc = SYSCalendar.heute();
            }
            if (SYSCalendar.isInFuture(gc.getTimeInMillis())){
                gc = SYSCalendar.heute();
            }
            value = new Date(gc.getTimeInMillis());
            valid = true;
        } else if (value instanceof BigDecimal) {

             BigDecimal betrag = SYSTools.parseCurrency(getText());
             valid = betrag != null && !betrag.equals(BigDecimal.ZERO);
             value = valid ? betrag : originalValue;
        } else {
            value = getText();
            valid = !value.toString().isEmpty();
        }
        if (valid) {
            fireEditingStopped();
        }

        return valid;
    }

    public void addCellEditorListener(CellEditorListener cel) {
        listeners.addElement(cel);
    }

    public void removeCellEditorListener(CellEditorListener cel) {
        listeners.removeElement(cel);
    }

    protected void fireEditingCanceled() {
        ChangeEvent ce = new ChangeEvent(this);
        for (int i = listeners.size() - 1; i >= 0; i--) {
            ((CellEditorListener) listeners.elementAt(i)).editingCanceled(ce);
        }
    }

    protected void fireEditingStopped() {
        ChangeEvent ce = new ChangeEvent(this);
        for (int i = listeners.size() - 1; i >= 0; i--) {
            ((CellEditorListener) listeners.elementAt(i)).editingStopped(ce);
        }
    }
}

