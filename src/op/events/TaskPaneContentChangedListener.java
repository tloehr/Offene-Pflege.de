package op.events;

import java.util.EventListener;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 04.02.12
 * Time: 14:42
 * To change this template use File | Settings | File Templates.
 */
public interface TaskPaneContentChangedListener extends EventListener {
    public void contentChanged(TaskPaneContentChangedEvent evt);
}
