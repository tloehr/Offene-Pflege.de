package interfaces;

import javax.swing.*;
import java.util.HashSet;

/**
 * Created by tloehr on 11.05.15.
 */
public class EditPanelDefault<T> extends JPanel implements EditPanelInterface<T> {
    protected T data;
    protected HashSet<DataChangeListener<T>> listDCL;

    @Override
    public T getResult() {
        return null;
    }

    @Override
    public void setStartFocus() {

    }

    @Override
    public void setDataObject(T data) {
        this.data = data;
    }

    @Override
    public String doValidation() {
        return "";
    }

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
        listDCL.clear();
        removeAll();
    }

    public EditPanelDefault() {
        super();
        notify();
        listDCL = new HashSet<>();
    }

    public void broadcast(DataChangeEvent<T> dce) {
        for (DataChangeListener<T> dcl : listDCL) {
            dcl.dataChanged(dce);
        }
    }

    public EditPanelDefault(T data) {
        this();
        setDataObject(data);
    }
}
