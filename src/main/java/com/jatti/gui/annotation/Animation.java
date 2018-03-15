package com.jatti.gui.annotation;

import com.jatti.gui.basic.AnimationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Animation {

    AnimationType animationType();

    int row() default 0;

}
