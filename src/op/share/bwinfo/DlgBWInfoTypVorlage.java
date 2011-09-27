/*
 * OffenePflege
 * Copyright (C) 2008 Torsten L�hr
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
 * Auf deutsch (freie �bersetzung. Rechtlich gilt die englische Version)
 * Dieses Programm ist freie Software. Sie k�nnen es unter den Bedingungen der GNU General Public License, 
 * wie von der Free Software Foundation ver�ffentlicht, weitergeben und/oder modifizieren, gem�� Version 2 der Lizenz.
 *
 * Die Ver�ffentlichung dieses Programms erfolgt in der Hoffnung, da� es Ihnen von Nutzen sein wird, aber 
 * OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT F�R EINEN 
 * BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, 
 * schreiben Sie an die Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA.
 * 
 */
package op.share.bwinfo;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import op.OPDE;
import op.tools.DBRetrieve;
import op.tools.DlgException;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;
import op.tools.TMResultSet;

/**
 * Dieser Dialog wird gebraucht, wenn man eine neue Frage zu den bestehenden hinzuf�gen m�chte.
 * Es werden nur diejenigen Fragen angezeigt, die zur Zeit nicht gesetzt sind oder bei denen es egal is (IntervalMode = 2).
 * Heimaufenthalte werden generell nicht angezeigt (HAUF).
 * 
 * Alle gew�hlten Fragen werden direkt eingef�gt.
 * 
 * @author  tloehr
 */
public class DlgBWInfoTypVorlage extends javax.swing.JDialog {

    private Frame parent;
    private int katart;
    private String bwkennung;
    private JPopupMenu menuInfo;
    private long bwikid = -1;
    private int intervalmode = -1;

    public int getIntervalmode() {
        return intervalmode;
    }

    /** Creates new form DlgBWInfoTyp */
    public DlgBWInfoTypVorlage(java.awt.Frame parent, String bwkennung, int art) {
        super(parent, true);
        this.parent = parent;
        this.katart = art;
        this.bwkennung = bwkennung;
        initComponents();
        initDialog();
        SYSTools.centerOnParent(parent, this);
        setTitle(SYSTools.getWindowTitle("Informationsauswahl"));
        setVisible(true);
    }

    private void initDialog() {
        txtSuche.setText("");
        reloadInfoTable("");
        reloadVorlagenTable("");
        saveOK();
    }

    private void reloadVorlagenTable(String suche) {
        String sql =
                " SELECT DISTINCT Bezeichnung, Bezeichnung FROM BWInfoVorlagen ";
        if (!suche.equals("")) {
            sql += " WHERE Bezeichnung like ? ";
        }
        sql += " ORDER BY Bezeichnung ";
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            if (!suche.equals("")) {
                suche = op.tools.DBHandling.createSearchPattern(suche);
                stmt.setString(1, suche);
            }
            ResultSet rs = stmt.executeQuery();
            tblVorlagen.setModel(new TMResultSet(rs));
            tblVorlagen.setSelectionMode(DefaultListSelectionModel.SINGLE_INTERVAL_SELECTION);
        } catch (SQLException ex) {
            new DlgException(ex);
        }

        btnOk.setEnabled(tblInfo.getModel().getRowCount() > 0);
    }

    private void reloadInfoTable(String suche) {
        tblInfo.setModel(new TMInfoListe(katart, suche));
        tblInfo.setSelectionMode(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        tblInfo.getColumnModel().getColumn(0).setCellRenderer(new RNDInfoTyp());
        tblInfo.getColumnModel().getColumn(1).setCellRenderer(new RNDInfoTyp());

        jspInfo.dispatchEvent(new ComponentEvent(jspInfo, ComponentEvent.COMPONENT_RESIZED));

        btnOk.setEnabled(tblInfo.getModel().getRowCount() > 0);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        txtSuche = new javax.swing.JTextField();
        btnCancel = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jspInfo = new javax.swing.JScrollPane();
        tblInfo = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblVorlagen = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 18));
        jLabel1.setText("W�hlen Sie die gew�nschte Information");

        jLabel2.setText("Suche:");

        txtSuche.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtSucheCaretUpdate(evt);
            }
        });

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/cancel.png"))); // NOI18N
        btnCancel.setText("Abbrechen");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnOk.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/apply.png"))); // NOI18N
        btnOk.setText("�bernehmen");
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Einzelinformationen"));

        jspInfo.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jspInfoComponentResized(evt);
            }
        });

        tblInfo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblInfo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblInfoMousePressed(evt);
            }
        });
        jspInfo.setViewportView(tblInfo);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jspInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 774, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jspInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Vorlagen"));

        tblVorlagen.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblVorlagen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblVorlagenMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(tblVorlagen);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 774, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 808, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSuche, javax.swing.GroupLayout.DEFAULT_SIZE, 761, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnOk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtSuche, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnOk))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        if (!tblInfo.getSelectionModel().isSelectionEmpty()) {
            saveInfos();
        }
        if (!tblVorlagen.getSelectionModel().isSelectionEmpty()) {
            saveVorlage();
        }
        dispose();
    }//GEN-LAST:event_btnOkActionPerformed

    private void saveInfos() {
        int sel[] = tblInfo.getSelectedRows();

        for (int s = 0; s < sel.length; s++) {
            String bwinftyp = (String) ((TMInfoListe) tblInfo.getModel()).getValueAt(sel[s], TMInfoListe.COL_BWINFTYP);
            intervalmode = ((Integer) ((TMInfoListe) tblInfo.getModel()).getValueAt(sel[s], TMInfoListe.COL_INTERVALMODE)).intValue();
            bwikid = ((Long) ((TMInfoListe) tblInfo.getModel()).getValueAt(sel[s], TMInfoListe.COL_BWIKID)).longValue();
            // Existiert die Frage schon ?
            ArrayList freeIntervals = DBRetrieve.getFreeIntervals("BWInfo", "Von", "Bis", "BWKennung='" + bwkennung + "' AND BWINFTYP='" + bwinftyp + "'");
            if (intervalmode == BWInfo.MODE_INTERVAL_SINGLE_INCIDENTS || intervalmode == BWInfo.MODE_INTERVAL_NOCONSTRAINTS || DBRetrieve.isInFreeIntervals(freeIntervals, SYSCalendar.nowDBDate(), SYSConst.DATE_BIS_AUF_WEITERES)) {
                DBHandling.neueBWInfoEinfuegen(bwinftyp, bwkennung);
            } else {
                JOptionPane.showMessageDialog(this, "Die Information konnte nicht eingegeben werden, da ein Zeitraumkonflikt vorlag.", SYSTools.getWindowTitle("Zeitraumkonflikt"), JOptionPane.WARNING_MESSAGE);
            }
            // Fr�her war hier noch die M�glichkeit bei Verwaltungsfragen einen neuen Zeitraum einzugeben. Den habe ich aber rausgeschmissen.
        }

    }

    private void saveOK() {
        btnOk.setEnabled(!(tblInfo.getSelectionModel().isSelectionEmpty() && tblVorlagen.getSelectionModel().isSelectionEmpty()));
    }

    public long getBWIKID(){
        return bwikid;
    }



    /**
     * Diese Methode speichert alle Informationen, die in der Vorlage stehen
     * erneut als <i>unbeantwortete</i> BWInfos ab.
     * <u>Voraussetzung:</u> Der Zeitraum darf noch nicht belegt sein. In diesem Fall wird diese entsprechende Info einfach ignoriert.
     */
    private void saveVorlage() {
        ListSelectionModel lsm = tblVorlagen.getSelectionModel();
        String vorlage = tblVorlagen.getValueAt(lsm.getLeadSelectionIndex(), 0).toString();
        String sql =
                " SELECT v.BWINFTYP, t.IntervalMode " +
                " FROM BWInfoVorlagen v " +
                " INNER JOIN BWInfoTyp t ON v.BWINFTYP = t.BWINFTYP " +
                " WHERE v.Bezeichnung = ? ";
        ArrayList conflicts = new ArrayList();

        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setString(1, vorlage);
            ResultSet rs = stmt.executeQuery();

            if (rs.first()) {
                rs.beforeFirst();
                while (rs.next()) {
                    //int interval = rs.getInt("IntervalMode");
                    String bwinftyp = rs.getString("BWINFTYP");
                    // Existiert die Frage schon ?
                    ArrayList freeIntervals = DBRetrieve.getFreeIntervals("BWInfo", "Von", "Bis", "BWKennung='" + bwkennung + "' AND BWINFTYP='" + bwinftyp + "'");
                    if (DBRetrieve.isInFreeIntervals(freeIntervals, SYSCalendar.nowDBDate(), SYSConst.DATE_BIS_AUF_WEITERES)) {
                        DBHandling.neueBWInfoEinfuegen(bwinftyp, bwkennung);
                    } else {
                        conflicts.add(DBRetrieve.getSingleValue("BWInfoTyp", "BWInfoKurz", "BWINFTYP", bwinftyp));
                    }
                }
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            new DlgException(ex);
        }

        if (!conflicts.isEmpty()) {
            String conflictList = "Die folgenden Informationen konnten nicht eingef�gt werden, da ein Zeitraumkonflikt vorlag.<ul>";
            Iterator it = conflicts.iterator();
            while (it.hasNext()) {
                conflictList += "<li>" + it.next().toString() + "</li>";
            }
            conflicts.clear();
            conflictList = SYSTools.toHTML(conflictList + "</ul>");
            JOptionPane.showMessageDialog(this, conflictList, SYSTools.getWindowTitle("Zeitraumkonflikt"), JOptionPane.WARNING_MESSAGE);
        }
    }

    private void txtSucheCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtSucheCaretUpdate
        reloadInfoTable(txtSuche.getText());
        reloadVorlagenTable(txtSuche.getText());
        saveOK();
    }//GEN-LAST:event_txtSucheCaretUpdate

    private void tblInfoMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblInfoMousePressed
        if (evt.getClickCount() == 2) {
            btnOk.doClick();
        } else {
            tblVorlagen.clearSelection();
            saveOK();
            if (!evt.isPopupTrigger()) {
                return;
            }
            Point p = evt.getPoint();
            ListSelectionModel lsm = tblInfo.getSelectionModel();
//
//        int col = tblInfo.columnAtPoint(p);
//        int row = tblInfo.rowAtPoint(p);

            SYSTools.unregisterListeners(menuInfo);
            menuInfo = new JPopupMenu();

            // Neue Vorlage hinzuf�gen.
            JMenuItem itemPopupAdd = new JMenuItem("Markierte Infos als neue Vorlage speichern.");
            itemPopupAdd.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    String neueVorlage =
                            JOptionPane.showInputDialog("Bitte geben Sie die Bezeichnung f�r die neue Vorlage ein.", "");
                    if (!SYSTools.catchNull(neueVorlage).equals("")) {
                        op.tools.DBHandling.deleteRecords("BWInfoVorlagen", "Bezeichnung", neueVorlage);
                        int sel[] = tblInfo.getSelectedRows();
                        HashMap hm = new HashMap();
                        hm.put("Bezeichnung", neueVorlage);
                        for (int s = 0; s < sel.length; s++) {
                            String bwinftyp = (String) ((TMInfoListe) tblInfo.getModel()).getValueAt(sel[s], TMInfoListe.COL_BWINFTYP);
                            hm.put("BWINFTYP", bwinftyp);
                            op.tools.DBHandling.insertRecord("BWInfoVorlagen", hm);
                        }
                        hm.clear();
                        reloadVorlagenTable(neueVorlage);
                        saveOK();
                    }
                }
            });
            itemPopupAdd.setEnabled(!lsm.isSelectionEmpty());
            menuInfo.add(itemPopupAdd);
            menuInfo.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
        }
    }//GEN-LAST:event_tblInfoMousePressed

    private void tblVorlagenMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVorlagenMousePressed
        tblInfo.clearSelection();
        saveOK();
    }//GEN-LAST:event_tblVorlagenMousePressed

    private void jspInfoComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspInfoComponentResized
        JScrollPane jsp = (JScrollPane) evt.getComponent();
        Dimension dim = jsp.getSize();
        TableColumnModel tcm1 = tblInfo.getColumnModel();
        int textWidth = dim.width - (140 + 25);
        tcm1.getColumn(TMInfoListe.COL_KATEGORIE).setPreferredWidth(140);
        tcm1.getColumn(TMInfoListe.COL_HTML).setPreferredWidth(textWidth);

    }//GEN-LAST:event_jspInfoComponentResized
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOk;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JScrollPane jspInfo;
    private javax.swing.JTable tblInfo;
    private javax.swing.JTable tblVorlagen;
    private javax.swing.JTextField txtSuche;
    // End of variables declaration//GEN-END:variables
}
