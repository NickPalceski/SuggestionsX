package nick.boptart.suggestionsX.listener;
import nick.boptart.suggestionsX.manager.ConfigManager;
import nick.boptart.suggestionsX.util.listener.ListenerUtil;
import nick.boptart.suggestionsX.util.listener.PendingListenerUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PendingMenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;

        ItemStack clickedItem = event.getCurrentItem();
        String invTitle = ChatColor.stripColor(event.getView().getTitle());
        String menuTitle = ChatColor.stripColor(ConfigManager.getMenuTitle("pending-menu-title"));

        //Check if the title of the inventory is the pending menu title in config
        if (invTitle.startsWith(menuTitle)) {
            event.setCancelled(true);
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            String clickedItemName = clickedItem.getItemMeta() != null
                    ? ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName())
                    : "";

            // Get current page number from the title (assuming last word is the page number)
            Player player = (Player) event.getWhoClicked();

            int page = ListenerUtil.getPageNumberFromTitle(invTitle);
            PendingListenerUtil.handlePageClicks(clickedItemName, page, player);

            ListenerUtil.handleBackButtonClick(clickedItem, player);

            PendingListenerUtil.handleSuggestionClicks(event, clickedItem, player);
        }
    }

}
