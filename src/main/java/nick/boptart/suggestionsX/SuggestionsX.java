package nick.boptart.suggestionsX;

import nick.boptart.suggestionsX.command.SuggestionsCommand;
import nick.boptart.suggestionsX.listener.ChatListener;
import nick.boptart.suggestionsX.listener.InventoryListener;
import nick.boptart.suggestionsX.listener.PlayerJoinListener;
import nick.boptart.suggestionsX.manager.ConfigManager;
import nick.boptart.suggestionsX.manager.PlayerManager;

import org.bukkit.plugin.java.JavaPlugin;


public final class SuggestionsX extends JavaPlugin {

    private static SuggestionsX instance;
    private static ConfigManager configManager;

    public static SuggestionsX getInstance() {
        return instance;
    }
    public static ConfigManager getConfigManager() {
        return configManager;
    }


    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        configManager = new ConfigManager(this);
        configManager.initialize();

        PlayerManager.initialize(this);

        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        //handles Main Menu and other commands... (second argument)
        getCommand("suggestions").setExecutor(new SuggestionsCommand(this));

    }





    @Override
    public void onDisable() {
        // Plugin shutdown logic

        //save all suggestions,config data, and player files on shutdown.
        ConfigManager.saveSuggestions();
        ConfigManager.savePendingSuggestions();
        ConfigManager.savePlayerFiles();



    }





}
