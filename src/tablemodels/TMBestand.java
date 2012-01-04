package tablemodels;

import entity.verordnungen.*;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 04.01.12
 * Time: 12:10
 * To change this template use File | Settings | File Templates.
 */
public class TMBestand extends AbstractTableModel{
    public static final int COL_NAME = 0;
    public static final int COL_MENGE = 1;
    protected List<Object[]> data;

    public TMBestand(List<Object[]> data) {
        this.data = data;
    }

    public BigDecimal getBestandsMenge(int row) {
        return (BigDecimal) data.get(row)[1];
    }

    public MedBestand getBestand(int row) {
        return (MedBestand) data.get(row)[0];
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
        switch (col){
            case COL_NAME : {
                result = MedBestandTools.getBestandAsHTML(getBestand(row));
                break;
            }
            case COL_MENGE : {
                result = getBestandsMenge(row).setScale(2, BigDecimal.ROUND_HALF_UP) + " " + DarreichungTools.getPackungsEinheit(getBestand(row).getDarreichung());
                break;
            }
            default: {
                result = "";
            }
        }
        return result;
    }
}
