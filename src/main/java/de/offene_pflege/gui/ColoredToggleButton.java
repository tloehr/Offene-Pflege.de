package de.offene_pflege.gui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Taken from: http://www.java-forum.org/awt-swing-javafx-and-swt/67194-jtogglebutton-hintergrundfarbe-beim-druecken-festlegen.html
 */
public class ColoredToggleButton extends JToggleButton implements ItemListener {

    protected Color _activatedBGColor;
    protected Color _deactivatedBGColor;
    protected Color _activatedFGColor;
    protected Color _deactivatedFGColor;


//    // Standard JToggleButton with ActionListeners attached
//    public ColoredToggleButton() {
//        super();
//        setSelected(false);
//    }
//
//    // Standard JToggleButton with ActionListeners attached
//    public ColoredToggleButton(String label) {
//        super(label);
//        setSelected(false);
//    }
//
//    public ColoredToggleButton(Color activatedColor, Color deactivatedColor) {
//        super();
//        _activatedBGColor = activatedColor;
//        _deactivatedBGColor = deactivatedColor;
//        setSelected(false);
//        // Set a new UI because of the toggle button
//        setUI(new CustomButtonUI(activatedColor));
//        setBackground(deactivatedColor);
//        setContentAreaFilled(false);
//        //addActionListener(this);
//        addItemListener(this);
//    }
//
//    public ColoredToggleButton(String label, Color activatedColor, Color deactivatedColor) {
//        super(label);
//        _activatedBGColor = activatedColor;
//        _deactivatedBGColor = deactivatedColor;
//        setSelected(false);
//        // Set a new UI because of the toggle button
//        setUI(new CustomButtonUI(activatedColor));
//        setBackground(deactivatedColor);
//        setContentAreaFilled(false);
//        //addActionListener(this);
//        addItemListener(this);
//    }

    public ColoredToggleButton(String label, Color activatedColor, Color deactivatedColor, Color activatedFGColor, Color deactivatedFGColor) {
        super(label);
        _activatedBGColor = activatedColor;
        _deactivatedBGColor = deactivatedColor;
        _activatedFGColor = activatedFGColor;
        _deactivatedFGColor = deactivatedFGColor;

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        setSelected(false);
        // Set a new UI because of the toggle button
        setUI(new CustomButtonUI(activatedColor));
        setBackground(deactivatedColor);
        setForeground(isSelected() ? Color.yellow : Color.GRAY);
        setContentAreaFilled(false);
        //addActionListener(this);
        addItemListener(this);
    }

    public Color getActivatedColor() {
        return _activatedBGColor;
    }

    public Color getDeactivatedColor() {
        return _deactivatedBGColor;
    }

    // Overloaded in order to paint the background
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        GradientPaint gradient = new GradientPaint(20, 10, getBackground(), 20, h, getBackground(), true);
        g2.setPaint(gradient);
        g2.fillRoundRect(0, 0, w, h, 10, 10);
        super.paintComponent(g);
    }

    public void itemStateChanged(ItemEvent arg0) {
        setForeground(isSelected() ? Color.yellow : Color.GRAY);
        if (!isSelected()) {
            setBackground(getDeactivatedColor());
        }
    }


}

class CustomButtonUI extends BasicToggleButtonUI {

    private Color _selectedColor;

    public CustomButtonUI(Color selectedColor) {
        _selectedColor = selectedColor;
    }

    public void paintButtonPressed(Graphics g, AbstractButton b) {
        if (b.getBackground() != getSelectedColor())
            b.setBackground(getSelectedColor());
    }

    public Color getSelectedColor() {
        return _selectedColor;
    }
}
