package gui.events;

import java.lang.reflect.InvocationTargetException;
import java.util.EventListener;

/**
 * Created by tloehr on 03.06.15.
 */
public interface ContentRequestedEventListener<T> extends EventListener {
    void contentRequested(ContentRequestedEvent<T> cre) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException;
}
