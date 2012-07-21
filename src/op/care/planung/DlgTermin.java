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

import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.event.*;
import com.toedter.calendar.*;
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
//        bgMonat.add(dummy);

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
        jtpMain = new JTabbedPane();
        pnlTermin = new JPanel();
        pnlRegular = new JPanel();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        jLabel4 = new JLabel();
        jLabel5 = new JLabel();
        spinMorgens = new JSpinner();
        spinMittags = new JSpinner();
        spinAbends = new JSpinner();
        spinNachtAb = new JSpinner();
        spinNachtMo = new JSpinner();
        jLabel6 = new JLabel();
        cmbUhrzeit = new JComboBox();
        spinUhrzeit = new JSpinner();
        spinNachmittags = new JSpinner();
        jLabel11 = new JLabel();
        lblEin1 = new JLabel();
        lblEin2 = new JLabel();
        lblEin3 = new JLabel();
        lblEin4 = new JLabel();
        lblEin5 = new JLabel();
        lblEin6 = new JLabel();
        lblEin7 = new JLabel();
        cbErforderlich = new JCheckBox();
        pnlWdh = new JPanel();
        rbTag = new JRadioButton();
        rbWoche = new JRadioButton();
        rbMonat = new JRadioButton();
        jLabel7 = new JLabel();
        jLabel8 = new JLabel();
        cbMon = new JCheckBox();
        cbDie = new JCheckBox();
        cbMit = new JCheckBox();
        cbDon = new JCheckBox();
        cbFre = new JCheckBox();
        cbSam = new JCheckBox();
        cbSon = new JCheckBox();
        jLabel9 = new JLabel();
        rbMonatTag = new JRadioButton();
        jLabel10 = new JLabel();
        rbMonatWTag = new JRadioButton();
        cmbWTag = new JComboBox();
        spinTaeglich = new JSpinner();
        spinWoche = new JSpinner();
        spinMonat = new JSpinner();
        spinMonatTag = new JSpinner();
        spinMonatWTag = new JSpinner();
        lblLDatum = new JLabel();
        jdcLDatum = new JDateChooser();
        sep1 = new JSeparator();
        jPanel1 = new JPanel();
        jScrollPane1 = new JScrollPane();
        txtBemerkung = new JTextArea();
        jPanel3 = new JPanel();
        jLabel12 = new JLabel();
        txtDauer = new JTextField();
        jLabel13 = new JLabel();
        jScrollPane2 = new JScrollPane();
        treeMass = new JTree();
        jPanel4 = new JPanel();
        btnSave = new JButton();
        btnDiscard = new JButton();
        lblMassnahme = new JLabel();
        jPanel2 = new JPanel();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();

        //======== jtpMain ========
        {
            jtpMain.setTabPlacement(SwingConstants.BOTTOM);
            jtpMain.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    jtpMainStateChanged(e);
                }
            });

            //======== pnlTermin ========
            {

                //======== pnlRegular ========
                {
                    pnlRegular.setBorder(new TitledBorder("H\u00e4ufigkeit der Anwendung"));

                    //---- jLabel1 ----
                    jLabel1.setForeground(new Color(0, 0, 204));
                    jLabel1.setText("Morgens:");

                    //---- jLabel2 ----
                    jLabel2.setForeground(new Color(255, 102, 0));
                    jLabel2.setText("Mittags:");

                    //---- jLabel3 ----
                    jLabel3.setForeground(new Color(255, 0, 51));
                    jLabel3.setText("Abends:");

                    //---- jLabel4 ----
                    jLabel4.setText("Nacht, sp\u00e4t abends:");

                    //---- jLabel5 ----
                    jLabel5.setText("Anzahl");

                    //---- spinMorgens ----
                    spinMorgens.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            spinMorgensStateChanged(e);
                        }
                    });

                    //---- spinMittags ----
                    spinMittags.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            spinMittagsStateChanged(e);
                        }
                    });

                    //---- spinAbends ----
                    spinAbends.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            spinAbendsStateChanged(e);
                        }
                    });

                    //---- spinNachtAb ----
                    spinNachtAb.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            spinNachtAbStateChanged(e);
                        }
                    });

                    //---- spinNachtMo ----
                    spinNachtMo.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            spinNachtMoStateChanged(e);
                        }
                    });
                    spinNachtMo.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            spinNachtMoFocusGained(e);
                        }
                    });

                    //---- jLabel6 ----
                    jLabel6.setText("Nachts, fr\u00fch morgens:");

                    //---- cmbUhrzeit ----
                    cmbUhrzeit.setModel(new DefaultComboBoxModel(new String[] {
                        "10:00",
                        "10:15",
                        "10:30",
                        "10:45"
                    }));
                    cmbUhrzeit.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            cmbUhrzeitItemStateChanged(e);
                        }
                    });

                    //---- spinUhrzeit ----
                    spinUhrzeit.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            spinUhrzeitStateChanged(e);
                        }
                    });

                    //---- spinNachmittags ----
                    spinNachmittags.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            spinNachmittagsStateChanged(e);
                        }
                    });

                    //---- jLabel11 ----
                    jLabel11.setForeground(new Color(0, 153, 51));
                    jLabel11.setText("Nachmittag:");

                    //---- lblEin1 ----
                    lblEin1.setFont(new Font("Dialog", Font.PLAIN, 10));
                    lblEin1.setText("x");

                    //---- lblEin2 ----
                    lblEin2.setFont(new Font("Dialog", Font.PLAIN, 10));
                    lblEin2.setText("x");

                    //---- lblEin3 ----
                    lblEin3.setFont(new Font("Dialog", Font.PLAIN, 10));
                    lblEin3.setText("x");

                    //---- lblEin4 ----
                    lblEin4.setFont(new Font("Dialog", Font.PLAIN, 10));
                    lblEin4.setText("x");

                    //---- lblEin5 ----
                    lblEin5.setFont(new Font("Dialog", Font.PLAIN, 10));
                    lblEin5.setText("x");

                    //---- lblEin6 ----
                    lblEin6.setFont(new Font("Dialog", Font.PLAIN, 10));
                    lblEin6.setText("x");

                    //---- lblEin7 ----
                    lblEin7.setFont(new Font("Dialog", Font.PLAIN, 10));
                    lblEin7.setText("x");

                    //---- cbErforderlich ----
                    cbErforderlich.setText("Bearbeitung erforderlich");
                    cbErforderlich.setToolTipText("Wird solange, immer wieder auf den DFN gesetzt, bis jemand die Massnahme als \"erledigt\" oder \"unerledigt\" markiert.");
                    cbErforderlich.setMargin(new Insets(0, 0, 0, 0));

                    GroupLayout pnlRegularLayout = new GroupLayout(pnlRegular);
                    pnlRegular.setLayout(pnlRegularLayout);
                    pnlRegularLayout.setHorizontalGroup(
                        pnlRegularLayout.createParallelGroup()
                            .addGroup(pnlRegularLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pnlRegularLayout.createParallelGroup()
                                    .addGroup(pnlRegularLayout.createSequentialGroup()
                                        .addGroup(pnlRegularLayout.createParallelGroup()
                                            .addComponent(jLabel1)
                                            .addComponent(jLabel2)
                                            .addComponent(jLabel6)
                                            .addComponent(jLabel11)
                                            .addComponent(jLabel3)
                                            .addGroup(pnlRegularLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(cmbUhrzeit, GroupLayout.Alignment.LEADING, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel4, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                        .addGap(8, 8, 8)
                                        .addGroup(pnlRegularLayout.createParallelGroup()
                                            .addComponent(spinUhrzeit, GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                                            .addComponent(spinNachtAb, GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                                            .addComponent(spinAbends, GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                                            .addComponent(spinNachmittags, GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                                            .addComponent(spinMittags, GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                                            .addComponent(spinMorgens, GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                                            .addComponent(spinNachtMo, GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                                            .addComponent(jLabel5, GroupLayout.Alignment.TRAILING))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(pnlRegularLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(lblEin7, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblEin6, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblEin5, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblEin3, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblEin4, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblEin2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblEin1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addComponent(cbErforderlich))
                                .addContainerGap())
                    );
                    pnlRegularLayout.setVerticalGroup(
                        pnlRegularLayout.createParallelGroup()
                            .addGroup(pnlRegularLayout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlRegularLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel6)
                                    .addComponent(spinNachtMo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblEin1))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlRegularLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel1)
                                    .addComponent(spinMorgens, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblEin2))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlRegularLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2)
                                    .addComponent(spinMittags, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblEin3))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlRegularLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel11)
                                    .addComponent(spinNachmittags, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblEin4))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlRegularLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3)
                                    .addComponent(spinAbends, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblEin5))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlRegularLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(spinNachtAb, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblEin6))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlRegularLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(cmbUhrzeit, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(spinUhrzeit, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblEin7))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cbErforderlich)
                                .addContainerGap(17, Short.MAX_VALUE))
                    );
                }

                //======== pnlWdh ========
                {
                    pnlWdh.setBorder(new TitledBorder("Wiederholungen"));

                    //---- rbTag ----
                    rbTag.setText("t\u00e4glich alle");
                    rbTag.setBorder(BorderFactory.createEmptyBorder());
                    rbTag.setMargin(new Insets(0, 0, 0, 0));
                    rbTag.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            rbTagActionPerformed(e);
                        }
                    });

                    //---- rbWoche ----
                    rbWoche.setText("w\u00f6chentlich alle");
                    rbWoche.setBorder(BorderFactory.createEmptyBorder());
                    rbWoche.setMargin(new Insets(0, 0, 0, 0));
                    rbWoche.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            rbWocheActionPerformed(e);
                        }
                    });

                    //---- rbMonat ----
                    rbMonat.setText("monatlich alle");
                    rbMonat.setBorder(BorderFactory.createEmptyBorder());
                    rbMonat.setMargin(new Insets(0, 0, 0, 0));
                    rbMonat.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            rbMonatActionPerformed(e);
                        }
                    });

                    //---- jLabel7 ----
                    jLabel7.setText("Tage");

                    //---- jLabel8 ----
                    jLabel8.setText("Wochen am:");

                    //---- cbMon ----
                    cbMon.setText("Mon");
                    cbMon.setBorder(BorderFactory.createEmptyBorder());
                    cbMon.setMargin(new Insets(0, 0, 0, 0));
                    cbMon.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cbMonActionPerformed(e);
                        }
                    });

                    //---- cbDie ----
                    cbDie.setText("Die");
                    cbDie.setBorder(BorderFactory.createEmptyBorder());
                    cbDie.setMargin(new Insets(0, 0, 0, 0));
                    cbDie.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cbDieActionPerformed(e);
                        }
                    });

                    //---- cbMit ----
                    cbMit.setText("Mit");
                    cbMit.setBorder(BorderFactory.createEmptyBorder());
                    cbMit.setMargin(new Insets(0, 0, 0, 0));
                    cbMit.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cbMitActionPerformed(e);
                        }
                    });

                    //---- cbDon ----
                    cbDon.setText("Don");
                    cbDon.setBorder(BorderFactory.createEmptyBorder());
                    cbDon.setMargin(new Insets(0, 0, 0, 0));
                    cbDon.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cbDonActionPerformed(e);
                        }
                    });

                    //---- cbFre ----
                    cbFre.setText("Fre");
                    cbFre.setBorder(BorderFactory.createEmptyBorder());
                    cbFre.setMargin(new Insets(0, 0, 0, 0));
                    cbFre.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cbFreActionPerformed(e);
                        }
                    });

                    //---- cbSam ----
                    cbSam.setText("Sam");
                    cbSam.setBorder(BorderFactory.createEmptyBorder());
                    cbSam.setMargin(new Insets(0, 0, 0, 0));
                    cbSam.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cbSamActionPerformed(e);
                        }
                    });

                    //---- cbSon ----
                    cbSon.setText("Son");
                    cbSon.setBorder(BorderFactory.createEmptyBorder());
                    cbSon.setMargin(new Insets(0, 0, 0, 0));
                    cbSon.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cbSonActionPerformed(e);
                        }
                    });

                    //---- jLabel9 ----
                    jLabel9.setText("Monat(e)");

                    //---- rbMonatTag ----
                    rbMonatTag.setText("wiederholt am");
                    rbMonatTag.setBorder(BorderFactory.createEmptyBorder());
                    rbMonatTag.setMargin(new Insets(0, 0, 0, 0));
                    rbMonatTag.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            rbMonatTagActionPerformed(e);
                        }
                    });

                    //---- jLabel10 ----
                    jLabel10.setText("Tag");

                    //---- rbMonatWTag ----
                    rbMonatWTag.setText("wiederholt am");
                    rbMonatWTag.setBorder(BorderFactory.createEmptyBorder());
                    rbMonatWTag.setMargin(new Insets(0, 0, 0, 0));
                    rbMonatWTag.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            rbMonatWTagActionPerformed(e);
                        }
                    });

                    //---- cmbWTag ----
                    cmbWTag.setModel(new DefaultComboBoxModel(new String[] {
                        "Montag",
                        "Dienstag",
                        "Mittwoch",
                        "Donnerstag",
                        "Freitag",
                        "Samstag",
                        "Sonntag"
                    }));
                    cmbWTag.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            cmbWTagItemStateChanged(e);
                        }
                    });

                    //---- spinTaeglich ----
                    spinTaeglich.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            spinTaeglichStateChanged(e);
                        }
                    });

                    //---- spinWoche ----
                    spinWoche.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            spinWocheStateChanged(e);
                        }
                    });

                    //---- spinMonat ----
                    spinMonat.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            spinMonatStateChanged(e);
                        }
                    });

                    //---- spinMonatTag ----
                    spinMonatTag.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            spinMonatTagStateChanged(e);
                        }
                    });

                    //---- spinMonatWTag ----
                    spinMonatWTag.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            spinMonatWTagStateChanged(e);
                        }
                    });

                    //---- lblLDatum ----
                    lblLDatum.setText("Erste Anwendung am:");

                    //---- jdcLDatum ----
                    jdcLDatum.setEnabled(false);

                    GroupLayout pnlWdhLayout = new GroupLayout(pnlWdh);
                    pnlWdh.setLayout(pnlWdhLayout);
                    pnlWdhLayout.setHorizontalGroup(
                        pnlWdhLayout.createParallelGroup()
                            .addGroup(pnlWdhLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pnlWdhLayout.createParallelGroup()
                                    .addComponent(sep1, GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
                                    .addGroup(pnlWdhLayout.createSequentialGroup()
                                        .addComponent(rbTag)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(spinTaeglich, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel7))
                                    .addGroup(pnlWdhLayout.createSequentialGroup()
                                        .addComponent(rbWoche)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(spinWoche, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel8))
                                    .addGroup(pnlWdhLayout.createSequentialGroup()
                                        .addGap(17, 17, 17)
                                        .addGroup(pnlWdhLayout.createParallelGroup()
                                            .addGroup(pnlWdhLayout.createSequentialGroup()
                                                .addComponent(cbFre)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cbSam)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cbSon))
                                            .addGroup(pnlWdhLayout.createSequentialGroup()
                                                .addComponent(cbMon)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cbDie)
                                                .addGap(16, 16, 16)
                                                .addComponent(cbMit)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cbDon))))
                                    .addGroup(pnlWdhLayout.createSequentialGroup()
                                        .addGap(17, 17, 17)
                                        .addGroup(pnlWdhLayout.createParallelGroup()
                                            .addGroup(pnlWdhLayout.createSequentialGroup()
                                                .addComponent(rbMonatTag)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(spinMonatTag, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE))
                                            .addGroup(pnlWdhLayout.createSequentialGroup()
                                                .addComponent(rbMonatWTag)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(spinMonatWTag, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)))
                                        .addGap(20, 20, 20)
                                        .addGroup(pnlWdhLayout.createParallelGroup()
                                            .addComponent(jLabel10)
                                            .addComponent(cmbWTag, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(pnlWdhLayout.createSequentialGroup()
                                        .addComponent(rbMonat)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(spinMonat, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel9))
                                    .addGroup(GroupLayout.Alignment.TRAILING, pnlWdhLayout.createSequentialGroup()
                                        .addComponent(lblLDatum)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jdcLDatum, GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)))
                                .addContainerGap())
                    );
                    pnlWdhLayout.setVerticalGroup(
                        pnlWdhLayout.createParallelGroup()
                            .addGroup(pnlWdhLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pnlWdhLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(rbTag)
                                    .addComponent(spinTaeglich, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlWdhLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(rbWoche)
                                    .addComponent(spinWoche, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlWdhLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(cbMon)
                                    .addComponent(cbDie)
                                    .addComponent(cbMit)
                                    .addComponent(cbDon))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlWdhLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(cbFre)
                                    .addComponent(cbSam)
                                    .addComponent(cbSon))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlWdhLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(rbMonat)
                                    .addComponent(jLabel9)
                                    .addComponent(spinMonat, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlWdhLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(rbMonatTag)
                                    .addComponent(spinMonatTag, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlWdhLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(rbMonatWTag)
                                    .addComponent(spinMonatWTag, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmbWTag, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sep1, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                                .addGroup(pnlWdhLayout.createParallelGroup()
                                    .addComponent(jdcLDatum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblLDatum))
                                .addContainerGap())
                    );
                }

                //======== jPanel1 ========
                {
                    jPanel1.setBorder(new TitledBorder("Kommentar zur Anwendung (Erscheint im DFN)"));

                    //======== jScrollPane1 ========
                    {

                        //---- txtBemerkung ----
                        txtBemerkung.setColumns(20);
                        txtBemerkung.setRows(5);
                        jScrollPane1.setViewportView(txtBemerkung);
                    }

                    GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
                    jPanel1.setLayout(jPanel1Layout);
                    jPanel1Layout.setHorizontalGroup(
                        jPanel1Layout.createParallelGroup()
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
                                .addContainerGap())
                    );
                    jPanel1Layout.setVerticalGroup(
                        jPanel1Layout.createParallelGroup()
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                                .addContainerGap())
                    );
                }

                GroupLayout pnlTerminLayout = new GroupLayout(pnlTermin);
                pnlTermin.setLayout(pnlTerminLayout);
                pnlTerminLayout.setHorizontalGroup(
                    pnlTerminLayout.createParallelGroup()
                        .addGroup(pnlTerminLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(pnlTerminLayout.createParallelGroup()
                                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(GroupLayout.Alignment.TRAILING, pnlTerminLayout.createSequentialGroup()
                                    .addComponent(pnlRegular, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(pnlWdh, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                            .addContainerGap())
                );
                pnlTerminLayout.setVerticalGroup(
                    pnlTerminLayout.createParallelGroup()
                        .addGroup(pnlTerminLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(pnlTerminLayout.createParallelGroup()
                                .addComponent(pnlWdh, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(pnlRegular, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGap(9, 9, 9)
                            .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addContainerGap())
                );
            }
            jtpMain.addTab("Termine", pnlTermin);


            //======== jPanel3 ========
            {

                //---- jLabel12 ----
                jLabel12.setText("Dauer:");

                //---- txtDauer ----
                txtDauer.setText("jTextField1");
                txtDauer.addCaretListener(new CaretListener() {
                    @Override
                    public void caretUpdate(CaretEvent e) {
                        txtDauerCaretUpdate(e);
                    }
                });

                //---- jLabel13 ----
                jLabel13.setText("Minuten");

                //======== jScrollPane2 ========
                {

                    //---- treeMass ----
                    treeMass.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                            treeMassMousePressed(e);
                        }
                    });
                    jScrollPane2.setViewportView(treeMass);
                }

                GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
                jPanel3.setLayout(jPanel3Layout);
                jPanel3Layout.setHorizontalGroup(
                    jPanel3Layout.createParallelGroup()
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel3Layout.createParallelGroup()
                                .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 651, Short.MAX_VALUE)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(jLabel12)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtDauer, GroupLayout.PREFERRED_SIZE, 178, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel13)))
                            .addContainerGap())
                );
                jPanel3Layout.setVerticalGroup(
                    jPanel3Layout.createParallelGroup()
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel12)
                                .addComponent(txtDauer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel13))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                            .addContainerGap())
                );
            }
            jtpMain.addTab("Aufwand", jPanel3);

        }

        //======== jPanel4 ========
        {

            //---- btnSave ----
            btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnSave.setText("Speichern");
            btnSave.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnSaveActionPerformed(e);
                }
            });

            //---- btnDiscard ----
            btnDiscard.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
            btnDiscard.setText("Verwerfen");
            btnDiscard.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnDiscardActionPerformed(e);
                }
            });

            GroupLayout jPanel4Layout = new GroupLayout(jPanel4);
            jPanel4.setLayout(jPanel4Layout);
            jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup()
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnSave)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDiscard)
                        .addContainerGap())
            );
            jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup()
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(btnDiscard)
                            .addComponent(btnSave))
                        .addContainerGap())
            );
        }

        //---- lblMassnahme ----
        lblMassnahme.setFont(new Font("Dialog", Font.BOLD, 14));
        lblMassnahme.setForeground(new Color(51, 51, 255));
        lblMassnahme.setText("jLabel5");

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(jtpMain, GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE)
                                .addComponent(lblMassnahme, GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE))
                            .addGap(12, 12, 12))
                        .addComponent(jPanel4, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblMassnahme)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jtpMain, GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
        setSize(714, 633);
        setLocationRelativeTo(null);

        //======== jPanel2 ========
        {

            GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
            jPanel2.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup()
                    .addGap(0, 100, Short.MAX_VALUE)
            );
            jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup()
                    .addGap(0, 100, Short.MAX_VALUE)
            );
        }

        //---- bgWdh ----
        ButtonGroup bgWdh = new ButtonGroup();
        bgWdh.add(rbTag);
        bgWdh.add(rbWoche);
        bgWdh.add(rbMonat);

        //---- bgMonat ----
        ButtonGroup bgMonat = new ButtonGroup();
        bgMonat.add(rbMonatTag);
        bgMonat.add(rbMonatWTag);
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
    private JTabbedPane jtpMain;
    private JPanel pnlTermin;
    private JPanel pnlRegular;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JSpinner spinMorgens;
    private JSpinner spinMittags;
    private JSpinner spinAbends;
    private JSpinner spinNachtAb;
    private JSpinner spinNachtMo;
    private JLabel jLabel6;
    private JComboBox cmbUhrzeit;
    private JSpinner spinUhrzeit;
    private JSpinner spinNachmittags;
    private JLabel jLabel11;
    private JLabel lblEin1;
    private JLabel lblEin2;
    private JLabel lblEin3;
    private JLabel lblEin4;
    private JLabel lblEin5;
    private JLabel lblEin6;
    private JLabel lblEin7;
    private JCheckBox cbErforderlich;
    private JPanel pnlWdh;
    private JRadioButton rbTag;
    private JRadioButton rbWoche;
    private JRadioButton rbMonat;
    private JLabel jLabel7;
    private JLabel jLabel8;
    private JCheckBox cbMon;
    private JCheckBox cbDie;
    private JCheckBox cbMit;
    private JCheckBox cbDon;
    private JCheckBox cbFre;
    private JCheckBox cbSam;
    private JCheckBox cbSon;
    private JLabel jLabel9;
    private JRadioButton rbMonatTag;
    private JLabel jLabel10;
    private JRadioButton rbMonatWTag;
    private JComboBox cmbWTag;
    private JSpinner spinTaeglich;
    private JSpinner spinWoche;
    private JSpinner spinMonat;
    private JSpinner spinMonatTag;
    private JSpinner spinMonatWTag;
    private JLabel lblLDatum;
    private JDateChooser jdcLDatum;
    private JSeparator sep1;
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private JTextArea txtBemerkung;
    private JPanel jPanel3;
    private JLabel jLabel12;
    private JTextField txtDauer;
    private JLabel jLabel13;
    private JScrollPane jScrollPane2;
    private JTree treeMass;
    private JPanel jPanel4;
    private JButton btnSave;
    private JButton btnDiscard;
    private JLabel lblMassnahme;
    private JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
