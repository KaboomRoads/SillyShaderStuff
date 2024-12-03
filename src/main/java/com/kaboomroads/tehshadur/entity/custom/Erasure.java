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
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ARGB;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
            ErasureExplosion erasureExplosion = new ErasureExplosion(level, this, null, new EntityBasedExplosionDamageCalculator(this) {
                @Override
                public float getKnockbackMultiplier(Entity entity) {
                    return 30.0F;
                }

                @Override
                public float getEntityDamageAmount(Explosion explosion, Entity entity, float f) {
                    return super.getEntityDamageAmount(explosion, entity, f) * 7.5F;
                }
            }, pos, 50, true, Explosion.BlockInteraction.DESTROY);
            erasureExplosion.explode();
            level.playSound(null, blockPosition(), ModSounds.ERASURE_EXPLODE, SoundSource.AMBIENT, 16.0F, 1.0F);
            for (ServerPlayer player : level.getPlayers(player -> player.distanceToSqr(pos) <= 256 * 256)) {
                ServerPlayNetworking.send(player, new FlashPayload(ARGB.colorFromFloat(0.5F, 0.0F, 1.0F, 1.0F), 1, 80, 20));
                ServerPlayNetworking.send(player, new FovPayload(0.75F, 5, 1, 40));
                ServerPlayNetworking.send(player, new ScreenShakePayload(2.0F, 0.1F, 5, 25, 60));
                level.sendParticles(player, ModParticles.ERASURE_RESIDUE, false, true, pos.x, pos.y, pos.z, 500, 0, 0, 0, 0.5);
            }
        }
    }

    @Override
    public float getBlockExplosionResistance(Explosion explosion, BlockGetter level, BlockPos pos, BlockState blockState, FluidState fluidState, float explosionPower) {
        return explosionPower * 0.01F;
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
                .add(Blocks.COAL_BLOCK.defaultBlockState(), 50)
                .add(Blocks.STONE.defaultBlockState(), 20)
                .add(Blocks.COBBLESTONE.defaultBlockState(), 20)
                .add(Blocks.BASALT.defaultBlockState(), 20)
                .add(Blocks.OBSIDIAN.defaultBlockState(), 20)
                .add(Blocks.MAGMA_BLOCK.defaultBlockState(), 10)
                .add(Blocks.TUFF.defaultBlockState(), 5)
                .add(Blocks.COBBLED_DEEPSLATE.defaultBlockState(), 5)
                .add(Blocks.DEEPSLATE.defaultBlockState(), 5)
                .add(Blocks.CRYING_OBSIDIAN.defaultBlockState(), 1)
                .add(Blocks.IRON_ORE.defaultBlockState(), 1)
                .add(Blocks.LAVA.defaultBlockState(), 1)
                .build();

        @NotNull
        public List<Hruska> calculateExplodedPositionsAndStates() {
            List<Hruska> list = new ArrayList<>();
            for (int j = 0; j < 32; j++)
                for (int k = 0; k < 32; k++)
                    for (int l = 0; l < 32; l++)
                        if (j == 0 || j == 31 || k == 0 || k == 31 || l == 0 || l == 31) {
                            double d = (float) j / 31.0F * 2.0F - 1.0F;
                            double e = (float) k / 31.0F * 2.0F - 1.0F;
                            double f = (float) l / 31.0F * 2.0F - 1.0F;
                            double g = Math.sqrt(d * d + e * e + f * f);
                            d /= g;
                            e /= g;
                            f /= g;
                            ServerLevel level = level();
                            float h = radius() * (0.7F + level.random.nextFloat() * 0.6F);
                            double m = center().x;
                            double n = center().y;
                            double o = center().z;
                            for (float p = 0.3F; h > 0.0F; h -= 0.225F) {
                                BlockPos blockPos = BlockPos.containing(m, n, o);
                                BlockState blockState = level.getBlockState(blockPos);
                                BlockPos.MutableBlockPos cursor = blockPos.mutable();
                                boolean b = false;
                                for (Direction dir : Direction.values()) {
                                    cursor.setWithOffset(blockPos, dir);
                                    BlockState relative = level.getBlockState(cursor);
                                    if (relative.canBeReplaced()) {
                                        b = true;
                                        break;
                                    }
                                }
                                FluidState fluidState = blockState.getFluidState();
                                if (!level.isInWorldBounds(blockPos)) break;
                                Optional<Float> optional = damageCalculator.getBlockExplosionResistance(this, level, blockPos, blockState, fluidState);
                                if (optional.isPresent()) h -= (optional.get() + p) * p;
                                if (b && (!blockState.canBeReplaced() || !blockState.isAir()) && h > 0.0F && damageCalculator.shouldBlockExplode(this, level, blockPos, blockState, h))
                                    list.add(new Hruska(blockPos, blockState));
                                m += d * p;
                                n += e * p;
                                o += f * p;
                            }
                        }
            return list;
        }

        @Override
        public void explode() {
            level().gameEvent(source, GameEvent.EXPLODE, center());
            List<Hruska> list = calculateExplodedPositionsAndStates();
            hurtEntities();
            if (interactsWithBlocks()) {
                ProfilerFiller profilerFiller = Profiler.get();
                profilerFiller.push("explosion_blocks");
                interactWithBlocks(list);
                profilerFiller.pop();
            }
        }

        private void interactWithBlocks(List<Hruska> list) {
            ServerLevel level = level();
            Util.shuffle(list, level.random);
            for (Hruska entry : list) {
                BlockPos pos = entry.pos;
                BlockState state = entry.state;
                if (!state.canBeReplaced()) {
                    Optional<BlockState> randomState = RANDOM_STATES.getRandomValue(level.random);
                    randomState.ifPresent(blockState -> level.setBlock(pos, blockState, 2));
                    if (level.random.nextInt(50) == 0) {
                        BlockPos above = pos.above();
                        BlockState aboveState = level.getBlockState(above);
                        if (aboveState.canBeReplaced())
                            level.setBlockAndUpdate(above, ModBlocks.ERASURE_FIRE.defaultBlockState());
                    }
                } else if (!state.isAir()) level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
            }
        }

        public record Hruska(BlockPos pos, BlockState state) {
        }
    }
}
