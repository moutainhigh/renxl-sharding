package com.renxl.sharding.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RenxlRoute {
    @AliasFor("defaultDb")
    String value() default "";
    /**
     * 默认的数据库别名 指定该别名的将不会根据RenxlRouteId指定的字段进行分库分表
     * @return
     */
    @AliasFor("value")
    String defaultDb() default "";

    /**
     * 从logictables中排除相关表，从而达到不进行分表的效果
     * @return
     */
    String[] excludesTables() default "";

    /**
     * 需要进行分库分表的逻辑表名集合
     * @return
     */
    String[] logictables() default "";
}
