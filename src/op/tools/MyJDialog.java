package op.tools;

import com.sun.awt.AWTUtilities;
import op.OPDE;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

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