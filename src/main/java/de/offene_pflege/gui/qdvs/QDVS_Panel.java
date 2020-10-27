/*
 * Created by JFormDesigner on Wed Feb 12 14:31:57 CET 2020
 */

package de.offene_pflege.gui.qdvs;

import com.jidesoft.combobox.DateExComboBox;
import com.jidesoft.combobox.FolderChooserComboBox;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.swing.JideBoxLayout;
import de.offene_pflege.entity.building.Homes;
import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.entity.info.ResidentTools;
import de.offene_pflege.entity.system.SYSPropsTools;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.gui.events.AddTextListener;
import de.offene_pflege.gui.interfaces.CleanablePanel;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.HasLogger;
import de.offene_pflege.op.tools.JavaTimeConverter;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import de.offene_pflege.services.HomesService;
import de.offene_pflege.services.qdvs.DAS_REGELN;
import de.offene_pflege.services.qdvs.MyErrorHandler;
import de.offene_pflege.services.qdvs.QSData;
import de.offene_pflege.services.qdvs.QdvsService;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.jdesktop.swingx.VerticalLayout;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

/**
 * @author Torsten Löhr
 */
public class QDVS_Panel extends CleanablePanel implements HasLogger, AddTextListener {
    private final JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private JTree tblResidents;
    LocalDate STICHTAG, LETZTE_ERFASSUNG, ERHEBUNG;
    Homes home;
    File workdir;
    QdvsService qdvsService;
    private List<Resident> liste_bewohner;
    private File target;
    private JSplitPane content;
    private JPanel right;
    private JTextArea txtLog;
    private HashMap<String, DAS_REGELN> REGELN;
    MultiKeyMap<MultiKey<Integer>, ArrayList<String>> ERRORS;
    MultiKeyMap<MultiKey<Integer>, Long> LOOKUP;
    boolean vorpruefungOK;

    /**
     * Diese Klasse erzeugt das Panel für die QDVS Auswertung
     *
     * @param jspSearch
     */
    public QDVS_Panel(JScrollPane jspSearch) {
        super("de.offene_pflege.gui.qdvs");
        this.jspSearch = jspSearch;
        qdvsService = new QdvsService(this);
        initPanel();
    }

    void initPanel() {
        right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.PAGE_AXIS));
        txtLog = new JTextArea();
        txtLog.setLineWrap(true);
        right.add(new JScrollPane(txtLog));
//        right.add(new JButton("Something"));

        ERRORS = new MultiKeyMap<>();
        LOOKUP = new MultiKeyMap<>();

        if (OPDE.getLocalProps().containsKey(SYSPropsTools.KEY_QDVS_STICHTAG))
            STICHTAG = JavaTimeConverter.from_iso8601(OPDE.getLocalProps().getProperty(SYSPropsTools.KEY_QDVS_STICHTAG)).toLocalDate();
        else
            STICHTAG = LocalDate.now();

        if (OPDE.getLocalProps().containsKey(SYSPropsTools.KEY_QDVS_LETZTER_STICHTAG))
            LETZTE_ERFASSUNG = JavaTimeConverter.from_iso8601(OPDE.getLocalProps().getProperty(SYSPropsTools.KEY_QDVS_LETZTER_STICHTAG)).toLocalDate();
        else
            LETZTE_ERFASSUNG = STICHTAG.minusMonths(6);

        if (OPDE.getLocalProps().containsKey(SYSPropsTools.KEY_QDVS_ERHEBUNG))
            ERHEBUNG = JavaTimeConverter.from_iso8601(OPDE.getLocalProps().getProperty(SYSPropsTools.KEY_QDVS_ERHEBUNG)).toLocalDate();
        else
            ERHEBUNG = LocalDate.now();


        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        tblResidents = new JTree();
        tblResidents.setRootVisible(false);
        tblResidents.setCellRenderer(new DelegateDefaultCellRenderer());

        content = new JSplitPane();
        content.setLeftComponent(new JScrollPane(tblResidents));
        content.setRightComponent(right);
        content.setDividerLocation(0.5d);
        content.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        add(content);

        workdir = new File(OPDE.getLocalProps().getProperty(SYSPropsTools.KEY_QDVS_WORKPATH, System.getProperty("user.home")));
        if (!workdir.exists()) workdir = new File(System.getProperty("user.home"));
        OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_QDVS_WORKPATH, workdir.getAbsolutePath());
        prepareSearchArea();

        // Einmal die Lookup-Regeln lesen
        File path = new File(workdir, home.getCareproviderid() + File.separator);
        File csv = new File(path, "DAS_Plausibilitaetsregeln.csv");
        REGELN = lese_DAS_REGELN(csv);

        prepareListOFResidents(); // einmal am Anfang, damit die Liste der BW ausgefüllt ist
    }


    DefaultMutableTreeNode createTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("ROOT");
        HashMap<Long, TreeInfoNode> map_zur_zuordnung_der_fehler = new HashMap<>();
        liste_bewohner.forEach(resident -> {
            map_zur_zuordnung_der_fehler.put(resident.getIdbewohner(), new TreeInfoNode(resident));
            root.add(map_zur_zuordnung_der_fehler.get(resident.getIdbewohner()));
        });
        if (vorpruefungOK) {
            // Das hier matched die ERRORS (zeile, spalte des fehlers - closing tag) zu den Bewohnern (ebenfalls zeile, spalte des closing tags)
            ERRORS.forEach((multiKey, strings) -> {
                long idbewohner = LOOKUP.get(multiKey);
                strings.forEach(s -> {
                    String regel = s;
                    if (REGELN.containsKey(s)) {
                        regel = REGELN.get(s).getRule_id() + ": " + REGELN.get(s).getRule_text();
                    }
                    map_zur_zuordnung_der_fehler.get(idbewohner).add(new TreeInfoNode(regel, TreeInfoNode.SELF_FOUND_ERROR));
                });
            });
        } else {
            qdvsService.getResidentInfoObjectMap().forEach((resident, qdvsResidentInfoObject) -> {
                TreeInfoNode node = map_zur_zuordnung_der_fehler.get(resident.getIdbewohner());
                qdvsResidentInfoObject.getFehler().forEach(fehler -> {
                    node.add(new TreeInfoNode(fehler, TreeInfoNode.VORPRUEFUNG_FEHLER));
                    node.setType(TreeInfoNode.RESIDENT_RED);
                });
            });
        }
        return root;
    }


    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout(3));
        mypanel.setBackground(Color.WHITE);

        CollapsiblePane searchPane = new CollapsiblePane(SYSTools.xx(internalClassID));
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);

        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }

        GUITools.addAllComponents(mypanel, addCommands());
//           GUITools.addAllComponents(mypanel, addFilters());


        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();
    }


    void ergebniserfasung() {

        OPDE.getMainframe().setBlockedTransparent(true);

        // Die Auswertung kommt immer in ein eigenes Verzeichnis.
        String dir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        File path = new File(workdir, home.getCareproviderid() + File.separator + dir + File.separator);
        target = new File(path, "qs-data.xml");

        qdvsService.setParameters(STICHTAG, ERHEBUNG, LETZTE_ERFASSUNG, home, liste_bewohner, target);

        ERRORS.clear();
        LOOKUP.clear();

        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                vorpruefungOK = qdvsService.ergebniserfassung();
                if (vorpruefungOK) {
                    addLog("qdvs.plausibilitaet.pruefen");

                    File path = new File(workdir, home.getCareproviderid() + File.separator);
                    File xsd = new File(path, "interface_qs_data/das_interface.xsd");

                    ERRORS = validateFile(target, xsd); // welche Fehler
                    LOOKUP = getLookupTable(target); // welcher Bewohner

                    addLog("qdvs.erfassung.abgeschlossen");
                }

                return null;
            }

            @Override
            protected void done() {
                DefaultMutableTreeNode root = createTree();
                tblResidents.setModel(new DefaultTreeModel(root));
                try {
                    FileUtils.writeStringToFile(new File(target.getParentFile(), "ergebnis.html"), toHTML(root));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                OPDE.getMainframe().setBlockedTransparent(false);
            }
        };
        worker.execute();

    }

    /**
     * bei jeder Änderung der Auswahlen für Zeitraum, Einrichtung usw. werden einmal die Parameter dem QDVS Service
     * mitgeteilt.
     */
    void prepareListOFResidents() {
        getLogger().debug("setParameters()");


//        try {
//            // copy xsd from resources
//            final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
//            if (jarFile.isFile()) getLogger().info("JARFILE" + jarFile.toString());
//
//            String strResourcePath = "/das-pflege/v01.3";
//            URL inputUrl = getClass().getResource(strResourcePath);
////            final File apps = new File(inputUrl.toURI());
//            Files.walk(Paths.get(inputUrl.toURI()))
//                    .filter(Files::isRegularFile)
//                    .forEach(path1 -> {
//                        try {
//                            getLogger().debug(path1);
//
//                            FileUtils.copyFileToDirectory(path1.toFile(), path.getParentFile());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    });
//
////            File dest = new File("/path/to/destination/file");
//
////            FileUtils.copyURLToFile(inputUrl, dest);
//        } catch (URISyntaxException | IOException e) {
//            getLogger().error(e);
//            // bad luck
//        }
        liste_bewohner = ResidentTools.getAll(home, STICHTAG.atTime(23, 59, 59));

        tblResidents.setModel(new DefaultTreeModel(createTree()));
    }


    private java.util.List<Component> addCommands() {

        FolderChooserComboBox fcWorkdir = new FolderChooserComboBox();
        fcWorkdir.setSelectedItem(workdir);
        fcWorkdir.addPropertyChangeListener("selectedItem", evt -> {
            OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_QDVS_WORKPATH, evt.getNewValue().toString());
            workdir = (File) evt.getNewValue();
            prepareListOFResidents();
        });

        final DateExComboBox dcmbLetzte = new DateExComboBox(); // wann zuletzt eine Erhebung durchgeführt wurde
        final DateExComboBox dcmbStichtag = new DateExComboBox(); // welcher Stichtag verwendet werden soll.
        final DateExComboBox dcmbErhebung = new DateExComboBox(); // Bis zu welchem Datum die ResInfos verwendet werden. Das ist meist das aktuelle Datum, aber man kann es einstellen. Ist wichtig, wenn man nach einer Fehlermeldung durch DAS-PFLEGE Korrekturen vornimmt.
        dcmbStichtag.setShowNoneButton(false);
        dcmbStichtag.setInvalidValueAllowed(false);
        dcmbStichtag.getDateModel().setMaxDate(new GregorianCalendar());
        dcmbStichtag.setDate(JavaTimeConverter.toDate(STICHTAG));
        dcmbStichtag.addPropertyChangeListener("selectedItem", evt -> {
            GregorianCalendar gc = (GregorianCalendar) evt.getNewValue();
            OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_QDVS_STICHTAG, JavaTimeConverter.to_iso8601(gc));
            STICHTAG = JavaTimeConverter.toJavaLocalDateTime(gc.getTime()).toLocalDate();
            prepareListOFResidents();
        });

        dcmbLetzte.setShowNoneButton(false);
        dcmbLetzte.setInvalidValueAllowed(false);
        dcmbLetzte.getDateModel().setMaxDate(new GregorianCalendar());
        dcmbLetzte.setDate(JavaTimeConverter.toDate(LETZTE_ERFASSUNG));
        dcmbLetzte.addPropertyChangeListener("selectedItem", evt -> {
            GregorianCalendar gc = (GregorianCalendar) evt.getNewValue();
            OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_QDVS_LETZTER_STICHTAG, JavaTimeConverter.to_iso8601(gc));
            LETZTE_ERFASSUNG = JavaTimeConverter.toJavaLocalDateTime(gc.getTime()).toLocalDate();
        });

        dcmbErhebung.setShowNoneButton(false);
        dcmbErhebung.setInvalidValueAllowed(false);
        dcmbErhebung.getDateModel().setMaxDate(new GregorianCalendar());
        dcmbErhebung.setDate(JavaTimeConverter.toDate(STICHTAG));
        dcmbErhebung.addPropertyChangeListener("selectedItem", evt -> {
            GregorianCalendar gc = (GregorianCalendar) evt.getNewValue();
            OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_QDVS_ERHEBUNG, JavaTimeConverter.to_iso8601(gc));
            ERHEBUNG = JavaTimeConverter.toJavaLocalDateTime(gc.getTime()).toLocalDate();
        });

        JComboBox<Homes> cmbHome = new JComboBox<>();
        HomesService.setComboBox(cmbHome);
        home = (Homes) cmbHome.getSelectedItem();
        cmbHome.addItemListener(e -> {
            home = (Homes) e.getItem();
            prepareListOFResidents();
        });


        java.util.List<Component> list = new ArrayList();
        list.add(cmbHome);
        list.add(new JLabel(SYSTools.xx("qdvs.stichtag")));
        list.add(dcmbStichtag);
        list.add(new JLabel(SYSTools.xx("qdvs.erhebungsdatum")));
        list.add(dcmbErhebung);
        list.add(new JLabel(SYSTools.xx("qdvs.letzte.erhebung")));
        list.add(dcmbLetzte);
        list.add(new JLabel(SYSTools.xx("qdvs.workdir")));
        list.add(fcWorkdir);
        list.add(new JSeparator());
        list.add(GUITools.createHyperlinkButton(SYSTools.xx("qdvs.ergebniserfassung"), SYSConst.icon22exec, e -> {
            ergebniserfasung();
        }));


        return list;
    }

    /**
     * Die Klartext-Darstellung der Fehlermeldungen steht in einer CSV Datei. Die muss ich hier einlesen, damit ich da
     * nachher drauf zugreifen kann.
     *
     * @param csv
     * @return
     */
    private HashMap<String, DAS_REGELN> lese_DAS_REGELN(File csv) {
        HashMap<String, DAS_REGELN> regeln = new HashMap<>();

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(csv));

            for (CSVRecord record : CSVFormat.EXCEL.withDelimiter(';').parse(reader)) {
                try {
                    DAS_REGELN das_regeln = new DAS_REGELN(record.get(0), Integer.valueOf(record.get(1)), record.get(2), record.get(3), record.get(4));
                    regeln.put(das_regeln.getAssert_test(), das_regeln);
                } catch (NumberFormatException nfe) {
                    // egal
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return regeln;

    }

    // erzeugt eine Multikey Map
    // der key ist ein Paar aus zeile und spalte des closing tags der zu dem Fehler gehört
    // der value ist eine Liste aller Fehler die dazu gehören
    // diese Fehlerliste lässt sich mit den Regeln der CSV Datei Matchen um die restlichen Daten zu den Regeln zu erhalten.
    private MultiKeyMap<MultiKey<Integer>, ArrayList<String>> validateFile(File xmlFile, File xsdFile) throws SAXException, IOException, XMLStreamException {
        // 1. Lookup a factory for the W3C XML Schema language
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/XML/XMLSchema/v1.1");
//                factory.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.VALIDATE_ANNOTATIONS_FEATURE, true);

        // 2. Compile the schema.
        File schemaLocation = xsdFile;
        Schema schema = factory.newSchema(schemaLocation);

        // 3. Get a validator from the schema.
        Validator validator = schema.newValidator();

        // 4. Parse the document you want to check.
        Source source = new StreamSource(xmlFile);
        XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(source);
        MyErrorHandler myErrorHandler = new MyErrorHandler(reader);

        // set my own ErrorHandler
        validator.setErrorHandler(myErrorHandler);

        // 5. Check the document
        try {
            validator.validate(new StAXSource(reader));
            System.out.println(xmlFile.getName() + " is valid.");
        } catch (SAXException ex) {
            System.out.println(xmlFile.getName() + " is not valid because ");
            System.out.println(ex.getMessage());
        }

        return myErrorHandler.getERRORS();

    }

    /**
     * Erzeugt einen Lookup Table der zu jedem zeile, spalte paar die idbewohner enthält. Das ist ein Trick, weil ich
     * das nicht direkt aus dem Parser rauslesen kann. Ich muss ja die Fehler dem BW zuordnen können.
     *
     * @param xmlFile
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    private MultiKeyMap<MultiKey<Integer>, Long> getLookupTable(File xmlFile) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        SAXParser parser = parserFactory.newSAXParser();
        XMLReader reader = parser.getXMLReader();
        Source source = new StreamSource(xmlFile);
        QSData struktur = new QSData();
        reader.setContentHandler(struktur);
        reader.parse(new InputSource(source.getSystemId()));


        return struktur.getLookup();
    }

    String toHTML(DefaultMutableTreeNode node) {
//        String html = tree.isRoot() ? "ROOT" : tree.getUserObject();
        String html = "";
        final StringBuffer buffer = new StringBuffer();

        if (node instanceof TreeInfoNode) {

            TreeInfoNode tnode = (TreeInfoNode) node;

            if (tnode.getType() <= TreeInfoNode.RESIDENT_RED) { // mieser Trick. :-D
                String resident = QdvsService.toString((Resident) tnode.getUserObject());
                Collections.list(tnode.children()).forEach(treeNode -> buffer.append(toHTML((DefaultMutableTreeNode) treeNode)));
                if (buffer.length() > 0)
                    html = SYSConst.html_ul(SYSConst.html_li(resident + SYSConst.html_ul(buffer.toString())));
                else html = SYSConst.html_ul(SYSConst.html_li(resident));
            } else if (tnode.getType() == TreeInfoNode.SELF_FOUND_ERROR) {
                html = SYSConst.html_li(tnode.getUserObject().toString());
            } else if (tnode.getType() == TreeInfoNode.ERROR_BY_DAS) {
                html = SYSConst.html_li("DAS: " + tnode.getUserObject().toString());
            } else if (tnode.getType() == TreeInfoNode.VORPRUEFUNG_FEHLER) {
                html = SYSConst.html_li(tnode.getUserObject().toString());
            }
        } else {
            Collections.list(node.children()).forEach(treeNode -> buffer.append(toHTML((DefaultMutableTreeNode) treeNode)));
            html = buffer.toString();
        }

        return html;

    }

    @Override
    public void addLog(String log) {
        getLogger().debug(SYSTools.xx(log));
        SwingUtilities.invokeLater(() -> {
            txtLog.append(SYSTools.xx(log) + "\n");
        });

    }

    private class TreeInfoNode extends DefaultMutableTreeNode {
        public static final int RESIDENT_GREY = 0;
        public static final int RESIDENT_GREEN = 1;
        public static final int RESIDENT_YELLOW = 2;
        public static final int RESIDENT_RED = 3;
        public static final int SELF_FOUND_ERROR = 4;
        public static final int ERROR_BY_DAS = 5;
        public static final int VORPRUEFUNG_FEHLER = 6;
        private int type = 0;

        public TreeInfoNode(Resident resident) {
            setUserObject(resident);
            type = RESIDENT_GREY;
        }

        public TreeInfoNode(String error, int type) {
            setUserObject(error);
            this.type = type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

//    private class TreeRenderer extends DefaultTreeCellRenderer {
//        TreeRenderer() {
//            super();
//        }
//
//        @Override
//        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
//
//
//            JLabel component = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
//            component.setText("empty");
//
//            if (value instanceof TreeInfoNode) {
//                TreeInfoNode node = (TreeInfoNode) value;
//                Object userObject = node.getUserObject();
//
//                if (node.getType() == TreeInfoNode.RESIDENT)
//                    component.setText(QdvsService.toString((Resident) userObject));
//                if (node.getType() == TreeInfoNode.SELF_FOUND_ERROR) {
//                    component.setText(SYSTools.toHTMLForScreen(SYSConst.html_paragraph(userObject.toString())));
//                }
//                if (node.getType() == TreeInfoNode.ERROR_BY_DAS) component.setText(userObject.toString());
//            }
//            return component;
//        }
//
//
//    }

    class DelegateDefaultCellRenderer extends DefaultTreeCellRenderer {
        TextAreaRenderer taRenderer = new TextAreaRenderer();

        public DelegateDefaultCellRenderer() {
            taRenderer.setBackgroundNonSelectionColor(getBackgroundNonSelectionColor());
            taRenderer.setBackgroundSelectionColor(getBackgroundSelectionColor());
            taRenderer.setTextNonSelectionColor(getTextNonSelectionColor());
            taRenderer.setTextSelectionColor(getTextSelectionColor());
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected, boolean expanded, boolean leaf,
                                                      int row, boolean hasFocus) {

            if (!(value instanceof TreeInfoNode)) return new JLabel();

            TreeInfoNode node = (TreeInfoNode) value;
            Object userObject = node.getUserObject();

            if (node.getType() <= TreeInfoNode.RESIDENT_RED) {

                Icon icon = SYSConst.findIcon("/artwork/12x12/person-green.png");
                if (node.getType() == TreeInfoNode.RESIDENT_RED)
                    icon = SYSConst.findIcon("/artwork/12x12/person-red.png");
                if (node.getType() == TreeInfoNode.RESIDENT_YELLOW)
                    icon = SYSConst.findIcon("/artwork/12x12/person-yellow.png");
                if (node.getType() == TreeInfoNode.RESIDENT_GREY)
                    icon = SYSConst.findIcon("/artwork/12x12/person-grey.png");

                JLabel lbl = (JLabel) super.getTreeCellRendererComponent(tree, value, selected,
                        expanded, leaf, row, hasFocus);
                lbl.setIcon(icon);
                return lbl;
            } else {
                return taRenderer.getTreeCellRendererComponent(tree, value, selected,
                        expanded, leaf, row, hasFocus);
            }
        }
    }

    class TextAreaRenderer extends JPanel implements TreeCellRenderer {
        JTextArea textarea;
        Color backgroundNonSelectionColor;
        Color backgroundSelectionColor;
        Color textNonSelectionColor;
        Color textSelectionColor;

        public TextAreaRenderer() {
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            textarea = new JTextArea();
            textarea.setLineWrap(true);
            textarea.setWrapStyleWord(true);
            add(textarea);
        }

        public void setBackgroundNonSelectionColor(Color c) {
            this.backgroundNonSelectionColor = c;
        }

        public void setBackgroundSelectionColor(Color c) {
            this.backgroundSelectionColor = c;
        }

        public void setTextNonSelectionColor(Color c) {
            this.textNonSelectionColor = c;
        }

        public void setTextSelectionColor(Color c) {
            this.textSelectionColor = c;
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected, boolean expanded, boolean leaf,
                                                      int row, boolean hasFocus) {

            if (selected) {
                setForeground(textSelectionColor);
                setBackground(backgroundSelectionColor);
                textarea.setForeground(textSelectionColor);
                textarea.setBackground(backgroundSelectionColor);
            } else {
                setForeground(textNonSelectionColor);
                setBackground(backgroundNonSelectionColor);
                textarea.setForeground(textNonSelectionColor);
                textarea.setBackground(backgroundNonSelectionColor);
            }


            if (value instanceof TreeInfoNode) {
                TreeInfoNode node = (TreeInfoNode) value;
                Object userObject = node.getUserObject();

                if (node.getType() <= TreeInfoNode.RESIDENT_RED) {
                    textarea.setText(QdvsService.toString((Resident) userObject));
                }
                if (node.getType() == TreeInfoNode.SELF_FOUND_ERROR) {
                    textarea.setText(userObject.toString());
                }
                if (node.getType() == TreeInfoNode.VORPRUEFUNG_FEHLER) {
                    textarea.setText(userObject.toString());
                }
                if (node.getType() == TreeInfoNode.ERROR_BY_DAS) {
                    textarea.setText(userObject.toString());
                }
            } else {
                textarea.setText("empty");
            }

            return this;
        }
    }


}
