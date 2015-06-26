package op.settings;

import gui.interfaces.EditorComponent;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by tloehr on 26.06.15.
 */
public class MailSettingsBean {

    @NotEmpty
    @EditorComponent(label = "opde.settings.global.mail.host", component = {"textfield"})
    String host = "";
    @NotEmpty
    @EditorComponent(label = "opde.settings.global.mail.port", component = {"textfield"})
    String port = "";
    @NotEmpty
    @EditorComponent(label = "opde.settings.global.mail.user", component = {"textfield"})
    String user = "";
    @NotEmpty
    @EditorComponent(label = "opde.settings.global.mail.password", component = {"textfield"})
    String password = "";
    @NotEmpty
    @EditorComponent(label = "opde.settings.global.mail.sender", component = {"textfield"})
    String sender = "";
    @NotEmpty
    @EditorComponent(label = "opde.settings.global.mail.recipient", component = {"textfield"})
    String recipient = "";
    @NotEmpty
    @EditorComponent(label = "opde.settings.global.mail.sender.personal", component = {"textfield"})
    String sender_personal = "";
    @NotEmpty
    @EditorComponent(label = "opde.settings.global.mail.recipient.personal", component = {"textfield"})
    String sender_recipient = "";
    @NotEmpty
    @EditorComponent(label = "opde.settings.global.mail.recipient.spamfilter", component = {"textfield"})
    String spamfiler_key = "";
    @EditorComponent(label = "opde.settings.global.mail.auth", component = {"onoffswitch"})
    boolean auth = false;
    @EditorComponent(label = "opde.settings.global.mail.starttls", component = {"onoffswitch"})
    boolean starttls = false;
    @EditorComponent(label = "opde.settings.global.mail.tls", component = {"onoffswitch"})
    boolean tls = false;

    public MailSettingsBean() {


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
}
