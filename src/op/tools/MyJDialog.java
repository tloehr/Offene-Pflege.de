package op.tools;

import com.sun.awt.AWTUtilities;
import op.OPDE;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

import javax.swing.*;
import javax.swing.border.LineBorder;
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
    private boolean isDisposed;
    private JPanel content;

    public MyJDialog(boolean decorated) {
        this(OPDE.getMainframe(), decorated);
    }

    public MyJDialog() {
        this(true);
    }

    public MyJDialog(JFrame owner) {
        this(owner, true);
    }

    public MyJDialog(Dialog owner, boolean decorated) {
        super(owner, true);
        initContent();
        isDisposed = false;
        setResizable(false);
        setUndecorated(!decorated);
        initAnimator();
    }

    public MyJDialog(JFrame owner, boolean decorated) {
        super(owner, true);
        initContent();
        isDisposed = false;
        setResizable(false);
        setUndecorated(!decorated);
        initAnimator();
    }

    public MyJDialog(Dialog owner) {
        this(owner, true);
    }

    private void initContent() {
        content = new JPanel();
        content.setBorder(new LineBorder(Color.BLACK, 1));
        setContentPane(content);
    }

    @Override
    public void dispose() {
        setVisible(false);
    }

    protected void initAnimator() {
        if (!OPDE.isAnimation()) {
            return;
        }

        final TimingSource ts = new SwingTimerTimingSource();
        thisDialog = this;
        Animator.setDefaultTimingSource(ts);
        ts.init();
        fadeIn = new Animator.Builder().setDuration(250, TimeUnit.MILLISECONDS).setStartDirection(Animator.Direction.FORWARD).addTarget(new TimingTargetAdapter() {

            @Override
            public void timingEvent(Animator animator, double fraction) {
                if (AWTUtilities.isTranslucencySupported(AWTUtilities.Translucency.TRANSLUCENT)) {
                    try {
                        Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
                        Method mSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);
                        mSetWindowOpacity.invoke(null, thisDialog, new Float(fraction));
                        repaint();
                    } catch (Exception ex) {
//                        OPDE.info("timingEvent#1");
//                        OPDE.warn(ex);
                    }
                }
            }
        }).build();

        fadeOut = new Animator.Builder().setDuration(250, TimeUnit.MILLISECONDS).setStartDirection(Animator.Direction.BACKWARD).addTarget(new TimingTargetAdapter() {

            @Override
            public void timingEvent(Animator animator, double fraction) {
                if (AWTUtilities.isTranslucencySupported(AWTUtilities.Translucency.TRANSLUCENT)) {
                    try {
                        Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
                        Method mSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);
                        mSetWindowOpacity.invoke(null, thisDialog, new Float(fraction));
                        repaint();
                    } catch (Exception ex) {
//                        OPDE.info("timingEvent#2");
//                        OPDE.warn(ex);
                    }
                }
            }

            @Override
            public void end(Animator source) {
                isDisposed = true;
                superdispose();
            }
        }).build();
    }

    @Override
    public void setVisible(boolean visible) {
        if (isDisposed) {
            return;
        }

        if (!OPDE.isAnimation()) {
            super.setVisible(visible);
            return;
        }

        setLocation(OPDE.getMainframe().getLocationForDialog(getSize()));
        if (visible) {
            if (fadeIn != null && !fadeIn.isRunning()) {
                if (AWTUtilities.isTranslucencySupported(AWTUtilities.Translucency.TRANSLUCENT)) {
                    try {
                        Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
                        Method mSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);
                        mSetWindowOpacity.invoke(null, thisDialog, 0.0f);
                        repaint();
                    } catch (Exception ex) {
//                        OPDE.info("setVisible#1");
//                        OPDE.warn(ex);
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
//                        OPDE.info("setVisible#2");
//                        OPDE.warn(ex);
                    }
                }
                fadeOut.start();
            }
        }
    }

    private void superdispose() {
        super.dispose();
    }
}