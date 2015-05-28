package gui;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import interfaces.ContentProvider;
import interfaces.DataChangeListener;
import interfaces.EditPanelDefault;
import op.tools.SYSTools;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * Created by tloehr on 28.05.15.
 */
public class PnlBeanEditor<T> extends EditPanelDefault<T> {
    //    final BeanInfo beanInfo;
    private final String[] fields;
    private HashMap<String, PropertyDescriptor> propsMap;
    private HashMap<String, MethodDescriptor> methodMap;

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

        String rows = StringUtils.repeat("p, 3dlu,", fields.length);
        rows = SYSTools.left(rows, rows.length() - 1, "");

        setLayout(new FormLayout("right:pref, 3dlu, pref:grow, pref", rows));

        int row = 1;
        for (String field : fields) {

            JLabel lblName = new JLabel(field);
            lblName.setFont(new Font("Arial", Font.PLAIN, 14));
            add(lblName, CC.xy(1, row));


            add(new JTextField(PropertyUtils.getProperty(data, field).toString()), CC.xy(3, row));


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
