package de.offene_pflege.gui.interfaces;

import com.jidesoft.swing.JideButton;
import de.offene_pflege.gui.GUITools;
import org.jdesktop.swingx.HorizontalLayout;
import org.jdesktop.swingx.VerticalLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 08.09.12
 * Time: 11:57
 * To change this template use File | Settings | File Templates.
 */
public class DefaultCPTitle  {
    JPanel titlePanelleft, titlePanelright, titlePanel, additionalIconPanel;
    JideButton btnTitle;
    JPanel buttons_under_title            ;


    public DefaultCPTitle(String title, ActionListener actionListener) {

        additionalIconPanel = new JPanel();
        additionalIconPanel.setLayout(new BoxLayout(additionalIconPanel, BoxLayout.LINE_AXIS));
        additionalIconPanel.setOpaque(false);

        titlePanelleft = new JPanel();
        titlePanelleft.setLayout(new BoxLayout(titlePanelleft, BoxLayout.LINE_AXIS));

        btnTitle = GUITools.createHyperlinkButton(title, null, null);
        btnTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnTitle.addActionListener(actionListener);

        titlePanelleft.add(additionalIconPanel);

        buttons_under_title = new JPanel(new VerticalLayout());
        buttons_under_title.setOpaque(false);
        buttons_under_title.add(btnTitle);
        titlePanelleft.add(buttons_under_title);

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

    }

    public JPanel get_buttons_under_title(){
        return buttons_under_title;
    }

    public JPanel getAdditionalIconPanel() {
        return additionalIconPanel;
    }

    public JPanel getLeft() {
        return titlePanelleft;
    }

    public JPanel getRight() {
        return titlePanelright;
    }

    public JPanel getMain() {
        return titlePanel;
    }

    public JideButton getButton(){
        return btnTitle;
    }
}
