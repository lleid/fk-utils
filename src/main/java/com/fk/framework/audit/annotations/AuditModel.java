package com.fk.framework.audit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides additional information about Audit models.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditModel {
    /**
     * Provide an alternative name for the model.
     */
    String value() default "";

    /**
     * Provide a longer description of the class.
     */
    String description() default "";
}
