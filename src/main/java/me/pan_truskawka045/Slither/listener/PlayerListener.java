package me.pan_truskawka045.Slither.listener;

import lombok.RequiredArgsConstructor;
import me.pan_truskawka045.Slither.SpigotSlitherPlugin;
import me.pan_truskawka045.injector.Init;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final SpigotSlitherPlugin plugin;

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            event.setCancelled(true);
            player.setFoodLevel(20);
        }
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        event.getPlayer().setFoodLevel(20);
    }

    @Init
    private void init() {
        plugin.registerListener(this);
    }
}
