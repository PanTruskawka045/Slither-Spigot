package me.pan_truskawka045.Slither.start.menu;

import lombok.RequiredArgsConstructor;
import me.pan_truskawka045.Slither.SpigotSlitherPlugin;
import me.pan_truskawka045.Slither.user.SlitherUser;
import me.pan_truskawka045.Slither.user.UserStorage;
import me.pan_truskawka045.injector.Init;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
public class MenuClickListener implements Listener {

    private final SpigotSlitherPlugin plugin;
    private final UserStorage userStorage;

    @EventHandler(priority = EventPriority.LOWEST)
    public void interact(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        SlitherUser user = userStorage.getUser(event.getPlayer().getUniqueId());

        if (user == null || user.getStartMenuView() == null) {
            return;
        }

        user.getStartMenuView().click();
    }

    @Init
    public void init() {
        plugin.registerListener(this);
    }
}
