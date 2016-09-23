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
package op.care;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import entity.EntityTools;
import entity.files.SYSFilesTools;
import entity.info.ResInfoTools;
import entity.info.Resident;
import op.OPDE;
import op.care.reports.PnlReport;
import op.threads.DisplayMessage;
import gui.GUITools;
import op.tools.NursingRecordsPanel;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.jdesktop.swingx.VerticalLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;

/**
 * @author tloehr
 */
public class PnlResOverview extends NursingRecordsPanel {


    private Resident resident;
    private CollapsiblePanes searchPanes;
    private JScrollPane jspSearch;
    private JToggleButton tbMedi, tbBilanz, tbBerichte;
    private ItemListener itemListener;
    private MouseAdapter mouseAdapter;
    private boolean initPhase = false;

    @Override
    public String getInternalClassID() {
        return internalClassID;
    }

    /**
     * Creates new form PnlResOverview
     */
    public PnlResOverview(Resident resident, JScrollPane jspSearch) {
        super("nursingrecords.overview");
        initComponents();
        this.jspSearch = jspSearch;
        initPanel();
        prepareSearchArea();
        switchResident(resident);
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
    public void switchResident(Resident res) {
        this.resident = EntityTools.find(Resident.class, res.getRID());
        GUITools.setResidentDisplay(resident);
        reloadDisplay();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the PrinterForm Editor.
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

        final boolean withworker = true;
        if (withworker) {
            initPhase = true;

            OPDE.getMainframe().setBlocked(true);
            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), -1, 100));

            SwingWorker worker = new SwingWorker() {
                String html = "";

                @Override
                protected Object doInBackground() throws Exception {
                    html = SYSTools.toHTML(ResInfoTools.getTXReport(resident, false, tbMedi.isSelected(), tbBilanz.isSelected(), tbBerichte.isSelected(), true, false, false, true, false));
                    return null;
                }

                @Override
                protected void done() {
                    txtUebersicht.setText(html);
                    txtUebersicht.repaint();
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            GUITools.scroll2show(jspHTML, 0, null);
                        }
                    });
                    initPhase = false;
                    OPDE.getDisplayManager().setProgressBarMessage(null);
                    OPDE.getMainframe().setBlocked(false);
                }
            };
            worker.execute();

        } else {
            initPhase = true;
            OPDE.getMainframe().setBlocked(true);
            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), -1, 100));

//            txtUebersicht.repaint();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    String html = SYSTools.toHTML(ResInfoTools.getTXReport(resident, false, tbMedi.isSelected(), tbBilanz.isSelected(), tbBerichte.isSelected(), true, false, false, true, false));
                    txtUebersicht.setText(html);
//                    jspHTML.getVerticalScrollBar().setValue(0);

                    GUITools.scroll2show(jspHTML, 0, null);

                    initPhase = false;
                    OPDE.getDisplayManager().setProgressBarMessage(null);
                    OPDE.getMainframe().setBlocked(false);
                }
            });


        }


        OPDE.getMainframe().setBlocked(true);
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), -1, 100));

        SwingWorker worker = new SwingWorker() {
            String html = "";

            @Override
            protected Object doInBackground() throws Exception {
                html = SYSTools.toHTML(ResInfoTools.getTXReport(resident, false, tbMedi.isSelected(), tbBilanz.isSelected(), tbBerichte.isSelected(), true, false, false, true, false));
                return null;
            }

            @Override
            protected void done() {
                txtUebersicht.setText(html);
                txtUebersicht.repaint();
                jspHTML.getVerticalScrollBar().setValue(0);
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
        labelPanel.setLayout(new VerticalLayout(3));

        CollapsiblePane panelFilter = new CollapsiblePane(SYSTools.xx("misc.msg.Filter"));
        panelFilter.setStyle(CollapsiblePane.PLAIN_STYLE);
        panelFilter.setCollapsible(false);

        tbMedi = GUITools.getNiceToggleButton(SYSTools.xx("nursingrecords.prescription"));
        tbMedi.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (initPhase) return;
//                SYSPropsTools.storeState(internalClassID + ":tbMedi", tbMedi);
                reloadDisplay();
            }
        });
        tbMedi.setHorizontalAlignment(SwingConstants.LEFT);
        labelPanel.add(tbMedi);
//        SYSPropsTools.restoreState(internalClassID + ":tbMedi", tbMedi);

        tbBerichte = GUITools.getNiceToggleButton(SYSTools.xx("nursingrecords.reports"));
        tbBerichte.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (initPhase) return;
//                SYSPropsTools.storeState(internalClassID + ":tbBerichte", tbBerichte);
                reloadDisplay();
            }
        });
        tbBerichte.setHorizontalAlignment(SwingConstants.LEFT);
        labelPanel.add(tbBerichte);
//        SYSPropsTools.restoreState(internalClassID + ":tbBerichte", tbBerichte);

        tbBilanz = GUITools.getNiceToggleButton(SYSTools.xx("misc.msg.liquid.result"));
        tbBilanz.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (initPhase) return;
//                SYSPropsTools.storeState(internalClassID + ":tbBilanz", tbBilanz);
                reloadDisplay();
            }
        });
        tbBilanz.setHorizontalAlignment(SwingConstants.LEFT);
        labelPanel.add(tbBilanz);
//        SYSPropsTools.restoreState(internalClassID + ":tbBilanz", tbBilanz);

//        tbBWInfo = GUITools.getNiceToggleButton(SYSTools.xx(PnlInfo.internalClassID));
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


        CollapsiblePane searchPane = new CollapsiblePane(SYSTools.xx(internalClassID));
        searchPane.setSlidingDirection(SwingConstants.SOUTH);
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);

        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout(3));
        mypanel.setBackground(Color.WHITE);

        JideButton printButton = GUITools.createHyperlinkButton("Drucken", SYSConst.icon22print2, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SYSFilesTools.print(ResInfoTools.getTXReport(resident, true, false, tbMedi.isSelected(), tbBilanz.isSelected(), tbBerichte.isSelected(), true, false, true, true), true);
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
