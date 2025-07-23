package nick.boptart.suggestionsX.listener;

import nick.boptart.suggestionsX.SuggestionsX;
import nick.boptart.suggestionsX.gui.AdminMainMenu;
import nick.boptart.suggestionsX.gui.MainMenu;
import nick.boptart.suggestionsX.gui.PendingMenu;
import nick.boptart.suggestionsX.manager.ConfigManager;
import nick.boptart.suggestionsX.suggestion.Suggestion;
import nick.boptart.suggestionsX.util.ListenerUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class PendingMenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        String invTitle = ChatColor.stripColor(event.getView().getTitle());
        ItemStack clickedItem = event.getCurrentItem();
        String pendingMenuTitle = ChatColor.stripColor(ConfigManager.getMenuTitle("pending-menu-title"));

        if (inventory == null) return;

        //Check if the title of the inventory is the pending menu title in config
        if (invTitle.startsWith(pendingMenuTitle)) {
            event.setCancelled(true);
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            ListenerUtil.handlePendingClicks(clickedItem, player, event);
        }
    }

}
