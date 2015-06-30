package op.settings;

/**
 * Created by tloehr on 30.06.15.
 */
public class MedicationConfigBean {
    boolean handleStocks;

    public MedicationConfigBean(boolean handleStocks) {
        this.handleStocks = handleStocks;
    }

    public boolean isHandleStocks() {
        return handleStocks;
    }

    public void setHandleStocks(boolean handleStocks) {
        this.handleStocks = handleStocks;
    }
}
