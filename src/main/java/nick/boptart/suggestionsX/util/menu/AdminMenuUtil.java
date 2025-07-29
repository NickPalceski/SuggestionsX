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

public class AdminMenuUtil {

    public static void fillMenuSuggestions(int page, Inventory adminMenu) {
        List<Suggestion> allSuggestions = ConfigManager.getSuggestions();
        int suggestionsPerPage = 45;
        int start = (page - 1) * suggestionsPerPage;
        int end = Math.min(start + suggestionsPerPage, allSuggestions.size());

        List<Suggestion> pageSuggestions = allSuggestions.subList(start, end);

        int slot = 0;
        for (Suggestion suggestion : pageSuggestions) {
            if (slot >= suggestionsPerPage) break;
            setSuggestionMeta(slot, suggestion, adminMenu);
            slot++;
        }
    }

    private static void setSuggestionMeta(int index, Suggestion suggestion, Inventory adminMenu) {
        ItemStack suggestionItem = new ItemStack(Material.PAPER);
        ItemMeta suggestionItemMeta = suggestionItem.getItemMeta();
        suggestionItemMeta.setDisplayName(ChatColor.YELLOW + suggestion.getTitle());

        // Store the suggestion UUID inside the item meta (to identify uniquely)
        MenuUtil.createSuggestionMetaKey(suggestionItemMeta, suggestion);

        List<String> lore = new ArrayList<>();
        setSuggestionsLore(lore, suggestion);
        MenuUtil.handleSuggestionMetaEnchants(suggestionItemMeta);

        suggestionItemMeta.setLore(lore);
        suggestionItem.setItemMeta(suggestionItemMeta);

        adminMenu.setItem(index % 45, suggestionItem);
    }

    private static void setSuggestionsLore(List<String> lore, Suggestion suggestion) {
        lore.add(ChatColor.AQUA + "Suggested by: " + ChatColor.GRAY + suggestion.getCreator());
        lore.add(ChatColor.LIGHT_PURPLE + "Description: " + ChatColor.GRAY + suggestion.getDescription());
        lore.add(ChatColor.GREEN + "Up Votes: " + suggestion.getPosVotes());
        lore.add(ChatColor.RED + "Down Votes: " + suggestion.getNegVotes());
        lore.add(" ");
        lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "UPVOTE " + ChatColor.GRAY + "(Left-Click)");
        lore.add(ChatColor.RED + "" + ChatColor.BOLD + "DOWNVOTE " + ChatColor.GRAY + "(Right-Click)");
        lore.add(" ");
        lore.add(ChatColor.DARK_RED + "" + ChatColor.BOLD + "DELETE " + ChatColor.GRAY + "(Shift + Right-Click)");
    }
}
