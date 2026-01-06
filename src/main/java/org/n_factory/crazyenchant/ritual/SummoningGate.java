package org.n_factory.crazyenchant.ritual;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class SummoningGate extends Entity {
    private float health = 150.0F; //自前体力

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public SummoningGate(EntityType<? extends Entity> type, Level level) {
        super(type, level);
        this.addTag("summoning_gate");
        this.blocksBuilding = true; //衝突判定あり

        this.setBoundingBox(this.makeBoundingBox()); //初期ボックス設定
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.health = tag.getFloat("Health");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("Health", this.health);
    }

    //毎tick演出(紫粒子)
    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.PORTAL, this.getX(), this.getY() + 0.5, this.getZ(), 0, 0.05, 0);
        }
    }

    //殴られた時の処理
    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (this.isInvulnerable()) return false;

        setHealth(getHealth() - amount);
        if (getHealth() <= 0) {
            this.discard();
            this.level().explode(null, this.getX(), this.getY(), this.getZ(), 4.0F, Level.ExplosionInteraction.MOB);
        }
        return true;
    }

    //ボックス定義(サイズ2x2)
    @Override
    protected @NotNull AABB makeBoundingBox() {
        return new AABB(this.getX() - 1.0, this.getY(), this.getZ() - 1.0, this.getX() + 1.0, this.getY() +2.0, this.getZ() + 1.0);
    }
}
