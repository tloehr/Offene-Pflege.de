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

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import entity.Allowance;
import entity.AllowanceTools;
import entity.files.SYSFilesTools;
import entity.info.Resident;
import entity.info.ResidentTools;
import op.OPDE;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author tloehr
 */
public class PnlAllowance extends CleanablePanel {
    public static final String internalClassID = "admin.residents.cash";

    private Resident currentResident;
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private JXSearchField txtSearch;
    private JComboBox cmbResident;

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
        cashmap.clear();
        cpMap.clear();
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
        jspCash = new JScrollPane();
        cpCash = new CollapsiblePanes();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== pnlBarbetrag ========
        {
            pnlBarbetrag.setLayout(new BoxLayout(pnlBarbetrag, BoxLayout.X_AXIS));

            //======== jspCash ========
            {

                //======== cpCash ========
                {
                    cpCash.setLayout(new BoxLayout(cpCash, BoxLayout.X_AXIS));
                }
                jspCash.setViewportView(cpCash);
            }
            pnlBarbetrag.add(jspCash);
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
//            String html = SYSTools.htmlUmlautConversion(AllowanceTools.getMonthAsHTML(liste, vortrag, bewohner));
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
        cpCash.removeAll();
        cashmap.clear();
        cpMap.clear();

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

            for (Resident resident : lstResidents) {
                createCP4(resident);
            }
            if (currentResident != null) {
                OPDE.getDisplayManager().setMainMessage(ResidentTools.getLabelText(currentResident));
            } else {
                OPDE.getDisplayManager().setMainMessage(" ");
            }
            buildPanel();
        }

    }

    private CollapsiblePane createCP4(final Resident resident) {
        /***
         *                          _        ____ ____  _  _    ______           _     _            _ __
         *       ___ _ __ ___  __ _| |_ ___ / ___|  _ \| || |  / /  _ \ ___  ___(_) __| | ___ _ __ | |\ \
         *      / __| '__/ _ \/ _` | __/ _ \ |   | |_) | || |_| || |_) / _ \/ __| |/ _` |/ _ \ '_ \| __| |
         *     | (__| | |  __/ (_| | ||  __/ |___|  __/|__   _| ||  _ <  __/\__ \ | (_| |  __/ | | | |_| |
         *      \___|_|  \___|\__,_|\__\___|\____|_|      |_| | ||_| \_\___||___/_|\__,_|\___|_| |_|\__| |
         *                                                     \_\                                    /_/
         */
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

        JPanel titlePanelleft = new JPanel();
        titlePanelleft.setLayout(new BoxLayout(titlePanelleft, BoxLayout.LINE_AXIS));
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


        /***
         *      ____       _       _   ____           _     _            _
         *     |  _ \ _ __(_)_ __ | |_|  _ \ ___  ___(_) __| | ___ _ __ | |_
         *     | |_) | '__| | '_ \| __| |_) / _ \/ __| |/ _` |/ _ \ '_ \| __|
         *     |  __/| |  | | | | | |_|  _ <  __/\__ \ | (_| |  __/ | | | |_
         *     |_|   |_|  |_|_| |_|\__|_| \_\___||___/_|\__,_|\___|_| |_|\__|
         *
         */
        final JButton btnPrintResident = new JButton(SYSConst.icon22print);
        btnPrintResident.setPressedIcon(SYSConst.icon22printPressed);
        btnPrintResident.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnPrintResident.setContentAreaFilled(false);
        btnPrintResident.setBorder(null);
        btnPrintResident.setToolTipText(OPDE.lang.getString(internalClassID + ".btnprintresident.tooltip"));
        btnPrintResident.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SYSFilesTools.print(AllowanceTools.getAsHTML(AllowanceTools.getAll(resident), BigDecimal.ZERO, currentResident), true);
            }


        });
        titlePanelright.add(btnPrintResident);

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
        cpResident.setBackground(getBG(resident, 7));

        cpResident.setHorizontalAlignment(SwingConstants.LEADING);
        cpResident.setOpaque(false);

        return cpResident;
    }


    private CollapsiblePane createCP4(final Resident resident, final int year) {
        /***
         *                          _        ____ ____  _  _    ______           _     _            _       _       _ __
         *       ___ _ __ ___  __ _| |_ ___ / ___|  _ \| || |  / /  _ \ ___  ___(_) __| | ___ _ __ | |_    (_)_ __ | |\ \
         *      / __| '__/ _ \/ _` | __/ _ \ |   | |_) | || |_| || |_) / _ \/ __| |/ _` |/ _ \ '_ \| __|   | | '_ \| __| |
         *     | (__| | |  __/ (_| | ||  __/ |___|  __/|__   _| ||  _ <  __/\__ \ | (_| |  __/ | | | |_ _  | | | | | |_| |
         *      \___|_|  \___|\__,_|\__\___|\____|_|      |_| | ||_| \_\___||___/_|\__,_|\___|_| |_|\__( ) |_|_| |_|\__| |
         *                                                     \_\                                     |/             /_/
         */

        final DateTime start = new DateTime(year, 1, 1, 0, 0);
        final DateTime end = new DateTime(year, 12, 31, 23, 59).isAfterNow() ? new DateTime() : new DateTime(year, 12, 31, 23, 59);

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
        JPanel titlePanelleft = new JPanel();
        titlePanelleft.setLayout(new BoxLayout(titlePanelleft, BoxLayout.LINE_AXIS));
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


        /***
         *      ____       _       _ __   __
         *     |  _ \ _ __(_)_ __ | |\ \ / /__  __ _ _ __
         *     | |_) | '__| | '_ \| __\ V / _ \/ _` | '__|
         *     |  __/| |  | | | | | |_ | |  __/ (_| | |
         *     |_|   |_|  |_|_| |_|\__||_|\___|\__,_|_|
         *
         */
        final JButton btnPrintYear = new JButton(SYSConst.icon22print);
        btnPrintYear.setPressedIcon(SYSConst.icon22printPressed);
        btnPrintYear.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnPrintYear.setContentAreaFilled(false);
        btnPrintYear.setBorder(null);
        btnPrintYear.setToolTipText(OPDE.lang.getString(internalClassID + ".btnprintmonth.tooltip"));
        btnPrintYear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SYSFilesTools.print(AllowanceTools.getAsHTML(AllowanceTools.getYear(resident, start.toDate()), carry, currentResident), true);
            }


        });
        titlePanelright.add(btnPrintYear);


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
        cpYear.setBackground(getBG(resident, 9));
//        cp.setCollapsible(user.isActive());

        cpYear.setHorizontalAlignment(SwingConstants.LEADING);
        cpYear.setOpaque(false);

        return cpYear;
    }


    private CollapsiblePane createCP4(final Resident resident, final DateTime month) {
        /***
         *                          _        ____ ____  _  _    ______           _     _            _       ____        _      _____ _              __
         *       ___ _ __ ___  __ _| |_ ___ / ___|  _ \| || |  / /  _ \ ___  ___(_) __| | ___ _ __ | |_    |  _ \  __ _| |_ __|_   _(_)_ __ ___   __\ \
         *      / __| '__/ _ \/ _` | __/ _ \ |   | |_) | || |_| || |_) / _ \/ __| |/ _` |/ _ \ '_ \| __|   | | | |/ _` | __/ _ \| | | | '_ ` _ \ / _ \ |
         *     | (__| | |  __/ (_| | ||  __/ |___|  __/|__   _| ||  _ <  __/\__ \ | (_| |  __/ | | | |_ _  | |_| | (_| | ||  __/| | | | | | | | |  __/ |
         *      \___|_|  \___|\__,_|\__\___|\____|_|      |_| | ||_| \_\___||___/_|\__,_|\___|_| |_|\__( ) |____/ \__,_|\__\___||_| |_|_| |_| |_|\___| |
         *                                                     \_\                                     |/                                           /_/
         */


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

        JPanel titlePanelleft = new JPanel();
        titlePanelleft.setLayout(new BoxLayout(titlePanelleft, BoxLayout.LINE_AXIS));
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

        /***
         *      ____       _       _   __  __             _   _
         *     |  _ \ _ __(_)_ __ | |_|  \/  | ___  _ __ | |_| |__
         *     | |_) | '__| | '_ \| __| |\/| |/ _ \| '_ \| __| '_ \
         *     |  __/| |  | | | | | |_| |  | | (_) | | | | |_| | | |
         *     |_|   |_|  |_|_| |_|\__|_|  |_|\___/|_| |_|\__|_| |_|
         *
         */
        final JButton btnPrintMonth = new JButton(SYSConst.icon22print);
        btnPrintMonth.setPressedIcon(SYSConst.icon22printPressed);
        btnPrintMonth.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnPrintMonth.setContentAreaFilled(false);
        btnPrintMonth.setBorder(null);
        btnPrintMonth.setToolTipText(OPDE.lang.getString(internalClassID + ".btnprintmonth.tooltip"));
        btnPrintMonth.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                if (!cashmap.containsKey(key)) {
                    cashmap.put(key, AllowanceTools.getMonth(resident, month.toDate()));
                }
                SYSFilesTools.print(AllowanceTools.getAsHTML(cashmap.get(key), carry, currentResident), true);
            }
        });
        titlePanelright.add(btnPrintMonth);

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

        cpMonth.setBackground(getBG(resident, 10));

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

                JLabel lblEOM = new JLabel("<html><table border=\"0\">" +
                        "<tr>" +
                        "<td width=\"130\" align=\"left\">" + DateFormat.getDateInstance().format(month.dayOfMonth().withMaximumValue().toDate()) + "</td>" +
                        "<td width=\"400\" align=\"left\">" + OPDE.lang.getString(internalClassID + ".endofmonth") + "</td>" +
                        "<td width=\"100\" align=\"right\"></td>" +
                        "<td width=\"100\" align=\"right\">" +
                        (rowsum.compareTo(BigDecimal.ZERO) < 0 ? "<font color=\"red\">" : "") +
                        cf.format(rowsum) +
                        (rowsum.compareTo(BigDecimal.ZERO) < 0 ? "</font>" : "") +
                        "</td>" +
                        "</tr>" +
                        "</table>" +

                        "</font></html>");
                lblEOM.setBackground(getBG(resident, 11));
                pnlMonth.add(lblEOM);

                for (final Allowance allowance : cashmap.get(key)) {

                    JPanel singlePaneLeft = new JPanel();
                    singlePaneLeft.setLayout(new BoxLayout(singlePaneLeft, BoxLayout.LINE_AXIS));
                    JPanel singlePaneRight = new JPanel();
                    singlePaneRight.setLayout(new BoxLayout(singlePaneRight, BoxLayout.LINE_AXIS));

                    singlePaneLeft.setOpaque(false);
                    singlePaneRight.setOpaque(false);
                    JPanel singlePaneBoth = new JPanel();
                    singlePaneBoth.setOpaque(false);

                    singlePaneBoth.setLayout(new GridBagLayout());
                    ((GridBagLayout) singlePaneBoth.getLayout()).columnWidths = new int[]{0, 80};
                    ((GridBagLayout) singlePaneBoth.getLayout()).columnWeights = new double[]{1.0, 1.0};

                    singlePaneBoth.add(singlePaneLeft, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                    singlePaneBoth.add(singlePaneRight, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                            new Insets(0, 0, 0, 0), 0, 0));


                    JLabel lblSingle = new JLabel("<html><table border=\"0\">" +
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
                    lblSingle.setBackground(getBG(resident, 11));


                    singlePaneLeft.add(lblSingle);

                    /***
                     *      _____    _ _ _
                     *     | ____|__| (_) |_
                     *     |  _| / _` | | __|
                     *     | |__| (_| | | |_
                     *     |_____\__,_|_|\__|
                     *
                     */
                    final JButton btnEdit = new JButton(SYSConst.icon22edit1);
                    btnEdit.setPressedIcon(SYSConst.icon22edit1Pressed);
                    btnEdit.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    btnEdit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    btnEdit.setContentAreaFilled(false);
                    btnEdit.setBorder(null);
                    btnEdit.setToolTipText(OPDE.lang.getString(internalClassID + ".btnedit.tooltip"));
                    btnEdit.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {

                        }
                    });
                    singlePaneRight.add(btnEdit);

                    /***
                     *      ____       _      _
                     *     |  _ \  ___| | ___| |_ ___
                     *     | | | |/ _ \ |/ _ \ __/ _ \
                     *     | |_| |  __/ |  __/ ||  __/
                     *     |____/ \___|_|\___|\__\___|
                     *
                     */
                    final JButton btnDelete = new JButton(SYSConst.icon22delete);
                    btnDelete.setPressedIcon(SYSConst.icon22deletePressed);
                    btnDelete.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    btnDelete.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    btnDelete.setContentAreaFilled(false);
                    btnDelete.setBorder(null);
                    btnDelete.setToolTipText(OPDE.lang.getString(internalClassID + ".btndelete.tooltip"));
                    btnDelete.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            new DlgYesNo(OPDE.lang.getString("misc.questions.delete1") + allowance.getBelegtext() + OPDE.lang.getString("misc.questions.delete2"), SYSConst.icon48delete, new Closure() {
                                @Override
                                public void execute(Object answer) {
                                    if (answer.equals(JOptionPane.YES_OPTION)) {
                                        EntityManager em = OPDE.createEM();
                                        try {
                                            em.getTransaction().begin();
                                            Allowance myAllowance = em.merge(allowance);
                                            em.lock(em.merge(myAllowance.getResident()), LockModeType.OPTIMISTIC);
                                            em.remove(myAllowance);
                                            em.getTransaction().commit();

                                            DateTime txDate = new DateTime(myAllowance.getBelegDatum());
                                            final String keyMonth = myAllowance.getResident().getRID() + "-" + txDate.getYear() + "-" + txDate.getMonthOfYear();
                                            cashmap.get(keyMonth).remove(myAllowance);
                                            createCP4(myAllowance.getResident());
                                            buildPanel();
                                        } catch (OptimisticLockException ole) {
                                            if (em.getTransaction().isActive()) {
                                                em.getTransaction().rollback();
                                            }
                                            if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
                                                OPDE.getMainframe().emptyFrame();
                                                OPDE.getMainframe().afterLogin();
                                            }
                                            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                        } catch (Exception e) {
                                            if (em.getTransaction().isActive()) {
                                                em.getTransaction().rollback();
                                            }
                                            OPDE.fatal(e);
                                        } finally {
                                            em.close();
                                        }
                                    }
                                }
                            });


                        }
                    });
                    singlePaneRight.add(btnDelete);


                    pnlMonth.add(singlePaneBoth);

                    rowsum = rowsum.subtract(allowance.getBetrag());
                }

                JLabel lblBOM = new JLabel("<html><table border=\"0\">" +
                        "<tr>" +
                        "<td width=\"130\" align=\"left\">" + DateFormat.getDateInstance().format(month.dayOfMonth().withMinimumValue().toDate()) + "</td>" +
                        "<td width=\"400\" align=\"left\">" + OPDE.lang.getString(internalClassID + ".startofmonth") + "</td>" +
                        "<td width=\"100\" align=\"right\"></td>" +
                        "<td width=\"100\" align=\"right\">" +
                        (rowsum.compareTo(BigDecimal.ZERO) < 0 ? "<font color=\"red\">" : "") +
                        cf.format(rowsum) +
                        (rowsum.compareTo(BigDecimal.ZERO) < 0 ? "</font>" : "") +
                        "</td>" +
                        "</tr>" +
                        "</table>" +

                        "</font></html>");
                lblBOM.setBackground(getBG(resident, 11));
                pnlMonth.add(lblBOM);

                cpMonth.setContentPane(pnlMonth);
            }
        });

        cpMonth.setHorizontalAlignment(SwingConstants.LEADING);
        cpMonth.setOpaque(false);

        return cpMonth;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel pnlBarbetrag;
    private JScrollPane jspCash;
    private CollapsiblePanes cpCash;
    // End of variables declaration//GEN-END:variables


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
        GUITools.addAllComponents(mypanel, addFilters());

        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();

    }


    private java.util.List<Component> addFilters() {
        java.util.List<Component> list = new ArrayList<Component>();

        txtSearch = new JXSearchField(OPDE.lang.getString("misc.msg.searchphrase"));
        txtSearch.setFont(SYSConst.ARIAL14);
        txtSearch.setInstantSearchDelay(750);
        txtSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cmbResident.setModel(SYSTools.list2cmb(ResidentTools.getBy(txtSearch.getText().trim())));
                lstResidents = new ArrayList<Resident>();
                lstResidents.add((Resident) cmbResident.getSelectedItem());
                currentResident = (Resident) cmbResident.getSelectedItem();
                reloadDisplay();
            }
        });

        list.add(txtSearch);

        cmbResident = new JComboBox();
        cmbResident.setFont(SYSConst.ARIAL14);
        cmbResident.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    lstResidents = new ArrayList<Resident>();
                    lstResidents.add((Resident) e.getItem());
                    currentResident = (Resident) e.getItem();
                    reloadDisplay();
                }
            }
        });
        list.add(cmbResident);

        final JideButton btnAllActiveResidents = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".showallactiveresidents"), null, null);
        btnAllActiveResidents.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                lstResidents = ResidentTools.getAllActive();
                currentResident = null;
                reloadDisplay();
            }
        });
        list.add(btnAllActiveResidents);

        final JideButton btnAllInactiveResidents = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".showallactiveresidents"), null, null);
        btnAllInactiveResidents.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                lstResidents = ResidentTools.getAllInactive();
                currentResident = null;
                reloadDisplay();
            }
        });
        list.add(btnAllInactiveResidents);

        final JideButton btnAllResidents = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".showallresidents"), null, null);
        btnAllResidents.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                lstResidents = ResidentTools.getAllInactive();
                lstResidents.addAll(ResidentTools.getAllActive());
                Collections.sort(lstResidents);
                currentResident = null;
                reloadDisplay();
            }
        });
        list.add(btnAllResidents);

        return list;
    }

    private java.util.List<Component> addCommands() {

        java.util.List<Component> list = new ArrayList<Component>();

        /***
         *      _____       _              _______  __
         *     | ____|_ __ | |_ ___ _ __  |_   _\ \/ /___
         *     |  _| | '_ \| __/ _ \ '__|   | |  \  // __|
         *     | |___| | | | ||  __/ |      | |  /  \\__ \
         *     |_____|_| |_|\__\___|_|      |_| /_/\_\___/
         *
         */
        final JidePopup popupTX = new JidePopup();
        popupTX.setMovable(false);
        PnlTX pnlTX = new PnlTX(new Allowance(currentResident), new Closure() {
            @Override
            public void execute(Object o) {
                OPDE.debug(o);
                if (o != null) {

                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        Allowance myAllowance = em.merge((Allowance) o);
                        em.lock(em.merge(myAllowance.getResident()), LockModeType.OPTIMISTIC);
                        em.getTransaction().commit();

                        DateTime txDate = new DateTime(myAllowance.getBelegDatum());

                        final String keyResident = myAllowance.getResident().getRID();
                        final String keyYear = myAllowance.getResident().getRID() + "-" + txDate.getYear();
                        final String keyMonth = myAllowance.getResident().getRID() + "-" + txDate.getYear() + "-" + txDate.getMonthOfYear();
                        createCP4(myAllowance.getResident());
                        cpMap.remove(keyMonth);
                        if (!cashmap.containsKey(keyMonth)) {
                            cashmap.put(keyMonth, new ArrayList<Allowance>());
                            cashmap.put(keyMonth, AllowanceTools.getMonth(myAllowance.getResident(), myAllowance.getBelegDatum()));
                        }

                        try {
                            cpMap.get(keyResident).setCollapsed(false);
                            cpMap.get(keyYear).setCollapsed(false);
                            cpMap.get(keyMonth).setCollapsed(false);
                        } catch (PropertyVetoException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }

                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                buildPanel();
                                jspCash.getVerticalScrollBar().setValue(SwingUtilities.convertPoint(cpMap.get(keyMonth), cpMap.get(keyMonth).getLocation(), cpCash).y);
                            }
                        });

                    } catch (OptimisticLockException ole) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
                            OPDE.getMainframe().emptyFrame();
                            OPDE.getMainframe().afterLogin();
                        }
                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                    } catch (Exception e) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        OPDE.fatal(e);
                    } finally {
                        em.close();
                    }

                }
            }
        });
        popupTX.setContentPane(pnlTX);
        popupTX.removeExcludedComponent(pnlTX);
        popupTX.setDefaultFocusComponent(pnlTX);

        final JideButton btnNewTX = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".enterTXs"), SYSConst.icon22addUser, null);
        btnNewTX.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                popupTX.setOwner(btnNewTX);
                GUITools.showPopup(popupTX, SwingConstants.NORTH_EAST);
            }
        });
        list.add(btnNewTX);

        return list;
    }

    private Color getBG(Resident resident, int level) {
        if (lstResidents.indexOf(resident) % 2 == 0) {
            return SYSConst.purple_pastel1[level];
        } else {
            return SYSConst.greyscale[level];
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
