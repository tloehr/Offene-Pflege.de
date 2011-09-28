/*
 * RiverLayout is released under the LGPL licence. You may therefore freely use it in 
 * both non-commerical and commercial applications and you don't need to open up your source code.
 * Improvements made to RiverLayout should however be returned to the project for the benefit of everyone.  
 * 
 * Contact etc
 * RiverLayout is written by me, David Ekholm, Datadosen david@jalbum.net
 * If you like it or have some suggestions, please let me know.
 * You can reach me by mail or phone (although email is preferred):
 * Datadosen
 * David Ekholm
 * Mantalsvägen 33
 * s-175 50 Järfälla
 * Sweden
 * Phone: +46 8 580 15668. Mobile: +46 70 486 77 38 
 */

package se.datadosen.component;

import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 * A ControlPanel is a JPanel which has "RiverLayout" as default layout manager.
 * This is the preferred panel for making user interfaces in a simple way.
 *
 * @author David Ekholm
 * @version 1.0
 * @see RiverLayout
 */
public class ControlPanel extends JPanel implements se.datadosen.util.JComponentHolder {

    /**
     * Create a plain ControlPanel
     */
    public ControlPanel() {
        super(new RiverLayout());
    }

    /**
     * Create a control panel framed with a titled border
     */
    public ControlPanel(String title) {
        this();
        setTitle(title);
    }

    public void setTitle(String title) {
        setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), title));
    }
}