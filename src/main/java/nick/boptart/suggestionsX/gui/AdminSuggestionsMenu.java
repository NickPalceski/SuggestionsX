package nick.boptart.suggestionsX.gui;

import nick.boptart.suggestionsX.manager.ConfigManager;
import nick.boptart.suggestionsX.util.MenuUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class AdminSuggestionsMenu {

    private static final int suggestionsSize = ConfigManager.getSuggestions().size();

    public static int page = 1;

    public static void openAdminSuggestionsGUI(Player player) {
        Inventory adminSuggestionsMenu = createAdminSuggestionsGUI();
        player.openInventory(adminSuggestionsMenu);
    }

    private static Inventory createAdminSuggestionsGUI() {
        int menuSize = 54;

        String menuTitle = ChatColor.translateAlternateColorCodes('&', ConfigManager.getMenuTitle("admin-suggestions-title"));
        Inventory adminMenu = org.bukkit.Bukkit.createInventory(null, menuSize, menuTitle + ChatColor.BLACK + " " + (page));

        MenuUtil.fillMenuNavigation(menuSize, suggestionsSize, page, adminMenu);
        MenuUtil.fillAdminMenuWithSuggestions(menuSize, suggestionsSize, page, adminMenu);

        return adminMenu;
    }


}
