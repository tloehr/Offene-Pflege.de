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


package de.offene_pflege.op.care.med.structure;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import com.jidesoft.wizard.WizardDialog;
import de.offene_pflege.entity.prescription.*;
import de.offene_pflege.entity.system.SYSPropsTools;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.gui.interfaces.CleanablePanel;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.care.med.inventory.DlgNewStocks;
import de.offene_pflege.op.care.med.inventory.PnlMedOrders;
import de.offene_pflege.op.care.med.prodassistant.MedProductWizard;
import de.offene_pflege.op.system.InternalClassACL;
import de.offene_pflege.op.threads.DisplayManager;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.NumberVerifier;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.formula.functions.T;
import org.jdesktop.swingx.HorizontalLayout;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.VerticalLayout;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author tloehr
 */
@Log4j2
public class PnlMed extends CleanablePanel {

    private JPopupMenu menu;
    private CollapsiblePanes searchPanes;
    private JScrollPane jspSearch;
    private JXSearchField txtSuche;
    private JList lstPraep;
    private JToggleButton tbIDs, tbShowClosed;
    private JTextField days_range, filter_gp_hospital;

    private Optional<PnlMedStructure> optPnlMedStructure;
    private Optional<PnlMedOrders> optPnlMedOrders;

    /**
     * Creates new form FrmMed
     */
    public PnlMed(JScrollPane jspSearch) {
        super("opde.medication");
        this.jspSearch = jspSearch;
        optPnlMedStructure = Optional.empty();
        optPnlMedOrders = Optional.empty();
        initPanel();
    }

    @Override
    public void cleanup() {
        super.cleanup();
        SYSTools.unregisterListeners(this);
    }

    @Override
    public void reload() {
        optPnlMedStructure.ifPresent(pnlMedStructure -> pnlMedStructure.reload());
        optPnlMedOrders.ifPresent(pnlMedOrders -> {
            if (tbShowClosed.isSelected()) {
                pnlMedOrders.reload(MedOrderTools.get_medorders(Integer.parseInt(days_range.getText())));
            } else {
                pnlMedOrders.reload(MedOrderTools.get_medorders(0));
            }
        });
    }

    private void initPanel() {
        setLayout(new CardLayout());
        prepareSearchArea();
        orderButtonPressed(); // wir starten immer mit der Bestellung
    }


    private void lstPraepValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstPraepValueChanged
        if (!evt.getValueIsAdjusting() && lstPraep.getSelectedValue() != null) {
            SwingUtilities.invokeLater(() -> {
                if (optPnlMedStructure.isEmpty()) {
                    optPnlMedOrders = Optional.empty();
                    removeAll();
                    optPnlMedStructure = Optional.of(new PnlMedStructure(tbIDs.isSelected(), (MedProducts) lstPraep.getSelectedValue()));
                    add(optPnlMedStructure.get());
                } else {
                    optPnlMedStructure.get().createTree((MedProducts) lstPraep.getSelectedValue());
                }
                revalidate();
                repaint();
            });
        }
    }

    @Override
    public String getInternalClassID() {
        return internalClassID;
    }

    private void txtSucheActionPerformed(ActionEvent evt) {//GEN-FIRST:event_txtSucheActionPerformed
//        treeMed.setCellRenderer(new DefaultTreeCellRenderer());
        //treeMed.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
//        treeMed.setVisible(false);
        if (txtSuche.getText().isEmpty()) {
            lstPraep.setModel(new DefaultListModel());
        } else {
            EntityManager em = OPDE.createEM();
            Query query = em.createQuery("SELECT m FROM MedProducts m WHERE m.text LIKE :bezeichnung ORDER BY m.text");
            query.setParameter("bezeichnung", "%" + txtSuche.getText() + "%");
            lstPraep.setModel(SYSTools.list2dlm(query.getResultList()));
            em.close();
        }
    }


    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout(3));
        mypanel.setBackground(Color.WHITE);

        CollapsiblePane searchPane = new CollapsiblePane(SYSTools.xx(internalClassID));
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);

        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            log.error(e);
        }

        GUITools.addAllComponents(mypanel, addOrders());
        mypanel.add(new JSeparator(SwingConstants.HORIZONTAL));
        GUITools.addAllComponents(mypanel, addStocks());
        mypanel.add(new JSeparator(SwingConstants.HORIZONTAL));
        GUITools.addAllComponents(mypanel, addStructure());


        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();

    }

    private List<Component> addStocks() {
        java.util.List<Component> list = new ArrayList<>();
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.INSERT, internalClassID)) {
            final JideButton addButton = GUITools.createHyperlinkButton(MedProductWizard.internalClassID, SYSConst.icon22wizard, null);

            addButton.addActionListener(actionEvent -> {

                JidePopup currentPopup = new JidePopup();

                WizardDialog wizard = new MedProductWizard(o -> {
                    currentPopup.hidePopup();
                    // keine Maßnahme nötig
                }).getWizard();

                currentPopup.setMovable(false);
                currentPopup.setPreferredSize((new Dimension(800, 450)));
                currentPopup.setResizable(false);
                currentPopup.getContentPane().setLayout(new BoxLayout(currentPopup.getContentPane(), BoxLayout.LINE_AXIS));
                currentPopup.getContentPane().add(wizard.getContentPane());
                currentPopup.setOwner(addButton);
                currentPopup.removeExcludedComponent(addButton);
                currentPopup.setTransient(false);
                currentPopup.setDefaultFocusComponent(wizard.getContentPane());
                //                currentPopup.addPropertyChangeListener("visible", propertyChangeEvent -> currentPopup.getContentPane().getComponentCount());

                GUITools.showPopup(currentPopup, SwingConstants.NORTH_EAST);
            });

            list.add(addButton);
        }

        if (OPDE.isCalcMediUPR1() && OPDE.getAppInfo().isAllowedTo(InternalClassACL.INSERT, internalClassID)) {
            JideButton buchenButton = GUITools.createHyperlinkButton("nursingrecords.inventory.newstocks", SYSConst.icon22addrow, actionEvent -> {
                currentEditor = new DlgNewStocks(null);
                currentEditor.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e);
                        log.debug("CLOSING");
                        currentEditor = null;
                    }

                    @Override
                    public void windowClosed(WindowEvent e) {
                        super.windowClosed(e);
                        log.debug("CLOSED");
                        reload();
                    }
                });
                currentEditor.setVisible(true);
            });
            list.add(buchenButton);
        }
        return list;
    }

    private java.util.List<Component> addStructure() {
        java.util.List<Component> list = new ArrayList<>();

        tbIDs = GUITools.getNiceToggleButton("misc.msg.showIDs");
        tbIDs.addItemListener(e -> optPnlMedStructure.ifPresent(pnlMedStructure -> pnlMedStructure.reload()));
        tbIDs.setHorizontalAlignment(SwingConstants.LEFT);
        list.add(tbIDs);

        txtSuche = new JXSearchField("Suchen");
        txtSuche.setFont(SYSConst.ARIAL14);
        txtSuche.addActionListener(actionEvent -> txtSucheActionPerformed(actionEvent));
        txtSuche.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                SYSTools.markAllTxt(txtSuche);
            }
        });
        list.add(txtSuche);

        lstPraep = new JList(new DefaultListModel());
        lstPraep.setCellRenderer(MedProductsTools.getMedProdukteRenderer());
        lstPraep.setFont(SYSConst.ARIAL14);
        lstPraep.addListSelectionListener(listSelectionEvent -> lstPraepValueChanged(listSelectionEvent));
        lstPraep.setFixedCellWidth(200);

        list.add(new JScrollPane(lstPraep));

        return list;
    }

    private java.util.List<Component> addOrders() {
        java.util.List<Component> list = new ArrayList<>();
        final JideButton orderButton = GUITools.createHyperlinkButton("nursingrecords.inventory.orders", SYSConst.icon22shopping, null);
        orderButton.addActionListener(actionEvent -> {
            orderButtonPressed();
        });
        list.add(orderButton);

        days_range = new JTextField(5);
        days_range.setText(OPDE.getProps().getProperty("opde.medorder:auto_order_day_range", "7"));
        days_range.setInputVerifier(new NumberVerifier(BigDecimal.ONE, BigDecimal.valueOf(31), true));
        days_range.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                SYSPropsTools.storeProp("opde.medorder:auto_order_day_range", ((JTextComponent) e.getSource()).getText().trim(), OPDE.getLogin().getUser());
                reload();
            }
        });

        JPanel pnl = new JPanel(new HorizontalLayout(5));
        pnl.add(new JLabel("Zeitraum:"));
        pnl.add(days_range);
        pnl.add(new JLabel("Tage"));
        list.add(pnl);

        tbShowClosed = GUITools.getNiceToggleButton("Zeige erledigte");
        tbShowClosed.addItemListener(e -> optPnlMedOrders.ifPresent(pnlMedOrders -> {
            reload();
        }));
        tbShowClosed.setHorizontalAlignment(SwingConstants.LEFT);
        list.add(tbShowClosed);

//        list.add(new JLabel("Filter Arzt / KH"));
//        filter_gp_hospital = new JTextField(10);
//        filter_gp_hospital.addActionListener(e -> {
//
//        });

        JButton generate_orders = GUITools.createHyperlinkButton("Bedarf prüfen", SYSConst.icon22calc, null);
        generate_orders.addActionListener(e -> {
            orderButtonPressed(); // just in case

            optPnlMedOrders.ifPresent(pnlMedOrders -> {
                OPDE.getMainframe().setBlocked(true);
                OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), -1, 100));

                SwingWorker worker = new SwingWorker() {

                    @Override
                    protected Object doInBackground() {
                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();
                            MedOrderTools.generate_orders(Integer.parseInt(days_range.getText()), MedOrderTools.get_open_orders(em)).forEach(medOrder -> em.merge(medOrder));
                            em.getTransaction().commit();
                        } catch (OptimisticLockException | RollbackException ole) {
                            log.warn(ole);
                            if (em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                            if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                                OPDE.getMainframe().emptyFrame();
                                OPDE.getMainframe().afterLogin();
                            }
                            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                        } catch (Exception exc) {
                            if (em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                            OPDE.fatal(exc);
                        } finally {
                            em.close();
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        OPDE.getDisplayManager().setProgressBarMessage(null);
                        OPDE.getMainframe().setBlocked(false);
                        reload();
                    }
                };
                worker.execute();
            });
        });
        list.add(generate_orders);


        final JideButton printButton = GUITools.createHyperlinkButton("Bestellung drucken", SYSConst.icon22print2, null);
        printButton.addActionListener(actionEvent -> {
            optPnlMedOrders.ifPresent(pnlMedOrders -> pnlMedOrders.print());
        });
        list.add(printButton);


        return list;
    }

    private void orderButtonPressed() {
        if (optPnlMedOrders.isPresent()) return;
        SwingUtilities.invokeLater(() -> {
            txtSuche.setText(null);
            optPnlMedStructure.ifPresent(pnlMedStructure -> pnlMedStructure.cleanup());
            optPnlMedStructure = Optional.empty();
            removeAll();
            optPnlMedOrders = Optional.of(new PnlMedOrders());
            add(optPnlMedOrders.get());
            reload();
            revalidate();
            repaint();
        });
    }


}
