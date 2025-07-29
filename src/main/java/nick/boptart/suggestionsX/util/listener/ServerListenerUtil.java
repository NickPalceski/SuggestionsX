package nick.boptart.suggestionsX.util.listener;

import nick.boptart.suggestionsX.SuggestionsX;
import nick.boptart.suggestionsX.menu.SuggestionsMenu;
import nick.boptart.suggestionsX.manager.ConfigManager;
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

public class ServerListenerUtil {

    public static void handleSuggestionClicks(ItemStack clickedItem, Player player, InventoryClickEvent event) {
        //server suggestions click
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

    public static void handlePageClicks(String clickedItemName, int page, Player player) {
        if ("Next Page".equals(clickedItemName)) {
            player.closeInventory();
            SuggestionsMenu nextPageMenu = new SuggestionsMenu();
            nextPageMenu.openPlayerSuggestionsGUI(player, page + 1);

        } else if ("Last Page".equals(clickedItemName)) {
            player.closeInventory();
            SuggestionsMenu prevPageMenu = new SuggestionsMenu();
            prevPageMenu.openPlayerSuggestionsGUI(player, page - 1);
        }
    }
}
