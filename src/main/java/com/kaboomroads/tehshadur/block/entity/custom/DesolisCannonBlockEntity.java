package com.kaboomroads.tehshadur.block.entity.custom;

import com.kaboomroads.tehshadur.block.entity.ModBlockEntities;
import com.kaboomroads.tehshadur.entity.custom.Erasure;
import com.kaboomroads.tehshadur.sound.ModSounds;
import com.kaboomroads.tehshadur.util.BitQueue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public class DesolisCannonBlockEntity extends BlockEntity implements AnimatedBlockEntity {
    public int tickCount = 0;
    public final AnimationState fireAnimationState = new AnimationState();
    public final BitQueue targetPosition = new BitQueue();
    public boolean powered;
    public boolean zero;
    public boolean one;
    public long kaboomTime = 0;
    public long beamTime = Long.MIN_VALUE;
    public final int maxBeamTime = 40;
    private boolean loaded = false;

    public DesolisCannonBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.DESOLIS_CANNON, pos, blockState);
    }

    public DesolisCannonBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putLong("target_position", targetPosition.bits);
        tag.putBoolean("powered", powered);
        tag.putBoolean("zero", zero);
        tag.putBoolean("one", one);
        tag.putLong("kaboom_time", kaboomTime);
        tag.putBoolean("loaded", loaded);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        targetPosition.bits = tag.getLong("target_position");
        powered = tag.getBoolean("powered");
        zero = tag.getBoolean("zero");
        one = tag.getBoolean("one");
        kaboomTime = tag.getLong("kaboom_time");
        loaded = tag.getBoolean("loaded");
    }

    @Override
    public boolean triggerEvent(int id, int data) {
        if (id == 0) {
            fireAnimationState.start(tickCount);
            beamTime = level.getGameTime() + maxBeamTime;
            return true;
        }
        return super.triggerEvent(id, data);
    }

    public void load() {
        loaded = true;
        level.playSound(null, getBlockPos(), ModSounds.DESOLIS_CANNON_LOAD, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void fire() {
        if (!level.isClientSide && loaded && kaboomTime <= 0) {
            BlockPos blockPos = getBlockPos();
            Vec3 ePos = Vec3.atBottomCenterOf(blockPos);
            BlockHitResult fireResult = level.clip(new ClipContext(Vec3.atLowerCornerWithOffset(blockPos, 0.5, 2, 0.5), new Vec3(ePos.x, level.getMaxY(), ePos.z), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));

            if (fireResult.getType() == HitResult.Type.MISS) {
                loaded = false;
                kaboomTime = 100;
                level.blockEvent(blockPos, getBlockState().getBlock(), 0, 0);


                level.playSound(null, blockPos, ModSounds.DESOLIS_CANNON_FIRE, SoundSource.BLOCKS, 8.0F, 1.0F);

                BlockPos target = BlockPos.of(targetPosition.bits);
                Vec3 pos = Vec3.atBottomCenterOf(target);
                BlockHitResult result = level.clip(new ClipContext(new Vec3(pos.x, level.getMaxY(), pos.z), new Vec3(pos.x, level.getMinY(), pos.z), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));
                pos = result.getLocation();
                if (pos.y - Mth.floor(pos.y) >= 0.75) pos = new Vec3(pos.x, Mth.floor(pos.y) + 1, pos.z);
                Erasure erasure = new Erasure(level);
                erasure.setPos(pos);
                level.addFreshEntity(erasure);
            }
        }
    }

    public static void serverTick(ServerLevel level, BlockPos blockPos, BlockState blockState, DesolisCannonBlockEntity blockEntity) {
        blockEntity.tickCount++;
        if (blockEntity.kaboomTime > 0) blockEntity.kaboomTime--;
        if (blockEntity.kaboomTime > 0 && blockEntity.kaboomTime <= 80) {
            Vec3 pos = Vec3.atBottomCenterOf(blockPos);
            level.sendParticles(ParticleTypes.SMOKE, pos.x, pos.y + 1.75, pos.z, 1, 0, 0, 0, 0);
        }
    }

    public static void clientTick(Level level, BlockPos blockPos, BlockState blockState, DesolisCannonBlockEntity blockEntity) {
        blockEntity.tickCount++;
    }

    @Override
    public int tickCount() {
        return tickCount;
    }
}
