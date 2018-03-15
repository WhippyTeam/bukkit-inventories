package com.jatti.gui.basic;

import com.jatti.gui.Inventories;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Trade {

    private UUID uuid;
    private List<MerchantRecipe> trades = new ArrayList<>();

    public Trade() {
    }

    public Trade(UUID uuid) {
        this.uuid = uuid;
        Inventories.addTrade(this);
    }

    public static Trade getTrade(UUID uuid) {

        for (Trade trade : Inventories.getTradeList()) {

            if (trade.getUUID().equals(uuid)) {
                return trade;
            }
        }

        return new Trade(uuid);
    }

    public static Trade getTrade(String uuid) {

        for (Trade trade : Inventories.getTradeList()) {

            if (trade.getUUID().toString().equals(uuid)) {
                return trade;
            }
        }

        return new Trade(UUID.fromString(uuid));
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public void setUUID(UUID uuid) {
        if (this.uuid == null) {
            this.uuid = uuid;
            Inventories.addTrade(this);
        } else {
            this.uuid = uuid;
        }
    }

    public void setUUID(String uuid) {
        if (this.uuid == null) {
            this.uuid = UUID.fromString(uuid);
            Inventories.addTrade(this);
        } else {
            this.uuid = UUID.fromString(uuid);
        }
    }

    public List<MerchantRecipe> getTrades() {

        if (trades == null) {
            return new ArrayList<>();
        }

        return trades;
    }

    public void setTrades(List<MerchantRecipe> trades) {
        this.trades = trades;
    }
}
