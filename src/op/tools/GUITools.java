package op.tools;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideButton;
import entity.info.*;
import op.OPDE;
import op.system.FileDrop;
import op.threads.DisplayMessage;
import org.apache.commons.collections.Closure;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 16.06.11
 * Time: 14:46
 * To change this template use File | Settings | File Templates.
 */
public class GUITools {

    public static void exportToPNG(JPanel pnl, File output) {
        BufferedImage bi = new BufferedImage(pnl.getSize().width, pnl.getSize().height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.createGraphics();
        pnl.paint(g);
        g.dispose();
        try {
            ImageIO.write(bi, "png", output);
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("PNG exported"));
        } catch (Exception e) {
        }

    }


    public static ByteArrayOutputStream getAsImage(JPanel pnl) throws Exception {
        BufferedImage bi = new BufferedImage(pnl.getPreferredSize().width, pnl.getPreferredSize().height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.createGraphics();
        pnl.paint(g);
        g.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", baos);
        return baos;
    }

    public static void exportToPNG(JPanel pnl, String prefix) {
        if (prefix == null) {
            prefix = "pnl2png";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        exportToPNG(pnl, new File(OPDE.getOPWD() + File.separator + OPDE.SUBDIR_CACHE + File.separator + prefix + "_" + sdf.format(new Date()) + ".png"));
    }

    public static JideButton createHyperlinkButton(String titleORlangbundle, Icon icon, ActionListener actionListener) {

        final JideButton button = new JideButton(SYSTools.xx(titleORlangbundle), icon);
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
            title = SYSTools.xx(titleORlangbundle);
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


    public static JButton getTinyButton(String tooltip, Icon icon){
        JButton jButton = new JButton(icon);
        jButton.setContentAreaFilled(false);
        jButton.setBorder(null);
        jButton.setBorderPainted(false);
        jButton.setToolTipText(SYSTools.xx(tooltip));
        jButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return jButton;
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


        Point desiredPosition = getDesiredPosition(popup, location);

//        OPDE.debug(Boolean.toString(isFullyVisibleOnScreen(popup, desiredPosition)));


        if (keepOnScreen && !isFullyVisibleOnScreen(popup, desiredPosition)) {
            int[] positions = new int[]{SwingConstants.SOUTH_EAST, SwingConstants.SOUTH_WEST, SwingConstants.NORTH_EAST, SwingConstants.NORTH_WEST, SwingConstants.SOUTH, SwingConstants.EAST, SwingConstants.SOUTH_WEST, SwingConstants.NORTH, SwingConstants.CENTER};
            boolean found = false;

            for (int pos : positions) {
                desiredPosition = getDesiredPosition(popup, pos);
                if (isFullyVisibleOnScreen(popup, desiredPosition)) {
                    found = true;
                    OPDE.debug("fits on screen");
                    break;
                }
            }

            if (!found) {
                // desiredPosition = getDesiredPosition(popup, location);
                desiredPosition = centerOnScreen(popup);
                OPDE.debug("didnt find any position thats on the screen");
            }

        }

        popup.showPopup(desiredPosition.x, desiredPosition.y);

    }

    private static Point centerOnScreen(JidePopup popup) {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();

        int midx = width / 2;
        int midy = height / 2;


        int x = midx - popup.getContentPane().getPreferredSize().width / 2;
        int y = midy - popup.getContentPane().getPreferredSize().height / 2;

        return new Point(x, y);
    }

    private static Point getDesiredPosition(JidePopup popup, int location) {
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
        return new Point(x, y);
    }

    public static boolean isFullyVisibleOnScreen(JidePopup popup, Point point) {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();

        int spreadX = point.x + popup.getContentPane().getPreferredSize().width;
        int spreadY = point.y + popup.getContentPane().getPreferredSize().height;

        OPDE.debug("PointX: " + point.x);
        OPDE.debug("PointY: " + point.y);

        return point.x >= 0 && point.y >= 0 && width > spreadX && height > spreadY;

    }

    public static void showPopup(JidePopup popup, int location) {
        showPopup(popup, location, true);
    }


    public static JPanel getDropPanel(FileDrop.Listener dropListener) {
        return getDropPanel(dropListener, SYSTools.xx("nursingrecords.files.drophere"));
    }

    public static JPanel getDropPanel(FileDrop.Listener dropListener, String text) {
        JPanel dropPanel = new JPanel();
        dropPanel.setLayout(new BorderLayout());
        JLabel dropLabel = new JLabel(text, new ImageIcon(Double.class.getResource("/artwork/48x48/kget_dock.png")), SwingConstants.CENTER);
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
        ResInfo biohazard = ResInfoTools.getLastResinfo(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_INFECTION));
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
            result[index] = SYSTools.xx(key);
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

    public static void expand(CollapsiblePane cp) throws PropertyVetoException {
//        ArrayList<CollapsiblePane> path = new ArrayList<CollapsiblePane>();
//        path.add(cp);
        cp.setCollapsed(false);
        Container cont = cp.getParent();
        while (cont != null) {
            if (cont instanceof CollapsiblePane) {
                ((CollapsiblePane) cont).setCollapsed(false);
            }
            cont = cont.getParent();
        }
//
//
//        if (root instanceof CollapsiblePane) {
//            if (((CollapsiblePane) root).isCollapsible()) {
//                ((CollapsiblePane) root).setCollapsed(collapsed);
//            }
//        }
//        for (Component component : root.getComponents()) {
//            if (component instanceof Container) {
//                setCollapsed((Container) component, collapsed);
//            }
//        }
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

    public static void scroll2show(final JScrollPane jsp, final JComponent component, Container container, final Closure what2doAfterwards) {
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
                public void timingEvent(final Animator animator, double fraction) {
                    final BigDecimal value = new BigDecimal(start).add(new BigDecimal(fraction).multiply(new BigDecimal(distance)));
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            Rectangle r = component.getVisibleRect();
                            if (r.getSize().equals(component.getSize())) {
                                animator.stop();
                            } else if (r.isEmpty()) {
                                OPDE.debug("not visible");
                                jsp.getVerticalScrollBar().setValue(value.intValue());
                            } else {
                                OPDE.debug("partly visible");
                                jsp.getVerticalScrollBar().setValue(value.intValue());
                            }
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
        final boolean wasOpaque = component.isOpaque();
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
                component.setOpaque(wasOpaque);
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

    public static Animator flashIcon(final AbstractButton btn, final Icon icon) {


        final Icon originalIcon = btn.isSelected() ? btn.getSelectedIcon() : btn.getIcon();

        final TimingSource ts = new SwingTimerTimingSource();
        Animator.setDefaultTimingSource(ts);
        ts.init();

        Animator animator = new Animator.Builder().setDuration(750, TimeUnit.MILLISECONDS).setRepeatCount(Animator.INFINITE).setRepeatBehavior(Animator.RepeatBehavior.REVERSE).setStartDirection(Animator.Direction.FORWARD).addTarget(new TimingTargetAdapter() {
            @Override
            public void begin(Animator source) {

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (btn.isSelected()) {
                            btn.setSelectedIcon(icon);
                        } else {
                            btn.setIcon(icon);
                        }

                    }
                });


            }

            @Override
            public void reverse(Animator source) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (btn.isSelected()) {
                            btn.setSelectedIcon(icon);
                        } else {
                            btn.setIcon(icon);
                        }

                    }
                });
            }

            @Override
            public void repeat(Animator source) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (btn.isSelected()) {
                            btn.setSelectedIcon(originalIcon);
                        } else {
                            btn.setIcon(originalIcon);
                        }

                    }
                });
            }

            @Override
            public void end(Animator source) {
                repeat(source);
            }
        }).build();


        return animator;
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
        btnExpandAll.setToolTipText(SYSTools.xx("misc.msg.expandall"));
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
        btnCollapseAll.setToolTipText(SYSTools.xx("misc.msg.collapseall"));
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

    public static JidePopup getTextEditor(String preset, int rows, int cols, final Closure saveClosure, Component owner) {

        final JidePopup popup = new JidePopup();
        popup.setMovable(false);
        popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));

        final JTextComponent editor = rows == 1 ? new JTextField(preset, cols) : new JTextArea(preset, rows, cols);
        editor.setFont(SYSConst.ARIAL18);
        if (rows > 1) {
            ((JTextArea) editor).setLineWrap(false);
            ((JTextArea) editor).setWrapStyleWord(false);
        }
        popup.getContentPane().add(new JScrollPane(editor));
        final JButton saveButton = new JButton(SYSConst.icon16apply);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popup.hidePopup();
                saveClosure.execute(SYSTools.tidy(editor.getText()));
            }
        });

        saveButton.setHorizontalAlignment(SwingConstants.RIGHT);
        JPanel pnl = new JPanel(new BorderLayout(10, 10));
        JScrollPane pnlEditor = new JScrollPane(editor);

        pnl.add(pnlEditor, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        saveButton.setContentAreaFilled(false);
        saveButton.setBorder(null);
        saveButton.setBorderPainted(false);
        buttonPanel.add(saveButton);
        pnl.setBorder(new EmptyBorder(10, 10, 10, 10));
        pnl.add(buttonPanel, BorderLayout.EAST);

        popup.setOwner(owner);
        popup.removeExcludedComponent(owner);
        popup.getContentPane().add(pnl);
        popup.setDefaultFocusComponent(editor);

        return popup;
    }

    public static JidePopup createPanelPopup(final PopupPanel myPnl, final Closure saveAction, Component owner) {
        final JidePopup popup = new JidePopup();
        popup.setMovable(false);
        JPanel pnl = new JPanel(new BorderLayout(10, 10));
        pnl.setBorder(new EmptyBorder(5, 5, 5, 5));
        pnl.add(myPnl, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));

        JButton saveButton = new JButton(SYSConst.icon22apply);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (myPnl.isSaveOK()) {
                    popup.hidePopup();
                    saveAction.execute(myPnl.getResult());
                }
            }
        });
        saveButton.setContentAreaFilled(false);
        saveButton.setBorder(null);
        saveButton.setBorderPainted(false);
        btnPanel.add(Box.createHorizontalGlue());
        btnPanel.add(saveButton);
        pnl.add(btnPanel, BorderLayout.SOUTH);

        popup.setContentPane(pnl);
        popup.setPreferredSize(pnl.getPreferredSize());
        pnl.revalidate();
        popup.setOwner(owner);
        popup.removeExcludedComponent(owner);
//        popup.removeExcludedComponent(pnl);
        popup.setDefaultFocusComponent(pnl);
        return popup;
    }

    public static boolean containsEmpty(ArrayList<JTextComponent> list) {
        boolean result = false;
        for (JTextComponent comp : list) {
            if (comp.getText().trim().isEmpty()) {
                result = true;
                break;
            }
        }
        return result;
    }

    public static FocusTraversalPolicy createTraversalPolicy(final ArrayList<Component> list) {
        FocusTraversalPolicy myPolicy = new FocusTraversalPolicy() {

            @Override
            public Component getComponentAfter(Container aContainer, Component aComponent) {
                int pos = list.indexOf(aComponent) + 1;
                if (pos == list.size()) {
                    pos = 0;
                }
                return list.get(pos);
            }

            @Override
            public Component getComponentBefore(Container aContainer, Component aComponent) {
                int pos = list.indexOf(aComponent) - 1;
                if (pos < 0) {
                    pos = list.size() - 1;
                }
                return list.get(pos);
            }

            @Override
            public Component getFirstComponent(Container aContainer) {
                return list.get(0);
            }

            @Override
            public Component getLastComponent(Container aContainer) {
                return list.get(list.size() - 1);
            }

            @Override
            public Component getDefaultComponent(Container aContainer) {
                return list.get(0);
            }
        };
        return myPolicy;
    }

    public static void load(Properties content, java.util.List<Component> components) {
        for (Component comp : components) {
            if (comp instanceof JTextComponent) {
                ((JTextComponent) comp).setText(content.getProperty(comp.getName()));
            } else if (comp instanceof AbstractButton) {
                ((AbstractButton) comp).setSelected(Boolean.parseBoolean(SYSTools.catchNull(content.getProperty(comp.getName()), "false")));
            }
        }
    }

    public static void save(Properties content, java.util.List<Component> components) {
        for (Component comp : components) {
            if (comp instanceof JTextComponent) {
                content.setProperty(comp.getName(), ((JTextComponent) comp).getText());
            } else if (comp instanceof AbstractButton) {
                content.setProperty(comp.getName(), Boolean.toString(((AbstractButton) comp).isSelected()));
            }
        }
    }

    public static Color invert(Color color) {
        return new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
    }

    /**
     * http://stackoverflow.com/questions/8741479/automatically-determine-optimal-fontcolor-by-backgroundcolor
     *
     * @param background
     * @return
     */
    public static Color getForeground(Color background) {
        int red = 0;
        int green = 0;
        int blue = 0;

        if (background.getRed() + background.getGreen() + background.getBlue() < 383) {
            red = 255;
            green = 255;
            blue = 255;
        }
        return new Color(red, green, blue);
    }

    /**
     * http://stackoverflow.com/questions/4059133/getting-html-color-codes-with-a-jcolorchooser
     *
     * @param c
     * @return
     */
    public static String toHexString(Color c) {
        StringBuilder sb = new StringBuilder("");

        if (c.getRed() < 16) sb.append('0');
        sb.append(Integer.toHexString(c.getRed()));

        if (c.getGreen() < 16) sb.append('0');
        sb.append(Integer.toHexString(c.getGreen()));

        if (c.getBlue() < 16) sb.append('0');
        sb.append(Integer.toHexString(c.getBlue()));

        return sb.toString();
    }

    /**
     * Creates a Color object according to the names of the Java color constants.
     * A HTML color string like "62A9FF" may also be used. Please remove the leading "#".
     *
     * @param colornameOrHTMLCode
     * @return the desired color. Defaults to BLACK, in case of an error.
     */
    public static Color getColor(String colornameOrHTMLCode) {
        Color color = Color.black;

        if (colornameOrHTMLCode.equalsIgnoreCase("red")) {
            color = Color.red;
        } else if (colornameOrHTMLCode.equalsIgnoreCase("blue")) {
            color = Color.blue;
        } else if (colornameOrHTMLCode.equalsIgnoreCase("dark_red")) {
            color = Color.red.darker();
        } else if (colornameOrHTMLCode.equalsIgnoreCase("green")) {
            color = Color.green;
        } else if (colornameOrHTMLCode.equalsIgnoreCase("dark_green")) {
            color = Color.green.darker();
        } else if (colornameOrHTMLCode.equalsIgnoreCase("yellow")) {
            color = Color.yellow;
        } else if (colornameOrHTMLCode.equalsIgnoreCase("cyan")) {
            color = Color.CYAN;
        } else if (colornameOrHTMLCode.equalsIgnoreCase("light_gray")) {
            color = Color.LIGHT_GRAY;
        } else if (colornameOrHTMLCode.equalsIgnoreCase("dark_gray")) {
            color = Color.DARK_GRAY;
        } else if (colornameOrHTMLCode.equalsIgnoreCase("gray")) {
            color = Color.GRAY;
        } else if (colornameOrHTMLCode.equalsIgnoreCase("pink")) {
            color = Color.PINK;
        } else if (colornameOrHTMLCode.equalsIgnoreCase("magenta")) {
            color = Color.MAGENTA;
        } else if (colornameOrHTMLCode.equalsIgnoreCase("white")) {
            color = Color.WHITE;
        } else if (colornameOrHTMLCode.equalsIgnoreCase("orange")) {
            color = SYSConst.gold7;
        } else if (colornameOrHTMLCode.equalsIgnoreCase("dark_orange")) {
            color = SYSConst.darkorange;
        } else {
            try {
                int red = Integer.parseInt(colornameOrHTMLCode.substring(0, 2), 16);
                int green = Integer.parseInt(colornameOrHTMLCode.substring(2, 4), 16);
                int blue = Integer.parseInt(colornameOrHTMLCode.substring(4), 16);
                color = new Color(red, green, blue);
            } catch (NumberFormatException nfe) {
                color = Color.BLACK;
            }
        }
        return color;
    }

    /**
     * creates a blend between two colors. The float specifies where the balance is.
     * the more towards 1.0 emphasizes the <b>first</b> color.
     * the more towards 0.0 emphasizes the <b>second</b> color.
     * @param clOne
     * @param clTwo
     * @param fAmount
     * @return
     */
    public static Color blend(Color clOne, Color clTwo, float fAmount) {
        float fInverse = 1.0f - fAmount;

        // I had to look up getting colour components in java.  Google is good :)
        float afOne[] = new float[3];
        clOne.getColorComponents(afOne);
        float afTwo[] = new float[3];
        clTwo.getColorComponents(afTwo);

        float afResult[] = new float[3];
        afResult[0] = afOne[0] * fAmount + afTwo[0] * fInverse;
        afResult[1] = afOne[1] * fAmount + afTwo[1] * fInverse;
        afResult[2] = afOne[2] * fAmount + afTwo[2] * fInverse;

        return new Color(afResult[0], afResult[1], afResult[2]);
    }

//    public static Color brighter(Color originalColour, float FACTOR) {
//
//
//        float hsbVals[] = Color.RGBtoHSB(originalColour.getRed(),
//                originalColour.getGreen(),
//                originalColour.getBlue(), null);
//
//        Color highlight = Color.getHSBColor(hsbVals[0], hsbVals[1], FACTOR * (1f + hsbVals[2]));
////            Color shadow = Color.getHSBColor( hsbVals[0], hsbVals[1], 0.5f * hsbVals[2] );
//
//        return highlight;
//
//
////        return new Color(Math.min((int) (color.getRed() * (1 / FACTOR)), 255),
////                Math.min((int) (color.getGreen() * (1 / FACTOR)), 255),
////                Math.min((int) (color.getBlue() * (1 / FACTOR)), 255));
//    }
//
//    public static Color darker(Color color, float FACTOR) {
//        return new Color(Math.max((int) (color.getRed() * FACTOR), 0),
//                Math.max((int) (color.getGreen() * FACTOR), 0),
//                Math.max((int) (color.getBlue() * FACTOR), 0));
//    }
//
//
//    static Image iconToImage(Icon icon) {
//       if (icon instanceof ImageIcon) {
//          return ((ImageIcon)icon).getImage();
//       }
//       else {
//          int w = icon.getIconWidth();
//          int h = icon.getIconHeight();
//          GraphicsEnvironment ge =
//            GraphicsEnvironment.getLocalGraphicsEnvironment();
//          GraphicsDevice gd = ge.getDefaultScreenDevice();
//          GraphicsConfiguration gc = gd.getDefaultConfiguration();
//          BufferedImage image = gc.createCompatibleImage(w, h);
//          Graphics2D g = image.createGraphics();
//          icon.paintIcon(null, g, 0, 0);
//          g.dispose();
//          return image;
//       }
//     }


//    public static Icon paint(Icon in) {
//       Image myImage = iconToImage(in);
//       BufferedImage bufferedImage = new BufferedImage(myImage.getWidth(null), myImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
//
//        GraphicsEnvironment ge =
//                GraphicsEnvironment.getLocalGraphicsEnvironment();
//              GraphicsDevice gd = ge.getDefaultScreenDevice();
//              GraphicsConfiguration gc = gd.getDefaultConfiguration();
//              BufferedImage image = gc.createCompatibleImage(in.getIconWidth(), in.getIconHeight());
//              Graphics2D g = image.createGraphics();
//
//
//       Graphics gb = bufferedImage.getGraphics();
//       gb.drawImage(myImage, 0, 0, null);
//       gb.dispose();
//
//       AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
//       tx.translate(-myImage.getWidth(null), 0);
//       AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
//       bufferedImage = op.filter(bufferedImage, null);
//
//
//
//       g2d.drawImage(myImage, 10, 10, null);
//       g2d.drawImage(bufferedImage, null, 300, 10);
//     }
}
