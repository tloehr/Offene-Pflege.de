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
import com.jidesoft.plaf.basic.ThemePainter;
import com.jidesoft.status.LabelStatusBarItem;
import com.jidesoft.status.MemoryStatusBarItem;
import com.jidesoft.status.StatusBar;
import com.jidesoft.status.TimeStatusBarItem;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideSplitPane;
import com.jidesoft.wizard.WizardDialog;
import entity.Stationen;
import entity.StationenTools;
import entity.files.SYSFilesTools;
import entity.info.BWInfoTools;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.system.SYSLoginTools;
import entity.system.SYSPropsTools;
import op.allowance.PnlAllowance;
import op.residents.bwassistant.AddBWWizard;
import op.care.PnlCare;
import op.care.info.PnlInfo;
import op.care.med.PnlMed;
import op.process.PnlProcess;
import op.system.DlgLogin;
import op.system.InternalClass;
import op.system.InternalClassACL;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.threads.PrintProcessor;
import op.tools.*;
import op.users.PnlUser;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.VerticalLayout;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.SoftBevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author __USER__
 */
public class FrmMain extends JFrame {

    public static final String internalClassID = "opde.mainframe";

    private boolean initPhase;

    public DisplayManager getDisplayManager() {
        return displayManager;
    }

    private DisplayManager displayManager;

    private PrintProcessor printProcessor;

    public PrintProcessor getPrintProcessor() {
        return printProcessor;
    }

    private CleanablePanel currentVisiblePanel;
    private Resident currentBewohner;
    private JideButton previousProgButton;
    private DlgLogin dlgLogin;
    private LabelStatusBarItem labelUSER;
    private JScrollPane jspSearch, jspApps;
    private CollapsiblePanes panesSearch, panesApps;
    private Closure bwchange;
    private HashMap<Resident, JideButton> bwButtonMap;
//    private MouseListener blockingListener;


    public FrmMain() {
        initPhase = true;
        initComponents();

        currentVisiblePanel = null;
        currentBewohner = null;
        lblWait.setText(OPDE.lang.getString("misc.msg.wait"));
        lblWait.setVisible(false);

        if (OPDE.isDebug()) {
            setSize(1366, 768);
            //setSize(1280, 1024);
        } else {
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        setTitle(SYSTools.getWindowTitle(""));

        displayManager = new DisplayManager(pbMsg, lblMainMsg, lblSubMsg);
        displayManager.start();

        printProcessor = new PrintProcessor();
        printProcessor.start();


        bwchange = new Closure() {
            @Override
            public void execute(Object o) {
                currentBewohner = (Resident) o;
            }
        };

//        blockingListener = new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent mouseEvent) {
//                OPDE.debug("PING");
//                mouseEvent.consume();
//            }
//
//            @Override
//            public void mousePressed(MouseEvent mouseEvent) {
//                OPDE.debug("PING");
//                mouseEvent.consume();
//            }
//        };

        // StatusBar Setup
        final LabelStatusBarItem label = new LabelStatusBarItem("Line");
        label.setText(OPDE.getLocalProps().getProperty("javax.persistence.jdbc.url"));
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

        initPhase = false;

    }


    public void emptyFrame() {
        if (currentVisiblePanel != null) {
            pnlCard.remove(currentVisiblePanel);
            lblWait.setVisible(false);
        }
        currentVisiblePanel = null;
        displayManager.clearAllMessages();
        jspSearch.removeAll();
        jspSearch = null;
        jspApps.removeAll();
        jspApps = null;
        panesSearch.removeAll();
        panesSearch = null;
        panesApps.removeAll();
        panesApps = null;
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
            SYSPropsTools.storeProp(internalClassID + ":splitPaneLeftDividerLocation", SYSTools.getDividerInRelativePosition(splitPaneLeft).toString(), OPDE.getLogin().getUser());
        }
    }

    public void afterLogin() {

        prepareSearchArea();
        labelUSER.setText(OPDE.getLogin().getUser().getFullname());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                initPhase = true;
                double pos;
                try {
                    pos = Double.parseDouble(OPDE.getProps().getProperty(internalClassID + ":splitPaneLeftDividerLocation"));
                } catch (Exception e) {
                    pos = 0.5d;
                }
                splitPaneLeft.setDividerLocation(0, SYSTools.getDividerInAbsolutePosition(splitPaneLeft, pos));
                initPhase = false;
            }
        };

        SwingUtilities.invokeLater(runnable);

        displayManager.setMainMessage("Willkommen bei Offene-Pflege.de");
//        displayManager.addSubMessage(new DisplayMessage("Wählen Sie eine(n) BewohnerIn aus oder das gewünschten Programm.", 2));

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
        btnVerlegung = new JButton();
        btnExit = new JButton();
        lblSubMsg = new FadingLabel();
        pbMsg = new JProgressBar();
        btnReload = new JButton();
        splitPaneLeft = new JideSplitPane();
        pnlCard = new JPanel();
        pnlWait = new JPanel();
        lblWait = new JLabel();
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
            pnlMain.setLayout(new FormLayout(
                    "0dlu, $lcgap, pref, $lcgap, left:default:grow, 2*($rgap)",
                    "$rgap, default, $rgap, default:grow, $lgap, pref, $lgap, 0dlu"));

            //======== pnlMainMessage ========
            {
                pnlMainMessage.setBackground(new Color(220, 223, 208));
                pnlMainMessage.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
                pnlMainMessage.setLayout(new FormLayout(
                        "$rgap, $lcgap, pref, $lcgap, default:grow, 2*($lcgap, default), $lcgap, $rgap",
                        "$rgap, $lgap, pref, $lgap, fill:11dlu, $lgap, fill:15dlu, $lgap, $rgap"));

                //---- lblMainMsg ----
                lblMainMsg.setText("OPDE");
                lblMainMsg.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 22));
                lblMainMsg.setForeground(new Color(105, 80, 69));
                lblMainMsg.setHorizontalAlignment(SwingConstants.CENTER);
                lblMainMsg.setIcon(null);
                lblMainMsg.setHorizontalTextPosition(SwingConstants.LEADING);
                pnlMainMessage.add(lblMainMsg, CC.xy(5, 3));

                //---- btnVerlegung ----
                btnVerlegung.setIcon(new ImageIcon(getClass().getResource("/artwork/48x48/ambulance2.png")));
                btnVerlegung.setBorder(null);
                btnVerlegung.setBorderPainted(false);
                btnVerlegung.setContentAreaFilled(false);
                btnVerlegung.setSelectedIcon(null);
                btnVerlegung.setToolTipText("Verlegungsbericht drucken");
                btnVerlegung.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnVerlegung.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/48x48/ambulance2_pressed.png")));
                btnVerlegung.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnVerlegungActionPerformed(e);
                    }
                });
                pnlMainMessage.add(btnVerlegung, CC.xywh(3, 3, 1, 3));

                //---- btnExit ----
                btnExit.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/lock.png")));
                btnExit.setBorder(null);
                btnExit.setBorderPainted(false);
                btnExit.setOpaque(false);
                btnExit.setContentAreaFilled(false);
                btnExit.setToolTipText("Abmelden");
                btnExit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnExit.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/lock_pressed.png")));
                btnExit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnExitActionPerformed(e);
                    }
                });
                pnlMainMessage.add(btnExit, CC.xywh(9, 3, 1, 3));

                //---- lblSubMsg ----
                lblSubMsg.setText("OPDE");
                lblSubMsg.setFont(new Font("Arial", Font.PLAIN, 14));
                lblSubMsg.setForeground(new Color(105, 80, 69));
                lblSubMsg.setHorizontalAlignment(SwingConstants.CENTER);
                pnlMainMessage.add(lblSubMsg, CC.xy(5, 5));

                //---- pbMsg ----
                pbMsg.setValue(50);
                pbMsg.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 12));
                pbMsg.setForeground(new Color(105, 80, 69));
                pnlMainMessage.add(pbMsg, CC.xywh(5, 7, 3, 1, CC.FILL, CC.DEFAULT));

                //---- btnReload ----
                btnReload.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/reload.png")));
                btnReload.setBorder(null);
                btnReload.setBorderPainted(false);
                btnReload.setOpaque(false);
                btnReload.setContentAreaFilled(false);
                btnReload.setToolTipText("Ansicht aktualisieren");
                btnReload.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnReload.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/reload_pressed.png")));
                btnReload.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnReloadActionPerformed(e);
                    }
                });
                pnlMainMessage.add(btnReload, CC.xy(9, 7));
            }
            pnlMain.add(pnlMainMessage, CC.xywh(3, 2, 4, 1));

            //---- splitPaneLeft ----
            splitPaneLeft.setOneTouchExpandable(true);
            splitPaneLeft.setProportionalLayout(true);
            splitPaneLeft.setShowGripper(true);
            splitPaneLeft.addPropertyChangeListener("dividerLocation", new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent e) {
                    splitPaneLeftPropertyChange(e);
                }
            });
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

            //---- statusBar ----
            statusBar.setBackground(new Color(238, 238, 238));
            pnlMain.add(statusBar, CC.xywh(3, 6, 4, 1, CC.FILL, CC.FILL));
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
            SYSFilesTools.print(SYSTools.htmlUmlautConversion(BWInfoTools.getUeberleitung(currentBewohner, true, true, true, true, true, true, true, true)), false);
        } else {
            displayManager.addSubMessage(new DisplayMessage("Bitte wählen Sie zuerst eine(n) BewohnerIn aus.", 5));
        }
    }//GEN-LAST:event_btnVerlegungActionPerformed

    private CollapsiblePane addApps() {
        JPanel mypanel = new JPanel(new VerticalLayout());
        mypanel.setBackground(Color.WHITE);
        final CollapsiblePane mypane = new CollapsiblePane(OPDE.lang.getString(internalClassID + ".Apps"));
        mypane.setFont(SYSConst.ARIAL14);

        // Darf neue Bewohner anlegen
        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(PnlInfo.internalClassID, InternalClassACL.MANAGER)) { // => ACLMATRIX
            JideButton addbw = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".addbw"), SYSConst.icon22addbw, null);
//            final MyJDialog dlg = new MyJDialog();
            addbw.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());
            addbw.setAlignmentX(Component.LEFT_ALIGNMENT);
            addbw.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    final MyJDialog dlg = new MyJDialog();
                    WizardDialog wizard = new AddBWWizard(new Closure() {
                        @Override
                        public void execute(Object o) {
                            dlg.dispose();
                            jspSearch.removeAll();
                            jspSearch = null;
                            jspApps.removeAll();
                            jspApps = null;
                            panesSearch.removeAll();
                            panesSearch = null;
                            panesApps.removeAll();
                            panesApps = null;
                            splitPaneLeft.removeAll();
                            prepareSearchArea();
                        }
                    }).getWizard();
                    dlg.setContentPane(wizard.getContentPane());
                    dlg.pack();
                    dlg.setSize(new Dimension(800, 550));
                    dlg.setVisible(true);
                }
            });
            mypanel.add(addbw);
        }

        for (InternalClass ic : OPDE.getAppInfo().getMainClasses()) {

            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(ic.getInternalClassID(), InternalClassACL.EXECUTE)) {

                final String shortDescription = ic.getShortDescription();
                final String longDescription = ic.getLongDescription();
                final String javaclass = ic.getJavaclass();

                Icon icon = null;
                try {
                    icon = new ImageIcon(getClass().getResource("/artwork/22x22/" + ic.getIconname()));
                } catch (Exception e) {

                }

                JideButton progButton = GUITools.createHyperlinkButton(shortDescription, icon, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {

                        if (previousProgButton != null) {
                            previousProgButton.setBackground(Color.WHITE);
                            previousProgButton.setOpaque(false);
                        }
                        previousProgButton = (JideButton) actionEvent.getSource();
                        previousProgButton.setBackground(Color.YELLOW);
                        previousProgButton.setOpaque(true);

                        displayManager.setMainMessage(shortDescription);
                        displayManager.addSubMessage(new DisplayMessage(longDescription, 5));
                        setPanelTo(loadPanel(javaclass));

                    }
                });

                progButton.setToolTipText(longDescription);

                mypanel.add(progButton);
            }
        }
//        mypane.setSlidingDirection(SwingConstants.SOUTH);
        mypane.setStyle(CollapsiblePane.PLAIN_STYLE);
        mypane.setContentPane(mypanel);
//        panesApps.add(mypane);
//        panesApps.setBackground(mypane.getBackground());
        return mypane;
    }

    private void prepareSearchArea() {

        panesSearch = new CollapsiblePanes();
        panesSearch.setLayout(new JideBoxLayout(panesSearch, JideBoxLayout.Y_AXIS));
        jspSearch = new JScrollPane(panesSearch);

        panesApps = new CollapsiblePanes();
        panesApps.setLayout(new JideBoxLayout(panesApps, JideBoxLayout.Y_AXIS));

//        panesApps.add(addCommandNewBW());
        panesApps.add(addApps());

        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("Stationen.findAllSorted");
        ArrayList<Stationen> stationen = new ArrayList<Stationen>(query.getResultList());
        em.close();
        for (Stationen station : stationen) {
            panesApps.add(addNursingRecords(station));
        }

        // Darf auf das Archiv zugreifen
        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(PnlInfo.internalClassID, InternalClassACL.ARCHIVE)) { // => ACLMATRIX
            panesApps.add(addNursingRecords(null));
        }

        panesApps.addExpansion();
        jspApps = new JScrollPane(panesApps);

        splitPaneLeft.setOrientation(JideSplitPane.VERTICAL_SPLIT);
        splitPaneLeft.add(jspApps);
        splitPaneLeft.add(jspSearch);

    }

    private CleanablePanel loadPanel(String classname) {
        CleanablePanel panel = null;
        currentBewohner = null;
        if (classname.equals("op.allowance.PnlAllowance")) {
            panel = new PnlAllowance(jspSearch);
        } else if (classname.equals("op.process.PnlProcess")) {
            panel = new PnlProcess(jspSearch);
        } else if (classname.equals("op.care.med.PnlMed")) {
            panel = new PnlMed(jspSearch);
        } else if (classname.equals("op.users.PnlUser")) {
            panel = new PnlUser(jspSearch);
        }
        return panel;
    }

    private CollapsiblePane addNursingRecords(Stationen station) {
        bwButtonMap = new HashMap<Resident, JideButton>();

        EntityManager em = OPDE.createEM();
        Query query;
        if (station == null) {
            query = em.createQuery("SELECT b FROM Resident b WHERE b.station IS NULL ORDER BY b.nachname, b.vorname");
        } else {
            query = em.createQuery("SELECT b FROM Resident b WHERE b.station = :station ORDER BY b.nachname, b.vorname");
            query.setParameter("station", station);
        }
        ArrayList<Resident> bewohnerliste = new ArrayList<Resident>(query.getResultList());
        em.close();

        CollapsiblePane mypane = new CollapsiblePane(station == null ? OPDE.lang.getString("misc.msg.Archive") : station.getBezeichnung());
        mypane.setFont(SYSConst.ARIAL14);
        mypane.setEmphasized(station != null && station.equals(StationenTools.getSpecialStation()));
//        mypane.setSlidingDirection(SwingConstants.SOUTH);
        mypane.setStyle(CollapsiblePane.PLAIN_STYLE);

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new VerticalLayout());
        labelPanel.setBackground(Color.WHITE);

        try {
            mypane.setCollapsed(!mypane.isEmphasized());
        } catch (PropertyVetoException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        for (final Resident innerbewohner : bewohnerliste) {

            ActionListener actionListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (currentBewohner != innerbewohner) {

                        if (previousProgButton != null) {
                            previousProgButton.setBackground(Color.WHITE);
                            previousProgButton.setOpaque(false);
                        }

                        previousProgButton = (JideButton) actionEvent.getSource();
                        previousProgButton.setBackground(Color.YELLOW);
                        previousProgButton.setOpaque(true);

                        currentBewohner = innerbewohner;

//                        ((NursingRecordsPanel) currentVisiblePanel).switchResident(innerbewohner);
                        if (currentVisiblePanel instanceof PnlCare) {
                            ((NursingRecordsPanel) currentVisiblePanel).switchResident(innerbewohner);
                        } else {
                            setPanelTo(new PnlCare(innerbewohner, jspSearch));
                        }

                    }
                }
            };

            String titel = innerbewohner.getNachname() + ", " + innerbewohner.getVorname() + " [" + innerbewohner.getRID() + "]";
            JideButton button = GUITools.createHyperlinkButton(titel, null, actionListener);
            button.setForegroundOfState(ThemePainter.STATE_DEFAULT, innerbewohner.getGeschlecht() == ResidentTools.GESCHLECHT_WEIBLICH ? Color.red : Color.blue);
            button.setBackground(Color.WHITE);
//            button.putClientProperty("bewohner", innerbewohner);

            labelPanel.add(button);
            bwButtonMap.put(innerbewohner, button);
        }

        mypane.setContentPane(labelPanel);
        return mypane;
    }

    public void change2Bewohner(Resident bw) {
        if (previousProgButton != null) {
            previousProgButton.setBackground(Color.WHITE);
            previousProgButton.setOpaque(false);
        }
        currentBewohner = bw;

        previousProgButton = bwButtonMap.get(bw);
        previousProgButton.setBackground(Color.YELLOW);
        previousProgButton.setOpaque(true);


        List<Component> list = findPathForComponent(panesApps, previousProgButton);

        if (list != null && !list.isEmpty()) {
            for (Component comp : list) {
                if (comp instanceof CollapsiblePane) {
                    try {
                        ((CollapsiblePane) comp).setCollapsed(false);
                        previousProgButton.scrollRectToVisible(previousProgButton.getBounds());
                    } catch (PropertyVetoException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    break;
                }
            }
        }
    }


    private java.util.List<Component> findPathForComponent(Container container, Component comp) {
        java.util.List<Component> result = null;

        java.util.List<Component> containerList = Arrays.asList(container.getComponents());

        for (Component component : containerList) {
            if (component.equals(comp)) { // ANKER
                result = new ArrayList<Component>();
                result.add(comp);
                break;
            } else {
                if (component instanceof Container) {
                    result = findPathForComponent((Container) component, comp);
                    if (result != null) {
                        result.add(component);
                        break;
                    }
                }
            }
        }
        return result;
    }

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

    public Point getLocationForDialog(Dimension dimOfDialog) {
        Point point = new Point((getSize().width - dimOfDialog.width) / 2, pnlMainMessage.getHeight() + 10);
        SwingUtilities.convertPointToScreen(point, this);
        return point;
    }

    private void setPanelTo(CleanablePanel pnl) {
        if (currentVisiblePanel != null) {
            pnlCard.remove(currentVisiblePanel);
        }

        currentVisiblePanel = pnl;
//        pnlMain.add(currentVisiblePanel, CC.xywh(5, 4, 2, 2, CC.FILL, CC.FILL));
        pnlCard.add("cardContent", currentVisiblePanel);
        ((CardLayout) pnlCard.getLayout()).show(pnlCard, "cardContent");

    }

    public void dispose() {
        displayManager.interrupt();
        cleanup();
        super.dispose();
    }

    private void showLogin() {
        dlgLogin = new DlgLogin(new Closure() {
            @Override
            public void execute(Object o) {
                if (o != null) {
                    afterLogin();
                } else {
                    System.exit(1);
                }
            }
        });
    }

    private void cleanup() {
        if (currentVisiblePanel != null) {
            currentVisiblePanel.cleanup();
        }
    }

    private void logout() {
        emptyFrame();
        OPDE.saveLocalProps();

        labelUSER.setText("--");

        try {
            OPDE.closeDB();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        SYSLoginTools.logout();

        System.gc();
        cleanup();

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel pnlMain;
    private JPanel pnlMainMessage;
    private FadingLabel lblMainMsg;
    private JButton btnVerlegung;
    private JButton btnExit;
    private FadingLabel lblSubMsg;
    private JProgressBar pbMsg;
    private JButton btnReload;
    private JideSplitPane splitPaneLeft;
    private JPanel pnlCard;
    private JPanel pnlWait;
    private JLabel lblWait;
    private StatusBar statusBar;
    // End of variables declaration//GEN-END:variables


}
