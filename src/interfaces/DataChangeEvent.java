/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interfaces;

import op.OPDE;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.EventObject;
import java.util.Set;

/**
 * @author tloehr
 */
public class DataChangeEvent<T> extends EventObject {
    T data;
    String validationResult;
    Set<ConstraintViolation<T>> constraintViolations;
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

    public DataChangeEvent(Object source, T data) {
        super(source);
        Validator validator = OPDE.getValidatorFactory().getValidator();

        this.data = data;
        constraintViolations = validator.validate(data);
        this.valid = constraintViolations.isEmpty();
    }

    public String getValidationResult() {
        return validationResult;
    }

    public Set<ConstraintViolation<T>> getConstraintViolations() {
        return constraintViolations;
    }

    public boolean isValid() {
        return valid;
    }
}
