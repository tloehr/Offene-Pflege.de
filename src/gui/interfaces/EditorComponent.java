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

    // hier ein editor auswahl feld eingeben
    String[] combobox() default "";
    String[] comboboxWithEmptyElement() default "";
    String label();
    String tooltip() default "";


}
