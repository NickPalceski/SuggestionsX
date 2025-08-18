package nick.boptart.suggestionsX.util.listener;
import nick.boptart.suggestionsX.menu.*;
import nick.boptart.suggestionsX.manager.ConfigManager;
import nick.boptart.suggestionsX.suggestion.Suggestion;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ListenerUtil {

    public static void handleMainMenuClicks(ItemStack clickedItem, Player player) {

        //Server suggestions menu
        if (clickedItem.getType() == Material.BOOK && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Server Suggestions")) {

            if (player.hasPermission("suggestions.admin")) {
                AdminSuggestionsMenu refreshedMenu = new AdminSuggestionsMenu();
                refreshedMenu.openAdminSuggestionsGUI(player, 1);
            } else {
                SuggestionsMenu refreshedMenu = new SuggestionsMenu();
                refreshedMenu.openPlayerSuggestionsGUI(player, 1);
            }
        }

        // Own suggestions menu
        if (clickedItem.getType() == Material.PLAYER_HEAD && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Your Suggestions")) {

            OwnSuggestionsMenu ownMenu = new OwnSuggestionsMenu();
            ownMenu.openOwnMenu(player, 1);
        }

        // Pending suggestions menu
        if (clickedItem.getType() == Material.YELLOW_TERRACOTTA && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Pending Suggestions")) {
            if (!(player.hasPermission("suggestions.pending.view"))) {
                player.sendMessage(ChatColor.RED + "You do not have permission to view pending suggestions.");
                return;
            }

            PendingMenu refreshedMenu = new PendingMenu();
            refreshedMenu.openPendingSuggestionsMenu(player, 1);
        }
    }

    public static void handleBackButtonClick(ItemStack clickedItem, Player player) {
        if (clickedItem.getType() == Material.OAK_DOOR && clickedItem.getItemMeta().getDisplayName().equals("Go Back")) {
            player.closeInventory();

            if (player.hasPermission("suggestions.admin")) {
                AdminMainMenu mainMenu = new AdminMainMenu();
                mainMenu.openAdminGUI(player);

            } else {
                MainMenu mainMenu = new MainMenu();
                mainMenu.openPlayerGUI(player);
            }
        }
    }

    public static void approveSuggestion(Suggestion clickedSuggestion, Player player) {

        //remove from pending file and save
        ConfigManager.removeSuggestionFromPendingFile(clickedSuggestion);
        ConfigManager.savePendingSuggestionsToFile();

        clickedSuggestion.setStatus(Suggestion.Status.APPROVED); // Update status to approved

        //Add directly to suggestions file and save suggestions
        ConfigManager.addSuggestionToFile(clickedSuggestion);
        ConfigManager.saveSuggestionsToFile();

        //Refresh in-memory lists
        ConfigManager.loadSuggestions();
        ConfigManager.loadPendingSuggestions();

        player.closeInventory();
        PendingMenu refreshedMenu = new PendingMenu();
        refreshedMenu.openPendingSuggestionsMenu(player, 1);
    }

    public static void denySuggestion(Suggestion clickedSuggestion, Player player) {

        //remove from pending file and save
        ConfigManager.removeSuggestionFromPendingFile(clickedSuggestion);
        ConfigManager.savePendingSuggestionsToFile();

        clickedSuggestion.setStatus(Suggestion.Status.DENIED);

        //Refresh in-memory lists
        ConfigManager.loadSuggestions();
        ConfigManager.loadPendingSuggestions();

        player.closeInventory();
        PendingMenu refreshedMenu = new PendingMenu();
        refreshedMenu.openPendingSuggestionsMenu(player, 1);
    }

    public static int getPageNumberFromTitle(String invTitle) {
        int page = 1;
        String[] parts = invTitle.split(" ");
        try {
            page = Integer.parseInt(parts[parts.length - 1]);
        } catch (NumberFormatException ignored) {}
        return page;
    }

}
