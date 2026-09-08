package me.pan_truskawka045.Slither.user;

import com.mongodb.client.model.UpdateOptions;
import lombok.RequiredArgsConstructor;
import me.pan_truskawka045.Slither.SpigotSlitherPlugin;
import me.pan_truskawka045.Slither.mongodb.MongoDBService;
import me.pan_truskawka045.Slither.skin.WormSkinType;
import org.bson.Document;
import org.bson.types.Binary;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.mongodb.client.model.Filters.eq;

@RequiredArgsConstructor
public class UserRepository {

    private static final String USERS_COLLECTION = "users";

    private final MongoDBService mongoDBService;
    private final SpigotSlitherPlugin plugin;

    public void save(SlitherUser user) {
        UUID uuid = user.getPlayer().getUniqueId();
        Document document = userDataDocument(user.getUserData()).append("name", user.getPlayer().getName());

        plugin.getAsyncExecutor().execute(() -> mongoDBService.getCollection(USERS_COLLECTION)
                .updateOne(eq("_id", uuid.toString()), new Document("$set", document), new UpdateOptions().upsert(true)));
    }

    public void updateHeadSkin(UUID uuid, String name, byte[] headSkin) {
        plugin.getAsyncExecutor().execute(() -> mongoDBService.getCollection(USERS_COLLECTION)
                .updateOne(eq("_id", uuid.toString()), new Document("$set", headSkinUpdateDocument(name, headSkin)), new UpdateOptions().upsert(true)));
    }

    public CompletableFuture<UserData> fetch(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            Document document = mongoDBService.getCollection(USERS_COLLECTION)
                    .find(eq("_id", uuid.toString()))
                    .first();

            if (document == null) {
                return new UserData();
            }

            return userDataFrom(document);
        }, plugin.getAsyncExecutor());
    }

    private UserData userDataFrom(Document document) {
        UserData userData = new UserData();
        if (document.containsKey("bestScore")) {
            userData.setBestScore(document.getLong("bestScore"));
        }
        if (document.containsKey("bestTimeLived")) {
            userData.setBestTimeLived(document.getLong("bestTimeLived"));
        }
        if (document.containsKey("selectedSkin")) {
            try {
                userData.setSelectedSkin(WormSkinType.valueOf(document.getString("selectedSkin")));
            } catch (Exception exception) {
                userData.setSelectedSkin(WormSkinType.values()[0]);
            }
        }
        userData.setHeadSkin(headSkinFrom(document));
        return userData;
    }

    private byte[] headSkinFrom(Document document) {
        Object value = document.get("headSkin");
        if (!(value instanceof Binary headSkin)) {
            return null;
        }
        return headSkin.getData();
    }

    private Document userDataDocument(UserData userData) {
        return new Document("bestScore", userData.getBestScore())
                .append("bestTimeLived", userData.getBestTimeLived())
                .append("selectedSkin", userData.getSelectedSkin().name());
    }

    private Document headSkinUpdateDocument(String name, byte[] headSkin) {
        return new Document("name", name)
                .append("headSkin", headSkin);
    }
}
