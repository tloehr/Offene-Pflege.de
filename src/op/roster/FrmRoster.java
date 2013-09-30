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
import com.jidesoft.swing.StyledLabel;
import com.jidesoft.swing.StyledLabelBuilder;
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
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;

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

    public FrmRoster(Rosters roster) {

        this.roster = roster;
        this.readOnly = roster.getOpenedBy() != null;

        setTitle(new LocalDate(roster.getMonth()).toString("MMMM yyyy"));

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


        tmRoster = new TMRoster(roster, readOnly);

        TMRosterHeader tmRosterHeader = new TMRosterHeader(tmRoster);
        TMRosterFooter tmRosterFooter = new TMRosterFooter(tmRoster);

        tsp1 = new TableScrollPane(tmRoster, tmRosterHeader, tmRosterFooter, false);

        tsp1.getColumnHeaderTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tsp1.getColumnFooterTable().setAutoResizeMode(JideTable.AUTO_RESIZE_FILL);
        tsp1.getMainTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tsp1.getMainTable().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

//        tsp1.getRowHeaderTable().getColumnModel().getColumn(0).setCellRenderer(new TableCellRenderer() {
//            @Override
//            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//                StyledLabel lbl = StyledLabelBuilder.createStyledLabel(value.toString());
//                if (isSelected) {
//                    lbl.setBackground(((StyleModel) table.getModel()).getCellStyleAt(row, column).getSelectionBackground());
//                } else {
//                    lbl.setBackground(((StyleModel) table.getModel()).getCellStyleAt(row, column).getBackground());
//                }
//
//                lbl.setOpaque(true);
//                return lbl;
//            }
//        });
//
//        for (int col = 0; col < tsp1.getMainTable().getColumnCount(); col++) {
//            tsp1.getMainTable().getColumnModel().getColumn(col).setCellRenderer(new TableCellRenderer() {
//                @Override
//                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//                    StyledLabel lbl = StyledLabelBuilder.createStyledLabel(value.toString());
//                    if (isSelected) {
//                        lbl.setBackground(((StyleModel) table.getModel()).getCellStyleAt(row, column).getSelectionBackground());
//                    } else {
//                        lbl.setBackground(((StyleModel) table.getModel()).getCellStyleAt(row, column).getBackground());
//                    }
//
//                    lbl.setOpaque(true);
//                    return lbl;
//                }
//            });
//        }

        tsp1.getMainTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mousePressedOnTable(e);
            }
        });


        tsp1.getRowHeaderTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tsp1.getRowFooterTable().setAutoResizeMode(JideTable.AUTO_RESIZE_FILL);

        CellConstraints c = new CellConstraints();
        add(tsp1, c.xy(3, 3, CellConstraints.FILL, CellConstraints.FILL));

        tsp1.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent evt) {
                super.componentResized(evt);
                tsp1.getRowHeaderTable().getColumnModel().getColumn(0).setPreferredWidth(120);
                tsp1.getRowHeaderTable().getColumnModel().getColumn(1).setPreferredWidth(120);
                for (int col = 0; col < tsp1.getMainTable().getColumnCount(); col++) {  //(int day = 0; day < new LocalDate(roster.getMonth()).dayOfMonth().withMaximumValue().getDayOfMonth(); day++) {
                    tsp1.getMainTable().getColumnModel().getColumn(col).setPreferredWidth(100);

                }
                tsp1.getRowFooterTable().getColumnModel().getColumn(0).setPreferredWidth(140);
            }
        });

        tsp1.setRowSelectionAllowed(false);
        tsp1.setColumnSelectionAllowed(false);
        tsp1.setCellSelectionEnabled(true);

        int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
        InputMap inputMap = tsp1.getMainTable().getInputMap(condition);
        ActionMap actionMap = tsp1.getMainTable().getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        actionMap.put("delete", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {

//                OPDE.debug("delete ?");
                int rowIndex = tsp1.getMainTable().getSelectedRow();
                int colIndex = tsp1.getMainTable().getSelectedColumn();

                if (rowIndex >= 0 || colIndex >= 0) {
                    tmRoster.emptyCell(rowIndex, colIndex);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            tsp1.getMainTable().validate();
                            tsp1.getMainTable().repaint();
                        }
                    });
                }
            }
        });


    }

    private void btnSortUserActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        toolBar1 = new JToolBar();
        btnLock = new JButton();
        btnSortHomes = new JButton();

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

    private void mousePressedOnTable(java.awt.event.MouseEvent evt) {

        Point p = evt.getPoint();
//            ListSelectionModel lsm = tblFiles.getSelectionModel();
//            Point p2 = evt.getPoint();
//            SwingUtilities.convertPointToScreen(p2, tblFiles);
//            final Point screenposition = p2;

//        boolean singleRowSelected = lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex();

        final int row = tsp1.getMainTable().rowAtPoint(p);
        final int col = tsp1.getMainTable().columnAtPoint(p);

        tsp1.getMainTable().setRowSelectionInterval(row, row);
        tsp1.getMainTable().setColumnSelectionInterval(col, col);

//        if (singleRowSelected) {
//            lsm.setSelectionInterval(row, row);
//        }


        if (SwingUtilities.isRightMouseButton(evt)) {

            SYSTools.unregisterListeners(menu);
            menu = tmRoster.getContextMenuAt(row, col);

            if (menu != null) {
                menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
            }
        }


    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JToolBar toolBar1;
    private JButton btnLock;
    private JButton btnSortHomes;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
