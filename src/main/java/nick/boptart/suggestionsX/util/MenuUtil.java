package nick.boptart.suggestionsX.util;

import nick.boptart.suggestionsX.suggestion.Suggestion;
import nick.boptart.suggestionsX.SuggestionsX;
import nick.boptart.suggestionsX.manager.ConfigManager;
import nick.boptart.suggestionsX.manager.PlayerManager;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MenuUtil {

    private static final SuggestionsX plugin = SuggestionsX.getInstance();

    public static void fillMenuNavigation(int menuSize, int suggestionsSize, int page, Inventory menu) {
        //Back button
        ItemStack backMenu = new ItemStack(Material.OAK_DOOR);
        ItemMeta backMeta = backMenu.getItemMeta();
        backMeta.setDisplayName("Go Back");
        backMenu.setItemMeta(backMeta);
        menu.setItem(menuSize - 5, backMenu); // add to middle last row

        // Add navigation arrows if needed
        int lastRow = menuSize - 9;

        if ((lastRow) < (suggestionsSize - (lastRow)*page)) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextPageMeta = nextPage.getItemMeta();
            nextPageMeta.setDisplayName(ChatColor.GREEN + "Next Page");
            nextPage.setItemMeta(nextPageMeta);
            menu.setItem(menuSize - 1, nextPage);
        }
        if (page > 1) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta prevPageMeta = prevPage.getItemMeta();
            prevPageMeta.setDisplayName(ChatColor.GREEN + "Last Page");
            prevPage.setItemMeta(prevPageMeta);
            menu.setItem(menuSize - 9, prevPage);
        }
    }

    public static void fillAdminMenuWithSuggestions(int menuSize, int suggestionsSize, int page, Inventory adminMenu) {
        int startIndex = (page-1) * (menuSize-9);
        int endIndex = (page) * (menuSize - 9);
        for (int i = startIndex; i < suggestionsSize && i < endIndex; i++) {
            Suggestion suggestion = ConfigManager.getSuggestions().get(i);
            setAdminSuggestionsMeta(i, suggestion, adminMenu);
        }
    }

    private static void setAdminSuggestionsMeta(int index, Suggestion suggestion, Inventory adminMenu) {
        ItemStack suggestionItem = new ItemStack(Material.PAPER);
        ItemMeta suggestionItemMeta = suggestionItem.getItemMeta();
        suggestionItemMeta.setDisplayName(ChatColor.YELLOW + suggestion.getTitle());

        // Store the suggestion UUID inside the item meta (to identify uniquely)
        createSuggestionMetaKey(suggestionItemMeta, suggestion);

        List<String> lore = new ArrayList<>();
        setAdminSuggestionsLore(lore, suggestion);
        handleSuggestionMetaEnchants(suggestionItemMeta);

        suggestionItemMeta.setLore(lore);
        suggestionItem.setItemMeta(suggestionItemMeta);

        adminMenu.setItem(index % 45, suggestionItem);
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
    public static void fillOwnMenuSuggestions(Player player, Inventory ownMenu) {
        File playerFile = PlayerManager.getPlayerFile(player.getUniqueId(), plugin);
        if (!(playerFile == null)) {
            // retrieve the player's suggestions
            List<Suggestion> playerSuggestions = PlayerManager.getPlayerSuggestions(playerFile);

            if (!(playerSuggestions.isEmpty())){
                fillOwnSuggestionsData(playerSuggestions, ownMenu);
            }
            else {
                player.sendMessage(ChatColor.YELLOW + "You have no suggestions.");
            }
        }
        else{
            player.sendMessage(ChatColor.YELLOW + "Could not fetch player file!");
        }
    }

    private static void fillOwnSuggestionsData(List<Suggestion> playerSuggestions, Inventory ownMenu) {
        for (int i = 0; i < playerSuggestions.size(); i++) {
            Suggestion suggestion = playerSuggestions.get(i);
            String suggestionTitle = suggestion.getTitle();
            String suggestionDesc = suggestion.getDescription();
            int suggestionStatus = suggestion.getStatus();

            //Create suggestion item
            ItemStack ownSuggestionItem = new ItemStack(Material.WRITTEN_BOOK);
            ItemMeta ownMeta = ownSuggestionItem.getItemMeta();
            ownMeta.setDisplayName(ChatColor.YELLOW + suggestionTitle);

            // Store the suggestion UUID inside the item meta (to identify uniquely)
            createSuggestionMetaKey(ownMeta, suggestion);

            //Fill suggestion lore
            List<String> ownSuggestionLore = new ArrayList<>();
            switch(suggestionStatus){
                case 0:
                    setPendingOwnSuggestionLore(suggestionDesc, ownSuggestionLore);
                    break;
                case 1:
                    setApprovedOwnSuggestionLore(ownSuggestionLore, suggestion);
                    break;
                case 2:
                    setDeniedOwnSuggestionLore(suggestionDesc, ownSuggestionLore);
                    break;
            }
            handleSuggestionMetaEnchants(ownMeta);

            //Finalize lore
            ownMeta.setLore(ownSuggestionLore);
            ownSuggestionItem.setItemMeta(ownMeta);

            ownMenu.setItem(i % 45, ownSuggestionItem);
        }
    }

    private static void setApprovedOwnSuggestionLore(List<String> lore, Suggestion suggestion) {
        lore.add(ChatColor.LIGHT_PURPLE + "Description: " + ChatColor.GRAY + suggestion.getDescription());
        lore.add(ChatColor.GREEN + "Up Votes: " + suggestion.getPosVotes());
        lore.add(ChatColor.RED + "Down Votes: " + suggestion.getNegVotes());
        lore.add(" ");
        lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "APPROVED");
        lore.add(ChatColor.WHITE + "Click to delete." + ChatColor.GRAY + " (Left-Click)");
        lore.add(ChatColor.ITALIC + "Approved suggestions can only be removed by an admin.");
    }

    private static void setDeniedOwnSuggestionLore(String desc, List<String> lore) {
        lore.add(ChatColor.LIGHT_PURPLE + "Description: " + ChatColor.GRAY + desc);
        lore.add(" ");
        lore.add(ChatColor.DARK_RED + "" + ChatColor.BOLD + "DENIED");
        lore.add(ChatColor.WHITE + "Click to delete." + ChatColor.GRAY + " (Left-Click)");
        lore.add(ChatColor.ITALIC + "You will get your suggestion point back.");
    }

    private static void setPendingOwnSuggestionLore(String desc, List<String> lore) {
        lore.add(ChatColor.LIGHT_PURPLE + "Description: " + ChatColor.GRAY + desc);
        lore.add(" ");
        lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "PENDING");
        lore.add(ChatColor.WHITE + "Click to delete." + ChatColor.GRAY + " (Left-Click)");
        lore.add(ChatColor.ITALIC + "Pending suggestions can be removed at any time.");
    }

    private static void createSuggestionMetaKey(ItemMeta meta, Suggestion suggestion) {
        NamespacedKey key = new NamespacedKey(SuggestionsX.getInstance(), "suggestion_uuid");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, suggestion.getUniqueID().toString());
    }

    private static void handleSuggestionMetaEnchants(ItemMeta meta) {
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    }
    private static void setAdminSuggestionsLore(List<String> lore, Suggestion suggestion) {
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

    public static int getOwnSuggestionsSize(File playerFile){
        if (!(playerFile == null)) {
            return PlayerManager.getPlayerSuggestions(playerFile).size();
        }
        else{
            return 0;
        }
    }

    public static void fillPendingSuggestionsMenu(int menuSize, int suggestionsSize, int page, Inventory pendingMenu){
        int startIndex = (page-1) * (menuSize-9);
        int endIndex = (page) * (menuSize - 9);

        for (int i = startIndex; i < suggestionsSize && i < endIndex; i++) {
            Suggestion suggestion = ConfigManager.getPendingSuggestions().get(i);
            setPendingSuggestionsData(i, suggestion, pendingMenu);
        }
    }

    private static void setPendingSuggestionsData(int index, Suggestion suggestion, Inventory pendingMenu){
        ItemStack pendingSuggestionItem = new ItemStack(Material.TALL_GRASS);
        ItemMeta pendingSuggestionMeta = pendingSuggestionItem.getItemMeta();
        pendingSuggestionMeta.setDisplayName(ChatColor.YELLOW + suggestion.getTitle());

        // Store the suggestion UUID inside the item meta (to identify uniquely)
        createSuggestionMetaKey(pendingSuggestionMeta, suggestion);

        List<String> lore = new ArrayList<>();
        setPendingSuggestionLore(lore, suggestion);

        handleSuggestionMetaEnchants(pendingSuggestionMeta);

        pendingSuggestionMeta.setLore(lore);
        pendingSuggestionItem.setItemMeta(pendingSuggestionMeta);

        pendingMenu.setItem(index % 45, pendingSuggestionItem);
    }

    private static void setPendingSuggestionLore(List<String> lore, Suggestion suggestion){
        lore.add(ChatColor.AQUA + "Suggested by: " + ChatColor.GRAY + suggestion.getCreator());
        lore.add(ChatColor.LIGHT_PURPLE + "Description: " + ChatColor.GRAY + suggestion.getDescription());
        lore.add(" ");
        lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "APPROVE " + ChatColor.GRAY + "(Left-Click)");
        lore.add(ChatColor.RED + "" + ChatColor.BOLD + "DENY " + ChatColor.GRAY + "(Right-Click)");
    }

    public static void fillServerSuggestionsMenu(int menuSize, int suggestionsSize, int page, Inventory suggestionsMenu){
        int startIndex = (page-1) * (menuSize-9);
        int endIndex = (page) * (menuSize - 9);

        for (int i = startIndex; i < suggestionsSize && i < endIndex; i++) {
            Suggestion suggestion = ConfigManager.getSuggestions().get(i);
            setServerSuggestionsData(i, suggestion, suggestionsMenu);
        }
    }

    private static void setServerSuggestionsData(int index, Suggestion suggestion, Inventory suggestionsMenu){
        ItemStack serverSuggestionItem = new ItemStack(Material.PAPER);
        ItemMeta serverSuggestionMeta = serverSuggestionItem.getItemMeta();
        serverSuggestionMeta.setDisplayName(ChatColor.YELLOW + suggestion.getTitle());

        // Store the suggestion UUID inside the item meta (to identify uniquely)
        createSuggestionMetaKey(serverSuggestionMeta, suggestion);

        List<String> lore = new ArrayList<>();
        setServerSuggestionsLore(lore, suggestion);

        handleSuggestionMetaEnchants(serverSuggestionMeta);

        serverSuggestionMeta.setLore(lore);
        serverSuggestionItem.setItemMeta(serverSuggestionMeta);
        suggestionsMenu.setItem(index % 45, serverSuggestionItem);
    }

    private static void setServerSuggestionsLore(List<String> lore, Suggestion suggestion){
        lore.add(ChatColor.AQUA + "Suggested by: " + ChatColor.GRAY + suggestion.getCreator());
        lore.add(ChatColor.LIGHT_PURPLE + "Description: " + ChatColor.GRAY + suggestion.getDescription());
        lore.add(ChatColor.GREEN + "Up Votes: " + suggestion.getPosVotes());
        lore.add(ChatColor.RED + "Down Votes: " + suggestion.getNegVotes());
        lore.add(" ");
        lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "UPVOTE " + ChatColor.GRAY + "(Left-Click)");
        lore.add(ChatColor.RED + "" + ChatColor.BOLD + "DOWNVOTE " + ChatColor.GRAY + "(Right-Click)");
    }
}
