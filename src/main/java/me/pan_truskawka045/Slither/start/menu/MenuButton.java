package me.pan_truskawka045.Slither.start.menu;

import lombok.AccessLevel;
import lombok.Getter;
import me.pan_truskawka045.Slither.user.SlitherUser;
import me.pan_truskawka045.Slither.util.SpigotUtil;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import java.util.List;

public class MenuButton {

    private final SlitherUser user;
    private final Location location;

    @Getter(AccessLevel.PROTECTED)
    private final Display.TextDisplay display;
    private final double width, height;

    private final double zAxis = 257.05;

    private boolean lastHovered = false;
    private Runnable onHover = () -> {
    };
    private Runnable onClick = () -> {
    };
    private Runnable onRelease = () -> {
    };


    public MenuButton(SlitherUser user, Location location, TextDisplayFactory displayFactory, double width, double height) {
        this.user = user;
        this.location = location;
        this.display = displayFactory.createDisplay();
        this.width = width;
        this.height = height;
    }

    public void onHover(Runnable onHover) {
        this.onHover = onHover;
    }

    public void onClick(Runnable onClick) {
        this.onClick = onClick;
    }

    public void onRelease(Runnable onRelease) {
        this.onRelease = onRelease;
    }

    public void spawn() {
        this.display.setPos(this.location.getX(), this.location.getY(), this.zAxis);

        ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(
                this.display.getId(),
                this.display.getUUID(),
                this.display.getX(),
                this.display.getY(),
                this.display.getZ(),
                this.display.getXRot(),
                this.display.getYRot(),
                EntityType.TEXT_DISPLAY,
                0,
                Vec3.ZERO,
                this.display.getYHeadRot()
        );

        SpigotUtil.sendPacket(this.user.getPlayer(), packet);

        List<SynchedEntityData.DataValue<?>> nonDefaultValues = this.display.getEntityData().getNonDefaultValues();

        if (nonDefaultValues != null) {
            ClientboundSetEntityDataPacket data = new ClientboundSetEntityDataPacket(this.display.getId(), nonDefaultValues);
            SpigotUtil.sendPacket(this.user.getPlayer(), data);
        }


    }

    public void setText(Component component) {
        this.display.setText(io.papermc.paper.adventure.PaperAdventure.asVanilla(component));


        List<SynchedEntityData.DataValue<?>> nonDefaultValues = this.display.getEntityData().getNonDefaultValues();

        if (nonDefaultValues != null) {
            ClientboundSetEntityDataPacket data = new ClientboundSetEntityDataPacket(this.display.getId(), nonDefaultValues);
            SpigotUtil.sendPacket(this.user.getPlayer(), data);
        }
    }

    public void tick() {
        Location location = this.user.getPlayer().getEyeLocation();
        Vector direction = location.getDirection();
        if (direction.getZ() > 0) {
            setHovered(false);
            return;
        }
        if (location.getZ() < this.zAxis) {
            setHovered(false);
            return;
        }

        double zDiff = this.zAxis - location.getZ();
        direction.multiply(zDiff / direction.getZ());

        Vector add = location.toVector().add(direction);

        boolean hover = Math.abs(this.location.getX() - add.getX()) < this.width / 2 && (add.getY() >= this.location.y() && add.getY() <= this.location.y() + this.height);

        setHovered(hover);
    }

    private void setHovered(boolean hover) {
        if (this.lastHovered == hover) {
            return;
        }
        this.lastHovered = hover;
        if (hover) {
            this.onHover.run();
        } else {
            this.onRelease.run();
        }
    }

    protected void click() {
        if(this.lastHovered) {
            this.onClick.run();
        }
    }

}
