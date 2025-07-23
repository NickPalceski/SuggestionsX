package nick.boptart.suggestionsX;

import nick.boptart.suggestionsX.command.SuggestionsCommand;
import nick.boptart.suggestionsX.listener.*;
import nick.boptart.suggestionsX.manager.ConfigManager;

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
        instance = this;
        instance.saveDefaultConfig();
        configManager = new ConfigManager(this);
        configManager.initialize();

        //Register listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new MainMenuListener(), this);
        getServer().getPluginManager().registerEvents(new OwnSuggestionsListener(), this);
        getServer().getPluginManager().registerEvents(new ServerSuggestionsListener(), this);
        getServer().getPluginManager().registerEvents(new PendingMenuListener(), this);

        //handles Main Menu and other commands... (second argument)
        getCommand("suggestions").setExecutor(new SuggestionsCommand());
    }



    @Override
    public void onDisable() {
        //save all suggestions,config data, and player files on shutdown.
        //TODO error in console on shutdown
        ConfigManager.saveSuggestions();
        ConfigManager.savePendingSuggestions();
        ConfigManager.savePlayerFiles();

    }

}
