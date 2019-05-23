package entity.info;

import entity.EntityTools;
import entity.building.HomesTools;
import entity.building.Rooms;
import entity.building.Station;
import entity.prescription.GPTools;
import entity.prescription.Prescription;
import entity.prescription.PrescriptionTools;
import entity.process.QProcessElement;
import entity.reports.NReportTools;
import entity.system.Commontags;
import entity.system.CommontagsTools;
import entity.values.ResValue;
import entity.values.ResValueTools;
import entity.values.ResValueTypesTools;
import op.OPDE;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 24.10.11
 * Time: 16:10
 * To change this template use File | Settings | File Templates.
 */
public class ResInfoTools {


    public static ResInfo getLastResinfo(Resident bewohner, ResInfoType bwinfotyp) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfo b WHERE b.resident = :bewohner AND b.bwinfotyp = :bwinfotyp ORDER BY b.from DESC");
        query.setParameter("bewohner", bewohner);
        query.setParameter("bwinfotyp", bwinfotyp);
        query.setFirstResult(0);
        query.setMaxResults(1);
        List<ResInfo> bwinfos = query.getResultList();
        em.close();
        return bwinfos.isEmpty() ? null : bwinfos.get(0);
    }


    public static List<ResInfo> getSpecialInfos() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfo b WHERE b.resident.station IS NOT NULL AND b.bwinfotyp.type IN (:absence, :infection, :warning, :diabetes, :allergy, :fallrisk) AND b.to > :now ");
        query.setParameter("absence", ResInfoTypeTools.TYPE_ABSENCE);
        query.setParameter("infection", ResInfoTypeTools.TYPE_INFECTION);
        query.setParameter("warning", ResInfoTypeTools.TYPE_WARNING);
        query.setParameter("allergy", ResInfoTypeTools.TYPE_ALLERGY);
        query.setParameter("diabetes", ResInfoTypeTools.TYPE_DIABETES);
        query.setParameter("fallrisk", ResInfoTypeTools.TYPE_FALLRISK);
        query.setParameter("now", new Date());
        List<ResInfo> resinfos = query.getResultList();
        em.close();
        return resinfos;
    }

    public static ResInfo getLastResinfo(Resident bewohner, int type) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfo b WHERE b.resident = :bewohner AND b.bwinfotyp.type = :type ORDER BY b.from DESC");
        query.setParameter("bewohner", bewohner);
        query.setParameter("type", type);
        query.setFirstResult(0);
        query.setMaxResults(1);
        List<ResInfo> bwinfos = query.getResultList();
        em.close();
        return bwinfos.isEmpty() ? null : bwinfos.get(0);
    }

    public static ArrayList<ResInfoCategory> getCategories(ArrayList<ResInfo> listInfos) {
        HashSet<ResInfoCategory> cat = new HashSet<ResInfoCategory>();
        for (ResInfo resInfo : listInfos) {
            cat.add(resInfo.getResInfoType().getResInfoCat());
        }
        ArrayList<ResInfoCategory> list = new ArrayList<ResInfoCategory>(cat);
        Collections.sort(list);
        return list;
    }


    /**
     * @param resident
     * @param resInfoType
     * @return
     * @see <a href="https://github.com/tloehr/Offene-Pflege.de/issues/10">GitHub #10</a>
     * ORDER type was "DESC" should be "ASC".
     */
    public static ResInfo getFirstResinfo(Resident resident, ResInfoType resInfoType) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfo b WHERE b.resident = :resident AND b.bwinfotyp = :resInfoType ORDER BY b.from ASC");
        query.setParameter("resident", resident);
        query.setParameter("resInfoType", resInfoType);
        query.setFirstResult(0);
        query.setMaxResults(1);
        List<ResInfo> bwinfos = query.getResultList();
        em.close();
        return bwinfos.isEmpty() ? null : bwinfos.get(0);
    }


    public static ArrayList<ResInfo> getAll(Resident resident, ResInfoType type, LocalDate start, LocalDate end) {
        DateTime from = start.toDateTimeAtStartOfDay();
        DateTime to = SYSCalendar.eod(end);

        return getAll(resident, type, from, to);
    }


    public static ArrayList<ResInfo> getAll(Resident resident, ResInfoType type, DateTime from, DateTime to) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery(
                " SELECT rinfo FROM ResInfo rinfo " +
                        " WHERE rinfo.resident = :bewohner " +
                        " AND rinfo.bwinfotyp = :bwinfotyp " +
                        " AND ((rinfo.from <= :from AND rinfo.to >= :from) OR " +
                        " (rinfo.from <= :to AND rinfo.to >= :to) OR " +
                        " (rinfo.from > :from AND rinfo.to < :to)) " +
                        " ORDER BY rinfo.from DESC"
        );
        query.setParameter("bewohner", resident);
        query.setParameter("bwinfotyp", type);
        query.setParameter("from", from.toDate());
        query.setParameter("to", to.toDate());
        ArrayList<ResInfo> resInfos = new ArrayList<ResInfo>(query.getResultList());
        em.close();
        return resInfos;
    }


    public static ArrayList<ResInfo> getAll(ResInfoType type, DateTime pit) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery(
                " SELECT rinfo FROM ResInfo rinfo " +
                        " WHERE rinfo.bwinfotyp = :bwinfotyp " +
                        " AND (rinfo.from <= :pit AND rinfo.to >= :pit) "
        );

        query.setParameter("bwinfotyp", type);
        query.setParameter("pit", pit.toDate());
        ArrayList<ResInfo> resInfos = new ArrayList<ResInfo>(query.getResultList());
        em.close();
        return resInfos;
    }


    public static ArrayList<ResInfo> getAll(Resident resident, ResInfoCategory cat, LocalDate start, LocalDate end) {
        DateTime from = start.toDateTimeAtStartOfDay();
        DateTime to = SYSCalendar.eod(end);
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery(
                " SELECT rinfo FROM ResInfo rinfo " +
                        " WHERE rinfo.resident = :bewohner AND rinfo.bwinfotyp.resInfoCat = :cat " +
                        " AND ((rinfo.from <= :from AND rinfo.to >= :from) OR " +
                        " (rinfo.from <= :to AND rinfo.to >= :to) OR " +
                        " (rinfo.from > :from AND rinfo.to < :to)) " +
                        " ORDER BY rinfo.bwinfotyp.bWInfoKurz, rinfo.from DESC"
        );
        query.setParameter("bewohner", resident);
        query.setParameter("cat", cat);
        query.setParameter("from", from.toDate());
        query.setParameter("to", to.toDate());
        ArrayList<ResInfo> resInfos = new ArrayList<ResInfo>(query.getResultList());
        em.close();
        return resInfos;
    }

    public static ArrayList<ResInfo> getAll(Resident resident, LocalDate start, LocalDate end) {
        DateTime from = start.toDateTimeAtStartOfDay();
        DateTime to = SYSCalendar.eod(end);
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery(
                " SELECT rinfo FROM ResInfo rinfo " +
                        " WHERE rinfo.resident = :bewohner " +
                        " AND ((rinfo.from <= :from AND rinfo.to >= :from) OR " +
                        " (rinfo.from <= :to AND rinfo.to >= :to) OR " +
                        " (rinfo.from > :from AND rinfo.to < :to)) " +
                        " ORDER BY rinfo.bwinfotyp.bWInfoKurz, rinfo.from DESC"
        );
        query.setParameter("bewohner", resident);
        query.setParameter("from", from.toDate());
        query.setParameter("to", to.toDate());
        ArrayList<ResInfo> resInfos = new ArrayList<ResInfo>(query.getResultList());
        em.close();
        return resInfos;
    }

    public static ArrayList<ResInfo> getAll(Resident resident, ResInfoType type) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfo b WHERE b.resident = :bewohner AND b.bwinfotyp = :bwinfotyp ORDER BY b.from DESC");
        query.setParameter("bewohner", resident);
        query.setParameter("bwinfotyp", type);
        ArrayList<ResInfo> resInfos = new ArrayList<ResInfo>(query.getResultList());
        em.close();
        return resInfos;
    }

    public static ArrayList<ResInfo> getActive(Resident resident, ResInfoType type) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfo b WHERE b.resident = :bewohner AND b.bwinfotyp = :bwinfotyp AND b.from <= :from AND b.to >= :to ORDER BY b.from DESC");
        query.setParameter("bewohner", resident);
        query.setParameter("bwinfotyp", type);
        query.setParameter("from", new Date());
        query.setParameter("to", new Date());
        ArrayList<ResInfo> resInfos = new ArrayList<ResInfo>(query.getResultList());
        em.close();
        return resInfos;
    }

    public static ArrayList<ResInfo> getAll(Resident resident) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfo b WHERE b.resident = :bewohner  ORDER BY b.from DESC");
        query.setParameter("bewohner", resident);
        ArrayList<ResInfo> resInfos = new ArrayList<ResInfo>(query.getResultList());
        em.close();
        return resInfos;
    }

    public static ArrayList<ResInfo> getAll(int ctype, LocalDate from, LocalDate to) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("" +
                " SELECT DISTINCT p FROM ResInfo p " +
                " JOIN p.commontags ct " +
                " WHERE ((p.from <= :from AND p.to >= :from) OR " +
                " (p.from <= :to AND p.to >= :to) OR " +
                " (p.from > :from AND p.to < :to)) " +
                " AND ct.type = :type ");
        query.setParameter("from", from.toDateTimeAtStartOfDay().toDate());
        query.setParameter("to", SYSCalendar.eod(to).toDate());
        query.setParameter("type", ctype);
        ArrayList<ResInfo> planungen = new ArrayList<ResInfo>(query.getResultList());
        em.close();
        return planungen;
    }

    public static ArrayList<ResInfo> getAllActive(Resident resident) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfo b WHERE b.resident = :bewohner AND b.to = :tfn ORDER BY b.from DESC");
        query.setParameter("bewohner", resident);
        query.setParameter("tfn", SYSConst.DATE_UNTIL_FURTHER_NOTICE);
        ArrayList<ResInfo> resInfos = new ArrayList<ResInfo>(query.getResultList());
        em.close();
        return resInfos;
    }

    public static ArrayList<ResInfo> getClosedWithActiveForms(Resident resident) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfo b WHERE b.resident = :bewohner AND b.bwinfotyp.type <> :type AND b.to < :tfn ORDER BY b.from DESC");
        query.setParameter("bewohner", resident);
        query.setParameter("type", ResInfoTypeTools.TYPE_OLD);
        query.setParameter("tfn", SYSConst.DATE_UNTIL_FURTHER_NOTICE);
        ArrayList<ResInfo> resInfos = new ArrayList<ResInfo>(query.getResultList());
        em.close();
        return resInfos;
    }

    public static ArrayList<ResInfo> getClosedWithOldForms(Resident resident) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfo b WHERE b.resident = :bewohner AND b.bwinfotyp.type = :type AND b.to < :tfn ORDER BY b.from DESC");
        query.setParameter("bewohner", resident);
        query.setParameter("type", ResInfoTypeTools.TYPE_OLD);
        query.setParameter("tfn", SYSConst.DATE_UNTIL_FURTHER_NOTICE);
        ArrayList<ResInfo> resInfos = new ArrayList<ResInfo>(query.getResultList());
        em.close();
        return resInfos;
    }

    public static ArrayList<ResInfo> getActive(Resident bewohner, ResInfoCategory cat) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfo b WHERE b.resident = :bewohner AND b.from <= :from AND b.to >= :to AND b.bwinfotyp.resInfoCat = :cat ORDER BY b.from DESC, b.bwinfotyp.bwinftyp");
        query.setParameter("bewohner", bewohner);
        query.setParameter("cat", cat);
        query.setParameter("from", new Date());
        query.setParameter("to", new Date());
        ArrayList<ResInfo> resInfos = new ArrayList<ResInfo>(query.getResultList());
        em.close();
        return resInfos;
    }

    public static ArrayList<ResInfo> getTemplatesByType(Resident resident2exclude, int resinfotype) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfo b WHERE b.resident <> :resident AND b.from <= :from AND b.to >= :to AND b.bwinfotyp.type = :resinfotype ORDER BY b.resident.name ASC");
        query.setParameter("resident", resident2exclude);
        query.setParameter("resinfotype", resinfotype);
        query.setParameter("from", new Date());
        query.setParameter("to", new Date());
        ArrayList<ResInfo> resInfos = new ArrayList<ResInfo>(query.getResultList());
        em.close();
        return resInfos;
    }

    public static ArrayList<ResInfo> getActive(Resident bewohner, int katart) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfo b WHERE b.resident = :bewohner AND b.from <= :from AND b.to >= :to AND b.bwinfotyp.resInfoCat.catType = :katart ORDER BY b.from DESC");
        query.setParameter("bewohner", bewohner);
        query.setParameter("katart", katart);
        query.setParameter("from", new Date());
        query.setParameter("to", new Date());
        ArrayList<ResInfo> resInfos = new ArrayList<ResInfo>(query.getResultList());
        em.close();
        return resInfos;
    }

    public static String getResInfosAsHTML(List<ResInfo> resInfos, boolean withClosed, String highlight) {
        String html = "";

        if (!resInfos.isEmpty()) {
            html += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\"><tr>"
                    + "<th>Kategorie</th><th>Typ</th><th>Info</th><th>Text</th>\n</tr>";
            for (ResInfo resInfo : resInfos) {
                if (withClosed || !resInfo.isClosed()) {
                    html += "<tr>";
                    html += "<td valign=\"top\">" + resInfo.getResInfoType().getResInfoCat().getText();
                    html += "</td>";
                    html += "<td valign=\"top\">" + resInfo.getResInfoType().getShortDescription();
                    html += "</td>";
                    html += "<td valign=\"top\">" + resInfo.getPITAsHTML();
                    html += resInfo.isClosed() ? "<br/>" + SYSConst.html_22x22_StopSign : "";
                    html += "</td>";
                    html += "<td valign=\"top\">" + resInfo.getHtml();
                    html += !SYSTools.catchNull(resInfo.getText()).isEmpty() ? "<p>" + SYSTools.xx("misc.msg.comment") + ": " + resInfo.getText() + "</p>" : "";
                    html += "</td>";
                    html += "</tr>\n";
                }
            }
            html += "</table>\n";
        }

        if (!SYSTools.catchNull(highlight).isEmpty()) {
            html = SYSTools.replace(html, highlight, "<font style=\"BACKGROUND-COLOR: yellow\">" + highlight + "</font>", true);
        }

        return html;
    }

    /**
     * calculates how much a given info can be period extended within a given sorted list of (other) infos including
     * the given one.
     *
     * @param info
     * @param sortedInfoList
     * @return
     */
    public static Pair<Date, Date> getMinMaxExpansion(ResInfo info, ArrayList<ResInfo> sortedInfoList) {
        Date min = null, max = null;

        ResInfo firstHauf = getFirstResinfo(info.getResident(), ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_STAY));
//        min = firstHauf.getFrom();

        if (info.getResInfoType().getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_SINGLE_INCIDENTS) {
            return new Pair<Date, Date>(null, null);
        }

        if (info.getResInfoType().getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_NOCONSTRAINTS) {
            min = firstHauf.getFrom();
            max = SYSConst.DATE_UNTIL_FURTHER_NOTICE;
            return new Pair<Date, Date>(min, max);
        }

        if (sortedInfoList.contains(info)) {
            // Liste ist "verkehrt rum" sortiert. Daher ist das linke Element, das spätere.
            int pos = sortedInfoList.indexOf(info);
            try {
                ResInfo leftElement = sortedInfoList.get(pos - 1);
                DateTime dtVon = new DateTime(leftElement.getFrom());
                max = dtVon.minusSeconds(1).toDate();
            } catch (IndexOutOfBoundsException e) {
                max = SYSConst.DATE_UNTIL_FURTHER_NOTICE;
            }

            try {
                ResInfo rightElement = sortedInfoList.get(pos + 1);
                DateTime dtBis = new DateTime(rightElement.getTo());
                min = dtBis.plusSeconds(1).toDate();
            } catch (IndexOutOfBoundsException e) {
                min = firstHauf.getFrom();
            }
        }

        return new Pair<Date, Date>(min, max);
    }


    public static boolean isGone(Resident resident) {
        ResInfo bwinfo_hauf = ResInfoTools.getLastResinfo(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_STAY));
        return bwinfo_hauf == null || getContent(bwinfo_hauf).getProperty(ResInfoTypeTools.STAY_KEY).equalsIgnoreCase(ResInfoTypeTools.STAY_VALUE_LEFT);
    }

    public static boolean isDead(Resident resident) {
        ResInfo bwinfo_hauf = ResInfoTools.getLastResinfo(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_STAY));
        return bwinfo_hauf != null && getContent(bwinfo_hauf).getProperty(ResInfoTypeTools.STAY_KEY).equalsIgnoreCase(ResInfoTypeTools.STAY_VALUE_DEAD);
    }


    /**
     * Tells since when a resident was away.
     *
     * @return Date of the departure. null if not away.
     */
    public static Date absentSince(Resident resident) {
        ResInfo lastabsence = getLastResinfo(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_ABSENCE));
        return lastabsence == null || lastabsence.isClosed() ? null : lastabsence.getFrom();
    }

    public static boolean isAway(Resident resident) {
        return absentSince(resident) != null;
    }

    /**
     * checks if a resident was present on a specific day. away means also, that he left or came back on that day.
     *
     * @param resident
     * @param targetDate
     * @return
     */
    public static boolean wasAway(Resident resident, LocalDate targetDate) {
        ArrayList<ResInfo> listAbsence = ResInfoTools.getAll(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_ABSENCE), targetDate, targetDate);
        return !listAbsence.isEmpty();
    }

    public static boolean isBiohazard(Resident resident) {
        ResInfo biohazard = ResInfoTools.getLastResinfo(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_INFECTION));
        return biohazard != null;
    }


    public static boolean isEditable(ResInfo resInfo) {
        return resInfo.getResInfoType().getType() != ResInfoTypeTools.TYPE_DIAGNOSIS
                && resInfo.getResInfoType().getType() != ResInfoTypeTools.TYPE_ABSENCE
                && resInfo.getResInfoType().getType() != ResInfoTypeTools.TYPE_STAY
                && !resInfo.getResInfoType().isObsolete()
                && resInfo.getResident().isActive()
                && (!resInfo.isClosed() || resInfo.isNoConstraints() || resInfo.isSingleIncident())
                && resInfo.getPrescription() == null;
    }

    /**
     * Ermittelt für eine ResInfo eine passende HTML Darstellung. Diese Methode wird nur bei einer Neueingabe oder Änderung
     * verwendet. ResInfo Beans speichert die HTML Darstellung aus Performance Gründen kurz nach Ihrer Entstehung ab.
     *
     * @param resInfo
     * @return
     */
    public static String getContentAsHTML(ResInfo resInfo) {
        ArrayList result = parseResInfo(resInfo);

        DefaultMutableTreeNode struktur = (DefaultMutableTreeNode) result.get(0);
        Properties content = (Properties) result.get(1);
        ArrayList<RiskBean> scaleriskmodel = (ArrayList<RiskBean>) result.get(2);

        return toHTML(struktur, content, scaleriskmodel);

    }

    /**
     * diese Methode erstellt eine einfache Textdarstellung für eine Wunde (WOUND..). Das findet Verwendung bei der Erstellung des Überleitbogens.
     * @param resInfo
     * @param ignorebodyscheme
     * @return
     */
    public static String getContentAsPlainText(ResInfo resInfo, boolean ignorebodyscheme) {
        ArrayList result = parseResInfo(resInfo);

        DefaultMutableTreeNode struktur = (DefaultMutableTreeNode) result.get(0);
        Properties content = (Properties) result.get(1);
        ArrayList<RiskBean> scaleriskmodel = (ArrayList<RiskBean>) result.get(2);

        return toPlainText(struktur, content, scaleriskmodel, ignorebodyscheme);

    }

    private static String toHTML(DefaultMutableTreeNode struktur, Properties content, ArrayList<RiskBean> scaleriskmodel) {
        BigDecimal scalesum = null;
        String html = "<ul>";
        if (!content.isEmpty()) {
            Enumeration en = struktur.children();

            while (en.hasMoreElements()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
                InfoTreeNodeBean infonode = (InfoTreeNodeBean) node.getUserObject();
                String value = SYSTools.catchNull(content.getProperty(infonode.getName()));

                if (node.isLeaf()) {
                    if (!value.isEmpty() && !value.equalsIgnoreCase("false")) {
                        if (value.equalsIgnoreCase("true")) {
                            html += "<li><b>" + infonode.getLabel() + "</b></li>";
                        } else {
                            if (!value.equalsIgnoreCase("tnz")) {
                                OPDE.debug(infonode.getName());
                                if (!infonode.getName().equalsIgnoreCase("hauf") && (infonode.getTagName().equalsIgnoreCase("optiongroup") || infonode.getTagName().equalsIgnoreCase("scalegroup") || infonode.getTagName().equalsIgnoreCase("combobox"))) {
                                    InfoTreeNodeBean thisNode = null;
                                    try {
                                        thisNode = (InfoTreeNodeBean) findNameInTree(struktur, value).getUserObject();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        System.exit(1);
                                    }
                                    String text = SYSTools.catchNull(thisNode.getLabel());
                                    if (infonode.getTagName().equalsIgnoreCase("scalegroup")) {
                                        scalesum = scalesum == null ? thisNode.getScore() : scalesum.add(thisNode.getScore());
//                                        Double score = (Double) ((Object[]) thisNode.getUserObject())[3];
                                        html += "<li><b>" + infonode.getLabel() + ":</b> " + text + " (" + SYSTools.xx("misc.msg.scaleriskvalue") + ": " + SYSTools.formatBigDecimal(thisNode.getScore().setScale(2, BigDecimal.ROUND_UP)) + ")</li>";
//                                        scalesum += score;
                                    } else {
                                        html += "<li><b>" + infonode.getLabel() + ":</b> " + text + "</li>";
                                    }
                                } else {
                                    if (infonode.getName().equalsIgnoreCase("java")) {
                                        html += value;
                                        content.remove(infonode.getName());
                                    } else {
                                        html += "<li><b>" + infonode.getLabel() + ":</b> " + value + "</li>";
                                    }
                                }
                            }
                        }
                    }
                    if (infonode.getTagName().equalsIgnoreCase("bodyscheme")) {
                        ArrayList<String> bodyparts = new ArrayList<String>();
                        for (String key : content.stringPropertyNames()) {
                            // nur die Körperteile, die angewählt wurden.
                            //https://github.com/tloehr/Offene-Pflege.de/issues/73
                            if (key.startsWith(infonode.getName()) && content.get(key).toString().equalsIgnoreCase("true")) {
                                bodyparts.add(key.substring(4));
                            }
                        }

                        if (!bodyparts.isEmpty()) {
                            // + " " + SYSTools.xx("misc.msg.for")+
                            html += "<li><b>" + SYSTools.xx("misc.msg.bodylocations") + ":</b> ";
                            html += "<ul>";
                            for (String bodykey : bodyparts) {
                                html += "<li>" + SYSTools.xx(bodykey) + "</li>";
                            }
                            html += "</ul>";
                            html += "</li>";
                        }


                    }
                } else { // TABGROUPS, weil ist kein Blatt (Leaf)
                    // nur anzeigen, wenn es mindestens eine angekreuzte Checkbox in dieser TABGROUP gibt.
                    if (treeHasTrueCheckboxes(node, content)) {
                        html += "<li><u>" + infonode.getLabel() + "</u>" + toHTML(node, content, null) + "</li>";
                    }
                }

            } // while
            html += "</ul>";
        }

        if (scaleriskmodel != null && scalesum != null) {
            // nun noch die Einschätzung des Risikos
            // Bezeichnung und Farbe

            String risiko = SYSTools.xx("misc.msg.scalerisk.unknown");
            String color = "black";
            for (RiskBean risk : scaleriskmodel) {
                if (risk.getFrom().compareTo(scalesum) <= 0 && scalesum.compareTo(risk.getTo()) <= 0) {
                    color = risk.getColor();
                    risiko = risk.getLabel();
                    break;
                }
            }
            html += "<b><font color=\"" + color + "\">" + SYSTools.xx("misc.msg.scalerisk.rating") + ": " + scalesum + " (" + risiko + ")</font></b><br/>";
        }
        return html;
    }

    /**
     * Ausgelagerter Algorithmus aus  getContentAsPlainText().
     * @param struktur
     * @param content
     * @param scaleriskmodel
     * @param ignorebodyscheme
     * @return
     */
    private static String toPlainText(DefaultMutableTreeNode struktur, Properties content, ArrayList<RiskBean> scaleriskmodel, boolean ignorebodyscheme) {
        BigDecimal scalesum = null;
        String plaintext = "";
        if (!content.isEmpty()) {
            Enumeration en = struktur.children();

            while (en.hasMoreElements()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
                InfoTreeNodeBean infonode = (InfoTreeNodeBean) node.getUserObject();
                String value = SYSTools.catchNull(content.getProperty(infonode.getName()));

                if (node.isLeaf()) {
                    if (!value.isEmpty() && !value.equalsIgnoreCase("false")) {
                        if (value.equalsIgnoreCase("true")) {
                            plaintext += infonode.getLabel() + ", ";
                        } else {
                            if (!value.equalsIgnoreCase("tnz")) {
                                OPDE.debug(infonode.getName());
                                if (!infonode.getName().equalsIgnoreCase("hauf") && (infonode.getTagName().equalsIgnoreCase("optiongroup") || infonode.getTagName().equalsIgnoreCase("scalegroup") || infonode.getTagName().equalsIgnoreCase("combobox"))) {
                                    InfoTreeNodeBean thisNode = null;
                                    try {
                                        thisNode = (InfoTreeNodeBean) findNameInTree(struktur, value).getUserObject();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        System.exit(1);
                                    }
                                    String text = SYSTools.catchNull(thisNode.getLabel());
                                    if (infonode.getTagName().equalsIgnoreCase("scalegroup")) {
                                        scalesum = scalesum == null ? thisNode.getScore() : scalesum.add(thisNode.getScore());
                                        plaintext += infonode.getLabel() + ": " + text + " (" + SYSTools.xx("misc.msg.scaleriskvalue") + ": " + SYSTools.formatBigDecimal(thisNode.getScore().setScale(2, BigDecimal.ROUND_UP)) + "); ";
                                    } else {
                                        plaintext += infonode.getLabel() + ": " + text + "; ";
                                    }
                                } else {
                                    if (infonode.getName().equalsIgnoreCase("java")) {
                                        plaintext += value;
                                        content.remove(infonode.getName());
                                    } else {
                                        plaintext += infonode.getLabel() + ": " + value + "; ";
                                    }
                                }
                            }
                        }
                    }
                    if (!ignorebodyscheme && infonode.getTagName().equalsIgnoreCase("bodyscheme")) {
                        ArrayList<String> bodyparts = new ArrayList<String>();
                        for (String key : content.stringPropertyNames()) {
                            if (key.startsWith(infonode.getName())) {
                                bodyparts.add(key.substring(4));
                            }
                        }

                        if (!bodyparts.isEmpty()) {
                            // + " " + SYSTools.xx("misc.msg.for")+
                            plaintext += SYSTools.xx("misc.msg.bodylocations") + ": ";
                            for (String bodykey : bodyparts) {
                                plaintext += SYSTools.xx(bodykey) + ", ";
                            }
                            plaintext = plaintext.substring(0, plaintext.length() - 2) + "; ";
                        }


                    }
                } else { // TABGROUPS, weil ist kein Blatt (Leaf)
                    // nur anzeigen, wenn es mindestens eine angekreuzte Checkbox in dieser TABGROUP gibt.
                    if (treeHasTrueCheckboxes(node, content)) {
                        plaintext += infonode.getLabel() + ": " + toPlainText(node, content, null, ignorebodyscheme) + "; ";
                    }
                }

            } // while
//            plaintext += "; ";
        }

        if (scaleriskmodel != null && scalesum != null) {
            // nun noch die Einschätzung des Risikos
            // Bezeichnung und Farbe

            String risiko = SYSTools.xx("misc.msg.scalerisk.unknown");
            for (RiskBean risk : scaleriskmodel) {
                if (risk.getFrom().compareTo(scalesum) <= 0 && scalesum.compareTo(risk.getTo()) <= 0) {
                    risiko = risk.getLabel();
                    break;
                }
            }
            plaintext += SYSTools.xx("misc.msg.scalerisk.rating") + ": " + scalesum + " (" + risiko + "); ";
        }
        
        // Wenn bei der Auswertung der Plaintext leer bleibt kommt es hier zu einem Absturz.
        // Tritt nur bei Wunden auf.
        // https://github.com/tloehr/Offene-Pflege.de/issues/111
        return plaintext.isEmpty() ? plaintext : plaintext.substring(0, plaintext.length() - 2);
    }

    private static boolean treeHasTrueCheckboxes(DefaultMutableTreeNode tree, Properties content) {
        Enumeration en = tree.children();
        boolean found = false;

        while (!found & en.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
            InfoTreeNodeBean infonode = (InfoTreeNodeBean) node.getUserObject();

            if (node.isLeaf()) {
                found = infonode.getTagName().equalsIgnoreCase("checkbox") && SYSTools.catchNull(content.getProperty(infonode.getName()), "false").equalsIgnoreCase("true");
            } else {
                found = treeHasTrueCheckboxes(node, content);
            }

        }
        return found;
    }

    public static ArrayList parseResInfo(ResInfo resInfo) {
        HandlerStruktur s = new HandlerStruktur();

        try {
            // Erst Struktur...
            String texts = "<?xml version=\"1.0\"?><xml>" + resInfo.getResInfoType().getXml() + "</xml>";
            XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
            InputSource is = new org.xml.sax.InputSource(new java.io.BufferedReader(new java.io.StringReader(texts)));
            parser.setContentHandler(s);
            parser.parse(is);

        } catch (Exception sax) {
            OPDE.fatal(sax);
        }


        DefaultMutableTreeNode struktur = s.getStruktur();

        Properties content = new Properties();
        try {
            StringReader reader = new StringReader(resInfo.getProperties());
            content.load(reader);
            reader.close();
        } catch (IOException ex) {
            OPDE.fatal(ex);
        }


        ArrayList result = new ArrayList();
        result.add(struktur);
        result.add(content);
        result.add(s.getScaleriskmodel());

        return result;

    }
//
//    public static ResInfo getAnnotation4Prescription(Prescription prescription, Commontags tag) {
//        EntityManager em = OPDE.createEM();
//
//        int resinfotype_type = -1;
//        if (tag.getType() == CommontagsTools.TYPE_SYS_ANTIBIOTICS) {
//            resinfotype_type = ResInfoTypeTools.TYPE_ANTIBIOTICS;
//        }
//        if (resinfotype_type == -1) return null;
//
//        Query query = em.createQuery("SELECT b FROM ResInfo b WHERE b.prescription= :prescription AND b.bwinfotyp.type = :resinfotype ");
//        query.setParameter("prescription", prescription);
//        query.setParameter("resinfotype", resinfotype_type);
//
//        List<ResInfo> bwinfos = query.getResultList();
//        em.close();
//
//        return bwinfos.isEmpty() ? null : bwinfos.get(0);
//    }

    public static ResInfo getAnnotation4Prescription(Prescription prescription, Commontags tag) {
        for (ResInfo annotation : prescription.getAnnotations()) {
            if (CommontagsTools.getTagForAnnotation(annotation).equals(tag)) {
                return annotation;
            }

        }
        return null;
    }

    private static class HandlerStruktur extends DefaultHandler {

        private HashMap listStruct = null;
        private ArrayList colStruct = null;
        private DefaultMutableTreeNode struktur;
        private DefaultMutableTreeNode tabgroup;

        private BigDecimal scalesum = null; // Wird nur bei Skalen benutzt. Enthält immer die Gesamtsumme einer Skala.
        private ArrayList<RiskBean> scaleriskmodel;

        public void startDocument() throws SAXException {
            struktur = new DefaultMutableTreeNode(new InfoTreeNodeBean("root", "", ""));
        }

        public ArrayList<RiskBean> getScaleriskmodel() {
            return scaleriskmodel;
        }

        public void startElement(String nsURI, String strippedName, String tagName, Attributes attributes) throws SAXException {
            // Baut eine Liste mit names und labels auf.
            String name = attributes.getValue("name");
            String label = SYSTools.xx(attributes.getValue("label"));

            if (!tagName.equalsIgnoreCase("java")) {
                if (tagName.equalsIgnoreCase("list")) {
                    listStruct = new HashMap();
                    colStruct = new ArrayList();
                    listStruct.put("name", name);
                    listStruct.put("label", label);
                    listStruct.put("fk", attributes.getValue("fk"));
                    listStruct.put("pk", attributes.getValue("pk"));
                    listStruct.put("table", attributes.getValue("table"));
                } else {
                    if (listStruct != null) { // wir müssen uns innerhalb einer List Struktur befinden.
                        if (tagName.equalsIgnoreCase("col")) {
                            HashMap hm = new HashMap();
                            hm.put("name", attributes.getValue("name"));
                            String prefix = attributes.getValue("prefix");
                            if (prefix == null) {
                                prefix = "";
                            }
                            hm.put("prefix", prefix);
                            colStruct.add(hm);
                        }
                    } else { // keine LIST Struktur
                        if (name != null) {
                            if (tagName.equalsIgnoreCase("scale")) {
                                scalesum = BigDecimal.ZERO;
                                scaleriskmodel = new ArrayList<RiskBean>();
                            } else if (tagName.equalsIgnoreCase("tabgroup")) {
                                tabgroup = new DefaultMutableTreeNode(new InfoTreeNodeBean("tabgroup", name, label, Integer.parseInt(SYSTools.catchNull(attributes.getValue("size"), "0"))));
                            } else {
                                if (tabgroup != null) {
                                    tabgroup.add(new DefaultMutableTreeNode(new InfoTreeNodeBean(tagName, name, label)));
                                } else {
                                    if (scalesum != null && tagName.equalsIgnoreCase("option")) {
                                        BigDecimal score = BigDecimal.ZERO;
                                        try {
                                            score = new BigDecimal(NumberFormat.getNumberInstance().parse(attributes.getValue("score")).doubleValue());
                                        } catch (ParseException e) {
                                            // FATAL!!
                                        }
                                        struktur.add(new DefaultMutableTreeNode(new InfoTreeNodeBean(tagName, name, label, score)));
                                    } else {
                                        struktur.add(new DefaultMutableTreeNode(new InfoTreeNodeBean(tagName, name, label)));
                                    }
                                }
                            }
                        } else if (tagName.equalsIgnoreCase("risk")) {
                            // Dieser Teil ermittelt die Risikotabelle.
                            scaleriskmodel.add(new RiskBean(attributes.getValue("from"), attributes.getValue("to"), attributes.getValue("label"), attributes.getValue("color"), attributes.getValue("rating")));
                        }

                    }
                }
            } else {
                struktur.add(new DefaultMutableTreeNode(new InfoTreeNodeBean(attributes.getValue("label"), "java", attributes.getValue("classname"))));
            }

        }

        public DefaultMutableTreeNode getStruktur() {
            return struktur;
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (localName.equalsIgnoreCase("tabgroup")) {
                //System.out.println("TabGroup ends");
                struktur.add(tabgroup);
                tabgroup = null;
            }
            if (localName.equalsIgnoreCase("col")) {
                listStruct.put("col", colStruct.clone());
            }
            if (localName.equalsIgnoreCase("list")) {
                struktur.add(new DefaultMutableTreeNode(new Object[]{"list", listStruct.get("name").toString(), "", listStruct.clone()}));
                colStruct = null;
                listStruct = null;
            }

        }
    }

    public static DefaultMutableTreeNode findNameInTree(DefaultMutableTreeNode nodeintree, String name) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) nodeintree.getRoot();
        Enumeration en = root.breadthFirstEnumeration();
        boolean found = false;
        DefaultMutableTreeNode result = null;
        while (!found && en.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
            found = ((InfoTreeNodeBean) node.getUserObject()).getName().equalsIgnoreCase(name);
            if (found) {
                result = node;
            }

        }
        return result;
    }


    public static String getTXReportHeader(Resident resident, boolean withlongheader) {
        String result = "";

        result += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\">";

        if (withlongheader) {
            if (resident.getStation() != null) {
                result += "<tr><td valign=\"top\">BewohnerIn wohnt im</td><td valign=\"top\"><b>" + HomesTools.getAsText(resident.getStation().getHome()) + "</b></td></tr>";
            }
        }


        for (ResInfo resInfo : ResInfoTools.getAll(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_ROOM), SYSCalendar.midOfDay(), SYSCalendar.midOfDay())) {
            Properties p1 = SYSTools.load(resInfo.getProperties());
            long rid1 = Long.parseLong(SYSTools.catchNull(p1.getProperty("room.id"), "-1"));
            Rooms room1 = EntityTools.find(Rooms.class, rid1);

            result += SYSConst.html_table_tr(
                    SYSConst.html_table_td(
                            "misc.msg.room.of.resident", "left", "top"
                    ) +
                            SYSConst.html_table_td(
                                    SYSConst.html_bold(room1.toString()), "left", "top"
                            )
            );
        }


        ResValue weight = ResValueTools.getLast(resident, ResValueTypesTools.WEIGHT);

        BigDecimal theoreticalweight = weight == null ? null : weight.getVal1();

        result += "<tr><td valign=\"top\">Zuletzt bestimmtes Körpergewicht</td><td valign=\"top\"><b>";
        if (theoreticalweight == null) {
            result += "Die/der BW wurde noch nicht gewogen.";
//            theoreticalweight = BigDecimal.ZERO;
        } else {

            ResInfo amputation = getLastResinfo(resident, ResInfoTypeTools.TYPE_AMPUTATION);
            BigDecimal adjustmentPercentage = getWeightAdjustmentPercentage(amputation);

            if (adjustmentPercentage.equals(BigDecimal.ZERO)) {
                result += SYSTools.formatBigDecimal(weight.getVal1()) + " " + weight.getType().getUnit1() + " (" + DateFormat.getDateInstance().format(weight.getPit()) + ")";
            } else {
                result += "Das Körpergewicht muss aufgrund von Amputationen angepasst werden.<br/>";
                result += SYSTools.xx("misc.msg.amputation") + ": " + getAmputationAsCompactText(amputation) + "<br/>";
                result += "Mess-Gewicht: " + weight.getVal1().setScale(2, RoundingMode.HALF_UP) + " " + weight.getType().getUnit1() + " (" + DateFormat.getDateInstance().format(weight.getPit()) + ")<br/>";
                result += "Prozentuale Anpassung: " + adjustmentPercentage.setScale(2, RoundingMode.HALF_UP) + "&#37;<br/>";
                theoreticalweight = weight.getVal1().multiply(BigDecimal.ONE.add(adjustmentPercentage.multiply(new BigDecimal(0.01))));
                result += "Theoretisches Gewicht: " + theoreticalweight.setScale(2, RoundingMode.HALF_UP) + " " + weight.getType().getUnit1() + "<br/>";
            }

        }
        result += "</b></td></tr>";


        if (resident.isActive()) {
            ResValue height = ResValueTools.getLast(resident, ResValueTypesTools.HEIGHT);
            result += "<tr><td valign=\"top\">Zuletzt bestimmte Körpergröße</td><td valign=\"top\"><b>";
            if (height == null) {
                result += "Bisher wurde noch keine Körpergröße ermittelt.";
            } else {
                result += SYSTools.formatBigDecimal(height.getVal1()) + " " + height.getType().getUnit1() + " (" + DateFormat.getDateInstance().format(height.getPit()) + ")";
            }
            result += "</b></td></tr>";

            result += "<tr><td valign=\"top\">Ernährungsdaten</td><td valign=\"top\"><b>";

            ResInfo food = getLastResinfo(resident, ResInfoTypeTools.TYPE_FOOD);


            BigDecimal h = height == null ? null : height.getVal1();

            BigDecimal bmi = ResValueTools.getBMI(theoreticalweight, h); // body mass index
            BigDecimal ubw = getUBW(food); // usual body weight
            BigDecimal ibw = ResValueTools.getIBW(h, resident.getGender()); // ideal body weight
            BigDecimal bmr = ResValueTools.getBasalMetabolicRate(theoreticalweight, h, ResidentTools.getAge(resident).getYears(), resident.getGender()); // base metabolic rate
            BigDecimal rl = ResValueTools.getRequiredLiquid(theoreticalweight); // required amount of liquid
            BigDecimal tla = getTargetLiquidAmount(food);

            if (bmi == null) {
                result += "Ein BMI kann noch nicht bestimmt werden.<br/>";
            } else {
                result += "BMI: " + bmi.setScale(2, RoundingMode.HALF_UP) + "<br/>";
            }


            if (ubw == null) {
                result += "Das übliche Gewicht ist bisher unbekannt.<br/>";
            } else {
                result += "Übliches Gewicht: " + ubw.setScale(2, RoundingMode.HALF_UP) + " " + ResValueTypesTools.getType(ResValueTypesTools.WEIGHT).getUnit1() + "<br/>";
            }

            if (ibw == null) {
                result += "Das Idealgewicht konnte noch nicht bestimmt werden.<br/>";
            } else {
                result += "Idealgewicht: " + ibw.setScale(2, RoundingMode.HALF_UP) + " " + ResValueTypesTools.getType(ResValueTypesTools.WEIGHT).getUnit1() + "<br/>";
            }

            if (bmr == null) {
                result += "Der Grundumsatz konnte noch nicht berechnet werden.<br/>";
            } else {
                result += "Grundumsatz: " + bmr.setScale(2, RoundingMode.HALF_UP) + " kcal/24h <br/>";
                result += "tatsächlicher Umsatz: " + bmr.multiply(new BigDecimal(1.2)).setScale(2, RoundingMode.HALF_UP) + " kcal/24h (wenn Bettlägerig)  " + bmr.multiply(new BigDecimal(1.3)).setScale(2, RoundingMode.HALF_UP) + " kcal/24h (wenn normal mobilisiert)<br/>";
            }

            if (rl == null) {
                result += "Der Flüssigkeitsbedarf konnte noch nicht berechnet werden.<br/>";
            } else {
                result += "Flüssigkeitsbedarf: " + rl.setScale(2, RoundingMode.HALF_UP) + " ml/24h<br/>";
            }

            if (tla != null) {
                result += "Zieltrinkmenge: " + tla.setScale(2, RoundingMode.HALF_UP) + " ml/24h<br/>";
            }

            result += "</b></td></tr>";
        }

        ResValue bz = ResValueTools.getLast(resident, ResValueTypesTools.GLUCOSE);
        result += "<tr><td valign=\"top\">Zuletzt gemessener BZ</td><td valign=\"top\"><b>";
        if (bz == null) {
            result += "Bisher kein BZ Wert vorhanden.";
        } else {
            result += SYSTools.formatBigDecimal(bz.getVal1()) + " " + bz.getType().getUnit1() + " (" + DateFormat.getDateInstance().format(bz.getPit()) + ")";
        }
        result += "</b></td></tr>";

        ResInfo bwinfo_hauf = ResInfoTools.getLastResinfo(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_STAY));
        if (bwinfo_hauf != null) {
            result += "<tr><td valign=\"top\">" + SYSTools.xx("misc.msg.movein") + "</td><td valign=\"top\">";
            result += "<b>" + DateFormat.getDateInstance().format(bwinfo_hauf.getFrom()) + "</b>";
            result += "</td></tr>";
        }

        ResInfo bwinfo_pstf = ResInfoTools.getLastResinfo(resident, ResInfoTypeTools.TYPE_NURSING_INSURANCE);
        if (bwinfo_pstf != null) {
            result += "<tr><td valign=\"top\">" + SYSTools.xx("ninsurance.grade") + "</td><td valign=\"top\">";
            result += bwinfo_pstf.getHtml();
            result += "</td></tr>";
        }


        ResInfo lc = getLastResinfo(resident, ResInfoTypeTools.TYPE_LEGALCUSTODIANS);
        if (lc != null && !lc.isClosed()) {
            result += "<tr><td valign=\"top\">" + SYSTools.xx("misc.msg.lc") + "</td><td valign=\"top\">";
//            result += LCustodianTools.getFullName(resident.getLCustodian1());

            if (!OPDE.isAnonym()) {

                result += lc.getHtml();
//                result += ", " + resident.getLCustodian1().getPlz() + " " + resident.getLCustodian1().getOrt();
//                result += ", " + SYSTools.xx("misc.msg.phone") + ": " + resident.getLCustodian1().getTel();
//
//                if (!SYSTools.catchNull(resident.getLCustodian1().getMobil()).isEmpty()) {
//                    result += ", " + SYSTools.xx("misc.msg.mobilephone") + ": " + resident.getLCustodian1().getMobil();
//                }

            }

            result += "</td></tr>";
        }

        if (resident.getPN1() != null) {
            result += "<tr id=\"fonttext\"><td valign=\"top\">" + SYSTools.xx("misc.msg.primaryNurse") + "</td><td valign=\"top\">";
            result += resident.getPN1().getFullname();
            result += "</td></tr>";
        }


        Date absentSince = ResInfoTools.absentSince(resident);
        if (absentSince != null) {
            result += "<tr id=\"fonttext\"><td valign=\"top\">" + SYSTools.xx("misc.msg.ResidentAbsentSince") + "</td><td valign=\"top\">";
            result += DateFormat.getDateInstance().format(absentSince);
            result += "</td></tr>";
        }

        ResInfo bwinfo_angeh = ResInfoTools.getLastResinfo(resident, ResInfoTypeTools.getByID("CONFIDANTS"));
        if (bwinfo_angeh != null) {
            result += "<tr id=\"fonttext\"><td valign=\"top\">" + SYSTools.xx("misc.msg.relatives") + "</td><td valign=\"top\">";
            result += bwinfo_angeh.getHtml();
            result += "</td></tr>";
        }

        result += "</table>";

        /***
         *       ____                           _   ____                 _   _ _   _
         *      / ___| ___ _ __   ___ _ __ __ _| | |  _ \ _ __ __ _  ___| |_(_) |_(_) ___  _ __   ___ _ __
         *     | |  _ / _ \ '_ \ / _ \ '__/ _` | | | |_) | '__/ _` |/ __| __| | __| |/ _ \| '_ \ / _ \ '__|
         *     | |_| |  __/ | | |  __/ | | (_| | | |  __/| | | (_| | (__| |_| | |_| | (_) | | | |  __/ |
         *      \____|\___|_| |_|\___|_|  \__,_|_| |_|   |_|  \__,_|\___|\__|_|\__|_|\___/|_| |_|\___|_|
         *
         */
        if (resident.getGP() != null) {
            result += "<h2 id=\"fonth2\">" + SYSTools.xx("misc.msg.gp") + "</h2>";

            result += "<div id=\"fonttext\">";
            if (OPDE.isAnonym()) {
                result += "[" + SYSTools.xx("misc.msg.anon") + "]";
            } else {
                result += GPTools.getFullName(resident.getGP()) + ", " + resident.getGP().getStreet();
                result += ", " + resident.getGP().getZIP() + " " + resident.getGP().getCity();
                result += ", " + SYSTools.xx("misc.msg.phone") + ": " + resident.getGP().getTel() + ", " + SYSTools.xx("misc.msg.fax") + ": " + resident.getGP().getFax();
            }
            result += "</div>";

        }

        /***
         *      ____                  _       _ _     _
         *     / ___| _ __   ___  ___(_) __ _| (_)___| |_ ___
         *     \___ \| '_ \ / _ \/ __| |/ _` | | / __| __/ __|
         *      ___) | |_) |  __/ (__| | (_| | | \__ \ |_\__ \
         *     |____/| .__/ \___|\___|_|\__,_|_|_|___/\__|___/
         *           |_|
         */


        final ArrayList<ResInfo> specialists = ResInfoTools.getActive(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_SPECIALIST));

        if (!specialists.isEmpty()) {
            result += "<h2 id=\"fonth2\">" + SYSTools.xx("misc.msg.specialists") + "</h2>";

            result += "<div id=\"fonttext\">";
            if (OPDE.isAnonym()) {
                result += "[" + SYSTools.xx("misc.msg.anon") + "]";
            } else {
                for (ResInfo specialist : specialists) {
                    result += getContentAsHTML(specialist);
                }
            }
            result += "</div>";

        }
        return result;
    }

    public static List<ResInfo> getInfosFor(ResValue resValue) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfo b WHERE b.resValue= :resValue ");
        query.setParameter("resValue", resValue);


        List<ResInfo> bwinfos = query.getResultList();
        em.close();
        return bwinfos;
    }


    public static String getTXReport(Resident resident, boolean withlongheader,
                                     boolean medi, boolean bilanz, boolean withNReports,
                                     boolean diag, boolean grundpflege, boolean haut, boolean vital, boolean withHTMLIcons) {
        /***
         *      _   _                _
         *     | | | | ___  __ _  __| | ___ _ __
         *     | |_| |/ _ \/ _` |/ _` |/ _ \ '__|
         *     |  _  |  __/ (_| | (_| |  __/ |
         *     |_| |_|\___|\__,_|\__,_|\___|_|
         *
         */
        String result = "<h1 id=\"fonth1\">Pflegeinformationen</h1>";

        DateFormat df = DateFormat.getDateInstance();
        if (withlongheader) {
            result += "<h2 id=\"fonth2\">" + ResidentTools.getLabelText(resident) + "</h2>";
        }


        result += getTXReportHeader(resident, withlongheader);

        /***
         *      ____  _
         *     |  _ \(_) __ _  __ _ _ __   ___  ___  ___ _ __
         *     | | | | |/ _` |/ _` | '_ \ / _ \/ __|/ _ \ '_ \
         *     | |_| | | (_| | (_| | | | | (_) \__ \  __/ | | |
         *     |____/|_|\__,_|\__, |_| |_|\___/|___/\___|_| |_|
         *                    |___/
         */
        if (diag) {
            result += getDiags(resident);
        }

        /***
         *     __     __                     _
         *     \ \   / /__ _ __ ___  _ __ __| |_ __  _   _ _ __   __ _  ___ _ __
         *      \ \ / / _ \ '__/ _ \| '__/ _` | '_ \| | | | '_ \ / _` |/ _ \ '_ \
         *       \ V /  __/ | | (_) | | | (_| | | | | |_| | | | | (_| |  __/ | | |
         *        \_/ \___|_|  \___/|_|  \__,_|_| |_|\__,_|_| |_|\__, |\___|_| |_|
         *                                                       |___/
         */
        if (medi) {
            EntityManager em = OPDE.createEM();
            Query query = em.createQuery("SELECT b FROM Prescription b WHERE b.resident = :resident AND b.to > :now ");
            query.setParameter("resident", resident);
            query.setParameter("now", new Date());
            List listeVerordnungen = query.getResultList();
            Collections.sort(listeVerordnungen);
            result += PrescriptionTools.getPrescriptionsAsHTML(listeVerordnungen, true, false, false, false, false);
            em.close();
        }

        /***
         *      _   _ ____                       _
         *     | \ | |  _ \ ___ _ __   ___  _ __| |_ ___
         *     |  \| | |_) / _ \ '_ \ / _ \| '__| __/ __|
         *     | |\  |  _ <  __/ |_) | (_) | |  | |_\__ \
         *     |_| \_|_| \_\___| .__/ \___/|_|   \__|___/
         *                     |_|
         */
        if (withNReports) {
            EntityManager em = OPDE.createEM();
            Query query = em.createQuery(" " +
                    " SELECT p FROM NReport p " +
                    " JOIN p.commontags ct "
                    + " WHERE p.resident = :bewohner AND (ct.type = :handover OR ct.type = :emergency ) AND p.pit >= :von "
                    + " ORDER BY p.pit DESC ");

            query.setParameter("bewohner", resident);
            query.setParameter("von", new DateTime().toDateMidnight().minusDays(7).toDate());
            query.setParameter("handover", CommontagsTools.TYPE_SYS_HANDOVER);
            query.setParameter("emergency", CommontagsTools.TYPE_SYS_EMERGENCY);
            result += NReportTools.getNReportsAsHTML(query.getResultList(), true, null, null);
            em.close();

        }

        /***
         *      ____  _ _
         *     | __ )(_) | __ _ _ __  ____
         *     |  _ \| | |/ _` | '_ \|_  /
         *     | |_) | | | (_| | | | |/ /
         *     |____/|_|_|\__,_|_| |_/___|
         *
         */
        if (bilanz) {
            BigDecimal trinkmin = BigDecimal.ZERO;
            BigDecimal trinkmax = BigDecimal.ZERO;

            boolean hateinfuhren = ResValueTools.hatEinfuhren(resident);
            boolean hatausfuhren = ResValueTools.hatAusfuhren(resident);
            result += hateinfuhren || hatausfuhren ? "<h2 id=\"fonth2\">" + SYSTools.xx("misc.msg.liquid.result") + "</h2>" : "";

            if (hatausfuhren) {
                EntityManager em = OPDE.createEM();
                String sql = "SELECT ein.PIT, ein.EINFUHR, ifnull(aus.AUSFUHR,0) AUSFUHR, (ein.EINFUHR+ifnull(aus.AUSFUHR,0)) BILANZ FROM "
                        + "("
                        + "   SELECT PIT, SUM(Wert) AUSFUHR FROM resvalue "
                        + "   WHERE ReplacedBy IS NULL AND Wert < 0 AND BWKennung=? AND TYPE = ? AND PIT >= ? "
                        + "   GROUP BY DATE(PIT) "
                        + ") aus"
                        + " "
                        + "RIGHT OUTER JOIN"
                        + " "
                        + "("
                        + "   SELECT PIT, SUM(Wert) EINFUHR FROM resvalue "
                        + "   WHERE ReplacedBy IS NULL AND Wert > 0 AND BWKennung=? AND TYPE = ? AND PIT >= ?"
                        + "   GROUP BY DATE(PIT) "
                        + ") ein "
                        + "ON DATE(aus.PIT) = DATE(ein.PIT) "
                        + "ORDER BY aus.PIT DESC";
                Query query = em.createNativeQuery(sql);
                query.setParameter(1, resident.getRID());
                query.setParameter(2, ResValueTypesTools.LIQUIDBALANCE);
                query.setParameter(3, new DateTime().minusWeeks(1).toDateMidnight().toDate());
                query.setParameter(4, resident.getRID());
                query.setParameter(5, ResValueTypesTools.LIQUIDBALANCE);
                query.setParameter(6, new DateTime().minusWeeks(1).toDateMidnight().toDate());

                List<Object[]> list = query.getResultList();
                em.close();

                if (!list.isEmpty()) {
                    result += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\"> <tr>"
                            + "<th>" + SYSTools.xx("misc.msg.Date") + "</th><th>" + SYSTools.xx("misc.msg.ingestion") + "</th><th>" + SYSTools.xx("misc.msg.egestion") + "</th><th>" + SYSTools.xx("misc.msg.result") + "</th><th>" + SYSTools.xx("misc.msg.rating") + "</th></tr>";

                    for (Object[] objects : list) {

                        BigDecimal einfuhr = ((BigDecimal) objects[1]);
                        BigDecimal ausfuhr = ((BigDecimal) objects[2]);
                        BigDecimal ergebnis = ((BigDecimal) objects[3]);

                        result += "<tr>";
                        result += "<td>" + df.format(((Timestamp) objects[0])) + "</td>";
                        result += "<td>" + SYSTools.formatBigDecimal(einfuhr.setScale(BigDecimal.ROUND_UP)) + "</td>";
                        result += "<td>" + SYSTools.formatBigDecimal(ausfuhr.setScale(BigDecimal.ROUND_UP).abs()) + "</td>";
                        result += "<td>" + SYSTools.formatBigDecimal(ergebnis.setScale(BigDecimal.ROUND_UP)) + "</td>";
                        if (trinkmin.compareTo(einfuhr) > 0) {
                            result += "<td>Einfuhr zu niedrig. Minimum: " + SYSTools.formatBigDecimal(trinkmin.setScale(BigDecimal.ROUND_UP)) + " ml in 24h</td>";
                        } else if (trinkmax.compareTo(einfuhr) > 0) {
                            result += "<td>Einfuhr zu hoch. Maximum: " + SYSTools.formatBigDecimal(trinkmax.setScale(BigDecimal.ROUND_UP)) + " ml in 24h</td>";
                        } else {
                            result += "<td>--</td>";
                        }

                        result += "</tr>";
                    }
                    result += "</table>";
                }


            } else if (hateinfuhren) {


                EntityManager em = OPDE.createEM();
                String sql = " "
                        + " SELECT PIT, SUM(Wert) FROM resvalue "
                        + " WHERE ReplacedBy IS NULL AND Wert > 0 AND BWKennung=? AND TYPE = ? AND PIT >= ? "
                        + " GROUP BY DATE(PIT) "
                        + " ORDER BY PIT DESC";

                Query query = em.createNativeQuery(sql);
                query.setParameter(1, resident.getRID());
                query.setParameter(2, ResValueTypesTools.LIQUIDBALANCE);
                query.setParameter(3, new DateTime().minusWeeks(1).toDateMidnight().toDate());
                List<Object[]> list = query.getResultList();
                em.close();


                if (!list.isEmpty()) {

                    result += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\"><tr>"
                            + "<th>" + SYSTools.xx("misc.msg.Date") + "</th><th>" + SYSTools.xx("misc.msg.ingestion") + "</th><th>" + SYSTools.xx("misc.msg.rating") + "</th></tr>";

                    for (Object[] objects : list) {

                        BigDecimal einfuhr = ((BigDecimal) objects[1]);
                        result += "<tr>";
                        result += "<td>" + df.format(((Timestamp) objects[0])) + "</td>";
                        result += "<td>" + SYSTools.formatBigDecimal(einfuhr.setScale(BigDecimal.ROUND_UP)) + "</td>";

                        if (trinkmin.compareTo(einfuhr) > 0) {
                            result += "<td>Einfuhr zu niedrig. Minimum: " + SYSTools.formatBigDecimal(trinkmin.setScale(BigDecimal.ROUND_UP)) + " ml in 24h</td>";
                        } else if (trinkmax.compareTo(einfuhr) > 0) {
                            result += "<td>Einfuhr zu hoch. Maximum: " + SYSTools.formatBigDecimal(trinkmax.setScale(BigDecimal.ROUND_UP)) + " ml in 24h</td>";
                        } else {
                            result += "<td>--</td>";
                        }

                        result += "</tr>";
                    }
                }
                result += "</table>";

            }

        }

        /***
         *       ____                      _        __ _
         *      / ___|_ __ _   _ _ __   __| |_ __  / _| | ___  __ _  ___
         *     | |  _| '__| | | | '_ \ / _` | '_ \| |_| |/ _ \/ _` |/ _ \
         *     | |_| | |  | |_| | | | | (_| | |_) |  _| |  __/ (_| |  __/
         *      \____|_|   \__,_|_| |_|\__,_| .__/|_| |_|\___|\__, |\___|
         *                                  |_|               |___/
         */
        if (grundpflege) {
            List<ResInfo> bwinfos = getActive(resident, ResInfoCategoryTools.BASICS);
            if (!bwinfos.isEmpty()) {
                result += "<h2 id=\"fonth2\">" + bwinfos.get(0).getResInfoType().getResInfoCat().getText() + "</h2><div id=\"fonttext\">";
                for (ResInfo bwinfo : bwinfos) {
                    result += "<b>" + bwinfo.getResInfoType().getShortDescription() + "</b><br/>";
                    result += bwinfo.getHtml();
                }
                result += "</div>";
            }
//            result += "<br/><br/>";
        }


        /***
         *      _   _             _
         *     | | | | __ _ _   _| |_
         *     | |_| |/ _` | | | | __|
         *     |  _  | (_| | |_| | |_
         *     |_| |_|\__,_|\__,_|\__|
         *
         */
        if (haut) {
            List<ResInfo> bwinfos = getActive(resident, ResInfoCategoryTools.SKIN);
            if (!bwinfos.isEmpty()) {
                result += "<h2 id=\"fonth2\">" + bwinfos.get(0).getResInfoType().getResInfoCat().getText() + "</h2><div id=\"fonttext\">";
                for (ResInfo bwinfo : bwinfos) {
                    result += "<b>" + bwinfo.getResInfoType().getShortDescription() + "</b><br/>";
                    result += bwinfo.getHtml();
                }
                result += "</div>";
            }
//            result += "<br/><br/>";
        }

        /***
         *      ____                  _       _  __        __               _
         *     / ___| _ __   ___  ___(_) __ _| | \ \      / /_ _ _ __ _ __ (_)_ __   __ _ ___
         *     \___ \| '_ \ / _ \/ __| |/ _` | |  \ \ /\ / / _` | '__| '_ \| | '_ \ / _` / __|
         *      ___) | |_) |  __/ (__| | (_| | |   \ V  V / (_| | |  | | | | | | | | (_| \__ \
         *     |____/| .__/ \___|\___|_|\__,_|_|    \_/\_/ \__,_|_|  |_| |_|_|_| |_|\__, |___/
         *           |_|                                                            |___/
         */
        ResInfo biohazard = ResInfoTools.getLastResinfo(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_INFECTION));
        if (biohazard != null && biohazard.isCurrentlyValid()) {
            result += SYSConst.html_h2("misc.msg.biohazard");
            result += withHTMLIcons ? SYSConst.html_48x48_biohazard : "";
            result += getCompactHTML(biohazard);
        }

        ResInfo diabetes = ResInfoTools.getLastResinfo(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_DIABETES));
        if (diabetes != null && diabetes.isCurrentlyValid()) {
            result += SYSConst.html_h2("misc.msg.diabetes");
            result += withHTMLIcons ? SYSConst.html_48x48_diabetes : "";
            result += getCompactHTML(diabetes);
        }

        ResInfo warning = ResInfoTools.getLastResinfo(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_WARNING));
        if (warning != null && warning.isCurrentlyValid()) {
            result += SYSConst.html_h2("misc.msg.warning");
            result += withHTMLIcons ? SYSConst.html_48x48_warning : "";
            result += getCompactHTML(warning);
        }

        ResInfo allergy = ResInfoTools.getLastResinfo(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_ALLERGY));
        if (allergy != null && allergy.isCurrentlyValid()) {
            result += SYSConst.html_h2("misc.msg.allergy");
            result += withHTMLIcons ? SYSConst.html_48x48_allergy : "";
            result += getCompactHTML(allergy);
        }

        /***
         *     __     ___ _        _
         *     \ \   / (_) |_ __ _| |
         *      \ \ / /| | __/ _` | |
         *       \ V / | | || (_| | |
         *        \_/  |_|\__\__,_|_|
         *
         */
        if (vital) {
            List<ResInfo> bwinfos = getActive(resident, ResInfoCategoryTools.VITAL);
            if (!bwinfos.isEmpty()) {
                result += "<h2 id=\"fonth2\">" + bwinfos.get(0).getResInfoType().getResInfoCat().getText() + "</h2><div id=\"fonttext\">";
                for (ResInfo bwinfo : bwinfos) {
                    result += "<b>" + bwinfo.getResInfoType().getShortDescription() + "</b><br/>";
                    result += bwinfo.getHtml();
                }
                result += "</div>";
            }
//            result += "<br/><br/>";
        }


        return result;
    }

    private static String getDiags(Resident bewohner) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfo b WHERE b.resident = :bewohner AND b.bwinfotyp = :bwinfotyp AND b.to > :now ORDER BY b.from DESC");
        query.setParameter("bewohner", bewohner);
        query.setParameter("bwinfotyp", ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_DIAGNOSIS));
        query.setParameter("now", new Date());
        List<ResInfo> diags = query.getResultList();
        em.close();
        Collections.sort(diags);

        String result = "";

        if (!diags.isEmpty()) {

            result += SYSConst.html_h2("misc.msg.diags");

            String table = SYSConst.html_table_tr(
                    SYSConst.html_table_th("misc.msg.diag.icd10") +
                            SYSConst.html_table_th("misc.msg.Date") +
                            SYSConst.html_table_th("misc.msg.diag") +
                            SYSConst.html_table_th("misc.msg.diag.side") +
                            SYSConst.html_table_th("misc.msg.diag.security") +
                            SYSConst.html_table_th("misc.msg.comment")
            );


            for (ResInfo diag : diags) {
                Properties props = getContent(diag);
                table += SYSConst.html_table_tr(
                        SYSConst.html_table_td(props.getProperty("icd")) +
                                SYSConst.html_table_td(DateFormat.getDateInstance().format(diag.getFrom())) +
                                SYSConst.html_table_td(SYSTools.replace(props.getProperty("text"), "\n", "&nbsp;", false)) +
                                SYSConst.html_table_td(props.getProperty("koerperseite")) +
                                SYSConst.html_table_td(props.getProperty("diagnosesicherheit")) +

                                (SYSTools.catchNull(diag.getText()).isEmpty() ?
                                        SYSConst.html_table_td("--", "center") :
                                        SYSConst.html_table_td(diag.getText())
                                )
                );
            }


            result += SYSConst.html_table(table, "1");
        }

        return result;
    }


    public static void setContent(ResInfo bwinfo, Properties props) {
        try {
            StringWriter writer = new StringWriter();
            props.store(writer, "[" + bwinfo.getResInfoType().getID() + "] " + bwinfo.getResInfoType().getShortDescription());
            bwinfo.setProperties(writer.toString());
            writer.close();
        } catch (IOException ex) {
            OPDE.fatal(ex);
        }
    }

    public static Properties getContent(ResInfo bwinfo) {
        Properties props = new Properties();
        try {
            StringReader reader = new StringReader(bwinfo.getProperties());
            props.load(reader);
            reader.close();
        } catch (IOException ex) {
            OPDE.fatal(ex);
        }
        return props;
    }

    public static String getFallsAnonymous(int monthsback, Closure progress) {
        StringBuilder html = new StringBuilder(1000);
        LocalDate from = SYSCalendar.bom(new LocalDate().minusMonths(monthsback));
        EntityManager em = OPDE.createEM();
        DateFormat df = DateFormat.getDateInstance();
        SimpleDateFormat monthFormatter = new SimpleDateFormat("MMMM yyyy");
        int p = -1;
        progress.execute(new Pair<Integer, Integer>(p, 100));

        ResInfoType fallType = ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_FALL);

        String jpql = " " +
                " SELECT ri " +
                " FROM ResInfo ri " +
                " WHERE ri.bwinfotyp.equiv = :equiv " +
                " AND ri.resident.adminonly <> 2 " +
                " AND ri.from >= :from ";

        Query query = em.createQuery(jpql);
        query.setParameter("equiv", fallType.getEquiv());
        query.setParameter("from", from.toDateTimeAtStartOfDay().toDate());
        ArrayList<ResInfo> listData = new ArrayList<ResInfo>(query.getResultList());

        Query query1 = em.createQuery("SELECT s FROM Station s ORDER BY s.name ");
        ArrayList<Station> listStation = new ArrayList<Station>(query1.getResultList());

        em.close();

        // virtual station. is never persisted
        Station exResident = new Station(SYSTools.xx("opde.controlling.exResidents"), null);

        // Init Maps
        HashMap<LocalDate, HashMap<Station, Integer>> statMap = new HashMap<LocalDate, HashMap<Station, Integer>>();
        for (LocalDate month = from; month.compareTo(SYSCalendar.bom(new LocalDate())) <= 0; month = month.plusMonths(1)) {
            statMap.put(month, new HashMap<Station, Integer>());
            for (Station station : listStation) {
                statMap.get(month).put(station, 0);
            }
            statMap.get(month).put(exResident, 0);
        }

        p = 0;
        // Calculate Stats
        for (ResInfo ri : listData) {
            p++;
            progress.execute(new Pair<Integer, Integer>(p, listData.size()));
            LocalDate currentMonth = SYSCalendar.bom(new LocalDate(ri.getFrom()));
            Station station = ri.getResident().getStation() == null ? exResident : ri.getResident().getStation();
            int numFalls = statMap.get(currentMonth).get(station) + 1;
            statMap.get(currentMonth).put(station, numFalls);
        }

        ArrayList<LocalDate> listMonths = new ArrayList<LocalDate>(statMap.keySet());
        Collections.sort(listMonths);

        html.append(SYSConst.html_h1("opde.controlling.nursing.falls.anonymous"));
        html.append(SYSConst.html_h2(SYSTools.xx("misc.msg.analysis") + ": " + df.format(from.toDate()) + " &raquo;&raquo; " + df.format(new Date())));

        StringBuffer table = new StringBuffer(1000);
        table.append(SYSConst.html_table_tr(
                SYSConst.html_table_th("misc.msg.month") +
                        SYSConst.html_table_th("misc.msg.subdivision") +
                        SYSConst.html_table_th("opde.controlling.nursing.falls.fallCount")
        ));

        listStation.add(exResident);

        int zebra = 0;
        for (LocalDate currentMonth : listMonths) {
            zebra++;
            for (Station station : listStation) {
                table.append(SYSConst.html_table_tr(
                        SYSConst.html_table_td(monthFormatter.format(currentMonth.toDate())) +
                                SYSConst.html_table_td(station.getName()) +
                                SYSConst.html_table_td(statMap.get(currentMonth).get(station).toString(), "right")
                        , zebra % 2 == 0   // <= highlight
                ));
            }
        }

        html.append(SYSConst.html_table(table.toString(), "1"));

        statMap.clear();
        listData.clear();
        listStation.clear();

        return html.toString();
    }


    public static String getFallsByResidents(int monthsback, Closure progress) {
        StringBuilder html = new StringBuilder(1000);
        LocalDate from = SYSCalendar.bom(new LocalDate().minusMonths(monthsback));
        EntityManager em = OPDE.createEM();
        DateFormat df = DateFormat.getDateInstance();

        int p = -1;
        progress.execute(new Pair<Integer, Integer>(p, 100));

        ResInfoType fallType = ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_FALL);

        String jpql1 = " " +
                " SELECT ri " +
                " FROM ResInfo ri " +
                " WHERE ri.bwinfotyp.equiv = :equiv " +
                " AND ri.resident.adminonly <> 2 " +
                " AND ri.from >= :from ";

        Query query1 = em.createQuery(jpql1);
        query1.setParameter("equiv", fallType.getEquiv());
        query1.setParameter("from", from.toDateTimeAtStartOfDay().toDate());
        ArrayList<QProcessElement> listData = new ArrayList<QProcessElement>(query1.getResultList());

        p = 0;
        HashMap<Resident, ArrayList<QProcessElement>> dataMap = new HashMap<Resident, ArrayList<QProcessElement>>();
        for (QProcessElement element : listData) {
            p++;
            progress.execute(new Pair<Integer, Integer>(p, listData.size()));
            if (!dataMap.containsKey(element.getResident())) {
                dataMap.put(element.getResident(), new ArrayList<QProcessElement>());
            }
            dataMap.get(element.getResident()).add(element);
        }

        ArrayList<Resident> listResident = new ArrayList<Resident>(dataMap.keySet());
        Collections.sort(listResident);

        em.close();

        html.append(SYSConst.html_h1("opde.controlling.nursing.falls.byResident"));
        html.append(SYSConst.html_h2(SYSTools.xx("misc.msg.analysis") + ": " + df.format(from.toDate()) + " &raquo;&raquo; " + df.format(new Date())));

        p = 0;
        for (Resident resident : listResident) {
            int fallCount = 0;

            progress.execute(new Pair<Integer, Integer>(p, listResident.size()));
            p++;

            html.append(SYSConst.html_h2(ResidentTools.getTextCompact(resident)));

            StringBuffer table = new StringBuffer(1000);

            table.append(SYSConst.html_table_tr(
                    SYSConst.html_table_th("misc.msg.Date") +
                            SYSConst.html_table_th("misc.msg.details")
            ));

            Collections.sort(dataMap.get(resident), (o1, o2) -> new Long(o1.getPITInMillis()).compareTo(new Long(o2.getPITInMillis())) * -1);

            for (QProcessElement element : dataMap.get(resident)) {
                if (element instanceof ResInfo) {
                    fallCount++;
                }

                table.append(SYSConst.html_table_tr(
                        SYSConst.html_table_td(element.getPITAsHTML(), "left", "top") +
                                SYSConst.html_table_td(element.getContentAsHTML())
                ));
            }

            table.append(SYSConst.html_table_tr(
                    SYSConst.html_table_th("opde.controlling.nursing.falls.fallCount") +
                            SYSConst.html_table_th(new Integer(fallCount).toString())
            ));

            html.append(SYSConst.html_table(table.toString(), "1"));
        }


        dataMap.clear();
        listData.clear();
        listResident.clear();

        return html.toString();
    }

    public static String getFallsIndicatorsByMonth(int monthsback, Closure progress) throws Exception {
        StringBuilder html = new StringBuilder(1000);


        LocalDate from = SYSCalendar.bom(new LocalDate().minusMonths(monthsback));
        Interval interval = new Interval(from.toDateTimeAtStartOfDay(), SYSCalendar.eom(new LocalDate()).toDateTimeAtCurrentTime());
        DateFormat df = DateFormat.getDateInstance();

        int p = -1;
        progress.execute(new Pair<Integer, Integer>(p, 100));
        html.append(SYSConst.html_h1("opde.controlling.nursing.fallsindicators.byMonth"));
        html.append(SYSConst.html_h2(SYSTools.xx("misc.msg.analysis") + ": " + df.format(from.toDate()) + " &raquo;&raquo; " + df.format(new Date())));
        String tableContent = SYSConst.html_table_tr(SYSConst.html_table_th("Monat") + SYSConst.html_table_th("Sturzindikator"));

        p = 0;
        for (LocalDate month = from; !month.isAfter(SYSCalendar.bom(new LocalDate())); month = month.plusMonths(1)) {
            p++;
            progress.execute(new Pair<Integer, Integer>(p, new Long(interval.toDuration().getStandardDays() / 30l).intValue()));
            BigDecimal occupantDays = new BigDecimal(getOccupantDays(SYSCalendar.bom(month), SYSCalendar.min(SYSCalendar.eom(month), new LocalDate())));
            BigDecimal sumFalls = new BigDecimal(getFalls(SYSCalendar.bom(month), SYSCalendar.eom(month)).size());
            BigDecimal fallsIndicator = sumFalls.divide(occupantDays, 6, RoundingMode.HALF_UP).multiply(new BigDecimal(1000));

            tableContent += SYSConst.html_table_tr(
                    SYSConst.html_table_td(month.toString("MMMM YYYY")) +
                            SYSConst.html_table_td(SYSTools.formatBigDecimal(sumFalls) + " / " + occupantDays + " * 1000 = " + SYSTools.formatBigDecimal(fallsIndicator.setScale(2, BigDecimal.ROUND_HALF_UP)), "right")
            );
        }

        html.append(SYSConst.html_table(tableContent, "1"));

        return html.toString();
    }

    public static void closeAll(EntityManager em, Resident resident, Date enddate, String reason) throws Exception {
        Query query = em.createQuery("SELECT b FROM ResInfo b WHERE b.resident = :resident AND b.to >= :now");
        query.setParameter("resident", resident);
        query.setParameter("now", enddate);
        List<ResInfo> bwinfos = query.getResultList();

        for (ResInfo info : bwinfos) {
            em.lock(info, LockModeType.OPTIMISTIC);
            info.setTo(enddate);
            info.setUserOFF(em.merge(OPDE.getLogin().getUser()));

            if (info.getResInfoType().getType() == ResInfoTypeTools.TYPE_STAY) {
                Properties props = ResInfoTools.getContent(info);
                props.setProperty(ResInfoTypeTools.STAY_KEY, reason);
                ResInfoTools.setContent(info, props);
            }
        }
    }

    public static String getCompactHTML(ResInfo resInfo) {
        String result = SYSConst.html_div(resInfo.getHtml());
        if (!SYSTools.catchNull(resInfo.getText()).isEmpty()) {
            result += SYSConst.html_paragraph(SYSConst.html_bold(SYSTools.xx("misc.msg.comment")) + ":<br/>" + resInfo.getText().trim());
        }
        return result;
    }

    public static BigDecimal getWeightAdjustmentPercentage(ResInfo amputation) {
        BigDecimal adjustment = BigDecimal.ZERO;

        if (amputation != null) {
            Properties content = getContent(amputation);

            for (String key : new String[]{"upperleft", "upperright"}) {
                if (content.containsKey(key)) {
                    String prop = content.getProperty(key);
                    if (prop.equalsIgnoreCase("hand")) {
                        adjustment = adjustment.add(ResValueTools.HAND);
                    } else if (prop.equalsIgnoreCase("belowellbow")) {
                        adjustment = adjustment.add(ResValueTools.BELOW_ELLBOW);
                    } else if (prop.equalsIgnoreCase("aboveellbow")) {
                        adjustment = adjustment.add(ResValueTools.ABOVE_ELLBOW);
                    } else if (prop.equalsIgnoreCase("complete")) {
                        adjustment = adjustment.add(ResValueTools.ENTIRE_UPPER_EXTREMITY);
                    }
                }
            }

            for (String key : new String[]{"lowerleft", "lowerright"}) {
                if (content.containsKey(key)) {
                    String prop = content.getProperty(key);
                    if (prop.equalsIgnoreCase("foot")) {
                        adjustment = adjustment.add(ResValueTools.FOOT);
                    } else if (prop.equalsIgnoreCase("belowknee")) {
                        adjustment = adjustment.add(ResValueTools.BELOW_KNEE);
                    } else if (prop.equalsIgnoreCase("aboveknee")) {
                        adjustment = adjustment.add(ResValueTools.ABOVE_KNEE);
                    } else if (prop.equalsIgnoreCase("complete")) {
                        adjustment = adjustment.add(ResValueTools.ENTIRE_LOWER_EXTREMITY);
                    }
                }
            }
        }

        return adjustment;
    }

    public static BigDecimal getUBW(ResInfo food) {
        BigDecimal ubw = null;
        if (food != null && food.getResInfoType().getType() == ResInfoTypeTools.TYPE_FOOD) {
            Properties content = getContent(food);
            if (content.containsKey("ubw")) {
                try {
                    ubw = new BigDecimal(content.getProperty("ubw"));
                } catch (NumberFormatException nfe) {
                    ubw = null;
                }

            }
        }
        return ubw;
    }

    /**
     * @param food
     * @return
     */
    public static BigDecimal getTargetLiquidAmount(ResInfo food) {
        BigDecimal tla = null;
        if (food != null && food.getResInfoType().getType() == ResInfoTypeTools.TYPE_FOOD) {
            Properties content = getContent(food);
            if (content.containsKey("zieltrinkmenge")) {
                try {
                    tla = new BigDecimal(content.getProperty("zieltrinkmenge"));
                } catch (NumberFormatException nfe) {
                    tla = null;
                }
            }
        }
        return tla;
    }


    public static String getAmputationAsCompactText(ResInfo amputation) {
        String result = "";
        if (amputation != null) {
            Properties content = getContent(amputation);
            for (String key : new String[]{"upperleft", "upperright"}) {
                if (content.containsKey(key)) {
                    String prop = content.getProperty(key);
                    if (prop.equalsIgnoreCase("hand")) {
                        result += SYSTools.xx("amputation.hand");
                    } else if (prop.equalsIgnoreCase("belowellbow")) {
                        result += SYSTools.xx("amputation.belowellbow");
                    } else if (prop.equalsIgnoreCase("aboveellbow")) {
                        result += SYSTools.xx("amputation.aboveellbow");
                    } else if (prop.equalsIgnoreCase("complete")) {
                        result += SYSTools.xx("amputation.complete.arm");
                    }
                    if (!prop.equalsIgnoreCase("none")) {
                        result += ", " + (key.equals("upperleft") ? SYSTools.xx("misc.msg.left") : SYSTools.xx("misc.msg.right")) + "; ";
                    }
                }

            }

            for (String key : new String[]{"lowerleft", "lowerright"}) {
                if (content.containsKey(key)) {
                    String prop = content.getProperty(key);
                    if (prop.equalsIgnoreCase("foot")) {
                        result += SYSTools.xx("amputation.foot");
                    } else if (prop.equalsIgnoreCase("belowknee")) {
                        result += SYSTools.xx("amputation.belowknee");
                    } else if (prop.equalsIgnoreCase("aboveknee")) {
                        result += SYSTools.xx("amputation.aboveknee");
                    } else if (prop.equalsIgnoreCase("complete")) {
                        result += SYSTools.xx("amputation.complete.leg");
                    }
                    if (!prop.equalsIgnoreCase("none")) {
                        result += ", " + (key.equals("lowerleft") ? SYSTools.xx("misc.msg.left") : SYSTools.xx("misc.msg.right")) + "; ";
                    }
                }
            }
        }
        return result.isEmpty() ? "" : result.substring(0, result.length() - 2);

    }

    public static int getOccupantDays(Resident resident, LocalDate from, LocalDate to) {
        ResInfoType stayType = ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_STAY);
        ArrayList<ResInfo> listStays = getAll(resident, stayType, from, to);
        if (listStays.isEmpty()) return 0;

        Interval interval0 = new Interval(from.toDateTimeAtStartOfDay(), SYSCalendar.eod(to));

        ResInfoType absenceType = ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_ABSENCE);
        ArrayList<ResInfo> listAbsences = getAll(resident, absenceType, from, to);

        int daysPresent = 0;
        for (ResInfo resInfo : listStays) {
            Interval interval1 = new Interval(new LocalDate(resInfo.getFrom()).toDateTimeAtStartOfDay(), SYSCalendar.eod(new LocalDate(resInfo.getTo())));
            Interval overlap = interval0.overlap(interval1);

            if (overlap != null) {
                daysPresent += overlap.toDuration().getStandardDays() + 1;
//                OPDE.debug(daysPresent);
            }
        }

        int daysAbsent = 0;
        for (ResInfo resInfo : listAbsences) {
            Interval interval1 = new Interval(new LocalDate(resInfo.getFrom()).toDateTimeAtStartOfDay(), SYSCalendar.eod(new LocalDate(resInfo.getTo())));
            Interval overlap = interval0.overlap(interval1);

            if (overlap != null) {
                daysAbsent += overlap.toDuration().getStandardDays() + 1;
            }
        }

        return daysPresent - daysAbsent;
    }


    public static int getOccupantDays(LocalDate from, LocalDate to) {
        int daysPresent = 0;
        for (Resident resident : ResidentTools.getAll(from, to)) {
            daysPresent += getOccupantDays(resident, from, to);
        }
        return daysPresent;
    }

//    public static BigDecimal getFallIndicator(LocalDate from, LocalDate to) {
//        BigDecimal occupantDays = new BigDecimal(getOccupantDays(from, to));
//        BigDecimal sumFalls = new BigDecimal(getFalls(from, to).size());
//
//        return sumFalls.divide(occupantDays, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(1000));
//    }


    public static ArrayList<ResInfo> getFalls(LocalDate start, LocalDate end) {
        DateTime from = start.toDateTimeAtStartOfDay();
        DateTime to = SYSCalendar.eod(end);

        EntityManager em = OPDE.createEM();
        DateFormat df = DateFormat.getDateInstance();

        ResInfoType fallType = ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_FALL);

        String jpql = " " +
                " SELECT ri " +
                " FROM ResInfo ri " +
                " WHERE ri.bwinfotyp.equiv = :equiv " +
                " AND ri.resident.adminonly <> 2 " +
                " AND ((ri.from <= :from AND ri.to >= :from) OR " +
                " (ri.from <= :to AND ri.to >= :to) OR " +
                " (ri.from > :from AND ri.to < :to)) " +
                " AND ri.from >= :from ";

        Query query = em.createQuery(jpql);
        query.setParameter("equiv", fallType.getEquiv());
        query.setParameter("from", from.toDate());
        query.setParameter("to", to.toDate());
        ArrayList<ResInfo> listData = new ArrayList<ResInfo>(query.getResultList());

        return listData;
    }


    public static boolean hasSevereFallRisk(ResInfo resinfo) {
        boolean riskDetected = false;

        // eine Sturzeinschätzung alleine reicht noch nicht aus, damit es als Symbol auftauchen soll. Nur ab bei
        // sturzrisiko = mittel und sturzrisiko = ja (was stark heisst)

        if (resinfo != null && resinfo.getResInfoType().getType() == ResInfoTypeTools.TYPE_FALLRISK && resinfo.isCurrentlyValid()) {
            Properties myprops = SYSTools.load(resinfo.getProperties());
            riskDetected = myprops.getProperty("sturzrisiko").equalsIgnoreCase("ja") || myprops.getProperty("sturzrisiko").equalsIgnoreCase("mittel");
        }

        return riskDetected;
    }

    public static boolean hasSevereFallRisk(Resident resident) {
        return hasSevereFallRisk(getLastResinfo(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_FALLRISK)));
    }

    public static String getPrevalenceAntibioticUse(LocalDate day, Closure progress) {
        return "";
    }

    public static String getPrevalenceAntibioticUse(LocalDate day, Station station, Closure progress) {
        return "";
    }

    public static String getPrevalenceAntibioticUse(LocalDate day, Resident resident, Closure progress) {
        return "";
    }

}
