package op.tools;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideButton;
import entity.info.BWInfoTools;
import entity.info.Resident;
import entity.info.ResidentTools;
import op.OPDE;
import op.care.sysfiles.PnlFiles;
import op.system.FileDrop;
import org.apache.commons.collections.Closure;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.util.MissingResourceException;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 16.06.11
 * Time: 14:46
 * To change this template use File | Settings | File Templates.
 */
public class GUITools {

    public static JideButton createHyperlinkButton(String name, Icon icon, ActionListener actionListener) {
        final JideButton button = new JideButton(name, icon);
        button.setButtonStyle(JideButton.HYPERLINK_STYLE);
        button.setFont(SYSConst.ARIAL14);

        button.setOpaque(false);
        button.setHorizontalAlignment(SwingConstants.LEADING);

        button.setRequestFocusEnabled(true);
        button.setFocusable(true);

        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (actionListener != null) {
            button.addActionListener(actionListener);
        }
        return button;
    }

    public static void addAllComponents(JPanel panel, java.util.List<Component> componentList) {
        for (Component component : componentList) {
            panel.add(component);
        }
    }

    public static MouseAdapter getHyperlinkStyleMouseAdapter() {
        return new MouseAdapter() {
            String text = "";

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                text = SYSTools.removeTags(((javax.swing.AbstractButton) mouseEvent.getSource()).getText(), "html");
                ((javax.swing.AbstractButton) mouseEvent.getSource()).setText("<html><u>" + text + "</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                ((javax.swing.AbstractButton) mouseEvent.getSource()).setText(SYSTools.toHTMLForScreen(text));
            }
        };
    }

    public static JToggleButton getNiceToggleButton(String titleORlangbundle) {
        String title = SYSTools.catchNull(titleORlangbundle);
        try {
            title = OPDE.lang.getString(titleORlangbundle);
        } catch (Exception e){
            // ok, its not a langbundle key
        }

        JToggleButton tb = new JToggleButton(title);
        tb.setIcon(new ImageIcon(tb.getClass().getResource("/artwork/22x22/cb-off.png")));
        tb.setSelectedIcon(new ImageIcon(tb.getClass().getResource("/artwork/22x22/cb-on.png")));
        tb.setContentAreaFilled(false);
        tb.setBorderPainted(false);
        tb.setBorder(null);
        tb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tb.setBackground(Color.WHITE);
        tb.setAlignmentX(Component.LEFT_ALIGNMENT);
        return tb;
    }

    /**
     * Shows a JidePopup in relation to its owner. Calculates the new position that it leaves the owner
     * visible. The popup is placed according to the <code>location</code> setting. The size of the content
     * pane is taken into the calculation in order to find the necessary <code>x, y</code> coordinates on the screen.
     * <p/>
     * <ul>
     * <li>SwingConstants.CENTER <i>You can use this, but I fail to see the sense in it.</i></li>
     * <li>SwingConstants.SOUTH</li>
     * <li>SwingConstants.NORTH</li>
     * <li>SwingConstants.WEST</li>
     * <li>SwingConstants.EAST</li>
     * <li>SwingConstants.NORTH_EAST</li>
     * <li>SwingConstants.NORTH_WEST</li>
     * <li>SwingConstants.SOUTH_EAST</li>
     * <li>SwingConstants.SOUTH_WEST</li>
     * </ul>
     *
     * @param popup    the JidePopup to show
     * @param location where to show the popup in relation to the <code>reference</code>. Use the SwingConstants above.
     */
    public static void showPopup(JidePopup popup, int location, boolean keepOnScreen) {
        Container content = popup.getContentPane();
        Point p2 = new Point(popup.getOwner().getX(), popup.getOwner().getY());
        SwingUtilities.convertPointToScreen(p2, popup.getOwner());
        final Point screenposition = p2;

        int x = screenposition.x;
        int y = screenposition.y;

        switch (location) {
            case SwingConstants.SOUTH_WEST: {
                x = screenposition.x - content.getPreferredSize().width;
                y = screenposition.y + popup.getOwner().getPreferredSize().height;
                break;
            }
            case SwingConstants.SOUTH_EAST: {
                x = screenposition.x + popup.getOwner().getPreferredSize().width;
                y = screenposition.y + popup.getOwner().getPreferredSize().height;
                break;
            }
            case SwingConstants.NORTH_EAST: {
                x = screenposition.x + popup.getOwner().getPreferredSize().width;
                y = screenposition.y - popup.getOwner().getPreferredSize().height - content.getPreferredSize().height;
                break;
            }
            case SwingConstants.NORTH_WEST: {
                x = screenposition.x - content.getPreferredSize().width - popup.getOwner().getPreferredSize().width;
                y = screenposition.y - popup.getOwner().getPreferredSize().height - content.getPreferredSize().height;
                break;
            }
            case SwingConstants.EAST: {
                x = screenposition.x + popup.getOwner().getPreferredSize().width;
                y = screenposition.y;
                break;
            }
            case SwingConstants.WEST: {
                x = screenposition.x - content.getPreferredSize().width;
                y = screenposition.y;
                break;
            }
            case SwingConstants.NORTH: {
                x = screenposition.x;
                y = screenposition.y - content.getPreferredSize().height;
                break;
            }
            case SwingConstants.CENTER: {
                x = screenposition.x + popup.getOwner().getPreferredSize().width / 2 - content.getPreferredSize().width / 2;
                y = screenposition.y + popup.getOwner().getPreferredSize().height / 2 - content.getPreferredSize().height / 2;
                break;
            }
            default: {
                // nop
            }
        }
        popup.showPopup(x, y);
    }

    public static void showPopup(JidePopup popup, int location) {
        showPopup(popup, location, true);
    }

    public static JPanel getDropPanel(FileDrop.Listener dropListener) {
        JPanel dropPanel = new JPanel();
        dropPanel.setLayout(new BorderLayout());
        JLabel dropLabel = new JLabel(OPDE.lang.getString(PnlFiles.internalClassID + ".drophere"), new ImageIcon(Double.class.getResource("/artwork/48x48/kget_dock.png")), SwingConstants.CENTER);
        dropLabel.setFont(SYSConst.ARIAL20);
        dropLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        dropLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        dropPanel.add(BorderLayout.CENTER, dropLabel);
        dropPanel.setPreferredSize(new Dimension(180, 180));
        dropPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));


        new FileDrop(dropPanel, dropListener);
        return dropPanel;
    }

    public static void setBWDisplay(Resident bewohner) {
        OPDE.getDisplayManager().setMainMessage(ResidentTools.getLabelText(bewohner));
        if (BWInfoTools.isAbwesend(bewohner)) {
            OPDE.getDisplayManager().setIconAway();
        } else if (BWInfoTools.isVerstorben(bewohner)) {
            OPDE.getDisplayManager().setIconDead();
        } else if (BWInfoTools.isAusgezogen(bewohner)) {
            OPDE.getDisplayManager().setIconGone();
        }
    }

    public static String[] getLocalizedMessages(String[] languagekeys) {
        String[] result = new String[languagekeys.length];
        int index = 0;
        for (String key : languagekeys) {
            result[index] = OPDE.lang.getString(key);
            index++;
        }
        return result;
    }

    public static void setCollapsed(Container root, boolean collapsed) throws PropertyVetoException {
        if (root instanceof CollapsiblePane) {
            ((CollapsiblePane) root).setCollapsed(collapsed);
        }
        for (Component component : root.getComponents()) {
            if (component instanceof Container) {
                setCollapsed((Container) component, collapsed);
            }
        }
    }

    public static void scroll2show(final JScrollPane jsp, final Component component, Container container, final Closure what2doAfterwards) {

        final int start = jsp.getVerticalScrollBar().getValue();
        final int end = SwingUtilities.convertPoint(component, component.getLocation(), container).y;
        final int distance = end - start;

        final TimingSource ts = new SwingTimerTimingSource();
        Animator.setDefaultTimingSource(ts);
        ts.init();

        Animator animator = new Animator.Builder().setInterpolator(new AccelerationInterpolator(0.15f, 0.8f)).setDuration(750, TimeUnit.MILLISECONDS).setStartDirection(Animator.Direction.FORWARD).addTarget(new TimingTargetAdapter() {
            @Override
            public void begin(Animator source) {
            }

            @Override
            public void timingEvent(Animator animator, double fraction) {
                final BigDecimal value = new BigDecimal(start).add(new BigDecimal(fraction).multiply(new BigDecimal(distance)));
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        jsp.getVerticalScrollBar().setValue(value.intValue());
                    }
                });
            }

            @Override
            public void end(Animator source) {
                component.repaint();
                what2doAfterwards.execute(null);
            }
        }).build();
        animator.start();

//        jsp.getVerticalScrollBar().setValue(Math.min(SwingUtilities.convertPoint(component, component.getLocation(), container).y, jsp.getVerticalScrollBar().getMaximum()));
    }


    public static void flashBackground(final JComponent component, final Color flashcolor, int repeatTimes) {
        final Color originalColor = component.getBackground();
        final TimingSource ts = new SwingTimerTimingSource();
        Animator.setDefaultTimingSource(ts);
        ts.init();
        component.setOpaque(true);
        Animator animator = new Animator.Builder().setDuration(750, TimeUnit.MILLISECONDS).setRepeatCount(repeatTimes).setRepeatBehavior(Animator.RepeatBehavior.REVERSE).setStartDirection(Animator.Direction.FORWARD).addTarget(new TimingTargetAdapter() {
            @Override
            public void begin(Animator source) {
            }

            @Override
            public void timingEvent(Animator animator, final double fraction) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        // interpolateColor(component.getBackground(), flashcolor, fraction)
                        component.setBackground(interpolateColor(originalColor, flashcolor, fraction));
                        component.repaint();
                    }
                });
            }

            @Override
            public void end(Animator source) {
                component.setOpaque(false);
                component.repaint();
//                SwingUtilities.invokeLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        // interpolateColor(component.getBackground(), flashcolor, fraction)
////                        component.setBackground(originalColor);
//
//                    }
//                });
            }
        }).build();
        animator.start();

//        jsp.getVerticalScrollBar().setValue(Math.min(SwingUtilities.convertPoint(component, component.getLocation(), container).y, jsp.getVerticalScrollBar().getMaximum()));
    }

    /**
     * @param distance a double between 0.0f and 1.0f to express the distance between the source and destination color
     *                 see http://stackoverflow.com/questions/27532/generating-gradients-programatically
     * @return
     */
    public static Color interpolateColor(Color source, Color destination, double distance) {
        int red = (int) (destination.getRed() * distance + source.getRed() * (1 - distance));
        int green = (int) (destination.getGreen() * distance + source.getGreen() * (1 - distance));
        int blue = (int) (destination.getBlue() * distance + source.getBlue() * (1 - distance));
        return new Color(red, green, blue);
    }
}
