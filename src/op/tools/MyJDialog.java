package op.tools;

import op.OPDE;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 11.05.12
 * Time: 14:15
 * To change this template use File | Settings | File Templates.
 */
public class MyJDialog extends JDialog {
    private Animator animator;


    public MyJDialog() {
        super(OPDE.getMainframe(), true);
        setResizable(false);

//        initAnimator();
//        getRootPane().setOpaque(false);

    }

    public MyJDialog(Frame frame) {
        super(frame);

//        initAnimator();
//        getRootPane().setOpaque(false);

    }

//    protected void initAnimator() {
//        final TimingSource ts = new SwingTimerTimingSource();
//        Animator.setDefaultTimingSource(ts);
//        ts.init();
//        animator = new Animator.Builder().setDuration(250, TimeUnit.MILLISECONDS).setRepeatCount(1).setRepeatBehavior(Animator.RepeatBehavior.REVERSE).setStartDirection(Animator.Direction.BACKWARD).addTarget(new TimingTargetAdapter() {
//
//            @Override
//            public void timingEvent(Animator animator, double fraction) {
//                int alpha = new Double(255 * fraction).intValue();
////                getContentPane().setBackground(new Color(getContentPane().getBackground().getRed(), getContentPane().getBackground().getGreen(), getContentPane().getBackground().getBlue(), alpha));
//                setForeground(new Color(getForeground().getRed(), getForeground().getGreen(), getForeground().getBlue(), alpha));
//                setBackground(new Color(getBackground().getRed(), getBackground().getGreen(), getBackground().getBlue(), alpha));
////                repaint();
//            }
//
////            @Override
////            public void end(Animator source) {
////                setForeground(new Color(getForeground().getRed(), getForeground().getGreen(), getForeground().getBlue()));
////            }
//        }).build();
//    }


    @Override
    public void setVisible(boolean b) {

        if (b) {
            setLocation(OPDE.getMainframe().getLocationForDialog(getSize()));
//            if (animator != null && !animator.isRunning()) {
//                animator.start();
//            }
//        } else {
//            if (animator != null && !animator.isRunning()) {
//                animator.startReverse();
//            }
        }
        super.setVisible(b);
    }
}
