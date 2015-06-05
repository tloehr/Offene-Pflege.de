package gui.interfaces;

import gui.events.DataChangeListener;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 23.02.13
 * Time: 15:09
 * To change this template use File | Settings | File Templates.
 */
public interface EditPanelInterface<T> {



    public void setStartFocus();

//    public void setDataObject(T t);

    public String doValidation();

    public void addDataChangeListener(DataChangeListener<T> dcl);

    public void removeDataChangeListener(DataChangeListener<T> dcl);

    public void cleanup();

    public void reload();

}
