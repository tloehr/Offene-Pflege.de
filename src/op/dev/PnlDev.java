/*
 * Created by JFormDesigner on Sat Jun 15 15:03:44 CEST 2013
 */

package op.dev;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import com.toedter.calendar.JDateChooser;
import entity.EntityTools;
import entity.files.SYSFilesTools;
import entity.info.*;
import entity.nursingprocess.DFNTools;
import entity.nursingprocess.NursingProcess;
import entity.nursingprocess.NursingProcessTools;
import entity.prescription.*;
import entity.reports.NReportTools;
import fx.RepCtrl;
import gui.GUITools;
import gui.interfaces.CleanablePanel;
import op.OPDE;
import op.care.info.PnlEditResInfo;
import op.threads.DisplayManager;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
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
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author Torsten Löhr
 */
public class PnlDev extends CleanablePanel {
    Resident resident = null;

    public PnlDev() {
        super("opde.dev");
        initComponents();

        cmbMonth.setModel(SYSCalendar.createMonthList(new LocalDate().minusYears(1), new LocalDate()));


        tabbedPane1.insertTab("JavaFX", null, new RepCtrl(), "no tips", 0);
        tabbedPane1.setSelectedIndex(0);

//        tabbedPane1.setComponentAt(1, new PnlCommonTags(new HashSet<Commontags>()));

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
        PnlEditResInfo pnlEditResInfo = new PnlEditResInfo(txtXML.getText(), new Closure() {
            @Override
            public void execute(Object o) {
                txtException.setText(SYSTools.catchNull(o));
            }
        });

        if (pnlEditResInfo.getLastParsingException() == null) {
            pnlEditResInfo.setEnabled(true, PnlEditResInfo.NEW);
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
            for (StackTraceElement ste : Arrays.asList(pnlEditResInfo.getLastParsingException().getStackTrace())) {
                exc += ste.toString() + "\n";
            }

            txtException.setText(exc);

        }
        invalidate();
    }

    private void txtXMLFocusGained(FocusEvent e) {
        txtXML.selectAll();
    }

    private void button2ActionPerformed(ActionEvent e) {

        int[] stufen = new int[]{0, 45, 120, 240};

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");

//        System.out.println("Pflegestufen-Auswertung für " + sdf.format(((DateMidnight) cmbMonth.getSelectedItem()).toDate()));
//        System.out.println("-------------------------------------------------------");
//        System.out.println("-------------------------------------------------------");

//        String priorResident = "";

//        ResInfo ps = null;
//        BigDecimal essen = BigDecimal.ZERO;
//        BigDecimal mobil = BigDecimal.ZERO;
        BigDecimal grundpf = BigDecimal.ZERO;
        BigDecimal behand = BigDecimal.ZERO;
        BigDecimal hausw = BigDecimal.ZERO;
        BigDecimal sozial = BigDecimal.ZERO;
        BigDecimal sonst = BigDecimal.ZERO;

        HashMap<Resident, ArrayList<BigDecimal>> stat = new HashMap<Resident, ArrayList<BigDecimal>>();
        HashMap<Resident, String> psstat = new HashMap<Resident, String>();


        DecimalFormat nf = new DecimalFormat();
        ArrayList<Resident> listResident = ResidentTools.getAllActive();
//        Resident currentResident = null;
        // vorauswertung
        for (Resident res : listResident) {
            stat.put(res, new ArrayList<BigDecimal>());
            ResInfo ps = ResInfoTools.getLastResinfo(res, ResInfoTypeTools.TYPE_NURSING_INSURANCE);
            if (ps != null) {
                try {
                    StringReader reader = new StringReader(ps.getProperties());
                    Properties props = new Properties();
                    props.load(reader);
//                                        System.out.println("Reelle Pflegestufe: " + props.getProperty("result"));
                    psstat.put(res, props.getProperty("result"));
                    reader.close();
                } catch (IOException ex) {
                    OPDE.fatal(ex);
                }
            }
        }

        ArrayList<Object[]> listCare = DFNTools.getAVGTimesPerDay((LocalDate) cmbMonth.getSelectedItem());
        // pflege
        int pos = 0;
        for (Object[] objects : listCare) {
            String rid = objects[0].toString();
            BigDecimal avg = (BigDecimal) objects[1];
            long catid = ((BigInteger) objects[2]).longValue();

            if (catid == 15) {
                behand = behand.add(avg);
            } else if (catid == 10) {
                hausw = hausw.add(avg);
            } else if (catid == 12) {
                sozial = sozial.add(avg);
            } else if (catid == 1 || catid == 2 || catid == 3 || catid == 4) {
                grundpf = grundpf.add(avg);
            }

            // wenn das hier der letzte eintrag ist oder der nächste einem anderen BW gehören wird.
            // dann abschluss dieses durchgangs!
            if (pos == listCare.size() - 1 || !listCare.get(pos + 1)[0].equals(rid)) {

                EntityManager em = OPDE.createEM();
                Resident currentResident = em.find(Resident.class, rid);
                em.close();

                stat.get(currentResident).add(grundpf);
                stat.get(currentResident).add(behand);
                stat.get(currentResident).add(hausw);
                stat.get(currentResident).add(sozial);
                stat.get(currentResident).add(sonst);

                grundpf = BigDecimal.ZERO;
                behand = BigDecimal.ZERO;
                hausw = BigDecimal.ZERO;
                sozial = BigDecimal.ZERO;
                sonst = BigDecimal.ZERO;
            }
            pos++;
        }

        behand = BigDecimal.ZERO;
//        priorResident = "";
        pos = 0;
        ArrayList<Object[]> listMed = BHPTools.getAVGTimesPerDay((LocalDate) cmbMonth.getSelectedItem());
        // behandlungspflege
        for (Object[] objects : listMed) {
            String rid = objects[0].toString();
            BigDecimal avg = (BigDecimal) objects[1];
            behand = behand.add(avg);

            // wenn das hier der letzte eintrag ist oder der nächste einem anderen BW gehören wird.
            // dann abschluss dieses durchgangs!
            if (pos == listMed.size() - 1 || !listMed.get(pos + 1)[0].equals(rid)) {

                EntityManager em = OPDE.createEM();
                Resident currentResident = em.find(Resident.class, rid);
                em.close();

                behand = behand.add(stat.get(currentResident).get(1));
                stat.get(currentResident).set(1, behand);

                behand = BigDecimal.ZERO;

            }

            pos++;

        }

        System.out.println("Bewohner[in];Hauswirtschaft;Soziales;Grundpflege;Behandlungspflege;Sonstiges;PS MDK;PS berechnet");
        System.out.println("Pflegestufen-Auswertung für " + sdf.format(((LocalDate) cmbMonth.getSelectedItem()).toDate()) + ";;;;;;;");
        // abschluss
        String line = "%s;%s;%s;%s;%s;%s;%s;%s";
        for (Resident res : listResident) {
            if (stat.containsKey(res) && !stat.get(res).isEmpty()) {

                int psc = -1;
                for (int stufe : stufen) {
                    if (stufe > stat.get(res).get(0).intValue()) {
//                        System.out.println("Berechnete Pflegestufe: PS" + psc);
                        break;
                    }
                    psc++;
                }

                System.out.println(
                        String.format(line,
                                ResidentTools.getLabelText(res),
                                nf.format(stat.get(res).get(2)),
                                nf.format(stat.get(res).get(3)),
                                nf.format(stat.get(res).get(0)),
                                nf.format(stat.get(res).get(1)),
                                nf.format(stat.get(res).get(4)),
                                SYSTools.catchNull(psstat.get(res), "--"),
                                "PS " + psc)
                );

//                System.out.println("Hauswirtschaft: " + nf.format(stat.get(res).get(2)) + " Minuten am Tag");
//                System.out.println("Soziales: " + nf.format(stat.get(res).get(3)) + " Minuten am Tag");
//                System.out.println("Grundpflege (Körperpflege, Ernährung, Mobilität, Ausscheidung): " + nf.format(stat.get(res).get(0)) + " Minuten am Tag");
//                System.out.println("Behandlungspflege: " + nf.format(stat.get(res).get(1)) + " Minuten am Tag");
//                System.out.println("Sonstiges: " + nf.format(stat.get(res).get(4)) + " Minuten am Tag");
//
//                if (psstat.containsKey(res)) {
//                    System.out.println("Pflegestufe laut MDK: " + psstat.get(res));
//                }
//
//                int psc = 0;
//                for (int stufe : stufen) {
//                    if (stufe > stat.get(res).get(0).intValue()) {
//                        System.out.println("Berechnete Pflegestufe: PS" + psc);
//                        break;
//                    }
//                    psc++;
//                }
//                System.out.println("===========================================================");

            } else {
                System.out.println(
                        String.format(line,
                                ResidentTools.getLabelText(res),
                                "--",
                                "--",
                                "--",
                                "--",
                                "--",
                                "--",
                                "--")
                );
            }
        }
    }

    private void txtPZNCaretUpdate(CaretEvent e) {
        OPDE.debug(MedPackageTools.parsePZN(txtPZN.getText().trim()));
    }

    private void btnMod11ActionPerformed(ActionEvent e) {
        OPDE.debug(MedPackageTools.getMOD11Checksum(txtPZN.getText().trim() + "0"));
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
                    if (sheet.getRow(rowindex).getCell(DosageFormID).getCellType() == Cell.CELL_TYPE_STRING) {
                        formid = new Long(Long.parseLong(sheet.getRow(rowindex).getCell(DosageFormID).getStringCellValue()));
                    } else if (sheet.getRow(rowindex).getCell(DosageFormID).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        formid = new Long((long) sheet.getRow(rowindex).getCell(DosageFormID).getNumericCellValue());
                    }

                    if (!mapDosageForm.containsKey(formid)) {
                        throw new NullPointerException("unknown or missing DosageForm");
                    }

                    int expindays = 0;
                    if (sheet.getRow(rowindex).getCell(EXPinDAYS) != null) {
                        if (sheet.getRow(rowindex).getCell(EXPinDAYS).getCellType() == Cell.CELL_TYPE_STRING) {
                            expindays = Integer.parseInt(sheet.getRow(rowindex).getCell(EXPinDAYS).getStringCellValue());
                        } else if (sheet.getRow(rowindex).getCell(EXPinDAYS).getCellType() == Cell.CELL_TYPE_NUMERIC) {
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
                    if (sheet.getRow(rowindex).getCell(PZN).getCellType() == Cell.CELL_TYPE_STRING) {
                        pzn = sheet.getRow(rowindex).getCell(PZN).getStringCellValue();
                        pzn = new Long(Long.parseLong(pzn)).toString();
                    } else if (sheet.getRow(rowindex).getCell(PZN).getCellType() == Cell.CELL_TYPE_NUMERIC) {
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
                        if (sheet.getRow(rowindex).getCell(SIZE).getCellType() == Cell.CELL_TYPE_STRING) {
                            sSize = SYSTools.catchNull(sheet.getRow(rowindex).getCell(SIZE).getStringCellValue(), "N1");
                        }
                        pos = Math.max(ArrayUtils.indexOf(MedPackageTools.GROESSE, sSize), 0);
                    }

                    BigDecimal amount = BigDecimal.ZERO;
                    if (sheet.getRow(rowindex).getCell(AMOUNT).getCellType() == Cell.CELL_TYPE_STRING) {
                        amount = SYSTools.parseDecimal(sheet.getRow(rowindex).getCell(AMOUNT).getStringCellValue());
                    } else if (sheet.getRow(rowindex).getCell(AMOUNT).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        amount = new BigDecimal(sheet.getRow(rowindex).getCell(AMOUNT).getNumericCellValue());
                    }

                    tf.getPackages().add(new MedPackage(tf, amount, (short) pos, pzn));

                    sheet.getRow(rowindex).createCell(IMPORT_STATE).setCellValue("ok");

                } catch (Exception exc) {
                    // Structure error within the XLS file
                    OPDE.warn(exc);
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
                OPDE.warn(ole);
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
            OPDE.error(ex);
        }
    }

    private void button3ActionPerformed(ActionEvent e) {
//        new MREPrevalenceSheets(new LocalDate(), false);
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

        OPDE.debug("ResInfos");
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

        OPDE.debug("nursingProcess");
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

        OPDE.debug("prescriptions");
        html.append("<h1 id=\"fonth1\" >" + SYSTools.xx("nursingrecords.prescription") + "</h1>\n");
        html.append(PrescriptionTools.getPrescriptionsAsHTML(PrescriptionTools.getAll(resident, new LocalDate(dcFrom.getDate()), new LocalDate(dcTo.getDate())), false, false, true, true, true));

        OPDE.debug("reports");
        html.append("<h1 id=\"fonth1\" >" + SYSTools.xx("nursingrecords.reports") + "</h1>\n");
        html.append(NReportTools.getNReportsAsHTML(NReportTools.getNReports(resident, new LocalDate(dcFrom.getDate()), new LocalDate(dcTo.getDate())), false, null, null));


        File f = SYSFilesTools.print(html.toString(), true, false);
        try {
            FileUtils.copyFileToDirectory(f, new File(System.getProperty("user.home")));
        } catch (IOException e1) {
            OPDE.error(e1);
        }


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
        label1 = new JLabel();
        button3 = new JButton();
        panel2 = new JPanel();
        cmbMonth = new JComboBox();
        button2 = new JButton();
        txtPZN = new JTextField();
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
                    "pref, 9*($lcgap, default)",
                    "pref, 7*($lgap, default)"));

                //---- label1 ----
                label1.setText("text");
                panel3.add(label1, CC.xy(15, 9));

                //---- button3 ----
                button3.setText("Create xlsx");
                button3.addActionListener(e -> button3ActionPerformed(e));
                panel3.add(button3, CC.xy(19, 15));
            }
            tabbedPane1.addTab("test1", panel3);

            //======== panel2 ========
            {
                panel2.setLayout(new FormLayout(
                    "left:default:grow",
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
                    "default, $lcgap, default",
                    "6*(default, $lgap), default"));

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
            }
            tabbedPane1.addTab("text", panel4);
        }
        add(tabbedPane1, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JScrollPane scrollPane1;
    private JTextArea txtXML;
    private JScrollPane scrollPane2;
    private JTextArea txtException;
    private JButton button1;
    private JPanel panel3;
    private JLabel label1;
    private JButton button3;
    private JPanel panel2;
    private JComboBox cmbMonth;
    private JButton button2;
    private JTextField txtPZN;
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
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
