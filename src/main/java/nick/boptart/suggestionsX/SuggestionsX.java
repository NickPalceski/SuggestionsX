package nick.boptart.suggestionsX;

import nick.boptart.suggestionsX.command.SuggestionsCommand;
import nick.boptart.suggestionsX.listener.ChatListener;
import nick.boptart.suggestionsX.listener.InventoryListener;
import nick.boptart.suggestionsX.manager.ConfigManager;

import org.bukkit.plugin.java.JavaPlugin;


public final class SuggestionsX extends JavaPlugin {

    private static SuggestionsX instance;

    public static SuggestionsX getInstance() {
        return instance;
    }



    @Override
    public void onEnable() {
        // Plugin startup logic

        startConfig();

        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(ConfigManager.getGUITitles()), this);

        //handles Main Menu and other commands... (second argument)
        getCommand("suggestions").setExecutor(new SuggestionsCommand(this));

    }





    @Override
    public void onDisable() {
        // Plugin shutdown logic

        //save all suggestions and config data on shutdown. TODO: save player file(s)
        ConfigManager.saveSuggestions();
        ConfigManager.savePendingSuggestions();


    }





    public void startConfig() {
        ConfigManager configManager = new ConfigManager(this);
        ConfigManager.initialize(configManager);
    }


}
