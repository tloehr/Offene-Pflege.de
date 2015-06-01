package gui;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import interfaces.ContentProvider;
import interfaces.DataChangeEvent;
import interfaces.DataChangeListener;
import interfaces.EditPanelDefault;
import op.OPDE;
import op.tools.GUITools;
import op.tools.SYSConst;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.HorizontalLayout;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.validation.ConstraintViolationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by tloehr on 28.05.15.
 */
public class PnlBeanEditor<T> extends EditPanelDefault<T> {
    private final Class<T> clazz;
    private final String[] fields;
    private Logger logger = Logger.getLogger(this.getClass());
    public static final int SAVE_MODE_IMMEDIATE = 0;
    public static final int SAVE_MODE_OK_CANCEL = 1;
    private int saveMode;

    public PnlBeanEditor(DataChangeListener dcl, ContentProvider<T> contentProvider, Class<T> clazz, String[] fields, int saveMode) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        super(dcl, contentProvider);
        this.clazz = clazz;
        this.fields = fields;
        this.saveMode = saveMode;

        initPanel();
        initButtonPanel();
    }


    void initPanel() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        setLayout(new FormLayout("default, $lcgap, 162dlu:grow, $lcgap, default",
                (fields.length + (saveMode == SAVE_MODE_IMMEDIATE ? 0 : 1) + "*(default, $lgap), default")));

        int row = 1;
        for (String field : fields) {

            JLabel lblName = new JLabel(field);
            lblName.setFont(new Font("Arial", Font.PLAIN, 14));
            add(lblName, CC.xy(1, row));

            Component comp = null;

            if (PropertyUtils.getProperty(data, field) instanceof String) {
                JTextField txt = new JTextField(PropertyUtils.getProperty(data, field).toString());
//                txt.addFocusListener(new FocusAdapter() {
//                    @Override
//                    public void focusLost(FocusEvent e) {
//                        try {
//                            PropertyUtils.setProperty(data, field, ((JTextField) e.getSource()).getText());
//                            broadcast(new DataChangeEvent(thisPanel, data));
//                        } catch (Exception e1) {
//                            logger.debug(e1);
//                        }
//                    }
//                });


                txt.getDocument().addDocumentListener(new DocumentListener() {
                    public void changedUpdate(DocumentEvent e) {
                        check(e);
                    }

                    public void removeUpdate(DocumentEvent e) {
                        check(e);
                    }

                    public void insertUpdate(DocumentEvent e) {
                        check(e);
                    }

                    public void check(DocumentEvent e) {

                        try {
                            String text = e.getDocument().getText(0, e.getDocument().getLength());
                            PropertyUtils.setProperty(data, field, text);
                            broadcast(new DataChangeEvent(thisPanel, data));
                        } catch (BadLocationException e1) {
                            OPDE.error(logger, e1);
                        } catch (IllegalAccessException e1) {
                            OPDE.error(logger, e1);
                        } catch (InvocationTargetException e1) {
                            OPDE.error(logger, e1);
                        } catch (NoSuchMethodException e1) {
                            OPDE.error(logger, e1);
                        } catch (ConstraintViolationException e1) {
                            logger.error(e1);
                        }

                        noch nicht getestet

                    }
                });

                comp = txt;
            } else if (PropertyUtils.getProperty(data, field) instanceof Short || PropertyUtils.getProperty(data, field) instanceof Integer) {
                JTextField txt = new JTextField(PropertyUtils.getProperty(data, field).toString());
                txt.addVetoableChangeListener(new VetoableChangeListener() {
                    @Override
                    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {

                        logger.debug(evt);
                        logger.debug(evt.getPropertyName());


                    }
                });

//                txt.addFocusListener(new FocusAdapter() {
//                    @Override
//                    public void focusLost(FocusEvent e) {
//                        try {
//
//                            PropertyUtils.setProperty(data, field, ((JTextField) e.getSource()).getText());
//                            broadcast(new DataChangeEvent(thisPanel, data));
//                        } catch (Exception e1) {
//                            logger.debug(e1);
//                        }
//                    }
//                });
                comp = txt;
            } else if (PropertyUtils.getProperty(data, field) instanceof Boolean) {
                ItemListener il = e -> {
                    if (e.getStateChange() == ItemEvent.SELECTED || e.getStateChange() == ItemEvent.DESELECTED) {
                        try {
                            PropertyUtils.setProperty(data, field, new Boolean(e.getStateChange() == ItemEvent.SELECTED));
                            broadcast(new DataChangeEvent(thisPanel, data));
                        } catch (Exception e1) {
                            logger.debug(e1);
                        }
                    }
                };

                JToggleButton btnSingle = GUITools.getNiceToggleButton(field);
                btnSingle.addItemListener(il);
                comp = btnSingle;
            }


            add(comp == null ? new JLabel("??") : comp, CC.xy(3, row));

            row += 2;

        }


    }


    void initButtonPanel() {
        if (saveMode == SAVE_MODE_IMMEDIATE) return;

        JPanel buttonPanel = new JPanel(new HorizontalLayout(5));

        JButton btnOK = new JButton(SYSConst.icon22apply);
        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    DataChangeEvent dce = new DataChangeEvent(thisPanel, data);
                    broadcast(dce);
                } catch (ConstraintViolationException cve) {

                }
            }
        });

        JButton btnCancel = new JButton(SYSConst.icon22cancel);
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OPDE.getValidatorFactory().getValidator().validate(data, clazz);
                broadcast(new DataChangeEvent(thisPanel, data));
            }
        });

        buttonPanel.add(btnOK);
        buttonPanel.add(new JButton("CANCEL"));


//        add(buttonPanel, CC.xyw(1, row));


    }


    @Override
    public void setStartFocus() {

    }

    @Override
    public String doValidation() {
        return null;
    }

    @Override
    public void refreshDisplay() {

    }
}
