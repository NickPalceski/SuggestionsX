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

public class SuggestionsMenu {




    public static void openPlayerSuggestionsGUI(Player player) {
        Inventory gui = createPlayerSuggestionsGUI();
        player.openInventory(gui);
    }


    private static Inventory createPlayerSuggestionsGUI() {
        int page = 0;
        int size = 54;

        // Fetch the suggestions GUI title from the config
        String suggestionsGUITitle = ChatColor.translateAlternateColorCodes('&', ConfigManager.getConfigManager().getPlayerSuggestionsTitle());

        // Create the inventory with the title
        Inventory gui = org.bukkit.Bukkit.createInventory(null, size, suggestionsGUITitle + ChatColor.BLACK + " " + (page + 1));

        int startIndex = page * (size-9);
        int endIndex = (page+1) * (size - 9);

        ItemStack backMenu = new ItemStack(Material.OAK_DOOR);
        ItemMeta glassMeta = backMenu.getItemMeta();
        glassMeta.setDisplayName("Go Back");
        backMenu.setItemMeta(glassMeta);
        gui.setItem(size - 5, backMenu); // add to middle last row

        for (int i = startIndex; i < ConfigManager.getSuggestions().size() && i < endIndex; i++) {
            Suggestion suggestion = ConfigManager.getSuggestions().get(i);
            ItemStack paper = new ItemStack(Material.PAPER);
            ItemMeta meta = paper.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW + suggestion.getTitle());
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.AQUA + "Suggested by: " + ChatColor.GRAY + suggestion.getCreator());
            lore.add(ChatColor.LIGHT_PURPLE + "Description: " + ChatColor.GRAY + suggestion.getDescription());
            lore.add(ChatColor.GREEN + "Up Votes: " + suggestion.getPosVotes());
            lore.add(ChatColor.RED + "Down Votes: " + suggestion.getNegVotes());
            lore.add("Total Votes: " + (suggestion.getTotalVotes()));
            lore.add(" ");
            lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "UPVOTE " + ChatColor.GRAY + "(Left-Click)");
            lore.add(ChatColor.RED + "" + ChatColor.BOLD + "DOWNVOTE " + ChatColor.GRAY + "(Right-Click)");


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
