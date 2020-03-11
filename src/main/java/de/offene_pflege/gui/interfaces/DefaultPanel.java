package de.offene_pflege.gui.interfaces;

import de.offene_pflege.gui.PnlYesNo;

import javax.swing.*;
import java.awt.*;

/**
 * Created by tloehr on 18.06.15.
 */
public abstract class DefaultPanel extends CleanablePanel {
    protected JPanel mainPanel, dialogPanel;
    protected DefaultPanel thisPanel;

    public DefaultPanel(String internalClassID) {
        super(internalClassID);
        thisPanel = this;
        setLayout(new CardLayout());
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        dialogPanel = new JPanel();
        add(mainPanel, "main");
        add(dialogPanel, "dialog");
        mainView();
    }

    public void mainView() {
        ((CardLayout) getLayout()).show(this, "main");
    }

    public void dialogView() {
        ((CardLayout) getLayout()).show(this, "dialog");
    }

    public void ask(PnlYesNo pnlYesNo) {
        SwingUtilities.invokeLater(() -> {
            dialogPanel.removeAll();
            dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.PAGE_AXIS));
            dialogPanel.add(pnlYesNo);
            dialogPanel.revalidate();
            dialogPanel.repaint();
            dialogView();
        });
    }
}
