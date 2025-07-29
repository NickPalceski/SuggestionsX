package nick.boptart.suggestionsX.util.menu;

import nick.boptart.suggestionsX.manager.ConfigManager;
import nick.boptart.suggestionsX.suggestion.Suggestion;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PendingMenuUtil {

    public static void fillMenuSuggestions(int page, Inventory pendingMenu){
        List<Suggestion> suggestions = ConfigManager.getPendingSuggestions();
        int suggestionsPerPage = 45;
        int start = (page - 1) * suggestionsPerPage;
        int end = Math.min(start + suggestionsPerPage, suggestions.size());
        List<Suggestion> pageSuggestions = suggestions.subList(start, end);

        int slot = 0;
        for (Suggestion suggestion : pageSuggestions) {
            setSuggestionData(slot, suggestion, pendingMenu);
            slot++;
        }
    }

    private static void setSuggestionData(int index, Suggestion suggestion, Inventory pendingMenu){
        ItemStack pendingSuggestionItem = new ItemStack(Material.TALL_GRASS);
        ItemMeta pendingSuggestionMeta = pendingSuggestionItem.getItemMeta();
        pendingSuggestionMeta.setDisplayName(ChatColor.YELLOW + suggestion.getTitle());

        // Store the suggestion UUID inside the item meta (to identify uniquely)
        MenuUtil.createSuggestionMetaKey(pendingSuggestionMeta, suggestion);

        List<String> lore = new ArrayList<>();
        setSuggestionLore(lore, suggestion);

        MenuUtil.handleSuggestionMetaEnchants(pendingSuggestionMeta);

        pendingSuggestionMeta.setLore(lore);
        pendingSuggestionItem.setItemMeta(pendingSuggestionMeta);

        pendingMenu.setItem(index % 45, pendingSuggestionItem);
    }

    private static void setSuggestionLore(List<String> lore, Suggestion suggestion){
        lore.add(ChatColor.AQUA + "Suggested by: " + ChatColor.GRAY + suggestion.getCreator());
        lore.add(ChatColor.LIGHT_PURPLE + "Description: " + ChatColor.GRAY + suggestion.getDescription());
        lore.add(" ");
        lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "APPROVE " + ChatColor.GRAY + "(Left-Click)");
        lore.add(ChatColor.RED + "" + ChatColor.BOLD + "DENY " + ChatColor.GRAY + "(Right-Click)");
    }
}
