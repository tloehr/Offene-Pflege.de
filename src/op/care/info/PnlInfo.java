/*
 * Created by JFormDesigner on Fri Jun 22 12:26:53 CEST 2012
 */

package op.care.info;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideTabbedPane;
import entity.Bewohner;
import entity.BewohnerTools;
import entity.files.SYSFilesTools;
import entity.info.*;
import entity.system.SYSPropsTools;
import op.OPDE;
import op.care.sysfiles.DlgFiles;
import op.care.sysfiles.PnlFiles;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.text.DateFormat;
import java.util.*;
import java.util.List;

/**
 * @author Torsten Löhr
 */
public class PnlInfo extends NursingRecordsPanel {
    public static final String internalClassID = "nursingrecords.info";

    public final Icon icon16redStar = new ImageIcon(getClass().getResource("/artwork/16x16/redstar.png"));
    public final Icon icon22add = new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png"));

    public final Icon icon22addPressed = new ImageIcon(getClass().getResource("/artwork/22x22/bw/add-pressed.png"));
    public final Icon icon22attach = new ImageIcon(getClass().getResource("/artwork/22x22/bw/attach.png"));
    public final Icon icon22attachPressed = new ImageIcon(getClass().getResource("/artwork/22x22/bw/attach_pressed.png"));
    public final Icon icon22edit = new ImageIcon(getClass().getResource("/artwork/22x22/bw/kspread.png"));
    public final Icon icon22editPressed = new ImageIcon(getClass().getResource("/artwork/22x22/bw/kspread_pressed.png"));
    public final Icon icon22gotoEnd = new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_end.png"));
    public final Icon icon22gotoEndPressed = new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_end_pressed.png"));
    public final Icon icon22stop = new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_stop.png"));
    public final Icon icon22stopPressed = new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_stop_pressed.png"));
    public final Icon icon48stop = new ImageIcon(getClass().getResource("/artwork/48x48/bw/player_stop.png"));
    public final Icon icon22delete = new ImageIcon(getClass().getResource("/artwork/22x22/bw/editdelete.png"));
    public final Icon icon22deletePressed = new ImageIcon(getClass().getResource("/artwork/22x22/bw/editdelete_pressed.png"));
    public final Icon icon48delete = new ImageIcon(getClass().getResource("/artwork/48x48/bw/editdelete.png"));
    public final Icon icon22view = new ImageIcon(getClass().getResource("/artwork/22x22/bw/viewmag.png"));
    public final Icon icon22viewPressed = new ImageIcon(getClass().getResource("/artwork/22x22/bw/viewmag-selected.png"));
    public final Icon icon22changePeriod = new ImageIcon(getClass().getResource("/artwork/22x22/bw/reload_page.png"));
    public final Icon icon22changePeriodPressed = new ImageIcon(getClass().getResource("/artwork/22x22/bw/reload_page_pressed.png"));
    public final Icon icon16bysecond = new ImageIcon(getClass().getResource("/artwork/16x16/bw/bysecond.png"));
    public final Icon icon16byday = new ImageIcon(getClass().getResource("/artwork/16x16/bw/byday.png"));
    public final Icon icon16pit = new ImageIcon(getClass().getResource("/artwork/16x16/bw/pointintime.png"));

    private final int MAX_HTML_LENGTH = 80;
    private Bewohner bewohner;
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;

    private HashMap<BWInfoTyp, CollapsiblePane> panelmap;
    private HashMap<BWInfoKat, List<BWInfoTyp>> bwinfotypen;
    private HashMap<BWInfo, JToggleButton> bwinfo4html;
    private HashMap<BWInfoTyp, List<BWInfo>> bwinfos;
    private List<BWInfoKat> kategorien;

    private JToggleButton tbEmpty, tbInactive;
    private JideButton btnBWDied, btnBWMovedOut, btnBWisAway, btnBWisBack;


    private boolean initPhase;

    public PnlInfo(Bewohner bewohner, JScrollPane jspSearch) {
        initPhase = true;
        this.jspSearch = jspSearch;
        this.bewohner = bewohner;
        initComponents();
        initPanel();
        initPhase = false;
    }

    private void initPanel() {
        prepareSearchArea();
        bwinfotypen = new HashMap<BWInfoKat, List<BWInfoTyp>>();
        bwinfos = new HashMap<BWInfoTyp, List<BWInfo>>();
        bwinfo4html = new HashMap<BWInfo, JToggleButton>();

        tabKat.setTabColorProvider(JideTabbedPane.ONENOTE_COLOR_PROVIDER);
        tabKat.setTabResizeMode(JideTabbedPane.RESIZE_MODE_DEFAULT);
        tabKat.setTabPlacement(JTabbedPane.LEFT);
//        tabKat.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        panelmap = new HashMap<BWInfoTyp, CollapsiblePane>();

        btnPrint.setEnabled(OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.PRINT)); // => ACL_MATRIX
        splitPane1.addPropertyChangeListener("dividerLocation", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                SYSPropsTools.storeProp(internalClassID + ":splitPane1DividerLocation", SYSTools.getDividerInRelativePosition(splitPane1).toString(), OPDE.getLogin().getUser());
            }
        });

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                double pos;
                try {
                    pos = Double.parseDouble(OPDE.getProps().getProperty(internalClassID + ":splitPane1DividerLocation"));
                } catch (Exception e) {
                    pos = 0.75d;
                }
                splitPane1.setDividerLocation(SYSTools.getDividerInAbsolutePosition(splitPane1, pos));
            }
        });

        change2Bewohner(bewohner);
    }

    @Override
    public void change2Bewohner(Bewohner bewohner) {
        this.bewohner = bewohner;
        GUITools.setBWDisplay(bewohner);
        reloadDisplay();
    }

    private void setAllViewButtonsOff(BWInfoKat kat) {
        initPhase = true;
        for (BWInfoTyp typ : bwinfotypen.get(kat)) {
            if (bwinfos.containsKey(typ)) {
                for (BWInfo info : bwinfos.get(typ)) {
                    if (bwinfo4html.containsKey(info)) {
                        bwinfo4html.get(info).setSelected(false);
                    }
                }
            }
        }
        initPhase = false;
    }


    private String getHTML() {
        if (tabKat.getSelectedIndex() < 0) return "";
        String html = "";

        BWInfoKat aktuelleKategorie = kategorien.get(tabKat.getSelectedIndex());

        for (BWInfoTyp typ : bwinfotypen.get(aktuelleKategorie)) {
            if (bwinfos.containsKey(typ)) {
                for (BWInfo info : bwinfos.get(typ)) {
                    if (bwinfo4html.containsKey(info) && bwinfo4html.get(info).isSelected()) {
                        html += html.isEmpty() ? "" : "<hline/>";
                        html += BWInfoTools.getHTML(info);
                    }
                }
            }
        }

        return html;
    }


    private void reloadDisplay() {
        /***
         *               _                 _ ____  _           _
         *      _ __ ___| | ___   __ _  __| |  _ \(_)___ _ __ | | __ _ _   _
         *     | '__/ _ \ |/ _ \ / _` |/ _` | | | | / __| '_ \| |/ _` | | | |
         *     | | |  __/ | (_) | (_| | (_| | |_| | \__ \ |_) | | (_| | |_| |
         *     |_|  \___|_|\___/ \__,_|\__,_|____/|_|___/ .__/|_|\__,_|\__, |
         *                                              |_|            |___/
         */
        initPhase = true;

        final boolean withworker = true;
        if (withworker) {


            OPDE.getMainframe().setBlocked(true);
            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));

//            lblWait.setText(OPDE.lang.getString("misc.msg.wait"));
//            ((CardLayout) pnlCard.getLayout()).show(pnlCard, "cardWait");

            tabKat.removeAll();
            bwinfos.clear();
            panelmap.clear();

            SwingWorker worker = new SwingWorker() {
                TableModel model;

                @Override
                protected Object doInBackground() throws Exception {
                    try {
                        int progress = 0;

                        // Eliminate empty categories
                        kategorien = new ArrayList<BWInfoKat>();
                        for (final BWInfoKat kat : BWInfoKatTools.getKategorien()) {
                            if (!BWInfoTypTools.findByKategorie(kat).isEmpty()) {
                                kategorien.add(kat);
                            }
                        }

                        // create tabs
                        for (final BWInfoKat kat : kategorien) {
                            progress++;
                            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, kategorien.size()));

                            if (!BWInfoTypTools.findByKategorie(kat).isEmpty()) {
                                tabKat.addTab(kat.getBezeichnung(), new JScrollPane(createCollapsiblePanesFor(kat)));
                            } else {
                                kategorien.remove(kat);
                            }
                        }
                    } catch (Exception e) {
                        OPDE.fatal(e);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    txtHTML.setText(null);
                    tabKat.setSelectedIndex(SYSPropsTools.getInteger(internalClassID + ":tabKatSelectedIndex"));
                    refreshDisplay();
                    btnBWDied.setEnabled(bewohner.isAktiv());
                    btnBWMovedOut.setEnabled(bewohner.isAktiv());
                    btnBWisAway.setEnabled(bewohner.isAktiv() && !BWInfoTools.isAbwesend(bewohner));
                    btnBWisBack.setEnabled(bewohner.isAktiv() && BWInfoTools.isAbwesend(bewohner));
                    initPhase = false;
                    OPDE.getDisplayManager().setProgressBarMessage(null);
                    OPDE.getMainframe().setBlocked(false);
                }
            };
            worker.execute();

        } else {
            kategorien = BWInfoKatTools.getKategorien();
            tabKat.removeAll();
            bwinfos.clear();
            panelmap.clear();
            for (BWInfoKat kat : kategorien) {
                tabKat.addTab(kat.getBezeichnung(), new JScrollPane(createCollapsiblePanesFor(kat)));
                txtHTML.setText(null);
            }
            tabKat.setSelectedIndex(SYSPropsTools.getInteger(internalClassID + ":tabKatSelectedIndex"));
            refreshDisplay();
        }
        initPhase = false;

    }


    private CollapsiblePanes createCollapsiblePanesFor(BWInfoKat kat) {
        CollapsiblePanes cpane = new CollapsiblePanes();
        try {
            cpane.setLayout(new JideBoxLayout(cpane, JideBoxLayout.Y_AXIS));

            bwinfotypen.put(kat, BWInfoTypTools.findByKategorie(kat));

            for (BWInfoTyp typ : bwinfotypen.get(kat)) {
                bwinfos.put(typ, BWInfoTools.findByBewohnerUndTyp(bewohner, typ));
                CollapsiblePane panel = createPanelFor(typ);
                cpane.add(panel);
                panel.setVisible((tbEmpty.isSelected() || !bwinfos.get(typ).isEmpty()) && (tbInactive.isSelected() || bwinfos.get(typ).isEmpty() || !bwinfos.get(typ).get(0).isAbgesetzt()));
                panelmap.put(typ, panel);
            }
            cpane.addExpansion();
        } catch (Exception e) {
            OPDE.fatal(e);
        }
        return cpane;
    }

    private void refreshDisplay() {
        BWInfoKat aktuelleKategorie = kategorien.get(tabKat.getSelectedIndex());
        for (BWInfoTyp typ : bwinfotypen.get(aktuelleKategorie)) {
            panelmap.get(typ).setVisible((tbEmpty.isSelected() || !bwinfos.get(typ).isEmpty()) && (tbInactive.isSelected() || bwinfos.get(typ).isEmpty() || !bwinfos.get(typ).get(0).isAbgesetzt()));
        }
    }

    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);
        searchPanes.add(addCommands());
        searchPanes.add(addFilters());
        searchPanes.addExpansion();
    }

    private CollapsiblePane addCommands() {

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout());
        mypanel.setBackground(Color.WHITE);

        CollapsiblePane searchPane = new CollapsiblePane(); //OPDE.lang.getString(internalClassID)
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);

        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.MANAGER)) {

            /***
             *      _       ____                 _
             *     (_)___  |  _ \  ___  __ _  __| |
             *     | / __| | | | |/ _ \/ _` |/ _` |
             *     | \__ \ | |_| |  __/ (_| | (_| |
             *     |_|___/ |____/ \___|\__,_|\__,_|
             *
             */
            btnBWMovedOut = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".resident.movedout"), new ImageIcon(getClass().getResource("/artwork/22x22/delete_user.png")), null);
            btnBWDied = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".resident.died"), new ImageIcon(getClass().getResource("/artwork/22x22/cross1.png")), null);
            btnBWisAway = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".resident.isaway"), new ImageIcon(getClass().getResource("/artwork/22x22/person-away.png")), null);
            btnBWisBack = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".resident.isback"), new ImageIcon(getClass().getResource("/artwork/22x22/person.png")), null);
            btnBWDied.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgUhrzeitDatum(OPDE.lang.getString(internalClassID + ".dlg.dateofdeath"), new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {
                                Date dod = (Date) o;
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    em.lock(em.merge(bewohner), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                                    bewohner.setStation(null);
                                    BWInfo hauf = em.merge(BWInfoTools.getLastBWInfo(bewohner, BWInfoTypTools.findByBWINFTYP(BWInfoTypTools.TYP_HEIMAUFNAHME)));
                                    em.lock(hauf, LockModeType.OPTIMISTIC);

                                    hauf.setBis(dod);
                                    hauf.setAbgesetztDurch(em.merge(OPDE.getLogin().getUser()));

                                    Properties props = BWInfoTools.getContent(hauf);
                                    props.setProperty("hauf", "verstorben");
                                    BWInfoTools.setContent(hauf, props);

                                    BewohnerTools.endOfStay(em, em.merge(bewohner), dod);
                                    em.getTransaction().commit();
                                    btnBWDied.setEnabled(false);
                                    btnBWMovedOut.setEnabled(false);
                                    btnBWisAway.setEnabled(false);
                                    btnBWisBack.setEnabled(false);
//                                    reloadDisplay();
//                                    GUITools.setBWDisplay(bewohner);
                                    OPDE.getMainframe().emptyFrame();
                                    OPDE.getMainframe().afterLogin();
                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.toHTMLForScreen(OPDE.lang.getString(internalClassID + ".msg.isdeadnow")), 5));
                                } catch (OptimisticLockException ole) {
                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                } catch (Exception e) {
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    OPDE.fatal(e);
                                } finally {
                                    em.close();
                                }
                            }
                        }
                    });
                }
            });
            btnBWDied.setEnabled(bewohner.isAktiv());
            mypanel.add(btnBWDied);
            /***
             *                                   _               _
             *      _ __ ___   _____   _____  __| |   ___  _   _| |_
             *     | '_ ` _ \ / _ \ \ / / _ \/ _` |  / _ \| | | | __|
             *     | | | | | | (_) \ V /  __/ (_| | | (_) | |_| | |_
             *     |_| |_| |_|\___/ \_/ \___|\__,_|  \___/ \__,_|\__|
             *
             */
            btnBWMovedOut.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgUhrzeitDatum(OPDE.lang.getString(internalClassID + ".dlg.dateofmoveout"), new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {
                                Date dod = (Date) o;
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    em.lock(em.merge(bewohner), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                                    bewohner.setStation(null);
                                    BWInfo hauf = em.merge(BWInfoTools.getLastBWInfo(bewohner, BWInfoTypTools.findByBWINFTYP(BWInfoTypTools.TYP_HEIMAUFNAHME)));
                                    em.lock(hauf, LockModeType.OPTIMISTIC);
                                    hauf.setBis(dod);
                                    hauf.setAbgesetztDurch(em.merge(OPDE.getLogin().getUser()));

                                    Properties props = BWInfoTools.getContent(hauf);
                                    props.setProperty("hauf", "verstorben");
                                    BWInfoTools.setContent(hauf, props);

                                    BewohnerTools.endOfStay(em, em.merge(bewohner), dod);
                                    em.getTransaction().commit();
                                    btnBWDied.setEnabled(false);
                                    btnBWMovedOut.setEnabled(false);
                                    btnBWisAway.setEnabled(false);
                                    btnBWisBack.setEnabled(false);
//                                    reloadDisplay();
//                                    GUITools.setBWDisplay(bewohner);
                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.toHTMLForScreen(OPDE.lang.getString(internalClassID + ".msg.hasgonenow")), 5));
                                    OPDE.getMainframe().emptyFrame();
                                    OPDE.getMainframe().afterLogin();
                                } catch (OptimisticLockException ole) {
                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    reload();
                                } catch (Exception e) {
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    OPDE.fatal(e);
                                } finally {
                                    em.close();
                                }
                            }
                        }
                    });
                }
            });
            btnBWMovedOut.setEnabled(bewohner.isAktiv());
            mypanel.add(btnBWMovedOut);
            /***
             *      _          _
             *     (_)___     / \__      ____ _ _   _
             *     | / __|   / _ \ \ /\ / / _` | | | |
             *     | \__ \  / ___ \ V  V / (_| | |_| |
             *     |_|___/ /_/   \_\_/\_/ \__,_|\__, |
             *                                  |___/
             */
            btnBWisAway.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    final JidePopup popup = new JidePopup();
                    final BWInfo abwesenheit = new BWInfo(BWInfoTypTools.findByBWINFTYP(BWInfoTypTools.TYP_ABWESENHEIT), bewohner);
                    PnlAbwesend pnlAbwesend = new PnlAbwesend(abwesenheit, new Closure() {
                        @Override
                        public void execute(Object o) {
                            popup.hidePopup();
                            if (o != null) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    em.lock(em.merge(bewohner), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                                    em.merge(o);
                                    em.getTransaction().commit();
                                    btnBWisAway.setEnabled(false);
                                    btnBWisBack.setEnabled(true);
                                    refreshTabKat(abwesenheit.getBwinfotyp().getBwInfokat());
                                    GUITools.setBWDisplay(bewohner);
                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".msg.isawaynow")));
                                } catch (OptimisticLockException ole) {
                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    reload();
                                } catch (Exception e) {
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    OPDE.fatal(e);
                                } finally {
                                    em.close();
                                }

                            }
                        }
                    });
                    popup.setMovable(false);
                    popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));

                    popup.setOwner(btnBWisAway);
                    popup.removeExcludedComponent(btnBWisAway);
                    popup.getContentPane().add(pnlAbwesend);
                    popup.setDefaultFocusComponent(pnlAbwesend);
                    GUITools.showPopup(popup, SwingConstants.NORTH_EAST);
                }
            });
            btnBWisAway.setEnabled(bewohner.isAktiv() && !BWInfoTools.isAbwesend(bewohner));
            mypanel.add(btnBWisAway);
            /***
             *      _       ____             _
             *     (_)___  | __ )  __ _  ___| | __
             *     | / __| |  _ \ / _` |/ __| |/ /
             *     | \__ \ | |_) | (_| | (__|   <
             *     |_|___/ |____/ \__,_|\___|_|\_\
             *
             */

            btnBWisBack.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        em.lock(em.merge(bewohner), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                        BWInfo lastabsence = em.merge(BWInfoTools.getLastBWInfo(bewohner, BWInfoTypTools.findByBWINFTYP(BWInfoTypTools.TYP_ABWESENHEIT)));
                        em.lock(lastabsence, LockModeType.OPTIMISTIC);
                        lastabsence.setBis(new Date());
                        lastabsence.setAbgesetztDurch(em.merge(OPDE.getLogin().getUser()));
                        em.getTransaction().commit();
                        btnBWisAway.setEnabled(true);
                        btnBWisBack.setEnabled(false);
                        refreshTabKat(lastabsence.getBwinfotyp().getBwInfokat());
                        GUITools.setBWDisplay(bewohner);
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".msg.isbacknow")));
                    } catch (OptimisticLockException ole) {
                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        reload();
                    } catch (Exception e) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        OPDE.fatal(e);
                    } finally {
                        em.close();
                    }
                }
            });
            btnBWisBack.setEnabled(bewohner.isAktiv() && BWInfoTools.isAbwesend(bewohner));
            mypanel.add(btnBWisBack);
        }

//        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.PRINT)) { // => ACL_MATRIX
//            JideButton printButton = GUITools.createHyperlinkButton(OPDE.lang.getString("misc.commands.print"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/printer.png")), new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                    SYSFilesTools.print("<h1 id=\"fonth1\" >" + OPDE.lang.getString(internalClassID) + ": " + BewohnerTools.getBWLabelText(bewohner) + "</h1>" + getHTML(), true);
//                }
//            });
//            mypanel.add(printButton);
//        }

        searchPane.setContentPane(mypanel);
        searchPanes.add(searchPane);


        searchPane.setContentPane(mypanel);
        return searchPane;
    }

    private CollapsiblePane addFilters() {
        JPanel labelPanel = new JPanel();
        labelPanel.setBackground(Color.WHITE);
        labelPanel.setLayout(new VerticalLayout(5));

        CollapsiblePane panelFilter = new CollapsiblePane(); // OPDE.lang.getString("misc.msg.Filter")
        panelFilter.setStyle(CollapsiblePane.PLAIN_STYLE);
        panelFilter.setCollapsible(false);

        tbEmpty = GUITools.getNiceToggleButton(OPDE.lang.getString(internalClassID + ".empty"));
        tbEmpty.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (initPhase) return;
                SYSPropsTools.storeState(internalClassID + ":tbEmpty", tbEmpty);
                refreshDisplay();
            }
        });
        tbEmpty.setHorizontalAlignment(SwingConstants.LEFT);
        labelPanel.add(tbEmpty);
        SYSPropsTools.restoreState(internalClassID + ":tbEmpty", tbEmpty);


        tbInactive = GUITools.getNiceToggleButton(OPDE.lang.getString(internalClassID + ".inactive"));
        tbInactive.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (initPhase) return;
                SYSPropsTools.storeState(internalClassID + ":tbInactive", tbInactive);
                refreshDisplay();
            }
        });
        tbInactive.setHorizontalAlignment(SwingConstants.LEFT);
        labelPanel.add(tbInactive);
        SYSPropsTools.restoreState(internalClassID + ":tbInactive", tbInactive);

        panelFilter.setContentPane(labelPanel);

        return panelFilter;
    }

    private String getHyperlinkButtonTextForPanelHead(BWInfoTyp typ) {
        String result = "";
        if (!bwinfos.get(typ).isEmpty()) {
            BWInfo ersterBWInfo = bwinfos.get(typ).get(0);
            result += "<font " + (ersterBWInfo.isAbgesetzt() ? SYSConst.html_lightslategrey : "color=\"BLACK\"") + ">";
            if (ersterBWInfo.isAbgesetzt() || typ.getIntervalMode() == BWInfoTypTools.MODE_INTERVAL_NOCONSTRAINTS || typ.getIntervalMode() == BWInfoTypTools.MODE_INTERVAL_SINGLE_INCIDENTS) {
                result += "<b>" + typ.getBWInfoKurz() + "</b>: " + bwinfos.get(typ).size() + " " + (bwinfos.get(typ).size() != 1 ? OPDE.lang.getString("misc.msg.Entries") : OPDE.lang.getString("misc.msg.Entry"));
            } else {
                result += DateFormat.getDateInstance().format(ersterBWInfo.getVon()) + " &rarr;| ";
                result += "<b>" + typ.getBWInfoKurz() + "</b>: ";
                result += SYSTools.getHTMLSubstring(ersterBWInfo.getHtml(), MAX_HTML_LENGTH);
            }

            result += "</font>";
        } else {
            result = typ.getBWInfoKurz() + ": <i>" + OPDE.lang.getString("misc.msg.noentryyet") + "<i>";
        }
        return SYSTools.toHTMLForScreen(result);
    }

    private String getHyperlinkButtonTextForPanelContent(BWInfo bwinfo) {
        String result = "";
        DateFormat df = bwinfo.isSingleIncident() ? DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT) : DateFormat.getDateInstance();
        result += df.format(bwinfo.getVon()) + (bwinfo.isSingleIncident() ? " " : " &rarr;" + (bwinfo.getBis().equals(SYSConst.DATE_BIS_AUF_WEITERES) ? "|" : " " + DateFormat.getDateInstance().format(bwinfo.getBis())));
        result += ": ";
        result += SYSTools.getHTMLSubstring(bwinfo.getHtml(), MAX_HTML_LENGTH);

        return SYSTools.toHTMLForScreen(result);
    }

    private Icon getIcon(BWInfoTyp typ) {
        if (typ.getIntervalMode() == BWInfoTypTools.MODE_INTERVAL_SINGLE_INCIDENTS) {
            return icon16pit;
        }
        if (typ.getIntervalMode() == BWInfoTypTools.MODE_INTERVAL_BYDAY) {
            return icon16byday;
        }
        return icon16bysecond;
    }

    private String getUserInfoAsHTML(BWInfo bwinfo) {
        String html = "</br><div id=\"fonttext\">";
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT);
        if (bwinfo.getBwinfotyp().getIntervalMode() == BWInfoTypTools.MODE_INTERVAL_BYDAY) {
            df = DateFormat.getDateInstance();
        }
        html += df.format(bwinfo.getVon()) + (bwinfo.isSingleIncident() ? " " : " &rarr; ") + bwinfo.getAngesetztDurch().getNameUndVorname();
        html += "</div>";
        return html;
    }


    private CollapsiblePane createPanelFor(final BWInfoTyp typ) {

        /***
         *      _   _ _____    _    ____  _____ ____
         *     | | | | ____|  / \  |  _ \| ____|  _ \
         *     | |_| |  _|   / _ \ | | | |  _| | |_) |
         *     |  _  | |___ / ___ \| |_| | |___|  _ <
         *     |_| |_|_____/_/   \_\____/|_____|_| \_\
         *
         */
        final CollapsiblePane panelForBWInfoTyp = new CollapsiblePane();
        try {

            final BWInfo ersterBWInfo = bwinfos.get(typ).isEmpty() ? null : bwinfos.get(typ).get(0);
            final boolean shallBeCollapsible = !bwinfos.get(typ).isEmpty() && (bwinfos.get(typ).size() > 1 || ersterBWInfo.isAbgesetzt() || typ.getIntervalMode() == BWInfoTypTools.MODE_INTERVAL_NOCONSTRAINTS || typ.getIntervalMode() == BWInfoTypTools.MODE_INTERVAL_SINGLE_INCIDENTS);

            JPanel titlePanel = new JPanel();
            titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.LINE_AXIS));

            JPanel titlePanelleft = new JPanel();
            titlePanelleft.setLayout(new BoxLayout(titlePanelleft, BoxLayout.LINE_AXIS));

            /***
             *      _     _       _    _           _   _                _   _                _
             *     | |   (_)_ __ | | _| |__  _   _| |_| |_ ___  _ __   | | | | ___  __ _  __| | ___ _ __
             *     | |   | | '_ \| |/ / '_ \| | | | __| __/ _ \| '_ \  | |_| |/ _ \/ _` |/ _` |/ _ \ '__|
             *     | |___| | | | |   <| |_) | |_| | |_| || (_) | | | | |  _  |  __/ (_| | (_| |  __/ |
             *     |_____|_|_| |_|_|\_\_.__/ \__,_|\__|\__\___/|_| |_| |_| |_|\___|\__,_|\__,_|\___|_|
             *
             */
            JideButton title = GUITools.createHyperlinkButton(getHyperlinkButtonTextForPanelHead(typ), getIcon(typ), null);
            title.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());
            title.setAlignmentX(Component.LEFT_ALIGNMENT);
            title.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (ersterBWInfo != null && !ersterBWInfo.isAbgesetzt() && !ersterBWInfo.isNoConstraints()) {
                        setAllViewButtonsOff(typ.getBwInfokat());
                        txtHTML.setText(SYSTools.toHTML(BWInfoTools.getHTML(ersterBWInfo)));
                    } else {
                        txtHTML.setText("<html>&nbsp;</html>");
                    }
                }
            });
            titlePanelleft.add(title);


            JPanel titlePanelright = new JPanel();
            titlePanelright.setLayout(new BoxLayout(titlePanelright, BoxLayout.LINE_AXIS));

            /***
             *      ____        _   _               __     ___                 _   _                _
             *     | __ ) _   _| |_| |_ ___  _ __   \ \   / (_) _____      __ | | | | ___  __ _  __| | ___ _ __
             *     |  _ \| | | | __| __/ _ \| '_ \   \ \ / /| |/ _ \ \ /\ / / | |_| |/ _ \/ _` |/ _` |/ _ \ '__|
             *     | |_) | |_| | |_| || (_) | | | |   \ V / | |  __/\ V  V /  |  _  |  __/ (_| | (_| |  __/ |
             *     |____/ \__,_|\__|\__\___/|_| |_|    \_/  |_|\___| \_/\_/   |_| |_|\___|\__,_|\__,_|\___|_|
             *
             */
            if (ersterBWInfo != null) {
                final JToggleButton btnView = new JToggleButton(icon22view);
                btnView.setSelectedIcon(icon22viewPressed);
                btnView.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnView.setContentAreaFilled(false);
                btnView.setBorder(null);
                btnView.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                bwinfo4html.put(ersterBWInfo, btnView);
                btnView.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent itemEvent) {
                        if (initPhase) return;
                        // Pressing the VIEW button on a header causes all members in that list to show themself, too.
                        if (!panelForBWInfoTyp.isCollapsed()) {
                            for (BWInfo info : bwinfos.get(typ)) {
                                if (bwinfo4html.containsKey(info)) {
                                    bwinfo4html.get(info).setSelected(btnView.isSelected());
                                }
                            }
                        }
                        String html = getHTML();
                        txtHTML.setText(html.isEmpty() ? "<html>&nbsp;</html>" : SYSTools.toHTMLForScreen(html));
                    }
                });
//                btnView.setEnabled(ersterBWInfo == null || !ersterBWInfo.isHeimaufnahme());
                titlePanelright.add(btnView);
            }

            /***
             *      ____        _   _                   _       _     _
             *     | __ ) _   _| |_| |_ ___  _ __      / \   __| | __| |
             *     |  _ \| | | | __| __/ _ \| '_ \    / _ \ / _` |/ _` |
             *     | |_) | |_| | |_| || (_) | | | |  / ___ \ (_| | (_| |
             *     |____/ \__,_|\__|\__\___/|_| |_| /_/   \_\__,_|\__,_|
             *
             */
            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.INSERT)) { // => ACL_MATRIX
                JButton btnAdd = new JButton(icon22add);
                btnAdd.setPressedIcon(icon22addPressed);
                btnAdd.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnAdd.setContentAreaFilled(false);
                btnAdd.setBorder(null);
                btnAdd.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        final BWInfo mybwinfo;
                        if (ersterBWInfo == null || typ.getIntervalMode() == BWInfoTypTools.MODE_INTERVAL_SINGLE_INCIDENTS || ersterBWInfo.isAbgesetzt()) {
                            mybwinfo = new BWInfo(typ, bewohner);
                        } else {
                            mybwinfo = ersterBWInfo.clone();
                            mybwinfo.setVon(new Date());
                            mybwinfo.setBis(SYSConst.DATE_BIS_AUF_WEITERES);
                        }

                        Closure closure = new Closure() {
                            @Override
                            public void execute(Object o) {
                                if (o != null) {
                                    EntityManager em = OPDE.createEM();
                                    try {
                                        em.getTransaction().begin();
                                        em.lock(em.merge(bewohner), LockModeType.OPTIMISTIC);
                                        BWInfo newinfo = em.merge((BWInfo) o);
                                        em.lock(newinfo, LockModeType.OPTIMISTIC);
                                        if (typ.getIntervalMode() != BWInfoTypTools.MODE_INTERVAL_SINGLE_INCIDENTS && ersterBWInfo != null) {
                                            BWInfo myersterbwinfo = em.merge(ersterBWInfo);
                                            em.lock(myersterbwinfo, LockModeType.OPTIMISTIC);
                                            myersterbwinfo.setBis(new DateTime(newinfo.getVon()).minusSeconds(1).toDate());
                                            myersterbwinfo.setAbgesetztDurch(newinfo.getAngesetztDurch());
                                        }
                                        newinfo.setHtml(BWInfoTools.getContentAsHTML(newinfo));
                                        newinfo = em.merge(newinfo);
                                        em.getTransaction().commit();
                                        refreshTabKat(newinfo.getBwinfotyp().getBwInfokat());
                                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.anewentryhasbeenadded")));
                                    } catch (OptimisticLockException ole) {
                                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                        if (em.getTransaction().isActive()) {
                                            em.getTransaction().rollback();
                                        }
                                        reload();
                                    } catch (Exception e) {
                                        if (em.getTransaction().isActive()) {
                                            em.getTransaction().rollback();
                                        }
                                        OPDE.fatal(e);
                                    } finally {
                                        em.close();
                                    }
                                } else {
                                    OPDE.getDisplayManager().clearSubMessages();
                                }
                            }
                        };

                        if (typ.getBwinftyp().equalsIgnoreCase(BWInfoTypTools.TYP_DIAGNOSE)) {
                            new DlgDiag(mybwinfo, closure);
                        } else {
                            new DlgInfo(mybwinfo, closure);
                        }
                    }
                });
                titlePanelright.add(btnAdd);
                btnAdd.setEnabled(ersterBWInfo == null || (!ersterBWInfo.isHeimaufnahme() && !ersterBWInfo.getBwinfotyp().isObsolete()));
            }

            /***
             *      ____        _   _                _____    _ _ _
             *     | __ ) _   _| |_| |_ ___  _ __   | ____|__| (_) |_
             *     |  _ \| | | | __| __/ _ \| '_ \  |  _| / _` | | __|
             *     | |_) | |_| | |_| || (_) | | | | | |__| (_| | | |_
             *     |____/ \__,_|\__|\__\___/|_| |_| |_____\__,_|_|\__|
             *
             */
            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE)) {  // => ACL_MATRIX
                JButton btnEdit = new JButton(icon22edit);
                btnEdit.setPressedIcon(icon22editPressed);
                btnEdit.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnEdit.setContentAreaFilled(false);
                btnEdit.setBorder(null);
                btnEdit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        new DlgInfo(ersterBWInfo, new Closure() {
                            @Override
                            public void execute(Object o) {
                                if (o != null) {
                                    EntityManager em = OPDE.createEM();
                                    try {
                                        em.getTransaction().begin();
                                        BWInfo newinfo = em.merge((BWInfo) o);
                                        em.lock(em.merge(bewohner), LockModeType.OPTIMISTIC);
                                        em.lock(newinfo, LockModeType.OPTIMISTIC);
                                        newinfo.setHtml(BWInfoTools.getContentAsHTML(newinfo));
                                        newinfo.setAngesetztDurch(em.merge(OPDE.getLogin().getUser()));
                                        em.getTransaction().commit();
                                        refreshTabKat(typ.getBwInfokat());
                                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.thisentryhasbeenedited")));
                                    } catch (OptimisticLockException ole) {
                                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                        if (em.getTransaction().isActive()) {
                                            em.getTransaction().rollback();
                                        }
                                        reload();
                                    } catch (Exception e) {
                                        if (em.getTransaction().isActive()) {
                                            em.getTransaction().rollback();
                                        }
                                        OPDE.fatal(e);
                                    } finally {
                                        em.close();
                                    }
                                }
                            }
                        });
                    }
                });
                boolean isManager = OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.MANAGER);
                boolean mayBeEdited = ersterBWInfo != null && !ersterBWInfo.isAbgesetzt() && !ersterBWInfo.isHeimaufnahme() && !ersterBWInfo.getBwinfotyp().isObsolete() && (OPDE.isAdmin() || isManager || ersterBWInfo.getAngesetztDurch().equals(OPDE.getLogin().getUser()));
                btnEdit.setEnabled(mayBeEdited);
            }

            /***
             *      ____        _   _                ____  _
             *     | __ ) _   _| |_| |_ ___  _ __   / ___|| |_ ___  _ __
             *     |  _ \| | | | __| __/ _ \| '_ \  \___ \| __/ _ \| '_ \
             *     | |_) | |_| | |_| || (_) | | | |  ___) | || (_) | |_) |
             *     |____/ \__,_|\__|\__\___/|_| |_| |____/ \__\___/| .__/
             *                                                     |_|
             */
            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.CANCEL)) { // => ACL_MATRIX
                JButton btnStop = new JButton(icon22stop);
                btnStop.setPressedIcon(icon22stopPressed);
                btnStop.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnStop.setContentAreaFilled(false);
                btnStop.setBorder(null);
                btnStop.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        new DlgYesNo(OPDE.lang.getString("misc.questions.cancel") + "<br/>" + ersterBWInfo.getHtml(), icon48stop, new Closure() {
                            @Override
                            public void execute(Object answer) {
                                if (answer.equals(JOptionPane.YES_OPTION)) {
                                    EntityManager em = OPDE.createEM();
                                    try {
                                        em.getTransaction().begin();
                                        BWInfo newinfo = em.merge(ersterBWInfo);
                                        em.lock(em.merge(bewohner), LockModeType.OPTIMISTIC);
                                        em.lock(newinfo, LockModeType.OPTIMISTIC);
                                        newinfo.setBis(new Date());
                                        newinfo.setAbgesetztDurch(em.merge(OPDE.getLogin().getUser()));
                                        em.getTransaction().commit();
                                        refreshTabKat(typ.getBwInfokat());
                                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.thisentryhasbeencancelled")));
                                    } catch (OptimisticLockException ole) {
                                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                        if (em.getTransaction().isActive()) {
                                            em.getTransaction().rollback();
                                        }
                                        reload();
                                    } catch (Exception e) {
                                        if (em.getTransaction().isActive()) {
                                            em.getTransaction().rollback();
                                        }
                                        OPDE.fatal(e);
                                    } finally {
                                        em.close();
                                    }
                                }
                            }
                        });
                    }
                });
                btnStop.setEnabled(ersterBWInfo != null && !ersterBWInfo.isAbgesetzt() && !ersterBWInfo.isHeimaufnahme() && typ.getIntervalMode() != BWInfoTypTools.MODE_INTERVAL_SINGLE_INCIDENTS);
                titlePanelright.add(btnStop);
            }

            /***
             *      ____        _   _                ____       _      _
             *     | __ ) _   _| |_| |_ ___  _ __   |  _ \  ___| | ___| |_ ___
             *     |  _ \| | | | __| __/ _ \| '_ \  | | | |/ _ \ |/ _ \ __/ _ \
             *     | |_) | |_| | |_| || (_) | | | | | |_| |  __/ |  __/ ||  __/
             *     |____/ \__,_|\__|\__\___/|_| |_| |____/ \___|_|\___|\__\___|
             *
             */
            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.DELETE)) {  // => ACL_MATRIX
                JButton btnDelete = new JButton(icon22delete);
                btnDelete.setPressedIcon(icon22deletePressed);
                btnDelete.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnDelete.setContentAreaFilled(false);
                btnDelete.setBorder(null);
                btnDelete.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        new DlgYesNo(OPDE.lang.getString("misc.questions.delete") + "<br/>" + ersterBWInfo.getHtml(), icon48delete, new Closure() {
                            @Override
                            public void execute(Object answer) {
                                if (answer.equals(JOptionPane.YES_OPTION)) {
                                    EntityManager em = OPDE.createEM();
                                    try {
                                        em.getTransaction().begin();
                                        em.merge(ersterBWInfo);
                                        em.lock(em.merge(bewohner), LockModeType.OPTIMISTIC);
                                        em.lock(ersterBWInfo, LockModeType.OPTIMISTIC);
                                        em.remove(ersterBWInfo);
                                        em.getTransaction().commit();
                                        refreshTabKat(typ.getBwInfokat());
                                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.thisentryhasbeendeleted")));
                                    } catch (OptimisticLockException ole) {
                                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                        if (em.getTransaction().isActive()) {
                                            em.getTransaction().rollback();
                                        }
                                        reload();
                                    } catch (Exception e) {
                                        if (em.getTransaction().isActive()) {
                                            em.getTransaction().rollback();
                                        }
                                        OPDE.fatal(e);
                                    } finally {
                                        em.close();
                                    }
                                }
                            }
                        });
                    }
                });
                btnDelete.setEnabled(ersterBWInfo != null && !ersterBWInfo.isHeimaufnahme());
                titlePanelright.add(btnDelete);
            }

            /***
             *      ____        _   _                _____ _ _         _   _   _             _       _   _                _
             *     | __ ) _   _| |_| |_ ___  _ __   |  ___(_) | ___   / \ | |_| |_ __ _  ___| |__   | | | | ___  __ _  __| | ___ _ __
             *     |  _ \| | | | __| __/ _ \| '_ \  | |_  | | |/ _ \ / _ \| __| __/ _` |/ __| '_ \  | |_| |/ _ \/ _` |/ _` |/ _ \ '__|
             *     | |_) | |_| | |_| || (_) | | | | |  _| | | |  __// ___ \ |_| || (_| | (__| | | | |  _  |  __/ (_| | (_| |  __/ |
             *     |____/ \__,_|\__|\__\___/|_| |_| |_|   |_|_|\___/_/   \_\__|\__\__,_|\___|_| |_| |_| |_|\___|\__,_|\__,_|\___|_|
             *
             */
            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(PnlFiles.internalClassID, InternalClassACL.INSERT)) { // => ACL_MATRIX
                JButton btnAttach = new JButton(icon22attach);
                btnAttach.setPressedIcon(icon22attachPressed);
                btnAttach.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnAttach.setContentAreaFilled(false);
                btnAttach.setBorder(null);
                btnAttach.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        new DlgFiles(ersterBWInfo, new Closure() {
                            @Override
                            public void execute(Object o) {
                                refreshTabKat(typ.getBwInfokat());
                            }
                        });
                    }
                });
                btnAttach.setEnabled(ersterBWInfo != null && !ersterBWInfo.isNoConstraints() && !ersterBWInfo.isAbgesetzt() && !ersterBWInfo.isHeimaufnahme());

                if (ersterBWInfo != null && !ersterBWInfo.isAbgesetzt() && ersterBWInfo.getAttachedFiles().size() > 0) {
                    JLabel lblNum = new JLabel(Integer.toString(ersterBWInfo.getAttachedFiles().size()), icon16redStar, SwingConstants.CENTER);
                    lblNum.setFont(SYSConst.ARIAL10BOLD);
                    lblNum.setForeground(Color.YELLOW);
                    lblNum.setHorizontalTextPosition(SwingConstants.CENTER);
                    DefaultOverlayable overlayableBtn = new DefaultOverlayable(btnAttach, lblNum, DefaultOverlayable.SOUTH_EAST);
                    overlayableBtn.setOpaque(false);
                    overlayableBtn.setOverlayVisible(ersterBWInfo != null);
                    titlePanelright.add(overlayableBtn);
                } else {
                    titlePanelright.add(btnAttach);
                }
            }

            titlePanelleft.setOpaque(false);
            titlePanelright.setOpaque(false);
            titlePanel.setOpaque(false);

            titlePanel.add(titlePanelleft);
            titlePanel.add(titlePanelright);

            panelForBWInfoTyp.setTitleLabelComponent(titlePanel);
            panelForBWInfoTyp.setSlidingDirection(SwingConstants.SOUTH);
            panelForBWInfoTyp.setStyle(CollapsiblePane.TREE_STYLE);
            panelForBWInfoTyp.setHorizontalAlignment(SwingConstants.LEADING);

            panelForBWInfoTyp.setEmphasized(bwinfos.get(typ).isEmpty());

            JPanel labelPanel = new JPanel();
            labelPanel.setLayout(new VerticalLayout());

            /***
             *       ___ ___  _  _ _____ ___ _  _ _____
             *      / __/ _ \| \| |_   _| __| \| |_   _|
             *     | (_| (_) | .` | | | | _|| .` | | |
             *      \___\___/|_|\_| |_| |___|_|\_| |_|
             *
             */
            if (!bwinfos.get(typ).isEmpty()) {
                // In diesen Fällen steht im Kopf kein BWInfo Eintrag. Daher müssen die alle hier rein geschrieben werden.
                int startwert = ersterBWInfo.isAbgesetzt() || ersterBWInfo.isNoConstraints() || ersterBWInfo.isSingleIncident() ? 0 : 1;

                for (int infonum = startwert; infonum < bwinfos.get(typ).size(); infonum++) {
                    final BWInfo innerBWInfo = bwinfos.get(typ).get(infonum);

                    final JideButton contentButton = GUITools.createHyperlinkButton(getHyperlinkButtonTextForPanelContent(innerBWInfo), null, null);
                    contentButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                    contentButton.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());
                    contentButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            setAllViewButtonsOff(typ.getBwInfokat());
                            txtHTML.setText(SYSTools.toHTML(BWInfoTools.getHTML(innerBWInfo)));
                        }
                    });


                    JPanel contentLine = new JPanel();
                    contentLine.setLayout(new BoxLayout(contentLine, BoxLayout.LINE_AXIS));

                    JPanel contentLineRight = new JPanel();
                    contentLineRight.setLayout(new BoxLayout(contentLineRight, BoxLayout.LINE_AXIS));

                    /***
                     *      ____        _   _               __     ___                  ____            _             _
                     *     | __ ) _   _| |_| |_ ___  _ __   \ \   / (_) _____      __  / ___|___  _ __ | |_ ___ _ __ | |_
                     *     |  _ \| | | | __| __/ _ \| '_ \   \ \ / /| |/ _ \ \ /\ / / | |   / _ \| '_ \| __/ _ \ '_ \| __|
                     *     | |_) | |_| | |_| || (_) | | | |   \ V / | |  __/\ V  V /  | |__| (_) | | | | ||  __/ | | | |_
                     *     |____/ \__,_|\__|\__\___/|_| |_|    \_/  |_|\___| \_/\_/    \____\___/|_| |_|\__\___|_| |_|\__|
                     *
                     */
                    JToggleButton btnView = new JToggleButton(icon22view);
                    btnView.setSelectedIcon(icon22viewPressed);
                    btnView.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    btnView.setContentAreaFilled(false);
                    btnView.setBorder(null);

                    bwinfo4html.put(innerBWInfo, btnView);
                    btnView.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent itemEvent) {
                            if (initPhase) return;
                            String html = getHTML();
                            txtHTML.setText(html.isEmpty() ? "<html>&nbsp;</html>" : SYSTools.toHTML(html));
                        }
                    });
                    contentLineRight.add(btnView);

                    /***
                     *      ____        _   _              ____  _                 ____            _             _
                     *     | __ ) _   _| |_| |_ ___  _ __ / ___|| |_ ___  _ __    / ___|___  _ __ | |_ ___ _ __ | |_
                     *     |  _ \| | | | __| __/ _ \| '_ \\___ \| __/ _ \| '_ \  | |   / _ \| '_ \| __/ _ \ '_ \| __|
                     *     | |_) | |_| | |_| || (_) | | | |___) | || (_) | |_) | | |__| (_) | | | | ||  __/ | | | |_
                     *     |____/ \__,_|\__|\__\___/|_| |_|____/ \__\___/| .__/   \____\___/|_| |_|\__\___|_| |_|\__|
                     *                                                   |_|
                     */
                    if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.CANCEL)) { // => ACL_MATRIX
                        JButton btnStop = new JButton(icon22stop);
                        btnStop.setPressedIcon(icon22stopPressed);
                        btnStop.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        btnStop.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        btnStop.setContentAreaFilled(false);
                        btnStop.setBorder(null);
                        btnStop.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                new DlgYesNo(OPDE.lang.getString("misc.questions.cancel") + "<br/>" + innerBWInfo.getHtml(), icon48stop, new Closure() {
                                    @Override
                                    public void execute(Object answer) {
                                        if (answer.equals(JOptionPane.YES_OPTION)) {
                                            EntityManager em = OPDE.createEM();
                                            try {
                                                em.getTransaction().begin();
                                                BWInfo newinfo = em.merge(innerBWInfo);
                                                em.lock(em.merge(bewohner), LockModeType.OPTIMISTIC);
                                                em.lock(newinfo, LockModeType.OPTIMISTIC);
                                                newinfo.setBis(new Date());
                                                newinfo.setAbgesetztDurch(em.merge(OPDE.getLogin().getUser()));
                                                em.getTransaction().commit();
                                                refreshTabKat(typ.getBwInfokat());
                                                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.thisentryhasbeencancelled")));
                                            } catch (OptimisticLockException ole) {
                                                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                                if (em.getTransaction().isActive()) {
                                                    em.getTransaction().rollback();
                                                }
                                                reload();
                                            } catch (Exception e) {
                                                if (em.getTransaction().isActive()) {
                                                    em.getTransaction().rollback();
                                                }
                                                OPDE.fatal(e);
                                            } finally {
                                                em.close();
                                            }
                                        }
                                    }
                                });
                            }
                        });
                        btnStop.setEnabled(!innerBWInfo.isAbgesetzt() && innerBWInfo.isNoConstraints());
                        contentLineRight.add(btnStop);
                    }

                    /***
                     *      ____        _   _                 ____ _                              ____           _           _
                     *     | __ ) _   _| |_| |_ ___  _ __    / ___| |__   __ _ _ __   __ _  ___  |  _ \ ___ _ __(_) ___   __| |
                     *     |  _ \| | | | __| __/ _ \| '_ \  | |   | '_ \ / _` | '_ \ / _` |/ _ \ | |_) / _ \ '__| |/ _ \ / _` |
                     *     | |_) | |_| | |_| || (_) | | | | | |___| | | | (_| | | | | (_| |  __/ |  __/  __/ |  | | (_) | (_| |
                     *     |____/ \__,_|\__|\__\___/|_| |_|  \____|_| |_|\__,_|_| |_|\__, |\___| |_|   \___|_|  |_|\___/ \__,_|
                     *                                                               |___/
                     */
                    final JButton btnChangePeriod = new JButton(icon22changePeriod);
                    btnChangePeriod.setPressedIcon(icon22changePeriodPressed);
                    btnChangePeriod.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    btnChangePeriod.setContentAreaFilled(false);
                    btnChangePeriod.setBorder(null);
                    btnChangePeriod.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                    ActionListener al;
                    if (typ.getIntervalMode() == BWInfoTypTools.MODE_INTERVAL_SINGLE_INCIDENTS) {
                        al = new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                final JidePopup popup = new JidePopup();
                                PnlZeitpunkt pnlZeitpunkt = new PnlZeitpunkt(innerBWInfo.getVon(), new Closure() {
                                    @Override
                                    public void execute(Object o) {
                                        popup.hidePopup();
                                        if (o != null) {

                                            EntityManager em = OPDE.createEM();
                                            try {
                                                em.getTransaction().begin();
                                                BWInfo mybwinfo = em.merge(innerBWInfo);
                                                em.lock(em.merge(bewohner), LockModeType.OPTIMISTIC);
                                                em.lock(mybwinfo, LockModeType.OPTIMISTIC);
                                                Date date = (Date) o;
                                                mybwinfo.setVon(date);
                                                mybwinfo.setBis(date);
                                                em.getTransaction().commit();
                                                refreshTabKat(typ.getBwInfokat());
                                                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.pitchanged")));
                                            } catch (OptimisticLockException ole) {
                                                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                                if (em.getTransaction().isActive()) {
                                                    em.getTransaction().rollback();
                                                }
                                                reload();
                                            } catch (Exception e) {
                                                if (em.getTransaction().isActive()) {
                                                    em.getTransaction().rollback();
                                                }
                                                OPDE.fatal(e);
                                            } finally {
                                                em.close();
                                            }
                                        }
                                    }
                                });
                                popup.setMovable(false);
                                popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));

                                popup.setOwner(panelForBWInfoTyp);
                                popup.removeExcludedComponent(panelForBWInfoTyp);
                                popup.getContentPane().add(pnlZeitpunkt);
                                popup.setDefaultFocusComponent(pnlZeitpunkt);
                                GUITools.showPopup(popup, SwingConstants.SOUTH_WEST);
                            }
                        };
                    } else {
                        al = new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                final JidePopup popup = new JidePopup();
                                Pair<Date, Date> ausdehnung = BWInfoTools.getMinMaxAusdehnung(innerBWInfo, new ArrayList<BWInfo>(bwinfos.get(typ)));
                                PnlZeitraum pnlZeitraum = new PnlZeitraum(ausdehnung.getFirst(), ausdehnung.getSecond(), innerBWInfo.getVon(), innerBWInfo.getBis(), new Closure() {
                                    @Override
                                    public void execute(Object o) {
                                        popup.hidePopup();
                                        if (o != null) {
                                            EntityManager em = OPDE.createEM();
                                            try {
                                                em.getTransaction().begin();
                                                BWInfo mybwinfo = em.merge(innerBWInfo);
                                                em.lock(em.merge(bewohner), LockModeType.OPTIMISTIC);
                                                em.lock(mybwinfo, LockModeType.OPTIMISTIC);
                                                Pair<Date, Date> period = (Pair<Date, Date>) o;
                                                mybwinfo.setVon(period.getFirst());
                                                mybwinfo.setBis(period.getSecond());
                                                em.getTransaction().commit();
                                                refreshTabKat(typ.getBwInfokat());
                                                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.periodchanged")));
                                            } catch (OptimisticLockException ole) {
                                                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                                if (em.getTransaction().isActive()) {
                                                    em.getTransaction().rollback();
                                                }
                                                reload();
                                            } catch (Exception e) {
                                                if (em.getTransaction().isActive()) {
                                                    em.getTransaction().rollback();
                                                }
                                                OPDE.fatal(e);
                                            } finally {
                                                em.close();
                                            }
                                        }

                                    }
                                });
                                popup.setMovable(false);
                                popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));

                                popup.setOwner(btnChangePeriod);
                                popup.removeExcludedComponent(btnChangePeriod);
                                popup.getContentPane().add(pnlZeitraum);
                                popup.setDefaultFocusComponent(pnlZeitraum);
                                GUITools.showPopup(popup, SwingConstants.SOUTH_WEST);
                            }
                        };
                    }
                    btnChangePeriod.addActionListener(al);
                    btnChangePeriod.setEnabled(!innerBWInfo.isHeimaufnahme());
                    contentLineRight.add(btnChangePeriod);

                    /***
                     *      ____        _   _                _____ _ _         _   _   _             _        ____            _             _
                     *     | __ ) _   _| |_| |_ ___  _ __   |  ___(_) | ___   / \ | |_| |_ __ _  ___| |__    / ___|___  _ __ | |_ ___ _ __ | |_
                     *     |  _ \| | | | __| __/ _ \| '_ \  | |_  | | |/ _ \ / _ \| __| __/ _` |/ __| '_ \  | |   / _ \| '_ \| __/ _ \ '_ \| __|
                     *     | |_) | |_| | |_| || (_) | | | | |  _| | | |  __// ___ \ |_| || (_| | (__| | | | | |__| (_) | | | | ||  __/ | | | |_
                     *     |____/ \__,_|\__|\__\___/|_| |_| |_|   |_|_|\___/_/   \_\__|\__\__,_|\___|_| |_|  \____\___/|_| |_|\__\___|_| |_|\__|
                     *
                     */
                    if (OPDE.getAppInfo().userHasAccessLevelForThisClass(PnlFiles.internalClassID, InternalClassACL.INSERT)) {    // => ACL_MATRIX
                        JButton btnAttach = new JButton(icon22attach);
                        btnAttach.setPressedIcon(icon22attachPressed);
                        btnAttach.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        btnAttach.setContentAreaFilled(false);
                        btnAttach.setBorder(null);
                        btnAttach.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        btnAttach.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                // No Attachments anymore for OLD entries. Only view.
                                // But active NoConstraints may get attachments
                                Closure closure = null;
                                if (innerBWInfo.isActiveNoConstraint()) {
                                    closure = new Closure() {
                                        @Override
                                        public void execute(Object o) {
                                            refreshTabKat(typ.getBwInfokat());
                                        }
                                    };
                                }

                                new DlgFiles(innerBWInfo, closure);
                            }
                        });

                        if (innerBWInfo.getAttachedFiles().size() > 0) {
                            JLabel lblNum = new JLabel(Integer.toString(innerBWInfo.getAttachedFiles().size()), icon16redStar, SwingConstants.CENTER);
                            lblNum.setFont(SYSConst.ARIAL10BOLD);
                            lblNum.setForeground(Color.YELLOW);
                            lblNum.setHorizontalTextPosition(SwingConstants.CENTER);
                            DefaultOverlayable overlayableBtn = new DefaultOverlayable(btnAttach, lblNum, DefaultOverlayable.SOUTH_EAST);
                            overlayableBtn.setOpaque(false);
                            overlayableBtn.setOverlayVisible(ersterBWInfo != null);
                            contentLineRight.add(overlayableBtn);
                        } else {
                            contentLineRight.add(btnAttach);
                        }

                        btnAttach.setEnabled(innerBWInfo.isActiveNoConstraint() || innerBWInfo.getAttachedFiles().size() > 0);
                    }

                    contentLine.add(contentButton);
                    contentLine.add(contentLineRight);

                    labelPanel.add(contentLine);
                }
            }

            panelForBWInfoTyp.setContentPane(labelPanel);
            panelForBWInfoTyp.setCollapsible(shallBeCollapsible);
            panelForBWInfoTyp.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
                @Override
                public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                    if (ersterBWInfo != null) {
                        bwinfo4html.get(ersterBWInfo).setEnabled(!ersterBWInfo.isAbgesetzt() || ersterBWInfo.isSingleIncident());
                    }
                    SYSPropsTools.storeBoolean(internalClassID + ":panelCollapsed:" + typ.getBwinftyp(), false, OPDE.getLogin().getUser());
                }

                @Override
                public void paneCollapsed(CollapsiblePaneEvent collapsiblePaneEvent) {
                    if (ersterBWInfo != null) {
                        bwinfo4html.get(ersterBWInfo).setEnabled(!ersterBWInfo.isAbgesetzt() || ersterBWInfo.isSingleIncident());
                    }
                    SYSPropsTools.storeBoolean(internalClassID + ":panelCollapsed:" + typ.getBwinftyp(), true, OPDE.getLogin().getUser());
                }
            });
            if (shallBeCollapsible) {
                panelForBWInfoTyp.setCollapsed(SYSPropsTools.isBooleanTrue(internalClassID + ":panelCollapsed:" + typ.getBwinftyp(), true));
            }
//            panelForBWInfoTyp.setVisible((tbEmpty.isSelected() || ersterBWInfo != null) && tbInactive.isSelected() || (ersterBWInfo != null && !ersterBWInfo.isAbgesetzt()));

        } catch (PropertyVetoException e) {
            OPDE.error(e);
        } catch (Exception e) {
            OPDE.fatal(e);
        }


        return panelForBWInfoTyp;
    }

    private void refreshTabKat(BWInfoKat kat) {
        int katindex = kategorien.indexOf(kat);
        tabKat.removeTabAt(katindex);
        tabKat.insertTab(kat.getBezeichnung(), null, new JScrollPane(createCollapsiblePanesFor(kat)), null, katindex);
        tabKat.setSelectedIndex(katindex);
        tabKat.repaint();
    }


    @Override
    public void cleanup() {
        bwinfotypen.clear();
        bwinfos.clear();
    }

    @Override
    public void reload() {
        reloadDisplay();
    }

    private void tabKatStateChanged(ChangeEvent e) {
        if (initPhase || tabKat.getTabCount() != kategorien.size()) return;
        String html = getHTML();
        txtHTML.setText(html.isEmpty() ? "<html>&nbsp;</html>" : SYSTools.toHTMLForScreen(html));
        SYSPropsTools.storeProp(internalClassID + ":tabKatSelectedIndex", Integer.toString(tabKat.getSelectedIndex()), OPDE.getLogin().getUser());
    }

    private void txtHTMLPropertyChange(PropertyChangeEvent e) {
        if (SYSTools.getDividerInRelativePosition(splitPane1) > 0.8d) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    splitPane1.setDividerLocation(0.75d);
                }
            });
        }
    }

    private void thisComponentResized(ComponentEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                double pos;
                try {
                    pos = Double.parseDouble(OPDE.getProps().getProperty(internalClassID + ":splitPane1DividerLocation"));
                } catch (Exception e) {
                    pos = 0.75d;
                }
                splitPane1.setDividerLocation(SYSTools.getDividerInAbsolutePosition(splitPane1, pos));
            }
        });
    }

    private void btnPrintActionPerformed(ActionEvent e) {
        SYSFilesTools.print(txtHTML.getText(), true);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        splitPane1 = new JSplitPane();
        tabKat = new JideTabbedPane();
        panel1 = new JPanel();
        scrollPane1 = new JScrollPane();
        txtHTML = new JTextPane();
        panel2 = new JPanel();
        btnPrint = new JButton();

        //======== this ========
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                thisComponentResized(e);
            }
        });
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== splitPane1 ========
        {
            splitPane1.setOneTouchExpandable(true);

            //======== tabKat ========
            {
                tabKat.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
                tabKat.setShowIconsOnTab(false);
                tabKat.setSelectedTabFont(new Font("Arial", Font.BOLD, 14));
                tabKat.setFont(new Font("Arial", Font.PLAIN, 14));
                tabKat.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        tabKatStateChanged(e);
                    }
                });
            }
            splitPane1.setLeftComponent(tabKat);

            //======== panel1 ========
            {
                panel1.setLayout(new BorderLayout());

                //======== scrollPane1 ========
                {

                    //---- txtHTML ----
                    txtHTML.setEditable(false);
                    txtHTML.setContentType("text/html");
                    txtHTML.addPropertyChangeListener("text", new PropertyChangeListener() {
                        @Override
                        public void propertyChange(PropertyChangeEvent e) {
                            txtHTMLPropertyChange(e);
                        }
                    });
                    scrollPane1.setViewportView(txtHTML);
                }
                panel1.add(scrollPane1, BorderLayout.CENTER);

                //======== panel2 ========
                {
                    panel2.setLayout(new BoxLayout(panel2, BoxLayout.LINE_AXIS));

                    //---- btnPrint ----
                    btnPrint.setText(null);
                    btnPrint.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/printer.png")));
                    btnPrint.setBorderPainted(false);
                    btnPrint.setContentAreaFilled(false);
                    btnPrint.setBorder(null);
                    btnPrint.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnPrintActionPerformed(e);
                        }
                    });
                    panel2.add(btnPrint);
                }
                panel1.add(panel2, BorderLayout.SOUTH);
            }
            splitPane1.setRightComponent(panel1);
        }
        add(splitPane1);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JSplitPane splitPane1;
    private JideTabbedPane tabKat;
    private JPanel panel1;
    private JScrollPane scrollPane1;
    private JTextPane txtHTML;
    private JPanel panel2;
    private JButton btnPrint;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
