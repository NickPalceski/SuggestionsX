package nick.boptart.suggestionsX.util.listener;

import nick.boptart.suggestionsX.SuggestionsX;
import nick.boptart.suggestionsX.menu.OwnSuggestionsMenu;
import nick.boptart.suggestionsX.manager.ConfigManager;
import nick.boptart.suggestionsX.manager.PlayerManager;
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

public class OwnListenerUtil {

    public static void handleSuggestionClicks(InventoryClickEvent clickEvent, ItemStack clickedItem, Player player) {
        ClickType click = clickEvent.getClick();
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
                        handlePendingClick(clickedSuggestion, player);
                        break;

                    case 1: // Approved
                        handleApprovedClick(clickedSuggestion, player);
                        break;

                    case 2: // Denied
                        handleDeniedClick(clickedSuggestion, player);
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

    private static void handlePendingClick(Suggestion clickedSuggestion, Player player) {
        player.sendMessage(ChatColor.YELLOW + "Removing pending suggestion...");
        // Remove from in memory pending list
        boolean removed = ConfigManager.removeSuggestionByUUID(ConfigManager.getPendingSuggestions(), clickedSuggestion.getUniqueID());
        player.sendMessage(removed ? "Suggestion removed from pending list." : "Failed to remove suggestion from pending list.");
        ConfigManager.savePendingSuggestions();
        // Remove from pending file
        ConfigManager.removeSuggestionFromPendingConfig(clickedSuggestion);

        player.sendMessage(ChatColor.YELLOW + "Removing from player file...");
        PlayerManager.removeSuggestionFromPlayer(clickedSuggestion, clickedSuggestion.getCreator());

        player.closeInventory();
        OwnSuggestionsMenu refreshedInv = new OwnSuggestionsMenu();
        refreshedInv.openOwnMenu(player,1);
    }

    private static void handleApprovedClick(Suggestion clickedSuggestion, Player player) {
        player.sendMessage(ChatColor.YELLOW + "Removing approved suggestion...");
        boolean removedApproved = ConfigManager.removeSuggestionByUUID(ConfigManager.getSuggestions(), clickedSuggestion.getUniqueID());
        player.sendMessage(removedApproved ? "Suggestion removed from suggestions list." : "Failed to remove suggestion from suggestions list.");
        // Saves file's after removal
        ConfigManager.removeSuggestionFromConfig(clickedSuggestion);
        PlayerManager.removeSuggestionFromPlayer(clickedSuggestion, clickedSuggestion.getCreator());
        // Saves in memory suggestions to file
        ConfigManager.saveSuggestionsToFile();
        OwnSuggestionsMenu refreshedInv1 = new OwnSuggestionsMenu();
        refreshedInv1.openOwnMenu(player,1);
    }

    private static void handleDeniedClick(Suggestion clickedSuggestion, Player player) {
        player.sendMessage(ChatColor.YELLOW + "Removing denied suggestion...");
        PlayerManager.removeSuggestionFromPlayer(clickedSuggestion, clickedSuggestion.getCreator());

        int playerSuggestionCount = PlayerManager.getPlayerSuggestionCount(clickedSuggestion.getCreator());
        PlayerManager.setPlayerSuggestionCount(clickedSuggestion.getCreator(), playerSuggestionCount + 1);
        OwnSuggestionsMenu refreshedInv2 = new OwnSuggestionsMenu();
        refreshedInv2.openOwnMenu(player,1);
    }

    public static void handlePageClicks(String clickedItemName, int page, Player player) {
        if ("Next Page".equals(clickedItemName)) {
            player.closeInventory();
            OwnSuggestionsMenu nextPageMenu = new OwnSuggestionsMenu();
            nextPageMenu.openOwnMenu(player, page + 1);

        } else if ("Last Page".equals(clickedItemName)) {
            player.closeInventory();
            OwnSuggestionsMenu prevPageMenu = new OwnSuggestionsMenu();
            prevPageMenu.openOwnMenu(player, page - 1);
        }
    }
}
