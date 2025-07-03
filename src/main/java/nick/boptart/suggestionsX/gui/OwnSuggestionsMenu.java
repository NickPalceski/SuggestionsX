package nick.boptart.suggestionsX.gui;

import nick.boptart.suggestionsX.SuggestionsX;
import nick.boptart.suggestionsX.manager.ConfigManager;
import nick.boptart.suggestionsX.manager.PlayerManager;
import nick.boptart.suggestionsX.util.Suggestion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OwnSuggestionsMenu {

    public int page = 1;

    public void openOwnMenu(Player player) {
        Inventory ownMenu = createOwnMenu(player, page);
        player.openInventory(ownMenu);
    }


    public Inventory createOwnMenu(Player player, int page) {
        this.page = page;
        final int size = 54;

        // Fetch the suggestions GUI title from the config
        String title = ChatColor.translateAlternateColorCodes(
                '&', ConfigManager.getConfigManager().getOwnSuggestionsTitle()
                        + ChatColor.BLACK + " " + page);

        // Create the inventory with the title
        Inventory gui = org.bukkit.Bukkit.createInventory(null, size, title);

        ItemStack backButton = new ItemStack(Material.OAK_DOOR);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName("Go Back");
        backButton.setItemMeta(backMeta);
        gui.setItem(size - 5, backButton); // add to middle last row

        //get player UUID and File
        UUID playerUUID = player.getUniqueId();
        File playerFile = PlayerManager.getPlayerFileByName(player.getName());
        if (playerFile == null) {
            player.sendMessage(ChatColor.RED + "Error: Could not find your player file.");
            Bukkit.getLogger().severe("Player file is NULL for " + player.getName() + " (UUID: " + playerUUID + ")");
            return gui;
        }

        // Now safely retrieve the player's suggestions
        List<Suggestion> playerSugg = PlayerManager.getPlayerSuggestions(playerFile);
        if (playerSugg.isEmpty()){
            player.sendMessage(ChatColor.YELLOW + "You have no suggestions.");
        }

        //get all suggestion info
        for (int i = 0; i < playerSugg.size(); i++) {
            Suggestion suggestion = playerSugg.get(i);

            ItemStack ownSugg = new ItemStack(Material.WRITTEN_BOOK);
            ItemMeta ownSuggMeta = ownSugg.getItemMeta();
            ownSuggMeta.setDisplayName(ChatColor.YELLOW + suggestion.getTitle());
            ownSuggMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
            ownSuggMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            List<String> suggDataLore = new ArrayList<>();

            String desc = suggestion.getDescription();

            if (suggestion.getStatus() == 0) {
                suggDataLore.add(ChatColor.LIGHT_PURPLE + "Description: " + ChatColor.GRAY + desc);
                suggDataLore.add(" ");
                suggDataLore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "PENDING");
                suggDataLore.add(ChatColor.WHITE + "Click to delete." + ChatColor.GRAY + " (Left-Click)");
                suggDataLore.add(ChatColor.ITALIC + "Pending suggestions can be removed at any time.");
            }
            else if (suggestion.getStatus()== 1){
                suggDataLore.add(ChatColor.LIGHT_PURPLE + "Description: " + ChatColor.GRAY + desc);
                suggDataLore.add(ChatColor.GREEN + "Up Votes: " + suggestion.getPosVotes());
                suggDataLore.add(ChatColor.RED + "Down Votes: " + suggestion.getNegVotes());
                suggDataLore.add("Total Votes: " + (suggestion.getTotalVotes()));
                suggDataLore.add(" ");
                suggDataLore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "APPROVED");
                suggDataLore.add(ChatColor.WHITE + "Click to delete." + ChatColor.GRAY + " (Left-Click)");
                suggDataLore.add(ChatColor.ITALIC + "Approved suggestions can only be removed by an admin.");

            }
            else{
                suggDataLore.add(ChatColor.LIGHT_PURPLE + "Description: " + ChatColor.GRAY + desc);
                suggDataLore.add(" ");
                suggDataLore.add(ChatColor.DARK_RED + "" + ChatColor.BOLD + "DENIED");
                suggDataLore.add(ChatColor.WHITE + "Click to delete." + ChatColor.GRAY + " (Left-Click)");
                suggDataLore.add(ChatColor.ITALIC + "You will get your suggestion point back.");
            }

            ownSuggMeta.setLore(suggDataLore);
            ownSugg.setItemMeta(ownSuggMeta);
            gui.setItem(i % 45, ownSugg);
        }

        // Add navigation arrows if needed
        if ((size - 9) < (ConfigManager.getSuggestions().size())) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextPageMeta = nextPage.getItemMeta();
            nextPageMeta.setDisplayName(ChatColor.GREEN + "Next Page");
            nextPage.setItemMeta(nextPageMeta);
            gui.setItem(size - 1, nextPage);
        }

        if (page > 1) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta prevPageMeta = prevPage.getItemMeta();
            prevPageMeta.setDisplayName(ChatColor.GREEN + "Last Page");
            prevPage.setItemMeta(prevPageMeta);
            gui.setItem(size - 9, prevPage);
        }

        return gui;

    }

}
