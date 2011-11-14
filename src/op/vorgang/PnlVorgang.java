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

import javax.swing.border.*;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;
import entity.*;
import op.OPDE;
import op.care.CleanablePanel;
import op.share.tools.PnlEditor;
import op.tools.InternalClassACL;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.pushingpixels.trident.Timeline;
import tablemodels.TMElement;
import tablerenderer.RNDHTML;

import javax.persistence.Query;
import javax.swing.*;
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
import java.text.DateFormat;
import java.util.*;
import java.util.List;

/**
 * @author tloehr
 */
public class PnlVorgang extends CleanablePanel {

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
    protected HashMap<JComponent, ArrayList<Short>> authorizationMap;

    private Timeline textmessageTL;


    /**
     * Creates new form PnlVorgang
     */
    public PnlVorgang(JFrame parent) {
        this(null, null, parent);
    }

    /**
     * Creates new form PnlVorgang
     */
    public PnlVorgang(JFrame parent, Bewohner bewohner) {
        this(null, bewohner, parent);
    }

    public PnlVorgang(Vorgaenge vorgang, Bewohner bewohner, JFrame parent) {
        ignoreEvents = true;
        initComponents();
        initAuthorizationMap();
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


//        // TODO: Hier müssen noch Rechte rein
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

        if (aktuellerBewohner != null) {
            addVorgaengeFuerBW(aktuellerBewohner);
        } else {
            addMeineVorgaenge();
            addMeineAbgelaufenenVorgaenge();
            //addAlleVorgaenge();
            addVorgaengeFuerBW();
            if (OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.MANAGER)) {
                addVorgaengeFuerMA();
                addAblaufendeVorgaenge();
            }
        }


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


    protected void addMeineAbgelaufenenVorgaenge() {
        pnlMeineAltenVorgaenge = new JXTaskPane("Meine alten Vorgänge");
        //pnlVorgaengeRunningOut.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/redled.png")));
        pnlMeineAltenVorgaenge.setCollapsed(true);

        pnlMeineAltenVorgaenge.addPropertyChangeListener("collapsed", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (!(Boolean) evt.getNewValue()) {
                    loadMeineInaktivenVorgaenge();
                } else {
                    pnlMeineAltenVorgaenge.removeAll();
                }
            }
        });
        ((Container) taskContainer).add(pnlMeineAltenVorgaenge);

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

    protected void addVorgaengeFuerBW(Bewohner bewohner) {
        Query query = OPDE.getEM().createNamedQuery("Vorgaenge.findActiveByBewohner");
        query.setParameter("bewohner", bewohner);
        List<Vorgaenge> listVorgaenge = query.getResultList();
        Iterator<Vorgaenge> it = listVorgaenge.iterator();

        JXTaskPane bwpanel = new JXTaskPane(bewohner.getNachname() + ", " + bewohner.getVorname());
        bwpanel.setCollapsed(false);

        if (!listVorgaenge.isEmpty()) {
            while (it.hasNext()) {
                final Vorgaenge innervorgang = it.next();
                bwpanel.add(new AbstractAction() {
                    {
                        putValue(Action.NAME, innervorgang.getTitel());
                    }

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        loadTable(innervorgang);
                        loadDetails(innervorgang);
                    }
                });
            }
        }
        ((Container) taskContainer).add(bwpanel);
    }


    protected void addVorgaengeFuerBW() {
        //((Container) taskContainer).add(new JLabel("Bewohner"));

        List<Bewohner> bewohner = OPDE.getEM().createNamedQuery("Bewohner.findAllActiveSorted").getResultList();

        JXTaskPane allbwpanel = new JXTaskPane("nach BewohnerInnen");
        allbwpanel.setCollapsed(true);

        for (Bewohner bw : bewohner) {

            Query query = OPDE.getEM().createNamedQuery("Vorgaenge.findActiveByBewohner");
            query.setParameter("bewohner", bw);
            List<Vorgaenge> listVorgaenge = query.getResultList();
            Iterator<Vorgaenge> it = listVorgaenge.iterator();

            if (!listVorgaenge.isEmpty()) {
                JXTaskPane bwpanel = new JXTaskPane(bw.getNachname() + ", " + bw.getVorname());
                bwpanel.setCollapsed(true);

                while (it.hasNext()) {
                    final Vorgaenge innervorgang = it.next();
                    bwpanel.add(new AbstractAction() {
                        {
                            putValue(Action.NAME, innervorgang.getTitel());
                        }

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            loadTable(innervorgang);
                            loadDetails(innervorgang);
                        }
                    });
                }

                allbwpanel.add(bwpanel);
            }

        }

        ((Container) taskContainer).add(allbwpanel);
    }


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
        List<Users> listeUser = OPDE.getEM().createNamedQuery("Users.findByStatusSorted").setParameter("status", 1).getResultList();

        JXTaskPane allmapanel = new JXTaskPane("nach MitarbeiterInnen");
        allmapanel.setCollapsed(true);

        for (Users user : listeUser) {

            Query query = OPDE.getEM().createNamedQuery("Vorgaenge.findActiveByBesitzer");
            query.setParameter("besitzer", user);
            List<Vorgaenge> listVorgaenge = query.getResultList();
            Iterator<Vorgaenge> it = listVorgaenge.iterator();

            if (!listVorgaenge.isEmpty()) {
                JXTaskPane mapanel = new JXTaskPane(user.getNachname() + ", " + user.getVorname());
                mapanel.setCollapsed(true);

                while (it.hasNext()) {
                    final Vorgaenge innervorgang = it.next();
                    OPDE.debug(innervorgang);
                    mapanel.add(new AbstractAction() {
                        {
                            String titel = innervorgang.getTitel();
                            if (innervorgang.getBewohner() != null) {
                                titel += " [" + innervorgang.getBewohner().getBWKennung() + "]";
                            }
                            putValue(Action.NAME, titel);
                        }

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            loadTable(innervorgang);
                            loadDetails(innervorgang);
                        }
                    });
                }
                allmapanel.add(mapanel);
            }

        }

        ((Container) taskContainer).add(allmapanel);

    }

    protected void addMeineVorgaenge() {
        pnlMyVorgaenge = new JXTaskPane("Meine alten Vorgänge");
        pnlMyVorgaenge.setSpecial(true);
        pnlMyVorgaenge.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/identity.png")));
        //pnlVorgaengeRunningOut.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/redled.png")));
        pnlMyVorgaenge.setCollapsed(false);
        loadMeineVorgaenge();

        pnlMyVorgaenge.addPropertyChangeListener("collapsed", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (!(Boolean) evt.getNewValue()) {
                    loadMeineVorgaenge();
                } else {
                    pnlMyVorgaenge.removeAll();
                }
            }
        });
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

    protected void loadMeineVorgaenge() {

        Query query = OPDE.getEM().createNamedQuery("Vorgaenge.findActiveByBesitzer");
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
    }

    protected void loadMeineInaktivenVorgaenge() {

        Query query = OPDE.getEM().createNamedQuery("Vorgaenge.findInactiveByBesitzer");
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
            txtTitel.setEditable(OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.MANAGER));
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
        btnEndReactivate.setEnabled(!btnDetails.isSelected() && OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.CANCEL));
    }

    private void btnApplyActionPerformed(ActionEvent e) {

        switch (laufendeOperation) {
            case LAUFENDE_OPERATION_BERICHT_EINGABE: {
                VBericht vbericht = new VBericht(pnlEditor.getHTML(), VBerichtTools.VBERICHT_ART_USER, aktuellerVorgang);
                EntityTools.store(vbericht);
                //((TMElement) tblElements.getModel()).addVBericht(vbericht);
                loadTable(aktuellerVorgang);
                splitTEPercent = SYSTools.showSide(splitTableEditor, SYSTools.LEFT_UPPER_SIDE, speedSlow);
                break;
            }
            case LAUFENDE_OPERATION_VORGANG_BEARBEITEN: {

                try {
                    OPDE.getEM().getTransaction().begin();
                    if (pdcaChanged) {
                        VBericht vbericht = new VBericht("PDCA Stufe erhöht auf: " + VorgaengeTools.PDCA[aktuellerVorgang.getPdca()], VBerichtTools.VBERICHT_ART_PDCA, aktuellerVorgang);
                        vbericht.setPdca(aktuellerVorgang.getPdca());
                        OPDE.getEM().persist(vbericht);
                    }
                    OPDE.getEM().merge(aktuellerVorgang);
                    OPDE.getEM().getTransaction().commit();
                } catch (Exception exc) {
                    OPDE.fatal(exc.getMessage());
                    OPDE.getEM().getTransaction().rollback();
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
                OPDE.getEM().refresh(aktuellerVorgang);
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
        btnPDCAPlus.setEnabled(false);
        pdcaChanged = true;
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
            itemPopupDelete.setEnabled(OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.MANAGER));

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

        btnEndReactivate.setEnabled(vorgang != null && !btnDetails.isSelected() && OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.CANCEL));
        //btnDelete.setEnabled(vorgang != null && !btnDetails.isSelected() && OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.DELETE));

        btnAddBericht.setEnabled(vorgang != null && OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE));
        btnSystemInfo.setEnabled(vorgang != null && OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.MANAGER));
        //btnDelElement.setEnabled(vorgang != null && OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.MANAGER));
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
        scrollPane2 = new JScrollPane();
        taskContainer = new JXTaskPaneContainer();
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
        btnTakeOver = new JButton();
        btnAssign = new JToggleButton();
        label7 = new JLabel();
        cmbKat = new JComboBox();
        label8 = new JLabel();
        lblPDCA = new JLabel();
        btnPDCAPlus = new JButton();
        scrollPane1 = new JScrollPane();
        listOwner = new JList();
        pnlButtonsLeft = new JPanel();
        btnAddVorgang = new JButton();
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
            "$rgap, 0dlu, 148dlu, $rgap, 316dlu:grow, 0dlu, $rgap",
            "$rgap, 0dlu, default, $lgap, fill:default:grow, $lgap, 22dlu, 0dlu, $lgap, 1dlu"));

        //======== scrollPane2 ========
        {
            scrollPane2.setViewportView(taskContainer);
        }
        add(scrollPane2, CC.xywh(3, 3, 1, 3));

        //---- lblVorgang ----
        lblVorgang.setFont(new Font("Lucida Grande", Font.BOLD, 18));
        lblVorgang.setForeground(Color.blue);
        lblVorgang.setHorizontalAlignment(SwingConstants.CENTER);
        lblVorgang.setText(" ");
        add(lblVorgang, CC.xy(5, 3));

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
                        new Object[][] {
                            {null, null, null, null},
                            {null, null, null, null},
                            {null, null, null, null},
                            {null, null, null, null},
                        },
                        new String[] {
                            "Title 1", "Title 2", "Title 3", "Title 4"
                        }
                    ) {
                        Class<?>[] columnTypes = new Class<?>[] {
                            Object.class, Object.class, Object.class, Object.class
                        };
                        @Override
                        public Class<?> getColumnClass(int columnIndex) {
                            return columnTypes[columnIndex];
                        }
                    });
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
                        "0dlu, $lcgap, 70dlu, $lcgap, default:grow, 2*($lcgap, default), $lcgap, 0dlu",
                        "0dlu, 9*($lgap, fill:default), 4*($lgap, default)"));

                    //---- label1 ----
                    label1.setText("Titel");
                    pnlDetails.add(label1, CC.xywh(3, 3, 2, 1));

                    //---- lblBW ----
                    lblBW.setText("Allgemeiner Vorgang");
                    lblBW.setFont(new Font("Lucida Grande", Font.BOLD, 16));
                    lblBW.setForeground(Color.blue);
                    pnlDetails.add(lblBW, CC.xywh(5, 5, 5, 1));

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
                    pnlDetails.add(txtTitel, CC.xywh(5, 3, 5, 1));

                    //---- lblStart ----
                    lblStart.setText("15.05.2011");
                    lblStart.setFont(new Font("Lucida Grande", Font.BOLD, 16));
                    pnlDetails.add(lblStart, CC.xywh(5, 7, 5, 1));

                    //---- lblEnde ----
                    lblEnde.setText("noch nicht abgeschlossen");
                    lblEnde.setFont(new Font("Lucida Grande", Font.BOLD, 16));
                    pnlDetails.add(lblEnde, CC.xywh(5, 11, 5, 1));

                    //---- lblCreator ----
                    lblCreator.setText("text");
                    lblCreator.setFont(new Font("Lucida Grande", Font.BOLD, 16));
                    pnlDetails.add(lblCreator, CC.xywh(5, 13, 5, 1));

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
                    pnlDetails.add(jdcWV, CC.xywh(5, 9, 5, 1));

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
                    pnlDetails.add(btnTakeOver, CC.xy(7, 15));

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
                    pnlDetails.add(btnAssign, CC.xy(9, 15));

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
                    pnlDetails.add(cmbKat, CC.xywh(5, 17, 5, 1));

                    //---- label8 ----
                    label8.setText("PDCA Zyklus");
                    pnlDetails.add(label8, CC.xy(3, 19));

                    //---- lblPDCA ----
                    lblPDCA.setText("Plan");
                    lblPDCA.setFont(new Font("Lucida Grande", Font.BOLD, 16));
                    pnlDetails.add(lblPDCA, CC.xywh(5, 19, 3, 1));

                    //---- btnPDCAPlus ----
                    btnPDCAPlus.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/addgreanbuble.png")));
                    btnPDCAPlus.setToolTipText("PDCA Zyklus einen Schritt weiter drehen");
                    btnPDCAPlus.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnPDCAPlusActionPerformed(e);
                        }
                    });
                    pnlDetails.add(btnPDCAPlus, CC.xy(9, 19));
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
        add(splitTableDetails, CC.xy(5, 5));

        //======== pnlButtonsLeft ========
        {
            pnlButtonsLeft.setLayout(new BoxLayout(pnlButtonsLeft, BoxLayout.X_AXIS));

            //---- btnAddVorgang ----
            btnAddVorgang.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/edit_add.png")));
            btnAddVorgang.setToolTipText("Neuen Vorgang erstellen (nicht Bewohnerbezogen)");
            btnAddVorgang.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnAddVorgangActionPerformed(e);
                }
            });
            pnlButtonsLeft.add(btnAddVorgang);
        }
        add(pnlButtonsLeft, CC.xy(3, 7));

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
                btnAddBericht.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/edit_add.png")));
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
                btnDetails.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/graphic-design.png")));
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
                btnPrint.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/printer1.png")));
                btnPrint.setEnabled(false);
                btnPrint.setToolTipText("Vorgang drucken");
                panel5.add(btnPrint);

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
                panel5.add(btnEndReactivate);

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
                panel5.add(btnSystemInfo);
            }
            splitButtonsCenter.setTopComponent(panel5);

            //======== pnlButtonsRight ========
            {
                pnlButtonsRight.setLayout(new BoxLayout(pnlButtonsRight, BoxLayout.X_AXIS));

                //---- btnApply ----
                btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
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
                btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
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
        add(splitButtonsCenter, CC.xy(5, 7));
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
        btnAddVorgang.setEnabled(enabled);
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
        //To change body of implemented methods use File | Settings | File Templates.
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


    protected void initAuthorizationMap() {
        authorizationMap = new HashMap<JComponent, ArrayList<Short>>();

        /**
         * btnNewKat ist der Knopf der neue Kategorien hinzufügt.
         * Man muss mindestens Manager sein um den drücken zu können.
         */
        //authorizationMap.put(btnNewKat, new ArrayList());
        //authorizationMap.get(btnNewKat).add(InternalClassACL.MANAGER);

    }

    protected void setCenterButtons2Edit(String text) {
        if (laufendeOperation == LAUFENDE_OPERATION_NICHTS) {
            textmessageTL = SYSTools.flashLabel(lblMessage, text);
            SYSTools.showSide(splitButtonsCenter, SYSTools.RIGHT_LOWER_SIDE, speedFast);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane scrollPane2;
    private JXTaskPaneContainer taskContainer;
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
    private JButton btnTakeOver;
    private JToggleButton btnAssign;
    private JLabel label7;
    private JComboBox cmbKat;
    private JLabel label8;
    private JLabel lblPDCA;
    private JButton btnPDCAPlus;
    private JScrollPane scrollPane1;
    private JList listOwner;
    private JPanel pnlButtonsLeft;
    private JButton btnAddVorgang;
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
