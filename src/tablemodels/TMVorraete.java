package tablemodels;

import entity.verordnungen.MedVorrat;

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
    protected List<Object[]> data;

    public TMVorraete(List<Object[]> data) {
        this.data = data;
    }

    public BigDecimal getBestandsMenge(int row) {
        return (BigDecimal) data.get(row)[1];
    }

    public MedVorrat getVorrat(int row) {
        return (MedVorrat) data.get(row)[0];
    }

    public int findPositionOf(MedVorrat vorrat) {
        int pos = -1;
        for (Object[] o : data) {
            if (((MedVorrat) o[0]).equals(vorrat)) {
                pos = data.indexOf(o);
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
                result = getVorrat(row).getText();
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
