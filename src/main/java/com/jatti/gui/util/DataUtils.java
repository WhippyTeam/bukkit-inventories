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
import com.jatti.gui.basic.Inv;
import com.jatti.gui.exception.InventoryActionException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataUtils {

    public static Inventory handleInventoryAnnotation(com.jatti.gui.annotation.Inventory inventoryAnnotation) {
        InventoryType type = inventoryAnnotation.inventoryType();
        String name = color(inventoryAnnotation.name());

        if (type == InventoryType.CHEST) {
            return Bukkit.createInventory(null, inventoryAnnotation.size(), name);
        } else {
            return Bukkit.createInventory(null, type, name);
        }
    }

    public static void handleItemAnnotation(Item item, String name, Inventory inventory, Class<?> clazz) {

        ItemStack itemStack = null;

        if (!item.item().isEmpty()) {

            for (Method method : clazz.getDeclaredMethods()) {

                if (method.getReturnType() == ItemStack.class) {

                    if (method.getName().equals(item.item())) {
                        itemStack = (ItemStack) method.getDefaultValue();
                        break;
                    }

                }

            }

        } else {

            itemStack = DataUtils.getItem(item.material(), item.amount(), item.type(), item.name(), item.lore(),
                    item.forceEmptyName(), item.forceEmptyLore());

        }

        inventory.setItem(item.slot(), itemStack);
        String action = item.action();
        Inv inv = Inv.getInv(name);

        if (!action.isEmpty()) {

            try {
                inv.getItemActions().put(item.slot(), clazz.getMethod(action, InventoryClickEvent.class));
            } catch (NoSuchMethodException | SecurityException e) {

                throw new InventoryActionException("The \"" + action + "\" method was not found or has incompatible parameters!");
            }

        }

        inv.getItemClickable().put(item.slot(), item.clickable());
    }

    public static void handleFillAnnotation(Fill fill, String name, Inventory inventory) {
        int firstEmpty;

        Inv inv = Inv.getInv(name);

        for (int i = 0; i < inventory.getSize(); i++) {

            if (inventory.getItem(i) == null) {
                inventory.setItem(i, DataUtils.getItem(fill.material(), fill.amount(), fill.type(), fill.name(), fill.lore(),
                        fill.forceEmptyName(), fill.forceEmptyLore()));
                inv.getItemClickable().put(i, fill.clickable());
            }

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
