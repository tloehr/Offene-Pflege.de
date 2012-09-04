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
package op.allowance;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import entity.Allowance;
import entity.AllowanceTools;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.system.Users;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.*;
import op.users.DlgUser;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * @author tloehr
 */
public class PnlAllowance extends CleanablePanel {
    public static final String internalClassID = "admin.residents.cash";
    private TableModelListener tml;

    private Date min;
    private Date max;
    private BigDecimal betrag;
    private JPopupMenu menu;
    private Resident currentResident;
    private CollapsiblePane panelText, panelTime;
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private JComboBox cmbVon, cmbBis, cmbMonat;
    private JXSearchField txtBW;
    private boolean ignoreDateComboEvent;
    private HashMap<Resident, Object[]> searchSaldoButtonMap;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    NumberFormat cf = NumberFormat.getCurrencyInstance();
    Format monthFormatter = new SimpleDateFormat("MMMM yyyy");

    private ArrayList<Resident> lstResidents;
    // this map contains the monthly lists of allowances, once they have been loaded
    // it's a cache to speed up things. the key is a combined string like this:
    // RID-MONTHNUMBER-YEARNUMBER
    private HashMap<String, ArrayList<Allowance>> cashmap;
    private HashMap<String, CollapsiblePane> cpMap;


//    /**
//     * Creates new form FrmBWAttr
//     */
//    private void tblTGMousePressed(MouseEvent e) {
//
//        Point p = e.getPoint();
//        Point p2 = e.getPoint();
//        // Convert a coordinate relative to a component's bounds to screen coordinates
//        SwingUtilities.convertPointToScreen(p2, tblTG);
//
//        final Point screenposition = p2;
//
//        final int row = tblTG.rowAtPoint(p);
//        final int col = tblTG.columnAtPoint(p);
//        OPDE.debug("COLUMN: " + col);
//        final ListSelectionModel lsm = tblTG.getSelectionModel();
//        boolean singleRowSelected = lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex();
//
//
//        if (lsm.getMinSelectionIndex() == lsm.getMaxSelectionIndex()) {
//            lsm.setSelectionInterval(row, row);
//        }
//
//        // Kontext Menü
//        if (singleRowSelected && e.isPopupTrigger()) {
//
//            SYSTools.unregisterListeners(menu);
//            menu = new JPopupMenu();
//
//            final JMenuItem itemPopupEdit = new JMenuItem("Eintrag bearbeiten");
//            itemPopupEdit.addActionListener(new java.awt.event.ActionListener() {
//                public void actionPerformed(java.awt.event.ActionEvent evt) {
//                    final TMBarbetrag tm = (TMBarbetrag) tblTG.getModel();
//                    final Allowance mytg = tm.getListData().get(tm.getModelRow(row));  // Rechnet die Zeile um. Berücksichtigt die Zusammenfassungszeile
//
//                    final JidePopup popup = new JidePopup();
////
//                    popup.setMovable(false);
//                    popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
//
//                    final JTextField txtEditor = new JTextField(20);
//
//                    switch (col) {
//                        case TMBarbetrag.COL_Datum: {
//                            txtEditor.setText(DateFormat.getDateInstance().format(mytg.getBelegDatum()));
//                            break;
//                        }
//                        case TMBarbetrag.COL_Text: {
//                            txtEditor.setText(mytg.getBelegtext().trim());
//                            break;
//                        }
//                        case TMBarbetrag.COL_Betrag: {
//                            NumberFormat nf = DecimalFormat.getCurrencyInstance();
//                            txtEditor.setText(nf.format(mytg.getBetrag()));
//                            break;
//                        }
//                        default: {
//
//                        }
//                    }
//
//                    popup.getContentPane().add(txtEditor);
//
//                    final JButton saveButton = new JButton(new ImageIcon(getClass().getResource("/artwork/22x22/bw/apply.png")));
//                    saveButton.addActionListener(new ActionListener() {
//                        @Override
//                        public void actionPerformed(ActionEvent actionEvent) {
//                            EntityManager em = OPDE.createEM();
//                            try {
//                                em.getTransaction().begin();
//                                Allowance mytg2 = em.merge(mytg);
//                                switch (col) {
//                                    case TMBarbetrag.COL_Datum: {
//                                        mytg2.setBelegDatum(checkDatum(txtEditor.getText(), mytg2.getBelegDatum()));
//                                        break;
//                                    }
//                                    case TMBarbetrag.COL_Text: {
//                                        mytg2.setBelegtext(txtEditor.getText());
//                                        break;
//                                    }
//                                    case TMBarbetrag.COL_Betrag: {
//                                        mytg2.setBetrag(checkBetrag(txtEditor.getText(), mytg2.getBetrag()));
//                                        break;
//                                    }
//                                    default: {
//
//                                    }
//                                }
//                                em.getTransaction().commit();
//                                tm.getListData().set(tm.getListData().indexOf(mytg), mytg2);
//                                tm.fireTableCellUpdated(row, col);
//                                popup.hidePopup();
//
//                                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Datensatz '" + mytg.getBelegtext() + "' geändert.", 2));
//
//                                if (col == TMBarbetrag.COL_Datum && !panelTime.isCollapsed()) {
//                                    if (min.after(mytg2.getBelegDatum())) {
//                                        // Neuer Eintrag liegt ausserhalb des bisherigen Intervals.
//                                        min = SYSCalendar.bom(mytg2.getBelegDatum());
//                                        initSearchTime();
//                                    }
//                                    cmbMonat.setSelectedItem(SYSCalendar.bom(mytg2.getBelegDatum()));
//                                } else if (col == TMBarbetrag.COL_Betrag) {
//                                    summeNeuRechnen();
//                                    BigDecimal saldo = (BigDecimal) searchSaldoButtonMap.get(bewohner)[0];
//                                    JideButton button = (JideButton) searchSaldoButtonMap.get(bewohner)[1];
//
//                                    saldo = saldo.add(mytg2.getBetrag());
//                                    searchSaldoButtonMap.put(bewohner, new Object[]{button, saldo});
//
//                                    String titel = "<html>" + bewohner.getNachname() + ", " + bewohner.getVorname() + " [" + bewohner.getRID() + "] <b><font " + (saldo.compareTo(BigDecimal.ZERO) < 0 ? "color=\"red\"" : "color=\"black\"") + ">" + currencyFormat.format(saldo) + "</font></b></html>";
//                                    button.setText(titel);
//                                }
//
//                            } catch (Exception e) {
//                                em.getTransaction().rollback();
//                            } finally {
//                                em.close();
//                            }
//                        }
//                    });
//                    txtEditor.addActionListener(new ActionListener() {
//                        @Override
//                        public void actionPerformed(ActionEvent actionEvent) {
//                            saveButton.doClick();
//                        }
//                    });
//
////                    popup.setOwner(tblTG);
//                    popup.getContentPane().add(new JPanel().add(saveButton));
//                    popup.setDefaultFocusComponent(txtEditor);
//                    popup.showPopup(screenposition.x, screenposition.y);
//                }
//            });
//            menu.add(itemPopupEdit);
//            itemPopupEdit.setEnabled(col != TMBarbetrag.COL_Zeilensaldo && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE) && ((TMBarbetrag) tblTG.getModel()).isReal(row));
//            //
//            // =====
//            //
//            JMenuItem itemPopupDelete = new JMenuItem("Eintrag löschen");
//            itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {
//                public void actionPerformed(java.awt.event.ActionEvent evt) {
//                    final TMBarbetrag tm = (TMBarbetrag) tblTG.getModel();
//                    final Allowance mytg = tm.getListData().get(tm.getModelRow(row));  // Rechnet die Zeile um. Berücksichtigt die Zusammenfassungszeile
//
//                    final JOptionPane loeschenPane = new JOptionPane("Sie löschen nun den Datensatz '" + mytg.getBelegtext() + "'.\nMöchten Sie das ?", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
//                    loeschenPane.addPropertyChangeListener(new PropertyChangeListener() {
//                        @Override
//                        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
//                            if (propertyChangeEvent.getPropertyName().equals(JOptionPane.VALUE_PROPERTY)) {
//
//                                if (((Integer) propertyChangeEvent.getNewValue()) == JOptionPane.YES_OPTION) {
//                                    EntityTools.delete(mytg);
//                                    tm.getListData().remove(mytg);
//                                    tm.fireTableRowsDeleted(row, row);
//                                    summeNeuRechnen();
//                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Datensatz '" + mytg.getBelegtext() + "' gelöscht.", 2));
//                                }
////                                loeschenPane.setLocation(OPDE.getMainframe().getLocationForDialog(loeschenPane.getSize()));
//                                loeschenPane.setVisible(false);
//                            }
//                        }
//                    });
//                    loeschenPane.setLocation(OPDE.getMainframe().getLocationForDialog(loeschenPane.getSize()));
//                    loeschenPane.setVisible(true);
//                }
//            });
//            menu.add(itemPopupDelete);
//            itemPopupDelete.setEnabled(OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.DELETE) && ((TMBarbetrag) tblTG.getModel()).isReal(row));
//            menu.show(e.getComponent(), (int) p.getX(), (int) p.getY());
//        }
//    }

    public PnlAllowance(JScrollPane jspSearch) {
        super();
        this.jspSearch = jspSearch;
        initComponents();
        prepareSearchArea();
        initPanel();
        reloadDisplay();

//        setVisible(true);
//
//        ignoreDateComboEvent = true;
//        prepareSearchArea();
//        setMinMax();
//        initSearchTime();
//
//        ignoreDateComboEvent = false;

    }


    private void initPanel() {
        lstResidents = new ArrayList<Resident>();
        cpMap = new HashMap<String, CollapsiblePane>();
        cashmap = new HashMap<String, ArrayList<Allowance>>();
    }

    @Override
    public void reload() {
        reloadDisplay();
    }

    @Override
    public void cleanup() {
        lstResidents.clear();
        cpCash.removeAll();
        searchPanes.removeAll();
        searchPanes.removeAll();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlBarbetrag = new JPanel();
        jPanel4 = new JPanel();
        jspCash = new JScrollPane();
        cpCash = new CollapsiblePanes();
        jPanel5 = new JPanel();
        txtDatum = new JTextField();
        txtBelegtext = new JTextField();
        txtBetrag = new JTextField();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== pnlBarbetrag ========
        {
            pnlBarbetrag.setLayout(new FormLayout(
                    "default:grow, $lcgap, pref",
                    "fill:default:grow, $lgap, fill:default, $lgap, $rgap"));

            //======== jPanel4 ========
            {
                jPanel4.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
                jPanel4.setLayout(new FormLayout(
                        "default:grow",
                        "fill:default:grow"));

                //======== jspCash ========
                {

                    //======== cpCash ========
                    {
                        cpCash.setLayout(new BoxLayout(cpCash, BoxLayout.X_AXIS));
                    }
                    jspCash.setViewportView(cpCash);
                }
                jPanel4.add(jspCash, CC.xy(1, 1, CC.DEFAULT, CC.FILL));
            }
            pnlBarbetrag.add(jPanel4, CC.xywh(1, 1, 3, 1));

            //======== jPanel5 ========
            {
                jPanel5.setBorder(LineBorder.createBlackLineBorder());
                jPanel5.setLayout(new FormLayout(
                        "default:grow(0.30000000000000004), $lcgap, default:grow(0.7000000000000001), $lcgap, 30dlu:grow(0.30000000000000004)",
                        "fill:default"));

                //---- txtDatum ----
                txtDatum.setEnabled(false);
                txtDatum.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        txtDatumActionPerformed(e);
                    }
                });
                txtDatum.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        txtDatumFocusGained(e);
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        txtDatumFocusLost(e);
                    }
                });
                jPanel5.add(txtDatum, CC.xy(1, 1, CC.FILL, CC.DEFAULT));

                //---- txtBelegtext ----
                txtBelegtext.setEnabled(false);
                txtBelegtext.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        txtBelegtextActionPerformed(e);
                    }
                });
                txtBelegtext.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        txtBelegtextFocusGained(e);
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        txtBelegtextFocusLost(e);
                    }
                });
                jPanel5.add(txtBelegtext, CC.xy(3, 1, CC.FILL, CC.DEFAULT));

                //---- txtBetrag ----
                txtBetrag.setHorizontalAlignment(SwingConstants.RIGHT);
                txtBetrag.setEnabled(false);
                txtBetrag.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        txtBetragActionPerformed(e);
                    }
                });
                txtBetrag.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        txtBetragFocusGained(e);
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        txtBetragFocusLost(e);
                    }
                });
                jPanel5.add(txtBetrag, CC.xy(5, 1, CC.FILL, CC.DEFAULT));
            }
            pnlBarbetrag.add(jPanel5, CC.xywh(1, 3, 3, 1));
        }
        add(pnlBarbetrag);
    }// </editor-fold>//GEN-END:initComponents

//    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
//        if (!btnEdit.isSelected()) {
//            if (tblTG.getCellEditor() != null) {
//                tblTG.getCellEditor().cancelCellEditing();
//            }
//        }
//        ((TMBarbetrag) tblTG.getModel()).setEditable(btnEdit.isSelected());
//    }//GEN-LAST:event_btnEditActionPerformed

    private void txtBelegtextFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBelegtextFocusLost
        if (txtBelegtext.getText().trim().isEmpty()) {
            txtBelegtext.setText("Geben Sie einen Belegtext ein.");
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Sie können das Belegtextfeld nicht leer lassen.", 2));
//            lblMessage.setText(timeDF.format(new Date()) + " Uhr : " + "Sie können das Belegtextfeld nicht leer lassen.");
        }
    }//GEN-LAST:event_txtBelegtextFocusLost

    private void txtBelegtextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBelegtextFocusGained
        ((JTextField) evt.getSource()).selectAll();
    }//GEN-LAST:event_txtBelegtextFocusGained

    private void txtBetragFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBetragFocusGained
        ((JTextField) evt.getSource()).selectAll();
    }//GEN-LAST:event_txtBetragFocusGained

    private void txtBetragFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBetragFocusLost
//        betrag = SYSTools.parseCurrency(txtBetrag.getText());
//        if (betrag != null) {
//            if (!betrag.equals(BigDecimal.ZERO)) {
//                insert();
//                summeNeuRechnen();
//            } else {
//                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Beträge mit '0,00 " + SYSConst.eurosymbol + "' werden nicht angenommen.", 2));
////                lblMessage.setText(timeDF.format(new Date()) + " Uhr : " + "Beträge mit '0,00 " + SYSConst.eurosymbol + "' werden nicht angenommen.");
//            }
//
//        } else {
//            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Bitte geben Sie Euro Beträge in der folgenden Form ein: '10,0 " + SYSConst.eurosymbol + "'", 2));
////            lblMessage.setText(timeDF.format(new Date()) + " Uhr : " + "Bitte geben Sie Euro Beträge in der folgenden Form ein: '10,0 " + SYSConst.eurosymbol + "'");
//            betrag = BigDecimal.ZERO;
//        }
//        txtBetrag.setText(NumberFormat.getCurrencyInstance().format(betrag));
    }//GEN-LAST:event_txtBetragFocusLost

    private void txtBetragActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBetragActionPerformed
        txtDatum.requestFocus();
    }//GEN-LAST:event_txtBetragActionPerformed

    private void txtBelegtextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBelegtextActionPerformed
        txtBetrag.requestFocus();
    }//GEN-LAST:event_txtBelegtextActionPerformed

    private void txtDatumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDatumActionPerformed
        txtBelegtext.requestFocus();
    }//GEN-LAST:event_txtDatumActionPerformed

    private void txtDatumFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDatumFocusLost
        GregorianCalendar gc;
        try {
            gc = SYSCalendar.erkenneDatum(((JTextField) evt.getSource()).getText());
        } catch (NumberFormatException ex) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Sie haben ein falsches Datum eingegeben. Wurde auf heute zurückgesetzt.", 2));
//            lblMessage.setText(timeDF.format(new Date()) + " Uhr : Sie haben ein falsches Datum eingegeben. Wurde auf heute zurückgesetzt.");
            gc = SYSCalendar.today();
        }
        // Datum in der Zukunft ?
        if (SYSCalendar.sameDay(gc, SYSCalendar.today()) > 0) {
            gc = SYSCalendar.today();
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Sie haben ein Datum in der Zukunft eingegeben. Wurde auf heute zurückgesetzt.", 2));
//            lblMessage.setText(timeDF.format(new Date()) + " Uhr : Sie haben ein Datum in der Zukunft eingegeben. Wurde auf heute zurückgesetzt.");
        }
        ((JTextField) evt.getSource()).setText(SYSCalendar.printGCGermanStyle(gc));
    }//GEN-LAST:event_txtDatumFocusLost

    private void txtDatumFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDatumFocusGained
        ((JTextField) evt.getSource()).selectAll();
    }//GEN-LAST:event_txtDatumFocusGained

//    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
//        TMBarbetrag tm = (TMBarbetrag) tblTG.getModel();
//        printSingle(tm.getListData(), tm.getVortrag());
//    }//GEN-LAST:event_btnPrintActionPerformed

//    private void printSingle(List<Allowance> liste, BigDecimal vortrag) {
//
//        try {
//            // Create temp file.
//            File temp = File.createTempFile("barbetrag", ".html");
//
//            // Delete temp file when program exits.
//            temp.deleteOnExit();
//
//            // Write to temp file
//            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
//            String html = SYSTools.htmlUmlautConversion(AllowanceTools.getEinzelnAsHTML(liste, vortrag, bewohner));
//
//            out.write(html);
//
//            out.close();
//            SYSFilesTools.handleFile(temp, Desktop.Action.OPEN);
//        } catch (IOException e) {
////            new DlgException(e);
//        }
//
//    }

    /**
     * Setzt den Zeitraum, innerhalb dessen die Belege in der Tabelle angezeigt werden können. Nicht unbedingt werden.
     */
//    private void setMinMax() {
//        // Ermittelt die maximale Ausdehnung (chronologisch gesehen) aller Belege für einen bestimmten BW
//
//        min = SYSCalendar.today_date();
//
//        if (bewohner != null) {
//            EntityManager em = OPDE.createEM();
//            Query query = em.createQuery("SELECT MIN(tg.belegDatum) FROM Allowance tg WHERE tg.bewohner = :bewohner");
//            query.setParameter("bewohner", bewohner);
//            min = (Date) query.getSingleResult();
//            em.close();
//        }
//
//        min = SYSCalendar.bom(min == null ? SYSCalendar.today_date() : min);
//        max = SYSCalendar.eom(SYSCalendar.today_date());
//
//    }

//    private void summeNeuRechnen() {
//        TMBarbetrag tm = (TMBarbetrag) tblTG.getModel();
//        BigDecimal zeilensaldo = tm.getZeilenSaldo();
//
////        BigDecimal summe = (BigDecimal) DBHandling.getSingleValue("Taschengeld", "SUM(Betrag)", "BWKennung", currentBW);
//
//        NumberFormat nf = NumberFormat.getCurrencyInstance();
////        lblBetrag.setText(nf.format(zeilensaldo));
////        if (zeilensaldo.compareTo(BigDecimal.ZERO) < 0) {
////            lblBetrag.setForeground(Color.RED);
////        } else {
////            lblBetrag.setForeground(Color.BLACK);
////        }
//
//        OPDE.getDisplayManager().setMainMessage(ResidentTools.getLabelText(bewohner) + ", Saldo: " + nf.format(zeilensaldo));
//
//
//    }
    private void reloadDisplay() {
        /***
         *               _                 _ ____  _           _
         *      _ __ ___| | ___   __ _  __| |  _ \(_)___ _ __ | | __ _ _   _
         *     | '__/ _ \ |/ _ \ / _` |/ _` | | | | / __| '_ \| |/ _` | | | |
         *     | | |  __/ | (_) | (_| | (_| | |_| | \__ \ |_) | | (_| | |_| |
         *     |_|  \___|_|\___/ \__,_|\__,_|____/|_|___/ .__/|_|\__,_|\__, |
         *                                              |_|            |___/
         */


        final boolean withworker = false;
        if (withworker) {

//            OPDE.getMainframe().setBlocked(true);
//            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));
//
//            cpDFN.removeAll();
//
//            SwingWorker worker = new SwingWorker() {
//
//                @Override
//                protected Object doInBackground() throws Exception {
//
//                    int progress = 0;
//                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, 100));
//
//                    for (DFN dfn : DFNTools.getDFNs(resident, jdcDatum.getDate())) {
//                        shiftMAPDFN.get(dfn.getShift()).add(dfn);
//                    }
//
//                    for (Byte shift : new Byte[]{DFNTools.SHIFT_ON_DEMAND, DFNTools.SHIFT_VERY_EARLY, DFNTools.SHIFT_EARLY, DFNTools.SHIFT_LATE, DFNTools.SHIFT_VERY_LATE}) {
//                        shiftMAPpane.put(shift, createCP4(shift));
//                        try {
//                            shiftMAPpane.get(shift).setCollapsed(shift == DFNTools.SHIFT_ON_DEMAND || shift != SYSCalendar.whatShiftIs(new Date()));
//                        } catch (PropertyVetoException e) {
//                            OPDE.debug(e);
//                        }
//                        progress += 20;
//                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, 100));
//                    }
//
//                    return null;
//                }
//
//                @Override
//                protected void done() {
//                    buildPanel(true);
//                    initPhase = false;
//                    OPDE.getDisplayManager().setProgressBarMessage(null);
//                    OPDE.getMainframe().setBlocked(false);
//                }
//            };
//            worker.execute();

        } else {
            lstResidents = ResidentTools.getAllActive();

            for (Resident resident : lstResidents) {
                createCP4(resident);
            }
//            for (Groups group : lstGroups) {
//                createCP4(group);
//            }
            buildPanel();
        }
//        initPhase = false;
    }


    private CollapsiblePane createCP4(final Resident resident) {
        final String key = resident.getRID();
        if (!cpMap.containsKey(key)) {
            cpMap.put(key, new CollapsiblePane());
            try {
                cpMap.get(key).setCollapsed(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
        final CollapsiblePane cpResident = cpMap.get(key);

        /***
         *      _   _ _____    _    ____  _____ ____
         *     | | | | ____|  / \  |  _ \| ____|  _ \
         *     | |_| |  _|   / _ \ | | | |  _| | |_) |
         *     |  _  | |___ / ___ \| |_| | |___|  _ <
         *     |_| |_|_____/_/   \_\____/|_____|_| \_\
         *
         */

        JPanel titlePanelleft = new JPanel();
        titlePanelleft.setLayout(new BoxLayout(titlePanelleft, BoxLayout.LINE_AXIS));


        /***
         *      _     _       _    _           _   _                _   _                _
         *     | |   (_)_ __ | | _| |__  _   _| |_| |_ ___  _ __   | | | | ___  __ _  __| | ___ _ __
         *     | |   | | '_ \| |/ / '_ \| | | | __| __/ _ \| '_ \  | |_| |/ _ \/ _` |/ _` |/ _ \ '__|
         *     | |___| | | | |   <| |_) | |_| | |_| || (_) | | | | |  _  |  __/ (_| | (_| |  __/ |
         *     |_____|_|_| |_|_|\_\_.__/ \__,_|\__|\__\___/|_| |_| |_| |_|\___|\__,_|\__,_|\___|_|
         *
         */
        JideButton btnResident = GUITools.createHyperlinkButton("<html><font size=+1><table border=\"0\">" +
                "<tr>" +

                "<td width=\"520\" align=\"left\">" + resident.toString() + "</td>" +
                "<td width=\"200\" align=\"right\">" + cf.format(AllowanceTools.getSUM(resident)) + "</td>" +

                "</tr>" +
                "</table>" +


                "</font></html>", null, null);

        btnResident.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnResident.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    cpResident.setCollapsed(!cpResident.isCollapsed());
                } catch (PropertyVetoException e) {
                    OPDE.error(e);
                }
            }
        });

        titlePanelleft.add(btnResident);


        JPanel titlePanelright = new JPanel();
        titlePanelright.setLayout(new BoxLayout(titlePanelright, BoxLayout.LINE_AXIS));

        titlePanelleft.setOpaque(false);
        titlePanelright.setOpaque(false);
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);

        titlePanel.setLayout(new GridBagLayout());
        ((GridBagLayout) titlePanel.getLayout()).columnWidths = new int[]{0, 80};
        ((GridBagLayout) titlePanel.getLayout()).columnWeights = new double[]{1.0, 1.0};

        titlePanel.add(titlePanelleft, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 5), 0, 0));

        titlePanel.add(titlePanelright, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 0), 0, 0));

        cpResident.setTitleLabelComponent(titlePanel);
        cpResident.setSlidingDirection(SwingConstants.SOUTH);


        /***
         *           _ _      _            _                               _     _            _   
         *       ___| (_) ___| | _____  __| |   ___  _ __    _ __ ___  ___(_) __| | ___ _ __ | |_ 
         *      / __| | |/ __| |/ / _ \/ _` |  / _ \| '_ \  | '__/ _ \/ __| |/ _` |/ _ \ '_ \| __|
         *     | (__| | | (__|   <  __/ (_| | | (_) | | | | | | |  __/\__ \ | (_| |  __/ | | | |_ 
         *      \___|_|_|\___|_|\_\___|\__,_|  \___/|_| |_| |_|  \___||___/_|\__,_|\___|_| |_|\__|
         *
         */
        cpResident.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                JPanel pnlContent = new JPanel(new VerticalLayout());


                // somebody clicks on the name of the resident. the cash informations
                // are loaded from the database, if necessary.


//                    final String cashmapkey = resident.getRID()+"-"+monthFormatter.format();
//                cashmap.put(resident, AllowanceTools.getAll(resident));

                Pair<Allowance, Allowance> minmax = AllowanceTools.getMinMax(resident);

                if (minmax == null) {
                    return;
                }

                DateTime start = new DateTime(minmax.getFirst().getBelegDatum()).dayOfMonth().withMinimumValue();
                DateTime end = new DateTime(minmax.getSecond().getBelegDatum()).dayOfMonth().withMinimumValue();

                if (!resident.equals(currentResident)) {
                    OPDE.getDisplayManager().setMainMessage(ResidentTools.getLabelText(resident));
                    currentResident = resident;
                }

                for (DateTime year = end; year.compareTo(start) > 0; year = year.minusYears(1)) {
                    pnlContent.add(createCP4(resident, year.getYear()));
                }

                cpResident.setContentPane(pnlContent);
                cpResident.setOpaque(false);
            }

        });
        cpResident.setBackground(SYSConst.purple_pastel1[7]);
//        cp.setCollapsible(user.isActive());

        cpResident.setHorizontalAlignment(SwingConstants.LEADING);
        cpResident.setOpaque(false);

        return cpResident;
    }


    private CollapsiblePane createCP4(final Resident resident, final int year) {
        final String key = resident.getRID() + "-" + Integer.toString(year);
        if (!cpMap.containsKey(key)) {
            cpMap.put(key, new CollapsiblePane());
            try {
                cpMap.get(key).setCollapsed(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
        final CollapsiblePane cpYear = cpMap.get(key);

        /***
         *      _   _ _____    _    ____  _____ ____
         *     | | | | ____|  / \  |  _ \| ____|  _ \
         *     | |_| |  _|   / _ \ | | | |  _| | |_) |
         *     |  _  | |___ / ___ \| |_| | |___|  _ <
         *     |_| |_|_____/_/   \_\____/|_____|_| \_\
         *
         */

        JPanel titlePanelleft = new JPanel();
        titlePanelleft.setLayout(new BoxLayout(titlePanelleft, BoxLayout.LINE_AXIS));


        /***
         *      _     _       _    _           _   _                _   _                _
         *     | |   (_)_ __ | | _| |__  _   _| |_| |_ ___  _ __   | | | | ___  __ _  __| | ___ _ __
         *     | |   | | '_ \| |/ / '_ \| | | | __| __/ _ \| '_ \  | |_| |/ _ \/ _` |/ _` |/ _ \ '__|
         *     | |___| | | | |   <| |_) | |_| | |_| || (_) | | | | |  _  |  __/ (_| | (_| |  __/ |
         *     |_____|_|_| |_|_|\_\_.__/ \__,_|\__|\__\___/|_| |_| |_| |_|\___|\__,_|\__,_|\___|_|
         *
         */
//        JideButton btnResident = GUITools.createHyperlinkButton("<html><font size=+1>" +
//
//                Integer.toString(year) +
//
//                "</font></html>", null, null);


        DateTime from = new DateTime(year, 1, 1, 0, 0);
        DateTime to = new DateTime(year, 1, 1, 0, 0).dayOfYear().withMaximumValue();
        final BigDecimal carry = AllowanceTools.getSUM(resident, to);

        JideButton btnYear = GUITools.createHyperlinkButton("<html><table border=\"0\">" +
                "<tr>" +

                "<td width=\"520\" align=\"left\">" + Integer.toString(year) + "</td>" +
                "<td width=\"200\" align=\"right\">" +
                (carry.compareTo(BigDecimal.ZERO) < 0 ? "<font color=\"red\">" : "") +
                cf.format(carry) +
                (carry.compareTo(BigDecimal.ZERO) < 0 ? "</font>" : "") +
                "</td>" +

                "</tr>" +
                "</table>" +


                "</font></html>", null, null);

        btnYear.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnYear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    cpYear.setCollapsed(!cpYear.isCollapsed());
                } catch (PropertyVetoException e) {
                    OPDE.error(e);
                }
            }
        });

        titlePanelleft.add(btnYear);


        JPanel titlePanelright = new JPanel();
        titlePanelright.setLayout(new BoxLayout(titlePanelright, BoxLayout.LINE_AXIS));

        titlePanelleft.setOpaque(false);
        titlePanelright.setOpaque(false);
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);

        titlePanel.setLayout(new GridBagLayout());
        ((GridBagLayout) titlePanel.getLayout()).columnWidths = new int[]{0, 80};
        ((GridBagLayout) titlePanel.getLayout()).columnWeights = new double[]{1.0, 1.0};

        titlePanel.add(titlePanelleft, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 5), 0, 0));

        titlePanel.add(titlePanelright, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 0), 0, 0));

        cpYear.setTitleLabelComponent(titlePanel);
        cpYear.setSlidingDirection(SwingConstants.SOUTH);


        /***
         *           _ _      _            _
         *       ___| (_) ___| | _____  __| |   ___  _ __    _   _  ___  __ _ _ __
         *      / __| | |/ __| |/ / _ \/ _` |  / _ \| '_ \  | | | |/ _ \/ _` | '__|
         *     | (__| | | (__|   <  __/ (_| | | (_) | | | | | |_| |  __/ (_| | |
         *      \___|_|_|\___|_|\_\___|\__,_|  \___/|_| |_|  \__, |\___|\__,_|_|
         *                                                   |___/
         */
        cpYear.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                JPanel pnlContent = new JPanel(new VerticalLayout());

                if (!resident.equals(currentResident)) {
                    OPDE.getDisplayManager().setMainMessage(ResidentTools.getLabelText(resident));
                    currentResident = resident;
                }

                // somebody clicked on the year
                // monthly informations will be generated. even if there
                // are no allowances for that month

                DateTime start = new DateTime(year, 1, 1, 0, 0);
                DateTime end = new DateTime(year, 12, 31, 23, 59).isAfterNow() ? new DateTime() : new DateTime(year, 12, 31, 23, 59);

                for (DateTime month = end; month.compareTo(start) > 0; month = month.minusMonths(1)) {
//                    CollapsiblePane cpYear = new CollapsiblePane(Integer.toString(year.getYear()));
//                    cpYear.setFont(SYSConst.ARIAL14BOLD);
//                    JPanel pnlYear = new JPanel(new VerticalLayout());

//                    for (DateTime month = year.dayOfYear().withMaximumValue(); month.compareTo(year.dayOfYear().withMinimumValue()) < 0; month = month.minusYears(1)) {
//                        pnlYear.add(createCP4(resident, month.toDate()));
//                    }
//                    cpYear.setContentPane(pnlYear);
                    pnlContent.add(createCP4(resident, month));
                }

                cpYear.setContentPane(pnlContent);
                cpYear.setOpaque(false);
            }

        });
        cpYear.setBackground(SYSConst.purple_pastel1[9]);
//        cp.setCollapsible(user.isActive());

        cpYear.setHorizontalAlignment(SwingConstants.LEADING);
        cpYear.setOpaque(false);

        return cpYear;
    }


    private CollapsiblePane createCP4(final Resident resident, final DateTime month) {

//        final String key = resident.getRID() + "-" + DateFormat.getDateInstance().format(month) + ".xmonth";
        final String key = resident.getRID() + "-" + month.getYear() + "-" + month.getMonthOfYear();
        if (!cpMap.containsKey(key)) {
            cpMap.put(key, new CollapsiblePane());
            try {
                cpMap.get(key).setCollapsed(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
        final CollapsiblePane cpMonth = cpMap.get(key);

        /***
         *      _   _ _____    _    ____  _____ ____
         *     | | | | ____|  / \  |  _ \| ____|  _ \
         *     | |_| |  _|   / _ \ | | | |  _| | |_) |
         *     |  _  | |___ / ___ \| |_| | |___|  _ <
         *     |_| |_|_____/_/   \_\____/|_____|_| \_\
         *
         */

        JPanel titlePanelleft = new JPanel();
        titlePanelleft.setLayout(new BoxLayout(titlePanelleft, BoxLayout.LINE_AXIS));


        /***
         *      _     _       _    _           _   _                _   _                _
         *     | |   (_)_ __ | | _| |__  _   _| |_| |_ ___  _ __   | | | | ___  __ _  __| | ___ _ __
         *     | |   | | '_ \| |/ / '_ \| | | | __| __/ _ \| '_ \  | |_| |/ _ \/ _` |/ _` |/ _ \ '__|
         *     | |___| | | | |   <| |_) | |_| | |_| || (_) | | | | |  _  |  __/ (_| | (_| |  __/ |
         *     |_____|_|_| |_|_|\_\_.__/ \__,_|\__|\__\___/|_| |_| |_| |_|\___|\__,_|\__,_|\___|_|
         *
         */

//        DateTime from = new DateTime(month).dayOfMonth().withMinimumValue();
        final DateTime to = new DateTime(month).dayOfMonth().withMaximumValue();
        final BigDecimal carry = AllowanceTools.getSUM(resident, to);
        JideButton btnMonth = GUITools.createHyperlinkButton("<html><table border=\"0\">" +
                "<tr>" +

                "<td width=\"520\" align=\"left\">" + monthFormatter.format(month.toDate()) + "</td>" +
                "<td width=\"200\" align=\"right\">" +
                (carry.compareTo(BigDecimal.ZERO) < 0 ? "<font color=\"red\">" : "") +
                cf.format(carry) +
                (carry.compareTo(BigDecimal.ZERO) < 0 ? "</font>" : "") +
                "</td>" +
                "</tr>" +
                "</table>" +


                "</font></html>", null, null);

        btnMonth.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnMonth.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    cpMonth.setCollapsed(!cpMonth.isCollapsed());
                } catch (PropertyVetoException e) {
                    OPDE.error(e);
                }
            }
        });

        titlePanelleft.add(btnMonth);


        JPanel titlePanelright = new JPanel();
        titlePanelright.setLayout(new BoxLayout(titlePanelright, BoxLayout.LINE_AXIS));

        titlePanelleft.setOpaque(false);
        titlePanelright.setOpaque(false);
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);

        titlePanel.setLayout(new GridBagLayout());
        ((GridBagLayout) titlePanel.getLayout()).columnWidths = new int[]{0, 80};
        ((GridBagLayout) titlePanel.getLayout()).columnWeights = new double[]{1.0, 1.0};

        titlePanel.add(titlePanelleft, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 5), 0, 0));

        titlePanel.add(titlePanelright, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 0), 0, 0));

        cpMonth.setTitleLabelComponent(titlePanel);
        cpMonth.setSlidingDirection(SwingConstants.SOUTH);

        cpMonth.setBackground(SYSConst.purple_pastel1[10]);

        /***
         *           _ _      _            _                                       _   _
         *       ___| (_) ___| | _____  __| |   ___  _ __    _ __ ___   ___  _ __ | |_| |__
         *      / __| | |/ __| |/ / _ \/ _` |  / _ \| '_ \  | '_ ` _ \ / _ \| '_ \| __| '_ \
         *     | (__| | | (__|   <  __/ (_| | | (_) | | | | | | | | | | (_) | | | | |_| | | |
         *      \___|_|_|\___|_|\_\___|\__,_|  \___/|_| |_| |_| |_| |_|\___/|_| |_|\__|_| |_|
         *
         */
        cpMonth.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {

                if (!resident.equals(currentResident)) {
                    OPDE.getDisplayManager().setMainMessage(ResidentTools.getLabelText(resident));
                    currentResident = resident;
                }

                if (!cashmap.containsKey(key)) {
                    cashmap.put(key, AllowanceTools.getMonth(resident, month.toDate()));
//                    Collections.sort(cashmap.get(key));
                }

                if (cashmap.get(key).isEmpty()) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.nothingtoshow")));
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                cpMonth.setCollapsed(true);
                            } catch (PropertyVetoException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }
                        }
                    });
                    return;
                }

                JPanel pnlMonth = new JPanel(new VerticalLayout());
                BigDecimal rowsum = carry; //AllowanceTools.getSUM(resident, to.dayOfMonth().withMinimumValue().minusDays(1));
                for (Allowance allowance : cashmap.get(key)) {
                    rowsum = rowsum.subtract(allowance.getBetrag());

                    JLabel lbl = new JLabel("<html><table border=\"0\">" +
                            "<tr>" +
                            "<td width=\"130\" align=\"left\">" + DateFormat.getDateInstance().format(allowance.getBelegDatum()) + "</td>" +
                            "<td width=\"400\" align=\"left\">" + allowance.getBelegtext() + "</td>" +
                            "<td width=\"100\" align=\"right\">" +
                            (allowance.getBetrag().compareTo(BigDecimal.ZERO) < 0 ? "<font color=\"red\">" : "") +
                            cf.format(allowance.getBetrag()) +
                            (allowance.getBetrag().compareTo(BigDecimal.ZERO) < 0 ? "</font>" : "") +
                            "</td>" +
                            "<td width=\"100\" align=\"right\">" +
                            (rowsum.compareTo(BigDecimal.ZERO) < 0 ? "<font color=\"red\">" : "") +
                            cf.format(rowsum) +
                            (rowsum.compareTo(BigDecimal.ZERO) < 0 ? "</font>" : "") +
                            "</td>" +
                            "</tr>" +
                            "</table>" +

                            "</font></html>");
                    lbl.setBackground(SYSConst.purple_pastel1[11]);
                    pnlMonth.add(lbl);
                }
                cpMonth.setContentPane(pnlMonth);
            }
        });

        cpMonth.setHorizontalAlignment(SwingConstants.LEADING);
        cpMonth.setOpaque(false);

        return cpMonth;
    }


//    private void updateSummenAngabe() {
//        EntityManager em = OPDE.createEM();
//        Query query = em.createQuery("SELECT SUM(tg.betrag) FROM Allowance tg ");
//        BigDecimal summe = BigDecimal.ZERO;
//        try {
//            summe = (BigDecimal) query.getSingleResult();
//        } catch (NoResultException nre) {
//            summe = BigDecimal.ZERO;
//        } catch (Exception e) {
//            OPDE.fatal(e);
//        }
//
//        NumberFormat nf = NumberFormat.getCurrencyInstance();
//        String summentext = nf.format(summe);
//
//        // Ist auch eine Anzeige für die Vergangenheit gewünscht ?
//        // Nur wenn ein anderer Monat als der aktuelle gewählt ist.
//        if (cmbPast.getSelectedIndex() < cmbPast.getModel().getSize() - 1) {
//            Query queryPast = em.createQuery("SELECT SUM(tg.betrag) FROM Allowance tg WHERE tg.belegDatum <= :datum");
//            queryPast.setParameter("datum", SYSCalendar.eom((Date) cmbPast.getSelectedItem()));
//
//            BigDecimal summePast = BigDecimal.ZERO;
//            try {
//                summePast = (BigDecimal) queryPast.getSingleResult();
//            } catch (NoResultException nre) {
//                summePast = BigDecimal.ZERO;
//            } catch (Exception e) {
//                OPDE.fatal(e);
//            }
//
//            summentext += " (" + nf.format(summePast) + ")";
//        }
//
//        em.close();
//
//        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Gesamtsaldo aller Barbeträge: " + summentext, 10));
//
////        if (summe.compareTo(BigDecimal.ZERO) < 0) {
////            lblSumme.setForeground(Color.RED);
////        } else {
////            lblSumme.setForeground(Color.BLACK);
////        }
//
//    }

//    private void insert() {
//
//        Date datum = new Date(SYSCalendar.erkenneDatum(txtDatum.getText()).getTimeInMillis());
//        TMBarbetrag tm = (TMBarbetrag) tblTG.getModel();
//
//        Allowance allowance = new Allowance(datum, txtBelegtext.getText().trim(), betrag, bewohner, OPDE.getLogin().getUser());
//        EntityTools.persist(allowance);
//        tm.getListData().add(allowance);
//        Collections.sort(tm.getListData());
//
////        schaltet auf den Monat um, in dem der letzte Beleg eingegeben wurde.
////        Sofern die ein bestimmter Monat eingestellt war.
//        if (!panelTime.isCollapsed()) {
////            GregorianCalendar gcDatum = SYSCalendar.toGC(datum);
//            if (min.after(datum)) {
//                // Neuer Eintrag liegt ausserhalb des bisherigen Intervals.
//                min = SYSCalendar.bom(datum);
//                initSearchTime();
//            }
//            cmbMonat.setSelectedItem(SYSCalendar.bom(datum));
//        } else {
//            reloadTable();
//        }
//
////        reloadTable((Date) cmbVon.getSelectedItem(), (Date) cmbBis.getSelectedItem());
//        txtBelegtext.setText("Bitte geben Sie einen Belegtext ein.");
//        txtBetrag.setText("0.00 " + SYSConst.eurosymbol);
//        betrag = BigDecimal.ZERO;
//        txtDatum.requestFocus();
//
//        BigDecimal saldo = (BigDecimal) searchSaldoButtonMap.get(bewohner)[0];
//        JideButton button = (JideButton) searchSaldoButtonMap.get(bewohner)[1];
//
//        saldo = saldo.add(allowance.getBetrag());
//        searchSaldoButtonMap.put(bewohner, new Object[]{saldo, button});
//
//        String titel = "<html>" + bewohner.getNachname() + ", " + bewohner.getVorname() + " [" + bewohner.getRID() + "] <b><font " + (saldo.compareTo(BigDecimal.ZERO) < 0 ? "color=\"red\"" : "color=\"black\"") + ">" + currencyFormat.format(saldo) + "</font></b></html>";
//        button.setText(titel);
//
//
//        // Das hier markiert den zuletzt eingefügten Datensatz.
//        int index = tm.getListData().indexOf(allowance);
//        ListSelectionModel lsm = tblTG.getSelectionModel();
//        lsm.setSelectionInterval(index, index);
//        // Das hier rollt auf den zuletzt eingefügten Datensatz.
//        tblTG.invalidate();
//        Rectangle rect = tblTG.getCellRect(index, 0, true);
//        tblTG.scrollRectToVisible(rect);
//    }

//    private void reloadTable() {
//        reloadTable(null, null);
//    }
//
//    private void reloadTable(Date von, Date bis) {
//        if (bewohner == null) {
//            return;
//        }
//
//        tml = new TableModelListener() {
//            public void tableChanged(TableModelEvent e) {
//                if (e.getColumn() == 2) { // Betrag hat sich geändert
//                    summeNeuRechnen();
//                }
//            }
//        };
//
//        tblTG.setModel(new TMBarbetrag(bewohner, von, bis, false));
//        tblTG.getModel().addTableModelListener(tml);
//        tblTG.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//        tblTG.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
//
//        jspData.dispatchEvent(new ComponentEvent(jspData, ComponentEvent.COMPONENT_RESIZED));
//
//        tblTG.getColumnModel().getColumn(2).setCellRenderer(new CurrencyRenderer());
//        tblTG.getColumnModel().getColumn(3).setCellRenderer(new CurrencyRenderer());
//
////        tblTG.getColumnModel().getColumn(0).setCellEditor(new CEDefault());
////        tblTG.getColumnModel().getColumn(1).setCellEditor(new CEDefault());
////        tblTG.getColumnModel().getColumn(2).setCellEditor(new CEDefault());
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel pnlBarbetrag;
    private JPanel jPanel4;
    private JScrollPane jspCash;
    private CollapsiblePanes cpCash;
    private JPanel jPanel5;
    private JTextField txtDatum;
    private JTextField txtBelegtext;
    private JTextField txtBetrag;
    // End of variables declaration//GEN-END:variables


//    private void prepareSearchArea() {
//        searchPanes = new CollapsiblePanes();
//        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
//        jspSearch.setViewportView(searchPanes);
//
//        JPanel mypanel = new JPanel();
//        mypanel.setLayout(new VerticalLayout());
//        mypanel.setBackground(Color.WHITE);
////        mypanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
//
//        JideButton printButton = GUITools.createHyperlinkButton(OPDE.lang.getString("misc.commands.print"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/printer.png")), new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent actionEvent) {
//                if (bewohner != null) {
//                    TMBarbetrag tm = (TMBarbetrag) tblTG.getModel();
//                    printSingle(tm.getListData(), tm.getVortrag());
//                } else {
//                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Wählen Sie zuerst eine(n) BewohnerIn aus.", 2));
//                }
//            }
//        });
//        mypanel.add(printButton);
//
//        if (OPDE.isAdmin()) {
//            JideButton gesamtSummeButton = GUITools.createHyperlinkButton("Gesamtsumme ermitteln", new ImageIcon(getClass().getResource("/artwork/22x22/bw/kcalc.png")), new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                    updateSummenAngabe();
//                }
//            });
//            mypanel.add(gesamtSummeButton);
//
//            cmbPast = new JComboBox();
//            cmbPast.setModel(SYSCalendar.createMonthList(SYSCalendar.addField(SYSCalendar.today_date(), -2, GregorianCalendar.YEAR), SYSCalendar.today_date()));
//            cmbPast.setSelectedIndex(cmbPast.getModel().getSize() - 1);
//            cmbPast.setRenderer(new ListCellRenderer() {
//                Format formatter = new SimpleDateFormat("MMMM yyyy");
//
//                @Override
//                public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
//                    String text = formatter.format(o);
//                    return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
//                }
//            });
//            cmbPast.addItemListener(new ItemListener() {
//                @Override
//                public void itemStateChanged(ItemEvent itemEvent) {
//                    if (!ignoreDateComboEvent) {
//                        updateSummenAngabe();
//                    }
//                }
//            });
//            cmbPast.setToolTipText("Monat für den die Gesamtsummenberechnung ermittelt werden soll");
//
//            mypanel.add(cmbPast);
//        }
//
//        txtBW = new JXSearchField("Bewohnername oder Kennung");
//        txtBW.setInstantSearchDelay(2000); // 2 Sekunden bevor der Caret Update zieht
//        txtBW.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (txtBW.getText().trim().isEmpty()) {
//                    return;
//                }
//                ResidentTools.findeBW(txtBW.getText().trim(), new Closure() {
//                    @Override
//                    public void execute(Object o) {
//                        if (o == null) {
//                            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Keine(n) passende(n) Bewohner(in) gefunden.", 2));
//                        } else {
//                            bewohner = (Resident) o;
//                            reloadDisplay();
//                        }
//                    }
//                });
//            }
//        });
//
//        mypanel.add(new JXTitledSeparator("Suchkriterien"));
//        mypanel.add(txtBW);
//
//
//        cmbVon = new JComboBox();
//
//        cmbVon.setRenderer(new ListCellRenderer() {
//            Format formatter = new SimpleDateFormat("MMMM yyyy");
//
//            @Override
//            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
//                String text = formatter.format(o);
//                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
//            }
//        });
//
//        cmbVon.addItemListener(new ItemListener() {
//            @Override
//            public void itemStateChanged(ItemEvent itemEvent) {
//                if (ignoreDateComboEvent) {
//                    return;
//                }
//                if (cmbVon.getSelectedIndex() > cmbBis.getSelectedIndex()) {
//                    ignoreDateComboEvent = true;
//                    cmbBis.setSelectedIndex(cmbVon.getSelectedIndex());
//                    ignoreDateComboEvent = false;
//                }
//                reloadTable((Date) cmbVon.getSelectedItem(), (Date) cmbBis.getSelectedItem());
//            }
//        });
//
//        cmbVon.setToolTipText("Anzeigen der Belege ab welchem Monat ?");
//        mypanel.add(new JXTitledSeparator("Zeitraum Von - Bis"));
////        mypanel.add(new TitledSeparator("Von", TitledSeparator.TYPE_PARTIAL_LINE, SwingConstants.LEADING));
//        mypanel.add(cmbVon);
//
//
//        cmbBis = new JComboBox();
//        cmbBis.setRenderer(new ListCellRenderer() {
//            Format formatter = new SimpleDateFormat("MMMM yyyy");
//
//            @Override
//            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
//                String text = formatter.format(o);
//                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
//            }
//        });
//
//        cmbBis.addItemListener(new ItemListener() {
//            @Override
//            public void itemStateChanged(ItemEvent itemEvent) {
//                if (ignoreDateComboEvent) {
//                    return;
//                }
//                if (cmbVon.getSelectedIndex() > cmbBis.getSelectedIndex()) {
//                    ignoreDateComboEvent = true;
//                    cmbVon.setSelectedIndex(cmbBis.getSelectedIndex());
//                    ignoreDateComboEvent = false;
//                }
//                reloadTable((Date) cmbVon.getSelectedItem(), (Date) cmbBis.getSelectedItem());
//            }
//        });
//
////        panelTime.add(new JLabel(" "));
////        mypanel.add(new TitledSeparator("Bis", TitledSeparator.TYPE_PARTIAL_GRADIENT_LINE, SwingConstants.LEADING));
//        mypanel.add(cmbBis);
//        cmbBis.setToolTipText("Anzeigen der Belege bis zu welchem Monat ?");
//
//        cmbMonat = new JComboBox();
//        cmbMonat.setRenderer(new ListCellRenderer() {
//            Format formatter = new SimpleDateFormat("MMMM yyyy");
//
//            @Override
//            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
////                OPDE.debug(o.toString());
//                String text = formatter.format(o);
//                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
//            }
//        });
//
//        cmbMonat.addItemListener(new ItemListener() {
//            @Override
//            public void itemStateChanged(ItemEvent itemEvent) {
//                ignoreDateComboEvent = true;
//                cmbVon.setSelectedItem(cmbMonat.getSelectedItem());
//                cmbBis.setSelectedItem(cmbMonat.getSelectedItem());
//                ignoreDateComboEvent = false;
//                reloadTable((Date) cmbVon.getSelectedItem(), (Date) cmbBis.getSelectedItem());
//            }
//        });
//
////        panelTime.add(new JLabel(" "));
////        TitledSeparator ts = new TitledSeparator("Bestimmter Monat");
//
////        com.jidesoft.swing.PartialLineBorder
////        new PartialLineBorder(Color.WHITE, 1);
//        mypanel.add(new JXTitledSeparator("Bestimmter Monat"));
//
//        cmbMonat.setToolTipText("Anzeigen der Belege nur für einen bestimmten Monat");
//        mypanel.add(cmbMonat);
//
//        JPanel buttonPanel = new JPanel();
//        buttonPanel.setBackground(Color.WHITE);
//        buttonPanel.setLayout(new HorizontalLayout());
//        buttonPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
//
//        JButton homeButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_start.png")));
//        homeButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent actionEvent) {
//                if (cmbMonat.getSelectedIndex() > 0) {
//                    cmbMonat.setSelectedIndex(0);
//                }
//            }
//        });
//        homeButton.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_start_pressed.png")));
//        homeButton.setBorder(null);
//        homeButton.setBorderPainted(false);
//        homeButton.setOpaque(false);
//        homeButton.setContentAreaFilled(false);
//
//        JButton backButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_rev.png")));
//        backButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent actionEvent) {
//                if (cmbMonat.getSelectedIndex() > 0) {
//                    cmbMonat.setSelectedIndex(cmbMonat.getSelectedIndex() - 1);
//                }
//            }
//        });
//        backButton.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_rev_pressed.png")));
//        backButton.setBorder(null);
//        backButton.setBorderPainted(false);
//        backButton.setOpaque(false);
//        backButton.setContentAreaFilled(false);
//
//
//        JButton fwdButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_fwd.png")));
//        fwdButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent actionEvent) {
//                if (cmbMonat.getSelectedIndex() < cmbMonat.getModel().getSize() - 1) {
//                    cmbMonat.setSelectedIndex(cmbMonat.getSelectedIndex() + 1);
//                }
//            }
//        });
//        fwdButton.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_fwd_pressed.png")));
//        fwdButton.setBorder(null);
//        fwdButton.setBorderPainted(false);
//        fwdButton.setOpaque(false);
//        fwdButton.setContentAreaFilled(false);
//
//        JButton endButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_end.png")));
//        endButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent actionEvent) {
//                if (cmbMonat.getSelectedIndex() < cmbMonat.getModel().getSize() - 1) {
//                    cmbMonat.setSelectedIndex(cmbMonat.getModel().getSize() - 1);
//                }
//            }
//        });
//        endButton.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_end_pressed.png")));
//        endButton.setBorder(null);
//        endButton.setBorderPainted(false);
//        endButton.setOpaque(false);
//        endButton.setContentAreaFilled(false);
//
//
//        buttonPanel.add(homeButton);
//        buttonPanel.add(backButton);
//        buttonPanel.add(fwdButton);
//        buttonPanel.add(endButton);
//        mypanel.add(buttonPanel);
//
//        CollapsiblePane searchPane = new CollapsiblePane("Barbeträge");
//        searchPane.setSlidingDirection(SwingConstants.SOUTH);
//        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
//        searchPane.setCollapsible(false);
//        searchPane.setContentPane(mypanel);
//
//        searchPanes.add(searchPane);
//        searchPanes.add(addBySearchBW());
//
//        searchPanes.addExpansion();
//        jspSearch.validate();
//    }


     private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout(3));
        mypanel.setBackground(Color.WHITE);

        CollapsiblePane searchPane = new CollapsiblePane(OPDE.lang.getString(internalClassID));
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);

        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }

        GUITools.addAllComponents(mypanel, addCommands());
//        GUITools.addAllComponents(mypanel, addFilters());

        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();

    }


    private java.util.List<Component> addCommands() {

        java.util.List<Component> list = new ArrayList<Component>();

        /***
         *         _       _     _ _   _
         *        / \   __| | __| | | | |___  ___ _ __
         *       / _ \ / _` |/ _` | | | / __|/ _ \ '__|
         *      / ___ \ (_| | (_| | |_| \__ \  __/ |
         *     /_/   \_\__,_|\__,_|\___/|___/\___|_|
         *
         */
        final JideButton btnNewTX = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".btnAddUser"), SYSConst.icon22addUser, null);
        btnNewTX.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new DlgUser(new Users(), new Closure() {
                    @Override
                    public void execute(Object o) {
                        if (o != null) {
                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();
                                Users user = em.merge((Users) o);
                                em.getTransaction().commit();
                                lstUsers.add(user);
                                reloadDisplay();
                            } catch (Exception e) {
                                em.getTransaction().rollback();
                            } finally {
                                em.close();
                            }
                        }
                    }
                });


//                    new DlgProcess(new QProcess(resident), new Closure() {
//                        @Override
//                        public void execute(Object o) {
//                            if (o != null) {
//                                EntityManager em = OPDE.createEM();
//                                try {
//                                    em.getTransaction().begin();
//                                    QProcess qProcess = em.merge((QProcess) o);
//                                    em.getTransaction().commit();
//                                    processList.add(qProcess);
//                                    qProcessMap.put(qProcess, createCP4(qProcess));
//                                    buildPanel();
//                                } catch (Exception e) {
//                                    em.getTransaction().rollback();
//                                } finally {
//                                    em.close();
//                                }
//                            }
//                        }
//                    });
            }
        });
        list.add(btnNewTX);

        return list;
    }

//    private CollapsiblePane addBySearchBW() {
//        panelText = new CollapsiblePane("Bewohnerliste");
//
//        JPanel mypanel = new JPanel();
//        mypanel.setLayout(new VerticalLayout());
//        mypanel.setBackground(Color.WHITE);
//        searchSaldoButtonMap = new HashMap<Resident, Object[]>();
//
//        EntityManager em = OPDE.createEM();
//
//        Query query = em.createQuery(" " +
//                " SELECT b, SUM(k.betrag) FROM Resident b " +
//                " LEFT JOIN b.konto k " +
//                " WHERE b.station IS NOT NULL " +
//                " GROUP BY b " +
//                " ORDER BY b.nachname, b.vorname, b.bWKennung ");
//
//        List<Object[]> bwSearchList = query.getResultList();
//
//        em.close();
//
//        for (int row = 0; row < bwSearchList.size(); row++) {
//            final Resident myBewohner = (Resident) bwSearchList.get(row)[0];
//            BigDecimal saldo = bwSearchList.get(row)[1] == null ? BigDecimal.ZERO : (BigDecimal) bwSearchList.get(row)[1];
//
//            String titel = "<html>" + myBewohner.getNachname() + ", " + myBewohner.getVorname() + " [" + myBewohner.getRID() + "] <b><font " + (saldo.compareTo(BigDecimal.ZERO) < 0 ? "color=\"red\"" : "color=\"black\"") + ">" + currencyFormat.format(saldo) + "</font></b></html>";
//
//            JideButton button = GUITools.createHyperlinkButton(titel, null, new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                    bewohner = myBewohner;
//                    reloadDisplay();
//                }
//            });
//            button.setButtonStyle(JideButton.FLAT_STYLE);
//            searchSaldoButtonMap.put(myBewohner, new Object[]{saldo, button});
//            mypanel.add(button);
//        }
//
//        try {
//            panelText.setCollapsed(true);
//        } catch (PropertyVetoException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//        panelText.setSlidingDirection(SwingConstants.SOUTH);
//        panelText.setStyle(CollapsiblePane.PLAIN_STYLE);
//        panelText.setContentPane(mypanel);
//        return panelText;
//    }

//    private void initSearchTime() {
//
//        ignoreDateComboEvent = true;
//
//        cmbVon.setModel(SYSCalendar.createMonthList(min, max));
//        cmbVon.setSelectedIndex(cmbVon.getModel().getSize() - 1);
//
//        cmbBis.setModel(SYSCalendar.createMonthList(min, max));
//        cmbBis.setSelectedIndex(cmbBis.getModel().getSize() - 1);
//
//        cmbMonat.setModel(SYSCalendar.createMonthList(min, max));
//        cmbMonat.setSelectedIndex(cmbMonat.getModel().getSize() - 1);
//
//        ignoreDateComboEvent = false;
//    }

    private class CurrencyRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable jTable, Object value, boolean b, boolean b1, int i, int i1) {
            String text = value.toString();
            if (value instanceof BigDecimal) {
                BigDecimal bd = (BigDecimal) value;
                if (bd.compareTo(BigDecimal.ZERO) < 0) {
                    setForeground(Color.RED);
                } else {
                    setForeground(Color.BLACK);
                }

                NumberFormat nf = NumberFormat.getCurrencyInstance();
                text = nf.format(value);
                setHorizontalAlignment(JLabel.RIGHT);
            }
            return super.getTableCellRendererComponent(jTable, text, b, b1, i, i1);    //To change body of overridden methods use File | Settings | File Templates.
        }
    }




    private void buildPanel() {
        cpCash.removeAll();
        cpCash.setLayout(new JideBoxLayout(cpCash, JideBoxLayout.Y_AXIS));


        for (Resident resident : lstResidents) {

            cpCash.add(cpMap.get(resident.getRID()));

        }

        cpCash.addExpansion();
    }

}
