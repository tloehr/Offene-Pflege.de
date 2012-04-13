/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PnlVorgang.java
 *
 * Created on 03.06.2011, 16:38:35
 */
package op.vorgang;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.swing.JideButton;
import com.toedter.calendar.JDateChooser;
import entity.Bewohner;
import entity.BewohnerTools;
import entity.EntityTools;
import entity.Users;
import entity.vorgang.*;
import op.OPDE;
import op.events.TaskPaneContentChangedEvent;
import op.events.TaskPaneContentChangedListener;
import op.share.tools.PnlEditor;
import op.tools.*;
import org.jdesktop.swingx.JXTaskPane;
import org.pushingpixels.trident.Timeline;
import tablemodels.TMElement;
import tablerenderer.RNDHTML;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.text.DateFormat;
import java.util.*;
import java.util.List;

/**
 * @author tloehr
 */
public class PnlVorgang extends NursingRecordsPanel {

    public static final String internalClassID = "opde.tickets";
    private static int speedSlow = 700;
    private static int speedFast = 500;

    private final int LAUFENDE_OPERATION_NICHTS = 0;
    private final int LAUFENDE_OPERATION_BERICHT_EINGABE = 1;
    private final int LAUFENDE_OPERATION_BERICHT_LOESCHEN = 2;
    private final int LAUFENDE_OPERATION_ELEMENT_ENTFERNEN = 3;
    private final int LAUFENDE_OPERATION_VORGANG_BEARBEITEN = 4;

    private int laufendeOperation;

    protected Vorgaenge aktuellerVorgang;
    protected Bewohner aktuellerBewohner;
    protected JPopupMenu menu;
    protected JFrame myFrame;
    protected double splitTDPercent, splitDOPercent, splitTEPercent, splitBCPercent;
    protected JXTaskPane pnlMyVorgaenge, pnlMeineAltenVorgaenge, pnlAlleVorgaenge, pnlVorgaengeRunningOut, pnlVorgaengeByBW, pnlVorgaengeByMA;
    protected boolean pdcaChanged = false, ignoreEvents = false;
    //protected IconFlash iconflasher;
    protected JComboBox cmbBW, cmbMA;
    private ArrayList<CollapsiblePane> panelSearch;
    //    private int positionToAddPanels;
//    protected HashMap<JComponent, ArrayList<Short>> authorizationMap;
    private TaskPaneContentChangedListener taskPaneContentChangedListener;
    private Timeline textmessageTL;

    public PnlVorgang(Vorgaenge vorgang, Bewohner bewohner, JFrame parent, TaskPaneContentChangedListener taskPaneContentChangedListener) {
        ignoreEvents = true;
        initComponents();
        this.taskPaneContentChangedListener = taskPaneContentChangedListener;

        panelSearch = new ArrayList<CollapsiblePane>();

//        if (panelSearch == null) {
//            this.panelSearch = taskContainer;
//        } else {
//            this.panelSearch = panelSearch;
////            this.panelSearch.add(new JXTitledSeparator("Vorgänge", SwingConstants.LEFT, new ImageIcon(getClass().getResource("/artwork/22x22/bw/viewmag1.png"))));
////            FormLayout fl = (FormLayout) getLayout();
////            fl.setColumnSpec(3, new ColumnSpec(ColumnSpec.FILL, Sizes.dluX(0), ColumnSpec.NO_GROW));
//        }
//        positionToAddPanels = this.panelSearch.getComponentCount();

        laufendeOperation = LAUFENDE_OPERATION_NICHTS;

        listOwner.setModel(SYSTools.newListModel("Users.findByStatusSorted", new Object[]{"status", 1}));
        cmbKat.setModel(SYSTools.newComboboxModel("VKat.findAllSorted"));

        splitTDPercent = SYSTools.showSide(splitTableDetails, SYSTools.LEFT_UPPER_SIDE);
        splitDOPercent = SYSTools.showSide(splitDetailsOwner, SYSTools.LEFT_UPPER_SIDE);
        splitTEPercent = SYSTools.showSide(splitTableEditor, SYSTools.LEFT_UPPER_SIDE);
        splitBCPercent = SYSTools.showSide(splitButtonsCenter, SYSTools.LEFT_UPPER_SIDE);

        myFrame = parent;
        this.aktuellerVorgang = vorgang;
        this.aktuellerBewohner = bewohner;

//
//        cmbBW = new JComboBox(SYSTools.newComboboxModel("Bewohner.findAllActiveSorted"));
//        cmbBW.setSelectedIndex(-1);
//        cmbBW.addItemListener(new ItemListener() {
//            @Override
//            public void itemStateChanged(ItemEvent e) {
//               // getVorgaengeFuerBW();
//            }
//        });
        cmbMA = new JComboBox(SYSTools.newComboboxModel("Users.findByStatusSorted", new Object[]{"status", 1}));
        cmbMA.setSelectedIndex(-1);
        cmbMA.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                getVorgaengeFuerMA();
            }
        });

//        if (aktuellerBewohner != null) {
//            panelSearch.add(addVorgaengeFuerBW(aktuellerBewohner));
//            taskPaneContentChangedListener.contentChanged(new TaskPaneContentChangedEvent(this, panelSearch, TaskPaneContentChangedEvent.BOTTOM, "Vorgänge"));
//        } else {
//            addMeineVorgaenge();
//            addMeineAbgelaufenenVorgaenge();
//            //addAlleVorgaenge();
////            addVorgaengeFuerBW();
//            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.MANAGER)) {
//                addVorgaengeFuerMA();
//                addAblaufendeVorgaenge();
//            }
//            taskPaneContentChangedListener.contentChanged(new TaskPaneContentChangedEvent(this, panelSearch, TaskPaneContentChangedEvent.TOP, "Vorgänge"));
//
//        }


        //

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
        ignoreEvents = false;
    }

//    private void removeSearchPanels() {
//        if (panelSearch.getComponentCount() > positionToAddPanels) {
//            int count = panelSearch.getComponentCount();
//            for (int i = count - 1; i >= positionToAddPanels; i--) {
//                panelSearch.remove(positionToAddPanels);
//            }
//        }
//    }

//    protected void addAblaufendeVorgaenge() {
//        pnlVorgaengeRunningOut = new JXTaskPane("Vorgänge, die bald ablaufen");
//        pnlVorgaengeRunningOut.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/redled.png")));
//        pnlVorgaengeRunningOut.setCollapsed(true);
//
//        pnlVorgaengeRunningOut.addPropertyChangeListener("collapsed", new PropertyChangeListener() {
//            @Override
//            public void propertyChange(PropertyChangeEvent evt) {
//                if (!(Boolean) evt.getNewValue()) {
//                    loadVorgaengeRunningOut();
//                } else {
//                    pnlVorgaengeRunningOut.removeAll();
//                }
//            }
//        });
//        panelSearch.add(pnlVorgaengeRunningOut);
//
//    }


//    protected void addMeineAbgelaufenenVorgaenge() {
//        pnlMeineAltenVorgaenge = new JXTaskPane("Meine alten Vorgänge");
//        //pnlVorgaengeRunningOut.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/redled.png")));
//        pnlMeineAltenVorgaenge.setCollapsed(true);
//
//        pnlMeineAltenVorgaenge.addPropertyChangeListener("collapsed", new PropertyChangeListener() {
//            @Override
//            public void propertyChange(PropertyChangeEvent evt) {
//                if (!(Boolean) evt.getNewValue()) {
//                    loadMeineInaktivenVorgaenge();
//                } else {
//                    pnlMeineAltenVorgaenge.removeAll();
//                }
//            }
//        });
//        panelSearch.add(pnlMeineAltenVorgaenge);
//
//    }


//    protected void addAlleVorgaenge() {
//        pnlAlleVorgaenge = new JXTaskPane("Alle aktiven Vorgänge");
//        pnlAlleVorgaenge.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/groupevent.png")));
//        pnlAlleVorgaenge.setCollapsed(true);
//
//        pnlAlleVorgaenge.addPropertyChangeListener("collapsed", new PropertyChangeListener() {
//            @Override
//            public void propertyChange(PropertyChangeEvent evt) {
//                if (!(Boolean) evt.getNewValue()) {
//                    loadAllVorgaenge();
//                } else {
//                    pnlAlleVorgaenge.removeAll();
//                }
//            }
//        });
//        panelSearch.add(pnlAlleVorgaenge);
//    }

    protected CollapsiblePane addVorgaengeFuerBW(Bewohner bewohner) {

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.PAGE_AXIS));

        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("Vorgaenge.findActiveByBewohner");
        query.setParameter("bewohner", bewohner);
        List<Vorgaenge> listVorgaenge = query.getResultList();
        Iterator<Vorgaenge> it = listVorgaenge.iterator();
        em.close();

        CollapsiblePane bwpanel = new CollapsiblePane(bewohner.getNachname() + ", " + bewohner.getVorname());
        try {
            bwpanel.setCollapsed(false);
        } catch (PropertyVetoException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        if (!listVorgaenge.isEmpty()) {
            while (it.hasNext()) {
                final Vorgaenge innervorgang = it.next();
                JideButton buttonBW = GUITools.createHyperlinkButton(innervorgang.getTitel(), null, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        loadTable(innervorgang);
                        loadDetails(innervorgang);
                    }
                });
                labelPanel.add(buttonBW);
            }
        }

        bwpanel.setContentPane(labelPanel);

        return bwpanel;
    }


//    protected void addVorgaengeFuerBW() {
//        //((Container) taskContainer).add(new JLabel("Bewohner"));
//        EntityManager em = OPDE.createEM();
//        List<Bewohner> bewohner = em.createNamedQuery("Bewohner.findAllActiveSorted").getResultList();
//
//        JXTaskPane allbwpanel = new JXTaskPane("nach BewohnerInnen");
//        allbwpanel.setCollapsed(true);
//
//        for (Bewohner bw : bewohner) {
//
//            Query query = em.createNamedQuery("Vorgaenge.findActiveByBewohner");
//            query.setParameter("bewohner", bw);
//            List<Vorgaenge> listVorgaenge = query.getResultList();
//            Iterator<Vorgaenge> it = listVorgaenge.iterator();
//
//            if (!listVorgaenge.isEmpty()) {
//                JXTaskPane bwpanel = new JXTaskPane(bw.getNachname() + ", " + bw.getVorname());
//                bwpanel.setCollapsed(true);
//
//                while (it.hasNext()) {
//                    final Vorgaenge innervorgang = it.next();
//                    bwpanel.add(new AbstractAction() {
//                        {
//                            putValue(Action.NAME, innervorgang.getTitel());
//                        }
//
//                        @Override
//                        public void actionPerformed(ActionEvent e) {
//                            loadTable(innervorgang);
//                            loadDetails(innervorgang);
//                        }
//                    });
//                }
//
//                allbwpanel.add(bwpanel);
//            }
//
//        }
//
//        panelSearch.add(allbwpanel);
//
//        em.close();
//    }


//    protected Object[] getTableButtons() {
//        return new Object[]{new JButton(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/edit_remove.png"))),
//                new TableButtonBehaviour() {
//                    @Override
//                    public void actionPerformed(TableButtonActionEvent e) {
//                        ((TMElement) tblElements.getModel()).removeRow(e.getTable().getSelectedRow());
//                    }
//
//                    @Override
//                    public boolean isEnabled(JTable table, int row, int col) {
//                        VorgangElement element = ((TMElement) tblElements.getModel()).getElement(row);
//                        boolean systemBericht = (element instanceof VBericht) && ((VBericht) element).isSystem();
//                        return !systemBericht;
//                    }
//                }};
//    }

    protected void getVorgaengeFuerMA() {
        pnlVorgaengeByMA.removeAll();
        pnlVorgaengeByMA.add(cmbMA);
        pnlVorgaengeByMA.add(new JSeparator());
        Users selectedUser = (Users) cmbMA.getSelectedItem();
        if (selectedUser != null) {
            EntityManager em = OPDE.createEM();
            Query query = em.createNamedQuery("Vorgaenge.findActiveByBesitzer");
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
            em.close();
        }
    }
//
//    protected void addVorgaengeFuerMA() {
//        EntityManager em = OPDE.createEM();
//        List<Users> listeUser = em.createNamedQuery("Users.findByStatusSorted").setParameter("status", 1).getResultList();
//
//        JXTaskPane allmapanel = new JXTaskPane("nach MitarbeiterInnen");
//        allmapanel.setCollapsed(true);
//
//        for (Users user : listeUser) {
//
//            Query query = em.createNamedQuery("Vorgaenge.findActiveByBesitzer");
//            query.setParameter("besitzer", user);
//            List<Vorgaenge> listVorgaenge = query.getResultList();
//            Iterator<Vorgaenge> it = listVorgaenge.iterator();
//
//            if (!listVorgaenge.isEmpty()) {
//                JXTaskPane mapanel = new JXTaskPane(user.getNachname() + ", " + user.getVorname());
//                mapanel.setCollapsed(true);
//
//                while (it.hasNext()) {
//                    final Vorgaenge innervorgang = it.next();
//                    OPDE.debug(innervorgang);
//                    mapanel.add(new AbstractAction() {
//                        {
//                            String titel = innervorgang.getTitel();
//                            if (innervorgang.getBewohner() != null) {
//                                titel += " [" + innervorgang.getBewohner().getBWKennung() + "]";
//                            }
//                            putValue(Action.NAME, titel);
//                        }
//
//                        @Override
//                        public void actionPerformed(ActionEvent e) {
//                            loadTable(innervorgang);
//                            loadDetails(innervorgang);
//                        }
//                    });
//                }
//                allmapanel.add(mapanel);
//            }
//
//        }
//
//        panelSearch.add(allmapanel);
//        em.close();
//    }
//
//    protected void addMeineVorgaenge() {
//        pnlMyVorgaenge = new JXTaskPane("Meine alten Vorgänge");
//        pnlMyVorgaenge.setSpecial(true);
//        pnlMyVorgaenge.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/identity.png")));
//        //pnlVorgaengeRunningOut.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/redled.png")));
//        pnlMyVorgaenge.setCollapsed(false);
//        loadMeineVorgaenge();
//
//        pnlMyVorgaenge.addPropertyChangeListener("collapsed", new PropertyChangeListener() {
//            @Override
//            public void propertyChange(PropertyChangeEvent evt) {
//                if (!(Boolean) evt.getNewValue()) {
//                    loadMeineVorgaenge();
//                } else {
//                    pnlMyVorgaenge.removeAll();
//                }
//            }
//        });
//        panelSearch.add(pnlMyVorgaenge);
//    }
//
//
//    protected void loadAllVorgaenge() {
//
//        if (pnlAlleVorgaenge.isEnabled()) {
//            EntityManager em = OPDE.createEM();
//            Query query = em.createNamedQuery("Vorgaenge.findAllActiveSorted");
//            ArrayList<Vorgaenge> alleAktiven = new ArrayList(query.getResultList());
//
//            Iterator<Vorgaenge> it = alleAktiven.iterator();
//
//            while (it.hasNext()) {
//                final Vorgaenge innervorgang = it.next();
//                pnlAlleVorgaenge.add(new AbstractAction() {
//                    {
//                        putValue(Action.NAME, innervorgang.getTitel());
//                        putValue(Action.SHORT_DESCRIPTION, (innervorgang.getBewohner() == null ? "allgemeiner Vorgang" : innervorgang.getBewohner().getNachname()));
//                    }
//
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        loadTable(innervorgang);
//                        if (btnDetails.isSelected()) {
//                            loadDetails(innervorgang);
//                        }
//                    }
//                });
//
//            }
//            em.close();
//        }
//    }
//
//
//    protected void loadVorgaengeRunningOut() {
//
//        if (pnlVorgaengeRunningOut.isEnabled()) {
//            EntityManager em = OPDE.createEM();
//            Query query = em.createNamedQuery("Vorgaenge.findActiveRunningOut");
//            query.setParameter("wv", SYSCalendar.addDate(new Date(), 4)); // 4 Tage von heute aus gerechnet.
//            ArrayList<Vorgaenge> vorgaenge = new ArrayList(query.getResultList());
//
//            Iterator<Vorgaenge> it = vorgaenge.iterator();
//
//            while (it.hasNext()) {
//                final Vorgaenge innervorgang = it.next();
//                pnlVorgaengeRunningOut.add(new AbstractAction() {
//                    {
//                        putValue(Action.NAME, innervorgang.getTitel());
//                        putValue(Action.SHORT_DESCRIPTION, (innervorgang.getBewohner() == null ? "allgemeiner Vorgang" : innervorgang.getBewohner().getNachname()));
//                    }
//
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        loadTable(innervorgang);
//                        if (btnDetails.isSelected()) {
//                            loadDetails(innervorgang);
//                        }
//                    }
//                });
//
//            }
//            em.close();
//        }
//    }

    protected void loadMeineVorgaenge() {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("Vorgaenge.findActiveByBesitzer");
        query.setParameter("besitzer", OPDE.getLogin().getUser());
        ArrayList<Vorgaenge> byBesitzer = new ArrayList(query.getResultList());

        Iterator<Vorgaenge> it = byBesitzer.iterator();

        while (it.hasNext()) {

            final Vorgaenge innervorgang = it.next();

            pnlMyVorgaenge.add(new AbstractAction() {

                {
                    String titel = innervorgang.getTitel();
                    if (innervorgang.getBewohner() != null) {
                        titel += " [" + innervorgang.getBewohner().getBWKennung() + "]";
                    }
                    putValue(Action.NAME, titel);
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

        pnlMyVorgaenge.setTitle("Meine Vorgänge (" + byBesitzer.size() + ")");
        em.close();
    }

    protected void loadMeineInaktivenVorgaenge() {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("Vorgaenge.findInactiveByBesitzer");
        query.setParameter("besitzer", OPDE.getLogin().getUser());

        ArrayList<Vorgaenge> vorgaenge = new ArrayList(query.getResultList());

        Iterator<Vorgaenge> it = vorgaenge.iterator();

        while (it.hasNext()) {
            final Vorgaenge innervorgang = it.next();
            pnlMeineAltenVorgaenge.add(new AbstractAction() {
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

        pnlMeineAltenVorgaenge.setTitle("Meine alten Vorgänge (" + vorgaenge.size() + ")");
        em.close();
    }


    protected void loadDetails(Vorgaenge vorgang) {
        lblVorgang.setText(vorgang.getTitel() + " [" + (vorgang.getBewohner() == null ? "allgemein" : vorgang.getBewohner().getBWKennung()) + "]");

        // Wenn nötig, laufende Operation abbrechen und obere Knopfreihe anzeigen.
        if (laufendeOperation != LAUFENDE_OPERATION_NICHTS) {
            laufendeOperation = LAUFENDE_OPERATION_NICHTS;
            lblMessage.setText(null);
            SYSTools.showSide(splitButtonsCenter, SYSTools.LEFT_UPPER_SIDE, speedSlow);
        }

        if (btnDetails.isSelected()) {
            ignoreEvents = true;
            txtTitel.setText(vorgang.getTitel());
            lblBW.setText(vorgang.getBewohner() == null ? "Allgemeiner Vorgang" : BewohnerTools.getBWLabelText(vorgang.getBewohner()));
            lblStart.setText(DateFormat.getDateInstance().format(vorgang.getVon()));
            jdcWV.setDate(vorgang.getWv());
            lblEnde.setText(vorgang.getBis().equals(SYSConst.DATE_BIS_AUF_WEITERES) ? "noch nicht abgeschlossen" : DateFormat.getDateInstance().format(vorgang.getBis()));
            lblCreator.setText(vorgang.getErsteller().getNameUndVorname());
            lblOwner.setText(vorgang.getBesitzer().getNameUndVorname());
            cmbKat.setSelectedItem(vorgang.getKategorie());
            lblPDCA.setText(VorgaengeTools.PDCA[vorgang.getPdca()]);
            listOwner.setSelectedValue(vorgang.getBesitzer(), true);

            // ACLs
            txtTitel.setEditable(OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.MANAGER));
            ignoreEvents = false;
        }

    }

    private void btnAssignItemStateChanged(ItemEvent e) {
        double percent = btnAssign.isSelected() ? 0.65d : 1.0d;
        SYSTools.showSide(splitDetailsOwner, percent, speedSlow, null);
    }

    private void btnDetailsItemStateChanged(ItemEvent e) {
        splitTDPercent = btnDetails.isSelected() ? 0.4d : 1.0d;
        SYSTools.showSide(splitTableDetails, splitTDPercent, speedSlow, null);
        loadDetails(aktuellerVorgang);
        btnEndReactivate.setEnabled(!btnDetails.isSelected() && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.CANCEL));
    }

    private void btnApplyActionPerformed(ActionEvent e) {

        switch (laufendeOperation) {
            case LAUFENDE_OPERATION_BERICHT_EINGABE: {
                VBericht vbericht = new VBericht(pnlEditor.getHTML(), VBerichtTools.VBERICHT_ART_USER, aktuellerVorgang);
                EntityTools.persist(vbericht);
                //((TMElement) tblElements.getModel()).addVBericht(vbericht);
                loadTable(aktuellerVorgang);
                splitTEPercent = SYSTools.showSide(splitTableEditor, SYSTools.LEFT_UPPER_SIDE, speedSlow);
                break;
            }
            case LAUFENDE_OPERATION_VORGANG_BEARBEITEN: {
                EntityManager em = OPDE.createEM();
                try {
                    em.getTransaction().begin();
                    if (pdcaChanged) {
                        VBericht vbericht = new VBericht("PDCA Stufe erhöht auf: " + VorgaengeTools.PDCA[aktuellerVorgang.getPdca()], VBerichtTools.VBERICHT_ART_PDCA, aktuellerVorgang);
                        vbericht.setPdca(aktuellerVorgang.getPdca());
                        em.persist(vbericht);
                    }
                    aktuellerVorgang = em.merge(aktuellerVorgang);
                    em.getTransaction().commit();
                } catch (Exception exc) {
                    em.getTransaction().rollback();
                    OPDE.fatal(exc);
                } finally {
                    em.close();
                }

                pdcaChanged = false;
                btnPDCAPlus.setEnabled(true);

                break;
            }
            default: {

            }
        }

        lblMessage.setText(null);
        textmessageTL.cancel();
        SYSTools.showSide(splitButtonsCenter, SYSTools.LEFT_UPPER_SIDE, speedFast);
        laufendeOperation = LAUFENDE_OPERATION_NICHTS;

    }

    private void btnCancelActionPerformed(ActionEvent e) {
//        if (savePressedOnce) {
//            btnApply.setText(null);
//            alternatingFlash.stop();
//            alternatingFlash = null;
//        }
//
//        loadDetails(aktuellerVorgang);
//        setDetailsChanged(false);
//        savePressedOnce = false;
        switch (laufendeOperation) {
            case LAUFENDE_OPERATION_BERICHT_EINGABE: {
                splitTEPercent = SYSTools.showSide(splitTableEditor, SYSTools.LEFT_UPPER_SIDE, speedSlow);
                break;
            }
            case LAUFENDE_OPERATION_VORGANG_BEARBEITEN: {
                btnPDCAPlus.setEnabled(true);
                EntityManager em = OPDE.createEM();
                em.refresh(aktuellerVorgang);
                em.close();
                loadDetails(aktuellerVorgang);
                break;
            }
            default: {

            }
        }
        lblMessage.setText(null);
        textmessageTL.cancel();
        SYSTools.showSide(splitButtonsCenter, SYSTools.LEFT_UPPER_SIDE, speedFast);
        laufendeOperation = LAUFENDE_OPERATION_NICHTS;
    }

    private void listOwnerValueChanged(ListSelectionEvent e) {
        if (ignoreEvents) return;
        setCenterButtons2Edit("Änderungen speichern ?");
        laufendeOperation = LAUFENDE_OPERATION_VORGANG_BEARBEITEN;
        aktuellerVorgang.setBesitzer((Users) listOwner.getSelectedValue());
        lblEnde.setText(aktuellerVorgang.getBesitzer().getNameUndVorname());
        btnAssign.setSelected(false);
//        split2Percent = 100;
//        new SplitAnimator(splitPane2, split2Percent).execute();

    }


    private void cmbKatItemStateChanged(ItemEvent e) {
        if (ignoreEvents) return;
        setCenterButtons2Edit("Änderungen speichern ?");
        laufendeOperation = LAUFENDE_OPERATION_VORGANG_BEARBEITEN;
        aktuellerVorgang.setKategorie((VKat) cmbKat.getSelectedItem());

    }

    private void jdcWVPropertyChange(PropertyChangeEvent e) {
        if (ignoreEvents) return;
        if (e.getPropertyName().equals("date")) {
            setCenterButtons2Edit("Änderungen speichern ?");
            laufendeOperation = LAUFENDE_OPERATION_VORGANG_BEARBEITEN;
            aktuellerVorgang.setWv(jdcWV.getDate());
        }
    }

    private void txtTitelCaretUpdate(CaretEvent e) {
        if (ignoreEvents) return;
        setCenterButtons2Edit("Änderungen speichern ?");
        laufendeOperation = LAUFENDE_OPERATION_VORGANG_BEARBEITEN;
        aktuellerVorgang.setTitel(txtTitel.getText());
        lblVorgang.setText(aktuellerVorgang.getTitel());
    }

    private void btnPDCAPlusActionPerformed(ActionEvent e) {
        setCenterButtons2Edit("Änderungen speichern ?");
        laufendeOperation = LAUFENDE_OPERATION_VORGANG_BEARBEITEN;
        aktuellerVorgang.setPdca(VorgaengeTools.incPDCA(aktuellerVorgang.getPdca()));
        lblPDCA.setText(VorgaengeTools.PDCA[aktuellerVorgang.getPdca()]);
        btnPDCAPlus.setEnabled(false);
        pdcaChanged = true;
    }

//    /**
//     * Wenn möglich enabled diese Methode die entsprechende Komponente.
//     * Hängt ab von der Gruppenmitgliedschaft des Users.
//     *
//     * @param comp
//     */
//    protected void enable(JComponent comp) {
//        boolean answer = false;
//        if (authorizationMap.containsKey(comp)) {
//            ArrayList<Short> list = authorizationMap.get(comp);
//            for (Iterator<Short> itAcl = list.iterator(); !answer && itAcl.hasNext(); ) {
//                short acl = itAcl.next();
//                // ist user Mitglied in einer der zugelassenen ACL Gruppen ?
//                answer = OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, acl);
//            }
//        } else {
//            // Im Zweifel zulassen.
//            answer = true;
//        }
//
//        comp.setEnabled(answer);
//    }

    private void btnEndReactivateActionPerformed(ActionEvent e) {
        if (aktuellerVorgang.isAbgeschlossen()) {
            VorgaengeTools.reopenVorgang(aktuellerVorgang);
            //new TextFlash(lblMessage, "Vorgang wieder geöffnet", true, false, 600).execute();
            btnEndReactivate.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/shutdown.png")));
            btnEndReactivate.setToolTipText("Vorgang abschließen");

        } else {
            VorgaengeTools.endVorgang(aktuellerVorgang);
            //new TextFlash(lblMessage, "Vorgang abgeschlossen", true, false, 600).execute();
            btnEndReactivate.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/reload.png")));
            btnEndReactivate.setToolTipText("Vorgang wieder aktivieren");

        }

        if (!pnlMyVorgaenge.isCollapsed()) {
            pnlMyVorgaenge.removeAll();
            loadMeineVorgaenge();
        }

        if (!pnlMeineAltenVorgaenge.isCollapsed()) {
            pnlMeineAltenVorgaenge.removeAll();
            loadMeineInaktivenVorgaenge();
        }


    }


    private void btnTakeOverActionPerformed(ActionEvent e) {
        aktuellerVorgang.setBesitzer(OPDE.getLogin().getUser());
        lblEnde.setText(aktuellerVorgang.getBesitzer().getNameUndVorname());

    }

//    private void btnDeleteActionPerformed(ActionEvent e) {
//        if (savePressedOnce) { // Wurde bereits einmal gedrückt. Also ist das hier die Bestätigung.
//            alternatingFlash.stop();
//            btnDelete.setText(null);
//            alternatingFlash = null;
//            btnCancel1.setVisible(false);
//            VorgaengeTools.deleteVorgang(aktuellerVorgang);
//            btnDetails.setSelected(false);
//            loadTable(null);
//        } else {
//            btnCancel1.setVisible(true);
//            btnDelete.setText("WIRKLICH ?");
//            alternatingFlash = new ComponentAlternatingFlash(btnDelete, btnCancel1, new ImageIcon(getClass().getResource("/artwork/22x22/help3.png")));
//            alternatingFlash.execute();
//        }
//
//        setLowerMiddleButtons(savePressedOnce);
//        btnDelElement.setEnabled(!savePressedOnce);
//
//        savePressedOnce = !savePressedOnce;
//    }

    private void btnSystemInfoItemStateChanged(ItemEvent e) {
        loadTable(aktuellerVorgang);
    }

    private void btnDelElementItemStateChanged(ItemEvent e) {
        loadTable(aktuellerVorgang);
    }

    private void btnAddVorgangActionPerformed(ActionEvent e) {
        //new TextFlash(lblMessage, "Vorgang abgeschlossen", true, false, 1000).execute();
    }


    private void cmbKatFocusGained(FocusEvent e) {
        // TODO add your code here
    }

    private void tblElementsMousePressed(MouseEvent e) {
        Point p = e.getPoint();
        final int col = tblElements.columnAtPoint(p);
        final int row = tblElements.rowAtPoint(p);
        ListSelectionModel lsm = tblElements.getSelectionModel();
        lsm.setSelectionInterval(row, row);


        // wenn es ein Medikament ist und der Status offen, dann nur änderbar, wenn es einen angebrochenen Bestand gibt.
        // Etwas umständlich, aus Optimierungsgründen
        // Nun noch Menüeinträge
        if (e.isPopupTrigger()) {
            SYSTools.unregisterListeners(menu);
            menu = new JPopupMenu();


            JMenuItem itemPopupDelete = new JMenuItem("Eintrag löschen");
            itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {

                }
            });
            menu.add(itemPopupDelete);
            itemPopupDelete.setEnabled(OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.MANAGER));

            menu.add(new JSeparator());

            menu.show(e.getComponent(), (int) p.getX(), (int) p.getY());

        }
    }

    private void thisComponentResized(ComponentEvent e) {
//        splitTableEditorComponentResized(e);
//        splitDetailsOwnerComponentResized(e);
//        splitTableDetailsComponentResized(e);
    }

    private void splitDetailsOwnerComponentResized(ComponentEvent e) {
        SYSTools.showSide(splitDetailsOwner, splitDOPercent);
    }

    private void splitTableEditorComponentResized(ComponentEvent e) {
        SYSTools.showSide(splitTableEditor, splitTEPercent);
    }

    private void splitTableDetailsComponentResized(ComponentEvent e) {
        SYSTools.showSide(splitTableDetails, splitTDPercent);
    }

    private void splitButtonsCenterComponentResized(ComponentEvent e) {
        SYSTools.showSide(splitButtonsCenter, splitBCPercent);
    }


    protected void loadTable(Vorgaenge vorgang) {
        aktuellerVorgang = vorgang;

        if (vorgang == null) {
            tblElements.setModel(new DefaultTableModel());
        } else {

            lblVorgang.setText(vorgang.getTitel() + " [" + (vorgang.getBewohner() == null ? "allgemein" : vorgang.getBewohner().getBWKennung()) + "]");

            List<VorgangElement> elements = new ArrayList<VorgangElement>(VorgaengeTools.findElementeByVorgang(vorgang, btnSystemInfo.isSelected()));

            tblElements.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

            tblElements.setModel(new TMElement(elements));
            tblElements.getColumnModel().getColumn(TMElement.COL_PIT).setCellRenderer(new RNDHTML());
            tblElements.getColumnModel().getColumn(TMElement.COL_PDCA).setCellRenderer(new RNDHTML());
            tblElements.getColumnModel().getColumn(TMElement.COL_CONTENT).setCellRenderer(new RNDHTML());
            tblElements.getColumnModel().getColumn(TMElement.COL_PIT).setHeaderValue("Datum / MA");
            tblElements.getColumnModel().getColumn(TMElement.COL_PDCA).setHeaderValue("PDCA");
            tblElements.getColumnModel().getColumn(TMElement.COL_CONTENT).setHeaderValue("Inhalt");

//            if (btnDelElement.isSelected()) {
//                // ButtonsRenderer und ButtonsEditor sind dafür da, damit man in den Tabellen Spalten Knöpfe einfügen kann
//                // Es gibt immer einen Cancel Button und einen Menge von Funktionsknöpfe. Was diese Funktionsknöpfe
//                // machen sollen, steht in den Actionlistenern, die man mit übergibt.
//                tblElements.addColumn(new TableColumn(TMElement.COL_OPERATIONS, 0,
//                        //
//                        new ButtonsRenderer(new JButton(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/cancel.png"))),
//                                getTableButtons()),
//                        //
//                        new ButtonsEditor(tblElements, new JButton(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/cancel.png"))),
//                                getTableButtons()
//                        )
//                )
//                );
//                tblElements.getColumnModel().getColumn(TMElement.COL_OPERATIONS).setHeaderValue("--");
//                new TableColumnSizeAnimator(jspElements, tblElements.getColumnModel().getColumn(TMElement.COL_OPERATIONS), 150).execute();
//                //OPDE.debug(tblElements.getColumnCount());
//            }

            jspElements.dispatchEvent(new ComponentEvent(jspElements, ComponentEvent.COMPONENT_RESIZED));

            if (aktuellerVorgang.isAbgeschlossen()) {
                btnEndReactivate.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/reload.png")));
                btnEndReactivate.setToolTipText("Vorgang wieder aktivieren");
            } else {
                btnEndReactivate.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/shutdown.png")));
                btnEndReactivate.setToolTipText("Vorgang abschließen");
            }


        }

        btnEndReactivate.setEnabled(vorgang != null && !btnDetails.isSelected() && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.CANCEL));
        //btnDelete.setEnabled(vorgang != null && !btnDetails.isSelected() && OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.DELETE));

        btnAddBericht.setEnabled(vorgang != null && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE));
        btnSystemInfo.setEnabled(vorgang != null && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.MANAGER));
        //btnDelElement.setEnabled(vorgang != null && OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.MANAGER));
        btnDetails.setEnabled(vorgang != null && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE));
        btnPrint.setEnabled(vorgang != null && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.PRINT));

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
        lblVorgang = new JLabel();
        splitTableDetails = new JSplitPane();
        splitTableEditor = new JSplitPane();
        jspElements = new JScrollPane();
        tblElements = new JTable();
        pnlEditor = new PnlEditor();
        splitDetailsOwner = new JSplitPane();
        pnlDetails = new JPanel();
        label1 = new JLabel();
        lblBW = new JLabel();
        label2 = new JLabel();
        label3 = new JLabel();
        label4 = new JLabel();
        label5 = new JLabel();
        label6 = new JLabel();
        txtTitel = new JTextField();
        lblStart = new JLabel();
        lblEnde = new JLabel();
        lblCreator = new JLabel();
        lblOwner = new JLabel();
        jdcWV = new JDateChooser();
        btnAssign = new JToggleButton();
        label7 = new JLabel();
        cmbKat = new JComboBox();
        label8 = new JLabel();
        lblPDCA = new JLabel();
        btnPDCAPlus = new JButton();
        scrollPane1 = new JScrollPane();
        listOwner = new JList();
        splitButtonsCenter = new JSplitPane();
        panel5 = new JPanel();
        btnAddBericht = new JButton();
        btnDetails = new JToggleButton();
        btnPrint = new JButton();
        btnEndReactivate = new JButton();
        btnSystemInfo = new JToggleButton();
        pnlButtonsRight = new JPanel();
        btnApply = new JButton();
        hSpacer1 = new JPanel(null);
        lblMessage = new JLabel();
        hSpacer2 = new JPanel(null);
        btnCancel = new JButton();

        //======== this ========
        setBorder(new LineBorder(Color.black, 1, true));
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                thisComponentResized(e);
            }
        });
        setLayout(new FormLayout(
                "$rgap, 0dlu, $rgap, 316dlu:grow, 0dlu, $rgap",
                "$rgap, 0dlu, default, $lgap, fill:default:grow, $lgap, 22dlu, 0dlu, $lgap, 1dlu"));

        //---- lblVorgang ----
        lblVorgang.setFont(new Font("Lucida Grande", Font.BOLD, 18));
        lblVorgang.setForeground(Color.blue);
        lblVorgang.setHorizontalAlignment(SwingConstants.CENTER);
        lblVorgang.setText(" ");
        add(lblVorgang, CC.xy(4, 3));

        //======== splitTableDetails ========
        {
            splitTableDetails.setDividerLocation(300);
            splitTableDetails.setEnabled(false);
            splitTableDetails.setDividerSize(0);
            splitTableDetails.setDoubleBuffered(true);
            splitTableDetails.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    splitTableDetailsComponentResized(e);
                }
            });

            //======== splitTableEditor ========
            {
                splitTableEditor.setOrientation(JSplitPane.VERTICAL_SPLIT);
                splitTableEditor.setDividerLocation(300);
                splitTableEditor.setDividerSize(0);
                splitTableEditor.setEnabled(false);
                splitTableEditor.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        splitTableEditorComponentResized(e);
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
                    ));
                    tblElements.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    tblElements.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                            tblElementsMousePressed(e);
                        }
                    });
                    jspElements.setViewportView(tblElements);
                }
                splitTableEditor.setTopComponent(jspElements);
                splitTableEditor.setBottomComponent(pnlEditor);
            }
            splitTableDetails.setLeftComponent(splitTableEditor);

            //======== splitDetailsOwner ========
            {
                splitDetailsOwner.setDividerSize(0);
                splitDetailsOwner.setDividerLocation(600);
                splitDetailsOwner.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        splitDetailsOwnerComponentResized(e);
                    }
                });

                //======== pnlDetails ========
                {
                    pnlDetails.setLayout(new FormLayout(
                            "0dlu, $lcgap, 70dlu, $lcgap, default:grow, $lcgap, default, $lcgap, 0dlu",
                            "0dlu, 9*($lgap, fill:default)"));

                    //---- label1 ----
                    label1.setText("Titel");
                    pnlDetails.add(label1, CC.xywh(3, 3, 2, 1));

                    //---- lblBW ----
                    lblBW.setText("Allgemeiner Vorgang");
                    lblBW.setFont(new Font("Lucida Grande", Font.BOLD, 16));
                    lblBW.setForeground(Color.blue);
                    pnlDetails.add(lblBW, CC.xywh(5, 5, 3, 1));

                    //---- label2 ----
                    label2.setText("Erstellt am");
                    pnlDetails.add(label2, CC.xywh(3, 7, 2, 1));

                    //---- label3 ----
                    label3.setText("Wiedervorlage");
                    pnlDetails.add(label3, CC.xywh(3, 9, 2, 1));

                    //---- label4 ----
                    label4.setText("Abgeschlossen am");
                    pnlDetails.add(label4, CC.xywh(3, 11, 2, 1));

                    //---- label5 ----
                    label5.setText("Erstellt von");
                    pnlDetails.add(label5, CC.xywh(3, 13, 2, 1));

                    //---- label6 ----
                    label6.setText("Wird bearbeitet von");
                    pnlDetails.add(label6, CC.xywh(3, 15, 2, 1));

                    //---- txtTitel ----
                    txtTitel.setFont(new Font("Lucida Grande", Font.BOLD, 16));
                    txtTitel.addCaretListener(new CaretListener() {
                        @Override
                        public void caretUpdate(CaretEvent e) {
                            txtTitelCaretUpdate(e);
                        }
                    });
                    pnlDetails.add(txtTitel, CC.xywh(5, 3, 3, 1));

                    //---- lblStart ----
                    lblStart.setText("15.05.2011");
                    lblStart.setFont(new Font("Lucida Grande", Font.BOLD, 16));
                    pnlDetails.add(lblStart, CC.xywh(5, 7, 3, 1));

                    //---- lblEnde ----
                    lblEnde.setText("noch nicht abgeschlossen");
                    lblEnde.setFont(new Font("Lucida Grande", Font.BOLD, 16));
                    pnlDetails.add(lblEnde, CC.xywh(5, 11, 3, 1));

                    //---- lblCreator ----
                    lblCreator.setText("text");
                    lblCreator.setFont(new Font("Lucida Grande", Font.BOLD, 16));
                    pnlDetails.add(lblCreator, CC.xywh(5, 13, 3, 1));

                    //---- lblOwner ----
                    lblOwner.setText("text");
                    lblOwner.setFont(new Font("Lucida Grande", Font.BOLD, 16));
                    pnlDetails.add(lblOwner, CC.xy(5, 15));

                    //---- jdcWV ----
                    jdcWV.setFont(new Font("Lucida Grande", Font.BOLD, 16));
                    jdcWV.addPropertyChangeListener(new PropertyChangeListener() {
                        @Override
                        public void propertyChange(PropertyChangeEvent e) {
                            jdcWVPropertyChange(e);
                        }
                    });
                    pnlDetails.add(jdcWV, CC.xywh(5, 9, 3, 1));

                    //---- btnAssign ----
                    btnAssign.setFont(new Font("Lucida Grande", Font.BOLD, 14));
                    btnAssign.setToolTipText("Vorgang an anderen Benutzer \u00fcberweisen");
                    btnAssign.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/1rightarrow.png")));
                    btnAssign.setBackground(new Color(204, 238, 238));
                    btnAssign.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            btnAssignItemStateChanged(e);
                        }
                    });
                    pnlDetails.add(btnAssign, CC.xy(7, 15));

                    //---- label7 ----
                    label7.setText("Geh\u00f6rt zu:");
                    pnlDetails.add(label7, CC.xy(3, 17));

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
                    pnlDetails.add(cmbKat, CC.xywh(5, 17, 3, 1));

                    //---- label8 ----
                    label8.setText("PDCA Zyklus");
                    pnlDetails.add(label8, CC.xy(3, 19));

                    //---- lblPDCA ----
                    lblPDCA.setText("Plan");
                    lblPDCA.setFont(new Font("Lucida Grande", Font.BOLD, 16));
                    pnlDetails.add(lblPDCA, CC.xywh(5, 19, 2, 1));

                    //---- btnPDCAPlus ----
                    btnPDCAPlus.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
                    btnPDCAPlus.setToolTipText("PDCA Zyklus einen Schritt weiter drehen");
                    btnPDCAPlus.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnPDCAPlusActionPerformed(e);
                        }
                    });
                    pnlDetails.add(btnPDCAPlus, CC.xy(7, 19));
                }
                splitDetailsOwner.setLeftComponent(pnlDetails);

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
                splitDetailsOwner.setRightComponent(scrollPane1);
            }
            splitTableDetails.setRightComponent(splitDetailsOwner);
        }
        add(splitTableDetails, CC.xy(4, 5, CC.DEFAULT, CC.FILL));

        //======== splitButtonsCenter ========
        {
            splitButtonsCenter.setOrientation(JSplitPane.VERTICAL_SPLIT);
            splitButtonsCenter.setDividerSize(0);
            splitButtonsCenter.setDividerLocation(35);
            splitButtonsCenter.setBorder(null);
            splitButtonsCenter.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    splitButtonsCenterComponentResized(e);
                }
            });

            //======== panel5 ========
            {
                panel5.setLayout(new BoxLayout(panel5, BoxLayout.X_AXIS));

                //---- btnAddBericht ----
                btnAddBericht.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
                btnAddBericht.setToolTipText("Neuen Bericht schreiben");
                btnAddBericht.setEnabled(false);
                btnAddBericht.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnAddBerichtActionPerformed(e);
                    }
                });
                panel5.add(btnAddBericht);

                //---- btnDetails ----
                btnDetails.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/edit.png")));
                btnDetails.setToolTipText("Details anzeigen / \u00e4ndern");
                btnDetails.setEnabled(false);
                btnDetails.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        btnDetailsItemStateChanged(e);
                    }
                });
                panel5.add(btnDetails);

                //---- btnPrint ----
                btnPrint.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/printer.png")));
                btnPrint.setEnabled(false);
                btnPrint.setToolTipText("Vorgang drucken");
                panel5.add(btnPrint);

                //---- btnEndReactivate ----
                btnEndReactivate.setFont(new Font("Lucida Grande", Font.BOLD, 14));
                btnEndReactivate.setToolTipText("Vorgang abschlie\u00dfen");
                btnEndReactivate.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/exit.png")));
                btnEndReactivate.setEnabled(false);
                btnEndReactivate.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnEndReactivateActionPerformed(e);
                    }
                });
                panel5.add(btnEndReactivate);

                //---- btnSystemInfo ----
                btnSystemInfo.setToolTipText("System Berichte anzeigen / verbergen");
                btnSystemInfo.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/idea.png")));
                btnSystemInfo.setEnabled(false);
                btnSystemInfo.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        btnSystemInfoItemStateChanged(e);
                    }
                });
                panel5.add(btnSystemInfo);
            }
            splitButtonsCenter.setTopComponent(panel5);

            //======== pnlButtonsRight ========
            {
                pnlButtonsRight.setLayout(new BoxLayout(pnlButtonsRight, BoxLayout.X_AXIS));

                //---- btnApply ----
                btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/apply.png")));
                btnApply.setToolTipText("\u00c4nderungen sichern");
                btnApply.setFont(new Font("Lucida Grande", Font.BOLD, 14));
                btnApply.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnApplyActionPerformed(e);
                    }
                });
                pnlButtonsRight.add(btnApply);
                pnlButtonsRight.add(hSpacer1);

                //---- lblMessage ----
                lblMessage.setFont(new Font("Lucida Grande", Font.BOLD, 14));
                lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
                lblMessage.setText("My Message");
                pnlButtonsRight.add(lblMessage);
                pnlButtonsRight.add(hSpacer2);

                //---- btnCancel ----
                btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/editdelete.png")));
                btnCancel.setToolTipText("\u00c4nderungen verwerfen");
                btnCancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnCancelActionPerformed(e);
                    }
                });
                pnlButtonsRight.add(btnCancel);
            }
            splitButtonsCenter.setBottomComponent(pnlButtonsRight);
        }
        add(splitButtonsCenter, CC.xy(4, 7));
    }// </editor-fold>//GEN-END:initComponents

    private void jspElementsComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspElementsComponentResized
        if (tblElements.getModel().getColumnCount() < 3) return;
        JScrollPane jsp = (JScrollPane) evt.getComponent();
        Dimension dim = jsp.getSize();

        //SYSTools.packTable(tblElements, 5);

        // Größe der Text Spalten im DFN ändern.
        // Summe der fixen Spalten + ein bisschen)
        int textWidth = dim.width - 260;
        TableColumnModel tcm = tblElements.getColumnModel();
        tcm.getColumn(TMElement.COL_PIT).setPreferredWidth(180);
        tcm.getColumn(TMElement.COL_PDCA).setPreferredWidth(70);
        tcm.getColumn(TMElement.COL_CONTENT).setPreferredWidth(textWidth);

    }//GEN-LAST:event_jspElementsComponentResized

    private void pnlMyVorgaengeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlMyVorgaengeMousePressed
    }//GEN-LAST:event_pnlMyVorgaengeMousePressed

    protected void setLowerMiddleButtons(boolean enabled) {
//        btnAddVorgang.setEnabled(enabled);
        btnAddBericht.setEnabled(enabled);
        btnSystemInfo.setEnabled(enabled);
        btnDetails.setEnabled(enabled);
        btnPrint.setEnabled(enabled);
        btnEndReactivate.setEnabled(enabled);
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void change2Bewohner(Bewohner bewohner) {
        aktuellerBewohner = bewohner;
//        SYSTools.removeSearchPanels(panelSearch, positionToAddPanels);
        panelSearch.clear();
        panelSearch.add(addVorgaengeFuerBW(aktuellerBewohner)); // TaskPaneContentChangedEvent.BOTTOM,
        taskPaneContentChangedListener.contentChanged(new TaskPaneContentChangedEvent(this, panelSearch, "Vorgänge"));
    }

    private void btnAddBerichtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddBerichtActionPerformed
        laufendeOperation = LAUFENDE_OPERATION_BERICHT_EINGABE;
        pnlEditor.setHTML(null);

        if (splitTDPercent < 1.0d) {
            splitTDPercent = SYSTools.showSide(splitTableEditor, SYSTools.LEFT_UPPER_SIDE, speedFast);
        }

        splitTEPercent = SYSTools.showSide(splitTableEditor, 0.5d, speedSlow, null);
        textmessageTL = SYSTools.flashLabel(lblMessage, "Bericht speichern ?");
        SYSTools.showSide(splitButtonsCenter, SYSTools.RIGHT_LOWER_SIDE, speedFast);

        pnlEditor.requestFocus();

    }//GEN-LAST:event_btnAddBerichtActionPerformed


//    protected void initAuthorizationMap() {
//        authorizationMap = new HashMap<JComponent, ArrayList<Short>>();
//
//        /**
//         * btnNewKat ist der Knopf der neue Kategorien hinzufügt.
//         * Man muss mindestens Manager sein um den drücken zu können.
//         */
//        //authorizationMap.put(btnNewKat, new ArrayList());
//        //authorizationMap.get(btnNewKat).add(InternalClassACL.MANAGER);
//
//    }

    protected void setCenterButtons2Edit(String text) {
        if (laufendeOperation == LAUFENDE_OPERATION_NICHTS) {
            textmessageTL = SYSTools.flashLabel(lblMessage, text);
            SYSTools.showSide(splitButtonsCenter, SYSTools.RIGHT_LOWER_SIDE, speedFast);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel lblVorgang;
    private JSplitPane splitTableDetails;
    private JSplitPane splitTableEditor;
    private JScrollPane jspElements;
    private JTable tblElements;
    private PnlEditor pnlEditor;
    private JSplitPane splitDetailsOwner;
    private JPanel pnlDetails;
    private JLabel label1;
    private JLabel lblBW;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;
    private JLabel label6;
    private JTextField txtTitel;
    private JLabel lblStart;
    private JLabel lblEnde;
    private JLabel lblCreator;
    private JLabel lblOwner;
    private JDateChooser jdcWV;
    private JToggleButton btnAssign;
    private JLabel label7;
    private JComboBox cmbKat;
    private JLabel label8;
    private JLabel lblPDCA;
    private JButton btnPDCAPlus;
    private JScrollPane scrollPane1;
    private JList listOwner;
    private JSplitPane splitButtonsCenter;
    private JPanel panel5;
    private JButton btnAddBericht;
    private JToggleButton btnDetails;
    private JButton btnPrint;
    private JButton btnEndReactivate;
    private JToggleButton btnSystemInfo;
    private JPanel pnlButtonsRight;
    private JButton btnApply;
    private JPanel hSpacer1;
    private JLabel lblMessage;
    private JPanel hSpacer2;
    private JButton btnCancel;
    // End of variables declaration//GEN-END:variables
}
