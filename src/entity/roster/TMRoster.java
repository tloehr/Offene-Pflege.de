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
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import java.awt.*;
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

    public static final int CT_INVALID = -1;
    public static final int CT_EMP_LASTNAME = 0;
    public static final int CT_EMP_FIRSTNAME = 1;
    public static final int CT_EMP_PREFERRED_HOME = 2;
    public static final int CT_EMP_QUALIFICATION = 3;
    public static final int CT_HEADER_USER = 4;
    public static final int CT_HEADER_USER_EMPTY = 5;
    public static final int CT_HEADER_CARRY = 6;
    public static final int CT_HEADER_CARRY_EMPTY = 7;
    public static final int CT_HEADER_SUM = 8;
    public static final int CT_HEADER_SUM_EMPTY = 9;
    public static final int CT_HEADER_DATE = 10;
    public static final int CT_HEADER_WEEKDAY = 11;
    public static final int CT_CARRY_SICK = 12;
    public static final int CT_CARRY_HOLIDAY = 13;
    public static final int CT_CARRY_EMPTY = 14;
    public static final int CT_CARRY_HOURS = 15;
    public static final int CT_SUM_SICK = 16;
    public static final int CT_SUM_HOLIDAY = 17;
    public static final int CT_SUM_EMPTY = 18;
    public static final int CT_SUM_HOURS = 19;
    public static final int CT_P1 = 20;
    public static final int CT_P2 = 21;
    public static final int CT_ACTUAL = 22;
    public static final int CT_HOURS = 23;
    public static final int CT_HEADER_HOME_STAT = 24;
    public static final int CT_HEADER_HOME_STAT_EMPTY1 = 25;
    public static final int CT_HEADER_HOME_STAT_EMPTY2 = 26;
    public static final int CT_HEADER_EXAM_STAT = 27;
    public static final int CT_HEADER_HELPER_STAT = 28;
    public static final int CT_HEADER_SOCIAL_STAT = 29;
    public static final int CT_FOOTER_HOME_STAT = 30;
    public static final int CT_FOOTER_HOME_STAT_EMPTY1 = 31;
    public static final int CT_FOOTER_HOME_STAT_EMPTY2 = 32;

    private Rosters roster;
    private final boolean readOnly;
    private Font font;
    // contains all users which are show on the table. not necessarily all users who own rplans.
    ArrayList<Pair<Users, Homes>> userlist;
    HashMap<Character, Homes> prefixMap;

    // ALL rplans for this roster. iteration over the keyset of this map will return ALL users who own rplanss.
    HashMap<Users, ArrayList<Rplan>> content;
    HashMap<Users, UserContracts> contracts;
    RosterParameters rosterParameters = null;

    private final LocalDate month;
    public static final int ROW_HEADER = 3;
    public final int ROW_FOOTER;
    public static final int ROW_FOOTER_WIDTH = 0;
    public static final int COL_HEADER = 2;
    public static final int COL_SUM = 2;
    public final int COL_FOOTER;

    public CellStyle baseStyle;

    private Homes defaultHome;
    HashMap<Users, StatsPerUser> statsPerUser;
    StatsPerDay statsPerDay;
    private Closure updateFooter = null;


    public TMRoster(Rosters roster, boolean readOnly, Font font) {
        this.roster = roster;
        this.readOnly = readOnly;
        this.font = font;
        this.month = new LocalDate(roster.getMonth());
        defaultHome = StationTools.getStationForThisHost().getHome();

        content = new HashMap<Users, ArrayList<Rplan>>();
        contracts = UsersTools.getUsersWithValidContractsIn(month);
        prefixMap = new HashMap<Character, Homes>();
        statsPerUser = new HashMap<Users, StatsPerUser>();
        userlist = new ArrayList<Pair<Users, Homes>>();
        rosterParameters = RostersTools.getParameters(roster);
        initUserlist();
        prepareContent();

        ROW_FOOTER = getColumnCount() - ROW_FOOTER_WIDTH;
        COL_FOOTER = COL_HEADER + getRowCount();// - (9 * homestats.size());

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
        if (columnIndex == 0 && rowIndex % 4 == 0) {
            return Users.class;
        }
        return String.class;
    }

    public int getCellTypeAt(int rowIndex, int columnIndex) {
        int cellType = CT_INVALID;
        if (columnIndex == 0) {
            if (rowIndex % 4 == 0) {
                cellType = CT_EMP_LASTNAME;
            } else if (rowIndex % 4 == 1) {
                cellType = CT_EMP_FIRSTNAME;
            } else if (rowIndex % 4 == 2) {
                cellType = CT_EMP_PREFERRED_HOME;
            } else if (rowIndex % 4 == 3) {
                cellType = CT_EMP_QUALIFICATION;
            }
        } else if (columnIndex == 1) { // homestats carry
            if (rowIndex % 4 == 0) {
                cellType = CT_CARRY_SICK;
            } else if (rowIndex % 4 == 1) {
                cellType = CT_CARRY_HOLIDAY;
            } else if (rowIndex % 4 == 2) {
                cellType = CT_CARRY_EMPTY;
            } else if (rowIndex % 4 == 3) {
                cellType = CT_CARRY_HOURS;
            }
        } else if (columnIndex == 2) { // homestats sum
            if (rowIndex % 4 == 0) {
                cellType = CT_SUM_SICK;
            } else if (rowIndex % 4 == 1) {
                cellType = CT_SUM_HOLIDAY;
            } else if (rowIndex % 4 == 2) {
                cellType = CT_SUM_EMPTY;
            } else if (rowIndex % 4 == 3) {
                cellType = CT_SUM_HOURS;
            }
        } else if (columnIndex >= ROW_HEADER && columnIndex < ROW_FOOTER) {
            if (rowIndex % 4 == 0) {
                cellType = CT_P1;
            } else if (rowIndex % 4 == 1) {
                cellType = CT_P2;
            } else if (rowIndex % 4 == 2) {
                cellType = CT_ACTUAL;
            } else if (rowIndex % 4 == 3) {
                cellType = CT_HOURS;
            }
        }
//        else if (columnIndex == ROW_FOOTER) {
//                   if (rowIndex % 4 == 0) {
//                       cellType = CT_SUM_SICK;
//                   } else if (rowIndex % 4 == 1) {
//                       cellType = CT_SUM_HOLIDAY;
//                   } else if (rowIndex % 4 == 2) {
//                       cellType = CT_SUM_EMPTY;
//
//                   } else if (rowIndex % 4 == 3) {
//                       cellType = CT_SUM_HOURS;
//                   }
//               }

        return cellType;
    }

    @Override
    public ConverterContext getConverterContextAt(int i, int i2) {
        return ConverterContext.DEFAULT_CONTEXT;
    }

    @Override
    public EditorContext getEditorContextAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0 && rowIndex % 4 == 0) {
            return new EditorContext("UserSelectionEditor");
        }
        if (columnIndex == 0 && rowIndex % 4 == 2) {
            return new EditorContext("HomesSelectionEditor");
        }
        return new EditorContext("DefaultTextEditor");
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

//

    }

    @Override
    public int getTableIndex(int i) {
        return 0;
    }


    @Override
    public CellStyle getCellStyleAt(int rowIndex, int columnIndex) {

        CellStyle myStyle = new CellStyle();
        myStyle.setHorizontalAlignment(SwingConstants.CENTER);

        Color background = null;

        if (rowIndex / 4 % 2 == 0) {
            background = SYSConst.greyscale[SYSConst.medium1];
        } else {
            background = SYSConst.yellow1[SYSConst.medium1];
        }


        // basedata
        if (columnIndex >= ROW_HEADER && columnIndex < getColumnCount() - ROW_FOOTER_WIDTH) {
            if (getDay(columnIndex).getDayOfWeek() == DateTimeConstants.SUNDAY || getDay(columnIndex).getDayOfWeek() == DateTimeConstants.SATURDAY) {
                if (rowIndex / 4 % 2 == 0) {
                    background = SYSConst.red1[SYSConst.medium3];
                } else {
                    background = SYSConst.red1[SYSConst.medium1];
                }
            }

            if (OPDE.isHoliday(getDay(columnIndex))) {
                if (rowIndex / 4 % 2 == 0) {
                    background = SYSConst.red2[SYSConst.medium3];
                } else {
                    background = SYSConst.red2[SYSConst.medium1];
                }
            }

            Users user = userlist.get(rowIndex / 4).getFirst();
            if (user != null) {
                if (rowIndex % 4 == 0) {
                    Rplan myRplan = content.get(user).get(columnIndex - ROW_HEADER);
                    myStyle.setForeground(myRplan == null || myRplan.getP1().isEmpty() ? Color.black : myRplan.getHome1().getColor());
                } else if (rowIndex % 4 == 1) {
                    Rplan myRplan = content.get(user).get(columnIndex - ROW_HEADER);
                    myStyle.setForeground(myRplan == null || myRplan.getP2().isEmpty() ? Color.black : myRplan.getHome2().getColor());
                }
            }
        }

        myStyle.setFont(font);


        myStyle.setBackground(background);  //+ (rowIndex % 4)

        return myStyle;
    }

    public void setFont(Font font) {
        this.font = font;
        fireTableDataChanged();
    }

    public Font getFont() {
        return font;
    }

    @Override
    public boolean isCellStyleOn() {
        return true;
    }

    public boolean isInPlanningArea(int columnIndex) {
        return columnIndex >= ROW_HEADER && columnIndex < ROW_FOOTER;
    }


    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (readOnly) return false;

        boolean symbolEditable = false;
        boolean preferredHomes = columnIndex == 0 && rowIndex % 4 == 2;

        boolean setUser = columnIndex == 0 && rowIndex % 4 == 0;

        Users user = userlist.get(rowIndex / 4).getFirst();

        if (user != null && isInPlanningArea(columnIndex)) {

            Rplan rplan = content.get(user).get(columnIndex - ROW_HEADER);

            if (rplan == null) {
                symbolEditable = rowIndex % 4 == 0; // empty plans must be started at the first row
            } else {
                boolean p2 = rplan.getP3().isEmpty() && !rplan.getP1().isEmpty();
                boolean p1 = (rplan.getP2().isEmpty() && !rplan.getP1().isEmpty()) || rplan.getP1().isEmpty();

                if (rowIndex % 4 == 0) {
                    symbolEditable = p1;
                } else if (rowIndex % 4 == 1) {
                    symbolEditable = p2;

                }
            }
        }
        return setUser || preferredHomes || (symbolEditable && user != null && isInPlanningArea(columnIndex));
    }

    public Pair<Point, Point> getBaseTable() {
        return new Pair(new Point(ROW_HEADER, COL_HEADER), new Point(COL_FOOTER, ROW_FOOTER));
    }


    private void prepareContent() {

        for (Homes home : HomesTools.getAll()) {
            prefixMap.put(home.getPrefix(), home);
        }

        for (Rplan rplan : RPlanTools.getAll(roster)) {
            Users user = rplan.getOwner();

            if (!content.containsKey(user)) {
                content.put(user, new ArrayList<Rplan>());
                for (int i = 0; i < month.dayOfMonth().withMaximumValue().getDayOfMonth(); i++) {
                    content.get(user).add(null);
                }
            }

            DateTime start = new DateTime(rplan.getStart());
            content.get(user).add(start.getDayOfMonth() - 1, rplan);

        }

        for (Users user : statsPerUser.keySet()) {
            statsPerUser.get(user).update(content.get(user));
        }

        statsPerDay = new StatsPerDay(HomesTools.getAll(), month, content, contracts, rosterParameters);

    }

    public void cleanup() {
        content.clear();
        contracts.clear();
//        homeslist.clear();
        userlist.clear();
//        homestats.clear();
        statsPerUser.clear();
//        statsPerDay.clear();
    }

    @Override
    public int getRowCount() {
        return userlist.size() * 4;// + (9 * homestats.size()); // 9 lines for every home (3x exam, 3x helper, 3x social)
    }

    @Override
    public int getColumnCount() {
        return month.dayOfMonth().withMaximumValue().getDayOfMonth() + ROW_HEADER; // there is 1 sum col at the end
    }

    public LocalDate getDay(int columnIndex) {
        return month.plusDays(columnIndex - ROW_HEADER);
    }

//    private int getBaseCol(int columnIndex) {
//        return columnIndex - ROW_HEADER;
//    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

        if (aValue.toString().equalsIgnoreCase(getValueAt(rowIndex, columnIndex).toString())) return;

        Users user = userlist.get(rowIndex / 4).getFirst();
//        boolean selectUser = columnIndex == 0 && rowIndex % 4 == 0;

        int ct = getCellTypeAt(rowIndex, columnIndex);

        if (aValue instanceof Homes) {
            userlist.set(rowIndex / 4, new Pair<Users, Homes>(user, (Homes) aValue));
            fireTableCellUpdated(rowIndex, columnIndex);
        } else if (aValue instanceof Users) {
            Users myUser = (Users) aValue;
            userlist.set(rowIndex / 4, new Pair<Users, Homes>(myUser, defaultHome));

            if (!statsPerUser.containsKey(myUser)) {
                statsPerUser.put(myUser, new StatsPerUser(WorkAccountTools.getSum(month, myUser, WorkAccountTools.HOURS), WorkAccountTools.getSick(month, myUser), WorkAccountTools.getSum(month, myUser, WorkAccountTools.HOLIDAYS), rosterParameters));
                content.put(myUser, new ArrayList<Rplan>());
                for (int i = 0; i < month.dayOfMonth().withMaximumValue().getDayOfMonth(); i++) {
                    content.get(myUser).add(null);
                }
            }

            int startrow = rowIndex - (rowIndex % 4);
            fireTableRowsUpdated(startrow, startrow + 3);
        } else if (ct == CT_EMP_LASTNAME) {
            OPDE.debug(aValue);
        } else {

            Homes prefixHome = null;
            String newSymbol = aValue.toString();
            if (aValue.toString().length() > 1 && prefixMap.containsKey(aValue.toString().charAt(0))) {
                prefixHome = prefixMap.get(aValue.toString().charAt(0));
                newSymbol = aValue.toString().substring(1);
            }

            Symbol symbol = rosterParameters.getSymbol(newSymbol);

            if (!newSymbol.isEmpty() && symbol == null) return; // entered UNKNOWN symbol.
            if (symbol != null && !rosterParameters.getSymbol(newSymbol).isAllowed(getDay(columnIndex)))
                return; // the symbol is valid but not on THAT particular day
            if (newSymbol.isEmpty()) { // he wants to remove the symbol
                emptyCellInMaintable(rowIndex, columnIndex);
                return;
            }

            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();

                Homes preferredHome = em.merge(prefixHome == null ? userlist.get(rowIndex / 4).getSecond() : prefixHome);

                Rplan oldPlan = content.get(user).get(columnIndex - ROW_HEADER);
                if (oldPlan == null) {
                    oldPlan = new Rplan(roster, preferredHome, getDay(columnIndex).toDate(), em.merge(user));
                }

                Rplan myRplan = em.merge(oldPlan);
                em.lock(myRplan, LockModeType.OPTIMISTIC);

//                HomeStats homeStat = homestats.get(myRplan.getHome1()).get(new LocalDate(myRplan.getStart()).getDayOfMonth() - 1);
//                boolean exam = contracts.get(myRplan.getOwner()).getParameterSet(month).isExam();

                if (rowIndex % 4 == 0) {
                    myRplan.setP1(newSymbol);
                    myRplan.setHome1(preferredHome);
                } else if (rowIndex % 4 == 1) {
                    myRplan.setP2(newSymbol);
                    myRplan.setHome2(preferredHome);
                }
//                else if (rowIndex % 4 == 2) {
//                    myRplan.setP3(newSymbol);
//                    myRplan.setHome3(preferredHome);
//                }

                myRplan.setValuesFromSymbol(symbol, contracts.get(user).getParameterSet(getDay(columnIndex)));

//                int type = symbol.getSection() == RosterXML.SOCIAL ? HomeStats.SOCIAL : (exam ? HomeStats.EXAM : HomeStats.HELPER);
//
//                if (oldPlan.getId() == 0) {
//                    homeStat.add(type, rosterParameters.getSymbol(myRplan.getEffectiveP()));
//                } else {
//                    homeStat.replace(type, rosterParameters.getSymbol(oldPlan.getEffectiveP()), rosterParameters.getSymbol(myRplan.getEffectiveP()));
//                }

                em.getTransaction().commit();

                // update the content and stats
                content.get(user).set(columnIndex - ROW_HEADER, myRplan);
                statsPerUser.get(user).update(content.get(user));
                statsPerDay.update(content, columnIndex - ROW_HEADER);


                for (int i : indexOfAllUser(user)) {
                    int startrow = i * 4;

                    OPDE.debug(startrow);
                    OPDE.debug(columnIndex);

                    fireTableCellUpdated(startrow, columnIndex);
                    OPDE.debug(String.format("content(%s, %s): " + getValueAt(startrow, columnIndex), startrow, columnIndex));
                    fireTableCellUpdated(startrow + 1, columnIndex);
                    OPDE.debug(String.format("content(%s, %s): " + getValueAt(startrow + 1, columnIndex), startrow + 1, columnIndex));
                    fireTableCellUpdated(startrow + 2, columnIndex);
                    OPDE.debug(String.format("content(%s, %s): " + getValueAt(startrow + 2, columnIndex), startrow + 2, columnIndex));
                    fireTableCellUpdated(startrow + 3, columnIndex);
                    OPDE.debug(String.format("content(%s, %s): " + getValueAt(startrow + 3, columnIndex), startrow + 3, columnIndex));


                    fireTableCellUpdated(startrow, COL_SUM);
                    fireTableCellUpdated(startrow + 1, COL_SUM);
                    fireTableCellUpdated(startrow + 2, COL_SUM);
                    fireTableCellUpdated(startrow + 3, COL_SUM);

                }

                if (updateFooter != null) {
                    updateFooter.execute(columnIndex);
                }

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

            }
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
//        OPDE.debug(String.format("rowindex: %d   columnindex: %d", rowIndex, columnIndex));
        Object value = "--";
        int ct = getCellTypeAt(rowIndex, columnIndex);

        Users user = userlist.get(rowIndex / 4).getFirst();

        if (user == null) {
            return "";
        }

        if (ct == CT_EMP_LASTNAME) {
            value = user;
        } else if (ct == CT_EMP_FIRSTNAME) {
            value = user.getVorname();
        } else if (ct == CT_EMP_PREFERRED_HOME) {
            value = userlist.get(rowIndex / 4).getSecond();
        } else if (ct == CT_EMP_QUALIFICATION) {
            if (contracts.get(user).getParameterSet(month).isTrainee()) {
                value = "Schüler";
            } else {
                value = contracts.get(user).getParameterSet(month).isExam() ? "<html><b>Examen</b></html>" : "Helfer";
            }
        } else if (ct == CT_CARRY_HOURS) {
            value = "St: " + statsPerUser.get(user).getHoursCarry().setScale(2, RoundingMode.HALF_UP).toString();
        } else if (ct == CT_CARRY_SICK) {
            value = "Kr: " + statsPerUser.get(user).getSickCarry().setScale(2, RoundingMode.HALF_UP).toString();
        } else if (ct == CT_CARRY_HOLIDAY) {
            value = "Url: " + statsPerUser.get(user).getHolidayCarry().setScale(2, RoundingMode.HALF_UP).toString();
        } else if (ct == CT_P1) {
            value = content.get(user).get(columnIndex - ROW_HEADER) != null ? content.get(user).get(columnIndex - ROW_HEADER).getP1() : "";
//            if (!value.toString().isEmpty()) {
//                value = String.format("<html><b>%s</b></html>", value);
//            }
        } else if (ct == CT_P2) {
            value = content.get(user).get(columnIndex - ROW_HEADER) != null ? content.get(user).get(columnIndex - ROW_HEADER).getP2() : "";
        } else if (ct == CT_ACTUAL) {
            value = "";
        } else if (ct == CT_HOURS) {
            Rplan rplan = content.get(user).get(columnIndex - ROW_HEADER);
            value = rplan != null && rplan.getType() != Symbol.PVALUE ? rplan.getNetValue().setScale(2, RoundingMode.HALF_UP).toString() : "";
        } else if (ct == CT_SUM_HOURS) {
            value = "St: " + statsPerUser.get(user).getHoursSum().setScale(2, RoundingMode.HALF_UP).toString();
        } else if (ct == CT_SUM_SICK) {
            value = "Kr: " + statsPerUser.get(user).getSickSum().setScale(2, RoundingMode.HALF_UP).toString();
        } else if (ct == CT_SUM_HOLIDAY) {
            value = "Url: " + statsPerUser.get(user).getHolidaySum().setScale(2, RoundingMode.HALF_UP).toString();
        }

        return value;
    }

    public RosterParameters getRosterParameters() {
        return rosterParameters;
    }


//    public HashMap<Homes, ArrayList<HomeStats>> getHomestats() {
//        return homestats;
//    }

    public void setFooterUpdateListener(Closure updateFooter) {
        this.updateFooter = updateFooter;
    }


    /**
     * clears the selected cell, if possible. Only used to empty the user assignment in the table.
     */
    public void emptyCellInRowheaderTable(int rowIndex, int columnIndex) {

        //        columnIndex = columnIndex;
        //        rowIndex = rowIndex + COL_HEADER;

        //        OPDE.debug(String.format("rowindex: %d   columnindex: %d", rowIndex, columnIndex));

        if (rowIndex % 4 != 0) return;


        userlist.set(rowIndex / 4, new Pair(null, null));


        int startrow = rowIndex - (rowIndex % 4);

        fireTableCellUpdated(startrow, columnIndex);
        fireTableCellUpdated(startrow + 1, columnIndex);
        fireTableCellUpdated(startrow + 2, columnIndex);
        fireTableCellUpdated(startrow + 3, columnIndex);

        fireTableCellUpdated(startrow, getColumnCount() - 1);
        fireTableCellUpdated(startrow + 1, getColumnCount() - 1);
        fireTableCellUpdated(startrow + 2, getColumnCount() - 1);
        fireTableCellUpdated(startrow + 3, getColumnCount() - 1);

        if (updateFooter != null) {
            // adapt columnindex. the keypressed event ignores the column index of the row header table.
            updateFooter.execute(columnIndex + 2);
        }


    }

    public StatsPerDay getStatsPerDay() {
        return statsPerDay;
    }

    /**
     * clears the selected cell, if possible
     */
    public void emptyCellInMaintable(int rowIndex, int columnIndex) {

        Users user = userlist.get(rowIndex / 4).getFirst();

        if (user == null){
            return;
        }

        Rplan oldPlan = content.get(user).get(columnIndex);

        if (oldPlan == null) {
            return;
        }
        if (rowIndex % 4 == 0 && oldPlan.getP1().isEmpty()) {
            return;
        }
        if (rowIndex % 4 == 1 && oldPlan.getP2().isEmpty()) {
            return;
        }
        if (rowIndex % 4 >= 2) {
            return;
        }


        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            Rosters myRoster = em.merge(roster);
            Rplan myRplan = em.merge(oldPlan);

//            HomeStats stat = homestats.get(myRplan.getHome1()).get(new LocalDate(myRplan.getStart()).getDayOfMonth() - 1);
            em.lock(myRplan, LockModeType.OPTIMISTIC);
//            boolean exam = contracts.get(myRplan.getOwner()).getParameterSet(month).isExam();


            if (rowIndex % 4 == 0) {
                em.remove(myRplan);
//                int type = rosterParameters.getSymbol(myRplan.getP1()).getSection() == RosterXML.SOCIAL ? StatsPerDay.SOCIAL : (exam ? StatsPerDay.EXAM : StatsPerDay.HELPER);
//                stat.subtract(type, rosterParameters.getSymbol(myRplan.getP1()));

                myRplan = null;
            } else {

                Symbol symbol = null;

                myRplan.setP2(null);
                myRplan.setHome2(null);
                symbol = rosterParameters.getSymbol(myRplan.getP1());

//                } else if (rowIndex % 4 == 2) {
//                    myRplan.setP3(null);
//                    myRplan.setHome3(null);
//                    symbol = rosterParameters.getSymbol(myRplan.getP2());
//                }

                myRplan.setValuesFromSymbol(symbol, contracts.get(user).getParameterSet(getDay(columnIndex)));
//                int type = rosterParameters.getSymbol(myRplan.getEffectiveP()).getSection() == RosterXML.SOCIAL ? StatsPerDay.SOCIAL : (exam ? StatsPerDay.EXAM : StatsPerDay.HELPER);
//                stat.replace(type, rosterParameters.getSymbol(oldPlan.getEffectiveP()), rosterParameters.getSymbol(myRplan.getEffectiveP()));
            }


            em.getTransaction().commit();


            if (myRplan == null) {
                // update the content
                content.get(user).set(columnIndex, null);
            } else {
                // update the content
                content.get(user).set(columnIndex, myRplan);
            }

            statsPerUser.get(user).update(content.get(user));
            statsPerDay.update(content, columnIndex);


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

            fireTableCellUpdated(startrow, columnIndex + ROW_HEADER);
            fireTableCellUpdated(startrow + 1, columnIndex + ROW_HEADER);
            fireTableCellUpdated(startrow + 2, columnIndex + ROW_HEADER);
            fireTableCellUpdated(startrow + 3, columnIndex + ROW_HEADER);

            fireTableCellUpdated(startrow, COL_SUM);
            fireTableCellUpdated(startrow + 1, COL_SUM);
            fireTableCellUpdated(startrow + 2, COL_SUM);
            fireTableCellUpdated(startrow + 3, COL_SUM);

            if (updateFooter != null) {
                // adapt columnindex. the keypressed event ignores the column index of the row header table.
                updateFooter.execute(columnIndex + ROW_HEADER);
            }

        }

    }

    private void initUserlist() {
        String strUserlist = OPDE.getProps().getProperty("rosterid:" + roster.getId());
        if (SYSTools.catchNull(strUserlist).isEmpty()) return;

        EntityManager em = OPDE.createEM();

        for (String token1 : StringUtils.split(strUserlist, ',')) {

            if (token1.equalsIgnoreCase("null")) {
                userlist.add(new Pair(null, null));
            } else {
                String[] strAssign = StringUtils.split(token1, '=');
                Users myUser = em.find(Users.class, strAssign[0]);
                Homes myHome = em.find(Homes.class, strAssign[1]);
                userlist.add(new Pair<Users, Homes>(myUser, myHome));

                if (!statsPerUser.containsKey(myUser)) {
                    statsPerUser.put(myUser, new StatsPerUser(WorkAccountTools.getSum(month, myUser, WorkAccountTools.HOURS), WorkAccountTools.getSick(month, myUser), WorkAccountTools.getSum(month, myUser, WorkAccountTools.HOLIDAYS), rosterParameters));
                    content.put(myUser, new ArrayList<Rplan>());
                    for (int i = 0; i < month.dayOfMonth().withMaximumValue().getDayOfMonth(); i++) {
                        content.get(myUser).add(null);
                    }
                }
            }
        }

        em.close();
    }


    public String getUserList() {
        String strUserlist = "";

        for (Pair<Users, Homes> pairUH : userlist) {
            if (pairUH.getFirst() == null) {
                strUserlist += "null,";
            } else {
                strUserlist += pairUH.getFirst().getUID() + "=" + pairUH.getSecond().getEID() + ",";
            }
        }

        if (!strUserlist.isEmpty()) {
            strUserlist = strUserlist.substring(0, strUserlist.length() - 1);
        }
        return strUserlist;
    }

    public JPopupMenu getMainContextMenuAt(final int rowIndex, final int columnIndex) {
        if (readOnly) return null;

        JPopupMenu menu = new JPopupMenu();

        JMenuItem itemInsertBlock = new JMenuItem(OPDE.lang.getString("opde.roster.insert.block"), SYSConst.icon22add);
        itemInsertBlock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userlist.add(rowIndex / 4, new Pair(null, null));
                fireTableDataChanged();
            }
        });
        menu.add(itemInsertBlock);

        JMenuItem itemRemoveBlock = new JMenuItem(OPDE.lang.getString("opde.roster.remove.block"), SYSConst.icon22delete);
        itemRemoveBlock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userlist.remove(rowIndex / 4);
                fireTableDataChanged();
            }
        });
        menu.add(itemRemoveBlock);

//        // SELECT
//        JMenuItem itemPopupShow = new JMenuItem(OPDE.lang.getString("misc.commands.show"), SYSConst.icon22magnify1);
//        itemPopupShow.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//
//            }
//        });
//        menu.add(itemPopupShow);
        return menu;
    }

    public JPopupMenu getRowHeaderContextMenuAt(final int rowIndex, final int columnIndex) {
        if (readOnly) return null;

        JPopupMenu menu = new JPopupMenu();

        OPDE.debug(rowIndex);
        OPDE.debug(columnIndex);

        //        // SELECT
        //        JMenuItem itemPopupShow = new JMenuItem(OPDE.lang.getString("misc.commands.show"), SYSConst.icon22magnify1);
        //        itemPopupShow.addActionListener(new java.awt.event.ActionListener() {
        //            public void actionPerformed(java.awt.event.ActionEvent evt) {
        //
        //            }
        //        });
        //        menu.add(itemPopupShow);
        return menu;
    }

//    public HashMap<Homes, StatsPerDay> getStatsPerDay(int columnIndex) {
//        HashMap<Homes, StatsPerDay> mapStats = new HashMap<Homes, StatsPerDay>();
//
//        for (Homes home : prefixMap.values()) {
//            mapStats.put(home, new StatsPerDay());
//        }
//
//        for (Users user : content.keySet()) {
//            Rplan rplan = content.get(user).get(columnIndex);
//            if (rplan != null) {
//                boolean exam = contracts.get(user).getParameterSet(getDay(columnIndex)).isExam();
//                Symbol symbol = rosterParameters.getSymbol(rplan.getEffectiveP());
//                int type = symbol.getSection() == RosterXML.SOCIAL ? StatsPerDay.SOCIAL : (exam ? StatsPerDay.EXAM : StatsPerDay.HELPER);
//                mapStats.get(rplan.getEffectiveHome()).add(type, rosterParameters.getSymbol(rplan.getEffectiveP()));
//            }
//
//        }
//        return mapStats;
//    }

    static ArrayList<Integer> indexOfAll(Object obj, ArrayList list) {
        ArrayList<Integer> indexList = new ArrayList<Integer>();
        for (int i = 0; i < list.size(); i++)
            if (obj.equals(list.get(i)))
                indexList.add(i);
        return indexList;
    }

    private ArrayList<Integer> indexOfAllUser(Users user) {
        ArrayList<Integer> indexList = new ArrayList<Integer>();
        int i = 0;
        for (Pair<Users, Homes> pair : userlist) {
            if (user.equals(pair.getFirst())) {
                indexList.add(i);
            }
            i++;
        }
        return indexList;
    }
}
