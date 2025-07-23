package nick.boptart.suggestionsX.gui;

import nick.boptart.suggestionsX.SuggestionsX;
import nick.boptart.suggestionsX.manager.ConfigManager;
import nick.boptart.suggestionsX.manager.PlayerManager;
import nick.boptart.suggestionsX.util.MenuUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import java.io.File;
import java.util.UUID;

public class OwnSuggestionsMenu {

    private static final SuggestionsX plugin = SuggestionsX.getInstance();
    public int page = 1;

    public void openOwnMenu(Player player) {
        Inventory ownMenu = createOwnMenu(player, page);
        player.openInventory(ownMenu);
    }


    private Inventory createOwnMenu(Player player, int page) {
        this.page = page;
        int menuSize = 54;

        UUID playerUUID = player.getUniqueId();
        File playerFile = PlayerManager.getPlayerFile(playerUUID, plugin);

        int ownSuggestionsSize = MenuUtil.getOwnSuggestionsSize(playerFile);

        String menuTitle = ChatColor.translateAlternateColorCodes(
                '&', ConfigManager.getMenuTitle("own-suggestions-title")
                        + ChatColor.BLACK + " " + page);
        Inventory ownMenu = Bukkit.createInventory(null, menuSize, menuTitle);

        MenuUtil.fillMenuNavigation(menuSize, ownSuggestionsSize, page, ownMenu);
        MenuUtil.fillOwnMenuSuggestions(player, ownMenu);

        return ownMenu;
    }
}
