package nick.boptart.suggestionsX.menu;

import nick.boptart.suggestionsX.manager.ConfigManager;
import nick.boptart.suggestionsX.util.menu.MenuUtil;
import nick.boptart.suggestionsX.util.menu.PendingMenuUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class PendingMenu {

    public void openPendingSuggestionsMenu(Player player, int page) {
        Inventory pendingMenu = createPendingMenu(page);
        player.openInventory(pendingMenu);
    }

    private Inventory createPendingMenu(int page) {
        int menuSize = 54;
        int pendingSuggestionsSize = ConfigManager.getPendingSuggestions().size();

        String pendingMenuTitle = ChatColor.translateAlternateColorCodes('&', ConfigManager.getMenuTitle("pending-menu-title"));
        Inventory pendingMenu = org.bukkit.Bukkit.createInventory(null, menuSize, pendingMenuTitle + ChatColor.BLACK + " " + page);

        MenuUtil.fillMenuNavigation(menuSize, pendingSuggestionsSize, page, pendingMenu);
        PendingMenuUtil.fillMenuSuggestions(page, pendingMenu);

        return pendingMenu;
    }

}
