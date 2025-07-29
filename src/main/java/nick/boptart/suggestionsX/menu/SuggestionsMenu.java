package nick.boptart.suggestionsX.menu;

import nick.boptart.suggestionsX.manager.ConfigManager;
import nick.boptart.suggestionsX.util.menu.MenuUtil;
import nick.boptart.suggestionsX.util.menu.ServerMenuUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class SuggestionsMenu {


    public void openPlayerSuggestionsGUI(Player player, int page) {
        Inventory serverSuggestionsMenu = createPlayerSuggestionsGUI(page);
        player.openInventory(serverSuggestionsMenu);
    }


    private Inventory createPlayerSuggestionsGUI(int page) {
        int menuSize = 54;
        int suggestionsSize = ConfigManager.getSuggestions().size();

        String suggestionsMenuTitle = ChatColor.translateAlternateColorCodes('&', ConfigManager.getMenuTitle("server-suggestions-title"));
        Inventory suggestionsMenu = org.bukkit.Bukkit.createInventory(null, menuSize, suggestionsMenuTitle + ChatColor.BLACK + " " + page);

        MenuUtil.fillMenuNavigation(menuSize, suggestionsSize, page, suggestionsMenu);
        ServerMenuUtil.fillMenuSuggestions(page, suggestionsMenu);

        return suggestionsMenu;
    }

}
