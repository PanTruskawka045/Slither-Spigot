package me.pan_truskawka045.Slither.worm;

import lombok.RequiredArgsConstructor;
import me.pan_truskawka045.Slither.food.FoodFactory;
import me.pan_truskawka045.Slither.food.FoodStorage;
import me.pan_truskawka045.Slither.game.GameService;
import me.pan_truskawka045.Slither.skin.WormSkinType;
import me.pan_truskawka045.Slither.user.SlitherUser;
import me.pan_truskawka045.Slither.util.SpigotUtil;
import me.pan_truskawka045.injector.Inject;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.World;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class WormFactory {

    private final World world;
    private final FoodFactory foodFactory;
    private final FoodStorage foodService;

    @Inject
    private GameService gameService;

    public WormEntity createWorm(SlitherUser rider, double x, double z, WormSkinType skinType) {

        ServerLevel serverLevel = SpigotUtil.getServerLevel(world);

        WormEntity wormEntity = new WormEntity(rider, serverLevel, foodService, foodFactory, skinType.create(), gameService);
        wormEntity.setPos(x, 101, z);
        serverLevel.addFreshEntity(wormEntity);

        SpigotUtil.getServerPlayer(rider).startRiding(wormEntity);

        return wormEntity;
    }

}
