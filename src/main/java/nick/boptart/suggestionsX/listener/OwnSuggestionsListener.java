package nick.boptart.suggestionsX.listener;

import nick.boptart.suggestionsX.gui.AdminMainMenu;
import nick.boptart.suggestionsX.gui.MainMenu;
import nick.boptart.suggestionsX.manager.ConfigManager;
import nick.boptart.suggestionsX.manager.PlayerManager;
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
import org.bukkit.plugin.java.JavaPlugin;

public class OwnSuggestionsListener implements Listener {

    private final JavaPlugin plugin;

    public OwnSuggestionsListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        //Get player and inventory
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;

        //Get inventory title and clicked item
        String title = ChatColor.stripColor(event.getView().getTitle());
        ItemStack clickedItem = event.getCurrentItem();

        String ownSuggestionsTitle = ChatColor.stripColor(ConfigManager.getMenuTitle("own-suggestions-title"));

        //Check if the title of the inventory is the own suggestions menu title in config
        if (title.startsWith(ownSuggestionsTitle)) {
            event.setCancelled(true);
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            handleOwnMenuClicks(clickedItem, player, event);
        }
    }

    private void handleOwnMenuClicks(ItemStack clickedItem, Player player, InventoryClickEvent event) {

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

        //handle own suggestions clicking
        if ((clickedItem.getType() == Material.WRITTEN_BOOK && clickedItem.containsEnchantment(Enchantment.UNBREAKING))){

            ClickType click = event.getClick();
            String suggestionTitle = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
            plugin.getLogger().info("Stripped suggestion title: " + suggestionTitle);
            Suggestion clickedSuggestion = Suggestion.getSuggestionByTitle(suggestionTitle);

            if (clickedSuggestion == null) {
                System.out.println("Error: clickedSuggestion is null for title: " + suggestionTitle);
                player.sendMessage(ChatColor.RED + "Error: Suggestion not found.");
                return;
            }

            System.out.println("Preparing to remove: " + clickedSuggestion.getTitle() + " (UUID: " + clickedSuggestion.getUniqueID() + ")");
            System.out.println("Player File: " + PlayerManager.getPlayerFile(PlayerManager.getCreatorUUID(clickedSuggestion.getCreator()), plugin));

            int currStatus = clickedSuggestion.getStatus();

            if (click == ClickType.LEFT) {
                switch (currStatus) {
                    case 0:  // Pending
                        System.out.println("Removing pending suggestion...");
                        boolean removed = ConfigManager.getPendingSuggestions().remove(clickedSuggestion);
                        System.out.println("Pending suggestions after removal: " + ConfigManager.getPendingSuggestions().size());
                        System.out.println(removed ? "Suggestion removed from pending list." : "Failed to remove suggestion from pending list.");

                        System.out.println("📂 Removing from player file...");
                        PlayerManager.removeSuggestionFromPlayer(clickedSuggestion, clickedSuggestion.getCreator());

                        ConfigManager.savePendingSuggestions();
                        PlayerManager.savePlayerFile(PlayerManager.getPlayerFile(PlayerManager.getCreatorUUID(clickedSuggestion.getCreator()), plugin));
                        break;

                    case 1: // Approved
                        System.out.println("🗑 Removing approved suggestion...");
                        boolean removedApproved = ConfigManager.getSuggestions().remove(clickedSuggestion);
                        System.out.println(removedApproved ? "Suggestion removed from suggestions list." : "Failed to remove suggestion from suggestions list.");

                        PlayerManager.removeSuggestionFromPlayer(clickedSuggestion, clickedSuggestion.getCreator());

                        ConfigManager.saveSuggestions();
                        PlayerManager.savePlayerFile(PlayerManager.getPlayerFile(PlayerManager.getCreatorUUID(clickedSuggestion.getCreator()), plugin));
                        break;

                    case 2: // Denied
                        System.out.println("🗑 Removing denied suggestion...");
                        PlayerManager.removeSuggestionFromPlayer(clickedSuggestion, clickedSuggestion.getCreator());

                        int playerSuggestionCount = PlayerManager.getPlayerSuggestionCount(clickedSuggestion.getCreator());
                        PlayerManager.setPlayerSuggestionCount(clickedSuggestion.getCreator(), playerSuggestionCount + 1);
                        break;

                    default:
                        player.sendMessage(ChatColor.RED + "Invalid suggestion status.");
                        break;
                }
            } else {
                player.sendMessage(ChatColor.RED + "Invalid click type.");

            }
        }
    }
}
