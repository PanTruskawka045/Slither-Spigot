package me.pan_truskawka045.Slither.game;

import lombok.RequiredArgsConstructor;
import me.pan_truskawka045.Slither.SpigotSlitherPlugin;
import me.pan_truskawka045.injector.Init;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;

@RequiredArgsConstructor
public class GameListener implements Listener {

    private final SpigotSlitherPlugin spigotSlitherPlugin;

    @EventHandler
    private void onDismount(EntityDismountEvent event) {
        if (event.getDismounted() instanceof Slime) {
            event.setCancelled(true);
        }
    }

    @Init
    private void init() {
        spigotSlitherPlugin.registerListener(this);
    }


}
