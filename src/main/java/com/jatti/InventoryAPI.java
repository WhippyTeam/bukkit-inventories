package com.jatti;

import com.jatti.inventory.InventoryContainer;
import com.jatti.inventory.example.ExampleInventory;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class InventoryAPI extends JavaPlugin {

    public void onEnable(){
        InventoryContainer.register(ExampleInventory.class, "example");
        Bukkit.getPluginManager().registerEvents(new ExampleInventory(), this);
    }

}
