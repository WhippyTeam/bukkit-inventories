package com.jatti.gui.animation.impl;

import com.jatti.gui.AnimationType;
import com.jatti.gui.animation.AbstractAnimation;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.Map.Entry;

public class DiscoAnimation extends AbstractAnimation {

    public DiscoAnimation(Plugin plugin, AnimationType type, long delay, long interval) {
        super(plugin, type, delay, interval);
    }

    @Override
    public void start(Inventory targetInventory) {
        this.animationTask = new BukkitRunnable() {

            @Override
            public void run() {
                List<Integer> from = new ArrayList<>(getSlots());
                List<Integer> to = new ArrayList<>(getSlots());

                Collections.shuffle(from);
                Collections.shuffle(to);

                Map<Integer, ItemStack> newPositions = new HashMap<>();
                for (int i = 0; i < getSlots().size(); i++) {
                    newPositions.put(to.get(i), targetInventory.getItem(from.get(i)));
                }

                for (Entry<Integer, ItemStack> itemEntry : newPositions.entrySet()) {
                    targetInventory.setItem(itemEntry.getKey(), itemEntry.getValue());
                }
            }
        }.runTaskTimer(getPlugin(), getDelay(), getInterval());
    }

    @Override
    public Set<Integer> getInvolvedSlots() {
        return new HashSet<>(getSlots());
    }

}
