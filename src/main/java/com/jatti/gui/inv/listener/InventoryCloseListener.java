package com.jatti.gui.inv.listener;

import com.jatti.gui.Inventories;
import com.jatti.gui.inv.InventoryImpl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryCloseListener implements Listener {

    @EventHandler
    public void onClose(InventoryClickEvent event) {

        Inventories.getInventoryList().parallelStream().filter(inv -> inv.getInventory().getTitle().equals(event.getInventory().getTitle())).forEach(InventoryImpl::stopAnimations);

    }
}
