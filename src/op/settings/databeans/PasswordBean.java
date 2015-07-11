package op.settings.databeans;

import gui.interfaces.EditorComponent;
import op.tools.SYSTools;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by tloehr on 10.07.15.
 */
public class PasswordBean {

    @NotEmpty(message = "opde.settings.personal.oldpw.empty")
    @EditorComponent(label = "opde.settings.personal.oldpw", component = {"textfield"})
    String oldPassword;

    @NotEmpty(message = "opde.settings.personal.newpw.empty")
    @EditorComponent(label = "opde.settings.personal.newpw", component = {"textfield"})
    String newPassword;

    @NotEmpty(message = "opde.settings.personal.newpw.again.empty")
    @EditorComponent(label = "opde.settings.personal.newpw.again", component = {"textfield"})
    String newPasswordAgain;

    public PasswordBean() {
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
