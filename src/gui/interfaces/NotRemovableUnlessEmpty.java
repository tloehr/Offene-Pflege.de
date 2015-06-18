package gui.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used, when an existing relation should technically prevent the deletion of an entity.
 * This is only for GUI control. No JPA affection.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NotRemovableUnlessEmpty {
    String message() default "msg.validation.cantberemoved.collection.not.empty";
}
