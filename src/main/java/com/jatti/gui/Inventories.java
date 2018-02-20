/*
   MIT License

   Copyright (c) 2018 Bartlomiej Stefanski

   Permission is hereby granted, free of charge, to any person obtaining a copy
   of this software and associated documentation files (the "Software"), to deal
   in the Software without restriction, including without limitation the rights
   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
   copies of the Software, and to permit persons to whom the Software is
   furnished to do so, subject to the following conditions:

   The above copyright notice and this permission notice shall be included in all
   copies or substantial portions of the Software.

   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
   SOFTWARE.
*/

package com.jatti.gui;

import com.jatti.gui.annotation.Fill;
import com.jatti.gui.annotation.Item;
import com.jatti.gui.annotation.Items;
import com.jatti.gui.exception.InventoryParseException;
import com.jatti.gui.utils.DataUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Inventories {

    private static final Map<String, Inventory> INVENTORY_MAP = new HashMap<>();

    public static void register(Class<?> clazz, String name) {
        if (clazz.getDeclaredMethods().length != 0) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getDeclaredAnnotations().length != 0) {
                    
                    Inventory inventory = Bukkit.createInventory(null, 9, "");
                    for (Annotation annotation : method.getDeclaredAnnotations()) {
                        if (annotation.annotationType() == com.jatti.gui.annotation.Inventory.class) {
                            com.jatti.gui.annotation.Inventory inventoryAnnotation = (com.jatti.gui.annotation.Inventory) annotation;
                            
                            if (inventoryAnnotation.inventoryType() == InventoryType.CHEST || inventoryAnnotation.inventoryType() == InventoryType.ENDER_CHEST) {
                                inventory = Bukkit.createInventory(null, inventoryAnnotation.size(), ChatColor.translateAlternateColorCodes('&', inventoryAnnotation.name()));
                            } else {
                                inventory = Bukkit.createInventory(null, inventoryAnnotation.inventoryType(), ChatColor.translateAlternateColorCodes('&', inventoryAnnotation.name()));
                            }
                        }

                        if (annotation.annotationType() == Items.class) {
                            for (Item item : ((Items) annotation).value()) {
                                inventory.setItem(item.slot(), DataUtils.getItem(item.material(), item.amount(), item.type(), item.name(), item.lore()));
                            }
                        }

                        if (annotation.annotationType() == Fill.class) {
                            Fill fill = (Fill) annotation;

                            ItemStack itemStack = DataUtils.getItem(fill.material(), fill.amount(), fill.type(), fill.name(), fill.lore());
                            for (int i = 0; i < inventory.getSize(); i++) {
                                if (inventory.getItem(i) == null) {
                                    inventory.setItem(i, itemStack);
                                }
                            }

                        }

                    }

                    INVENTORY_MAP.put(name, inventory);
                }
            }
        } else {
            throw new InventoryParseException("Class does not have any methods!");
        }
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
        for (int i=9; i >= 0; i--) {
            workbench.setItem(i, inv.getItem(i));
        }
        
        player.updateInventory();
    }

    public static Map<String, Inventory> getInventoryMap() {
        return new HashMap<>(INVENTORY_MAP);
    }
}
