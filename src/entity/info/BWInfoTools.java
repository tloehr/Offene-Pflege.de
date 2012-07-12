package entity.info;

import entity.*;
import entity.verordnungen.VerordnungTools;
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
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.IOException;
import java.io.StringReader;
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
public class BWInfoTools {

    public static final int ART_PFLEGE = 0;
    public static final int ART_VERWALTUNG = 1; // BRAUCHT RECHT USER1
    public static final int ART_STAMMDATEN = 2; // BRAUCHT RECHT USER2


    /**
     * Eine kompakte HTML Darstellung aus der aktuellen BWInfo
     * Inkl. Bemerkungsfeld.
     *
     * @param bwinfo
     */
    public static String getHTML(BWInfo bwinfo) {
        String html = "<h2 id=\"fonth2\" >" + bwinfo.getBwinfotyp().getBWInfoKurz() + "</h2><div id=\"fonttext\">" + bwinfo.getHtml() + "</div>";

        if (!SYSTools.catchNull(bwinfo.getBemerkung()).isEmpty()) {
            html += "<p id=\"fonttext\" ><b><u>" + OPDE.lang.getString("misc.msg.comment") + ":</u></b></p>";
            html += "<p id=\"fonttext\" >" + bwinfo.getBemerkung() + "</p>";
        }

        if (bwinfo.isAbgesetzt()) {
            html += "<p id=\"fonttext\" ><b><u>" + OPDE.lang.getString("misc.msg.OutdatedSince") + ":</u></b> " + DateFormat.getDateInstance().format(bwinfo.getBis()) + "</p>";
        }

        return html;
    }

    public static BWInfo getLastBWInfo(Bewohner bewohner, BWInfoTyp bwinfotyp) {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("BWInfo.findByBewohnerByBWINFOTYP_DESC");
        query.setParameter("bewohner", bewohner);
        query.setParameter("bwinfotyp", bwinfotyp);
        query.setFirstResult(0);
        query.setMaxResults(1);
        List<BWInfo> bwinfos = query.getResultList();
        em.close();
        return bwinfos.isEmpty() ? null : bwinfos.get(0);
    }

    public static List<BWInfo> findByBewohnerUndTyp(Bewohner bewohner, BWInfoTyp typ) {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("BWInfo.findByBewohnerByBWINFOTYP_DESC");
        query.setParameter("bewohner", bewohner);
        query.setParameter("bwinfotyp", typ);
        List<BWInfo> bwInfos = query.getResultList();
        em.close();
        return bwInfos;
    }

    public static List<BWInfo> findByBewohnerUndKatArt(Bewohner bewohner, int katart) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM BWInfo b WHERE b.bewohner = :bewohner AND b.bwinfotyp.bwInfokat.katArt = :katart ORDER BY b.von DESC");
        query.setParameter("bewohner", bewohner);
        query.setParameter("katart", katart);
        List<BWInfo> bwInfos = query.getResultList();
        em.close();
        return bwInfos;
    }


    public static Zeitraum getZeitraum(BWInfo bwinfo) {
        Zeitraum zeitraum = null;
        try {
            zeitraum = new Zeitraum(bwinfo.getVon(), bwinfo.getBis());
        } catch (Exception ex) {
            new DlgException(ex);
        }
        return zeitraum;
    }

    public static Pair<Date, Date> getMinMaxAusdehnung(BWInfo info, ArrayList<BWInfo> sortedInfoList) {
        Date min = null, max = null;

        if (info.getBwinfotyp().getIntervalMode() == BWInfoTypTools.MODE_INTERVAL_SINGLE_INCIDENTS) {
            return new Pair<Date, Date>(null, null);
        }

        if (info.getBwinfotyp().getIntervalMode() == BWInfoTypTools.MODE_INTERVAL_NOCONSTRAINTS) {
            min = SYSConst.DATE_VON_ANFANG_AN;
            max = SYSConst.DATE_BIS_AUF_WEITERES;
            return new Pair<Date, Date>(min, max);
        }

        if (sortedInfoList.contains(info)) {
            // Liste ist "verkehrt rum" sortiert. Daher ist das linke Element, das spätere.
            int pos = sortedInfoList.indexOf(info);
            try {
                BWInfo leftElement = sortedInfoList.get(pos - 1);
                DateTime dtVon = new DateTime(leftElement.getVon());
                max = dtVon.minusSeconds(1).toDate();
            } catch (IndexOutOfBoundsException e) {
                max = SYSConst.DATE_BIS_AUF_WEITERES;
            }

            try {
                BWInfo rightElement = sortedInfoList.get(pos + 1);
                DateTime dtBis = new DateTime(rightElement.getBis());
                min = dtBis.plusSeconds(1).toDate();
            } catch (IndexOutOfBoundsException e) {
                min = SYSConst.DATE_VON_ANFANG_AN;
            }
        }

        return new Pair<Date, Date>(min, max);
    }


    public static boolean isAusgezogen(BWInfo bwinfo) {
        return !(bwinfo == null || bwinfo.getXml().indexOf("ausgezogen") == -1);
    }

    public static boolean isVerstorben(BWInfo bwinfo) {
        return !(bwinfo == null || bwinfo.getXml().indexOf("verstorben") == -1);
    }


    /**
     * Ermittelt, seit wann ein Bewohner abwesend war.
     *
     * @return Datum des Beginns der Abwesenheitsperiode. =NULL wenn ANwesend.
     */
    public static Date getAbwesendSeit(Bewohner bewohner) {

        Date d = null;
        EntityManager em = OPDE.createEM();
        try {

            String jpql = "" +
                    " SELECT b FROM BWInfo b WHERE b.bwinfotyp.bwinftyp = 'abwe' AND b.bewohner = :bewohner AND b.von <= :von AND b.bis >= :bis";
            Query query = em.createQuery(jpql);
            query.setParameter("bewohner", bewohner);
            query.setParameter("von", new Date());
            query.setParameter("bis", new Date());
            d = (Date) query.getSingleResult();
        } catch (NoResultException nre) {
            d = null;
        } catch (Exception e) {
            OPDE.fatal(e);
        } finally {
            em.close();
        }
        return d;
    }

//    /**
//     * @return Eine ArrayList aus Date[0..1] Arrays mit jeweils Von, Bis, die alle Heimaufenthalte des BW enthalten.
//     */
//    public static List<BWInfo> getHeimaufenthalte(Bewohner bewohner) {
//        List<BWInfo> result = new Vector<BWInfo>();
//        EntityManager em = OPDE.createEM();
//        try {
//            String jpql = "" +
//                    " SELECT b FROM BWInfo b" +
//                    " WHERE b.bwinfotyp.bwinftyp = 'hauf' AND b.bewohner = :bewohner " +
//                    " ORDER BY b.von ";
//            Query query = em.createQuery(jpql);
//            query.setParameter("bewohner", bewohner);
//            result = query.getResultList();
//        } catch (Exception e) {
//            OPDE.fatal(e);
//        } finally {
//            em.close();
//        }
//        return result;
//    }

    /**
     * Ermittelt für eine BWInfo eine passende HTML Darstellung. Diese Methode wird nur bei einer Neueingabe oder Änderung
     * verwendet. BWInfo Beans speicher die HTML Darstellung aus Performance Gründen kurz nach Ihrer Entstehung ab.
     *
     * @param bwInfo
     * @return
     */
    public static String getContentAsHTML(BWInfo bwInfo) {
        ArrayList result = parseBWInfo(bwInfo);

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
        } else {
            html = "<font color=\"red\"><i>bisher unbeantwortet</i></font><br/>";
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

    public static ArrayList parseBWInfo(BWInfo bwInfo) {
        HandlerStruktur s = new HandlerStruktur();

        try {
            // Erst Struktur...
            String texts = "<?xml version=\"1.0\"?><xml>" + bwInfo.getBwinfotyp().getXml() + "</xml>";
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
            StringReader reader = new StringReader(bwInfo.getProperties());
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

//    private static class HandlerInhalt extends DefaultHandler {
//
//        private Properties content = new Properties();
//        private DefaultMutableTreeNode struktur;
//        // private String html;
//
//        HandlerInhalt(DefaultMutableTreeNode struktur) {
//            this.struktur = struktur;
//            //System.out.println("struktur: "+struktur);
//            //antwort.put("xml", xml);
//        }
//
//
//        public void startElement(String nsURI, String strippedName, String tagName, Attributes attributes) throws SAXException {
//            if (!tagName.equalsIgnoreCase("xml")) {
//                if (tagName.equalsIgnoreCase("java")) { // eine Java Klasse sorgt selbst für ihre Darstellung. Da gibts hier nicht viel zu tun.
//                    // Bisher gibts nur Diagnosen in dieser Form
//                    content.put("icd", attributes.getValue("icd"));
//                    content.put("text", attributes.getValue("text"));
//                    content.put("arztid", attributes.getValue("arztid"));
//                    content.put("khid", attributes.getValue("khid"));
//                    content.put("koerperseite", attributes.getValue("koerperseite"));
//                    content.put("diagnosesicherheit", attributes.getValue("diagnosesicherheit"));
//
//                    String atr = attributes.getValue("html");
//                    atr = atr.replaceAll("&lt;", "<");
//                    atr = atr.replaceAll("&gt;", ">");
//                    content.put(tagName, atr); // Hier steht schon HTML drin.
//                } else if (tagName.equalsIgnoreCase("unbeantwortet")) {
//                    content.clear();
//                } else {
//                    DefaultMutableTreeNode node = findNameInTree(struktur, tagName);
//                    if (node != null) {
//
//                        InfoTreeNodeBean myNode = (InfoTreeNodeBean) node.getUserObject();
//
//                        String value = SYSTools.catchNull(attributes.getValue("value"));
//
//                        if (myNode.getTagName().equalsIgnoreCase("option")) {
//                            content.put(tagName, myNode.getLabel());
//                        } else {
//                            content.put(tagName, value);
//                        }
//
//                    }
//                }
//            }
//
//        }
//
//        public void endElement(String uri, String localName, String qName) throws SAXException {
//        }
//
//        public void endDocument() {
//            //html += "</ul>";
//        }
//
//        public Properties getContent() {
//            return content;
//        }
//    } // private class HandlerFragenInhalt

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


    public static String getUeberleitung(Bewohner bewohner, boolean withlongheader,
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
            result += "<h2 id=\"fonth2\">" + BewohnerTools.getBWLabelText(bewohner) + "</h2>";
        }
        result += "<table id=\"fonttext\"  border=\"1\" cellspacing=\"0\">";

//        if (print) {
//            result += "<tr><td valign=\"top\">Gedruckt:</td><td valign=\"top\"><b>" + df.format(new Date()) + " (" + OPDE.getLogin().getUser().getNameUndVorname() + ")</b></td></tr>";
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
                result += "<tr><td valign=\"top\">BewohnerIn wohnt im</td><td valign=\"top\"><b>" + EinrichtungenTools.getAsText(bewohner.getStation().getEinrichtung()) + "</b></td></tr>";
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
        BWerte gewicht = BWerteTools.getLetztenBWert(bewohner, BWerteTools.GEWICHT);
        result += "<tr><td valign=\"top\">Zuletzt bestimmtes Körpergewicht</td><td valign=\"top\"><b>";
        if (gewicht == null) {
            result += "Die/der BW wurde noch nicht gewogen.";
        } else {
            result += gewicht.getWert().toPlainString() + " " + BWerteTools.EINHEIT[BWerteTools.GEWICHT] + " (" + df.format(gewicht.getPit()) + ")";
        }
        result += "</b></td></tr>";

        BWerte groesse = BWerteTools.getLetztenBWert(bewohner, BWerteTools.GROESSE);
        result += "<tr><td valign=\"top\">Zuletzt bestimmte Körpergröße</td><td valign=\"top\"><b>";
        if (groesse == null) {
            result += "Bisher wurde noch keine Körpergröße ermittelt.";
        } else {
            result += groesse.getWert().toPlainString() + " " + BWerteTools.EINHEIT[BWerteTools.GROESSE] + " (" + df.format(groesse.getPit()) + ")";
        }
        result += "</b></td></tr>";

        result += "<tr><td valign=\"top\">Somit letzter BMI</td><td valign=\"top\"><b>";
        if (gewicht == null || groesse == null) {
            result += "Ein BMI kann noch nicht bestimmt werden.";
        } else {
            BigDecimal bmi = gewicht.getWert().divide(groesse.getWert().pow(2), 2, BigDecimal.ROUND_HALF_UP);
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
        BWerte bz = BWerteTools.getLetztenBWert(bewohner, BWerteTools.BZ);
        result += "<tr><td valign=\"top\">Zuletzt gemessener BZ</td><td valign=\"top\"><b>";
        if (bz == null) {
            result += "Bisher kein BZ Wert vorhanden.";
        } else {
            result += bz.getWert().toPlainString() + " " + BWerteTools.EINHEIT[BWerteTools.BZ] + " (" + df.format(bz.getPit()) + ")";
        }
        result += "</b></td></tr>";

        /***
         *      ____  _ _                    _______ ___  ____   _____
         *     | __ )(_) | __ _ _ __  ____  / /_   _/ _ \|  _ \ / _ \ \
         *     |  _ \| | |/ _` | '_ \|_  / | |  | || | | | | | | | | | |
         *     | |_) | | | (_| | | | |/ /  | |  | || |_| | |_| | |_| | |
         *     |____/|_|_|\__,_|_| |_/___| | |  |_| \___/|____/ \___/| |
         *                                  \_\                     /_/
         */
//        boolean bilanzdurchbwinfo = false;
//        int trinkmin = 0;
//        int trinkmax = 0;
//        if (bilanz) {
//            BWInfo bwinfo4 = new BWInfo(bwkennung, "CONTROL", SYSCalendar.nowDBDate());
//            if (bwinfo4.getAttribute().size() > 0) {
//                HashMap antwort = (HashMap) ((HashMap) bwinfo4.getAttribute().get(0)).get("antwort");
//                bilanzdurchbwinfo = antwort.get("c.bilanz").toString().equalsIgnoreCase("true");
//
//                boolean minkontrolle = antwort.get("c.einfuhr").toString().equalsIgnoreCase("true");
//                boolean maxkontrolle = antwort.get("c.ueber").toString().equalsIgnoreCase("true");
//                if (minkontrolle || maxkontrolle) {
//                    trinkmin = Integer.parseInt(antwort.get("c.einfmenge").toString());
//                    trinkmax = Integer.parseInt(antwort.get("c.uebermenge").toString());
//                    result += "<tr><td valign=\"top\">Trinkmenge</td><td valign=\"top\">";
//                    if (minkontrolle) {
//                        result += "Die Trinkmenge sollte nicht <b><u>unter</u> " + trinkmin + " ml in 24h</b> liegen.<br/>";
//                    }
//                    if (maxkontrolle) {
//                        result += "Die Trinkmenge sollte nicht <b><u>über</u> " + trinkmax + " ml in 24h</b> liegen.";
//                    }
//                    result += "</td></tr>";
//                }
//            }
//            bwinfo4.cleanup();
//        }
//        bilanzdurchbwinfo &= bilanz;


        /***
         *      _   _    _   _   _ _____
         *     | | | |  / \ | | | |  ___|
         *     | |_| | / _ \| | | | |_
         *     |  _  |/ ___ \ |_| |  _|
         *     |_| |_/_/   \_\___/|_|
         *
         */
        BWInfo bwinfo_hauf = BWInfoTools.getLastBWInfo(bewohner, BWInfoTypTools.findByBWINFTYP("HAUF"));
        if (bwinfo_hauf != null) {
            result += "<tr><td valign=\"top\">" + OPDE.lang.getString("misc.msg.movein") + "</td><td valign=\"top\">";
            result += "<b>" + df.format(bwinfo_hauf.getVon()) + "</b>";
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
        BWInfo bwinfo_pstf = BWInfoTools.getLastBWInfo(bewohner, BWInfoTypTools.findByBWINFTYP("PSTF"));
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
        if (bewohner.getBetreuer1() != null) {
            result += "<tr><td valign=\"top\">" + OPDE.lang.getString("misc.msg.lg") + "</td><td valign=\"top\">";
            result += BetreuerTools.getFullName(bewohner.getBetreuer1()) + ", " + bewohner.getBetreuer1().getStrasse();
            result += ", " + bewohner.getBetreuer1().getPlz() + " " + bewohner.getBetreuer1().getOrt();
            result += ", " + OPDE.lang.getString("misc.msg.phone") + ": " + bewohner.getBetreuer1().getTel() + ", " + OPDE.lang.getString("misc.msg.mobilephone") + ": " + bewohner.getBetreuer1().getMobil();

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
        BWInfo bwinfo_angeh = BWInfoTools.getLastBWInfo(bewohner, BWInfoTypTools.findByBWINFTYP("ANGEH"));
        if (bwinfo_angeh != null) {
            result += "<tr><td valign=\"top\">" + OPDE.lang.getString("misc.msg.relatives") + "</td><td valign=\"top\">";
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
        if (bewohner.getHausarzt() != null) {
            result += "<h2 id=\"fonth2\">" + OPDE.lang.getString("misc.msg.gp") + "</h2>";
            result += "<div id=\"fonttext\">" + ArztTools.getFullName(bewohner.getHausarzt()) + ", " + bewohner.getHausarzt().getStrasse();
            result += ", " + bewohner.getHausarzt().getPlz() + " " + bewohner.getHausarzt().getOrt();
            result += ", " + OPDE.lang.getString("misc.msg.phone") + ": " + bewohner.getHausarzt().getTel() + ", " + OPDE.lang.getString("misc.msg.fax") + ": " + bewohner.getHausarzt().getFax();
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
            result += getDiagnosen(bewohner);
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
            Query query = em.createQuery("SELECT b FROM Verordnung b WHERE b.bewohner = :bewohner AND b.abDatum > :now ");
            query.setParameter("bewohner", bewohner);
            query.setParameter("now", new Date());
            result += VerordnungTools.getVerordnungenAsHTML(query.getResultList(), false);
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
            Query query = em.createQuery("SELECT p FROM Pflegeberichte p "
                    + " WHERE p.bewohner = :bewohner AND p.pit >= :von "
                    + " ORDER BY p.pit DESC ");
            query.setParameter("bewohner", bewohner);
            query.setParameter("von", new DateTime().toDateMidnight().minusDays(7).toDate());
            result += PflegeberichteTools.getBerichteAsHTML(query.getResultList(), true, false);
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

            result += "<h2 id=\"fonth2\">" + OPDE.lang.getString("misc.msg.liquid.result") + "</h2>";
            boolean hateinfuhren = BWerteTools.hatEinfuhren(bewohner);
            boolean hatausfuhren = BWerteTools.hatAusfuhren(bewohner);

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
                query.setParameter(1, bewohner.getBWKennung());
                query.setParameter(2, BWerteTools.BILANZ);
                query.setParameter(3, new DateTime().minusWeeks(1).toDateMidnight().toDate());
                query.setParameter(4, bewohner.getBWKennung());
                query.setParameter(5, BWerteTools.BILANZ);
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


                String s = " SELECT PIT, SUM(Wert) EINFUHR FROM BWerte "
                        + "   WHERE ReplacedBy = 0 AND Wert > 0 AND BWKennung=? AND XML='<BILANZ/>' "
                        + "   AND DATE(PIT) >= ADDDATE(DATE(now()), INTERVAL -7 DAY) "
                        + "   Group By DATE(PIT) "
                        + " ORDER BY PIT desc";


                EntityManager em = OPDE.createEM();
                String sql = " "
                        + " SELECT PIT, SUM(Wert) FROM BWerte "
                        + " WHERE ReplacedBy IS NULL AND Wert > 0 AND BWKennung=? AND Type = ? AND PIT >= ? "
                        + " Group By DATE(PIT) "
                        + " ORDER BY PIT desc";

                Query query = em.createNativeQuery(sql);
                query.setParameter(1, bewohner.getBWKennung());
                query.setParameter(2, BWerteTools.BILANZ);
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

            } else {
                result += OPDE.lang.getString("misc.msg.insufficientdata");
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
            List<BWInfo> bwinfos = BWInfoTools.findByBewohnerUndKatArt(bewohner, BWInfoKatTools.GRUNDPFLEGE);
            if (!bwinfos.isEmpty()) {
                result += "<h2 id=\"fonth2\">" + bwinfos.get(0).getBwinfotyp().getBwInfokat().getBezeichnung() + "</h2>";
                for (BWInfo bwinfo : bwinfos) {
                    result += bwinfo.getHtml();
                }
            }
            result += "<br/><br/>";
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
            List<BWInfo> bwinfos = BWInfoTools.findByBewohnerUndKatArt(bewohner, BWInfoKatTools.HAUT);
            if (!bwinfos.isEmpty()) {
                result += "<h2 id=\"fonth2\">" + bwinfos.get(0).getBwinfotyp().getBwInfokat().getBezeichnung() + "</h2>";
                for (BWInfo bwinfo : bwinfos) {
                    result += "<b>" + bwinfo.getBwinfotyp().getBWInfoKurz() + "</b><br/>";
                    result += bwinfo.getHtml();
                }
            }
            result += "<br/><br/>";
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
            List<BWInfo> bwinfos = BWInfoTools.findByBewohnerUndKatArt(bewohner, BWInfoKatTools.VITAL);
            if (!bwinfos.isEmpty()) {
                result += "<h2 id=\"fonth2\">" + bwinfos.get(0).getBwinfotyp().getBwInfokat().getBezeichnung() + "</h2>";
                for (BWInfo bwinfo : bwinfos) {
                    result += "<b>" + bwinfo.getBwinfotyp().getBWInfoKurz() + "</b><br/>";
                    result += bwinfo.getHtml();
                }
            }
            result += "<br/><br/>";
        }


        return SYSTools.toHTML(result);
    }

    private static String getDiagnosen(Bewohner bewohner) {

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM BWInfo b WHERE b.bewohner = :bewohner AND b.bwinfotyp = :bwinfotyp AND b.bis > :now ORDER BY b.von DESC");
        query.setParameter("bewohner", bewohner);
        query.setParameter("bwinfotyp", BWInfoTypTools.findByBWINFTYP("DIAG"));
        query.setParameter("now", new Date());
        List<BWInfo> diags = query.getResultList();
        em.close();

        String result = "";

        if (!diags.isEmpty()) {
            result += "<h2 id=\"fonth2\">" + OPDE.lang.getString("misc.msg.diags") + "</h2>";
            result += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\">";
            result += "<tr><th>ICD</th><th>" + OPDE.lang.getString("misc.msg.diag") + "</th><th>" + OPDE.lang.getString("misc.msg.diag.side") + "</th><th>" + OPDE.lang.getString("misc.msg.diag.security") + "</th></tr>";
            for (BWInfo diag : diags) {
                Properties props = new Properties();
                try {
                    StringReader reader = new StringReader(diag.getProperties());
                    props.load(reader);
                    reader.close();
                } catch (IOException ex) {
                    OPDE.fatal(ex);
                }
                result += "<tr><td>" + props.getProperty("icd") + "</td><td>" + props.getProperty("text") + "</td><td>" + props.getProperty("koerperseite") + "</td><td>" + props.getProperty("diagnosesicherheit") + "</td></tr>";
            }
            result += "</table>";
        }

        return result;
    }

}
