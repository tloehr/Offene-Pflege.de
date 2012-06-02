package op.tools;

/**
 * http://codeidol.com/java/swing/Transparent-and-Animated-Windows/Turn-Dialogs-into-Frame-Anchored-Sheets/
 * http://book.javanb.com/swing-hacks/swinghacks-chp-6-sect-6.html
 * Hack 44 und Hack 45
 */

import op.OPDE;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.util.concurrent.TimeUnit;

public class SheetableJFrame extends JFrame {

    public static final int INCOMING = 1;
    public static final int OUTGOING = -1;
    public static final long ANIMATION_DURATION_MILLIS = 400;

    JComponent sheet;
    JPanel glass;

    AnimatingSheet animatingSheet;
    int animationDirection;
    Timer animationTimer;
    Animator animator;
//    BufferedImage offscreenImage;

    MouseAdapter mouseAdapter;

    public SheetableJFrame() {
        super();
        initAnimator();
        glass = (JPanel) getGlassPane();
        animatingSheet = new AnimatingSheet();
        animatingSheet.setBorder(new LineBorder(Color.black, 1));

        // Empty MouseAdapter to block unwanted clicks to the glass pane.
        mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
            }

            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {
            }
        };

    }

    public JComponent showJDialogAsSheet(JDialog dialog) {
        sheet = (JComponent) dialog.getContentPane();
        sheet.setBorder(new LineBorder(Color.black, 1));
        glass.addMouseListener(mouseAdapter);
        glass.removeAll();
        animationDirection = INCOMING;
        startAnimation();
        return sheet;
    }

    public void hideSheet() {
        animationDirection = OUTGOING;
        startAnimation();
    }


    private void startAnimation() {
        glass.repaint();
        // clear glasspane and set up animatingSheet
        animatingSheet.setSource(sheet);
        glass.removeAll();
        glass.setLayout(null);
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.anchor = GridBagConstraints.NORTH;
        animatingSheet.setBounds(glass.getInsets().left + glass.getWidth() / 2 - sheet.getWidth() / 2, glass.getInsets().top + 100, sheet.getWidth(), sheet.getHeight());
        glass.add(animatingSheet);
//        gbc.gridy = 1;
//        gbc.weighty = Integer.MAX_VALUE;
//        glass.add(Box.createGlue(), gbc);
        glass.setVisible(true);
        if (animationDirection == INCOMING){
            animator.start();
        } else {
            animator.startReverse();
        }

    }

    private void initAnimator() {
        final TimingSource ts = new SwingTimerTimingSource();
        Animator.setDefaultTimingSource(ts);
        ts.init();
        animator = new Animator.Builder().setDuration(ANIMATION_DURATION_MILLIS, TimeUnit.MILLISECONDS).setRepeatCount(1).setStartDirection(Animator.Direction.FORWARD).addTarget(new TimingTargetAdapter() {
            AccelerationInterpolator ai = new AccelerationInterpolator(.4f, .4f);
            int animatingHeight = 0;

            @Override
            public void end(Animator source) {
                super.end(source);

                if (animationDirection == INCOMING) {
                    finishShowingSheet();
                } else {
                    glass.removeMouseListener(mouseAdapter);
                    glass.removeAll();
                    glass.setVisible(false);
                }
            }


            @Override
            public void timingEvent(Animator animator, double fraction) {
                // Interpolate for Acceleration and Deceleration
                fraction = ai.interpolate(fraction);
                animatingHeight = (int) (fraction * sheet.getHeight());
                animatingSheet.setAnimatingHeight(animatingHeight);
                animatingSheet.repaint();
            }
        }).build();
    }

    private void finishShowingSheet() {
        glass.removeAll();
        glass.setLayout(null);
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.anchor = GridBagConstraints.NORTH;

        sheet.setBounds(glass.getInsets().left + glass.getWidth() / 2 - sheet.getWidth() / 2, glass.getInsets().top + 100, sheet.getWidth(), sheet.getHeight());
        glass.add(sheet);
//        gbc.gridy = 2;
//        gbc.weighty = Integer.MAX_VALUE;
//        glass.add(Box.createGlue(), gbc);
        glass.revalidate();
        glass.repaint();
    }

    class AnimatingSheet extends JPanel {
        Dimension animatingSize = new Dimension(0, 1);
        JComponent source;
        BufferedImage offscreenImage;

        public AnimatingSheet() {
            super();
            setOpaque(true);
        }

        public void setSource(JComponent source) {
            this.source = source;
            animatingSize.width = source.getWidth();
            makeOffscreenImage(source);
        }

        public void setAnimatingHeight(int height) {

            animatingSize.height = height;
            setSize(animatingSize);
        }

        private void makeOffscreenImage(JComponent source) {
            GraphicsConfiguration gfxConfig =
                    GraphicsEnvironment.getLocalGraphicsEnvironment()
                            .getDefaultScreenDevice()
                            .getDefaultConfiguration();
            offscreenImage =
                    gfxConfig.createCompatibleImage(source.getWidth(),
                            source.getHeight());
            Graphics2D offscreenGraphics =
                    (Graphics2D) offscreenImage.getGraphics();

        }

        public Dimension getPreferredSize() {
            return animatingSize;
        }

        public Dimension getMinimumSize() {
            return animatingSize;
        }

        public Dimension getMaximumSize() {
            return animatingSize;
        }

        public void paint(Graphics g) {
            // get the bottom-most n pixels of source and
            // paint them into g, where n is height

//            OPDE.debug(source.getWidth() +", " + animatingSize.height);


            BufferedImage fragment;
            try {
                fragment = offscreenImage.getSubimage(0, offscreenImage.getHeight() - animatingSize.height, source.getWidth(), animatingSize.height);
            } catch (RasterFormatException rfe){
                fragment = offscreenImage;
            }
            // g.drawImage (fragment, 0, 0, this);
            g.drawImage(fragment, 0, 0, this);
        }
    }
}