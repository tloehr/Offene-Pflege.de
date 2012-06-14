package op.tools;

import com.jidesoft.swing.JideButton;
import entity.system.SYSPropsTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
        button.setButtonStyle(JideButton.HYPERLINK_STYLE);
        button.setFont(new Font("Arial", Font.PLAIN, 12));

        button.setOpaque(false);
        button.setHorizontalAlignment(SwingConstants.LEADING);

        button.setRequestFocusEnabled(true);
        button.setFocusable(true);

        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (actionListener != null) {
            button.addActionListener(actionListener);
        }
        return button;
    }


    public static MouseAdapter getHyperlinkStyleMouseAdapter() {
        return new MouseAdapter() {
//            String text = "";
//
//            @Override
//            public void mouseEntered(MouseEvent mouseEvent) {
//                text = ((javax.swing.AbstractButton) mouseEvent.getSource()).getText();
//                ((javax.swing.AbstractButton) mouseEvent.getSource()).setText("<html><u>" + text + "</u></html>");
//            }
//
//            @Override
//            public void mouseExited(MouseEvent mouseEvent) {
//                ((javax.swing.AbstractButton) mouseEvent.getSource()).setText(text);
//            }
        };
    }

    public static JToggleButton getNiceToggleButton(String title){
        JToggleButton tb = new JToggleButton(title);
        tb.setIcon(new ImageIcon(tb.getClass().getResource("/artwork/22x22/cb-off.png")));
        tb.setSelectedIcon(new ImageIcon(tb.getClass().getResource("/artwork/22x22/cb-on.png")));
        tb.setContentAreaFilled(false);
        tb.setBorderPainted(false);
        tb.setBorder(null);
        tb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tb.setBackground(Color.WHITE);
        tb.setAlignmentX(Component.LEFT_ALIGNMENT);
        return tb;
    }
}
