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
import org.apache.commons.collections.Closure;
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
import java.util.Arrays;
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
    HashMap<Homes, ArrayList<DailyStats>> homestats;
    HashMap<Users, ArrayList<BigDecimal>> userstats;
    private Closure updateFooter = null;


    public TMRoster(Rosters roster, boolean readOnly) {
        defaultHome = StationTools.getStationForThisHost().getHome();

        this.roster = roster;
        this.readOnly = readOnly;
        this.month = new LocalDate(roster.getMonth());

        content = new HashMap<Users, ArrayList<Rplan>>();
        contracts = new HashMap<Users, UserContracts>();
        homestats = new HashMap<Homes, ArrayList<DailyStats>>();
        userstats = new HashMap<Users, ArrayList<BigDecimal>>();

        rosterParameters = RostersTools.getParameters(roster);
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


            if (getDay(columnIndex).getDayOfWeek() == DateTimeConstants.SUNDAY || getDay(columnIndex).getDayOfWeek() == DateTimeConstants.SATURDAY) {
                colors = SYSConst.red1;
            }

            if (OPDE.isHoliday(getDay(columnIndex))) {
                colors = SYSConst.red2;
            }

            if (rowIndex % 4 != 3) {
                Rplan myRplan = content.get(user).get(columnIndex - ROW_HEADER);

                if (rowIndex % 4 == 0) {
                    myStyle.setForeground(myRplan == null || myRplan.getP1().isEmpty() ? Color.black : myRplan.getHome1().getColor());
                } else if (rowIndex % 4 == 1) {
                    myStyle.setForeground(myRplan == null || myRplan.getP2().isEmpty() ? Color.black : myRplan.getHome2().getColor());
                } else if (rowIndex % 4 == 2) {
                    myStyle.setForeground(myRplan == null || myRplan.getP3().isEmpty() ? Color.black : myRplan.getHome3().getColor());
                }

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

    private boolean inMainArea(int columnIndex) {
        return columnIndex >= ROW_HEADER && columnIndex < ROW_FOOTER;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {

        if (readOnly) return false;

        boolean symbolEditable = false;
        boolean preferredHomes = columnIndex == 0 && rowIndex % 4 == 2;

        if (inMainArea(columnIndex)) {
            Rplan rplan = content.get(rosterParameters.getUserlist().get(rowIndex / 4)).get(columnIndex - ROW_HEADER);

            if (rplan == null) {
                symbolEditable = rowIndex % 4 == 0; // empty plans must be started at the first row
            } else {
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
        }

        return preferredHomes || (inMainArea(columnIndex) && symbolEditable);
    }


    public Pair<Point, Point> getBaseTable() {
        return new Pair(new Point(ROW_HEADER, COL_HEADER), new Point(COL_FOOTER, ROW_FOOTER));
    }

    private void prepareContent() {

        for (Homes home : HomesTools.getAll()) {
            homestats.put(home, new ArrayList<DailyStats>());
            for (int i = 0; i < month.dayOfMonth().withMaximumValue().getDayOfMonth(); i++) {
                homestats.get(home).add(new DailyStats());
            }
        }

        // as long as the roster is active, all users which have valid contracts can be added to it.
        if (roster.isActive()) {
            for (Users user : UsersTools.getUsers(false)) {
                if (user.hasContracts()) {
                    contracts.put(user, UsersTools.getContracts(user));
                    content.put(user, new ArrayList<Rplan>());
                    for (int i = 0; i < month.dayOfMonth().withMaximumValue().getDayOfMonth(); i++) {
                        content.get(user).add(null);
                    }
                    userstats.put(user, new ArrayList<BigDecimal>(Arrays.asList(new BigDecimal[]{WorkAccountTools.getSum(month, user, WorkAccountTools.HOURS), WorkAccountTools.getSick(month, user), WorkAccountTools.getSum(month, user, WorkAccountTools.HOLIDAYS), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO})));
                }
            }
        }

        for (Rplan rplan : RPlanTools.getAll(roster)) {
            // later on, when the roster is not necessarily active anymore. Only those users connected to it, are visible.
            if (!contracts.containsKey(rplan.getOwner())) {
                contracts.put(rplan.getOwner(), UsersTools.getContracts(rplan.getOwner()));
                userstats.put(rplan.getOwner(), new ArrayList<BigDecimal>(Arrays.asList(new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO})));
            }

            if (!content.containsKey(rplan.getOwner())) {
                content.put(rplan.getOwner(), new ArrayList<Rplan>());
                for (int i = 0; i < month.dayOfMonth().withMaximumValue().getDayOfMonth(); i++) {
                    content.get(rplan.getOwner()).add(null);
                }
            }

            if (!rosterParameters.getUserlist().contains(rplan.getOwner())) {
                rosterParameters.getUserlist().add(rplan.getOwner());
                rosterParameters.getPreferredHome().put(rplan.getOwner(), defaultHome);
            }


            DateTime start = new DateTime(rplan.getStart());
            content.get(rplan.getOwner()).add(start.getDayOfMonth() - 1, rplan);

            Symbol symbol = rosterParameters.getSymbol(rplan.getEffectiveP());
            homestats.get(rplan.getHome1()).get(start.getDayOfMonth() - 1).add(contracts.get(rplan.getOwner()).getParameterSet(month).isExam(), symbol);

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
        return rosterParameters.getUserlist().size() * 4;// + (9 * homestats.size()); // 9 lines for every home (3x exam, 3x helper, 3x social)
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

        Users user = rosterParameters.getUserlist().get(rowIndex / 4);
        boolean selectUser = columnIndex == 0 && rowIndex % 4 == 0;

        if (aValue instanceof Homes) {
            rosterParameters.getPreferredHome().put(user, (Homes) aValue);
            fireTableCellUpdated(rowIndex, columnIndex);
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

                Homes preferredHome = em.merge(rosterParameters.getPreferredHome().containsKey(user) ? rosterParameters.getPreferredHome().get(user) : defaultHome);

                Rplan oldPlan = content.get(user).get(columnIndex - ROW_HEADER);
                if (oldPlan == null) {
                    oldPlan = new Rplan(roster, preferredHome, getDay(columnIndex).toDate(), em.merge(user));
                }

                Rplan myRplan = em.merge(oldPlan);
                em.lock(myRplan, LockModeType.OPTIMISTIC);

                DailyStats stat = homestats.get(myRplan.getHome1()).get(new LocalDate(myRplan.getStart()).getDayOfMonth() - 1);
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
                    stat.add(exam, rosterParameters.getSymbol(myRplan.getEffectiveP()));
                } else {
                    stat.replace(exam, rosterParameters.getSymbol(oldPlan.getEffectiveP()), rosterParameters.getSymbol(myRplan.getEffectiveP()));
                }

                em.getTransaction().commit();

                // update the content
                content.get(user).set(columnIndex - ROW_HEADER, myRplan);

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
        Users user = rosterParameters.getUserlist().get(rowIndex / 4);
        if (columnIndex == 0) {  // Usernames
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
        } else if (columnIndex == 1) { // homestats carry
            if (rowIndex % 4 == 0) {
                BigDecimal hoursCarry = userstats.get(user).get(0);
                value = "Stunden: " + hoursCarry.setScale(2, RoundingMode.HALF_UP).toString();
            } else if (rowIndex % 4 == 1) {
                BigDecimal sickdays = userstats.get(user).get(1);
                value = "Krankheitstage: " + sickdays.setScale(2, RoundingMode.HALF_UP).toString();
            } else if (rowIndex % 4 == 2) {
                BigDecimal holidays = userstats.get(user).get(2);
                value = "Urlaubstage: " + holidays.setScale(2, RoundingMode.HALF_UP).toString();
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
                    BigDecimal basehours = content.get(user).get(columnIndex - ROW_HEADER).getBasehours();
                    BigDecimal breaktime = content.get(user).get(columnIndex - ROW_HEADER).getBreaktime();
                    BigDecimal extrahours = content.get(user).get(columnIndex - ROW_HEADER).getExtrahours();

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
        }

        return value;
    }

    public RosterParameters getRosterParameters() {
        return rosterParameters;
    }


    public HashMap<Homes, ArrayList<DailyStats>> getHomestats() {
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
        Users user = rosterParameters.getUserlist().get(rowIndex / 4);

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

            DailyStats stat = homestats.get(myRplan.getHome1()).get(new LocalDate(myRplan.getStart()).getDayOfMonth() - 1);

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

            if (updateFooter != null) {
                updateFooter.execute(columnIndex);
            }


        }


    }
}
