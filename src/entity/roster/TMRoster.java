package entity.roster;

import com.jidesoft.converter.ConverterContext;
import com.jidesoft.grid.*;
import entity.Homes;
import entity.StationTools;
import entity.system.Users;
import entity.system.UsersTools;
import op.OPDE;
import op.tools.Pair;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
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
    private Rosters roster;
    private final boolean readOnly;
    HashMap<Users, ArrayList<RPlan>> content = new HashMap<Users, ArrayList<RPlan>>();
    ArrayList<Users> listUsers = new ArrayList<Users>();
    HashMap<Users, UserContracts> contracts = new HashMap<Users, UserContracts>();
    RosterParameters rosterParameters = null;
    HashMap<Users, Homes> preferredHome = new HashMap<Users, Homes>();

    private final DateMidnight month;
    private CellStyle cellStyle = new CellStyle();
    public final int ROW_HEADER = 2;
    public final int ROW_FOOTER;
    public final int ROW_FOOTER_WIDTH = 1;
    public final int COL_HEADER = 2;
    public final int COL_FOOTER;

    public CellStyle baseStyle;

    public TMRoster(Rosters roster, boolean readOnly) {
        this.roster = roster;
        this.readOnly = readOnly;
        this.month = new DateMidnight(roster.getMonth());
        prepareContent(roster.getShifts());
        rosterParameters = RostersTools.getParameters(roster);

        ROW_FOOTER = getColumnCount() - ROW_FOOTER_WIDTH;
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
    public Class<?> getCellClassAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0 && rowIndex % 4 == 2) {
            return Homes.class;
        }
        return String.class;
    }

    @Override
    public ConverterContext getConverterContextAt(int i, int i2) {
        return ConverterContext.DEFAULT_CONTEXT;
    }

    @Override
    public EditorContext getEditorContextAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0 && rowIndex % 4 == 2) {
            return new EditorContext("HomesSelectionEditor");
        }
        return super.getEditorContextAt(rowIndex, columnIndex);
    }

    @Override
    public int getColumnType(int column) {
        if (column < ROW_HEADER) {
            return HEADER_COLUMN;
        } else if (column >= getColumnCount() - ROW_FOOTER_WIDTH) {
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

        if (columnIndex >= ROW_HEADER && columnIndex < getColumnCount() - ROW_FOOTER_WIDTH) {
            if (getDay(columnIndex).getDayOfWeek() == DateTimeConstants.SUNDAY || getDay(columnIndex).getDayOfWeek() == DateTimeConstants.SATURDAY) {
                colors = SYSConst.red1;
            }

            if (OPDE.isHoliday(getDay(columnIndex))) {
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

        if (readOnly) return false;

        boolean noBorders = columnIndex >= ROW_HEADER && columnIndex < getColumnCount() - ROW_FOOTER_WIDTH;
        boolean symbolEditable = false;
        boolean preferredHomes = false;

        if (noBorders) {
            RPlan rPlan = content.get(listUsers.get(rowIndex / 4)).get(getBaseCol(columnIndex));

            boolean p3 = rPlan.getP3().isEmpty() && !rPlan.getP2().isEmpty();
            boolean p2 = rPlan.getP2().isEmpty() && !rPlan.getP1().isEmpty();

            if (rowIndex % 4 == 0) {
                symbolEditable = p2;
            } else if (rowIndex % 4 == 1) {
                symbolEditable = p2;
            } else if (rowIndex % 4 == 2) {
                symbolEditable = p3;
            }
        } else {
            // only for the prefered homes combo box
            preferredHomes = columnIndex == 0 && rowIndex % 4 == 2;
        }

        return preferredHomes || (noBorders && symbolEditable);
    }


    public Pair<Point, Point> getBaseTable() {
        return new Pair(new Point(ROW_HEADER, COL_HEADER), new Point(COL_FOOTER, ROW_FOOTER));
    }

    private void prepareContent(java.util.List<RPlan> input) {
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
        preferredHome.clear();
        contracts.clear();
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
        return month.plusDays(getBaseCol(col));
    }

    private int getBaseCol(int columnIndex) {
        return columnIndex - ROW_HEADER;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Users user = listUsers.get(rowIndex / 4);
        if (aValue instanceof Homes) {
            preferredHome.put(user, (Homes) aValue);
            fireTableCellUpdated(rowIndex, columnIndex);
        } else {
            String newSymbol = aValue.toString();

            if (rosterParameters.getSymbol(newSymbol) == null) return;
            if (!rosterParameters.getSymbol(newSymbol).isAllowed(getDay(columnIndex))) return;

            Symbol symbol = rosterParameters.getSymbol(newSymbol);
            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();

                Rosters myRoster = em.merge(roster);
                em.lock(myRoster, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                RPlan myRPlan = em.merge(content.get(user).get(getBaseCol(columnIndex)));
                em.lock(myRPlan, LockModeType.OPTIMISTIC);

                if (rowIndex % 4 == 0) {
                    myRPlan.setP1(newSymbol);
                } else if (rowIndex % 4 == 1) {
                    myRPlan.setP2(newSymbol);
                } else if (rowIndex % 4 == 2) {
                    myRPlan.setP3(newSymbol);
                }

                myRPlan.setBasehours(symbol.getBaseHours());
                myRPlan.setExtrahours(symbol.getExtraHours(getDay(columnIndex), contracts.get(user).getParameterSet(getDay(columnIndex))));
                myRPlan.setBreaktime(symbol.getBreak());
                myRPlan.setStart(symbol.getStart(getDay(columnIndex)).toDate());
                myRPlan.setOwner(em.merge(user));


                DateTime end = symbol.getEnd(getDay(columnIndex));
                myRPlan.setEnd(end == null ? null : end.toDate());

                em.getTransaction().commit();

                content.get(user).remove(getBaseCol(columnIndex));
                content.get(user).add(getBaseCol(columnIndex), myRPlan);
                roster = myRoster;

            } catch (OptimisticLockException ole) {
                OPDE.debug(ole.getMessage());
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                OPDE.fatal(e);
            } finally {
                em.close();

                int startrow = rowIndex - (rowIndex % 4);
                fireTableCellUpdated(startrow, columnIndex);
                fireTableCellUpdated(startrow + 1, columnIndex);
                fireTableCellUpdated(startrow + 2, columnIndex);
                fireTableCellUpdated(startrow + 3, columnIndex);
            }
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = "--";
        Users user = listUsers.get(rowIndex / 4);
        if (getBaseCol(columnIndex) == -2) {
            if (rowIndex % 4 == 0) {
                value = user.getName();
            } else if (rowIndex % 4 == 1) {
                value = user.getVorname();
            } else if (rowIndex % 4 == 2) {
                if (!preferredHome.containsKey(user)) {
                    Homes myHome = StationTools.getStationForThisHost().getHome();
                    String homeid = SYSTools.catchNull(rosterParameters.getPreferredHomeID(user.getUID()));
                    if (!homeid.isEmpty()) {
                        EntityManager em = OPDE.createEM();
                        myHome = em.find(Homes.class, homeid);
                        em.close();
                    }
                    preferredHome.put(user, myHome);
                }

                return preferredHome.get(user);


            } else {

                if (contracts.get(user).getParameterSet(month).isTrainee()) {
                    value = "Schüler";
                } else {
                    value = contracts.get(user).getParameterSet(month).isExam() ? "Examen" : "Helfer";
                }
            }
        } else if (getBaseCol(columnIndex) == -1) {
            value = "0";
        } else if (getBaseCol(columnIndex) >= 0 && getBaseCol(columnIndex) < ROW_FOOTER - 2) {
            if (rowIndex % 4 == 0) {
                value = content.get(user).get(getBaseCol(columnIndex)).getP1();
            } else if (rowIndex % 4 == 1) {
                value = content.get(user).get(getBaseCol(columnIndex)).getP2();
            } else if (rowIndex % 4 == 2) {
                value = content.get(user).get(getBaseCol(columnIndex)).getP3();
            } else {
                BigDecimal basehours = content.get(user).get(getBaseCol(columnIndex)).getBasehours();
                BigDecimal breaktime = content.get(user).get(getBaseCol(columnIndex)).getBreaktime();
                BigDecimal extrahours = content.get(user).get(getBaseCol(columnIndex)).getExtrahours();

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
