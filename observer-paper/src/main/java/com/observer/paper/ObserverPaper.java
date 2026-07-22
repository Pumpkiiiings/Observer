package com.observer.paper;

import com.observer.paper.command.ObserverCommand;
import com.observer.paper.input.KeyInputManager;
import com.observer.paper.layout.LayoutManager;
import com.observer.paper.network.ObserverNetworkManager;
import com.observer.paper.menu.MenuManager;
import com.observer.paper.menu.TestMenu;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public class ObserverPaper extends JavaPlugin {
    private static ObserverPaper instance;
    private ObserverPlayerManager playerManager;
    private ObserverNetworkManager networkManager;
    private LayoutManager layoutManager;
    private com.observer.paper.save.SaveManager saveManager;
    private KeyInputManager keyInputManager;
    private com.observer.paper.audio.ObserverSoundManager soundManager;
    private com.observer.paper.environment.ObserverEnvironmentManager environmentManager;
    private MenuManager menuManager;
    private com.observer.paper.keys.ObserverKeyboardManager keyboardManager;
    private com.observer.paper.keys.KeyActionManager keyActionManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        this.soundManager = new com.observer.paper.audio.ObserverSoundManager(getLogger());
        this.environmentManager = new com.observer.paper.environment.ObserverEnvironmentManager(getLogger());
        this.playerManager = new ObserverPlayerManager();
        this.networkManager = new ObserverNetworkManager(this, playerManager);
        this.layoutManager = new LayoutManager(this);
        this.saveManager = new com.observer.paper.save.SaveManager(this);
        this.keyInputManager = new KeyInputManager(this);
        
        this.menuManager = new MenuManager(this);
        this.keyboardManager = new com.observer.paper.keys.ObserverKeyboardManager();
        this.keyActionManager = new com.observer.paper.keys.KeyActionManager(this);

        // Registrar Menús Hardcodeados
        this.menuManager.registerMenu(new TestMenu());

        getServer().getPluginManager().registerEvents(playerManager, this);
        this.keyInputManager.register();

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(ObserverCommand.build().build(), "Observer main command");
        });

        // Support /reload: re-handshake players already online
        for (org.bukkit.entity.Player player : getServer().getOnlinePlayers()) {
            org.bukkit.Bukkit.getScheduler().runTaskLater(this, () -> {
                if (player.isOnline()) {
                    networkManager.sendHandshakeRequest(player);
                }
            }, 20L);
        }

        // Initialize layouts and saves synchronously so they are available immediately
        layoutManager.initialize();
        saveManager.initialize();

        getLogger().info("Observer Paper enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Observer Paper disabled.");
    }

    public void reload() {
        reloadConfig();
        
        if (keyActionManager != null) {
            keyActionManager.loadBinds();
        }
        
        if (layoutManager != null) {
            layoutManager.reload();
        }
        
        if (saveManager != null) {
            saveManager.reload();
        }
        
        getLogger().info("[Observer] All systems reloaded.");
    }

    public static ObserverPaper getInstance() { return instance; }
    public ObserverPlayerManager getPlayerManager() { return playerManager; }
    public ObserverNetworkManager getNetworkManager() { return networkManager; }
    public LayoutManager getLayoutManager() { return layoutManager; }
    public com.observer.paper.save.SaveManager getSaveManager() { return saveManager; }
    public com.observer.paper.audio.ObserverSoundManager getSoundManager() { return soundManager; }
    public com.observer.paper.environment.ObserverEnvironmentManager getEnvironmentManager() { return environmentManager; }
    public MenuManager getMenuManager() { return menuManager; }
    public com.observer.paper.keys.ObserverKeyboardManager getKeyboardManager() { return keyboardManager; }
}
