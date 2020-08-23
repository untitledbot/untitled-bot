package dev.alexisok.untitledbot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the provided method or class is finished
 * and modifications should not be made.  This should only
 * be used for example classes and should only be modified
 * when there is an update to the API or there is an error
 * that should be fixed.  Code is never finished, but this
 * method indicates that there should not be modifications
 * to anything with this annotation.
 */
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Finished {
    String since();
}
