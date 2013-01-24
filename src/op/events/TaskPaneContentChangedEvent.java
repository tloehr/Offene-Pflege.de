package op.events;

import com.jidesoft.pane.CollapsiblePane;
import org.jdesktop.swingx.JXTaskPane;

import java.util.EventObject;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 04.02.12
 * Time: 14:38
 * To change this template use File | Settings | File Templates.
 */
public class TaskPaneContentChangedEvent extends EventObject {

    protected List<CollapsiblePane> taskPanes;
    protected String title;

    public TaskPaneContentChangedEvent(Object source, List<CollapsiblePane> taskPanes, String title) {
        super(source);
        this.taskPanes = taskPanes;
        this.title = title;
    }

    public List<CollapsiblePane> getTaskPanes() {
        return taskPanes;
    }

    public String getTitle() {
        return title;
    }
}


