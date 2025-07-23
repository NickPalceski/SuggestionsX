package nick.boptart.suggestionsX.util;

import nick.boptart.suggestionsX.SuggestionsX;
import nick.boptart.suggestionsX.gui.*;
import nick.boptart.suggestionsX.manager.ConfigManager;
import nick.boptart.suggestionsX.manager.PlayerManager;
import nick.boptart.suggestionsX.manager.VoteManager;
import nick.boptart.suggestionsX.suggestion.Suggestion;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class ListenerUtil {

    private static final SuggestionsX plugin =  SuggestionsX.getInstance();

    public static void handleMainMenuClicks(ItemStack clickedItem, Player player) {

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

            PendingMenu.openPendingSuggestionsMenu(player);
        }
    }

    public static void handleOwnMenuClicks(ItemStack clickedItem, Player player, InventoryClickEvent event) {
        ClickType click = event.getClick();

        handleNavigationClicks(clickedItem, player);
        handleOwnSuggestionClicks(click, clickedItem, player);

    }

    public static void handleNavigationClicks(ItemStack clickedItem, Player player) {
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
        //TODO implement "next page" clicking
    }

    public static void handleOwnSuggestionClicks(ClickType click, ItemStack clickedItem, Player player) {
        //handle own suggestions clicking
        if ((clickedItem.getType() == Material.WRITTEN_BOOK && clickedItem.containsEnchantment(Enchantment.UNBREAKING))){

            //Get clicked suggestion by UUID
            ItemMeta suggestionMeta = clickedItem.getItemMeta();
            NamespacedKey key = new NamespacedKey(SuggestionsX.getInstance(), "suggestion_uuid");

            if (suggestionMeta == null || !suggestionMeta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                player.sendMessage(ChatColor.RED + "Could not identify the suggestion.");
                return;
            }

            String strUUID = suggestionMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
            UUID uuid = UUID.fromString(strUUID);
            Suggestion clickedSuggestion = ConfigManager.getSuggestionByUUID(uuid);

            if (clickedSuggestion == null) {
                player.sendMessage(ChatColor.RED + "Suggestion not found!");
                return;
            }

            int currStatus = clickedSuggestion.getStatus();

            if (click == ClickType.LEFT) {
                switch (currStatus) {
                    case 0:  // Pending
                        handleOwnPendingClick(clickedSuggestion, player);
                        break;

                    case 1: // Approved
                        handleOwnApprovedClick(clickedSuggestion, player);
                        break;

                    case 2: // Denied
                        handleOwnDeniedClick(clickedSuggestion, player);
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

    private static void handleOwnPendingClick(Suggestion clickedSuggestion, Player player) {
        System.out.println("Removing pending suggestion...");
        boolean removed = ConfigManager.getPendingSuggestions().remove(clickedSuggestion);
        System.out.println("Pending suggestions after removal: " + ConfigManager.getPendingSuggestions().size());
        System.out.println(removed ? "Suggestion removed from pending list." : "Failed to remove suggestion from pending list.");

        System.out.println("Removing from player file...");
        PlayerManager.removeSuggestionFromPlayer(clickedSuggestion, clickedSuggestion.getCreator());

        ConfigManager.savePendingSuggestions();
        PlayerManager.savePlayerFile(PlayerManager.getPlayerFile(PlayerManager.getCreatorUUID(clickedSuggestion.getCreator()), plugin));
        player.closeInventory();
        OwnSuggestionsMenu refreshedInv = new OwnSuggestionsMenu();
        refreshedInv.openOwnMenu(player);
    }

    private static void handleOwnApprovedClick(Suggestion clickedSuggestion, Player player) {
        System.out.println("Removing approved suggestion...");
        boolean removedApproved = ConfigManager.getSuggestions().remove(clickedSuggestion);
        System.out.println(removedApproved ? "Suggestion removed from suggestions list." : "Failed to remove suggestion from suggestions list.");

        PlayerManager.removeSuggestionFromPlayer(clickedSuggestion, clickedSuggestion.getCreator());

        ConfigManager.saveSuggestions();
        PlayerManager.savePlayerFile(PlayerManager.getPlayerFile(PlayerManager.getCreatorUUID(clickedSuggestion.getCreator()), plugin));
        OwnSuggestionsMenu refreshedInv1 = new OwnSuggestionsMenu();
        refreshedInv1.openOwnMenu(player);
    }

    private static void handleOwnDeniedClick(Suggestion clickedSuggestion, Player player) {
        System.out.println("Removing denied suggestion...");
        PlayerManager.removeSuggestionFromPlayer(clickedSuggestion, clickedSuggestion.getCreator());

        int playerSuggestionCount = PlayerManager.getPlayerSuggestionCount(clickedSuggestion.getCreator());
        PlayerManager.setPlayerSuggestionCount(clickedSuggestion.getCreator(), playerSuggestionCount + 1);
        OwnSuggestionsMenu refreshedInv2 = new OwnSuggestionsMenu();
        refreshedInv2.openOwnMenu(player);
    }

    public static void handlePendingClicks(ItemStack clickedItem, Player player, InventoryClickEvent event) {

        handleNavigationClicks(clickedItem, player);

        //handle pending suggestion clicking
        if ((clickedItem.getType() == Material.TALL_GRASS && clickedItem.containsEnchantment(Enchantment.UNBREAKING))){

            if (player.hasPermission("suggestions.admin")) {

                //Get clicked suggestion by UUID
                ItemMeta meta = clickedItem.getItemMeta();
                NamespacedKey key = new NamespacedKey(SuggestionsX.getInstance(), "suggestion_uuid");

                if (meta == null || !meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                    player.sendMessage(ChatColor.RED + "Could not identify the suggestion.");
                    return;
                }

                String strUUID = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                UUID uuid = UUID.fromString(strUUID);
                Suggestion clickedSuggestion = ConfigManager.getSuggestionByUUID(uuid);

                if (clickedSuggestion == null) {
                    player.sendMessage(ChatColor.RED + "Suggestion not found!");
                    return;
                }

                ClickType click = event.getClick();

                switch (click) {
                    case LEFT:
                        approveSuggestion(clickedSuggestion, player);
                        break;

                    case RIGHT:
                        denySuggestion(clickedSuggestion, player);
                        break;

                    default:
                        player.sendMessage(ChatColor.RED + "You do not have permission or invalid click type.");
                        break;
                }
            }
        }
    }

    private static void approveSuggestion(Suggestion clickedSuggestion, Player player) {
        clickedSuggestion.updateStatus(1);

        //remove from pending list and add to suggestions list.
        ConfigManager.getPendingSuggestions().remove(clickedSuggestion);
        ConfigManager.removeSuggestionFromPendingConfig(clickedSuggestion);

        ConfigManager.getSuggestions().add(clickedSuggestion);

        //save changes?
        ConfigManager.savePendingSuggestions();
        ConfigManager.saveSuggestions();
        player.closeInventory();
        PendingMenu.openPendingSuggestionsMenu(player);
    }

    private static void denySuggestion(Suggestion clickedSuggestion, Player player) {
        clickedSuggestion.updateStatus(2);

        //remove from pending list
        ConfigManager.getPendingSuggestions().remove(clickedSuggestion);
        ConfigManager.removeSuggestionFromPendingConfig(clickedSuggestion);

        //save changes?
        ConfigManager.savePendingSuggestions();
        ConfigManager.saveSuggestions();
        player.closeInventory();
        PendingMenu.openPendingSuggestionsMenu(player);
    }

    public static void handleServerSuggestionClicks(ItemStack clickedItem, Player player, InventoryClickEvent event) {

        handleNavigationClicks(clickedItem, player);

        //Handle server suggestions click
        if (clickedItem.getType() == Material.PAPER && clickedItem.containsEnchantment(Enchantment.UNBREAKING)) {

            //Get clicked suggestion by UUID
            ItemMeta meta = clickedItem.getItemMeta();
            NamespacedKey key = new NamespacedKey(SuggestionsX.getInstance(), "suggestion_uuid");

            if (meta == null || !meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                player.sendMessage(ChatColor.RED + "Could not identify the suggestion.");
                return;
            }

            String strUUID = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
            UUID uuid = UUID.fromString(strUUID);
            Suggestion clickedSuggestion = ConfigManager.getSuggestionByUUID(uuid);

            if (clickedSuggestion == null) {
                player.sendMessage(ChatColor.RED + "Suggestion not found!");
                return;
            }

            ClickType click = event.getClick();

            switch (click) {
                case LEFT:  //upvote
                    VoteManager.handleUpVote(clickedSuggestion, player);
                    break;

                case RIGHT: //downvote
                    VoteManager.handleDownVote(clickedSuggestion, player);
                    break;

                case SHIFT_RIGHT:   //admin delete
                    VoteManager.handleAdminDelete(clickedSuggestion, player);
                    break;

                default:
                    player.sendMessage(ChatColor.RED + "Invalid click type.");
                    break;
            }
        }
    }
}
