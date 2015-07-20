package op.settings.databeans;

import entity.system.SYSPropsTools;
import gui.interfaces.EditorComponent;
import op.OPDE;
import op.tools.SYSTools;
import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.NotEmpty;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.validation.constraints.Size;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * Created by tloehr on 02.07.15.
 */
public class DatabaseConnectionBean {

    @NotEmpty
    @Size(min = 1, max = 30, message = "msg.string.length.error")
    @EditorComponent(label = "opde.settings.db.host", component = {"textfield"})
    String host;
    @NotEmpty
    @Size(min = 1, max = 30, message = "msg.string.length.error")
    @EditorComponent(label = "opde.settings.db.port", parserClass = "gui.parser.IntegerParser", component = {"textfield"})
    String port;
    @NotEmpty
    @Size(min = 1, max = 30, message = "msg.string.length.error")
    @EditorComponent(label = "misc.msg.Users", component = {"textfield"})
    String user;
    @Size(min = 0, max = 30, message = "msg.string.length.error")
    @EditorComponent(label = "misc.msg.password", component = {"textfield"})
    String password;
    @NotEmpty
    @Size(min = 1, max = 30, message = "msg.string.length.error")
    @EditorComponent(label = "opde.settings.db.catalog", component = {"textfield"})
    String catalog;

    public DatabaseConnectionBean(Properties preset) {

        Logger logger = Logger.getLogger(getClass());

        host = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_JDBC_HOST));
        port = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_JDBC_PORT), "3306");
        user = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_JDBC_USER), "opdeuser");


        try {
            password = OPDE.getDesEncrypter().decrypt(SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_JDBC_PASSWORD)));
        } catch (BadPaddingException e) {
            password = "";
        } catch (Exception e) {
            OPDE.fatal(logger, e);
        }

        catalog = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_JDBC_CATALOG, "opde"));

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

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public Properties toProperties(Properties myProps) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        myProps.put(SYSPropsTools.KEY_JDBC_HOST, host.trim());
        myProps.put(SYSPropsTools.KEY_JDBC_PORT, port.trim());
        myProps.put(SYSPropsTools.KEY_JDBC_USER, user.trim());
        myProps.put(SYSPropsTools.KEY_JDBC_PASSWORD, OPDE.getDesEncrypter().encrypt(password.trim()));
        myProps.put(SYSPropsTools.KEY_JDBC_CATALOG, catalog.trim());
        return myProps;
    }


}
