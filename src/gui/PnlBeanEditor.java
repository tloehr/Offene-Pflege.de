package gui;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import interfaces.ContentProvider;
import gui.events.DataChangeEvent;
import gui.events.DataChangeListener;
import interfaces.EditPanelDefault;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.GUITools;
import op.tools.SYSConst;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.HorizontalLayout;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;

/**
 * Created by tloehr on 28.05.15.
 */
public class PnlBeanEditor<T> extends EditPanelDefault<T> {
    private final Class<T> clazz;
    private final String[] fields;
    private Logger logger = Logger.getLogger(this.getClass());
    private HashSet<Component> componentSet;
    public static final int SAVE_MODE_IMMEDIATE = 0;
    public static final int SAVE_MODE_OK_CANCEL = 1;
    private int saveMode;

    public PnlBeanEditor(DataChangeListener dcl, ContentProvider<T> contentProvider, Class<T> clazz, String[] fields, int saveMode) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        super(dcl, contentProvider);
        this.clazz = clazz;
        this.fields = fields;
        this.saveMode = saveMode;
        this.componentSet = new HashSet<>();

        initPanel();
        initButtonPanel();
    }

    void initPanel() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        setLayout(new FormLayout("5dlu, default, $lcgap, 162dlu:grow, $lcgap, 5dlu",
                "5dlu, " + (fields.length + (saveMode == SAVE_MODE_IMMEDIATE ? 0 : 1) + "*(default, $lgap), default, 5dlu")));

        int row = 1;
        for (String field : fields) {

            JLabel lblName = new JLabel(field);
            lblName.setFont(new Font("Arial", Font.PLAIN, 14));
            add(lblName, CC.xy(2, row + 1));

            Component comp = null;

            if (PropertyUtils.getProperty(data, field) instanceof String) {
                JTextField txt = new JTextField(PropertyUtils.getProperty(data, field).toString());

                txt.getDocument().addDocumentListener(new RelaxedDocumentListener(de -> {
                    try {
                        String text = de.getDocument().getText(0, de.getDocument().getLength());
                        PropertyUtils.setProperty(data, field, text);
                        if (saveMode == SAVE_MODE_IMMEDIATE) broadcast(new DataChangeEvent(thisPanel, data));
                    } catch (BadLocationException e1) {
                        OPDE.error(logger, e1);
                    } catch (IllegalAccessException e1) {
                        OPDE.error(logger, e1);
                    } catch (InvocationTargetException e1) {
                        OPDE.error(logger, e1);
                    } catch (NoSuchMethodException e1) {
                        OPDE.error(logger, e1);
                    } catch (ConstraintViolationException e1) {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(e1.getMessage()));
                    }
                }));


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
            comp.setName(field);
            componentSet.add(comp);

            add(comp == null ? new JLabel("??") : comp, CC.xy(4, row + 1));

            row += 2;

        }


    }

    void initButtonPanel() {
        if (saveMode == SAVE_MODE_IMMEDIATE) return;

        JPanel buttonPanel = new JPanel(new HorizontalLayout(5));

        JButton btnOK = new JButton(SYSConst.icon22apply);
        btnOK.addActionListener(e -> {
            try {
                broadcast(new DataChangeEvent(thisPanel, data));
            } catch (ConstraintViolationException cve) {
                for (ConstraintViolation cv : cve.getConstraintViolations()) {
                    System.err.println(cv.getMessage());
                    logger.debug(cv.getConstraintDescriptor());
                }
            }
            reload();
        });
        buttonPanel.add(btnOK);

        JButton btnCancel = new JButton(SYSConst.icon22cancel);
        btnCancel.addActionListener(e -> {
            reload(); // revert to old bean state
        });

        buttonPanel.add(btnCancel);


        add(buttonPanel, CC.xyw(1, fields.length + 5, 5, CC.RIGHT, CC.DEFAULT));
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
        logger.debug(data.toString());
        for (Component comp : componentSet) {
            if (comp instanceof JTextComponent){
                try {
                    ((JTextComponent) comp).setText(PropertyUtils.getProperty(data, comp.getName()).toString());
                } catch (IllegalAccessException e) {
                    OPDE.error(logger, e);
                } catch (InvocationTargetException e) {
                    OPDE.error(logger, e);
                } catch (NoSuchMethodException e) {
                    OPDE.error(logger, e);
                }
            }
        }
    }
}
