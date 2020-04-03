/*
 * Created by JFormDesigner on Wed Feb 12 14:31:57 CET 2020
 */

package de.offene_pflege.gui.qdvs;

import com.jidesoft.combobox.DateExComboBox;
import com.jidesoft.combobox.FolderChooserComboBox;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.swing.JideBoxLayout;
import de.offene_pflege.backend.entity.done.Homes;
import de.offene_pflege.backend.services.HomesService;
import de.offene_pflege.backend.entity.done.Resident;
import de.offene_pflege.backend.services.ResidentTools;
import de.offene_pflege.backend.entity.system.SYSPropsTools;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.gui.events.AddTextListener;
import de.offene_pflege.gui.interfaces.CleanablePanel;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.HasLogger;
import de.offene_pflege.op.tools.JavaTimeConverter;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import de.offene_pflege.services.qdvs.DAS_REGELN;
import de.offene_pflege.services.qdvs.MyErrorHandler;
import de.offene_pflege.services.qdvs.QSData;
import de.offene_pflege.services.qdvs.QdvsService;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
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
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

/**
 * @author Torsten Löhr
 */
public class QDVS_Panel extends CleanablePanel implements HasLogger, AddTextListener {
    private final JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private JTree tblResidents;
    LocalDate STICHTAG, LETZTE_ERFASSUNG;
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
        right.add(new JScrollPane(txtLog));
        right.add(new JButton("Something"));

        ERRORS = new MultiKeyMap<>();
        LOOKUP = new MultiKeyMap<>();

        STICHTAG = LocalDate.now();
        LETZTE_ERFASSUNG = STICHTAG.minusMonths(6);
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


        setParameters(); // einmal am Anfang, damit die Liste der BW ausgefüllt ist
    }


    DefaultMutableTreeNode createTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("ROOT");
        HashMap<Long, TreeInfoNode> map_zur_zuordnung_der_fehler = new HashMap<>();
        liste_bewohner.forEach(resident -> {
            map_zur_zuordnung_der_fehler.put(resident.getIdbewohner(), new TreeInfoNode(resident));
            root.add(map_zur_zuordnung_der_fehler.get(resident.getIdbewohner()));
        });

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

        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                qdvsService.ergebniserfassung();

                addLog("qdvs.plausibilitaet.pruefen");

                File path = new File(workdir, home.getCareproviderid() + File.separator);
                File xsd = new File(path, "interface_qs_data/das_interface.xsd");

                ERRORS = validateFile(target, xsd); // welche Fehler
                LOOKUP = getLookupTable(target); // welcher Bewohner

                tblResidents.setModel(new DefaultTreeModel(createTree()));

                addLog("qdvs.erfassung.abgeschlossen");

                return null;
            }

            @Override
            protected void done() {
                tblResidents.setModel(new DefaultTreeModel(createTree()));
                OPDE.getMainframe().setBlockedTransparent(false);
            }
        };
        worker.execute();

    }

    /**
     * bei jeder Änderung der Auswahlen für Zeitraum, Einrihtung usw. werden einmal die Parameter dem QDVS Service
     * mitgeteilt.
     */
    void setParameters() {
        getLogger().debug("setParameters()");
        String dir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        File path = new File(workdir, home.getCareproviderid() + File.separator + dir + File.separator);
        target = new File(path, "qs-data.xml");

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
        qdvsService.setParameters(STICHTAG, LocalDate.now(), LETZTE_ERFASSUNG, home, liste_bewohner, target);
        ERRORS.clear();
        LOOKUP.clear();
        tblResidents.setModel(new DefaultTreeModel(createTree()));
    }


    private java.util.List<Component> addCommands() {
        FolderChooserComboBox fcWorkdir = new FolderChooserComboBox();
        fcWorkdir.setSelectedItem(workdir);
        fcWorkdir.addPropertyChangeListener("selectedItem", evt -> {
            OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_QDVS_WORKPATH, evt.getNewValue().toString());
            workdir = (File) evt.getNewValue();
            setParameters();
        });

        final DateExComboBox dcmbLetzte = new DateExComboBox();
        final DateExComboBox dcmbStichtag = new DateExComboBox();
        dcmbStichtag.setShowNoneButton(false);
        dcmbStichtag.setInvalidValueAllowed(false);
        dcmbStichtag.getDateModel().setMaxDate(new GregorianCalendar());
        dcmbStichtag.setDate(JavaTimeConverter.toDate(STICHTAG));
        dcmbStichtag.addPropertyChangeListener("selectedItem", evt -> {
            STICHTAG = JavaTimeConverter.toJavaLocalDateTime(((GregorianCalendar) evt.getNewValue()).getTime()).toLocalDate();
            LETZTE_ERFASSUNG = STICHTAG.minusMonths(6);
            dcmbLetzte.setDate(JavaTimeConverter.toDate(LETZTE_ERFASSUNG));
            setParameters();
        });

        dcmbLetzte.setShowNoneButton(false);
        dcmbLetzte.setInvalidValueAllowed(false);
        dcmbLetzte.getDateModel().setMaxDate(new GregorianCalendar());
        dcmbLetzte.setDate(JavaTimeConverter.toDate(LETZTE_ERFASSUNG));
        dcmbLetzte.addPropertyChangeListener("selectedItem", evt -> {
            LETZTE_ERFASSUNG = JavaTimeConverter.toJavaLocalDateTime(((GregorianCalendar) evt.getNewValue()).getTime()).toLocalDate();
            setParameters();
        });


        JComboBox<Homes> cmbHome = new JComboBox<>();
        HomesService.setComboBox(cmbHome);
        home = (Homes) cmbHome.getSelectedItem();
        cmbHome.addItemListener(e -> {
            home = (Homes) e.getItem();
            setParameters();
        });


        java.util.List<Component> list = new ArrayList();
        list.add(new JLabel(SYSTools.xx("qdvs.stichtag")));
        list.add(cmbHome);
        list.add(dcmbStichtag);
        list.add(fcWorkdir);
        list.add(GUITools.createHyperlinkButton(SYSTools.xx("qdvs.ergebniserfassung"), SYSConst.icon22add, e -> {
            ergebniserfasung();
        }));


        return list;
    }

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
    // der key ist eine paar aus zeile und spalte des closing tags mit dem Fehler
    // der value ist eine Liste aller Fehler die dazu gehören
    // diese Fehlerliste lässt sich mit den Regeln der CSV Datei Matchen um die restlichen Daten zu den Regeln zu erhalten.
    private MultiKeyMap<MultiKey<Integer>, ArrayList<String>> validateFile(File xmlFile, File xsdFile) throws SAXException, IOException, XMLStreamException {
        // 1. Lookup a factory for the W3C XML Schema language
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/XML/XMLSchema/v1.1");
        //        factory.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.VALIDATE_ANNOTATIONS_FEATURE, true);

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
     * Erzeugt einen Lookup Table der zu jedem zeile, spalte paar die idbewohner enthält.
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

    @Override
    public void addLog(String log) {
        SwingUtilities.invokeLater(() -> {
            txtLog.append(SYSTools.xx(log) + "\n");
            OPDE.getDisplayManager().addSubMessage(log);
        });

    }

    private class TreeInfoNode extends DefaultMutableTreeNode {
        public static final int RESIDENT = 0;
        public static final int SELF_FOUND_ERROR = 1;
        public static final int ERROR_BY_DAS = 2;
        private int type = 0;

        public TreeInfoNode(Resident resident) {
            setUserObject(resident);
            type = RESIDENT;
        }

        public TreeInfoNode(String error, int type) {
            setUserObject(error);
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

    private class TreeRenderer extends DefaultTreeCellRenderer {
        TreeRenderer() {
            super();
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {


            JLabel component = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            component.setText("empty");

            if (value instanceof TreeInfoNode) {
                TreeInfoNode node = (TreeInfoNode) value;
                Object userObject = node.getUserObject();

                if (node.getType() == TreeInfoNode.RESIDENT)
                    component.setText(QdvsService.toString((Resident) userObject));
                if (node.getType() == TreeInfoNode.SELF_FOUND_ERROR) {
                    component.setText(SYSTools.toHTMLForScreen(SYSConst.html_paragraph(userObject.toString())));
                }
                if (node.getType() == TreeInfoNode.ERROR_BY_DAS) component.setText(userObject.toString());
            }
            return component;
        }


    }

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
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();

            if (userObject instanceof Resident || !leaf) {
                return super.getTreeCellRendererComponent(tree, value, selected,
                        expanded, leaf, row, hasFocus);
            } else {
                return taRenderer.getTreeCellRendererComponent(tree, value, selected,
                        expanded, leaf, row, hasFocus);
            }
        }
    }

    class TextAreaRenderer extends JScrollPane implements TreeCellRenderer {
        JTextArea textarea;
        Color backgroundNonSelectionColor;
        Color backgroundSelectionColor;
        Color textNonSelectionColor;
        Color textSelectionColor;

        public TextAreaRenderer() {
            textarea = new JTextArea(6, 40);
            textarea.setLineWrap(true);
            textarea.setWrapStyleWord(true);
//          textarea.setBorder(new TitledBorder(&amp;amp;quot;This is a JTextArea&amp;amp;quot;));
            getViewport().add(textarea);
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

                if (node.getType() == TreeInfoNode.RESIDENT)
                    textarea.setText(QdvsService.toString((Resident) userObject));
                if (node.getType() == TreeInfoNode.SELF_FOUND_ERROR) {
                    textarea.setText(userObject.toString());
                }
                if (node.getType() == TreeInfoNode.ERROR_BY_DAS) textarea.setText(userObject.toString());
            } else {
                textarea.setText("empty");
            }

            return this;
        }
    }


}
