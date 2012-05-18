package tablemodels;

import entity.verordnungen.DarreichungTools;
import entity.verordnungen.MedBestand;
import entity.verordnungen.MedBestandTools;
import entity.verordnungen.MedBuchungen;
import op.tools.SYSTools;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 04.01.12
 * Time: 17:15
 * To change this template use File | Settings | File Templates.
 */
public class TMBuchungen extends AbstractTableModel {
    public static final int COL_ID = 0;
    public static final int COL_Datum = 1;
    public static final int COL_Text = 2;
    public static final int COL_Menge = 3;
    public static final int COL_User = 4;
    protected List<MedBuchungen> data;
    protected DateFormat df;

    public TMBuchungen(List<MedBuchungen> data) {
        this.data = data;
        df = DateFormat.getDateInstance();
    }

    public List<MedBuchungen> getData() {
        return data;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int row, int col) {
        Object result = "";

        switch (col){
            case COL_ID : {
                result = data.get(row).getBuchID();
                break;
            }
            case COL_Datum : {
                result = df.format(data.get(row).getPit());
                break;
            }
            case COL_Text : {
                result = SYSTools.catchNull(data.get(row).getText(), "--");
                break;
            }
            case COL_Menge : {
                result = data.get(row).getMenge().setScale(2, BigDecimal.ROUND_HALF_UP);
                break;
            }
            case COL_User : {
                result = data.get(row).getUser().getNameUndVorname();
                break;
            }
            default: {
                result = "";
            }
        }
        return result;
    }
}