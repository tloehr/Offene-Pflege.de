package tablemodels;

import entity.prescription.DosageFormTools;
import entity.prescription.MedInventory;
import entity.prescription.MedInventoryTools;
import op.tools.Pair;
import op.tools.SYSConst;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 03.01.12
 * Time: 17:08
 * To change this template use File | Settings | File Templates.
 */
public class TMVorraete extends AbstractTableModel {
    public static final int COL_NAME = 0;
    public static final int COL_MENGE = 1;
    protected List<Pair<MedInventory, BigDecimal>> data;

    public TMVorraete(List<Pair<MedInventory, BigDecimal>> data) {
        this.data = data;
    }

    public BigDecimal getBestandsMenge(int row) {
        BigDecimal menge = data.get(row).getSecond();
        return menge == null ? BigDecimal.ZERO : menge;
    }

    public void setBestandsMenge(int row, BigDecimal menge) {
        data.set(row, new Pair<MedInventory, BigDecimal>(getVorrat(row), menge));
    }


    public List<Pair<MedInventory, BigDecimal>> getData() {
        return data;
    }

    public MedInventory getVorrat(int row) {
        return data.get(row).getFirst();
    }

    public int findPositionOf(MedInventory inventory) {
        int pos = -1;
        for (Pair<MedInventory, BigDecimal> pair : data) {
            if (pair.getFirst().equals(inventory)) {
                pos = data.indexOf(pair);
                break;
            }
        }
        return pos;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int row, int col) {
        Object result = "";
        switch (col) {
            case COL_NAME: {
                result = MedInventoryTools.getVorratAsHTML(getVorrat(row));
                break;
            }
            case COL_MENGE: {
                result = "<font face=\"" + SYSConst.ARIAL14.getFamily() + "\">" + getBestandsMenge(row).setScale(2, BigDecimal.ROUND_HALF_UP) + " " + DosageFormTools.EINHEIT[MedInventoryTools.getForm(getVorrat(row)).getPackEinheit()] + "</font>";
                break;
            }
            default: {
                result = "";
            }
        }
        return result;
    }
}
