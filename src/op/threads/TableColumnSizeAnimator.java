package op.threads;

import op.OPDE;
import op.events.DefaultEvent;
import op.events.DefaultEventListener;

import javax.persistence.Column;
import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableColumn;
import java.awt.event.ComponentEvent;
import java.util.EventListener;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 27.06.11
 * Time: 15:39
 * To change this template use File | Settings | File Templates.
 */
public class TableColumnSizeAnimator extends SwingWorker {
    protected int targetWidth;
    protected int currentWidth;
    protected int startWidth;
    protected int step = 1;
    protected int distance;
    protected boolean done = false;
    protected EventListenerList listenerList;
    protected TableColumn column;
    protected JScrollPane jsp;

    public TableColumnSizeAnimator(JScrollPane jsp, TableColumn column, int width) {
        this.column = column;
        listenerList = new EventListenerList();
        this.jsp = jsp;

        targetWidth = width;
        currentWidth = column.getWidth();
        startWidth = column.getWidth();

        distance = Math.abs(targetWidth - startWidth);
        if (targetWidth < currentWidth) {
            step *= -1; // Vorzeichenwechsel für Rückwärts.
        }

        OPDE.debug("width: "+width);

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
            currentWidth += step;
            int progress = Math.abs(startWidth - currentWidth);
            double percentage = (double) progress / (double) distance * 100d;
            column.setWidth(currentWidth);

            jsp.dispatchEvent(new ComponentEvent(jsp, ComponentEvent.COMPONENT_RESIZED));

            //int speed = Math.min(20, new Double(speedFunction(percentage - 50)).intValue());

            //OPDE.debug(speed);
            Thread.sleep(3);

            done = step > 0 ? currentWidth >= targetWidth : currentWidth <= targetWidth;
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
