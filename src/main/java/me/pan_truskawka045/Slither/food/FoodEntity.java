package me.pan_truskawka045.Slither.food;

import com.mojang.math.Transformation;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.entity.CraftArmorStand;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

public class FoodEntity extends ArmorStand {

    private static final float SIZE = 1.0F;
    private static final float HEAD_HEIGHT_RATIO = 0.30F;
    private static final float ARMOR_STAND_DOWN_RATIO = 0.65F;
    private static final float BLOCK_HEIGHT_RATIO = 0.35F;
    private final Display.BlockDisplay innerBlock;

    public FoodEntity(Level level, Vec3 position, FoodColor color) {
        super(level, position.x, position.y, position.z);

        this.setInvisible(true);
        this.setSmall(true);
        this.setNoGravity(true);
        this.setInvulnerable(true);
        this.setSilent(true);
        this.noPhysics = true;
        this.persist = false;
        this.onlyThisWorldSession = true;
        this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(color.getStainedGlass()));
        ((CraftArmorStand)this.getBukkitEntity()).addDisabledSlots(org.bukkit.inventory.EquipmentSlot.values());

        AttributeInstance scaleAttribute = this.getAttribute(Attributes.SCALE);
        if (scaleAttribute != null) {
            scaleAttribute.setBaseValue(SIZE);
        }

        float armorStandHeight = this.getBbHeight();
        float blockSize = armorStandHeight * BLOCK_HEIGHT_RATIO;
        this.setPos(position.x, position.y - armorStandHeight * ARMOR_STAND_DOWN_RATIO, position.z);

        this.innerBlock = new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, level);
        innerBlock.setBlockState(color.getInnerBlock().defaultBlockState());
        innerBlock.startRiding(this, true, true);
        this.positionRider(innerBlock);

        Vec3 passengerOffset = innerBlock.position().subtract(this.position());
        float blockCenterY = armorStandHeight * (1.0F - HEAD_HEIGHT_RATIO / 2.0F);
        innerBlock.setTransformation(new Transformation(
                new Vector3f(
                        (float) (-blockSize / 2.0F - passengerOffset.x),
                        (float) (blockCenterY - blockSize / 2.0F - passengerOffset.y),
                        (float) (-blockSize / 2.0F - passengerOffset.z)
                ),
                new Quaternionf(),
                new Vector3f(blockSize, blockSize, blockSize),
                new Quaternionf()
        ));
    }

    @Override
    public void remove(RemovalReason reason, EntityRemoveEvent.@Nullable Cause eventCause) {
        super.remove(reason, eventCause);
        innerBlock.remove(reason);
    }

}
