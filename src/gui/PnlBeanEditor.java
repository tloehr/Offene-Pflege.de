package gui;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import gui.events.DataChangeEvent;
import gui.events.DataChangeListener;
import gui.events.RelaxedDocumentListener;
import gui.interfaces.DataProvider;
import gui.interfaces.EditPanelDefault;
import gui.interfaces.EditorComponent;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.GUITools;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ArrayUtils;
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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashSet;

/**
 * Created by tloehr on 28.05.15.
 */
public class PnlBeanEditor<T> extends EditPanelDefault<T> {
    private final Class<T> clazz;
    //    private final String[][] fields;
    private Logger logger = Logger.getLogger(this.getClass());
    private HashSet<Component> componentSet;
    public static final int SAVE_MODE_IMMEDIATE = 0;
    public static final int SAVE_MODE_OK_CANCEL = 1;
    private int saveMode;


    public PnlBeanEditor(DataProvider<T> dataProvider, Class<T> clazz, int saveMode)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        super(dataProvider);
        this.clazz = clazz;
//        this.fields = null;

        this.saveMode = saveMode;
        this.componentSet = new HashSet<>();

        initPanel();
        initButtonPanel();
    }

    void initPanel() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        Field[] fields = data.getClass().getDeclaredFields();

        // I have to count them first.
        int numfields = 0;
        for (final Field field : fields) {
            if (field.isAnnotationPresent(EditorComponent.class)) {
                numfields++;
            }
        }

        setLayout(new FormLayout("5dlu, default, $lcgap, 162dlu:grow, $lcgap, 5dlu",
                "5dlu, " + (numfields + (saveMode == SAVE_MODE_IMMEDIATE ? 0 : 1) + "*(default, $lgap), default, 5dlu")));

        int row = 1;


        for (final Field field : fields) {
            if (field.isAnnotationPresent(EditorComponent.class)) {

                EditorComponent editorComponent = field.getAnnotation(EditorComponent.class);

                JLabel lblName = new JLabel(SYSTools.xx(editorComponent.label()));
                lblName.setFont(new Font("Arial", Font.PLAIN, 14));
                add(lblName, CC.xy(2, row + 1));

                JComponent comp = null;


                if (editorComponent.component()[0].equalsIgnoreCase("textfield")) {

                    JTextField txt = new JTextField(PropertyUtils.getProperty(data, field.getName()).toString());

                    txt.getDocument().addDocumentListener(new RelaxedDocumentListener(de -> {
                        try {
                            String text = de.getDocument().getText(0, de.getDocument().getLength());
                            Object value = text;
                            if (!editorComponent.parserClass().isEmpty()) {
                                String p = editorComponent.parserClass();
                                Class parserClazz = Class.forName(p);
                                value = parserClazz.getMethod("parse", String.class).invoke(parserClazz.newInstance(), text);
                            }

                            PropertyUtils.setProperty(data, field.getName(), field.getType().cast(value));
                            if (saveMode == SAVE_MODE_IMMEDIATE) broadcast(new DataChangeEvent(thisPanel, data));
                        } catch (BadLocationException e1) {
                            OPDE.error(logger, e1);
                        } catch (IllegalAccessException e1) {
                            OPDE.error(logger, e1);
                        } catch (InvocationTargetException ite) {
                            if (ite.getTargetException() instanceof ParseException) {
                                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(ite.getTargetException().getMessage(), DisplayMessage.WARNING));
                            } else {
                                OPDE.error(logger, ite);
                            }
                        } catch (NoSuchMethodException e1) {
                            OPDE.error(logger, e1);
                        } catch (ConstraintViolationException cve) {
                            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(cve));
                        } catch (ClassNotFoundException e) {
                            OPDE.error(logger, e);
                        } catch (InstantiationException e) {
                            OPDE.error(logger, e);
                        }
                    }));
                    comp = txt;
                } else if (editorComponent.component()[0].equalsIgnoreCase("combobox")) {
                    ItemListener il = e -> {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            try {
                                PropertyUtils.setProperty(data, field.getName(), field.getType().cast(((JComboBox) e.getSource()).getSelectedIndex()));
                                broadcast(new DataChangeEvent(thisPanel, data));
                            } catch (Exception e1) {
                                logger.debug(e1);
                            }
                        }
                    };

                    JComboBox combobox = new JComboBox(new DefaultComboBoxModel<>(ArrayUtils.subarray(editorComponent.component(), 1, editorComponent.component().length - 1)));
                    combobox.setSelectedIndex(Integer.parseInt(PropertyUtils.getProperty(data, field.getName()).toString()));
                    combobox.addItemListener(il);
                    comp = combobox;
                } else if (editorComponent.component()[0].equalsIgnoreCase("onoffswitch")) {
                    ItemListener il = e -> {
                        if (e.getStateChange() == ItemEvent.SELECTED || e.getStateChange() == ItemEvent.DESELECTED) {
                            try {
                                PropertyUtils.setProperty(data, field.getName(), new Boolean(e.getStateChange() == ItemEvent.SELECTED));
                                broadcast(new DataChangeEvent(thisPanel, data));
                            } catch (Exception e1) {
                                logger.debug(e1);
                            }
                        }
                    };

                    JToggleButton btnSingle = GUITools.getNiceToggleButton(editorComponent.label());
                    btnSingle.addItemListener(il);
                    comp = btnSingle;
                }
                comp.setName(field.getName());
                comp.setToolTipText(editorComponent.tooltip().isEmpty() ? null : SYSTools.xx(editorComponent.tooltip()));
                componentSet.add(comp);

                add(comp == null ? new JLabel("??") : comp, CC.xy(4, row + 1));

                row += 2;
            }
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
                String violations = "";
                for (ConstraintViolation cv : cve.getConstraintViolations()) {
                    violations += cv.getMessage() + "; ";
                }
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(violations, DisplayMessage.WARNING));
            }
            reload();
        });
        buttonPanel.add(btnOK);

        JButton btnCancel = new JButton(SYSConst.icon22cancel);
        btnCancel.addActionListener(e -> {
            reload(); // revert to old bean state
        });

        buttonPanel.add(btnCancel);


        add(buttonPanel, CC.xyw(1, componentSet.size() + 5, 5, CC.RIGHT, CC.DEFAULT));
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
//        logger.debug(data.toString());
        for (Component comp : componentSet) {
            if (comp instanceof JTextComponent) {
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
