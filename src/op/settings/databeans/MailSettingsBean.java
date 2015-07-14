package op.settings.databeans;

import entity.system.SYSPropsTools;
import gui.interfaces.EditorComponent;
import op.tools.SYSTools;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;
import java.util.Properties;

/**
 * Created by tloehr on 26.06.15.
 */
public class MailSettingsBean {

    @Size(min = 1, max = 200, message = "msg.validation.string.length.error")
    @EditorComponent(label = "opde.settings.global.mail.host", component = {"textfield"})
    String host = "";
    @Size(min = 1, max = 200, message = "msg.validation.string.length.error")
    @EditorComponent(label = "opde.settings.global.mail.port", component = {"textfield"})
    String port = "25";
    @Size(min = 1, max = 200, message = "msg.validation.string.length.error")
    @EditorComponent(label = "opde.settings.global.mail.user", component = {"textfield"})
    String user = "";
    @Size(min = 0, max = 200, message = "msg.validation.string.length.error")
    @EditorComponent(label = "opde.settings.global.mail.password", component = {"textfield"})
    String password = "";
    @Size(min = 1, max = 200, message = "msg.validation.string.length.error")
    @EditorComponent(label = "opde.settings.global.mail.sender", component = {"textfield"})
    String sender = "";
    @Size(min = 1, max = 200, message = "msg.validation.string.length.error")
    @EditorComponent(label = "opde.settings.global.mail.recipient", component = {"textfield"})
    String recipient = "";
    @Size(min = 1, max = 200, message = "msg.validation.string.length.error")
    @EditorComponent(label = "opde.settings.global.mail.sender.personal", component = {"textfield"})
    String sender_personal = "";
    @Size(min = 1, max = 200, message = "msg.validation.string.length.error")
    @EditorComponent(label = "opde.settings.global.mail.recipient.personal", component = {"textfield"})
    String sender_recipient = "";
    @Size(min = 0, max = 200, message = "msg.validation.string.length.error")
    @EditorComponent(label = "opde.settings.global.mail.recipient.spamfilter", component = {"textfield"})
    String spamfiler_key = "";
    @EditorComponent(label = "opde.settings.global.mail.auth", component = {"onoffswitch"}, filled = "false")
    boolean auth = false;
    @EditorComponent(label = "opde.settings.global.mail.starttls", component = {"onoffswitch"}, filled = "false")
    boolean starttls = false;
    @EditorComponent(label = "opde.settings.global.mail.tls", component = {"onoffswitch"}, filled = "false")
    boolean tls = false;

    public MailSettingsBean(Properties preset) {
        host = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_MAIL_HOST));
        port = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_MAIL_PORT), "25");
        user = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_MAIL_USER));
        password = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_MAIL_PASSWORD));
        sender = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_MAIL_SENDER));
        recipient = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_MAIL_RECIPIENT));
        sender_personal = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_MAIL_SENDER_PERSONAL));
        sender_recipient = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_MAIL_RECIPIENT_PERSONAL));
        spamfiler_key = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_MAIL_SPAMFILTER_KEY));
        auth = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_MAIL_AUTH)).equalsIgnoreCase("true");
        starttls = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_MAIL_STARTTLS)).equalsIgnoreCase("true");
        tls = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_MAIL_TLS)).equalsIgnoreCase("true");
    }


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSender_personal() {
        return sender_personal;
    }

    public void setSender_personal(String sender_personal) {
        this.sender_personal = sender_personal;
    }

    public String getSender_recipient() {
        return sender_recipient;
    }

    public void setSender_recipient(String sender_recipient) {
        this.sender_recipient = sender_recipient;
    }

    public String getSpamfiler_key() {
        return spamfiler_key;
    }

    public void setSpamfiler_key(String spamfiler_key) {
        this.spamfiler_key = spamfiler_key;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public boolean isStarttls() {
        return starttls;
    }

    public void setStarttls(boolean starttls) {
        this.starttls = starttls;
    }

    public boolean isTls() {
        return tls;
    }

    public void setTls(boolean tls) {
        this.tls = tls;
    }

    public Properties toProperties(Properties props) {
        props.put(SYSPropsTools.KEY_MAIL_HOST, host.trim());
        props.put(SYSPropsTools.KEY_MAIL_PORT, port.trim());
        props.put(SYSPropsTools.KEY_MAIL_USER, user.trim());
        props.put(SYSPropsTools.KEY_MAIL_PASSWORD, password.trim());
        props.put(SYSPropsTools.KEY_MAIL_SENDER, sender.trim());
        props.put(SYSPropsTools.KEY_MAIL_RECIPIENT, recipient.trim());
        props.put(SYSPropsTools.KEY_MAIL_SENDER_PERSONAL, sender_personal.trim());
        props.put(SYSPropsTools.KEY_MAIL_RECIPIENT_PERSONAL, sender_recipient.trim());
        props.put(SYSPropsTools.KEY_MAIL_AUTH, Boolean.toString(auth));
        props.put(SYSPropsTools.KEY_MAIL_TLS, Boolean.toString(tls));
        props.put(SYSPropsTools.KEY_MAIL_STARTTLS, Boolean.toString(starttls));
        props.put(SYSPropsTools.KEY_MAIL_SPAMFILTER_KEY, SYSTools.tidy(SYSTools.catchNull(spamfiler_key)));
        return props;
    }
}
