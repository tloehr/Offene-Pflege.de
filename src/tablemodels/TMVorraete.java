package tablemodels;

import entity.verordnungen.MedVorrat;
import op.tools.Pair;

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
    protected List<Pair<MedVorrat, BigDecimal>> data;

    public TMVorraete(List<Pair<MedVorrat, BigDecimal>> data) {
        this.data = data;
    }

    public BigDecimal getBestandsMenge(int row) {
        BigDecimal menge = data.get(row).getSecond();
        return menge == null ? BigDecimal.ZERO : menge;
    }

    public void setBestandsMenge(int row, BigDecimal menge) {
        data.set(row, new Pair<MedVorrat, BigDecimal>(getVorrat(row), menge));
    }


    public List<Pair<MedVorrat, BigDecimal>> getData() {
        return data;
    }

    public MedVorrat getVorrat(int row) {
        return data.get(row).getFirst();
    }

    public int findPositionOf(MedVorrat vorrat) {
        int pos = -1;
        for (Pair<MedVorrat, BigDecimal> pair : data) {
            if (pair.getFirst().equals(vorrat)) {
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
                result = "["+getVorrat(row).getVorID() +"] " + getVorrat(row).getText();
                break;
            }
            case COL_MENGE: {
                result = getBestandsMenge(row).setScale(2, BigDecimal.ROUND_HALF_UP);
                break;
            }
            default: {
                result = "";
            }
        }
        return result;
    }
}
