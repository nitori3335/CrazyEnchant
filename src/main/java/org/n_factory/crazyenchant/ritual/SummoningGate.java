package org.n_factory.crazyenchant.ritual;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.n_factory.crazyenchant.Crazyenchant;

/**
 * SummoningGate の改良版（同期・スポーンパケット・攻撃処理を追加）
 */
public class SummoningGate extends Entity {
    private static final EntityDataAccessor<Float> DATA_HP =
            SynchedEntityData.defineId(SummoningGate.class, EntityDataSerializers.FLOAT);
    private static final float MAX_HEALTH = 150.0F;

    public SummoningGate(EntityType<? extends SummoningGate> type, Level level) {
        super(type, level);
        this.addTag("summoning_gate");
        this.blocksBuilding = true; // 衝突判定あり
        // bounding box の初期化（位置が更新されたときは必要に応じて更新する）
        this.setBoundingBox(this.makeBoundingBox());
    }

    @Override
    protected void defineSynchedData() {
        // HP を同期する
        this.entityData.define(DATA_HP, MAX_HEALTH);
    }

    public float getHealth() {
        return this.entityData.get(DATA_HP);
    }

    public void setHealth(float hp) {
        this.entityData.set(DATA_HP, Math.max(0f, Math.min(hp, MAX_HEALTH)));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("Health")) {
            this.setHealth(tag.getFloat("Health"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("Health", this.getHealth());
    }

    // 毎tick演出（クライアント側でパーティクル）
    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.PORTAL, this.getX(), this.getY() + 0.5, this.getZ(), 0, 0.05, 0);
        }
        // 必要なら位置が変わったときに bounding box を更新する
    }

    // サーバー→クライアントでスポーンパケットを送るために NetworkHooks を使う
    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    // レイキャスト / 攻撃判定で拾えるように
    @Override
    public boolean isPickable() {
        return true;
    }

    // ダメージ処理はサーバー側のみで行う（クライアント側では false）
    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (this.level().isClientSide) return false; // サーバーでのみ処理する
        if (this.isRemoved()) return false;
        if (this.isInvulnerableTo(source)) return false;

        // デバッグ（サーバーのログに出るはず）
        if (this.level() instanceof ServerLevel) {
            Crazyenchant.LOGGER.info("SummoningGate hurt: amount={} source={}", amount, source.getMsgId());
        }

        float newHp = this.getHealth() - amount;
        this.setHealth(newHp);

        if (newHp <= 0f) {
            this.level().playSound(null, this.blockPosition(), SoundEvents.GENERIC_EXPLODE, this.getSoundSource(), 1.0F, 1.0F);
            this.discard();

            // 地形破壊なし爆発（エンティティダメージ・ノックバック・パーティクルあり）
            this.level().explode(null, this.getX(), this.getY(), this.getZ(), 4.0F, false, Level.ExplosionInteraction.NONE);

            // 儀式側の状態更新があればここで呼ぶ
        } else {
            this.level().playSound(null, this.blockPosition(), SoundEvents.STONE_HIT, this.getSoundSource(), 0.8F, 1.2F);
        }
        return true;
    }

    // 当たり判定箱（サイズ2x2）はそのまま維持
    @Override
    protected @NotNull AABB makeBoundingBox() {
        return new AABB(this.getX() - 1.0, this.getY(), this.getZ() - 1.0, this.getX() + 1.0, this.getY() + 2.0, this.getZ() + 1.0);
    }
}