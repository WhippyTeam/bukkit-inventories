package com.jatti.gui.listener;

import com.jatti.gui.Inventories;
import com.jatti.gui.basic.Trade;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.UUID;

public class VillagerClickListener implements Listener {

    @EventHandler
    public void onEntityClick(PlayerInteractAtEntityEvent event) {

        if (event.getRightClicked().getType() == EntityType.VILLAGER) {

            Villager villager = (Villager) event.getRightClicked();
            UUID uuid = villager.getUniqueId();

            for (Trade trade : Inventories.getTradeList()) {

                if (trade.getUUID().equals(uuid)) {

                    villager.setRecipes(trade.getTrades());
                }
            }
        }

    }
}
