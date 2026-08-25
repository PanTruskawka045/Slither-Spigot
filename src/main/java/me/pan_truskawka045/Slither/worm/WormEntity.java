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

public class WormEntity extends Slime {

    private static final int MIN_POINTS = 20;

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

}
