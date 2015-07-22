package op.settings.databeans;

import entity.system.SYSPropsTools;
import op.OPDE;
import op.tools.DesEncrypter;
import op.tools.SYSTools;
import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.NotEmpty;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Created by tloehr on 02.07.15.
 */
public class DatabaseConnectionBean {

    @NotEmpty
    String host;
    Integer port;
    @NotEmpty
    String user;
    @NotEmpty
    String password;
    @NotEmpty
    String catalog;

    public DatabaseConnectionBean(Properties preset) {

        Logger logger = Logger.getLogger(getClass());

        host = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_JDBC_HOST));
        port = Integer.parseInt(SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_JDBC_PORT), "3306"));
        catalog = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_JDBC_CATALOG, "opde"));

        if (host.isEmpty() || catalog.isEmpty()) {
            // if the is an old URL in the config file, try to parse it
            String url = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_JDBC_URL));
            if (url.length() >= 13) { // to trim "jdbc:mysql://"
                StringTokenizer st = new StringTokenizer(url.substring(13, url.length()), ":/");
                if (st.countTokens() == 3) {
                    host = st.nextToken();
                    port = Integer.parseInt(st.nextToken());
                    catalog = st.nextToken();
                }
            }

        }
        user = SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_JDBC_USER), "opdeuser");

        try {
            password = OPDE.getDesEncrypter().decrypt(SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_JDBC_PASSWORD)));
        } catch (BadPaddingException e) {
            password = "";
        } catch (Exception e) {
            OPDE.fatal(logger, e);
        }

        // could still be encoded with the old algorithm. trying.
        if (password.isEmpty()) {
            DesEncrypter oldDesEncrypter = new DesEncrypter(SYSTools.catchNull(OPDE.getLocalProps().getProperty(SYSPropsTools.KEY_HOSTKEY)));

            try {
                password = oldDesEncrypter.decrypt(SYSTools.catchNull(preset.getProperty(SYSPropsTools.KEY_JDBC_PASSWORD)));
            } catch (BadPaddingException e) {
                password = "";
            } catch (Exception e) {
                OPDE.fatal(logger, e);
            }
        }


    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
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
        myProps.put(SYSPropsTools.KEY_JDBC_PORT, port.toString());
        myProps.put(SYSPropsTools.KEY_JDBC_USER, user.trim());
        myProps.put(SYSPropsTools.KEY_JDBC_PASSWORD, OPDE.getDesEncrypter().encrypt(password.trim()));
        myProps.put(SYSPropsTools.KEY_JDBC_CATALOG, catalog.trim());
        return myProps;
    }


}
