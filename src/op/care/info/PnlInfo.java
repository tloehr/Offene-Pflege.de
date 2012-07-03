/*
 * Created by JFormDesigner on Fri Jun 22 12:26:53 CEST 2012
 */

package op.care.info;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideTabbedPane;
import entity.Bewohner;
import entity.BewohnerTools;
import entity.EntityTools;
import entity.files.SYSFilesTools;
import entity.info.*;
import entity.system.SYSPropsTools;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.VerticalLayout;

import javax.persistence.EntityManager;
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

    public final Icon icon22add = new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png"));
    public final Icon icon22addPressed = new ImageIcon(getClass().getResource("/artwork/22x22/bw/add-pressed.png"));
    public final Icon icon22edit = new ImageIcon(getClass().getResource("/artwork/22x22/bw/kspread.png"));
    public final Icon icon22editPressed = new ImageIcon(getClass().getResource("/artwork/22x22/bw/kspread_pressed.png"));
    public final Icon icon22gotoEnd = new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_end.png"));
    public final Icon icon22gotoEndPressed = new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_end_pressed.png"));
    public final Icon icon22stop = new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_stop.png"));
    public final Icon icon22stopPressed = new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_stop_pressed.png"));
    public final Icon icon22view = new ImageIcon(getClass().getResource("/artwork/22x22/bw/viewmag.png"));
    public final Icon icon22viewPressed = new ImageIcon(getClass().getResource("/artwork/22x22/bw/viewmag-selected.png"));
    public final Icon icon22changePeriod = new ImageIcon(getClass().getResource("/artwork/22x22/bw/reload_page.png"));
    public final Icon icon22changePeriodPressed = new ImageIcon(getClass().getResource("/artwork/22x22/bw/reload_page_pressed.png"));

    public final Icon icon16bysecond = new ImageIcon(getClass().getResource("/artwork/16x16/bw/bysecond.png"));
    public final Icon icon16byday = new ImageIcon(getClass().getResource("/artwork/16x16/bw/bysecond.png"));
    public final Icon icon16pit = new ImageIcon(getClass().getResource("/artwork/16x16/bw/pointintime.png"));

    private final int MAX_HTML_LENGTH = 80;
    private JPopupMenu menu;
    private Bewohner bewohner;
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private boolean[] infolist;

    private HashMap<BWInfoTyp, CollapsiblePane> panelmap;
    private HashMap<BWInfoKat, List<BWInfoTyp>> bwinfotypen;
    private HashMap<BWInfo, JToggleButton> bwinfo4html;
    private HashMap<BWInfoTyp, List<BWInfo>> bwinfos;
    private List<BWInfoKat> kategorien;

    private JToggleButton tbEmpty, tbInactive;


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
                splitPane1.addPropertyChangeListener("dividerLocation", new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                        SYSPropsTools.storeProp(internalClassID + ":splitPane1DividerLocation", SYSTools.getDividerInRelativePosition(splitPane1).toString(), OPDE.getLogin().getUser());
                    }
                });
            }
        });

        change2Bewohner(bewohner);


    }

    @Override
    public void change2Bewohner(Bewohner bewohner) {
        this.bewohner = bewohner;
        OPDE.getDisplayManager().setMainMessage(BewohnerTools.getBWLabelText(bewohner));
        reloadDisplay();
    }

    private void setAllViewButtonsOff() {
        if (tabKat.getSelectedIndex() < 0) return;
        initPhase = true;
        BWInfoKat aktuelleKategorie = kategorien.get(tabKat.getSelectedIndex());
        for (BWInfoTyp typ : bwinfotypen.get(aktuelleKategorie)) {
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
                        OPDE.debug(info.getHtml());
                        html += BWInfoTools.getHTML(info);
                    }
                }
            }
        }

        return html;
    }

    private void reloadDisplay() {
        initPhase = true;

        final boolean withworker = true;
        if (withworker) {

            OPDE.getMainframe().setBlocked(true);
            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));

            tabKat.removeAll();
            bwinfos.clear();
            panelmap.clear();

            SwingWorker worker = new SwingWorker() {
                TableModel model;

                @Override
                protected Object doInBackground() throws Exception {
                    try {
                        int progress = 0;
                        kategorien = BWInfoKatTools.getKategorien();

                        for (final BWInfoKat kat : kategorien) {
                            progress++;
                            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, kategorien.size()));
                            tabKat.addTab(kat.getBezeichnung(), new JScrollPane(createCollapsiblePanesFor(kat)));
//                            SwingUtilities.invokeLater(new Runnable() {
//                                @Override
//                                public void run() {
//
//                                }
//                            });
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
//                    SYSPropsTools.storeProp(internalClassID + ":tabKatSelectedIndex", Integer.toString(tabKat.getSelectedIndex()), OPDE.getLogin().getUser());

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
        }
        initPhase = false;

    }


    private CollapsiblePanes createCollapsiblePanesFor(BWInfoKat kat) {
        CollapsiblePanes cpane = new CollapsiblePanes();
        try {
            cpane.setLayout(new JideBoxLayout(cpane, JideBoxLayout.Y_AXIS));

//            if (!bwinfotypen.containsKey(kat)) {
            bwinfotypen.put(kat, BWInfoTypTools.findByKategorie(kat));
//            }

            for (BWInfoTyp typ : bwinfotypen.get(kat)) {
                bwinfos.put(typ, BWInfoTools.findByBewohnerUndTyp(bewohner, typ));
                CollapsiblePane panel = createPanelFor(typ);
                cpane.add(panel);
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
            panelmap.get(typ).setVisible(tbEmpty.isSelected() || !bwinfos.get(typ).isEmpty());
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

        CollapsiblePane searchPane = new CollapsiblePane(OPDE.lang.getString(internalClassID));
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);

        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.PRINT)) {
            JideButton printButton = GUITools.createHyperlinkButton(OPDE.lang.getString("misc.commands.print"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/printer.png")), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    SYSFilesTools.print("<h1 id=\"fonth1\" >" + OPDE.lang.getString(internalClassID) + ": " + BewohnerTools.getBWLabelText(bewohner) + "</h1>" + getHTML(), true);
                }
            });
            mypanel.add(printButton);
        }

        searchPane.setContentPane(mypanel);
        searchPanes.add(searchPane);


        searchPane.setContentPane(mypanel);
        return searchPane;
    }

    private CollapsiblePane addFilters() {
        JPanel labelPanel = new JPanel();
        labelPanel.setBackground(Color.WHITE);
        labelPanel.setLayout(new VerticalLayout(5));

        CollapsiblePane panelFilter = new CollapsiblePane(OPDE.lang.getString("misc.msg.Filter"));
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

        result += DateFormat.getDateInstance().format(bwinfo.getVon()) + (bwinfo.isSingleIncident() ? " " : " &rarr; " + DateFormat.getDateInstance().format(bwinfo.getBis()));
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

        final CollapsiblePane panelForBWInfoTyp = new CollapsiblePane();
        try {

            final BWInfo ersterBWInfo = bwinfos.get(typ).isEmpty() ? null : bwinfos.get(typ).get(0);
            final boolean shallBeCollapsible = !bwinfos.get(typ).isEmpty() && (bwinfos.get(typ).size() > 1 || ersterBWInfo.isAbgesetzt() || typ.getIntervalMode() == BWInfoTypTools.MODE_INTERVAL_NOCONSTRAINTS || typ.getIntervalMode() == BWInfoTypTools.MODE_INTERVAL_SINGLE_INCIDENTS);

            JPanel titlePanel00 = new JPanel();
            titlePanel00.setLayout(new BoxLayout(titlePanel00, BoxLayout.LINE_AXIS));

            JPanel titlePanel00left = new JPanel();
            titlePanel00left.setLayout(new BoxLayout(titlePanel00left, BoxLayout.LINE_AXIS));

            JideButton title = GUITools.createHyperlinkButton(getHyperlinkButtonTextForPanelHead(typ), getIcon(typ), null);
            title.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());
            title.setAlignmentX(Component.LEFT_ALIGNMENT);
            title.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (ersterBWInfo != null) {
                        setAllViewButtonsOff();
                        txtHTML.setText(SYSTools.toHTML(BWInfoTools.getHTML(ersterBWInfo) + getUserInfoAsHTML(ersterBWInfo)));
                    }
                }
            });
            titlePanel00left.add(title);


            JPanel titlePanel00right = new JPanel();
            titlePanel00right.setLayout(new BoxLayout(titlePanel00right, BoxLayout.LINE_AXIS));

            JButton btnAdd = new JButton(icon22add);
            btnAdd.setPressedIcon(icon22addPressed);
            btnAdd.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnAdd.setContentAreaFilled(false);
            btnAdd.setBorder(null);
            btnAdd.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    BWInfo mybwinfo;
                    if (ersterBWInfo == null || typ.getIntervalMode() == BWInfoTypTools.MODE_INTERVAL_SINGLE_INCIDENTS) {
                        mybwinfo = new BWInfo(typ, bewohner);
                    } else {
                        mybwinfo = ersterBWInfo.clone();
                    }
                    new DlgInfo(mybwinfo, new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {
                                BWInfo newinfo = (BWInfo) o;
                                if (ersterBWInfo != null) {
                                    ersterBWInfo.setBis(SYSCalendar.addField(newinfo.getVon(), -1, GregorianCalendar.SECOND));
                                    ersterBWInfo.setAbgesetztDurch(newinfo.getAngesetztDurch());
                                }
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    if (ersterBWInfo != null) {
                                        em.merge(ersterBWInfo);
                                    }
                                    newinfo.setHtml(BWInfoTools.getContentAsHTML(newinfo));
                                    em.merge(newinfo);
                                    em.getTransaction().commit();

                                    refreshTabKat(newinfo.getBwinfotyp().getBwInfokat());
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

            JButton btn1 = new JButton(icon22edit);
            btn1.setPressedIcon(icon22editPressed);
            btn1.setAlignmentX(Component.RIGHT_ALIGNMENT);
            JButton btn2 = new JButton(icon22gotoEnd);
            btn2.setPressedIcon(icon22gotoEndPressed);
            btn2.setAlignmentX(Component.RIGHT_ALIGNMENT);
            JButton btn3 = new JButton(icon22stop);
            btn3.setPressedIcon(icon22stopPressed);
            btn3.setAlignmentX(Component.RIGHT_ALIGNMENT);

            if (ersterBWInfo != null && typ.getIntervalMode() != BWInfoTypTools.MODE_INTERVAL_NOCONSTRAINTS) {

                JToggleButton btnView = new JToggleButton(icon22view);
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
                        String html = getHTML();
                        txtHTML.setText(html.isEmpty() ? "<html>&nbsp;</html>" : SYSTools.toHTMLForScreen(html));
                    }
                });

                titlePanel00right.add(btnView);

            }


            btn1.setContentAreaFilled(false);
            btn1.setBorder(null);
            btn2.setContentAreaFilled(false);
            btn2.setBorder(null);
            btn3.setContentAreaFilled(false);
            btn3.setBorder(null);


            titlePanel00right.add(btnAdd);
            titlePanel00right.add(btn1);
            titlePanel00right.add(btn2);
            titlePanel00right.add(btn3);
            titlePanel00left.setOpaque(false);
            titlePanel00right.setOpaque(false);
            titlePanel00.setOpaque(false);

            titlePanel00.add(titlePanel00left);
            titlePanel00.add(titlePanel00right);

            panelForBWInfoTyp.setTitleLabelComponent(titlePanel00);
            panelForBWInfoTyp.setSlidingDirection(SwingConstants.SOUTH);
            panelForBWInfoTyp.setStyle(CollapsiblePane.TREE_STYLE);
            panelForBWInfoTyp.setHorizontalAlignment(SwingConstants.LEADING);

            panelForBWInfoTyp.setEmphasized(bwinfos.get(typ).isEmpty());

            JPanel labelPanel = new JPanel();
            labelPanel.setLayout(new VerticalLayout());


            // Hier wird der Content erzeugt
            if (!bwinfos.get(typ).isEmpty()) {
                // In diesen Fällen steht im Kopf kein BWInfo Eintrag. Daher müssen die alle hier rein geschrieben werden.
                int startwert = ersterBWInfo.isAbgesetzt() || typ.getIntervalMode() == BWInfoTypTools.MODE_INTERVAL_NOCONSTRAINTS || typ.getIntervalMode() == BWInfoTypTools.MODE_INTERVAL_SINGLE_INCIDENTS ? 0 : 1;
//                OPDE.debug("startwert: "+startwert);
                for (int infonum = startwert; infonum < bwinfos.get(typ).size(); infonum++) {


                    final BWInfo innerBWInfo = bwinfos.get(typ).get(infonum);

                    final JideButton contentButton = GUITools.createHyperlinkButton(getHyperlinkButtonTextForPanelContent(innerBWInfo), null, null);
                    contentButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                    contentButton.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());
                    contentButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            setAllViewButtonsOff();
                            txtHTML.setText(SYSTools.toHTMLForScreen(BWInfoTools.getHTML(innerBWInfo)));
                        }
                    });


                    JPanel contentLine = new JPanel();
                    contentLine.setLayout(new BoxLayout(contentLine, BoxLayout.LINE_AXIS));

                    JPanel contentLineRight = new JPanel();
                    contentLineRight.setLayout(new BoxLayout(contentLineRight, BoxLayout.LINE_AXIS));

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
                                PnlUhrzeitDatum pnlZeitpunkt = new PnlUhrzeitDatum(innerBWInfo.getVon());
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
                                            Pair<Date, Date> period = (Pair<Date, Date>) o;
                                            innerBWInfo.setVon(period.getFirst());
                                            innerBWInfo.setBis(period.getSecond());
                                            EntityTools.merge(innerBWInfo);
//                                            contentButton.setText(getHyperlinkButtonTextForPanelContent(innerBWInfo));
                                            refreshTabKat(innerBWInfo.getBwinfotyp().getBwInfokat());
//                                            bwinfos.get(typ).set(f_infonum, innerBWInfo);
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
                            txtHTML.setText(html.isEmpty() ? "<html>&nbsp;</html>" : SYSTools.toHTMLForScreen(html));
                        }
                    });

                    contentLineRight.add(btnView);
                    contentLineRight.add(btnChangePeriod);


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
                    SYSPropsTools.storeBoolean(internalClassID + ":panelCollapsed:" + typ.getBwinftyp(), false, OPDE.getLogin().getUser());
                }

                @Override
                public void paneCollapsed(CollapsiblePaneEvent collapsiblePaneEvent) {
                    SYSPropsTools.storeBoolean(internalClassID + ":panelCollapsed:" + typ.getBwinftyp(), true, OPDE.getLogin().getUser());
                }
            });
            if (shallBeCollapsible) {
                panelForBWInfoTyp.setCollapsed(SYSPropsTools.isBooleanTrue(internalClassID + ":panelCollapsed:" + typ.getBwinftyp(), true));
            }
            panelForBWInfoTyp.setVisible((tbEmpty.isSelected() || ersterBWInfo != null) && tbInactive.isSelected() || (ersterBWInfo != null && !ersterBWInfo.isAbgesetzt()));

        } catch (PropertyVetoException e) {
            OPDE.error(e);
        } catch (Exception e) {
            OPDE.fatal(e);
        }


        return panelForBWInfoTyp;
    }

    private void refreshTabKat(BWInfoKat kat) {
        int i = tabKat.getSelectedIndex();
        tabKat.removeTabAt(i);
        tabKat.insertTab(kat.getBezeichnung(), null, createCollapsiblePanesFor(kat), null, i);
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

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        splitPane1 = new JSplitPane();
        tabKat = new JideTabbedPane();
        scrollPane1 = new JScrollPane();
        txtHTML = new JTextPane();

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
            splitPane1.setRightComponent(scrollPane1);
        }
        add(splitPane1);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JSplitPane splitPane1;
    private JideTabbedPane tabKat;
    private JScrollPane scrollPane1;
    private JTextPane txtHTML;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
