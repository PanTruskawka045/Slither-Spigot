package me.pan_truskawka045.Slither.worm;

import lombok.RequiredArgsConstructor;
import me.pan_truskawka045.Slither.food.FoodStorage;
import me.pan_truskawka045.Slither.skin.WormSkinType;
import me.pan_truskawka045.Slither.util.SpigotUtil;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.World;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class WormFactory {

    private final World world;
    private final FoodStorage foodService;

    public WormEntity createWorm(Player player, double x, double z, WormSkinType skinType) {

        ServerLevel serverLevel = SpigotUtil.getServerLevel(world);

        WormEntity wormEntity = new WormEntity(serverLevel, foodService, skinType.create());
        wormEntity.setPos(x, 101, z);
        serverLevel.addFreshEntity(wormEntity);

        SpigotUtil.getServerPlayer(player).startRiding(wormEntity);

        return wormEntity;
    }

}
