package gui;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.pane.CollapsiblePane;
import gui.events.DataChangeEvent;
import gui.events.RelaxedDocumentListener;
import gui.interfaces.DataProvider;
import gui.interfaces.EditPanelDefault;
import gui.interfaces.EditorComponent;
import gui.interfaces.YesNoToggleButton;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.HorizontalLayout;

import javax.swing.*;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyVetoException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashSet;

/**
 * Created by tloehr on 28.05.15.
 */
public class PnlBeanEditor<T> extends EditPanelDefault<T> {
    private final Class<T> clazz;
    //    private JPanel customPanel;
    //    private final String[][] fields;
    private Logger logger = Logger.getLogger(this.getClass());
    private HashSet<Component> componentSet;
    public static final int SAVE_MODE_IMMEDIATE = 0;
    public static final int SAVE_MODE_OK_CANCEL = 1;
    public static final int SAVE_MODE_CUSTOM = 2; // requires ButtonPanel
    private int saveMode;


    public PnlBeanEditor(DataProvider<T> dataProvider, Class<T> clazz, int saveMode)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        super(dataProvider);
        this.clazz = clazz;
        setOpaque(false);

        this.saveMode = saveMode;
        this.componentSet = new HashSet<>();

//        if (saveMode == SAVE_MODE_CUSTOM) throw new NoSuchMethodException("wrong constructor for this mode");

        initPanel();
        initButtonPanel();
    }

    public PnlBeanEditor(DataProvider<T> dataProvider, Class<T> clazz)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        this(dataProvider, clazz, SAVE_MODE_CUSTOM);
    }

    public void setCustomPanel(JPanel customPanel){
        add(customPanel, CC.xyw(1, componentSet.size() * 2 + 2, 5, CC.FILL, CC.FILL));
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);

        if (componentSet == null) return;

        for (Component comp : componentSet) {
            comp.setBackground(GUITools.blend(bg, Color.WHITE, 0.2f));
        }
    }

    @Override
    public void setOpaque(boolean isOpaque) {
        super.setOpaque(isOpaque);
        if (componentSet == null) return;
        for (Component comp : componentSet) {
            if (comp instanceof JColorChooser) {
                ((JColorChooser) comp).setOpaque(isOpaque);
            }
        }
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
                lblName.setFont(new Font("Arial", Font.BOLD, 14));


                JComponent comp = null;

                if (editorComponent.component()[0].equalsIgnoreCase("textfield")) {
                    JTextField txt = new JTextField(PropertyUtils.getProperty(data, field.getName()).toString());

                    // its not that simple
                    // txt.setColumns(field.getAnnotation(Size.class) != null ? field.getAnnotation(Size.class).max() : 0);

                    txt.getDocument().addDocumentListener(new RelaxedDocumentListener(de -> {

                        reload();

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
                            reload();
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
                    String yesText = "misc.msg.yes";
                    String noText = "misc.msg.no";
                    if (editorComponent.component().length == 3) {
                        yesText = editorComponent.component()[1];
                        noText = editorComponent.component()[2];
                    }

                    comp = new YesNoToggleButton(yesText, noText, (boolean) PropertyUtils.getProperty(data, field.getName()));

                    ((YesNoToggleButton) comp).addItemListener(e -> {
                        reload();
                        try {
                            PropertyUtils.setProperty(data, field.getName(), new Boolean(e.getStateChange() == ItemEvent.SELECTED));
                            broadcast(new DataChangeEvent(thisPanel, data));
                        } catch (Exception e1) {
                            logger.debug(e1);
                        }
                    });
                } else if (editorComponent.component()[0].equalsIgnoreCase("colorset")) {

                    JColorChooser clr = new JColorChooser(GUITools.getColor(PropertyUtils.getProperty(data, field.getName()).toString()));
                    clr.getSelectionModel().addChangeListener(e -> {
                        reload();
                        try {
                            PropertyUtils.setProperty(data, field.getName(), GUITools.toHexString(((ColorSelectionModel) e.getSource()).getSelectedColor()));
                            DataChangeEvent<T> dce = new DataChangeEvent(thisPanel, data);
                            dce.setTriggersReload(true);
                            broadcast(dce);
                        } catch (Exception e1) {
                            logger.debug(e1);
                        }
                    });


                    CollapsiblePane cp = new CollapsiblePane(SYSTools.xx(editorComponent.label()));
                    cp.setStyle(CollapsiblePane.PLAIN_STYLE);
                    cp.setIcon(SYSConst.icon22colorset);
                    try {
                        cp.setCollapsed(true);
                    } catch (PropertyVetoException e) {
                        //bah
                    }
                    cp.setOpaque(false);
                    cp.setContentPane(clr);

                    comp = cp;

                } else {
                    OPDE.fatal(logger, new IllegalStateException("invalid component name in EditorComponent Annotation"));
                }

                CellConstraints cc = CC.xy(4, row + 1);


                if (!(comp instanceof CollapsiblePane)) {
                    add(lblName, CC.xy(2, row + 1, CC.LEFT, CC.TOP));
                } else {
                    cc = CC.xyw(2, row + 1, 4, CC.FILL, CC.DEFAULT);
                }

                comp.setEnabled(editorComponent.readonly().equals("false"));
                comp.setName(field.getName());
                comp.setToolTipText(editorComponent.tooltip().isEmpty() ? null : SYSTools.xx(editorComponent.tooltip()));
                componentSet.add(comp);

                add(comp == null ? new JLabel("??") : comp, cc);

                row += 2;
            }
        }
    }

    void initButtonPanel() {
        if (saveMode != SAVE_MODE_OK_CANCEL) return;

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
            } catch (InvocationTargetException e1) {
                e1.printStackTrace();
            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            }
            reload();
        });
        buttonPanel.add(btnOK);

        JButton btnCancel = new JButton(SYSConst.icon22cancel);
        btnCancel.addActionListener(e -> {
            reload(); // revert to old bean state
        });

        buttonPanel.add(btnCancel);


        add(buttonPanel, CC.xyw(1, componentSet.size() * 2 + 2, 5, CC.RIGHT, CC.DEFAULT));
    }


    public void broadcast() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        super.broadcast(new DataChangeEvent(thisPanel, data));
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
