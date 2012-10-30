package entity.info;

import entity.HomesTools;
import entity.prescription.DocTools;
import entity.prescription.PrescriptionTools;
import entity.reports.NReportTools;
import entity.values.ResValue;
import entity.values.ResValueTools;
import entity.values.ResValueTypesTools;
import op.OPDE;
import op.tools.*;
import org.joda.time.DateTime;
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
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
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

    public static ResInfo getFirstResinfo(Resident resident, ResInfoType resInfoType) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfo b WHERE b.resident = :resident AND b.bwinfotyp = :resInfoType ORDER BY b.from DESC");
        query.setParameter("resident", resident);
        query.setParameter("resInfoType", resInfoType);
        query.setFirstResult(0);
        query.setMaxResults(1);
        List<ResInfo> bwinfos = query.getResultList();
        em.close();
        return bwinfos.isEmpty() ? null : bwinfos.get(0);
    }

    public static ArrayList<ResInfo> getByResidentAndType(Resident resident, ResInfoType type) {
        long begin = System.currentTimeMillis();
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfo b WHERE b.resident = :bewohner AND b.bwinfotyp = :bwinfotyp ORDER BY b.from DESC");
        query.setParameter("bewohner", resident);
        query.setParameter("bwinfotyp", type);
        ArrayList<ResInfo> resInfos = new ArrayList<ResInfo>(query.getResultList());
        em.close();
        SYSTools.showTimeDifference(begin);
        return resInfos;
    }

    public static ArrayList<ResInfo> getAll(Resident resident) {
        long begin = System.currentTimeMillis();
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfo b WHERE b.resident = :bewohner ORDER BY b.from DESC");
        query.setParameter("bewohner", resident);
        ArrayList<ResInfo> resInfos = new ArrayList<ResInfo>(query.getResultList());
        em.close();
        SYSTools.showTimeDifference(begin);
        return resInfos;
    }

    public static ArrayList<ResInfo> getActiveBWInfosByBewohnerUndKatArt(Resident bewohner, int katart) {
        long begin = System.currentTimeMillis();
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfo b WHERE b.resident = :bewohner AND b.from <= :from AND b.to >= :to AND b.bwinfotyp.resInfoCat.catType = :katart ORDER BY b.from DESC");
        query.setParameter("bewohner", bewohner);
        query.setParameter("katart", katart);
        query.setParameter("from", new Date());
        query.setParameter("to", new Date());
        ArrayList<ResInfo> resInfos = new ArrayList<ResInfo>(query.getResultList());
        em.close();
        SYSTools.showTimeDifference(begin);
        return resInfos;
    }

    public static String getResInfosAsHTML(List<ResInfo> resInfos, boolean withClosed) {
        String html = "";

        if (!resInfos.isEmpty()) {
//            html += (withlongheader ? " " + OPDE.lang.getString("misc.msg.for") + " " + ResidentTools.getLabelText(resInfos.get(0).getResident()) : "") + "</h2>\n";
            html += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\"><tr>"
                    + "<th>Info</th><th>Text</th>\n</tr>";
            for (ResInfo resInfo : resInfos) {
                if (withClosed || !resInfo.isClosed()) {
                    html += "<tr>";
                    html += "<td valign=\"top\">" + resInfo.getPITAsHTML();
                    html += resInfo.isClosed() ? "<br/>" + SYSConst.html_22x22_StopSign : "";
                    html += "</td>";
                    html += "<td valign=\"top\">" + resInfo.getHtml();
                    html += !SYSTools.catchNull(resInfo.getText()).isEmpty() ? "<p>" + OPDE.lang.getString("misc.msg.comment") + ": " + resInfo.getText() + "</p>" : "";
                    html += "</td>";
                    html += "</tr>\n";
                }
            }
            html += "</table>\n";
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
            max = new Date();
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
                max = new Date();
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


    public static boolean isGone(Resident bewohner) {
        ResInfo bwinfo_hauf = ResInfoTools.getLastResinfo(bewohner, ResInfoTypeTools.getByID("HAUF"));
        return bwinfo_hauf == null || getContent(bwinfo_hauf).getProperty("hauf").equalsIgnoreCase("ausgezogen");
    }

    public static boolean isDead(Resident bewohner) {
        ResInfo bwinfo_hauf = ResInfoTools.getLastResinfo(bewohner, ResInfoTypeTools.getByID("HAUF"));
        return bwinfo_hauf != null && getContent(bwinfo_hauf).getProperty("hauf").equalsIgnoreCase("verstorben");
    }


    /**
     * Tells since when a resident was away.
     *
     * @return Date of the departure. null if not away.
     */
    public static Date absentSince(Resident bewohner) {
        ResInfo lastabsence = getLastResinfo(bewohner, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_ABSENCE));
        return lastabsence == null || lastabsence.isClosed() ? null : lastabsence.getFrom();
    }

    public static boolean isAway(Resident bewohner) {
        return absentSince(bewohner) != null;
    }

    public static boolean isChangeable(ResInfo resInfo) {
        return resInfo.getResident().isActive() && (!resInfo.isClosed() || resInfo.isNoConstraints() || resInfo.isSingleIncident());
    }

    /**
     * Ermittelt für eine ResInfo eine passende HTML Darstellung. Diese Methode wird nur bei einer Neueingabe oder Änderung
     * verwendet. ResInfo Beans speicher die HTML Darstellung aus Performance Gründen kurz nach Ihrer Entstehung ab.
     *
     * @param resInfo
     * @return
     */
    public static String getContentAsHTML(ResInfo resInfo) {
        ArrayList result = parseBWInfo(resInfo);

        DefaultMutableTreeNode struktur = (DefaultMutableTreeNode) result.get(0);
        Properties content = (Properties) result.get(1);
        ArrayList<RiskBean> scaleriskmodel = (ArrayList<RiskBean>) result.get(2);

        return toHTML(struktur, content, scaleriskmodel);

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
                                        html += "<li><b>" + infonode.getLabel() + ":</b> " + text + " (Risikowert: " + thisNode.getScore().setScale(2, BigDecimal.ROUND_UP).toPlainString() + ")</li>";
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

            String risiko = "unbekanntes Risiko";
            String color = "black";
            for (RiskBean risk : scaleriskmodel) {
                if (risk.getFrom().compareTo(scalesum) <= 0 && scalesum.compareTo(risk.getTo()) <= 0) {
                    color = risk.getColor();
                    risiko = risk.getLabel();
                    break;
                }
            }
            html += "<b><font color=\"" + color + "\">Risiko-Einschätzung: " + scalesum + " (" + risiko + ")</font></b><br/>";
        }
        return html;
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

    public static ArrayList parseBWInfo(ResInfo resInfo) {
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
            String label = attributes.getValue("label");

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
                            scaleriskmodel.add(new RiskBean(attributes.getValue("from"), attributes.getValue("to"), attributes.getValue("label"), attributes.getValue("color")));
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


    public static String getTXReport(Resident bewohner, boolean withlongheader,
                                     boolean medi, boolean bilanz, boolean bericht,
                                     boolean diag, boolean grundpflege, boolean haut, boolean vital) {

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
            result += "<h2 id=\"fonth2\">" + ResidentTools.getLabelText(bewohner) + "</h2>";
        }
        result += "<table id=\"fonttext\"  border=\"1\" cellspacing=\"0\">";

//        if (print) {
//            result += "<tr><td valign=\"top\">Gedruckt:</td><td valign=\"top\"><b>" + df.format(new Date()) + " (" + OPDE.getLogin().getUser().getFullname() + ")</b></td></tr>";
//        }

        /***
         *      _____ _            _      _     _
         *     | ____(_)_ __  _ __(_) ___| |__ | |_ _   _ _ __   __ _
         *     |  _| | | '_ \| '__| |/ __| '_ \| __| | | | '_ \ / _` |
         *     | |___| | | | | |  | | (__| | | | |_| |_| | | | | (_| |
         *     |_____|_|_| |_|_|  |_|\___|_| |_|\__|\__,_|_| |_|\__, |
         *                                                      |___/
         */
        if (withlongheader) {
            if (bewohner.getStation() != null) {
                result += "<tr><td valign=\"top\">BewohnerIn wohnt im</td><td valign=\"top\"><b>" + HomesTools.getAsText(bewohner.getStation().getHome()) + "</b></td></tr>";
            }
        }

        /***
         *       ____     _   _  ___         ______               _      _     _      ______  __  __ ___
         *      / ___|_ _(_)_(_)/ _ \ ___   / / ___| _____      _(_) ___| |__ | |_   / / __ )|  \/  |_ _|
         *     | |  _| '__/ _ \| |/ // _ \ / / |  _ / _ \ \ /\ / / |/ __| '_ \| __| / /|  _ \| |\/| || |
         *     | |_| | | | (_) | |\ \  __// /| |_| |  __/\ V  V /| | (__| | | | |_ / / | |_) | |  | || |
         *      \____|_|  \___/| ||_/\___/_/  \____|\___| \_/\_/ |_|\___|_| |_|\__/_/  |____/|_|  |_|___|
         *                     |_|
         */
        ResValue weight = ResValueTools.getLast(bewohner, ResValueTypesTools.WEIGHT);
        result += "<tr><td valign=\"top\">Zuletzt bestimmtes Körpergewicht</td><td valign=\"top\"><b>";
        if (weight == null) {
            result += "Die/der BW wurde noch nicht gewogen.";
        } else {
            result += weight.getVal1().toPlainString() + " " + weight.getType().getUnit1() + " (" + df.format(weight.getPit()) + ")";
        }
        result += "</b></td></tr>";

        ResValue height = ResValueTools.getLast(bewohner, ResValueTypesTools.HEIGHT);
        result += "<tr><td valign=\"top\">Zuletzt bestimmte Körpergröße</td><td valign=\"top\"><b>";
        if (height == null) {
            result += "Bisher wurde noch keine Körpergröße ermittelt.";
        } else {
            result += height.getVal1().toPlainString() + " " + height.getType().getUnit1() + " (" + df.format(height.getPit()) + ")";
        }
        result += "</b></td></tr>";

        result += "<tr><td valign=\"top\">Somit letzter BMI</td><td valign=\"top\"><b>";
        if (weight == null || height == null) {
            result += "Ein BMI kann noch nicht bestimmt werden.";
        } else {
            BigDecimal bmi = weight.getVal1().divide(height.getVal1().pow(2), 2, BigDecimal.ROUND_HALF_UP);
            result += bmi.toPlainString();
        }
        result += "</b></td></tr>";

        /***
         *      ____ _____
         *     | __ )__  /
         *     |  _ \ / /
         *     | |_) / /_
         *     |____/____|
         *
         */
        ResValue bz = ResValueTools.getLast(bewohner, ResValueTypesTools.GLUCOSE);
        result += "<tr><td valign=\"top\">Zuletzt gemessener BZ</td><td valign=\"top\"><b>";
        if (bz == null) {
            result += "Bisher kein BZ Wert vorhanden.";
        } else {
            result += bz.getVal1().toPlainString() + " " + bz.getType().getUnit1() + " (" + df.format(bz.getPit()) + ")";
        }
        result += "</b></td></tr>";

        /***
         *      _   _    _   _   _ _____
         *     | | | |  / \ | | | |  ___|
         *     | |_| | / _ \| | | | |_
         *     |  _  |/ ___ \ |_| |  _|
         *     |_| |_/_/   \_\___/|_|
         *
         */
        ResInfo bwinfo_hauf = ResInfoTools.getLastResinfo(bewohner, ResInfoTypeTools.getByID("HAUF"));
        if (bwinfo_hauf != null) {
            result += "<tr><td valign=\"top\">" + OPDE.lang.getString("misc.msg.movein") + "</td><td valign=\"top\">";
            result += "<b>" + df.format(bwinfo_hauf.getFrom()) + "</b>";
            result += "</td></tr>";
        }

        /***
         *      ____  ____
         *     |  _ \/ ___|
         *     | |_) \___ \
         *     |  __/ ___) |
         *     |_|   |____/
         *
         */
        ResInfo bwinfo_pstf = ResInfoTools.getLastResinfo(bewohner, ResInfoTypeTools.getByID("PSTF"));
        if (bwinfo_pstf != null) {
            result += "<tr><td valign=\"top\">" + OPDE.lang.getString("misc.msg.ps") + "</td><td valign=\"top\">";
            result += bwinfo_pstf.getHtml();
            result += "</td></tr>";
        }
        /***
         *      ____       _
         *     | __ )  ___| |_ _ __ ___ _   _  ___ _ __
         *     |  _ \ / _ \ __| '__/ _ \ | | |/ _ \ '__|
         *     | |_) |  __/ |_| | |  __/ |_| |  __/ |
         *     |____/ \___|\__|_|  \___|\__,_|\___|_|
         *
         */
        if (bewohner.getLCustodian1() != null) {
            result += "<tr><td valign=\"top\">" + OPDE.lang.getString("misc.msg.lg") + "</td><td valign=\"top\">";
            result += LCustodianTools.getFullName(bewohner.getLCustodian1()) + ", " + bewohner.getLCustodian1().getStrasse();
            result += ", " + bewohner.getLCustodian1().getPlz() + " " + bewohner.getLCustodian1().getOrt();
            result += ", " + OPDE.lang.getString("misc.msg.phone") + ": " + bewohner.getLCustodian1().getTel() + ", " + OPDE.lang.getString("misc.msg.mobilephone") + ": " + bewohner.getLCustodian1().getMobil();

            result += "</td></tr>";
        }

        /***
         *      ______     __
         *     | __ ) \   / /
         *     |  _ \\ \ / /
         *     | |_) |\ V /
         *     |____/  \_/
         *
         */
        if (bewohner.getBv1() != null) {
            result += "<tr id=\"fonttext\"><td valign=\"top\">" + OPDE.lang.getString("misc.msg.bv") + "</td><td valign=\"top\">";
            result += bewohner.getBv1().getFullname();
            result += "</td></tr>";
        }

        /***
         *         _                     _     _   _      _
         *        / \   _ __   __ _  ___| |__ (_)_(_)_ __(_) __ _  ___
         *       / _ \ | '_ \ / _` |/ _ \ '_ \ / _ \| '__| |/ _` |/ _ \
         *      / ___ \| | | | (_| |  __/ | | | (_) | |  | | (_| |  __/
         *     /_/   \_\_| |_|\__, |\___|_| |_|\___/|_|  |_|\__, |\___|
         *                    |___/                         |___/
         */
        ResInfo bwinfo_angeh = ResInfoTools.getLastResinfo(bewohner, ResInfoTypeTools.getByID("ANGEH"));
        if (bwinfo_angeh != null) {
            result += "<tr id=\"fonttext\"><td valign=\"top\">" + OPDE.lang.getString("misc.msg.relatives") + "</td><td valign=\"top\">";
            result += bwinfo_angeh.getHtml();
            result += "</td></tr>";
        }

        result += "</table>";

        /***
         *      _   _                                _
         *     | | | | __ _ _   _ ___  __ _ _ __ ___| |_
         *     | |_| |/ _` | | | / __|/ _` | '__|_  / __|
         *     |  _  | (_| | |_| \__ \ (_| | |   / /| |_
         *     |_| |_|\__,_|\__,_|___/\__,_|_|  /___|\__|
         *
         */
        if (bewohner.getDoc() != null) {
            result += "<h2 id=\"fonth2\">" + OPDE.lang.getString("misc.msg.gp") + "</h2>";
            result += "<div id=\"fonttext\">" + DocTools.getFullName(bewohner.getDoc()) + ", " + bewohner.getDoc().getStrasse();
            result += ", " + bewohner.getDoc().getPlz() + " " + bewohner.getDoc().getOrt();
            result += ", " + OPDE.lang.getString("misc.msg.phone") + ": " + bewohner.getDoc().getTel() + ", " + OPDE.lang.getString("misc.msg.fax") + ": " + bewohner.getDoc().getFax();
            result += "</div>";
        }

        /***
         *      ____  _
         *     |  _ \(_) __ _  __ _ _ __   ___  ___  ___ _ __
         *     | | | | |/ _` |/ _` | '_ \ / _ \/ __|/ _ \ '_ \
         *     | |_| | | (_| | (_| | | | | (_) \__ \  __/ | | |
         *     |____/|_|\__,_|\__, |_| |_|\___/|___/\___|_| |_|
         *                    |___/
         */
        if (diag) {
            result += getDiags(bewohner);
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
            query.setParameter("resident", bewohner);
            query.setParameter("now", new Date());
            List listeVerordnungen = query.getResultList();
            Collections.sort(listeVerordnungen);
            result += PrescriptionTools.getPrescriptionsAsHTML(listeVerordnungen, true, false, false, false);
            em.close();
        }

        /***
         *      ____            _      _     _
         *     | __ )  ___ _ __(_) ___| |__ | |_ ___
         *     |  _ \ / _ \ '__| |/ __| '_ \| __/ _ \
         *     | |_) |  __/ |  | | (__| | | | ||  __/
         *     |____/ \___|_|  |_|\___|_| |_|\__\___|
         *
         */
        if (bericht) {
            EntityManager em = OPDE.createEM();
            Query query = em.createQuery("SELECT p FROM NReport p "
                    + " WHERE p.resident = :bewohner AND p.pit >= :von "
                    + " ORDER BY p.pit DESC ");
            query.setParameter("bewohner", bewohner);
            query.setParameter("von", new DateTime().toDateMidnight().minusDays(7).toDate());
            result += NReportTools.getReportsAsHTML(query.getResultList(), true, false, null, null);
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

            boolean hateinfuhren = ResValueTools.hatEinfuhren(bewohner);
            boolean hatausfuhren = ResValueTools.hatAusfuhren(bewohner);
            result += hateinfuhren || hatausfuhren ? "<h2 id=\"fonth2\">" + OPDE.lang.getString("misc.msg.liquid.result") + "</h2>" : "";

            if (hatausfuhren) {
                EntityManager em = OPDE.createEM();
                String sql = "SELECT ein.PIT, ein.EINFUHR, ifnull(aus.AUSFUHR,0) AUSFUHR, (ein.EINFUHR+ifnull(aus.AUSFUHR,0)) BILANZ FROM "
                        + "("
                        + "   SELECT PIT, SUM(Wert) AUSFUHR FROM BWerte "
                        + "   WHERE ReplacedBy IS NULL AND Wert < 0 AND BWKennung=? AND Type = ? AND PIT >= ? "
                        + "   GROUP BY DATE(PIT) "
                        + ") aus"
                        + " "
                        + "RIGHT OUTER JOIN"
                        + " "
                        + "("
                        + "   SELECT PIT, SUM(Wert) EINFUHR FROM BWerte "
                        + "   WHERE ReplacedBy IS NULL AND Wert > 0 AND BWKennung=? AND Type = ? AND PIT >= ?"
                        + "   GROUP BY DATE(PIT) "
                        + ") ein "
                        + "ON DATE(aus.PIT) = DATE(ein.PIT) "
                        + "ORDER BY aus.PIT desc";
                Query query = em.createNativeQuery(sql);
                query.setParameter(1, bewohner.getRID());
                query.setParameter(2, ResValueTypesTools.LIQUIDBALANCE);
                query.setParameter(3, new DateTime().minusWeeks(1).toDateMidnight().toDate());
                query.setParameter(4, bewohner.getRID());
                query.setParameter(5, ResValueTypesTools.LIQUIDBALANCE);
                query.setParameter(6, new DateTime().minusWeeks(1).toDateMidnight().toDate());

                List<Object[]> list = query.getResultList();
                em.close();

                if (!list.isEmpty()) {
                    result += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\"> <tr>"
                            + "<th>" + OPDE.lang.getString("misc.msg.Date") + "</th><th>" + OPDE.lang.getString("misc.msg.ingestion") + "</th><th>" + OPDE.lang.getString("misc.msg.egestion") + "</th><th>" + OPDE.lang.getString("misc.msg.result") + "</th><th>" + OPDE.lang.getString("misc.msg.rating") + "</th></tr>";

                    for (Object[] objects : list) {

                        BigDecimal einfuhr = ((BigDecimal) objects[1]);
                        BigDecimal ausfuhr = ((BigDecimal) objects[2]);
                        BigDecimal ergebnis = ((BigDecimal) objects[3]);

//                                DateFormat df = DateFormat.getDateInstance();
                        result += "<tr>";
                        result += "<td>" + df.format(((Timestamp) objects[0])) + "</td>";
                        result += "<td>" + einfuhr.setScale(BigDecimal.ROUND_UP).toPlainString() + "</td>";
                        result += "<td>" + ausfuhr.setScale(BigDecimal.ROUND_UP).abs().toPlainString() + "</td>";
                        result += "<td>" + ergebnis.setScale(BigDecimal.ROUND_UP).toPlainString() + "</td>";
                        if (trinkmin.compareTo(einfuhr) > 0) {
                            result += "<td>Einfuhr zu niedrig. Minimum: " + trinkmin.setScale(BigDecimal.ROUND_UP).toPlainString() + " ml in 24h</td>";
                        } else if (trinkmax.compareTo(einfuhr) > 0) {
                            result += "<td>Einfuhr zu hoch. Maximum: " + trinkmax.setScale(BigDecimal.ROUND_UP).toPlainString() + " ml in 24h</td>";
                        } else {
                            result += "<td>--</td>";
                        }

                        result += "</tr>";
                    }
                    result += "</table>";
                }


            } else if (hateinfuhren) {


//                String s = " SELECT PIT, SUM(Wert) EINFUHR FROM ResValue "
//                        + "   WHERE ReplacedBy = 0 AND Wert > 0 AND BWKennung=? AND XML='<LIQUIDBALANCE/>' "
//                        + "   AND DATE(PIT) >= ADDDATE(DATE(now()), INTERVAL -7 DAY) "
//                        + "   Group By DATE(PIT) "
//                        + " ORDER BY PIT desc";


                EntityManager em = OPDE.createEM();
                String sql = " "
                        + " SELECT PIT, SUM(Wert) FROM BWerte "
                        + " WHERE ReplacedBy IS NULL AND Wert > 0 AND BWKennung=? AND Type = ? AND PIT >= ? "
                        + " Group By DATE(PIT) "
                        + " ORDER BY PIT desc";

                Query query = em.createNativeQuery(sql);
                query.setParameter(1, bewohner.getRID());
                query.setParameter(2, ResValueTypesTools.LIQUIDBALANCE);
                query.setParameter(3, new DateTime().minusWeeks(1).toDateMidnight().toDate());
                List<Object[]> list = query.getResultList();
                em.close();


                if (!list.isEmpty()) {

                    result += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\"><tr>"
                            + "<th>" + OPDE.lang.getString("misc.msg.Date") + "</th><th>" + OPDE.lang.getString("misc.msg.ingestion") + "</th><th>" + OPDE.lang.getString("misc.msg.rating") + "</th></tr>";

                    for (Object[] objects : list) {

                        BigDecimal einfuhr = ((BigDecimal) objects[1]);
                        result += "<tr>";
                        result += "<td>" + df.format(((Timestamp) objects[0])) + "</td>";
                        result += "<td>" + einfuhr.setScale(BigDecimal.ROUND_UP).toPlainString() + "</td>";

                        if (trinkmin.compareTo(einfuhr) > 0) {
                            result += "<td>Einfuhr zu niedrig. Minimum: " + trinkmin.setScale(BigDecimal.ROUND_UP).toPlainString() + " ml in 24h</td>";
                        } else if (trinkmax.compareTo(einfuhr) > 0) {
                            result += "<td>Einfuhr zu hoch. Maximum: " + trinkmax.setScale(BigDecimal.ROUND_UP).toPlainString() + " ml in 24h</td>";
                        } else {
                            result += "<td>--</td>";
                        }

                        result += "</tr>";
                    }
                }
                result += "</table>";

            }
//            else {
//                result += OPDE.lang.getString("misc.msg.insufficientdata");
//            }


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
            List<ResInfo> bwinfos = getActiveBWInfosByBewohnerUndKatArt(bewohner, ResInfoCategoryTools.GRUNDPFLEGE);
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
            List<ResInfo> bwinfos = getActiveBWInfosByBewohnerUndKatArt(bewohner, ResInfoCategoryTools.HAUT);
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
         *     __     ___ _        _
         *     \ \   / (_) |_ __ _| |
         *      \ \ / /| | __/ _` | |
         *       \ V / | | || (_| | |
         *        \_/  |_|\__\__,_|_|
         *
         */
        if (vital) {
            List<ResInfo> bwinfos = getActiveBWInfosByBewohnerUndKatArt(bewohner, ResInfoCategoryTools.VITAL);
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
            result += "<h2 id=\"fonth2\">" + OPDE.lang.getString("misc.msg.diags") + "</h2>";
            result += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\">";
            result += "<tr><th>ICD</th><th>" + OPDE.lang.getString("misc.msg.diag") + "</th><th>" + OPDE.lang.getString("misc.msg.diag.side") + "</th><th>" + OPDE.lang.getString("misc.msg.diag.security") + "</th></tr>";
            for (ResInfo diag : diags) {
                Properties props = getContent(diag);
                result += "<tr><td>" + props.getProperty("icd") + "</td><td>" + props.getProperty("text") + "</td><td>" + props.getProperty("koerperseite") + "</td><td>" + props.getProperty("diagnosesicherheit") + "</td></tr>";
            }
            result += "</table>";
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

    public static void closeAll(EntityManager em, Resident bewohner, Date enddate) throws Exception {
        Query query = em.createQuery("SELECT b FROM ResInfo b WHERE b.resident = :bewohner AND b.to >= :now");
        query.setParameter("bewohner", bewohner);
        query.setParameter("now", enddate);
        List<ResInfo> bwinfos = query.getResultList();

        for (ResInfo info : bwinfos) {
            em.lock(info, LockModeType.OPTIMISTIC);
            info.setTo(enddate);
            info.setUserOFF(em.merge(OPDE.getLogin().getUser()));
        }
    }

}
