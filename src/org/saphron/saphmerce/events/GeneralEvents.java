package org.saphron.saphmerce.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.saphron.saphmerce.Saphmerce;

public class GeneralEvents implements Listener {

    Saphmerce plugin;

    public GeneralEvents(Saphmerce p) { plugin = p; }

    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent e) {
        plugin.getProfileManager().addProfile(e.getUniqueId());

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        plugin.getProfileManager().removeProfile(p.getUniqueId());
    }
}
