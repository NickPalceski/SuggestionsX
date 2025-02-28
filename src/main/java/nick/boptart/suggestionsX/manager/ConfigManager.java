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
import java.util.*;

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

    private final Set<String> validGuiTitles;

    public Map<UUID, String> getPlayerFileCache() {
        return playerFileCache;
    }

    private final Map<UUID, String> playerFileCache = new HashMap<>();

    private int defaultSuggestionsLimit;

    private String playerMenuTitle;
    private String pendingMenuTitle;
    private String ownSuggestionsTitle;
    private String adminMenuTitle;
    private String playerSuggestionsTitle;
    private String adminSuggestionsTitle;



    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.validGuiTitles = new HashSet<>();
        loadConfig();
        createSuggestionDataFiles(plugin);
        createPlayerDataFolder(plugin);
    }

    public void initialize() {
        configManager = this;
        this.loadSuggestions();
        this.loadPendingSuggestions();
        this.loadPlayerCache();

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

        SuggestionsX.getInstance().getConfig().getConfigurationSection("gui").getValues(false).forEach((key, value) -> {
            String title = ChatColor.translateAlternateColorCodes('&', value.toString());
            configManager.validGuiTitles.add(ChatColor.stripColor(title));
        });
        return configManager.validGuiTitles;
    }

    public static Suggestion getSuggestionByUUID(UUID uuid) {
        FileConfiguration pendingConfig = configManager.getPendingConfig();
        FileConfiguration suggestionsConfig = configManager.getSuggestionsConfig();

        String path = "pending." + uuid.toString();
        if (pendingConfig.contains(path)) {
            return new Suggestion(
                    pendingConfig.getString(path + ".title"),
                    pendingConfig.getString(path + ".description"),
                    pendingConfig.getString(path + ".suggester")
            );
        }

        path = "suggestions." + uuid;
        if (suggestionsConfig.contains(path)) {
            return new Suggestion(
                    suggestionsConfig.getString(path + ".title"),
                    suggestionsConfig.getString(path + ".description"),
                    suggestionsConfig.getString(path + ".suggester")
            );
        }

        System.out.println("❌ No suggestion found for UUID: " + uuid);
        return null;
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
    // Load player cache from player files ( faster access than searching through files )
    public void loadPlayerCache() {
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
                System.out.println("✅ Created PlayerData folder: " + playerDataFolder.getAbsolutePath());
            } else {
                System.out.println("❌ FAILED to create PlayerData folder!");
                return;
            }
        }

        UUID playerUUID = player.getUniqueId();
        String correctFileName = player.getName() + ".yml";
        File correctPlayerFile = new File(playerDataFolder, correctFileName);
        File existingFile = null;

        // **Check if a file already exists for the player's UUID**
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
                System.out.println("🔄 Renamed player file: " + existingFile.getName() + " → " + correctFileName);
            } else {
                System.out.println("❌ Failed to rename player file!");
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

                    System.out.println("✅ Created new player file: " + correctFileName);
                } else {
                    System.out.println("❌ Failed to create player file: " + correctFileName);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("❌ IO Exception while creating player file!");
            }
        } else {
            System.out.println("ℹ️ Player file already exists: " + correctFileName);
        }

        // Update playerFileCache Dynamically
        configManager.playerFileCache.put(playerUUID, correctFileName);
        System.out.println("📥 Added to playerFileCache: " + playerUUID + " → " + correctFileName);

    }

    public static File getPlayerFile(UUID playerUUID, JavaPlugin plugin) {
        String fileName = configManager.playerFileCache.get(playerUUID);
        if (fileName != null) {
            return new File(plugin.getDataFolder(), "SuggestionData/PlayerData/" + fileName);
        }
        return null;
    }

    public static void savePlayerFiles() {
        File playerDataFolder = new File(SuggestionsX.getInstance().getDataFolder(), "SuggestionData/PlayerData");

        if (!playerDataFolder.exists()) {

            createPlayerDataFolder(SuggestionsX.getInstance());

        } else if (!playerDataFolder.isDirectory()) {
            System.out.println("❌ ERROR: PlayerData folder is missing or not a directory.");
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
                    System.out.println("❌ Failed to save player file: " + file.getName());
                }
            }
        }
    }



    public static void saveSuggestions() {
        FileConfiguration suggestionsConfig = configManager.getSuggestionsConfig();

        Set<String> existingKeys = suggestionsConfig.getConfigurationSection("suggestions").getKeys(false);
        for (Suggestion suggestion : suggestions) {
            String path = "suggestions." + suggestion.getUniqueID();

            if (!existingKeys.contains(suggestion.getUniqueID().toString())) {

                suggestionsConfig.set(path + ".title", suggestion.getTitle());
                suggestionsConfig.set(path + ".description", suggestion.getDescription());
                suggestionsConfig.set(path + ".suggester", suggestion.getCreator());
                suggestionsConfig.set(path + ".totalVotes", suggestion.totalVotes);
                suggestionsConfig.set(path + ".posVotes", suggestion.posVotes);
                suggestionsConfig.set(path + ".negVotes", suggestion.negVotes);
                //Add voters to the Set of the suggestion(s)?
                suggestionsConfig.set(path + ".voters", suggestion.getVoters());
            }
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

        // Load existing suggestions (UUID) from the file
        Set<String> existingKeys = pendingConfig.getConfigurationSection("pending").getKeys(false);

        for (Suggestion suggestion : pendingSuggestions) {
            String path = "pending." + suggestion.getUniqueID();
            if (!existingKeys.contains(suggestion.getUniqueID().toString())) {
                pendingConfig.set(path + ".title", suggestion.getTitle());
                pendingConfig.set(path + ".description", suggestion.getDescription());
                pendingConfig.set(path + ".suggester", suggestion.getCreator());
            }
        }

        try {
            pendingConfig.save(pendingFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(ChatColor.RED + "Could not save pending suggestions file!");
        }
    }

     void loadSuggestions() {
        FileConfiguration suggestionsConfig = configManager.getSuggestionsConfig();
        if (suggestionsConfig.contains("suggestions")) {
            for (String key : suggestionsConfig.getConfigurationSection("suggestions").getKeys(false)) {
                String title = suggestionsConfig.getString("suggestions." + key + ".title");
                String description = suggestionsConfig.getString("suggestions." + key + ".description");
                String suggester = suggestionsConfig.getString("suggestions." + key + ".suggester");
                int totalVotes = suggestionsConfig.getInt("suggestions." + key + ".totalVotes");
                int posVotes = suggestionsConfig.getInt("suggestions." + key + ".posVotes");
                int negVotes = suggestionsConfig.getInt("suggestions." + key + ".negVotes");

                Suggestion suggestion = new Suggestion(title, description, suggester);
                suggestion.totalVotes = totalVotes;
                suggestion.posVotes = posVotes;
                suggestion.negVotes = negVotes;
                suggestions.add(suggestion);
            }
        }
    }

     void loadPendingSuggestions() {
        FileConfiguration pendingConfig = configManager.getPendingConfig();
        if (pendingConfig.contains("pending")) {
            for (String key : pendingConfig.getConfigurationSection("pending").getKeys(false)) {
                String title = pendingConfig.getString("pending." + key + ".title");
                String description = pendingConfig.getString("pending." + key + ".description");
                String suggester = pendingConfig.getString("pending." + key + ".suggester");
                Suggestion suggestion = new Suggestion(title, description, suggester);
                pendingSuggestions.add(suggestion);
            }
        }
    }

    public void reloadSuggestionDataFiles() {
        createSuggestionDataFiles(plugin);
        loadSuggestions();
        loadPendingSuggestions();
    }



}
