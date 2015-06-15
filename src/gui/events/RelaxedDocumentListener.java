package gui.events;

import gui.interfaces.GenericClosure;
import op.OPDE;
import org.apache.log4j.Logger;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tloehr on 02.06.15.
 */
public class RelaxedDocumentListener implements DocumentListener {
    private final long reactionDelayInMillis;
    private final GenericClosure<DocumentEvent> handleAction;
    private Timer timer;

    public RelaxedDocumentListener(long reactionDelayInMillis, GenericClosure<DocumentEvent> handleAction) {
        this.handleAction = handleAction;
        this.reactionDelayInMillis = reactionDelayInMillis;
    }

    public RelaxedDocumentListener(GenericClosure<DocumentEvent> handleAction) {
        this(OPDE.DEFAULT_DOCUMENT_LISTENER_REACTION_TIME_IN_MILLIS, handleAction);
    }

    public void changedUpdate(DocumentEvent e) {
        check(e);
    }

    public void removeUpdate(DocumentEvent e) {
        check(e);
    }

    public void insertUpdate(DocumentEvent e) {
        check(e);
    }

    public void cleanup() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void check(DocumentEvent e) {
        cleanup();
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    handleAction.execute(e);
                } catch (Exception e1) {
                    OPDE.fatal(Logger.getLogger(getClass()), e1);
                }
            }
        }, reactionDelayInMillis);
    }

}
