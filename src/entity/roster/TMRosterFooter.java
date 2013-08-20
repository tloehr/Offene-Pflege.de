package entity.roster;

import com.jidesoft.converter.ConverterContext;
import com.jidesoft.converter.DoubleConverter;
import com.jidesoft.grid.*;

import javax.swing.table.TableModel;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 20.08.13
 * Time: 15:43
 * To change this template use File | Settings | File Templates.
 */
public class TMRosterFooter  extends AbstractMultiTableModel implements ColumnIdentifierTableModel, StyleModel {
        TableModel basemodel;
        private static final long serialVersionUID = -9132647394140127017L;

        public TMRosterFooter(TableModel model) {
            basemodel = model;
        }

        public CellStyle getCellStyleAt(int rowIndex, int columnIndex) {
            return new CellStyle();
        }

        public boolean isCellStyleOn() {
            return false;
        }

        @Override
        public String getColumnName(int column) {
            return basemodel.getColumnName(column);
        }

        public Object getColumnIdentifier(int columnIndex) {
            return ((ColumnIdentifierTableModel) basemodel).getColumnIdentifier(columnIndex);
        }

        public int getColumnCount() {
            return basemodel.getColumnCount();
        }

        public int getRowCount() {
            return 1;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return basemodel.getColumnClass(columnIndex);
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            return 0;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        public int getColumnType(int column) {
            return ((MultiTableModel) basemodel).getColumnType(column);
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