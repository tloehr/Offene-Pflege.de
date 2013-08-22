package entity.roster;

import com.jidesoft.grid.AbstractMultiTableModel;
import com.jidesoft.grid.CellStyle;
import com.jidesoft.grid.ColumnIdentifierTableModel;
import com.jidesoft.grid.StyleModel;
import entity.system.Users;
import entity.system.UsersTools;
import op.OPDE;
import op.tools.Pair;
import op.tools.SYSConst;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 17.08.13
 * Time: 11:59
 * To change this template use File | Settings | File Templates.
 */
public class TMRoster extends AbstractMultiTableModel implements ColumnIdentifierTableModel, StyleModel {
    HashMap<Users, ArrayList<RPlan>> content = new HashMap<Users, ArrayList<RPlan>>();
    ArrayList<Users> listUsers = new ArrayList<Users>();
    HashMap<Users, UserContracts> contracts = new HashMap<Users, UserContracts>();

    private final DateMidnight month;
    private CellStyle cellStyle = new CellStyle();
    public final int ROW_HEADER = 2;
    public final int ROW_FOOTER;
    public final int COL_HEADER = 2;
    public final int COL_FOOTER;

    public CellStyle baseStyle;

    public TMRoster(ArrayList<RPlan> completeRoster, DateMidnight month) {
        this.month = month.dayOfMonth().withMinimumValue();
        prepareContent(completeRoster);
        ROW_FOOTER = getColumnCount() - 1;
        COL_FOOTER = COL_HEADER + getRowCount();

        baseStyle = new CellStyle();
        baseStyle.setFont(SYSConst.ARIAL16);

    }

    @Override
    public Object getColumnIdentifier(int column) {
        return "Column " + (column + 1);

    }

    public int[] getMinimumWidths() {
        int[] mins = new int[getColumnCount()];

        for (int i = 0; i < getColumnCount(); i++) {
            mins[i] = 35;
        }

        return mins;
    }

    public int[] getMaximumWidths() {
        int[] mins = new int[getColumnCount()];

        for (int i = 0; i < getColumnCount(); i++) {
            mins[i] = 70;
        }

        return mins;
    }


    @Override
    public int getColumnType(int column) {
        if (column < ROW_HEADER) {
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
    public CellStyle getCellStyleAt(int rowIndex, int columnIndex) {
        CellStyle myStyle = cellStyle;
        myStyle.setHorizontalAlignment(SwingConstants.CENTER);

        Color[] colors = null;

        if (rowIndex / 4 % 2 == 0) {
            colors = SYSConst.greyscale;
        } else {
            colors = SYSConst.yellow1;
            //myStyle.setBackground(SYSConst.yellow1[6 + (rowIndex % 4)]);
        }

        if (columnIndex >= ROW_HEADER && columnIndex < getColumnCount() - 1) {
            if (getDay(getBaseCol(columnIndex)).getDayOfWeek() == DateTimeConstants.SUNDAY || getDay(getBaseCol(columnIndex)).getDayOfWeek() == DateTimeConstants.SATURDAY) {
                colors = SYSConst.red1;
            }

            if (OPDE.isHoliday(getDay(getBaseCol(columnIndex)))) {
                colors = SYSConst.red2;
            }
        }

        myStyle.setBackground(colors[6 + (rowIndex % 4)]);

        return myStyle;
    }

    @Override
    public boolean isCellStyleOn() {
        return true;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }


    public Pair<Point, Point> getBaseTable() {
        return new Pair(new Point(ROW_HEADER, COL_HEADER), new Point(COL_FOOTER, ROW_FOOTER));
    }

    private void prepareContent(ArrayList<RPlan> input) {
        for (RPlan rplan : input) {

            if (!content.containsKey(rplan.getOwner())) {
                content.put(rplan.getOwner(), new ArrayList<RPlan>());
                for (int i = 0; i < month.dayOfMonth().withMaximumValue().getDayOfMonth(); i++) {
                    content.get(rplan.getOwner()).add(null);
                }
                listUsers.add(rplan.getOwner());

                contracts.put(rplan.getOwner(), UsersTools.getContracts(rplan.getOwner()));
            }

            DateTime start = new DateTime(rplan.getStart());
            content.get(rplan.getOwner()).add(start.getDayOfMonth() - 1, rplan);
        }
        Collections.sort(listUsers, new Comparator<Users>() {
            @Override
            public int compare(Users o1, Users o2) {
                return o1.getFullname().compareToIgnoreCase(o2.getFullname());
            }
        });
    }

    @Override
    public String getColumnName(int column) {
        return null;
    }

    public void cleanup() {
        content.clear();
        listUsers.clear();
    }

    @Override
    public int getRowCount() {
        return listUsers.size() * 4;
    }

    @Override
    public int getColumnCount() {
        return month.dayOfMonth().withMaximumValue().getDayOfMonth() + ROW_HEADER + 1; // there is 1 sum col at the end
    }

    public DateMidnight getDay(int col) {
        return month.plusDays(col);
    }

    public int getBaseCol(int columnIndex) {
        return columnIndex - ROW_HEADER;
    }


    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String value = "--";

        if (getBaseCol(columnIndex) == -2) {
            if (rowIndex % 4 == 0) {
                value = listUsers.get(rowIndex / 4).getName();
            } else if (rowIndex % 4 == 1) {
                value = listUsers.get(rowIndex / 4).getVorname();
            } else if (rowIndex % 4 == 2) {
                value = "";
            } else {

                if (contracts.get(listUsers.get(rowIndex / 4)).getParameterSet(month).isTrainee()) {
                    value = "Schüler";
                } else {
                    value = contracts.get(listUsers.get(rowIndex / 4)).getParameterSet(month).isExam() ? "Examen" : "Helfer";
                }
            }
        } else if (getBaseCol(columnIndex) == -1) {
            value = "0";
        } else if (getBaseCol(columnIndex) >= 0 && getBaseCol(columnIndex) < ROW_FOOTER - 2) {
            if (rowIndex % 4 == 0) {
                value = content.get(listUsers.get(rowIndex / 4)).get(getBaseCol(columnIndex)).getP1();
            } else if (rowIndex % 4 == 1) {
                value = content.get(listUsers.get(rowIndex / 4)).get(getBaseCol(columnIndex)).getP2();
            } else if (rowIndex % 4 == 2) {
                value = content.get(listUsers.get(rowIndex / 4)).get(getBaseCol(columnIndex)).getP3();
            } else {
                BigDecimal basehours = content.get(listUsers.get(rowIndex / 4)).get(getBaseCol(columnIndex)).getBasehours();
                BigDecimal breaktime = content.get(listUsers.get(rowIndex / 4)).get(getBaseCol(columnIndex)).getBreaktime();
                BigDecimal extrahours = content.get(listUsers.get(rowIndex / 4)).get(getBaseCol(columnIndex)).getExtrahours();

                value = basehours.add(breaktime).add(extrahours).setScale(2, RoundingMode.HALF_UP).toString();

//                if (breaktime.compareTo(BigDecimal.ZERO) > 0) {
//                    value += " -" + breaktime.setScale(2, RoundingMode.HALF_UP).toString();
//                }
//
//                if (extrahours.compareTo(BigDecimal.ZERO) > 0) {
//                    value += " +" + extrahours.setScale(2, RoundingMode.HALF_UP).toString();
//                }

            }
        }
        return value;
    }
}
