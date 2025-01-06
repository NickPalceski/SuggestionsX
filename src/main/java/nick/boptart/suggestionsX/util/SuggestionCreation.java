package nick.boptart.suggestionsX.util;

import nick.boptart.suggestionsX.manager.ConfigManager;
import nick.boptart.suggestionsX.manager.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SuggestionCreation {

    private static final Map<UUID, Map<String, String>> playersAddingSuggestion = new HashMap<>();
    public static Map<UUID, Map<String, String>> getPlayersAddingSuggestion() {
        return playersAddingSuggestion;
    }


    public static void startAddingSuggestion(Player player) {
        UUID playerUUID = player.getUniqueId();
        Map<String, String> suggestionData = new HashMap<>();

        suggestionData.put("stage", "title");
        playersAddingSuggestion.put(playerUUID, suggestionData);

        player.sendMessage(ChatColor.AQUA + "Please type the " + ChatColor.BOLD + "title" + ChatColor.AQUA + " of your suggestion in chat.");
        player.sendMessage("-----------------------------------------------");
    }

    public static void playerSuggestionChat(Player player, String message) {
        UUID playerUUID = player.getUniqueId();
        Map<String, String> suggestionData = playersAddingSuggestion.get(playerUUID);

        String stage = suggestionData.get("stage");

        switch (stage) {

            case "title":
                player.playSound(player, Sound.BLOCK_ANVIL_USE, 0.8f, 0.8f);
                suggestionData.put("title", message);
                player.sendMessage(ChatColor.GREEN + "Title set to:" + ChatColor.WHITE + " " + message);

                player.sendMessage(ChatColor.AQUA + "Type the " + ChatColor.BOLD + "description" + ChatColor.AQUA + " of your suggestion.");
                player.sendMessage("-----------------------------------------------");
                suggestionData.put("stage", "description");
                break;

            case "description":
                player.playSound(player, Sound.BLOCK_ANVIL_USE, 0.8f, 0.8f);
                suggestionData.put("description", message);
                player.sendMessage(ChatColor.GREEN + "Description set to:" + ChatColor.WHITE + " " + message);

                Suggestion suggestion = new Suggestion(
                        suggestionData.get("title"),
                        suggestionData.get("description"),
                        player.getName() // Use the player's name as the suggestor
                );

                ConfigManager.getPendingSuggestions().add(suggestion);
                // Add the suggestion UUID to the player's file
                File playerFile = PlayerManager.getPlayerFile(playerUUID);
                if (playerFile != null) {
                    FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
                    List<String> suggestions = playerConfig.getStringList("suggestions");
                    suggestions.add(suggestion.getUniqueID().toString());
                    playerConfig.set("suggestions", suggestions);

                    try {
                        playerConfig.save(playerFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                        player.sendMessage("§cFailed to save your suggestion.");
                    }

                }
                player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Suggestion pending:" + ChatColor.WHITE + " " + suggestion.getTitle());
                player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8f, 0.8f);
                playersAddingSuggestion.remove(playerUUID);

                ConfigManager.savePendingSuggestions();
                break;

            default:
                player.playSound(player, Sound.ENTITY_VILLAGER_NO, 0.8f, 0.8f);
                player.sendMessage(ChatColor.RED + "Unexpected error occurred.");
                playersAddingSuggestion.remove(playerUUID);
                break;
        }
    }

    public static void adminSuggestionChat(Player player, String message) {
        UUID playerUUID = player.getUniqueId();
        Map<String, String> suggestionData = playersAddingSuggestion.get(playerUUID);

        String stage = suggestionData.get("stage");

        switch (stage) {

            case "title":
                player.playSound(player, Sound.BLOCK_ANVIL_USE, 0.8f, 0.8f);
                suggestionData.put("title", message);
                player.sendMessage(ChatColor.GREEN + "Title set to:" + ChatColor.WHITE + " " + message);

                player.sendMessage(ChatColor.AQUA + "Type the " + ChatColor.BOLD + "description" + ChatColor.AQUA + " of suggestion.");
                player.sendMessage("-----------------------------------------------");
                suggestionData.put("stage", "description");
                break;

            case "description":
                player.playSound(player, Sound.BLOCK_ANVIL_USE, 0.8f, 0.8f);
                suggestionData.put("description", message);
                player.sendMessage(ChatColor.GREEN + "Description set to:" + ChatColor.WHITE + " " + message);
                player.sendMessage(ChatColor.AQUA + "Type " + ChatColor.BOLD + "suggestor's name.");
                player.sendMessage("-----------------------------------------------");
                suggestionData.put("stage", "playerName");
                break;

            case "playerName":
                player.playSound(player, Sound.BLOCK_ANVIL_USE, 0.8f, 0.8f);
                suggestionData.put("playerName", message);
                player.sendMessage(ChatColor.GREEN + "Creator set to:" + ChatColor.WHITE + " " + message);

                Suggestion suggestion = new Suggestion(
                        suggestionData.get("title"),
                        suggestionData.get("description"),
                        suggestionData.get("playerName")
                );
                ConfigManager.getSuggestions().add(suggestion);
                player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Suggestion added:" + ChatColor.WHITE + " " + suggestion.getTitle());
                player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8f, 0.8f);
                playersAddingSuggestion.remove(playerUUID);

                ConfigManager.saveSuggestions();
                break;

            default:
                player.playSound(player, Sound.ENTITY_VILLAGER_NO, 0.8f, 0.8f);
                player.sendMessage(ChatColor.RED + "Unexpected error occurred.");
                playersAddingSuggestion.remove(playerUUID);
                break;
        }
    }


}
