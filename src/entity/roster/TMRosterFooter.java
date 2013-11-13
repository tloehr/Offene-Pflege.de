package entity.roster;

import com.jidesoft.converter.ConverterContext;
import com.jidesoft.converter.DoubleConverter;
import com.jidesoft.grid.AbstractMultiTableModel;
import com.jidesoft.grid.CellStyle;
import com.jidesoft.grid.ColumnIdentifierTableModel;
import com.jidesoft.grid.StyleModel;
import entity.Homes;
import entity.HomesTools;
import op.OPDE;
import op.tools.GUITools;
import op.tools.SYSConst;
import org.apache.commons.collections.Closure;
import org.joda.time.DateTimeConstants;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
        boolean dateline = rowIndex == 0;
        int lineidx = (rowIndex - 1) / 3;
        CellStyle myStyle = new CellStyle();
        myStyle.setHorizontalAlignment(SwingConstants.CENTER);

        Font font = basemodel.getFont();

        Homes home = null;
        if (!dateline) {
            home = listHomes.get((rowIndex - 1) / 3);
        }
        Color background = SYSConst.bluegrey;
        Color foreground = Color.BLACK;
        if (dateline) {
            if (basemodel.isInPlanningArea(columnIndex)) {
                if (basemodel.getDay(columnIndex).getDayOfWeek() == DateTimeConstants.SUNDAY || basemodel.getDay(columnIndex).getDayOfWeek() == DateTimeConstants.SATURDAY) {
                    background = GUITools.blend(SYSConst.bluegrey, Color.black, 0.85f);
                }
                if (OPDE.isHoliday(basemodel.getDay(columnIndex))) {
                    background = GUITools.blend(SYSConst.bluegrey, Color.black, 0.8f);
                }
            } else {
                background = SYSConst.bluegrey;
            }
        } else {

            // basedata
            if (columnIndex >= TMRoster.ROW_HEADER && columnIndex < getColumnCount() - TMRoster.ROW_FOOTER_WIDTH) {
                if (basemodel.getDay(columnIndex).getDayOfWeek() == DateTimeConstants.SUNDAY || basemodel.getDay(columnIndex).getDayOfWeek() == DateTimeConstants.SATURDAY) {
                    if (lineidx % 2 == 0) {
                        if (basemodel.getDay(columnIndex).getDayOfWeek() == DateTimeConstants.SUNDAY || basemodel.getDay(columnIndex).getDayOfWeek() == DateTimeConstants.SATURDAY) {
                            background = SYSConst.greyscale[SYSConst.light4];
                        }
                        if (OPDE.isHoliday(basemodel.getDay(columnIndex))) {
                            background = SYSConst.greyscale[SYSConst.light3];
                        }
                    } else {

                        if (basemodel.getDay(columnIndex).getDayOfWeek() == DateTimeConstants.SUNDAY || basemodel.getDay(columnIndex).getDayOfWeek() == DateTimeConstants.SATURDAY) {
                            background = SYSConst.greyscale[SYSConst.medium2];
                        }
                        if (OPDE.isHoliday(basemodel.getDay(columnIndex))) {
                            background = SYSConst.greyscale[SYSConst.medium3];
                        }
                    }
                } else {
                    if (lineidx % 2 == 0) {
                        background = Color.WHITE;
                    } else {
                        background = SYSConst.greyscale[SYSConst.medium1];
                    }
                }


                if ((rowIndex - 1) % 3 == 0) {


                    if (basemodel.getStatsPerDay().getExam_early(home, columnIndex - TMRoster.ROW_HEADER).equals(BigDecimal.ZERO) ||
                            basemodel.getStatsPerDay().getExam_late(home, columnIndex - TMRoster.ROW_HEADER).equals(BigDecimal.ZERO) ||
                            basemodel.getStatsPerDay().getExam_night(home, columnIndex - TMRoster.ROW_HEADER).equals(BigDecimal.ZERO)) {
                        foreground = Color.red.darker();
                    }


//                    foreground = basemodel.getStatsPerDay().getExam_early(home, columnIndex - TMRoster.ROW_HEADER).equals(BigDecimal.ZERO) ? Color.RED.darker() : Color.BLACK;
                }

                font = font.deriveFont(13f);
                font = font.deriveFont(Font.PLAIN);

//                if ((rowIndex - 1) % 3 == 1) {
//                    foreground = basemodel.getStatsPerDay().getExam_late(home, columnIndex - TMRoster.ROW_HEADER).equals(BigDecimal.ZERO) ? Color.RED.darker() : Color.BLACK;
//                }
//
//                if ((rowIndex - 1) % 3 == 2) {
//                    foreground = basemodel.getStatsPerDay().getExam_night(home, columnIndex - TMRoster.ROW_HEADER).equals(BigDecimal.ZERO) ? Color.RED.darker() : Color.BLACK;
//                }

            }
        }


        myStyle.setFont(font);
        myStyle.setBackground(background);
        myStyle.setForeground(foreground);

        return myStyle;
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
        return listHomes.size() * 3 + 1;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return basemodel.getColumnClass(columnIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        boolean dateline = rowIndex == 0;
        int lineidx = (rowIndex - 1) % 3;

        Object value = "";
        if (columnIndex == 0) {
            value = "";

            if (!dateline && lineidx == 0) {
                value = listHomes.get((rowIndex - 1) / 3).getShortname();
            }
        } else if (columnIndex == 1) {
            if (dateline) {
                value = "Datum";
            } else if (lineidx == 0) {
                value = "Examen";
            } else if (lineidx == 1) {
                value = "Helfer";
            } else if (lineidx == 2) {
                value = "Sozial";
            }
        } else if (columnIndex >= TMRoster.COL_HEADER) { // here are the homestats data
            if (dateline) {
                value = basemodel.getDay(columnIndex).toString("dd.MM.");
            } else {
                StatsPerDay stats = basemodel.getStatsPerDay();
                Homes home = listHomes.get((rowIndex - 1) / 3);
                int day = columnIndex - TMRoster.ROW_HEADER;
                if (lineidx == 0) {
                    value = String.format("F%s S%s N%s", stats.getExam_early(home, day).setScale(1, RoundingMode.HALF_UP), stats.getExam_late(home, day).setScale(1, RoundingMode.HALF_UP), stats.getExam_night(home, day).setScale(1, RoundingMode.HALF_UP));
                } else if (lineidx == 1) {
                    value = String.format("F%s S%s N%s", stats.getHelper_early(home, day).setScale(1, RoundingMode.HALF_UP), stats.getHelper_late(home, day).setScale(1, RoundingMode.HALF_UP), stats.getHelper_night(home, day).setScale(1, RoundingMode.HALF_UP));
                } else if (lineidx == 2) {
                    value = String.format("F%s S%s N%s", stats.getSocial_early(home, day).setScale(1, RoundingMode.HALF_UP), stats.getSocial_late(home, day).setScale(1, RoundingMode.HALF_UP), stats.getSocial_night(home, day).setScale(1, RoundingMode.HALF_UP));
                }
            }
        } else if (getColumnType(columnIndex) == FOOTER_COLUMN) {
            value = "";
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