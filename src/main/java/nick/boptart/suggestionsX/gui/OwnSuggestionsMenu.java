package nick.boptart.suggestionsX.gui;

import nick.boptart.suggestionsX.manager.ConfigManager;
import nick.boptart.suggestionsX.manager.PlayerManager;
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
import java.util.UUID;

public class OwnSuggestionsMenu {



    public void openOwnMenu(Player player) {
        Inventory ownMenu = ownMenu(player);
        player.openInventory(ownMenu);
    }


    public Inventory ownMenu(Player player) {
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

        //get player UUID
        UUID playerUUID = player.getUniqueId();

        //get player  file and their suggestion's UUID
        List <String> playerSugg= PlayerManager.getPlayerSuggestions(PlayerManager.getPlayerFile(playerUUID));

        //get all suggestion info
        for (int i = startIndex; i < playerSugg.size() && i < endIndex; i++) {
            UUID suggUUID = UUID.fromString(playerSugg.get(i));
            Suggestion suggestion = ConfigManager.getSuggestionByUUID(suggUUID);
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
            }
            else if (suggestion.getStatus()== 1){
                lore.add(ChatColor.LIGHT_PURPLE + "Description: " + ChatColor.GRAY + suggestion.getDescription());
                lore.add(ChatColor.GREEN + "Up Votes: " + suggestion.getPosVotes());
                lore.add(ChatColor.RED + "Down Votes: " + suggestion.getNegVotes());
                lore.add("Total Votes: " + (suggestion.getTotalVotes()));
                lore.add(" ");
                lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "APPROVED");

            }
            else{
                lore.add(ChatColor.LIGHT_PURPLE + "Description: " + ChatColor.GRAY + suggestion.getDescription());
                lore.add(" ");
                lore.add(ChatColor.DARK_RED + "" + ChatColor.BOLD + "DENIED");
                lore.add(ChatColor.WHITE + "Click to remove." + ChatColor.GRAY + " (Left-Click)");
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
