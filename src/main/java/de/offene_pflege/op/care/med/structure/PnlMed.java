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
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.gui.interfaces.CleanablePanel;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.care.med.inventory.DlgNewStocks;
import de.offene_pflege.op.care.med.inventory.PnlMedOrders;
import de.offene_pflege.op.care.med.prodassistant.MedProductWizard;
import de.offene_pflege.op.system.InternalClassACL;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import lombok.extern.log4j.Log4j2;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.VerticalLayout;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
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
    private JToggleButton tbIDs;

    private Optional<PnlMedStructure> optPnlMedStructure;

    /**
     * Creates new form FrmMed
     */
    public PnlMed(JScrollPane jspSearch) {
        super("opde.medication");
        this.jspSearch = jspSearch;
        optPnlMedStructure = Optional.empty();
        initPanel();
    }

    @Override
    public void cleanup() {
        super.cleanup();
        //  https://github.com/tloehr/Offene-Pflege.de/issues/62
        // closes an open modal dialog, if necessary.
        // when the timeout occurs

        SYSTools.unregisterListeners(this);
    }

    @Override
    public void reload() {
        optPnlMedStructure.ifPresent(pnlMedStructure -> pnlMedStructure.reload());
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

        GUITools.addAllComponents(mypanel, addCommands());
        GUITools.addAllComponents(mypanel, addFilters());


        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();

    }

    private java.util.List<Component> addFilters() {
        java.util.List<Component> list = new ArrayList<Component>();

        tbIDs = GUITools.getNiceToggleButton("misc.msg.showIDs");
        tbIDs.addItemListener(e -> reload());
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

    private java.util.List<Component> addCommands() {
        java.util.List<Component> list = new ArrayList<Component>();
        final JideButton orderButton = GUITools.createHyperlinkButton("nursingrecords.inventory.orders", SYSConst.icon22shopping, null);
        orderButton.addActionListener(actionEvent -> {
            orderButtonPressed();
        });

        list.add(orderButton);


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
                        currentEditor = null;
                    }
                });
                currentEditor.setVisible(true);
            });
            list.add(buchenButton);
        }

        return list;
    }

    private void orderButtonPressed() {
        SwingUtilities.invokeLater(() -> {
            txtSuche.setText(null);
            optPnlMedStructure.ifPresent(pnlMedStructure -> pnlMedStructure.cleanup());
            optPnlMedStructure = Optional.empty();
            removeAll();
            add(new PnlMedOrders());
            revalidate();
            repaint();
        });
    }


}
