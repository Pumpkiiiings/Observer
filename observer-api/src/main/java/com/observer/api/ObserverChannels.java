package com.observer.api;

public final class ObserverChannels {
    public static final String NAMESPACE = "observer";
    public static final String HANDSHAKE = "handshake";
    public static final String COMPONENT_CREATE = "component_create";
    public static final String COMPONENT_REMOVE = "component_remove";
    public static final String UPDATE_TEXT_CONTENT = "update_text_content";
    public static final String UPDATE_POSITION = "update_position";
    public static final String OBSERVER_EVENT = "event";
    public static final String ENVIRONMENT_UPDATE = "environment_update";
    public static final String CLEAR_HUD = "clear_hud";
    public static final String RESOURCE_RELOAD = "resource_reload";
    public static final String MENU_OPEN = "menu_open";
    public static final String MENU_ACTION = "menu_action";
    public static final String MENU_CLOSE = "menu_close";
    // Keys
    public static final String OBSERVER_KEYS_SYNC = "keys_sync";
    public static final String OBSERVER_KEYS_ACTION = "keys_action";
    public static final String OBSERVER_KEYS_UPDATE = "keys_update";
    public static final String SCREEN_EFFECT = "screen_effect";
    public static final String PLAYER_ACTION = "player_action";

    private ObserverChannels() {}

    public static String channel(String path) {
        return NAMESPACE + ":" + path;
    }

    @SuppressWarnings("unchecked")
    public static <T extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<T> createType(String path) {
        try {
            Class<?> idClass;
            try {
                idClass = Class.forName("net.minecraft.resources.ResourceLocation");
            } catch (ClassNotFoundException e) {
                idClass = Class.forName("net.minecraft.resources.Identifier"); // Note: in Fabric Loom 1.21 Yarn it might be net.minecraft.util.Identifier
                if (idClass == null) {
                    throw new RuntimeException("Could not find ResourceLocation or Identifier class");
                }
            }
            Object idObj = idClass.getMethod("fromNamespaceAndPath", String.class, String.class).invoke(null, NAMESPACE, path);
            java.lang.reflect.Constructor<?> ctor = net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type.class.getConstructor(idClass);
            return (net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<T>) ctor.newInstance(idObj);
        } catch (Exception e) {
            // Attempt fallback to net.minecraft.util.Identifier for pure Yarn
            try {
                Class<?> idClass = Class.forName("net.minecraft.util.Identifier");
                Object idObj = idClass.getMethod("of", String.class, String.class).invoke(null, NAMESPACE, path);
                java.lang.reflect.Constructor<?> ctor = net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type.class.getConstructor(idClass);
                return (net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<T>) ctor.newInstance(idObj);
            } catch (Exception ex) {
                throw new RuntimeException("Failed to create CustomPacketPayload.Type for " + path, ex);
            }
        }
    }
}
