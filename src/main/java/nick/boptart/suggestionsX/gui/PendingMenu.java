package nick.boptart.suggestionsX.gui;

import nick.boptart.suggestionsX.manager.ConfigManager;
import nick.boptart.suggestionsX.util.Suggestion;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PendingMenu {


    public static void openPendingSuggestionsMenu(Player player) {
        Inventory gui = createPendingMenu();
        player.openInventory(gui);
    }


    private static Inventory createPendingMenu() {
        int page = 0;
        int size = 54;

        String suggestionsGUITitle = ChatColor.translateAlternateColorCodes('&', ConfigManager.getConfigManager().getPendingMenuTitle());
        Inventory gui = org.bukkit.Bukkit.createInventory(null, size, suggestionsGUITitle + ChatColor.BLACK +" "+ (page + 1));

        int startIndex = page * (size-9);
        int endIndex = (page+1) * (size - 9);

        ItemStack backMenu = new ItemStack(Material.OAK_DOOR);
        ItemMeta glassMeta = backMenu.getItemMeta();
        glassMeta.setDisplayName("Go Back");
        backMenu.setItemMeta(glassMeta);
        gui.setItem(size - 5, backMenu); // add to middle last row

        for (int i = startIndex; i < ConfigManager.getPendingSuggestions().size() && i < endIndex; i++) {
            Suggestion suggestion = ConfigManager.getPendingSuggestions().get(i);
            ItemStack book = new ItemStack(Material.TALL_GRASS);
            ItemMeta meta = book.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW + suggestion.getTitle());
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.AQUA + "Suggested by: " + ChatColor.GRAY + suggestion.getCreator());
            lore.add(ChatColor.LIGHT_PURPLE + "Description: " + ChatColor.GRAY + suggestion.getDescription());
            lore.add(" ");
            lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "APPROVE " + ChatColor.GRAY + "(Left-Click)");
            lore.add(ChatColor.RED + "" + ChatColor.BOLD + "DENY " + ChatColor.GRAY + "(Right-Click)");

            meta.setLore(lore);
            book.setItemMeta(meta);
            gui.setItem(i % 45, book);
        }

        // Add navigation arrows if needed
        if ((size - 9) < (ConfigManager.getSuggestions().size() - (size-9)*page)) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextPageMeta = nextPage.getItemMeta();
            nextPageMeta.setDisplayName(ChatColor.GREEN + "Next Page");
            nextPage.setItemMeta(nextPageMeta);
            gui.setItem(size - 1, nextPage);
        }

        if (page > 0) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta prevPageMeta = prevPage.getItemMeta();
            prevPageMeta.setDisplayName(ChatColor.GREEN + "Previous Page");
            prevPage.setItemMeta(prevPageMeta);
            gui.setItem(size - 9, prevPage);
        }

        return gui;
    }

}
