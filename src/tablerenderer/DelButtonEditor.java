/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tablerenderer;

import op.OPDE;
import op.threads.ComponentAlternatingFlash;
import op.tools.SYSTools;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.EventObject;

public class DelButtonEditor extends AbstractCellEditor implements TableCellEditor {

    protected boolean pressedOnce;
    protected ComponentAlternatingFlash alternatingFlash;
    protected ArrayList<JPanel> panels;
    protected JTable table;

    public DelButtonEditor(JTable table) {
        this.table = table;
        panels = new ArrayList(table.getRowCount());
        for (int i = 0; i < table.getRowCount(); i++){
            panels.add(null);
        }
    }

    @Override
    public boolean isCellEditable(EventObject e) {
        return true;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }

    public void cleanup() {
        //SYSTools.unregisterListeners(panel);
    }

    @Override
    public Object getCellEditorValue() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        OPDE.debug("Cell is selected: " + isSelected);
        JPanel panel = getPanel(row);
        panel.setBackground(SYSTools.getTableCellBackgroundColor(isSelected, row));
        stopCellEditing();
        return panel;
    }

    protected JPanel getPanel(int row) {
        OPDE.debug("getPanel(): ROW:"+row);
        if (panels.get(row) == null) {
            OPDE.debug("Panel neu erstellen.");
            JPanel panel = new JPanel();
            pressedOnce = false;
            OPDE.debug(panel.getComponentCount());
            panel.add(getDelButton(row));
            panel.add(getCancelButton(row));
            panel.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    e.consume();
                    cancelCellEditing();
                }
            });
            OPDE.debug(panel.getComponentCount());
            addCellEditorListener(new CellEditorListener() {
                @Override
                public void editingStopped(ChangeEvent e) {
                    OPDE.debug("editingStopped");
                }

                @Override
                public void editingCanceled(ChangeEvent e) {
                    OPDE.debug("editingCanceled");
                }
            });
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {

                }

                @Override
                public void mousePressed(MouseEvent e) {
                }
            });
            panels.add(row, panel);
        } else {
            OPDE.debug("Panel gabs schon.");
        }
        return panels.get(row);
    }

    protected JButton getDelButton(int row) {
        JButton button = new JButton(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/edit_remove.png")));
        final int r = row;

        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JButton delButton = (JButton) panels.get(r).getComponent(0);
                JButton cancelButton = (JButton) panels.get(r).getComponent(1);

                //table.setRowSelectionInterval(row, row);
                if (pressedOnce) {
                    delButton.setText(null);
                    alternatingFlash.stop();
                    cancelButton.setVisible(false);
                    alternatingFlash = null;
                } else {
                    delButton.setText("WIRKLICH ?");
                    alternatingFlash = new ComponentAlternatingFlash(delButton, cancelButton, new ImageIcon(getClass().getResource("/artwork/16x16/help2.png")));
                    alternatingFlash.execute();
                    cancelButton.setVisible(true);
                }
                OPDE.debug("delButtonAction");
                pressedOnce = !pressedOnce;
            }
        });
        button.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                e.consume();
                cancelCellEditing();
            }
        });
        return button;
    }

    @Override
    public boolean stopCellEditing() {
//        if (iconflasher != null) {
//            iconflasher.stop();
//        }
        if (alternatingFlash != null) {
            alternatingFlash.stop();
        }
        pressedOnce = false;
        //table.setRowSelectionAllowed(true);
        OPDE.debug("Method stopCellEditing called");
        return super.stopCellEditing();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void cancelCellEditing() {
        if (alternatingFlash != null) {
            alternatingFlash.stop();
        }
        pressedOnce = false;
        //table.setRowSelectionAllowed(true);

        OPDE.debug("Method cancelCellEditing called");
        super.cancelCellEditing();    //To change body of overridden methods use File | Settings | File Templates.
    }

    protected JButton getCancelButton(int row) {
        JButton button = new JButton(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/cancel.png")));
        final int r = row;

        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JButton cancelButton = (JButton) panels.get(r).getComponent(1);
                if (alternatingFlash != null) {
//                    iconflasher.stop();
//                    ((JButton) iconflasher.getComponent()).setText(null);
//                    iconflasher = null;
                    alternatingFlash.stop();
                    alternatingFlash = null;
                    //table.setRowSelectionAllowed(true);
                }
                pressedOnce = false;
                cancelButton.setVisible(false);
                OPDE.debug("cancelButtonAction");
            }
        });

        button.setVisible(false);
        button.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    e.consume();
                    OPDE.debug(e);
                }
            });

        return button;
    }
}
