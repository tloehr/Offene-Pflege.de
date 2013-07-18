/*
 * Created by JFormDesigner on Sat Jun 15 15:03:44 CEST 2013
 */

package op.dev;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import entity.info.*;
import entity.nursingprocess.DFNTools;
import op.OPDE;
import op.care.info.PnlEditResInfo;
import op.tools.CleanablePanel;
import op.tools.GUITools;
import op.tools.SYSCalendar;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.joda.time.DateMidnight;

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
import java.util.Properties;

/**
 * @author Torsten Löhr
 */
public class PnlDev extends CleanablePanel {
    public static final String internalClassID = "opde.dev";

    public PnlDev() {
        initComponents();

        cmbMonth.setModel(SYSCalendar.createMonthList(new DateMidnight().minusYears(1), new DateMidnight()));

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

        ArrayList<Object[]> list = DFNTools.getAVGTimesPerDay((DateMidnight) cmbMonth.getSelectedItem());

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");

        System.out.println("Pflegestufen-Auswertung für " + sdf.format(((DateMidnight) cmbMonth.getSelectedItem()).toDate()));
        System.out.println("-------------------------------------------------------");
        System.out.println("-------------------------------------------------------");

        String priorResident = "";
        Resident currentResident = null;
        ResInfo ps = null;
        BigDecimal essen = BigDecimal.ZERO;
        BigDecimal mobil = BigDecimal.ZERO;
        BigDecimal grundpf = BigDecimal.ZERO;
        BigDecimal behand = BigDecimal.ZERO;
        BigDecimal hausw = BigDecimal.ZERO;
        BigDecimal sozial = BigDecimal.ZERO;
        BigDecimal sonst = BigDecimal.ZERO;

        DecimalFormat nf = new DecimalFormat();

        for (Object[] objects : list) {
            String rid = objects[0].toString();
            BigDecimal avg = (BigDecimal) objects[1];
            long catid = ((BigInteger) objects[2]).longValue();

            if (!priorResident.equals(rid)) {
                if (currentResident != null) {

                    System.out.println("Auswertung für " + ResidentTools.getLabelText(currentResident));
                    System.out.println("Essen und Trinken: " + nf.format(essen) + " Minuten am Tag");
                    System.out.println("Mobilität: " + nf.format(mobil) + " Minuten am Tag");
                    System.out.println("Hauswirtschaft: " + nf.format(hausw) + " Minuten am Tag");
                    System.out.println("Soziales: " + nf.format(sozial) + " Minuten am Tag");
                    System.out.println("-------");
                    System.out.println("Grundpflege: " + nf.format(grundpf) + " Minuten am Tag");
                    System.out.println("Behandlungspflege: " + nf.format(behand) + " Minuten am Tag");
                    System.out.println("Sonstiges: " + nf.format(sonst) + " Minuten am Tag");

                    if (ps != null) {
                        try {
                            StringReader reader = new StringReader(ps.getProperties());
                            Properties props = new Properties();
                            props.load(reader);
                            System.out.println("Reelle Pflegestufe: " + props.getProperty("result"));
                            reader.close();
                        } catch (IOException ex) {
                            OPDE.fatal(ex);
                        }
                    }

                    int psc = 0;
                    for (int stufe : stufen) {
                        if (stufe > grundpf.intValue()) {
                            System.out.println("Berechnete Pflegestufe: PS" + psc);
                            break;
                        }
                        psc++;
                    }


                    System.out.println("===========================================================");
                    essen = BigDecimal.ZERO;
                    mobil = BigDecimal.ZERO;
                    grundpf = BigDecimal.ZERO;
                    behand = BigDecimal.ZERO;
                    hausw = BigDecimal.ZERO;
                    sozial = BigDecimal.ZERO;
                    sonst = BigDecimal.ZERO;
                }

                EntityManager em = OPDE.createEM();
                currentResident = em.find(Resident.class, rid);
                ps = ResInfoTools.getLastResinfo(currentResident, ResInfoTypeTools.TYPE_NURSING_INSURANCE);
                em.close();
                priorResident = rid;
            }

            if (catid == 4) {
                essen = essen.add(avg);
            } else if (catid == 3) {
                mobil = mobil.add(avg);
            } else if (catid == 15) {
                behand = behand.add(avg);
            } else if (catid == 10) {
                hausw = hausw.add(avg);
            } else if (catid == 12) {
                sozial = sozial.add(avg);
            } else if (catid == 1 || catid == 2) {
                grundpf = grundpf.add(avg);
            }

        }

        if (currentResident != null) {

            System.out.println("Auswertung für " + ResidentTools.getLabelText(currentResident));
            System.out.println("Essen und Trinken: " + nf.format(essen) + " Minuten am Tag");
            System.out.println("Mobilität: " + nf.format(mobil) + " Minuten am Tag");
            System.out.println("Hauswirtschaft: " + nf.format(hausw) + " Minuten am Tag");
            System.out.println("Soziales: " + nf.format(sozial) + " Minuten am Tag");
            System.out.println("-------");
            System.out.println("Grundpflege: " + nf.format(grundpf) + " Minuten am Tag");
            System.out.println("Behandlungspflege: " + nf.format(behand) + " Minuten am Tag");
            System.out.println("Sonstiges: " + nf.format(sonst) + " Minuten am Tag");
            if (ps != null) {
                try {
                    StringReader reader = new StringReader(ps.getProperties());
                    Properties props = new Properties();
                    props.load(reader);
                    System.out.println("Reelle Pflegestufe: " + props.getProperty("result"));
                    reader.close();
                } catch (IOException ex) {
                    OPDE.fatal(ex);
                }


            }

            int psc = 0;
            for (int stufe : stufen) {
                if (stufe > grundpf.intValue()) {
                    System.out.println("Berechnete Pflegestufe: PS" + psc);
                    break;
                }
                psc++;
            }
            System.out.println("===========================================================");

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
        panel2 = new JPanel();
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
    private JComboBox cmbMonth;
    private JButton button2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
