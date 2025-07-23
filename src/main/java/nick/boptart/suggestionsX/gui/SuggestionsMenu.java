package nick.boptart.suggestionsX.gui;

import nick.boptart.suggestionsX.manager.ConfigManager;
import nick.boptart.suggestionsX.util.MenuUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class SuggestionsMenu {

    public static int page = 1;

    public static void openPlayerSuggestionsGUI(Player player) {
        Inventory serverSuggestionsMenu = createPlayerSuggestionsGUI();
        player.openInventory(serverSuggestionsMenu);
    }


    private static Inventory createPlayerSuggestionsGUI() {
        int menuSize = 54;
        int suggestionsSize = ConfigManager.getSuggestions().size();

        String suggestionsMenuTitle = ChatColor.translateAlternateColorCodes('&', ConfigManager.getMenuTitle("server-suggestions-title"));
        Inventory suggestionsMenu = org.bukkit.Bukkit.createInventory(null, menuSize, suggestionsMenuTitle + ChatColor.BLACK + " " + page);

        MenuUtil.fillMenuNavigation(menuSize, menuSize, page, suggestionsMenu);
        MenuUtil.fillServerSuggestionsMenu(menuSize, suggestionsSize, page, suggestionsMenu);

        return suggestionsMenu;
    }

}
