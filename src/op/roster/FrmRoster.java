/*
 * Created by JFormDesigner on Mon Aug 26 14:23:16 CEST 2013
 */

package op.roster;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.combobox.ExComboBox;
import com.jidesoft.combobox.ListExComboBox;
import com.jidesoft.converter.ConverterContext;
import com.jidesoft.converter.ObjectConverter;
import com.jidesoft.converter.ObjectConverterManager;
import com.jidesoft.grid.*;
import entity.Homes;
import entity.HomesTools;
import entity.roster.Rosters;
import entity.roster.TMRoster;
import entity.roster.TMRosterFooter;
import entity.roster.TMRosterHeader;
import entity.system.SYSPropsTools;
import entity.system.Users;
import entity.system.UsersTools;
import op.OPDE;
import op.tools.GUITools;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.regex.Pattern;

/**
 * @author Torsten Löhr
 */
public class FrmRoster extends JFrame {
    public static final String internalClassID = "opde.roster";
    private TMRoster tmRoster = null;
    private Rosters roster;
    private final boolean readOnly;
    private TableScrollPane tsp1;
    private JPopupMenu menu;
    private Font[] fonts = new Font[]{SYSConst.ARIAL14, SYSConst.ARIAL14BOLD, SYSConst.ARIAL18, SYSConst.ARIAL18BOLD, SYSConst.ARIAL20, SYSConst.ARIAL20BOLD, SYSConst.ARIAL24, SYSConst.ARIAL24BOLD};
    private int currentFontIndex = 0;

    public FrmRoster(Rosters roster) {

        this.roster = roster;
        this.readOnly = roster.getOpenedBy() != null;

        setTitle(new LocalDate(roster.getMonth()).toString("MMMM yyyy"));

        currentFontIndex = SYSPropsTools.getInteger("opde.roster.fontsize");
        SYSPropsTools.storeProp("opde.roster.fontsize", Integer.toString(currentFontIndex), OPDE.getLogin().getUser());

        if (!readOnly) {
            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();
                Rosters myRoster = em.merge(roster);
                em.lock(myRoster, LockModeType.OPTIMISTIC);
                myRoster.setOpenedBy(em.merge(OPDE.getLogin()));
                em.getTransaction().commit();
                this.roster = myRoster;
            } catch (OptimisticLockException ole) {
                OPDE.debug(ole);
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

        initComponents();
        initFrame();

        setVisible(true);

    }

    @Override
    public void dispose() {
        if (!readOnly) {
            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();
                Rosters myRoster = em.merge(roster);
                em.lock(myRoster, LockModeType.OPTIMISTIC);
                myRoster.setOpenedBy(null);

                SYSPropsTools.storeProp(em, "rosterid:" + myRoster.getId(), tmRoster.getUserList());

//                myRoster.setXml(tmRoster.getRosterParameters().toXML());

                em.getTransaction().commit();
                this.roster = myRoster;
            } catch (OptimisticLockException ole) {
                OPDE.debug(ole);
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
        OPDE.getMainframe().removeRoster(roster);
        super.dispose();
    }

    private void initFrame() {

        btnLock.setIcon(readOnly ? SYSConst.icon22encrypted : SYSConst.icon22decrypted);
        btnLock.setEnabled(readOnly);

        ObjectConverterManager.initDefaultConverter();
        CellEditorManager.initDefaultEditor();
        CellRendererManager.initDefaultRenderer();
//        CellRendererManager.registerRenderer(String.class, new DefaultTableCellRenderer() {
//            @Override
//            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//                if (comp instanceof JLabel) {
////                    comp.setFont(SYSConst.ARIAL20);
//                }
//                return comp;
//            }
//        });

        ObjectConverterManager.registerConverter(Homes.class, new ObjectConverter() {
            @Override
            public String toString(Object o, ConverterContext converterContext) {
                return o instanceof Homes ? ((Homes) o).getShortname() : "";
            }

            @Override
            public boolean supportToString(Object o, ConverterContext converterContext) {
                return true;
            }

            @Override
            public Object fromString(String s, ConverterContext converterContext) {
                return null;
            }

            @Override
            public boolean supportFromString(String s, ConverterContext converterContext) {
                return false;
            }
        });

        ObjectConverterManager.registerConverter(Users.class, new ObjectConverter() {
            @Override
            public String toString(Object o, ConverterContext converterContext) {
                return o instanceof Users ? ((Users) o).getName() : "";
            }

            @Override
            public boolean supportToString(Object o, ConverterContext converterContext) {
                return true;
            }

            @Override
            public Object fromString(String s, ConverterContext converterContext) {
                return null;
            }

            @Override
            public boolean supportFromString(String s, ConverterContext converterContext) {
                return false;
            }
        });

        CellEditorManager.registerEditor(Homes.class, new CellEditorFactory() {
            public CellEditor create() {
                return new ExComboBoxCellEditor() {
                    ExComboBox myEditor;

                    @Override
                    public ExComboBox createExComboBox() {
                        myEditor = new ListExComboBox(HomesTools.getAll().toArray());
                        myEditor.setRenderer(HomesTools.getRenderer());
//                        myEditor.setFont(SYSConst.ARIAL20);
                        return myEditor;
                    }

                    @Override
                    public void setCellEditorValue(Object o) {
                        Homes myHome = (Homes) o;
                        myEditor.setSelectedItem(myHome);
                    }

                    @Override
                    public boolean isCellEditable(EventObject eventObject) {
                        if (eventObject instanceof MouseEvent) {
                            return ((MouseEvent) eventObject).getClickCount() >= 2;
                        }
                        return true;

                    }
                };
            }
        }, new EditorContext("HomesSelectionEditor"));

        CellEditorManager.registerEditor(Users.class, new CellEditorFactory() {
            public CellEditor create() {
                return new ExComboBoxCellEditor() {
                    ExComboBox myEditor;

                    @Override
                    public ExComboBox createExComboBox() {
                        ArrayList<Users> listAllAllowedUsers = new ArrayList<Users>(UsersTools.getUsersWithValidContractsIn(new LocalDate(roster.getMonth())).keySet());
                        Collections.sort(listAllAllowedUsers);

                        myEditor = new ListExComboBox(listAllAllowedUsers.toArray());
                        myEditor.setRenderer(UsersTools.getRenderer());
                        myEditor.setFont(SYSConst.ARIAL20);
                        return myEditor;
                    }

                    @Override
                    public void setCellEditorValue(Object o) {
                        if (o instanceof Users) {
//                            Users myUser = (Users) o;
                            myEditor.setSelectedItem(o);
                        }
                    }

                    @Override
                    public boolean isCellEditable(EventObject eventObject) {
                        if (eventObject instanceof MouseEvent) {
                            return ((MouseEvent) eventObject).getClickCount() >= 2;
                        }
                        return true;

                    }
                };
            }
        }, new EditorContext("UserSelectionEditor"));

        CellEditorManager.registerEditor(String.class, new CellEditorFactory() {
            public CellEditor create() {
                return new TextFieldCellEditor(String.class) {
                    JTextField myEditor;

                    @Override
                    protected JTextField createTextField() {
                        myEditor = super.createTextField();
//                        myEditor.setFont(SYSConst.ARIAL20);
                        return myEditor;
                    }

                    @Override
                    public void setCellEditorValue(Object o) {
                        myEditor.setText(o.toString());
                    }

                    @Override
                    public boolean isCellEditable(EventObject eventObject) {
                        if (eventObject instanceof MouseEvent) {
                            return ((MouseEvent) eventObject).getClickCount() >= 2;
                        }
                        return true;

                    }
                };
            }
        }, new EditorContext("DefaultTextEditor"));


        tmRoster = new TMRoster(roster, readOnly, fonts[currentFontIndex]);
        btnFontSize.setText(fonts[currentFontIndex].getFontName() + ", " + fonts[currentFontIndex].getSize());

        TMRosterHeader tmRosterHeader = new TMRosterHeader(tmRoster);
        TMRosterFooter tmRosterFooter = new TMRosterFooter(tmRoster);

        tsp1 = new TableScrollPane(tmRoster, tmRosterHeader, tmRosterFooter, false);

        TableUtils.unifyTableCellSelection(tsp1.getAllChildTables(), tsp1.getMainTable());
        TableUtils.unifyTableColumnSelection(tsp1.getAllChildTables());
        TableUtils.unifyTableRowSelection(tsp1.getAllChildTables());

        tsp1.getColumnHeaderTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tsp1.getColumnFooterTable().setAutoResizeMode(JideTable.AUTO_RESIZE_FILL);
        tsp1.getMainTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);


//        tsp1.getMainTable().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);


//        tsp1.getRowHeaderTable().setFocusable(false);
//        tsp1.getRowHeaderTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//            @Override
//            public void valueChanged(ListSelectionEvent e) {
//                if (!tsp1.getRowHeaderTable().getSelectionModel().isSelectionEmpty()) {
//                    tsp1.getRowHeaderTable().getSelectionModel().clearSelection();
//                }
//            }
//        });
//
//        tsp1.getRowFooterTable().setFocusable(false);
//

//
        for (final JTable jTable : tsp1.getAllChildTables()) {
            jTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    mousePressedOnTable(e, jTable);
                }
            });


            TableUtils.autoResizeAllRows(jTable);
//            JLabel lbl = new JLabel("X");
//            lbl.setFont(SYSConst.ARIAL20);
//
//            jTable.setRowHeight(lbl.getPreferredSize().height);

        }


        blockHeaders();
        hotkeys();

        tsp1.getRowHeaderTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tsp1.getRowFooterTable().setAutoResizeMode(JideTable.AUTO_RESIZE_FILL);

        CellConstraints c = new CellConstraints();
        add(tsp1, c.xy(3, 3, CellConstraints.FILL, CellConstraints.FILL));

        tsp1.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent evt) {
                super.componentResized(evt);
                tsp1.getRowHeaderTable().getColumnModel().getColumn(0).setPreferredWidth(120);
                tsp1.getRowHeaderTable().getColumnModel().getColumn(1).setPreferredWidth(450);
//                tsp1.getRowHeaderTable().getColumnModel().getColumn(2).setPreferredWidth(200);
                for (int col = 0; col < tsp1.getMainTable().getColumnCount(); col++) {  //(int day = 0; day < new LocalDate(roster.getMonth()).dayOfMonth().withMaximumValue().getDayOfMonth(); day++) {
                    tsp1.getMainTable().getColumnModel().getColumn(col).setPreferredWidth(100);

                }
//                tsp1.getRowFooterTable().getColumnModel().getColumn(0).setPreferredWidth(140);
            }
        });

        tsp1.setRowSelectionAllowed(false);
        tsp1.setColumnSelectionAllowed(false);
        tsp1.setAllowMultiSelectionInDifferentTable(false);
        tsp1.setCellSelectionEnabled(true);


        tsp1.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                OPDE.debug(evt.getPropertyName() + ":" + evt.getNewValue());
            }
        });


    }

    private void btnSortUserActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void btnFontSizeActionPerformed(ActionEvent e) {
        currentFontIndex++;
        if (currentFontIndex >= fonts.length) currentFontIndex = 0;
        tmRoster.setFont(fonts[currentFontIndex]);
        for (final JTable jTable : tsp1.getAllChildTables()) {
            TableUtils.autoResizeAllRows(jTable);
        }
        SYSPropsTools.storeProp("opde.roster.fontsize", Integer.toString(currentFontIndex), OPDE.getLogin().getUser());
        btnFontSize.setText(fonts[currentFontIndex].getFontName() + ", " + fonts[currentFontIndex].getSize());
    }

    private void btnSortHomes2ActionPerformed(ActionEvent e) {

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        toolBar1 = new JToolBar();
        btnLock = new JButton();
        btnSortHomes = new JButton();
        btnSortHomes2 = new JButton();
        btnFontSize = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
                "default, $lcgap, default:grow, $lcgap, default",
                "default, $lgap, default:grow, 2*($lgap, default)"));

        //======== toolBar1 ========
        {

            //---- btnLock ----
            btnLock.setText(null);
            btnLock.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnSortUserActionPerformed(e);
                }
            });
            toolBar1.add(btnLock);

            //---- btnSortHomes ----
            btnSortHomes.setText("text");
            toolBar1.add(btnSortHomes);

            //---- btnSortHomes2 ----
            btnSortHomes2.setText("text");
            btnSortHomes2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnSortHomes2ActionPerformed(e);
                }
            });
            toolBar1.add(btnSortHomes2);

            //---- btnFontSize ----
            btnFontSize.setText("fontsize");
            btnFontSize.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnFontSizeActionPerformed(e);
                }
            });
            toolBar1.add(btnFontSize);
        }
        contentPane.add(toolBar1, CC.xywh(1, 1, 5, 1));
        setSize(875, 660);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FrmRoster frmRoster = (FrmRoster) o;

        if (roster != null ? !roster.equals(frmRoster.roster) : frmRoster.roster != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = roster != null ? roster.hashCode() : 0;
        return result;
    }

    public Rosters getRoster() {
        return roster;
    }

    private void mousePressedOnTable(java.awt.event.MouseEvent evt, final JTable table) {

        Point p = evt.getPoint();
//            ListSelectionModel lsm = tblFiles.getSelectionModel();
//            Point p2 = evt.getPoint();
//            SwingUtilities.convertPointToScreen(p2, tblFiles);
//            final Point screenposition = p2;

//        boolean singleRowSelected = lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex();

        final int row = table.rowAtPoint(p);
        final int col = table.columnAtPoint(p);

        table.setRowSelectionInterval(row, row);
        table.setColumnSelectionInterval(col, col);


//        if (singleRowSelected) {
//            lsm.setSelectionInterval(row, row);
//        }

        if (table.equals(tsp1.getMainTable())) {
            if (SwingUtilities.isRightMouseButton(evt)) {
                SYSTools.unregisterListeners(menu);
                menu = tmRoster.getMainContextMenuAt(row, col);

                if (menu != null) {
                    menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
                }
            }
        }

        if (table.equals(tsp1.getRowHeaderTable())) {
            if (SwingUtilities.isRightMouseButton(evt)) {
                SYSTools.unregisterListeners(menu);
                menu = tmRoster.getRowHeaderContextMenuAt(row, col, this);

                if (menu != null) {
                    menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
                }
            }
        }

    }

    private void hotkeys() {
        int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
//        tsp1.getMainTable().getInputMap().remove(KeyStroke.getKeyStroke(KeyEvent.VK_COPY, 0));
        tsp1.getMainTable().getInputMap(condition).put(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK), "paste");
        InputMap inputMap = tsp1.getInputMap(condition);
        ActionMap actionMap = tsp1.getActionMap();
        /***
         *          _      _      _
         *       __| | ___| | ___| |_ ___
         *      / _` |/ _ \ |/ _ \ __/ _ \
         *     | (_| |  __/ |  __/ ||  __/
         *      \__,_|\___|_|\___|\__\___|
         *
         */
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        actionMap.put("delete", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {

                if (GUITools.isSelectionInMaintable(tsp1)) {
                    for (Point p : GUITools.getSelectedCells(tsp1.getMainTable())) {
                        tmRoster.emptyCellInMaintable(p.y, p.x);
                    }
                } else if (GUITools.isSelectionInRowHeaderTable(tsp1)) {
                    for (Point p : GUITools.getSelectedCells(tsp1.getRowHeaderTable())) {
                        tmRoster.emptyCellInRowheaderTable(p.y, p.x);
                    }
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            tsp1.validate();
                            tsp1.repaint();
                        }
                    });
                }
            }
        });
        /***
         *                      _
         *      _ __   __ _ ___| |_ ___
         *     | '_ \ / _` / __| __/ _ \
         *     | |_) | (_| \__ \ ||  __/
         *     | .__/ \__,_|___/\__\___|
         *     |_|
         */
        tsp1.getMainTable().getActionMap().put("paste", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {

                if (tsp1.getSelectedRowCount() != 1 || tsp1.getSelectedColumnCount() != 1) return;

                try {
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

                    String clip = clipboard.getData(DataFlavor.stringFlavor).toString();

                    if (!clip.endsWith("\n")) clip += "\n";

                    Pattern p = Pattern.compile("([A-Za-z0-9 .,-]*[\\t\\n]{1})*");

                    if (p.matcher(clip).matches()) {


                        // relative position within selection
                        int row = tsp1.getSelectedRow() - TMRoster.COL_HEADER;
                        int col = tsp1.getSelectedColumn() - TMRoster.ROW_HEADER;

                        for (String line : clip.split("\\r?\\n")) {
                            for (String token : line.split("\t")) {
                                if (row < tsp1.getMainTable().getRowCount() && col < tsp1.getMainTable().getColumnCount() && tsp1.getMainTable().isCellEditable(row, col)) {
                                    tsp1.getMainTable().setValueAt(token, row, col);
                                }
                                col++;

                            }
                            row++;
                        }

                    }
                } catch (UnsupportedFlavorException ex) {

                } catch (IOException e1) {

                }
            }
        });
    }

    private void blockHeaders() {
        tsp1.getColumnFooterTable().setFocusable(false);
        tsp1.getColumnFooterTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!tsp1.getColumnFooterTable().getSelectionModel().isSelectionEmpty()) {
                    tsp1.getColumnFooterTable().getSelectionModel().clearSelection();
                }
            }
        });

        tsp1.getRowHeaderColumnHeaderTable().setFocusable(false);
        tsp1.getRowHeaderColumnHeaderTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!tsp1.getRowHeaderColumnHeaderTable().getSelectionModel().isSelectionEmpty()) {
                    tsp1.getRowHeaderColumnHeaderTable().getSelectionModel().clearSelection();
                }
            }
        });
        tsp1.getRowHeaderColumnFooterTable().setFocusable(false);
        tsp1.getRowHeaderColumnFooterTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!tsp1.getRowHeaderColumnFooterTable().getSelectionModel().isSelectionEmpty()) {
                    tsp1.getRowHeaderColumnFooterTable().getSelectionModel().clearSelection();
                }
            }
        });

        tsp1.getRowHeaderColumnHeaderTable().setFocusable(false);
        tsp1.getRowHeaderColumnHeaderTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!tsp1.getRowHeaderColumnHeaderTable().getSelectionModel().isSelectionEmpty()) {
                    tsp1.getRowHeaderColumnHeaderTable().getSelectionModel().clearSelection();
                }
            }
        });

//        tsp1.getRowFooterColumnHeaderTable().setFocusable(false);
//        tsp1.getRowFooterColumnHeaderTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//            @Override
//            public void valueChanged(ListSelectionEvent e) {
//                if (!tsp1.getRowFooterColumnHeaderTable().getSelectionModel().isSelectionEmpty()) {
//                    tsp1.getRowFooterColumnHeaderTable().getSelectionModel().clearSelection();
//                }
//            }
//        });

//        tsp1.getRowFooterColumnFooterTable().setFocusable(false);
//        tsp1.getRowFooterColumnFooterTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//            @Override
//            public void valueChanged(ListSelectionEvent e) {
//                if (!tsp1.getRowFooterColumnFooterTable().getSelectionModel().isSelectionEmpty()) {
//                    tsp1.getRowFooterColumnFooterTable().getSelectionModel().clearSelection();
//                }
//            }
//        });

        tsp1.getColumnHeaderTable().setFocusable(false);
        tsp1.getColumnHeaderTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!tsp1.getColumnHeaderTable().getSelectionModel().isSelectionEmpty()) {
                    tsp1.getColumnHeaderTable().getSelectionModel().clearSelection();
                }
            }
        });

//        tsp1.getRowFooterTable().setFocusable(false);
//        tsp1.getRowFooterTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//            @Override
//            public void valueChanged(ListSelectionEvent e) {
//                if (!tsp1.getRowFooterTable().getSelectionModel().isSelectionEmpty()) {
//                    tsp1.getRowFooterTable().getSelectionModel().clearSelection();
//                }
//            }
//        });
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JToolBar toolBar1;
    private JButton btnLock;
    private JButton btnSortHomes;
    private JButton btnSortHomes2;
    private JButton btnFontSize;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
