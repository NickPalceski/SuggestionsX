package nick.boptart.suggestionsX.manager;

import nick.boptart.suggestionsX.SuggestionsX;
import nick.boptart.suggestionsX.suggestion.Suggestion;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigManager {

    private static ConfigManager configManager;
    private static final SuggestionsX plugin = SuggestionsX.getInstance();
    private static FileConfiguration config;

    private static File suggestionsFile;
    private static File pendingFile;
    private static FileConfiguration suggestionsConfig;
    private static FileConfiguration pendingConfig;


    private static final List<Suggestion> suggestions = new ArrayList<>();
    private static final List<Suggestion> pendingSuggestions = new ArrayList<>();

    public Map<UUID, String> getPlayerFileCache() {
        return playerFileCache;
    }

    private static final Map<UUID, String> playerFileCache = new HashMap<>();

    private static int defaultSuggestionsLimit;

    public static ConfigManager getConfigManager() { return configManager; }

    public static List<Suggestion> getSuggestions() {
        return suggestions;
    }

    public static FileConfiguration getSuggestionsConfig() { return suggestionsConfig; }
    public static FileConfiguration getPendingConfig() { return pendingConfig; }

    public int getDefaultSuggestionsLimit() { return defaultSuggestionsLimit; }


    public static List<Suggestion> getPendingSuggestions() {
        return pendingSuggestions;
    }


    public ConfigManager(SuggestionsX plugin) {
        loadConfig();
        createSuggestionDataFiles(plugin);
        createPlayerDataFolder(plugin);
    }

    public static void addPendingSuggestionToFile(Suggestion suggestion) {
        String path = "pending." + suggestion.getUniqueID();
        pendingConfig.set(path + ".title", suggestion.getTitle());
        pendingConfig.set(path + ".description", suggestion.getDescription());
        pendingConfig.set(path + ".suggester", suggestion.getCreator());
        pendingConfig.set(path + ".status", suggestion.getStatus());

        try {
            pendingConfig.save(pendingFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(ChatColor.RED + "Could not add pending suggestion to file!");
        }
    }

    public static void addSuggestionToFile(Suggestion clickedSuggestion) {

        String path = "suggestions." + clickedSuggestion.getUniqueID();
        suggestionsConfig.set(path + ".title", clickedSuggestion.getTitle());
        suggestionsConfig.set(path + ".description", clickedSuggestion.getDescription());
        suggestionsConfig.set(path + ".suggester", clickedSuggestion.getCreator());
        suggestionsConfig.set(path + ".status", clickedSuggestion.getStatus());
        suggestionsConfig.set(path + ".posVotes", clickedSuggestion.getPosVotes());
        suggestionsConfig.set(path + ".negVotes", clickedSuggestion.getNegVotes());
        suggestionsConfig.set(path + ".posVoters", clickedSuggestion.getUpVoters());
        suggestionsConfig.set(path + ".negVoters", clickedSuggestion.getDownVoters());

        try {
            suggestionsConfig.save(suggestionsFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(ChatColor.RED + "Could not add suggestion to file!");
        }
    }

    public static void removeSuggestionFromPendingFile(Suggestion suggestion) {
        pendingConfig.set("pending." + suggestion.getUniqueID(), null);

        try {
            pendingConfig.save(pendingFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(ChatColor.RED + "Could not save pending file!");
        }
    }

    public static void removeSuggestionFromFile(Suggestion clickedSuggestion) {
        suggestionsConfig.set("suggestions." + clickedSuggestion.getUniqueID(), null);

        try {
            suggestionsConfig.save(suggestionsFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(ChatColor.RED + "Could not save suggestions file!");
        }
    }

    public void initialize() {
        configManager = this;
        loadSuggestions();
        loadPendingSuggestions();
        loadPlayerCache();

    }

    public static String getMenuTitle(String configTitle) {
        String title = SuggestionsX.getInstance().getConfig().getString("gui." + configTitle);
        if (title == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', title);
    }

    public static Suggestion getSuggestionByUUID(List<Suggestion> suggestions ,UUID uuid) {
        for (Suggestion suggestion : suggestions) {
            if (suggestion.getUniqueID().equals(uuid)) {
                return suggestion;
            }
        }
        return null;
    }

    public static boolean removeSuggestionByUUID(List<Suggestion> suggestions, UUID uuid) {
        return suggestions.removeIf(suggestion -> suggestion.getUniqueID().equals(uuid));
    }


    public static void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        defaultSuggestionsLimit = config.getInt("default-suggestions-limit");

    }
    // Load player cache from player files ( faster access than searching through files )
    public static void loadPlayerCache() {
        File playerDataFolder = new File(plugin.getDataFolder(), "SuggestionData/PlayerData");
        if (!playerDataFolder.exists()) return;

        for (File file : playerDataFolder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".yml")) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                String storedUUID = config.getString("uuid");
                if (storedUUID != null) {
                    playerFileCache.put(UUID.fromString(storedUUID), file.getName());
                }
            }
        }
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

    public static void createPlayerDataFolder(JavaPlugin plugin) {
        File dataFolder = new File(plugin.getDataFolder(), "SuggestionData/PlayerData");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public static void createPlayerFile(Player player, JavaPlugin plugin) {
        File playerDataFolder = new File(plugin.getDataFolder(), "SuggestionData/PlayerData");

        if (!playerDataFolder.exists()) {
            if (playerDataFolder.mkdirs()) {
                System.out.println("Created PlayerData folder: " + playerDataFolder.getAbsolutePath());
            } else {
                System.out.println("FAILED to create PlayerData folder!");
                return;
            }
        }

        UUID playerUUID = player.getUniqueId();
        String correctFileName = player.getName() + ".yml";
        File correctPlayerFile = new File(playerDataFolder, correctFileName);
        File existingFile = null;

        // Check if a file already exists for the player's UUID
        for (File file : playerDataFolder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".yml")) {
                FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(file);
                String storedUUID = playerConfig.getString("uuid");

                if (storedUUID != null && storedUUID.equals(playerUUID.toString())) {
                    existingFile = file;
                    break;
                }
            }
        }

        // **Rename file if the player changed names**
        if (existingFile != null && !existingFile.getName().equals(correctFileName)) {
            File newFile = new File(playerDataFolder, correctFileName);
            if (existingFile.renameTo(newFile)) {
                System.out.println("Renamed player file: " + existingFile.getName() + " → " + correctFileName);
            } else {
                System.out.println("Failed to rename player file!");
                return;
            }
        }

        // **Create file if it does not exist**
        if (!correctPlayerFile.exists()) {
            try {
                if (correctPlayerFile.createNewFile()) {
                    FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(correctPlayerFile);
                    playerConfig.set("uuid", playerUUID.toString());
                    playerConfig.set("suggestionsLimit", ConfigManager.getConfigManager().getDefaultSuggestionsLimit());
                    playerConfig.set("suggestions", new ArrayList<>());
                    playerConfig.save(correctPlayerFile);

                    System.out.println("Created new player file: " + correctFileName);
                } else {
                    System.out.println("Failed to create player file: " + correctFileName);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("IO Exception while creating player file!");
            }
        } else {
            System.out.println("Player file already exists: " + correctFileName);
        }

        // Update playerFileCache Dynamically
        playerFileCache.put(playerUUID, correctFileName);
        System.out.println("Added to playerFileCache: " + playerUUID + " → " + correctFileName);

    }




    public static void saveSuggestionsToFile() {
        // Clear the old suggestions section
        suggestionsConfig.set("suggestions", null);

        // Write all current suggestions
        for (Suggestion suggestion : suggestions) {
            String path = "suggestions." + suggestion.getUniqueID();
            suggestionsConfig.set(path + ".title", suggestion.getTitle());
            suggestionsConfig.set(path + ".description", suggestion.getDescription());
            suggestionsConfig.set(path + ".suggester", suggestion.getCreator());
            suggestionsConfig.set(path + ".status", suggestion.getStatus());
            suggestionsConfig.set(path + ".posVotes", suggestion.getPosVotes());
            suggestionsConfig.set(path + ".negVotes", suggestion.getNegVotes());
            suggestionsConfig.set(path + ".posVoters", suggestion.getUpVoters());
            suggestionsConfig.set(path + ".negVoters", suggestion.getDownVoters());
        }

        try {
            suggestionsConfig.save(suggestionsFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(ChatColor.RED + "Could not save suggestions file!");
        }
    }

    public static void savePendingSuggestionsToFile() {
        // Clear the old pending section
        pendingConfig.set("pending", null);

        // Write all current pending suggestions
        for (Suggestion suggestion : pendingSuggestions) {
            String path = "pending." + suggestion.getUniqueID();
            pendingConfig.set(path + ".title", suggestion.getTitle());
            pendingConfig.set(path + ".description", suggestion.getDescription());
            pendingConfig.set(path + ".suggester", suggestion.getCreator());
            pendingConfig.set(path + ".status", suggestion.getStatus());
        }

        try {
            pendingConfig.save(pendingFile);
            System.out.println("Successfully saved `pending.yml`!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save `pending.yml`!");
        }
    }

     public static void loadSuggestions() {
        suggestions.clear();

        if (suggestionsConfig.contains("suggestions")) {
            ConfigurationSection section = suggestionsConfig.getConfigurationSection("suggestions");
                for (String key : section.getKeys(false)) {
                    //get suggestion uuid
                    UUID uuid = UUID.fromString(key);
                    String title = suggestionsConfig.getString("suggestions." + key + ".title");
                    String description = suggestionsConfig.getString("suggestions." + key + ".description");
                    String suggester = suggestionsConfig.getString("suggestions." + key + ".suggester");
                    int posVotes = suggestionsConfig.getInt("suggestions." + key + ".posVotes");
                    int negVotes = suggestionsConfig.getInt("suggestions." + key + ".negVotes");
                    String statusString = suggestionsConfig.getString("suggestions." + key + ".status");
                    Suggestion.Status status = Suggestion.Status.valueOf(statusString);

                    // Get suggestion from file
                    Suggestion suggestion = new Suggestion(uuid, title, description, suggester, status);
                    suggestion.posVotes = posVotes;
                    suggestion.negVotes = negVotes;
                    suggestions.add(suggestion);
                }
        }
    }

    public static void loadPendingSuggestions() {
        pendingSuggestions.clear();

        if (pendingConfig.contains("pending")) {
            ConfigurationSection section = pendingConfig.getConfigurationSection("pending");
            for (String key : section.getKeys(false)) {
                //get suggestion uuid
                UUID uuid = UUID.fromString(key);
                String title = pendingConfig.getString("pending." + key + ".title");
                String description = pendingConfig.getString("pending." + key + ".description");
                String suggester = pendingConfig.getString("pending." + key + ".suggester");
                String statusString = pendingConfig.getString("pending." + key + ".status");
                Suggestion.Status status = Suggestion.Status.valueOf(statusString);

                // Get suggestion from file
                Suggestion suggestion = new Suggestion(uuid, title, description, suggester, status);
                pendingSuggestions.add(suggestion);
            }
        } else {
            System.out.println("No pending suggestions found in config.");
        }
    }

    public static void reloadSuggestionDataFiles() {
        createSuggestionDataFiles(plugin);
        loadSuggestions();
        loadPendingSuggestions();
    }

    public static void savePlayerFiles() {
        File playerDataFolder = new File(SuggestionsX.getInstance().getDataFolder(), "SuggestionData/PlayerData");

        if (!playerDataFolder.exists()) {

            ConfigManager.createPlayerDataFolder(SuggestionsX.getInstance());

        } else if (!playerDataFolder.isDirectory()) {
            System.out.println("ERROR: PlayerData folder is missing or not a directory.");
            System.out.println("Expected Path: " + playerDataFolder.getAbsolutePath());
            return;
        }

        for (File file : playerDataFolder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".yml")) {
                FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(file);

                try {
                    playerConfig.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Failed to save player file: " + file.getName());
                }
            }
        }
    }

}
