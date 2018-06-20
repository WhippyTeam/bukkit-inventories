package com.jatti.gui.trade;

import com.jatti.gui.Inventories;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VillagerTrade {

    private final UUID uuid;
    private final List<MerchantRecipe> trades = new ArrayList<>();

    public VillagerTrade(UUID uuid) {
        this.uuid = uuid;
        Inventories.addTrade(this);
    }

    public static VillagerTrade get(UUID uuid) {
        for (VillagerTrade trade : Inventories.getTradeList()) {
            if (trade.getUUID().equals(uuid)) {
                return trade;
            }
        }

        return null;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public List<MerchantRecipe> getTrades() {
        return new ArrayList<>(this.trades);
    }

    public void addTrade(MerchantRecipe trade) {
        this.trades.add(trade);
    }

}
