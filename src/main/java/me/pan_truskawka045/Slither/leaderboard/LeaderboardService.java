package me.pan_truskawka045.Slither.leaderboard;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import lombok.extern.log4j.Log4j2;
import me.pan_truskawka045.Slither.SpigotSlitherPlugin;
import me.pan_truskawka045.Slither.mongodb.MongoDBService;
import me.pan_truskawka045.Slither.util.BitmapGlyphInfo;
import me.pan_truskawka045.injector.Init;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bson.Document;
import org.bson.types.Binary;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.LongFunction;

@Log4j2
public class LeaderboardService {

    private static final String SURVIVAL_TIME_LEADERBOARD_ID = "survival-time";
    private static final String POINTS_LEADERBOARD_ID = "points";

    private final double PIXEL_WIDTH = 0.025;
    private final double LINE_HEIGHT = PIXEL_WIDTH * 10;

    private final SpigotSlitherPlugin spigotSlitherPlugin;
    private final World world;
    private final MongoDBService mongoDBService;
    private TextDisplay survivalTimeLeaderboard;
    private TextDisplay pointsLeaderboard;
    private final Map<String, List<TextDisplay>> headTextDisplays = new HashMap<>();

    public LeaderboardService(SpigotSlitherPlugin spigotSlitherPlugin, World world, MongoDBService mongoDBService) {
        this.spigotSlitherPlugin = spigotSlitherPlugin;
        this.world = world;
        this.mongoDBService = mongoDBService;
    }

    @Init
    private void init() {
        survivalTimeLeaderboard = spawnDisplay(
                new Location(world, 131.0, 115.0, 265.99, 180, 0),
                Component.text("Survival Time Leaderboard").color(NamedTextColor.GOLD)
        );
        pointsLeaderboard = spawnDisplay(
                new Location(world, 125.0, 115.0, 265.99, 180, 0),
                Component.text("Points Leaderboard").color(NamedTextColor.GOLD)
        );
        headTextDisplays.put(SURVIVAL_TIME_LEADERBOARD_ID, new ArrayList<>());
        headTextDisplays.put(POINTS_LEADERBOARD_ID, new ArrayList<>());

        Bukkit.getScheduler().runTaskTimerAsynchronously(spigotSlitherPlugin, this::refreshLeaderboards, 10 * 20L, 20L * 60 * 5);
    }

    private TextDisplay spawnDisplay(Location location, Component text) {
        world.setChunkForceLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4, true);

        return world.spawn(location, TextDisplay.class, display -> {
            display.setOnlyThisWorldSession(true);
            display.setAlignment(TextDisplay.TextAlignment.LEFT);
            display.setShadowed(false);
            display.setDefaultBackground(false);
            display.setBillboard(Billboard.FIXED);
            display.text(text);
        });
    }

    private void refreshLeaderboards() {

        LeaderboardText survivalTimeText = createLeaderboard(SURVIVAL_TIME_LEADERBOARD_ID, survivalTimeLeaderboard, "Survival Time Leaderboard", "bestTimeLived", this::formatTime);
        LeaderboardText pointsText = createLeaderboard(POINTS_LEADERBOARD_ID, pointsLeaderboard, "Points Leaderboard", "bestScore", this::formatPoints);

        Bukkit.getScheduler().runTask(spigotSlitherPlugin, () -> {
            survivalTimeLeaderboard.text(survivalTimeText.component);
            survivalTimeLeaderboard.setLineWidth(survivalTimeText.width);
            pointsLeaderboard.text(pointsText.component);
            pointsLeaderboard.setLineWidth(pointsText.width);
        });
    }

    private LeaderboardText createLeaderboard(String leaderboardId, TextDisplay leaderboardDisplay, String title, String scoreField, LongFunction<String> scoreFormatter) {
        Component leaderboard = Component.text(title).color(NamedTextColor.GOLD);
        int width = BitmapGlyphInfo.getStringWidth(title);
        int place = 1;

        FindIterable<Document> leaderboardData = mongoDBService.getCollection("users")
                .find(Filters.gte(scoreField, 0L))
                .sort(Sorts.descending(scoreField))
                .limit(10);

        List<byte[]> headSkins = new ArrayList<>();

        for (Document user : leaderboardData) {
            String name = user.getString("name");
            long score = user.getLong(scoreField);
            String record = String.format(Locale.ROOT, "#%02d  . %s: %s", place, name == null ? "Unknown" : name, scoreFormatter.apply(score));

            width = Math.max(width, BitmapGlyphInfo.getStringWidth(record));

            leaderboard = leaderboard.appendNewline()
                    .append(Component.text(String.format(Locale.ROOT, "#%02d", place)).color(placeColor(place)))
                    .append(Component.text("  . "))
                    .append(Component.text(name == null ? "Unknown" : name).color(NamedTextColor.AQUA))
                    .append(Component.text(": ").color(NamedTextColor.GRAY))
                    .append(Component.text(scoreFormatter.apply(score)).color(NamedTextColor.GREEN));

            Binary headSkin = user.get("headSkin", Binary.class);
            headSkins.add(headSkin == null ? new byte[0] : headSkin.getData());

            place++;
        }


        spawnHeads(leaderboardId, leaderboardDisplay, width, headSkins, place);


        return new LeaderboardText(leaderboard, width);
    }

    private void spawnHeads(String leaderboardId, TextDisplay leaderboardDisplay, int width, List<byte[]> headSkins, int place) {
        Bukkit.getScheduler().runTask(spigotSlitherPlugin, () -> {
            List<TextDisplay> leaderboardHeadTextDisplays = headTextDisplays.get(leaderboardId);
            leaderboardHeadTextDisplays.forEach(TextDisplay::remove);
            leaderboardHeadTextDisplays.clear();

            Location location = leaderboardDisplay.getLocation();
            int prefixLength = BitmapGlyphInfo.getStringWidth("#00 ");

            double offset = width * PIXEL_WIDTH / 2;

            offset -= prefixLength * PIXEL_WIDTH;


            for (int i = 0; i < headSkins.size(); i++) {
                double yOffset = (place - 1 - i) * LINE_HEIGHT;

                byte[] head = headSkins.get(i);

                Location base = location.clone().add(offset - PIXEL_WIDTH * 2.5, yOffset - PIXEL_WIDTH, -0.005);

                for (int headX = 0; headX < 8; headX++) {
                    for (int headY = 0; headY < 8; headY++) {
                        int arrayOffset = (headY * 8 + headX) * 3;
                        int pixel = pixelColor(head[arrayOffset], head[arrayOffset + 1], head[arrayOffset + 2]);
                        TextDisplay textDisplay = spawnPixel(base.clone().add(-headX * PIXEL_WIDTH, -headY * PIXEL_WIDTH, 0), pixel, 1.0);
                        leaderboardHeadTextDisplays.add(textDisplay);
                    }
                }
            }
        });
    }


    private String formatPoints(long points) {
        return String.format(Locale.ROOT, "%,d", points);
    }

    static int pixelColor(byte red, byte green, byte blue) {
        return 0xFF000000 | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
    }

    private String formatTime(long ticks) {
        long seconds = ticks / 20;
        return String.format(Locale.ROOT, "%d:%02d", seconds / 60, seconds % 60);
    }

    private NamedTextColor placeColor(int place) {
        return switch (place) {
            case 1 -> NamedTextColor.GOLD;
            case 2 -> NamedTextColor.GRAY;
            case 3 -> NamedTextColor.DARK_AQUA;
            default -> NamedTextColor.WHITE;
        };
    }

    private class LeaderboardText {

        private final Component component;
        private final int width;

        private LeaderboardText(Component component, int width) {
            this.component = component;
            this.width = width;
        }
    }

    private TextDisplay spawnPixel(Location location, int color, double pixelScale) {
        TextDisplay textDisplay = location.getWorld().spawn(location, TextDisplay.class);
        textDisplay.setBackgroundColor(Color.fromRGB(color & 0xFFFFFF));
        textDisplay.text(Component.text(" "));
        textDisplay.setBrightness(new Display.Brightness(15, 15));
        textDisplay.setOnlyThisWorldSession(true);

        Transformation transformation = textDisplay.getTransformation();
        transformation.getScale().set(1 / 5d * pixelScale, 1 / 10d * pixelScale, 1);
        transformation.getTranslation().set(-(3 / 5d) * PIXEL_WIDTH * pixelScale, 0, 0);
        textDisplay.setTransformation(transformation);

        return textDisplay;
    }

}
