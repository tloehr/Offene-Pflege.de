package entity.system;

import entity.files.SYSFilesTools;
import entity.prescription.MedStockTools;
import entity.prescription.PrescriptionTools;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import org.joda.time.LocalDate;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by tloehr on 12.01.15.
 */
public class NotificationTools {
    public static final String NKEY_DRUG_WEIGHT_CONTROL = "nkey_drug_weight_control";

    public static File[] notify(Users user) throws Exception {

        ArrayList<File> files = new ArrayList<>();

        for (Notification notification : user.getNotifications()) {
            files.add(getControllingFile(notification.getNkey()));
        }

        return files.toArray(new File[]{});
    }


    public static File getControllingFile(String nkey) throws Exception {
        StringBuilder builder = new StringBuilder();
        if (nkey.equals(NKEY_DRUG_WEIGHT_CONTROL)) {
            builder.append(MedStockTools.getNarcoticsWeightList(new LocalDate().minusMonths(1), new LocalDate()));
        }
        return SYSFilesTools.getHtmlFile(builder.toString(), nkey, "html");
    }


    public static Notification find(Users user, String nkey) {
        for (Notification notification : user.getNotifications()) {
            if (notification.getNkey().equals(nkey)) return notification;
        }
        return null;
    }


}
