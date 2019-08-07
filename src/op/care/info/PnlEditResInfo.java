package op.care.info;

import com.itextpdf.text.Image;
import com.itextpdf.text.*;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.OverlayTextArea;
import entity.EntityTools;
import entity.building.Rooms;
import entity.building.RoomsTools;
import entity.files.SYSFilesTools;
import entity.info.ResInfo;
import entity.info.ResInfoTools;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.prescription.GP;
import entity.prescription.GPTools;
import entity.prescription.Hospital;
import entity.prescription.HospitalTools;
import entity.values.ResValue;
import entity.values.ResValueTools;
import entity.values.ResValueTypes;
import entity.values.ResValueTypesTools;
import gui.GUITools;
import op.OPDE;
import op.system.PDF;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;
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
import java.util.*;

/**
 * Created with IntelliJ IDEA. User: tloehr Date: 11.05.13 Time: 13:57 To change this template use File | Settings |
 * File Templates.
 */
public class PnlEditResInfo implements HasLogger {

    public static final int EDIT = 0;
    public static final int CHANGE = 1;
    public static final int NEW = 2;
    public static final int DISPLAY = 3;

    public static final String internalClassID = "nursingrecords.info.dlg";
    private final int TYPE_DONT_CARE = 0;
    private final int TYPE_INT = 1;
    private final int TYPE_DOUBLE = 2;
    private final int TYPE_DATE = 3;
    private final int TYPE_TIME = 4;
    private int mode;
    private boolean scalemode;
    //    private ResValue scaleValue = null;
    private final int TEXTFIELD_STANDARD_WIDTH = 35;

    boolean initPanel = false;
    Properties content;
    private JTextArea sumlabel;
    private Component focusOwner = null;

    public boolean isEnabled() {
        return enabled;
    }

    private boolean enabled;

    private ArrayList<RiskBean> scaleriskmodel;
    private String scalesumlabeltext;
    private ArrayList<String> scaleButtonGroups, defaultdisabled; // eine Liste mit den Namen der Buttongroups eines scales;
    private HashMap<String, Object> components;
    private ArrayList<String> lockedforchanges;
    private ArrayList<JComponent> focusTraversal;
    private HashMap<String, ArrayList<String>> enables, disables;
    private HashMap<String, String> enabledBy, disabledBy;
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
        this.resInfo = resInfo;
        this.closure = closure;
        this.mode = DISPLAY;
        if (basecolor != null) {
            background = GUITools.blend(basecolor, Color.WHITE, 0.1f);
        }
        initPanel(resInfo.getResInfoType().getXml());
    }

    public void addInfoButtons(JPanel pnl, String tooltip, String tx) {
        if (tooltip == null && tx == null) return;


        final JLabel txt = new JLabel();

        final JidePopup popupInfo = new JidePopup();
        popupInfo.setMovable(false);

        JScrollPane scrl = new JScrollPane(txt);
        scrl.setMaximumSize(new Dimension(550, 300));

        popupInfo.setContentPane(scrl);
        popupInfo.removeExcludedComponent(txt);
        popupInfo.setDefaultFocusComponent(txt);


        if (tooltip != null) {
            tooltip = SYSTools.xx(tooltip);
            tooltip = tooltip.replace('[', '<').replace(']', '>');

            if (tooltip.indexOf("<p>") < 0 && tooltip.indexOf("<li>") < 0) {
                tooltip = HTMLTools.p(tooltip);
            }

            tooltip = tooltip.replace("<p>", "<p style=\"width:300px;\">");
            tooltip = tooltip.replace("<li>", "<li style=\"width:300px;\">");


            final JButton ttip = GUITools.getTinyButton(HTMLTools.toHTML(tooltip), SYSConst.findIcon(SYSConst.fontawesome_info_circle_o));
            txt.setText(HTMLTools.toHTML(tooltip));

            ttip.addActionListener(e -> {
                popupInfo.setOwner(ttip);
                GUITools.showPopup(popupInfo, SwingConstants.SOUTH_WEST);
            });
            pnl.add("left", ttip);
        }

        if (tx != null) {

            tx = tx.replace('[', '<').replace(']', '>');

            if (tx.indexOf("<p>") < 0 && tx.indexOf("<li>") < 0) {
                tx = "<p>" + tx + "</p>";
            }

            tx = tx.replace("<p>", "<p style=\"width:300px;\">");
            tx = tx.replace("<li>", "<li style=\"width:300px;\">");

            final JButton btntx = GUITools.getTinyButton(HTMLTools.toHTML(tx), SYSConst.icon16ambulance);
            txt.setText(HTMLTools.toHTML(tx));

            btntx.addActionListener(e -> {
                popupInfo.setOwner(btntx);
                GUITools.showPopup(popupInfo, SwingConstants.SOUTH_WEST);
            });


            pnl.add("left", btntx);
        }

        pnl.add("left", new JLabel(" "));
    }

    /**
     * only for development reasons
     *
     * @param xml
     */
    public PnlEditResInfo(String xml, Closure closure) {
        this.resInfo = null;
        this.closure = closure;
        this.mode = NEW;
        initPanel(xml);
    }

    public void cleanup() {
//        focusTraversal.clear();
//        enables.clear();
//        disables.clear();
//        lockedforchanges.clear();
//        components.clear();
//        enabledBy.clear();
//        disabledBy.clear();
    }

    private void initPanel(String xml) {
        content = new Properties();
        focusTraversal = new ArrayList<JComponent>();
        lockedforchanges = new ArrayList<String>();
        enables = new HashMap<String, ArrayList<String>>();
        disables = new HashMap<String, ArrayList<String>>();
        defaultdisabled = new ArrayList<String>();
        enabledBy = new HashMap<String, String>();
        disabledBy = new HashMap<String, String>();

        pnlContent = new JPanel(new BorderLayout());
        initPanel = true;
        lastParsingException = null;

        // Structure...
        try {
            String xmltext = "<?xml version=\"1.0\"?><structure>" + xml + "</structure>";
            XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
            InputSource is = new org.xml.sax.InputSource(new java.io.BufferedReader(new java.io.StringReader(xmltext)));

            HandlerDatenStruktur h = new HandlerDatenStruktur();
            parser.setContentHandler(h);

            parser.parse(is);

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
            if (resInfo != null && resInfo.getResValue() != null) {
                pnlContent.add(new JLabel(SYSTools.xx("nursingrecords.info.dlg.will.create.value")), BorderLayout.NORTH);
            }

            pnlContent.add(h.getPanel(), BorderLayout.CENTER);
            pnlContent.add(new JScrollPane(ovrComment), BorderLayout.SOUTH);

        } catch (SAXException ex1) {
            ex1.printStackTrace();
            lastParsingException = ex1;
        } catch (IOException ex) {
            ex.printStackTrace();
            lastParsingException = ex;
        }


        if (resInfo != null) {
            // ... and content
            setContent();
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

        JButton apply1 = GUITools.getTinyButton(null, SYSConst.icon22apply);
        apply1.addActionListener(e -> {
            if (resInfo != null) {
                closure.execute(getResInfo());
            } else {
                closure.execute(content);
            }
            cleanup();
        });
        upperButtonBanel.add(apply1);

        JButton apply2 = GUITools.getTinyButton(null, SYSConst.icon22apply);
        apply2.addActionListener(e -> {
            if (resInfo != null) {
                closure.execute(getResInfo());
            } else {
                closure.execute(content);
            }
            cleanup();
        });
        lowerButtonBanel.add(apply2);


        JButton cancel1 = GUITools.getTinyButton(null, SYSConst.icon22cancel);
        cancel1.addActionListener(e -> cancel());
        upperButtonBanel.add(cancel1);


        JButton cancel2 = GUITools.getTinyButton(null, SYSConst.icon22cancel);
        cancel2.addActionListener(e -> cancel());
        lowerButtonBanel.add(cancel2);


        enclosingUpperButtonPanel.add(upperButtonBanel, BorderLayout.LINE_START);
        main.add(enclosingUpperButtonPanel, BorderLayout.NORTH);

        enclosingLowerButtonPanel.add(lowerButtonBanel, BorderLayout.LINE_END);
        main.add(enclosingLowerButtonPanel, BorderLayout.SOUTH);


//        JPanel hdrPanel = new JPanel(new BorderLayout());
//        JLabel jl = new JLabel(resInfo != null ? resInfo.getResInfoType().getShortDescription() : "dev");
//        jl.setFont(SYSConst.ARIAL24BOLD);
//        hdrPanel.add(jl, BorderLayout.CENTER);
//        hdrPanel.add(new JSeparator(), BorderLayout.SOUTH);
//        main.add(jl, BorderLayout.NORTH);

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
    }

    public void cancel() {
        closure.execute(null);
        cleanup();
    }

    public Exception getLastParsingException() {
        return lastParsingException;
    }

    /**
     * retrieves presets from various locations in the database. mainly from ResValue.
     *
     * @param preset
     * @param deflt
     * @return
     */
    private String getPreset(String preset, String deflt) {
        String d = SYSTools.catchNull(deflt);
//        NumberFormat nf = DecimalFormat.getNumberInstance();
//        nf.setMinimumFractionDigits(2);
//        nf.setMaximumFractionDigits(2);
//        nf.setGroupingUsed(false);

        if (SYSTools.catchNull(preset).isEmpty()) {
            return d;
        }

        Resident resident;
        if (resInfo == null) {
            resident = ResidentTools.getAllActive().get(0);  // only for development reasons
        } else {
            resident = resInfo.getResident();
        }

        if (preset.equalsIgnoreCase("currenttime")) {
            return DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());
        }

        if (preset.equalsIgnoreCase("currentdate")) {
            return DateFormat.getDateInstance(DateFormat.DEFAULT).format(new Date());
        }

        if (preset.equalsIgnoreCase("heightlast")) {
            ResValue r = ResValueTools.getLast(resident, ResValueTypesTools.HEIGHT);
            return r == null ? d : SYSTools.formatBigDecimal(r.getVal1());
        }
        if (preset.equalsIgnoreCase("weightlast")) {
            ResValue r = ResValueTools.getLast(resident, ResValueTypesTools.WEIGHT);
            return r == null ? d : SYSTools.formatBigDecimal(r.getVal1());
        }
        if (preset.equalsIgnoreCase("weight-1m")) {
            long target = new DateTime().minusMonths(1).getMillis();
            ArrayList<ResValue> list = ResValueTools.getResValues(resident, ResValueTypesTools.WEIGHT, new LocalDate().minusDays(45), new LocalDate().minusDays(15));

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
            ArrayList<ResValue> list = ResValueTools.getResValues(resident, ResValueTypesTools.WEIGHT, new LocalDate().minusMonths(6).minusDays(15), new LocalDate().minusMonths(5).minusDays(15));

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
            ArrayList<ResValue> list = ResValueTools.getResValues(resident, ResValueTypesTools.WEIGHT, new LocalDate().minusYears(1).minusDays(15), new LocalDate().minusMonths(11).minusDays(15));

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
            setXEnabled(main, enabled);

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

    private ResInfo getResInfo() {
        try {
            StringWriter writer = new StringWriter();

//            // remove content form disabled controls
            for (String key : content.stringPropertyNames()) {

                if (components.containsKey(key) && components.get(key) instanceof Component && !((Component) components.get(key)).isEnabled()) {
                    content.remove(key);
                    getLogger().debug("removing content for: " + key);
                }
            }

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

    public void setEnabled(boolean enabled, int mode) {
        this.mode = mode;
        this.enabled = enabled;
        setXEnabled(main, enabled);
    }


    /**
     * läuft rekursiv durch alle Kinder eines Containers und setzt deren Enabled Status auf enabled.
     */
    private void setXEnabled(JComponent container, boolean enabled) {
        // Bei einer Combobox muss die Rekursion ebenfalls enden.
        // Sie besteht aus weiteren Unterkomponenten
        // "disabled" wird sie aber bereits hier.
        if (container.getComponentCount() == 0 || container instanceof JComboBox) {
            // Rekursionsanker
            container.setEnabled(enabled);
        } else {
            Component[] c = container.getComponents();
            for (int i = 0; i < c.length; i++) {
                if (c[i] instanceof JComponent) {
                    JComponent jc = (JComponent) c[i];

                    // OPDE.debug(SYSTools.catchNull(jc.getName()));

                    if (!enabled) {
                        setXEnabled(jc, enabled);
                    } else if (mode == CHANGE && lockedforchanges.contains(jc.getName())) {
                        setXEnabled(jc, false);
                    } else if (mode == NEW && defaultdisabled.contains(jc.getName())) {
                        setXEnabled(jc, false);
                    } else if (enabledBy.containsKey(jc.getName())) {
                        setXEnabled(jc, ((JCheckBox) components.get(enabledBy.get(jc.getName()))).isSelected());
                    } else if (disabledBy.containsKey(jc.getName())) {
                        setXEnabled(jc, !((JCheckBox) components.get(disabledBy.get(jc.getName()))).isSelected());
                    } else {
                        setXEnabled(jc, enabled);
                    }
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
     * tells whether the user has changed the data or not.
     *
     * @return
     */
    public boolean isChanged() {
        return changed;
    }

    private void setContent() {
        txtComment.setText(SYSTools.catchNull(resInfo.getText()));
        if (SYSTools.catchNull(resInfo.getProperties()).isEmpty()) {
            return;
        }

        try {
            StringReader reader = new StringReader(resInfo.getProperties());
            content.load(reader);
            reader.close();
        } catch (IOException ex) {
            OPDE.fatal(ex);
        }

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
//            OPDE.debug(((JCheckBox) e.getSource()).getName() + " clicked");
            content.put(name + "." + ((JCheckBox) e.getSource()).getName(), Boolean.toString(((JCheckBox) e.getSource()).isSelected()));
            changed = true;
        }
    }

    private class CheckBoxItemListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent evt) {
            JCheckBox j = (JCheckBox) evt.getSource();
            String cbname = j.getName();
            content.put(cbname, Boolean.toString(j.isSelected()));
            changed = true;

            if (enables.containsKey(cbname)) {
                for (String key : enables.get(cbname)) {
                    if (components.containsKey(key) && components.get(key) instanceof Component) {
                        ((Component) components.get(key)).setEnabled(j.isSelected());
                        if (components.get(key) instanceof JTextComponent) {
                            ((JTextComponent) components.get(key)).setText("");
                        }

                    }
                }
            }
            if (disables.containsKey(cbname)) {
                for (String key : disables.get(cbname)) {
                    if (components.containsKey(key) && components.get(key) instanceof Component) {
                        boolean disable = j.isSelected();
                        ((Component) components.get(key)).setEnabled(!disable);
                        if (components.get(key) instanceof JTextComponent) {
                            ((JTextComponent) components.get(key)).setText(null);
                        }
//                        ((Component) components.get(key)).action();
//                        ((Component) components.get(key)).repaint();
                    }
                }
            }
        }

    }


    private class ComboBoxItemStateListener implements ItemListener {

        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            if (evt.getStateChange() != ItemEvent.SELECTED) return;
            JComboBox j = (JComboBox) evt.getSource();
            if (!j.isEnabled()) return;

            ComboBoxBean bean = (ComboBoxBean) j.getSelectedItem();
            content.put(j.getName(), bean.getName());
            j.setToolTipText(bean.getTooltip());
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
            JTextComponent j = (JTextComponent) e.getSource();
            String text = ((JTextComponent) e.getSource()).getText();

            if (type != TYPE_DONT_CARE) {

                if (optional && text.trim().isEmpty()) {
                    // nop!
                } else if (type == TYPE_DATE) {
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

            // set a title
            JLabel jl = new JLabel(resInfo != null ? resInfo.getResInfoType().getShortDescription() : "dev");
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
                        jl.setFont(new Font("Arial", fontstyle, 14));
                    }

                    // underline it
                    Font original = jl.getFont();
                    Map map = original.getAttributes();
                    map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                    jl.setFont(original.deriveFont(map));

                    outerpanel.add("p left", jl);
                    addInfoButtons(outerpanel, attributes.getValue("tooltip"), attributes.getValue("tx"));
                }
            }
            if (tagName.equalsIgnoreCase("scale")) {
                scalemode = true;
                scalesumlabeltext = SYSTools.xx(attributes.getValue("label"));
                scaleButtonGroups = new ArrayList();
                scaleriskmodel = new ArrayList();
                try {
                    ResValueTypes scaleValueType = ResValueTypesTools.getType(Short.parseShort(SYSTools.catchNull(attributes.getValue("resvaltype"))));
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
             */
            if (tagName.equalsIgnoreCase("tabgroup")) {
                JLabel jl = new JLabel(SYSTools.xx(attributes.getValue("label")));
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
                    jl.setFont(new Font("Arial", fontstyle, 14));
                }

                // underline it
                Font original = jl.getFont();
                Map map = original.getAttributes();
                map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                jl.setFont(original.deriveFont(map));

//                jl.setToolTipText(attributes.getValue("tooltip") == null ? null : SYSTools.toHTML("<p>" + SYSTools.xx(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')) + "</p>");
                String layout = SYSTools.catchNull(attributes.getValue("layout"), "br left");
                outerpanel.add(layout, jl);
                addInfoButtons(outerpanel, attributes.getValue("tooltip"), attributes.getValue("tx"));
                tabgroup = true;
            }
            /***
             *               _   _
             *      ___ _ __| |_(_)___ _ _
             *     / _ \ '_ \  _| / _ \ ' \
             *     \___/ .__/\__|_\___/_||_|
             *         |_|
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
                addInfoButtons(innerpanel, attributes.getValue("tooltip"), attributes.getValue("tx"));

                j.addActionListener(new RadioButtonActionListener());
                if (scalemode) {
                    components.put(groupname + ":" + compName, new Pair<JRadioButton, BigDecimal>(j, score)); // Hier weichen wir vom üblichen SChema ab und übergeben nicht nur die Component sondern auch den Score.
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
             *
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

                String layout = SYSTools.catchNull(attributes.getValue("layout"), tabgroup ? "tab" : "br left");
                outerpanel.add(layout, j);
                addInfoButtons(outerpanel, attributes.getValue("tooltip"), attributes.getValue("tx"));

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
                    j.setFont(new Font("Arial", fontstyle, size));
                } else {
                    j.setFont(new Font("Arial", fontstyle, 12));
                }

                if (attributes.getValue("default") != null && attributes.getValue("default").equals("true")) {
                    j.setSelected(true);
                }
                content.put(groupname, (j.isSelected() ? "true" : "false"));
                if (SYSTools.catchNull(attributes.getValue("lockedforchanges"), "false").equalsIgnoreCase("true")) {
                    lockedforchanges.add(groupname);
                }
                if (!SYSTools.catchNull(attributes.getValue("enables")).isEmpty()) {
                    StringTokenizer st = new StringTokenizer(attributes.getValue("enables"), ",");
                    while (st.hasMoreTokens()) {
                        if (!enables.containsKey(groupname)) {
                            enables.put(groupname, new ArrayList<String>());
                        }
                        String token = st.nextToken();
                        enables.get(groupname).add(token);
                        enabledBy.put(token, groupname);
                    }
                }
                if (!SYSTools.catchNull(attributes.getValue("disables")).isEmpty()) {
                    StringTokenizer st = new StringTokenizer(attributes.getValue("disables"), ",");
                    while (st.hasMoreTokens()) {
                        if (!disables.containsKey(groupname)) {
                            disables.put(groupname, new ArrayList<String>());
                        }
                        String token = st.nextToken();
                        disables.get(groupname).add(token);
                        disabledBy.put(token, groupname);
                    }
                }
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
                        tffl = new TextFieldFocusListener(TYPE_DATE, new Pair<DateTime, DateTime>(new DateTime(), new DateTime(SYSConst.DATE_UNTIL_FURTHER_NOTICE)), optional);
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
                JLabel jl = new JLabel(SYSTools.xx(attributes.getValue("label")) + ":");

                // https://github.com/tloehr/Offene-Pflege.de/issues/110
//                JTextComponent j = new JTextField(length);


//                JTextComponent j = new JTextArea("");
//                JScrollPane scrlpane = new JScrollPane(j);

                JTextField j = new JTextField(length);
                j.setOpaque(false);
                j.setDisabledTextColor(Color.DARK_GRAY);
                focusTraversal.add(j);
                j.setName(groupname);


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
                    j.setFont(new Font("Arial", fontstyle, size));
                    jl.setFont(new Font("Arial", fontstyle, size));
                } else {
                    j.setFont(new Font("Arial", fontstyle, 12));
                    jl.setFont(new Font("Arial", fontstyle, 12));
                }


                String layout = SYSTools.catchNull(attributes.getValue("layout"), "br left"); // br left
                outerpanel.add(layout, jl);


                String innerlayout = SYSTools.catchNull(attributes.getValue("innerlayout"), "left" + hfill);
                outerpanel.add(innerlayout, j);

                addInfoButtons(outerpanel, attributes.getValue("tooltip"), attributes.getValue("tx"));

                components.put(groupname, j); // für den späteren Direktzugriff
                j.addFocusListener(tffl);
                //                j.addCaretListener(new TextFieldCaretListener(type, notempty));
//                String defaultText = SYSTools.catchNull(attributes.getValue("default"));
                if (mode != CHANGE) {
                    j.setText(getPreset(attributes.getValue("preset"), attributes.getValue("default")));
                }
                content.put(groupname, j.getText());
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

                int fontstyle = Font.PLAIN;
                if (!SYSTools.catchNull(attributes.getValue("fontstyle")).isEmpty()) {
                    if (attributes.getValue("fontstyle").equalsIgnoreCase("bold")) {
                        fontstyle = Font.BOLD;
                    }
                    if (attributes.getValue("fontstyle").equalsIgnoreCase("italic")) {
                        fontstyle = Font.ITALIC;
                    }
                }

                String layout = SYSTools.catchNull(attributes.getValue("layout"), "br left");
                if (attributes.getValue("label") != null) {
                    JLabel jl = new JLabel(SYSTools.xx(attributes.getValue("label")) + ":");

                    if (!SYSTools.catchNull(attributes.getValue("size")).isEmpty()) {
                        int size = Integer.parseInt(attributes.getValue("size"));
                        jl.setFont(new Font("Arial", fontstyle, size));
                    } else {
                        jl.setFont(new Font("Arial", fontstyle, 12));
                    }

                    outerpanel.add(layout, jl);
                    layout = "left";
                }

                components.put(groupname, pnlGP);
                outerpanel.add(layout, pnlGP);
                addInfoButtons(outerpanel, attributes.getValue("tooltip"), attributes.getValue("tx"));
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

                DefaultComboBoxModel<Rooms> dcmb = SYSTools.list2cmb(RoomsTools.getAllActive());
                dcmb.insertElementAt(null, 0);

                JComboBox<Rooms> cmbRooms = new JComboBox<>(dcmb);
                cmbRooms.setSelectedIndex(0);

                cmbRooms.setRenderer(RoomsTools.getRenderer());

                cmbRooms.addItemListener(e -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        long rid;
                        String roomText;
                        if (e.getItem() == null) {
                            rid = -1;
                            roomText = "--";
                        } else {
                            rid = ((Rooms) e.getItem()).getRoomID();
                            roomText = ((Rooms) e.getItem()).toString();
                        }
                        content.put(thisGroupName + ".id", Long.toString(rid));
                        content.put(thisGroupName + ".text", roomText);
                        changed = true;
                    }
                });
                pnlRoom.add(cmbRooms, BorderLayout.CENTER);

                int fontstyle = Font.PLAIN;
                if (!SYSTools.catchNull(attributes.getValue("fontstyle")).isEmpty()) {
                    if (attributes.getValue("fontstyle").equalsIgnoreCase("bold")) {
                        fontstyle = Font.BOLD;
                    }
                    if (attributes.getValue("fontstyle").equalsIgnoreCase("italic")) {
                        fontstyle = Font.ITALIC;
                    }
                }

                String layout = SYSTools.catchNull(attributes.getValue("layout"), "br left");
                if (attributes.getValue("label") != null) {
                    JLabel jl = new JLabel(SYSTools.xx(attributes.getValue("label")) + ":");

                    if (!SYSTools.catchNull(attributes.getValue("size")).isEmpty()) {
                        int size = Integer.parseInt(attributes.getValue("size"));
                        jl.setFont(new Font("Arial", fontstyle, size));
                    } else {
                        jl.setFont(new Font("Arial", fontstyle, 12));
                    }

                    outerpanel.add(layout, jl);
                    layout = "left";
                }

                components.put(groupname, pnlRoom);
                outerpanel.add(layout, pnlRoom);
                addInfoButtons(outerpanel, attributes.getValue("tooltip"), attributes.getValue("tx"));
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

                int fontstyle = Font.PLAIN;
                if (!SYSTools.catchNull(attributes.getValue("fontstyle")).isEmpty()) {
                    if (attributes.getValue("fontstyle").equalsIgnoreCase("bold")) {
                        fontstyle = Font.BOLD;
                    }
                    if (attributes.getValue("fontstyle").equalsIgnoreCase("italic")) {
                        fontstyle = Font.ITALIC;
                    }
                }

                String layout = SYSTools.catchNull(attributes.getValue("layout"), "br left");
                if (attributes.getValue("label") != null) {
                    JLabel jl = new JLabel(SYSTools.xx(attributes.getValue("label")) + ":");

                    if (!SYSTools.catchNull(attributes.getValue("size")).isEmpty()) {
                        int size = Integer.parseInt(attributes.getValue("size"));
                        jl.setFont(new Font("Arial", fontstyle, size));
                    } else {
                        jl.setFont(new Font("Arial", fontstyle, 12));
                    }

                    outerpanel.add(layout, jl);
                    layout = "left";
                }

                components.put(groupname, pnlHospital);
                outerpanel.add(layout, pnlHospital);
                addInfoButtons(outerpanel, attributes.getValue("tooltip"), attributes.getValue("tx"));
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
            /***
             *      _   _                       _         _
             *     | |_(_)_ _ _  _   __ _ _ __ | |__ _  _| |__ _ _ _  __ ___   __ __ _ _ _
             *     |  _| | ' \ || | / _` | '  \| '_ \ || | / _` | ' \/ _/ -_) / _/ _` | '_|
             *      \__|_|_||_\_, | \__,_|_|_|_|_.__/\_,_|_\__,_|_||_\__\___| \__\__,_|_|
             *                |__/
             */
            if (tagName.equalsIgnoreCase("tx")) {
                JLabel jl = new JLabel(SYSConst.icon22ambulance);
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
                JLabel jl = new JLabel(new javax.swing.ImageIcon(getClass().getResource(attributes.getValue("image"))));
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
             *
             */
            if (tagName.equalsIgnoreCase("label")) {
                //groupname = attributes.getValue("name");
                groupname = null;
                JLabel jl = new JLabel();

                if (!SYSTools.catchNull(attributes.getValue("color")).isEmpty()) {
                    jl.setForeground(GUITools.getColor(attributes.getValue("color")));
                }
                if (!SYSTools.catchNull(attributes.getValue("bgcolor")).isEmpty()) {
                    jl.setBackground(GUITools.getColor(attributes.getValue("bgcolor")));
                    jl.setOpaque(true);
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

                String content_for_label = SYSTools.xx(attributes.getValue("label"));
                if (!SYSTools.catchNull(attributes.getValue("parwidth")).isEmpty()) {
                    content_for_label = "<html><p style=\"width:" + attributes.getValue("parwidth") + ";\">" + content_for_label + "</p></html>";
                }
                jl.setText(content_for_label);


//                jl.setToolTipText(attributes.getValue("tooltip") == null ? null : SYSTools.toHTML("<p>" + SYSTools.xx(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')) + "</p>");
//                jl.setToolTipText(SYSTools.toHTML(SYSTools.xx(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')));
                String layout = SYSTools.catchNull(attributes.getValue("layout"), "br left");
                outerpanel.add(layout, jl);
                addInfoButtons(outerpanel, attributes.getValue("tooltip"), attributes.getValue("tx"));
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
//                jcb.setToolTipText(attributes.getValue("tooltip") == null ? null : SYSTools.toHTML("<p>" + SYSTools.xx(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')) + "</p>");
                components.put(groupname, jcb);
                jcb.addItemListener(new ComboBoxItemStateListener());
                JLabel jl = new JLabel(SYSTools.xx(attributes.getValue("label")) + ":");
                String layout = SYSTools.catchNull(attributes.getValue("layout"), "br left");
                outerpanel.add(layout, jl);
                outerpanel.add("left", jcb);
                addInfoButtons(outerpanel, attributes.getValue("tooltip"), attributes.getValue("tx"));
//                if (SYSTools.catchNull(attributes.getValue("defaultdisabled"), "false").equalsIgnoreCase("true")) {
//                    defaultdisabled.add(groupname);
//                }
                if (SYSTools.catchNull(attributes.getValue("lockedforchanges"), "false").equalsIgnoreCase("true")) {
                    lockedforchanges.add(groupname);
                }
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

            if (SYSTools.catchNull(attributes.getValue("defaultdisabled"), "false").equalsIgnoreCase("true")) {
                defaultdisabled.add(groupname);
            }
        }

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


}
