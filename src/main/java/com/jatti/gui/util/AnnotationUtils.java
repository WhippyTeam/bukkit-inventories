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

import com.jatti.gui.animation.AbstractAnimation;
import com.jatti.gui.animation.annotation.Animation;
import com.jatti.gui.animation.impl.DiscoAnimation;
import com.jatti.gui.inv.InventoryImpl;
import com.jatti.gui.inv.annotation.ConfigItem;
import com.jatti.gui.inv.annotation.Fill;
import com.jatti.gui.inv.annotation.Inventory;
import com.jatti.gui.inv.annotation.Item;
import com.jatti.gui.inv.exception.InventoryActionException;
import com.jatti.gui.inv.exception.InventoryParseException;
import com.jatti.gui.trade.VillagerTrade;
import com.jatti.gui.trade.annotation.TradeItem;
import com.jatti.gui.trade.exception.VillagerTradeParseException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class AnnotationUtils {

    private AnnotationUtils() {
    }

    public static InventoryImpl handleInventoryAnnotation(Inventory inventoryAnnotation, String name) {
        InventoryType type = inventoryAnnotation.inventoryType();
        String title = color(inventoryAnnotation.title());

        if (type == InventoryType.CHEST) {
            return new InventoryImpl(name, Bukkit.createInventory(null, inventoryAnnotation.size(), title));
        } else {
            return new InventoryImpl(name, Bukkit.createInventory(null, type, title));
        }
    }

    public static void handleItemAnnotation(Item item, InventoryImpl inventory, Class<?> clazz) {
        if (inventory == null) {
            throw new InventoryParseException("Inventory annotation must be first, before any Item annotation!");
        }

        ItemStack itemStack = null;
        if (!item.item().isEmpty()) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getReturnType() == ItemStack.class && method.getName().equals(item.item())) {
                    itemStack = (ItemStack) method.getDefaultValue();
                    break;
                }
            }
        } else {
            itemStack = AnnotationUtils.getItem(item.material(), item.amount(), item.type(), item.name(), item.lore(),
                    item.forceEmptyName(), item.forceEmptyLore());
        }

        inventory.getInventory().setItem(item.slot(), itemStack);
        inventory.setItemClickability(item.slot(), item.clickable());

        String action = item.action();
        if (!action.isEmpty()) {
            try {
                inventory.addActionForItem(item.slot(), clazz.getMethod(action, InventoryClickEvent.class));
            } catch (NoSuchMethodException | SecurityException e) {
                throw new InventoryActionException("The \"" + action + "\" method was not found or has incompatible parameters!");
            }
        }
    }

    public static void handleConfigItemAnnotation(FileConfiguration config, ConfigItem itemFromConfig, InventoryImpl inventory, Class<?> clazz) {
        ItemStack itemStack = config.getItemStack(itemFromConfig.value());

        if (itemStack == null) {
            throw new InventoryParseException("There is no \"" + itemFromConfig.value() + "\" value in config!");
        }

        inventory.getInventory().setItem(itemFromConfig.slot(), itemStack);
        inventory.setItemClickability(itemFromConfig.slot(), itemFromConfig.clickable());

        String action = itemFromConfig.action();
        if (!action.isEmpty()) {
            try {
                inventory.addActionForItem(itemFromConfig.slot(), clazz.getMethod(action, InventoryClickEvent.class));
            } catch (NoSuchMethodException | SecurityException e) {
                throw new InventoryActionException("The \"" + action + "\" method was not found or has incompatible parameters!");
            }
        }
    }

    public static void handleFillAnnotation(Fill fill, InventoryImpl inventory) {
        if (inventory == null) {
            throw new InventoryParseException("Inventory annotation must be first, before any Fill annotation!");
        }

        int firstEmpty;
        while ((firstEmpty = inventory.getInventory().firstEmpty()) > 0) {
            inventory.getInventory().setItem(firstEmpty, AnnotationUtils.getItem(fill.material(), fill.amount(), fill.type(),
                    fill.name(), fill.lore(), fill.forceEmptyName(), fill.forceEmptyLore()));

            inventory.setItemClickability(firstEmpty, fill.clickable());
        }
    }

    public static void handleAnimationAnnotation(Animation animation, InventoryImpl inventory, Plugin plugin) {
        if (inventory == null) {
            throw new InventoryParseException("Inventory annotation must be first, before any Animation annotation!");
        }

        if (animation.row() < 0) {
            throw new InventoryParseException("Row number in the Animation annotation can not be negative!");
        }

        if (animation.row() > inventory.getInventory().getSize() / 9) {
            throw new InventoryParseException("Row number in the Animation annotation can not greater than the inventory row count!");
        }

        AbstractAnimation anim = new DiscoAnimation(plugin, animation.animationType(), animation.delay(), animation.interval());
        if (animation.row() == 0) {
            for (int row = 1; row <= inventory.getInventory().getSize() / 9; row++) {
                for (int slot = (row - 1) * 9; slot < row * 9; slot++) {
                    anim.addSlot(slot);
                }
            }
        } else {
            for (int slot = (animation.row() - 1) * 9; slot < animation.row() * 9; slot++) {
                anim.addSlot(slot);
            }
        }

        for (int slot : animation.slots()) {
            anim.addSlot(slot);
        }

        inventory.addAnimation(anim);
    }

    public static void handleTradeItemAnnotation(TradeItem tradeItem, VillagerTrade villagerTrade, Class<?> clazz) {
        if (villagerTrade == null) {
            throw new VillagerTradeParseException("VillagerTrade annotation must be first, before any TradeItem annotation!");
        }

        String firstTradeCost = tradeItem.firstTradeCost();
        String secondTradeCost = tradeItem.secondTradeCost();
        String tradeResult = tradeItem.tradeResult();

        if (tradeResult.isEmpty()) {
            throw new VillagerTradeParseException("Trade result can not be empty!");
        }

        if (firstTradeCost.isEmpty() && !secondTradeCost.isEmpty()) {
            firstTradeCost = secondTradeCost;
            secondTradeCost = "";
        }

        if (firstTradeCost.isEmpty()) {
            throw new VillagerTradeParseException("At least one trade cost must not be empty!");
        }

        ItemStack firstTradeCostItem = null;
        ItemStack secondTradeCostItem = null;
        ItemStack tradeResultItem = null;

        for (Method itemMethod : clazz.getDeclaredMethods()) {
            if (itemMethod.getReturnType() != ItemStack.class) {
                continue;
            }

            if (itemMethod.getName().equals(tradeItem.tradeResult())) {
                tradeResultItem = (ItemStack) itemMethod.getDefaultValue();
            }

            if (itemMethod.getName().equals(firstTradeCost)) {
                firstTradeCostItem = (ItemStack) itemMethod.getDefaultValue();
            }

            if (itemMethod.getName().equals(secondTradeCost)) {
                secondTradeCostItem = (ItemStack) itemMethod.getDefaultValue();
            }
        }

        MerchantRecipe merchantRecipe = new MerchantRecipe(tradeResultItem, tradeItem.maxUses());

        if (firstTradeCostItem != null) {
            merchantRecipe.addIngredient(firstTradeCostItem);
        }

        if (secondTradeCostItem != null) {
            merchantRecipe.addIngredient(secondTradeCostItem);
        }

        villagerTrade.addTrade(merchantRecipe);
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
}
