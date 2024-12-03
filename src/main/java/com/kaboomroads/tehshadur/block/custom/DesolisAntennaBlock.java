package com.kaboomroads.tehshadur.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DesolisAntennaBlock extends Block {
    public static final IntegerProperty PART = IntegerProperty.create("part", 0, 1);
    public static final VoxelShape BOTTOM_SHAPE = Block.box(6, 0, 6, 10, 16, 10);
    public static final VoxelShape TOP_SHAPE = Block.box(6.5, 0, 6.5, 9.5, 16, 9.5);

    public DesolisAntennaBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(PART, 0));
    }

    @NotNull
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(PART) == 1 ? TOP_SHAPE : BOTTOM_SHAPE;
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
}
