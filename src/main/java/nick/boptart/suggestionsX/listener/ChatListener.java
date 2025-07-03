package nick.boptart.suggestionsX.listener;

import nick.boptart.suggestionsX.util.SuggestionCreation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (!(SuggestionCreation.getPlayersAddingSuggestion().containsKey(playerUUID))) {
            return;
        }

        event.setCancelled(true);
        if (player.hasPermission("suggestions.admin.create")) {
            SuggestionCreation.adminSuggestionChat(player, event.getMessage());
            return;
        }

        SuggestionCreation.playerSuggestionChat(player, event.getMessage());
    }


}
