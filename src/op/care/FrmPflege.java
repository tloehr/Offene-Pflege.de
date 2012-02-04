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
package op.care;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.Bewohner;
import entity.BewohnerTools;
import entity.Stationen;
import entity.StationenTools;
import op.OPDE;
import op.threads.HeapStat;
import op.tools.SYSPrint;
import op.tools.SYSTools;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
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
import java.util.Iterator;

/**
 * @author __USER__
 */
public class FrmPflege extends javax.swing.JFrame {

    public static final String internaalClassID = "opde.mainframe";


    private boolean initPhase;
    private HeapStat hs;
    private JPanel currentVisiblePanel;
    private Bewohner currentBewohner;
    private JFrame thisFrame;

    private int positionToAddPanels;

    private java.util.List<JXTaskPane> bewohnerPanes, programPanes, currentAdditionalSearchPanes;

    /**
     * Creates new form FrmPflege
     */
    private void btnLogoutHandler(ActionEvent e) {
        OPDE.ocmain.lockOC();
    }

    public FrmPflege() {
        initPhase = true;
        initComponents();
        positionToAddPanels = 0;
        currentVisiblePanel = null;
        currentBewohner = null;
        thisFrame = this;
        setTitle(SYSTools.getWindowTitle("Pflegedokumentation"));
        this.setVisible(true);

        if (OPDE.isDebug()) {
            setSize(1440, 900);
            //setSize(1280, 1024);
        } else {
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

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

        createBewohnerListe();

        hs = new HeapStat(pbMsg, lblMainMsg, lblSubMsg);
        hs.start();

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
        btnVerlegung = new JButton();
        pnlMain = new JPanel();
        pnlMainMessage = new JPanel();
        lblMainMsg = new JLabel();
        lblSubMsg = new JLabel();
        pbMsg = new JProgressBar();
        pnlTopRight = new JPanel();
        btnLogout = new JButton();
        jspSearch = new JScrollPane();
        panelSearch = new JXTaskPaneContainer();
        scrollMain = new JScrollPane();

        //---- btnVerlegung ----
        btnVerlegung.setText("Verlegung");
        btnVerlegung.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        btnVerlegung.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/infored.png")));
        btnVerlegung.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnVerlegungActionPerformed(e);
            }
        });

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Offene-Pflege.de");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        //======== pnlMain ========
        {
            pnlMain.setLayout(new FormLayout(
                    "$rgap, $lcgap, default, $lcgap, left:default:grow, $ugap, default, $lcgap, $rgap",
                    "$rgap, default, $rgap, default:grow, $lgap, $rgap"));

            //======== pnlMainMessage ========
            {
                pnlMainMessage.setBackground(new Color(234, 237, 223));
                pnlMainMessage.setBorder(new DropShadowBorder(Color.black, 5, 0.3f, 12, true, true, true, true));
                pnlMainMessage.setLayout(new FormLayout(
                        "default, $lcgap, default:grow, $lcgap, default",
                        "$rgap, $lgap, fill:13dlu, $lgap, fill:11dlu, $lgap, fill:default, $lgap, $rgap"));

                //---- lblMainMsg ----
                lblMainMsg.setText("Main Message Line for Main Text");
                lblMainMsg.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 16));
                lblMainMsg.setForeground(new Color(105, 80, 69));
                lblMainMsg.setHorizontalAlignment(SwingConstants.CENTER);
                pnlMainMessage.add(lblMainMsg, CC.xy(3, 3));

                //---- lblSubMsg ----
                lblSubMsg.setText("Main Message Line for Main Text");
                lblSubMsg.setFont(new Font("Arial", Font.PLAIN, 14));
                lblSubMsg.setForeground(new Color(105, 80, 69));
                lblSubMsg.setHorizontalAlignment(SwingConstants.CENTER);
                pnlMainMessage.add(lblSubMsg, CC.xy(3, 5));

                //---- pbMsg ----
                pbMsg.setValue(50);
                pnlMainMessage.add(pbMsg, CC.xy(3, 7, CC.FILL, CC.DEFAULT));
            }
            pnlMain.add(pnlMainMessage, CC.xywh(3, 2, 3, 1));

            //======== pnlTopRight ========
            {
                pnlTopRight.setLayout(new VerticalLayout());

                //---- btnLogout ----
                btnLogout.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/gpg.png")));
                btnLogout.setText("<html>L\u00f6hr, Torsten<br/>Abmelden</html>");
                btnLogout.setFont(new Font("Lucida Grande", Font.BOLD, 13));
                btnLogout.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnLogoutHandler(e);
                    }
                });
                pnlTopRight.add(btnLogout);
            }
            pnlMain.add(pnlTopRight, CC.xy(7, 2));

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
            pnlMain.add(scrollMain, CC.xywh(5, 4, 3, 1, CC.FILL, CC.FILL));
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


    private void createProgrammListe() {

           menuStructure.add(new String[]{"Pflege/Pflegeakte", "op.care.FrmPflege", "pflegeakte.png"});
        menuStructure.add(new String[]{"Pflege/Medikamente", "op.care.med.FrmMed", "agt_virussafe.png"});
        menuStructure.add(new String[]{"Pflege/Massnahmen", "op.care.planung.massnahmen.FrmMassnahmen", "work.png"});
        menuStructure.add(new String[]{"Bewohner/Bewohnerdaten", "op.bw.admin.FrmBWAttr", "groupevent.png"});
        menuStructure.add(new String[]{"Bewohner/Barbeträge", "op.bw.tg.FrmTG", "coins.png"});
        menuStructure.add(new String[]{"System/Mitarbeiter", "op.ma.admin.FrmUser", "identity.png"});
        menuStructure.add(new String[]{"System/Datei-Manager", "op.sysfiles.FrmFilesManager", "kfm.png"});
        menuStructure.add(new String[]{"Controlling/Controlling", "op.controlling.FrmCtrlMonitor", "kfind.png"});
        menuStructure.add(new String[]{"Controlling/Vorgänge", "op.vorgang.FrmVorgang", "utilities-file-archiver.png"});
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

        while (it.hasNext()) {
            final Bewohner innerbewohner = it.next();
            if (station != innerbewohner.getStation()) {
                station = innerbewohner.getStation();
                currentStationsPane = new JXTaskPane(station.getBezeichnung());
                currentStationsPane.setSpecial(station.equals(meineStation));
                currentStationsPane.setCollapsed(!currentStationsPane.isSpecial());
                panelSearch.add((JPanel) currentStationsPane);
                positionToAddPanels++; // Damit ich weiss, wo ich nachher die anderen Suchfelder dranhängen kann.
            }

            currentStationsPane.add(new AbstractAction() {
                {
                    String titel = innerbewohner.getNachname() + ", " + innerbewohner.getVorname() + " [" + innerbewohner.getBWKennung() + "]";
                    putValue(Action.NAME, "<html><font color=\"" + (BewohnerTools.isWeiblich(innerbewohner) ? "red" : "blue") + "\">" + titel + "</font></html>");
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    hs.setMainMessage(BewohnerTools.getBWLabelText(innerbewohner));
                    if (currentVisiblePanel instanceof PnlPflege) { // tritt nur beim ersten mal auf. Dann werden die Tabs freigeschaltet und erstmalig gefüllt.
                        ((CleanablePanel) currentVisiblePanel).change2Bewohner(innerbewohner);
                    } else {
                        currentVisiblePanel = new PnlPflege(thisFrame, panelSearch, innerbewohner);
                        setPanelTo(currentVisiblePanel);
                    }
                }
            });
        }


        em.close();
    }

    private void setPanelTo(JPanel pnl) {
        currentVisiblePanel = pnl;
        pnlTopRight = new JPanel(new VerticalLayout(0));
        if (currentVisiblePanel instanceof PnlPflege) {
            pnlTopRight.add(btnVerlegung);
        } else {
            collapseBewohner();
        }
        pnlTopRight.add(btnLogout);

        scrollMain.setViewportView(currentVisiblePanel);
    }

    public void dispose() {
        hs.interrupt();
        cleanup();
        super.dispose();
    }

    private void cleanup() {

        if (currentVisiblePanel instanceof CleanablePanel) {
            ((CleanablePanel) currentVisiblePanel).cleanup();
        }

    }

//    private void reloadDisplay(Bewohner bewohner) {
//        if (initPhase) {
//            return;
//        }
//        currentBewohner = bewohner;
//
//        cleanup();
//
//
//    }

//    private void removeSearchPanels(){
//        if (panelSearch.getComponentCount() > positionToAddPanels){
//            int count = panelSearch.getComponentCount();
//            for (int i = count-1; i >= positionToAddPanels; i--){
//                panelSearch.remove(positionToAddPanels);
//            }
//        }
//    }

//    private void reloadTable(Bewohner bewohner) {
//        // Bewohner-Liste
//
//        if (cbArchiv.isSelected()) { // Archivmodus gewünscht.
//            tblBW.setModel(new TMBW());
//            initPhase = true;
//            //cmbStation.setSelectedItem("Alle");
//            cmbStation.setEnabled(false);
//            initPhase = false;
//        } else {
//            tblBW.setModel(new TMBW((Stationen) cmbStation.getSelectedItem()));
//        }
//
//        tblBW.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
//        lsmbw.addListSelectionListener(lslBW);
//        if (tblBW.getModel().getRowCount() > 0) {
//            tblBW.setRowSelectionInterval(0, 0);
//        }
//        TableColumnModel tcm1 = tblBW.getColumnModel();
////        tcm1.getColumn(0).setHeaderValue("Name");
////        tcm1.getColumn(1).setHeaderValue("Info");
//        jspBW.dispatchEvent(new ComponentEvent(jspBW, ComponentEvent.COMPONENT_RESIZED));
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton btnVerlegung;
    private JPanel pnlMain;
    private JPanel pnlMainMessage;
    private JLabel lblMainMsg;
    private JLabel lblSubMsg;
    private JProgressBar pbMsg;
    private JPanel pnlTopRight;
    private JButton btnLogout;
    private JScrollPane jspSearch;
    private JXTaskPaneContainer panelSearch;
    private JScrollPane scrollMain;
    // End of variables declaration//GEN-END:variables


}
