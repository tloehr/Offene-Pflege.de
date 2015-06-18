/*
 * Created by JFormDesigner on Sat Nov 03 11:45:21 CET 2012
 */

package op.residents;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import entity.building.Station;
import entity.building.StationTools;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.prescription.GP;
import entity.prescription.GPTools;
import entity.system.Users;
import entity.system.UsersTools;
import op.OPDE;
import op.threads.DisplayMessage;
import gui.GUITools;
import op.tools.MyJDialog;
import op.tools.SYSCalendar;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.HorizontalLayout;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Torsten Löhr
 */
public class DlgEditResidentBaseData extends MyJDialog {
    public static final String internalClassID = "nursingrecords.info.DlgEditResidentBaseData";
    private Resident resident;
    private Closure actionBlock;
    private boolean ignoreEvent = false;
    private JToggleButton tbAdminOnly, tbCalcMediUPR1;
    Date dob = null;

    public DlgEditResidentBaseData(Resident resident, Closure actionBlock) {
        super(false);
        this.resident = resident;
        this.actionBlock = actionBlock;
        initComponents();
        initDialog();
        pack();
        setVisible(true);
    }


    private void initDialog() {
        lblName.setText(SYSTools.xx("misc.msg.name"));
        lblFirstname.setText(SYSTools.xx("misc.msg.firstname"));
        lblDOB.setText(SYSTools.xx("misc.msg.dob"));
        lblGender.setText(SYSTools.xx("misc.msg.gender"));
//        lblRoom.setText(SYSTools.xx("misc.msg.room"));
        lblStation.setText(SYSTools.xx("misc.msg.subdivision"));
        lblPrimNurse1.setText(SYSTools.xx("misc.msg.primaryNurse") + " 1");
        lblPrimNurse2.setText(SYSTools.xx("misc.msg.primaryNurse") + " 2");
        lblGP.setText(SYSTools.xx("misc.msg.gp"));
        rbMale.setText(SYSTools.xx("misc.msg.male"));
        rbFemale.setText(SYSTools.xx("misc.msg.female"));

        rbMale.setSelected(resident.getGender() == ResidentTools.MALE);
        rbFemale.setSelected(resident.getGender() == ResidentTools.FEMALE);

        txtName.setText(resident.getNameNeverAnonymous());
        txtFirstname.setText(resident.getFirstnameNeverAnonymous());

        txtDOB.setText(DateFormat.getDateInstance().format(resident.getDOB()));

        ArrayList<GP> listGPs = GPTools.getAllActive();
        listGPs.add(0, null);
        cmbGP.setModel(new DefaultComboBoxModel(listGPs.toArray()));
        cmbGP.setRenderer(GPTools.getRenderer());
        cmbGP.setSelectedItem(resident.getGP());

        cmbStation.setModel(StationTools.getAll4Combobox(false));
        cmbStation.setRenderer(SYSTools.getDefaultRenderer());
        cmbStation.setSelectedItem(resident.getStation());
//        cmbStation.addItemListener(new ItemListener() {
//            @Override
//            public void itemStateChanged(ItemEvent e) {
//                if (e.getStateChange() == ItemEvent.SELECTED) {
//                    if (ignoreEvent) return;
//                    ignoreEvent = true;
////                    if (cmbRoom.getSelectedItem() != null && ((Rooms) cmbRoom.getSelectedItem()).getStation() != e.getItem()) {
////                        // somebody changed the station so that the room doesnt fit anymore
////                        // set to null then
////                        cmbRoom.setSelectedItem(null);
////                    }
//                    ignoreEvent = false;
//                }
//            }
//        });

        ArrayList<Users> listUsers = UsersTools.getUsers(false);
        listUsers.add(0, null);
        cmbPrimNurse1.setModel(new DefaultComboBoxModel(listUsers.toArray()));
        cmbPrimNurse1.setRenderer(UsersTools.getRenderer());
        cmbPrimNurse1.setSelectedItem(resident.getPN1());
        cmbPrimNurse2.setModel(new DefaultComboBoxModel(listUsers.toArray()));
        cmbPrimNurse2.setRenderer(UsersTools.getRenderer());
        cmbPrimNurse2.setSelectedItem(resident.getPN2());

//        ArrayList<Rooms> listRooms = RoomsTools.getAllActive();
//        listGPs.add(0, null);
//        cmbRoom.setModel(new DefaultComboBoxModel(listRooms.toArray()));
//        cmbRoom.setRenderer(SYSTools.getDefaultRenderer());
//        cmbRoom.setSelectedItem(resident.getRoom());
//        cmbRoom.addItemListener(new ItemListener() {
//            @Override
//            public void itemStateChanged(ItemEvent e) {
//                if (ignoreEvent) return;
//                ignoreEvent = true;
//                if (e.getStateChange() == ItemEvent.SELECTED && e.getItem() != null) {
//                    cmbStation.setSelectedItem(((Rooms) e.getItem()).getStation());
//                }
//                ignoreEvent = false;
//            }
//        });

        tbCalcMediUPR1 = GUITools.getNiceToggleButton(SYSTools.xx(internalClassID+".tbCalcMediUPR1"));
        tbCalcMediUPR1.setToolTipText(SYSTools.xx(internalClassID+".tooltip.tbCalcMediUPR1"));
        tbCalcMediUPR1.setSelected(resident.isCalcMediUPR1());
        add(tbCalcMediUPR1, CC.xywh(3, 21, 3, 1, CC.LEFT, CC.FILL));

        tbAdminOnly = GUITools.getNiceToggleButton(SYSTools.xx("misc.msg.adminonly"));
        tbAdminOnly.setSelected(resident.getAdminonly() == 2);
        add(tbAdminOnly, CC.xywh(3, 23, 3, 1, CC.LEFT, CC.FILL));
    }

    private boolean saveOK() {
        if (txtName.getText().trim().isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(internalClassID + ".nameXX", DisplayMessage.WARNING));
            return false;
        }
        if (txtFirstname.getText().trim().isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(internalClassID + ".firstnameXX", DisplayMessage.WARNING));
            return false;
        }

        try {
            dob = SYSCalendar.parseDate(txtDOB.getText());
        } catch (NumberFormatException nfe) {
            dob = null;
        }
        if (!SYSCalendar.isBirthdaySane(dob)) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx(internalClassID + ".dobXX"), DisplayMessage.WARNING));
            return false;
        }

        return true;
    }

    @Override
    public void dispose() {
        super.dispose();
        actionBlock.execute(resident);
    }

    private void btnCancelActionPerformed(ActionEvent e) {
        resident = null;
        dispose();
    }

    private void btnApplyActionPerformed(ActionEvent e) {
        if (!saveOK()) return;

        resident.setName(txtName.getText().trim());
        resident.setFirstname(txtFirstname.getText().trim());
        resident.setDOB(dob);
        resident.setEditor(OPDE.getLogin().getUser());
        resident.setGender(rbFemale.isSelected() ? ResidentTools.FEMALE : ResidentTools.MALE);
        resident.setGP((GP) cmbGP.getSelectedItem());
        resident.setPN1((Users) cmbPrimNurse1.getSelectedItem());
        resident.setPN2((Users) cmbPrimNurse2.getSelectedItem());
//        resident.setRoom((Rooms) cmbRoom.getSelectedItem());
        resident.setStation((Station) cmbStation.getSelectedItem());
        resident.setCalcMediUPR1(tbCalcMediUPR1.isSelected());
        resident.setAdminonly(tbAdminOnly.isSelected() ? ResidentTools.ADMINONLY : ResidentTools.NORMAL);

        dispose();
    }

    private void btnAddGPActionPerformed(ActionEvent e) {
        final PnlEditGP pnlGP = new PnlEditGP(new GP());
        final JidePopup popup = GUITools.createPanelPopup(pnlGP, new Closure() {
            @Override
            public void execute(Object o) {
                if (o != null) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        GP myGP = em.merge((GP) o);
                        em.getTransaction().commit();
                        cmbGP.setModel(new DefaultComboBoxModel(new GP[]{myGP}));
                        resident.setGP(myGP);
                    } catch (Exception ex) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        OPDE.fatal(ex);
                    } finally {
                        em.close();
                    }
                }
            }
        }, btnAddGP);
        GUITools.showPopup(popup, SwingConstants.EAST);
    }

    private void btnEditGPActionPerformed(ActionEvent e) {
        if (cmbGP.getSelectedItem() == null) return;
        final PnlEditGP pnlGP = new PnlEditGP((GP) cmbGP.getSelectedItem());
        final JidePopup popup = GUITools.createPanelPopup(pnlGP, new Closure() {
            @Override
            public void execute(Object o) {
                if (o != null) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        GP myGP = em.merge((GP) o);
                        em.getTransaction().commit();
                        cmbGP.setModel(new DefaultComboBoxModel(new GP[]{myGP}));
                        resident.setGP(myGP);
                    } catch (Exception ex) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        OPDE.fatal(ex);
                    } finally {
                        em.close();
                    }
                }
            }
        }, btnEditGP);
        GUITools.showPopup(popup, SwingConstants.EAST);
    }



    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblName = new JLabel();
        txtName = new JTextField();
        lblFirstname = new JLabel();
        txtFirstname = new JTextField();
        lblDOB = new JLabel();
        txtDOB = new JTextField();
        lblGender = new JLabel();
        panel2 = new JPanel();
        rbMale = new JRadioButton();
        rbFemale = new JRadioButton();
        lblStation = new JLabel();
        cmbStation = new JComboBox();
        lblPrimNurse1 = new JLabel();
        cmbPrimNurse1 = new JComboBox();
        lblPrimNurse2 = new JLabel();
        cmbPrimNurse2 = new JComboBox();
        lblGP = new JLabel();
        cmbGP = new JComboBox();
        panel3 = new JPanel();
        btnAddGP = new JButton();
        btnEditGP = new JButton();
        panel1 = new JPanel();
        btnCancel = new JButton();
        btnApply = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "13dlu, $lcgap, default, $lcgap, default:grow, $lcgap, default, $lcgap, 13dlu",
            "13dlu, 11*($lgap, default), $lgap, 13dlu"));

        //---- lblName ----
        lblName.setText("text");
        lblName.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblName, CC.xy(3, 3));

        //---- txtName ----
        txtName.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(txtName, CC.xywh(5, 3, 3, 1));

        //---- lblFirstname ----
        lblFirstname.setText("text");
        lblFirstname.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblFirstname, CC.xy(3, 5));

        //---- txtFirstname ----
        txtFirstname.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(txtFirstname, CC.xywh(5, 5, 3, 1));

        //---- lblDOB ----
        lblDOB.setText("text");
        lblDOB.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblDOB, CC.xy(3, 7));

        //---- txtDOB ----
        txtDOB.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(txtDOB, CC.xywh(5, 7, 3, 1));

        //---- lblGender ----
        lblGender.setText("text");
        lblGender.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblGender, CC.xy(3, 9));

        //======== panel2 ========
        {
            panel2.setFont(new Font("Arial", Font.PLAIN, 14));
            panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

            //---- rbMale ----
            rbMale.setText("text");
            rbMale.setFont(new Font("Arial", Font.PLAIN, 14));
            panel2.add(rbMale);

            //---- rbFemale ----
            rbFemale.setText("text");
            rbFemale.setFont(new Font("Arial", Font.PLAIN, 14));
            panel2.add(rbFemale);
        }
        contentPane.add(panel2, CC.xy(5, 9));

        //---- lblStation ----
        lblStation.setText("text");
        lblStation.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblStation, CC.xy(3, 11));

        //---- cmbStation ----
        cmbStation.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(cmbStation, CC.xywh(5, 11, 3, 1));

        //---- lblPrimNurse1 ----
        lblPrimNurse1.setText("text");
        lblPrimNurse1.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblPrimNurse1, CC.xy(3, 13));

        //---- cmbPrimNurse1 ----
        cmbPrimNurse1.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(cmbPrimNurse1, CC.xywh(5, 13, 3, 1));

        //---- lblPrimNurse2 ----
        lblPrimNurse2.setText("text");
        lblPrimNurse2.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblPrimNurse2, CC.xy(3, 15));

        //---- cmbPrimNurse2 ----
        cmbPrimNurse2.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(cmbPrimNurse2, CC.xywh(5, 15, 3, 1));

        //---- lblGP ----
        lblGP.setText("text");
        lblGP.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblGP, CC.xy(3, 17));

        //---- cmbGP ----
        cmbGP.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(cmbGP, CC.xy(5, 17));

        //======== panel3 ========
        {
            panel3.setLayout(new HorizontalLayout(5));

            //---- btnAddGP ----
            btnAddGP.setText(null);
            btnAddGP.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
            btnAddGP.setBorderPainted(false);
            btnAddGP.setContentAreaFilled(false);
            btnAddGP.setBorder(null);
            btnAddGP.addActionListener(e -> btnAddGPActionPerformed(e));
            panel3.add(btnAddGP);

            //---- btnEditGP ----
            btnEditGP.setText(null);
            btnEditGP.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/edit3.png")));
            btnEditGP.setBorderPainted(false);
            btnEditGP.setContentAreaFilled(false);
            btnEditGP.setBorder(null);
            btnEditGP.addActionListener(e -> btnEditGPActionPerformed(e));
            panel3.add(btnEditGP);
        }
        contentPane.add(panel3, CC.xy(7, 17));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

            //---- btnCancel ----
            btnCancel.setText(null);
            btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
            btnCancel.addActionListener(e -> btnCancelActionPerformed(e));
            panel1.add(btnCancel);

            //---- btnApply ----
            btnApply.setText(null);
            btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnApply.addActionListener(e -> btnApplyActionPerformed(e));
            panel1.add(btnApply);
        }
        contentPane.add(panel1, CC.xywh(5, 23, 3, 1, CC.RIGHT, CC.FILL));
        pack();
        setLocationRelativeTo(getOwner());

        //---- buttonGroup1 ----
        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(rbMale);
        buttonGroup1.add(rbFemale);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblName;
    private JTextField txtName;
    private JLabel lblFirstname;
    private JTextField txtFirstname;
    private JLabel lblDOB;
    private JTextField txtDOB;
    private JLabel lblGender;
    private JPanel panel2;
    private JRadioButton rbMale;
    private JRadioButton rbFemale;
    private JLabel lblStation;
    private JComboBox cmbStation;
    private JLabel lblPrimNurse1;
    private JComboBox cmbPrimNurse1;
    private JLabel lblPrimNurse2;
    private JComboBox cmbPrimNurse2;
    private JLabel lblGP;
    private JComboBox cmbGP;
    private JPanel panel3;
    private JButton btnAddGP;
    private JButton btnEditGP;
    private JPanel panel1;
    private JButton btnCancel;
    private JButton btnApply;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
