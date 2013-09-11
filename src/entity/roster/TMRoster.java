package entity.roster;

import com.jidesoft.converter.ConverterContext;
import com.jidesoft.grid.*;
import entity.Homes;
import entity.HomesTools;
import entity.StationTools;
import entity.system.Users;
import entity.system.UsersTools;
import op.OPDE;
import op.tools.Pair;
import op.tools.SYSConst;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
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
    HashMap<Users, ArrayList<Rplan>> content;
    HashMap<Users, UserContracts> contracts;
    RosterParameters rosterParameters = null;

    private final LocalDate month;
    public final int ROW_HEADER = 2;
    public final int ROW_FOOTER;
    public final int ROW_FOOTER_WIDTH = 1;
    public final int COL_HEADER = 2;
    public final int COL_FOOTER;

    public CellStyle baseStyle;

    private Homes defaultHome;
    HashMap<Homes, ArrayList<DailyStats>> stats;

    public TMRoster(Rosters roster, boolean readOnly) {
        defaultHome = StationTools.getStationForThisHost().getHome();

        this.roster = roster;
        this.readOnly = readOnly;
        this.month = new LocalDate(roster.getMonth());

        content = new HashMap<Users, ArrayList<Rplan>>();
        contracts = new HashMap<Users, UserContracts>();
        stats = new HashMap<Homes, ArrayList<DailyStats>>();

        rosterParameters = RostersTools.getParameters(roster);
        prepareContent(roster.getShifts());

        ROW_FOOTER = getColumnCount() - ROW_FOOTER_WIDTH;
        COL_FOOTER = COL_HEADER + getRowCount();// - (9 * stats.size());

        baseStyle = new CellStyle();
        baseStyle.setFont(SYSConst.ARIAL16);
    }

    @Override
    public Object getColumnIdentifier(int column) {
        return "Column " + (column + 1);

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

        CellStyle myStyle = new CellStyle();
        myStyle.setHorizontalAlignment(SwingConstants.CENTER);

        Color[] colors = null;

        if (rowIndex / 4 % 2 == 0) {
            colors = SYSConst.greyscale;
        } else {
            colors = SYSConst.yellow1;
            //myStyle.setBackground(SYSConst.yellow1[6 + (rowIndex % 4)]);
        }

        // basedata
        if (columnIndex >= ROW_HEADER && columnIndex < getColumnCount() - ROW_FOOTER_WIDTH) {
            Users user = rosterParameters.getUserlist().get(rowIndex / 4);
            Rplan myRplan = content.get(user).get(getBaseCol(columnIndex));

            if (getDay(columnIndex).getDayOfWeek() == DateTimeConstants.SUNDAY || getDay(columnIndex).getDayOfWeek() == DateTimeConstants.SATURDAY) {
                colors = SYSConst.red1;
            }

            if (OPDE.isHoliday(getDay(columnIndex))) {
                colors = SYSConst.red2;
            }

            if (rowIndex % 4 != 3) {
                myStyle.setForeground(myRplan.getHome().getColor());
            }

        }

        myStyle.setBackground(colors[7]);  //+ (rowIndex % 4)
//        myStyle.setFont(new Font("Arial", Font.PLAIN, 28));


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
        boolean preferredHomes = columnIndex == 0 && rowIndex % 4 == 2;
        boolean selectUser = columnIndex == 0 && rowIndex % 4 == 0;

        if (noBorders) {
            Rplan rplan = content.get(rosterParameters.getUserlist().get(rowIndex / 4)).get(getBaseCol(columnIndex));

            boolean p3 = !rplan.getP2().isEmpty();
            boolean p2 = rplan.getP3().isEmpty() && !rplan.getP1().isEmpty();
            boolean p1 = (rplan.getP2().isEmpty() && !rplan.getP1().isEmpty()) || rplan.getP1().isEmpty();

            if (rowIndex % 4 == 0) {
                symbolEditable = p1;
            } else if (rowIndex % 4 == 1) {
                symbolEditable = p2;
            } else if (rowIndex % 4 == 2) {
                symbolEditable = p3;
            }
        }

        return selectUser || preferredHomes || (noBorders && symbolEditable);
    }


    public Pair<Point, Point> getBaseTable() {
        return new Pair(new Point(ROW_HEADER, COL_HEADER), new Point(COL_FOOTER, ROW_FOOTER));
    }

    private void prepareContent(java.util.List<Rplan> input) {

        for (Homes home : HomesTools.getAll()) {
            stats.put(home, new ArrayList<DailyStats>());
            for (int i = 0; i < month.dayOfMonth().withMaximumValue().getDayOfMonth(); i++) {
                stats.get(home).add(new DailyStats());
            }
        }

        for (Rplan rplan : input) {

            if (!content.containsKey(rplan.getOwner())) {
                content.put(rplan.getOwner(), new ArrayList<Rplan>());
                for (int i = 0; i < month.dayOfMonth().withMaximumValue().getDayOfMonth(); i++) {
                    content.get(rplan.getOwner()).add(null);
                }
                if (!rosterParameters.getUserlist().contains(rplan.getOwner())) {
                    rosterParameters.getUserlist().add(rplan.getOwner());
                    rosterParameters.getPreferredHome().put(rplan.getOwner(), defaultHome);
                }

                contracts.put(rplan.getOwner(), UsersTools.getContracts(rplan.getOwner()));

            }

            DateTime start = new DateTime(rplan.getStart());
            content.get(rplan.getOwner()).add(start.getDayOfMonth() - 1, rplan);

            Symbol symbol = rosterParameters.getSymbol(rplan.getEffectiveP());
            stats.get(rplan.getHome()).get(start.getDayOfMonth() - 1).add(symbol.shift1, contracts.get(rplan.getOwner()).getParameterSet(month).isExam(), symbol.getStatval1());
            stats.get(rplan.getHome()).get(start.getDayOfMonth() - 1).add(symbol.shift2, contracts.get(rplan.getOwner()).getParameterSet(month).isExam(), symbol.getStatval2());

        }
//        Collections.sort(listUsers, new Comparator<Users>() {
//            @Override
//            public int compare(Users o1, Users o2) {
//                return o1.getFullname().compareToIgnoreCase(o2.getFullname());
//            }
//        });
    }

    @Override
    public String getColumnName(int column) {
        return null;
    }

    public void cleanup() {
        content.clear();
        contracts.clear();
    }

    @Override
    public int getRowCount() {
        return rosterParameters.getUserlist().size() * 4;// + (9 * stats.size()); // 9 lines for every home (3x exam, 3x helper, 3x social)
    }

    @Override
    public int getColumnCount() {
        return month.dayOfMonth().withMaximumValue().getDayOfMonth() + ROW_HEADER + 1; // there is 1 sum col at the end
    }

    public LocalDate getDay(int col) {
        return month.plusDays(getBaseCol(col));
    }

    private int getBaseCol(int columnIndex) {
        return columnIndex - ROW_HEADER;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

        Users user = rosterParameters.getUserlist().get(rowIndex / 4);
        boolean selectUser = columnIndex == 0 && rowIndex % 4 == 0;

        if (aValue instanceof Homes) {
            rosterParameters.getPreferredHome().put(user, (Homes) aValue);
            fireTableCellUpdated(rowIndex, columnIndex);
        } else if (selectUser) {

            OPDE.debug(aValue);

        } else {
            String newSymbol = aValue.toString();
            Symbol oldSymbol = rosterParameters.getSymbol(getValueAt(rowIndex, columnIndex).toString());
            Symbol symbol = rosterParameters.getSymbol(newSymbol);

            if (!newSymbol.isEmpty() && symbol == null) return;
            if (symbol != null && !rosterParameters.getSymbol(newSymbol).isAllowed(getDay(columnIndex))) return;

            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();

                Rosters myRoster = em.merge(roster);
                em.lock(myRoster, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                Rplan myRplan = em.merge(content.get(user).get(getBaseCol(columnIndex)));
                em.lock(myRplan, LockModeType.OPTIMISTIC);

                if (newSymbol.isEmpty()) {
                    if (rowIndex % 4 == 0) {
                        symbol = null;
                        em.remove(myRplan);
                    } else if (rowIndex % 4 == 1) {
                        myRplan.setP2(null);
                        symbol = rosterParameters.getSymbol(myRplan.getP1());
                    } else if (rowIndex % 4 == 2) {
                        myRplan.setP3(null);
                        symbol = rosterParameters.getSymbol(myRplan.getP2());
                    }
                } else {
                    if (rowIndex % 4 == 0) {
                        myRplan.setP1(newSymbol);
                    } else if (rowIndex % 4 == 1) {
                        myRplan.setP2(newSymbol);
                    } else if (rowIndex % 4 == 2) {
                        myRplan.setP3(newSymbol);
                    }
                }

                if (symbol != null) {
                    myRplan.setBasehours(symbol.getBaseHours());
                    myRplan.setExtrahours(symbol.getExtraHours(getDay(columnIndex), contracts.get(user).getParameterSet(getDay(columnIndex))));
                    myRplan.setBreaktime(symbol.getBreak());
                    myRplan.setStart(symbol.getStart(getDay(columnIndex)).toDate());
                    myRplan.setOwner(em.merge(user));
                    DateTime end = symbol.getEnd(getDay(columnIndex));
                    myRplan.setEnd(end == null ? null : end.toDate());
                }

                em.getTransaction().commit();

                // update the stats
                boolean exam = contracts.get(myRplan.getOwner()).getParameterSet(month).isExam();
                if (oldSymbol != null) {
                    stats.get(myRplan.getHome()).get(new LocalDate(myRplan.getStart()).getDayOfMonth() - 1).subtract(oldSymbol.shift1, exam, oldSymbol.getStatval1());
                    stats.get(myRplan.getHome()).get(new LocalDate(myRplan.getStart()).getDayOfMonth() - 1).subtract(oldSymbol.shift1, exam, oldSymbol.getStatval2());
                }
                if (symbol != null) {
                    stats.get(myRplan.getHome()).get(new LocalDate(myRplan.getStart()).getDayOfMonth() - 1).add(symbol.shift1, exam, symbol.getStatval1());
                    stats.get(myRplan.getHome()).get(new LocalDate(myRplan.getStart()).getDayOfMonth() - 1).add(symbol.shift2, exam, symbol.getStatval2());
                }

                // update the content
                content.get(user).remove(getBaseCol(columnIndex));
                if (em.contains(myRplan)) {
                    content.get(user).add(getBaseCol(columnIndex), myRplan);
                }

                roster = myRoster;

            } catch (OptimisticLockException ole) {
                OPDE.warn(ole);
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

                fireTableCellUpdated(COL_FOOTER, columnIndex);
                fireTableCellUpdated(COL_FOOTER+1, columnIndex);
                fireTableCellUpdated(COL_FOOTER+2, columnIndex);
                fireTableCellUpdated(COL_FOOTER+3, columnIndex);

            }
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = "--";
        Users user = rosterParameters.getUserlist().get(rowIndex / 4);
        if (getBaseCol(columnIndex) == -2) {  // Usernames
            if (rowIndex % 4 == 0) {
                value = user.getName();
            } else if (rowIndex % 4 == 1) {
                value = user.getVorname();
            } else if (rowIndex % 4 == 2) {
                return rosterParameters.getPreferredHome().get(user).getShortname();
            } else {

                if (contracts.get(user).getParameterSet(month).isTrainee()) {
                    value = "Schüler";
                } else {
                    value = contracts.get(user).getParameterSet(month).isExam() ? "{Examen:bold}" : "Helfer";
                }
            }
        } else if (getBaseCol(columnIndex) == -1) { // stats carry
            if (rowIndex % 4 == 0) {
                BigDecimal hoursCarry = WorkAccountTools.getSum(month, user, WorkAccountTools.HOURS);
                value = "Stunden: " + hoursCarry.setScale(2, RoundingMode.HALF_UP).toString();
            } else if (rowIndex % 4 == 1) {
                value = "";
            } else if (rowIndex % 4 == 2) {
                value = "";
            }


        } else if (getBaseCol(columnIndex) >= 0 && getBaseCol(columnIndex) < ROW_FOOTER - 2) {
            if (rowIndex % 4 == 0) {
                value = content.get(user).get(getBaseCol(columnIndex)).getP1();
            } else if (rowIndex % 4 == 1) {
                value = content.get(user).get(getBaseCol(columnIndex)).getP2();
            } else if (rowIndex % 4 == 2) {
                value = content.get(user).get(getBaseCol(columnIndex)).getP3();
                if (!value.toString().isEmpty()) {
                    OPDE.debug(value);
                }
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

    public RosterParameters getRosterParameters() {
        return rosterParameters;
    }


    public HashMap<Homes, ArrayList<DailyStats>> getStats() {
        return stats;
    }
}
