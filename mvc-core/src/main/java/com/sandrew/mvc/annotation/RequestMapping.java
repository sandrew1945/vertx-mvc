package com.sandrew.mvc.annotation;


import com.sandrew.mvc.core.RequestMethod;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping
{
    String name() default "";

    String[] value() default {};

    RequestMethod[] method() default {};
}
