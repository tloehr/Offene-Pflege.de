package op.threads;

import op.events.DefaultEvent;
import op.events.DefaultEventListener;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.util.EventListener;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 16.06.11
 * Time: 15:44
 * To change this template use File | Settings | File Templates.
 */
public class IconFlash extends SwingWorker {

    protected Icon originalIcon, alternateIcon;
    protected boolean done;
    protected JButton btn;

    public IconFlash(JButton btn, Icon alternateIcon) {
        originalIcon = btn.getIcon();
        this.alternateIcon = alternateIcon;
        this.btn = btn;
        this.btn.setEnabled(true);
    }

    public void stop(){
        done  = true;
    }

    public JComponent getComponent() {
        return btn;
    }

    @Override
    protected Object doInBackground() throws Exception {
        done = false;
        while (!done) {
            if (btn.getIcon().equals(alternateIcon)){
                btn.setIcon(originalIcon);
            } else {
                btn.setIcon(alternateIcon);
            }
            Thread.sleep(500);
        }
        return null;
    }

    @Override
    protected void done() {
        super.done();
        btn.setIcon(originalIcon);
    }
}