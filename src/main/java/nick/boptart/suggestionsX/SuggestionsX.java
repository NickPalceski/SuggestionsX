package nick.boptart.suggestionsX;

import nick.boptart.suggestionsX.command.SuggestionsCommand;
import nick.boptart.suggestionsX.listener.*;
import nick.boptart.suggestionsX.manager.ConfigManager;

import org.bukkit.plugin.java.JavaPlugin;


public final class SuggestionsX extends JavaPlugin {

    private static SuggestionsX instance;

    public static SuggestionsX getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        ConfigManager configManager = new ConfigManager(this);
        configManager.initialize();

        //Register listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new MainMenuListener(), this);
        getServer().getPluginManager().registerEvents(new OwnSuggestionsListener(), this);
        getServer().getPluginManager().registerEvents(new ServerSuggestionsListener(), this);
        getServer().getPluginManager().registerEvents(new PendingMenuListener(), this);

        //handles Main Menu and other commands... (second args)
        getCommand("suggestions").setExecutor(new SuggestionsCommand());
    }


    @Override
    public void onDisable() {
        //save all suggestions,config data, and player files on shutdown.
        //TODO error in console on shutdown?
        ConfigManager.saveSuggestionsToFile();
        ConfigManager.savePendingSuggestions();
        ConfigManager.savePlayerFiles();

    }

}
