package op.tools;

import com.install4j.api.launcher.Variables;
import entity.system.SYSPropsTools;
import op.OPDE;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;

/**
 * this class handles everything that is needed to adapt to the local machine's structure and needs.
 * including os constraints like paths and serial number generation.
 */
public class LocalMachine {

    /**
     * computes a serial number out of local os tools. it knows how to handle mac, win and unix machines.
     * if, for any reason, the generation fails it reverts to a randomly created UUID which is stored in
     * opde.cfg by the key of SYSPropsTools.KEY_HOSTKEY
     *
     * @return a unique UUID which is used for password encryption as a key
     */
    public static final String getSerialNumber() {

        String cmd = "";
        String marker = "";
        String result = "";

        if (SystemUtils.IS_OS_WINDOWS) {
            cmd = "reg query HKLM\\SOFTWARE\\Microsoft\\Cryptography /v MachineGuid";
            marker = "MachineGuid    REG_SZ";
            result = getSerialNumber(cmd, marker);
        }

        // https://github.com/tloehr/Offene-Pflege.de/issues/36
        if (SystemUtils.IS_OS_LINUX) {
            File machineid = new File("/var/lib/dbus/machine-id"); // most systems have it here
            if (!machineid.exists()) { // in Fedora 19 and 20 the file can be found here.
                machineid = new File("/etc/machine-id");
            }

            try {
                result = FileUtils.readFileToString(machineid).trim();
            } catch (IOException e) {
                result = null;
            }
        }

        if (SystemUtils.IS_OS_MAC_OSX) {
            cmd = "/usr/sbin/system_profiler SPHardwareDataType";
            marker = "Hardware UUID:";
            result = getSerialNumber(cmd, marker);
        }

        // this is always a viable fallback, if everything else fails use the hostkey instead
        if (result == null) result = OPDE.getLocalProps().getProperty(SYSPropsTools.KEY_HOSTKEY);

        OPDE.debug("Serial Number: " + SYSTools.catchNull(result, "null"));

        return result;
    }


    private static final String getSerialNumber(String command, String marker) {
        String result = null;
        final StringBuilder consoleOutput = new StringBuilder();

        CommandLine cmdLine = CommandLine.parse(command);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(0);

        OutputStream output = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                consoleOutput.append((char) b);
            }
        };
        executor.setStreamHandler(new PumpStreamHandler(output));

        // todo: hier gibts ein Timing Problem bei Linux Maschinen
        // todo: warum 2x aufgerufen ?

        try {
            executor.execute(cmdLine);
            Scanner scanner = new Scanner(consoleOutput.toString());
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                OPDE.debug(line);
                if (line.trim().startsWith(marker)) {
                    result = StringUtils.removeStart(line.trim(), marker).trim();
                    OPDE.debug(line);
                    break;
                }
            }
            scanner.close();
        } catch (IOException e) {
            OPDE.warn(e);
            result = null;
        } catch (Exception e) {
            OPDE.error(e);
            result = null;
        }

        return result;
    }


    public static final String getLogPath() {
        return getAppDataPath() + File.separator + "logs";
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


    /**
     * this is the path where OPDE can find all jar files and the directories artwork, dbscripts, license and the system templates
     *
     * @return
     */
    public static final String getProgrammPath() {

        // usually the installer saves this information in response.varfile, but not necessarily
        String install4jDir = SYSTools.catchNull(Variables.getInstallerVariable("sys.installationDir"));

        if (SystemUtils.IS_OS_WINDOWS) {
            return install4jDir.isEmpty() ? System.getenv("ProgramFiles") + File.separator + "Offene-Pflege.de" : install4jDir;
        }
        if (SystemUtils.IS_OS_LINUX) {
            return install4jDir.isEmpty() ? "/opt/opde" : install4jDir;
        }
        if (SystemUtils.IS_OS_MAC_OSX) {
            return install4jDir.isEmpty() ? "/Applications/Offene-Pflege.de.app/Contents/java/app" : install4jDir + "/Offene-Pflege.de.app/Contents/java/app";
        }
        return null;
    }


}
