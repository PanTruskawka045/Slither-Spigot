package me.pan_truskawka045.Slither.user;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserStorage {

    private final Map<UUID, SlitherUser> users = new ConcurrentHashMap<>();

    public synchronized void removeUser(UUID uuid) {
        users.remove(uuid);
    }

    public synchronized void addUser(SlitherUser user) {
        users.put(user.getPlayer().getUniqueId(), user);
    }

    public synchronized SlitherUser getUser(UUID uuid) {
        return users.get(uuid);
    }

    public Collection<SlitherUser> getOnlineUsers() {
        return users.values();
    }
}
