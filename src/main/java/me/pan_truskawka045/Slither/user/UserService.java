package me.pan_truskawka045.Slither.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.pan_truskawka045.Slither.mongodb.MongoDBService;
import me.pan_truskawka045.Slither.skin.WormSkinType;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.eq;

@RequiredArgsConstructor
@Log4j2
public class UserService {

    private final Executor executor = Executors.newCachedThreadPool();
    private final List<BiConsumer<SlitherUser, PlayerJoinEvent>> onJoin = new ArrayList<>();
    private final List<BiConsumer<SlitherUser, PlayerQuitEvent>> onQuit = new ArrayList<>();

    private final MongoDBService mongoDBService;

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

    public void saveUser(SlitherUser user) {
        UUID uuid = user.getPlayer().getUniqueId();
        UserData userData = user.getUserData();
        Document document = new Document("_id", uuid.toString())
                .append("bestScore", userData.getBestScore())
                .append("bestTimeLived", userData.getBestTimeLived())
                .append("selectedSkin", userData.getSelectedSkin().name());

        //There should be a mechanism to mark parts of this data as dirty and save only dirty parts.
        executor.execute(() -> mongoDBService.getCollection("users")
                .replaceOne(eq("_id", uuid.toString()), document, new ReplaceOptions().upsert(true)));
    }

    public CompletableFuture<UserData> fetchUserData(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            Document document = mongoDBService.getCollection("users")
                    .find(eq("_id", uuid.toString()))
                    .first();

            if (document == null) {
                return new UserData();
            }

            UserData userData = new UserData();
            userData.setBestScore(document.getLong("bestScore"));
            userData.setBestTimeLived(document.getLong("bestTimeLived"));
            userData.setSelectedSkin(WormSkinType.valueOf(document.getString("selectedSkin")));
            return userData;
        }, executor);
    }

}
