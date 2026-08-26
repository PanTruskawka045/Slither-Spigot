package me.pan_truskawka045.Slither.start.skin;

import com.mojang.datafixers.util.Pair;
import lombok.RequiredArgsConstructor;
import me.pan_truskawka045.Slither.skin.AbstractWormSkin;
import me.pan_truskawka045.Slither.skin.WormSkinType;
import me.pan_truskawka045.Slither.user.SlitherUser;
import me.pan_truskawka045.Slither.util.SpigotUtil;
import net.minecraft.core.Rotations;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor
public class SkinPreview {

    private static final double SEGMENT_SPACING = 0.0625D;
    private static final double WAVE_AMPLITUDE = 0.15D;
    private static final double WAVE_SPATIAL_FREQUENCY = 3.2D;
    private static final double WAVE_TICK_FREQUENCY = 0.15D;
    private static final double PACKET_POSITION_SCALE = 4096.0D;

    private final List<ArmorStand> preview = new ArrayList<>();
    private final List<Short> verticalOffsets = new ArrayList<>();

    private final SlitherUser user;
    private int ticks = 0;


    public void tick() {
        ticks++;

        Iterator<ArmorStand> iterator = preview.iterator();

        List<Packet<?>> packets = new ArrayList<>();


        for (int i = 0; iterator.hasNext(); i++) {
            ArmorStand next = iterator.next();

            double currentOffset = getWaveOffset(next.getX());
            double previousOffset = getWaveOffset(next.getX() - SEGMENT_SPACING);
            short targetOffset = (short) Math.round(currentOffset * PACKET_POSITION_SCALE);
            short deltaY = (short) (targetOffset - verticalOffsets.get(i));
            verticalOffsets.set(i, targetOffset);

            float pitch = (float) Math.toDegrees(Math.atan2(currentOffset - previousOffset, SEGMENT_SPACING));
            packets.add(new ClientboundMoveEntityPacket.PosRot(
                    next.getId(),
                    (short) 0,
                    deltaY,
                    (short) 0,
                    (byte) (next.getYRot() * 256.0F / 360.0F),
                    (byte) (pitch * 256.0F / 360.0F),
                    false
            ));
            next.setHeadPose(headPoseFor(pitch));
            List<SynchedEntityData.DataValue<?>> data = next.getEntityData().packDirty();
            if (data != null) {
                packets.add(new ClientboundSetEntityDataPacket(next.getId(), data));
            }

        }

        SpigotUtil.sendPackets(user, packets);
    }

    private double getWaveOffset(double x) {
        double sinAngle = ticks * WAVE_TICK_FREQUENCY + -x * WAVE_SPATIAL_FREQUENCY;
        return Math.sin(sinAngle) * WAVE_AMPLITUDE;
    }

    private Rotations headPoseFor(float pitch) {
        return new Rotations(pitch, 0.0F, 0.0F);
    }

    public void applySkin(WormSkinType wormSkinType) {
        AbstractWormSkin abstractWormSkin = wormSkinType.create();
        Iterator<ArmorStand> iterator = preview.iterator();

        ArmorStand head = iterator.next();

        List<ClientboundSetEquipmentPacket> packets = new ArrayList<>();

        packets.add(new ClientboundSetEquipmentPacket(head.getId(), List.of(new Pair<>(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(abstractWormSkin.getHeadItem())))));

        for (int i = 0; iterator.hasNext(); i++) {
            ArmorStand next = iterator.next();

            ItemStack bodyItem = abstractWormSkin.getBodyItem(i);
            packets.add(new ClientboundSetEquipmentPacket(next.getId(), List.of(new Pair<>(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(bodyItem)))));

        }

        SpigotUtil.sendPackets(user, packets);
    }

    public void spawn() {
        for (double x = 126.0 - 12 * SEGMENT_SPACING; x <= 126.0 + 12 * SEGMENT_SPACING; x += SEGMENT_SPACING) {
            ArmorStand stand = new ArmorStand(SpigotUtil.getServerLevel(user.getPlayer().getWorld()), x, 116.5, 257.1);
            stand.setNoGravity(true);
            preview.add(stand);
            verticalOffsets.add((short) 0);
            AttributeInstance attribute = stand.getAttribute(Attributes.SCALE);
            if (attribute != null) {
                attribute.setBaseValue(0.25D);
            }
            stand.setInvisible(true);

            stand.setYRot(90);

            ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(
                    stand.getId(),
                    stand.getUUID(),
                    stand.getX(),
                    stand.getY(),
                    stand.getZ(),
                    stand.getXRot(),
                    stand.getYRot(),
                    EntityType.ARMOR_STAND,
                    0,
                    Vec3.ZERO,
                    stand.getYHeadRot()
            );

            SpigotUtil.sendPacket(user, packet);

            List<SynchedEntityData.DataValue<?>> data = stand.getEntityData().getNonDefaultValues();

            if (data != null) {
                ClientboundSetEntityDataPacket dataPacket = new ClientboundSetEntityDataPacket(stand.getId(), data);
                SpigotUtil.sendPacket(user, dataPacket);
            }

            ClientboundUpdateAttributesPacket attributesPacket = new ClientboundUpdateAttributesPacket(stand.getId(), stand.getAttributes().getSyncableAttributes());

            SpigotUtil.sendPacket(user, attributesPacket);

        }


    }

    public void remove() {
        int[] ids = preview.stream().mapToInt(Entity::getId).toArray();
        SpigotUtil.sendPacket(user, new ClientboundRemoveEntitiesPacket(ids));
    }


}
