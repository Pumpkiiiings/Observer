package com.observer.paper.api.event;

import com.observer.api.model.KeyAction;
import com.observer.api.model.ObserverKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a player interacts with a keyboard or mouse key monitored by Observer.
 */
public class ObserverKeyEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final ObserverKey key;
    private final KeyAction action;
    private final long timestamp;
    private final boolean shiftDown;
    private final boolean ctrlDown;
    private final boolean altDown;

    public ObserverKeyEvent(Player player, ObserverKey key, KeyAction action, long timestamp, boolean shiftDown, boolean ctrlDown, boolean altDown) {
        // Run synchronously as Bukkit PluginMessageListener is synchronous
        super(false);
        this.player = player;
        this.key = key;
        this.action = action;
        this.timestamp = timestamp;
        this.shiftDown = shiftDown;
        this.ctrlDown = ctrlDown;
        this.altDown = altDown;
    }

    public Player getPlayer() { return player; }
    public ObserverKey getKey() { return key; }
    public KeyAction getAction() { return action; }
    public long getTimestamp() { return timestamp; }
    public boolean isShiftDown() { return shiftDown; }
    public boolean isCtrlDown() { return ctrlDown; }
    public boolean isAltDown() { return altDown; }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
