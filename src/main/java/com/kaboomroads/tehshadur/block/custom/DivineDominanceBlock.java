package com.kaboomroads.tehshadur.block.custom;

import com.kaboomroads.tehshadur.block.entity.ModBlockEntities;
import com.kaboomroads.tehshadur.block.entity.custom.DivineDominanceBlockEntity;
import com.kaboomroads.tehshadur.border.SimpleBorderProvider;
import com.kaboomroads.tehshadur.mixinducks.EntityDuck;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DivineDominanceBlock extends BaseEntityBlock {
    public static final MapCodec<DivineDominanceBlock> CODEC = simpleCodec(DivineDominanceBlock::new);
    public static final IntegerProperty PART = IntegerProperty.create("part", 0, 2);

    public DivineDominanceBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(PART, 0));
    }

    @NotNull
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @NotNull
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @NotNull
    @Override
    protected BlockState updateShape(BlockState blockState, LevelReader levelReader, ScheduledTickAccess scheduledTickAccess, BlockPos blockPos, Direction direction, BlockPos blockPos2, BlockState relState, RandomSource randomSource) {
        BlockState result = OmenMonolithBlock.updateParts(blockState, direction, relState, this, PART);
        return result != null ? result : super.updateShape(blockState, levelReader, scheduledTickAccess, blockPos, direction, blockPos2, relState, randomSource);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return OmenMonolithBlock.checkPlacement(context, PART, defaultBlockState());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        OmenMonolithBlock.placeParts(level, pos, state, PART);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PART);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (level instanceof ServerLevel serverLevel && level.getBlockEntity(pos) instanceof DivineDominanceBlockEntity blockEntity) {
            for (UUID uuid : blockEntity.entityUUIDs) {
                Entity entity = serverLevel.getEntity(uuid);
                if (entity != null) {
                    EntityDuck duck = ((EntityDuck) entity);
                    if (blockEntity.borderProvider.equals(duck.tehshadur$getBorderProvider()))
                        duck.tehshadur$setBorderProvider(new SimpleBorderProvider(null));
                }
            }
            blockEntity.entityUUIDs.clear();
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(PART) == 0 ? new DivineDominanceBlockEntity(pos, state) : null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, ModBlockEntities.DIVINE_DOMINANCE, level.isClientSide ? DivineDominanceBlockEntity::clientTick : (Level level1, BlockPos blockPos, BlockState blockState, DivineDominanceBlockEntity blockEntity) -> DivineDominanceBlockEntity.serverTick(((ServerLevel) level1), blockPos, blockState, blockEntity));
    }
}
