/*
 * Created by JFormDesigner on Fri Jul 13 15:32:45 CEST 2012
 */

package op.care.info;

import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import org.apache.commons.collections.Closure;

/**
 * @author Torsten Löhr
 */
public class PnlPIT extends JPanel {
    private Date date;
    private op.tools.PnlPIT pnlPIT;
    private Closure actionBlock;

    public PnlPIT(Date date, Closure actionBlock) {
        this.date = date;
        pnlPIT = new op.tools.PnlPIT(date);
        this.actionBlock = actionBlock;
        add(pnlPIT, CC.xy(3, 2));
        initComponents();
    }

    private void btnCancelActionPerformed(ActionEvent e) {
        actionBlock.execute(null);
    }

    private void btnOKActionPerformed(ActionEvent e) {
        actionBlock.execute(pnlPIT.getPIT());
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel3 = new JPanel();
        btnCancel = new JButton();
        btnOK = new JButton();

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, default:grow, $lcgap, default",
            "default, fill:default:grow, 2*($lgap, default)"));

        //======== panel3 ========
        {
            panel3.setLayout(new BoxLayout(panel3, BoxLayout.LINE_AXIS));

            //---- btnCancel ----
            btnCancel.setText(null);
            btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/cancel.png")));
            btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnCancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnCancelActionPerformed(e);
                }
            });
            panel3.add(btnCancel);

            //---- btnOK ----
            btnOK.setText(null);
            btnOK.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/apply.png")));
            btnOK.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnOK.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnOKActionPerformed(e);
                }
            });
            panel3.add(btnOK);
        }
        add(panel3, CC.xy(3, 4, CC.RIGHT, CC.DEFAULT));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel3;
    private JButton btnCancel;
    private JButton btnOK;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
