package me.pan_truskawka045.Slither.food;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.world.phys.AABB;
import org.bukkit.Location;

import java.util.*;

public class FoodStorage {

    private final Map<Integer, List<Food>> points = new Int2ObjectOpenHashMap<>();


    public synchronized void addPoint(Food food) {
        int hash = getHash(food);

        points.computeIfAbsent(hash, k -> new ArrayList<>()).add(food);

    }

    public synchronized void removePoint(Food food) {
        int hash = getHash(food);
        List<Food> list = points.get(hash);
        if (list != null) {
            list.remove(food);
            if (list.isEmpty()) {
                points.remove(hash);
            }
        }
    }

    public synchronized List<Food> getAllInAABB(AABB aabb) {

        int minX = (int) Math.max(aabb.minX, 0);
        int maxX = (int) Math.min(aabb.maxX, 255);

        int minZ = (int) Math.max(aabb.minZ, 0);
        int maxZ = (int) Math.min(aabb.maxZ, 255);

        List<Food> list = new LinkedList<>();

        for (int x = minX >> 2; x <= maxX >> 2; x++) {
            for (int z = minZ >> 2; z <= maxZ >> 2; z++) {
                int hash = (x << 6) | z;
                List<Food> foods = points.getOrDefault(hash, Collections.emptyList());
                for (Food food : foods) {
                    Location location = food.getLocation();

                    //Y level is ignored since playing field is 2D
                    if (aabb.contains(location.getX(), aabb.minY, location.getZ())) {
                        list.add(food);
                    }
                }
            }
        }
        return list;
    }

    private int getHash(Food food) {
        int foodX = food.getLocation().getBlockX(), foodZ = food.getLocation().getBlockZ();

        //foodX -> 0-255, foodZ -> 0-255
        return (foodX >> 2) << 6 | foodZ >> 2;
    }

}
