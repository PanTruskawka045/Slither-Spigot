package me.pan_truskawka045.Slither.game;

import lombok.RequiredArgsConstructor;
import me.pan_truskawka045.Slither.SpigotSlitherPlugin;
import me.pan_truskawka045.Slither.food.FoodColor;
import me.pan_truskawka045.Slither.food.FoodFactory;
import me.pan_truskawka045.Slither.food.FoodReason;
import me.pan_truskawka045.Slither.food.FoodStorage;
import me.pan_truskawka045.Slither.skin.WormSkinType;
import me.pan_truskawka045.Slither.user.SlitherUser;
import me.pan_truskawka045.Slither.worm.WormFactory;
import me.pan_truskawka045.injector.Init;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

@RequiredArgsConstructor
public class GameService {

    private static final int FOOD_LIMIT = 400;

    private final World world;
    private final WormFactory wormFactory;
    private final FoodFactory foodFactory;
    private final FoodStorage foodStorage;
    private final SpigotSlitherPlugin spigotSlitherPlugin;

    @Init
    private void init() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(spigotSlitherPlugin, this::spawnNaturalFood, 20L, 20L);
    }

    public void joinPlayer(SlitherUser user, WormSkinType skinType) {
        double[] position = randomPosOnMap();

        user.getPlayer().teleport(new Location(world, position[0], 115, position[1], -180, 0));

        wormFactory.createWorm(user, position[0], position[1], skinType);
    }

    public void eliminatePlayer(SlitherUser user) {

        user.getPlayer().teleport(new Location(world, 128.0, 115, 265.0, -180, 0));

    }

    private void spawnNaturalFood() {
        if (foodStorage.getNaturalFoodCount() >= FOOD_LIMIT) {
            return;
        }

        double[] position = randomPosOnMap();
        FoodColor[] colors = FoodColor.values();
        FoodColor color = colors[(int) (Math.random() * colors.length)];
        foodFactory.create(new Location(world, position[0], 101, position[1]), color, FoodReason.NATURAL);
    }

    private double[] randomPosOnMap() {
        double radius = Math.sqrt(Math.random()) * 253 / 2;
        double angle = Math.random() * 2 * Math.PI;
        return new double[]{radius * Math.cos(angle) + 128.5, radius * Math.sin(angle) + 128.5};
    }

}
