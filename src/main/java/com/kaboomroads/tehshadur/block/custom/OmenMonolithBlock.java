package com.kaboomroads.tehshadur.block.custom;

import com.kaboomroads.tehshadur.block.entity.ModBlockEntities;
import com.kaboomroads.tehshadur.block.entity.custom.OmenMonolithBlockEntity;
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
import net.minecraft.world.level.block.Blocks;
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

public class OmenMonolithBlock extends BaseEntityBlock {
    public static final MapCodec<OmenMonolithBlock> CODEC = simpleCodec(OmenMonolithBlock::new);
    public static final IntegerProperty PART = IntegerProperty.create("part", 0, 3);

    public OmenMonolithBlock(Properties properties) {
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
        BlockState result = updateParts(blockState, direction, relState, this, PART);
        return result != null ? result : super.updateShape(blockState, levelReader, scheduledTickAccess, blockPos, direction, blockPos2, relState, randomSource);
    }

    public static @Nullable BlockState updateParts(BlockState blockState, Direction direction, BlockState relState, Block block, IntegerProperty property) {
        if (direction.getAxis() == Direction.Axis.Y) {
            int part = blockState.getValue(property);
            if ((part == 0 && direction == Direction.UP) || (part == property.max && direction == Direction.DOWN) || (part != 0 && part != property.max))
                return relState.is(block) ? blockState : Blocks.AIR.defaultBlockState();
        }
        return null;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return checkPlacement(context, this, PART);
    }

    @Nullable
    public static BlockState checkPlacement(BlockPlaceContext context, Block block, IntegerProperty property) {
        BlockPos blockPos = context.getClickedPos();
        Level level = context.getLevel();
        if (blockPos.getY() <= level.getMaxY() - property.max) {
            for (int i = 1; i <= property.max; i++)
                if (!level.getBlockState(blockPos.above(i)).canBeReplaced(context)) return null;
            return block.defaultBlockState();
        } else return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        placeParts(level, pos, state, PART);
    }

    public static void placeParts(Level level, BlockPos pos, BlockState state, IntegerProperty part) {
        for (int i = 1; i <= part.max; i++) level.setBlock(pos.above(i), state.setValue(part, i), UPDATE_ALL);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PART);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (level instanceof ServerLevel serverLevel && level.getBlockEntity(pos) instanceof OmenMonolithBlockEntity blockEntity) {
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
        return state.getValue(PART) == 0 ? new OmenMonolithBlockEntity(pos, state) : null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, ModBlockEntities.OMEN_MONOLITH, level.isClientSide ? OmenMonolithBlockEntity::clientTick : (Level level1, BlockPos blockPos, BlockState blockState, OmenMonolithBlockEntity blockEntity) -> OmenMonolithBlockEntity.serverTick(((ServerLevel) level1), blockPos, blockState, blockEntity));
    }
}
