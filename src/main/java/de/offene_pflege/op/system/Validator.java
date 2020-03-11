package de.offene_pflege.op.system;

/**
 * Created by tloehr on 12.01.15.
 */
public interface Validator<T> {
    public boolean isValid(String value);

    public T parse(String text);

}
