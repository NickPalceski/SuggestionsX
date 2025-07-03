package nick.boptart.suggestionsX.manager;

import org.bukkit.entity.Player;
import nick.boptart.suggestionsX.util.Suggestion;
import org.bukkit.event.inventory.ClickType;

import java.util.UUID;

public class VoteManager {


    public static boolean hasVotedNeg(UUID playerUUID, Suggestion suggestion) {
        return suggestion.getDownVoters().contains(playerUUID); // Returns false if the UUID already exists
    }

    public static boolean hasVotedPos(UUID playerUUID, Suggestion suggestion) {
        return suggestion.getUpVoters().contains(playerUUID);
    }



    public static void handleVote(Player player, Suggestion suggestion, ClickType clickType) {
        UUID playerUUID = player.getUniqueId();

        boolean upVoted = hasVotedPos(playerUUID, suggestion);
        boolean downVoted = hasVotedNeg(playerUUID, suggestion);

        // Admin delete shortcut
        if (clickType == ClickType.SHIFT_RIGHT && player.hasPermission("suggestions.admin")) {
            ConfigManager.getSuggestions().remove(suggestion);
            player.sendMessage("§cYou have deleted this suggestion.");
            return;
        }

        if (clickType == ClickType.LEFT) { // Upvote
            if (upVoted) {
                // Remove upvote
                removePlayerFromUpVoters(playerUUID, suggestion);
                suggestion.decreasePosVotes();
                suggestion.decreaseTotalVotes();
                player.sendMessage("§cRemoved your upvote.");
            } else {
                // Remove downvote if it existed
                if (downVoted) {
                    removePlayerFromDownVoters(playerUUID, suggestion);
                    suggestion.decreaseNegVotes();
                }

                // Add upvote
                addPlayerToUpVoters(playerUUID, suggestion);
                suggestion.increasePosVotes();
                suggestion.increaseTotalVotes(suggestion);
                player.sendMessage("§aYour upvote has been added!");
            }

        } else if (clickType == ClickType.RIGHT) { // Downvote
            if (downVoted) {
                // Remove downvote
                removePlayerFromDownVoters(playerUUID, suggestion);
                suggestion.decreaseNegVotes();
                suggestion.decreaseTotalVotes();
                player.sendMessage("§cRemoved your downvote.");
            } else {
                // Remove upvote if it existed
                if (upVoted) {
                    removePlayerFromUpVoters(playerUUID, suggestion);
                    suggestion.decreasePosVotes();
                }

                // Add downvote
                addPlayerToDownVoters(playerUUID, suggestion);
                suggestion.increaseNegVotes();
                suggestion.increaseTotalVotes(suggestion);
                player.sendMessage("§aYour downvote has been added!");
            }

        } else {
            player.sendMessage("§cInvalid click type.");
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
