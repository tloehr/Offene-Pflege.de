package op.tools;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.swing.JideButton;
import op.OPDE;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 08.09.12
 * Time: 11:57
 * To change this template use File | Settings | File Templates.
 */
public class DefaultCollapsiblePane extends CollapsiblePane {
    JPanel titlePanelleft, titlePanelright, titlePanel, additionalIconPanel;
    JideButton btnTitle;

    ActionListener defaultActionListener;

    public DefaultCollapsiblePane() {
        super();

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

        setTitleLabelComponent(titlePanel);
    }

    public DefaultCollapsiblePane(String title) {
        this();
        setTitleButtonText(title);
    }

    public DefaultCollapsiblePane(String title, ActionListener actionListener) {
        this(title);
        addTitleButtonActionListener(actionListener);
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

    public void setTitleButtonText(String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                btnTitle.setText(SYSTools.xx(text));
                revalidate();
                repaint();
            }
        });

    }

    public JideButton getTitleButton() {
        return btnTitle;
    }
}
