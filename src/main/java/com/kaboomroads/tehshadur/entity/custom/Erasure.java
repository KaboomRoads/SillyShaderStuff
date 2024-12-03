package com.kaboomroads.tehshadur.entity.custom;

import com.kaboomroads.tehshadur.block.ModBlocks;
import com.kaboomroads.tehshadur.entity.ModEntities;
import com.kaboomroads.tehshadur.networking.payload.FlashPayload;
import com.kaboomroads.tehshadur.networking.payload.FovPayload;
import com.kaboomroads.tehshadur.networking.payload.ScreenShakePayload;
import com.kaboomroads.tehshadur.particle.ModParticles;
import com.kaboomroads.tehshadur.sound.ModSounds;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ARGB;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class Erasure extends Entity {
    public static final EntityDataAccessor<Long> END = SynchedEntityData.defineId(Erasure.class, EntityDataSerializers.LONG);
    public final int maxTime = 200;
    public boolean kablooey = false;

    public Erasure(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public Erasure(Level level) {
        this(ModEntities.ERASURE, level);
    }

    @Override
    public void tick() {
        super.tick();
        long end = getEnd();
        long gameTime = level().getGameTime();
        if (!level().isClientSide && end == Long.MAX_VALUE) {
            end = gameTime + maxTime;
            setEnd(end);
        } else {
            long time = end - gameTime;
            if (time <= 0) discard();
            else if (time <= 100 && !kablooey) {
                kablooey = true;
                kablooey();
            }
        }
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    public void kablooey() {
        if (level() instanceof ServerLevel level) {
            Vec3 pos = position();
            ErasureExplosion erasureExplosion = new ErasureExplosion(level, this, null, new EntityBasedExplosionDamageCalculator(this), pos, 50, true, Explosion.BlockInteraction.DESTROY);
            erasureExplosion.explode();
            level.playSound(null, blockPosition(), ModSounds.ERASURE_EXPLODE, SoundSource.AMBIENT, 16.0F, 1.0F);
            for (ServerPlayer player : level.getPlayers(player -> player.distanceToSqr(pos) <= 256 * 256)) {
                ServerPlayNetworking.send(player, new FlashPayload(ARGB.colorFromFloat(0.5F, 0.0F, 1.0F, 1.0F), 1, 80, 20));
                ServerPlayNetworking.send(player, new FovPayload(0.75F, 5, 1, 40));
                ServerPlayNetworking.send(player, new ScreenShakePayload(2.0F, 0.1F, 5, 25, 60));
                level.sendParticles(player, ModParticles.ERASURE_RESIDUE, true, pos.x, pos.y, pos.z, 100, 0, 0, 0, 0.5);
            }
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(END, Long.MAX_VALUE);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putLong("end", getEnd());
        tag.putBoolean("kablooey", kablooey);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("end")) setEnd(tag.getLong("end"));
        kablooey = tag.getBoolean("kablooey");
    }

    public long getEnd() {
        return entityData.get(END);
    }

    public void setEnd(long end) {
        entityData.set(END, end);
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float f) {
        return false;
    }

    public static class ErasureExplosion extends ServerExplosion {
        public ErasureExplosion(ServerLevel serverLevel, @Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator explosionDamageCalculator, Vec3 vec3, float f, boolean bl, BlockInteraction blockInteraction) {
            super(serverLevel, entity, damageSource, explosionDamageCalculator, vec3, f, bl, blockInteraction);
        }

        public static final SimpleWeightedRandomList<BlockState> RANDOM_STATES = SimpleWeightedRandomList.<BlockState>builder()
                .add(Blocks.COAL_BLOCK.defaultBlockState(), 100)
                .add(Blocks.STONE.defaultBlockState(), 20)
                .add(Blocks.COBBLESTONE.defaultBlockState(), 20)
                .add(Blocks.MAGMA_BLOCK.defaultBlockState(), 10)
                .add(Blocks.OBSIDIAN.defaultBlockState(), 5)
                .add(Blocks.LAVA.defaultBlockState(), 1)
                .build();

        @Override
        public void interactWithBlocks(List<BlockPos> list) {
            Util.shuffle(list, level().random);
            for (BlockPos pos : list) {
                BlockState state = level().getBlockState(pos);
                if (!state.canBeReplaced()) {
                    Optional<BlockState> randomState = RANDOM_STATES.getRandomValue(level().random);
                    if (randomState.isPresent()) {
                        level().setBlock(pos, randomState.get(), 2);
                    }
                } else if (state.canBeReplaced() && !state.isAir()) {
                    level().setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                }
            }
        }

        @Override
        public void createFire(List<BlockPos> list) {
            Level level = level();
            for (BlockPos blockPos : list) {
                if (level.random.nextInt(5) == 0 && level.getBlockState(blockPos).isAir() && level.getBlockState(blockPos.below()).isSolidRender())
                    level.setBlockAndUpdate(blockPos, ModBlocks.ERASURE_FIRE.defaultBlockState());
            }
        }
    }
}
