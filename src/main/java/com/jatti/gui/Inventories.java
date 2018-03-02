/*
 * MIT License
 * 
 * Copyright (c) 2018 Bartlomiej Stefanski
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.jatti.gui;

import com.jatti.gui.annotation.Fill;
import com.jatti.gui.annotation.Item;
import com.jatti.gui.annotation.Items;
import com.jatti.gui.exception.InventoryParseException;
import com.jatti.gui.util.DataUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Inventories {

    private static final Map<String, Inventory> INVENTORY_MAP = new HashMap<>();

    public static void register(Class<?> clazz, String name) {
        if (clazz.getDeclaredMethods().length == 0) {
            throw new InventoryParseException("Class \"" + clazz.getName() + "\" does not have any methods!");
        }

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getDeclaredAnnotations().length == 0) {
                continue;
            }

            Inventory inventory = Bukkit.createInventory(null, 9, "");
            for (Annotation annotation : method.getDeclaredAnnotations()) {
                if (annotation.annotationType() == com.jatti.gui.annotation.Inventory.class) {
                    inventory = DataUtils.handleInventoryAnnotation((com.jatti.gui.annotation.Inventory) annotation);
                }
                
                else if (annotation.annotationType() == Items.class) {
                    for (Item item : ((Items) annotation).value()) {
                        DataUtils.handleItemAnnotation(item, inventory);
                    }
                }
                
                else if (annotation.annotationType() == Fill.class) {
                    DataUtils.handleFillAnnotation((Fill) annotation, inventory);
                }
            }

            INVENTORY_MAP.put(name, inventory);
        }
    }

    public static Map<String, Inventory> getInventoryMap() {
        return new HashMap<>(INVENTORY_MAP);
    }

    public static Inventory getInventory(String inventoryName) {
        return INVENTORY_MAP.get(inventoryName);
    }

    public static void openInventory(String name, Player player) {
        player.openInventory(getInventory(name));
    }

    public static void openWorkbench(String name, Player player) {
        Inventory inv = getInventory(name);
        if (inv.getType() != InventoryType.WORKBENCH) {
            player.openInventory(inv);
            return;
        }

        InventoryView workbench = player.openWorkbench(null, true);
        for (int i = 9; i >= 0; i--) {
            workbench.setItem(i, inv.getItem(i));
        }

        player.updateInventory();
    }

    private Inventories() {}
}
