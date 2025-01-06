package nick.boptart.suggestionsX.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import nick.boptart.suggestionsX.util.Suggestion;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class VoteManager {


    public boolean hasNotVoted(UUID playerUUID, Suggestion suggestion) {
        return suggestion.getVoters().add(playerUUID); // Returns false if the UUID already exists
    }

    public boolean hasVoted(UUID playerUUID, Suggestion suggestion) {
        return suggestion.getVoters().contains(playerUUID);
    }

    public void handleVote(Player player, Suggestion suggestion) {
        UUID playerUUID = player.getUniqueId();

        if (hasVoted(playerUUID, suggestion)) {
            player.sendMessage("§cYou have already voted for this suggestion!");
            return;
        }

        if (hasNotVoted(playerUUID, suggestion)) {
            player.sendMessage("§aYour vote has been added!");

        } else {
            player.sendMessage("§cFailed to add your vote. Please try again.");
        }
    }

    public void removePlayerFromVoters(UUID playerUUID, Suggestion suggestion) {
        suggestion.getVoters().remove(playerUUID);
    }

    public void addPlayerFromVoters(UUID playerUUID, Suggestion suggestion) {
        suggestion.getVoters().add(playerUUID);
    }



}
