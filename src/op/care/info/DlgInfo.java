/*
 * Created by JFormDesigner on Tue Jun 26 16:48:16 CEST 2012
 */

package op.care.info;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.info.BWInfo;
import entity.info.BWInfoTypTools;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.HorizontalLayout;
import org.jdesktop.swingx.VerticalLayout;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

/**
 * @author Torsten Löhr
 */
public class DlgInfo extends MyJDialog {
    public static final String internalClassID = "nursingrecords.info.dlg";
    private final int TYPE_DONT_CARE = 0;
    private final int TYPE_INT = 1;
    private final int TYPE_DOUBLE = 2;
    private boolean scalemode = false;
    private final int TEXTFIELD_STANDARD_WIDTH = 35;

    boolean initPanel = false;
    Properties content;
    private JLabel sumlabel;

    private ArrayList<RiskBean> scaleriskmodel;
    private String scalesumlabeltext;
    private ArrayList<String> scaleButtonGroups; // eine Liste mit den Namen der Buttongroups eines scales;
    private HashMap components;
    private BWInfo bwInfo;
    private Closure actionBlock;
    private JTextArea txtBemerkung;
    private PnlUhrzeitDatum pnlUhrzeitDatum;

    public DlgInfo(BWInfo bwInfo, Closure actionBlock) {
        super();
        this.bwInfo = bwInfo;
        this.actionBlock = actionBlock;
        initComponents();
        initDialog();
//        pack();
        setVisible(true);
    }

    @Override
    public void dispose() {
        super.dispose();
        actionBlock.execute(bwInfo);
    }

    private void initDialog() {
        content = new Properties();
        txtBemerkung = new JTextArea();
        txtBemerkung.setFont(SYSConst.ARIAL14);
        txtBemerkung.setWrapStyleWord(true);
        txtBemerkung.setLineWrap(true);
        txtBemerkung.setText(SYSTools.catchNull(bwInfo.getBemerkung()));
        contentPanel.add(new JScrollPane(getPanel()), CC.xywh(1, 1, 3, 1, CC.FILL, CC.FILL));
        if (bwInfo.getBwinfotyp().getIntervalMode() == BWInfoTypTools.MODE_INTERVAL_SINGLE_INCIDENTS) {
            pnlUhrzeitDatum = new PnlUhrzeitDatum();
            JLabel header = new JLabel(OPDE.lang.getString(internalClassID + ".singleincident"));
            header.setFont(SYSConst.ARIAL20);
            JPanel panel = new JPanel(new VerticalLayout(5));
            header.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(header);
            panel.add(pnlUhrzeitDatum);
            contentPanel.add(new JScrollPane(txtBemerkung), CC.xy(1, 3, CC.FILL, CC.FILL));
            contentPanel.add(panel, CC.xy(3, 3, CC.FILL, CC.FILL));
        } else {
            contentPanel.add(new JScrollPane(txtBemerkung), CC.xywh(1, 3, 3, 1, CC.FILL, CC.FILL));
        }

        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(bwInfo.getBwinfotyp().getBWInfoKurz(), 10));
    }

    private void btnCancelActionPerformed(ActionEvent e) {
        bwInfo = null;
        dispose();
    }

    private void btnOKActionPerformed(ActionEvent e) {

        if (pnlUhrzeitDatum != null) {
            OPDE.debug(pnlUhrzeitDatum.getPIT());
            bwInfo.setVon(pnlUhrzeitDatum.getPIT());
        }

        try {
            StringWriter writer = new StringWriter();
            content.store(writer, "[" + bwInfo.getBwinfotyp().getBwinftyp() + "] " + bwInfo.getBwinfotyp().getBWInfoKurz());
            bwInfo.setProperties(writer.toString());
            OPDE.debug(writer.toString());
            writer.close();
        } catch (IOException e1) {
            OPDE.fatal(e1);
        }

        bwInfo.setBemerkung(txtBemerkung.getText().trim());

        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        buttonBar = new JPanel();
        btnCancel = new JButton();
        btnOK = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.DIALOG_BORDER);
            dialogPane.setLayout(new FormLayout(
                    "default:grow",
                    "fill:default:grow, $rgap, fill:default"));

            //======== contentPanel ========
            {
                contentPanel.setLayout(new FormLayout(
                        "default:grow, $lcgap, 133dlu",
                        "fill:default:grow, $lgap, 90dlu"));
            }
            dialogPane.add(contentPanel, CC.xy(1, 1));

            //======== buttonBar ========
            {
                buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
                buttonBar.setLayout(new HorizontalLayout(5));

                //---- btnCancel ----
                btnCancel.setText(null);
                btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
                btnCancel.setHorizontalAlignment(SwingConstants.RIGHT);
                btnCancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnCancelActionPerformed(e);
                    }
                });
                buttonBar.add(btnCancel);

                //---- btnOK ----
                btnOK.setText(null);
                btnOK.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                btnOK.setHorizontalAlignment(SwingConstants.RIGHT);
                btnOK.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnOKActionPerformed(e);
                    }
                });
                buttonBar.add(btnOK);
            }
            dialogPane.add(buttonBar, CC.xy(1, 3, CC.RIGHT, CC.DEFAULT));
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(900, 600);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel buttonBar;
    private JButton btnCancel;
    private JButton btnOK;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    /**
     * createPanel erstellt anhand zweier XML Dokumente (eins für die Struktur, eins für den Inhalt) ein JPanel mit den passenden Eingabe Elementen.
     * Die Struktur bestimmt, welche Swing Elemente verwendet werden. Der Inhalt setzt die Swing Widget entsprechend. Ob RadioButtons gedrückt sind,
     * was in Textfeldern steht, welcher Listeneintrag ausgewählt wurde. etc...
     */
    private JPanel getPanel() {
        JPanel jp = new JPanel();
        initPanel = true;

        // Struktur...
        try {
            String xmltext = "<?xml version=\"1.0\"?><structure>" + bwInfo.getBwinfotyp().getXml() + "</structure>";
            XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
            InputSource is = new org.xml.sax.InputSource(new java.io.BufferedReader(new java.io.StringReader(xmltext)));

            HandlerDatenStruktur h = new HandlerDatenStruktur();
            parser.setContentHandler(h);

            parser.parse(is);

            jp = h.getPanel();

        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }


        // ... und Inhalt
        setContent();

        initPanel = false;
        return jp;
    }

    private void calcScale() {
        if (!scalemode) return;

        BigDecimal scalesum = BigDecimal.ZERO;

        for (String bgName : scaleButtonGroups) {
            OPDE.debug(components.toString());
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
            for (RiskBean risk : scaleriskmodel) {
                if (risk.getFrom().compareTo(scalesum) <= 0 && scalesum.compareTo(risk.getTo()) <= 0) {
                    color = risk.getColor();
                    risiko = risk.getLabel();
                    break;
                }
            }
            sumlabel.setText(scalesumlabeltext + ": " + scalesum + " (" + risiko + ")");
            sumlabel.setForeground(SYSTools.getColor(color));
        }
    }


    private void setContent() {
        try {
            StringReader reader = new StringReader(bwInfo.getProperties());
            content.load(reader);
            reader.close();
        } catch (IOException ex) {
            OPDE.fatal(ex);
        }

        OPDE.debug(content.toString());

        for (Object key : components.keySet()) {
            Object entry = components.get(key);

            if (entry instanceof JRadioButton) {
                StringTokenizer st = new StringTokenizer(key.toString(), ":");
                String tagname = st.nextToken();
                String value = st.nextToken();
//                OPDE.debug("key: " + key.toString());
//                OPDE.debug("componentname: " + componentname);
                ((JRadioButton) entry).setSelected(content.containsKey(tagname) && content.getProperty(tagname).equals(value));
            } else if (entry instanceof Pair) { // Scale
                StringTokenizer st = new StringTokenizer(key.toString(), ":");
                String tagname = st.nextToken();
                String value = st.nextToken();
//                OPDE.debug("key: " + key.toString());
//                OPDE.debug("componentname: " + componentname);
                ((Pair<JRadioButton, BigDecimal>) entry).getFirst().setSelected(content.getProperty(tagname).equals(value));
            } else if (entry instanceof JCheckBox) {
                ((JCheckBox) entry).setSelected(content.getProperty(key.toString()).equalsIgnoreCase("true"));
            } else if (entry instanceof JTextField) {
                ((JTextField) entry).setText(SYSTools.unescapeXML(content.getProperty(key.toString())));
            } else if (entry instanceof JComboBox) {
                SYSTools.selectInComboBox((JComboBox) entry, content.getProperty(key.toString()));
//                 ((JComboBox) entry), content.getProperty(key.toString())
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
        }
    }

    private class CheckBoxActionListener implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JCheckBox j = (JCheckBox) evt.getSource();
            String cbname = j.getName();
            content.put(cbname, (j.isSelected() ? "true" : "false"));
        }
    }

    private class ComboBoxItemStateListener implements ItemListener {

        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            JComboBox j = (JComboBox) evt.getSource();
            String cmbname = j.getName();
            ComboBoxModel cbm = j.getModel();
            ListElement le = (ListElement) cbm.getSelectedItem();
            content.put(cmbname, le.getData());
        }
    }

    private class TextFieldFocusListener implements FocusListener {
        int type = TYPE_DONT_CARE;

        TextFieldFocusListener(int type) {
            this.type = type;
        }

        public void focusGained(FocusEvent e) {
            SYSTools.markAllTxt((JTextField) e.getSource());
        }

        public void focusLost(FocusEvent e) {
            JTextField j = (JTextField) e.getSource();
            String text = ((JTextField) e.getSource()).getText();

            if (type != TYPE_DONT_CARE) {
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
            content.put(j.getName(), SYSTools.escapeXML(j.getText()));
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
     * erstellten Widgets, der Zugriff erfolgt über das <code>name</code> Attribut aus der XML Struktur. So dass man, gemäß des obigen Beispiels unter 1.), über
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
                    outerpanel.add("p left", jl);
                }
            }
            if (tagName.equalsIgnoreCase("scale")) {
                scalemode = true;
                scalesumlabeltext = attributes.getValue("label");
                scaleButtonGroups = new ArrayList();
                scaleriskmodel = new ArrayList();
            }
            if (tagName.equalsIgnoreCase("risk")) {
                scaleriskmodel.add(new RiskBean(attributes.getValue("from"), attributes.getValue("to"), attributes.getValue("label"), attributes.getValue("color")));
            }
            if (tagName.equalsIgnoreCase("tabgroup")) {
                JLabel jl = new JLabel(attributes.getValue("label") + ":");
                if (!SYSTools.catchNull(attributes.getValue("color")).isEmpty()) {
                    jl.setForeground(SYSTools.getColor(attributes.getValue("color")));
                }
                if (!SYSTools.catchNull(attributes.getValue("size")).isEmpty()) {
//                    int size = Integer.parseInt(attributes.getValue("size"));
                    jl.setFont(SYSConst.ARIAL14BOLD);
                }
                jl.setToolTipText(SYSTools.toHTML(SYSTools.catchNull(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')));
                outerpanel.add("p left", jl);
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
            // ---------------------- CHECKBOXES --------------------------------
            if (tagName.equalsIgnoreCase("checkbox")) {
                groupname = attributes.getValue("name");
                JCheckBox j = new JCheckBox(attributes.getValue("label"));
                j.setToolTipText(SYSTools.toHTML(SYSTools.catchNull(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')));
                String layout = attributes.getValue("layout");
                if (SYSTools.catchNull(layout).isEmpty()) {
                    layout = "tab";
                }
                j.setName(groupname);
                outerpanel.add(j);
                components.put(groupname, j); // für den späteren Direktzugriff
                j.addActionListener(new CheckBoxActionListener());
                if (tabgroup) {
                    outerpanel.add(layout, j);
                } else {
                    outerpanel.add("p left", j);
                }
                if (attributes.getValue("default") != null && attributes.getValue("default").equals("true")) {
                    j.setSelected(true);
                }
                content.put(groupname, (j.isSelected() ? "true" : "false"));
            }
            // ---------------------- TEXTFELDER --------------------------------
            if (tagName.equalsIgnoreCase("textfield")) {
                groupname = attributes.getValue("name");
                // Hiermit kann man den Datentyp des Textfeldes erzwingen.
                // Der Caretlistener sorgt für den Rest.
                int type = TYPE_DONT_CARE;
                if (SYSTools.catchNull(attributes.getValue("type")).equals("int")) {
                    type = TYPE_INT;
                }
                if (SYSTools.catchNull(attributes.getValue("type")).equals("double")) {
                    type = TYPE_DOUBLE;
                }
//
//                boolean notempty = SYSTools.catchNull(attributes.getValue("notempty")).equals("true");

                JLabel jl = new JLabel(attributes.getValue("label") + ":");
                JTextField j = new JTextField(TEXTFIELD_STANDARD_WIDTH);
                j.setName(groupname);
                j.setToolTipText(SYSTools.toHTML(SYSTools.catchNull(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')));
                outerpanel.add("p left", jl);
                outerpanel.add("tab hfill", j);
                components.put(groupname, j); // für den späteren Direktzugriff
                j.addFocusListener(new TextFieldFocusListener(type));
//                j.addCaretListener(new TextFieldCaretListener(type, notempty));
                String defaultText = SYSTools.catchNull(attributes.getValue("default"));
                j.setText(defaultText);
                content.put(groupname, defaultText);
            }
            // ---------------------- Trenner --------------------------------
            if (tagName.equalsIgnoreCase("separator")) {
                //groupname = attributes.getValue("name");
                //JLabel jl = new JLabel(new javax.swing.ImageIcon(getClass().getResource(attributes.getValue("image"))));
                outerpanel.add("p hfill", new JSeparator());
            }
            // ---------------------- Bildlabels --------------------------------
            if (tagName.equalsIgnoreCase("imagelabel")) {
                groupname = attributes.getValue("name");
                JLabel jl = new JLabel(new javax.swing.ImageIcon(getClass().getResource(attributes.getValue("image"))));
                jl.setToolTipText(SYSTools.toHTML(SYSTools.catchNull(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')));
                outerpanel.add("p left", jl);
            }
            // ---------------------- Textlabels --------------------------------
            if (tagName.equalsIgnoreCase("label")) {
                groupname = attributes.getValue("name");
                JLabel jl = new JLabel(attributes.getValue("label"));
                if (!SYSTools.catchNull(attributes.getValue("color")).isEmpty()) {
                    jl.setForeground(SYSTools.getColor(attributes.getValue("color")));
                }
                if (!SYSTools.catchNull(attributes.getValue("size")).isEmpty()) {
//                    int size = Integer.parseInt(attributes.getValue("size"));
                    jl.setFont(SYSConst.ARIAL14BOLD);
                }
                jl.setToolTipText(SYSTools.toHTML(SYSTools.catchNull(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')));
                outerpanel.add("p left", jl);
            }
            // ---------------------- Comboboxen --------------------------------
            if (tagName.equalsIgnoreCase("combobox")) {
                groupname = attributes.getValue("name");
                boxModel = new DefaultComboBoxModel();
                //itemNum = 0;
                JComboBox jcb = new JComboBox();
                jcb.setName(groupname);
                jcb.setToolTipText(SYSTools.toHTML(SYSTools.catchNull(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')));
                components.put(groupname, jcb);
                jcb.addItemListener(new ComboBoxItemStateListener());
                JLabel jl = new JLabel(attributes.getValue("label") + ":");
                outerpanel.add("p left", jl);
                outerpanel.add("tab hfill", jcb);

//                if (!SYSTools.catchNull(attributes.getValue("help")).isEmpty()) {
//
//                    try {
//                        JButton jb = new JButton("Hilfe");
//                        final URI uri = new URI(attributes.getValue("help"));
//                        jb.addActionListener(new java.awt.event.ActionListener() {
//
//                            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                                if (Desktop.isDesktopSupported()) {
//                                    Desktop desktop = Desktop.getDesktop();
//                                    try {
//                                        desktop.browse(uri);
//                                    } catch (IOException iOException) {
//                                    }
//                                }
//                            }
//                        });
//                        outerpanel.add(jb);
//                    } catch (URISyntaxException uRISyntaxException) {
//                    }
//
//                }
            }
            if (tagName.equalsIgnoreCase("item")) {
                //itemNum++;
                boxModel.addElement(new ListElement(attributes.getValue("label"), attributes.getValue("name")));
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
                ListElement le = (ListElement) j.getSelectedItem();
                // Hier muss unterschieden werden, ob der PK ein Long oder ein String ist.
                if (le.getPk() <= 0) {
                    content.put(j.getName(), le.getData());
                } else {
                    content.put(j.getName(), Long.toString(le.getPk()));
                }
                boxModel = null;
            }
        }

        public void endDocument() {
        }

        public JPanel getPanel() {
            return this.outerpanel;
        }
    } // private class HandlerDatenStruktur

}
