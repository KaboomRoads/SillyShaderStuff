package com.kaboomroads.tehshadur.item.custom;

import com.google.common.collect.ImmutableList;
import com.kaboomroads.tehshadur.block.ModBlocks;
import com.kaboomroads.tehshadur.block.custom.DesolisAntennaBlock;
import com.kaboomroads.tehshadur.item.ModComponents;
import com.kaboomroads.tehshadur.util.BitQueue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DesolisRecieverItem extends Item {
    public DesolisRecieverItem(Properties properties) {
        super(properties.component(ModComponents.BLOCK_POSITIONS, List.of()));
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos.MutableBlockPos pos = context.getClickedPos().mutable();
        BlockState state = level.getBlockState(pos);
        if (!level.isClientSide && state.is(ModBlocks.DESOLIS_ANTENNA)) {
            ItemStack itemStack = context.getItemInHand();
            List<BlockPos> old = itemStack.get(ModComponents.BLOCK_POSITIONS);
            boolean sneaking = context.getPlayer().isShiftKeyDown();
            int part = state.getValue(DesolisAntennaBlock.PART);
            int max = DesolisAntennaBlock.PART.max;
            while (part < max) {
                pos.move(Direction.UP);
                state = level.getBlockState(pos);
                if (state.hasProperty(DesolisAntennaBlock.PART))
                    part = state.getValue(DesolisAntennaBlock.PART);
                else return InteractionResult.FAIL;
            }
            if (!sneaking && old.size() < 3 && !old.contains(pos)) {
                List<BlockPos> positions = ImmutableList.<BlockPos>builderWithExpectedSize(old.size() + 1).addAll(old).add(pos).build();
                itemStack.set(ModComponents.BLOCK_POSITIONS, positions);
                context.getPlayer().displayClientMessage(Component.translatable("item.tehshadur.desolis_reciever.connect"), true);
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(context);
    }

    @NotNull
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        if (!level.isClientSide) {
            ItemStack itemStack = player.getItemInHand(interactionHand);
            boolean sneaking = player.isShiftKeyDown();
            List<BlockPos> current = itemStack.get(ModComponents.BLOCK_POSITIONS);
            if (!sneaking) {
                for (BlockPos p : current) {
                    BlockPos.MutableBlockPos pos = p.mutable();
                    BlockState state = level.getBlockState(pos);
                    if (state.is(ModBlocks.DESOLIS_ANTENNA)) {
                        int part = state.getValue(DesolisAntennaBlock.PART);
                        int max = DesolisAntennaBlock.PART.max;
                        while (part < max) {
                            pos.move(Direction.UP);
                            state = level.getBlockState(pos);
                            if (state.hasProperty(DesolisAntennaBlock.PART))
                                part = state.getValue(DesolisAntennaBlock.PART);
                            else {
                                player.displayClientMessage(Component.translatable("item.tehshadur.desolis_reciever.signal_issue"), true);
                                return InteractionResult.FAIL;
                            }
                        }
                        BlockHitResult result = level.clip(new ClipContext(Vec3.atLowerCornerWithOffset(pos, 0.5, 1, 0.5), new Vec3(pos.getX() + 0.5, level.getMaxY(), pos.getZ() + 0.5), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));
                        if (result.getType() != HitResult.Type.MISS) {
                            player.displayClientMessage(Component.translatable("item.tehshadur.desolis_reciever.signal_issue"), true);
                            return InteractionResult.FAIL;
                        }
                    } else {
                        player.displayClientMessage(Component.translatable("item.tehshadur.desolis_reciever.signal_issue"), true);
                        return InteractionResult.FAIL;
                    }
                }
                if (current.size() >= 3) {
                    Vec3 first = Vec3.atLowerCornerWithOffset(current.get(0), 0.5, 1, 0.5);
                    Vec3 second = Vec3.atLowerCornerWithOffset(current.get(1), 0.5, 1, 0.5);
                    Vec3 third = Vec3.atLowerCornerWithOffset(current.get(2), 0.5, 1, 0.5);
                    int centerX = Mth.floor((first.x + second.x + third.x) / 3.0);
                    int centerY = Mth.floor((first.y + second.y + third.y) / 3.0);
                    int centerZ = Mth.floor((first.z + second.z + third.z) / 3.0);
                    long bits = new BlockPos(centerX, centerY, centerZ).asLong();
                    player.displayClientMessage(Component.literal(new BitQueue(bits).toCompactString()), true);
                    return InteractionResult.SUCCESS;
                }
            } else if (!current.isEmpty()) {
                itemStack.set(ModComponents.BLOCK_POSITIONS, List.of());
                player.displayClientMessage(Component.translatable("item.tehshadur.desolis_reciever.clear"), true);
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(level, player, interactionHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        List<BlockPos> positions = stack.get(ModComponents.BLOCK_POSITIONS);
        for (BlockPos pos : positions)
            tooltipComponents.add(Component.literal(pos.getX() + " " + pos.getY() + " " + pos.getZ()));
    }
}
