/*
 * Created by JFormDesigner on Sat Jun 15 15:03:44 CEST 2013
 */

package op.dev;

import java.awt.event.*;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import op.OPDE;
import op.care.info.PnlEditResInfo;
import op.tools.CleanablePanel;
import op.tools.GUITools;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Properties;

/**
 * @author Torsten Löhr
 */
public class PnlDev extends CleanablePanel {
    public static final String internalClassID = "opde.dev";

    public PnlDev() {
        initComponents();
    }

    @Override
    public void cleanup() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void reload() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getInternalClassID() {
        return internalClassID;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private void button1ActionPerformed(ActionEvent e) {
        final JidePopup popup = new JidePopup();
        PnlEditResInfo pnlEditResInfo = new PnlEditResInfo(txtXML.getText(), new Closure() {
            @Override
            public void execute(Object o) {
                txtException.setText(SYSTools.catchNull(o));
            }
        });

        if (pnlEditResInfo.getLastParsingException() == null) {
            pnlEditResInfo.setEnabled(true);
            popup.setMovable(false);
            popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
            JScrollPane scrl = new JScrollPane(pnlEditResInfo.getPanel());
            scrl.setPreferredSize(new Dimension(pnlEditResInfo.getPanel().getPreferredSize().width + 100, Math.min(pnlEditResInfo.getPanel().getPreferredSize().height, OPDE.getMainframe().getHeight())));

            popup.setOwner(txtXML);
            popup.removeExcludedComponent(txtXML);
            popup.getContentPane().add(scrl);
            popup.setDefaultFocusComponent(scrl);
            GUITools.showPopup(popup, SwingConstants.CENTER);

            txtException.setText(null);
        } else {

            String exc = pnlEditResInfo.getLastParsingException().getMessage() +"\n";
            for (StackTraceElement ste : Arrays.asList(pnlEditResInfo.getLastParsingException().getStackTrace())){
                exc += ste.toString() + "\n";
            }

            txtException.setText(exc);

        }
        invalidate();
    }

    private void txtXMLFocusGained(FocusEvent e) {
        txtXML.selectAll();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        scrollPane1 = new JScrollPane();
        txtXML = new JTextArea();
        scrollPane2 = new JScrollPane();
        txtException = new JTextArea();
        button1 = new JButton();

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, 130dlu, $lcgap, default:grow, $lcgap, default",
            "default, $lgap, fill:default:grow, 2*($lgap, default)"));

        //======== scrollPane1 ========
        {

            //---- txtXML ----
            txtXML.setLineWrap(true);
            txtXML.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    txtXMLFocusGained(e);
                }
            });
            scrollPane1.setViewportView(txtXML);
        }
        add(scrollPane1, CC.xy(3, 3, CC.FILL, CC.FILL));

        //======== scrollPane2 ========
        {

            //---- txtException ----
            txtException.setBackground(Color.pink);
            txtException.setLineWrap(true);
            scrollPane2.setViewportView(txtException);
        }
        add(scrollPane2, CC.xy(5, 3, CC.FILL, CC.FILL));

        //---- button1 ----
        button1.setText("ResInfoType Form Test");
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button1ActionPerformed(e);
            }
        });
        add(button1, CC.xywh(3, 5, 3, 1));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane scrollPane1;
    private JTextArea txtXML;
    private JScrollPane scrollPane2;
    private JTextArea txtException;
    private JButton button1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
