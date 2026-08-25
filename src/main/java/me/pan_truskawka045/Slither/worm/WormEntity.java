package me.pan_truskawka045.Slither.worm;

import me.pan_truskawka045.Slither.food.FoodStorage;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Stack;

public class WormEntity extends Slime {

    private static final int MIN_POINTS = 20;
    private static final double MOVEMENT_SPEED = .4D;

    private final Stack<WormFragment> fragments = new Stack<>();

    private final FoodStorage foodStorage;

    private int points = 20;
    //    private int length = 20;
    private double angle = Math.random() * Math.TAU;

    private double scale = 1;

    public WormEntity(Level level, FoodStorage foodStorage) {
        super(EntityType.SLIME, level);
        this.foodStorage = foodStorage;

        setNoGravity(true);
        this.noPhysics = true;
        setDeltaMovement(Vec3.ZERO);
        this.moveControl = new MoveControl(this);

    }


    @Override
    protected void registerGoals() {

    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        return false;
    }

    @Override
    public void jumpFromGround() {

    }

    @Override
    protected void dropEquipment(ServerLevel level) {
    }

    @Override
    protected void dropFromLootTable(ServerLevel level, DamageSource damageSource, boolean playerKill) {
    }

    @Override
    public boolean shouldDropExperience() {
        return false;
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        this.updateScale();
        this.updateSize();
        this.move();
    }

    public double getEntityScale() {
        int score = Math.max(this.points, MIN_POINTS);
        return Math.floor((Math.log(score) - 3) * 10D) / 10D;
    }

    private void updateScale() {
        double scale = getEntityScale();

        if (scale == this.scale) {
            return;
        }
        this.scale = scale;

        AttributeInstance scaleAttribute = this.getAttribute(Attributes.SCALE);
        if (scaleAttribute != null) {
            scaleAttribute.setBaseValue(this.scale);
        }

    }

    private void move() {
        double dX = Math.cos(angle) * MOVEMENT_SPEED;
        double dZ = Math.sin(angle) * MOVEMENT_SPEED;

        double x = this.getX() + dX;
        double y = this.getY();
        double z = this.getZ() + dZ;

        this.setDeltaMovement(Vec3.ZERO);
        this.setPos(x, y, z);
        this.setYRot((float) Math.toDegrees(angle) - 90F);
    }

    private int getWormLength() {
        return Math.floorDiv(this.points, 100) + 3;
    }

    private void updateSize() {
        int size = getWormLength();
        int currentSize = this.fragments.size();
        if (currentSize == size) {
            return;
        }
        if (currentSize > size) {
            for (int i = currentSize; i >= size; i--) {
                WormFragment wormFragment = this.fragments.removeFirst();
                wormFragment.remove(RemovalReason.DISCARDED);
            }
        }
        if (currentSize < size) {
            for (int i = currentSize; i < size; i++) {
                WormFragment peek = this.fragments.isEmpty() ? null : this.fragments.getFirst();
                Vec3 position = peek == null ? this.position() : peek.position();
                WormFragment wormFragment = new WormFragment(this.level(), position, this, peek);
                this.fragments.add(wormFragment);
                this.level().addFreshEntity(wormFragment);
            }
        }
    }

}
