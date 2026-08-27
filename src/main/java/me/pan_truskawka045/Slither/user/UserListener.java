package me.pan_truskawka045.Slither.user;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import me.pan_truskawka045.Slither.SpigotSlitherPlugin;
import me.pan_truskawka045.injector.Init;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class UserListener implements Listener {

    private final SpigotSlitherPlugin plugin;
    private final UserStorage userStorage;
    private final SlitherUserFactory slitherUserFactory;
    private final UserService userService;
    private final Cache<UUID, UserData> userDataCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build();

    @EventHandler(priority = EventPriority.LOWEST)
    public void preLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        userService.fetchUserData(uuid).thenAccept(userData -> userDataCache.put(uuid, userData)).join();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void join(PlayerJoinEvent event) {
        SlitherUser user = slitherUserFactory.createUser(event.getPlayer());
        UUID userUuid = event.getPlayer().getUniqueId();
        UserData userData = userDataCache.getIfPresent(userUuid);

        if (userData != null) {
            user.setUserData(userData);
        }
        userDataCache.invalidate(userUuid);

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
