package me.pan_truskawka045.Slither.worm;

import lombok.Getter;
import me.pan_truskawka045.Slither.food.FoodColor;
import me.pan_truskawka045.Slither.food.FoodFactory;
import me.pan_truskawka045.Slither.food.FoodReason;
import me.pan_truskawka045.Slither.skin.AbstractWormSkin;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.util.Vector;

public class WormFragment extends ArmorStand {

    public static final int DEATH_TIMEOUT = 2;
    @Getter
    private final WormEntity parent;
    private final WormFragment previous;
    private final FoodFactory foodFactory;
    private final int color;

    private double scale = 1;
    private int ticksWithoutPrevious = 0;

    public WormFragment(Level level, Vec3 position, WormEntity parent, WormFragment previous, AbstractWormSkin skin,
                        int index, FoodFactory foodFactory) {
        super(level, position.x, position.y, position.z);

        this.parent = parent;
        this.previous = previous;
        this.foodFactory = foodFactory;
        this.color = index == 0 ? skin.getHeadColor() : skin.getBodyColor(index - 1);
        this.setInvisible(true);
        this.setNoGravity(true);
        this.setInvulnerable(true);
        this.setSilent(true);
        this.noPhysics = true;
        this.equipment.set(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(index == 0 ? skin.getHeadItem() : skin.getBodyItem(index - 1)));

        this.persist = false;
    }

    void fragmentTick() {
        super.tick();
        if (this.previous == null) {
            tickHead();
        } else {
            tickBody();
        }
        updateScale();
        spawnSpeedParticle();
    }

    boolean belongsTo(WormEntity worm) {
        return this.parent == worm;
    }

    @Override
    public void tick() {
        if (this.previous == null) {
            if (!this.parent.isAlive() && ++this.ticksWithoutPrevious >= DEATH_TIMEOUT) {
                Vec3 position = this.position();

                Particle.SMOKE.builder()
                        .count(20)
                        .location(this.level().getWorld(), position.x, position.y + 0.5, position.z)
                        .receivers(64, false)
                        .offset(0.3, 0.3, 0.3)
                        .spawn();

                spawnFoodDrops();
                this.discard();
                return;
            }
            return;
        }
        if (!this.previous.isAlive() && ++this.ticksWithoutPrevious >= DEATH_TIMEOUT) {
            Vec3 position = this.position();

            Particle.SMOKE.builder()
                    .count(20)
                    .location(this.level().getWorld(), position.x, position.y + 0.5, position.z)
                    .receivers(64, false)
                    .offset(0.3, 0.3, 0.3)
                    .spawn();

            spawnFoodDrops();
            this.discard();
            return;
        }
    }

    private void tickBody() {
        if (!this.previous.isAlive()) {
            return;
        }

        this.ticksWithoutPrevious = 0;
        double maxDistance = this.scale * 0.5;
        CraftEntity previousEntity = this.previous.getBukkitEntity();
        Location previousLocation = previousEntity.getLocation().clone()
                .add(0D, previousEntity.getEyeHeight(), 0D);
        CraftEntity fragmentEntity = this.getBukkitEntity();
        Location fragmentLocation = fragmentEntity.getLocation().clone()
                .add(0D, fragmentEntity.getEyeHeight(), 0D);
        Vector direction = previousLocation.toVector().subtract(fragmentLocation.toVector());

        if (direction.lengthSquared() <= maxDistance * maxDistance) {
            return;
        }

        direction.normalize();
        Vector position = previousLocation.toVector().subtract(direction.clone().multiply(maxDistance));
        Location location = fragmentLocation.clone();
        location.set(position.getX(), position.getY(), position.getZ());
        location.setYaw((float) Math.toDegrees(Math.atan2(-direction.getX(), direction.getZ())));
        location.setPitch((float) Math.toDegrees(Math.atan2(-direction.getY(),
                Math.hypot(direction.getX(), direction.getZ()))));

        this.teleportTo(location.getX(), location.getY() - this.getEyeHeight(), location.getZ());
        this.setRot(location.getYaw(), 0F);
    }

    private void tickHead() {
        if (!this.parent.isAlive()) {
            return;
        }

        this.ticksWithoutPrevious = 0;
        CraftEntity bukkitEntity = this.parent.getBukkitEntity();
        Location location = bukkitEntity.getLocation().clone().add(0, bukkitEntity.getEyeHeight(), 0);

        this.teleportTo(location.getX(), location.getY() - this.getEyeHeight(), location.getZ());
        this.setRot(location.getYaw(), location.getPitch());
    }

    private void updateScale() {
        if (!this.parent.isAlive()) {
            return;
        }
        double scale = this.parent.getEntityScale();

        if (scale == this.scale) {
            return;
        }
        this.scale = scale;

        AttributeInstance scaleAttribute = this.getAttribute(Attributes.SCALE);
        if (scaleAttribute != null) {
            scaleAttribute.setBaseValue(this.scale);
        }

    }

    private void spawnSpeedParticle() {
        if (!this.parent.isSpeeding()) {
            return;
        }

        Vec3 position = this.position();

        Particle.DUST.builder()
                .color(Color.fromRGB(this.color), 1F)
                .count(3)
                .location(this.level().getWorld(), position.x, position.y + this.getEyeHeight() + 0.2 * this.scale, position.z)
                .receivers(64, false)
                .offset(0.2 * this.scale, 0.2 * this.scale, 0.2 * this.scale)
                .spawn();
    }

    private void spawnFoodDrops() {
        int count = Math.min(this.parent.getPoints() / this.parent.getWormSize() / 2 / 3, 30);
        FoodColor[] colors = FoodColor.values();
        Vec3 position = this.position();

        for (int i = 0; i < count; i++) {
            double radius = Math.random() * this.scale * 1.5;
            double angle = Math.random() * Math.TAU;
            FoodColor color = colors[(int) (Math.random() * colors.length)];
            Location location = new Location(this.level().getWorld(),
                    position.x + radius * Math.cos(angle), 101, position.z + radius * Math.sin(angle));
            this.foodFactory.create(location, color, FoodReason.DROP);
        }
    }

}
