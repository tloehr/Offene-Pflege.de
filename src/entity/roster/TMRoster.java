package entity.roster;

import com.jidesoft.grid.*;
import entity.system.Users;
import op.OPDE;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 17.08.13
 * Time: 11:59
 * To change this template use File | Settings | File Templates.
 */
public class TMRoster extends AbstractMultiTableModel implements ColumnIdentifierTableModel, HeaderStyleModel, StyleModel {
    HashMap<Users, ArrayList<RPlan>> content = new HashMap<Users, ArrayList<RPlan>>();
    ArrayList<Users> listUsers = new ArrayList<Users>();
    private final DateMidnight month;
    private CellStyle cellStyle = new CellStyle();

    public TMRoster(ArrayList<RPlan> completeRoster, DateMidnight month) {
        this.month = month.dayOfMonth().withMinimumValue();
        prepareContent(completeRoster);
    }

    @Override
    public Object getColumnIdentifier(int column) {
        return "Column " + (column + 1);

    }

    @Override
    public CellStyle getHeaderStyleAt(int i, int i2) {
        return cellStyle;
    }

    @Override
    public boolean isHeaderStyleOn() {
        return false;
    }

    @Override
    public int getColumnType(int column) {
        if (column < 2) {
            return HEADER_COLUMN;
        } else if (column >= getColumnCount() - 1) {
            return FOOTER_COLUMN;
        } else {
            return REGULAR_COLUMN;
        }

    }

    @Override
    public int getTableIndex(int i) {
        return 0;
    }

    @Override
    public CellStyle getCellStyleAt(int i, int i2) {
        return cellStyle;
    }

    @Override
    public boolean isCellStyleOn() {
        return false;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    private void prepareContent(ArrayList<RPlan> input) {
        for (RPlan rplan : input) {

            if (!content.containsKey(rplan.getOwner())) {
                content.put(rplan.getOwner(), new ArrayList<RPlan>(month.dayOfMonth().withMaximumValue().getDayOfMonth()));

                for (int i = 0; i < month.dayOfMonth().withMaximumValue().getDayOfMonth(); i++) {
                    content.get(rplan.getOwner()).add(null);
                }


                listUsers.add(rplan.getOwner());
            }

            DateTime start = new DateTime(rplan.getStart());
            content.get(rplan.getOwner()).add(start.getDayOfMonth(), rplan);
        }
    }

    @Override
    public String getColumnName(int column) {
        return  null;
    }

    public void cleanup() {
        content.clear();
        listUsers.clear();
    }

    @Override
    public int getRowCount() {
        return listUsers.size();
    }

    @Override
    public int getColumnCount() {
        return month.dayOfMonth().withMaximumValue().getDayOfMonth() + 3;
    }

    private DateMidnight getDay(int col) {
        if (col < 2 || col >= getColumnCount()) return null;
        return month.plusDays(col - 2);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        String value;
        if (columnIndex == 0) {
            value = "Name";
        } else if (columnIndex == 1) {
            value = "Vorher";
        } else if (columnIndex == getRowCount() - 1) {
            value = "Nachher";
        } else {
            value = "F";//content.get(listUsers.get(rowIndex)).get(columnIndex - 2).getP1();
        }
        return value;

    }
}
