package me.pan_truskawka045.Slither.game;

import lombok.RequiredArgsConstructor;
import me.pan_truskawka045.Slither.food.FoodStorage;
import me.pan_truskawka045.Slither.skin.WormSkinType;
import me.pan_truskawka045.Slither.user.SlitherUser;
import me.pan_truskawka045.Slither.worm.WormFactory;
import org.bukkit.Location;
import org.bukkit.World;

@RequiredArgsConstructor
public class GameService {

    private final World world;
    private final WormFactory wormFactory;

    public void joinPlayer(SlitherUser user, WormSkinType skinType) {
        double[] position = randomPosOnMap();

        user.getPlayer().teleport(new Location(world, position[0], 115, position[1], -180, 0));

        wormFactory.createWorm(user.getPlayer(), position[0], position[1], skinType);
    }

    private double[] randomPosOnMap() {
        double radius = Math.sqrt(Math.random()) * 253 / 2;
        double angle = Math.random() * 2 * Math.PI;
        return new double[]{radius * Math.cos(angle) + 128.5, radius * Math.sin(angle) + 128.5};
    }

}
