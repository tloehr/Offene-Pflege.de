/*
 * Created by JFormDesigner on Sat Nov 03 11:45:21 CET 2012
 */

package op.residents;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import entity.Rooms;
import entity.RoomsTools;
import entity.Station;
import entity.StationTools;
import entity.info.LCustodian;
import entity.info.LCustodianTools;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.prescription.Doc;
import entity.prescription.DocTools;
import entity.system.Users;
import entity.system.UsersTools;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.HorizontalLayout;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
    private JToggleButton tbAdminOnly;
    Date dob = null;

    public DlgEditResidentBaseData(Resident resident, Closure actionBlock) {
        super();
        this.resident = resident;
        this.actionBlock = actionBlock;
        initComponents();
        initDialog();
        pack();
        setVisible(true);
    }


    private void initDialog() {
        lblName.setText(OPDE.lang.getString("misc.msg.name"));
        lblFirstname.setText(OPDE.lang.getString("misc.msg.firstname"));
        lblDOB.setText(OPDE.lang.getString("misc.msg.dob"));
        lblGender.setText(OPDE.lang.getString("misc.msg.gender"));
        lblRoom.setText(OPDE.lang.getString("misc.msg.room"));
        lblStation.setText(OPDE.lang.getString("misc.msg.subdivision"));
        lblPrimNurse1.setText(OPDE.lang.getString("misc.msg.primaryNurse") + " 1");
        lblPrimNurse2.setText(OPDE.lang.getString("misc.msg.primaryNurse") + " 2");
        lblGP.setText(OPDE.lang.getString("misc.msg.gp"));
        lblLCust.setText(OPDE.lang.getString("misc.msg.lc"));
        rbMale.setText(OPDE.lang.getString("misc.msg.male"));
        rbFemale.setText(OPDE.lang.getString("misc.msg.female"));

        rbMale.setSelected(resident.getGender() == ResidentTools.MALE);
        rbFemale.setSelected(resident.getGender() == ResidentTools.FEMALE);

        txtName.setText(resident.getNameNeverAnonymous());
        txtFirstname.setText(resident.getFirstnameNeverAnonymous());

        txtDOB.setText(DateFormat.getDateInstance().format(resident.getDOB()));

        ArrayList<Doc> listGPs = DocTools.getAllActive();
        listGPs.add(0, null);
        cmbGP.setModel(new DefaultComboBoxModel(listGPs.toArray()));
        cmbGP.setRenderer(DocTools.getRenderer());
        cmbGP.setSelectedItem(resident.getGP());

        cmbStation.setModel(StationTools.getAll4Combobox(false));
        cmbStation.setRenderer(SYSTools.getDefaultRenderer());
        cmbStation.setSelectedItem(resident.getStation());
        cmbStation.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (ignoreEvent) return;
                    ignoreEvent = true;
                    if (cmbRoom.getSelectedItem() != null && ((Rooms) cmbRoom.getSelectedItem()).getStation() != e.getItem()) {
                        // somebody changed the station so that the room doesnt fit anymore
                        // set to null then
                        cmbRoom.setSelectedItem(null);
                    }
                    ignoreEvent = false;
                }
            }
        });

        ArrayList<Users> listUsers = UsersTools.getUsers(false);
        listUsers.add(0, null);
        cmbPrimNurse1.setModel(new DefaultComboBoxModel(listUsers.toArray()));
        cmbPrimNurse1.setRenderer(UsersTools.getRenderer());
        cmbPrimNurse1.setSelectedItem(resident.getPN1());
        cmbPrimNurse2.setModel(new DefaultComboBoxModel(listUsers.toArray()));
        cmbPrimNurse2.setRenderer(UsersTools.getRenderer());
        cmbPrimNurse2.setSelectedItem(resident.getPN2());

        ArrayList<LCustodian> listLCs = LCustodianTools.getAllActive();
        listLCs.add(0, null);
        cmbLCust.setModel(new DefaultComboBoxModel(listLCs.toArray()));
        cmbLCust.setRenderer(LCustodianTools.getRenderer());
        cmbLCust.setSelectedItem(resident.getLCustodian1());

        ArrayList<Rooms> listRooms = RoomsTools.getAllActive();
        listGPs.add(0, null);
        cmbRoom.setModel(new DefaultComboBoxModel(listRooms.toArray()));
        cmbRoom.setRenderer(SYSTools.getDefaultRenderer());
        cmbRoom.setSelectedItem(resident.getRoom());
        cmbRoom.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (ignoreEvent) return;
                ignoreEvent = true;
                if (e.getStateChange() == ItemEvent.SELECTED && e.getItem() != null) {
                    cmbStation.setSelectedItem(((Rooms) e.getItem()).getStation());
                }
                ignoreEvent = false;
            }
        });

        tbAdminOnly = GUITools.getNiceToggleButton(OPDE.lang.getString("misc.msg.adminonly"));
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
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".dobXX"), DisplayMessage.WARNING));
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
        resident.setGP((Doc) cmbGP.getSelectedItem());
        resident.setLCustodian1((LCustodian) cmbLCust.getSelectedItem());
        resident.setPN1((Users) cmbPrimNurse1.getSelectedItem());
        resident.setPN2((Users) cmbPrimNurse2.getSelectedItem());
        resident.setRoom((Rooms) cmbRoom.getSelectedItem());
        resident.setStation((Station) cmbStation.getSelectedItem());

        resident.setAdminonly(tbAdminOnly.isSelected() ? ResidentTools.ADMINONLY : ResidentTools.NORMAL);

        dispose();
    }

    private void btnAddGPActionPerformed(ActionEvent e) {
        final PnlEditGP pnlGP = new PnlEditGP(new Doc());
        final JidePopup popup = createPopup(pnlGP);
        popup.setOwner(btnAddGP);
        popup.showPopup(SwingConstants.EAST, btnAddGP);
    }

    private JidePopup createPopup(final PnlEditGP pnlGP) {
        final JidePopup popup = new JidePopup();
        popup.setMovable(false);
        JPanel pnl = new JPanel(new BorderLayout(10, 10));

        pnl.add(pnlGP, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));

        JButton save = new JButton(SYSConst.icon22apply);
//        save.setAlignmentX(0.0f);
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popup.hidePopup();
                if (pnlGP.getDoc() != null) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        Doc myGP = em.merge(pnlGP.getDoc());
                        em.getTransaction().commit();
                        cmbGP.setModel(new DefaultComboBoxModel(new Doc[]{myGP}));
                        resident.setGP(myGP);
                    } catch (Exception ex) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        OPDE.fatal(ex);
                    } finally {
                        em.close();
                    }
//                    cmbGP.setModel(new DefaultComboBoxModel(new Doc[]{pnlGP.getDoc()}));
//                    resident.setGP(pnlGP.getDoc());
                }
            }
        });
        btnPanel.add(Box.createHorizontalGlue());
        btnPanel.add(save);
        pnl.add(btnPanel, BorderLayout.SOUTH);

        popup.setContentPane(pnl);
        popup.setPreferredSize(pnl.getPreferredSize());
        pnl.revalidate();
        popup.removeExcludedComponent(pnl);
        popup.setDefaultFocusComponent(pnl);
        return popup;
    }

    private JidePopup createPopup(final PnlEditLC pnlLC) {
        final JidePopup popup = new JidePopup();
        popup.setMovable(false);
        JPanel pnl = new JPanel(new BorderLayout(10, 10));

        pnl.add(pnlLC, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));

        JButton save = new JButton(SYSConst.icon22apply);
//        save.setAlignmentX(0.0f);
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popup.hidePopup();
                if (pnlLC.getLCustodian() != null) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        LCustodian myLC = em.merge(pnlLC.getLCustodian());
                        em.getTransaction().commit();
                        cmbLCust.setModel(new DefaultComboBoxModel(new LCustodian[]{myLC}));
                        resident.setLCustodian1(myLC);
                    } catch (Exception ex) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        OPDE.fatal(ex);
                    } finally {
                        em.close();
                    }
//                    cmbLCust.setModel(new DefaultComboBoxModel(new LCustodian[]{pnlLC.getLCustodian()}));
//                    resident.setLCustodian1(pnlLC.getLCustodian());
                }
            }
        });
        btnPanel.add(Box.createHorizontalGlue());
        btnPanel.add(save);
        pnl.add(btnPanel, BorderLayout.SOUTH);

        popup.setContentPane(pnl);
        popup.setPreferredSize(pnl.getPreferredSize());
        pnl.revalidate();
        popup.removeExcludedComponent(pnl);
        popup.setDefaultFocusComponent(pnl);
        return popup;
    }

    private void btnEditGPActionPerformed(ActionEvent e) {
        if (cmbGP.getSelectedItem() == null) return;
        final PnlEditGP pnlGP = new PnlEditGP((Doc) cmbGP.getSelectedItem());
        final JidePopup popup = createPopup(pnlGP);
        popup.setOwner(btnEditGP);
        popup.showPopup(SwingConstants.EAST, btnEditGP);
    }

    private void btnEditLCActionPerformed(ActionEvent e) {
        if (cmbLCust.getSelectedItem() == null) return;
        final PnlEditLC pnlLC = new PnlEditLC((LCustodian) cmbLCust.getSelectedItem());
        final JidePopup popup = createPopup(pnlLC);
        popup.setOwner(btnEditLC);
        popup.showPopup(SwingConstants.EAST, btnEditLC);
    }

    private void btnAddLCActionPerformed(ActionEvent e) {
        final PnlEditLC pnlLC = new PnlEditLC(new LCustodian());
        final JidePopup popup = createPopup(pnlLC);
        popup.setOwner(btnAddLC);
        popup.showPopup(SwingConstants.EAST, btnAddLC);
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
        lblRoom = new JLabel();
        cmbRoom = new JComboBox();
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
        lblLCust = new JLabel();
        cmbLCust = new JComboBox();
        panel4 = new JPanel();
        btnAddLC = new JButton();
        btnEditLC = new JButton();
        panel1 = new JPanel();
        btnCancel = new JButton();
        btnApply = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "13dlu, $lcgap, default, $lcgap, default:grow, $lcgap, default, $lcgap, 13dlu",
            "13dlu, 12*($lgap, default), $lgap, 13dlu"));

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

        //---- lblRoom ----
        lblRoom.setText("text");
        lblRoom.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblRoom, CC.xy(3, 11));

        //---- cmbRoom ----
        cmbRoom.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(cmbRoom, CC.xywh(5, 11, 3, 1));

        //---- lblStation ----
        lblStation.setText("text");
        lblStation.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblStation, CC.xy(3, 13));

        //---- cmbStation ----
        cmbStation.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(cmbStation, CC.xywh(5, 13, 3, 1));

        //---- lblPrimNurse1 ----
        lblPrimNurse1.setText("text");
        lblPrimNurse1.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblPrimNurse1, CC.xy(3, 15));

        //---- cmbPrimNurse1 ----
        cmbPrimNurse1.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(cmbPrimNurse1, CC.xywh(5, 15, 3, 1));

        //---- lblPrimNurse2 ----
        lblPrimNurse2.setText("text");
        lblPrimNurse2.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblPrimNurse2, CC.xy(3, 17));

        //---- cmbPrimNurse2 ----
        cmbPrimNurse2.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(cmbPrimNurse2, CC.xywh(5, 17, 3, 1));

        //---- lblGP ----
        lblGP.setText("text");
        lblGP.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblGP, CC.xy(3, 19));

        //---- cmbGP ----
        cmbGP.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(cmbGP, CC.xy(5, 19));

        //======== panel3 ========
        {
            panel3.setLayout(new HorizontalLayout(5));

            //---- btnAddGP ----
            btnAddGP.setText(null);
            btnAddGP.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
            btnAddGP.setBorderPainted(false);
            btnAddGP.setContentAreaFilled(false);
            btnAddGP.setBorder(null);
            btnAddGP.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnAddGPActionPerformed(e);
                }
            });
            panel3.add(btnAddGP);

            //---- btnEditGP ----
            btnEditGP.setText(null);
            btnEditGP.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/edit3.png")));
            btnEditGP.setBorderPainted(false);
            btnEditGP.setContentAreaFilled(false);
            btnEditGP.setBorder(null);
            btnEditGP.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnEditGPActionPerformed(e);
                }
            });
            panel3.add(btnEditGP);
        }
        contentPane.add(panel3, CC.xy(7, 19));

        //---- lblLCust ----
        lblLCust.setText("text");
        lblLCust.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblLCust, CC.xy(3, 21));

        //---- cmbLCust ----
        cmbLCust.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(cmbLCust, CC.xy(5, 21));

        //======== panel4 ========
        {
            panel4.setLayout(new HorizontalLayout(5));

            //---- btnAddLC ----
            btnAddLC.setText(null);
            btnAddLC.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
            btnAddLC.setBorderPainted(false);
            btnAddLC.setContentAreaFilled(false);
            btnAddLC.setBorder(null);
            btnAddLC.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnAddLCActionPerformed(e);
                }
            });
            panel4.add(btnAddLC);

            //---- btnEditLC ----
            btnEditLC.setText(null);
            btnEditLC.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/edit3.png")));
            btnEditLC.setBorderPainted(false);
            btnEditLC.setContentAreaFilled(false);
            btnEditLC.setBorder(null);
            btnEditLC.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnEditLCActionPerformed(e);
                }
            });
            panel4.add(btnEditLC);
        }
        contentPane.add(panel4, CC.xy(7, 21));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

            //---- btnCancel ----
            btnCancel.setText(null);
            btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
            btnCancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnCancelActionPerformed(e);
                }
            });
            panel1.add(btnCancel);

            //---- btnApply ----
            btnApply.setText(null);
            btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnApply.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnApplyActionPerformed(e);
                }
            });
            panel1.add(btnApply);
        }
        contentPane.add(panel1, CC.xywh(5, 25, 3, 1, CC.RIGHT, CC.FILL));
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
    private JLabel lblRoom;
    private JComboBox cmbRoom;
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
    private JLabel lblLCust;
    private JComboBox cmbLCust;
    private JPanel panel4;
    private JButton btnAddLC;
    private JButton btnEditLC;
    private JPanel panel1;
    private JButton btnCancel;
    private JButton btnApply;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
