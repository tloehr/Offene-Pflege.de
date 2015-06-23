package gui.interfaces;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideButton;
import gui.GUITools;
import gui.events.ContentRequestedEvent;
import gui.events.ContentRequestedEventListener;
import gui.events.DataChangeEvent;
import gui.events.DataChangeListener;
import op.OPDE;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 08.09.12
 * Time: 11:57
 * To change this template use File | Settings | File Templates.
 */
public class DefaultCollapsiblePane<T> extends CollapsiblePane implements DataChangeListener<T> {
    //    private final Closure contentProvider;
    private final ContentRequestedEventListener headerUpdate, contentUpdate;


    JPanel titlePanelleft, titlePanelright, titlePanel, additionalIconPanel;
    final JideButton btnTitle;
    boolean flashAfterEdit = false;
    final long id = System.nanoTime();

    ActionListener defaultActionListener;

    private final DefaultCollapsiblePane thisPane;
    private Logger logger;

    public DefaultCollapsiblePane(ContentRequestedEventListener headerUpdate, ContentRequestedEventListener contentUpdate) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        this(headerUpdate, contentUpdate, null);
    }

    public DefaultCollapsiblePane(ContentRequestedEventListener headerUpdate, ContentRequestedEventListener contentUpdate, JPanel menuPanel) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        super();
        this.headerUpdate = headerUpdate;
        this.contentUpdate = contentUpdate;

        setStyle(CollapsiblePane.TREE_STYLE);

        thisPane = this;
        logger = Logger.getLogger(getClass() + ": id");
//        logger.setLevel(Level.DEBUG);

        additionalIconPanel = new JPanel();
        additionalIconPanel.setLayout(new BoxLayout(additionalIconPanel, BoxLayout.LINE_AXIS));
        additionalIconPanel.setOpaque(false);

        titlePanelleft = new JPanel();
        titlePanelleft.setLayout(new BoxLayout(titlePanelleft, BoxLayout.LINE_AXIS));

        btnTitle = GUITools.createHyperlinkButton("", null, null);
        btnTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanelleft.add(additionalIconPanel);
        titlePanelleft.add(btnTitle);

        titlePanelright = new JPanel();
        titlePanelright.setLayout(new BoxLayout(titlePanelright, BoxLayout.LINE_AXIS));

        titlePanelleft.setOpaque(false);
        titlePanelright.setOpaque(false);

        titlePanel = new JPanel();
        titlePanel.setOpaque(false);

        titlePanel.setLayout(new GridBagLayout());
        ((GridBagLayout) titlePanel.getLayout()).columnWidths = new int[]{0, 80};
        ((GridBagLayout) titlePanel.getLayout()).columnWeights = new double[]{1.0, 1.0};

        titlePanel.add(titlePanelleft, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 5), 0, 0));

        titlePanel.add(titlePanelright, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 0), 0, 0));


        defaultActionListener = e -> {
//            logger.debug(btnTitle.getName() + " " + btnTitle.getText());
            setCollapsed(!isCollapsed());
        };
        btnTitle.addActionListener(defaultActionListener);
        btnTitle.setName("btn" + id);

        setCollapsible(true);
        setCollapsed(true);
        setSlidingDirection(SwingConstants.SOUTH);
        setBackground(Color.white);

        setTitleLabelComponent(titlePanel);

        if (menuPanel != null) {

            final JButton btnMenu = new JButton(SYSConst.icon22menu);
            btnMenu.setPressedIcon(SYSConst.icon22Pressed);
            btnMenu.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnMenu.setAlignmentY(Component.TOP_ALIGNMENT);
            btnMenu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnMenu.setContentAreaFilled(false);
            btnMenu.setBorder(null);
            btnMenu.addActionListener(e -> {
                JidePopup popup = new JidePopup();
                popup.setMovable(false);
                popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
                popup.setOwner(btnMenu);
                popup.removeExcludedComponent(btnMenu);

                popup.getContentPane().add(menuPanel);
                popup.setDefaultFocusComponent(menuPanel);

                GUITools.showPopup(popup, SwingConstants.WEST);
            });
            titlePanelright.add(btnMenu);
        }

        reload();

        flashAfterEdit = true;

    }

    @Override
    public void dataChanged(DataChangeEvent evt) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        headerUpdate.contentRequested(new ContentRequestedEvent(thisPane));
    }


    public void reload() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        headerUpdate.contentRequested(new ContentRequestedEvent(thisPane));
        contentUpdate.contentRequested(new ContentRequestedEvent(thisPane));
    }

    public void addTitleButtonActionListener(ActionListener actionListener) {
        btnTitle.removeActionListener(defaultActionListener);
        btnTitle.addActionListener(actionListener);
    }

    public JPanel getAdditionalIconPanel() {
        return additionalIconPanel;
    }

    public JPanel getLeft() {
        return titlePanelleft;
    }

    @Override
    public void setCollapsed(boolean b) {
        try {
            super.setCollapsed(b);
        } catch (PropertyVetoException pve) {
            OPDE.warn(pve);
        }
    }

    public JPanel getRight() {
        return titlePanelright;
    }

    public JPanel getMain() {
        return titlePanel;
    }

//    @Override
//    public void reload() {
//
//    }

    public void setTitleButtonText(String text) {
        btnTitle.setText(SYSTools.xx(text));
        if (flashAfterEdit) {
            GUITools.flashIcon(btnTitle, SYSConst.icon22apply);
        }
    }

    public JideButton getTitleButton() {
        return btnTitle;
    }
}
