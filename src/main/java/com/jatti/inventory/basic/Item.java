package com.jatti.inventory.basic;

import org.bukkit.Material;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(value = Items.class)
public @interface Item {

    Material material();

    int amount();

    String name();

    String[] lore();

    int slot();
}
