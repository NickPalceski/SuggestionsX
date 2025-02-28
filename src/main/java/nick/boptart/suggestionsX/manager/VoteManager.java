package nick.boptart.suggestionsX.manager;

import org.bukkit.entity.Player;
import nick.boptart.suggestionsX.util.Suggestion;
import org.bukkit.event.inventory.ClickType;

import java.util.UUID;

public class VoteManager {


    public static boolean hasNotVoted(UUID playerUUID, Suggestion suggestion) {
        return suggestion.getVoters().add(playerUUID); // Returns false if the UUID already exists
    }

    public static boolean hasVoted(UUID playerUUID, Suggestion suggestion) {
        return suggestion.getVoters().contains(playerUUID);
    }

    public static void handleVote(Player player, Suggestion suggestion, ClickType clickType) {
        UUID playerUUID = player.getUniqueId();

        if (hasVoted(playerUUID, suggestion)) {

            suggestion.getVoters().remove(playerUUID);
            suggestion.decreaseTotalVotes();
            //TODO: somehow figure out if the player upvoted or downvoted and decrease the respective vote count.

            player.sendMessage("§cRemoved your previous vote.");

        } else if (hasNotVoted(playerUUID, suggestion) && clickType == ClickType.LEFT) { //upvote
            suggestion.increasePosVotes();
            suggestion.increaseTotalVotes(suggestion);
            suggestion.getVoters().add(playerUUID);
            player.sendMessage("§aYour upvote has been added!");

        } else if (hasNotVoted(playerUUID, suggestion) && clickType == ClickType.RIGHT) { //downvote
            player.sendMessage("§cFailed to add your vote. Please try again.");
            suggestion.increaseNegVotes();
            suggestion.increaseTotalVotes(suggestion);
            suggestion.getVoters().add(playerUUID);
            player.sendMessage("§aYour downvote has been added!");

        } else if (clickType == ClickType.SHIFT_RIGHT && player.hasPermission("suggestions.admin")) {
            ConfigManager.getSuggestions().remove(suggestion);
            player.sendMessage("§cYou have deleted this suggestion.");

        }else{
            player.sendMessage("§cFailed to register a valid click.");
        }

    }

    public static void removePlayerFromVoters(UUID playerUUID, Suggestion suggestion) {
        suggestion.getVoters().remove(playerUUID);
    }

    public static void addPlayerFromVoters(UUID playerUUID, Suggestion suggestion) {
        suggestion.getVoters().add(playerUUID);
    }



}
