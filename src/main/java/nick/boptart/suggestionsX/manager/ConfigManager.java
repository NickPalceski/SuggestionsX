package nick.boptart.suggestionsX.manager;

import nick.boptart.suggestionsX.SuggestionsX;
import nick.boptart.suggestionsX.util.Suggestion;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigManager {

    private static ConfigManager configManager;
    private final JavaPlugin plugin;
    private FileConfiguration config;

    private static File suggestionsFile;
    private static File pendingFile;
    private static FileConfiguration suggestionsConfig;
    private static FileConfiguration pendingConfig;


    private static final List<Suggestion> suggestions = new ArrayList<>();
    private static final List<Suggestion> pendingSuggestions = new ArrayList<>();

    private int defaultSuggestionsLimit;

    private String playerMenuTitle;
    private String pendingMenuTitle;
    private String ownSuggestionsTitle;
    private String adminMenuTitle;
    private String playerSuggestionsTitle;
    private String adminSuggestionsTitle;



    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
        createSuggestionDataFiles(plugin);
        createPlayerDataFiles(plugin);
    }

    public static void initialize(ConfigManager configManager) {
        ConfigManager.configManager = configManager;
        ConfigManager.loadSuggestions();
        ConfigManager.loadPendingSuggestions();
    }

    public static ConfigManager getConfigManager() { return configManager; }

    public static List<Suggestion> getSuggestions() {
        return suggestions;
    }

    public FileConfiguration getSuggestionsConfig() { return suggestionsConfig; }

    public FileConfiguration getPendingConfig() { return pendingConfig; }

    public int getDefaultSuggestionsLimit() { return defaultSuggestionsLimit; }

    public String getPlayerMenuTitle() { return playerMenuTitle; }

    public String getPendingMenuTitle() { return pendingMenuTitle; }

    public String getOwnSuggestionsTitle() { return ownSuggestionsTitle; }

    public String getAdminMenuTitle() { return adminMenuTitle; }

    public String getPlayerSuggestionsTitle() { return playerSuggestionsTitle; }

    public String getAdminSuggestionsTitle() { return adminSuggestionsTitle; }

    public static List<Suggestion> getPendingSuggestions() {
        return pendingSuggestions;
    }


    public static Set<String> getGUITitles() {
        Set<String> guiTitles = new HashSet<>();
        SuggestionsX.getInstance().getConfig().getConfigurationSection("gui").getValues(false).forEach((key, value) -> {
            String title = ChatColor.translateAlternateColorCodes('&', value.toString());
            guiTitles.add(ChatColor.stripColor(title));
        });
        return guiTitles;
    }


    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        defaultSuggestionsLimit = config.getInt("default-suggestions-limit");

        ownSuggestionsTitle = config.getString("gui.own-suggestions-menu-title");
        playerMenuTitle = config.getString("gui.player-menu-title");
        playerSuggestionsTitle = config.getString("gui.player-suggestions-title");

        adminSuggestionsTitle = config.getString("gui.admin-suggestions-title");
        pendingMenuTitle = config.getString("gui.pending-menu-title");
        adminMenuTitle = config.getString("gui.admin-menu-title");
    }

    public static void createSuggestionDataFiles(JavaPlugin plugin) {
        File dataFolder = new File(plugin.getDataFolder(), "SuggestionData");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        suggestionsFile = new File(dataFolder, "suggestions.yml");
        if (!suggestionsFile.exists()) {
            try {
                suggestionsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        pendingFile = new File(dataFolder, "pending.yml");
        if (!pendingFile.exists()) {
            try {
                pendingFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        suggestionsConfig = YamlConfiguration.loadConfiguration(suggestionsFile);
        pendingConfig = YamlConfiguration.loadConfiguration(pendingFile);
    }

    public static void createPlayerDataFiles(JavaPlugin plugin) {
        File dataFolder = new File(plugin.getDataFolder(), "SuggestionData/PlayerData");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public static void createPlayerFile(Player player, JavaPlugin plugin) {
        File playerFile = new File(plugin.getDataFolder(), "SuggestionData/PlayerData/" + player.getName() + ".yml");
        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
                FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
                playerConfig.set("uuid", player.getUniqueId().toString());
                playerConfig.set("suggestionsLimit", configManager.getDefaultSuggestionsLimit());
                playerConfig.set("suggestions", new ArrayList<String>());
                playerConfig.save(playerFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public static void saveSuggestions() {
        FileConfiguration suggestionsConfig = configManager.getSuggestionsConfig();
        suggestionsConfig.set("suggestions", null); // Clear existing data
        for (Suggestion suggestion : suggestions) {
            String path = "suggestions." + suggestion.getUniqueID();
            suggestionsConfig.set(path + ".title", suggestion.getTitle());
            suggestionsConfig.set(path + ".description", suggestion.getDescription());
            suggestionsConfig.set(path + ".suggestor", suggestion.getCreator());
            suggestionsConfig.set(path + ".totalVotes", suggestion.totalVotes);
            suggestionsConfig.set(path + ".posVotes", suggestion.posVotes);
            suggestionsConfig.set(path + ".negVotes", suggestion.negVotes);

            //TODO: Add voters to the Set of the suggestion(s)?
        }
        try {
            suggestionsConfig.save(suggestionsFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(ChatColor.RED + "Could not save suggestions file!");
        }
    }

    public static void savePendingSuggestions() {
        FileConfiguration pendingConfig = configManager.getPendingConfig();
        pendingConfig.set("pending", null); // Clear existing data
        for (Suggestion suggestion : pendingSuggestions) {
            String path = "pending." + suggestion.getUniqueID();
            pendingConfig.set(path + ".title", suggestion.getTitle());
            pendingConfig.set(path + ".description", suggestion.getDescription());
            pendingConfig.set(path + ".suggestor", suggestion.getCreator());
        }
        try {
            suggestionsConfig.save(pendingFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(ChatColor.RED + "Could not save pending suggestions file!");
        }
    }

    static void loadSuggestions() {
        FileConfiguration suggestionsConfig = configManager.getSuggestionsConfig();
        if (suggestionsConfig.contains("suggestions")) {
            for (String key : suggestionsConfig.getConfigurationSection("suggestions").getKeys(false)) {
                String title = suggestionsConfig.getString("suggestions." + key + ".title");
                String description = suggestionsConfig.getString("suggestions." + key + ".description");
                String suggestor = suggestionsConfig.getString("suggestions." + key + ".suggestor");
                int totalVotes = suggestionsConfig.getInt("suggestions." + key + ".totalVotes");
                int posVotes = suggestionsConfig.getInt("suggestions." + key + ".posVotes");
                int negVotes = suggestionsConfig.getInt("suggestions." + key + ".negVotes");
                //TODO: Add voters Set to Suggestion


                Suggestion suggestion = new Suggestion(title, description, suggestor);
                suggestion.totalVotes = totalVotes;
                suggestion.posVotes = posVotes;
                suggestion.negVotes = negVotes;
                suggestions.add(suggestion);
            }
        }
    }

    static void loadPendingSuggestions() {
        FileConfiguration pendingConfig = configManager.getPendingConfig();
        if (pendingConfig.contains("pending")) {
            for (String key : pendingConfig.getConfigurationSection("pending").getKeys(false)) {
                String title = pendingConfig.getString("pending." + key + ".title");
                String description = pendingConfig.getString("pending." + key + ".description");
                String suggestor = pendingConfig.getString("pending." + key + ".suggestor");
                Suggestion suggestion = new Suggestion(title, description, suggestor);
                pendingSuggestions.add(suggestion);
            }
        }
    }

    public static void reloadSuggestionDataFiles() {
        createSuggestionDataFiles(ConfigManager.configManager.plugin);
        loadSuggestions();
        loadPendingSuggestions();
    }



}
