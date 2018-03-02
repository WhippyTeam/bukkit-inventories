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

package com.jatti.gui.util;

import com.jatti.gui.annotation.Fill;
import com.jatti.gui.annotation.Item;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataUtils {

    public static Inventory handleInventoryAnnotation(com.jatti.gui.annotation.Inventory inventoryAnnotation) {
        InventoryType type = inventoryAnnotation.inventoryType();
        String name = color(inventoryAnnotation.name());

        if (type == InventoryType.CHEST || type == InventoryType.ENDER_CHEST) {
            return Bukkit.createInventory(null, inventoryAnnotation.size(), name);
        } else {
            return Bukkit.createInventory(null, type, name);
        }
    }

    public static void handleItemAnnotation(Item item, Inventory inventory) {
        inventory.setItem(item.slot(), DataUtils.getItem(item.material(), item.amount(), item.type(), item.name(), item.lore(),
                        item.forceEmptyName(), item.forceEmptyLore()));
    }
    
    public static void handleFillAnnotation(Fill fill, Inventory inventory) {
        int firstEmpty = 0;
        
        while ((firstEmpty = inventory.firstEmpty()) > 0) {
            inventory.setItem(firstEmpty, DataUtils.getItem(fill.material(), fill.amount(), fill.type(), fill.name(), fill.lore(),
                            fill.forceEmptyName(), fill.forceEmptyLore()));
        }
    }

    public static ItemStack getItem(Material type, int amount, short data, String name, String[] lore, boolean forceEmptyName, boolean forceEmptyLore) {
        ItemStack itemStack = new ItemStack(type, amount, data);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (!name.isEmpty() || forceEmptyName) {
            itemMeta.setDisplayName(color(name));
        }

        if (lore.length > 1 || (lore.length == 1 && !lore[0].isEmpty()) || forceEmptyLore) {
            itemMeta.setLore(colorList(Arrays.asList(lore)));
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static String color(String toColor) {
        return ChatColor.translateAlternateColorCodes('&', toColor);
    }

    public static List<String> colorList(List<String> toColor) {
        List<String> colored = new ArrayList<>();
        for (String s : toColor) {
            colored.add(color(s));
        }

        return colored;
    }

    private DataUtils() {}
}
