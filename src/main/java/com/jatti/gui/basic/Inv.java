package com.jatti.gui.basic;

import com.jatti.gui.Inventories;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Inv {

    private final String name;
    private final Map<Integer, Method> itemActions = new HashMap<>();
    private final Map<Integer, Boolean> itemClickable = new HashMap<>();

    private Inventory inventory;

    public Inv(String name) {
        this.name = name;
        Inventories.addInventory(this);
    }

    public static Inv getInv(String name) {
        for (Inv inv : Inventories.getInventoryList()) {
            if (inv.getName().equals(name)) {
                return inv;
            }
        }

        return new Inv(name);
    }

    public String getName() {
        return name;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Map<Integer, Method> getItemActions() {
        return itemActions;
    }

    public Map<Integer, Boolean> getItemClickable() {
        return itemClickable;
    }

    public Method getActionForItem(int slot) {
        if (itemActions.get(slot) == null) {
            return null;
        }

        return itemActions.get(slot);

    }

    public boolean getClickableForItem(int slot) {
        if (itemClickable.get(slot) == null) {
            return false;
        }

        return itemClickable.get(slot);
    }

}
