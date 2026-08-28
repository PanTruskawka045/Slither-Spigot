package me.pan_truskawka045.Slither.user;

import com.mongodb.client.model.UpdateOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.pan_truskawka045.Slither.mongodb.MongoDBService;
import me.pan_truskawka045.Slither.skin.WormSkinType;
import org.bson.Document;
import org.bson.types.Binary;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
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
        Document document = userDataDocument(userData).append("name", user.getPlayer().getName());

        executor.execute(() -> mongoDBService.getCollection("users")
                .updateOne(eq("_id", uuid.toString()), new Document("$set", document), new UpdateOptions().upsert(true)));
    }

    public CompletableFuture<byte[]> fetchHeadSkin(URL skinUrl) {
        if (skinUrl == null) {
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.supplyAsync(() -> {
            try (InputStream skinStream = openSkinStream(skinUrl)) {
                BufferedImage skin = ImageIO.read(skinStream);

                if (skin == null) {
                    throw new IOException("Skin URL did not return an image");
                }

                return headSkinFrom(skin);
            } catch (IOException | IllegalArgumentException exception) {
                log.warn("Could not load skin from {}", skinUrl, exception);
                return null;
            }
        }, executor);
    }

    public void updateHeadSkin(UUID uuid, String name, byte[] headSkin) {
        executor.execute(() -> mongoDBService.getCollection("users")
                .updateOne(eq("_id", uuid.toString()), new Document("$set", headSkinUpdateDocument(name, headSkin)), new UpdateOptions().upsert(true)));
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
            userData.setHeadSkin(headSkinFrom(document));
            return userData;
        }, executor);
    }

    public byte[] headSkinFrom(Document document) {
        Binary headSkin = document.get("headSkin", Binary.class);
        return headSkin == null ? null : headSkin.getData();
    }

    public byte[] headSkinFrom(BufferedImage skin) {
        if (skin.getWidth() < 48 || skin.getHeight() < 16) {
            throw new IllegalArgumentException("Skin texture must contain the base and overlay head regions");
        }

        byte[] headSkin = new byte[8 * 8 * 3];

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int basePixel = skin.getRGB(8 + x, 8 + y);
                int overlayPixel = skin.getRGB(40 + x, 8 + y);
                int offset = (y * 8 + x) * 3;

                headSkin[offset] = (byte) compositeChannel(basePixel, overlayPixel, 16);
                headSkin[offset + 1] = (byte) compositeChannel(basePixel, overlayPixel, 8);
                headSkin[offset + 2] = (byte) compositeChannel(basePixel, overlayPixel, 0);
            }
        }

        return headSkin;
    }

    public Document userDataDocument(UserData userData) {
        return new Document("bestScore", userData.getBestScore())
                .append("bestTimeLived", userData.getBestTimeLived())
                .append("selectedSkin", userData.getSelectedSkin().name());
    }

    public Document headSkinUpdateDocument(String name, byte[] headSkin) {
        return new Document("name", name)
                .append("headSkin", headSkin);
    }

    private InputStream openSkinStream(URL skinUrl) throws IOException {
        URLConnection connection = skinUrl.openConnection();
        connection.setConnectTimeout(5_000);
        connection.setReadTimeout(5_000);
        return connection.getInputStream();
    }

    private int compositeChannel(int basePixel, int overlayPixel, int shift) {
        int overlayAlpha = overlayPixel >>> 24;
        return (overlayAlpha == 0 ? basePixel : overlayPixel) >>> shift & 0xFF;
    }

}
