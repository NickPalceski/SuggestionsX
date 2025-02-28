package nick.boptart.suggestionsX.command;

import nick.boptart.suggestionsX.manager.ConfigManager;
import nick.boptart.suggestionsX.SuggestionsX;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadSuggestionsCommand implements CommandExecutor {

    private final SuggestionsX plugin;

    public ReloadSuggestionsCommand(SuggestionsX plugin) {
        this.plugin = plugin;
    }

    //reload both Config and suggestionData files.
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("suggestions.reload")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        plugin.reloadConfig();
        ConfigManager.getConfigManager().loadConfig();
        sender.sendMessage(ChatColor.GREEN + "SuggestionsX config reloaded!");

        SuggestionsX.getConfigManager().reloadSuggestionDataFiles();
        sender.sendMessage(ChatColor.GREEN + "SuggestionsX suggestion data reloaded!");
        return true;
    }
}
