package com.jatti.gui.basic;

import com.jatti.gui.Inventories;
import com.jatti.gui.exception.InventoryAnimationException;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Inv {

    private String name;
    private Inventory inventory;
    private Map<Integer, Method> itemActions;
    private Map<Integer, Boolean> itemClickable;
    private Map<Integer, AnimationType> animations;

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

    public void setName(String name) {
        this.name = name;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Map<Integer, Method> getItemActions() {

        if (itemActions == null) {
            return new HashMap<>();
        }

        return itemActions;
    }

    public void setItemActions(Map<Integer, Method> itemActions) {
        this.itemActions = itemActions;
    }

    public Map<Integer, Boolean> getItemClickable() {

        if (itemClickable == null) {
            return new HashMap<>();
        }

        return itemClickable;
    }

    public void setItemClickable(Map<Integer, Boolean> itemClickable) {
        this.itemClickable = itemClickable;
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

    //TODO for kamilkime hehe
    public void animate(int row) {

        if (inventory.getSize() < row * 9 && row != 0) {
            throw new InventoryAnimationException("Inventory size was to small to have given row in it!");
        }

        AnimationType animationType = animations.get(row);

        if (row > 0) {
            int rowStart = getRowRange(row)[0];
            int rowEnd = getRowRange(row)[1];
            Map<Integer, ItemStack> items = new HashMap<>();

            for (int i = rowStart; i < rowEnd; i++) {
                items.put(i, inventory.getItem(i));
                inventory.clear(i);
            }

            switch (animationType) {

                case SWIPE_LEFT:
                    break;

                case SWIPE_RIGHT:
                    break;

                case SWIPE_LOOP:
                    break;

            }
        }
    }

    private int[] getRowRange(int row) {
        return new int[]{(row - 1) * 9, row * 9 - 1};
    }
}
