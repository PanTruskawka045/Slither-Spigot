package me.pan_truskawka045.Slither.food;

import lombok.RequiredArgsConstructor;
import me.pan_truskawka045.Slither.util.SpigotUtil;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Location;

@RequiredArgsConstructor
public class FoodFactory {

    private final FoodStorage foodStorage;

    public void create(Location location) {

    }

    public FoodEntity createEntity(Location location, FoodColor color) {
        ServerLevel level = SpigotUtil.getServerLevel(location);
        FoodEntity foodEntity = new FoodEntity(level, SpigotUtil.toVec3(location), color);
        if (!level.tryAddFreshEntityWithPassengers(foodEntity)) {
            throw new IllegalStateException("Could not spawn food entity");
        }
        return foodEntity;
    }

    public Food create(Location location, FoodColor color, FoodReason reason) {
        FoodEntity entity = createEntity(location, color);
        Food food = new Food(location, entity, reason);
        foodStorage.addPoint(food);
        return food;
    }

}
