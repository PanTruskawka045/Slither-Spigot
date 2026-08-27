package me.pan_truskawka045.Slither.food;

import lombok.Getter;
import org.bukkit.Location;

@Getter
public class Food {

    private final Location location;
    private final FoodEntity entity;
    private final FoodReason reason;

    public Food(Location location, FoodEntity entity, FoodReason reason) {
        this.location = location.clone();
        this.entity = entity;
        this.reason = reason;
    }

}
