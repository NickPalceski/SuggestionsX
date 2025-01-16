package nick.boptart.suggestionsX.gui;

import nick.boptart.suggestionsX.manager.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class MainMenu {


    public void openPlayerGUI(Player player) {
        Inventory mainMenu = createPlayerMenu(player); // Get the GUI
        player.openInventory(mainMenu);
    }


    private Inventory createPlayerMenu(Player player) {
        String playerGUITitle = ChatColor.translateAlternateColorCodes('&', ConfigManager.getConfigManager().getPlayerMenuTitle());
        Inventory playerGUI = org.bukkit.Bukkit.createInventory(null, 27, playerGUITitle);

        // Create items
        ItemStack suggestions = new ItemStack(Material.BOOK, 1);
        ItemMeta suggestionsMeta = suggestions.getItemMeta();
        suggestionsMeta.setDisplayName("View Suggestions");
        suggestions.setItemMeta(suggestionsMeta);

        // Add the player's head to the GUI
        ItemStack ownSuggestions = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) ownSuggestions.getItemMeta();
        if (headMeta != null) {
            headMeta.setOwningPlayer(player); // Set the head to the current player
            headMeta.setDisplayName(ChatColor.YELLOW + "Your Suggestions");
            ownSuggestions.setItemMeta(headMeta);
        }

        // Add items to the inventory
        playerGUI.setItem(12, suggestions);
        playerGUI.setItem(14, ownSuggestions);

        return playerGUI; // Return the created inventory
    }


}
