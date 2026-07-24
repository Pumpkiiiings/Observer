package com.observer.fabric.animation;

import com.observer.api.ObserverChannels;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationFactory;
import com.zigythebird.playeranimcore.enums.PlayState;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.Identifier;

public class ObserverAnimationManager {

    public static final Identifier ANIMATION_LAYER_ID = Identifier.fromNamespaceAndPath(ObserverChannels.NAMESPACE, "animations");

    public static void initialize() {
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(ANIMATION_LAYER_ID, 1500,
                player -> new PlayerAnimationController(player,
                        (controller, state, animSetter) -> PlayState.STOP
                )
        );
    }

    public static void loadDynamicAnimations(java.util.Map<String, String> animations) {
        for (java.util.Map.Entry<String, String> entry : animations.entrySet()) {
            try {
                String animationName = entry.getKey();
                String jsonString = entry.getValue();
                
                com.google.gson.JsonObject jsonObject = com.zigythebird.playeranimcore.PlayerAnimLib.GSON.fromJson(jsonString, com.google.gson.JsonObject.class);
                java.util.Map<String, com.zigythebird.playeranimcore.animation.Animation> loadedAnims = 
                        com.zigythebird.playeranimcore.loading.UniversalAnimLoader.loadAnimations(jsonObject);
                
                for (java.util.Map.Entry<String, com.zigythebird.playeranimcore.animation.Animation> animEntry : loadedAnims.entrySet()) {
                    String subName = animEntry.getKey();
                    // If there's only 1 animation in the JSON, register it as the file name. 
                    // Otherwise register it as fileName_subName (e.g. walk_animation.model.walk)
                    String finalName = loadedAnims.size() == 1 ? animationName : animationName + "_" + subName.replace("animation.model.", "");
                    
                    String namespace = com.observer.api.ObserverChannels.NAMESPACE.toLowerCase();
                    String path = finalName.toLowerCase();
                    if (finalName.contains(":")) {
                        String[] parts = finalName.split(":");
                        namespace = parts[0].toLowerCase();
                        path = parts[1].toLowerCase();
                    }
                    
                    net.minecraft.resources.Identifier id = net.minecraft.resources.Identifier.fromNamespaceAndPath(namespace, path);
                    
                    try {
                        com.zigythebird.playeranim.animation.PlayerAnimResources.getAnimations().put(id, animEntry.getValue());
                    } catch (UnsupportedOperationException e) {
                        // If map is unmodifiable, try reflection to get the backing field
                        for (java.lang.reflect.Field field : com.zigythebird.playeranim.animation.PlayerAnimResources.class.getDeclaredFields()) {
                            if (java.util.Map.class.isAssignableFrom(field.getType())) {
                                field.setAccessible(true);
                                java.util.Map map = (java.util.Map) field.get(null);
                                if (map != null) {
                                    map.put(id, animEntry.getValue());
                                }
                            }
                        }
                    }
                    System.out.println("[Observer] Loaded dynamic animation: " + id.toString());
                }
            } catch (Exception e) {
                System.err.println("[Observer] Failed to load dynamic animation: " + entry.getKey());
                e.printStackTrace();
            }
        }
    }

    public static void playAnimation(Player player, String animationName) {
        if (!(player instanceof AbstractClientPlayer)) return;
        
        try {
            PlayerAnimationController controller = (PlayerAnimationController) PlayerAnimationAccess.getPlayerAnimationLayer(
                    (AbstractClientPlayer) player, ANIMATION_LAYER_ID);
            
            if (controller != null) {
                String namespace = ObserverChannels.NAMESPACE.toLowerCase();
                String path = animationName.toLowerCase();
                if (animationName.contains(":")) {
                    String[] parts = animationName.toLowerCase().split(":");
                    namespace = parts[0];
                    path = parts[1];
                }
                
                controller.triggerAnimation(Identifier.fromNamespaceAndPath(namespace, path));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopAnimation(Player player) {
        if (!(player instanceof AbstractClientPlayer)) return;
        
        try {
            PlayerAnimationController controller = (PlayerAnimationController) PlayerAnimationAccess.getPlayerAnimationLayer(
                    (AbstractClientPlayer) player, ANIMATION_LAYER_ID);
            
            if (controller != null) {
                controller.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
