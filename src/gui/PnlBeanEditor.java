package gui;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.pane.CollapsiblePane;
import gui.events.DataChangeEvent;
import gui.events.RelaxedDocumentListener;
import gui.interfaces.*;
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
import javax.validation.constraints.Size;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyVetoException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.ParseException;
import java.util.HashSet;

/**
 * Created by tloehr on 28.05.15.
 */
public class PnlBeanEditor<T> extends EditPanelDefault<T> {
    private final Class<T> clazz;
    //    private JPanel customPanel;
    //    private final String[][] fields;
    private boolean ignoreListener = false;
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

        initPanel();
        initButtonPanel();
    }

    public PnlBeanEditor(DataProvider<T> dataProvider, Class<T> clazz)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        this(dataProvider, clazz, SAVE_MODE_CUSTOM);
    }

    public void setCustomPanel(JPanel customPanel) {
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

//                    boolean accepted = false;

                    JPanel innerPanel = new JPanel();
                    innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.LINE_AXIS));
                    final JLabel lblOK = new JLabel(SYSConst.icon16apply);

                    final JTextField txt;
                    if (field.isAnnotationPresent(Size.class)) {
                        Size sizeConstraint = field.getAnnotation(Size.class);
                        txt = new BoundedTextField(sizeConstraint.min(), sizeConstraint.max());
                    } else {
                        txt = new JTextField();
                    }

                    txt.setText(PropertyUtils.getProperty(data, field.getName()).toString());

                    txt.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            ((JTextField) e.getSource()).selectAll();
                        }
                    });

                    txt.getDocument().addDocumentListener(new RelaxedDocumentListener(de -> {
                        if (ignoreListener) return;

                        if (saveMode == SAVE_MODE_IMMEDIATE)
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
                            if (saveMode == SAVE_MODE_IMMEDIATE)
                                broadcast();
                            OPDE.getDisplayManager().clearSubMessages();
                            lblOK.setIcon(SYSConst.icon16apply);
                            lblOK.setToolTipText(null);
                            GUITools.flashBackground(txt, SYSConst.darkolivegreen1, Color.white, 2);
                        } catch (BadLocationException e1) {
                            OPDE.error(logger, e1);
                            lblOK.setIcon(SYSConst.icon16cancel);
                            lblOK.setToolTipText(SYSTools.toHTMLForScreen(SYSConst.html_bold(e1.getMessage())));
                        } catch (IllegalAccessException e1) {
                            OPDE.error(logger, e1);
                            lblOK.setToolTipText(SYSTools.toHTMLForScreen(SYSConst.html_bold(e1.getMessage())));
                        } catch (InvocationTargetException ite) {
                            if (ite.getTargetException() instanceof ParseException) {
                                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(ite.getTargetException().getMessage(), DisplayMessage.WARNING));
                            } else {
                                OPDE.error(logger, ite);
                            }
                            lblOK.setIcon(SYSConst.icon16cancel);
                            lblOK.setToolTipText(SYSTools.toHTMLForScreen(SYSConst.html_bold(ite.getMessage())));
                        } catch (NoSuchMethodException e) {
                            OPDE.error(logger, e);
                            lblOK.setIcon(SYSConst.icon16cancel);
                            lblOK.setToolTipText(SYSTools.toHTMLForScreen(SYSConst.html_bold(e.getMessage())));
                        } catch (ConstraintViolationException cve) {
                            if (saveMode == SAVE_MODE_IMMEDIATE) {
                                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(cve));
                            }

                            String message = "";
                            for (ConstraintViolation cv : cve.getConstraintViolations()) {
                                logger.debug(cv.getPropertyPath().toString());
                                logger.debug(field.getName());
                                if (cv.getPropertyPath().toString().equals(field.getName())) {
                                    message = cv.getMessage();
                                    break;
                                }
                            }

                            if (message.isEmpty()) {
                                OPDE.getDisplayManager().clearSubMessages();
                                lblOK.setIcon(SYSConst.icon16apply);
                                lblOK.setToolTipText(null);
                                GUITools.flashBackground(txt, SYSConst.darkolivegreen1, Color.white, 2);
                            } else {
                                lblOK.setIcon(SYSConst.icon16cancel);
                                lblOK.setToolTipText(SYSTools.toHTMLForScreen(SYSConst.html_paragraph(message)));
                                GUITools.flashBackground(txt, SYSConst.orangered, Color.white, 2);
                            }


                        } catch (ClassNotFoundException e) {
                            OPDE.error(logger, e);
                            lblOK.setIcon(SYSConst.icon16cancel);
                            lblOK.setToolTipText(SYSTools.toHTMLForScreen(SYSConst.html_bold(e.getMessage())));
                        } catch (InstantiationException e) {
                            OPDE.error(logger, e);
                            lblOK.setIcon(SYSConst.icon16cancel);
                            lblOK.setToolTipText(SYSTools.toHTMLForScreen(SYSConst.html_bold(e.getMessage())));
                        } catch (SQLIntegrityConstraintViolationException e) {
                            OPDE.warn(logger, e);
                            lblOK.setIcon(SYSConst.icon16cancel);
                            lblOK.setToolTipText(SYSTools.toHTMLForScreen("error.sql.integrity"));
                            GUITools.flashBackground(txt, SYSConst.orangered, Color.white, 2);
                        }
                    }));

                    innerPanel.add(txt);
                    innerPanel.add(lblOK);
                    innerPanel.setOpaque(false);

                    comp = innerPanel;
                    comp.setName("innerPanel");
                    txt.setName(field.getName());
                } else if (editorComponent.component()[0].equalsIgnoreCase("combobox")) {

                    // this is the default ItemListener, if there is no renderer defined
                    ItemListener il = e -> {
                        if (ignoreListener) return;

                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            if (saveMode == SAVE_MODE_IMMEDIATE)
                                reload();
                            try {
                                PropertyUtils.setProperty(data, field.getName(), field.getType().cast(((JComboBox) e.getSource()).getSelectedIndex()));
                                if (saveMode == SAVE_MODE_IMMEDIATE)
                                    broadcast();
                            } catch (Exception e1) {
                                logger.debug(e1);
                            }
                        }
                    };

                    ListCellRenderer<T> renderer = null;
                    DefaultComboBoxModel dcbm = new DefaultComboBoxModel<>(ArrayUtils.subarray(editorComponent.component(), 1, editorComponent.component().length - 1));
                    try {
                        if (!editorComponent.renderer().isEmpty()) {
                            String r = editorComponent.renderer();
                            Class rendererClazz = Class.forName(r);

                            renderer = (ListCellRenderer) rendererClazz.newInstance();

                            String m = editorComponent.model();
                            Class modelClazz = Class.forName(m);

                            dcbm = (DefaultComboBoxModel) modelClazz.newInstance();


                            // if there is a renderer weg got for the object itself, rather than the selected index
                            il = e -> {
                                if (e.getStateChange() == ItemEvent.SELECTED) {
                                    if (ignoreListener) return;
                                    if (saveMode == SAVE_MODE_IMMEDIATE)
                                        reload();
                                    try {
                                        PropertyUtils.setProperty(data, field.getName(), field.getType().cast(((JComboBox) e.getSource()).getSelectedItem()));
                                        if (saveMode == SAVE_MODE_IMMEDIATE)
                                            broadcast();
                                    } catch (Exception e1) {
                                        logger.debug(e1);
                                    }
                                }
                            };

                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    }


                    JComboBox combobox = new JComboBox(dcbm);
                    if (renderer != null) {
                        combobox.setSelectedItem(PropertyUtils.getProperty(data, field.getName()));
                    } else {
                        combobox = new JComboBox(new DefaultComboBoxModel<>(ArrayUtils.subarray(editorComponent.component(), 1, editorComponent.component().length - 1)));
                    }

                    combobox.addItemListener(il);
                    if (renderer != null) combobox.setRenderer(renderer);

                    comp = combobox;
                    comp.setName(field.getName());
                } else if (editorComponent.component()[0].equalsIgnoreCase("onoffswitch")) {
                    String yesText = "misc.msg.yes";
                    String noText = "misc.msg.no";
                    if (editorComponent.component().length == 3) {
                        yesText = editorComponent.component()[1];
                        noText = editorComponent.component()[2];
                    }

                    comp = new YesNoToggleButton(yesText, noText, (boolean) PropertyUtils.getProperty(data, field.getName()));
                    comp.setName(field.getName());
                    ((YesNoToggleButton) comp).addItemListener(e -> {
                        if (ignoreListener) return;
                        if (saveMode == SAVE_MODE_IMMEDIATE)
                            reload();
                        try {
                            PropertyUtils.setProperty(data, field.getName(), new Boolean(e.getStateChange() == ItemEvent.SELECTED));
                            if (saveMode == SAVE_MODE_IMMEDIATE)
                                broadcast();
                        } catch (Exception e1) {
                            logger.debug(e1);
                        }
                    });
                } else if (editorComponent.component()[0].equalsIgnoreCase("colorset")) {

                    JColorChooser clr = new JColorChooser(GUITools.getColor(PropertyUtils.getProperty(data, field.getName()).toString()));
                    clr.getSelectionModel().addChangeListener(e -> {
                        if (saveMode == SAVE_MODE_IMMEDIATE)
                            reload();
                        try {
                            PropertyUtils.setProperty(data, field.getName(), GUITools.toHexString(((ColorSelectionModel) e.getSource()).getSelectedColor()));
                            DataChangeEvent<T> dce = new DataChangeEvent(thisPanel, data);
                            dce.setTriggersReload(true);
                            if (saveMode == SAVE_MODE_IMMEDIATE)
                                broadcast();
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
                    comp.setName(field.getName());
                } else {
                    OPDE.fatal(logger, new IllegalStateException("invalid component name in EditorComponent Annotation"));
                }

                if (comp instanceof CollapsiblePane) {
                    add(comp, CC.xyw(2, row + 1, 4, CC.FILL, CC.DEFAULT));
                } else if (editorComponent.filled().equals("false")) {
                    add(lblName, CC.xy(2, row + 1, CC.LEFT, CC.TOP));
                    add(comp, CC.xy(4, row + 1, CC.LEFT, CC.TOP));
                } else {
                    add(lblName, CC.xy(2, row + 1, CC.LEFT, CC.TOP));
                    add(comp, CC.xy(4, row + 1));
                }

                SYSTools.setXEnabled(comp, editorComponent.readonly().equals("false"));
//                comp.setEnabled();
                comp.setToolTipText(editorComponent.tooltip().isEmpty() ? null : SYSTools.xx(editorComponent.tooltip()));
                componentSet.add(comp);


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
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(cve));
            } catch (InvocationTargetException e1) {
                e1.printStackTrace();
            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (SQLIntegrityConstraintViolationException e1) {
                OPDE.warn(logger, e1);
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(e1.getMessage()));
            }
            if (saveMode == SAVE_MODE_IMMEDIATE)
                reload();
        });
        buttonPanel.add(btnOK);

        JButton btnCancel = new JButton(SYSConst.icon22cancel);
        btnCancel.addActionListener(e -> {
            reload();
            refreshDisplay();
            // revert to old bean state
        });

        buttonPanel.add(btnCancel);


        add(buttonPanel, CC.xyw(1, componentSet.size() * 2 + 2, 5, CC.RIGHT, CC.DEFAULT));
    }

//    @Override
//    public void broadcast() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
//        if (saveMode != SAVE_MODE_IMMEDIATE) return;
//        super.broadcast(new DataChangeEvent(thisPanel, data));
//    }


    /**
     * causes the editor to send the current state of its data to all listeners.
     *
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */

    public void broadcast() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, SQLIntegrityConstraintViolationException {
        super.broadcast(new DataChangeEvent(thisPanel, data));
    }

//    @Override
//    public void broadcast(DataChangeEvent<T> dce) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
//        if (saveMode != SAVE_MODE_IMMEDIATE) return;
//        super.broadcast(new DataChangeEvent(thisPanel, data));
//    }


    @Override
    public void setStartFocus() {

    }

    @Override
    public void setEnabled(boolean enabled) {
        for (Component comp : componentSet) {
            comp.setEnabled(enabled);
        }
        super.setEnabled(enabled);
    }

    @Override
    public String doValidation() {
        return null;
    }

    @Override
    public void refreshDisplay() {
        ignoreListener = true;
        for (Component comp : componentSet) {
            if (comp instanceof JPanel && comp.getName().equals("innerPanel")) {  // textcomponents are embedded in a JPanel
                for (Component innerComp : ((JPanel) comp).getComponents()) {
                    if (innerComp instanceof JTextComponent) {
                        try {
                            ((JTextComponent) innerComp).setText(PropertyUtils.getProperty(data, innerComp.getName()).toString());
                        } catch (IllegalAccessException e) {
                            OPDE.error(logger, e);
                        } catch (InvocationTargetException e) {
                            OPDE.error(logger, e);
                        } catch (NoSuchMethodException e) {
                            OPDE.error(logger, e);
                        }
                    }
                }
            } else if (comp instanceof YesNoToggleButton) {
                try {
                    ((YesNoToggleButton) comp).setSelected((boolean) PropertyUtils.getProperty(data, comp.getName()));
                } catch (IllegalAccessException e) {
                    OPDE.error(logger, e);
                } catch (InvocationTargetException e) {
                    OPDE.error(logger, e);
                } catch (NoSuchMethodException e) {
                    OPDE.error(logger, e);
                }
            } else if (comp instanceof JComboBox) {

                //TODO: Das muss noch fertig werden
                JComboBox combobox = (JComboBox) comp;

//                                    if (renderer != null) {
//                                        combobox.setSelectedItem(PropertyUtils.getProperty(data, field.getName()));
//                                    } else {
//                                        combobox = new JComboBox(new DefaultComboBoxModel<>(ArrayUtils.subarray(editorComponent.component(), 1, editorComponent.component().length - 1)));
//                                    }
            }
        }

        ignoreListener = false;
    }


}
