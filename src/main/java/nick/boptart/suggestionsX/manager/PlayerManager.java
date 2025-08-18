package nick.boptart.suggestionsX.manager;

import nick.boptart.suggestionsX.SuggestionsX;
import nick.boptart.suggestionsX.suggestion.Suggestion;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerManager {

    private static final SuggestionsX plugin = SuggestionsX.getInstance();

    public static boolean hasSuggestionPoints(Player player) {
        File playerFile = getPlayerFileByName(player.getName());

        if (playerFile != null) {
            int suggestionCount = getPlayerSuggestionCount(player.getName());

            if (suggestionCount > 0){
                return true;
            }
            else{
                System.out.println("No suggestions left!");
            }
        }

        else{
            System.out.println("§cCould not find player file.");
        }
        return false;
    }


    public static int getPlayerSuggestionCount(String playerName) {
        File playerFile = getPlayerFileByName(playerName);

        if (playerFile != null) {
            FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
            int suggestionCount = playerConfig.getInt("suggestionsLimit"); // Ensure correct key name

            System.out.println("Retrieved suggestion count for " + playerName + ": " + suggestionCount);
            return suggestionCount;
        }

        System.out.println("Could not find player file for " + playerName);
        return 0;
    }

    public static void setPlayerSuggestionCount(String playerName, int num){
        File playerFile = getPlayerFileByName(playerName);
        if (playerFile != null) {
            FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
            playerConfig.set("suggestionsLimit", num);
            try {
                playerConfig.save(playerFile);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("§cFailed to save player file.");
            }
        }
        else {
            System.out.println("§cCould not find player file.");
        }
    }

    public static UUID getPlayerUUIDByName(String playerName) {
        File playerFile = getPlayerFileByName(playerName);
        if (playerFile != null) {
            FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
            String uuidString = playerConfig.getString("uuid");
            if (uuidString != null) {
                return UUID.fromString(uuidString);
            }
        }
        return null; // Return null if no UUID is found
    }

    public static File getPlayerFileByName(String playerName) {
        File playerDataFolder = new File(SuggestionsX.getInstance().getDataFolder(), "SuggestionData/PlayerData");

        if (!playerDataFolder.exists() || !playerDataFolder.isDirectory()) {
            System.out.println("ERROR: PlayerData folder is missing or not a directory.");
            System.out.println("Expected Path: " + playerDataFolder.getAbsolutePath());
            return null;
        }

        System.out.println("Checking folder: " + playerDataFolder.getAbsolutePath());

        for (File file : playerDataFolder.listFiles()) {
            System.out.println("Found file: " + file.getName());

            if (file.isFile() && file.getName().equalsIgnoreCase(playerName + ".yml")) {
                System.out.println("Matched player file: " + file.getAbsolutePath());
                return file;
            }
        }
        //TODO handle case where player name is changed?
        System.out.println("No matching file found for player: " + playerName);
        return null;
    }

    public static Suggestion getSuggestionByCreator(String creatorName) {
        return ConfigManager.getSuggestions().stream()
                .filter(suggestion -> suggestion.getCreator().equalsIgnoreCase(creatorName))
                .findFirst()
                .orElse(null);
    }

    public static UUID getCreatorUUID(String creatorName) {
        Suggestion suggestion = getSuggestionByCreator(creatorName);
        if (suggestion != null) {
            return getPlayerUUIDByName(suggestion.getCreator());
        }
        return null;
    }

    public static void addSuggestionToPlayer(Suggestion suggestion, String playerName) {
        UUID playerUUID = getPlayerUUIDByName(playerName);

        // Add the suggestion UUID to the player's file
        File playerFile = PlayerManager.getPlayerFile(playerUUID, plugin);
        if (playerFile != null) {
            FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
            List<String> suggestions = playerConfig.getStringList("suggestions");
            suggestions.add(suggestion.getUniqueID().toString());
            playerConfig.set("suggestions", suggestions);
            savePlayerFile(playerFile);
        }
        else {
            System.out.println("Could not find player file.");
        }

    }

    public static void removeSuggestionFromPlayer(Suggestion suggestion, String playerName) {
        UUID playerUUID = getPlayerUUIDByName(playerName);

        // Remove the suggestion UUID from the player's file
        File playerFile = PlayerManager.getPlayerFile(playerUUID, plugin);
        if (playerFile != null) {
            FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
            List<String> suggestions = playerConfig.getStringList("suggestions");
            suggestions.remove(suggestion.getUniqueID().toString());
            playerConfig.set("suggestions", suggestions);

            savePlayerFile(playerFile);
        }
        else {
            System.out.println("Could not find player file.");
        }

    }

    public static List<Suggestion> getPlayerSuggestions(File playerFile) {
        List<Suggestion> playerSuggestions = new ArrayList<>();
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        List<String> uuidStrings = config.getStringList("suggestions");

        for (String uuidStr : uuidStrings) {
            UUID uuid = UUID.fromString(uuidStr);
            Suggestion suggestion = getSuggestionByUUID(uuid); // This should search all lists

            if (suggestion != null) {
                playerSuggestions.add(suggestion);
            }
        }
        return playerSuggestions;
    }

    public static Suggestion getSuggestionByUUID(UUID uuid) {
        for (Suggestion suggestion : ConfigManager.getSuggestions()) {
            if (suggestion.getUniqueID().equals(uuid)) {
                return suggestion;
            }
        }
        for (Suggestion suggestion : ConfigManager.getPendingSuggestions()) {
            if (suggestion.getUniqueID().equals(uuid)) {
                return suggestion;
            }
        }
        return null;
    }

    public static void savePlayerFile(File playerFile){
        if (playerFile != null) {
            FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
            try {
                playerConfig.save(playerFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static File getPlayerFile(UUID playerUUID, SuggestionsX plugin) {
        String fileName = ConfigManager.getConfigManager().getPlayerFileCache().get(playerUUID);
        if (fileName != null) {
            return new File(plugin.getDataFolder(), "SuggestionData/PlayerData/" + fileName);
        }
        return null;
    }

}
