package com.jatti.gui.inv.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(value = ConfigItems.class)
public @interface ConfigItem {

    String value();

    String action() default "";

    boolean clickable() default false;

    int slot();
}
