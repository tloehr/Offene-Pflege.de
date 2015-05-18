/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interfaces;

import java.util.EventObject;

/**
 * @author tloehr
 */
public class DataChangeEvent<T> extends EventObject {
    T data;

    public T getData() {
        return data;
    }

    public DataChangeEvent(Object source, T data) {
        super(source);
        this.data = data;
    }

//    public DataChangeEvent(Object source) {
//        super(source);
//    }


}
