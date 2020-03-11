package de.offene_pflege.gui.interfaces;

import javax.swing.*;
import java.awt.*;

/**
 * Created by tloehr on 11.06.15.
 */
public class DefaultContentPane extends JPanel {

    DefaultCollapsiblePanes cps = new DefaultCollapsiblePanes();


    public DefaultContentPane() {
        super();
    }

    public DefaultCollapsiblePanes getCps() {
        return cps;
    }

    @Override
    public Component add(Component comp) {
        return cps.add(comp);
    }





}
