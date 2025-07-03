package nick.boptart.suggestionsX.listener;

import nick.boptart.suggestionsX.gui.AdminMainMenu;
import nick.boptart.suggestionsX.gui.MainMenu;
import nick.boptart.suggestionsX.manager.ConfigManager;
import nick.boptart.suggestionsX.manager.VoteManager;
import nick.boptart.suggestionsX.util.Suggestion;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ServerSuggestionsListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        //Get player and inventory
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;

        //Get inventory title and clicked item
        String title = ChatColor.stripColor(event.getView().getTitle());
        ItemStack clickedItem = event.getCurrentItem();

        String serverSuggestionsTitle = ChatColor.stripColor(ConfigManager.getMenuTitle("server-suggestions-title"));

        //Check if the title of the inventory is the server suggestions menu title in config
        if (title.startsWith(serverSuggestionsTitle)) {
            event.setCancelled(true);
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            handleServerSuggestionClicks(clickedItem, player, event);
        }
    }

    private void handleServerSuggestionClicks(ItemStack clickedItem, Player player, InventoryClickEvent event) {

        //Handle back button click
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

        //Handle server suggestions click
        if (clickedItem.getType() == Material.PAPER && clickedItem.containsEnchantment(Enchantment.UNBREAKING)) {

            ClickType click = event.getClick();

            //get suggestion title.
            String suggestionTitle = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
            Suggestion clickedSuggestion = Suggestion.getSuggestionByTitle(suggestionTitle);

            switch (click) {
                case LEFT, RIGHT, SHIFT_RIGHT:
                    VoteManager.handleVote(player, clickedSuggestion, click);
                    break;

                default:
                    player.sendMessage(ChatColor.RED + "Invalid click type.");
                    break;
            }
        }
    }
}
