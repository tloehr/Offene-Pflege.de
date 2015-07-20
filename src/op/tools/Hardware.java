package op.tools;

import org.apache.commons.lang3.SystemUtils;


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

    public static final String getAppDataPath() {
        final String sep = System.getProperty("file.separator");
        if (SystemUtils.IS_OS_WINDOWS) {
            return System.getenv("APPDATA") + sep + "Offene-Pflege.de";
        }
        if (SystemUtils.IS_OS_LINUX) {
            return System.getProperty("user.home") + sep + ".opde";
        }
        if (SystemUtils.IS_OS_MAC_OSX) {
            return System.getProperty("user.home") + sep + "Library" + sep + "Application Support" + sep + "Offene-Pflege.de";
        }
        return null;
    }
}
