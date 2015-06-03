package gui.events;

import java.util.EventObject;

/**
 * Created by tloehr on 03.06.15.
 */
public class ContentRequestedEvent<T> extends EventObject {

    public ContentRequestedEvent(Object source) {
        super(source);
    }

}
