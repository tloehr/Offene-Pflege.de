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
import entity.Bewohner;
import entity.BewohnerTools;
import entity.Stationen;
import entity.StationenTools;
import op.care.PnlPflege;
import op.events.TaskPaneContentChangedEvent;
import op.events.TaskPaneContentChangedListener;
import op.threads.DisplayManager;
import op.tools.*;
import op.vorgang.PnlVorgang;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.JXTitledSeparator;
import org.jdesktop.swingx.VerticalLayout;
import org.jdesktop.swingx.border.DropShadowBorder;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author __USER__
 */
public class FrmMain extends SheetableJFrame {

    public static final String internalClassID = "opde.mainframe";


    private boolean initPhase;
    private DisplayManager displayManager;
    private JPanel currentVisiblePanel;
    private Bewohner currentBewohner;
    private JFrame thisFrame;

    private int positionToAddPanels;

    private java.util.List<JXTaskPane> bewohnerPanes, additionalSearchPanes;
    private JXTaskPane programPane;
    private TaskPaneContentChangedListener tpccl;

    /**
     * Creates new form FrmPflege
     */
    private void btnLogoutHandler(ActionEvent e) {
        OPDE.ocmain.lockOC();
    }

    public FrmMain() {
        initPhase = true;
        initComponents();
        positionToAddPanels = 0;
        currentVisiblePanel = null;
        currentBewohner = null;
        bewohnerPanes = new ArrayList<JXTaskPane>();
        additionalSearchPanes = new ArrayList<JXTaskPane>();
        programPane = null;
        thisFrame = this;
        setTitle(SYSTools.getWindowTitle("Pflegedokumentation"));
        this.setVisible(true);

        if (OPDE.isDebug()) {
            setSize(1440, 900);
            //setSize(1280, 1024);
        } else {
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        tpccl = new TaskPaneContentChangedListener() {
            @Override
            public void contentChanged(TaskPaneContentChangedEvent evt) {
                panelSearch.removeAll();
                panelSearch.add((JPanel) programPane);

                if (evt.getWhereToPut() == TaskPaneContentChangedEvent.TOP) {
                    if (!evt.getTitle().isEmpty()){
                        panelSearch.add(new JXTitledSeparator(evt.getTitle(), SwingConstants.LEFT, new ImageIcon(getClass().getResource("/artwork/22x22/bw/viewmag1.png"))));
                    }
                    if (evt.getTaskPanes() != null) {
                        for (JXTaskPane pane : evt.getTaskPanes()) {
                            panelSearch.add((JPanel) pane);
                        }
                    }
                    panelSearch.add(new JXTitledSeparator("Pflegeakte", SwingConstants.LEFT, new ImageIcon(getClass().getResource("/artwork/22x22/pflegeakte.png"))));
                    for (JXTaskPane pane : bewohnerPanes) {
                        panelSearch.add((JPanel) pane);
                    }
                } else {
                    panelSearch.add(new JXTitledSeparator("Pflegeakte", SwingConstants.LEFT, new ImageIcon(getClass().getResource("/artwork/22x22/pflegeakte.png"))));
                    for (JXTaskPane pane : bewohnerPanes) {
                        panelSearch.add((JPanel) pane);
                    }
                    if (!evt.getTitle().isEmpty()){
                        panelSearch.add(new JXTitledSeparator(evt.getTitle(), SwingConstants.LEFT, new ImageIcon(getClass().getResource("/artwork/22x22/bw/viewmag1.png"))));
                    }
                    if (evt.getTaskPanes() != null) {
                        for (JXTaskPane pane : evt.getTaskPanes()) {
                            panelSearch.add((JPanel) pane);
                        }
                    }
                }
                panelSearch.validate();
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

        createProgramList();
        createBewohnerListe();

        displayManager = new DisplayManager(pbMsg, lblMainMsg, lblSubMsg);
        displayManager.start();

        btnLogout.setText("<html>" + OPDE.getLogin().getUser().getNameUndVorname() + "<br/>Abmelden</html>");

        JLabel lbl = new JLabel("Bitte wählen Sie eine(n) BewohnerIn aus.");
        lbl.setIcon(new ImageIcon(getClass().getResource("/artwork/256x256/agt_back.png")));
        lbl.setFont(new java.awt.Font("Lucida Grande", 1, 42));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setForeground(Color.blue);
        currentVisiblePanel = new JPanel();
        currentVisiblePanel.setLayout(new BorderLayout());
        currentVisiblePanel.add(lbl, BorderLayout.CENTER);
        scrollMain.setViewportView(currentVisiblePanel);

//        jtpPflegeakte.setComponentAt(jtpPflegeakte.getSelectedIndex(), pnl);
//        for (int i = 0; i < jtpPflegeakte.getTabCount(); i++) {
//            jtpPflegeakte.setEnabledAt(i, false);
//        }
//
        initPhase = false;

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
        btnVerlegung = new JButton();
        lblMainMsg = new FadingLabel();
        btnLogout = new JButton();
        lblSubMsg = new FadingLabel();
        pbMsg = new JProgressBar();
        button1 = new JButton();
        jspSearch = new JScrollPane();
        panelSearch = new JXTaskPaneContainer();
        scrollMain = new JScrollPane();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Offene-Pflege.de");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        //======== pnlMain ========
        {
            pnlMain.setLayout(new FormLayout(
                "$rgap, $lcgap, default, $lcgap, left:default:grow, 2*($rgap)",
                "$rgap, default, $rgap, default:grow, $lgap, $rgap"));

            //======== pnlMainMessage ========
            {
                pnlMainMessage.setBackground(new Color(234, 237, 223));
                pnlMainMessage.setBorder(new DropShadowBorder(Color.black, 5, 0.3f, 12, true, true, true, true));
                pnlMainMessage.setLayout(new FormLayout(
                    "$rgap, $lcgap, pref, $lcgap, default:grow, $lcgap, pref, $lcgap, $rgap",
                    "$rgap, $lgap, fill:13dlu, $lgap, fill:11dlu, $lgap, fill:default, $lgap, $rgap"));

                //---- btnVerlegung ----
                btnVerlegung.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 13));
                btnVerlegung.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/infored.png")));
                btnVerlegung.setBorder(null);
                btnVerlegung.setToolTipText("Verlegungsbericht drucken");
                btnVerlegung.setBorderPainted(false);
                btnVerlegung.setOpaque(true);
                btnVerlegung.setBackground(new Color(0, 0, 0, 0));
                btnVerlegung.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnVerlegungActionPerformed(e);
                    }
                });
                pnlMainMessage.add(btnVerlegung, CC.xywh(2, 3, 2, 5));

                //---- lblMainMsg ----
                lblMainMsg.setText("Main Message Line for Main Text");
                lblMainMsg.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 16));
                lblMainMsg.setForeground(new Color(105, 80, 69));
                lblMainMsg.setHorizontalAlignment(SwingConstants.CENTER);
                pnlMainMessage.add(lblMainMsg, CC.xy(5, 3));

                //---- btnLogout ----
                btnLogout.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/bw/lock.png")));
                btnLogout.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 13));
                btnLogout.setBorder(null);
                btnLogout.setBorderPainted(false);
                btnLogout.setBackground(new Color(0, 0, 0, 0));
                btnLogout.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnLogoutHandler(e);
                    }
                });
                pnlMainMessage.add(btnLogout, CC.xy(7, 3));

                //---- lblSubMsg ----
                lblSubMsg.setText("Main Message Line for Main Text");
                lblSubMsg.setFont(new Font("Arial", Font.PLAIN, 14));
                lblSubMsg.setForeground(new Color(105, 80, 69));
                lblSubMsg.setHorizontalAlignment(SwingConstants.CENTER);
                pnlMainMessage.add(lblSubMsg, CC.xy(5, 5));

                //---- pbMsg ----
                pbMsg.setValue(50);
                pbMsg.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 12));
                pbMsg.setForeground(new Color(105, 80, 69));
                pnlMainMessage.add(pbMsg, CC.xy(5, 7, CC.FILL, CC.DEFAULT));

                //---- button1 ----
                button1.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/bw/fileclose.png")));
                button1.setBorder(null);
                button1.setBorderPainted(false);
                button1.setBackground(new Color(0, 0, 0, 0));
                pnlMainMessage.add(button1, CC.xy(7, 7));
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
                jspSearch.setViewportView(panelSearch);
            }
            pnlMain.add(jspSearch, CC.xy(3, 4, CC.FILL, CC.FILL));
            pnlMain.add(scrollMain, CC.xywh(5, 4, 2, 1, CC.FILL, CC.FILL));
        }
        contentPane.add(pnlMain);
        setSize(945, 695);
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void jtpMainStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jtpMainStateChanged
//        reloadDisplay(currentBewohner);
    }//GEN-LAST:event_jtpMainStateChanged

    private void btnVerlegungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerlegungActionPerformed
        if (currentBewohner != null) {
            SYSPrint.print(thisFrame, SYSTools.htmlUmlautConversion(op.care.DBHandling.getUeberleitung(currentBewohner, true, true, true, false, false, true, true, true, true, false)), false);
        }
    }//GEN-LAST:event_btnVerlegungActionPerformed

    private void jspBWComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspBWComponentResized
        //formatBWTable();
    }//GEN-LAST:event_jspBWComponentResized

    private void jtpPflegeakteStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jtpPflegeakteStateChanged
//        reloadDisplay(currentBewohner);
    }//GEN-LAST:event_jtpPflegeakteStateChanged

    private void jtpUebersichtStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jtpUebersichtStateChanged
//        reloadDisplay(currentBewohner);
    }//GEN-LAST:event_jtpUebersichtStateChanged


    private void createProgramList() {
        programPane = new JXTaskPane("Programme");
        programPane.setSpecial(true);
        programPane.setCollapsed(false);

        panelSearch.add((JPanel) programPane);
        for (InternalClass ic : OPDE.getAppInfo().getMainClasses()) {


            final String shortDescription = ic.getShortDescription();
            final String javaclass = ic.getJavaClass();

            programPane.add(new AbstractAction() {
                {
                    putValue(Action.NAME, shortDescription);
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    displayManager.setMainMessage(shortDescription);
                    setPanelTo(loadPanel(javaclass));
                }
            });
        }
    }

    private JPanel loadPanel(String classname) {
//        Class c;
//        JPanel panel = null;
//        try {
//            c = Class.forName(classname);
//            panel = (JPanel) c.newInstance();
//        } catch (Exception ex) {
//            OPDE.debug(ex);
//        }


        JPanel panel = null;

//        if (classname.equals("op.bw.tg.PnlTG")) {
//            panel = new PnlTG(this, tpccl);
//        } else

         if (classname.equals("op.vorgang.PnlVorgang")) {
            panel = new PnlVorgang(null, null, this, tpccl);
        }

        return panel;
    }


    private void collapseBewohner() {
        for (JXTaskPane pane : bewohnerPanes) {
            pane.setCollapsed(true);
        }
    }

    /**
     * Das erstellt eine Liste aller Bewohner mit direktem Verweis auf die jeweilige Pflegeakte.
     */
    private void createBewohnerListe() {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("Bewohner.findAllActiveSortedByStationen");
        Iterator<Bewohner> it = query.getResultList().iterator();

        Stationen station = null;
        Stationen meineStation = StationenTools.getStation4ThisHost();
        JXTaskPane currentStationsPane = null;

        panelSearch.add(new JXTitledSeparator("Pflegeakte", SwingConstants.LEFT, new ImageIcon(getClass().getResource("/artwork/22x22/pflegeakte.png"))));

        while (it.hasNext()) {
            final Bewohner innerbewohner = it.next();

            // Neue Stationspanel ist nötig.
            if (station != innerbewohner.getStation()) {
                station = innerbewohner.getStation();
                currentStationsPane = new JXTaskPane(station.getBezeichnung());
                currentStationsPane.setSpecial(station.equals(meineStation));
                currentStationsPane.setCollapsed(!currentStationsPane.isSpecial());
                panelSearch.add((JPanel) currentStationsPane);
                bewohnerPanes.add(currentStationsPane);
                positionToAddPanels++; // Damit ich weiss, wo ich nachher die anderen Suchfelder dranhängen kann.
            }

            currentStationsPane.add(new AbstractAction() {
                {
                    String titel = innerbewohner.getNachname() + ", " + innerbewohner.getVorname() + " [" + innerbewohner.getBWKennung() + "]";
                    putValue(Action.NAME, "<html><font color=\"" + (BewohnerTools.isWeiblich(innerbewohner) ? "red" : "blue") + "\">" + titel + "</font></html>");
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    displayManager.setMainMessage(BewohnerTools.getBWLabelText(innerbewohner));
                    if (currentVisiblePanel instanceof NursingRecordsPanel) { // tritt nur beim ersten mal auf. Dann werden die Tabs freigeschaltet und erstmalig gefüllt.
                        ((NursingRecordsPanel) currentVisiblePanel).change2Bewohner(innerbewohner);
                    } else {
                        programPane.setCollapsed(true);
                        currentVisiblePanel = new PnlPflege(thisFrame, innerbewohner, tpccl);
                        setPanelTo(currentVisiblePanel);
                    }
                }
            });
        }

        em.close();
    }

    private void setPanelTo(JPanel pnl) {
        currentVisiblePanel = pnl;
//        pnlTopRight = new JPanel(new VerticalLayout());
        btnVerlegung.setVisible(currentVisiblePanel instanceof PnlPflege);
        if (!(currentVisiblePanel instanceof PnlPflege)) {
            collapseBewohner();
        }
        scrollMain.setViewportView(currentVisiblePanel);
    }

    public void dispose() {
        displayManager.interrupt();
        cleanup();
        super.dispose();
    }

    private void cleanup() {

        if (currentVisiblePanel instanceof CleanablePanel) {
            ((CleanablePanel) currentVisiblePanel).cleanup();
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel pnlMain;
    private JPanel pnlMainMessage;
    private JButton btnVerlegung;
    private FadingLabel lblMainMsg;
    private JButton btnLogout;
    private FadingLabel lblSubMsg;
    private JProgressBar pbMsg;
    private JButton button1;
    private JScrollPane jspSearch;
    private JXTaskPaneContainer panelSearch;
    private JScrollPane scrollMain;
    // End of variables declaration//GEN-END:variables


}
