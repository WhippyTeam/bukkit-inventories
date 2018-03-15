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

import com.jatti.gui.annotation.*;
import com.jatti.gui.basic.Inv;
import com.jatti.gui.basic.Trade;
import com.jatti.gui.exception.InventoryParseException;
import com.jatti.gui.exception.VillagerTradeParseException;
import com.jatti.gui.listener.InventoryItemActionListener;
import com.jatti.gui.listener.VillagerClickListener;
import com.jatti.gui.util.DataUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.Plugin;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Inventories {

    private static final List<Inv> INV_LIST = new ArrayList<>();
    private static final List<Trade> TRADE_LIST = new ArrayList<>();

    public static void init(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(new InventoryItemActionListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new VillagerClickListener(), plugin);
    }

    public static void registerInventory(Class<?> clazz, String name) {
        if (clazz.getDeclaredMethods().length == 0) {
            throw new InventoryParseException("Class \"" + clazz.getName() + "\" does not have any methods!");
        }

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getDeclaredAnnotations().length == 0) {
                continue;
            }

            Inventory inventory = Bukkit.createInventory(null, 9, "");
            Trade trade = new Trade();

            for (Annotation annotation : method.getDeclaredAnnotations()) {
                if (annotation.annotationType() == com.jatti.gui.annotation.Inventory.class) {
                    inventory = DataUtils.handleInventoryAnnotation((com.jatti.gui.annotation.Inventory) annotation);

                }
                
                else if (annotation.annotationType() == Item.class) {
                    DataUtils.handleItemAnnotation((Item) annotation, name, inventory, clazz);
                }
                
                else if (annotation.annotationType() == Items.class) {
                    for (Item item : ((Items) annotation).value()) {
                        DataUtils.handleItemAnnotation(item, name, inventory, clazz);
                    }
                }
                
                else if (annotation.annotationType() == Fill.class) {
                    DataUtils.handleFillAnnotation((Fill) annotation, name, inventory);
                } else if (annotation.annotationType() == VillagerTrade.class) {
                    VillagerTrade villagerTrade = (VillagerTrade) annotation;
                    trade.setUUID(villagerTrade.villagerUUID());
                } else if (annotation.annotationType() == TradeItem.class) {
                    TradeItem tradeItem = (TradeItem) annotation;
                    String firstTradeCost = tradeItem.firstTradeCost();
                    String secondTradeCost = tradeItem.secondTradeCost();
                    ItemStack tradeResult = null;
                    ItemStack firstCost = null;
                    ItemStack secondCost = null;

                    if (tradeItem.tradeResult().isEmpty()) {
                        throw new VillagerTradeParseException("Trade result can not be empty!");
                    }

                    if (firstTradeCost.isEmpty() && (!secondTradeCost.isEmpty())) {
                        firstTradeCost = secondTradeCost;
                        secondTradeCost = "";
                    }

                    if (firstTradeCost.isEmpty() && secondTradeCost.isEmpty()) {
                        throw new VillagerTradeParseException("At least one trade cost can not be empty!");
                    }


                    for (Method itemMethod : clazz.getDeclaredMethods()) {

                        if (itemMethod.getReturnType() == ItemStack.class) {

                            if (itemMethod.getName().equals(tradeItem.tradeResult())) {
                                tradeResult = (ItemStack) itemMethod.getDefaultValue();
                                continue;
                            }

                            if (itemMethod.getName().equals(firstTradeCost)) {
                                firstCost = (ItemStack) itemMethod.getDefaultValue();
                                continue;
                            }

                            if (itemMethod.getName().equals(secondTradeCost)) {
                                secondCost = (ItemStack) itemMethod.getDefaultValue();
                            }
                        }
                    }

                    if (tradeResult != null) {

                        MerchantRecipe merchantRecipe = new MerchantRecipe(tradeResult, tradeItem.maxUses());

                        if (firstCost != null) {
                            merchantRecipe.addIngredient(firstCost);
                        }

                        if (secondCost != null) {
                            merchantRecipe.addIngredient(secondCost);
                        }

                        trade.getTrades().add(merchantRecipe);


                    }

                } else if (annotation.annotationType() == TradeItems.class) {

                    TradeItems items = (TradeItems) annotation;

                    for (TradeItem tradeItem : items.value()) {

                        String firstTradeCost = tradeItem.firstTradeCost();
                        String secondTradeCost = tradeItem.secondTradeCost();
                        ItemStack tradeResult = null;
                        ItemStack firstCost = null;
                        ItemStack secondCost = null;

                        if (tradeItem.tradeResult().isEmpty()) {
                            throw new VillagerTradeParseException("Trade result can not be empty!");
                        }

                        if (firstTradeCost.isEmpty() && (!secondTradeCost.isEmpty())) {
                            firstTradeCost = secondTradeCost;
                            secondTradeCost = "";
                        }

                        if (firstTradeCost.isEmpty() && secondTradeCost.isEmpty()) {
                            throw new VillagerTradeParseException("At least one trade cost can not be empty!");
                        }


                        for (Method itemMethod : clazz.getDeclaredMethods()) {

                            if (itemMethod.getReturnType() == ItemStack.class) {

                                if (itemMethod.getName().equals(tradeItem.tradeResult())) {
                                    tradeResult = (ItemStack) itemMethod.getDefaultValue();
                                    continue;
                                }

                                if (itemMethod.getName().equals(firstTradeCost)) {
                                    firstCost = (ItemStack) itemMethod.getDefaultValue();
                                    continue;
                                }

                                if (itemMethod.getName().equals(secondTradeCost)) {
                                    secondCost = (ItemStack) itemMethod.getDefaultValue();
                                }
                            }
                        }

                        if (tradeResult != null) {

                            MerchantRecipe merchantRecipe = new MerchantRecipe(tradeResult, tradeItem.maxUses());

                            if (firstCost != null) {
                                merchantRecipe.addIngredient(firstCost);
                            }

                            if (secondCost != null) {
                                merchantRecipe.addIngredient(secondCost);
                            }

                            trade.getTrades().add(merchantRecipe);

                        }

                    }

                }
            }

            if (clazz.isAnnotationPresent(com.jatti.gui.annotation.Inventory.class)) {
                Inv inv = Inv.getInv(name);
                inv.setInventory(inventory);
            } else {
                Inv inv = Inv.getInv(name);
                removeInventory(inv);
            }

        }
    }

    public static void registerVillagerTrade(Class<?> clazz) {

    }

    public static List<Trade> getTradeList() {
        return new ArrayList<>(TRADE_LIST);
    }

    public static void addTrade(Trade trade) {
        if (!TRADE_LIST.contains(trade)) {
            TRADE_LIST.add(trade);
        }
    }

    public static void removeTrade(Trade trade) {
        if (TRADE_LIST.contains(trade)) {
            TRADE_LIST.remove(trade);
        }
    }

    public static List<Inv> getInventoryList() {
        return new ArrayList<>(INV_LIST);
    }

    public static void addInventory(Inv inventory) {
        if (!INV_LIST.contains(inventory)) {
            INV_LIST.add(inventory);
        }
    }

    public static void removeInventory(Inv inventory) {
        if (INV_LIST.contains(inventory)) {
            INV_LIST.remove(inventory);
        }
    }

    public static Inventory getInventory(String name) {
        for (Inv inv : getInventoryList()) {

            if (inv.getName().equals(name)) {
                return inv.getInventory();
            }

        }
        return null;
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
