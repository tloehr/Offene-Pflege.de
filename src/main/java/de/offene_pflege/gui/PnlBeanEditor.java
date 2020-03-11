package de.offene_pflege.gui;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.pane.CollapsiblePane;
import de.offene_pflege.gui.events.DataChangeEvent;
import de.offene_pflege.gui.events.RelaxedDocumentListener;
import de.offene_pflege.gui.interfaces.*;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.Closure;
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
 * This Panel accepts annotated Beans which describe a form that needs to be entered.
 * It uses the annotations from the class gui.interfaces.EditorComponent to tell this editor
 * how to handle the values of the Bean Class. Only fields which are annotated as EditorComponent
 * are handled here. The others are ignored.
 * <p>
 * there is a data object in the parent class which is automatically initialized by the constructor
 * <p>
 * <p>
 * So why all this fuss ? This class makes the creation of an editor very easy. We simply define a Bean class
 * with all the constraints we want to be obeyed during the edit phase. And this class handles it in no time.
 * All the constraints are taken care of by the javax.validation framework.
 */
public class PnlBeanEditor<T> extends EditPanelDefault<T> {
    private final Class<T> clazz;
    private Closure cancelCallback;
    //    private JPanel customPanel;
    //    private final String[][] fields;
    private boolean ignoreListener = false;
    private Logger logger = Logger.getLogger(this.getClass());
    private HashSet<Component> componentSet;
    public static final int SAVE_MODE_IMMEDIATE = 0;
    public static final int SAVE_MODE_OK_CANCEL = 1;
    public static final int SAVE_MODE_CUSTOM = 2; // requires ButtonPanel
    private int saveMode;


    /**
     * constructor
     *
     * @param dataProvider is the implementation of the DataProvider which is used every time this editor needs the data to display
     * @param clazz        is the class object of the bound class for this editor. technical reasons. no big deal.
     * @param saveMode     tells the editor when to save its contents to the ancestor's data object
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    public PnlBeanEditor(DataProvider<T> dataProvider, Class<T> clazz, int saveMode)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        super(dataProvider);
        cancelCallback = null;
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

//    public PnlBeanEditor(DataProvider<T> dataProvider, Class<T> clazz, Closure cancelCallback)
//            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
//        this(dataProvider, clazz, SAVE_MODE_OK_CANCEL);
//        this.cancelCallback = cancelCallback;
//    }

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


    /**
     * creates the panel programmically according to the definitions in the bean blass T.
     *
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    void initPanel() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        // fields means the fields of the BeanClass which defines the bahavior of the editor
        Field[] fields = data.getClass().getDeclaredFields();

//        Method[] methods = data.getClass().getDeclaredMethods();

        // I have to count them first.
        // makes sure, that we only care about fields which are meant for this editor.
        int numfields = 0;
        for (final Field field : fields) {
            if (field.isAnnotationPresent(EditorComponent.class)) {
                numfields++;
            }
        }

        // create a simple formlayout which is extendable
        setLayout(new FormLayout("5dlu, default, $lcgap, 162dlu:grow, $lcgap, 5dlu",
                "5dlu, " + (numfields + (saveMode == SAVE_MODE_IMMEDIATE ? 0 : 1) + "*(default, $lgap), default, 5dlu")));

        int row = 1;

        // deal with every single field
        for (final Field field : fields) {
            if (field.isAnnotationPresent(EditorComponent.class)) { // we only care about EditorComponents

                EditorComponent editorComponent = field.getAnnotation(EditorComponent.class);

                JLabel lblName = new JLabel(SYSTools.xx(editorComponent.label()));
                lblName.setFont(new Font("Arial", Font.BOLD, 14));

                JComponent comp = null;

                // this section handles every single type of editor component
                if (editorComponent.component()[0].equalsIgnoreCase("textfield")) {

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

                    // the data object sets the contents of the textfield
                    txt.setText(PropertyUtils.getProperty(data, field.getName()).toString());

                    txt.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            ((JTextField) e.getSource()).selectAll();
                        }
                    });

                    // changes to the textfield are handled by a document listener
                    txt.getDocument().addDocumentListener(new RelaxedDocumentListener(de -> {
                        if (ignoreListener) return;

                        if (saveMode == SAVE_MODE_IMMEDIATE)
                            reload();

                        try {
                            String text = de.getDocument().getText(0, de.getDocument().getLength());
                            Object value = text;
                            // if there is a parser defined. it is loaded here
                            if (!editorComponent.parserClass().isEmpty()) {
                                String p = editorComponent.parserClass();
                                logger.debug(String.format("Parserclass for Field %s: %s", field.getName(), p));

                                // loads the specified parser class (needs to have a "parse" method)
                                Class parserClazz = Class.forName(p);


                                value = parserClazz.getMethod("parse", String.class).invoke(parserClazz.newInstance(), text);
                                logger.debug(String.format("Parsed value: %s", value.toString()));
                            }


                            PropertyUtils.setProperty(data, field.getName(), ConvertUtils.convert(value, field.getType())); // ConverterUtils fixes #25
//                            logger.debug(String.format("Content of the 'data' object: %s", ((Properties) data).getProperty(field.getName())));

                            if (saveMode == SAVE_MODE_IMMEDIATE) {
                                broadcast(); // spread the news, that the data object was updated. in case of a contraint violation, an exception is thrown
                            }

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
                            logger.debug("Constraint violation!!");
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
                        // https://github.com/tloehr/Offene-Pflege.de/issues/29
                        combobox.setSelectedIndex(Integer.parseInt(PropertyUtils.getProperty(data, field.getName()).toString()));
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
                            if (saveMode == SAVE_MODE_IMMEDIATE) {
                                DataChangeEvent<T> dce = new DataChangeEvent(thisPanel, data);
                                if (editorComponent.triggersReload().equalsIgnoreCase("true"))
                                    dce.setTriggersReload(true);
                                broadcast(dce);
                            }
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
                            PropertyUtils.setProperty(data, field.getName(), GUITools.getHTMLColor(((ColorSelectionModel) e.getSource()).getSelectedColor()));

                            if (saveMode == SAVE_MODE_IMMEDIATE) {
                                DataChangeEvent<T> dce = new DataChangeEvent(thisPanel, data);
                                if (editorComponent.triggersReload().equalsIgnoreCase("true"))
                                    dce.setTriggersReload(true);
                                broadcast(dce);
                            }
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
//                comp.setPanelEnabled();
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
            if (cancelCallback == null) {
                reload();
                refreshDisplay();
            } else {
                cancelCallback.execute(null);
            }
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
    public void broadcast() throws
            IllegalAccessException, NoSuchMethodException, InvocationTargetException, SQLIntegrityConstraintViolationException {
        broadcast(new DataChangeEvent(thisPanel, data));
    }

    public void broadcast(DataChangeEvent dce) throws
            IllegalAccessException, NoSuchMethodException, InvocationTargetException, SQLIntegrityConstraintViolationException {
        super.broadcast(dce);
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
