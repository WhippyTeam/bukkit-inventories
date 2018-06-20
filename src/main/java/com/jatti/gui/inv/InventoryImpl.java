package com.jatti.gui.inv;

import com.jatti.gui.Inventories;
import com.jatti.gui.animation.AbstractAnimation;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InventoryImpl {

    private final String name;
    private final Inventory inventory;

    private final Map<Integer, Method> itemAction = new HashMap<>();
    private final Map<Integer, Boolean> itemClickability = new HashMap<>();
    private final Set<AbstractAnimation> animations = new HashSet<>();

    public InventoryImpl(String name, Inventory inventory) {
        this.name = name;
        this.inventory = inventory;
        Inventories.addInventory(this);
    }

    public static InventoryImpl get(String name) {
        for (InventoryImpl inv : Inventories.getInventoryList()) {
            if (inv.getName().equals(name)) {
                return inv;
            }
        }

        return null;
    }

    public String getName() {
        return this.name;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public Method getActionForItem(int slot) {
        return this.itemAction.get(slot);
    }

    public void addActionForItem(int slot, Method action) {
        this.itemAction.put(slot, action);
    }

    public boolean isItemClickable(int slot) {
        return this.itemClickability.getOrDefault(slot, false);
    }

    public void setItemClickability(int slot, boolean clickability) {
        this.itemClickability.put(slot, clickability);
    }

    public void addAnimation(AbstractAnimation animation) {
        animations.add(animation);
    }

    public void animate() {
        /**  Inventory inventory = Bukkit.createInventory(null, this.inventory.getSize(), this.inventory.getTitle());
         inventory.addItem(this.inventory.getContents());
         animations.forEach(animation -> animation.start(inventory));**/
    }

}
