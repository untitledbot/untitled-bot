package dev.alexisok.untitledbot.annotation;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

/**
 * Things annotated with this will be subject for removal
 * for the specified version.  They will be removed from the
 * code and anything using them will no longer function.
 * <br>
 * Note: things that use this are inherently {@link Deprecated}.
 * 
 * @see java.lang.annotation.Annotation
 * @see java.lang.Deprecated
 * @author AlexIsOK
 * @since 0.0.1
 */
@Inherited
@Documented
@SuppressWarnings("unused")
@Retention(RetentionPolicy.SOURCE)
@Target({CONSTRUCTOR, FIELD, METHOD, ANNOTATION_TYPE, TYPE})
public @interface ToBeRemoved {
    String value() default "Next major release.";
}
