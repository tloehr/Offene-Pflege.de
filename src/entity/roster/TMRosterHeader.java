package entity.roster;

import com.jidesoft.converter.ConverterContext;
import com.jidesoft.converter.DoubleConverter;
import com.jidesoft.grid.AbstractMultiTableModel;
import com.jidesoft.grid.CellStyle;
import com.jidesoft.grid.ColumnIdentifierTableModel;
import com.jidesoft.grid.StyleModel;
import op.OPDE;
import op.tools.GUITools;
import op.tools.SYSConst;
import org.joda.time.DateTimeConstants;

import javax.swing.*;
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
//    Pair<Point, Point> basetable;

    public TMRosterHeader(TMRoster model) {
        basemodel = model;
//        basetable = basemodel.getBaseTable();
    }

    public CellStyle getCellStyleAt(int rowIndex, int columnIndex) {
        CellStyle cellStyle = new CellStyle();

        cellStyle.setBackground(SYSConst.bluegrey);
        if (basemodel.isInPlanningArea(columnIndex)) {
            if (basemodel.getDay(columnIndex).getDayOfWeek() == DateTimeConstants.SUNDAY || basemodel.getDay(columnIndex).getDayOfWeek() == DateTimeConstants.SATURDAY) {
                cellStyle.setBackground(GUITools.blend(SYSConst.bluegrey, Color.black, 0.85f));
            }
            if (OPDE.isHoliday(basemodel.getDay(columnIndex))) {
                cellStyle.setBackground(GUITools.blend(SYSConst.bluegrey, Color.black, 0.8f));
            }
        }

        cellStyle.setFont(basemodel.getFont());

        cellStyle.setHorizontalAlignment(SwingConstants.CENTER);
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
        String value = "";

        if (columnIndex >= TMRoster.ROW_HEADER && columnIndex < basemodel.getColumnCount()) {
            if (rowIndex == 0) {
                value = basemodel.getDay(columnIndex).toString("dd.MM.yy");
            } else if (rowIndex == 1) {
                String holiday = OPDE.getHoliday(basemodel.getDay(columnIndex));
                value = basemodel.getDay(columnIndex).toString("EE") + (holiday.isEmpty() ? "" : " (" + holiday + ")");
            }
        } else if (columnIndex == 0 && rowIndex == 1) {
            value = "Username";
        } else if (columnIndex == 1 && rowIndex == 1) {
            value = "Statistik";
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
