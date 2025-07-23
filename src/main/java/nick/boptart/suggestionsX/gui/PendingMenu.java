package nick.boptart.suggestionsX.gui;

import nick.boptart.suggestionsX.manager.ConfigManager;
import nick.boptart.suggestionsX.util.MenuUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class PendingMenu {

    public static int page = 1;

    public static void openPendingSuggestionsMenu(Player player) {
        Inventory pendingMenu = createPendingMenu();
        player.openInventory(pendingMenu);
    }

    private static Inventory createPendingMenu() {
        int menuSize = 54;
        int pendingSuggestionsSize = ConfigManager.getPendingSuggestions().size();

        String pendingMenuTitle = ChatColor.translateAlternateColorCodes('&', ConfigManager.getMenuTitle("pending-menu-title"));
        Inventory pendingMenu = org.bukkit.Bukkit.createInventory(null, menuSize, pendingMenuTitle + ChatColor.BLACK + " " + page);

        MenuUtil.fillMenuNavigation(menuSize, pendingSuggestionsSize, page, pendingMenu);
        MenuUtil.fillPendingSuggestionsMenu(menuSize, pendingSuggestionsSize, page, pendingMenu);

        return pendingMenu;
    }

}
