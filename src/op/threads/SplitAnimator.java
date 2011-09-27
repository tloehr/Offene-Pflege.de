package op.threads;

import op.events.DefaultEvent;
import op.events.DefaultEventListener;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.util.EventListener;

/**
 * Diese Klasse dient dazu, SplitPanes zu animieren, damit empfinde ich ein wenig die Bedienung
 * der Apple Geräte nach.
 */
public class SplitAnimator extends SwingWorker {
    protected int targetPositionOfDivider;
    protected int currentPositionOfDivider;
    protected int startPositionOfDivider;
    protected int step = 1;
    protected int distance;
    protected boolean done = false;
    protected JSplitPane splitPane;
    protected EventListenerList listenerList;

    public static int getDividerInPercentage(JSplitPane mysplit) {
        int max;
        if (mysplit.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
            max = mysplit.getWidth();
        } else {
            max = mysplit.getHeight();
        }
        int current = mysplit.getDividerLocation();
        double percentage = (double) current / (double) max * 100d;
        return new Double(percentage).intValue();
    }

    public static int getDividerInAbsolutePosition(JSplitPane mysplit, int percent) {
        int max;
        if (mysplit.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
            max = mysplit.getWidth();
        } else {
            max = mysplit.getHeight();
        }
        return new Double(max * (percent / 100d)).intValue();
    }

    /**
     * Schiebt den Slider auf die maximale Position.
     *
     * @param split
     */
    public SplitAnimator(JSplitPane split) {
        this(split, 100);
    }

    /**
     * Schiebt den Slider der angegebenen JSplitPane auf die prozentual festgelegte Position.
     *
     * @param split
     * @param dividerPositionInPercent
     */
    public SplitAnimator(JSplitPane split, int dividerPositionInPercent) {
        splitPane = split;
        listenerList = new EventListenerList();

        double percent = new Double(dividerPositionInPercent).doubleValue();

        double max = 0d;
        if (split.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
            max = new Double(split.getWidth());
        } else {
            max = new Double(split.getHeight());
        }

        targetPositionOfDivider = new Double(max * (percent / 100d)).intValue();
        currentPositionOfDivider = splitPane.getDividerLocation();
        startPositionOfDivider = currentPositionOfDivider;
        this.targetPositionOfDivider = targetPositionOfDivider;
        distance = Math.abs(targetPositionOfDivider - startPositionOfDivider);
        if (targetPositionOfDivider < currentPositionOfDivider) {
            step *= -1; // Vorzeichenwechsel für Rückwärts.
        }
    }

    public void addThreadDoneListener(DefaultEventListener listener) {
        listenerList.add(DefaultEventListener.class, listener);
    }

    /**
     * Diese Funktion "malt" genau genommen eine "Wanne" im Funktionsplot. Und trifft ziemlich
     * gut die Beschleunigungswerte, die ich gerne hätte.
     *
     * @param x
     * @return
     */
    protected double speedFunction(double x) {
        return 0.000001 * Math.pow(x, 4);
    }

    void fireEvent(DefaultEvent evt) {
        Object[] listeners = listenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance

        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == DefaultEventListener.class) {
                ((DefaultEventListener) listeners[i + 1]).eventHappened(evt);
            }
        }
    }

    @Override
    protected Object doInBackground() throws Exception {
        done = false;
        while (!done) {

            currentPositionOfDivider += step;
            int progress = Math.abs(startPositionOfDivider - currentPositionOfDivider);
            double percentage = (double) progress / (double) distance * 100d;
            splitPane.setDividerLocation(currentPositionOfDivider);

            int speed = Math.min(20, new Double(speedFunction(percentage - 50)).intValue());
            //OPDE.debug(speed);
            Thread.sleep(speed);

            done = step > 0 ? currentPositionOfDivider >= targetPositionOfDivider : currentPositionOfDivider <= targetPositionOfDivider;
        }
        return null;
    }

    @Override
    protected void done() {
        super.done();

        DefaultEvent evt = new DefaultEvent(this);
        evt.getProps().put("message", "done()");
        fireEvent(evt);

        // Aufräumen
        Object[] listeners = listenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            listenerList.remove((Class) listeners[i], (EventListener) listeners[i + 1]);
        }

    }
}
