package op.tools;

import entity.info.Resident;
import gui.interfaces.CleanablePanel;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 04.02.12
 * Time: 14:54
 * To change this template use File | Settings | File Templates.
 */
public abstract class NursingRecordsPanel extends CleanablePanel {
    public NursingRecordsPanel(String internalClassID) {
        super(internalClassID);
    }

    public abstract void switchResident(Resident resident);

}
