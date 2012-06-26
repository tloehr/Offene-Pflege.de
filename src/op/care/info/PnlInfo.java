/*
 * Created by JFormDesigner on Fri Jun 22 12:26:53 CEST 2012
 */

package op.care.info;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideTabbedPane;
import entity.Bewohner;
import entity.BewohnerTools;
import entity.info.*;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.GUITools;
import op.tools.NursingRecordsPanel;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.jdesktop.swingx.VerticalLayout;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;

/**
 * @author Torsten Löhr
 */
public class PnlInfo extends NursingRecordsPanel {
    public static final String internalClassID = "nursingrecords.info";
    private JPopupMenu menu;
    private Bewohner bewohner;
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;

    private HashMap<BWInfoTyp, CollapsiblePane> panelmap;
    private HashMap<BWInfoKat, List<BWInfoTyp>> bwinfotypen;
    private HashMap<BWInfoTyp, List<BWInfo>> bwinfos;

    public PnlInfo(Bewohner bewohner, JScrollPane jspSearch) {
        this.jspSearch = jspSearch;
        this.bewohner = bewohner;
        initComponents();
        initPanel();
    }

    private void initPanel() {
        prepareSearchArea();
        bwinfotypen = new HashMap<BWInfoKat, List<BWInfoTyp>>();
        bwinfos = new HashMap<BWInfoTyp, List<BWInfo>>();

        panelmap = new HashMap<BWInfoTyp, CollapsiblePane>();
        change2Bewohner(bewohner);
    }

    @Override
    public void change2Bewohner(Bewohner bewohner) {
        this.bewohner = bewohner;
        OPDE.getDisplayManager().setMainMessage(BewohnerTools.getBWLabelText(bewohner));
        reloadDisplay();
    }


    private void reloadDisplay() {


        OPDE.getMainframe().setBlocked(true);
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));
        tabKat.removeAll();

        SwingWorker worker = new SwingWorker() {
            TableModel model;

            @Override
            protected Object doInBackground() throws Exception {
                int progress = 0;
                List<BWInfoKat> kategorien = BWInfoKatTools.getKategorien();
                for (BWInfoKat kat : kategorien) {
                    progress++;
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, kategorien.size()));
                    CollapsiblePanes cpane = new CollapsiblePanes();
                    cpane.setLayout(new JideBoxLayout(cpane, JideBoxLayout.Y_AXIS));


                    if (!bwinfotypen.containsKey(kat)) {
                        bwinfotypen.put(kat, BWInfoTypTools.findByKategorie(kat));
                    }

                    bwinfos.clear();


                    for (BWInfoTyp typ : bwinfotypen.get(kat)) {

                        bwinfos.put(typ, BWInfoTools.findByBewohnerUndTyp(bewohner, typ));
                        CollapsiblePane panel = createPanelFor(typ);
                        cpane.add(panel);
                        panelmap.put(typ, panel);
                    }
                    cpane.addExpansion();
                    tabKat.addTab(kat.getBezeichnung(), new JScrollPane(cpane));

                }
                return null;
            }

            @Override
            protected void done() {
                OPDE.getDisplayManager().setProgressBarMessage(null);
                OPDE.getMainframe().setBlocked(false);
            }
        };
        worker.execute();


    }

    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);
//        searchPanes.add(addCommands());
//        searchPanes.add(addFilters());
        searchPanes.addExpansion();
    }


    private String getHyperlinkButtonTextForPanelHead(BWInfoTyp typ) {
        String result = "";
        if (!bwinfos.get(typ).isEmpty()) {
            BWInfo ersterBWInfo = bwinfos.get(typ).get(0);
            result += "<font " + (ersterBWInfo.isAbgesetzt() ? SYSConst.html_lightslategrey : "color=\"BLACK\"") + ">";
            result += ersterBWInfo.isAbgesetzt() ? "&rarr; " + DateFormat.getDateInstance().format(ersterBWInfo.getBis()) + " " : DateFormat.getDateInstance().format(ersterBWInfo.getVon()) + " &rarr; ";
            result += typ.getBWInfoKurz() + ": " + (ersterBWInfo.isAbgesetzt() ? "<i>" + OPDE.lang.getString("misc.msg.currentlynoentry") + "</i>" : "<b>" + SYSTools.getHTMLSubstring(ersterBWInfo.getHtml(), 200) + "</b>");
            result += "</font>";
        } else {
            result = typ.getBWInfoKurz() + ": <i>" + OPDE.lang.getString("misc.msg.noentryyet") + "<i>";
        }
        return SYSTools.toHTMLForScreen(result);
    }

    private String getHyperlinkButtonTextForPanelContent(BWInfo bwinfo) {
        String result = "";

        result += DateFormat.getDateInstance().format(bwinfo.getVon()) + " &rarr; " + DateFormat.getDateInstance().format(bwinfo.getBis());
        result += ": ";
        result += SYSTools.getHTMLSubstring(bwinfo.getHtml(), 200);

        return SYSTools.toHTMLForScreen(result);
    }


    private CollapsiblePane createPanelFor(BWInfoTyp typ) {

        CollapsiblePane panel00 = new CollapsiblePane();
        JPanel titlePanel00 = new JPanel();
        titlePanel00.setLayout(new BoxLayout(titlePanel00, BoxLayout.LINE_AXIS));

        JPanel titlePanel00left = new JPanel();
        titlePanel00left.setLayout(new BoxLayout(titlePanel00left, BoxLayout.LINE_AXIS));

        JideButton title = GUITools.createHyperlinkButton(getHyperlinkButtonTextForPanelHead(typ), null, null);
        title.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlePanel00left.add(title);

        JPanel titlePanel00right = new JPanel();
        titlePanel00right.setLayout(new BoxLayout(titlePanel00right, BoxLayout.LINE_AXIS));

        JButton btn0 = new JButton(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
        btn0.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add-pressed.png.png")));
        btn0.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JButton btn1 = new JButton(new ImageIcon(getClass().getResource("/artwork/22x22/bw/kspread.png")));
        btn1.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JButton btn2 = new JButton(new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_end.png")));
        btn2.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JButton btn3 = new JButton(new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_stop.png")));
        btn3.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JToggleButton btn4 = new JToggleButton(new ImageIcon(getClass().getResource("/artwork/22x22/bw/viewmag1.png")));
        btn4.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/viewmag-selected.png")));
        btn4.setAlignmentX(Component.RIGHT_ALIGNMENT);

        btn0.setContentAreaFilled(false);
        btn0.setBorder(null);
        btn1.setContentAreaFilled(false);
        btn1.setBorder(null);
        btn2.setContentAreaFilled(false);
        btn2.setBorder(null);
        btn3.setContentAreaFilled(false);
        btn3.setBorder(null);
        btn4.setContentAreaFilled(false);
        btn4.setBorder(null);

        titlePanel00right.add(btn4);
        titlePanel00right.add(btn0);
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
                JideButton button = GUITools.createHyperlinkButton(getHyperlinkButtonTextForPanelContent(bwinfos.get(typ).get(infonum)), null, null);
                button.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());
                labelPanel.add(button);
            }
        }

        panel00.setContentPane(labelPanel);

        try {
            panel00.setCollapsed(true);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
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

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        tabKat = new JideTabbedPane();
        scrollPane1 = new JScrollPane();
        textArea1 = new JTextPane();

        //======== this ========
        setLayout(new FormLayout(
                "left:default:grow, $lcgap, pref:grow",
                "fill:default:grow"));

        //======== tabKat ========
        {
            tabKat.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
            tabKat.setShowIconsOnTab(false);
        }
        add(tabKat, CC.xy(1, 1, CC.FILL, CC.FILL));

        //======== scrollPane1 ========
        {

            //---- textArea1 ----
            textArea1.setContentType("text/html");
            scrollPane1.setViewportView(textArea1);
        }
        add(scrollPane1, CC.xy(3, 1, CC.FILL, CC.FILL));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JideTabbedPane tabKat;
    private JScrollPane scrollPane1;
    private JTextPane textArea1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
