package me.pan_truskawka045.Slither.worm;

import lombok.Getter;
import lombok.Setter;
import me.pan_truskawka045.Slither.food.Food;
import me.pan_truskawka045.Slither.food.FoodFactory;
import me.pan_truskawka045.Slither.food.FoodStorage;
import me.pan_truskawka045.Slither.game.GameService;
import me.pan_truskawka045.Slither.skin.AbstractWormSkin;
import me.pan_truskawka045.Slither.user.SlitherUser;
import me.pan_truskawka045.Slither.util.Components;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

import java.util.Stack;

public class WormEntity extends Slime {

    private static final int ACTION_BAR_INTERVAL_TICKS = 10;
    private static final int BOOST_TICKS = 3;
    private static final int INITIAL_BODY_PARTS = 2;
    private static final int MAX_BODY_PARTS = 411;
    private static final double BODY_PART_GROWTH_EXPONENT = 2.25D;
    private static final double SCORE_MULTIPLIER = 15D;
    private static final double SCORE_OFFSET = 5D;
    private static final double MOVEMENT_SPEED = .4D;

    private final Stack<WormFragment> fragments = new Stack<>();

    private final FoodStorage foodStorage;
    private final FoodFactory foodFactory;
    private final AbstractWormSkin skin;
    private final GameService gameService;
    private final SlitherUser rider;

    @Getter
    @Setter
    private int points = 20;
    //    private int length = 20;
    private double angle = Math.random() * Math.TAU;
    private double scale = 1;
    private int ticksWithoutPassenger = 0;
    private int boostTicksRemaining = 0;


    public WormEntity(SlitherUser rider, Level level, FoodStorage foodStorage, FoodFactory foodFactory,
                      AbstractWormSkin skin, GameService gameService) {
        super(EntityType.SLIME, level);
        this.foodStorage = foodStorage;
        this.foodFactory = foodFactory;
        this.skin = skin;
        this.gameService = gameService;
        this.rider = rider;

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
            sendPointsActionBar(serverPlayer);
            this.ticksWithoutPassenger = 0;
        } else if (++this.ticksWithoutPassenger >= 10) {
            this.discard();
        }

        Vec3 forward = new Vec3(Math.cos(this.angle), 0, Math.sin(this.angle));
        AABB collisionBox = this.getBoundingBox().expandTowards(forward.scale(this.getBbWidth()));
        collectFood();
        level.getEntitiesOfClass(WormFragment.class, collisionBox,
                        wormFragment -> !wormFragment.belongsTo(this))
                .stream()
                .map(WormFragment::getParent)
                .findFirst()
                .ifPresent(killer -> {
                    String riderName = killer.getRiderName();
                    if (riderName == null) {
                        return;
                    }
                    Bukkit.broadcast(Components.elimination(this.getRiderName(), riderName));
                    Vec3 pos = this.position().add(0, 1, 0);
                    for (Entity passenger : this.getPassengers()) {
                        passenger.dismountTo(pos.x, pos.y, pos.z);
                    }
                    this.gameService.eliminatePlayer(this.rider);
                    this.discard();
                });
    }

    private void collectFood() {
        AABB foodCollectionBox = this.getBoundingBox().inflate(
                this.getBbWidth() / 1.3D,
                this.getBbHeight() / 2D,
                this.getBbWidth() / 1.3D
        );

        for (Food food : this.foodStorage.getAllInAABB(foodCollectionBox)) {
            this.foodStorage.removePoint(food);
            food.getEntity().discard();
            food.getLocation().getWorld().playSound(food.getLocation(), Sound.ENTITY_GENERIC_EAT, 1F, 1F);
            this.points += 3;
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
        if (input.jump() && this.boostTicksRemaining == 0 && this.points > 20) {
            this.points--;
            this.boostTicksRemaining = BOOST_TICKS;
        }
    }

    private String getRiderName() {
        if (!this.passengers.isEmpty() && this.passengers.getFirst() instanceof ServerPlayer serverPlayer) {
            return serverPlayer.getScoreboardName();
        }
        return null;
    }

    @Override
    public void tick() {
        this.move();
        super.tick();
        this.fragments.forEach(WormFragment::fragmentTick);

        if (this.horizontalCollision) {
            String riderName = this.getRiderName();
            if (riderName == null) {
                return;
            }
            Bukkit.broadcast(Components.wallElimination(riderName));
            Vec3 pos = this.position().add(0, 1, 0);
            for (Entity passenger : this.getPassengers()) {
                passenger.dismountTo(pos.x, pos.y, pos.z);
            }
            this.gameService.eliminatePlayer(this.rider);
            this.discard();
        }
    }

    public double getEntityScale() {
        return getEntityScale(this.points);
    }

    public boolean isSpeeding() {
        return this.boostTicksRemaining > 0;
    }

    public int getWormSize() {
        return this.fragments.size();
    }

    private void sendPointsActionBar(ServerPlayer serverPlayer) {
        if (this.tickCount % ACTION_BAR_INTERVAL_TICKS == 0) {
            serverPlayer.getBukkitEntity().sendActionBar(Components.wormPoints(this.points));
        }
    }

    private static double getEntityScale(int score) {
        return Math.min(6D, 1D + (getBodyPartCount(score) - INITIAL_BODY_PARTS) / 106D);
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
        double movementSpeed = MOVEMENT_SPEED;
        if (this.isSpeeding()) {
            movementSpeed *= 2D;
            this.boostTicksRemaining--;
        }

        double dX = Math.cos(angle) * movementSpeed;
        double dZ = Math.sin(angle) * movementSpeed;

        this.setDeltaMovement(dX, 0, dZ);
        this.setYRot((float) Math.toDegrees(angle) - 90F);
    }

    private int getWormLength() {
        return getWormLength(this.points);
    }

    private static int getWormLength(int score) {
        return getBodyPartCount(score) + 1;
    }

    private void updateSize() {
        int size = getWormLength();
        int currentSize = this.fragments.size();
        if (currentSize == size) {
            return;
        }
        if (currentSize > size) {
            int toRemove = currentSize - size;
            for (int i = 0; i < toRemove; i++) {
                WormFragment wormFragment = this.fragments.removeLast();
                wormFragment.remove(RemovalReason.DISCARDED);
            }
        }
        if (currentSize < size) {
            for (int i = currentSize; i < size; i++) {
                WormFragment peek = this.fragments.isEmpty() ? null : this.fragments.getLast();
                Vec3 position = peek == null ? this.position() : peek.position().subtract(0, peek.getEyeHeight(), 0);
                WormFragment wormFragment = new WormFragment(this.level(), position, this, peek, skin, i, foodFactory);
                this.fragments.add(wormFragment);
                this.level().addFreshEntity(wormFragment);
            }
        }
    }

    private static int getBodyPartCount(int score) {
        int bodyParts = INITIAL_BODY_PARTS;
        double volume = 0D;

        for (int part = 1; part <= MAX_BODY_PARTS; part++) {
            volume += 1D / getPartFullnessMultiplier(part - 1);
            if (part >= INITIAL_BODY_PARTS && getScore(volume) > score) {
                break;
            }
            bodyParts = part;
        }
        return bodyParts;
    }

    private static double getPartFullnessMultiplier(int bodyParts) {
        return Math.pow(1D - (double) bodyParts / MAX_BODY_PARTS, BODY_PART_GROWTH_EXPONENT);
    }

    private static int getScore(double volume) {
        return (int) Math.floor(SCORE_MULTIPLIER * (volume - 1D) - SCORE_OFFSET);
    }

    public void turnLeft() {
        this.angle -= Math.toRadians(7);
    }

    public void turnRight() {
        this.angle += Math.toRadians(7);
    }

}
