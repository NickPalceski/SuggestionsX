package nick.boptart.suggestionsX.listener;

import nick.boptart.suggestionsX.manager.ConfigManager;
import nick.boptart.suggestionsX.util.ListenerUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ServerSuggestionsListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        String title = ChatColor.stripColor(event.getView().getTitle());
        ItemStack clickedItem = event.getCurrentItem();
        String serverSuggestionsTitle = ChatColor.stripColor(ConfigManager.getMenuTitle("server-suggestions-title"));

        if (inventory == null) return;

        //Check if the title of the inventory is the server suggestions menu title in config
        if (title.startsWith(serverSuggestionsTitle)) {
            event.setCancelled(true);
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            ListenerUtil.handleServerSuggestionClicks(clickedItem, player, event);
        }
    }
}
