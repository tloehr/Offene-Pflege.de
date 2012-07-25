package tablemodels;

import entity.verordnungen.DarreichungTools;
import entity.verordnungen.MedBestand;
import entity.verordnungen.MedBestandTools;
import op.OPDE;
import op.tools.Pair;
import op.tools.SYSConst;

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
public class TMBestand extends AbstractTableModel {
    public static final int COL_NAME = 0;
    public static final int COL_MENGE = 1;
    protected List<Pair<MedBestand, BigDecimal>> data;

    public TMBestand(List<Pair<MedBestand, BigDecimal>> data) {
        this.data = data;
    }

    public List<Pair<MedBestand, BigDecimal>> getData() {
        return data;
    }

    public BigDecimal getBestandsMenge(int row) {
        return data.get(row).getSecond();
    }

    public void setBestandsMenge(int row, BigDecimal menge) {
        data.set(row, new Pair<MedBestand, BigDecimal>(getBestand(row), menge));
    }

    public MedBestand getBestand(int row) {
        return data.get(row).getFirst();
    }

    public int findPositionOf(MedBestand bestand) {
        int pos = -1;
        for (Pair<MedBestand, BigDecimal> pair : data) {
            if (pair.getFirst().equals(bestand)) {
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
                result = MedBestandTools.getBestandAsHTML(getBestand(row));
                break;
            }
            case COL_MENGE: {
                result = "<font face=\"" + SYSConst.ARIAL14.getFamily() + "\">"+ getBestandsMenge(row).setScale(2, BigDecimal.ROUND_HALF_UP) + " " + DarreichungTools.getPackungsEinheit(getBestand(row).getDarreichung())+"</font>";
                break;
            }
            default: {
                result = "";
            }
        }
        return result;
    }
}
