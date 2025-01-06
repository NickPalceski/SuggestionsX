package nick.boptart.suggestionsX.command;

import nick.boptart.suggestionsX.gui.AdminMainMenu;
import nick.boptart.suggestionsX.gui.MainMenu;
import nick.boptart.suggestionsX.SuggestionsX;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SuggestionsCommand implements CommandExecutor {

    private final SuggestionsX plugin;

    public SuggestionsCommand(SuggestionsX plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // Handle main menu command
            if (player.hasPermission("suggestions.admin")) {
                AdminMainMenu gui = new AdminMainMenu();
                gui.openAdminGUI(player);
                return true;
            }

            MainMenu gui = new MainMenu();
            gui.openPlayerGUI(player);
            return true;

        } else if (args[0].equalsIgnoreCase("reload")) {

            return new ReloadSuggestionsCommand(plugin).onCommand(sender, command, label, args);

        } else if (args[0].equalsIgnoreCase("create")) {

            return new CreateSuggestionCommand().onCommand(sender, command, label, args);

        }

        return false;

    }


}
