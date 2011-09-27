package op.threads;

import op.OPDE;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 21.06.11
 * Time: 14:47
 * To change this template use File | Settings | File Templates.
 */
public class ComponentAlternatingFlash extends SwingWorker {

    protected Color comp1Color, comp2Color, flashingColor;
    protected Icon comp1Icon, comp2Icon, flashingIcon;
    protected boolean done, odd;
    protected JComponent comp1, comp2;

    public JComponent getComp1() {
        return comp1;
    }

    public JComponent getComp2() {
        return comp2;
    }

    public ComponentAlternatingFlash(JComponent comp1, JComponent comp2, Color flashingColor) {
        comp1Color = comp1.getBackground();
        comp2Color = comp2.getBackground();
        comp1.setOpaque(true);
        comp2.setOpaque(true);
        this.comp1 = comp1;
        this.comp2 = comp2;
        this.flashingColor = flashingColor;
        odd = true;
    }

    public ComponentAlternatingFlash(JButton btn1, JButton btn2, Icon flashingIcon) {
        comp1Icon = btn1.getIcon();
        comp2Icon = btn2.getIcon();
        this.comp1 = btn1;
        this.comp2 = btn2;
        this.flashingIcon = flashingIcon;
        odd = true;
    }

    public void stop() {
        done = true;
    }

    @Override
    protected Object doInBackground() throws Exception {
        done = false;
        while (!done) {
            if (flashingIcon != null) {
                if (odd) {
                    ((JButton) comp1).setIcon(comp1Icon);
                    ((JButton) comp2).setIcon(flashingIcon);
                } else {
                    ((JButton) comp1).setIcon(flashingIcon);
                    ((JButton) comp2).setIcon(comp2Icon);
                }
            } else {
                if (odd) {
                    comp1.setBackground(comp1Color);
                    comp2.setBackground(flashingColor);
                } else {
                    comp1.setBackground(flashingColor);
                    comp2.setBackground(comp2Color);
                }
            }
            odd = !odd;
            Thread.sleep(600);
        }
        return null;
    }

    @Override
    protected void done() {
        super.done();
        if (flashingIcon != null) {
            ((JButton) comp1).setIcon(comp1Icon);
            ((JButton) comp2).setIcon(comp2Icon);
        } else {
            comp1.setBackground(comp1Color);
            comp2.setBackground(comp2Color);
            comp1.setOpaque(false);
            comp2.setOpaque(false);
        }
    }
}
