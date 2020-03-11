/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.offene_pflege.gui.events;

import de.offene_pflege.op.OPDE;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.EventObject;
import java.util.Set;

/**
 * @author tloehr
 */
public class DataChangeEvent<T> extends EventObject {
    T data;
    private boolean triggersReload = false;

    public T getData() {
        return data;
    }

    private DataChangeEvent(Object source) {
        super(source);
    }

    public DataChangeEvent(Object source, T data) throws ConstraintViolationException {
        super(source);
        setData(data);
    }

    public void setData(T data) throws ConstraintViolationException {
        this.data = data;
        Validator validator = OPDE.getValidatorFactory().getValidator();

        Set<ConstraintViolation<T>> constraintViolations = validator.validate(this.data);

        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }

    public boolean isTriggersReload() {
        return triggersReload;
    }

    public void setTriggersReload(boolean triggersReload) {
        this.triggersReload = triggersReload;
    }
}
