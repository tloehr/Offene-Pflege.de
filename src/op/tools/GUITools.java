package op.tools;

import com.jidesoft.plaf.basic.ThemePainter;
import com.jidesoft.swing.JideButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 16.06.11
 * Time: 14:46
 * To change this template use File | Settings | File Templates.
 */
public class GUITools {

    public static JideButton createHyperlinkButton(String name, Icon icon, ActionListener actionListener) {
        final JideButton button = new JideButton(name, icon);
        final String n = name;
        button.setButtonStyle(JideButton.HYPERLINK_STYLE);

        button.setOpaque(false);
        button.setHorizontalAlignment(SwingConstants.LEADING);

        button.setRequestFocusEnabled(true);
        button.setFocusable(true);

        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(actionListener);

        return button;
    }
}
