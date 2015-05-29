package gui;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import interfaces.ContentProvider;
import interfaces.DataChangeEvent;
import interfaces.DataChangeListener;
import interfaces.EditPanelDefault;
import op.OPDE;
import op.tools.GUITools;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.validation.ConstraintViolation;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by tloehr on 28.05.15.
 */
public class PnlBeanEditor<T> extends EditPanelDefault<T> {
    //    final BeanInfo beanInfo;
    private final String[] fields;
    private HashMap<String, PropertyDescriptor> propsMap;
    private HashMap<String, MethodDescriptor> methodMap;
    private Logger logger = Logger.getLogger(this.getClass());

    public PnlBeanEditor(DataChangeListener dcl, ContentProvider<T> contentProvider, Class<T> clazz, String[] fields) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        super(dcl, contentProvider);
        this.fields = fields;

//        beanInfo = Introspector.getBeanInfo(clazz);
//
//        beanInfo.getBeanDescriptor();
//        beanInfo.getAdditionalBeanInfo();
//        beanInfo.getPropertyDescriptors();
//        beanInfo.getMethodDescriptors();
//
//
//        propsMap = new HashMap<>();
//        methodMap = new HashMap<>();
////        PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
//
//
//        for (PropertyDescriptor prop : beanInfo.getPropertyDescriptors()) {
//            propsMap.put(prop.getName(), prop);
//        }
//
//        for (MethodDescriptor md : beanInfo.getMethodDescriptors()) {
//            methodMap.put(md.getName(), md);
//        }

        initPanel();


    }

    void initPanel() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {


        //todo: validator testen


//        )
//
//        String rows = StringUtils.repeat("p, 3dlu,", fields.length);
//        rows = SYSTools.left(rows, rows.length() - 1, "");

        setLayout(new FormLayout("2*(default, $lcgap), 162dlu:grow, $lcgap, default",
                fields.length + "*(default, $lgap), default"));

        int row = 1;
        for (String field : fields) {

            JLabel lblName = new JLabel(field);
            lblName.setFont(new Font("Arial", Font.PLAIN, 14));
            add(lblName, CC.xy(1, row));

            Component comp = null;

            if (PropertyUtils.getProperty(data, field) instanceof String) {
                JTextField txt = new JTextField(PropertyUtils.getProperty(data, field).toString());
                txt.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        try {
                            PropertyUtils.setProperty(data, field, ((JTextField) e.getSource()).getText());
                            broadcast(new DataChangeEvent(thisPanel, data));
                        } catch (Exception e1) {
                            logger.debug(e1);
                        }
                    }
                });
                comp = txt;
            } else if (PropertyUtils.getProperty(data, field) instanceof Short || PropertyUtils.getProperty(data, field) instanceof Integer) {
                JTextField txt = new JTextField(PropertyUtils.getProperty(data, field).toString());
                txt.addVetoableChangeListener(new VetoableChangeListener() {
                    @Override
                    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {

                        logger.debug(evt);
                        Set<ConstraintViolation<T>> constraintViolations = OPDE.getValidatorFactory().getValidator().validateProperty();
                        //todo: schau mal ob das in die Zukunft gucken kann ?


                    }
                });

                txt.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        try {

                            PropertyUtils.setProperty(data, field, ((JTextField) e.getSource()).getText());
                            broadcast(new DataChangeEvent(thisPanel, data));
                        } catch (Exception e1) {
                            logger.debug(e1);
                        }
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


            add(comp == null ? new JLabel("??") : comp, CC.xy(3, row));

            row += 2;

        }


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
