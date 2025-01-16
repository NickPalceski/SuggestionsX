package nick.boptart.suggestionsX.listener;

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

import java.util.Set;
import java.util.UUID;

public class InventoryListener implements Listener {

    private final Set<String> validGuiTitles;

    public InventoryListener(Set<String> validGuiTitles) {
        this.validGuiTitles = validGuiTitles;
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        UUID playerUUID = player.getUniqueId();
        Inventory inventory = event.getClickedInventory();

        if (inventory == null) return;

        String title = ChatColor.stripColor(event.getView().getTitle());
        ItemStack clickedItem = event.getCurrentItem();


        //handle main menu inventory clicks
        if (validGuiTitles.contains(title)) {
            event.setCancelled(true);

            // Your click handling logic here
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            if (clickedItem.getType() == Material.BOOK && clickedItem.getItemMeta().getDisplayName().equals("View Suggestions")) {

                if (player.hasPermission("suggestions.admin")) {
                    AdminSuggestionsMenu.openAdminSuggestionsGUI(player);
                } else {
                    SuggestionsMenu.openPlayerSuggestionsGUI(player);
                }

            } else if (clickedItem.getType() == Material.PLAYER_HEAD && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Your Suggestions")) {

                OwnSuggestionsMenu ownMenu = new OwnSuggestionsMenu();
                ownMenu.openOwnMenu(player);

            } else if (clickedItem.getType() == Material.YELLOW_TERRACOTTA && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Pending Suggestions")) {
                if (!(player.hasPermission("suggestions.admin"))) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to view pending suggestions.");
                    return;
                }
                // Open the player's suggestions GUI
                PendingMenu.openPendingSuggestionsMenu(player);
            }


        }

        // Handle suggestions menu, pending menu, own suggestions menu (checks the title ends with a number (page))
        else if (validGuiTitles.stream().anyMatch(title::startsWith) && title.matches(".*\\d+$")) {
            event.setCancelled(true);

            //handle back to main menu
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

        //handle suggestions menu clicking
        else if (clickedItem.getType() == Material.PAPER && clickedItem.containsEnchantment(Enchantment.UNBREAKING)) {
            ClickType click = event.getClick();

            //get suggestion title.
            String suggestionTitle = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
            //get suggestion UUID
            UUID suggestionUUID = Suggestion.getSuggestionByTitle(suggestionTitle).getUniqueID();

            //admin click handling
            if (player.hasPermission("suggestions.admin")) {
                switch (click) {
                    case LEFT:
                        // TODO: Get clicked suggestion, upvote and increment total votes, update that the player voted for clicked suggestion (remove vote if already voted)
                        break;
                    case RIGHT:
                        // TODO: Get clicked suggestion, downvote and increment total votes, update that the player voted for clicked suggestion (remove vote if already voted)
                        break;
                    case SHIFT_RIGHT:
                        //TODO: Get clicked suggestion, delete suggestion.
                        break;
                    default:
                        player.sendMessage(ChatColor.RED + "Invalid click type.");
                        break;
                }
            }

                //else, player click handling
            switch (click) {
                case LEFT:
                    //TODO: Get clicked suggestion, upvote and increment total votes, update that the player voted for clicked suggestion (remove vote if already voted)
                    break;
                case RIGHT:
                    //TODO: Get clicked suggestion, downvote and increment total votes, update that the player voted for clicked suggestion (remove vote if already voted)
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

                //get suggestion UUID
                UUID suggestionUUID = Suggestion.getSuggestionByTitle(suggestionTitle).getUniqueID();

                switch (click) {
                    case LEFT:


                        //approve suggestion
                        Suggestion.getSuggestionByTitle(suggestionTitle).updateStatus(1);

                        //remove from pending list and add to suggestions list.
                        ConfigManager.getPendingSuggestions().remove(Suggestion.getSuggestionByTitle(suggestionTitle));
                        ConfigManager.getSuggestions().add(Suggestion.getSuggestionByTitle(suggestionTitle));

                        //add suggestion UUID to suggestor's file.
                        PlayerManager.getPlayerFile(PlayerManager.getCreatorUUID(Suggestion.getSuggestionByTitle(suggestionTitle).getCreator()));
                        PlayerManager.addSuggestionToPlayer(Suggestion.getSuggestionByTitle(suggestionTitle), Suggestion.getSuggestionByTitle(suggestionTitle).getCreator());

                        //save changes?
                        ConfigManager.savePendingSuggestions();
                        ConfigManager.saveSuggestions();
                        PlayerManager.savePlayerFile(PlayerManager.getPlayerFile(PlayerManager.getCreatorUUID(Suggestion.getSuggestionByTitle(suggestionTitle).getCreator())));

                        break;
                    case RIGHT:
                        //delete suggestion (reflects in players own suggestions)

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
            //get suggestion UUID
            UUID suggestionUUID = Suggestion.getSuggestionByTitle(suggestionTitle).getUniqueID();

            int currStatus = Suggestion.getSuggestionByTitle(ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName())).getStatus();

            if (click == ClickType.LEFT) {

                //Check suggestion status, perform action based on status.
                switch (currStatus){

                    case 0://pending

                        //remove suggestion from pending suggestions
                        ConfigManager.getPendingSuggestions().remove(Suggestion.getSuggestionByTitle(suggestionTitle));
                        //remove suggestion UUID from suggestor's file
                        PlayerManager.removeSuggestionFromPlayer(Suggestion.getSuggestionByTitle(suggestionTitle), Suggestion.getSuggestionByTitle(suggestionTitle).getCreator());
                        //save changes?
                        ConfigManager.savePendingSuggestions();
                        PlayerManager.savePlayerFile(PlayerManager.getPlayerFile(PlayerManager.getCreatorUUID(Suggestion.getSuggestionByTitle(suggestionTitle).getCreator())));
                        break;

                    case 1://approved
                        //TODO:pend approval for deletion

                        break;

                    case 2://denied

                        //remove suggestion from suggestors file
                        PlayerManager.removeSuggestionFromPlayer(Suggestion.getSuggestionByTitle(suggestionTitle), Suggestion.getSuggestionByTitle(suggestionTitle).getCreator());
                        //refund suggestors point
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



    //end of method
    }






}
