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

    public OwnSuggestionsMenu(SuggestionsX plugin) {
        this.plugin = plugin;
    }

    private final SuggestionsX plugin;



    public void openOwnMenu(Player player) {
        Inventory ownMenu = createOwnMenu(player);
        player.openInventory(ownMenu);
    }


    public Inventory createOwnMenu(Player player) {
        int page = 0;
        int size = 54;

        // Fetch the suggestions GUI title from the config
        String title = ChatColor.translateAlternateColorCodes('&', ConfigManager.getConfigManager().getOwnSuggestionsTitle());

        // Create the inventory with the title
        Inventory gui = org.bukkit.Bukkit.createInventory(null, size, title + ChatColor.BLACK + " " + (page + 1));

        int startIndex = page * (size-9);
        int endIndex = (page+1) * (size - 9);

        ItemStack backMenu = new ItemStack(Material.OAK_DOOR);
        ItemMeta glassMeta = backMenu.getItemMeta();
        glassMeta.setDisplayName("Go Back");
        backMenu.setItemMeta(glassMeta);
        gui.setItem(size - 5, backMenu); // add to middle last row

        //get player UUID and File
        UUID playerUUID = player.getUniqueId();
        File playerFile = ConfigManager.getPlayerFile(playerUUID, plugin);
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

            ItemStack paper = new ItemStack(Material.WRITTEN_BOOK);
            ItemMeta meta = paper.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW + suggestion.getTitle());
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            List<String> lore = new ArrayList<>();
            if (suggestion.getStatus() == 0) {
                lore.add(ChatColor.LIGHT_PURPLE + "Description: " + ChatColor.GRAY + suggestion.getDescription());
                lore.add(" ");
                lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "PENDING");
                lore.add(ChatColor.WHITE + "Click to delete." + ChatColor.GRAY + " (Left-Click)");
                lore.add(ChatColor.ITALIC + "Pending suggestions can be removed at any time.");
            }
            else if (suggestion.getStatus()== 1){
                lore.add(ChatColor.LIGHT_PURPLE + "Description: " + ChatColor.GRAY + suggestion.getDescription());
                lore.add(ChatColor.GREEN + "Up Votes: " + suggestion.getPosVotes());
                lore.add(ChatColor.RED + "Down Votes: " + suggestion.getNegVotes());
                lore.add("Total Votes: " + (suggestion.getTotalVotes()));
                lore.add(" ");
                lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "APPROVED");
                lore.add(ChatColor.WHITE + "Click to delete." + ChatColor.GRAY + " (Left-Click)");
                lore.add(ChatColor.ITALIC + "Approved suggestions will have to be approved for removal.");

            }
            else{
                lore.add(ChatColor.LIGHT_PURPLE + "Description: " + ChatColor.GRAY + suggestion.getDescription());
                lore.add(" ");
                lore.add(ChatColor.DARK_RED + "" + ChatColor.BOLD + "DENIED");
                lore.add(ChatColor.WHITE + "Click to delete." + ChatColor.GRAY + " (Left-Click)");
                lore.add(ChatColor.ITALIC + "You will get your suggestion point back.");
            }

            meta.setLore(lore);
            paper.setItemMeta(meta);
            gui.setItem(i % 45, paper);
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
            prevPageMeta.setDisplayName(ChatColor.GREEN + "Last Page");
            prevPage.setItemMeta(prevPageMeta);
            gui.setItem(size - 9, prevPage);
        }

        return gui;

    }

}
