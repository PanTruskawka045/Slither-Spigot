package me.pan_truskawka045.Slither.user;

import lombok.RequiredArgsConstructor;
import me.pan_truskawka045.Slither.SpigotSlitherPlugin;
import me.pan_truskawka045.injector.Init;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class UserListener implements Listener {

    private final SpigotSlitherPlugin plugin;
    private final UserStorage userStorage;
    private final SlitherUserFactory slitherUserFactory;
    private final UserService userService;


    @EventHandler(priority = EventPriority.LOWEST)
    public void join(PlayerJoinEvent event) {
        SlitherUser user = slitherUserFactory.createUser(event.getPlayer());

        userStorage.addUser(user);
        userService.onJoin(user, event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void quit(PlayerQuitEvent event) {
        SlitherUser user = userStorage.getUser(event.getPlayer().getUniqueId());

        if (user == null) {
            return;
        }

        userService.onQuit(user, event);
        userStorage.removeUser(event.getPlayer().getUniqueId());
    }

    @Init
    public void init() {
        plugin.registerListener(this);
    }

}
