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
package op;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.plaf.basic.ThemePainter;
import com.jidesoft.status.LabelStatusBarItem;
import com.jidesoft.status.MemoryStatusBarItem;
import com.jidesoft.status.StatusBar;
import com.jidesoft.status.TimeStatusBarItem;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideLabel;
import com.jidesoft.swing.JideSplitPane;
import entity.building.Station;
import entity.building.StationTools;
import entity.info.*;
import entity.prescription.PrescriptionTools;
import entity.system.SYSLoginTools;
import entity.system.SYSPropsTools;
import gui.GUITools;
import gui.interfaces.CleanablePanel;
import op.allowance.PnlAllowance;
import op.care.PnlCare;
import op.care.med.structure.PnlMed;
import op.care.supervisor.PnlHandover;
import op.controlling.PnlControlling;
import op.dev.PnlDev;
import op.process.PnlProcess;
import op.settings.PnlSettings;
import op.system.DlgLogin;
import op.system.InternalClassACL;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.threads.PrintProcessor;
import op.tools.NursingRecordsPanel;
import op.tools.SYSConst;
import op.tools.SYSTools;
import op.users.PnlUser;
import op.welcome.PnlWelcome;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.VerticalLayout;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.SoftBevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.net.URI;
import java.text.DateFormat;
import java.util.*;

/**
 * @author __USER__
 */
public class FrmMain extends JFrame {
    public static final String internalClassID = "opde.mainframe";
    private boolean initPhase;
    private ArrayList<CollapsiblePane> listOfNursingrecords;

    private Map<Integer, Set<Resident>> specialities;
    private Map<Resident, JPanel> iconPanels;

    private DlgLogin dlgLogin;

    public DisplayManager getDisplayManager() {
        return displayManager;
    }

    private DisplayManager displayManager;
    private PrintProcessor printProcessor;

    public PrintProcessor getPrintProcessor() {
        return printProcessor;
    }

    private CleanablePanel currentVisiblePanel;
    private Resident currentResident;
    //    private String currentClassname;
    private JideButton previousProgButton;
    private LabelStatusBarItem labelUSER;
    private JScrollPane jspSearch, jspApps;
    private CollapsiblePanes panesSearch, panesApps;
    //    private HashMap<Resident, JideButton> bwButtonMap;
    private JideButton homeButton;
    private Logger logger;

    public FrmMain() {
        initPhase = true;

        logger = Logger.getLogger(getClass());

        initComponents();


        // for the timeout function
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (OPDE.getLogin() != null) {
                displayManager.touch();
            }
        }, AWTEvent.MOUSE_MOTION_EVENT_MASK);

        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (OPDE.getLogin() != null) {
                displayManager.touch();
            }
        }, AWTEvent.KEY_EVENT_MASK);

        currentVisiblePanel = null;
        currentResident = null;
        lblWait.setText(SYSTools.xx("misc.msg.wait"));
        lblWait.setVisible(false);
        listOfNursingrecords = new ArrayList<>();
        btnHelp.setToolTipText(SYSTools.xx("opde.mainframe.btnHelp.tooltip"));

        iconPanels = Collections.synchronizedMap(new HashMap<Resident, JPanel>());

        if (OPDE.isDebug()) {
            setSize(1280, 800); // Macbook White 13"
        } else {
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        setTitle(SYSTools.getWindowTitle(""));

        displayManager = new DisplayManager(pbMsg, lblMainMsg, lblSubMsg, pnlIcons, pbTimeout, o -> {
            logout();
            showLogin();
        });
        displayManager.start();

        printProcessor = new PrintProcessor();
        printProcessor.start();

        // StatusBar Setup
        final LabelStatusBarItem label = new LabelStatusBarItem("Line");
        label.setText(OPDE.getUrl() + " " + OPDE.getAppInfo().getBuildInformation());
        if (OPDE.isCustomJDBCUrl()) {
            label.setForeground(Color.RED);
        }
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        statusBar.add(label, JideBoxLayout.FLEXIBLE);
        labelUSER = new LabelStatusBarItem("Line");
        labelUSER.setText("--");
        labelUSER.setAlignment(JLabel.CENTER);
        labelUSER.setFont(new Font("Arial", Font.PLAIN, 14));
        statusBar.add(labelUSER, JideBoxLayout.FLEXIBLE);
        final TimeStatusBarItem time = new TimeStatusBarItem();
        time.setFont(new Font("Arial", Font.PLAIN, 14));
        time.setUpdateInterval(10000);
        time.setTextFormat(DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT));
        time.setAlignment(JLabel.CENTER);
        statusBar.add(time, JideBoxLayout.FLEXIBLE);
        final MemoryStatusBarItem gc = new MemoryStatusBarItem();
        gc.setFont(new Font("Arial", Font.PLAIN, 14));
        statusBar.add(gc, JideBoxLayout.FLEXIBLE);

        btnResetSplitpane.setToolTipText(SYSTools.xx("opde.mainframe.btnSplitReset.tooltip"));

        initPhase = false;

    }

    public void completeRefresh() {
        emptyFrame();
        afterLogin();
    }


    public void emptyFrame() {
        if (currentVisiblePanel != null) {
            currentVisiblePanel.cleanup();
            pnlCard.remove(currentVisiblePanel);
            lblWait.setVisible(false);
        }
        currentVisiblePanel = null;
        displayManager.clearAllMessages();

        emptySearchArea();
    }

    public void emptySearchArea() {
        if (jspSearch != null)
            jspSearch.removeAll();
        jspSearch = null;
        if (jspApps != null)
            jspApps.removeAll();
        jspApps = null;

        if (panesSearch != null)
            panesSearch.removeAll();
        panesSearch = null;
        if (panesApps != null)
            panesApps.removeAll();
        panesApps = null;

        if (splitPaneLeft != null)
            splitPaneLeft.removeAll();
    }

    private void btnExitActionPerformed(ActionEvent e) {
        logout();
        showLogin();
    }

    private void thisWindowClosing(WindowEvent e) {
        if (OPDE.getLogin() != null) {
            logout();
        }

        System.exit(0);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);

        if (b) {
            showLogin();
        }
    }

    private void btnReloadActionPerformed(ActionEvent e) {
        if (currentVisiblePanel != null) {
            currentVisiblePanel.reload();
        }

    }

    private void splitPaneLeftPropertyChange(PropertyChangeEvent e) {
        if (!initPhase) {
            SYSPropsTools.storeProp("opde.mainframe:splitPaneLeftDividerLocation", SYSTools.getDividerInRelativePosition(splitPaneLeft).toString(), OPDE.getLogin().getUser());
        }
    }

    public CleanablePanel getCurrentVisiblePanel() {
        return currentVisiblePanel;
    }

    private void btnHelpActionPerformed(ActionEvent e) {
        if (currentVisiblePanel != null && Desktop.isDesktopSupported()) { // && currentVisiblePanel.getInternalClassID() != null && OPDE.getAppInfo().getInternalClasses().containsKey(currentVisiblePanel.getInternalClassID())

            if (currentVisiblePanel.getHelpKey() != null) {
                try {
                    URI uri = new URI(SYSTools.xx(currentVisiblePanel.getHelpKey()));
                    Desktop.getDesktop().browse(uri);
                } catch (Exception ex) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.mainframe.noHelpAvailable"));
                }
            } else {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.mainframe.noHelpAvailable"));
            }
        } else {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.mainframe.noHelpAvailable"));
        }
    }

    private void btnTXActionPerformed(ActionEvent e) {
        if (currentResident != null) {

            new TXEssenDoc(currentResident);


        } else {
            displayManager.addSubMessage(new DisplayMessage("misc.msg.choose.a.resident.first"));
        }
    }


    public void removeSpeciality(ResInfoType type, final Resident resident) {
        if (!type.isAlertType() && type.getType() != ResInfoTypeTools.TYPE_ABSENCE) {
            return;
        }
        synchronized (specialities) {
            specialities.get(type.getType()).remove(resident);
        }
        synchronized (iconPanels) {
            setIconPanel(iconPanels.get(resident), resident);
        }
        SwingUtilities.invokeLater(() -> iconPanels.get(resident).repaint());
    }

    public void addSpeciality(ResInfoType type, final Resident resident) {
        if (!type.isAlertType() && type.getType() != ResInfoTypeTools.TYPE_ABSENCE) {
            return;
        }

        if (type.getType() == ResInfoTypeTools.TYPE_FALLRISK && !ResInfoTools.hasSevereFallRisk(resident)) {
            return;
        }

        synchronized (specialities) {
            specialities.get(type.getType()).add(resident);
        }
        synchronized (iconPanels) {
            setIconPanel(iconPanels.get(resident), resident);
        }
        SwingUtilities.invokeLater(() -> iconPanels.get(resident).repaint());

    }


    private void btnResetSplitpaneActionPerformed(ActionEvent e) {
        splitPaneLeft.setDividerLocation(0, SYSTools.getDividerInAbsolutePosition(splitPaneLeft, 0.5d));
    }

    private void pnlMainComponentResized(ComponentEvent e) {
//        logger.debug(e.getComponent().getSize().width + "x" + e.getComponent().getSize().height);

        // Anpassung für kleine Bildschirme
        // https://github.com/tloehr/Offene-Pflege.de/issues/85
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                FormLayout layout = (FormLayout) pnlMain.getLayout();
                layout.setColumnSpec(3, ColumnSpec.decode((e.getComponent().getSize().width < 1440 ? "[50dlu,default,130dlu]" : "pref")));

                pnlMain.validate();
                pnlMain.repaint();
            }
        });


    }

    public void afterLogin() {
        OPDE.getDisplayManager().touch();
        dlgLogin = null;

        if (specialities != null) {
            synchronized (specialities) {
                SYSTools.clear(specialities);
            }
        }

        specialities = Collections.synchronizedMap(new HashMap<Integer, Set<Resident>>());
        synchronized (specialities) {
            specialities.put(ResInfoTypeTools.TYPE_ABSENCE, new HashSet<Resident>());
            specialities.put(ResInfoTypeTools.TYPE_INFECTION, new HashSet<Resident>());
            specialities.put(ResInfoTypeTools.TYPE_WARNING, new HashSet<Resident>());
            specialities.put(ResInfoTypeTools.TYPE_ALLERGY, new HashSet<Resident>());
            specialities.put(ResInfoTypeTools.TYPE_DIABETES, new HashSet<Resident>());
            specialities.put(ResInfoTypeTools.TYPE_FALLRISK, new HashSet<Resident>());

            for (ResInfo info : ResInfoTools.getSpecialInfos()) {
                // eine Sturzeinschätzung alleine reicht noch nicht aus, damit es als Symbol auftauchen soll. Nur ab bei
                // sturzrisiko = mittel und sturzrisiko = ja (was stark heisst)
                if (info.getResInfoType().getType() == ResInfoTypeTools.TYPE_FALLRISK) {
                    if (ResInfoTools.hasSevereFallRisk(info)) {
                        specialities.get(info.getResInfoType().getType()).add(info.getResident());
                    }
                } else { // alle anderen Specialities können ruhig dabei
                    specialities.get(info.getResInfoType().getType()).add(info.getResident());
                }
            }
        }

        prepareSearchArea();
        labelUSER.setText(OPDE.getLogin().getUser().getFullname());

        Runnable runnable = () -> {
            initPhase = true;
            double pos;
            try {
                pos = Double.parseDouble(OPDE.getProps().getProperty("opde.mainframe:splitPaneLeftDividerLocation"));
            } catch (Exception e) {
                pos = 0.5d;
            }
            splitPaneLeft.setDividerLocation(0, SYSTools.getDividerInAbsolutePosition(splitPaneLeft, pos));
            initPhase = false;
            homeButton.doClick();
        };

        SwingUtilities.invokeLater(runnable);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the PrinterForm Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlMain = new JPanel();
        pnlMainMessage = new JPanel();
        btnTX = new JButton();
        panel1 = new JPanel();
        pnlIcons = new JPanel();
        lblMainMsg = new JLabel();
        btnExit = new JButton();
        lblSubMsg = new JideLabel();
        btnHelp = new JButton();
        pbMsg = new JProgressBar();
        btnReload = new JButton();
        splitPaneLeft = new JideSplitPane();
        pnlCard = new JPanel();
        pnlWait = new JPanel();
        lblWait = new JLabel();
        pbTimeout = new JProgressBar();
        panel2 = new JPanel();
        btnResetSplitpane = new JButton();
        statusBar = new StatusBar();

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
            pnlMain.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    pnlMainComponentResized(e);
                }
            });
            pnlMain.setLayout(new FormLayout(
                "0dlu, $lcgap, pref, $lcgap, left:default:grow, 2*($rgap)",
                "$rgap, pref, $rgap, default:grow, 3dlu, $nlgap, bottom:pref, $lgap, 0dlu"));

            //======== pnlMainMessage ========
            {
                pnlMainMessage.setBackground(new Color(220, 223, 208));
                pnlMainMessage.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
                pnlMainMessage.setLayout(new FormLayout(
                    "0dlu, $lcgap, 23dlu, $lcgap, default:grow, $lcgap, min, $lcgap, 0dlu",
                    "0dlu, $lgap, 15dlu, $lgap, fill:11dlu, $lgap, fill:pref:grow, $lgap, pref, $lgap, 0dlu"));

                //---- btnTX ----
                btnTX.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/ambulance2.png")));
                btnTX.setBorder(null);
                btnTX.setBorderPainted(false);
                btnTX.setSelectedIcon(null);
                btnTX.setToolTipText("Verlegungsbericht drucken");
                btnTX.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnTX.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/ambulance2_pressed.png")));
                btnTX.setContentAreaFilled(false);
                btnTX.addActionListener(e -> btnTXActionPerformed(e));
                pnlMainMessage.add(btnTX, CC.xywh(3, 3, 1, 3));

                //======== panel1 ========
                {
                    panel1.setOpaque(false);
                    panel1.setLayout(new BoxLayout(panel1, BoxLayout.LINE_AXIS));

                    //======== pnlIcons ========
                    {
                        pnlIcons.setOpaque(false);
                        pnlIcons.setLayout(new BoxLayout(pnlIcons, BoxLayout.LINE_AXIS));
                    }
                    panel1.add(pnlIcons);

                    //---- lblMainMsg ----
                    lblMainMsg.setText("OPDE");
                    lblMainMsg.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 22));
                    lblMainMsg.setForeground(new Color(105, 80, 69));
                    lblMainMsg.setHorizontalAlignment(SwingConstants.CENTER);
                    lblMainMsg.setIcon(null);
                    lblMainMsg.setHorizontalTextPosition(SwingConstants.LEADING);
                    panel1.add(lblMainMsg);
                }
                pnlMainMessage.add(panel1, CC.xy(5, 3, CC.CENTER, CC.DEFAULT));

                //---- btnExit ----
                btnExit.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/lock.png")));
                btnExit.setBorder(null);
                btnExit.setBorderPainted(false);
                btnExit.setOpaque(false);
                btnExit.setContentAreaFilled(false);
                btnExit.setToolTipText("Abmelden");
                btnExit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnExit.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/lock_pressed.png")));
                btnExit.addActionListener(e -> btnExitActionPerformed(e));
                pnlMainMessage.add(btnExit, CC.xywh(7, 3, 1, 3));

                //---- lblSubMsg ----
                lblSubMsg.setText("OPDE");
                lblSubMsg.setFont(new Font("Arial", Font.PLAIN, 14));
                lblSubMsg.setForeground(new Color(105, 80, 69));
                lblSubMsg.setHorizontalAlignment(SwingConstants.CENTER);
                lblSubMsg.setVerticalAlignment(SwingConstants.TOP);
                pnlMainMessage.add(lblSubMsg, CC.xywh(5, 5, 1, 3));

                //---- btnHelp ----
                btnHelp.setText(null);
                btnHelp.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/help_circle_blue.png")));
                btnHelp.setBorderPainted(false);
                btnHelp.setContentAreaFilled(false);
                btnHelp.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/help_pressed.png")));
                btnHelp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnHelp.addActionListener(e -> btnHelpActionPerformed(e));
                pnlMainMessage.add(btnHelp, CC.xywh(3, 7, 1, 3));

                //---- pbMsg ----
                pbMsg.setValue(50);
                pbMsg.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 12));
                pbMsg.setForeground(new Color(105, 80, 69));
                pnlMainMessage.add(pbMsg, CC.xy(5, 9, CC.FILL, CC.FILL));

                //---- btnReload ----
                btnReload.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/reload0000.png")));
                btnReload.setBorder(null);
                btnReload.setBorderPainted(false);
                btnReload.setOpaque(false);
                btnReload.setContentAreaFilled(false);
                btnReload.setToolTipText("Ansicht aktualisieren");
                btnReload.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnReload.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/reload_pressed.png")));
                btnReload.addActionListener(e -> btnReloadActionPerformed(e));
                pnlMainMessage.add(btnReload, CC.xywh(7, 7, 1, 3));
            }
            pnlMain.add(pnlMainMessage, CC.xywh(3, 2, 4, 1, CC.DEFAULT, CC.FILL));

            //---- splitPaneLeft ----
            splitPaneLeft.setOneTouchExpandable(true);
            splitPaneLeft.setProportionalLayout(true);
            splitPaneLeft.setShowGripper(true);
            splitPaneLeft.addPropertyChangeListener("dividerLocation", e -> splitPaneLeftPropertyChange(e));
            pnlMain.add(splitPaneLeft, CC.xy(3, 4, CC.FILL, CC.FILL));

            //======== pnlCard ========
            {
                pnlCard.setLayout(new CardLayout());

                //======== pnlWait ========
                {
                    pnlWait.setLayout(new BorderLayout());

                    //---- lblWait ----
                    lblWait.setText("text");
                    lblWait.setFont(new Font("Arial", Font.BOLD, 22));
                    lblWait.setHorizontalAlignment(SwingConstants.CENTER);
                    pnlWait.add(lblWait, BorderLayout.CENTER);
                }
                pnlCard.add(pnlWait, "cardWait");
            }
            pnlMain.add(pnlCard, CC.xy(5, 4, CC.FILL, CC.FILL));
            pnlMain.add(pbTimeout, CC.xywh(3, 5, 4, 1, CC.FILL, CC.DEFAULT));

            //======== panel2 ========
            {
                panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

                //---- btnResetSplitpane ----
                btnResetSplitpane.setText(null);
                btnResetSplitpane.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/view_top_bottom.png")));
                btnResetSplitpane.setAlignmentY(1.0F);
                btnResetSplitpane.addActionListener(e -> btnResetSplitpaneActionPerformed(e));
                panel2.add(btnResetSplitpane);

                //---- statusBar ----
                statusBar.setBackground(new Color(238, 238, 238));
                statusBar.setAlignmentY(1.0F);
                panel2.add(statusBar);
            }
            pnlMain.add(panel2, CC.xywh(3, 7, 4, 1, CC.FILL, CC.BOTTOM));
        }
        contentPane.add(pnlMain);
        setSize(945, 695);
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    public void clearPreviousProgbutton() {
        if (previousProgButton != null) {
            previousProgButton.setBackground(Color.WHITE);
            previousProgButton.setOpaque(false);
        }
    }

    public JButton getBtnReload() {
        return btnReload;
    }

    public JScrollPane prepareSearchArea() {
        // fixes #1
        if (panesApps != null) {
            panesApps.removeAll();
        }
        for (CollapsiblePane cp : listOfNursingrecords) {
            cp.removeAll();
        }
        listOfNursingrecords.clear();

        panesSearch = new CollapsiblePanes();
        panesSearch.setLayout(new JideBoxLayout(panesSearch, JideBoxLayout.Y_AXIS));
        jspSearch = new JScrollPane(panesSearch);

        panesApps = new CollapsiblePanes();
        panesApps.setLayout(new JideBoxLayout(panesApps, JideBoxLayout.Y_AXIS));


        homeButton = GUITools.createHyperlinkButton(SYSTools.xx("opde.welcome"), SYSConst.icon22home, actionEvent -> {
            if (previousProgButton != null) {
                previousProgButton.setBackground(Color.WHITE);
                previousProgButton.setOpaque(false);
            }
            currentResident = null;
            previousProgButton = (JideButton) actionEvent.getSource();
            previousProgButton.setBackground(Color.YELLOW);
            previousProgButton.setOpaque(true);
            displayManager.setMainMessage(SYSTools.xx("opde.welcome"));
            displayManager.addSubMessage(new DisplayMessage(SYSTools.xx("opde.welcome.longDescription")));
            displayManager.clearAllIcons();
            setPanelTo(new PnlWelcome(jspSearch));
        });
        panesApps.add(homeButton);

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT s FROM Station s WHERE s.home.active = TRUE ORDER BY s.name");
        ArrayList<Station> stationen = new ArrayList<Station>(query.getResultList());
        em.close();
        for (Station station : stationen) {
            panesApps.add(addNursingRecords(station));
        }


        // May see the archive
        // https://github.com/tloehr/Offene-Pflege.de/issues/92
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.ARCHIVE, "nursingrecords.info")) {
            panesApps.add(addNursingRecords(null));
        }

        panesApps.addExpansion();
        jspApps = new myAppsScrollPane(panesApps);


//        final int width = panesApps.getPreferredSize().width + 16;
//        panesApps.addComponentListener(new ComponentAdapter() {
//            @Override
//            public void componentResized(ComponentEvent e) {
//                SwingUtilities.invokeLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        panesApps.setPreferredSize(new Dimension(width, panesApps.getPreferredSize().height));
//                        jspApps.revalidate();
//                    }
//                });
//
//
//
////                super.componentResized(e);    //To change body of overridden methods use File | Settings | File Templates.
////                panesApps.setPreferredSize(new Dimension(width, panesApps.getPreferredSize().height));
//
//            }
//        });

        splitPaneLeft.setOrientation(JideSplitPane.VERTICAL_SPLIT);
        splitPaneLeft.add(jspApps);
        splitPaneLeft.add(jspSearch);
        return jspSearch;
    }

    public CleanablePanel loadPanel(String classname) {
        CleanablePanel panel = null;
        currentResident = null;
//        currentClassname = classname;
        if (classname.equals("op.allowance.PnlAllowance")) {
            panel = new PnlAllowance(jspSearch);
        } else if (classname.equals("op.process.PnlProcess")) {
            panel = new PnlProcess(jspSearch);
        } else if (classname.equals("op.care.med.structure.PnlMed")) {
            panel = new PnlMed(jspSearch);
        } else if (classname.equals("op.users.PnlUser")) {
            panel = new PnlUser(jspSearch);
        } else if (classname.equals("op.care.supervisor.PnlHandover")) {
            panel = new PnlHandover(jspSearch);
        } else if (classname.equals("op.welcome.PnlWelcome")) {
            panel = new PnlWelcome(jspSearch);
        } else if (classname.equals("op.controlling.PnlControlling")) {
            panel = new PnlControlling(jspSearch, null);
        } else if (classname.equals("op.settings.PnlSettings")) {
            panel = new PnlSettings(jspSearch);
        } else if (classname.equals("op.dev.PnlDev")) {
            panel = new PnlDev();
        }
        return panel;
    }

//    public String getCurrentClassname() {
//        return currentClassname;
//    }
//
//    public void setCurrentClassname(String currentClassname) {
//        this.currentClassname = currentClassname;
//    }

    private CollapsiblePane addNursingRecords(final Station station) {
        EntityManager em = OPDE.createEM();
        Query query;
        if (station == null) {
            query = em.createQuery("SELECT b FROM Resident b WHERE b.station IS NULL ORDER BY b.name, b.firstname");
        } else {
            query = em.createQuery("SELECT b FROM Resident b WHERE b.station = :station ORDER BY b.name, b.firstname");
            query.setParameter("station", station);
        }
        ArrayList<Resident> residentList = new ArrayList<Resident>(query.getResultList());
        em.close();

        CollapsiblePane mypane = new CollapsiblePane(station == null ? SYSTools.xx("misc.msg.Archive") : station.getName());
        mypane.setFont(SYSConst.ARIAL14);
        mypane.setEmphasized(station != null && station.equals(StationTools.getStationForThisHost()));
        mypane.setStyle(CollapsiblePane.PLAIN_STYLE);

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new VerticalLayout());
        labelPanel.setBackground(Color.WHITE);

        try {
            mypane.setCollapsed(!mypane.isEmphasized());
        } catch (PropertyVetoException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        if (!residentList.isEmpty() && station != null) {
            JideButton button = GUITools.createHyperlinkButton("opde.mainframe.printdailyplan", SYSConst.icon22print2, e -> PrescriptionTools.printDailyPlan(station));
            button.setBackground(Color.WHITE);
            labelPanel.add(button);
        }

        for (final Resident resident : residentList) {
            ActionListener actionListener = actionEvent -> {
                if (currentResident != resident) {

                    if (previousProgButton != null) {
                        previousProgButton.setBackground(Color.WHITE);
                        previousProgButton.setOpaque(false);
                    }

                    previousProgButton = (JideButton) actionEvent.getSource();
                    previousProgButton.setBackground(Color.YELLOW);
                    previousProgButton.setOpaque(true);

                    currentResident = resident;

                    if (currentVisiblePanel instanceof PnlCare) {
                        ((NursingRecordsPanel) currentVisiblePanel).switchResident(resident);
                    } else {
                        setPanelTo(new PnlCare(resident, jspSearch));
                    }

                }
            };

            JPanel singleButtonPanel = new JPanel();
            singleButtonPanel.setLayout(new BoxLayout(singleButtonPanel, BoxLayout.X_AXIS));
            singleButtonPanel.setBorder(null);

            singleButtonPanel.setOpaque(false);

            String titel = resident.getName() + ", " + resident.getFirstname() + " [" + resident.getRIDAnonymous() + "]";
            JideButton button = GUITools.createHyperlinkButton(titel, null, actionListener);
            button.setForegroundOfState(ThemePainter.STATE_DEFAULT, resident.getGender() == ResidentTools.FEMALE ? Color.red : Color.blue);
            button.setBackground(Color.WHITE);

            singleButtonPanel.add(button);

            OPDE.getDisplayManager().clearSubMessages();

            if (station != null) { // not for the archive
                JPanel pnl = new JPanel();
                pnl.setLayout(new BoxLayout(pnl, BoxLayout.X_AXIS));
                pnl.setOpaque(false);
                pnl.setBorder(null);

                setIconPanel(pnl, resident);

                synchronized (iconPanels) {
                    iconPanels.put(resident, pnl);
                }

                singleButtonPanel.add(pnl);
            }

            labelPanel.add(singleButtonPanel);
        }

        mypane.setContentPane(labelPanel);
        listOfNursingrecords.add(mypane);
        return mypane;
    }


    private void setIconPanel(JPanel pnl, Resident resident) {
        pnl.removeAll();
        synchronized (specialities) {
            if (specialities.get(ResInfoTypeTools.TYPE_ABSENCE).contains(resident)) {
                pnl.add(new JLabel(SYSConst.icon16residentAbsent));
            }
            if (specialities.get(ResInfoTypeTools.TYPE_INFECTION).contains(resident)) {
                pnl.add(new JLabel(SYSConst.icon16biohazard));
            }
            if (specialities.get(ResInfoTypeTools.TYPE_DIABETES).contains(resident)) {
                pnl.add(new JLabel(SYSConst.icon16diabetes));
            }
            if (specialities.get(ResInfoTypeTools.TYPE_ALLERGY).contains(resident)) {
                pnl.add(new JLabel(SYSConst.icon16allergy));
            }
            if (specialities.get(ResInfoTypeTools.TYPE_WARNING).contains(resident)) {
                pnl.add(new JLabel(SYSConst.icon16warning));
            }
            if (specialities.get(ResInfoTypeTools.TYPE_FALLRISK).contains(resident)) {
                pnl.add(new JLabel(SYSConst.findIcon(SYSConst.icon16falling)));
            }
        }
    }


    /**
     * fixes #1
     */
    public void collapseNursingRecords() {
        try {
            for (CollapsiblePane cp : listOfNursingrecords) {
                cp.setCollapsed(true);
            }
        } catch (PropertyVetoException e) {
            // bah!
        }
    }

    public Resident getCurrentResident() {
        return currentResident;
    }

    public void setCurrentResident(Resident currentResident) {
        this.currentResident = currentResident;
    }

//    public boolean isBlocked() {
//        return lblWait.isVisible();
//    }

    public void setBlocked(boolean blocked) {
        if (blocked) {
            lblWait.setVisible(true);
            JPanel glass = new JPanel();
            glass.addMouseListener(new MouseAdapter() {
            });
            glass.addMouseMotionListener(new MouseMotionAdapter() {
            });
            glass.addKeyListener(new KeyAdapter() {
            });
            glass.setOpaque(false);
            setGlassPane(glass);
            getGlassPane().setVisible(true);
            ((CardLayout) pnlCard.getLayout()).show(pnlCard, "cardWait");
        } else {
            getGlassPane().setVisible(false);
            setGlassPane(new JPanel());
            ((CardLayout) pnlCard.getLayout()).show(pnlCard, "cardContent");
        }
    }

//    public Point getLocationForDialog(Dimension dimOfDialog) {
//        Point point = new Point((getSize().width - dimOfDialog.width) / 2, pnlMainMessage.getHeight() + 10);
//        SwingUtilities.convertPointToScreen(point, this);
//        return point;
//    }

    public void setPanelTo(CleanablePanel pnl) {
        if (currentVisiblePanel != null) {
            pnlCard.remove(currentVisiblePanel);
            currentVisiblePanel.cleanup();
        }

        currentVisiblePanel = pnl;
        pnlCard.add("cardContent", currentVisiblePanel);
        ((CardLayout) pnlCard.getLayout()).show(pnlCard, "cardContent");
    }

    public void dispose() {
        displayManager.interrupt();
        cleanup();
        super.dispose();
    }

    private void showLogin() {
        dlgLogin = new DlgLogin(o -> {
            if (o != null) {
                afterLogin();
            } else {
                // nicht mehr nötig durch install4j
                //FileUtils.deleteQuietly(new File(OPDE.getOPWD() + File.separatorChar + "opde.pid"));
                System.exit(0);
            }
        });
    }

    private void cleanup() {
        if (currentVisiblePanel != null) {
            currentVisiblePanel.cleanup();
        }
        synchronized (specialities) {
            specialities.clear();
        }
        synchronized (iconPanels) {
            iconPanels.clear();
        }
    }

    private void logout() {
        emptyFrame();
        OPDE.saveLocalProps();

        labelUSER.setText("--");

        SYSLoginTools.logout();

        // After the logout a forced garbage collection seems to be adequate
        System.gc();
        cleanup();

    }


    /**
     * this class is only used for resident names that are so long, that the icons are not visible anymore.
     * it simply tells Swing, that this particular scroll pane is 16 pixels wider than it really is.
     * dirty trick, but works.
     */
    private class myAppsScrollPane extends JScrollPane {
        private myAppsScrollPane(Component view) {
            super(view);
        }


        @Override
        public Dimension getPreferredSize() {

            Dimension dim = super.getPreferredSize();
            return new Dimension(dim.width + 16, dim.height);
        }
    }


    public Point getLocationForDialog(Dimension dimOfDialog) {
        Point point = new Point((getSize().width - dimOfDialog.width) / 2, pnlMainMessage.getHeight() + 10);
        SwingUtilities.convertPointToScreen(point, this);
        return point;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel pnlMain;
    private JPanel pnlMainMessage;
    private JButton btnTX;
    private JPanel panel1;
    private JPanel pnlIcons;
    private JLabel lblMainMsg;
    private JButton btnExit;
    private JideLabel lblSubMsg;
    private JButton btnHelp;
    private JProgressBar pbMsg;
    private JButton btnReload;
    private JideSplitPane splitPaneLeft;
    private JPanel pnlCard;
    private JPanel pnlWait;
    private JLabel lblWait;
    private JProgressBar pbTimeout;
    private JPanel panel2;
    private JButton btnResetSplitpane;
    private StatusBar statusBar;
    // End of variables declaration//GEN-END:variables


}
