package op.tools;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideButton;
import entity.info.*;
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
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 16.06.11
 * Time: 14:46
 * To change this template use File | Settings | File Templates.
 */
public class GUITools {

    public static JideButton createHyperlinkButton(String titleORlangbundle, Icon icon, ActionListener actionListener) {
        String title = SYSTools.catchNull(titleORlangbundle);
        try {
            title = OPDE.lang.getString(titleORlangbundle);
        } catch (Exception e) {
            // ok, its not a langbundle key
        }
        final JideButton button = new JideButton(title, icon);
        button.setButtonStyle(JideButton.HYPERLINK_STYLE);
        button.setFont(SYSConst.ARIAL14);

        button.setOpaque(false);
        button.setHorizontalAlignment(SwingConstants.LEADING);
//        button.setVerticalAlignment(SwingConstants.TOP);
//        button.setVerticalTextPosition(SwingConstants.TOP);
//        button.setHorizontalTextPosition(SwingConstants.RIGHT);

        button.setRequestFocusEnabled(true);
        button.setFocusable(true);

        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (actionListener != null) {
            button.addActionListener(actionListener);
        }
        return button;
    }

    public static JTextField createIntegerTextField(final int min, final int max, final int init) {
        final JTextField txt = new JTextField(Integer.toString(init), 10);
        txt.setFont(SYSConst.ARIAL14);
        txt.setHorizontalAlignment(SwingConstants.RIGHT);
        txt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txt.selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                int i = 0;
                try {
                    i = Integer.parseInt(txt.getText());
                } catch (NumberFormatException e1) {
                    txt.setText(Integer.toString(init));
                }

                if (min > i || i > max) {
                    txt.setText(Integer.toString(init));
                }
            }
        });
        return txt;
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
        } catch (Exception e) {
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

        final Point screenposition = new Point(popup.getOwner().getLocationOnScreen().x, popup.getOwner().getLocationOnScreen().y);

        int x = screenposition.x;
        int y = screenposition.y;

        switch (location) {
            case SwingConstants.SOUTH_WEST: {
                x = screenposition.x - content.getPreferredSize().width;
                y = screenposition.y;
                break;
            }
            case SwingConstants.SOUTH_EAST: {
                x = screenposition.x + popup.getOwner().getPreferredSize().width;
                y = screenposition.y;
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
                y = screenposition.y - (popup.getOwner().getPreferredSize().height / 2);
                break;
            }
            case SwingConstants.WEST: {
                x = screenposition.x - content.getPreferredSize().width;
                y = screenposition.y - (popup.getOwner().getPreferredSize().height / 2);
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

    public static void setResidentDisplay(Resident resident) {
        OPDE.getDisplayManager().setMainMessage(ResidentTools.getLabelText(resident), SYSTools.toHTML(ResInfoTools.getTXReportHeader(resident, false)));
        // result += getTXReportHeader(resident, withlongheader);
        ResInfo biohazard = ResInfoTools.getLastResinfo(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_BIOHAZARD));
        ResInfo diabetes = ResInfoTools.getLastResinfo(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_DIABETES));
        ResInfo warning = ResInfoTools.getLastResinfo(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_WARNING));
        ResInfo allergy = ResInfoTools.getLastResinfo(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_ALLERGY));

        OPDE.getDisplayManager().setIconBiohazard(biohazard != null && biohazard.isCurrentlyValid() ? SYSTools.toHTML(SYSConst.html_div(biohazard.getHtml())) : null);
        OPDE.getDisplayManager().setIconDiabetes(diabetes != null && diabetes.isCurrentlyValid() ? SYSTools.toHTML(SYSConst.html_div(diabetes.getHtml())) : null);
        OPDE.getDisplayManager().setIconWarning(warning != null && warning.isCurrentlyValid() ? SYSTools.toHTML(SYSConst.html_div(warning.getHtml())) : null);
        OPDE.getDisplayManager().setIconAllergy(allergy != null && allergy.isCurrentlyValid() ? SYSTools.toHTML(SYSConst.html_div(allergy.getHtml())) : null);

        if (ResInfoTools.isAway(resident)) {
            OPDE.getDisplayManager().setIconAway();
        } else if (ResInfoTools.isDead(resident)) {
            OPDE.getDisplayManager().setIconDead();
        } else if (ResInfoTools.isGone(resident)) {
            OPDE.getDisplayManager().setIconGone();
        }
        OPDE.getDisplayManager().clearSubMessages();
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
            if (((CollapsiblePane) root).isCollapsible()) {
                ((CollapsiblePane) root).setCollapsed(collapsed);
            }
        }
        for (Component component : root.getComponents()) {
            if (component instanceof Container) {
                setCollapsed((Container) component, collapsed);
            }
        }
    }


    public static void scroll2show(final JScrollPane jsp, int end, final Closure what2doAfterwards) {
        final int start = jsp.getVerticalScrollBar().getValue();
        OPDE.debug("scroll2show: trying to move from " + start);
        OPDE.debug("scroll2show: trying to move to " + end);

        end = Math.max(0, end);
        end = Math.min(jsp.getVerticalScrollBar().getMaximum(), end);

        if (OPDE.isAnimation()) {
            final int distance = end - start;
            final TimingSource ts = new SwingTimerTimingSource();
            Animator.setDefaultTimingSource(ts);
            ts.init();

            Animator animator = new Animator.Builder().setInterpolator(new AccelerationInterpolator(0.15f, 0.8f)).setDuration(500, TimeUnit.MILLISECONDS).setStartDirection(Animator.Direction.FORWARD).addTarget(new TimingTargetAdapter() {
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
                    if (what2doAfterwards != null)
                        what2doAfterwards.execute(null);
                }
            }).build();
            animator.start();
        } else {
            final int myend = end;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    jsp.getVerticalScrollBar().setValue(myend);
                }
            });
        }
//        jsp.getVerticalScrollBar().setValue(Math.min(SwingUtilities.convertPoint(component, component.getLocation(), container).y, jsp.getVerticalScrollBar().getMaximum()));
    }

    public static void scroll2show(final JScrollPane jsp, final Component component, Container container, final Closure what2doAfterwards) {
        if (component == null)
            return; // this prevents NULL pointer exceptions when quickly switching the residents after the entry
        final int start = jsp.getVerticalScrollBar().getValue();
        final int end = SwingUtilities.convertPoint(component, component.getLocation(), container).y;
        if (OPDE.isAnimation()) {
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
                    if (what2doAfterwards != null)
                        what2doAfterwards.execute(null);
                }
            }).build();
            animator.start();
        } else {
            final int myend = end;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    jsp.getVerticalScrollBar().setValue(myend);
                }
            });
        }
//        jsp.getVerticalScrollBar().setValue(Math.min(SwingUtilities.convertPoint(component, component.getLocation(), container).y, jsp.getVerticalScrollBar().getMaximum()));
    }


    public static void flashBackground(final JComponent component, final Color flashcolor, int repeatTimes) {
        if (component == null)
            return; // this prevents NULL pointer exceptions when quickly switching the residents after the entry
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

    public static JidePopup getHTMLPopup(Component owner, String html) {
        final JTextPane txt = new JTextPane();
        txt.setContentType("text/html");
        txt.setEditable(false);
        final JidePopup popupInfo = new JidePopup();
        popupInfo.setMovable(false);
        popupInfo.setContentPane(new JScrollPane(txt));
        popupInfo.removeExcludedComponent(txt);
        popupInfo.setDefaultFocusComponent(txt);
        popupInfo.setOwner(owner);
        txt.setText(SYSTools.toHTML(html));
        return popupInfo;
    }

    public static void addExpandCollapseButtons(final Container cp, JPanel pane) {
        final JButton btnExpandAll = new JButton(SYSConst.icon22expand);
        btnExpandAll.setPressedIcon(SYSConst.icon22addPressed);
        btnExpandAll.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnExpandAll.setContentAreaFilled(false);
        btnExpandAll.setBorder(null);
        btnExpandAll.setToolTipText(OPDE.lang.getString("misc.msg.expandall"));
        btnExpandAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    GUITools.setCollapsed(cp, false);
                } catch (PropertyVetoException e) {
                    // bah!
                }
            }


        });
        pane.add(btnExpandAll);

        final JButton btnCollapseAll = new JButton(SYSConst.icon22collapse);
        btnCollapseAll.setPressedIcon(SYSConst.icon22addPressed);
        btnCollapseAll.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnCollapseAll.setContentAreaFilled(false);
        btnCollapseAll.setBorder(null);
        btnCollapseAll.setToolTipText(OPDE.lang.getString("misc.msg.collapseall"));
        btnCollapseAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    GUITools.setCollapsed(cp, true);
                } catch (PropertyVetoException e) {
                    // bah!
                }
            }


        });
        pane.add(btnCollapseAll);

    }
}
