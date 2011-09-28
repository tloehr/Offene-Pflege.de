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
package op.share.bwinfo;

import op.OPDE;
import op.share.tools.DlgDBEdit;
import op.tools.DBHandling;
import op.tools.*;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import se.datadosen.component.RiverLayout;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.*;

/**
 * Dieser Dialog ist ein Standardfenster für alle Bewohnerdaten. Die Daten haben ganz unterschiedliche
 * Formen. Diesem Umstand wird alleine dadurch schon Rechnung getragen, dass diese Daten in einem
 * Textfeld (im XML Format) in der Datenbank gespeichert werden. Es gibt Daten die mittels Radiobuttons
 * eingegeben werden müssen, teilweise sind Textfields oder Checkboxes nötig. Damit man nun nicht
 * für jede evtl. benötigte Eingabeart einen eigenen Dialog erstellen muss, haben wir hier einen generischen
 * Dialog entwickelt, der sich mittels zweier XML Dokumente jeweils auf das angeforderte Aussehen einstellt.
 *
 * @param xmls In diesem XML Dokument steht die Struktur
 * @param xlmc Hier steht der Inhalt
 * @author tloehr
 */
public class DlgBWInfo extends javax.swing.JDialog {

    private int dlgMode; // Enthält die Angabe, in welchem Zustand sich der Dialog befindet.
    public static final int MODE_EDIT = 0; // Ein bestehender Wert wird korrigiert (UPDATE)
    public static final int MODE_CHANGE = 1; // Ein bestehender Wert verändert sich, der alte bleibt erhalten. (UPDATE, INSERT)
    // Für die Textfelder, damit man dort Datentypen vorschreiben.
    private static final int TYPE_DONT_CARE = 0;
    private static final int TYPE_INT = 1;
    private static final int TYPE_DOUBLE = 2;
    private final String[] titleText = {"Korrektur", "Veränderung", "Neueingabe"};
    private HashMap antwort;
    private HashMap components;
    private boolean initPanel;
    JPanel pnlDaten;
    String xmlstructure;
    String xmlcontent;
    String bwkennung;
    private String bemerkung;
    private String bwinftyp;
    private long bwinfoid;
    private int intervalmode;
    private int katart;
    private ArrayList freeIntervals;
    private HashMap info;
    private ArrayList veto;
    private boolean formChanged;
    // letzter gültiger Stand der Datums und Zeitpunkte. 
    // wird zum Auslesen gebraucht und falls man auf das zuletzt gültige zurückkehren möchte.
    private Date von;
    private Date bis;
    private boolean withTime; // Wird die Uhrzeit mit abgefragt ?
    private Frame parent;
    private double scalesum; // Wird nur bei Skalen benutzt. Enthält immer die Gesamtsumme einer Skala.
    private boolean scalemode; // anzeige, ob sich der parser innerhalb einer Scale Umgebung befindet.
    private String scalelabel;
    //private String scalename;
    private JLabel sumlabel;
    private ArrayList scaleriskmodel;
    private ArrayList scaleButtonGroups; // eine Liste mit den Buttongroups eines scales;

    /**
     * DlgDaten
     *
     * @param parent    Parentframe dieses Dialogs
     * @param bwkennung Kennung des Bewohners, um den es geht
     * @param hm        enthält die Angaben über das Attribut um das es geht, inklusive der XML Dokumente bzgl. Struktur und Inhalt der Information.
     *                  Diese HashMap ist ein Element der ArrayList aus der Klasse BWInfo.
     * @param mode      teilt dem Dialog mit, wie er sich verhalten soll (MODE_NEW, MODE_CHANGE, MODE_EDIT)
     */
    public DlgBWInfo(Frame parent, String bwkennung, HashMap hm, int mode) {
        super(parent, true);
        this.parent = parent;
        this.veto = new ArrayList();
        dlgMode = mode;
        xmlstructure = (String) hm.get("xmls");
        xmlcontent = (String) hm.get("xmlc");
        bwinftyp = (String) hm.get("bwinftyp");
        bemerkung = (String) hm.get("bemerkung");
        intervalmode = (Integer) hm.get("intervalmode");
        katart = (Integer) hm.get("katart");
        bwinfoid = (Long) hm.get("bwinfoid");
        von = (Date) hm.get("von");
        bis = (Date) hm.get("bis");
        this.info = hm;
        this.bwkennung = bwkennung;
        if (this.xmlcontent == null) {
            this.xmlcontent = "";
            cbUnbeantwortet.setSelected(true);
        }
        withTime = intervalmode == BWInfo.MODE_INTERVAL_SINGLE_INCIDENTS;

        initComponents();
        initDialog();

    }

    private void initDialog() {

        // Korrigieren
        if (this.dlgMode == MODE_EDIT) {
            /**
             * - Die Freien Intervalle werden ermittelt. Jedoch wird die zu ändernde Frage ignoriert,
             *   so dass ihr Intervall als frei angesehen wird. Nur für den Fall, dass die Korrektur
             *   bedeutet, dass sich das Interval ausdehnen soll.
             */
            if (katart == BWInfo.ART_VERWALTUNG) {
                freeIntervals = DBRetrieve.getFreeIntervals("BWInfo", "Von", "Bis", "BWINFOID<>" + Long.toString(bwinfoid) + " AND BWKennung='" + bwkennung + "' AND BWINFTYP='" + bwinftyp + "'");
            }
            // Hat sich geändert
        } else { //MODE_CHANGE
            /**
             * - Änderungen finden immer ab JETZT statt (bzw. ab heute morgen 0h BYDAY).
             * - Bei Verwaltungsfragen kann man das Anfangsdatum nach vorne verschieben. Soweit, das es unmittelbar nach dem bisherigen VON
             *   bleibt.
             */
            freeIntervals = new ArrayList();//DBRetrieve.getFreeIntervals("BWInfo", "Von", "Bis", "BWKennung='" + bwkennung + "' AND BWINFTYP='" + bwinftyp + "'");
//            Date prevIntervalVon = von;
//            von = SYSCalendar.nowDBDate();
            if (katart == BWInfo.ART_VERWALTUNG || katart == BWInfo.ART_STAMMDATEN) {
                Date startInterval;
                if (intervalmode == BWInfo.MODE_INTERVAL_BYDAY) {
                    startInterval = SYSCalendar.addField(von, 1, GregorianCalendar.DATE);
                } else {
                    startInterval = SYSCalendar.addField(von, 1, GregorianCalendar.SECOND);
                }
                freeIntervals.add(new Date[]{startInterval, SYSConst.DATE_BIS_AUF_WEITERES});
            } else {
                freeIntervals.add(new Date[]{von, SYSConst.DATE_BIS_AUF_WEITERES});
            }

            if (intervalmode == BWInfo.MODE_INTERVAL_BYDAY) {
                von = new Date(SYSCalendar.startOfDay());
            } else {
                von = new Date(SYSCalendar.midOfDay());
            }
        }


        // Zeitraumveränderung sind nur bei Verwaltungs und Stammdaten möglich
        // Oder wenn man Admin ist. Oder wenn man zur Verwaltungsgruppe gehört.
        //boolean zeitraumEnabled = katart == BWInfo.ART_VERWALTUNG || katart == BWInfo.ART_STAMMDATEN || OPDE.isAdmin() || OPDE.getGroups().contains("verwaltung");
        boolean zeitraumEnabled = katart == BWInfo.ART_VERWALTUNG || katart == BWInfo.ART_STAMMDATEN || OPDE.isAdmin();
        btnVon.setEnabled(zeitraumEnabled);
        btnBis.setEnabled(zeitraumEnabled);

        setButtonText();

        if (bemerkung == null) {
            bemerkung = "";
        }


        this.setTitle(SYSTools.getWindowTitle("BWInfo (" + titleText[dlgMode]) + ")");
        txtBemerkung.setText(bemerkung);
        this.lblKurz.setText((String) info.get("bwinfokurz") + " (" + titleText[dlgMode] + ")");

        // Einen xmlcontent gibts nur bei CHANGE UND EDIT.
        pnlDaten = createPanel(xmlstructure, xmlcontent);
        jspFrage.setViewportView(pnlDaten);
        jspFrage.getVerticalScrollBar().setUnitIncrement(6);
        SYSTools.setXEnabled(pnlDaten, !cbUnbeantwortet.isSelected());

        SYSTools.centerOnParent(parent, this);
        formChanged = false;
        saveOK();
        this.setVisible(true);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblKurz = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jspFrage = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtBemerkung = new javax.swing.JTextArea();
        btnCancel = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        cbUnbeantwortet = new javax.swing.JCheckBox();
        lbl1 = new javax.swing.JLabel();
        lbl2 = new javax.swing.JLabel();
        btnVon = new javax.swing.JButton();
        btnBis = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblKurz.setFont(new java.awt.Font("Dialog", 1, 18));
        lblKurz.setText("lblDaten");

        txtBemerkung.setColumns(20);
        txtBemerkung.setLineWrap(true);
        txtBemerkung.setRows(5);
        txtBemerkung.setToolTipText("Tagen Sie hier bitte Bemerkungen ein.");
        txtBemerkung.setWrapStyleWord(true);
        txtBemerkung.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtBemerkungCaretUpdate(evt);
            }
        });
        jScrollPane2.setViewportView(txtBemerkung);

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/cancel.png"))); // NOI18N
        btnCancel.setText("Abbrechen");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/apply.png"))); // NOI18N
        btnSave.setText("Speichern");
        btnSave.setEnabled(false);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        cbUnbeantwortet.setText("Unbeantwortet");
        cbUnbeantwortet.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbUnbeantwortet.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbUnbeantwortet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbUnbeantwortetActionPerformed(evt);
            }
        });

        lbl1.setText("Von:");

        lbl2.setText("Bis:");

        btnVon.setText("15.04.2009");
        btnVon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVonActionPerformed(evt);
            }
        });

        btnBis.setText("bis auf weiteres");
        btnBis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBisActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 670, Short.MAX_VALUE)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, jspFrage, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 670, Short.MAX_VALUE)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, lblKurz, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 670, Short.MAX_VALUE)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 670, Short.MAX_VALUE)
                                        .add(layout.createSequentialGroup()
                                                .add(btnSave)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(btnCancel))
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                                .add(cbUnbeantwortet)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                                .add(lbl1)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(btnVon)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(lbl2)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(btnBis)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(layout.createSequentialGroup()
                                .addContainerGap()
                                .add(lblKurz)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jspFrage, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                                .add(6, 6, 6)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(cbUnbeantwortet, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(btnVon)
                                        .add(lbl2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(lbl1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE)
                                        .add(btnBis))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 155, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(18, 18, 18)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(btnSave)
                                        .add(btnCancel))
                                .addContainerGap())
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width - 682) / 2, (screenSize.height - 553) / 2, 682, 553);
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (this.dlgMode == MODE_EDIT) {
            saveEDIT();
        } else {
            saveCHANGE();
        }
        this.dispose();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void saveOK() {
        btnSave.setEnabled(veto.isEmpty() && formChanged && DBRetrieve.isInFreeIntervals(freeIntervals, von, bis));
        if (!DBRetrieve.isInFreeIntervals(freeIntervals, von, bis)) {
            btnSave.setToolTipText("Der gewählte Zeitraum ist nicht frei.");
        }
        if (!formChanged) {
            btnSave.setToolTipText("Es wurde nichts verändert, daher kann man nichts speichern.");
        }
    }

    private void saveEDIT() {
        //OPDE.getLogger().debug(toXML());
        HashMap hm = new HashMap();
        hm.put("Von", von);
        hm.put("Bis", bis);
        hm.put("BWINFTYP", bwinftyp);
        hm.put("BWKennung", bwkennung);
        hm.put("Bemerkung", txtBemerkung.getText());
        hm.put("AnUKennung", OPDE.getLogin().getUser().getUKennung());
        if (bis.before(SYSConst.DATE_BIS_AUF_WEITERES)) {
            hm.put("AbUKennung", OPDE.getLogin().getUser().getUKennung());
        }
        hm.put("XML", toXML());
        DBHandling.updateRecord("BWInfo", hm, "BWINFOID", this.bwinfoid);
    }

    private void saveCHANGE() {
        // Das muss innerhalb einer eigenen Transaktion ablaufen, da die beiden Operationen zusammenhängen.
        try {
            OPDE.getDb().db.setAutoCommit(false);
            OPDE.getDb().db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            OPDE.getDb().db.commit();

            // Zuerst den bestehenden kürzen
            OPDE.getLogger().debug(toXML());
            HashMap hm = new HashMap();
            hm.put("Bis", SYSCalendar.addField(von, -1, GregorianCalendar.SECOND)); // Ist in beiden Fällen das gleiche.
            // Die Intervalle sind eine künstliche Beschränkung. So dass die Wechsel jeweils um Mitternacht erfolgen.
            // Somit muss das vorrausgehende Intervall um genau eine Sekunde gekürzt werden und nicht um einen Tag.
            // Sonst wäre z.B. BIS = 31.08.2008 00:00:00 und VON' = 01.09.2008 00:00:00 (dazwischen liegen 24 Stunden)
            hm.put("AbUKennung", OPDE.getLogin().getUser().getUKennung());
            if (!DBHandling.updateRecord("BWInfo", hm, "BWINFOID", this.bwinfoid)) {
                throw new SQLException("saveCHANGE(): updateRecord ist fehlgeschlagen·");
            }
            hm.clear();

            // Dann den neuen einfügen.
            hm = new HashMap();
            hm.put("Von", von);
            hm.put("Bis", bis);
            hm.put("BWINFTYP", bwinftyp);
            hm.put("BWKennung", bwkennung);
            hm.put("AnUKennung", OPDE.getLogin().getUser().getUKennung());
            hm.put("XML", toXML());
            hm.put("Bemerkung", txtBemerkung.getText());
            long newbwinfoid = DBHandling.insertRecord("BWInfo", hm);
            if (newbwinfoid <= 0) {
                throw new SQLException("saveCHANGE(): insertRecord ist fehlgeschlagen·");
            }

            OPDE.getDb().db.commit();
            OPDE.getDb().db.setAutoCommit(true);

        } catch (SQLException ex) {
            try {
                OPDE.getDb().db.rollback();
                OPDE.fatal(this.toString() + ": rolling back operation.");
            } catch (SQLException ex1) {
                ex1.printStackTrace();
                System.exit(1);
            }
            new DlgException(ex);
        }
    }

    public void dispose() {
        SYSTools.unregisterListeners(this);
        super.dispose();
    }

    private void cbUnbeantwortetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbUnbeantwortetActionPerformed
        SYSTools.setXEnabled(pnlDaten, !cbUnbeantwortet.isSelected());
        if (cbUnbeantwortet.isSelected()) {
            antwort = new HashMap();
            antwort.put("unbeantwortet", "true");
            txtBemerkung.setText("");
        } else {
            if (xmlcontent.equalsIgnoreCase("<unbeantwortet value=\"true\"/>")) {
                pnlDaten = createPanel(xmlstructure, "");
            } else {
                pnlDaten = createPanel(xmlstructure, xmlcontent);
            }
            jspFrage.setViewportView(pnlDaten);
        }
        formChanged = true;
        saveOK();
    }//GEN-LAST:event_cbUnbeantwortetActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void txtBemerkungCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtBemerkungCaretUpdate
        formChanged = true;
        saveOK();
    }//GEN-LAST:event_txtBemerkungCaretUpdate

    private void btnVonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVonActionPerformed
        DlgZeitpunkt dlg = new DlgZeitpunkt(parent, "Startzeitpunkt festlegen");
//        ArrayList hauf = DBRetrieve.getHauf(bwkennung);
//        Date[] d = (Date[]) hauf.get(hauf.size() - 1);
//        Date min = d[0];
        Date min = SYSConst.DATE_VON_ANFANG_AN;
        Date max = bis;
        if (intervalmode == BWInfo.MODE_INTERVAL_SINGLE_INCIDENTS) {
            max = SYSConst.DATE_BIS_AUF_WEITERES;
        }
        Date result = dlg.showDialog(von, min, max, withTime);
        if (result != null) {
            von = result;
            formChanged = true;
            if (intervalmode == BWInfo.MODE_INTERVAL_BYDAY) {
                von = new Date(SYSCalendar.addTime2Date(SYSCalendar.toGC(von), SYSCalendar.toGC(SYSCalendar.startOfDay())).getTimeInMillis());
            } else if (intervalmode == BWInfo.MODE_INTERVAL_BYSECOND) {
                von = new Date(SYSCalendar.addTime2Date(SYSCalendar.toGC(von), SYSCalendar.toGC(SYSCalendar.midOfDay())).getTimeInMillis());
            } else if (intervalmode == BWInfo.MODE_INTERVAL_SINGLE_INCIDENTS) {
                bis = von;
            }

            setButtonText();
            saveOK();
        }
    }//GEN-LAST:event_btnVonActionPerformed

    private void btnBisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBisActionPerformed
        DlgZeitpunkt dlg = new DlgZeitpunkt(parent, "Endzeitpunkt festlegen");
        Date result = dlg.showDialog(bis, von, SYSConst.DATE_BIS_AUF_WEITERES, withTime);
        if (result != null) {
            bis = result;
            if (intervalmode == BWInfo.MODE_INTERVAL_BYDAY) {
                bis = new Date(SYSCalendar.addTime2Date(SYSCalendar.toGC(bis), SYSCalendar.toGC(SYSCalendar.endOfDay())).getTimeInMillis());
            } else if (intervalmode == BWInfo.MODE_INTERVAL_BYSECOND) {
                bis = new Date(SYSCalendar.addTime2Date(SYSCalendar.toGC(bis), SYSCalendar.toGC(SYSCalendar.midOfDay())).getTimeInMillis());
                bis = SYSCalendar.addField(bis, -1, GregorianCalendar.SECOND);
            }
            formChanged = true;
            setButtonText();
            saveOK();
        }
    }//GEN-LAST:event_btnBisActionPerformed

    private void setButtonText() {
        DateFormat df = null;
        btnBis.setVisible(!withTime);
        lbl2.setVisible(!withTime);
        if (withTime) {
            df = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT);
            lbl1.setText("Am/Um:");
            btnVon.setText(df.format(von));
        } else {
            btnVon.setText(SYSCalendar.printGermanStyle(von));
            btnBis.setText(SYSCalendar.printGermanStyle(bis));
        }

    }

    /**
     * createPanel erstellt anhand zweier XML Dokumente (eins für die Struktur, eins für den Inhalt) ein JPanel mit den passenden Eingabe Elementen.
     * Die Struktur bestimmt, welche Swing Elemente verwendet werden. Der Inhalt setzt die Swing Widget entsprechend. Ob RadioButtons gedrückt sind,
     * was in Textfeldern steht, welcher Listeneintrag ausgewählt wurde. etc...
     */
    private JPanel createPanel(String structure, String content) {
        JPanel jp = new JPanel();
        initPanel = true;
        // Erst die Struktur...
        try {
            String xmltext = "<?xml version=\"1.0\"?><structure>" + structure + "</structure>";
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
        // ....dann der Inhalt
        try {
            String xmltext = "<?xml version=\"1.0\"?><content>" + content + "</content>";
            XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
            InputSource is = new org.xml.sax.InputSource(new java.io.BufferedReader(new java.io.StringReader(xmltext)));
            HandlerDatenInhalt h = new HandlerDatenInhalt();
            parser.setContentHandler(h);
            parser.parse(is);
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        initPanel = false;
        return jp;
    }

    private void calcScale() {
        scalesum = 0d;
        Iterator it = scaleButtonGroups.iterator();

        while (it.hasNext()) {
            String groupname = (String) it.next();
            ButtonGroup bg = (ButtonGroup) components.get(groupname);
            Enumeration e = bg.getElements();
            boolean found = false;
            while (e.hasMoreElements() && !found) {
                AbstractButton ab = (AbstractButton) e.nextElement();
                if (ab.getModel().isSelected()) {
                    found = true;
                    Object[] o = (Object[]) components.get(groupname + "." + ab.getName());
                    scalesum += (Double) o[1];
                }
            }
        }
        // nun noch die Einschätzung des Risikos
        // Bezeichnung und Farbe
        it = scaleriskmodel.iterator();
        boolean found = false;
        String risiko = "unbekanntes Risiko";
        while (it.hasNext() && !found) {
            Object[] o = (Object[]) it.next();
            double from = (Double) o[0];
            double to = (Double) o[1];
            if (from <= scalesum && scalesum <= to) {
                found = true;
                Color c = SYSTools.getColor(o[3].toString());
                sumlabel.setForeground(c);
                risiko = o[2].toString();
            }
        }
        sumlabel.setText(scalelabel + ": " + scalesum + " (" + risiko + ")");
    }

    public String toXML() {
        String xml = "";

        Iterator it = antwort.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            // Ein " im XML Code bringt den Parser durcheinander. Daher werden die hier gegen &quot; ausgetauscht.
            xml += "<" + key + " value=\"" + value.replaceAll("\"", "&quot;") + "\"/>";
        }
        return xml;
    } // toXML()

    /**
     * Setzt die Min Max Grenzen der "VON", "BIS" controls neu.
     * BIS kann nicht vor VON gesetzt werden und VON nicht nach BIS.
     * Muss nach jeder Änderung von "VON" oder "BIS" aufgerufen werden.
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBis;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnVon;
    private javax.swing.JCheckBox cbUnbeantwortet;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JScrollPane jspFrage;
    private javax.swing.JLabel lbl1;
    private javax.swing.JLabel lbl2;
    private javax.swing.JLabel lblKurz;
    private javax.swing.JTextArea txtBemerkung;
    // End of variables declaration//GEN-END:variables
//
//    private class ButtonDefaultListener implements ActionListener {
//
//        public void actionPerformed(ActionEvent evt) {
//            JToggleButton j = (JToggleButton) evt.getSource();
//        }
//    }

    private class RadioButtonActionListener implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JRadioButton j = (JRadioButton) evt.getSource();
            JPanel pnlFrage = (JPanel) j.getParent();
            String groupname = pnlFrage.getName();
            String optionname = j.getName();
            antwort.put(groupname, optionname);
            formChanged = true;
            saveOK();
        }
    }

    private class ScaleOptionActionListener implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JRadioButton j = (JRadioButton) evt.getSource();
            JPanel pnlFrage = (JPanel) j.getParent();
            String groupname = pnlFrage.getName();
            String optionname = j.getName();
            antwort.put(groupname, optionname);
            calcScale();
            formChanged = true;
            saveOK();
        }
    }

    private class CheckBoxActionListener implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            JCheckBox j = (JCheckBox) evt.getSource();
            String cbname = j.getName();
            antwort.put(cbname, (j.isSelected() ? "true" : "false"));
            formChanged = true;
            saveOK();
        }
    }

    private class ComboBoxItemStateListener implements ItemListener {

        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            JComboBox j = (JComboBox) evt.getSource();
            String cmbname = j.getName();
            ComboBoxModel cbm = (ComboBoxModel) j.getModel();
            ListElement le = (ListElement) cbm.getSelectedItem();
            // Hier muss unterschieden werden, ob der PK ein Long oder ein String ist.
            if (le.getPk() <= 0) {
                antwort.put(cmbname, le.getData());
            } else {
                antwort.put(cmbname, Long.toString(le.getPk()));
            }
            formChanged = true;
            saveOK();
        }
    }

    private class TextFieldCaretListener implements CaretListener {

        int type;
        boolean notempty;

        TextFieldCaretListener(int type, boolean notempty) {
            this.type = type;
            this.notempty = notempty;
        }

        @Override
        public void caretUpdate(CaretEvent e) {
            if (initPanel) {
                return;
            }

            JTextField j = (JTextField) e.getSource();

            if (type > 0) { // int type verlangt                
                try {
                    if (type == TYPE_INT) {
                        Integer.parseInt(j.getText());
                    } else {
                        Double.parseDouble(j.getText());
                    }
                    veto.remove(j.getName());

                } catch (NumberFormatException nfe) {
                    if (!veto.contains(j.getName())) {
                        veto.add(j.getName());
                    }
                }
            } else {
                if (notempty && j.getText().isEmpty()) {
                    if (!veto.contains(j.getName())) {
                        veto.add(j.getName());
                    }
                } else {
                    veto.remove(j.getName());
                }
            }


            formChanged = true;
            saveOK();
        }
    }

    private class TextFieldFocusListener implements FocusListener {

        public void focusGained(FocusEvent e) {
            // Alles markieren, wenn das Feld den Focus erhält.
            JTextField j = (JTextField) e.getSource();
            j.setSelectionStart(0);
            j.setSelectionEnd(j.getText().length());
        }

        public void focusLost(FocusEvent e) {

            JTextField j = (JTextField) e.getSource();
            String tfname = j.getName();
            antwort.put(tfname, SYSTools.escapeXML(j.getText()));
            formChanged = true;
            saveOK();
        }
    }

    private void setDB4List(String listgroupname) {
        Object[] listDetails = (Object[]) components.get(listgroupname + ".details");
        String tablename = listDetails[0].toString();
        ArrayList fields = (ArrayList) listDetails[1];
        HashMap filter = (HashMap) listDetails[2];
        ArrayList order = (ArrayList) listDetails[3];
        ArrayList fieldsprefix = (ArrayList) listDetails[4];
        // Am Ende des Listen Elements wird die Datenbank abgefragt.
        ResultSet rs = DBHandling.getResultSet(
                tablename,
                SYSTools.ArrayList2StringArray(fields),
                filter,
                SYSTools.ArrayList2StringArray(order));

        DefaultListModel dlm = SYSTools.rs2lst(rs, SYSTools.ArrayList2StringArray(fieldsprefix));
        JComboBox j = (JComboBox) components.get(listgroupname);
        j.setModel(SYSTools.lst2cmb(dlm));
        String cmbname = j.getName();
        ComboBoxModel cbm = (ComboBoxModel) j.getModel();
        ListElement le = (ListElement) cbm.getSelectedItem();
        // Hier muss unterschieden werden, ob der PK ein Long oder ein String ist.
        if (le.getPk() <= 0) {
            antwort.put(cmbname, le.getData());
        } else {
            antwort.put(cmbname, Long.toString(le.getPk()));
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
        //private HashMap bg = new HashMap();
        private String tablename;
        private ArrayList fields;
        private ArrayList fieldsprefix;
        private ArrayList order;
        private HashMap filter;
        private String listgroupname;
        private String groupname;
        private DefaultComboBoxModel boxModel;
        //private int itemNum = 0;

        @Override
        public void startDocument() throws SAXException {
            antwort = new HashMap();
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
                scalesum = 0d;
                //scalename = attributes.getValue("name");
                scalelabel = attributes.getValue("label");
                scaleButtonGroups = new ArrayList();
                scaleriskmodel = new ArrayList();
            }
            if (tagName.equalsIgnoreCase("risk")) {
                // from, to, label, color
                Object[] o = new Object[]{Double.parseDouble(attributes.getValue("from")), Double.parseDouble(attributes.getValue("to")), attributes.getValue("label"), attributes.getValue("color")};
                scaleriskmodel.add(o);
            }
            if (tagName.equalsIgnoreCase("tabgroup")) {
                JLabel jl = new JLabel(attributes.getValue("label") + ":");
                if (!SYSTools.catchNull(attributes.getValue("color")).equals("")) {
                    jl.setForeground(SYSTools.getColor(attributes.getValue("color")));
                }
                if (!SYSTools.catchNull(attributes.getValue("size")).equals("")) {
                    int size = Integer.parseInt(attributes.getValue("size"));
                    jl.setFont(new java.awt.Font("Dialog", Font.BOLD, size));
                }
                jl.setToolTipText(SYSTools.toHTML(SYSTools.catchNull(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')));
                outerpanel.add("p left", jl);
                tabgroup = true;
            }
            if (tagName.equalsIgnoreCase("option")) {
                double score = 0;
                if (scalemode) {
                    score = Double.parseDouble(SYSTools.catchNull(attributes.getValue("score")));
                }
                JRadioButton j = new JRadioButton(attributes.getValue("label"));
                j.setToolTipText(SYSTools.toHTML(SYSTools.catchNull(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')));
                String compName = attributes.getValue("name");
                String layout = attributes.getValue("layout");
                if (SYSTools.catchNull(layout).equals("")) {
                    layout = "left";
                }
                j.setName(compName);
                innerpanel.add(layout, j);

                if (scalemode) {
                    j.addActionListener(new ScaleOptionActionListener());
                    components.put(groupname + "." + compName, new Object[]{j, score}); // für den späteren Direktzugriff
                } else {
                    j.addActionListener(new RadioButtonActionListener());
                    components.put(groupname + "." + compName, j); // für den späteren Direktzugriff
                }
                ((ButtonGroup) components.get(groupname)).add(j); // der Knopf wird zu der passenden ButtonGroup hinzugefügt.

                if (SYSTools.catchNull(attributes.getValue("default")).equals("true")) {
                    j.setSelected(true);
                    //scalesum += score;
                    antwort.put(groupname, attributes.getValue("name"));
                }
            }
            // ---------------------- CHECKBOXES --------------------------------
            if (tagName.equalsIgnoreCase("checkbox")) {
                groupname = attributes.getValue("name");
                JCheckBox j = new JCheckBox(attributes.getValue("label"));
                j.setToolTipText(SYSTools.toHTML(SYSTools.catchNull(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')));
                String layout = attributes.getValue("layout");
                if (SYSTools.catchNull(layout).equals("")) {
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
                antwort.put(groupname, (j.isSelected() ? "true" : "false"));
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

                boolean notempty = SYSTools.catchNull(attributes.getValue("notempty")).equals("true");


                JLabel jl = new JLabel(attributes.getValue("label") + ":");
                JTextField j = new JTextField(50);
                j.setName(groupname);
                j.setToolTipText(SYSTools.toHTML(SYSTools.catchNull(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')));
                outerpanel.add("p left", jl);
                outerpanel.add("tab hfill", j);
                components.put(groupname, j); // für den späteren Direktzugriff
                j.addFocusListener(new TextFieldFocusListener());
                j.addCaretListener(new TextFieldCaretListener(type, notempty));
                String defaultText = attributes.getValue("default");
                if (defaultText != null) {
                    j.setText(defaultText);
                } else {
                    defaultText = "";
                }
                antwort.put(groupname, defaultText);
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
                if (!SYSTools.catchNull(attributes.getValue("color")).equals("")) {
                    jl.setForeground(SYSTools.getColor(attributes.getValue("color")));
                }
                if (!SYSTools.catchNull(attributes.getValue("size")).equals("")) {
                    int size = Integer.parseInt(attributes.getValue("size"));
                    jl.setFont(new java.awt.Font("Dialog", Font.BOLD, size));
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

                if (!SYSTools.catchNull(attributes.getValue("help")).equals("")) {

                    try {
                        JButton jb = new JButton("Hilfe");
                        final URI uri = new URI(attributes.getValue("help"));
                        jb.addActionListener(new java.awt.event.ActionListener() {

                            public void actionPerformed(java.awt.event.ActionEvent evt) {
                                if (Desktop.isDesktopSupported()) {
                                    Desktop desktop = Desktop.getDesktop();
                                    try {
                                        desktop.browse(uri);
                                    } catch (IOException iOException) {
                                    }
                                }
                            }
                        });
                        outerpanel.add(jb);
                    } catch (URISyntaxException uRISyntaxException) {
                    }

                }
            }
            if (tagName.equalsIgnoreCase("item")) {
                //itemNum++;
                boxModel.addElement(new ListElement(attributes.getValue("label"), attributes.getValue("name")));
            }
            // ---------------------- Datenbank-Listen --------------------------------
            if (tagName.equalsIgnoreCase("list")) {
                groupname = attributes.getValue("name");
                JLabel jl = new JLabel(attributes.getValue("label") + ":");
                JComboBox j = new JComboBox();
                j.setToolTipText(SYSTools.toHTML(SYSTools.catchNull(attributes.getValue("tooltip")).replace('[', '<').replace(']', '>')));
                j.setName(groupname);
                outerpanel.add("p left", jl);
                JButton editButton = new JButton(new ImageIcon(getClass().getResource("/artwork/22x22/edit.png")));
                editButton.setBorder(null);
                editButton.setOpaque(true);
                editButton.setBackground(Color.LIGHT_GRAY);
                editButton.setToolTipText("Drücken Sie hier um die nebenstehende Liste zu bearbeiten.");
                outerpanel.add("tab", editButton);
                outerpanel.add("tab hfill", j);
                components.put(groupname, j); // für den späteren Direktzugriff
                //components.put(groupname + ":edit", editButton); // für den späteren Direktzugriff
                j.addItemListener(new ComboBoxItemStateListener());
                antwort.put(groupname, 0);
                fields = new ArrayList();
                tablename = attributes.getValue("table");
                listgroupname = groupname;
                fieldsprefix = new ArrayList();
                order = new ArrayList();
                filter = new HashMap();
                // Bearbeitungsmöglichkeit für die ComboBox
                editButton.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        Object[] listDetails = (Object[]) components.get(listgroupname + ".details");
                        String tablename = listDetails[0].toString();
                        //ArrayList fields = (ArrayList) listDetails[1];
                        HashMap filter = (HashMap) listDetails[2];
                        ArrayList order = (ArrayList) listDetails[3];
                        String sql = DBHandling.getSQLStatement(
                                tablename,
                                new String[]{"*"},
                                filter,
                                SYSTools.ArrayList2StringArray(order));
                        new DlgDBEdit(parent, sql);
                        setDB4List(listgroupname);
                    }
                });
            }
            if (tagName.equalsIgnoreCase("col")) {
                fields.add(attributes.getValue("name"));
                if (attributes.getValue("prefix") != null) {
                    fieldsprefix.add(attributes.getValue("prefix"));
                } else {
                    fieldsprefix.add("");
                }
                if (SYSTools.catchNull(attributes.getValue("order")).equalsIgnoreCase("true")) {
                    order.add(attributes.getValue("name")); // Hier wird der Spaltenname als Sortierkriterium hinzugefügt.
                }
            }
            if (tagName.equalsIgnoreCase("filter")) {
                String wherefield = attributes.getValue("name");
                String wherevalue = attributes.getValue("value");
                String comparisonOperator = attributes.getValue("operator");
                filter.put(wherefield, new Object[]{wherevalue, comparisonOperator});
            }
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (qName.equalsIgnoreCase("optiongroup") || qName.equalsIgnoreCase("scalegroup")) {
                outerpanel.add("tab", innerpanel);
            }
            if (qName.equalsIgnoreCase("scale")) {
                //outerpanel.add("tab", innerpanel);
                outerpanel.add("p hfill", new JSeparator());
                sumlabel = new JLabel();
                sumlabel.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
                outerpanel.add("br", sumlabel);
                //scalemode = false;
                calcScale();
            }
            if (qName.equalsIgnoreCase("combobox")) {
                JComboBox j = (JComboBox) components.get(groupname);
                j.setModel(boxModel);
                ListElement le = (ListElement) j.getSelectedItem();
                // Hier muss unterschieden werden, ob der PK ein Long oder ein String ist.
                if (le.getPk() <= 0) {
                    antwort.put(j.getName(), le.getData());
                } else {
                    antwort.put(j.getName(), Long.toString(le.getPk()));
                }
                boxModel = null;
            }
            if (qName.equalsIgnoreCase("list")) {
                // listDetails enthält alles, was nötig ist um die Datenbank für eine
                // ComboBox abzufragen. Diese wird dann ebenso in components eingetragen,
                // wie die Referenz auf die ComboBox selbst. Der Name ist kanonisch, soll
                // heissen, setzt sich aus listgroupname.details zusammen.
                Object[] listDetails = new Object[]{tablename, fields, filter, order, fieldsprefix};
                components.put(listgroupname + ".details", listDetails);
                setDB4List(listgroupname);
            }
        }

        public void endDocument() {
        }

        public JPanel getPanel() {
            return this.outerpanel;
        }
    } // private class HandlerDatenStruktur

    /**
     * Dieser Handler nimmt die XML Inhaltsstruktur und setzt die Widgets entsprechend.
     * <ul>
     * <li>Checkboxes: &lt;tnz value="true"&gt;</li>
     * <li>Optiongroups: &lt;jn value="nein"&gt;</li>
     * <li>Textfelder: &lt;kontakt value="Nur Kontakt zu ihrer Schwester."&gt;</li>
     * </ul>
     * <p/>
     * Neben dem einstellen der Widgets, wird ebenfalls die HashMap <code>antwort</code> entsprechend mit gepflegt.
     */
    private class HandlerDatenInhalt extends DefaultHandler {

        public void startElement(String nsURI, String strippedName, String tagName, Attributes attributes) throws SAXException {
            String value = attributes.getValue("value");
            if (tagName.equalsIgnoreCase("unbeantwortet")) {
                antwort.put("unbeantwortet", "true");
                cbUnbeantwortet.setSelected(true);
                //cbTNZ.doClick();
            } else {
                if (components.containsKey(tagName + "." + value)) {// dass können nur Options oder Scales sein
                    JRadioButton jc;
                    if (scalemode) {
                        Object[] o = (Object[]) components.get(tagName + "." + value);
                        jc = (JRadioButton) o[0];
                    } else {
                        jc = (JRadioButton) components.get(tagName + "." + value);
                    }
                    jc.setSelected(true);
                } else { // Das können nur CheckBoxes, Listen oder Textfields sein.
                    JComponent jc = (JComponent) components.get(tagName);
                    if (jc instanceof JCheckBox) {
                        ((JCheckBox) jc).setSelected(value.equalsIgnoreCase("true"));
                    }
                    if (jc instanceof JTextField) {
                        ((JTextField) jc).setText(SYSTools.unescapeXML(value));
                    }
                    if (jc instanceof JComboBox) {
                        JComboBox jcmb = ((JComboBox) jc);

                        try {
                            long pk = Long.parseLong(value);
                            SYSTools.selectInComboBox(jcmb, pk);
                        } catch (NumberFormatException numberFormatException) {
                            SYSTools.selectInComboBox(jcmb, value.toString());
                        }
                    }
                }
                if (!tagName.equalsIgnoreCase("content")) {
                    antwort.put(tagName, value);
                }
            }
        }

        public void endDocument() {
            if (scalemode) {
                calcScale();
            }
        }
    } // private class HandlerDatenInhalt <jn value="ja"/>

    public String getBemerkung() {
        return bemerkung;
    }
}
