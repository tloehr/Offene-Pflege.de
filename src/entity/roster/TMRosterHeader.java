package entity.roster;

import com.jidesoft.converter.ConverterContext;
import com.jidesoft.converter.DoubleConverter;
import com.jidesoft.grid.AbstractMultiTableModel;
import com.jidesoft.grid.CellStyle;
import com.jidesoft.grid.ColumnIdentifierTableModel;
import com.jidesoft.grid.StyleModel;
import op.OPDE;
import op.tools.Pair;
import op.tools.SYSConst;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 20.08.13
 * Time: 15:42
 * To change this template use File | Settings | File Templates.
 */
public class TMRosterHeader extends AbstractMultiTableModel implements ColumnIdentifierTableModel, StyleModel {
    TMRoster basemodel;
    private static final long serialVersionUID = -9132647394140127017L;
    Pair<Point, Point> basetable;

    public TMRosterHeader(TMRoster model) {
        basemodel = model;
        basetable = basemodel.getBaseTable();
    }

    public CellStyle getCellStyleAt(int rowIndex, int columnIndex) {
        CellStyle cellStyle = new CellStyle();
        cellStyle.setFont(SYSConst.ARIAL14);
        cellStyle.setBackground(SYSConst.bluegrey);
        return cellStyle;
    }

    public boolean isCellStyleOn() {
        return true;
    }

    @Override
    public String getColumnName(int column) {
        return basemodel.getColumnName(column);
    }

    public Object getColumnIdentifier(int columnIndex) {
        return basemodel.getColumnIdentifier(columnIndex);
    }

    public int getColumnCount() {
        return basemodel.getColumnCount();
    }

    public int getRowCount() {
        return 2;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return basemodel.getColumnClass(columnIndex);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        String value = "ERR";

        if (columnIndex >= basetable.getFirst().x && columnIndex < basemodel.getColumnCount() - 1) {
            if (rowIndex == 0) {
                value = basemodel.getDay(basemodel.getBaseCol(columnIndex)).toString("dd.MM.");
            } else if (rowIndex == 1) {
                String holiday = OPDE.getHoliday(basemodel.getDay(basemodel.getBaseCol(columnIndex)));
                value = basemodel.getDay(basemodel.getBaseCol(columnIndex)).toString("EE") + (holiday.isEmpty() ? "" : " (" + holiday + ")");
            }
        } else if (columnIndex == 0) {
            value = "Username";
        } else if (columnIndex == 1) {
            value = "Vorher";
        } else if (columnIndex >= basemodel.getColumnCount() - 1) {
            value = "Nachher";
        }
        return value;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public int getColumnType(int column) {
        return basemodel.getColumnType(column);
    }

    public int getTableIndex(int columnIndex) {
        return 0;
    }

    @Override
    public Class<?> getCellClassAt(int row, int column) {
        return getColumnClass(column);
    }

    @Override
    public ConverterContext getConverterContextAt(int row, int column) {
        return column >= 1 ? DoubleConverter.CONTEXT_FRACTION_NUMBER : null;
    }
}
