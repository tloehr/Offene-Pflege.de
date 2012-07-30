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

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import entity.Bewohner;
import entity.BewohnerTools;
import entity.files.SYSFilesTools;
import entity.info.BWInfoTools;
import entity.system.SYSPropsTools;
import op.OPDE;
import op.care.berichte.PnlBerichte;
import op.threads.DisplayMessage;
import op.tools.GUITools;
import op.tools.NursingRecordsPanel;
import op.tools.SYSTools;
import org.jdesktop.swingx.VerticalLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;

/**
 * @author tloehr
 */
public class PnlBWUebersicht extends NursingRecordsPanel {
    public static final String internalClassID = "nursingrecords.overview";

    private Bewohner bewohner;
    private CollapsiblePanes searchPanes;
    private JScrollPane jspSearch;
    private JToggleButton tbMedi, tbBilanz, tbBerichte;
    private ItemListener itemListener;
    private MouseAdapter mouseAdapter;
    private boolean initPhase = false;

    /**
     * Creates new form PnlBWUebersicht
     */
    public PnlBWUebersicht(Bewohner bewohner, JScrollPane jspSearch) {
        initComponents();
        this.jspSearch = jspSearch;
        initPanel();
        prepareSearchArea();
        change2Bewohner(bewohner);
    }

    private void initPanel() {
        txtUebersicht.setContentType("text/html");

        itemListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                reloadDisplay();
            }
        };

        mouseAdapter = GUITools.getHyperlinkStyleMouseAdapter();

    }

    @Override
    public void cleanup() {
        SYSTools.unregisterListeners(this);
    }

    @Override
    public void reload() {
        reloadDisplay();
    }


    @Override
    public void change2Bewohner(Bewohner bewohner) {
        this.bewohner = bewohner;
        GUITools.setBWDisplay(bewohner);
        reloadDisplay();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jspHTML = new JScrollPane();
        txtUebersicht = new JTextPane();

        //======== this ========
        setLayout(new CardLayout());

        //======== jspHTML ========
        {

            //---- txtUebersicht ----
            txtUebersicht.setEditable(false);
            jspHTML.setViewportView(txtUebersicht);
        }
        add(jspHTML, "card1");
    }// </editor-fold>//GEN-END:initComponents

    public void reloadDisplay() {
        initPhase = true;
        OPDE.getMainframe().setBlocked(true);
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));

        SwingWorker worker = new SwingWorker() {
            String html = "";

            @Override
            protected Object doInBackground() throws Exception {
                html = SYSTools.toHTML(BWInfoTools.getUeberleitung(bewohner, false, tbMedi.isSelected(), tbBilanz.isSelected(), tbBerichte.isSelected(), true, false, false, true));
                return null;
            }

            @Override
            protected void done() {
                txtUebersicht.setText(html);
                txtUebersicht.repaint();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        jspHTML.getViewport().setViewPosition(new Point(0, 0));
                    }
                });
                initPhase = false;
                OPDE.getDisplayManager().setProgressBarMessage(null);
                OPDE.getMainframe().setBlocked(false);
            }
        };
        worker.execute();
    }

    private CollapsiblePane addFilters() {

        JPanel labelPanel = new JPanel();
        labelPanel.setBackground(Color.WHITE);
        labelPanel.setLayout(new VerticalLayout(5));

        CollapsiblePane panelFilter = new CollapsiblePane(OPDE.lang.getString("misc.msg.Filter"));
        panelFilter.setStyle(CollapsiblePane.PLAIN_STYLE);
        panelFilter.setCollapsible(false);

        tbMedi = GUITools.getNiceToggleButton(OPDE.lang.getString("misc.msg.medication"));
        tbMedi.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (initPhase) return;
                SYSPropsTools.storeState(internalClassID + ":tbMedi", tbMedi);
                reloadDisplay();
            }
        });
        tbMedi.setHorizontalAlignment(SwingConstants.LEFT);
        labelPanel.add(tbMedi);
        SYSPropsTools.restoreState(internalClassID + ":tbMedi", tbMedi);

        tbBerichte = GUITools.getNiceToggleButton(OPDE.lang.getString(PnlBerichte.internalClassID));
        tbBerichte.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (initPhase) return;
                SYSPropsTools.storeState(internalClassID + ":tbBerichte", tbBerichte);
                reloadDisplay();
            }
        });
        tbBerichte.setHorizontalAlignment(SwingConstants.LEFT);
        labelPanel.add(tbBerichte);
        SYSPropsTools.restoreState(internalClassID + ":tbBerichte", tbBerichte);

        tbBilanz = GUITools.getNiceToggleButton(OPDE.lang.getString("misc.msg.liquid.result"));
        tbBilanz.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (initPhase) return;
                SYSPropsTools.storeState(internalClassID + ":tbBilanz", tbBilanz);
                reloadDisplay();
            }
        });
        tbBilanz.setHorizontalAlignment(SwingConstants.LEFT);
        labelPanel.add(tbBilanz);
        SYSPropsTools.restoreState(internalClassID + ":tbBilanz", tbBilanz);

//        tbBWInfo = GUITools.getNiceToggleButton(OPDE.lang.getString(PnlInfo.internalClassID));
//        tbBWInfo.addItemListener(new ItemListener() {
//            @Override
//            public void itemStateChanged(ItemEvent e) {
//                if (initPhase) return;
//                SYSPropsTools.storeState(internalClassID + ":tbBWInfo", tbBWInfo);
//                reloadDisplay();
//            }
//        });
//        tbBilanz.setHorizontalAlignment(SwingConstants.LEFT);
//        labelPanel.add(tbBWInfo);
//        SYSPropsTools.restoreState(internalClassID + ":tbBWInfo", tbBWInfo);

        panelFilter.setContentPane(labelPanel);

        return panelFilter;


    }

    private void prepareSearchArea() {
        initPhase = true;
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));


        CollapsiblePane searchPane = new CollapsiblePane(OPDE.lang.getString(internalClassID));
        searchPane.setSlidingDirection(SwingConstants.SOUTH);
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);

        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout(5));
        mypanel.setBackground(Color.WHITE);

        JideButton printButton = GUITools.createHyperlinkButton("Drucken", new ImageIcon(getClass().getResource("/artwork/22x22/bw/printer.png")), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SYSFilesTools.print(SYSTools.htmlUmlautConversion(BWInfoTools.getUeberleitung(bewohner, true, false, tbMedi.isSelected(), tbBilanz.isSelected(), tbBerichte.isSelected(), true, false, true)), true);
            }
        });
        mypanel.add(printButton);


        searchPane.setContentPane(mypanel);
        searchPanes.add(searchPane);
        searchPanes.add(addFilters());
        searchPanes.addExpansion();

        jspSearch.setViewportView(searchPanes);

        initPhase = false;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspHTML;
    private JTextPane txtUebersicht;
    // End of variables declaration//GEN-END:variables
}
