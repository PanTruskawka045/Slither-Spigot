package me.pan_truskawka045.Slither.worm;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.util.Vector;

public class WormFragment extends ArmorStand {

    private final WormEntity parent;
    private final WormFragment previous;

    private double scale = 1;
    private int ticksWithoutPrevious = 0;

    public WormFragment(Level level, Vec3 position, WormEntity parent, WormFragment previous) {
        super(level, position.x, position.y, position.z);

        this.parent = parent;
        this.previous = previous;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.previous == null) {
            tickHead();
        } else {
            tickBody();
        }
        updateScale();
    }

    private void tickBody() {
        if (this.previous.isRemoved() && ++this.ticksWithoutPrevious >= 10) {
            //TODO add smoke particles
            //TODO add food drop spawn
            this.discard();
            return;
        }
        double maxDistance = this.scale * 0.25;
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
        fragmentEntity.setEyePosition(location);
    }

    private void tickHead() {

        CraftEntity bukkitEntity = this.parent.getBukkitEntity();
        Location location = bukkitEntity.getLocation().clone().add(0, bukkitEntity.getEyeHeight(), 0);

        this.getBukkitEntity().setEyePosition(location);
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

}
