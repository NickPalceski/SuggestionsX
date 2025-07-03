package nick.boptart.suggestionsX.listener;

import nick.boptart.suggestionsX.gui.*;
import nick.boptart.suggestionsX.manager.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MainMenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        //Get player and inventory
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;

        //Get inventory title and clicked item
        String title = ChatColor.stripColor(event.getView().getTitle());
        ItemStack clickedItem = event.getCurrentItem();

        String playerConfigTitle = ChatColor.stripColor(ConfigManager.getMenuTitle("player-menu-title"));
        String adminConfigTitle = ChatColor.stripColor(ConfigManager.getMenuTitle("admin-menu-title"));

        //Check if the title of the inventory is the main menu title in config
        if ((playerConfigTitle.equals(title)) || (adminConfigTitle.equals(title))) {

            event.setCancelled(true);
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            handleMainMenuClicks(clickedItem, player);
        }
    }



    void handleMainMenuClicks(ItemStack clickedItem, Player player) {

        //Server suggestions menu
        if (clickedItem.getType() == Material.BOOK && clickedItem.getItemMeta().getDisplayName().equals("View Suggestions")) {

            if (player.hasPermission("suggestions.admin")) {
                AdminSuggestionsMenu.openAdminSuggestionsGUI(player);
            } else {
                SuggestionsMenu.openPlayerSuggestionsGUI(player);
            }
        }

        // Own suggestions menu
        if (clickedItem.getType() == Material.PLAYER_HEAD && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Your Suggestions")) {

            OwnSuggestionsMenu ownMenu = new OwnSuggestionsMenu();
            ownMenu.openOwnMenu(player);
        }

        // Pending suggestions menu
        if (clickedItem.getType() == Material.YELLOW_TERRACOTTA && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Pending Suggestions")) {
            if (!(player.hasPermission("suggestions.pending.view"))) {
                player.sendMessage(ChatColor.RED + "You do not have permission to view pending suggestions.");
                return;
            }
            // Open the player's suggestions GUI
            PendingMenu.openPendingSuggestionsMenu(player);
        }
    }

}
