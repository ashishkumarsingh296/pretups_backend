package com.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//To change the resteasy api services to support spring services, this annotation has been created.
//where ever this annotation would be applied with @RestController, it would be registered with
//path /rest instead of /rstapi
//logic for this can be found in RootApplicationConfig.java -> method configurePathMatch
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RestEasyAnnotation {
    String value() default "";
}