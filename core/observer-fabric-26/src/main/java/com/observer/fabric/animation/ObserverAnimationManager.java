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

    public static void playAnimation(Player player, String animationName) {
        if (!(player instanceof AbstractClientPlayer)) return;
        
        try {
            PlayerAnimationController controller = (PlayerAnimationController) PlayerAnimationAccess.getPlayerAnimationLayer(
                    (AbstractClientPlayer) player, ANIMATION_LAYER_ID);
            
            if (controller != null) {
                String namespace = ObserverChannels.NAMESPACE;
                String path = animationName;
                if (animationName.contains(":")) {
                    String[] parts = animationName.split(":");
                    namespace = parts[0];
                    path = parts[1];
                }
                
                controller.triggerAnimation(Identifier.fromNamespaceAndPath(namespace, path));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
