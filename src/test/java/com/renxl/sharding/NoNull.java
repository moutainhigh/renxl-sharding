package com.renxl.sharding;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface NoNull {
    @AliasFor("fields")
    String value() default "";
    @AliasFor("value")
    String fields() default "";
}