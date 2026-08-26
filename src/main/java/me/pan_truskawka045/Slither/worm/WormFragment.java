package me.pan_truskawka045.Slither.worm;

import me.pan_truskawka045.Slither.skin.AbstractWormSkin;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.util.Vector;

public class WormFragment extends ArmorStand {

    private final WormEntity parent;
    private final WormFragment previous;

    private double scale = 1;
    private int ticksWithoutPrevious = 0;

    public WormFragment(Level level, Vec3 position, WormEntity parent, WormFragment previous, AbstractWormSkin skin, int index) {
        super(level, position.x, position.y, position.z);

        this.parent = parent;
        this.previous = previous;
        this.setInvisible(true);
        this.setNoGravity(true);
        this.setInvulnerable(true);
        this.setSilent(true);
        this.noPhysics = true;
        this.equipment.set(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(index == 0 ? skin.getHeadItem() : skin.getBodyItem(index - 1)));

        this.persist = false;
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
        if (this.parent.isRemoved()) {
            if (++this.ticksWithoutPrevious >= 10) {
                this.discard();
            }
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

}
