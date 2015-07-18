/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.reports;

import entity.building.Homes;
import entity.files.SYSFilesTools;
import entity.info.ResInfoTools;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.nursingprocess.DFNTools;
import entity.prescription.*;
import op.OPDE;
import op.care.med.inventory.PnlInventory;
import op.care.supervisor.PnlHandover;
import op.threads.DisplayMessage;
import op.tools.SYSCalendar;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author tloehr
 */
public class HandoversTools {

    public static String getDateAndUser(Handovers bericht, boolean showIDs) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");
        result = sdf.format(bericht.getPit()) + "; " + bericht.getUser().getFullname();
        if (showIDs) {
            result += "<br/><i>(" + bericht.getUebid() + ")</i>";
        }
        return "<font " + getHTMLColor(bericht) + ">" + result + "</font>";
    }

    private static String getHTMLColor(Handovers bericht) {
        return OPDE.getProps().getProperty(SYSCalendar.SHIFT_KEY_TEXT[SYSCalendar.whatShiftIs(bericht.getPit())] + "_FGBHP");
    }


    /**
     * retrieves all NReports for a certain day which have been assigned with the Tags Nr. 1 (Handover) and Nr. 2 (Emergency)
     *
     * @param day
     * @return
     */
    public static ArrayList<Handovers> getBy(LocalDate day, Homes home) {

        return getBy(day.toDateTimeAtStartOfDay(), SYSCalendar.eod(day), home);
    }

    public static ArrayList<Handovers> getBy(int year, Homes home) {
        DateTime dtYear = new DateTime(year, 1, 1, 0, 0);
        DateTime from = dtYear.dayOfYear().withMinimumValue().secondOfDay().withMinimumValue();
        DateTime to = dtYear.dayOfYear().withMaximumValue().secondOfDay().withMaximumValue();
        return getBy(from, to, home);
    }

    public static ArrayList<Handovers> getBy(DateTime from, DateTime to, Homes home) {
        EntityManager em = OPDE.createEM();
        ArrayList<Handovers> list = null;

        try {

            String jpql = " SELECT ho " +
                    " FROM Handovers ho " +
                    " WHERE " +
                    " ho.pit >= :from AND ho.pit <= :to " +
                    " AND ho.home = :home " +
                    " ORDER BY ho.pit DESC ";

            Query query = em.createQuery(jpql);

            query.setParameter("from", from.toDate());
            query.setParameter("to", to.toDate());
            query.setParameter("home", home);

            list = new ArrayList<Handovers>(query.getResultList());

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return list;
    }

    public static String getAsHTML(Handovers handover) {
        String result = "<div id=\"fonttext\">";

        result += getDateAndUser(handover);

//        DateFormat df = DateFormat.getDateTimeInstance();

        String tmp = SYSTools.replace(handover.getText(), "\n", "<br/>", false);

        result += "<p>" + tmp + "<p/>";

        result += "<div/>";
        return result;
    }

    public static String getDateAndUser(Handovers handover) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");
        result = sdf.format(handover.getPit()) + "; " + handover.getUser().getFullname();
        return result;
    }

    /**
     * gibt eine HTML Darstellung des Einrichtungsnamen zurück.
     *
     * @return
     */
    public static String getHomeAsHTML(Handovers bericht) {
        String result = "";

        String fonthead = "<font " + getHTMLColor(bericht) + ">";
        result += bericht.getHome().getName();
        result = fonthead + result + "</font>";
        return result;
    }

    public static void printSupervision(final LocalDate day, final Homes home, final Closure afterAction) {
        SwingWorker worker = new SwingWorker() {
            HashMap<Resident, Date> mapAbsentSince = new HashMap<Resident, Date>();
            ArrayList<Resident> listAllActiveResidents;
            HashMap<Resident, HashMap<Byte, Long>> bhpStats;
            ArrayList<BHP> listOpenBHPs;
            String html = "<h1 id=\"fonth1\" >" + SYSTools.xx("nursingrecords.handover.supervisorsReport") + ": " + DateFormat.getDateInstance().format(day.toDate()) + "</h1>";

            @Override
            protected Object doInBackground() throws Exception {

                html += "<div id=\"fonttext\">";

                int progress = 1;


                OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), -1, 100));

                listAllActiveResidents = ResidentTools.getAllActive(home);
                bhpStats = new HashMap<Resident, HashMap<Byte, Long>>();
                listOpenBHPs = BHPTools.getOpenBHPs(day, home);

                int max = listAllActiveResidents.size() * 2 + listOpenBHPs.size();

                /***
                 *      _____                 _           _   _ ____                       _
                 *     | ____|_ __ ___  _ __ | |_ _   _  | \ | |  _ \ ___ _ __   ___  _ __| |_ ___
                 *     |  _| | '_ ` _ \| '_ \| __| | | | |  \| | |_) / _ \ '_ \ / _ \| '__| __/ __|
                 *     | |___| | | | | | |_) | |_| |_| | | |\  |  _ <  __/ |_) | (_) | |  | |_\__ \
                 *     |_____|_| |_| |_| .__/ \__|\__, | |_| \_|_| \_\___| .__/ \___/|_|   \__|___/
                 *                     |_|        |___/                  |_|
                 */
                html += "<h2 id=\"fonth2\" >" + SYSTools.xx("nursingrecords.handover.residentsWONReports") + "</h2>";

                String htmlul1 = "";
                for (Resident resident : listAllActiveResidents) {
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
                    progress++;
                    Date absentSince = ResInfoTools.absentSince(resident);
                    if (absentSince != null) {
                        mapAbsentSince.put(resident, absentSince);
                    }


                    long num = NReportTools.getNum(resident, day);
                    if (num == 0) {
                        htmlul1 += "<li>" + ResidentTools.getTextCompact(resident);
                        if (mapAbsentSince.containsKey(resident)) {
                            htmlul1 += SYSTools.xx("misc.msg.ResidentAbsentSince") + ": " + DateFormat.getDateInstance().format(mapAbsentSince.get(resident));
                        }
                        htmlul1 += "</li>";
                    }
                }
                if (htmlul1.isEmpty()) {
                    html += "<h3 id=\"fonth3\">" + SYSTools.xx("misc.msg.noEntries") + "...</h3>";
                } else {
                    html += "<ul>" + htmlul1 + "</ul>";
                }

                /***
                 *       ___                     ____  _   _ ____
                 *      / _ \ _ __   ___ _ __   | __ )| | | |  _ \ ___
                 *     | | | | '_ \ / _ \ '_ \  |  _ \| |_| | |_) / __|
                 *     | |_| | |_) |  __/ | | | | |_) |  _  |  __/\__ \
                 *      \___/| .__/ \___|_| |_| |____/|_| |_|_|   |___/
                 *           |_|
                 */
                OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), 2, 4));
                html += "<h2 id=\"fonth2\" >" + SYSTools.xx("nursingrecords.handover.openBHPs") + "</h2>";

                Collections.sort(listOpenBHPs, new Comparator<BHP>() {
                    @Override
                    public int compare(BHP o1, BHP o2) {
                        int result = o1.getResident().compareTo(o2.getResident());
                        if (result == 0) {
                            result = o1.getShift().compareTo(o2.getShift());
                        }
                        return result;
                    }
                });

                // resident -> shift -> num of empty BHPs
                for (BHP bhp : listOpenBHPs) {
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
                    progress++;
                    if (!bhpStats.containsKey(bhp.getResident())) {
                        bhpStats.put(bhp.getResident(), new HashMap<Byte, Long>());
                        bhpStats.get(bhp.getResident()).put(SYSCalendar.SHIFT_VERY_EARLY, 0l);
                        bhpStats.get(bhp.getResident()).put(SYSCalendar.SHIFT_EARLY, 0l);
                        bhpStats.get(bhp.getResident()).put(SYSCalendar.SHIFT_LATE, 0l);
                        bhpStats.get(bhp.getResident()).put(SYSCalendar.SHIFT_VERY_LATE, 0l);
                    }
                    long l = bhpStats.get(bhp.getResident()).get(bhp.getShift());
                    bhpStats.get(bhp.getResident()).put(bhp.getShift(), l + 1);
                }

                if (bhpStats.isEmpty()) {
                    html += "<h3 id=\"fonth3\">" + SYSTools.xx("misc.msg.noEntries") + "...</h3>";
                } else {
                    for (Resident resident : bhpStats.keySet()) {
                        html += "<h3 id=\"fonth3\">" + ResidentTools.getTextCompact(resident);
                        if (mapAbsentSince.containsKey(resident)) {
                            htmlul1 += SYSTools.xx("misc.msg.ResidentAbsentSince") + ": " + DateFormat.getDateInstance().format(mapAbsentSince.get(resident));
                        }
                        html += "</h3>";

                        html += "<ul>";
                        int i = 0;
                        for (Byte shift : SYSCalendar.SHIFTS) {
                            if (bhpStats.get(resident).get(shift) > 0l) {
                                html += "<li>" + SYSTools.xx(SYSCalendar.SHIFT_TEXT[i]) + ": " + bhpStats.get(resident).get(shift) + "</li>";
                            }
                            i++;
                        }
                        html += "</ul>";
                    }
                }

                /***
                 *      _____                 _           __  __          _ ___                _             _
                 *     | ____|_ __ ___  _ __ | |_ _   _  |  \/  | ___  __| |_ _|_ ____   _____| |_ ___  _ __(_) ___  ___
                 *     |  _| | '_ ` _ \| '_ \| __| | | | | |\/| |/ _ \/ _` || || '_ \ \ / / _ \ __/ _ \| '__| |/ _ \/ __|
                 *     | |___| | | | | | |_) | |_| |_| | | |  | |  __/ (_| || || | | \ V /  __/ || (_) | |  | |  __/\__ \
                 *     |_____|_| |_| |_| .__/ \__|\__, | |_|  |_|\___|\__,_|___|_| |_|\_/ \___|\__\___/|_|  |_|\___||___/
                 *                     |_|        |___/
                 */
                OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), 3, 4));
                html += "<h2 id=\"fonth2\" >" + SYSTools.xx("nursingrecords.handover.emptyInventories") + "</h2>";
                String htmlul3 = "";
                for (Resident resident : listAllActiveResidents) {
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
                    progress++;
                    String htmlul2 = "";
                    ArrayList<MedInventory> listInventories = MedInventoryTools.getAllActive(resident);
                    for (MedInventory inventory : listInventories) {
                        MedStock stock = MedInventoryTools.getCurrentOpened(inventory);
                        BigDecimal stockSum = null;
                        if (stock != null) {
                            stockSum = MedStockTools.getSum(stock);
                        }

                        if (stock == null || stockSum.compareTo(BigDecimal.ZERO) <= 0) {
                            htmlul2 += "<li>" + inventory.getText() + ": <b>";
                            htmlul2 += (stock == null ?
                                    SYSTools.xx("nursingrecords.inventory.noOpenStock") :
                                    "[" + stock.getID() + "] " + SYSTools.xx("nursingrecords.inventory.StockSum") + " " + stockSum.setScale(2, BigDecimal.ROUND_HALF_UP) + " " + TradeFormTools.getPackUnit(stock.getTradeForm()));
                            htmlul2 += "</b></li>";
                        }
                    }
                    if (!htmlul2.isEmpty()) {
                        htmlul3 += "<li>" + ResidentTools.getTextCompact(resident) + "<ul>" + htmlul2 + "</ul></li>";
                    }
                }

                if (htmlul3.isEmpty()) {
                    html += "<h3 id=\"fonth3\">" + SYSTools.xx("misc.msg.noEntries") + "...</h3>";
                } else {
                    html += "<ul>" + htmlul3 + "</ul>";
                }

                html += "</div>";
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            protected void done() {
                OPDE.getDisplayManager().setProgressBarMessage(null);
                mapAbsentSince.clear();
                bhpStats.clear();
                SYSFilesTools.print(html, false);
                if (afterAction != null) {
                    afterAction.execute(null);
                }
            }
        };

        worker.execute();

    }
}
