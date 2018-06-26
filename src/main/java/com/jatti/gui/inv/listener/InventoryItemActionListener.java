package com.jatti.gui.inv.listener;

import com.jatti.gui.inv.exception.InventoryActionException;
import com.jatti.gui.Inventories;
import com.jatti.gui.inv.InventoryImpl;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InventoryItemActionListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        String inventoryName = "";
        for (InventoryImpl inv : Inventories.getInventoryList()) {
            if (inv.getInventory().equals(event.getClickedInventory())) {
                inventoryName = inv.getName();
                break;
            }
        }

        if (inventoryName.isEmpty()) {
            return;
        }

        InventoryImpl inv = InventoryImpl.get(inventoryName);
        Method action = inv.getActionForItem(event.getSlot());

        if (!inv.isItemClickable(event.getSlot())) {
            event.setResult(Result.DENY);
        }

        if (action != null) {
            try {
                action.invoke(action.getDeclaringClass().newInstance(), event);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
                throw new InventoryActionException("Unable to run action method for inventory \"" + inventoryName + "\" and slot " + event.getSlot());
            }
        }
    }

}
