package nick.boptart.suggestionsX.listener;

import nick.boptart.suggestionsX.gui.*;
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

public class MainMenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        String menuTitle = ChatColor.stripColor(event.getView().getTitle());
        ItemStack clickedItem = event.getCurrentItem();

        String playerConfigTitle = ChatColor.stripColor(ConfigManager.getMenuTitle("player-menu-title"));
        String adminConfigTitle = ChatColor.stripColor(ConfigManager.getMenuTitle("admin-menu-title"));

        if (inventory == null) return;

        //Check if the title of the inventory is the main menu title in config
        if ((playerConfigTitle.equals(menuTitle)) || (adminConfigTitle.equals(menuTitle))) {

            event.setCancelled(true);
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            ListenerUtil.handleMainMenuClicks(clickedItem, player);
        }
    }
}
