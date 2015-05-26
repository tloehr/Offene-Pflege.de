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
    String validationResult;
    boolean valid;

    public T getData() {
        return data;
    }

    public DataChangeEvent(Object source, T data, String validationResult) {
        super(source);
        this.data = data;
        this.validationResult = validationResult;
        this.valid = validationResult.isEmpty();
    }

    public boolean isValid() {
        return valid;
    }
}
