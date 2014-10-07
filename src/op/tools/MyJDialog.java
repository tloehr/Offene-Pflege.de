package op.tools;

import op.OPDE;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 11.05.12
 * Time: 14:15
 * To change this template use File | Settings | File Templates.
 */
public class MyJDialog extends JDialog {
    private JPanel content;

    public MyJDialog(boolean decorated) {
        this(OPDE.getMainframe(), decorated);
    }

    public MyJDialog() {
        this(true);
    }

    public MyJDialog(JFrame owner) {
        this(owner, true);
    }

    public MyJDialog(Dialog owner, boolean decorated) {
        super(owner, true);
        setLocationRelativeTo(getParent());
        initContent();
        setResizable(false);
        setUndecorated(!decorated);
    }

    public MyJDialog(JFrame owner, boolean decorated) {
        super(owner, true);
        setLocationRelativeTo(getParent());
        initContent();
        setResizable(false);
        setUndecorated(!decorated);
    }

    @Override
    public Dimension getPreferredSize() {
        // makes sure, that the dialog is never larger than the screen in use.
        Rectangle maximumsize = GUITools.getScreenSize(GUITools.getCurrentScreen(OPDE.getMainframe()));
        Dimension dim = super.getPreferredSize();
        if (dim.width > maximumsize.width) dim.width = maximumsize.width;
        if (dim.height > maximumsize.height) dim.height = maximumsize.height;
        return dim;
    }

    public MyJDialog(Dialog owner) {
        this(owner, true);
    }

    private void initContent() {
        content = new JPanel();
        content.setBorder(new LineBorder(Color.BLACK, 1));
        setContentPane(content);
    }

    @Override
    public void dispose() {
        setVisible(false);
    }

}