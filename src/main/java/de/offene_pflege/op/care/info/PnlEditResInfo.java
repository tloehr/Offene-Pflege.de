package de.offene_pflege.op.care.info;

import com.itextpdf.text.Image;
import com.itextpdf.text.*;
import com.jidesoft.combobox.DateExComboBox;
import com.jidesoft.combobox.ExComboBox;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.OverlayTextArea;
import de.offene_pflege.entity.EntityTools;
import de.offene_pflege.entity.building.Rooms;
import de.offene_pflege.entity.files.SYSFilesTools;
import de.offene_pflege.entity.info.*;
import de.offene_pflege.entity.prescription.GP;
import de.offene_pflege.entity.prescription.GPTools;
import de.offene_pflege.entity.prescription.Hospital;
import de.offene_pflege.entity.prescription.HospitalTools;
import de.offene_pflege.entity.values.ResValue;
import de.offene_pflege.entity.values.ResValueTools;
import de.offene_pflege.entity.values.Resvaluetypes;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.system.PDF;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.*;
import de.offene_pflege.services.ResvaluetypesService;
import de.offene_pflege.services.RoomsService;
import org.apache.commons.collections.Closure;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.awt.Font;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created with IntelliJ IDEA. User: tloehr Date: 11.05.13 Time: 13:57 To change this template use File | Settings |
 * File Templates.
 */
public class PnlEditResInfo implements HasLogger {
    public static final String internalClassID = "nursingrecords.info.dlg";

    // Zustand des gesamten Formulars
    public static final int EDIT = 0; // wenn man das bestehende einfach korrigieren möchte
    public static final int CHANGE = 1; // wenn ein neues ResInfo das alte ersetzt
    public static final int NEW = 2; // bei einem leeren neuen ResInfo Eintrag
    public static final int DISPLAY = 3; // Wenn es einfach dargestellt wird


    private final int TYPE_DONT_CARE = 0;
    private final int TYPE_INT = 1;
    private final int TYPE_DOUBLE = 2;
    private final int TYPE_DATE = 3;
    private final int TYPE_TIME = 4;
    private int mode;
    private boolean scalemode;
    //    private ResValue scaleValue = null;
    private final int TEXTFIELD_STANDARD_WIDTH = 35;


    /**
     * es gibt zwei phasen in denen das Panel benutzt wird in der PHASE_AUFBAU werden die Struktur aus dem XML des Types
     * gelesen und ein passendes Swing-Panel erzeugt. Damit sind auch schon alle listener verbunden, die jede Änderung
     * an den Controls im _content_ speichern.
     */
    private final int PHASE_AUFBAU = 0;
    private final int PHASE_BENUTZUNG = 1;
    private int aktuelle_phase; // am Anfang wird immer aufgebaut.

    boolean initPanel = false;
    Properties content;
    private JTextArea sumlabel;
    private Component focusOwner = null;

    private boolean panel_enabled;

    private ArrayList<RiskBean> scaleriskmodel;
    private String scalesumlabeltext;
    private ArrayList<String> scaleButtonGroups; // eine Liste mit den Namen der Buttongroups eines scales;
    private ArrayList<String> lockedforchanges;
    private HashMap<String, Object> components;
    private HashMap<String, Object> keywords;

    HashMap<String, MasterComponent> master_components = new HashMap<>();

    private ArrayList<JComponent> focusTraversal;

    private ResInfo resInfo;
    private Closure closure;
    private JPanel pnlContent, main;
    private boolean changed = false;
    private OverlayTextArea txtComment;
    private DefaultOverlayable ovrComment;
    Exception lastParsingException;
    Color background;


    public PnlEditResInfo(ResInfo resInfo, Color basecolor) {
        this(resInfo, null, basecolor);
    }

    public PnlEditResInfo(ResInfo resInfo, Closure closure, Color basecolor) {
        aktuelle_phase = PHASE_AUFBAU;
        this.resInfo = resInfo;
        this.closure = closure;
        this.mode = DISPLAY;
        if (basecolor != null) {
            background = GUITools.blend(basecolor, Color.WHITE, 0.1f);
        }
        try {
            initPanel(resInfo.getResInfoType().getXml());
        } catch (ParserConfigurationException e) {
            OPDE.fatal(e);
        }
    }

    private JidePopup createPopupInfo(String strtxt, Component owner) {
        JLabel txt = new JLabel(strtxt);
        JidePopup popupInfo = new JidePopup();
        popupInfo.setOwner(owner);
        popupInfo.setMovable(true);
        popupInfo.setResizable(true);

        JScrollPane scrl = new JScrollPane(txt);
        scrl.setMaximumSize(new Dimension(550, 300));

        popupInfo.setContentPane(scrl);
        popupInfo.removeExcludedComponent(txt);
        popupInfo.setDefaultFocusComponent(txt);
        return popupInfo;
    }

    private String prepareTooltip(String in) {
        if (in != null) {
            in = SYSTools.xx(in);
            in = in.replace('[', '<').replace(']', '>');

            if (in.indexOf("<p>") < 0 && in.indexOf("<li>") < 0) {
                in = "<p>" + in + "</p>";
            }

            in = in.replace("<p>", "<p style=\"width:300px;\">");
            in = in.replace("<li>", "<li style=\"width:300px;\">");
        }
        return in;
    }

    /**
     * Dieser Konstuktor ist nur für den schnellen Test von neuen XML Formularen
     *
     * @param xml
     */
    public PnlEditResInfo(String xml, Closure closure) {
        aktuelle_phase = PHASE_AUFBAU;
        this.resInfo = ResInfoTools.createResInfo(ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_FOOD), ResidentTools.getAllActive().get(1), OPDE.getLogin().getUser()); // irgendwelche zufalls werte für den DEV Mode
        this.closure = closure;
        this.mode = NEW;
        try {
            initPanel(xml);
        } catch (ParserConfigurationException e) {
            OPDE.fatal(e);
        }
    }

    public void cleanup() {

    }

    public boolean isPanel_enabled() {
        return panel_enabled;
    }

    private void initPanel(String xml_structure) throws ParserConfigurationException {
        aktuelle_phase = PHASE_AUFBAU; // damit ignorieren die listener alle Änderungen

        keywords_erstellen();

        content = new Properties();
        focusTraversal = new ArrayList<>();
        lockedforchanges = new ArrayList<>();

        pnlContent = new JPanel(new BorderLayout());
        initPanel = true;
        lastParsingException = null;

        // Struktur...
        try {
            String xmltext = "<?xml version=\"1.0\"?><structure>" + xml_structure + "</structure>";
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            SAXParser parser = parserFactory.newSAXParser();
            XMLReader reader = parser.getXMLReader();

            InputSource is = new InputSource(new java.io.BufferedReader(new StringReader(xmltext)));

            HandlerDatenStruktur struktur = new HandlerDatenStruktur();
            reader.setContentHandler(struktur);

            reader.parse(is);

            // Zu jedem ResInfo (unabhängig von der Struktur) gibt es immer ein Bemerkungsfeld. Das wird hier erzeugt.
            txtComment = new OverlayTextArea();
            txtComment.setBorder(new LineBorder(Color.DARK_GRAY, 1));
            txtComment.setOpaque(true);
            if (background != null) {
                txtComment.setBackground(background);
            }
            txtComment.setRows(3);
            txtComment.setWrapStyleWord(true);
            txtComment.setLineWrap(true);
            txtComment.setDisabledTextColor(Color.DARK_GRAY);
            txtComment.addCaretListener(e -> {
                if (initPanel) return;
                changed = true;
            });
            ovrComment = new DefaultOverlayable(txtComment);
            JLabel lblComment = new JLabel(SYSTools.xx("misc.msg.comment"));
            lblComment.setForeground(Color.LIGHT_GRAY);
            lblComment.setFont(SYSConst.ARIAL18BOLD);
            ovrComment.addOverlayComponent(lblComment, DefaultOverlayable.SOUTH_EAST);
            if (resInfo.getResValue() != null) {
                pnlContent.add(new JLabel(SYSTools.xx("nursingrecords.info.dlg.will.create.value")), BorderLayout.NORTH);
            }

            pnlContent.add(struktur.getPanel(), BorderLayout.CENTER);
            pnlContent.add(new JScrollPane(ovrComment), BorderLayout.SOUTH);

        } catch (SAXException ex1) {
            ex1.printStackTrace();
            lastParsingException = ex1;
        } catch (IOException ex) {
            ex.printStackTrace();
            lastParsingException = ex;
        }

        // ... und Inhalte
        // falls das _resInfo_ neu ist hat es noch keinen content. Daher setzen wir das hier einmal auf den
        // default content, der sich aus dem erzeugten Formular ergibt.
        if (resInfo.getProperties().isEmpty()) {
            getContent(); // liest die controls aus und setzt die properties
            calcScale(); // fals nötig. Damit haben auch neue Einträge direkt eine Scalesum im Content. Auch wenn nichts geändert wird.
            checkDepencies(); // Setzt Abhängigkeiten, die bereits zu Beginn eines neuen ResInfos zu beachten sind.
        } else {
            setContent(); // liest die properties und setzt die Formular components entsprechend
            // depedency check erfolgt direkt in der _setContent()_ methode
        }

        initPanel = false;

        // add apply and cancel button
        main = new JPanel(new BorderLayout());
        main.setBorder(new EmptyBorder(10, 10, 10, 10));
        main.add(pnlContent, BorderLayout.CENTER);


        JPanel enclosingUpperButtonPanel = new JPanel(new BorderLayout());
        enclosingUpperButtonPanel.setOpaque(false);

        JPanel enclosingLowerButtonPanel = new JPanel(new BorderLayout());
        enclosingLowerButtonPanel.setOpaque(false);

        JPanel upperButtonBanel = new JPanel();
        upperButtonBanel.setLayout(new BoxLayout(upperButtonBanel, BoxLayout.LINE_AXIS));
        upperButtonBanel.setOpaque(false);

        JPanel lowerButtonBanel = new JPanel();
        lowerButtonBanel.setLayout(new BoxLayout(lowerButtonBanel, BoxLayout.LINE_AXIS));
        lowerButtonBanel.setOpaque(false);

        // export 2 png function for development
        if (OPDE.isDebug()) {
            JButton png = new JButton(SYSConst.icon22magnify1);
            png.setBorder(null);
            png.setContentAreaFilled(false);
            png.setPressedIcon(SYSConst.icon22Pressed);
            png.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            png.addActionListener(e -> GUITools.exportToPNG(pnlContent, resInfo.getResInfoType().getID()));
            upperButtonBanel.add(png);
        }

        ActionListener applyListener = e -> {
            closure.execute(getResInfo());
            cleanup();
        };

        JButton apply1 = GUITools.getTinyButton(null, SYSConst.icon22apply);
        apply1.addActionListener(applyListener);
        upperButtonBanel.add(apply1);

        JButton apply2 = GUITools.getTinyButton(null, SYSConst.icon22apply);
        apply2.addActionListener(applyListener);
        lowerButtonBanel.add(apply2);


        JButton cancel1 = GUITools.getTinyButton(null, SYSConst.icon22cancel);
        cancel1.addActionListener(e -> cancel());
        upperButtonBanel.add(cancel1);


        JButton cancel2 = GUITools.getTinyButton(null, SYSConst.icon22cancel);
        cancel2.addActionListener(e -> cancel());
        lowerButtonBanel.add(cancel2);


        // icons für den Zeitraum
        JLabel iconlabel = new JLabel(SYSConst.findIcon(SYSConst.icon22intervalBySecond));
        if (resInfo.getResInfoType().getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_SINGLE_INCIDENTS) {
            iconlabel.setIcon(SYSConst.findIcon(SYSConst.icon22singleIncident));
        }
        lowerButtonBanel.add(iconlabel);


        enclosingUpperButtonPanel.add(upperButtonBanel, BorderLayout.LINE_START);
        main.add(enclosingUpperButtonPanel, BorderLayout.NORTH);

        enclosingLowerButtonPanel.add(lowerButtonBanel, BorderLayout.LINE_END);
        main.add(enclosingLowerButtonPanel, BorderLayout.SOUTH);

        if (!focusTraversal.isEmpty()) {
            main.setFocusCycleRoot(true);
            main.setFocusTraversalPolicy(new FocusTraversalPolicy() {
                @Override
                public Component getComponentAfter(Container aContainer, Component aComponent) {
                    if (focusOwner == null) {
                        focusOwner = focusTraversal.get(0);
                    } else {
                        int pos = focusTraversal.indexOf(focusOwner) + 1;
                        if (pos >= focusTraversal.size()) {
                            pos = 0;
                        }
                        focusOwner = focusTraversal.get(pos);
                    }
                    return focusOwner;
                }

                @Override
                public Component getComponentBefore(Container aContainer, Component aComponent) {
                    if (focusOwner == null) {
                        focusOwner = focusTraversal.get(focusTraversal.size() - 1);
                    } else {
                        int pos = focusTraversal.indexOf(focusOwner) - 1;
                        if (pos < 0) {
                            pos = focusTraversal.size() - 1;
                        }
                        focusOwner = focusTraversal.get(pos);
                    }
                    return focusOwner;
                }

                @Override
                public Component getFirstComponent(Container aContainer) {
                    return focusTraversal.get(0);
                }

                @Override
                public Component getLastComponent(Container aContainer) {
                    return focusTraversal.get(focusTraversal.size() - 1);
                }

                @Override
                public Component getDefaultComponent(Container aContainer) {
                    return focusTraversal.get(0);
                }
            });
            SwingUtilities.invokeLater(() -> {
                focusTraversal.get(0).requestFocus();
                focusOwner = focusTraversal.get(0);
            });
        }
        setXEnabled(main, false);

        aktuelle_phase = PHASE_BENUTZUNG; // ab jetzt ist alles aktiv
    }

    private void keywords_erstellen() {
        keywords = new HashMap<>();
        keywords.put("now", JavaTimeConverter.to_iso8601(LocalDateTime.now()));
        keywords.put("null", null);
    }

    public void cancel() {
        closure.execute(null);
        cleanup();
    }

    public Exception getLastParsingException() {
        return lastParsingException;
    }


    /**
     * Zieht Werte aus verschiedenen Quellen heran. Meistens aus den Bewohner-Werten.
     *
     * @param preset
     * @param deflt
     * @return
     */
    private String getPreset(String preset, String deflt) {
        String d = SYSTools.catchNull(deflt);
        if (SYSTools.catchNull(preset).isEmpty()) {
            return d;
        }

        Resident resident = resInfo.getResident();

        if (preset.equalsIgnoreCase("currenttime")) {
            return DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());
        }

        if (preset.equalsIgnoreCase("currentdate")) {
            return DateFormat.getDateInstance(DateFormat.DEFAULT).format(new Date());
        }

        if (preset.equalsIgnoreCase("heightlast")) {
            Optional<ResValue> r = ResValueTools.getLast(resident, ResvaluetypesService.HEIGHT);
            return r.isPresent() ? SYSTools.formatBigDecimal(r.get().getVal1()) : d;
        }
        if (preset.equalsIgnoreCase("weightlast")) {
            Optional<ResValue> r = ResValueTools.getLast(resident, ResvaluetypesService.WEIGHT);
            return r.isPresent() ? SYSTools.formatBigDecimal(r.get().getVal1()) : d;
        }
        if (preset.equalsIgnoreCase("weight-1m")) {
            long target = new DateTime().minusMonths(1).getMillis();
            ArrayList<ResValue> list = ResValueTools.getResValues(resident, ResvaluetypesService.WEIGHT, new LocalDate().minusDays(45), new LocalDate().minusDays(15));

            ResValue closest = null;
            long distance = Long.MAX_VALUE;
            for (ResValue rv : list) {
                if (Math.abs(target - rv.getPit().getTime()) < distance) {
                    distance = Math.abs(target - rv.getPit().getTime());
                    closest = rv;
                }
            }

            return closest == null ? d : SYSTools.formatBigDecimal(closest.getVal1());
        }
        if (preset.equalsIgnoreCase("weight-6m")) {
            long target = new DateTime().minusMonths(6).getMillis();
            ArrayList<ResValue> list = ResValueTools.getResValues(resident, ResvaluetypesService.WEIGHT, new LocalDate().minusMonths(6).minusDays(15), new LocalDate().minusMonths(5).minusDays(15));

            ResValue closest = null;
            long distance = Long.MAX_VALUE;
            for (ResValue rv : list) {
                if (Math.abs(target - rv.getPit().getTime()) < distance) {
                    distance = Math.abs(target - rv.getPit().getTime());
                    closest = rv;
                }
            }

            return closest == null ? d : SYSTools.formatBigDecimal(closest.getVal1());
        }
        if (preset.equalsIgnoreCase("weight-1y")) {
            long target = new DateTime().minusYears(1).getMillis();
            ArrayList<ResValue> list = ResValueTools.getResValues(resident, ResvaluetypesService.WEIGHT, new LocalDate().minusYears(1).minusDays(15), new LocalDate().minusMonths(11).minusDays(15));

            ResValue closest = null;
            long distance = Long.MAX_VALUE;
            for (ResValue rv : list) {
                if (Math.abs(target - rv.getPit().getTime()) < distance) {
                    distance = Math.abs(target - rv.getPit().getTime());
                    closest = rv;
                }
            }

            return closest == null ? d : SYSTools.formatBigDecimal(closest.getVal1());
        }
        return d;
    }

    public void print() {
        try {

            final PDF pdf = new PDF(null, "", 10);

            Paragraph h1 = new Paragraph(new Phrase(SYSTools.xx("nursingrecords.info.single"), PDF.plain(PDF.sizeH1())));
            h1.setAlignment(Element.ALIGN_CENTER);
            pdf.getDocument().add(h1);

            Paragraph p = new Paragraph(new Phrase(ResidentTools.getLabelText(resInfo.getResident())));
            p.setAlignment(Element.ALIGN_CENTER);
            pdf.getDocument().add(p);
            pdf.getDocument().add(Chunk.NEWLINE);

            Paragraph p1 = new Paragraph();

            p1.add(new Chunk(resInfo.getResInfoType().getResInfoCat().getText()));
            p1.add(Chunk.NEWLINE);
            p1.add(new Chunk(resInfo.getResInfoType().getShortDescription()));
            p1.add(Chunk.NEWLINE);
            p1.add(Chunk.NEWLINE);


            DateFormat df = resInfo.isSingleIncident() || resInfo.isBySecond() ? DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT) : DateFormat.getDateInstance();

            if (resInfo.isSingleIncident()) {
                p1.add(new Chunk(df.format(resInfo.getFrom()) + " " + resInfo.getUserON().getFullname()));
                p1.add(Chunk.NEWLINE);
            } else if (resInfo.isClosed()) {
                p1.add(new Chunk(df.format(resInfo.getFrom()) + " (" + resInfo.getUserON().getFullname()) + ") >> " + df.format(resInfo.getTo()) + " (" + resInfo.getUserOFF().getFullname() + ")");
                p1.add(Chunk.NEWLINE);

            } else {
                p1.add(new Chunk(df.format(resInfo.getFrom()) + " (" + resInfo.getUserON().getFullname()) + ") >> ");
                p1.add(Chunk.NEWLINE);
            }
            pdf.getDocument().add(p1);

            setXEnabled(main, true);
            Image image = Image.getInstance(GUITools.getAsImage(pnlContent).toByteArray());
            setXEnabled(main, panel_enabled);

            image.scaleToFit(Utilities.millimetersToPoints(170f), Utilities.millimetersToPoints(170f));


            pdf.getDocument().add(image);

            pdf.getDocument().close();


            SYSFilesTools.handleFile(pdf.getOutputFile(), Desktop.Action.OPEN);

        } catch (Exception e) {
            OPDE.fatal(e);
        }

    }


    public void setClosure(Closure closure) {
        this.closure = closure;
    }

    /**
     * Setzt die Properties des aktuellen resInfo, fügt noch den Kommentartext hinzu und gibt das Objekt dann zurück.
     * Wird beim Speichern in der Closure verwendet.
     *
     * @return
     */
    private ResInfo getResInfo() {
        resInfo.setProperties(getContent());
        resInfo.setText(txtComment.getText());
        return resInfo;
    }

    /**
     * liest alle Einträge aus dem Inhalt des aktuellen Formulars und erzeugt eine Propertie als String.
     *
     * @return
     */
    private String getContent() {
        String props = "";
        try {
            StringWriter writer = new StringWriter();

//            // Wenn components unsichtbar sind, dann wurden sie aufgrund von dependencies entfernt.
//            // folglich soll ihr content auch nicht gespeichert werden
//            for (String key : content.stringPropertyNames()) {
//                if (components.containsKey(key) && components.get(key) instanceof Component && !((Component) components.get(key)).isVisible()) {
//                    content.remove(key);
//                    getLogger().debug("component is invisble => removing content for: " + key);
//                }
//            }

            content.store(writer, "[" + resInfo.getResInfoType().getID() + "] " + resInfo.getResInfoType().getShortDescription());
            props = writer.toString();
            writer.close();
        } catch (IOException e1) {
            OPDE.fatal(e1);
        }
        return props;
    }


    /**
     * this method creates a panel based on the XML structure taken from ResInfoType and the content stored in the
     * properties data in ResInfo.
     */
    public JPanel getPanel() {

        if (background != null) {
            pnlContent.setOpaque(true);
            pnlContent.setBackground(background);
        }

        if (main != null) {
            if (background != null) {
                main.setOpaque(true);
                main.setBackground(background);
            }
            return main;
        }


        return pnlContent;
    }

    public void setPanelEnabled(boolean enabled, int mode) {
        this.mode = mode;
        this.panel_enabled = enabled;
        setXEnabled(main, enabled);
    }


    /**
     * läuft rekursiv durch alle Kinder eines Containers und setzt deren Enabled Status auf panel_enabled.
     */
    private void setXEnabled(JComponent container, boolean enabled) {
        // Bei einer Combobox muss die Rekursion ebenfalls enden.
        // Sie besteht aus weiteren Unterkomponenten
        // "disabled" wird sie aber bereits hier.
        // Rekursionsanker
        if (container.getComponentCount() == 0 || container instanceof JComboBox || container instanceof DateExComboBox) {

            // alles was im nachhinein nicht mehr geändertwertden soll, bleibt disabled. Stammt daher, dass die Dekubitus Kategorie nicht mehr geändert werden soll.
            if (mode == CHANGE && lockedforchanges.contains(container.getName())) {
                container.setEnabled(false);
            } else {
                container.setEnabled(enabled); // disablen geht immer
            }

        } else { // rekursion um alle components in den containern zu erreichen
            Component[] c = container.getComponents();
            for (int i = 0; i < c.length; i++) {
                if (c[i] instanceof JComponent) {
                    JComponent jc = (JComponent) c[i];
                    setXEnabled(jc, enabled);
                }
            }
        }
    }

    private void calcScale() {
        if (!scalemode) return;

        BigDecimal scalesum = BigDecimal.ZERO;

        for (String bgName : scaleButtonGroups) {
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

            content.put("scalesum", SYSTools.formatBigDecimal(scalesum));
            content.put("risk", risiko);
            content.put("rating", rating);

            if (resInfo.getResValue() != null) {
                resInfo.getResValue().setVal1(scalesum);
                resInfo.getResValue().setText(SYSTools.xx("nursingrecords.info.dlg.value.from.info") + ": " + resInfo.getResInfoType().getShortDescription() + " " + resInfo.getResInfoType().getLongDescription() + ": " + risiko);
            }
        }
    }

    /**
     * meldet ob das Formular bearbeitet wurde oder nicht
     *
     * @return
     */
    public boolean isChanged() {
        return changed;
    }


    /**
     * liest den Inhalt der Properties der resinfo in die variable content. Dann werden alle Formular Elemente
     * entsprechend dieser Werte gesetzt.
     */
    private void setContent() {
        txtComment.setText(SYSTools.catchNull(resInfo.getText()));

//        if (SYSTools.catchNull(resInfo.getProperties()).isEmpty()) {
//            return;
//        }

        try {
            StringReader reader = new StringReader(resInfo.getProperties());
            content.load(reader);
            reader.close();
        } catch (IOException ex) {
            OPDE.fatal(ex);
        }

        // Inhalte setzen
        for (String key : components.keySet()) {

            setContentForComponent(key);

            // Dependency
            checkDependency(key, content.getProperty(key));
        }

        calcScale();
    }

    private void setContentForComponent(String key) {
        Object entry = components.get(key);

        if (entry instanceof JRadioButton) {
            StringTokenizer st = new StringTokenizer(key, ":");
            String tagname = st.nextToken();
            String value = st.nextToken();
            ((JRadioButton) entry).setSelected(content.containsKey(tagname) && content.getProperty(tagname).equals(value));
        } else if (entry instanceof Pair) { // Scale
            StringTokenizer st = new StringTokenizer(key, ":");
            String tagname = st.nextToken();
            String value = st.nextToken();
            ((Pair<JRadioButton, BigDecimal>) entry).getFirst().setSelected(content.getProperty(tagname).equals(value));
        } else if (entry instanceof JCheckBox) {
            ((JCheckBox) entry).setSelected(content.getProperty(key).equalsIgnoreCase("true"));
        } else if (entry instanceof JTextField) {
            ((JTextField) entry).setText(SYSTools.unescapeXML(content.getProperty(key)));
        } else if (entry instanceof DateExComboBox) {
            try {
                ((DateExComboBox) entry).setDate(JavaTimeConverter.toDate(JavaTimeConverter.from_iso8601(content.getProperty(key))));
            } catch (java.time.format.DateTimeParseException dtpe) {
                ((DateExComboBox) entry).setDate(null);
            }
        } else if (entry instanceof PnlBodyScheme) {
            ((PnlBodyScheme) entry).setContent(content);
        } else if (entry instanceof PnlGP) {
            long gpid = Long.parseLong(SYSTools.catchNull(content.getProperty(key + ".id"), "-1"));
            if (gpid > 0) {
                GP gp = EntityTools.find(GP.class, gpid);
                ((PnlGP) entry).setSelected(gp);
            }
        } else if (entry instanceof PnlHospital) {
            long hid = Long.parseLong(SYSTools.catchNull(content.getProperty(key + ".id"), "-1"));
            if (hid > 0) {
                Hospital hospital = EntityTools.find(Hospital.class, hid);
                ((PnlHospital) entry).setSelected(hospital);
            }
        } else if (entry instanceof JPanel) {
            JPanel thisPanel = ((JPanel) entry);
            if (((JPanel) entry).getName().equals("roomSelect")) {
                long rid = Long.parseLong(SYSTools.catchNull(content.getProperty(key + ".id"), "-1"));
                JComboBox<Rooms> mycmb = (JComboBox) thisPanel.getComponents()[0]; // there can be only one, highlander :P
                if (rid > 0) {
                    Rooms room = EntityTools.find(Rooms.class, rid);
                    mycmb.setSelectedItem(room);
                } else {
                    mycmb.setSelectedItem(null);
                }
            }
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

    private class RadioButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            if (aktuelle_phase == PHASE_AUFBAU) return;
            JRadioButton j = (JRadioButton) evt.getSource();
            JPanel innerpanel = (JPanel) j.getParent();
            String groupname = innerpanel.getName();
            String value = j.getName();
            content.put(groupname, value);
            if (scalemode) {
                calcScale();
            }
            checkDependency(groupname, value);

            changed = true;
        }
    }

    /**
     * geht alle Komponenten durch und prüft ob Abhängigkeiten zu berücksichtigen sind
     */
    private void checkDepencies() {
        for (String key : components.keySet()) {
            checkDependency(key, content.getProperty(key));
        }
    }

    /**
     * @param masterComponentKey - prüft ob die Sichtbarkeit von Slavecomponents geändert werden muss entsprechend
     *                           dieser MasterComponent
     * @param value              - der Wert, der zu berücksichtigen ist.
     */
    private void checkDependency(String masterComponentKey, String value) {
        if (master_components.containsKey(masterComponentKey) && value != null) {
            master_components.get(masterComponentKey).getSlaveComponents().forEach(slaveComponent -> slaveComponent.setVisible(value));
        }
    }

    private class BodySchemeItemListener implements ItemListener {
        private final String name;

        private BodySchemeItemListener(String name) {
            this.name = name;
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (aktuelle_phase == PHASE_AUFBAU) return;
            content.put(name + "." + ((JCheckBox) e.getSource()).getName(), Boolean.toString(((JCheckBox) e.getSource()).isSelected()));
            changed = true;
        }
    }

    private class CheckBoxItemListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent evt) {
            if (aktuelle_phase == PHASE_AUFBAU) return;
            JCheckBox j = (JCheckBox) evt.getSource();
            String cbname = j.getName();
            String value = Boolean.toString(j.isSelected());
            content.put(cbname, value);
            checkDependency(cbname, value);
            changed = true;
        }
    }

    private class ComboBoxItemStateListener implements ItemListener {
        public void itemStateChanged(ItemEvent evt) {
            if (aktuelle_phase == PHASE_AUFBAU) return;
            if (evt.getStateChange() != ItemEvent.SELECTED) return;
            JComboBox j = (JComboBox) evt.getSource();
            if (!j.isEnabled()) return;

            ComboBoxBean bean = (ComboBoxBean) j.getSelectedItem();
            content.put(j.getName(), bean.getName());
            j.setToolTipText(bean.getTooltip());
            checkDependency(j.getName(), bean.getName());
            changed = true;
        }
    }

    private class DateExComboboxListener implements ItemListener {
        //datefield
        public void itemStateChanged(ItemEvent evt) {
            if (aktuelle_phase == PHASE_AUFBAU) return;
            if (evt.getStateChange() != ItemEvent.SELECTED) return;
            DateExComboBox j = (DateExComboBox) evt.getSource();
            if (!j.isEnabled()) return;

            if (evt.getItem() == null) {
                content.put(j.getName(), "");
            } else if (evt.getItem() instanceof GregorianCalendar) { // diese Component löst 2x aus. einmal mit Date, einmal mit GregorianCalendar
                content.put(j.getName(), JavaTimeConverter.to_iso8601(j.getDate()));
            }
            changed = true;
        }
    }

    private class TextFieldFocusListener implements FocusListener {
        int type = TYPE_DONT_CARE;
        Pair<DateTime, DateTime> minmax;
        boolean optional;

        TextFieldFocusListener(int type, boolean optional) {
            this.type = type;
            this.optional = optional;
            minmax = new Pair<>(new DateTime(SYSConst.DATE_THE_VERY_BEGINNING), new DateTime(SYSConst.DATE_UNTIL_FURTHER_NOTICE));
        }

        TextFieldFocusListener(int type, Pair<DateTime, DateTime> minmax, boolean optional) {
            this.type = type;
            this.minmax = minmax;
            this.optional = optional;
        }

        public void focusGained(FocusEvent e) {
            SYSTools.markAllTxt((JTextComponent) e.getSource());
        }

        public void focusLost(FocusEvent e) {
            if (aktuelle_phase == PHASE_AUFBAU) return;
            JTextComponent j = (JTextComponent) e.getSource();
            String text = ((JTextComponent) e.getSource()).getText();

            if (type != TYPE_DONT_CARE) {

                if (optional && text.trim().isEmpty()) {
                    // nop!
                } else if (type == TYPE_DATE) {
                    try {
                        Date myDate = SYSCalendar.parseDate(text);
                        if (JavaTimeConverter.isBefore(myDate, minmax.getFirst().toDate())) {
                            throw new Exception("date out of bounds");
                        }
                        if (JavaTimeConverter.isAfter(myDate, minmax.getSecond().toDate())) {
                            throw new Exception("date out of bounds");
                        }
                        j.setText(DateFormat.getDateInstance().format(myDate));
                    } catch (Exception ex) {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.wrongdate", DisplayMessage.WARNING));
                        j.setText(DateFormat.getDateInstance().format(new Date()));
                    }
                } else if (type == TYPE_TIME) {
                    try {
                        Date myDate = new Date(SYSCalendar.parseTime(text).getTimeInMillis());
                        j.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(myDate));
                    } catch (Exception ex) {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.wrongtime", DisplayMessage.WARNING));
                        j.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date()));
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
                            num = Integer.valueOf(1);
                        }
                    }

                    if (type == TYPE_DOUBLE) {
                        if (num == null) {
                            num = Double.valueOf(1.0d);
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
     * Dieser Handler ist ein SaxParser Handler. Er durchläuft das Struktur XML Dokument und erstellt einen JPanel, der
     * alle notwendigen Swing Komponenten enthält.
     * <p>
     * Folgende XML Konstrukte können verwendet werden:
     * <ol>
     * <li><code>&lt;checkbox name=&quot;aengstlich&quot; label=&quot;ängstlich&quot;/&gt;</code> führt zu <img
     * src="doc-files/checkbox.png">
     * </li>
     * </ol>
     * <p>
     * Die beschriebenen Konstrukte können nacheinander verwendet werden, so dass nach einer Optiongroup mehrere
     * Checkboxes folgen. Ein Konstrukt wird immer in eine eigene JPanel mit einem FlowLayout eingeschlossen
     * (innerpanel). Die innerpanels werden dann alle der Reihe nach wieder in eine JPanel (untereinander, GridLayout)
     * eingefügt (outerpanel). Diese outerpanel ist letztlich das Ergebnis.
     * <p>
     * Ausserdem schreibt der Handler in die beiden HashMaps <code>components</code> und <code>antwort</code>.
     * <code>components</code> enthält die erstellten Components, der Zugriff erfolgt über das <code>name</code>
     * Attribut aus der XML Struktur. So dass man, gemäß des obigen Beispiels unter 1.), über
     * <code>component.get("aengstlich")</code> den Zugriff auf die entsprechend JCheckbox erhält.
     * <p>
     * <code>antwort</code> enthält den aktuellen Zustand des jeweiligen Widgets. Bei Checkboxes (wie im Beispiel
     * beschrieben): ("aengstlich", "false"). Bei Optiongroups setzt sich der Name des einzelnen Radiobuttons aus
     * gruppenname und optionname zusammen: ("hilfebedarf.uA", "true"). Textfelder enthalten den Eingabetext direkt:
     * ("vorname", "Torsten"). Listen enthalten den Primary Key der entsprechenden Tabellenzeile (meist ist das ein
     * <code>long</code> Wert: ("zimm", 38).
     */
    private class HandlerDatenStruktur extends DefaultHandler {

        private static final String FONT = "Helvetica";
        private JPanel outerpanel; // enthält den component. Bei Optiongroups oder Tabgroups bildet outpanel den Rahmen, der alle Teile dieses Components umfasst.
        private JPanel innerpanel;
        private String groupname;
        private DefaultComboBoxModel boxModel;
        private JidePopup popupInfo = null;


        @Override
        public void startDocument() throws SAXException {
            components = new HashMap();

            outerpanel = new JPanel(new RiverLayout());

            // set a title
            JLabel jl = new JLabel(resInfo.getResInfoType().getShortDescription());
            if (OPDE.isDebug()) jl.setToolTipText(SYSTools.toHTMLForScreen(ResInfoTools.getContentAsHTML(resInfo)));
            jl.setFont(SYSConst.ARIAL24BOLD);
            outerpanel.add(jl, RiverLayout.LEFT);
        }

        @Override
        public void startElement(String nsURI, String strippedName, String tagName, final Attributes attributes) throws SAXException {
            /***
             *               _   _
             *      ___ _ __| |_(_)___ _ _  __ _ _ _ ___ _  _ _ __
             *     / _ \ '_ \  _| / _ \ ' \/ _` | '_/ _ \ || | '_ \
             *     \___/ .__/\__|_\___/_||_\__, |_| \___/\_,_| .__/
             *         |_|                 |___/             |_|
             */
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
                innerpanel.setOpaque(false);
                if (attributes.getValue("label") != null) {
                    JLabel jl = new JLabel(SYSTools.xx(attributes.getValue("label")));

                    jl.setFont(new Font(FONT, getFontStyle(attributes), getFontSize(attributes)));
                    underline(jl);

                    outerpanel.add("p left", jl);
                    addInfoButtons(outerpanel, attributes);
                }
            }
            if (tagName.equalsIgnoreCase("scale")) {
                scalemode = true;
                scalesumlabeltext = SYSTools.xx(attributes.getValue("label"));
                scaleButtonGroups = new ArrayList();
                scaleriskmodel = new ArrayList();
                try {
                    Resvaluetypes scaleValueType = ResvaluetypesService.getType(Short.parseShort(SYSTools.catchNull(attributes.getValue("resvaltype"))));
                    resInfo.setResValue(new ResValue(resInfo.getResident(), scaleValueType, resInfo.getFrom()));
                    resInfo.getResValue().setVal1(BigDecimal.ZERO);
                } catch (NumberFormatException nfe) {
                    resInfo.setResValue(null);
                }
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
             * Eine Tabgroub fasst Checkboxes zusammen, damit die zum einen auf dem Formular kombiniert werden und
             * zum anderen beim HTML und Textdruck als Einheit erscheinen.
             */
            if (tagName.equalsIgnoreCase("tabgroup")) {
                JLabel jl = new JLabel(SYSTools.xx(attributes.getValue("label")));
                if (!SYSTools.catchNull(attributes.getValue("color")).isEmpty()) {
                    jl.setForeground(GUITools.getColor(attributes.getValue("color")));
                }

                int fontstyle = getFontStyle(attributes);
                jl.setFont(new Font(FONT, fontstyle, getFontSize(attributes)));

                // underline it
                Font original = jl.getFont();
                Map map = original.getAttributes();
                map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                jl.setFont(original.deriveFont(map));

//                jl.setToolTipText(attributes.getValue("tooltip") == null ? null : SYSTools.toHTML("<p>" + SYSTools.xx(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')) + "</p>");
                String layout = SYSTools.catchNull(attributes.getValue("layout"), "br left");
                outerpanel.add(layout, jl);
                addInfoButtons(outerpanel, attributes);

            }
            /***
             *               _   _
             *      ___ _ __| |_(_)___ _ _
             *     / _ \ '_ \  _| / _ \ ' \
             *     \___/ .__/\__|_\___/_||_|
             *         |_|
             * comp:option
             */
            if (tagName.equalsIgnoreCase("option")) {
                BigDecimal score = BigDecimal.ZERO;
                if (scalemode) {
                    score = SYSTools.parseDecimal(attributes.getValue("score"));
                }
                JRadioButton j = new JRadioButton(SYSTools.xx(attributes.getValue("label")));
                j.setOpaque(false);
                focusTraversal.add(j);


                String compName = attributes.getValue("name");
                String layout = attributes.getValue("layout");
                if (SYSTools.catchNull(layout).isEmpty()) {
                    layout = "left";
                }
                j.setName(compName);
                innerpanel.add(layout, j);
                addInfoButtons(innerpanel, attributes);

                j.addActionListener(new RadioButtonActionListener());
                if (scalemode) {
                    components.put(groupname + ":" + compName, new Pair<>(j, score)); // Hier weichen wir vom üblichen SChema ab und übergeben nicht nur die Component sondern auch den Score.
                } else {
                    j.addActionListener(new RadioButtonActionListener());
                    components.put(groupname + ":" + compName, j); // für den späteren Direktzugriff
                }
                ((ButtonGroup) components.get(groupname)).add(j); // der Knopf wird zu der passenden ButtonGroup hinzugefügt.

                if (SYSTools.catchNull(attributes.getValue("default")).equals("true")) {
                    j.setSelected(true);
                    content.put(groupname, attributes.getValue("name"));
                }
            }
            /***
             *         _           _   _
             *      __| |_  ___ __| |_| |__  _____ __
             *     / _| ' \/ -_) _| / / '_ \/ _ \ \ /
             *     \__|_||_\___\__|_\_\_.__/\___/_\_\
             * comp:checkbox
             */
            if (tagName.equalsIgnoreCase("checkbox")) {
                groupname = attributes.getValue("name");

                JCheckBox j = new JCheckBox(SYSTools.xx(attributes.getValue("label")));
                j.setOpaque(false);
                focusTraversal.add(j);
                j.setName(groupname);

                components.put(groupname, j); // für den späteren Direktzugriff
//                j.addActionListener(new CheckBoxActionListener());
                j.addItemListener(new CheckBoxItemListener());

                createComponent(outerpanel, j, attributes);

//                String layout = SYSTools.catchNull(attributes.getValue("layout"), tabgroup ? "tab" : "br left");
//                outerpanel.add(layout, j);
//                addInfoButtons(outerpanel, attributes);
//                addLockforchangesIfUsed(j, attributes);
//                addDepenciesIfUsed(outerpanel, j, attributes);

                int fontstyle = getFontStyle(attributes);
                j.setFont(new Font(FONT, fontstyle, getFontSize(attributes)));

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
             * comp:textfield
             */
            if (tagName.equalsIgnoreCase("textfield")) {
                groupname = attributes.getValue("name");

                boolean optional = SYSTools.catchNull(attributes.getValue("optional"), "false").equalsIgnoreCase("true");

                TextFieldFocusListener tffl = new TextFieldFocusListener(TYPE_DONT_CARE, optional);
                if (SYSTools.catchNull(attributes.getValue("type")).equals("int")) {
                    tffl = new TextFieldFocusListener(TYPE_INT, optional);
                }
                if (SYSTools.catchNull(attributes.getValue("type")).equals("double")) {
                    tffl = new TextFieldFocusListener(TYPE_DOUBLE, optional);
                }
                if (SYSTools.catchNull(attributes.getValue("type")).equals("date")) {
                    if (SYSTools.catchNull(attributes.getValue("onlyinfuture"), "false").equalsIgnoreCase("true")) {
                        tffl = new TextFieldFocusListener(TYPE_DATE, new Pair<>(new DateTime(), new DateTime(SYSConst.DATE_UNTIL_FURTHER_NOTICE)), optional);
                    } else {
                        tffl = new TextFieldFocusListener(TYPE_DATE, optional);
                    }
                }
                if (SYSTools.catchNull(attributes.getValue("type")).equals("time")) {
                    tffl = new TextFieldFocusListener(TYPE_TIME, optional);
                }
                int length = TEXTFIELD_STANDARD_WIDTH;
                String hfill = SYSTools.catchNull(attributes.getValue("hfill")).equalsIgnoreCase("false") ? "" : " hfill";
                if (!SYSTools.catchNull(attributes.getValue("length")).isEmpty()) {
                    length = Integer.parseInt(attributes.getValue("length"));
                    hfill = "";
                }


                JTextField j = new JTextField(length);
                j.setOpaque(false);
                j.setDisabledTextColor(Color.DARK_GRAY);
                focusTraversal.add(j);
                j.setName(groupname);

                int fontstyle = getFontStyle(attributes);
                j.setFont(new Font(FONT, fontstyle, getFontSize(attributes)));

                createComponent(outerpanel, j, attributes);
                components.put(groupname, j); // für den späteren Direktzugriff
                j.addFocusListener(tffl);
                if (mode != CHANGE) {
                    j.setText(getPreset(attributes.getValue("preset"), attributes.getValue("default")));
                }
                content.put(groupname, j.getText());
            }

            /***
             *          _       _        __ _      _     _
             *       __| | __ _| |_ ___ / _(_) ___| | __| |
             *      / _` |/ _` | __/ _ \ |_| |/ _ \ |/ _` |
             *     | (_| | (_| | ||  __/  _| |  __/ | (_| |
             *      \__,_|\__,_|\__\___|_| |_|\___|_|\__,_|
             * comp:datefield
             */
            if (tagName.equalsIgnoreCase("datefield")) {
                // Allgemeine Einstellungen
                groupname = attributes.getValue("name");
                DateExComboBox date_component = new DateExComboBox();
                date_component.setFormat(DateFormat.getDateInstance(DateFormat.MEDIUM));
                focusTraversal.add(date_component);
                date_component.setName(groupname);
                date_component.setShowNoneButton(false);
                date_component.getDateModel().setMaxDate(new GregorianCalendar());

                date_component.setPopupType(ExComboBox.DROPDOWN);
                date_component.setPreferredSize(new Dimension(170, 30));

                // Listener um den Content zu speichern und die dependencies zu ermöglichen
                date_component.addItemListener(new DateExComboboxListener());
                createComponent(outerpanel, date_component, attributes);

                if (SYSTools.catchNull(attributes.getValue("default")).equals("now")) {
                    date_component.setDate(new Date());
                }

                // für den späteren Direktzugriff
                components.put(groupname, date_component);

                // default content setzen
                content.put(groupname, (date_component.getDate() == null ? "" : JavaTimeConverter.to_iso8601(date_component.getDate())));
            }


            /***
             *                                  __
             *      ___ ___ ___  ___ ________ _/ /____  ____
             *     (_-</ -_) _ \/ _ `/ __/ _ `/ __/ _ \/ __/
             *    /___/\__/ .__/\_,_/_/  \_,_/\__/\___/_/
             *           /_/
             */
            if (tagName.equalsIgnoreCase("separator")) {
                String layout = SYSTools.catchNull(attributes.getValue("layout"), "p hfill");
                outerpanel.add(layout, new JSeparator());
            }
            /***
             *                        _        _
             *      __ _ _ __ ___ ___| |___ __| |_
             *     / _` | '_ (_-</ -_) / -_) _|  _|
             *     \__, | .__/__/\___|_\___\__|\__|
             *     |___/|_|
             */
            if (tagName.equalsIgnoreCase("gpselect")) {
                groupname = attributes.getValue("name");
                final String thisGroupName = groupname;

                String sNeurologist = attributes.getValue("neurologist");
                Boolean neurologist = sNeurologist == null ? null : (sNeurologist.equalsIgnoreCase("true") ? true : false);

                String sDermatology = attributes.getValue("dermatology");
                Boolean dermatology = sDermatology == null ? null : (sDermatology.equalsIgnoreCase("true") ? true : false);

                PnlGP pnlGP = new PnlGP(o -> {
                    long gpid;
                    String gpText;
                    if (o == null) {
                        gpid = -1;
                        gpText = "--";
                    } else {
                        gpid = ((GP) o).getArztID();
                        gpText = GPTools.getCompleteAddress((GP) o);
                    }
                    content.put(thisGroupName + ".id", Long.toString(gpid));
                    content.put(thisGroupName + ".text", gpText);
                    changed = true;
                }, neurologist, dermatology);

                int fontstyle = getFontStyle(attributes);

                String layout = SYSTools.catchNull(attributes.getValue("layout"), "br left");
                if (attributes.getValue("label") != null) {
                    JLabel jl = new JLabel(SYSTools.xx(attributes.getValue("label")) + ":");

                    jl.setFont(new Font(FONT, fontstyle, getFontSize(attributes)));

                    outerpanel.add(layout, jl);
                    layout = "left";
                }

                components.put(groupname, pnlGP);
                outerpanel.add(layout, pnlGP);
                addInfoButtons(outerpanel, attributes);
            }
            /***
             *                                ____       _           _
             *      _ __ ___   ___  _ __ ___ / ___|  ___| | ___  ___| |_
             *     | '__/ _ \ / _ \| '_ ` _ \\___ \ / _ \ |/ _ \/ __| __|
             *     | | | (_) | (_) | | | | | |___) |  __/ |  __/ (__| |_
             *     |_|  \___/ \___/|_| |_| |_|____/ \___|_|\___|\___|\__|
             *
             */
            if (tagName.equalsIgnoreCase("roomselect")) {
                groupname = attributes.getValue("name");
                final String thisGroupName = groupname;

                JPanel pnlRoom = new JPanel();
                pnlRoom.setLayout(new BorderLayout());
                pnlRoom.setName("roomSelect");

                DefaultComboBoxModel<Rooms> dcmb = SYSTools.list2cmb(RoomsService.getAllActive());
                dcmb.insertElementAt(null, 0);

                JComboBox<Rooms> cmbRooms = new JComboBox<>(dcmb);
                cmbRooms.setSelectedIndex(0);

                cmbRooms.setRenderer(RoomsService.getRenderer());

                cmbRooms.addItemListener(e -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        long rid;
                        String roomText;
                        if (e.getItem() == null) {
                            rid = -1;
                            roomText = "--";
                        } else {
                            rid = ((Rooms) e.getItem()).getId();
                            roomText = ((Rooms) e.getItem()).toString();
                        }
                        content.put(thisGroupName + ".id", Long.toString(rid));
                        content.put(thisGroupName + ".text", roomText);
                        changed = true;
                    }
                });
                pnlRoom.add(cmbRooms, BorderLayout.CENTER);

                int fontstyle = getFontStyle(attributes);

                String layout = SYSTools.catchNull(attributes.getValue("layout"), "br left");
                if (attributes.getValue("label") != null) {
                    JLabel jl = new JLabel(SYSTools.xx(attributes.getValue("label")) + ":");

                    jl.setFont(new Font(FONT, fontstyle, getFontSize(attributes)));

                    outerpanel.add(layout, jl);
                    layout = "left";
                }


                components.put(groupname, pnlRoom);
                outerpanel.add(layout, pnlRoom);
                addInfoButtons(outerpanel, attributes);
            }
            /***
             *      _                     _ _        _          _           _
             *     | |__   ___  ___ _ __ (_) |_ __ _| |___  ___| | ___  ___| |_
             *     | '_ \ / _ \/ __| '_ \| | __/ _` | / __|/ _ \ |/ _ \/ __| __|
             *     | | | | (_) \__ \ |_) | | || (_| | \__ \  __/ |  __/ (__| |_
             *     |_| |_|\___/|___/ .__/|_|\__\__,_|_|___/\___|_|\___|\___|\__|
             *                     |_|
             */
            if (tagName.equalsIgnoreCase("hospitalselect")) {
                groupname = attributes.getValue("name");
                final String thisGroupName = groupname;

                PnlHospital pnlHospital = new PnlHospital(o -> {
                    long hid;
                    String hText;
                    if (o == null) {
                        hid = -1;
                        hText = "--";
                    } else {
                        hid = ((Hospital) o).getKhid();
                        hText = HospitalTools.getCompleteAddress((Hospital) o);
                    }
                    content.put(thisGroupName + ".id", Long.toString(hid));
                    content.put(thisGroupName + ".text", hText);
                    changed = true;
                });

                int fontstyle = getFontStyle(attributes);

                String layout = SYSTools.catchNull(attributes.getValue("layout"), "br left");
                if (attributes.getValue("label") != null) {
                    JLabel jl = new JLabel(SYSTools.xx(attributes.getValue("label")) + ":");

                    jl.setFont(new Font(FONT, fontstyle, getFontSize(attributes)));

                    outerpanel.add(layout, jl);
                    layout = "left";
                }

                components.put(groupname, pnlHospital);
                outerpanel.add(layout, pnlHospital);
                addInfoButtons(outerpanel, attributes);
            }
            /***
             *                        __              _                  _      _
             *      __ ___ _ __ _  _ / _|_ _ ___ _ __| |_ ___ _ __  _ __| |__ _| |_ ___
             *     / _/ _ \ '_ \ || |  _| '_/ _ \ '  \  _/ -_) '  \| '_ \ / _` |  _/ -_)
             *     \__\___/ .__/\_, |_| |_| \___/_|_|_\__\___|_|_|_| .__/_\__,_|\__\___|
             *            |_|   |__/                               |_|
             */
            if (mode != CHANGE && tagName.equalsIgnoreCase("copyfromtemplate")) {

                JPanel pnl = new JPanel(new RiverLayout());

                ArrayList<ResInfo> listTemplates = ResInfoTools.getTemplatesByType(resInfo.getResident(), resInfo.getResInfoType().getType());
                final JComboBox cmb = new JComboBox();
                cmb.setModel(SYSTools.list2cmb(listTemplates));
                cmb.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
                    String text = "";
                    if (value instanceof ResInfo) {
                        text = ResidentTools.getFullName(((ResInfo) value).getResident());
                    } else {
                        text = SYSTools.catchNull(value);
                    }

                    return new DefaultListCellRenderer().getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
                });
                cmb.setSelectedIndex(-1);
                pnl.add("left hfill", cmb);

                JButton btnCopyOver = new JButton(SYSTools.xx("nursingrecords.info.dlg.copyfromtemplate"));
                btnCopyOver.addActionListener(e -> {
                    if (cmb.getSelectedItem() != null) {
                        ResInfo template = (ResInfo) cmb.getSelectedItem();
                        resInfo.setProperties(template.getProperties());
                        setContent();
                    }
                });
                pnl.add("left", btnCopyOver);

                outerpanel.add("left hfill", pnl);
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

            if (tagName.equalsIgnoreCase("tx")) {
                JLabel jl = new JLabel(SYSConst.findIcon(SYSConst.strIcon22ambulance));
                jl.setToolTipText(attributes.getValue("tooltip") == null ? null : SYSTools.toHTML("<p style=\"width:300px;\">" + SYSTools.xx(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')) + "</p>");
                outerpanel.add(jl);
            }
            if (tagName.equalsIgnoreCase("qdvs")) {
                JLabel jl = new JLabel(SYSConst.findIcon(SYSConst.icon22qi));
                jl.setToolTipText(attributes.getValue("tooltip") == null ? null : SYSTools.toHTML("<p style=\"width:300px;\">" + SYSTools.xx(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')) + "</p>");
                outerpanel.add(jl);
            }
            if (tagName.equalsIgnoreCase("bi")) {
                JLabel jl = new JLabel(SYSConst.findIcon(SYSConst.icon22bi));
                jl.setToolTipText(attributes.getValue("tooltip") == null ? null : SYSTools.toHTML("<p style=\"width:300px;\">" + SYSTools.xx(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')) + "</p>");
                outerpanel.add(jl);
            }

            /***
             *                 _
             *      _   _ _ __| |
             *     | | | | '__| |
             *     | |_| | |  | |
             *      \__,_|_|  |_|
             *
             */
            if (tagName.equalsIgnoreCase("url")) {
                JideButton link = GUITools.createHyperlinkButton(attributes.getValue("label"), SYSConst.icon16internet, e -> {
                    Desktop desktop = Desktop.getDesktop();
                    try {
                        desktop.browse(new URI(attributes.getValue("link")));
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    } catch (URISyntaxException use) {
                        use.printStackTrace();

                    }
                });
                link.setToolTipText(attributes.getValue("link"));
                String layout = SYSTools.catchNull(attributes.getValue("layout"), "br left");
                outerpanel.add(layout, link);
            }
            /***
             *      _                     _      _         _
             *     (_)_ __  __ _ __ _ ___| |__ _| |__  ___| |
             *     | | '  \/ _` / _` / -_) / _` | '_ \/ -_) |
             *     |_|_|_|_\__,_\__, \___|_\__,_|_.__/\___|_|
             *                  |___/
             */
            if (tagName.equalsIgnoreCase("imagelabel")) {
                groupname = attributes.getValue("name");
                JLabel jl = new JLabel(new ImageIcon(getClass().getResource(attributes.getValue("image"))));
                jl.setToolTipText(attributes.getValue("tooltip") == null ? null : SYSTools.toHTML("<p>" + SYSTools.xx(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')) + "</p>");
                String layout = SYSTools.catchNull(attributes.getValue("layout"), "p left");
                jl.setText(attributes.getValue("text"));
                outerpanel.add(layout, jl);
            }
            /***
             *      _      _         _
             *     | |__ _| |__  ___| |
             *     | / _` | '_ \/ -_) |
             *     |_\__,_|_.__/\___|_|
             * comp:label
             */
            if (tagName.equalsIgnoreCase("label")) {
                groupname = attributes.getValue("name") == null ? UUID.randomUUID().toString() : attributes.getValue("name");
                JLabel jl = new JLabel();
                jl.setName(groupname);

                if (!SYSTools.catchNull(attributes.getValue("color")).isEmpty()) {
                    jl.setForeground(GUITools.getColor(attributes.getValue("color")));
                }
                if (!SYSTools.catchNull(attributes.getValue("bgcolor")).isEmpty()) {
                    jl.setBackground(GUITools.getColor(attributes.getValue("bgcolor")));
                    jl.setOpaque(true);
                }

                jl.setFont(new Font(FONT, getFontStyle(attributes), getFontSize(attributes)));

                String content_for_label = SYSTools.xx(attributes.getValue("label"));
                if (!SYSTools.catchNull(attributes.getValue("parwidth")).isEmpty()) {
                    content_for_label = "<html><p style=\"width:" + attributes.getValue("parwidth") + ";\">" + content_for_label + "</p></html>";
                }
                jl.setText(content_for_label);


//                String layout = SYSTools.catchNull(attributes.getValue("layout"), "br left");
                createComponent(outerpanel, jl, attributes);
                //outerpanel.add(layout, jl);
                //addInfoButtons(outerpanel, attributes);
//                addDepenciesIfUsed(outerpanel, jl, attributes);
//                components.put(groupname, jl); // das ist nötig, damit die dependencies funktionieren.
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
                jcb.setOpaque(false);
                focusTraversal.add(jcb);
                jcb.setName(groupname);
                components.put(groupname, jcb);
                jcb.addItemListener(new ComboBoxItemStateListener());

                createComponent(outerpanel, jcb, attributes);

            }
            /***
             *      _ _
             *     (_) |_ ___ _ __
             *     | |  _/ -_) '  \
             *     |_|\__\___|_|_|_|
             *
             */
            if (tagName.equalsIgnoreCase("item")) {
                boxModel.addElement(new ComboBoxBean(SYSTools.xx(attributes.getValue("label")), attributes.getValue("name"), attributes.getValue("tooltip")));
                if (SYSTools.catchNull(attributes.getValue("default")).equals("true")) {
                    content.put(groupname, attributes.getValue("name"));
                }
            }
        }

        private void underline(JLabel jl) {
            // underline it
            Font original = jl.getFont();
            Map map = original.getAttributes();
            map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            jl.setFont(original.deriveFont(map));
        }

        private int getFontSize(Attributes attributes) {
            int size = 12;
            if (!SYSTools.catchNull(attributes.getValue("size")).isEmpty()) {
                size = Integer.parseInt(attributes.getValue("size"));

            }
            return size;
        }

        private int getFontStyle(Attributes attributes) {
            int fontstyle = Font.PLAIN;
            if (!SYSTools.catchNull(attributes.getValue("fontstyle")).isEmpty()) {
                if (attributes.getValue("fontstyle").equalsIgnoreCase("bold")) {
                    fontstyle = Font.BOLD;
                }
                if (attributes.getValue("fontstyle").equalsIgnoreCase("italic")) {
                    fontstyle = Font.ITALIC;
                }
            }

            return fontstyle;
        }

        /**
         * Erstellt ein kleines Panel für die Component und berücksichtig Labels und Layouts falls nötig.
         *
         * @param embedPanel
         * @param component
         * @param attributes
         */
        private void createComponent(JPanel embedPanel, Component component, Attributes attributes) {
            JPanel componentPanel = new JPanel(new RiverLayout());
            componentPanel.setOpaque(false);
            String layout = SYSTools.catchNull(attributes.getValue("layout"), "br left");

            // Checkboxen brauchen kein Label
            if (!(component instanceof JCheckBox) && !(component instanceof JLabel)) {
                JLabel jl = new JLabel(SYSTools.xx(attributes.getValue("label")) + ":");
                componentPanel.add(jl);
            }

            componentPanel.add("left", component);
            addInfoButtons(componentPanel, attributes);
            addDepenciesIfUsed(componentPanel, component, attributes);
            addLockforchangesIfUsed(component, attributes);
            embedPanel.add(layout, componentPanel);
        }

        /**
         * Falls nötig, werden Info Icons an die ResInfos angehangen. Das kann sein für
         * <li>Einfache Info-Symbole</li>
         * <li>Überleitbogen</li>
         * <li>Begutachrungsrelevante Eintröge</li>
         * <li>QDVS Indikatoren</li>
         *
         * @param pnl
         * @param attributes
         */
        private void addInfoButtons(JPanel pnl, Attributes attributes) {
            String tooltip = attributes.getValue("tooltip");
            String tx = attributes.getValue("tx");
            String bi = attributes.getValue("bi");
            String qdvs = attributes.getValue("qdvs");

            if (tooltip == null && tx == null && bi == null && qdvs == null) return;

            if (tooltip != null) {
                final JButton ttip = GUITools.getTinyButton(HTMLTools.toHTML(tooltip), SYSConst.findIcon(SYSConst.fontawesome_info_circle_o));
                ttip.addActionListener(e -> {
                    if (popupInfo != null) popupInfo.hidePopupImmediately();
                    popupInfo = createPopupInfo(HTMLTools.toHTML(prepareTooltip(tooltip)), ttip);
                    GUITools.showPopup(popupInfo, SwingConstants.SOUTH_WEST);
                });
                pnl.add("left", ttip);
            }

            if (tx != null) {
                final JButton btntx = GUITools.getTinyButton(HTMLTools.toHTML(tx), SYSConst.findIcon(SYSConst.strIcon16ambulance));
                btntx.addActionListener(e -> {
                    if (popupInfo != null) popupInfo.hidePopupImmediately();
                    popupInfo = createPopupInfo(HTMLTools.toHTML(prepareTooltip(tx)), btntx);
                    GUITools.showPopup(popupInfo, SwingConstants.SOUTH_WEST);
                });

                pnl.add("left", btntx);
            }

            if (bi != null) { // Begutachtungsinstrument
                final JButton btnbi = GUITools.getTinyButton(HTMLTools.toHTML(bi), SYSConst.findIcon(SYSConst.icon22bi));
                btnbi.addActionListener(e -> {
                    if (popupInfo != null) popupInfo.hidePopupImmediately();
                    popupInfo = createPopupInfo(HTMLTools.toHTML(prepareTooltip(bi)), btnbi);
                    GUITools.showPopup(popupInfo, SwingConstants.SOUTH_WEST);
                });


                pnl.add("left", btnbi);
            }

            if (qdvs != null) { // indikator
                final JButton btnqi = GUITools.getTinyButton(HTMLTools.toHTML(qdvs), SYSConst.findIcon(SYSConst.icon16qi));

                btnqi.addActionListener(e -> {
                    if (popupInfo != null) popupInfo.hidePopupImmediately();
                    popupInfo = createPopupInfo(HTMLTools.toHTML(prepareTooltip(qdvs)), btnqi);
                    popupInfo.setOwner(btnqi);
                    GUITools.showPopup(popupInfo, SwingConstants.SOUTH_WEST);
                });


                pnl.add("left", btnqi);
            }

            pnl.add("left", new JLabel(" "));

        }

        /**
         * Diese Methode erstellt eine Abhöngigkeit in der master_components Map, die in den Listenern später verwendet
         * wird.
         *
         * @param slave
         * @param slaves_attributes
         */
        private void addDepenciesIfUsed(JPanel slavePanel, Component slave, Attributes slaves_attributes) {
            String depends_on = slaves_attributes.getValue("depends-on");
            String enabled_when_eq = slaves_attributes.getValue("visible-when-dependency-eq");
            String enabled_when_neq = slaves_attributes.getValue("visible-when-dependency-neq");
            String enabled_when_leq = slaves_attributes.getValue("visible-when-dependency-leq");
            String value_when_shown = SYSTools.catchNull(slaves_attributes.getValue("default-value-when-shown"));
            String value_when_hidden = SYSTools.catchNull(slaves_attributes.getValue("default-value-when-hidden"));

            // hier wird der value ersetzt wenn es das keyword gibt, ansonsten wird er einfach durch sich selbst ersetzt.
            // wenn die Angabe fehlt, dann wird der value immer als leer angenommen.
            final Optional<String> default_value_when_shown = value_when_shown.isEmpty() ? Optional.empty() : Optional.of(keywords.getOrDefault(value_when_shown, value_when_shown).toString());
            final Optional<String> default_value_when_hidden = value_when_hidden.isEmpty() ? Optional.empty() : Optional.ofNullable(keywords.getOrDefault(value_when_hidden, value_when_shown).toString());

            if (depends_on != null) {
                master_components.putIfAbsent(depends_on, new MasterComponent(depends_on));

                if (enabled_when_eq != null) {
                    master_components.get(depends_on).getSlaveComponents().add(new SlaveComponent(slavePanel, slave, SlaveComponent.ACTION_ENABLE_EQ, enabled_when_eq));
                }
                if (enabled_when_neq != null) {
                    master_components.get(depends_on).getSlaveComponents().add(new SlaveComponent(slavePanel, slave, SlaveComponent.ACTION_ENABLE_NEQ, enabled_when_neq));
                }
                if (enabled_when_leq != null) {
                    master_components.get(depends_on).getSlaveComponents().add(new SlaveComponent(slavePanel, slave, SlaveComponent.ACTION_ENABLE_LEQ, enabled_when_leq));
                }
            }

            // hier werden die Standard-Werte gesetzt wenn sich der Status HIDDEN oder SHOWN ändert.
            // Passiert nur, wenn Dependencies betroffen sind.
            slavePanel.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentHidden(ComponentEvent e) {
                    getLogger().debug(slave.getName() + " is hidden");
                    if (default_value_when_hidden.isPresent()) {
                        content.put(slave.getName(), default_value_when_hidden.get());
                        setContentForComponent(slave.getName());
                    } else {
                        content.put(slave.getName(), "");
                    }
                }

                @Override
                public void componentShown(ComponentEvent e) {
                    getLogger().debug(slave.getName() + " is shown");
                    if (default_value_when_shown.isPresent()) {
                        content.put(slave.getName(), default_value_when_shown.get());
                        setContentForComponent(slave.getName());
                    } else {
                        content.put(slave.getName(), "");
                    }
                }
            });
        }


        /**
         * Manchmal müssen Veränderungen bei einem CHANGE verhindert werden. Das war bisher nur bei der Einteilung in
         * Wund-Kategorien so.
         *
         * @param comp
         * @param slaves_attributes
         */
        private void addLockforchangesIfUsed(Component comp, Attributes slaves_attributes) {
            String locked = slaves_attributes.getValue("lockedforchanges");
            if (locked != null && locked.equalsIgnoreCase("true")) {
                lockedforchanges.add(comp.getName());
            }
        }

//        private int getFontStyle(Attributes attributes) {
//            int fontstyle = Font.PLAIN;
//            if (!SYSTools.catchNull(attributes.getValue("fontstyle")).isEmpty()) {
//                if (attributes.getValue("fontstyle").equalsIgnoreCase("bold")) {
//                    fontstyle = Font.BOLD;
//                }
//                if (attributes.getValue("fontstyle").equalsIgnoreCase("italic")) {
//                    fontstyle = Font.ITALIC;
//                }
//            }
//            return fontstyle;
//        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (qName.equalsIgnoreCase("optiongroup") || qName.equalsIgnoreCase("scalegroup")) {
                outerpanel.add("br", innerpanel);
            }
            if (qName.equalsIgnoreCase("scale")) {
                outerpanel.add("p hfill", new JSeparator());

                sumlabel = new JTextArea();
                sumlabel.setRows(3);
                sumlabel.setEditable(false);

                sumlabel.setFont(SYSConst.ARIAL20BOLD);
                outerpanel.add("br hfill", new JScrollPane(sumlabel));
            }
            if (qName.equalsIgnoreCase("combobox")) {
                JComboBox j = (JComboBox) components.get(groupname);
                j.setModel(boxModel);
                ComboBoxBean bean = (ComboBoxBean) j.getSelectedItem();
                j.setToolTipText(bean.getTooltip());
                content.put(j.getName(), bean.getName());
            }
        }

        public void endDocument() {
            // adding a focusgained listener to all JComponents
            for (final Object key : components.keySet()) {
                if (components.get(key) instanceof JComponent) {
                    ((JComponent) components.get(key)).addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            focusOwner = (JComponent) e.getSource();
                        }
                    });
                }
            }
        }

        public JPanel getPanel() {
            if (background != null) {
                outerpanel.setOpaque(false);
            }
            return this.outerpanel;
        }


    } // private class HandlerDatenStruktur


    /**
     * Das hier ist eine Hilfsklasse, die ich als Payload für Comboboxes verwende.
     */
    class ComboBoxBean {
        private String label, name, tooltip;

        @Override
        public String toString() {
            return label;
        }

        public ComboBoxBean(String label, String name, String tooltip) {
            this.label = label;
            this.name = name;
            this.tooltip = SYSTools.toHTMLForScreen("<p style=\"width:300px;\">" + SYSTools.catchNull(tooltip).replace('[', '<').replace(']', '>') + "</p>");
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
            return SYSTools.xx(tooltip);
        }


        @Override
        public boolean equals(Object obj) {
            return name.equals(((ComboBoxBean) obj).getName());
        }
    }

    /**
     * Container Bean für die Affected Components
     */
    class MasterComponent {
        private final String myComponentKey;
        private final ArrayList<SlaveComponent> slaveComponents;

        public MasterComponent(String myComponentKey) {
            this.myComponentKey = myComponentKey;
            slaveComponents = new ArrayList<>();
        }

        public String getMyComponentKey() {
            return myComponentKey;
        }

        public ArrayList<SlaveComponent> getSlaveComponents() {
            return slaveComponents;
        }

    }

    class SlaveComponent {
        static final int ACTION_ENABLE_EQ = 0; // einschalten wenn der wert GLEICH ist
        static final int ACTION_ENABLE_NEQ = 1; // einschalten wenn der wert UNGLEICH ist
        static final int ACTION_ENABLE_LEQ = 2; // einschalten wenn der wert KLEINER GLEICH

        private final JPanel panelContainingSlave;
        private final Component me;
        private final int action; // was mache ich dann ?
        private final String condition;

        public SlaveComponent(JPanel panelContainingSlave, Component me, int action, String condition) {
            this.panelContainingSlave = panelContainingSlave;
            this.me = me;
            this.action = action;
            this.condition = condition;
        }

        public void setVisible(String value) {
            getLogger().debug("component: " + me.getName());
            getLogger().debug("submitted value: " + value);

            if (action == ACTION_ENABLE_EQ) {
                panelContainingSlave.setVisible(condition.equals(value));
            }
            if (action == ACTION_ENABLE_NEQ) {
                panelContainingSlave.setVisible(!condition.equals(value));
            }
            if (action == ACTION_ENABLE_LEQ) {
                int int_condition = Integer.valueOf(condition);
                int int_value = Integer.valueOf(value);
                panelContainingSlave.setVisible(int_value <= int_condition);
            }
            getLogger().debug("JUST CHANGED MY VISIBILTY TO " + panelContainingSlave.isVisible());
        }
    }


}
