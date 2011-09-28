/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmVorgang.java
 *
 * Created on 03.06.2011, 16:38:35
 */
package op.vorgang;

import com.toedter.calendar.JDateChooser;
import entity.*;
import op.OPDE;
import op.events.DefaultEvent;
import op.events.DefaultEventListener;
import op.share.tools.PnlEditor;
import op.threads.ComponentAlternatingFlash;
import op.threads.SplitAnimator;
import op.threads.TableColumnSizeAnimator;
import op.threads.TextFlash;
import op.tools.InternalClassACL;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import tablemodels.TMElement;
import tablerenderer.*;

import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.util.*;
import java.util.List;

/**
 * @author tloehr
 */
public class FrmVorgang extends javax.swing.JFrame {

    public static final String internalClassID = "opde.tickets";

    protected Vorgaenge aktuellerVorgang;
    protected JPopupMenu menu;
    protected JFrame myFrame;
    protected int split1Percent, split2Percent, split3Percent, split4Percent;
    protected JXTaskPane pnlMyVorgaenge, pnlAlleVorgaenge, pnlVorgaengeRunningOut, pnlVorgaengeByBW, pnlVorgaengeByMA;
    protected boolean savePressedOnce = false, detailsChanged = false, ignoreEvents = false;
    //protected IconFlash iconflasher;
    protected ComponentAlternatingFlash alternatingFlash;
    protected JComboBox cmbBW, cmbMA;
    protected TableColumn delColumn;
    protected HashMap<JComponent, ArrayList<Short>> authorizationMap;


    /**
     * Creates new form FrmVorgang
     */
    public FrmVorgang() {
        this(null);
    }

    public FrmVorgang(Vorgaenge vorgang) {
        initComponents();
        initAuthorizationMap();

        listOwner.setModel(SYSTools.newListModel("Users.findByStatusSorted", new Object[]{"status", 1}));
        cmbKat.setModel(SYSTools.newComboboxModel("VKat.findAllSorted"));

        splitPane1.setDividerLocation(splitPane1.getWidth());
        splitPane2.setDividerLocation(splitPane2.getWidth());
        splitPane3.setDividerLocation(splitPane3.getWidth());
        splitPane4.setDividerLocation(splitPane4.getWidth());
        split1Percent = 100;
        split2Percent = 100;
        split3Percent = 100;
        split4Percent = 100;
        myFrame = this;
        aktuellerVorgang = null;
        btnCancel1.setVisible(false);


        // TODO: Hier müssen noch Rechte rein

        cmbBW = new JComboBox(SYSTools.newComboboxModel("Bewohner.findAllActiveSorted"));
        cmbBW.setSelectedIndex(-1);
        cmbBW.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                getVorgaengeFuerBW();
            }
        });
        cmbMA = new JComboBox(SYSTools.newComboboxModel("Users.findByStatusSorted", new Object[]{"status", 1}));
        cmbMA.setSelectedIndex(-1);
        cmbMA.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                getVorgaengeFuerMA();
            }
        });

        addMyVorgaenge();
        addAlleVorgaenge();
        addVorgaengeFuerBW();
        addVorgaengeFuerMA();
        addAblaufendeVorgaenge();

        //TableColumn column = tblElements.getColumnModel().getColumn(1);

        tblElements.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (tblElements.isEditing()) {
                    tblElements.getColumnModel().getColumn(1).getCellEditor().cancelCellEditing();
                    e.consume();
                }
            }
        });

        loadTable(null);
    }

    protected void addAblaufendeVorgaenge() {
        pnlVorgaengeRunningOut = new JXTaskPane("Vorgänge, die bald ablaufen");
        pnlVorgaengeRunningOut.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/redled.png")));
        pnlVorgaengeRunningOut.setCollapsed(true);

        pnlVorgaengeRunningOut.addPropertyChangeListener("collapsed", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (!(Boolean) evt.getNewValue()) {
                    loadVorgaengeRunningOut();
                } else {
                    pnlVorgaengeRunningOut.removeAll();
                }
            }
        });
        ((Container) taskContainer).add(pnlVorgaengeRunningOut);

    }

    protected void addAlleVorgaenge() {
        pnlAlleVorgaenge = new JXTaskPane("Alle aktiven Vorgänge");
        pnlAlleVorgaenge.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/groupevent.png")));
        pnlAlleVorgaenge.setCollapsed(true);

        pnlAlleVorgaenge.addPropertyChangeListener("collapsed", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (!(Boolean) evt.getNewValue()) {
                    loadAllVorgaenge();
                } else {
                    pnlAlleVorgaenge.removeAll();
                }
            }
        });
        ((Container) taskContainer).add(pnlAlleVorgaenge);
    }


    protected void addVorgaengeFuerBW() {
        pnlVorgaengeByBW = new JXTaskPane("Vorgänge nach BewohnerInnen");
        //pnlVorgaengeByBW.setSpecial(true);
        pnlVorgaengeByBW.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/edit_group.png")));
        pnlVorgaengeByBW.setCollapsed(true);
        //pnlVorgaengeByBW.setLayout(new BoxLayout(pnlVorgaengeByBW, BoxLayout.Y_AXIS));
        pnlVorgaengeByBW.add(cmbBW);
        ((Container) taskContainer).add(pnlVorgaengeByBW);
    }

    protected void getVorgaengeFuerBW() {
        pnlVorgaengeByBW.removeAll();
        pnlVorgaengeByBW.add(cmbBW);
        pnlVorgaengeByBW.add(new JSeparator());
        Bewohner selectedBW = (Bewohner) cmbBW.getSelectedItem();
        if (selectedBW != null) {
            Query query = OPDE.getEM().createNamedQuery("Vorgaenge.findActiveByBW");
            query.setParameter("bewohner", selectedBW);
            List<Vorgaenge> listVorgaenge = query.getResultList();
            Iterator<Vorgaenge> it = listVorgaenge.iterator();

            while (it.hasNext()) {
                final Vorgaenge innervorgang = it.next();
                pnlVorgaengeByBW.add(new AbstractAction() {

                    {
                        putValue(Action.NAME, innervorgang.getTitel());
                        //putValue(Action.SHORT_DESCRIPTION, null);
                    }

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        loadTable(innervorgang);
                        if (btnDetails.isSelected()) {
                            loadDetails(innervorgang);
                        }
                    }
                });
            }
        }
    }

    protected Object[] getTableButtons() {
        return new Object[]{new JButton(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/edit_remove.png"))),
                new TableButtonBehaviour() {
                    @Override
                    public void actionPerformed(TableButtonActionEvent e) {
                        ((TMElement) tblElements.getModel()).removeRow(e.getTable().getSelectedRow());
                    }

                    @Override
                    public boolean isEnabled(JTable table, int row, int col) {
                        VorgangElement element = ((TMElement) tblElements.getModel()).getElement(row);
                        boolean systemBericht = (element instanceof VBericht) && ((VBericht) element).isSystem();
                        return !systemBericht;
                    }
                }};
    }

    protected void getVorgaengeFuerMA() {
        pnlVorgaengeByMA.removeAll();
        pnlVorgaengeByMA.add(cmbMA);
        pnlVorgaengeByMA.add(new JSeparator());
        Users selectedUser = (Users) cmbMA.getSelectedItem();
        if (selectedUser != null) {
            Query query = OPDE.getEM().createNamedQuery("Vorgaenge.findActiveByBesitzer");
            query.setParameter("besitzer", selectedUser);
            List<Vorgaenge> listVorgaenge = query.getResultList();
            Iterator<Vorgaenge> it = listVorgaenge.iterator();

            while (it.hasNext()) {
                final Vorgaenge innervorgang = it.next();
                pnlVorgaengeByMA.add(new AbstractAction() {
                    {
                        putValue(Action.NAME, innervorgang.getTitel());
                        //putValue(Action.SHORT_DESCRIPTION, null);
                    }

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        loadTable(innervorgang);
                        if (btnDetails.isSelected()) {
                            loadDetails(innervorgang);
                        }
                    }
                });
            }
        }
    }

    protected void addVorgaengeFuerMA() {
        pnlVorgaengeByMA = new JXTaskPane("Vorgänge nach BewohnerInnen");
        pnlVorgaengeByMA.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/edit_group.png")));
        pnlVorgaengeByMA.setCollapsed(true);
        //pnlVorgaengeByMA.setLayout(new BoxLayout(pnlVorgaengeByMA, BoxLayout.Y_AXIS));

        pnlVorgaengeByMA.add(cmbMA);
        ((Container) taskContainer).add(pnlVorgaengeByMA);

    }

    protected void addMyVorgaenge() {
        pnlMyVorgaenge = new JXTaskPane();
        pnlMyVorgaenge.setSpecial(true);
        pnlMyVorgaenge.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/identity.png")));
        Query query = OPDE.getEM().createNamedQuery("Vorgaenge.findActiveByBesitzer");
        query.setParameter("besitzer", OPDE.getLogin().getUser());
        ArrayList<Vorgaenge> byBesitzer = new ArrayList(query.getResultList());

        Iterator<Vorgaenge> it = byBesitzer.iterator();

        while (it.hasNext()) {

            final Vorgaenge innervorgang = it.next();

            pnlMyVorgaenge.add(new AbstractAction() {

                {
                    putValue(Action.NAME, innervorgang.getTitel());
                    putValue(Action.SHORT_DESCRIPTION, innervorgang.getBewohner().getNachname());
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    loadTable(innervorgang);
                    if (btnDetails.isSelected()) {
                        loadDetails(innervorgang);
                    }
                }
            });

        }
        pnlMyVorgaenge.setTitle("Meine Vorgänge (" + byBesitzer.size() + ")");
        ((Container) taskContainer).add(pnlMyVorgaenge);
    }

    protected void loadAllVorgaenge() {

        if (pnlAlleVorgaenge.isEnabled()) {

            Query query = OPDE.getEM().createNamedQuery("Vorgaenge.findAllActiveSorted");
            ArrayList<Vorgaenge> alleAktiven = new ArrayList(query.getResultList());

            Iterator<Vorgaenge> it = alleAktiven.iterator();

            while (it.hasNext()) {
                final Vorgaenge innervorgang = it.next();
                pnlAlleVorgaenge.add(new AbstractAction() {
                    {
                        putValue(Action.NAME, innervorgang.getTitel());
                        putValue(Action.SHORT_DESCRIPTION, (innervorgang.getBewohner() == null ? "allgemeiner Vorgang" : innervorgang.getBewohner().getNachname()));
                    }

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        loadTable(innervorgang);
                        if (btnDetails.isSelected()) {
                            loadDetails(innervorgang);
                        }
                    }
                });

            }
        }
    }


    protected void loadVorgaengeRunningOut() {

        if (pnlVorgaengeRunningOut.isEnabled()) {

            Query query = OPDE.getEM().createNamedQuery("Vorgaenge.findActiveRunningOut");
            query.setParameter("wv", SYSCalendar.addDate(new Date(), 4)); // 4 Tage von heute aus gerechnet.
            ArrayList<Vorgaenge> vorgaenge = new ArrayList(query.getResultList());

            Iterator<Vorgaenge> it = vorgaenge.iterator();

            while (it.hasNext()) {
                final Vorgaenge innervorgang = it.next();
                pnlVorgaengeRunningOut.add(new AbstractAction() {
                    {
                        putValue(Action.NAME, innervorgang.getTitel());
                        putValue(Action.SHORT_DESCRIPTION, (innervorgang.getBewohner() == null ? "allgemeiner Vorgang" : innervorgang.getBewohner().getNachname()));
                    }

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        loadTable(innervorgang);
                        if (btnDetails.isSelected()) {
                            loadDetails(innervorgang);
                        }
                    }
                });

            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (alternatingFlash != null) {
            alternatingFlash.stop();
        }
    }

    protected void loadDetails(Vorgaenge vorgang) {
        ignoreEvents = true;
        txtTitel.setText(vorgang.getTitel());
        lblBW.setText(vorgang.getBewohner() == null ? "Allgemeiner Vorgang" : BewohnerTools.getBWLabel(vorgang.getBewohner()));
        lblStart.setText(DateFormat.getDateInstance().format(vorgang.getVon()));
        jdcWV.setDate(vorgang.getWv());
        lblEnde.setText(vorgang.getBis().equals(SYSConst.DATE_BIS_AUF_WEITERES) ? "noch nicht abgeschlossen" : DateFormat.getDateInstance().format(vorgang.getBis()));
        lblCreator.setText(vorgang.getErsteller().getNameUndVorname());
        lblOwner.setText(vorgang.getBesitzer().getNameUndVorname());
        cmbKat.setSelectedItem(vorgang.getKategorie());
        enable(btnNewKat);
        lblPDCA.setText(VorgaengeTools.PDCA[vorgang.getPdca()]);
        cbPDCA.setSelected(vorgang.getPdca() != VorgaengeTools.PDCA_OFF);
        btnPDCAPlus.setEnabled(cbPDCA.isSelected());
        listOwner.setSelectedValue(vorgang.getBesitzer(), true);

        // ACLs
        txtTitel.setEnabled(OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.MANAGER));
        ignoreEvents = false;
    }

    private void btnAssignItemStateChanged(ItemEvent e) {
        if (btnAssign.isSelected()) {
            listOwner.setEnabled(true);

            split2Percent = 65;
            SplitAnimator splitAnimator = new SplitAnimator(splitPane2, split2Percent);
            splitAnimator.addThreadDoneListener(new DefaultEventListener() {
                @Override
                public void eventHappened(DefaultEvent evt) {
                    OPDE.debug("Erledigt");
                }
            });
            splitAnimator.execute();
        } else {
            split2Percent = 100;
            new SplitAnimator(splitPane2, split2Percent).execute();
        }
    }

    private void btnDetailsItemStateChanged(ItemEvent e) {
        if (btnDetails.isSelected()) {
            SYSTools.setXEnabled(taskContainer, false);
            split1Percent = 40;
            loadDetails(aktuellerVorgang);
            new SplitAnimator(splitPane1, split1Percent).execute();
        } else {
            split1Percent = 100;
            new SplitAnimator(splitPane1, split1Percent).execute();
            SYSTools.setXEnabled(taskContainer, true);
        }
        btnEndReactivate.setEnabled(!btnDetails.isSelected() && OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.CANCEL));
        btnDelete.setEnabled(!btnDetails.isSelected() && OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.DELETE));
    }

    private void splitPane1ComponentResized(ComponentEvent e) {
        splitPane1.setDividerLocation(SplitAnimator.getDividerInAbsolutePosition(splitPane1, split1Percent));
    }

    private void splitPane2ComponentResized(ComponentEvent e) {
        splitPane2.setDividerLocation(SplitAnimator.getDividerInAbsolutePosition(splitPane2, split2Percent));
    }

    private void btnAcceptKatActionPerformed(ActionEvent e) {
        VKat vkat = VKatTools.addKat(txtKat.getText());
        cmbKat.setModel(SYSTools.newComboboxModel("VKat.findAllSorted"));
        cmbKat.setSelectedItem(vkat);
        split3Percent = 100;
        new SplitAnimator(splitPane3, split3Percent).execute();
        enable(btnNewKat);
    }

    private void txtKatCaretUpdate(CaretEvent e) {
        btnAcceptKat.setEnabled(!txtKat.getText().isEmpty());
    }

    private void btnApplyActionPerformed(ActionEvent e) {
        if (savePressedOnce) {
            btnApply.setText(null);
            alternatingFlash.stop();
            alternatingFlash = null;
            EntityTools.store(aktuellerVorgang);

            setDetailsChanged(false);
        } else {
            btnApply.setText("WIRKLICH ?");
            alternatingFlash = new ComponentAlternatingFlash(btnApply, btnCancel, new ImageIcon(getClass().getResource("/artwork/22x22/help3.png")));
            alternatingFlash.execute();
        }
        savePressedOnce = !savePressedOnce;
    }

    private void btnCancelActionPerformed(ActionEvent e) {
        if (savePressedOnce) {
            btnApply.setText(null);
            alternatingFlash.stop();
            alternatingFlash = null;
        }

        loadDetails(aktuellerVorgang);
        setDetailsChanged(false);
        savePressedOnce = false;
    }

    private void listOwnerValueChanged(ListSelectionEvent e) {
        if (ignoreEvents) return;
        aktuellerVorgang.setBesitzer((Users) listOwner.getSelectedValue());
        lblEnde.setText(aktuellerVorgang.getBesitzer().getNameUndVorname());
        btnAssign.setSelected(false);
//        split2Percent = 100;
//        new SplitAnimator(splitPane2, split2Percent).execute();
        setDetailsChanged(true);
    }

    protected void setDetailsChanged(boolean changed) {
        btnApply.setEnabled(changed);
        btnCancel.setEnabled(changed);
    }

    private void cmbKatItemStateChanged(ItemEvent e) {
        if (ignoreEvents) return;
        aktuellerVorgang.setKategorie((VKat) cmbKat.getSelectedItem());
        setDetailsChanged(true);
    }

    private void jdcWVPropertyChange(PropertyChangeEvent e) {
        if (ignoreEvents) return;
        if (e.getPropertyName().equals("date")) {
            aktuellerVorgang.setWv(jdcWV.getDate());
            setDetailsChanged(true);
        }
    }

    private void txtTitelCaretUpdate(CaretEvent e) {
        if (ignoreEvents) return;
        aktuellerVorgang.setTitel(txtTitel.getText());
        setDetailsChanged(true);
    }

    private void btnPDCAPlusActionPerformed(ActionEvent e) {
        aktuellerVorgang.setPdca(VorgaengeTools.incPDCA(aktuellerVorgang.getPdca()));
        btnPDCAPlus.setEnabled(false);
    }

    private void cbPDCAItemStateChanged(ItemEvent e) {
        if (ignoreEvents) return;
        if (cbPDCA.isSelected()) {
            aktuellerVorgang.setPdca(VorgaengeTools.PDCA_PLAN);
        } else {
            aktuellerVorgang.setPdca(VorgaengeTools.PDCA_OFF);
        }
        setDetailsChanged(true);
    }

    /**
     * Wenn möglich enabled diese Methode die entsprechende Komponente.
     * Hängt ab von der Gruppenmitgliedschaft des Users.
     *
     * @param comp
     */
    protected void enable(JComponent comp) {
        boolean answer = false;
        if (authorizationMap.containsKey(comp)) {
            ArrayList<Short> list = authorizationMap.get(comp);
            for (Iterator<Short> itAcl = list.iterator(); !answer && itAcl.hasNext(); ) {
                short acl = itAcl.next();
                // ist user Mitglied in einer der zugelassenen ACL Gruppen ?
                answer = OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, acl);
            }
        } else {
            // Im Zweifel zulassen.
            answer = true;
        }

        comp.setEnabled(answer);
    }

    private void splitPane3ComponentResized(ComponentEvent e) {
        splitPane3.setDividerLocation(SplitAnimator.getDividerInAbsolutePosition(splitPane3, split3Percent));
    }

    private void btnNewKatActionPerformed(ActionEvent e) {
        split3Percent = 30;
        new SplitAnimator(splitPane3, split3Percent).execute();
        txtKat.requestFocus();
        btnNewKat.setEnabled(false);
    }

    private void cmbKatFocusGained(FocusEvent e) {
        if (split3Percent < 100) {
            split3Percent = 100;
            new SplitAnimator(splitPane3, split3Percent).execute();
        }
        enable(btnNewKat);
    }

    private void btnEndReactivateActionPerformed(ActionEvent e) {
        if (savePressedOnce) { // Wurde bereits einmal gedrückt. Also ist das hier die Bestätigung.
            alternatingFlash.stop();
            ((JButton) alternatingFlash.getComp1()).setText(null);
            alternatingFlash = null;
            btnCancel1.setVisible(false);

            if (aktuellerVorgang.isAbgeschlossen()) {
                VorgaengeTools.reopenVorgang(aktuellerVorgang);
                new TextFlash(lblMessage, "Vorgang wieder geöffnet", true, false, 600).execute();
                btnEndReactivate.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/shutdown.png")));
                btnEndReactivate.setToolTipText("Vorgang abschließen");
            } else {
                VorgaengeTools.endVorgang(aktuellerVorgang);
                new TextFlash(lblMessage, "Vorgang abgeschlossen", true, false, 600).execute();
                btnEndReactivate.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/reload.png")));
                btnEndReactivate.setToolTipText("Vorgang wieder öffnen");
            }
        } else {
            btnCancel1.setVisible(true);
            btnEndReactivate.setText("WIRKLICH ?");
            alternatingFlash = new ComponentAlternatingFlash(btnEndReactivate, btnCancel1, new ImageIcon(getClass().getResource("/artwork/22x22/help3.png")));
            alternatingFlash.execute();
        }

        setLowerMiddleButtons(savePressedOnce);
        btnEndReactivate.setEnabled(!savePressedOnce);

        savePressedOnce = !savePressedOnce;
    }

    private void btnCancel1ActionPerformed(ActionEvent e) {
        if (alternatingFlash != null) {
            alternatingFlash.stop();
            ((JButton) alternatingFlash.getComp1()).setText(null);

            if (alternatingFlash.getComp1().equals(btnAddBericht)) {
                split4Percent = 100;
                new SplitAnimator(splitPane4, split4Percent).execute();
            }

            alternatingFlash = null;
        }
        savePressedOnce = false;
        btnCancel1.setVisible(false);

        setLowerMiddleButtons(true);

    }

    private void splitPane4ComponentResized(ComponentEvent e) {
        splitPane4.setDividerLocation(SplitAnimator.getDividerInAbsolutePosition(splitPane4, split4Percent));
    }

    private void btnTakeOverActionPerformed(ActionEvent e) {
        aktuellerVorgang.setBesitzer(OPDE.getLogin().getUser());
        lblEnde.setText(aktuellerVorgang.getBesitzer().getNameUndVorname());
        setDetailsChanged(true);
    }

    private void btnDeleteActionPerformed(ActionEvent e) {
        if (savePressedOnce) { // Wurde bereits einmal gedrückt. Also ist das hier die Bestätigung.
            alternatingFlash.stop();
            btnDelete.setText(null);
            alternatingFlash = null;
            btnCancel1.setVisible(false);
            VorgaengeTools.deleteVorgang(aktuellerVorgang);
            btnDetails.setSelected(false);
            loadTable(null);
        } else {
            btnCancel1.setVisible(true);
            btnDelete.setText("WIRKLICH ?");
            alternatingFlash = new ComponentAlternatingFlash(btnDelete, btnCancel1, new ImageIcon(getClass().getResource("/artwork/22x22/help3.png")));
            alternatingFlash.execute();
        }

        setLowerMiddleButtons(savePressedOnce);
        btnDelElement.setEnabled(!savePressedOnce);

        savePressedOnce = !savePressedOnce;
    }

    private void btnCancelKatActionPerformed(ActionEvent e) {
        txtKat.setText("");
        split3Percent = 100;
        new SplitAnimator(splitPane3, split3Percent).execute();
        enable(btnNewKat);
    }

    private void btnSystemInfoItemStateChanged(ItemEvent e) {
        loadTable(aktuellerVorgang);
    }

    private void btnDelElementItemStateChanged(ItemEvent e) {
        loadTable(aktuellerVorgang);
    }

    private void btnAddVorgangActionPerformed(ActionEvent e) {
        new TextFlash(lblMessage, "Vorgang abgeschlossen", true, false, 1000).execute();
    }


    protected void loadTable(Vorgaenge vorgang) {
        aktuellerVorgang = vorgang;

        if (vorgang == null) {
            tblElements.setModel(new DefaultTableModel());
        } else {

            List<VorgangElement> elements = new ArrayList<VorgangElement>(VorgaengeTools.findElementeByVorgang(vorgang, btnSystemInfo.isSelected()));

            tblElements.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

            tblElements.setModel(new TMElement(elements));
            tblElements.getColumnModel().getColumn(TMElement.COL_PIT).setCellRenderer(new RNDHTML());
            tblElements.getColumnModel().getColumn(TMElement.COL_CONTENT).setCellRenderer(new RNDHTML());
            tblElements.getColumnModel().getColumn(TMElement.COL_PIT).setHeaderValue("Datum / MA");
            tblElements.getColumnModel().getColumn(TMElement.COL_CONTENT).setHeaderValue("Inhalt");

            if (btnDelElement.isSelected()) {
                // ButtonsRenderer und ButtonsEditor sind dafür da, damit man in den Tabellen Spalten Knöpfe einfügen kann
                // Es gibt immer einen Cancel Button und einen Menge von Funktionsknöpfe. Was diese Funktionsknöpfe
                // machen sollen, steht in den Actionlistenern, die man mit übergibt.
                tblElements.addColumn(new TableColumn(TMElement.COL_OPERATIONS, 0,
                        //
                        new ButtonsRenderer(new JButton(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/cancel.png"))),
                                getTableButtons()),
                        //
                        new ButtonsEditor(tblElements, new JButton(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/cancel.png"))),
                                getTableButtons()
                        )
                )
                );
                tblElements.getColumnModel().getColumn(TMElement.COL_OPERATIONS).setHeaderValue("--");
                new TableColumnSizeAnimator(jspElements, tblElements.getColumnModel().getColumn(TMElement.COL_OPERATIONS), 150).execute();
                //OPDE.debug(tblElements.getColumnCount());
            }

            jspElements.dispatchEvent(new ComponentEvent(jspElements, ComponentEvent.COMPONENT_RESIZED));

            if (aktuellerVorgang.isAbgeschlossen()) {
                btnEndReactivate.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/reload.png")));
            } else {
                btnEndReactivate.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/shutdown.png")));
            }


        }

        btnEndReactivate.setEnabled(vorgang != null && !btnDetails.isSelected() && OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.CANCEL));
        btnDelete.setEnabled(vorgang != null && !btnDetails.isSelected() && OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.DELETE));

        btnAddBericht.setEnabled(vorgang != null && OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE));
        btnSystemInfo.setEnabled(vorgang != null && OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.MANAGER));
        btnDelElement.setEnabled(vorgang != null && OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.MANAGER));
        btnDetails.setEnabled(vorgang != null && OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE));
        btnPrint.setEnabled(vorgang != null && OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.PRINT));

        OPDE.debug(tblElements.getColumnCount());

    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        btnAddBericht = new JButton();
        btnDetails = new JToggleButton();
        btnAddVorgang = new JButton();
        splitPane1 = new JSplitPane();
        splitPane4 = new JSplitPane();
        jspElements = new JScrollPane();
        tblElements = new JTable();
        pnlEditor = new PnlEditor();
        splitPane2 = new JSplitPane();
        pnlDetails = new JPanel();
        btnTakeOver = new JButton();
        btnAssign = new JToggleButton();
        label1 = new JLabel();
        lblBW = new JLabel();
        label2 = new JLabel();
        label3 = new JLabel();
        label4 = new JLabel();
        label5 = new JLabel();
        label6 = new JLabel();
        splitPane3 = new JSplitPane();
        panel1 = new JPanel();
        cmbKat = new JComboBox();
        btnNewKat = new JButton();
        panel2 = new JPanel();
        txtKat = new JTextField();
        btnAcceptKat = new JButton();
        btnCancelKat = new JButton();
        cbPDCA = new JCheckBox();
        txtTitel = new JTextField();
        lblStart = new JLabel();
        lblEnde = new JLabel();
        lblCreator = new JLabel();
        lblOwner = new JLabel();
        vSpacer1 = new JPanel(null);
        lblPDCA = new JLabel();
        btnPDCAPlus = new JButton();
        jdcWV = new JDateChooser();
        scrollPane1 = new JScrollPane();
        listOwner = new JList();
        btnPrint = new JButton();
        scrollPane2 = new JScrollPane();
        taskContainer = new JXTaskPaneContainer();
        btnCancel = new JButton();
        btnApply = new JButton();
        btnEndReactivate = new JButton();
        btnDelete = new JButton();
        btnCancel1 = new JButton();
        btnSystemInfo = new JToggleButton();
        btnDelElement = new JToggleButton();
        lblMessage = new JLabel();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();

        //---- btnAddBericht ----
        btnAddBericht.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/edit_add.png")));
        btnAddBericht.setToolTipText("Neuen Bericht schreiben");
        btnAddBericht.setEnabled(false);
        btnAddBericht.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnAddBerichtActionPerformed(e);
            }
        });

        //---- btnDetails ----
        btnDetails.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/graphic-design.png")));
        btnDetails.setToolTipText("Details anzeigen / \u00e4ndern");
        btnDetails.setEnabled(false);
        btnDetails.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                btnDetailsItemStateChanged(e);
            }
        });

        //---- btnAddVorgang ----
        btnAddVorgang.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/edit_add.png")));
        btnAddVorgang.setToolTipText("Neuen Vorgang erstellen (nicht Bewohnerbezogen)");
        btnAddVorgang.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnAddVorgangActionPerformed(e);
            }
        });

        //======== splitPane1 ========
        {
            splitPane1.setDividerLocation(300);
            splitPane1.setEnabled(false);
            splitPane1.setDividerSize(5);
            splitPane1.setDoubleBuffered(true);
            splitPane1.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    splitPane1ComponentResized(e);
                }
            });

            //======== splitPane4 ========
            {
                splitPane4.setOrientation(JSplitPane.VERTICAL_SPLIT);
                splitPane4.setDividerLocation(300);
                splitPane4.setEnabled(false);
                splitPane4.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        splitPane4ComponentResized(e);
                    }
                });

                //======== jspElements ========
                {
                    jspElements.addComponentListener(new ComponentAdapter() {
                        @Override
                        public void componentResized(ComponentEvent e) {
                            jspElementsComponentResized(e);
                        }
                    });

                    //---- tblElements ----
                    tblElements.setModel(new DefaultTableModel(
                            new Object[][]{
                                    {null, null, null, null},
                                    {null, null, null, null},
                                    {null, null, null, null},
                                    {null, null, null, null},
                            },
                            new String[]{
                                    "Title 1", "Title 2", "Title 3", "Title 4"
                            }
                    ) {
                        Class<?>[] columnTypes = new Class<?>[]{
                                Object.class, Object.class, Object.class, Object.class
                        };

                        @Override
                        public Class<?> getColumnClass(int columnIndex) {
                            return columnTypes[columnIndex];
                        }
                    });
                    tblElements.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    jspElements.setViewportView(tblElements);
                }
                splitPane4.setTopComponent(jspElements);
                splitPane4.setBottomComponent(pnlEditor);
            }
            splitPane1.setLeftComponent(splitPane4);

            //======== splitPane2 ========
            {
                splitPane2.setDividerSize(5);
                splitPane2.setDividerLocation(500);
                splitPane2.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        splitPane2ComponentResized(e);
                    }
                });

                //======== pnlDetails ========
                {

                    //---- btnTakeOver ----
                    btnTakeOver.setFont(new Font("Lucida Grande", Font.BOLD, 14));
                    btnTakeOver.setToolTipText("Vorgang \u00fcbernehmen");
                    btnTakeOver.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/1leftarrow.png")));
                    btnTakeOver.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnTakeOverActionPerformed(e);
                        }
                    });

                    //---- btnAssign ----
                    btnAssign.setFont(new Font("Lucida Grande", Font.BOLD, 14));
                    btnAssign.setToolTipText("Vorgang an anderen Benutzer \u00fcberweisen");
                    btnAssign.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/1rightarrow.png")));
                    btnAssign.setBackground(new Color(204, 238, 238));
                    btnAssign.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            btnAssignItemStateChanged(e);
                        }
                    });

                    //---- label1 ----
                    label1.setText("Titel");

                    //---- lblBW ----
                    lblBW.setText("Allgemeiner Vorgang");
                    lblBW.setFont(new Font("Lucida Grande", Font.BOLD, 16));
                    lblBW.setForeground(Color.blue);

                    //---- label2 ----
                    label2.setText("Erstellt am");

                    //---- label3 ----
                    label3.setText("Wiedervorlage");

                    //---- label4 ----
                    label4.setText("Abgeschlossen am");

                    //---- label5 ----
                    label5.setText("Erstellt von");

                    //---- label6 ----
                    label6.setText("Wird bearbeitet von");

                    //======== splitPane3 ========
                    {
                        splitPane3.setEnabled(false);
                        splitPane3.addComponentListener(new ComponentAdapter() {
                            @Override
                            public void componentResized(ComponentEvent e) {
                                splitPane3ComponentResized(e);
                            }
                        });

                        //======== panel1 ========
                        {
                            panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

                            //---- cmbKat ----
                            cmbKat.setFont(new Font("Lucida Grande", Font.BOLD, 14));
                            cmbKat.setToolTipText("Kategorie des Vorgangs");
                            cmbKat.addItemListener(new ItemListener() {
                                @Override
                                public void itemStateChanged(ItemEvent e) {
                                    cmbKatItemStateChanged(e);
                                }
                            });
                            cmbKat.addFocusListener(new FocusAdapter() {
                                @Override
                                public void focusGained(FocusEvent e) {
                                    cmbKatFocusGained(e);
                                }
                            });
                            panel1.add(cmbKat);

                            //---- btnNewKat ----
                            btnNewKat.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/edit_add.png")));
                            btnNewKat.setToolTipText("Neue Kategorie hinzuf\u00fcgen");
                            btnNewKat.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    btnNewKatActionPerformed(e);
                                }
                            });
                            panel1.add(btnNewKat);
                        }
                        splitPane3.setLeftComponent(panel1);

                        //======== panel2 ========
                        {
                            panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

                            //---- txtKat ----
                            txtKat.setToolTipText("Name der neuen Kategorie");
                            txtKat.addCaretListener(new CaretListener() {
                                @Override
                                public void caretUpdate(CaretEvent e) {
                                    txtKatCaretUpdate(e);
                                }
                            });
                            panel2.add(txtKat);

                            //---- btnAcceptKat ----
                            btnAcceptKat.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/apply.png")));
                            btnAcceptKat.setToolTipText("Kategorie \u00fcbernehmen");
                            btnAcceptKat.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    btnAcceptKatActionPerformed(e);
                                }
                            });
                            panel2.add(btnAcceptKat);

                            //---- btnCancelKat ----
                            btnCancelKat.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/cancel.png")));
                            btnCancelKat.setToolTipText("Eingabe abbrechen");
                            btnCancelKat.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    btnCancelKatActionPerformed(e);
                                }
                            });
                            panel2.add(btnCancelKat);
                        }
                        splitPane3.setRightComponent(panel2);
                    }

                    //---- cbPDCA ----
                    cbPDCA.setText("PDCA Zyklus");
                    cbPDCA.setEnabled(false);
                    cbPDCA.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            cbPDCAItemStateChanged(e);
                        }
                    });

                    //---- txtTitel ----
                    txtTitel.setFont(new Font("Lucida Grande", Font.BOLD, 16));
                    txtTitel.addCaretListener(new CaretListener() {
                        @Override
                        public void caretUpdate(CaretEvent e) {
                            txtTitelCaretUpdate(e);
                        }
                    });

                    //---- lblStart ----
                    lblStart.setText("15.05.2011");
                    lblStart.setFont(new Font("Lucida Grande", Font.BOLD, 16));

                    //---- lblEnde ----
                    lblEnde.setText("noch nicht abgeschlossen");
                    lblEnde.setFont(new Font("Lucida Grande", Font.BOLD, 16));

                    //---- lblCreator ----
                    lblCreator.setText("text");
                    lblCreator.setFont(new Font("Lucida Grande", Font.BOLD, 16));

                    //---- lblOwner ----
                    lblOwner.setText("text");
                    lblOwner.setFont(new Font("Lucida Grande", Font.BOLD, 16));

                    //---- lblPDCA ----
                    lblPDCA.setText("Plan");
                    lblPDCA.setFont(new Font("Lucida Grande", Font.BOLD, 16));

                    //---- btnPDCAPlus ----
                    btnPDCAPlus.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/addgreanbuble.png")));
                    btnPDCAPlus.setToolTipText("PDCA Zyklus einen Schritt weiter drehen");
                    btnPDCAPlus.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnPDCAPlusActionPerformed(e);
                        }
                    });

                    //---- jdcWV ----
                    jdcWV.setFont(new Font("Lucida Grande", Font.BOLD, 16));
                    jdcWV.addPropertyChangeListener(new PropertyChangeListener() {
                        @Override
                        public void propertyChange(PropertyChangeEvent e) {
                            jdcWVPropertyChange(e);
                        }
                    });

                    GroupLayout pnlDetailsLayout = new GroupLayout(pnlDetails);
                    pnlDetails.setLayout(pnlDetailsLayout);
                    pnlDetailsLayout.setHorizontalGroup(
                            pnlDetailsLayout.createParallelGroup()
                                    .addGroup(GroupLayout.Alignment.TRAILING, pnlDetailsLayout.createSequentialGroup()
                                            .addContainerGap()
                                            .addGroup(pnlDetailsLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                    .addComponent(vSpacer1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
                                                    .addComponent(splitPane3, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
                                                    .addGroup(GroupLayout.Alignment.LEADING, pnlDetailsLayout.createSequentialGroup()
                                                            .addComponent(cbPDCA)
                                                            .addGap(18, 18, 18)
                                                            .addComponent(lblPDCA)
                                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(btnPDCAPlus))
                                                    .addGroup(GroupLayout.Alignment.LEADING, pnlDetailsLayout.createSequentialGroup()
                                                            .addGroup(pnlDetailsLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                                    .addComponent(label3, GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                                                                    .addGroup(pnlDetailsLayout.createParallelGroup()
                                                                            .addGroup(GroupLayout.Alignment.TRAILING, pnlDetailsLayout.createParallelGroup()
                                                                                    .addComponent(label1, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
                                                                                    .addComponent(label2, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE)
                                                                                    .addGroup(pnlDetailsLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                                                                            .addComponent(label5, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                            .addComponent(label4, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                                                            .addComponent(label6)))
                                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                            .addGroup(pnlDetailsLayout.createParallelGroup()
                                                                    .addComponent(lblBW, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                                                                    .addComponent(lblStart, GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                                                                    .addComponent(lblEnde, GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                                                                    .addComponent(jdcWV, GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                                                                    .addComponent(txtTitel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                                                                    .addGroup(GroupLayout.Alignment.TRAILING, pnlDetailsLayout.createSequentialGroup()
                                                                            .addComponent(lblOwner, GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                                                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                            .addComponent(btnTakeOver)
                                                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                            .addComponent(btnAssign))
                                                                    .addComponent(lblCreator, GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE))))
                                            .addContainerGap())
                    );
                    pnlDetailsLayout.linkSize(SwingConstants.HORIZONTAL, new Component[]{label1, label2, label3, label4, label5, label6});
                    pnlDetailsLayout.setVerticalGroup(
                            pnlDetailsLayout.createParallelGroup()
                                    .addGroup(GroupLayout.Alignment.TRAILING, pnlDetailsLayout.createSequentialGroup()
                                            .addContainerGap()
                                            .addGroup(pnlDetailsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(label1)
                                                    .addComponent(txtTitel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(lblBW)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(pnlDetailsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(label2)
                                                    .addComponent(lblStart))
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(pnlDetailsLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(label3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(jdcWV, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(pnlDetailsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(label4)
                                                    .addComponent(lblEnde))
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(pnlDetailsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(label5)
                                                    .addComponent(lblCreator))
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(pnlDetailsLayout.createParallelGroup()
                                                    .addComponent(btnAssign, GroupLayout.Alignment.TRAILING, 0, 0, Short.MAX_VALUE)
                                                    .addComponent(btnTakeOver, GroupLayout.Alignment.TRAILING)
                                                    .addComponent(lblOwner, GroupLayout.Alignment.TRAILING)
                                                    .addComponent(label6, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE))
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(splitPane3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addGroup(pnlDetailsLayout.createParallelGroup()
                                                    .addGroup(pnlDetailsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                            .addComponent(cbPDCA, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(lblPDCA, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                                                    .addComponent(btnPDCAPlus, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(vSpacer1, GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                                            .addGap(20, 20, 20))
                    );
                    pnlDetailsLayout.linkSize(SwingConstants.VERTICAL, new Component[]{btnPDCAPlus, cbPDCA, lblPDCA});
                    pnlDetailsLayout.linkSize(SwingConstants.VERTICAL, new Component[]{btnAssign, btnTakeOver, label6, lblOwner});
                }
                splitPane2.setLeftComponent(pnlDetails);

                //======== scrollPane1 ========
                {

                    //---- listOwner ----
                    listOwner.addListSelectionListener(new ListSelectionListener() {
                        @Override
                        public void valueChanged(ListSelectionEvent e) {
                            listOwnerValueChanged(e);
                        }
                    });
                    scrollPane1.setViewportView(listOwner);
                }
                splitPane2.setRightComponent(scrollPane1);
            }
            splitPane1.setRightComponent(splitPane2);
        }

        //---- btnPrint ----
        btnPrint.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/printer1.png")));
        btnPrint.setEnabled(false);
        btnPrint.setToolTipText("Vorgang drucken");

        //======== scrollPane2 ========
        {
            scrollPane2.setViewportView(taskContainer);
        }

        //---- btnCancel ----
        btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
        btnCancel.setEnabled(false);
        btnCancel.setToolTipText("\u00c4nderungen verwerfen");
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnCancelActionPerformed(e);
            }
        });

        //---- btnApply ----
        btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
        btnApply.setEnabled(false);
        btnApply.setToolTipText("\u00c4nderungen sichern");
        btnApply.setFont(new Font("Lucida Grande", Font.BOLD, 14));
        btnApply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnApplyActionPerformed(e);
                btnApplyActionPerformed(e);
            }
        });

        //---- btnEndReactivate ----
        btnEndReactivate.setFont(new Font("Lucida Grande", Font.BOLD, 14));
        btnEndReactivate.setToolTipText("Vorgang abschlie\u00dfen");
        btnEndReactivate.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/shutdown.png")));
        btnEndReactivate.setEnabled(false);
        btnEndReactivate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnEndReactivateActionPerformed(e);
            }
        });

        //---- btnDelete ----
        btnDelete.setFont(new Font("Lucida Grande", Font.BOLD, 14));
        btnDelete.setToolTipText("Vorgang l\u00f6schen");
        btnDelete.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/deleteall.png")));
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnDeleteActionPerformed(e);
            }
        });

        //---- btnCancel1 ----
        btnCancel1.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
        btnCancel1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnCancel1ActionPerformed(e);
            }
        });

        //---- btnSystemInfo ----
        btnSystemInfo.setToolTipText("System Berichte anzeigen / verbergen");
        btnSystemInfo.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/info.png")));
        btnSystemInfo.setEnabled(false);
        btnSystemInfo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                btnSystemInfoItemStateChanged(e);
            }
        });

        //---- btnDelElement ----
        btnDelElement.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/edit_remove.png")));
        btnDelElement.setEnabled(false);
        btnDelElement.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                btnDelElementItemStateChanged(e);
            }
        });

        //---- lblMessage ----
        lblMessage.setFont(new Font("Lucida Grande", Font.BOLD, 14));
        lblMessage.setHorizontalAlignment(SwingConstants.CENTER);

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                .addGap(20, 20, 20)
                                                .addComponent(btnAddVorgang))
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 296, GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                .addComponent(btnAddBericht)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnDetails)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnDelElement)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnSystemInfo)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnPrint)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnEndReactivate)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnDelete)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnCancel1)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(lblMessage, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnApply)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnCancel))
                                        .addComponent(splitPane1, GroupLayout.DEFAULT_SIZE, 852, Short.MAX_VALUE))
                                .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE)
                                        .addComponent(splitPane1, GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE))
                                .addGap(11, 11, 11)
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(lblMessage, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnDelElement, 0, 0, Short.MAX_VALUE)
                                        .addComponent(btnAddVorgang, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnCancel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnApply, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnAddBericht, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnDetails, 0, 0, Short.MAX_VALUE)
                                        .addComponent(btnPrint, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnEndReactivate, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnDelete, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnCancel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnSystemInfo, GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE))
                                .addGap(13, 13, 13))
        );
        contentPaneLayout.linkSize(SwingConstants.VERTICAL, new Component[]{btnAddBericht, btnAddVorgang, btnApply, btnCancel, btnCancel1, btnDelElement, btnDelete, btnDetails, btnEndReactivate, btnPrint, btnSystemInfo, lblMessage});
        pack();
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void jspElementsComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspElementsComponentResized
        JScrollPane jsp = (JScrollPane) evt.getComponent();
        Dimension dim = jsp.getSize();
        // Größe der Text Spalten im DFN ändern.
        // Summe der fixen Spalten + ein bisschen)
        int textWidth = dim.width - 200;
        TableColumnModel tcm = tblElements.getColumnModel();
        if (tcm.getColumnCount() == 2) {
            tcm.getColumn(TMElement.COL_PIT).setPreferredWidth(180);
            tcm.getColumn(TMElement.COL_CONTENT).setPreferredWidth(textWidth);
        } else if (tcm.getColumnCount() == 3) { // mit Operations Spalte
            textWidth -= tcm.getColumn(TMElement.COL_OPERATIONS).getWidth();
            tcm.getColumn(TMElement.COL_PIT).setPreferredWidth(180);
            tcm.getColumn(TMElement.COL_CONTENT).setPreferredWidth(textWidth);
            tcm.getColumn(TMElement.COL_OPERATIONS).setPreferredWidth(tcm.getColumn(TMElement.COL_OPERATIONS).getWidth());
        }
    }//GEN-LAST:event_jspElementsComponentResized

    private void pnlMyVorgaengeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlMyVorgaengeMousePressed
    }//GEN-LAST:event_pnlMyVorgaengeMousePressed

    protected void setLowerMiddleButtons(boolean enabled) {
        btnAddVorgang.setEnabled(enabled);
        btnAddBericht.setEnabled(enabled);
        btnDelElement.setEnabled(enabled);
        btnSystemInfo.setEnabled(enabled);
        btnDetails.setEnabled(enabled);
        btnPrint.setEnabled(enabled);
        btnEndReactivate.setEnabled(enabled);
        btnDelete.setEnabled(enabled);
    }

    private void btnAddBerichtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddBerichtActionPerformed

        if (savePressedOnce) { // Wurde bereits einmal gedrückt. Also ist das hier die Bestätigung.
            alternatingFlash.stop();
            btnAddBericht.setText(null);
            alternatingFlash = null;
            btnCancel1.setVisible(false);
            VBericht vbericht = new VBericht(pnlEditor.getHTML(), VBerichtTools.VBERICHT_ART_USER, aktuellerVorgang);
            EntityTools.store(vbericht);
            split4Percent = 100;
            new SplitAnimator(splitPane4, split4Percent).execute();
            //btnDetails.setSelected(false);
            loadTable(aktuellerVorgang);
        } else {

            pnlEditor.setHTML(null);
            split4Percent = 50;
            new SplitAnimator(splitPane4, split4Percent).execute();
            btnAddBericht.setText("Bericht speichern");

            btnCancel1.setVisible(true);
            alternatingFlash = new ComponentAlternatingFlash(btnAddBericht, btnCancel1, new ImageIcon(getClass().getResource("/artwork/22x22/help3.png")));
            alternatingFlash.execute();
        }

        setLowerMiddleButtons(savePressedOnce);
        btnAddBericht.setEnabled(true);

        savePressedOnce = !savePressedOnce;

    }//GEN-LAST:event_btnAddBerichtActionPerformed


    protected void initAuthorizationMap() {
        authorizationMap = new HashMap<JComponent, ArrayList<Short>>();

        /**
         * btnNewKat ist der Knopf der neue Kategorien hinzufügt.
         * Man muss mindestens Manager sein um den drücken zu können.
         */
        authorizationMap.put(btnNewKat, new ArrayList());
        authorizationMap.get(btnNewKat).add(InternalClassACL.MANAGER);

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton btnAddBericht;
    private JToggleButton btnDetails;
    private JButton btnAddVorgang;
    private JSplitPane splitPane1;
    private JSplitPane splitPane4;
    private JScrollPane jspElements;
    private JTable tblElements;
    private PnlEditor pnlEditor;
    private JSplitPane splitPane2;
    private JPanel pnlDetails;
    private JButton btnTakeOver;
    private JToggleButton btnAssign;
    private JLabel label1;
    private JLabel lblBW;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;
    private JLabel label6;
    private JSplitPane splitPane3;
    private JPanel panel1;
    private JComboBox cmbKat;
    private JButton btnNewKat;
    private JPanel panel2;
    private JTextField txtKat;
    private JButton btnAcceptKat;
    private JButton btnCancelKat;
    private JCheckBox cbPDCA;
    private JTextField txtTitel;
    private JLabel lblStart;
    private JLabel lblEnde;
    private JLabel lblCreator;
    private JLabel lblOwner;
    private JPanel vSpacer1;
    private JLabel lblPDCA;
    private JButton btnPDCAPlus;
    private JDateChooser jdcWV;
    private JScrollPane scrollPane1;
    private JList listOwner;
    private JButton btnPrint;
    private JScrollPane scrollPane2;
    private JXTaskPaneContainer taskContainer;
    private JButton btnCancel;
    private JButton btnApply;
    private JButton btnEndReactivate;
    private JButton btnDelete;
    private JButton btnCancel1;
    private JToggleButton btnSystemInfo;
    private JToggleButton btnDelElement;
    private JLabel lblMessage;
    // End of variables declaration//GEN-END:variables
}
