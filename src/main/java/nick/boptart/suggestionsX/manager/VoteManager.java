package nick.boptart.suggestionsX.manager;

import nick.boptart.suggestionsX.menu.AdminSuggestionsMenu;
import org.bukkit.entity.Player;
import nick.boptart.suggestionsX.suggestion.Suggestion;

import java.util.UUID;

public class VoteManager {

    //TODO implement a cooldown for voting so players can't spam vote

    public static boolean hasVotedNeg(UUID playerUUID, Suggestion suggestion) {
        return suggestion.getDownVoters().contains(playerUUID); // Returns false if the UUID already exists
    }

    public static boolean hasVotedPos(UUID playerUUID, Suggestion suggestion) {
        return suggestion.getUpVoters().contains(playerUUID);
    }

    public static void handleAdminDelete(Suggestion clickedSuggestion, Player player) {
        // Admin delete shortcut
        if (player.hasPermission("suggestions.admin")) {
            // Remove the suggestion from the creators file (saves player file)
            PlayerManager.removeSuggestionFromPlayer(clickedSuggestion, clickedSuggestion.getCreator());

            ConfigManager.removeSuggestionFromFile(clickedSuggestion);
            // Remove the suggestion from the in-memory list?
            ConfigManager.removeSuggestionByUUID(ConfigManager.getSuggestions(), clickedSuggestion.getUniqueID());
            //save suggestions to file (clears file and updates with in-memory list)
            ConfigManager.saveSuggestionsToFile();

            player.sendMessage("§cYou have deleted this suggestion.");
            player.closeInventory();
            AdminSuggestionsMenu refreshedMenu = new AdminSuggestionsMenu();
            refreshedMenu.openAdminSuggestionsGUI(player, 1);
        }
    }

    public static void handleUpVote(Suggestion clickedSuggestion, Player player) {
        UUID playerUUID = player.getUniqueId();

        boolean upVoted = hasVotedPos(playerUUID, clickedSuggestion);
        boolean downVoted = hasVotedNeg(playerUUID, clickedSuggestion);

        if (upVoted) {
            // Remove upvote
            removePlayerFromUpVoters(playerUUID, clickedSuggestion);
            clickedSuggestion.decreasePosVotes();
            clickedSuggestion.decreaseTotalVotes();
            player.sendMessage("§cRemoved your upvote.");
            // save suggestions to file (saves votes and updates UI)
            ConfigManager.saveSuggestionsToFile();
        } else {
            // Remove downvote if it existed
            if (downVoted) {
                removePlayerFromDownVoters(playerUUID, clickedSuggestion);
                clickedSuggestion.decreaseNegVotes();
                clickedSuggestion.decreaseTotalVotes();
                player.sendMessage("§cRemoved your downvote.");
                // save suggestions to file (saves votes and updates UI)
                ConfigManager.saveSuggestionsToFile();
            }

            // Add upvote
            addPlayerToUpVoters(playerUUID, clickedSuggestion);
            clickedSuggestion.increasePosVotes();
            clickedSuggestion.increaseTotalVotes(clickedSuggestion);
            player.sendMessage("§aYour upvote has been added!");
            // save suggestions to file (saves votes and updates UI)
            ConfigManager.saveSuggestionsToFile();
        }
    }

    public static void handleDownVote(Suggestion clickedSuggestion, Player player) {
        UUID playerUUID = player.getUniqueId();

        boolean upVoted = hasVotedPos(playerUUID, clickedSuggestion);
        boolean downVoted = hasVotedNeg(playerUUID, clickedSuggestion);

        if (downVoted) {
            // Remove downvote
            removePlayerFromDownVoters(playerUUID, clickedSuggestion);
            clickedSuggestion.decreaseNegVotes();
            clickedSuggestion.decreaseTotalVotes();
            player.sendMessage("§cRemoved your downvote.");
            // save suggestions to file (saves votes and updates UI)
            ConfigManager.saveSuggestionsToFile();
        } else {
            // Remove upvote if it existed
            if (upVoted) {
                removePlayerFromUpVoters(playerUUID, clickedSuggestion);
                clickedSuggestion.decreasePosVotes();
                clickedSuggestion.decreaseTotalVotes();
                player.sendMessage("§cRemoved your upvote.");
                // save suggestions to file (saves votes and updates UI)
                ConfigManager.saveSuggestionsToFile();
            }

            // Add downvote
            addPlayerToDownVoters(playerUUID, clickedSuggestion);
            clickedSuggestion.increaseNegVotes();
            clickedSuggestion.increaseTotalVotes(clickedSuggestion);
            player.sendMessage("§aYour downvote has been added!");
            // save suggestions to file (saves votes and updates UI)
            ConfigManager.saveSuggestionsToFile();
        }
    }

    public static void removePlayerFromUpVoters(UUID playerUUID, Suggestion suggestion) {
        suggestion.getUpVoters().remove(playerUUID);
    }

    public static void addPlayerToUpVoters(UUID playerUUID, Suggestion suggestion) {
        suggestion.getUpVoters().add(playerUUID);
    }

    public static void removePlayerFromDownVoters(UUID playerUUID, Suggestion suggestion) {
        suggestion.getDownVoters().remove(playerUUID);
    }

    public static void addPlayerToDownVoters(UUID playerUUID, Suggestion suggestion) {
        suggestion.getDownVoters().add(playerUUID);
    }



}
