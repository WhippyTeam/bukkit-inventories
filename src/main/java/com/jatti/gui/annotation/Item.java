package com.jatti.gui.annotation;

import org.bukkit.Material;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(value = Items.class)
public @interface Item {

    Material material();

    short type() default 0;

    int amount() default 1;

    String name() default "";

    String[] lore() default "";

    int slot();

}
