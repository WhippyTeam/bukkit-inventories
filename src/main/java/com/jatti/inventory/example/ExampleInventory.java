package com.jatti.inventory.example;

import com.jatti.inventory.InventoryContainer;
import com.jatti.inventory.basic.Inv;
import com.jatti.inventory.basic.Item;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ExampleInventory implements Listener{

    @Inv(name = "&6Example Inventory", size = 9)
    @Item(material = Material.ANVIL, amount = 1, name = "&6Example anvil", lore = {"This is anvil!"}, slot = 3)
    @Item(material = Material.GOLD_AXE, amount = 1, name = "&6Example axe", lore = {"This is axe!"}, slot = 7)
    public void open(Player player){
        InventoryContainer.openInventory("example", player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        open(event.getPlayer());
    }
}
