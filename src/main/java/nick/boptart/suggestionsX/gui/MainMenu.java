package nick.boptart.suggestionsX.gui;

import nick.boptart.suggestionsX.manager.ConfigManager;
import nick.boptart.suggestionsX.util.MenuUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MainMenu {

    public void openPlayerGUI(Player player) {
        Inventory mainMenu = createPlayerMenu(player);
        player.openInventory(mainMenu);
    }

    private Inventory createPlayerMenu(Player player) {
        String menuTitle = ConfigManager.getMenuTitle("player-menu-title");
        Inventory mainMenu = org.bukkit.Bukkit.createInventory(null, 27, menuTitle);

        MenuUtil.fillMainMenu(player, mainMenu);

        return mainMenu;
    }


}
