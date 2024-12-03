package com.kaboomroads.tehshadur.block.custom;

import com.kaboomroads.tehshadur.block.entity.ModBlockEntities;
import com.kaboomroads.tehshadur.block.entity.custom.DesolisCannonBlockEntity;
import com.kaboomroads.tehshadur.item.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DesolisCannonBlock extends BaseEntityBlock {
    public static final MapCodec<DesolisCannonBlock> CODEC = simpleCodec(DesolisCannonBlock::new);
    public static final IntegerProperty PART = IntegerProperty.create("part", 0, 1);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final VoxelShape BOTTOM_SHAPE = Shapes.or(Block.box(0, 0, 0, 16, 10, 16), Block.box(4, 10, 4, 12, 16, 12));
    public static final VoxelShape TOP_SHAPE = Block.box(4, 0, 4, 12, 16, 12);

    public DesolisCannonBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(PART, 0).setValue(FACING, Direction.NORTH));
    }

    @NotNull
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @NotNull
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(PART) == 1 ? TOP_SHAPE : BOTTOM_SHAPE;
    }

    @Override
    protected void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, @Nullable Orientation orientation, boolean bl) {
        if (!level.isClientSide && level.getBlockEntity(blockPos) instanceof DesolisCannonBlockEntity blockEntity) {
            Direction facing = blockState.getValue(FACING);
            Direction left = facing.getCounterClockWise();
            Direction right = facing.getClockWise();
            boolean newZero = level.getSignal(blockPos.relative(left), left) > 0;
            boolean newOne = level.getSignal(blockPos.relative(right), right) > 0;
            boolean newPowered = !newZero && !newOne && level.hasNeighborSignal(blockPos);
            if (blockEntity.zero != newZero) {
                if (newZero) {
                    blockEntity.zero = true;
                    blockEntity.targetPosition.add(false);
                } else blockEntity.zero = false;
            }
            if (blockEntity.one != newOne) {
                if (newOne) {
                    blockEntity.one = true;
                    blockEntity.targetPosition.add(true);
                } else blockEntity.one = false;
            }
            if (blockEntity.powered != newPowered) {
                if (newPowered) {
                    blockEntity.powered = true;
                    for (Player player : level.players()) {
                        BlockPos tp = BlockPos.of(blockEntity.targetPosition.bits);
                        player.displayClientMessage(Component.literal(tp.getX() + " " + tp.getY() + " " + tp.getZ() + " " + blockEntity.targetPosition), false);
                    }
                    blockEntity.fire();
                } else blockEntity.powered = false;
            }
        }
    }

    @NotNull
    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (!level.isClientSide && itemStack.is(ModItems.OBLITERATION_ROUND)) {
            BlockPos.MutableBlockPos cursor = blockPos.mutable();
            int part = blockState.getValue(PART);
            int min = PART.min;
            while (part > min) {
                cursor.move(Direction.DOWN);
                blockState = level.getBlockState(cursor);
                if (blockState.hasProperty(PART))
                    part = blockState.getValue(PART);
                else
                    return super.useItemOn(itemStack, blockState, level, blockPos, player, interactionHand, blockHitResult);
            }
            if (level.getBlockEntity(cursor) instanceof DesolisCannonBlockEntity blockEntity && !blockEntity.isLoaded()) {
                itemStack.shrink(1);
                blockEntity.load();
            }
        }
        return super.useItemOn(itemStack, blockState, level, blockPos, player, interactionHand, blockHitResult);
    }

    @NotNull
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return state.getValue(PART) == PART.min ? RenderShape.MODEL : RenderShape.INVISIBLE;
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
        return OmenMonolithBlock.checkPlacement(context, PART, defaultBlockState().setValue(FACING, context.getHorizontalDirection()));
    }

    @NotNull
    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @NotNull
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        OmenMonolithBlock.placeParts(level, pos, state, PART);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PART, FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(PART) == 0 ? new DesolisCannonBlockEntity(pos, state) : null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, ModBlockEntities.DESOLIS_CANNON, level.isClientSide ? DesolisCannonBlockEntity::clientTick : (Level level1, BlockPos blockPos, BlockState blockState, DesolisCannonBlockEntity blockEntity) -> DesolisCannonBlockEntity.serverTick(((ServerLevel) level1), blockPos, blockState, blockEntity));
    }
}
