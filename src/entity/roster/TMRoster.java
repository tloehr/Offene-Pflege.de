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
    ArrayList<Users> userlist;
    ArrayList<Homes> homeslist;
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
    HashMap<Homes, ArrayList<HomeStats>> homestats;
    HashMap<Users, UserStats> userstats;
    private Closure updateFooter = null;


    public TMRoster(Rosters roster, boolean readOnly) {
        this.roster = roster;
        this.readOnly = readOnly;
        this.month = new LocalDate(roster.getMonth());
        defaultHome = StationTools.getStationForThisHost().getHome();

        content = new HashMap<Users, ArrayList<Rplan>>();
        contracts = UsersTools.getUsersWithValidContractsIn(month);
        homestats = new HashMap<Homes, ArrayList<HomeStats>>();
        userstats = new HashMap<Users, UserStats>();
        userlist = new ArrayList<Users>();
        homeslist = new ArrayList<Homes>();
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
        } else if (columnIndex == ROW_FOOTER) {
            if (rowIndex % 4 == 0) {
                cellType = CT_SUM_SICK;
            } else if (rowIndex % 4 == 1) {
                cellType = CT_SUM_HOLIDAY;
            } else if (rowIndex % 4 == 2) {
                cellType = CT_SUM_EMPTY;

            } else if (rowIndex % 4 == 3) {
                cellType = CT_SUM_HOURS;
            }
        }

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


            if (getDay(columnIndex).getDayOfWeek() == DateTimeConstants.SUNDAY || getDay(columnIndex).getDayOfWeek() == DateTimeConstants.SATURDAY) {
                colors = SYSConst.red1;
            }

            if (OPDE.isHoliday(getDay(columnIndex))) {
                colors = SYSConst.red2;
            }

            Users user = userlist.get(rowIndex / 4);
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

        myStyle.setBackground(colors[7]);  //+ (rowIndex % 4)

        return myStyle;
    }

    @Override
    public boolean isCellStyleOn() {
        return true;
    }

    private boolean inMainArea(int columnIndex) {
        return columnIndex >= ROW_HEADER && columnIndex < ROW_FOOTER;
    }


    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (readOnly) return false;

        boolean symbolEditable = false;
        boolean preferredHomes = columnIndex == 0 && rowIndex % 4 == 2;

        boolean setUser = columnIndex == 0 && rowIndex % 4 == 0;

        Users user = userlist.get(rowIndex / 4);

        if (user != null && inMainArea(columnIndex)) {

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
        return setUser || preferredHomes || (symbolEditable && user != null && inMainArea(columnIndex));
    }

    public Pair<Point, Point> getBaseTable() {
        return new Pair(new Point(ROW_HEADER, COL_HEADER), new Point(COL_FOOTER, ROW_FOOTER));
    }


    private void prepareContent() {

        for (Homes home : HomesTools.getAll()) {
            homestats.put(home, new ArrayList<HomeStats>());
            for (int i = 0; i < month.dayOfMonth().withMaximumValue().getDayOfMonth(); i++) {
                homestats.get(home).add(new HomeStats());
            }
        }

//        // as long as the roster is active, all users which have valid contracts can be added to it.
//        if (roster.isActive()) {
//            for (Users user : UsersTools.getUsers(false)) {
//                if (user.hasContracts()) {
//                    contracts.put(user, UsersTools.getContracts(user));
//                    content.put(user, new ArrayList<Rplan>());
//                    for (int i = 0; i < month.dayOfMonth().withMaximumValue().getDayOfMonth(); i++) {
//                        content.get(user).add(null);
//                    }
//                    // read the carry values
//                    userstats.put(user, new UserStats(WorkAccountTools.getSum(month, user, WorkAccountTools.HOURS), WorkAccountTools.getSick(month, user), WorkAccountTools.getSum(month, user, WorkAccountTools.HOLIDAYS), rosterParameters));
//                }
//            }
//        }

        for (Rplan rplan : RPlanTools.getAll(roster)) {
            Users user = rplan.getOwner();
            // later on, when the roster is not necessarily active anymore. Only those users connected to it, are visible.
//            if (!contracts.containsKey(rplan.getOwner())) {
//                contracts.put(user, UsersTools.getContracts(user));
//                userstats.put(user, new UserStats(WorkAccountTools.getSum(month, user, WorkAccountTools.HOURS), WorkAccountTools.getSick(month, user), WorkAccountTools.getSum(month, user, WorkAccountTools.HOLIDAYS), rosterParameters));
//            }

            if (!content.containsKey(user)) {
                content.put(user, new ArrayList<Rplan>());
                for (int i = 0; i < month.dayOfMonth().withMaximumValue().getDayOfMonth(); i++) {
                    content.get(user).add(null);
                }
            }

//            if (!userlist.contains(user)) {
//                rosterParameters.getUserlist().add(user);
//                rosterParameters.getPreferredHome().put(user, defaultHome);
//            }


            DateTime start = new DateTime(rplan.getStart());
            content.get(user).add(start.getDayOfMonth() - 1, rplan);

            Symbol symbol = rosterParameters.getSymbol(rplan.getEffectiveP());
            homestats.get(rplan.getHome1()).get(start.getDayOfMonth() - 1).add(contracts.get(rplan.getOwner()).getParameterSet(month).isExam(), symbol);
        }

        for (Users user : userstats.keySet()) {
            userstats.get(user).update(content.get(user));
        }

    }

//
//    @Override
//    public String getColumnName(int column) {
//        return null;
//    }

    public void cleanup() {
        content.clear();
        contracts.clear();
        homeslist.clear();
        userlist.clear();
        homestats.clear();
        userstats.clear();
    }

    @Override
    public int getRowCount() {
        return userlist.size() * 4;// + (9 * homestats.size()); // 9 lines for every home (3x exam, 3x helper, 3x social)
    }

    @Override
    public int getColumnCount() {
        return month.dayOfMonth().withMaximumValue().getDayOfMonth() + ROW_HEADER + 1; // there is 1 sum col at the end
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

        Users user = userlist.get(rowIndex / 4);
        boolean selectUser = columnIndex == 0 && rowIndex % 4 == 0;

        if (aValue instanceof Homes) {
            homeslist.set(rowIndex / 4, (Homes) aValue);
            fireTableCellUpdated(rowIndex, columnIndex);
        } else if (aValue instanceof Users) {
            Users myUser = (Users) aValue;
            userlist.set(rowIndex / 4, myUser);
            homeslist.set(rowIndex / 4, defaultHome);

            if (!userstats.containsKey(myUser)) {
                userstats.put(myUser, new UserStats(WorkAccountTools.getSum(month, myUser, WorkAccountTools.HOURS), WorkAccountTools.getSick(month, myUser), WorkAccountTools.getSum(month, myUser, WorkAccountTools.HOLIDAYS), rosterParameters));
                content.put(myUser, new ArrayList<Rplan>());
                for (int i = 0; i < month.dayOfMonth().withMaximumValue().getDayOfMonth(); i++) {
                    content.get(myUser).add(null);
                }
            }

            int startrow = rowIndex - (rowIndex % 4);
            fireTableRowsUpdated(startrow, startrow + 3);
        } else if (selectUser) {
            OPDE.debug(aValue);
        } else {
            String newSymbol = aValue.toString();
//            Symbol prevSymbol = rosterParameters.getSymbol(getValueAt(rowIndex, columnIndex).toString());
            Symbol symbol = rosterParameters.getSymbol(newSymbol);

            if (!newSymbol.isEmpty() && symbol == null) return; // entered UNKNOWN symbol.
            if (symbol != null && !rosterParameters.getSymbol(newSymbol).isAllowed(getDay(columnIndex)))
                return; // the symbol is valid but not on THAT particular day
            if (newSymbol.isEmpty()) { // he wants to remove the symbol
                emptyCell(rowIndex, columnIndex);
                return;
            }

            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();

                Homes preferredHome = em.merge(homeslist.get(rowIndex / 4));

                Rplan oldPlan = content.get(user).get(columnIndex - ROW_HEADER);
                if (oldPlan == null) {
                    oldPlan = new Rplan(roster, preferredHome, getDay(columnIndex).toDate(), em.merge(user));
                }

                Rplan myRplan = em.merge(oldPlan);
                em.lock(myRplan, LockModeType.OPTIMISTIC);

                HomeStats homeStat = homestats.get(myRplan.getHome1()).get(new LocalDate(myRplan.getStart()).getDayOfMonth() - 1);
                boolean exam = contracts.get(myRplan.getOwner()).getParameterSet(month).isExam();

                if (rowIndex % 4 == 0) {
                    myRplan.setP1(newSymbol);
                    myRplan.setHome1(preferredHome);
                } else if (rowIndex % 4 == 1) {
                    myRplan.setP2(newSymbol);
                    myRplan.setHome2(preferredHome);
                } else if (rowIndex % 4 == 2) {
                    myRplan.setP3(newSymbol);
                    myRplan.setHome3(preferredHome);
                }

                myRplan.setValuesFromSymbol(symbol, contracts.get(user).getParameterSet(getDay(columnIndex)));

                if (oldPlan.getId() == 0) {
                    homeStat.add(exam, rosterParameters.getSymbol(myRplan.getEffectiveP()));
                } else {
                    homeStat.replace(exam, rosterParameters.getSymbol(oldPlan.getEffectiveP()), rosterParameters.getSymbol(myRplan.getEffectiveP()));
                }

                em.getTransaction().commit();

                // update the content and stats
                content.get(user).set(columnIndex - ROW_HEADER, myRplan);
                userstats.get(user).update(content.get(user));

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

                fireTableCellUpdated(startrow, getColumnCount() - 1);
                fireTableCellUpdated(startrow + 1, getColumnCount() - 1);
                fireTableCellUpdated(startrow + 2, getColumnCount() - 1);
                fireTableCellUpdated(startrow + 3, getColumnCount() - 1);

                if (updateFooter != null) {
                    updateFooter.execute(columnIndex);
                }

            }
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
//        OPDE.debug(String.format("rowindex: %d   columnindex: %d", rowIndex, columnIndex));
        Object value = "--";

        Users user = userlist.get(rowIndex / 4);

        if (user == null) {
            return "";
        }

        if (columnIndex == 0) {  // Usernames
            if (rowIndex % 4 == 0) {
                value = user;
            } else if (rowIndex % 4 == 1) {
                value = user.getVorname();
            } else if (rowIndex % 4 == 2) {
                return homeslist.get(rowIndex / 4);
            } else {

                if (contracts.get(user).getParameterSet(month).isTrainee()) {
                    value = "Schüler";
                } else {
                    value = contracts.get(user).getParameterSet(month).isExam() ? "{Examen:bold}" : "Helfer";
                }
            }
        } else if (columnIndex == 1) { // homestats carry
            if (rowIndex % 4 == 3) {
                value = "St: " + userstats.get(user).getHoursCarry().setScale(2, RoundingMode.HALF_UP).toString();
            } else if (rowIndex % 4 == 0) {
                value = "Kr: " + userstats.get(user).getSickCarry().setScale(2, RoundingMode.HALF_UP).toString();
            } else if (rowIndex % 4 == 1) {
                value = "Url: " + userstats.get(user).getHolidayCarry().setScale(2, RoundingMode.HALF_UP).toString();
            }


        } else if (columnIndex >= ROW_HEADER && columnIndex < ROW_FOOTER) {
//            if (columnIndex == 4 && rowIndex == 12){
//                OPDE.debug("buh!");
//            }

            if (content.get(user).get(columnIndex - ROW_HEADER) != null) {

                if (rowIndex % 4 == 0) {
                    value = content.get(user).get(columnIndex - ROW_HEADER).getP1();
                } else if (rowIndex % 4 == 1) {
                    value = content.get(user).get(columnIndex - ROW_HEADER).getP2();
                } else if (rowIndex % 4 == 2) {
                    value = content.get(user).get(columnIndex - ROW_HEADER).getP3();
                    if (!value.toString().isEmpty()) {
                        OPDE.debug(value);
                    }
                } else {

                    value = content.get(user).get(columnIndex - ROW_HEADER).getNetValue().setScale(2, RoundingMode.HALF_UP).toString();

//                if (breaktime.compareTo(BigDecimal.ZERO) > 0) {
//                    value += " -" + breaktime.setScale(2, RoundingMode.HALF_UP).toString();
//                }
//
//                if (extrahours.compareTo(BigDecimal.ZERO) > 0) {
//                    value += " +" + extrahours.setScale(2, RoundingMode.HALF_UP).toString();
//                }

                }
            }
        } else if (columnIndex >= ROW_FOOTER) {
            if (rowIndex % 4 == 3) {
                value = "St: " + userstats.get(user).getHoursSum().setScale(2, RoundingMode.HALF_UP).toString();
            } else if (rowIndex % 4 == 0) {
                value = "Kr: " + userstats.get(user).getSickSum().setScale(2, RoundingMode.HALF_UP).toString();
            } else if (rowIndex % 4 == 1) {
                value = "Url: " + userstats.get(user).getHolidaySum().setScale(2, RoundingMode.HALF_UP).toString();
            }
        }

        return value;
    }

    public RosterParameters getRosterParameters() {
        return rosterParameters;
    }


    public HashMap<Homes, ArrayList<HomeStats>> getHomestats() {
        return homestats;
    }

    public void setFooterUpdateListener(Closure updateFooter) {
        this.updateFooter = updateFooter;
    }

    /**
     * clears the selected cell, if possible
     */
    public void emptyCell(int rowIndex, int columnIndex) {
//        columnIndex = columnIndex;
        //        rowIndex = rowIndex + COL_HEADER;

//        OPDE.debug(String.format("rowindex: %d   columnindex: %d", rowIndex, columnIndex));
        Users user = userlist.get(rowIndex / 4);

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
        if (rowIndex % 4 == 2 && oldPlan.getP3().isEmpty()) {
            return;
        }
        if (rowIndex % 4 == 3) {
            return;
        }

        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            Rosters myRoster = em.merge(roster);
            Rplan myRplan = em.merge(oldPlan);

            HomeStats stat = homestats.get(myRplan.getHome1()).get(new LocalDate(myRplan.getStart()).getDayOfMonth() - 1);
            em.lock(myRplan, LockModeType.OPTIMISTIC);
            boolean exam = contracts.get(myRplan.getOwner()).getParameterSet(month).isExam();

            if (rowIndex % 4 == 0) {
                em.remove(myRplan);
                stat.subtract(exam, rosterParameters.getSymbol(myRplan.getP1()));

                myRplan = null;
            } else {

                Symbol symbol = null;
                if (rowIndex % 4 == 1) {
                    myRplan.setP2(null);
                    myRplan.setHome2(null);
                    symbol = rosterParameters.getSymbol(myRplan.getP1());
                } else if (rowIndex % 4 == 2) {
                    myRplan.setP3(null);
                    myRplan.setHome3(null);
                    symbol = rosterParameters.getSymbol(myRplan.getP2());
                }

                myRplan.setValuesFromSymbol(symbol, contracts.get(user).getParameterSet(getDay(columnIndex)));
                stat.replace(exam, rosterParameters.getSymbol(oldPlan.getEffectiveP()), rosterParameters.getSymbol(myRplan.getEffectiveP()));
            }


            em.getTransaction().commit();


            if (myRplan == null) {
                // update the content
                content.get(user).set(columnIndex, null);
            } else {
                // update the content
                content.get(user).set(columnIndex, myRplan);
            }

            userstats.get(user).update(content.get(user));

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

            fireTableCellUpdated(startrow, getColumnCount() - 1);
            fireTableCellUpdated(startrow + 1, getColumnCount() - 1);
            fireTableCellUpdated(startrow + 2, getColumnCount() - 1);
            fireTableCellUpdated(startrow + 3, getColumnCount() - 1);

            if (updateFooter != null) {
                updateFooter.execute(columnIndex);
            }

        }

    }

    private void initUserlist() {
        String strUserlist = OPDE.getProps().getProperty("rosterid:" + roster.getId());
        if (SYSTools.catchNull(strUserlist).isEmpty()) return;

        EntityManager em = OPDE.createEM();

        for (String token1 : StringUtils.split(strUserlist, ',')) {

            if (token1.equalsIgnoreCase("null")) {
                userlist.add(null);
                homeslist.add(null);
            } else {
                String[] strAssign = StringUtils.split(token1, '=');
                Users myUser = em.find(Users.class, strAssign[0]);
                Homes myHome = em.find(Homes.class, strAssign[1]);
                userlist.add(myUser);
                homeslist.add(myHome);
                if (!userstats.containsKey(myUser)) {
                    userstats.put(myUser, new UserStats(WorkAccountTools.getSum(month, myUser, WorkAccountTools.HOURS), WorkAccountTools.getSick(month, myUser), WorkAccountTools.getSum(month, myUser, WorkAccountTools.HOLIDAYS), rosterParameters));
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

        int index = 0;
        for (Users user : userlist) {
            if (user == null) {
                strUserlist += "null,";
            } else {
                strUserlist += user.getUID() + "=" + homeslist.get(index).getEID() + ",";
            }

            index++;
        }

        if (!strUserlist.isEmpty()) {
            strUserlist = strUserlist.substring(0, strUserlist.length() - 1);
        }
        return strUserlist;
    }
}
