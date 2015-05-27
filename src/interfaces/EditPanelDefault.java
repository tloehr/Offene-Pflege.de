package interfaces;

import org.apache.commons.collections.Closure;

import javax.swing.*;
import java.util.HashSet;

/**
 * Created by tloehr on 11.05.15.
 */
public abstract class EditPanelDefault<T> extends JPanel implements EditPanelInterface<T> {
    protected T data;
    protected HashSet<DataChangeListener<T>> listDCL;
    private final Closure contentProvider;
    protected final EditPanelDefault<T> thisPanel = this;
    protected boolean edited = false;

    public EditPanelDefault(DataChangeListener dcl, Closure contentProvider) {
        this(contentProvider);
        addDataChangeListener(dcl);
    }

    public EditPanelDefault(Closure contentProvider) {
        super();
        listDCL = new HashSet<>();
        this.contentProvider = contentProvider;
        contentProvider.execute(thisPanel);
    }




    @Override
    public abstract void setStartFocus();

    @Override
    public void setDataObject(T data) {
        this.data = data;
    }

    @Override
    public abstract String doValidation();

    @Override
    public void addDataChangeListener(DataChangeListener<T> dcl) {
        listDCL.add(dcl);
    }

    @Override
    public void removeDataChangeListener(DataChangeListener<T> dcl) {
        listDCL.remove(dcl);
    }

    @Override
    public void cleanup() {
        edited = false;
        listDCL.clear();
        removeAll();
    }

    public void broadcast(DataChangeEvent<T> dce) {
        for (DataChangeListener<T> dcl : listDCL) {
            dcl.dataChanged(dce);
        }
    }

    @Override
    public void reload() {
        SwingUtilities.invokeLater(() -> {
            contentProvider.execute(thisPanel);
            edited = false;
            refreshDisplay();
            revalidate();
            repaint();
        });
    }

    public abstract void refreshDisplay();

}
