package nick.boptart.suggestionsX.suggestion;

import nick.boptart.suggestionsX.SuggestionsX;
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
                        player.getName(), // Use the player's name as the suggester
                        Suggestion.Status.PENDING
                );

                ConfigManager.getPendingSuggestions().add(suggestion);
                ConfigManager.savePendingSuggestionsToFile();

                // Add the suggestion UUID to the player's file
                File playerFile = PlayerManager.getPlayerFile(playerUUID, SuggestionsX.getInstance());

//                //Refresh in-memory list
//                ConfigManager.loadPendingSuggestions();

                if (playerFile != null) {
                    FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
                    List<String> suggestions = playerConfig.getStringList("suggestions");
                    suggestions.add(suggestion.getUniqueID().toString());
                    playerConfig.set("suggestions", suggestions);

                    int suggestionCount = playerConfig.getInt("suggestionsLimit", 0);
                    playerConfig.set("suggestionsLimit", suggestionCount - 1);

                    try {
                        playerConfig.save(playerFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                        player.sendMessage("§cFailed to save your suggestion to player file.");
                    }
                }
                player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Suggestion pending:" + ChatColor.WHITE + " " + suggestion.getTitle());
                player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8f, 0.8f);
                playersAddingSuggestion.remove(playerUUID);
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
                player.sendMessage(ChatColor.AQUA + "Type " + ChatColor.BOLD + "suggester's name (Case Sensitive).");
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
                        suggestionData.get("playerName"),
                        Suggestion.Status.APPROVED);
                ConfigManager.getSuggestions().add(suggestion);

//                //Refresh in-memory list
//                ConfigManager.loadSuggestions();

                File suggesterFile = PlayerManager.getPlayerFileByName(suggestionData.get("playerName"));

                if (suggesterFile != null) {
                    FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(suggesterFile);
                    List<String> playerSuggestions = playerConfig.getStringList("suggestions");
                    playerSuggestions.add(suggestion.getUniqueID().toString());
                    playerConfig.set("suggestions", playerSuggestions);

                    try {
                        playerConfig.save(suggesterFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                        player.sendMessage("§cFailed to save suggestion to suggester.");
                    }

                }
                else{
                    player.sendMessage("§cCould not find suggester's file.");
                }
                playersAddingSuggestion.remove(playerUUID);
                ConfigManager.saveSuggestionsToFile();
                player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Suggestion added to server suggestions:" + ChatColor.WHITE + " " + suggestion.getTitle());
                player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8f, 0.8f);

                break;

            default:
                player.playSound(player, Sound.ENTITY_VILLAGER_NO, 0.8f, 0.8f);
                player.sendMessage(ChatColor.RED + "Unexpected error occurred.");
                playersAddingSuggestion.remove(playerUUID);
                break;
        }
    }


}
