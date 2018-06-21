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

import com.jatti.gui.animation.annotation.Animation;
import com.jatti.gui.inv.InventoryImpl;
import com.jatti.gui.inv.annotation.*;
import com.jatti.gui.inv.exception.InventoryParseException;
import com.jatti.gui.inv.listener.InventoryCloseListener;
import com.jatti.gui.inv.listener.InventoryItemActionListener;
import com.jatti.gui.trade.VillagerTrade;
import com.jatti.gui.trade.annotation.Trade;
import com.jatti.gui.trade.annotation.TradeItem;
import com.jatti.gui.trade.annotation.TradeItems;
import com.jatti.gui.trade.listener.VillagerClickListener;
import com.jatti.gui.util.AnnotationUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;

import java.lang.annotation.Annotation;
import java.util.*;

public final class Inventories {

    private static final Set<InventoryImpl> INV_LIST = new HashSet<>();
    private static final Set<VillagerTrade> TRADE_LIST = new HashSet<>();
    private static Plugin plugin;

    public static void init(Plugin pl) {
        plugin = pl;
        Bukkit.getPluginManager().registerEvents(new InventoryItemActionListener(), pl);
        Bukkit.getPluginManager().registerEvents(new VillagerClickListener(), pl);
        Bukkit.getPluginManager().registerEvents(new InventoryCloseListener(), pl);
    }

    public static void registerInventory(Class<?> clazz, String name) {
        Annotation[] annotations = clazz.getDeclaredAnnotations();
        if (annotations.length == 0) {
            throw new InventoryParseException("Class \"" + clazz.getName() + "\" does not have any annotations!");
        }

        if (!clazz.isAnnotationPresent(Inventory.class)) {
            throw new InventoryParseException("Class \"" + clazz.getName() + "\" does not have an Inventory annotation!");
        }

        InventoryImpl inventory = null;
        for (Annotation annotation : annotations) {

            switch (annotation.annotationType().getSimpleName()) {

                case "Inventory":
                    inventory = AnnotationUtils.handleInventoryAnnotation((Inventory) annotation, name);
                    break;

                case "Item":
                    AnnotationUtils.handleItemAnnotation((Item) annotation, inventory, clazz);
                    break;

                case "Items":
                    for (Item item : ((Items) annotation).value()) {
                        AnnotationUtils.handleItemAnnotation(item, inventory, clazz);
                    }
                    break;

                case "Fill":
                    AnnotationUtils.handleFillAnnotation((Fill) annotation, inventory);
                    break;

                case "Animation":
                    AnnotationUtils.handleAnimationAnnotation((Animation) annotation, inventory, plugin);
                    break;

                case "ConfigItem":
                    AnnotationUtils.handleConfigItemAnnotation(plugin.getConfig(), (ConfigItem) annotation, inventory, clazz);
                    break;

                case "ConfigItems":
                    for (ConfigItem item : ((ConfigItems) annotation).value()) {
                        AnnotationUtils.handleConfigItemAnnotation(plugin.getConfig(), item, inventory, clazz);
                    }
                    break;
            }
        }
    }

    public static void registerVillagerTrade(Class<?> clazz) {
        Annotation[] annotations = clazz.getDeclaredAnnotations();
        if (annotations.length == 0) {
            throw new InventoryParseException("Class \"" + clazz.getName() + "\" does not have any annotations!");
        }

        if (!clazz.isAnnotationPresent(Trade.class)) {
            throw new InventoryParseException("Class \"" + clazz.getName() + "\" does not have a VillagerTrade annotation!");
        }

        VillagerTrade villagerTrade = null;
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == Trade.class) {
                villagerTrade = new VillagerTrade(UUID.fromString(((Trade) annotation).villagerUUID()));
            } else if (annotation.annotationType() == TradeItem.class) {
                AnnotationUtils.handleTradeItemAnnotation((TradeItem) annotation, villagerTrade, clazz);
            } else if (annotation.annotationType() == TradeItems.class) {
                for (TradeItem tradeItem : ((TradeItems) annotation).value()) {
                    AnnotationUtils.handleTradeItemAnnotation(tradeItem, villagerTrade, clazz);
                }
            }
        }
    }

    public static List<VillagerTrade> getTradeList() {
        return new ArrayList<>(TRADE_LIST);
    }

    public static void addTrade(VillagerTrade trade) {
        if (!TRADE_LIST.contains(trade)) {
            TRADE_LIST.add(trade);
        }
    }

    public static void removeTrade(VillagerTrade trade) {
        TRADE_LIST.remove(trade);
    }

    public static List<InventoryImpl> getInventoryList() {
        return new ArrayList<>(INV_LIST);
    }

    public static void addInventory(InventoryImpl inventory) {
        if (!INV_LIST.contains(inventory)) {
            INV_LIST.add(inventory);
        }
    }

    public static void removeInventory(InventoryImpl inventory) {
        INV_LIST.remove(inventory);
    }

    public static InventoryImpl getInventory(String name) {
        for (InventoryImpl inv : getInventoryList()) {
            if (inv.getName().equals(name)) {
                return inv;
            }
        }

        return null;
    }

    public static void openInventory(String name, Player player) {
        InventoryImpl inv = getInventory(name);
        if (inv == null) {
            return;
        }

        player.openInventory(inv.getInventory());
        inv.animate();
    }

    public static void openWorkbench(String name, Player player) {
        InventoryImpl inv = getInventory(name);
        if (inv == null) {
            return;
        }

        if (inv.getInventory().getType() != InventoryType.WORKBENCH) {
            player.openInventory(inv.getInventory());
            return;
        }

        InventoryView workbench = player.openWorkbench(null, true);
        for (int i = 9; i >= 0; i--) {
            workbench.setItem(i, inv.getInventory().getItem(i));
        }

        player.updateInventory();
    }

    private Inventories() {}
}
