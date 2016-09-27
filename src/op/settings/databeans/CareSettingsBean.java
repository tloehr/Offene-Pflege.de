package op.settings.databeans;

import gui.interfaces.EditorComponent;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

/**
 * Created by tloehr on 10.07.15.
 */
public class CareSettingsBean {

    @NotEmpty(message = "opde.settings.personal.oldpw.empty")
    @EditorComponent(label = "opde.settings.personal.oldpw", parserClass = "gui.parser.OldPasswordParser", component = {"textfield"})
    String oldPassword;

    @NotEmpty(message = "opde.settings.personal.newpw.empty")
    @EditorComponent(label = "opde.settings.personal.newpw", component = {"textfield"})
    String newPassword;

    @NotEmpty(message = "opde.settings.personal.newpw.again.empty")
    @EditorComponent(label = "opde.settings.personal.newpw.again", component = {"textfield"})
    String newPasswordAgain;


    @Size(min = 0, max = 200, message = "msg.string.length.error")
    @EditorComponent(label = "opde.settings.ftp.wd", component = {"textfield"})
    String bhp_max_minutes_to_withdraw;

    public CareSettingsBean() {
        oldPassword = "";
        newPassword = "";
        newPasswordAgain = "";
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordAgain() {
        return newPasswordAgain;
    }

    public void setNewPasswordAgain(String newPasswordAgain) {
        this.newPasswordAgain = newPasswordAgain;
    }
}
