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
package op.care.planung.massnahmen;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import op.OPDE;
import op.share.bwinfo.BWInfo;
import op.tools.CheckTreeManager;
import op.tools.CheckTreeSelectionModel;
import op.tools.DBHandling;
import op.tools.DBRetrieve;
import op.tools.ListElement;
import op.tools.SYSTools;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author  tloehr
 */
public class FrmMassnahmen extends javax.swing.JFrame {

    public static final int ART_PFLEGE = 1;
    public static final int ART_BHP = 2;
    public static final int ART_SOZIAL = 4;
    private CheckTreeManager cm;
    private cbClickListener cblst;
    private CheckTreeSelectionModel sm;
    private JPopupMenu menu;
    private JFrame parent;
    private long massid = -1;
    private boolean ignoreEvent = false;

    /** Creates new form FrmMassnahmen */
    public FrmMassnahmen() {
        parent = this;
        initForm();
    }

    private void initForm() {
        initComponents();
        ignoreEvent = true;
        setTitle(SYSTools.getWindowTitle("Massnahmen Datenbank"));
        cmbKategorie.setModel(op.share.bwinfo.DBHandling.ladeKategorien(BWInfo.ART_PFLEGE, false, false));
        cmbKategorie.setSelectedIndex(-1);

        Object[] arten = new Object[]{new ListElement("Pflege", ART_PFLEGE), new ListElement("BHP", ART_BHP), new ListElement("Sozial", ART_SOZIAL)};
        cmbArt.setModel(new DefaultComboBoxModel(arten));
        cmbArt.setSelectedIndex(-1);

        lblMassnahme.setText("");
        txtBezeichnung.setText("");
        txtDauer.setText("");
        treeMass.setModel(new DefaultTreeModel(null));
        lstMass.setModel(new DefaultListModel());
        ignoreEvent = false;

        //createTree();
        SYSTools.center(this);
        setVisible(true);
    }

    @Override
    public void dispose() {
        if (sm != null) {
            sm.removeTreeSelectionListener(cblst);
        }
        super.dispose();
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
            txtDauer.setEnabled(true);
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
                txtDauer.setEnabled(false);
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
            txtDauer.setEnabled(false);
            txtDauer.setText(Double.toString(sum));
            // Hier kommt der Listener, der feststellt, wenn irgendeine Checkbox geclickt wird.
            sm.addTreeSelectionListener(cblst);
            treeMass.setEnabled(true);
        }
    }
//    
//    /**
//     * Berechnet den Zeit und Material verbrauch in dem Baum. Der Aufruf dieser Methode erneuert auch die Baumanzeige.
//     * 
//     * @return Die Gesamtzeit inclusive aller Erschwernis / Erleichterungsfaktoren (prozentual und zeitlich)
//     */

    private void createNewTree() {
        // Wurzel
        Object[] o = new Object[]{"", 0d, new Vector(), new Vector(), ParserMassnahmen.TYPE_ROOT, 0d};
        ListElement le = new ListElement(txtBezeichnung.getText(), o);
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
        txtDauer.setEnabled(false);
    }

    private void reloadTree() {
        Vector v = SYSTools.getExpansionState(treeMass);
        DefaultTreeModel tm = (DefaultTreeModel) treeMass.getModel();
        tm.reload();
        SYSTools.setExpansionState(treeMass, v);
        v.clear();
    }

//    private void setTree2Unselected(DefaultMutableTreeNode root) {
//        Enumeration e = root.depthFirstEnumeration();
//        while (e.hasMoreElements()) {
//            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
//            ListElement le = (ListElement) node.getUserObject();
//            Object[] o = (Object[]) le.getObject();
//            o[ParserMassnahmen.O_SELECTED] = Boolean.FALSE;
//        }
//    }
    private class cbClickListener implements TreeSelectionListener {

        public void valueChanged(TreeSelectionEvent arg0) {
            //CheckTreeSelectionModel sm = (CheckTreeSelectionModel) arg0.getSource();
            lstMass.setEnabled(false);
            txtSuche.setEnabled(false);
            txtDauer.setText(Double.toString(Tools.calculateTree(treeMass.getModel(), sm)));
            reloadTree();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        btnNeu = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        txtSuche = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstMass = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        treeMass = new javax.swing.JTree();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtBezeichnung = new javax.swing.JTextField();
        txtDauer = new javax.swing.JTextField();
        cmbKategorie = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        cmbArt = new javax.swing.JComboBox();
        cbAktiv = new javax.swing.JCheckBox();
        lblMassnahme = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnNeu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/filenew.png"))); // NOI18N
        btnNeu.setText("Neu");
        btnNeu.setFocusable(false);
        btnNeu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNeuActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNeu);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/apply.png"))); // NOI18N
        btnSave.setText("Speichern");
        btnSave.setEnabled(false);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/cancel.png"))); // NOI18N
        btnCancel.setText("Abbrechen");
        btnCancel.setEnabled(false);
        btnCancel.setFocusable(false);
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jToolBar1.add(btnCancel);

        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        txtSuche.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtSucheCaretUpdate(evt);
            }
        });

        lstMass.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstMass.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstMass.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstMassValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(lstMass);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtSuche, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtSuche, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        treeMass.setEnabled(false);
        treeMass.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                treeMassMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(treeMass);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Details"));

        jLabel1.setText("Bezeichnung");

        jLabel2.setText("Dauer");

        jLabel3.setText("Kategorie");

        txtBezeichnung.setText("jTextField1");
        txtBezeichnung.setEnabled(false);
        txtBezeichnung.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtBezeichnungCaretUpdate(evt);
            }
        });

        txtDauer.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDauer.setText("10");
        txtDauer.setToolTipText("Dauer in Minuten");
        txtDauer.setEnabled(false);
        txtDauer.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtDauerCaretUpdate(evt);
            }
        });

        cmbKategorie.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbKategorie.setEnabled(false);
        cmbKategorie.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbKategorieItemStateChanged(evt);
            }
        });

        jLabel4.setText("Art");

        cmbArt.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbArt.setEnabled(false);
        cmbArt.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbArtItemStateChanged(evt);
            }
        });

        cbAktiv.setText("Aktiv");
        cbAktiv.setToolTipText("Soll diese Massnahmen für neue Pflegeplanungen angeboten werden ?");
        cbAktiv.setEnabled(false);
        cbAktiv.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbAktivItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtBezeichnung, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(txtDauer, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3))
                            .addComponent(cmbArt, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbAktiv)
                            .addComponent(cmbKategorie, 0, 296, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtBezeichnung, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDauer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(cmbKategorie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbArt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(cbAktiv))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        lblMassnahme.setFont(new java.awt.Font("Dialog", 1, 14));
        lblMassnahme.setForeground(new java.awt.Color(51, 51, 255));
        lblMassnahme.setText("jLabel5");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
                    .addComponent(lblMassnahme, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblMassnahme)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 827, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
    HashMap data = new HashMap();
    data.put("Bezeichnung", txtBezeichnung.getText());
    data.put("Dauer", Double.parseDouble(txtDauer.getText()));
    data.put("MassArt", ((ListElement) cmbArt.getSelectedItem()).getPk());
    data.put("BWIKID", ((ListElement) cmbKategorie.getSelectedItem()).getPk());
    data.put("XMLT", Tools.toXML(treeMass.getModel(), sm));
    data.put("Aktiv", cbAktiv.isSelected());

    if (massid == 0) { // Neue Massnahme wurde eingegeben.
        massid = DBHandling.insertRecord("Massnahmen", data);
    } else {
        DBHandling.updateRecord("Massnahmen", data, "MassID", massid);
    }

    btnNeu.setEnabled(true);
    txtSuche.setEnabled(true);
    lstMass.setEnabled(true);
    saveOK();
    txtSuche.setText(txtBezeichnung.getText());
    if (lstMass.getModel().getSize() > 0) {
        lstMass.getSelectionModel().setLeadSelectionIndex(0);
    }
}//GEN-LAST:event_btnSaveActionPerformed

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
                            lstMass.setEnabled(false);
                            txtSuche.setEnabled(false);
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
                            lstMass.setEnabled(false);
                            txtSuche.setEnabled(false);
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
            if (typ == ParserMassnahmen.TYPE_Vorbereitung
                    || typ == ParserMassnahmen.TYPE_Nachbereitung
                    || typ == ParserMassnahmen.TYPE_DF
                    || typ == ParserMassnahmen.TYPE_Teilschritt) {
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
                        lstMass.setEnabled(false);
                        txtSuche.setEnabled(false);
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
                        lstMass.setEnabled(false);
                        txtSuche.setEnabled(false);
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
                            lstMass.setEnabled(false);
                            txtSuche.setEnabled(false);
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
                                lstMass.setEnabled(false);
                                txtSuche.setEnabled(false);
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
                    if (mynode.isLeaf()
                            || JOptionPane.showConfirmDialog(parent, "Damit wird der ganze Teilbaum gelöscht.\n\nSind Sie sicher ?", le4label.getValue() + " entfernen ??", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        // Drei Fälle
                        if (typ == ParserMassnahmen.TYPE_Teilschritt
                                || typ == ParserMassnahmen.TYPE_DF) {
                            mynode.removeFromParent();
                        } else if (typ == ParserMassnahmen.TYPE_Vorbereitung
                                || typ == ParserMassnahmen.TYPE_Nachbereitung) {
                            mynode.removeAllChildren();
                        } else { // ROOT
                            treeMass.setModel(new DefaultTreeModel(null));
                            txtDauer.setEnabled(true);
                            //--
//                            Object[] ov = new Object[]{"", 0d, new Vector(), new Vector(), ParserMassnahmen.TYPE_Vorbereitung, 0d};
//                            ListElement lev = new ListElement("Vorbereitung", ov);
//                            DefaultMutableTreeNode nodev = new DefaultMutableTreeNode(lev);
//                            //--
//                            Object[] on = new Object[]{"", 0d, new Vector(), new Vector(), ParserMassnahmen.TYPE_Nachbereitung, 0d};
//                            ListElement len = new ListElement("Nachbereitung", ov);
//                            DefaultMutableTreeNode noden = new DefaultMutableTreeNode(len);
//                            //--
//                            mynode.add(nodev);
//                            mynode.add(noden);
                        }
                        ((DefaultTreeModel) treeMass.getModel()).reload();
                        txtDauer.setText(Double.toString(Tools.calculateTree(treeMass.getModel(), sm)));
                        lstMass.setEnabled(false);
                        txtSuche.setEnabled(false);
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
                lstMass.setEnabled(false);
                txtSuche.setEnabled(false);
                saveOK();
            }
            menu.add(menudel);
        } else {
            JMenuItem item = new JMenuItem("Neuen Baum erstellen");

            item.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    createNewTree();
                    lstMass.setEnabled(false);
                    txtSuche.setEnabled(false);
                    saveOK();
                }
            });
            menu.add(item);
            item.setEnabled(treeMass.getModel().getRoot() == null && massid > -1);
        }
        menu.show(evt.getComponent(), evt.getX(), evt.getY());
    }
}//GEN-LAST:event_treeMassMousePressed

private void txtSucheCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtSucheCaretUpdate
    lstMass.setModel(SYSTools.rs2lst(DBRetrieve.getResultSet("Massnahmen", new String[]{"MassID", "Bezeichnung"}, "Bezeichnung", DBHandling.createSearchPattern(txtSuche.getText()), "like", new String[]{"Bezeichnung"})));
    // Detailanzeige zurück setzen
    cmbKategorie.setSelectedIndex(-1);
    cmbArt.setSelectedIndex(-1);
    massid = -1;
    lblMassnahme.setText("");
    txtBezeichnung.setText("");
    txtDauer.setText("");
    cbAktiv.setSelected(false);
    treeMass.setModel(new DefaultTreeModel(null));
    cmbKategorie.setEnabled(false);
    cmbArt.setEnabled(false);
    txtBezeichnung.setEnabled(false);
    txtDauer.setEnabled(false);
    cbAktiv.setEnabled(false);
    btnSave.setEnabled(false);
    btnCancel.setEnabled(false);
}//GEN-LAST:event_txtSucheCaretUpdate

    private void saveOK() {
        HashMap filter = new HashMap();
        filter.put("Aktiv", new Object[]{1, "="});
        filter.put("Bezeichnung", new Object[]{txtBezeichnung.getText(), "="});
        if (massid > 0) {
            filter.put("MassID", new Object[]{massid, "<>"}); // das verhindert, dass die Massnahme sich bei Änderung selbst findet.
        }
        boolean bezeichnungEindeutig = DBRetrieve.getSingleValue("Massnahmen", "Bezeichnung", filter) == null;
        boolean artGewählt = cmbArt.getSelectedIndex() > -1;
        boolean kategorieGewählt = cmbKategorie.getSelectedIndex() > -1;
        boolean zeitVorhanden = false;
        try {
            Double.parseDouble(txtDauer.getText());
            zeitVorhanden = true;
        } catch (NumberFormatException numberFormatException) {
            zeitVorhanden = false;
        }
        btnSave.setEnabled(bezeichnungEindeutig && artGewählt && kategorieGewählt && zeitVorhanden);
        btnNeu.setEnabled(!btnSave.isEnabled());
    }

private void lstMassValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstMassValueChanged
    if (lstMass.getSelectedValue() != null) {
        ListElement le = (ListElement) lstMass.getSelectedValue();
        massid = le.getPk();
        HashMap mass = DBRetrieve.getSingleRecord("Massnahmen", "MassID", massid);
        //double dauer = ((Double) ).doubleValue();
        int art = ((Integer) mass.get("MassArt")).intValue();
        long bwikid = ((BigInteger) mass.get("BWIKID")).longValue();
        String xml = SYSTools.catchNull(mass.get("XMLT"));
        ignoreEvent = true;
        lblMassnahme.setText(mass.get("Bezeichnung").toString());
        txtBezeichnung.setText(mass.get("Bezeichnung").toString());
        txtDauer.setText(mass.get("Dauer").toString());
        cbAktiv.setSelected((Boolean) mass.get("Aktiv"));
        SYSTools.selectInComboBox(cmbKategorie, bwikid);
        SYSTools.selectInComboBox(cmbArt, art);
        ignoreEvent = false;
        cmbKategorie.setEnabled(true);
        cmbArt.setEnabled(true);
        txtDauer.setEnabled(true);
        txtBezeichnung.setEnabled(true);
        cbAktiv.setEnabled(true);
        createTree(xml);
        reloadTree();
        btnSave.setEnabled(false);
        btnCancel.setEnabled(true);
    }
}//GEN-LAST:event_lstMassValueChanged

private void btnNeuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNeuActionPerformed
    cmbKategorie.setSelectedIndex(-1);
    cmbArt.setSelectedIndex(-1);
    massid = 0;
    lblMassnahme.setText("");
    txtBezeichnung.setText("");
    txtDauer.setText("");
    cbAktiv.setSelected(true);
    treeMass.setModel(new DefaultTreeModel(null));
    lstMass.setModel(new DefaultListModel());
    txtSuche.setEnabled(true);
    cmbKategorie.setEnabled(true);
    cmbArt.setEnabled(true);
    txtBezeichnung.setEnabled(true);
    txtDauer.setEnabled(true);
    txtBezeichnung.requestFocus();
    cbAktiv.setEnabled(true);
    btnNeu.setEnabled(false);
    treeMass.setEnabled(true);
    saveOK();
}//GEN-LAST:event_btnNeuActionPerformed

private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
    if (JOptionPane.showConfirmDialog(this, "Sollen die Eingaben wirklich verworfen werden ?", "Frage", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
        lstMass.setEnabled(true);
        txtSuche.setEnabled(true);
        btnSave.setEnabled(false);
        btnNeu.setEnabled(true);
        if (massid <= 0) {
            cmbKategorie.setSelectedIndex(-1);
            cmbArt.setSelectedIndex(-1);
            massid = -1;
            lblMassnahme.setText("");
            txtBezeichnung.setText("");
            txtDauer.setText("");
            treeMass.setModel(new DefaultTreeModel(null));
            lstMass.setModel(new DefaultListModel());
            txtSuche.setEnabled(false);
            cmbKategorie.setEnabled(false);
            cmbArt.setEnabled(false);
            txtBezeichnung.setEnabled(false);
            txtDauer.setEnabled(false);
            txtBezeichnung.requestFocus();
            btnNeu.setEnabled(true);
            treeMass.setEnabled(false);
            txtSuche.requestFocus();
        } else {
            lstMassValueChanged(null);
        }
    }
}//GEN-LAST:event_btnCancelActionPerformed

private void txtBezeichnungCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtBezeichnungCaretUpdate
    if (ignoreEvent) {
        return;
    }
    lblMassnahme.setText(txtBezeichnung.getText());
    DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeMass.getModel().getRoot();
    if (root != null) {
        ListElement le = (ListElement) root.getUserObject();
        le.setValue(txtBezeichnung.getText());
        ((DefaultTreeModel) treeMass.getModel()).reload();
    }
    saveOK();
}//GEN-LAST:event_txtBezeichnungCaretUpdate

private void txtDauerCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtDauerCaretUpdate
    if (ignoreEvent) {
        return;
    }
    saveOK();
}//GEN-LAST:event_txtDauerCaretUpdate

private void cmbKategorieItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbKategorieItemStateChanged
    if (ignoreEvent) {
        return;
    }
    saveOK();
}//GEN-LAST:event_cmbKategorieItemStateChanged

private void cmbArtItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbArtItemStateChanged
    if (ignoreEvent) {
        return;
    }

    saveOK();
}//GEN-LAST:event_cmbArtItemStateChanged

private void cbAktivItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbAktivItemStateChanged
    if (ignoreEvent) {
        return;
    }

    saveOK();
}//GEN-LAST:event_cbAktivItemStateChanged

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    if (JOptionPane.showConfirmDialog(this, "Wollen Sie das Fenster schließen ?\n\nUngesicherte Eingaben gehen verloren.", "Frage", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
        dispose();
    }
}//GEN-LAST:event_formWindowClosing
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnNeu;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox cbAktiv;
    private javax.swing.JComboBox cmbArt;
    private javax.swing.JComboBox cmbKategorie;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblMassnahme;
    private javax.swing.JList lstMass;
    private javax.swing.JTree treeMass;
    private javax.swing.JTextField txtBezeichnung;
    private javax.swing.JTextField txtDauer;
    private javax.swing.JTextField txtSuche;
    // End of variables declaration//GEN-END:variables
}
