package op.care.info;

import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.OverlayTextArea;
import entity.info.ResInfo;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 11.05.13
 * Time: 13:57
 * To change this template use File | Settings | File Templates.
 */
public class PnlEditResInfo {


    public static final String internalClassID = "nursingrecords.info.dlg";
    private final int TYPE_DONT_CARE = 0;
    private final int TYPE_INT = 1;
    private final int TYPE_DOUBLE = 2;
    private final int TYPE_DATE = 3;
    private boolean scalemode = false;
    private final int TEXTFIELD_STANDARD_WIDTH = 35;

    boolean initPanel = false;
    Properties content;
    private JLabel sumlabel;

    private ArrayList<RiskBean> scaleriskmodel;
    private String scalesumlabeltext;
    private ArrayList<String> scaleButtonGroups; // eine Liste mit den Namen der Buttongroups eines scales;
    private HashMap components;
    private ResInfo resInfo;
    private Closure closure;
    //    private op.tools.PnlPIT pnlPIT;
    private JPanel pnlContent, main;
    private boolean changed = false;
    private OverlayTextArea txtComment;
    private DefaultOverlayable ovrComment;

    public PnlEditResInfo(ResInfo resInfo) {
        this(resInfo, null);
    }

    public PnlEditResInfo(ResInfo resInfo, Closure closure) {
        this.resInfo = resInfo;
        this.closure = closure;
        initPanel();
    }

    private void initPanel() {
        content = new Properties();

        pnlContent = new JPanel(new BorderLayout());
        initPanel = true;

        // Structure...
        try {
            String xmltext = "<?xml version=\"1.0\"?><structure>" + resInfo.getResInfoType().getXml() + "</structure>";
            XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
            InputSource is = new org.xml.sax.InputSource(new java.io.BufferedReader(new java.io.StringReader(xmltext)));

            HandlerDatenStruktur h = new HandlerDatenStruktur();
            parser.setContentHandler(h);

            parser.parse(is);

            txtComment = new OverlayTextArea();
            txtComment.setRows(3);
            txtComment.setWrapStyleWord(true);
            txtComment.setLineWrap(true);
            txtComment.setDisabledTextColor(Color.DARK_GRAY);
            txtComment.addCaretListener(new CaretListener() {
                @Override
                public void caretUpdate(CaretEvent e) {
                    if (initPanel) return;
                    changed = true;
                }
            });
            ovrComment = new DefaultOverlayable(txtComment);
            JLabel lblComment = new JLabel(OPDE.lang.getString("misc.msg.comment"));
            lblComment.setForeground(Color.LIGHT_GRAY);
            lblComment.setFont(SYSConst.ARIAL18BOLD);
            ovrComment.addOverlayComponent(lblComment, DefaultOverlayable.SOUTH_EAST);
            pnlContent.add(h.getPanel(), BorderLayout.CENTER);
            pnlContent.add(ovrComment, BorderLayout.SOUTH);

        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }


        // ... and content
        setContent();
        initPanel = false;

        // add apply and cancel button
        main = null;
        if (closure != null) {
            main = new JPanel(new BorderLayout());
            main.setBorder(new EmptyBorder(10, 10, 10, 10));
            main.add(pnlContent, BorderLayout.CENTER);

            JPanel btnPanel = new JPanel(new BorderLayout());

            JButton apply = new JButton(SYSConst.icon22apply);
            apply.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    closure.execute(getResInfo());
                }
            });
            btnPanel.add(apply, BorderLayout.LINE_END);
            main.add(btnPanel, BorderLayout.SOUTH);

            JPanel hdrPanel = new JPanel(new BorderLayout());
            JLabel jl = new JLabel(resInfo.getResInfoType().getShortDescription());
            jl.setFont(SYSConst.ARIAL24BOLD);
            hdrPanel.add(jl, BorderLayout.CENTER);
            hdrPanel.add(new JSeparator(), BorderLayout.SOUTH);
            main.add(jl, BorderLayout.NORTH);
        }


        SYSTools.setXEnabled(pnlContent, main != null);
    }


    public ResInfo getResInfo() {

        try {
            StringWriter writer = new StringWriter();
            content.store(writer, "[" + resInfo.getResInfoType().getID() + "] " + resInfo.getResInfoType().getShortDescription());
            resInfo.setProperties(writer.toString());
            writer.close();
        } catch (IOException e1) {
            OPDE.fatal(e1);
        }

        resInfo.setText(txtComment.getText());

        return resInfo;
    }


    /**
     * this method creates a panel based on the XML structure taken from ResInfoType and the content stored in the properties data
     * in ResInfo.
     */
    public JPanel getPanel() {
        if (main != null) return main;
        return pnlContent;
    }

    public void setEnabled(boolean enabled) {
        SYSTools.setXEnabled(pnlContent, enabled);
    }

    private void calcScale() {
        if (!scalemode) return;

        BigDecimal scalesum = BigDecimal.ZERO;

        for (String bgName : scaleButtonGroups) {
            //            OPDE.debug(components.toString());
            ButtonGroup bg = (ButtonGroup) components.get(bgName);
            Enumeration e = bg.getElements();
            boolean found = false;
            while (e.hasMoreElements() && !found) {
                AbstractButton ab = (AbstractButton) e.nextElement();
                if (ab.getModel().isSelected()) {
                    found = true;
                    scalesum = scalesum.add(((Pair<JComboBox, BigDecimal>) components.get(bgName + ":" + ab.getName())).getSecond());
                }
            }
        }

        if (scaleriskmodel != null && scalesum != null) {
            // nun noch die Einschätzung des Risikos
            // Bezeichnung und Farbe

            String risiko = "unbekanntes Risiko";
            String color = "black";
            String rating = "0";
            for (RiskBean risk : scaleriskmodel) {
                if (risk.getFrom().compareTo(scalesum) <= 0 && scalesum.compareTo(risk.getTo()) <= 0) {
                    color = risk.getColor();
                    risiko = risk.getLabel();
                    rating = risk.getRating();
                    break;
                }
            }
            sumlabel.setText(scalesumlabeltext + ": " + scalesum + " (" + risiko + ")");
            sumlabel.setForeground(GUITools.getColor(color));

            content.put("scalesum", scalesum.toString());
            content.put("risk", risiko);
            content.put("rating", rating);
        }


    }

    /**
     * tells whether the user has changed the data or not.
     *
     * @return
     */
    public boolean isChanged() {
        return changed;
    }

    private void setContent() {
        try {
            StringReader reader = new StringReader(resInfo.getProperties());
            content.load(reader);
            reader.close();
        } catch (IOException ex) {
            OPDE.fatal(ex);
        }

        txtComment.setText(resInfo.getText());

        for (Object key : components.keySet()) {
            Object entry = components.get(key);

            if (entry instanceof JRadioButton) {
                StringTokenizer st = new StringTokenizer(key.toString(), ":");
                String tagname = st.nextToken();
                String value = st.nextToken();
                ((JRadioButton) entry).setSelected(content.containsKey(tagname) && content.getProperty(tagname).equals(value));
            } else if (entry instanceof Pair) { // Scale
                StringTokenizer st = new StringTokenizer(key.toString(), ":");
                String tagname = st.nextToken();
                String value = st.nextToken();
                ((Pair<JRadioButton, BigDecimal>) entry).getFirst().setSelected(content.getProperty(tagname).equals(value));
            } else if (entry instanceof JCheckBox) {
                ((JCheckBox) entry).setSelected(content.getProperty(key.toString()).equalsIgnoreCase("true"));
            } else if (entry instanceof JTextField) {
                ((JTextField) entry).setText(SYSTools.unescapeXML(content.getProperty(key.toString())));
            } else if (entry instanceof PnlBodyScheme) {
                ((PnlBodyScheme) entry).setContent(content);
            } else if (entry instanceof JComboBox) {
                JComboBox cmb = ((JComboBox) entry);
                for (int i = 0; i < cmb.getModel().getSize(); i++) {
                    if (((ComboBoxBean) cmb.getModel().getElementAt(i)).getName().equals(content.getProperty(key.toString()))) {
                        cmb.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }

        calcScale();
    }

    private class RadioButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            JRadioButton j = (JRadioButton) evt.getSource();
            JPanel innerpanel = (JPanel) j.getParent();
            String groupname = innerpanel.getName();
            String optionname = j.getName();
            content.put(groupname, optionname);
            if (scalemode) {
                calcScale();
            }
            changed = true;
        }
    }

    private class BodySchemeItemListener implements ItemListener {
        private final String name;

        private BodySchemeItemListener(String name) {
            this.name = name;
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            content.put(name + "." + ((JCheckBox) e.getSource()).getName(), Boolean.toString(((JCheckBox) e.getSource()).isSelected()));
            changed = true;
        }
    }

    private class CheckBoxActionListener implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JCheckBox j = (JCheckBox) evt.getSource();
            String cbname = j.getName();
            content.put(cbname, Boolean.toString(j.isSelected()));
            changed = true;
        }
    }

    private class ComboBoxItemStateListener implements ItemListener {

        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            if (evt.getStateChange() != ItemEvent.SELECTED) return;
            JComboBox j = (JComboBox) evt.getSource();
//            String cmbname = j.getName();
//            ComboBoxModel cbm = j.getModel();
            ComboBoxBean bean = (ComboBoxBean) j.getSelectedItem();
            content.put(j.getName(), bean.getName());
            j.setToolTipText(bean.getTooltip());
//            ListElement le = (ListElement) cbm.getSelectedItem();
//            content.put(cmbname, le.getData());
            changed = true;
        }
    }

    private class TextFieldFocusListener implements FocusListener {
        int type = TYPE_DONT_CARE;
        Pair<DateTime, DateTime> minmax;

        TextFieldFocusListener(int type) {
            this.type = type;
            minmax = new Pair<DateTime, DateTime>(new DateTime(SYSConst.DATE_THE_VERY_BEGINNING), new DateTime(SYSConst.DATE_UNTIL_FURTHER_NOTICE));
        }

        TextFieldFocusListener(int type, Pair<DateTime, DateTime> minmax) {
            this.type = type;
            this.minmax = minmax;
        }

        public void focusGained(FocusEvent e) {
            SYSTools.markAllTxt((JTextField) e.getSource());
        }

        public void focusLost(FocusEvent e) {
            JTextField j = (JTextField) e.getSource();
            String text = ((JTextField) e.getSource()).getText();

            if (type != TYPE_DONT_CARE) {
                if (type == TYPE_DATE) {
                    try {
                        Date myDate = SYSCalendar.parseDate(text);
                        if (new DateMidnight(myDate).isBefore(minmax.getFirst().toDateMidnight())) {
                            throw new Exception("date out of bounds");
                        }
                        if (new DateMidnight(myDate).isAfter(minmax.getSecond().toDateMidnight())) {
                            throw new Exception("date out of bounds");
                        }
                        j.setText(DateFormat.getDateInstance().format(myDate));
                    } catch (Exception ex) {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.wrongdate", DisplayMessage.WARNING));
                        j.setText(DateFormat.getDateInstance().format(new Date()));
                    }
                } else {
                    NumberFormat nf = DecimalFormat.getNumberInstance();
                    text = text.replace(".", ",");
                    Number num;
                    try {
                        num = nf.parse(text);
                    } catch (ParseException ex) {
                        num = null;
                    }

                    if (type == TYPE_INT) {
                        if (num == null) {
                            num = new Integer(1);
                        }
                    }

                    if (type == TYPE_DOUBLE) {
                        if (num == null) {
                            num = new Double(1.0);
                        }
                    }
                    j.setText(num.toString());
                }
            }
            content.put(j.getName(), SYSTools.escapeXML(j.getText()));
            changed = true;
        }
    }


    /**
     * Dieser Handler ist ein SaxParser Handler. Er durchläuft das Struktur XML Dokument und erstellt einen JPanel, der alle
     * notwendigen Swing Komponenten enthält.
     * <p/>
     * Folgende XML Konstrukte können verwendet werden:
     * <ol>
     * <li><code>&lt;checkbox name=&quot;aengstlich&quot; label=&quot;ängstlich&quot;/&gt;</code> führt zu <img src="doc-files/checkbox.png">
     * </li>
     * </ol>
     * <p/>
     * Die beschriebenen Konstrukte können nacheinander verwendet werden, so dass nach einer Optiongroup mehrere Checkboxes folgen.
     * Ein Konstrukt wird immer in eine eigene JPanel mit einem FlowLayout eingeschlossen (innerpanel).
     * Die innerpanels werden dann alle der Reihe nach wieder in eine JPanel (untereinander, GridLayout) eingefügt (outerpanel).
     * Diese outerpanel ist letztlich das Ergebnis.
     * <p/>
     * Ausserdem schreibt der Handler in die beiden HashMaps <code>components</code> und <code>antwort</code>. <code>components</code> enthält die
     * erstellten Components, der Zugriff erfolgt über das <code>name</code> Attribut aus der XML Struktur. So dass man, gemäß des obigen Beispiels unter 1.), über
     * <code>component.get("aengstlich")</code> den Zugriff auf die entsprechend JCheckbox erhält.
     * <p/>
     * <code>antwort</code> enthält den aktuellen Zustand des jeweiligen Widgets. Bei Checkboxes (wie im Beispiel beschrieben): ("aengstlich", "false"). Bei Optiongroups
     * setzt sich der Name des einzelnen Radiobuttons aus gruppenname und optionname zusammen: ("hilfebedarf.uA", "true"). Textfelder enthalten den Eingabetext direkt:
     * ("vorname", "Torsten"). Listen enthalten den Primary Key der entsprechenden Tabellenzeile (meist ist das ein <code>long</code> Wert: ("zimm", 38).
     */
    private class HandlerDatenStruktur extends DefaultHandler {

        private JPanel outerpanel;
        private JPanel innerpanel;
        private boolean tabgroup;
        private String groupname;
        private DefaultComboBoxModel boxModel;

        @Override
        public void startDocument() throws SAXException {
            components = new HashMap();
            boxModel = null;
            outerpanel = new JPanel(new RiverLayout());
            tabgroup = false;
        }

        @Override
        public void startElement(String nsURI, String strippedName, String tagName, Attributes attributes) throws SAXException {
//            OPDE.debug(SYSTools.catchNull(attributes.getValue("name"), "noname"));
            // ---------------------- OPTIONGROUPS --------------------------------
            if (tagName.equalsIgnoreCase("optiongroup") || tagName.equalsIgnoreCase("scalegroup")) {
                groupname = attributes.getValue("name");
                //Diese HashMap enthält alle Buttongroups zugeordnet zu den Gruppennamen
                //ButtonGroup thisBG = new ButtonGroup();
                components.put(groupname, new ButtonGroup()); // Jede neue Optiongroup braucht eine eigene Buttongroup.
                if (scalemode) {
                    scaleButtonGroups.add(groupname);
                }
                innerpanel = new JPanel(new RiverLayout());
                innerpanel.setName(groupname);
                if (attributes.getValue("label") != null) {
                    JLabel jl = new JLabel(attributes.getValue("label") + ":");
                    jl.setToolTipText(SYSTools.toHTML(SYSTools.catchNull(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')));
                    outerpanel.add("br left", jl);
                }
            }
            if (tagName.equalsIgnoreCase("scale")) {
                scalemode = true;
                scalesumlabeltext = attributes.getValue("label");
                scaleButtonGroups = new ArrayList();
                scaleriskmodel = new ArrayList();
            }
            if (tagName.equalsIgnoreCase("risk")) {
                scaleriskmodel.add(new RiskBean(attributes.getValue("from"), attributes.getValue("to"), attributes.getValue("label"), attributes.getValue("color"), attributes.getValue("rating")));
            }
            /***
             *      _        _
             *     | |_ __ _| |__  __ _ _ _ ___ _  _ _ __
             *     |  _/ _` | '_ \/ _` | '_/ _ \ || | '_ \
             *      \__\__,_|_.__/\__, |_| \___/\_,_| .__/
             *                    |___/             |_|
             */
            if (tagName.equalsIgnoreCase("tabgroup")) {
                JLabel jl = new JLabel(attributes.getValue("label") + ":");
                if (!SYSTools.catchNull(attributes.getValue("color")).isEmpty()) {
                    jl.setForeground(GUITools.getColor(attributes.getValue("color")));
                }

                int fontstyle = Font.BOLD;
                if (!SYSTools.catchNull(attributes.getValue("fontstyle")).isEmpty()) {
                    if (attributes.getValue("fontstyle").equalsIgnoreCase("plain")) {
                        fontstyle = Font.PLAIN;
                    }
                    if (attributes.getValue("fontstyle").equalsIgnoreCase("italic")) {
                        fontstyle = Font.ITALIC;
                    }
                }
                if (!SYSTools.catchNull(attributes.getValue("size")).isEmpty()) {
                    int size = Integer.parseInt(attributes.getValue("size"));
                    jl.setFont(new Font("Arial", fontstyle, size));
                } else {
                    jl.setFont(new Font("Arial", fontstyle, 14));
                }

                jl.setToolTipText(SYSTools.toHTML(SYSTools.catchNull(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')));
                String layout = SYSTools.catchNull(attributes.getValue("layout"), "br left");
                outerpanel.add(layout, jl);
                tabgroup = true;
            }
            if (tagName.equalsIgnoreCase("option")) {
                BigDecimal score = BigDecimal.ZERO;
                if (scalemode) {
                    score = SYSTools.parseBigDecimal(attributes.getValue("score"));
                }
                JRadioButton j = new JRadioButton(attributes.getValue("label"));
                j.setToolTipText(SYSTools.toHTML(SYSTools.catchNull(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')));
                String compName = attributes.getValue("name");
                String layout = attributes.getValue("layout");
                if (SYSTools.catchNull(layout).isEmpty()) {
                    layout = "left";
                }
                j.setName(compName);
                innerpanel.add(layout, j);

                j.addActionListener(new RadioButtonActionListener());
                if (scalemode) {
                    //                    j.addActionListener(new ScaleOptionActionListener());
                    components.put(groupname + ":" + compName, new Pair<JRadioButton, BigDecimal>(j, score)); // Hier weichen wir vom üblichen SChema ab und übergeben nicht nur die Component sondern auch den Score.
                } else {
                    j.addActionListener(new RadioButtonActionListener());
                    components.put(groupname + ":" + compName, j); // für den späteren Direktzugriff
                }
                ((ButtonGroup) components.get(groupname)).add(j); // der Knopf wird zu der passenden ButtonGroup hinzugefügt.

                if (SYSTools.catchNull(attributes.getValue("default")).equals("true")) {
                    j.setSelected(true);
                    //scalesum += score;
                    content.put(groupname, attributes.getValue("name"));
                }
            }
            /***
             *         _           _   _
             *      __| |_  ___ __| |_| |__  _____ __
             *     / _| ' \/ -_) _| / / '_ \/ _ \ \ /
             *     \__|_||_\___\__|_\_\_.__/\___/_\_\
             *
             */
            if (tagName.equalsIgnoreCase("checkbox")) {
                groupname = attributes.getValue("name");
                JCheckBox j = new JCheckBox(attributes.getValue("label"));
                j.setToolTipText(SYSTools.toHTML("<p>" + SYSTools.catchNull(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')) + "</p>");
                j.setName(groupname);
                outerpanel.add(j);
                components.put(groupname, j); // für den späteren Direktzugriff
                j.addActionListener(new CheckBoxActionListener());

                String layout = SYSTools.catchNull(attributes.getValue("layout"), tabgroup ? "tab" : "br left");
                outerpanel.add(layout, j);

                if (attributes.getValue("default") != null && attributes.getValue("default").equals("true")) {
                    j.setSelected(true);
                }
                content.put(groupname, (j.isSelected() ? "true" : "false"));
            }
            /***
             *      _           _    __ _     _    _
             *     | |_ _____ _| |_ / _(_)___| |__| |
             *     |  _/ -_) \ /  _|  _| / -_) / _` |
             *      \__\___/_\_\\__|_| |_\___|_\__,_|
             *
             */
            if (tagName.equalsIgnoreCase("textfield")) {
                groupname = attributes.getValue("name");

                TextFieldFocusListener tffl = new TextFieldFocusListener(TYPE_DONT_CARE);
                if (SYSTools.catchNull(attributes.getValue("type")).equals("int")) {
                    tffl = new TextFieldFocusListener(TYPE_INT);
                }
                if (SYSTools.catchNull(attributes.getValue("type")).equals("double")) {
                    tffl = new TextFieldFocusListener(TYPE_DOUBLE);
                }
                if (SYSTools.catchNull(attributes.getValue("type")).equals("date")) {
                    if (SYSTools.catchNull(attributes.getValue("onlyinfuture"), "false").equalsIgnoreCase("true")) {
                        tffl = new TextFieldFocusListener(TYPE_DATE, new Pair<DateTime, DateTime>(new DateTime(), new DateTime(SYSConst.DATE_UNTIL_FURTHER_NOTICE)));
                    } else {
                        tffl = new TextFieldFocusListener(TYPE_DATE);
                    }
                }
                int length = TEXTFIELD_STANDARD_WIDTH;
                if (!SYSTools.catchNull(attributes.getValue("length")).isEmpty()) {
                    length = Integer.parseInt(attributes.getValue("length"));
                }
                JLabel jl = new JLabel(attributes.getValue("label") + ":");
                JTextField j = new JTextField(length);
                j.setName(groupname);
                j.setToolTipText(SYSTools.toHTML("<p>" + SYSTools.catchNull(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')) + "</p>");
//                j.setToolTipText(SYSTools.toHTML(SYSTools.catchNull(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')));
                String layout = SYSTools.catchNull(attributes.getValue("layout"), "br left");
                outerpanel.add(layout, jl);

                String hfill = SYSTools.catchNull(attributes.getValue("hfill")).equalsIgnoreCase("false") ? "" : " hfill";
                outerpanel.add("tab" + hfill, j);
                components.put(groupname, j); // für den späteren Direktzugriff
                j.addFocusListener(tffl);
                //                j.addCaretListener(new TextFieldCaretListener(type, notempty));
                String defaultText = SYSTools.catchNull(attributes.getValue("default"));
                j.setText(defaultText);
                content.put(groupname, defaultText);
            }
            // ---------------------- Separators --------------------------------
            if (tagName.equalsIgnoreCase("separator")) {
                String layout = SYSTools.catchNull(attributes.getValue("layout"), "p hfill");
                outerpanel.add(layout, new JSeparator());
            }
            /***
             *      _             _             _
             *     | |__  ___  __| |_  _ ___ __| |_  ___ _ __  ___
             *     | '_ \/ _ \/ _` | || (_-</ _| ' \/ -_) '  \/ -_)
             *     |_.__/\___/\__,_|\_, /__/\__|_||_\___|_|_|_\___|
             *                      |__/
             */
            if (tagName.equalsIgnoreCase("bodyscheme")) {
                groupname = attributes.getValue("name");
                PnlBodyScheme pnlBodyScheme = new PnlBodyScheme(groupname, new BodySchemeItemListener(groupname));
                String layout = SYSTools.catchNull(attributes.getValue("layout"), "br left");
                components.put(groupname, pnlBodyScheme);
                outerpanel.add(layout, pnlBodyScheme);
            }
            // ---------------------- tiny ambulance car --------------------------------
            if (tagName.equalsIgnoreCase("tx")) {
                JLabel jl = new JLabel(SYSConst.icon16ambulance);
                jl.setToolTipText(OPDE.lang.getString("nursingrecords.info.tx.tooltip"));
                outerpanel.add(jl);
            }
            // ---------------------- Imagelabels --------------------------------
            if (tagName.equalsIgnoreCase("imagelabel")) {
                groupname = attributes.getValue("name");
                JLabel jl = new JLabel(new javax.swing.ImageIcon(getClass().getResource(attributes.getValue("image"))));
                jl.setToolTipText(SYSTools.toHTML("<p>" + SYSTools.catchNull(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')) + "</p>");
//                jl.setToolTipText(SYSTools.toHTML(SYSTools.catchNull(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')));
                String layout = SYSTools.catchNull(attributes.getValue("layout"), "p left");
                outerpanel.add(layout, jl);
            }
            /***
             *      _      _         _
             *     | |__ _| |__  ___| |
             *     | / _` | '_ \/ -_) |
             *     |_\__,_|_.__/\___|_|
             *
             */
            if (tagName.equalsIgnoreCase("label")) {
                groupname = attributes.getValue("name");
                JLabel jl = new JLabel(attributes.getValue("label"));
                if (!SYSTools.catchNull(attributes.getValue("color")).isEmpty()) {
                    jl.setForeground(GUITools.getColor(attributes.getValue("color")));
                }
                int fontstyle = Font.PLAIN;
                if (!SYSTools.catchNull(attributes.getValue("fontstyle")).isEmpty()) {
                    if (attributes.getValue("fontstyle").equalsIgnoreCase("bold")) {
                        fontstyle = Font.BOLD;
                    }
                    if (attributes.getValue("fontstyle").equalsIgnoreCase("italic")) {
                        fontstyle = Font.ITALIC;
                    }
                }
                if (!SYSTools.catchNull(attributes.getValue("size")).isEmpty()) {
                    int size = Integer.parseInt(attributes.getValue("size"));
                    jl.setFont(new Font("Arial", fontstyle, size));
                } else {
                    jl.setFont(new Font("Arial", fontstyle, 12));
                }
                jl.setToolTipText(SYSTools.toHTML("<p>" + SYSTools.catchNull(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')) + "</p>");
//                jl.setToolTipText(SYSTools.toHTML(SYSTools.catchNull(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')));
                String layout = SYSTools.catchNull(attributes.getValue("layout"), "br left");
                outerpanel.add(layout, jl);
            }
            /***
             *                   _         _
             *      __ ___ _ __ | |__  ___| |__  _____ __
             *     / _/ _ \ '  \| '_ \/ _ \ '_ \/ _ \ \ /
             *     \__\___/_|_|_|_.__/\___/_.__/\___/_\_\
             *
             */
            if (tagName.equalsIgnoreCase("combobox")) {
                groupname = attributes.getValue("name");
                boxModel = new DefaultComboBoxModel();
                JComboBox jcb = new JComboBox();
                jcb.setName(groupname);
                jcb.setToolTipText(SYSTools.toHTML("<p>" + SYSTools.catchNull(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')) + "</p>");
                components.put(groupname, jcb);
                jcb.addItemListener(new ComboBoxItemStateListener());
                JLabel jl = new JLabel(attributes.getValue("label") + ":");
                String layout = SYSTools.catchNull(attributes.getValue("layout"), "br left");
                outerpanel.add(layout, jl);
                outerpanel.add("tab", jcb);

            }
            /***
             *      _ _
             *     (_) |_ ___ _ __
             *     | |  _/ -_) '  \
             *     |_|\__\___|_|_|_|
             *
             */
            if (tagName.equalsIgnoreCase("item")) {
                boxModel.addElement(new ComboBoxBean(attributes.getValue("label"), attributes.getValue("name"), attributes.getValue("tooltip")));
                if (SYSTools.catchNull(attributes.getValue("default")).equals("true")) {
                    content.put(groupname, attributes.getValue("name"));
                }
            }
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (qName.equalsIgnoreCase("optiongroup") || qName.equalsIgnoreCase("scalegroup")) {
                outerpanel.add("tab", innerpanel);
            }
            if (qName.equalsIgnoreCase("scale")) {
                outerpanel.add("p hfill", new JSeparator());
                sumlabel = new JLabel();
                sumlabel.setFont(SYSConst.ARIAL20BOLD);
                outerpanel.add("br", sumlabel);
            }
            if (qName.equalsIgnoreCase("combobox")) {
                JComboBox j = (JComboBox) components.get(groupname);
                j.setModel(boxModel);
            }
        }

        public void endDocument() {
        }

        public JPanel getPanel() {
            return this.outerpanel;
        }
    } // private class HandlerDatenStruktur


    class ComboBoxBean {
        private String label, name, tooltip;

        @Override
        public String toString() {
            return label;
        }

        public ComboBoxBean(String label, String name, String tooltip) {
            this.label = label;
            this.name = name;
            this.tooltip = tooltip;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }


        public String getTooltip() {
            return tooltip;
        }

        public void setTooltip(String tooltip) {
            this.tooltip = tooltip;
        }

        @Override
        public boolean equals(Object obj) {
            return name.equals(((ComboBoxBean) obj).getName());
        }
    }


}
