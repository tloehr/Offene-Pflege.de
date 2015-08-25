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

        return getAppDataPath() + File.separator + "logs";



    }

    public static final String getAppDataPath() {

//        String fallback = System.getProperty("user.home") + File.separator + ".opde";
//
////        if (SystemUtils.IS_OS_LINUX) {
////            return System.getProperty("user.home") + File.separator + ".opde";
////        }
//
//        return com.install4j.api.launcher.Variables.getInstallerVariable("sys.appdataDir") != null ?  com.install4j.api.launcher.Variables.getInstallerVariable("sys.appdataDir").toString() + File.separator + "Offene-Pflege.de" : fallback;
//

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


    /**
     * this is the path where OPDE can find all jar files and the directories artwork, dbscripts, license and the system templates
     * @return
     */
    public static final String getProgrammPath() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return System.getenv("ProgramFiles") + File.separator + "Offene-Pflege.de";
        }
        if (SystemUtils.IS_OS_LINUX) {
            return "/usr/local/opde";
        }
        if (SystemUtils.IS_OS_MAC_OSX) {
            return "/Applications/Offene-Pflege.de.app/Contents/java/app";
        }
        return null;
    }
}
