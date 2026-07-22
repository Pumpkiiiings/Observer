package com.observer.api;

import net.minecraft.resources.Identifier;

public final class ObserverChannels {
    public static final String NAMESPACE = "observer";
    public static final Identifier HANDSHAKE = Identifier.fromNamespaceAndPath(NAMESPACE, "handshake");
    public static final Identifier COMPONENT_CREATE = Identifier.fromNamespaceAndPath(NAMESPACE, "component_create");
    public static final Identifier COMPONENT_REMOVE = Identifier.fromNamespaceAndPath(NAMESPACE, "component_remove");
    public static final Identifier UPDATE_TEXT_CONTENT = Identifier.fromNamespaceAndPath(NAMESPACE, "update_text_content");
    public static final Identifier UPDATE_POSITION = Identifier.fromNamespaceAndPath(NAMESPACE, "update_position");
    public static final Identifier OBSERVER_EVENT = Identifier.fromNamespaceAndPath(NAMESPACE, "event");
    public static final Identifier ENVIRONMENT_UPDATE = Identifier.fromNamespaceAndPath(NAMESPACE, "environment_update");
    public static final Identifier CLEAR_HUD = Identifier.fromNamespaceAndPath(NAMESPACE, "clear_hud");
    public static final Identifier RESOURCE_RELOAD = Identifier.fromNamespaceAndPath(NAMESPACE, "resource_reload");
    public static final Identifier MENU_OPEN = Identifier.fromNamespaceAndPath(NAMESPACE, "menu_open");
    public static final Identifier MENU_ACTION = Identifier.fromNamespaceAndPath(NAMESPACE, "menu_action");
    public static final Identifier MENU_CLOSE = Identifier.fromNamespaceAndPath(NAMESPACE, "menu_close");
    // Keys
    public static final Identifier OBSERVER_KEYS_SYNC = Identifier.fromNamespaceAndPath(NAMESPACE, "keys_sync");
    public static final Identifier OBSERVER_KEYS_ACTION = Identifier.fromNamespaceAndPath(NAMESPACE, "keys_action");
    public static final Identifier OBSERVER_KEYS_UPDATE = Identifier.fromNamespaceAndPath(NAMESPACE, "keys_update");
    
    private ObserverChannels() {}
}
