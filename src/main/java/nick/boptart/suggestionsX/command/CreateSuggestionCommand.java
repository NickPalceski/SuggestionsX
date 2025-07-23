package nick.boptart.suggestionsX.command;

import nick.boptart.suggestionsX.manager.PlayerManager;
import nick.boptart.suggestionsX.suggestion.SuggestionCreation;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.UUID;

public class CreateSuggestionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        // Check if player is already in the process of adding a suggestion
        if (SuggestionCreation.getPlayersAddingSuggestion().containsKey(playerUUID)) {
            player.sendMessage(ChatColor.YELLOW + "You are already in the process of adding a suggestion.");
            return true;
        }

        if (PlayerManager.hasSuggestionPoints(player)){
            SuggestionCreation.startAddingSuggestion(player);
            return true;
        }
        player.sendMessage(ChatColor.RED + "You do not have enough suggestion points to create a suggestion.");
        return false;

    }


}
