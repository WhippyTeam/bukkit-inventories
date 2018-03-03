package com.jatti.gui.listener;

import com.jatti.gui.Inventories;
import com.jatti.gui.exception.InventoryActionException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

public class InventoryItemActionListener implements Listener {
    
    @EventHandler
    public void onClick(InventoryClickEvent event) {
       String inventoryName = "";
       
       for (Entry<String, Inventory> invEntry : Inventories.getInventoryMap().entrySet()) {
           if (invEntry.getValue().equals(event.getClickedInventory())) {
               inventoryName = invEntry.getKey();
               break;
           }
       }
       
       if (inventoryName.isEmpty()) {
           return;
       }
       
       Map<Integer, Method> actionMap = Inventories.getActionMap().get(inventoryName);
       if (actionMap == null) {
           return;
       }
       
       Method action = actionMap.get(event.getSlot());
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