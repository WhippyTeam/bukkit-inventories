package com.jatti.gui.listener;

import com.jatti.gui.Inventories;
import com.jatti.gui.basic.Inv;
import com.jatti.gui.exception.InventoryActionException;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InventoryItemActionListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        String inventoryName = "";

        for (Inv inv : Inventories.getInventoryList()) {

            if (inv.getInventory().equals(event.getClickedInventory())) {
                inventoryName = inv.getName();
                break;
            }

        }

        if (inventoryName.isEmpty()) {
            return;
        }

        Inv inv = Inv.getInv(inventoryName);

        if (!inv.getClickableForItem(event.getSlot())) {
            event.setResult(Event.Result.DENY);
        }

        Method action = inv.getActionForItem(event.getSlot());

        if (action == null) {
            return;
        }

        try {
            action.invoke(action.getDeclaringClass().newInstance(), event);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
            throw new InventoryActionException("Unable to run action method for inventory \"" + inventoryName + "\" and slot " + event.getSlot());
        }
    }
}