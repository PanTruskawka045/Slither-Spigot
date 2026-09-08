package me.pan_truskawka045.Slither.user;

import lombok.extern.log4j.Log4j2;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Log4j2
public class UserService {

    private final List<BiConsumer<SlitherUser, PlayerJoinEvent>> onJoin = new ArrayList<>();
    private final List<BiConsumer<SlitherUser, PlayerQuitEvent>> onQuit = new ArrayList<>();

    public void join(Consumer<SlitherUser> onJoin) {
        this.onJoin.add((user, event) -> onJoin.accept(user));
    }

    public void join(BiConsumer<SlitherUser, PlayerJoinEvent> onJoin) {
        this.onJoin.add(onJoin);
    }

    public void leave(Consumer<SlitherUser> onLeave) {
        quit(onLeave);
    }

    public void leave(BiConsumer<SlitherUser, PlayerQuitEvent> onLeave) {
        quit(onLeave);
    }

    public void quit(Consumer<SlitherUser> onQuit) {
        this.onQuit.add((user, event) -> onQuit.accept(user));
    }

    public void quit(BiConsumer<SlitherUser, PlayerQuitEvent> onQuit) {
        this.onQuit.add(onQuit);
    }

    protected void onJoin(SlitherUser user, PlayerJoinEvent event) {
        for (BiConsumer<SlitherUser, PlayerJoinEvent> consumer : onJoin) {
            try {
                consumer.accept(user, event);
            } catch (Exception e) {
                log.error("Error while executing onJoin consumer for user {}", user.getPlayer().getName(), e);
            }
        }
    }

    protected void onQuit(SlitherUser user, PlayerQuitEvent event) {
        for (BiConsumer<SlitherUser, PlayerQuitEvent> consumer : onQuit) {
            try {
                consumer.accept(user, event);
            } catch (Exception e) {
                log.error("Error while executing onQuit consumer for user {}", user.getPlayer().getName(), e);
            }
        }
    }

}
