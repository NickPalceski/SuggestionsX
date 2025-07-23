package nick.boptart.suggestionsX.gui;

import nick.boptart.suggestionsX.manager.ConfigManager;
import nick.boptart.suggestionsX.util.MenuUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;


public class AdminMainMenu {

    public void openAdminGUI(Player player) {
        Inventory adminMainMenu = adminMenu(player);
        player.openInventory(adminMainMenu);
    }

    private Inventory adminMenu(Player player) {
        String menuTitle = ConfigManager.getMenuTitle("admin-menu-title");
        Inventory adminMenu = org.bukkit.Bukkit.createInventory(null, 27, menuTitle);

        MenuUtil.fillMainMenu(player,adminMenu);

        return adminMenu;
    }

}
