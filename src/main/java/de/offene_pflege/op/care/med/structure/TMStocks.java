package de.offene_pflege.op.care.med.structure;

import de.offene_pflege.entity.EntityTools;
import de.offene_pflege.entity.prescription.MedStock;
import de.offene_pflege.op.tools.SYSTools;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.math.NumberUtils;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.util.ArrayList;

@Log4j2
public class TMStocks extends AbstractTableModel {
    public static final int COL_UPR = 6;
    private final ArrayList<MedStock> listStocks;
    private final String[] columnNames;

    public TMStocks(ArrayList<MedStock> listStocks) {
        this.listStocks = listStocks;
        columnNames = new String[]{SYSTools.xx("upreditor.col1"), SYSTools.xx("upreditor.col2"), SYSTools.xx("upreditor.col3"), SYSTools.xx("upreditor.col4"), SYSTools.xx("upreditor.col5"), SYSTools.xx("upreditor.col6"), SYSTools.xx("upreditor.col7")};
    }

    @Override
    public int getRowCount() {
        return listStocks.size();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getColumnCount() {
        return 7;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public boolean isCellEditable(int row, int column) {
        return column == COL_UPR;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        MedStock medStock = listStocks.get(rowIndex);
        try {
            BigDecimal upr = NumberUtils.toScaledBigDecimal(aValue.toString(), 2, RoundingMode.HALF_UP);
            if (upr.compareTo(BigDecimal.ZERO) >= 0) {
                medStock.setUPR(upr);
                listStocks.set(rowIndex, EntityTools.merge(medStock));
            } else {
                log.warn("number must be positive '{}'", aValue);
            }
        } catch (NumberFormatException nfe) {
            log.warn("wrong number format for '{}'", aValue);
        }
        fireTableCellUpdated(rowIndex, columnIndex);

//          MedOrder medOrder = medOrderList.get(row);
//          if (column == COL_complete) {
//              Boolean complete = (Boolean) aValue;
//              medOrder.setClosed_on(complete ? LocalDateTime.now() : null);
//              medOrder.setClosed_by(complete ? OPDE.getLogin().getUser() : null);
//              if (!complete) medOrder.setCreated_by(OPDE.getLogin().getUser());
//          } else if (column == COL_WHERE_TO_ORDER) {
//              if (aValue instanceof GP) {
//                  medOrder.setGp((GP) aValue);
//                  medOrder.setHospital(null);
//              } else {
//                  medOrder.setGp(null);
//                  medOrder.setHospital((Hospital) aValue);
//              }
//              medOrder.setCreated_by(OPDE.getLogin().getUser());
//          } else if (column == COL_note) {
//              medOrder.setNote(StringUtils.abbreviate(aValue.toString().trim(), 200));
//              medOrder.setCreated_by(OPDE.getLogin().getUser());
//          }
//          medOrderList.set(row, EntityTools.merge(medOrder));
//          if (column == COL_complete) fireTableCellUpdated(row, COL_TradeForm);
//          fireTableCellUpdated(row, column);
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object result;

        switch (columnIndex) {
            case 0: {
                result = listStocks.get(rowIndex).getID();
                break;
            }
            case 1: {
                result = listStocks.get(rowIndex).getInventory().getResident().getId();
                break;
            }
            case 2: {
                result = DateFormat.getDateInstance().format(listStocks.get(rowIndex).getIN());
                break;
            }
            case 3: {
                result = "--";//SYSTools.formatBigDecimal(MedStockTools.getStartTX(listStocks.get(rowIndex)).getAmount().setScale(2, RoundingMode.HALF_UP));
                break;
            }
            case 4: {
                result = "--";
                break;
            }
            case 5: {
                if (!listStocks.get(rowIndex).isClosed())
                    result = "OPEN";
                else
                    result = SYSTools.formatBigDecimal(listStocks.get(rowIndex).getUPREffective().setScale(2, RoundingMode.HALF_UP));
                break;
            }
            case 6: {
                result = SYSTools.formatBigDecimal(listStocks.get(rowIndex).getUPR().setScale(2, RoundingMode.HALF_UP));
                break;
            }
            default: {
                result = "ERROR";
            }
        }
        return result;
    }


}
