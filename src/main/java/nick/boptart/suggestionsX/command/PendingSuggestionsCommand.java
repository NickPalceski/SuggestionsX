package nick.boptart.suggestionsX.command;

import nick.boptart.suggestionsX.gui.PendingMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PendingSuggestionsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        if (!(player.hasPermission("suggestions.admin"))) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        PendingMenu.openPendingSuggestionsGUI(player);
        return true;

    }
}
