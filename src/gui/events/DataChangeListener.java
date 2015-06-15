/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.events;

import java.lang.reflect.InvocationTargetException;
import java.util.EventListener;

/**
 *
 * @author tloehr
 */
public interface DataChangeListener<T> extends EventListener {

    void dataChanged(DataChangeEvent<T> evt) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException;

}
