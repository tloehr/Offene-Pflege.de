/*
 * Created by JFormDesigner on Fri Jun 22 12:26:53 CEST 2012
 */

package op.care.info;

import java.awt.event.*;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideTabbedPane;
import entity.Bewohner;
import entity.BewohnerTools;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
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
    public final Icon icon22view = new ImageIcon(getClass().getResource("/artwork/22x22/bw/viewmag1.png"));
    public final Icon icon22viewPressed = new ImageIcon(getClass().getResource("/artwork/22x22/bw/viewmag1-selected.png"));
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

    private JToggleButton tbEmpty;


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
                    initPhase = false;
                    OPDE.getDisplayManager().setProgressBarMessage(null);
                    OPDE.getMainframe().setBlocked(false);
                }
            };
            worker.execute();

        } else {
            kategorien = BWInfoKatTools.getKategorien();

            for (BWInfoKat kat : kategorien) {
                tabKat.addTab(kat.getBezeichnung(), new JScrollPane(createCollapsiblePanesFor(kat)));
                txtHTML.setText(null);
            }
        }
        initPhase = false;

    }


    private CollapsiblePanes createCollapsiblePanesFor(BWInfoKat kat) {
        CollapsiblePanes cpane = new CollapsiblePanes();
        try {
            cpane.setLayout(new JideBoxLayout(cpane, JideBoxLayout.Y_AXIS));

            if (!bwinfotypen.containsKey(kat)) {
                bwinfotypen.put(kat, BWInfoTypTools.findByKategorie(kat));
            }

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

        panelFilter.setContentPane(labelPanel);

        return panelFilter;
    }

    private String getHyperlinkButtonTextForPanelHead(BWInfoTyp typ) {
        String result = "";
        if (!bwinfos.get(typ).isEmpty()) {
            BWInfo ersterBWInfo = bwinfos.get(typ).get(0);
            result += "<font " + (ersterBWInfo.isAbgesetzt() ? SYSConst.html_lightslategrey : "color=\"BLACK\"") + ">";
            result += ersterBWInfo.isAbgesetzt() ? "&rarr; " + DateFormat.getDateInstance().format(ersterBWInfo.getBis()) + " " : DateFormat.getDateInstance().format(ersterBWInfo.getVon()) + (ersterBWInfo.isSingleIncident() ? " " : " &rarr; ");
            result += "<b>" + typ.getBWInfoKurz() + "</b>: " + (ersterBWInfo.isAbgesetzt() ? "<i>" + OPDE.lang.getString("misc.msg.currentlynoentry") + "</i>" : SYSTools.getHTMLSubstring(ersterBWInfo.getHtml(), MAX_HTML_LENGTH));
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


    private CollapsiblePane createPanelFor(BWInfoTyp typ) {

        //TODO: By NOCONSTRAINTS MUSS FÜR JEDEN INFO EINE PANEL ANGELEGT WERDEN.

        CollapsiblePane panel00 = new CollapsiblePane();
        try {

            final BWInfo aktuellerBWInfo = bwinfos.get(typ).isEmpty() ? null : bwinfos.get(typ).get(0);
            final BWInfoTyp mytyp = typ;

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
                    if (aktuellerBWInfo != null) {
                        setAllViewButtonsOff();
                        txtHTML.setText(SYSTools.toHTML(BWInfoTools.getHTML(aktuellerBWInfo) + getUserInfoAsHTML(aktuellerBWInfo)));
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
                    if (aktuellerBWInfo == null) {
                        mybwinfo = new BWInfo(mytyp, bewohner);
                    } else {
                        mybwinfo = aktuellerBWInfo.clone();
                    }
                    new DlgInfo(mybwinfo, new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {
                                BWInfo newinfo = (BWInfo) o;
                                if (aktuellerBWInfo != null) {
                                    aktuellerBWInfo.setBis(SYSCalendar.addField(newinfo.getVon(), -1, GregorianCalendar.SECOND));
                                    aktuellerBWInfo.setAbgesetztDurch(newinfo.getAngesetztDurch());
                                }
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    if (aktuellerBWInfo != null) {
                                        em.merge(aktuellerBWInfo);
                                    }
                                    newinfo.setHtml(BWInfoTools.getContentAsHTML(newinfo));
                                    em.merge(newinfo);
                                    em.getTransaction().commit();
                                    int i = tabKat.getSelectedIndex();
                                    tabKat.removeTabAt(i);
                                    tabKat.insertTab("test", null, createCollapsiblePanesFor(newinfo.getBwinfotyp().getBwInfokat()), null, i);
                                    tabKat.repaint();
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

            if (aktuellerBWInfo != null) {

                JToggleButton btnView = new JToggleButton(icon22view);
                btnView.setSelectedIcon(icon22viewPressed);
                btnView.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnView.setContentAreaFilled(false);
                btnView.setBorder(null);

                bwinfo4html.put(aktuellerBWInfo, btnView);
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

            panel00.setTitleLabelComponent(titlePanel00);
            panel00.setSlidingDirection(SwingConstants.SOUTH);
            panel00.setStyle(CollapsiblePane.TREE_STYLE);
            panel00.setHorizontalAlignment(SwingConstants.LEADING);

            panel00.setEmphasized(bwinfos.get(typ).isEmpty());
            panel00.setCollapsible(bwinfos.get(typ).size() > 1);
            BWInfo ersterBWInfo = bwinfos.get(typ).isEmpty() ? null : bwinfos.get(typ).get(0);
            JPanel labelPanel = new JPanel();
            labelPanel.setLayout(new VerticalLayout());

            if (bwinfos.get(typ).size() > 1) {
                int startwert = ersterBWInfo.isAbgesetzt() ? 0 : 1; // Dann muss der ja noch unten dabei geschrieben werden.
                for (int infonum = startwert; infonum < bwinfos.get(typ).size(); infonum++) {
                    final BWInfo innerBWInfo = bwinfos.get(typ).get(infonum);
                    JideButton button = GUITools.createHyperlinkButton(getHyperlinkButtonTextForPanelContent(innerBWInfo), null, null);
                    button.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());
                    button.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            setAllViewButtonsOff();
                            txtHTML.setText(SYSTools.toHTMLForScreen(BWInfoTools.getHTML(innerBWInfo)));
                        }
                    });
                    labelPanel.add(button);
                }
            }

            panel00.setContentPane(labelPanel);
            panel00.setCollapsed(true);

            panel00.setVisible(tbEmpty.isSelected() || aktuellerBWInfo != null);

        } catch (PropertyVetoException e) {
            OPDE.error(e);
        } catch (Exception e) {
            OPDE.fatal(e);
        }


        return panel00;
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
