package nick.boptart.suggestionsX.util.listener;

import nick.boptart.suggestionsX.SuggestionsX;
import nick.boptart.suggestionsX.menu.PendingMenu;
import nick.boptart.suggestionsX.manager.ConfigManager;
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

public class PendingListenerUtil {

    public static void handleSuggestionClicks(InventoryClickEvent event, ItemStack clickedItem, Player player) {
        //pending suggestion clicking
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
                        ListenerUtil.approveSuggestion(clickedSuggestion, player);
                        break;

                    case RIGHT:
                        ListenerUtil.denySuggestion(clickedSuggestion, player);
                        break;

                    default:
                        player.sendMessage(ChatColor.RED + "You do not have permission or invalid click type.");
                        break;
                }
            }
        }
    }

    public static void handlePageClicks(String clickedItemName, int page, Player player) {
        if ("Next Page".equals(clickedItemName)) {
            player.closeInventory();
            PendingMenu nextPageMenu = new PendingMenu();
            nextPageMenu.openPendingSuggestionsMenu(player, page + 1);

        } else if ("Last Page".equals(clickedItemName)) {
            player.closeInventory();
            PendingMenu prevPageMenu = new PendingMenu();
            prevPageMenu.openPendingSuggestionsMenu(player, page - 1);
        }
    }
}
