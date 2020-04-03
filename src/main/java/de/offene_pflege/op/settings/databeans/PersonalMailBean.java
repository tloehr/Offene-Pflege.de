package de.offene_pflege.op.settings.databeans;

import de.offene_pflege.backend.entity.system.OPUsers;
import de.offene_pflege.gui.interfaces.EditorComponent;
import de.offene_pflege.op.tools.SYSTools;
import javax.validation.constraints.NotEmpty;

/**
 * Created by tloehr on 11.07.15.
 */
public class PersonalMailBean {

    @NotEmpty
    @EditorComponent(label = "opde.settings.personal.mailaddress", parserClass = "gui.parser.MailParser", component = {"textfield"})
    String mail;

    public PersonalMailBean() {
        this.mail = "";
    }

    public PersonalMailBean(OPUsers user) {
        this.mail = SYSTools.catchNull(user.getEMail());
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

}
