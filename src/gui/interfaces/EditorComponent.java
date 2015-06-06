package gui.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * these annotations are used with the class PnlBeanEditor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EditorComponent {

    String[] component() default "textfield"; // syntax like component = {"textfield"} or component = {"combobox", "2.Untergeschoss", "1.Untergeschoss", "Erdgeschoss", "1.Etage", "2.Etage", "3.Etage", "4.Etage", "5.Etage", "6.Etage"}
    String label();
    String tooltip() default "";
    String parserClass() default "";

}
