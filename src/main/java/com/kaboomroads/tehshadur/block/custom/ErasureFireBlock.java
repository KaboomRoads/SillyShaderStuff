package com.kaboomroads.tehshadur.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ErasureFireBlock extends BaseFireBlock {
    public static final MapCodec<ErasureFireBlock> CODEC = simpleCodec(ErasureFireBlock::new);

    @NotNull
    @Override
    public MapCodec<ErasureFireBlock> codec() {
        return CODEC;
    }

    public ErasureFireBlock(BlockBehaviour.Properties properties) {
        super(properties, 3.0F);
    }

    @NotNull
    @Override
    protected BlockState updateShape(
            BlockState blockState,
            LevelReader levelReader,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos blockPos,
            Direction direction,
            BlockPos blockPos2,
            BlockState blockState2,
            RandomSource randomSource
    ) {
        return canSurvive(blockState, levelReader, blockPos) ? defaultBlockState() : Blocks.AIR.defaultBlockState();
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
    }

    @Override
    protected boolean canBurn(BlockState state) {
        return true;
    }
}