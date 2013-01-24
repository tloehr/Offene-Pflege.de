package op.events;

import java.util.EventListener;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 15.06.11
 * Time: 14:44
 * To change this template use File | Settings | File Templates.
 */
public interface DefaultEventListener extends EventListener {
    public void eventHappened(DefaultEvent evt);
}
