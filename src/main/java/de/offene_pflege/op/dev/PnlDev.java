/*
 * Created by JFormDesigner on Sat Jun 15 15:03:44 CEST 2013
 */

package de.offene_pflege.op.dev;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.combobox.DateExComboBox;
import com.jidesoft.popup.JidePopup;
import com.toedter.calendar.JDateChooser;
import de.offene_pflege.entity.EntityTools;
import de.offene_pflege.entity.files.SYSFilesTools;
import de.offene_pflege.entity.info.*;
import de.offene_pflege.entity.nursingprocess.NursingProcess;
import de.offene_pflege.entity.nursingprocess.NursingProcessTools;
import de.offene_pflege.entity.prescription.*;
import de.offene_pflege.entity.reports.NReportTools;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.gui.interfaces.CleanablePanel;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.care.info.PnlEditResInfo;
import de.offene_pflege.op.threads.DisplayManager;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.CaretEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.*;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.*;

/**
 * @author Torsten Löhr
 */
@Log4j2
public class PnlDev extends CleanablePanel {
    Resident resident = null;
    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JScrollPane scrollPane1;
    private JTextArea txtXML;
    private JScrollPane scrollPane2;
    private JTextArea txtException;
    private JButton button1;
    private JPanel panel3;
    private JTextField txtResInfoID;
    private JScrollPane scrollPane6;
    private JTextPane textArea2;
    private JButton button5;
    private JButton button6;
    private JButton button3;
    private JButton button4;
    private JPanel panel2;
    private JComboBox cmbMonth;
    private JButton button2;
    private JTextField txtPZN;
    private JTextField txtCountry;
    private JButton btnMod11;
    private JButton btnImportMedDB;
    private JPanel panel4;
    private JTextField txtResSearch;
    private JLabel lblResname;
    private JLabel label3;
    private JDateChooser dcFrom;
    private JLabel label4;
    private JDateChooser dcTo;
    private JButton btnVKontrole;
    private DateExComboBox dateExComboBox1;
    private JPanel panel5;
    private JScrollPane scrollPane3;
    private JTree tree1;
    private JSplitPane splitPane1;
    private JScrollPane scrollPane5;
    private JTable table1;
    private JScrollPane scrollPane4;
    private JTextArea textArea1;
    public PnlDev() {
        super("opde.dev");
        initComponents();
        txtCountry.setText(Locale.getDefault().getCountry().toLowerCase());

    }

    @Override
    public void cleanup() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void reload() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getInternalClassID() {
        return internalClassID;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private void button1ActionPerformed(ActionEvent e) {
        final JidePopup popup = new JidePopup();
        PnlEditResInfo pnlEditResInfo = new PnlEditResInfo(txtXML.getText(), o -> txtException.setText(SYSTools.catchNull(o)));

        if (pnlEditResInfo.getLastParsingException() == null) {
            pnlEditResInfo.setPanelEnabled(true, PnlEditResInfo.NEW);
            popup.setMovable(false);
            popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
            JScrollPane scrl = new JScrollPane(pnlEditResInfo.getPanel());
            scrl.setPreferredSize(new Dimension(pnlEditResInfo.getPanel().getPreferredSize().width + 100, Math.min(pnlEditResInfo.getPanel().getPreferredSize().height, OPDE.getMainframe().getHeight())));

            popup.setOwner(txtXML);
            popup.removeExcludedComponent(txtXML);
            popup.getContentPane().add(scrl);
            popup.setDefaultFocusComponent(scrl);
            GUITools.showPopup(popup, SwingConstants.CENTER);

            txtException.setText(null);
        } else {

            String exc = pnlEditResInfo.getLastParsingException().getMessage() + "\n";
            for (StackTraceElement ste : pnlEditResInfo.getLastParsingException().getStackTrace()) {
                exc += ste.toString() + "\n";
            }

            txtException.setText(exc);

        }
        invalidate();
    }

    private void txtXMLFocusGained(FocusEvent e) {
        txtXML.selectAll();
    }

    private void txtPZNCaretUpdate(CaretEvent e) {
        try {
            log.debug(MedPackageTools.parsePZN(txtPZN.getText().trim(), txtCountry.getText().trim()));
        } catch (NumberFormatException e1) {
            log.error(e1);
        }

    }

    private void btnMod11ActionPerformed(ActionEvent e) {
        log.debug(MedPackageTools.getMOD11Checksum(txtPZN.getText().trim() + "0"));
    }

    private void btnImportMedDBActionPerformed(ActionEvent e) {

        final int MPText = 0;
        final int SUBTEXT = 1;
        final int DosageFormID = 2;
        final int EXPinDAYS = 3;
        final int PZN = 4;
        final int SIZE = 5;
        final int AMOUNT = 6;
        final int ACME_NAME = 7;
        final int ACME_STREET = 8;
        final int ACME_ZIP = 9;
        final int ACME_CITY_COUNTRY = 10;
        final int ACME_TEL = 11;
        final int ACME_FAX = 12;
        final int ACME_WWW = 13;
        final int IMPORT_STATE = 14;

        HashMap<String, MedProducts> mapMedProducts = new HashMap<String, MedProducts>();
        HashMap<String, ACME> mapACME = new HashMap<String, ACME>();
//        ArrayList<MedProducts> listMedProducts = new ArrayList<MedProducts>();

        HashMap<Long, DosageForm> mapDosageForm = new HashMap<Long, DosageForm>();

        String filename = "/local/meddb.xls";

        try {

            for (DosageForm dosageForm : DosageFormTools.getAll()) {
                mapDosageForm.put(dosageForm.getId(), dosageForm);
            }

            FileInputStream fileInput = new FileInputStream(new File(filename));

            //Get the workbook instance for XLS file
            HSSFWorkbook workbook = new HSSFWorkbook(fileInput);

            //Get first sheet from the workbook
            HSSFSheet sheet = workbook.getSheetAt(0);

            //Get iterator to all the rows in current sheet
//            Iterator<Row> rowIterator = sheet.iterator();

            for (int rowindex = 1; rowindex < sheet.getLastRowNum(); rowindex++) {
                try {
                    String keyMP = sheet.getRow(rowindex).getCell(MPText).getStringCellValue();
                    String keyACME = sheet.getRow(rowindex).getCell(ACME_NAME).getStringCellValue();

                    if (keyMP.isEmpty()) {
                        throw new NullPointerException("MedProduct has no text");
                    }

                    if (keyACME.isEmpty()) {
                        throw new NullPointerException("Company has no name");
                    }

                    if (!mapMedProducts.containsKey(keyMP)) {

                        if (!mapACME.containsKey(keyACME)) {
                            String street = sheet.getRow(rowindex).getCell(ACME_STREET) != null ? sheet.getRow(rowindex).getCell(ACME_STREET).getStringCellValue() : "";
                            String zip = sheet.getRow(rowindex).getCell(ACME_ZIP) != null ? sheet.getRow(rowindex).getCell(ACME_ZIP).getStringCellValue() : "";
                            String city = sheet.getRow(rowindex).getCell(ACME_CITY_COUNTRY) != null ? sheet.getRow(rowindex).getCell(ACME_CITY_COUNTRY).getStringCellValue() : "";
                            String tel = sheet.getRow(rowindex).getCell(ACME_TEL) != null ? sheet.getRow(rowindex).getCell(ACME_TEL).getStringCellValue() : "";
                            String fax = sheet.getRow(rowindex).getCell(ACME_FAX) != null ? sheet.getRow(rowindex).getCell(ACME_FAX).getStringCellValue() : "";
                            String www = sheet.getRow(rowindex).getCell(ACME_WWW) != null ? sheet.getRow(rowindex).getCell(ACME_WWW).getStringCellValue() : "";

                            mapACME.put(keyACME, new ACME(sheet.getRow(rowindex).getCell(ACME_NAME).getStringCellValue(), street, zip, city, tel, fax, www));
                        }

                        mapMedProducts.put(keyMP, new MedProducts(mapACME.get(keyACME), keyMP));
                    }

                    String subtext = sheet.getRow(rowindex).getCell(SUBTEXT) != null ? sheet.getRow(rowindex).getCell(SUBTEXT).getStringCellValue() : "";


                    Long formid = -1l;
                    if (sheet.getRow(rowindex).getCell(DosageFormID).getCellType() == CellType.STRING) {
                        formid = new Long(Long.parseLong(sheet.getRow(rowindex).getCell(DosageFormID).getStringCellValue()));
                    } else if (sheet.getRow(rowindex).getCell(DosageFormID).getCellType() == CellType.NUMERIC) {
                        formid = new Long((long) sheet.getRow(rowindex).getCell(DosageFormID).getNumericCellValue());
                    }

                    if (!mapDosageForm.containsKey(formid)) {
                        throw new NullPointerException("unknown or missing DosageForm");
                    }

                    int expindays = 0;
                    if (sheet.getRow(rowindex).getCell(EXPinDAYS) != null) {
                        if (sheet.getRow(rowindex).getCell(EXPinDAYS).getCellType() == CellType.STRING) {
                            expindays = Integer.parseInt(sheet.getRow(rowindex).getCell(EXPinDAYS).getStringCellValue());
                        } else if (sheet.getRow(rowindex).getCell(EXPinDAYS).getCellType() == CellType.NUMERIC) {
                            expindays = (int) sheet.getRow(rowindex).getCell(EXPinDAYS).getNumericCellValue();
                        }
                    }

                    TradeForm tf = null;
                    for (TradeForm tradeForm : mapMedProducts.get(keyMP).getTradeforms()) {
                        if (SYSTools.catchNull(tradeForm.getSubtext()).equals(subtext) && tradeForm.getDosageForm().getId().longValue() == formid) {
                            tf = tradeForm;
                            break;
                        }
                    }

                    if (tf == null) {
                        tf = new TradeForm(mapMedProducts.get(keyMP), subtext, mapDosageForm.get(formid));
                        if (expindays > 0) {
                            tf.setDaysToExpireAfterOpened(expindays);
                        }

                        mapMedProducts.get(keyMP).getTradeforms().add(tf);
                    }

                    String pzn = "";
                    if (sheet.getRow(rowindex).getCell(PZN).getCellType() == CellType.STRING) {
                        pzn = sheet.getRow(rowindex).getCell(PZN).getStringCellValue();
                        pzn = new Long(Long.parseLong(pzn)).toString();
                    } else if (sheet.getRow(rowindex).getCell(PZN).getCellType() == CellType.NUMERIC) {
                        pzn = new Long((long) sheet.getRow(rowindex).getCell(PZN).getNumericCellValue()).toString();
                    }

                    if (pzn.isEmpty()) {
                        throw new NullPointerException("missing PZN");
                    }

                    if (pzn.length() < 7) {
                        pzn = StringUtils.repeat("0", 7 - pzn.length()) + pzn;
                    }


                    pzn = MedPackageTools.parsePZN(pzn);

                    if (pzn == null) {
                        throw new NullPointerException("illegal PZN");
                    }

                    int pos = 0;
                    if (sheet.getRow(rowindex).getCell(SIZE) != null) {
                        String sSize = "N1";
                        if (sheet.getRow(rowindex).getCell(SIZE).getCellType() == CellType.STRING) {
                            sSize = SYSTools.catchNull(sheet.getRow(rowindex).getCell(SIZE).getStringCellValue(), "N1");
                        }
                        pos = Math.max(ArrayUtils.indexOf(MedPackageTools.GROESSE, sSize), 0);
                    }

                    BigDecimal amount = BigDecimal.ZERO;
                    if (sheet.getRow(rowindex).getCell(AMOUNT).getCellType() == CellType.STRING) {
                        amount = SYSTools.parseDecimal(sheet.getRow(rowindex).getCell(AMOUNT).getStringCellValue());
                    } else if (sheet.getRow(rowindex).getCell(AMOUNT).getCellType() == CellType.NUMERIC) {
                        amount = new BigDecimal(sheet.getRow(rowindex).getCell(AMOUNT).getNumericCellValue());
                    }

                    tf.getPackages().add(new MedPackage(tf, amount, (short) pos, pzn));

                    sheet.getRow(rowindex).createCell(IMPORT_STATE).setCellValue("ok");

                } catch (Exception exc) {
                    // Structure error within the XLS file
                    log.warn(exc);
                    sheet.getRow(rowindex).createCell(IMPORT_STATE).setCellValue("error: " + exc.getMessage());
                }
            }


            fileInput.close();

            FileOutputStream fileOutput = new FileOutputStream(filename);
            workbook.write(fileOutput);
            fileOutput.close();


            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();

                Query q1 = em.createQuery("DELETE FROM MedProducts ");
                Query q2 = em.createQuery("DELETE FROM TradeForm ");
                Query q3 = em.createQuery("DELETE FROM MedPackage ");
                Query q4 = em.createQuery("DELETE FROM ACME ");

                q1.executeUpdate();
                q2.executeUpdate();
                q3.executeUpdate();
                q4.executeUpdate();

                for (MedProducts medProducts : mapMedProducts.values()) {
                    em.persist(medProducts);
                }

                em.getTransaction().commit();

            } catch (OptimisticLockException ole) {
                log.warn(ole);
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                    OPDE.getMainframe().emptyFrame();
                    OPDE.getMainframe().afterLogin();
                }
                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
            } catch (Exception ex1) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                OPDE.fatal(ex1);
            } finally {
                em.close();
            }

            mapACME.clear();
            mapMedProducts.clear();
            mapACME.clear();

        } catch (Exception ex) {
            log.error(ex);
        }
    }

    private void button3ActionPerformed(ActionEvent e) {

        // batch upgrade für NINSUR02 nach NINSUR03
        // 18.01.2020

        EntityManager em = OPDE.createEM();
        try {

            ResInfoType ninsurance = ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_NURSING_INSURANCE);


            // UPDATE resinfo r SET r.Bis = '9999-12-31 23:59:59', r.AbUKennung = NULL WHERE r.BWINFTYP = 'NINSUR02' AND Date(r.Bis) = '2020-01-16';
            // DELETE FROM resinfo WHERE BWINFTYP = 'NINSUR03';

            // Schon abgesetzte wiederherstellen
//            Query q00 = em.createNativeQuery("UPDATE resinfo r SET r.Bis = '9999-12-31 23:59:59', r.AbUKennung = NULL WHERE r.BWINFTYP = 'NINSUR02' AND Date(r.Bis) = '2020-01-16';");

            // Schon eingegebene löschen
//            Query q0 = em.createNativeQuery(" DELETE FROM resinfo WHERE BWINFTYP = 'NINSUR03';");

            // alle alten resinfos die noch aktiv sind
            Query q1 = em.createQuery(" SELECT ri FROM ResInfo ri WHERE ri.bwinfotyp.bwinftyp = :id AND ri.to = :forever ");
            q1.setParameter("id", "NINSUR02");
            q1.setParameter("forever", SYSConst.DATE_UNTIL_FURTHER_NOTICE);

            ArrayList<ResInfo> list = new ArrayList<>(q1.getResultList());

            if (!list.isEmpty()) {

                em.getTransaction().begin();

//                q00.executeUpdate();
//                q0.executeUpdate();

                for (ResInfo info : list) {
                    ResInfo oldinfo = em.merge(info);

                    resident = oldinfo.getResident();

                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                    em.lock(oldinfo, LockModeType.OPTIMISTIC);
                    ResInfoTools.setTo(oldinfo, new Date());
                    oldinfo.setUserOFF(em.merge(OPDE.getLogin().getUser()));

                    Properties content = new Properties();

                    try {
                        StringReader reader = new StringReader(oldinfo.getProperties());
                        content.load(reader);
                        reader.close();
                    } catch (IOException ex) {
                        OPDE.fatal(ex);
                    }

                    String result = content.getProperty("grade").replaceAll("[^0-9.]", "");

                    content.setProperty("requested", "false");
                    content.setProperty("grade", result);

                    ResInfo newInfo = em.merge(ResInfoTools.createResInfo(ninsurance, resident, OPDE.getLogin().getUser()));
                    newInfo.setText(oldinfo.getText());
                    ResInfoTools.setContent(newInfo, content);
//                    newInfo.setHtml(ResInfoTools.getContentAsHTML(newInfo));

//                    log.debug(newInfo.getResident().toString());

                }

                em.getTransaction().commit();

                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Ok. " + list.size() + " Datensätze geändert."));

            } else {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Nichts mehr zu tun. Keine offenen NINSURANCE gefunden."));
            }
        } catch (OptimisticLockException ole) {
            log.warn(ole);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                OPDE.getMainframe().emptyFrame();
                OPDE.getMainframe().afterLogin();
            }
            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
        } catch (Exception ex1) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.fatal(ex1);
        } finally {
            em.close();
        }
    }

    private void txtResSearchActionPerformed(ActionEvent e) {
        resident = EntityTools.find(Resident.class, txtResSearch.getText().trim());
        if (resident == null) return;

        ResInfo stay1 = ResInfoTools.getFirstResinfo(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_STAY));
        ResInfo stay2 = ResInfoTools.getLastResinfo(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_STAY));

        lblResname.setText(ResidentTools.getLabelText(resident));
        dcFrom.setDate(stay1.getFrom());
        dcTo.setDate(stay2.getTo());

    }

    private void btnVKontroleActionPerformed(ActionEvent e) {

        // ResInfos

        log.debug("ResInfos");
        StringBuilder html = new StringBuilder(2000000);

        html.append(SYSConst.center("Pflegeverlauf " + SYSTools.xx("misc.msg.for") + " " + ResidentTools.getLabelText(resident) +
                "<br/>Zeitraum: " + DateFormat.getDateInstance().format(dcFrom.getDate()) + " bis " + DateFormat.getDateInstance().format(dcTo.getDate())));

        html.append("<h1 id=\"fonth1\" >" + SYSTools.xx("nursingrecords.info"));

        for (ResInfoCategory cat : ResInfoCategoryTools.getAll()) {


            ArrayList<ResInfo> listInfos = ResInfoTools.getAll(resident, cat, new LocalDate(dcFrom.getDate()), new LocalDate(dcTo.getDate()));
            if (!listInfos.isEmpty()) {
                html.append("<h2 id=\"fonth2\"><b>Pflegemodellkategorie:</b> " + cat.getText() + "</h2>\n");
//            for (ResInfoType type : ResInfoTypeTools.getByCat(cat)) {
//                ArrayList<ResInfo> listInfos = ResInfoTools.getAll(resident, new LocalDate(dcFrom.getDate()), new LocalDate(dcTo.getDate()));
//
//                if ()

//                html.append("<h3 id=\"fonth3\" >" + type.getShortDescription() + "</h3>\n");
//
//                html.append(type.getType() == ResInfoTypeTools.TYPE_INFECTION ? SYSConst.html_48x48_biohazard : "");
//                html.append(type.getType() == ResInfoTypeTools.TYPE_DIABETES ? SYSConst.html_48x48_diabetes : "");
//                html.append(type.getType() == ResInfoTypeTools.TYPE_ALLERGY ? SYSConst.html_48x48_allergy : "");
//                html.append(type.getType() == ResInfoTypeTools.TYPE_WARNING ? SYSConst.html_48x48_warning : "");

                html.append(ResInfoTools.getResInfosAsHTML(listInfos, true, null));
            }
        }

        log.debug("nursingProcess");
        // nursingProcess
        html.append("<h1 id=\"fonth1\" >" + SYSTools.xx("nursingrecords.nursingprocess") + "</h1>\n");

        for (ResInfoCategory cat : ResInfoCategoryTools.getAll4NP()) {
            ArrayList<NursingProcess> allNPsForThisCat = NursingProcessTools.getAll(resident, cat, new LocalDate(dcFrom.getDate()), new LocalDate(dcTo.getDate()));
            if (!allNPsForThisCat.isEmpty()) {
                html.append("<h2 id=\"fonth2\" >" + cat.getText() + "</h2>\n");
                for (NursingProcess np : allNPsForThisCat) {
                    html.append("<h3 id=\"fonth3\" >" + np.getTopic() + "</h3>\n");
                    html.append(NursingProcessTools.getAsHTML(np, false, true, true, true));
                }
            }
        }

        log.debug("prescriptions");
        html.append("<h1 id=\"fonth1\" >" + SYSTools.xx("nursingrecords.prescription") + "</h1>\n");
        html.append(PrescriptionTools.getPrescriptionsAsHTML(PrescriptionTools.getAll(resident, new LocalDate(dcFrom.getDate()), new LocalDate(dcTo.getDate())), false, false, true, true, true));

        log.debug("reports");
        html.append("<h1 id=\"fonth1\" >" + SYSTools.xx("nursingrecords.reports") + "</h1>\n");
        html.append(NReportTools.getNReportsAsHTML(NReportTools.getNReports(resident, new LocalDate(dcFrom.getDate()), new LocalDate(dcTo.getDate())), false, null, null));


        File f = SYSFilesTools.print(html.toString(), true, false);
        try {
            FileUtils.copyFileToDirectory(f, new File(System.getProperty("user.home")));
        } catch (IOException e1) {
            log.error(e1);
        }


    }

    private void button4ActionPerformed(ActionEvent e) {
        // batch upgrade für sozial01
        // 20.01.2020

        EntityManager em = OPDE.createEM();
        try {

            // alle alten resinfos die noch aktiv sind
            Query q1 = em.createQuery(" SELECT ri FROM ResInfo ri WHERE ri.bwinfotyp.bwinftyp = :id AND ri.to = :forever ");
            q1.setParameter("id", "confidants");
            q1.setParameter("forever", SYSConst.DATE_UNTIL_FURTHER_NOTICE);

            ArrayList<ResInfo> list = new ArrayList<>(q1.getResultList());

            if (!list.isEmpty()) {

                em.getTransaction().begin();

                for (ResInfo info : list) {
                    ResInfo oldinfo = em.merge(info);

                    resident = oldinfo.getResident();

                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                    em.lock(oldinfo, LockModeType.OPTIMISTIC);
                    ResInfoTools.setTo(oldinfo, new Date());
                    oldinfo.setUserOFF(em.merge(OPDE.getLogin().getUser()));

                    Properties content = new Properties();

                    try {
                        StringReader reader = new StringReader(oldinfo.getProperties());
                        content.load(reader);
                        reader.close();
                    } catch (IOException ex) {
                        OPDE.fatal(ex);
                    }

                    content.setProperty("GAINTERAKTION", "0");
                    content.setProperty("GAKONTAKTPFLEGE", "0");

                    ResInfo newInfo = em.merge(ResInfoTools.createResInfo(ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_SOZIALES), resident, OPDE.getLogin().getUser()));
                    newInfo.setText(oldinfo.getText());
                    ResInfoTools.setContent(newInfo, content);
//                    newInfo.setHtml(ResInfoTools.getContentAsHTML(newInfo));

                }

                em.getTransaction().commit();

                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Ok. " + list.size() + " Datensätze geändert."));

            } else {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Nichts mehr zu tun. Keine offenen NINSURANCE gefunden."));
            }
        } catch (OptimisticLockException ole) {
            log.warn(ole);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                OPDE.getMainframe().emptyFrame();
                OPDE.getMainframe().afterLogin();
            }
            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
        } catch (Exception ex1) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.fatal(ex1);
        } finally {
            em.close();
        }
    }

    private void button5ActionPerformed(ActionEvent e) {
        Optional<ResInfo> resInfo = ResInfoTools.findByID(Long.parseLong(txtResInfoID.getText().trim()));
        resInfo.ifPresent(resInfo1 -> {
            textArea2.setText(ResInfoTools.getContentAsHTML(resInfo1));
        });

    }

    private void txtResInfoIDActionPerformed(ActionEvent e) {
        button5ActionPerformed(e);
    }

    private void button6ActionPerformed(ActionEvent e) {
        Optional<ResInfo> resInfo = ResInfoTools.findByID(Long.parseLong(txtResInfoID.getText().trim()));
        resInfo.ifPresent(resInfo1 -> {
            textArea2.setText(ResInfoTools.getContentAsPlainText(resInfo1));
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        tabbedPane1 = new JTabbedPane();
        panel1 = new JPanel();
        scrollPane1 = new JScrollPane();
        txtXML = new JTextArea();
        scrollPane2 = new JScrollPane();
        txtException = new JTextArea();
        button1 = new JButton();
        panel3 = new JPanel();
        txtResInfoID = new JTextField();
        scrollPane6 = new JScrollPane();
        textArea2 = new JTextPane();
        button5 = new JButton();
        button6 = new JButton();
        button3 = new JButton();
        button4 = new JButton();
        panel2 = new JPanel();
        cmbMonth = new JComboBox();
        button2 = new JButton();
        txtPZN = new JTextField();
        txtCountry = new JTextField();
        btnMod11 = new JButton();
        btnImportMedDB = new JButton();
        panel4 = new JPanel();
        txtResSearch = new JTextField();
        lblResname = new JLabel();
        label3 = new JLabel();
        dcFrom = new JDateChooser();
        label4 = new JLabel();
        dcTo = new JDateChooser();
        btnVKontrole = new JButton();
        dateExComboBox1 = new DateExComboBox();
        panel5 = new JPanel();
        scrollPane3 = new JScrollPane();
        tree1 = new JTree();
        splitPane1 = new JSplitPane();
        scrollPane5 = new JScrollPane();
        table1 = new JTable();
        scrollPane4 = new JScrollPane();
        textArea1 = new JTextArea();

        //======== this ========
        setLayout(new BorderLayout());

        //======== tabbedPane1 ========
        {

            //======== panel1 ========
            {
                panel1.setLayout(new FormLayout(
                        "default, $lcgap, 130dlu, $lcgap, default:grow, $lcgap, default",
                        "default, $lgap, fill:default:grow, 2*($lgap, default)"));

                //======== scrollPane1 ========
                {

                    //---- txtXML ----
                    txtXML.setLineWrap(true);
                    txtXML.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            txtXMLFocusGained(e);
                        }
                    });
                    scrollPane1.setViewportView(txtXML);
                }
                panel1.add(scrollPane1, CC.xy(3, 3, CC.FILL, CC.FILL));

                //======== scrollPane2 ========
                {

                    //---- txtException ----
                    txtException.setBackground(Color.pink);
                    txtException.setLineWrap(true);
                    scrollPane2.setViewportView(txtException);
                }
                panel1.add(scrollPane2, CC.xy(5, 3, CC.FILL, CC.FILL));

                //---- button1 ----
                button1.setText("ResInfoType Form Test");
                button1.addActionListener(e -> button1ActionPerformed(e));
                panel1.add(button1, CC.xywh(3, 5, 3, 1));
            }
            tabbedPane1.addTab("text", panel1);

            //======== panel3 ========
            {
                panel3.setLayout(new FormLayout(
                        "pref, $lcgap, default, $lcgap, default:grow",
                        "pref, 4*($lgap, default), $lgap, default:grow, 3*($lgap, default)"));

                //---- txtResInfoID ----
                txtResInfoID.addActionListener(e -> txtResInfoIDActionPerformed(e));
                panel3.add(txtResInfoID, CC.xy(1, 3));

                //======== scrollPane6 ========
                {
                    scrollPane6.setViewportView(textArea2);
                }
                panel3.add(scrollPane6, CC.xywh(5, 3, 1, 15));

                //---- button5 ----
                button5.setText("ResInfoToHTML");
                button5.addActionListener(e -> button5ActionPerformed(e));
                panel3.add(button5, CC.xy(1, 5));

                //---- button6 ----
                button6.setText("ResInfoToTXT");
                button6.addActionListener(e -> button6ActionPerformed(e));
                panel3.add(button6, CC.xy(1, 7));

                //---- button3 ----
                button3.setText("update NIINSUR02 -> NINSUR03");
                button3.addActionListener(e -> button3ActionPerformed(e));
                panel3.add(button3, CC.xy(1, 15));

                //---- button4 ----
                button4.setText("update SOZIAL01");
                button4.addActionListener(e -> button4ActionPerformed(e));
                panel3.add(button4, CC.xy(1, 17));
            }
            tabbedPane1.addTab("NINSUNRANCE", panel3);

            //======== panel2 ========
            {
                panel2.setLayout(new FormLayout(
                        "left:default:grow, $ugap, default",
                        "default, $lgap, default, $rgap, fill:default, 6*($lgap, default)"));
                panel2.add(cmbMonth, CC.xy(1, 3, CC.FILL, CC.DEFAULT));

                //---- button2 ----
                button2.setText("mach mal");
                button2.setContentAreaFilled(false);
                button2.setBorderPainted(false);
                button2.setBorder(null);
                button2.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/1downarrow.png")));
                button2.setHorizontalTextPosition(SwingConstants.LEADING);
                button2.addActionListener(e -> button2ActionPerformed(e));
                panel2.add(button2, CC.xy(1, 5));

                //---- txtPZN ----
                txtPZN.setToolTipText("PZN Check");
                txtPZN.addCaretListener(e -> txtPZNCaretUpdate(e));
                panel2.add(txtPZN, CC.xy(1, 11, CC.FILL, CC.DEFAULT));
                panel2.add(txtCountry, CC.xy(3, 11));

                //---- btnMod11 ----
                btnMod11.setText("calc mod11");
                btnMod11.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
                btnMod11.addActionListener(e -> btnMod11ActionPerformed(e));
                panel2.add(btnMod11, CC.xy(1, 13));

                //---- btnImportMedDB ----
                btnImportMedDB.setText("import meddb.xls");
                btnImportMedDB.addActionListener(e -> btnImportMedDBActionPerformed(e));
                panel2.add(btnImportMedDB, CC.xy(1, 17));
            }
            tabbedPane1.addTab("text", panel2);

            //======== panel4 ========
            {
                panel4.setLayout(new FormLayout(
                        "5*(default, $lcgap), default",
                        "10*(default, $lgap), default"));

                //---- txtResSearch ----
                txtResSearch.addActionListener(e -> txtResSearchActionPerformed(e));
                panel4.add(txtResSearch, CC.xy(3, 3));

                //---- lblResname ----
                lblResname.setText("resname");
                panel4.add(lblResname, CC.xy(3, 5));

                //---- label3 ----
                label3.setText("von");
                panel4.add(label3, CC.xy(1, 7));
                panel4.add(dcFrom, CC.xy(3, 7));

                //---- label4 ----
                label4.setText("bis");
                panel4.add(label4, CC.xy(1, 9));
                panel4.add(dcTo, CC.xy(3, 9));

                //---- btnVKontrole ----
                btnVKontrole.setText("Pflegeverlaufskontrolle");
                btnVKontrole.addActionListener(e -> btnVKontroleActionPerformed(e));
                panel4.add(btnVKontrole, CC.xy(3, 11));
                panel4.add(dateExComboBox1, CC.xy(3, 21));
            }
            tabbedPane1.addTab("text", panel4);

            //======== panel5 ========
            {
                panel5.setLayout(new FormLayout(
                        "default, $rgap, default:grow",
                        "fill:default, $lgap, default:grow"));

                //======== scrollPane3 ========
                {
                    scrollPane3.setViewportView(tree1);
                }
                panel5.add(scrollPane3, CC.xy(1, 3, CC.DEFAULT, CC.FILL));

                //======== splitPane1 ========
                {
                    splitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);

                    //======== scrollPane5 ========
                    {
                        scrollPane5.setViewportView(table1);
                    }
                    splitPane1.setTopComponent(scrollPane5);

                    //======== scrollPane4 ========
                    {
                        scrollPane4.setViewportView(textArea1);
                    }
                    splitPane1.setBottomComponent(scrollPane4);
                }
                panel5.add(splitPane1, CC.xy(3, 3, CC.DEFAULT, CC.FILL));
            }
            tabbedPane1.addTab("text", panel5);
        }
        add(tabbedPane1, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private void button2ActionPerformed(ActionEvent e) {

    }
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
