package op.tools;

import org.apache.commons.lang3.SystemUtils;

import java.io.File;


public class Hardware {

    /**
     * Return computer serial number.
     *
     * @return Computer's SN
     */
    public static final String getSerialNumber() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return Hardware4Win.getSerialNumber();
        }
        if (SystemUtils.IS_OS_LINUX) {
            return Hardware4Nix.getSerialNumber();
        }
        if (SystemUtils.IS_OS_MAC_OSX) {
            return Hardware4Mac.getSerialNumber();
        }
        return null;
    }

    public static final String getLogPath() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return System.getenv("APPDATA") + File.separator + "Offene-Pflege.de";
        }
        if (SystemUtils.IS_OS_LINUX) {
            return System.getProperty("user.home") + File.separator + ".opde" + File.separator + "logs";
        }
        if (SystemUtils.IS_OS_MAC_OSX) {
            return System.getProperty("user.home") + File.separator + "Library" + File.separator + "Logs" + File.separator + "Offene-Pflege.de";
        }
        return null;
    }

    public static final String getAppDataPath() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return System.getenv("APPDATA") + File.separator + "Offene-Pflege.de";
        }
        if (SystemUtils.IS_OS_LINUX) {
            return System.getProperty("user.home") + File.separator + ".opde";
        }
        if (SystemUtils.IS_OS_MAC_OSX) {
            return System.getProperty("user.home") + File.separator + "Library" + File.separator + "Application Support" + File.separator + "Offene-Pflege.de";
        }
        return null;
    }


    public static final String getProgrammPath() {

        if (SystemUtils.IS_OS_WINDOWS) {
            return System.getenv("ProgramFiles") + File.separator + "Offene-Pflege.de";
        }
        if (SystemUtils.IS_OS_LINUX) {
            return "/usr/local/opde";
        }
        if (SystemUtils.IS_OS_MAC_OSX) {
            return "/Applications/Offene-Pflege.de.app/Contents/Resources";
        }
        return null;
    }
}
