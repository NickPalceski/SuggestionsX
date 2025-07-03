package nick.boptart.suggestionsX.gui;

import nick.boptart.suggestionsX.manager.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class AdminMainMenu {




    public void openAdminGUI(Player player) {
        Inventory gui = adminMenu(player);
        player.openInventory(gui);
    }

    private Inventory adminMenu(Player player) {
        String adminGUITitle = ConfigManager.getMenuTitle("admin-menu-title");
        Inventory adminGUI = org.bukkit.Bukkit.createInventory(null, 27, adminGUITitle);

        // Create items
        ItemStack pending = new ItemStack(Material.YELLOW_TERRACOTTA, 1);
        ItemMeta pendingMeta = pending.getItemMeta();
        pendingMeta.setDisplayName(ChatColor.GOLD + "Pending Suggestions");
        pending.setItemMeta(pendingMeta);

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
        adminGUI.setItem(11, pending);
        adminGUI.setItem(13, suggestions);
        adminGUI.setItem(15, ownSuggestions);

        return adminGUI; // Return the created inventory
    }






}
