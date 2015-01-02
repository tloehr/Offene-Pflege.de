/*
 * OffenePflege
 * Copyright (C) 2011 Torsten Löhr
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
 */
package op.care.sysfiles;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideBoxLayout;
import entity.EntityTools;
import entity.files.SYSFiles;
import entity.files.SYSFilesTools;
import entity.info.Resident;
import op.OPDE;
import op.system.FileDrop;
import op.system.InternalClassACL;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.VerticalLayout;
import tablerenderer.RNDHTML;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

/**
 * @author tloehr
 */
public class PnlFiles extends NursingRecordsPanel {
    public static final String internalClassID = "nursingrecords.files";
    private JPopupMenu menu;
    private Resident resident;
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    //    private RowFilter<TMSYSFiles, Integer> textFilter;
    private TableRowSorter<TMSYSFiles> sorter;
    private TMSYSFiles tmSYSFiles;

    /**
     * Creates new form PnlFiles
     */
    public PnlFiles(Resident resident, JScrollPane jspSearch) {
        initComponents();
        this.jspSearch = jspSearch;
        initPanel();
        switchResident(resident);
    }


    private void initPanel() {
        prepareSearchArea();
    }

    @Override
    public void cleanup() {
        SYSTools.unregisterListeners(menu);
        SYSTools.unregisterListeners(this);
    }

    @Override
    public void switchResident(Resident res) {
        this.resident = EntityTools.find(Resident.class, res.getRID());
        GUITools.setResidentDisplay(resident);
        reloadTable();
    }

    @Override
    public void reload() {
        reloadTable();
    }

    @Override
    public String getInternalClassID() {
        return internalClassID;
    }

    void reloadTable() {
        HashSet<SYSFiles> filesSet = new HashSet<>();

        EntityManager em = OPDE.createEM();

        Query query0 = em.createQuery("SELECT s FROM SYSFiles s JOIN s.residentAssignCollection res WHERE res.resident = :resident");
        query0.setParameter("resident", resident);
        filesSet.addAll(query0.getResultList());

        Query query1 = em.createQuery("SELECT s FROM SYSFiles s JOIN s.nrAssignCollection nr WHERE nr.nReport.resident = :resident");
        query1.setParameter("resident", resident);
        filesSet.addAll(query1.getResultList());

        Query query2 = em.createQuery("SELECT s FROM SYSFiles s JOIN s.bwiAssignCollection bwi WHERE bwi.bwinfo.resident = :resident");
        query2.setParameter("resident", resident);
        filesSet.addAll(query2.getResultList());

        Query query3 = em.createQuery("SELECT s FROM SYSFiles s JOIN s.preAssignCollection pre WHERE pre.prescription.resident = :resident");
        query3.setParameter("resident", resident);
        filesSet.addAll(query3.getResultList());

        Query query4 = em.createQuery("SELECT s FROM SYSFiles s JOIN s.valAssignCollection val WHERE val.value.resident = :resident");
        query4.setParameter("resident", resident);
        filesSet.addAll(query4.getResultList());

        Query query5 = em.createQuery("SELECT s FROM SYSFiles s JOIN s.npAssignCollection np WHERE np.nursingProcess.resident = :resident");
        query5.setParameter("resident", resident);
        filesSet.addAll(query5.getResultList());

        em.close();

        ArrayList<SYSFiles> listFiles = new ArrayList<>(filesSet);

        Collections.sort(listFiles);

//        createFilters();
        tmSYSFiles = new TMSYSFiles(listFiles);
        tblFiles.setModel(tmSYSFiles);

        sorter = new TableRowSorter(tmSYSFiles);
        sorter.setSortsOnUpdates(true);
        tblFiles.setRowSorter(sorter);

//        sorter.setComparator(TMSYSFiles.COL_PIT, new Comparator<Date>() {
//            @Override
//            public int compare(Date o1, Date o2) {
//                return o1.compareTo(o2);
//            }
//        });

//        sorter.setRowFilter(textFilter);

        tblFiles.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");
                return super.getTableCellRendererComponent(table, sdf.format((Date) value), isSelected, hasFocus, row, column);
            }
        });
        tblFiles.getColumnModel().getColumn(1).setCellRenderer(new RNDHTML());
        tblFiles.getColumnModel().getColumn(2).setCellRenderer(new RNDHTML());
        tblFiles.getColumnModel().getColumn(3).setCellRenderer(new RNDHTML());

        tblFiles.getColumnModel().getColumn(0).setHeaderValue(SYSTools.xx("nursingrecords.files.tabheader1"));
        tblFiles.getColumnModel().getColumn(1).setHeaderValue(SYSTools.xx("nursingrecords.files.tabheader2"));
        tblFiles.getColumnModel().getColumn(2).setHeaderValue(SYSTools.xx("nursingrecords.files.tabheader3"));
        tblFiles.getColumnModel().getColumn(3).setHeaderValue(SYSTools.xx("nursingrecords.files.tabheader4"));

        jspFiles.dispatchEvent(new ComponentEvent(jspFiles, ComponentEvent.COMPONENT_RESIZED));
    }


    private void createFilters() {

//        textFilter = new RowFilter<TMSYSFiles, Integer>() {
//            @Override
//            public boolean include(Entry<? extends TMSYSFiles, ? extends Integer> entry) {
//                int row = entry.getIdentifier();
//                SYSFiles sysFile = entry.getModel().getRow(row);
//
//                if (!tbOldStocks.isSelected() && stock.isAusgebucht()) return false;
//
//                if (!treeIngredients.getSelectionModel().isSelectionEmpty() && !treeIngredients.getSelectionModel().getLeadSelectionPath().getLastPathComponent().equals(treeIngredients.getModel().getRoot())) {
//                    for (TreePath path : treeIngredients.getSelectionModel().getSelectionPaths()) {
//                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
//                        if (node.getUserObject() instanceof Recipes) {
//                            if (!RecipeTools.contains((Recipes) node.getUserObject(), stock.getProdukt().getIngTypes())) {
//                                return false;
//                            }
//                        } else if (node.getUserObject() instanceof Ingtypes2Recipes) {
//                            if (!stock.getProdukt().getIngTypes().equals(((Ingtypes2Recipes) node.getUserObject()).getIngType())) {
//                                return false;
//                            }
//                        }
//                    }
//                }
//
//                String textKriterium = searchUnAss.getText().trim().toLowerCase();
//                if (textKriterium.isEmpty()) return true;
//
//                return (stock.getProdukt().getBezeichnung().toLowerCase().indexOf(textKriterium) >= 0 ||
//                        Long.toString(stock.getId()).equals(textKriterium) ||
//                        Tools.catchNull(stock.getProdukt().getGtin()).indexOf(textKriterium) >= 0 ||
//                        stock.getProdukt().getIngTypes().getBezeichnung().toLowerCase().indexOf(textKriterium) >= 0 ||
//                        stock.getProdukt().getIngTypes().getWarengruppe().getBezeichnung().toLowerCase().indexOf(textKriterium) >= 0);
//
//
//            }
//        };

    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the PrinterForm Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlMain = new JPanel();
        jspFiles = new JScrollPane();
        tblFiles = new JTable();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== pnlMain ========
        {
            pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.X_AXIS));

            //======== jspFiles ========
            {
                jspFiles.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        jspFilesComponentResized(e);
                    }
                });

                //---- tblFiles ----
                tblFiles.setModel(new DefaultTableModel(
                        new Object[][]{
                                {null, null, null, null},
                                {null, null, null, null},
                                {null, null, null, null},
                                {null, null, null, null},
                        },
                        new String[]{
                                "Title 1", "Title 2", "Title 3", "Title 4"
                        }
                ));
                tblFiles.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                tblFiles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                tblFiles.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        tblFilesMousePressed(e);
                    }
                });
                jspFiles.setViewportView(tblFiles);
            }
            pnlMain.add(jspFiles);
        }
        add(pnlMain);
    }// </editor-fold>//GEN-END:initComponents

    private void tblFilesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblFilesMousePressed

        Point p = evt.getPoint();
        ListSelectionModel lsm = tblFiles.getSelectionModel();
        Point p2 = evt.getPoint();
        SwingUtilities.convertPointToScreen(p2, tblFiles);
        final Point screenposition = p2;

        boolean singleRowSelected = lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex();

        final int row = tblFiles.rowAtPoint(p);
        final int col = tblFiles.columnAtPoint(p);

        if (singleRowSelected) {
            lsm.setSelectionInterval(row, row);
        }

        final TMSYSFiles tm = (TMSYSFiles) tblFiles.getModel();
        final SYSFiles sysfile = tm.getRow(tblFiles.convertRowIndexToModel(row));

        if (SwingUtilities.isRightMouseButton(evt)) {

            SYSTools.unregisterListeners(menu);
            menu = new JPopupMenu();

            // SELECT
            JMenuItem itemPopupShow = new JMenuItem(SYSTools.xx("misc.commands.show"), SYSConst.icon22magnify1);
            itemPopupShow.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    SYSFilesTools.handleFile(sysfile, Desktop.Action.OPEN);
                }
            });
            menu.add(itemPopupShow);


            if (col == TMSYSFiles.COL_DESCRIPTION && OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {

                final JMenuItem itemPopupEdit = new JMenuItem(SYSTools.xx("misc.commands.edit"), SYSConst.icon22edit3);
                itemPopupEdit.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {

                        final JidePopup popup = new JidePopup();
                        popup.setMovable(false);
                        popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));

                        final JComponent editor = new JTextArea(sysfile.getBeschreibung(), 10, 40);
                        ((JTextArea) editor).setLineWrap(true);
                        ((JTextArea) editor).setWrapStyleWord(true);
                        ((JTextArea) editor).setEditable(true);

                        popup.getContentPane().add(new JScrollPane(editor));
                        final JButton saveButton = new JButton(SYSConst.icon22apply);
                        saveButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    popup.hidePopup();
                                    SYSFiles mySysfile = em.merge(sysfile);
                                    mySysfile.setBeschreibung(((JTextArea) editor).getText().trim());
                                    em.getTransaction().commit();
                                    tm.setSYSFile(tblFiles.convertRowIndexToModel(row), mySysfile);
                                } catch (Exception e) {
                                    em.getTransaction().rollback();
                                    OPDE.fatal(e);
                                } finally {
                                    em.close();
                                }

                            }
                        });

                        saveButton.setHorizontalAlignment(SwingConstants.RIGHT);
                        JPanel pnl = new JPanel(new BorderLayout(10, 10));
                        JScrollPane pnlEditor = new JScrollPane(editor);

                        pnl.add(pnlEditor, BorderLayout.CENTER);
                        JPanel buttonPanel = new JPanel();
                        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
                        buttonPanel.add(saveButton);
                        pnl.setBorder(new EmptyBorder(10, 10, 10, 10));
                        pnl.add(buttonPanel, BorderLayout.SOUTH);

                        popup.setOwner(tblFiles);
                        popup.removeExcludedComponent(tblFiles);
                        popup.getContentPane().add(pnl);
                        popup.setDefaultFocusComponent(editor);

                        popup.showPopup(screenposition.x, screenposition.y);

                    }
                });
                menu.add(itemPopupEdit);
            }


            if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.DELETE, internalClassID)) {
                JMenuItem itemPopupDelete = new JMenuItem(SYSTools.xx("misc.commands.delete"), SYSConst.icon22delete);
                itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {

                        new DlgYesNo(SYSTools.xx("misc.questions.delete1") + "<br/><b>" + sysfile.getFilename() + "</b><br/>" + SYSTools.xx("misc.questions.delete2"), new ImageIcon(getClass().getResource("/artwork/48x48/bw/trashcan_empty.png")), new Closure() {
                            @Override
                            public void execute(Object o) {
                                if (o.equals(JOptionPane.YES_OPTION)) {
                                    SYSFilesTools.deleteFile(sysfile);
                                    reloadTable();
                                }
                            }
                        });

                    }
                });
                menu.add(itemPopupDelete);
                itemPopupDelete.setEnabled(singleRowSelected);
            }

            menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
        } else if (evt.getClickCount() == 2) {
            SYSFilesTools.handleFile(sysfile, Desktop.Action.OPEN);
        }


    }//GEN-LAST:event_tblFilesMousePressed

    private void jspFilesComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspFilesComponentResized
        JScrollPane jsp = (JScrollPane) evt.getComponent();
        Dimension dim = jsp.getSize();
        // Größe der Text Spalte im TB ändern.
        // Summe der fixen Spalten  = 210 + ein bisschen
        int textWidth = dim.width - 250;
        tblFiles.getColumnModel().getColumn(0).setPreferredWidth(170);
        tblFiles.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblFiles.getColumnModel().getColumn(2).setPreferredWidth(textWidth / 3 * 2);
        tblFiles.getColumnModel().getColumn(3).setPreferredWidth(textWidth / 3);
//        tblFiles.getColumnModel().getColumn(2).setPreferredWidth(100);

//        SYSTools.packTable(tblFiles, 0);

    }//GEN-LAST:event_jspFilesComponentResized

    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);

        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
            searchPanes.add(addCommands());
        }
        searchPanes.addExpansion();
    }

    private CollapsiblePane addCommands() {
        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout());
        mypanel.setBackground(Color.WHITE);

        CollapsiblePane cmdPane = new CollapsiblePane(SYSTools.xx(internalClassID));
        cmdPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        cmdPane.setCollapsible(false);

        try {
            cmdPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        mypanel.add(GUITools.getDropPanel(new FileDrop.Listener() {
            public void filesDropped(java.io.File[] files) {
                java.util.List<SYSFiles> successful = SYSFilesTools.putFiles(files, resident);
                if (!successful.isEmpty()) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(successful.size() + " " + SYSTools.xx("misc.msg.Files") + " " + SYSTools.xx("misc.msg.added")));
                }
                reloadTable();
            }
        }));


        cmdPane.setContentPane(mypanel);
        return cmdPane;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel pnlMain;
    private JScrollPane jspFiles;
    private JTable tblFiles;
    // End of variables declaration//GEN-END:variables
}
