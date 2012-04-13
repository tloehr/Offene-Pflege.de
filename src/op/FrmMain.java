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
package op;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.plaf.basic.ThemePainter;
import com.jidesoft.status.*;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.TitledSeparator;
import entity.Bewohner;
import entity.BewohnerTools;
import entity.Stationen;
import entity.StationenTools;
import entity.system.SYSLoginTools;
import op.bw.tg.PnlTG;
import op.care.PnlPflege;
import op.events.TaskPaneContentChangedEvent;
import op.events.TaskPaneContentChangedListener;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.*;
import op.vorgang.PnlVorgang;
import org.apache.commons.collections.Closure;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * @author __USER__
 */
public class FrmMain extends SheetableJFrame {

    public static final String internalClassID = "opde.mainframe";


    private boolean initPhase;

    public DisplayManager getDisplayManager() {
        return displayManager;
    }

    private DisplayManager displayManager;
    private JPanel currentVisiblePanel;
    private Bewohner currentBewohner;
    private JFrame thisFrame;
    private DlgLogin dlgLogin;
    private CollapsiblePane panelPflegeakte;

    private int positionToAddPanels;

    //    private java.util.List<JXTaskPane> bewohnerPanes, additionalSearchPanes;
//    private JXTaskPane programPane;
    private TaskPaneContentChangedListener tpccl;


    public FrmMain() {
        initPhase = true;
        initComponents();
        positionToAddPanels = 0;
        currentVisiblePanel = null;
        currentBewohner = null;
//        bewohnerPanes = new ArrayList<JXTaskPane>();
//        additionalSearchPanes = new ArrayList<JXTaskPane>();
//        programPane = null;
        thisFrame = this;
        setTitle(SYSTools.getWindowTitle("Pflegedokumentation"));
        this.setVisible(true);

        if (OPDE.isDebug()) {
            setSize(1440, 900);
            //setSize(1280, 1024);
        } else {
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        displayManager = new DisplayManager(pbMsg, lblMainMsg, lblSubMsg);
        displayManager.start();

        tpccl = new TaskPaneContentChangedListener() {
            @Override
            public void contentChanged(TaskPaneContentChangedEvent evt) {
                createSearchPane(evt.getTaskPanes(), evt.getTitle());
            }
        };

//        btnVerlegung = new JButton("Verlegung");
//        btnVerlegung.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/infored.png")));
//        btnVerlegung.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent actionEvent) {
//                if (currentBewohner != null) {
//                    SYSPrint.print(thisFrame, SYSTools.htmlUmlautConversion(op.care.DBHandling.getUeberleitung(currentBewohner, true, true, true, false, false, true, true, true, true, false)), false);
//                }
//            }
//        });


//        jtpPflegeakte.setComponentAt(jtpPflegeakte.getSelectedIndex(), pnl);
//        for (int i = 0; i < jtpPflegeakte.getTabCount(); i++) {
//            jtpPflegeakte.setEnabledAt(i, false);
//        }
//

        initPhase = false;

        showLogin();

    }


    private void emptyFrame() {
        currentVisiblePanel = new JPanel();
        currentVisiblePanel.setLayout(new BorderLayout());
        scrollMain.setViewportView(currentVisiblePanel);
        displayManager.clearAllMessages();
    }

    private void btnExitActionPerformed(ActionEvent e) {
        logout();
    }

    private void thisWindowClosing(WindowEvent e) {
        if (OPDE.getLogin() != null) {
            // OPDE.getDb().doLogout();
            try {
                OPDE.getDb().db.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }

        System.exit(0);
    }

    private void afterLogin() {

        createSearchPane(null, "");
        emptyFrame();

        displayManager.setMainMessage("Willkommen bei Offene-Pflege.de");
        displayManager.addSubMessage(new DisplayMessage("Wählen Sie eine(n) BewohnerIn aus oder das gewünschten Programm.", 2));

    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlMain = new JPanel();
        pnlMainMessage = new JPanel();
        lblMainMsg = new FadingLabel();
        btnExit = new JButton();
        lblSubMsg = new FadingLabel();
        pbMsg = new JProgressBar();
        jspSearch = new JScrollPane();
        panesSearch = new CollapsiblePanes();
        scrollMain = new JScrollPane();
        statusBar1 = new StatusBar();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Offene-Pflege.de");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                thisWindowClosing(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        //======== pnlMain ========
        {
            pnlMain.setLayout(new FormLayout(
                "$rgap, $lcgap, pref, $lcgap, left:default:grow, 2*($rgap)",
                "$rgap, default, $rgap, default:grow, $lgap, pref, $lgap, $rgap"));

            //======== pnlMainMessage ========
            {
                pnlMainMessage.setBackground(new Color(234, 237, 223));
                pnlMainMessage.setBorder(LineBorder.createBlackLineBorder());
                pnlMainMessage.setLayout(new FormLayout(
                    "$rgap, $lcgap, pref, $lcgap, default:grow, $lcgap, pref, $lcgap, $rgap",
                    "$rgap, $lgap, fill:13dlu, $lgap, fill:11dlu, $lgap, fill:default, $lgap, $rgap"));

                //---- lblMainMsg ----
                lblMainMsg.setText("Main Message Line for Main Text");
                lblMainMsg.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 16));
                lblMainMsg.setForeground(new Color(105, 80, 69));
                lblMainMsg.setHorizontalAlignment(SwingConstants.CENTER);
                pnlMainMessage.add(lblMainMsg, CC.xywh(3, 3, 3, 1));

                //---- btnExit ----
                btnExit.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/lock.png")));
                btnExit.setBorder(new EmptyBorder(5, 5, 5, 5));
                btnExit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnExitActionPerformed(e);
                    }
                });
                pnlMainMessage.add(btnExit, CC.xywh(7, 3, 1, 4));

                //---- lblSubMsg ----
                lblSubMsg.setText("Main Message Line for Main Text");
                lblSubMsg.setFont(new Font("Arial", Font.PLAIN, 14));
                lblSubMsg.setForeground(new Color(105, 80, 69));
                lblSubMsg.setHorizontalAlignment(SwingConstants.CENTER);
                pnlMainMessage.add(lblSubMsg, CC.xywh(3, 5, 3, 1));

                //---- pbMsg ----
                pbMsg.setValue(50);
                pbMsg.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 12));
                pbMsg.setForeground(new Color(105, 80, 69));
                pnlMainMessage.add(pbMsg, CC.xywh(3, 7, 5, 1, CC.FILL, CC.DEFAULT));
            }
            pnlMain.add(pnlMainMessage, CC.xywh(3, 2, 4, 1));

            //======== jspSearch ========
            {
                jspSearch.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        jspBWComponentResized(e);
                    }
                });

                //======== panesSearch ========
                {
                    panesSearch.setLayout(new BoxLayout(panesSearch, BoxLayout.PAGE_AXIS));
                }
                jspSearch.setViewportView(panesSearch);
            }
            pnlMain.add(jspSearch, CC.xy(3, 4, CC.FILL, CC.FILL));
            pnlMain.add(scrollMain, CC.xywh(5, 4, 2, 1, CC.FILL, CC.FILL));
            pnlMain.add(statusBar1, CC.xywh(3, 6, 4, 1, CC.FILL, CC.FILL));
        }
        contentPane.add(pnlMain);
        setSize(945, 695);
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

//    private void jtpMainStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jtpMainStateChanged
////        reloadDisplay(currentBewohner);
//    }//GEN-LAST:event_jtpMainStateChanged

    private void btnVerlegungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerlegungActionPerformed
        if (currentBewohner != null) {

        } else {
            displayManager.addSubMessage(new DisplayMessage("Bitte wählen Sie zuerst eine(n) BewohnerIn aus.", 5));
        }
    }//GEN-LAST:event_btnVerlegungActionPerformed

    private void jspBWComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspBWComponentResized
        //formatBWTable();
    }//GEN-LAST:event_jspBWComponentResized

    private void createProgramListe() {

        for (InternalClass ic : OPDE.getAppInfo().getMainClasses()) {


            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(ic.getInternalClassname(), InternalClassACL.EXECUTE)) {

                final String shortDescription = ic.getShortDescription();
                final String longDescription = ic.getLongDescription();
                final String javaclass = ic.getJavaClass();

                final CollapsiblePane programPane = new CollapsiblePane(shortDescription);
                programPane.setSlidingDirection(SwingConstants.SOUTH);
                programPane.setStyle(CollapsiblePane.TREE_STYLE);

//                JPanel labelPaanel = new JPanel();
//                labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.PAGE_AXIS));

                try {
                    programPane.setCollapsed(true);
                } catch (PropertyVetoException e) {
                    e.printStackTrace();
                }

//            ActionListener actionListener = new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                    displayManager.setMainMessage(shortDescription);
//                    displayManager.addSubMessage(new DisplayMessage(longDescription, 5));
//                    setPanelTo(loadPanel(javaclass, programPane));
//                }
//            };

//            JideButton button = GUITools.createHyperlinkButton(shortDescription, null, actionListener);
                programPane.setToolTipText(longDescription);
                programPane.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
                    @Override
                    public void paneExpanding(CollapsiblePaneEvent collapsiblePaneEvent) {
                        displayManager.setMainMessage(shortDescription);
                        displayManager.addSubMessage(new DisplayMessage(longDescription, 5));
                        setPanelTo(loadPanel(javaclass, programPane));

                        collapseAllOthers(programPane);

                    }
                });
                panesSearch.add(programPane);
            }
        }

//        programPane.setContentPane(labelPanel);
//        panesSearch.add(programPane);
    }

    private void createSearchPane(java.util.List<CollapsiblePane> panesToAdd, String titleForAdditionalPanes) {
        panesSearch = new CollapsiblePanes();
        panesSearch.setLayout(new JideBoxLayout(panesSearch, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(panesSearch);

        createProgramListe();
        createPflegedokumentation();

        if (panesToAdd != null) {
            if (!SYSTools.catchNull(titleForAdditionalPanes).isEmpty()) {
                TitledSeparator sep = new TitledSeparator();
                sep.setLabelComponent(new JLabel(titleForAdditionalPanes, null, JLabel.LEFT));
                sep.setTextAlignment(SwingConstants.RIGHT);
                sep.setBarAlignment(SwingConstants.CENTER);
                panesSearch.add(sep);
            }
            for (CollapsiblePane pane : panesToAdd) {
                panesSearch.add(pane);
            }
        }

        panesSearch.addExpansion();
    }

    private JPanel loadPanel(String classname, CollapsiblePane programPane) {

        JPanel panel = null;

        if (classname.equals("op.bw.tg.PnlTG")) {
            panel = new PnlTG(this, programPane);
        } else if (classname.equals("op.vorgang.PnlVorgang")) {
            panel = new PnlVorgang(null, null, this, tpccl);
        }

        return panel;
    }


    private void collapseBewohner() {
//        for (JXTaskPane pane : bewohnerPanes) {
//            pane.setCollapsed(true);
//        }
    }


    private CollapsiblePane createBewohnerListe(Stationen station) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM Bewohner b WHERE b.station = :station ORDER BY b.nachname, b.vorname");
        query.setParameter("station", station);
        Iterator<Bewohner> it = query.getResultList().iterator();
        em.close();

        CollapsiblePane mypane = new CollapsiblePane(station.getBezeichnung());
        mypane.setEmphasized(station.equals(StationenTools.getSpecialStation()));
        mypane.setSlidingDirection(SwingConstants.SOUTH);
        mypane.setStyle(CollapsiblePane.TREE_STYLE);

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.PAGE_AXIS));

        try {
            mypane.setCollapsed(!mypane.isEmphasized());
        } catch (PropertyVetoException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        while (it.hasNext()) {
            final Bewohner innerbewohner = it.next();

            ActionListener actionListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    displayManager.setMainMessage(BewohnerTools.getBWLabelText(innerbewohner));
                    currentBewohner = innerbewohner;
                    if (currentVisiblePanel instanceof NursingRecordsPanel) { // tritt nur beim ersten mal auf. Dann werden die Tabs freigeschaltet und erstmalig gefüllt.
                        ((NursingRecordsPanel) currentVisiblePanel).change2Bewohner(innerbewohner);
                    } else {
//                        programPane.setCollapsed(true);
                        currentVisiblePanel = new PnlPflege(thisFrame, innerbewohner, tpccl);
                        setPanelTo(currentVisiblePanel);
                    }
                }
            };

            String titel = innerbewohner.getNachname() + ", " + innerbewohner.getVorname() + " [" + innerbewohner.getBWKennung() + "]";
            JideButton button = GUITools.createHyperlinkButton(titel, null, actionListener);
            button.setForegroundOfState(ThemePainter.STATE_DEFAULT, innerbewohner.getGeschlecht() == BewohnerTools.GESCHLECHT_WEIBLICH ? Color.red : Color.blue);

            labelPanel.add(button);
        }

        mypane.setContentPane(labelPanel);
        return mypane;
    }


    /**
     * Das erstellt eine Liste aller Bewohner mit direktem Verweis auf die jeweilige Pflegeakte.
     */
    private void createPflegedokumentation() {

        if (!OPDE.getAppInfo().userHasAccessLevelForThisClass("nursingrecords.main", InternalClassACL.EXECUTE)) {
            return;
        }


        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("Stationen.findAllSorted");
        Iterator<Stationen> it = query.getResultList().iterator();
        em.close();

//        CollapsiblePane currentStationsPane = null;

//        TitledSeparator sep = new TitledSeparator();
//        sep.setLabelComponent(new JLabel("Pflegeakte", null, JLabel.LEFT));
//        sep.setTextAlignment(SwingConstants.RIGHT);
//        sep.setBarAlignment(SwingConstants.CENTER);
//        sep.setSeparatorBorder(new PartialGradientLineBorder(new Color[]{Color.BLACK, Color.WHITE}, 5, PartialSide.SOUTH));

        JPanel dokuPanel = new JPanel();
        dokuPanel.setLayout(new BoxLayout(dokuPanel, BoxLayout.PAGE_AXIS));

        JideButton buttonVerlegung = GUITools.createHyperlinkButton("Verlegungsbericht", new ImageIcon(getClass().getResource("/artwork/16x16/infored.png")), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (currentBewohner != null) {
                    SYSPrint.print(thisFrame, SYSTools.htmlUmlautConversion(op.care.DBHandling.getUeberleitung(currentBewohner, true, true, true, false, false, true, true, true, true, false)), false);
                } else {
                    displayManager.addSubMessage(new DisplayMessage("Bitte wählen Sie zuerst eine(n) BewohnerIn aus.", 5));
                }

            }
        });

        dokuPanel.add(buttonVerlegung);

        while (it.hasNext()) {
            dokuPanel.add(createBewohnerListe(it.next()));
        }

        panelPflegeakte = new CollapsiblePane("Pflegeakte", new ImageIcon(getClass().getResource("/artwork/16x16/pflegeakte.png")));
        panelPflegeakte.setSlidingDirection(SwingConstants.SOUTH);
        panelPflegeakte.setStyle(CollapsiblePane.TREE_STYLE);
        panelPflegeakte.setContentPane(dokuPanel);

        // Wenn die Pflegeakte ausgeklappt wird, dann werden alle Programme eingeklappt.
        panelPflegeakte.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                collapseAllOthers(panelPflegeakte);
            }
        });

        panesSearch.add(panelPflegeakte);
    }

    private void setPanelTo(JPanel pnl) {
        currentVisiblePanel = pnl;
//        pnlTopRight = new JPanel(new VerticalLayout());
//        btnVerlegung.setVisible(currentVisiblePanel instanceof PnlPflege);

        scrollMain.setViewportView(currentVisiblePanel);
    }

    public void dispose() {
        displayManager.interrupt();
        cleanup();
        super.dispose();
    }

    private void showLogin() {
        dlgLogin = new DlgLogin(this, "", new Closure() {
            @Override
            public void execute(Object o) {
                if (o != null) {
                    hideSheet();
                    afterLogin();
                } else {
                    System.exit(1);
                }
            }
        });
        showJDialogAsSheet(dlgLogin);
    }

    private void cleanup() {

        if (currentVisiblePanel instanceof CleanablePanel) {
            ((CleanablePanel) currentVisiblePanel).cleanup();
        }

    }

    private void logout() {
        emptyFrame();

        try {
            OPDE.closeDB();
        } catch (SQLException ex) {
            new DlgException(ex);
            ex.printStackTrace();
            System.exit(1);
        }

        SYSLoginTools.logout();

        System.gc();
        OPDE.getEMF().getCache().evictAll();

        cleanup();

        showLogin();
    }

    private void collapseAllOthers(CollapsiblePane thisPane) {
        for (int comp = 0; comp < panesSearch.getComponentCount(); comp++) {
            if (panesSearch.getComponent(comp) != thisPane && panesSearch.getComponent(comp) instanceof CollapsiblePane) {
                try {
                    ((CollapsiblePane) panesSearch.getComponent(comp)).setCollapsed(true);
                } catch (PropertyVetoException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel pnlMain;
    private JPanel pnlMainMessage;
    private FadingLabel lblMainMsg;
    private JButton btnExit;
    private FadingLabel lblSubMsg;
    private JProgressBar pbMsg;
    private JScrollPane jspSearch;
    private CollapsiblePanes panesSearch;
    private JScrollPane scrollMain;
    private StatusBar statusBar1;
    // End of variables declaration//GEN-END:variables


}
