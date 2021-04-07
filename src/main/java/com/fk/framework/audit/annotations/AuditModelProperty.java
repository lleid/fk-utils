package com.fk.framework.audit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 审计追踪参数
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditModelProperty {

    /**
     * 字段中文名称
     *
     * @return
     */
    String name() default "";

    /**
     * 如果是对象或List，需要配置Class
     *
     * @return
     */
    String className() default "";

    /**
     * 当属性为Date时，需要设置格式化的方式。
     * 默认为yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    String pattern() default "";

    /**
     * 如果是对象或List，若需要审计详情中需要记录该信息时，需要指明字段名称
     *
     * @return
     */
    String[] include() default {};

    /**
     * 是否持久的
     * 不需要比对，审计追踪中一直显示
     *
     * @return
     */
    boolean isPersistent() default false;
}
