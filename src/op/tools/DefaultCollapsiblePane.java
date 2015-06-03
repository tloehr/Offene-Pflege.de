package op.tools;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.swing.JideButton;
import gui.events.ContentRequestedEvent;
import gui.events.ContentRequestedEventListener;
import gui.events.DataChangeEvent;
import gui.events.DataChangeListener;
import op.OPDE;
import org.apache.commons.collections.Closure;
import org.jdesktop.core.animation.timing.Animator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 08.09.12
 * Time: 11:57
 * To change this template use File | Settings | File Templates.
 */
public class DefaultCollapsiblePane extends CollapsiblePane {
//    private final Closure contentProvider;
    private final ContentRequestedEventListener cre;
    private Animator animator = null;

    JPanel titlePanelleft, titlePanelright, titlePanel, additionalIconPanel;
    JideButton btnTitle;
    boolean flashAfterEdit = true;

    ActionListener defaultActionListener;

    private final DefaultCollapsiblePane thisPane;

    public DefaultCollapsiblePane(ContentRequestedEventListener cre) {
        super();
        this.cre = cre;
        thisPane = this;

        defaultActionListener = e -> {
            setCollapsed(!isCollapsed());
        };

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

        btnTitle.addActionListener(defaultActionListener);

        setCollapsible(true);
        setCollapsed(true);
        setSlidingDirection(SwingConstants.SOUTH);


        setTitleLabelComponent(titlePanel);


        cre.contentRequested(new ContentRequestedEvent(thisPane));


    }




//    public DefaultCollapsiblePane(String title,Closure contentProvider) {
//        this(contentProvider);
//        setTitleButtonText(title);
//    }
//
//    public DefaultCollapsiblePane(String title, ActionListener actionListener) {
//        this(title);
//        addTitleButtonActionListener(actionListener);
//    }


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

    public void reload() {
        cre.contentRequested(new ContentRequestedEvent(thisPane));
        SwingUtilities.invokeLater(() -> {
            revalidate();
            repaint();
            if (flashAfterEdit) {
                animator = GUITools.flashBackground(animator, thisPane, Color.YELLOW, 2);
            }
        });
    }

    public void setTitleButtonText(String text) {
        btnTitle.setText(SYSTools.xx(text));
    }

    public JideButton getTitleButton() {
        return btnTitle;
    }
}
