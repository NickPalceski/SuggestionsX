package nick.boptart.suggestionsX.util.menu;

import nick.boptart.suggestionsX.suggestion.Suggestion;
import nick.boptart.suggestionsX.SuggestionsX;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

public class MenuUtil {

    public static void fillMenuNavigation(int menuSize, int suggestionsSize, int page, Inventory menu) {
        // Back button
        ItemStack backMenu = new ItemStack(Material.OAK_DOOR);
        ItemMeta backMeta = backMenu.getItemMeta();
        backMeta.setDisplayName("Go Back");
        backMenu.setItemMeta(backMeta);
        menu.setItem(menuSize - 5, backMenu);

        int suggestionsPerPage = menuSize - 9;
        int maxPage = (int) Math.ceil((double) suggestionsSize / suggestionsPerPage);

        // Next Page arrow
        if (page < maxPage) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextPageMeta = nextPage.getItemMeta();
            nextPageMeta.setDisplayName(ChatColor.GREEN + "Next Page");
            nextPage.setItemMeta(nextPageMeta);
            menu.setItem(menuSize - 1, nextPage);
        }

        // Last Page arrow
        if (page > 1) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta prevPageMeta = prevPage.getItemMeta();
            prevPageMeta.setDisplayName(ChatColor.GREEN + "Last Page");
            prevPage.setItemMeta(prevPageMeta);
            menu.setItem(menuSize - 9, prevPage);
        }
    }

    //TODO add material and color to config (so players can change)
    public static void fillMainMenu(Player player, Inventory menu) {
        // Create pending item
        ItemStack pending = new ItemStack(Material.YELLOW_TERRACOTTA, 1);
        ItemMeta pendingMeta = pending.getItemMeta();
        pendingMeta.setDisplayName(ChatColor.GOLD + "Pending Suggestions");
        pending.setItemMeta(pendingMeta);

        // Create server suggestions item
        ItemStack suggestions = new ItemStack(Material.BOOK, 1);
        ItemMeta suggestionsMeta = suggestions.getItemMeta();
        suggestionsMeta.setDisplayName(ChatColor.WHITE + "Suggestions");
        suggestions.setItemMeta(suggestionsMeta);

        // Create own suggestions item
        ItemStack ownSuggestions = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) ownSuggestions.getItemMeta();
        if (headMeta != null) {
            headMeta.setOwningPlayer(player);
            headMeta.setDisplayName(ChatColor.YELLOW + "Your Suggestions");
            ownSuggestions.setItemMeta(headMeta);
        }

        // Add items to the inventory
        menu.setItem(11, pending);
        menu.setItem(13, suggestions);
        menu.setItem(15, ownSuggestions);
    }

    public static void createSuggestionMetaKey(ItemMeta meta, Suggestion suggestion) {
        NamespacedKey key = new NamespacedKey(SuggestionsX.getInstance(), "suggestion_uuid");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, suggestion.getUniqueID().toString());
    }

    public static void handleSuggestionMetaEnchants(ItemMeta meta) {
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    }
}
