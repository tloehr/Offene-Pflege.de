/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interfaces;

import op.OPDE;

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
    String validationResult;

    boolean valid;

    public T getData() {
        return data;
    }

    private DataChangeEvent(Object source) {
        super(source);
    }

    public DataChangeEvent(Object source, T data, String validationResult) throws ConstraintViolationException {
        super(source);
        this.data = data;
        this.validationResult = validationResult;
        this.valid = validationResult.isEmpty();
    }

    public DataChangeEvent(Object source, T data) throws ConstraintViolationException {
        super(source);
        Validator validator = OPDE.getValidatorFactory().getValidator();

        Set<ConstraintViolation<T>> constraintViolations = validator.validate(data);
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }

        this.data = data;
        this.valid = true;
    }

    public String getValidationResult() {
        return validationResult;
    }

    public boolean isValid() {
        return valid;
    }
}
