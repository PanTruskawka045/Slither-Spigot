package me.pan_truskawka045.Slither.worm;

import me.pan_truskawka045.Slither.food.FoodStorage;
import me.pan_truskawka045.Slither.skin.AbstractWormSkin;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Stack;

public class WormEntity extends Slime {

    private static final int MIN_POINTS = 20;
    private static final double MOVEMENT_SPEED = .4D;

    private final Stack<WormFragment> fragments = new Stack<>();

    private final FoodStorage foodStorage;
    private final AbstractWormSkin skin;

    private int points = 20;
    //    private int length = 20;
    private double angle = Math.random() * Math.TAU;
    private double scale = 1;
    private int ticksWithoutPassenger = 0;


    public WormEntity(Level level, FoodStorage foodStorage, AbstractWormSkin skin) {
        super(EntityType.SLIME, level);
        this.foodStorage = foodStorage;
        this.skin = skin;

        setNoGravity(true);
        setDeltaMovement(Vec3.ZERO);
        this.moveControl = new MoveControl(this);
        this.setPersistenceRequired(false);
        this.persist = false;

        addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, MobEffectInstance.INFINITE_DURATION,
                0, false, false, false));
        setDeltaMovement(Vec3.ZERO);
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
        if (!this.passengers.isEmpty() && this.passengers.getFirst() instanceof ServerPlayer serverPlayer) {
            tickPassenger(serverPlayer);
            this.ticksWithoutPassenger = 0;
        } else if (++this.ticksWithoutPassenger >= 10) {
            this.discard();
        }
    }

    private void tickPassenger(ServerPlayer serverPlayer) {
        Input input = serverPlayer.getLastClientInput();
        if (input.left() && !input.right()) {
            turnLeft();
        }
        if (input.right() && !input.left()) {
            turnRight();
        }
    }

    @Override
    public void tick() {
        this.move();
        super.tick();

        if (this.horizontalCollision) {
            //TODO death
        }
    }

    public double getEntityScale() {
        int score = Math.max(this.points, MIN_POINTS);
        return Math.floor((log(2, score) - 3) * 10D) / 10D;
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

        this.setDeltaMovement(dX, 0, dZ);
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
                WormFragment wormFragment = this.fragments.removeLast();
                wormFragment.remove(RemovalReason.DISCARDED);
            }
        }
        if (currentSize < size) {
            for (int i = currentSize; i < size; i++) {
                WormFragment peek = this.fragments.isEmpty() ? null : this.fragments.getLast();
                Vec3 position = peek == null ? this.position() : peek.position();
                WormFragment wormFragment = new WormFragment(this.level(), position, this, peek, skin, i);
                this.fragments.add(wormFragment);
                this.level().addFreshEntity(wormFragment);
            }
        }
    }

    private double log(double base, double value) {
        return Math.log(value) / Math.log(base);
    }

    public void turnLeft() {
        this.angle -= Math.toRadians(7);
    }

    public void turnRight() {
        this.angle += Math.toRadians(7);
    }

}
