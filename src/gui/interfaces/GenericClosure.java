package gui.interfaces;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by tloehr on 02.06.15.
 */
public interface GenericClosure<T> {
    void execute(T var1) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException;
}
