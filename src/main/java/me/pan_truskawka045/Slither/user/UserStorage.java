package me.pan_truskawka045.Slither.user;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserStorage {

    private final Map<UUID, SlitherUser> users = new ConcurrentHashMap<>();
    private final Map<UUID, SlitherUser> usersInGame = new ConcurrentHashMap<>();

    public synchronized void removeUser(UUID uuid) {
        users.remove(uuid);
        usersInGame.remove(uuid);
    }

    public synchronized void addUser(SlitherUser user) {
        users.put(user.getPlayer().getUniqueId(), user);
    }

    public synchronized SlitherUser getUser(UUID uuid) {
        return users.get(uuid);
    }

    public synchronized boolean inGame(UUID uuid) {
        return usersInGame.containsKey(uuid);
    }

}
