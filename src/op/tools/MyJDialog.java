package op.tools;

import com.sun.awt.AWTUtilities;
import op.OPDE;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 11.05.12
 * Time: 14:15
 * To change this template use File | Settings | File Templates.
 */
public class MyJDialog extends JDialog {
    private Animator fadeIn, fadeOut;
    private MyJDialog thisDialog;


    public MyJDialog() {
        super();
        setResizable(false);
        setUndecorated(true);
        initAnimator();
    }

    public MyJDialog(Frame frame) {
        super(frame, true);
        setResizable(false);
        setUndecorated(true);
        initAnimator();
    }

    protected void initAnimator() {
        final TimingSource ts = new SwingTimerTimingSource();
        thisDialog = this;
        Animator.setDefaultTimingSource(ts);
        ts.init();
        fadeIn = new Animator.Builder().setDuration(250, TimeUnit.MILLISECONDS).setStartDirection(Animator.Direction.FORWARD).addTarget(new TimingTargetAdapter() {

            @Override
            public void timingEvent(Animator animator, double fraction) {
                OPDE.debug(fraction);
                if (AWTUtilities.isTranslucencySupported(AWTUtilities.Translucency.TRANSLUCENT)) {
                    try {
                        Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
                        Method mSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);
                        mSetWindowOpacity.invoke(null, thisDialog, new Float(fraction));
                        repaint();
                    } catch (Exception ex) {
                        OPDE.warn(ex);
                    }
                }
            }
        }).build();

        fadeOut = new Animator.Builder().setDuration(250, TimeUnit.MILLISECONDS).setStartDirection(Animator.Direction.BACKWARD).addTarget(new TimingTargetAdapter() {

            @Override
            public void timingEvent(Animator animator, double fraction) {
                OPDE.debug(fraction);
                if (AWTUtilities.isTranslucencySupported(AWTUtilities.Translucency.TRANSLUCENT)) {
                    try {
                        Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
                        Method mSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);
                        mSetWindowOpacity.invoke(null, thisDialog, new Float(fraction));
                        repaint();
                    } catch (Exception ex) {
                        OPDE.warn(ex);
                    }
                }
            }

            @Override
            public void end(Animator source) {
                supervisible(false);
            }
        }).build();
    }

    @Override
    public void setVisible(boolean b) {
        setLocation(OPDE.getMainframe().getLocationForDialog(getSize()));
        if (b) {
            if (fadeIn != null && !fadeIn.isRunning()) {
                if (AWTUtilities.isTranslucencySupported(AWTUtilities.Translucency.TRANSLUCENT)) {
                    try {
                        Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
                        Method mSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);
                        mSetWindowOpacity.invoke(null, thisDialog, 0.0f);
                        repaint();
                    } catch (Exception ex) {
                        OPDE.fatal(ex);
                    }
                }
                fadeIn.start();
                super.setVisible(true);
            }
        } else {
            if (fadeIn != null && fadeIn.isRunning()) {
                fadeIn.cancel();
            }
            if (fadeOut != null && !fadeOut.isRunning()) {
                if (AWTUtilities.isTranslucencySupported(AWTUtilities.Translucency.TRANSLUCENT)) {
                    try {
                        Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
                        Method mSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);
                        mSetWindowOpacity.invoke(null, thisDialog, 1.0f);
                        repaint();
                    } catch (Exception ex) {
                        OPDE.fatal(ex);
                    }
                }
                fadeOut.start();
            }
        }
    }


    private void supervisible(boolean b) {
        super.setVisible(b);
    }

}