/*
 * Created by JFormDesigner on Sat Jul 14 11:46:11 CEST 2012
 */

package de.offene_pflege.op.care.info;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideTabbedPane;
import de.offene_pflege.entity.EntityTools;
import de.offene_pflege.entity.info.ResInfo;
import de.offene_pflege.entity.info.ResInfoTools;
import de.offene_pflege.entity.info.ResInfoTypeTools;
import de.offene_pflege.entity.prescription.Hospital;
import de.offene_pflege.entity.prescription.HospitalTools;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.residents.PnlEditHospital;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.HorizontalLayout;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;

/**
 * @author Torsten Löhr
 */
public class PnlAway extends JPanel {

    public static final int TAB_HOSPITAL = 0;
    public static final int TAB_HOLLIDAY = 1;
    public static final int TAB_OTHER = 2;

    public static final String internalClassID = "nursingrecords.info.pnlabwesend";
    private ResInfo abwesenheit;
    private Closure actionBlock;
    private Properties props;

    public PnlAway(ResInfo abwesenheit, Closure actionBlock) {
        this.abwesenheit = abwesenheit;
        this.actionBlock = actionBlock;
        initComponents();
        initPanel();
    }

    private void initPanel() {

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT h FROM Hospital h WHERE h.state = 0 ORDER BY h.name");
        java.util.List<Hospital> list = query.getResultList();
        em.close();
//
//        pnlEditKH = new PnlEditKH(new Hospital());
//        pnlRight.add(pnlEditKH, 0);

        cmbHospital.setModel(new DefaultComboBoxModel(list.toArray()));
        cmbHospital.setRenderer(HospitalTools.getKHRenderer());

        tab1.setTitleAt(TAB_HOSPITAL, SYSTools.xx("misc.msg.hospital"));
        tab1.setTitleAt(TAB_HOLLIDAY, SYSTools.xx("misc.msg.holliday"));
        tab1.setTitleAt(TAB_OTHER, SYSTools.xx("misc.msg.otherreasons"));

        lblKH.setText(SYSTools.xx("misc.msg.pleaseenterdescription"));
        lblHolliday.setText(SYSTools.xx("misc.msg.pleaseenterdescription"));
        lblOther.setText(SYSTools.xx("misc.msg.pleaseenterdescription"));

        props = ResInfoTools.getContent(abwesenheit);

        if (props.containsKey("type")) {
            if (props.getProperty("type").equals(ResInfoTypeTools.TYPE_ABSENCE_HOSPITAL)) {
                tab1.setSelectedIndex(TAB_HOSPITAL);
            } else if (props.getProperty("type").equals(ResInfoTypeTools.TYPE_ABSENCE_HOLLIDAY)) {
                tab1.setSelectedIndex(TAB_HOLLIDAY);
            } else if (props.getProperty("type").equals(ResInfoTypeTools.TYPE_ABSENCE_OTHER)) {
                tab1.setSelectedIndex(TAB_OTHER);
            }
        } else {
            tab1.setSelectedIndex(TAB_HOSPITAL);
        }

        Hospital preselect = null;
        if (props.containsKey("khid")) {
            Long khid = Long.parseLong(props.getProperty("khid"));
            preselect = EntityTools.find(Hospital.class, khid);
            cmbHospital.setSelectedItem(preselect);
        }

        SwingUtilities.invokeLater(() -> SYSTools.showSide(split1, SYSTools.LEFT_UPPER_SIDE));

    }

    private void btnAddKHActionPerformed(ActionEvent e) {
//        SYSTools.showSide(split1, SYSTools.RIGHT_LOWER_SIDE, SYSConst.SCROLL_TIME_FAST);
        final PnlEditHospital pnlHospital = new PnlEditHospital(new Hospital());
        JidePopup popup = GUITools.createPanelPopup(pnlHospital, o -> {
            if (o != null) {
                cmbHospital.setModel(new DefaultComboBoxModel(new Hospital[]{(Hospital) o}));
            }
        }, btnAddKH);
        GUITools.showPopup(popup, SwingConstants.EAST);
    }

    private void btnToLeftActionPerformed(ActionEvent e) {
//        Hospital newKH = pnlEditKH.getKrankenhaus();
//        if (newKH != null) {
//            cmbHospital.setModel(new DefaultComboBoxModel(new Hospital[]{newKH}));
//        }
//        SYSTools.showSide(split1, SYSTools.LEFT_UPPER_SIDE, SYSConst.SCROLL_TIME_FAST);

    }

    private void btnCancelActionPerformed(ActionEvent e) {
        actionBlock.execute(null);
    }

    private void btnOKActionPerformed(ActionEvent e) {

        switch (tab1.getSelectedIndex()) {
            case TAB_HOSPITAL: {
                abwesenheit.setText(txtKH.getText().trim());

                Hospital hospital = (Hospital) cmbHospital.getSelectedItem();

                if (hospital.getKhid() == null) {
                    hospital = EntityTools.merge(hospital);
                }

//                abwesenheit.setHtml("<br/><b><u>" + SYSTools.xx("misc.msg.hospital") + "</u></b>" +
//                        "<ul><li>" + HospitalTools.getFullName(hospital) + "</li></ul>");

                props.put("type", ResInfoTypeTools.TYPE_ABSENCE_HOSPITAL);
                props.put("khid", hospital.getKhid().toString());
                break;
            }
            case TAB_HOLLIDAY: {
                abwesenheit.setText(txtHolliday.getText().trim());
//                abwesenheit.setHtml("<i>" + SYSTools.xx("misc.msg.holliday") + "</i><br/>");
                props.put("type", ResInfoTypeTools.TYPE_ABSENCE_HOLLIDAY);
                break;
            }
            case TAB_OTHER: {
                abwesenheit.setText(txtOther.getText().trim());
//                abwesenheit.setHtml("<i>" + SYSTools.xx("misc.msg.otherreasons") + "</i><br/>");
                props.put("type", ResInfoTypeTools.TYPE_ABSENCE_OTHER);
                break;
            }
            default: {

            }
        }

        try {
            StringWriter writer = new StringWriter();
            props.store(writer, "");
            abwesenheit.setProperties(writer.toString());
        } catch (Exception ex) {
            OPDE.fatal(ex);
        }

        ResInfoTools.setFrom(abwesenheit, new Date());
        ResInfoTools.setTo(abwesenheit, SYSConst.DATE_UNTIL_FURTHER_NOTICE);

        actionBlock.execute(abwesenheit);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        tab1 = new JideTabbedPane();
        pnlKH = new JPanel();
        split1 = new JSplitPane();
        panel5 = new JPanel();
        cmbHospital = new JComboBox();
        btnAddKH = new JButton();
        lblKH = new JLabel();
        scrollPane3 = new JScrollPane();
        txtKH = new JTextArea();
        pnlRight = new JPanel();
        btnToLeft = new JButton();
        pnlHolliday = new JPanel();
        lblHolliday = new JLabel();
        scrollPane1 = new JScrollPane();
        txtHolliday = new JTextArea();
        pnlOther = new JPanel();
        lblOther = new JLabel();
        scrollPane2 = new JScrollPane();
        txtOther = new JTextArea();
        panel3 = new JPanel();
        btnCancel = new JButton();
        btnOK = new JButton();

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, default:grow, $lcgap, default",
            "default, $lgap, fill:default:grow, $lgap, fill:default, $lgap, default"));

        //======== tab1 ========
        {
            tab1.setFont(new Font("Arial", Font.PLAIN, 14));
            tab1.setSelectedTabFont(new Font("Arial", Font.BOLD, 14));

            //======== pnlKH ========
            {
                pnlKH.setLayout(new BoxLayout(pnlKH, BoxLayout.X_AXIS));

                //======== split1 ========
                {
                    split1.setDividerLocation(200);
                    split1.setDividerSize(1);
                    split1.setEnabled(false);

                    //======== panel5 ========
                    {
                        panel5.setLayout(new FormLayout(
                            "default:grow, default",
                            "pref, $ugap, default, $lgap, fill:default:grow"));

                        //---- cmbHospital ----
                        cmbHospital.setFont(new Font("Arial", Font.PLAIN, 14));
                        panel5.add(cmbHospital, CC.xy(1, 1, CC.FILL, CC.DEFAULT));

                        //---- btnAddKH ----
                        btnAddKH.setText(null);
                        btnAddKH.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
                        btnAddKH.setBorderPainted(false);
                        btnAddKH.setContentAreaFilled(false);
                        btnAddKH.setBorder(null);
                        btnAddKH.addActionListener(e -> btnAddKHActionPerformed(e));
                        panel5.add(btnAddKH, CC.xy(2, 1));

                        //---- lblKH ----
                        lblKH.setText("text");
                        lblKH.setFont(new Font("Arial", Font.PLAIN, 14));
                        panel5.add(lblKH, CC.xywh(1, 3, 2, 1));

                        //======== scrollPane3 ========
                        {

                            //---- txtKH ----
                            txtKH.setFont(new Font("Arial", Font.PLAIN, 14));
                            scrollPane3.setViewportView(txtKH);
                        }
                        panel5.add(scrollPane3, CC.xywh(1, 5, 2, 1));
                    }
                    split1.setLeftComponent(panel5);

                    //======== pnlRight ========
                    {
                        pnlRight.setLayout(new BoxLayout(pnlRight, BoxLayout.LINE_AXIS));

                        //---- btnToLeft ----
                        btnToLeft.setText(null);
                        btnToLeft.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/1leftarrow.png")));
                        btnToLeft.addActionListener(e -> btnToLeftActionPerformed(e));
                        pnlRight.add(btnToLeft);
                    }
                    split1.setRightComponent(pnlRight);
                }
                pnlKH.add(split1);
            }
            tab1.addTab("text", pnlKH);

            //======== pnlHolliday ========
            {
                pnlHolliday.setLayout(new BorderLayout());

                //---- lblHolliday ----
                lblHolliday.setText("text");
                lblHolliday.setFont(new Font("Arial", Font.PLAIN, 14));
                pnlHolliday.add(lblHolliday, BorderLayout.NORTH);

                //======== scrollPane1 ========
                {

                    //---- txtHolliday ----
                    txtHolliday.setFont(new Font("Arial", Font.PLAIN, 14));
                    scrollPane1.setViewportView(txtHolliday);
                }
                pnlHolliday.add(scrollPane1, BorderLayout.CENTER);
            }
            tab1.addTab("text", pnlHolliday);

            //======== pnlOther ========
            {
                pnlOther.setLayout(new BorderLayout());

                //---- lblOther ----
                lblOther.setText("text");
                lblOther.setFont(new Font("Arial", Font.PLAIN, 14));
                pnlOther.add(lblOther, BorderLayout.NORTH);

                //======== scrollPane2 ========
                {

                    //---- txtOther ----
                    txtOther.setFont(new Font("Arial", Font.PLAIN, 14));
                    scrollPane2.setViewportView(txtOther);
                }
                pnlOther.add(scrollPane2, BorderLayout.CENTER);
            }
            tab1.addTab("text", pnlOther);
        }
        add(tab1, CC.xy(3, 3));

        //======== panel3 ========
        {
            panel3.setLayout(new HorizontalLayout(5));

            //---- btnCancel ----
            btnCancel.setText(null);
            btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
            btnCancel.addActionListener(e -> btnCancelActionPerformed(e));
            panel3.add(btnCancel);

            //---- btnOK ----
            btnOK.setText(null);
            btnOK.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnOK.addActionListener(e -> btnOKActionPerformed(e));
            panel3.add(btnOK);
        }
        add(panel3, CC.xy(3, 5, CC.RIGHT, CC.DEFAULT));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JideTabbedPane tab1;
    private JPanel pnlKH;
    private JSplitPane split1;
    private JPanel panel5;
    private JComboBox cmbHospital;
    private JButton btnAddKH;
    private JLabel lblKH;
    private JScrollPane scrollPane3;
    private JTextArea txtKH;
    private JPanel pnlRight;
    private JButton btnToLeft;
    private JPanel pnlHolliday;
    private JLabel lblHolliday;
    private JScrollPane scrollPane1;
    private JTextArea txtHolliday;
    private JPanel pnlOther;
    private JLabel lblOther;
    private JScrollPane scrollPane2;
    private JTextArea txtOther;
    private JPanel panel3;
    private JButton btnCancel;
    private JButton btnOK;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
