package op.settings.databeans;

import entity.system.SYSPropsTools;
import gui.interfaces.EditorComponent;
import op.tools.SYSTools;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Properties;

/**
 * Created by tloehr on 02.07.15.
 */
public class FTPConfigBean {

    @NotEmpty
    @EditorComponent(label = "opde.settings.ftp.host", component = {"textfield"})
    String host = "";
    @NotEmpty
    @EditorComponent(label = "opde.settings.ftp.port", parserClass = "gui.parser.IntegerParser", component = {"textfield"})
    String port = "20";
    @NotEmpty
    @EditorComponent(label = "opde.settings.ftp.user", component = {"textfield"})
    String user;
    @NotEmpty
    @EditorComponent(label = "misc.msg.password", component = {"textfield"})
    String password;
    @NotEmpty
    @EditorComponent(label = "opde.settings.ftp.wd", component = {"textfield"})
    String workingdir = "/";

    public FTPConfigBean(Properties preset) {
        host = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_FTP_HOST));
        port = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_FTP_PORT), "20");
        user = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_FTP_PASSWORD));
        password = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_MAIL_PASSWORD));
        workingdir = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_FTP_WD, "/"));
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

    public String getWorkingdir() {
        return workingdir;
    }

    public void setWorkingdir(String workingdir) {
        this.workingdir = workingdir;
    }

    public Properties toProperties(Properties myFTPProps) {
        myFTPProps.put(SYSPropsTools.KEY_FTP_HOST, host.trim());
        myFTPProps.put(SYSPropsTools.KEY_FTP_PORT, port.trim());
        myFTPProps.put(SYSPropsTools.KEY_FTP_USER, user.trim());
        myFTPProps.put(SYSPropsTools.KEY_FTP_PASSWORD, password.trim());
        myFTPProps.put(SYSPropsTools.KEY_FTP_WD, workingdir.trim());
        return myFTPProps;
    }
}
