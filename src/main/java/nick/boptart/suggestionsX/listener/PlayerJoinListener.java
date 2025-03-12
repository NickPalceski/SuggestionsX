package nick.boptart.suggestionsX.listener;

import nick.boptart.suggestionsX.manager.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final JavaPlugin plugin;


    public PlayerJoinListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // Fetch player file properly
        if (!ConfigManager.getConfigManager().getPlayerFileCache().containsKey(playerUUID)) {
            System.out.println("New player detected! Creating file...");
            ConfigManager.createPlayerFile(player, plugin);
        }
    }


}
