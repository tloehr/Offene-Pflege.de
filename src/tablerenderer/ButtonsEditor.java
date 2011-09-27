package tablerenderer;

import op.OPDE;
import op.threads.ComponentAlternatingFlash;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 25.06.11
 * Time: 13:44
 * To change this template use File | Settings | File Templates.
 */
public class ButtonsEditor extends JPanel implements TableCellEditor {
    protected boolean clickedOnceAlready;
    protected JButton cancelButton;
    protected ComponentAlternatingFlash alternatingFlash;
    protected HashMap<JButton, TableButtonBehaviour> behaviourMap;

    public ButtonsEditor(final JTable table, JButton cancelButton, Object[]... buttons) {
        super();
        this.cancelButton = cancelButton;
        clickedOnceAlready = false;

        setOpaque(true);
        behaviourMap = new HashMap<JButton, TableButtonBehaviour>();

        // Dadruch wird der Hintergrund der Zelle richtig eingefärbt.
        MouseListener ml = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                setBackground((Color) UIManager.get("Table.selectionBackground"));
            }
        };

        for (Object[] mybutton : buttons) {
            final JButton button = (JButton) mybutton[0];
            final TableButtonBehaviour action = (TableButtonBehaviour) mybutton[1];
            behaviourMap.put(button, action);

            button.setFocusable(false);
            button.setRolloverEnabled(false);

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (clickedOnceAlready) {
                        stopButtonEditProcess();
                        action.actionPerformed(new TableButtonActionEvent(e, table));
                    } else {
                        startButtonEditProcess(button);
                    }
                }
            });
            button.addMouseListener(ml);

            add(button);
        }

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelCellEditing();
            }
        });
        cancelButton.addMouseListener(ml);
        add(cancelButton);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.setBackground(table.getSelectionBackground());
        setButtonsEnabled(table, row, column);
        return this;
    }

    @Override
    public Object getCellEditorValue() {
        return "";
    }


    protected void setButtonsEnabled(JTable table, int row, int col){
        Iterator<JButton> it = behaviourMap.keySet().iterator();
        while (it.hasNext()){
            JButton btn = it.next();
            btn.setEnabled(behaviourMap.get(btn).isEnabled(table, row, col));
        }
    }

    //Copid from AbstractCellEditor
    //protected EventListenerList listenerList = new EventListenerList();
    transient protected ChangeEvent changeEvent = null;

    protected void startButtonEditProcess(JButton button) {
        button.setText("WIRKLICH ?");
        alternatingFlash = new ComponentAlternatingFlash(button, cancelButton, new ImageIcon(getClass().getResource("/artwork/16x16/help3.png")));
        alternatingFlash.execute();
        clickedOnceAlready = true;
    }

    protected void stopButtonEditProcess() {
        if (alternatingFlash != null) {
            JButton button = (JButton) alternatingFlash.getComp1();
            button.setText(null);
            alternatingFlash.stop();
            alternatingFlash = null;
        }
        clickedOnceAlready = false;
    }

    @Override
    public boolean isCellEditable(java.util.EventObject e) {
        return true;
    }

    @Override
    public boolean shouldSelectCell(java.util.EventObject anEvent) {
        return true;
    }

    @Override
    public boolean stopCellEditing() {
        boolean ok = alternatingFlash == null;
        if (ok) {
            stopButtonEditProcess();
            fireEditingStopped();
        }
        return ok;
    }

    @Override
    public void cancelCellEditing() {
        stopButtonEditProcess();
        fireEditingCanceled();
    }

    @Override
    public void addCellEditorListener(CellEditorListener l) {
        listenerList.add(CellEditorListener.class, l);
    }

    @Override
    public void removeCellEditorListener(CellEditorListener l) {
        listenerList.remove(CellEditorListener.class, l);
    }

    public CellEditorListener[] getCellEditorListeners() {
        return (CellEditorListener[]) listenerList.getListeners(CellEditorListener.class);
    }

    protected void fireEditingStopped() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == CellEditorListener.class) {
                // Lazily create the event:
                if (changeEvent == null) changeEvent = new ChangeEvent(this);
                ((CellEditorListener) listeners[i + 1]).editingStopped(changeEvent);
            }
        }
    }

    protected void fireEditingCanceled() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == CellEditorListener.class) {
                // Lazily create the event:
                if (changeEvent == null) changeEvent = new ChangeEvent(this);
                ((CellEditorListener) listeners[i + 1]).editingCanceled(changeEvent);
            }
        }
    }
}
