package nick.boptart.suggestionsX.menu;

import nick.boptart.suggestionsX.manager.ConfigManager;
import nick.boptart.suggestionsX.util.menu.AdminMenuUtil;
import nick.boptart.suggestionsX.util.menu.MenuUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class AdminSuggestionsMenu {

    public void openAdminSuggestionsGUI(Player player, int page) {
        Inventory adminSuggestionsMenu = createAdminSuggestionsGUI(page);
        player.openInventory(adminSuggestionsMenu);
    }

    private Inventory createAdminSuggestionsGUI(int page) {
        int menuSize = 54;
        int suggestionsSize = ConfigManager.getSuggestions().size();

        String menuTitle = ChatColor.translateAlternateColorCodes('&', ConfigManager.getMenuTitle("admin-suggestions-title"));
        Inventory adminMenu = org.bukkit.Bukkit.createInventory(null, menuSize, menuTitle + ChatColor.BLACK + " " + (page));

        MenuUtil.fillMenuNavigation(menuSize, suggestionsSize, page, adminMenu);
        AdminMenuUtil.fillMenuSuggestions(page, adminMenu);

        return adminMenu;
    }


}
