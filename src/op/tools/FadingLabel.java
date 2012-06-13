package op.tools;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * Dieser Label blendet sanft um, wenn sein Text geändert wird. Dazu verwendet er das TimingFramework und seinen AlphaComposite Wert.
 */
public class FadingLabel extends JLabel {
    protected Animator animator;
    protected String newLabelText;
    protected float opacity = 1.0f;

    public FadingLabel(String s, Icon icon, int i) {
        super(s, icon, i);
        initAnimator();
    }

    public FadingLabel(String s, int i) {
        super(s, i);
        initAnimator();
    }

    public FadingLabel(String s) {
        super(s);
        initAnimator();
    }

    public FadingLabel(Icon icon, int i) {
        super(icon, i);
        initAnimator();
    }

    public FadingLabel(Icon icon) {
        super(icon);
        initAnimator();
    }

    public FadingLabel() {
        super();
        initAnimator();
    }

    protected void initAnimator() {
        final TimingSource ts = new SwingTimerTimingSource();
        Animator.setDefaultTimingSource(ts);
        ts.init();

        animator = new Animator.Builder().setDuration(250, TimeUnit.MILLISECONDS).setRepeatCount(2).setRepeatBehavior(Animator.RepeatBehavior.REVERSE).setStartDirection(Animator.Direction.BACKWARD).addTarget(new TimingTargetAdapter() {
            String prevString;

            @Override
            public void begin(Animator source) {
                opacity = 1.0f;
                repaint();
            }

            @Override
            public void timingEvent(Animator animator, double fraction) {
                // Nur setzen beim Einblenden und auch nur dann, wenn sich der String geändert hat.
                if (animator.getCurrentDirection() == Animator.Direction.FORWARD) {
                    if (SYSTools.catchNull(prevString).equals(SYSTools.catchNull(newLabelText))) {
                        setTextSuper(newLabelText);
                    }
                    prevString = newLabelText;
                }
                opacity = new Double(fraction).floatValue();
                repaint();
            }

            @Override
            public void end(Animator source) {
                opacity = 1.0f;
                repaint();
            }
        }).build();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        super.paint(g2);
        g2.dispose();
    }

    @Override
    public void setText(String s) {
        if (SYSTools.catchNull(s).equals(SYSTools.catchNull(newLabelText))) {
            return;
        }
        newLabelText = s;
        if (animator != null && !animator.isRunning()) {
            animator.start();
        }
    }

    private void setTextSuper(String text) {
        super.setText(text);
    }
}
