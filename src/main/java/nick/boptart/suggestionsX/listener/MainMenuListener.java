package nick.boptart.suggestionsX.listener;

import nick.boptart.suggestionsX.manager.ConfigManager;
import nick.boptart.suggestionsX.util.listener.ListenerUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MainMenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;

        String invTitle = ChatColor.stripColor(event.getView().getTitle());
        String playerMenuTitle = ChatColor.stripColor(ConfigManager.getMenuTitle("player-menu-title"));
        String adminMenuTitle = ChatColor.stripColor(ConfigManager.getMenuTitle("admin-menu-title"));

        //Check if the title of the inventory is the main menu title in config
        if ((playerMenuTitle.equals(invTitle)) || (adminMenuTitle.equals(invTitle))) {
            ItemStack clickedItem = event.getCurrentItem();
            event.setCancelled(true);

            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            Player player = (Player) event.getWhoClicked();
            ListenerUtil.handleMainMenuClicks(clickedItem, player);
        }
    }
}
