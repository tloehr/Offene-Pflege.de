package de.offene_pflege.gui.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * these annotations are used with the class PnlBeanEditor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface EditorComponent {

    // syntax like component = {"textfield"} or component = {"combobox", "2.Untergeschoss", "1.Untergeschoss", "Erdgeschoss", "1.Etage", "2.Etage", "3.Etage", "4.Etage", "5.Etage", "6.Etage"}
    // component = {"onoffswitch", "leftext", "righttext"}
    String[] component() default "textfield";
    String readonly() default "false";
    String triggersReload() default "false";
    String filled() default "true"; // should the line be filled with the component or left orientated
    String label();
    String tooltip() default "";
    String parserClass() default "";
    String renderer() default "";
    String model() default "";

}
