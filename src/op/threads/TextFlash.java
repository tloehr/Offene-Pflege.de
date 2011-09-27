package op.threads;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 28.06.11
 * Time: 14:41
 * To change this template use File | Settings | File Templates.
 */
public class TextFlash extends SwingWorker {
    JLabel comp;
    boolean soft;
    boolean repeat;
    int maxtime;
    Color[] softColors;
    Color originalForeground;

    public TextFlash(JLabel comp, String text, boolean soft, boolean repeat, int maxtime) {
        this.comp = comp;
        this.soft = soft;
        this.repeat = repeat;
        this.originalForeground = comp.getForeground();
        this.maxtime = maxtime;

        this.comp.setForeground(this.comp.getBackground());
        this.comp.setText(text);

        softColors = new Color[]{comp.getBackground(), Color.LIGHT_GRAY, Color.GRAY, Color.DARK_GRAY, Color.BLACK};
    }


    @Override
    protected Object doInBackground() throws Exception {
        Color[] cycle = soft ? softColors : null;

        Double speed = new Double(maxtime) / (2d * new Double(cycle.length));

        for (int i = 0; i < cycle.length; i++) {
            comp.setForeground(cycle[i]);
            Thread.sleep(speed.intValue());
        }
        for (int i = cycle.length - 1; i >= 0; i--) {
            comp.setForeground(cycle[i]);
            Thread.sleep(speed.intValue());
        }

        return null;
    }

    @Override
    protected void done() {
        super.done();
        comp.setText(null);
        comp.setForeground(originalForeground);
    }
}
