package op.tools;

import entity.system.SYSPropsTools;
import op.OPDE;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;


public class LocalMachine {

    /**
     * Return computer serial number.
     *
     * @return Computer's SN
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
        if (SystemUtils.IS_OS_LINUX) {
            cmd = "blkid -o export";
            marker = "UUID=";
            result = getSerialNumber(cmd, marker);
        }
        if (SystemUtils.IS_OS_MAC_OSX) {
            cmd = "/usr/sbin/system_profiler SPHardwareDataType";
            marker = "Hardware UUID:";
            result = getSerialNumber(cmd, marker);
        }

        // this is always a viable fallback, if everything else fails
        if (result == null) result = OPDE.getLocalProps().getProperty(SYSPropsTools.KEY_HOSTKEY);

        OPDE.debug("Serial Number: "+ result);

        return result;
    }


    public static final String getSerialNumber(String command, String marker) {
        String result = null;
        final StringBuilder consoleOutput = new StringBuilder();

//        Map map = new HashMap();

//        map.put("host", jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_HOST));
//        map.put("port", jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_PORT));
//        map.put("user", txtAdmin.getText().trim());
//        map.put("pw", new String(txtPassword.getPassword()).trim());
//        map.put("file", sBackupFile);
//        map.put("catalog", jdbcProps.getProperty(SYSPropsTools.KEY_JDBC_CATALOG));
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

        try {
            executor.execute(cmdLine);
            Scanner scanner = new Scanner(consoleOutput.toString());
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                OPDE.debug(line);
                if (line.trim().startsWith(marker)){
                    result = StringUtils.removeStart(line.trim(), marker).trim();
                    OPDE.debug(line);
                    break;
                }
            }
            scanner.close();
        } catch (IOException e) {
            OPDE.warn(e);
        }

        return result;
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
     *
     * @return
     */
    public static final String getProgrammPath() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return System.getenv("ProgramFiles") + File.separator + "Offene-Pflege.de";
        }
        if (SystemUtils.IS_OS_LINUX) {
            return "/opt/opde";
        }
        if (SystemUtils.IS_OS_MAC_OSX) {
            return "/Applications/Offene-Pflege.de.app/Contents/java/app";
        }
        return null;
    }



}
