/*
 * Created by JFormDesigner on Sat Jun 15 15:03:44 CEST 2013
 */

package op.dev;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import entity.info.*;
import entity.nursingprocess.DFNTools;
import entity.prescription.BHPTools;
import entity.roster.ContractsParameterSet;
import entity.roster.UserContract;
import op.OPDE;
import op.care.info.PnlEditResInfo;
import op.tools.CleanablePanel;
import op.tools.GUITools;
import op.tools.SYSCalendar;
import op.tools.SYSTools;
import op.users.PnlContractsEditor;
import org.apache.commons.collections.Closure;
import org.joda.time.DateMidnight;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
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
    public static final String internalClassID = "opde.dev";

    public PnlDev() {
        initComponents();

        cmbMonth.setModel(SYSCalendar.createMonthList(new LocalDate().minusYears(1), new LocalDate()));

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

        ArrayList<Object[]> listCare = DFNTools.getAVGTimesPerDay((DateMidnight) cmbMonth.getSelectedItem());
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
        ArrayList<Object[]> listMed = BHPTools.getAVGTimesPerDay((DateMidnight) cmbMonth.getSelectedItem());
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
        System.out.println("Pflegestufen-Auswertung für " + sdf.format(((DateMidnight) cmbMonth.getSelectedItem()).toDate()) + ";;;;;;;");
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

    private void button3ActionPerformed(ActionEvent e) {
        JFrame frm = new JFrame();

        UserContract contract = new UserContract(new ContractsParameterSet());
        contract.getDefaults().setExam(true);

        frm.setContentPane(new PnlContractsEditor(contract, false));
        frm.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frm.setVisible(true);
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
        panel2 = new JPanel();
        button3 = new JButton();
        cmbMonth = new JComboBox();
        button2 = new JButton();

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
                button1.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        button1ActionPerformed(e);
                    }
                });
                panel1.add(button1, CC.xywh(3, 5, 3, 1));
            }
            tabbedPane1.addTab("text", panel1);

            //======== panel2 ========
            {
                panel2.setLayout(new FormLayout(
                    "left:default:grow",
                    "default, $lgap, default, $rgap, fill:default, $lgap, default"));

                //---- button3 ----
                button3.setText("ContracsEditor");
                button3.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        button3ActionPerformed(e);
                    }
                });
                panel2.add(button3, CC.xy(1, 1));
                panel2.add(cmbMonth, CC.xy(1, 3, CC.FILL, CC.DEFAULT));

                //---- button2 ----
                button2.setText("mach mal");
                button2.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        button2ActionPerformed(e);
                    }
                });
                panel2.add(button2, CC.xy(1, 5));
            }
            tabbedPane1.addTab("text", panel2);
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
    private JPanel panel2;
    private JButton button3;
    private JComboBox cmbMonth;
    private JButton button2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
