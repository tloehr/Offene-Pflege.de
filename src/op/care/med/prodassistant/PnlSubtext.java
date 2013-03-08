/*
 * Created by JFormDesigner on Wed May 30 16:04:17 CEST 2012
 */

package op.care.med.prodassistant;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.prescription.*;
import op.OPDE;
import op.care.med.PnlDosageForm;
import op.tools.GUITools;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.JXSearchField;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author Torsten Löhr
 */
public class PnlSubtext extends JPanel {
    private MedProducts product;
    private TradeForm tradeForm;
    private DosageForm dosageForm;
    private Closure validate;
    private boolean ignoreEvent = false;

    public static final String internalClassID = MedProductWizard.internalClassID + ".subtext";

    public PnlSubtext(Closure validate, MedProducts product) {
        this.validate = validate;
        this.product = product;
        initComponents();
        initPanel();
    }

    public void setProduct(MedProducts product) {
        this.product = product;
    }

    private void initPanel() {
        if (!product.getTradeforms().isEmpty()) {
            ArrayList model = new ArrayList(product.getTradeforms());
            model.add(0, "<html><b>" + OPDE.lang.getString("misc.msg.noneOfThem") + "</b></html>");
            DefaultListModel lmDaf = SYSTools.list2dlm(model);
            lstDaf.setModel(lmDaf);
            lstDaf.setCellRenderer(TradeFormTools.getRenderer(TradeFormTools.LONG));
        }
        lblMsg.setText(OPDE.lang.getString(internalClassID + ".existingTradeforms"));
        lblMsg.setVisible(!product.getTradeforms().isEmpty());
        jsp1.setVisible(!product.getTradeforms().isEmpty());
        lstDaf.setVisible(!product.getTradeforms().isEmpty());

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery(" SELECT m FROM DosageForm m ");

        java.util.List listDosageForm = query.getResultList();
        Collections.sort(listDosageForm, new Comparator<Object>() {
            @Override
            public int compare(Object us, Object them) {
                return DosageFormTools.toPrettyString((DosageForm) us).compareTo(DosageFormTools.toPrettyString((DosageForm) them));
            }
        });

        cmbFormen.setModel(SYSTools.list2cmb(listDosageForm));
        cmbFormen.setRenderer(DosageFormTools.getRenderer(0));
        em.close();

        dosageForm = (DosageForm) cmbFormen.getSelectedItem();

        tradeForm = new TradeForm(product, "", dosageForm);
        validate.execute(tradeForm);
    }

    private void txtZusatzActionPerformed(ActionEvent e) {
        cmbFormen.setEnabled(true);
        tradeForm = new TradeForm(product, txtZusatz.getText().trim(), dosageForm);
        validate.execute(tradeForm);
        if (lstDaf.isVisible() && lstDaf.getSelectedIndex() != 0) {
            ignoreEvent = true;
            lstDaf.setSelectedIndex(0);
            ignoreEvent = false;
        }
    }

    private void lstDafValueChanged(ListSelectionEvent e) {
        if (ignoreEvent) {
            return;
        }
        if (!e.getValueIsAdjusting()) {
            if (lstDaf.getSelectedIndex() > 0) {
                tradeForm = (TradeForm) lstDaf.getSelectedValue();
                txtZusatz.setText(null);
                cmbFormen.setEnabled(false);
            } else {
                cmbFormen.setEnabled(true);
                tradeForm = new TradeForm(product, txtZusatz.getText().trim(), dosageForm);
            }
            validate.execute(tradeForm);
        }
    }

    private void cmbFormenItemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) return;

        dosageForm = (DosageForm) cmbFormen.getSelectedItem();

        tradeForm = new TradeForm(product, txtZusatz.getText().trim(), dosageForm);
        validate.execute(tradeForm);
    }


    private void btnAddActionPerformed(ActionEvent e) {
        PnlDosageForm pnl = new PnlDosageForm(new DosageForm(0));

        GUITools.showPopup(GUITools.createPanelPopup(pnl, new Closure() {
            @Override
            public void execute(Object o) {
                if (o != null) {
                    cmbFormen.setModel(new DefaultComboBoxModel(new DosageForm[]{(DosageForm) o}));
                    dosageForm = (DosageForm) cmbFormen.getSelectedItem();
                    tradeForm = new TradeForm(product, txtZusatz.getText().trim(), dosageForm);
                    validate.execute(tradeForm);
                }
            }
        }, btnAdd), SwingConstants.SOUTH_WEST);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        txtZusatz = new JXSearchField();
        cmbFormen = new JComboBox();
        btnAdd = new JButton();
        lbl1 = new JLabel();
        lblMsg = new JLabel();
        jsp1 = new JScrollPane();
        lstDaf = new JList();

        //======== this ========
        setLayout(new FormLayout(
            "2*(default, $lcgap), default:grow, 2*($lcgap, default)",
            "2*(default, $lgap), default, $rgap, pref, $lgap, default:grow, $lgap, default"));

        //---- txtZusatz ----
        txtZusatz.setFont(new Font("Arial", Font.PLAIN, 14));
        txtZusatz.setInstantSearchDelay(0);
        txtZusatz.setPrompt("Zusatzbezeichnung");
        txtZusatz.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtZusatzActionPerformed(e);
            }
        });
        add(txtZusatz, CC.xywh(3, 3, 5, 1));

        //---- cmbFormen ----
        cmbFormen.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbFormen.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbFormenItemStateChanged(e);
            }
        });
        add(cmbFormen, CC.xywh(3, 5, 3, 1));

        //---- btnAdd ----
        btnAdd.setBackground(Color.white);
        btnAdd.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
        btnAdd.setToolTipText("Medikamente bearbeiten");
        btnAdd.setBorder(null);
        btnAdd.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add-pressed.png")));
        btnAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnAddActionPerformed(e);
            }
        });
        add(btnAdd, CC.xy(7, 5));

        //---- lbl1 ----
        lbl1.setText(null);
        lbl1.setIcon(new ImageIcon(getClass().getResource("/artwork/other/medicine2.png")));
        lbl1.setFont(new Font("Arial", Font.PLAIN, 18));
        add(lbl1, CC.xy(3, 9, CC.CENTER, CC.FILL));

        //---- lblMsg ----
        lblMsg.setText("text");
        lblMsg.setFont(new Font("Arial", Font.PLAIN, 14));
        lblMsg.setHorizontalAlignment(SwingConstants.RIGHT);
        add(lblMsg, CC.xywh(3, 7, 5, 1));

        //======== jsp1 ========
        {

            //---- lstDaf ----
            lstDaf.setFont(new Font("Arial", Font.PLAIN, 14));
            lstDaf.setVisible(false);
            lstDaf.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    lstDafValueChanged(e);
                }
            });
            jsp1.setViewportView(lstDaf);
        }
        add(jsp1, CC.xywh(5, 9, 3, 1, CC.DEFAULT, CC.FILL));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JXSearchField txtZusatz;
    private JComboBox cmbFormen;
    private JButton btnAdd;
    private JLabel lbl1;
    private JLabel lblMsg;
    private JScrollPane jsp1;
    private JList lstDaf;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
