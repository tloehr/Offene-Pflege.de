package op.settings.databeans;

import gui.interfaces.EditorComponent;
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

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
