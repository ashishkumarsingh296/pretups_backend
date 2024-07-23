package com.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is a custom annotation used for @ControllerAdvice
 * It's a combination of @ControllerAdvice and @ResponseBody to cater
 * the Rest response only and not look for view resolver, which is default behavior for @ControllerAdvice
 */

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ControllerAdvice
@ResponseBody
public @interface PretupsRestControllerAdvice {
    @AliasFor(
            annotation = ControllerAdvice.class
    )
    String value() default "";
}
