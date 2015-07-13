package op.settings.databeans;

import entity.system.Users;
import gui.interfaces.EditorComponent;
import op.tools.SYSTools;
import org.hibernate.validator.constraints.NotEmpty;

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

    public PersonalMailBean(Users user) {
        this.mail = SYSTools.catchNull(user.getEMail());
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

}
