/*
 * Created by JFormDesigner on Fri Jun 29 14:19:55 CEST 2012
 */

package op.care.info;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.swing.RangeSlider;
import com.toedter.calendar.JDateChooser;
import op.OPDE;
import op.tools.GUITools;
import op.tools.Pair;
import op.tools.SYSConst;
import org.apache.commons.collections.Closure;
import org.joda.time.DateTime;
import org.joda.time.Days;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

/**
 * @author Torsten Löhr
 */
public class PnlPeriod extends JPanel {
    private boolean ignore;
    private Date min, max, from, to;
    DateTime dtmin, dtmax;
    int maximum;
    private boolean fromTheVeryBeginning;
    private boolean untilFurtherNotice;
    private JToggleButton tbTVB, tbUFN;
    private Closure actionBlock;

    public PnlPeriod(Date min, Date max, Date from, Date to, Closure actionBlock) {
        this.fromTheVeryBeginning = from.equals(SYSConst.DATE_THE_VERY_BEGINNING);
        this.untilFurtherNotice = to.equals(SYSConst.DATE_UNTIL_FURTHER_NOTICE);
        this.actionBlock = actionBlock;
        this.min = min;
        this.max = max;//new Date(Math.min(new Date().getTime(), max.getTime())); // max date is always today
        this.from = from;
        this.to = to;
        dtmin = new DateTime(this.min);
        dtmax = new DateTime(this.max);
        initComponents();
        initPanel();
    }

    private void btnMaxActionPerformed(ActionEvent e) {
        slider.setHighValue(maximum);
    }

    private void btnBackFromActionPerformed(ActionEvent e) {
        slider.setLowValue(Math.max(0, slider.getLowValue() - 1));
    }

    private void btnFwdFromActionPerformed(ActionEvent e) {
        slider.setLowValue(Math.min(slider.getHighValue(), slider.getLowValue() + 1));
    }

    private void btnFwdToActionPerformed(ActionEvent e) {
        slider.setHighValue(Math.min(maximum, slider.getHighValue() + 1));
    }

    private void btnBackToActionPerformed(ActionEvent e) {
        slider.setHighValue(Math.max(slider.getLowValue(), slider.getHighValue() - 1));
    }

    private void btnOKActionPerformed(ActionEvent e) {

        Date from = tbTVB.isSelected() ? SYSConst.DATE_THE_VERY_BEGINNING : jdcVon.getDate();
        Date to = tbUFN.isSelected() ? SYSConst.DATE_UNTIL_FURTHER_NOTICE : jdcBis.getDate();

        if (from != null && to != null) {

            DateTime dtFrom = new DateTime(from);
            DateTime dtTo = new DateTime(to);

            if (dtmin.isAfter(dtFrom) || dtmax.isBefore(dtTo)) {
                actionBlock.execute(null);
            } else {
                actionBlock.execute(new Pair<Date, Date>(from, to));
            }

            OPDE.debug("accepted min: " + dtmin);
            OPDE.debug("current from: " + dtFrom);
            OPDE.debug("current to: " + dtTo);
            OPDE.debug("accepted max: " + dtmax);

        } else {
            actionBlock.execute(null);
        }
    }

    private void btnCancelActionPerformed(ActionEvent e) {
        actionBlock.execute(null);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        btnMin = new JButton();
        btnBackFrom = new JButton();
        btnFwdFrom = new JButton();
        jdcVon = new JDateChooser();
        slider = new RangeSlider();
        jdcBis = new JDateChooser();
        panel2 = new JPanel();
        btnBackTo = new JButton();
        btnFwdTo = new JButton();
        btnMax = new JButton();
        pnlTVB = new JPanel();
        pnlUFN = new JPanel();
        panel3 = new JPanel();
        btnCancel = new JButton();
        btnOK = new JButton();

        //======== this ========
        setLayout(new FormLayout(
                "2*(default, $lcgap), 62dlu, $lcgap, default:grow, $lcgap, 62dlu, 2*($lcgap, default)",
                "2*(default, $lgap), default, 6dlu, default, $lgap, default"));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.LINE_AXIS));

            //---- btnMin ----
            btnMin.setText(null);
            btnMin.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_start.png")));
            btnMin.setContentAreaFilled(false);
            btnMin.setBorderPainted(false);
            btnMin.setBorder(null);
            btnMin.setSelectedIcon(null);
            btnMin.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_start_pressed.png")));
            btnMin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnMin.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnMinActionPerformed(e);
                }
            });
            panel1.add(btnMin);

            //---- btnBackFrom ----
            btnBackFrom.setText(null);
            btnBackFrom.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_rev.png")));
            btnBackFrom.setContentAreaFilled(false);
            btnBackFrom.setBorderPainted(false);
            btnBackFrom.setBorder(null);
            btnBackFrom.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_rev_pressed.png")));
            btnBackFrom.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnBackFrom.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnBackFromActionPerformed(e);
                }
            });
            panel1.add(btnBackFrom);

            //---- btnFwdFrom ----
            btnFwdFrom.setText(null);
            btnFwdFrom.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_fwd.png")));
            btnFwdFrom.setContentAreaFilled(false);
            btnFwdFrom.setBorderPainted(false);
            btnFwdFrom.setBorder(null);
            btnFwdFrom.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_fwd_pressed.png")));
            btnFwdFrom.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnFwdFrom.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnFwdFromActionPerformed(e);
                }
            });
            panel1.add(btnFwdFrom);
        }
        add(panel1, CC.xy(3, 3));

        //---- jdcVon ----
        jdcVon.setFont(new Font("Arial", Font.PLAIN, 14));
        jdcVon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jdcVon.addPropertyChangeListener("date", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                jdcVonPropertyChange(e);
            }
        });
        add(jdcVon, CC.xy(5, 3));

        //---- slider ----
        slider.setPaintLabels(true);
        slider.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        slider.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                sliderPropertyChange(e);
            }
        });
        add(slider, CC.xy(7, 3));

        //---- jdcBis ----
        jdcBis.setFont(new Font("Arial", Font.PLAIN, 14));
        jdcBis.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jdcBis.addPropertyChangeListener("date", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                jdcBisPropertyChange(e);
            }
        });
        add(jdcBis, CC.xy(9, 3));

        //======== panel2 ========
        {
            panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

            //---- btnBackTo ----
            btnBackTo.setText(null);
            btnBackTo.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_rev.png")));
            btnBackTo.setContentAreaFilled(false);
            btnBackTo.setBorderPainted(false);
            btnBackTo.setBorder(null);
            btnBackTo.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_rev_pressed.png")));
            btnBackTo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnBackTo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnBackToActionPerformed(e);
                }
            });
            panel2.add(btnBackTo);

            //---- btnFwdTo ----
            btnFwdTo.setText(null);
            btnFwdTo.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_fwd.png")));
            btnFwdTo.setContentAreaFilled(false);
            btnFwdTo.setBorderPainted(false);
            btnFwdTo.setBorder(null);
            btnFwdTo.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_fwd_pressed.png")));
            btnFwdTo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnFwdTo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnFwdToActionPerformed(e);
                }
            });
            panel2.add(btnFwdTo);

            //---- btnMax ----
            btnMax.setText(null);
            btnMax.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_end.png")));
            btnMax.setContentAreaFilled(false);
            btnMax.setBorderPainted(false);
            btnMax.setBorder(null);
            btnMax.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_end_pressed.png")));
            btnMax.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnMax.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnMaxActionPerformed(e);
                }
            });
            panel2.add(btnMax);
        }
        add(panel2, CC.xy(11, 3));

        //======== pnlTVB ========
        {
            pnlTVB.setLayout(new BoxLayout(pnlTVB, BoxLayout.X_AXIS));
        }
        add(pnlTVB, CC.xywh(3, 5, 3, 1, CC.DEFAULT, CC.FILL));

        //======== pnlUFN ========
        {
            pnlUFN.setLayout(new BoxLayout(pnlUFN, BoxLayout.X_AXIS));
        }
        add(pnlUFN, CC.xywh(9, 5, 3, 1, CC.DEFAULT, CC.FILL));

        //======== panel3 ========
        {
            panel3.setLayout(new BoxLayout(panel3, BoxLayout.LINE_AXIS));

            //---- btnCancel ----
            btnCancel.setText(null);
            btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/cancel.png")));
            btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnCancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnCancelActionPerformed(e);
                }
            });
            panel3.add(btnCancel);

            //---- btnOK ----
            btnOK.setText(null);
            btnOK.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/apply.png")));
            btnOK.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnOK.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnOKActionPerformed(e);
                }
            });
            panel3.add(btnOK);
        }
        add(panel3, CC.xywh(3, 7, 9, 1, CC.RIGHT, CC.DEFAULT));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    private void initPanel() {
        ignore = true;
//        OPDE.debug("min " + min + " max " + max);
//        OPDE.debug("from " + from + " to " + to);

        final Date myMax = max.equals(SYSConst.DATE_UNTIL_FURTHER_NOTICE) ? new Date() : max;

        slider.setRangeDraggable(false);
        jdcVon.setMinSelectableDate(min);
        jdcVon.setMaxSelectableDate(to);
        jdcVon.setDate(from);
        jdcBis.setMaxSelectableDate(myMax);
        jdcBis.setMinSelectableDate(from);
        jdcBis.setDate(to);

        maximum = Days.daysBetween(new DateTime(min), new DateTime(myMax)).getDays();

        int low = Days.daysBetween(new DateTime(min), new DateTime(from)).getDays();
        int high = Days.daysBetween(new DateTime(min), new DateTime(to)).getDays();

        tbTVB = GUITools.getNiceToggleButton("misc.msg.fromTheVeryBeginning");
        tbUFN = GUITools.getNiceToggleButton("misc.msg.untilFurtherNotice");

        tbTVB.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                btnMin.setEnabled(!tbTVB.isSelected());
                btnBackFrom.setEnabled(!tbTVB.isSelected());
                btnFwdFrom.setEnabled(!tbTVB.isSelected());
                jdcVon.setEnabled(!tbTVB.isSelected());
                if (e.getStateChange() != ItemEvent.SELECTED){
                    jdcVon.setDate(min);
                }
            }
        });

        tbUFN.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                btnMax.setEnabled(!tbUFN.isSelected());
                btnFwdTo.setEnabled(!tbUFN.isSelected());
                btnBackTo.setEnabled(!tbUFN.isSelected());
                jdcBis.setEnabled(!tbUFN.isSelected());
                if (e.getStateChange() != ItemEvent.SELECTED){
                    jdcBis.setDate(myMax);
                }
            }
        });

        tbTVB.setSelected(fromTheVeryBeginning);
        tbUFN.setSelected(untilFurtherNotice);

        tbTVB.setEnabled(min.equals(SYSConst.DATE_THE_VERY_BEGINNING));
        tbUFN.setEnabled(max.equals(SYSConst.DATE_UNTIL_FURTHER_NOTICE));

        pnlTVB.add(tbTVB);
        pnlUFN.add(tbUFN);

        slider.setMinimum(0);
        slider.setMaximum(maximum);
        slider.setLowValue(low);
        slider.setHighValue(high);

        ignore = false;
    }

    private void sliderPropertyChange(PropertyChangeEvent e) {
        ignore = true;
        if (e.getPropertyName().equals("lowValue") && !tbTVB.isSelected()) {
            int val = (Integer) e.getNewValue();

            if (val > slider.getMinimum()) {
                jdcVon.setDate(dtmin.plusDays(val).toDateMidnight().toDate()); // Innerhalb der Grenzen beginnen alle Tage immer um Mitternacht.
            } else {
                jdcVon.setDate(min); // Ansonsten genau an der Grenze
            }
        }
        if (e.getPropertyName().equals("highValue") && !tbUFN.isSelected()) {
            int val = (Integer) e.getNewValue();
            if (val < slider.getMaximum()) {
                jdcBis.setDate(dtmin.plusDays(val + 1).toDateMidnight().toDateTime().minusSeconds(1).toDate()); // Innerhalb der Grenzen enden alle Tage immer um 23:59:59.
            } else {
                jdcBis.setDate(max); // Ansonsten genau an der Grenze
            }
        }
        ignore = false;

    }

    private void jdcVonPropertyChange(PropertyChangeEvent e) {
        if (ignore) return;
        int low = Days.daysBetween(new DateTime(min), new DateTime((Date) e.getNewValue())).getDays();
        jdcBis.setMinSelectableDate((Date) e.getNewValue());
        slider.setLowValue(low);
    }

    private void jdcBisPropertyChange(PropertyChangeEvent e) {
        if (ignore) return;
        int high = Days.daysBetween(new DateTime(min), new DateTime((Date) e.getNewValue())).getDays();
        jdcVon.setMaxSelectableDate((Date) e.getNewValue());
        slider.setHighValue(high);
    }

    private void btnMinActionPerformed(ActionEvent e) {
        slider.setLowValue(0);
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JButton btnMin;
    private JButton btnBackFrom;
    private JButton btnFwdFrom;
    private JDateChooser jdcVon;
    private RangeSlider slider;
    private JDateChooser jdcBis;
    private JPanel panel2;
    private JButton btnBackTo;
    private JButton btnFwdTo;
    private JButton btnMax;
    private JPanel pnlTVB;
    private JPanel pnlUFN;
    private JPanel panel3;
    private JButton btnCancel;
    private JButton btnOK;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
