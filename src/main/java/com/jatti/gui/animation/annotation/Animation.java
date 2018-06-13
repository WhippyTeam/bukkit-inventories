package com.jatti.gui.animation.annotation;

import com.jatti.gui.AnimationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Animation {

    AnimationType animationType();

    int row() default 0;

    int[] slots() default {};

    long delay() default 0L;

    long interval() default 4L;
    
}
