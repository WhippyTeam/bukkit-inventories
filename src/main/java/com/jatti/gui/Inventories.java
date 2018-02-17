/*
MIT License

Copyright (c) 2018 Bartłomiej Stefański

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
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Inventories {

    private static final Map<String, Inventory> INVENTORY_MAP = new HashMap<>();

    public static void openInventory(String name, Player player) {
        player.openInventory(INVENTORY_MAP.get(name));
    }

    public static void register(Class clazz, String name) {
        Inventory inventory = Bukkit.createInventory(null, 9, "");

        if (clazz.getDeclaredMethods().length != 0) {

            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getDeclaredAnnotations().length != 0) {

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
                            Items itemsAnnotation = (Items) annotation;

                            for (Item item : itemsAnnotation.value()) {
                                ItemStack itemStack = new ItemStack(item.material(), item.amount(), item.type());
                                ItemMeta itemMeta = itemStack.getItemMeta();
                                itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', item.name()));

                                String lore = ChatColor.translateAlternateColorCodes('&', StringUtils.replaceEach(
                                        Arrays.toString(item.lore()),
                                        new String[]{"[", "]"},
                                        new String[]{"", ""}
                                ));

                                itemMeta.setLore(Arrays.asList(lore));
                                itemStack.setItemMeta(itemMeta);

                                inventory.setItem(item.slot(), itemStack);
                            }
                        }

                        if (annotation.annotationType() == Fill.class) {
                            Fill fillAnnotation = (Fill) annotation;

                            ItemStack itemStack = new ItemStack(fillAnnotation.material(), fillAnnotation.amount(), fillAnnotation.type());
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', fillAnnotation.name()));

                            String lore = ChatColor.translateAlternateColorCodes('&', StringUtils.replaceEach(
                                    Arrays.toString(fillAnnotation.lore()),
                                    new String[]{"[", "]"},
                                    new String[]{"", ""}
                            ));

                            itemMeta.setLore(Arrays.asList(lore));
                            itemStack.setItemMeta(itemMeta);

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
            throw new InventoryParseException("Class does not have any method.");
        }
    }

    public static Inventory getInventory(String inventoryName) {
        return INVENTORY_MAP.get(inventoryName);
    }

    public static Map<String, Inventory> getInventoryMap() {
        return new HashMap<>(INVENTORY_MAP);
    }
}
