package nick.boptart.suggestionsX.listener;

import nick.boptart.suggestionsX.SuggestionsX;
import nick.boptart.suggestionsX.manager.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final SuggestionsX plugin;

    public PlayerJoinListener(SuggestionsX plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        //TODO: set default values for player in files
        ConfigManager.createPlayerFile(player, plugin);



    }


}
