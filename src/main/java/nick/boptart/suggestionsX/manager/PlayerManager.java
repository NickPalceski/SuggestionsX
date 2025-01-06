package nick.boptart.suggestionsX.manager;

import nick.boptart.suggestionsX.util.Suggestion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class PlayerManager {

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
        File playerDataFolder = new File("SuggestionData/PlayerData");
        if (playerDataFolder.exists() && playerDataFolder.isDirectory()) {
            for (File file : playerDataFolder.listFiles()) {
                if (file.isFile() && file.getName().equalsIgnoreCase(playerName + ".yml")) {
                    return file;
                }
            }
        }
        return null; // Return null if no matching file is found
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
        return null; // Return null if no suggestion or UUID is found
    }

    public static void addSuggestionToPlayer(Suggestion suggestion, String playerName) {
        UUID playerUUID = getPlayerUUIDByName(playerName);

        // Add the suggestion UUID to the player's file
        File playerFile = getPlayerFile(playerUUID);
        if (playerFile != null) {
            FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
            List<String> suggestions = playerConfig.getStringList("suggestions");
            suggestions.add(suggestion.getUniqueID().toString());
            playerConfig.set("suggestions", suggestions);
            try {
                playerConfig.save(playerFile);
            } catch (IOException e) {
                e.printStackTrace();
                Bukkit.getPlayer(playerUUID).sendMessage("§cFailed to save your vote. Please try again.");
            }
        }
        else {
            Bukkit.getPlayer(playerUUID).sendMessage("§cCould not find player file.");
        }

    }

    public static File getPlayerFile(UUID playerUUID) {
        File playerDataFolder = new File("SuggestionData/PlayerData");
        if (playerDataFolder.exists() && playerDataFolder.isDirectory()) {
            for (File file : playerDataFolder.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".yml")) {
                    FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(file);
                    String uuid = playerConfig.getString("uuid");
                    if (playerUUID.toString().equals(uuid)) {
                        return file;
                    }
                }
            }
        }
        return null; // Return null if no matching file is found
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


}
