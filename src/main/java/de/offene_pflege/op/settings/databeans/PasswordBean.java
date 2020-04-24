package de.offene_pflege.op.settings.databeans;

import de.offene_pflege.gui.interfaces.EditorComponent;
import javax.validation.constraints.NotEmpty;

/**
 * Created by tloehr on 10.07.15.
 */
public class PasswordBean {

    @NotEmpty(message = "opde.settings.personal.oldpw.empty")
    @EditorComponent(label = "opde.settings.personal.oldpw", parserClass = "de.offene_pflege.gui.parser.OldPasswordParser", component = {"textfield"})
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
