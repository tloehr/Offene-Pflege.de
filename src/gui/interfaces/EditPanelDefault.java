package gui.interfaces;

import gui.events.DataChangeEvent;
import gui.events.DataChangeListener;

import javax.swing.*;
import java.util.HashSet;

/**
 * Created by tloehr on 11.05.15.
 */
public abstract class EditPanelDefault<T> extends JPanel implements EditPanelInterface<T> {
    protected T data;
    protected HashSet<DataChangeListener<T>> listDCL;
    private final ContentProvider<T> contentProvider;
    protected final EditPanelDefault<T> thisPanel = this;
    protected boolean edited = false;

    public EditPanelDefault(DataChangeListener dcl, ContentProvider<T> contentProvider) {
        this(contentProvider);
        addDataChangeListener(dcl);
    }

    public EditPanelDefault(ContentProvider<T> contentProvider) {
        super();
        listDCL = new HashSet<>();
        this.contentProvider = contentProvider;
        data = contentProvider.getContent();
    }

    @Override
    public abstract void setStartFocus();

//    @Override
//    public void setDataObject(T data) {
//        this.data = data;
//    }

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
        data = null;
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
            data = contentProvider.getContent();
            edited = false;
            refreshDisplay();
            revalidate();
            repaint();
        });
    }

    public abstract void refreshDisplay();

}
