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
 * 
 */
package de.offene_pflege.op.care.nursingprocess;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;
import de.offene_pflege.backend.entity.nursingprocess.NursingProcess;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.Pair;
import de.offene_pflege.op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.joda.time.DateTime;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Date;

/**
 * @author tloehr
 */
public class PnlEval extends JPanel {
    public static final String internalClassID = "nursingrecords.nursingprocess.pnleval";
    private Closure actionBlock;
    private boolean disableDate;
    private NursingProcess np;

    /**
     * Creates new form DlgDiscontinue
     */
    public PnlEval(NursingProcess np, Closure actionBlock, boolean disableDate) {
        this.np = np;
        this.actionBlock = actionBlock;
        this.disableDate = disableDate;
        initComponents();
        initPanel();
    }

    private void initPanel() {
        pnlReason.setBorder(new TitledBorder(SYSTools.xx(internalClassID + (disableDate ? ".title4close" : ".title4change"))));

        if (disableDate) {
            lblNextEval.setVisible(false);
            jdcNextEval.setVisible(false);
        } else {
            lblNextEval.setText(SYSTools.xx(internalClassID + ".nextevaldate") + ": ");
            jdcNextEval.setDate(new DateTime().plusWeeks(4).toDate());
            jdcNextEval.setMinSelectableDate(new DateTime().plusDays(1).toDate());
            jdcNextEval.setMaxSelectableDate(new DateTime().plusYears(1).toDate());
        }
        setPreferredSize(new Dimension(525, 250));
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the PrinterForm Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlReason = new JPanel();
        jScrollPane1 = new JScrollPane();
        txtBemerkung = new JTextArea();
        panel2 = new JPanel();
        lblNextEval = new JLabel();
        jdcNextEval = new JDateChooser();
        panel1 = new JPanel();
        btnOK = new JButton();

        //======== this ========
        setLayout(new FormLayout(
                "default, default:grow, $lcgap, default",
                "default, $lgap, fill:default:grow, 3*($lgap, default)"));

        //======== pnlReason ========
        {
            pnlReason.setBorder(new TitledBorder(null, "text", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
                    new Font("Arial", Font.PLAIN, 14)));
            pnlReason.setFont(new Font("Arial", Font.PLAIN, 14));
            pnlReason.setLayout(new BoxLayout(pnlReason, BoxLayout.X_AXIS));

            //======== jScrollPane1 ========
            {

                //---- txtBemerkung ----
                txtBemerkung.setColumns(20);
                txtBemerkung.setRows(5);
                txtBemerkung.setFont(new Font("Arial", Font.PLAIN, 14));
                jScrollPane1.setViewportView(txtBemerkung);
            }
            pnlReason.add(jScrollPane1);
        }
        add(pnlReason, CC.xy(2, 3, CC.FILL, CC.FILL));

        //======== panel2 ========
        {
            panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

            //---- lblNextEval ----
            lblNextEval.setText("text ");
            lblNextEval.setFont(new Font("Arial", Font.PLAIN, 14));
            panel2.add(lblNextEval);

            //---- jdcNextEval ----
            jdcNextEval.setFont(new Font("Arial", Font.PLAIN, 14));
            panel2.add(jdcNextEval);
        }
        add(panel2, CC.xy(2, 5, CC.FILL, CC.DEFAULT));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

            //---- btnOK ----
            btnOK.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnOK.setText(null);
            btnOK.addActionListener(e -> btnOKActionPerformed(e));
            panel1.add(btnOK);
        }
        add(panel1, CC.xy(2, 7, CC.RIGHT, CC.DEFAULT));
    }// </editor-fold>//GEN-END:initComponents

    private void btnOKActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        if (txtBemerkung.getText().trim().isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx(internalClassID + ".textxx"), DisplayMessage.WARNING));
            return;
        }
        if (!disableDate && jdcNextEval.getDate() == null) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx(internalClassID + ".datexx"), DisplayMessage.WARNING));
            return;
        }
        np.setNextEval(disableDate ? new Date() : jdcNextEval.getDate());
        actionBlock.execute(new Pair<NursingProcess, String>(np, txtBemerkung.getText()));
    }//GEN-LAST:event_btnOKActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel pnlReason;
    private JScrollPane jScrollPane1;
    private JTextArea txtBemerkung;
    private JPanel panel2;
    private JLabel lblNextEval;
    private JDateChooser jdcNextEval;
    private JPanel panel1;
    private JButton btnOK;
    // End of variables declaration//GEN-END:variables
}
