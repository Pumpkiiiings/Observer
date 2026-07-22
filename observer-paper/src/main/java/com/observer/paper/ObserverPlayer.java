package com.observer.paper;

import com.observer.api.ObserverFeature;
import org.bukkit.entity.Player;

import java.util.EnumSet;

public class ObserverPlayer {
    private final Player bukkitPlayer;
    private final int protocolVersion;
    private final String observerVersion;
    private final EnumSet<ObserverFeature> supportedFeatures;

    public ObserverPlayer(Player bukkitPlayer, int protocolVersion, String observerVersion, EnumSet<ObserverFeature> supportedFeatures) {
        this.bukkitPlayer = bukkitPlayer;
        this.protocolVersion = protocolVersion;
        this.observerVersion = observerVersion;
        this.supportedFeatures = supportedFeatures;
    }

    public Player getBukkitPlayer() {
        return bukkitPlayer;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public String getObserverVersion() {
        return observerVersion;
    }

    public boolean supports(ObserverFeature feature) {
        return supportedFeatures.contains(feature);
    }
}
