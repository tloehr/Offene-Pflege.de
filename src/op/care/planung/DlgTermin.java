/*
 * OffenePflege
 * Copyright (C) 2008 Torsten Löhr
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License V2 as published by the Free Software Foundation
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even 
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to 
 * the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
 * www.offene-pflege.de
 * ------------------------ 
 * Auf deutsch (freie Übersetzung. Rechtlich gilt die englische Version)
 * Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License, 
 * wie von der Free Software Foundation veröffentlicht, weitergeben und/oder modifizieren, gemäß Version 2 der Lizenz.
 *
 * Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, daß es Ihnen von Nutzen sein wird, aber 
 * OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN 
 * BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, 
 * schreiben Sie an die Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA.
 * 
 */
package op.care.planung;

import op.OPDE;
import op.care.planung.massnahmen.DlgNode;
import op.care.planung.massnahmen.ParserMassnahmen;
import op.care.planung.massnahmen.RNDMassTree;
import op.care.planung.massnahmen.Tools;
import op.tools.*;
import op.tools.DBHandling;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author tloehr
 */
public class DlgTermin extends javax.swing.JDialog {

    private static int TAB_TERMIN = 0;
    private static int TAB_AUFWAND = 1;
    private CheckTreeManager cm;
    private cbClickListener cblst;
    private CheckTreeSelectionModel sm;
    private JPopupMenu menu;
    private HashMap template;
    private JRadioButton dummy;
    private ArrayList termid;
    private String tage[] = {"Mon", "Die", "Mit", "Don", "Fre", "Sam", "Son"};
    private boolean ignoreEvent = false;
    private long tmp;
    private JDialog parent;
    private JDCPropertyChangeListener jdcpcl;
    private boolean group;


//    /**
//     * für Änderungen
//     *
//     */
//    public DlgTermin(java.awt.Frame parent, ArrayList termid, long tmp, boolean group) {
//        super(parent, true);
//        this.parent = parent;
//        this.termid = termid;
//        this.tmp = tmp;
//        this.group = group;
//        initDialog();
//    }

    // Aendern in Class.showDialog();
    public DlgTermin(JDialog parent, ArrayList termid, long tmp, boolean group) {
        super(parent, true);
        this.parent = parent;
        this.termid = termid;
        this.tmp = tmp;
        this.group = group;
        initDialog();
    }

    private class cbClickListener implements TreeSelectionListener {

        public void valueChanged(TreeSelectionEvent arg0) {
            txtDauer.setText(Double.toString(Tools.calculateTree(treeMass.getModel(), sm)));
            reloadTree();
        }
    }

    private void reloadTree() {
        Vector v = SYSTools.getExpansionState(treeMass);
        DefaultTreeModel tm = (DefaultTreeModel) treeMass.getModel();
        tm.reload();
        SYSTools.setExpansionState(treeMass, v);
        v.clear();
    }

    private void initDialog() {

        initComponents();
        // Die Daten werden immer vom ersten genommen. Bei Mehrfachsetzung.
        //long termid0 = ((Long) termid.get(0)).longValue();
        initData();

        this.setTitle(SYSTools.getWindowTitle("Details für Massnahme(n)"));

        int selectIndex = 0;

        dummy = new JRadioButton();
        bgMonat.add(dummy);

        cmbUhrzeit.setModel(new DefaultComboBoxModel(SYSCalendar.fillUhrzeiten().toArray()));

        ignoreEvent = true;

        String bez = DBRetrieve.getSingleValue("Massnahmen", "Bezeichnung", "MassID", template.get("MassID")).toString();
        lblMassnahme.setText(bez);

        createTree(SYSTools.catchNull(template.get("XML")));

        spinNachtMo.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
        spinMorgens.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
        spinMittags.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
        spinNachmittags.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
        spinAbends.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
        spinUhrzeit.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
        spinNachtAb.setModel(new SpinnerNumberModel(0, 0, 1000, 1));

        spinNachtMo.setValue(((Integer) template.get("NachtMo")).intValue());
        spinMorgens.setValue(((Integer) template.get("Morgens")).intValue());
        spinMittags.setValue(((Integer) template.get("Mittags")).intValue());
        spinNachmittags.setValue(((Integer) template.get("Nachmittags")).intValue());
        spinAbends.setValue(((Integer) template.get("Abends")).intValue());
        spinNachtAb.setValue(((Integer) template.get("NachtAb")).intValue());

        spinUhrzeit.setValue(((Integer) template.get("UhrzeitAnzahl")).intValue());

        if (template.get("Uhrzeit") == null) {
            cmbUhrzeit.setSelectedIndex(-1);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            SYSTools.selectInComboBox(cmbUhrzeit, sdf.format((Time) template.get("Uhrzeit")), true);
        }
        spinTaeglich.setModel(new SpinnerNumberModel(0, 0, 365, 1));
        spinWoche.setModel(new SpinnerNumberModel(0, 0, 52, 1));
        spinMonat.setModel(new SpinnerNumberModel(0, 0, 12, 1));
        spinMonatTag.setModel(new SpinnerNumberModel(1, 0, 31, 1));
        spinMonatWTag.setModel(new SpinnerNumberModel(0, 0, 5, 1));

        //rbTag.setSelected(true);
        //rbMonatTag.setSelected(true);

        for (int i = 0; i < 7; i++) {
            if (template.containsKey(tage[i])) {
                selectIndex = i;
            }
        }

        spinTaeglich.setValue(template.get("Taeglich"));
        rbTag.setSelected(Integer.parseInt(spinTaeglich.getValue().toString()) > 0);
        spinWoche.setValue(template.get("Woechentlich"));
        rbWoche.setSelected(Integer.parseInt(spinWoche.getValue().toString()) > 0);
        spinMonat.setValue(template.get("Monatlich"));
        rbMonat.setSelected(Integer.parseInt(spinMonat.getValue().toString()) > 0);
        spinMonatTag.setValue(template.get("TagNum"));


        if (((Integer) template.get("Woechentlich")).intValue() > 0) {
            cbMon.setSelected(((Integer) template.get("Mon")).intValue() > 0);
            cbDie.setSelected(((Integer) template.get("Die")).intValue() > 0);
            cbMit.setSelected(((Integer) template.get("Mit")).intValue() > 0);
            cbDon.setSelected(((Integer) template.get("Don")).intValue() > 0);
            cbFre.setSelected(((Integer) template.get("Fre")).intValue() > 0);
            cbSam.setSelected(((Integer) template.get("Sam")).intValue() > 0);
            cbSon.setSelected(((Integer) template.get("Son")).intValue() > 0);
            rbWoche.setSelected(true);
        }

        if (((Integer) template.get("Monatlich")).intValue() > 0) {
            if (((Integer) template.get("TagNum")).intValue() > 0) {
                spinMonatTag.setValue(template.get("TagNum"));
                rbMonatTag.setSelected(true);
            } else {
                cmbWTag.setSelectedIndex(selectIndex);
                spinMonatWTag.setValue(template.get(tage[selectIndex]));
                rbMonatWTag.setSelected(true);
            }
            rbMonat.setSelected(true);
        }

        if (((Integer) template.get("Taeglich")).intValue() > 0) {
            rbTag.setSelected(true);
        }


        cbErforderlich.setSelected(((Boolean) template.get("Erforderlich")).booleanValue());

        jdcLDatum.setDate((Date) template.get("LDatum"));
        jdcpcl = new JDCPropertyChangeListener();
        jdcLDatum.getDateEditor().addPropertyChangeListener(jdcpcl);

        if (group) {
            txtBemerkung.setText("Gruppenmassnahme. Keine Bemerkung möglich.");
        } else {
            String text = "";
            if (template.get("Bemerkung") != null) {
                text = template.get("Bemerkung").toString();
            }
            txtBemerkung.setText(text);
        }
        txtBemerkung.setEnabled(!group);

        jdcLDatum.setEnabled(true);

        dummy.setSelected(true);
        rbMonatTag.setSelected(Integer.parseInt(spinMonatTag.getValue().toString()) > 0);
        rbMonatWTag.setSelected(Integer.parseInt(spinMonatWTag.getValue().toString()) > 0);

        ((JSpinner.DefaultEditor) spinMorgens.getEditor()).getTextField().setBackground(SYSConst.lightblue);
        ((JSpinner.DefaultEditor) spinMittags.getEditor()).getTextField().setBackground(SYSConst.gold7);
        ((JSpinner.DefaultEditor) spinNachmittags.getEditor()).getTextField().setBackground(SYSConst.melonrindgreen);
        ((JSpinner.DefaultEditor) spinAbends.getEditor()).getTextField().setBackground(SYSConst.bermuda_sand);
        ((JSpinner.DefaultEditor) spinNachtAb.getEditor()).getTextField().setBackground(SYSConst.bluegrey);

        cbErforderlich.setEnabled(!group);

        txtDauer.setText(template.get("Dauer").toString());
        createTree(SYSTools.catchNull(template.get("XML")));

        ignoreEvent = false;
    }

    public void showDialog() {
        SYSTools.centerOnParent(parent, this);
        setVisible(true);
    }

    private void createNewTree() {
        // Wurzel
        Object[] o = new Object[]{"", 0d, new Vector(), new Vector(), ParserMassnahmen.TYPE_ROOT, 0d};
        ListElement le = new ListElement(lblMassnahme.getText(), o);
        DefaultMutableTreeNode tree = new DefaultMutableTreeNode(le);

        // Vorbereitung
        o = new Object[]{"", 0d, new Vector(), new Vector(), ParserMassnahmen.TYPE_Vorbereitung, 0d};
        le = new ListElement("Vorbereitung", o);
        tree.add(new DefaultMutableTreeNode(le));

        // Nachbereitung
        o = new Object[]{"", 0d, new Vector(), new Vector(), ParserMassnahmen.TYPE_Nachbereitung, 0d};
        le = new ListElement("Nachbereitung", o);
        tree.add(new DefaultMutableTreeNode(le));

        treeMass.setModel(new DefaultTreeModel(tree));
        treeMass.setCellRenderer(new RNDMassTree());
        cblst = new cbClickListener();
        cm = new CheckTreeManager(treeMass);
        sm = (CheckTreeSelectionModel) cm.getSelectionModel();
        sm.addTreeSelectionListener(cblst);

        txtDauer.setText("0");
    }

    private void createTree(String xml) {
        treeMass.setVisible(true);
        cblst = new cbClickListener();
        DefaultMutableTreeNode tree = null;
        TreePath[] preselectedPaths = null;

        if (SYSTools.catchNull(xml).equals("")) {
            treeMass.setCellRenderer(new DefaultTreeCellRenderer());
            tree = new DefaultMutableTreeNode();
            treeMass.setModel(new DefaultTreeModel(null));
            treeMass.setEnabled(true);

        } else {
            try {
                XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
                InputSource is = new org.xml.sax.InputSource(new java.io.BufferedReader(new java.io.StringReader(xml)));
                ParserMassnahmen s = new ParserMassnahmen();
                parser.setContentHandler(s);
                parser.parse(is);
                tree = s.getTree();
                preselectedPaths = s.getSelectionPaths();
            } catch (SAXException sAXException) {
                OPDE.getLogger().error(sAXException);
            } catch (IOException iOException) {
            }

            treeMass.setModel(new DefaultTreeModel(tree));
            treeMass.setCellRenderer(new RNDMassTree());

            cm = new CheckTreeManager(treeMass);
            sm = (CheckTreeSelectionModel) cm.getSelectionModel();
            sm.addSelectionPaths(preselectedPaths);
            SYSTools.expandAll(treeMass);
            double sum = Tools.calculateTree(treeMass.getModel(), sm);
            txtDauer.setText(Double.toString(sum));
            // Hier kommt der Listener, der feststellt, wenn irgendeine Checkbox geclickt wird.
            sm.addTreeSelectionListener(cblst);
            treeMass.setEnabled(true);
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgWdh = new javax.swing.ButtonGroup();
        bgMonat = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        jtpMain = new javax.swing.JTabbedPane();
        pnlTermin = new javax.swing.JPanel();
        pnlRegular = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        spinMorgens = new javax.swing.JSpinner();
        spinMittags = new javax.swing.JSpinner();
        spinAbends = new javax.swing.JSpinner();
        spinNachtAb = new javax.swing.JSpinner();
        spinNachtMo = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        cmbUhrzeit = new javax.swing.JComboBox();
        spinUhrzeit = new javax.swing.JSpinner();
        spinNachmittags = new javax.swing.JSpinner();
        jLabel11 = new javax.swing.JLabel();
        lblEin1 = new javax.swing.JLabel();
        lblEin2 = new javax.swing.JLabel();
        lblEin3 = new javax.swing.JLabel();
        lblEin4 = new javax.swing.JLabel();
        lblEin5 = new javax.swing.JLabel();
        lblEin6 = new javax.swing.JLabel();
        lblEin7 = new javax.swing.JLabel();
        cbErforderlich = new javax.swing.JCheckBox();
        pnlWdh = new javax.swing.JPanel();
        rbTag = new javax.swing.JRadioButton();
        rbWoche = new javax.swing.JRadioButton();
        rbMonat = new javax.swing.JRadioButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        cbMon = new javax.swing.JCheckBox();
        cbDie = new javax.swing.JCheckBox();
        cbMit = new javax.swing.JCheckBox();
        cbDon = new javax.swing.JCheckBox();
        cbFre = new javax.swing.JCheckBox();
        cbSam = new javax.swing.JCheckBox();
        cbSon = new javax.swing.JCheckBox();
        jLabel9 = new javax.swing.JLabel();
        rbMonatTag = new javax.swing.JRadioButton();
        jLabel10 = new javax.swing.JLabel();
        rbMonatWTag = new javax.swing.JRadioButton();
        cmbWTag = new javax.swing.JComboBox();
        spinTaeglich = new javax.swing.JSpinner();
        spinWoche = new javax.swing.JSpinner();
        spinMonat = new javax.swing.JSpinner();
        spinMonatTag = new javax.swing.JSpinner();
        spinMonatWTag = new javax.swing.JSpinner();
        lblLDatum = new javax.swing.JLabel();
        jdcLDatum = new com.toedter.calendar.JDateChooser();
        sep1 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtBemerkung = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        txtDauer = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        treeMass = new javax.swing.JTree();
        jPanel4 = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnDiscard = new javax.swing.JButton();
        lblMassnahme = new javax.swing.JLabel();

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jtpMain.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        jtpMain.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jtpMainStateChanged(evt);
            }
        });

        pnlRegular.setBorder(javax.swing.BorderFactory.createTitledBorder("Häufigkeit der Anwendung"));

        jLabel1.setForeground(new java.awt.Color(0, 0, 204));
        jLabel1.setText("Morgens:");

        jLabel2.setForeground(new java.awt.Color(255, 102, 0));
        jLabel2.setText("Mittags:");

        jLabel3.setForeground(new java.awt.Color(255, 0, 51));
        jLabel3.setText("Abends:");

        jLabel4.setText("Nacht, spät abends:");

        jLabel5.setText("Anzahl");

        spinMorgens.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinMorgensStateChanged(evt);
            }
        });

        spinMittags.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinMittagsStateChanged(evt);
            }
        });

        spinAbends.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinAbendsStateChanged(evt);
            }
        });

        spinNachtAb.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinNachtAbStateChanged(evt);
            }
        });

        spinNachtMo.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinNachtMoStateChanged(evt);
            }
        });
        spinNachtMo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                spinNachtMoFocusGained(evt);
            }
        });

        jLabel6.setText("Nachts, früh morgens:");

        cmbUhrzeit.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"10:00", "10:15", "10:30", "10:45"}));
        cmbUhrzeit.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbUhrzeitItemStateChanged(evt);
            }
        });

        spinUhrzeit.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinUhrzeitStateChanged(evt);
            }
        });

        spinNachmittags.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinNachmittagsStateChanged(evt);
            }
        });

        jLabel11.setForeground(new java.awt.Color(0, 153, 51));
        jLabel11.setText("Nachmittag:");

        lblEin1.setFont(new java.awt.Font("Dialog", 0, 10));
        lblEin1.setText("x");

        lblEin2.setFont(new java.awt.Font("Dialog", 0, 10));
        lblEin2.setText("x");

        lblEin3.setFont(new java.awt.Font("Dialog", 0, 10));
        lblEin3.setText("x");

        lblEin4.setFont(new java.awt.Font("Dialog", 0, 10));
        lblEin4.setText("x");

        lblEin5.setFont(new java.awt.Font("Dialog", 0, 10));
        lblEin5.setText("x");

        lblEin6.setFont(new java.awt.Font("Dialog", 0, 10));
        lblEin6.setText("x");

        lblEin7.setFont(new java.awt.Font("Dialog", 0, 10));
        lblEin7.setText("x");

        cbErforderlich.setText("Bearbeitung erforderlich");
        cbErforderlich.setToolTipText("Wird solange, immer wieder auf den DFN gesetzt, bis jemand die Massnahme als \"erledigt\" oder \"unerledigt\" markiert.");
        cbErforderlich.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout pnlRegularLayout = new org.jdesktop.layout.GroupLayout(pnlRegular);
        pnlRegular.setLayout(pnlRegularLayout);
        pnlRegularLayout.setHorizontalGroup(
                pnlRegularLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(pnlRegularLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(pnlRegularLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(pnlRegularLayout.createSequentialGroup()
                                                .add(pnlRegularLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(jLabel1)
                                                        .add(jLabel2)
                                                        .add(jLabel6)
                                                        .add(jLabel11)
                                                        .add(jLabel3)
                                                        .add(pnlRegularLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                                                .add(org.jdesktop.layout.GroupLayout.LEADING, cmbUhrzeit, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                                .add(8, 8, 8)
                                                .add(pnlRegularLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(spinUhrzeit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                                                        .add(spinNachtAb, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                                                        .add(spinAbends, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                                                        .add(spinNachmittags, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                                                        .add(spinMittags, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                                                        .add(spinMorgens, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                                                        .add(spinNachtMo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel5))
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(pnlRegularLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                                        .add(org.jdesktop.layout.GroupLayout.LEADING, lblEin7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .add(org.jdesktop.layout.GroupLayout.LEADING, lblEin6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .add(org.jdesktop.layout.GroupLayout.LEADING, lblEin5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .add(org.jdesktop.layout.GroupLayout.LEADING, lblEin3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .add(org.jdesktop.layout.GroupLayout.LEADING, lblEin4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .add(lblEin2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .add(lblEin1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                        .add(cbErforderlich))
                                .addContainerGap())
        );
        pnlRegularLayout.setVerticalGroup(
                pnlRegularLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(pnlRegularLayout.createSequentialGroup()
                                .add(jLabel5)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(pnlRegularLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jLabel6)
                                        .add(spinNachtMo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(lblEin1))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(pnlRegularLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jLabel1)
                                        .add(spinMorgens, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(lblEin2))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(pnlRegularLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jLabel2)
                                        .add(spinMittags, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(lblEin3))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(pnlRegularLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jLabel11)
                                        .add(spinNachmittags, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(lblEin4))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(pnlRegularLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jLabel3)
                                        .add(spinAbends, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(lblEin5))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(pnlRegularLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jLabel4)
                                        .add(spinNachtAb, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(lblEin6))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(pnlRegularLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(cmbUhrzeit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(spinUhrzeit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(lblEin7))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(cbErforderlich)
                                .addContainerGap(17, Short.MAX_VALUE))
        );

        pnlWdh.setBorder(javax.swing.BorderFactory.createTitledBorder("Wiederholungen"));

        bgWdh.add(rbTag);
        rbTag.setText("täglich alle");
        rbTag.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbTag.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbTag.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbTagActionPerformed(evt);
            }
        });

        bgWdh.add(rbWoche);
        rbWoche.setText("wöchentlich alle");
        rbWoche.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbWoche.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbWoche.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbWocheActionPerformed(evt);
            }
        });

        bgWdh.add(rbMonat);
        rbMonat.setText("monatlich alle");
        rbMonat.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbMonat.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbMonat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbMonatActionPerformed(evt);
            }
        });

        jLabel7.setText("Tage");

        jLabel8.setText("Wochen am:");

        cbMon.setText("Mon");
        cbMon.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbMon.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbMon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbMonActionPerformed(evt);
            }
        });

        cbDie.setText("Die");
        cbDie.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbDie.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbDie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbDieActionPerformed(evt);
            }
        });

        cbMit.setText("Mit");
        cbMit.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbMit.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbMit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbMitActionPerformed(evt);
            }
        });

        cbDon.setText("Don");
        cbDon.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbDon.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbDon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbDonActionPerformed(evt);
            }
        });

        cbFre.setText("Fre");
        cbFre.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbFre.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbFre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbFreActionPerformed(evt);
            }
        });

        cbSam.setText("Sam");
        cbSam.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbSam.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbSam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSamActionPerformed(evt);
            }
        });

        cbSon.setText("Son");
        cbSon.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbSon.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbSon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSonActionPerformed(evt);
            }
        });

        jLabel9.setText("Monat(e)");

        bgMonat.add(rbMonatTag);
        rbMonatTag.setText("wiederholt am");
        rbMonatTag.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbMonatTag.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbMonatTag.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbMonatTagActionPerformed(evt);
            }
        });

        jLabel10.setText("Tag");

        bgMonat.add(rbMonatWTag);
        rbMonatWTag.setText("wiederholt am");
        rbMonatWTag.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbMonatWTag.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbMonatWTag.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbMonatWTagActionPerformed(evt);
            }
        });

        cmbWTag.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"}));
        cmbWTag.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbWTagItemStateChanged(evt);
            }
        });

        spinTaeglich.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinTaeglichStateChanged(evt);
            }
        });

        spinWoche.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinWocheStateChanged(evt);
            }
        });

        spinMonat.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinMonatStateChanged(evt);
            }
        });

        spinMonatTag.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinMonatTagStateChanged(evt);
            }
        });

        spinMonatWTag.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinMonatWTagStateChanged(evt);
            }
        });

        lblLDatum.setText("Erste Anwendung am:");

        jdcLDatum.setEnabled(false);

        org.jdesktop.layout.GroupLayout pnlWdhLayout = new org.jdesktop.layout.GroupLayout(pnlWdh);
        pnlWdh.setLayout(pnlWdhLayout);
        pnlWdhLayout.setHorizontalGroup(
                pnlWdhLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(pnlWdhLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(pnlWdhLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(sep1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
                                        .add(pnlWdhLayout.createSequentialGroup()
                                                .add(rbTag)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(spinTaeglich, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(jLabel7))
                                        .add(pnlWdhLayout.createSequentialGroup()
                                                .add(rbWoche)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(spinWoche, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(jLabel8))
                                        .add(pnlWdhLayout.createSequentialGroup()
                                                .add(17, 17, 17)
                                                .add(pnlWdhLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(pnlWdhLayout.createSequentialGroup()
                                                                .add(cbFre)
                                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                                .add(cbSam)
                                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                                .add(cbSon))
                                                        .add(pnlWdhLayout.createSequentialGroup()
                                                                .add(cbMon)
                                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                                .add(cbDie)
                                                                .add(16, 16, 16)
                                                                .add(cbMit)
                                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                                .add(cbDon))))
                                        .add(pnlWdhLayout.createSequentialGroup()
                                                .add(17, 17, 17)
                                                .add(pnlWdhLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(pnlWdhLayout.createSequentialGroup()
                                                                .add(rbMonatTag)
                                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                                .add(spinMonatTag, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                        .add(pnlWdhLayout.createSequentialGroup()
                                                                .add(rbMonatWTag)
                                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                                .add(spinMonatWTag, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                                .add(20, 20, 20)
                                                .add(pnlWdhLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(jLabel10)
                                                        .add(cmbWTag, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                        .add(pnlWdhLayout.createSequentialGroup()
                                                .add(rbMonat)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(spinMonat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(jLabel9))
                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlWdhLayout.createSequentialGroup()
                                                .add(lblLDatum)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(jdcLDatum, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        pnlWdhLayout.setVerticalGroup(
                pnlWdhLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(pnlWdhLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(pnlWdhLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(rbTag)
                                        .add(spinTaeglich, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(jLabel7))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(pnlWdhLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(rbWoche)
                                        .add(spinWoche, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(jLabel8))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(pnlWdhLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(cbMon)
                                        .add(cbDie)
                                        .add(cbMit)
                                        .add(cbDon))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(pnlWdhLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(cbFre)
                                        .add(cbSam)
                                        .add(cbSon))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(pnlWdhLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(rbMonat)
                                        .add(jLabel9)
                                        .add(spinMonat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(pnlWdhLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(rbMonatTag)
                                        .add(spinMonatTag, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(jLabel10))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(pnlWdhLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(rbMonatWTag)
                                        .add(spinMonatWTag, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(cmbWTag, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(sep1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 21, Short.MAX_VALUE)
                                .add(pnlWdhLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jdcLDatum, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(lblLDatum))
                                .addContainerGap())
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Kommentar zur Anwendung (Erscheint im DFN)"));

        txtBemerkung.setColumns(20);
        txtBemerkung.setRows(5);
        jScrollPane1.setViewportView(txtBemerkung);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
                                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel1Layout.createSequentialGroup()
                                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout pnlTerminLayout = new org.jdesktop.layout.GroupLayout(pnlTermin);
        pnlTermin.setLayout(pnlTerminLayout);
        pnlTerminLayout.setHorizontalGroup(
                pnlTerminLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(pnlTerminLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(pnlTerminLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlTerminLayout.createSequentialGroup()
                                                .add(pnlRegular, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(pnlWdh, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
        );
        pnlTerminLayout.setVerticalGroup(
                pnlTerminLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(pnlTerminLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(pnlTerminLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(pnlWdh, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(pnlRegular, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(9, 9, 9)
                                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );

        pnlRegular.getAccessibleContext().setAccessibleName("Häufigkeit");

        jtpMain.addTab("Termine", pnlTermin);

        jLabel12.setText("Dauer:");

        txtDauer.setText("jTextField1");
        txtDauer.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtDauerCaretUpdate(evt);
            }
        });

        jLabel13.setText("Minuten");

        treeMass.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                treeMassMousePressed(evt);
            }
        });
        jScrollPane2.setViewportView(treeMass);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 651, Short.MAX_VALUE)
                                        .add(jPanel3Layout.createSequentialGroup()
                                                .add(jLabel12)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(txtDauer, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 178, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(jLabel13)))
                                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jLabel12)
                                        .add(txtDauer, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(jLabel13))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                                .addContainerGap())
        );

        jtpMain.addTab("Aufwand", jPanel3);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/apply.png"))); // NOI18N
        btnSave.setText("Speichern");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnDiscard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/cancel.png"))); // NOI18N
        btnDiscard.setText("Verwerfen");
        btnDiscard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiscardActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(btnSave)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(btnDiscard)
                                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(btnDiscard)
                                        .add(btnSave))
                                .addContainerGap())
        );

        lblMassnahme.setFont(new java.awt.Font("Dialog", 1, 14));
        lblMassnahme.setForeground(new java.awt.Color(51, 51, 255));
        lblMassnahme.setText("jLabel5");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(layout.createSequentialGroup()
                                .addContainerGap()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(layout.createSequentialGroup()
                                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                                        .add(jtpMain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE)
                                                        .add(lblMassnahme, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE))
                                                .add(12, 12, 12))
                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .add(lblMassnahme)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jtpMain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width - 714) / 2, (screenSize.height - 633) / 2, 714, 633);
    }// </editor-fold>//GEN-END:initComponents

    private void spinNachtMoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_spinNachtMoFocusGained
        SYSTools.markAllTxt(((JSpinner.DefaultEditor) spinNachtMo.getEditor()).getTextField());
    }//GEN-LAST:event_spinNachtMoFocusGained

    private void initData() {
        if (termid != null && termid.size() > 0) {
            template = DBRetrieve.getSingleRecord("MassTermin", "TermID", termid.get(0));
        } else {
            // Dieser Fall kann so eigentlich nicht eintreten. Keine Ahnung, warum das hier drin steht.
            template = new HashMap();
            template.put("NachtMo", 0);
            template.put("Morgens", 0);
            template.put("Mittags", 0);
            template.put("Nachmittags", 0);
            template.put("Abends", 0);
            template.put("NachtAb", 0);
            template.put("UhrzeitAnzahl", 1);
            GregorianCalendar gc = new GregorianCalendar();
            gc.set(GregorianCalendar.HOUR, 10);
            gc.set(GregorianCalendar.MINUTE, 0);
            gc.set(GregorianCalendar.SECOND, 0);
            gc.set(GregorianCalendar.MILLISECOND, 0);
            template.put("Uhrzeit", new Time(gc.getTimeInMillis()));
            template.put("ETermin", null);
            template.put("Taeglich", 0);
            template.put("Woechentlich", 1);
            template.put("Mon", 1);
            template.put("Die", 0);
            template.put("Mit", 0);
            template.put("Don", 0);
            template.put("Fre", 0);
            template.put("Sam", 0);
            template.put("Son", 0);
            template.put("Monatlich", 0);
            template.put("TagNum", 0);
            template.put("Bemerkung", "");
            template.put("LDatum", new Timestamp(SYSCalendar.now()));
            template.put("Erforderlich", new Boolean(false));

        }
    }

    private void cmbUhrzeitItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbUhrzeitItemStateChanged
        if (ignoreEvent) {
            return;
        }
        ignoreEvent = true;
        spinNachtMo.setValue(0);
        spinMorgens.setValue(0);
        spinMittags.setValue(0);
        spinNachmittags.setValue(0);
        spinAbends.setValue(0);
        spinNachtAb.setValue(0);

        if (Integer.parseInt(spinUhrzeit.getValue().toString()) == 0) {
            spinUhrzeit.setValue(1);
        }

        ignoreEvent = false;
    }//GEN-LAST:event_cmbUhrzeitItemStateChanged

    public void dispose() {
        SYSTools.unregisterListeners(this);
        jdcLDatum.removePropertyChangeListener(jdcpcl);
        jdcLDatum.cleanup();
        super.dispose();
    }

    private void spinUhrzeitStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinUhrzeitStateChanged
        if (ignoreEvent) {
            return;
        }
        ignoreEvent = true;
        spinNachtMo.setValue(0);
        spinMorgens.setValue(0);
        spinMittags.setValue(0);
        spinNachmittags.setValue(0);
        spinAbends.setValue(0);
        spinNachtAb.setValue(0);
        if (cmbUhrzeit.getSelectedIndex() == -1) {
            // cmbUhrzeit auf die nächste volle Stunde ab JETZT setzen.
            GregorianCalendar gc = new GregorianCalendar();
            gc.add(GregorianCalendar.HOUR, 1);
            gc.set(GregorianCalendar.MINUTE, 0);
            gc.set(GregorianCalendar.SECOND, 0);
            gc.set(GregorianCalendar.MILLISECOND, 0);
            SYSTools.selectInComboBox(cmbUhrzeit, SYSCalendar.toGermanTime(gc), true);
        }
        if (Integer.parseInt(spinUhrzeit.getValue().toString()) == 0) {
            spinUhrzeit.setValue(1);
        }

        ignoreEvent = false;
    }//GEN-LAST:event_spinUhrzeitStateChanged

    private void spinNachmittagsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinNachmittagsStateChanged
        spinZeitStateChanged();
    }//GEN-LAST:event_spinNachmittagsStateChanged

    private void spinNachtMoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinNachtMoStateChanged
        spinZeitStateChanged();
    }//GEN-LAST:event_spinNachtMoStateChanged

    private void rbMonatWTagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbMonatWTagActionPerformed
        if (ignoreEvent) {
            return;
        }
        ignoreEvent = true;
        if (!rbMonat.isSelected()) {
            rbMonat.setSelected(true);
            spinMonat.setValue(1);
            rbMonatWTag.setSelected(true);
        }
        spinMonatWTag.setValue(1);
        spinMonatTag.setValue(0);
        cmbWTag.setSelectedIndex(0);
        ignoreEvent = false;
    }//GEN-LAST:event_rbMonatWTagActionPerformed

    private void rbMonatTagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbMonatTagActionPerformed
        if (ignoreEvent) {
            return;
        }
        ignoreEvent = true;
        if (!rbMonat.isSelected()) {
            rbMonat.setSelected(true);
            spinMonat.setValue(1); // BugID #
            rbMonatTag.setSelected(true);
        }
        spinMonatTag.setValue(1);
        spinMonatWTag.setValue(0);
        cmbWTag.setSelectedIndex(0);
        ignoreEvent = false;
    }//GEN-LAST:event_rbMonatTagActionPerformed

    private void rbMonatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbMonatActionPerformed
        if (ignoreEvent) {
            return;
        }
        ignoreEvent = true;
        spinTaeglich.setValue(0);
        spinWoche.setValue(0);
        cbMon.setSelected(false);
        cbDie.setSelected(false);
        cbMit.setSelected(false);
        cbDon.setSelected(false);
        cbFre.setSelected(false);
        cbSam.setSelected(false);
        cbSon.setSelected(false);
        spinMonat.setValue(1);
        spinMonatTag.setValue(1);
        spinMonatWTag.setValue(0);
        cmbWTag.setSelectedIndex(0);
        rbMonatTag.setSelected(true);
        jdcLDatum.setEnabled(true);
        ignoreEvent = false;
    }//GEN-LAST:event_rbMonatActionPerformed

    private void rbWocheActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbWocheActionPerformed
        if (ignoreEvent) {
            return;
        }
        ignoreEvent = true;

        spinTaeglich.setValue(0);
        spinWoche.setValue(1);

        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            cbMon.setSelected(true);
        }

        dummy.setSelected(true);
        spinMonat.setValue(0);
        spinMonatTag.setValue(0);
        spinMonatWTag.setValue(0);
        cmbWTag.setSelectedIndex(0);
        jdcLDatum.setEnabled(true);
        ignoreEvent = false;
    }//GEN-LAST:event_rbWocheActionPerformed

    private void rbTagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbTagActionPerformed
        if (ignoreEvent) {
            return;
        }
        ignoreEvent = true;
        spinTaeglich.setValue(1);
        //spinTaeglich.getModel().
        spinWoche.setValue(0);
        cbMon.setSelected(false);
        cbDie.setSelected(false);
        cbMit.setSelected(false);
        cbDon.setSelected(false);
        cbFre.setSelected(false);
        cbSam.setSelected(false);
        cbSon.setSelected(false);
        dummy.setSelected(true);
        spinMonat.setValue(0);
        spinMonatTag.setValue(0);
        spinMonatWTag.setValue(0);
        cmbWTag.setSelectedIndex(0);
        jdcLDatum.setEnabled(true);
        ignoreEvent = false;
    }//GEN-LAST:event_rbTagActionPerformed

    private void cbSonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSonActionPerformed
        if (ignoreEvent) {
            return;
        }
        if (!rbWoche.isSelected()) {
            rbWoche.doClick();
        }
        ignoreEvent = true;

        dummy.setSelected(true);

        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            JOptionPane.showMessageDialog(this, "Sie müssen mindestens einen Wochentag angeben.");
            ((JCheckBox) evt.getSource()).setSelected(true);
        }

        ignoreEvent = false;
    }//GEN-LAST:event_cbSonActionPerformed

    private void cbSamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSamActionPerformed
        if (ignoreEvent) {
            return;
        }
        if (!rbWoche.isSelected()) {
            rbWoche.doClick();
        }
        ignoreEvent = true;

        dummy.setSelected(true);

        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            JOptionPane.showMessageDialog(this, "Sie müssen mindestens einen Wochentag angeben.");
            ((JCheckBox) evt.getSource()).setSelected(true);
        }

        ignoreEvent = false;
    }//GEN-LAST:event_cbSamActionPerformed

    private void cbFreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbFreActionPerformed
        if (ignoreEvent) {
            return;
        }
        if (!rbWoche.isSelected()) {
            rbWoche.doClick();
        }
        ignoreEvent = true;

        dummy.setSelected(true);

        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            JOptionPane.showMessageDialog(this, "Sie müssen mindestens einen Wochentag angeben.");
            ((JCheckBox) evt.getSource()).setSelected(true);
        }

        ignoreEvent = false;
    }//GEN-LAST:event_cbFreActionPerformed

    private void cbDonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbDonActionPerformed
        if (ignoreEvent) {
            return;
        }
        if (!rbWoche.isSelected()) {
            rbWoche.doClick();
        }
        ignoreEvent = true;

        dummy.setSelected(true);

        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            JOptionPane.showMessageDialog(this, "Sie müssen mindestens einen Wochentag angeben.");
            ((JCheckBox) evt.getSource()).setSelected(true);
        }

        ignoreEvent = false;
    }//GEN-LAST:event_cbDonActionPerformed

    private void cbMitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbMitActionPerformed
        if (ignoreEvent) {
            return;
        }
        if (!rbWoche.isSelected()) {
            rbWoche.doClick();
        }
        ignoreEvent = true;

        dummy.setSelected(true);

        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            JOptionPane.showMessageDialog(this, "Sie müssen mindestens einen Wochentag angeben.");
            ((JCheckBox) evt.getSource()).setSelected(true);
        }

        ignoreEvent = false;
    }//GEN-LAST:event_cbMitActionPerformed

    private void cbDieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbDieActionPerformed
        if (ignoreEvent) {
            return;
        }
        if (!rbWoche.isSelected()) {
            rbWoche.doClick();
        }
        ignoreEvent = true;

        dummy.setSelected(true);

        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            JOptionPane.showMessageDialog(this, "Sie müssen mindestens einen Wochentag angeben.");
            ((JCheckBox) evt.getSource()).setSelected(true);
        }

        ignoreEvent = false;
    }//GEN-LAST:event_cbDieActionPerformed

    private void cbMonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbMonActionPerformed
        if (ignoreEvent) {
            return;
        }

        if (!rbWoche.isSelected()) {
            rbWoche.doClick();
        }
        ignoreEvent = true;
        dummy.setSelected(true);

        if (!(cbSon.isSelected() || cbSam.isSelected() || cbFre.isSelected() || cbDon.isSelected() || cbMit.isSelected() || cbDie.isSelected() || cbMon.isSelected())) {
            JOptionPane.showMessageDialog(this, "Sie müssen mindestens einen Wochentag angeben.");
            ((JCheckBox) evt.getSource()).setSelected(true);
        }

        ignoreEvent = false;
    }//GEN-LAST:event_cbMonActionPerformed

    private void cmbWTagItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbWTagItemStateChanged
        if (ignoreEvent) {
            return;
        }

        if (!rbMonat.isSelected()) {
            rbMonat.doClick();
        }


        if (!rbMonatWTag.isSelected()) {
            rbMonatWTag.doClick();
            //spinMonatWTag.setValue(1);
        }
    }//GEN-LAST:event_cmbWTagItemStateChanged

    private void spinMonatWTagStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinMonatWTagStateChanged
        if (ignoreEvent) {
            return;
        }

        if (!rbMonat.isSelected()) {
            rbMonat.doClick();
        }
        if (!rbMonatWTag.isSelected()) {
            rbMonatWTag.doClick();
        }

        ignoreEvent = true;
        int monat = Integer.parseInt(spinMonat.getValue().toString());
        if (monat == 0) {
            spinMonat.setValue(1);
        }
        ignoreEvent = false;
    }//GEN-LAST:event_spinMonatWTagStateChanged

    private void spinMonatTagStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinMonatTagStateChanged
        if (ignoreEvent) {
            return;
        }

        if (!rbMonat.isSelected()) {
            rbMonat.doClick();
        }
        if (!rbMonatTag.isSelected()) {
            rbMonatTag.doClick();
        }
        ignoreEvent = true;
        rbMonatTag.setSelected(true);
        //if (!rbMonatTag.isSelected()) rbMonatTag.setSelected(true);
        int monat = Integer.parseInt(spinMonatTag.getValue().toString());
        if (monat == 0) {
            spinMonatTag.setValue(1);
        }
        ignoreEvent = false;
    }//GEN-LAST:event_spinMonatTagStateChanged

    private void spinMonatStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinMonatStateChanged
        if (ignoreEvent) {
            return;
        }

        if (!rbMonat.isSelected()) {
            rbMonat.doClick();
        }
        ignoreEvent = true;
        int monat = Integer.parseInt(spinMonat.getValue().toString());
        if (monat == 0) {
            spinMonat.setValue(1);
        }
        ignoreEvent = false;
    }//GEN-LAST:event_spinMonatStateChanged

    private void spinWocheStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinWocheStateChanged
        if (ignoreEvent) {
            return;
        }

        if (!rbWoche.isSelected()) {
            rbWoche.doClick();
        }

        ignoreEvent = true;
        dummy.setSelected(true);
        int woche = Integer.parseInt(spinWoche.getValue().toString());
        if (woche == 0) {
            spinWoche.setValue(1);
        }
        ignoreEvent = false;
    }//GEN-LAST:event_spinWocheStateChanged

    private void spinTaeglichStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinTaeglichStateChanged
        if (ignoreEvent) {
            return;
        }

        if (!rbTag.isSelected()) {
            rbTag.doClick();
        }
        ignoreEvent = true;
        dummy.setSelected(true);
        ignoreEvent = false;
    }//GEN-LAST:event_spinTaeglichStateChanged

    private void spinZeitStateChanged() {
        if (ignoreEvent) {
            return;
        }
        ignoreEvent = true;

        int nachtMo = Integer.parseInt(spinNachtMo.getValue().toString());
        int morgens = Integer.parseInt(spinMorgens.getValue().toString());
        int mittags = Integer.parseInt(spinMittags.getValue().toString());
        int nachmittags = Integer.parseInt(spinNachmittags.getValue().toString());
        int abends = Integer.parseInt(spinAbends.getValue().toString());
        int nachtAb = Integer.parseInt(spinNachtAb.getValue().toString());

        spinUhrzeit.setValue(0);
        cmbUhrzeit.setSelectedIndex(-1);

        if (nachtMo + morgens + mittags + nachmittags + abends + nachtAb == 0) {
            spinMorgens.setValue(1);
        }

        ignoreEvent = false;
    }

    private void spinNachtAbStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinNachtAbStateChanged
        spinZeitStateChanged();
    }//GEN-LAST:event_spinNachtAbStateChanged

    private void spinAbendsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinAbendsStateChanged
        spinZeitStateChanged();
    }//GEN-LAST:event_spinAbendsStateChanged

    private void spinMittagsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinMittagsStateChanged
        spinZeitStateChanged();
    }//GEN-LAST:event_spinMittagsStateChanged

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        save();
        dispose();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void save() {
        HashMap hm = new HashMap();

        hm.put("NachtMo", spinNachtMo.getValue());
        hm.put("Morgens", spinMorgens.getValue());
        hm.put("Mittags", spinMittags.getValue());
        hm.put("Nachmittags", spinNachmittags.getValue());
        hm.put("Abends", spinAbends.getValue());
        hm.put("NachtAb", spinNachtAb.getValue());
        hm.put("XML", Tools.toXML(treeMass.getModel(), sm));
        hm.put("Dauer", txtDauer.getText());
        hm.put("UhrzeitAnzahl", spinUhrzeit.getValue());
        ListElement e = (ListElement) cmbUhrzeit.getSelectedItem();

        if (e == null) {
            hm.put("Uhrzeit", null);
        } else {
            hm.put("Uhrzeit", new Timestamp(((GregorianCalendar) e.getObject()).getTimeInMillis()));
        }

        hm.put("LDatum", jdcLDatum.getDate());

        hm.put("Taeglich", spinTaeglich.getValue());
        hm.put("Woechentlich", spinWoche.getValue());
        hm.put("Monatlich", spinMonat.getValue());
        hm.put("TagNum", spinMonatTag.getValue());


        if (cbSon.isSelected()) {
            hm.put("Son", 1);
        } else {
            hm.put("Son", 0);
        }
        if (cbSam.isSelected()) {
            hm.put("Sam", 1);
        } else {
            hm.put("Sam", 0);
        }
        if (cbFre.isSelected()) {
            hm.put("Fre", 1);
        } else {
            hm.put("Fre", 0);
        }
        if (cbDon.isSelected()) {
            hm.put("Don", 1);
        } else {
            hm.put("Don", 0);
        }
        if (cbMit.isSelected()) {
            hm.put("Mit", 1);
        } else {
            hm.put("Mit", 0);
        }
        if (cbDie.isSelected()) {
            hm.put("Die", 1);
        } else {
            hm.put("Die", 0);
        }
        if (cbMon.isSelected()) {
            hm.put("Mon", 1);
        } else {
            hm.put("Mon", 0);
        }

        if (rbMonatWTag.isSelected()) {
            hm.put(tage[cmbWTag.getSelectedIndex()], spinMonatWTag.getValue());
        }
        hm.put("Erforderlich", cbErforderlich.isSelected());
        hm.put("tmp", tmp);
        if (!group) {
            hm.put("Bemerkung", txtBemerkung.getText());
        }


        Connection db = OPDE.getDb().db;
        try {
            // Hier beginnt eine Transaktion
            db.setAutoCommit(false);
            db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            db.commit();

            for (int i = 0; i < termid.size(); i++) {
                if (!DBHandling.updateRecord("MassTermin", hm, "TermID", termid.get(i))) {
                    throw new SQLException("Fehler bei UPDATE MassTermin");
                }
            }

            db.commit();
            db.setAutoCommit(true);
            hm.clear();
        } catch (SQLException ex) {
            try {
                db.rollback();
            } catch (SQLException ex1) {
                new DlgException(ex1);
                ex1.printStackTrace();
                System.exit(1);
            }
            new DlgException(ex);
        }

    }

    private void btnDiscardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiscardActionPerformed
        this.template = null;
        this.setVisible(false);
    }//GEN-LAST:event_btnDiscardActionPerformed

    private void spinMorgensStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinMorgensStateChanged
        spinZeitStateChanged();
    }//GEN-LAST:event_spinMorgensStateChanged

    private void treeMassMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeMassMousePressed
        if (evt.isPopupTrigger()) {
            SYSTools.unregisterListeners(menu);
            menu = new JPopupMenu();

            if (treeMass.getRowForLocation(evt.getX(), evt.getY()) != -1) {
//            JMenuItem itemedit = null;
//            JMenuItem itemdelete = null;

                final TreePath curPath = treeMass.getPathForLocation(evt.getX(), evt.getY());
                DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) curPath.getLastPathComponent();
                treeMass.setSelectionPath(curPath);
                final ListElement le = (ListElement) dmtn.getUserObject();
                Object[] o = (Object[]) le.getObject();
                //Object[] o = new Object[]{beschreibung, zeit, new Vector(), new Vector(), typ, 0d};
                //Object[] modfaktor = new Object[]{label, beschreibung, zeit, prozent, new Boolean(selected)};
                Vector mdfs = (Vector) o[ParserMassnahmen.O_MODFAKTOR];
//            String label = le.getValue();
//            Double zeit = (Double) o[ParserMassnahmen.O_ZEIT];
//            Double sum = (Double) o[ParserMassnahmen.O_SUMME];
                final int typ = (Integer) o[ParserMassnahmen.O_TYP];


                // Aufbau Kontextmenü
                // ==================
                // (1) Erschwernis / Erleichterung ->  Nummer 1
                //                                     Nummer 2
                // ------------------------------------------------------------
                // Neu  ->  (2) Teilschritt (Nur Unterhalb Vorber... Nachber.. Durchf...)
                //          (3) Durchführung (nur Unterhalb Root)
                //          (4) Erschwernis / Erleichterung
                // Bearbeiten -> (5) (je nachdem, was markiert ist, nicht bei ROOT)
                //               (6)   Erschwernis / Erleichterung Nummer 1 (NUR WENN VORHANDEN)
                //                                                 Nummer 2
                // Löschen  ->  (7)(je nachdem, was markiert ist)
                //              (8)    Erschwernis / Erleichterung ->  Nummer 1
                //                                                     Nummer 2
                // --------------------------------------------------------------
                // Baum aus Vorlage zurück setzen (9)
                // Ausschneiden (+ Unterknoten und Modfaktoren)
                // Kopieren (+ Unterknoten und Modfaktoren)
                // Einfügen (an markierte Stelle)


                // ===
                // (1)
                // ===
                if (mdfs.size() > 0) { // Es gibt also mindestens eine Erleichterung.
                    // Dann müssen wir ein Untermenü erstellen, die eine Aufstellung aller
                    // MODFAKTOREN enthält.
                    JMenu menumod = new JMenu("Erschwernis/Erleichterung");

                    Enumeration e = mdfs.elements();
                    int num = 0;
                    while (e.hasMoreElements()) {
                        Object[] thisobj = (Object[]) e.nextElement();
                        boolean selected = (Boolean) thisobj[4];
                        String lbl = thisobj[0].toString();
                        String beschreibung = thisobj[1].toString();
                        JMenuItem item = new JMenuItem(lbl);
                        item.setToolTipText(beschreibung.equals("") ? null : beschreibung);
                        if (selected) {
                            item.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/darkcheck.png")));
                        }
                        final int t = num;
                        item.addActionListener(new java.awt.event.ActionListener() {

                            public void actionPerformed(java.awt.event.ActionEvent evt) {
                                Tools.selectModfaktor(t, curPath);
                                txtDauer.setText(Double.toString(Tools.calculateTree(treeMass.getModel(), sm)));
                                saveOK();
                                reloadTree();
                            }
                        });
                        num++;
                        menumod.add(item);
                    }
                    menu.add(menumod);
                    menu.add(new JSeparator(JSeparator.HORIZONTAL));
                }

                JMenu menunew = new JMenu("Neu");
                // ===
                // (3)
                // ===
                if (typ == ParserMassnahmen.TYPE_ROOT) {
                    JMenuItem item = new JMenuItem("Durchführung");

                    final DefaultMutableTreeNode mynode = dmtn;
                    item.addActionListener(new java.awt.event.ActionListener() {

                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            DlgNode dlg = new DlgNode(parent, new Object[]{ParserMassnahmen.TYPE_DF, null, null, null, null});
                            Object[] data = dlg.showDialog();
                            if (data != null) {
                                Object[] o = new Object[]{data[2].toString(), data[3], new Vector(), new Vector(), ParserMassnahmen.TYPE_DF, 0d};
                                ListElement le = new ListElement(data[1].toString(), o);
                                DefaultMutableTreeNode node = new DefaultMutableTreeNode(le);
                                // Jetzt soll der neue Knoten hinten an den Baum angefügt werden. Jedoch immer VOR der Nachbereitung.
                                // Somit:
                                ((DefaultMutableTreeNode) mynode.getRoot()).insert(node, mynode.getRoot().getChildCount() - 1);
                                ((DefaultTreeModel) treeMass.getModel()).reload();
                                saveOK();
                            }
                            dlg.dispose();
                        }
                    });
                    menunew.add(item);
                }
                // ===
                // (2)
                // ===
                if (typ == ParserMassnahmen.TYPE_Vorbereitung ||
                        typ == ParserMassnahmen.TYPE_Nachbereitung ||
                        typ == ParserMassnahmen.TYPE_DF ||
                        typ == ParserMassnahmen.TYPE_Teilschritt) {
                    JMenuItem item = new JMenuItem("Teilschritt");
                    final DefaultMutableTreeNode mynode = dmtn;
                    item.addActionListener(new java.awt.event.ActionListener() {

                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            DlgNode dlg = new DlgNode(parent, new Object[]{ParserMassnahmen.TYPE_Teilschritt, null, null, null, null});
                            Object[] data = dlg.showDialog();
                            if (data != null) {
                                Object[] o = new Object[]{data[2].toString(), data[3], new Vector(), new Vector(), ParserMassnahmen.TYPE_DF, 0d};
                                ListElement le = new ListElement(data[1].toString(), o);
                                DefaultMutableTreeNode node = new DefaultMutableTreeNode(le);
                                mynode.add(node);
                                Enumeration expansion = treeMass.getExpandedDescendants(new TreePath(treeMass.getModel().getRoot()));
                                ((DefaultTreeModel) treeMass.getModel()).reload();
                                while (expansion.hasMoreElements()) {
                                    treeMass.expandPath((TreePath) expansion.nextElement());
                                }
                            }
                            saveOK();
                            dlg.dispose();
                        }
                    });
                    menunew.add(item);
                }
                // ===
                // (4)
                // ===
                JMenuItem item1 = new JMenuItem("Erschwernis/Erleichterung");
                final DefaultMutableTreeNode mynode1 = dmtn;
                item1.addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        DlgNode dlg = new DlgNode(parent, new Object[]{ParserMassnahmen.TYPE_MODFAKTOR, null, null, null, null});
                        Object[] data = dlg.showDialog();
                        if (data != null) {
                            String label = data[1].toString();
                            String beschreibung = data[2].toString();
                            double zeit = (Double) data[3];
                            double prozent = (Double) data[4];
                            Object[] modfaktor = new Object[]{label, beschreibung, zeit, prozent, new Boolean(false)};
                            ListElement le = (ListElement) mynode1.getUserObject();
                            Object[] o = (Object[]) le.getObject();
                            Vector mdfs = (Vector) o[ParserMassnahmen.O_MODFAKTOR];
                            mdfs.add(modfaktor);
                            reloadTree();
                            saveOK();
                        }
                        dlg.dispose();
                    }
                });
                menunew.add(item1);

                menu.add(menunew);

                JMenu menuedit = null;
                if (typ != ParserMassnahmen.TYPE_ROOT) {
                    menuedit = new JMenu("Bearbeiten");
                    // ===
                    // (5)
                    // ===
                    ListElement le4label = (ListElement) dmtn.getUserObject();
                    JMenuItem itemNode = new JMenuItem(le4label.getValue());
                    final DefaultMutableTreeNode mynode = dmtn;
                    itemNode.addActionListener(new java.awt.event.ActionListener() {

                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            ListElement le = (ListElement) mynode.getUserObject();
                            Object[] o = (Object[]) le.getObject();
                            DlgNode dlg = new DlgNode(parent, new Object[]{typ, le.getValue(), o[0], o[1], null});
                            Object[] data = dlg.showDialog();
                            if (data != null) {
                                //Object[] o = new Object[]{data[2].toString(), data[3], new Vector(), new Vector(), ParserMassnahmen.TYPE_DF, 0d};
                                o[0] = data[2].toString();
                                o[1] = data[3];
                                ListElement newle = new ListElement(data[1].toString(), o);
                                mynode.setUserObject(newle);
                                txtDauer.setText(Double.toString(Tools.calculateTree(treeMass.getModel(), sm)));
                                saveOK();
                            }
                            dlg.dispose();
                        }
                    });
                    menuedit.add(itemNode);
                }


                // ===
                // (6)
                // ===
                if (mdfs.size() > 0) {
                    if (menuedit == null) {
                        menuedit = new JMenu("Bearbeiten");
                    }
                    final Vector mymdfs = mdfs;
                    Enumeration e = mdfs.elements();
                    int num = 0;
                    while (e.hasMoreElements()) {
                        Object[] thisobj = (Object[]) e.nextElement();
                        final boolean selected = (Boolean) thisobj[ParserMassnahmen.O_MF_SELECTED];
                        final String lbl = thisobj[ParserMassnahmen.O_MF_LABEL].toString();
                        final String beschreibung = thisobj[ParserMassnahmen.O_MF_BESCHREIBUNG].toString();
                        final double zeit1 = (Double) thisobj[ParserMassnahmen.O_MF_ZEIT];
                        final double prozent1 = (Double) thisobj[ParserMassnahmen.O_MF_PROZENT];
                        JMenuItem item = new JMenuItem(lbl);
                        item.setToolTipText(beschreibung.equals("") ? null : beschreibung);
                        final int t = num;
                        item.addActionListener(new java.awt.event.ActionListener() {

                            public void actionPerformed(java.awt.event.ActionEvent evt) {
                                //{typ, le.getValue(), o[0], o[1], null}
                                DlgNode dlg = new DlgNode(parent, new Object[]{ParserMassnahmen.TYPE_MODFAKTOR, lbl, beschreibung, zeit1, prozent1});
                                Object[] data = dlg.showDialog();
                                if (data != null) {
                                    String newlabel = data[1].toString();
                                    String newbeschreibung = data[2].toString();
                                    double newzeit = (Double) data[3];
                                    double newprozent = (Double) data[4];
                                    Object[] modfaktor = new Object[]{newlabel, newbeschreibung, newzeit, newprozent, new Boolean(selected)};
                                    mymdfs.set(t, modfaktor);
                                    txtDauer.setText(Double.toString(Tools.calculateTree(treeMass.getModel(), sm)));
                                    saveOK();
                                }
                                dlg.dispose();
                            }
                        });
                        num++;
                        menuedit.add(item);
                    }
                }
                if (menuedit != null) {
                    menu.add(menuedit);
                }


                // ===
                // (7)
                // ===
                JMenu menudel = new JMenu("Löschen");

                final ListElement le4label = (ListElement) dmtn.getUserObject();
                JMenuItem itemNode = new JMenuItem(le4label.getValue());
                final DefaultMutableTreeNode mynode = dmtn;
                itemNode.addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        if (mynode.isLeaf() ||
                                JOptionPane.showConfirmDialog(parent, "Damit wird der ganze Teilbaum gelöscht.\n\nSind Sie sicher ?", le4label.getValue() + " entfernen ??", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            // Drei Fälle
                            if (typ == ParserMassnahmen.TYPE_Teilschritt ||
                                    typ == ParserMassnahmen.TYPE_DF) {
                                mynode.removeFromParent();
                            } else if (typ == ParserMassnahmen.TYPE_Vorbereitung ||
                                    typ == ParserMassnahmen.TYPE_Nachbereitung) {
                                mynode.removeAllChildren();
                            } else { // ROOT
                                treeMass.setModel(new DefaultTreeModel(null));
                            }
                            ((DefaultTreeModel) treeMass.getModel()).reload();
                            txtDauer.setText(Double.toString(Tools.calculateTree(treeMass.getModel(), sm)));
                            saveOK();
                            SYSTools.expandAll(treeMass);
                        }
                    }
                });
                menudel.add(itemNode);

                // ===
                // (8)
                // ===
                if (mdfs.size() > 0) {
                    menudel.add(new JSeparator());
                    final Vector mymdfs = mdfs;
                    Enumeration e = mdfs.elements();
                    int num = 0;
                    //final DefaultMutableTreeNode mynode = dmtn;
                    while (e.hasMoreElements()) {
                        Object[] thisobj = (Object[]) e.nextElement();
                        final String lbl = thisobj[ParserMassnahmen.O_MF_LABEL].toString();
                        final String beschreibung = thisobj[ParserMassnahmen.O_MF_BESCHREIBUNG].toString();
                        JMenuItem item = new JMenuItem(lbl);
                        item.setToolTipText(beschreibung.equals("") ? null : beschreibung);
                        final int t = num;
                        item.addActionListener(new java.awt.event.ActionListener() {

                            public void actionPerformed(java.awt.event.ActionEvent evt) {
                                mymdfs.remove(t);
                                txtDauer.setText(Double.toString(Tools.calculateTree(treeMass.getModel(), sm)));
                            }
                        });
                        num++;
                        menudel.add(item);
                    }
                    saveOK();
                }
                menu.add(menudel);

                // ===
                // (9)
                // ===
                menu.add(new JSeparator());
                JMenuItem menurestore = new JMenuItem("Baum auf Ausgangszustand zurücksetzen");
                menurestore.addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        if (JOptionPane.showConfirmDialog(parent, "Wirklich ?", "Auf Ausgangszustand zurücksetzen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            createTree(SYSTools.catchNull(template.get("XML")));
                        }
                    }
                });
                menu.add(menurestore);

                // ===
                // (10)
                // ===
                JMenuItem menutemplate = new JMenuItem("Baum auf Vorlage zurücksetzen");
                menutemplate.addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        if (JOptionPane.showConfirmDialog(parent, "Wirklich ?", "Auf Vorlage zurücksetzen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            createTree(SYSTools.catchNull(DBRetrieve.getSingleValue("Massnahmen", "XMLT", "MassID", template.get("MassID"))));
                        }
                    }
                });
                menu.add(menutemplate);
            } else {
                JMenuItem item = new JMenuItem("Neuen Baum erstellen");

                item.addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        createNewTree();
                        saveOK();
                    }
                });
                menu.add(item);
                item.setEnabled(treeMass.getModel().getRoot() == null);
            }
            menu.show(evt.getComponent(), evt.getX(), evt.getY());
            // Weiter gehts. Bring den Baum bei den Planungen zum laufen
        }
    }//GEN-LAST:event_treeMassMousePressed

    private void jtpMainStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jtpMainStateChanged
    }//GEN-LAST:event_jtpMainStateChanged

    private void txtDauerCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtDauerCaretUpdate
        if (ignoreEvent) {
            return;
        }
        saveOK();
    }//GEN-LAST:event_txtDauerCaretUpdate

    private void saveOK() {
        //HashMap filter = new HashMap();
        boolean zeitVorhanden = false;
        try {
            Double.parseDouble(txtDauer.getText());
            zeitVorhanden = true;
        } catch (NumberFormatException numberFormatException) {
            zeitVorhanden = false;
        }
        btnSave.setEnabled(zeitVorhanden);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgMonat;
    private javax.swing.ButtonGroup bgWdh;
    private javax.swing.JButton btnDiscard;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox cbDie;
    private javax.swing.JCheckBox cbDon;
    private javax.swing.JCheckBox cbErforderlich;
    private javax.swing.JCheckBox cbFre;
    private javax.swing.JCheckBox cbMit;
    private javax.swing.JCheckBox cbMon;
    private javax.swing.JCheckBox cbSam;
    private javax.swing.JCheckBox cbSon;
    private javax.swing.JComboBox cmbUhrzeit;
    private javax.swing.JComboBox cmbWTag;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private com.toedter.calendar.JDateChooser jdcLDatum;
    private javax.swing.JTabbedPane jtpMain;
    private javax.swing.JLabel lblEin1;
    private javax.swing.JLabel lblEin2;
    private javax.swing.JLabel lblEin3;
    private javax.swing.JLabel lblEin4;
    private javax.swing.JLabel lblEin5;
    private javax.swing.JLabel lblEin6;
    private javax.swing.JLabel lblEin7;
    private javax.swing.JLabel lblLDatum;
    private javax.swing.JLabel lblMassnahme;
    private javax.swing.JPanel pnlRegular;
    private javax.swing.JPanel pnlTermin;
    private javax.swing.JPanel pnlWdh;
    private javax.swing.JRadioButton rbMonat;
    private javax.swing.JRadioButton rbMonatTag;
    private javax.swing.JRadioButton rbMonatWTag;
    private javax.swing.JRadioButton rbTag;
    private javax.swing.JRadioButton rbWoche;
    private javax.swing.JSeparator sep1;
    private javax.swing.JSpinner spinAbends;
    private javax.swing.JSpinner spinMittags;
    private javax.swing.JSpinner spinMonat;
    private javax.swing.JSpinner spinMonatTag;
    private javax.swing.JSpinner spinMonatWTag;
    private javax.swing.JSpinner spinMorgens;
    private javax.swing.JSpinner spinNachmittags;
    private javax.swing.JSpinner spinNachtAb;
    private javax.swing.JSpinner spinNachtMo;
    private javax.swing.JSpinner spinTaeglich;
    private javax.swing.JSpinner spinUhrzeit;
    private javax.swing.JSpinner spinWoche;
    private javax.swing.JTree treeMass;
    private javax.swing.JTextArea txtBemerkung;
    private javax.swing.JTextField txtDauer;
    // End of variables declaration//GEN-END:variables
}
