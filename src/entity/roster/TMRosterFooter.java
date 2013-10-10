package entity.roster;

import com.jidesoft.converter.ConverterContext;
import com.jidesoft.converter.DoubleConverter;
import com.jidesoft.grid.AbstractMultiTableModel;
import com.jidesoft.grid.CellStyle;
import com.jidesoft.grid.ColumnIdentifierTableModel;
import com.jidesoft.grid.StyleModel;
import entity.Homes;
import entity.HomesTools;
import org.apache.commons.collections.Closure;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 20.08.13
 * Time: 15:43
 * To change this template use File | Settings | File Templates.
 */
public class TMRosterFooter extends AbstractMultiTableModel implements ColumnIdentifierTableModel, StyleModel {
    TMRoster basemodel;
    ArrayList<Homes> listHomes;

    private static final long serialVersionUID = -9132647394140127017L;

    public TMRosterFooter(TMRoster model) {
        basemodel = model;
        listHomes = new ArrayList<Homes>(HomesTools.getAll());

        basemodel.setFooterUpdateListener(new Closure() {
            @Override
            public void execute(Object o) {
                for (int row = 0; row < getRowCount(); row++) {
                    fireTableCellUpdated(row, (Integer) o);
//                    OPDE.debug(String.format("row %s, col %s", row, o));
                }
            }
        });
    }

    @Override
    public CellStyle getCellStyleAt(int rowIndex, int columnIndex) {
       return basemodel.getCellStyleAt(rowIndex, columnIndex);
    }

    @Override
    public boolean isCellStyleOn() {
        return true;
    }

    @Override
    public String getColumnName(int column) {
        return basemodel.getColumnName(column);
    }

    @Override
    public Object getColumnIdentifier(int columnIndex) {
        return basemodel.getColumnIdentifier(columnIndex);
    }

    @Override
    public int getColumnCount() {
        return basemodel.getColumnCount();
    }

    @Override
    public int getRowCount() {
        return listHomes.size() * 3;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return basemodel.getColumnClass(columnIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = "footer";
        if (columnIndex == 0) {
            value = "";
            if (rowIndex % 3 == 0) {
                value = listHomes.get(rowIndex / 3).getShortname();
            }
        } else if (columnIndex == 1) {
            if (rowIndex % 3 == 0) {
                value = "Examen F / S / N";
            } else if (rowIndex % 3 == 1) {
                value = "Helfer F / S / N";
            } else if (rowIndex % 3 == 2) {
                value = "Sozial F / S / N";
            }
        } else if (getColumnType(columnIndex) == FOOTER_COLUMN) {
            value = "";
        } else { // here is the homestats data
            //HomeStats stats = basemodel.getHomestats().get(listHomes.get(rowIndex / 3)).get(columnIndex - TMRoster.ROW_HEADER);

            StatsPerDay stats = basemodel.getStatsPerDay(columnIndex - TMRoster.ROW_HEADER).get(listHomes.get(rowIndex / 3));

            if (rowIndex % 3 == 0) {
                value = String.format("EF%s ES%s EN%s", stats.exam_early, stats.exam_late, stats.exam_night);
            } else if (rowIndex % 3 == 1) {
                value = String.format("HF%s HS%s HN%s", stats.helper_early, stats.helper_late, stats.helper_night);
            } else if (rowIndex % 3 == 2) {
                value = String.format("SF%s SS%s SN%s", stats.social_early, stats.social_late, stats.social_night);
            }
        }

        return value;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public int getColumnType(int column) {
        return basemodel.getColumnType(column);
    }

    @Override
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