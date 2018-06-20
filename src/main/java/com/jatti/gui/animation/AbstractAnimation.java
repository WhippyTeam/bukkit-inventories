package com.jatti.gui.animation;

import com.jatti.gui.AnimationType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class AbstractAnimation {

    private final Plugin plugin;
    private final AnimationType type;
    private final long delay;
    private final long interval;
    private final List<Integer> slots;

    protected BukkitTask animationTask;

    public AbstractAnimation(Plugin plugin, AnimationType type, long delay, long interval) {
        this.plugin = plugin;
        this.type = type;
        this.delay = delay;
        this.interval = interval;
        this.slots = new ArrayList<>();
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public AnimationType getType() {
        return this.type;
    }

    public long getDelay() {
        return this.delay;
    }

    public long getInterval() {
        return this.interval;
    }

    public List<Integer> getSlots() {
        return new ArrayList<>(this.slots);
    }

    public void addSlot(int slot) {
        slots.add(slot);
    }

    public boolean isRunning() {
        return this.animationTask != null && !this.animationTask.isCancelled();
    }

    public void stop() {
        this.animationTask.cancel();
    }

    public abstract void start(Inventory targetInventory);

    public abstract Set<Integer> getInvolvedSlots();

}
