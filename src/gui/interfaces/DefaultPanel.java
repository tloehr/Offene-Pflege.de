package gui.interfaces;

import gui.PnlYesNo;
import op.OPDE;

import javax.swing.*;
import java.awt.*;

/**
 * Created by tloehr on 18.06.15.
 */
public abstract class DefaultPanel extends JPanel {
    protected JPanel mainPanel, dialogPanel;
    protected DefaultPanel thisPanel;
    protected String internalClassID = null;

    public void cleanup() {}

    public void reload() {
        OPDE.getEMF().getCache().evictAll();
    }

    public String getInternalClassID() {
        return internalClassID;
    }

    public DefaultPanel() {
        super();
        thisPanel = this;
        setLayout(new CardLayout());
        mainPanel = new JPanel();
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
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                dialogPanel.removeAll();
                dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.PAGE_AXIS));
                dialogPanel.add(pnlYesNo);
                dialogPanel.revalidate();
                dialogPanel.repaint();
                dialogView();
            }
        });
    }
}
