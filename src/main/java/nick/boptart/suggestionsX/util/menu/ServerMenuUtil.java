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

public class ServerMenuUtil {

    public static void fillMenuSuggestions(int page, Inventory suggestionsMenu){
        List<Suggestion> suggestions = ConfigManager.getSuggestions();
        int suggestionsPerPage = 45;
        int start = (page - 1) * suggestionsPerPage;
        int end = Math.min(start + suggestionsPerPage, suggestions.size());
        List<Suggestion> pageSuggestions = suggestions.subList(start, end);

        int slot = 0;
        for (Suggestion suggestion : pageSuggestions) {
            setSuggestionData(slot, suggestion, suggestionsMenu); // Implement this method to add the suggestion to the menu
            slot++;
        }
    }

    private static void setSuggestionData(int index, Suggestion suggestion, Inventory suggestionsMenu){
        ItemStack serverSuggestionItem = new ItemStack(Material.PAPER);
        ItemMeta serverSuggestionMeta = serverSuggestionItem.getItemMeta();
        serverSuggestionMeta.setDisplayName(ChatColor.YELLOW + suggestion.getTitle());

        // Store the suggestion UUID inside the item meta (to identify uniquely)
        MenuUtil.createSuggestionMetaKey(serverSuggestionMeta, suggestion);

        List<String> lore = new ArrayList<>();
        setSuggestionLore(lore, suggestion);

        MenuUtil.handleSuggestionMetaEnchants(serverSuggestionMeta);

        serverSuggestionMeta.setLore(lore);
        serverSuggestionItem.setItemMeta(serverSuggestionMeta);
        suggestionsMenu.setItem(index % 45, serverSuggestionItem);
    }

    private static void setSuggestionLore(List<String> lore, Suggestion suggestion){
        lore.add(ChatColor.AQUA + "Suggested by: " + ChatColor.GRAY + suggestion.getCreator());
        lore.add(ChatColor.LIGHT_PURPLE + "Description: " + ChatColor.GRAY + suggestion.getDescription());
        lore.add(ChatColor.GREEN + "Up Votes: " + suggestion.getPosVotes());
        lore.add(ChatColor.RED + "Down Votes: " + suggestion.getNegVotes());
        lore.add(" ");
        lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "UPVOTE " + ChatColor.GRAY + "(Left-Click)");
        lore.add(ChatColor.RED + "" + ChatColor.BOLD + "DOWNVOTE " + ChatColor.GRAY + "(Right-Click)");
    }
}
