package nick.boptart.suggestionsX.util.menu;

import nick.boptart.suggestionsX.SuggestionsX;
import nick.boptart.suggestionsX.manager.PlayerManager;
import nick.boptart.suggestionsX.suggestion.Suggestion;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OwnMenuUtil {

    private static final SuggestionsX plugin = SuggestionsX.getInstance();

    public static void fillMenuSuggestions(Player player, int page, Inventory ownMenu) {
        File playerFile = PlayerManager.getPlayerFile(player.getUniqueId(), plugin);
        List<Suggestion> playerSuggestions = PlayerManager.getPlayerSuggestions(playerFile);

        int suggestionsPerPage = 45;
        int start = (page - 1) * suggestionsPerPage;
        int end = Math.min(start + suggestionsPerPage, playerSuggestions.size());
        List<Suggestion> pageSuggestions = playerSuggestions.subList(start, end);

        int slot = 0;
        for (Suggestion suggestion : pageSuggestions) {
            fillSuggestionData(slot, suggestion, ownMenu); // Your method to add the suggestion to the menu
            slot++;
        }
    }

    public static int getPlayerSuggestionsSize(File playerFile){
        if (!(playerFile == null)) {
            return PlayerManager.getPlayerSuggestions(playerFile).size();
        }
        else{
            return 0;
        }
    }

    private static void fillSuggestionData(int index, Suggestion suggestion, Inventory ownMenu) {
        String suggestionTitle = suggestion.getTitle();
        String suggestionDesc = suggestion.getDescription();
        int suggestionStatus = suggestion.getStatus();

        //Create suggestion item
        ItemStack ownSuggestionItem = new ItemStack(Material.WRITTEN_BOOK);
        ItemMeta ownMeta = ownSuggestionItem.getItemMeta();
        ownMeta.setDisplayName(ChatColor.YELLOW + suggestionTitle);

        // Store the suggestion UUID inside the item meta (to identify uniquely)
        MenuUtil.createSuggestionMetaKey(ownMeta, suggestion);

        //Fill suggestion lore
        List<String> ownSuggestionLore = new ArrayList<>();
        switch(suggestionStatus) {
            case 0:
                setPendingSuggestionLore(suggestionDesc, ownSuggestionLore);
                break;
            case 1:
                setApprovedSuggestionLore(ownSuggestionLore, suggestion);
                break;
            case 2:
                setDeniedSuggestionLore(suggestionDesc, ownSuggestionLore);
                break;
            default:
                ownSuggestionLore.add(ChatColor.RED + "Unknown suggestion status.");
                break;
        }
        MenuUtil.handleSuggestionMetaEnchants(ownMeta);

        //Finalize lore
        ownMeta.setLore(ownSuggestionLore);
        ownSuggestionItem.setItemMeta(ownMeta);

        ownMenu.setItem(index % 45, ownSuggestionItem);
    }

    private static void setApprovedSuggestionLore(List<String> lore, Suggestion suggestion) {
        lore.add(ChatColor.LIGHT_PURPLE + "Description: " + ChatColor.GRAY + suggestion.getDescription());
        lore.add(ChatColor.GREEN + "Up Votes: " + suggestion.getPosVotes());
        lore.add(ChatColor.RED + "Down Votes: " + suggestion.getNegVotes());
        lore.add(" ");
        lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "APPROVED");
        lore.add(ChatColor.WHITE + "Click to delete." + ChatColor.GRAY + " (Left-Click)");
        lore.add(ChatColor.ITALIC + "Approved suggestions can only be removed by an admin.");
    }

    private static void setDeniedSuggestionLore(String desc, List<String> lore) {
        lore.add(ChatColor.LIGHT_PURPLE + "Description: " + ChatColor.GRAY + desc);
        lore.add(" ");
        lore.add(ChatColor.DARK_RED + "" + ChatColor.BOLD + "DENIED");
        lore.add(ChatColor.WHITE + "Click to delete." + ChatColor.GRAY + " (Left-Click)");
        lore.add(ChatColor.ITALIC + "You will get your suggestion point back.");
    }

    private static void setPendingSuggestionLore(String desc, List<String> lore) {
        lore.add(ChatColor.LIGHT_PURPLE + "Description: " + ChatColor.GRAY + desc);
        lore.add(" ");
        lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "PENDING");
        lore.add(ChatColor.WHITE + "Click to delete." + ChatColor.GRAY + " (Left-Click)");
        lore.add(ChatColor.ITALIC + "Pending suggestions can be removed at any time.");
    }

}
