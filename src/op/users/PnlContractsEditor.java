/*
 * Created by JFormDesigner on Sat Dec 07 12:14:43 CET 2013
 */

package op.users;

import javax.swing.*;
import com.jgoodies.forms.layout.*;
import entity.roster.ContractsParameterSet;
import entity.roster.UserContract;
import entity.system.Users;
import entity.system.UsersTools;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import org.joda.time.LocalDate;

/**
 * @author Torsten Löhr
 */
public class PnlContractsEditor extends JPanel {

    private JPanel pnlWorkingDaysHours;
    private UserContract contract;
    private JTextField txtWDPW, txtTHPM, txtTHPW, txtTHPD;
    private Users user;

    public PnlContractsEditor(Users user) {
        this(new UserContract(new ContractsParameterSet(SYSCalendar.eom(new LocalDate()).plusDays(1), SYSConst.LD_UNTIL_FURTHER_NOTICE)), user);
        this.contract.getDefaults().setExam(UsersTools.isQualified(user)); // just as a suggestion for new contracts
        this.user = user;
    }

    public PnlContractsEditor(UserContract contract, Users user) {
        this.contract = contract;
        this.user = user;
        initComponents();
        initPanel();
    }


    void initPanel(){
        initWorkingHoursPanel();
    }


    void initWorkingHoursPanel(){
        pnlWorkingDaysHours = new JPanel();
        pnlWorkingDaysHours.setLayout(new BoxLayout(pnlWorkingDaysHours, BoxLayout.X_AXIS));

        txtWDPW



    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, default:grow, $lcgap, default",
            "3*(default, $lgap), default"));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }





    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
