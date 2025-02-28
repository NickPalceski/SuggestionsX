package nick.boptart.suggestionsX.listener;

import nick.boptart.suggestionsX.SuggestionsX;
import nick.boptart.suggestionsX.gui.*;
import nick.boptart.suggestionsX.manager.ConfigManager;
import nick.boptart.suggestionsX.manager.PlayerManager;
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
import org.bukkit.plugin.java.JavaPlugin;


public class InventoryListener implements Listener {

    private final JavaPlugin plugin;

    public InventoryListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        //UUID playerUUID = player.getUniqueId();
        Inventory inventory = event.getClickedInventory();

        if (inventory == null) return;

        String title = ChatColor.stripColor(event.getView().getTitle());
        ItemStack clickedItem = event.getCurrentItem();


        //Check if the title of the inventory is a valid GUI title
        if (ConfigManager.getGUITitles().contains(title)) {

            event.setCancelled(true);
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            handleMainMenuClicks(clickedItem, player);
        }

        // Handle suggestions menu, pending menu, own suggestions menu (checks the title ends with a number (page))
        else if (ConfigManager.getGUITitles().stream().anyMatch(title::startsWith) && title.matches(".*\\d+$")) {

            event.setCancelled(true);
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            handleOtherMenuClicks(clickedItem, player, event);
        }


    }



    void handleMainMenuClicks(ItemStack clickedItem, Player player) {

        //Server suggestions GUI
        if (clickedItem.getType() == Material.BOOK && clickedItem.getItemMeta().getDisplayName().equals("View Suggestions")) {

            if (player.hasPermission("suggestions.admin")) {
                AdminSuggestionsMenu.openAdminSuggestionsGUI(player);
            } else {
                SuggestionsMenu.openPlayerSuggestionsGUI(player);
            }

            // Own suggestions GUI
        } else if (clickedItem.getType() == Material.PLAYER_HEAD && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Your Suggestions")) {

            OwnSuggestionsMenu ownMenu = new OwnSuggestionsMenu(SuggestionsX.getInstance());
            ownMenu.openOwnMenu(player);

            // Pending suggestions GUI
        } else if (clickedItem.getType() == Material.YELLOW_TERRACOTTA && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Pending Suggestions")) {
            //TODO: change permission to something like suggestionsx.pending.view
            if (!(player.hasPermission("suggestions.admin"))) {
                player.sendMessage(ChatColor.RED + "You do not have permission to view pending suggestions.");
                return;
            }
            // Open the player's suggestions GUI
            PendingMenu.openPendingSuggestionsMenu(player);
        }

    }


    void handleOtherMenuClicks(ItemStack clickedItem, Player player, InventoryClickEvent event) {
        if (clickedItem.getType() == Material.OAK_DOOR && clickedItem.getItemMeta().getDisplayName().equals("Go Back")) {
            player.closeInventory();
            //TODO: Change permission to something like suggestionsx.mainmenu.admin
            if (player.hasPermission("suggestions.admin")) {
                AdminMainMenu mainMenu = new AdminMainMenu();
                mainMenu.openAdminGUI(player);
            } else {
                MainMenu mainMenu = new MainMenu();
                mainMenu.openPlayerGUI(player);
            }

        }
        //suggestions menu click
        else if (clickedItem.getType() == Material.PAPER && clickedItem.containsEnchantment(Enchantment.UNBREAKING)) {

            ClickType click = event.getClick();

            //get suggestion title.
            String suggestionTitle = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
            Suggestion suggestion = Suggestion.getSuggestionByTitle(suggestionTitle);

            switch (click) {
                case LEFT, RIGHT, SHIFT_RIGHT:
                    VoteManager.handleVote(player, suggestion, click);
                    break;

                default:
                    player.sendMessage(ChatColor.RED + "Invalid click type.");
                    break;
            }

        }
        //handle pending menu clicking
        else if ((clickedItem.getType() == Material.TALL_GRASS && clickedItem.containsEnchantment(Enchantment.UNBREAKING))){
            ClickType click = event.getClick();

            //admin click handling
            if (player.hasPermission("suggestions.admin")) {
                //get suggestion title.
                String suggestionTitle = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

                switch (click) {
                    case LEFT:
                        //approve suggestion
                        Suggestion.getSuggestionByTitle(suggestionTitle).updateStatus(1);

                        //remove from pending list and add to suggestions list.
                        ConfigManager.getPendingSuggestions().remove(Suggestion.getSuggestionByTitle(suggestionTitle));
                        ConfigManager.getSuggestions().add(Suggestion.getSuggestionByTitle(suggestionTitle));

                        //add suggestion UUID to suggester's file.
                        ConfigManager.getPlayerFile(PlayerManager.getCreatorUUID(Suggestion.getSuggestionByTitle(suggestionTitle).getCreator()), plugin);
                        PlayerManager.addSuggestionToPlayer(Suggestion.getSuggestionByTitle(suggestionTitle), Suggestion.getSuggestionByTitle(suggestionTitle).getCreator());

                        //save changes?
                        ConfigManager.savePendingSuggestions();
                        ConfigManager.saveSuggestions();
                        PlayerManager.savePlayerFile(ConfigManager.getPlayerFile(PlayerManager.getCreatorUUID(Suggestion.getSuggestionByTitle(suggestionTitle).getCreator()), plugin));

                        break;
                    case RIGHT:
                        //deny suggestion
                        Suggestion.getSuggestionByTitle(suggestionTitle).updateStatus(2);

                        //remove from pending list
                        ConfigManager.getPendingSuggestions().remove(Suggestion.getSuggestionByTitle(suggestionTitle));

                        //save changes?
                        ConfigManager.savePendingSuggestions();
                        ConfigManager.saveSuggestions();
                        break;

                    default:
                        player.sendMessage(ChatColor.RED + "You do not have permission or invalid click type.");
                        break;
                }
            }
        }

        //handle own suggestions menu clicking
        else if ((clickedItem.getType() == Material.WRITTEN_BOOK && clickedItem.containsEnchantment(Enchantment.UNBREAKING))){

            ClickType click = event.getClick();
            //get suggestion title.
            String suggestionTitle = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
            plugin.getLogger().info("Stripped suggestion title: " + suggestionTitle);

            int currStatus = Suggestion.getSuggestionByTitle(ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName())).getStatus();

            if (click == ClickType.LEFT) {
                //Check suggestion status, perform action based on status.
                switch (currStatus){
                    case 0:     //pending

                        //remove suggestion from pending suggestions
                        ConfigManager.getPendingSuggestions().remove(Suggestion.getSuggestionByTitle(suggestionTitle));

                        //remove suggestion UUID from suggester's file
                        PlayerManager.removeSuggestionFromPlayer(Suggestion.getSuggestionByTitle(suggestionTitle), Suggestion.getSuggestionByTitle(suggestionTitle).getCreator());

                        //save changes?
                        ConfigManager.savePendingSuggestions();
                        PlayerManager.savePlayerFile(ConfigManager.getPlayerFile(PlayerManager.getCreatorUUID(Suggestion.getSuggestionByTitle(suggestionTitle).getCreator()), plugin));
                        break;

                    case 1:     //approved

                        //TODO: Pend approval for deletion (delete instantly for now)
                        ConfigManager.getSuggestions().remove(Suggestion.getSuggestionByTitle(suggestionTitle));

                        //remove suggestion UUID from suggester's file
                        PlayerManager.removeSuggestionFromPlayer(Suggestion.getSuggestionByTitle(suggestionTitle), Suggestion.getSuggestionByTitle(suggestionTitle).getCreator());

                        //save changes?
                        ConfigManager.savePendingSuggestions();
                        PlayerManager.savePlayerFile(ConfigManager.getPlayerFile(PlayerManager.getCreatorUUID(Suggestion.getSuggestionByTitle(suggestionTitle).getCreator()), plugin));

                        break;

                    case 2:     //denied

                        //remove suggestion from suggesters file
                        PlayerManager.removeSuggestionFromPlayer(Suggestion.getSuggestionByTitle(suggestionTitle), Suggestion.getSuggestionByTitle(suggestionTitle).getCreator());

                        //refund suggesters point
                        int playerSuggestionCount = PlayerManager.getPlayerSuggestionCount(String.valueOf(player));
                        PlayerManager.setPlayerSuggestionCount(String.valueOf(player), playerSuggestionCount + 1);
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
