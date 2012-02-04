package op.events;

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
    public static final int TOP = 0;
    public static final int MIDDLE = 1;
    public static final int BOTTOM = 2;

    protected List<JXTaskPane> taskPanes;
    protected int whereToPut;
    protected String title;

    public TaskPaneContentChangedEvent(Object source, List<JXTaskPane> taskPanes, int whereToPut, String title) {
        super(source);
        this.taskPanes = taskPanes;
        this.whereToPut = whereToPut;
        this.title = title;
    }

    public List<JXTaskPane> getTaskPanes() {
        return taskPanes;
    }

    public int getWhereToPut() {
        return whereToPut;
    }

    public String getTitle() {
        return title;
    }
}


