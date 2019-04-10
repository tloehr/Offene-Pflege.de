package op.settings.databeans;

import entity.system.SYSPropsTools;
import gui.interfaces.EditorComponent;
import op.tools.SYSTools;

import javax.validation.constraints.Size;
import java.util.Properties;

/**
 * Created by tloehr on 02.07.15.
 */
public class FTPConfigBean {

    @Size(min = 1, max = 200, message = "msg.string.length.error")
    @EditorComponent(label = "opde.settings.ftp.host", component = {"textfield"})
    String host;
    @Size(min = 1, max = 200, message = "msg.string.length.error")
    @EditorComponent(label = "opde.settings.ftp.port", parserClass = "gui.parser.IntegerParser", component = {"textfield"})
    String port;
    @Size(min = 1, max = 200, message = "msg.string.length.error")
    @EditorComponent(label = "opde.settings.ftp.user", component = {"textfield"})
    String user;
    @Size(min = 0, max = 200, message = "msg.string.length.error")
    @EditorComponent(label = "misc.msg.password", component = {"textfield"})
    String password;
    @Size(min = 0, max = 200, message = "msg.string.length.error")
    @EditorComponent(label = "opde.settings.ftp.wd", component = {"textfield"})
    String workingdir;

    public FTPConfigBean(Properties preset) {
        host = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_FTP_HOST));
        port = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_FTP_PORT), "21");
        user = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_FTP_USER));
        password = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_FTP_PASSWORD));
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
